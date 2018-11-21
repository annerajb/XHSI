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


public class HeadingDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SET = "Set";
    private static final String CANCEL = "Cancel";

    private JTextField heading;

    private String field_validation_errors = null;

    private Avionics avionics;

    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public HeadingDialog(JFrame owner_frame, Avionics avionics) {
        super(owner_frame, "Heading");

        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_heading_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);
        
        this.heading.setText("");
        pack();
        this.avionics=avionics;        		
    }

    
    public void init_heading() {
        this.heading.setText(""+Math.round(avionics.heading_bug()));
    }


    private JPanel create_heading_panel() {
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
        JLabel fix_label = new JLabel("New heading bug", JLabel.TRAILING);
        altitude_panel.add(fix_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.heading = new JTextField(5);
        altitude_panel.add(this.heading, cons);
        dialog_line++;

        return altitude_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel button_panel = new JPanel(layout);

        JButton set_button = new JButton("Set");
        set_button.setActionCommand(HeadingDialog.SET);
        set_button.addActionListener(this);

        JButton cancel_button = new JButton("Cancel");
        cancel_button.setActionCommand(HeadingDialog.CANCEL);
        cancel_button.addActionListener(this);

        button_panel.add(set_button);
        button_panel.add(cancel_button);

        return button_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(HeadingDialog.SET)) {
        	set_heading();
        } else if (event.getActionCommand().equals(HeadingDialog.CANCEL)) {
            // show_holding();
        }
    }


    private void set_heading() {
        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid heading",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // heading
            if ( this.heading.getText().equals("") ) {
                avionics.set_autopilot_hdg(0);
            } else {
            	avionics.set_autopilot_hdg(Integer.parseInt(this.heading.getText()));
            }
        }
    }


    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // heading 
        if ( ! this.heading.getText().equals("") ) {
            try {
                int hld_track = Integer.parseInt(this.heading.getText());
                if ((hld_track < 0) || (hld_track > 359)) {
                    field_validation_errors += "Heading out of range (0-359)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Heading contains non-numeric characters!\n";
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
