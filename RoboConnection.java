//
// RoboConnection, this is where we connect to the FTP server and upload the file
// (c) Jimmy Larsson 1998
//

import sun.net.ftp.*;
import sun.net.*;
import java.io.*;
import java.util.*;

public class RoboConnection
{
  protected static final int BLOCK_SIZE = 1024;
  protected RoboFTPClient ftp;
  protected DBEntry entry; 

  public RoboConnection (DBEntry entry) throws IOException
  {
    this.entry = entry;
    ftp = new RoboFTPClient (entry.getHost(), entry.getUser(), entry.getPass());
    ftp.binary ();
  }

  public void upload () throws IOException, FileNotFoundException
  {
    String directory = entry.getServerRoot();

    System.out.println ("Making dirs on ftp-server...");
    changeAndMakeDirs (directory);

    System.out.println ("Getting file out-stream...file: " + entry.getFile().getName());
    TelnetOutputStream outStream = ftp.put (entry.getFile().getName());

    System.out.println ("Getting file in-stream...");
    File inFile = entry.getFile();
    FileInputStream inStream = new FileInputStream (inFile);

    byte[] buffer = new byte[BLOCK_SIZE];

    int totalBytes = 0;

    System.out.println ("Sending file...");
    while (inStream.available() > 0)
    {
      int bytesRead = inStream.read(buffer);
      totalBytes += bytesRead;

      if (bytesRead == -1)
	break;
      outStream.write(buffer, 0, bytesRead);
      System.out.println (bytesRead + " bytes read!");
    }

    System.out.println ("File sent...");
    outStream.flush();
    outStream.close();
    inStream.close();
    
    ftp.closeServer();
  }

  protected void changeAndMakeDirs (String dir) throws IOException
  {
    Vector pathElements = splitPath(dir);
    Enumeration e = pathElements.elements();

    ftp.cd("/");

    while (e.hasMoreElements())
    {
      String dirPart = (String) e.nextElement();

      try {
	ftp.cd(dirPart);
      } catch (IOException e1)
      {
	// DEBUG OUTPUT
	//System.out.println (e1);
	ftp.mkdir(dirPart);
	ftp.cd(dirPart);
      }
    }
  }
  
  protected Vector splitPath (String path)
  {
    Vector result = new Vector ();

    int start;
    int stop;
    
    if (!path.startsWith("/"))
    {
      stop = path.indexOf("/", 0);
      if (stop < 0)
	stop = path.length();
      start = 0;
    } else
    {
      start = 1;
      stop = path.indexOf("/", start);
      if (stop <= 0)
	stop = path.length();
    }
    
    result.addElement (path.substring(start, stop));
    start = path.indexOf("/", stop);

    while (start != -1)
    {
      stop = path.indexOf("/", start + 1);
      if (stop < 0)
	stop = path.length();

      result.addElement (path.substring (start + 1, stop));
      start = path.indexOf("/", stop);
    }
	
    // DEBUG OUTPUT
    // System.out.println(result);
    return result;
  }
}
