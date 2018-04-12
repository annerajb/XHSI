/*
 * datarefs_ff_a320.c
 *
 *  Created on: 5 march 2018
 *      Author: https://www.thi.de/en/
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>


#define XPLM200 1

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif


#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMPlugin.h"
#include "datarefs_ff_a320.h"
#include "ids.h"

#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"

// "SharedValue.h" from Flight Factor
// #include "SharedValue.h"

// DataRefs for Flight Factor A320 Ultimate

XPLMPluginID ff_a320_PluginId = XPLM_NO_PLUGIN_ID;

// SharedValuesInterface svi;

XPLMDataRef ff_a320_plugin_status;

int ff_a320_ready = 0;
int ff_a320_version = 0;


void findFlightFactorA320DataRefs(void) {

	ff_a320_PluginId = XPLMFindPluginBySignature(XPLM_FF_SIGNATURE);

	if ( (ff_a320_PluginId == XPLM_NO_PLUGIN_ID) ) {
		ff_a320_ready = 0;
    } else {
        if ( ff_a320_ready == 0 ) {

        	ff_a320_ready = 1;
        	ff_a320_version = 1;

            XPLMDebugString("XHSI: using Flight Factor A320 Ultimate DataRefs\n");



        }
    }
}

float checkFlightFactorA320Callback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findFlightFactorA320DataRefs();

    // come back in 5sec
    return 5.0;
}

void writeFlightFactorA320Data(int id, float value) {
	char ff_info[80];
	sprintf(ff_info, "XHSI: received Flight Factor A320 setting: ID=%d  VALUE=%f\n", id, value);
	XPLMDebugString(ff_info);

}
