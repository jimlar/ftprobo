//
// RoboFTPClient, this is my own ftp handler, based on sun's
// (c) Jimmy Larsson 1998
//

import sun.net.ftp.*;
import sun.net.*;
import java.io.*;

public class RoboFTPClient extends FtpClient
{
  public RoboFTPClient (String host, String user, String pass) throws IOException
  {
    super (host, FtpClient.FTP_PORT);
    login (user, pass);
  }

  public void mkdir (String dir) throws IOException
  {
    int status = issueCommand ("MKD " + dir);

    if (status >= 300)
    {
      FTPRobo.log.addMess ("error creating FTP directory \"" + dir + "\" on: " + toString());
      throw new IOException ("error creating remote FTP directory");
    }
  }

  public static void main (String[] args) throws IOException
  {
    RoboFTPClient ftp = new RoboFTPClient ("localhost", "anonymous", "plopp");
    System.out.println (ftp.welcomeMsg);
    ftp.cd("/");
    TelnetInputStream ti = ftp.list();
    byte[] b = new byte[1024];

    ti.read (b, 0 ,1024);
    System.out.println (b);
  }
}
