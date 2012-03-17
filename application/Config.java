//
// Config, this is where all configs are, this are only read, never written back!
// (c) Jimmy Larsson 1998
//

import java.util.*;
import java.io.*;

public class Config
{
  public String localPath;
  public String cachePath;
  public String failedPath;
  public Vector ftpServers;
  public Vector ftpServerPaths;
  public Vector ftpUsers;
  public Vector ftpPasses;
  public Vector ftpConnections;
  
  protected RoboClient rc;


  public Config (ClientConnection cc, RoboClient rc)
  {
    getConfig (cc);
    this.rc = rc;
  }

  public void updateDisplay ()
  {
    rc.setLocalPath (localPath);
    rc.setCachePath (cachePath);
    rc.setFailedPath (failedPath);

  
    rc.clearServers();
    Enumeration servers = ftpServers.elements();
    Enumeration paths = ftpServerPaths.elements();
    Enumeration users = ftpUsers.elements();
    Enumeration passes = ftpPasses.elements();
    Enumeration conns = ftpConnections.elements();
     
    while (servers.hasMoreElements())
    {
      String server = (String) servers.nextElement();
      String path = (String) paths.nextElement();
      String user = (String) users.nextElement();
      String realPass = (String) passes.nextElement();
      String conn = (String) conns.nextElement();
 
      rc.addServer ("ftp://" + server + path);
    }
  }

  public void getConfig (ClientConnection cc)
  {
    try {
      Vector paths = cc.getPaths();
      if (paths != null)
      {
	Enumeration e = paths.elements();
	
	localPath = (String) e.nextElement();
	cachePath = (String) e.nextElement();
	failedPath = (String) e.nextElement();
      }

      Vector serverData = cc.getServers();
      if (serverData != null)
      {
	Enumeration e2 = serverData.elements();
	
	ftpServers = (Vector) e2.nextElement();
	ftpServerPaths = (Vector) e2.nextElement();
	ftpUsers = (Vector) e2.nextElement();
	ftpPasses = (Vector) e2.nextElement();
	ftpConnections = (Vector) e2.nextElement();
      }
    } catch (IOException e1)
    {
      System.out.println ("Error getting configuration! " + e1);
    }
      
  }

}
