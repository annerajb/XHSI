/**
* XHSISettings.java
* 
* Add HSI settings options to the menu bar, handle the commands that the
* menu selections generate and keep static variables with the settings
* 
* Copyright (C) 2009-2014  Marc Rogiers (marrog.123@gmail.com)
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

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.SimCommand;


/**
* Add EFIS settings options to the menu bar, handle the commands that the
* menu selections generate and keep static variables with the settings
*/
public class XHSISettings implements ActionListener, PreferencesObserver {

    //private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public Avionics avionics;

    private JFrame main_frame;
//    private JFrame nd_frame;

    // menu item commands must be unique...

    public static final String ACTION_STYLE_BOEING = "Boeing";
    public static final String ACTION_STYLE_AIRBUS = "Airbus";

    public static final String ACTION_SOURCE_NAV1 = "NAV 1";
    public static final String ACTION_SOURCE_NAV2 = "NAV 2";
    public static final String ACTION_SOURCE_FMC = "FMC";

    public static final String ACTION_SUBMODE_APP = "Mode APP";
    public static final String ACTION_SUBMODE_VOR = "Mode VOR";
    public static final String ACTION_SUBMODE_MAP = "Mode MAP";
    public static final String ACTION_SUBMODE_NAV = "Mode NAV";
    public static final String ACTION_SUBMODE_PLN = "Mode PLN";
    public static final String ACTION_MODE_CENTERED = "CTR";
    public static final String ACTION_ZOOMIN = "Zoom In";

    public static final String LABEL_SUBMODE_APP = "  APP";
    public static final String LABEL_SUBMODE_VOR = "  VOR";
    public static final String LABEL_SUBMODE_MAP = "  MAP";
    public static final String LABEL_SUBMODE_NAV = "  NAV";
    public static final String LABEL_SUBMODE_PLN = "  PLN";
    public static final String LABEL_MODE_CENTERED = "  CTR";
    public static final String LABEL_ZOOMIN = "Zoom in (\u00F7100)";

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

    public static final String ACTION_SYNC_CRS1 = "Sync CRS1";

    public static final String ACTION_RADIO2_ADF2 = "ADF2";
    public static final String ACTION_RADIO2_OFF = "Radio2 off";
    public static final String LABEL_RADIO2_OFF = "Off";
    public static final String ACTION_RADIO2_NAV2 = "NAV2";

    public static final String ACTION_RADIO2_DME_ARC = "Draw DME2 arc ...";

    public static final String ACTION_SYNC_CRS2 = "Sync CRS2";

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

    public static final String ACTION_HOLDING_HIDE = "Hide holding";
    public static final String ACTION_HOLDING_SHOW = "Define holding ...";

    public static final String ACTION_FIX_HIDE = "Hide CDU fix";
    public static final String ACTION_FIX_SHOW = "Define CDU fix ...";

    public static final String ACTION_AP_SET_ALTITUDE = "Set Altitude ...";
    
    public static final String ACTION_AP_LEVEL_CHG = "Level Change";
    public static final String ACTION_AP_LEVEL_CHG_MNG = "Level Change Managed";
    public static final String ACTION_AP_LEVEL_HOLD = "Hold";
    public static final String ACTION_AP_SET_SPEED = "Set Speed ...";
    public static final String ACTION_AP_SPEED_MNG = "Speed Managed";
    public static final String ACTION_AP_SPEED_SEL = "Speed Selected";
    public static final String ACTION_AP_SET_HEADING = "Set Heading ...";
    public static final String ACTION_AP_HDG_MODE = "Follow Heading";
    public static final String ACTION_AP_NAV_MODE = "NAV";
    public static final String ACTION_AP_VOR_MODE = "VOR";
    public static final String ACTION_AP_SET_VS = "Set Vertical Speed ...";
    public static final String ACTION_AP_LEVEL_OFF = "Level Off";
    public static final String ACTION_AP_VS_MODE = "V/S Mode";
    public static final String ACTION_AP_FD_SWITCH = "Flight director";
    public static final String ACTION_AP_AP_SWITCH = "Autopilot";    
    public static final String ACTION_AP_ILS_ON = "ILS on";
    public static final String ACTION_AP_ILS_OFF = "ILS on";
    public static final String ACTION_AP_LOC = "LOC capture";
    public static final String ACTION_AP_GS = "Glide slope capture";
    public static final String ACTION_AP_ATHR_ON = "A/THR on";
    public static final String ACTION_AP_ATHR_OFF = "A/THR off";
    public static final String ACTION_AP_METRIC_ON = "Display metric alt on";
    public static final String ACTION_AP_METRIC_OFF = "Display metric alt off";
    public static final String ACTION_AP_TRK_FPA_ON = "Bird on";
    public static final String ACTION_AP_TRK_FPA_OFF = "Bird off";
    public static final String ACTION_AP_TRK_MACH_ON = "Display Mach speed";
    public static final String ACTION_AP_TRK_MACH_OFF = "Display Kts speed";
   
    
    public static final String ACTION_ENGINE_TYPE_N1 = "N1";    
    public static final String ACTION_ENGINE_TYPE_EPR = "EPR";    
    public static final String ACTION_ENGINE_TYPE_TRQ = "TRQ";
    public static final String ACTION_ENGINE_TYPE_MAP = "MAP";

    public static final String ACTION_TRQ_SCALE_LBFT = "LbFt";
    public static final int TRQ_SCALE_LBFT = 0;
    public static final String ACTION_TRQ_SCALE_NM = "Nm";
    public static final int TRQ_SCALE_NM = 1;
    public static final String ACTION_TRQ_SCALE_PERCENT = "Percent";
    public static final int TRQ_SCALE_PERCENT = 2;
    
    public static final String ACTION_FUEL_UNITS_KG = "Kg";
    public static final int FUEL_UNITS_KG = 0;
    public static final String ACTION_FUEL_UNITS_LBS = "Lb";
    public static final int FUEL_UNITS_LBS = 1;
    public static final String ACTION_FUEL_UNITS_USG = "USG";
    public static final int FUEL_UNITS_USG = 2;
    public static final String ACTION_FUEL_UNITS_LTR = "Ltr";
    public static final int FUEL_UNITS_LTR = 3;

    public static final String ACTION_TEMP_UNITS_C = "Celcius";
    public static final int TEMP_UNITS_C = 0;
    public static final String ACTION_TEMP_UNITS_F = "Fahrenheit";
    public static final int TEMP_UNITS_F = 1;
    public static final String ACTION_TEMP_UNITS_K = "Kelvin";
    public static final int TEMP_UNITS_K = 2;
    
    public static final String ACTION_MAX_TRQ_AUTO = "Auto";
    public static final String ACTION_MAX_TRQ_SET = "Set ...";
    
//    public static final String ACTION_ESTIMATE_FUEL_CAPACITY = "Estimate fuel capacity";
//    public static final String ACTION_SET_FUEL_CAPACITY = "Set fuel capacity ...";
    public static final String ACTION_RESET_MAX_FF = "Reset max FF";

    public static final String ACTION_MFD_ARPT_CHART  = "Airport Chart";
    public static final String ACTION_MFD_FPLN        = "Flight Plan";
    public static final String ACTION_MFD_LOWER_EICAS = "Lower EICAS";
    public static final String ACTION_MFD_RTU         = "RTU Display";
    public static final String ACTION_MFD_SYSTEM      = "Cruise";
    public static final String ACTION_MFD_FCTL        = "Flight Controls";
    public static final String ACTION_MFD_APU         = "APU";
    public static final String ACTION_MFD_ELEC        = "Electrics";
    public static final String ACTION_MFD_WHEELS      = "Wheels";
    public static final String ACTION_MFD_DOOR_OXY    = "Doors and oxygen";
    public static final String ACTION_MFD_BLEED       = "Bleed air";
    public static final String ACTION_MFD_COND        = "Air conditionning";
    public static final String ACTION_MFD_FUEL        = "Fuel";
    public static final String ACTION_MFD_CAB_PRESS   = "Cabin pressure";
    public static final String ACTION_MFD_HYDR        = "Hydraulics";
    public static final String ACTION_MFD_STATUS      = "Status";
    
    public static final String ACTION_CDU_AIRCRAFT_OR_DUMMY      = "Aircraft's custom FMC (or a dummy)";
    public static final String ACTION_CDU_XFMC        = "X-FMC";
    public static final String ACTION_CDU_UFMC        = "UFMC/X737FMC";
    public static final String ACTION_CDU_LEFT        = "Left CDU";
    public static final String ACTION_CDU_RIGHT       = "Right CDU";

    public static final String ACTION_CLOCK_UTC = "UTC";
    public static final String ACTION_CLOCK_LT = "Local Time";
    public static final String ACTION_CHR_START_STOP_RESET = "CHR Start/Stop/Reset";

    public static final int ENGINE_TYPE_N1 = 0;
    public static final int ENGINE_TYPE_EPR = 1;
    public static final int ENGINE_TYPE_TRQ = 2;
    public static final int ENGINE_TYPE_MAP = 3;
            
    private String range_list[] = { "10", "20", "40", "80", "160", "320", "640" };
    private String zoomin_range_list[] = { "0.10", "0.20", "0.40", "0.80", "1.60", "3.20", "6.40" };


    public int style = Avionics.STYLE_BOEING;
//    public int old_style = Avionics.STYLE_BOEING;
    
    public int source = Avionics.HSI_SOURCE_NAV1;
    public int radio1 = Avionics.EFIS_RADIO_NAV;
    public float dme1_radius = 0.0f;
    public int radio2 = Avionics.EFIS_RADIO_NAV;
    public float dme2_radius = 0.0f;
    public int map_mode = Avionics.EFIS_MAP_MAP;
    public int map_centered = Avionics.EFIS_MAP_EXPANDED;
    public int map_range = 40;
    public int map_range_index = 2;
    public boolean map_zoomin;
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

    public boolean draw_cdu_fix = false;
    public String cdu_fix = "";
    public int cdu_fix_radial = 0;
    public float cdu_fix_dist = 1.0f;

    public int engine_type;
    public int trq_scale;
    public float max_trq;
    public int fuel_units;
    public int temp_units;
    
    public int xpdr = Avionics.XPDR_TA;
//    public FUEL_UNITS fuel_units = FUEL_UNITS.KG;
    
    public int mfd_mode = 0;

    public int cdu_source = 0;    
    public int cdu_side = 0;

    public int clock_mode = 0;

    // ------ Style Sub Menu ------
    private JRadioButtonMenuItem radio_button_style_boeing;
    private JRadioButtonMenuItem radio_button_style_airbus;
    
    // ------ Transponder Sub Menu ------
    private JRadioButtonMenuItem radio_button_xpdr_off;
    private JRadioButtonMenuItem radio_button_xpdr_stby;
    private JRadioButtonMenuItem radio_button_xpdr_on;
    private JRadioButtonMenuItem radio_button_xpdr_ta;
    private JRadioButtonMenuItem radio_button_xpdr_tara;

    // ------ Source Sub Menu ------
    private JRadioButtonMenuItem radio_button_source_nav1;
    private JRadioButtonMenuItem radio_button_source_nav2;
    private JRadioButtonMenuItem radio_button_source_fmc;
    
    // ------ ND Sub Menu ------
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
    //    private JCheckBoxMenuItem checkbox_map_zoomin;

    private JRadioButtonMenuItem[] radio_button_range = new JRadioButtonMenuItem[14];

    private JCheckBoxMenuItem checkbox_symbols_show_arpt;
    private JCheckBoxMenuItem checkbox_symbols_show_wpt;
    private JCheckBoxMenuItem checkbox_symbols_show_vor;
    private JCheckBoxMenuItem checkbox_symbols_show_ndb;
    private JCheckBoxMenuItem checkbox_symbols_show_tfc;
    private JCheckBoxMenuItem checkbox_symbols_show_pos;
    private JCheckBoxMenuItem checkbox_symbols_show_data;


    public JRadioButtonMenuItem radiobutton_holding_hide;
    public JRadioButtonMenuItem radiobutton_holding_show;

    public JRadioButtonMenuItem radiobutton_fix_hide;
    public JRadioButtonMenuItem radiobutton_fix_show;

    public JCheckBoxMenuItem checkbox_dme1_arc;
    public JCheckBoxMenuItem checkbox_dme2_arc;

    private DMEArcDialog dme1_radius_dialog;
    private DMEArcDialog dme2_radius_dialog;
    private HoldingDialog holding_dialog;
    private FixDialog fix_dialog;
    private MaxTRQDialog max_trq_dialog;
    private AltitudeDialog altitude_dialog;
    private HeadingDialog heading_dialog;
    private VerticalSpeedDialog v_speed_dialog;
    private SpeedDialog speed_dialog;
    //private FuelDialog fuel_dialog;

    // ------ MCP Sub Menu ------
    /*
     *  private AltitudeDialog altitude_dialog;
     *  private SpeedDialog speed_dialog;
     *  private HeadingDialog heading_dialog;
     *  private VerticalSpeedDialog vertical_speed_dialog;
     */
    private JCheckBoxMenuItem checkbox_autopilot;
    private JCheckBoxMenuItem checkbox_flight_director;

    // ------ EICAS Sub Menu ------
    private JRadioButtonMenuItem radio_button_engine_n1;
    private JRadioButtonMenuItem radio_button_engine_epr;
    private JRadioButtonMenuItem radio_button_engine_trq;
    private JRadioButtonMenuItem radio_button_engine_map;
    
    private JRadioButtonMenuItem radio_button_trq_scale_lbft;
    private JRadioButtonMenuItem radio_button_trq_scale_nm;
    private JRadioButtonMenuItem radio_button_trq_scale_percent;

    private JRadioButtonMenuItem radiobutton_max_trq_auto;
    private JRadioButtonMenuItem radiobutton_max_trq_set;

    private JRadioButtonMenuItem radio_button_fuel_units_kg;
    private JRadioButtonMenuItem radio_button_fuel_units_lbs;
    private JRadioButtonMenuItem radio_button_fuel_units_usg;
    private JRadioButtonMenuItem radio_button_fuel_units_ltr;

    private JRadioButtonMenuItem radio_button_temp_units_c;
    private JRadioButtonMenuItem radio_button_temp_units_f;
    // private JRadioButtonMenuItem radio_button_temp_units_k;
    
    // ------ MFD Sub Menu ------
    private JRadioButtonMenuItem radio_button_mfd_arpt;
    private JRadioButtonMenuItem radio_button_mfd_fpln;
    private JRadioButtonMenuItem radio_button_mfd_eicas;
    private JRadioButtonMenuItem radio_button_mfd_rtu;
    private JRadioButtonMenuItem radio_button_mfd_apu;
    private JRadioButtonMenuItem radio_button_mfd_fctl;
    private JRadioButtonMenuItem radio_button_mfd_sys;
    private JRadioButtonMenuItem radio_button_mfd_elec;
    private JRadioButtonMenuItem radio_button_mfd_wheels;
    private JRadioButtonMenuItem radio_button_mfd_door_oxy;
    private JRadioButtonMenuItem radio_button_mfd_bleed;
    private JRadioButtonMenuItem radio_button_mfd_cond;
    private JRadioButtonMenuItem radio_button_mfd_fuel;
    private JRadioButtonMenuItem radio_button_mfd_cab_press;
    private JRadioButtonMenuItem radio_button_mfd_hydr;
    private JRadioButtonMenuItem radio_button_mfd_status;
    
    // ------ CDU Sub Menu ------
    private JRadioButtonMenuItem radio_button_cdu_aircraft;
    private JRadioButtonMenuItem radio_button_cdu_xfmc;
    private JRadioButtonMenuItem radio_button_cdu_ufmc;
    
    private JRadioButtonMenuItem radio_button_cdu_left;
    private JRadioButtonMenuItem radio_button_cdu_right;
    
    // ------ Clock Sub Menu ------
    private JRadioButtonMenuItem radio_button_clock_utc;
    private JRadioButtonMenuItem radio_button_clock_lt;
    
    private static XHSISettings single_instance = null;


    public static XHSISettings get_instance() {
        if (XHSISettings.single_instance == null) {
            XHSISettings.single_instance = new XHSISettings();
        }
        return XHSISettings.single_instance;
    }


    public void init_frames(JFrame xhsi_main_frame) {

        this.main_frame = xhsi_main_frame;
        this.dme1_radius_dialog = new DMEArcDialog(xhsi_main_frame, 1);
        this.dme2_radius_dialog = new DMEArcDialog(xhsi_main_frame, 2);
        this.holding_dialog = new HoldingDialog(xhsi_main_frame);
        this.fix_dialog = new FixDialog(xhsi_main_frame);
        this.max_trq_dialog = new MaxTRQDialog(xhsi_main_frame);
        this.altitude_dialog = new AltitudeDialog(xhsi_main_frame, avionics);
        this.heading_dialog = new HeadingDialog(xhsi_main_frame, avionics);
        this.v_speed_dialog = new VerticalSpeedDialog(xhsi_main_frame, avionics);
        this.speed_dialog = new SpeedDialog(xhsi_main_frame, avionics);
        // this.fuel_dialog = new FuelDialog(xhsi_main_frame);

    }


    public void create_menu(JMenuBar menu_bar) {

        JMenuItem menu_item;
        JCheckBoxMenuItem checkbox_menu_item;
        JRadioButtonMenuItem radio_button_menu_item;

        // Hmmm... code re-use? This looks more like code polycopy...

        /*
         *  define the "Style" menu
         */
        JMenu xhsi_style_menu = new JMenu("Style");

        ButtonGroup style_group = new ButtonGroup();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_STYLE_BOEING);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        style_group.add(radio_button_menu_item);
        xhsi_style_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_style_boeing = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_STYLE_AIRBUS);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        style_group.add(radio_button_menu_item);
        xhsi_style_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_style_airbus = radio_button_menu_item;

        
        // add the "Style" menu to the menubar
        menu_bar.add(xhsi_style_menu);


        /*
         *  define the "Transponder" menu
         */
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
        radio_button_menu_item.setToolTipText("Transponder on; TCAS will issue Traffic Alerts (Resolution Alerts are inoperative)");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        xpdr_group.add(radio_button_menu_item);
        xhsi_xpdr_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_xpdr_tara = radio_button_menu_item;

        // add the "Transponder" menu to the menubar
        menu_bar.add(xhsi_xpdr_menu);


        /*
         *  define the "Source" menu
         */
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

        // add the "NAV-source" menu to the menubar
        menu_bar.add(xhsi_source_menu);


        /*
         *  define the "ND" menu
         */
        JMenu nd_menu = new JMenu("ND");
        
        // define the "Radio1" submenu
        JMenu nd_radio1_submenu;

        ButtonGroup radio1_group = new ButtonGroup();

        // define the menu items, and add them to the "Radio1" submenu
        nd_radio1_submenu = new JMenu("Radio1");

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO1_ADF1);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for ADF1");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio1_group.add(radio_button_menu_item);
        nd_radio1_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio1_adf = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.LABEL_RADIO1_OFF);
        radio_button_menu_item.setToolTipText("Don't display bearing lines or pointer arrows for Radio1");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_RADIO1_OFF);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio1_group.add(radio_button_menu_item);
        nd_radio1_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio1_off = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO1_NAV1);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for NAV1");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        radio1_group.add(radio_button_menu_item);
        nd_radio1_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio1_nav = radio_button_menu_item;

        nd_radio1_submenu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_RADIO1_DME_ARC);
        checkbox_menu_item.setToolTipText("Draw a circle around NAV1's DME");
        checkbox_menu_item.addActionListener(this);
        nd_radio1_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way from DMEArcDialog.java
        checkbox_dme1_arc = checkbox_menu_item;

        nd_radio1_submenu.addSeparator();

        menu_item = new JMenuItem(XHSISettings.ACTION_SYNC_CRS1);
        menu_item.setToolTipText("Set NAV1 CRS to LOC/ILS frontcourse or Direct-To VOR course");
        menu_item.addActionListener(this);
        nd_radio1_submenu.add(menu_item);

        // add the "Radio1" menu to the "ND" menu
        nd_menu.add(nd_radio1_submenu);


        // define the "Radio2" menu
        JMenu nd_radio2_submenu;

        ButtonGroup radio2_group = new ButtonGroup();

        // define the menu items, and add them to the "Radio2" submenu
        nd_radio2_submenu = new JMenu("Radio2");

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO2_ADF2);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for ADF2");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio2_group.add(radio_button_menu_item);
        nd_radio2_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio2_adf = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.LABEL_RADIO2_OFF);
        radio_button_menu_item.setToolTipText("Don't display bearing lines or pointer arrows for Radio2");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_RADIO2_OFF);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        radio2_group.add(radio_button_menu_item);
        nd_radio2_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio2_off = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_RADIO2_NAV2);
        radio_button_menu_item.setToolTipText("Display bearing lines or pointer arrows for NAV2");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        radio2_group.add(radio_button_menu_item);
        nd_radio2_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_radio2_nav = radio_button_menu_item;

        nd_radio2_submenu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_RADIO2_DME_ARC);
        checkbox_menu_item.setToolTipText("Draw a circle around NAV2's DME");
        checkbox_menu_item.addActionListener(this);
        nd_radio2_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way from DMEArcDialog.java
        checkbox_dme2_arc = checkbox_menu_item;

        nd_radio2_submenu.addSeparator();

        menu_item = new JMenuItem(XHSISettings.ACTION_SYNC_CRS2);
        menu_item.setToolTipText("Set NAV1 CRS to LOC/ILS frontcourse or Direct-To VOR course");
        menu_item.addActionListener(this);
        nd_radio2_submenu.add(menu_item);

        // add the "Radio2" submenu to the "ND" menu
        nd_menu.add(nd_radio2_submenu);


        // define the "Mode" submenu
        JMenu nd_mode_submenu = new JMenu("Mode");

        boolean airbus_modes = XHSIPreferences.get_instance().get_airbus_modes();

        ButtonGroup submode_group = new ButtonGroup();

        // define the menu items, and add them to the "Mode" submenu
        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_ILS : XHSISettings.LABEL_SUBMODE_APP);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_APP + " : HSI with CDI and GS");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_APP);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        nd_mode_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_app = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_VOR : XHSISettings.LABEL_SUBMODE_VOR);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_VOR + " HSI with CDI");
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_VOR);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        nd_mode_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_vor = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ROSE_NAV : XHSISettings.LABEL_SUBMODE_MAP);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_MAP);
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_MAP);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        submode_group.add(radio_button_menu_item);
        nd_mode_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_map = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_ARC : XHSISettings.LABEL_SUBMODE_NAV);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_NAV);
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_NAV);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        nd_mode_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_nav = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(airbus_modes ? XHSISettings.LABEL_SUBMODE_PLAN : XHSISettings.LABEL_SUBMODE_PLN);
        radio_button_menu_item.setToolTipText(XHSISettings.ACTION_SUBMODE_PLN);
        radio_button_menu_item.setActionCommand(XHSISettings.ACTION_SUBMODE_PLN);
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        submode_group.add(radio_button_menu_item);
        nd_mode_submenu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_submode_pln = radio_button_menu_item;

        nd_mode_submenu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.LABEL_MODE_CENTERED);
        checkbox_menu_item.setToolTipText("Centered / Expanded");
        checkbox_menu_item.setActionCommand(XHSISettings.ACTION_MODE_CENTERED);
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setMnemonic(KeyEvent.VK_C);
        checkbox_menu_item.setSelected(false);
        checkbox_menu_item.setVisible( ! airbus_modes );
        nd_mode_submenu.add(checkbox_menu_item);
        // keep a reference
        this.checkbox_mode_centered = checkbox_menu_item;

        // add the "Mode" submenu to the "ND" menu
        nd_menu.add(nd_mode_submenu);


        // define the "Range" submenu
        JMenu nd_range_submenu = new JMenu("Range");

        ButtonGroup range_group = new ButtonGroup();

        for (int i=0; i<this.zoomin_range_list.length; i++) {
            radio_button_menu_item = new JRadioButtonMenuItem( this.zoomin_range_list[i] );
            radio_button_menu_item.setToolTipText(this.zoomin_range_list[i] + " NM");
            radio_button_menu_item.addActionListener(this);
            radio_button_menu_item.setSelected(false);
            range_group.add(radio_button_menu_item);
            nd_range_submenu.add(radio_button_menu_item);
            radio_button_range[i] = radio_button_menu_item;
        }

        nd_range_submenu.addSeparator();

        for (int i=0; i<this.range_list.length; i++) {
            radio_button_menu_item = new JRadioButtonMenuItem( this.range_list[i] );
            radio_button_menu_item.setToolTipText(this.range_list[i] + " NM");
            radio_button_menu_item.addActionListener(this);
            if ( i==this.map_range_index )
                // initial default
                radio_button_menu_item.setSelected(true);
            else
                radio_button_menu_item.setSelected(false);
            range_group.add(radio_button_menu_item);
            nd_range_submenu.add(radio_button_menu_item);
            radio_button_range[i+7] = radio_button_menu_item;
        }

//        xhsi_range_menu.addSeparator();
//
//        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.LABEL_ZOOMIN);
//        checkbox_menu_item.setToolTipText("Zoom in (\u00F7100)");
//        checkbox_menu_item.setActionCommand(XHSISettings.ACTION_ZOOMIN);
//        checkbox_menu_item.addActionListener(this);
//        xhsi_range_menu.add(checkbox_menu_item);
//        // keep a reference
//        this.checkbox_map_zoomin = checkbox_menu_item;

        // add the "Range" submenu to the "ND" menu
        nd_menu.add(nd_range_submenu);


        // define the "Symbols" menu
        JMenu nd_symbols_submenu = new JMenu("Symbols");

        // define the menu items, and add them to the "Symbols" submenu
        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_ARPT);
        checkbox_menu_item.setToolTipText("Show Airports");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_arpt = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_WPT);
        checkbox_menu_item.setToolTipText("Shox Fixes");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_wpt = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_VOR);
        checkbox_menu_item.setToolTipText("Show VORs");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_vor = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_NDB);
        checkbox_menu_item.setToolTipText("Show NDBs");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_ndb = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_TFC);
        checkbox_menu_item.setToolTipText("Show Traffic");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_tfc = checkbox_menu_item;

        nd_symbols_submenu.addSeparator();

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_POS);
        checkbox_menu_item.setToolTipText("Draw bearing lines instead of pointer arrows)");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_pos = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_SYMBOLS_SHOW_DATA);
        checkbox_menu_item.setToolTipText("Show altitudes for FMS waypoints");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(true);
        nd_symbols_submenu.add(checkbox_menu_item);
        // keep a reference to the checkbox to set or clear it in a non-standard way
        this.checkbox_symbols_show_data = checkbox_menu_item;

        // add the "Symbols" menu to the menubar
        nd_menu.add(nd_symbols_submenu);

        // define the "Holding" submenu
        JMenu nd_holding_submenu = new JMenu("Holding");

        ButtonGroup holding_group = new ButtonGroup();

        // define the menu items, and add them to the "Holding" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_HOLDING_HIDE);
        radio_button_menu_item.setToolTipText("Hide the holding");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        nd_holding_submenu.add(radio_button_menu_item);
        holding_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from HoldingDialog.java
        radiobutton_holding_hide = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_HOLDING_SHOW);
        radio_button_menu_item.setToolTipText("Draw a holding");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        nd_holding_submenu.add(radio_button_menu_item);
        holding_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from HoldingDialog.java
        radiobutton_holding_show = radio_button_menu_item;

        // add the "Holding" submenu to the "ND" menu
        nd_menu.add(nd_holding_submenu);


        // define the "Fix" submenu
        JMenu nd_fix_submenu = new JMenu("Fix");

        ButtonGroup fix_group = new ButtonGroup();

        // define the menu items, and add them to the "Fix" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_FIX_HIDE);
        radio_button_menu_item.setToolTipText("Hide the CDU FIX");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        nd_fix_submenu.add(radio_button_menu_item);
        fix_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from FixDialog.java
        radiobutton_fix_hide = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_FIX_SHOW);
        radio_button_menu_item.setToolTipText("Draw a CDU FIX");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        nd_fix_submenu.add(radio_button_menu_item);
        fix_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from FixDialog.java
        radiobutton_fix_show = radio_button_menu_item;

        // add the "Fix" submenu to the "ND" menu
        nd_menu.add(nd_fix_submenu);
        
        // add the "ND" menu to the menubar
        menu_bar.add(nd_menu);

        
        /*
         *  define the "AutoPilot" or MCP/FCU menu
         */
        JMenu ap_menu = new JMenu("AP");
        
        // Altitude Sub Menu
        JMenu ap_altitude_submenu = new JMenu("Altitude");
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_SET_ALTITUDE);
        menu_item.setToolTipText("Set the autopilot altitude target");
        menu_item.addActionListener(this);
        ap_altitude_submenu.add(menu_item);
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_LEVEL_HOLD);
        menu_item.setToolTipText("Hold current altitude");
        menu_item.addActionListener(this);
        ap_altitude_submenu.add(menu_item);
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_LEVEL_CHG);
        menu_item.setToolTipText("Engage level change - open mode");
        menu_item.addActionListener(this);
        ap_altitude_submenu.add(menu_item);

        menu_item = new JMenuItem(XHSISettings.ACTION_AP_LEVEL_CHG_MNG);
        menu_item.setToolTipText("Engage level change - managed mode");
        menu_item.addActionListener(this);
        ap_altitude_submenu.add(menu_item);
        
        ap_menu.add(ap_altitude_submenu);

        // Speed Sub Menu
        JMenu ap_speed_submenu = new JMenu("Speed");
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_SET_SPEED);
        menu_item.setToolTipText("Set the auto/throttle/thrust speed target");
        menu_item.addActionListener(this);
        ap_speed_submenu.add(menu_item);
   
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_SPEED_MNG);
        menu_item.setToolTipText("Autopilot speed managed");
        menu_item.addActionListener(this);
        ap_speed_submenu.add(menu_item);

        menu_item = new JMenuItem(XHSISettings.ACTION_AP_SPEED_SEL);
        menu_item.setToolTipText("Autopilot speed selected");
        menu_item.addActionListener(this);
        ap_speed_submenu.add(menu_item);
        
        ap_menu.add(ap_speed_submenu);
        
        // Heading Sub Menu
        JMenu ap_heading_submenu = new JMenu("Heading");
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_SET_HEADING);
        menu_item.setToolTipText("Set the autopilot heading bug");
        menu_item.addActionListener(this);
        ap_heading_submenu.add(menu_item);

        menu_item = new JMenuItem(XHSISettings.ACTION_AP_HDG_MODE);
        menu_item.setToolTipText("Follow the heading bug");
        menu_item.addActionListener(this);
        ap_heading_submenu.add(menu_item);
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_NAV_MODE);
        menu_item.setToolTipText("Follow the flight plan");
        menu_item.addActionListener(this);
        ap_heading_submenu.add(menu_item);

        menu_item = new JMenuItem(XHSISettings.ACTION_AP_VOR_MODE);
        menu_item.setToolTipText("Follow tuned VOR 1");
        menu_item.addActionListener(this);
        ap_heading_submenu.add(menu_item);
        
        ap_menu.add(ap_heading_submenu);
        
        // Vertical Velocity Sub Menu
        JMenu ap_vv_submenu = new JMenu("Vertical Velocity");
        
        menu_item = new JMenuItem(XHSISettings.ACTION_AP_SET_VS);
        menu_item.setToolTipText("Set the autopilot vertical velocity target");
        menu_item.addActionListener(this);
        ap_vv_submenu.add(menu_item);
        
        ap_menu.add(ap_vv_submenu);
        
        ap_menu.addSeparator();
        
        // TODO :
        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_AP_AP_SWITCH);
        checkbox_menu_item.setToolTipText("Set the autopilot on/off");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(false);
        ap_menu.add(checkbox_menu_item);
        // keep a reference
        this.checkbox_autopilot = checkbox_menu_item;

        checkbox_menu_item = new JCheckBoxMenuItem(XHSISettings.ACTION_AP_FD_SWITCH);
        checkbox_menu_item.setToolTipText("Set the fligth director on/off");
        checkbox_menu_item.addActionListener(this);
        checkbox_menu_item.setSelected(false);
        ap_menu.add(checkbox_menu_item);
        // keep a reference
        this.checkbox_flight_director = checkbox_menu_item;

        
        // add the "AP" menu to the menubar
        menu_bar.add(ap_menu);
        
        
        
        /*
         *  define the "EICAS" menu
         */
        JMenu xhsi_eicas_menu = new JMenu("EICAS");

//        engine_type = 0;
//        if ( XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_MAP) )
//            engine_type = XHSISettings.ENGINE_TYPE_MAP;
//        else if ( XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_TRQ) )
//            engine_type = XHSISettings.ENGINE_TYPE_TRQ;
////        else if ( XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_EPR) )
////            eicas_type = XHSISettings.EICAS_TYPE_EPR;
//        else
//            engine_type = XHSISettings.ENGINE_TYPE_N1;
        
        ButtonGroup eicas_group = new ButtonGroup();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_ENGINE_TYPE_N1);
        radio_button_menu_item.setToolTipText("Engine type N1/EGT/...");
        radio_button_menu_item.addActionListener(this);
//        radio_button_menu_item.setSelected(engine_type == XHSISettings.ENGINE_TYPE_N1);
        radio_button_menu_item.setSelected(true);
        xhsi_eicas_menu.add(radio_button_menu_item);
        eicas_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_engine_n1 = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_ENGINE_TYPE_EPR);
        radio_button_menu_item.setToolTipText("Engine type EPR/...");
        radio_button_menu_item.addActionListener(this);
//        radio_button_menu_item.setSelected(eicas_type == XHSISettings.EICAS_TYPE_EPR);
        radio_button_menu_item.setSelected(false);
        xhsi_eicas_menu.add(radio_button_menu_item);
        eicas_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_engine_epr = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_ENGINE_TYPE_TRQ);
        radio_button_menu_item.setToolTipText("Engine type TRQ/ITT/PROP/...");
        radio_button_menu_item.addActionListener(this);
//        radio_button_menu_item.setSelected(engine_type == XHSISettings.ENGINE_TYPE_TRQ);
        radio_button_menu_item.setSelected(false);
        xhsi_eicas_menu.add(radio_button_menu_item);
        eicas_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_engine_trq = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_ENGINE_TYPE_MAP);
        radio_button_menu_item.setToolTipText("Engine type MAP/RPM/FF/..");
        radio_button_menu_item.addActionListener(this);
//        radio_button_menu_item.setSelected(engine_type == XHSISettings.ENGINE_TYPE_MAP);
        radio_button_menu_item.setSelected(false);
        xhsi_eicas_menu.add(radio_button_menu_item);
        eicas_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_engine_map = radio_button_menu_item;

        xhsi_eicas_menu.addSeparator();

        // define the "TRQ scale" submenu
        JMenu trq_scale_submenu = new JMenu("TRQ units");

        ButtonGroup trq_scale_group = new ButtonGroup();

        // define the menu items, and add them to the "Fix" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_TRQ_SCALE_LBFT);
        radio_button_menu_item.setToolTipText("TRQ in LbFt");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        trq_scale_submenu.add(radio_button_menu_item);
        trq_scale_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_trq_scale_lbft = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_TRQ_SCALE_NM);
        radio_button_menu_item.setToolTipText("TRQ in Nm");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        trq_scale_submenu.add(radio_button_menu_item);
        trq_scale_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_trq_scale_nm = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_TRQ_SCALE_PERCENT);
        radio_button_menu_item.setToolTipText("TRQ in %");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        trq_scale_submenu.add(radio_button_menu_item);
        trq_scale_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_trq_scale_percent = radio_button_menu_item;

        // Add the TRQ scale submenu to the EICAS menu
        xhsi_eicas_menu.add(trq_scale_submenu);
        
        // define the "Max TRQ" submenu
        JMenu max_trq_submenu = new JMenu("Max TRQ");

        ButtonGroup max_trq_group = new ButtonGroup();

        // define the menu items, and add them to the "Fix" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MAX_TRQ_AUTO);
        radio_button_menu_item.setToolTipText("Get the value for max TRQ from X-Plane");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        max_trq_submenu.add(radio_button_menu_item);
        max_trq_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from FixDialog.java
        radiobutton_max_trq_auto = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MAX_TRQ_SET);
        radio_button_menu_item.setToolTipText("Set the value for max TRQ");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        max_trq_submenu.add(radio_button_menu_item);
        max_trq_group.add(radio_button_menu_item);
        // keep a reference to the radiobutton to set or clear it in a non-standard way from FixDialog.java
        radiobutton_max_trq_set = radio_button_menu_item;

        // add the "Max TRQ" submenu to the "EICAS" menu
        xhsi_eicas_menu.add(max_trq_submenu);
        
        xhsi_eicas_menu.addSeparator();

        // define the "Fuel units" submenu
        JMenu fuel_units_submenu = new JMenu("Fuel units");

        ButtonGroup fuel_units_group = new ButtonGroup();

        // define the menu items, and add them to the "Fix" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_FUEL_UNITS_KG);
        radio_button_menu_item.setToolTipText("Kg");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        fuel_units_submenu.add(radio_button_menu_item);
        fuel_units_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_fuel_units_kg = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_FUEL_UNITS_LBS);
        radio_button_menu_item.setToolTipText("Lb");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        fuel_units_submenu.add(radio_button_menu_item);
        fuel_units_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_fuel_units_lbs = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_FUEL_UNITS_USG);
        radio_button_menu_item.setToolTipText("US Gallon");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        fuel_units_submenu.add(radio_button_menu_item);
        fuel_units_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_fuel_units_usg = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_FUEL_UNITS_LTR);
        radio_button_menu_item.setToolTipText("Liter");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        fuel_units_submenu.add(radio_button_menu_item);
        fuel_units_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_fuel_units_ltr = radio_button_menu_item;

        // Add the Fuel units submenu to the EICAS menu
        xhsi_eicas_menu.add(fuel_units_submenu);
        
        // define the "Temp units" submenu
        JMenu temp_units_submenu = new JMenu("Temp units");

        ButtonGroup temp_units_group = new ButtonGroup();

        // define the menu items, and add them to the "Fix" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_TEMP_UNITS_C);
        radio_button_menu_item.setToolTipText("Celcius");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        temp_units_submenu.add(radio_button_menu_item);
        temp_units_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_temp_units_c = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_TEMP_UNITS_F);
        radio_button_menu_item.setToolTipText("Farhenheit");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        temp_units_submenu.add(radio_button_menu_item);
        temp_units_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_temp_units_f = radio_button_menu_item;

        // Add the Temp units submenu to the EICAS menu
        xhsi_eicas_menu.add(temp_units_submenu);

        
        xhsi_eicas_menu.addSeparator();

//        menu_item = new JMenuItem(XHSISettings.ACTION_ESTIMATE_FUEL_CAPACITY);
//        menu_item.setToolTipText("Estimate the aircraft's total fuel capacity");
//        menu_item.addActionListener(this);
//        xhsi_eicas_menu.add(menu_item);
//
//        menu_item = new JMenuItem(XHSISettings.ACTION_SET_FUEL_CAPACITY);
//        menu_item.setToolTipText("Set the aircraft's total fuel capacity manually");
//        menu_item.addActionListener(this);
//        xhsi_eicas_menu.add(menu_item);
//
//        xhsi_eicas_menu.addSeparator();

        menu_item = new JMenuItem(XHSISettings.ACTION_RESET_MAX_FF);
        menu_item.setToolTipText("Reset the range of the Fuel Flow dials");
        menu_item.addActionListener(this);
        xhsi_eicas_menu.add(menu_item);

        // add the "EICAS" menu to the menubar
        menu_bar.add(xhsi_eicas_menu);


        // define the "MFD" menu
        JMenu xhsi_mfd_menu = new JMenu("MFD");

        ButtonGroup mfd_group = new ButtonGroup();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_ARPT_CHART);
        radio_button_menu_item.setToolTipText("Airport Chart");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_arpt = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_FPLN);
        radio_button_menu_item.setToolTipText("Flight Plan");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_fpln = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_RTU);
        radio_button_menu_item.setToolTipText("Radio Tuning Unit Display");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_rtu = radio_button_menu_item;

        xhsi_mfd_menu.addSeparator();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_LOWER_EICAS);
        radio_button_menu_item.setToolTipText("Lower EICAS");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_eicas = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_BLEED);
        radio_button_menu_item.setToolTipText("Bleed air");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_bleed = radio_button_menu_item;
        
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_CAB_PRESS);
        radio_button_menu_item.setToolTipText("Cabin pressure");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_cab_press = radio_button_menu_item;
        
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_ELEC);
        radio_button_menu_item.setToolTipText("Electrics");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_elec = radio_button_menu_item;
               
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_HYDR);
        radio_button_menu_item.setToolTipText("Hydraulics");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_hydr = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_FUEL);
        radio_button_menu_item.setToolTipText("Fuel");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_fuel = radio_button_menu_item;
                   
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_APU);
        radio_button_menu_item.setToolTipText("Auxiliary Power Unit");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_apu = radio_button_menu_item;
        
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_COND);
        radio_button_menu_item.setToolTipText("Air conditionning");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_cond = radio_button_menu_item;
            
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_DOOR_OXY);
        radio_button_menu_item.setToolTipText("Doors and oxygen");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_door_oxy = radio_button_menu_item;
                             
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_WHEELS);
        radio_button_menu_item.setToolTipText("Wheels");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_wheels = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_FCTL);
        radio_button_menu_item.setToolTipText("Flight Controls");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_fctl = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_SYSTEM);
        radio_button_menu_item.setToolTipText("Systems - Cruise");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_sys = radio_button_menu_item;
        
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_MFD_STATUS);
        radio_button_menu_item.setToolTipText("Status");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        mfd_group.add(radio_button_menu_item);
        xhsi_mfd_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_mfd_status = radio_button_menu_item;
  
        
        // add the "MFD" menu to the menubar
        menu_bar.add(xhsi_mfd_menu);


        // define the "CDU" menu
        JMenu xhsi_cdu_menu = new JMenu("CDU");

        ButtonGroup cdu_group = new ButtonGroup();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CDU_AIRCRAFT_OR_DUMMY);
        radio_button_menu_item.setToolTipText("Aircraft's custom FMC, if available");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        cdu_group.add(radio_button_menu_item);
        xhsi_cdu_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_cdu_aircraft = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CDU_XFMC);
        radio_button_menu_item.setToolTipText("X-FMC");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        cdu_group.add(radio_button_menu_item);
        xhsi_cdu_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_cdu_xfmc = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CDU_UFMC);
        radio_button_menu_item.setToolTipText("UFMC or X737FMC");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        // TODO : for beta 9 version
        // cdu_group.add(radio_button_menu_item);
        // xhsi_cdu_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_cdu_ufmc = radio_button_menu_item;

        xhsi_cdu_menu.addSeparator();
        
        // define the "CDU Side" submenu
        JMenu cdu_side_submenu = new JMenu("CDU Side");

        ButtonGroup cdu_side_group = new ButtonGroup();

        // define the menu items, and add them to the "Fix" menu
        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CDU_LEFT);
        radio_button_menu_item.setToolTipText("Display Captain CDU (Left)");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        cdu_side_submenu.add(radio_button_menu_item);
        cdu_side_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_cdu_left = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CDU_RIGHT);
        radio_button_menu_item.setToolTipText("Display First Officer CDU (Right)");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        cdu_side_submenu.add(radio_button_menu_item);
        cdu_side_group.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_cdu_right = radio_button_menu_item;

        // Add the Temp units submenu to the EICAS menu
        xhsi_cdu_menu.add(cdu_side_submenu);

        
        // add the "CDU" menu to the menubar
        menu_bar.add(xhsi_cdu_menu);


        // define the "Clock" menu
        JMenu xhsi_clock_menu = new JMenu("Clock");

        ButtonGroup clock_group = new ButtonGroup();

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CLOCK_UTC);
        radio_button_menu_item.setToolTipText("Clock shows UTC");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(true);
        clock_group.add(radio_button_menu_item);
        xhsi_clock_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_clock_utc = radio_button_menu_item;

        radio_button_menu_item = new JRadioButtonMenuItem(XHSISettings.ACTION_CLOCK_LT);
        radio_button_menu_item.setToolTipText("Clock shows Local Time");
        radio_button_menu_item.addActionListener(this);
        radio_button_menu_item.setSelected(false);
        clock_group.add(radio_button_menu_item);
        xhsi_clock_menu.add(radio_button_menu_item);
        // keep a reference
        this.radio_button_clock_lt = radio_button_menu_item;

        xhsi_clock_menu.addSeparator();

        menu_item = new JMenuItem(XHSISettings.ACTION_CHR_START_STOP_RESET);
        menu_item.setToolTipText("Start/Stop/Reset the chronograph");
        menu_item.addActionListener(this);
        xhsi_clock_menu.add(menu_item);

        // add the "Clock" menu to the menubar
        menu_bar.add(xhsi_clock_menu);


    }


    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals(XHSISettings.ACTION_STYLE_BOEING)) {
            style = Avionics.STYLE_BOEING;
            this.avionics.set_instrument_style(style);
        } else if (command.equals(XHSISettings.ACTION_STYLE_AIRBUS)) {
            style = Avionics.STYLE_AIRBUS;
            this.avionics.set_instrument_style(style);

        } else if (command.equals(XHSISettings.ACTION_SOURCE_NAV1)) {
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
        } else if (command.equals(XHSISettings.ACTION_SYNC_CRS1)) {
            NavigationRadio nav1 = this.avionics.get_nav_radio(1);
//            if ( nav1.receiving() ) {
                if ( nav1.freq_is_localizer() ) {
                    // sync to Localizer frontcourse
                    this.avionics.set_nav1_obs( this.avionics.nav1_course() );
                } else {
                    // sync to VOR Direct-To course
                    this.avionics.set_nav1_obs( ( nav1.get_radial() + 180.0f ) % 360.0f );
                }
//            }

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
        } else if (command.equals(XHSISettings.ACTION_SYNC_CRS2)) {
            NavigationRadio nav2 = this.avionics.get_nav_radio(2);
//            if ( nav2.receiving() ) {
                if ( nav2.freq_is_localizer() ) {
                    // sync to Localizer frontcourse
                    this.avionics.set_nav2_obs( this.avionics.nav2_course() );
                } else {
                    // sync to VOR Direct-To course
                    this.avionics.set_nav2_obs( ( nav2.get_radial() + 180.0f ) % 360.0f );
                }
//            }

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
            this.avionics.set_xpdr_mode(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_STBY)) {
            xpdr = Avionics.XPDR_STBY;
            this.avionics.set_xpdr_mode(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_ON)) {
            xpdr = Avionics.XPDR_ON;
            this.avionics.set_xpdr_mode(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_TA)) {
            xpdr = Avionics.XPDR_TA;
            this.avionics.set_xpdr_mode(xpdr);
        } else if (command.equals(XHSISettings.ACTION_XPDR_TARA)) {
            xpdr = Avionics.XPDR_TARA;
            this.avionics.set_xpdr_mode(xpdr);

        } else if (command.equals(XHSISettings.ACTION_HOLDING_HIDE)) {
            // Hide Holding pattern
            draw_holding = false;
        } else if (command.equals(XHSISettings.ACTION_HOLDING_SHOW)) {
            // Set & show holding pattern
            this.holding_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.holding_dialog.getWidth()/2, this.main_frame.getY()+120);
            this.holding_dialog.setVisible(true);
            this.holding_dialog.pack();

        } else if (command.equals(XHSISettings.ACTION_FIX_HIDE)) {
            // Hide CDU FIX
            draw_cdu_fix = false;
        } else if (command.equals(XHSISettings.ACTION_FIX_SHOW)) {
            // Set & show CDU FIX
            this.fix_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.fix_dialog.getWidth()/2, this.main_frame.getY()+150);
            this.fix_dialog.setVisible(true);
            this.fix_dialog.pack();
        } else if (command.equals(XHSISettings.ACTION_AP_SET_ALTITUDE)) {            
            // Set & show altitude dialog
            this.altitude_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.altitude_dialog.getWidth()/2, this.main_frame.getY()+120);
            this.altitude_dialog.init_altitude();
            this.altitude_dialog.setVisible(true);
            this.altitude_dialog.pack();
            
        } else if (command.equals(XHSISettings.ACTION_AP_LEVEL_CHG)) {
        	this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_ALT_SEL);
        } else if (command.equals(XHSISettings.ACTION_AP_LEVEL_CHG_MNG)) {
        	this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_ALT_MNG);
        } else if (command.equals(XHSISettings.ACTION_AP_SET_SPEED)) {
            this.speed_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.speed_dialog.getWidth()/2, this.main_frame.getY()+120);
            this.speed_dialog.init_altitude();
            this.speed_dialog.setVisible(true);
            this.speed_dialog.pack();
        } else if (command.equals(XHSISettings.ACTION_AP_SPEED_MNG)) {
        	this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_SPD_MNG);
        } else if (command.equals(XHSISettings.ACTION_AP_SPEED_SEL)) {
        	this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_SPD_SEL);
        } else if (command.equals(XHSISettings.ACTION_AP_SET_HEADING)) {           
            // Set & show heading dialog
            this.heading_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.heading_dialog.getWidth()/2, this.main_frame.getY()+120);
            this.heading_dialog.init_heading();
            this.heading_dialog.setVisible(true);
            this.heading_dialog.pack();
        } else if (command.equals(XHSISettings.ACTION_AP_HDG_MODE)) {
        	this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_HDG_SEL);
        } else if (command.equals(XHSISettings.ACTION_AP_NAV_MODE)) {
        	this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_HDG_MNG);
        } else if (command.equals(XHSISettings.ACTION_AP_VOR_MODE)) {
        } else if (command.equals(XHSISettings.ACTION_AP_SET_VS)) {
            // Set & show vertical speed dialog
            this.v_speed_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.v_speed_dialog.getWidth()/2, this.main_frame.getY()+120);
            this.v_speed_dialog.init_altitude();
            this.v_speed_dialog.setVisible(true);
            this.v_speed_dialog.pack();
        } else if (command.equals(XHSISettings.ACTION_AP_VS_MODE)) {
        } else if (command.equals(XHSISettings.ACTION_AP_FD_SWITCH)) { 
        	// TODO: Flight director for all aicrafts
    		boolean fd_on = this.avionics.autopilot_mode() >= 1;	    		
    		if ( this.avionics.is_qpac()) { 
    			fd_on = this.avionics.qpac_fd_on();
    			this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_EFIS_CAPT_FD);
    		} else {
    			if (fd_on) {
    				// This will set both autopilot and FD to OFF
    				this.avionics.set_autopilot_mode(0);
    			} else {
    				this.avionics.set_autopilot_mode(1);;
    			}
    		}    		    		
        } else if (command.equals(XHSISettings.ACTION_AP_AP_SWITCH)) {
        	// TODO: Autopilot switch for all aircrafts
    		boolean ap_on;
    		if (this.avionics.is_qpac()) {
    			// TODO: Set AP1 only
    			ap_on = this.avionics.qpac_ap1() || this.avionics.qpac_ap2();
    			this.avionics.get_aircraft().get_sim_command().send(SimCommand.CMD_FCU_AP1);
    		} else if (this.avionics.is_jar_a320neo()) {
    			ap_on = this.avionics.jar_a320neo_ap1() || this.avionics.jar_a320neo_ap2();
    		} else {
    			ap_on = this.avionics.autopilot_mode() > 1;
    			if (ap_on) {
    				this.avionics.set_autopilot_mode(1);
    			} else {
    				// This will set both autopilot and FD to ON
    				this.avionics.set_autopilot_mode(2);;
    			}    			
    		}        
        } else if (command.equals(XHSISettings.ACTION_AP_ILS_ON)) {
        } else if (command.equals(XHSISettings.ACTION_AP_ILS_OFF)) {
        } else if (command.equals(XHSISettings.ACTION_AP_LOC)) {
        } else if (command.equals(XHSISettings.ACTION_AP_GS)) {
        } else if (command.equals(XHSISettings.ACTION_AP_ATHR_ON)) {
        } else if (command.equals(XHSISettings.ACTION_AP_ATHR_OFF)) {
        } else if (command.equals(XHSISettings.ACTION_AP_METRIC_ON)) {
        } else if (command.equals(XHSISettings.ACTION_AP_METRIC_OFF)) {
        } else if (command.equals(XHSISettings.ACTION_AP_TRK_FPA_ON)) {
        } else if (command.equals(XHSISettings.ACTION_AP_TRK_FPA_OFF)) {
        } else if (command.equals(XHSISettings.ACTION_AP_TRK_MACH_ON)) {
        } else if (command.equals(XHSISettings.ACTION_AP_TRK_MACH_OFF)) {
        	
        	
        } else if (command.equals(XHSISettings.ACTION_ENGINE_TYPE_N1)) {
            engine_type = XHSISettings.ENGINE_TYPE_N1;
            this.avionics.set_engine_type(engine_type);
        } else if (command.equals(XHSISettings.ACTION_ENGINE_TYPE_EPR)) {
            engine_type = XHSISettings.ENGINE_TYPE_EPR;
            this.avionics.set_engine_type(engine_type);
        } else if (command.equals(XHSISettings.ACTION_ENGINE_TYPE_TRQ)) {
            engine_type = XHSISettings.ENGINE_TYPE_TRQ;
            this.avionics.set_engine_type(engine_type);
        } else if (command.equals(XHSISettings.ACTION_ENGINE_TYPE_MAP)) {
            engine_type = XHSISettings.ENGINE_TYPE_MAP;
            this.avionics.set_engine_type(engine_type);

        } else if (command.equals(XHSISettings.ACTION_TRQ_SCALE_LBFT)) {
            trq_scale = XHSISettings.TRQ_SCALE_LBFT;
            this.avionics.set_trq_scale(trq_scale);
        } else if (command.equals(XHSISettings.ACTION_TRQ_SCALE_NM)) {
            trq_scale = XHSISettings.TRQ_SCALE_NM;
            this.avionics.set_trq_scale(trq_scale);
        } else if (command.equals(XHSISettings.ACTION_TRQ_SCALE_PERCENT)) {
            trq_scale = XHSISettings.TRQ_SCALE_PERCENT;
            this.avionics.set_trq_scale(trq_scale);

        } else if (command.equals(XHSISettings.ACTION_MAX_TRQ_AUTO)) {
            this.avionics.set_max_trq_override(0.0f);
        } else if (command.equals(XHSISettings.ACTION_MAX_TRQ_SET)) {
            // Set & show holding pattern
            this.max_trq_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.max_trq_dialog.getWidth()/2, this.main_frame.getY()+120);
            this.max_trq_dialog.setVisible(true);
            this.max_trq_dialog.pack();

        } else if (command.equals(XHSISettings.ACTION_FUEL_UNITS_KG)) {
            fuel_units = XHSISettings.FUEL_UNITS_KG;
            this.avionics.set_fuel_units(fuel_units);
        } else if (command.equals(XHSISettings.ACTION_FUEL_UNITS_LBS)) {
            fuel_units = XHSISettings.FUEL_UNITS_LBS;
            this.avionics.set_fuel_units(fuel_units);
        } else if (command.equals(XHSISettings.ACTION_FUEL_UNITS_USG)) {
            fuel_units = XHSISettings.FUEL_UNITS_USG;
            this.avionics.set_fuel_units(fuel_units);
        } else if (command.equals(XHSISettings.ACTION_FUEL_UNITS_LTR)) {
            fuel_units = XHSISettings.FUEL_UNITS_LTR;
            this.avionics.set_fuel_units(fuel_units);

        } else if (command.equals(XHSISettings.ACTION_TEMP_UNITS_C)) {
            temp_units = XHSISettings.TEMP_UNITS_C;
            this.avionics.set_temp_units(temp_units);
        } else if (command.equals(XHSISettings.ACTION_TEMP_UNITS_F)) {
            temp_units = XHSISettings.TEMP_UNITS_F;
            this.avionics.set_temp_units(temp_units);
            
//        } else if (command.equals(XHSISettings.ACTION_ESTIMATE_FUEL_CAPACITY)) {
//            // Estimate total fuel capacity
//            this.avionics.get_aircraft().estimate_fuel_capacity();

//        } else if (command.equals(XHSISettings.ACTION_SET_FUEL_CAPACITY)) {
//            // Set total fuel capacity manually
//            // update the fields in the dialog box
//            this.fuel_dialog.init_capacity();
//            this.fuel_dialog.setLocation(this.main_frame.getX()+this.main_frame.getWidth()/2-this.fuel_dialog.getWidth()/2, this.main_frame.getY()+100);
//            this.fuel_dialog.setVisible(true);
//            this.fuel_dialog.pack();

        } else if (command.equals(XHSISettings.ACTION_RESET_MAX_FF)) {
            this.avionics.get_aircraft().reset_max_FF();

        } else if (command.equals(XHSISettings.ACTION_MFD_ARPT_CHART)) {
            mfd_mode = Avionics.MFD_MODE_ARPT;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_FPLN)) {
            mfd_mode = Avionics.MFD_MODE_FPLN;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_RTU)) {
            mfd_mode = Avionics.MFD_MODE_RTU;
            this.avionics.set_mfd_mode(mfd_mode);

        } else if (command.equals(XHSISettings.ACTION_MFD_LOWER_EICAS)) {
            mfd_mode = Avionics.MFD_MODE_EICAS;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_BLEED)) {
            mfd_mode = Avionics.MFD_MODE_BLEED;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_CAB_PRESS)) {
            mfd_mode = Avionics.MFD_MODE_CAB_PRESS;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_ELEC)) {
            mfd_mode = Avionics.MFD_MODE_ELEC;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_HYDR)) {
            mfd_mode = Avionics.MFD_MODE_HYDR;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_FUEL)) {
            mfd_mode = Avionics.MFD_MODE_FUEL;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_APU)) {
            mfd_mode = Avionics.MFD_MODE_APU;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_COND)) {
            mfd_mode = Avionics.MFD_MODE_COND;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_DOOR_OXY)) {
            mfd_mode = Avionics.MFD_MODE_DOOR_OXY;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_WHEELS)) {
            mfd_mode = Avionics.MFD_MODE_WHEELS;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_FCTL)) {
            mfd_mode = Avionics.MFD_MODE_FCTL;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_SYSTEM)) {
            mfd_mode = Avionics.MFD_MODE_SYS;
            this.avionics.set_mfd_mode(mfd_mode);
        } else if (command.equals(XHSISettings.ACTION_MFD_STATUS)) {
            mfd_mode = Avionics.MFD_MODE_STATUS;
            this.avionics.set_mfd_mode(mfd_mode);

        } else if (command.equals(XHSISettings.ACTION_CDU_AIRCRAFT_OR_DUMMY)) {
            cdu_source = Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY;
            this.avionics.set_cdu_source(cdu_source);
        } else if (command.equals(XHSISettings.ACTION_CDU_XFMC)) {
            cdu_source = Avionics.CDU_SOURCE_XFMC;
            this.avionics.set_cdu_source(cdu_source);
        } else if (command.equals(XHSISettings.ACTION_CDU_UFMC)) {
            cdu_source = Avionics.CDU_SOURCE_UFMC;
            this.avionics.set_cdu_source(cdu_source);
            
            //XHSI_CDU_SIDE
        } else if (command.equals(XHSISettings.ACTION_CDU_LEFT)) {
            cdu_side = Avionics.CDU_LEFT;
            this.avionics.set_cdu_side(cdu_side);
        } else if (command.equals(XHSISettings.ACTION_CDU_RIGHT)) {
            cdu_side = Avionics.CDU_RIGHT;
            this.avionics.set_cdu_side(cdu_side);
            
        } else if (command.equals(XHSISettings.ACTION_CLOCK_UTC)) {
            clock_mode = Avionics.CLOCK_MODE_UTC;
            this.avionics.set_clock_mode(clock_mode);
        } else if (command.equals(XHSISettings.ACTION_CLOCK_LT)) {
            clock_mode = Avionics.CLOCK_MODE_LT;
            this.avionics.set_clock_mode(clock_mode);
        } else if (command.equals(XHSISettings.ACTION_CHR_START_STOP_RESET)) {
            this.avionics.chr_control(Avionics.CHR_CONTROL_START_STOP_RESET);

        } else {
            for (int i=0; i<this.range_list.length; i++) {
                if (command.equals(this.range_list[i])) {
                    // Range override
                    map_range = Integer.parseInt(command);
                    map_range_index = i;
                    this.avionics.set_range_index(i);
                    map_zoomin = false;
                    this.avionics.set_zoomin(map_zoomin);
                }
            }
            for (int i=0; i<this.zoomin_range_list.length; i++) {
                if (command.equals(this.zoomin_range_list[i])) {
                    // Range override
                    // A trick: use the standard range list, but with map_zoomin set to true
                    map_range = Integer.parseInt(this.range_list[i]);
                    map_range_index = i;
                    this.avionics.set_range_index(i);
                    map_zoomin = true;
                    this.avionics.set_zoomin(map_zoomin);
                }
            }

        }

    }


    public void update(Avionics avionics) {

        // settings have been changed in X-Plane, change the selections in the menu accordingly

        XHSIPreferences prefs = XHSIPreferences.get_instance();

        boolean switchable;
//        this.checkbox_avionics_power.setSelected(avionics.power());

        int new_style = avionics.get_instrument_style();
        // WFT? : if (old_style != new_style) { style=new_style; old_style=new_style; }
        this.radio_button_style_boeing.setSelected( new_style == Avionics.STYLE_BOEING );
        this.radio_button_style_airbus.setSelected( new_style == Avionics.STYLE_AIRBUS );
        switchable = prefs.get_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE).equals(XHSIPreferences.INSTRUMENT_STYLE_SWITCHABLE);
        this.radio_button_style_boeing.setEnabled( switchable );
        this.radio_button_style_airbus.setEnabled( switchable );

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
        if ( avionics.map_zoomin() ) {
            for (int i=0; i<this.zoomin_range_list.length; i++) {
                this.radio_button_range[i].setSelected(  new_range_index == i );
            }
            for (int i=0; i<this.range_list.length; i++) {
                this.radio_button_range[i+7].setSelected(  false );
            }
        } else {
            for (int i=0; i<this.zoomin_range_list.length; i++) {
                this.radio_button_range[i].setSelected(  false );
            }
            for (int i=0; i<this.range_list.length; i++) {
                this.radio_button_range[i+7].setSelected(  new_range_index == i );
            }
        }
//        this.checkbox_map_zoomin.setSelected( avionics.map_zoomin() );

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
        
        // Autopilot
        // TODO: A320/B737 modes
        int new_ap = avionics.autopilot_mode();
        this.checkbox_autopilot.setSelected(new_ap>1);
        this.checkbox_flight_director.setSelected(new_ap>0);

        int new_mfd_mode = avionics.get_mfd_mode();
        this.radio_button_mfd_arpt.setSelected( new_mfd_mode == Avionics.MFD_MODE_ARPT );
        this.radio_button_mfd_fpln.setSelected( new_mfd_mode == Avionics.MFD_MODE_FPLN );
        this.radio_button_mfd_rtu.setSelected( new_mfd_mode == Avionics.MFD_MODE_RTU );
        this.radio_button_mfd_eicas.setSelected( new_mfd_mode == Avionics.MFD_MODE_EICAS );
        this.radio_button_mfd_apu.setSelected( new_mfd_mode == Avionics.MFD_MODE_APU );
        this.radio_button_mfd_fctl.setSelected( new_mfd_mode == Avionics.MFD_MODE_FCTL );
        this.radio_button_mfd_sys.setSelected( new_mfd_mode == Avionics.MFD_MODE_SYS );
        this.radio_button_mfd_elec.setSelected( new_mfd_mode == Avionics.MFD_MODE_ELEC );
        this.radio_button_mfd_wheels.setSelected( new_mfd_mode == Avionics.MFD_MODE_WHEELS );
        this.radio_button_mfd_door_oxy.setSelected( new_mfd_mode == Avionics.MFD_MODE_DOOR_OXY );
        this.radio_button_mfd_bleed.setSelected( new_mfd_mode == Avionics.MFD_MODE_BLEED );
        this.radio_button_mfd_cond.setSelected( new_mfd_mode == Avionics.MFD_MODE_COND );
        this.radio_button_mfd_fuel.setSelected( new_mfd_mode == Avionics.MFD_MODE_FUEL );
        this.radio_button_mfd_cab_press.setSelected( new_mfd_mode == Avionics.MFD_MODE_CAB_PRESS );
        this.radio_button_mfd_hydr.setSelected( new_mfd_mode == Avionics.MFD_MODE_HYDR );
        this.radio_button_mfd_status.setSelected( new_mfd_mode == Avionics.MFD_MODE_STATUS );
        
        switchable = prefs.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_SWITCHABLE)
        		|| prefs.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_LINKED)
        		|| prefs.get_preference(XHSIPreferences.PREF_INSTRUMENT_POSITION).equals(XHSIPreferences.INSTRUCTOR);
        this.radio_button_mfd_arpt.setEnabled( switchable );
        this.radio_button_mfd_fpln.setEnabled( switchable );
        this.radio_button_mfd_eicas.setEnabled( switchable );
        this.radio_button_mfd_rtu.setEnabled( switchable );
        this.radio_button_mfd_apu.setEnabled( switchable );
        this.radio_button_mfd_fctl.setEnabled( switchable );
        this.radio_button_mfd_sys.setEnabled( switchable );
        this.radio_button_mfd_elec.setEnabled( switchable );
        this.radio_button_mfd_wheels.setEnabled( switchable );
        this.radio_button_mfd_door_oxy.setEnabled( switchable );
        this.radio_button_mfd_bleed.setEnabled( switchable );
        this.radio_button_mfd_cond.setEnabled( switchable );
        this.radio_button_mfd_fuel.setEnabled( switchable );
        this.radio_button_mfd_cab_press.setEnabled( switchable );
        this.radio_button_mfd_hydr.setEnabled( switchable );
        this.radio_button_mfd_status.setEnabled( switchable );

        switchable = prefs.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_SWITCHABLE);
        this.radio_button_cdu_aircraft.setEnabled( switchable );
        this.radio_button_cdu_xfmc.setEnabled( switchable );
        this.radio_button_cdu_ufmc.setEnabled( switchable );
        int new_cdu_source = avionics.get_cdu_source();
        this.radio_button_cdu_aircraft.setSelected( new_cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY );
        this.radio_button_cdu_xfmc.setSelected( new_cdu_source == Avionics.CDU_SOURCE_XFMC );
        this.radio_button_cdu_ufmc.setSelected( new_cdu_source == Avionics.CDU_SOURCE_UFMC );

        switchable = prefs.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_SWITCHABLE);
        this.radio_button_cdu_left.setEnabled( switchable );
        this.radio_button_cdu_right.setEnabled( switchable );
        int new_cdu_side = avionics.get_cdu_side();
        this.radio_button_cdu_left.setSelected( new_cdu_side == Avionics.CDU_LEFT );
        this.radio_button_cdu_right.setSelected( new_cdu_side == Avionics.CDU_RIGHT );
        
        boolean new_clock_mode = avionics.clock_shows_utc();
        this.radio_button_clock_utc.setSelected(new_clock_mode);
        this.radio_button_clock_lt.setSelected(!new_clock_mode);

        int new_engine_type = avionics.get_engine_type();
        this.radio_button_engine_n1.setSelected( new_engine_type == XHSISettings.ENGINE_TYPE_N1 );
        this.radio_button_engine_epr.setSelected( new_engine_type == XHSISettings.ENGINE_TYPE_EPR );
        this.radio_button_engine_trq.setSelected( new_engine_type == XHSISettings.ENGINE_TYPE_TRQ );
        this.radio_button_engine_map.setSelected( new_engine_type == XHSISettings.ENGINE_TYPE_MAP );
        switchable = prefs.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_SWITCHABLE);
        this.radio_button_engine_n1.setEnabled( switchable );
        this.radio_button_engine_epr.setEnabled( switchable );
        this.radio_button_engine_trq.setEnabled( switchable );
        this.radio_button_engine_map.setEnabled( switchable );

        int new_trq_scale = avionics.get_trq_scale();
        this.radio_button_trq_scale_lbft.setSelected( new_trq_scale == XHSISettings.TRQ_SCALE_LBFT );
        this.radio_button_trq_scale_nm.setSelected( new_trq_scale == XHSISettings.TRQ_SCALE_NM );
        this.radio_button_trq_scale_percent.setSelected( new_trq_scale == XHSISettings.TRQ_SCALE_PERCENT );
        switchable = ( new_engine_type == XHSISettings.ENGINE_TYPE_TRQ ) && prefs.get_preference(XHSIPreferences.PREF_TRQ_SCALE).equals(XHSIPreferences.TRQ_SCALE_SWITCHABLE);
        this.radio_button_trq_scale_lbft.setEnabled( switchable );
        this.radio_button_trq_scale_nm.setEnabled( switchable );
        this.radio_button_trq_scale_percent.setEnabled( switchable );
        
        boolean new_trq_max_override = ( avionics.get_aircraft().get_max_TRQ_override() != 0.0f );
        this.radiobutton_max_trq_auto.setSelected( ! new_trq_max_override );
        this.radiobutton_max_trq_set.setSelected( new_trq_max_override );
        switchable = ( new_engine_type == XHSISettings.ENGINE_TYPE_TRQ );
        this.radiobutton_max_trq_auto.setEnabled( switchable );
        this.radiobutton_max_trq_set.setEnabled( switchable );
        
        int new_fuel_units = avionics.get_fuel_units();
        this.radio_button_fuel_units_kg.setSelected( new_fuel_units == XHSISettings.FUEL_UNITS_KG );
        this.radio_button_fuel_units_lbs.setSelected( new_fuel_units == XHSISettings.FUEL_UNITS_LBS );
        this.radio_button_fuel_units_usg.setSelected( new_fuel_units == XHSISettings.FUEL_UNITS_USG );
        this.radio_button_fuel_units_ltr.setSelected( new_fuel_units == XHSISettings.FUEL_UNITS_LTR );
        switchable = prefs.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_UNITS_SWITCHABLE);
        this.radio_button_fuel_units_kg.setEnabled( switchable );
        this.radio_button_fuel_units_lbs.setEnabled( switchable );
        this.radio_button_fuel_units_usg.setEnabled( switchable );
        this.radio_button_fuel_units_ltr.setEnabled( switchable );

        int new_temp_units = avionics.get_temp_units();
        this.radio_button_temp_units_c.setSelected( new_temp_units == XHSISettings.TEMP_UNITS_C );
        this.radio_button_temp_units_f.setSelected( new_temp_units == XHSISettings.TEMP_UNITS_F );
        // this.radio_button_temp_units_k.setSelected( new_temp_units == XHSISettings.TEMP_UNITS_K );
        switchable = prefs.get_preference(XHSIPreferences.PREF_TEMP_UNITS).equals(XHSIPreferences.TEMP_UNITS_SWITCHABLE);
        this.radio_button_temp_units_c.setEnabled( switchable );
        this.radio_button_temp_units_f.setEnabled( switchable );
        // this.radio_button_temp_units_k.setEnabled( switchable );
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
