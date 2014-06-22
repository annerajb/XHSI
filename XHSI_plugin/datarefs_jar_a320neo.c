/*
 * datarefs_jar_a320neo.c
 *
 *  Created on: 9 june 2014
 *      Author: Nicolas Carel
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>


#define XPLM200 1

//#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
//#include "XPLMNavigation.h"
//#include "XPLMDisplay.h"
//#include "XPLMMenus.h"
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"

// DataRefs for JAR Design Airbus A320 Neo


XPLMDataRef jar_a320_neo_plugin_status;
XPLMDataRef jar_a320_neo_baro_hpa;
XPLMDataRef jar_a320_neo_fcu_hdg_trk;


int jar_a320_neo_ready = 0;


void findJarA320NeoDataRefs(void) {
	// For datarefs checks, remove for release
	// char msg[200];
	// XPLMDataTypeID reftype;

	jar_a320_neo_plugin_status = XPLMFindDataRef("sim/custom/xap/elec/acess");

	if ( ( jar_a320_neo_plugin_status == NULL ) || ( XPLMGetDatai(jar_a320_neo_plugin_status) < 0 ) ) {

		jar_a320_neo_ready = 0;

    } else {
        if ( jar_a320_neo_ready == 0 ) {

        	jar_a320_neo_ready = 1;

            XPLMDebugString("XHSI: finding JarDesign A320 Neo DataRefs\n");
            jar_a320_neo_baro_hpa = XPLMFindDataRef("sim/custom/xap/fcu/baro_ishpa");
            jar_a320_neo_fcu_hdg_trk = XPLMFindDataRef("sim/custom/xap/fcu/hdgtrk");

        }
    }
}

float checkJarA320NeoCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findJarA320NeoDataRefs();

    // come back in 5sec
    return 5.0;
}
