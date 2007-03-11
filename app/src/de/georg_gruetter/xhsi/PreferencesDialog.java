/**
 * PreferencesDialog.java
 * 
 * Dialog for setting preferences of XHSI
 * 
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PreferencesDialog extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JTextField xplane_dir_textfield;
	private JTextField port_textfield;
	private JComboBox loglevel_combobox;
	private HSIPreferences preferences;
	private Level[] loglevels = new Level[] { Level.OFF, Level.SEVERE, Level.WARNING, Level.CONFIG, Level.INFO, Level.FINE, Level.FINEST };
	
	private String field_validation_errors = null;
	
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	public PreferencesDialog() {
		super("XHSI Preferences");
		
		this.preferences = HSIPreferences.get_instance();
		
		setResizable(false);
	    Container content_pane = getContentPane();
	    content_pane.setLayout(new BorderLayout());
		content_pane.add(create_preferences_panel(), BorderLayout.CENTER);
		content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);
		
		init_preferences();
		pack();
	}
	
	private void init_preferences() {
		this.xplane_dir_textfield.setText(preferences.get_preference(HSIPreferences.PREF_XPLANE_DIR));
		this.port_textfield.setText(preferences.get_preference(HSIPreferences.PREF_PORT));
		
		for (int i=0;i<loglevels.length;i++) {
			if (logger.getLevel() == loglevels[i]) {
				this.loglevel_combobox.setSelectedIndex(i);
				break;
			}
		}
	}
	
	private JPanel create_preferences_panel() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints cons = new GridBagConstraints();
		JPanel preferences_panel = new JPanel(layout);

		cons.ipadx = 0;
		cons.ipady = 0;
		cons.insets = new Insets(5,10,0,0);
		
		cons.gridx = 0;
		cons.gridy = 0;
		cons.anchor = GridBagConstraints.EAST;
		preferences_panel.add(new JLabel("X-Plane home directory", JLabel.TRAILING),cons);

		cons.gridx = 2;
		cons.anchor = GridBagConstraints.WEST;
		this.xplane_dir_textfield = new JTextField(25);
		preferences_panel.add(this.xplane_dir_textfield, cons);

		cons.gridx = 3;
		JButton browse_button = new JButton("Browse");
		browse_button.setActionCommand("browse");
		browse_button.addActionListener(this);
		preferences_panel.add(browse_button);
		
		cons.gridx = 0;
		cons.gridy = 1;
		cons.anchor = GridBagConstraints.EAST;
		JLabel port_label = new JLabel("Port (0-65535)", JLabel.TRAILING);
		preferences_panel.add(port_label, cons);

		cons.gridx = 2;
		cons.gridy = 1;
		cons.anchor = GridBagConstraints.WEST;		
		this.port_textfield = new JTextField(5);
		preferences_panel.add(this.port_textfield, cons);
		
		cons.gridx = 0;
		cons.gridy = 2;
		cons.anchor = GridBagConstraints.EAST;
		preferences_panel.add(new JLabel("Logging Level", JLabel.TRAILING), cons);

		cons.gridx = 2;
		cons.gridy = 2;
		cons.anchor = GridBagConstraints.WEST;		
		this.loglevel_combobox = new JComboBox();
		this.loglevel_combobox.addItem("Off");
		this.loglevel_combobox.addItem("Severe");
		this.loglevel_combobox.addItem("Warning");
		this.loglevel_combobox.addItem("Configuration");
		this.loglevel_combobox.addItem("Info");
		this.loglevel_combobox.addItem("Fine");
		this.loglevel_combobox.addItem("Finest");
		this.loglevel_combobox.addActionListener(this);
		preferences_panel.add(this.loglevel_combobox, cons);

		return preferences_panel;	
	}
	
	private JPanel create_dialog_buttons_panel() {
		FlowLayout layout = new FlowLayout();
		JPanel preferences_panel = new JPanel(layout);

		JButton cancel_button = new JButton("Cancel");
		cancel_button.setActionCommand("cancel");
		cancel_button.addActionListener(this);
				
		JButton apply_button = new JButton("Apply");
		apply_button.setActionCommand("apply");
		apply_button.addActionListener(this);

		JButton ok_button = new JButton("OK");
		ok_button.setActionCommand("ok");
		ok_button.addActionListener(this);
		
		preferences_panel.add(cancel_button);
		preferences_panel.add(apply_button);
		preferences_panel.add(ok_button);
		
		return preferences_panel;
	}

	public void actionPerformed(ActionEvent event) {
		if ("browse".equals(event.getActionCommand())) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ret = fc.showOpenDialog(this);
			
			if (ret == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				this.xplane_dir_textfield.setText(file.getAbsolutePath());
			}			
		} else if ("cancel".equals(event.getActionCommand())) {
			this.setVisible(false);
			init_preferences();
		} else if ("apply".equals(event.getActionCommand())) {
			set_preferences();
		} else if ("ok".equals(event.getActionCommand())) {
			this.setVisible(false);
			set_preferences();
		}
	}
	
	private void set_preferences() {
		if (fields_valid() == false) {
			JOptionPane.showMessageDialog(this,
				    this.field_validation_errors,
				    "Invalid Preferences",
				    JOptionPane.ERROR_MESSAGE);			
		} else {
			int loglevel_index = this.loglevel_combobox.getSelectedIndex();
			Level loglevel = this.loglevels[loglevel_index];
			logger.setLevel(loglevel);
			this.preferences.set_preference(HSIPreferences.PREF_LOGLEVEL, loglevel.toString());

			if (this.xplane_dir_textfield.getText().equals(this.preferences.get_preference(HSIPreferences.PREF_XPLANE_DIR)) == false) 
				this.preferences.set_preference(HSIPreferences.PREF_XPLANE_DIR, this.xplane_dir_textfield.getText());
			
			if (this.port_textfield.getText().equals(this.preferences.get_preference(HSIPreferences.PREF_PORT)) == false) 
				this.preferences.set_preference(HSIPreferences.PREF_PORT, this.port_textfield.getText());
		}
	}
	
	private boolean fields_valid() {
		this.field_validation_errors = new String();
		
		// Port
		int port;
		try {
			port = Integer.parseInt(this.port_textfield.getText());
			if ((port < 0) || (port > 65535)) {
				field_validation_errors +="Port outside of range (0-65535)!\n";
			} 
		} catch (NumberFormatException nf) {
			field_validation_errors += "Port contains non-numeric characters!\n"; 
		}
		
		if (field_validation_errors.equals("") == false) {
			field_validation_errors = field_validation_errors.trim();
			return false;
		} else {
			return true;
		}
	}	
}
