/**
* Aircraft.java
* 
* Model class for an aircraft.
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

import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;

public interface Aircraft {
	
    public enum ValveStatus { VALVE_OPEN, VALVE_CLOSED, VALVE_OPEN_FAILED, VALVE_CLOSED_FAILED, JAMMED };
    public enum SpoilerStatus { RETRACTED, EXTENDED, FAILED, JAMMED };
    public enum HydPumpStatus { OFF, ON, FAILED };
    public enum PumpStatus { OFF, ON, LOW_PRESSURE, FAILED }; 
    public enum HydPTUStatus { OFF, STANDBY, LEFT, RIGHT };
    public enum ElecBus { NONE, BUS_1, BUS_2, BOTH };
    
    // Bleed valve circuits
    public final static int BLEED_VALVE_CROSS = 0;
    public final static int BLEED_VALVE_APU = 1;
    public final static int BLEED_VALVE_ENG1 = 2;
    public final static int BLEED_VALVE_ENG2 = 3;
    public final static int BLEED_VALVE_ENG1_HP = 4;
    public final static int BLEED_VALVE_ENG2_HP = 5;
    public final static int BLEED_VALVE_PACK1 = 6;
    public final static int BLEED_VALVE_PACK2 = 7;
    // Air valve circuits
    public final static int AIR_VALVE_RAM_AIR = 0;
    public final static int AIR_VALVE_HOT_AIR = 1;    
    
    /**
     * @return int - xhsi plugin version
     */
    public int plugin_version();
    
    /**
     * @return String - aircraft_registration
     */
    public String aircraft_registration();

    /**
     * @return boolean - battery power is on
     */
    public boolean battery();

    /**
     * @return boolean - cockpit_lights are on
     */
    public boolean cockpit_lights();

    /**
     * @return float - latitude in degrees
     */
    public float lat();

    /**
     * @return float - longitude in degrees
     */
    public float lon();

    /**
     * @return float - MSL in meters
     */
    public float msl_m();

    /**
     * @return float - AGL in meters
     */
    public float agl_m();

    /**
     * @return float - turn speed in degrees per second
     */
    public float turn_rate();

    /**
     * @return float - ground speed in knots
     */
    public float ground_speed();

    /**
     * @return float - true air speed in knots
     */
    public float true_air_speed();

    /**
     * @return float - magnetic heading of the aircraft in degrees
     */
    public float heading();

    /**
     * @return float - hpath in degrees
     */
    public float hpath();

//    /**
//     * @return float - altitude
//     */
//    public float indicated_altitude();

//    /**
//     * @return float - vertical velocity
//     */
//    public float indicated_vv();

    /**
     * @return float - pitch angle of the aircraft in degrees
     */
    public float pitch();

    /**
     * @return float - roll angle of the aircraft in degrees
     */
    public float bank();

    /**
     * @return float - g load factor - 1.0 is standard earth attraction 
     */
    public float g_load();

    /**
     * @return float - Yoke pitch ratio (-1.0f to +1.0f)
     */

    public float yoke_pitch();

    /**
     * @return float - Yoke roll ratio (-1.0f to +1.0f)
     */
    public float yoke_roll();

    /**
     * @return float - Rudder heading ratio (-1.0f to +1.0f)
     */
    public float rudder_hdg();

    /**
     * @return float - Left and Right Pedal deflection (0.0f to +1.0f)
     */
    public float brk_pedal_left();
    public float brk_pedal_right();

    
    /**
     * Returns the magnetic track of the aircraft in degrees. If ground_speed
     * is lower than 5 knots, returns heading of aircraft.
     *
     * @return float - magnetic track of the aircraft in degrees
     */
    public float track();

    /**
     * Returns the difference between the track and the heading
     * of the aircraft.
     *
     * @return float - difference of horizontal path and heading in degrees
     */
    public float drift();

    /**
     * @return float - sideslip in degrees
     */
    public float sideslip();

    /**
     * @return float - angle of attack
     */
    public float aoa();

    /**
     * @return float - IAS in kts
     */
    public float airspeed_ind();

    /**
     * @return float - altitude in ft
     */
    public float altitude_ind();

    /**
     * @return float - VVI in fpm
     */
    public float vvi();

    /**
     * @return int - radar altimeter bug for pilot or copilot
     */
    public int ra_bug();

    /**
     * @return int - decision altitude bug for pilot or copilot
     */
    public int da_bug();

    /**
     * @return boolean - minimums reference is decision altitude, not decision height
     */
    public boolean mins_is_baro();

    /**
     * @return int - qnh setting for pilot or copilot
     */
    public int qnh();
    public int qnh(boolean pilot);

    /**
     * @return int - qnh setting for pilot or copilot
     */
    public float altimeter_in_hg();
    public float altimeter_in_hg(boolean pilot);

    /**
     * @return float - ASI trend kts/s
     */
    public float airspeed_acceleration();

     /**
     * @return float - magnetic variation at current position
     */
    public float magnetic_variation();

    /**
     * @return float - outside air temperature
     */
    public float oat();

    /**
     * @return float - total air temperature
     */
    public float tat();

    /**
     * @return float - temperature at sealevel
     */
    public float isa();
    
    /**
     * @return float - mach number
     */
    public float mach();

    /**
     * @return float - This is the speed of sound in Kts at the plane's location.
     */
    public float sound_speed();

    /**
     * Returns the distance between the aircraft and the given navigation
     * object in nautical miles.
     *
     * @param nav_object - the navigation object to which the distance should be computed
     * @return float - distance to nav_object in nautical miles
     */
    public float distance_to(NavigationObject nav_object);

    /**
     * Returns a rough distance between the aircraft and the given navigation
     * object in nautical miles. This calculation is less accurate but much
     * faster than <code>distance_to</code>.
     *
     * @param nav_object - the navigation object to which the distance should be computed
     * @return float - distance to nav_object in nautical miles
     */
    public float rough_distance_to(NavigationObject nav_object);

    /**
     * Returns the EET to arrive at the given
     * navigation object based on the distance to the aircraft and its
     * current ground speed. Note: we assume the aircraft is on a direct
     * course to nav_object. I do not consider the actual closing speed.
     *
     * @param nav_object - the navigation object
     * @return long - the EET to the nav_object
     */
    public long ete_to(NavigationObject nav_object);

    /**
     * Returns the zulu ETA time when the aircraft will arive at the given
     * navigation object based on the distance to the aircraft and its
     * current ground speed. Note: we assume the aircraft is on a direct
     * course to nav_object. I do not consider the actual closing speed.
     *
     * @param nav_object - the navigation object
     * @return long - the arrival time at nav_object in zulu time
     */
    public long time_when_arriving_at(NavigationObject nav_object);

    /**
     * Returns the EET to fly the given distance at the current
     * ground speed.
     *
     * @param distance - the distance to cover
     * @return long - the time to fly the distance
     */
    public long ete_for_distance(float distance);

    /**
     * Returns the time in hours to fly the given distance at the current
     * ground speed.
     *
     * @param distance - the distance to cover
     * @return long - the time to fly the distance
     */
    public long time_after_distance(float distance);

    /**
     * Returns the current time + EET
     *
     * @param eet - the EET in minutes
     * @return long - current time + EET
     */
    public long time_after_ete(float eet);

    /**
     * @return float - simulator zulu time in seconds
     */
    public float sim_time_zulu();

    /**
     * @return float - simulator local time in seconds
     */
    public float sim_time_local();

    /**
     * @return float - elapsed time (used for the CHR)
     */
    public float timer_elapsed_time();

    /**
     * @return float - total flight time (used for the ET)
     */
    public float total_flight_time();

//    /**
//     * @return boolean - timer is running (used for the CHR)
//     */
//    public boolean timer_is_running();

    /**
     * @return Avionics - reference to avionics model of this aircraft
     */
    public Avionics get_avionics();

    /**
     * @return AircraftEnvironment - reference to environment model of this aircraft
     */
    public AircraftEnvironment get_environment();


    /**
     * @return int - number of gears
     */
    public int num_gears();


    /**
     * @return boolean - gear is retractable
     */
    public boolean has_retractable_gear();
    
    
    /**
     * @return float - position of a gear
     */
    public float get_gear(int gear);


    /**
     * @return boolean - all gears down and locked?
     */
    public boolean gear_is_down();


    /**
     * @return boolean - all gears up?
     */
    public boolean gear_is_up();


    /**
     * @return boolean - are we on the ground?
     */
    public boolean on_ground();


    /**
     * @return boolean - master warning lit?
     */
    public boolean master_warning();


    /**
     * @return boolean - master warning lit?
     */
    public boolean master_caution();


    /**
     * @return float - pitch trim
     */
    public float get_pitch_trim();


    /**
     * @return float - roll trim
     */
    public float get_roll_trim();


    /**
     * @return float - yaw trim
     */
    public float get_yaw_trim();

    /**
     * @return float - left elevator position
     */
    public float get_left_elev_pos();
    
    /**
     * @return float - right elevator position
     */
    public float get_right_elev_pos();
    
    /**
     * @return float - left aileron position
     */
    public float get_left_aileron_pos();
    
    /**
     * @return float - right aileron position
     */
    public float get_right_aileron_pos();    
    
    /**
     * @return float - rudder elevator position
     */
    public float get_rudder_pos();

    /**
     * @return float - aileron max angle up
     */
    public float get_aileron_max_up();

    /**
     * @return float - aileron max angle down
     */
    public float get_aileron_max_down();
    
    /**
     * @return float - elevator max angle up
     */
    public float get_elev_max_up();

    /**
     * @return float - elevator max angle down
     */
    public float get_elev_max_down();
    
    /**
     * @return float - rudder max angle left and right
     */
    public float get_rudder_max_lr();

    /**
     * @return float - slats position
     */
    public float get_slat_position();


    /**
     * @return float - flaps position
     */
    public float get_flap_position();


    /**
     * @return float - flaps handle position
     */
    public float get_flap_handle();


    /**
     * @return int - number of flap detents
     */
    public int get_flap_detents();


    /**
     * @return float - Actual Speed Brake position
     */
    public float get_speed_brake();


    /**
     * @return boolean - Aircraft has speedbrake/spoilers
     */
    public boolean has_speed_brake();


    /**
     * @return boolean - Speed Brake armed
     */
    public boolean speed_brake_armed();

    /**
     * @return int - number of spoilers
     */
    public int num_spoilers();
    
    /**
     * @return float - spoiler deflection ration
     */
    public float get_spoiler_pos(int pos);
    
    /**
     * @return float - left wing spoiler status
     */    
    public SpoilerStatus get_spoiler_status_left(int pos);    

    /**
     * @return float - right wing spoiler status
     */    
    public SpoilerStatus get_spoiler_status_right(int pos);    
   
    /**
     * @return float - Parking Brake
     */
    public float get_parking_brake();

    
    /**
     * @return int - signs : 0=off, 1=auto, 2=on 
     */
    public int seat_belt_sign(); 
    public int no_smoking_sign();
    
    /**
     * @return boolean - Lights 
     */
    public boolean landing_lights_on();
    public boolean landing_taxi_lights_on();
    public boolean beacon_on();
    public boolean nav_lights_on();
    public boolean strobe_lights_on();
    

    /**
     * @return boolean - Stall warning
     */
    public boolean stall_warning();


    /**
     * @return boolean - GPWS
     */
    public boolean terrain_warning();


    /**
     * @return boolean - low fuel
     */
    public boolean low_fuel();


    /**
     * @return boolean - AP disconnect
     */
    public boolean ap_disconnect();


    /**
     * @return boolean - icing detected
     */
    public boolean icing();


    /**
     * @return boolean - pitot heat is off
     */
    public boolean pitot_heat();


    /**
     * @return boolean - gear warning
     */
    public boolean gear_warning();


    /**
     * @return boolean - gear unsafe
     */
    public boolean gear_unsafe();


    /**
     * @return int - Autobrake level -1=RTO / 0=Off / 1..4=On
     */
    public int auto_brake();


    /**
     * @return float - Vso: stall landing configuration
     */
    public float get_Vso();


    /**
     * @return float - Vs: stall clean
     */
    public float get_Vs();


    /**
     * @return float - Vfe: max flaps extended
     */
    public float get_Vfe();


    /**
     * @return float - Vno: max normal
     */
    public float get_Vno();


    /**
     * @return float - Vne: never exceed
     */
    public float get_Vne();


    /**
     * @return float - Mmo: max mach
     */
    public float get_Mmo();


    /**
     * @return float - Vle: max landing gear extended
     */
    public float get_Vle();


    /**
     * @return float - Vmca: min control speed when airborne
     */
    public float get_Vmca();


    /**
     * @return float - Vyse: best rate of climb speed with a single operating engine in a light, twin-engine aircraft
     */
    public float get_Vyse();


    /**
     * @return int - number of engines
     */
    public int num_engines();


    /**
     * @return int - number of fuel tanks
     */
    public int num_tanks();

    /**
     * @return float - relative size of a fuel tank (total = 1.0f)
     */
    public float tank_ratio(int tank);


    /**
     * @return boolean - low (or high) oil pressure
     */
    public boolean oil_press_alert(int eng);


    /**
     * @return boolean - high oil temperature
     */
    public boolean oil_temp_alert(int eng);


    /**
     * @return boolean - low fuel pressure
     */
    public boolean fuel_press_alert(int eng);


    /**
     * @return float - Fuel quantity (kg) per tank
     */
    public float get_fuel(int tank);


    /**
     * @return float - Total Fuel quantity (kg)
     */
    public float get_total_fuel();

    public void estimate_fuel_capacity();

    public float get_fuel_capacity();

    public float get_tank_capacity(int tank);

    /**
     * @return PumpStatus - Tank pump on, off, low pressure or failed
     */
    public PumpStatus get_tank_pump(int tank);

    /**
     * @return ValveStatus - Fuel XFer Valve
     */
    public ValveStatus get_tank_xfer_valve();


    public float fuel_multiplier();
    
//    public void set_fuel_capacity(float capacity);
    
    /**
     * @return float - Gross Weight (kg)
     */
    public float gross_weight();

    /**
     * @return float - Engine N1 %
     */
    public float get_N1(int engine);


    /**
     * @return float - UFMC Reference N1 %
     */
    public float get_ref_N1(int engine);
    
    
    /**
     * @return String Thrust Mode : 0: ---, 1: TO, 2: R-TO, 3: R- CLB, 4: CLB, 4: CRZ, 6: GA, 7:CON, 8: MAX.
     */
    public String get_thrust_mode();


    /**
     * @return float - Engine EGT %
     */
    public float get_EGT_percent(int engine);


    /**
     * @return float - Engine EGT value
     */
    public float get_EGT_value(int engine);


    /**
     * @return float - Engine N2 %
     */
    public float get_N2(int engine);


    /**
     * @return float - Engine FF
     */
    public float get_FF(int engine);


    /**
     * @return float - Maximum recorded FF
     */
    public float get_max_FF();


    /**
     * @void - Rest maximum recorded FF
     */
    public void reset_max_FF();


    /**
     * @return float - Oil P ratio
     */
    public float get_oil_press_ratio(int engine);

    /**
     * @return float - Oil T ratio
     */
    public float get_oil_temp_ratio(int engine);

    /**
     * @return float - Oil Q ratio
     */
    public float get_oil_quant_ratio(int engine);

    /**
     * @return float - Engine vibration %
     */
    public float get_vib(int engine);

    /**
     * @return float - Hydraulics P ratio
     */
    public float get_hyd_press(int circuit);

    /**
     * @return float - Hydraulics Q ratios
     */
    public float get_hyd_quant(int circuit);

    /**
     * @return HydPumpStatus - Hydraulics main pumps (OFF, ON, FAILED);
     */
    public HydPumpStatus get_hyd_pump(int circuit);

    /**
     * @return HydPTUStatus - Hydraulics PTU (OFF, STANDBY, LEFT, RIGHT);
     */
    public HydPTUStatus get_hyd_ptu();

    
    /**
     * @return float - Maximum _available_ engine TRQ
     */
    public float get_max_TRQ_LbFt();

    /**
     * @return float - The value to override the max TRQ
     */
    public float get_max_TRQ_override();
    
    /**
     * @return float - Engine TRQ
     */
    public float get_TRQ_LbFt(int engine);

    /**
     * @return float - Engine TRQ
     */
    public float get_TRQ_Nm(int engine);

    /**
     * @return float - ITT %
     */
    public float get_ITT_percent(int engine);

    /**
     * @return float - Engine ITT deg C
     */
    public float get_ITT_value(int engine);

    /**
     * @return float - prop RPM redline
     */
    public float get_max_prop_RPM();
    
    /**
     * @return float - prop RPM
     */
    public float get_prop_RPM(int engine);

    /**
     * @return int - prop mode (0=feather, 1=normal, 2=beta, 3=reverse)
     */
    public int get_prop_mode(int engine);
    
    /**
     * @return float - NG %
     */
    public float get_NG(int engine);

    
    /**
     * @return boolean - Is thrust reverser deployed
     */
    public boolean reverser(int engine);


    /**
     * @return float - Thrust reverser deploy ratio
     */
    public float reverser_position(int engine);


    /**
     * @return float - Engine MPR
     */
    public float get_MPR(int engine);


    /**
     * @return float - Engine EPR
     */
    public float get_EPR(int engine);

    /**
     * @return float - Engine Throttle ratio
     */
    public float get_throttle(int engine);

    /**
     * @return boolean - Fire extinguisher on
     */
    public boolean fire_extinguisher(int engine);

    /**
     * @return float - Minimum runway length
     */
    public float get_min_rwy_length();


    // TODO : Check if boolean is appropriate 
    /**
     * @return boolean - Runway length in meters
     */
    public boolean rwy_len_meters();


    /**
     * @return String - Nearest airport
     */
    public String get_nearest_arpt();


    /**
     * @return String - Nearest airport
     */
    public void set_nearest_arpt(String nrst_arpt);

    /**
     * @return boolean - Aircraft has Auxiliary Power Unit (APU)
     */
    public boolean has_apu();
  
    /**
     * @return Float - Auxiliary Power Unit (APU) N1
     */
    public float apu_n1();
    
    /**
     * @return Float - Auxiliary Power Unit (APU) EGT
     */
    public float apu_egt();
    
    /**
     * @return Float - Auxiliary Power Unit (APU) EGT Warning limit
     */
    public float apu_egt_limit();
    
    /**
     * @return Float - Auxiliary Power Unit (APU) Generator output current (Amp)
     */
    public float apu_gen_amp();
    
    /**
     * @return boolean - Auxiliary Power Unit (APU) - True if APU is running
     */   
    public boolean apu_running();

    /**
     * @return boolean - Auxiliary Power Unit (APU) - True if APU generator is available
     */   
    public boolean apu_gen_on();
    
    /**
     * @return int - Auxiliary Power Unit (APU) starter position (0, 1 or 2)
     */  
    public int apu_starter();

    /**
     * @return boolean - Emergency Ram Air Generator On 
     */  
    public boolean ram_air_gen_on();

    /**
     * @return boolean - Ground Power Unit Generator On 
     */  
    public boolean gpu_gen_on();
    
    /**
     * @return float - Ground Power Unit Generator Amps 
     */  
    public float gpu_gen_amps();

    /**
     * @return int - Number of batteries 
     */  
    public int num_batteries();
    
    /**
     * @return int - Number of electric buses 
     */  
    public int num_buses();

    /**
     * @return int - Number of generators (may differ from number of engines) 
     */  
    public int num_generators();

    /**
     * @return int - Number of inverters 
     */  
    public int num_inverters();    
    
    /**
     * @return boolean - AC bus tie 
     */  
    public boolean ac_bus_tie();  
    
    /**
     * @return ElecBus - APU AC bus connection 
     */  
    public ElecBus apu_on_bus();
    
    /**
     * @return ElecBus - GPU AC bus connection
     */  
    public ElecBus gpu_on_bus();
    
    /**
     * @return ElecBus - AC Essential bus connection (AC1 or AC2) 
     */  
    public ElecBus ac_ess_on_bus();
        
    /**
     * @return boolean - Engine Generator on bus (nb gen. may differ from number of engines) 
     */  
    public boolean eng_gen_on_bus(int eng);  
        
    
    /**
     * @return boolean - Aircraft has Bleed Air Circuits (ENG & APU)
     */
    public boolean has_bleed_air();

    /**
     * @return ValveStatus - Aircraft has Bleed Air Circuits (ENG & APU)
     */
    public ValveStatus bleed_valve(int circuit);
    
    /**
     * @return Float - Cabin altitude in feet
     */
    public float cabin_altitude();
    
    /**
     * @return Float - Cabin delta pressure (psi ratio)
     */
    public float cabin_delta_p();

    /**
     * @return Float - Cabin pressure vertical speed (feet / minute)
     */
    public float cabin_vs();
    
}
