/*
 * datarefs_qpac.h
 *
 *  Created on: 19 févr. 2014
 *      Author: Nicolas Carel
 */

#ifndef DATAREFS_QPAC_H_
#define DATAREFS_QPAC_H_


// global vars

extern XPLMDataRef qpac_plugin_status;

// Autopilot
extern XPLMDataRef qpac_ap1;
extern XPLMDataRef qpac_ap2;
extern XPLMDataRef qpac_ap_phase;
extern XPLMDataRef qpac_presel_crz;
extern XPLMDataRef qpac_presel_clb;
extern XPLMDataRef qpac_presel_mach;
extern XPLMDataRef qpac_ap_vertical_mode;
extern XPLMDataRef qpac_ap_vertical_armed;
extern XPLMDataRef qpac_ap_lateral_mode;
extern XPLMDataRef qpac_ap_lateral_armed;
extern XPLMDataRef qpac_npa_valid;
extern XPLMDataRef qpac_npa_no_points;
extern XPLMDataRef qpac_npa_slope;
extern XPLMDataRef qpac_appr_illuminated;
extern XPLMDataRef qpac_loc_illuminated;
extern XPLMDataRef qpac_appr_type;
extern XPLMDataRef qpac_appr_mda;
extern XPLMDataRef qpac_alt_is_cstr;
extern XPLMDataRef qpac_constraint_alt;
// FCU
extern XPLMDataRef qpac_fcu_hdg_trk;
extern XPLMDataRef qpac_fcu_metric_alt;
extern XPLMDataRef qpac_fcu_vs_dashed;
extern XPLMDataRef qpac_fcu_spd_dashed;
extern XPLMDataRef qpac_fcu_spd_managed;
extern XPLMDataRef qpac_fcu_hdg_dashed;
extern XPLMDataRef qpac_fcu_hdg_managed;
extern XPLMDataRef qpac_fcu_alt_managed;
// Auto-Thrust and speed
extern XPLMDataRef qpac_athr_mode;
extern XPLMDataRef qpac_athr_mode2;
extern XPLMDataRef qpac_athr_limited;
extern XPLMDataRef qpac_thr_lever_mode;
extern XPLMDataRef qpac_fma_thr_warning;
extern XPLMDataRef qpac_flex_temp;
extern XPLMDataRef qpac_thr_rating_type;
extern XPLMDataRef qpac_thr_rating_n1;
extern XPLMDataRef qpac_throttle_input;
// ILS Sig and Deviation Capt. and FO
extern XPLMDataRef qpac_loc_val_capt;
extern XPLMDataRef qpac_loc_on_capt;
extern XPLMDataRef qpac_gs_val_capt;
extern XPLMDataRef qpac_gs_on_capt;
extern XPLMDataRef qpac_ils_on_capt;
extern XPLMDataRef qpac_loc_val_fo;
extern XPLMDataRef qpac_loc_on_fo;
extern XPLMDataRef qpac_gs_val_fo;
extern XPLMDataRef qpac_gs_on_fo;
extern XPLMDataRef qpac_ils_on_fo;
extern XPLMDataRef qpac_ils_crs;
extern XPLMDataRef qpac_ils_1;
extern XPLMDataRef qpac_ils_2;
extern XPLMDataRef qpac_ils_3;
// FD
extern XPLMDataRef qpac_fd1;
extern XPLMDataRef qpac_fd2;
extern XPLMDataRef qpac_fd1_ver_bar;
extern XPLMDataRef qpac_fd1_hor_bar;
extern XPLMDataRef qpac_fd1_yaw_bar;
extern XPLMDataRef qpac_fd2_ver_bar;
extern XPLMDataRef qpac_fd2_hor_bar;
extern XPLMDataRef qpac_fd2_yaw_bar;
// Baro
extern XPLMDataRef qpac_baro_std_capt;
extern XPLMDataRef qpac_baro_unit_capt;
extern XPLMDataRef qpac_baro_hide_capt;
extern XPLMDataRef qpac_baro_std_fo;
extern XPLMDataRef qpac_baro_unit_fo;
extern XPLMDataRef qpac_baro_hide_fo;
// V Speeds
extern XPLMDataRef qpac_v1_value;
extern XPLMDataRef qpac_v1;
extern XPLMDataRef qpac_vr;
extern XPLMDataRef qpac_vmo;
extern XPLMDataRef qpac_vls;
extern XPLMDataRef qpac_vf;
extern XPLMDataRef qpac_vs;
extern XPLMDataRef qpac_v_green_dot;
extern XPLMDataRef qpac_alpha_prot;
extern XPLMDataRef qpac_alpha_max;
// Failures
extern XPLMDataRef qpac_capt_hdg_valid;
extern XPLMDataRef qpac_capt_att_valid;
extern XPLMDataRef qpac_capt_ias_valid;
extern XPLMDataRef qpac_capt_alt_valid;
extern XPLMDataRef qpac_co_hdg_valid;
extern XPLMDataRef qpac_co_att_valid;
extern XPLMDataRef qpac_co_ias_valid;
extern XPLMDataRef qpac_co_alt_valid;
// EFIS
extern XPLMDataRef qpac_capt_efis_nd_mode;
extern XPLMDataRef qpac_co_efis_nd_mode;
extern XPLMDataRef qpac_capt_efis_nd_range;
extern XPLMDataRef qpac_co_efis_nd_range;
// Brakes
// 0=OFF, 1=Engaged, 2=DECEL
extern XPLMDataRef qpac_autobrake_low;
extern XPLMDataRef qpac_autobrake_med;
extern XPLMDataRef qpac_autobrake_max;
// Flaps and slats
extern XPLMDataRef qpac_flaps_request_pos;
extern XPLMDataRef qpac_slats_request_pos;

extern int qpac_ready;

/* QPAC Versions :
 *  major version number is x100
 *  0 = Not ready
 *  110 = QPAC Freeware 1.1
 *  150 = PeterAircraft A320
 *  202 = QPAC 2.02 Final Basic
 */
#define QPAC_VERSION_1_1 110
#define QPAC_VERSION_PA  150
#define QPAC_VERSION_2_02_BASIC 202
extern int qpac_version;

// global functions
float	checkQpacCallback(float, float, int, void *);

#endif /* DATAREFS_QPAC_H_ */
