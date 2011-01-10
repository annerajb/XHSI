/**
 * PreferencesDialog.java
 * 
 * Dialog for setting preferences of XHSI
 * 
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;


public class PreferencesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JFrame nd_ui;

    private JTextField aptnav_dir_textfield;
    private JTextField port_textfield;
    private JComboBox loglevel_combobox;
    private JCheckBox use_avionics_power_checkbox;
    private JCheckBox anti_alias_checkbox;
    private JComboBox operator_combobox;
    private String operators[] = { XHSIPreferences.PILOT, XHSIPreferences.COPILOT, XHSIPreferences.INSTRUCTOR };
    private JCheckBox start_ontop_checkbox;
    private JCheckBox hide_window_frame_checkbox;
    private JCheckBox panel_locked_checkbox;
    private JButton get_button;
    private JTextField panel_pos_x_textfield;
    private JTextField panel_pos_y_textfield;
    private JTextField panel_width_textfield;
    private JTextField panel_height_textfield;
    private JTextField panel_border_textfield;
    private JComboBox border_style_combobox;
    private String borders[] = { "fancy", "irongray", "black" };
    private JComboBox orientations_combobox;
    private String orientations[] = { XHSIPreferences.ND_UP, XHSIPreferences.ND_LEFT, XHSIPreferences.ND_RIGHT, XHSIPreferences.ND_UPSIDE_DOWN };
    private JCheckBox panel_square_checkbox;
    private JTextField min_rwy_textfield;
    private XHSIPreferences preferences;
    //private Level[] loglevels = new Level[] { Level.OFF, Level.SEVERE, Level.WARNING, Level.CONFIG, Level.INFO, Level.FINE, Level.FINEST };
    private Level[] loglevels = { Level.OFF, Level.SEVERE, Level.WARNING, Level.CONFIG, Level.INFO, Level.FINE, Level.FINEST };
    private JComboBox rwy_units_combobox;
    private String units[] = { "meters", "feet" };
    private JCheckBox draw_rwy_checkbox;
    private JCheckBox airbus_modes_checkbox;
    private JCheckBox draw_range_arcs_checkbox;
    private JCheckBox use_more_color_checkbox;
    private JCheckBox mode_mismatch_caution_checkbox;
    private JCheckBox tcas_always_on_checkbox;
    private JCheckBox classic_hsi_checkbox;
    private JCheckBox appvor_uncluttered_checkbox;
    private JCheckBox plan_aircraft_center_checkbox;
    private JCheckBox bold_fonts_checkbox;

    private int nd_pos_x, nd_pos_y, nd_width, nd_height;

    private String field_validation_errors = null;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public PreferencesDialog(JFrame owner_frame, JFrame instrument_frame) {

        super(owner_frame, "XHSI Preferences");

        this.nd_ui = instrument_frame;

        this.preferences = XHSIPreferences.get_instance();

        this.setResizable(false);
        
        Container content_pane = getContentPane();
        content_pane.setLayout(new BorderLayout());
        content_pane.add(create_preferences_tabs(), BorderLayout.CENTER);
        content_pane.add(create_dialog_buttons_panel(), BorderLayout.SOUTH);

        init_preferences();
        pack();

    }


    private void init_preferences() {

        this.aptnav_dir_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
        this.port_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PORT));

        for (int i=0; i<loglevels.length; i++) {
            if (logger.getLevel() == loglevels[i]) {
                this.loglevel_combobox.setSelectedIndex(i);
            }
        }

        this.use_avionics_power_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_USE_AVIONICS_POWER).equalsIgnoreCase("true"));

        this.anti_alias_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_ANTI_ALIAS).equalsIgnoreCase("true"));

        String instrument_position = preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION);
        for (int i=0; i<operators.length; i++) {
            if ( instrument_position.equalsIgnoreCase(operators[i]) ) {
                this.operator_combobox.setSelectedIndex(i);
            }
        }

        this.start_ontop_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_START_ONTOP).equalsIgnoreCase("true"));

        this.hide_window_frame_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAME).equalsIgnoreCase("true"));

        this.panel_locked_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PANEL_LOCKED).equalsIgnoreCase("true"));

        enable_lock_fields();

        this.panel_pos_x_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PANEL_POS_X));
        this.panel_pos_y_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PANEL_POS_Y));
        this.panel_width_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PANEL_WIDTH));
        this.panel_height_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PANEL_HEIGHT));
        this.panel_border_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_PANEL_BORDER));

        String pref_orientation = preferences.get_preference(XHSIPreferences.PREF_ND_ORIENTATION);
        for (int i=0; i<orientations.length; i++) {
            if ( pref_orientation.equals( orientations[i] ) ) {
                this.orientations_combobox.setSelectedIndex(i);
            }
        }

        String border_style = preferences.get_preference(XHSIPreferences.PREF_BORDER_STYLE);
        for (int i=0; i<borders.length; i++) {
            if ( border_style.equals( borders[i] ) ) {
                this.border_style_combobox.setSelectedIndex(i);
            }
        }

        this.panel_square_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PANEL_SQUARE).equalsIgnoreCase("true"));

        this.min_rwy_textfield.setText(preferences.get_preference(XHSIPreferences.PREF_MIN_RWY_LEN));

        String rwy_units = preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS);
        for (int i=0; i<units.length; i++) {
            if ( rwy_units.equals( units[i] ) ) {
                this.rwy_units_combobox.setSelectedIndex(i);
            }
        }

        this.airbus_modes_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_AIRBUS_MODES).equalsIgnoreCase("true"));

        this.draw_rwy_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_RUNWAYS).equalsIgnoreCase("true"));

        this.draw_range_arcs_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS).equalsIgnoreCase("true"));

        this.use_more_color_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_USE_MORE_COLOR).equalsIgnoreCase("true"));

        this.mode_mismatch_caution_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION).equalsIgnoreCase("true"));

        this.tcas_always_on_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON).equalsIgnoreCase("true"));

        this.classic_hsi_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_CLASSIC_HSI).equalsIgnoreCase("true"));

        this.appvor_uncluttered_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER).equalsIgnoreCase("true"));

        this.plan_aircraft_center_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER).equalsIgnoreCase("true"));

        this.bold_fonts_checkbox.setSelected(preferences.get_preference(XHSIPreferences.PREF_BOLD_FONTS).equalsIgnoreCase("true"));

    }


    private JTabbedPane create_preferences_tabs() {

        JTabbedPane tabs_panel = new JTabbedPane();
        tabs_panel.add( "SYSTEM", create_system_tab() );
        tabs_panel.add( "ND Window", create_window_tab() );
        tabs_panel.add( "ND Options", create_options_tab() );

        return tabs_panel;

    }


    private JPanel create_system_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel preferences_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5,10,0,0);

        int dialog_line = 0;

        // AptNav Resources directory
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("AptNav Resources directory", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.aptnav_dir_textfield = new JTextField(40);
        preferences_panel.add(this.aptnav_dir_textfield, cons);
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        JButton browse_button = new JButton("Browse");
        browse_button.setActionCommand("browse");
        browse_button.addActionListener(this);
        preferences_panel.add(browse_button, cons);
        dialog_line++;
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.gridwidth = 3;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("(can be X-Plane base directory, or directory where AptNav20yymmXP900.zip has been unzipped)", JLabel.TRAILING), cons);
        cons.gridwidth = 1;
        dialog_line++;

        // incoming UDP port
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Incoming UDP port (default 49020) (*)", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.port_textfield = new JTextField(5);
        preferences_panel.add(this.port_textfield, cons);
        dialog_line++;

        // some info concerning Incoming UDP port
        dialog_line++;
        cons.gridx = 2;
        cons.gridy = dialog_line;
        //cons.gridwidth = 3;
        cons.anchor = GridBagConstraints.WEST;
        preferences_panel.add(new JLabel("(must match XHSI_plugin's Destination UDP port)", JLabel.TRAILING), cons);
        cons.gridwidth = 1;
        dialog_line++;

        // Logging Level
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Logging Level", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
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
        dialog_line++;

        // Anti-alias
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Anti-aliasing", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.anti_alias_checkbox = new JCheckBox();
        preferences_panel.add(this.anti_alias_checkbox, cons);
        dialog_line++;

        // pilot/co-pilot/instructor
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Instrument position", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.operator_combobox = new JComboBox();
        this.operator_combobox.addItem("Pilot (standard X-Plane settings)");
        this.operator_combobox.addItem("Copilot (XHSI's extra settings)");
        this.operator_combobox.addItem("Instructor (independent settings)");
        this.operator_combobox.addActionListener(this);
        preferences_panel.add(this.operator_combobox, cons);
        dialog_line++;

        // A reminder
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        dialog_line++;

        return preferences_panel;

    }


    private JPanel create_window_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel preferences_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5,10,0,0);

        int dialog_line = 0;

        // Start with "Keep window on top"
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Set \"Window on top\" at startup", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.start_ontop_checkbox = new JCheckBox();
        preferences_panel.add(this.start_ontop_checkbox, cons);
        dialog_line++;

        // Draw window frame
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Hide window title bar and frame", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.hide_window_frame_checkbox = new JCheckBox("  (*)");
        preferences_panel.add(this.hide_window_frame_checkbox, cons);
        dialog_line++;

        // Lock the window's position and size
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Lock instrument window position and size", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_locked_checkbox = new JCheckBox("  (values below...)");
        this.panel_locked_checkbox.setActionCommand("locktoggle");
        this.panel_locked_checkbox.addActionListener(this);
        preferences_panel.add(this.panel_locked_checkbox, cons);
        dialog_line++;

        // Save current window's position and size
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Get current position and size", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        get_button = new JButton("Get");
        get_button.setActionCommand("getwindow");
        get_button.addActionListener(this);
        preferences_panel.add(get_button, cons);
        dialog_line++;

        // panel position x
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Horizontal window position", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_pos_x_textfield = new JTextField(4);
        preferences_panel.add(this.panel_pos_x_textfield, cons);
        dialog_line++;

        // panel position y
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Vertical window position", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_pos_y_textfield = new JTextField(4);
        preferences_panel.add(this.panel_pos_y_textfield, cons);
        dialog_line++;

        // panel width
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Window width", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_width_textfield = new JTextField(4);
        preferences_panel.add(this.panel_width_textfield, cons);
        dialog_line++;

        // panel height
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Window height", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_height_textfield = new JTextField(4);
        preferences_panel.add(this.panel_height_textfield, cons);
        dialog_line++;

        // panel border
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Border size", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_border_textfield = new JTextField(3);
        preferences_panel.add(this.panel_border_textfield, cons);
        dialog_line++;

        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Border style", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.border_style_combobox = new JComboBox();
        this.border_style_combobox.addItem("Double rounded");
        this.border_style_combobox.addItem("Iron gray");
        this.border_style_combobox.addItem("Black");
        this.border_style_combobox.addActionListener(this);
        preferences_panel.add(this.border_style_combobox, cons);
        dialog_line++;

        // orientation
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Orientation", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.orientations_combobox = new JComboBox();
        this.orientations_combobox.addItem(XHSIPreferences.ND_UP);
        this.orientations_combobox.addItem(XHSIPreferences.ND_LEFT);
        this.orientations_combobox.addItem(XHSIPreferences.ND_RIGHT);
        this.orientations_combobox.addItem(XHSIPreferences.ND_UPSIDE_DOWN);
        this.orientations_combobox.addActionListener(this);
        preferences_panel.add(this.orientations_combobox, cons);
        dialog_line++;

        // Draw square window
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Keep the instrument display square", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.panel_square_checkbox = new JCheckBox();
        preferences_panel.add(this.panel_square_checkbox, cons);
        dialog_line++;

        // A reminder
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        dialog_line++;

        return preferences_panel;

    }


    private JPanel create_options_tab() {

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints cons = new GridBagConstraints();
        JPanel preferences_panel = new JPanel(layout);

        cons.ipadx = 10;
        cons.ipady = 0;
        cons.insets = new Insets(5, 10, 0, 0);

        int dialog_line = 0;

        // Pseudo Airbus display modes
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Airbus display modes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.airbus_modes_checkbox = new JCheckBox("  ( ROSE_ILS / ROSE_VOR / ROSE_NAV / ARC / PLAN )");
        preferences_panel.add(this.airbus_modes_checkbox, cons);
        dialog_line++;

        // Display Centered APP and VOR as a classic HSI without moving map
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Display Centered APP and VOR as a classic HSI", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.classic_hsi_checkbox = new JCheckBox("  (as in a real B737-NG)");
        preferences_panel.add(this.classic_hsi_checkbox, cons);
        dialog_line++;

        // Keep moving map in APP and VOR modes uncluttered
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Display only tuned navaids in APP and VOR modes", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.appvor_uncluttered_checkbox = new JCheckBox("  (to keep the moving map in APP and VOR modes uncluttered)");
        preferences_panel.add(this.appvor_uncluttered_checkbox, cons);
        dialog_line++;

        // Center PLAN mode on waypoint
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Center PLAN mode on aircraft", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.plan_aircraft_center_checkbox = new JCheckBox();
        preferences_panel.add(this.plan_aircraft_center_checkbox, cons);
        dialog_line++;

        // Use avionics power
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Use X-Plane's avionics power", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.use_avionics_power_checkbox = new JCheckBox("  (display will be black when there is no avionics power)");
        preferences_panel.add(this.use_avionics_power_checkbox, cons);
        dialog_line++;

        // Draw range arcs
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Draw range arcs", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_range_arcs_checkbox = new JCheckBox();
        preferences_panel.add(this.draw_range_arcs_checkbox, cons);
        dialog_line++;

        // Draw runways at lowest map ranges
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Draw runways at lowest map ranges", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.draw_rwy_checkbox = new JCheckBox("  (memory hungry, start with \"java -Xms100m -Xmx150m -jar XHSI.jar\")  (*)");
        preferences_panel.add(this.draw_rwy_checkbox, cons);
        dialog_line++;

        // Minimum runway length
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Airport minimum runway length", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.min_rwy_textfield = new JTextField(4);
        preferences_panel.add(this.min_rwy_textfield, cons);
        dialog_line++;
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Runway length units", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.rwy_units_combobox = new JComboBox();
        this.rwy_units_combobox.addItem("meters");
        this.rwy_units_combobox.addItem("feet");
        this.rwy_units_combobox.addActionListener(this);
        preferences_panel.add(this.rwy_units_combobox, cons);
        dialog_line++;

        // Display app/vor frequency mismatch caution message
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Warn for EFIS MODE/NAV FREQ mismatch", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.mode_mismatch_caution_checkbox = new JCheckBox();
        preferences_panel.add(this.mode_mismatch_caution_checkbox, cons);
        dialog_line++;

        // Display app/vor frequency mismatch caution message
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("TCAS always ON", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.tcas_always_on_checkbox = new JCheckBox();
        preferences_panel.add(this.tcas_always_on_checkbox, cons);
        dialog_line++;

        // Use more color variations
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Use more color nuances", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.use_more_color_checkbox = new JCheckBox();
        preferences_panel.add(this.use_more_color_checkbox, cons);
        dialog_line++;

        // Bold fonts
        cons.gridx = 0;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("Bold fonts", JLabel.TRAILING), cons);
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.WEST;
        this.bold_fonts_checkbox = new JCheckBox();
        preferences_panel.add(this.bold_fonts_checkbox, cons);
        dialog_line++;

        // A reminder
        cons.gridx = 2;
        cons.gridy = dialog_line;
        cons.anchor = GridBagConstraints.EAST;
        preferences_panel.add(new JLabel("(*) : requires a restart", JLabel.TRAILING), cons);
        dialog_line++;

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

        if ( event.getActionCommand().equals("browse") ) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int ret = fc.showOpenDialog(this);

            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                this.aptnav_dir_textfield.setText(file.getAbsolutePath());
            }
        } else if ( event.getActionCommand().equals("locktoggle")) {
            enable_lock_fields();
        } else if ( event.getActionCommand().equals("getwindow")) {
            this.panel_pos_x_textfield.setText( "" + this.nd_ui.getX() );
            this.panel_pos_y_textfield.setText( "" + this.nd_ui.getY() );
            this.panel_width_textfield.setText( "" + this.nd_ui.getWidth() );
            this.panel_height_textfield.setText( "" + this.nd_ui.getHeight() );
        } else if ( event.getActionCommand().equals("cancel") ) {
            this.setVisible(false);
            init_preferences();
        } else if ( event.getActionCommand().equals("apply") ) {
            if ( set_preferences() ) {
                resize_nd();
            }
        } else if ( event.getActionCommand().equals("ok") ) {
            if ( set_preferences() ) {
                this.setVisible(false);
                resize_nd();
            }
        }

    }


    private void enable_lock_fields() {
        this.get_button.setEnabled( this.panel_locked_checkbox.isSelected() );
        this.panel_pos_x_textfield.setEnabled( this.panel_locked_checkbox.isSelected() );
        this.panel_pos_y_textfield.setEnabled( this.panel_locked_checkbox.isSelected() );
        this.panel_width_textfield.setEnabled( this.panel_locked_checkbox.isSelected() );
        this.panel_height_textfield.setEnabled( this.panel_locked_checkbox.isSelected() );
        this.panel_border_textfield.setEnabled( this.panel_locked_checkbox.isSelected() );
    }


    private void resize_nd() {

        if ( this.panel_locked_checkbox.isSelected() ) {
            this.nd_ui.setBounds(this.nd_pos_x, this.nd_pos_y, this.nd_width, this.nd_height);
        }

    }


    private boolean set_preferences() {

        boolean valid = fields_valid();
        if ( ! valid ) {
            JOptionPane.showMessageDialog(this,
                    this.field_validation_errors,
                    "Invalid Preferences",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            int loglevel_index = this.loglevel_combobox.getSelectedIndex();
            Level loglevel = this.loglevels[loglevel_index];
            logger.setLevel(loglevel);
            this.preferences.set_preference(XHSIPreferences.PREF_LOGLEVEL, loglevel.toString());

            if ( this.aptnav_dir_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_APTNAV_DIR, this.aptnav_dir_textfield.getText());

            if ( this.port_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_PORT)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_PORT, this.port_textfield.getText());

            if ( this.use_avionics_power_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_USE_AVIONICS_POWER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_USE_AVIONICS_POWER, this.use_avionics_power_checkbox.isSelected()?"true":"false");

            if ( this.anti_alias_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_ANTI_ALIAS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_ANTI_ALIAS, this.anti_alias_checkbox.isSelected()?"true":"false");

            if ( ! operators[this.operator_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION)) )
                this.preferences.set_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION, operators[this.operator_combobox.getSelectedIndex()]);

            if ( this.start_ontop_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_START_ONTOP).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_START_ONTOP, this.start_ontop_checkbox.isSelected()?"true":"false");

            if ( this.hide_window_frame_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAME).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_HIDE_WINDOW_FRAME, this.hide_window_frame_checkbox.isSelected()?"true":"false");

            if ( this.panel_locked_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PANEL_LOCKED).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PANEL_LOCKED , this.panel_locked_checkbox.isSelected()?"true":"false");

            if ( ! this.panel_pos_x_textfield.getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_PANEL_POS_X) ) )
                this.preferences.set_preference( XHSIPreferences.PREF_PANEL_POS_X , this.panel_pos_x_textfield.getText() );
            if ( ! this.panel_pos_y_textfield.getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_PANEL_POS_Y) ) )
                this.preferences.set_preference( XHSIPreferences.PREF_PANEL_POS_Y , this.panel_pos_y_textfield.getText() );
            if ( ! this.panel_width_textfield.getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_PANEL_WIDTH) ) )
                this.preferences.set_preference( XHSIPreferences.PREF_PANEL_WIDTH , this.panel_width_textfield.getText() );
            if ( ! this.panel_height_textfield.getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_PANEL_HEIGHT) ) )
                this.preferences.set_preference( XHSIPreferences.PREF_PANEL_HEIGHT , this.panel_height_textfield.getText() );
            if ( ! this.panel_border_textfield.getText().equals( this.preferences.get_preference(XHSIPreferences.PREF_PANEL_BORDER) ) )
                this.preferences.set_preference( XHSIPreferences.PREF_PANEL_BORDER , this.panel_border_textfield.getText() );
            
            if ( ! borders[this.border_style_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_BORDER_STYLE)) )
                this.preferences.set_preference(XHSIPreferences.PREF_BORDER_STYLE, borders[this.border_style_combobox.getSelectedIndex()]);

            if ( ! orientations[this.orientations_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_ND_ORIENTATION)) )
                this.preferences.set_preference(XHSIPreferences.PREF_ND_ORIENTATION, orientations[this.orientations_combobox.getSelectedIndex()]);

            if ( this.panel_square_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PANEL_SQUARE).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PANEL_SQUARE, this.panel_square_checkbox.isSelected()?"true":"false");

            if ( this.min_rwy_textfield.getText().equals(this.preferences.get_preference(XHSIPreferences.PREF_MIN_RWY_LEN)) == false )
                this.preferences.set_preference(XHSIPreferences.PREF_MIN_RWY_LEN, this.min_rwy_textfield.getText());

            if ( ! units[this.rwy_units_combobox.getSelectedIndex()].equals(this.preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS)) )
                this.preferences.set_preference(XHSIPreferences.PREF_RWY_LEN_UNITS, units[this.rwy_units_combobox.getSelectedIndex()]);

            if ( this.draw_rwy_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_RUNWAYS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_RUNWAYS, this.draw_rwy_checkbox.isSelected()?"true":"false");

            if ( this.airbus_modes_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_AIRBUS_MODES).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_AIRBUS_MODES, this.airbus_modes_checkbox.isSelected()?"true":"false");

            if ( this.draw_range_arcs_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_DRAW_RANGE_ARCS, this.draw_range_arcs_checkbox.isSelected()?"true":"false");

            if ( this.use_more_color_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_USE_MORE_COLOR).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_USE_MORE_COLOR, this.use_more_color_checkbox.isSelected()?"true":"false");

            if ( this.mode_mismatch_caution_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_MODE_MISMATCH_CAUTION, this.mode_mismatch_caution_checkbox.isSelected()?"true":"false");

            if ( this.tcas_always_on_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_TCAS_ALWAYS_ON, this.tcas_always_on_checkbox.isSelected()?"true":"false");

            if ( this.classic_hsi_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_CLASSIC_HSI).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_CLASSIC_HSI, this.classic_hsi_checkbox.isSelected()?"true":"false");

            if ( this.appvor_uncluttered_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_APPVOR_UNCLUTTER, this.appvor_uncluttered_checkbox.isSelected()?"true":"false");

            if ( this.plan_aircraft_center_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_PLAN_AIRCRAFT_CENTER, this.plan_aircraft_center_checkbox.isSelected()?"true":"false");

            if ( this.bold_fonts_checkbox.isSelected() != this.preferences.get_preference(XHSIPreferences.PREF_BOLD_FONTS).equals("true") )
                this.preferences.set_preference(XHSIPreferences.PREF_BOLD_FONTS, this.bold_fonts_checkbox.isSelected()?"true":"false");

        }

        return valid;

    }


    private boolean fields_valid() {

        this.field_validation_errors = new String();

        // Incoming port
        int port;
        try {
            port = Integer.parseInt(this.port_textfield.getText());
            if ((port < 1024) || (port > 65535)) {
                field_validation_errors +="Port out of range (1024-65535)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Port contains non-numeric characters!\n";
        }

        // minimum runway length
        int min_rwy;
        try {
            min_rwy = Integer.parseInt(this.min_rwy_textfield.getText());
            if ((min_rwy < 0) || (min_rwy > 9999)) {
                field_validation_errors +="Minimum Runway Length out of range (0-9999)!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Minimum Runway Length contains non-numeric characters!\n";
        }

        // Window horizontal position
        try {
            this.nd_pos_x = Integer.parseInt(this.panel_pos_x_textfield.getText());
            if ((this.nd_pos_x < -9999) || (this.nd_pos_x > 9999)) {
                field_validation_errors +="Window horizontal position out of range!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Window horizontal position contains non-numeric characters!\n";
        }
        // Window vertical position
        try {
            this.nd_pos_y = Integer.parseInt(this.panel_pos_y_textfield.getText());
            if ((this.nd_pos_y < -9999) || (this.nd_pos_y > 9999)) {
                field_validation_errors +="Window horizontal position out of range!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Window horizontal position contains non-numeric characters!\n";
        }
        // Window width
        try {
            this.nd_width = Integer.parseInt(this.panel_width_textfield.getText());
            if ((this.nd_width < 500) || (this.nd_width > 2499)) {
                field_validation_errors +="Window width out of range!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Window width contains non-numeric characters!\n";
        }
        // Window height
        try {
            this.nd_height = Integer.parseInt(this.panel_height_textfield.getText());
            if ((this.nd_height < 500) || (this.nd_height > 1999)) {
                field_validation_errors +="Window height out of range!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Window height contains non-numeric characters!\n";
        }
        // Border width
        int border;
        try {
            border = Integer.parseInt(this.panel_border_textfield.getText());
            if ( (border < 0) || (border > 399) || ((this.nd_width - 2*border) < 480) || ((this.nd_height - 2*border) < 480) ) {
                field_validation_errors +="Instrument border out of range!\n";
            }
        } catch (NumberFormatException nf) {
            field_validation_errors += "Window height contains non-numeric characters!\n";
        }

        if (field_validation_errors.equals("") == false) {
            field_validation_errors = field_validation_errors.trim();
            return false;
        } else {
            return true;
        }

    }


}
