//
// RoboLog, this is the logfile handler
// (c) Jimmy Larsson 1998
//

import java.io.*;
import java.util.*;
import java.text.*;

public class RoboLog
{
  public static final String SESSION_LOG_FILENAME = "logs" + File.separator + "session.log";
  public static final String ROBO_LOG = "logs" + File.separator + "total.log";

  protected File logFile;
  protected PrintWriter pw;

  public RoboLog (File logFile)
  {
    pw = null;
    File logDir = new File (logFile.getParent());
    logDir.mkdirs();

    if (logFile.exists())
      logFile.delete();

    this.logFile = logFile;
    try {
    pw = new PrintWriter (new FileOutputStream (logFile));
    initLog ();
    } catch (IOException e1)
    {
      System.err.println ("!! Error creating logfile");
    }
  }

  public synchronized void initLog ()
  {
    pw.println ("");
    addMess ("------ Log restarted");
  }

  public synchronized void closeLog ()
  {
    if (pw != null)
    {
      addMess ("------ Log closed");
      pw.close ();
      
      // Append this sessions log to the BIG log
      try {
	File bigLogFile = new File (ROBO_LOG);
	RandomAccessFile bigLog = new RandomAccessFile (bigLogFile, "rw");
	
	bigLog.seek (bigLog.length());
	BufferedReader in = new BufferedReader (new FileReader (logFile));
	String line;
      
	line = in.readLine ();
	while (line != null)
	{
	  bigLog.writeUTF (line + "\n");
	  line = in.readLine();
	}
	
	bigLog.close();
	in.close();
	pw = null;
      } catch (IOException e1)
      {
	System.out.println ("error opening system log!!");
      }
      pw = null;
    }
  }

  public synchronized void addMess (String mess, String user)
  {
    String logEntry = new String();
    Calendar cal = Calendar.getInstance (new SimpleTimeZone (9,"se"));
    Date now = cal.getTime();
    DateFormat df = DateFormat.getDateTimeInstance();
    df.setCalendar (cal);

    logEntry = logEntry.concat (df.format(now));

    if (user != null)
    {
      logEntry = logEntry.concat (":" + user + ": " + mess);
    } else
    {      
      logEntry = logEntry.concat (": " + mess);      
    }
    
    // DEBUG OUTPUT
    //System.out.println (logEntry);

    pw.println (logEntry);
    pw.flush();
  }

  public void addMess  (String mess)
  {
    addMess (mess, null);
  }
}

