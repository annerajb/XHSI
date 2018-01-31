
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
#include "datarefs_pilotedge.h"
#include "datarefs_qpac.h"
#include "datarefs_pa_a320.h"
#include "datarefs_jar_a320neo.h"
#include "datarefs_xjoymap.h"
#include "datarefs_x_raas.h"
#include "endianess.h"


// MTU Ethernet : 1500
// UDP Payload (ipV4) : 1472
// UDP Payload (ipV6) : 1452 (because ipV6 header is 20 bytes more than ipV4 header)
//
// struct SimDataPoint {
//    int    id;    // 4 bytes
//    float  value; // 4 bytes
//};
//
//struct SimDataPacket {
//    char                packet_id[4];          // 4 bytes
//    int                 nb_of_sim_data_points; // 4 bytes 
//    struct SimDataPoint sim_data_points[222];  // 222 * 8 bytes
//};
//
// Free for SimDataPoint : 1472 - 8 = 1464
// Maximum number of SimDataPoint records : 1464 / 8 = 183
//
// We set the maximum number of SimDataPoint records to 182.
#define MAX_DATAPOINTS 182

// QPAC SD line buffer length
#define QPAC_SD_LINE_BUF_LEN 40

// Define global vars
// The data packets =========================================
struct SimDataPacket	   sim_packet;
struct FmsDataPacket	   fms_packet[10];
struct TcasDataPacket	   tcas_packet;
struct IncomingPacket      efis_packet;
struct RemoteCommandPacket rcmd_packet;

int max_adcd_size = 0;
int max_auxs_size = 0;
int max_avio_size = 0;
int max_custom_avio_size = 0;
int max_engi_size = 0;
int max_stat_size = 0;

char msg[200];

// Used to calculate IAS relative speeds
float ias=0.0f;

/* Convert IAS relative speed to absolute speed for QPAC AirbusFBW
 * Result set to -1 if out of range
 */
float qpac_ias_shift(float dataref) {
	if (dataref <= 0.0f || dataref >= 83.5f ) {
		return -1.0f;
	} else {
		return ias + dataref - 41.7f;
	}
}

/* Convert IAS relative speed to absolute speed for QPAC AirbusFBW
 * Result set to -1 if out of range
 */
float qpac_ias_vmo(float dataref) {
	if (dataref <= 0.0f || dataref >= 83.5f ) {
		return -1.0f;
	} else {
		return ias - dataref + 41.7f;
	}
}

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
        if ((id > QPAC_STATUS) && (id < JAR_A320NEO_STATUS)) {
            writeQpacDataRef(id, float_value);
        } else 	if ((id >= JAR_A320NEO_STATUS) && (id <= JAR_A320NEO_END)) {
        	writeJarA320neoDataRef(id, float_value);
        } else {
        	writeDataRef(id, float_value);
        }
    }

}


int createADCPacket(void) {

	int i = 0;
	int packet_size;
	int g;
	int gears;
	int lights_signs;
	int gear_type[10];
	float gear_ratio[10];
	int gear_doors;
	int d;
	int gear_doors_type[20];
	float gear_doors_ang[20];
	float gear_doors_ext_ang[20];
	float gear_doors_ret_ang[20];
	float door_deploy_ratio;

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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_FORCES_G_LOAD );
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(g_load));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_MISC_TURNRATE_NOROLL );
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(turnrate_noroll));
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_WEATHER_TEMPERATURE_SEALEVEL_C);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(isa));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_WEATHER_TEMPERATURE_LE_C);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(tat));
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CLOCK_TIMER_MODE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(clock_timer_mode));
    i++;


    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION );
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(master_caution));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING );
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(master_warning));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ANNUNCIATORS_MASTER_ACCEPT );
    if (qpac_ready) {
    	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_clear_illuminated));
    } else {
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(master_accept));
    }
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

    // GEAR DOORS
    // Count gear doors, only ones that are closed when gears are down, type = 3
    XPLMGetDatavi(gear_door_type, gear_doors_type, 0, 20);
    XPLMGetDatavf(gear_door_ext_ang, gear_doors_ext_ang, 0, 20);
    XPLMGetDatavf(gear_door_ret_ang, gear_doors_ret_ang, 0, 20);
    XPLMGetDatavf(gear_door_ang, gear_doors_ang, 0, 20);
    gear_doors = 0;
    for (d=0; (d<20) && (gear_doors < 10); d++ )
    {
    	if ( gear_doors_type[d] == 3 ) {

    		if ( (gear_doors_ret_ang[d]-gear_doors_ext_ang[d]) != 0) {
    			// Check there is not division by 0
    			door_deploy_ratio = ((gear_doors_ret_ang[d]-gear_doors_ang[d]) / (gear_doors_ret_ang[d]-gear_doors_ext_ang[d]));
    		} else {
    			door_deploy_ratio=0;
    		}

    		// door_deploy_ratio = gear_doors_ang[d];
    	    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_AIRCRAFT_GEAR_DOOR_DEPLOY_RATIO_+ gear_doors);
    	    sim_packet.sim_data_points[i].value = custom_htonf(door_deploy_ratio);
    	    i++;
    	    gear_doors++;
    	}
    }
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_AIRCRAFT_GEAR_DOOR_COUNT);
    sim_packet.sim_data_points[i].value = custom_htonf((float) gear_doors);
    i++;

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(parkbrake_ratio));
    i++;

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_LIGHTS);
    /* lights and signs based on switches positions
    lights_signs = XPLMGetDatai(landing_light) << 4 | XPLMGetDatai(navigation_light) << 3 |
    		XPLMGetDatai(no_smoking) << 2 | XPLMGetDatai(fasten_seat_belts) << 1 |
    		XPLMGetDatai(strobe_lights) << 1 | XPLMGetDatai(taxi_lights) | XPLMGetDatai(beacon) ;
    */
    if (x737_ready) {
    	lights_signs =
            (XPLMGetDatai(x737_beacon_light_switch) +
            XPLMGetDatai(x737_left_fixed_land_light_switch) * 2 +
            (XPLMGetDatai(x737_position_light_switch) != 0 ? 4 : 0) +
            (XPLMGetDatai(x737_position_light_switch) == -1 ? 8 : 0) +
            XPLMGetDatai(x737_taxi_light_switch) * 16);
    } else {
    	lights_signs =
            XPLMGetDatai(beacon_lights_on) |
            XPLMGetDatai(landing_lights_on)   << 1 |
            XPLMGetDatai(nav_lights_on)       << 2 |
            XPLMGetDatai(strobe_lights_on)    << 3 |
            XPLMGetDatai(taxi_light_on)       << 4 |
            XPLMGetDatai(no_smoking)          << 6 |
            XPLMGetDatai(fasten_seat_belts)   << 8;
    }
    sim_packet.sim_data_points[i].value = custom_htonf((float) lights_signs);
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_FUEL_PUMPS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(fuel_pumps)); // TODO : check bitfield
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_YOKE_PITCH_RATIO);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(yoke_pitch_ratio));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_YOKE_ROLL_RATIO);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(yoke_roll_ratio));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_YOKE_HDG_RATIO);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(yoke_hdg_ratio));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_ELEVATOR_TRIM);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(elevator_trim));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_AILERON_TRIM);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(aileron_trim));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_RUDDER_TRIM);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(rudder_trim));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_SLATRAT);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(slat_deploy));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_RIGHT_BRK_RATIO);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(right_brake_ratio));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_LEFT_BRK_RATIO);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(left_brake_ratio));
    i++;
	// Ctrls surfaces
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_LEFT_ELEV);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(left_elevator_pos));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_RIGHT_ELEV);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(right_elevator_pos));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_RUDDER);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(rudder_pos));
	i++;
	if ( qpac_ready ) {
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_LEFT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_left_aileron_pos));
		i++;
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_RIGHT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_right_aileron_pos));
		i++;
	} else if (pa_a320_ready) {
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_LEFT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(pa_a320_left_aileron_pos));
		i++;
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_RIGHT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(pa_a320_right_aileron_pos));
		i++;
	} else if (jar_a320_neo_ready) {
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_LEFT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(left_wing_aileron_1_def[1]));
		i++;
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_RIGHT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(right_wing_aileron_1_def[1]));
		i++;
	} else	{
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_LEFT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(left_aileron_pos));
		i++;
		sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_CONTROLS_RIGHT_AIL);
		sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(right_aileron_pos));
		i++;
	}


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
    if ( packet_size > max_adcd_size) {
        max_adcd_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far for ADCD: %d\n", max_adcd_size);
        XPLMDebugString(msg);
        if ( i > MAX_DATAPOINTS ) {
            sprintf(msg, "XHSI: max number of sim data points exceeded for ADCD: %d (max: %d)\n", i, MAX_DATAPOINTS);
            XPLMDebugString(msg);
        }
    }
    
    return packet_size;

}

/**
 * Concerns :
 *  Auxiliary power plants: APU, GPU, RAT
 *  Hydraulics, Bleed air, Hot air, Packs
 */
int createAuxiliarySystemsPacket(void) {

    int i = 0;
    int packet_size;
    int apu_status;
    int wheel_status;
    int tire_status;

    strncpy(sim_packet.packet_id, "AUXS", 4);

    /*
     *  Auxiliary power plants
     *  APU, GPU, RAM AIR TURBIN (RAT)
     */
    sim_packet.sim_data_points[i].id = custom_htoni(APU_N1);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(apu_n1));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(APU_GEN_AMP);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(apu_gen_amp));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(GPU_GEN_AMP);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(elec_gpu_amps));
    i++;
    if (jar_a320_neo_ready) {
    	// Jar A320 did not use default X-Plane APU and GPU datarefs
    	apu_status =
    			XPLMGetDatai(jar_a320_neo_elec_gpu_av) << 7 |
    			XPLMGetDatai(jar_a320_neo_elec_gpu_on) << 6 |
    			XPLMGetDatai(jar_a320_neo_elec_rat_on) << 5 |
    			XPLMGetDatai(apu_running) << 4 |
    			(XPLMGetDataf(jar_a320_neo_elec_apu_volt)>380.0f) << 2 |
    			XPLMGetDatai(apu_starter); // Starter on 2 bits
    } else {
    	apu_status =
    			(1 << 7) |  // GPU always avail
    			XPLMGetDatai(elec_gpu_on) << 6 |
    			XPLMGetDatai(ram_air_turbin) << 5 |
    			XPLMGetDatai(apu_running) << 4 |
    			XPLMGetDatai(apu_gen_on) << 2 |
    			XPLMGetDatai(apu_starter); // Starter on 2 bits
    }
    sim_packet.sim_data_points[i].id = custom_htoni(AUX_GEN_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) apu_status);
    i++;

    /*
     *  Wheels, brakes, tires, steering, gears
     */
    if (qpac_ready) {
        wheel_status = (XPLMGetDatai(qpac_right_brake_release) << 6) |
        		(XPLMGetDatai(qpac_left_brake_release) << 5) |
        		(XPLMGetDatai(sim_op_fail_rel_gear_act)==6) << 4 |
        		(XPLMGetDatai(sim_op_fail_rel_gear_ind)==6) << 3 |
        		(XPLMGetDatai(sim_op_fail_rel_rbrake)==6) << 2 |
        		(XPLMGetDatai(sim_op_fail_rel_lbrake)==6) << 1 |
        		XPLMGetDatai(qpac_nw_anti_skid); // Wheel steer on 1 bit
    } else if (jar_a320_neo_ready) {
    	wheel_status =
    			(XPLMGetDatai(sim_op_fail_rel_gear_act)==6) << 4 |
    			(XPLMGetDatai(sim_op_fail_rel_gear_ind)==6) << 3 |
    			(XPLMGetDatai(sim_op_fail_rel_rbrake)==6) << 2 |
    			(XPLMGetDatai(sim_op_fail_rel_lbrake)==6) << 1 |
    			XPLMGetDatai(jar_a320_neo_wheels_anti_skid) ; // Wheel steer on 1 bit
    } else
    {
    	wheel_status =
    			(XPLMGetDatai(sim_op_fail_rel_gear_act)==6) << 4 |
    			(XPLMGetDatai(sim_op_fail_rel_gear_ind)==6) << 3 |
    			(XPLMGetDatai(sim_op_fail_rel_rbrake)==6) << 2 |
    			(XPLMGetDatai(sim_op_fail_rel_lbrake)==6) << 1 |
    			XPLMGetDatai(nose_wheel_steer_on); // Wheel steer on 1 bit
    }
    sim_packet.sim_data_points[i].id = custom_htoni(WHEEL_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) wheel_status);
    i++;
    tire_status = (XPLMGetDatai(sim_op_fail_rel_tire5) == 6) << 4 |
    		(XPLMGetDatai(sim_op_fail_rel_tire4) == 6) << 3 |
    		(XPLMGetDatai(sim_op_fail_rel_tire3) == 6) << 2 |
    		(XPLMGetDatai(sim_op_fail_rel_tire2) == 6) << 1 |
    		(XPLMGetDatai(sim_op_fail_rel_tire1) == 6); // Wheel steer on 1 bit
    sim_packet.sim_data_points[i].id = custom_htoni(TIRE_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) tire_status);
    i++;

    // now we know the number of datapoints
    sim_packet.nb_of_sim_data_points = custom_htoni( i );

    // packet size : char[4] + int + ( # * ( int + float) )
    packet_size = 8 + i * 8;
    if ( packet_size > max_auxs_size) {
    	max_auxs_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far for AUXS: %d\n", max_auxs_size);
        XPLMDebugString(msg);
        if ( i > MAX_DATAPOINTS ) {
            sprintf(msg, "XHSI: max number of sim data points exceeded for AUXS: %d (max: %d)\n", i, MAX_DATAPOINTS);
            XPLMDebugString(msg);
        }
    }

    return packet_size;

}

/**
 * Concerns Radio, Transponder, EFIS selectors and modes
 * Main gauges
 * System failures
 * Electrics
 */
int createAvionicsPacket(void) {

    int i = 0;
    int packet_size;
    char nav_id_bytes[8];
    //float gear_ratio[10];
    int wxr_opt;
    int egpws_modes;
    int std_gauges_failures_pilot;
    int std_gauges_failures_copilot;

    int xhsi_cdu_source;
    int xhsi_cdu_side;

    int elec_status;
    int bat;
    float battery_volt[8];
    float battery_amp[8];
    int battery_status[8];
    int gen;
    int generators_status;
    int generators_on[8];
    int inverters[2];
    float generators_amps[8];
    float bus_volts[6];
    float bus_load_amps[6];
    int bus_on[6];
    int bus;

    strncpy(sim_packet.packet_id, "AVIO", 4);

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_AVIONICS_ON);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(avionics_on));
    i++;

    if (jar_a320_neo_ready) {
    	bat = (XPLMGetDatai(jar_a320_neo_elec_bat1_on) & 0x01) |
    		(XPLMGetDatai(jar_a320_neo_elec_bat2_on) & 0x01) << 1 ;
    } else {
    	XPLMGetDatavi(elec_battery_on, battery_status,0,4);
    	bat = battery_status[0] | battery_status[1] << 1 | battery_status[2] << 2 | battery_status[3] << 3;
    }
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_BATTERY_ON);
    // sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(battery_on));
    sim_packet.sim_data_points[i].value = custom_htonf((float) bat);
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(cockpit_lights_on));
    i++;

    if (qpac_ready) {
    	XPLMGetDatavf(qpac_elec_battery_volt, battery_volt, 0, 4);
        XPLMGetDatavf(elec_battery_amps, battery_amp, 0, 4);
    } else if (jar_a320_neo_ready) {
    	battery_amp[0] = XPLMGetDataf(jar_a320_neo_elec_bat1_amp);
    	battery_amp[1] = XPLMGetDataf(jar_a320_neo_elec_bat2_amp);
    	battery_amp[2] = 0.0f;
    	battery_amp[3] = 0.0f;
    	battery_volt[0] = XPLMGetDataf(jar_a320_neo_elec_bat1_volt);
    	battery_volt[1] = XPLMGetDataf(jar_a320_neo_elec_bat2_volt);
    	battery_volt[2] = 0.0f;
    	battery_volt[3] = 0.0f;
    } else {
    	XPLMGetDatavf(elec_voltage_actual_volts, battery_volt, 0, 4);
        XPLMGetDatavf(elec_battery_amps, battery_amp, 0, 4);
    }
    for (bat=0; bat<4; bat++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_BATTERY_VOLT_ + bat);
        sim_packet.sim_data_points[i].value = custom_htonf( battery_volt[bat] );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_BATTERY_AMP_ + bat);
        sim_packet.sim_data_points[i].value = custom_htonf( battery_amp[bat] );
        i++;
    }

    // electric status
    /*
     * Bus mapping for A320 display
     * 0 = AC1
     * 1 = AC2
     * 2 = DC1
     * 3 = DC2
     * 4 = AC ESS
     * 5 = DC ESS
     *
     */
    // TODO : JAR DESIGN ELEC
    XPLMGetDatavf(elec_bus_volts, bus_volts, 0, 6);
    for (bus=0;bus<6;bus++) bus_on[bus] = bus_volts[bus] > 1.0;
    XPLMGetDatavi(elec_inverter_on, inverters, 0, 2);
    if (jar_a320_neo_ready) {
    	elec_status =
    		(XPLMGetDatai(jar_a320_neo_elec_ac1_source) != 0) |
    		(XPLMGetDatai(jar_a320_neo_elec_ac2_source) != 0) << 1  |
    		(XPLMGetDatai(jar_a320_neo_elec_dc1) != 0) << 2 |
    		(XPLMGetDatai(jar_a320_neo_elec_dc2) != 0) << 3 |
    		(XPLMGetDatai(jar_a320_neo_elec_ac_ess) != 0) << 4 |
    		(XPLMGetDatai(jar_a320_neo_elec_dc_ess) != 0) << 5 |
    		inverters[0] << 6 |
    		inverters[1] << 7 |
    		(XPLMGetDatai(jar_a320_neo_elec_commrc) != 0) << 8 |
    		(XPLMGetDatai(jar_a320_neo_elec_galley) != 0) << 9;
    } else {
    	elec_status =
    		bus_on[0] |
    		bus_on[1] << 1 |
    		bus_on[2] << 2 |
    		bus_on[3] << 3 |
    		bus_on[4] << 4 |
    		bus_on[5] << 5 |
    		inverters[0] << 6 |
    		inverters[1] << 7;
    }
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_INV_BUS_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) elec_status);
    i++;

    XPLMGetDatavf(elec_bus_load_amps, bus_load_amps, 0, 6);
    for (bus=0; bus<6; bus++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_BUS_LOAD_ + bus);
        sim_packet.sim_data_points[i].value = custom_htonf( bus_load_amps[bus] );
        i++;
    }

    XPLMGetDatavf(elec_generator_amps, generators_amps, 0, 8);
    for (gen=0; gen<8; gen++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_GENERATOR_AMPS_ + gen);
        sim_packet.sim_data_points[i].value = custom_htonf( generators_amps[gen] );
        i++;
    }

    if (jar_a320_neo_ready) {
    	generators_status =
    		((XPLMGetDataf(jar_a320_neo_elec_gen1_volt) > 110.0f) & 0x01) |
    	    ((XPLMGetDataf(jar_a320_neo_elec_gen2_volt) > 110.0f) & 0x01) << 1 ;
    } else {
    	XPLMGetDatavi(elec_generator_on, generators_on, 0, 8);
    	generators_status = generators_on[0] | generators_on[1] << 1 | generators_on[2] << 2 | generators_on[3] << 3 |
    			generators_on[4] << 4 | generators_on[5] << 5 | generators_on[6] << 6 | generators_on[7] << 7;
    }
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ELECTRICAL_GENERATOR_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) generators_status);
    i++;



    // Standard gauges failures
    // Each value is on 3 bits (enum failure integer 0 to 6)
    std_gauges_failures_pilot =
            XPLMGetDatai(sim_op_fail_rel_ss_ahz) << 15 |
            XPLMGetDatai(sim_op_fail_rel_ss_alt) << 12 |
            XPLMGetDatai(sim_op_fail_rel_ss_asi) << 9 |
            XPLMGetDatai(sim_op_fail_rel_ss_dgy) << 6 |
            XPLMGetDatai(sim_op_fail_rel_ss_tsi) << 3 |
            XPLMGetDatai(sim_op_fail_rel_ss_vvi) ;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_GAUGES_FAILURES_PILOT);
    sim_packet.sim_data_points[i].value = custom_htonf( (float) std_gauges_failures_pilot );
    i++;
    std_gauges_failures_copilot =
            XPLMGetDatai(sim_op_fail_rel_cop_ahz) << 15 |
            XPLMGetDatai(sim_op_fail_rel_cop_alt) << 12 |
            XPLMGetDatai(sim_op_fail_rel_cop_asi) << 9 |
            XPLMGetDatai(sim_op_fail_rel_cop_dgy) << 6 |
            XPLMGetDatai(sim_op_fail_rel_cop_tsi) << 3 |
            XPLMGetDatai(sim_op_fail_rel_cop_vvi) ;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_GAUGES_FAILURES_COPILOT);
    sim_packet.sim_data_points[i].value = custom_htonf( (float) std_gauges_failures_copilot );
    i++;

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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_GPS_HAS_GLIDESLOPE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(gps_has_glideslope));
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

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_GPS_NAV_ID_0_3);
    XPLMGetDatab(gps_id, nav_id_bytes, 0, 8);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_INDICATORS_GPS_NAV_ID_4_7);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes + 4, 4 );
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

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_FREQUENCY_HZ_833);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com1_frequency_hz_833));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_STANDBY_FREQUENCY_HZ_833);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com1_standby_frequency_hz_833));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_FREQUENCY_HZ_833);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com2_frequency_hz_833));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_STANDBY_FREQUENCY_HZ_833);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(com2_standby_frequency_hz_833));
    i++;

    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_RTU_CONTACT_ATC);
//    sim_packet.sim_data_points[i].value =  custom_htonf((float)( XPLMGetDatai(xhsi_rtu_contact_atc) || ( pilotedge_ready && XPLMGetDatai(pilotedge_tx_status) )));
    sim_packet.sim_data_points[i].value =  custom_htonf((float)( ( pilotedge_ready && XPLMGetDatai(pilotedge_tx_status) )));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_RTU_SELECTED_RADIO);
    sim_packet.sim_data_points[i].value =  custom_htonf((float) XPLMGetDatai(xhsi_rtu_selected_radio));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_RTU_PILOTEDGE_RX);
    sim_packet.sim_data_points[i].value =  custom_htonf((float)( pilotedge_ready && XPLMGetDatai(pilotedge_rx_status) ));
    i++;

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_TRANSPONDER_CODE);
    sim_packet.sim_data_points[i].value =  custom_htonf((float) XPLMGetDatai(transponder_code));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_TRANSPONDER_MODE);
    sim_packet.sim_data_points[i].value =  custom_htonf((float) XPLMGetDatai(transponder_mode));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_RADIOS_TRANSPONDER_ID);
    sim_packet.sim_data_points[i].value =  custom_htonf((float) XPLMGetDatai(transponder_id));
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
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_TERRAIN);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_shows_terrain));
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
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_ELAPSED_TIME_SEC);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(efis_pilot_chrono));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_WXR_TILT);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(efis_pilot_wxr_tilt));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_WXR_GAIN);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(efis_pilot_wxr_gain));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_WXR_MODE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_pilot_wxr_mode));
    i++;
    wxr_opt =
		 (XPLMGetDatai(efis_pilot_wxr_target) & 0x01) << 7 |
		 (XPLMGetDatai(efis_pilot_wxr_alert) & 0x01) << 6 |
		 (XPLMGetDatai(efis_pilot_wxr_narrow) & 0x01) << 5 |
		 (XPLMGetDatai(efis_pilot_wxr_react) & 0x01) << 4 |
		 (XPLMGetDatai(efis_pilot_wxr_slave) & 0x01) << 3 |
		 (XPLMGetDatai(efis_pilot_wxr_auto_tilt) & 0x01) << 2 |
		 (XPLMGetDatai(efis_pilot_wxr_auto_gain) & 0x01 ) << 1 |
		 (XPLMGetDatai(efis_pilot_wxr_test) & 0x01 );
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_PILOT_WXR_OPT);
    sim_packet.sim_data_points[i].value = custom_htonf((float)wxr_opt);
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
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_TERRAIN);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_shows_terrain));
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
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_ELAPSED_TIME_SEC);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(efis_copilot_chrono));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_WXR_TILT);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(efis_copilot_wxr_tilt));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_WXR_GAIN);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(efis_copilot_wxr_gain));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_WXR_MODE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(efis_copilot_wxr_mode));
    i++;
    wxr_opt =
		 (XPLMGetDatai(efis_copilot_wxr_target) & 0x01) << 7 |
		 (XPLMGetDatai(efis_copilot_wxr_alert) & 0x01) << 6 |
		 (XPLMGetDatai(efis_copilot_wxr_narrow) & 0x01) << 5 |
		 (XPLMGetDatai(efis_copilot_wxr_react) & 0x01) << 4 |
		 (XPLMGetDatai(efis_copilot_wxr_slave) & 0x01) << 3 |
		 (XPLMGetDatai(efis_copilot_wxr_auto_tilt) & 0x01) << 2 |
		 (XPLMGetDatai(efis_copilot_wxr_auto_gain) & 0x01 ) << 1 |
		 (XPLMGetDatai(efis_copilot_wxr_test) & 0x01 );
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EFIS_COPILOT_WXR_OPT);
    sim_packet.sim_data_points[i].value = custom_htonf((float)wxr_opt);
    i++;

// EGPWS
    egpws_modes = (XPLMGetDatai(egpws_flaps_mode) & 0x01) << 2 |
    		(XPLMGetDatai(egpws_gs_mode) & 0x01 ) << 1 |
    		(XPLMGetDatai(egpws_sys) & 0x01 );
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EPGWS_MODES);
    sim_packet.sim_data_points[i].value = custom_htonf((float)egpws_modes);
    i++;


// EICAS
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_ENGINE_TYPE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(engine_type));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EICAS_TRQ_SCALE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(trq_scale));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FUEL_UNITS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(fuel_units));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_TEMP_UNITS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(temp_units));
    i++;

// MFD
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_MFD_MODE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(mfd_mode));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_CREW_OXY_PSI);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(mfd_crew_oxy_psi));
    i++;



// CDU
    xhsi_cdu_source = (XPLMGetDatai(cdu_pilot_source) & 0x0F) | ((XPLMGetDatai(cdu_copilot_source) & 0x0F) << 4);
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_CDU_SOURCE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) xhsi_cdu_source);
    i++;
    xhsi_cdu_side = (XPLMGetDatai(cdu_pilot_side) & 0x0F) | ((XPLMGetDatai(cdu_copilot_side) & 0x0F) << 4);
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_CDU_SIDE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) xhsi_cdu_side);
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
    sim_packet.sim_data_points[i].id = custom_htoni(DUPLICATE_THETA_FOR_PITCH);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(theta));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_FD_ROLL);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(autopilot_fd_roll));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(DUPLICATE_PHI_FOR_BANK);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(phi));
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_HEADING_ROLL_MODE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_heading_roll_mode));
    i++;

    // Cabin Pressure
    if (qpac_ready) {
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_CABIN_DELTA_P);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cabin_delta_p));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_CABIN_ALT);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cabin_alt));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_CABIN_VVI);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cabin_vs));
    	i++;

    } else {
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_CABIN_DELTA_P);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cabin_delta_p));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_CABIN_ALT);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cabin_altitude));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_CABIN_VVI);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(cabin_vvi));
    	i++;
    	// Cabin Pressure actuators
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_ACT_ALT);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(pressurization_alt_target));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_ACT_VVI);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(pressurization_vvi_target));
    	i++;
    	// TODO: bit array
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_MODES);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDatai(pressurization_mode));
    	i++;
    	/*
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_MODES);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDatai(pressurization_dump_to_alt));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_MODES);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDatai(pressurization_dump_all));
    	i++;
    	*/
    	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_PRESSURIZATION_MAX_ALT);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(pressurization_max_alt));
    	i++;

    }




    // now we know the number of datapoints
    sim_packet.nb_of_sim_data_points = custom_htoni( i );

    // packet size : char[4] + int + ( # * ( int + float) )
    packet_size = 8 + i * 8;
    if ( packet_size > max_avio_size) {
        max_avio_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far for AVIO: %d\n", max_avio_size);
        XPLMDebugString(msg);
        if ( i > MAX_DATAPOINTS ) {
            sprintf(msg, "XHSI: max number of sim data points exceeded for AVIO: %d (max: %d)\n", i, MAX_DATAPOINTS);
            XPLMDebugString(msg);
        }
    }

    return packet_size;

}


int createCustomAvionicsPacket(void) {

    int i = 0;
    int j = 0;
    int packet_size;
    char nav_id_bytes[4];
    char qpac_ils_char[12];
    int fcu_data;
    int fcu_baro;
    int ap_appr;
    int qpac_ils;
    int qpac_failures;
    int qpac_spoilers_tab[20];
    int spoilers;
    int qpac_fcc_tab[5];
    int qpac_fcc;
    float qpac_hyd_press_tab[3];
    float qpac_hyd_qty_tab[3];
    int hyd_pumps = 0;
    int qpac_hyd_pump_tab[3];
    int qpac_fuel_pumps = 0;
    int qpac_fuel_pump_tab[6];
    int qpac_fuel_auto_pump_tab[6];
    int qpac_fuel_valves = 0;
    int qpac_fuel_valves_tab[6];
    int qpac_air_valves;
    // float ram_air_valve;
    int bleed_valves;
    float qpac_door_pax_tab[4];
    float qpac_door_cargo_tab[4];
    int elec_status;
    int qpac_elec_buttons;
    int qpac_elec_oph_tab[4];
    float qpac_nacelle_temp_tab[4];
    int qpac_ignition;
    int qpac_start_valve_tab[4];
    char qpac_sd_line_buffer[QPAC_SD_LINE_BUF_LEN];
    int qpac_sd_data_len;
    int qpac_sd_line_len;
    float qpac_vib_n2;
    float qpac_temp;
    int anti_ice;
    float qpac_tyre_delta_t;
    float qpac_tyre_pressure;
    float qpac_tyre_press_tab[4];
    float qpac_brake_temp_tab[4];

    int auto_brake_level;
    int brake_status;
    int door_status;

    int pa_a320_failures;
    int xjoymap_stick;
    int xraas_nd_message;

    strncpy(sim_packet.packet_id, "AVIO", 4);

    if ( x_raas_ready ) {
    	xraas_nd_message = XPLMGetDatai(x_raas_nd_alert);
        sim_packet.sim_data_points[i].id = custom_htoni(X_RAAS_ND_ALERT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) (xraas_nd_message & 0x0000FFFF));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X_RAAS_RWY_LEN_AVAIL);
        sim_packet.sim_data_points[i].value = custom_htonf((float) ((xraas_nd_message & 0xFFFF0000) >> 16) );
        i++;
    }

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

        sim_packet.sim_data_points[i].id = custom_htoni(X737_STBY_PWR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_stby_pwr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_PFD_PWR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_PFD_pwr));
        i++;

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

        sim_packet.sim_data_points[i].id = custom_htoni(X737_LVLCHANGE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_lvlchange));
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

        // EFIS captain
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_ND_RANGE_ENUM);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_efis0_nd_range_enum));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_FPV);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_FPV));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_MTR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_MTR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_TFC);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_TFC));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_CTR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_CTR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_WXR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_WXR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_STA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_STA));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_WPT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_WPT));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_ARPT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_ARPT));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_DATA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_DATA));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_POS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_POS));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_TERR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis0_TERR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_DH_SRC);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_efis0_DH_source));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS0_DH_VAL);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_efis0_DH_value));
        i++;
        
        // EFIS f/o
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_ND_RANGE_ENUM);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_efis1_nd_range_enum));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_FPV);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_FPV));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_MTR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_MTR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_TFC);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_TFC));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_CTR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_CTR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_WXR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_WXR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_STA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_STA));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_WPT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_WPT));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_ARPT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_ARPT));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_DATA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_DATA));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_POS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_POS));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_TERR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(x737_efis1_TERR));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_DH_SRC);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_efis1_DH_source));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(X737_EFIS1_DH_VAL);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(x737_efis1_DH_value));
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

    sim_packet.sim_data_points[i].id = custom_htoni(QPAC_STATUS);
    if (pa_a320_version == 0) {
    	sim_packet.sim_data_points[i].value = custom_htonf((float) qpac_version);
    } else {
    	sim_packet.sim_data_points[i].value = custom_htonf((float) pa_a320_version);
    }
    i++;
    if ( qpac_ready ) {
    	// Autopilot 1&2+ Flight directors 1&2 on/off
        int qpac_ap_fd_data =
        		XPLMGetDatai(qpac_ap1) << 3 |
    			XPLMGetDatai(qpac_ap2) << 2 |
    			XPLMGetDatai(qpac_fd1) << 1 |
    			XPLMGetDatai(qpac_fd2) ;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_FD);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_ap_fd_data );
        i++;

        // Autopilot
        // sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP2);
        // sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_ap2));
        // i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_PHASE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_ap_phase));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_PRESEL_CRZ);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_presel_crz));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_PRESEL_CLB);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_presel_clb));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_PRESEL_MACH);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_presel_mach));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_VERTICAL_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_ap_vertical_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_VERTICAL_ARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_ap_vertical_armed));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_LATERAL_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_ap_lateral_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_LATERAL_ARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_ap_lateral_armed));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_NPA_VALID);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_npa_valid));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_NPA_NO_POINTS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_npa_no_points));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_APPR_TYPE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_appr_type));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_APPR_MDA);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_appr_mda));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ALT_IS_CSTR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_alt_is_cstr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_CONSTRAINT_ALT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_constraint_alt));
        i++;

		// FCU
        fcu_data = 0;
        fcu_baro = 0;
        if (pa_a320_ready) {
        	fcu_data =
        		XPLMGetDatai(qpac_fcu_alt_managed) << 7 |
        		XPLMGetDatai(qpac_fcu_hdg_managed) << 6 |
        		XPLMGetDatai(qpac_fcu_hdg_dashed) << 5 |
        		XPLMGetDatai(qpac_fcu_spd_managed) << 4 |
        		XPLMGetDatai(qpac_fcu_spd_dashed) << 3 |
    			XPLMGetDatai(qpac_fcu_vs_dashed) << 2 |
    			XPLMGetDatai(pa_a320_fcu_metric_alt) << 1 |
    			XPLMGetDatai(pa_a320_fcu_hdg_trk) ;
            fcu_baro =
        			XPLMGetDatai(qpac_baro_std_fo) << 6 |
        			XPLMGetDatai(qpac_baro_unit_fo) << 5 |
        			XPLMGetDatai(pa_a320_baro_hide) << 4 |
        			XPLMGetDatai(qpac_baro_std_capt) << 2 |
        			XPLMGetDatai(qpac_baro_unit_capt) << 1 |
        			XPLMGetDatai(pa_a320_baro_hide) ;
        } else {
        	fcu_data =
        		XPLMGetDatai(qpac_fcu_alt_managed) << 7 |
        		XPLMGetDatai(qpac_fcu_hdg_managed) << 6 |
        		XPLMGetDatai(qpac_fcu_hdg_dashed) << 5 |
        		XPLMGetDatai(qpac_fcu_spd_managed) << 4 |
        		XPLMGetDatai(qpac_fcu_spd_dashed) << 3 |
        		XPLMGetDatai(qpac_fcu_vs_dashed) << 2 |
        		XPLMGetDatai(qpac_fcu_metric_alt) << 1 |
        		XPLMGetDatai(qpac_fcu_hdg_trk) ;
            fcu_baro =
        			XPLMGetDatai(qpac_baro_std_fo) << 6 |
        			XPLMGetDatai(qpac_baro_unit_fo) << 5 |
        			XPLMGetDatai(qpac_baro_hide_fo) << 4 |
        			XPLMGetDatai(qpac_baro_std_capt) << 2 |
        			XPLMGetDatai(qpac_baro_unit_capt) << 1 |
        			XPLMGetDatai(qpac_baro_hide_capt) ;
        }
        ap_appr =
        		XPLMGetDatai(qpac_loc_illuminated) << 1 |
        		XPLMGetDatai(qpac_appr_illuminated) ;

        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FCU);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) fcu_data );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FCU_BARO);
        sim_packet.sim_data_points[i].value = custom_htonf((float) fcu_baro);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AP_APPR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) ap_appr);
        i++;

        // Auto-Thrust
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ATHR_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_athr_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ATHR_MODE2);
        if (pa_a320_ready && (XPLMGetDatai(pa_a320_thr_a_floor) > 0)) {
        	sim_packet.sim_data_points[i].value = custom_htonf((float) (XPLMGetDatai(pa_a320_thr_a_floor)+10));
        } else if (qpac_alpha_floor_mode != NULL && (XPLMGetDatai(qpac_alpha_floor_mode) > 0)) {
        	sim_packet.sim_data_points[i].value = custom_htonf((float) (XPLMGetDatai(qpac_alpha_floor_mode)+10));
        } else {
        	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_athr_mode2));
        }
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ATHR_LIMITED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_athr_limited));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_THR_LEVER_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_thr_lever_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FMA_THR_WARNING);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_fma_thr_warning));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FLEX_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_flex_temp));
        i++;

        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_THR_RATING_TYPE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_thr_rating_type));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_THR_RATING_N1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_thr_rating_n1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_THR_RATING_EPR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_thr_rating_epr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_THROTTLE_INPUT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_throttle_input));
        i++;

        // ILS Sig and Deviation Capt. and FO
        qpac_ils =	
        		XPLMGetDatai(qpac_ils_on_fo) << 5 |
        		XPLMGetDatai(qpac_ils_on_capt) << 4 |
				XPLMGetDatai(qpac_gs_on_fo) << 3 |
    			XPLMGetDatai(qpac_loc_on_fo) << 2 |
    			XPLMGetDatai(qpac_gs_on_capt) << 1 |
    			XPLMGetDatai(qpac_loc_on_capt) ;

    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ILS_FLAGS);
    	sim_packet.sim_data_points[i].value = custom_htonf((float) qpac_ils );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_LOC_VAL_CAPT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_loc_val_capt));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_GS_VAL_CAPT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_gs_val_capt));
        i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_LOC_VAL_FO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_loc_val_fo));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_GS_VAL_FO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_gs_val_fo));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ILS_CRS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_ils_crs));
        i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ILS_ID);
    	XPLMGetDatab(qpac_ils_1, nav_id_bytes, 1, 4);
        strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
    	i++;
    	/*
    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ILS_DME_T);
    	XPLMGetDatab(qpac_ils_3, nav_id_bytes, 0, 4);
        strncpy( (char *)&sim_packet.sim_data_points[i].value, nav_id_bytes, 4 );
    	i++;
    	*/

    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ILS_FREQ);
    	XPLMGetDatab(qpac_ils_2, qpac_ils_char, 0, 12);
    	sim_packet.sim_data_points[i].value = custom_htonf((float)atof(qpac_ils_char)*100.0f);
    	i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ILS_DME);
    	XPLMGetDatab(qpac_ils_3, qpac_ils_char, 0, 12);
        sim_packet.sim_data_points[i].value = custom_htonf((float)atof(qpac_ils_char));
    	i++;

        // FD
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FD1_VER_BAR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_fd1_ver_bar));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FD1_HOR_BAR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_fd1_hor_bar));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FD2_VER_BAR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_fd2_ver_bar));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FD2_HOR_BAR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_fd2_hor_bar));
        i++;
/*
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FD1_YAW_BAR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_fd1_yaw_bar));
        i++;
*/
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FD2_YAW_BAR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_fd2_yaw_bar));
        i++;

        // V Speeds
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_V1_VALUE);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_v1_value));
        i++;
        // send values relative to XPLMGetDataf(airspeed_pilot)
    	ias = XPLMGetDataf(airspeed_pilot);
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_VR);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_vr)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_VMO);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_vmo(XPLMGetDataf(qpac_vmo)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_VLS);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_vls)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_VF);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_vf)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_VS);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_vs)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_V_GREEN_DOT);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_v_green_dot)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ALPHA_PROT);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_alpha_prot)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ALPHA_MAX);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_alpha_max)));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_VFE_NEXT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_vfe_next));
        i++;

        // EFIS
        if (qpac_capt_efis_nd_mode != NULL) {
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_EFIS_ND_MODE_CAPT);
            sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_capt_efis_nd_mode));
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_EFIS_ND_RANGE_CAPT);
            sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_capt_efis_nd_range));
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_EFIS_ND_MODE_FO);
            sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_co_efis_nd_mode));
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_EFIS_ND_RANGE_FO);
            sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_co_efis_nd_range));
            i++;
        }

        // Failures
        qpac_failures = 0xFFFF;
        if (qpac_co_hdg_valid != NULL) {
        	qpac_failures =
        		XPLMGetDatai(qpac_co_hdg_valid) << 7 |
    			XPLMGetDatai(qpac_co_att_valid) << 6 |
    			XPLMGetDatai(qpac_co_ias_valid) << 5 |
    			XPLMGetDatai(qpac_co_alt_valid) << 4 |
    			XPLMGetDatai(qpac_capt_hdg_valid) << 3 |
    			XPLMGetDatai(qpac_capt_att_valid) << 2 |
    			XPLMGetDatai(qpac_capt_ias_valid) << 1 |
    			XPLMGetDatai(qpac_capt_alt_valid) ;
        } else if (pa_a320_ready) {
        	pa_a320_failures = XPLMGetDatai(pa_a320_ir_capt_avail);
        	// if 0 : whole PFD failed
        	if (pa_a320_failures == 0) qpac_failures = 0; else
        	if (pa_a320_failures < 3 ) qpac_failures = 0x77;
        }
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FAILURES);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_failures );
        i++;
        // Brakes & Tyres
        if (XPLMGetDatai(qpac_autobrake_low) > 0) {
        	auto_brake_level=1;
        	}
        else if (XPLMGetDatai(qpac_autobrake_med) > 0) {
        	auto_brake_level=2;
        	}
        else if (XPLMGetDatai(qpac_autobrake_max) > 0) {
        	auto_brake_level=4;
        	}
        else {
        	auto_brake_level=0;
        	}
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AUTO_BRAKE_LEVEL);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) auto_brake_level );
        i++;
        // Brake temperatures
        // Tyre pressures
        if (pa_a320_ready) {
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_);
            sim_packet.sim_data_points[i].value = custom_htonf( (float) 0.0f );
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+1);
            sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_brake_temp1) );
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+2);
            sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_brake_temp2) );
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+3);
            sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_brake_temp3) );
            i++;
            sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+4);
            sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_brake_temp4) );
            i++;

            if (pa_a320_version==733) {
                sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_);
                sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_tyre_press_f) );
                i++;
                sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+1);
                sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_tyre_press_l1) );
                i++;
                sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+2);
                sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_tyre_press_l2) );
                i++;
                sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+3);
                sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_tyre_press_r1) );
                i++;
                sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+4);
                sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(pa_a320_tyre_press_r2) );
                i++;
            }
        } else {

        	if (qpac_version>=204) {
                XPLMGetDatavf(qpac_brake_temp_array, qpac_brake_temp_tab, 0, 3);
                XPLMGetDatavf(qpac_tire_press_array, qpac_tyre_press_tab, 0, 3);

        		qpac_tyre_delta_t = 1+(((273.15f + XPLMGetDataf(oat))-286.15f) / 286.15f);

        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire1) == 6) ? 0.0f : 180.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire2) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+1);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire3) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+2);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire4) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+3);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire5) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+4);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;

        		// qpac_brake_fan;

        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_);
        		sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(oat) );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+1);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_brake_temp_tab[0] );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+2);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_brake_temp_tab[1] );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+3);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_brake_temp_tab[2] );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+4);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_brake_temp_tab[3] );
        		i++;
        	} else {
        		// Ref psi nose gear : 180 psi
        		// Ref psi main gear : 200 psi
        		qpac_tyre_delta_t = 1+(((273.15f + XPLMGetDataf(oat))-286.15f) / 286.15f);

        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire1) == 6) ? 0.0f : 180.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire2) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+1);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire3) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+2);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire4) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+3);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		qpac_tyre_pressure = (XPLMGetDatai(sim_op_fail_rel_tire5) == 6) ? 0.0f : 200.0f * qpac_tyre_delta_t;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TYRE_PSI_+4);
        		sim_packet.sim_data_points[i].value = custom_htonf( qpac_tyre_pressure );
        		i++;
        		// Brake temperature not managed - return oat
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_);
        		sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(oat) );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+1);
        		sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(oat) );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+2);
        		sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(oat) );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+3);
        		sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(oat) );
        		i++;
        		sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BRAKE_TEMP_+4);
        		sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(oat) );
        		i++;
        	}

        }

        // Triple Pressure indicator
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TPI_LEFT);
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(qpac_tot_left_brake) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TPI_RIGHT);
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(qpac_tot_right_brake) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_TPI_ACCU);
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(qpac_brake_accu) );
        i++;
        // Flaps and slats
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FLAPS_REQ_POS);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatai(qpac_flaps_request_pos) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_SLATS_REQ_POS);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatai(qpac_slats_request_pos) );
        i++;

        // ECAM
        // Spoilers qpac_spoilers, transmitted in a 2 bits field set
        // TODO : check qpac_spoilers_array type (int or float)
        if (qpac_spoilers_array != NULL) {
        	XPLMGetDatavi(qpac_spoilers_array, qpac_spoilers_tab, 0, 10);
        	spoilers = (qpac_spoilers_tab[0] & 0x03) |
        					(qpac_spoilers_tab[2] & 0x03) << 2 |
        					(qpac_spoilers_tab[4] & 0x03) << 4 |
        					(qpac_spoilers_tab[6] & 0x03) << 6 |
        					(qpac_spoilers_tab[8] & 0x03) << 8 ;
         	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_SPOILERS_LEFT);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) spoilers );
        	i++;
        	spoilers = qpac_spoilers_tab[1] |
        					qpac_spoilers_tab[3] << 2 |
        					qpac_spoilers_tab[5] << 4 |
        					qpac_spoilers_tab[7] << 6 |
        					qpac_spoilers_tab[9] << 8 ;
        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_SPOILERS_RIGHT);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) spoilers );
        	i++;
        } else {
         	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_SPOILERS_LEFT);
        	sim_packet.sim_data_points[i].value = custom_htonf( 3.0f );
        	i++;
        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_SPOILERS_RIGHT);
        	sim_packet.sim_data_points[i].value = custom_htonf( 7.0f );
        	i++;
        }


    	// ELAC and SEC control computers
        if (qpac_fcc_avail_array != NULL) {
        	XPLMGetDatavi(qpac_fcc_avail_array, qpac_fcc_tab, 0, 5);
        	qpac_fcc = 0;
        	for (j=0; j<5; j++) {
        		qpac_fcc |= (qpac_fcc_tab[j] & 0x01) << j;
        	}
        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FCC);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fcc );
        	i++;
        } else {
        	// dataref not available on QPAC v1, turn all computers on
        	qpac_fcc = 0xFF;
        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FCC);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fcc );
        	i++;
        }

    	// Rudder limit
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ALPHA_MAX);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_rudder_limit_pos));
        i++;

        // Hydraulics
        XPLMGetDatavf(qpac_hyd_pressure_array, qpac_hyd_press_tab, 0, 3);
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_G_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_hyd_press_tab[0]);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_Y_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_hyd_press_tab[1]);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_B_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_hyd_press_tab[2]);
        i++;

        XPLMGetDatavf(qpac_hyd_sys_qty_array, qpac_hyd_qty_tab, 0, 3);
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_G_QTY);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_hyd_qty_tab[0]);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_Y_QTY);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_hyd_qty_tab[1]);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_B_QTY);
        sim_packet.sim_data_points[i].value = custom_htonf(qpac_hyd_qty_tab[2]);
        i++;


        // Hydraulic pumps
        // Each pump status is on 2 bits
        if (qpac_hyd_pump_array != NULL) {
        	XPLMGetDatavi(qpac_hyd_pump_array, qpac_hyd_pump_tab, 0, 3);
        	hyd_pumps = 0;
        	for (j=0; j<3; j++) {
        		hyd_pumps |= (qpac_hyd_pump_tab[j] & 0x03) << (j*2);
        	}
        	// Shift 6 bits left for rat, ptu and elec pumps
        	hyd_pumps <<= 6;
        	hyd_pumps |=
        			(XPLMGetDatai(qpac_hyd_rat_mode) & 0x03 ) |
        			(XPLMGetDatai(qpac_hyd_y_elec_mode) & 0x03) << 2 |
        			(XPLMGetDatai(qpac_hyd_ptu_mode) & 0x03) << 4 ;

        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_PUMPS);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) hyd_pumps );
        	i++;
        } else {
        	// dataref not available on QPAC v1, turn all pumps on
        	// TODO : find out the right value !
        	hyd_pumps = 0xFF;
        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_HYD_PUMPS);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fcc );
        	i++;
        }

        // Cabin Pressure
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_CABIN_DELTA_P);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cabin_delta_p));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_CABIN_ALT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cabin_alt));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_CABIN_VS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cabin_vs));
        i++;

        qpac_air_valves =
        		(XPLMGetDatai(qpac_cond_hot_air_valve) & 0x01) << 1 ;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_HOT_AIR_VALVES);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_air_valves );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_OUTFLOW_VALVE);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_outflow_valve));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_COCKPIT_TRIM);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cond_cockpit_trim));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_ZONE1_TRIM);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cond_zone1_trim));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_ZONE2_TRIM);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_cond_zone2_trim));
        i++;

        // Bleed Air
        bleed_valves =
        		(XPLMGetDatai(qpac_bleed_x) & 0x03)  |
        		(XPLMGetDatai(qpac_bleed_apu) & 0x03) << 2 |
        		(XPLMGetDatai(qpac_bleed_eng1) & 0x03) << 4 |
        		(XPLMGetDatai(qpac_bleed_eng2) & 0x03) << 6 |
        		(XPLMGetDatai(qpac_bleed_eng1_hp) & 0x03) << 8 |
        		(XPLMGetDatai(qpac_bleed_eng2_hp) & 0x03) << 10 |
				(XPLMGetDatai(qpac_bleed_pack1_fcv) & 0x03) << 12 |
				(XPLMGetDatai(qpac_bleed_pack2_fcv) & 0x03) << 14;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BLEED_VALVES);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) bleed_valves );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BLEED_LEFT_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_bleed_left_press));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BLEED_RIGHT_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_bleed_right_press));
        i++;

        // Ignition valves and switches
        XPLMGetDatavi(qpac_start_valve_array, qpac_start_valve_tab, 0, 4);
        qpac_ignition =
        		(qpac_start_valve_tab[0] & 0x01) |
        		(qpac_start_valve_tab[1] & 0x01) << 1 |
        		(qpac_start_valve_tab[2] & 0x01) << 2 |
        		(qpac_start_valve_tab[3] & 0x01) << 3 |
        		(XPLMGetDatai(qpac_ewd_start_mode) & 0x01) << 4 |
        		(XPLMGetDatai(qpac_eng_mode_switch) & 0x03) << 6;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ENG_IGNITION);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_ignition );
        i++;

        XPLMGetDatavf(qpac_nacelle_temp_array, qpac_nacelle_temp_tab, 0, 2);
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_NACELLE_TEMP_ + 0);
        sim_packet.sim_data_points[i].value = custom_htonf( qpac_nacelle_temp_tab[0] );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_NACELLE_TEMP_ + 1);
        sim_packet.sim_data_points[i].value = custom_htonf( qpac_nacelle_temp_tab[1] );
        i++;

        // ATA 21 CONDITIONING

        // Grab SD Cabin temperatures- SD Page ID = 7
        if ( XPLMGetDatai(qpac_sd_page) == 7 ) {
        	// SD line 4 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[3],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>24) {
            	// Cockpit
            	qpac_temp = (qpac_sd_line_buffer[7]>='0'?(qpac_sd_line_buffer[7]-'0')*10.0f:0.0f) + (qpac_sd_line_buffer[8]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_COCKPIT_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// Forward
            	qpac_temp = (qpac_sd_line_buffer[15]>='0'?(qpac_sd_line_buffer[15]-'0')*10.0f:0.0f) + (qpac_sd_line_buffer[16]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_FWD_CABIN_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// AFT
            	qpac_temp = (qpac_sd_line_buffer[23]>='0'?(qpac_sd_line_buffer[23]-'0')*10.0f:0.0f) + (qpac_sd_line_buffer[24]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_AFT_CABIN_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;
            }
        	// SD line 6 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[5],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>24) {
            	// Cockpit
            	qpac_temp = (qpac_sd_line_buffer[7]-'0')*10.0f + (qpac_sd_line_buffer[8]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_COCKPIT_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// Forward
            	qpac_temp = (qpac_sd_line_buffer[15]-'0')*10.0f + (qpac_sd_line_buffer[16]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_FWD1_CABIN_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// AFT
            	qpac_temp = (qpac_sd_line_buffer[23]-'0')*10.0f + (qpac_sd_line_buffer[24]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_AFT1_CABIN_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;
            }
        }
        // Grab SD Cabin cruise temperatures- SD Page ID = 11
        if ( XPLMGetDatai(qpac_sd_page) == 11 ) {
        	// SD line 15 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[14],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>17) {
            	// Cockpit
            	qpac_temp = (qpac_sd_line_buffer[3]>='0'?(qpac_sd_line_buffer[3]-'0')*10.0f:0.0f) + (qpac_sd_line_buffer[4]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_COCKPIT_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// Forward
            	qpac_temp = (qpac_sd_line_buffer[9]>='0'?(qpac_sd_line_buffer[9]-'0')*10.0f:0.0f) + (qpac_sd_line_buffer[10]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_FWD_CABIN_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// AFT
            	qpac_temp = (qpac_sd_line_buffer[16]>='0'?(qpac_sd_line_buffer[16]-'0')*10.0f:0.0f) + (qpac_sd_line_buffer[17]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_AFT_CABIN_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;
            }
        }

        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_FLOW);
        sim_packet.sim_data_points[i].value = custom_htonf((XPLMGetDataf(qpac_bleed_pack1_flow)-0.8f)*2.5f);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_FLOW);
        sim_packet.sim_data_points[i].value = custom_htonf((XPLMGetDataf(qpac_bleed_pack2_flow)-0.8f)*2.5f);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_BYPASS_RATIO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_bleed_pack1_temp));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_BYPASS_RATIO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_bleed_pack2_temp));
        i++;
        // Grab SD PACK temperatures and bleed air temperature - SD Page ID = 1
        if ( XPLMGetDatai(qpac_sd_page) == 1 ) {
        	// SD line 13 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[12],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>24) {
            	// Left Bleed air temperature
            	qpac_temp = (qpac_sd_line_buffer[5]>='0'?(qpac_sd_line_buffer[5]-'0')*100.0f:0.0f) +
            			(qpac_sd_line_buffer[6]>='0'?(qpac_sd_line_buffer[6]-'0')*10.0f:0.0f) +
            			(qpac_sd_line_buffer[7]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BLEED_LEFT_PRESS_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// Right bleed air temperature
            	qpac_temp = (qpac_sd_line_buffer[27]>='0'?(qpac_sd_line_buffer[27]-'0')*100.0f:0.0f) +
            			(qpac_sd_line_buffer[28]>='0'?(qpac_sd_line_buffer[28]-'0')*10.0f:0.0f) +
            			(qpac_sd_line_buffer[29]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_BLEED_RIGHT_PRESS_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;
            }
        	// SD line 5 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[4],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>28) {
            	// Pack 1 Compressor Outlet Temperature
            	qpac_temp = (qpac_sd_line_buffer[4]>='0'?(qpac_sd_line_buffer[4]-'0')*100.0f:0.0f) +
            			(qpac_sd_line_buffer[5]>='0'?(qpac_sd_line_buffer[5]-'0')*10.0f:0.0f) +
            			(qpac_sd_line_buffer[6]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_COMP_OUTLET_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// Pack 2 Compressor Outlet Temperature
            	qpac_temp = (qpac_sd_line_buffer[26]>='0'?(qpac_sd_line_buffer[26]-'0')*100.0f:0.0f) +
            			(qpac_sd_line_buffer[27]>='0'?(qpac_sd_line_buffer[27]-'0')*10.0f:0.0f) +
            			(qpac_sd_line_buffer[28]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_COMP_OUTLET_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;
            }
        	// SD line 2 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[1],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>27) {
            	// Pack 1 Outlet Temperature
            	qpac_temp = (qpac_sd_line_buffer[4]>='0'?(qpac_sd_line_buffer[4]-'0')*100.0f:0.0f) +
            			(qpac_sd_line_buffer[5]>='0'?(qpac_sd_line_buffer[5]-'0')*10.0f:0.0f) +
            			(qpac_sd_line_buffer[6]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;

            	// Pack 2 Outlet Temperature
            	qpac_temp = (qpac_sd_line_buffer[26]>='0'?(qpac_sd_line_buffer[26]-'0')*100.0f:0.0f) +
            			(qpac_sd_line_buffer[27]>='0'?(qpac_sd_line_buffer[27]-'0')*10.0f:0.0f) +
            			(qpac_sd_line_buffer[27]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_TEMP);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_temp);
            	i++;
            }
        }

        // bit array (2 bits per valve) : LSB order : PACK 1, PACK 2, RAM AIR, CAB FAN1, CAB FAN2, PRESS MAN MODE (1bit)
        // ram_air_valve = XPLMGetDataf(qpac_bleed_ram_air_valve);
    	qpac_air_valves = ((XPLMGetDatai(qpac_bleed_ram_air) & 0x01) << 5) |
    			(XPLMGetDatai(qpac_cabin_man_press_mode) & 0x01);
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_AIR_VALVES);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_air_valves );
    	i++;

        /*
        qpac_cond_hot_air_valve = XPLMFindDataRef("AirbusFBW/HotAirValve");
        // Bleed
        qpac_bleed_intercon = XPLMFindDataRef("AirbusFBW/BleedIntercon");
        qpac_bleed_ram_air [integer]
        qpac_bleed_ram_air_valve [float] range 0 to 1
        // APU
        qpac_apu_egt = XPLMFindDataRef("AirbusFBW/");
        qpac_apu_egt_limit = XPLMFindDataRef("AirbusFBW/");
    	*/

        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_APU_EGT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(qpac_apu_egt));
        i++;

        // ATA 28 FUEL
        // Fuel pumps
        // Each pump status is on 2 bits
        // 0 = OFF amber(and automatic mode should be ON)
        // 1 = ON green
        // 2 = OFF green (automatic OFF or OFF when automatic should be OFF)
        // 3 = Low Pressure amber
        // Pumps index 2 and 3 are the center pumps with auto mode
        if (qpac_fuel_auto_pump_sd_array != NULL) {
        	XPLMGetDatavi(qpac_fuel_auto_pump_sd_array, qpac_fuel_auto_pump_tab, 0, 6);
        	XPLMGetDatavi(qpac_fuel_pump_ohp_array, qpac_fuel_pump_tab, 0, 6);
        	if (qpac_fuel_auto_pump_tab[2] == 0) qpac_fuel_auto_pump_tab[2] = 2;
        	if (qpac_fuel_auto_pump_tab[3] == 0) qpac_fuel_auto_pump_tab[3] = 2;
        	qpac_fuel_pumps = (qpac_fuel_pump_tab[0] & 0x03)
        	 | (qpac_fuel_pump_tab[1] & 0x03) << 2
        	 | (qpac_fuel_auto_pump_tab[2] & 0x03) << 4
        	 | (qpac_fuel_auto_pump_tab[3] & 0x03) << 6
        	 | (qpac_fuel_pump_tab[4] & 0x03) << 8
        	 | (qpac_fuel_pump_tab[5] & 0x03) << 10;
        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FUEL_PUMPS);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fuel_pumps );
        	i++;
        }
        // Fuel valves
        // Each valve status is on 2 bits
        if (qpac_fuel_xfv_array != NULL) {
        	qpac_fuel_valves = 0;
        	XPLMGetDatavi(qpac_fuel_eng_lp_valve_array, qpac_fuel_valves_tab, 0, 2);
        	for (j=0; j<2; j++) {
        		qpac_fuel_valves |= (qpac_fuel_valves_tab[j] & 0x03) << (j*2);
        	}
        	// X-Fer valve is on 3 bits
        	XPLMGetDatavi(qpac_fuel_xfv_array, qpac_fuel_valves_tab, 0, 1);
        	qpac_fuel_valves |= (qpac_fuel_valves_tab[0] & 0x07) << 8;

        	// Transfer valves from outer tanks to inner tanks
        	// Boolean, 4 bits
        	XPLMGetDatavi(qpac_fuel_tv_array, qpac_fuel_valves_tab, 0, 4);
        	for (j=0; j<4; j++) {
        		qpac_fuel_valves |= (qpac_fuel_valves_tab[j] & 0x01) << (11+j);
        	}

        	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_FUEL_VALVES);
        	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fuel_valves );
        	i++;
        }


        // Doors
        if (qpac_door_pax_array != NULL) {
        	XPLMGetDatavf(qpac_door_pax_array, qpac_door_pax_tab, 0, 4);
        	XPLMGetDatavf(qpac_door_cargo_array, qpac_door_cargo_tab, 0, 4);
        	door_status = (XPLMGetDataf(qpac_door_bulk_door)>0.0f) |
        			(qpac_door_pax_tab[0]>0.0) << 1 |
        			(qpac_door_pax_tab[1]>0.0) << 2 |
        			(qpac_door_pax_tab[2]>0.0) << 3 |
        			(qpac_door_pax_tab[3]>0.0) << 4 |
        			(qpac_door_cargo_tab[0]>0.0) << 5 |
        			(qpac_door_cargo_tab[1]>0.0) << 6 ;
        } else {
        	// TODO: based on beacon
        	door_status = 0x01;
        }
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_DOOR_STATUS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) door_status);
        i++;

        // ELEC
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ELEC_AC_CROSS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_elec_ac_cross_connect));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ELEC_CX_LEFT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_elec_connect_left));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ELEC_CX_CENTER);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_elec_connect_center));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ELEC_CX_RIGHT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_elec_connect_right));
        i++;
        XPLMGetDatavi(qpac_elec_ohp_array, qpac_elec_oph_tab, 0, 4);
        qpac_elec_buttons = qpac_elec_oph_tab[0] |
        		qpac_elec_oph_tab[1] << 1 |
        		qpac_elec_oph_tab[2] << 2 |
        		qpac_elec_oph_tab[3] << 3;
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ELEC_BUTTONS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) qpac_elec_buttons);
        i++;


        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_SD_PAGE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_sd_page));
        i++;

        // Grab SD Engine vibration values - Engine SD Page = 0
        if ( XPLMGetDatai(qpac_sd_page) == 0 ) {
        	// SD line 11 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[10],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>26) {
            	// Engine 1
            	qpac_vib_n2 = (qpac_sd_line_buffer[10]-'0')*10.0f + (qpac_sd_line_buffer[12]-'0');
            	// qpac_vib_n2 = (qpac_sd_line_buffer[12]-'0')/10;
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;

            	// Engine 2
            	qpac_vib_n2 = (qpac_sd_line_buffer[23]-'0')*10.0f + (qpac_sd_line_buffer[25]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_+1);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;
            }
        	// SD line 12 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[11],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>26) {
            	// Engine 1
            	qpac_vib_n2 = (qpac_sd_line_buffer[10]-'0')*10.0f + (qpac_sd_line_buffer[12]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_N2_);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;

            	// Engine 2
            	qpac_vib_n2 = (qpac_sd_line_buffer[23]-'0')*10.0f + (qpac_sd_line_buffer[25]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_N2_+1);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;
            }
        }
        // Grab SD Engine vibration values - Cruise SD Page = 11
        if ( XPLMGetDatai(qpac_sd_page) == 11 ) {
        	// SD line 7 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[6],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>26) {
            	// Engine 1
            	qpac_vib_n2 = (qpac_sd_line_buffer[8]-'0')*10.0f + (qpac_sd_line_buffer[10]-'0');
            	// qpac_vib_n2 = (qpac_sd_line_buffer[12]-'0')/10;
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;

            	// Engine 2
            	qpac_vib_n2 = (qpac_sd_line_buffer[25]-'0')*10.0f + (qpac_sd_line_buffer[27]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_+1);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;
            }
        	// SD line 8 green
            qpac_sd_data_len = XPLMGetDatab(qpac_sd_line_green[7],qpac_sd_line_buffer,0,sizeof(qpac_sd_line_buffer));
            qpac_sd_line_len = (qpac_sd_data_len > 0) ? (int)strlen(qpac_sd_line_buffer) : 0;
            if (qpac_sd_line_len>26) {
            	// Engine 1
            	qpac_vib_n2 = (qpac_sd_line_buffer[8]-'0')*10.0f + (qpac_sd_line_buffer[10]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_N2_);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;

            	// Engine 2
            	qpac_vib_n2 = (qpac_sd_line_buffer[25]-'0')*10.0f + (qpac_sd_line_buffer[27]-'0');
            	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FLIGHTMODEL_ENGINE_VIB_N2_+1);
            	sim_packet.sim_data_points[i].value = custom_htonf(qpac_vib_n2);
            	i++;
            }
        }

        // Anti-ice
        anti_ice =
        		(XPLMGetDatai(qpac_wing_anti_ice) & 0x03)  |
        		(XPLMGetDatai(qpac_wing_anti_ice_lights) & 0x03) << 2 |
        		(XPLMGetDatai(qpac_eng1_anti_ice) & 0x03) << 4 |
        		(XPLMGetDatai(qpac_eng1_anti_ice_lights) & 0x03) << 6 |
        		(XPLMGetDatai(qpac_eng2_anti_ice) & 0x03) << 8 |
        		(XPLMGetDatai(qpac_eng2_anti_ice_lights) & 0x03) << 10 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(QPAC_ANTI_ICE_STATUS);
    	sim_packet.sim_data_points[i].value = custom_htonf((float)anti_ice);
    	i++;
    }


    sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_STATUS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) jar_a320_neo_ready );
    i++;
    if ( jar_a320_neo_ready ) {

        // Approach
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APPR_TYPE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_ap_appr_type));
        i++;
        // sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APPR_TYPE);
        // sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_ap_appr_illuminated));
        // i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APPR_DH);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_fma_dh_alt));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APPR_MDA);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_fma_mda_alt));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FMA_CAT);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_fma_cat_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FMA_DUAL);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_fma_dual_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FMA_DH);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_fma_dh_mode));
        i++;

		// FCU
        fcu_data =
        		XPLMGetDatai(jar_a320_neo_ap1) << 8 |
    			XPLMGetDatai(jar_a320_neo_ap2) << 7 |
        		XPLMGetDatai(jar_a320_neo_hdg_managed) << 6 |
        		XPLMGetDatai(jar_a320_neo_hdg_dashed) << 5 |
        		XPLMGetDatai(jar_a320_neo_spd_managed) << 4 |
        		XPLMGetDatai(jar_a320_neo_fd) << 3 |
        		XPLMGetDatai(jar_a320_neo_vs_dashed) << 2 |
        		XPLMGetDatai(jar_a320_neo_fcu_metric_alt) << 1 |
        		XPLMGetDatai(jar_a320_neo_fcu_hdg_trk) ;
        fcu_baro =
        			// XPLMGetDatai(qpac_baro_std_fo) << 6 |
        			XPLMGetDatai(jar_a320_neo_baro_hpa) << 5 |
        			// XPLMGetDatai(qpac_baro_hide_fo) << 4 |
        			// XPLMGetDatai(qpac_baro_std_capt) << 2 |
        			XPLMGetDatai(jar_a320_neo_baro_hpa) << 1  // |
        			// XPLMGetDatai(qpac_baro_hide_capt)
        			;
        ap_appr =
        		XPLMGetDatai(jar_a320_neo_ap_loc_illuminated) << 1 |
        		XPLMGetDatai(jar_a320_neo_ap_appr_illuminated) ;

        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FCU);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) fcu_data );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FCU_BARO);
        sim_packet.sim_data_points[i].value = custom_htonf((float) fcu_baro);
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APPR);
        sim_packet.sim_data_points[i].value = custom_htonf((float) ap_appr);
        i++;


        // Auto-pilot
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AP_PHASE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_ap_phase));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AP_VERTICAL_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_vert_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AP_VERTICAL_ARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_vert_wait));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AP_LATERAL_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_lat_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AP_LATERAL_ARMED);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_lat_wait));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AP_COMMON_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_com_mode));
        i++;
        // JAR_A320NEO_ALT_IS_CSTR
        // JAR_A320NEO_CONSTRAINT_ALT

        // Auto-Thrust
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_ATHR_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_athr_mode));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_THR_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_thr_mode));
        i++;
        // sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_ATHR_LIMITED);
        // sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_athr_limited));
        // i++;
        // sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_THR_LEVER_MODE);
        // sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_thr_lever_mode));
        // i++;
        // sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FMA_THR_WARNING);
        // sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(qpac_fma_thr_warning));
        // i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FLEX_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_flex_t));
        i++;


        // V Speeds
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_V1);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_v1));
        i++;
                sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_VR);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_vr));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_VMO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_vmax));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_VLS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_vls));
        i++;
        //sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_VF);
        //sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_vf)));
        //i++;
        //sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_VS);
        //sim_packet.sim_data_points[i].value = custom_htonf(qpac_ias_shift(XPLMGetDataf(qpac_vs)));
        //i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_V_GREEN_DOT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_vgrdot));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_ALPHA_PROT);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_vaprot));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_ALPHA_MAX);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_vamax));
        i++;

        // EFIS
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_ND_MODE);
        sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(jar_a320_neo_nd_mode));
        i++;

        // Brakes
        if (XPLMGetDatai(jar_a320_neo_autobrake_low) == 1) {
        	auto_brake_level=1;
        	}
        else if (XPLMGetDatai(jar_a320_neo_autobrake_med) == 1) {
        	auto_brake_level=2;
        	}
        else if (XPLMGetDatai(jar_a320_neo_autobrake_max) == 1) {
        	auto_brake_level=4;
        	}
        else {
        	auto_brake_level=0;
        	}
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AUTO_BRAKE_LEVEL);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) auto_brake_level );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_ACCU_PSI);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_brakes_accu_press));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_LEFT_PSI);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_brakes_left_press));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_RIGHT_PSI);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_brakes_right_press));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_TEMP_ + 0 );
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_wheels_temp_l_1));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_TEMP_ + 1 );
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_wheels_temp_l_2));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_TEMP_ + 2 );
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_wheels_temp_r_1));
    	i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_TEMP_ + 3 );
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_wheels_temp_r_2));
    	i++;
        brake_status =
        		XPLMGetDatai(jar_a320_neo_wheels_ped_disc) << 2 |
        		XPLMGetDatai(jar_a320_neo_wheels_brake_fan) << 1 |
        		XPLMGetDatai(jar_a320_neo_wheels_brake_hot) ;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BRAKE_STATUS);
        sim_packet.sim_data_points[i].value = custom_htonf((float) brake_status);
        i++;

        // Hydraulics
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_HYD_B_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_hydr_b_press_aft_acc_old));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_HYD_G_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_hydr_g_press_aft_acc_old));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_HYD_Y_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_hydr_y_press_aft_acc_old));
        i++;
        // Hydraulic pumps
        // Each pump status is on ??? bits
        // jar_a320_neo_hydr_b_elec_pump_fault_light
        // jar_a320_neo_hydr_g_eng_pump_fault_light
        // jar_a320_neo_hydr_y_elec_pump_fault_light
        // jar_a320_neo_hydr_y_eng_pump_fault_light
        hyd_pumps =
        		(XPLMGetDatai(jar_a320_neo_hydr_g_eng_pump_mode) &0x01) |
        		(XPLMGetDatai(jar_a320_neo_hydr_g_eng_pump_fault_light) &0x01) << 1 |
        		(XPLMGetDatai(jar_a320_neo_hydr_y_eng_pump_mode) &0x01) << 2 |
        		(XPLMGetDatai(jar_a320_neo_hydr_y_eng_pump_fault_light) &0x01) << 3 |
        		(XPLMGetDatai(jar_a320_neo_hydr_b_elec_pump_mode) & 0x01) << 4 |
        		(XPLMGetDatai(jar_a320_neo_hydr_b_elec_pump_fault_light) & 0x01) << 5 ;
        // Shift 6 bits left for rat, ptu and elec pumps
        // jar_a320_neo_hydr_ptu_delta is a float
        hyd_pumps <<= 6;
        hyd_pumps |=
        		(XPLMGetDatai(qpac_hyd_rat_mode) & 0x03 ) |
        		(XPLMGetDatai(jar_a320_neo_hydr_y_elec_pump_mode) & 0x01) << 2 |
        		(XPLMGetDatai(jar_a320_neo_hydr_y_elec_pump_fault_light) & 0x01) << 3 |
        		(XPLMGetDatai(jar_a320_neo_hydr_ptu_mode) & 0x01) << 4;
				// (XPLMGetDatai(jar_a320_neo_hydr_ptu_delta) & 0x01) << 5;

        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_HYD_PUMPS);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) hyd_pumps );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_HYD_PTU);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_hydr_ptu_delta));
        i++;

        // Doors
        door_status =  (XPLMGetDataf(jar_a320_neo_doors_c_b_now) > 0.01) |
        		(XPLMGetDataf(jar_a320_neo_doors_c_f_now) > 0.01) << 1 |
        		(XPLMGetDataf(jar_a320_neo_doors_p_b_l_now) > 0.01) << 2 |
        		(XPLMGetDataf(jar_a320_neo_doors_p_b_r_now) > 0.01) << 3 |
        		(XPLMGetDataf(jar_a320_neo_doors_p_f_l_now) > 0.01) << 4 |
        		(XPLMGetDataf(jar_a320_neo_doors_p_f_r_now) > 0.01) << 5;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_DOOR_STATUS);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) door_status );
        i++;

        // ATA 21 Conditioning
    	// Cockpit
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_COCKPIT_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cockpit_temp));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_COCKPIT_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cockpit_duct));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_COCKPIT_TRIM);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cockpit_trm_valve));
    	i++;

    	// Forward
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_FWD_CABIN_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_fwd_temp));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_FWD1_CABIN_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_fwd_duct));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_ZONE1_TRIM);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_fwd_trm_valve));
    	i++;

    	// AFT
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_AFT_CABIN_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_aft_temp));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_AFT1_CABIN_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_aft_duct));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_ZONE2_TRIM);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_aft_trm_valve));
    	i++;

    	// CARGO FWD
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_FWD_CARGO_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cargo_fwd_temp));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_CARGO_FWD_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cargo_fwd_duct));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_CARGO_FWD_TRIM);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cargo_fwd_trm_valve));
    	i++;

    	// CARGO AFT
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_AFT_CARGO_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cargo_aft_temp));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_INLET_CARGO_AFT_TEMP);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cargo_aft_duct));
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_CARGO_AFT_TRIM);
    	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_cargo_aft_trm_valve));
    	i++;

    	// TODO: Valves
    	// Hot air valves - bit arrays (2 bits per valve)
    	// LSB order : Hot Air 1, Hot Air 2, Cargo 1, Cargo 2
    	qpac_air_valves = ((XPLMGetDatai(jar_a320_neo_cond_hot_air) & 0x03) ) |
    			((XPLMGetDatai(jar_a320_neo_cond_cargo_hot_air) & 0x03) << 4);
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_HOT_AIR_VALVES);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_air_valves );
    	i++;

    	// PACKs
    	// bit array (2 bits per valve) : LSB order : PACK 1, PACK 2, RAM AIR, CAB FAN1, CAB FAN2, PRESS MAN MODE (1bit)
    	qpac_air_valves = ((XPLMGetDatai(jar_a320_neo_cond_ram_air) & 0x01) << 5) |
    			(!(XPLMGetDatai(jar_a320_neo_press_mode)) & 0x01);
    	sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_AIR_VALVES);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_air_valves );
    	i++;

    	/*
        XPLMDataRef jar_a320_neo_cond_aft_duct;
        XPLMDataRef jar_a320_neo_cond_aft_temp;
        XPLMDataRef jar_a320_neo_cond_aft_trm_valve;

        XPLMDataRef jar_a320_neo_cond_cabin_aft_duct;
        XPLMDataRef jar_a320_neo_cond_cabin_aft_temp;
        XPLMDataRef jar_a320_neo_cond_cabin_aft_trm_valve;
        XPLMDataRef jar_a320_neo_cond_cabin_aft_valve;

        XPLMDataRef jar_a320_neo_cond_cabin_fwd_duct;
        XPLMDataRef jar_a320_neo_cond_cabin_fwd_temp;
        XPLMDataRef jar_a320_neo_cond_cabin_fwd_trm_valve;
        XPLMDataRef jar_a320_neo_cond_cabin_fwd_valve;

        XPLMDataRef jar_a320_neo_cond_cockpit_duct;
        XPLMDataRef jar_a320_neo_cond_cockpit_temp;
        XPLMDataRef jar_a320_neo_cond_cockpit_trm_valve;

        XPLMDataRef jar_a320_neo_cond_econ_flow;

        XPLMDataRef jar_a320_neo_cond_fwd_duct;
        XPLMDataRef jar_a320_neo_cond_fwd_temp;
        XPLMDataRef jar_a320_neo_cond_fwd_trm_valve;

        XPLMDataRef jar_a320_neo_cond_hot_air;
        XPLMDataRef jar_a320_neo_cond_hot_air_temp;

        XPLMDataRef jar_a320_neo_cond_pack1;
        XPLMDataRef jar_a320_neo_cond_pack12_line;
        XPLMDataRef jar_a320_neo_cond_pack1_comp_deg;
        XPLMDataRef jar_a320_neo_cond_pack1_f;
        XPLMDataRef jar_a320_neo_cond_pack1_flow;
        XPLMDataRef jar_a320_neo_cond_pack1_line;
        XPLMDataRef jar_a320_neo_cond_pack1_ndl;
        XPLMDataRef jar_a320_neo_cond_pack1_out_deg;

        XPLMDataRef jar_a320_neo_cond_pack2;
        XPLMDataRef jar_a320_neo_cond_pack2_comp_deg;
        XPLMDataRef jar_a320_neo_cond_pack2_f;
        XPLMDataRef jar_a320_neo_cond_pack2_flow;
        XPLMDataRef jar_a320_neo_cond_pack2_line;
        XPLMDataRef jar_a320_neo_cond_pack2_ndl;
        XPLMDataRef jar_a320_neo_cond_pack2_out_deg;

        XPLMDataRef jar_a320_neo_cond_ram_air;
        */

    	// ATA 36 PNEUMATIC / Bleed Air
    	/* Unused datarefs
    	 * jar_a320_neo_bleed_eng1_bleed_knob;
    	 * jar_a320_neo_bleed_eng2_bleed_knob;
    	*/
        bleed_valves =
        		(XPLMGetDatai(jar_a320_neo_bleed_cross_valve) & 0x03)  |
        		(XPLMGetDatai(jar_a320_neo_bleed_apu_bleed_valve) & 0x03) << 2 |
        		(XPLMGetDatai(jar_a320_neo_bleed_eng1_bleed_valve) & 0x03) << 4 |
        		(XPLMGetDatai(jar_a320_neo_bleed_eng2_bleed_valve) & 0x03) << 6 |
        		(XPLMGetDatai(jar_a320_neo_bleed_eng1_bleed_hp_valve) & 0x03) << 8 |
        		(XPLMGetDatai(jar_a320_neo_bleed_eng2_bleed_hp_valve) & 0x03) << 10 |
				(XPLMGetDatai(jar_a320_neo_cond_pack1) & 0x03) << 12 |
				(XPLMGetDatai(jar_a320_neo_cond_pack2) & 0x03) << 14;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BLEED_VALVES);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) bleed_valves );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BLEED_LEFT_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_bleed_eng1_bleed_psi));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BLEED_RIGHT_PRESS);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_bleed_eng2_bleed_psi));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BLEED_LEFT_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_bleed_eng1_bleed_temp));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_BLEED_RIGHT_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_bleed_eng2_bleed_temp));
        i++;

        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_COMP_OUTLET_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack1_comp_deg));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_COMP_OUTLET_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack2_comp_deg));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack1_out_deg));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_TEMP);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack2_out_deg));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_BYPASS_RATIO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack1_ndl));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_BYPASS_RATIO);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack2_ndl));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK1_FLOW);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack1_flow));
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_COND_PACK2_FLOW);
        sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(jar_a320_neo_cond_pack2_flow));
        i++;

        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_NACELLE_TEMP_ + 0);
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_eng_1_nac_temp) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_NACELLE_TEMP_ + 1);
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_eng_2_nac_temp) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_SD_PAGE);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) XPLMGetDatai(jar_a320_disp_sys_mode) );
        i++;

        // Anti-ice
        anti_ice =
        		(XPLMGetDatai(jar_a320_neo_icerain_wing) & 0x03)  |
        		(XPLMGetDatai(jar_a320_neo_icerain_wing_flt) & 0x03) << 2 |
        		(XPLMGetDatai(jar_a320_neo_icerain_eng1) & 0x03) << 4 |
        		(XPLMGetDatai(jar_a320_neo_icerain_eng2) & 0x03) << 8 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_ANTI_ICE_STATUS);
    	sim_packet.sim_data_points[i].value = custom_htonf((float)anti_ice);
    	i++;

        // FUEL
        // Fuel pumps
        // Each pump status is on 2 bits
    	qpac_fuel_pumps =
    			(XPLMGetDatai(jar_a320_neo_fuel_t1_pump1) & 0x03) |
    			(XPLMGetDatai(jar_a320_neo_fuel_t1_pump2) & 0x03) << 2 |
    			(XPLMGetDatai(jar_a320_neo_fuel_t2_pump1) & 0x03) << 4 |
    			(XPLMGetDatai(jar_a320_neo_fuel_t2_pump2) & 0x03) << 6 |
    			(XPLMGetDatai(jar_a320_neo_fuel_t3_pump1) & 0x03) << 8 |
    			(XPLMGetDatai(jar_a320_neo_fuel_t3_pump2) & 0x03) << 10 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FUEL_PUMPS);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fuel_pumps );
    	i++;

        /* Fuel valves
         * Each engine fuel valve status is on 2 bits
    	 * 0-1 : engine 0
    	 * 1-2 : engine 1
    	 * 3-4 : engine 2
    	 * 5-6 : engine 3
    	 * 7-8-9: X-Feed valve
    	 * 10-11 : Left transfer valves
    	 * 12-13 : Right transfer valves
    	 */
    	qpac_fuel_valves = (XPLMGetDatai(jar_a320_neo_eng_2_fuel_valve) & 0x03) << 2 |
    			(XPLMGetDatai(jar_a320_neo_eng_1_fuel_valve) & 0x03) |
    			(XPLMGetDatai(jar_a320_neo_fuel_xfeed) & 0x07) << 8 |
    			(XPLMGetDatai(jar_a320_neo_fuel_inn_out_left) & 0x03) << 11 |
    			(XPLMGetDatai(jar_a320_neo_fuel_inn_out_right) & 0x03) << 13;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_FUEL_VALVES);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) qpac_fuel_valves );
    	i++;

    	// Spoilers
    	spoilers= ((XPLMGetDataf(left_wing_spoiler_1_def[1])>12.5) & 0x01) |
    			  ((XPLMGetDataf(left_wing_spoiler_1_def[1])>1.0) & 0x01) << 2 |
    			  ((XPLMGetDataf(left_wing_spoiler_1_def[1])>1.0) & 0x01) << 4 |
    			  ((XPLMGetDataf(left_wing_spoiler_2_def[1])>1.0) & 0x01) << 6 |
    			  ((XPLMGetDataf(left_wing_spoiler_2_def[1])>0.5) & 0x01) << 8 ;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_SPOILERS_LEFT);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) spoilers );
    	i++;
    	spoilers= ((XPLMGetDataf(right_wing_spoiler_1_def[1])>12.5) & 0x01) |
    			  ((XPLMGetDataf(right_wing_spoiler_1_def[1])>1.0) & 0x01) << 2 |
    			  ((XPLMGetDataf(right_wing_spoiler_1_def[1])>1.0) & 0x01) << 4 |
    			  ((XPLMGetDataf(right_wing_spoiler_2_def[1])>1.0) & 0x01) << 6 |
    			  ((XPLMGetDataf(right_wing_spoiler_2_def[1])>0.5) & 0x01) << 8 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_SPOILERS_RIGHT);
    	sim_packet.sim_data_points[i].value = custom_htonf( (float) spoilers );
    	i++;

    	// ATA24 Electrics

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_EXT_HZ);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_ext_hz) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_EXT_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_ext_volt) );
    	i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN1_HZ);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen1_hz) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN1_PER);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen1_per) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN1_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen1_volt) );
    	i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN2_HZ);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen2_hz) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN2_PER);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen2_per) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN2_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen2_volt) );
    	i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APU_HZ);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_apu_hz) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APU_PER);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_apu_per) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_APU_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_apu_volt) );
    	i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN_EM_HZ);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen_emer_hz) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN_EM_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_gen_emer_volt) );
    	i++;

    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_TR1_AMP);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_tr1_amp) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_TR1_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_tr1_volt) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_TR2_AMP);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_tr2_amp) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_TR2_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_tr2_volt) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_TR_EM_AMP);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_tr_em_amp) );
    	i++;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_TR_EM_VOLT);
    	sim_packet.sim_data_points[i].value =  custom_htonf( XPLMGetDataf(jar_a320_neo_elec_tr_em_volt) );
    	i++;

    	elec_status=
    			(XPLMGetDatai(jar_a320_neo_elec_dc1) & 0x03) |
    			(XPLMGetDatai(jar_a320_neo_elec_dc2) & 0x03) << 2 |
    			(XPLMGetDatai(jar_a320_neo_elec_dcbus) & 0x01) << 4 |
    			(XPLMGetDatai(jar_a320_neo_elec_dc_ess) & 0x01) << 5 |
    			(XPLMGetDatai(jar_a320_neo_elec_dc_ess_shed) & 0x01) << 6 |
    			(XPLMGetDatai(jar_a320_neo_elec_dc_some_on) & 0x01) << 7 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_DC_STATUS);
    	sim_packet.sim_data_points[i].value =  custom_htonf( (float) elec_status );
    	i++;
    	elec_status =
    	    	(XPLMGetDatai(jar_a320_neo_elec_ac1_source) & 0x07) |
    	    	(XPLMGetDatai(jar_a320_neo_elec_ac2_source) & 0x07) << 3 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_ac_ess) & 0x03) << 6 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_ac_ess_alt) & 0x01) << 8 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_ac_ess_shed) & 0x01) << 9 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_bus_tie) & 0x01) << 10 ;
    	    	// (XPLMGetDatai(jar_a320_neo_elec_commrc) & 0x01) << 11 |
    	  	    // (XPLMGetDatai(jar_a320_neo_elec_galley) & 0x01) << 12 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_AC_STATUS);
    	sim_packet.sim_data_points[i].value =  custom_htonf( (float) elec_status );
    	i++;
    	elec_status =
    	    	(XPLMGetDatai(jar_a320_neo_elec_lft_gen_on) & 0x01) |
    	    	(XPLMGetDatai(jar_a320_neo_elec_rgh_gen_on) & 0x01) << 1 |
    			(XPLMGetDatai(jar_a320_neo_elec_apu_gen_on) & 0x01) << 2 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_gpu_on) & 0x01) << 3 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_gpu_av) & 0x01) << 4 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_emer)  & 0x01) << 5 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_gen1_line_on) & 0x01) << 6 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_man_rat_on) & 0x01) << 7 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_rat_av) & 0x01) << 8 |
    	    	(XPLMGetDatai(jar_a320_neo_elec_rat_on) & 0x01) << 9;
    	sim_packet.sim_data_points[i].id = custom_htoni(JAR_A320NEO_GEN_STATUS);
    	sim_packet.sim_data_points[i].value =  custom_htonf( (float) elec_status );
    	i++;


    }

    if (xjoymap_ready) {
    	xjoymap_stick = (XPLMGetDatai(xjoymap_side_stick_priority) & 0x03)  |
        		(XPLMGetDatai(xjoymap_dual_input) & 0x01) << 4 ;
    	sim_packet.sim_data_points[i].id = custom_htoni(XJOYMAP_STICK_PRIORITY);
    	sim_packet.sim_data_points[i].value = custom_htonf((float)xjoymap_stick);
    	i++;
    }

	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );

	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_custom_avio_size) {
            max_custom_avio_size = packet_size;
            sprintf(msg, "XHSI: max packet size so far for Custom AVIO: %d\n", max_custom_avio_size);
            XPLMDebugString(msg);
            if ( i > MAX_DATAPOINTS ) {
                sprintf(msg, "XHSI: max number of sim data points exceeded for Custom AVIO: %d (max: %d)\n", i, MAX_DATAPOINTS);
                XPLMDebugString(msg);
            }
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
    int extinguisher;
    int extinguishers[8];
    int ign_keys_tab[8];
    int ign_keys;
    int fadec;
    int fadecs[8];


    strncpy(sim_packet.packet_id, "ENGI", 4);


    if (jar_a320_neo_ready || pa_a320_ready) {
    	// Fix JarDesign that set num_tank=1 instead of 5
    	// Fix PeterAircraft that set num_tank=6 instead of 5
    	tanks = 5;
    } else {
    	tanks = XPLMGetDatai(num_tanks);
    }
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_WEIGHT_ACF_M_FUEL_TOT);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(fuel_capacity));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_WEIGHT_M_TOTAL);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(m_total));
    i++;
	
    if (jar_a320_neo_ready) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ );
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_fuel_t2) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ + 1 );
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_fuel_t1) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ + 2 );
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_fuel_t3) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ + 3 );
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_fuel_t0) );
        i++;
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ + 4 );
        sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(jar_a320_neo_fuel_t4) );
        i++;
    } else {
        XPLMGetDatavf(fuel_quantity, fuelfloat, 0, tanks);
        for (e=0; e<tanks; e++) {
            sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_FUEL_QUANTITY_ + e);
            sim_packet.sim_data_points[i].value = custom_htonf( fuelfloat[e] );
            i++;
        }
    }

    XPLMGetDatavf(mfd_fuel_used, engifloat, 0, engines);
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(XHSI_FUEL_USED_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
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

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ENGINE_MAX_EGT);
    sim_packet.sim_data_points[i].value = custom_htonf( XPLMGetDataf(engine_max_egt_value) );
    i++;

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

    XPLMGetDatavf(oil_p_psi, engifloat, 0, engines);
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_PSI_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
    }

    XPLMGetDatavf(oil_t_ratio, engifloat, 0, engines);
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
    }

    XPLMGetDatavf(oil_t_c, engifloat, 0, engines);
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_C_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
    }

    XPLMGetDatavf(oil_q_ratio, engifloat, 0, engines);
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
    }

    XPLMGetDatavf(throttle_ratio, engifloat, 0, engines);
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ENGINE_ACTUATORS_THROTTLE_RATIO_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
    }

    // Let QPAC value override if present
    if (!qpac_ready) {
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
    }

    // Ignitors (ignition_key, igniter_on, ignition_on) - to send as an 8 bits field for the 3 datarefs
    // Fadec to send as an 8 bits field
    XPLMGetDatavi(ignition_key, ign_keys_tab, 0, engines);
    for (e=0, ign_keys=0; e<engines; e++) {
    	ign_keys |= (ign_keys_tab[e] & 0x07) << (e*3);
    }
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_ENGINE_IGN_KEY);
    sim_packet.sim_data_points[i].value = custom_htonf((float) ign_keys);
    i++;


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

    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_EICAS_OVERRIDE_TRQ_MAX);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(override_trq_max));
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

    if (qpac_ready && (qpac_eng_epr_array != NULL ) && (engines <= 4)) {
    	XPLMGetDatavf(qpac_eng_epr_array, engifloat, 0, engines);
    } else {
    	XPLMGetDatavf(engine_epr, engifloat, 0, engines);
    }
    for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_EPR_ + e);
        sim_packet.sim_data_points[i].value = custom_htonf( engifloat[e] );
        i++;
    }

    // Fire Extinguishers : bit field, one bit per engine
    extinguisher = 0;
    XPLMGetDatavi(engine_fire_extinguisher, extinguishers, 0, engines);
    for (e=0; e<engines; e++) {
        extinguisher |= ((extinguishers[e] & 0x01) << e);
    }
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ENGINE_FIRE_EXTINGUISHER);
    sim_packet.sim_data_points[i].value = custom_htonf((float) extinguisher );
    i++;

    // FADECs : bit field, one bit per engine
    fadec = 0;
    XPLMGetDatavi(engine_fadec, fadecs, 0, engines);
    for (e=0; e<engines; e++) {
    	fadec |= ((fadecs[e] & 0x01) << e);
    }
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_ENGINE_FADEC);
    sim_packet.sim_data_points[i].value = custom_htonf((float) fadec );
    i++;

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
    if ( packet_size > max_engi_size) {
        max_engi_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far for ENGI: %d\n", max_engi_size);
        XPLMDebugString(msg);
        if ( i > MAX_DATAPOINTS ) {
            sprintf(msg, "XHSI: max number of sim data points exceeded for ENGI: %d (max: %d)\n", i, MAX_DATAPOINTS);
            XPLMDebugString(msg);
        }
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
    char registration[40];
    // default tire reference pressures
    float nose_tire_ref_pressure = 180.0f;
    float main_tire_ref_pressure = 200.0f;

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


    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_TAILNUM_0_3);
    XPLMGetDatab(acf_tailnum, registration, 0, 40);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, registration, 4 );
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_VIEW_ACF_TAILNUM_4_7);
    strncpy( (char *)&sim_packet.sim_data_points[i].value, registration + 4, 4 );
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
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_PARTS_ACF_SBRKEQ);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(speedbrake_equiped));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_GEAR_ACF_GEAR_RETRACT);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(retractable_gear));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_OVERFLOW_ACF_VMCA);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vmca));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_OVERFLOW_ACF_VYSE);
    sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_vyse));
    i++;

	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACL_ELEV_DN);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_controls_elev_dn));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACL_ELEV_UP);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_controls_elev_up));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACL_AIL_DN);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_controls_ail_dn));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACL_AIL_UP);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_controls_ail_up));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_CONTROLS_ACL_RUDDER_LR);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(acf_controls_rudder_lr));
	i++;

	// ELEC Aircraft constants
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ELECTRICAL_NUM_BATTERIES);
    if (jar_a320_neo_ready) {
    	// Workarround, num batteries not well defined in JarDesign A320 neo, correct number is 2
    	sim_packet.sim_data_points[i].value = custom_htonf(2.0f);
    } else {
    	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(acf_batteries));
    }
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ELECTRICAL_NUM_BUSES);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(acf_buses));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ELECTRICAL_NUM_GENERATORS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(acf_generators));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ELECTRICAL_NUM_INVERTERS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(acf_inverters));
    i++;

    // Tyre pressure aircraft constants
    // TODO: Should be read from an aircraft configuration file
    if (pa_a320_ready) {
    	nose_tire_ref_pressure = 180.0f;
    	main_tire_ref_pressure = 200.0f;
    } else if (qpac_ready) {
    	nose_tire_ref_pressure = 180.0f;
    	main_tire_ref_pressure = 210.0f;
    }
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_AIRCRAFT_NOSE_TIRE_REF_PRESSURE);
    sim_packet.sim_data_points[i].value = custom_htonf(nose_tire_ref_pressure);
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_AIRCRAFT_MAIN_TIRE_REF_PRESSURE);
    sim_packet.sim_data_points[i].value = custom_htonf(main_tire_ref_pressure);
    i++;

    // ENGINE LIMITS
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ENGINE_RED_OIL_T);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(oil_t_red));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ENGINE_RED_OIL_P);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(oil_p_red));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_ENGINE_RED_EPR);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(engine_epr_red));
	i++;

    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_STYLE);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(xhsi_instrument_style));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_RWY_LENGTH_MIN);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(xhsi_rwy_length_min));
    i++;
    sim_packet.sim_data_points[i].id = custom_htoni(XHSI_RWY_UNITS);
    sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(xhsi_rwy_units));
    i++;


    // now we know the number of datapoints
    sim_packet.nb_of_sim_data_points = custom_htoni( i );

    // packet size : char[4] + int + ( # * ( int + float) )
    packet_size = 8 + i * 8;
    if ( packet_size > max_stat_size) {
        max_stat_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far for STAT: %d\n", max_stat_size);
        XPLMDebugString(msg);
        if ( i > MAX_DATAPOINTS ) {
            sprintf(msg, "XHSI: max number of sim data points exceeded for STAT: %d (max: %d)\n", i, MAX_DATAPOINTS);
            XPLMDebugString(msg);
        }
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

        while ( total_waypoints > 0
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

        //    if ( ( total_waypoints > 1 ) && ( cur_waypoint != total_waypoints ) ) {
        //        sprintf(msg, "XHSI: FMC: error count: %d %d\n", cur_waypoint, total_waypoints);
        //        XPLMDebugString(msg);
        //    }

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

int createRemoteCommandPacket(int command) {
    strncpy(rcmd_packet.packet_id, "RCMD", 4);
    rcmd_packet.command = custom_htoni(command);
    return 4 + 4;
}
