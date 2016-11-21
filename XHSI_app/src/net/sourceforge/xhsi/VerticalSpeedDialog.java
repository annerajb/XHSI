/**
 * AltitudeDialog.java
 * 
 * Dialog for setting holding
 * 
 * Copyright (C) 2016  Nicolas Carel
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sourceforge.xhsi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.xhsi.model.Avionics;


public class VerticalSpeedDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SET = "Set";
    private static final String CANCEL = "Cancel";

    private JTextField vv;

    private String field_validation_errors = null;

    private Avionics avionics;


    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public VerticalSpeedDialog(JFrame owner_frame, Avionics avionics) {
        super(owner_frame, "Vertical Speed");

        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_altitude_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);
        
        this.vv.setText("");
        pack();
        this.avionics=avionics;        		
    }

    
    public void init_altitude() {
        this.vv.setText(""+Math.round(avionics.autopilot_vv()));
    }


    private JPanel create_altitude_panel() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel altitude_panel = new JPanel(layout);

        int dialog_line = 0;

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // altitude
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel fix_label = new JLabel("New V/S target", JLabel.TRAILING);
        altitude_panel.add(fix_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.vv = new JTextField(5);
        altitude_panel.add(this.vv, cons);
        dialog_line++;

        return altitude_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel button_panel = new JPanel(layout);

        JButton hide_button = new JButton("Set");
        hide_button.setActionCommand(VerticalSpeedDialog.SET);
        hide_button.addActionListener(this);
        
        // TODO : Set and Engage button

        JButton show_button = new JButton("Cancel");
        show_button.setActionCommand(VerticalSpeedDialog.CANCEL);
        show_button.addActionListener(this);

        button_panel.add(hide_button);
        button_panel.add(show_button);

        return button_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(VerticalSpeedDialog.SET)) {
        	set_vertical_speed();
        } else if (event.getActionCommand().equals(VerticalSpeedDialog.CANCEL)) {
            // show_holding();
        }
    }


    private void set_vertical_speed() {
        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid vertical speed",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // vertical speed
            if ( this.vv.getText().equals("") ) {
                avionics.set_autopilot_vv(0);
            } else {
            	avionics.set_autopilot_vv(Integer.parseInt(this.vv.getText()));
            }
        }
    }


    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // vertical speed
        if ( ! this.vv.getText().equals("") ) {
            try {
                int vv_value = Integer.parseInt(this.vv.getText());
                if ((vv_value < -5000) || (vv_value > 5000)) {
                    field_validation_errors += "Vertical speed out of range (-5000 to 5000)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Vertical speed contains non-numeric characters!\n";
            }
        }

        if ( ! field_validation_errors.equals("") ) {
            field_validation_errors = field_validation_errors.trim();
            return false;
        } else {
            return true;
        }
    }

}
