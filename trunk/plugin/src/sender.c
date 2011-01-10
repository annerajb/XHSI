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
#include "packets.h"
#include "structs.h"
#include "net.h"



float sendSimCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createSimPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				if ( sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1 ) {
					XPLMDebugString("XHSI: caught error while sending SIMD packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
			}
		}

		return nav_data_delay;

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
	int packet_size;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open)  {

		packet_size = createFmsPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				if (sendto(sockfd, (const char*)&fms_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
					XPLMDebugString("XHSI: caught error while sending FMSR packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
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

