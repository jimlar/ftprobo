//
// RoboStatus, here is where all status is stored
// (c) Jimmy Larsson 1998
//

import java.util.*;

public class RoboStatus
{
  protected long totalUploads = 0;
  protected long failedUploads = 0;
  protected long successedUploads = 0;
  protected long retries = 0;
  protected Date startTime = null;
  protected Date stopTime = null;

  public void clear ()
  {
    totalUploads = 0;
    failedUploads = 0;
    successedUploads = 0;
    retries = 0;
    startTime = null;
    stopTime = null;
  }

  public void incTotalUploads ()
  {
    totalUploads++;
  }

  public void incFailedUploads ()
  {
    failedUploads++;
  }

  public void incSuccessedUploads ()
  {
    successedUploads++;
  }

  public void incRetries ()
  {
    retries++;
  }

  public long getTotalUploads ()
  {
    return totalUploads;
  }

  public long getFailedUploads ()
  {
    return failedUploads;
  }

  public long getSuccessedUploads ()
  {
    return successedUploads;
  }

  public long getRetries ()
  {
    return retries;
  }
  
  public void stampStartTime ()
  {
    startTime = new Date ();
  }

  public void stampStopTime ()
  {
    stopTime = new Date ();
  }

  public Date getStartTime ()
  {
    return startTime;
  }

  public Date getStopTime ()
  {
    return stopTime;
  }
}
