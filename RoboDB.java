//
// RoboDB, this is the database class for my FTP robot
// (c) Jimmy Larsson 1998
//

import java.util.*;
import java.io.*;

public class RoboDB 
{
  protected Vector entries;
  public static final int COPY_BUFFER_SIZE = 1024;

  public RoboDB ()
  {
    entries = new Vector ();
  }

  //Are we empty?
  public boolean empty ()
  {
    return entries.isEmpty();
  }

  // Add all files to the database
  public synchronized void addFiles (RoboConfig config)
  {
    addFilesInPath (config.getLocalPath(), config);
  }

  //Add files from path and recursively
  public synchronized void addFilesFromPath (String path, RoboConfig config)
  {
    addFilesInPath (config.getLocalPath() + File.separator + path, config);
  }

  //Add an entry
  public synchronized void add (DBEntry entry)
  {
    FTPRobo.getStatus().incTotalUploads();
   
    entries.addElement(entry);
  }

  // Max connections for entry reached, put last in queue for later use
  public synchronized void tossBack (DBEntry entry)
  {
    entries.addElement(entry);
  }

  //Get next availiable entry
  public synchronized DBEntry getNext ()
  {
    if (entries.isEmpty())
      return null;
    
    DBEntry tmpEntry = (DBEntry) entries.firstElement();
    entries.removeElement(tmpEntry);
    
    if (tmpEntry.timeToRetry())
      return tmpEntry;
    else
    {
      tossBack (tmpEntry);
      return null;
    }
  }


  //Entry failed, increase retries and put back in queue, or discard entry
  public synchronized void retry (DBEntry entry)
  {
    entry.incRetryCount();
    if (entry.retryCount() > FTPRobo.MAX_RETRIES)
    {
      ///// MOVE FAILED FILE TO FAILED-DIRECTORY
      String relFile = entry.getFile().getAbsolutePath().substring(FTPRobo.getConfig().getCachePath().length());
      FTPRobo.log.addMess (relFile + " failed all retries.");

      String path = FTPRobo.getConfig().getFailedPath();

      if (!entry.getFile().getParent().equalsIgnoreCase(FTPRobo.getConfig().getCachePath()))
      {
	/// DEBUGGING MESS
	///System.out.println (entry.getFile().getParent() + ", substr index " + FTPRobo.getConfig().getCachePath().length());

	String pathEnd = entry.getFile().getParent().substring(FTPRobo.getConfig().getCachePath().length());
	
	if (path.endsWith (File.separator) && pathEnd.startsWith (File.separator))
	  pathEnd = pathEnd.substring (1);
	
	if (!path.endsWith (File.separator) && !pathEnd.startsWith (File.separator))
	  path = path.concat (File.separator);
	
	path = path.concat (pathEnd);

	File newDir = new File (path);
	newDir.mkdirs();
      }

      File failedFile = new File (path, entry.getFile().getName());

      entry.getFile().renameTo (failedFile);
      FTPRobo.getStatus().incFailedUploads();
 
    } else
    {
      //Get retry time
      long retryTime;
      if (entry.retryCount() < RoboConfig.RETRY_TIMES.length)
	retryTime = RoboConfig.RETRY_TIMES[entry.retryCount()];
      else
	retryTime = RoboConfig.RETRY_TIMES[RoboConfig.RETRY_TIMES.length - 1];
      
      entry.retryIn (retryTime);

      //Keep track of retried files
      String timeString;
      if (retryTime > 60000)
	timeString = retryTime/60000 + " minutes";
      else
	timeString = retryTime/1000 + " seconds";

      String relFile = entry.getFile().getAbsolutePath().substring(FTPRobo.getConfig().getCachePath().length());

      FTPRobo.getStatus().incRetries();
      FTPRobo.log.addMess ("retrying " + relFile + " in " + timeString);
      
      entries.addElement(entry);
      
    }
  }

  public synchronized void clear ()
  {
    entries = new Vector ();
  }

  protected void addFilesInPath (String thePath, RoboConfig config)
  {
    File dir = new File (thePath);
    String[] files;
    
    files = dir.list();

    /// DEBUG
      //System.out.println ("Files: " + files);
    for (int i = 0; i < files.length; i++)
    {
      File curFile = new File (dir.getPath(), files[i]);

      if (!curFile.getAbsolutePath().equalsIgnoreCase(config.getFailedPath()))
      {
	// DEBUG PRINT
	//System.out.println (curFile.toString());

	if (curFile.isDirectory())
	{
	  addFilesInPath (curFile.getAbsolutePath(), config);
	} else
	{
	  // Check if this file needs update...
	  long curLM = curFile.lastModified();

	  if (curLM > config.getLastTotalUpdate() && curLM < FTPRobo.startLastModified)
	  {
	    // The file needs an update... do it...
	    Enumeration servers = FTPRobo.getConfig().getFtpServers().elements();
	    Enumeration paths = FTPRobo.getConfig().getFtpServerPaths().elements();
	    Enumeration users = FTPRobo.getConfig().getFtpUsers().elements();
	    Enumeration passes = FTPRobo.getConfig().getFtpPasses().elements();
	  
	    while (servers.hasMoreElements())
	    {
	      String server = (String) servers.nextElement();
	      String path = (String) paths.nextElement();
	      String user = (String) users.nextElement();
	      String pass = (String) passes.nextElement();

	
	      if (!curFile.getParent().equalsIgnoreCase(config.getLocalPath()))
	      {
		String pathEnd = curFile.getParent().substring(config.getLocalPath().length());

		if (path.endsWith (File.separator) && pathEnd.startsWith (File.separator))
		  pathEnd = pathEnd.substring (1);

		if (!path.endsWith (File.separator) && !pathEnd.startsWith (File.separator))
		  path = path.concat (File.separator);

		path = path.concat (pathEnd);

		// DEBUG PRINT
		//System.out.println (path + "   " + pathEnd + "    "  + curFile.getParent());
	      }
	      
	      // Copy the file to the cache directory and add the copy instead !!
	      String cacheFileName = new String ();
	      cacheFileName = cacheFileName.concat (config.getCachePath ());
	      cacheFileName = cacheFileName.concat (curFile.getAbsolutePath().substring (config.getLocalPath().length(), curFile.getAbsolutePath().length()));
	    
	      // DEBUG PRINT
	      //System.out.println (cacheFileName);
       
	      File cacheFile = new File (cacheFileName);
	      try {
		copyFile (curFile, cacheFile);
	      } catch (IOException e1)
	      {
		FTPRobo.log.addMess ("unable to copy " + curFile.toString() + " to cache!");
		break;
	      }

	      DBEntry entry = new DBEntry (cacheFile, server, path, user, pass);
	      add (entry);

	      // DEBUG PRINT
	      System.out.println ("Added file " + curFile.toString() + " to DataBase");
	    }
	  }
	}
      }
    }
  }

  protected void copyFile (File from, File to) throws IOException
  {
    // Make dirs if they are missing!
    File parentDir = new File (to.getParent());
    parentDir.mkdirs();

    // DEBUG PRINT
    //System.out.println ("To FILE: " + to.toString());

    byte[] buffer = new byte[COPY_BUFFER_SIZE];

    FileInputStream in = new FileInputStream (from);
    FileOutputStream out = new FileOutputStream (to);

    while (in.available() > 0)
    {
      int bytesRead = in.read (buffer);
      
      if (bytesRead == -1)
	break;

      out.write (buffer, 0, bytesRead);
    }

    out.flush ();
    out.close ();
    in.close ();
  }
    
}

      
