/**
* XPlaneAvionics.java
* 
* The X-Plane specific implementation of Avionics.
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
package net.sourceforge.xhsi.model.xplane;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.Observer;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;
import net.sourceforge.xhsi.model.TCAS;


public class XPlaneAvionics implements Avionics, Observer {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private XPlaneSimDataRepository sim_data;
    private Aircraft aircraft;
    private FMS fms;
    private TCAS tcas;
    private XHSISettings xhsi_settings;
    private XHSIPreferences xhsi_preferences;
    private XPlaneUDPSender udp_sender;

    private NavigationRadio nav1_radio;
    private NavigationRadio nav2_radio;
    private NavigationRadio adf1_radio;
    private NavigationRadio adf2_radio;
    private NavigationRadio gps_radio;

    public XPlaneAvionics(Aircraft aircraft) {
        this.sim_data = XPlaneSimDataRepository.get_instance();
        this.sim_data.add_observer((Observer) this);
        this.aircraft = aircraft;
        this.fms = FMS.get_instance();
        this.tcas = TCAS.get_instance();

        this.xhsi_settings = XHSISettings.get_instance();
        this.xhsi_settings.linkAvionics( this );
        this.xhsi_preferences = XHSIPreferences.get_instance();
        this.udp_sender = XPlaneUDPSender.get_instance();

        this.gps_radio = new NavigationRadio(
                NavigationRadio.RADIO_TYPE_GPS, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_DME_DIST_M, // id_dme_distance
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_DME_TIME_SECS, // id_dme_time
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM, // id_course
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_FROMTO, // id_fromto
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_HDEF_DOT, // id_cdi_dots
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_VDEF_DOT, // id_gs_dots
                this);

        this.nav1_radio = new NavigationRadio(
                1, // bank
                NavigationRadio.RADIO_TYPE_NAV, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ, // id_freq
                XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID, // id_nav_id
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M, // id_dme_distance
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_DME_TIME_SECS, // id_dme_time
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM, // id_obs
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM, // id_course
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_FROMTO, // id_fromto
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT, // id_cdi_dots
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_CDI, // id_gs_active
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT, // id_gs_dots
                this);

        this.nav2_radio = new NavigationRadio(
                2,
                NavigationRadio.RADIO_TYPE_NAV, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ, // id_freq
                XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID, // id_nav_id
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M, // id_dme_distance
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_DME_TIME_SECS, // id_dme_time
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM, // id_obs
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM, // id_course
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_FROMTO, // id_fromto
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT, // id_cdi_dots
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_CDI, // id_gs_active
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT, // id_gs_dots
                this);

        this.adf1_radio = new NavigationRadio(
                1,
                NavigationRadio.RADIO_TYPE_ADF, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ, // id_freq
                XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID, // id_nav_id
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M, // wtf?
                this);

        this.adf2_radio = new NavigationRadio(
                2,
                NavigationRadio.RADIO_TYPE_ADF, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ, // id_freq
                XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID, // id_nav_id
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M, // wtf?
                this);
    }

    public NavigationRadio get_selected_radio(int bank) {
        if (bank == 1) {
            if (efis_radio1() == EFIS_RADIO_NAV) {
                return this.nav1_radio;
            } else if (efis_radio1() == EFIS_RADIO_ADF) {
                return this.adf1_radio;
            } else {
                return null;
            }
        } else if (bank == 2) {
            if (efis_radio2() == EFIS_RADIO_NAV) {
                return this.nav2_radio;
            } else if (efis_radio2() == EFIS_RADIO_ADF) {
                return this.adf2_radio;
            } else {
                return null;
            }
        } else
            return null;
    }

    public NavigationRadio get_nav_radio(int bank) {
        if (bank == 1) {
            return this.nav1_radio;
        } else if (bank == 2) {
            return this.nav2_radio;
        } else {
            return null;
        }
    }

    public NavigationRadio get_gps_radio() {
        return this.gps_radio;
    }

    public RadioNavBeacon get_tuned_navaid(int bank) {
        NavigationRadio radio;
        RadioNavigationObject rnav_object;

        // find the VOR or NDB, depending
        radio = get_selected_radio(bank);
        if ( (radio != null)
            //&& (radio.freq_is_nav()) // nope, can be a VOR or a NDB
            && (radio.receiving())
            ) {
            rnav_object = radio.get_radio_nav_object();
            if ((rnav_object != null) && (rnav_object instanceof RadioNavBeacon)) {
                return (RadioNavBeacon) rnav_object;
            }
        }
        return null;
    }

    public Localizer get_tuned_localizer(int bank) {
        NavigationRadio radio;
        RadioNavigationObject rnav_object;

        // is this NAVx tuned to a Localizer?
        radio = get_nav_radio(bank);
        if ( (radio != null)
                && (radio.freq_is_nav())
                //&& (radio.receiving()) // nope, can be out of range
            ) {
            rnav_object = radio.get_radio_nav_object();
            if ((rnav_object != null) && (rnav_object instanceof Localizer)) {
                return (Localizer) rnav_object;
            }
        }
        // or none?
        return null;
    }


    public boolean power() {
        if ( XHSIPreferences.get_instance().get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            return xhsi_settings.avionics_power;
        } else {
            return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_AVIONICS_ON) != 0.0f );
        }
    }

    public int map_range_index() {
        // ranges: 10, 20, 40, 80, 160, 320, 640
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE);
        } else {
            return xhsi_settings.range;
        }
    }

    public int map_range() {
        // ranges: 10, 20, 40, 80, 160, 320, 640
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR) ];
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE) ];
        } else {
            return xhsi_settings.range;
        }
    }

    public int map_mode() {
        // modes: 0=centered, 1=expanded (see the constants in model/Avionics)
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE));
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_CTR));
        } else {
            return xhsi_settings.map_centered;
        }
    }

    public int map_submode() {
        // submodes: 0=APP, 1=VOR, 2=MAP, 3=NAV, 4=PLN (see the constants in model/Avionics)
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE));
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_MODE));
        } else {
            return xhsi_settings.map_mode;
        }
    }

    public int hsi_source() {
        //return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_HSI_SELECTOR);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_HSI_SELECTOR));
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_HSI_SOURCE));
        } else {
            return xhsi_settings.source;
        }
    }

    public int efis_radio1() {
        //return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR));
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_RADIO1));
        } else {
            return xhsi_settings.radio1;
        }
    }

    public int efis_radio2() {
        //return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR));
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_RADIO2));
        } else {
            return xhsi_settings.radio2;
        }
    }

    public boolean efis_shows_wpt() {
        //return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS) == 1.0f);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT) == 1.0f);
        } else {
            return xhsi_settings.show_wpt;
        }
    }

    public boolean efis_shows_ndb() {
        //return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS) == 1.0f);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB) == 1.0f);
        } else {
            return xhsi_settings.show_ndb;
        }
    }

    public boolean efis_shows_vor() {
        //return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS) == 1.0f);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR) == 1.0f);
        } else {
            return xhsi_settings.show_vor;
        }
    }

    public boolean efis_shows_arpt() {
        //return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS) == 1.0f);
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT) == 1.0f);
        } else {
            return xhsi_settings.show_arpt;
        }
    }

    public boolean efis_shows_tfc() {
        //return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS) == 1.0f);
        if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TFC) == 1.0f);
        } else {
            return xhsi_settings.show_tfc;
        }
    }

    public boolean efis_shows_data() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_DATA) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_DATA) == 1.0f);
        } else {
            return xhsi_settings.show_data;
        }
    }

    public boolean efis_shows_pos() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_POS) == 1.0f);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_POS) == 1.0f);
        } else {
            return xhsi_settings.show_pos;
        }
    }

    public float heading_bug() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_HEADING_MAG); }

    public int autpilot_state() { return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_STATE);    }

    public float autopilot_vv() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY);    }

    public float autopilot_altitude() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_ALTITUDE);    }

    public Aircraft get_aircraft() { return this.aircraft; }

    public FMS get_fms() { return this.fms; }

    public TCAS get_tcas() { return this.tcas; }

    public int transponder_mode() {
        //return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_MODE);
        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_MODE));
        } else {
            return xhsi_settings.xpdr;
        }
    }

    public float nav1_obs() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM); }

    public float nav2_obs() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM); }

    public float nav1_course()
    {
        if ( get_nav_radio(1).receiving() )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM);
        else
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM);
    }

    public float nav2_course()
    {
        if ( get_nav_radio(2).receiving() )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM);
        else
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM);
    }

    public float gps_course() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM); }
    // these should probably be in NavigationRadio
    public float nav1_hdef_dot() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT); }
    public float nav2_hdef_dot() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT); }
    public float gps_hdef_dot() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_HDEF_DOT); }
    public int nav1_fromto() { return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_FROMTO); }
    public int nav2_fromto() { return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_FROMTO); }
    public int gps_fromto() { return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_FROMTO); }
    public float nav1_vdef_dot() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT); }
    public float nav2_vdef_dot() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT); }
    public float gps_vdef_dot() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_VDEF_DOT); }
    public boolean nav1_gs_active() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_CDI) != 0; }
    public boolean nav2_gs_active() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_CDI) != 0; }
    public boolean gps_gs_active() { return false; }

//    public void set_power(boolean new_power){
//        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
//            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT2_SWITCHES_AVIONICS_ON, new_power ? 1.0f : 0.0f );
//        }
//    }

    public void set_hsi_source(int new_source) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_HSI_SELECTOR, (float) new_source );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_HSI_SOURCE, (float) new_source );
        }
    }

    public void set_nav1_obs(float new_obs1) {
        udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM, (float) new_obs1 );
    }

    public void set_nav2_obs(float new_obs2) {
        udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM, (float) new_obs2 );
    }

    public void set_radio1(int new_radio1) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR, (float) new_radio1 );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_RADIO1, (float) new_radio1 );
        }
    }

    public void set_radio2(int new_radio2) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR, (float) new_radio2 );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_RADIO2, (float) new_radio2 );
        }
    }

    public void set_submode(int new_submode) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, (float) new_submode );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_MODE, (float) new_submode );
        }
    }

    public void set_mode(int new_mode) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE, (float) new_mode );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_CTR, (float) new_mode );
        }
    }

    public void set_range_index(int new_range_index) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, (float) new_range_index );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE, (float) new_range_index );
        }
    }

    public void set_xpdr(int new_xpdr) {
        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_MODE, (float) new_xpdr );
        }
    }

    public void set_show_arpt(boolean new_arpt) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS, new_arpt ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT, new_arpt ? 1.0f : 0.0f );
        }
    }

    public void set_show_wpt(boolean new_wpt) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS, new_wpt ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT, new_wpt ? 1.0f : 0.0f );
        }
    }

    public void set_show_vor(boolean new_vor) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS, new_vor ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR, new_vor ? 1.0f : 0.0f );
        }
    }

    public void set_show_ndb(boolean new_ndb) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS, new_ndb ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB, new_ndb ? 1.0f : 0.0f );
        }
    }

    public void set_show_tfc(boolean new_tfc) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS, new_tfc ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TFC, new_tfc ? 1.0f : 0.0f );
        }
    }

    public void set_show_pos(boolean new_pos) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_POS, new_pos ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_POS, new_pos ? 1.0f : 0.0f );
        }
    }

    public void set_show_data(boolean new_data) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_DATA, new_data ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_DATA, new_data ? 1.0f : 0.0f );
        }
    }



    public void update() {

        // when new data arrives, select/deselect menu options accordingly
        xhsi_settings.update(this);

    }


}
