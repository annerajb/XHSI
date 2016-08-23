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
#include "structs.h"
#include "datarefs.h"

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
XPLMDataRef qpac_mcdu1_label_special[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu1_small_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_magenta[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_small_special[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu1_content_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_magenta[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu1_content_special[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu1_scratch_yellow;
XPLMDataRef qpac_mcdu1_scratch_green;
XPLMDataRef qpac_mcdu1_scratch_white;
XPLMDataRef qpac_mcdu1_scratch_amber;

//
// MCDU2
XPLMDataRef qpac_mcdu2_title_yellow;
XPLMDataRef qpac_mcdu2_title_blue;
XPLMDataRef qpac_mcdu2_title_green;
XPLMDataRef qpac_mcdu2_title_white;

XPLMDataRef qpac_mcdu2_label_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_label_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_label_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_label_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_label_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_label_magenta[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_label_special[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu2_small_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_small_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_small_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_small_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_small_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_small_magenta[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_small_special[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu2_content_yellow[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_content_blue[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_content_amber[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_content_green[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_content_white[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_content_magenta[QPAC_MCDU_LINES];
XPLMDataRef qpac_mcdu2_content_special[QPAC_MCDU_LINES];

XPLMDataRef qpac_mcdu2_scratch_yellow;
XPLMDataRef qpac_mcdu2_scratch_green;
XPLMDataRef qpac_mcdu2_scratch_white;
XPLMDataRef qpac_mcdu2_scratch_amber;

XPLMPluginID qpacV1PluginId = XPLM_NO_PLUGIN_ID;
XPLMPluginID qpacPaPluginId = XPLM_NO_PLUGIN_ID;
XPLMPluginID qpacV2PluginId = XPLM_NO_PLUGIN_ID;
int qpac_mcdu_ready = 0;
int qpac_ewd_ready = 0;

struct QpacEwdMsgLinesDataPacket qpacEwdMsgPacket;
struct QpacMcduMsgLinesDataPacket qpacMcduMsgPacket;

// Store previous MCDU packets to compare before sending
struct QpacMcduMsgLinesDataPacket qpacMcdu1PreviousMsgPacket;
struct QpacMcduMsgLinesDataPacket qpacMcdu2PreviousMsgPacket;

// Store previous EWD packets to compare before sending
struct QpacEwdMsgLinesDataPacket qpacEwdPreviousMsgPacket;

float qpac_msg_delay;

void findQpacMsgDataRefs(void) {
    int         i;
    char        buf[100];

    qpacV2PluginId = XPLMFindPluginBySignature("QPAC.airbus.fbw");  // QPAC version 2
    qpacV1PluginId = XPLMFindPluginBySignature("QPAC.A320.airbus.fbw");  // QPAC version 1
    qpacPaPluginId = XPLMFindPluginBySignature("A320.airbus.fbw");  // QPAC Peter Aircraft
    // TODO: RWDesign A330 plugin signature
    if((qpacV1PluginId != XPLM_NO_PLUGIN_ID) || (qpacV2PluginId != XPLM_NO_PLUGIN_ID) || (qpacPaPluginId != XPLM_NO_PLUGIN_ID)) {
        sprintf(buf, "XHSI: QPAC plugin found - loading EWD datarefs\n");
        XPLMDebugString(buf);
        qpac_ewd_ready = 1;

        for (i=0; i<QPAC_EWD_LINES; i++) {
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
    } else {
    	qpac_ewd_ready = 0;
    }

    if((qpacV2PluginId != XPLM_NO_PLUGIN_ID) || (qpacPaPluginId != XPLM_NO_PLUGIN_ID)) {
    	sprintf(buf, "XHSI: QPAC v2 plugin found - loading MCDU datarefs\n");
    	XPLMDebugString(buf);
    	qpac_mcdu_ready = 1;

        for (i=0; i<QPAC_MCDU_LINES-1; i++) {
        	// MCDU1
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
            sprintf(buf, "AirbusFBW/MCDU1label%ds", i+1);
            qpac_mcdu1_label_special[i] = XPLMFindDataRef(buf);

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
            sprintf(buf, "AirbusFBW/MCDU1scont%ds", i+1);
            qpac_mcdu1_small_special[i] = XPLMFindDataRef(buf);

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
            sprintf(buf, "AirbusFBW/MCDU1cont%ds", i+1);
            qpac_mcdu1_content_special[i] = XPLMFindDataRef(buf);

        	// MCDU2
            sprintf(buf, "AirbusFBW/MCDU2label%dy", i+1);
            qpac_mcdu2_label_yellow[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2label%db", i+1);
            qpac_mcdu2_label_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2label%da", i+1);
            qpac_mcdu2_label_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2label%dg", i+1);
            qpac_mcdu2_label_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2label%dw", i+1);
            qpac_mcdu2_label_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2label%dm", i+1);
            qpac_mcdu2_label_magenta[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2label%ds", i+1);
            qpac_mcdu2_label_special[i] = XPLMFindDataRef(buf);

            sprintf(buf, "AirbusFBW/MCDU2scont%dy", i+1);
            qpac_mcdu2_small_yellow[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2scont%db", i+1);
            qpac_mcdu2_small_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2scont%da", i+1);
            qpac_mcdu2_small_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2scont%dg", i+1);
            qpac_mcdu2_small_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2scont%dw", i+1);
            qpac_mcdu2_small_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2scont%dm", i+1);
            qpac_mcdu2_small_magenta[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2scont%ds", i+1);
            qpac_mcdu2_small_special[i] = XPLMFindDataRef(buf);

            sprintf(buf, "AirbusFBW/MCDU2cont%dy", i+1);
            qpac_mcdu2_content_yellow[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2cont%db", i+1);
            qpac_mcdu2_content_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2cont%da", i+1);
            qpac_mcdu2_content_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2cont%dg", i+1);
            qpac_mcdu2_content_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2cont%dw", i+1);
            qpac_mcdu2_content_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2cont%dm", i+1);
            qpac_mcdu2_content_magenta[i] = XPLMFindDataRef(buf);
            sprintf(buf, "AirbusFBW/MCDU2cont%ds", i+1);
            qpac_mcdu2_content_special[i] = XPLMFindDataRef(buf);

        }

        // MCDU1
        qpac_mcdu1_scratch_yellow = XPLMFindDataRef("AirbusFBW/MCDU1spy");
        qpac_mcdu1_scratch_amber = XPLMFindDataRef("AirbusFBW/MCDU1spa");
        qpac_mcdu1_scratch_white = XPLMFindDataRef("AirbusFBW/MCDU1spw");
        qpac_mcdu1_title_yellow = XPLMFindDataRef("AirbusFBW/MCDU1titley");
        qpac_mcdu1_title_blue = XPLMFindDataRef("AirbusFBW/MCDU1titleb");
        qpac_mcdu1_title_green = XPLMFindDataRef("AirbusFBW/MCDU1titleg");
        qpac_mcdu1_title_white = XPLMFindDataRef("AirbusFBW/MCDU1titlew");

        // MCDU2
        qpac_mcdu2_scratch_yellow = XPLMFindDataRef("AirbusFBW/MCDU2spy");
        qpac_mcdu2_scratch_amber = XPLMFindDataRef("AirbusFBW/MCDU2spa");
        qpac_mcdu2_scratch_white = XPLMFindDataRef("AirbusFBW/MCDU2spw");
        qpac_mcdu2_title_yellow = XPLMFindDataRef("AirbusFBW/MCDU2titley");
        qpac_mcdu2_title_blue = XPLMFindDataRef("AirbusFBW/MCDU2titleb");
        qpac_mcdu2_title_green = XPLMFindDataRef("AirbusFBW/MCDU2titleg");
        qpac_mcdu2_title_white = XPLMFindDataRef("AirbusFBW/MCDU2titlew");
    } else {
    	qpac_mcdu_ready = 0;
    }
    // Initialize previous MsgPacket to force sending the first one
    qpacMcdu1PreviousMsgPacket.nb_of_lines=0;
    qpacMcdu2PreviousMsgPacket.nb_of_lines=0;
    qpacEwdPreviousMsgPacket.nb_of_lines=0;
}

/*
 * LINE COMPRESSION PROTOCOL FOR QPAC MESSAGES
 *
 * Compressed output format:
 * -------------------------
 * fcpptext1;fcpptext2;fcpptext3; etc etc
 * f : (1 char) font s=small, l=large
 * c : (1 char) color r=red, b=blue, m=magenta, y=yellow, g=green, a=amber, w=white
 * pp : (2 char) column position of embedded string
 * textx : string to be displayed
 *
 * The asterix char (*) has been translated from 176(d) to 30(d).
 * The box char [] has been translated to 31 (d)
 *
 *
 * E/WD messages, max 3 messages
 * MCDU messages, max 4 messages
 * NOTE: bufferlength : for plaintext format always max 37 bytes
 * For compressed format max length can be calculated with (7+textlength)*nr_of_messages
 * For 4 messages to be displayed the buflength has to be at least 63 bytes
 * Save values is thus 80 bytes
 */


/*
 * A320 E/WD Engine Warning Display
 * Upper ECAM Messages + Memo
 */

int createQpacEwdPacket(void) {

   int i,j;
   int datalen = 0;
   int p;
   char color = 'u';
   int red_len, blue_len, amber_len, green_len, white_len;
   int space = 0;

   char red_buffer[EWD_BUF_LEN];
   char blue_buffer[EWD_BUF_LEN];
   char amber_buffer[EWD_BUF_LEN];
   char green_buffer[EWD_BUF_LEN];
   char white_buffer[EWD_BUF_LEN];
   char encoded_string[EWD_BUF_LEN];

   strncpy(qpacEwdMsgPacket.packet_id, "QPAE", 4);
   qpacEwdMsgPacket.nb_of_lines = custom_htoni(QPAC_EWD_LINES);

   for(i=0; i<QPAC_EWD_LINES; i++){
     qpacEwdMsgPacket.lines[i].lineno = custom_htoni(i);
     datalen = XPLMGetDatab(qpac_ewd_red[i],red_buffer,0,sizeof(red_buffer));
     red_len = (datalen > 0) ? (int)strlen(red_buffer) : 0;

     datalen = XPLMGetDatab(qpac_ewd_blue[i],blue_buffer,0,sizeof(blue_buffer));
     blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;

     datalen = XPLMGetDatab(qpac_ewd_amber[i],amber_buffer,0,sizeof(amber_buffer));
     amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;

     datalen = XPLMGetDatab(qpac_ewd_green[i],green_buffer,0,sizeof(green_buffer));
     green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;

     datalen = XPLMGetDatab(qpac_ewd_white[i],white_buffer,0,sizeof(white_buffer));
     white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;

     color = 'u';
     encoded_string[0] = 0;
     space=0;
     for (j=0,p=0; (j<45) && (p<(EWD_BUF_LEN-9)); j++) {
    	 if ((j < green_len) && (green_buffer[j] > 32)) {
    		 if (color != 'g') {
    			 color = 'g';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= green_buffer[j];

    	 } else if ((j < blue_len) && (blue_buffer[j] > 32)) {
    		 if (color != 'b') {
    			 color = 'b';
    			 space=0;
    			 if (p>0) { encoded_string[p] = ';'; p++; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'b';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p] = blue_buffer[j];
    		 p++;
    	 } else if ((j < amber_len) && (amber_buffer[j] > 32)) {
    		 if (color != 'a') {
    			 color = 'a';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= amber_buffer[j];
    	 } else if ((j < red_len) && (red_buffer[j] > 32)) {
    		 if (color != 'r') {
    			 color = 'r';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= red_buffer[j];
    	 /* } else if ((j < white_len) && (white_buffer[j] > 32)) {
    		 if (color != 'w') {
    			 color = 'w';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= white_buffer[j];
		*/
    	 } else if (((j < blue_len) || (j < green_len)|| (j < amber_len)|| (j < red_len)|| (j < white_len)) && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    	 	 // color = 'u';
    		 break;
    	 }

     }
     encoded_string[p] = 0;
     strcpy(qpacEwdMsgPacket.lines[i].linestr,encoded_string);
     qpacEwdMsgPacket.lines[i].len = custom_htoni(p);


     // strcpy(qpacEwdMsgPacket.lines[i].linestr,green_buffer);
     // qpacEwdMsgPacket.lines[i].len = custom_htoni((int)strlen(qpacEwdMsgPacket.lines[i].linestr));
   }

  return 4 + 4 + QPAC_EWD_LINES * 88;

}

/*
 * Qpac A320 MCDU
 *
 * Decoding special text
 * 0 = blue left arrow
 * 1 = blue right arrow
 * 2 = white left arrow
 * 3 = white right arrow
 * 4 = amber left arrow
 * 5 = amber right arrow
 * 6 = green left arrow
 * 7 = green right arrow
 * 8 = yellow left arrow
 * 9 = yellow right arrow
 * A = blue left bracket
 * B = blue right bracket
 * C = white up arrow
 * D = white down arrow
 * E = amber box
 */

// char *sc_table   = "éèéèéèéèéè1234567[]§µ£";
char sc_table[] = { 30, 31, 30, 31, 30, 31, 30, 31, 30, 31, 32, 32, 32, 32, 32, 32, 32, '[', ']', 26, 27, 28, 0 };
char *sc_color_s = "bbwwaaggyyaaaaaaabbwwa";
char *sc_color   = "BBWWAAGGYYBBAAAAAAAWWA";

char special_char(char c) {
	if (c>='0' && c <= 'F') return sc_table[c-'0']; else return ' ';
}

char special_color_s(char c) {
	if (c>='0' && c <= 'F') return sc_color_s[c-'0']; else return ' ';
}

char special_color(char c) {
	if (c>='0' && c <= 'F') return sc_color[c-'0']; else return 'a';
}


/**
 * mcdu_id should be 0 for left, 1 for right. Any value different from 0 is treated as right
 */
int createQpacMcduPacket(int mcdu_id) {

   int i,l;
   int j;
   int datalen = 0;
   int p;
   char color = 'u';
   int blue_len, amber_len, green_len, white_len, yellow_len, magenta_len, special_len;
   int s_blue_len, s_amber_len, s_green_len, s_white_len, s_yellow_len, s_magenta_len,s_special_len;
   int space = 0;
   char yellow_buffer[MCDU_BUF_LEN];
   char white_buffer[MCDU_BUF_LEN];
   char blue_buffer[MCDU_BUF_LEN];
   char magenta_buffer[MCDU_BUF_LEN];
   char green_buffer[MCDU_BUF_LEN];
   char amber_buffer[MCDU_BUF_LEN];
   char special_buffer[MCDU_BUF_LEN];
   char s_yellow_buffer[MCDU_BUF_LEN];
   char s_white_buffer[MCDU_BUF_LEN];
   char s_blue_buffer[MCDU_BUF_LEN];
   char s_magenta_buffer[MCDU_BUF_LEN];
   char s_green_buffer[MCDU_BUF_LEN];
   char s_amber_buffer[MCDU_BUF_LEN];
   char s_special_buffer[MCDU_BUF_LEN];
   char encoded_string[EWD_BUF_LEN];

   strncpy(qpacMcduMsgPacket.packet_id, "QPAM", 4);
   qpacMcduMsgPacket.nb_of_lines = custom_htoni(QPAC_MCDU_LINES);
   qpacMcduMsgPacket.side = custom_htoni(mcdu_id);

   l=0;
   // Page title
   if (mcdu_id) {
	   datalen = XPLMGetDatab(qpac_mcdu2_title_yellow,yellow_buffer,0,sizeof(yellow_buffer));
	   yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
	   datalen = XPLMGetDatab(qpac_mcdu2_title_white,white_buffer,0,sizeof(white_buffer));
	   white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
	   datalen = XPLMGetDatab(qpac_mcdu2_title_blue,blue_buffer,0,sizeof(blue_buffer));
	   blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
	   datalen = XPLMGetDatab(qpac_mcdu2_title_green,green_buffer,0,sizeof(green_buffer));
	   green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;
   } else {
	   datalen = XPLMGetDatab(qpac_mcdu1_title_yellow,yellow_buffer,0,sizeof(yellow_buffer));
	   yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
	   datalen = XPLMGetDatab(qpac_mcdu1_title_white,white_buffer,0,sizeof(white_buffer));
	   white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
	   datalen = XPLMGetDatab(qpac_mcdu1_title_blue,blue_buffer,0,sizeof(blue_buffer));
	   blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
	   datalen = XPLMGetDatab(qpac_mcdu1_title_green,green_buffer,0,sizeof(green_buffer));
	   green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;
   }

   encoded_string[0] = 0;
   p=0;
   if (green_len>0)  {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'g';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], green_buffer);
   } else if (yellow_len>0) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'y';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], yellow_buffer);
   } else if (white_len>0) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'w';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], white_buffer);
   } else if (blue_len>0) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'g';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], blue_buffer);
   }

   strcpy(qpacMcduMsgPacket.lines[l].linestr,encoded_string);
   qpacMcduMsgPacket.lines[l].len = custom_htoni((int)strlen(qpacMcduMsgPacket.lines[l].linestr));
   qpacMcduMsgPacket.lines[l].lineno = custom_htoni(l);
   l++;
   /*
   strcpy(qpacMcduMsgPacket.lines[l].linestr, white_buffer);
   qpacMcduMsgPacket.lines[l].len = custom_htoni((int)strlen(qpacMcduMsgPacket.lines[l].linestr));
   qpacMcduMsgPacket.lines[l].lineno = custom_htoni(l);
   l++;
   */

   for(i=0; i<6; i++){
	   if (mcdu_id) {
		     datalen = XPLMGetDatab(qpac_mcdu2_label_yellow[i],yellow_buffer,0,sizeof(yellow_buffer));
		     yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu2_label_white[i],white_buffer,0,sizeof(white_buffer));
		     white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu2_label_blue[i],blue_buffer,0,sizeof(blue_buffer));
		     blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu2_label_magenta[i],magenta_buffer,0,sizeof(magenta_buffer));
		     magenta_len = (datalen > 0) ? (int)strlen(magenta_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu2_label_green[i],green_buffer,0,sizeof(green_buffer));
		     green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu2_label_amber[i],amber_buffer,0,sizeof(amber_buffer));
		     amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu2_label_special[i],special_buffer,0,sizeof(special_buffer));
		     special_len = (datalen > 0) ? (int)strlen(special_buffer) : 0;
	   } else {
		     datalen = XPLMGetDatab(qpac_mcdu1_label_yellow[i],yellow_buffer,0,sizeof(yellow_buffer));
		     yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu1_label_white[i],white_buffer,0,sizeof(white_buffer));
		     white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu1_label_blue[i],blue_buffer,0,sizeof(blue_buffer));
		     blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu1_label_magenta[i],magenta_buffer,0,sizeof(magenta_buffer));
		     magenta_len = (datalen > 0) ? (int)strlen(magenta_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu1_label_green[i],green_buffer,0,sizeof(green_buffer));
		     green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu1_label_amber[i],amber_buffer,0,sizeof(amber_buffer));
		     amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;
		     datalen = XPLMGetDatab(qpac_mcdu1_label_special[i],special_buffer,0,sizeof(special_buffer));
		     special_len = (datalen > 0) ? (int)strlen(special_buffer) : 0;
	   }



     color = 'u';
     encoded_string[0] = 0;
     space=0;
     for (j=0,p=0; (j<45) && (p<(EWD_BUF_LEN-9)); j++) {
    	 if ((j < green_len) && (green_buffer[j] > 32)) {
    		 if (color != 'g') {
    			 color = 'g';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= green_buffer[j];
    	 } else if ((j < blue_len) && (blue_buffer[j] > 32)) {
    		 if (color != 'b') {
    			 color = 'b';
    			 space=0;
    			 if (p>0) { encoded_string[p] = ';'; p++; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'b';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p] = blue_buffer[j];
    		 p++;
    	 } else if ((j < amber_len) && (amber_buffer[j] > 32)) {
    		 if (color != 'a') {
    			 color = 'a';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= amber_buffer[j];
    	 } else if ((j < yellow_len) && (yellow_buffer[j] > 32)) {
    		 if (color != 'y') {
    			 color = 'y';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= yellow_buffer[j];
    	 } else if ((j < white_len) && (white_buffer[j] > 32)) {
    		 if (color != 'w') {
    			 color = 'w';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= white_buffer[j];
    	 } else if (((j < blue_len) || (j < green_len)|| (j < amber_len)|| (j < yellow_len)|| (j < white_len)) && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    	 	 // color = 'u';
    		 break;
    	 }

     }
     encoded_string[p] = 0;
     strcpy(qpacMcduMsgPacket.lines[l].linestr,encoded_string);
     qpacMcduMsgPacket.lines[l].len = custom_htoni(p);
     qpacMcduMsgPacket.lines[l].lineno = custom_htoni(l);
     l++;

     // Debug : send "mcdu test" text on each line
     // strcpy(qpacMcduMsgPacket.lines[i].linestr, "s,r,00,mcdu test");
     /*
     strcpy(qpacMcduMsgPacket.lines[l].linestr, white_buffer);
     qpacMcduMsgPacket.lines[l].len = custom_htoni((int)strlen(qpacMcduMsgPacket.lines[l].linestr));
     l++;
     */

     if (mcdu_id) {
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_yellow[i],yellow_buffer,0,sizeof(yellow_buffer));
    	 yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_white[i],white_buffer,0,sizeof(white_buffer));
    	 white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_blue[i],blue_buffer,0,sizeof(blue_buffer));
    	 blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_magenta[i],magenta_buffer,0,sizeof(magenta_buffer));
    	 magenta_len = (datalen > 0) ? (int)strlen(magenta_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_green[i],green_buffer,0,sizeof(green_buffer));
    	 green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_amber[i],amber_buffer,0,sizeof(amber_buffer));
    	 amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_content_special[i],special_buffer,0,sizeof(special_buffer));
    	 special_len = (datalen > 0) ? (int)strlen(special_buffer) : 0;

    	 datalen = XPLMGetDatab(qpac_mcdu2_small_yellow[i],s_yellow_buffer,0,sizeof(s_yellow_buffer));
    	 s_yellow_len = (datalen > 0) ? (int)strlen(s_yellow_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_small_white[i],s_white_buffer,0,sizeof(s_white_buffer));
    	 s_white_len = (datalen > 0) ? (int)strlen(s_white_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_small_blue[i],s_blue_buffer,0,sizeof(s_blue_buffer));
    	 s_blue_len = (datalen > 0) ? (int)strlen(s_blue_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_small_magenta[i],s_magenta_buffer,0,sizeof(s_magenta_buffer));
    	 s_magenta_len = (datalen > 0) ? (int)strlen(s_magenta_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_small_green[i],s_green_buffer,0,sizeof(s_green_buffer));
    	 s_green_len = (datalen > 0) ? (int)strlen(s_green_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_small_amber[i],s_amber_buffer,0,sizeof(s_amber_buffer));
    	 s_amber_len = (datalen > 0) ? (int)strlen(s_amber_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu2_small_special[i],s_special_buffer,0,sizeof(s_special_buffer));
    	 s_special_len = (datalen > 0) ? (int)strlen(s_special_buffer) : 0;
     } else {
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_yellow[i],yellow_buffer,0,sizeof(yellow_buffer));
    	 yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_white[i],white_buffer,0,sizeof(white_buffer));
    	 white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_blue[i],blue_buffer,0,sizeof(blue_buffer));
    	 blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_magenta[i],magenta_buffer,0,sizeof(magenta_buffer));
    	 magenta_len = (datalen > 0) ? (int)strlen(magenta_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_green[i],green_buffer,0,sizeof(green_buffer));
    	 green_len = (datalen > 0) ? (int)strlen(green_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_amber[i],amber_buffer,0,sizeof(amber_buffer));
    	 amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_content_special[i],special_buffer,0,sizeof(special_buffer));
    	 special_len = (datalen > 0) ? (int)strlen(special_buffer) : 0;

    	 datalen = XPLMGetDatab(qpac_mcdu1_small_yellow[i],s_yellow_buffer,0,sizeof(s_yellow_buffer));
    	 s_yellow_len = (datalen > 0) ? (int)strlen(s_yellow_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_small_white[i],s_white_buffer,0,sizeof(s_white_buffer));
    	 s_white_len = (datalen > 0) ? (int)strlen(s_white_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_small_blue[i],s_blue_buffer,0,sizeof(s_blue_buffer));
    	 s_blue_len = (datalen > 0) ? (int)strlen(s_blue_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_small_magenta[i],s_magenta_buffer,0,sizeof(s_magenta_buffer));
    	 s_magenta_len = (datalen > 0) ? (int)strlen(s_magenta_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_small_green[i],s_green_buffer,0,sizeof(s_green_buffer));
    	 s_green_len = (datalen > 0) ? (int)strlen(s_green_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_small_amber[i],s_amber_buffer,0,sizeof(s_amber_buffer));
    	 s_amber_len = (datalen > 0) ? (int)strlen(s_amber_buffer) : 0;
    	 datalen = XPLMGetDatab(qpac_mcdu1_small_special[i],s_special_buffer,0,sizeof(s_special_buffer));
    	 s_special_len = (datalen > 0) ? (int)strlen(s_special_buffer) : 0;
     }


     color = 'u';
     encoded_string[0] = 0;
     space=0;
     for (j=0,p=0; (j<45) && (p<(EWD_BUF_LEN-9)); j++) {
    	 if ((j < green_len) && (green_buffer[j] != ' ')) {
    		 if (color != 'G') {
    			 color = 'G';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'g';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (green_buffer[j] < ' ') green_buffer[j] = '?';
    		 encoded_string[p++]= green_buffer[j];
    	 } else if ((j < blue_len) && (blue_buffer[j] != ' ')) {
    		 if (color != 'B') {
    			 color = 'B';
    			 space=0;
    			 if (p>0) { encoded_string[p] = ';'; p++; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'b';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (blue_buffer[j] < ' ') blue_buffer[j] = '?';
    		 encoded_string[p] = blue_buffer[j];
    		 p++;
    	 } else if ((j < amber_len) && (!(amber_buffer[j] == ' '))) {
    		 if (color != 'A') {
    			 color = 'A';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'a';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (amber_buffer[j] < ' ') amber_buffer[j] = '?';
    		 encoded_string[p++]= amber_buffer[j];
    	 } else if ((j < yellow_len) && (yellow_buffer[j] != ' ')) {
    		 if (color != 'Y') {
    			 color = 'Y';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'y';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (amber_buffer[j] < ' ') amber_buffer[j] = '?';
    		 encoded_string[p++]= yellow_buffer[j];
    	 } else if ((j < white_len) && (white_buffer[j] != ' ')) {
    		 if (color != 'W') {
    			 color = 'W';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'w';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (white_buffer[j] < ' ') white_buffer[j] = '?';
    		 encoded_string[p++]= white_buffer[j];
    	 } else if ((j < magenta_len) && (magenta_buffer[j] != ' ')) {
    		 if (color != 'M') {
    			 color = 'M';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'm';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (magenta_buffer[j] < ' ') magenta_buffer[j] = '?';
    		 encoded_string[p++]= magenta_buffer[j];
    	 } else if ((j < special_len) && (special_buffer[j] != ' ')) {
    		 if (color != special_color(special_buffer[j])) {
    			 color = special_color(special_buffer[j]);
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = special_color_s(special_buffer[j]);
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= special_char(special_buffer[j]);
    	 } else if ((j < s_blue_len) && (s_blue_buffer[j] != ' ')) {
    		 if (color != 'b') {
    			 color = 'b';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'b';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (s_blue_buffer[j] < ' ') s_blue_buffer[j] = '?';
    		 encoded_string[p++]= s_blue_buffer[j];
    	 } else if ((j < s_white_len) && (s_white_buffer[j] != ' ')) {
    		 if (color != 'w') {
    			 color = 'w';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'w';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (s_white_buffer[j] < ' ') s_white_buffer[j] = '?';
    		 encoded_string[p++]= s_white_buffer[j];
    	 } else if ((j < s_green_len) && (s_green_buffer[j] != ' ')) {
    		 if (color != 'g') {
    			 color = 'g';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'g';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (s_green_buffer[j] < ' ') s_green_buffer[j] = '?';
    		 encoded_string[p++]= s_green_buffer[j];
    	 } else if ((j < s_amber_len) && (s_amber_buffer[j] != ' ')) {
    		 if (color != 'a') {
    			 color = 'a';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'a';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (s_amber_buffer[j] < ' ') s_amber_buffer[j] = '?';
    		 encoded_string[p++]= (s_amber_buffer[j] < 32) ? s_amber_buffer[j] + 32 : s_amber_buffer[j];
    	 } else if ((j < s_yellow_len) && (s_yellow_buffer[j] != ' ')) {
    		 if (color != 'y') {
    			 color = 'y';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'y';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (s_yellow_buffer[j] < ' ') s_yellow_buffer[j] = '?';
    		 encoded_string[p++]= s_yellow_buffer[j];
    	 } else if ((j < s_magenta_len) && (s_magenta_buffer[j] != ' ')) {
    		 if (color != 'm') {
    			 color = 'm';
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'm';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (s_magenta_buffer[j] < ' ') s_magenta_buffer[j] = '?';
    		 encoded_string[p++]= s_magenta_buffer[j];
    	 } else if ((j < s_special_len) && (s_special_buffer[j] != ' ')) {
    		 if (color != special_color_s(s_special_buffer[j])) {
    			 color = special_color_s(s_special_buffer[j]);
				 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = special_color_s(s_special_buffer[j]);
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= special_char(s_special_buffer[j]);


    	 } else if (((j < blue_len) || (j < green_len)|| (j < amber_len)|| (j < yellow_len)|| (j < white_len) || (j<magenta_len) || (j<special_len)
    			  || (j < s_blue_len) || (j < s_amber_len) || (j < s_yellow_len) || (j < s_green_len) || (j < s_white_len) || (j < s_magenta_len) || (j < s_special_len))
    			  && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    	 	 // color = 'u';
    		 break;
    	 }

     }
     encoded_string[p] = 0;
     strcpy(qpacMcduMsgPacket.lines[l].linestr,encoded_string);
     qpacMcduMsgPacket.lines[l].len = custom_htoni(p);
     qpacMcduMsgPacket.lines[l].lineno = custom_htoni(l);
     l++;

     // Debug : send "mcdu test" text on each line
     /*
     qpacMcduMsgPacket.lines[l].lineno = custom_htoni(l);
     strcpy(qpacMcduMsgPacket.lines[l].linestr, "sr00mcdu data line");
     qpacMcduMsgPacket.lines[l].len = custom_htoni((int)strlen(qpacMcduMsgPacket.lines[l].linestr));
     */

   }

   // Scratch pad line
   qpacMcduMsgPacket.lines[l].lineno = custom_htoni(l);
   if (mcdu_id) {
	   XPLMGetDatab(qpac_mcdu2_scratch_yellow,yellow_buffer,0,sizeof(yellow_buffer));
	   yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
	   XPLMGetDatab(qpac_mcdu2_scratch_white,white_buffer,0,sizeof(white_buffer));
	   white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
	   XPLMGetDatab(qpac_mcdu2_scratch_amber,amber_buffer,0,sizeof(amber_buffer));
	   amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;
   } else {
	   XPLMGetDatab(qpac_mcdu1_scratch_yellow,yellow_buffer,0,sizeof(yellow_buffer));
	   yellow_len = (datalen > 0) ? (int)strlen(yellow_buffer) : 0;
	   XPLMGetDatab(qpac_mcdu1_scratch_white,white_buffer,0,sizeof(white_buffer));
	   white_len = (datalen > 0) ? (int)strlen(white_buffer) : 0;
	   XPLMGetDatab(qpac_mcdu1_scratch_amber,amber_buffer,0,sizeof(amber_buffer));
	   amber_len = (datalen > 0) ? (int)strlen(amber_buffer) : 0;
   }

   color = 'u';
   encoded_string[0] = 0;
   p=0;
   if ((white_len>0) && (white_buffer[0] > 32)) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'w';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], white_buffer);
   } else if ((yellow_len>0) && (yellow_buffer[0] > 32)) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'y';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], yellow_buffer);
   } else if ((amber_len>0) && (amber_buffer[0] > 32)) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'a';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], amber_buffer);
   }
   strcpy(qpacMcduMsgPacket.lines[l].linestr, encoded_string);
   qpacMcduMsgPacket.lines[l].len = custom_htoni((int)strlen(qpacMcduMsgPacket.lines[l].linestr));

   /*
   datalen = XPLMGetDatab(qpac_mcdu1_small_amber[0],blue_buffer,0,sizeof(blue_buffer));
   blue_len = (datalen > 0) ? (int)strlen(blue_buffer) : 0;
   strcpy(qpacMcduMsgPacket.lines[l].linestr, blue_buffer);
   qpacMcduMsgPacket.lines[l].len = custom_htoni(blue_len);
   */

   return 4 + 4 + QPAC_MCDU_LINES * 88;

}


float sendQpacMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int mcdu_packet_size;
	int ewd_packet_size;


	// TODO: Store previous packet / Send if different
	// TODO: Force sending MCDU packets every 2 seconds (application startup time)
    // TODO: adjust packet delay. Set to adc * 3.0
	qpac_msg_delay = adc_data_delay * 3.0f;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && qpac_ewd_ready)  {

		if (qpac_mcdu_ready) {
			mcdu_packet_size = createQpacMcduPacket(XPLMGetDatai(cdu_pilot_side));
	        if ( mcdu_packet_size > 0 ) {
	            for (i=0; i<NUM_DEST; i++) {
	                if (dest_enable[i]) {
	                	if (sendto(sockfd, (const char*)&qpacMcduMsgPacket, mcdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
	                		XPLMDebugString("XHSI: caught error while sending QpacMcduMsg packet! (");
	                		XPLMDebugString((char * const) strerror(GET_ERRNO));
	                		XPLMDebugString(")\n");
	                	}
	                }
	            }
	            if (XPLMGetDatai(cdu_pilot_side)==0)
	            	qpacMcdu1PreviousMsgPacket = qpacMcduMsgPacket;
	            else
	            	qpacMcdu2PreviousMsgPacket = qpacMcduMsgPacket;
	        }
	        if (XPLMGetDatai(cdu_pilot_side) != XPLMGetDatai(cdu_copilot_side)) {
				mcdu_packet_size = createQpacMcduPacket(XPLMGetDatai(cdu_copilot_side));
		        if ( mcdu_packet_size > 0 ) {
		            for (i=0; i<NUM_DEST; i++) {
		                if (dest_enable[i]) {
		                	if (sendto(sockfd, (const char*)&qpacMcduMsgPacket, mcdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
		                		XPLMDebugString("XHSI: caught error while sending QpacMcduMsg packet! (");
		                		XPLMDebugString((char * const) strerror(GET_ERRNO));
		                		XPLMDebugString(")\n");
		                	}
		                }
		            }
		        }
	            if (XPLMGetDatai(cdu_copilot_side)==0)
	            	qpacMcdu1PreviousMsgPacket = qpacMcduMsgPacket;
	            else
	            	qpacMcdu2PreviousMsgPacket = qpacMcduMsgPacket;
	        }
		}

		ewd_packet_size = createQpacEwdPacket();

        if ( ewd_packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&qpacEwdMsgPacket, ewd_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending QpacEwdMsg packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }

                }
            }
            qpacEwdPreviousMsgPacket = qpacEwdMsgPacket;
            return qpac_msg_delay;
        } else {
            return qpac_msg_delay;
        }

	} else {
		return 10.0f;
	}

}
