
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMGraphics.h"
#include "XPLMMenus.h"
#include "XPLMPlanes.h"
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"


#include "globals.h"
#include "ids.h"
#include "structs.h"
#include "datarefs.h"
//#include "display.h"
#include "endianess.h"


// Define global vars
// The data packets =========================================
struct SimDataPacket	sim_packet;
struct FmsDataPacket	fms_packet;
struct TcasDataPacket	tcas_packet;
struct CommandPacket    efis_packet;



void decodeCommandPacket(void) {

    int i, nb;
    long id;
    float float_value;

    nb = (int) custom_ntohl(efis_packet.nb_of_command_points);

	for (i=0; i<nb; i++) {
	    id = custom_ntohl(efis_packet.command_points[i].id);
        float_value = custom_ntohf(efis_packet.command_points[i].value);
        writeDataRef(id, float_value);
	}

}


int createSimPacket(void) {

	int i = 0;
	char nav_id_bytes[4];

	strncpy(sim_packet.packet_id, "SIMD", 4);

	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_GROUNDSPEED);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(groundspeed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(true_airspeed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_MAGPSI);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(magpsi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_HPATH);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(hpath));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_LATITUDE);
	sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatad(latitude) );
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_LONGITUDE);
	sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatad(longitude) );
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_PHI);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(phi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_R);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(r));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_MAGVAR);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(magvar));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_ELEVATION);
	sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatad(msl) );
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_Y_AGL);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(agl));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_POSITION_VH_IND_FPM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(vh_ind_fpm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_FLIGHTMODEL_MISC_H_IND);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(h_ind));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_ELECTRICAL_AVIONICS_ON);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(avionics_on));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(adf1_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(adf2_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf1_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf2_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf1_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf2_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_obs_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_obs_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_course_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_course_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_CDI);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_cdi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_CDI);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_cdi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_hdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_hdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_FROMTO);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_fromto));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_FROMTO);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_fromto));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_vdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_vdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_course_degtm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_HDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_hdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_FROMTO);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gps_fromto));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_VDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_vdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_DME_TIME_SECS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_dme_time_secs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV2_DME_TIME_SECS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_dme_time_secs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_GPS_DME_TIME_SECS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_dme_time_secs));
	i++;

	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID);
	XPLMGetDatab(nav1_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID);
	XPLMGetDatab(nav2_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID);
	XPLMGetDatab(adf1_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID);
	XPLMGetDatab(adf2_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_TRANSPONDER_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(transponder_mode));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_HSI_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(hsi_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_map_range_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_dme_1_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_dme_2_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_weather));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_tcas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_airports));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_waypoints));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_vors));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_ndbs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_map_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_map_submode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_PILOT_STA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_stas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_PILOT_DATA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_data));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_PILOT_POS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_pos));
	i++;

    // copilot
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_HSI_SOURCE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(copilot_hsi_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_MAP_RANGE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_range_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_RADIO1);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_dme_1_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_RADIO2);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_dme_2_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_WXR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_weather));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_TFC);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_tcas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_ARPT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_airports));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_WPT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_waypoints));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_VOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_vors));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_NDB);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_ndbs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_MAP_CTR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_MAP_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_submode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_STA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_stas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_DATA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_data));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) XHSI_EFIS_COPILOT_POS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_pos));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_AUTOPILOT_STATE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_state));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_vertical_velocity));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_AUTOPILOT_ALTITUDE);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_altitude));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_approach_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_AUTOPILOT_HEADING_MAG);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_heading_mag));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_WEATHER_WIND_SPEED_KT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(wind_speed_kt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_WEATHER_WIND_DIRECTION_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(wind_direction_degt));
	i++;

	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_TIME_ZULU_TIME_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(zulu_time_sec));
	i++;
	sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_TIME_LOCAL_TIME_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(local_time_sec));
	i++;


	sim_packet.sim_data_points[i].id = custom_htonl((long) PLUGIN_VERSION_ID);
	sim_packet.sim_data_points[i].value = custom_htonf((float) PLUGIN_VERSION_NUMBER);
	i++;

	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htonl( (long) i );

	// packet size : char[4] + long + ( # * ( long + float) )
	return 8 + i * 8;

}


int createFmsPacket(void) {

	char id[256];
	long altitude;
	float lat;
	float lon;
	//int toc_tod_flag = 0;

	// long nb_of_fms_entries = XPLMCountFMSEntries();
	// This is because in X-Plane V9 there are 500 FMS entries.
	// This will cause the fms_packet size to be larger than can be send using sendto().
	// So force the old X-Plane V8 size  (Sandy Barbour) : 1.0 Beta 7
	// long nb_of_fms_entries = MAX_FMS_ENTRIES_ALLOWED;
	// no, we will take all non-zero entries, but stop when we reach MAX_FMS_ENTRIES_ALLOWED : 1.0 Beta 10

	long displayed_entry;
	long active_entry;
	long nb_of_fms_entries;

	 // the entry number in the FMS
	long i;
	// the entry number in the packet that we send
	// at the end, it will contain the number of non-zero entries that are transmitted
	long n = 0;

	XPLMNavType type;
	XPLMNavRef outRef;

	nb_of_fms_entries = XPLMCountFMSEntries();

	if (nb_of_fms_entries > 0) {

		strncpy(fms_packet.packet_id, "FMSR", 4);

		fms_packet.ete_for_active = custom_htonf(XPLMGetDataf(gps_dme_time_secs));
		fms_packet.groundspeed = custom_htonf(XPLMGetDataf(groundspeed));

		displayed_entry = XPLMGetDisplayedFMSEntry();
		fms_packet.displayed_entry_index = -1; // default : none

		active_entry = XPLMGetDestinationFMSEntry();
		fms_packet.active_entry_index = -1; // default : none

		i = 0;
		while ( n < nb_of_fms_entries && n < MAX_FMS_ENTRIES_ALLOWED && i < MAX_FMS_ENTRIES_POSSIBLE ) {
			XPLMGetFMSEntryInfo(
								i,
								&type,
								id,
								&outRef,
								&altitude,
								&lat,
								&lon);
			if ( lat != 0.0f || lon != 0.0f ) {
				// only send non-zero entries : 1.0 Beta 10
				if (i == displayed_entry) {
					// the corrected entry number for the displayed entry
					fms_packet.displayed_entry_index = custom_htonl( n );
				}
					// the corrected entry number for the active entry
				if (i == active_entry) {
					fms_packet.active_entry_index = custom_htonl( n );
				}

				fms_packet.entries[n].type = custom_htonl( ( long) type );
				if (type == 2048) {
					// this T/C and T/D stuff cannot be precise enough...
					//if (toc_tod_flag == 0) {
					//	strncpy(fms_packet.entries[n].id, "T/C", sizeof(fms_packet.entries[n].id));
					//	toc_tod_flag = 1;
					//} else {
					//  strncpy(fms_packet.entries[n].id, "T/D", sizeof(fms_packet.entries[n].id));
					strncpy(fms_packet.entries[n].id, "Lat/Lon", sizeof(fms_packet.entries[n].id));
					//}
				} else {
					strncpy(fms_packet.entries[n].id, id, sizeof(fms_packet.entries[n].id));
				}
				fms_packet.entries[n].altitude = custom_htonf( (float) altitude );
				fms_packet.entries[n].lat = custom_htonf(lat);
				fms_packet.entries[n].lon = custom_htonf(lon);
				n++;
			}
			i++;
		}

	}

	fms_packet.nb_of_entries = custom_htonl( n );

	// packet size : char[4] + float + float + long + long + long + ( # * ( long + char[8] + float + float + float ) )
	return 24 + n * 24;

}


int createTcasPacket(void) {

	int i;
	double x, y, z, lat, lon, elev;
    int total;
    int active;
    XPLMPluginID who;

	strncpy(tcas_packet.packet_id, "MPAC", 4);

	XPLMCountAircraft(&total, &active, &who);

//	char debug_message[256];
//	sprintf(debug_message, "XHSI: MP Total : %d / Active : %d\n", total, active);
//	XPLMDebugString(debug_message);

    if ( total > 1 ) {

        // active can be one when the multiplayer library is used, so we need total
        // nope, active seems always to be equal to total

        tcas_packet.mp_total = custom_htonl( (long)total );
        tcas_packet.mp_active = custom_htonl( (long)active );

        tcas_packet.radar_altitude = custom_htonf( (float)XPLMGetDatad(agl) * 3.28084f );

        tcas_packet.tcas_entries[0].latitude = custom_htonf( (float)XPLMGetDatad(latitude) );
        tcas_packet.tcas_entries[0].longitude = custom_htonf( (float)XPLMGetDatad(longitude) );
        tcas_packet.tcas_entries[0].elevation = custom_htonf( (float)XPLMGetDatad(msl) * 3.28084f );

        for (i=1; i<total; i++) {
            x = XPLMGetDatad(multiplayer_x[i]);
            y = XPLMGetDatad(multiplayer_y[i]);
            z = XPLMGetDatad(multiplayer_z[i]);
            XPLMLocalToWorld(x, y, z, &lat, &lon, &elev);
            tcas_packet.tcas_entries[i].latitude = custom_htonf( (float)lat );
            tcas_packet.tcas_entries[i].longitude = custom_htonf( (float)lon );
            tcas_packet.tcas_entries[i].elevation = custom_htonf( (float)elev * 3.28084f );
        }

        // packet size : char[4] + long + + long + float + ( # * ( float + float + float ) )
        return 4 + 4 + 4 + 4 + total * 3 * 4;

    } else {
        return 0;
    }

}

