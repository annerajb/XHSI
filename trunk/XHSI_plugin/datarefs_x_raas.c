/*
 * datarefs_x_raas.c
 *
 *  Created on: 7 nov. 2017
 *      Author: Nicolas Carel
 *
 *  X-RAAS is an open-source plugin that simulates
 *  the Honeywell Runway Awareness and Advisory System
 *  wrote by Skiselkov [Copyright (c) 2016 Saso Kiselkov]
 *  http://forums.x-pilot.com/files/file/1047-x-raas-runway-awareness-and-advisory-system/
 *
 *  Starting X-RAAS support in XHSI since version 2.0.3 (10 april 2017)
 *
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#define XPLM200 1

#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "ids.h"
#include "datarefs_x_raas.h"

int x_raas_ready = 0;
int x_raas_version = 0;

// Plugin signature
XPLMPluginID x_raas_PluginId = XPLM_NO_PLUGIN_ID;

// DataRefs for X-RAAS
// GPWS integration
XPLMDataRef x_raas_gpws_prio;
XPLMDataRef x_raas_gpws_prio_act;
XPLMDataRef x_raas_gpws_inop;
XPLMDataRef x_raas_gpws_inop_act;
XPLMDataRef x_raas_gpws_flaps;
XPLMDataRef x_raas_gpws_flaps_act;
XPLMDataRef x_raas_gpws_terr;
XPLMDataRef x_raas_gpws_terr_act;
// Approach speed monitor
XPLMDataRef x_raas_vapp;
XPLMDataRef x_raas_vapp_act;
XPLMDataRef x_raas_vref;
XPLMDataRef x_raas_vref_act;
// Exact flaps setting checks
XPLMDataRef x_raas_to_flaps;
XPLMDataRef x_raas_to_flaps_act;
XPLMDataRef x_raas_ldg_flaps;
XPLMDataRef x_raas_ldg_flaps_act;
XPLMDataRef flaps_request;
// ND Alert
XPLMDataRef x_raas_nd_alert;
XPLMDataRef x_raas_nd_alert_disabled;


void findXRaasDataRefs(void) {
    char        buf[100];

    // Find X-RAAS plugin by signature
    x_raas_PluginId = XPLMFindPluginBySignature("skiselkov.xraas2");

	if ( x_raas_PluginId == XPLM_NO_PLUGIN_ID ) {
        x_raas_ready = 0;
        x_raas_version = 0;
    } else {
        if ( x_raas_ready == 0 ) {
            sprintf(buf, "XHSI: X-RAAS2 plugin found - loading datarefs\n");
            XPLMDebugString(buf);

        	x_raas_ready = 1;
            x_raas_version = 200;
            // GPWS integration
            x_raas_gpws_prio = XPLMFindDataRef("xraas/override/GPWS_prio");
            x_raas_gpws_prio_act = XPLMFindDataRef("xraas/override/GPWS_prio_act");
            x_raas_gpws_inop = XPLMFindDataRef("xraas/override/GPWS_inop");
            x_raas_gpws_inop_act = XPLMFindDataRef("xraas/override/GPWS_inop_act");
            x_raas_gpws_flaps = XPLMFindDataRef("xraas/override/GPWS_flaps_ovrd");
            x_raas_gpws_flaps_act = XPLMFindDataRef("xraas/override/GPWS_flaps_ovrd_act");
            x_raas_gpws_terr = XPLMFindDataRef("xraas/override/GPWS_terr_ovrd");
            x_raas_gpws_terr_act = XPLMFindDataRef("xraas/override/GPWS_terr_ovrd_act");
            // Approach speed monitor
            x_raas_vapp = XPLMFindDataRef("xraas/override/Vapp");
            x_raas_vapp_act = XPLMFindDataRef("xraas/override/Vapp_act");
            x_raas_vref = XPLMFindDataRef("xraas/override/Vref");
            x_raas_vref_act = XPLMFindDataRef("xraas/override/Vref_act");
            // Exact flaps setting checks
            x_raas_to_flaps = XPLMFindDataRef("xraas/override/takeoff_flaps");
            x_raas_to_flaps_act = XPLMFindDataRef("xraas/override/takeoff_flaps_act");
            x_raas_ldg_flaps = XPLMFindDataRef("xraas/override/landing_flaps");
            x_raas_ldg_flaps_act = XPLMFindDataRef("xraas/override/landing_flaps_act");
            flaps_request = XPLMFindDataRef("sim/flightmodel/controls/flaprqst");
            // ND Alert
            x_raas_nd_alert = XPLMFindDataRef("xraas/ND_alert");
            x_raas_nd_alert_disabled = XPLMFindDataRef("xraas/ND_alert_overlay_disabled");
        }
    }
}


float checkXRaasCallback(
    float	inElapsedSinceLastCall,
    float	inElapsedTimeSinceLastFlightLoop,
    int		inCounter,
    void *	inRefcon) {

	findXRaasDataRefs();

	// come back in 5sec
	return 5.0;
}
