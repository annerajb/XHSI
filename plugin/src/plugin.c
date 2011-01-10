/**
* XHSI_plugin.c
*
* This X-Plane plugin sends X-Plane simulator data to an XHSI
* application instance via UDP.
*
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
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

/** Attention when adding DataRefs !
* A new DataRef should be added in 4 places:
*
* - define the same constant as in the XHSI app
*         #define SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT 122
*
* - define a variable to contain the DataRef
*         XPLMDataRef nav1_vdef_dot;
*
* - find the DataRef
*         nav1_vdef_dot = XPLMFindDataRef("sim/cockpit/radios/nav1_vdef_dot");
*
* - get the data and add it to the UDP packet (pay attention to the type:
*     XPLMGetDatad : double,
*     XPLMGetDataf : float,
*     XPLMGetDatai : int,
*   and don't forget the i++; )
*         sim_packet.sim_data_points[i].id = custom_htonl((long) SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT);
*         sim_packet.sim_data_points[i].value = custom_htonf((float) XPLMGetDataf(nav1_vdef_dot));
*         i++;
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define XPLM200 1

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPWidgets.h"
#include "XPStandardWidgets.h"


#include "globals.h"

#include "settings.h"
#include "structs.h"
#include "datarefs.h"
#include "ui.h"
#include "net.h"
#include "packets.h"
#include "sender.h"
#include "receiver.h"
#include "commands.h"
//#include "display.h"



// Define global vars
// plugin status
int     	xhsi_plugin_enabled;
int		    xhsi_send_enabled;



// X-Plane SDK API methods -----------------------------------

PLUGIN_API int XPluginStart(
						char *		outName,
						char *		outSig,
						char *		outDesc) {

	XPLMDebugString(PLUGIN_VERSION_TEXT);
	XPLMDebugString("\n");

	strcpy(outName, PLUGIN_VERSION_TEXT);
	strcpy(outSig, "xhsi.plugin");
	strcpy(outDesc, "This plugin communicates with XHSI application instances over UDP.");

	// Find the datarefs we want to send and receive
	findDataRefs();

	// Register custom X-Plane datarefs
	registerPilotDataRefs();
	registerCopilotDataRefs();

	// register custom X-Plane commands
	registerCommands();

	// read config file or use defaults
	initSettings();

    if (openSocket() == 0) {
        return 0;
    }

	setAddresses();

	createUI();

	return 1;

}


PLUGIN_API int XPluginEnable(void) {

char nav_id_bytes[10];
strcpy(nav_id_bytes, "    ");
XPLMGetDatab(nav1_id, nav_id_bytes, 0, 4);
XPLMDebugString("XHSI: NAV1_ID = ");
XPLMDebugString(nav_id_bytes);
XPLMDebugString(" len=");
int i;
i = strlen(nav_id_bytes);
sprintf(nav_id_bytes, "%d", i);
XPLMDebugString(nav_id_bytes);
XPLMDebugString("\n");
	xhsi_send_enabled = 1;
	xhsi_plugin_enabled = 1;
    XPLMDebugString("XHSI: enabled\n");

	// register flight loop callbacks
	XPLMRegisterFlightLoopCallback(
							sendSimCallback,
							nav_data_delay,
							NULL);

	XPLMRegisterFlightLoopCallback(
							sendFmsCallback,
							fms_data_delay,
							NULL);

	XPLMRegisterFlightLoopCallback(
							sendTcasCallback,
							tcas_data_delay,
							NULL);

	XPLMRegisterFlightLoopCallback(
							receiveCallback,
							recv_delay,
							NULL);

	// initialize custom X-Plane datarefs
	XPLMRegisterFlightLoopCallback(
							initPilotCallBack,
							1.0f,
							NULL);
	XPLMRegisterFlightLoopCallback(
							initCopilotCallBack,
							1.0f,
							NULL);

	return bindSocket();

}


PLUGIN_API void XPluginReceiveMessage(
					XPLMPluginID	inFromWho,
					long			inMessage,
					void *			inParam) {
    // nop
}


PLUGIN_API void XPluginDisable(void) {

	XPLMUnregisterFlightLoopCallback(sendSimCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendFmsCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendTcasCallback, NULL);

	XPLMUnregisterFlightLoopCallback(receiveCallback, NULL);

	XPLMUnregisterFlightLoopCallback(initPilotCallBack, NULL);
	XPLMUnregisterFlightLoopCallback(initCopilotCallBack, NULL);

	XPLMDebugString("XHSI: disabled\n");
	xhsi_plugin_enabled = 0;
	xhsi_send_enabled = 0;

}


PLUGIN_API void	XPluginStop(void) {

	// unregister custom X-Plane commands
	unregisterCommands();

    // unregister custom X-Plane datarefs
 	unregisterPilotDataRefs();
 	unregisterCopilotDataRefs();

	destroyUI();
	closeSocket();

}


