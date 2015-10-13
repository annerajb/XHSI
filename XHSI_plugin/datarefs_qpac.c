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
#include "ids.h"
#include "qpac_msg.h"


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
XPLMDataRef qpac_npa_slope;
XPLMDataRef qpac_appr_illuminated;
XPLMDataRef qpac_loc_illuminated;
XPLMDataRef qpac_appr_type;
XPLMDataRef qpac_appr_mda;
XPLMDataRef qpac_alt_is_cstr;
XPLMDataRef qpac_constraint_alt;
// FCU
XPLMDataRef qpac_fcu_hdg_trk;
XPLMDataRef qpac_fcu_metric_alt;
XPLMDataRef qpac_fcu_vs_dashed;
XPLMDataRef qpac_fcu_spd_dashed;
XPLMDataRef qpac_fcu_spd_managed;
XPLMDataRef qpac_fcu_hdg_dashed;
XPLMDataRef qpac_fcu_hdg_managed;
XPLMDataRef qpac_fcu_alt_managed;
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
XPLMDataRef qpac_thr_rating_type;
XPLMDataRef qpac_thr_rating_n1;
XPLMDataRef qpac_throttle_input;
// ILS Sig and Deviation Capt. and FO
XPLMDataRef qpac_loc_val_capt;
XPLMDataRef qpac_loc_on_capt;
XPLMDataRef qpac_gs_val_capt;
XPLMDataRef qpac_gs_on_capt;
XPLMDataRef qpac_ils_on_capt;
XPLMDataRef qpac_loc_val_fo;
XPLMDataRef qpac_loc_on_fo;
XPLMDataRef qpac_gs_val_fo;
XPLMDataRef qpac_gs_on_fo;
XPLMDataRef qpac_ils_on_fo;
XPLMDataRef qpac_ils_crs;
XPLMDataRef qpac_ils_1;
XPLMDataRef qpac_ils_2;
XPLMDataRef qpac_ils_3;
// FD
XPLMDataRef qpac_fd1;
XPLMDataRef qpac_fd2;
XPLMDataRef qpac_fd1_ver_bar;
XPLMDataRef qpac_fd1_hor_bar;
XPLMDataRef qpac_fd1_yaw_bar;
XPLMDataRef qpac_fd2_ver_bar;
XPLMDataRef qpac_fd2_hor_bar;
XPLMDataRef qpac_fd2_yaw_bar;
// Baro
XPLMDataRef qpac_baro_std_capt;
XPLMDataRef qpac_baro_unit_capt;
XPLMDataRef qpac_baro_hide_capt;
XPLMDataRef qpac_baro_std_fo;
XPLMDataRef qpac_baro_unit_fo;
XPLMDataRef qpac_baro_hide_fo;
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
// EFIS
XPLMDataRef qpac_capt_efis_nd_mode;
XPLMDataRef qpac_co_efis_nd_mode;
XPLMDataRef qpac_capt_efis_nd_range;
XPLMDataRef qpac_co_efis_nd_range;
// Brakes
// 0=OFF, 1=Engaged, 2=DECEL
XPLMDataRef qpac_autobrake_low;
XPLMDataRef qpac_autobrake_med;
XPLMDataRef qpac_autobrake_max;
// Flaps and slats
XPLMDataRef qpac_flaps_request_pos;
XPLMDataRef qpac_slats_request_pos;
// Left and right ailerons
XPLMDataRef qpac_right_aileron_pos;
XPLMDataRef qpac_left_aileron_pos;
// Spoilers
XPLMDataRef qpac_spoilers_array;
// ELAC and SEC
XPLMDataRef qpac_fcc_avail_array;
// Rudder limits
XPLMDataRef qpac_rudder_limit_pos;
// Hydraulics
XPLMDataRef qpac_hyd_pressure_array;
XPLMDataRef qpac_hyd_pump_array;
XPLMDataRef qpac_hyd_rat_mode;
XPLMDataRef qpac_hyd_y_elec_mode;
XPLMDataRef qpac_hyd_ptu_mode;
XPLMDataRef qpac_hyd_sys_qty_array;
// fire valve : sim/cockpit2/engine/fire_estinguisher_on[0,1] boolean
// Cabin Pressure
XPLMDataRef qpac_cabin_delta_p;
XPLMDataRef qpac_cabin_alt;
XPLMDataRef qpac_cabin_vs;
XPLMDataRef qpac_outflow_valve;
// ENG lower ECAM
XPLMDataRef qpac_ewd_start_mode;
XPLMDataRef qpac_start_valve_array;
XPLMDataRef qpac_nacelle_temp_array;
// COND
XPLMDataRef qpac_cond_hot_air_valve;
XPLMDataRef qpac_cond_cockpit_trim;
XPLMDataRef qpac_cond_zone1_trim;
XPLMDataRef qpac_cond_zone2_trim;
// Bleed
XPLMDataRef qpac_bleed_intercon;
XPLMDataRef qpac_bleed_x;
XPLMDataRef qpac_bleed_apu;
XPLMDataRef qpac_bleed_eng1;
XPLMDataRef qpac_bleed_eng2;
XPLMDataRef qpac_bleed_eng1_hp;
XPLMDataRef qpac_bleed_eng2_hp;
XPLMDataRef qpac_bleed_pack1_fcu;
XPLMDataRef qpac_bleed_pack2_fcu;
XPLMDataRef qpac_bleed_pack1_flow;
XPLMDataRef qpac_bleed_pack2_flow;
XPLMDataRef qpac_bleed_pack1_temp;
XPLMDataRef qpac_bleed_pack2_temp;
XPLMDataRef qpac_bleed_ram_air;
// APU
XPLMDataRef qpac_apu_egt;
XPLMDataRef qpac_apu_egt_limit;
// ELEC
XPLMDataRef qpac_elec_ext_pow_box;
XPLMDataRef qpac_elec_ac_cross_connect;
XPLMDataRef qpac_elec_connect_left;
XPLMDataRef qpac_elec_connect_center;
XPLMDataRef qpac_elec_connect_right;
XPLMDataRef qpac_elec_battery_supply;
XPLMDataRef qpac_elec_connectors;
XPLMDataRef qpac_elec_ohp_array;
XPLMDataRef qpac_elec_apu_box;
// FUEL
XPLMDataRef qpac_fuel_pump_array;
XPLMDataRef qpac_fuel_xfv_array ;
XPLMDataRef qpac_fuel_eng_lp_valve_array;
XPLMDataRef qpac_fuel_tv_array;
// ECAM SD page selection
XPLMDataRef qpac_sd_page;
XPLMDataRef qpac_clear_illuminated;
XPLMDataRef qpac_sd_eng;
XPLMDataRef qpac_sd_bleed;
XPLMDataRef qpac_sd_press;
XPLMDataRef qpac_sd_elec;
XPLMDataRef qpac_sd_hyd;
XPLMDataRef qpac_sd_fuel;
XPLMDataRef qpac_sd_apu;
XPLMDataRef qpac_sd_cond;
XPLMDataRef qpac_sd_door;
XPLMDataRef qpac_sd_wheel;
XPLMDataRef qpac_sd_fctl;
XPLMDataRef qpac_sd_status;
XPLMDataRef qpac_sd_to_config;

//qpac fcu toggles and push/pull commands
XPLMCommandRef qpac_to_config_press;
XPLMCommandRef qpac_push_alt;
XPLMCommandRef qpac_pull_alt;
XPLMCommandRef qpac_push_vs;
XPLMCommandRef qpac_pull_vs;
XPLMCommandRef qpac_push_hdg;
XPLMCommandRef qpac_pull_hdg;
XPLMCommandRef qpac_push_spd;
XPLMCommandRef qpac_pull_spd;
XPLMCommandRef qpac_athr_toggle;
XPLMCommandRef qpac_appr_toggle;
XPLMCommandRef qpac_loc_toggle;
XPLMCommandRef qpac_exped_toggle;
XPLMCommandRef qpac_abrk_low_toggle;
XPLMCommandRef qpac_abrk_med_toggle;
XPLMCommandRef qpac_abrk_max_toggle;

// MCDU1 Keys
XPLMCommandRef qpac_mdcu1_init;
XPLMCommandRef qpac_mdcu1_data;
XPLMCommandRef qpac_mdcu1_menu;
XPLMCommandRef qpac_mdcu1_perf;
XPLMCommandRef qpac_mdcu1_prog;
XPLMCommandRef qpac_mdcu1_fpln;
XPLMCommandRef qpac_mdcu1_dirto;
XPLMCommandRef qpac_mdcu1_radnav;
XPLMCommandRef qpac_mdcu1_airport;
XPLMCommandRef qpac_mdcu1_slew_up;
XPLMCommandRef qpac_mdcu1_slew_down;
XPLMCommandRef qpac_mdcu1_slew_left;
XPLMCommandRef qpac_mdcu1_slew_right;
XPLMCommandRef qpac_mdcu1_lsk_1l;
XPLMCommandRef qpac_mdcu1_lsk_2l;
XPLMCommandRef qpac_mdcu1_lsk_3l;
XPLMCommandRef qpac_mdcu1_lsk_4l;
XPLMCommandRef qpac_mdcu1_lsk_5l;
XPLMCommandRef qpac_mdcu1_lsk_6l;
XPLMCommandRef qpac_mdcu1_lsk_1r;
XPLMCommandRef qpac_mdcu1_lsk_2r;
XPLMCommandRef qpac_mdcu1_lsk_3r;
XPLMCommandRef qpac_mdcu1_lsk_4r;
XPLMCommandRef qpac_mdcu1_lsk_5r;
XPLMCommandRef qpac_mdcu1_lsk_6r;
XPLMCommandRef qpac_mdcu1_key_slash;
XPLMCommandRef qpac_mdcu1_key_space;
XPLMCommandRef qpac_mdcu1_key_overfly;
XPLMCommandRef qpac_mdcu1_key_clear;
XPLMCommandRef qpac_mdcu1_key_pm;
XPLMCommandRef qpac_mdcu1_key_decimal;
XPLMCommandRef qpac_mdcu1_key_0;
XPLMCommandRef qpac_mdcu1_key_1;
XPLMCommandRef qpac_mdcu1_key_2;
XPLMCommandRef qpac_mdcu1_key_3;
XPLMCommandRef qpac_mdcu1_key_4;
XPLMCommandRef qpac_mdcu1_key_5;
XPLMCommandRef qpac_mdcu1_key_6;
XPLMCommandRef qpac_mdcu1_key_7;
XPLMCommandRef qpac_mdcu1_key_8;
XPLMCommandRef qpac_mdcu1_key_9;
XPLMCommandRef qpac_mdcu1_key_a;
XPLMCommandRef qpac_mdcu1_key_b;
XPLMCommandRef qpac_mdcu1_key_c;
XPLMCommandRef qpac_mdcu1_key_d;
XPLMCommandRef qpac_mdcu1_key_e;
XPLMCommandRef qpac_mdcu1_key_f;
XPLMCommandRef qpac_mdcu1_key_g;
XPLMCommandRef qpac_mdcu1_key_h;
XPLMCommandRef qpac_mdcu1_key_i;
XPLMCommandRef qpac_mdcu1_key_j;
XPLMCommandRef qpac_mdcu1_key_k;
XPLMCommandRef qpac_mdcu1_key_l;
XPLMCommandRef qpac_mdcu1_key_m;
XPLMCommandRef qpac_mdcu1_key_n;
XPLMCommandRef qpac_mdcu1_key_o;
XPLMCommandRef qpac_mdcu1_key_p;
XPLMCommandRef qpac_mdcu1_key_q;
XPLMCommandRef qpac_mdcu1_key_r;
XPLMCommandRef qpac_mdcu1_key_s;
XPLMCommandRef qpac_mdcu1_key_t;
XPLMCommandRef qpac_mdcu1_key_u;
XPLMCommandRef qpac_mdcu1_key_v;
XPLMCommandRef qpac_mdcu1_key_w;
XPLMCommandRef qpac_mdcu1_key_x;
XPLMCommandRef qpac_mdcu1_key_y;
XPLMCommandRef qpac_mdcu1_key_z;

int qpac_ready = 0;
int qpac_version = 0;

/* QPAC Versions :
 *  major version number is x100
 *  0 = Not ready
 *  110 = QPAC Freeware 1.1
 *  150 = PeterAircraft A320
 *  202 = QPAC 2.02 Final Basic
 */

void findQpacDataRefs(void) {
	// For datarefs checks, remove for release
	// char msg[200];
	// XPLMDataTypeID reftype;

	qpac_plugin_status = XPLMFindDataRef("AirbusFBW/APPhase");

	if ( ( qpac_plugin_status == NULL ) || ( XPLMGetDatai(qpac_plugin_status) < 0 ) ) {

        qpac_ready = 0;
        qpac_version = 0;

    } else {
        if ( qpac_ready == 0 ) {

            qpac_ready = 1;
            qpac_version = 110;

            XPLMDebugString("XHSI: using QPAC AirbusFBW DataRefs\n");

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
            qpac_npa_slope = XPLMFindDataRef("AirbusFBW/NPASlope");
            qpac_appr_illuminated = XPLMFindDataRef("AirbusFBW/APPRilluminated");
            qpac_loc_illuminated = XPLMFindDataRef("AirbusFBW/LOCilluminated");
            qpac_appr_type = XPLMFindDataRef("AirbusFBW/ApprType");
            qpac_appr_mda = XPLMFindDataRef("AirbusFBW/ApprMDA");
            qpac_alt_is_cstr = XPLMFindDataRef("AirbusFBW/ALTisCstr");
            qpac_constraint_alt = XPLMFindDataRef("AirbusFBW/ConstraintAlt");
            // FCU
            qpac_fcu_hdg_trk = XPLMFindDataRef("AirbusFBW/HDGTRKmode");
            qpac_fcu_metric_alt = XPLMFindDataRef("AirbusFBW/MetricAlt");
            qpac_fcu_vs_dashed = XPLMFindDataRef("AirbusFBW/VSdashed");
            qpac_fcu_hdg_dashed = XPLMFindDataRef("AirbusFBW/HDGdashed");
            qpac_fcu_hdg_managed = XPLMFindDataRef("AirbusFBW/HDGmanaged");
            qpac_fcu_spd_dashed = XPLMFindDataRef("AirbusFBW/SPDdashed");
            qpac_fcu_spd_managed = XPLMFindDataRef("AirbusFBW/SPDmanaged");
            qpac_fcu_alt_managed = XPLMFindDataRef("AirbusFBW/ALTmanaged");

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
            qpac_thr_rating_type = XPLMFindDataRef("AirbusFBW/THRRatingType");
            qpac_thr_rating_n1 = XPLMFindDataRef("AirbusFBW/THRRatingN1");
            qpac_throttle_input = XPLMFindDataRef("AirbusFBW/throttle_input");
            // ILS Sig and Deviation Capt. and FO
            qpac_loc_val_capt = XPLMFindDataRef("AirbusFBW/LOCvalCapt");
            qpac_loc_on_capt = XPLMFindDataRef("AirbusFBW/LOConCapt");
            qpac_gs_val_capt = XPLMFindDataRef("AirbusFBW/GSvalCapt");
            qpac_gs_on_capt = XPLMFindDataRef("AirbusFBW/GSonCapt");
            qpac_ils_on_capt = XPLMFindDataRef("AirbusFBW/ILSonCapt");
            qpac_loc_val_fo = XPLMFindDataRef("AirbusFBW/LOCvalFO");
            qpac_loc_on_fo = XPLMFindDataRef("AirbusFBW/LOConFO");
            qpac_gs_val_fo = XPLMFindDataRef("AirbusFBW/GSvalFO");
            qpac_gs_on_fo = XPLMFindDataRef("AirbusFBW/GSonFO");
            qpac_ils_on_fo = XPLMFindDataRef("AirbusFBW/ILSonFO");
            qpac_ils_crs = XPLMFindDataRef("AirbusFBW/ILSCrs");
            qpac_ils_1 = XPLMFindDataRef("AirbusFBW/ILS1");
            qpac_ils_2 = XPLMFindDataRef("AirbusFBW/ILS2");
            qpac_ils_3 = XPLMFindDataRef("AirbusFBW/ILS3");
            // FD
            qpac_fd1 = XPLMFindDataRef("AirbusFBW/FD1Engage");
            qpac_fd2 = XPLMFindDataRef("AirbusFBW/FD2Engage");
            qpac_fd1_ver_bar = XPLMFindDataRef("AirbusFBW/FD1VerBar");
            qpac_fd1_hor_bar = XPLMFindDataRef("AirbusFBW/FD1HorBar");
            qpac_fd1_yaw_bar = XPLMFindDataRef("AirbusFBW/YawBar1");
            qpac_fd2_ver_bar = XPLMFindDataRef("AirbusFBW/FD2VerBar");
            qpac_fd2_hor_bar = XPLMFindDataRef("AirbusFBW/FD2HorBar");
            qpac_fd2_yaw_bar = XPLMFindDataRef("AirbusFBW/YawBar2");
            // Baro
            qpac_baro_std_capt = XPLMFindDataRef("AirbusFBW/BaroStdCapt");
            qpac_baro_unit_capt = XPLMFindDataRef("AirbusFBW/BaroUnitCapt");
            qpac_baro_hide_capt = XPLMFindDataRef("AirbusFBW/HideBaroCapt");
            qpac_baro_std_fo = XPLMFindDataRef("AirbusFBW/BaroStdFO");
            qpac_baro_unit_fo = XPLMFindDataRef("AirbusFBW/BaroUnitFO");
            qpac_baro_hide_fo = XPLMFindDataRef("AirbusFBW/HideBaroCO");
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
            // EFIS
            qpac_capt_efis_nd_mode = XPLMFindDataRef("AirbusFBW/NDmodeCapt");
            qpac_co_efis_nd_mode = XPLMFindDataRef("AirbusFBW/NDmodeFO");
            qpac_capt_efis_nd_range = XPLMFindDataRef("AirbusFBW/NDrangeCapt");
            qpac_co_efis_nd_range = XPLMFindDataRef("AirbusFBW/NDrangeFO");
            if (qpac_capt_efis_nd_range != NULL) qpac_version = 202;
            // Brakes
            // TODO : check dataref qpac_autobrake_low = XPLMFindDataRef("AirbusFBW/AutoBrkLow");
            qpac_autobrake_low = XPLMFindDataRef("AirbusFBW/AutoBrkLo");
            qpac_autobrake_med = XPLMFindDataRef("AirbusFBW/AutoBrkMed");
            qpac_autobrake_max = XPLMFindDataRef("AirbusFBW/AutoBrkMax");
            // Flaps and slats
            qpac_flaps_request_pos = XPLMFindDataRef("AirbusFBW/FlapsRequestPos");
            qpac_slats_request_pos = XPLMFindDataRef("AirbusFBW/SlatsRequestPos");
            // Ailerons
            qpac_right_aileron_pos = XPLMFindDataRef("sim/flightmodel/controls/wing4r_ail1def");
            qpac_left_aileron_pos = XPLMFindDataRef("sim/flightmodel/controls/wing4l_ail1def");
            // Spoilers
            qpac_spoilers_array = XPLMFindDataRef("AirbusFBW/SDSpoilerArray");
            // ELAC and SEC
            qpac_fcc_avail_array = XPLMFindDataRef("AirbusFBW/FCCAvailArray");
            // Rudder limits
            qpac_rudder_limit_pos = XPLMFindDataRef("AirbusFBW/RTLPosition");
            // Hydraulics
            qpac_hyd_pressure_array = XPLMFindDataRef("AirbusFBW/HydSysPressArray");
            qpac_hyd_pump_array = XPLMFindDataRef("AirbusFBW/HydPumpArray");
            qpac_hyd_rat_mode = XPLMFindDataRef("AirbusFBW/HydRATMode");
            qpac_hyd_y_elec_mode = XPLMFindDataRef("AirbusFBW/HydYElecMode");
            qpac_hyd_ptu_mode = XPLMFindDataRef("AirbusFBW/HydPTUMode");
            qpac_hyd_sys_qty_array = XPLMFindDataRef("AirbusFBW/HydSysQtyArray");
            // fire valve : sim/cockpit2/engine/fire_estinguisher_on[0,1] boolean
            // Cabin Pressure
            qpac_cabin_delta_p = XPLMFindDataRef("AirbusFBW/CabinDeltaP");
            qpac_cabin_alt = XPLMFindDataRef("AirbusFBW/CabinAlt");
            qpac_cabin_vs = XPLMFindDataRef("AirbusFBW/CabinVS");
            qpac_outflow_valve = XPLMFindDataRef("AirbusFBW/OutflowValve");
            // ENG lower ECAM
            qpac_ewd_start_mode = XPLMFindDataRef("AirbusFBW/EWDStartMode");
            qpac_start_valve_array = XPLMFindDataRef("AirbusFBW/StartValveArray");
            qpac_nacelle_temp_array = XPLMFindDataRef("AirbusFBW/NacelleTempArray");
            // COND
            qpac_cond_hot_air_valve = XPLMFindDataRef("AirbusFBW/HotAirValve");
            qpac_cond_cockpit_trim = XPLMFindDataRef("AirbusFBW/CockpitTrim");
            qpac_cond_zone1_trim = XPLMFindDataRef("AirbusFBW/Zone1Trim");
            qpac_cond_zone2_trim = XPLMFindDataRef("AirbusFBW/Zone2Trim");
            // Bleed
            qpac_bleed_intercon = XPLMFindDataRef("AirbusFBW/BleedIntercon");
            qpac_bleed_x = XPLMFindDataRef("AirbusFBW/XBleedInd");
            qpac_bleed_apu = XPLMFindDataRef("AirbusFBW/APUBleedInd");
            qpac_bleed_eng1 = XPLMFindDataRef("AirbusFBW/ENG1BleedInd");
            qpac_bleed_eng2 = XPLMFindDataRef("AirbusFBW/ENG2BleedInd");
            qpac_bleed_eng1_hp = XPLMFindDataRef("AirbusFBW/ENG1HPBleedInd");
            qpac_bleed_eng2_hp = XPLMFindDataRef("AirbusFBW/ENG2HPBleedInd");
            qpac_bleed_pack1_fcu = XPLMFindDataRef("AirbusFBW/Pack1FCUInd");
            qpac_bleed_pack2_fcu = XPLMFindDataRef("AirbusFBW/Pack2FCUInd");
            qpac_bleed_pack1_flow = XPLMFindDataRef("AirbusFBW/Pack1Flow");
            qpac_bleed_pack2_flow = XPLMFindDataRef("AirbusFBW/Pack2Flow");
            qpac_bleed_pack1_temp = XPLMFindDataRef("AirbusFBW/Pack1Temp");
            qpac_bleed_pack2_temp = XPLMFindDataRef("AirbusFBW/Pack2Temp");
            qpac_bleed_ram_air = XPLMFindDataRef("AirbusFBW/RamAirValueSD");
            // APU
            qpac_apu_egt = XPLMFindDataRef("AirbusFBW/APUEGT");
            qpac_apu_egt_limit = XPLMFindDataRef("AirbusFBW/APUEGTLimit");
            // ELEC
            qpac_elec_ext_pow_box = XPLMFindDataRef("AirbusFBW/SDExtPowBox");
            qpac_elec_ac_cross_connect = XPLMFindDataRef("AirbusFBW/SDACCrossConnect");
            qpac_elec_connect_left = XPLMFindDataRef("AirbusFBW/SDELConnectLeft");
            qpac_elec_connect_center = XPLMFindDataRef("AirbusFBW/SDELConnectCenter");
            qpac_elec_connect_right = XPLMFindDataRef("AirbusFBW/SDELConnectRight");
            qpac_elec_battery_supply = XPLMFindDataRef("AirbusFBW/SDELBatterySupply");
            qpac_elec_connectors = XPLMFindDataRef("AirbusFBW/ElecConnectors"); // array [0,1]
            qpac_elec_ohp_array = XPLMFindDataRef("AirbusFBW/ElecOHPArray"); // array [0,1]
            qpac_elec_apu_box  = XPLMFindDataRef("AirbusFBW/SDAPUBox");

            // FUEL
            qpac_fuel_pump_array = XPLMFindDataRef("AirbusFBW/FuelPumpOHPArray");
            // Fuel pump : 0 = closed amber ; 1 = open green ; 2 = closed amber ; 3 = LO amber
            qpac_fuel_xfv_array = XPLMFindDataRef("AirbusFBW/FuelXFVSDArray");
            // X Fer Valve 0 = jammed "/"  ; 1 = closed green ; 2 = open green; 3 = closed amber ; 4 = open amber
            qpac_fuel_eng_lp_valve_array = XPLMFindDataRef("AirbusFBW/ENGFuelLPValveArray");
            // ENG LP Valve 0 = jammed "/"  ; 1 = amber closed ; 2 = amber open ; 3 = green open
            qpac_fuel_tv_array = XPLMFindDataRef("AirbusFBW/FuelTVSDArray");

            // ECAM SD page selection
            qpac_sd_page = XPLMFindDataRef("AirbusFBW/SDPage");
            qpac_clear_illuminated = XPLMFindDataRef("AirbusFBW/CLRillum");
            qpac_sd_eng = XPLMFindDataRef("AirbusFBW/SDENG");
            qpac_sd_bleed = XPLMFindDataRef("AirbusFBW/SDBLEED");
            qpac_sd_press = XPLMFindDataRef("AirbusFBW/SDPRESS");
            qpac_sd_elec = XPLMFindDataRef("AirbusFBW/SDELEC");
            qpac_sd_hyd = XPLMFindDataRef("AirbusFBW/SDHYD");
            qpac_sd_fuel = XPLMFindDataRef("AirbusFBW/SDFUEL");
            qpac_sd_apu = XPLMFindDataRef("AirbusFBW/SDAPU");
            qpac_sd_cond = XPLMFindDataRef("AirbusFBW/SDCOND");
            qpac_sd_door = XPLMFindDataRef("AirbusFBW/SDDOOR");
            qpac_sd_wheel = XPLMFindDataRef("AirbusFBW/SDWHEEL");
            qpac_sd_fctl = XPLMFindDataRef("AirbusFBW/SDFCTL");
            qpac_sd_status = XPLMFindDataRef("AirbusFBW/SDSTATUS");
            qpac_sd_to_config = XPLMFindDataRef("AirbusFBW/TOConfigPress");

            //qpac fcu toggles and push/pull commands
            qpac_to_config_press = XPLMFindCommand("AirbusFBW/TOConfigPress");
            qpac_push_alt = XPLMFindCommand("AirbusFBW/PushAltitude");
            qpac_pull_alt = XPLMFindCommand("AirbusFBW/PullAltitude");
            qpac_push_vs = XPLMFindCommand("AirbusFBW/PushVSSel");
            qpac_pull_vs = XPLMFindCommand("AirbusFBW/PullVSSel");
            qpac_push_hdg = XPLMFindCommand("AirbusFBW/PushHDGSel");
            qpac_pull_hdg = XPLMFindCommand("AirbusFBW/PullHDGSel");
            qpac_push_spd = XPLMFindCommand("AirbusFBW/PushSPDSel");
            qpac_pull_spd = XPLMFindCommand("AirbusFBW/PullSPDSel");
            qpac_athr_toggle = XPLMFindCommand("AirbusFBW/ATHRbutton");
            qpac_appr_toggle = XPLMFindCommand("AirbusFBW/APPRbutton");
            qpac_loc_toggle = XPLMFindCommand("AirbusFBW/LOCbutton");
            qpac_exped_toggle = XPLMFindCommand("AirbusFBW/EXPEDbutton");
            qpac_abrk_low_toggle = XPLMFindCommand("AirbusFBW/AbrkLo");
            qpac_abrk_med_toggle = XPLMFindCommand("AirbusFBW/AbrkMed");
            qpac_abrk_max_toggle = XPLMFindCommand("AirbusFBW/AbrkMax");

            // MCDU1 Keys
            qpac_mdcu1_init = XPLMFindCommand("AirbusFBW/MCDU1Init");
            qpac_mdcu1_data = XPLMFindCommand("AirbusFBW/MCDU1Data");
            qpac_mdcu1_menu = XPLMFindCommand("AirbusFBW/MCDU1Menu");
            qpac_mdcu1_perf = XPLMFindCommand("AirbusFBW/MCDU1Perf");
            qpac_mdcu1_prog = XPLMFindCommand("AirbusFBW/MCDU1Prog");
            qpac_mdcu1_fpln = XPLMFindCommand("AirbusFBW/MCDU1Fpln");
            qpac_mdcu1_dirto = XPLMFindCommand("AirbusFBW/MCDU1DirTo");
            qpac_mdcu1_radnav = XPLMFindCommand("AirbusFBW/MCDU1RadNav");
            qpac_mdcu1_airport = XPLMFindCommand("AirbusFBW/MCDU1Airport");
            qpac_mdcu1_slew_up = XPLMFindCommand("AirbusFBW/MCDU1SlewUp");
            qpac_mdcu1_slew_down = XPLMFindCommand("AirbusFBW/MCDU1SlewDown");
            qpac_mdcu1_slew_left = XPLMFindCommand("AirbusFBW/MCDU1SlewLeft");
            qpac_mdcu1_slew_right = XPLMFindCommand("AirbusFBW/MCDU1SlewRight");
            qpac_mdcu1_lsk_1l = XPLMFindCommand("AirbusFBW/MCDU1LSK1L");
            qpac_mdcu1_lsk_2l = XPLMFindCommand("AirbusFBW/MCDU1LSK2L");
            qpac_mdcu1_lsk_3l = XPLMFindCommand("AirbusFBW/MCDU1LSK3L");
            qpac_mdcu1_lsk_4l = XPLMFindCommand("AirbusFBW/MCDU1LSK4L");
            qpac_mdcu1_lsk_5l = XPLMFindCommand("AirbusFBW/MCDU1LSK5L");
            qpac_mdcu1_lsk_6l = XPLMFindCommand("AirbusFBW/MCDU1LSK6L");
            qpac_mdcu1_lsk_1r = XPLMFindCommand("AirbusFBW/MCDU1LSK1R");
            qpac_mdcu1_lsk_2r = XPLMFindCommand("AirbusFBW/MCDU1LSK2R");
            qpac_mdcu1_lsk_3r = XPLMFindCommand("AirbusFBW/MCDU1LSK3R");
            qpac_mdcu1_lsk_4r = XPLMFindCommand("AirbusFBW/MCDU1LSK4R");
            qpac_mdcu1_lsk_5r = XPLMFindCommand("AirbusFBW/MCDU1LSK5R");
            qpac_mdcu1_lsk_6r = XPLMFindCommand("AirbusFBW/MCDU1LSK6R");
            qpac_mdcu1_key_slash = XPLMFindCommand("AirbusFBW/MCDU1KeySlash");
            qpac_mdcu1_key_space = XPLMFindCommand("AirbusFBW/MCDU1KeySpace");
            qpac_mdcu1_key_overfly = XPLMFindCommand("AirbusFBW/MCDU1KeyOverfly");
            qpac_mdcu1_key_clear = XPLMFindCommand("AirbusFBW/MCDU1KeyClear");
            qpac_mdcu1_key_pm = XPLMFindCommand("AirbusFBW/MCDU1KeyPM");
            qpac_mdcu1_key_decimal = XPLMFindCommand("AirbusFBW/MCDU1KeyDecimal");
            qpac_mdcu1_key_0 = XPLMFindCommand("AirbusFBW/MCDU1Key0");
            qpac_mdcu1_key_1 = XPLMFindCommand("AirbusFBW/MCDU1Key1");
            qpac_mdcu1_key_2 = XPLMFindCommand("AirbusFBW/MCDU1Key2");
            qpac_mdcu1_key_3 = XPLMFindCommand("AirbusFBW/MCDU1Key3");
            qpac_mdcu1_key_4 = XPLMFindCommand("AirbusFBW/MCDU1Key4");
            qpac_mdcu1_key_5 = XPLMFindCommand("AirbusFBW/MCDU1Key5");
            qpac_mdcu1_key_6 = XPLMFindCommand("AirbusFBW/MCDU1Key6");
            qpac_mdcu1_key_7 = XPLMFindCommand("AirbusFBW/MCDU1Key7");
            qpac_mdcu1_key_8 = XPLMFindCommand("AirbusFBW/MCDU1Key8");
            qpac_mdcu1_key_9 = XPLMFindCommand("AirbusFBW/MCDU1Key9");
            qpac_mdcu1_key_a = XPLMFindCommand("AirbusFBW/MCDU1KeyA");
            qpac_mdcu1_key_b = XPLMFindCommand("AirbusFBW/MCDU1KeyB");
            qpac_mdcu1_key_c = XPLMFindCommand("AirbusFBW/MCDU1KeyC");
            qpac_mdcu1_key_d = XPLMFindCommand("AirbusFBW/MCDU1KeyD");
            qpac_mdcu1_key_e = XPLMFindCommand("AirbusFBW/MCDU1KeyE");
            qpac_mdcu1_key_f = XPLMFindCommand("AirbusFBW/MCDU1KeyF");
            qpac_mdcu1_key_g = XPLMFindCommand("AirbusFBW/MCDU1KeyG");
            qpac_mdcu1_key_h = XPLMFindCommand("AirbusFBW/MCDU1KeyH");
            qpac_mdcu1_key_i = XPLMFindCommand("AirbusFBW/MCDU1KeyI");
            qpac_mdcu1_key_j = XPLMFindCommand("AirbusFBW/MCDU1KeyJ");
            qpac_mdcu1_key_k = XPLMFindCommand("AirbusFBW/MCDU1KeyK");
            qpac_mdcu1_key_l = XPLMFindCommand("AirbusFBW/MCDU1KeyL");
            qpac_mdcu1_key_m = XPLMFindCommand("AirbusFBW/MCDU1KeyM");
            qpac_mdcu1_key_n = XPLMFindCommand("AirbusFBW/MCDU1KeyN");
            qpac_mdcu1_key_o = XPLMFindCommand("AirbusFBW/MCDU1KeyO");
            qpac_mdcu1_key_p = XPLMFindCommand("AirbusFBW/MCDU1KeyP");
            qpac_mdcu1_key_q = XPLMFindCommand("AirbusFBW/MCDU1KeyQ");
            qpac_mdcu1_key_r = XPLMFindCommand("AirbusFBW/MCDU1KeyR");
            qpac_mdcu1_key_s = XPLMFindCommand("AirbusFBW/MCDU1KeyS");
            qpac_mdcu1_key_t = XPLMFindCommand("AirbusFBW/MCDU1KeyT");
            qpac_mdcu1_key_u = XPLMFindCommand("AirbusFBW/MCDU1KeyU");
            qpac_mdcu1_key_v = XPLMFindCommand("AirbusFBW/MCDU1KeyV");
            qpac_mdcu1_key_w = XPLMFindCommand("AirbusFBW/MCDU1KeyW");
            qpac_mdcu1_key_x = XPLMFindCommand("AirbusFBW/MCDU1KeyX");
            qpac_mdcu1_key_y = XPLMFindCommand("AirbusFBW/MCDU1KeyY");
            qpac_mdcu1_key_z = XPLMFindCommand("AirbusFBW/MCDU1KeyZ");


            findQpacMsgDataRefs();

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

void cmdQpacSDPage(int page) {
	switch (page) {
		case 0  : XPLMSetDatai(qpac_sd_eng,1); break;
		case 1  : XPLMSetDatai(qpac_sd_bleed,1); break;
		case 2  : XPLMSetDatai(qpac_sd_press,1); break;
		case 3  : XPLMSetDatai(qpac_sd_elec,1); break;
		case 4  : XPLMSetDatai(qpac_sd_hyd,1); break;
		case 5  : XPLMSetDatai(qpac_sd_fuel,1); break;
		case 6  : XPLMSetDatai(qpac_sd_apu,1); break;
		case 7  : XPLMSetDatai(qpac_sd_cond,1); break;
		case 8  : XPLMSetDatai(qpac_sd_door,1); break;
		case 9  : XPLMSetDatai(qpac_sd_wheel,1); break;
		case 10  : XPLMSetDatai(qpac_sd_fctl,1); break;
		case 11  :
			XPLMSetDatai(qpac_sd_eng,0);
			XPLMSetDatai(qpac_sd_bleed,0);
			XPLMSetDatai(qpac_sd_press,0);
			XPLMSetDatai(qpac_sd_elec,0);
			XPLMSetDatai(qpac_sd_hyd,0);
			XPLMSetDatai(qpac_sd_fuel,0);
			XPLMSetDatai(qpac_sd_apu,0);
			XPLMSetDatai(qpac_sd_cond,0);
			XPLMSetDatai(qpac_sd_door,0);
			XPLMSetDatai(qpac_sd_wheel,0);
			XPLMSetDatai(qpac_sd_fctl,0);
			XPLMSetDatai(qpac_sd_status,0);
			break;
		case 12 : XPLMSetDatai(qpac_sd_status,1); break;
	}
}

void writeQpacDataRef(int id, float value) {

    char info_string[80];
    sprintf(info_string, "XHSI: received AirbusFBW setting: ID=%d  VALUE=%f\n", id, value);
    XPLMDebugString(info_string);

    switch (id) {
		case QPAC_SD_PAGE :
			cmdQpacSDPage((int) value);
			break;
		case QPAC_KEY_PRESS :
			switch ((int)value) {

				case QPAC_KEY_TO_CONFIG :
					if(qpac_ready){
						XPLMCommandOnce(qpac_to_config_press);
					}
					break;
				case QPAC_KEY_PUSH_ALT :
					if(qpac_ready){
						XPLMCommandOnce(qpac_push_alt);
					}
					break;
				case QPAC_KEY_PULL_ALT :
					if(qpac_ready){
						XPLMCommandOnce(qpac_pull_alt);
					}
					break;
				case QPAC_KEY_PUSH_VS :
					if(qpac_ready){
						XPLMCommandOnce(qpac_push_vs);
					}
					break;
				case QPAC_KEY_PULL_VS :
					if(qpac_ready){
						XPLMCommandOnce(qpac_pull_vs);
					}
					break;
				case QPAC_KEY_PUSH_HDG :
					if(qpac_ready){
						XPLMCommandOnce(qpac_push_hdg);
					}
					break;
				case QPAC_KEY_PULL_HDG :
					if(qpac_ready){
						XPLMCommandOnce(qpac_pull_hdg);
					}
					break;
				case QPAC_KEY_PUSH_SPD :
					if(qpac_ready){
						XPLMCommandOnce(qpac_push_spd);
					}
					break;
				case QPAC_KEY_PULL_SPD :
					if(qpac_ready){
						XPLMCommandOnce(qpac_pull_spd);
					}
					break;
				case QPAC_KEY_ATHR :
					if(qpac_ready){
						XPLMCommandOnce(qpac_athr_toggle);
					}
					break;
				case QPAC_KEY_APPR :
					if(qpac_ready){
						XPLMCommandOnce(qpac_appr_toggle);
					}
					break;
				case QPAC_KEY_EXPED :
					if(qpac_ready){
						XPLMCommandOnce(qpac_loc_toggle);
					}
					break;
				case QPAC_KEY_LOC :
					if(qpac_ready){
						XPLMCommandOnce(qpac_exped_toggle);
					}
					break;
				case QPAC_KEY_ABRK_LOW :
					if(qpac_ready){
						XPLMCommandOnce(qpac_abrk_low_toggle);
					}
					break;
				case QPAC_KEY_ABRK_MED :
					if(qpac_ready){
						XPLMCommandOnce(qpac_abrk_med_toggle);
					}
					break;
				case QPAC_KEY_ABRK_MAX :
					if(qpac_ready){
						XPLMCommandOnce(qpac_abrk_max_toggle);
					}
					break;
				case QPAC_KEY_MDCU1_INIT : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_init); } break;
				case QPAC_KEY_MDCU1_DATA : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_data); } break;
				case QPAC_KEY_MDCU1_MENU : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_menu); } break;
				case QPAC_KEY_MDCU1_PERF : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_perf); } break;
				case QPAC_KEY_MDCU1_PROG : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_prog); } break;
				case QPAC_KEY_MDCU1_FPLN : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_fpln); } break;
				case QPAC_KEY_MDCU1_DIR_TO : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_dirto); } break;
				case QPAC_KEY_MDCU1_RAD_NAV : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_radnav); } break;
				case QPAC_KEY_MDCU1_AIRPORT : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_airport); } break;
				case QPAC_KEY_MDCU1_SLEW_UP : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_slew_up); } break;
				case QPAC_KEY_MDCU1_SLEW_DOWN : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_slew_down); } break;
				case QPAC_KEY_MDCU1_SLEW_LEFT : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_slew_left); } break;
				case QPAC_KEY_MDCU1_SLEW_RIGHT : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_slew_right); } break;
				case QPAC_KEY_MDCU1_LSK1L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_1l); } break;
				case QPAC_KEY_MDCU1_LSK2L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_2l); } break;
				case QPAC_KEY_MDCU1_LSK3L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_3l); } break;
				case QPAC_KEY_MDCU1_LSK4L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_4l); } break;
				case QPAC_KEY_MDCU1_LSK5L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_5l); } break;
				case QPAC_KEY_MDCU1_LSK6L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_6l); } break;
				case QPAC_KEY_MDCU1_LSK1R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_1r); } break;
				case QPAC_KEY_MDCU1_LSK2R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_2r); } break;
				case QPAC_KEY_MDCU1_LSK3R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_3r); } break;
				case QPAC_KEY_MDCU1_LSK4R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_4r); } break;
				case QPAC_KEY_MDCU1_LSK5R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_5r); } break;
				case QPAC_KEY_MDCU1_LSK6R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_lsk_6r); } break;
				case QPAC_KEY_MDCU1_DEL    : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_clear); } break;
				case QPAC_KEY_MDCU1_SPACE  : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_space); } break;
				case QPAC_KEY_MDCU1_OVERFL : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_overfly); } break;
				case QPAC_KEY_MDCU1_PLUS_M : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_pm); } break;
				case QPAC_KEY_MDCU1_DOT    : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_decimal); } break;
				case QPAC_KEY_MDCU1_SLASH  : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_slash); } break;
				case QPAC_KEY_MDCU1_0 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_0); } break;
				case QPAC_KEY_MDCU1_1 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_1); } break;
				case QPAC_KEY_MDCU1_2 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_2); } break;
				case QPAC_KEY_MDCU1_3 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_3); } break;
				case QPAC_KEY_MDCU1_4 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_4); } break;
				case QPAC_KEY_MDCU1_5 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_5); } break;
				case QPAC_KEY_MDCU1_6 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_6); } break;
				case QPAC_KEY_MDCU1_7 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_7); } break;
				case QPAC_KEY_MDCU1_8 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_8); } break;
				case QPAC_KEY_MDCU1_9 : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_9); } break;
				case QPAC_KEY_MDCU1_A : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_a); } break;
				case QPAC_KEY_MDCU1_B : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_b); } break;
				case QPAC_KEY_MDCU1_C : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_c); } break;
				case QPAC_KEY_MDCU1_D : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_d); } break;
				case QPAC_KEY_MDCU1_E : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_e); } break;
				case QPAC_KEY_MDCU1_F : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_f); } break;
				case QPAC_KEY_MDCU1_G : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_g); } break;
				case QPAC_KEY_MDCU1_H : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_h); } break;
				case QPAC_KEY_MDCU1_I : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_i); } break;
				case QPAC_KEY_MDCU1_J : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_j); } break;
				case QPAC_KEY_MDCU1_K : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_k); } break;
				case QPAC_KEY_MDCU1_L : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_l); } break;
				case QPAC_KEY_MDCU1_M : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_m); } break;
				case QPAC_KEY_MDCU1_N : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_n); } break;
				case QPAC_KEY_MDCU1_O : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_o); } break;
				case QPAC_KEY_MDCU1_P : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_p); } break;
				case QPAC_KEY_MDCU1_Q : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_q); } break;
				case QPAC_KEY_MDCU1_R : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_r); } break;
				case QPAC_KEY_MDCU1_S : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_s); } break;
				case QPAC_KEY_MDCU1_T : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_t); } break;
				case QPAC_KEY_MDCU1_U : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_u); } break;
				case QPAC_KEY_MDCU1_V : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_v); } break;
				case QPAC_KEY_MDCU1_W : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_w); } break;
				case QPAC_KEY_MDCU1_X : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_x); } break;
				case QPAC_KEY_MDCU1_Y : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_y); } break;
				case QPAC_KEY_MDCU1_Z : if (qpac_ready) { XPLMCommandOnce(qpac_mdcu1_key_z); } break;

			}

			break;
    }
}

