//
// RoboThread, this is the tread class for my FTP robot
// (c) Jimmy Larsson 1998
//

import java.io.*;
import sun.net.ftp.*;

public class RoboThread extends Thread
{
  protected RoboDB dataBase;
  protected boolean stop;
  protected boolean sleeping;
  protected static long noOfThreads = 0;
  protected static long noAsleep = 0;
  protected static boolean done = false;


  public RoboThread ()
  {
    noOfThreads++;
    sleeping = false;
    dataBase = FTPRobo.getDB();
    stop = false;
    start();
  }

  public static void resetThreads ()
  {
    noOfThreads = 0;
    noAsleep = 0;
    done = false;
  }

  public static boolean allSleeping ()
  {
    if (noAsleep == noOfThreads)
      return true;

    return false;
  }

  public void signalStop ()
  {
    stop = true;
  }

  public void run ()
  {
    DBEntry curEntry;
    RoboConnection con;

    while (!done)
    {
      curEntry = dataBase.getNext ();

      if (curEntry != null)
      {
	if (sleeping)
	{
	  noAsleep--;
	  sleeping = false;
	}

	try {
	  if (FTPRobo.connections.connectTo (curEntry.getHost()))
	  {
	    con = new RoboConnection (curEntry);
	    con.upload();
	    FTPRobo.getStatus().incSuccessedUploads();
	    FTPRobo.connections.disconnectFrom (curEntry.getHost());
	  } else
	  {
	    dataBase.tossBack (curEntry);
	  }
	} catch (FtpLoginException e1)
	{
	  //System.out.println ("error logging in: " + e1);
	  //FTPRobo.log.addMess ("error logging in: " + e1.toString());
	  FTPRobo.connections.disconnectFrom (curEntry.getHost());
	} catch (FileNotFoundException e1)
	{
	  //System.out.println ("file not found: " + e1);
	  //FTPRobo.log.addMess ("file not found: " + e1.toString());
	  dataBase.retry(curEntry);
	  FTPRobo.connections.disconnectFrom (curEntry.getHost());
	} catch (IOException e2)
	{
	  //System.out.println ("io error: " + e2);
	  //FTPRobo.log.addMess ("io error: " + e2.toString());
	  dataBase.retry(curEntry);
	  FTPRobo.connections.disconnectFrom (curEntry.getHost());
	}
	
      } else
      {
	if (!sleeping)
	{
	  sleeping = true;
	  noAsleep++;

	  // DEBUG OUTPUT
	  //System.out.println ("Sleeping: " + noAsleep);

	  if (allSleeping() && dataBase.empty())
	  {
	    done = true;
	    FTPRobo.getInstance().roboReady();
	  }
	}
	yield();
      }

      if(stop)
	break;

    }
    noOfThreads--;
    noAsleep = 0;

  }

}
