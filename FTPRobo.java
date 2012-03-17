//
// This is the main class
// (c) Jimmy Larsson 1998
//

import java.util.*;
import java.io.*;

public class FTPRobo
{
  public static final int DEFAULT_PORT = 6789;
  public static final double VERSION = 0.03;
  public static final int ROBOS = 10;              // Number of ftp threads
  public static final int MAX_RETRIES = 2000;
  public static String stateString = "not started";
  public static boolean running = false;
  public static int port;
  public static FTPRobo instance;
  public static RoboLog log;
  public static long startLastModified;
  public static RoboServerConnections connections;

  protected static RoboConfig config;
  protected static RoboStatus status;
  protected static RoboDB database;
  protected Vector threads;
  protected RoboTCPUI ui;
  protected boolean fullUpdate;

  public FTPRobo ()
  {
    config = new RoboConfig ();
    status = new RoboStatus ();
    database = new RoboDB ();
    connections = new RoboServerConnections ();
    instance = this;
    ui = new RoboTCPUI(config.getPort());
    threads = null;
  }

  public static FTPRobo getInstance ()
  {
    return instance;
  }

  public static RoboDB getDB ()
  {
    return database;
  }
  
  public static RoboStatus getStatus ()
  {
    return status;
  }

  public static RoboConfig getConfig ()
  {
    return config;
  }

  // This is run when all threads are done
  public void roboReady ()
  {
    // Put a message in the log
    if (fullUpdate)
    {
      log.addMess ("full update finished, " +  status.getTotalUploads() + " files queued, " + status.getFailedUploads() + " failed.");
    } else
    {
      log.addMess ("partial update finished, " + status.getTotalUploads() + " files queued, " + status.getFailedUploads() + " failed.");
    }

    // Mark valid if we've done a full update, and all files successed
    if (fullUpdate && (status.getFailedUploads() == 0))
      config.setLastTotalUpdate (config.getLastModifiedNow());

    // Clear cache
    clearCache (config.getCachePath());

    //Reset thread class
    RoboThread.resetThreads();

    // set state markers
    stateString = "not started";
    running = false;
    
    // Close log
    log.closeLog();

    // Send mail with report
    RoboMailer mail = new RoboMailer ();
    String mailMessage = new String ();
    String line;

    try {
      BufferedReader log = new BufferedReader (new FileReader (new File (RoboLog.SESSION_LOG_FILENAME)));
      line = log.readLine ();
    
      while (line != null)
      {
	mailMessage = mailMessage.concat (line + "\n");
	line = log.readLine();
      }
    } catch (IOException e1)
    {
      mailMessage = "Unable to read logfile!";
    }

    mail.sendMail (mailMessage, config.getReportTo());
  }

  protected void clearCache (String path)
  {
    String[] files;
    File curFile;
    File dir = new File (path);
    files = dir.list();
    
    for (int i = 0; i < files.length; i++)
    {  
      curFile = new File (dir.getAbsolutePath(), files[i]);
  
      if (curFile.isDirectory())
      {
	clearCache (curFile.getAbsolutePath());
      } else
      {
	curFile.delete();
      }
    }
  }

  public void roboStartFromPath (String path)
  {
    log = new RoboLog (new File (RoboLog.SESSION_LOG_FILENAME));
    log.addMess ("started partial update from " + path);
    fullUpdate = false;
    startLastModified = config.getLastModifiedNow ();
    database.clear ();
    status.clear();
    database.addFilesFromPath (path, config);
    
    threads = new Vector ();
    
    for (int i = 1;i < ROBOS; i++)
    {
      threads.addElement(new RoboThread ());
    }
    
    status.stampStartTime();
    running = true;
    stateString = "running";
  }

  public void roboStart ()
  {
    log = new RoboLog (new File (RoboLog.SESSION_LOG_FILENAME));
    log.addMess ("started FULL update");
    fullUpdate = true;
    startLastModified = config.getLastModifiedNow ();
    database.clear ();
    status.clear();
    database.addFiles (config);

    threads = new Vector ();

    for (int i = 1;i < ROBOS; i++)
    {
      threads.addElement(new RoboThread ());
    }

    status.stampStartTime();
    running = true;
    stateString = "running";
  }

  public void roboStop ()
  {
    if (threads != null)
    {
      for (Enumeration e = threads.elements(); e.hasMoreElements();)
      {
	((RoboThread) e.nextElement ()).signalStop();
      }

      stateString = "stopping nicely";
    } else
    {
      stateString = "stopped";
      running = false;
    }
  }

  public void roboHalt ()
  {
    if (threads != null)
    {
      for (Enumeration e = threads.elements(); e.hasMoreElements();)
      {
	((RoboThread) e.nextElement ()).stop();
      }

    }
    
    stateString = "stopped";
    running = false;
    threads = null;      
  }

  public void roboQuit ()
  {
    roboHalt ();
    log.closeLog();
    System.exit (0);
  }

  public static void main (String[] args)
  {
    FTPRobo robo = new FTPRobo ();

  }    
}
