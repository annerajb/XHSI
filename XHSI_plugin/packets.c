
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
#include "datarefs_qpac.h"
#include "datarefs_pa_a320.h"
#include "datarefs_jar_a320neo.h"
#include "endianess.h"



// Define global vars
// The data packets =========================================
struct SimDataPacket	sim_packet;
struct FmsDataPacket	fms_packet[10];
struct TcasDataPacket	tcas_packet;
struct IncomingPacket   efis_packet;

int max_packet_size = 0;
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
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CLOCK_TIMER_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(clock_timer_mode));
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

    sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_LIGHTS);
    if(x737_ready){
			sim_packet.sim_data_points[i].value = custom_htonf((float)
            (XPLMGetDatai(x737_beacon_light_switch) +
            XPLMGetDatai(x737_left_fixed_land_light_switch) * 2 +
            (XPLMGetDatai(x737_position_light_switch) != 0 ? 4 : 0) +
            (XPLMGetDatai(x737_position_light_switch) == -1 ? 8 : 0) +
            XPLMGetDatai(x737_taxi_light_switch) * 16));
    } else {
        sim_packet.sim_data_points[i].value = custom_htonf((float)
            (XPLMGetDatai(beacon_lights_on) +
            XPLMGetDatai(landing_lights_on) * 2 +
            XPLMGetDatai(nav_lights_on) * 4 +
            XPLMGetDatai(strobe_lights_on) * 8 +
            XPLMGetDatai(taxi_light_on) * 16));
    }
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
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_YOKE_PITCH_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(yoke_pitch_ratio));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT2_CONTROLS_YOKE_ROLL_RATIO);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(yoke_roll_ratio));
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
        sprintf(msg, "XHSI: max packet size so far (ADCD): %d\n", max_packet_size);
        XPLMDebugString(msg);
    }
	return packet_size;

}


int createAvionicsPacket(void) {

	int i = 0;
	int packet_size;
	char nav_id_bytes[4];
//	float gear_ratio[10];
	int std_gauges_failures_pilot;
	int std_gauges_failures_copilot;

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
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_COCKPIT_AUTOPILOT_HEADING_ROLL_MODE);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(autopilot_heading_roll_mode));
	i++;

	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );

	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far (AVIO): %d\n", max_packet_size);
        XPLMDebugString(msg);
    }
	return packet_size;

}


int createCustomAvionicsPacket(void) {

	int i = 0;
	int packet_size;
	char nav_id_bytes[4];
	char qpac_ils_char[12];
    int fcu_data;
    int fcu_baro;
    int ap_appr;
    int qpac_ils;
	int qpac_failures;
	int auto_brake_level;
	int pa_a320_failures;

	strncpy(sim_packet.packet_id, "AVIO", 4);


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
        // Brakes
        if (XPLMGetDatai(qpac_autobrake_low) == 1) {
        	auto_brake_level=1;
        	}
        else if (XPLMGetDatai(qpac_autobrake_med) == 1) {
        	auto_brake_level=2;
        	}
        else if (XPLMGetDatai(qpac_autobrake_max) == 1) {
        	auto_brake_level=4;
        	}
        else {
        	auto_brake_level=0;
        	}
        sim_packet.sim_data_points[i].id = custom_htoni(QPAC_AUTO_BRAKE_LEVEL);
        sim_packet.sim_data_points[i].value = custom_htonf( (float) auto_brake_level );
        i++;
    }

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
    }


	// now we know the number of datapoints
	sim_packet.nb_of_sim_data_points = custom_htoni( i );

	// packet size : char[4] + int + ( # * ( int + float) )
	packet_size = 8 + i * 8;
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far (AVIO): %d\n", max_packet_size);
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
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_WEIGHT_ACF_M_FUEL_TOT);
	sim_packet.sim_data_points[i].value = custom_htonf(XPLMGetDataf(fuel_capacity));
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

    XPLMGetDatavf(engine_epr, engifloat, 0, engines);
	for (e=0; e<engines; e++) {
        sim_packet.sim_data_points[i].id = custom_htoni(SIM_FLIGHTMODEL_ENGINE_ENGN_EPR_ + e);
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
        sprintf(msg, "XHSI: max packet size so far (ENGI): %d\n", max_packet_size);
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
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_PARTS_ACF_SBRKEQ);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(speedbrake_equiped));
	i++;
	sim_packet.sim_data_points[i].id = custom_htoni(SIM_AIRCRAFT_GEAR_ACF_GEAR_RETRACT);
	sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDatai(retractable_gear));
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
	if ( packet_size > max_packet_size) {
        max_packet_size = packet_size;
        sprintf(msg, "XHSI: max packet size so far (STAT): %d\n", max_packet_size);
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

