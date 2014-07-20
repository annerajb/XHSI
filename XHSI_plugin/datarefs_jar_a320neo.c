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

// FCU
XPLMDataRef jar_a320_neo_baro_hpa;
XPLMDataRef jar_a320_neo_fcu_hdg_trk;
XPLMDataRef jar_a320_neo_fcu_metric_alt;
XPLMDataRef jar_a320_neo_vs_dashed;
XPLMDataRef jar_a320_neo_hdg_dashed;
XPLMDataRef jar_a320_neo_hdg_managed;
XPLMDataRef jar_a320_neo_lvlch_dot;
XPLMDataRef jar_a320_neo_spd_managed;
XPLMDataRef jar_a320_neo_alt_managed;

// Autopilot and FD
XPLMDataRef jar_a320_neo_ap_phase;
XPLMDataRef jar_a320_neo_ap1;
XPLMDataRef jar_a320_neo_ap2;
XPLMDataRef jar_a320_neo_ils;
XPLMDataRef jar_a320_neo_fd;

// Vertical and horizontal modes
XPLMDataRef jar_a320_neo_com_mode;
XPLMDataRef jar_a320_neo_vert_mode;
XPLMDataRef jar_a320_neo_vert_wait;
XPLMDataRef jar_a320_neo_lat_mode;
XPLMDataRef jar_a320_neo_lat_wait;

// Landing capabilities
XPLMDataRef jar_a320_neo_fma_cat_mode;
XPLMDataRef jar_a320_neo_fma_dual_mode;
XPLMDataRef jar_a320_neo_fma_dh_mode;
XPLMDataRef jar_a320_neo_fma_mda_alt;
XPLMDataRef jar_a320_neo_fma_dh_alt;

// Approach
XPLMDataRef jar_a320_neo_ap_appr_type;
XPLMDataRef jar_a320_neo_ap_appr_illuminated;
XPLMDataRef jar_a320_neo_ap_loc_illuminated;

// A/THR
XPLMDataRef jar_a320_neo_athr_mode;
XPLMDataRef jar_a320_neo_thr_mode;
XPLMDataRef jar_a320_neo_flex_t;

// Brakes
XPLMDataRef jar_a320_neo_autobrake_low;
XPLMDataRef jar_a320_neo_autobrake_med;
XPLMDataRef jar_a320_neo_autobrake_max;

// FMS
XPLMDataRef jar_a320_neo_fms_tr_alt;

// V-Speeds
XPLMDataRef jar_a320_neo_vls;
XPLMDataRef jar_a320_neo_vamax;
XPLMDataRef jar_a320_neo_vaprot;
XPLMDataRef jar_a320_neo_vmax;
XPLMDataRef jar_a320_neo_v1;
XPLMDataRef jar_a320_neo_vr;
XPLMDataRef jar_a320_neo_vgrdot;

// EFIS
XPLMDataRef jar_a320_neo_nd_mode;

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

            // Autopilot and FD
            jar_a320_neo_ap_phase =  XPLMFindDataRef("sim/custom/xap/fly_phase");
            jar_a320_neo_ap1 = XPLMFindDataRef("sim/custom/xap/fcu/ap1");
            jar_a320_neo_ap2 = XPLMFindDataRef("sim/custom/xap/fcu/ap2");
            jar_a320_neo_ils = XPLMFindDataRef("sim/custom/xap/fcu/ils");
            jar_a320_neo_fd = XPLMFindDataRef("sim/custom/xap/fcu/fd");

            // Vertical and horizontal modes
            jar_a320_neo_com_mode = XPLMFindDataRef("sim/custom/xap/ap/common_mode");
            jar_a320_neo_vert_mode = XPLMFindDataRef("sim/custom/xap/ap/vert_mode");
            jar_a320_neo_vert_wait = XPLMFindDataRef("sim/custom/xap/ap/vert_wait");
            jar_a320_neo_lat_mode = XPLMFindDataRef("sim/custom/xap/ap/lat_mode");
            jar_a320_neo_lat_wait = XPLMFindDataRef("sim/custom/xap/ap/lat_wait");

            // FCU
            jar_a320_neo_baro_hpa = XPLMFindDataRef("sim/custom/xap/fcu/baro_ishpa");
            jar_a320_neo_fcu_hdg_trk = XPLMFindDataRef("sim/custom/xap/fcu/hdgtrk");
            jar_a320_neo_fcu_metric_alt = XPLMFindDataRef("sim/custom/xap/fcu/metric_alt");
            jar_a320_neo_vs_dashed = XPLMFindDataRef("sim/custom/xap/fcu/vvi_dash_view");
            jar_a320_neo_hdg_dashed = XPLMFindDataRef("sim/custom/xap/fcu/hdgtrk/hdg_dash");
            jar_a320_neo_hdg_managed = XPLMFindDataRef("sim/custom/xap/fcu/hdgtrk/hdg_ballon");
            jar_a320_neo_lvlch_dot = XPLMFindDataRef("sim/custom/xap/fcu/lvlch_dot");
            jar_a320_neo_spd_managed = XPLMFindDataRef("sim/custom/xap/ap/spdmanaged");
            // jar_a320_neo_alt_managed = XPLMFindDataRef("sim/custom/xap/");

            // Landing capabilities
            jar_a320_neo_fma_cat_mode = XPLMFindDataRef("sim/custom/xap/fma/cat_mode");
            jar_a320_neo_fma_dual_mode = XPLMFindDataRef("sim/custom/xap/fma/dual_mode");
            jar_a320_neo_fma_dh_mode = XPLMFindDataRef("sim/custom/xap/fma/dh_mode");
            jar_a320_neo_fma_mda_alt = XPLMFindDataRef("sim/custom/xap/alt/mda_alt");
            jar_a320_neo_fma_dh_alt = XPLMFindDataRef("sim/custom/xap/alt/dh_alt");

            // Approach
            jar_a320_neo_ap_appr_type = XPLMFindDataRef("sim/custom/xap/ap/appr_type");
            jar_a320_neo_ap_appr_illuminated = XPLMFindDataRef("sim/custom/xap/fcu/appr_bat");
            jar_a320_neo_ap_loc_illuminated = XPLMFindDataRef("sim/custom/xap/fcu/loc_bat");

            // A/THR
            jar_a320_neo_athr_mode = XPLMFindDataRef("sim/custom/xap/ap/athr_mode");
            jar_a320_neo_thr_mode = XPLMFindDataRef("sim/custom/xap/ap/thr_mode");
            jar_a320_neo_flex_t = XPLMFindDataRef("sim/custom/xap/engines/flex_t");

            // Brakes
            jar_a320_neo_autobrake_low = XPLMFindDataRef("sim/custom/xap/brakes/auto_lo");
            jar_a320_neo_autobrake_med = XPLMFindDataRef("sim/custom/xap/brakes/auto_med");
            jar_a320_neo_autobrake_max = XPLMFindDataRef("sim/custom/xap/brakes/auto_max");

            // EFIS
            jar_a320_neo_nd_mode = XPLMFindDataRef("sim/custom/xap/fcu/nd_mode");

            // FMS
            jar_a320_neo_fms_tr_alt = XPLMFindDataRef("sim/custom/xap/ap/trans_alt");

            // V-Speeds
            jar_a320_neo_vls = XPLMFindDataRef("sim/custom/xap/pfd/vls_knots");
            jar_a320_neo_vamax = XPLMFindDataRef("sim/custom/xap/pfd/vamax_knots");
            jar_a320_neo_vaprot = XPLMFindDataRef("sim/custom/xap/pfd/vaprot_knots");
            jar_a320_neo_vmax = XPLMFindDataRef("sim/custom/xap/pfd/vmax_knots");
            jar_a320_neo_v1 = XPLMFindDataRef("sim/custom/xap/pfd/v1_knots");
            jar_a320_neo_vr = XPLMFindDataRef("sim/custom/xap/pfd/vr_knots");
            jar_a320_neo_vgrdot = XPLMFindDataRef("sim/custom/xap/pfd/vgrdot_knots");

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
