
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

extern XPLMDataRef  vh_ind_fpm;
extern XPLMDataRef  h_ind;

extern XPLMDataRef  avionics_on;
//extern XPLMDataRef  avionics_switch;

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

extern XPLMDataRef nav1_id;
extern XPLMDataRef nav2_id;
extern XPLMDataRef adf1_id;
extern XPLMDataRef adf2_id;
extern XPLMDataRef gps_id;

extern XPLMDataRef  autopilot_state;
extern XPLMDataRef  autopilot_vertical_velocity;
extern XPLMDataRef  autopilot_altitude;
extern XPLMDataRef  autopilot_approach_selector;
extern XPLMDataRef	autopilot_heading_mag;

extern XPLMDataRef	transponder_mode;

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

//// TCAS
//extern XPLMDataRef  relative_bearing_degs;
//extern XPLMDataRef  relative_distance_mtrs;
//extern XPLMDataRef  relative_altitude_mtrs;


// custom datarefs - pilot
extern XPLMDataRef	efis_pilot_shows_stas;
extern XPLMDataRef	efis_pilot_shows_data;
extern XPLMDataRef	efis_pilot_shows_pos;


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


// Multiplayer
extern XPLMDataRef multiplayer_x[NUM_TCAS];
extern XPLMDataRef multiplayer_y[NUM_TCAS];
extern XPLMDataRef multiplayer_z[NUM_TCAS];



// global functions
void	findDataRefs(void);

void    registerPilotDataRefs(void);
void    registerCopilotDataRefs(void);

float   initPilotCallBack(float, float, int, void *);
float   initCopilotCallBack(float, float, int, void *);

void    unregisterPilotDataRefs(void);
void    unregisterCopilotDataRefs(void);

void    writeDataRef(int, float);
