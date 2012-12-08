
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



// DataRefs for the CL30

XPLMDataRef cl30_plugin_status;

XPLMDataRef cl30_v1;
XPLMDataRef cl30_vr;
XPLMDataRef cl30_v2;
XPLMDataRef cl30_vt;
XPLMDataRef cl30_vga;
XPLMDataRef cl30_vref;
XPLMDataRef cl30_refspds;
XPLMDataRef cl30_mast_warn;
XPLMDataRef cl30_mast_caut;
XPLMDataRef cl30_carets;
XPLMDataRef cl30_to_n1;
XPLMDataRef cl30_clb_n1;




int cl30_ready = 0;


void findCL30DataRefs(void) {

	cl30_plugin_status = XPLMFindDataRef("sim/custom/xap/poweron_p");

	if ( cl30_plugin_status == NULL ) {

        cl30_ready = 0;

    } else {

        if ( cl30_ready == 0 ) {

            cl30_ready = 1;

            cl30_v1 = XPLMFindDataRef("sim/custom/xap/refspds_v1");
            cl30_vr = XPLMFindDataRef("sim/custom/xap/refspds_vr");
            cl30_v2 = XPLMFindDataRef("sim/custom/xap/refspds_v2");
            cl30_vt = XPLMFindDataRef("sim/custom/xap/refspds_vt");
            cl30_vga = XPLMFindDataRef("sim/custom/xap/refspds_vga");
            cl30_vref = XPLMFindDataRef("sim/custom/xap/refspds_vref");

            cl30_refspds = XPLMFindDataRef("sim/custom/xap/refspds");

            cl30_mast_warn = XPLMFindDataRef("sim/custom/xap/mast_warn");
            cl30_mast_caut = XPLMFindDataRef("sim/custom/xap/mast_caut");

            cl30_carets = XPLMFindDataRef("sim/custom/xap/carets");
            cl30_to_n1 = XPLMFindDataRef("sim/custom/xap/TO_n1");
            cl30_clb_n1 = XPLMFindDataRef("sim/custom/xap/CLB_n1");

//            cl30_ = XPLMFindDataRef("");
//            cl30_ = XPLMFindDataRef("");

        }

    }

}


float checkCL30Callback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findCL30DataRefs();

    // come back in 5sec
    return 5.0;

}

