//
// This is the FTPRobo, application interface
// (c) Jimmy Larsson
//

import java.awt.*;
import java.io.*;

public class RoboClient extends Frame 
{
  public static final String CLIENT_VERSION_STRING = "FTPRobo client 0.13, (c) Jimmy Larsson 1998";
  //Networking objects
  public ClientConnection cc;
  public Config config;


  //UI Objects
  protected DisplayListener listener;
  protected MenuBar menubar;                                // the menubar
  protected Menu file, advanced;                                     // menu panes
  protected MenuItem partialUpdate, fileMValid, fileQuit, helpAbout, helpSearchTips;
  protected Button clear, start, status, viewLog, stop; // buttons
  protected TextField localDirField, cacheDirField, failedDirField; 
  protected TextArea statusArea;      // A list
  protected List serverList;
    // Sub-containers for all this stuff.
  protected Panel fieldPanel, statusPanel, tabPage1, tabPage2, tabPage3, buttonpanel;
  protected TabbedNotebook tabNotebook;
  

  // The layout manager for each of the containers.
  protected GridBagLayout gridbag = new GridBagLayout();

  // Labels for the statistics page  
  protected Label statProcessed, statWaiting, statBad, statTreeLevel;
 
  public RoboClient(String title) 
  {
    super(title);

    
    ///////////////////////////////////////
    // UGLY UI STUFF (REALLY UGLY!) ///////
    ///////////////////////////////////////

    // Create listener
    listener = new DisplayListener (this);

    // Create TabbedNotebook
    tabNotebook = new TabbedNotebook ();

    // Create the menubar.  Tell the frame about it.
    menubar = new MenuBar();
    this.setMenuBar(menubar);

    // Create the file menu.  Add two items to it.  Add to menubar.
    file = new Menu("File");
    advanced = new Menu ("Advanced");

    // The advanced menu:

    fileMValid = new MenuItem("Mark valid now");
    fileMValid.addActionListener(listener);
    fileMValid.setActionCommand("markvalid");
    advanced.add(fileMValid);

    partialUpdate = new MenuItem("Start partial update");
    partialUpdate.addActionListener(listener);
    partialUpdate.setActionCommand("startfrompath");
    advanced.add(partialUpdate);

    // The file menu:

    helpAbout = new MenuItem("About");
    helpAbout.addActionListener(listener);
    helpAbout.setActionCommand("about");
    file.add(helpAbout);

    fileQuit = new MenuItem("Quit");
    fileQuit.addActionListener(listener);
    fileQuit.setActionCommand("quit");
    file.addSeparator();
    file.add(fileQuit);

    menubar.add(file);
    menubar.add(advanced);
    
    //help.addSeparator();

    //helpSearchTips = new MenuItem("Search Tips");
    //helpSearchTips.addActionListener(listener);
    //helpSearchTips.setActionCommand("searchtips");
    //help.add(helpSearchTips);

    //menubar.add(help);

    // Display the help menu in a special reserved place.
    //menubar.setHelpMenu(help);
    
    // CONTROL PAGE OF THE TABBED NOTEBOOK
    /////////////////////////////////////////////////////////////////////
    // Create pushbuttons
    start = new Button("Start");
    start.addActionListener(listener);
    start.setActionCommand("start");

    stop = new Button("Stop");
    stop.addActionListener(listener);
    stop.setActionCommand("stop");

    status = new Button("Status");
    status.addActionListener(listener);
    status.setActionCommand("status");

    clear = new Button("Clear");
    clear.addActionListener(listener);
    clear.setActionCommand("clear");

    viewLog = new Button("View log");
    viewLog.addActionListener(listener);
    viewLog.setActionCommand("viewlog");

    statusArea = new TextArea();
    statusArea.append (RoboClient.CLIENT_VERSION_STRING + "\n");
    
    // Create a panel to hold fieldPanel and buttonpanel
    tabPage1 = new Panel (gridbag);

    // Create a panel for the bottom textarea.
    // Use a GridBagLayout, and arrange items with constrain(), as above.
    statusPanel = new Panel();
    statusPanel.setLayout(gridbag);
    
    // Place textarea
    constrain(statusPanel, new Label("Transcript:"), 0, 0, 1, 1);
    constrain(statusPanel, statusArea, 0, 1, 1, 3, GridBagConstraints.BOTH,
              GridBagConstraints.NORTH, 1.0, 1.0, 0, 0, 0, 0);
    
    // Do the same for the buttons along the top-right part of the window.
    buttonpanel = new Panel();
    buttonpanel.setLayout(gridbag);
    
    // Place the buttons
    constrain(buttonpanel, start, 0, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(buttonpanel, stop, 0, 1, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(buttonpanel, status, 0, 2, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(buttonpanel, clear, 0, 3, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(buttonpanel, viewLog, 0, 4, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);


    constrain(tabPage1, statusPanel, 0, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.CENTER, 1.0, 1.0, 5, 2, 2, 2);

    constrain(tabPage1, buttonpanel, 1, 0, 1, 1, GridBagConstraints.NONE,
              GridBagConstraints.NORTHEAST, 0.0, 0.0, 5, 0, 5, 5);
    

    // SERVER PAGE OF THE TABBED NOTEBOOK
    ///////////////////////////////////////////////////////////////////////////
    tabPage2 = new Panel (new GridBagLayout());

    // Create pushbuttons
    Button addSrv = new Button("Add");
    addSrv.addActionListener(listener);
    addSrv.setActionCommand("srv_add");

    Button editSrv = new Button("Edit");
    editSrv.addActionListener(listener);
    editSrv.setActionCommand("srv_edit");

    Button removeSrv = new Button("Remove");
    removeSrv.addActionListener(listener);
    removeSrv.setActionCommand("srv_remove");

    Panel srvButtonPanel = new Panel();
    srvButtonPanel.setLayout(gridbag);
    // Place the buttons
    constrain(srvButtonPanel, addSrv, 0, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(srvButtonPanel, removeSrv, 1, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(srvButtonPanel, editSrv, 2, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);

    serverList = new List ();
    serverList.setMultipleMode(false);

    // Add to tabPage2
    constrain(tabPage2, srvButtonPanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
              GridBagConstraints.NORTHEAST, 0.0, 0.0, 5, 0, 5, 5);
    
    constrain(tabPage2, serverList, 0, 0, 1, 1, GridBagConstraints.BOTH, 
	      GridBagConstraints.NORTHWEST, 1.0, 1.0, 5, 5, 5, 5);


    // DIRECTORIES PAGE OF THE TABBED NOTEBOOK
    ///////////////////////////////////////////////////////////////////////////
    tabPage3 = new Panel (gridbag);

    // Create pushbuttons
    Button dirSave = new Button("Save");
    dirSave.addActionListener(listener);
    dirSave.setActionCommand("dir_save");

    Button dirCancel = new Button("Cancel");
    dirCancel.addActionListener(listener);
    dirCancel.setActionCommand("dir_cancel");

    // Create textfields
    localDirField = new TextField();
    localDirField.addActionListener(listener);

    cacheDirField = new TextField();
    cacheDirField.addActionListener(listener);

    failedDirField = new TextField();
    failedDirField.addActionListener(listener);

    Panel dirButtonPanel = new Panel();
    dirButtonPanel.setLayout(gridbag);
    
    // Place the buttons
    constrain(dirButtonPanel, dirSave, 0, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(dirButtonPanel, dirCancel, 2, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);

    // Create a Panel to contain all the components along the
    // top-left part of the window.  Use a GridBagLayout for it.
    fieldPanel = new Panel();
    fieldPanel.setLayout(gridbag);
    
    // Place textfields
    constrain(fieldPanel, new Label("Local homepage directory:"), 0, 0, 1, 1);
    constrain(fieldPanel, localDirField, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);
    
    constrain(fieldPanel, new Label("Cache directory:"), 0, 2, 1, 1);
    constrain(fieldPanel, cacheDirField, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);
    
    constrain(fieldPanel, new Label("Failed directory:"), 0, 4, 1, 1);
    constrain(fieldPanel, failedDirField, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);
    
    // Add fieldPanel and buttonpanel to tabPage3
    constrain(tabPage3, dirButtonPanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
              GridBagConstraints.NORTHEAST, 0.0, 0.0, 5, 0, 5, 5);
    
    constrain(tabPage3, fieldPanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 
                   GridBagConstraints.NORTHWEST, 1.0, 0.0, 5, 5, 5, 5);


    // Add pages to Notebook
    tabNotebook.addPage(tabPage1, "Controlling");
    tabNotebook.addPage(tabPage2, "Servers");
    tabNotebook.addPage(tabPage3, "Directories");


    // Finally, use a GridBagLayout to arrange the panels themselves
    this.setLayout(gridbag);
    
    // And add the notebook to the toplevel window
    constrain(this, tabNotebook, 0, 0, 1, 1, GridBagConstraints.BOTH, 
              GridBagConstraints.NORTHWEST, 1.0, 1.0, 0, 0, 0, 0);

    
    // We cannot be resized
    //this.setResizable(false);
    
    // pack and show
    this.setSize(450,300);
    this.show();
  }
  
  public void constrain(Container container, Component component, 
	    int grid_x, int grid_y, int grid_width, int grid_height,
                  int fill, int anchor, double weight_x, double weight_y,
                  int top, int left, int bottom, int right)
  {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = grid_x; c.gridy = grid_y;
    c.gridwidth = grid_width; c.gridheight = grid_height;
    c.fill = fill; c.anchor = anchor;
    c.weightx = weight_x; c.weighty = weight_y;
    if (top+bottom+left+right > 0)
      c.insets = new Insets(top, left, bottom, right);
    
    ((GridBagLayout)container.getLayout()).setConstraints(component, c);
    container.add(component);
  }
    
  public void constrain(Container container, Component component, 
	       int grid_x, int grid_y, int grid_width, int grid_height) 
  {
    constrain(container, component, grid_x, grid_y, 
              grid_width, grid_height, GridBagConstraints.NONE, 
              GridBagConstraints.NORTHWEST, 0.0, 0.0, 0, 0, 0, 0);
  }
    
  public void constrain(Container container, Component component, 
                  int grid_x, int grid_y, int grid_width, int grid_height,
                  int top, int left, int bottom, int right) 
  {
    constrain(container, component, grid_x, grid_y, 
              grid_width, grid_height, GridBagConstraints.NONE, 
              GridBagConstraints.NORTHWEST, 
              0.0, 0.0, top, left, bottom, right);
  }
    


  public void addStatus (String message)
  {
    statusArea.append (message + "\n");
  }

  public void addServer (String serverLabel)
  {
    serverList.add (serverLabel);
  }

  public void removeServer (int serverNo)
  {
    serverList.remove (serverNo);
  }

  public void clearServers ()
  {
    if (serverList.getItemCount() > 0)
      serverList.removeAll();
  }

  public int getSelectedServer ()
  {
    return serverList.getSelectedIndex();
  }

  public void clearStatus ()
  {
    statusArea.setText (RoboClient.CLIENT_VERSION_STRING + "\n");
  }

  public void quit ()
  {
    try {cc.close();} catch (IOException e1) {}
    System.exit (0);
  }

  public void setLocalPath (String path)
  {
    localDirField.setText(path);
  }

  public String getLocalPath ()
  {
    return localDirField.getText();
  }

  public void setCachePath (String path)
  {
    cacheDirField.setText (path);
  }

  public String getCachePath ()
  {
    return cacheDirField.getText();
  }

  public void setFailedPath (String path)
  {
    failedDirField.setText (path);
  }

  public String getFailedPath ()
  {
    return failedDirField.getText();
  }

  public static void main (String[] args)
  {
    RoboClient rc = new RoboClient ("RoboClient");
    rc.cc = new ClientConnection ("localhost", 6789, "moonfire", "moonfire");
    rc.config = new Config (rc.cc, rc);
    rc.config.updateDisplay();
    rc.addStatus ("Connected to server... awating command...");
  }

}




