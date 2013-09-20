
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



// DataRefs for the X737

XPLMDataRef x737_plugin_status;

// x737/systems/afds/...
XPLMDataRef x737_fdA_status;
XPLMDataRef x737_fdB_status;
XPLMDataRef x737_CMD_A;
XPLMDataRef x737_CMD_B;
XPLMDataRef x737_A_PITCH;
XPLMDataRef x737_B_PITCH;
XPLMDataRef x737_A_ROLL;
XPLMDataRef x737_B_ROLL;

// x737/systems/PFD/...
XPLMDataRef x737_MCPSPD_mode;
XPLMDataRef x737_FMCSPD_mode;
XPLMDataRef x737_RETARD_mode;
XPLMDataRef x737_THRHLD_mode;
XPLMDataRef x737_LNAVARMED_mode;
XPLMDataRef x737_VORLOCARMED_mode;
XPLMDataRef x737_PITCHSPD_mode;
XPLMDataRef x737_ALTHLD_mode;
XPLMDataRef x737_VSARMED_mode;
XPLMDataRef x737_VS_mode;
XPLMDataRef x737_VNAVALT_mode;
XPLMDataRef x737_VNAVPATH_mode;
XPLMDataRef x737_VNAVSPD_mode;
XPLMDataRef x737_GSARMED_mode;
XPLMDataRef x737_GS_mode;
XPLMDataRef x737_FLAREARMED_mode;
XPLMDataRef x737_FLARE_mode;
XPLMDataRef x737_TOGA_mode;
XPLMDataRef x737_LNAV_mode;
XPLMDataRef x737_HDG_mode;
XPLMDataRef x737_VORLOC_mode;
XPLMDataRef x737_N1_mode;
XPLMDataRef x737_PFD_pwr;

// x737/systems/athr/...
XPLMDataRef x737_ATHR_armed;

// x737/systems/eec/...
XPLMDataRef x737_N1_phase;
XPLMDataRef x737_N1_limit_eng1;
XPLMDataRef x737_N1_limit_eng2;

// x737/systems/electrics/...
XPLMDataRef x737_stby_pwr;



int x737_ready = 0;


void findX737DataRefs(void) {

	x737_plugin_status = XPLMFindDataRef("x737/systems/afds/plugin_status");

	if ( ( x737_plugin_status == NULL ) || ( XPLMGetDatai(x737_plugin_status) == 0 ) ) {

        x737_ready = 0;

    } else {

        if ( x737_ready == 0 ) {

            x737_ready = 1;

            XPLMDebugString("XHSI: finding EADT x737 DataRefs\n");

            x737_fdA_status = XPLMFindDataRef("x737/systems/afds/fdA_status");
            x737_fdB_status = XPLMFindDataRef("x737/systems/afds/fdB_status");
            x737_CMD_A = XPLMFindDataRef("x737/systems/afds/CMD_A");
            x737_CMD_B = XPLMFindDataRef("x737/systems/afds/CMD_B");

            x737_A_PITCH = XPLMFindDataRef("x737/systems/afds/AP_A_pitch");
            x737_B_PITCH = XPLMFindDataRef("x737/systems/afds/AP_B_pitch");
            x737_A_ROLL = XPLMFindDataRef("x737/systems/afds/AP_A_roll");
            x737_B_ROLL = XPLMFindDataRef("x737/systems/afds/AP_B_roll");

            x737_MCPSPD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_MCPSPD_mode_on");
            x737_FMCSPD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_FMCSPD_mode_on");
            x737_RETARD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_RETARD_mode_on");
            x737_THRHLD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_THRHLD_mode_on");
            x737_LNAVARMED_mode = XPLMFindDataRef("x737/systems/PFD/PFD_LNAVARMED_mode_on");
            x737_VORLOCARMED_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VORLOCARMED_mode_on");
            x737_PITCHSPD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_PITCHSPD_mode_on");
            x737_ALTHLD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_ALTHLD_mode_on");
            x737_VSARMED_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VSARMED_mode_on");
            x737_VS_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VS_mode_on");
            x737_VNAVALT_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VNAVALT_mode_on");
            x737_VNAVPATH_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VNAVPATH_mode_on");
            x737_VNAVSPD_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VNAVSPD_mode_on");
            x737_GSARMED_mode = XPLMFindDataRef("x737/systems/PFD/PFD_GSARMED_mode_on");
            x737_GS_mode = XPLMFindDataRef("x737/systems/PFD/PFD_GS_mode_on");
            x737_FLAREARMED_mode = XPLMFindDataRef("x737/systems/PFD/PFD_FLAREARMED_mode_on");
            x737_FLARE_mode = XPLMFindDataRef("x737/systems/PFD/PFD_FLARE_mode_on");
            x737_TOGA_mode = XPLMFindDataRef("x737/systems/PFD/PFD_TOGA_mode_on");
            x737_LNAV_mode = XPLMFindDataRef("x737/systems/PFD/PFD_LNAV_mode_on");
            x737_HDG_mode = XPLMFindDataRef("x737/systems/PFD/PFD_HDG_mode_on");
            x737_VORLOC_mode = XPLMFindDataRef("x737/systems/PFD/PFD_VORLOC_mode_on");
            x737_N1_mode = XPLMFindDataRef("x737/systems/PFD/PFD_N1_mode_on");

            x737_ATHR_armed = XPLMFindDataRef("x737/systems/athr/athr_armed");

            x737_N1_phase = XPLMFindDataRef("x737/systems/eec/N1_phase");
            x737_N1_limit_eng1 = XPLMFindDataRef("x737/systems/eec/N1_limit_eng1");
            x737_N1_limit_eng2 = XPLMFindDataRef("x737/systems/eec/N1_limit_eng2");

            x737_stby_pwr = XPLMFindDataRef("x737/systems/electrics/stbyPwrAuto");
            x737_PFD_pwr = XPLMFindDataRef("x737/systems/PFD/PFD_A_powered");

        }

    }

}


float checkX737Callback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findX737DataRefs();

    // come back in 5sec
    return 5.0;

}

