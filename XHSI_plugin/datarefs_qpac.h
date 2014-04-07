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
extern XPLMDataRef qpac_appr_illuminated;
extern XPLMDataRef qpac_appr_type;
extern XPLMDataRef qpac_appr_mda;
extern XPLMDataRef qpac_alt_is_cstr;
extern XPLMDataRef qpac_constraint_alt;
// Auto-Thrust and speed
extern XPLMDataRef qpac_athr_mode;
extern XPLMDataRef qpac_athr_mode2;
extern XPLMDataRef qpac_athr_limited;
extern XPLMDataRef qpac_thr_lever_mode;
extern XPLMDataRef qpac_fma_thr_warning;
extern XPLMDataRef qpac_flex_temp;
// ILS Sig and Deviation Capt. and FO
extern XPLMDataRef qpac_loc_val_capt;
extern XPLMDataRef qpac_loc_on_capt;
extern XPLMDataRef qpac_gs_val_capt;
extern XPLMDataRef qpac_gs_on_capt;
extern XPLMDataRef qpac_loc_val_fo;
extern XPLMDataRef qpac_loc_on_fo;
extern XPLMDataRef qpac_gs_val_fo;
extern XPLMDataRef qpac_gs_on_fo;
extern XPLMDataRef qpac_ils_crs;
extern XPLMDataRef qpac_ils_crs_dev;
extern XPLMDataRef qpac_ils_1;
extern XPLMDataRef qpac_ils_2;
extern XPLMDataRef qpac_ils_3;
// FD
extern XPLMDataRef qpac_fd1;
extern XPLMDataRef qpac_fd2;
extern XPLMDataRef qpac_fd1_ver_bar;
extern XPLMDataRef qpac_fd1_hor_bar;
extern XPLMDataRef qpac_fd2_ver_bar;
extern XPLMDataRef qpac_fd2_hor_bar;
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

extern int qpac_ready;

// global functions
float	checkQpacCallback(float, float, int, void *);

#endif /* DATAREFS_QPAC_H_ */
