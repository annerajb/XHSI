#ifndef DATAREFS_H_
#define DATAREFS_H_

// Values for SIM_COCKPIT_AUTOPILOT_KEY_PRESS
// Must be in sync with XPlaneCommand.java
// MCP Buttons
#define AP_KEY_IS_MACH 1
#define AP_KEY_CMD_A 2
#define AP_KEY_SPD_TOGGLE 3
#define AP_KEY_LVL_CHG_TOGGLE 4
#define AP_KEY_HDG_SEL_TOGGLE 5
#define AP_KEY_VS_TOGGLE 6
#define AP_KEY_NAV_TOGGLE 7
#define AP_KEY_APPR_TOGGLE 8
#define AP_KEY_GLIDE_SLOPE 9
#define AP_KEY_BACK_COURSE 10
#define AP_KEY_ALT_HOLD_TOGGLE 11
#define AP_KEY_ILS_CAPT_TOGGLE 12
#define AP_KEY_ILS_FO_TOGGLE 13
#define AP_KEY_WLV 14
// Lights
#define AP_KEY_NAV_LIGHTS_TOGGLE 20
#define AP_KEY_BEACON_LIGHTS_TOGGLE 21
#define AP_KEY_TAXI_LIGHTS_TOGGLE 22
#define AP_KEY_STROBE_LIGHTS_TOGGLE 23
#define AP_KEY_LDG_LIGHTS_TOGGLE 24
// Flight controls
#define AP_KEY_LDG_GEAR_TOGGLE 30
#define AP_KEY_FLAPS_DOWN 31
#define AP_KEY_FLAPS_UP 32
#define AP_KEY_SPD_BREAK_DOWN 33
#define AP_KEY_SPD_BREAK_UP 34
#define AP_KEY_LDG_GEAR_DOWN 35
#define AP_KEY_LDG_GEAR_UP 36
// Master caution, warning, accept
#define AP_KEY_CLR_MASTER_WARNING 50
#define AP_KEY_CLR_MASTER_CAUTION 51
#define AP_KEY_CLR_MASTER_ACCEPT 52
// Systems
#define AP_KEY_PITOT_HEAT_TOGGLE 60
// Dual commands - needs xjoymap-dual plugin
#define AP_KEY_STICK_DUAL 70
#define AP_KEY_STICK_CAPT 71
#define AP_KEY_STICK_FO 72

// global vars
extern XPLMDataRef groundspeed;
extern XPLMDataRef true_airspeed;
extern XPLMDataRef magpsi;
extern XPLMDataRef hpath;
extern XPLMDataRef latitude;
extern XPLMDataRef longitude;
extern XPLMDataRef phi;
extern XPLMDataRef r;
extern XPLMDataRef magvar;
extern XPLMDataRef msl;
extern XPLMDataRef agl;
extern XPLMDataRef theta;
extern XPLMDataRef vpath;
extern XPLMDataRef alpha;
extern XPLMDataRef beta;
extern XPLMDataRef on_ground;

//extern XPLMDataRef vh_ind_fpm;
//extern XPLMDataRef h_ind;
extern XPLMDataRef airspeed_pilot;
extern XPLMDataRef airspeed_copilot;
extern XPLMDataRef altitude_pilot;
extern XPLMDataRef altitude_copilot;
extern XPLMDataRef vvi_pilot;
extern XPLMDataRef vvi_copilot;
extern XPLMDataRef sideslip;
extern XPLMDataRef ra_bug_pilot;
extern XPLMDataRef ra_bug_copilot;
extern XPLMDataRef baro_pilot;
extern XPLMDataRef baro_copilot;
extern XPLMDataRef airspeed_acceleration;
extern XPLMDataRef g_load;
extern XPLMDataRef turnrate_noroll;

//Instruments failures pilot
extern XPLMDataRef sim_op_fail_rel_ss_ahz;
extern XPLMDataRef sim_op_fail_rel_ss_alt;
extern XPLMDataRef sim_op_fail_rel_ss_asi;
extern XPLMDataRef sim_op_fail_rel_ss_dgy;
extern XPLMDataRef sim_op_fail_rel_ss_tsi;
extern XPLMDataRef sim_op_fail_rel_ss_vvi;
//Instruments failures co-pilot
extern XPLMDataRef sim_op_fail_rel_cop_ahz;
extern XPLMDataRef sim_op_fail_rel_cop_alt;
extern XPLMDataRef sim_op_fail_rel_cop_asi;
extern XPLMDataRef sim_op_fail_rel_cop_dgy;
extern XPLMDataRef sim_op_fail_rel_cop_tsi;
extern XPLMDataRef sim_op_fail_rel_cop_vvi;
// Gears failures
extern XPLMDataRef sim_op_fail_rel_gear_ind;
extern XPLMDataRef sim_op_fail_rel_gear_act;
// Brake failures
extern XPLMDataRef sim_op_fail_rel_lbrake;
extern XPLMDataRef sim_op_fail_rel_rbrake;
// Tires blowout
extern XPLMDataRef sim_op_fail_rel_tire1;
extern XPLMDataRef sim_op_fail_rel_tire2;
extern XPLMDataRef sim_op_fail_rel_tire3;
extern XPLMDataRef sim_op_fail_rel_tire4;
extern XPLMDataRef sim_op_fail_rel_tire5;
extern XPLMDataRef sim_op_fail_rel_tire6; // DRE only see tires 1 to 6
extern XPLMDataRef sim_op_fail_rel_tire7;
extern XPLMDataRef sim_op_fail_rel_tire8;
extern XPLMDataRef sim_op_fail_rel_tire9;
extern XPLMDataRef sim_op_fail_rel_tire10;

extern XPLMDataRef avionics_on;
extern XPLMDataRef battery_on;
extern XPLMDataRef cockpit_lights_on;

extern XPLMDataRef beacon_lights_on;
extern XPLMDataRef landing_lights_on;
extern XPLMDataRef nav_lights_on;
extern XPLMDataRef strobe_lights_on;
extern XPLMDataRef taxi_light_on;

extern XPLMDataRef landing_lights;
extern XPLMDataRef nav_lights;
extern XPLMDataRef no_smoking;
extern XPLMDataRef fasten_seat_belts;
extern XPLMDataRef strobe_lights;
extern XPLMDataRef taxi_lights;
extern XPLMDataRef beacon_lights;


extern XPLMDataRef pitot_heat_on;

extern XPLMDataRef nav1_freq_hz;
extern XPLMDataRef nav2_freq_hz;
extern XPLMDataRef adf1_freq_hz;
extern XPLMDataRef adf2_freq_hz;
extern XPLMDataRef nav1_dir_degt;
extern XPLMDataRef nav2_dir_degt;
extern XPLMDataRef adf1_dir_degt;
extern XPLMDataRef adf2_dir_degt;
extern XPLMDataRef nav1_dme_dist_m;
extern XPLMDataRef nav2_dme_dist_m;
extern XPLMDataRef adf1_dme_dist_m;
extern XPLMDataRef adf2_dme_dist_m;
extern XPLMDataRef nav1_obs_degm;
extern XPLMDataRef nav2_obs_degm;
extern XPLMDataRef nav1_course_degm;
extern XPLMDataRef nav2_course_degm;
extern XPLMDataRef nav1_cdi;
extern XPLMDataRef nav2_cdi;
extern XPLMDataRef nav1_hdef_dot;
extern XPLMDataRef nav2_hdef_dot;
extern XPLMDataRef nav1_fromto;
extern XPLMDataRef nav2_fromto;
extern XPLMDataRef nav1_vdef_dot;
extern XPLMDataRef nav2_vdef_dot;
extern XPLMDataRef gps_dir_degt;
extern XPLMDataRef gps_dme_dist_m;
extern XPLMDataRef gps_course_degtm;
extern XPLMDataRef gps_hdef_dot;
extern XPLMDataRef gps_fromto;
extern XPLMDataRef gps_vdef_dot;

extern XPLMDataRef nav1_dme_time_secs;
extern XPLMDataRef nav2_dme_time_secs;
extern XPLMDataRef gps_dme_time_secs;

extern XPLMDataRef gps_has_glideslope;

// extern XPLMDataRef nav1_dme_nm;
// extern XPLMDataRef nav2_dme_nm;
// extern XPLMDataRef gps_dme_nm;
extern XPLMDataRef hsi_dme_nm_pilot;
extern XPLMDataRef hsi_dme_nm_copilot;

extern XPLMDataRef outer_marker;
extern XPLMDataRef middle_marker;
extern XPLMDataRef inner_marker;

extern XPLMDataRef nav1_stdby_freq_hz;
extern XPLMDataRef nav2_stdby_freq_hz;
extern XPLMDataRef adf1_stdby_freq_hz;
extern XPLMDataRef adf2_stdby_freq_hz;

extern XPLMDataRef nav1_id;
extern XPLMDataRef nav2_id;
extern XPLMDataRef adf1_id;
extern XPLMDataRef adf2_id;
extern XPLMDataRef gps_id;

extern XPLMDataRef com1_freq_hz;
extern XPLMDataRef com1_stdby_freq_hz;
extern XPLMDataRef com2_freq_hz;
extern XPLMDataRef com2_stdby_freq_hz;

extern XPLMDataRef com1_frequency_hz_833;
extern XPLMDataRef com1_standby_frequency_hz_833;
extern XPLMDataRef com2_frequency_hz_833;
extern XPLMDataRef com2_standby_frequency_hz_833;

extern XPLMDataRef autopilot_state;
extern XPLMDataRef autopilot_vertical_velocity;
extern XPLMDataRef autopilot_altitude;
extern XPLMDataRef autopilot_approach_selector;
extern XPLMDataRef autopilot_heading_mag;
extern XPLMDataRef autopilot_airspeed;
extern XPLMDataRef autopilot_airspeed_is_mach;
extern XPLMDataRef autopilot_fd_pitch;
extern XPLMDataRef autopilot_fd_roll;
extern XPLMDataRef autopilot_mode;
extern XPLMDataRef autopilot_autothrottle_enabled;
extern XPLMDataRef autopilot_autothrottle_on;
extern XPLMDataRef autopilot_hdg_status;
extern XPLMDataRef autopilot_lnav_status;
extern XPLMDataRef autopilot_vs_status;
extern XPLMDataRef autopilot_spd_status;
extern XPLMDataRef autopilot_alt_hold_status;
extern XPLMDataRef autopilot_gs_status;
extern XPLMDataRef autopilot_vnav_status;
extern XPLMDataRef autopilot_toga_status;
extern XPLMDataRef autopilot_toga_lateral_status;
extern XPLMDataRef autopilot_roll_status;
extern XPLMDataRef autopilot_pitch_status;
extern XPLMDataRef autopilot_backcourse_status;
extern XPLMDataRef autopilot_heading_roll_mode;


extern XPLMDataRef transponder_mode;
extern XPLMDataRef transponder_code;
extern XPLMDataRef transponder_id;


extern XPLMDataRef efis_map_range_selector;
extern XPLMDataRef efis_dme_1_selector;
extern XPLMDataRef efis_dme_2_selector;
extern XPLMDataRef efis_shows_weather;
extern XPLMDataRef efis_shows_tcas;
extern XPLMDataRef efis_shows_airports;
extern XPLMDataRef efis_shows_waypoints;
extern XPLMDataRef efis_shows_vors;
extern XPLMDataRef efis_shows_ndbs;
extern XPLMDataRef efis_map_mode;
extern XPLMDataRef efis_map_submode;
extern XPLMDataRef hsi_selector;


extern XPLMDataRef wind_speed_kt;
extern XPLMDataRef wind_direction_degt;
extern XPLMDataRef zulu_time_sec;
extern XPLMDataRef local_time_sec;
extern XPLMDataRef sim_paused;
extern XPLMDataRef oat;
extern XPLMDataRef tat;
extern XPLMDataRef isa;
extern XPLMDataRef sound_speed;
extern XPLMDataRef timer_is_running;
extern XPLMDataRef elapsed_time_sec;
extern XPLMDataRef flight_time_sec;
extern XPLMDataRef clock_timer_mode;


extern XPLMDataRef acf_vso;
extern XPLMDataRef acf_vs;
extern XPLMDataRef acf_vfe;
extern XPLMDataRef acf_vno;
extern XPLMDataRef acf_vne;
extern XPLMDataRef acf_mmo;
extern XPLMDataRef acf_vle;
extern XPLMDataRef speedbrake_equiped;
extern XPLMDataRef retractable_gear;
extern XPLMDataRef acf_vmca;
extern XPLMDataRef acf_vyse;
extern XPLMDataRef acf_tailnum;


extern XPLMDataRef master_caution;
extern XPLMDataRef master_warning;
extern XPLMDataRef gear_handle;
extern XPLMDataRef gear_unsafe;
extern XPLMDataRef gear_unsafe;
extern XPLMDataRef gear_types;
extern XPLMDataRef parkbrake_ratio;
extern XPLMDataRef flap_deploy;
extern XPLMDataRef flap_handle;
extern XPLMDataRef flap_detents;
extern XPLMDataRef ap_disc;
extern XPLMDataRef low_fuel;
extern XPLMDataRef gpws;
extern XPLMDataRef ice;
extern XPLMDataRef pitot_heat;
extern XPLMDataRef stall;
extern XPLMDataRef gear_warning;
extern XPLMDataRef auto_brake_level;
extern XPLMDataRef speedbrake_handle;
extern XPLMDataRef speedbrake_ratio;
extern XPLMDataRef gear_deploy;
extern XPLMDataRef gear_door_ang;
extern XPLMDataRef gear_door_type;
extern XPLMDataRef gear_door_ext_ang;
extern XPLMDataRef gear_door_ret_ang;
extern XPLMDataRef yoke_pitch_ratio;
extern XPLMDataRef yoke_roll_ratio;
extern XPLMDataRef yoke_hdg_ratio;
extern XPLMDataRef elevator_trim;
extern XPLMDataRef aileron_trim;
extern XPLMDataRef rudder_trim;
extern XPLMDataRef slat_deploy;
extern XPLMDataRef right_brake_ratio;
extern XPLMDataRef left_brake_ratio;



extern XPLMDataRef num_tanks;
extern XPLMDataRef num_engines;
extern XPLMDataRef reverser_deployed;
extern XPLMDataRef oil_pressure;
extern XPLMDataRef oil_temperature;
extern XPLMDataRef fuel_pressure;
extern XPLMDataRef total_fuel;
extern XPLMDataRef fuel_quantity;
extern XPLMDataRef fuel_capacity;
extern XPLMDataRef fuel_pumps;
extern XPLMDataRef m_total;
extern XPLMDataRef engine_n1;
extern XPLMDataRef engine_egt_percent;
extern XPLMDataRef engine_egt_value;
extern XPLMDataRef engine_max_egt_value;
extern XPLMDataRef engine_fire_extinguisher;
extern XPLMDataRef engine_fadec;
extern XPLMDataRef reverser_ratio;
extern XPLMDataRef tank_ratio;
extern XPLMDataRef engine_n2;
extern XPLMDataRef fuel_flow;
extern XPLMDataRef oil_p_ratio;
extern XPLMDataRef oil_p_psi;
extern XPLMDataRef oil_t_ratio;
extern XPLMDataRef oil_t_c;
extern XPLMDataRef oil_q_ratio;
extern XPLMDataRef oil_t_red;
extern XPLMDataRef oil_p_red;
extern XPLMDataRef throttle_ratio;
extern XPLMDataRef engine_epr_red;
extern XPLMDataRef ignition_key;

// for VIB
extern XPLMDataRef vib_running;
extern XPLMDataRef vib_n1_low;
extern XPLMDataRef vib_n1_high;
extern XPLMDataRef vib_reverse;
extern XPLMDataRef vib_chip;
extern XPLMDataRef vib_fire;
// Hydraulics
extern XPLMDataRef hyd_p_1;
extern XPLMDataRef hyd_p_2;
extern XPLMDataRef hyd_q_1;
extern XPLMDataRef hyd_q_2;
// TurboProp
extern XPLMDataRef engine_trq_max;
extern XPLMDataRef engine_trq;
extern XPLMDataRef engine_itt;
extern XPLMDataRef engine_itt_c;
extern XPLMDataRef prop_rpm_max;
extern XPLMDataRef prop_rpm;
extern XPLMDataRef prop_mode;
// Piston
extern XPLMDataRef piston_mpr;
// EPR
extern XPLMDataRef engine_epr;

// APU
extern XPLMDataRef apu_n1;
extern XPLMDataRef apu_gen_amp;
extern XPLMDataRef apu_running;
extern XPLMDataRef apu_gen_on;
extern XPLMDataRef apu_starter;

// Elec systems
// Aircraft constants
extern XPLMDataRef  acf_batteries;
extern XPLMDataRef  acf_buses;
extern XPLMDataRef  acf_generators;
extern XPLMDataRef  acf_inverters;
// Batteries
extern XPLMDataRef  elec_battery_on;
extern XPLMDataRef  elec_battery_amps;
extern XPLMDataRef  elec_voltage_actual_volts;
extern XPLMDataRef  elec_voltage_indicated_volts;
// Generator
extern XPLMDataRef  elec_generator_on;
extern XPLMDataRef  elec_generator_amps;
// GPU
extern XPLMDataRef  elec_gpu_on;
extern XPLMDataRef  elec_gpu_amps;
// Inverters
extern XPLMDataRef  elec_inverter_on;
// Buses
extern XPLMDataRef  elec_bus_load_amps;
extern XPLMDataRef  elec_bus_volts;
// RAM air turbin
extern XPLMDataRef  ram_air_turbin;

// Control surfaces
extern XPLMDataRef  left_elevator_pos;
extern XPLMDataRef  right_elevator_pos;
extern XPLMDataRef  rudder_pos;
extern XPLMDataRef  right_aileron_pos;
extern XPLMDataRef  left_aileron_pos;
// 4 wings - aileron and spoilers
extern XPLMDataRef  left_wing_aileron_1_def[4];
extern XPLMDataRef  left_wing_aileron_2_def[4];
extern XPLMDataRef  left_wing_spoiler_1_def[4];
extern XPLMDataRef  left_wing_spoiler_2_def[4];
extern XPLMDataRef  right_wing_aileron_1_def[4];
extern XPLMDataRef  right_wing_aileron_2_def[4];
extern XPLMDataRef  right_wing_spoiler_1_def[4];
extern XPLMDataRef  right_wing_spoiler_2_def[4];
// Control surfaces limits
extern XPLMDataRef acf_controls_elev_dn;
extern XPLMDataRef acf_controls_elev_up;
extern XPLMDataRef acf_controls_ail_dn;
extern XPLMDataRef acf_controls_ail_up;
extern XPLMDataRef acf_controls_rudder_lr;

// Pressurization
extern XPLMDataRef cabin_altitude;
extern XPLMDataRef cabin_vvi;
extern XPLMDataRef cabin_delta_p;

//// TCAS
//extern XPLMDataRef relative_bearing_degs;
//extern XPLMDataRef relative_distance_mtrs;
//extern XPLMDataRef relative_altitude_mtrs;


// custom datarefs - general
extern XPLMDataRef xhsi_instrument_style;
extern XPLMDataRef xhsi_rwy_length_min;
extern XPLMDataRef xhsi_rwy_units;
//extern XPLMDataRef xhsi_rtu_contact_atc;
extern XPLMDataRef xhsi_rtu_selected_radio;


// custom datarefs - EICAS
extern XPLMDataRef engine_type;
extern XPLMDataRef trq_scale;
extern XPLMDataRef fuel_units;
extern XPLMDataRef temp_units;
extern XPLMDataRef override_trq_max;


// custom datarefs - MFD
extern XPLMDataRef mfd_mode;
extern XPLMDataRef mfd_fuel_used;
extern XPLMDataRef mfd_crew_oxy_psi;

// custom datarefs - CDU
extern XPLMDataRef cdu_pilot_source;
extern XPLMDataRef cdu_copilot_source;
extern XPLMDataRef cdu_pilot_side;
extern XPLMDataRef cdu_copilot_side;

// custom datarefs - pilot
extern XPLMDataRef efis_pilot_shows_stas;
extern XPLMDataRef efis_pilot_shows_data;
extern XPLMDataRef efis_pilot_shows_pos;
extern XPLMDataRef efis_pilot_da_bug;
extern XPLMDataRef efis_pilot_mins_mode;
extern XPLMDataRef efis_pilot_map_zoomin;


// custom datarefs - copilot
extern XPLMDataRef efis_copilot_map_range_selector;
extern XPLMDataRef efis_copilot_dme_1_selector;
extern XPLMDataRef efis_copilot_dme_2_selector;
//extern XPLMDataRef efis_copilot_shows_weather;
extern XPLMDataRef efis_copilot_shows_tcas;
extern XPLMDataRef efis_copilot_shows_airports;
extern XPLMDataRef efis_copilot_shows_waypoints;
extern XPLMDataRef efis_copilot_shows_vors;
extern XPLMDataRef efis_copilot_shows_ndbs;
extern XPLMDataRef efis_copilot_shows_stas;
extern XPLMDataRef efis_copilot_shows_data;
extern XPLMDataRef efis_copilot_shows_pos;
extern XPLMDataRef efis_copilot_map_mode;
extern XPLMDataRef efis_copilot_map_submode;
extern XPLMDataRef copilot_hsi_selector;
extern XPLMDataRef efis_copilot_da_bug;
extern XPLMDataRef efis_copilot_mins_mode;
extern XPLMDataRef efis_copilot_map_zoomin;

// Nose Wheel Steering
extern XPLMDataRef nose_wheel_steer_on;
extern XPLMDataRef tailwheel_lock_ratio;
extern XPLMDataRef tire_steer_command_deg;
extern XPLMDataRef tire_steer_actual_deg;
extern XPLMDataRef acf_gear_steers;
extern XPLMDataRef nose_wheel_steer;
extern XPLMDataRef tire_steer_cmd;
extern XPLMDataRef tire_steer_act;

// Multiplayer
extern XPLMDataRef multiplayer_x[NUM_TCAS];
extern XPLMDataRef multiplayer_y[NUM_TCAS];
extern XPLMDataRef multiplayer_z[NUM_TCAS];


// for the NAV-Sync button commands (Direct-to-VOR & Sync-LOC/ILS)
extern XPLMDataRef nav_type_;


// global functions
void	findDataRefs(void);

void    registerPilotDataRefs(void);
void    registerCopilotDataRefs(void);
void    registerGeneralDataRefs(void);
void    registerEICASDataRefs(void);
void    registerMFDDataRefs(void);
void    registerCDUDataRefs(void);

float   notifyDataRefEditorCallback(float, float, int, void *);

float   initGeneralCallback(float, float, int, void *);
float   initPilotCallback(float, float, int, void *);
float   initCopilotCallback(float, float, int, void *);
float   initEICASCallback(float, float, int, void *);
float   initMFDCallback(float, float, int, void *);
float   initCDUCallback(float, float, int, void *);

void    unregisterPilotDataRefs(void);
void    unregisterCopilotDataRefs(void);
void	unregisterGeneralDataRefs(void);
void    unregisterEICASDataRefs(void);
void    unregisterMFDDataRefs(void);
void    unregisterCDUDataRefs(void);

void    writeDataRef(int, float);

#endif /* DATAREFS_H_ */
