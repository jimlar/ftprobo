//
// UICommands, this is where all UI actions are taken
// they are just separated to keep code tidy
// (c) Jimmy Larsson
//

import java.io.*;
import java.util.*;

public class UICommands 
{
  protected BufferedReader in;
  protected PrintWriter out;

  public UICommands (BufferedReader in, PrintWriter out)
  {
    this.out = out;
    this.in = in;
  }
    
  public void start ()
  {
    FTPRobo.getInstance().roboStart();
    out.println ("+OK\r");
  }

  public void startFromPath (Vector args)
  {
    Enumeration e = args.elements();
    String path = (String) e.nextElement();

    if (path.startsWith ("/"))
    {
      out.println ("-Error: path must be relative, action aborted!");
      return;
    }
    
    File testFile = new File (FTPRobo.getConfig().getLocalPath() + File.separator + path);
    if (!testFile.exists())
    {
      out.println ("-Error: path does not seem to exist, aborted!");
      return;
    }

    out.println ("+OK\r");
    FTPRobo.getInstance().roboStartFromPath (path);
  }

  public void stop ()
  {
    FTPRobo.getInstance().roboStop();
    out.println ("+OK\r");
  }

  public void quit (UIConnection con)
  {
    con.disconnect();
  }

  public void shutdown ()
  {
    FTPRobo.getInstance().roboQuit();
  }

  public void showSessionLog ()
  {
    String sessionLog = new String();

    try {
      BufferedReader log = new BufferedReader (new FileReader (new File (RoboLog.SESSION_LOG_FILENAME)));
      String line = log.readLine ();
      
      while (line != null)
      {
	sessionLog = sessionLog.concat (line + "\r\n");
	line = log.readLine();
      }
    } catch (IOException e1)
    {
      sessionLog = "Unable to read logfile!\r";
    }
   
    out.println (sessionLog);
    out.println ("\r\n+OK\r");
  }


  public void showPaths ()
  {
    if (FTPRobo.getConfig().getLocalPath() != null)
    {
      out.println ("+LOCAL " + FTPRobo.getConfig().getLocalPath() + "\r");
      out.println ("+CACHE " + FTPRobo.getConfig().getCachePath() + "\r");
      out.println ("+FAILED " + FTPRobo.getConfig().getFailedPath() + "\r");
    } else
    {
      out.println ("-ERR: Local paths not set!\r");
    }
  }

  public void showStatus ()
  {
    if (FTPRobo.running)
    {
      RoboStatus stat = FTPRobo.getStatus();
      
      out.println("+ " + stat.getSuccessedUploads() + " " + stat.getTotalUploads() + " " + stat.getFailedUploads() + " " + stat.getRetries() + "\r");
    } else
    {
      out.println("-Not running.\r");
    }
  }

  public void addServer (Vector args)
  {
    Enumeration e = args.elements();
    
    String server = (String) e.nextElement();
    String path = (String) e.nextElement();
    String user = (String) e.nextElement();
    String pass = (String) e.nextElement();
    String conns = (String) e.nextElement();
    
    if (path.endsWith (File.separator))
      path = path.substring (0, path.length() - 1);

    FTPRobo.getConfig().addFtpServer(server, path, user, pass, conns);
    out.println ("+OK\r");
  }

  public void delServer (Vector args)
  {
    int serverNo = Integer.parseInt ((String) args.firstElement());
    FTPRobo.getConfig().delFtpServer (serverNo);
    out.println ("+OK\r");
  }

  public void markValid ()
  {
    FTPRobo.getConfig().setLastTotalUpdate (FTPRobo.getConfig().getLastModifiedNow());
    out.println ("+OK\r");
  }

  public void setSmtpHost (Vector args)
  {
    Enumeration e = args.elements();
    String host = (String) e.nextElement();
    FTPRobo.getConfig().setSmtpHost(host);
    out.println("+OK\r");
  }

  public void showSmtpHost ()
  {
    out.println ("+SMTP " + FTPRobo.getConfig().getSmtpHost() + "\r");
  }

  public void setReportTo (Vector args)
  {
    Enumeration e = args.elements();
    String to = (String) e.nextElement();
    FTPRobo.getConfig().setReportTo(to);
    out.println("+OK\r");
  }

  public void showReportTo ()
  {
    out.println ("+REPORTTO " + FTPRobo.getConfig().getReportTo() + "\r");
  }

  public void setPaths (Vector args)
  {
    Enumeration e = args.elements();
    
    String path = (String) e.nextElement();
    String cache = (String) e.nextElement();
    String failed = (String) e.nextElement();

    // Strip trailing slashes
    if (path.endsWith (File.separator))
      path = path.substring (0, path.length() - 1);

    
    if (cache.endsWith (File.separator))
      cache = cache.substring (0, cache.length() - 1);

    if (failed.endsWith (File.separator))
      failed = failed.substring (0, failed.length() - 1);

    FTPRobo.getConfig().setLocalPaths (path, cache, failed);
    out.println ("+OK\r");
  }

  public void setPort (int port)
  {
    FTPRobo.getConfig().setPort (port);
    out.println ("+OK\r");  
  }

  public void listServers ()
  {
    Enumeration servers = FTPRobo.getConfig().getFtpServers().elements();
    Enumeration paths = FTPRobo.getConfig().getFtpServerPaths().elements();
    Enumeration users = FTPRobo.getConfig().getFtpUsers().elements();
    Enumeration passes = FTPRobo.getConfig().getFtpPasses().elements();
    Enumeration conns = FTPRobo.getConfig().getFtpConnections().elements();
    int i = 1;
     
    while (servers.hasMoreElements())
    {
      String server = (String) servers.nextElement();
      String path = (String) paths.nextElement();
      String user = (String) users.nextElement();
      String realPass = (String) passes.nextElement();
      String conn = (String) conns.nextElement();
 
      out.println ("+ " + i + " " + server + " " + path + " " + user + " " + realPass + " " +conn +"\r");
      i++;
    }
    out.println ("+OK\r");
  }


  // Welcome message
  public void printWelcome () throws IOException
  {
    out.println ("\r\nFTPRobo version " + FTPRobo.VERSION + ", (c) Jimmy Larsson 1998\r");
  }

  // Welcome message, after login OK
  public void loginOk () throws IOException
  {
    out.println ("+OK\r");
  }

  public void changePass (Vector args) throws IOException
  {
    Enumeration e = args.elements();
    String newPass = (String) e.nextElement();
    
    FTPRobo.getConfig().setPassword (newPass);
    out.println ("+OK\r");
  }

  public void changeUser (Vector args) throws IOException
  {
    Enumeration e = args.elements();
    String newUsr = (String) e.nextElement();

    FTPRobo.getConfig().setUserName (newUsr);
    out.println ("+OK\r");
  }

  // help message
  public void printHelp () throws IOException
  {
    out.println ("-Commands:\r");
    out.println ("-shutdown    - shutdown FTPRobo completely\r");
    out.println ("-quit        - exit (dissconnects from FTPRobo)\r");
    out.println ("-stop        - Stops FTPRobo nicely\r");
    out.println ("-start       - Start sending prepared files\r");
    out.println ("-startfrompath <relative path> - Starts sending from path down\r");
    out.println ("-status      - View status and progress\r");
    out.println ("-showpaths   - Show local paths\r");
    out.println ("-setpaths <path> <cache path> <failed files path> - Set local paths\r");
    out.println ("-listsrv     - shows ftpservers\r");
    out.println ("-addsrv <host> <basedir> <username> <password> <connections> - Adds an FTP-server\r");
    out.println ("-delsrv <server#> - removes server with given number\r");
    out.println ("-setport <port>   - Sets port for this UI\r");
    out.println ("-markvalid - marks current file tree valid\r");
    out.println ("-showsmtphost - view current SMTP host\r");
    out.println ("-setsmtphost - sets the SMTP host\r");
    out.println ("-showreportto - shows e-mail address to report to\r");
    out.println ("-setreportto - sets the e-mail address to report to \r");
    out.println ("-chguser <username> - chage username\r");
    out.println ("-chgpass <password> - change password\r");
    out.println ("-showlog - show log for last/this session\r");
  }

  public void printPrompt ()
  {
    out.print("[" + FTPRobo.stateString + "]: ");
    out.flush();
  }
}
