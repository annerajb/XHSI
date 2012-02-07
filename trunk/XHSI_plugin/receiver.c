/*
 * receiver.c
 *
 * callback function to receive packets
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
#include "structs.h"
#include "packets.h"
#include "net.h"



float receiveCallback(
                   float	inElapsedSinceLastCall,
                   float	inElapsedTimeSinceLastFlightLoop,
                   int		inCounter,
                   void *	inRefcon) {

	int packet_size;
	//int addr_len;
	char debug_message[256];

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

        if ( pollReceive() ) {

            //addr_len = sizeof(orig_sockaddr);
            //packet_size = recvfrom(sockfd, (char *)&efis_packet, sizeof(struct CommandPacket), 0, (struct sockaddr *) &orig_sockaddr, &addr_len);
            packet_size = recv(sockfd, (char *)&efis_packet, sizeof(struct CommandPacket), 0);
            if ( packet_size < 0) {
                XPLMDebugString("XHSI: caught error while receiving packet! ");
                sprintf(debug_message, "errno: %d (", GET_ERRNO);
                XPLMDebugString(debug_message);
                XPLMDebugString((char * const) strerror(GET_ERRNO));
                XPLMDebugString(")\n");
                return 10.0f;
            } else {
                XPLMDebugString("XHSI: packet received\n");
                decodeCommandPacket();
                return recv_delay;
            }

        } else {
            return recv_delay;
        }

    } else {
		return 1.0f;
	}

}

