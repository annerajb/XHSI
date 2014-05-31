/**
 * MaxTRQDialog.java
 * 
 * Dialog for setting a value for Max TRQ
 * 
 * Copyright (C) 2014  Marc Rogiers (marrog.123@gmail.com)
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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
//import javax.swing.JSlider;
import javax.swing.JTextField;


public class MaxTRQDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SET = "Set";
    private static final String CANCEL = "Cancel";

    private JTextField max_trq_value;

    private String field_validation_errors = null;

    private XHSISettings xhsi_settings = XHSISettings.get_instance();


    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public MaxTRQDialog(JFrame owner_frame) {
        super(owner_frame, "Max TRQ");

        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(max_trq_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_max_trq();
        pack();
    }


    private void init_max_trq() {

        if (xhsi_settings.avionics.get_aircraft().get_max_TRQ_override() == 0.0f) {
            this.max_trq_value.setText("");
        } else {
            this.max_trq_value.setText("" + xhsi_settings.avionics.get_aircraft().get_max_TRQ_override());
        }

    }


    private JPanel max_trq_panel() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel max_trq_panel = new JPanel(layout);

        int dialog_line = 0;

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // Max TRQ
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel track_label = new JLabel("Maximum TRQ (LbFt)", JLabel.TRAILING);
        max_trq_panel.add(track_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.max_trq_value = new JTextField(3);
        max_trq_panel.add(this.max_trq_value, cons);
        dialog_line++;

        return max_trq_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel buttons_panel = new JPanel(layout);

        JButton cancel_button = new JButton("Cancel");
        cancel_button.setActionCommand(MaxTRQDialog.CANCEL);
        cancel_button.addActionListener(this);
        buttons_panel.add(cancel_button);

        JButton set_button = new JButton("Set");
        set_button.setActionCommand(MaxTRQDialog.SET);
        set_button.addActionListener(this);
        buttons_panel.add(set_button);

        return buttons_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(MaxTRQDialog.SET)) {
            set_max_trq();
        } else if (event.getActionCommand().equals(MaxTRQDialog.CANCEL)) {
            cancel();
        }
    }


    private void cancel() {
        // too easy ...
    }


    private void set_max_trq() {
        if ( ! fields_valid() ) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid value",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            xhsi_settings.avionics.set_max_trq_override(Float.parseFloat(this.max_trq_value.getText()));
        }
    }


    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // Max TRQ
        if ( ! this.max_trq_value.getText().equals("") ) {
            try {
                float the_value = Float.parseFloat(this.max_trq_value.getText());
                if ((the_value < 0.0f) || (the_value > 9999.9f)) {
                    field_validation_errors += "Max TRQ out of range!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Max TRQ contains non-numeric characters!\n";
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
