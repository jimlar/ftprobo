//
// This is the clients connection to the server
// (c) Jimmy Larsson 1998
//

// MAIL SETTINGS NOT IMPLEMENTED YET...
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// USER SETTINGS NOT IMPLEMENTED EITHER
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


import java.net.*;
import java.io.*;
import java.util.*;


public class ClientConnection
{
  protected Socket socket = null;
  protected BufferedReader in = null;
  protected PrintWriter out = null;;


  public ClientConnection (String host, int port, String user, String pass)
  {
    try {
      socket = new Socket (host,port);
      in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
      out = new PrintWriter (socket.getOutputStream());

      waitFor ("ogin:");
      out.println (user);
      out.flush();
      waitFor ("assword:");
      out.println (pass);
      out.flush();
      in.readLine();

      System.out.println ("Client connected to " + host + " at port " + port);

    } catch (IOException e1)
    {
      // FATAL
      System.err.println ("!! error getting socket streams:" + e1.toString());
      System.exit (1);
    }
  }

  public boolean start () throws IOException
  {
    out.println ("START");
    out.flush();

    if (in.readLine().indexOf('+') != -1)
      return true;
    else
      return false;
  }

  public boolean startFromPath (String path) throws IOException
  {
    out.println ("startfrompath " + path);
    out.flush();
    if (in.readLine().indexOf('+') != -1)
      return true;
    else
      return false;
  }

  public boolean stop () throws IOException
  {
    out.println ("STOP");
    out.flush();

    if (in.readLine().indexOf('+') != -1)
      return true;
    else
      return false;

  }

  public void close () throws IOException
  {
    out.println ("quit");
    out.flush();
    socket.close();
  }

  public void shutdown () throws IOException
  {
    out.println ("shutdown");
    out.flush();
  }


  // Returns log in string
  public String getLog () throws IOException
  {
    out.println ("showlog");
    out.flush();
    String line = new String ("");
    String result = new String ("");

    while (!line.equalsIgnoreCase("+OK"))
    {
      result = result.concat (line + "\n");
      line = in.readLine();
    }

    return result;
  }

  //Returns paths in vector, format: [LOCAL, CACHE, FAILED] (strings)
  public Vector getPaths () throws IOException
  {
    Vector result = new Vector ();
    String line;

    out.println ("showpaths");
    out.flush();
    
    line = in.readLine();
    if (line.indexOf('+') == -1)
    {
      System.out.println ("Error: " + line);
      return null;
    }

    result.addElement (line.substring ("+LOCAL".length() + 1, line.length()));
    line = in.readLine ();
    result.addElement (line.substring ("+CACHE".length() + 1, line.length()));
    line = in.readLine ();
    result.addElement (line.substring ("+FAILED".length() + 1, line.length()));
    
    return result;
  }


  //Returns status in Vector, format: [successed, total, failed, retries] (Integers) 
  public Vector getStatus () throws IOException
  {
    out.println ("status");
    out.flush();
    String line;
    Vector result = new Vector ();

    line = in.readLine();
    if (line.indexOf('+') == -1)
      return null;
    
    StringTokenizer st = new StringTokenizer (line);
    //throw away '+'
    st.nextToken();

    for (int i = 0; i < 4; i++)
    {
      result.addElement (Integer.valueOf(st.nextToken()));
    }

    return result;
  } 
   
  // Adds a server (you don't say... =)
  public boolean addServer (String host, String basePath, String user, String pass, int maxConns) throws IOException
  {
    out.println ("addsrv " + host + " " + basePath + " " + user + " " + pass + " " + maxConns);
    out.flush();

    String line = in.readLine();
    if (line.indexOf('+') == -1)
      return false;

    return true;
  }

  // remove server, server numbering starting from 1
  public boolean delServer (int server) throws IOException
  {
    out.println ("delsrv " + server);
    out.flush();

    String line = in.readLine();
    if (line.indexOf('+') == -1)
      return false;
    
    return true;
  }

  // Mark current upload tree valid
  public boolean  markValid () throws IOException
  {
    out.println ("markvalid");
    out.flush();

    String line = in.readLine();
    if (line.indexOf('+') == -1)
      return false;
    
    return true;
  }

  // MAIL SETTINGS NOT IMPLEMENTED YET...
  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

  public boolean setPaths (String upload, String cache, String failed) throws IOException
  {
    out.println ("setpaths " + upload + " " + cache + " " + failed);
    out.flush();

    String line = in.readLine();
    if (line.indexOf('+') == -1)
      return false;
    
    return true;
  }


  //List servers, return Vector, format: [Vector: hosts, Vector: paths, Vector: users, Vector:passes, Vector: maxConns]
  public Vector getServers () throws IOException
  {
    Vector result = new Vector ();
    Vector hosts = new Vector ();
    Vector paths = new Vector ();
    Vector users = new Vector ();
    Vector pass = new Vector ();
    Vector maxConns = new Vector ();

    String line = new String ();

    out.println ("listsrv");
    out.flush();
    
    while (!line.equalsIgnoreCase("+OK"))
    {
      line = in.readLine();
      if (line.indexOf('+') == -1)
	return null;

      StringTokenizer st = new StringTokenizer (line);
      //throw away '+'
      if (st.nextToken().equalsIgnoreCase("+OK"))
	break;
      st.nextToken();  //throw away the server number

      hosts.addElement (st.nextToken());
      paths.addElement (st.nextToken());
      users.addElement (st.nextToken());
      pass.addElement (st.nextToken());
      maxConns.addElement (st.nextToken());
    }

    result.addElement (hosts);
    result.addElement (paths);
    result.addElement (users);  
    result.addElement (pass);
    result.addElement (maxConns);

    return result;

  }

  ////////////////////////////////////////////////  
  // Protected helpers ///////////////////////////
  ////////////////////////////////////////////////

  protected void waitFor (String word) throws IOException
  { 
    //Blocks until "word" has been received
    char ch;
    int count = 0;

    while (true)
    {
      ch = (char) in.read();
      if (ch == word.charAt(count))
	count++;
      else
	count = 0;

      if (count == word.length() - 1)
	break;
      
      // DEBUGGING SHIT
      ///System.out.println ("Char: " + ch + " count " + count + " charAt " + word.charAt(count) + " word " + word);
    }
  }

  public static void main (String[] args) throws IOException
  {
    ClientConnection cc = new ClientConnection ("emma.jimnet.se",6789, "moonfire", "moonfire");

    cc.close();

  }
}
