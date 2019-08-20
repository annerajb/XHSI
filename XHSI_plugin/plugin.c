/**
* XHSI_plugin.c
*
* This X-Plane plugin sends X-Plane simulator data to an XHSI
* application instance via UDP.
*
* Copyright (C) 2007-2009  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009-2015  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014-2019  Nicolas Carel
* Copyright (C) 2017-2018  Technische Hochschule Ingolstadt (https://www.thi.de/en/)
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

#include "XPLMPlugin.h"
#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"

#include "globals.h"

#include "settings.h"
#include "structs.h"
#include "datarefs.h"
#include "datarefs_ufmc.h"
#include "datarefs_x737.h"
#include "datarefs_z737.h"
#include "datarefs_cl30.h"
#include "datarefs_qpac.h"
#include "datarefs_pa_a320.h"
#include "datarefs_jar_a320neo.h"
#include "datarefs_ff_a320.h"
#include "datarefs_pilotedge.h"
#include "datarefs_xjoymap.h"
#include "datarefs_x_raas.h"
#include "ui.h"
#include "net.h"
#include "packets.h"
#include "sender.h"
#include "receiver.h"
#include "commands.h"
#include "xfmc.h"
#include "ufmc.h"
#include "qpac_msg.h"
#include "jar_a320neo_msg.h"



// Define global vars
// plugin status
int xhsi_plugin_enabled;
int	xhsi_send_enabled;


// Source FMS
int xhsi_fms;



// X-Plane SDK API methods -----------------------------------

PLUGIN_API int XPluginStart(
        char *      outName,
        char *      outSig,
        char *      outDesc) {

    XPLMDebugString(PLUGIN_VERSION_TEXT);
    XPLMDebugString("\n");

    strcpy(outName, PLUGIN_VERSION_TEXT);
    strcpy(outSig, "xhsi.plugin");
    strcpy(outDesc, "This plugin communicates with XHSI application instances over UDP.");

    // Use Posix-style paths
    XPLMEnableFeature("XPLM_USE_NATIVE_PATHS", 1);


    // Find the datarefs we want to send and receive
    findDataRefs();
    findXfmcDataRefs();

    // Register custom X-Plane datarefs
    registerPilotDataRefs();
    registerCopilotDataRefs();
    registerGeneralDataRefs();
    registerEICASDataRefs();
    registerMFDDataRefs();
    registerCDUDataRefs();

    // register custom X-Plane commands
    registerCommands();

    // read config file or use defaults
    initSettings();

    createUI();

    return 1;

}


PLUGIN_API int XPluginEnable(void) {

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
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
    		sendAuxiliarySystemsCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
            sendAvionicsCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
            sendStaticCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
            sendEnginesCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
            sendFmsCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
            sendTcasCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
            receiveCallback,
            -100.0f,
            NULL);

    // initialize custom X-Plane datarefs
    XPLMRegisterFlightLoopCallback(
            initGeneralCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initPilotCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initCopilotCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initEGPWSCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initWeatherRadarCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initEICASCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initMFDCallback,
            -1.0f,
            NULL);
    XPLMRegisterFlightLoopCallback(
            initCDUCallback,
            -1.0f,
            NULL);

    // UFMC
    XPLMRegisterFlightLoopCallback(
            checkUFMCCallback,
            -1.0f,
            NULL);

    XPLMRegisterFlightLoopCallback(
    		sendUfmcExtendedFmsCallback,
            -1.0f,
            NULL);

    // X737
    XPLMRegisterFlightLoopCallback(
            checkX737Callback,
            -1.0f,
            NULL);

    // Z737 Zibo Mod Boeing 737-800
    XPLMRegisterFlightLoopCallback(
            checkZibo737Callback,
            -1.0f,
            NULL);

    // CL30
    XPLMRegisterFlightLoopCallback(
            checkCL30Callback,
            -1.0f,
            NULL);

    // PilotEdge
    XPLMRegisterFlightLoopCallback(
            checkPilotEdgeCallback,
            -1.0f,
            NULL);

    // QPAC AirbusFBW
    XPLMRegisterFlightLoopCallback(
            checkQpacCallback,
            -1.0f,
            NULL);
    // Peter Aircraf Airbus A320
    XPLMRegisterFlightLoopCallback(
            checkPaA320Callback,
            -1.0f,
            NULL);
    // JarDesign Airbus A320neo Aircraft
    XPLMRegisterFlightLoopCallback(
            checkJarA320NeoCallback,
            -1.0f,
            NULL);
    // FlightFactor Airbus A320 ultimate Aircraft
    XPLMRegisterFlightLoopCallback(
    		checkFlightFactorA320Callback,
            -1.0f,
            NULL);
    // xjoymap - Dual commands
    XPLMRegisterFlightLoopCallback(
            checkXjoymapCallback,
            -1.0f,
            NULL);
    // xraas - Runway Awareness and Advisory System
    XPLMRegisterFlightLoopCallback(
            checkXRaasCallback,
            -1.0f,
            NULL);

    // X-FMC
    XPLMRegisterFlightLoopCallback(
            sendXfmcCallback,
            -1.0f,
            NULL);

    // U-FMC
    XPLMRegisterFlightLoopCallback(
            sendUfmcCallback,
            -1.0f,
            NULL);

    // QPAC Messages E/WD and MCDU
    XPLMRegisterFlightLoopCallback(
            sendQpacMsgCallback,
            -1.0f,
            NULL);

    // JAR A320 NEO Messages E/WD and MCDU
    XPLMRegisterFlightLoopCallback(
            sendJar_a320MsgCallback,
            -1.0f,
            NULL);

    // Z737 Zibo Mod Boeing 737-800 CDU
    XPLMRegisterFlightLoopCallback(
            sendZibo737MsgCallback,
            -1.0f,
            NULL);

    // Notify DataRefEditor of our custom DataRefs
    XPLMRegisterFlightLoopCallback(
            notifyDataRefEditorCallback,
            -1.0f,
            NULL);

    // Compute Chronometer
    XPLMRegisterFlightLoopCallback(
    		computeChronoCallback,
            -1.0f,
            NULL);

    XPLMDebugString("XHSI: flightloop callbacks registered\n");

    return 1;

}


PLUGIN_API void XPluginReceiveMessage(
        XPLMPluginID	inFromWho,
        long		inMessage,
        void *		inParam) {
    // nop
}


PLUGIN_API void XPluginDisable(void) {

    XPLMDebugString("XHSI: disabling...\n");

    XPLMDebugString("XHSI: unregistering flightloop callbacks\n");

    XPLMUnregisterFlightLoopCallback(sendADCCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendAuxiliarySystemsCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendAvionicsCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendEnginesCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendStaticCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendFmsCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendTcasCallback, NULL);

    XPLMUnregisterFlightLoopCallback(receiveCallback, NULL);

    XPLMUnregisterFlightLoopCallback(initPilotCallback, NULL);
    XPLMUnregisterFlightLoopCallback(initCopilotCallback, NULL);
    XPLMUnregisterFlightLoopCallback(initEGPWSCallback, NULL);
    XPLMUnregisterFlightLoopCallback(initWeatherRadarCallback, NULL);
    XPLMUnregisterFlightLoopCallback(initEICASCallback, NULL);
    XPLMUnregisterFlightLoopCallback(initMFDCallback, NULL);
    XPLMUnregisterFlightLoopCallback(initCDUCallback, NULL);

    // UFMC
    XPLMUnregisterFlightLoopCallback(checkUFMCCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendUfmcExtendedFmsCallback, NULL);

    XPLMUnregisterFlightLoopCallback(checkX737Callback, NULL);
    XPLMUnregisterFlightLoopCallback(checkZibo737Callback, NULL);
    XPLMUnregisterFlightLoopCallback(checkCL30Callback, NULL);
    XPLMUnregisterFlightLoopCallback(checkPilotEdgeCallback, NULL);
    XPLMUnregisterFlightLoopCallback(checkQpacCallback, NULL);
    XPLMUnregisterFlightLoopCallback(checkPaA320Callback, NULL);
    XPLMUnregisterFlightLoopCallback(checkJarA320NeoCallback, NULL);
    XPLMUnregisterFlightLoopCallback(checkFlightFactorA320Callback, NULL);
    XPLMUnregisterFlightLoopCallback(checkXjoymapCallback, NULL);
    XPLMUnregisterFlightLoopCallback(checkXRaasCallback, NULL);

    XPLMUnregisterFlightLoopCallback(sendXfmcCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendQpacMsgCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendJar_a320MsgCallback, NULL);
    XPLMUnregisterFlightLoopCallback(sendZibo737MsgCallback, NULL);

    XPLMUnregisterFlightLoopCallback(computeChronoCallback, NULL);

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
    unregisterGeneralDataRefs();
    unregisterEGPWSDataRefs();
    unregisterWeatherRadarDataRefs();
    unregisterEICASDataRefs();
    unregisterMFDDataRefs();
    unregisterCDUDataRefs();

    destroyUI();

}


