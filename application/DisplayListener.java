//
// The display listener, here all events are dispatched
// (c) Jimmy Larsson 1998
//

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public final class DisplayListener implements ActionListener, ItemListener
{
  protected RoboClient client;

  public DisplayListener (RoboClient c)
  {
    client = c;
  }

  // This catches:
  //       List double-click
  //       Button-click
  //       Textfield editing finished
  //       Menu choices

  public void actionPerformed (ActionEvent e)
  {
    String action = e.getActionCommand();

    if (action.equalsIgnoreCase("start"))
    {
      // START clicked
      try {
	if (client.cc.start())
	  client.addStatus ("Full Update started...");
	else
	  client.addStatus ("Start failed!");     
      } catch (IOException e1)
      {
	client.addStatus ("Start failed! " + e1);
      }     

    } else if (action.equalsIgnoreCase("stop"))
    {
      // STOP clicked
      try {
	if (client.cc.stop())
	  client.addStatus ("Server stopping...");
	else
	  client.addStatus ("Stop failed!");
      } catch (IOException e1)
      {
	client.addStatus ("Stop failed! " + e1);	
      }

    } else if (action.equalsIgnoreCase("status"))
    {
      // STATUS clicked
      try {
	Vector stats = client.cc.getStatus();
	if (stats != null)
	{
	  Enumeration en = stats.elements();

	  int successed = ((Integer) en.nextElement()).intValue();
	  int total = ((Integer) en.nextElement()).intValue();
	  int failed = ((Integer) en.nextElement()).intValue();
	  int retries = ((Integer) en.nextElement()).intValue();
	
	  client.addStatus ("Progress:");
	  client.addStatus (total + " uploads queued");
	  client.addStatus (successed + " done");
	  client.addStatus (failed + " failed");
	  client.addStatus (retries + " total retries");
	} else
	{
	  client.addStatus ("-Not running");
	}
	  
      } catch (IOException e1)
      {
	client.addStatus ("Status report failed! " + e1);	
      }

    } else if (action.equalsIgnoreCase("viewlog"))
      // VIEW LOG clicked
    {    
      try {
	String log = client.cc.getLog();
	
	client.addStatus ("\nLogfile:");
	client.addStatus (log);

      } catch (IOException e1)
      {
	client.addStatus ("View log failed! " + e1);	
      }


    } else if (action.equalsIgnoreCase("clear"))
      // CLEAR clicked
    {    
      client.clearStatus();


    } else if (action.equalsIgnoreCase("quit"))
    {
      // QUIT
      client.quit();
 
    } else if (action.equalsIgnoreCase("dir_cancel"))
    {
      // CANCEL dir edit
      client.config.updateDisplay();
      
    }else if (action.equalsIgnoreCase("markvalid"))
    {
      // markvalid
      try {
	if (client.cc.markValid())
	  client.addStatus ("Current tree marked valid");
	else
	  client.addStatus ("Could not mark tree valid!");
      } catch (IOException e1)
      {
	client.addStatus ("Could not perform makrvalid! " + e1);
      }

    } else if (action.equalsIgnoreCase("startfrompath"))
    {
      // start_from_path
      // open dialog to ask for path....
      // partial update

      
      // OLD way:
      PartialUpdateDialog pd = new PartialUpdateDialog (client.config.localPath + "/", this);

      // NEW way:
      //FileDialog fd = new FileDialog (client, "Select directory to start from");
      //fd.setDirectory (client.config.localPath);
      //fd.show();

      //client.addStatus (fd.getDirectory());

      //if (fd.getDirectory().length() > 0)
      //	startPartial (fd.getDirectory());

      
    } else if (action.equalsIgnoreCase("dir_save"))
    {
      // save local dirs
      
      String ul = client.getLocalPath();
      String cache = client.getCachePath();
      String failed = client.getFailedPath();

      try {
	if (client.cc.setPaths (ul, cache, failed))
	  client.addStatus ("Local paths set!");
	else
	  client.addStatus ("Couldn't set local paths!");
      } catch (IOException e1)
      {
	client.addStatus ("Couldn't set local paths! " + e1);
      }	
      client.config.getConfig(client.cc);
      client.config.updateDisplay();
      
    } else if (action.equalsIgnoreCase("srv_remove"))
    {
      // remove server
      int serverNo = client.getSelectedServer();

      if (serverNo != -1)
      {
	try {
	  serverNo++;
	  if (client.cc.delServer (serverNo))
	    client.addStatus ("Server " + serverNo + " deleted");
	  else
	    client.addStatus ("Error deleting server!");
	} catch (IOException e1)
	{
	  client.addStatus ("Error deleting server! " + e1);
	}

	client.config.getConfig(client.cc);
	client.config.updateDisplay();
      }	  


    } else if (action.equalsIgnoreCase("srv_edit"))
    {
      // edit server
      int index = client.getSelectedServer();
      if (index != -1)
      {
	String server = (String) client.config.ftpServers.elementAt (index);
	String path = (String) client.config.ftpServerPaths.elementAt (index);
	String user = (String) client.config.ftpUsers.elementAt (index);
	String pass = (String) client.config.ftpPasses.elementAt (index);
	String cons = (String) client.config.ftpConnections.elementAt (index);
	
	ServerEdit se = new ServerEdit (server, path, user, pass, cons, this, index);
      }

    } else if (action.equalsIgnoreCase("srv_add"))
    {
      // add server
      ServerEdit se = new ServerEdit (this);


    } else if (action.equalsIgnoreCase("about"))
    {
      // Put about text in status window...
      client.addStatus ("FTPRobo, client part, (c) Jimmy Larsson 1998");
      client.addStatus ("-------------");
      client.addStatus ("Bug reports, suggestions to:");
      client.addStatus (" jimmy@moonfire.se");
      client.addStatus ("-------------");
    }
  }

  public void startPartial (String absPath)
  {
    //check path and start update
    if (absPath.indexOf(client.config.localPath) != -1)
    {
      String relPath = absPath.substring (client.config.localPath.length()+1, absPath.length());

      try {
	if (client.cc.startFromPath (relPath))
	{
	  client.addStatus ("Partial update started from " + relPath);
	} else
	{
	  client.addStatus ("Error starting partial update from " + relPath);
	}
      } catch (IOException e1)
      {
	client.addStatus ("Error starting partial update from " + relPath + ":" + e1);
      }
    } else
    {
      client.addStatus ("Error starting partial update, not a valid path!");
    }

  }

  public void saveEditServer (String host, String path, String user, String pass, String cons, int index)
  {

    try {
      index++;
      if (client.cc.delServer (index))
      {
	if (client.cc.addServer (host, path, user, pass, (Integer.valueOf(cons)).intValue()))
	{
	  client.addStatus ("Server " + index + " edited and saved");
	}
	else
	{
	  client.addStatus ("Error saving server!");
	}
      }
      else
      {
	client.addStatus ("Error saving server!");
      }
    } catch (IOException e1)
    {
      client.addStatus ("Error saving server! " + e1);
    }

    client.config.getConfig(client.cc);
    client.config.updateDisplay();
  }

  public void saveAddServer (String host, String path, String user, String pass, String cons)
  {
    try {
      if (client.cc.addServer (host, path, user, pass, (Integer.valueOf(cons)).intValue()))
      {
	client.addStatus ("Server added and saved");
      }
      else
      {
	client.addStatus ("Error saving server!");
      }
    } catch (IOException e1)
    {
      client.addStatus ("Error saving server! " + e1);
    }

    client.config.getConfig(client.cc);
    client.config.updateDisplay();

  }

  



  // This catches:
  //       Checkbox
  //       CheckboxMenuItem
  //       Choice
  //       List (item selected)

  public void itemStateChanged (ItemEvent e)
  {
 

  }
}
