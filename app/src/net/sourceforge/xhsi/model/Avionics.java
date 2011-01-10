/**
* Avionics.java
* 
* Model for an aircraft's avionics systems
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package net.sourceforge.xhsi.model;

public interface Avionics {

    public static final int HSI_SOURCE_NAV1 = 0;
    public static final int HSI_SOURCE_NAV2 = 1;
    public static final int HSI_SOURCE_GPS = 2;

    public static final int EFIS_RADIO_ADF = 0;
    public static final int EFIS_RADIO_OFF = 1;
    public static final int EFIS_RADIO_NAV = 2;
    public static final int EFIS_RADIO_BOTH = 999; // for later, only available in override

    public static final int EFIS_MAP_CENTERED = 0;
    public static final int EFIS_MAP_EXPANDED = 1;
    public static final int EFIS_MAP_APP = 0;
    public static final int EFIS_MAP_VOR = 1;
    public static final int EFIS_MAP_MAP = 2;
    public static final int EFIS_MAP_NAV = 3;
    public static final int EFIS_MAP_PLN = 4;

    public static final int EFIS_MAP_RANGE[] = {10, 20, 40, 80, 160, 320, 640};

    public static final int XPDR_OFF = 0;
    public static final int XPDR_STBY = 1;
    public static final int XPDR_ON = 2;
    public static final int XPDR_TA = 3;
    public static final int XPDR_TARA = 4;


    /**
     * @return boolean - do we have avionics power?
     */
    public boolean power();

    /**
     * @return int - selected range of map display in switch setting
     */
    public int map_range_index();

    /**
     * @return int - selected range of map display in NM
     */
    public int map_range();

    /**
     * @return int - map mode CENTERED or EXPANDED
     */
    public int map_mode();

    /**
     * @return int - map submode APP, VOR, MAP, NAV or PLN
     */
    public int map_submode();

    /**
     * @return int - HSI source selector - either HSI_SOURCE_NAV1, HSI_SOURCE_NAV2 or HSI_SOURCE_GPS
     */
    public int hsi_source();

    /**
     * @return int - EFIS Radio1 setting - either EFIS_RADIO_ADF, EFIS_RADIO_NAV or EFIS_RADIO_OFF
     */
    public int efis_radio1();

    /**
     * @return int - EFIS Radio2 setting - either EFIS_RADIO_ADF, EFIS_RADIO_NAV or EFIS_RADIO_OFF
     */
    public int efis_radio2();

    /**
     * @return boolean - true if EFIS displays waypoints, false otherwise
     */
    public boolean efis_shows_wpt();

    /**
     * @return boolean - true if EFIS displays VORs, false otherwise
     */
    public boolean efis_shows_vor();

    /**
     * @return boolean - true if EFIS displays NDBs, false otherwise
     */
    public boolean efis_shows_ndb();

    /**
     * @return boolean - true if EFIS displays airports, false otherwise
     */
    public boolean efis_shows_arpt();

    /**
     * @return boolean - true if EFIS displays TCAS information, false otherwise
     */
    public boolean efis_shows_tfc();

    /**
     * @return boolean - true if EFIS displays FMS altitude information, false otherwise
     */
    public boolean efis_shows_data();

    /**
     * @return boolean - true if EFIS displays bearing lines, false otherwise
     */
    public boolean efis_shows_pos();

    /**
     * @return NavigationRadio - model class representing the currently selected radio or null, if none is selected
     */
    public NavigationRadio get_selected_radio(int bank);

    /**
     * @return NavigationRadio - model class representing the NAV radio
     */
    public NavigationRadio get_nav_radio(int bank);

    /**
     * @return NavigationRadio - model class representing the GPS
     */
    public NavigationRadio get_gps_radio();

    /**
     * @return Localizer - model class representing the currently selected localizer or null, if none is selected
     */
    public Localizer get_tuned_localizer(int bank);

    /**
     * @return VOR - model class representing the currently selected VOR or null, if none is selected
     */
    public RadioNavBeacon get_tuned_navaid(int bank);

    /**
     * @return float - selected OBS for NAV1 in degrees
     */
    public float nav1_obs();

    /**
     * @return float - selected OBS for NAV2 in degrees
     */
    public float nav2_obs();

    /**
     * @return float - selected course for NAV1 in degrees
     */
    public float nav1_course();

    /**
     * @return float - selected course for NAV2 in degrees
     */
    public float nav2_course();

    /**
     * @return float - selected course for GPS in degrees
     */
    public float gps_course();

    /**
     * @return float - deflection for NAV1 in dots
     */
    public float nav1_hdef_dot();

    /**
     * @return float - deflection for NAV2 in dots
     */
    public float nav2_hdef_dot();

    /**
     * @return float - deflection for GPS in dots
     */
    public float gps_hdef_dot();

    /**
     * @return float - NAV1 OFF/TO/FROM indicator
     */
    public int nav1_fromto();

    /**
     * @return float - NAV2 OFF/TO/FROM indicator
     */
    public int nav2_fromto();

    /**
     * @return float - GPS OFF/TO/FROM indicator
     */
    public int gps_fromto();

    /**
     * @return float - deflection for NAV1 GS in dots
     */
    public float nav1_vdef_dot();

    /**
     * @return float - deflection for NAV2 GS in dots
     */
    public float nav2_vdef_dot();

    /**
     * @return float - deflection for GPS GS in dots
     */
    public float gps_vdef_dot();

    /**
     * @return boolean - NAV1 GS active
     */

    public boolean nav1_gs_active();
    /**
     * @return boolean - NAV2 GS active
     */
    public boolean nav2_gs_active();

    /**
     * @return boolean - GPS GS active
     */
    public boolean gps_gs_active();


    /**
     * TODO: constants for autopilot states need to be defined
     * 
     * @return int - bitmask for autopilot state
     */
    public int autpilot_state();

    /**
     * @return float - vertical velocity in feet per minute selected in autopilot
     */
    public float autopilot_vv();

    /**
     * @return float - altitude in feed selected in autopilot
     */
    public float autopilot_altitude();

    /**
     * @return float - heading in degrees selected in autopilot
     */
    public float heading_bug();        // degree radians

    /**
     * @return Aircraft - reference to aircraft model class to which avionics belongs
     */
    public Aircraft get_aircraft();

    /**
     * @return FMS - reference to flight management system model class
     */
    public FMS get_fms();

    /**
     * @return TCAS - reference to tcas model class
     */
    public TCAS get_tcas();

    /**
     * @return int - transponder mode
     */
    public int transponder_mode();


    //    public void set_power(boolean new_power);

    public void set_hsi_source(int new_source);

    public void set_nav1_obs(float new_obs1);

    public void set_nav2_obs(float new_obs2);

    public void set_radio1(int new_radio1);

    public void set_radio2(int new_radio2);

    public void set_submode(int new_submode);

    public void set_mode(int new_mode);

    public void set_range_index(int new_range_index);

    public void set_xpdr(int new_xpdr);

    public void set_show_arpt(boolean new_arpt);

    public void set_show_wpt(boolean new_wpt);

    public void set_show_vor(boolean new_vor);

    public void set_show_ndb(boolean new_ndb);

    public void set_show_tfc(boolean new_tfc);

    public void set_show_pos(boolean new_pos);

    public void set_show_data(boolean new_data);


}
