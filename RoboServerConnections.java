//
// RoboServerConnections.java
// This class is used to keep track of server connections
//

import java.util.*;

public class RoboServerConnections
{
  protected Vector servers;
  protected Vector connections;
  protected Vector maxCons;

  public RoboServerConnections ()
  {
    clear ();
    update ();
  }

  //Clear all
  public void clear ()
  {
    servers = new Vector ();
    connections = new Vector ();
    maxCons = new Vector ();
  }

  //Add new servers (don't delete removed ones)
  public synchronized  void update ()
  {
    Enumeration cnfServers = FTPRobo.getConfig().getFtpServers().elements();
    Enumeration cnfConns = FTPRobo.getConfig().getFtpConnections().elements();
    
    while (cnfServers.hasMoreElements())
    {
      String server = (String) cnfServers.nextElement();
      String conn = (String) cnfConns.nextElement();

      if (!this.servers.contains (server))
      {
	this.servers.addElement (server);
	this.maxCons.addElement (new Integer (conn));
	this.connections.addElement (new Integer (0));
      }
    }
  }

  public synchronized boolean connectTo (String hostname)
  {
    int index = servers.indexOf (hostname);

    if (index < 0)
      return false;

    Integer con = (Integer) connections.elementAt (index);
    Integer maxCon = (Integer) maxCons.elementAt (index);

    if (con.intValue() < maxCon.intValue())   //We're home free.. =), connect
    {
      con = new Integer (con.intValue() + 1);
      connections.setElementAt (con, index);
      return true;
    } else
    {
      return false;
    }
  }

  public synchronized void disconnectFrom (String hostname)
  {
    int index = servers.indexOf (hostname);

    if (index < 0)
      return;

    Integer con = (Integer) connections.elementAt (index);
    if (con.intValue() > 0)
    {
      con = new Integer (con.intValue() - 1);
      connections.setElementAt (con, index);
    }
  }

}
