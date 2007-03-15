/**
* SimDataSender.c
* 
* This X-Plane plugin sends X-Plane simulator data to an XHSI
* application instance via UDP.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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

/*
 * Open questions
 * - X-Plane does not seem to remember the Enable/Disable setting for the plugin. Is this a bug?
 */
#define plugin_version_text "XHSI Plugin 1.0 Beta 6"
#define plugin_version_id 10006			

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

#if IBM

#include <windows.h>
#include <winsock2.h>

#else

#include <sys/socket.h>
#include <sys/errno.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#endif
// Sim value ids ===============================================================
// Aircraft position
#define SIM_FLIGHTMODEL_POSITION_GROUNDSPEED 0 
#define SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED 1 
#define SIM_FLIGHTMODEL_POSITION_MAGPSI 2 
#define SIM_FLIGHTMODEL_POSITION_HPATH 3 
#define SIM_FLIGHTMODEL_POSITION_LATITUDE 4 
#define SIM_FLIGHTMODEL_POSITION_LONGITUDE 5 
#define SIM_FLIGHTMODEL_POSITION_PHI 6 			// roll angle
#define SIM_FLIGHTMODEL_POSITION_R 7 			// rotation rate
#define SIM_FLIGHTMODEL_POSITION_MAGVAR 8
 
// Radios
#define SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ 100 
#define SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ 101 
#define SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ 102 
#define SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ 103 
#define SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT 104 
#define SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT 105 
#define SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT 106 
#define SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT 107 
#define SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M 108 
#define SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M 109 
#define SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M 110 
#define SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M 111 
#define SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM 112  
#define SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM 113 

#define SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM 114
#define SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM 115
#define SIM_COCKPIT_RADIOS_NAV1_CDI 116
#define SIM_COCKPIT_RADIOS_NAV2_CDI 117
#define SIM_COCKPIT_AUTOPILOT_STATE 150
#define SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY 151
#define SIM_COCKPIT_AUTOPILOT_ALTITUDE 152
#define SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR 153

// AP and EFIS
#define SIM_COCKPIT_AUTOPILOT_HEADING_MAG 200 
#define SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR 201 
#define SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR 202 
#define SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR 203 
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER 204 
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS 205 
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS 206 
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS 207 
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS 208 
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS 209 

// Environment
#define SIM_WEATHER_WIND_SPEED_KT 300 
#define SIM_WEATHER_WIND_DIRECTION_DEGT 301 
#define SIM_TIME_ZULU_TIME_SEC 302
#define SIM_TIME_LOCAL_TIME_SEC 303

// Plugin Version
#define PLUGIN_VERSION_ID 400

// Menu items
#define MENU_ITEM_PREFERENCES 0
#define MENU_ITEM_ENABLED 1

XPLMDataRef groundspeed;
XPLMDataRef true_airspeed;
XPLMDataRef magpsi;
XPLMDataRef hpath;
XPLMDataRef latitude;
XPLMDataRef longitude;
XPLMDataRef phi;
XPLMDataRef r;
XPLMDataRef magvar;
XPLMDataRef nav1_freq_hz;
XPLMDataRef nav2_freq_hz;
XPLMDataRef adf1_freq_hz;
XPLMDataRef adf2_freq_hz;
XPLMDataRef nav1_dir_degt;
XPLMDataRef nav2_dir_degt;
XPLMDataRef adf1_dir_degt;
XPLMDataRef adf2_dir_degt;
XPLMDataRef nav1_dme_dist_m;
XPLMDataRef nav2_dme_dist_m;
XPLMDataRef adf1_dme_dist_m;
XPLMDataRef adf2_dme_dist_m;
XPLMDataRef nav1_obs_degm;
XPLMDataRef nav2_obs_degm;

XPLMDataRef nav1_course_degm;
XPLMDataRef nav2_course_degm;
XPLMDataRef nav1_cdi;
XPLMDataRef nav2_cdi;
XPLMDataRef autopilot_state;
XPLMDataRef autopilot_vertical_velocity;
XPLMDataRef autopilot_altitude;
XPLMDataRef autopilot_approach_selector;
XPLMDataRef	autopilot_heading_mag;

XPLMDataRef	efis_map_range_selector;
XPLMDataRef	efis_dme_1_selector;
XPLMDataRef	efis_dme_2_selector;
XPLMDataRef	efis_shows_weather;
XPLMDataRef	efis_shows_tcas;
XPLMDataRef	efis_shows_airports;
XPLMDataRef	efis_shows_waypoints;
XPLMDataRef	efis_shows_vors;
XPLMDataRef	efis_shows_ndbs;
XPLMDataRef	wind_speed_kt;
XPLMDataRef	wind_direction_degt;
XPLMDataRef zulu_time_sec;
XPLMDataRef local_time_sec;

#if BYTE_ORDER == LITTLE_ENDIAN

float htonf(float x) {
	float r;
	
    unsigned char *s1 = (unsigned char *) &x;
    unsigned char *s2 = (unsigned char *) &r;
    
    s2[0] = s1[3];
    s2[1] = s1[2];
    s2[2] = s1[1];
    s2[3] = s1[0];

	return r;
}

#else
#define htonf(x) x;
#endif

// Packet data structures ============================================
struct hsi_sim_data_point {
	float	id;
	float	value;
};

struct hsi_sim_data_packet {
	char						packet_id[4];
	float						nb_of_data_points;
	struct hsi_sim_data_point	data_points[100];
};

struct xpln_fms_entry {
	float						type;			
	char						id[5];
	float						altitude;
	float						lat;
	float						lon;
};

struct xpln_fms_entries {
	char						packet_id[4];
	float						nb_of_entries;
	float						active_entry_index;
	float						destination_entry_index;
	struct xpln_fms_entry		entries[99];
};

// Socket and packet handling =======================================
char						*dest_ip;
unsigned short int		dest_port;
unsigned short int		src_port;
int							sockfd;
struct sockaddr_in			src_addr, dest_addr;
struct hsi_sim_data_point	data_point;
struct hsi_sim_data_packet	data_packet;
struct xpln_fms_entry		fms_entry;
struct xpln_fms_entries		fms_entries;

// Status and control =======================================
int							socket_open;
unsigned long int			sim_data_frames_per_second;
unsigned long int			fms_data_frames_per_second;
float						time_for_each_sim_data_frame;
float						time_for_each_fms_data_frame;
int							ret;

// HMI stuff =======================================

XPWidgetID	preferences_widget, preferences_sender_window_widget, preferences_receiver_window_widget;
XPWidgetID	ip_textbox_widget, receiver_port_textbox_widget, sender_port_textbox_widget;
XPWidgetID  data_rate_sim_textbox, data_rate_fms_textbox;
XPWidgetID	sender_label, receiver_label, ip_label, sender_port_label, receiver_port_label;
XPWidgetID  sim_data_rate_label, fms_data_rate_label;
XPWidgetID  cancel_button, set_button;
XPLMMenuID	menu_id;
int			menu_item;
int			widget_shows;
int			widget_created;
int			plugin_enabled;
int		    send_enabled;

// forward declarations =======================================

// socket stuff
void set_addresses();

#if IBM
int start_winsock();
#endif
int ensure_socket_bound();

// packet creation
void	findDataRefs(void);
void	createHSISimDataPacket(void);
void 	createHSIFMSDataPacket(void);
float	SendSimDataFlightLoopCallback(float, float, int, void *);    
float	SendFMSDataFlightLoopCallback(float, float, int, void *);    

// HMI
void createMenu();
void	ExternalHSIMenuHandler(void *, void *); 
void	CreateExternalHSIPreferencesDialog(int, int, int, int);
int		Preferences_Widget_Handler(XPWidgetMessage, XPWidgetID, long, long);

// X-Plane SDK API methods -----------------------------------

PLUGIN_API int XPluginStart(
						char *		outName,
						char *		outSig,
						char *		outDesc)
{
	char* debug_message;
	
	strcpy(outName, plugin_version_text);
	strcpy(outSig, "de.georg_gruetter.xhsi.plugin");
	strcpy(outDesc, "This plugin sends simulator data to an XHSI application instance via UDP");
	
	// Initialize status variables
	widget_shows = 0;
	widget_created = 0;
	plugin_enabled = 0;		// set to 1 if the plugin is enabled from the menu. 0 otherwise
	send_enabled = 1;		// set to 1 if socket is open and preferences window is not shown. 0 otherwise
	socket_open = 0;
	
	// Find the data refs we want to send 
	findDataRefs();

	// Default settings
	// Todo: store settings in preferences file
	dest_ip = "127.0.0.1";
	dest_port = 49001;
	src_port = 49005;
	sim_data_frames_per_second = 26;
	fms_data_frames_per_second = 1;
	time_for_each_sim_data_frame = 1.0/sim_data_frames_per_second;
	time_for_each_fms_data_frame = 1.0/fms_data_frames_per_second;
	
	// start winsock
	#if IBM
	if (start_winsock() == 0) {
		return 0;
	}
	#endif
		
	// create socket and addresses
	sockfd = socket(PF_INET, SOCK_DGRAM, 0);
	
#if IBM	
	if (sockfd == INVALID_SOCKET) {
		sprintf(debug_message, "XHSI: failed - Could not open socket! Errorcode: %d\n", WSAGetLastError());
		XPLMDebugString(debug_message);
		return 0;
	} else {
		XPLMDebugString("XHSI: socket created\n");
	}	
#endif
	
	set_addresses();
	createMenu();
	
	return 1;
}

										
PLUGIN_API void	XPluginStop(void)
{
	
	XPLMDestroyMenu(menu_id);
	if (widget_created == 1) {
		XPDestroyWidget(preferences_widget, 1);
		widget_created = 0;
	}
	
	if (socket_open == 1) {
#if IBM		
		if (closesocket(sockfd) == -1) {
#else
	    if (close(sockfd) == -1) {
#endif
			XPLMDebugString("XHSI: failed - Caught error while closing socket! (");
			XPLMDebugString((char * const) strerror(errno));
			XPLMDebugString(")\n");
		} else {
			XPLMDebugString("XHSI: Closed Socket\n");
		}
		socket_open = 0;
		send_enabled = 0;
	}		
}

PLUGIN_API void XPluginDisable(void) {
	XPLMUnregisterFlightLoopCallback(SendSimDataFlightLoopCallback, NULL);	
	XPLMUnregisterFlightLoopCallback(SendFMSDataFlightLoopCallback, NULL);	
	XPLMDebugString("XHSI: disabled\n");
	plugin_enabled = 0;
	send_enabled = 0;
}

PLUGIN_API int XPluginEnable(void)
{
	send_enabled = 1;
	plugin_enabled = 1;
    XPLMDebugString("XHSI: enabled\n");	
	
	// Register flight loop callbacks
	XPLMRegisterFlightLoopCallback(		
										SendSimDataFlightLoopCallback,	
										time_for_each_sim_data_frame,
										NULL);	
	
	XPLMRegisterFlightLoopCallback(		
										SendFMSDataFlightLoopCallback,	
										time_for_each_fms_data_frame,
										NULL);	
	return ensure_socket_bound();
}

PLUGIN_API void XPluginReceiveMessage(
					XPLMPluginID	inFromWho,
					long			inMessage,
					void *			inParam)
{
}

#if IBM
int start_winsock() {
	WSADATA wsa;
	WORD version;
	version = (2<<8)+1;
	int rc = WSAStartup(version, &wsa);
	char* debug_message;
	
	if (rc != 0) {
		sprintf(debug_message, "XHSI failed: Could not start Winsock! Errorcode: %d\n", WSAGetLastError());
		XPLMDebugString(debug_message);
		return 0;
	} else {
		XPLMDebugString("XHSI: winsock started\n");
		return 1;
	}
}
#endif

float SendSimDataFlightLoopCallback(
                                   float                inElapsedSinceLastCall,    
                                   float                inElapsedTimeSinceLastFlightLoop,    
                                   int                  inCounter,    
                                   void *               inRefcon)
{
	if ((plugin_enabled == 1) && (send_enabled == 1) && (socket_open == 1)) {
		createHSISimDataPacket();	
		if (sendto(sockfd, (const char*)&data_packet, sizeof(data_packet), 0, (struct sockaddr *)&dest_addr, sizeof(struct sockaddr)) == -1) {
			XPLMDebugString("XHSI: Caught error while sending SIM packet! (");
			XPLMDebugString((char * const) strerror(errno));
			XPLMDebugString(")\n");		
		} 
		return time_for_each_sim_data_frame;	
	} else {
		return 1;
	}
} 

float SendFMSDataFlightLoopCallback(
									float                inElapsedSinceLastCall,    
									float                inElapsedTimeSinceLastFlightLoop,    
									int                  inCounter,    
									void *               inRefcon)
{
	if ((plugin_enabled == 1) && (send_enabled == 1) && (socket_open == 1))  {
		createHSIFMSDataPacket();	
		if (sendto(sockfd, (const char*)&fms_entries, sizeof(fms_entries), 0, (struct sockaddr *)&dest_addr, sizeof(struct sockaddr)) == -1) {
			XPLMDebugString("XHSI: Caught error while sending FMS packet! (");
			XPLMDebugString((char * const) strerror(errno));
			XPLMDebugString(")\n");		
		} 
		return time_for_each_fms_data_frame;		
	} else {
		return 1;
	}
} 

// Socket stuff ---------------------------------------------------------------------------

void set_addresses() {
	XPLMDebugString("XHSI: Setting addresses");
	
	src_addr.sin_family = AF_INET;
	src_addr.sin_port = htons(src_port);
	src_addr.sin_addr.s_addr = INADDR_ANY;
	memset(&(src_addr.sin_zero), '\0', 8);
	
	dest_addr.sin_family = AF_INET;
	dest_addr.sin_port = htons(dest_port);
	dest_addr.sin_addr.s_addr = inet_addr(dest_ip);
	memset(&(dest_addr.sin_zero), '\0', 8);	

	char addr_cleartext[100];
	sprintf(addr_cleartext, "XHSI: Prefs: IP=%s  SRC-PORT=%d  DEST-PORT=%d  SIM-FDEL=%f  FMS-FDEL=%f\n",dest_ip, src_port, dest_port,time_for_each_sim_data_frame,time_for_each_fms_data_frame);
	XPLMDebugString(addr_cleartext);
}

int ensure_socket_bound() {
	if (socket_open != 1) {
		if (bind(sockfd, (struct sockaddr*)&src_addr, sizeof(struct sockaddr)) == -1) {
			XPLMDebugString("XHSI: caught error while binding socket (");
			XPLMDebugString((char * const) strerror(errno));
			XPLMDebugString(")\n");
			socket_open = 0;
			return -1;
		} else {
			XPLMDebugString("XHSI: Socket bound!\n");
			socket_open = 1;
			return 1;
		}	
	} else {
		return 1;
	}
} 


// preferences menu and widget -------------------------------------------------------

void createMenu() {
	menu_item = XPLMAppendMenuItem(XPLMFindPluginsMenu(), "XHSI", NULL, 1);	
	menu_id = XPLMCreateMenu("XHSI", XPLMFindPluginsMenu(), menu_item, ExternalHSIMenuHandler, NULL);
	XPLMAppendMenuItem(menu_id, "Preferences", (void *) MENU_ITEM_PREFERENCES, 1);
}	

void ExternalHSIMenuHandler(void * mRef, void * iRef)
{
	int item_num = (int) iRef;
		
	if (item_num == MENU_ITEM_PREFERENCES) {
		if (widget_created == 0) {
			CreateExternalHSIPreferencesDialog(300,650,300,170);
			widget_created = 1;
		}
		if(!XPIsWidgetVisible(preferences_widget)) {
			XPShowWidget(preferences_widget);
		}		
		send_enabled = 0;
		widget_shows = 1;
	} 
}	

void CreateExternalHSIPreferencesDialog(int x, int y, int w, int h) {
	
	int x2 = x + w;
	int y2 = y - h;
	
	preferences_widget = XPCreateWidget(x,y,x2,y2,
										1,								// visible
										"XHSI Preferences",		// window title
										1,								// root
										NULL,							// no container
										xpWidgetClass_MainWindow);
	XPSetWidgetProperty(preferences_widget, xpProperty_MainWindowHasCloseBoxes, 1);
	
	preferences_sender_window_widget = XPCreateWidget(x+20,y-20,190,y2+50,
											   1,								// visible
											   "Sender",						// window title
											   1,								// root
											   preferences_widget,				// no container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(preferences_sender_window_widget, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	preferences_receiver_window_widget = XPCreateWidget(x+210,y-20,x2-20,y2+50,
											   1,								// visible
											   "Receiver",						// window title
											   1,								// root
											   preferences_widget,				// no container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(preferences_receiver_window_widget, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	
	sender_label = XPCreateWidget(x+10, y-10, x+100, y-60,
							  1, "Sender", 0, preferences_widget,
							  xpWidgetClass_Caption);

	receiver_label = XPCreateWidget(x+160, y-10, x2, y-60,
							  1, "Receiver", 0, preferences_widget,
							  xpWidgetClass_Caption);

	
	ip_label = XPCreateWidget(x+160, y-50, x2, y-60,
									   1, "IP", 0, preferences_widget,
									   xpWidgetClass_Caption);
		
	ip_textbox_widget = XPCreateWidget(x+190, y-50, x+290, y-60,
									  1, "127.0.0.1", 0, preferences_widget,
									  xpWidgetClass_TextField);

	
	receiver_port_label = XPCreateWidget(x+160, y-70, x2, y-80,
							  1, "Port", 0, preferences_widget,
							  xpWidgetClass_Caption);
	
	receiver_port_textbox_widget = XPCreateWidget(x+245, y-70, x+290, y-80,
									   1, "49001", 0, preferences_widget,
									   xpWidgetClass_TextField);
	
	
	sim_data_rate_label = XPCreateWidget(x+10, y-90, x2, y-100,
										 1, "Sim data frames/s", 0, preferences_widget,
										 xpWidgetClass_Caption);
	
	data_rate_sim_textbox = XPCreateWidget(x+110, y-90, x+150, y-100,
											  1, "26", 0, preferences_widget,
											  xpWidgetClass_TextField);
	
	
	fms_data_rate_label = XPCreateWidget(x+10, y-110, x2, y-120,
										 1, "FMS data frames/s", 0, preferences_widget,
										 xpWidgetClass_Caption);
	
	data_rate_fms_textbox = XPCreateWidget(x+110, y-110, x+150, y-120,
										   1, "1", 0, preferences_widget,
										   xpWidgetClass_TextField);
	
	cancel_button = XPCreateWidget(x+(w/4), y2+30, x+(w/2)-10, y2+20,
										 1, "Cancel", 0, preferences_widget,
										 xpWidgetClass_Button);
	XPSetWidgetProperty(cancel_button, xpProperty_ButtonType, xpPushButton);

	
	set_button = XPCreateWidget(x+(w/2)+10, y2+30, x+((3*w)/4), y2+20,
								  1, "Set", 0, preferences_widget,
								  xpWidgetClass_Button);
	XPSetWidgetProperty(set_button, xpProperty_ButtonType, xpPushButton);
	
	
	XPAddWidgetCallback(preferences_widget, Preferences_Widget_Handler);
}

void close_dialog() {
	if (widget_shows == 1) {
		XPHideWidget(preferences_widget);
		widget_shows = 0;
		send_enabled = 1;
	}	
}

int Preferences_Widget_Handler(
							   XPWidgetMessage		inMessage,
							   XPWidgetID			inWidget,
							   long					inParam1,
							   long					inParam2) {
	
	char buffer[256];
	char buffer2[256];
	
	if (inMessage == xpMessage_CloseButtonPushed) {
		close_dialog();
		return 1;
	}
	
	if (inMessage == xpMsg_PushButtonPressed) {
		if (inParam1 == (long)set_button) {
			XPGetWidgetDescriptor(ip_textbox_widget, buffer, sizeof(buffer));			
			dest_ip = buffer;

			XPGetWidgetDescriptor(receiver_port_textbox_widget, buffer2, sizeof(buffer2));			
			dest_port = strtol(buffer2, NULL, 10);
						
			XPGetWidgetDescriptor(data_rate_sim_textbox, buffer2, sizeof(buffer2));			
			sim_data_frames_per_second = strtol(buffer2, NULL, 10);			
			time_for_each_sim_data_frame = 1.0/sim_data_frames_per_second;

			XPGetWidgetDescriptor(data_rate_fms_textbox, buffer2, sizeof(buffer2));			
			fms_data_frames_per_second = strtol(buffer2, NULL, 10);			
			time_for_each_fms_data_frame = 1.0/fms_data_frames_per_second;

			set_addresses();
			ensure_socket_bound();
			close_dialog();
			return 1;
			
		} else if (inParam1 == (long)cancel_button) {
			close_dialog();
			return 1;
		}
	}
	return 0;
}

void createHSISimDataPacket(void) {
	
	strncpy(data_packet.packet_id, "HSID",4);
	data_packet.nb_of_data_points = htonf(45.0);
	
	data_packet.data_points[0].id = htonf((float) SIM_FLIGHTMODEL_POSITION_GROUNDSPEED);
	data_packet.data_points[0].value = htonf(XPLMGetDataf(groundspeed));
	data_packet.data_points[1].id = htonf((float) SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED);
	data_packet.data_points[1].value = htonf(XPLMGetDataf(true_airspeed));
	data_packet.data_points[2].id = htonf((float) SIM_FLIGHTMODEL_POSITION_MAGPSI);
	data_packet.data_points[2].value = htonf(XPLMGetDataf(magpsi));
	data_packet.data_points[3].id = htonf((float) SIM_FLIGHTMODEL_POSITION_HPATH);
	data_packet.data_points[3].value = htonf(XPLMGetDataf(hpath));
	data_packet.data_points[4].id = htonf((float) SIM_FLIGHTMODEL_POSITION_LATITUDE);
	data_packet.data_points[4].value = htonf((float) XPLMGetDatad(latitude));
	data_packet.data_points[5].id = htonf((float) SIM_FLIGHTMODEL_POSITION_LONGITUDE);
	data_packet.data_points[5].value = htonf((float) XPLMGetDatad(longitude));
	data_packet.data_points[6].id = htonf((float) SIM_FLIGHTMODEL_POSITION_PHI);
	data_packet.data_points[6].value = htonf(XPLMGetDataf(phi));
	data_packet.data_points[7].id = htonf((float) SIM_FLIGHTMODEL_POSITION_R);
	data_packet.data_points[7].value = htonf(XPLMGetDataf(r));
	
	data_packet.data_points[8].id = htonf((float) SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ);
	data_packet.data_points[8].value = htonf((float) XPLMGetDatai(nav1_freq_hz));
	data_packet.data_points[9].id = htonf((float) SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ);
	data_packet.data_points[9].value = htonf((float) XPLMGetDatai(nav2_freq_hz));
	data_packet.data_points[10].id = htonf((float) SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ);
	data_packet.data_points[10].value = htonf((float) XPLMGetDatai(adf1_freq_hz));
	data_packet.data_points[11].id = htonf((float) SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ);
	data_packet.data_points[11].value = htonf((float) XPLMGetDatai(adf2_freq_hz));
	data_packet.data_points[12].id = htonf((float) SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT);
	data_packet.data_points[12].value = htonf(XPLMGetDataf(nav1_dir_degt));	
	data_packet.data_points[13].id = htonf((float) SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT);
	data_packet.data_points[13].value = htonf(XPLMGetDataf(nav2_dir_degt));	
	data_packet.data_points[14].id = htonf((float) SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT);
	data_packet.data_points[14].value = htonf(XPLMGetDataf(adf1_dir_degt));	
	data_packet.data_points[15].id = htonf((float) SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT);
	data_packet.data_points[15].value = htonf(XPLMGetDataf(adf2_dir_degt));	
	data_packet.data_points[16].id = htonf((float) SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M);
	data_packet.data_points[16].value = htonf(XPLMGetDataf(nav1_dme_dist_m));	
	data_packet.data_points[17].id = htonf((float) SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M);
	data_packet.data_points[17].value = htonf(XPLMGetDataf(nav2_dme_dist_m));	
	data_packet.data_points[18].id = htonf((float) SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M);
	data_packet.data_points[18].value = htonf(XPLMGetDataf(adf1_dme_dist_m));	
	data_packet.data_points[19].id = htonf((float) SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M);
	data_packet.data_points[19].value = htonf(XPLMGetDataf(adf2_dme_dist_m));	
	data_packet.data_points[20].id = htonf((float) SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM);
	data_packet.data_points[20].value = htonf(XPLMGetDataf(nav1_obs_degm));		
	data_packet.data_points[21].id = htonf((float) SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM);
	data_packet.data_points[21].value = htonf(XPLMGetDataf(nav2_obs_degm));		
	
	data_packet.data_points[22].id = htonf((float) SIM_COCKPIT_AUTOPILOT_HEADING_MAG);
	data_packet.data_points[22].value = htonf(XPLMGetDataf(autopilot_heading_mag));			
	data_packet.data_points[23].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR);
	data_packet.data_points[23].value = htonf((float) XPLMGetDatai(efis_map_range_selector));		
	data_packet.data_points[24].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR);
	data_packet.data_points[24].value = htonf((float) XPLMGetDatai(efis_dme_1_selector));		
	data_packet.data_points[25].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR);
	data_packet.data_points[25].value = htonf((float) XPLMGetDatai(efis_dme_2_selector));		
	data_packet.data_points[26].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER);
	data_packet.data_points[26].value = htonf((float) XPLMGetDatai(efis_shows_weather));		
	data_packet.data_points[27].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS);
	data_packet.data_points[27].value = htonf((float) XPLMGetDatai(efis_shows_tcas));		
	data_packet.data_points[28].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS);
	data_packet.data_points[28].value = htonf((float) XPLMGetDatai(efis_shows_airports));		
	data_packet.data_points[29].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS);
	data_packet.data_points[29].value = htonf((float) XPLMGetDatai(efis_shows_waypoints));		
	data_packet.data_points[30].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS);
	data_packet.data_points[30].value = htonf((float) XPLMGetDatai(efis_shows_vors));		
	data_packet.data_points[31].id = htonf((float) SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS);
	data_packet.data_points[31].value = htonf((float) XPLMGetDatai(efis_shows_ndbs));		
		
	data_packet.data_points[32].id = htonf((float) SIM_WEATHER_WIND_SPEED_KT);
	data_packet.data_points[32].value = htonf(XPLMGetDataf(wind_speed_kt));		
	data_packet.data_points[33].id = htonf((float) SIM_WEATHER_WIND_DIRECTION_DEGT);
	data_packet.data_points[33].value = htonf(XPLMGetDataf(wind_direction_degt));			
	
	data_packet.data_points[34].id = htonf((float) SIM_TIME_ZULU_TIME_SEC);
	data_packet.data_points[34].value = htonf(XPLMGetDataf(zulu_time_sec));		
	data_packet.data_points[35].id = htonf((float) SIM_TIME_LOCAL_TIME_SEC);
	data_packet.data_points[35].value = htonf(XPLMGetDataf(local_time_sec));	

	data_packet.data_points[36].id = htonf((float) SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM);
	data_packet.data_points[36].value = htonf(XPLMGetDataf(nav1_course_degm));	
	data_packet.data_points[37].id = htonf((float) SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM);
	data_packet.data_points[37].value = htonf(XPLMGetDataf(nav2_course_degm));	

	data_packet.data_points[38].id = htonf((float) SIM_COCKPIT_RADIOS_NAV1_CDI);
	data_packet.data_points[38].value = htonf((float) XPLMGetDatai(nav1_cdi));	
	data_packet.data_points[39].id = htonf((float) SIM_COCKPIT_RADIOS_NAV2_CDI);
	data_packet.data_points[39].value = htonf((float) XPLMGetDatai(nav1_cdi));	

	data_packet.data_points[40].id = htonf((float) SIM_COCKPIT_AUTOPILOT_STATE);
	data_packet.data_points[40].value = htonf((float) XPLMGetDatai(autopilot_state));	

	data_packet.data_points[41].id = htonf((float) SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY);
	data_packet.data_points[41].value = htonf((float) XPLMGetDataf(autopilot_vertical_velocity));	
	data_packet.data_points[42].id = htonf((float) SIM_COCKPIT_AUTOPILOT_ALTITUDE);
	data_packet.data_points[42].value = htonf((float) XPLMGetDataf(autopilot_altitude));	
	
	data_packet.data_points[43].id = htonf((float) SIM_FLIGHTMODEL_POSITION_MAGVAR);
	data_packet.data_points[43].value = htonf(XPLMGetDataf(magvar));	
	
	data_packet.data_points[44].id = htonf((float) PLUGIN_VERSION_ID);
	data_packet.data_points[44].value = htonf((float) plugin_version_id);
}

void createHSIFMSDataPacket(void) {
	char id[80];
	long altitude;
	float lat;
	float lon;
	int toc_tod_flag = 0;
	long nb_of_fms_entries = XPLMCountFMSEntries();
	long i;
	fms_entries.nb_of_entries = htonf((float)nb_of_fms_entries);
	
	if (nb_of_fms_entries > 0) {
		strncpy(fms_entries.packet_id, "FMSE",4);
		fms_entries.active_entry_index = htonf((float)XPLMGetDisplayedFMSEntry());
		fms_entries.destination_entry_index = htonf((float)XPLMGetDestinationFMSEntry());
		
		XPLMNavType type;
		XPLMNavRef outRef; 
		
		for (i=0;i<nb_of_fms_entries;i++) {
			XPLMGetFMSEntryInfo(
								i,
								&type,
								id,
								&outRef,
								&altitude,
								&lat,
								&lon);
			
			fms_entries.entries[i].type = htonf((float) type);
			fms_entries.entries[i].altitude = htonf((float) altitude);
			if (fms_entries.entries[i].type == 2048) {
				if (toc_tod_flag == 0) {
					strncpy(fms_entries.entries[i].id,"T/C",sizeof(fms_entries.entries[i].id));
					toc_tod_flag = 1;
				} else {
					strncpy(fms_entries.entries[i].id,"T/D",sizeof(fms_entries.entries[i].id));
				}
			} else {
				strncpy(fms_entries.entries[i].id, id, sizeof(fms_entries.entries[i].id));
			}
			fms_entries.entries[i].lat = htonf(lat);
			fms_entries.entries[i].lon = htonf(lon);	
		}
	}
}

void findDataRefs(void) {
	groundspeed = XPLMFindDataRef("sim/flightmodel/position/groundspeed");	
	true_airspeed = XPLMFindDataRef("sim/flightmodel/position/true_airspeed"); 
	magpsi = XPLMFindDataRef("sim/flightmodel/position/magpsi"); 
	hpath = XPLMFindDataRef("sim/flightmodel/position/hpath");
	latitude = XPLMFindDataRef("sim/flightmodel/position/latitude");	// double
	longitude = XPLMFindDataRef("sim/flightmodel/position/longitude");	// double
	phi = XPLMFindDataRef("sim/flightmodel/position/phi");
	r = XPLMFindDataRef("sim/flightmodel/position/R");
		magvar = XPLMFindDataRef("sim/flightmodel/position/magnetic_variation");
	
	nav1_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav1_freq_hz");  // int
	nav2_freq_hz = XPLMFindDataRef("sim/cockpit/radios/nav2_freq_hz");	// int
	adf1_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf1_freq_hz");  // int
	adf2_freq_hz = XPLMFindDataRef("sim/cockpit/radios/adf2_freq_hz");  // int
	nav1_dir_degt = XPLMFindDataRef("sim/cockpit/radios/nav1_dir_degt");
	nav2_dir_degt = XPLMFindDataRef("sim/cockpit/radios/nav2_dir_degt");
	adf1_dir_degt = XPLMFindDataRef("sim/cockpit/radios/adf1_dir_degt");
	adf2_dir_degt = XPLMFindDataRef("sim/cockpit/radios/adf2_dir_degt");
	nav1_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/nav1_dme_dist_m");
	nav2_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/nav2_dme_dist_m");
	adf1_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/adf1_dme_dist_m");
	adf2_dme_dist_m = XPLMFindDataRef("sim/cockpit/radios/adf2_dme_dist_m");
	nav1_obs_degm = XPLMFindDataRef("sim/cockpit/radios/nav1_obs_degm");
	nav2_obs_degm = XPLMFindDataRef("sim/cockpit/radios/nav2_obs_degm");	
	
	nav1_course_degm = XPLMFindDataRef("sim/cockpit/radios/nav1_course_degm");
	nav2_course_degm = XPLMFindDataRef("sim/cockpit/radios/nav2_course_degm");
	nav1_cdi = XPLMFindDataRef("sim/cockpit/radios/nav1_CDI");
	nav2_cdi = XPLMFindDataRef("sim/cockpit/radios/nav2_CDI");

	autopilot_state = XPLMFindDataRef("sim/cockpit/autopilot/autopilot_state");
	autopilot_vertical_velocity = XPLMFindDataRef("sim/cockpit/autopilot/vertical_velocity");
	autopilot_altitude = XPLMFindDataRef("sim/cockpit/autopilot/altitude");
	autopilot_approach_selector = XPLMFindDataRef("sim/cockpit/autopilot/approach_selector");
	
	autopilot_heading_mag = XPLMFindDataRef("sim/cockpit/autopilot/heading_mag");			
	efis_map_range_selector = XPLMFindDataRef("sim/cockpit/switches/EFIS_map_range_selector");	// int	
	efis_dme_1_selector = XPLMFindDataRef("sim/cockpit/switches/EFIS_dme_1_selector");		// int
	efis_dme_2_selector = XPLMFindDataRef("sim/cockpit/switches/EFIS_dme_2_selector");		// int
	efis_shows_weather = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_weather");		// int
	efis_shows_tcas = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_tcas");				// int
	efis_shows_airports = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_airports");		// int
	efis_shows_waypoints = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_waypoints");	// int
	efis_shows_vors = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_VORs");				// int
	efis_shows_ndbs = XPLMFindDataRef("sim/cockpit/switches/EFIS_shows_NDBs");				// int
	
	wind_speed_kt = XPLMFindDataRef("sim/weather/wind_speed_kt");
	wind_direction_degt = XPLMFindDataRef("sim/weather/wind_direction_degt");
	
	zulu_time_sec = XPLMFindDataRef("sim/time/zulu_time_sec");
	local_time_sec = XPLMFindDataRef("sim/time/local_time_sec");
}

