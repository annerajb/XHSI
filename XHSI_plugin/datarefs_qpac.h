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
// Left and right ailerons
extern XPLMDataRef qpac_right_aileron_pos;
extern XPLMDataRef qpac_left_aileron_pos;
// Spoilers
extern XPLMDataRef qpac_spoilers_array;
// ELAC and SEC
extern XPLMDataRef qpac_fcc_avail_array;
// Rudder limits
extern XPLMDataRef qpac_rudder_limit_pos;
// Hydraulics
extern XPLMDataRef qpac_hyd_pressure_array;
extern XPLMDataRef qpac_hyd_pump_array;
extern XPLMDataRef qpac_hyd_rat_mode;
extern XPLMDataRef qpac_hyd_y_elec_mode;
extern XPLMDataRef qpac_hyd_ptu_mode;
extern XPLMDataRef qpac_hyd_sys_qty_array;
// fire valve : sim/cockpit2/engine/fire_estinguisher_on[0,1] boolean
// Cabin Pressure
extern XPLMDataRef qpac_cabin_delta_p;
extern XPLMDataRef qpac_cabin_alt;
extern XPLMDataRef qpac_cabin_vs;
extern XPLMDataRef qpac_outflow_valve;
// ENG lower ECAM
extern XPLMDataRef qpac_ewd_start_mode;
extern XPLMDataRef qpac_start_valve_array;
extern XPLMDataRef qpac_nacelle_temp_array;
// COND
extern XPLMDataRef qpac_cond_hot_air_valve;
extern XPLMDataRef qpac_cond_cockpit_trim;
extern XPLMDataRef qpac_cond_zone1_trim;
extern XPLMDataRef qpac_cond_zone2_trim;
// Bleed
extern XPLMDataRef qpac_bleed_intercon;
extern XPLMDataRef qpac_bleed_x;
extern XPLMDataRef qpac_bleed_apu;
extern XPLMDataRef qpac_bleed_eng1;
extern XPLMDataRef qpac_bleed_eng2;
extern XPLMDataRef qpac_bleed_eng1_hp;
extern XPLMDataRef qpac_bleed_eng2_hp;
extern XPLMDataRef qpac_bleed_pack1_fcu;
extern XPLMDataRef qpac_bleed_pack2_fcu;
extern XPLMDataRef qpac_bleed_pack1_flow;
extern XPLMDataRef qpac_bleed_pack2_flow;
extern XPLMDataRef qpac_bleed_pack1_temp;
extern XPLMDataRef qpac_bleed_pack2_temp;
extern XPLMDataRef qpac_bleed_ram_air;
// APU
extern XPLMDataRef qpac_apu_egt;
extern XPLMDataRef qpac_apu_egt_limit;
// ELEC
extern XPLMDataRef qpac_elec_ext_pow_box;
extern XPLMDataRef qpac_elec_ac_cross_connect;
extern XPLMDataRef qpac_elec_connect_left;
extern XPLMDataRef qpac_elec_connect_center;
extern XPLMDataRef qpac_elec_connect_right;
extern XPLMDataRef qpac_elec_battery_supply;
extern XPLMDataRef qpac_elec_connectors;
extern XPLMDataRef qpac_elec_ohp_array;
extern XPLMDataRef qpac_elec_apu_box;
// FUEL
extern XPLMDataRef qpac_fuel_pump_array;
extern XPLMDataRef qpac_fuel_xfv_array ;
extern XPLMDataRef qpac_fuel_eng_lp_valve_array;
extern XPLMDataRef qpac_fuel_tv_array;
// ECAM SD page selection
extern XPLMDataRef qpac_sd_page;
extern XPLMDataRef qpac_clear_illuminated;

//qpac fcu toggles and push/pull commands
extern XPLMCommandRef qpac_to_config_press;
extern XPLMCommandRef qpac_push_alt;
extern XPLMCommandRef qpac_pull_alt;
extern XPLMCommandRef qpac_push_vs;
extern XPLMCommandRef qpac_pull_vs;
extern XPLMCommandRef qpac_push_hdg;
extern XPLMCommandRef qpac_pull_hdg;
extern XPLMCommandRef qpac_push_spd;
extern XPLMCommandRef qpac_pull_spd;
extern XPLMCommandRef qpac_athr_toggle;
extern XPLMCommandRef qpac_appr_toggle;
extern XPLMCommandRef qpac_loc_toggle;
extern XPLMCommandRef qpac_exped_toggle;
extern XPLMCommandRef qpac_abrk_low_toggle;
extern XPLMCommandRef qpac_abrk_med_toggle;
extern XPLMCommandRef qpac_abrk_max_toggle;


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
float checkQpacCallback(float, float, int, void *);
void writeQpacDataRef(int, float);

#endif /* DATAREFS_QPAC_H_ */
