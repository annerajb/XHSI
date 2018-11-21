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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.xhsi.model.Avionics;


public class RadarGainTiltDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String SET = "Set";
    private static final String CANCEL = "Cancel";

    private JTextField tilt;
    private JTextField gain;

    private String field_validation_errors = null;

    private Avionics avionics;
    
    private static DecimalFormat gain_formatter;
    private static DecimalFormat tilt_formatter;

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public RadarGainTiltDialog(JFrame owner_frame, Avionics avionics) {
        super(owner_frame, "Weather radar gain and tilt");
        
        gain_formatter = new DecimalFormat("##0.0");
        DecimalFormatSymbols gain_symbols = gain_formatter.getDecimalFormatSymbols();
        gain_symbols.setDecimalSeparator('.');
        gain_formatter.setDecimalFormatSymbols(gain_symbols);

        tilt_formatter = new DecimalFormat("##0.0");
        DecimalFormatSymbols tilt_symbols = tilt_formatter.getDecimalFormatSymbols();
        tilt_symbols.setDecimalSeparator('.');
        tilt_formatter.setDecimalFormatSymbols(tilt_symbols);

        
        this.setResizable(false);

        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_altitude_panel(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);
        
        this.tilt.setText("");
        this.gain.setText("");
        pack();
        this.avionics=avionics;        		
    }

    
    public void init() {
        this.gain.setText(gain_formatter.format(avionics.wxr_gain()));
        this.tilt.setText(tilt_formatter.format(avionics.wxr_tilt()));
    }


    private JPanel create_altitude_panel() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel altitude_panel = new JPanel(layout);

        int dialog_line = 0;

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        // tilt
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel tilt_fix_label = new JLabel("New radar tilt", JLabel.TRAILING);
        altitude_panel.add(tilt_fix_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.tilt = new JTextField(5);
        altitude_panel.add(this.tilt, cons);
        dialog_line++;

        // gain
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        JLabel gain_fix_label = new JLabel("New radar gain", JLabel.TRAILING);
        altitude_panel.add(gain_fix_label, cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.gain = new JTextField(5);
        altitude_panel.add(this.gain, cons);
        dialog_line++;

        
        return altitude_panel;
    }


    private JPanel create_dialog_buttons_panel() {
        FlowLayout layout = new FlowLayout();
        JPanel button_panel = new JPanel(layout);

        JButton hide_button = new JButton("Set");
        hide_button.setActionCommand(RadarGainTiltDialog.SET);
        hide_button.addActionListener(this);

        JButton show_button = new JButton("Cancel");
        show_button.setActionCommand(RadarGainTiltDialog.CANCEL);
        show_button.addActionListener(this);

        button_panel.add(hide_button);
        button_panel.add(show_button);

        return button_panel;
    }


    public void actionPerformed(ActionEvent event) {
        this.setVisible(false);
        if (event.getActionCommand().equals(RadarGainTiltDialog.SET)) {
        	set_values();
        } else if (event.getActionCommand().equals(RadarGainTiltDialog.CANCEL)) {
            // show_holding();
        }
    }

    private void set_values() {
        if (fields_valid() == false) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid values",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            // gain 
            if ( this.gain.getText().equals("") ) {
                avionics.set_wxr_gain(0.5f);
                avionics.set_wxr_auto_gain(true);
            } else {
            	avionics.set_wxr_gain(Float.parseFloat(this.gain.getText()));
            }
            // tilt 
            if ( this.tilt.getText().equals("") ) {
                avionics.set_wxr_tilt(0.0f);
                avionics.set_wxr_auto_tilt(true);
            } else {
            	avionics.set_wxr_tilt(Float.parseFloat(this.tilt.getText()));
            }
        }
    }

    private boolean fields_valid() {
        this.field_validation_errors = new String();

        // gain 
        if ( ! this.gain.getText().equals("") ) {
            try {
                float gain_check = Float.parseFloat(this.gain.getText()); 
                if ((gain_check < -16) || (gain_check > 16)) {
                    field_validation_errors += "Gain out of range (-16dBz to +16dBz)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Gain contains non-numeric characters!\n";
            }
        }

        // tilt 
        if ( ! this.tilt.getText().equals("") ) {
            try {
            	float tilt_check = Float.parseFloat(this.tilt.getText());
                if ((tilt_check < -15) || (tilt_check > 15)) {
                    field_validation_errors += "Tilt out of range (-15° to +15°)!\n";
                }
            } catch (NumberFormatException nf) {
                field_validation_errors += "Tilt contains non-numeric characters!\n";
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
