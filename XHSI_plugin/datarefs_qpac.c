/*
 * datarefs_qpac.c
 *
 *  Created on: 19 févr. 2014
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



// DataRefs for the QPAC AirbusFBW
// Used by QPAC v1.0 v1.1 and v2.0
// Used by Peter Hager / Peter's Aircraft


XPLMDataRef qpac_plugin_status;

// Autopilot
XPLMDataRef qpac_ap1;
XPLMDataRef qpac_ap2;
XPLMDataRef qpac_ap_phase;
XPLMDataRef qpac_ap_vertical_mode;
XPLMDataRef qpac_ap_vertical_armed;
XPLMDataRef qpac_ap_lateral_mode;
XPLMDataRef qpac_ap_lateral_armed;
XPLMDataRef qpac_npa_valid;
XPLMDataRef qpac_npa_no_points;
XPLMDataRef qpac_appr_illuminated;
XPLMDataRef qpac_appr_type;
XPLMDataRef qpac_appr_mda;
XPLMDataRef qpac_alt_is_cstr;
XPLMDataRef qpac_constraint_alt;
// Auto-Thrust and speeds
XPLMDataRef qpac_athr_mode;
XPLMDataRef qpac_athr_mode2;
XPLMDataRef qpac_athr_limited;
XPLMDataRef qpac_thr_lever_mode;
XPLMDataRef qpac_fma_thr_warning;
XPLMDataRef qpac_flex_temp;
XPLMDataRef qpac_presel_crz;
XPLMDataRef qpac_presel_clb;
XPLMDataRef qpac_presel_mach;
// RNAV Deviation
XPLMDataRef qpac_loc_val_capt;
XPLMDataRef qpac_loc_on_capt;
XPLMDataRef qpac_gs_val_capt;
XPLMDataRef qpac_gs_on_capt;
// ILS on NAV3
XPLMDataRef qpac_ils_crs;
XPLMDataRef qpac_ils_crs_dev;
XPLMDataRef qpac_ils_1;
XPLMDataRef qpac_ils_2;
XPLMDataRef qpac_ils_3;
// FD
XPLMDataRef qpac_fd1;
XPLMDataRef qpac_fd2;
XPLMDataRef qpac_fd1_ver_bar;
XPLMDataRef qpac_fd1_hor_bar;
XPLMDataRef qpac_fd2_ver_bar;
XPLMDataRef qpac_fd2_hor_bar;
// Baro
XPLMDataRef qpac_baro_std_capt;
XPLMDataRef qpac_baro_unit_capt;
XPLMDataRef qpac_baro_hide_capt;
// V Speeds
XPLMDataRef qpac_v1_value;
XPLMDataRef qpac_v1;
XPLMDataRef qpac_vr;
XPLMDataRef qpac_vmo;
XPLMDataRef qpac_vls;
XPLMDataRef qpac_vf;
XPLMDataRef qpac_vs;
XPLMDataRef qpac_v_green_dot;
XPLMDataRef qpac_alpha_prot;
XPLMDataRef qpac_alpha_max;
// Failures
XPLMDataRef qpac_capt_hdg_valid;
XPLMDataRef qpac_capt_att_valid;
XPLMDataRef qpac_capt_ias_valid;
XPLMDataRef qpac_capt_alt_valid;
XPLMDataRef qpac_co_hdg_valid;
XPLMDataRef qpac_co_att_valid;
XPLMDataRef qpac_co_ias_valid;
XPLMDataRef qpac_co_alt_valid;

int qpac_ready = 0;


void findQpacDataRefs(void) {
	// For datarefs checks, remove for release
	char msg[200];
	XPLMDataTypeID reftype;

	qpac_plugin_status = XPLMFindDataRef("AirbusFBW/APPhase");

	if ( ( qpac_plugin_status == NULL ) || ( XPLMGetDatai(qpac_plugin_status) < 0 ) ) {

        qpac_ready = 0;

    } else {
        if ( qpac_ready == 0 ) {

            qpac_ready = 1;

            XPLMDebugString("XHSI: finding QPAC AirbusFBW DataRefs\n");

            // Autopilot
            qpac_ap1 = XPLMFindDataRef("AirbusFBW/AP1Engage");
            qpac_ap2 = XPLMFindDataRef("AirbusFBW/AP2Engage");
            qpac_ap_phase = XPLMFindDataRef("AirbusFBW/APPhase");
            qpac_ap_vertical_mode = XPLMFindDataRef("AirbusFBW/APVerticalMode");
            qpac_ap_vertical_armed = XPLMFindDataRef("AirbusFBW/APVerticalArmed");
            qpac_ap_lateral_mode = XPLMFindDataRef("AirbusFBW/APLateralMode");
            qpac_ap_lateral_armed = XPLMFindDataRef("AirbusFBW/APLateralArmed");
            qpac_npa_valid = XPLMFindDataRef("AirbusFBW/NPAValid");
            qpac_npa_no_points = XPLMFindDataRef("AirbusFBW/NPANoPoints");
            qpac_appr_illuminated = XPLMFindDataRef("AirbusFBW/APPRilluminated");
            qpac_appr_type = XPLMFindDataRef("AirbusFBW/ApprType");
            qpac_appr_mda = XPLMFindDataRef("AirbusFBW/ApprMDA");
            qpac_alt_is_cstr = XPLMFindDataRef("AirbusFBW/ALTisCstr");
            qpac_constraint_alt = XPLMFindDataRef("AirbusFBW/ConstraintAlt");
            // Auto-Thrust and speed
            qpac_athr_mode = XPLMFindDataRef("AirbusFBW/ATHRmode");
            qpac_athr_mode2 = XPLMFindDataRef("AirbusFBW/ATHRmode2");
            qpac_athr_limited = XPLMFindDataRef("AirbusFBW/ATHRlimited");
            qpac_thr_lever_mode = XPLMFindDataRef("AirbusFBW/THRLeverMode");
            qpac_fma_thr_warning = XPLMFindDataRef("AirbusFBW/FMATHRWarning");
            qpac_flex_temp = XPLMFindDataRef("AirbusFBW/FlexTemp");
            qpac_presel_crz = XPLMFindDataRef("AirbusFBW/Presel_CRZ");
            qpac_presel_clb = XPLMFindDataRef("AirbusFBW/Presel_CLB");
            qpac_presel_mach = XPLMFindDataRef("AirbusFBW/PreselMach");
            // RNAV Deviation
            qpac_loc_val_capt = XPLMFindDataRef("AirbusFBW/LOCvalCapt");
            qpac_loc_on_capt = XPLMFindDataRef("AirbusFBW/LOConCapt");
            qpac_gs_val_capt = XPLMFindDataRef("AirbusFBW/GSvalCapt");
            qpac_gs_on_capt = XPLMFindDataRef("AirbusFBW/GSonCapt");
            // ILS on NAV3
            qpac_ils_crs = XPLMFindDataRef("AirbusFBW/ILSCrs");
            qpac_ils_crs_dev = XPLMFindDataRef("AirbusFBW/ILSCourseDev");
            qpac_ils_1 = XPLMFindDataRef("AirbusFBW/ILS1");
            qpac_ils_2 = XPLMFindDataRef("AirbusFBW/ILS2");
            qpac_ils_3 = XPLMFindDataRef("AirbusFBW/ILS3");
            // FD
            qpac_fd1 = XPLMFindDataRef("AirbusFBW/FD1Engage");
            qpac_fd2 = XPLMFindDataRef("AirbusFBW/FD2Engage");
            qpac_fd1_ver_bar = XPLMFindDataRef("AirbusFBW/FD1VerBar");
            qpac_fd1_hor_bar = XPLMFindDataRef("AirbusFBW/FD1HorBar");
            qpac_fd2_ver_bar = XPLMFindDataRef("AirbusFBW/FD2VerBar");
            qpac_fd2_hor_bar = XPLMFindDataRef("AirbusFBW/FD2HorBar");
            // Baro
            qpac_baro_std_capt = XPLMFindDataRef("AirbusFBW/BaroStdCapt");
            qpac_baro_unit_capt = XPLMFindDataRef("AirbusFBW/BaroUnitCapt");
            qpac_baro_hide_capt = XPLMFindDataRef("AirbusFBW/HideBaroCapt");
            // V Speeds
            qpac_v1_value = XPLMFindDataRef("AirbusFBW/V1Value");
            qpac_v1 = XPLMFindDataRef("AirbusFBW/V1Capt"); //  AirbusFBW/V1 or AirbusFBW/V1Capt
            qpac_vr = XPLMFindDataRef("AirbusFBW/VR");
            qpac_vmo = XPLMFindDataRef("AirbusFBW/VMO");
            qpac_vls = XPLMFindDataRef("AirbusFBW/VLS");
            qpac_vf = XPLMFindDataRef("AirbusFBW/VF");
            qpac_vs = XPLMFindDataRef("AirbusFBW/VS");
            qpac_v_green_dot = XPLMFindDataRef("AirbusFBW/VGreenDot");
            qpac_alpha_prot = XPLMFindDataRef("AirbusFBW/AlphaProt");
            qpac_alpha_max = XPLMFindDataRef("AirbusFBW/AlphaMax");
            // Failures
            qpac_capt_hdg_valid = XPLMFindDataRef("AirbusFBW/CaptHDGValid");
            qpac_capt_att_valid = XPLMFindDataRef("AirbusFBW/CaptATTValid");
            qpac_capt_ias_valid = XPLMFindDataRef("AirbusFBW/CaptIASValid");
            qpac_capt_alt_valid = XPLMFindDataRef("AirbusFBW/CaptALTValid");
            qpac_co_hdg_valid = XPLMFindDataRef("AirbusFBW/CoHDGValid");
            qpac_co_att_valid = XPLMFindDataRef("AirbusFBW/CoATTValid");
            qpac_co_ias_valid = XPLMFindDataRef("AirbusFBW/CoIASValid");
            qpac_co_alt_valid = XPLMFindDataRef("AirbusFBW/CoALTValid");

            // Some checks with Datarefs
            reftype = XPLMGetDataRefTypes(qpac_appr_mda);
            sprintf(msg, "XHSI: DEBUG AirbusFBW/ApprMDA DataTypeID: %d\n", reftype);
            XPLMDebugString(msg);

        }
    }
}

float checkQpacCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findQpacDataRefs();

    // come back in 5sec
    return 5.0;
}
