/*
 * datarefs_xjoymap.c
 *
 *  Created on: 22 sept. 2016
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
#include "ids.h"


/* DataRefs for xjoymap with Dual Command
 * Used for side stick priority mecanism
 * Extracted from xjoymap-dual Python source
 *
 * CMD_PREFIX = 'xjoymap/'       # prefix for new commands
 * CMD_STICK_CAPT = 'StickCapt'  # Stick priority Captain
 * CMD_STICK_BOTH = 'StickDual'  # Stick priority Dual
 * CMD_STICK_FO = 'StickFO'      # Stick priority FO
 * REF_STICK_PRIORITY = 'StickPriority' # 0=Both, 1=Capt, 2=FO
 * REF_DUAL_INPUT = 'DualInput' # 0=No, 1=Yes
 */

XPLMDataRef xjoymap_side_stick_priority;
XPLMDataRef xjoymap_dual_input;

XPLMCommandRef xjoymap_stick_capt;
XPLMCommandRef xjoymap_stick_dual;
XPLMCommandRef xjoymap_stick_fo;

int xjoymap_ready = 0;


void findXjoymapDataRefs(void) {

    xjoymap_side_stick_priority = XPLMFindDataRef("xjoymap/StickPriority");

	if ( ( xjoymap_side_stick_priority == NULL ) || ( XPLMGetDatai(xjoymap_side_stick_priority) < 0 ) ) {

		xjoymap_ready = 0;

    } else {
        if ( xjoymap_ready == 0 ) {

        	xjoymap_ready = 1;

            XPLMDebugString("XHSI: using xjoymap DataRefs\n");

            xjoymap_dual_input = XPLMFindDataRef("xjoymap/DualInput");;

            xjoymap_stick_capt = XPLMFindCommand("xjoymap/main/StickCapt");
            xjoymap_stick_dual = XPLMFindCommand("xjoymap/main/StickDual");
            xjoymap_stick_fo = XPLMFindCommand("xjoymap/main/StickFo");
        }
    }
}


float checkXjoymapCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findXjoymapDataRefs();

    // come back in 5sec
    return 5.0;
}
