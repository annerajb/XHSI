/*
 * ufmc.c
 *
 *  Created on: 4 févr. 2018
 *      Author: Nicolas Carel
 */

#include <stdio.h>
#include <string.h>

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif


#include "XPLMDataAccess.h"
#include "XPLMPlugin.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"


#include "structs.h"

#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"
#include "datarefs_ufmc.h"
#include "datarefs.h"
#include "ufmc.h"

struct ExtendedFmsDataPacket	   ufmc_efms_packet[20];
int max_ufmc_efms_size = 0;

struct UfmcLinesDataPacket ufmcPacket;

float ufmc_delay;


int createUfmcPacket(void) {

   int i;
   // char str_test[80];
   strncpy(ufmcPacket.packet_id, "UFMC", 4);
   ufmcPacket.nb_of_lines = custom_htoni(NUM_UFMC_LINES);
   ufmcPacket.status = custom_htoni(XPLMGetDatai(ufmc_flight_phase));
   for(i=0; i<NUM_UFMC_LINES; i++){
     ufmcPacket.lines[i].lineno = custom_htoni(i);
     XPLMGetDatab(ufmc_line[i],ufmcPacket.lines[i].linestr,0,sizeof(ufmcPacket.lines[i].linestr));
     // sprintf(str_test,"LINE %d",i);
     // strcpy(ufmcPacket.lines[i].linestr, str_test);
     ufmcPacket.lines[i].len = custom_htoni((int)strlen(ufmcPacket.lines[i].linestr));
   }

  return 4 + 4 + 4 + 14 * 88;

}



float sendUfmcCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int packet_size;


	ufmc_delay = adc_data_delay * 3.0f;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && (ufmcPluginId != XPLM_NO_PLUGIN_ID))  {

		packet_size = createUfmcPacket();

        if ( packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&ufmcPacket, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending UFMC packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
                }
            }
            return ufmc_delay;
        } else {
            return ufmc_delay;
        }

	} else {
		return 10.0f;
	}

}

/*
 * Under development, not used yet
 */
int createUfmcExtendedFmsPackets(void) {

    char nav_id[256];
    char msg[80];
    int altitude;
    int speed;
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

    int type;


    int i;

    int cur_pack = 0;

    // the actual number of waypoints, not counting zero'd waypoints
    total_waypoints = (int) XPLMGetDatai(ufmc_waypt_number);

    if (total_waypoints > 0) {

        sprintf(msg, "XHSI: UFMC: waypt_number=%d\n", total_waypoints);
        XPLMDebugString(msg);

        displayed_entry = (int) XPLMGetDatai(ufmc_waypt_pln_index);
        active_entry = (int) XPLMGetDatai(ufmc_waypt_pln_index);

        cur_entry = 0;
        //XPLMDebugString("XHSI: UFMC: cur_entry=");

        while ( total_waypoints > 0
                && cur_waypoint < total_waypoints
                && cur_entry < MAX_EXTENDED_FMS_ENTRIES_ALLOWED ) {

            sprintf(msg, " %d ", cur_entry);
            XPLMDebugString(msg);

            cur_pack = (int)cur_waypoint / 50;
            XPLMSetDatai(ufmc_waypt_index, cur_waypoint);

            altitude = XPLMGetDatai(ufmc_waypt_altitude);
            lat = XPLMGetDataf(ufmc_waypt_lat);
            lon = XPLMGetDataf(ufmc_waypt_lon);
            speed = (int) XPLMGetDataf(ufmc_waypt_speed);
            XPLMGetDatab(ufmc_waypt_name,nav_id,0,sizeof(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id));
            type = XPLMGetDatai(ufmc_waypt_type_altitude);
/*
            XPLMGetFMSEntryInfo(
                    cur_entry,
                    &type,
                    nav_id,
                    &outRef,
                    &altitude,
                    &lat,
                    &lon);
*/
            // only send non-zero entries
            if ( type != 255 ) {
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

                ufmc_efms_packet[cur_pack].entries[cur_packpoint].type = custom_htoni( (int)type );
                if (type == xplm_Nav_LatLon) {
                    strncpy(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id, "Lat/Lon", sizeof(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id));
                } else {
                    strncpy(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id, nav_id, sizeof(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id));
                }
                ufmc_efms_packet[cur_pack].entries[cur_packpoint].altitude = custom_htoni(altitude);
                ufmc_efms_packet[cur_pack].entries[cur_packpoint].lat = custom_htonf(lat);
                ufmc_efms_packet[cur_pack].entries[cur_packpoint].lon = custom_htonf(lon);
                ufmc_efms_packet[cur_pack].entries[cur_packpoint].speed = custom_htoni(speed);

                // get ready for the next waypoint
                cur_waypoint++;
                // at the end, cur_waypoint will be the same as total_waypoints
            }
        cur_entry++;
        }
        XPLMDebugString("\n");
        sprintf(msg, "XHSI: UFMC: last=%d\n", cur_entry);
        XPLMDebugString(msg);
        sprintf(msg, "XHSI: UFMC: count=%d\n", cur_waypoint);
        XPLMDebugString(msg);

        //    if ( ( total_waypoints > 1 ) && ( cur_waypoint != total_waypoints ) ) {
        //        sprintf(msg, "XHSI: UFMC: error count: %d %d\n", cur_waypoint, total_waypoints);
        //        XPLMDebugString(msg);
        //    }

    }

    ete = XPLMGetDataf(gps_dme_time_secs);
    gs = XPLMGetDataf(groundspeed);

    //    for (i = 0; i <= ((total_waypoints-1)/MAX_FMS_ENTRIES_POSSIBLE); i++) {
    for (i = 0; i <= cur_pack; i++) {
        strncpy(ufmc_efms_packet[i].packet_id, "FMS", 3);
        ufmc_efms_packet[i].packet_id[3] = '0' + (unsigned char)i;
        //sprintf(msg, "XHSI: UFMC filling %c%c%c%c \n",fms_packet[i].packet_id[0],fms_packet[i].packet_id[1],fms_packet[i].packet_id[2],fms_packet[i].packet_id[3]);
        //XPLMDebugString(msg);
        ufmc_efms_packet[i].ete_for_active = custom_htonf( ete );
        ufmc_efms_packet[i].groundspeed = custom_htonf( gs );
        ufmc_efms_packet[i].nb_of_entries = custom_htoni( total_waypoints );
        ufmc_efms_packet[i].displayed_entry_index = custom_htoni( displayed_waypoint );
        ufmc_efms_packet[i].active_entry_index = custom_htoni( active_waypoint );
    }

    // packet size : char[4] + float + float + int + int + int + ( # * ( int + char[8] + float + float + float ) )
    // return 24 + cur_waypoint * 24;
    // return the number of waypoints; let the sender sort it out...
    return total_waypoints;

}

float sendUfmcExtendedFmsCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int j;
	int waypoint_count;
	int packet_size;
	int last_pack;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif


	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && fms_source==FMS_SOURCE_UFMC)  {

		waypoint_count = createUfmcExtendedFmsPackets();
        last_pack = ( waypoint_count - 1 ) / MAX_FMS_ENTRIES_ALLOWED;

        for (j=0; j<=last_pack; j++) {

            if ( j == last_pack ) {
                packet_size = 24 + ( waypoint_count % MAX_FMS_ENTRIES_ALLOWED ) * 44;
            } else {
                packet_size = 24 + ( MAX_FMS_ENTRIES_ALLOWED ) * 44;
            }
//sprintf(pack_msg, "XHSI: sending %c%c%c%c \n",fms_packet[j].packet_id[0],fms_packet[j].packet_id[1],fms_packet[j].packet_id[2],fms_packet[j].packet_id[3]);
//XPLMDebugString(pack_msg);

            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    res = sendto(sockfd, (const char*)&ufmc_efms_packet[j], packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
                    if ( res == SOCKET_ERROR ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending UFMC FMCx packet! (");
                        sprintf(msg, "%d", WSAGetLastError());
                        XPLMDebugString(msg);
                        XPLMDebugString(")\n");
                    }
#else
                    if ( res < 0 ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending UFMC FMCx packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
#endif
                }
            }

        }

		if ( send_error )
			return 5.0f;
		else
			return fms_data_delay;

	} else {
		return 1.0f;
	}

}

