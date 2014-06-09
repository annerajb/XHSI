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
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;
import net.sourceforge.xhsi.model.TCAS;
import net.sourceforge.xhsi.model.Avionics.FailedElement;
import net.sourceforge.xhsi.model.Avionics.FailureMode;


public class XPlaneAvionics implements Avionics, Observer {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private SimDataRepository sim_data;
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

    /* Internal X-Plane failure values
     * always working = 0
     * fail at mean_time = 1
	 * fail at exact time = 2
	 * fail at speed = 3
	 * fail at altitude = 4
	 * fail at key = 5
	 * inoperative now = 6
     */

    
    public XPlaneAvionics(Aircraft aircraft, ModelFactory sim_model) {

        this.sim_data = sim_model.get_repository_instance();
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
                sim_model,
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
                sim_model,
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
                sim_model,
                this);

        this.adf1_radio = new NavigationRadio(
                1,
                NavigationRadio.RADIO_TYPE_ADF, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ, // id_freq
                XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID, // id_nav_id
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M, // wtf?
                sim_model,
                this);

        this.adf2_radio = new NavigationRadio(
                2,
                NavigationRadio.RADIO_TYPE_ADF, // radio_type
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ, // id_freq
                XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID, // id_nav_id
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT, // id_deflection
                XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M, // wtf?
                sim_model,
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


    public float get_radio_freq(int radio_num) {

        if ( radio_num == RADIO_NAV1 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ);
        else if ( radio_num == RADIO_NAV2 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ);
        else if ( radio_num == RADIO_ADF1 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ);
        else if ( radio_num == RADIO_ADF2 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ);
        else if ( radio_num == RADIO_COM1 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_COM1_FREQ_HZ);
        else if ( radio_num == RADIO_COM2 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_COM2_FREQ_HZ);
        else if ( radio_num == RADIO_NAV1_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_STDBY_FREQ_HZ);
        else if ( radio_num == RADIO_NAV2_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_STDBY_FREQ_HZ);
        else if ( radio_num == RADIO_ADF1_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_STDBY_FREQ_HZ);
        else if ( radio_num == RADIO_ADF2_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_STDBY_FREQ_HZ);
        else if ( radio_num == RADIO_COM1_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_COM1_STDBY_FREQ_HZ);
        else if ( radio_num == RADIO_COM2_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ);
        else
            return 0.0f;

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
                //&& (radio.receiving()) // nope, can be out of map_range
            ) {
            rnav_object = radio.get_radio_nav_object();
            if ((rnav_object != null) && (rnav_object instanceof Localizer)) {
                return (Localizer) rnav_object;
            }
        }
        // or none?
        return null;

    }


    public int get_instrument_style() {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            return xhsi_settings.style;
        } else {
            if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE).equals(XHSIPreferences.INSTRUMENT_STYLE_SWITCHABLE)) {
                return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_STYLE);
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE).equals(XHSIPreferences.INSTRUMENT_STYLE_BOEING)) {
                return Avionics.STYLE_BOEING;
            } else /* if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_INSTRUMENT_STYLE).equals(XHSIPreferences.INSTRUMENT_STYLE_AIRBUS)) */ {
                return Avionics.STYLE_AIRBUS;
            }
        }

    }


    public boolean power() {

        if ( XHSIPreferences.get_instance().get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ||
                ! XHSIPreferences.get_instance().get_use_power() ) {
            return true;
        } else {
            if ( is_x737() )
                return ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_PWR) != 0.0f );
            else
                return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_AVIONICS_ON) != 0.0f );
        }

    }


    public boolean outer_marker() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_OUTER_MARKER_LIT) == 1.0f );
    }


    public boolean middle_marker() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_MIDDLE_MARKER_LIT) == 1.0f );
    }


    public boolean inner_marker() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_INNER_MARKER_LIT) == 1.0f );
    }


    public int map_range_index() {

        // ranges: 0:10, 1:20, 2:40, 3:80, 4:160, 5:320, 6:640
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE);
        } else {
            return xhsi_settings.map_range_index;
        }

    }


    public int map_range() {

        // ranges: 10, 20, 40, 80, 160, 320, 640
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR) ];
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE) ];
        } else {
            return xhsi_settings.map_range;
        }

    }


    public boolean map_zoomin() {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_MAP_ZOOMIN) == 1.0f;
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_ZOOMIN) == 1.0f;
        } else {
            return xhsi_settings.map_zoomin;
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

        if ( xhsi_preferences.get_hsi_source() == 1 ) {
            // Always NAV1 set in Preferences
            return Avionics.HSI_SOURCE_NAV1;
        } else if ( xhsi_preferences.get_hsi_source() == 2 ) {
            // Always NAV2 set in Preferences
            return Avionics.HSI_SOURCE_NAV2;
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            // Pilot selection
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_HSI_SELECTOR));
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // Copilot selection
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_HSI_SOURCE));
        } else {
            // Instructor selection
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


    public int get_mfd_mode() {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            return xhsi_settings.mfd_mode;
        } else {
            if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_SWITCHABLE)) {
                return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_MFD_MODE);
//            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_TAXI_CHART)) {
//                return 0;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_ARPT_CHART)) {
                return Avionics.MFD_MODE_ARPT;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_FPLN)) {
                return Avionics.MFD_MODE_FPLN;
            } else {
                return Avionics.MFD_MODE_EICAS;
            }
        }

    }


    public int get_trq_scale() {
        
        if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_TRQ_SCALE).equals(XHSIPreferences.TRQ_SCALE_SWITCHABLE) ) {
            return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EICAS_TRQ_SCALE);
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_TRQ_SCALE).equals(XHSIPreferences.TRQ_SCALE_LBFT) ) {
            return XHSISettings.TRQ_SCALE_LBFT;
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_TRQ_SCALE).equals(XHSIPreferences.TRQ_SCALE_NM) ) {
            return XHSISettings.TRQ_SCALE_NM;
        } else /* if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_TRQ_SCALE).equals(XHSIPreferences.TRQ_SCALE_PERCENT) ) */{
            return XHSISettings.TRQ_SCALE_PERCENT;
        }

    }
    

    public int get_fuel_units() {

        if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_UNITS_SWITCHABLE) ) {
            return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_FUEL_UNITS);
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_UNITS_KG) ) {
            return XHSISettings.FUEL_UNITS_KG;
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_UNITS_LBS) ) {
            return XHSISettings.FUEL_UNITS_LBS;
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_UNITS_USG) ) {
            return XHSISettings.FUEL_UNITS_USG;
        } else /* if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_LTR) ) */{
            return XHSISettings.FUEL_UNITS_LTR;
        }

    }
    
    
    public float fuel_multiplier() {
        
        if ( xhsi_settings.fuel_units == XHSISettings.FUEL_UNITS_LBS ) return 2.20462262185f;
        else if ( xhsi_settings.fuel_units == XHSISettings.FUEL_UNITS_USG ) return 2.20462262185f/6.02f;
        else if ( xhsi_settings.fuel_units == XHSISettings.FUEL_UNITS_LTR ) return 2.20462262185f/6.02f*3.785411784f;
        else /* if ( xhsi_settings.fuel_units == XHSISettings.FUEL_UNITS_KG ) */ return 1.0f;
        
    }


    public int get_engine_type() {

        if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_SWITCHABLE) ) {
            return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_ENGINE_TYPE);
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_N1) ) {
            return XHSISettings.ENGINE_TYPE_N1;
//        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_EPR) ) {
//            return XHSISettings.ENGINE_TYPE_EPR;
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_TRQ) ) {
            return XHSISettings.ENGINE_TYPE_TRQ;
        } else {
            return XHSISettings.ENGINE_TYPE_MAP;
        }

    }


    public int autopilot_state() { return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE);    }

    public float autopilot_vv() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY);    }

    public float autopilot_altitude() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_ALTITUDE);    }

    public float autopilot_speed() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AIRSPEED);    }

    public boolean autopilot_speed_is_mach() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AIRSPEED_IS_MACH) == 1.0f;    }

    public float heading_bug() { return normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_HEADING_MAG) ); }

    public float fd_pitch() {

        if ( is_x737() ) {

            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_B_PITCH);
            } else {
                return sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_A_PITCH);
            }

        } else {
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_FD_PITCH);
        }
    }

    public float fd_roll() {

        if ( is_x737() ) {

            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_B_ROLL);
            } else {
//logger.warning("FD_roll: "+sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_A_ROLL));
                return sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_A_ROLL);
            }

        } else {
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_FD_ROLL);
        }

    }

    public boolean is_x737() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_STATUS) != 0.0f );
    }

    public int x737_mcp_spd() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_MCPSPD);
    }

    public int x737_fmc_spd() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_FMCSPD);
    }

    public int x737_retard() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_RETARD);
    }

    public int x737_thr_hld() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_THRHLD);
    }

    public int x737_lnav_armed() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_LNAVARMED);
    }

    public int x737_vorloc_armed() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VORLOCARMED);
    }

    public int x737_pitch_spd() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_PITCHSPD);
    }

    public int x737_alt_hld() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_ALTHLD);
    }

    public int x737_vs_armed() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VSARMED);
    }

    public int x737_vs() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VS);
    }

    public int x737_vnav_alt() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VNAVALT);
    }

    public int x737_vnav_path() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VNAVPATH);
    }

    public int x737_vnav_spd() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VNAVSPD);
    }

    public int x737_gs_armed() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_GSARMED);
    }

    public int x737_gs() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_GS);
    }

    public int x737_flare_armed() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_FLAREARMED);
    }

    public int x737_flare() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_FLARE);
    }

    public int x737_toga() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_TOGA);
    }

    public int x737_lnav() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_LNAV);
    }

    public int x737_hdg() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_HDG);
    }

    public int x737_vorloc() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_VORLOC);
    }

    public int x737_n1() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_N1);
    }

    public boolean x737_athr_armed() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_ATHR_ARMED) == 1.0f );
    }


    public int autopilot_mode() {

        if ( is_x737() ) {

            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                if ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_CMD_B) == 1.0f ) return 2;
                else if ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_FD_B) == 1.0f ) return 1;
                else return 0;
            } else {
                if ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_CMD_A) == 1.0f ) return 2;
                else if ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_FD_A) == 1.0f ) return 1;
                else return 0;
            }

        } else {
            return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_MODE);
        }

    }


    public boolean autothrottle_enabled() { return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ENABLED) == 1.0f );  }

    public boolean autothrottle_on() { return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ON) == 1.0f );  }

    public boolean ap_hdg_sel_on() { return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_HEADING_STATUS) == 2.0f );  }

    public boolean ap_vorloc_arm() {
        return ( (hsi_source() != Avionics.HSI_SOURCE_GPS) && (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_NAV_STATUS) == 1.0f) );
    }

    public boolean ap_vorloc_on() {
        return ( (hsi_source() != Avionics.HSI_SOURCE_GPS) && (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_NAV_STATUS) == 2.0f) );
    }

    public boolean ap_lnav_arm() {
        return ( (hsi_source() == Avionics.HSI_SOURCE_GPS) && (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_NAV_STATUS) == 1.0f) );
    }

    public boolean ap_lnav_on() {
        return ( (hsi_source() == Avionics.HSI_SOURCE_GPS) && (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_NAV_STATUS) == 2.0f) );
    }

    public boolean ap_vs_arm() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_VVI_STATUS) == 1.0f );
    }

    public boolean ap_vs_on() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_VVI_STATUS) == 2.0f );
    }

    public boolean ap_flch_on() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_SPEED_STATUS) == 2.0f );
    }

    public boolean ap_alt_hold_arm() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_ALTITUDE_HOLD_STATUS) == 1.0f );
    }

    public boolean ap_alt_hold_on() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_ALTITUDE_HOLD_STATUS) == 2.0f );
    }

    public boolean ap_gs_arm() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_GLIDESLOPE_STATUS) == 1.0f );
    }

    public boolean ap_gs_on() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_GLIDESLOPE_STATUS) == 2.0f );
    }

    public boolean ap_bc_arm() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_BACKCOURSE_STATUS) == 1.0f );
    }

    public boolean ap_bc_on() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_BACKCOURSE_STATUS) == 2.0f );
    }

    public boolean ap_vnav_arm() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_VNAV_STATUS) == 1.0f );
    }

    public boolean ap_vnav_on() {
        return (
                ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_VNAV_STATUS) == 2.0f ) ||
                // sprecial treatment for the FMS/pseudo-VNAV
                ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE) & 0x01000) != 0 )
                );
    }

    public boolean ap_vtoga_arm() {
        return ( (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_TOGA_STATUS) == 1.0f) );
    }

    public boolean ap_vtoga_on() {
        return ( (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_TOGA_STATUS) == 2.0f) );
    }

    public boolean ap_ltoga_arm() {
        return ( (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_TOGA_LATERAL_STATUS) == 1.0f) );
    }

    public boolean ap_ltoga_on() {
        return ( (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_TOGA_LATERAL_STATUS) == 2.0f) );
    }

    public boolean ap_roll_on() {
        return ( (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_ROLL_STATUS) == 2.0f) );
    }

    public boolean ap_pitch_on() {
        return ( (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_AUTOPILOT_PITCH_STATUS) == 2.0f) );
    }

    public Aircraft get_aircraft() { return this.aircraft; }

    public FMS get_fms() { return this.fms; }

    public TCAS get_tcas() { return this.tcas; }

    public int transponder_mode() {

        //return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_MODE);
        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_MODE);
        } else {
            return xhsi_settings.xpdr;
        }

    }

    public int transponder_code() {

        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_CODE);

    }

    public boolean clock_shows_utc() {
        return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CLOCK_TIMER_MODE) == 0.0f );
    }

    public float nav1_obs() { 
        float obs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM) );
        float crs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM) );
        if ( xhsi_preferences.get_auto_frontcourse() && get_nav_radio(1).freq_is_localizer() && ( Math.round(obs*10.0f) != Math.round(crs*10.0f) ) ) {
            set_nav1_obs( crs );
        }
        return obs;
    }

    public float nav2_obs() {
        float obs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM) );
        float crs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM) );
        if ( xhsi_preferences.get_auto_frontcourse() && get_nav_radio(2).freq_is_localizer() && ( Math.round(obs*10.0f) != Math.round(crs*10.0f) ) ) {
            set_nav2_obs( crs );
        }
        return obs;
    }


    public float nav1_course() {

        if ( get_nav_radio(1).receiving() )
            return normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM) );
        else
            return nav1_obs();

    }


    public float nav2_course() {

        if ( get_nav_radio(2).receiving() )
            return normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM) );
        else
            return nav2_obs();

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

    public boolean nav1_gs_active() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_CDI) != 0.0f; }

    public boolean nav2_gs_active() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_CDI) != 0.0f; }

    public boolean gps_gs_active() { return false; }

    
    public boolean is_cl30() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.CL30_STATUS) == 1.0f );
    }

    public int cl30_refspds() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_REFSPDS);
    }
    public int cl30_v1() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_V1);
    }
    public int cl30_vr() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_VR);
    }
    public int cl30_v2() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_V2);
    }
    public int cl30_vt() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_VT);
    }
    public int cl30_vga() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_VGA);
    }
    public int cl30_vref() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.CL30_VREF);
    }
    
    
    public float cl30_mast_warn() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.CL30_MAST_WARN);
    }
    
    public float cl30_mast_caut() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.CL30_MAST_CAUT);
    }

    // QPAC Airbus FBW
    public boolean is_qpac() {
    	return ( sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS) == 1.0f );
    }
        
    // Autopilot
    public boolean qpac_ap1() {
    	// return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP1) > 0 ? true : false;
    	int qpac_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_FD));
    	return (qpac_ap_fd_data & 0x08) > 0 ? true : false; 	

    }

    public boolean qpac_ap2() {
//    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP2) > 0 ? true : false;
    	int qpac_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_FD));
    	return (qpac_ap_fd_data & 0x04) > 0 ? true : false; 	

    }

    public int qpac_ap_phase() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_PHASE));
    }
    
    public int qpac_presel_crz() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_PRESEL_CRZ));
    }
    
    public int qpac_presel_clb() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_PRESEL_CLB));
    }
    
    public float qpac_presel_mach() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_PRESEL_MACH);
    }
    
    public int qpac_ap_vertical_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_VERTICAL_MODE));
    }
    
    public int qpac_ap_vertical_armed() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_VERTICAL_ARMED));
    }

    public int qpac_ap_lateral_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_LATERAL_MODE));
    }
    public int qpac_ap_lateral_armed() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_LATERAL_ARMED));
    }

    public int qpac_npa_valid() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_NPA_VALID));
    }
    public int qpac_npa_no_points() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_NPA_NO_POINTS));
    }
    
    public boolean qpac_appr_illuminated() {
    	int qpac_ap_appr_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_APPR));
    	return  (qpac_ap_appr_data  & 0x01) > 0 ? true : false;
    }
    public boolean qpac_loc_illuminated() {
    	int qpac_ap_appr_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_APPR));
    	return  (qpac_ap_appr_data  & 0x02) > 0 ? true : false;
    }

    public int qpac_appr_type() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_APPR_TYPE));
    }
    public float qpac_appr_mda() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_APPR_MDA);
    }    

    public boolean qpac_alt_is_cstr(){
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ALT_IS_CSTR) > 0 ? true : false;
    }    
    public int qpac_constraint_alt(){
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_CONSTRAINT_ALT));
    }    
    // FCU
    public boolean qpac_fcu_hdg_trk(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x01) > 0 ? true : false;
    }    
    public boolean qpac_fcu_metric_alt(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x02) > 0 ? true : false;
    }    
    public boolean qpac_fcu_vs_dashed(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x04) > 0 ? true : false;
    }    
    public boolean qpac_fcu_spd_dashed(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x08) > 0 ? true : false;
    }    
    public boolean qpac_fcu_spd_managed(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x10) > 0 ? true : false;
    }    
    public boolean qpac_fcu_hdg_dashed(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x20) > 0 ? true : false;
    }    
    public boolean qpac_fcu_hdg_managed(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x40) > 0 ? true : false;
    }    
    public boolean qpac_fcu_alt_managed(){
    	int qpac_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU));
    	return (qpac_fcu_data & 0x80) > 0 ? true : false;
    }      
    
    // Baro
    public boolean qpac_baro_std(){
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))  
    		return (qpac_fcu_baro & 0x40) > 0 ? true : false;
    	else 
    		return (qpac_fcu_baro & 0x04) > 0 ? true : false;    	 	
    }
    public boolean qpac_baro_unit(){
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))  
    		return (qpac_fcu_baro & 0x20) > 0 ? true : false;
    	else 
    		return (qpac_fcu_baro & 0x02) > 0 ? true : false; 	
    }
    public boolean qpac_baro_hide(){
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))  
    		return (qpac_fcu_baro & 0x10) > 0 ? false : true;
    	else 
    		return (qpac_fcu_baro & 0x01) > 0 ? false : true;	
    }
    
    // Auto-Thrust
    public int qpac_athr_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ATHR_MODE));
    }
    public int qpac_athr_mode2() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ATHR_MODE2));
    }
    public int qpac_athr_limited() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ATHR_LIMITED));
    }
    public int qpac_thr_lever_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_THR_LEVER_MODE));
    }
    public int qpac_fma_thr_warning() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FMA_THR_WARNING));
    }
    public int qpac_flex_temp() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FLEX_TEMP));
    }
    
    // ILS Sig and Deviation Capt. and FO
    public float qpac_loc_val() {
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))  
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_LOC_VAL_FO);
    	else 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_LOC_VAL_CAPT);
    }
    
    public boolean qpac_loc_on(){
    	
    	int qpac_ils_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_FLAGS));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))
    		return (qpac_ils_data & 0x04) > 0 ? true : false;
    	else
    		return (qpac_ils_data & 0x01) > 0 ? true : false;
    }
    
    public float qpac_gs_val(){
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_GS_VAL_FO);
    	else
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_GS_VAL_CAPT);
    }
    
    public boolean qpac_gs_on(){
    	int qpac_ils_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_FLAGS));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))
    		return (qpac_ils_data & 0x08) > 0 ? true : false; 	
    	else
    		return (qpac_ils_data & 0x02) > 0 ? true : false;	
    }
    
    public float qpac_ils_crs(){
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_CRS);
    }
    
    public float qpac_ils_freq(){
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_FREQ);
    }   
    
    public String qpac_ils_id(){
    	return sim_data.get_sim_string(XPlaneSimDataRepository.QPAC_ILS_ID);
    }
    
    public float qpac_ils_dme(){
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_DME);
    }
    
    // FD
    public boolean qpac_fd1() {
    	// return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1) > 0 ? true : false;
    	int qpac_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_FD));
    	return (qpac_ap_fd_data & 0x02) > 0 ? true : false; 	
    }

    public boolean qpac_fd2() {
//    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2) > 0 ? true : false;
    	int qpac_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_FD));
    	return (qpac_ap_fd_data & 0x01) > 0 ? true : false; 	
    }

    public boolean qpac_fd_on() {
    	int qpac_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AP_FD));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))
    		return (qpac_ap_fd_data & 0x01) > 0 ? true : false; 	
    	else
    		return (qpac_ap_fd_data & 0x02) > 0 ? true : false;	
    }
    
    public float qpac_fd1_ver_bar() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1_VER_BAR);
    }    
    public float qpac_fd1_hor_bar() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1_HOR_BAR);
    }    
    public float qpac_fd1_yaw_bar() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1_YAW_BAR);
    }    
    public float qpac_fd2_ver_bar() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_VER_BAR);
    }    
    public float qpac_fd2_hor_bar() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_HOR_BAR);
    }    
    public float qpac_fd2_yaw_bar() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_YAW_BAR);
    }    
    public float qpac_fd_ver_bar() {
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT )) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_VER_BAR);
    	else
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1_VER_BAR);
    }    
    public float qpac_fd_hor_bar() {
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT )) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_HOR_BAR);
    	else
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1_HOR_BAR);
    }  
    public float qpac_fd_yaw_bar() {
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT )) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_YAW_BAR);
    	else
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD1_YAW_BAR);
    }  
    
    // V Speeds   
    public float qpac_v1_value() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_V1_VALUE);
    }
    public int qpac_v1() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_V1));
    }
    public int qpac_vr() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_VR));
    }    
    public int qpac_vmo() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_VMO));
    }    
    public int qpac_vls() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_VLS));
    }    
    
    public int qpac_vs() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_VS));
    }    
    public int qpac_vf() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_VF));
    }    
    public int qpac_v_green_dot() {
        return Math.round( sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_V_GREEN_DOT));
    }    
    public int qpac_alpha_prot() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ALPHA_PROT));
    }    
    public int qpac_alpha_max() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ALPHA_MAX));
    }    
    // Failures
    public float qpac_failures() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FAILURES);
    }
    
    // UMFC
    public boolean has_ufmc() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_STATUS) == 1.0f );
    }

    public float ufmc_v1() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_V1);
    }
    public float ufmc_vr() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_VR);
    }
    public float ufmc_v2() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_V2);
    }
    public float ufmc_vref() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_VREF);
    }
    public float ufmc_vf30() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_VF30);
    }
    public float ufmc_vf40() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_VF40);
    }



//    public void set_power(boolean new_power){
//        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
//            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT2_SWITCHES_AVIONICS_ON, new_power ? 1.0f : 0.0f );
//        }
//    }

    public void set_instrument_style(int new_style) {
        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_STYLE, (float) new_style );
    };

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


    public void set_zoomin(boolean new_zoomin) {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_MAP_ZOOMIN, new_zoomin ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_ZOOMIN, new_zoomin ? 1.0f : 0.0f );
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


    public void set_xpdr_mode(int new_xpdr_mode) {

        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_MODE, (float) new_xpdr_mode );
        }

    }


    public void set_clock_mode(int new_clock_mode) {

        udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT2_CLOCK_TIMER_MODE, (float) new_clock_mode );

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


    public void set_mfd_mode(int new_mode) {

        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_MFD_MODE, (float) new_mode );
        }

    }


    public void set_trq_scale(int new_scale) {

        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EICAS_TRQ_SCALE, (float) new_scale );

    }
    
    
    public void set_max_trq_override(float new_max_trq) {
        
        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EICAS_OVERRIDE_TRQ_MAX, new_max_trq );
        
    }


    public void set_fuel_units(int new_units) {

        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_FUEL_UNITS, (float) new_units );

    }
    
    
    public void set_engine_type(int new_type) {

        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_ENGINE_TYPE, (float) new_type );
        }

    }


    public void update() {

        // when new data arrives, select/deselect menu options accordingly
        xhsi_settings.update(this);

    }


    private float normalize(float deg) {

        while ( deg < 0.0f ) deg += 360.0f;
        if ( deg >= 360.0f ) deg %= 360.0f;
        return deg;

    }

    // Failures
    // 
    public FailureMode failure_mode(FailedElement element) {    	
    	int gauges_failure_data=0;
    	int failure_index = 0;
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		gauges_failure_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_GAUGES_FAILURES_COPILOT));
    	} else {
    		gauges_failure_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_GAUGES_FAILURES_PILOT));    		
    	}
    	switch (element) {
    		case PFD_VSI :failure_index = gauges_failure_data & 0x0007; break; 
    		case PFD_TURN :failure_index = (gauges_failure_data & 0x0038) >> 3; break;
    		case PFD_HEADING :failure_index = (gauges_failure_data & 0x01C0) >> 6; break;
    		case PFD_AIR_SPEED :failure_index = (gauges_failure_data & 0x000E00) >> 9; break;
    		case PFD_ALTITUDE :failure_index = (gauges_failure_data & 0x007000) >> 12; break;
    		case PFD_ATTITUDE :failure_index = (gauges_failure_data & 0x038000) >> 15; break;    		
    	}
   	 	
    	return FailureMode.values()[failure_index];  	
    }
 
    public void set_failure(FailedElement element, FailureMode mode) {
    	// Will allow XHSI to trigger failures
    	// Feature should be implemented with the Instructor mode of XSHI
    }
    
    // Failures
    public boolean att_valid () {
    	int qpac_failures_data=0;
    	if (is_qpac()) {
    		qpac_failures_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FAILURES));
    	}
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0004) > 0 ? true : false;
    		} 
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0040) > 0 ? true : false;
    		} 
    	} 
    	// Instructor : always true
    	return true;
    }
    
    public boolean hdg_valid () {
    	int qpac_failures_data=0;
    	if (is_qpac()) {
    		qpac_failures_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FAILURES));
    	}
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0008) > 0 ? true : false;
    		} 
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0080) > 0 ? true : false;
    		} 
    	} 
    	// Instructor : always true
    	return true;
    	
    }

 
    public boolean ias_valid () {
    	int qpac_failures_data=0;
    	if (is_qpac()) {
    		qpac_failures_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FAILURES));
    	}
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0002) > 0 ? true : false;
    		} 
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0020) > 0 ? true : false;
    		} 
    	} 
    	// Instructor : always true
    	return true;
    };


    public boolean alt_valid () {
    	int qpac_failures_data=0;
    	if (is_qpac()) {
    		qpac_failures_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FAILURES));
    	}
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0001) > 0 ? true : false;
    		} 
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0010) > 0 ? true : false;
    		} 
    	} 
    	// Instructor : always true
    	return true;
    };
  

}
