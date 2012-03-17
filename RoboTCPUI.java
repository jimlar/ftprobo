//
// RoboTCPUI, this is the interface for controlling Robo via telnet... nice eh? =)
// (c) Jimmy Larsson 1998
//

import java.net.*;
import java.io.*;

public class RoboTCPUI extends Thread
{
  public static final int UI_TCP_PORT = 6789;
  
  protected int port;
  protected ServerSocket socket;

  public RoboTCPUI (int prt)
  {
    if (prt == 0)
      port = UI_TCP_PORT;
    else
      port = prt;
    

    try {
      socket = new ServerSocket(port);
    } catch (IOException e1) {}

    this.start();
    System.out.println ("RoboUI started, listening on port " + port);

  }

  public void run ()
  {
    try {
      while (true)
      {
	Socket clientSocket = socket.accept();
	System.out.println("UI Connected to client");
	UIConnection connection = new UIConnection (clientSocket);
      }
    } catch (IOException e1)
    {
      System.out.println ("RoboUI: while waiting for clients to connect!");
    }
  }

  public static void main (String[] args)
  {
    RoboTCPUI ui = new RoboTCPUI(0);
  }

}
  
    
