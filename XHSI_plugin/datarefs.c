
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>


#define XPLM200 1

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPLMPlugin.h"
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"


#include "plugin.h"
#include "commands.h"
#include "globals.h"
#include "structs.h"
#include "ids.h"
#include "xfmc.h"
#include "datarefs.h"
#include "datarefs_x737.h"
#include "datarefs_qpac.h"
#include "datarefs_jar_a320neo.h"
#include "datarefs_xjoymap.h"

#define MSG_ADD_DATAREF 0x01000000           //  Add dataref to DRE message


// variables that will contain references to be used by XPLMGetData...

/*
 *  XHSI Custom datarefs
 *  --------------------
 */

// custom datarefs - general
XPLMDataRef xhsi_instrument_style;
XPLMDataRef xhsi_rwy_length_min;
XPLMDataRef xhsi_rwy_units;
//XPLMDataRef xhsi_rtu_contact_atc;
XPLMDataRef xhsi_rtu_selected_radio;

// custom datarefs - common clock and timer
XPLMDataRef xhsi_clock_brightness;
XPLMDataRef xhsi_utc_selector;
XPLMDataRef xhsi_et_running;
XPLMDataRef xhsi_et_frozen_time;
XPLMDataRef xhsi_show_date;

// custom datarefs - EICAS
XPLMDataRef eicas_brightness;
XPLMDataRef engine_type;
XPLMDataRef trq_scale;
XPLMDataRef fuel_units;
XPLMDataRef temp_units;
XPLMDataRef override_trq_max;
XPLMDataRef fwc_phase;

// custom datarefs - MFD
XPLMDataRef mfd_brightness;
XPLMDataRef mfd_mode;
XPLMDataRef mfd_fuel_used;
XPLMDataRef mfd_crew_oxy_psi;

// custom datarefs - CDU
XPLMDataRef cdu_pilot_brightness;
XPLMDataRef cdu_copilot_brightness;
XPLMDataRef cdu_pilot_source;
XPLMDataRef cdu_copilot_source;
XPLMDataRef cdu_pilot_side;
XPLMDataRef cdu_copilot_side;

// custom datarefs - EGPWS
XPLMDataRef egpws_flaps_mode;
XPLMDataRef egpws_gs_mode;
XPLMDataRef egpws_sys;

// custom datarefs - pilot
XPLMDataRef efis_pilot_nd_brightness;
XPLMDataRef efis_pilot_pfd_brightness;
XPLMDataRef efis_pilot_shows_stas;
XPLMDataRef efis_pilot_shows_data;
XPLMDataRef efis_pilot_shows_pos;
XPLMDataRef efis_pilot_shows_terrain;
XPLMDataRef efis_pilot_shows_vp;
XPLMDataRef efis_pilot_shows_ils;
XPLMDataRef efis_pilot_metric_alt;
XPLMDataRef efis_pilot_track_fpa;
XPLMDataRef efis_pilot_da_bug;
XPLMDataRef efis_pilot_mins_mode;
XPLMDataRef efis_pilot_map_zoomin;
XPLMDataRef efis_pilot_chrono;
XPLMDataRef efis_pilot_chrono_running;
XPLMDataRef efis_pilot_wxr_gain;
XPLMDataRef efis_pilot_wxr_tilt;
XPLMDataRef efis_pilot_wxr_auto_tilt;
XPLMDataRef efis_pilot_wxr_auto_gain;
XPLMDataRef efis_pilot_wxr_test;
XPLMDataRef efis_pilot_wxr_mode;    // 0=off, 1=weather, 2=weather+turbulence, 3=map
XPLMDataRef efis_pilot_wxr_slave;
XPLMDataRef efis_pilot_wxr_react;
XPLMDataRef efis_pilot_wxr_narrow;
XPLMDataRef efis_pilot_wxr_alert;
XPLMDataRef efis_pilot_wxr_target;

// custom datarefs - copilot
XPLMDataRef efis_copilot_nd_brightness;
XPLMDataRef efis_copilot_pfd_brightness;
XPLMDataRef efis_copilot_map_range_selector;
XPLMDataRef efis_copilot_dme_1_selector;
XPLMDataRef efis_copilot_dme_2_selector;
XPLMDataRef efis_copilot_shows_tcas;
XPLMDataRef efis_copilot_shows_airports;
XPLMDataRef efis_copilot_shows_waypoints;
XPLMDataRef efis_copilot_shows_vors;
XPLMDataRef efis_copilot_shows_ndbs;
XPLMDataRef efis_copilot_shows_stas;
XPLMDataRef efis_copilot_shows_data;
XPLMDataRef efis_copilot_shows_pos;
XPLMDataRef efis_copilot_shows_terrain;
XPLMDataRef efis_copilot_shows_vp;
XPLMDataRef efis_copilot_shows_ils;
XPLMDataRef efis_copilot_metric_alt;
XPLMDataRef efis_copilot_track_fpa;
XPLMDataRef efis_copilot_map_mode;
XPLMDataRef efis_copilot_map_submode;
XPLMDataRef copilot_hsi_selector;
XPLMDataRef efis_copilot_da_bug;
XPLMDataRef efis_copilot_mins_mode;
XPLMDataRef efis_copilot_map_zoomin;
XPLMDataRef efis_copilot_chrono;
XPLMDataRef efis_copilot_chrono_running;
XPLMDataRef efis_copilot_wxr_gain;
XPLMDataRef efis_copilot_wxr_tilt;
XPLMDataRef efis_copilot_wxr_auto_tilt;
XPLMDataRef efis_copilot_wxr_auto_gain;
XPLMDataRef efis_copilot_wxr_test;
XPLMDataRef efis_copilot_wxr_mode;
XPLMDataRef efis_copilot_wxr_slave;
XPLMDataRef efis_copilot_wxr_react;
XPLMDataRef efis_copilot_wxr_narrow;
XPLMDataRef efis_copilot_wxr_alert;
XPLMDataRef efis_copilot_wxr_target;


/*
 *  Standard datarefs
 *  -----------------
 */
XPLMDataRef groundspeed;
XPLMDataRef true_airspeed;
XPLMDataRef magpsi;
XPLMDataRef hpath;
XPLMDataRef latitude;
XPLMDataRef longitude;
XPLMDataRef phi;
XPLMDataRef r;
XPLMDataRef magvar;
XPLMDataRef msl;
XPLMDataRef agl;
XPLMDataRef theta;
XPLMDataRef vpath;
XPLMDataRef alpha;
XPLMDataRef beta;
XPLMDataRef on_ground;

//XPLMDataRef vh_ind_fpm;
//XPLMDataRef h_ind;
XPLMDataRef airspeed_pilot;
XPLMDataRef airspeed_copilot;
XPLMDataRef altitude_pilot;
XPLMDataRef altitude_copilot;
XPLMDataRef vvi_pilot;
XPLMDataRef vvi_copilot;
XPLMDataRef sideslip;
XPLMDataRef ra_bug_pilot;
XPLMDataRef ra_bug_copilot;
XPLMDataRef baro_pilot;
XPLMDataRef baro_copilot;
XPLMDataRef airspeed_acceleration;
XPLMDataRef g_load;
XPLMDataRef turnrate_noroll;

// Instruments failures pilot
XPLMDataRef sim_op_fail_rel_ss_ahz;
XPLMDataRef sim_op_fail_rel_ss_alt;
XPLMDataRef sim_op_fail_rel_ss_asi;
XPLMDataRef sim_op_fail_rel_ss_dgy;
XPLMDataRef sim_op_fail_rel_ss_tsi;
XPLMDataRef sim_op_fail_rel_ss_vvi;
// Instruments failures co-pilot
XPLMDataRef sim_op_fail_rel_cop_ahz;
XPLMDataRef sim_op_fail_rel_cop_alt;
XPLMDataRef sim_op_fail_rel_cop_asi;
XPLMDataRef sim_op_fail_rel_cop_dgy;
XPLMDataRef sim_op_fail_rel_cop_tsi;
XPLMDataRef sim_op_fail_rel_cop_vvi;
// Gears failures
XPLMDataRef sim_op_fail_rel_gear_ind;
XPLMDataRef sim_op_fail_rel_gear_act;
// Brake failures
XPLMDataRef sim_op_fail_rel_lbrake;
XPLMDataRef sim_op_fail_rel_rbrake;
// Tires
XPLMDataRef sim_op_fail_rel_tire1;
XPLMDataRef sim_op_fail_rel_tire2;
XPLMDataRef sim_op_fail_rel_tire3;
XPLMDataRef sim_op_fail_rel_tire4;
XPLMDataRef sim_op_fail_rel_tire5;
XPLMDataRef sim_op_fail_rel_tire6;
XPLMDataRef sim_op_fail_rel_tire7;
XPLMDataRef sim_op_fail_rel_tire8;
XPLMDataRef sim_op_fail_rel_tire9;
XPLMDataRef sim_op_fail_rel_tire10;



/*
 * Lights
 */

XPLMDataRef beacon_lights_on;
XPLMDataRef landing_lights_on;
XPLMDataRef nav_lights_on;
XPLMDataRef strobe_lights_on;
XPLMDataRef taxi_light_on;

/*
 * Instruments and cockpit lights
 */
XPLMDataRef cockpit_lights_on;                    // sim/cockpit/electrical/cockpit_lights_on [boolean]
XPLMDataRef cockpit_lights;                       // sim/cockpit/electrical/cockpit_lights [float 0 - 1 range]
XPLMDataRef cockpit_instrument_brightness;        // sim/cockpit/electrical/instrument_brightness (float 0 - 1 range)
XPLMDataRef cockpit_hud_brightness;               // sim/cockpit/electrical/HUD_brightness (float 0 - 1 range)
XPLMDataRef cockpit_instrument_brightness_ratio;  // sim/cockpit2/electrical/instrument_brightness_ratio_manual (array[16])
XPLMDataRef cockpit_panel_brightness_ratio;       // sim/cockpit2/electrical/instrument_brightness_ratio_manual (array[16])

/*
 * Ambient light
 */
XPLMDataRef sim_graphics_misc_cockpit_light_level_r;
XPLMDataRef sim_graphics_misc_cockpit_light_level_g;
XPLMDataRef sim_graphics_misc_cockpit_light_level_b;
XPLMDataRef sim_graphics_misc_outside_light_level_r;
XPLMDataRef sim_graphics_misc_outside_light_level_g;
XPLMDataRef sim_graphics_misc_outside_light_level_b;

// For the ECAM messages no smoking and belts + light switches commands
XPLMDataRef  landing_lights; 	// sim/cockpit2/switches/landing_lights_on boolean
XPLMDataRef  nav_lights;  		// sim/cockpit2/switches/navigation_lights_on boolean
XPLMDataRef  no_smoking; 	    // sim/cockpit2/switches/no_smoking integer (0=off, 1=auto, 2=on)
XPLMDataRef  fasten_seat_belts; // sim/cockpit2/switches/fasten_seat_belts integer (0=off, 1=auto, 2=on)
XPLMDataRef  strobe_lights; 	// sim/cockpit2/switches/strobe_light_on boolean
XPLMDataRef  taxi_lights; 	    // sim/cockpit2/switches/taxi_light_on boolean
XPLMDataRef  beacon_lights; 	// sim/cockpit2/switches/beacon_on boolean

XPLMDataRef pitot_heat_on;

XPLMDataRef nav1_freq_hz;
XPLMDataRef nav2_freq_hz;
XPLMDataRef adf1_freq_hz;
XPLMDataRef adf2_freq_hz;
XPLMDataRef nav1_dir_degt;
XPLMDataRef nav2_dir_degt;
XPLMDataRef adf1_dir_degt;
XPLMDataRef adf2_dir_degt;
XPLMDataRef nav1_dme_dist_m;
XPLMDataRef nav2_dme_dist_m;
XPLMDataRef adf1_dme_dist_m;
XPLMDataRef adf2_dme_dist_m;
XPLMDataRef nav1_obs_degm;
XPLMDataRef nav2_obs_degm;
XPLMDataRef nav1_course_degm;
XPLMDataRef nav2_course_degm;
XPLMDataRef nav1_cdi;
XPLMDataRef nav2_cdi;
XPLMDataRef nav1_hdef_dot;
XPLMDataRef nav2_hdef_dot;
XPLMDataRef nav1_fromto;
XPLMDataRef nav2_fromto;
XPLMDataRef nav1_vdef_dot;
XPLMDataRef nav2_vdef_dot;
XPLMDataRef gps_dir_degt;
XPLMDataRef gps_dme_dist_m;
XPLMDataRef gps_course_degtm;
XPLMDataRef gps_hdef_dot;
XPLMDataRef gps_fromto;
XPLMDataRef gps_vdef_dot;

XPLMDataRef nav1_dme_time_secs;
XPLMDataRef nav2_dme_time_secs;
XPLMDataRef gps_dme_time_secs;

XPLMDataRef gps_has_glideslope;

// XPLMDataRef nav1_dme_nm;
// XPLMDataRef nav2_dme_nm;
// XPLMDataRef gps_dme_nm;
XPLMDataRef hsi_dme_nm_pilot;
XPLMDataRef hsi_dme_nm_copilot;

XPLMDataRef outer_marker;
XPLMDataRef middle_marker;
XPLMDataRef inner_marker;

XPLMDataRef nav1_stdby_freq_hz;
XPLMDataRef nav2_stdby_freq_hz;
XPLMDataRef adf1_stdby_freq_hz;
XPLMDataRef adf2_stdby_freq_hz;

XPLMDataRef nav1_id;
XPLMDataRef nav2_id;
XPLMDataRef adf1_id;
XPLMDataRef adf2_id;
XPLMDataRef gps_id;

XPLMDataRef com1_freq_hz;
XPLMDataRef com1_stdby_freq_hz;
XPLMDataRef com2_freq_hz;
XPLMDataRef com2_stdby_freq_hz;

XPLMDataRef com1_frequency_hz_833;
XPLMDataRef com1_standby_frequency_hz_833;
XPLMDataRef com2_frequency_hz_833;
XPLMDataRef com2_standby_frequency_hz_833;

XPLMDataRef autopilot_state;
XPLMDataRef autopilot_vertical_velocity;
XPLMDataRef autopilot_altitude;
XPLMDataRef autopilot_approach_selector;
XPLMDataRef autopilot_heading_mag;
XPLMDataRef autopilot_airspeed;
XPLMDataRef autopilot_airspeed_is_mach;
XPLMDataRef autopilot_fd_pitch;
XPLMDataRef autopilot_fd_roll;
XPLMDataRef autopilot_mode;
XPLMDataRef autopilot_autothrottle_enabled;
XPLMDataRef autopilot_autothrottle_on;
XPLMDataRef autopilot_hdg_status;
XPLMDataRef autopilot_lnav_status;
XPLMDataRef autopilot_vs_status;
XPLMDataRef autopilot_spd_status;
XPLMDataRef autopilot_alt_hold_status;
XPLMDataRef autopilot_gs_status;
XPLMDataRef autopilot_vnav_status;
XPLMDataRef autopilot_toga_status;
XPLMDataRef autopilot_toga_lateral_status;
XPLMDataRef autopilot_roll_status;
XPLMDataRef autopilot_pitch_status;
XPLMDataRef autopilot_backcourse_status;
XPLMDataRef autopilot_heading_roll_mode;


XPLMDataRef transponder_mode;
XPLMDataRef transponder_code;
XPLMDataRef transponder_id;


XPLMDataRef efis_map_range_selector;
XPLMDataRef efis_dme_1_selector;
XPLMDataRef efis_dme_2_selector;
XPLMDataRef efis_shows_weather;
XPLMDataRef efis_shows_tcas;
XPLMDataRef efis_shows_airports;
XPLMDataRef efis_shows_waypoints;
XPLMDataRef efis_shows_vors;
XPLMDataRef efis_shows_ndbs;
XPLMDataRef efis_map_mode;
XPLMDataRef efis_map_submode;
XPLMDataRef hsi_selector;

XPLMDataRef wind_speed_kt;
XPLMDataRef wind_direction_degt;
XPLMDataRef sim_paused;
XPLMDataRef oat;
XPLMDataRef isa;
XPLMDataRef tat;
XPLMDataRef sound_speed;

// Clock and timers
XPLMDataRef zulu_time_sec;
XPLMDataRef local_time_sec;
XPLMDataRef timer_is_running;
XPLMDataRef elapsed_time_sec;
XPLMDataRef flight_time_sec;
XPLMDataRef clock_timer_mode;
XPLMDataRef clock_show_date;
XPLMDataRef clock_time_day;
XPLMDataRef clock_time_month;

XPLMDataRef acf_vso;
XPLMDataRef acf_vs;
XPLMDataRef acf_vfe;
XPLMDataRef acf_vno;
XPLMDataRef acf_vne;
XPLMDataRef acf_mmo;
XPLMDataRef acf_vle;
XPLMDataRef speedbrake_equiped;
XPLMDataRef retractable_gear;
XPLMDataRef acf_vmca;
XPLMDataRef acf_vyse;
XPLMDataRef acf_tailnum;


XPLMDataRef master_caution;
XPLMDataRef master_warning;
XPLMDataRef master_accept;
XPLMDataRef gear_handle;
XPLMDataRef gear_unsafe;
XPLMDataRef gear_types;
XPLMDataRef parkbrake_ratio;
XPLMDataRef flap_deploy;
XPLMDataRef flap_handle;
XPLMDataRef flap_detents;
XPLMDataRef ap_disc;
XPLMDataRef low_fuel;
XPLMDataRef gpws;
XPLMDataRef ice;
XPLMDataRef pitot_heat;
XPLMDataRef stall;
XPLMDataRef gear_warning;
XPLMDataRef auto_brake_level;
XPLMDataRef speedbrake_handle;
XPLMDataRef speedbrake_ratio;
XPLMDataRef gear_deploy;
XPLMDataRef gear_door_ang;
XPLMDataRef gear_door_type;
XPLMDataRef gear_door_ext_ang;
XPLMDataRef gear_door_ret_ang;
XPLMDataRef yoke_pitch_ratio;
XPLMDataRef yoke_roll_ratio;
XPLMDataRef yoke_hdg_ratio;
XPLMDataRef elevator_trim;
XPLMDataRef aileron_trim;
XPLMDataRef rudder_trim;
XPLMDataRef slat_deploy;
XPLMDataRef right_brake_ratio;
XPLMDataRef left_brake_ratio;

XPLMDataRef num_tanks;
XPLMDataRef num_engines;
XPLMDataRef reverser_deployed;
XPLMDataRef oil_pressure;
XPLMDataRef oil_temperature;
XPLMDataRef fuel_pressure;
XPLMDataRef total_fuel;
XPLMDataRef fuel_quantity;
XPLMDataRef fuel_capacity;
XPLMDataRef fuel_pumps;
XPLMDataRef m_total;
XPLMDataRef engine_n1;
XPLMDataRef engine_egt_percent;
XPLMDataRef engine_egt_value;
XPLMDataRef engine_max_egt_value;
XPLMDataRef engine_fire_extinguisher;
XPLMDataRef engine_fadec;
XPLMDataRef reverser_ratio;
XPLMDataRef tank_ratio;
XPLMDataRef engine_n2;
XPLMDataRef fuel_flow;
XPLMDataRef oil_p_ratio;
XPLMDataRef oil_p_psi;
XPLMDataRef oil_t_ratio;
XPLMDataRef oil_t_c;
XPLMDataRef oil_q_ratio;
XPLMDataRef oil_t_red;
XPLMDataRef oil_p_red;
XPLMDataRef throttle_ratio;
XPLMDataRef engine_epr_red;
XPLMDataRef ignition_key;

// for VIB
XPLMDataRef vib_running;
XPLMDataRef vib_n1_low;
XPLMDataRef vib_n1_high;
XPLMDataRef vib_reverse;
XPLMDataRef vib_chip;
XPLMDataRef vib_fire;
// Hydraulics
XPLMDataRef hyd_p_1;
XPLMDataRef hyd_p_2;
XPLMDataRef hyd_q_1;
XPLMDataRef hyd_q_2;
// TurboProp
XPLMDataRef engine_trq_max;
XPLMDataRef engine_trq;
XPLMDataRef engine_itt;
XPLMDataRef engine_itt_c;
XPLMDataRef prop_rpm_max;
XPLMDataRef prop_rpm;
XPLMDataRef prop_mode;
// Piston
XPLMDataRef piston_mpr;
// EPR
XPLMDataRef engine_epr;

// APU
XPLMDataRef apu_n1;
XPLMDataRef apu_gen_amp;
XPLMDataRef apu_running;
XPLMDataRef apu_gen_on;
XPLMDataRef apu_starter;


/*
 * Datarefs - futur use in MFD pages
 */
// Bleed Air
// XPLMDataRef  acf_full_bleed_air;  // sim/aircraft/overflow/acf_has_full_bleed_air
/* useless
XPLMDataRef  bleed_air_fail;  // sim/cockpit2/annunciators/bleed_air_fail [8]
XPLMDataRef  bleed_air_off;  // sim/cockpit2/annunciators/bleed_air_off [8]
*/

// Electrical
// Aircraft constants
XPLMDataRef acf_batteries; 	// sim/aircraft/electrical/num_batteries integer
XPLMDataRef acf_buses; 		// sim/aircraft/electrical/num_buses integer
XPLMDataRef acf_generators; // sim/aircraft/electrical/num_generators integer
XPLMDataRef acf_inverters; 	// sim/aircraft/electrical/num_inverters integer

XPLMDataRef avionics_on;
XPLMDataRef battery_on;
// Batteries
XPLMDataRef elec_battery_on; 				// sim/cockpit2/electrical/battery_on boolean [0/8]
XPLMDataRef elec_battery_amps; 				// sim/cockpit2/electrical/battery_amps float [0/8]
XPLMDataRef elec_voltage_actual_volts; 		// sim/cockpit2/electrical/battery_actual_volts float [0/8]
XPLMDataRef elec_voltage_indicated_volts; 	// sim/cockpit2/electrical/battery_indicated_volts float [0/8]
// Generator
XPLMDataRef elec_generator_on; 		// sim/cockpit2/electrical/generator_on boolean [0/8]
XPLMDataRef elec_generator_amps; 	// sim/cockpit2/electrical/generator_amps float [0/8]
// GPU
XPLMDataRef elec_gpu_on; 			// sim/cockpit2/electrical/generator_on boolean
XPLMDataRef elec_gpu_amps; 			// sim/cockpit2/electrical/generator_on boolean
// Inverters
XPLMDataRef elec_inverter_on; 		// sim/cockpit2/electrical/inverter_on boolean [0/2]
// Buses
XPLMDataRef elec_bus_load_amps; 	// sim/cockpit2/electrical/bus_load_amps float [0/6]
XPLMDataRef elec_bus_volts; 		// sim/cockpit2/electrical/bus_volts float [0/6]
// RAM air turbin
XPLMDataRef ram_air_turbin; 		// sim/cockpit2/switches/ram_air_turbin_on boolean



// Control surfaces position
XPLMDataRef  left_elevator_pos; 	// sim/flightmodel/controls/hstab1_elv1def float [-30 to +15]
XPLMDataRef  right_elevator_pos; 	// sim/flightmodel/controls/hstab2_elv1def float
XPLMDataRef  rudder_pos; 			// sim/flightmodel/controls/vstab1_rud1def float [-30 to +30°]
								// sim/flightmodel/controls/rdruddef [-30 to +30°]
								// sim/flightmodel/controls/ldruddef [-30 to +30°]

// 4 wings - aileron and spoilers
XPLMDataRef  left_wing_aileron_1_def[4];
XPLMDataRef  left_wing_aileron_2_def[4];
XPLMDataRef  left_wing_spoiler_1_def[4];
XPLMDataRef  left_wing_spoiler_2_def[4];
XPLMDataRef  right_wing_aileron_1_def[4];
XPLMDataRef  right_wing_aileron_2_def[4];
XPLMDataRef  right_wing_spoiler_1_def[4];
XPLMDataRef  right_wing_spoiler_2_def[4];

// Spoilers (1L to 4L and 1R to 4R)
XPLMDataRef  spoiler_3l;  		// sim/flightmodel/controls/wing3l_spo1def float [0 to +40°]
// Ailron (on airbus - ailrons_4l / cesna 172P ailron 1l)
XPLMDataRef  ailron_4l;  		// sim/flightmodel/controls/wing4l_ail1def float [-25 to +25°]

// Left and right ailerons
XPLMDataRef  right_aileron_pos; // -  sim/flightmodel/controls/rail1def [-30 to +30°]
XPLMDataRef  left_aileron_pos;  // -  sim/flightmodel/controls/lail1def [-30 to +30°]

// Control surfaces limits
XPLMDataRef acf_controls_elev_dn;
XPLMDataRef acf_controls_elev_up;
XPLMDataRef acf_controls_ail_dn;
XPLMDataRef acf_controls_ail_up;
XPLMDataRef acf_controls_rudder_lr;

// Pressurization
XPLMDataRef cabin_altitude;
XPLMDataRef cabin_vvi;
XPLMDataRef cabin_delta_p;
XPLMDataRef pressurization_alt_target;
XPLMDataRef pressurization_vvi_target;
XPLMDataRef pressurization_mode;
XPLMDataRef pressurization_dump_all;
XPLMDataRef pressurization_dump_to_alt;
XPLMDataRef pressurization_max_alt;

// Nose Wheel Steering
XPLMDataRef nose_wheel_steer_on;
XPLMDataRef tailwheel_lock_ratio;
XPLMDataRef tire_steer_command_deg;
XPLMDataRef tire_steer_actual_deg;
XPLMDataRef acf_gear_steers;
XPLMDataRef nose_wheel_steer;
XPLMDataRef tire_steer_cmd;
XPLMDataRef tire_steer_act;

/*
 * END of datarefs not yet exported
 */


//// TCAS
//XPLMDataRef relative_bearing_degs;
//XPLMDataRef relative_distance_mtrs;
//XPLMDataRef relative_altitude_mtrs;

// Multiplayer
XPLMDataRef multiplayer_x[NUM_TCAS];
XPLMDataRef multiplayer_y[NUM_TCAS];
XPLMDataRef multiplayer_z[NUM_TCAS];


// for the NAV-Sync button commands (Direct-to-VOR & Sync-LOC/ILS)
XPLMDataRef nav_type_;



// custom datarefs

// xhsi/style
int instrument_style;
int     getStyle(void* inRefcon)
{
     return instrument_style;
}
void	setStyle(void* inRefcon, int inValue)
{
      instrument_style = inValue;
}


// xhsi/rwy_length_min
int min_rwy_length;
int     getMinRwyLen(void* inRefcon)
{
     return min_rwy_length;
}
void	setMinRwyLen(void* inRefcon, int inValue)
{
      min_rwy_length = inValue;
}


// xhsi/rwy_units
int rwy_units;
int     getRwyUnits(void* inRefcon)
{
     return rwy_units;
}
void	setRwyUnits(void* inRefcon, int inValue)
{
      rwy_units = inValue;
}


// xhsi/eicas/engine_type
int eicas_engine_type;
int     getEICASMode(void* inRefcon)
{
     return eicas_engine_type;
}
void	setEICASMode(void* inRefcon, int inValue)
{
      eicas_engine_type = inValue;
}


// xhsi/eicas/trq_scale
int eicas_trq_scale;
int     getTRQscale(void* inRefcon)
{
     return eicas_trq_scale;
}
void	setTRQscale(void* inRefcon, int inValue)
{
      eicas_trq_scale = inValue;
}


// xhsi/eicas/trq_max_lbft
float eicas_trq_max;
float     getOverrideTRQmax(void* inRefcon)
{
     return eicas_trq_max;
}
void	setOverrideTRQmax(void* inRefcon, float inValue)
{
      eicas_trq_max = inValue;
}

// Flight Warning Computer - Flight Phase
// Read only
int fwc_flight_phase;
int getFWCFlightPhase(void* inRefcon)
{
     return fwc_flight_phase;
}
void setFWCFlightPhase(void* inRefcon, int inValue)
{
	fwc_flight_phase = inValue;
}

// xhsi/eicas/fuel_units
int eicas_fuel_units;
int     getFuelUnits(void* inRefcon)
{
     return eicas_fuel_units;
}
void	setFuelUnits(void* inRefcon, int inValue)
{
      eicas_fuel_units = inValue;
}


// xhsi/eicas/temp_units
int eicas_temp_units;
int     getTempUnits(void* inRefcon)
{
     return eicas_temp_units;
}
void	setTempUnits(void* inRefcon, int inValue)
{
      eicas_temp_units = inValue;
}


// xhsi/mfd/mode
int mfd_display_mode;
int     getMFDMode(void* inRefcon)
{
     return mfd_display_mode;
}
void	setMFDMode(void* inRefcon, int inValue)
{
      mfd_display_mode = inValue;
}

// xhsi/mfd/fuel_used
//XPLM_API int                  XPLMGetDatavf(
//                                   XPLMDataRef          inDataRef,
//                                   float *              outValues,    /* Can be NULL */
//                                   int                  inOffset,
//                                   int                  inMax);
//typedef int (* XPLMGetDatavf_f)(
//                                   void *               inRefcon,
//                                   float *              outValues,    /* Can be NULL */
//                                   int                  inOffset,
//                                   int                  inMax);
float fuel_used[8];
int  getFuelUsed(
        void *               inRefcon,
        float *              outValues,    /* Can be NULL */
        int                  inOffset,
        int                  inMax)
{
	int i;
	int retMax = inMax;
	if ((inOffset + inMax) > 7) retMax = 8 - inOffset;
	if (outValues == NULL || inMax == 0) {
		return 8;
	} else {
		for (i=0; i<retMax; i++) {
			outValues[i] = fuel_used[i+inOffset];
		}
	}
    return retMax;
}
void	setFuelUsed(
		void *               inRefcon,
        float *              inValues,
        int                  inOffset,
        int                  inCount)
{
	int i;
	int inMax = inCount;
	if ((inOffset + inMax) > 7) inMax = 8 - inOffset;
	if (inValues == NULL || inCount == 0) {
		return;
	} else {
		for (i=0; i<inMax; i++) {
			fuel_used[i+inOffset] = inValues[i];
		}
	}
    return;
}



// xhsi/mfd/crew_oxy_psi
float crew_oxy_psi;
float getCrewOxyPsi(void *inRefcon)
{
    return crew_oxy_psi;
}
void	setCrewOxyPsi(void* inRefcon, float inValue)
{
	crew_oxy_psi = inValue;
}


// xhsi/cdu_pilot/source
int cdu_pilot_source_value;
int     getPilotCDUSource(void* inRefcon)
{
     return cdu_pilot_source_value;
}
void	setPilotCDUSource(void* inRefcon, int inValue)
{
      cdu_pilot_source_value = inValue;
}

// xhsi/cdu_copilot/source
int cdu_copilot_source_value;
int     getCopilotCDUSource(void* inRefcon)
{
     return cdu_copilot_source_value;
}
void	setCopilotCDUSource(void* inRefcon, int inValue)
{
      cdu_copilot_source_value = inValue;
}

// xhsi/cdu_pilot/side
int cdu_pilot_side_value;
int     getPilotCDUSide(void* inRefcon)
{
     return cdu_pilot_side_value;
}
void	setPilotCDUSide(void* inRefcon, int inValue)
{
	cdu_pilot_side_value = inValue;
}

// xhsi/cdu_copilot/side
int cdu_copilot_side_value;
int     getCopilotCDUSide(void* inRefcon)
{
     return cdu_copilot_side_value;
}
void	setCopilotCDUSide(void* inRefcon, int inValue)
{
	cdu_copilot_side_value = inValue;
}

// custom datarefs for pilot

// xhsi/nd_pilot/sta
int pilot_sta;
int     getPilotSTA(void* inRefcon)
{
     return pilot_sta;
}
void	setPilotSTA(void* inRefcon, int inValue)
{
      pilot_sta = inValue;
}

// xhsi/nd_pilot/data
int pilot_data;
int     getPilotDATA(void* inRefcon)
{
     return pilot_data;
}
void	setPilotDATA(void* inRefcon, int inValue)
{
      pilot_data = inValue;
}

// xhsi/nd_pilot/pos
int pilot_pos;
int     getPilotPOS(void* inRefcon)
{
     return pilot_pos;
}
void	setPilotPOS(void* inRefcon, int inValue)
{
      pilot_pos = inValue;
}

// xhsi/nd_pilot/terrain
int pilot_terrain;
int     getPilotTerrain(void* inRefcon)
{
     return pilot_terrain;
}
void	setPilotTerrain(void* inRefcon, int inValue)
{
      pilot_terrain = inValue;
}

// xhsi/nd_pilot/vp
int pilot_vp;
int getPilotVerticalPath(void* inRefcon)
{
     return pilot_vp;
}
void setPilotVerticalPath(void* inRefcon, int inValue)
{
	pilot_vp = inValue;
}

// xhsi/nd_pilot/ils
int pilot_ils;
int getPilotILS(void* inRefcon)
{
     return pilot_ils;
}
void setPilotILS(void* inRefcon, int inValue)
{
	pilot_ils = inValue;
}

// xhsi/nd_pilot/metric_alt
int pilot_metric_alt;
int getPilotMetricAlt(void* inRefcon)
{
     return pilot_metric_alt;
}
void setPilotMetricAlt(void* inRefcon, int inValue)
{
	pilot_metric_alt = inValue;
	/* may be a very bad idea - prefer writedataref
	if (qpac_ready) XPLMSetDatai(qpac_fcu_metric_alt, inValue);
	if (jar_a320_neo_ready) XPLMSetDatai(jar_a320_neo_fcu_metric_alt, inValue);
	*/
}

// xhsi/nd_pilot/track_fpa
int pilot_track_fpa;
int getPilotTrackFPA(void* inRefcon)
{
     return pilot_track_fpa;
}
void setPilotTrackFPA(void* inRefcon, int inValue)
{
	pilot_track_fpa = inValue;
	/* may be a very bad idea - prefer writeDataRef XHSI_EFIS_PILOT_TRK_FPA
	if (qpac_ready) XPLMSetDatai(qpac_fcu_hdg_trk, inValue);
	if (jar_a320_neo_ready) XPLMSetDatai(jar_a320_neo_fcu_hdg_trk, inValue);
	*/
}

// xhsi/pfd_pilot/da_bug
int pilot_da_bug;
int     getPilotDAbug(void* inRefcon)
{
     return pilot_da_bug;
}
void	setPilotDAbug(void* inRefcon, int inValue)
{
      pilot_da_bug = inValue;
}

// xhsi/pfd_pilot/mins_mode
int pilot_mins_mode;
int     getPilotMinsMode(void* inRefcon)
{
     return pilot_mins_mode;
}
void	setPilotMinsMode(void* inRefcon, int inValue)
{
      pilot_mins_mode = inValue;
}

// xhsi/nd_pilot/map_zoomin
int pilot_map_zoomin;
int     getPilotMapRange100(void* inRefcon)
{
     return pilot_map_zoomin;
}
void	setPilotMapRange100(void* inRefcon, int inValue)
{
      pilot_map_zoomin = inValue;
}

// xhsi/nd_pilot/chrono
float pilot_chrono;
float    getPilotChrono(void* inRefcon)
{
    return pilot_chrono;
}
void	setPilotChrono(void* inRefcon, float inValue)
{
	pilot_chrono = inValue;
}

// xhsi/nd_pilot/chrono_run
int pilot_chrono_run; // Boolean
int    getPilotChronoRun(void* inRefcon)
{
    return pilot_chrono_run;
}
void	setPilotChronoRun(void* inRefcon, int inValue)
{
	pilot_chrono_run = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_gain
float pilot_wxr_gain; // Boolean
float getPilotWxrGain(void* inRefcon)
{
    return pilot_wxr_gain;
}
void setPilotWxrGain(void* inRefcon, float inValue)
{
	pilot_wxr_gain = inValue;
}

// xhsi/nd_pilot/wxr_tilt
float pilot_wxr_tilt; // Boolean
float getPilotWxrTilt(void* inRefcon)
{
    return pilot_wxr_tilt;
}
void setPilotWxrTilt(void* inRefcon, float inValue)
{
	pilot_wxr_tilt = inValue;
}

// xhsi/nd_pilot/wxr_auto_tilt
int pilot_wxr_auto_tilt; // Boolean
int    getPilotWxrAutoTilt(void* inRefcon)
{
    return pilot_wxr_auto_tilt;
}
void	setPilotWxrAutoTilt(void* inRefcon, int inValue)
{
	pilot_wxr_auto_tilt = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_auto_gain
int pilot_wxr_auto_gain; // Boolean
int    getPilotWxrAutoGain(void* inRefcon)
{
    return pilot_wxr_auto_gain;
}
void	setPilotWxrAutoGain(void* inRefcon, int inValue)
{
	pilot_wxr_auto_gain = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_test
int pilot_wxr_test; // Boolean
int    getPilotWxrTest(void* inRefcon)
{
    return pilot_wxr_test;
}
void	setPilotWxrTest(void* inRefcon, int inValue)
{
	pilot_wxr_test = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_mode
int pilot_wxr_mode; // Integer 0 to 4
int    getPilotWxrMode(void* inRefcon)
{
    return pilot_wxr_mode;
}
void	setPilotWxrMode(void* inRefcon, int inValue)
{
	pilot_wxr_mode = (inValue < 0 || inValue > 4) ? 0 : inValue;
}

// xhsi/nd_pilot/wxr_slave
int pilot_wxr_slave; // Boolean
int    getPilotWxrSlave(void* inRefcon)
{
    return pilot_wxr_slave;
}
void	setPilotWxrSlave(void* inRefcon, int inValue)
{
	pilot_wxr_slave = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_react
int pilot_wxr_react; // Boolean
int    getPilotWxrReact(void* inRefcon)
{
    return pilot_wxr_react;
}
void	setPilotWxrReact(void* inRefcon, int inValue)
{
	pilot_wxr_react = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_narrow
int pilot_wxr_narrow; // Boolean
int    getPilotWxrNarrow(void* inRefcon)
{
    return pilot_wxr_narrow;
}
void	setPilotWxrNarrow(void* inRefcon, int inValue)
{
	pilot_wxr_narrow = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_alert
int pilot_wxr_alert; // Boolean
int    getPilotWxrAlert(void* inRefcon)
{
    return pilot_wxr_alert;
}
void	setPilotWxrAlert(void* inRefcon, int inValue)
{
	pilot_wxr_alert = inValue != 0 ? 1 : 0;
}

// xhsi/nd_pilot/wxr_target
int pilot_wxr_target; // Boolean
int    getPilotWxrTarget(void* inRefcon)
{
    return pilot_wxr_target;
}
void	setPilotWxrTarget(void* inRefcon, int inValue)
{
	pilot_wxr_target = inValue != 0 ? 1 : 0;
}


// custom datarefs for copilot

// xhsi/nd_copilot/map_range
int copilot_map_range;
int     getCopilotMapRange(void* inRefcon)
{
     return copilot_map_range;
}
void	setCopilotMapRange(void* inRefcon, int inValue)
{
      copilot_map_range = inValue;
}

// xhsi/nd_copilot/radio1
int copilot_radio1;
int     getCopilotRadio1(void* inRefcon)
{
     return copilot_radio1;
}
void	setCopilotRadio1(void* inRefcon, int inValue)
{
      copilot_radio1 = inValue;
}

// xhsi/nd_copilot/radio2
int copilot_radio2;
int     getCopilotRadio2(void* inRefcon)
{
     return copilot_radio2;
}
void	setCopilotRadio2(void* inRefcon, int inValue)
{
      copilot_radio2 = inValue;
}

// xhsi/nd_copilot/tfc
int copilot_tfc;
int     getCopilotTFC(void* inRefcon)
{
     return copilot_tfc;
}
void	setCopilotTFC(void* inRefcon, int inValue)
{
      copilot_tfc = inValue;
}

// xhsi/nd_copilot/arpt
int copilot_arpt;
int     getCopilotARPT(void* inRefcon)
{
     return copilot_arpt;
}
void	setCopilotARPT(void* inRefcon, int inValue)
{
      copilot_arpt = inValue;
}

// xhsi/nd_copilot/wpt
int copilot_wpt;
int     getCopilotWPT(void* inRefcon)
{
     return copilot_wpt;
}
void	setCopilotWPT(void* inRefcon, int inValue)
{
      copilot_wpt = inValue;
}

// xhsi/nd_copilot/vor
int copilot_vor;
int     getCopilotVOR(void* inRefcon)
{
     return copilot_vor;
}
void	setCopilotVOR(void* inRefcon, int inValue)
{
      copilot_vor = inValue;
}

// xhsi/nd_copilot/ndb
int copilot_ndb;
int     getCopilotNDB(void* inRefcon)
{
     return copilot_ndb;
}
void	setCopilotNDB(void* inRefcon, int inValue)
{
      copilot_ndb = inValue;
}

// xhsi/nd_copilot/sta
int copilot_sta;
int     getCopilotSTA(void* inRefcon)
{
     return copilot_sta;
}
void	setCopilotSTA(void* inRefcon, int inValue)
{
      copilot_sta = inValue;
}

// xhsi/nd_copilot/data
int copilot_data;
int     getCopilotDATA(void* inRefcon)
{
     return copilot_data;
}
void	setCopilotDATA(void* inRefcon, int inValue)
{
      copilot_data = inValue;
}

// xhsi/nd_copilot/pos
int copilot_pos;
int     getCopilotPOS(void* inRefcon)
{
     return copilot_pos;
}
void	setCopilotPOS(void* inRefcon, int inValue)
{
      copilot_pos = inValue;
}

// xhsi/nd_copilot/terrain
int copilot_terrain;
int     getCopilotTerrain(void* inRefcon)
{
     return copilot_terrain;
}
void	setCopilotTerrain(void* inRefcon, int inValue)
{
      copilot_terrain = inValue;
}

// xhsi/nd_copilot/vp
int copilot_vp;
int     getCopilotVerticalPath(void* inRefcon)
{
     return copilot_vp;
}
void	setCopilotVerticalPath(void* inRefcon, int inValue)
{
	copilot_vp = inValue;
}

// xhsi/nd_copilot/ils
int copilot_ils;
int getCopilotILS(void* inRefcon)
{
     return copilot_ils;
}
void setCopilotILS(void* inRefcon, int inValue)
{
	copilot_ils = inValue;
}

// xhsi/nd_copilot/metric_alt
int copilot_metric_alt;
int getCopilotMetricAlt(void* inRefcon)
{
     return copilot_metric_alt;
}
void setCopilotMetricAlt(void* inRefcon, int inValue)
{
	copilot_metric_alt = inValue;
}

// xhsi/nd_copilot/track_fpa
int copilot_track_fpa;
int getCopilotTrackFPA(void* inRefcon)
{
     return copilot_track_fpa;
}
void setCopilotTrackFPA(void* inRefcon, int inValue)
{
	copilot_track_fpa = inValue;
}

// xhsi/nd_copilot/map_ctr
int copilot_map_ctr;
int     getCopilotMapCTR(void* inRefcon)
{
     return copilot_map_ctr;
}
void	setCopilotMapCTR(void* inRefcon, int inValue)
{
      copilot_map_ctr = inValue;
}

// xhsi/nd_copilot/map_mode
int copilot_map_mode;
int     getCopilotMapMode(void* inRefcon)
{
     return copilot_map_mode;
}
void	setCopilotMapMode(void* inRefcon, int inValue)
{
      copilot_map_mode = inValue;
}

// xhsi/nd_copilot/nav_source
int copilot_hsi_source;
int     getCopilotHSISource(void* inRefcon)
{
     return copilot_hsi_source;
}
void	setCopilotHSISource(void* inRefcon, int inValue)
{
      copilot_hsi_source = inValue;
}

// xhsi/pfd_copilot/da_bug
int copilot_da_bug;
int     getCopilotDAbug(void* inRefcon)
{
     return copilot_da_bug;
}
void	setCopilotDAbug(void* inRefcon, int inValue)
{
      copilot_da_bug = inValue;
}

// xhsi/pfd_copilot/mins_mode
int copilot_mins_mode;
int     getCopilotMinsMode(void* inRefcon)
{
     return copilot_mins_mode;
}
void	setCopilotMinsMode(void* inRefcon, int inValue)
{
      copilot_mins_mode = inValue;
}

// xhsi/nd_copilot/map_zoomin
int copilot_map_zoomin;
int     getCopilotMapRange100(void* inRefcon)
{
     return copilot_map_zoomin;
}
void	setCopilotMapRange100(void* inRefcon, int inValue)
{
      copilot_map_zoomin = inValue;
}

// xhsi/nd_copilot/chrono
float copilot_chrono;
float    getCopilotChrono(void* inRefcon)
{
    return copilot_chrono;
}
void	setCopilotChrono(void* inRefcon, float inValue)
{
	copilot_chrono = inValue;
}

// xhsi/nd_copilot/chrono_run
int copilot_chrono_run; // Boolean
int    getCopilotChronoRun(void* inRefcon)
{
    return copilot_chrono_run;
}
void	setCopilotChronoRun(void* inRefcon, int inValue)
{
	copilot_chrono_run = inValue != 0 ? 1 : 0;
}


// xhsi/nd_copilot/wxr_gain
float copilot_wxr_gain; // Boolean
float getCopilotWxrGain(void* inRefcon)
{
    return copilot_wxr_gain;
}
void setCopilotWxrGain(void* inRefcon, float inValue)
{
	copilot_wxr_gain = inValue;
}

// xhsi/nd_copilot/wxr_tilt
float copilot_wxr_tilt; // Boolean
float getCopilotWxrTilt(void* inRefcon)
{
    return copilot_wxr_tilt;
}
void setCopilotWxrTilt(void* inRefcon, float inValue)
{
	copilot_wxr_tilt = inValue;
}

// xhsi/nd_copilot/wxr_auto_tilt
int copilot_wxr_auto_tilt; // Boolean
int getCopilotWxrAutoTilt(void* inRefcon)
{
    return copilot_wxr_auto_tilt;
}
void setCopilotWxrAutoTilt(void* inRefcon, int inValue)
{
	copilot_wxr_auto_tilt = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_auto_gain
int copilot_wxr_auto_gain; // Boolean
int getCopilotWxrAutoGain(void* inRefcon)
{
    return copilot_wxr_auto_gain;
}
void setCopilotWxrAutoGain(void* inRefcon, int inValue)
{
	copilot_wxr_auto_gain = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_test
int copilot_wxr_test; // Boolean
int getCopilotWxrTest(void* inRefcon)
{
    return copilot_wxr_test;
}
void setCopilotWxrTest(void* inRefcon, int inValue)
{
	copilot_wxr_test = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_mode
int copilot_wxr_mode; // Integer 0 to 4
int getCopilotWxrMode(void* inRefcon)
{
    return copilot_wxr_mode;
}
void setCopilotWxrMode(void* inRefcon, int inValue)
{
	copilot_wxr_mode = (inValue < 0 || inValue > 4) ? 0 : inValue;
}

// xhsi/nd_copilot/wxr_slave
int copilot_wxr_slave; // Boolean
int getCopilotWxrSlave(void* inRefcon)
{
    return copilot_wxr_slave;
}
void setCopilotWxrSlave(void* inRefcon, int inValue)
{
	copilot_wxr_slave = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_react
int copilot_wxr_react; // Boolean
int getCopilotWxrReact(void* inRefcon)
{
    return copilot_wxr_react;
}
void setCopilotWxrReact(void* inRefcon, int inValue)
{
	copilot_wxr_react = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_narrow
int copilot_wxr_narrow; // Boolean
int getCopilotWxrNarrow(void* inRefcon)
{
    return copilot_wxr_narrow;
}
void setCopilotWxrNarrow(void* inRefcon, int inValue)
{
	copilot_wxr_narrow = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_alert
int copilot_wxr_alert; // Boolean
int getCopilotWxrAlert(void* inRefcon)
{
    return copilot_wxr_alert;
}
void setCopilotWxrAlert(void* inRefcon, int inValue)
{
	copilot_wxr_alert = inValue != 0 ? 1 : 0;
}

// xhsi/nd_copilot/wxr_target
int copilot_wxr_target; // Boolean
int getCopilotWxrTarget(void* inRefcon)
{
    return copilot_wxr_target;
}
void setCopilotWxrTarget(void* inRefcon, int inValue)
{
	copilot_wxr_target = inValue != 0 ? 1 : 0;
}



//// xhsi/rtu/contact_atc
//int contact_atc;
//int     getContactATC(void* inRefcon)
//{
//     return contact_atc;
//}
//void	setContactATC(void* inRefcon, int inValue)
//{
//      contact_atc = inValue;
//}

// xhsi/rtu/selected_radio
int selected_radio;
int     getSelectedRadio(void* inRefcon)
{
     return selected_radio;
}
void	setSelectedRadio(void* inRefcon, int inValue)
{
      selected_radio = inValue;
}



// xhsi/clock/utc_selector (0=GPS, 1=INT, 2=SET)
int utc_selector;
int     getClockUTCSource(void* inRefcon)
{
    return utc_selector;
}
void	setClockUTCSource(void* inRefcon, int inValue)
{
	utc_selector = inValue;
}

// xhsi/clock/et_running (1=Running, 0=STOP, 2=RESET)
int et_running;
float et_frozen_time=0.0;
int     getClockETRunning(void* inRefcon)
{
    return et_running;
}
void	setClockETRunning(void* inRefcon, int inValue)
{
	if (inValue!=0 && inValue!=1) {
		// Reset Elapsed Flight Time and back to STOP
		XPLMSetDataf(flight_time_sec,0.0);
		et_running = 0;
		et_frozen_time = 0.0;
	} else {
		if (et_running==0 && inValue==1) {
			// Running position from stop or reset position
			// Restore frozen ET time
			XPLMSetDataf(flight_time_sec,et_frozen_time);
		}
		if (et_running==1 && inValue==0) {
			// Freeze time
			et_frozen_time = XPLMGetDataf(flight_time_sec);
		}
		et_running = inValue;
	}
}
// xhsi/clock/et_frozen_time
float   getClockETFrozenTime(void* inRefcon)
{
    return et_frozen_time;
}

// xhsi/clock/show_date
int show_date;
int     getClockShowDate(void* inRefcon)
{
	return show_date;
}
void	setClockShowDate(void* inRefcon, int inValue)
{
	show_date = inValue;
}


// EGPWS datarefs

// xhsi/egpws/flaps_mode
int epgws_flaps; // Boolean
int getEgpwsFlapsMode(void* inRefcon)
{
    return epgws_flaps;
}
void setEgpwsFlapsMode(void* inRefcon, int inValue)
{
	epgws_flaps = inValue != 0 ? 1 : 0;
}

// xhsi/egpws/gs_mode
int epgws_glide_slope; // Boolean
int getEgpwsGsMode(void* inRefcon)
{
    return epgws_glide_slope;
}
void setEgpwsGsMode(void* inRefcon, int inValue)
{
	epgws_glide_slope = inValue != 0 ? 1 : 0;
}

// xhsi/egpws/system
int epgws_system; // Boolean
int getEgpwsSystem(void* inRefcon)
{
    return epgws_system;
}
void setEgpwsSystem(void* inRefcon, int inValue)
{
	epgws_system = inValue != 0 ? 1 : 0;
}

float brightness[16];
#define BRIGHTNESS_PFD_CPT 0
#define BRIGHTNESS_ND_CPT 1
#define BRIGHTNESS_PFD_FO 2
#define BRIGHTNESS_ND_FO 3
#define BRIGHTNESS_EICAS 4
#define BRIGHTNESS_MFD 5
#define BRIGHTNESS_CLOCK 6
#define BRIGHTNESS_CDU_CPT 7
#define BRIGHTNESS_CDU_FO 8
#define BRIGHTNESS_CDU_OBS 9
#define BRIGHTNESS_FCU 10
#define BRIGHTNESS_OHP 11
#define BRIGHTNESS_PEDESTAL 12

float getBrightnessPFDCaptain(void* inRefcon) { return brightness[BRIGHTNESS_PFD_CPT]; }
float getBrightnessNDCaptain(void* inRefcon) { return brightness[BRIGHTNESS_ND_CPT]; }
float getBrightnessPFDCopilot(void* inRefcon) { return brightness[BRIGHTNESS_PFD_FO]; }
float getBrightnessNDCopilot(void* inRefcon) { return brightness[BRIGHTNESS_ND_FO]; }
float getBrightnessEICAS(void* inRefcon) { return brightness[BRIGHTNESS_EICAS]; }
float getBrightnessMFD(void* inRefcon) { return brightness[BRIGHTNESS_MFD]; }
float getBrightnessClock(void* inRefcon) { return brightness[BRIGHTNESS_CLOCK]; }
float getBrightnessCDUCaptain(void* inRefcon) { return brightness[BRIGHTNESS_CDU_CPT]; }
float getBrightnessCDUCopilot(void* inRefcon) { return brightness[BRIGHTNESS_CDU_FO]; }
float getBrightnessCDUObserver(void* inRefcon) { return brightness[BRIGHTNESS_CDU_OBS]; }
float getBrightnessFCU(void* inRefcon) { return brightness[BRIGHTNESS_FCU]; }
float getBrightnessOHP(void* inRefcon) { return brightness[BRIGHTNESS_OHP]; }
float getBrightnessPedestal(void* inRefcon) { return brightness[BRIGHTNESS_PEDESTAL]; }

void setBrightnessPFDCaptain(void* inRefcon, float inValue) { brightness[BRIGHTNESS_PFD_CPT] = inValue; }
void setBrightnessNDCaptain(void* inRefcon, float inValue) { brightness[BRIGHTNESS_ND_CPT] = inValue; }
void setBrightnessPFDCopilot(void* inRefcon, float inValue) { brightness[BRIGHTNESS_PFD_FO] = inValue; }
void setBrightnessNDCopilot(void* inRefcon, float inValue) { brightness[BRIGHTNESS_ND_FO] = inValue; }
void setBrightnessEICAS(void* inRefcon, float inValue) { brightness[BRIGHTNESS_EICAS] = inValue; }
void setBrightnessMFD(void* inRefcon, float inValue) { brightness[BRIGHTNESS_MFD] = inValue; }
void setBrightnessClock(void* inRefcon, float inValue) { brightness[BRIGHTNESS_CLOCK] = inValue; }
void setBrightnessCDUCaptain(void* inRefcon, float inValue) { brightness[BRIGHTNESS_CDU_CPT] = inValue; }
void setBrightnessCDUCopilot(void* inRefcon, float inValue) { brightness[BRIGHTNESS_CDU_FO] = inValue; }
void setBrightnessCDUObserver(void* inRefcon, float inValue) { brightness[BRIGHTNESS_CDU_OBS] = inValue; }
void setBrightnessFCU(void* inRefcon, float inValue) { brightness[BRIGHTNESS_FCU] = inValue; }
void setBrightnessOHP(void* inRefcon, float inValue) { brightness[BRIGHTNESS_OHP] = inValue; }
void setBrightnessPedestal(void* inRefcon, float inValue) {	brightness[BRIGHTNESS_PEDESTAL] = inValue; }



void registerPilotDataRefs(void) {

    XPLMDebugString("XHSI: registering custom pilot DataRefs\n");

    // xhsi/nd_pilot/brightness
    efis_pilot_nd_brightness = XPLMRegisterDataAccessor("xhsi/nd_pilot/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessNDCaptain, setBrightnessNDCaptain, // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/pfd_pilot/brightness
    efis_pilot_pfd_brightness = XPLMRegisterDataAccessor("xhsi/pfd_pilot/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessPFDCaptain, setBrightnessPFDCaptain, // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/nd_pilot/sta
    efis_pilot_shows_stas = XPLMRegisterDataAccessor("xhsi/nd_pilot/sta",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotSTA, setPilotSTA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_pilot/data
    efis_pilot_shows_data = XPLMRegisterDataAccessor("xhsi/nd_pilot/data",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotDATA, setPilotDATA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_pilot/pos
    efis_pilot_shows_pos = XPLMRegisterDataAccessor("xhsi/nd_pilot/pos",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotPOS, setPilotPOS,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/terrain
    efis_pilot_shows_terrain = XPLMRegisterDataAccessor("xhsi/nd_pilot/terrain",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotTerrain, setPilotTerrain,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/vp
    efis_pilot_shows_vp = XPLMRegisterDataAccessor("xhsi/nd_pilot/vp",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotVerticalPath, setPilotVerticalPath,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/ils
    efis_pilot_shows_ils = XPLMRegisterDataAccessor("xhsi/nd_pilot/ils",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotILS, setPilotILS,                        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/vp
    efis_pilot_metric_alt = XPLMRegisterDataAccessor("xhsi/nd_pilot/metric_alt",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotMetricAlt, setPilotMetricAlt,           // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/track_fpa
    efis_pilot_track_fpa = XPLMRegisterDataAccessor("xhsi/nd_pilot/track_fpa",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotTrackFPA, setPilotTrackFPA,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/pfd_pilot/da_bug
    efis_pilot_da_bug = XPLMRegisterDataAccessor("xhsi/pfd_pilot/da_bug",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotDAbug, setPilotDAbug,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/pfd_pilot/mins_mode
    efis_pilot_mins_mode = XPLMRegisterDataAccessor("xhsi/pfd_pilot/mins_mode",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotMinsMode, setPilotMinsMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_pilot/map_zoomin
    efis_pilot_map_zoomin = XPLMRegisterDataAccessor("xhsi/nd_pilot/map_zoomin",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotMapRange100, setPilotMapRange100,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/chrono_elapsed
    efis_pilot_chrono = XPLMRegisterDataAccessor("xhsi/nd_pilot/chrono_elapsed",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getPilotChrono, setPilotChrono,                  // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/chrono_run
    efis_pilot_chrono_running = XPLMRegisterDataAccessor("xhsi/nd_pilot/chrono_running",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotChronoRun, setPilotChronoRun,            // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_gain
    // Weather radar gain
    // float between 0 to 1, default 0
    efis_pilot_wxr_gain = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_gain",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getPilotWxrGain, setPilotWxrGain,                // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_tilt
    // Weather radar antena tilt in degrees (-15 to +15)
    // float between -15 to 15, default 0
    efis_pilot_wxr_tilt = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_tilt",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getPilotWxrTilt, setPilotWxrTilt,                // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_auto_tilt
    // Weather radar antena auto tilt system (or stabilizer)
    // boolean, default true (1)
    efis_pilot_wxr_auto_tilt = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_auto_tilt",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrAutoTilt, setPilotWxrAutoTilt,        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_auto_gain
    // Weather radar automatic calibrated gain
    // boolean, default true (1)
    efis_pilot_wxr_auto_gain = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_auto_gain",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrAutoGain, setPilotWxrAutoGain,        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_test
    // Weather radar test mode
    // boolean, default false (0)
    efis_pilot_wxr_test = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_test",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrTest, setPilotWxrTest,                // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_mode
    // Weather radar mode
    // 0=OFF, 1=Weather, 2=Weather+Turb, 3=Map
    // default 1=Weather
    efis_pilot_wxr_mode = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_mode",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrMode, setPilotWxrMode,                // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_slave
    // Weather radar slave :
    // when true, get the settings from the opposite side
    // default false (0)
    efis_pilot_wxr_slave = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_slave",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrSlave, setPilotWxrSlave,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_react
    // Weather radar REACT
    // when false, signal attenuation is simulated
    // default true (1)
    efis_pilot_wxr_react = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_react",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrReact, setPilotWxrReact,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_narrow
    // Weather radar narrow beam
    // when false - beam is at standard sweep of 120° (depends on the sweep angle settings)
    // when true - beam is at 60° and gives a faster sweep rate
    // default false (0)
    efis_pilot_wxr_narrow = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_narrow",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrNarrow, setPilotWxrNarrow,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/wxr_alert
    // Weather radar alert mode on
    // when true, high turbulence areas flashes
    // default false (0)
    efis_pilot_wxr_alert = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_alert",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrAlert, setPilotWxrAlert,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/nd_pilot/wxr_target
    // Weather radar target mode on
    // when true, alarm visible when high turbulence within 25 nm range
    // default false (0)
    efis_pilot_wxr_target = XPLMRegisterDataAccessor("xhsi/nd_pilot/wxr_target",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getPilotWxrTarget, setPilotWxrTarget,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    XPLMDebugString("XHSI: custom pilot DataRefs registered\n");

}


void registerCopilotDataRefs(void) {

    XPLMDebugString("XHSI: registering custom copilot DataRefs\n");

    // xhsi/nd_copilot/brightness
    efis_copilot_nd_brightness = XPLMRegisterDataAccessor("xhsi/nd_copilot/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessNDCopilot, setBrightnessNDCopilot, // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/pfd_copilot/brightness
    efis_copilot_pfd_brightness = XPLMRegisterDataAccessor("xhsi/pfd_copilot/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessPFDCopilot, setBrightnessPFDCopilot, // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/nd_copilot/map_range
    efis_copilot_map_range_selector = XPLMRegisterDataAccessor("xhsi/nd_copilot/map_range",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMapRange, setCopilotMapRange,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/radio1
    efis_copilot_dme_1_selector = XPLMRegisterDataAccessor("xhsi/nd_copilot/radio1",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotRadio1, setCopilotRadio1,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/radio2
    efis_copilot_dme_2_selector = XPLMRegisterDataAccessor("xhsi/nd_copilot/radio2",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotRadio2, setCopilotRadio2,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/tfc
    efis_copilot_shows_tcas = XPLMRegisterDataAccessor("xhsi/nd_copilot/tfc",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotTFC, setCopilotTFC,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/arpt
    efis_copilot_shows_airports = XPLMRegisterDataAccessor("xhsi/nd_copilot/arpt",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotARPT, setCopilotARPT,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/wpt
    efis_copilot_shows_waypoints = XPLMRegisterDataAccessor("xhsi/nd_copilot/wpt",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotWPT, setCopilotWPT,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/vor
    efis_copilot_shows_vors = XPLMRegisterDataAccessor("xhsi/nd_copilot/vor",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotVOR, setCopilotVOR,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/ndb
    efis_copilot_shows_ndbs = XPLMRegisterDataAccessor("xhsi/nd_copilot/ndb",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotNDB, setCopilotNDB,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/sta
    efis_copilot_shows_stas = XPLMRegisterDataAccessor("xhsi/nd_copilot/sta",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotSTA, setCopilotSTA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/data
    efis_copilot_shows_data = XPLMRegisterDataAccessor("xhsi/nd_copilot/data",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotDATA, setCopilotDATA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/pos
    efis_copilot_shows_pos = XPLMRegisterDataAccessor("xhsi/nd_copilot/pos",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotPOS, setCopilotPOS,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/terrain
    efis_copilot_shows_terrain = XPLMRegisterDataAccessor("xhsi/nd_copilot/terrain",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotTerrain, setCopilotTerrain,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/vp
    efis_copilot_shows_vp = XPLMRegisterDataAccessor("xhsi/nd_copilot/vp",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotVerticalPath, setCopilotVerticalPath,  // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/ils
    efis_copilot_shows_ils = XPLMRegisterDataAccessor("xhsi/nd_copilot/ils",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotILS, setCopilotILS,                        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/vp
    efis_copilot_metric_alt = XPLMRegisterDataAccessor("xhsi/nd_copilot/metric_alt",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotMetricAlt, setCopilotMetricAlt,           // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_pilot/track_fpa
    efis_copilot_track_fpa = XPLMRegisterDataAccessor("xhsi/nd_copilot/track_fpa",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotTrackFPA, setCopilotTrackFPA,              // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/map_ctr
    efis_copilot_map_mode = XPLMRegisterDataAccessor("xhsi/nd_copilot/map_ctr",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getCopilotMapCTR, setCopilotMapCTR,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/map_mode
    efis_copilot_map_submode = XPLMRegisterDataAccessor("xhsi/nd_copilot/map_mode",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMapMode, setCopilotMapMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/nav_source
    copilot_hsi_selector = XPLMRegisterDataAccessor("xhsi/nd_copilot/nav_source",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotHSISource, setCopilotHSISource,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/pfd_copilot/da_bug
    efis_copilot_da_bug = XPLMRegisterDataAccessor("xhsi/pfd_copilot/da_bug",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotDAbug, setCopilotDAbug,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/pfd_copilot/mins_mode
    efis_copilot_mins_mode = XPLMRegisterDataAccessor("xhsi/pfd_copilot/mins_mode",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMinsMode, setCopilotMinsMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/nd_copilot/map_zoomin
    efis_copilot_map_zoomin = XPLMRegisterDataAccessor("xhsi/nd_copilot/map_zoomin",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMapRange100, setCopilotMapRange100,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/chrono_elapsed
    efis_copilot_chrono = XPLMRegisterDataAccessor("xhsi/nd_copilot/chrono_elapsed",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getCopilotChrono, setCopilotChrono,              // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/chrono_running
    efis_copilot_chrono_running = XPLMRegisterDataAccessor("xhsi/nd_copilot/chrono_running",
    									xplmType_Int,                                  // The types we support
                                        1,                                               // Writable
                                        getCopilotChronoRun, setCopilotChronoRun,        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_gain
    // Weather radar gain
    // float between 0 to 1, default 0
    efis_copilot_wxr_gain = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_gain",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getCopilotWxrGain, setCopilotWxrGain,            // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_tilt
    // Weather radar antena tilt in degrees (-15 to +15)
    // float between -15 to 15, default 0
    efis_copilot_wxr_tilt = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_tilt",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getCopilotWxrTilt, setCopilotWxrTilt,            // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_auto_tilt
    // Weather radar antena auto tilt system (or stabilizer)
    // boolean, default true (1)
    efis_copilot_wxr_auto_tilt = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_auto_tilt",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrAutoTilt, setCopilotWxrAutoTilt,    // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_auto_gain
    // Weather radar automatic calibrated gain
    // boolean, default true (1)
    efis_copilot_wxr_auto_gain = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_auto_gain",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrAutoGain, setCopilotWxrAutoGain,    // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_test
    // Weather radar test mode
    // boolean, default false (0)
    efis_copilot_wxr_test = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_test",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrTest, setCopilotWxrTest,            // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_mode
    // Weather radar mode
    // 0=OFF, 1=Weather, 2=Weather+Turb, 3=Map 4=Forced on (ignore standby on ground)
    // default 1=Weather
    efis_copilot_wxr_mode = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_mode",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrMode, setCopilotWxrMode,            // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_slave
    // Weather radar slave :
    // when true, get the settings from the opposite side
    // default false (0)
    efis_copilot_wxr_slave = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_slave",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrSlave, setCopilotWxrSlave,          // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_react
    // Weather radar REACT
    // when false, signal attenuation is simulated
    // default true (1)
    efis_copilot_wxr_react = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_react",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrReact, setCopilotWxrReact,          // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_narrow
    // Weather radar narrow beam
    // when false - beam is at standard sweep of 120° (depends on the sweep angle settings)
    // when true - beam is at 60° and gives a faster sweep rate
    // default false (0)
    efis_copilot_wxr_narrow = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_narrow",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrNarrow, setCopilotWxrNarrow,        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/nd_copilot/wxr_alert
    // Weather radar alert mode on
    // when true, high turbulence areas flashes
    // default false (0)
    efis_copilot_wxr_alert = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_alert",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrAlert, setCopilotWxrAlert,          // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/nd_copilot/wxr_target
    // Weather radar target mode on
    // when true, alarm visible when high turbulence within 25 nm range
    // default false (0)
    efis_copilot_wxr_target = XPLMRegisterDataAccessor("xhsi/nd_copilot/wxr_target",
                                        xplmType_Int,                                    // The types we support
                                        1,                                               // Writable
                                        getCopilotWxrTarget, setCopilotWxrTarget,        // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    XPLMDebugString("XHSI: custom copilot DataRefs registered\n");

}


void registerGeneralDataRefs(void) {

    XPLMDebugString("XHSI: registering custom General DataRefs\n");

    // xhsi/style
    xhsi_instrument_style = XPLMRegisterDataAccessor("xhsi/style",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getStyle, setStyle,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/rwy_length_min
    xhsi_rwy_length_min = XPLMRegisterDataAccessor("xhsi/rwy_length_min",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getMinRwyLen, setMinRwyLen,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/rwy_units
    xhsi_rwy_units = XPLMRegisterDataAccessor("xhsi/rwy_units",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getRwyUnits, setRwyUnits,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

//    // xhsi/rtu/contact_atc
//    xhsi_rtu_contact_atc = XPLMRegisterDataAccessor("xhsi/rtu/contact_atc",
//                                        xplmType_Int,                                  // The types we support
//                                        1,                                                   // Writable
//                                        getContactATC, setContactATC,      // Integer accessors
//                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/rtu/selected_radio
    xhsi_rtu_selected_radio = XPLMRegisterDataAccessor("xhsi/rtu/selected_radio",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getSelectedRadio, setSelectedRadio,            // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/clock/brightness
    xhsi_clock_brightness = XPLMRegisterDataAccessor("xhsi/clock/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessClock, setBrightnessClock,          // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/clock/utc_selector
    xhsi_utc_selector = XPLMRegisterDataAccessor("xhsi/clock/utc_selector",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getClockUTCSource, setClockUTCSource,                // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);      // Refcons not used

    // xhsi/clock/et_running
    xhsi_et_running = XPLMRegisterDataAccessor("xhsi/clock/et_running",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getClockETRunning, setClockETRunning,          // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);      // Refcons not used

    // xhsi/clock/et_frozen_time
    xhsi_et_frozen_time = XPLMRegisterDataAccessor("xhsi/clock/et_frozen_time",
                                        xplmType_Float,                                // The types we support
                                        0,                                             // Read-only
                                        NULL, NULL,                                    // Integer accessors
                                        getClockETFrozenTime, NULL,                    // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);      // Refcons not used

    // xhsi/clock/show_date
    xhsi_show_date = XPLMRegisterDataAccessor("xhsi/clock/show_date",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getClockShowDate, setClockShowDate,            // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);      // Refcons not used

    XPLMDebugString("XHSI: custom General DataRefs registered\n");

}

void registerEGPWSDataRefs(void) {

    XPLMDebugString("XHSI: registering custom EGPWS DataRefs\n");

    // xhsi/egpws/flaps_mode
    egpws_flaps_mode = XPLMRegisterDataAccessor("xhsi/egpws/flaps_mode",
                                        xplmType_Int,                              // The types we support
                                        1,                                         // Writable
                                        getEgpwsFlapsMode, setEgpwsFlapsMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/egpws/gs_mode
    egpws_gs_mode = XPLMRegisterDataAccessor("xhsi/egpws/gs_mode",
                                        xplmType_Int,                              // The types we support
                                        1,                                         // Writable
                                        getEgpwsGsMode, setEgpwsGsMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/egpws/system
    egpws_sys = XPLMRegisterDataAccessor("xhsi/egpws/system",
                                         xplmType_Int,                              // The types we support
                                         1,                                         // Writable
                                         getEgpwsSystem, setEgpwsSystem,      // Integer accessors
                                         NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // TODO:
    /*
     * egpws_flaps_mode
     * egpws_gs_mode
     * egpws_sys
     *
     */

    XPLMDebugString("XHSI: custom EGPWS DataRefs registered\n");

}

void registerWeatherRadarDataRefs(void) {

    XPLMDebugString("XHSI: registering custom Weather radar DataRefs\n");

    // TODO:

    XPLMDebugString("XHSI: custom Weather radar DataRefs registered\n");

}

void registerEICASDataRefs(void) {

    XPLMDebugString("XHSI: registering custom EICAS DataRefs\n");

    // xhsi/eicas/brightness
    eicas_brightness = XPLMRegisterDataAccessor("xhsi/eicas/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessEICAS, setBrightnessEICAS,          // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/eicas/engine_type
    engine_type = XPLMRegisterDataAccessor("xhsi/eicas/engine_type",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getEICASMode, setEICASMode,                    // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/eicas/trq_scale
    trq_scale = XPLMRegisterDataAccessor("xhsi/eicas/trq_scale",
                                        xplmType_Int,                                  // The types we support
                                        1,                                             // Writable
                                        getTRQscale, setTRQscale,                      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/eicas/fuel_units
    fuel_units = XPLMRegisterDataAccessor("xhsi/eicas/fuel_units",
                                        xplmType_Int,                                  // Integer type
                                        1,                                             // Writable
                                        getFuelUnits, setFuelUnits,                    // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/eicas/temp_units
    temp_units = XPLMRegisterDataAccessor("xhsi/eicas/temp_units",
                                        xplmType_Int,                                  // Integer type
                                        1,                                             // Writable
                                        getTempUnits, setTempUnits,                    // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

	// xhsi/eicas/trq_max_lbft
	override_trq_max = XPLMRegisterDataAccessor("xhsi/eicas/trq_max_lbft",
										xplmType_Float,                                // Float type
										1,                                             // Writable
										NULL, NULL,                                    // No integer accessors
										getOverrideTRQmax, setOverrideTRQmax,          // Float accessors
										NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/eicas/fwc_phase
	fwc_phase = XPLMRegisterDataAccessor("xhsi/eicas/fwc_phase",
                                        xplmType_Int,                                  // Integer type
                                        1,                                             // Writable
                                        getFWCFlightPhase, setFWCFlightPhase,          // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    XPLMDebugString("XHSI: custom EICAS DataRefs registered\n");

}


void registerMFDDataRefs(void) {

    XPLMDebugString("XHSI: registering custom MFD DataRefs\n");

    // xhsi/mfd/brightness
    mfd_brightness = XPLMRegisterDataAccessor("xhsi/mfd/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessMFD, setBrightnessMFD,              // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/mfd/mode
    mfd_mode = XPLMRegisterDataAccessor("xhsi/mfd/mode",
                                        xplmType_Int,                // The types we support
                                        1,                           // Writable
                                        getMFDMode, setMFDMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/mfd/fuel_used
    mfd_fuel_used = XPLMRegisterDataAccessor("xhsi/mfd/fuel_used",
    									xplmType_FloatArray,                // The types we support
                                        1,                                  // Writable
                                        NULL, NULL,      					// Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL,
                                        getFuelUsed, setFuelUsed, 			// Float array accessors
                                        NULL, NULL, NULL, NULL);            // Refcons not used

    // xhsi/mfd/crew_oxy_psi
    mfd_crew_oxy_psi = XPLMRegisterDataAccessor("xhsi/mfd/crew_oxy_psi",
			xplmType_Float,  // The types we support
            1,               // Writable
            NULL, NULL,      // Integer accessors
            getCrewOxyPsi, setCrewOxyPsi,		// Float accessors
            NULL, NULL, NULL, NULL,
            NULL, NULL, 	 // Float array accessors
            NULL, NULL, NULL, NULL);

    XPLMDebugString("XHSI: custom MFD DataRefs registered\n");

}


void registerCDUDataRefs(void) {

    XPLMDebugString("XHSI: registering custom CDU DataRefs\n");

    // xhsi/cdu_pilot/brightness
    cdu_pilot_brightness = XPLMRegisterDataAccessor("xhsi/cdu_pilot/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessCDUCaptain, setBrightnessCDUCaptain, // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/cdu_copilot/brightness
    cdu_copilot_brightness = XPLMRegisterDataAccessor("xhsi/cdu_copilot/brightness",
                                        xplmType_Float,                                  // The types we support
                                        1,                                               // Writable
                                        NULL, NULL,
                                        getBrightnessCDUCopilot, setBrightnessCDUCopilot, // Float accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

    // xhsi/cdu_pilot/source
    cdu_pilot_source = XPLMRegisterDataAccessor("xhsi/cdu_pilot/source",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotCDUSource, setPilotCDUSource,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/cdu_copilot/source
    cdu_copilot_source = XPLMRegisterDataAccessor("xhsi/cdu_copilot/source",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotCDUSource, setCopilotCDUSource,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/cdu_pilot/side
    cdu_pilot_side = XPLMRegisterDataAccessor("xhsi/cdu_pilot/side",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotCDUSide, setPilotCDUSide,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used
    // xhsi/cdu_copilot/side
    cdu_copilot_side = XPLMRegisterDataAccessor("xhsi/cdu_copilot/side",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotCDUSide, setCopilotCDUSide,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    XPLMDebugString("XHSI: custom CDU DataRefs registered\n");

}



float notifyDataRefEditorCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMPluginID PluginID = XPLMFindPluginBySignature("xplanesdk.examples.DataRefEditor");
    if (PluginID != XPLM_NO_PLUGIN_ID)
    {
        XPLMDebugString("XHSI: notifying DataRefEditor\n");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/style");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/rwy_length_min");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/rwy_units");

        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/mfd/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/mfd/mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/mfd/fuel_used");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/mfd/crew_oxy_psi");

        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/cdu_pilot/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/cdu_copilot/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/cdu_pilot/source");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/cdu_copilot/source");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/cdu_pilot/side");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/cdu_copilot/side");
        // TODO: EGPWS
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/engine_type");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/trq_scale");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/fuel_units");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/temp_units");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/trq_max_lbft");

        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_pilot/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/sta");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/data");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/pos");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/terrain");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/vp");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/map_zoomin");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/chrono");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/chrono_run");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_gain");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_tilt");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_auto_gain");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_auto_tilt");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_test");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_slave");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_react");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_narrow");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_alert");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/wxr_target");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_pilot/da_bug");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_pilot/mins_mode");

        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_copilot/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_range");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/radio1");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/radio2");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/tfc");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/arpt");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wpt");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/vor");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/ndb");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/sta");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/data");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/pos");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/terrain");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/vp");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_ctr");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/nav_source");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_zoomin");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/chrono");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/chrono_run");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_gain");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_tilt");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_auto_gain");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_auto_tilt");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_test");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_slave");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_react");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_narrow");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_alert");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/wxr_target");

        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_copilot/da_bug");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_copilot/mins_mode");
//        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/rtu/contact_atc");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/rtu/selected_radio");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/clock/brightness");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/clock/utc_selector");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/clock/et_running");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/clock/et_frozen_time");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/clock/show_date");
    }

    return 0.0f;

}



float initGeneralCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom general DataRefs\n");

    // set defaults

    // General instrument style 0:Boeing 1:Airbus
    XPLMSetDatai(xhsi_instrument_style, 0);

    // Don't override the minimum runway length that is defined in the XHSI_app/Preferences
    XPLMSetDatai(xhsi_rwy_length_min, 0);

    // Runway length units 0:Meters 1:Feet (has no effect when min_rwy_lentgh==0)
    XPLMSetDatai(xhsi_rwy_units, 0);

//    // This dataref is normally set by a XPLMCommand intercept (see commands.c))
//    XPLMSetDatai(xhsi_rtu_contact_atc, 0);

    // No radio selected in the RTU
    XPLMSetDatai(xhsi_rtu_selected_radio, 0);

    // Clock Brightness set to MAX
    XPLMSetDataf(xhsi_clock_brightness, 1.0f);

    // UTC Selector is in GPS position (0=GPS, 1=INT, 2=SET)
    XPLMSetDatai(xhsi_utc_selector,0);

    // ET Switch is in running position (1=Running, 0=STOP, 2=RESET)
    // This ensure ET frozen time is set
    XPLMSetDatai(xhsi_et_running,0);
    XPLMSetDatai(xhsi_et_running,1);

    XPLMDebugString("XHSI: custom general DataRefs initialized\n");

    return 0.0f;

}


float initPilotCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom pilot DataRefs\n");

    // set reasonable defaults for the pilot's ND

    // ND Brightness set to MAX
    XPLMSetDataf(efis_pilot_nd_brightness, 1.0f);

    // PFD Brightness set to MAX
    XPLMSetDataf(efis_pilot_pfd_brightness, 1.0f);

    // STA on
    XPLMSetDatai(efis_pilot_shows_stas, 1);

    // DATA on
    XPLMSetDatai(efis_pilot_shows_data, 1);

    // POS off
    XPLMSetDatai(efis_pilot_shows_pos, 0);

    // TERRAIN on ND - EGPWS off
    XPLMSetDatai(efis_pilot_shows_terrain, 0);

    // Vertical Path on ND
    XPLMSetDatai(efis_pilot_shows_vp, 0);

    // Instrument Landing System
    XPLMSetDatai(efis_pilot_shows_ils, 0);

    // Metric Altitude
    XPLMSetDatai(efis_pilot_metric_alt, 0);

    // Track FPA (Flight Path Angle / Flight Path Vector / Bird)
    XPLMSetDatai(efis_pilot_track_fpa, 0);

    // just a default DA
    XPLMSetDatai(efis_pilot_da_bug, 0);

    // mins is radio
    XPLMSetDatai(efis_pilot_mins_mode, 0);

    // normal scale
    XPLMSetDatai(efis_pilot_map_zoomin, 0);

    // chrono at 00:00
    XPLMSetDataf(efis_pilot_chrono, 0);

    // chrono stop
    XPLMSetDatai(efis_pilot_chrono_running, 0);

    // Weather radar gain 0 dB
    XPLMSetDataf(efis_pilot_wxr_gain, 0.5);

    // Weather radar tilt 0 degrees
    XPLMSetDataf(efis_pilot_wxr_tilt, 0);

    // Weather radar auto tilt ON
    XPLMSetDatai(efis_pilot_wxr_auto_tilt, 1);

    // Weather radar auto gain ON
    XPLMSetDatai(efis_pilot_wxr_auto_gain, 1);

    // Weather radar auto test OFF
    XPLMSetDatai(efis_pilot_wxr_test, 0);

    // Weather radar mode : Weather (ON with standby on ground)
    XPLMSetDatai(efis_pilot_wxr_mode, 1);

    // Weather radar slave OFF
    XPLMSetDatai(efis_pilot_wxr_slave, 0);

    // Weather radar REACT OFF (Rain Echo Attenuation Compensation Technique)
    XPLMSetDatai(efis_pilot_wxr_slave, 0);

    // Weather radar narrow scan OFF (60° fast scan mode)
    XPLMSetDatai(efis_pilot_wxr_narrow, 0);

    // Weather radar alert OFF
    XPLMSetDatai(efis_pilot_wxr_alert, 0);

    // Weather radar target OFF
    XPLMSetDatai(efis_pilot_wxr_target, 0);


    XPLMDebugString("XHSI: custom pilot DataRefs initialized\n");

    return 0.0f;

}


float initCopilotCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom copilot DataRefs\n");

    // set reasonable defaults for the copilot's ND

    // ND Brightness set to MAX
    XPLMSetDataf(efis_copilot_nd_brightness, 1.0f);

    // PFD Brightness set to MAX
    XPLMSetDataf(efis_copilot_pfd_brightness, 1.0f);

    // map range 40
    XPLMSetDatai(efis_copilot_map_range_selector, 2);

    // radio1 : NAV1
    XPLMSetDatai(efis_copilot_dme_1_selector, 2);

    // radio2 : NAV2
    XPLMSetDatai(efis_copilot_dme_2_selector, 2);

    // TFC on
    XPLMSetDatai(efis_copilot_shows_tcas, 1);

    // ARPT on
    XPLMSetDatai(efis_copilot_shows_airports, 1);

    // WPT on
    XPLMSetDatai(efis_copilot_shows_waypoints, 1);

    // VOR on
    XPLMSetDatai(efis_copilot_shows_vors, 1);

    // NDB on
    XPLMSetDatai(efis_copilot_shows_ndbs, 1);

    // STA on
    XPLMSetDatai(efis_copilot_shows_stas, 1);

    // DATA on
    XPLMSetDatai(efis_copilot_shows_data, 1);

    // POS off
    XPLMSetDatai(efis_copilot_shows_pos, 0);

    // TERRAIN on ND - EGPWS off
    XPLMSetDatai(efis_copilot_shows_terrain, 0);

    // TERRAIN on ND - Vertical path
    XPLMSetDatai(efis_copilot_shows_vp, 0);

    // Instrument Landing System
    XPLMSetDatai(efis_copilot_shows_ils, 0);

    // Metric Altitude
    XPLMSetDatai(efis_copilot_metric_alt, 0);

    // Track FPA (Flight Path Angle / Flight Path Vector / Bird)
    XPLMSetDatai(efis_copilot_track_fpa, 0);

    // CTR on ! (just to be a little different from the pilot's ND default)
    XPLMSetDatai(efis_copilot_map_mode, 0);

    // mode MAP
    XPLMSetDatai(efis_copilot_map_submode, 2);

    // HSI source : NAV2 !
    XPLMSetDatai(copilot_hsi_selector, 1);

    // just a default DA
    XPLMSetDatai(efis_copilot_da_bug, 1000);

    // mins is baro
    XPLMSetDatai(efis_copilot_mins_mode, 1);

    // scale * 100
    XPLMSetDatai(efis_copilot_map_zoomin, 0);

    // chrono at 00:00
    XPLMSetDataf(efis_copilot_chrono, 0);

    // chrono stop
    XPLMSetDatai(efis_copilot_chrono_running, 0);

    // Weather radar gain 0 dB
    XPLMSetDataf(efis_copilot_wxr_gain, 0.5);

    // Weather radar tilt 0 degrees
    XPLMSetDataf(efis_copilot_wxr_tilt, 0);

    // Weather radar auto tilt ON
    XPLMSetDatai(efis_copilot_wxr_auto_tilt, 1);

    // Weather radar auto gain ON
    XPLMSetDatai(efis_copilot_wxr_auto_gain, 1);

    // Weather radar auto test OFF
    XPLMSetDatai(efis_copilot_wxr_test, 0);

    // Weather radar mode:  Weather (ON with standby on ground)
    XPLMSetDatai(efis_copilot_wxr_mode, 1);

    // Weather radar slave OFF
    XPLMSetDatai(efis_copilot_wxr_slave, 0);

    // Weather radar REACT OFF (Rain Echo Attenuation Compensation Technique)
    XPLMSetDatai(efis_copilot_wxr_slave, 0);

    // Weather radar narrow scan OFF (60° fast scan mode)
    XPLMSetDatai(efis_copilot_wxr_narrow, 0);

    // Weather radar alert OFF
    XPLMSetDatai(efis_copilot_wxr_alert, 0);

    // Weather radar target OFF
    XPLMSetDatai(efis_copilot_wxr_target, 0);

    XPLMDebugString("XHSI: custom copilot DataRefs initialized\n");

    return 0.0f;
}

float initEGPWSCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom EGPWS DataRefs\n");

    // set some defaults...

    // Flaps mode on
    XPLMSetDatai(egpws_flaps_mode, 1);

	// Glide Slope mode on
    XPLMSetDatai(egpws_gs_mode, 1);

	// EGPWS ON
	XPLMSetDatai(egpws_sys, 1);

    XPLMDebugString("XHSI: custom EPGWS DataRefs initialized\n");

    return 0.0f;

}

float initWeatherRadarCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom Weather radar DataRefs\n");

    // set some defaults...
    // TODO:

    XPLMDebugString("XHSI: custom Weather radar DataRefs initialized\n");

    return 0.0f;

}

float initEICASCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom EICAS DataRefs\n");

    // set some defaults...

    // EICAS Brightness set to MAX
    XPLMSetDataf(eicas_brightness, 1.0f);

    // type 0 = N1 / 1 = EPR / 2 = TRQ / 3 = MAP
    XPLMSetDatai(engine_type, 0);

	// scale 0 = LbFt, 1 = Nm, 2 = %
    XPLMSetDatai(trq_scale, 0);

    // xhsi/eicas/fuel_units
    XPLMSetDatai(fuel_units,0);

    // xhsi/eicas/temp_units
    // Set to Celcius
    XPLMSetDatai(temp_units,0);

	// don't override the maximum torque that X-Plane calculates
	XPLMSetDataf(override_trq_max, 0.0f);

    // Set FWC Flight Phase to 0 (Power OFF)
    XPLMSetDatai(fwc_phase,0);

    XPLMDebugString("XHSI: custom EICAS DataRefs initialized\n");

    return 0.0f;

}


float initMFDCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {
	float zero_tab[8]= { 0,0,0,0,0,0,0,0 };

    XPLMDebugString("XHSI: initializing custom MFD DataRefs\n");

    // MFD Brightness set to MAX
    XPLMSetDataf(mfd_brightness, 1.0f);

    // set a default for the MFD mode

    // xhsi/mfd/mode
	/* MFD_MODE_ARPT = 0; MFD_MODE_FPLN = 1; MFD_MODE_RTU = 2; MFD_MODE_EICAS = 3; // MFD_MODE_ENGINE
	 * MFD_MODE_ENGINE = 3; // MFD_MODE_EICAS
	 * MFD_MODE_BLEED = 4; MFD_MODE_CAB_PRESS = 5; MFD_MODE_ELEC = 6; MFD_MODE_HYDR = 7;
	 * MFD_MODE_FUEL = 8;  MFD_MODE_APU = 9; MFD_MODE_COND = 10; MFD_MODE_DOOR_OXY = 11;
	 * MFD_MODE_WHEELS = 12; MFD_MODE_FCTL = 13; MFD_MODE_SYS = 14; // SYS on Boeing, CRUIZE on Airbus
	 * MFD_MODE_STATUS = 15;
	 * Set default mode to the engine page
	 */
    XPLMSetDatai(mfd_mode, 3);

    // xhsi/mfd/fuel_used float[8]
	XPLMSetDatavf(mfd_fuel_used, zero_tab, 0, 8);

	// xhsi/mfd/crew_oxy_psi
	// TODO: Init value should be read from XHSI Aircraft preference file
	XPLMSetDataf(mfd_crew_oxy_psi, 1850.0f);

	XPLMDebugString("XHSI: custom MFD DataRefs initialized\n");

    return 0.0f;

}


float initCDUCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom CDU DataRefs\n");

    // CDU Brightness Pilot set to MAX
    XPLMSetDataf(cdu_pilot_brightness, 1.0f);

    // CDU Brightness CoPilot set to MAX
    XPLMSetDataf(cdu_copilot_brightness, 1.0f);

    // set a default for the CDU source

    // mode 0 = X-Plane legacy FMS / 1 = X-FMC / 2 = UFMC/X737FMC
    XPLMSetDatai(cdu_pilot_source, 0);
    XPLMSetDatai(cdu_copilot_source, 0);
    // cdu_pilot_side, cdu_copilote_side : 0=send left CDU, 1=send right CDU (Only QPAC based aircrafts have 2 independent MCDU)
    XPLMSetDatai(cdu_pilot_side, 0);
    XPLMSetDatai(cdu_copilot_side, 0);

	XPLMDebugString("XHSI: custom CDU DataRefs initialized\n");

    return 0.0f;

}


void unregisterPilotDataRefs(void) {

    // xhsi/nd_pilot/brightness
	XPLMUnregisterDataAccessor(efis_pilot_nd_brightness);

    // xhsi/pfd_pilot/brightness
	XPLMUnregisterDataAccessor(efis_pilot_pfd_brightness);

    // xhsi/nd_pilot/sta
    XPLMUnregisterDataAccessor(efis_pilot_shows_stas);

    // xhsi/nd_pilot/data
    XPLMUnregisterDataAccessor(efis_pilot_shows_data);

    // xhsi/nd_pilot/pos
    XPLMUnregisterDataAccessor(efis_pilot_shows_pos);

    // xhsi/nd_pilot/terrain
    XPLMUnregisterDataAccessor(efis_pilot_shows_terrain);

    // xhsi/nd_pilot/vp
    XPLMUnregisterDataAccessor(efis_pilot_shows_vp);

    // xhsi/nd_pilot/ils
    XPLMUnregisterDataAccessor(efis_pilot_shows_ils);

    // xhsi/nd_pilot/metric_alt
    XPLMUnregisterDataAccessor(efis_pilot_metric_alt);

    // xhsi/nd_pilot/track_fpa
    XPLMUnregisterDataAccessor(efis_pilot_track_fpa);

    // xhsi/pfd_pilot/da_bug
    XPLMUnregisterDataAccessor(efis_pilot_da_bug);

    // xhsi/pfd_pilot/mins_mode
    XPLMUnregisterDataAccessor(efis_pilot_mins_mode);

    // xhsi/nd_pilot/map_zoomin
    XPLMUnregisterDataAccessor(efis_pilot_map_zoomin);

    // xhsi/nd_pilot/chrono
    XPLMUnregisterDataAccessor(efis_pilot_chrono);

    // xhsi/nd_pilot/chrono_run
    XPLMUnregisterDataAccessor(efis_pilot_chrono_running);

    // xhsi/nd_pilot/wxr_gain
    XPLMUnregisterDataAccessor(efis_pilot_wxr_gain);

    // xhsi/nd_pilot/wxr_tilt
    XPLMUnregisterDataAccessor(efis_pilot_wxr_tilt);

    // xhsi/nd_pilot/wxr_auto_tilt
    XPLMUnregisterDataAccessor(efis_pilot_wxr_auto_tilt);

    // xhsi/nd_pilot/wxr_auto_gain
    XPLMUnregisterDataAccessor(efis_pilot_wxr_auto_gain);

    // xhsi/nd_pilot/wxr_test
    XPLMUnregisterDataAccessor(efis_pilot_wxr_test);

    // xhsi/nd_pilot/wxr_mode
    XPLMUnregisterDataAccessor(efis_pilot_wxr_mode);

    // xhsi/nd_pilot/wxr_slave
    XPLMUnregisterDataAccessor(efis_pilot_wxr_slave);

    // xhsi/nd_pilot/wxr_react
    XPLMUnregisterDataAccessor(efis_pilot_wxr_react);

    // xhsi/nd_pilot/wxr_narrow
    XPLMUnregisterDataAccessor(efis_pilot_wxr_narrow);

    // xhsi/nd_pilot/wxr_alert
    XPLMUnregisterDataAccessor(efis_pilot_wxr_alert);

    // xhsi/nd_pilot/wxr_target
    XPLMUnregisterDataAccessor(efis_pilot_wxr_target);
}


void unregisterCopilotDataRefs(void) {

    // xhsi/nd_copilot/brightness
	XPLMUnregisterDataAccessor(efis_copilot_nd_brightness);

    // xhsi/pfd_copilot/brightness
	XPLMUnregisterDataAccessor(efis_copilot_pfd_brightness);

    // xhsi/nd_copilot/map_range
    XPLMUnregisterDataAccessor(efis_copilot_map_range_selector);

    // xhsi/nd_copilot/radio1
    XPLMUnregisterDataAccessor(efis_copilot_dme_1_selector);

    // xhsi/nd_copilot/radio2
    XPLMUnregisterDataAccessor(efis_copilot_dme_2_selector);

    // xhsi/nd_copilot/tfc
    XPLMUnregisterDataAccessor(efis_copilot_shows_tcas);

    // xhsi/nd_copilot/arpt
    XPLMUnregisterDataAccessor(efis_copilot_shows_airports);

    // xhsi/nd_copilot/wpt
    XPLMUnregisterDataAccessor(efis_copilot_shows_waypoints);

    // xhsi/nd_copilot/vor
    XPLMUnregisterDataAccessor(efis_copilot_shows_vors);

    // xhsi/nd_copilot/ndb
    XPLMUnregisterDataAccessor(efis_copilot_shows_ndbs);

    // xhsi/nd_copilot/sta
    XPLMUnregisterDataAccessor(efis_copilot_shows_stas);

    // xhsi/nd_copilot/data
    XPLMUnregisterDataAccessor(efis_copilot_shows_data);

    // xhsi/nd_copilot/pos
    XPLMUnregisterDataAccessor(efis_copilot_shows_pos);

    // xhsi/nd_copilot/terrain
    XPLMUnregisterDataAccessor(efis_copilot_shows_terrain);

    // xhsi/nd_copilot/vp
    XPLMUnregisterDataAccessor(efis_copilot_shows_vp);

    // xhsi/nd_copilot/ils
    XPLMUnregisterDataAccessor(efis_copilot_shows_ils);

    // xhsi/nd_copilot/metric_alt
    XPLMUnregisterDataAccessor(efis_copilot_metric_alt);

    // xhsi/nd_copilot/track_fpa
    XPLMUnregisterDataAccessor(efis_copilot_track_fpa);

    // xhsi/nd_copilot/map_ctr
    XPLMUnregisterDataAccessor(efis_copilot_map_mode);

    // xhsi/nd_copilot/map_mode
    XPLMUnregisterDataAccessor(efis_copilot_map_submode);

    // xhsi/nd_copilot/nav_source
    XPLMUnregisterDataAccessor(copilot_hsi_selector);

    // xhsi/pfd_copilot/da_bug
    XPLMUnregisterDataAccessor(efis_copilot_da_bug);

    // xhsi/pfd_copilot/mins_mode
    XPLMUnregisterDataAccessor(efis_copilot_mins_mode);

    // xhsi/nd_copilot/map_zoomin
    XPLMUnregisterDataAccessor(efis_copilot_map_zoomin);

    // xhsi/nd_copilot/chrono
    XPLMUnregisterDataAccessor(efis_copilot_chrono);

    // xhsi/nd_copilot/chrono_run
    XPLMUnregisterDataAccessor(efis_copilot_chrono_running);

    // xhsi/nd_copilot/wxr_gain
    XPLMUnregisterDataAccessor(efis_copilot_wxr_gain);

    // xhsi/nd_copilot/wxr_tilt
    XPLMUnregisterDataAccessor(efis_copilot_wxr_tilt);

    // xhsi/nd_copilot/wxr_auto_tilt
    XPLMUnregisterDataAccessor(efis_copilot_wxr_auto_tilt);

    // xhsi/nd_copilot/wxr_auto_gain
    XPLMUnregisterDataAccessor(efis_copilot_wxr_auto_gain);

    // xhsi/nd_copilot/wxr_test
    XPLMUnregisterDataAccessor(efis_copilot_wxr_test);

    // xhsi/nd_copilot/wxr_mode
    XPLMUnregisterDataAccessor(efis_copilot_wxr_mode);

    // xhsi/nd_copilot/wxr_slave
    XPLMUnregisterDataAccessor(efis_copilot_wxr_slave);

    // xhsi/nd_copilot/wxr_react
    XPLMUnregisterDataAccessor(efis_copilot_wxr_react);

    // xhsi/nd_copilot/wxr_narrow
    XPLMUnregisterDataAccessor(efis_copilot_wxr_narrow);

    // xhsi/nd_copilot/wxr_alert
    XPLMUnregisterDataAccessor(efis_copilot_wxr_alert);

    // xhsi/nd_copilot/wxr_target
    XPLMUnregisterDataAccessor(efis_copilot_wxr_target);
}

void unregisterGeneralDataRefs(void) {

    // xhsi/style
	XPLMUnregisterDataAccessor(xhsi_instrument_style);

    // xhsi/rwy_length_min
    XPLMUnregisterDataAccessor(xhsi_rwy_length_min);

    // xhsi/rwy_units
    XPLMUnregisterDataAccessor(xhsi_rwy_units);

//    // xhsi/rtu/contact_atc
//    XPLMUnregisterDataAccessor(xhsi_rtu_contact_atc);

    // xhsi/rtu/selected_radio
    XPLMUnregisterDataAccessor(xhsi_rtu_selected_radio);
    
    // xhsi/clock/brightness
    XPLMUnregisterDataAccessor(xhsi_clock_brightness);

    // xhsi/clock/utc_selector
    XPLMUnregisterDataAccessor(xhsi_utc_selector);

    // xhsi/clock/et_running
    XPLMUnregisterDataAccessor(xhsi_et_running);

    // xhsi/clock/et_frozen_time
    XPLMUnregisterDataAccessor(xhsi_et_frozen_time);

    // xhsi/clock/show_date
    XPLMUnregisterDataAccessor(xhsi_show_date);

}

void unregisterEGPWSDataRefs(void) {

    // xhsi/egpws/flaps_mode
	XPLMUnregisterDataAccessor(egpws_flaps_mode);

    // xhsi/egpws/gs_mode
	XPLMUnregisterDataAccessor(egpws_gs_mode);

    // xhsi/egpws/system
	XPLMUnregisterDataAccessor(egpws_sys);
}


void unregisterWeatherRadarDataRefs(void) {
// TODO:

}


void unregisterEICASDataRefs(void) {

	// xhsi/eicas/brightness
	XPLMUnregisterDataAccessor(eicas_brightness);

    // xhsi/eicas/engine_type
    XPLMUnregisterDataAccessor(engine_type);

    // xhsi/eicas/trq_scale
    XPLMUnregisterDataAccessor(trq_scale);

    // xhsi/eicas/fuel_units
    XPLMUnregisterDataAccessor(fuel_units);

    // xhsi/eicas/temp_units
    XPLMUnregisterDataAccessor(temp_units);

	// xhsi/eicas/trq_max_lbft
    XPLMUnregisterDataAccessor(override_trq_max);

	// xhsi/eicas/fwc_phase
    XPLMUnregisterDataAccessor(fwc_phase);
}

void unregisterMFDDataRefs(void) {

    // xhsi/mfd/brightness
	XPLMUnregisterDataAccessor(mfd_brightness);

    // xhsi/mfd/mode
    XPLMUnregisterDataAccessor(mfd_mode);

    // xhsi/mfd_fuel_used
    XPLMUnregisterDataAccessor(mfd_fuel_used);

    // xhsi/mfd_crew_oxy_psi
    XPLMUnregisterDataAccessor(mfd_crew_oxy_psi);

}

void unregisterCDUDataRefs(void) {

    // xhsi/cdu_pilot/brightness
	XPLMUnregisterDataAccessor(cdu_pilot_brightness);

    // xhsi/cdu_copilot/brightness
	XPLMUnregisterDataAccessor(cdu_copilot_brightness);

    // xhsi/cdu_pilot/source
    XPLMUnregisterDataAccessor(cdu_pilot_source);

    // xhsi/cdu_copilot/source
    XPLMUnregisterDataAccessor(cdu_copilot_source);

    // xhsi/cdu_pilot/side
    XPLMUnregisterDataAccessor(cdu_pilot_side);

    // xhsi/cdu_copilot/side
    XPLMUnregisterDataAccessor(cdu_copilot_side);

}


float computeChronoCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {
	int sim_run = !XPLMGetDatai(sim_paused);
	float chrono;
	int chrono_running;
	// Compute ND Chronometers
	if (xhsi_plugin_enabled && sim_run) {
		chrono=XPLMGetDataf(efis_pilot_chrono);
		chrono_running=XPLMGetDatai(efis_pilot_chrono_running);
	    if (chrono_running) {
	    	XPLMSetDataf(efis_pilot_chrono, chrono + inElapsedSinceLastCall);
	    }
		chrono=XPLMGetDataf(efis_copilot_chrono);
		chrono_running=XPLMGetDatai(efis_copilot_chrono_running);
	    if (chrono_running) {
	    	XPLMSetDataf(efis_copilot_chrono, chrono + inElapsedSinceLastCall);
	    }
	}
	return 1.0f;
}


void findDataRefs(void) {

	int 		i;
	char		buf[100];


    XPLMDebugString("XHSI: referencing standard DataRefs\n");

    // Aircraft position
    groundspeed = XPLMFindDataRef("sim/flightmodel/position/groundspeed");
    true_airspeed = XPLMFindDataRef("sim/flightmodel/position/true_airspeed");
    // magpsi = XPLMFindDataRef("sim/flightmodel/position/magpsi"); // DO NOT USE THIS
    magpsi = XPLMFindDataRef("sim/flightmodel/position/mag_psi"); // The real magnetic heading of the aircraft - the old magpsi dataref was FUBAR
    hpath = XPLMFindDataRef("sim/flightmodel/position/hpath");
    latitude = XPLMFindDataRef("sim/flightmodel/position/latitude");	// double
    longitude = XPLMFindDataRef("sim/flightmodel/position/longitude");	// double
    phi = XPLMFindDataRef("sim/flightmodel/position/phi");
    r = XPLMFindDataRef("sim/flightmodel/position/R");
    magvar = XPLMFindDataRef("sim/flightmodel/position/magnetic_variation");
    msl = XPLMFindDataRef("sim/flightmodel/position/elevation");
    agl = XPLMFindDataRef("sim/flightmodel/position/y_agl");
    theta = XPLMFindDataRef("sim/flightmodel/position/theta");
    vpath = XPLMFindDataRef("sim/flightmodel/position/vpath");
    alpha = XPLMFindDataRef("sim/flightmodel/position/alpha");
    beta = XPLMFindDataRef("sim/flightmodel/position/beta");
    on_ground = XPLMFindDataRef("sim/flightmodel/failures/onground_any");


    // Instruments
//	vh_ind_fpm = XPLMFindDataRef("sim/flightmodel/position/vh_ind_fpm");
//	h_ind = XPLMFindDataRef("sim/flightmodel/misc/h_ind");
    airspeed_pilot = XPLMFindDataRef("sim/cockpit2/gauges/indicators/airspeed_kts_pilot");
    airspeed_copilot = XPLMFindDataRef("sim/cockpit2/gauges/indicators/airspeed_kts_copilot");
    altitude_pilot = XPLMFindDataRef("sim/cockpit2/gauges/indicators/altitude_ft_pilot");
    altitude_copilot = XPLMFindDataRef("sim/cockpit2/gauges/indicators/altitude_ft_copilot");
    vvi_pilot = XPLMFindDataRef("sim/cockpit2/gauges/indicators/vvi_fpm_pilot");
    vvi_copilot = XPLMFindDataRef("sim/cockpit2/gauges/indicators/vvi_fpm_copilot");
    sideslip = XPLMFindDataRef("sim/cockpit2/gauges/indicators/sideslip_degrees");
    ra_bug_pilot = XPLMFindDataRef("sim/cockpit2/gauges/actuators/radio_altimeter_bug_ft_pilot");
    ra_bug_copilot = XPLMFindDataRef("sim/cockpit2/gauges/actuators/radio_altimeter_bug_ft_copilot");
    baro_pilot = XPLMFindDataRef("sim/cockpit2/gauges/actuators/barometer_setting_in_hg_pilot");
    baro_copilot = XPLMFindDataRef("sim/cockpit2/gauges/actuators/barometer_setting_in_hg_copilot");
    airspeed_acceleration = XPLMFindDataRef("sim/cockpit2/gauges/indicators/airspeed_acceleration_kts_sec_pilot");
    g_load =  XPLMFindDataRef("sim/flightmodel/forces/g_nrml");
    turnrate_noroll = XPLMFindDataRef("sim/flightmodel/misc/turnrate_noroll");

    // Instruments failures pilot
    sim_op_fail_rel_ss_ahz = XPLMFindDataRef("sim/operation/failures/rel_ss_ahz");
    sim_op_fail_rel_ss_alt = XPLMFindDataRef("sim/operation/failures/rel_ss_alt");
    sim_op_fail_rel_ss_asi = XPLMFindDataRef("sim/operation/failures/rel_ss_asi");
    sim_op_fail_rel_ss_dgy = XPLMFindDataRef("sim/operation/failures/rel_ss_dgy");
    sim_op_fail_rel_ss_tsi = XPLMFindDataRef("sim/operation/failures/rel_ss_tsi");
    sim_op_fail_rel_ss_vvi = XPLMFindDataRef("sim/operation/failures/rel_ss_vvi");
    // Instruments failures co-pilot
    sim_op_fail_rel_cop_ahz = XPLMFindDataRef("sim/operation/failures/rel_cop_ahz");
    sim_op_fail_rel_cop_alt = XPLMFindDataRef("sim/operation/failures/rel_cop_alt");
    sim_op_fail_rel_cop_asi = XPLMFindDataRef("sim/operation/failures/rel_cop_asi");
    sim_op_fail_rel_cop_dgy = XPLMFindDataRef("sim/operation/failures/rel_cop_dgy");
    sim_op_fail_rel_cop_tsi = XPLMFindDataRef("sim/operation/failures/rel_cop_tsi");
    sim_op_fail_rel_cop_vvi = XPLMFindDataRef("sim/operation/failures/rel_cop_vvi");
    // Gears failures
    sim_op_fail_rel_gear_ind = XPLMFindDataRef("sim/operation/failures/rel_gear_ind");
    sim_op_fail_rel_gear_act = XPLMFindDataRef("sim/operation/failures/rel_gear_act");
    // Brake failures
    sim_op_fail_rel_lbrake = XPLMFindDataRef("sim/operation/failures/rel_lbrake");
    sim_op_fail_rel_rbrake = XPLMFindDataRef("sim/operation/failures/rel_rbrake");
    // Tires blownout
    sim_op_fail_rel_tire1 = XPLMFindDataRef("sim/operation/failures/rel_tire1");
    sim_op_fail_rel_tire2 = XPLMFindDataRef("sim/operation/failures/rel_tire2");
    sim_op_fail_rel_tire3 = XPLMFindDataRef("sim/operation/failures/rel_tire3");
    sim_op_fail_rel_tire4 = XPLMFindDataRef("sim/operation/failures/rel_tire4");
    sim_op_fail_rel_tire5 = XPLMFindDataRef("sim/operation/failures/rel_tire5");
    sim_op_fail_rel_tire6 = XPLMFindDataRef("sim/operation/failures/rel_tire6");
    sim_op_fail_rel_tire7 = XPLMFindDataRef("sim/operation/failures/rel_tire7");
    sim_op_fail_rel_tire8 = XPLMFindDataRef("sim/operation/failures/rel_tire8");
    sim_op_fail_rel_tire9 = XPLMFindDataRef("sim/operation/failures/rel_tire9");
    sim_op_fail_rel_tire10 = XPLMFindDataRef("sim/operation/failures/rel_tire10");

    // Electrical
    avionics_on = XPLMFindDataRef("sim/cockpit/electrical/avionics_on");
    battery_on = XPLMFindDataRef("sim/cockpit/electrical/battery_on");
    cockpit_lights_on = XPLMFindDataRef("sim/cockpit/electrical/cockpit_lights_on");

    cockpit_lights = XPLMFindDataRef("sim/cockpit/electrical/cockpit_lights");
    cockpit_instrument_brightness = XPLMFindDataRef("sim/cockpit/electrical/instrument_brightness");
    cockpit_hud_brightness = XPLMFindDataRef("sim/cockpit/electrical/HUD_brightness");
    cockpit_instrument_brightness_ratio = XPLMFindDataRef("sim/cockpit2/electrical/instrument_brightness_ratio_manual");
    cockpit_panel_brightness_ratio = XPLMFindDataRef("sim/cockpit2/electrical/instrument_brightness_ratio_manual");

    // Ambient light
	sim_graphics_misc_cockpit_light_level_r = XPLMFindDataRef("sim/graphics/misc/cockpit_light_level_r");
	sim_graphics_misc_cockpit_light_level_g = XPLMFindDataRef("sim/graphics/misc/cockpit_light_level_g");
	sim_graphics_misc_cockpit_light_level_b = XPLMFindDataRef("sim/graphics/misc/cockpit_light_level_b");
	sim_graphics_misc_outside_light_level_r = XPLMFindDataRef("sim/graphics/misc/outside_light_level_r");
	sim_graphics_misc_outside_light_level_g = XPLMFindDataRef("sim/graphics/misc/outside_light_level_g");
	sim_graphics_misc_outside_light_level_b = XPLMFindDataRef("sim/graphics/misc/outside_light_level_b");

    // Lights
    beacon_lights_on = XPLMFindDataRef("sim/cockpit/electrical/beacon_lights_on");
    landing_lights_on = XPLMFindDataRef("sim/cockpit/electrical/landing_lights_on");
    nav_lights_on = XPLMFindDataRef("sim/cockpit/electrical/nav_lights_on");
    strobe_lights_on = XPLMFindDataRef("sim/cockpit/electrical/strobe_lights_on");
    taxi_light_on = XPLMFindDataRef("sim/cockpit/electrical/taxi_light_on");
	// Lights and PAX signs
    // For the ECAM messages landing lights, smoking, belts
    beacon_lights = XPLMFindDataRef("sim/cockpit2/switches/beacon_on");
    landing_lights = XPLMFindDataRef("sim/cockpit2/switches/landing_lights");
    nav_lights = XPLMFindDataRef("sim/cockpit2/switches/navigation_lights_on");
    strobe_lights = XPLMFindDataRef("sim/cockpit2/switches/strobe_light_on");
    taxi_lights = XPLMFindDataRef("sim/cockpit2/switches/taxi_light_on");
    no_smoking = XPLMFindDataRef("sim/cockpit2/switches/no_smoking");
    fasten_seat_belts = XPLMFindDataRef("sim/cockpit2/switches/fasten_seat_belts");


    pitot_heat_on = XPLMFindDataRef("sim/cockpit/switches/pitot_heat_on");

    // Radios
    nav1_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav1_freq_hz");  // int
    nav2_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav2_freq_hz");	// int
    adf1_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf1_freq_hz");  // int
    adf2_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf2_freq_hz");  // int
    nav1_dir_degt = XPLMFindDataRef("sim/cockpit/radios/nav1_dir_degt");
    nav2_dir_degt = XPLMFindDataRef("sim/cockpit/radios/nav2_dir_degt");
    adf1_dir_degt = XPLMFindDataRef("sim/cockpit/radios/adf1_dir_degt");
    adf2_dir_degt = XPLMFindDataRef("sim/cockpit/radios/adf2_dir_degt");
    nav1_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/nav1_dme_dist_m");
    nav2_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/nav2_dme_dist_m");
    adf1_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/adf1_dme_dist_m");
    adf2_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/adf2_dme_dist_m");
    nav1_obs_degm = XPLMFindDataRef("sim/cockpit/radios/nav1_obs_degm");
    nav2_obs_degm = XPLMFindDataRef("sim/cockpit/radios/nav2_obs_degm");

    nav1_course_degm = XPLMFindDataRef("sim/cockpit/radios/nav1_course_degm");
    nav2_course_degm = XPLMFindDataRef("sim/cockpit/radios/nav2_course_degm");
    nav1_cdi = XPLMFindDataRef("sim/cockpit/radios/nav1_CDI");
    nav2_cdi = XPLMFindDataRef("sim/cockpit/radios/nav2_CDI");
    nav1_hdef_dot = XPLMFindDataRef("sim/cockpit/radios/nav1_hdef_dot");
    nav2_hdef_dot = XPLMFindDataRef("sim/cockpit/radios/nav2_hdef_dot");
    nav1_fromto = XPLMFindDataRef("sim/cockpit/radios/nav1_fromto");
    nav2_fromto = XPLMFindDataRef("sim/cockpit/radios/nav2_fromto");
    nav1_vdef_dot = XPLMFindDataRef("sim/cockpit/radios/nav1_vdef_dot");
    nav2_vdef_dot = XPLMFindDataRef("sim/cockpit/radios/nav2_vdef_dot");

    gps_dir_degt = XPLMFindDataRef("sim/cockpit/radios/gps_dir_degt");
    gps_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/gps_dme_dist_m");
    gps_course_degtm = XPLMFindDataRef("sim/cockpit/radios/gps_course_degtm");
    gps_hdef_dot = XPLMFindDataRef("sim/cockpit/radios/gps_hdef_dot");
    gps_fromto = XPLMFindDataRef("sim/cockpit/radios/gps_fromto");
    gps_vdef_dot = XPLMFindDataRef("sim/cockpit/radios/gps_vdef_dot");

    nav1_dme_time_secs = XPLMFindDataRef("sim/cockpit/radios/nav1_dme_time_secs");
    nav2_dme_time_secs = XPLMFindDataRef("sim/cockpit/radios/nav2_dme_time_secs");
    gps_dme_time_secs = XPLMFindDataRef("sim/cockpit/radios/gps_dme_time_secs");

    gps_has_glideslope = XPLMFindDataRef("sim/cockpit/radios/gps_has_glideslope");
    
//	nav1_dme_nm = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav1_dme_distance_nm");
//	nav2_dme_nm = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav2_dme_distance_nm");
//	gps_dme_nm = XPLMFindDataRef("sim/cockpit2/radios/indicators/gps_dme_distance_nm");
    hsi_dme_nm_pilot = XPLMFindDataRef("sim/cockpit2/radios/indicators/hsi_dme_distance_nm_pilot");
    hsi_dme_nm_copilot = XPLMFindDataRef("sim/cockpit2/radios/indicators/hsi_dme_distance_nm_copilot");

    outer_marker = XPLMFindDataRef("sim/cockpit2/radios/indicators/outer_marker_lit");
    middle_marker = XPLMFindDataRef("sim/cockpit2/radios/indicators/middle_marker_lit");
    inner_marker = XPLMFindDataRef("sim/cockpit2/radios/indicators/inner_marker_lit");

    nav1_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav1_stdby_freq_hz");  // int
    nav2_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav2_stdby_freq_hz");  // int
    adf1_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf1_stdby_freq_hz");  // int
    adf2_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf2_stdby_freq_hz");  // int

    nav1_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav1_nav_id");
    nav2_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav2_nav_id");
    adf1_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/adf1_nav_id");
    adf2_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/adf2_nav_id");
    gps_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/gps_nav_id");

    com1_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com1_freq_hz");              // int (x100 MHz)
    com1_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com1_stdby_freq_hz");  // int (x100 MHz)
    com2_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com2_freq_hz");              // int (x100 MHz)
    com2_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com2_stdby_freq_hz");  // int (x100 MHz)

    com1_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com1_frequency_hz_833");                  // int (x1000 MHz)
    com1_standby_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com1_standby_frequency_hz_833");  // int (x1000 MHz)
    com2_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com2_frequency_hz_833");                  // int (x1000 MHz)
    com2_standby_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com2_standby_frequency_hz_833"); // int (x1000 MHz)

    // AP
    autopilot_state = XPLMFindDataRef("sim/cockpit/autopilot/autopilot_state");
    autopilot_vertical_velocity = XPLMFindDataRef("sim/cockpit/autopilot/vertical_velocity");
    autopilot_altitude = XPLMFindDataRef("sim/cockpit/autopilot/altitude");
    autopilot_approach_selector = XPLMFindDataRef("sim/cockpit/autopilot/approach_selector");
    autopilot_heading_mag = XPLMFindDataRef("sim/cockpit/autopilot/heading_mag");
    autopilot_airspeed = XPLMFindDataRef("sim/cockpit/autopilot/airspeed");
    autopilot_airspeed_is_mach = XPLMFindDataRef("sim/cockpit/autopilot/airspeed_is_mach");
    autopilot_fd_pitch = XPLMFindDataRef("sim/cockpit/autopilot/flight_director_pitch");
    autopilot_fd_roll = XPLMFindDataRef("sim/cockpit/autopilot/flight_director_roll");
    autopilot_mode = XPLMFindDataRef("sim/cockpit/autopilot/autopilot_mode");
    autopilot_autothrottle_enabled = XPLMFindDataRef("sim/cockpit2/autopilot/autothrottle_enabled");
    autopilot_autothrottle_on = XPLMFindDataRef("sim/cockpit2/autopilot/autothrottle_on");
    autopilot_hdg_status = XPLMFindDataRef("sim/cockpit2/autopilot/heading_status");
    autopilot_lnav_status = XPLMFindDataRef("sim/cockpit2/autopilot/nav_status");
    autopilot_vs_status = XPLMFindDataRef("sim/cockpit2/autopilot/vvi_status");
    autopilot_spd_status = XPLMFindDataRef("sim/cockpit2/autopilot/speed_status");
    autopilot_alt_hold_status = XPLMFindDataRef("sim/cockpit2/autopilot/altitude_hold_status");
    autopilot_gs_status = XPLMFindDataRef("sim/cockpit2/autopilot/glideslope_status");
    autopilot_vnav_status = XPLMFindDataRef("sim/cockpit2/autopilot/vnav_status");
    autopilot_toga_status = XPLMFindDataRef("sim/cockpit2/autopilot/TOGA_status");
    autopilot_toga_lateral_status = XPLMFindDataRef("sim/cockpit2/autopilot/TOGA_lateral_status");
    autopilot_roll_status = XPLMFindDataRef("sim/cockpit2/autopilot/roll_status");
    autopilot_pitch_status = XPLMFindDataRef("sim/cockpit2/autopilot/pitch_status");
    autopilot_backcourse_status = XPLMFindDataRef("sim/cockpit2/autopilot/backcourse_status");
    autopilot_heading_roll_mode = XPLMFindDataRef("sim/cockpit/autopilot/heading_roll_mode");


    // Transponder
    transponder_mode = XPLMFindDataRef("sim/cockpit/radios/transponder_mode");	// int
    transponder_code = XPLMFindDataRef("sim/cockpit/radios/transponder_code");	// int
    transponder_id = XPLMFindDataRef("sim/cockpit/radios/transponder_id");	// int


    // EFIS
    hsi_selector = XPLMFindDataRef("sim/cockpit/switches/HSI_selector");	// int
    efis_map_range_selector = XPLMFindDataRef("sim/cockpit/switches/EFIS_map_range_selector");	// int
    efis_dme_1_selector = XPLMFindDataRef("sim/cockpit/switches/EFIS_dme_1_selector");		// int
    efis_dme_2_selector = XPLMFindDataRef("sim/cockpit/switches/EFIS_dme_2_selector");		// int
    efis_shows_weather = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_weather");		// int
    efis_shows_tcas = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_tcas");				// int
    efis_shows_airports = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_airports");		// int
    efis_shows_waypoints = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_waypoints");	// int
    efis_shows_vors = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_VORs");				// int
    efis_shows_ndbs = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_NDBs");				// int
    efis_map_mode = XPLMFindDataRef("sim/cockpit/switches/EFIS_map_mode");	// int
    efis_map_submode = XPLMFindDataRef("sim/cockpit/switches/EFIS_map_submode");	// int


	// Environment
	wind_speed_kt = XPLMFindDataRef("sim/weather/wind_speed_kt");
	wind_direction_degt = XPLMFindDataRef("sim/weather/wind_direction_degt");
	sim_paused = XPLMFindDataRef("sim/time/paused");
	oat = XPLMFindDataRef("sim/weather/temperature_ambient_c");
	isa = XPLMFindDataRef("sim/weather/temperature_sealevel_c");
	tat = XPLMFindDataRef("sim/weather/temperature_le_c");
	sound_speed = XPLMFindDataRef("sim/weather/speed_sound_ms");

	// Clock and timers
	zulu_time_sec = XPLMFindDataRef("sim/time/zulu_time_sec");
	local_time_sec = XPLMFindDataRef("sim/time/local_time_sec");
    timer_is_running = XPLMFindDataRef("sim/time/timer_is_running_sec");
    elapsed_time_sec = XPLMFindDataRef("sim/time/timer_elapsed_time_sec");
    flight_time_sec = XPLMFindDataRef("sim/time/total_flight_time_sec");
    clock_timer_mode = XPLMFindDataRef("sim/cockpit2/clock_timer/timer_mode");
    clock_show_date = XPLMFindDataRef("sim/cockpit2/clock_timer/date_is_showing");
    clock_time_day = XPLMFindDataRef("sim/cockpit2/clock_timer/current_day");
    clock_time_month = XPLMFindDataRef("sim/cockpit2/clock_timer/current_month");

    // Aircraft constants
    acf_vso = XPLMFindDataRef("sim/aircraft/view/acf_Vso");
    acf_vs = XPLMFindDataRef("sim/aircraft/view/acf_Vs");
    acf_vfe = XPLMFindDataRef("sim/aircraft/view/acf_Vfe");
    acf_vno = XPLMFindDataRef("sim/aircraft/view/acf_Vno");
    acf_vne = XPLMFindDataRef("sim/aircraft/view/acf_Vne");
    acf_mmo = XPLMFindDataRef("sim/aircraft/view/acf_Mmo");
    acf_vle = XPLMFindDataRef("sim/aircraft/overflow/acf_Vle");
    speedbrake_equiped = XPLMFindDataRef("sim/aircraft/parts/acf_sbrkEQ");
    retractable_gear = XPLMFindDataRef("sim/aircraft/gear/acf_gear_retract");
    acf_vmca = XPLMFindDataRef("sim/aircraft/overflow/acf_Vmca");
    acf_vyse = XPLMFindDataRef("sim/aircraft/overflow/acf_Vyse");
    acf_tailnum = XPLMFindDataRef("sim/aircraft/view/acf_tailnum");
    // Control surfaces limits (constant)
    acf_controls_elev_dn = XPLMFindDataRef("sim/aircraft/controls/acf_elev_dn");
    acf_controls_elev_up = XPLMFindDataRef("sim/aircraft/controls/acf_elev_up");
    acf_controls_ail_dn = XPLMFindDataRef("sim/aircraft/controls/acf_ail1_dn");
    acf_controls_ail_up = XPLMFindDataRef("sim/aircraft/controls/acf_ail1_up");
    acf_controls_rudder_lr = XPLMFindDataRef("sim/aircraft/controls/acf_rudder_lr");


    // Controls & annunciators
    master_caution = XPLMFindDataRef("sim/cockpit/warnings/annunciators/master_caution");
    master_warning = XPLMFindDataRef("sim/cockpit/warnings/annunciators/master_warning");
    master_accept = XPLMFindDataRef("sim/cockpit/warnings/annunciators/master_accept");
    gear_handle = XPLMFindDataRef("sim/cockpit2/controls/gear_handle_down");
    gear_unsafe = XPLMFindDataRef("sim/cockpit2/annunciators/gear_unsafe");
    gear_types = XPLMFindDataRef("sim/aircraft/parts/acf_gear_type");
    parkbrake_ratio = XPLMFindDataRef("sim/cockpit2/controls/parking_brake_ratio");
    flap_deploy = XPLMFindDataRef("sim/cockpit2/controls/flap_ratio");
    flap_handle = XPLMFindDataRef("sim/cockpit2/controls/flap_handle_deploy_ratio");
    flap_detents = XPLMFindDataRef("sim/aircraft/controls/acf_flap_detents");
    ap_disc = XPLMFindDataRef("sim/cockpit2/annunciators/autopilot_disconnect");
    low_fuel = XPLMFindDataRef("sim/cockpit2/annunciators/fuel_quantity");
    gpws = XPLMFindDataRef("sim/cockpit2/annunciators/GPWS");
    ice = XPLMFindDataRef("sim/cockpit2/annunciators/ice");
    pitot_heat = XPLMFindDataRef("sim/cockpit2/annunciators/pitot_heat");
    stall = XPLMFindDataRef("sim/cockpit2/annunciators/stall_warning");
    gear_warning = XPLMFindDataRef("sim/cockpit2/annunciators/gear_warning");
    auto_brake_level = XPLMFindDataRef("sim/cockpit2/switches/auto_brake_level");
    speedbrake_handle = XPLMFindDataRef("sim/cockpit2/controls/speedbrake_ratio");
    speedbrake_ratio = XPLMFindDataRef("sim/flightmodel2/controls/speedbrake_ratio");
    gear_deploy = XPLMFindDataRef("sim/flightmodel2/gear/deploy_ratio");
    gear_door_ang = XPLMFindDataRef("sim/aircraft/gear/acf_gear_door_ang_now");
    gear_door_type = XPLMFindDataRef("sim/aircraft/gear/acf_gear_door_typ");
    gear_door_ext_ang = XPLMFindDataRef("sim/aircraft/gear/acf_gear_door_ext_ang");
    gear_door_ret_ang = XPLMFindDataRef("sim/aircraft/gear/acf_gear_door_ret_ang");
    yoke_pitch_ratio = XPLMFindDataRef("sim/cockpit2/controls/yoke_pitch_ratio");
    yoke_roll_ratio = XPLMFindDataRef("sim/cockpit2/controls/yoke_roll_ratio");
    yoke_hdg_ratio = XPLMFindDataRef("sim/cockpit2/controls/yoke_heading_ratio");
    elevator_trim = XPLMFindDataRef("sim/cockpit2/controls/elevator_trim");
    aileron_trim = XPLMFindDataRef("sim/cockpit2/controls/aileron_trim");
    rudder_trim = XPLMFindDataRef("sim/cockpit2/controls/rudder_trim");
    slat_deploy = XPLMFindDataRef("sim/flightmodel/controls/slatrat");
    right_brake_ratio = XPLMFindDataRef("sim/cockpit2/controls/right_brake_ratio");
    left_brake_ratio = XPLMFindDataRef("sim/cockpit2/controls/left_brake_ratio");

    // Control surfaces positions
    left_elevator_pos = XPLMFindDataRef("sim/flightmodel/controls/hstab1_elv1def");
    right_elevator_pos = XPLMFindDataRef("sim/flightmodel/controls/hstab2_elv1def");
    rudder_pos = XPLMFindDataRef("sim/flightmodel/controls/vstab1_rud1def");
    // Aileron position is complex. May depends on the wing parameters in Plane Maker
    // This is working only for Cesna172P in a first step
    right_aileron_pos = XPLMFindDataRef("sim/flightmodel/controls/rail1def");
    left_aileron_pos = XPLMFindDataRef("sim/flightmodel/controls/lail1def");
    // For other A/C
    // TODO: read a aircraft configuration file - values cannot be guessed
    // 4 wings
    left_wing_aileron_1_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1l_ail1def");
    left_wing_aileron_2_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1l_ail2def");
    left_wing_spoiler_1_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1l_spo1def");
    left_wing_spoiler_2_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1l_spo2def");
    right_wing_aileron_1_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1r_ail1def");
    right_wing_aileron_2_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1r_ail2def");
    right_wing_spoiler_1_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1r_spo1def");
    right_wing_spoiler_2_def[0] = XPLMFindDataRef("sim/flightmodel/controls/wing1r_spo2def");
    left_wing_aileron_1_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2l_ail1def");
    left_wing_aileron_2_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2l_ail2def");
    left_wing_spoiler_1_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2l_spo1def");
    left_wing_spoiler_2_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2l_spo2def");
    right_wing_aileron_1_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2r_ail1def");
    right_wing_aileron_2_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2r_ail2def");
    right_wing_spoiler_1_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2r_spo1def");
    right_wing_spoiler_2_def[1] = XPLMFindDataRef("sim/flightmodel/controls/wing2r_spo2def");
    left_wing_aileron_1_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3l_ail1def");
    left_wing_aileron_2_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3l_ail2def");
    left_wing_spoiler_1_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3l_spo1def");
    left_wing_spoiler_2_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3l_spo2def");
    right_wing_aileron_1_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3r_ail1def");
    right_wing_aileron_2_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3r_ail2def");
    right_wing_spoiler_1_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3r_spo1def");
    right_wing_spoiler_2_def[2] = XPLMFindDataRef("sim/flightmodel/controls/wing3r_spo2def");
    left_wing_aileron_1_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4l_ail1def");
    left_wing_aileron_2_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4l_ail2def");
    left_wing_spoiler_1_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4l_spo1def");
    left_wing_spoiler_2_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4l_spo2def");
    right_wing_aileron_1_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4r_ail1def");
    right_wing_aileron_2_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4r_ail2def");
    right_wing_spoiler_1_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4r_spo1def");
    right_wing_spoiler_2_def[3] = XPLMFindDataRef("sim/flightmodel/controls/wing4r_spo2def");
    // Engines and fuel
    num_tanks = XPLMFindDataRef("sim/aircraft/overflow/acf_num_tanks");
    num_engines = XPLMFindDataRef("sim/aircraft/engine/acf_num_engines");
    reverser_deployed = XPLMFindDataRef("sim/cockpit2/annunciators/reverser_deployed");
    oil_pressure = XPLMFindDataRef("sim/cockpit2/annunciators/oil_pressure");
    oil_temperature = XPLMFindDataRef("sim/cockpit2/annunciators/oil_temperature");
    fuel_pressure = XPLMFindDataRef("sim/cockpit2/annunciators/fuel_pressure");
    total_fuel = XPLMFindDataRef("sim/flightmodel/weight/m_fuel_total");
    fuel_quantity = XPLMFindDataRef("sim/cockpit2/fuel/fuel_quantity");
    fuel_capacity = XPLMFindDataRef("sim/aircraft/weight/acf_m_fuel_tot");
    fuel_pumps = XPLMFindDataRef("sim/cockpit/fuel/fuel_pump_on");
	m_total = XPLMFindDataRef("sim/flightmodel/weight/m_total");
    engine_n1 = XPLMFindDataRef("sim/flightmodel/engine/ENGN_N1_");
    engine_egt_percent = XPLMFindDataRef("sim/flightmodel/engine/ENGN_EGT");
    engine_egt_value = XPLMFindDataRef("sim/flightmodel/engine/ENGN_EGT_c");
    engine_max_egt_value = XPLMFindDataRef("sim/aircraft/engine/acf_max_EGT");
    engine_fire_extinguisher = XPLMFindDataRef("sim/cockpit2/engine/actuators/fire_extinguisher_on");
    engine_fadec = XPLMFindDataRef("sim/cockpit2/engine/actuators/fadec_on");
    reverser_ratio = XPLMFindDataRef("sim/flightmodel2/engines/thrust_reverser_deploy_ratio");
    tank_ratio = XPLMFindDataRef("sim/aircraft/overflow/acf_tank_rat");
    engine_n2 = XPLMFindDataRef("sim/flightmodel/engine/ENGN_N2_");
    fuel_flow = XPLMFindDataRef("sim/flightmodel/engine/ENGN_FF_");
    oil_p_ratio = XPLMFindDataRef("sim/flightmodel/engine/ENGN_oil_press");
    oil_p_psi = XPLMFindDataRef("sim/flightmodel/engine/ENGN_oil_press_psi");
    oil_t_ratio = XPLMFindDataRef("sim/flightmodel/engine/ENGN_oil_temp");
    oil_t_c = XPLMFindDataRef("sim/flightmodel/engine/ENGN_oil_temp_c");
    oil_q_ratio = XPLMFindDataRef("sim/cockpit2/engine/indicators/oil_quantity_ratio");
    oil_p_red = XPLMFindDataRef("sim/aircraft/limits/red_hi_oilP");
    oil_t_red = XPLMFindDataRef("sim/aircraft/limits/red_hi_oilT");
    engine_epr_red = XPLMFindDataRef("sim/aircraft/limits/red_hi_EPR");
    ignition_key = XPLMFindDataRef("sim/cockpit2/engine/actuators/ignition_key");
    throttle_ratio = XPLMFindDataRef("sim/cockpit2/engine/actuators/throttle_ratio");
    // for VIB
    vib_running = XPLMFindDataRef("sim/flightmodel/engine/ENGN_running");
    vib_n1_low = XPLMFindDataRef("sim/cockpit/warnings/annunciators/N1_low");
    vib_n1_high = XPLMFindDataRef("sim/cockpit/warnings/annunciators/N1_high");
    vib_reverse = XPLMFindDataRef("sim/cockpit/warnings/annunciators/reverser_on");
    vib_chip = XPLMFindDataRef("sim/cockpit/warnings/annunciators/chip_detected");
    vib_fire = XPLMFindDataRef("sim/cockpit/warnings/annunciators/engine_fires");
    // Hydraulics
    hyd_p_1 = XPLMFindDataRef("sim/operation/failures/hydraulic_pressure_ratio");
    hyd_p_2 = XPLMFindDataRef("sim/operation/failures/hydraulic_pressure_ratio2");
    hyd_q_1 = XPLMFindDataRef("sim/cockpit2/hydraulics/indicators/hydraulic_fluid_ratio_1");
    hyd_q_2 = XPLMFindDataRef("sim/cockpit2/hydraulics/indicators/hydraulic_fluid_ratio_2");
    // TurboProp
    engine_trq_max = XPLMFindDataRef("sim/aircraft/controls/acf_trq_max_eng");
    engine_trq = XPLMFindDataRef("sim/flightmodel/engine/ENGN_TRQ");
    engine_itt = XPLMFindDataRef("sim/flightmodel/engine/ENGN_ITT");
    engine_itt_c = XPLMFindDataRef("sim/flightmodel/engine/ENGN_ITT_c");
    prop_rpm = XPLMFindDataRef("sim/cockpit2/engine/indicators/prop_speed_rpm");
    prop_rpm_max = XPLMFindDataRef("sim/aircraft/controls/acf_RSC_redline_prp");
    prop_mode = XPLMFindDataRef("sim/flightmodel/engine/ENGN_propmode");
    // Piston
    piston_mpr = XPLMFindDataRef("sim/flightmodel/engine/ENGN_MPR");
	// EPR
    engine_epr = XPLMFindDataRef("sim/flightmodel/engine/ENGN_EPR");
    // APU
    apu_n1 = XPLMFindDataRef("sim/cockpit2/electrical/APU_N1_percent");
    apu_gen_amp = XPLMFindDataRef("sim/cockpit2/electrical/APU_generator_amps");
    apu_running = XPLMFindDataRef("sim/cockpit2/electrical/APU_running");
    apu_gen_on = XPLMFindDataRef("sim/cockpit2/electrical/APU_generator_on");
    apu_starter = XPLMFindDataRef("sim/cockpit2/electrical/APU_starter_switch");
    // Electrical
    // Aircraft constants
    acf_batteries  = XPLMFindDataRef("sim/aircraft/electrical/num_batteries");
    acf_buses = XPLMFindDataRef("sim/aircraft/electrical/num_buses integer");
    acf_generators = XPLMFindDataRef("sim/aircraft/electrical/num_generators");
    acf_inverters = XPLMFindDataRef("sim/aircraft/electrical/num_inverters");
    // Batteries
    elec_battery_on = XPLMFindDataRef("sim/cockpit2/electrical/battery_on");
    elec_battery_amps = XPLMFindDataRef("sim/cockpit2/electrical/battery_amps");
    elec_voltage_actual_volts = XPLMFindDataRef("sim/cockpit2/electrical/battery_voltage_actual_volts");
    elec_voltage_indicated_volts = XPLMFindDataRef("sim/cockpit2/electrical/battery_voltage_indicated_volts");
    // Generator
    elec_generator_on = XPLMFindDataRef("sim/cockpit2/electrical/generator_on");
    elec_generator_amps = XPLMFindDataRef("sim/cockpit2/electrical/generator_amps");
    // GPU
    elec_gpu_on = XPLMFindDataRef("sim/cockpit/electrical/gpu_on");
    elec_gpu_amps = XPLMFindDataRef("sim/cockpit/electrical/gpu_amps");
    // Inverters
    elec_inverter_on = XPLMFindDataRef("sim/cockpit2/electrical/inverter_on");
    // Buses
    elec_bus_load_amps = XPLMFindDataRef("sim/cockpit2/electrical/bus_load_amps");
    elec_bus_volts = XPLMFindDataRef("sim/cockpit2/electrical/bus_volts");
    // RAM air turbin
    ram_air_turbin = XPLMFindDataRef("sim/cockpit2/switches/ram_air_turbin_on");
    // Pressurization
    cabin_altitude = XPLMFindDataRef("sim/cockpit2/pressurization/indicators/cabin_altitude_ft");
    cabin_vvi = XPLMFindDataRef("sim/cockpit2/pressurization/indicators/cabin_vvi_fpm");
    cabin_delta_p = XPLMFindDataRef("sim/cockpit2/pressurization/indicators/pressure_diffential_psi");
    pressurization_alt_target = XPLMFindDataRef("sim/cockpit2/pressurization/actuators/cabin_altitude_ft");
    pressurization_vvi_target = XPLMFindDataRef("sim/cockpit2/pressurization/actuators/cabin_vvi_fpm");
    pressurization_mode = XPLMFindDataRef("sim/cockpit2/pressurization/actuators/bleed_air_mode");
    pressurization_dump_all = XPLMFindDataRef("sim/cockpit2/pressurization/actuators/dump_all");
    pressurization_dump_to_alt = XPLMFindDataRef("sim/cockpit2/pressurization/actuators/dump_to_altitude");
    pressurization_max_alt  = XPLMFindDataRef("sim/cockpit2/pressurization/actuators/max_allowable_altitude_ft");
    // Nose Wheel Steering
    nose_wheel_steer_on = XPLMFindDataRef("sim/cockpit2/controls/nosewheel_steer_on");
    tailwheel_lock_ratio = XPLMFindDataRef("sim/cockpit2/controls/tailwheel_lock_ratio");
    tire_steer_command_deg = XPLMFindDataRef("sim/flightmodel2/gear/tire_steer_command_deg");
    tire_steer_actual_deg = XPLMFindDataRef("sim/flightmodel2/gear/tire_steer_actual_deg");
    acf_gear_steers =  XPLMFindDataRef("sim/aircraft/overflow/acf_gear_steers"); // True is gear turn with rudder input
    nose_wheel_steer = XPLMFindDataRef("sim/flightmodel/controls/nose_wheel_steer");
    tire_steer_cmd = XPLMFindDataRef("sim/flightmodel/parts/controls/tire_steer_cmd");
    tire_steer_act = XPLMFindDataRef("sim/flightmodel/parts/controls/tire_steer_act");


//	// TCAS
//	relative_bearing_degs = XPLMFindDataRef("sim/cockpit2/tcas/indicators/relative_bearing_degs");
//	relative_distance_mtrs = XPLMFindDataRef("sim/cockpit2/tcas/indicators/relative_distance_mtrs");
//	relative_altitude_mtrs = XPLMFindDataRef("sim/cockpit2/tcas/indicators/relative_altitude_mtrs");

    // Multiplayer
	for (i=1; i<NUM_TCAS; i++) {
		sprintf(buf, "sim/multiplayer/position/plane%d_x", i);
		multiplayer_x[i] = XPLMFindDataRef(buf);

		sprintf(buf, "sim/multiplayer/position/plane%d_y", i);
		multiplayer_y[i] = XPLMFindDataRef(buf);

		sprintf(buf, "sim/multiplayer/position/plane%d_z", i);
		multiplayer_z[i] = XPLMFindDataRef(buf);
	}

	nav_type_ = XPLMFindDataRef("sim/cockpit/radios/nav_type");


	XPLMDebugString("XHSI: standard DataRefs referenced\n");

}


void writeDataRef(int id, float value) {

//    char info_string[80];
//    sprintf(info_string, "XHSI: received setting : ID=%d  VALUE=%f\n", id, value);
//    XPLMDebugString(info_string);

    switch (id) {

		// general

		case XHSI_STYLE :
			XPLMSetDatai(xhsi_instrument_style, (int)value);
            break;


		// clock

		case SIM_COCKPIT2_CLOCK_TIMER_MODE :
			XPLMSetDatai(clock_timer_mode, (int)value);
            break;

		case XHSI_TIME_UTC_SOURCE :
			XPLMSetDatai(xhsi_utc_selector, (int)value);
            break;

		case XHSI_TIME_ET_RUNNING :
			XPLMSetDatai(xhsi_et_running, (int)value);
            break;

		case SIM_TIME_TOTAL_FLIGHT_TIME_SEC :
			XPLMSetDataf(flight_time_sec,value);
            break;

		case SIM_TIME_SHOW_DATE :
			if (value==1.0f) { XPLMCommandOnce(sim_instruments_timer_show_date); }
			XPLMSetDatai(xhsi_show_date, (int)value);
            break;

        // transponder

        case SIM_COCKPIT_RADIOS_TRANSPONDER_MODE :
            XPLMSetDatai(transponder_mode, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_TRANSPONDER_CODE :
            XPLMSetDatai(transponder_code, (int)value);
            break;


        // OBS

        case SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM :
            XPLMSetDataf(nav1_obs_degm, value);
            break;

        case SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM :
            XPLMSetDataf(nav2_obs_degm, value);
            break;


        // pilot

        case SIM_COCKPIT_SWITCHES_HSI_SELECTOR :
            XPLMSetDatai(hsi_selector, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR :
            XPLMSetDatai(efis_map_range_selector, (int)value);
            if (qpac_ready && qpac_version>150) { XPLMSetDatai(qpac_capt_efis_nd_range, (int)value); }
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR :
            XPLMSetDatai(efis_dme_1_selector, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR :
            XPLMSetDatai(efis_dme_2_selector, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS :
            XPLMSetDatai(efis_shows_tcas, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS :
            XPLMSetDatai(efis_shows_airports, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS :
            XPLMSetDatai(efis_shows_waypoints, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS :
            XPLMSetDatai(efis_shows_vors, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS :
            XPLMSetDatai(efis_shows_ndbs, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER :
            XPLMSetDatai(efis_shows_weather, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_wx_power_switch, (int)value);
            break;

		case XHSI_EFIS_PILOT_DATA :
            XPLMSetDatai(efis_pilot_shows_data, (int)value);
            break;

        case XHSI_EFIS_PILOT_POS :
            XPLMSetDatai(efis_pilot_shows_pos, (int)value);
            break;

        case XHSI_EFIS_PILOT_TERRAIN :
            XPLMSetDatai(efis_pilot_shows_terrain, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_terrain_nd1, (int)value);
            break;

        case XHSI_EFIS_PILOT_VP :
            XPLMSetDatai(efis_pilot_shows_vp, (int)value);
            break;

        case XHSI_EFIS_PILOT_ILS :
            XPLMSetDatai(efis_pilot_shows_ils, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_ils_on_capt, (int)value);
            if (qpac_ready) XPLMSetDatai(jar_a320_neo_ils, (int)value);
            break;

        case XHSI_EFIS_PILOT_TRK_FPA :
            XPLMSetDatai(efis_pilot_track_fpa, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_fcu_hdg_trk, (int)value);
            if (jar_a320_neo_ready) XPLMSetDatai(jar_a320_neo_fcu_hdg_trk, (int)value);
            break;

        case XHSI_EFIS_PILOT_METRIC_ALT :
            XPLMSetDatai(efis_pilot_metric_alt, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_fcu_metric_alt, (int)value);
            if (jar_a320_neo_ready) XPLMSetDatai(jar_a320_neo_fcu_metric_alt, (int)value);
            break;

        case XHSI_EFIS_PILOT_WXR_TILT :
            XPLMSetDataf(efis_pilot_wxr_tilt, value);
            break;

        case XHSI_EFIS_PILOT_WXR_GAIN :
            XPLMSetDataf(efis_pilot_wxr_gain, value);
            break;

        case XHSI_EFIS_PILOT_WXR_MODE :
            XPLMSetDatai(efis_pilot_wxr_mode, (int)value);
            break;

        case XHSI_EFIS_PILOT_WXR_OPT :
        	XPLMSetDatai(efis_pilot_wxr_target,    ((int)value)>>7 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_alert,     ((int)value)>>6 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_narrow,    ((int)value)>>5 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_react,     ((int)value)>>4 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_slave,     ((int)value)>>3 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_auto_tilt, ((int)value)>>2 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_auto_gain, ((int)value)>>1 & 0x01 );
        	XPLMSetDatai(efis_pilot_wxr_test,      ((int)value)    & 0x01 );
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE :
            XPLMSetDatai(efis_map_mode, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE :
            XPLMSetDatai(efis_map_submode, (int)value);
            if (qpac_ready && qpac_version>110) { XPLMSetDatai(qpac_capt_efis_nd_mode, (int)value); }
            break;

        case XHSI_EFIS_PILOT_MAP_ZOOMIN :
            XPLMSetDatai(efis_pilot_map_zoomin, (int)value);
            break;


        // copilot

        case XHSI_EFIS_COPILOT_HSI_SOURCE :
            XPLMSetDatai(copilot_hsi_selector, (int)value);
            break;

        case XHSI_EFIS_COPILOT_MAP_RANGE :
            XPLMSetDatai(efis_copilot_map_range_selector, (int)value);
            if (qpac_ready && qpac_version>150) { XPLMSetDatai(qpac_co_efis_nd_range, (int)value); }
            break;

        case XHSI_EFIS_COPILOT_RADIO1 :
            XPLMSetDatai(efis_copilot_dme_1_selector, (int)value);
            break;

        case XHSI_EFIS_COPILOT_RADIO2 :
            XPLMSetDatai(efis_copilot_dme_2_selector, (int)value);
            break;

        case XHSI_EFIS_COPILOT_TFC :
            XPLMSetDatai(efis_copilot_shows_tcas , (int)value);
            break;

        case XHSI_EFIS_COPILOT_ARPT :
            XPLMSetDatai(efis_copilot_shows_airports , (int)value);
            break;

        case XHSI_EFIS_COPILOT_WPT :
            XPLMSetDatai(efis_copilot_shows_waypoints , (int)value);
            break;

        case XHSI_EFIS_COPILOT_VOR :
            XPLMSetDatai(efis_copilot_shows_vors , (int)value);
            break;

        case XHSI_EFIS_COPILOT_NDB :
            XPLMSetDatai(efis_copilot_shows_ndbs , (int)value);
            break;

        case XHSI_EFIS_COPILOT_DATA :
            XPLMSetDatai(efis_copilot_shows_data, (int)value);
            break;

        case XHSI_EFIS_COPILOT_POS :
            XPLMSetDatai(efis_copilot_shows_pos, (int)value);
            break;

        case XHSI_EFIS_COPILOT_TERRAIN :
            XPLMSetDatai(efis_copilot_shows_terrain, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_terrain_nd2, (int)value);
            break;

        case XHSI_EFIS_COPILOT_VP :
            XPLMSetDatai(efis_copilot_shows_vp, (int)value);
            break;

        case XHSI_EFIS_COPILOT_ILS :
            XPLMSetDatai(efis_copilot_shows_ils, (int)value);
            if (qpac_ready) XPLMSetDatai(qpac_ils_on_fo, (int)value);
            break;

        case XHSI_EFIS_COPILOT_TRK_FPA :
            XPLMSetDatai(efis_copilot_track_fpa, (int)value);
            break;

        case XHSI_EFIS_COPILOT_METRIC_ALT :
            XPLMSetDatai(efis_copilot_metric_alt, (int)value);
            break;

        case XHSI_EFIS_COPILOT_WXR_TILT :
            XPLMSetDataf(efis_copilot_wxr_tilt, value);
            break;

        case XHSI_EFIS_COPILOT_WXR_GAIN :
            XPLMSetDataf(efis_copilot_wxr_gain, value);
            break;

        case XHSI_EFIS_COPILOT_WXR_MODE :
            XPLMSetDatai(efis_copilot_wxr_mode, (int)value);
            break;

        case XHSI_EFIS_COPILOT_WXR_OPT :
        	XPLMSetDatai(efis_copilot_wxr_target,    ((int)value)>>7 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_alert,     ((int)value)>>6 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_narrow,    ((int)value)>>5 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_react,     ((int)value)>>4 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_slave,     ((int)value)>>3 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_auto_tilt, ((int)value)>>2 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_auto_gain, ((int)value)>>1 & 0x01 );
        	XPLMSetDatai(efis_copilot_wxr_test,      ((int)value)    & 0x01 );
            break;

        case XHSI_EFIS_COPILOT_MAP_CTR :
            XPLMSetDatai(efis_copilot_map_mode , (int)value);
            break;

        case XHSI_EFIS_COPILOT_MAP_MODE :
            XPLMSetDatai(efis_copilot_map_submode , (int)value);
            if (qpac_ready && qpac_version>110) { XPLMSetDatai(qpac_co_efis_nd_mode, (int)value); }
            break;

        case XHSI_EFIS_COPILOT_MAP_ZOOMIN :
            XPLMSetDatai(efis_copilot_map_zoomin, (int)value);
            break;

        // EGPWS
        case XHSI_EPGWS_MODES :
        	XPLMSetDatai(egpws_flaps_mode, ((int)value)>>2 & 0x01 );
        	XPLMSetDatai(egpws_gs_mode,    ((int)value)>>1 & 0x01 );
        	XPLMSetDatai(egpws_sys,        ((int)value)    & 0x01 );
            break;

        // EICAS

        case XHSI_ENGINE_TYPE :
            XPLMSetDatai(engine_type , (int)value);
            break;

        case XHSI_EICAS_TRQ_SCALE :
            XPLMSetDatai(trq_scale , (int)value);
            break;

		case XHSI_EICAS_OVERRIDE_TRQ_MAX :
			XPLMSetDataf(override_trq_max , value);
			break;

        case XHSI_FUEL_UNITS :
			XPLMSetDatai(fuel_units , (int)value);
            break;

        case XHSI_TEMP_UNITS :
			XPLMSetDatai(temp_units , (int)value);
            break;

        // MFD

        case XHSI_MFD_MODE :
            XPLMSetDatai(mfd_mode , (int)value);
            break;

        case XHSI_CREW_OXY_PSI :
            XPLMSetDataf(mfd_crew_oxy_psi , value);
            break;

        // CDU

        case XHSI_CDU_SOURCE :
        	XPLMSetDatai(cdu_pilot_source , (int)value & 0x0F);
        	XPLMSetDatai(cdu_copilot_source , ((int)value &0xF0)>>4);
            break;

        case XHSI_CDU_SIDE :
        	XPLMSetDatai(cdu_pilot_side , (int)value & 0x0F);
        	XPLMSetDatai(cdu_copilot_side , ((int)value &0xF0)>>4);
            break;

        // AP

        case SIM_COCKPIT_AUTOPILOT_HEADING_ROLL_MODE :
            XPLMSetDatai(autopilot_heading_roll_mode, (int)value);
            break;

        case SIM_COCKPIT_AUTOPILOT_HEADING_MAG :
            if(x737_ready){
                XPLMSetDataf(x737_HDG_magnhdg, value);
            } else {
                XPLMSetDataf(autopilot_heading_mag, value);
            }
            break;

        case SIM_COCKPIT_AUTOPILOT_AIRSPEED :
            if(x737_ready){
                XPLMSetDataf(x737_MCPSPD_spd, value);
            } else {
                XPLMSetDataf(autopilot_airspeed, value);
            }
            break;

        case SIM_COCKPIT_AUTOPILOT_ALTITUDE :
            if(x737_ready){
                XPLMSetDataf(x737_ALTHLD_baroalt, value);
            } else {
                XPLMSetDataf(autopilot_altitude, value);
            }
            break;

        case SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY :
            if(x737_ready){
                XPLMSetDataf(x737_VS_vvi, value);
            } else {
                XPLMSetDataf(autopilot_vertical_velocity, value);
            }
            break;

        case SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT :
            XPLMSetDataf(baro_pilot, value);
            break;

        case SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE :
        	XPLMSetDatai(autopilot_state, (int) value);
            break;

        case SIM_COCKPIT_AUTOPILOT_MODE :
        	XPLMSetDatai(autopilot_mode, (int) value);
            break;

       // MCP (and other) buttons (special case; don't set datarefs but trigger commands...)
        case SIM_COCKPIT_AUTOPILOT_KEY_PRESS :
             switch ((int)value) {
                // MCP buttons
                case AP_KEY_IS_MACH : //ismach
                    XPLMSetDatai(autopilot_airspeed_is_mach, ! XPLMGetDatai(autopilot_airspeed_is_mach));
                    break;
                case AP_KEY_CMD_A : //athr
                    if(x737_ready){
                        XPLMCommandOnce(x737_cmda_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_fdir_servos_toggle);
                    }
                    break;
                case AP_KEY_SPD_TOGGLE :
                    if(x737_ready){
                        XPLMCommandOnce(x737_mcpspd_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_autothrottle_toggle);
                    }
                    break;
                case AP_KEY_LVL_CHG_TOGGLE :
                    if(x737_ready){
                        XPLMCommandOnce(x737_lvlchange_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_level_change);
                    }
                    break;
                case AP_KEY_HDG_SEL_TOGGLE :
                    if(x737_ready){
                        XPLMCommandOnce(x737_hdgsel_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_heading);
                    }
                    break;
                case AP_KEY_WLV :
                    XPLMCommandOnce(sim_autopilot_wing_leveler);
                    break;
                case AP_KEY_VS_TOGGLE :
                    if(x737_ready){
                        XPLMCommandOnce(x737_vs_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_vertical_speed);
                    }
                    break;
                case AP_KEY_NAV_TOGGLE :
                    if(x737_ready){
                        int navsrc = XPLMGetDatai(hsi_selector);
                        if(navsrc == 2){
                            XPLMCommandOnce(x737_lnav_toggle);
                        } else {
                            XPLMCommandOnce(x737_vorloc_toggle);
                        }
                    } else {
                        XPLMCommandOnce(sim_autopilot_nav);
                    }
                    break;
                case AP_KEY_APPR_TOGGLE :
                    if(x737_ready){
                        XPLMCommandOnce(x737_app_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_approach);
                    }
                    break;
                case AP_KEY_GLIDE_SLOPE :
                    XPLMCommandOnce(sim_autopilot_glide_slope);
                    break;
                case AP_KEY_BACK_COURSE :
                    XPLMCommandOnce(sim_autopilot_back_course);
                    break;
                case AP_KEY_ALT_HOLD_TOGGLE :
                    if(x737_ready){
                        XPLMCommandOnce(x737_althld_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_altitude_hold);
                    }
                    break;
                case AP_KEY_ILS_CAPT_TOGGLE :
                	XPLMSetDatai(efis_pilot_shows_ils, !XPLMGetDatai(efis_pilot_shows_ils));
                	if (qpac_ready) {
                		XPLMSetDatai(qpac_ils_on_capt, !XPLMGetDatai(qpac_ils_on_capt));
                	}
                	break;
                case AP_KEY_ILS_FO_TOGGLE :
                	XPLMSetDatai(efis_copilot_shows_ils, !XPLMGetDatai(efis_copilot_shows_ils));
                	if (qpac_ready) {
                		XPLMSetDatai(qpac_ils_on_fo, !XPLMGetDatai(qpac_ils_on_fo));
                	}
                	break;
                // lights
                case AP_KEY_NAV_LIGHTS_TOGGLE :
                    if(x737_ready){
                        int posLightOld = XPLMGetDatai(x737_position_light_switch);
                        int posLightNew = (posLightOld == 1) || (posLightOld == -1) ? 0 : 1;
                        XPLMSetDatai(x737_position_light_switch, posLightNew);
                    } else {
                        XPLMCommandOnce(sim_lights_nav_lights_toggle);
                    }
                    break;
                case AP_KEY_BEACON_LIGHTS_TOGGLE :
                    if(x737_ready){
                        XPLMSetDatai(x737_beacon_light_switch, !XPLMGetDatai(x737_beacon_light_switch));
                    } else {
                        XPLMCommandOnce(sim_lights_beacon_lights_toggle);
                    }
                    break;
                case AP_KEY_TAXI_LIGHTS_TOGGLE :
                    if(x737_ready){
                        int v = !XPLMGetDatai(x737_taxi_light_switch);
                        XPLMSetDatai(x737_taxi_light_switch, v);
                        XPLMSetDatai(x737_left_turnoff_light_switch, v);
                        XPLMSetDatai(x737_right_turnoff_light_switch, v);
                    } else {
                        XPLMCommandOnce(sim_lights_taxi_lights_toggle);
                    }
                    break;
                case AP_KEY_STROBE_LIGHTS_TOGGLE :
                    if(x737_ready){
                        XPLMSetDatai(x737_position_light_switch, XPLMGetDatai(x737_position_light_switch) == -1 ? 0 : -1);
                    } else {
                        XPLMCommandOnce(sim_lights_strobe_lights_toggle);
                    }
                    break;
                case AP_KEY_LDG_LIGHTS_TOGGLE :
                    if(x737_ready){
                        int v = !XPLMGetDatai(x737_left_fixed_land_light_switch);
                        XPLMSetDatai(x737_left_fixed_land_light_switch, v);
                        XPLMSetDatai(x737_right_fixed_land_light_switch, v);
                        XPLMSetDatai(x737_left_retr_land_light_switch, v * 2);
                        XPLMSetDatai(x737_rigth_retr_land_light_switch, v * 2);
                        XPLMSetDatai(landing_lights_on, v);
                    } else {
                        XPLMCommandOnce(sim_lights_landing_lights_toggle);
                    }
                    break;
                //  flight_controls
                case AP_KEY_LDG_GEAR_TOGGLE :
                    XPLMCommandOnce(sim_flight_controls_landing_gear_toggle);
                    break;
                case AP_KEY_FLAPS_DOWN :
                    XPLMCommandOnce(sim_flight_controls_flaps_down);
                    break;
                case AP_KEY_FLAPS_UP :
                    XPLMCommandOnce(sim_flight_controls_flaps_up);
                    break;
                case AP_KEY_SPD_BREAK_DOWN :
                    XPLMCommandOnce(sim_flight_controls_speed_brakes_down_one);
                    break;
                case AP_KEY_SPD_BREAK_UP :
                    XPLMCommandOnce(sim_flight_controls_speed_brakes_up_one);
                    break;
                case AP_KEY_LDG_GEAR_DOWN :
                    XPLMCommandOnce(sim_flight_controls_landing_gear_down);
                    break;
                case AP_KEY_LDG_GEAR_UP :
                    XPLMCommandOnce(sim_flight_controls_landing_gear_up);
                    break;
				// pitot heat
                case AP_KEY_PITOT_HEAT_TOGGLE :
                    XPLMSetDatai(pitot_heat_on, ! XPLMGetDatai(pitot_heat_on));
                    break;
                // master caution, warning, accept
                case AP_KEY_CLR_MASTER_WARNING :
                	XPLMCommandOnce(sim_annunciator_clear_master_warning);
                	break;
                case AP_KEY_CLR_MASTER_CAUTION :
                	XPLMCommandOnce(sim_annunciator_clear_master_caution);
                	break;
                case AP_KEY_CLR_MASTER_ACCEPT :
                	XPLMCommandOnce(sim_annunciator_clear_master_accept);
                	break;
                // Dual commands
                case AP_KEY_STICK_DUAL :
                	if (xjoymap_ready) XPLMCommandOnce(xjoymap_stick_dual);
                	break;
                case AP_KEY_STICK_CAPT :
                	if (xjoymap_ready) XPLMCommandOnce(xjoymap_stick_capt);
                	break;
                case AP_KEY_STICK_FO :
                	if (xjoymap_ready) XPLMCommandOnce(xjoymap_stick_fo);
                	break;


            }
            break;


        // X-FMC
        case XFMC_KEYPATH :
            XPLMSetDatai(xfmc_keypath_ref, (int)value);
            break;

        // radio freqs
        // COM
        case SIM_COCKPIT_RADIOS_COM1_FREQ_HZ :
            XPLMSetDatai(com1_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_COM1_STDBY_FREQ_HZ :
            XPLMSetDatai(com1_stdby_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_COM2_FREQ_HZ :
            XPLMSetDatai(com2_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ :
            XPLMSetDatai(com2_stdby_freq_hz, (int)value);
            break;

	// COM with 8.33kHz spacing
        case SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_FREQUENCY_HZ_833 :
            XPLMSetDatai(com1_frequency_hz_833, (int)value);
            break;

        case SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_STANDBY_FREQUENCY_HZ_833 :
            XPLMSetDatai(com1_standby_frequency_hz_833, (int)value);
            break;

        case SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_FREQUENCY_HZ_833 :
            XPLMSetDatai(com2_frequency_hz_833, (int)value);
            break;

        case SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_STANDBY_FREQUENCY_HZ_833 :
            XPLMSetDatai(com2_standby_frequency_hz_833, (int)value);
            break;

        // NAV
        case SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ :
            XPLMSetDatai(nav1_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_NAV1_STDBY_FREQ_HZ :
            XPLMSetDatai(nav1_stdby_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ :
            XPLMSetDatai(nav2_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_NAV2_STDBY_FREQ_HZ :
            XPLMSetDatai(nav2_stdby_freq_hz, (int)value);
            break;

        // ADF
        case SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ :
            XPLMSetDatai(adf1_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_ADF1_STDBY_FREQ_HZ :
            XPLMSetDatai(adf1_stdby_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ :
            XPLMSetDatai(adf2_freq_hz, (int)value);
            break;

        case SIM_COCKPIT_RADIOS_ADF2_STDBY_FREQ_HZ :
            XPLMSetDatai(adf2_stdby_freq_hz, (int)value);
            break;

        // flips radios (special case; don't set datarefs but trigger commands...)
        case SIM_COCKPIT_RADIOS_STDBY_FLIP :
            switch ((int)value) {
                case 1 :
                    XPLMCommandOnce(nav1_standy_flip);
                    break;
                case 2 :
                    XPLMCommandOnce(nav2_standy_flip);
                    break;
                case 3 :
                    XPLMCommandOnce(com1_standy_flip);
                    break;
                case 4 :
                    XPLMCommandOnce(com2_standy_flip);
                    break;
                case 5 :
                    XPLMCommandOnce(adf1_standy_flip);
                    break;
                case 6 :
                    XPLMCommandOnce(adf2_standy_flip);
                    break;
                case 7 :
					XPLMCommandOnce(sim_transponder_transponder_ident);
                    break;
            }
            break;

        // control chronograph (special case; don't set datarefs but trigger commands...)
        case XHSI_CHRONOGRAPH_CONTROL :
            switch ((int)value) {
                case 0 :
                    XPLMCommandOnce(chr_start_stop_reset);
                    break;
                case 1 :
                    XPLMCommandOnce(chr_start_stop);
                    break;
                case 2 :
                    XPLMCommandOnce(chr_reset);
                    break;
                case 3 :
                    XPLMCommandOnce(pilot_chrono_stop_reset);
                    break;
                case 4 :
                    XPLMCommandOnce(pilot_chrono_start_stop);
                    break;
                case 5 :
                    XPLMCommandOnce(pilot_chrono_reset);
                    break;
                case 6 :
                    XPLMCommandOnce(copilot_chrono_stop_reset);
                    break;
                case 7 :
                    XPLMCommandOnce(copilot_chrono_start_stop);
                    break;
                case 8 :
                    XPLMCommandOnce(copilot_chrono_reset);
                    break;
            }

            break;

    }

}
