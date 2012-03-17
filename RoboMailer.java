//
// RoboMailer, this class is used to report status via mail after a complete update.
// (c) Jimmy Larsson 1998
//

import sun.net.smtp.*;
import java.io.*;

public class RoboMailer
{
  protected SmtpClient smtp = null;

  public RoboMailer ()
  {
    try {
      smtp = new SmtpClient (FTPRobo.getConfig().getSmtpHost());
    } catch (IOException e1)
    {
      FTPRobo.log.addMess ("unable to create mailer!! (bad SMTP host?)");
      smtp = null;
    }
  }

  public void sendMail (String message, String to)
  {

    if (smtp != null)
    {
      try {

	smtp.from ("ftprobo@moonfire.se");
	smtp.to (to);
	
	PrintStream mess = smtp.startMessage();
	
	//mess.println ("mail from: FTPRobo <ftprobo@moonfire.se>");
	//mess.println ("rcpt to: " + to);
	//mess.println ("data");
	//mess.println ("Subject: FTPRobo session report!");
	//mess.println ("To: " + to);

	mess.println ("Hi, this is FTPRobo with the status report from the last total update.");
	mess.println ("Here comes the logfile:");
	mess.println (message);
	mess.println ("----------------------------------");
	mess.println ("That's all for now!");
	//mess.println (".");
	smtp.closeServer ();
      } catch (IOException e1)
      {
	System.out.println ("RoboMailer: unable to sent mail-report!! " + e1);
      } catch (NullPointerException e1)
      {
	System.out.println ("RoboMailer: unable to sent mail-report!! " + e1);
      }
      
    } else
    {
      System.out.println ("RoboMailer: unable to sent mail-report, bad SMTP host!");
    }
  }
}
