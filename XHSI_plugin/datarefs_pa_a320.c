/*
 * datarefs_qpac.c
 *
 *  Created on: 1 may 2014
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

// DataRefs for Peter's Aircraf A320 Airbus Planes
// Used by Peter Hager / Peter's Aircraft


XPLMDataRef pa_a320_plugin_status;
XPLMDataRef pa_a320_baro_hide;
XPLMDataRef pa_a320_fcu_metric_alt;
XPLMDataRef pa_a320_fcu_hdg_trk;
XPLMDataRef pa_a320_ir_capt_avail;
XPLMDataRef pa_a320_pa1_horiz_bar;
XPLMDataRef pa_a320_alt_cruize;
XPLMDataRef pa_a320_fcu_fpa_sel;

int pa_a320_ready = 0;


void findPaA320DataRefs(void) {
	// For datarefs checks, remove for release
	// char msg[200];
	// XPLMDataTypeID reftype;

	pa_a320_plugin_status = XPLMFindDataRef("com/petersaircraft/airbus/ApprType");

	if ( ( pa_a320_plugin_status == NULL ) || ( XPLMGetDatai(pa_a320_plugin_status) < 0 ) ) {

		pa_a320_ready = 0;

    } else {
        if ( pa_a320_ready == 0 ) {

        	pa_a320_ready = 1;

            XPLMDebugString("XHSI: finding Peter Aircraft DataRefs\n");

            pa_a320_baro_hide = XPLMFindDataRef("com/petersaircraft/airbus/QNH_flash");
            pa_a320_fcu_metric_alt = XPLMFindDataRef("com/petersaircraft/airbus/metric_alt_show");
            pa_a320_fcu_hdg_trk = XPLMFindDataRef("com/petersaircraft/airbus/HDG_TRK_mode");
            pa_a320_ir_capt_avail = XPLMFindDataRef("com/petersaircraft/airbus/IR_Capt_avail");
            pa_a320_pa1_horiz_bar = XPLMFindDataRef("com/petersaircraft/airbus/PA1HorizBar");
            pa_a320_alt_cruize = XPLMFindDataRef("com/petersaircraft/airbus/ALT_CRZ_Capture");
            pa_a320_fcu_fpa_sel = XPLMFindDataRef("com/petersaircraft/airbus/FPA_SEL");

        }
    }
}

float checkPaA320Callback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findPaA320DataRefs();

    // come back in 5sec
    return 5.0;
}
