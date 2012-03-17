//
// UIConnection, this is the class that communicates with the user via TCP
// (c) Jimmy Larsson 1998
//

import java.net.*;
import java.io.*;
import java.util.*;

public class UIConnection extends Thread
{
  protected Socket socket;
  protected BufferedReader in;
  protected PrintWriter out;
  protected UICommands cmd;

  public UIConnection (Socket sock)
  {
    socket = sock;
    try {
      in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
      out = new PrintWriter (socket.getOutputStream());
    } catch (IOException e1)
    {
      try {
	socket.close();
      } catch (IOException e2) {};

      System.out.println ("UIConnection: error getting streams!");
      return;
    }

    cmd = new UICommands (in ,out);
    this.start();
  }

  public void disconnect ()
  {
    try {
      socket.close();
    } catch (IOException e1) {}

    this.stop();
  }

  // The command interpreting stuff, crap by the way.. =)
  public void run ()
  {
    String command;

    try {
      if (login ())
      {
	System.out.println ("User logged in");
	cmd.loginOk();
	
	while (true)
	{
	  out.flush ();
	  command = in.readLine();
	  interpret (command);
	}
      }
    } catch (IOException e1) {}
    finally {try {socket.close();} catch (IOException e2) {}}

    disconnect ();
  }

  
  //The login sequence
  protected boolean login ()
  {
    String usr;
    String pass;
    int retry = 0;

    try {
      while (retry < 3)
      {
	cmd.printWelcome ();
	out.print ("login: ");
	out.flush();
	usr = in.readLine();
	out.print ("password: ");
	out.flush();
	pass = in.readLine();

	if (usr.equalsIgnoreCase(FTPRobo.getConfig().getUserName()) && pass.equalsIgnoreCase(FTPRobo.getConfig().getPassword()))
	  return true;
	retry++;
      }

      out.println ("Sorry too many retries, disconnecting...");
    } catch (IOException e1) {}
    return false;
  }

  // Command interpreting
  protected void interpret (String command) throws IOException
  {
    if (command.equalsIgnoreCase ("start"))
    {
       cmd.start ();

    } else if (command.toLowerCase().startsWith ("startfrompath"))
    {
      Vector args = splitArgs (command.substring("startfrompath".length() + 1));
      cmd.startFromPath (args);

    } else if (command.equalsIgnoreCase ("showpaths"))
    {
       cmd.showPaths ();

    } else if (command.equalsIgnoreCase ("showreportto"))
    {
       cmd.showReportTo ();

    } else if (command.equalsIgnoreCase ("showsmtphost"))
    {
       cmd.showSmtpHost ();

    } else if (command.equalsIgnoreCase ("shutdown"))
    {
      cmd.shutdown ();

    } else if (command.equalsIgnoreCase ("quit"))
    {
       cmd.quit (this);

    } else if (command.equalsIgnoreCase ("stop"))
    {
       cmd.stop ();

    } else if (command.equalsIgnoreCase ("status"))
    {
       cmd.showStatus ();

    } else if (command.equalsIgnoreCase ("markvalid"))
    {
      cmd.markValid ();

    } else if (command.toLowerCase().startsWith ("setsmtphost"))
    {
      Vector args = splitArgs (command.substring("setsmtphost".length() + 1));
      cmd.setSmtpHost (args);

    } else if (command.toLowerCase().startsWith ("setreportto"))
    {
      Vector args = splitArgs (command.substring("setreportto".length() + 1));
      cmd.setReportTo (args);

    } else if (command.toLowerCase().startsWith ("addsrv"))
    {
      Vector args = splitArgs (command.substring("addsrv".length() + 1));
      cmd.addServer (args);

    } else if (command.toLowerCase().startsWith ("delsrv"))
    {
      Vector args = splitArgs (command.substring("delsrv".length() + 1));
      cmd.delServer (args);

    } else if (command.toLowerCase().startsWith ("chgpass"))
    {
      Vector args = splitArgs (command.substring("chgpass".length() + 1));
      cmd.changePass (args);

    } else if (command.toLowerCase().startsWith ("chguser"))
    {
      Vector args = splitArgs (command.substring("chguser".length() + 1));
      cmd.changeUser (args);

    } else if (command.toLowerCase().startsWith ("setpaths"))
    {
      Vector args = splitArgs (command.substring("setpaths".length() + 1));
      cmd.setPaths (args);

    } else if (command.toLowerCase().startsWith ("setport"))
    {

      int port = Integer.parseInt (command.substring("setport".length() + 1));
      cmd.setPort (port);

    } else if (command.equalsIgnoreCase ("listsrv"))
    {
       cmd.listServers ();

    } else if (command.equalsIgnoreCase ("showlog"))
    {
       cmd.showSessionLog ();

    } else 
    {
      cmd.printHelp ();
    }
  }

  protected Vector splitArgs (String args)
  {
    Vector result = new Vector ();
    
    int start = 0;
    int stop = 1;
    
    stop = args.indexOf(" ");
    if (stop < 0)
      stop = args.length();
    
    result.addElement (args.substring(start, stop));
    start = args.indexOf(" ", stop);

    while (start != -1)
    {
      stop = args.indexOf(" ", start + 1);
      if (stop < 0)
	stop = args.length();

      result.addElement (args.substring (start + 1, stop));

      start = args.indexOf(" ", stop);
    }
	
    return result;
  }
}
