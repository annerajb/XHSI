/**
* XPlaneAircraft.java
* 
* The X-Plane specific implementation of Aircraft.
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
package net.sourceforge.xhsi.model.xplane;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.AircraftEnvironment;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObject;
import net.sourceforge.xhsi.model.SimDataRepository;
import net.sourceforge.xhsi.model.Aircraft.CabinZone;
import net.sourceforge.xhsi.model.Aircraft.ElecBus;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;


public class XPlaneAircraft implements Aircraft {

    private SimDataRepository sim_data;
    private Avionics avionics;
    private AircraftEnvironment environment;
    private XHSIPreferences xhsi_preferences;
    private XHSISettings xhsi_settings;

    private float fuel_capacity;
    private float max_fuel_flow;
    private String nearest_arpt = "";

    private static String x737_thrust_modes[] = { "---", "TO", "R-TO", "R-CLB", "CLB", "CRZ", "G/A", "CON", "MAX" };
    private static String cl30_thrust_modes[] = { "---", "CRZ", "CLB", "TO", "APR" };


    public XPlaneAircraft(ModelFactory sim_model) {
        this.sim_data = sim_model.get_repository_instance();
        this.environment = new XPlaneAircraftEnvironment(sim_model);
        this.avionics = new XPlaneAvionics(this, sim_model);
        this.xhsi_preferences = XHSIPreferences.get_instance();
        this.xhsi_settings = XHSISettings.get_instance();
    }

    public Avionics get_avionics() {
        return this.avionics;
    }

    public AircraftEnvironment get_environment() {
        return this.environment;
    }

    public int plugin_version() { return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.PLUGIN_VERSION_ID)); }
    
    public String aircraft_registration() {
        return sim_data.get_sim_string(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_TAILNUM_0_3) + sim_data.get_sim_string(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_TAILNUM_4_7);
    }
    
    public boolean battery() {
        if ( XHSIPreferences.get_instance().get_instrument_operator().equals( XHSIPreferences.INSTRUCTOR ) ||
                ! XHSIPreferences.get_instance().get_use_power() ) {
            return true;
        } else {
            if ( this.avionics.is_x737() )
               return ( sim_data.get_sim_float(XPlaneSimDataRepository.X737_PFD_PWR) != 0.0f );
            else
               return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_BATTERY_ON) != 0.0f );
        }
    }

    public boolean cockpit_lights() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON) != 0.0f );
    }

    public float lat() {return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE); } // degrees
    public float lon() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE); } // degrees
    public float msl_m() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_ELEVATION)); } // meters
    public float agl_m() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_Y_AGL)); } // meters
    public float ground_speed() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_GROUNDSPEED) * 1.9438445f); } // m/s to knots
    public float true_air_speed() { return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED) * 1.94385f); } // m/s to knots
    public float heading() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGPSI); } // degrees magnetic
    public float hpath() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH); }
//    public float indicated_altitude() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_MISC_H_IND); }
//    public float indicated_vv() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_VH_IND_FPM); }
    public float pitch() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_THETA); }
    public float bank() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_PHI); }
    public float g_load() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_FORCES_G_LOAD); }
    public float yoke_pitch() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_YOKE_PITCH_RATIO); }
    public float yoke_roll() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_YOKE_ROLL_RATIO); }
    public float rudder_hdg() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_YOKE_HDG_RATIO); }
    public float brk_pedal_left() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_LEFT_BRK_RATIO); }
    public float brk_pedal_right() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_RIGHT_BRK_RATIO); }


    public float track() {
        // degrees magnetic
        if (ground_speed() < 5) {
            return heading();
        } else {
            float path = (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH) +
                           sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGVAR));
            if (path < 0)
                path += 360;
            else if (path > 360)
                path -= 360;

            return path;
        }
    }

    public float drift() {
        return heading() - track();
    }

    public float sideslip() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_SIDESLIP_DEGREES);
    }

    public float aoa() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_ALPHA);
    }

    public float airspeed_ind() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_PILOT);
        }
    }

    public float altitude_ind() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_PILOT);
        }
    }

    public float vvi() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_PILOT);
        }
    }

    public int ra_bug() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_COPILOT));
        } else {
            // pilot or instructor
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_PILOT));
        }
    }

    public int da_bug() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_DA_BUG));
        } else {
            // pilot or instructor
            return Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_DA_BUG));
        }
    }

    public boolean mins_is_baro() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_COPILOT_MINS_MODE) == 1.0);
        } else {
            // pilot or instructor
            return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EFIS_PILOT_MINS_MODE) == 1.0);
        }
    }

    public int qnh() {
        return Math.round( altimeter_in_hg() * 1013.0f / 29.92f );
    }
    
    public int qnh(boolean pilot) {
        return Math.round( altimeter_in_hg(pilot) * 1013.0f / 29.92f );
    }
    
    public float altimeter_in_hg() {
        if ( xhsi_preferences.get_instrument_operator().equals( XHSIPreferences.COPILOT ) ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT);
        }
    }
    
    public float altimeter_in_hg(boolean pilot) {
        if ( ! pilot ) {
            // copilot
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT);
        } else {
            // pilot or instructor
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT);
        }
    }
    
    public float airspeed_acceleration() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_ACCELERATION);
    }

    public float turn_rate() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_MISC_TURNRATE_NOROLL); }

    public float turn_speed() { return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_R); } // degrees per second

    public float distance_to(NavigationObject nav_object) {
        return CoordinateSystem.distance(lat(), lon(), nav_object.lat, nav_object.lon);
    }

    public float rough_distance_to(NavigationObject nav_object) {
        return CoordinateSystem.rough_distance(lat(), lon(), nav_object.lat, nav_object.lon);
    }

    public long ete_to(NavigationObject nav_object) {
        return ete_for_distance(distance_to(nav_object));
    }

    public long time_when_arriving_at(NavigationObject nav_object) {
        return time_after_distance(distance_to(nav_object));
    }

    public long time_after_distance(float distance_nm) {
        return (long) (sim_time_zulu() + (distance_nm / ground_speed()) * 3600.0f);
    }

    public long time_after_ete(float eet_min) {
        return (long) (sim_time_zulu() + eet_min * 60.0f);
    }

    public long ete_for_distance(float distance_nm) {
        return (long) (distance_nm / ground_speed() * 3600.0f);
    }

    
    public float sim_time_zulu() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_ZULU_TIME_SEC);
    }

    public float sim_time_local() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_LOCAL_TIME_SEC);
    }

//    public boolean timer_is_running() {
//        return (sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_TIMER_IS_RUNNING_SEC) != 0.0f );
//    }
    
    public float timer_elapsed_time() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_TIMER_ELAPSED_TIME_SEC);
    }

    public float total_flight_time() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_TIME_TOTAL_FLIGHT_TIME_SEC);
    }

    
    public float magnetic_variation() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGVAR);
    }

    public float oat() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_TEMPERATURE_AMBIENT_C);
    }

    public float tat() {
        return ( (oat() + 273.15f ) * ( 1 + ( (1.4f-1.0f)/2.0f*1.0f*mach()*mach()) ) ) - 273.15f;
    }
    
    public float isa() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_TEMPERATURE_SEALEVEL_C);
    } 

    public float mach() {
        return true_air_speed() / sound_speed();
    }

    public float sound_speed() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_WEATHER_SPEED_SOUND_MS) * 1.944f;
    }

    public int num_gears() { return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_AIRCRAFT_GEAR_COUNT); }

    public boolean has_retractable_gear() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_GEAR_ACF_GEAR_RETRACT) == 1.0f );
    }
    
    public float get_gear(int gear) {
        if ( ( gear >= 0 ) && ( gear < num_gears() ) ) {
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL2_GEAR_DEPLOY_RATIO_ + gear);
        } else {
            return -999.999f;
        }
    }

    public int num_gear_doors() { return (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_AIRCRAFT_GEAR_DOOR_COUNT); }
    
    public float get_gear_door(int gear) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_AIRCRAFT_GEAR_DOOR_DEPLOY_RATIO_ + gear);
    }
    
    public boolean gear_is_down() {
        boolean all_down = true;
        int n = num_gears();
        if (n > 0) {
            for (int i=0; i<n; i++) {
                if (get_gear(i) < 1.0f) {
                    all_down = false;
                }
            }
        }
        return all_down;
    }

    public boolean gear_is_up() {
        boolean all_up = true;
        int n = num_gears();
        if (n > 0) {
            for (int i=0; i<n; i++) {
                if (get_gear(i) > 0.0f) {
                    all_up = false;
                }
            }
        }
        return all_up;
    }


    public float brake_temp(int brake) {
    	/* by default, return OAT */
    	if (this.avionics.is_jar_a320neo()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BRAKE_TEMP_ + brake);    		
    	} else 
    		return oat();
    }

    public boolean brake_hot() {
    	if (this.avionics.is_jar_a320neo()) {
    		int brake_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BRAKE_STATUS);
    		return (brake_status & 0x01) != 0;
    	} else 
    	/* Hot brake is true if one brake >= 300°c */
    		return false;
    }

    public boolean brake_fan() {
    	// by default, aircraft has no brake fan
    	// TODO: Airbus brake fan status
    	if (this.avionics.is_jar_a320neo()) {
    		int brake_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BRAKE_STATUS);
    		return (brake_status & 0x02) != 0;
    	} else 
    		return false;
    }    

    public boolean brake_release_left() {
    	int wheel_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.WHEEL_STATUS);
    	return (wheel_status & 0x20) != 0;
    }

    public boolean brake_release_right() {
    	int wheel_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.WHEEL_STATUS);
    	return (wheel_status & 0x40) != 0;
    }

    public float brake_pressure_left() {
    	// by default, equals left brake pedal deflection
    	// should be 0 is left brake failure
    	if (this.avionics.is_jar_a320neo()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BRAKE_LEFT_PSI);    		
    	} else if (this.avionics.is_qpac()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_TPI_LEFT);  
    	} else 
    		return brk_pedal_left();
    }   

    public float brake_pressure_right() {
    	// by default, equals left brake pedal deflection
    	// should be 0 is right brake failure
    	if (this.avionics.is_jar_a320neo()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BRAKE_RIGHT_PSI);    		
    	} else if (this.avionics.is_qpac()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_TPI_RIGHT);  
    	} else 
    		return brk_pedal_right();
    }

    public float brake_pressure_accu() {
    	// by default, full
    	// should be 0 is both brakes failed
    	if (this.avionics.is_jar_a320neo()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BRAKE_ACCU_PSI);    		
    	} else if (this.avionics.is_qpac()) {
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_TPI_ACCU);    		
    	} else 
    		return 1.0f;
    }
    

    public float tire_psi(int tire) {
    	// TODO: Give a formula depending on GW, OAT and tire temperature
    	// Tire pressure delta = temperature delta from 13° (ISA at sea level)
    	// If tire if blown, set psi to pitot static pressure
    	int tire_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.TIRE_STATUS);
    	boolean tire_blown = ((0x01 << tire) & tire_status) != 0;
    	float isa_k = 273.15f + 13;
    	float oat_k = 273.15f + oat();
    	float delta_t = 1+((oat_k-isa_k) / isa_k);
    	return tire_blown ? 0.0f : tire_ref_psi(tire) * delta_t;
    }

    public float tire_ref_psi(int tire) {
    	// TODO: Preferences to set tire reference pressures
    	// Preset : reference pressure for A320
    	if (tire==0) {
    		// Nose gear
    		return 180.0f;
    	} else {
    		// Main gears
    		return 200.0f;
    	}    	
    }
    
    // TODO: remove !
    public float tire_temp(int tire) {
    	return oat();
    }
    
    // TODO: fix with jarDesign A320.
    public boolean nose_wheel_steering() {
    	int wheel_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.WHEEL_STATUS);
    	return (wheel_status & 0x01) != 0;
    }
    
    public boolean gear_indicators() {
    	int wheel_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.WHEEL_STATUS);
    	return (wheel_status & 0x08) == 0;
    }
    
    public boolean gear_actuators() {
    	int wheel_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.WHEEL_STATUS);
    	return (wheel_status & 0x10) == 0;
    }   
    
    public boolean on_ground() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_FAILURES_ONGROUND_ANY) == 1.0f );
    }

    public boolean master_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING) == 1.0f );
    }

    public boolean master_caution() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION) == 1.0f );
    }

    public float get_pitch_trim() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_ELEVATOR_TRIM);
    }

    public float get_roll_trim() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_AILERON_TRIM);
    }

    public float get_yaw_trim() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_RUDDER_TRIM);
    }
    
    public float get_left_elev_pos() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_CONTROLS_LEFT_ELEV);
    }
    
    public float get_right_elev_pos() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_CONTROLS_RIGHT_ELEV);
    }
    
    public float get_left_aileron_pos() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_CONTROLS_LEFT_AIL);
    }
    
    public float get_right_aileron_pos() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_CONTROLS_RIGHT_AIL);
    }
    
    public float get_rudder_pos() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_CONTROLS_RUDDER);
    }

    public float get_aileron_max_up() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACL_AIL_UP);
    }

    public float get_aileron_max_down() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACL_AIL_DN);
    }

    public float get_elev_max_up() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACL_ELEV_UP);
    }

    public float get_elev_max_down() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACL_ELEV_DN);
    }

    public float get_rudder_max_lr() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACL_RUDDER_LR);
    }
       
    public float get_slat_position() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_CONTROLS_SLATRAT);
    }
    
    public float get_flap_position() {
        // sim data for handle and real position are reversed
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_FLAP_HANDLE_DEPLOY_RATIO);
    }

    public float get_flap_handle() {
        // sim data for handle and real position are reversed
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_FLAP_RATIO);
    }

    public int get_flap_detents() {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACF_FLAP_DETENTS);
    }

    public float get_speed_brake() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL2_CONTROLS_SPEEDBRAKE_RATIO);
    }

    public boolean speed_brake_armed() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_SPEEDBRAKE_RATIO) == -0.5f );
    }

    public boolean has_speed_brake() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_PARTS_ACF_SBRKEQ) != 0.0f );
    }

    public int num_spoilers() {
    	return has_speed_brake() ? 1 : 0;
    }
    
    public float get_spoiler_pos(int pos) {
    	return get_speed_brake();
    }
  
    public SpoilerStatus get_spoiler_status_left(int pos) {
    	int spoiler = 0;
    	if ((this.avionics.is_qpac())) {
    		spoiler = (Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_SPOILERS_LEFT)) >> (pos*2)) & 0x03 ;
    		switch (spoiler) {
    		case 0: return SpoilerStatus.RETRACTED;
    		case 1: return SpoilerStatus.EXTENDED;
    		case 2: return SpoilerStatus.FAILED;
    		default: return SpoilerStatus.JAMMED;
    		}

    	} else {
    	return get_speed_brake() > 0.1f ? SpoilerStatus.EXTENDED : SpoilerStatus.RETRACTED ;
    	}
    }

    public SpoilerStatus get_spoiler_status_right(int pos) {
    	int spoiler = 0;
    	if ((this.avionics.is_qpac())) {
    		spoiler = (Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_SPOILERS_RIGHT)) >> (pos*2)) & 0x03 ;
    		switch (spoiler) {
    		case 0: return SpoilerStatus.RETRACTED;
    		case 1: return SpoilerStatus.EXTENDED;
    		case 2: return SpoilerStatus.FAILED;
    		default: return SpoilerStatus.JAMMED;
    		}

    	} else {
    	return get_speed_brake() > 0.1f ? SpoilerStatus.EXTENDED : SpoilerStatus.RETRACTED ;
    	}
    }
       
    public float get_parking_brake() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO);
    }
    
    public int seat_belt_sign () {
    	// bits 7 and 8 
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs >> 8) & 0x03;
    }
    
    public int no_smoking_sign () {
    	// bits 5 and 6
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs >> 6) & 0x03;    	
    }
    
    public boolean landing_lights_on () {
    	// bit 1
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs & 0x02) > 0 ? true : false;    	
    }
    
    public boolean landing_taxi_lights_on () {
    	// bit 4
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs & 0x10) > 0 ? true : false;    	
    }
    
    public boolean beacon_on () {
    	// bit 0
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs & 0x01) > 0 ? true : false;   	
    }
    
    public boolean nav_lights_on () {
    	// bit 2
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs & 0x04) > 0 ? true : false;    	
    }
    public boolean strobe_lights_on () {
    	// bit 3
    	int lights_signs = Math.round(sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_LIGHTS));
    	return (lights_signs & 0x08) > 0 ? true : false;   	
    }

    public boolean stall_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_STALL_WARNING) == 1.0f );
    }

    public boolean terrain_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_GPWS) == 1.0f );
    }

    public boolean low_fuel() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY) == 1.0f );
    }

    public boolean ap_disconnect() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_AUTOPILOT_DISCONNECT) == 1.0f );
    }

    public boolean icing() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_ICE) == 1.0f );
    }

    public boolean pitot_heat() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_PITOT_HEAT) == 1.0f );
    }

    public boolean gear_warning() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_GEAR_WARNING) == 1.0f );
    }

    public boolean gear_unsafe() {
        return ( sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_GEAR_UNSAFE) == 1.0f );
    }

    public int auto_brake() {
    	if (this.avionics.is_qpac()) {
    		return ((int)sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_AUTO_BRAKE_LEVEL));
    	} else  if (this.avionics.is_jar_a320neo()) {
    		return ((int)sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_AUTO_BRAKE_LEVEL));
    	} else 	{
    		return ((int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL)) - 1;
    	}
    }

    public float get_Vso() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VSO);
    }

    public float get_Vs() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VS);
    }

    public float get_Vfe() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VFE);
    }

    public float get_Vno() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VNO);
    }

    public float get_Vne() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_VNE);
    }

    public float get_Mmo() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_VIEW_ACF_MMO);
    }

    public float get_Vle() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_VLE);
    }

    public float get_Vmca() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_VMCA);
    }

    public float get_Vyse() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_VYSE);
    }

    public int num_engines() {
        //return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES);
        int xp_engines = (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES);
//xp_engines = 8;
        int override_count = this.xhsi_preferences.get_override_engine_count();
        if ( override_count == 0 ) {
            return xp_engines;
        } else {
            return Math.min(xp_engines, override_count);
        }
    }

    public int num_tanks() { return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS); }

    public float tank_ratio(int tank) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ + tank);
    }

    public boolean oil_press_alert(int eng) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_OIL_PRESSURE) & (1<<eng) ) != 0 );}
    public boolean oil_temp_alert(int eng) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_OIL_TEMPERATURE) & (1<<eng) ) != 0 );}
    public boolean fuel_press_alert(int eng) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_FUEL_PRESSURE) & (1<<eng) ) != 0 );}

    public float fuel_used(int eng) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_FUEL_USED_ + eng);
    }
    
    public float get_fuel(int tank) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_FUEL_QUANTITY_ + tank);
    }

    public float get_total_fuel() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL);
    }

    public void estimate_fuel_capacity() {
        // in any case, it cannot be less than the total fuel currently on board...
        this.fuel_capacity = get_total_fuel();
        // suppose at least one tank is full...
        int n = num_tanks();
        for (int i=0; i<n; i++) {
            this.fuel_capacity = Math.max( this.fuel_capacity, get_fuel(i) / tank_ratio(i) );
        }
    }

    public float get_fuel_capacity() {
//        if ( get_total_fuel() > this.fuel_capacity ) estimate_fuel_capacity();
//        return this.fuel_capacity;
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_WEIGHT_ACF_M_FUEL_TOT);
    }

    public float get_tank_capacity(int tank) {
//        if ( get_total_fuel() > this.fuel_capacity ) estimate_fuel_capacity();
//        return this.fuel_capacity * tank_ratio(tank);
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_WEIGHT_ACF_M_FUEL_TOT) * tank_ratio(tank);
    }

//    public void set_fuel_capacity(float capacity) {
//        this.fuel_capacity = capacity;
//    }

    public PumpStatus get_tank_pump(int tank) {
    	if (avionics.is_qpac()) {
    		int pump_status = ((int)sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FUEL_PUMPS) >> (tank*2)) & 0x03 ;
    		switch (pump_status) {
    			case 0: return PumpStatus.OFF;
    			case 1: return PumpStatus.ON;
    			case 3: return PumpStatus.LOW_PRESSURE;
    			default :return PumpStatus.FAILED;
    		}    		
    	} else {
    		return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_FUEL_PUMPS) & (1<<tank) ) != 0 ? PumpStatus.ON : PumpStatus.OFF );
    	}
    }

    public ValveStatus get_tank_xfer_valve() {
    	if (avionics.is_qpac()) {
    		int pump_status = ((int)sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FUEL_VALVES) >> 8) & 0x07 ;
    		switch (pump_status) {
    			case 0: return ValveStatus.TRANSIT;
    			case 1: return ValveStatus.VALVE_CLOSED;
    			case 2: return ValveStatus.VALVE_OPEN;
    			case 3: return ValveStatus.VALVE_CLOSED_FAILED;
    			case 4: return ValveStatus.VALVE_OPEN_FAILED;
    			default :return ValveStatus.JAMMED;
    		}    		
    	} else {
    		return ValveStatus.VALVE_CLOSED;
    	}
    }

    public ValveStatus get_eng_fuel_valve(int eng){
    	if (avionics.is_qpac()) {
    		int pump_status = ((int)sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_FUEL_VALVES) >> (eng*2)) & 0x03 ;
    		switch (pump_status) {
				case 0: return ValveStatus.TRANSIT;
    			case 1: return ValveStatus.VALVE_CLOSED_FAILED;
    			case 3: return ValveStatus.VALVE_OPEN;
    			case 2: return ValveStatus.VALVE_CLOSED;
    			case 4: return ValveStatus.VALVE_OPEN_FAILED;
    			default :return ValveStatus.JAMMED;
    		}    		
    	} else {
    		// TODO : Get the fire cut off status
    		return ValveStatus.VALVE_OPEN;
    	}    	
    }

    public float gross_weight() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_WEIGHT_M_TOTAL);
    }

    public float fuel_multiplier() {
        
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) return 2.20462262185f;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) return 2.20462262185f/6.02f;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) return 2.20462262185f/6.02f*3.785411784f;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) */ return 1.0f;
        
    }

    public float get_N1(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ + engine);
    }

    public float get_ref_N1(int engine) {
        float ref;
        if ( this.avionics.is_x737() ) {
            if ( engine == 0 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.X737_N1_LIMIT_ENG1);
            } else if ( engine == 1 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.X737_N1_LIMIT_ENG2);
            } else {
                ref = 0.0f;
            }
        } else if ( this.avionics.is_cl30() ) {
            int cl_caret = (int) sim_data.get_sim_float(XPlaneSimDataRepository.CL30_CARETS);
            if ( cl_caret == 2 ) {
                // should be : ref = sim_data.get_sim_float(XPlaneSimDataRepository.CL30_CLB_N1);
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.CL30_TO_N1);
            } else if ( cl_caret == 3 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.CL30_TO_N1);
            } else {
                ref = 0.0f;
            }
        } else if ( this.avionics.is_qpac() ) {                
            ref = sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_THR_RATING_N1);            
            }
        else if ( this.avionics.has_ufmc() ) {
            if ( engine == 0 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_1);
            } else if ( engine == 1 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_2);
            } else if ( engine == 2 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_3);
            } else if ( engine == 3 ) {
                ref = sim_data.get_sim_float(XPlaneSimDataRepository.UFMC_N1_4);
            } else {
                ref = 0.0f;
            }
        } else {
            ref = 0.0f;
        }
        return ref;
    }

    public String get_thrust_mode() {    	
        if ( this.avionics.is_x737() ) {
            return XPlaneAircraft.x737_thrust_modes[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.X737_N1_PHASE) ];
        } else if ( ( this.avionics.is_cl30() ) && ( this.reverser_position(0) == 0.0f ) ) {
            return XPlaneAircraft.cl30_thrust_modes[ (int) sim_data.get_sim_float(XPlaneSimDataRepository.CL30_CARETS) ];
        } else if ( this.avionics.is_qpac() ) {
        	int qpac_thr_rtype = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_THR_RATING_TYPE);
        	switch (qpac_thr_rtype) {
        	case 0 : return "";
        	case 1 : return "CL";
        	case 2 : return "MCT";
        	case 3 : return "TOGA";
        	case 4 : return "FLX";
        	default : return "-";
        	}    	
        } else {
            return "";
        }

    }

    public float get_EGT_percent(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ + engine) * 100.0f;
    }

    public float get_EGT_value(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ + engine);
    }
    
    public float get_EGT_max() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_MAX_EGT);
    }

    public float get_N2(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ + engine);
    }

    public float get_FF(int engine) {
        float ff = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ + engine);
        max_fuel_flow = Math.max(ff, max_fuel_flow);
        return ff;
    }

    public float get_max_FF() {
        return max_fuel_flow;
    }

    public void reset_max_FF() {
        max_fuel_flow = 0.0f;
    }

    public float get_oil_press_ratio(int engine) {
        float o_p = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ + engine);
        if ( o_p > 1.0f )
            return 1.0f;
        else
            return o_p;
    }

    public float get_oil_press_psi(int engine) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_PSI_ + engine);
    }
    
    public float get_oil_press_max() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_RED_OIL_P);
    }
    
    public float get_oil_temp_ratio(int engine) {
        float o_t = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ + engine);
        if ( o_t > 1.0f )
            return 1.0f;
        else
            return o_t;
    }

    public float get_oil_temp_max() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_RED_OIL_T);
    }
    
    public float get_oil_temp_c(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_C_ + engine);
    }
    
    public float get_oil_quant_ratio(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ + engine);
    }

    public float get_vib(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_FLIGHTMODEL_ENGINE_VIB_ + engine);
    }

    public float get_vib_n2(int engine) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_FLIGHTMODEL_ENGINE_VIB_N2_ + engine);
    }
    
    public float get_nac_temp(int engine) {
    	if ( this.avionics.is_qpac() ) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_NACELLE_TEMP_+engine);
    	else if ( this.avionics.is_jar_a320neo() ) 
    		return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_NACELLE_TEMP_+engine);
    	else
    		return oat();
    } 
    
    public float get_hyd_press(int circuit) {
        float h_p;
        // TODO: QPAC hyd pressure
        // A320-A340-A380 standard pressure is 3000 psi (3 circuits : Blue, Green, Yellow)
        // A350 two high pressure circuits at 5000 psi
        // B737 two main circuits : A and B, and one Standby
        if ( this.avionics.is_qpac() ) {
        	switch (circuit) {
        		case 0 : h_p = sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_HYD_G_PRESS);
        				 break;
        		case 1 : h_p = sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_HYD_Y_PRESS);
				 		 break;
        		case 2 : h_p = sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_HYD_B_PRESS);
				 		 break;
				default : h_p = 0.0f;
        	}
        	return h_p / 5000.0f;
        } else if (this.avionics.is_jar_a320neo()){
        	switch (circuit) {
    			case 0 : h_p = sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_HYD_G_PRESS);
    				 	break;
    			case 1 : h_p = sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_HYD_Y_PRESS);
			 		 	break;
    			case 2 : h_p = sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_HYD_B_PRESS);
			 		 	break;
    			default : h_p = 0.0f;
        	}
        	return h_p / 5000.0f;        	
        } else {
        	if ( circuit == 1 )
        		h_p = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1);
        	else
        		h_p = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2);
        	// most values seem to be in the range 3000-3600
        	if ( h_p > 5000.0f )
        		return 3333.0f / 5000.0f;
        	else
        		return h_p / 5000.0f;
        }
    }

    public float get_hyd_quant(int circuit) {
        if ( circuit == 1 )
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1);
        else
            return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2);
    }
    
    /* 
     * 0 Green circuit 
     * 1 Yellow circuit
     * 2 Blue circuit
     * 3 Yellow electric pump
     * 4 RAT pump
     */
    public HydPumpStatus get_hyd_pump(int circuit) {
    	
    	if (this.avionics.is_qpac()) {
    		int qpac_hyd_pumps = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_HYD_PUMPS);
    		int pump_status = (qpac_hyd_pumps >> (6+circuit*2)) & 0x03;
    		if (circuit==3) {
    			// Yellow ELEC pump
    			pump_status = (qpac_hyd_pumps >> 2) & 0x03;    			
    		} else if (circuit==4) {
    			// RAT pump
    			pump_status = qpac_hyd_pumps & 0x03;    	    			
    		}
    		switch (pump_status) {
				case 0 : return HydPumpStatus.OFF;
				case 1 : return HydPumpStatus.ON;
				case 2 : return HydPumpStatus.FAILED;
				default : return HydPumpStatus.OFF; 
    		}
    	} else 	if (this.avionics.is_jar_a320neo()) {
    		int hyd_pumps = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_HYD_PUMPS);
    		int pump_status = (hyd_pumps >> (6+circuit*2)) & 0x03;
    		if (circuit==3) {
    			// Yellow ELEC pump
    			pump_status = (hyd_pumps >> 2) & 0x03;    			
    		} else if (circuit==4) {
    			// RAT pump
    			pump_status = hyd_pumps & 0x03;    	    			
    		}
    		switch (pump_status) {
				case 0 : return HydPumpStatus.OFF;
				case 1 : return HydPumpStatus.ON;
				case 2 : return HydPumpStatus.FAILED;
				default : return HydPumpStatus.OFF; 
    		}
    	} else {
    		return HydPumpStatus.ON;
    	}
    }
    
    public HydPTUStatus get_hyd_ptu() {
    	if (this.avionics.is_qpac()) {
    		int qpac_hyd_pumps = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_HYD_PUMPS);
    		int ptu_status = (qpac_hyd_pumps >> 4) & 0x03; 
    		switch (ptu_status) {
    			case 0 : return HydPTUStatus.OFF;
    			case 1 : return HydPTUStatus.STANDBY;
    			case 2 : return HydPTUStatus.LEFT;
    			case 3 : return HydPTUStatus.RIGHT;
    		}
    		return HydPTUStatus.OFF;
    	} else if (this.avionics.is_jar_a320neo()) {
    		int hyd_pumps = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_HYD_PUMPS);
    		int ptu_status = (hyd_pumps >> 4) & 0x03; 
    		float ptu_delta =  sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_HYD_PTU);
    		if (ptu_status == 0) {
    			return HydPTUStatus.OFF;
    		} else if (ptu_delta > 0) {
    			return HydPTUStatus.RIGHT;
    		} else if (ptu_delta < 0) {
    			return HydPTUStatus.LEFT;
    		} else {
    			return HydPTUStatus.STANDBY;
    		}
    		/*
    		switch (ptu_status) {
    			case 0 : return HydPTUStatus.OFF;
    			case 1 : return HydPTUStatus.STANDBY;
    			case 2 : return HydPTUStatus.LEFT;
    			case 3 : return HydPTUStatus.RIGHT;
    			}
    		return HydPTUStatus.OFF;
    		*/
    	} else {
    		return HydPTUStatus.STANDBY;
    	}
    }

    public float get_TRQ_LbFt(int engine) {
        // NM = LbFt * 1.35581794884f
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ + engine) / 1.35581794884f;
    }

    public float get_TRQ_Nm(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ + engine);
    }

    public float get_max_TRQ_LbFt() {
        // NM = LbFt * 1.35581794884f
        float override = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EICAS_OVERRIDE_TRQ_MAX);
        if ( override > 0.0f ) return override;
        else return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACF_TRQ_MAX_ENG) / 1.35581794884f;
    }

    public float get_max_TRQ_override() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_EICAS_OVERRIDE_TRQ_MAX);
    }

    public float get_ITT_percent(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_ + engine) * 100.0f;
    }

    public float get_ITT_value(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_C_ + engine);
    }

    public float get_max_prop_RPM() {
        // rev/min = rad/s * 60 / (2 * PI)
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_CONTROLS_ACF_RSC_REDLINE_PRP) * 30.0f / (float)Math.PI;
    }

    public float get_prop_RPM(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ENGINE_INDICATORS_PROP_SPEED_RPM_ + engine);
    }

    public int get_prop_mode(int engine) {
        return (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_PROPMODE_ + engine);
    }

    public float get_NG(int engine) {
        // as seen on several turboprops in X-Plane
//        return get_N1(engine) * 1.04f;
        // well, just to get an idea...
        return get_N1(engine);
    }


    public boolean reverser(int engine) {return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ANNUNCIATORS_REVERSER_DEPLOYED) & (1<<engine) ) != 0 );}
    public float reverser_position(int engine) {
        float ratio = sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ + engine);
        if ( ( ratio > 0.99f ) || ( ( ratio == 0.0f ) && reverser(engine) ) ) {
            ratio = 1.0f;
        } else if ( ( ratio < 0.01f ) && ! reverser(engine) ) {
            ratio = 0.0f;
        }
        return ratio;
    }


    public float get_MPR(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_MPR_ + engine);
    }


    public float get_EPR(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_ENGINE_ENGN_EPR_ + engine);
    }
    
    public float get_EPR_max() {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ENGINE_RED_EPR);
    }
    
    public float get_throttle(int engine) {
        return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ENGINE_ACTUATORS_THROTTLE_RATIO_ + engine);
    }
    
    public IgnitionKeyPosition get_ignition_key(int engine) {
    	if (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS) > 0.0f) {    		
    		int ignition_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ENG_IGNITION);
    		return (ignition_status & 0x10) != 0  ? IgnitionKeyPosition.START  : IgnitionKeyPosition.OFF;
    	} else {
    		int key_pos = ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ENGINE_IGN_KEY) >> (engine*3)) & 0x07;
    		switch (key_pos) {
    		case 0 : return IgnitionKeyPosition.OFF;
    		case 1 : return IgnitionKeyPosition.RIGHT;
    		case 2 : return IgnitionKeyPosition.LEFT;
    		case 3 : return IgnitionKeyPosition.BOTH;
    		case 4 : return IgnitionKeyPosition.START;
    		default : return IgnitionKeyPosition.OFF;
    		}
    	}
        
    }

    public ValveStatus get_ignition_bleed_valve(int engine) {
    	if (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS) > 0.0f) {    		
    		int ignition_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ENG_IGNITION);
    		boolean bleed_valve = (ignition_status & (0x01 << engine)) != 0;
    		return (bleed_valve ? ValveStatus.VALVE_OPEN : ValveStatus.VALVE_CLOSED);
    	} else 
    	return ( get_ignition_key(engine) == IgnitionKeyPosition.START ? ValveStatus.VALVE_OPEN : ValveStatus.VALVE_CLOSED );
    }

    public boolean get_igniter_on(int engine) {
    	if (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS) > 0.0f) {    		
    		int ignition_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ENG_IGNITION);
    		return (ignition_status & 0x10) != 0;
    	} else 
        return ( get_ignition_key(engine) == IgnitionKeyPosition.START );
    	/* Piston Engine : igniter_on = ignition key on the start position 
    	 * Turbine : ignitor_on = ignition key on the start position + N2 between 10% and 30%
    	 */     	 
    }  

    public boolean fire_extinguisher(int engine) {
    	return ( ( (int)sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_ENGINE_FIRE_EXTINGUISHER) & (1<<engine) ) != 0 );
    }
    
    public float get_min_rwy_length() {
        float dataref_rwy_len = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RWY_LENGTH_MIN);
        if ( dataref_rwy_len > 0.0f ) {
            // there is an override for minimum runway length and units via datarefs
            if ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RWY_UNITS) == 1.0f )
                dataref_rwy_len *= 0.3048f;
            return dataref_rwy_len;
        } else {
            // return the preferences setting
            return this.xhsi_preferences.get_min_rwy_length();
        }
    }
    
    
    public boolean rwy_len_meters() {
        float dataref_rwy_len = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RWY_LENGTH_MIN);
        if ( dataref_rwy_len > 0.0f ) {
            // there is an override for minimum runway length and units via datarefs
            return ( sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_RWY_UNITS) == 0.0f );
        } else {
            // return the preferences setting
            return this.xhsi_preferences.get_preference(XHSIPreferences.PREF_RWY_LEN_UNITS).equals("meters");
        }
    }
    
    
    public String get_nearest_arpt() {
        if ( this.nearest_arpt.isEmpty() ) {
            return sim_data.get_sim_string(XPlaneSimDataRepository.XHSI_FLIGHTMODEL_POSITION_NEAREST_ARPT);
        } else {
            return this.nearest_arpt;
        }
    }


    public void set_nearest_arpt(String nrst_arpt) {
        this.nearest_arpt = nrst_arpt;
    }


    // Auxiliary Power Unit (APU)
    public boolean has_apu(){
    	return (num_generators()>1);
    }
    
    public float apu_n1() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.APU_N1);
    }
    
    public float apu_gen_amp() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.APU_GEN_AMP);
    }
    
    public float apu_bleed_psi() {
    	return (apu_n1() < 50.0f) ? 0.0f : (apu_n1()-50.0f) * 0.82f;
    }

    public float apu_egt() {
    	// APU EGT is simulated as a segmented curve is start mode. Peak EGT depends on TAT.
    	// In shutdown mode, it's a linear function of N1
    	float n1 = apu_n1();
    	float base_egt = oat()+5;
    	float peak_egt = 600 + (float) Math.max(oat(), (oat()-isa())*5.5 );
    	float stab_egt = 340 + (float) Math.max(oat(), (oat()-isa())*2.5 );
    	float egt = oat()+5;
    	if (apu_starter()>1) {
    		if (n1 < 15) { 
    			egt = base_egt;
    		} else if (n1 < 30) {
    			// base to peak (n1 between 15 and 30)
    			egt = base_egt + (peak_egt-base_egt) *((n1-15)/15); 
    		} else if (n1 < 99) {
    			// peak to stab (n1 between 30 and 99 - EGT decreasing)
    			egt = stab_egt + (peak_egt-stab_egt) * ((99-n1)/69);
    		} else {
    			egt = stab_egt;
    		}
    	} else if (apu_running()) {
    		egt = stab_egt;
    	} else if (n1 > 1.0) {
    		// shutdown sequence
    		egt = Math.max (n1/100 * stab_egt, base_egt);
    	}
    	return egt;
    }
  
    public float apu_egt_limit() {
    	// EGT limit is 1050°c during startup sequence
    	// At the end of the startup, smoothly goes to 705°
    	// stay at 705° during shutdown sequence
    	// at the end of the shutdown sequence, back to 1050°
    	float egt_limit=1050.0f;
    	float n1 = apu_n1();
    	if (apu_running()) {
    		if (n1 > 60) {
    			egt_limit = 1050 - (1050-705)*(n1-60)/40;
    		} 
    	} else if (n1 > 1) {
    		egt_limit=705;
    	}
    	return egt_limit;
    }
    
    public boolean apu_running() {
    	int apu_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.APU_STATUS);
    	return (( apu_status & 0x10) > 0);
    }

    public boolean apu_gen_on() {
    	int apu_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.APU_STATUS);
    	return (( apu_status & 0x04) > 0);
   	
    }
    
    public int apu_starter() {
    	int apu_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.APU_STATUS);
    	return ( apu_status & 0x03);
    }

    public boolean ram_air_gen_on() {
    	int apu_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.APU_STATUS);
    	return (( apu_status & 0x20) > 0);   	
    }

    public boolean gpu_gen_on() {
    	int apu_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.APU_STATUS);
    	return (( apu_status & 0x40) > 0);   	
    }

    public float gpu_gen_amps() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.GPU_GEN_AMP);
   	}

    public int num_batteries() {
    	return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ELECTRICAL_NUM_BATTERIES);
    }

    public int num_buses() {
    	return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ELECTRICAL_NUM_BUSES);
    }

    public float bus_load_amps(int bus) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_BUS_LOAD_+bus);
    }

    public boolean bus_powered(int bus) {
    	int bus_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_INV_BUS_STATUS);
    	return (bus_status & (0x01 << bus)) != 0;
    }
        
    public int num_generators() {
    	return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ELECTRICAL_NUM_GENERATORS);
    }

    public int num_inverters() {
    	return (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_AIRCRAFT_ELECTRICAL_NUM_INVERTERS);
    }
    
    public boolean inverter_on(int inv) {
    	int bus_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_INV_BUS_STATUS);
    	return (bus_status & (0x01 << (inv+6))) != 0;    	
    }
    
    public boolean ac_bus_tie() {
    	if (this.avionics.is_qpac()) {
        	// Complex aircrafts, Bus Tie depends on GPU, APU and ENG GEN, may be automatic or manual
    		int qpac_ac_cross = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_AC_CROSS);
    		switch (qpac_ac_cross) {
    			case 3 :
    			case 6 :
    			case 7 : return true;
    			default : return false;
    		}
    	} else {
        	// Common aircraft, no bus tie
        	return false;
    	}    		
    }
    
    public ElecBus apu_on_bus() {
    	if (this.avionics.is_qpac()) {
        	// Complex aircrafts, APU may be connected to bus 1, bus 2 or both buses
    		int qpac_ac_cross = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_AC_CROSS);
    		switch (qpac_ac_cross) {
    			case 1 : return ElecBus.BUS_1;
    			case 2 : return ElecBus.BUS_2;
    			case 3 : return ElecBus.BOTH;
    			default : return ElecBus.NONE;
    		}
    	} else {
        	// Common aircraft, APU GEN ON = APU on BUS 1
        	return apu_gen_on() ? ElecBus.BUS_1 : ElecBus.NONE;
    	}    		
    }
    
    public boolean apu_disc() {
    	if (this.avionics.is_qpac()) {
    		int overhead_elec = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_BUTTONS);
    		return ((overhead_elec & (0x04)) != 0);
    	} else {
    		return false;
    	}
    }
    
    public ElecBus gpu_on_bus() {
    	if (this.avionics.is_qpac()) {
        	// Complex aircrafts, GPU may be connected to bus 1, bus 2 or both buses
    		int qpac_ac_cross = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_AC_CROSS);
    		switch (qpac_ac_cross) {
    			case 4 : return ElecBus.BUS_1;
    			case 5 : return ElecBus.BUS_2;
    			case 6 : return ElecBus.BOTH;
    			default : return ElecBus.NONE;
    		}
    	} else {
        	// Common aircraft, APU GEN ON = APU on BUS 1
        	return gpu_gen_on() ? ElecBus.BUS_1 : ElecBus.NONE;
    	}    		
    }

    public ElecBus ac_ess_on_bus() {
    	int qpac_elec_connectors;
    	if (this.avionics.is_qpac()) {
    		qpac_elec_connectors = ((int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_CX_CENTER)) & 0x03;
    		switch (qpac_elec_connectors) {
    		case 0 : return ElecBus.NONE;
    		case 1 : return ElecBus.BUS_1;
    		case 2 : return ElecBus.BUS_2;
    		default: return ElecBus.BOTH;
    		}    		
    	} else {
    		// on AC 1 by default
    		return ElecBus.BUS_1;
    	}
    }
   
    public ElecBus dc_ess_on_bus() {
    	int qpac_elec_left, qpac_elec_right;
    	if (this.avionics.is_qpac()) {
    		qpac_elec_left = ((int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_CX_LEFT)) & 0x1C;
    		qpac_elec_right = ((int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_CX_RIGHT)) & 0x1C;
    		switch (qpac_elec_left) {
    		case 9 : return ElecBus.NONE;
    		case 10 : return ElecBus.BUS_1;
    		case 11 : return ElecBus.BUS_2;
    		default: return ElecBus.BOTH;
    		}    		
    	} else {
    		// on DC 1 by default
    		return ElecBus.BUS_1;
    	}
    }
    
    
    public boolean eng_gen_on_bus(int eng) {
    	int qpac_elec_connectors;
    	if (this.avionics.is_qpac()) {
    		if (eng==0) {  
    			qpac_elec_connectors = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_CX_LEFT);
    		} else { 
    			qpac_elec_connectors = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_CX_RIGHT);
    		}
    		return ((qpac_elec_connectors & 0x01) != 0);
    	} else {
    		return true;
    	}
    }

    public boolean eng_gen_on(int eng) {
    	int gen_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_GENERATOR_STATUS);
   		return (gen_status & (0x01<<eng)) != 0;
    }
        
    /*
     * True if engine generator disconnected by pilot action on the overhead panel
     * 
     */
    public boolean eng_gen_disc(int eng) {
    	if (this.avionics.is_qpac()) {
    		int overhead_elec = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_ELEC_BUTTONS);
    		return ((overhead_elec & (0x01 << eng)) != 0);
    	} else {
    		return false;
    	}
    }   
    
    public float battery_volt(int bat) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_BATTERY_VOLT_+bat);  
    }    

    public float battery_amp(int bat) {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_BATTERY_AMP_+bat);  
    }    
    
    public boolean battery_on(int bat) {
    	int bat_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT_ELECTRICAL_BATTERY_ON);
    	return (bat_status & (0x01 << bat)) != 0;
    }      

    // Bleed Air
    public boolean has_bleed_air() {
    	return true;
    }
    
    public int bleed_air_mode() {
    	return 0;
    }
    
    public ValveStatus bleed_valve(int circuit) {
    	if (this.avionics.is_qpac()) {
    		int bleed = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_BLEED_VALVES);
    		int valve = (bleed >> (circuit * 2)) & 0x03;
    		switch (valve) {
    		case 0 : return ValveStatus.VALVE_CLOSED;
    		case 1 : return ValveStatus.VALVE_OPEN;
    		case 2 : return ValveStatus.VALVE_CLOSED_FAILED;
    		default : return ValveStatus.VALVE_OPEN_FAILED;
    		}
    	} if (this.avionics.is_jar_a320neo()) {
    		int bleed = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BLEED_VALVES);
    		int valve = (bleed >> (circuit * 2)) & 0x03;
    		switch (valve) {
    		case 0 : return ValveStatus.VALVE_CLOSED;
    		case 1 : return ValveStatus.VALVE_OPEN;
    		case 2 : return ValveStatus.VALVE_CLOSED_FAILED;
    		default : return ValveStatus.VALVE_OPEN_FAILED;
    		}
    	} else {
    		return ValveStatus.VALVE_OPEN;
    	}
    }

    public float bleed_air_press(int circuit) {
    	if (this.avionics.is_qpac()) {
    		switch (circuit) {
    		case BLEED_LEFT : return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_BLEED_LEFT_PRESS);
    		case BLEED_RIGHT : return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_BLEED_RIGHT_PRESS);
    		default : return 0;
    		}
    	} else if (this.avionics.is_jar_a320neo()) {
    		switch (circuit) {
    		case BLEED_LEFT : return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BLEED_LEFT_PRESS);
    		case BLEED_RIGHT : return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BLEED_RIGHT_PRESS);
    		default : return 0;
    		}    		
    	} else
    		return -99.0f;
    }
    
    public float bleed_air_temp(int circuit) {
    	if (this.avionics.is_jar_a320neo()) {
    		switch (circuit) {
    		case BLEED_LEFT : return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BLEED_LEFT_TEMP);
    		case BLEED_RIGHT : return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_BLEED_RIGHT_TEMP);
    		default : return 0;
    		}    		
    	} else
    		return -99.0f;
    }
    
    public ValveStatus ram_air_valve() {
    	int air_valve = (int)sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_AIR_VALVES);
    	return (air_valve >> 4 & 0x03) > 0 ? ValveStatus.VALVE_OPEN : ValveStatus.VALVE_CLOSED;        
    }
   
    public float pack_flow(int pack) {
    	if (this.avionics.is_jar_a320neo()) {
    		if (pack==1) 
    			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK1_FLOW);
   			else 
       			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK2_FLOW); 				
    	} else 
    		return -99.0f;
    }

    public float pack_out_temp(int pack) {
    	if (this.avionics.is_jar_a320neo()) {
    		if (pack==1) 
    			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK1_TEMP);
   			else 
       			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK2_TEMP); 				
    	} else 
    		return -99.0f;
    }

    public float pack_comp_temp(int pack){
    	if (this.avionics.is_jar_a320neo()) {
    		if (pack==1) 
    			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK1_COMP_TEMP);
   			else 
       			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK2_COMP_TEMP); 				
    	} else 
    		return -99.0f;   	
    }
    
    public float pack_bypass_ratio(int pack) {
    	if (this.avionics.is_jar_a320neo()) {
    		if (pack==1) 
    			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK1_BYPASS_RATIO);
   			else 
       			return sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_PACK2_BYPASS_RATIO); 				
    	} else 
    		return -99.0f;   	
    } 
    
    // Cabin pressurization
    public float cabin_altitude() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_PRESSURIZATION_CABIN_ALT);
    }
    
    public float cabin_delta_p() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_PRESSURIZATION_CABIN_DELTA_P);
    }
    
    public float cabin_vs() {
    	return sim_data.get_sim_float(XPlaneSimDataRepository.SIM_COCKPIT2_PRESSURIZATION_CABIN_VVI);
    }

    public float cabin_outflow_valve() {    	
    	return sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_OUTFLOW_VALVE);
    }

    public float cabin_inlet_valve() {
    	// Ventilation inlet valve - always closed when not simulated 
    	return 0.0f;
    }

    public float cabin_extract_valve() {
    	// Ventilation inlet valve - always closed when not simulated
    	return 0.0f;
    }

    public float cabin_safety_valve() {
    	// Cabin pressure safety valve - normaly closed - open when delta p > 8.5
    	return 0.0f;
    }

    public float cabin_temp(CabinZone zone) {
    	float temp = -99.0f;
    	if (this.avionics.is_qpac() || this.avionics.is_jar_a320neo()) {
    		switch (zone) {
    		case COCKPIT: temp = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_COCKPIT_TEMP); break;
    		case AFT: temp = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_AFT_CABIN_TEMP); break;
    		case FORWARD: temp = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_FWD_CABIN_TEMP); break;
    		case CARGO_FWD: temp = this.avionics.is_jar_a320neo() ? sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_FWD_CARGO_TEMP) : -99.0f; break;
    		case CARGO_AFT: temp = this.avionics.is_jar_a320neo() ? sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_AFT_CARGO_TEMP) : -99.0f; break;
    		default : temp = -20.0f;
    		}
    	}
    	return temp;
    }
    
    public float cabin_inlet_temp(CabinZone zone)  {
    	float temp = -99.0f;
    	if (this.avionics.is_qpac() || this.avionics.is_jar_a320neo()) {
    		switch (zone) {
    		case COCKPIT: temp = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_INLET_COCKPIT_TEMP); break;
    		case AFT: temp = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_INLET_AFT1_CABIN_TEMP); break;
    		case FORWARD: temp = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_INLET_FWD1_CABIN_TEMP); break;
    		case CARGO_FWD: temp = this.avionics.is_jar_a320neo() ? sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_INLET_CARGO_FWD_TEMP) : -99.0f; break;
    		case CARGO_AFT: temp = this.avionics.is_jar_a320neo() ? sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_INLET_CARGO_AFT_TEMP) : -99.0f; break;
    		default : temp = -20.0f;
    		}
    	}
    	return temp;
    }
    
    public float cabin_hot_air_trim(CabinZone zone) {
    	float trim = 0.0f;
    	if (this.avionics.is_qpac() || this.avionics.is_jar_a320neo()) {
    		switch (zone) {
    		case COCKPIT: trim = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_COCKPIT_TRIM); break;
    		case AFT: trim = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_ZONE2_TRIM); break;
    		case FORWARD: trim = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_ZONE1_TRIM); break;
    		case CARGO_FWD: trim = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_CARGO_FWD_TRIM); break;
    		case CARGO_AFT: trim = sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_CARGO_AFT_TRIM); break;
    		default : trim = 0.15f;
    		}
    	}
    	return trim;
    }   
    
    public ValveStatus cabin_hot_air_valve(CabinZone zone) {
    	if (this.avionics.is_qpac()) {
    		int air_valve = (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_HOT_AIR_VALVES);
    		int valve = air_valve & 0x03;
    		switch (valve) {
    		case 0 : return ValveStatus.VALVE_CLOSED;
    		case 1 : return ValveStatus.VALVE_CLOSED_FAILED;
    		case 2 : return ValveStatus.VALVE_OPEN;
    		default : return ValveStatus.VALVE_OPEN_FAILED;
    		}
    	} else if (this.avionics.is_jar_a320neo()) {
    		int air_valve = (int) sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_COND_HOT_AIR_VALVES);
    		int valve = (zone == CabinZone.CARGO_AFT) ? (air_valve >> 4) & 0x03 : air_valve & 0x03;
    		switch (valve) {
    		case 0 : return ValveStatus.VALVE_CLOSED;
    		case 1 : return ValveStatus.VALVE_OPEN;
    		case 2 : return ValveStatus.VALVE_CLOSED_FAILED;
    		default : return ValveStatus.VALVE_OPEN_FAILED;
    		}
    	} else {
    		return ValveStatus.VALVE_OPEN;
    	}
    }
    
    public float crew_oxygen_psi() {
    	// Cabin crew oxygen bottle pressure
    	return sim_data.get_sim_float(XPlaneSimDataRepository.XHSI_CREW_OXY_PSI);
    }    
    
    public boolean door_unlocked(DoorId door) {
    	boolean door_position;   
    	if (sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_STATUS) > 0.0f) {
    		int door_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.JAR_A320NEO_DOOR_STATUS);
    		switch (door) {
    		case FRONT_LEFT: 	door_position = (door_status & 0x10) != 0; break;
    		case FRONT_RIGHT: 	door_position = (door_status & 0x20) != 0; break;
    		case AFT_LEFT: 		door_position = (door_status & 0x04) != 0; break;
    		case AFT_RIGHT: 	door_position = (door_status & 0x08) != 0; break;
    		case FRONT_CARGO: 	door_position = (door_status & 0x02) != 0; break;
    		case AFT_CARGO: 	door_position = (door_status & 0x01) != 0; break;
    		default: 			door_position = false; // Door is locked
    		}
    	} else if (sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_STATUS) > 0.0f) {
    		int door_status = (int) sim_data.get_sim_float(XPlaneSimDataRepository.QPAC_DOOR_STATUS);
    		switch (door) {
    		case FRONT_LEFT: 	door_position = (door_status & 0x02) != 0; break;
    		case FRONT_RIGHT: 	door_position = (door_status & 0x04) != 0; break;
    		case AFT_LEFT: 		door_position = (door_status & 0x08) != 0; break;
    		case AFT_RIGHT: 	door_position = (door_status & 0x10) != 0; break;
    		case FRONT_CARGO: 	door_position = (door_status & 0x20) != 0; break;
    		case AFT_CARGO: 	door_position = (door_status & 0x40) != 0; break;
    		case BULK: 			door_position = (door_status & 0x01) != 0; break;
    		default: 			door_position = false; // Door is locked
    		}
    	} else 	{
    		// use beacon switch status
    		if (! beacon_on() & (door == DoorId.FRONT_LEFT | door == DoorId.FRONT_CARGO | door == DoorId.AFT_CARGO | door == DoorId.BULK)) {
    			door_position=true;
    		} else {
    			door_position=false;
    		}
    	}
    	return door_position;
    }
      
}
