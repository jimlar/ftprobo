//
// DBEntry, this is the objects that lie in the database
// (c) Jimmy Larsson 1998
//

import java.io.*;
import java.util.*;

public final class DBEntry
{
  protected File file;
  protected String server;
  protected String serverRoot;
  protected int retries;
  protected String user;
  protected String password;
  protected Date nextRetry;

  public DBEntry (File f, String ftp, String root, String usr, String pass)
  {
    file = f;
    server = ftp;
    retries = 0;
    user = usr;
    password = pass;
    serverRoot = root;
    nextRetry = new Date ();
  }

  public String getHost ()
  {
    return server;
  }
    
  public void retryIn (long millis)
  {
    Date tmp = new Date ();
    tmp.setTime (tmp.getTime() + millis);
    nextRetry = tmp;
  }

  public boolean timeToRetry ()
  {
    Date now = new Date ();

    if (now.after(nextRetry))
      return true;
    else
      return false;
  }
 
  public String getServerRoot ()
  {
    return serverRoot;
  }

  public String getPass ()
  {
    return password;
  }

  public String getUser ()
  {
    return user;
  }

  public File getFile ()
  {
    return file;
  }

  public int retryCount ()
  {
    return retries;
  }

  public void incRetryCount ()
  {
    retries++;
  }
}
