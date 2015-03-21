
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


#include "commands.h"
#include "globals.h"
#include "structs.h"
#include "ids.h"
#include "xfmc.h"
#include "datarefs_x737.h"

#define MSG_ADD_DATAREF 0x01000000           //  Add dataref to DRE message


// variables that will contain references to be used by XPLMGetData...

// custom datarefs - general
XPLMDataRef xhsi_instrument_style;
XPLMDataRef xhsi_rwy_length_min;
XPLMDataRef xhsi_rwy_units;
XPLMDataRef xhsi_rtu_contact_atc;

// custom datarefs - EICAS
XPLMDataRef  engine_type;
XPLMDataRef  trq_scale;
XPLMDataRef  fuel_units;
XPLMDataRef  override_trq_max;

// custom datarefs - MFD
XPLMDataRef  mfd_mode;

// custom datarefs - pilot
XPLMDataRef  efis_pilot_shows_stas;
XPLMDataRef  efis_pilot_shows_data;
XPLMDataRef  efis_pilot_shows_pos;
XPLMDataRef  efis_pilot_da_bug;
XPLMDataRef  efis_pilot_mins_mode;
XPLMDataRef  efis_pilot_map_zoomin;

// custom datarefs - copilot
XPLMDataRef  efis_copilot_map_range_selector;
XPLMDataRef  efis_copilot_dme_1_selector;
XPLMDataRef  efis_copilot_dme_2_selector;
//XPLMDataRef  efis_copilot_shows_weather;
XPLMDataRef  efis_copilot_shows_tcas;
XPLMDataRef  efis_copilot_shows_airports;
XPLMDataRef  efis_copilot_shows_waypoints;
XPLMDataRef  efis_copilot_shows_vors;
XPLMDataRef  efis_copilot_shows_ndbs;
XPLMDataRef  efis_copilot_shows_stas;
XPLMDataRef  efis_copilot_shows_data;
XPLMDataRef  efis_copilot_shows_pos;
XPLMDataRef  efis_copilot_map_mode;
XPLMDataRef  efis_copilot_map_submode;
XPLMDataRef  copilot_hsi_selector;
XPLMDataRef  efis_copilot_da_bug;
XPLMDataRef  efis_copilot_mins_mode;
XPLMDataRef  efis_copilot_map_zoomin;

// standard datarefs
XPLMDataRef  groundspeed;
XPLMDataRef  true_airspeed;
XPLMDataRef  magpsi;
XPLMDataRef  hpath;
XPLMDataRef  latitude;
XPLMDataRef  longitude;
XPLMDataRef  phi;
XPLMDataRef  r;
XPLMDataRef  magvar;
XPLMDataRef  msl;
XPLMDataRef  agl;
XPLMDataRef  theta;
XPLMDataRef  vpath;
XPLMDataRef  alpha;
XPLMDataRef  beta;
XPLMDataRef  on_ground;

//XPLMDataRef  vh_ind_fpm;
//XPLMDataRef  h_ind;
XPLMDataRef  airspeed_pilot;
XPLMDataRef  airspeed_copilot;
XPLMDataRef  altitude_pilot;
XPLMDataRef  altitude_copilot;
XPLMDataRef  vvi_pilot;
XPLMDataRef  vvi_copilot;
XPLMDataRef  sideslip;
XPLMDataRef  ra_bug_pilot;
XPLMDataRef  ra_bug_copilot;
XPLMDataRef  baro_pilot;
XPLMDataRef  baro_copilot;
XPLMDataRef  airspeed_acceleration;

//Instruments failures pilot
XPLMDataRef  sim_op_fail_rel_ss_ahz;
XPLMDataRef  sim_op_fail_rel_ss_alt;
XPLMDataRef  sim_op_fail_rel_ss_asi;
XPLMDataRef  sim_op_fail_rel_ss_dgy;
XPLMDataRef  sim_op_fail_rel_ss_tsi;
XPLMDataRef  sim_op_fail_rel_ss_vvi;
//Instruments failures co-pilot
XPLMDataRef  sim_op_fail_rel_cop_ahz;
XPLMDataRef  sim_op_fail_rel_cop_alt;
XPLMDataRef  sim_op_fail_rel_cop_asi;
XPLMDataRef  sim_op_fail_rel_cop_dgy;
XPLMDataRef  sim_op_fail_rel_cop_tsi;
XPLMDataRef  sim_op_fail_rel_cop_vvi;

XPLMDataRef  avionics_on;
XPLMDataRef  battery_on;
XPLMDataRef  cockpit_lights_on;

XPLMDataRef  beacon_lights_on;
XPLMDataRef  landing_lights_on;
XPLMDataRef  nav_lights_on;
XPLMDataRef  strobe_lights_on;
XPLMDataRef  taxi_light_on;

XPLMDataRef  pitot_heat_on;

XPLMDataRef  nav1_freq_hz;
XPLMDataRef  nav2_freq_hz;
XPLMDataRef  adf1_freq_hz;
XPLMDataRef  adf2_freq_hz;
XPLMDataRef  nav1_dir_degt;
XPLMDataRef  nav2_dir_degt;
XPLMDataRef  adf1_dir_degt;
XPLMDataRef  adf2_dir_degt;
XPLMDataRef  nav1_dme_dist_m;
XPLMDataRef  nav2_dme_dist_m;
XPLMDataRef  adf1_dme_dist_m;
XPLMDataRef  adf2_dme_dist_m;
XPLMDataRef  nav1_obs_degm;
XPLMDataRef  nav2_obs_degm;
XPLMDataRef  nav1_course_degm;
XPLMDataRef  nav2_course_degm;
XPLMDataRef  nav1_cdi;
XPLMDataRef  nav2_cdi;
XPLMDataRef  nav1_hdef_dot;
XPLMDataRef  nav2_hdef_dot;
XPLMDataRef  nav1_fromto;
XPLMDataRef  nav2_fromto;
XPLMDataRef  nav1_vdef_dot;
XPLMDataRef  nav2_vdef_dot;
XPLMDataRef  gps_dir_degt;
XPLMDataRef  gps_dme_dist_m;
XPLMDataRef  gps_course_degtm;
XPLMDataRef  gps_hdef_dot;
XPLMDataRef  gps_fromto;
XPLMDataRef  gps_vdef_dot;

XPLMDataRef  nav1_dme_time_secs;
XPLMDataRef  nav2_dme_time_secs;
XPLMDataRef  gps_dme_time_secs;

// XPLMDataRef  nav1_dme_nm;
// XPLMDataRef  nav2_dme_nm;
// XPLMDataRef  gps_dme_nm;
XPLMDataRef  hsi_dme_nm_pilot;
XPLMDataRef  hsi_dme_nm_copilot;

XPLMDataRef  outer_marker;
XPLMDataRef  middle_marker;
XPLMDataRef  inner_marker;

XPLMDataRef  nav1_stdby_freq_hz;
XPLMDataRef  nav2_stdby_freq_hz;
XPLMDataRef  adf1_stdby_freq_hz;
XPLMDataRef  adf2_stdby_freq_hz;

XPLMDataRef  nav1_id;
XPLMDataRef  nav2_id;
XPLMDataRef  adf1_id;
XPLMDataRef  adf2_id;
//XPLMDataRef  gps_id;

XPLMDataRef  com1_freq_hz;
XPLMDataRef  com1_stdby_freq_hz;
XPLMDataRef  com2_freq_hz;
XPLMDataRef  com2_stdby_freq_hz;

XPLMDataRef  com1_frequency_hz_833;
XPLMDataRef  com1_standby_frequency_hz_833;
XPLMDataRef  com2_frequency_hz_833;
XPLMDataRef  com2_standby_frequency_hz_833;

XPLMDataRef  autopilot_state;
XPLMDataRef  autopilot_vertical_velocity;
XPLMDataRef  autopilot_altitude;
XPLMDataRef  autopilot_approach_selector;
XPLMDataRef  autopilot_heading_mag;
XPLMDataRef  autopilot_airspeed;
XPLMDataRef  autopilot_airspeed_is_mach;
XPLMDataRef  autopilot_fd_pitch;
XPLMDataRef  autopilot_fd_roll;
XPLMDataRef  autopilot_mode;
XPLMDataRef  autopilot_autothrottle_enabled;
XPLMDataRef  autopilot_autothrottle_on;
XPLMDataRef  autopilot_hdg_status;
XPLMDataRef  autopilot_lnav_status;
XPLMDataRef  autopilot_vs_status;
XPLMDataRef  autopilot_spd_status;
XPLMDataRef  autopilot_alt_hold_status;
XPLMDataRef  autopilot_gs_status;
XPLMDataRef  autopilot_vnav_status;
XPLMDataRef  autopilot_toga_status;
XPLMDataRef  autopilot_toga_lateral_status;
XPLMDataRef  autopilot_roll_status;
XPLMDataRef  autopilot_pitch_status;
XPLMDataRef  autopilot_backcourse_status;
XPLMDataRef  autopilot_heading_roll_mode;


XPLMDataRef  transponder_mode;
XPLMDataRef  transponder_code;
XPLMDataRef  transponder_id;


XPLMDataRef	 efis_map_range_selector;
XPLMDataRef	 efis_dme_1_selector;
XPLMDataRef	 efis_dme_2_selector;
XPLMDataRef	 efis_shows_weather;
XPLMDataRef	 efis_shows_tcas;
XPLMDataRef	 efis_shows_airports;
XPLMDataRef	 efis_shows_waypoints;
XPLMDataRef	 efis_shows_vors;
XPLMDataRef	 efis_shows_ndbs;
XPLMDataRef	 efis_map_mode;
XPLMDataRef	 efis_map_submode;
XPLMDataRef	 hsi_selector;

XPLMDataRef	 wind_speed_kt;
XPLMDataRef	 wind_direction_degt;
XPLMDataRef  zulu_time_sec;
XPLMDataRef  local_time_sec;
XPLMDataRef  oat;
XPLMDataRef  sound_speed;
XPLMDataRef  timer_is_running;
XPLMDataRef  elapsed_time_sec;
XPLMDataRef  flight_time_sec;
XPLMDataRef  clock_timer_mode;

XPLMDataRef  acf_vso;
XPLMDataRef  acf_vs;
XPLMDataRef  acf_vfe;
XPLMDataRef  acf_vno;
XPLMDataRef  acf_vne;
XPLMDataRef  acf_mmo;
XPLMDataRef  acf_vle;
XPLMDataRef  speedbrake_equiped;
XPLMDataRef  retractable_gear;


XPLMDataRef  master_caution;
XPLMDataRef  master_warning;
XPLMDataRef  gear_handle;
XPLMDataRef  gear_unsafe;
XPLMDataRef  gear_types;
XPLMDataRef  parkbrake_ratio;
XPLMDataRef  flap_deploy;
XPLMDataRef  flap_handle;
XPLMDataRef  flap_detents;
XPLMDataRef  ap_disc;
XPLMDataRef  low_fuel;
XPLMDataRef  gpws;
XPLMDataRef  ice;
XPLMDataRef  pitot_heat;
XPLMDataRef  stall;
XPLMDataRef  gear_warning;
XPLMDataRef  auto_brake_level;
XPLMDataRef  speedbrake_handle;
XPLMDataRef  speedbrake_ratio;
XPLMDataRef  gear_deploy;
XPLMDataRef  yoke_pitch_ratio;
XPLMDataRef  yoke_roll_ratio;
XPLMDataRef  yoke_hdg_ratio;
XPLMDataRef  elevator_trim;
XPLMDataRef  aileron_trim;
XPLMDataRef  rudder_trim;
XPLMDataRef  slat_deploy;
XPLMDataRef  right_brake_ratio;
XPLMDataRef  left_brake_ratio;

XPLMDataRef  num_tanks;
XPLMDataRef  num_engines;
XPLMDataRef  reverser_deployed;
XPLMDataRef  oil_pressure;
XPLMDataRef  oil_temperature;
XPLMDataRef  fuel_pressure;
XPLMDataRef  total_fuel;
XPLMDataRef  fuel_quantity;
XPLMDataRef  fuel_capacity;
XPLMDataRef  engine_n1;
XPLMDataRef  engine_egt_percent;
XPLMDataRef  engine_egt_value;
XPLMDataRef  reverser_ratio;
XPLMDataRef  tank_ratio;
XPLMDataRef  engine_n2;
XPLMDataRef  fuel_flow;
XPLMDataRef  oil_p_ratio;
XPLMDataRef  oil_t_ratio;
XPLMDataRef  oil_q_ratio;
XPLMDataRef  throttle_ratio;

// for VIB
XPLMDataRef  vib_running;
XPLMDataRef  vib_n1_low;
XPLMDataRef  vib_n1_high;
XPLMDataRef  vib_reverse;
XPLMDataRef  vib_chip;
XPLMDataRef  vib_fire;
// Hydraulics
XPLMDataRef  hyd_p_1;
XPLMDataRef  hyd_p_2;
XPLMDataRef  hyd_q_1;
XPLMDataRef  hyd_q_2;
// TurboProp
XPLMDataRef  engine_trq_max;
XPLMDataRef  engine_trq;
XPLMDataRef  engine_itt;
XPLMDataRef  engine_itt_c;
XPLMDataRef  prop_rpm_max;
XPLMDataRef  prop_rpm;
XPLMDataRef  prop_mode;
// Piston
XPLMDataRef  piston_mpr;
// EPR
XPLMDataRef  engine_epr;

// APU
XPLMDataRef  apu_n1;
XPLMDataRef  apu_gen_amp;
XPLMDataRef  apu_running;
XPLMDataRef  apu_gen_on;
XPLMDataRef  apu_starter;

//// TCAS
//XPLMDataRef relative_bearing_degs;
//XPLMDataRef relative_distance_mtrs;
//XPLMDataRef relative_altitude_mtrs;

// Multiplayer
XPLMDataRef  multiplayer_x[NUM_TCAS];
XPLMDataRef  multiplayer_y[NUM_TCAS];
XPLMDataRef  multiplayer_z[NUM_TCAS];


// for the NAV-Sync button commands (Direct-to-VOR & Sync-LOC/ILS)
XPLMDataRef  nav_type_;



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

// xhsi/rtu/contact_atc
int contact_atc;
int     getContactATC(void* inRefcon)
{
     return contact_atc;
}
void	setContactATC(void* inRefcon, int inValue)
{
      contact_atc = inValue;
}



void registerPilotDataRefs(void) {

    XPLMDebugString("XHSI: registering custom pilot DataRefs\n");

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


    XPLMDebugString("XHSI: custom pilot DataRefs registered\n");

}


void registerCopilotDataRefs(void) {

    XPLMDebugString("XHSI: registering custom copilot DataRefs\n");

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


    // xhsi/nd_copilot/map_ctr
    efis_copilot_map_mode = XPLMRegisterDataAccessor("xhsi/nd_copilot/map_ctr",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
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

    // xhsi/rtu/contact_atc
    xhsi_rtu_contact_atc = XPLMRegisterDataAccessor("xhsi/rtu/contact_atc",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getContactATC, setContactATC,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    XPLMDebugString("XHSI: custom General DataRefs registered\n");

}


void registerEICASDataRefs(void) {

    XPLMDebugString("XHSI: registering custom EICAS DataRefs\n");

    // xhsi/eicas/engine_type
    engine_type = XPLMRegisterDataAccessor("xhsi/eicas/engine_type",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getEICASMode, setEICASMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/eicas/trq_scale
    trq_scale = XPLMRegisterDataAccessor("xhsi/eicas/trq_scale",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getTRQscale, setTRQscale,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/eicas/fuel_units
    fuel_units = XPLMRegisterDataAccessor("xhsi/eicas/fuel_units",
                                        xplmType_Int,      // Integer accessors
                                        1,                                                   // Writable
                                        getFuelUnits, setFuelUnits,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

	// xhsi/eicas/trq_max_lbft
	override_trq_max = XPLMRegisterDataAccessor("xhsi/eicas/trq_max_lbft",
										xplmType_Float,      // Integer accessors
										1,                                                   // Writable
										NULL, NULL,      // No integer accessors
										getOverrideTRQmax, setOverrideTRQmax,      // Float accessors
										NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    XPLMDebugString("XHSI: custom EICAS DataRefs registered\n");

}


void registerMFDDataRefs(void) {

    XPLMDebugString("XHSI: registering custom MFD DataRefs\n");

    // xhsi/mfd/mode
    mfd_mode = XPLMRegisterDataAccessor("xhsi/mfd/mode",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getMFDMode, setMFDMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    XPLMDebugString("XHSI: custom MFD DataRefs registered\n");

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
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/mfd/mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/engine_type");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/trq_scale");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/fuel_units");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/eicas/trq_max_lbft");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/sta");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/data");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/pos");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_pilot/map_zoomin");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_pilot/da_bug");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_pilot/mins_mode");
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
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_ctr");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/nav_source");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/nd_copilot/map_zoomin");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_copilot/da_bug");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/pfd_copilot/mins_mode");
        XPLMSendMessageToPlugin(PluginID, MSG_ADD_DATAREF, (void*)"xhsi/rtu/contact_atc");
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

    // STA on
    XPLMSetDatai(efis_pilot_shows_stas, 1);

    // DATA on
    XPLMSetDatai(efis_pilot_shows_data, 1);

    // POS off
    XPLMSetDatai(efis_pilot_shows_pos, 0);

    // just a default DA
    XPLMSetDatai(efis_pilot_da_bug, 0);

    // mins is radio
    XPLMSetDatai(efis_pilot_mins_mode, 0);

    // normal scale
    XPLMSetDatai(efis_pilot_map_zoomin, 0);


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
    XPLMSetDatai(efis_copilot_map_zoomin, 1);


    XPLMDebugString("XHSI: custom copilot DataRefs initialized\n");

    return 0.0f;
}


float initEICASCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom EICAS DataRefs\n");

    // set some defaults...

    // type 0 = N1 / 1 = EPR / 2 = TRQ / 3 = MAP
    XPLMSetDatai(engine_type, 0);

	// scale 0 = LbFt, 1 = Nm, 2 = %
    XPLMSetDatai(trq_scale, 0);

	// don't override the maximum torque that X-Plane calculates
	XPLMSetDataf(override_trq_max, 0.0f);


    XPLMDebugString("XHSI: custom EICAS DataRefs initialized\n");

    return 0.0f;

}


float initMFDCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    XPLMDebugString("XHSI: initializing custom MFD DataRefs\n");

    // set a default for the MFD mode

    //// xhsi/mfd/mode
    //mfd_mode = XPLMFindDataRef ("xhsi/mfd/mode");
    // mode 0 = Airport Chart / 1 = Flight Plan / 2 = Lower EICAS
    XPLMSetDatai(mfd_mode, 0);

	XPLMDebugString("XHSI: custom MFD DataRefs initialized\n");

    return 0.0f;

}


void unregisterPilotDataRefs(void) {

    // xhsi/nd_pilot/sta
    XPLMUnregisterDataAccessor(efis_pilot_shows_stas);

    // xhsi/nd_pilot/data
    XPLMUnregisterDataAccessor(efis_pilot_shows_data);

    // xhsi/nd_pilot/pos
    XPLMUnregisterDataAccessor(efis_pilot_shows_pos);

    // xhsi/pfd_pilot/da_bug
    XPLMUnregisterDataAccessor(efis_pilot_da_bug);

    // xhsi/pfd_pilot/mins_mode
    XPLMUnregisterDataAccessor(efis_pilot_mins_mode);

    // xhsi/nd_pilot/map_zoomin
    XPLMUnregisterDataAccessor(efis_pilot_map_zoomin);

}


void unregisterCopilotDataRefs(void) {

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

}

void unregisterGeneralDataRefs(void) {

    // xhsi/style
	XPLMUnregisterDataAccessor(xhsi_instrument_style);

    // xhsi/rwy_length_min
    XPLMUnregisterDataAccessor(xhsi_rwy_length_min);

    // xhsi/rwy_units
    XPLMUnregisterDataAccessor(xhsi_rwy_units);

    // xhsi/rtu/contact_atc
    XPLMUnregisterDataAccessor(xhsi_rtu_contact_atc);

}

void unregisterEICASDataRefs(void) {

    // xhsi/eicas/engine_type
    XPLMUnregisterDataAccessor(engine_type);

    // xhsi/eicas/trq_scale
    XPLMUnregisterDataAccessor(trq_scale);

}

void unregisterMFDDataRefs(void) {

    // xhsi/mfd/mode
    XPLMUnregisterDataAccessor(mfd_mode);

}



void findDataRefs(void) {

	int 		i;
	char		buf[100];


    XPLMDebugString("XHSI: referencing standard DataRefs\n");

	// Aircraft position
	groundspeed = XPLMFindDataRef("sim/flightmodel/position/groundspeed");
	true_airspeed = XPLMFindDataRef("sim/flightmodel/position/true_airspeed");
	magpsi = XPLMFindDataRef("sim/flightmodel/position/magpsi");
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

    //Instruments failures pilot
    sim_op_fail_rel_ss_ahz = XPLMFindDataRef("sim/operation/failures/rel_ss_ahz");
    sim_op_fail_rel_ss_alt = XPLMFindDataRef("sim/operation/failures/rel_ss_alt");
    sim_op_fail_rel_ss_asi = XPLMFindDataRef("sim/operation/failures/rel_ss_asi");
    sim_op_fail_rel_ss_dgy = XPLMFindDataRef("sim/operation/failures/rel_ss_dgy");
    sim_op_fail_rel_ss_tsi = XPLMFindDataRef("sim/operation/failures/rel_ss_tsi");
    sim_op_fail_rel_ss_vvi = XPLMFindDataRef("sim/operation/failures/rel_ss_vvi");
    //Instruments failures co-pilot
    sim_op_fail_rel_cop_ahz = XPLMFindDataRef("sim/operation/failures/rel_cop_ahz");
    sim_op_fail_rel_cop_alt = XPLMFindDataRef("sim/operation/failures/rel_cop_alt");
    sim_op_fail_rel_cop_asi = XPLMFindDataRef("sim/operation/failures/rel_cop_asi");
    sim_op_fail_rel_cop_dgy = XPLMFindDataRef("sim/operation/failures/rel_cop_dgy");
    sim_op_fail_rel_cop_tsi = XPLMFindDataRef("sim/operation/failures/rel_cop_tsi");
    sim_op_fail_rel_cop_vvi = XPLMFindDataRef("sim/operation/failures/rel_cop_vvi");

    // Electrical
    avionics_on = XPLMFindDataRef("sim/cockpit/electrical/avionics_on");
    battery_on = XPLMFindDataRef("sim/cockpit/electrical/battery_on");
    cockpit_lights_on = XPLMFindDataRef("sim/cockpit/electrical/cockpit_lights_on");

	// Lights
    beacon_lights_on = XPLMFindDataRef("sim/cockpit/electrical/beacon_lights_on");
    landing_lights_on = XPLMFindDataRef("sim/cockpit/electrical/landing_lights_on");
    nav_lights_on = XPLMFindDataRef("sim/cockpit/electrical/nav_lights_on");
    strobe_lights_on = XPLMFindDataRef("sim/cockpit/electrical/strobe_lights_on");
    taxi_light_on = XPLMFindDataRef("sim/cockpit/electrical/taxi_light_on");

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

//	nav1_dme_nm = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav1_dme_distance_nm");
//	nav2_dme_nm = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav2_dme_distance_nm");
//	gps_dme_nm = XPLMFindDataRef("sim/cockpit2/radios/indicators/gps_dme_distance_nm");
	hsi_dme_nm_pilot = XPLMFindDataRef("sim/cockpit2/radios/indicators/hsi_dme_distance_nm_pilot");
	hsi_dme_nm_copilot = XPLMFindDataRef("sim/cockpit2/radios/indicatorshsi_dme_distance_nm_copilot/");

    outer_marker = XPLMFindDataRef("sim/cockpit2/radios/indicators/outer_marker_lit");
    middle_marker = XPLMFindDataRef("sim/cockpit2/radios/indicators/middle_marker_lit");
    inner_marker = XPLMFindDataRef("sim/cockpit2/radios/indicators/inner_marker_lit");

	nav1_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav1_stdby_freq_hz");  // int
	nav2_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav2_stdby_freq_hz");	// int
	adf1_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf1_stdby_freq_hz");  // int
	adf2_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf2_stdby_freq_hz");  // int

	nav1_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav1_nav_id");
	nav2_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav2_nav_id");
	adf1_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/adf1_nav_id");
	adf2_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/adf2_nav_id");
	//gps_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/gps_nav_id");

	com1_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com1_freq_hz");              // int (x100 MHz)
	com1_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com1_stdby_freq_hz");  // int (x100 MHz)
	com2_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com2_freq_hz");              // int (x100 MHz)
	com2_stdby_freq_hz = XPLMFindDataRef("sim/cockpit/radios/com2_stdby_freq_hz");  // int (x100 MHz)

	com1_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com1_frequency_hz_833");                  // int (x1000 MHz)
	com1_standby_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com1_standby_frequency_hz_833");  // int (x1000 MHz)
	com2_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com2_frequency_hz_833");                  // int (x1000 MHz)
	com2_standby_frequency_hz_833 = XPLMFindDataRef("sim/cockpit2/radios/actuators/com2_standby_frenquency_hz_833"); // int (x1000 MHz)

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
	efis_shows_weather = XPLMFindDataRef("sim/cockpit/switches/EFIFS_shows_weather");		// int
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
	zulu_time_sec = XPLMFindDataRef("sim/time/zulu_time_sec");
	local_time_sec = XPLMFindDataRef("sim/time/local_time_sec");
	oat = XPLMFindDataRef("sim/weather/temperature_ambient_c");
	sound_speed = XPLMFindDataRef("sim/weather/speed_sound_ms");
    timer_is_running = XPLMFindDataRef("sim/time/timer_is_running_sec");
    elapsed_time_sec = XPLMFindDataRef("sim/time/timer_elapsed_time_sec");
    flight_time_sec = XPLMFindDataRef("sim/time/total_flight_time_sec");
	clock_timer_mode = XPLMFindDataRef("sim/cockpit2/clock_timer/timer_mode");


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


    // Controls & annunciators
    master_caution = XPLMFindDataRef("sim/cockpit/warnings/annunciators/master_caution");
    master_warning = XPLMFindDataRef("sim/cockpit/warnings/annunciators/master_warning");
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
    yoke_pitch_ratio = XPLMFindDataRef("sim/cockpit2/controls/yoke_pitch_ratio");
    yoke_roll_ratio = XPLMFindDataRef("sim/cockpit2/controls/yoke_roll_ratio");
    yoke_hdg_ratio = XPLMFindDataRef("sim/cockpit2/controls/yoke_heading_ratio");
    elevator_trim = XPLMFindDataRef("sim/cockpit2/controls/elevator_trim");
    aileron_trim = XPLMFindDataRef("sim/cockpit2/controls/aileron_trim");
    rudder_trim = XPLMFindDataRef("sim/cockpit2/controls/rudder_trim");
    slat_deploy = XPLMFindDataRef("sim/flightmodel/controls/slatrat");
    right_brake_ratio = XPLMFindDataRef("sim/cockpit2/controls/right_brake_ratio");
    left_brake_ratio = XPLMFindDataRef("sim/cockpit2/controls/left_brake_ratio");
	

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
    engine_n1 = XPLMFindDataRef("sim/flightmodel/engine/ENGN_N1_");
    engine_egt_percent = XPLMFindDataRef("sim/flightmodel/engine/ENGN_EGT");
    engine_egt_value = XPLMFindDataRef("sim/flightmodel/engine/ENGN_EGT_c");
    reverser_ratio = XPLMFindDataRef("sim/flightmodel2/engines/thrust_reverser_deploy_ratio");
    tank_ratio = XPLMFindDataRef("sim/aircraft/overflow/acf_tank_rat");
    engine_n2 = XPLMFindDataRef("sim/flightmodel/engine/ENGN_N2_");
    fuel_flow = XPLMFindDataRef("sim/flightmodel/engine/ENGN_FF_");
    oil_p_ratio = XPLMFindDataRef("sim/flightmodel/engine/ENGN_oil_press");
    oil_t_ratio = XPLMFindDataRef("sim/flightmodel/engine/ENGN_oil_temp");
    oil_q_ratio = XPLMFindDataRef("sim/cockpit2/engine/indicators/oil_quantity_ratio");
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

    char info_string[80];
    sprintf(info_string, "XHSI: received setting : ID=%d  VALUE=%f\n", id, value);
    XPLMDebugString(info_string);

    switch (id) {

		// general

		case XHSI_STYLE :
			XPLMSetDatai(xhsi_instrument_style, (int)value);
            break;


		// clock

		case SIM_COCKPIT2_CLOCK_TIMER_MODE :
			XPLMSetDatai(clock_timer_mode, (int)value);
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
            break;

		case XHSI_EFIS_PILOT_DATA :
            XPLMSetDatai(efis_pilot_shows_data, (int)value);
            break;

        case XHSI_EFIS_PILOT_POS :
            XPLMSetDatai(efis_pilot_shows_pos, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE :
            XPLMSetDatai(efis_map_mode, (int)value);
            break;

        case SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE :
            XPLMSetDatai(efis_map_submode, (int)value);
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

        case XHSI_EFIS_COPILOT_MAP_CTR :
            XPLMSetDatai(efis_copilot_map_mode , (int)value);
            break;

        case XHSI_EFIS_COPILOT_MAP_MODE :
            XPLMSetDatai(efis_copilot_map_submode , (int)value);
            break;

        case XHSI_EFIS_COPILOT_MAP_ZOOMIN :
            XPLMSetDatai(efis_copilot_map_zoomin, (int)value);
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


        // MFD

        case XHSI_MFD_MODE :
            XPLMSetDatai(mfd_mode , (int)value);
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


       // MCP (and other) buttons (special case; don't set datarefs but trigger commands...)
        case SIM_COCKPIT_AUTOPILOT_KEY_PRESS :
             switch ((int)value) {
                // MCP buttons
                case 1 : //ismach
                    XPLMSetDatai(autopilot_airspeed_is_mach, ! XPLMGetDatai(autopilot_airspeed_is_mach));
                    break;
                case 2 : //athr
                    if(x737_ready){
                        XPLMCommandOnce(x737_cmda_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_fdir_servos_toggle);
                    }
                    break;
                case 3 :
                    if(x737_ready){
                        XPLMCommandOnce(x737_mcpspd_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_autothrottle_toggle);
                    }
                    break;
                case 4 :
                    if(x737_ready){
                        XPLMCommandOnce(x737_lvlchange_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_level_change);
                    }
                    break;
                case 5 :
                    if(x737_ready){
                        XPLMCommandOnce(x737_hdgsel_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_heading);
                    }
                    break;
                case 6 :
                    if(x737_ready){
                        XPLMCommandOnce(x737_vs_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_vertical_speed);
                    }
                    break;
                case 7 :
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
                case 8 :
                    if(x737_ready){
                        XPLMCommandOnce(x737_app_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_approach);
                    }
                    break;
                case 9 :
                    XPLMCommandOnce(sim_autopilot_glide_slope);
                    break;
                case 10 :
                    XPLMCommandOnce(sim_autopilot_back_course);
                    break;
                case 11 :
                    if(x737_ready){
                        XPLMCommandOnce(x737_althld_toggle);
                    } else {
                        XPLMCommandOnce(sim_autopilot_altitude_hold);
                    }
                    break;
                // lights
                case 20 :
                    if(x737_ready){
                        int posLightOld = XPLMGetDatai(x737_position_light_switch);
                        int posLightNew = (posLightOld == 1) || (posLightOld == -1) ? 0 : 1;
                        XPLMSetDatai(x737_position_light_switch, posLightNew);
                    } else {
                        XPLMCommandOnce(sim_lights_nav_lights_toggle);
                    }
                    break;
                case 21 :
                    if(x737_ready){
                        XPLMSetDatai(x737_beacon_light_switch, !XPLMGetDatai(x737_beacon_light_switch));
                    } else {
                        XPLMCommandOnce(sim_lights_beacon_lights_toggle);
                    }
                    break;
                case 22 :
                    if(x737_ready){
                        int v = !XPLMGetDatai(x737_taxi_light_switch);
                        XPLMSetDatai(x737_taxi_light_switch, v);
                        XPLMSetDatai(x737_left_turnoff_light_switch, v);
                        XPLMSetDatai(x737_right_turnoff_light_switch, v);
                    } else {
                        XPLMCommandOnce(sim_lights_taxi_lights_toggle);
                    }
                    break;
                case 23 :
                    if(x737_ready){
                        XPLMSetDatai(x737_position_light_switch, XPLMGetDatai(x737_position_light_switch) == -1 ? 0 : -1);
                    } else {
                        XPLMCommandOnce(sim_lights_strobe_lights_toggle);
                    }
                    break;
                case 24 :
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
                case 30 :
                    XPLMCommandOnce(sim_flight_controls_landing_gear_toggle);
                    break;
                case 31 :
                    XPLMCommandOnce(sim_flight_controls_flaps_down);
                    break;
                case 32 :
                    XPLMCommandOnce(sim_flight_controls_flaps_up);
                    break;
                case 33 :
                    XPLMCommandOnce(sim_flight_controls_speed_brakes_down_one);
                    break;
                case 34 :
                    XPLMCommandOnce(sim_flight_controls_speed_brakes_up_one);
                    break;
				// pitot heat
                case 35 :
                    XPLMSetDatai(pitot_heat_on, ! XPLMGetDatai(pitot_heat_on));
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
            }
            break;

    }

}
