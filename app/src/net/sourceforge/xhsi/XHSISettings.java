/**
* XHSISettings.java
* 
* Add HSI settings options to the menu bar, handle the commands that the
* menu selections generate and keep static variables with the settings
* 
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

//import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import net.sourceforge.xhsi.model.Avionics;


/**
* Add EFIS settings options to the menu bar, handle the commands that the
* menu selections generate and keep static variables with the settings
*/
public class XHSISettings implements ActionListener, PreferencesObserver {

    //private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Avionics avionics;

    private JFrame main_frame;
    private JFrame nd_frame;

    // menu item commands must be unique...

    public static final String ACTION_AVIONICS_POWER = "Power";

    public static final String ACTION_SOURCE_NAV1 = "NAV 1";
    public static final String ACTION_SOURCE_NAV2 = "NAV 2";
    public static final String ACTION_SOURCE_FMC = "FMC";

    public static final String ACTION_SUBMODE_APP = "Mode APP";
    public static final String ACTION_SUBMODE_VOR = "Mode VOR";
    public static final String ACTION_SUBMODE_MAP = "Mode MAP";
    public static final String ACTION_SUBMODE_NAV = "Mode NAV";
    public static final String ACTION_SUBMODE_PLN = "Mode PLN";
    public static final String ACTION_MODE_CENTERED = "CTR";

    public static final String LABEL_SUBMODE_APP = "  APP";
    public static final String LABEL_SUBMODE_VOR = "  VOR";
    public static final String LABEL_SUBMODE_MAP = "  MAP";
    public static final String LABEL_SUBMODE_NAV = "  NAV";
    public static final String LABEL_SUBMODE_PLN = "  PLN";
    public static final String LABEL_MODE_CENTERED = "  CTR";

    public static final String LABEL_SUBMODE_ROSE_ILS = "ROSE ILS";
    public static final String LABEL_SUBMODE_ROSE_VOR = "ROSE VOR";
    public static final String LABEL_SUBMODE_ROSE_NAV = "ROSE NAV";
    public static final String LABEL_SUBMODE_ARC = "     ARC";
    public static final String LABEL_SUBMODE_PLAN = "    PLAN";

    public static final String ACTION_RADIO1_ADF1 = "ADF1";
    public static final String ACTION_RADIO1_OFF = "Radio1 off";
    public static final String LABEL_RADIO1_OFF = "Off";
    public static final String ACTION_RADIO1_NAV1 = "NAV1";

    public static final String ACTION_RADIO1_DME_ARC = "Draw DME1 arc ...";

    public static final String ACTION_SYNC_OBS1 = "Set OBS1 to LOC1 course";

    public static final String ACTION_RADIO2_ADF2 = "ADF2";
    public static final String ACTION_RADIO2_OFF = "Radio2 off";
    public static final String LABEL_RADIO2_OFF = "Off";
    public static final String ACTION_RADIO2_NAV2 = "NAV2";

    public static final String ACTION_RADIO2_DME_ARC = "Draw DME2 arc ...";

    public static final String ACTION_SYNC_OBS2 = "Set OBS2 to LOC2 course";

    public static final String ACTION_SYMBOLS_SHOW_ARPT = "ARPT";
    public static final String ACTION_SYMBOLS_SHOW_WPT = "WPT";
    public static final String ACTION_SYMBOLS_SHOW_VOR = "VOR";
    public static final String ACTION_SYMBOLS_SHOW_NDB = "NDB";
    public static final String ACTION_SYMBOLS_SHOW_TFC = "TFC";
    public static final String ACTION_SYMBOLS_SHOW_POS = "POS";
    public static final String ACTION_SYMBOLS_SHOW_DATA = "DATA";

    public static final String ACTION_XPDR_OFF = "OFF";
    public static final String ACTION_XPDR_STBY = "STBY";
    public static final String ACTION_XPDR_ON = "ON";
    public static final String ACTION_XPDR_TA = "TA";
    public static final String ACTION_XPDR_TARA = "TA/RA";

    public static final String ACTION_HOLDING_HIDE = "Hide";
    public static final String ACTION_HOLDING_SHOW = "Define ...";

    private String range_list[] = { "10", "20", "40", "80", "160", "320", "640" };

    public boolean avionics_power = true;
    public int source = Avionics.HSI_SOURCE_NAV1;
    public int radio1 = Avionics.EFIS_RADIO_NAV;
    public float dme1_radius = 0.0f;
    public int radio2 = Avionics.EFIS_RADIO_NAV;
    public float dme2_radius = 0.0f;
    public int map_mode = Avionics.EFIS_MAP_MAP;
    public int map_centered = Avionics.EFIS_MAP_EXPANDED;
    public int range = 40;
    public boolean show_arpt = true;
    public boolean show_wpt = true;
    public boolean show_vor = true;
    public boolean show_ndb = true;
    public boolean show_tfc = true;
    public boolean show_data = true;
    public boolean show_pos = true;
    public boolean draw_holding = false;
    public String holding_fix = "";
    public int holding_track = 0;
    public float holding_legduration = 1.0f;
    public boolean holding_nonstandard = false;
    //public int holding_radial = 0;
    //public float holding_distance = 0.0f;
    public int xpdr = Avionics.XPDR_TA;


//    private JCheckBoxMenuItem checkbox_avionics_power;

    private JRadioButtonMenuItem radio_button_source_nav1;
    private JRadioButtonMenuItem radio_button_source_nav2;
    private JRadioButtonMenuItem radio_button_source_fmc;

    private JRadioButtonMenuItem radio_button_radio1_adf;
    private JRadioButtonMenuItem radio_button_radio1_off;
    private JRadioButtonMenuItem radio_button_radio1_nav;

    private JRadioButtonMenuItem radio_button_radio2_adf;
    private JRadioButtonMenuItem radio_button_radio2_off;
    private JRadioButtonMenuItem radio_button_radio2_nav;

    private JRadioButtonMenuItem radio_button_submode_app;
    private JRadioButtonMenuItem radio_button_submode_vor;
    private JRadioButtonMenuItem radio_button_submode_map;
    private JRadioButtonMenuItem radio_button_submode_nav;
    private JRadioButtonMenuItem radio_button_submode_pln;
    private JCheckBoxMenuItem checkbox_mode_centered;

    private JRadioButtonMenuItem[] radio_button_range = new JRadioButtonMenuItem[7];

    private JCheckBoxMenuItem checkbox_symbols_show_arpt;
    private JCheckBoxMenuItem checkbox_symbols_show_wpt;
    private JCheckBoxMenuItem checkbox_symbols_show_vor;
    private JCheckBoxMenuItem checkbox_symbols_show_ndb;
    private JCheckBoxMenuItem checkbox_symbols_show_tfc;
    private JCheckBoxMenuItem checkbox_symbols_show_pos;
    private JCheckBoxMenuItem checkbox_symbols_show_data;

    private JRadioButtonMenuItem radio_button_xpdr_off;
    private JRadioButtonMenuItem radio_button_xpdr_stby;
    private JRadioButtonMenuItem radio_button_xpdr_on;
    private JRadioButtonMenuItem radio_button_xpdr_ta;
    private JRadioButtonMenuItem radio_button_xpdr_tara;

    public JRadioButtonMenuItem radiobutton_holding_hide;
    public JRadioButtonMenuItem radiobutton_holding_show;

    public JCheckBoxMenuItem checkbox_dme1_arc;
    public JCheckBoxMenuItem checkbox_dme2_arc;

    private DMEArcDialog dme1_radius_dialog;
    private DMEArcDialog dme2_radius_dialog;
    private HoldingDialog holding_dialog;


    private static XHSISettings single_instance = null;


    public static XHSISettings get_instance() {
        if (XHSISettings.single_instance == null) {
            XHSISettings.single_instance = new XHSISettings();
        }
        return XHSISettings.single_instance;
    }


    public void init_frames(JFrame xhsi_main_frame, JFrame xhsi_nd_frame) {

        this.main_frame = xhsi_main_frame;
        this.nd_frame = xhsi_nd_frame;
        this.dme1_radius_dialog = new DMEArcDialog(xhsi_main_frame, 1);
        this.dme2_radius_dialog = new DMEArcDialog(xhsi_main_frame, 2);
        this.holding_dialog = new HoldingDialog(xhsi_main_frame);

    }


    public void create_menu(JMenuBar menu_bar) {

        JMenuItem menu_item;
        JCheckBoxMenuItem checkbox_menu_item;
        JRadioButtonMenuItem radio_button_menu_item;

        // Hmmm... code re-use? This looks more like code multiplication...

//        // define the "Avionics" menu
//        JMenu xhsi_avionics_menu = new JMenu("Avionics");
//        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_AVIONICS_POWER);
//        checkbox_menu_item.addActionListener(this);
//        checkbox_menu_item.setSelected(true);
//        xhsi_avionics_menu.add(checkbox_menu_item);
//        // keep a reference
//        this.checkbox_avionics_power = checkbox_menu_item;
//
//        // add the "Avionics" menu to the menubar
//        menu_bar.add(xhsi_avionics_menu);


        // define the "Transponder" menu
        JMenu xhsi_xpdr_menu = new JMenu("Transponder");

        ButtonGroup xpdr_group = new ButtonGroup();

        // define the menu items, and add them to the "Transponder" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_XPDR_OFF);
        radio_button_menu_item.setToolTipText("Transponder powered off");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        xpdr_group.add(radio_button_menu_item);
        xhsi_xpdr_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_xpdr_off = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_XPDR_STBY);
        radio_button_menu_item.setToolTipText("Transponder powered on, but not functioning");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        xpdr_group.add(radio_button_menu_item);
        xhsi_xpdr_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_xpdr_stby = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_XPDR_ON);
        radio_button_menu_item.setToolTipText("Transponder on");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        xpdr_group.add(radio_button_menu_item);
        xhsi_xpdr_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_xpdr_on = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_XPDR_TA);
        radio_button_menu_item.setToolTipText("Transponder on; TCAS will issue Traffic Alerts");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        xpdr_group.add(radio_button_menu_item);
        xhsi_xpdr_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_xpdr_ta = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_XPDR_TARA);
        radio_button_menu_item.setToolTipText("Transponder on; TCAS will issue Traffic Alerts (Resolution Alerts inoperative)");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        xpdr_group.add(radio_button_menu_item);
        xhsi_xpdr_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_xpdr_tara = radio_button_menu_item;

        // add the "Transponder" menu to the menubar
        menu_bar.add(xhsi_xpdr_menu);


        // define the "Source" menu
        JMenu xhsi_source_menu = new JMenu("Source");

        ButtonGroup source_group = new ButtonGroup();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_SOURCE_NAV1);
        radio_button_menu_item.setToolTipText("HSI follows NAV1");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        source_group.add(radio_button_menu_item);
        xhsi_source_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_source_nav1 = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_SOURCE_NAV2);
        radio_button_menu_item.setToolTipText("HSI follows NAV2");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        source_group.add(radio_button_menu_item);
        xhsi_source_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_source_nav2 = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_SOURCE_FMC);
        radio_button_menu_item.setToolTipText("HSI follows GPS/FMS/FMC");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        source_group.add(radio_button_menu_item);
        xhsi_source_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_source_fmc = radio_button_menu_item;

        // add the "Source" menu to the menubar
        menu_bar.add(xhsi_source_menu);


        // define the "Radio1" menu
        JMenu xhsi_radio1_menu;

        ButtonGroup radio1_group = new ButtonGroup();

        // define the menu items, and add them to the "Radio1" menu
        xhsi_radio1_menu = new JMenu("Radio1");

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO1_ADF1);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for ADF1");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio1_group.add(radio_button_menu_item);
        xhsi_radio1_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio1_adf = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.LABEL_RADIO1_OFF);
        radio_button_menu_item.setToolTipText("Don't display bearing lines or pointer arrows for Radio1");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_RADIO1_OFF);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio1_group.add(radio_button_menu_item);
        xhsi_radio1_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio1_off = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO1_NAV1);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for NAV1");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        radio1_group.add(radio_button_menu_item);
        xhsi_radio1_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio1_nav = radio_button_menu_item;

        xhsi_radio1_menu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_RADIO1_DME_ARC);
        checkbox_menu_item.setToolTipText("Draw a circle around NAV1's DME");
        checkbox_menu_item.addActionListener(this);
        xhsi_radio1_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way from DMEArcDialog.java
        checkbox_dme1_arc = checkbox_menu_item;

        xhsi_radio1_menu.addSeparator();

        menu_item = new JMenuItem(XHSISettings.ACTION_SYNC_OBS1);
        menu_item.setToolTipText("Set NAV1's OBS to the Localizer frontcourse");
        menu_item.addActionListener(this);
        xhsi_radio1_menu.add(menu_item);

        // add the "Radio1" menu to the menubar
        menu_bar.add(xhsi_radio1_menu);


        // define the "Radio2" menu
        JMenu xhsi_radio2_menu;

        ButtonGroup radio2_group = new ButtonGroup();

        // define the menu items, and add them to the "Radio2" menu
        xhsi_radio2_menu = new JMenu("Radio2");

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO2_ADF2);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for ADF2");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio2_group.add(radio_button_menu_item);
        xhsi_radio2_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio2_adf = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.LABEL_RADIO2_OFF);
        radio_button_menu_item.setToolTipText("Don't display bearing lines or pointer arrows for Radio2");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_RADIO2_OFF);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio2_group.add(radio_button_menu_item);
        xhsi_radio2_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio2_off = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO2_NAV2);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for NAV2");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        radio2_group.add(radio_button_menu_item);
        xhsi_radio2_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio2_nav = radio_button_menu_item;

        xhsi_radio2_menu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_RADIO2_DME_ARC);
        checkbox_menu_item.setToolTipText("Draw a circle around NAV2's DME");
        checkbox_menu_item.addActionListener(this);
        xhsi_radio2_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way from DMEArcDialog.java
        checkbox_dme2_arc = checkbox_menu_item;

        xhsi_radio2_menu.addSeparator();

        menu_item = new JMenuItem(XHSISettings.ACTION_SYNC_OBS2);
        menu_item.setToolTipText("Set NAV2's OBS to the Localizer frontcourse");
        menu_item.addActionListener(this);
        xhsi_radio2_menu.add(menu_item);

        // add the "Radio2" menu to the menubar
        menu_bar.add(xhsi_radio2_menu);


        // define the "Mode" menu
        JMenu xhsi_mode_menu = new JMenu("Mode");

        boolean airbus_modes = XHSIPreferences.get_instance().get_airbus_modes();

        ButtonGroup submode_group = new ButtonGroup();

        // define the menu items, and add them to the "Mode" menu
        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_ILS : XHSISettings.LABEL_SUBMODE_APP);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_APP + " mode");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_APP);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        xhsi_mode_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_app = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_VOR : XHSISettings.LABEL_SUBMODE_VOR);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_VOR + " mode");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_VOR);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        xhsi_mode_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_vor = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_NAV : XHSISettings.LABEL_SUBMODE_MAP);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_MAP + " mode");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_MAP);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        submode_group.add(radio_button_menu_item);
        xhsi_mode_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_map = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ARC : XHSISettings.LABEL_SUBMODE_NAV);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_NAV + " mode");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_NAV);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        xhsi_mode_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_nav = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_PLAN : XHSISettings.LABEL_SUBMODE_PLN);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_PLN + " mode");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_PLN);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        xhsi_mode_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_pln = radio_button_menu_item;

        xhsi_mode_menu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.LABEL_MODE_CENTERED);
        checkbox_menu_item.setToolTipText("Centered, or Expanded");
        checkbox_menu_item.setActionCommand(XHSISettings.ACTION_MODE_CENTERED);
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setMnemonic(KeyEvent.VK_C);
        checkbox_menu_item.setSelected(false);
        checkbox_menu_item.setVisible( ! airbus_modes );
        xhsi_mode_menu.add(checkbox_menu_item);
        // keep a reference
        this.checkbox_mode_centered = checkbox_menu_item;

        // add the "Mode" menu to the menubar
        menu_bar.add(xhsi_mode_menu);


        // define the "Range" menu
        JMenu xhsi_range_menu = new JMenu("Range");

        ButtonGroup range_group = new ButtonGroup();

        for (int i=0; i<this.range_list.length; i++) {
            radio_button_menu_item = new JRadioButtonMenuItem( this.range_list[i] );
            radio_button_menu_item.setToolTipText(this.range_list[i] + " NM");
            radio_button_menu_item.addActionListener(this);
            if ( i==2 )
                radio_button_menu_item.setSelected(true);
            else
                radio_button_menu_item.setSelected(false);
            range_group.add(radio_button_menu_item);
            xhsi_range_menu.add(radio_button_menu_item);
            radio_button_range[i] = radio_button_menu_item;
        }

        // add the "Range" menu to the menubar
        menu_bar.add(xhsi_range_menu);


        // define the "Symbols" menu
        JMenu xhsi_symbols_menu = new JMenu("Symbols");

        // define the menu items, and add them to the "Symbols" menu
        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_ARPT);
        checkbox_menu_item.setToolTipText("Show Airports");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_arpt = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_WPT);
        checkbox_menu_item.setToolTipText("Shox Fixes");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_wpt = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_VOR);
        checkbox_menu_item.setToolTipText("Show VORs");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_vor = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_NDB);
        checkbox_menu_item.setToolTipText("Show NDBs");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_ndb = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_TFC);
        checkbox_menu_item.setToolTipText("Show Traffic");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_tfc = checkbox_menu_item;

        xhsi_symbols_menu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_POS);
        checkbox_menu_item.setToolTipText("Draw bearing lines, or pointer arrows");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_pos = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_DATA);
        checkbox_menu_item.setToolTipText("Show altitudes for FMS waypoints");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        xhsi_symbols_menu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_data = checkbox_menu_item;

        // add the "Symbols" menu to the menubar
        menu_bar.add(xhsi_symbols_menu);

        // define the "Holding" menu
        JMenu hsi_holding_menu = new JMenu("Holding");

        ButtonGroup holding_group = new ButtonGroup();

        // define the menu items, and add them to the "Holding" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_HOLDING_HIDE);
        radio_button_menu_item.setToolTipText("Hide the holding");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        hsi_holding_menu.add(radio_button_menu_item);
        holding_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from HoldingDialog.java
        radiobutton_holding_hide = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_HOLDING_SHOW);
        radio_button_menu_item.setToolTipText("Draw a holding");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        hsi_holding_menu.add(radio_button_menu_item);
        holding_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from HoldingDialog.java
        radiobutton_holding_show = radio_button_menu_item;

        // add the "Holding" menu to the menubar
        menu_bar.add(hsi_holding_menu);

    }


    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

//        if (command.equals(XHSISettings.ACTION_AVIONICS_POWER)) {
//            avionics_power = this.checkbox_avionics_power.isSelected();
//            this.avionics.set_power(avionics_power);
//        } else
        if (command.equals(XHSISettings.ACTION_SOURCE_NAV1)) {
            source = Avionics.HSI_SOURCE_NAV1;
            this.avionics.set_hsi_source(source);
        } else if (command.equals(XHSISettings.ACTION_SOURCE_NAV2)) {
            source = Avionics.HSI_SOURCE_NAV2;
            this.avionics.set_hsi_source(source);
        } else if (command.equals(XHSISettings.ACTION_SOURCE_FMC)) {
            source = Avionics.HSI_SOURCE_GPS;
            this.avionics.set_hsi_source(source);

        } else if (command.equals(XHSISettings.ACTION_RADIO1_ADF1)) {
            radio1 = Avionics.EFIS_RADIO_ADF;
            this.avionics.set_radio1(radio1);
        } else if (command.equals(XHSISettings.ACTION_RADIO1_OFF)) {
            radio1 = Avionics.EFIS_RADIO_OFF;
            this.avionics.set_radio1(radio1);
        } else if (command.equals(XHSISettings.ACTION_RADIO1_NAV1)) {
            radio1 = Avionics.EFIS_RADIO_NAV;
            this.avionics.set_radio1(radio1);
        } else if (command.equals(XHSISettings.ACTION_RADIO1_DME_ARC)) {
            // Open dialog to set DME1 arc
            this.dme1_radius_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.dme2_radius_dialog.getWidth()-80, this.main_frame.getY()+90);
            this.dme1_radius_dialog.setVisible(true);
            this.dme1_radius_dialog.pack();
        } else if (command.equals(XHSISettings.ACTION_SYNC_OBS1)) {
            this.avionics.set_nav1_obs(this.avionics.nav1_course());

        } else if (command.equals(XHSISettings.ACTION_RADIO2_ADF2)) {
            radio2 = Avionics.EFIS_RADIO_ADF;
            this.avionics.set_radio2(radio2);
        } else if (command.equals(XHSISettings.ACTION_RADIO2_OFF)) {
            radio2 = Avionics.EFIS_RADIO_OFF;
            this.avionics.set_radio2(radio2);
        } else if (command.equals(XHSISettings.ACTION_RADIO2_NAV2)) {
            radio2 = Avionics.EFIS_RADIO_NAV;
            this.avionics.set_radio2(radio2);
        } else if (command.equals(XHSISettings.ACTION_RADIO2_DME_ARC)) {
            // Open dialog to set DME2 arc
            this.dme2_radius_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2+80, this.main_frame.getY()+90);
            this.dme2_radius_dialog.setVisible(true);
            this.dme2_radius_dialog.pack();
        } else if (command.equals(XHSISettings.ACTION_SYNC_OBS2)) {
            this.avionics.set_nav2_obs(this.avionics.nav2_course());

        } else if (command.equals(XHSISettings.ACTION_SUBMODE_APP)) {
            map_mode = Avionics.EFIS_MAP_APP;
            this.avionics.set_submode(map_mode);
        } else if (command.equals(XHSISettings.ACTION_SUBMODE_VOR)) {
            map_mode = Avionics.EFIS_MAP_VOR;
            this.avionics.set_submode(map_mode);
        } else if (command.equals(XHSISettings.ACTION_SUBMODE_MAP)) {
            map_mode = Avionics.EFIS_MAP_MAP;
            this.avionics.set_submode(map_mode);
        } else if (command.equals(XHSISettings.ACTION_SUBMODE_NAV)) {
            map_mode = Avionics.EFIS_MAP_NAV;
            this.avionics.set_submode(map_mode);
        } else if (command.equals(XHSISettings.ACTION_SUBMODE_PLN)) {
            map_mode = Avionics.EFIS_MAP_PLN;
            this.avionics.set_submode(map_mode);

        } else if (command.equals(XHSISettings.ACTION_MODE_CENTERED)) {
            map_centered = this.checkbox_mode_centered.isSelected() ? Avionics.EFIS_MAP_CENTERED : Avionics.EFIS_MAP_EXPANDED;
            this.avionics.set_mode(map_centered);

        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_ARPT)) {
            show_arpt = this.checkbox_symbols_show_arpt.isSelected();
            this.avionics.set_show_arpt(show_arpt);
        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_WPT)) {
            show_wpt = this.checkbox_symbols_show_wpt.isSelected();
            this.avionics.set_show_wpt(show_wpt);
        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_VOR)) {
            show_vor = this.checkbox_symbols_show_vor.isSelected();
            this.avionics.set_show_vor(show_vor);
        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_NDB)) {
            show_ndb = this.checkbox_symbols_show_ndb.isSelected();
            this.avionics.set_show_ndb(show_ndb);
        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_TFC)) {
            show_tfc = this.checkbox_symbols_show_tfc.isSelected();
            this.avionics.set_show_tfc(show_tfc);
        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_POS)) {
            show_pos = this.checkbox_symbols_show_pos.isSelected();
            this.avionics.set_show_pos(show_pos);
        } else if (command.equals(XHSISettings.ACTION_SYMBOLS_SHOW_DATA)) {
            show_data = this.checkbox_symbols_show_data.isSelected();
            this.avionics.set_show_data(show_data);

        } else if (command.equals(XHSISettings.ACTION_XPDR_OFF)) {
            xpdr = Avionics.XPDR_OFF;
            this.avionics.set_xpdr(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_STBY)) {
            xpdr = Avionics.XPDR_STBY;
            this.avionics.set_xpdr(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_ON)) {
            xpdr = Avionics.XPDR_ON;
            this.avionics.set_xpdr(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_TA)) {
            xpdr = Avionics.XPDR_TA;
            this.avionics.set_xpdr(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_TARA)) {
            xpdr = Avionics.XPDR_TARA;
            this.avionics.set_xpdr(xpdr);

        } else if (command.equals(XHSISettings.ACTION_HOLDING_HIDE)) {
            // Hide Holding pattern
            draw_holding = false;
        } else if (command.equals(XHSISettings.ACTION_HOLDING_SHOW)) {
            // Set & show holding pattern
            this.holding_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.holding_dialog.getWidth()/2, this.main_frame.getY()+100);
            this.holding_dialog.setVisible(true);
            this.holding_dialog.pack();

        } else for (int i=0; i<this.range_list.length; i++) {
            if (command.equals(this.range_list[i])) {
                // Range override
                range = Integer.parseInt(command);
                this.avionics.set_range_index(i);
            }
        }

    }


    public void update(Avionics avionics) {

        // settings have been changed in X-Plane, change the selections in the menu accordingly

//        this.checkbox_avionics_power.setSelected(avionics.power());

        int new_source = avionics.hsi_source();
        this.radio_button_source_nav1.setSelected( new_source == Avionics.HSI_SOURCE_NAV1 );
        this.radio_button_source_nav2.setSelected( new_source == Avionics.HSI_SOURCE_NAV2 );
        this.radio_button_source_fmc.setSelected( new_source == Avionics.HSI_SOURCE_GPS );

        int new_radio1 = avionics.efis_radio1();
        this.radio_button_radio1_adf.setSelected( new_radio1 == Avionics.EFIS_RADIO_ADF );
        this.radio_button_radio1_off.setSelected( new_radio1 == Avionics.EFIS_RADIO_OFF );
        this.radio_button_radio1_nav.setSelected( new_radio1 == Avionics.EFIS_RADIO_NAV );

        int new_radio2 = avionics.efis_radio2();
        this.radio_button_radio2_adf.setSelected( new_radio2 == Avionics.EFIS_RADIO_ADF );
        this.radio_button_radio2_off.setSelected( new_radio2 == Avionics.EFIS_RADIO_OFF );
        this.radio_button_radio2_nav.setSelected( new_radio2 == Avionics.EFIS_RADIO_NAV );

        int new_map_mode = avionics.map_submode();
        this.radio_button_submode_app.setSelected( new_map_mode == Avionics.EFIS_MAP_APP );
        this.radio_button_submode_vor.setSelected( new_map_mode == Avionics.EFIS_MAP_VOR );
        this.radio_button_submode_map.setSelected( new_map_mode == Avionics.EFIS_MAP_MAP );
        this.radio_button_submode_nav.setSelected( new_map_mode == Avionics.EFIS_MAP_NAV );
        this.radio_button_submode_pln.setSelected( new_map_mode == Avionics.EFIS_MAP_PLN );

        this.checkbox_mode_centered.setSelected( avionics.map_mode() == Avionics.EFIS_MAP_CENTERED );

        int new_range_index = avionics.map_range_index();
        for (int i=0; i<this.range_list.length; i++) {
            this.radio_button_range[i].setSelected(  new_range_index == i );
        }

        this.checkbox_symbols_show_arpt.setSelected( avionics.efis_shows_arpt() );
        this.checkbox_symbols_show_wpt.setSelected( avionics.efis_shows_wpt() );
        this.checkbox_symbols_show_vor.setSelected( avionics.efis_shows_vor() );
        this.checkbox_symbols_show_ndb.setSelected( avionics.efis_shows_ndb() );
        this.checkbox_symbols_show_tfc.setSelected( avionics.efis_shows_tfc() );
        this.checkbox_symbols_show_pos.setSelected( avionics.efis_shows_pos() );
        this.checkbox_symbols_show_data.setSelected( avionics.efis_shows_data() );

        int new_xpdr = avionics.transponder_mode();
        this.radio_button_xpdr_off.setSelected( new_xpdr == Avionics.XPDR_OFF );
        this.radio_button_xpdr_stby.setSelected( new_xpdr == Avionics.XPDR_STBY );
        this.radio_button_xpdr_on.setSelected( new_xpdr == Avionics.XPDR_ON );
        this.radio_button_xpdr_ta.setSelected( new_xpdr == Avionics.XPDR_TA );
        this.radio_button_xpdr_tara.setSelected( new_xpdr == Avionics.XPDR_TARA );

    }


    public void linkAvionics( Avionics link ) {
        // when the avionics instance exists, it will tell us...
        this.avionics = link;
    }


    public void preference_changed(String key) {

        boolean airbus_modes = XHSIPreferences.get_instance().get_airbus_modes();

        this.radio_button_submode_app.setText(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_ILS : XHSISettings.LABEL_SUBMODE_APP);
        this.radio_button_submode_vor.setText(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_VOR : XHSISettings.LABEL_SUBMODE_VOR);
        this.radio_button_submode_map.setText(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_NAV : XHSISettings.LABEL_SUBMODE_MAP);
        this.radio_button_submode_nav.setText(airbus_modes ? XHSISettings.LABEL_SUBMODE_ARC : XHSISettings.LABEL_SUBMODE_NAV);
        this.radio_button_submode_pln.setText(airbus_modes ? XHSISettings.LABEL_SUBMODE_PLAN : XHSISettings.LABEL_SUBMODE_PLN);
        this.checkbox_mode_centered.setVisible( ! airbus_modes );

    }


}
