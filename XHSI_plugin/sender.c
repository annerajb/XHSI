/*
 * sender.c
 *
 * callback functions to send the packets
 *
 * used by: plugin.c
 * uses: net.c, packets.c
 *
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPWidgets.h"
#include "XPStandardWidgets.h"


#include "globals.h"
#include "plugin.h"
#include "settings.h"
#include "structs.h"
#include "packets.h"
#include "net.h"



float sendADCCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;
	int res;
#if IBM
	char msg[80];
#endif

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createADCPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					XPLMDebugString("XHSI: caught error while sending ADCD packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					XPLMDebugString("XHSI: caught error while sending ADCD packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		return adc_data_delay;

	} else {
		return 1.0f;
	}

}


float sendAvionicsCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createAvionicsPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				if ( sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1 ) {
					XPLMDebugString("XHSI: caught error while sending AVIO packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
			}
		}

		return avionics_data_delay;

	} else {
		return 1.0f;
	}

}


float sendEnginesCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createEnginesPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				if ( sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1 ) {
					XPLMDebugString("XHSI: caught error while sending ENGI packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
			}
		}

		return engines_data_delay;

	} else {
		return 1.0f;
	}

}


float sendStaticCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createStaticPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				if ( sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1 ) {
					XPLMDebugString("XHSI: caught error while sending STAT packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
			}
		}

		return static_data_delay;

	} else {
		return 1.0f;
	}

}


float sendFmsCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int j;
	int waypoint_count;
	int packet_size;
	int last_pack;


	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open)  {

		waypoint_count = createFmsPackets();
        last_pack = ( waypoint_count - 1 ) / MAX_FMS_ENTRIES_ALLOWED;

        for (j=0; j<=last_pack; j++) {

            // packet size : char[4] + float + float + int + int + int + ( # * ( int + char[8] + float + float + float ) )
            // packet size : 24 + # * 24;
            if ( j == last_pack ) {
                packet_size = 24 + ( waypoint_count % MAX_FMS_ENTRIES_ALLOWED ) * 24;
            } else {
                packet_size = 24 + ( MAX_FMS_ENTRIES_ALLOWED ) * 24;
            }

            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&fms_packet[j], packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending FMCx packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
                }
            }

        }

		return fms_data_delay;

	} else {
		return 1.0f;
	}

}


float sendTcasCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int packet_size;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open)  {

		packet_size = createTcasPacket();

        if ( packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&tcas_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending TCAS packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
                }
            }
            return tcas_data_delay;
        } else {
            return 1.0f;
        }

	} else {
		return 1.0f;
	}

}

