//
// RoboConfig, this is where all configs are
// (c) Jimmy Larsson 1998
//

import java.util.*;
import java.io.*;

public class RoboConfig
{
  public static final String CONFIG_FILENAME = "FTPRobo.cnf";
  public static final String TMP_FILENAME = "FTPRobo.tmp";

  //Time between retries in milliseconds (You can specify more times if you want, it's dynamic)
  //Currently 0secs, 3secs, 10secs, 5min, 20min
  public static final long[] RETRY_TIMES = {0, 3000, 10000, 300000, 1200000};

  protected int port;
  protected File localPath;
  protected File cachePath;
  protected File failedPath;
  protected Vector ftpServers;
  protected Vector ftpServerPaths;
  protected Vector ftpUsers;
  protected Vector ftpPasses;
  protected Vector ftpConnections;
  protected long lastTotalUpdate;
  protected String reportTo;
  protected String smtpHost;
  protected String roboUserName;
  protected String roboPassword;


  public RoboConfig ()
  {
    loadConfig ();
  }

  public String getSmtpHost ()
  {
    return smtpHost;
  }

  public void setSmtpHost (String host)
  {
    smtpHost = host;
    saveConfig();
  }

  public String getReportTo ()
  {
    return reportTo;
  }

  public void setReportTo (String to)
  {
    reportTo = to;
    saveConfig ();
  }

  public synchronized void addFtpServer (String server, String path, String user, String pass, String connections)
  {
    ftpServers.addElement (server);
    ftpServerPaths.addElement (path);
    ftpUsers.addElement (user);
    ftpPasses.addElement (pass);
    ftpConnections.addElement (connections);
    saveConfig ();
    FTPRobo.connections.update();
  }

  public synchronized void delFtpServer (int serverNo)
  {
      serverNo--;

      if ((serverNo < ftpServers.size()) && (serverNo >= 0))
      {
	  ftpServers.removeElementAt (serverNo);
	  ftpServerPaths.removeElementAt (serverNo);
	  ftpUsers.removeElementAt (serverNo);
	  ftpPasses.removeElementAt (serverNo);
	  ftpConnections.removeElementAt (serverNo);
	  saveConfig ();
      }
  }	  
	      

  public synchronized void setLocalPaths (String path, String cachePath, String failedPath)
  {
    localPath = new File (path);
    this.cachePath = new File (cachePath);
    this.failedPath = new File (failedPath);
    saveConfig ();
  }

  public synchronized void setPort (int port)
  {
    this.port = port;
    saveConfig();
  }

  public long getLastModifiedNow ()
  {
    long result = 0;

    try {
      File tmpFile = new File (TMP_FILENAME);
      FileOutputStream tmpStream = new FileOutputStream (tmpFile);
    
      tmpStream.write (45);
      tmpStream.flush ();
      tmpStream.close ();

      result = tmpFile.lastModified ();
      tmpFile.delete ();
    } catch (IOException e1)
    {
      return 0;
    }
   
    return result;
  }
  
  public long getLastTotalUpdate ()
  {
    return lastTotalUpdate;
  }

  public void setLastTotalUpdate (long lastModified)
  {
    lastTotalUpdate = lastModified;
    saveConfig ();
  }

  public void setUserName (String name)
  {
    roboUserName = name;
    saveConfig();
  }

  public void setPassword (String password)
  {
    roboPassword = password;
    saveConfig();
  }

  public int getPort ()
  {
    return port;
  }

  public String getLocalPath ()
  {
    if (localPath != null)
      return localPath.getAbsolutePath();
    else
      return null;
  }

  public String getCachePath ()
  {
    if (cachePath != null)
      return cachePath.getAbsolutePath();
    else
      return null;
  }  

  public String getFailedPath ()
  {
    if (failedPath != null)
      return failedPath.getAbsolutePath();
    else
      return null;
  }

  public Vector getFtpServers ()
  {
    return ftpServers;
  }

  public Vector getFtpServerPaths ()
  {
    return ftpServerPaths;
  }

  public Vector getFtpUsers ()
  {
    return ftpUsers;
  }

  public Vector getFtpPasses ()
  {
    return ftpPasses;
  }

  public Vector getFtpConnections ()
  {
    return ftpConnections;
  }

  public String getUserName ()
  {
    return roboUserName;
  }

  public String getPassword ()
  {
    return roboPassword;
  }

  protected void loadConfig ()
  {
    try {
    FileInputStream istream = new FileInputStream(CONFIG_FILENAME);
    ObjectInputStream oi = new ObjectInputStream(istream);

    port = oi.readInt();
    localPath = (File) oi.readObject();
    cachePath = (File) oi.readObject();
    failedPath = (File) oi.readObject();
    lastTotalUpdate = oi.readLong();
    smtpHost = (String) oi.readObject();
    reportTo = (String) oi.readObject();
    ftpServers = (Vector) oi.readObject();
    ftpServerPaths = (Vector) oi.readObject();
    ftpUsers = (Vector) oi.readObject();
    ftpPasses = (Vector) oi.readObject();
    ftpConnections = (Vector) oi.readObject();
    roboUserName = (String) oi.readObject();
    roboPassword = (String) oi.readObject();

    istream.close();
    } catch (FileNotFoundException e1)
    {
      setDefaultConfig();
    } catch (StreamCorruptedException e2)
    {
      setDefaultConfig();
    } catch (ClassNotFoundException e3)
    {
      setDefaultConfig();
    } catch (OptionalDataException e4)
    {
      setDefaultConfig();
    } catch (IOException e5)
    {
      setDefaultConfig();
    }
  }


  protected void saveConfig ()
  {
    try {
    FileOutputStream ostream = new FileOutputStream(CONFIG_FILENAME);
    ObjectOutputStream oo = new ObjectOutputStream(ostream);

    oo.writeInt (port);
    oo.writeObject (localPath);
    oo.writeObject (cachePath);
    oo.writeObject (failedPath);
    oo.writeLong (lastTotalUpdate);
    oo.writeObject (smtpHost);
    oo.writeObject (reportTo);
    oo.writeObject (ftpServers);
    oo.writeObject (ftpServerPaths);
    oo.writeObject (ftpUsers);
    oo.writeObject (ftpPasses);
    oo.writeObject (ftpConnections);
    oo.writeObject (roboUserName);
    oo.writeObject (roboPassword);

    ostream.close();
    } catch (InvalidClassException e1) {}
    catch (NotSerializableException e2) {}
    catch (IOException e3) {}

  }

  protected void setDefaultConfig ()
  {
    localPath = null;
    cachePath = null;
    failedPath = null;
    lastTotalUpdate = getLastModifiedNow ();
    smtpHost = null;
    reportTo = null;
    ftpServers = new Vector ();
    ftpServerPaths = new Vector ();
    ftpUsers = new Vector ();
    ftpPasses = new Vector ();
    ftpConnections = new Vector ();
    roboUserName = "user";
    roboPassword = "secret";
  }
}
