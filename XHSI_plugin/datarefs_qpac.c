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
#include "datarefs_qpac.h"
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
// Compass
XPLMDataRef qpac_true_mag;
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
XPLMDataRef qpac_vfe_next;
XPLMDataRef qpac_v_green_dot_value;
XPLMDataRef qpac_vf_value;
XPLMDataRef qpac_vs_value;
XPLMDataRef qpac_v2_value;
XPLMDataRef qpac_vr_value;
XPLMDataRef qpac_alpha_floor_mode;

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
XPLMDataRef qpac_left_brake_release;
XPLMDataRef qpac_right_brake_release;
XPLMDataRef qpac_nw_anti_skid;
// Gears indicators
XPLMDataRef qpac_left_gear_ind;
XPLMDataRef qpac_nose_gear_ind;
XPLMDataRef qpac_right_gear_ind;
// Triple brake indicator
XPLMDataRef qpac_brake_accu;
XPLMDataRef qpac_tot_right_brake;
XPLMDataRef qpac_tot_left_brake;
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
// ENG lower ECAM (ignition and nacelles)
XPLMDataRef qpac_eng_mode_switch;
XPLMDataRef qpac_ewd_start_mode;
XPLMDataRef qpac_start_valve_array;
XPLMDataRef qpac_nacelle_temp_array;
// COND
XPLMDataRef qpac_cond_hot_air_valve;
XPLMDataRef qpac_cond_cockpit_trim;
XPLMDataRef qpac_cond_zone1_trim;
XPLMDataRef qpac_cond_zone2_trim;
XPLMDataRef qpac_cond_cockpit_temp;
XPLMDataRef qpac_cond_aft_cabin_temp;
XPLMDataRef qpac_cond_fwd_cabin_temp;
// Doors - OXY
XPLMDataRef qpac_door_pax_array;
XPLMDataRef qpac_door_cargo_array;
XPLMDataRef qpac_door_bulk_door;
XPLMDataRef qpac_crew_oxy_mask;
// Bleed
XPLMDataRef qpac_bleed_intercon;
XPLMDataRef qpac_bleed_x;
XPLMDataRef qpac_bleed_apu;
XPLMDataRef qpac_bleed_eng1;
XPLMDataRef qpac_bleed_eng2;
XPLMDataRef qpac_bleed_eng1_hp;
XPLMDataRef qpac_bleed_eng2_hp;
XPLMDataRef qpac_bleed_pack1_fcv;
XPLMDataRef qpac_bleed_pack2_fcv;
XPLMDataRef qpac_bleed_pack1_flow;
XPLMDataRef qpac_bleed_pack2_flow;
XPLMDataRef qpac_bleed_pack1_temp;
XPLMDataRef qpac_bleed_pack2_temp;
XPLMDataRef qpac_bleed_ram_air;
XPLMDataRef qpac_bleed_ram_air_valve;
XPLMDataRef qpac_bleed_left_press;
XPLMDataRef qpac_bleed_right_press;
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
XPLMDataRef qpac_elec_battery_volt;
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

// ECAM SD lines
XPLMDataRef qpac_sd_line_amber[QPAC_SD_LINES];
XPLMDataRef qpac_sd_line_blue[QPAC_SD_LINES];
XPLMDataRef qpac_sd_line_green[QPAC_SD_LINES];
XPLMDataRef qpac_sd_line_red[QPAC_SD_LINES];
XPLMDataRef qpac_sd_line_white[QPAC_SD_LINES];

// Anti-ice
XPLMDataRef qpac_wing_anti_ice;
XPLMDataRef qpac_wing_anti_ice_lights;
XPLMDataRef qpac_eng1_anti_ice;
XPLMDataRef qpac_eng1_anti_ice_lights;
XPLMDataRef qpac_eng2_anti_ice;
XPLMDataRef qpac_eng2_anti_ice_lights;

//qpac FCU toggles, push/pull commands, RMP, MCDU
XPLMCommandRef qpac_command[QPAC_KEY_MAX];

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
    int         i;
    char        buf[100];

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
            // Compass
            qpac_true_mag = XPLMFindDataRef("AirbusFBW/truemag");
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
            qpac_vfe_next = XPLMFindDataRef("qpac_airbus/pfdoutputs/general/VFENext_value");
            qpac_v_green_dot_value = XPLMFindDataRef("qpac_airbus/pfdoutputs/general/VGreenDot_value");
            qpac_vf_value = XPLMFindDataRef("qpac_airbus/pfdoutputs/general/VF_value");
            qpac_vs_value = XPLMFindDataRef("qpac_airbus/pfdoutputs/general/VS_value");
            qpac_v2_value = XPLMFindDataRef("qpac_airbus/performance/V2");
            qpac_vr_value = XPLMFindDataRef("qpac_airbus/performance/VR");
            qpac_alpha_floor_mode = XPLMFindDataRef("qpac_airbus/pfdoutputs/general/alpha_floor_mode");

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
            qpac_autobrake_low = XPLMFindDataRef("AirbusFBW/AutoBrkLo");
            qpac_autobrake_med = XPLMFindDataRef("AirbusFBW/AutoBrkMed");
            qpac_autobrake_max = XPLMFindDataRef("AirbusFBW/AutoBrkMax");
            qpac_left_brake_release = XPLMFindDataRef("AirbusFBW/LeftBrakeRelease");
            qpac_right_brake_release = XPLMFindDataRef("AirbusFBW/RightBrakeRelease");
            qpac_nw_anti_skid = XPLMFindDataRef("AirbusFBW/NWSnAntiSkid");
            // Gears indicators
            qpac_left_gear_ind = XPLMFindDataRef("AirbusFBW/LeftGearInd");
            qpac_nose_gear_ind = XPLMFindDataRef("AirbusFBW/NoseGearInd");
            qpac_right_gear_ind = XPLMFindDataRef("AirbusFBW/RightGearInd");
            // Triple brake indicator
            qpac_brake_accu = XPLMFindDataRef("AirbusFBW/BrakeAccu");
            qpac_tot_right_brake = XPLMFindDataRef("AirbusFBW/TotRightBrake");
            qpac_tot_left_brake = XPLMFindDataRef("AirbusFBW/TotLeftBrake");
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
            qpac_eng_mode_switch = XPLMFindDataRef("AirbusFBW/ENGModeSwitch");
            qpac_start_valve_array = XPLMFindDataRef("AirbusFBW/StartValveArray");
            qpac_nacelle_temp_array = XPLMFindDataRef("AirbusFBW/NacelleTempArray");
            // COND
            qpac_cond_hot_air_valve = XPLMFindDataRef("AirbusFBW/HotAirValve");
            qpac_cond_cockpit_trim = XPLMFindDataRef("AirbusFBW/CockpitTrim");
            qpac_cond_zone1_trim = XPLMFindDataRef("AirbusFBW/Zone1Trim");
            qpac_cond_zone2_trim = XPLMFindDataRef("AirbusFBW/Zone2Trim");
            qpac_cond_cockpit_temp = XPLMFindDataRef("AirbusFBW/CockpitTemp");
            qpac_cond_aft_cabin_temp = XPLMFindDataRef("AirbusFBW/AftCabinTemp");
            qpac_cond_fwd_cabin_temp = XPLMFindDataRef("AirbusFBW/FwdCabinTemp");
            // Doors - Oxygen
            qpac_door_pax_array = XPLMFindDataRef("AirbusFBW/PaxDoorArray");
            qpac_door_cargo_array = XPLMFindDataRef("AirbusFBW/CargoDoorArray");
            qpac_door_bulk_door = XPLMFindDataRef("AirbusFBW/BulkDoor");
            qpac_crew_oxy_mask = XPLMFindDataRef("AirbusFBW/CrewOxyMask");
            // Bleed
            qpac_bleed_intercon = XPLMFindDataRef("AirbusFBW/BleedIntercon");
            qpac_bleed_x = XPLMFindDataRef("AirbusFBW/XBleedInd");
            qpac_bleed_apu = XPLMFindDataRef("AirbusFBW/APUBleedInd");
            qpac_bleed_eng1 = XPLMFindDataRef("AirbusFBW/ENG1BleedInd");
            qpac_bleed_eng2 = XPLMFindDataRef("AirbusFBW/ENG2BleedInd");
            qpac_bleed_eng1_hp = XPLMFindDataRef("AirbusFBW/ENG1HPBleedInd");
            qpac_bleed_eng2_hp = XPLMFindDataRef("AirbusFBW/ENG2HPBleedInd");
            qpac_bleed_pack1_fcv = XPLMFindDataRef("AirbusFBW/Pack1FCVInd");
            qpac_bleed_pack2_fcv = XPLMFindDataRef("AirbusFBW/Pack2FCVInd");
            qpac_bleed_pack1_flow = XPLMFindDataRef("AirbusFBW/Pack1Flow");
            qpac_bleed_pack2_flow = XPLMFindDataRef("AirbusFBW/Pack2Flow");
            qpac_bleed_pack1_temp = XPLMFindDataRef("AirbusFBW/Pack1Temp");
            qpac_bleed_pack2_temp = XPLMFindDataRef("AirbusFBW/Pack2Temp");
            qpac_bleed_ram_air = XPLMFindDataRef("AirbusFBW/RamAirSwitchLights");
            qpac_bleed_ram_air_valve = XPLMFindDataRef("AirbusFBW/RamAirValveSD");
            qpac_bleed_left_press = XPLMFindDataRef("AirbusFBW/LeftBleedPress");
            qpac_bleed_right_press = XPLMFindDataRef("AirbusFBW/RightBleedPress");
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
            qpac_elec_battery_volt = XPLMFindDataRef("AirbusFBW/BatVolts"); // array [0,4]

            // FUEL
            // qpac_fuel_pump_array = XPLMFindDataRef("AirbusFBW/FuelPumpOHPArray");
            // Fuel pump : 0 = closed amber ; 1 = open green ; 2 = closed amber ; 3 = LO amber
            qpac_fuel_pump_array = XPLMFindDataRef("AirbusFBW/FuelPumpSDArray");
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

            // Anti-ice
            qpac_wing_anti_ice = XPLMFindDataRef("AirbusFBW/WAISwitch");
            qpac_wing_anti_ice_lights = XPLMFindDataRef("AirbusFBW/WAILights");
            qpac_eng1_anti_ice = XPLMFindDataRef("AirbusFBW/ENG1AISwitch");
            qpac_eng1_anti_ice_lights = XPLMFindDataRef("AirbusFBW/ENG1AILights");
            qpac_eng2_anti_ice  = XPLMFindDataRef("AirbusFBW/ENG2AISwitch");
            qpac_eng2_anti_ice_lights = XPLMFindDataRef("AirbusFBW/ENG2AILights");

            //qpac fcu toggles and push/pull commands
            qpac_command[QPAC_KEY_TO_CONFIG] = XPLMFindCommand("AirbusFBW/TOConfigPress");
            qpac_command[QPAC_KEY_TO_CONFIG] = XPLMFindCommand("AirbusFBW/TOConfigPress");
            qpac_command[QPAC_KEY_PUSH_ALT] = XPLMFindCommand("AirbusFBW/PushAltitude");
            qpac_command[QPAC_KEY_PULL_ALT] = XPLMFindCommand("AirbusFBW/PullAltitude");
            qpac_command[QPAC_KEY_PUSH_VS] = XPLMFindCommand("AirbusFBW/PushVSSel");
            qpac_command[QPAC_KEY_PULL_VS] = XPLMFindCommand("AirbusFBW/PullVSSel");
            qpac_command[QPAC_KEY_PUSH_HDG] = XPLMFindCommand("AirbusFBW/PushHDGSel");
            qpac_command[QPAC_KEY_PULL_HDG] = XPLMFindCommand("AirbusFBW/PullHDGSel");
            qpac_command[QPAC_KEY_PUSH_SPD] = XPLMFindCommand("AirbusFBW/PushSPDSel");
            qpac_command[QPAC_KEY_PULL_SPD] = XPLMFindCommand("AirbusFBW/PullSPDSel");
            qpac_command[QPAC_KEY_ATHR] = XPLMFindCommand("AirbusFBW/ATHRbutton");
            qpac_command[QPAC_KEY_APPR] = XPLMFindCommand("AirbusFBW/APPRbutton");
            qpac_command[QPAC_KEY_EXPED] = XPLMFindCommand("AirbusFBW/LOCbutton");
            qpac_command[QPAC_KEY_LOC] = XPLMFindCommand("AirbusFBW/EXPEDbutton");
            qpac_command[QPAC_KEY_ABRK_LOW] = XPLMFindCommand("AirbusFBW/AbrkLo");
            qpac_command[QPAC_KEY_ABRK_MED] = XPLMFindCommand("AirbusFBW/AbrkMed");
            qpac_command[QPAC_KEY_ABRK_MAX] = XPLMFindCommand("AirbusFBW/AbrkMax");
            // QPAC RMP1
            qpac_command[QPAC_KEY_RMP1_VHF1] = XPLMFindCommand("AirbusFBW/VHF1Capt");
            qpac_command[QPAC_KEY_RMP1_VHF2] = XPLMFindCommand("AirbusFBW/VHF2Capt");
            qpac_command[QPAC_KEY_RMP1_FREQ_DOWN_LRG] = XPLMFindCommand("AirbusFBW/RMP1FreqDownLrg");
            qpac_command[QPAC_KEY_RMP1_FREQ_DOWN_SML] = XPLMFindCommand("AirbusFBW/RMP1FreqDownSml");
            qpac_command[QPAC_KEY_RMP1_FREQ_UP_LRG] = XPLMFindCommand("AirbusFBW/RMP1FreqUpLrg");
            qpac_command[QPAC_KEY_RMP1_FREQ_UP_SML] = XPLMFindCommand("AirbusFBW/RMP1FreqUpSml");
            qpac_command[QPAC_KEY_RMP1_SWAP] = XPLMFindCommand("AirbusFBW/RMPSwapCapt");
            // QPAC RMP2
            qpac_command[QPAC_KEY_RMP2_VHF1] = XPLMFindCommand("AirbusFBW/VHF1Co");
            qpac_command[QPAC_KEY_RMP2_VHF2] = XPLMFindCommand("AirbusFBW/VHF2Co");
            qpac_command[QPAC_KEY_RMP2_FREQ_DOWN_LRG] = XPLMFindCommand("AirbusFBW/RMP2FreqDownLrg");
            qpac_command[QPAC_KEY_RMP2_FREQ_DOWN_SML] = XPLMFindCommand("AirbusFBW/RMP2FreqDownSml");
            qpac_command[QPAC_KEY_RMP2_FREQ_UP_LRG] = XPLMFindCommand("AirbusFBW/RMP2FreqUpLrg");
            qpac_command[QPAC_KEY_RMP2_FREQ_UP_SML] = XPLMFindCommand("AirbusFBW/RMP2FreqUpSml");
            qpac_command[QPAC_KEY_RMP2_SWAP] = XPLMFindCommand("AirbusFBW/RMPSwapCo");
            // QPAC RMP3
            qpac_command[QPAC_KEY_RMP3_VHF1] = XPLMFindCommand("AirbusFBW/VHF1RMP3");
            qpac_command[QPAC_KEY_RMP3_VHF2] = XPLMFindCommand("AirbusFBW/VHF2RMP3");
            qpac_command[QPAC_KEY_RMP3_FREQ_DOWN_LRG] = XPLMFindCommand("AirbusFBW/RMP3FreqDownLrg");
            qpac_command[QPAC_KEY_RMP3_FREQ_DOWN_SML] = XPLMFindCommand("AirbusFBW/RMP3FreqDownSml");
            qpac_command[QPAC_KEY_RMP3_FREQ_UP_LRG] = XPLMFindCommand("AirbusFBW/RMP3FreqUpLrg");
            qpac_command[QPAC_KEY_RMP3_FREQ_UP_SML] = XPLMFindCommand("AirbusFBW/RMP3FreqUpSml");
            qpac_command[QPAC_KEY_RMP3_SWAP] = XPLMFindCommand("AirbusFBW/RMP3Swap");
            // CAB PRESSURE
            qpac_command[QPAC_KEY_CAB_VS_UP] = XPLMFindCommand("AirbusFBW/CabVSUp");
            qpac_command[QPAC_KEY_CAB_VS_DOWN] = XPLMFindCommand("AirbusFBW/CabVSDown");
            // CHRONO
            qpac_command[QPAC_KEY_CHRONO_CAPT] = XPLMFindCommand("AirbusFBW/CaptChronoButton");
            qpac_command[QPAC_KEY_CHRONO_FO] = XPLMFindCommand("AirbusFBW/CoChronoButton");
            // MCDU1 Keys
            qpac_command[QPAC_KEY_MDCU1_INIT] = XPLMFindCommand("AirbusFBW/MCDU1Init");
            qpac_command[QPAC_KEY_MDCU1_DATA] = XPLMFindCommand("AirbusFBW/MCDU1Data");
            qpac_command[QPAC_KEY_MDCU1_MENU] = XPLMFindCommand("AirbusFBW/MCDU1Menu");
            qpac_command[QPAC_KEY_MDCU1_PERF] = XPLMFindCommand("AirbusFBW/MCDU1Perf");
            qpac_command[QPAC_KEY_MDCU1_PROG] = XPLMFindCommand("AirbusFBW/MCDU1Prog");
            qpac_command[QPAC_KEY_MDCU1_FPLN] = XPLMFindCommand("AirbusFBW/MCDU1Fpln");
            qpac_command[QPAC_KEY_MDCU1_DIR_TO] = XPLMFindCommand("AirbusFBW/MCDU1DirTo");
            qpac_command[QPAC_KEY_MDCU1_RAD_NAV] = XPLMFindCommand("AirbusFBW/MCDU1RadNav");
            qpac_command[QPAC_KEY_MDCU1_AIRPORT] = XPLMFindCommand("AirbusFBW/MCDU1Airport");
            qpac_command[QPAC_KEY_MDCU1_SLEW_UP] = XPLMFindCommand("AirbusFBW/MCDU1SlewUp");
            qpac_command[QPAC_KEY_MDCU1_SLEW_DOWN] = XPLMFindCommand("AirbusFBW/MCDU1SlewDown");
            qpac_command[QPAC_KEY_MDCU1_SLEW_LEFT] = XPLMFindCommand("AirbusFBW/MCDU1SlewLeft");
            qpac_command[QPAC_KEY_MDCU1_SLEW_RIGHT] = XPLMFindCommand("AirbusFBW/MCDU1SlewRight");
            qpac_command[QPAC_KEY_MDCU1_LSK1L] = XPLMFindCommand("AirbusFBW/MCDU1LSK1L");
            qpac_command[QPAC_KEY_MDCU1_LSK2L] = XPLMFindCommand("AirbusFBW/MCDU1LSK2L");
            qpac_command[QPAC_KEY_MDCU1_LSK3L] = XPLMFindCommand("AirbusFBW/MCDU1LSK3L");
            qpac_command[QPAC_KEY_MDCU1_LSK4L] = XPLMFindCommand("AirbusFBW/MCDU1LSK4L");
            qpac_command[QPAC_KEY_MDCU1_LSK5L] = XPLMFindCommand("AirbusFBW/MCDU1LSK5L");
            qpac_command[QPAC_KEY_MDCU1_LSK6L] = XPLMFindCommand("AirbusFBW/MCDU1LSK6L");
            qpac_command[QPAC_KEY_MDCU1_LSK1R] = XPLMFindCommand("AirbusFBW/MCDU1LSK1R");
            qpac_command[QPAC_KEY_MDCU1_LSK2R] = XPLMFindCommand("AirbusFBW/MCDU1LSK2R");
            qpac_command[QPAC_KEY_MDCU1_LSK3R] = XPLMFindCommand("AirbusFBW/MCDU1LSK3R");
            qpac_command[QPAC_KEY_MDCU1_LSK4R] = XPLMFindCommand("AirbusFBW/MCDU1LSK4R");
            qpac_command[QPAC_KEY_MDCU1_LSK5R] = XPLMFindCommand("AirbusFBW/MCDU1LSK5R");
            qpac_command[QPAC_KEY_MDCU1_LSK6R] = XPLMFindCommand("AirbusFBW/MCDU1LSK6R");
            qpac_command[QPAC_KEY_MDCU1_SLASH] = XPLMFindCommand("AirbusFBW/MCDU1KeySlash");
            qpac_command[QPAC_KEY_MDCU1_SPACE] = XPLMFindCommand("AirbusFBW/MCDU1KeySpace");
            qpac_command[QPAC_KEY_MDCU1_OVERFL] = XPLMFindCommand("AirbusFBW/MCDU1KeyOverfly");
            qpac_command[QPAC_KEY_MDCU1_DEL] = XPLMFindCommand("AirbusFBW/MCDU1KeyClear");
            qpac_command[QPAC_KEY_MDCU1_PLUS_M] = XPLMFindCommand("AirbusFBW/MCDU1KeyPM");
            qpac_command[QPAC_KEY_MDCU1_DOT] = XPLMFindCommand("AirbusFBW/MCDU1KeyDecimal");
            qpac_command[QPAC_KEY_MDCU1_0] = XPLMFindCommand("AirbusFBW/MCDU1Key0");
            qpac_command[QPAC_KEY_MDCU1_1] = XPLMFindCommand("AirbusFBW/MCDU1Key1");
            qpac_command[QPAC_KEY_MDCU1_2] = XPLMFindCommand("AirbusFBW/MCDU1Key2");
            qpac_command[QPAC_KEY_MDCU1_3] = XPLMFindCommand("AirbusFBW/MCDU1Key3");
            qpac_command[QPAC_KEY_MDCU1_4] = XPLMFindCommand("AirbusFBW/MCDU1Key4");
            qpac_command[QPAC_KEY_MDCU1_5] = XPLMFindCommand("AirbusFBW/MCDU1Key5");
            qpac_command[QPAC_KEY_MDCU1_6] = XPLMFindCommand("AirbusFBW/MCDU1Key6");
            qpac_command[QPAC_KEY_MDCU1_7] = XPLMFindCommand("AirbusFBW/MCDU1Key7");
            qpac_command[QPAC_KEY_MDCU1_8] = XPLMFindCommand("AirbusFBW/MCDU1Key8");
            qpac_command[QPAC_KEY_MDCU1_9] = XPLMFindCommand("AirbusFBW/MCDU1Key9");
            qpac_command[QPAC_KEY_MDCU1_A] = XPLMFindCommand("AirbusFBW/MCDU1KeyA");
            qpac_command[QPAC_KEY_MDCU1_B] = XPLMFindCommand("AirbusFBW/MCDU1KeyB");
            qpac_command[QPAC_KEY_MDCU1_C] = XPLMFindCommand("AirbusFBW/MCDU1KeyC");
            qpac_command[QPAC_KEY_MDCU1_D] = XPLMFindCommand("AirbusFBW/MCDU1KeyD");
            qpac_command[QPAC_KEY_MDCU1_E] = XPLMFindCommand("AirbusFBW/MCDU1KeyE");
            qpac_command[QPAC_KEY_MDCU1_F] = XPLMFindCommand("AirbusFBW/MCDU1KeyF");
            qpac_command[QPAC_KEY_MDCU1_G] = XPLMFindCommand("AirbusFBW/MCDU1KeyG");
            qpac_command[QPAC_KEY_MDCU1_H] = XPLMFindCommand("AirbusFBW/MCDU1KeyH");
            qpac_command[QPAC_KEY_MDCU1_I] = XPLMFindCommand("AirbusFBW/MCDU1KeyI");
            qpac_command[QPAC_KEY_MDCU1_J] = XPLMFindCommand("AirbusFBW/MCDU1KeyJ");
            qpac_command[QPAC_KEY_MDCU1_K] = XPLMFindCommand("AirbusFBW/MCDU1KeyK");
            qpac_command[QPAC_KEY_MDCU1_L] = XPLMFindCommand("AirbusFBW/MCDU1KeyL");
            qpac_command[QPAC_KEY_MDCU1_M] = XPLMFindCommand("AirbusFBW/MCDU1KeyM");
            qpac_command[QPAC_KEY_MDCU1_N] = XPLMFindCommand("AirbusFBW/MCDU1KeyN");
            qpac_command[QPAC_KEY_MDCU1_O] = XPLMFindCommand("AirbusFBW/MCDU1KeyO");
            qpac_command[QPAC_KEY_MDCU1_P] = XPLMFindCommand("AirbusFBW/MCDU1KeyP");
            qpac_command[QPAC_KEY_MDCU1_Q] = XPLMFindCommand("AirbusFBW/MCDU1KeyQ");
            qpac_command[QPAC_KEY_MDCU1_R] = XPLMFindCommand("AirbusFBW/MCDU1KeyR");
            qpac_command[QPAC_KEY_MDCU1_S] = XPLMFindCommand("AirbusFBW/MCDU1KeyS");
            qpac_command[QPAC_KEY_MDCU1_T] = XPLMFindCommand("AirbusFBW/MCDU1KeyT");
            qpac_command[QPAC_KEY_MDCU1_U] = XPLMFindCommand("AirbusFBW/MCDU1KeyU");
            qpac_command[QPAC_KEY_MDCU1_V] = XPLMFindCommand("AirbusFBW/MCDU1KeyV");
            qpac_command[QPAC_KEY_MDCU1_W] = XPLMFindCommand("AirbusFBW/MCDU1KeyW");
            qpac_command[QPAC_KEY_MDCU1_X] = XPLMFindCommand("AirbusFBW/MCDU1KeyX");
            qpac_command[QPAC_KEY_MDCU1_Y] = XPLMFindCommand("AirbusFBW/MCDU1KeyY");
            qpac_command[QPAC_KEY_MDCU1_Z] = XPLMFindCommand("AirbusFBW/MCDU1KeyZ");
            // MCDU2 Keys
            qpac_command[QPAC_KEY_MDCU2_INIT] = XPLMFindCommand("AirbusFBW/MCDU2Init");
            qpac_command[QPAC_KEY_MDCU2_DATA] = XPLMFindCommand("AirbusFBW/MCDU2Data");
            qpac_command[QPAC_KEY_MDCU2_MENU] = XPLMFindCommand("AirbusFBW/MCDU2Menu");
            qpac_command[QPAC_KEY_MDCU2_PERF] = XPLMFindCommand("AirbusFBW/MCDU2Perf");
            qpac_command[QPAC_KEY_MDCU2_PROG] = XPLMFindCommand("AirbusFBW/MCDU2Prog");
            qpac_command[QPAC_KEY_MDCU2_FPLN] = XPLMFindCommand("AirbusFBW/MCDU2Fpln");
            qpac_command[QPAC_KEY_MDCU2_DIR_TO] = XPLMFindCommand("AirbusFBW/MCDU2DirTo");
            qpac_command[QPAC_KEY_MDCU2_RAD_NAV] = XPLMFindCommand("AirbusFBW/MCDU2RadNav");
            qpac_command[QPAC_KEY_MDCU2_AIRPORT] = XPLMFindCommand("AirbusFBW/MCDU2Airport");
            qpac_command[QPAC_KEY_MDCU2_SLEW_UP] = XPLMFindCommand("AirbusFBW/MCDU2SlewUp");
            qpac_command[QPAC_KEY_MDCU2_SLEW_DOWN] = XPLMFindCommand("AirbusFBW/MCDU2SlewDown");
            qpac_command[QPAC_KEY_MDCU2_SLEW_LEFT] = XPLMFindCommand("AirbusFBW/MCDU2SlewLeft");
            qpac_command[QPAC_KEY_MDCU2_SLEW_RIGHT] = XPLMFindCommand("AirbusFBW/MCDU2SlewRight");
            qpac_command[QPAC_KEY_MDCU2_LSK1L] = XPLMFindCommand("AirbusFBW/MCDU2LSK1L");
            qpac_command[QPAC_KEY_MDCU2_LSK2L] = XPLMFindCommand("AirbusFBW/MCDU2LSK2L");
            qpac_command[QPAC_KEY_MDCU2_LSK3L] = XPLMFindCommand("AirbusFBW/MCDU2LSK3L");
            qpac_command[QPAC_KEY_MDCU2_LSK4L] = XPLMFindCommand("AirbusFBW/MCDU2LSK4L");
            qpac_command[QPAC_KEY_MDCU2_LSK5L] = XPLMFindCommand("AirbusFBW/MCDU2LSK5L");
            qpac_command[QPAC_KEY_MDCU2_LSK6L] = XPLMFindCommand("AirbusFBW/MCDU2LSK6L");
            qpac_command[QPAC_KEY_MDCU2_LSK1R] = XPLMFindCommand("AirbusFBW/MCDU2LSK1R");
            qpac_command[QPAC_KEY_MDCU2_LSK2R] = XPLMFindCommand("AirbusFBW/MCDU2LSK2R");
            qpac_command[QPAC_KEY_MDCU2_LSK3R] = XPLMFindCommand("AirbusFBW/MCDU2LSK3R");
            qpac_command[QPAC_KEY_MDCU2_LSK4R] = XPLMFindCommand("AirbusFBW/MCDU2LSK4R");
            qpac_command[QPAC_KEY_MDCU2_LSK5R] = XPLMFindCommand("AirbusFBW/MCDU2LSK5R");
            qpac_command[QPAC_KEY_MDCU2_LSK6R] = XPLMFindCommand("AirbusFBW/MCDU2LSK6R");
            qpac_command[QPAC_KEY_MDCU2_SLASH] = XPLMFindCommand("AirbusFBW/MCDU2KeySlash");
            qpac_command[QPAC_KEY_MDCU2_SPACE] = XPLMFindCommand("AirbusFBW/MCDU2KeySpace");
            qpac_command[QPAC_KEY_MDCU2_OVERFL] = XPLMFindCommand("AirbusFBW/MCDU2KeyOverfly");
            qpac_command[QPAC_KEY_MDCU2_DEL] = XPLMFindCommand("AirbusFBW/MCDU2KeyClear");
            qpac_command[QPAC_KEY_MDCU2_PLUS_M] = XPLMFindCommand("AirbusFBW/MCDU2KeyPM");
            qpac_command[QPAC_KEY_MDCU2_DOT] = XPLMFindCommand("AirbusFBW/MCDU2KeyDecimal");
            qpac_command[QPAC_KEY_MDCU2_0] = XPLMFindCommand("AirbusFBW/MCDU2Key0");
            qpac_command[QPAC_KEY_MDCU2_1] = XPLMFindCommand("AirbusFBW/MCDU2Key1");
            qpac_command[QPAC_KEY_MDCU2_2] = XPLMFindCommand("AirbusFBW/MCDU2Key2");
            qpac_command[QPAC_KEY_MDCU2_3] = XPLMFindCommand("AirbusFBW/MCDU2Key3");
            qpac_command[QPAC_KEY_MDCU2_4] = XPLMFindCommand("AirbusFBW/MCDU2Key4");
            qpac_command[QPAC_KEY_MDCU2_5] = XPLMFindCommand("AirbusFBW/MCDU2Key5");
            qpac_command[QPAC_KEY_MDCU2_6] = XPLMFindCommand("AirbusFBW/MCDU2Key6");
            qpac_command[QPAC_KEY_MDCU2_7] = XPLMFindCommand("AirbusFBW/MCDU2Key7");
            qpac_command[QPAC_KEY_MDCU2_8] = XPLMFindCommand("AirbusFBW/MCDU2Key8");
            qpac_command[QPAC_KEY_MDCU2_9] = XPLMFindCommand("AirbusFBW/MCDU2Key9");
            qpac_command[QPAC_KEY_MDCU2_A] = XPLMFindCommand("AirbusFBW/MCDU2KeyA");
            qpac_command[QPAC_KEY_MDCU2_B] = XPLMFindCommand("AirbusFBW/MCDU2KeyB");
            qpac_command[QPAC_KEY_MDCU2_C] = XPLMFindCommand("AirbusFBW/MCDU2KeyC");
            qpac_command[QPAC_KEY_MDCU2_D] = XPLMFindCommand("AirbusFBW/MCDU2KeyD");
            qpac_command[QPAC_KEY_MDCU2_E] = XPLMFindCommand("AirbusFBW/MCDU2KeyE");
            qpac_command[QPAC_KEY_MDCU2_F] = XPLMFindCommand("AirbusFBW/MCDU2KeyF");
            qpac_command[QPAC_KEY_MDCU2_G] = XPLMFindCommand("AirbusFBW/MCDU2KeyG");
            qpac_command[QPAC_KEY_MDCU2_H] = XPLMFindCommand("AirbusFBW/MCDU2KeyH");
            qpac_command[QPAC_KEY_MDCU2_I] = XPLMFindCommand("AirbusFBW/MCDU2KeyI");
            qpac_command[QPAC_KEY_MDCU2_J] = XPLMFindCommand("AirbusFBW/MCDU2KeyJ");
            qpac_command[QPAC_KEY_MDCU2_K] = XPLMFindCommand("AirbusFBW/MCDU2KeyK");
            qpac_command[QPAC_KEY_MDCU2_L] = XPLMFindCommand("AirbusFBW/MCDU2KeyL");
            qpac_command[QPAC_KEY_MDCU2_M] = XPLMFindCommand("AirbusFBW/MCDU2KeyM");
            qpac_command[QPAC_KEY_MDCU2_N] = XPLMFindCommand("AirbusFBW/MCDU2KeyN");
            qpac_command[QPAC_KEY_MDCU2_O] = XPLMFindCommand("AirbusFBW/MCDU2KeyO");
            qpac_command[QPAC_KEY_MDCU2_P] = XPLMFindCommand("AirbusFBW/MCDU2KeyP");
            qpac_command[QPAC_KEY_MDCU2_Q] = XPLMFindCommand("AirbusFBW/MCDU2KeyQ");
            qpac_command[QPAC_KEY_MDCU2_R] = XPLMFindCommand("AirbusFBW/MCDU2KeyR");
            qpac_command[QPAC_KEY_MDCU2_S] = XPLMFindCommand("AirbusFBW/MCDU2KeyS");
            qpac_command[QPAC_KEY_MDCU2_T] = XPLMFindCommand("AirbusFBW/MCDU2KeyT");
            qpac_command[QPAC_KEY_MDCU2_U] = XPLMFindCommand("AirbusFBW/MCDU2KeyU");
            qpac_command[QPAC_KEY_MDCU2_V] = XPLMFindCommand("AirbusFBW/MCDU2KeyV");
            qpac_command[QPAC_KEY_MDCU2_W] = XPLMFindCommand("AirbusFBW/MCDU2KeyW");
            qpac_command[QPAC_KEY_MDCU2_X] = XPLMFindCommand("AirbusFBW/MCDU2KeyX");
            qpac_command[QPAC_KEY_MDCU2_Y] = XPLMFindCommand("AirbusFBW/MCDU2KeyY");
            qpac_command[QPAC_KEY_MDCU2_Z] = XPLMFindCommand("AirbusFBW/MCDU2KeyZ");

            qpac_command[QPAC_KEY_AP1_PUSH] = XPLMFindCommand("airbus_qpac/ap1_push");
            qpac_command[QPAC_KEY_AP2_PUSH] = XPLMFindCommand("airbus_qpac/ap2_push");
            qpac_command[QPAC_KEY_FD1_PUSH] = XPLMFindCommand("airbus_qpac/fd1_push");
            qpac_command[QPAC_KEY_FD2_PUSH] = XPLMFindCommand("airbus_qpac/fd2_push");

            for (i=0; i<QPAC_SD_LINES; i++) {
                sprintf(buf, "AirbusFBW/SDline%da", i+1);
                qpac_sd_line_amber[i] = XPLMFindDataRef(buf);
                sprintf(buf, "AirbusFBW/SDline%db", i+1);
                qpac_sd_line_blue[i] = XPLMFindDataRef(buf);
                sprintf(buf, "AirbusFBW/SDline%dg", i+1);
                qpac_sd_line_green[i] = XPLMFindDataRef(buf);
                sprintf(buf, "AirbusFBW/SDline%dr", i+1);
                qpac_sd_line_red[i] = XPLMFindDataRef(buf);
                sprintf(buf, "AirbusFBW/SDline%dw", i+1);
                qpac_sd_line_white[i] = XPLMFindDataRef(buf);
            }

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
			if (value>=0 && value <= QPAC_KEY_MAX && qpac_ready) {
						XPLMCommandOnce(qpac_command[(int)value]);
			}
			break;
    }
}

