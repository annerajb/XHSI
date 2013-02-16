/**
* XHSI_plugin.c
*
* This X-Plane plugin sends X-Plane simulator data to an XHSI
* application instance via UDP.
*
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009-2011  Marc Rogiers (marrog.123@gmail.com)
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
#include <stdint.h>

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif

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
#include "datarefs_ufmc.h"
#include "datarefs_x737.h"
#include "datarefs_cl30.h"
#include "ui.h"
#include "net.h"
#include "packets.h"
#include "sender.h"
#include "receiver.h"
#include "commands.h"




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
	registerMFDDataRefs();

	// register custom X-Plane commands
	registerCommands();

	// read config file or use defaults
	initSettings();

	createUI();

	return 1;

}


PLUGIN_API int XPluginEnable(void) {

//char nav_id_bytes[10];
//strcpy(nav_id_bytes, "    ");
//XPLMGetDatab(nav1_id, nav_id_bytes, 0, 4);
//XPLMDebugString("XHSI: NAV1_ID = ");
//XPLMDebugString(nav_id_bytes);
//XPLMDebugString(" len=");
//int i;
//i = strlen(nav_id_bytes);
//sprintf(nav_id_bytes, "%d", i);
//XPLMDebugString(nav_id_bytes);
//XPLMDebugString("\n");
	xhsi_send_enabled = 1;
	xhsi_plugin_enabled = 1;

    XPLMDebugString("XHSI: enabling...\n");

    if (openSocket() == 0) {
        return 0;
    }

	setAddresses();

    bindSocket();

    nonBlocking();


    XPLMDebugString("XHSI: registering flightloop callbacks\n");

	// register flight loop callbacks
	XPLMRegisterFlightLoopCallback(
							sendADCCallback,
							adc_data_delay,
							NULL);

	XPLMRegisterFlightLoopCallback(
							sendAvionicsCallback,
							avionics_data_delay,
							NULL);

	XPLMRegisterFlightLoopCallback(
							sendStaticCallback,
							static_data_delay,
							NULL);

	XPLMRegisterFlightLoopCallback(
							sendEnginesCallback,
							engines_data_delay,
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
							recv_delay * 100.0f,
							NULL);

	// initialize custom X-Plane datarefs
	XPLMRegisterFlightLoopCallback(
							initPilotCallback,
							1.5f,
							NULL);
	XPLMRegisterFlightLoopCallback(
							initCopilotCallback,
							2.5f,
							NULL);

    // UFMC
    XPLMRegisterFlightLoopCallback(
							checkUFMCCallback,
							5.0f,
							NULL);

    // X737
    XPLMRegisterFlightLoopCallback(
							checkX737Callback,
							5.0f,
							NULL);

    // CL30
    XPLMRegisterFlightLoopCallback(
							checkCL30Callback,
							5.0f,
							NULL);

    XPLMDebugString("XHSI: flightloop callbacks registered\n");

	return 1;

}


PLUGIN_API void XPluginReceiveMessage(
					XPLMPluginID	inFromWho,
					long			inMessage,
					void *			inParam) {
    // nop
}


PLUGIN_API void XPluginDisable(void) {

	XPLMDebugString("XHSI: disabling...\n");

    XPLMDebugString("XHSI: unregistering flightloop callbacks\n");

	XPLMUnregisterFlightLoopCallback(sendADCCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendAvionicsCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendEnginesCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendStaticCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendFmsCallback, NULL);
	XPLMUnregisterFlightLoopCallback(sendTcasCallback, NULL);

	XPLMUnregisterFlightLoopCallback(receiveCallback, NULL);

	XPLMUnregisterFlightLoopCallback(initPilotCallback, NULL);
	XPLMUnregisterFlightLoopCallback(initCopilotCallback, NULL);

	XPLMUnregisterFlightLoopCallback(checkUFMCCallback, NULL);
	XPLMUnregisterFlightLoopCallback(checkX737Callback, NULL);
	XPLMUnregisterFlightLoopCallback(checkCL30Callback, NULL);

    XPLMDebugString("XHSI: flightloop callbacks unregistered\n");

	closeSocket();

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
 	unregisterMFDDataRefs();

	destroyUI();

}


