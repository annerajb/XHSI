/*
 * jar_a320_msg.c
 *
 *  Created on: 17 july 2015
 *      Author: Nicolas Carel
 *
 *  Send jar_a320 MCDU and ECAM messages packets
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

#include "datarefs_jar_a320neo.h"
#include "jar_a320neo_msg.h"

#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"

// ECAM
XPLMDataRef jar_a320_ewd_magenta[JAR_A320_EWD_LINES];
XPLMDataRef jar_a320_ewd_blue[JAR_A320_EWD_LINES];
XPLMDataRef jar_a320_ewd_amber[JAR_A320_EWD_LINES];
XPLMDataRef jar_a320_ewd_green[JAR_A320_EWD_LINES];
XPLMDataRef jar_a320_ewd_white[JAR_A320_EWD_LINES];
XPLMDataRef jar_a320_ewd_r_blue[JAR_A320_EWD_LINES];
XPLMDataRef jar_a320_ewd_r_green[JAR_A320_EWD_LINES];

// MCDU
XPLMDataRef jar_a320_mcdu_title_green;
XPLMDataRef jar_a320_mcdu_title_white;
XPLMDataRef jar_a320_mcdu_label[JAR_A320_MCDU_LINES];


// XPLMDataRef jar_a320_mcdu1_content_yellow[JAR_A320_MCDU_LINES];
XPLMDataRef jar_a320_mcdu_content_blue[JAR_A320_MCDU_LINES];
XPLMDataRef jar_a320_mcdu_content_amber[JAR_A320_MCDU_LINES];
XPLMDataRef jar_a320_mcdu_content_green[JAR_A320_MCDU_LINES];
XPLMDataRef jar_a320_mcdu_content_white[JAR_A320_MCDU_LINES];
XPLMDataRef jar_a320_mcdu_content_magenta[JAR_A320_MCDU_LINES];

XPLMDataRef jar_a320_mcdu_scratch_yellow;
XPLMDataRef jar_a320_mcdu_scratch_white;

XPLMPluginID jar_a320_PluginId = XPLM_NO_PLUGIN_ID;
int jar_a320_mcdu_ready = 0;
int jar_a320_ewd_ready = 0;

struct jar_a320EwdMsgLinesDataPacket jar_a320EwdMsgPacket;
struct jar_a320McduMsgLinesDataPacket jar_a320McduMsgPacket;

float jar_a320_msg_delay;

void findJar_a320MsgDataRefs(void) {
    int         i;
    char        buf[100];

    jar_a320_PluginId = XPLMFindPluginBySignature("1-sim.sasl");  // jar_a320 plugin signature
    if(jar_a320_PluginId != XPLM_NO_PLUGIN_ID) {
        sprintf(buf, "XHSI: jar_a320 plugin found - loading EWD datarefs\n");
        XPLMDebugString(buf);
        jar_a320_ewd_ready = 1;

        for (i=0; i<JAR_A320_EWD_LINES; i++) {
            sprintf(buf, "sim/custom/xap/ewd/line_%d_l_m", i+1);
            jar_a320_ewd_magenta[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/ewd/line_%d_l_g", i+1);
            jar_a320_ewd_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/ewd/line_%d_l_b", i+1);
            jar_a320_ewd_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/ewd/line_%d_l_w", i+1);
            jar_a320_ewd_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/ewd/line_%d_l_a", i+1);
            jar_a320_ewd_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/ewd/line_%d_r_b", i+1);
            jar_a320_ewd_r_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/ewd/line_%d_r_g", i+1);
            jar_a320_ewd_r_green[i] = XPLMFindDataRef(buf);
        }
    } else {
    	jar_a320_ewd_ready = 0;
    }

    if (jar_a320_PluginId != XPLM_NO_PLUGIN_ID) {
    	sprintf(buf, "XHSI: jar_a320 v2 plugin found - loading MCDU datarefs\n");
    	XPLMDebugString(buf);
    	jar_a320_mcdu_ready = 1;

        for (i=0; i<JAR_A320_MCDU_LINES-1; i++) {
        	sprintf(buf, "sim/custom/xap/mcdu/label_%d", i+1);
            jar_a320_mcdu_label[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/mcdu/line_%db", i+1);
            jar_a320_mcdu_content_blue[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/mcdu/line_%da", i+1);
            jar_a320_mcdu_content_amber[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/mcdu/line_%dg", i+1);
            jar_a320_mcdu_content_green[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/mcdu/line_%dw", i+1);
            jar_a320_mcdu_content_white[i] = XPLMFindDataRef(buf);
            sprintf(buf, "sim/custom/xap/mcdu/line_%dm", i+1);
            jar_a320_mcdu_content_magenta[i] = XPLMFindDataRef(buf);
        }

        jar_a320_mcdu_scratch_yellow = XPLMFindDataRef("sim/custom/xap/mcdu/scratchpad_a");
        jar_a320_mcdu_scratch_white = XPLMFindDataRef("sim/custom/xap/mcdu/scratchpad");
        jar_a320_mcdu_title_green = XPLMFindDataRef("sim/custom/xap/mcdu/title_g");
        jar_a320_mcdu_title_white = XPLMFindDataRef("sim/custom/xap/mcdu/title_w");

    } else {
    	jar_a320_mcdu_ready = 0;
    }
}

/*
 * LINE COMPRESSION PROTOCOL FOR jar_a320 MESSAGES
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

int createJar_a320EwdPacket(void) {

   int i,j,k;
   int datalen = 0;
   int p;
   char color = 'u';
   int magenta_len, blue_len, amber_len, green_len, white_len;
   int r_blue_len, r_green_len;

   int space = 0;

   char magenta_buffer[EWD_BUF_LEN];
   char blue_buffer[EWD_BUF_LEN];
   char amber_buffer[EWD_BUF_LEN];
   char green_buffer[EWD_BUF_LEN];
   char white_buffer[EWD_BUF_LEN];
   char r_blue_buffer[EWD_BUF_LEN];
   char r_green_buffer[EWD_BUF_LEN];
   char encoded_string[EWD_BUF_LEN];

   strncpy(jar_a320EwdMsgPacket.packet_id, "QPAE", 4);
   jar_a320EwdMsgPacket.nb_of_lines = custom_htoni(JAR_A320_EWD_LINES);

   for(i=0; i<JAR_A320_EWD_LINES; i++){
     jar_a320EwdMsgPacket.lines[i].lineno = custom_htoni(i);
     datalen = XPLMGetDatab(jar_a320_ewd_magenta[i],magenta_buffer,0,sizeof(magenta_buffer));
     magenta_len = (datalen > 0) ? strlen(magenta_buffer) : 0;

     datalen = XPLMGetDatab(jar_a320_ewd_blue[i],blue_buffer,0,sizeof(blue_buffer));
     blue_len = (datalen > 0) ? strlen(blue_buffer) : 0;

     datalen = XPLMGetDatab(jar_a320_ewd_amber[i],amber_buffer,0,sizeof(amber_buffer));
     amber_len = (datalen > 0) ? strlen(amber_buffer) : 0;

     datalen = XPLMGetDatab(jar_a320_ewd_green[i],green_buffer,0,sizeof(green_buffer));
     green_len = (datalen > 0) ? strlen(green_buffer) : 0;

     datalen = XPLMGetDatab(jar_a320_ewd_white[i],white_buffer,0,sizeof(white_buffer));
     white_len = (datalen > 0) ? strlen(white_buffer) : 0;

     datalen = XPLMGetDatab(jar_a320_ewd_r_blue[i],r_blue_buffer,0,sizeof(r_blue_buffer));
     r_blue_len = (datalen > 0) ? strlen(r_blue_buffer) : 0;

     datalen = XPLMGetDatab(jar_a320_ewd_r_green[i],r_green_buffer,0,sizeof(r_green_buffer));
     r_green_len = (datalen > 0) ? strlen(r_green_buffer) : 0;

     color = 'u';
     encoded_string[0] = 0;
     space=0;
     for (j=0,p=0; (j<26) && (p<(EWD_BUF_LEN-9)); j++) {
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
    	 } else if ((j < magenta_len) && (magenta_buffer[j] > 32)) {
    		 if (color != 'm') {
    			 color = 'm';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++]= magenta_buffer[j];
    	 } else if (((j < blue_len) || (j < green_len)|| (j < amber_len)|| (j < magenta_len)|| (j < white_len)) && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    	 	 // color = 'u';
    		 break;
    	 }
     }

     color = 'u';
     space=0;
     for (j=0,k=25; (j<16) && (p<(EWD_BUF_LEN-9)); j++,k++) {
    	 if ((j < r_green_len) && (r_green_buffer[j] > 32)) {
    		 if (color != 'g') {
    			 color = 'g';
    			 space=0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + k/10;
    			 encoded_string[p++] = '0' + k%10;
    		 }
    		 encoded_string[p++]= r_green_buffer[j];
    	 } else if ((j < r_blue_len) && (r_blue_buffer[j] > 32)) {
    		 if (color != 'b') {
    			 color = 'b';
    			 space=0;
    			 if (p>0) { encoded_string[p] = ';'; p++; }
    			 // encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'b';
    			 encoded_string[p++] = '0' + k/10;
    			 encoded_string[p++] = '0' + k%10;
    		 }
    		 encoded_string[p] = r_blue_buffer[j];
    		 p++;
    	 } else if (((j < r_blue_len) || (j < r_green_len)) && (space<2) ) {
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
     strcpy(jar_a320EwdMsgPacket.lines[i].linestr,encoded_string);
     jar_a320EwdMsgPacket.lines[i].len = custom_htoni(p);


     // strcpy(jar_a320EwdMsgPacket.lines[i].linestr,green_buffer);
     // jar_a320EwdMsgPacket.lines[i].len = custom_htoni((int)strlen(jar_a320EwdMsgPacket.lines[i].linestr));
   }

  return 4 + 4 + JAR_A320_EWD_LINES * 88;

}

/*
 * jar_a320 A320 MCDU
 */
int createJar_a320McduPacket(void) {

   int i,l;
   int j;
   int datalen = 0;
   int p;
   char color = 'u';
   int red_len, blue_len, amber_len, green_len, white_len, yellow_len, magenta_len;
   int space = 0;
   char yellow_buffer[MCDU_BUF_LEN];
   char white_buffer[MCDU_BUF_LEN];
   char blue_buffer[MCDU_BUF_LEN];
   char magenta_buffer[MCDU_BUF_LEN];
   char green_buffer[MCDU_BUF_LEN];
   char amber_buffer[MCDU_BUF_LEN];
   char encoded_string[EWD_BUF_LEN];

   strncpy(jar_a320McduMsgPacket.packet_id, "QPAM", 4);
   jar_a320McduMsgPacket.nb_of_lines = custom_htoni(JAR_A320_MCDU_LINES);

   l=0;
   // Page title
   datalen = XPLMGetDatab(jar_a320_mcdu_title_white,white_buffer,0,sizeof(white_buffer));
   white_len = (datalen > 0) ? strlen(white_buffer) : 0;
   datalen = XPLMGetDatab(jar_a320_mcdu_title_green,green_buffer,0,sizeof(green_buffer));
   green_len = (datalen > 0) ? strlen(green_buffer) : 0;

   encoded_string[0] = 0;
   p=0;
   if (green_len>0)  {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'g';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], green_buffer);
   } else if (white_len>0) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'w';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], white_buffer);
   }

   strcpy(jar_a320McduMsgPacket.lines[l].linestr,encoded_string);
   jar_a320McduMsgPacket.lines[l].len = custom_htoni((int)strlen(jar_a320McduMsgPacket.lines[l].linestr));
   jar_a320McduMsgPacket.lines[l].lineno = custom_htoni(l);
   l++;

   for(i=0; i<6; i++){

     datalen = XPLMGetDatab(jar_a320_mcdu_label[i],white_buffer,0,sizeof(white_buffer));
     white_len = (datalen > 0) ? strlen(white_buffer) : 0;
     color = 'u';
     encoded_string[0] = 0;
     space=0;
     for (j=0,p=0; (j<45) && (p<(EWD_BUF_LEN-9)); j++) {
    	 if ((j < white_len) && (white_buffer[j] > 32)) {
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
    	 } else if ( (j < white_len) && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    		 break;
    	 }
     }

     encoded_string[p] = 0;
     strcpy(jar_a320McduMsgPacket.lines[l].linestr,encoded_string);
     jar_a320McduMsgPacket.lines[l].len = custom_htoni(p);
     jar_a320McduMsgPacket.lines[l].lineno = custom_htoni(l);
     l++;

     datalen = XPLMGetDatab(jar_a320_mcdu_content_white[i],white_buffer,0,sizeof(white_buffer));
     white_len = (datalen > 0) ? strlen(white_buffer) : 0;
     datalen = XPLMGetDatab(jar_a320_mcdu_content_blue[i],blue_buffer,0,sizeof(blue_buffer));
     blue_len = (datalen > 0) ? strlen(blue_buffer) : 0;
     datalen = XPLMGetDatab(jar_a320_mcdu_content_magenta[i],magenta_buffer,0,sizeof(magenta_buffer));
     magenta_len = (datalen > 0) ? strlen(magenta_buffer) : 0;
     datalen = XPLMGetDatab(jar_a320_mcdu_content_green[i],green_buffer,0,sizeof(green_buffer));
     green_len = (datalen > 0) ? strlen(green_buffer) : 0;
     datalen = XPLMGetDatab(jar_a320_mcdu_content_amber[i],amber_buffer,0,sizeof(amber_buffer));
     amber_len = (datalen > 0) ? strlen(amber_buffer) : 0;

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
    	 } else if (((j < blue_len) || (j < green_len)|| (j < amber_len)|| (j < white_len) || (j<magenta_len)  )
    			  && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    		 break;
    	 }
     }
     encoded_string[p] = 0;
     strcpy(jar_a320McduMsgPacket.lines[l].linestr,encoded_string);
     jar_a320McduMsgPacket.lines[l].len = custom_htoni(p);
     jar_a320McduMsgPacket.lines[l].lineno = custom_htoni(l);
     l++;

   }

   // Scratch pad line
   jar_a320McduMsgPacket.lines[l].lineno = custom_htoni(l);
   XPLMGetDatab(jar_a320_mcdu_scratch_yellow,yellow_buffer,0,sizeof(yellow_buffer));
   yellow_len = (datalen > 0) ? strlen(yellow_buffer) : 0;
   XPLMGetDatab(jar_a320_mcdu_scratch_white,white_buffer,0,sizeof(white_buffer));
   white_len = (datalen > 0) ? strlen(white_buffer) : 0;
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
   }
   strcpy(jar_a320McduMsgPacket.lines[l].linestr, encoded_string);
   jar_a320McduMsgPacket.lines[l].len = custom_htoni((int)strlen(jar_a320McduMsgPacket.lines[l].linestr));

   return 4 + 4 + JAR_A320_MCDU_LINES * 88;

}

float sendJar_a320MsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int mcdu_packet_size;
	int ewd_packet_size;


	// TODO: Store previous packet / Send if different
    // TODO: adjust packet delay. Set to adc * 3.0
	jar_a320_msg_delay = adc_data_delay * 3.0f;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && jar_a320_ewd_ready)  {

		if (jar_a320_mcdu_ready) {
			mcdu_packet_size = createJar_a320McduPacket();
	        if ( mcdu_packet_size > 0 ) {
	            for (i=0; i<NUM_DEST; i++) {
	                if (dest_enable[i]) {
	                	if (sendto(sockfd, (const char*)&jar_a320McduMsgPacket, mcdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
	                		XPLMDebugString("XHSI: caught error while sending jar_a320McduMsg packet! (");
	                		XPLMDebugString((char * const) strerror(GET_ERRNO));
	                		XPLMDebugString(")\n");
	                	}
	                }
	            }

	        }
		}

		ewd_packet_size = createJar_a320EwdPacket();

        if ( ewd_packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&jar_a320EwdMsgPacket, ewd_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending jar_a320EwdMsg packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }

                }
            }
            return jar_a320_msg_delay;
        } else {
            return jar_a320_msg_delay;
        }

	} else {
		return 10.0f;
	}

}
