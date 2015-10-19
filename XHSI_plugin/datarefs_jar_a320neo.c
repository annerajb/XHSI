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
#include "jar_a320neo_msg.h"
#include "datarefs_jar_a320neo.h"
#include "ids.h"

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

// Baro
XPLMDataRef jar_a320_neo_baro_flash;

// Autopilot and FD
XPLMDataRef jar_a320_neo_ap_phase;
XPLMDataRef jar_a320_neo_ap1;
XPLMDataRef jar_a320_neo_ap2;
XPLMDataRef jar_a320_neo_ils;
XPLMDataRef jar_a320_neo_fd;

// FD Bars
XPLMDataRef jar_a320_neo_fd_h_show; // globalPropertyi("sim/custom/xap/pfd/myfd_h_show"))
XPLMDataRef jar_a320_neo_fd_pitch; // globalPropertyf("sim/cockpit2/autopilot/flight_director_pitch_deg"))
XPLMDataRef jar_a320_neo_fd_v_show; // globalPropertyi("sim/custom/xap/pfd/myfd_v_show"))
XPLMDataRef jar_a320_neo_fd_roll; // globalPropertyf("sim/cockpit2/autopilot/flight_director_roll_deg"))
XPLMDataRef jar_a320_neo_fd_y_show; // globalPropertyi("sim/custom/xap/pfd/myfd_y_show"))
XPLMDataRef jar_a320_neo_fd_yaw_dot; // globalPropertyf("sim/cockpit2/radios/indicators/nav1_hdef_dots_pilot"))

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
XPLMDataRef jar_a320_neo_yoyo_on;
XPLMDataRef jar_a320_neo_vdev;

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

// MCDU
XPLMDataRef jar_a320_mcdu_click[JAR_A320_MAX_MCDU_KEYS];

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

            XPLMDebugString("XHSI: using JarDesign A320 Neo DataRefs\n");

            // Autopilot and FD
            jar_a320_neo_ap_phase =  XPLMFindDataRef("sim/custom/xap/fly_phase");
            jar_a320_neo_ap1 = XPLMFindDataRef("sim/custom/xap/fcu/ap1");
            jar_a320_neo_ap2 = XPLMFindDataRef("sim/custom/xap/fcu/ap2");
            jar_a320_neo_ils = XPLMFindDataRef("sim/custom/xap/fcu/ils");
            jar_a320_neo_fd = XPLMFindDataRef("sim/custom/xap/fcu/fd");

            // FD Bars
            jar_a320_neo_fd_h_show = XPLMFindDataRef("sim/custom/xap/pfd/myfd_h_show");
            jar_a320_neo_fd_pitch = XPLMFindDataRef("sim/cockpit2/autopilot/flight_director_pitch_deg");
            jar_a320_neo_fd_v_show = XPLMFindDataRef("sim/custom/xap/pfd/myfd_v_show");
            jar_a320_neo_fd_roll = XPLMFindDataRef("sim/cockpit2/autopilot/flight_director_roll_deg");
            jar_a320_neo_fd_y_show = XPLMFindDataRef("sim/custom/xap/pfd/myfd_y_show");
            jar_a320_neo_fd_yaw_dot = XPLMFindDataRef("sim/cockpit2/radios/indicators/nav1_hdef_dots_pilot");

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
            // Baro
            jar_a320_neo_baro_flash = XPLMFindDataRef("sim/custom/xap/pfd/baro_flash");

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
            jar_a320_neo_yoyo_on = XPLMFindDataRef("sim/custom/yoyo_on");
            jar_a320_neo_vdev = XPLMFindDataRef("sim/custom/vdev");


            // V-Speeds
            jar_a320_neo_vls = XPLMFindDataRef("sim/custom/xap/pfd/vls_knots");
            jar_a320_neo_vamax = XPLMFindDataRef("sim/custom/xap/pfd/vamax_knots");
            jar_a320_neo_vaprot = XPLMFindDataRef("sim/custom/xap/pfd/vaprot_knots");
            jar_a320_neo_vmax = XPLMFindDataRef("sim/custom/xap/pfd/vmax_knots");
            jar_a320_neo_v1 = XPLMFindDataRef("sim/custom/xap/pfd/v1_knots");
            jar_a320_neo_vr = XPLMFindDataRef("sim/custom/xap/pfd/vr_knots");
            jar_a320_neo_vgrdot = XPLMFindDataRef("sim/custom/xap/pfd/vgrdot_knots");

            // MCDU click

            jar_a320_mcdu_click[0] = XPLMFindDataRef("sim/custom/xap/mcdu/click_mcdumenu");
            jar_a320_mcdu_click[1] = XPLMFindDataRef("sim/custom/xap/mcdu/click_data");
            jar_a320_mcdu_click[2] = XPLMFindDataRef("sim/custom/xap/mcdu/click_blank");
            jar_a320_mcdu_click[3] = XPLMFindDataRef("sim/custom/xap/mcdu/click_fpln");
            jar_a320_mcdu_click[4] = XPLMFindDataRef("sim/custom/xap/mcdu/click_airp");
            jar_a320_mcdu_click[5] = XPLMFindDataRef("sim/custom/xap/mcdu/click_fuel");
            jar_a320_mcdu_click[6] = XPLMFindDataRef("sim/custom/xap/mcdu/click_left");
            jar_a320_mcdu_click[7] = XPLMFindDataRef("sim/custom/xap/mcdu/click_down");
            jar_a320_mcdu_click[8] = XPLMFindDataRef("sim/custom/xap/mcdu/click_right");
            jar_a320_mcdu_click[9] = XPLMFindDataRef("sim/custom/xap/mcdu/click_up");
            jar_a320_mcdu_click[10] = XPLMFindDataRef("sim/custom/xap/mcdu/click_int");
            jar_a320_mcdu_click[11] = XPLMFindDataRef("sim/custom/xap/mcdu/click_prog");
            jar_a320_mcdu_click[12] = XPLMFindDataRef("sim/custom/xap/mcdu/click_dir");
            jar_a320_mcdu_click[13] = XPLMFindDataRef("sim/custom/xap/mcdu/click_radnav");
            jar_a320_mcdu_click[14] = XPLMFindDataRef("sim/custom/xap/mcdu/click_perf");
            jar_a320_mcdu_click[15] = XPLMFindDataRef("sim/custom/xap/mcdu/click_dot");
            jar_a320_mcdu_click[16] = XPLMFindDataRef("sim/custom/xap/mcdu/click_slash");
            jar_a320_mcdu_click[17] = XPLMFindDataRef("sim/custom/xap/mcdu/click_plusmin");
            jar_a320_mcdu_click[18] = XPLMFindDataRef("sim/custom/xap/mcdu/click_sp");
            jar_a320_mcdu_click[19] = XPLMFindDataRef("sim/custom/xap/mcdu/click_ovfy");
            jar_a320_mcdu_click[20] = XPLMFindDataRef("sim/custom/xap/mcdu/click_clr");
            jar_a320_mcdu_click[21] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l1");
            jar_a320_mcdu_click[22] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l2");
            jar_a320_mcdu_click[23] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l3");
            jar_a320_mcdu_click[24] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l4");
            jar_a320_mcdu_click[25] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l5");
            jar_a320_mcdu_click[26] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l6");
            jar_a320_mcdu_click[27] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r1");
            jar_a320_mcdu_click[28] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r2");
            jar_a320_mcdu_click[29] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r3");
            jar_a320_mcdu_click[30] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r4");
            jar_a320_mcdu_click[31] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r5");
            jar_a320_mcdu_click[32] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r6");

            jar_a320_mcdu_click[33] = XPLMFindDataRef("sim/custom/xap/mcdu/click_0");
            jar_a320_mcdu_click[34] = XPLMFindDataRef("sim/custom/xap/mcdu/click_1");
            jar_a320_mcdu_click[35] = XPLMFindDataRef("sim/custom/xap/mcdu/click_2");
            jar_a320_mcdu_click[36] = XPLMFindDataRef("sim/custom/xap/mcdu/click_3");
            jar_a320_mcdu_click[37] = XPLMFindDataRef("sim/custom/xap/mcdu/click_4");
            jar_a320_mcdu_click[38] = XPLMFindDataRef("sim/custom/xap/mcdu/click_5");
            jar_a320_mcdu_click[39] = XPLMFindDataRef("sim/custom/xap/mcdu/click_6");
            jar_a320_mcdu_click[40] = XPLMFindDataRef("sim/custom/xap/mcdu/click_7");
            jar_a320_mcdu_click[41] = XPLMFindDataRef("sim/custom/xap/mcdu/click_8");
            jar_a320_mcdu_click[42] = XPLMFindDataRef("sim/custom/xap/mcdu/click_9");
            jar_a320_mcdu_click[43] = XPLMFindDataRef("sim/custom/xap/mcdu/click_a");
            jar_a320_mcdu_click[44] = XPLMFindDataRef("sim/custom/xap/mcdu/click_b");
            jar_a320_mcdu_click[45] = XPLMFindDataRef("sim/custom/xap/mcdu/click_c");
            jar_a320_mcdu_click[46] = XPLMFindDataRef("sim/custom/xap/mcdu/click_d");
            jar_a320_mcdu_click[47] = XPLMFindDataRef("sim/custom/xap/mcdu/click_e");
            jar_a320_mcdu_click[48] = XPLMFindDataRef("sim/custom/xap/mcdu/click_f");
            jar_a320_mcdu_click[49] = XPLMFindDataRef("sim/custom/xap/mcdu/click_g");
            jar_a320_mcdu_click[50] = XPLMFindDataRef("sim/custom/xap/mcdu/click_h");
            jar_a320_mcdu_click[51] = XPLMFindDataRef("sim/custom/xap/mcdu/click_i");
            jar_a320_mcdu_click[52] = XPLMFindDataRef("sim/custom/xap/mcdu/click_j");
            jar_a320_mcdu_click[53] = XPLMFindDataRef("sim/custom/xap/mcdu/click_k");
            jar_a320_mcdu_click[54] = XPLMFindDataRef("sim/custom/xap/mcdu/click_l");
            jar_a320_mcdu_click[55] = XPLMFindDataRef("sim/custom/xap/mcdu/click_m");
            jar_a320_mcdu_click[56] = XPLMFindDataRef("sim/custom/xap/mcdu/click_n");
            jar_a320_mcdu_click[57] = XPLMFindDataRef("sim/custom/xap/mcdu/click_o");
            jar_a320_mcdu_click[58] = XPLMFindDataRef("sim/custom/xap/mcdu/click_p");
            jar_a320_mcdu_click[59] = XPLMFindDataRef("sim/custom/xap/mcdu/click_q");
            jar_a320_mcdu_click[60] = XPLMFindDataRef("sim/custom/xap/mcdu/click_r");
            jar_a320_mcdu_click[61] = XPLMFindDataRef("sim/custom/xap/mcdu/click_s");
            jar_a320_mcdu_click[62] = XPLMFindDataRef("sim/custom/xap/mcdu/click_t");
            jar_a320_mcdu_click[63] = XPLMFindDataRef("sim/custom/xap/mcdu/click_u");
            jar_a320_mcdu_click[64] = XPLMFindDataRef("sim/custom/xap/mcdu/click_v");
            jar_a320_mcdu_click[65] = XPLMFindDataRef("sim/custom/xap/mcdu/click_w");
            jar_a320_mcdu_click[66] = XPLMFindDataRef("sim/custom/xap/mcdu/click_x");
            jar_a320_mcdu_click[67] = XPLMFindDataRef("sim/custom/xap/mcdu/click_y");
            jar_a320_mcdu_click[68] = XPLMFindDataRef("sim/custom/xap/mcdu/click_z");

            findJar_a320MsgDataRefs();

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


void writeJarA320neoDataRef(int id, float value) {

    char info_string[80];
    sprintf(info_string, "XHSI: received JarDesign A320 data: ID=%d  VALUE=%f\n", id, value);
    XPLMDebugString(info_string);

    switch (id) {
		case JAR_A320NEO_MCDU_CLICK :
			if ((value >= 0) && (value <= 68)) XPLMSetDatai(jar_a320_mcdu_click[(int)value], 1);
			break;
    }
}

