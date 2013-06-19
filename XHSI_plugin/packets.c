
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

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
#include "datarefs_ufmc.h"
#include "datarefs_x737.h"
#include "datarefs_cl30.h"
#include "endianess.h"


// Define global vars
// The data packets =========================================
struct SimDataPacket	sim_packet;
struct FmsDataPacket	fms_packet[10];
struct TcasDataPacket	tcas_packet;
struct IncomingPacket   efis_packet;

int max_packet_size = 0;
char msg[200];


float fake_float(int integer) {

    float fake;
    unsigned char *s1 = (unsigned char *) &integer;
    unsigned char *s2 = (unsigned char *) &fake;
    s2[0] = s1[0];
    s2[1] = s1[1];
    s2[2] = s1[2];
    s2[3] = s1[3];

    return fake;
}


void decodeIncomingPacket(void) {

    int i, nb;
    int id;
    float float_value;

    nb = custom_ntohi(efis_packet.nb_of_data_points);

	for (i=0; i<nb; i++) {
	    id = custom_ntohi(efis_packet.data_points[i].id);
        float_value = custom_ntohf(efis_packet.data_points[i].value);
        writeDataRef(id, float_value);
	}

}


int createADCPacket(void) {

	int i = 0;
	int packet_size;
	int g;
	int gears;
	int gear_type[10];
	float gear_ratio[10];

	strncpy(sim_packet.packet_id, "ADCD", 4);

	sim_packet.sim_data_points[i].id = custom_htoni(PLUGIN_VERSION_ID);
	sim_packet.sim_data_points[i].value = custom_htonf((float) PLUGIN_VERSION_NUMBER);
	i++;


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_GROUNDSPEED);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(groundspeed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(true_airspeed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_MAGPSI);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(magpsi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_HPATH);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(hpath));
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_LATITUDE);
	sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatad(latitude) );
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_LONGITUDE);
	sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatad(longitude) );
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_PHI);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(phi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_R);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(r));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_MAGVAR);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(magvar));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_ELEVATION);
	sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatad(msl) );
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_Y_AGL);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(agl));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_THETA);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(theta));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_VPATH);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(vpath));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_ALPHA);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(alpha));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_BETA);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(beta));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_FAILURES_ONGROUND_ANY);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(on_ground));
	i++;


//	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_POSITION_VH_IND_FPM);
//	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(vh_ind_fpm));
//	i++;
//	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_MISC_H_IND);
//	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(h_ind));
//	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_PILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(airspeed_pilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_COPILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(airspeed_copilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_PILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(altitude_pilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_COPILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(altitude_copilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_PILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(vvi_pilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_COPILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(vvi_copilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_SIDESLIP_DEGREES );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(sideslip));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_PILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ra_bug_pilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_COPILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ra_bug_copilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(baro_pilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(baro_copilot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_ACCELERATION );
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(airspeed_acceleration));
	i++;


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_WEATHER_WIND_SPEED_KT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(wind_speed_kt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_WEATHER_WIND_DIRECTION_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(wind_direction_degt));
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_TIME_ZULU_TIME_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(zulu_time_sec));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_TIME_LOCAL_TIME_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(local_time_sec));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_WEATHER_TEMPERATURE_AMBIENT_C);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(oat));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_WEATHER_SPEED_SOUND_MS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(sound_speed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_TIME_TIMER_IS_RUNNING_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(timer_is_running));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_TIME_TIMER_ELAPSED_TIME_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(elapsed_time_sec));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_TIME_TOTAL_FLIGHT_TIME_SEC);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(flight_time_sec));
	i++;


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(master_caution));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(master_warning));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_GEAR_HANDLE_DOWN );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gear_handle));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_GEAR_UNSAFE );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gear_unsafe));
	i++;

    XPLMGetDatavi(gear_types, gear_type, 0, 10);
    gears = 0;
    while ( ( gears < 10 ) && ( gear_type[gears] > 1 ) )
    {
        gears++;
    }
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_AIRCRAFT_GEAR_COUNT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) gears);
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(parkbrake_ratio));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_FLAP_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(flap_deploy));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_FLAP_HANDLE_DEPLOY_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(flap_handle));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACF_FLAP_DETENTS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(flap_detents));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_AUTOPILOT_DISCONNECT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(ap_disc));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(low_fuel));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_GPWS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gpws));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_ICE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(ice));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_PITOT_HEAT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(pitot_heat));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_STALL_WARNING);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(stall));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_GEAR_WARNING);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gear_warning));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(auto_brake_level));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_SPEEDBRAKE_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(speedbrake_handle));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL2_CONTROLS_SPEEDBRAKE_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(speedbrake_ratio));
	i++;

    XPLMGetDatavf(gear_deploy, gear_ratio, 0, gears);
	for (g=0; g<gears; g++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL2_GEAR_DEPLOY_RATIO_ + g);
        sim_packet.sim_data_points[i].value = custom_htonf( gear_ratio[g] );
        i++;
	}


    sim_packet.sim_data_points[i].id = custom_htoni(CL30_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) cl30_ready);
    i++;
    if ( cl30_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(CL30_MAST_WARN);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cl30_mast_warn));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_MAST_CAUT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cl30_mast_caut));
        i++;

    }


	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );


	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size (ADCD): %d\n", max_packet_size);
        XPLMDebugString(msg);
    }
	return packet_size;

}


int createAvionicsPacket(void) {

	int i = 0;
	int packet_size;
	char nav_id_bytes[4];
//	float gear_ratio[10];

	strncpy(sim_packet.packet_id, "AVIO", 4);

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_AVIONICS_ON);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(avionics_on));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_BATTERY_ON);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(battery_on));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cockpit_lights_on));
	i++;


    if ( x737_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(X737_STBY_PWR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_stby_pwr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_PWR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_PFD_pwr));
        i++;

    }


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(adf1_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(adf2_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf1_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf2_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf1_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(adf2_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_obs_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_obs_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_course_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_course_degm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_CDI);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_cdi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_CDI);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_cdi));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_hdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_hdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_FROMTO);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_fromto));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_FROMTO);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_fromto));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_vdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_vdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_DIR_DEGT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_dir_degt));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_DME_DIST_M);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_dme_dist_m));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_course_degtm));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_HDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_hdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_FROMTO);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gps_fromto));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_VDEF_DOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_vdef_dot));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_DME_TIME_SECS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav1_dme_time_secs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_DME_TIME_SECS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(nav2_dme_time_secs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_DME_TIME_SECS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(gps_dme_time_secs));
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_OUTER_MARKER_LIT );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(outer_marker));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_MIDDLE_MARKER_LIT );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(middle_marker));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_INNER_MARKER_LIT );
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(inner_marker));
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV1_STDBY_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav1_stdby_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_NAV2_STDBY_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(nav2_stdby_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF1_STDBY_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(adf1_stdby_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_ADF2_STDBY_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(adf2_stdby_freq_hz));
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID);
	XPLMGetDatab(nav1_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID);
	XPLMGetDatab(nav2_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID);
	XPLMGetDatab(adf1_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID);
	XPLMGetDatab(adf2_id, nav_id_bytes, 0, 4);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_COM1_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com1_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_COM1_STDBY_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com1_stdby_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_COM2_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com2_freq_hz));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com2_stdby_freq_hz));
	i++;


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_TRANSPONDER_MODE);
	sim_packet.sim_data_points[i].value =  custom_htonf((float) XPLMGetDatai(transponder_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_TRANSPONDER_CODE);
	sim_packet.sim_data_points[i].value =  custom_htonf((float) XPLMGetDatai(transponder_code));
	i++;


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_HSI_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(hsi_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_map_range_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_dme_1_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_dme_2_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_weather));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_tcas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_airports));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_waypoints));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_vors));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_ndbs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_map_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_map_submode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_STA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_stas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_DATA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_data));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_POS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_pos));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_DA_BUG);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_da_bug));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_MINS_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_mins_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_MAP_ZOOMIN);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_map_zoomin));
	i++;

    // copilot
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_HSI_SOURCE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(copilot_hsi_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_MAP_RANGE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_range_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_RADIO1);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_dme_1_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_RADIO2);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_dme_2_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_WXR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_shows_weather));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_TFC);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_tcas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_ARPT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_airports));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_WPT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_waypoints));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_VOR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_vors));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_NDB);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_ndbs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_MAP_CTR);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_MAP_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_submode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_STA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_stas));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_DATA);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_data));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_POS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_pos));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_DA_BUG);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_da_bug));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_MINS_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_mins_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_MAP_ZOOMIN);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_map_zoomin));
	i++;


    // MFD
	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_MFD_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(mfd_mode));
	i++;

    // AP
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_state));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_vertical_velocity));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_ALTITUDE);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_altitude));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_approach_selector));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_HEADING_MAG);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_heading_mag));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_AIRSPEED);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_airspeed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_AIRSPEED_IS_MACH);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_airspeed_is_mach));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_FD_PITCH);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_fd_pitch));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_FD_ROLL);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_fd_roll));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_mode));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ENABLED);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_autothrottle_enabled));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ON);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_autothrottle_on));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_HEADING_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_hdg_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_NAV_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_lnav_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_VVI_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_vs_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_SPEED_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_spd_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_ALTITUDE_HOLD_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_alt_hold_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_GLIDESLOPE_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_gs_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_VNAV_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_vnav_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_TOGA_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_toga_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_TOGA_LATERAL_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_toga_lateral_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_ROLL_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_roll_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_PITCH_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_pitch_status));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_AUTOPILOT_BACKCOURSE_STATUS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_backcourse_status));
	i++;


    sim_packet.sim_data_points[i].id = custom_htoni(UFMC_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) ufmc_ready);
    i++;
    if ( ufmc_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_V1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_v1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_VR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_vr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_V2);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_v2));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_VREF);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_vref));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_VF30);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_vf30));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_VF40);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_vf40));
        i++;

    }


    sim_packet.sim_data_points[i].id = custom_htoni(X737_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) x737_ready);
    i++;
    if ( x737_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_FD_A);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_fdA_status));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_FD_B);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_fdB_status));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_CMD_A);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_CMD_A));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_CMD_B);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_CMD_B));
        i++;

        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_MCPSPD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_MCPSPD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_FMCSPD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_FMCSPD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_RETARD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_RETARD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_THRHLD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_THRHLD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_LNAVARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_LNAVARMED_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VORLOCARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VORLOCARMED_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_PITCHSPD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_PITCHSPD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_ALTHLD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_ALTHLD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VSARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VSARMED_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VS_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VNAVALT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VNAVALT_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VNAVPATH);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VNAVPATH_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VNAVSPD);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VNAVSPD_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_GSARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_GSARMED_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_GS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_GS_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_FLAREARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_FLAREARMED_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_FLARE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_FLARE_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_TOGA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_TOGA_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_LNAV);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_LNAV_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_HDG);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_HDG_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_VORLOC);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_VORLOC_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_N1);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_N1_mode));
        i++;

        sim_packet.sim_data_points[i].id = custom_htoni(X737_ATHR_ARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_ATHR_armed));
        i++;

        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_A_PITCH);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_A_PITCH));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_B_PITCH);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_B_PITCH));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_A_ROLL);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_A_ROLL));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_AFDS_B_ROLL);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_B_ROLL));
        i++;

    }


    sim_packet.sim_data_points[i].id = custom_htoni(CL30_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) cl30_ready);
    i++;
    if ( cl30_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(CL30_V1);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_v1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_VR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_vr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_V2);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_v2));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_VT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_vt));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_VGA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_vga));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_VREF);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_vref));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_REFSPDS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_refspds));
        i++;

    }


	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );

	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size (AVIO): %d\n", max_packet_size);
        XPLMDebugString(msg);
    }
	return packet_size;

}


int createEnginesPacket(void) {

	int i = 0;
	int packet_size;
	int e;
	int tanks;
	int engines;
	float fuelfloat[9];
	float engifloat[8];
	int engiint[8];
	int vib;
	int runs[8];
	int n1lows[8];
	int n1highs[8];
	int revs[8];
	int chips[8];
	int fires[8];

	strncpy(sim_packet.packet_id, "ENGI", 4);

    tanks = XPLMGetDatai(num_tanks);
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS);
	sim_packet.sim_data_points[i].value = custom_htonf((float) tanks);
	i++;
	engines = XPLMGetDatai(num_engines);
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES);
	sim_packet.sim_data_points[i].value = custom_htonf((float) engines);
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_REVERSER_DEPLOYED);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(reverser_deployed));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_OIL_PRESSURE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(oil_pressure));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_OIL_TEMPERATURE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(oil_temperature));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_FUEL_PRESSURE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(fuel_pressure));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(total_fuel));
	i++;

    XPLMGetDatavf(fuel_quantity, fuelfloat, 0, tanks);
	for (e=0; e<tanks; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( fuelfloat[e] );
        i++;
	}

    XPLMGetDatavf(engine_n1, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(engine_egt_percent, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(engine_egt_value, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(reverser_ratio, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(tank_ratio, engifloat, 0, tanks);
	for (e=0; e<tanks; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(engine_n2, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(fuel_flow, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(oil_p_ratio, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(oil_t_ratio, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(oil_q_ratio, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavi(vib_running, runs, 0, engines);
    XPLMGetDatavi(vib_n1_low, n1lows, 0, engines);
    XPLMGetDatavi(vib_n1_high, n1highs, 0, engines);
    XPLMGetDatavi(vib_reverse, revs, 0, engines);
    XPLMGetDatavi(vib_chip, chips, 0, engines);
    XPLMGetDatavi(vib_fire, fires, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_ + e);
        vib = runs[e] * ( 5 + n1lows[e]*5 + n1highs[e]*10 + revs[e]*10 + chips[e]*35 + fires[e]*35 );
        sim_packet.sim_data_points[i].value = custom_htonf( (float) vib );
        i++;
	}

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(hyd_p_1));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(hyd_p_2));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(hyd_q_1));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(hyd_q_2));
	i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACF_TRQ_MAX_ENG);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(engine_trq_max));
	i++;

    XPLMGetDatavf(engine_trq, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(engine_itt, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavf(engine_itt_c, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_C_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACF_RSC_REDLINE_PRP);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(prop_rpm_max));
	i++;

    XPLMGetDatavf(prop_rpm, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ENGINE_INDICATORS_PROP_SPEED_RPM_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}

    XPLMGetDatavi(prop_mode, engiint, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_PROPMODE_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( (float)engiint[e] );
        i++;
	}

    XPLMGetDatavf(piston_mpr, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_MPR_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
	}


    sim_packet.sim_data_points[i].id = custom_htoni(UFMC_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) ufmc_ready);
    i++;
    if ( ufmc_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_N1_1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_n1_1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_N1_2);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_n1_2));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_N1_3);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_n1_3));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(UFMC_N1_4);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(ufmc_n1_4));
        i++;

    }


    sim_packet.sim_data_points[i].id = custom_htoni(X737_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) x737_ready);
    i++;
    if ( x737_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(X737_N1_PHASE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_N1_phase));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_N1_LIMIT_ENG1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_N1_limit_eng1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_N1_LIMIT_ENG2);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_N1_limit_eng2));
        i++;

    }


    if ( cl30_ready ) {

        sim_packet.sim_data_points[i].id = custom_htoni(CL30_CARETS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cl30_carets));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_TO_N1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cl30_to_n1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(CL30_CLB_N1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cl30_clb_n1));
        i++;

    }


	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );

	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size (ENGI): %d\n", max_packet_size);
        XPLMDebugString(msg);
    }
	return packet_size;

}


int createStaticPacket(void) {

	int i = 0;
	int packet_size;
	float latf;
	float lonf;
	XPLMNavRef arpt_navref;
	char arpt_id[40];

	strncpy(sim_packet.packet_id, "STAT", 4);

	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_POSITION_NEAREST_ARPT);
	latf = (float) XPLMGetDatad(latitude);
	lonf = (float) XPLMGetDatad(longitude);
    arpt_navref = XPLMFindNavAid(
        NULL, // inNameFragment
        NULL, // inIDFragment
        &latf,
        &lonf,
        NULL, // inFrequency
        xplm_Nav_Airport);
    XPLMGetNavAidInfo(
        arpt_navref,
        NULL, // outType
        NULL, // outLatitude
        NULL, // outLongitude
        NULL, // outHeight
        NULL, // outFrequency
        NULL, // outHeading
        arpt_id,
        NULL, // outName
        NULL); // outReg
    strncpy( (char *)&sim_packet.sim_data_points[i].value, arpt_id, 4 );
	i++;


	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_VSO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vso));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_VS);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vs));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_VFE);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vfe));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_VNO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vno));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_VNE);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vne));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_MMO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_mmo));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_OVERFLOW_ACF_VLE);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vle));
	i++;


	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );

	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size (STAT): %d\n", max_packet_size);
        XPLMDebugString(msg);
    }
	return packet_size;

}


int createFmsPackets(void) {

	char nav_id[256];
	int altitude;
	float lat;
	float lon;

	float ete;
	float gs;

	int displayed_entry;
	int active_entry;
	int total_waypoints;
	int displayed_waypoint = -1;
	int active_waypoint = -1;

	 // the entry number in the FMS
	int cur_entry;

	// the same, but counting only non-zero entries
	// at the end, it should be equal to the number of non-zero entries
	int cur_waypoint = 0;

	// the entry number in the packet that we send
	int cur_packpoint = 0;

	XPLMNavType type;
	XPLMNavRef outRef;

    int i;

    int cur_pack = 0;

    // the actual number of waypoints, not counting zero'd waypoints
	total_waypoints = XPLMCountFMSEntries();

	if (total_waypoints > 0) {


//sprintf(msg, "XHSI: FMC: nb=%d\n", total_waypoints);
//XPLMDebugString(msg);

		displayed_entry = XPLMGetDisplayedFMSEntry();
		active_entry = XPLMGetDestinationFMSEntry();

		cur_entry = 0;
//XPLMDebugString("XHSI: FMC: cur_entry=");

		while ( total_waypoints > 1
             && cur_waypoint < total_waypoints
             && cur_entry < MAX_FMS_ENTRIES_POSSIBLE ) {

//sprintf(msg, " %d ", cur_entry);
//XPLMDebugString(msg);

            cur_pack = (int)cur_waypoint / 50;

			XPLMGetFMSEntryInfo(
                    cur_entry,
                    &type,
                    nav_id,
                    &outRef,
                    &altitude,
                    &lat,
                    &lon);

            // only send non-zero entries
			if ( type != xplm_Nav_Unknown ) {
			// if ( lat != 0.0f || lon != 0.0f ) {

				if (cur_entry == displayed_entry) {
					// the corrected entry number for the displayed entry
					displayed_waypoint = cur_waypoint;
				}
				if (cur_entry == active_entry) {
					// the corrected entry number for the active entry
					active_waypoint = cur_waypoint;
				}

                cur_packpoint = cur_waypoint % 50;

				fms_packet[cur_pack].entries[cur_packpoint].type = custom_htoni( (int)type );
				if (type == xplm_Nav_LatLon) {
					strncpy(fms_packet[cur_pack].entries[cur_packpoint].id, "Lat/Lon", sizeof(fms_packet[cur_pack].entries[cur_packpoint].id));
				} else {
					strncpy(fms_packet[cur_pack].entries[cur_packpoint].id, nav_id, sizeof(fms_packet[cur_pack].entries[cur_packpoint].id));
				}
				fms_packet[cur_pack].entries[cur_packpoint].altitude = custom_htoni(altitude);
				fms_packet[cur_pack].entries[cur_packpoint].lat = custom_htonf(lat);
				fms_packet[cur_pack].entries[cur_packpoint].lon = custom_htonf(lon);

				// get ready for the next waypoint
				cur_waypoint++;
				// at the end, cur_waypoint will be the same as total_waypoints
			}
			cur_entry++;
		}
//XPLMDebugString("\n");
//sprintf(msg, "XHSI: FMC: last=%d\n", cur_entry);
//XPLMDebugString(msg);
//sprintf(msg, "XHSI: FMC: count=%d\n", cur_waypoint);
//XPLMDebugString(msg);

	}

    if ( cur_waypoint != total_waypoints ) {
        sprintf(msg, "XHSI: FMC: error count: %d %d\n", cur_waypoint, total_waypoints);
        XPLMDebugString(msg);
    }

    ete = XPLMGetDataf(gps_dme_time_secs);
    gs = XPLMGetDataf(groundspeed);

//    for (i = 0; i <= ((total_waypoints-1)/MAX_FMS_ENTRIES_POSSIBLE); i++) {
    for (i = 0; i <= cur_pack; i++) {
		strncpy(fms_packet[i].packet_id, "FMC", 3);
		fms_packet[i].packet_id[3] = '0' + (unsigned char)i;
//sprintf(msg, "XHSI: filling %c%c%c%c \n",fms_packet[i].packet_id[0],fms_packet[i].packet_id[1],fms_packet[i].packet_id[2],fms_packet[i].packet_id[3]);
//XPLMDebugString(msg);
		fms_packet[i].ete_for_active = custom_htonf( ete );
		fms_packet[i].groundspeed = custom_htonf( gs );
        fms_packet[i].nb_of_entries = custom_htoni( total_waypoints );
        fms_packet[i].displayed_entry_index = custom_htoni( displayed_waypoint );
        fms_packet[i].active_entry_index = custom_htoni( active_waypoint );
    }

	// packet size : char[4] + float + float + int + int + int + ( # * ( int + char[8] + float + float + float ) )
	// return 24 + cur_waypoint * 24;
	// return the number of waypoints; let the sender sort it out...
	return total_waypoints;

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

        tcas_packet.mp_total = custom_htoni( total );
        tcas_packet.mp_active = custom_htoni( active );

        tcas_packet.radar_altitude = custom_htonf( (float)XPLMGetDataf(agl) * 3.28084f );

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

        // packet size : char[4] + int + int + float + ( # * ( float + float + float ) )
        return 4 + 4 + 4 + 4 + total * 3 * 4;

    } else {
        return 0;
    }

}
