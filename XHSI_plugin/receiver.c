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
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"


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
//#if IBM
//	int addr_len;
//#else
//    socklen_t addr_len;
//#endif
	int last_errno;
	char debug_message[256];

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

//        if ( pollReceive() ) {

            // recvfrom needs for know where to listen
            //addr_len = sizeof(recv_sockaddr);
            //packet_size = recvfrom(sockfd, (char *)&efis_packet, sizeof(struct IncomingPacket), 0, (struct sockaddr *) &recv_sockaddr, &addr_len);
            // on the other hand, our socket is already bound to a port
            packet_size = recv(sockfd, (char *)&efis_packet, sizeof(struct IncomingPacket), 0);
            if ( packet_size < 0) {

                last_errno = GET_ERRNO;
#if IBM
                if ( last_errno == WSAEWOULDBLOCK ) {
#else
                if ( last_errno == EWOULDBLOCK ) {
#endif
                    return recv_delay;
                }

                XPLMDebugString("XHSI: caught error while receiving packet! ");
                sprintf(debug_message, "errno: %d (", last_errno);
                XPLMDebugString(debug_message);
                XPLMDebugString((char * const) strerror(last_errno));
                XPLMDebugString(")\n");
                return 5.0f;

            } else if ( packet_size == 0 ) {
                return recv_delay;
            } else {

                XPLMDebugString("XHSI: packet received\n");
                decodeIncomingPacket();
                return recv_delay;

            }

//        } else {
//            return recv_delay;
//        }

    } else {
		return 1.0f;
	}

}

