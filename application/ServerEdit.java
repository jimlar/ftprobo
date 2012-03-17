//
// Dialog to edit ftpServers...
// (c) Jimmy Larsson 1998
//

import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class ServerEdit extends Frame implements ActionListener
{

  protected boolean done;
  protected DisplayListener ds;
  protected int index;
  protected boolean add;

  //UI Objects
  protected Button cancel, ok; // buttons
  protected TextField hostname, baseDir, userName, password, maxCons; 

    // Sub-containers for all this stuff.
  protected Panel fieldPanel, buttonPanel, mainPanel;

  // The layout manager for each of the containers.
  protected GridBagLayout gridbag = new GridBagLayout();
 
  public ServerEdit (DisplayListener ds)
  {
    this("Add server");
    this.ds = ds;
    add = true;
  }

  public ServerEdit (String host, String dir, String user, String pass, String cons, DisplayListener ds, int index)
  {
    this("Edit server: " + index);
    this.hostname.setText(host);
    this.baseDir.setText(dir);
    this.userName.setText(user);
    this.password.setText(pass);
    this.maxCons.setText(cons);
    add = false;
    this.index = index;
    this.ds = ds;
  }
  

  public ServerEdit(String title) 
  {
    super(title);

    
    ///////////////////////////////////////
    // UGLY UI STUFF (REALLY UGLY!) ///////
    ///////////////////////////////////////

    mainPanel = new Panel ();
    mainPanel.setLayout(gridbag);


    // Create pushbuttons
    Button ok = new Button("OK");
    ok.addActionListener(this);
    ok.setActionCommand("ok");

    Button cancel = new Button("Cancel");
    cancel.addActionListener(this);
    cancel.setActionCommand("cancel");

    // Create textfields
    hostname = new TextField();
    hostname.addActionListener(this);

    baseDir = new TextField();
    baseDir.addActionListener(this);

    userName = new TextField();
    userName.addActionListener(this);

    password = new TextField();
    password.addActionListener(this);

    maxCons = new TextField();
    maxCons.addActionListener(this);

    Panel dirButtonPanel = new Panel();
    dirButtonPanel.setLayout(gridbag);
    
    // Place the buttons
    constrain(dirButtonPanel, ok, 0, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(dirButtonPanel, cancel, 2, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);

    // Create a Panel to contain all the components along the
    // top-left part of the window.  Use a GridBagLayout for it.
    fieldPanel = new Panel();
    fieldPanel.setLayout(gridbag);
    
    // Place textfields
    constrain(fieldPanel, new Label("Hostname:"), 0, 0, 1, 1);
    constrain(fieldPanel, hostname, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);
    
    constrain(fieldPanel, new Label("Base directory:"), 0, 2, 1, 1);
    constrain(fieldPanel, baseDir, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);
    
    constrain(fieldPanel, new Label("Username:"), 0, 4, 1, 1);
    constrain(fieldPanel, userName, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);

    constrain(fieldPanel, new Label("Password:"), 0, 6, 1, 1);
    constrain(fieldPanel, password, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);

    constrain(fieldPanel, new Label("Max connections:"), 0, 8, 1, 1);
    constrain(fieldPanel, maxCons, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);

    
    // Add fieldPanel and buttonpanel to mainPanel
    constrain(mainPanel, dirButtonPanel, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
              GridBagConstraints.NORTHEAST, 0.0, 0.0, 5, 0, 5, 5);
    
    constrain(mainPanel, fieldPanel, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, 
                   GridBagConstraints.NORTHWEST, 1.0, 0.0, 5, 5, 5, 5);



    // Finally, use a GridBagLayout to arrange the panels themselves
    this.setLayout(gridbag);
    
    // And add the notebook to the toplevel window
    constrain(this, mainPanel, 0, 0, 1, 1, GridBagConstraints.BOTH, 
              GridBagConstraints.NORTHWEST, 1.0, 1.0, 0, 0, 0, 0);

    
    // We cannot be resized
    //this.setResizable(false);
    
    // pack and show
    this.setSize(350,320);
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
    


  // LISTENER STUFF //////////////////////////////////
  ////////////////////////////////////////////////////

  public void actionPerformed (ActionEvent e)
  {
    String action = e.getActionCommand();

    if (action.equalsIgnoreCase("ok"))
    {
      // OK pressed
      done = true;
      setVisible(false);
      if(add)
	ds.saveAddServer(hostname.getText(), baseDir.getText(), userName.getText(), password.getText(), maxCons.getText());
      else
	ds.saveEditServer(hostname.getText(), baseDir.getText(), userName.getText(), password.getText(), maxCons.getText(), index);
      dispose();


    } else if (action.equalsIgnoreCase("cancel"))
    {
      // CANCEL pressed
      done = true;
      setVisible(false);
      dispose();
    }
  }

  public boolean done ()
  {
    return done;
  }
}




