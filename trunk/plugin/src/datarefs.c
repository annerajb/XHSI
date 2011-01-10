
#include <stdio.h>
#include <string.h>
#include <stdlib.h>


#define XPLM200 1

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPWidgets.h"
#include "XPStandardWidgets.h"


#include "globals.h"
#include "structs.h"
#include "ids.h"


// variables that will contain references to be used by XPLMGetData...

// custom datarefs - pilot
XPLMDataRef	efis_pilot_shows_stas;
XPLMDataRef	efis_pilot_shows_data;
XPLMDataRef	efis_pilot_shows_pos;

// custom datarefs - copilot
XPLMDataRef	efis_copilot_map_range_selector;
XPLMDataRef	efis_copilot_dme_1_selector;
XPLMDataRef	efis_copilot_dme_2_selector;
//XPLMDataRef	efis_copilot_shows_weather;
XPLMDataRef	efis_copilot_shows_tcas;
XPLMDataRef	efis_copilot_shows_airports;
XPLMDataRef	efis_copilot_shows_waypoints;
XPLMDataRef	efis_copilot_shows_vors;
XPLMDataRef	efis_copilot_shows_ndbs;
XPLMDataRef	efis_copilot_shows_stas;
XPLMDataRef	efis_copilot_shows_data;
XPLMDataRef	efis_copilot_shows_pos;
XPLMDataRef	efis_copilot_map_mode;
XPLMDataRef	efis_copilot_map_submode;
XPLMDataRef	copilot_hsi_selector;

// standard datarefs
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

XPLMDataRef vh_ind_fpm;
XPLMDataRef h_ind;

XPLMDataRef avionics_on;
XPLMDataRef avionics_switch;

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

XPLMDataRef nav1_id;
XPLMDataRef nav2_id;
XPLMDataRef adf1_id;
XPLMDataRef adf2_id;
XPLMDataRef gps_id;

XPLMDataRef autopilot_state;
XPLMDataRef autopilot_vertical_velocity;
XPLMDataRef autopilot_altitude;
XPLMDataRef autopilot_approach_selector;
XPLMDataRef	autopilot_heading_mag;

XPLMDataRef	transponder_mode;

XPLMDataRef	efis_map_range_selector;
XPLMDataRef	efis_dme_1_selector;
XPLMDataRef	efis_dme_2_selector;
XPLMDataRef	efis_shows_weather;
XPLMDataRef	efis_shows_tcas;
XPLMDataRef	efis_shows_airports;
XPLMDataRef	efis_shows_waypoints;
XPLMDataRef	efis_shows_vors;
XPLMDataRef	efis_shows_ndbs;
XPLMDataRef	efis_map_mode;
XPLMDataRef	efis_map_submode;
XPLMDataRef	hsi_selector;

XPLMDataRef	wind_speed_kt;
XPLMDataRef	wind_direction_degt;
XPLMDataRef zulu_time_sec;
XPLMDataRef local_time_sec;

//// TCAS
//XPLMDataRef relative_bearing_degs;
//XPLMDataRef relative_distance_mtrs;
//XPLMDataRef relative_altitude_mtrs;

// Multiplayer
XPLMDataRef multiplayer_x[NUM_TCAS];
XPLMDataRef multiplayer_y[NUM_TCAS];
XPLMDataRef multiplayer_z[NUM_TCAS];


// custom datarefs for pilot

// xhsi/efis/pilot/sta
int pilot_sta;
int     getPilotSTA(void* inRefcon)
 {
     return pilot_sta;
 }
 void	setPilotSTA(void* inRefcon, int inValue)
 {
      pilot_sta = inValue;
 }

// xhsi/efis/pilot/data
int pilot_data;
int     getPilotDATA(void* inRefcon)
 {
     return pilot_data;
 }
 void	setPilotDATA(void* inRefcon, int inValue)
 {
      pilot_data = inValue;
 }

// xhsi/efis/pilot/pos
int pilot_pos;
int     getPilotPOS(void* inRefcon)
 {
     return pilot_pos;
 }
 void	setPilotPOS(void* inRefcon, int inValue)
 {
      pilot_pos = inValue;
 }


// custom datarefs for copilot

// xhsi/efis/copilot/map_range
int copilot_map_range;
int     getCopilotMapRange(void* inRefcon)
 {
     return copilot_map_range;
 }
 void	setCopilotMapRange(void* inRefcon, int inValue)
 {
      copilot_map_range = inValue;
 }

// xhsi/efis/copilot/radio1
int copilot_radio1;
int     getCopilotRadio1(void* inRefcon)
 {
     return copilot_radio1;
 }
 void	setCopilotRadio1(void* inRefcon, int inValue)
 {
      copilot_radio1 = inValue;
 }

// xhsi/efis/copilot/radio2
int copilot_radio2;
int     getCopilotRadio2(void* inRefcon)
 {
     return copilot_radio2;
 }
 void	setCopilotRadio2(void* inRefcon, int inValue)
 {
      copilot_radio2 = inValue;
 }

// xhsi/efis/copilot/tfc
int copilot_tfc;
int     getCopilotTFC(void* inRefcon)
 {
     return copilot_tfc;
 }
 void	setCopilotTFC(void* inRefcon, int inValue)
 {
      copilot_tfc = inValue;
 }

// xhsi/efis/copilot/arpt
int copilot_arpt;
int     getCopilotARPT(void* inRefcon)
 {
     return copilot_arpt;
 }
 void	setCopilotARPT(void* inRefcon, int inValue)
 {
      copilot_arpt = inValue;
 }

// xhsi/efis/copilot/wpt
int copilot_wpt;
int     getCopilotWPT(void* inRefcon)
 {
     return copilot_wpt;
 }
 void	setCopilotWPT(void* inRefcon, int inValue)
 {
      copilot_wpt = inValue;
 }

// xhsi/efis/copilot/vor
int copilot_vor;
int     getCopilotVOR(void* inRefcon)
 {
     return copilot_vor;
 }
 void	setCopilotVOR(void* inRefcon, int inValue)
 {
      copilot_vor = inValue;
 }

// xhsi/efis/copilot/ndb
int copilot_ndb;
int     getCopilotNDB(void* inRefcon)
 {
     return copilot_ndb;
 }
 void	setCopilotNDB(void* inRefcon, int inValue)
 {
      copilot_ndb = inValue;
 }

// xhsi/efis/copilot/sta
int copilot_sta;
int     getCopilotSTA(void* inRefcon)
 {
     return copilot_sta;
 }
 void	setCopilotSTA(void* inRefcon, int inValue)
 {
      copilot_sta = inValue;
 }

// xhsi/efis/copilot/data
int copilot_data;
int     getCopilotDATA(void* inRefcon)
 {
     return copilot_data;
 }
 void	setCopilotDATA(void* inRefcon, int inValue)
 {
      copilot_data = inValue;
 }

// xhsi/efis/copilot/pos
int copilot_pos;
int     getCopilotPOS(void* inRefcon)
 {
     return copilot_pos;
 }
 void	setCopilotPOS(void* inRefcon, int inValue)
 {
      copilot_pos = inValue;
 }

// xhsi/efis/copilot/map_ctr
int copilot_map_ctr;
int     getCopilotMapCTR(void* inRefcon)
 {
     return copilot_map_ctr;
 }
 void	setCopilotMapCTR(void* inRefcon, int inValue)
 {
      copilot_map_ctr = inValue;
 }

// xhsi/efis/copilot/map_mode
int copilot_map_mode;
int     getCopilotMapMode(void* inRefcon)
 {
     return copilot_map_mode;
 }
 void	setCopilotMapMode(void* inRefcon, int inValue)
 {
      copilot_map_mode = inValue;
 }

// xhsi/efis/copilot/hsi_source
int copilot_hsi_source;
int     getCopilotHSISource(void* inRefcon)
 {
     return copilot_hsi_source;
 }
 void	setCopilotHSISource(void* inRefcon, int inValue)
 {
      copilot_hsi_source = inValue;
 }


void registerPilotDataRefs(void) {

    // xhsi/efis/copilot/sta
    efis_pilot_shows_stas = XPLMRegisterDataAccessor("xhsi/efis/pilot/sta",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotSTA, setPilotSTA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/data
    efis_pilot_shows_data = XPLMRegisterDataAccessor("xhsi/efis/pilot/data",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotDATA, setPilotDATA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/pos
    efis_pilot_shows_pos = XPLMRegisterDataAccessor("xhsi/efis/pilot/pos",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getPilotPOS, setPilotPOS,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    XPLMDebugString("XHSI: custom pilot DataRefs registered\n");

}


void registerCopilotDataRefs(void) {

    // xhsi/efis/copilot/map_range
    efis_copilot_map_range_selector = XPLMRegisterDataAccessor("xhsi/efis/copilot/map_range",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMapRange, setCopilotMapRange,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/efis/copilot/radio1
    efis_copilot_dme_1_selector = XPLMRegisterDataAccessor("xhsi/efis/copilot/radio1",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotRadio1, setCopilotRadio1,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used

    // xhsi/efis/copilot/radio2
    efis_copilot_dme_2_selector = XPLMRegisterDataAccessor("xhsi/efis/copilot/radio2",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotRadio2, setCopilotRadio2,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/tfc
    efis_copilot_shows_tcas = XPLMRegisterDataAccessor("xhsi/efis/copilot/tfc",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotTFC, setCopilotTFC,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/arpt
    efis_copilot_shows_airports = XPLMRegisterDataAccessor("xhsi/efis/copilot/arpt",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotARPT, setCopilotARPT,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/wpt
    efis_copilot_shows_waypoints = XPLMRegisterDataAccessor("xhsi/efis/copilot/wpt",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotWPT, setCopilotWPT,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/vor
    efis_copilot_shows_vors = XPLMRegisterDataAccessor("xhsi/efis/copilot/vor",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotVOR, setCopilotVOR,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/ndb
    efis_copilot_shows_ndbs = XPLMRegisterDataAccessor("xhsi/efis/copilot/ndb",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotNDB, setCopilotNDB,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/sta
    efis_copilot_shows_stas = XPLMRegisterDataAccessor("xhsi/efis/copilot/sta",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotSTA, setCopilotSTA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/data
    efis_copilot_shows_data = XPLMRegisterDataAccessor("xhsi/efis/copilot/data",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotDATA, setCopilotDATA,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/pos
    efis_copilot_shows_pos = XPLMRegisterDataAccessor("xhsi/efis/copilot/pos",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotPOS, setCopilotPOS,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/map_ctr
    efis_copilot_map_mode = XPLMRegisterDataAccessor("xhsi/efis/copilot/map_ctr",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMapCTR, setCopilotMapCTR,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/map_mode
    efis_copilot_map_submode = XPLMRegisterDataAccessor("xhsi/efis/copilot/map_mode",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotMapMode, setCopilotMapMode,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    // xhsi/efis/copilot/hsi_source
    copilot_hsi_selector = XPLMRegisterDataAccessor("xhsi/efis/copilot/hsi_source",
                                        xplmType_Int,                                  // The types we support
                                        1,                                                   // Writable
                                        getCopilotHSISource, setCopilotHSISource,      // Integer accessors
                                        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);                                   // Refcons not used


    XPLMDebugString("XHSI: custom copilot DataRefs registered\n");

}



float initPilotCallBack(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    // set reasonable defaults for the pilot's ND

    // xhsi/efis/pilot/sta
    efis_pilot_shows_stas = XPLMFindDataRef ("xhsi/efis/pilot/sta");
    // STA on
    XPLMSetDatai(efis_pilot_shows_stas, 1);

    // xhsi/efis/pilot/data
    efis_pilot_shows_data = XPLMFindDataRef ("xhsi/efis/pilot/data");
    // DATA on
    XPLMSetDatai(efis_pilot_shows_data, 1);

    // xhsi/efis/pilot/pos
    efis_pilot_shows_pos = XPLMFindDataRef ("xhsi/efis/pilot/pos");
    // POS on
    XPLMSetDatai(efis_pilot_shows_pos, 1);

    XPLMDebugString("XHSI: custom pilot DataRefs initialized\n");

    return 0.0f;
}


float initCopilotCallBack(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

    // set reasonable defaults for the copilot's ND

    // xhsi/efis/copilot/map_range
    efis_copilot_map_range_selector = XPLMFindDataRef ("xhsi/efis/copilot/map_range");
    // map range 40
    XPLMSetDatai(efis_copilot_map_range_selector, 2);

    // xhsi/efis/copilot/radio1
    efis_copilot_dme_1_selector = XPLMFindDataRef ("xhsi/efis/copilot/radio1");
    // radio1 : NAV1
    XPLMSetDatai(efis_copilot_dme_1_selector, 2);

    // xhsi/efis/copilot/radio2
    efis_copilot_dme_2_selector = XPLMFindDataRef ("xhsi/efis/copilot/radio2");
    // radio2 : NAV2
    XPLMSetDatai(efis_copilot_dme_2_selector, 2);

    // xhsi/efis/copilot/tfc
    efis_copilot_shows_tcas = XPLMFindDataRef ("xhsi/efis/copilot/tfc");
    // TFC on
    XPLMSetDatai(efis_copilot_shows_tcas, 1);

    // xhsi/efis/copilot/arpt
    efis_copilot_shows_airports = XPLMFindDataRef ("xhsi/efis/copilot/arpt");
    // ARPT on
    XPLMSetDatai(efis_copilot_shows_airports, 1);

    // xhsi/efis/copilot/wpt
    efis_copilot_shows_waypoints = XPLMFindDataRef ("xhsi/efis/copilot/wpt");
    // WPT on
    XPLMSetDatai(efis_copilot_shows_waypoints, 1);

    // xhsi/efis/copilot/vor
    efis_copilot_shows_vors = XPLMFindDataRef ("xhsi/efis/copilot/vor");
    // VOR on
    XPLMSetDatai(efis_copilot_shows_vors, 1);

    // xhsi/efis/copilot/ndb
    efis_copilot_shows_ndbs = XPLMFindDataRef ("xhsi/efis/copilot/ndb");
    // NDB on
    XPLMSetDatai(efis_copilot_shows_ndbs, 1);

    // xhsi/efis/copilot/sta
    efis_copilot_shows_stas = XPLMFindDataRef ("xhsi/efis/copilot/sta");
    // STA on
    XPLMSetDatai(efis_copilot_shows_stas, 1);

    // xhsi/efis/copilot/data
    efis_copilot_shows_data = XPLMFindDataRef ("xhsi/efis/copilot/data");
    // DATA on
    XPLMSetDatai(efis_copilot_shows_data, 1);

    // xhsi/efis/copilot/pos
    efis_copilot_shows_pos = XPLMFindDataRef ("xhsi/efis/copilot/pos");
    // POS on
    XPLMSetDatai(efis_copilot_shows_pos, 1);

    // xhsi/efis/copilot/map_ctr
    efis_copilot_map_mode = XPLMFindDataRef ("xhsi/efis/copilot/map_ctr");
    // CTR on ! (just to be a little different from the pilot's ND default)
    XPLMSetDatai(efis_copilot_map_mode, 0);

    // xhsi/efis/copilot/map_mode
    efis_copilot_map_submode = XPLMFindDataRef ("xhsi/efis/copilot/map_mode");
    // mode MAP
    XPLMSetDatai(efis_copilot_map_submode, 2);

    // xhsi/efis/copilot/hsi_source
    copilot_hsi_selector = XPLMFindDataRef ("xhsi/efis/copilot/hsi_source");
    // HSI source : NAV2 !
    XPLMSetDatai(copilot_hsi_selector, 1);


    XPLMDebugString("XHSI: custom copilot DataRefs initialized\n");

    return 0.0f;
}


void unregisterPilotDataRefs(void) {

    // xhsi/efis/pilot/sta
    XPLMUnregisterDataAccessor(efis_pilot_shows_stas);

    // xhsi/efis/pilot/data
    XPLMUnregisterDataAccessor(efis_pilot_shows_data);

    // xhsi/efis/pilot/pos
    XPLMUnregisterDataAccessor(efis_pilot_shows_pos);

}


void unregisterCopilotDataRefs(void) {

    // xhsi/efis/copilot/map_range
    XPLMUnregisterDataAccessor(efis_copilot_map_range_selector);

    // xhsi/efis/copilot/radio1
    XPLMUnregisterDataAccessor(efis_copilot_dme_1_selector);

    // xhsi/efis/copilot/radio2
    XPLMUnregisterDataAccessor(efis_copilot_dme_2_selector);

    // xhsi/efis/copilot/tfc
    XPLMUnregisterDataAccessor(efis_copilot_shows_tcas);

    // xhsi/efis/copilot/arpt
    XPLMUnregisterDataAccessor(efis_copilot_shows_airports);

    // xhsi/efis/copilot/wpt
    XPLMUnregisterDataAccessor(efis_copilot_shows_waypoints);

    // xhsi/efis/copilot/vor
    XPLMUnregisterDataAccessor(efis_copilot_shows_vors);

    // xhsi/efis/copilot/ndb
    XPLMUnregisterDataAccessor(efis_copilot_shows_ndbs);

    // xhsi/efis/copilot/sta
    XPLMUnregisterDataAccessor(efis_copilot_shows_stas);

    // xhsi/efis/copilot/data
    XPLMUnregisterDataAccessor(efis_copilot_shows_data);

    // xhsi/efis/copilot/pos
    XPLMUnregisterDataAccessor(efis_copilot_shows_pos);

    // xhsi/efis/copilot/map_ctr
    XPLMUnregisterDataAccessor(efis_copilot_map_mode);

    // xhsi/efis/copilot/map_mode
    XPLMUnregisterDataAccessor(efis_copilot_map_submode);

    // xhsi/efis/copilot/hsi_source
    XPLMUnregisterDataAccessor(copilot_hsi_selector);

}


void findDataRefs(void) {

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


	// Instruments
	vh_ind_fpm = XPLMFindDataRef("sim/flightmodel/position/vh_ind_fpm");
	h_ind = XPLMFindDataRef("sim/flightmodel/misc/h_ind");


    // Electrical
    avionics_on = XPLMFindDataRef("sim/cockpit/electrical/avionics_on");
    avionics_switch = XPLMFindDataRef("sim/cockpit2/switches/avionics_power_on");


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

	nav1_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav1_nav_id");
	nav2_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav2_nav_id");
	adf1_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/adf1_nav_id");
	adf2_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/adf2_nav_id");
	gps_id = XPLMFindDataRef("sim/cockpit2/radios/indicators/gps_nav_id");

	// AP
	autopilot_state = XPLMFindDataRef("sim/cockpit/autopilot/autopilot_state");
	autopilot_vertical_velocity = XPLMFindDataRef("sim/cockpit/autopilot/vertical_velocity");
	autopilot_altitude = XPLMFindDataRef("sim/cockpit/autopilot/altitude");
	autopilot_approach_selector = XPLMFindDataRef("sim/cockpit/autopilot/approach_selector");
	autopilot_heading_mag = XPLMFindDataRef("sim/cockpit/autopilot/heading_mag");


	// Transponder
	transponder_mode = XPLMFindDataRef("sim/cockpit/radios/transponder_mode");	// int


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


//	// TCAS
//	relative_bearing_degs = XPLMFindDataRef("sim/cockpit2/tcas/indicators/relative_bearing_degs");
//	relative_distance_mtrs = XPLMFindDataRef("sim/cockpit2/tcas/indicators/relative_distance_mtrs");
//	relative_altitude_mtrs = XPLMFindDataRef("sim/cockpit2/tcas/indicators/relative_altitude_mtrs");

    // Multiplayer
	int 		i;
	char		buf[100];
	for (i=1; i<NUM_TCAS; i++) {
		sprintf(buf, "sim/multiplayer/position/plane%d_x", i);
		multiplayer_x[i] = XPLMFindDataRef(buf);

		sprintf(buf, "sim/multiplayer/position/plane%d_y", i);
		multiplayer_y[i] = XPLMFindDataRef(buf);

		sprintf(buf, "sim/multiplayer/position/plane%d_z", i);
		multiplayer_z[i] = XPLMFindDataRef(buf);
	}

}


void writeDataRef(int id, float value) {

    char info_string[80];
    sprintf(info_string, "XHSI: received setting : ID=%d  VALUE=%f\n", id, value);
    XPLMDebugString(info_string);

    switch (id) {

        // transponder

        case SIM_COCKPIT_RADIOS_TRANSPONDER_MODE :
            XPLMSetDatai(transponder_mode, (int)value);
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


        // AP HDG

        case SIM_COCKPIT_AUTOPILOT_HEADING_MAG :
            XPLMSetDataf(autopilot_heading_mag, value);
            break;


//        // Avionics power
//
//        case SIM_COCKPIT2_SWITCHES_AVIONICS_POWER_ON :
//            XPLMSetDatai(avionics_switch, (int)value);
//            break;

    }

}
