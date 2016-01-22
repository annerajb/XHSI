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


// Brakes
extern XPLMDataRef jar_a320_neo_autobrake_low;
extern XPLMDataRef jar_a320_neo_autobrake_med;
extern XPLMDataRef jar_a320_neo_autobrake_max;
extern XPLMDataRef jar_a320_neo_autobrake_on;
extern XPLMDataRef jar_a320_neo_brakes_accu_press;
extern XPLMDataRef jar_a320_neo_brakes_left_press;
extern XPLMDataRef jar_a320_neo_brakes_right_press;
extern XPLMDataRef jar_a320_neo_brakes_mode_na;

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

// WHEELS
extern XPLMDataRef jar_a320_neo_wheels_brake_fan;
extern XPLMDataRef jar_a320_neo_wheels_brake_hot;
extern XPLMDataRef jar_a320_neo_wheels_temp_l_1;
extern XPLMDataRef jar_a320_neo_wheels_temp_l_2;
extern XPLMDataRef jar_a320_neo_wheels_temp_r_1;
extern XPLMDataRef jar_a320_neo_wheels_temp_r_2;
extern XPLMDataRef jar_a320_neo_wheels_ped_disc;
extern XPLMDataRef jar_a320_neo_wheels_anti_skid;


extern int jar_a320_neo_ready;

// global functions
float checkJarA320NeoCallback(float, float, int, void *);
void writeJarA320neoDataRef(int, float);

#endif /* DATAREFS_JAR_A320NEO_H_ */
