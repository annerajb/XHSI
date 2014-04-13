
// global vars
extern XPLMDataRef  groundspeed;
extern XPLMDataRef  true_airspeed;
extern XPLMDataRef  magpsi;
extern XPLMDataRef  hpath;
extern XPLMDataRef  latitude;
extern XPLMDataRef  longitude;
extern XPLMDataRef  phi;
extern XPLMDataRef  r;
extern XPLMDataRef  magvar;
extern XPLMDataRef  msl;
extern XPLMDataRef  agl;
extern XPLMDataRef  theta;
extern XPLMDataRef  vpath;
extern XPLMDataRef  alpha;
extern XPLMDataRef  beta;
extern XPLMDataRef  on_ground;

//extern XPLMDataRef  vh_ind_fpm;
//extern XPLMDataRef  h_ind;
extern XPLMDataRef  airspeed_pilot;
extern XPLMDataRef  airspeed_copilot;
extern XPLMDataRef  altitude_pilot;
extern XPLMDataRef  altitude_copilot;
extern XPLMDataRef  vvi_pilot;
extern XPLMDataRef  vvi_copilot;
extern XPLMDataRef  sideslip;
extern XPLMDataRef  ra_bug_pilot;
extern XPLMDataRef  ra_bug_copilot;
extern XPLMDataRef  baro_pilot;
extern XPLMDataRef  baro_copilot;
extern XPLMDataRef  airspeed_acceleration;

//Instruments failures pilot
extern XPLMDataRef  sim_op_fail_rel_ss_ahz;
extern XPLMDataRef  sim_op_fail_rel_ss_alt;
extern XPLMDataRef  sim_op_fail_rel_ss_asi;
extern XPLMDataRef  sim_op_fail_rel_ss_dgy;
extern XPLMDataRef  sim_op_fail_rel_ss_tsi;
extern XPLMDataRef  sim_op_fail_rel_ss_vvi;
//Instruments failures co-pilot
extern XPLMDataRef  sim_op_fail_rel_cop_ahz;
extern XPLMDataRef  sim_op_fail_rel_cop_alt;
extern XPLMDataRef  sim_op_fail_rel_cop_asi;
extern XPLMDataRef  sim_op_fail_rel_cop_dgy;
extern XPLMDataRef  sim_op_fail_rel_cop_tsi;
extern XPLMDataRef  sim_op_fail_rel_cop_vvi;

extern XPLMDataRef  avionics_on;
extern XPLMDataRef  battery_on;
extern XPLMDataRef  cockpit_lights_on;

extern XPLMDataRef  beacon_lights_on;
extern XPLMDataRef  landing_lights_on;
extern XPLMDataRef  nav_lights_on;
extern XPLMDataRef  strobe_lights_on;
extern XPLMDataRef  taxi_light_on;

extern XPLMDataRef  pitot_heat_on;

extern XPLMDataRef  nav1_freq_hz;
extern XPLMDataRef  nav2_freq_hz;
extern XPLMDataRef  adf1_freq_hz;
extern XPLMDataRef  adf2_freq_hz;
extern XPLMDataRef  nav1_dir_degt;
extern XPLMDataRef  nav2_dir_degt;
extern XPLMDataRef  adf1_dir_degt;
extern XPLMDataRef  adf2_dir_degt;
extern XPLMDataRef  nav1_dme_dist_m;
extern XPLMDataRef  nav2_dme_dist_m;
extern XPLMDataRef  adf1_dme_dist_m;
extern XPLMDataRef  adf2_dme_dist_m;
extern XPLMDataRef  nav1_obs_degm;
extern XPLMDataRef  nav2_obs_degm;
extern XPLMDataRef  nav1_course_degm;
extern XPLMDataRef  nav2_course_degm;
extern XPLMDataRef  nav1_cdi;
extern XPLMDataRef  nav2_cdi;
extern XPLMDataRef  nav1_hdef_dot;
extern XPLMDataRef  nav2_hdef_dot;
extern XPLMDataRef  nav1_fromto;
extern XPLMDataRef  nav2_fromto;
extern XPLMDataRef  nav1_vdef_dot;
extern XPLMDataRef  nav2_vdef_dot;
extern XPLMDataRef  gps_dir_degt;
extern XPLMDataRef  gps_dme_dist_m;
extern XPLMDataRef  gps_course_degtm;
extern XPLMDataRef  gps_hdef_dot;
extern XPLMDataRef  gps_fromto;
extern XPLMDataRef  gps_vdef_dot;

extern XPLMDataRef  nav1_dme_time_secs;
extern XPLMDataRef  nav2_dme_time_secs;
extern XPLMDataRef  gps_dme_time_secs;

// extern XPLMDataRef  nav1_dme_nm;
// extern XPLMDataRef  nav2_dme_nm;
// extern XPLMDataRef  gps_dme_nm;
extern XPLMDataRef  hsi_dme_nm_pilot;
extern XPLMDataRef  hsi_dme_nm_copilot;

extern XPLMDataRef  outer_marker;
extern XPLMDataRef  middle_marker;
extern XPLMDataRef  inner_marker;

extern XPLMDataRef  nav1_stdby_freq_hz;
extern XPLMDataRef  nav2_stdby_freq_hz;
extern XPLMDataRef  adf1_stdby_freq_hz;
extern XPLMDataRef  adf2_stdby_freq_hz;

extern XPLMDataRef  nav1_id;
extern XPLMDataRef  nav2_id;
extern XPLMDataRef  adf1_id;
extern XPLMDataRef  adf2_id;
//extern XPLMDataRef  gps_id;

extern XPLMDataRef  com1_freq_hz;
extern XPLMDataRef  com1_stdby_freq_hz;
extern XPLMDataRef  com2_freq_hz;
extern XPLMDataRef  com2_stdby_freq_hz;

extern XPLMDataRef  autopilot_state;
extern XPLMDataRef  autopilot_vertical_velocity;
extern XPLMDataRef  autopilot_altitude;
extern XPLMDataRef  autopilot_approach_selector;
extern XPLMDataRef	autopilot_heading_mag;
extern XPLMDataRef	autopilot_airspeed;
extern XPLMDataRef	autopilot_airspeed_is_mach;
extern XPLMDataRef	autopilot_fd_pitch;
extern XPLMDataRef	autopilot_fd_roll;
extern XPLMDataRef  autopilot_mode;
extern XPLMDataRef  autopilot_autothrottle_enabled;
extern XPLMDataRef  autopilot_autothrottle_on;
extern XPLMDataRef  autopilot_hdg_status;
extern XPLMDataRef  autopilot_lnav_status;
extern XPLMDataRef  autopilot_vs_status;
extern XPLMDataRef  autopilot_spd_status;
extern XPLMDataRef  autopilot_alt_hold_status;
extern XPLMDataRef  autopilot_gs_status;
extern XPLMDataRef  autopilot_vnav_status;
extern XPLMDataRef  autopilot_toga_status;
extern XPLMDataRef  autopilot_toga_lateral_status;
extern XPLMDataRef  autopilot_roll_status;
extern XPLMDataRef  autopilot_pitch_status;
extern XPLMDataRef  autopilot_backcourse_status;
extern XPLMDataRef  autopilot_heading_roll_mode;


extern XPLMDataRef	transponder_mode;
extern XPLMDataRef	transponder_code;


extern XPLMDataRef	efis_map_range_selector;
extern XPLMDataRef	efis_dme_1_selector;
extern XPLMDataRef	efis_dme_2_selector;
extern XPLMDataRef	efis_shows_weather;
extern XPLMDataRef	efis_shows_tcas;
extern XPLMDataRef	efis_shows_airports;
extern XPLMDataRef	efis_shows_waypoints;
extern XPLMDataRef	efis_shows_vors;
extern XPLMDataRef	efis_shows_ndbs;
extern XPLMDataRef	efis_map_mode;
extern XPLMDataRef	efis_map_submode;
extern XPLMDataRef	hsi_selector;


extern XPLMDataRef	wind_speed_kt;
extern XPLMDataRef	wind_direction_degt;
extern XPLMDataRef  zulu_time_sec;
extern XPLMDataRef  local_time_sec;
extern XPLMDataRef  oat;
extern XPLMDataRef  sound_speed;
extern XPLMDataRef  timer_is_running;
extern XPLMDataRef  elapsed_time_sec;
extern XPLMDataRef  flight_time_sec;
extern XPLMDataRef  clock_timer_mode;


extern XPLMDataRef  acf_vso;
extern XPLMDataRef  acf_vs;
extern XPLMDataRef  acf_vfe;
extern XPLMDataRef  acf_vno;
extern XPLMDataRef  acf_vne;
extern XPLMDataRef  acf_mmo;
extern XPLMDataRef  acf_vle;


extern XPLMDataRef  master_caution;
extern XPLMDataRef  master_warning;
extern XPLMDataRef  gear_handle;
extern XPLMDataRef  gear_unsafe;
extern XPLMDataRef  gear_unsafe;
extern XPLMDataRef  gear_types;
extern XPLMDataRef  parkbrake_ratio;
extern XPLMDataRef  flap_deploy;
extern XPLMDataRef  flap_handle;
extern XPLMDataRef  flap_detents;
extern XPLMDataRef  ap_disc;
extern XPLMDataRef  low_fuel;
extern XPLMDataRef  gpws;
extern XPLMDataRef  ice;
extern XPLMDataRef  pitot_heat;
extern XPLMDataRef  stall;
extern XPLMDataRef  gear_warning;
extern XPLMDataRef  auto_brake_level;
extern XPLMDataRef  speedbrake_handle;
extern XPLMDataRef  speedbrake_ratio;
extern XPLMDataRef  gear_deploy;
extern XPLMDataRef  yoke_pitch_ratio;
extern XPLMDataRef  yoke_roll_ratio;


extern XPLMDataRef  num_tanks;
extern XPLMDataRef  num_engines;
extern XPLMDataRef  reverser_deployed;
extern XPLMDataRef  oil_pressure;
extern XPLMDataRef  oil_temperature;
extern XPLMDataRef  fuel_pressure;
extern XPLMDataRef  total_fuel;
extern XPLMDataRef  fuel_quantity;
extern XPLMDataRef  engine_n1;
extern XPLMDataRef  engine_egt_percent;
extern XPLMDataRef  engine_egt_value;
extern XPLMDataRef  reverser_ratio;
extern XPLMDataRef  tank_ratio;
extern XPLMDataRef  engine_n2;
extern XPLMDataRef  fuel_flow;
extern XPLMDataRef  oil_p_ratio;
extern XPLMDataRef  oil_t_ratio;
extern XPLMDataRef  oil_q_ratio;
// for VIB
extern XPLMDataRef  vib_running;
extern XPLMDataRef  vib_n1_low;
extern XPLMDataRef  vib_n1_high;
extern XPLMDataRef  vib_reverse;
extern XPLMDataRef  vib_chip;
extern XPLMDataRef  vib_fire;
// Hydraulics
extern XPLMDataRef  hyd_p_1;
extern XPLMDataRef  hyd_p_2;
extern XPLMDataRef  hyd_q_1;
extern XPLMDataRef  hyd_q_2;
// TurboProp
extern XPLMDataRef  engine_trq_max;
extern XPLMDataRef  engine_trq;
extern XPLMDataRef  engine_itt;
extern XPLMDataRef  engine_itt_c;
extern XPLMDataRef  prop_rpm_max;
extern XPLMDataRef  prop_rpm;
extern XPLMDataRef  prop_mode;
// Piston
extern XPLMDataRef  piston_mpr;


//// TCAS
//extern XPLMDataRef  relative_bearing_degs;
//extern XPLMDataRef  relative_distance_mtrs;
//extern XPLMDataRef  relative_altitude_mtrs;


// custom datarefs - general
extern XPLMDataRef  instrument_style;


// custom datarefs - EICAS
extern XPLMDataRef  engine_type;
extern XPLMDataRef  trq_scale;
extern XPLMDataRef  fuel_capacity;


// custom datarefs - MFD
extern XPLMDataRef  mfd_mode;


// custom datarefs - pilot
extern XPLMDataRef	efis_pilot_shows_stas;
extern XPLMDataRef	efis_pilot_shows_data;
extern XPLMDataRef	efis_pilot_shows_pos;
extern XPLMDataRef  efis_pilot_da_bug;
extern XPLMDataRef  efis_pilot_mins_mode;
extern XPLMDataRef  efis_pilot_map_zoomin;


// custom datarefs - copilot
extern XPLMDataRef	efis_copilot_map_range_selector;
extern XPLMDataRef	efis_copilot_dme_1_selector;
extern XPLMDataRef	efis_copilot_dme_2_selector;
//extern XPLMDataRef	efis_copilot_shows_weather;
extern XPLMDataRef	efis_copilot_shows_tcas;
extern XPLMDataRef	efis_copilot_shows_airports;
extern XPLMDataRef	efis_copilot_shows_waypoints;
extern XPLMDataRef	efis_copilot_shows_vors;
extern XPLMDataRef	efis_copilot_shows_ndbs;
extern XPLMDataRef	efis_copilot_shows_stas;
extern XPLMDataRef	efis_copilot_shows_data;
extern XPLMDataRef	efis_copilot_shows_pos;
extern XPLMDataRef	efis_copilot_map_mode;
extern XPLMDataRef	efis_copilot_map_submode;
extern XPLMDataRef	copilot_hsi_selector;
extern XPLMDataRef  efis_copilot_da_bug;
extern XPLMDataRef  efis_copilot_mins_mode;
extern XPLMDataRef  efis_copilot_map_zoomin;


// Multiplayer
extern XPLMDataRef multiplayer_x[NUM_TCAS];
extern XPLMDataRef multiplayer_y[NUM_TCAS];
extern XPLMDataRef multiplayer_z[NUM_TCAS];


// for the NAV-Sync button commands (Direct-to-VOR & Sync-LOC/ILS)
extern XPLMDataRef  nav_type_;


// global functions
void	findDataRefs(void);

void    registerPilotDataRefs(void);
void    registerCopilotDataRefs(void);
void    registerGeneralDataRefs(void);
void    registerEICASDataRefs(void);
void    registerMFDDataRefs(void);

float   notifyDataRefEditorCallback(float, float, int, void *);

float   initPilotCallback(float, float, int, void *);
float   initCopilotCallback(float, float, int, void *);
float   initEICASCallback(float, float, int, void *);
float   initMFDCallback(float, float, int, void *);

void    unregisterPilotDataRefs(void);
void    unregisterCopilotDataRefs(void);
void	unregisterGeneralDataRefs(void);
void    unregisterEICASDataRefs(void);
void    unregisterMFDDataRefs(void);

void    writeDataRef(int, float);
