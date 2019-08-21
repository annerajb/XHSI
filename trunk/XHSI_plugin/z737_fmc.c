/**
 * z737_fmc.c
 *
 * Created on: 20 août 2019
 *
 * This code is managing Zibo Mod FMC for Laminar Boeing 737-800
 *
 * Copyright (C) 2014-2019  Nicolas Carel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include <stdio.h>
#include <string.h>

#define XPLM200 1

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
#include "datarefs_z737.h"
#include "datarefs.h"
#include "z737_fmc.h"

struct ExtendedFmsDataPacket	   z737_fmc_efms_packet[20];
int max_z737_fmc_efms_size = 0;



int createZibo737ExtendedFmsPackets(void) {

    char nav_id[256];
    char msg[80];
    int altitude;
    int speed;
    float lat;
    float lon;

    float ete;
    float gs;

    float fmc_lat[128];
    float fmc_lon[128];
    float fmc_speed[128];
    float fmc_alt[128];

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
    total_waypoints = (int) XPLMGetDataf(laminar_B738_fms_num_of_wpts);

    XPLMGetDatavf(laminar_B738_fms_legs_lat, fmc_lat, 0, 128);
    XPLMGetDatavf(laminar_B738_fms_legs_lon, fmc_lon, 0, 128);
    XPLMGetDatavf(laminar_B738_fms_legs_spd, fmc_speed, 0, 128);
    XPLMGetDatavf(laminar_B738_fms_legs_alt_rest1, fmc_alt, 0, 128);

    if (total_waypoints > 0) {

        sprintf(msg, "XHSI: Boeing 737-800 / Zibo Mod FMC: waypt_number=%d\n", total_waypoints);
        XPLMDebugString(msg);

        // displayed_entry = (int) XPLMGetDatai(ufmc_waypt_pln_index);
        // active_entry = (int) XPLMGetDatai(ufmc_waypt_pln_index);
        displayed_entry = 0;
        active_entry = 0;

        cur_entry = 0;
        //XPLMDebugString("XHSI: UFMC: cur_entry=");

        while ( total_waypoints > 0
                && cur_waypoint < total_waypoints
                && cur_entry < MAX_EXTENDED_FMS_ENTRIES_ALLOWED ) {

            sprintf(msg, " %d ", cur_entry);
            XPLMDebugString(msg);

            cur_pack = (int)cur_waypoint / 50;

            altitude = fmc_alt[cur_waypoint];
            lat = fmc_lon[cur_waypoint];
            lon = fmc_lat[cur_waypoint];
            speed = fmc_speed[cur_waypoint];
            // XPLMGetDatab(ufmc_waypt_name,nav_id,0,sizeof(ufmc_efms_packet[cur_pack].entries[cur_packpoint].id));
            // type = XPLMGetDatai(ufmc_waypt_type_altitude);
            type = 2048;
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

                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].type = custom_htoni( (int)type );
                if (type == xplm_Nav_LatLon) {
                    strncpy(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id, "Lat/Lon", sizeof(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id));
                } else {
                    strncpy(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id, nav_id, sizeof(z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].id));
                }
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].altitude = custom_htoni(altitude);
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].lat = custom_htonf(lat);
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].lon = custom_htonf(lon);
                z737_fmc_efms_packet[cur_pack].entries[cur_packpoint].speed = custom_htoni(speed);

                // get ready for the next waypoint
                cur_waypoint++;
                // at the end, cur_waypoint will be the same as total_waypoints
            }
        cur_entry++;
        }
        XPLMDebugString("\n");
        sprintf(msg, "XHSI: Zibo 737 FMC: last=%d\n", cur_entry);
        XPLMDebugString(msg);
        sprintf(msg, "XHSI: Zibo 737 FMC: count=%d\n", cur_waypoint);
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
        strncpy(z737_fmc_efms_packet[i].packet_id, "FMS", 3);
        z737_fmc_efms_packet[i].packet_id[3] = '0' + (unsigned char)i;
        //sprintf(msg, "XHSI: UFMC filling %c%c%c%c \n",fms_packet[i].packet_id[0],fms_packet[i].packet_id[1],fms_packet[i].packet_id[2],fms_packet[i].packet_id[3]);
        //XPLMDebugString(msg);
        z737_fmc_efms_packet[i].ete_for_active = custom_htonf( ete );
        z737_fmc_efms_packet[i].groundspeed = custom_htonf( gs );
        z737_fmc_efms_packet[i].nb_of_entries = custom_htoni( total_waypoints );
        z737_fmc_efms_packet[i].displayed_entry_index = custom_htoni( displayed_waypoint );
        z737_fmc_efms_packet[i].active_entry_index = custom_htoni( active_waypoint );
    }

    // packet size : char[4] + float + float + int + int + int + ( # * ( int + char[8] + float + float + float ) )
    // return 24 + cur_waypoint * 24;
    // return the number of waypoints; let the sender sort it out...
    return total_waypoints;

}

float sendZibo737ExtendedFmsCallback(
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


	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && fms_source==FMS_SOURCE_Z737 && z737_ready)  {

		waypoint_count = createZibo737ExtendedFmsPackets();
        last_pack = ( waypoint_count - 1 ) / MAX_FMS_ENTRIES_ALLOWED;

        for (j=0; j<=last_pack; j++) {

            if ( j == last_pack ) {
                packet_size = 24 + ( waypoint_count % MAX_FMS_ENTRIES_ALLOWED ) * 44;
            } else {
                packet_size = 24 + ( MAX_FMS_ENTRIES_ALLOWED ) * 44;
            }

            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    res = sendto(sockfd, (const char*)&z737_fmc_efms_packet[j], packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
                    if ( res == SOCKET_ERROR ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending ZiboMod FMCx packet! (");
                        sprintf(msg, "%d", WSAGetLastError());
                        XPLMDebugString(msg);
                        XPLMDebugString(")\n");
                    }
#else
                    if ( res < 0 ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending ZiboMod FMCx packet! (");
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
