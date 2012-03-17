//
// Dialog to start a partial update
// (c) Jimmy Larsson 1998
//

import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class PartialUpdateDialog extends Frame implements ActionListener
{

  protected boolean done;
  protected DisplayListener ds;
  protected String localBaseDir;

  //UI Objects
  protected Button cancel, start; // buttons
  protected TextField topDir; 

    // Sub-containers for all this stuff.
  protected Panel fieldPanel, buttonPanel, mainPanel;

  // The layout manager for each of the containers.
  protected GridBagLayout gridbag = new GridBagLayout();
 
  public PartialUpdateDialog (String localBaseDir, DisplayListener ds)
  {
    this("Start a partial update");
    this.ds = ds;
    this.localBaseDir = localBaseDir;
    this.topDir.setText(localBaseDir);
  }

  public PartialUpdateDialog(String title) 
  {
    super(title);

    
    ///////////////////////////////////////
    // UGLY UI STUFF (REALLY UGLY!) ///////
    ///////////////////////////////////////

    mainPanel = new Panel ();
    mainPanel.setLayout(gridbag);


    // Create pushbuttons
    Button start = new Button("Start");
    start.addActionListener(this);
    start.setActionCommand("start");

    Button cancel = new Button("Cancel");
    cancel.addActionListener(this);
    cancel.setActionCommand("cancel");

    // Create textfields
    topDir = new TextField();
    topDir.addActionListener(this);

    Panel dirButtonPanel = new Panel();
    dirButtonPanel.setLayout(gridbag);
    
    // Place the buttons
    constrain(dirButtonPanel, start, 0, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);
    constrain(dirButtonPanel, cancel, 2, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.25, 0, 0, 0, 0);

    // Create a Panel to contain all the components along the
    // top-left part of the window.  Use a GridBagLayout for it.
    fieldPanel = new Panel();
    fieldPanel.setLayout(gridbag);
    
    // Place textfields
    constrain(fieldPanel, new Label("Top Directory:"), 0, 0, 1, 1);
    constrain(fieldPanel, topDir, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
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
    this.setSize(350,120);
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

    if (action.equalsIgnoreCase("start"))
    {
      // START pressed
      setVisible(false);
      ds.startPartial (topDir.getText());
      dispose();


    } else if (action.equalsIgnoreCase("cancel"))
    {
      // CANCEL pressed
      setVisible(false);
      dispose();

    }
  }

}




