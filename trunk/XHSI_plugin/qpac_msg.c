/*
 * qpac_msg.c
 *
 *  Created on: 17 july 2015
 *      Author: Nicolas Carel
 *
 *  Send QPAC MCDU and ECAM messages packets
 */

#include <stdio.h>
#include <string.h>

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif

#define XPLM200 1

#include "XPLMDataAccess.h"
#include "XPLMPlugin.h"
#include "XPLMUtilities.h"

#include "datarefs_qpac.h"
#include "qpac_msg.h"

#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"

// ECAM
XPLMDataRef qpac_ewd_red[QPAC_EWD_LINES];
XPLMDataRef qpac_ewd_blue[QPAC_EWD_LINES];
XPLMDataRef qpac_ewd_amber[QPAC_EWD_LINES];
XPLMDataRef qpac_ewd_green[QPAC_EWD_LINES];
XPLMDataRef qpac_ewd_white[QPAC_EWD_LINES];

// MCDU1
XPLMDataRef qpac_mcdu1_title_yellow;
XPLMDataRef qpac_mcdu1_title_blue;
XPLMDataRef qpac_mcdu1_title_green;
XPLMDataRef qpac_mcdu1_title_white;

XPLMDataRef qpac_mcdu1_label_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_label_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_label_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_label_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_label_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_label_magenta[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu1_small_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_magenta[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu1_content_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_magenta[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu1_scratch_yellow;
XPLMDataRef qpac_mcdu1_scratch_green;
XPLMDataRef qpac_mcdu1_scratch_white;

XPLMPluginID qpacPluginId = XPLM_NO_PLUGIN_ID;

struct QpacMsgLinesDataPacket qpacMsgPacket;

float qpac_msg_delay;

void findQpacMsgDataRefs(void) {
    int         i;
    char        buf[100];

    qpacPluginId = XPLMFindPluginBySignature("QPAC.airbus.fbw");
    if(qpacPluginId != XPLM_NO_PLUGIN_ID) {
        sprintf(buf, "XHSI: QPAC plugin found - loading EWD + MCDU datarefs\n");
        XPLMDebugString(buf);

        for (i=0; i<QPAC_EWD_LINES-1; i++) {
            sprintf(buf, "AirbusFBW/EWD%drText", i+1);
            qpac_ewd_red[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/EWD%dgText", i+1);
            qpac_ewd_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/EWD%dbText", i+1);
            qpac_ewd_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/EWD%dwText", i+1);
            qpac_ewd_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/EWD%daText", i+1);
            qpac_ewd_amber[i] = XPLMFindDataRef(buf);
        }

        for (i=0; i<QPAC_MCDU_LINES-1; i++) {
            sprintf(buf, "AirbusFBW/MCDU1label%dy", i+1);
            qpac_mcdu1_label_yellow[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1label%db", i+1);
            qpac_mcdu1_label_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1label%da", i+1);
            qpac_mcdu1_label_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1label%dg", i+1);
            qpac_mcdu1_label_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1label%dw", i+1);
            qpac_mcdu1_label_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1label%dm", i+1);
            qpac_mcdu1_label_magenta[i] = XPLMFindDataRef(buf);


            sprintf(buf, "AirbusFBW/MCDU1scont%dy", i+1);
            qpac_mcdu1_small_yellow[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1scont%db", i+1);
            qpac_mcdu1_small_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1scont%da", i+1);
            qpac_mcdu1_small_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1scont%dg", i+1);
            qpac_mcdu1_small_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1scont%dw", i+1);
            qpac_mcdu1_small_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1scont%dm", i+1);
            qpac_mcdu1_small_magenta[i] = XPLMFindDataRef(buf);

            sprintf(buf, "AirbusFBW/MCDU1cont%dy", i+1);
            qpac_mcdu1_content_yellow[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1cont%db", i+1);
            qpac_mcdu1_content_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1cont%da", i+1);
            qpac_mcdu1_content_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1cont%dg", i+1);
            qpac_mcdu1_content_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1cont%dw", i+1);
            qpac_mcdu1_content_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU1cont%dm", i+1);
            qpac_mcdu1_content_magenta[i] = XPLMFindDataRef(buf);
        }

        qpac_mcdu1_scratch_yellow = XPLMFindDataRef("AirbusFBW/MCDU1spy");
        qpac_mcdu1_scratch_white = XPLMFindDataRef("AirbusFBW/MCDU1spw");
        qpac_mcdu1_title_yellow = XPLMFindDataRef("AirbusFBW/MCDU1titley");
        qpac_mcdu1_title_blue = XPLMFindDataRef("AirbusFBW/MCDU1titleb");
        qpac_mcdu1_title_green = XPLMFindDataRef("AirbusFBW/MCDU1titleg");
        qpac_mcdu1_title_white = XPLMFindDataRef("AirbusFBW/MCDU1titlew");

    }
}

/*
 * A320 E/WD Engine Warning Display
 * Upper ECAM Messages + Memo
 */

int createQpacEwdPacket(void) {

   int i;

   strncpy(qpacMsgPacket.packet_id, "QPAE", 4);
   qpacMsgPacket.nb_of_lines = custom_htoni(QPAC_EWD_LINES);

   for(i=0; i<QPAC_EWD_LINES; i++){
     qpacMsgPacket.lines[i].lineno = custom_htoni(i);
     XPLMGetDatab(qpac_mcdu1_label_yellow[i],qpacMsgPacket.lines[i].linestr,0,sizeof(qpacMsgPacket.lines[i].linestr));
     qpacMsgPacket.lines[i].len = custom_htoni((int)strlen(qpacMsgPacket.lines[i].linestr));
   }

  return 4 + 4 + 4 + 14 * 88;

}

/*
 * Qpac A320 MCDU
 */
int createQpacMcduPacket(void) {

   int i;

   strncpy(qpacMsgPacket.packet_id, "QPAM", 4);
   qpacMsgPacket.nb_of_lines = custom_htoni(QPAC_MCDU_LINES);

   for(i=0; i<QPAC_MCDU_LINES; i++){
     qpacMsgPacket.lines[i].lineno = custom_htoni(i);
     XPLMGetDatab(qpac_mcdu1_label_yellow[i],qpacMsgPacket.lines[i].linestr,0,sizeof(qpacMsgPacket.lines[i].linestr));
     qpacMsgPacket.lines[i].len = custom_htoni((int)strlen(qpacMsgPacket.lines[i].linestr));
   }

  return 4 + 4 + 4 + 14 * 88;

}

float sendQpacMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int packet_size;

	// TODO: Store previous packet / Send if different

	qpac_msg_delay = adc_data_delay * 2.0f;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && qpac_ready)  {

		packet_size = createQpacMcduPacket();

        if ( packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&qpacMsgPacket, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending QpacMsg packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
                }
            }
            return qpac_msg_delay;
        } else {
            return qpac_msg_delay;
        }

	} else {
		return 10.0f;
	}

}
