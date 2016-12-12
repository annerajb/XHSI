/*
 * datarefs_jar_a320neo.h
 *
 *  Created on: 9 juin 2014
 *      Author: carel
 */

#ifndef DATAREFS_JAR_A320NEO_H_
#define DATAREFS_JAR_A320NEO_H_

extern XPLMDataRef jar_a320_neo_plugin_status;

// FCU
extern XPLMDataRef jar_a320_neo_baro_hpa;
extern XPLMDataRef jar_a320_neo_fcu_hdg_trk;
extern XPLMDataRef jar_a320_neo_fcu_metric_alt;
extern XPLMDataRef jar_a320_neo_vs_dashed;
extern XPLMDataRef jar_a320_neo_hdg_dashed;
extern XPLMDataRef jar_a320_neo_hdg_managed;
extern XPLMDataRef jar_a320_neo_lvlch_dot;
extern XPLMDataRef jar_a320_neo_spd_managed;
extern XPLMDataRef jar_a320_neo_alt_managed;

// Baro
extern XPLMDataRef jar_a320_neo_baro_flash;
// integer

// Autopilot and FD
extern XPLMDataRef jar_a320_neo_ap_phase;
extern XPLMDataRef jar_a320_neo_ap1;
extern XPLMDataRef jar_a320_neo_ap2;
extern XPLMDataRef jar_a320_neo_ils;
extern XPLMDataRef jar_a320_neo_fd;

// JARDesign A320Neo is using X-Plane flight director but there are datarefs...
// FD Bars
XPLMDataRef jar_a320_neo_fd_h_show;
// integer
XPLMDataRef jar_a320_neo_fd_pitch;
// float
XPLMDataRef jar_a320_neo_fd_v_show;
// integer
XPLMDataRef jar_a320_neo_fd_roll;
// float
XPLMDataRef jar_a320_neo_fd_y_show;
// integer
XPLMDataRef jar_a320_neo_fd_yaw_dot;
// float



// Vertical and horizontal modes
extern XPLMDataRef jar_a320_neo_com_mode;
// integer enum_values {"", "LAND", "FLARE", "ROLL OUT", "FINAL APP"}

extern XPLMDataRef jar_a320_neo_vert_mode;
// integer enum_values {"", "SRS", "CLB", "OP CLB", "ALT*", "ALT CST*", "ALT", "ALT CST", "ALT CRZ", "DES", "OP DES", "GS*", "GS", "V/S", "FPA"}

extern XPLMDataRef jar_a320_neo_vert_wait;
// integer enum_values {"", "CLB", "ALT", "ALT", "DES", "G/S", "FINAL", "ALT G/S", "ALT G/S", "ALT FINAL", "ALT FINAL", "DES GS", "DES FINAL"}

extern XPLMDataRef jar_a320_neo_lat_mode;
// integer enum_values {"", "RWY", " RWY TRK", "HDG", "TRACK", "NAV", "LOC*", "LOC", "APP NAV", "GA TRK", ""}

extern XPLMDataRef jar_a320_neo_lat_wait;
// integer enum_values {"", "NAV", "LOC", "APP NAV", "NAV", "LOC", "APP NAV", "NAV", "LOC", "APP NAV"}

// Landing capabilities
extern XPLMDataRef jar_a320_neo_fma_cat_mode;
extern XPLMDataRef jar_a320_neo_fma_dual_mode;
extern XPLMDataRef jar_a320_neo_fma_dh_mode;
//  {"", "CAT 1", "CAT 2", "CAT 3", "SINGLE", "DUAL", "MDA", "DH", "NO DH"}

extern XPLMDataRef jar_a320_neo_fma_mda_alt;
extern XPLMDataRef jar_a320_neo_fma_dh_alt;

// Approach
extern XPLMDataRef jar_a320_neo_ap_appr_type;
// String !!!
extern XPLMDataRef jar_a320_neo_ap_appr_illuminated;
// integer
extern XPLMDataRef jar_a320_neo_ap_loc_illuminated;
// integer


// A/THR
extern XPLMDataRef jar_a320_neo_athr_mode;
// integer enum_values {"", "MAN TOGA", "MAN FLX", "MAN MCT", "MAN THR", "THR MCT", "THR CLB", "THR IDLE", "THR LVR", "SPEED", "MACH", "A.FLOOR", "TOGALK", ""}

extern XPLMDataRef jar_a320_neo_thr_mode;
// integer enum_values {"", "A/THR" (armed), "A/THR" (active) }

extern XPLMDataRef jar_a320_neo_flex_t;
// integer

// ATA 36 PNEUMATIC
// Bleed Air
extern XPLMDataRef jar_a320_neo_bleed_apu_bleed_valve;
extern XPLMDataRef jar_a320_neo_bleed_eng1_bleed_knob;
extern XPLMDataRef jar_a320_neo_bleed_eng1_bleed_valve;
extern XPLMDataRef jar_a320_neo_bleed_eng1_bleed_temp;
extern XPLMDataRef jar_a320_neo_bleed_eng1_bleed_hp_valve;
extern XPLMDataRef jar_a320_neo_bleed_eng1_bleed_psi;
extern XPLMDataRef jar_a320_neo_bleed_eng2_bleed_knob;
extern XPLMDataRef jar_a320_neo_bleed_eng2_bleed_valve;
extern XPLMDataRef jar_a320_neo_bleed_eng2_bleed_temp;
extern XPLMDataRef jar_a320_neo_bleed_eng2_bleed_hp_valve;
extern XPLMDataRef jar_a320_neo_bleed_eng2_bleed_psi;
extern XPLMDataRef jar_a320_neo_bleed_cross_valve;

// Brakes
extern XPLMDataRef jar_a320_neo_autobrake_low;
extern XPLMDataRef jar_a320_neo_autobrake_med;
extern XPLMDataRef jar_a320_neo_autobrake_max;
extern XPLMDataRef jar_a320_neo_autobrake_on;
extern XPLMDataRef jar_a320_neo_brakes_accu_press;
extern XPLMDataRef jar_a320_neo_brakes_left_press;
extern XPLMDataRef jar_a320_neo_brakes_right_press;
extern XPLMDataRef jar_a320_neo_brakes_mode_na;

// ATA 21 Conditioning
extern XPLMDataRef jar_a320_neo_cond_aft_duct;
extern XPLMDataRef jar_a320_neo_cond_aft_temp;
extern XPLMDataRef jar_a320_neo_cond_aft_trm_valve;
extern XPLMDataRef jar_a320_neo_cond_cargo_aft_duct;
extern XPLMDataRef jar_a320_neo_cond_cargo_aft_temp;
extern XPLMDataRef jar_a320_neo_cond_cargo_aft_trm_valve;
extern XPLMDataRef jar_a320_neo_cond_cargo_aft_valve;
extern XPLMDataRef jar_a320_neo_cond_cargo_fwd_duct;
extern XPLMDataRef jar_a320_neo_cond_cargo_fwd_temp;
extern XPLMDataRef jar_a320_neo_cond_cargo_fwd_trm_valve;
extern XPLMDataRef jar_a320_neo_cond_cargo_fwd_valve;
extern XPLMDataRef jar_a320_neo_cond_cockpit_duct;
extern XPLMDataRef jar_a320_neo_cond_cockpit_temp;
extern XPLMDataRef jar_a320_neo_cond_cockpit_trm_valve;
extern XPLMDataRef jar_a320_neo_cond_econ_flow;
extern XPLMDataRef jar_a320_neo_cond_fwd_duct;
extern XPLMDataRef jar_a320_neo_cond_fwd_temp;
extern XPLMDataRef jar_a320_neo_cond_fwd_trm_valve;
extern XPLMDataRef jar_a320_neo_cond_hot_air;
extern XPLMDataRef jar_a320_neo_cond_cargo_hot_air;
extern XPLMDataRef jar_a320_neo_cond_pack1;
extern XPLMDataRef jar_a320_neo_cond_pack12_line;
extern XPLMDataRef jar_a320_neo_cond_pack1_comp_deg;
extern XPLMDataRef jar_a320_neo_cond_pack1_f;
extern XPLMDataRef jar_a320_neo_cond_pack1_flow;
extern XPLMDataRef jar_a320_neo_cond_pack1_line;
extern XPLMDataRef jar_a320_neo_cond_pack1_ndl;
extern XPLMDataRef jar_a320_neo_cond_pack1_out_deg;
extern XPLMDataRef jar_a320_neo_cond_pack2;
extern XPLMDataRef jar_a320_neo_cond_pack2_comp_deg;
extern XPLMDataRef jar_a320_neo_cond_pack2_f;
extern XPLMDataRef jar_a320_neo_cond_pack2_flow;
extern XPLMDataRef jar_a320_neo_cond_pack2_line;
extern XPLMDataRef jar_a320_neo_cond_pack2_ndl;
extern XPLMDataRef jar_a320_neo_cond_pack2_out_deg;
extern XPLMDataRef jar_a320_neo_cond_ram_air;

// Doors
extern XPLMDataRef jar_a320_neo_doors_c_b_kn;
extern XPLMDataRef jar_a320_neo_doors_c_b_now;
extern XPLMDataRef jar_a320_neo_doors_c_f_kn;
extern XPLMDataRef jar_a320_neo_doors_c_f_now;
extern XPLMDataRef jar_a320_neo_doors_p_b_l_kn;
extern XPLMDataRef jar_a320_neo_doors_p_b_l_now;
extern XPLMDataRef jar_a320_neo_doors_p_b_r_kn;
extern XPLMDataRef jar_a320_neo_doors_p_b_r_now;
extern XPLMDataRef jar_a320_neo_doors_p_f_l_kn;
extern XPLMDataRef jar_a320_neo_doors_p_f_l_now;
extern XPLMDataRef jar_a320_neo_doors_p_f_r_kn;
extern XPLMDataRef jar_a320_neo_doors_p_f_r_now;

// ATA 24 Electrics
extern XPLMDataRef jar_a320_neo_elec_ac1_source;
extern XPLMDataRef jar_a320_neo_elec_ac2_source;
extern XPLMDataRef jar_a320_neo_elec_ac_ess;
extern XPLMDataRef jar_a320_neo_elec_ac_ess_alt;
extern XPLMDataRef jar_a320_neo_elec_ac_ess_shed;
extern XPLMDataRef jar_a320_neo_elec_apu_gen_on;
extern XPLMDataRef jar_a320_neo_elec_bat1_amp;
extern XPLMDataRef jar_a320_neo_elec_bat1_volt;
extern XPLMDataRef jar_a320_neo_elec_bat1_on;
extern XPLMDataRef jar_a320_neo_elec_bat2_amp;
extern XPLMDataRef jar_a320_neo_elec_bat2_volt;
extern XPLMDataRef jar_a320_neo_elec_bat2_on;
extern XPLMDataRef jar_a320_neo_elec_bus_tie;
extern XPLMDataRef jar_a320_neo_elec_commrc;
extern XPLMDataRef jar_a320_neo_elec_dc1;
extern XPLMDataRef jar_a320_neo_elec_dc2;
extern XPLMDataRef jar_a320_neo_elec_dcbus;
extern XPLMDataRef jar_a320_neo_elec_dc_ess;
extern XPLMDataRef jar_a320_neo_elec_dc_ess_shed;
extern XPLMDataRef jar_a320_neo_elec_dc_some_on;
extern XPLMDataRef jar_a320_neo_elec_emer;
extern XPLMDataRef jar_a320_neo_elec_ext_hz;
extern XPLMDataRef jar_a320_neo_elec_ext_volt;
extern XPLMDataRef jar_a320_neo_elec_galley;
extern XPLMDataRef jar_a320_neo_elec_gen1_hz;
extern XPLMDataRef jar_a320_neo_elec_gen1_line_on;
extern XPLMDataRef jar_a320_neo_elec_gen1_per;
extern XPLMDataRef jar_a320_neo_elec_gen1_volt;
extern XPLMDataRef jar_a320_neo_elec_gen2_hz;
extern XPLMDataRef jar_a320_neo_elec_gen2_per;
extern XPLMDataRef jar_a320_neo_elec_gen2_volt;
extern XPLMDataRef jar_a320_neo_elec_apu_hz;
extern XPLMDataRef jar_a320_neo_elec_apu_per;
extern XPLMDataRef jar_a320_neo_elec_apu_volt;
extern XPLMDataRef jar_a320_neo_elec_gen_emer_hz;
extern XPLMDataRef jar_a320_neo_elec_gen_emer_volt;
extern XPLMDataRef jar_a320_neo_elec_gpu_av;
extern XPLMDataRef jar_a320_neo_elec_gpu_on;
extern XPLMDataRef jar_a320_neo_elec_lft_gen_on;
extern XPLMDataRef jar_a320_neo_elec_man_rat_cover;
extern XPLMDataRef jar_a320_neo_elec_man_rat_on;
extern XPLMDataRef jar_a320_neo_elec_rat_av;
extern XPLMDataRef jar_a320_neo_elec_rat_on;
extern XPLMDataRef jar_a320_neo_elec_rgh_gen_on;
extern XPLMDataRef jar_a320_neo_elec_tr1_amp;
extern XPLMDataRef jar_a320_neo_elec_tr1_volt;
extern XPLMDataRef jar_a320_neo_elec_tr2_amp;
extern XPLMDataRef jar_a320_neo_elec_tr2_volt;
extern XPLMDataRef jar_a320_neo_elec_tr_em_amp;
extern XPLMDataRef jar_a320_neo_elec_tr_em_volt;

// Engines
extern XPLMDataRef jar_a320_neo_eng_1_nac_temp;
extern XPLMDataRef jar_a320_neo_eng_2_nac_temp;
extern XPLMDataRef jar_a320_neo_eng_1_oil_press;
extern XPLMDataRef jar_a320_neo_eng_2_oil_press;
extern XPLMDataRef jar_a320_neo_eng_1_oil_qt;
extern XPLMDataRef jar_a320_neo_eng_2_oil_qt;
extern XPLMDataRef jar_a320_neo_eng_1_oil_temp;
extern XPLMDataRef jar_a320_neo_eng_2_oil_temp;
extern XPLMDataRef jar_a320_neo_eng_1_fire_pb;
extern XPLMDataRef jar_a320_neo_eng_1_fuel_valve;
extern XPLMDataRef jar_a320_neo_eng_2_fire_pb;
extern XPLMDataRef jar_a320_neo_eng_2_fuel_valve;

// FMS
extern XPLMDataRef jar_a320_neo_fms_tr_alt;
// integer : transition altitude
// V-DEV indicator
extern XPLMDataRef jar_a320_neo_yoyo_on;
// integer globalPropertyi("sim/custom/yoyo_on"))
extern XPLMDataRef jar_a320_neo_vdev;
// float	globalPropertyf("sim/custom/vdev"))

// V-Speeds (absolute values in knots)
extern XPLMDataRef jar_a320_neo_vls;
extern XPLMDataRef jar_a320_neo_vamax;
extern XPLMDataRef jar_a320_neo_vaprot;
extern XPLMDataRef jar_a320_neo_vmax;
extern XPLMDataRef jar_a320_neo_v1;
extern XPLMDataRef jar_a320_neo_vr;
extern XPLMDataRef jar_a320_neo_vgrdot;

// EFIS
extern XPLMDataRef jar_a320_neo_nd_mode;

// MCDU
#define JAR_A320_MAX_MCDU_KEYS 70
extern XPLMDataRef jar_a320_mcdu_click[JAR_A320_MAX_MCDU_KEYS];

// FUEL
extern XPLMDataRef jar_a320_neo_fuel_all_flow;
extern XPLMDataRef jar_a320_neo_fuel_bus_left;
extern XPLMDataRef jar_a320_neo_fuel_bus_right;
extern XPLMDataRef jar_a320_neo_fuel_bus_t1;
extern XPLMDataRef jar_a320_neo_fuel_bus_t3;
extern XPLMDataRef jar_a320_neo_fuel_cent_mode;
extern XPLMDataRef jar_a320_neo_fuel_inn_out_left;
extern XPLMDataRef jar_a320_neo_fuel_inn_out_right;
extern XPLMDataRef jar_a320_neo_fuel_t0;
extern XPLMDataRef jar_a320_neo_fuel_t1;
extern XPLMDataRef jar_a320_neo_fuel_t1_pump1;
extern XPLMDataRef jar_a320_neo_fuel_t1_pump2;
extern XPLMDataRef jar_a320_neo_fuel_t2;
extern XPLMDataRef jar_a320_neo_fuel_t2_pump1;
extern XPLMDataRef jar_a320_neo_fuel_t2_pump2;
extern XPLMDataRef jar_a320_neo_fuel_t3;
extern XPLMDataRef jar_a320_neo_fuel_t3_pump1;
extern XPLMDataRef jar_a320_neo_fuel_t3_pump2;
extern XPLMDataRef jar_a320_neo_fuel_t4;
extern XPLMDataRef jar_a320_neo_fuel_used1;
extern XPLMDataRef jar_a320_neo_fuel_used12;
extern XPLMDataRef jar_a320_neo_fuel_used2;
extern XPLMDataRef jar_a320_neo_fuel_xfeed;

// Hydraulics
extern XPLMDataRef jar_a320_neo_hydr_b_elec_pump_fault_light;
extern XPLMDataRef jar_a320_neo_hydr_b_elec_pump_mode;
extern XPLMDataRef jar_a320_neo_hydr_b_press_aft_acc_old;
extern XPLMDataRef jar_a320_neo_hydr_g_eng_pump_fault_light;
extern XPLMDataRef jar_a320_neo_hydr_g_eng_pump_mode;
extern XPLMDataRef jar_a320_neo_hydr_g_press_aft_acc_old;
extern XPLMDataRef jar_a320_neo_hydr_ptu_delta;
extern XPLMDataRef jar_a320_neo_hydr_ptu_mode;
extern XPLMDataRef jar_a320_neo_hydr_y_elec_pump_fault_light;
extern XPLMDataRef jar_a320_neo_hydr_y_elec_pump_mode;
extern XPLMDataRef jar_a320_neo_hydr_y_eng_pump_fault_light;
extern XPLMDataRef jar_a320_neo_hydr_y_eng_pump_mode;
extern XPLMDataRef jar_a320_neo_hydr_y_press_aft_acc_old;

// Ice and Rain
extern XPLMDataRef jar_a320_neo_icerain_eng1;
extern XPLMDataRef jar_a320_neo_icerain_eng2;
extern XPLMDataRef jar_a320_neo_icerain_window;
extern XPLMDataRef jar_a320_neo_icerain_wing;
extern XPLMDataRef jar_a320_neo_icerain_wing_flt;

// OXY
extern XPLMDataRef jar_a320_neo_oxy_crewsupp;
extern XPLMDataRef jar_a320_neo_oxy_manon;
extern XPLMDataRef jar_a320_neo_oxy_sys_on;
extern XPLMDataRef jar_a320_neo_oxy_textoxy;

// PRESSURE
extern XPLMDataRef jar_a320_neo_press_alt_rot;
extern XPLMDataRef jar_a320_neo_press_mode;
extern XPLMDataRef jar_a320_neo_press_cab_alt;
extern XPLMDataRef jar_a320_neo_press_cab_des;
extern XPLMDataRef jar_a320_neo_press_cab_vs;
extern XPLMDataRef jar_a320_neo_press_delta_p;
extern XPLMDataRef jar_a320_neo_press_outflow_valve;
extern XPLMDataRef jar_a320_neo_press_safety_valve;
extern XPLMDataRef jar_a320_neo_press_sys1;
extern XPLMDataRef jar_a320_neo_press_sys2;

// WHEELS
extern XPLMDataRef jar_a320_neo_wheels_brake_fan;
extern XPLMDataRef jar_a320_neo_wheels_brake_hot;
extern XPLMDataRef jar_a320_neo_wheels_temp_l_1;
extern XPLMDataRef jar_a320_neo_wheels_temp_l_2;
extern XPLMDataRef jar_a320_neo_wheels_temp_r_1;
extern XPLMDataRef jar_a320_neo_wheels_temp_r_2;
extern XPLMDataRef jar_a320_neo_wheels_ped_disc;
extern XPLMDataRef jar_a320_neo_wheels_anti_skid;

// SD_PAGE Display
extern XPLMDataRef jar_a320_disp_sys_mode;

extern int jar_a320_neo_ready;

// global functions
float checkJarA320NeoCallback(float, float, int, void *);
void writeJarA320neoDataRef(int, float);

#endif /* DATAREFS_JAR_A320NEO_H_ */
