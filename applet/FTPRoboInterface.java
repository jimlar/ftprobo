//
// This is the applet used to interface my FTPRobo
// (c) Jimmy Larsson 1998
//

import java.applet.*;
import java.awt.*;

public class FTPRoboInterface extends Applet
{
  protected TextField loginName;
  protected TextField loginPass;
  protected Button loginButton;
  protected GridBagLayout gridbag = new GridBagLayout ();
  protected CardLayout cards = new CardLayout ();

  public void init ()
  {

    // Create all components
    loginName = new TextField ();
    loginPass = new TextField ();
    loginButton = new Button ("Login");

    this.setLayout (cards);

    /// LOGIN PAGE ///////////////
    // The fields
    Panel loginPanel = new Panel ();
    loginPanel.setLayout (gridbag);

    Panel fieldPanel = new Panel();
    fieldPanel.setLayout(gridbag);
    
    // Place textfields
    constrain(fieldPanel, new Label("Login name:"), 0, 0, 1, 1);
    constrain(fieldPanel, loginName, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);
    
    constrain(fieldPanel, new Label("Password:"), 0, 2, 1, 1);
    constrain(fieldPanel, loginPass, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
	      GridBagConstraints.NORTHWEST, 1.0, 0.0, 0, 0, 0, 0);

    // The button(s)
    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(gridbag);
    
    // Place the button
    constrain(buttonPanel, loginButton, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
              GridBagConstraints.SOUTH, 0.0, 1.0, 2, 2, 2, 2);

    // Add all panels to loginPanel
    constrain(loginPanel, fieldPanel, 0, 0, 1, 1, GridBagConstraints.BOTH,
	GridBagConstraints.NORTHWEST, 1.0, 1.0 ,0, 0, 0, 0);
    constrain(loginPanel, buttonPanel, 1, 0, 1, 1, GridBagConstraints.BOTH,
              GridBagConstraints.NORTHEAST, 0.0, 0.0, 0, 0, 0, 0);
    
    // And add topPanel to toplevel window
    this.add ("login", loginPanel);

    
    /// NORMAL USER PAGE ///
    

  }



  protected void constrain(Container container, Component component, 
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
    
  protected void constrain(Container container, Component component, 
	       int grid_x, int grid_y, int grid_width, int grid_height) 
  {
    constrain(container, component, grid_x, grid_y, 
              grid_width, grid_height, GridBagConstraints.NONE, 
              GridBagConstraints.NORTHWEST, 0.0, 0.0, 0, 0, 0, 0);
  }
    
  protected void constrain(Container container, Component component, 
	    int grid_x, int grid_y, int grid_width, int grid_height,
                  int top, int left, int bottom, int right) 
  {
    constrain(container, component, grid_x, grid_y, 
              grid_width, grid_height, GridBagConstraints.NONE, 
              GridBagConstraints.NORTHWEST, 
              0.0, 0.0, top, left, bottom, right);
  }
    

}

