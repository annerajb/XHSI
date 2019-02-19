/**
* XPlaneAvionics.java
* 
* The X-Plane specific implementation of Avionics.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (c) 2016  Saso Kiselkov
* Copyright (c) 2019  Nicolas Carel
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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.XHSIInstrument.DU;
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
    
    private DecimalFormat runway_formatter;
    
    // Retain system year
    private int year; 

    /* Internal X-Plane failure values
     * always working = 0
     * fail at mean_time = 1
	 * fail at exact time = 2
	 * fail at speed = 3
	 * fail at altitude = 4
	 * fail at key = 5
	 * inoperative now = 6
     */

    
    @SuppressWarnings("deprecation")
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
        
        this.runway_formatter = new DecimalFormat("00");
        
        /*
         * This code maintains java 1.6 compatibility
         */
        Calendar cal = Calendar.getInstance();
        Date current_date = cal.getTime();
        this.year = current_date.getYear();

    }

    /*
     * Function to send only one bit 
     */
    private void sendBitPoint(int id, int bit, boolean value) {
    	int id_value = (int)sim_data.get_sim_float(id);
    	int bit_mask = 1 << bit;
    	int complement = 0xFFFF ^ bit_mask;
        udp_sender.sendDataPoint( id, value ? id_value | bit_mask : id_value & complement );
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
        else if ( radio_num == RADIO_COM1_833 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_FREQUENCY_HZ_833);
        else if ( radio_num == RADIO_COM2 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_COM2_FREQ_HZ);
        else if ( radio_num == RADIO_COM2_833 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_FREQUENCY_HZ_833);
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
        else if ( radio_num == RADIO_COM1_STDBY_833 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_STANDBY_FREQUENCY_HZ_833);
        else if ( radio_num == RADIO_COM2_STDBY )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ);
        else if ( radio_num == RADIO_COM2_STDBY_833 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_STANDBY_FREQUENCY_HZ_833);
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


    public boolean com1_is_833() {
        return ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_FREQUENCY_HZ_833) % 25 ) != 0;
    }
    
    public boolean com1_standby_is_833() {
        return ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_STANDBY_FREQUENCY_HZ_833) % 25 ) != 0;
    }
    
    public boolean com2_is_833() {
        return ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_FREQUENCY_HZ_833) % 25 ) != 0;
    }
    
    public boolean com2_standby_is_833() {
        return ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_STANDBY_FREQUENCY_HZ_833) % 25 ) != 0;
    }
    
    public boolean contact_atc() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RTU_CONTACT_ATC) != 0.0f;
    };
    
    public boolean pilotedge_rx() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RTU_PILOTEDGE_RX) != 0.0f;
    };
    
    public int rtu_selected_radio() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RTU_SELECTED_RADIO);
    };
    

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

    public InstrumentSide get_instrument_side() {
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT )) {
    		return InstrumentSide.PILOT;
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT )) {
    		return InstrumentSide.COPILOT;
    	} else 
    		return InstrumentSide.INSTRUCTOR;
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


    /**
     * @return boolean - Display Unit Power
     * TODO : tie with electric buses
     */
    public boolean du_power(DU display_unit) {
    	return du_power(display_unit, get_instrument_side());
    }
    
    public boolean du_power(DU display_unit, InstrumentSide side) {
    	return power();
    }
  
    /**
     * @return float - Display Unit Brightness
     */
    public float du_brightness(DU display_unit) {
    	return du_brightness(display_unit, get_instrument_side());
    }
    
    public float du_brightness(DU display_unit, InstrumentSide side) {
    	switch (display_unit) {
    	case Empty : return 0.0f;
    	case PFD : if (side == InstrumentSide.COPILOT) {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_PFD_FO));
    	} else {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_PFD_CPT));
    	}
    	case ND : if (side == InstrumentSide.COPILOT) {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_ND_FO));
    	} else {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_ND_CPT));
    	}
    	case EICAS : return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_EICAS));
    	case MFD : return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_MFD));
    	case CDU : if (side == InstrumentSide.COPILOT) {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_CDU_FO));
    	} else if (side == InstrumentSide.PILOT) {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_CDU_CPT));
    	} else {
    		return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_CDU_OBS));
    	}
    	case Annunciators : return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_INSTRUMENT_BRIGHTNESS));
    	case Clock : return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_DU_BRIGHT_CLOCK));
    	default: return 1.0f;
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
        // for x737 : -1:5
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (this.qpac_version() > 150) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_CAPT)); 
    		} else if (is_x737()) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_ND_RANGE_ENUM)); 
    		} else {
    			return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
    		}
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (this.qpac_version() > 150) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_FO)); 
    		} else if (is_x737()) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_ND_RANGE_ENUM)); 
    		} else {
    			return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE);
    		}
        } else {
            return xhsi_settings.map_range_index;
        }

    }

    public int map_range_index(InstrumentSide side) {
        // ranges: 0:10, 1:20, 2:40, 3:80, 4:160, 5:320, 6:640
        // for x737 : -1:5
        if ( side == InstrumentSide.PILOT ) {
    		if (this.qpac_version() > 150) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_CAPT)); 
    		} else if (is_x737()) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_ND_RANGE_ENUM)); 
    		} else {
    			return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
    		}
        } else if ( side == InstrumentSide.COPILOT ){
    		if (this.qpac_version() > 150) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_FO)); 
    		} else if (is_x737()) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_ND_RANGE_ENUM)); 
    		} else {
    			return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE);
    		}
        } else {
            return xhsi_settings.map_range_index;
        }
    }
    

    public int map_range() {

        // ranges: 10, 20, 40, 80, 160, 320, 640
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (this.qpac_version() > 150) {
    			return EFIS_MAP_RANGE[ (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_CAPT))]; 
    		} else if (is_x737()) {
    			return X737_MAP_RANGE[ (int) (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_ND_RANGE_ENUM))]; 
    		} else {
    			return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR) ];
    		}
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (this.qpac_version() > 150) {
    			return EFIS_MAP_RANGE[ (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_FO))]; 
    		} else if (is_x737()) {
    			return X737_MAP_RANGE[ (int) (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_ND_RANGE_ENUM))]; 
    		} else {
    			return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE) ];
    		}
        } else {
            return xhsi_settings.map_range;
        }

    }

    public int map_range(InstrumentSide side) {
        // ranges: 10, 20, 40, 80, 160, 320, 640
        if ( side == InstrumentSide.PILOT ) {
    		if (this.qpac_version() > 150) {
    			return EFIS_MAP_RANGE[ (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_CAPT))]; 
    		} else {
    			return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR) ];
    		}
        } else if ( side == InstrumentSide.COPILOT ) {
    		if (this.qpac_version() > 150) {
    			return EFIS_MAP_RANGE[ (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_RANGE_FO))]; 
    		} else {
    			return EFIS_MAP_RANGE[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_RANGE) ];
    		}
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

    public boolean map_zoomin(InstrumentSide side) {
        if ( side == InstrumentSide.PILOT ) {
            return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_MAP_ZOOMIN) == 1.0f;
        } else if ( side == InstrumentSide.COPILOT ){
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

    public int map_mode(InstrumentSide side) {
        // modes: 0=centered, 1=expanded (see the constants in model/Avionics)
        if ( side == InstrumentSide.PILOT ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE));
        } else if ( side == InstrumentSide.COPILOT ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_CTR));
        } else {
            return xhsi_settings.map_centered;
        }
    }

    public int map_submode() {
    	
    	// submodes: 0=APP, 1=VOR, 2=MAP, 3=NAV, 4=PLN (see the constants in model/Avionics)
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		if (this.qpac_version() > 110) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_MODE_CAPT)); 
    		} else {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE));
    		}
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (this.qpac_version() > 110) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_MODE_FO)); 
    		} else {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_MODE));
    		}
    	} else {
    		return xhsi_settings.map_mode;
    	}

    }

    public int map_submode(InstrumentSide side) {   	
    	// submodes: 0=APP, 1=VOR, 2=MAP, 3=NAV, 4=PLN (see the constants in model/Avionics)
    	if ( side == InstrumentSide.PILOT ) {
    		if (this.qpac_version() > 110) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_MODE_CAPT)); 
    		} else {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE));
    		}
    	} else if ( side == InstrumentSide.COPILOT ) {
    		if (this.qpac_version() > 110) {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_EFIS_ND_MODE_FO)); 
    		} else {
    			return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MAP_MODE));
    		}
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

    public int efis_radio1(InstrumentSide side) {
        if ( side == InstrumentSide.PILOT ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR));
        } else if ( side == InstrumentSide.COPILOT ) {
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

    public int efis_radio2(InstrumentSide side) {
        if ( side == InstrumentSide.PILOT ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR));
        } else if ( side == InstrumentSide.COPILOT ) {
            return (int) (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_RADIO2));
        } else {
            return xhsi_settings.radio2;
        }
    }

    public boolean efis_shows_cstr() {
    	// TODO: Implement show constraints EFIS button
    	return true;
    }
    
    public boolean efis_shows_cstr(InstrumentSide side) {
    	return true;
    }
   

    public boolean efis_shows_wpt() {

        if ( is_x737() ) {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_WPT) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_WPT) == 1.0f);
            } else {
                return xhsi_settings.show_wpt;
            }
        } else {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT) == 1.0f);
            } else {
                return xhsi_settings.show_wpt;
            }
        }

    }

    public boolean efis_shows_wpt(InstrumentSide side) {
        if ( is_x737() ) {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_WPT) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_WPT) == 1.0f);
            } else {
                return xhsi_settings.show_wpt;
            }
        } else {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT) == 1.0f);
            } else {
                return xhsi_settings.show_wpt;
            }
        }
    }


    public boolean efis_shows_ndb() {

        if ( is_x737() ) {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_STA) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_STA) == 1.0f);
            } else {
                return xhsi_settings.show_ndb;
            }
        } else {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB) == 1.0f);
            } else {
                return xhsi_settings.show_ndb;
            }
        }

    }

    public boolean efis_shows_ndb(InstrumentSide side) {
        if ( is_x737() ) {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_STA) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_STA) == 1.0f);
            } else {
                return xhsi_settings.show_ndb;
            }
        } else {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB) == 1.0f);
            } else {
                return xhsi_settings.show_ndb;
            }
        }
}


    public boolean efis_shows_vor() {

        if ( is_x737() ) {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_STA) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_STA) == 1.0f);
            } else {
                return xhsi_settings.show_vor;
            }
        } else {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR) == 1.0f);
            } else {
                return xhsi_settings.show_vor;
            }
        }

    }

    public boolean efis_shows_vor(InstrumentSide side) {
        if ( is_x737() ) {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_STA) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_STA) == 1.0f);
            } else {
                return xhsi_settings.show_vor;
            }
        } else {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR) == 1.0f);
            } else {
                return xhsi_settings.show_vor;
            }
        }
    }


    public boolean efis_shows_arpt() {

        if ( is_x737() ) {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_ARPT) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_ARPT) == 1.0f);
            } else {
                return xhsi_settings.show_arpt;
            }
        } else {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT) == 1.0f);
            } else {
                return xhsi_settings.show_arpt;
            }
        }

    }

    public boolean efis_shows_arpt(InstrumentSide side) {
        if ( is_x737() ) {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_ARPT) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_ARPT) == 1.0f);
            } else {
                return xhsi_settings.show_arpt;
            }
        } else {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT) == 1.0f);
            } else {
                return xhsi_settings.show_arpt;
            }
        }
    }


    public boolean efis_shows_tfc() {

        if ( is_x737() ) {
            if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_TFC) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_TFC) == 1.0f);
            } else {
                return xhsi_settings.show_tfc;
            }
        } else {
            if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TFC) == 1.0f);
            } else {
                return xhsi_settings.show_tfc;
            }
        }
    }

    public boolean efis_shows_tfc(InstrumentSide side) {
        if ( is_x737() ) {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_TFC) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_TFC) == 1.0f);
            } else {
                return xhsi_settings.show_tfc;
            }
        } else {
            if ( side == InstrumentSide.PILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS) == 1.0f);
            } else if ( side == InstrumentSide.COPILOT ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TFC) == 1.0f);
            } else {
                return xhsi_settings.show_tfc;
            }
        }
    }


    public boolean efis_shows_data() {

        if ( is_x737() ) {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_DATA) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_DATA) == 1.0f);
            } else {
                return xhsi_settings.show_data;
            }
        } else {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_DATA) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_DATA) == 1.0f);
            } else {
                return xhsi_settings.show_data;
            }
        }

    }


    public boolean efis_shows_pos() {

        if ( is_x737() ) {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS0_POS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.X737_EFIS1_POS) == 1.0f);
            } else {
                return xhsi_settings.show_pos;
            }
        } else {
            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_POS) == 1.0f);
            } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_POS) == 1.0f);
            } else {
                return xhsi_settings.show_pos;
            }
        }

    }
  
    public boolean efis_shows_terrain() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_TERRAIN) == 1.0f);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TERRAIN) == 1.0f);
    	} else {
    		return xhsi_settings.show_terrain;
    	}
    }
    
    public boolean efis_shows_terrain(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_TERRAIN) == 1.0f);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TERRAIN) == 1.0f);
    	} else {
    		return xhsi_settings.show_terrain;
    	}
    }
    
    public boolean efis_shows_vp() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_VP) == 1.0f);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VP) == 1.0f);
    	} else {
    		return xhsi_settings.show_vp;
    	}
    }
    
    public boolean efis_shows_vp(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_VP) == 1.0f);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VP) == 1.0f);
    	} else {
    		return xhsi_settings.show_vp;
    	}
    }
    
    /**   
    * @return boolean - true if EFIS displays Weather radar, false otherwise
    */
    public boolean efis_shows_wxr() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER) == 1.0f);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR) == 1.0f);
    	} else {
    		return xhsi_settings.show_weather;
    	}
    }
    public boolean efis_shows_wxr(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER) == 1.0f);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR) == 1.0f);
    	} else {
    		return xhsi_settings.show_weather;
    	}	
    }
   
    /*
     * Chrono and timers
     */
    
    public float efis_chrono_elapsed_time() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_ELAPSED_TIME_SEC);
    	else
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ELAPSED_TIME_SEC);
    }
    
    public float efis_chrono_elapsed_time(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_ELAPSED_TIME_SEC);
    	else
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ELAPSED_TIME_SEC);
    }
    
    public int clock_date_day() {
    	return ((int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_DATE)) & 0x1F ;
    }
    
    public int clock_date_month() {
    	return ((int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_DATE)) >> 6;
    };

    public int clock_date_year() {
    	return this.year;
    }
      
    public boolean clock_date_show() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_SHOW_DATE) != 0.0f;
    }

    public int clock_utc_source() {
    	return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_TIME_UTC_SOURCE);
    }
    
    public int clock_et_selector() {
    	return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_TIME_ET_RUNNING);
    }
    
    
    public float wxr_gain() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_GAIN);
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_GAIN);
    	} else {
    		return xhsi_settings.wxr_gain;
    	}
    }
    public float wxr_gain(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_GAIN);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_GAIN);
    	} else {
    		return xhsi_settings.wxr_gain;
    	}	
    }
   

    public float wxr_tilt() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_TILT);
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_TILT);
    	} else {
    		return xhsi_settings.wxr_tilt;
    	}
    }
    public float wxr_tilt(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_TILT);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_TILT);
    	} else {
    		return xhsi_settings.wxr_tilt;
    	}	
    }
    /*
    wxr_opt [ XHSI_EFIS_PILOT_WXR_OPT }
		 (XPLMGetDatai(efis_pilot_wxr_target) & 0x01) << 7 |
		 (XPLMGetDatai(efis_pilot_wxr_alert) & 0x01) << 6 |
		 (XPLMGetDatai(efis_pilot_wxr_narrow) & 0x01) << 5 |
		 (XPLMGetDatai(efis_pilot_wxr_react) & 0x01) << 4 |
		 (XPLMGetDatai(efis_pilot_wxr_slave) & 0x01) << 3 |
		 (XPLMGetDatai(efis_pilot_wxr_auto_tilt) & 0x01) << 2 |
		 (XPLMGetDatai(efis_pilot_wxr_auto_gain) & 0x01 ) << 1 |
		 (XPLMGetDatai(efis_pilot_wxr_test) & 0x01 );
		 */
    
    /**   
     * @return boolean - weather radar automatic gain adjust
     */     
    public boolean wxr_auto_gain() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x02) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x02) != 0;
    	} else {
    		return xhsi_settings.wxr_auto_gain;
    	}
    };
    
    /**   
     * @return boolean - weather radar automatic tilt adjust
     */     
    public boolean wxr_auto_tilt() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x04) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x04) != 0;
    	} else {
    		return xhsi_settings.wxr_auto_tilt;
    	}
    };;
    
    
    /**   
     * @return float - weather radar automatic tilt value [-15.0° - +15.0°]
     */     
    public float wxr_auto_tilt_value(float altitude, float range) {
    	/* 
    	 * on ground : +5.0
    	 * at 10000 ft : +2.0
    	 * Recommended Over Water Tilt Settings (Collins WXR-2100)
    	 * Altitude
    	 * (feet) 40 NM 80 NM 160 NM
    	 * 40,000 -7° -3° -2°
    	 * 35.000 -6° -2° -1°
    	 * 30,000 -4° -1° 0°
    	 * 25,000 -3° -1° 0°
    	 * 20,000 -2° 0° +1°
    	 */
    	float tilt = 5.0f; // Default value on ground
    	if (altitude < 10000.0f) { 
    		tilt = 5.0f - altitude/3333.0f;
    	} else {
    		tilt = 2.0f - (altitude - 10000.0f)/6666.0f;
    	}
    	
    	// Round to 0.25° step	    	
    	// return Math.round(tilt*10)/10;
    	return tilt;
    }
    
    /**   
     * @return boolean - weather radar test
     */     
    public boolean wxr_test() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x01) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x01) != 0;
    	} else {
    		return xhsi_settings.wxr_test;
    	}
    }

    /**   
     * @return boolean - weather active 
     * Active when on and not on ground, or when forced on (mode=4)
     */  
    public boolean wxr_active() {
    	int mode = wxr_mode();
    	boolean shows= efis_shows_wxr();
    	if ( (mode==0) || !shows) 
    		return false;
    	else if ((mode==4) && shows) 
    		return true;
    	else 
    		return shows && !aircraft.on_ground();
    }
    
    /**   
     * @return int - weather radar mode
     */     
    public int wxr_mode() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_MODE);
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_MODE);
    	} else {
    		return xhsi_settings.wxr_mode;
    	}
    }
    
    /**   
     * @return boolean - weather radar slave settings
     */     
    public boolean wxr_slave() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x08) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x08) != 0;
    	} else {
    		return xhsi_settings.wxr_slave;
    	}
    }
    
    /**   
     * @return boolean - weather radar REACT
     * Rain Echo Attenuation Compensation Technique
     */        
    public boolean wxr_react() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x10) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x10) != 0;
    	} else {
    		return xhsi_settings.wxr_react;
    	}
    };
    
    /**   
     * @return boolean - weather radar narrow mode (60° instead of 120° sweep range)
     */        
    public boolean wxr_narrow() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x20) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x20) != 0;
    	} else {
    		return xhsi_settings.wxr_narrow;
    	}
    };
    
    /**   
     * @return boolean - weather radar alert mode
     */    
    public boolean wxr_alert() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x40) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x40) != 0;
    	} else {
    		return xhsi_settings.wxr_alert;
    	}
    };
    
    /**   
     * @return boolean - weather radar target mode
     */    
    public boolean wxr_target() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) 
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT)) & 0x80) != 0;
    	else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT)) & 0x80) != 0;
    	} else {
    		return xhsi_settings.wxr_target;
    	}
    };
    
    public int qpac_get_mfd_mode() {
    	int sd_page = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_SD_PAGE);
    	switch (sd_page) {
    		case 0 : return Avionics.MFD_MODE_EICAS; 
    		case 1 : return Avionics.MFD_MODE_BLEED; 
    		case 2 : return Avionics.MFD_MODE_CAB_PRESS; 
    		case 3 : return Avionics.MFD_MODE_ELEC; 
    		case 4 : return Avionics.MFD_MODE_HYDR; 
    		case 5 : return Avionics.MFD_MODE_FUEL; 
    		case 6 : return Avionics.MFD_MODE_APU; 
    		case 7 : return Avionics.MFD_MODE_COND; 
    		case 8 : return Avionics.MFD_MODE_DOOR_OXY; 
    		case 9 : return Avionics.MFD_MODE_WHEELS; 
    		case 10: return Avionics.MFD_MODE_FCTL; 
    		case 11: return Avionics.MFD_MODE_SYS; 
    		case 12: return Avionics.MFD_MODE_STATUS;     	
    	}
    	return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_MFD_MODE);
    }

    public int jar_a320neo_get_mfd_mode() {
    	int sd_page = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_SD_PAGE);
    	switch (sd_page) {
    		case 0 : return Avionics.MFD_MODE_HYDR; 
    		case 1 : return Avionics.MFD_MODE_FUEL; 
    		case 2 : return Avionics.MFD_MODE_APU; 
    		case 3 : return Avionics.MFD_MODE_CAB_PRESS; 
    		case 4 : return Avionics.MFD_MODE_FCTL; 
    		case 5 : return Avionics.MFD_MODE_WHEELS; 
    		case 6 : return Avionics.MFD_MODE_ELEC; 
    		case 7 : return Avionics.MFD_MODE_BLEED; 
    		case 8 : return Avionics.MFD_MODE_COND; 
    		case 9 : return Avionics.MFD_MODE_DOOR_OXY; 
    		case 10: return Avionics.MFD_MODE_SYS; 
    		case 11: return Avionics.MFD_MODE_ENGINE;      	
    	}
    	return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_MFD_MODE);
    }
    
    public int ff_a320_get_mfd_mode() {
        if (is_ff_a320()) {
            int ffValue = (int) sim_data.get_sim_float(XPlaneSimDataRepository.XFF_MFD_BUTTONS) & 0x1FFF;
            switch (ffValue) {
                case 0x01:
                    return Avionics.MFD_MODE_EICAS;
                case 0x02:
                    return Avionics.MFD_MODE_BLEED;
                case 0x04:
                    return Avionics.MFD_MODE_CAB_PRESS;
                case 0x08:
                    return Avionics.MFD_MODE_ELEC;
                case 0x10:
                    return Avionics.MFD_MODE_HYDR;
                case 0x20:
                    return Avionics.MFD_MODE_FUEL;
                case 0x40:
                    return Avionics.MFD_MODE_APU;
                case 0x80:
                    return Avionics.MFD_MODE_COND;
                case 0x100:
                    return Avionics.MFD_MODE_DOOR_OXY;
                case 0x200:
                    return Avionics.MFD_MODE_WHEELS;
                case 0x400:
                    return Avionics.MFD_MODE_FCTL;
                case 0x800:
                    return Avionics.MFD_MODE_SYS;
                case 0x1000:
                    return Avionics.MFD_MODE_STATUS;
            }
        }
        return (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_MFD_MODE);
    }
    
    public int get_mfd_mode() {
    	int xhsi_mfd_mode = (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_MFD_MODE);

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            return xhsi_settings.mfd_mode;
        } else {
            if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_SWITCHABLE)) {
                return xhsi_mfd_mode;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_LINKED)) {
            	if (is_qpac() && (xhsi_mfd_mode != Avionics.MFD_MODE_FPLN) && (xhsi_mfd_mode != Avionics.MFD_MODE_ARPT) && (xhsi_mfd_mode != Avionics.MFD_MODE_RTU)) {
            		return qpac_get_mfd_mode();
            	} else if (is_jar_a320neo() && (xhsi_mfd_mode != Avionics.MFD_MODE_FPLN) && (xhsi_mfd_mode != Avionics.MFD_MODE_ARPT) && (xhsi_mfd_mode != Avionics.MFD_MODE_RTU)) {
            		return jar_a320neo_get_mfd_mode();
                } else if (is_ff_a320() && (xhsi_mfd_mode != Avionics.MFD_MODE_FPLN) && (xhsi_mfd_mode != Avionics.MFD_MODE_ARPT) && (xhsi_mfd_mode != Avionics.MFD_MODE_RTU)) {
                    return ff_a320_get_mfd_mode();
            	} else {
            		// like mode_switchable
            		return xhsi_mfd_mode;
            	}
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_ARPT_CHART)) {
                return Avionics.MFD_MODE_ARPT;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_FPLN)) {
                return Avionics.MFD_MODE_FPLN;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_LOWER_EICAS)) {
                return Avionics.MFD_MODE_EICAS;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_RTU)) {
                return Avionics.MFD_MODE_RTU;               
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_SYS)) {
                return Avionics.MFD_MODE_SYS;               
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_FCTL)) {
                return Avionics.MFD_MODE_FCTL;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_APU)) {
                return Avionics.MFD_MODE_APU;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_ELEC)) {
                return Avionics.MFD_MODE_ELEC;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_WHEELS)) {
                return Avionics.MFD_MODE_WHEELS;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_DOOR_OXY)) {
                return Avionics.MFD_MODE_DOOR_OXY;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_BLEED)) {
                return Avionics.MFD_MODE_BLEED;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_COND)) {
                return Avionics.MFD_MODE_COND;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_FUEL)) {
                return Avionics.MFD_MODE_FUEL;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_CAB_PRESS)) {
                return Avionics.MFD_MODE_CAB_PRESS;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_HYDR)) {
                return Avionics.MFD_MODE_HYDR;
            } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_MFD_MODE).equals(XHSIPreferences.MFD_MODE_STATUS)) {
                return Avionics.MFD_MODE_STATUS;
            } else {
                return Avionics.MFD_MODE_EICAS;
            }
            // TODO : add pages

        }

    }


    public int get_cdu_source() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		// PILOT CDU
    		if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_SWITCHABLE)) {
    			return ((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CDU_SOURCE)) & 0x0F;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_AIRCRAFT_OR_DUMMY)) {
    			return Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_XFMC)) {
    			return Avionics.CDU_SOURCE_XFMC;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_UFMC)) {
    			return Avionics.CDU_SOURCE_UFMC;
    		} else {
    			// Error, fallback
    			return Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY;
    		}
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		// COPILOT CDU
    		if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_SWITCHABLE)) {
    			return (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CDU_SOURCE)) & 0xF0) >> 4;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_AIRCRAFT_OR_DUMMY)) {
    			return Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_XFMC)) {
    			return Avionics.CDU_SOURCE_XFMC;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SOURCE).equals(XHSIPreferences.CDU_SOURCE_UFMC)) {
    			return Avionics.CDU_SOURCE_UFMC;
    		} else {
    			// Error, fallback
    			return Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY;
    		}
    	} else {
    		// INSTRUCTOR CDU
    		return xhsi_settings.cdu_source;
    	}    	
    }

    public int get_cdu_side() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		// PILOT CDU
    		int pilot_cdu_side = ((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CDU_SIDE)) & 0x0F;
    		if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_SWITCHABLE)) {
    			return pilot_cdu_side;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_LEFT)) {
    			// if (pilot_cdu_side != Avionics.CDU_LEFT) set_cdu_side(Avionics.CDU_LEFT);
    			return Avionics.CDU_LEFT;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_RIGHT)) {
    			// if (pilot_cdu_side != Avionics.CDU_RIGHT) set_cdu_side(Avionics.CDU_RIGHT);
    			return Avionics.CDU_RIGHT;
    		} else {
    			// Error, fallback
    			return Avionics.CDU_LEFT;
    		}
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		// COPILOT CDU
    		int copilot_cdu_side = (((int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CDU_SIDE)) & 0xF0) >> 4;
    		if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_SWITCHABLE)) {
    			return copilot_cdu_side;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_LEFT)) {
    			if (copilot_cdu_side != Avionics.CDU_LEFT) set_cdu_side(Avionics.CDU_LEFT);
    			return Avionics.CDU_LEFT;
    		} else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_CDU_SIDE).equals(XHSIPreferences.CDU_SIDE_RIGHT)) {
    			if (copilot_cdu_side != Avionics.CDU_RIGHT) set_cdu_side(Avionics.CDU_RIGHT);
    			return Avionics.CDU_RIGHT;
    		} else {
    			// Error, fallback
    			return Avionics.CDU_RIGHT;
    		}
    	} else {
    		// INSTRUCTOR CDU
    		return xhsi_settings.cdu_side;
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

    /**
     * @return int - 0=Celcius, 1=Farhenheit, 2=Kelvin
     */
    public int get_temp_units() {

        if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_TEMP_UNITS).equals(XHSIPreferences.TEMP_UNITS_SWITCHABLE) ) {
            return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_TEMP_UNITS);
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_TEMP_UNITS).equals(XHSIPreferences.TEMP_UNITS_CELCIUS) ) {
            return XHSISettings.TEMP_UNITS_C;
        } else /* if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.TEMP_UNITS_FAHRENHEIT) ) { */
            return XHSISettings.TEMP_UNITS_F;
        /* Uncomment to activate Kelvins 
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS).equals(XHSIPreferences.FUEL_UNITS_KELVIN) ) 
            return XHSISettings.TEMP_UNITS_K;
        */

    };
  
    /**
     * @return float / if temp_units in fahrenheit : T(°F) = 1,8 T(°C) + 32
     */
    public float convert_temperature(float temp_in_celcius) {
    	if (get_temp_units() == XHSISettings.TEMP_UNITS_C) {
    		return temp_in_celcius;
    	} else {
    		return temp_in_celcius * 1.8f + 32.0f;
    	}
    }
    
    
    
    public int get_engine_type() {

        if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_SWITCHABLE) ) {
            return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_ENGINE_TYPE);
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_N1) ) {
            return XHSISettings.ENGINE_TYPE_N1;
        } else if ( xhsi_preferences.get_preference(XHSIPreferences.PREF_ENGINE_TYPE).equals(XHSIPreferences.ENGINE_TYPE_EPR) ) {
            return XHSISettings.ENGINE_TYPE_EPR;
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

    /**
     * Autoland warning conditions:
     *  - Airborne
     *  - G/S LOC captured mode
     *  - Autopilot on 
     *  
     * Below 200 ft, the AUTOLAND red light illuminates if
     * - Both APs trip off
     * - Excessive beam deviation is sensed
     * - Localizer or glide slope transmitter or receiver fails
     * - A RA discrepancy of at least 15 ft is sensed.
     * TODO: This flag should be computed inside the plugin.
     */
    public boolean autopilot_autoland_warning() {
    	return false;
    }
    
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

    public float acf_pitch() {

        return sim_data.get_sim_float(XPlaneSimDataRepository.DUPLICATE_THETA_FOR_PITCH);

    }

    public float fd_roll() {

        if ( is_x737() ) {

            if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
                return sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_B_ROLL) * 10.0f;
            } else {
//logger.warning("FD_roll: "+sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_A_ROLL));
                return sim_data.get_sim_float(XPlaneSimDataRepository.X737_AFDS_A_ROLL) * 10.0f;
            }

        } else {
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_FD_ROLL);
        }

    }

    public float acf_bank() {

        return sim_data.get_sim_float(XPlaneSimDataRepository.DUPLICATE_PHI_FOR_BANK);

    }

    /**
     * @return integer - FWC (Flight Warning Computer) flight phase
     * 0 : power off
     * 1 : electric power on
     * 2 : 1st engine started
     * 3 : 1st engine to TOGA/FLEX power
     * 4 : 80 kts
     * 5 : Lift off
     * 6 : 1500 ft radio or 2mn after lift off - cruise period
     * 7 : 800 ft radio
     * 8 : Touch down
     * 9 : 80 kts
     * 10 : 2nd engine shutdown
     * 0 or 1 : 5 mn after phase 10
     * 
     */
    public int fwc_phase() {
    	// TODO: Create FWC dataref and computer flight phase in the plugin
    	return 0;
    }
    
    /**
     * @return boolean - Takeoff Alarms Inhibit
     * Airbus: FCOM 1.31.15, FWC flight phase 3, 4, 5
     *         from TOGA on ground to 1500 ft radio or 2mn after takeoff
     * General aviation: from takeoff thrust on ground up to 500 feet or 2mn after takeoff
     */
    public boolean to_inhibit() {
    	int phase = fwc_phase();
    	return (phase == 3) || (phase == 4) || (phase == 5);
    }
    
    /**
     * @return boolean - Landing Alarms Inhibit
     * Airbus: FCOM 1.31.15, FWC flight phase 7, 8
     *         Airbone < 800 ft to 80 kts on ground
     * General aviation: Airbone < 300 ft to 40 kts on ground
     */
    public boolean ldg_inhibit() {
    	int phase = fwc_phase();
    	return (phase == 7) || (phase == 8);
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

    public boolean transponder_ident() {

        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_TRANSPONDER_ID) != 0.0f;

    };
    
    public boolean clock_shows_utc() {
        return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CLOCK_TIMER_MODE) == 0.0f );
    }

    public float nav1_obs() { 
        float obs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM) );
        float crs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM) );
        if ( xhsi_preferences.is_auto_frontcourse_to_obs() && get_nav_radio(1).freq_is_localizer() && ( Math.round(obs*10.0f) != Math.round(crs*10.0f) ) ) {
            set_nav1_obs( crs );
        }
        return obs;
    }

    public float nav2_obs() {
        float obs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM) );
        float crs = normalize( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM) );
        if ( xhsi_preferences.is_auto_frontcourse_to_obs() && get_nav_radio(2).freq_is_localizer() && ( Math.round(obs*10.0f) != Math.round(crs*10.0f) ) ) {
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

    public boolean gps_gs_active() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_GPS_HAS_GLIDESLOPE) != 0.0f; }
    
    public String gps_nav_id() { return sim_data.get_sim_string(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_GPS_NAV_ID_0_3) + sim_data.get_sim_string(XPlaneSimDataRepository.SIM_COCKPIT2_RADIOS_INDICATORS_GPS_NAV_ID_4_7); }
    

    // PFD Display options
    public boolean pfd_shows_metric_alt() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_METRIC_ALT) == 1.0f);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_METRIC_ALT) == 1.0f);
    	} else {
    		return xhsi_settings.metric_alt;
    	}
    }
    
    public boolean pfd_shows_metric_alt(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_METRIC_ALT) == 1.0f);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_METRIC_ALT) == 1.0f);
    	} else {
    		return xhsi_settings.metric_alt;
    	}
    }
  
    public boolean pfd_shows_ils() {
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_ILS) == 1.0f);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ILS) == 1.0f);
    	} else {
    		return xhsi_settings.show_ils;
    	}
    }
    
    public boolean pfd_shows_ils(InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_ILS) == 1.0f);
    	} else if ( side == InstrumentSide.COPILOT ) {
    		return (sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ILS) == 1.0f);
    	} else {
    		return xhsi_settings.show_ils;
    	}
    }
    
    public boolean pfd_shows_baro_hpa () {
    	if (this.is_qpac()) 
    		return qpac_baro_unit(); 
    	else if (this.is_jar_a320neo() ) 
    		return jar_a320neo_baro_unit(); 
    	else return false;
    }

    public boolean pfd_shows_baro_hpa (InstrumentSide side) {
    	if (this.is_qpac()) 
    		return qpac_baro_unit(side); 
    	else if (this.is_jar_a320neo() ) 
    		return jar_a320neo_baro_unit(); 
    	else return false;
    }

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

    // General Airbus presence flag
    public boolean is_airbus() {
        return is_qpac() || is_ff_a320() || is_jar_a320neo();
    }
    
    // QPAC Airbus FBW
    public boolean is_qpac() {
    	return ( sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS) > 0.0f );
    }
    
    public int qpac_version() {
    	return Math.round( sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS));
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
    public boolean qpac_baro_std() {
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))  
    		return (qpac_fcu_baro & 0x40) > 0 ? true : false;
    	else 
    		return (qpac_fcu_baro & 0x04) > 0 ? true : false;    	 	
    }
    public boolean qpac_baro_std(InstrumentSide side) {
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if ( side != InstrumentSide.PILOT )  
    		return (qpac_fcu_baro & 0x40) > 0 ? true : false;
    	else 
    		return (qpac_fcu_baro & 0x04) > 0 ? true : false;    	 	
    }
    public boolean qpac_baro_unit() {
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))  
    		return (qpac_fcu_baro & 0x20) > 0 ? true : false;
    	else 
    		return (qpac_fcu_baro & 0x02) > 0 ? true : false; 	
    }
    public boolean qpac_baro_unit(InstrumentSide side) {
    	int qpac_fcu_baro = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCU_BARO));
    	if ( side != InstrumentSide.PILOT )  
    		return (qpac_fcu_baro & 0x20) > 0 ? true : false;
    	else 
    		return (qpac_fcu_baro & 0x02) > 0 ? true : false; 	
    }
    public boolean qpac_baro_hide() {
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
    
    public boolean qpac_ils_on(){
    	int qpac_ils_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_FLAGS));
    	if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ))
    		return (qpac_ils_data & 0x20) > 0 ? true : false; 	
    	else
    		return (qpac_ils_data & 0x10) > 0 ? true : false;	   	
    }
    
    public boolean qpac_ils_on(InstrumentSide side) {
    	int qpac_ils_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ILS_FLAGS));
    	if ( side != InstrumentSide.PILOT )
    		return (qpac_ils_data & 0x20) > 0 ? true : false; 	
    	else
    		return (qpac_ils_data & 0x10) > 0 ? true : false;	   	
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
    	// Only QPAC FD2 yaw bar is transmitting. 
    	// With QPAC v2 range is not between -0.5 and +0.5 but -20 to +20 visible
    	if (qpac_version() >= 202) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_YAW_BAR)/-40.0f;
    	} else 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FD2_YAW_BAR);
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
    public int qpac_vfe_next() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_VFE_NEXT));
    }  
    // Failures
    public float qpac_failures() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FAILURES);
    }
    // Flight controls computers
    public boolean qpac_fcc(int pos) {
    	int fcc_status = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCC));
        return ((fcc_status >> pos) & 0x01) != 0;
    }
    public boolean qpac_fcc(FCC pos) {
        int fcc_status = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FCC));
        return ((fcc_status >> pos.ordinal()) & 0x01) != 0;
    }

    public boolean fcc(FCC pos) {
        if (is_qpac()) {
            return qpac_fcc(pos);
        } else if (is_ff_a320()) {
            return !ff_a320_fcc_off(pos);
        } else {
            return false;
        }
    }
    
    // Flight Factor Airbus 320 Ultimate
    public boolean is_ff_a320() {
        return (sim_data.get_sim_float(XPlaneSimDataRepository.XFF_STATUS) > 0.0f);
    }

    public boolean ff_a320_fcc_off(FCC flightComputer) {
        int bit = (int) Math.pow(2, flightComputer.ordinal());
        return ((int) sim_data.get_sim_float(XPlaneSimDataRepository.XFF_FCC) & bit) > 0;
    }

    public boolean ff_a320_fcc_fault(FCC flightComputer) {
        int bit = (int) Math.pow(2, flightComputer.ordinal() + 7);
        return ((int) sim_data.get_sim_float(XPlaneSimDataRepository.XFF_FCC) & bit) > 0;
    }
    
    
    // JAR Design A320 neo
    public boolean is_jar_a320neo() {
    	return ( sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_STATUS) > 0.0f );
    }
    // BARO  
    public boolean jar_a320neo_baro_unit() {
    	return ( sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU_BARO) > 0.0f );
    }
    // Auto-pilot    
    public boolean jar_a320neo_ap1() {   	
    	int jar_a320neo_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_ap_fd_data & 0x100) > 0 ? true : false; 	

    }
    public boolean jar_a320neo_ap2() {
    	int jar_a320neo_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_ap_fd_data & 0x80) > 0 ? true : false; 	

    }
    public boolean jar_a320neo_fd() {
    	int jar_a320neo_ap_fd_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_ap_fd_data & 0x04) > 0 ? true : false; 	
    }
    public int jar_a320neo_ap_phase() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AP_PHASE));
    }
    public int jar_a320neo_ap_common_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AP_COMMON_MODE));
    }
    public int jar_a320neo_ap_vertical_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AP_VERTICAL_MODE));
    }   
    public int jar_a320neo_ap_vertical_armed() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AP_VERTICAL_ARMED));
    }
    public int jar_a320neo_ap_lateral_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AP_LATERAL_MODE));
    }
    public int jar_a320neo_ap_lateral_armed() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AP_LATERAL_ARMED));
    }
    // Approach
    public boolean jar_a320neo_appr_illuminated() {
    	int jar_a320neo_ap_appr_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_APPR));
    	return  (jar_a320neo_ap_appr_data  & 0x01) > 0 ? true : false;
    }
    public boolean jar_a320neo_loc_illuminated() {
    	int jar_a320neo_ap_appr_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_APPR));
    	return  (jar_a320neo_ap_appr_data  & 0x02) > 0 ? true : false;
    }
    public float jar_a320neo_appr_mda() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_APPR_MDA);
    }   
    public float jar_a320neo_appr_dh() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_APPR_MDA);
    }   
    // FCU
    public boolean jar_a320neo_fcu_hdg_trk() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x01) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_metric_alt() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x02) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_vs_dashed() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x04) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_spd_dashed() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x08) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_spd_managed() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x10) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_hdg_dashed() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x20) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_hdg_managed() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x40) > 0 ? true : false;
    }    
    public boolean jar_a320neo_fcu_alt_managed() {
    	int jar_a320neo_fcu_data = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FCU));
    	return (jar_a320neo_fcu_data & 0x80) > 0 ? true : false;
    }   
    // Auto-Thrust
    public int jar_a320neo_athr_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_ATHR_MODE));
    }
    public int jar_a320neo_thr_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_THR_MODE));
    }
    public int jar_a320neo_athr_limited() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_ATHR_LIMITED));
    }
    public int jar_a320neo_thr_lever_mode() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_THR_LEVER_MODE));
    }
    public int jar_a320neo_fma_thr_warning() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FMA_THR_WARNING));
    }
    public int jar_a320neo_flex_temp() {
    	return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_FLEX_TEMP));
    }
    // V Speeds   
    public int jar_a320neo_v1() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_V1));
    }
    public int jar_a320neo_vr() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_VR));
    }    
    public int jar_a320neo_vmo() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_VMO));
    }    
    public int jar_a320neo_vls() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_VLS));
    }        
    public int jar_a320neo_vs() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_VS));
    }    
    public int jar_a320neo_vf() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_VF));
    }    
    public int jar_a320neo_v_green_dot() {
        return Math.round( sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_V_GREEN_DOT));
    }    
    public int jar_a320neo_alpha_prot() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_ALPHA_PROT));
    }    
    public int jar_a320neo_alpha_max() {
        return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_ALPHA_MAX));
    } 
    
    /*
     *  X-RAAS
     *  Runway Awareness and Advisory System
     */
    public EPGWSAlertLevel egpws_alert_level() {
    	int xraas_msg_code = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.X_RAAS_ND_ALERT));
    	int color_code = (xraas_msg_code >> 6) & 0x3;
    	return color_code == 0 ? EPGWSAlertLevel.NORMAL : EPGWSAlertLevel.CAUTION;
    }

    public String egpws_alert_message() {
    	String message = "";

    	int xraas_msg_code = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.X_RAAS_ND_ALERT));
    	int msg_type = xraas_msg_code & 0x3f;

    	int color_code = (xraas_msg_code >> 6) & 0x3;

    	switch (msg_type) {
    		case Avionics.EGPWS_ALERT_FLAPS:
    			message = "FLAPS";
    			break;
    		case Avionics.EGPWS_ALERT_TOO_HIGH:
    			message = "TOO HIGH";
    			break;
    		case Avionics.EGPWS_ALERT_TOO_FAST:
    			message = "TOO FAST";
    			break;
    		case Avionics.EGPWS_ALERT_UNSTABLE:
    			message = "UNSTABLE";
    			break;
    		case Avionics.EGPWS_ALERT_TWY:
    			message = "TAXIWAY";
    			break;
    		case Avionics.EGPWS_ALERT_SHORT_RWY:
    			message = "SHORT RUNWAY";
    			break;
    		case Avionics.EGPWS_ALERT_ALTM_SETTING:
    			message = "ALTM SETTING";
    			break;
    		case Avionics.EGPWS_ALERT_APP:
    			message = "APP";
    			// don't break !
    		case Avionics.EGPWS_ALERT_ON: 
    			if (msg_type != Avionics.EGPWS_ALERT_APP) message = "ON";
    			int rwy_ID = (xraas_msg_code >> 8) & 0x3f;
    			int rwy_suffix = (xraas_msg_code >> 14) & 0x3;
    			int rwy_len = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.X_RAAS_RWY_LEN_AVAIL));
    			String str_rwy_suffix;
    			switch (rwy_suffix) {
    				case 1: str_rwy_suffix= "R"; break;
    				case 2: str_rwy_suffix= "L"; break;
    				case 3: str_rwy_suffix= "C"; break;
    				default: str_rwy_suffix= "";
    			}

    			if (rwy_ID == 0) {
    				message += " TAXIWAY";
    			} else if (rwy_ID == 37) {
    				message += " RWYS";
    			} else {
    				if (rwy_len == 0)
    					message += " "+runway_formatter.format(rwy_ID)+str_rwy_suffix;
    					// snprintf(decoded_msg, MSGLEN, "%s %02d%s", msg,
    					//     rwy_ID, decode_rwy_suffix(rwy_suffix));
    				else
    					message += " "+runway_formatter.format(rwy_ID)+str_rwy_suffix + " "+rwy_len;
    					// snprintf(decoded_msg, MSGLEN, "%s %02d%s %02d",
    					//    msg, rwy_ID, decode_rwy_suffix(rwy_suffix),
    					//    rwy_len);
    			}
    			break;
    		
    		case Avionics.EGPWS_ALERT_LONG_LAND:
    			message = "LONG LANDING";
    			break;
    		case Avionics.EGPWS_ALERT_DEEP_LAND:
    			message = "DEEP LANDING";
    			break;
    		default:
    			message="";
    	}

    	return message;
    	
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


    public void chr_control(int chr_action) {

        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_CHR_CONTROL, (float) chr_action );

    }

    public void set_clock_show_date(boolean show_date) {
    	// TODO : xhsi_preferences.get_clock_date_mode()
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_TIME_SHOW_DATE, show_date ? 1.0f : 0.0f );
    }

    public void set_clock_utc_source(int utc_source) {

    	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_TIME_UTC_SOURCE, (float) utc_source );
    }   

    public void set_clock_et_selector(int et_selector) {

    	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_TIME_ET_RUNNING, (float) et_selector  );
    }

    public void set_show_cstr(boolean new_cstr) {
    	// TODO: Implement show constraints EFIS button
    }

    
    public void set_show_arpt(boolean new_arpt) {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS, new_arpt ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_arpt) {
            	// deselect WPT, VOR, NDB, CSTR
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_CSTR, 0.0f );
            }
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT, new_arpt ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_arpt) {
            	// deselect WPT, VOR, NDB, CSTR
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_CSTR, 0.0f );
            }
        }
    }


    public void set_show_wpt(boolean new_wpt) {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS, new_wpt ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_wpt) {
            	// deselect ARPT, VOR, NDB, DATA
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_CSTR, 0.0f );
            }
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT, new_wpt ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_wpt) {
            	// deselect ARPT, VOR, NDB, DATA
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_CSTR, 0.0f );
            }
        }
    }


    public void set_show_vor(boolean new_vor) {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS, new_vor ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_vor) {
            	// deselect ARPT, WPT, NDB, DATA
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_CSTR, 0.0f );
            }
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR, new_vor ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_vor) {
            	// deselect ARPT, VOR, NDB, DATA
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_CSTR, 0.0f );
            }
        }
    }


    public void set_show_ndb(boolean new_ndb) {

        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS, new_ndb ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_ndb) {
            	// deselect ARPT, WPT, VOR, DATA
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_CSTR, 0.0f );
            }
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_NDB, new_ndb ? 1.0f : 0.0f );
            if ( !xhsi_preferences.get_symbols_multiselection() && new_ndb) {
            	// deselect ARPT, VOR, VOR, DATA
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ARPT, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WPT, 0.0f );
            	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VOR, 0.0f );
            	// udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_CSTR, 0.0f );
            }
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

    public void set_show_terrain(boolean new_data)  {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_TERRAIN, new_data ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TERRAIN, new_data ? 1.0f : 0.0f );
        }
    }
    
    public void set_show_terrain(boolean new_data, InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_TERRAIN, new_data ? 1.0f : 0.0f );
    	} else if ( side == InstrumentSide.COPILOT ) {
    		udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TERRAIN, new_data ? 1.0f : 0.0f );
    	} else {
    		xhsi_settings.show_terrain=new_data;
        }
    }

    public void set_show_vp(boolean new_data)  {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_VP, new_data ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_VP, new_data ? 1.0f : 0.0f );
        }
    }

    public void set_metric_alt(boolean new_metric_alt)  {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_METRIC_ALT, new_metric_alt ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_METRIC_ALT, new_metric_alt ? 1.0f : 0.0f );
        }
    }
    
    public void set_ils(boolean new_ils)  {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_ILS, new_ils ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ILS, new_ils ? 1.0f : 0.0f );
        }
    }
    
    public void set_ils(boolean new_ils, InstrumentSide side) {
    	if ( side == InstrumentSide.PILOT ) {
    		udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_ILS, new_ils ? 1.0f : 0.0f );
    	} else if ( side == InstrumentSide.COPILOT ) {
    		udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_ILS, new_ils ? 1.0f : 0.0f );
    	} else {
    		xhsi_settings.show_ils=new_ils;
        }
    }
    
    public void set_track_fpa(boolean new_track_fpa)  {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_TRK_FPA, new_track_fpa ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_TRK_FPA, new_track_fpa ? 1.0f : 0.0f );
        }
    }
    
    /*
     * Weather radar
     * Options bit mask 
     * pilot:   XHSI_EFIS_PILOT_WXR_OPT
     * copilot: XHSI_EFIS_COPILOT_WXR_OPT
     * ----------------
     * 
     * dataref                     bit   mask   complement
     * 
     * xhsi/nd_pilot/wxr_target     7    0x0080 0xFF7F
     * xhsi/nd_pilot/wxr_alert      6    0x0040 0xFFBF
     * xhsi/nd_pilot/wxr_narrow     5    0x0020 0xFFDF
     * xhsi/nd_pilot/wxr_react      4    0x0010 0xFFEF
     * xhsi/nd_pilot/wxr_slave      3    0x0008 0xFFF7
     * xhsi/nd_pilot/wxr_auto_tilt  2    0x0004 0xFFFB
     * xhsi/nd_pilot/wxr_auto_gain  1    0x0002 0xFFFD
     * xhsi/nd_pilot/wxr_test       0    0x0001 0xFFFE
     */
    
    public void set_show_wxr(boolean new_data) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER, new_data ? 1.0f : 0.0f );
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR, new_data ? 1.0f : 0.0f );
        }
    }
    
    public void set_wxr_gain(float new_data) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_GAIN, (float) new_data);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_GAIN, (float) new_data);
        }
    }
    
    public void set_wxr_tilt(float new_data) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_TILT, (float) new_data);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_TILT, (float) new_data);
        }
    }

    public void set_wxr_auto_tilt(boolean new_auto_tilt) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 2, new_auto_tilt);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 2, new_auto_tilt);
        }    	
    }
    
    public void set_wxr_auto_gain(boolean new_auto_gain) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 1, new_auto_gain);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 1, new_auto_gain);
        }       	
    }

    public void set_wxr_test(boolean new_test) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 0, new_test);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 0, new_test);
        }     	
    }

    
    public void set_wxr_mode(int new_data) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_MODE, (float) new_data);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_MODE, (float) new_data);
        }
    }
    

    public void set_wxr_slave(boolean new_slave) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 3, new_slave);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 3, new_slave);
        }        	
    }
    
    public void set_wxr_react(boolean new_react) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 4, new_react);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 4, new_react);
        }       	
    }
    
    public void set_wxr_narrow(boolean new_narrow) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 5, new_narrow);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 5, new_narrow);
        }     	
    }
    
    public void set_wxr_alert(boolean new_alert) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 6, new_alert);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 6, new_alert);
        }     	
    }
    
    public void set_wxr_target(boolean new_target) {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_PILOT_WXR_OPT, 7, new_target);
        } else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
        	sendBitPoint( XPlaneSimDataRepository.XHSI_EFIS_COPILOT_WXR_OPT, 7, new_target);
        }      	
    }
    
    /*
     *  Autopilot 
     */
    
    public void set_autopilot_altitude(float new_altitude){
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_ALTITUDE,new_altitude);
    }
    
    public void set_autopilot_speed(float new_speed){
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AIRSPEED,new_speed);    	
    }
    
    public void set_autopilot_vv(float new_vertical_speed){
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY,new_vertical_speed); 
    }
    
    public void set_autopilot_hdg(float new_heading){
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_HEADING_MAG,new_heading); 
    }
    
    public void set_autopilot_mode(int new_mode){
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_MODE,new_mode); 
    }
    

    public void set_mfd_mode(int new_mode) {
    	// Don't switch QPAC ECAM SD page if custom XHSI MFD page is displayed : Flight Plan , or Airport Chart
    	// TODO : introduce a preference setting to disable MFD menu link to ECAM SD page
        if ( ! xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ) {
            udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_MFD_MODE, (float) new_mode );
            if (this.is_qpac()) {
            	int sd_page = -1;
            	switch (new_mode) {
            		case Avionics.MFD_MODE_ARPT:
            		case Avionics.MFD_MODE_FPLN:
            		case Avionics.MFD_MODE_RTU: sd_page = -1; break;
            		case Avionics.MFD_MODE_ENGINE: sd_page = 0; break; 
            		case Avionics.MFD_MODE_BLEED: sd_page = 1; break;
            		case Avionics.MFD_MODE_CAB_PRESS: sd_page = 2; break;
            		case Avionics.MFD_MODE_ELEC: sd_page = 3; break;
            		case Avionics.MFD_MODE_HYDR: sd_page = 4; break;
            		case Avionics.MFD_MODE_FUEL: sd_page = 5; break;
            		case Avionics.MFD_MODE_APU: sd_page = 6; break;
            		case Avionics.MFD_MODE_COND: sd_page = 7; break;
            		case Avionics.MFD_MODE_DOOR_OXY: sd_page = 8; break;
            		case Avionics.MFD_MODE_WHEELS: sd_page = 9; break;
            		case Avionics.MFD_MODE_FCTL: sd_page = 10; break;
            		case Avionics.MFD_MODE_SYS: sd_page = 11; break; 
            		case Avionics.MFD_MODE_STATUS: sd_page = 12; break;
            	}
            	if (sd_page != -1) {
            		udp_sender.sendDataPoint( XPlaneSimDataRepository.QPAC_SD_PAGE, (float) sd_page );
            	}
            }
            if (this.is_ff_a320()) {
                int mfd_page = -1;
                switch (new_mode) {
                    case Avionics.MFD_MODE_ARPT:
                    case Avionics.MFD_MODE_FPLN:
                    case Avionics.MFD_MODE_RTU:
                        mfd_page = -1;
                        break;
                    case Avionics.MFD_MODE_ENGINE:
                        mfd_page = 0x04;
                        break;
                    case Avionics.MFD_MODE_BLEED:
                        mfd_page = 0x08;
                        break;
                    case Avionics.MFD_MODE_CAB_PRESS:
                        mfd_page = 0x10;
                        break;
                    case Avionics.MFD_MODE_ELEC:
                        mfd_page = 0x20;
                        break;
                    case Avionics.MFD_MODE_HYDR:
                        mfd_page = 0x40;
                        break;
                    case Avionics.MFD_MODE_FUEL:
                        mfd_page = 0x80;
                        break;
                    case Avionics.MFD_MODE_APU:
                        mfd_page = 0x100;
                        break;
                    case Avionics.MFD_MODE_COND:
                        mfd_page = 0x200;
                        break;
                    case Avionics.MFD_MODE_DOOR_OXY:
                        mfd_page = 0x400;
                        break;
                    case Avionics.MFD_MODE_WHEELS:
                        mfd_page = 0x800;
                        break;
                    case Avionics.MFD_MODE_FCTL:
                        mfd_page = 0x1000;
                        break;
                    case Avionics.MFD_MODE_SYS:
                        mfd_page = 0x2000;
                        break;
                    case Avionics.MFD_MODE_STATUS:
                        mfd_page = 0x8000;
                        break;
                }
                if (mfd_page != -1) {
                    udp_sender.sendDataPoint(XPlaneSimDataRepository.XFF_MFD_BUTTONS, (float) mfd_page);
                }
            }
            if (this.is_jar_a320neo()) {
            	int sd_page = -1;
            	switch (new_mode) {
            		case Avionics.MFD_MODE_ARPT:
            		case Avionics.MFD_MODE_FPLN:
            		case Avionics.MFD_MODE_RTU: 
            		case Avionics.MFD_MODE_STATUS: sd_page = -1; break;
            		case Avionics.MFD_MODE_ENGINE: sd_page = 11; break; 
            		case Avionics.MFD_MODE_BLEED: sd_page = 7; break;
            		case Avionics.MFD_MODE_CAB_PRESS: sd_page = 3; break;
            		case Avionics.MFD_MODE_ELEC: sd_page = 6; break;
            		case Avionics.MFD_MODE_HYDR: sd_page = 0; break;
            		case Avionics.MFD_MODE_FUEL: sd_page = 1; break;
            		case Avionics.MFD_MODE_APU: sd_page = 2; break;
            		case Avionics.MFD_MODE_COND: sd_page = 8; break;
            		case Avionics.MFD_MODE_DOOR_OXY: sd_page = 9; break;
            		case Avionics.MFD_MODE_WHEELS: sd_page = 5; break;
            		case Avionics.MFD_MODE_FCTL: sd_page = 4; break;
            		case Avionics.MFD_MODE_SYS: sd_page = 10; break; 
            	}
            	if (sd_page != -1) {
                    // Send to : sim/custom/xap/disp/sys/mode
            		udp_sender.sendDataPoint( XPlaneSimDataRepository.JAR_A320NEO_SD_PAGE, (float) sd_page );
            	}
            }
        }
    }


    public void set_cdu_source(int new_source) {
    	int cdu_source = (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CDU_SOURCE);

    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		// PILOT CDU
    		cdu_source = (cdu_source & 0xF0) | (new_source & 0x0F);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		// COPILOT CDU
    		cdu_source = (cdu_source & 0x0F) | ((new_source << 4) & 0xF0);
    	} 
    	// else INSTRUCTOR CDU
    	// ignore

        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_CDU_SOURCE, (float) cdu_source );
    }
    
    public void set_cdu_side(int new_side) {
    	int cdu_side = (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CDU_SIDE);
    	if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.PILOT ) ) {
    		// PILOT CDU
    		cdu_side = (cdu_side & 0xF0) | (new_side & 0x0F);
    	} else if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		// COPILOT CDU
    		cdu_side = (cdu_side & 0x0F) | ((new_side << 4) & 0xF0);
    	} 
    	// else INSTRUCTOR CDU
    	// ignore
    	    	
    	udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_CDU_SIDE, (float) cdu_side );
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
 
    public void set_temp_units(int new_units) {

        udp_sender.sendDataPoint( XPlaneSimDataRepository.XHSI_TEMP_UNITS, (float) new_units );

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
    		} else {
    			return failure_mode(FailedElement.PFD_ATTITUDE) == FailureMode.INOPERATIVE ? false : true;
    		}
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0040) > 0 ? true : false;
    		} else {
    			return failure_mode(FailedElement.PFD_ATTITUDE) == FailureMode.INOPERATIVE ? false : true;
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
    		} else {
    			return failure_mode(FailedElement.PFD_HEADING) == FailureMode.INOPERATIVE ? false : true;
    		}
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0080) > 0 ? true : false;
    		} else {
    			return failure_mode(FailedElement.PFD_HEADING) == FailureMode.INOPERATIVE ? false : true;
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
    		} else {
    			return failure_mode(FailedElement.PFD_AIR_SPEED) == FailureMode.INOPERATIVE ? false : true;
    		}
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0020) > 0 ? true : false;
    		} else {
    			return failure_mode(FailedElement.PFD_AIR_SPEED) == FailureMode.INOPERATIVE ? false : true;
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
    		} else {
    			return failure_mode(FailedElement.PFD_ALTITUDE) == FailureMode.INOPERATIVE ? false : true;
    		}
    	} else if (xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
    		if (is_qpac() ) {    			
    			return (qpac_failures_data & 0x0010) > 0 ? true : false;
    		} else {
    			return failure_mode(FailedElement.PFD_ALTITUDE) == FailureMode.INOPERATIVE ? false : true;
    		}
    	} 
    	// Instructor : always true
    	return true;
    };
  

}
