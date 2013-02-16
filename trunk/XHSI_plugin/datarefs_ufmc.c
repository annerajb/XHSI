
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



// DataRefs for the UFMC / x737FMC

XPLMDataRef ufmc_plugin_status;

XPLMDataRef ufmc_v1;
XPLMDataRef ufmc_vr;
XPLMDataRef ufmc_v2;
XPLMDataRef ufmc_vref;
XPLMDataRef ufmc_n1_1;
XPLMDataRef ufmc_n1_2;
XPLMDataRef ufmc_n1_3;
XPLMDataRef ufmc_n1_4;




int ufmc_ready = 0;


void findUFMCDataRefs(void) {

	ufmc_plugin_status = XPLMFindDataRef("FJCC/UFMC/PRESENT");

	if ( ( ufmc_plugin_status == NULL ) || ( XPLMGetDataf(ufmc_plugin_status) == 0.0 ) ) {

        ufmc_ready = 0;

    } else {

        if ( ufmc_ready == 0 ) {

            ufmc_ready = 1;

            XPLMDebugString("XHSI: registering FJCC UFMC/X737FMC DataRefs\n");

            ufmc_v1 = XPLMFindDataRef("FJCC/UFMC/V1");
            ufmc_vr = XPLMFindDataRef("FJCC/UFMC/Vr");
            ufmc_v2 = XPLMFindDataRef("FJCC/UFMC/V2");
            ufmc_vref = XPLMFindDataRef("FJCC/UFMC/VREF");

            ufmc_n1_1 = XPLMFindDataRef("FJCC/Engine/N1_1");
            ufmc_n1_2 = XPLMFindDataRef("FJCC/Engine/N1_2");
            ufmc_n1_3 = XPLMFindDataRef("FJCC/Engine/N1_3");
            ufmc_n1_4 = XPLMFindDataRef("FJCC/Engine/N1_4");

//            ufmc_ = XPLMFindDataRef("");
//            ufmc_ = XPLMFindDataRef("");

        }

    }

}


float checkUFMCCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findUFMCDataRefs();

    // come back in 5sec
    return 5.0;

}

