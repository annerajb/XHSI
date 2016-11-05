
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


#include "commands.h"

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
XPLMDataRef x737_lvlchange;

// x737/systems/eec/...
XPLMDataRef x737_N1_phase;
XPLMDataRef x737_N1_limit_eng1;
XPLMDataRef x737_N1_limit_eng2;

// x737/systems/electrics/...
XPLMDataRef x737_stby_pwr;

//mcp set
XPLMDataRef x737_MCPSPD_spd;
XPLMDataRef x737_HDG_magnhdg;
XPLMDataRef x737_ALTHLD_baroalt;
XPLMDataRef x737_VS_vvi;

XPLMDataRef x737_left_fixed_land_light_switch;
XPLMDataRef x737_right_fixed_land_light_switch;
XPLMDataRef x737_left_retr_land_light_switch;
XPLMDataRef x737_rigth_retr_land_light_switch;
XPLMDataRef x737_taxi_light_switch;
XPLMDataRef x737_left_turnoff_light_switch;
XPLMDataRef x737_right_turnoff_light_switch;
XPLMDataRef x737_position_light_switch;
XPLMDataRef x737_beacon_light_switch;

// EFIS
XPLMDataRef x737_efis0_nd_range_enum;
XPLMDataRef x737_efis0_FPV;
XPLMDataRef x737_efis0_MTR;
XPLMDataRef x737_efis0_TFC;
XPLMDataRef x737_efis0_CTR;
XPLMDataRef x737_efis0_WXR;
XPLMDataRef x737_efis0_STA;
XPLMDataRef x737_efis0_WPT;
XPLMDataRef x737_efis0_ARPT;
XPLMDataRef x737_efis0_DATA;
XPLMDataRef x737_efis0_POS;
XPLMDataRef x737_efis0_TERR;
XPLMDataRef x737_efis0_DH_source;
XPLMDataRef x737_efis0_DH_value;
            
XPLMDataRef x737_efis1_nd_range_enum;
XPLMDataRef x737_efis1_FPV;
XPLMDataRef x737_efis1_MTR;
XPLMDataRef x737_efis1_TFC;
XPLMDataRef x737_efis1_CTR;
XPLMDataRef x737_efis1_WXR;
XPLMDataRef x737_efis1_STA;
XPLMDataRef x737_efis1_WPT;
XPLMDataRef x737_efis1_ARPT;
XPLMDataRef x737_efis1_DATA;
XPLMDataRef x737_efis1_POS;
XPLMDataRef x737_efis1_TERR;
XPLMDataRef x737_efis1_DH_source;
XPLMDataRef x737_efis1_DH_value;

            
int x737_ready = 0;


void findX737DataRefs(void) {

	x737_plugin_status = XPLMFindDataRef("x737/systems/afds/plugin_status");

	if ( ( x737_plugin_status == NULL ) || ( XPLMGetDatai(x737_plugin_status) == 0 ) ) {

        x737_ready = 0;

    } else {

        if ( x737_ready == 0 ) {

            x737_ready = 1;

            XPLMDebugString("XHSI: using EADT x737 DataRefs\n");

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
            x737_lvlchange = XPLMFindDataRef("x737/systems/afds/LVLCHG");

            x737_N1_phase = XPLMFindDataRef("x737/systems/eec/N1_phase");
            x737_N1_limit_eng1 = XPLMFindDataRef("x737/systems/eec/N1_limit_eng1");
            x737_N1_limit_eng2 = XPLMFindDataRef("x737/systems/eec/N1_limit_eng2");

            x737_stby_pwr = XPLMFindDataRef("x737/systems/electrics/stbyPwrAuto");
            x737_PFD_pwr = XPLMFindDataRef("x737/systems/PFD/PFD_A_powered");


            //mcp and lights
            x737_MCPSPD_spd = XPLMFindDataRef("x737/systems/athr/MCPSPD_spd");
            x737_HDG_magnhdg = XPLMFindDataRef("x737/systems/afds/HDG_magnhdg");
            x737_ALTHLD_baroalt = XPLMFindDataRef("x737/systems/afds/ALTHLD_baroalt");
            x737_VS_vvi = XPLMFindDataRef("x737/systems/afds/VS_vvi");

            x737_cmda_toggle = XPLMFindCommand("x737/mcp/CMDA_TOGGLE");
            x737_mcpspd_toggle = XPLMFindCommand("x737/mcp/MCPSPD_MODE_TOGGLE");
            x737_lvlchange_toggle = XPLMFindCommand("x737/mcp/LVLCHANGE_TOGGLE");
            x737_hdgsel_toggle = XPLMFindCommand("x737/mcp/HDGSEL_TOGGLE");
            x737_lnav_toggle = XPLMFindCommand("x737/mcp/LNAV_TOGGLE");
            x737_vorloc_toggle = XPLMFindCommand("x737/mcp/VORLOC_TOGGLE");
            x737_app_toggle = XPLMFindCommand("x737/mcp/APP_TOGGLE");
            x737_althld_toggle = XPLMFindCommand("x737/mcp/ALTHLD_TOGGLE");
            x737_vs_toggle = XPLMFindCommand("x737/mcp/VS_TOGGLE");

            x737_left_fixed_land_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/leftFixedLanLtSwitch");
            x737_right_fixed_land_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/rightFixedLanLtSwitch");
            x737_left_retr_land_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/leftRetrLanLtSwitch");
            x737_rigth_retr_land_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/rightRetrLanLtSwitch");
            x737_taxi_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/taxiLightsSwitch");
            x737_left_turnoff_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/leftRunwayTurnoffSwitch");
            x737_right_turnoff_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/rightRunwayTurnoffSwitch");
            x737_position_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/positionLightSwitch");
            x737_beacon_light_switch = XPLMFindDataRef("x737/systems/exteriorLights/beaconLightSwitch");

            // EFIS
            x737_efis0_nd_range_enum = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/ND_RANGE_ENUM");
            x737_efis0_FPV = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/FPV_on");
            x737_efis0_MTR = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/MTR_on");
            x737_efis0_TFC = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/TFC_on");
            x737_efis0_CTR = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/CTR_on");
            x737_efis0_WXR = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/WXR_on");
            x737_efis0_STA = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/STA_on");
            x737_efis0_WPT = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/WPT_on");
            x737_efis0_ARPT = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/ARPT_on");
            x737_efis0_DATA = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/DATA_on");
            x737_efis0_POS = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/POS_on");
            x737_efis0_TERR = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/TERR_on");
            x737_efis0_DH_source = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/DH_source");
            x737_efis0_DH_value = XPLMFindDataRef("x737/cockpit/EFISCTRL_0/DH_value");

            x737_efis1_nd_range_enum = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/ND_RANGE_ENUM");
            x737_efis1_FPV = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/FPV_on");
            x737_efis1_MTR = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/MTR_on");
            x737_efis1_TFC = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/TFC_on");
            x737_efis1_CTR = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/CTR_on");
            x737_efis1_WXR = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/WXR_on");
            x737_efis1_STA = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/STA_on");
            x737_efis1_WPT = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/WPT_on");
            x737_efis1_ARPT = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/ARPT_on");
            x737_efis1_DATA = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/DATA_on");
            x737_efis1_POS = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/POS_on");
            x737_efis1_TERR = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/TERR_on");
            x737_efis1_DH_source = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/DH_source");
            x737_efis1_DH_value = XPLMFindDataRef("x737/cockpit/EFISCTRL_1/DH_value");
            
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

