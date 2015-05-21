
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>


#define XPLM200 1

#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMPlugin.h"


// DataRefs for the PilotEdge plugin
XPLMDataRef pilotedge_rx_status;
XPLMDataRef pilotedge_tx_status;
XPLMDataRef pilotedge_connected;


// global
int pilotedge_ready = 0;


void findPilotEdgeDataRefs(void) {

    if ( XPLMFindPluginBySignature("com.pilotedge.plugin.xplane") == XPLM_NO_PLUGIN_ID ) {

        pilotedge_ready = 0;

    } else {

        if ( pilotedge_ready == 0 ) {

            pilotedge_ready = 1;

            XPLMDebugString("XHSI: using PilotEdge DataRefs\n");

            pilotedge_rx_status = XPLMFindDataRef("pilotedge/radio/rx_status");
            pilotedge_tx_status = XPLMFindDataRef("pilotedge/radio/tx_status");
            pilotedge_connected = XPLMFindDataRef("pilotedge/status/connected");

        }

    }

}


float checkPilotEdgeCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int	inCounter,
        void *	inRefcon) {

    findPilotEdgeDataRefs();

    // come back in 11sec
    return 11.0;

}

