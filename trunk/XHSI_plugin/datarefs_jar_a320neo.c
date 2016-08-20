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
XPLMDataRef jar_a320_neo_fcu_alt100x;

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

// ADIRS
XPLMDataRef jar_a320_neo_adirs_adr1;
XPLMDataRef jar_a320_neo_adirs_adr2;
XPLMDataRef jar_a320_neo_adirs_adr3;
XPLMDataRef jar_a320_neo_adirs_disp;
XPLMDataRef jar_a320_neo_adirs_ir1;
XPLMDataRef jar_a320_neo_adirs_ir2;
XPLMDataRef jar_a320_neo_adirs_ir3;
XPLMDataRef jar_a320_neo_adirs_on_bat;

// APU
XPLMDataRef jar_a320_neo_apu_flap;
XPLMDataRef jar_a320_neo_apu_start_pb;

// Bleed Air
XPLMDataRef jar_a320_neo_bleed_apu_bleed_valve;
XPLMDataRef jar_a320_neo_bleed_eng1_bleed_knob;
XPLMDataRef jar_a320_neo_bleed_eng1_bleed_valve;
XPLMDataRef jar_a320_neo_bleed_eng1_bleed_temp;
XPLMDataRef jar_a320_neo_bleed_eng1_bleed_hp_valve;
XPLMDataRef jar_a320_neo_bleed_eng1_bleed_psi;
XPLMDataRef jar_a320_neo_bleed_eng2_bleed_knob;
XPLMDataRef jar_a320_neo_bleed_eng2_bleed_valve;
XPLMDataRef jar_a320_neo_bleed_eng2_bleed_temp;
XPLMDataRef jar_a320_neo_bleed_eng2_bleed_hp_valve;
XPLMDataRef jar_a320_neo_bleed_eng2_bleed_psi;
XPLMDataRef jar_a320_neo_bleed_cross_valve;

// Brakes
XPLMDataRef jar_a320_neo_autobrake_low;
XPLMDataRef jar_a320_neo_autobrake_med;
XPLMDataRef jar_a320_neo_autobrake_max;
XPLMDataRef jar_a320_neo_autobrake_on;
XPLMDataRef jar_a320_neo_brakes_accu_press;
XPLMDataRef jar_a320_neo_brakes_left_press;
XPLMDataRef jar_a320_neo_brakes_right_press;
XPLMDataRef jar_a320_neo_brakes_mode_na;

// ATA 21 Conditioning
XPLMDataRef jar_a320_neo_cond_aft_duct;
XPLMDataRef jar_a320_neo_cond_aft_temp;
XPLMDataRef jar_a320_neo_cond_aft_trm_valve;
XPLMDataRef jar_a320_neo_cond_cargo_aft_duct;
XPLMDataRef jar_a320_neo_cond_cargo_aft_temp;
XPLMDataRef jar_a320_neo_cond_cargo_aft_trm_valve;
XPLMDataRef jar_a320_neo_cond_cargo_aft_valve;
XPLMDataRef jar_a320_neo_cond_cargo_fwd_duct;
XPLMDataRef jar_a320_neo_cond_cargo_fwd_temp;
XPLMDataRef jar_a320_neo_cond_cargo_fwd_trm_valve;
XPLMDataRef jar_a320_neo_cond_cargo_fwd_valve;
XPLMDataRef jar_a320_neo_cond_cockpit_duct;
XPLMDataRef jar_a320_neo_cond_cockpit_temp;
XPLMDataRef jar_a320_neo_cond_cockpit_trm_valve;
XPLMDataRef jar_a320_neo_cond_econ_flow;
XPLMDataRef jar_a320_neo_cond_fwd_duct;
XPLMDataRef jar_a320_neo_cond_fwd_temp;
XPLMDataRef jar_a320_neo_cond_fwd_trm_valve;
XPLMDataRef jar_a320_neo_cond_hot_air;
XPLMDataRef jar_a320_neo_cond_cargo_hot_air;
XPLMDataRef jar_a320_neo_cond_pack1;
XPLMDataRef jar_a320_neo_cond_pack12_line;
XPLMDataRef jar_a320_neo_cond_pack1_comp_deg;
XPLMDataRef jar_a320_neo_cond_pack1_f;
XPLMDataRef jar_a320_neo_cond_pack1_flow;
XPLMDataRef jar_a320_neo_cond_pack1_line;
XPLMDataRef jar_a320_neo_cond_pack1_ndl;
XPLMDataRef jar_a320_neo_cond_pack1_out_deg;
XPLMDataRef jar_a320_neo_cond_pack2;
XPLMDataRef jar_a320_neo_cond_pack2_comp_deg;
XPLMDataRef jar_a320_neo_cond_pack2_f;
XPLMDataRef jar_a320_neo_cond_pack2_flow;
XPLMDataRef jar_a320_neo_cond_pack2_line;
XPLMDataRef jar_a320_neo_cond_pack2_ndl;
XPLMDataRef jar_a320_neo_cond_pack2_out_deg;
XPLMDataRef jar_a320_neo_cond_ram_air;

// Doors
XPLMDataRef jar_a320_neo_doors_c_b_kn;
XPLMDataRef jar_a320_neo_doors_c_b_now;
XPLMDataRef jar_a320_neo_doors_c_f_kn;
XPLMDataRef jar_a320_neo_doors_c_f_now;
XPLMDataRef jar_a320_neo_doors_p_b_l_kn;
XPLMDataRef jar_a320_neo_doors_p_b_l_now;
XPLMDataRef jar_a320_neo_doors_p_b_r_kn;
XPLMDataRef jar_a320_neo_doors_p_b_r_now;
XPLMDataRef jar_a320_neo_doors_p_f_l_kn;
XPLMDataRef jar_a320_neo_doors_p_f_l_now;
XPLMDataRef jar_a320_neo_doors_p_f_r_kn;
XPLMDataRef jar_a320_neo_doors_p_f_r_now;

// Electrics
XPLMDataRef jar_a320_neo_elec_ac1_source;
XPLMDataRef jar_a320_neo_elec_ac2_source;
XPLMDataRef jar_a320_neo_elec_ac_ess;
XPLMDataRef jar_a320_neo_elec_ac_ess_alt;
XPLMDataRef jar_a320_neo_elec_ac_ess_shed;
XPLMDataRef jar_a320_neo_elec_apu_gen_on;
XPLMDataRef jar_a320_neo_elec_bat1_amp;
XPLMDataRef jar_a320_neo_elec_bat1_volt;
XPLMDataRef jar_a320_neo_elec_bat1_on;
XPLMDataRef jar_a320_neo_elec_bat2_amp;
XPLMDataRef jar_a320_neo_elec_bat2_volt;
XPLMDataRef jar_a320_neo_elec_bat2_on;
XPLMDataRef jar_a320_neo_elec_bus_tie;
XPLMDataRef jar_a320_neo_elec_commrc;
XPLMDataRef jar_a320_neo_elec_dc1;
XPLMDataRef jar_a320_neo_elec_dc2;
XPLMDataRef jar_a320_neo_elec_dcbus;
XPLMDataRef jar_a320_neo_elec_dc_ess;
XPLMDataRef jar_a320_neo_elec_dc_ess_shed;
XPLMDataRef jar_a320_neo_elec_dc_some_on;
XPLMDataRef jar_a320_neo_elec_emer;
XPLMDataRef jar_a320_neo_elec_ext_hz;
XPLMDataRef jar_a320_neo_elec_ext_volt;
XPLMDataRef jar_a320_neo_elec_galley;
XPLMDataRef jar_a320_neo_elec_gen1_hz;
XPLMDataRef jar_a320_neo_elec_gen1_line_on;
XPLMDataRef jar_a320_neo_elec_gen1_per;
XPLMDataRef jar_a320_neo_elec_gen1_volt;
XPLMDataRef jar_a320_neo_elec_gen2_hz;
XPLMDataRef jar_a320_neo_elec_gen2_line_on;
XPLMDataRef jar_a320_neo_elec_gen2_per;
XPLMDataRef jar_a320_neo_elec_gen2_volt;
XPLMDataRef jar_a320_neo_elec_apu_hz;
XPLMDataRef jar_a320_neo_elec_apu_per;
XPLMDataRef jar_a320_neo_elec_apu_volt;
XPLMDataRef jar_a320_neo_elec_gen_emer_hz;
XPLMDataRef jar_a320_neo_elec_gen_emer_volt;
XPLMDataRef jar_a320_neo_elec_gpu_av;
XPLMDataRef jar_a320_neo_elec_gpu_on;
XPLMDataRef jar_a320_neo_elec_lft_gen_on;
XPLMDataRef jar_a320_neo_elec_man_rat_cover;
XPLMDataRef jar_a320_neo_elec_man_rat_on;
XPLMDataRef jar_a320_neo_elec_rat_av;
XPLMDataRef jar_a320_neo_elec_rat_on;
XPLMDataRef jar_a320_neo_elec_rgh_gen_on;
XPLMDataRef jar_a320_neo_elec_tr1_amp;
XPLMDataRef jar_a320_neo_elec_tr1_volt;
XPLMDataRef jar_a320_neo_elec_tr2_amp;
XPLMDataRef jar_a320_neo_elec_tr2_volt;
XPLMDataRef jar_a320_neo_elec_tr_em_amp;
XPLMDataRef jar_a320_neo_elec_tr_em_volt;

// Engines
XPLMDataRef jar_a320_neo_eng_1_nac_temp;
XPLMDataRef jar_a320_neo_eng_2_nac_temp;
XPLMDataRef jar_a320_neo_eng_1_oil_press;
XPLMDataRef jar_a320_neo_eng_2_oil_press;
XPLMDataRef jar_a320_neo_eng_1_oil_qt;
XPLMDataRef jar_a320_neo_eng_2_oil_qt;
XPLMDataRef jar_a320_neo_eng_1_oil_temp;
XPLMDataRef jar_a320_neo_eng_2_oil_temp;
XPLMDataRef jar_a320_neo_eng_1_fire_pb;
XPLMDataRef jar_a320_neo_eng_1_fuel_valve;
XPLMDataRef jar_a320_neo_eng_2_fire_pb;
XPLMDataRef jar_a320_neo_eng_2_fuel_valve;


// FMS
XPLMDataRef jar_a320_neo_fms_tr_alt;
XPLMDataRef jar_a320_neo_yoyo_on;
XPLMDataRef jar_a320_neo_vdev;

// FUEL
XPLMDataRef jar_a320_neo_fuel_all_flow;
XPLMDataRef jar_a320_neo_fuel_bus_left;
XPLMDataRef jar_a320_neo_fuel_bus_right;
XPLMDataRef jar_a320_neo_fuel_bus_t1;
XPLMDataRef jar_a320_neo_fuel_bus_t3;
XPLMDataRef jar_a320_neo_fuel_cent_mode;
XPLMDataRef jar_a320_neo_fuel_inn_out_left;
XPLMDataRef jar_a320_neo_fuel_inn_out_right;
XPLMDataRef jar_a320_neo_fuel_t0;
XPLMDataRef jar_a320_neo_fuel_t1;
XPLMDataRef jar_a320_neo_fuel_t1_pump1;
XPLMDataRef jar_a320_neo_fuel_t1_pump2;
XPLMDataRef jar_a320_neo_fuel_t2;
XPLMDataRef jar_a320_neo_fuel_t2_pump1;
XPLMDataRef jar_a320_neo_fuel_t2_pump2;
XPLMDataRef jar_a320_neo_fuel_t3;
XPLMDataRef jar_a320_neo_fuel_t3_pump1;
XPLMDataRef jar_a320_neo_fuel_t3_pump2;
XPLMDataRef jar_a320_neo_fuel_t4;
XPLMDataRef jar_a320_neo_fuel_used1;
XPLMDataRef jar_a320_neo_fuel_used12;
XPLMDataRef jar_a320_neo_fuel_used2;
XPLMDataRef jar_a320_neo_fuel_xfeed;

// GPWS
XPLMDataRef jar_a320_neo_gpws_flap;
XPLMDataRef jar_a320_neo_gpws_flap3;
XPLMDataRef jar_a320_neo_gpws_fr;
XPLMDataRef jar_a320_neo_gpws_gs;
XPLMDataRef jar_a320_neo_gpws_sys;
XPLMDataRef jar_a320_neo_gpws_terr;

// Hydraulics
XPLMDataRef jar_a320_neo_hydr_b_elec_pump_fault_light;
XPLMDataRef jar_a320_neo_hydr_b_elec_pump_mode;
XPLMDataRef jar_a320_neo_hydr_b_press_aft_acc_old;
XPLMDataRef jar_a320_neo_hydr_g_eng_pump_fault_light;
XPLMDataRef jar_a320_neo_hydr_g_eng_pump_mode;
XPLMDataRef jar_a320_neo_hydr_g_press_aft_acc_old;
XPLMDataRef jar_a320_neo_hydr_ptu_delta;
XPLMDataRef jar_a320_neo_hydr_ptu_mode;
XPLMDataRef jar_a320_neo_hydr_y_elec_pump_fault_light;
XPLMDataRef jar_a320_neo_hydr_y_elec_pump_mode;
XPLMDataRef jar_a320_neo_hydr_y_eng_pump_fault_light;
XPLMDataRef jar_a320_neo_hydr_y_eng_pump_mode;
XPLMDataRef jar_a320_neo_hydr_y_press_aft_acc_old;

// Ice and Rain
XPLMDataRef jar_a320_neo_icerain_eng1;
XPLMDataRef jar_a320_neo_icerain_eng2;
XPLMDataRef jar_a320_neo_icerain_window;
XPLMDataRef jar_a320_neo_icerain_wing;
XPLMDataRef jar_a320_neo_icerain_wing_flt;

// OXY
XPLMDataRef jar_a320_neo_oxy_crewsupp;
XPLMDataRef jar_a320_neo_oxy_manon;
XPLMDataRef jar_a320_neo_oxy_sys_on;
XPLMDataRef jar_a320_neo_oxy_textoxy;

// PRESSURE
XPLMDataRef jar_a320_neo_press_alt_rot;
XPLMDataRef jar_a320_neo_press_mode;
XPLMDataRef jar_a320_neo_press_cab_alt;
XPLMDataRef jar_a320_neo_press_cab_des;
XPLMDataRef jar_a320_neo_press_cab_vs;
XPLMDataRef jar_a320_neo_press_delta_p;
XPLMDataRef jar_a320_neo_press_outflow_valve;
XPLMDataRef jar_a320_neo_press_safety_valve;
XPLMDataRef jar_a320_neo_press_sys1;
XPLMDataRef jar_a320_neo_press_sys2;

// VENTILATION
XPLMDataRef jar_a320_neo_vent_blover;
XPLMDataRef jar_a320_neo_vent_cab_fans;
XPLMDataRef jar_a320_neo_vent_extract;
XPLMDataRef jar_a320_neo_vent_gnd_cool;
XPLMDataRef jar_a320_neo_vent_inlet_valve;
XPLMDataRef jar_a320_neo_vent_outlet_valve;

// WHEELS
XPLMDataRef jar_a320_neo_wheels_brake_fan;
XPLMDataRef jar_a320_neo_wheels_brake_hot;
XPLMDataRef jar_a320_neo_wheels_temp_l_1;
XPLMDataRef jar_a320_neo_wheels_temp_l_2;
XPLMDataRef jar_a320_neo_wheels_temp_r_1;
XPLMDataRef jar_a320_neo_wheels_temp_r_2;
XPLMDataRef jar_a320_neo_wheels_ped_disc;
XPLMDataRef jar_a320_neo_wheels_anti_skid;

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

// SD_PAGE Display
XPLMDataRef jar_a320_disp_sys_mode;

int jar_a320_neo_ready = 0;

/*
 * JarDesign a320neo dataref todo list
 *
sim/custom/rev[0]
sim/custom/terr_on_nd
*
* Autopilot
*
sim/custom/xap/ap/athr_mode[0]
sim/custom/xap/ap/spdmanaged[0]
*
*
sim/custom/xap/debug[0]
sim/custom/xap/disp/sys/emer_canc[0]
*
* CHRONO
*
sim/custom/xap/et_timer/all[0]
sim/custom/xap/et_timer/hr[0]
sim/custom/xap/et_timer/min[0]
*
* ECAM E/WD
*
sim/custom/xap/ewd_allkn[0]
sim/custom/xap/ewd_clr[0]
*
* LIGHTS
*
sim/custom/xap/extlight/cockp_left_br
sim/custom/xap/extlight/cockp_main_br[0]
sim/custom/xap/extlight/cockp_main_br_target
sim/custom/xap/extlight/cockp_rght_br
sim/custom/xap/fctr/mode[0]
*
* FIRE
*
sim/custom/xap/firetest/apu[0]
sim/custom/xap/firetest/e1[0]
sim/custom/xap/firetest/e2[0]
*
* Ice and Rain
*
sim/custom/xap/icerain/eng1_knob[0]
sim/custom/xap/icerain/eng2_knob[0]
sim/custom/xap/icerain/window[0]
sim/custom/xap/icerain/wing_knob[0]
sim/custom/xap/icerain/wing_knob_flt[0]
*
* Indicators
*
sim/custom/xap/ind_baro_hpa[0]
sim/custom/xap/ind_baro_inhg[0]
sim/custom/xap/indicators/autoland[0]
sim/custom/xap/indicators/mastcaut[0]
sim/custom/xap/indicators/mastwarn[0]
sim/custom/xap/is_cap_pf[0]
sim/custom/xap/isis_lt_dn[0]
sim/custom/xap/isis_lt_up[0]
sim/custom/xap/lght_dnd[0]
sim/custom/xap/lght_oh_cent[0]
sim/custom/xap/lght_oh_left[0]
sim/custom/xap/lght_oh_rght[0]
sim/custom/xap/lght_upd[0]
*
* RADIO
*
sim/custom/xap/radio/nav_but[0]
sim/custom/xap/radio/on[0]
sim/custom/xap/shtora/fl_on[0]
sim/custom/xap/to_conf_knob[0]
*
* WingTip
*
sim/custom/xap/wingtip
sim/custom/xap/wingtip_rev
 *
 */

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
            jar_a320_neo_fcu_alt100x = XPLMFindDataRef("sim/custom/xap/fcu/alt100x");

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

            // ADIRS
            jar_a320_neo_adirs_adr1 = XPLMFindDataRef("sim/custom/xap/adirs/adr1");
            jar_a320_neo_adirs_adr2 = XPLMFindDataRef("sim/custom/xap/adirs/adr2");
            jar_a320_neo_adirs_adr3 = XPLMFindDataRef("sim/custom/xap/adirs/adr3");
            jar_a320_neo_adirs_disp = XPLMFindDataRef("sim/custom/xap/adirs/disp");
            jar_a320_neo_adirs_ir1 = XPLMFindDataRef("sim/custom/xap/adirs/ir1");
            jar_a320_neo_adirs_ir2 = XPLMFindDataRef("sim/custom/xap/adirs/ir2");
            jar_a320_neo_adirs_ir3 = XPLMFindDataRef("sim/custom/xap/adirs/ir3");
            jar_a320_neo_adirs_on_bat = XPLMFindDataRef("sim/custom/xap/adirs/on_bat");

            // APU
            jar_a320_neo_apu_flap = XPLMFindDataRef("sim/custom/xap/apu/flap");
            jar_a320_neo_apu_start_pb = XPLMFindDataRef("sim/custom/xap/apu/start_pb");

            // Bleed Air
            jar_a320_neo_bleed_apu_bleed_valve = XPLMFindDataRef("sim/custom/xap/bleed/apu_blvlv");
            jar_a320_neo_bleed_eng1_bleed_knob = XPLMFindDataRef("sim/custom/xap/bleed/eng1_bl_knob");
            jar_a320_neo_bleed_eng1_bleed_valve = XPLMFindDataRef("sim/custom/xap/bleed/eng1_blvlv");
            jar_a320_neo_bleed_eng1_bleed_temp = XPLMFindDataRef("sim/custom/xap/bleed/eng1_deg");
            jar_a320_neo_bleed_eng1_bleed_hp_valve = XPLMFindDataRef("sim/custom/xap/bleed/eng1_hpvlv");
            jar_a320_neo_bleed_eng1_bleed_psi = XPLMFindDataRef("sim/custom/xap/bleed/eng1_psi");
            jar_a320_neo_bleed_eng2_bleed_knob = XPLMFindDataRef("sim/custom/xap/bleed/eng2_bl_knob");
            jar_a320_neo_bleed_eng2_bleed_valve = XPLMFindDataRef("sim/custom/xap/bleed/eng2_blvlv");
            jar_a320_neo_bleed_eng2_bleed_temp = XPLMFindDataRef("sim/custom/xap/bleed/eng2_deg");
            jar_a320_neo_bleed_eng2_bleed_hp_valve = XPLMFindDataRef("sim/custom/xap/bleed/eng2_hpvlv");
            jar_a320_neo_bleed_eng2_bleed_psi = XPLMFindDataRef("sim/custom/xap/bleed/eng2_psi");
            jar_a320_neo_bleed_cross_valve = XPLMFindDataRef("sim/custom/xap/bleed/xbl_vlv");


            // Brakes
            jar_a320_neo_autobrake_low = XPLMFindDataRef("sim/custom/xap/brakes/auto_lo");
            jar_a320_neo_autobrake_med = XPLMFindDataRef("sim/custom/xap/brakes/auto_med");
            jar_a320_neo_autobrake_max = XPLMFindDataRef("sim/custom/xap/brakes/auto_max");
            jar_a320_neo_autobrake_on = XPLMFindDataRef("sim/custom/xap/brakes/auto_on");
            jar_a320_neo_brakes_accu_press = XPLMFindDataRef("sim/custom/xap/brakes/accu_press");
            jar_a320_neo_brakes_left_press = XPLMFindDataRef("sim/custom/xap/brakes/left_br_press");
            jar_a320_neo_brakes_right_press = XPLMFindDataRef("sim/custom/xap/brakes/right_br_press");
            jar_a320_neo_brakes_mode_na = XPLMFindDataRef("sim/custom/xap/brakes/mode_na");

            // Conditionning
            jar_a320_neo_cond_aft_duct = XPLMFindDataRef("sim/custom/xap/cond/aft_duct");
            jar_a320_neo_cond_aft_temp = XPLMFindDataRef("sim/custom/xap/cond/aft_temp");
            jar_a320_neo_cond_aft_trm_valve = XPLMFindDataRef("sim/custom/xap/cond/aft_trmvlv");
            jar_a320_neo_cond_cargo_aft_duct = XPLMFindDataRef("sim/custom/xap/cond/c_aft_duct");
            jar_a320_neo_cond_cargo_aft_temp = XPLMFindDataRef("sim/custom/xap/cond/c_aft_temp");
            jar_a320_neo_cond_cargo_aft_trm_valve = XPLMFindDataRef("sim/custom/xap/cond/c_aft_trmvlv");
            jar_a320_neo_cond_cargo_aft_valve = XPLMFindDataRef("sim/custom/xap/cond/c_aft_vlv");
            jar_a320_neo_cond_cargo_fwd_duct = XPLMFindDataRef("sim/custom/xap/cond/c_fwd_duct");
            jar_a320_neo_cond_cargo_fwd_temp = XPLMFindDataRef("sim/custom/xap/cond/c_fwd_temp");
            jar_a320_neo_cond_cargo_fwd_trm_valve = XPLMFindDataRef("sim/custom/xap/cond/c_fwd_trmvlv");
            jar_a320_neo_cond_cargo_fwd_valve = XPLMFindDataRef("sim/custom/xap/cond/c_fwd_vlv");
            jar_a320_neo_cond_cockpit_duct = XPLMFindDataRef("sim/custom/xap/cond/ckpt_duct");
            jar_a320_neo_cond_cockpit_temp = XPLMFindDataRef("sim/custom/xap/cond/ckpt_temp");
            jar_a320_neo_cond_cockpit_trm_valve = XPLMFindDataRef("sim/custom/xap/cond/ckpt_trmvlv");
            jar_a320_neo_cond_econ_flow = XPLMFindDataRef("sim/custom/xap/cond/econ_flow");
            jar_a320_neo_cond_fwd_duct = XPLMFindDataRef("sim/custom/xap/cond/fwd_duct");
            jar_a320_neo_cond_fwd_temp = XPLMFindDataRef("sim/custom/xap/cond/fwd_temp");
            jar_a320_neo_cond_fwd_trm_valve = XPLMFindDataRef("sim/custom/xap/cond/fwd_trmvlv");
            jar_a320_neo_cond_hot_air = XPLMFindDataRef("sim/custom/xap/cond/hot_air");
            jar_a320_neo_cond_cargo_hot_air = XPLMFindDataRef("sim/custom/xap/cond/hot_air_c");
            jar_a320_neo_cond_pack1 = XPLMFindDataRef("sim/custom/xap/cond/pack1");
            jar_a320_neo_cond_pack12_line = XPLMFindDataRef("sim/custom/xap/cond/pack12_line");
            jar_a320_neo_cond_pack1_comp_deg = XPLMFindDataRef("sim/custom/xap/cond/pack1_comp_deg");
            jar_a320_neo_cond_pack1_f = XPLMFindDataRef("sim/custom/xap/cond/pack1_f");
            jar_a320_neo_cond_pack1_flow = XPLMFindDataRef("sim/custom/xap/cond/pack1_flow");
            jar_a320_neo_cond_pack1_line = XPLMFindDataRef("sim/custom/xap/cond/pack1_line");
            jar_a320_neo_cond_pack1_ndl = XPLMFindDataRef("sim/custom/xap/cond/pack1_ndl");
            jar_a320_neo_cond_pack1_out_deg = XPLMFindDataRef("sim/custom/xap/cond/pack1_out_deg");
            jar_a320_neo_cond_pack2 = XPLMFindDataRef("sim/custom/xap/cond/pack2");
            jar_a320_neo_cond_pack2_comp_deg = XPLMFindDataRef("sim/custom/xap/cond/pack2_comp_deg");
            jar_a320_neo_cond_pack2_f = XPLMFindDataRef("sim/custom/xap/cond/pack2_f");
            jar_a320_neo_cond_pack2_flow = XPLMFindDataRef("sim/custom/xap/cond/pack2_flow");
            jar_a320_neo_cond_pack2_line = XPLMFindDataRef("sim/custom/xap/cond/pack2_line");
            jar_a320_neo_cond_pack2_ndl = XPLMFindDataRef("sim/custom/xap/cond/pack2_ndl");
            jar_a320_neo_cond_pack2_out_deg = XPLMFindDataRef("sim/custom/xap/cond/pack2_out_deg");
            jar_a320_neo_cond_ram_air = XPLMFindDataRef("sim/custom/xap/cond/ram_air");

            // Doors
            jar_a320_neo_doors_c_b_kn = XPLMFindDataRef("sim/custom/xap/doors/c_b_kn");
            jar_a320_neo_doors_c_b_now = XPLMFindDataRef("sim/custom/xap/doors/c_b_now");
            jar_a320_neo_doors_c_f_kn = XPLMFindDataRef("sim/custom/xap/doors/c_f_kn");
            jar_a320_neo_doors_c_f_now = XPLMFindDataRef("sim/custom/xap/doors/c_f_now");
            jar_a320_neo_doors_p_b_l_kn = XPLMFindDataRef("sim/custom/xap/doors/p_b_l_kn");
            jar_a320_neo_doors_p_b_l_now = XPLMFindDataRef("sim/custom/xap/doors/p_b_l_now");
            jar_a320_neo_doors_p_b_r_kn = XPLMFindDataRef("sim/custom/xap/doors/p_b_r_kn");
            jar_a320_neo_doors_p_b_r_now = XPLMFindDataRef("sim/custom/xap/doors/p_b_r_now");
            jar_a320_neo_doors_p_f_l_kn = XPLMFindDataRef("sim/custom/xap/doors/p_f_l_kn");
            jar_a320_neo_doors_p_f_l_now = XPLMFindDataRef("sim/custom/xap/doors/p_f_l_now");
            jar_a320_neo_doors_p_f_r_kn = XPLMFindDataRef("sim/custom/xap/doors/p_f_r_kn");
            jar_a320_neo_doors_p_f_r_now = XPLMFindDataRef("sim/custom/xap/doors/p_f_r_now");

            // Electrics
            jar_a320_neo_elec_ac1_source = XPLMFindDataRef("sim/custom/xap/elec/ac1_source");
            jar_a320_neo_elec_ac2_source = XPLMFindDataRef("sim/custom/xap/elec/ac2_source");
            jar_a320_neo_elec_ac_ess = XPLMFindDataRef("sim/custom/xap/elec/acess");
            jar_a320_neo_elec_ac_ess_alt = XPLMFindDataRef("sim/custom/xap/elec/acess_alt");
            jar_a320_neo_elec_ac_ess_shed = XPLMFindDataRef("sim/custom/xap/elec/acess_shed");
            jar_a320_neo_elec_apu_gen_on = XPLMFindDataRef("sim/custom/xap/elec/apu_gen_on");
            jar_a320_neo_elec_bat1_amp = XPLMFindDataRef("sim/custom/xap/elec/bat1_amp");
            jar_a320_neo_elec_bat1_volt = XPLMFindDataRef("sim/custom/xap/elec/bat1_ind_volt");
            jar_a320_neo_elec_bat1_on = XPLMFindDataRef("sim/custom/xap/elec/bat1_on");
            jar_a320_neo_elec_bat2_amp = XPLMFindDataRef("sim/custom/xap/elec/bat2_amp");
            jar_a320_neo_elec_bat2_volt = XPLMFindDataRef("sim/custom/xap/elec/bat2_ind_volt");
            jar_a320_neo_elec_bat2_on = XPLMFindDataRef("sim/custom/xap/elec/bat2_on");
            jar_a320_neo_elec_bus_tie = XPLMFindDataRef("sim/custom/xap/elec/bus_tie");
            jar_a320_neo_elec_commrc = XPLMFindDataRef("sim/custom/xap/elec/commrc");
            jar_a320_neo_elec_dc1 = XPLMFindDataRef("sim/custom/xap/elec/dc1");
            jar_a320_neo_elec_dc2 = XPLMFindDataRef("sim/custom/xap/elec/dc2");
            jar_a320_neo_elec_dcbus = XPLMFindDataRef("sim/custom/xap/elec/dcbus");
            jar_a320_neo_elec_dc_ess = XPLMFindDataRef("sim/custom/xap/elec/dcess");
            jar_a320_neo_elec_dc_ess_shed = XPLMFindDataRef("sim/custom/xap/elec/dcess_shed");
            jar_a320_neo_elec_dc_some_on = XPLMFindDataRef("sim/custom/xap/elec/dc_some_on");
            jar_a320_neo_elec_emer = XPLMFindDataRef("sim/custom/xap/elec/emer");
            jar_a320_neo_elec_ext_hz = XPLMFindDataRef("sim/custom/xap/elec/ext_hz");
            jar_a320_neo_elec_ext_volt = XPLMFindDataRef("sim/custom/xap/elec/ext_volt");
            jar_a320_neo_elec_galley = XPLMFindDataRef("sim/custom/xap/elec/galley");
            jar_a320_neo_elec_gen1_hz = XPLMFindDataRef("sim/custom/xap/elec/gen1_hz");
            jar_a320_neo_elec_gen1_line_on = XPLMFindDataRef("sim/custom/xap/elec/gen1line_on");
            jar_a320_neo_elec_gen1_per = XPLMFindDataRef("sim/custom/xap/elec/gen1_per");
            jar_a320_neo_elec_gen1_volt = XPLMFindDataRef("sim/custom/xap/elec/gen1_volt");
            jar_a320_neo_elec_gen2_hz = XPLMFindDataRef("sim/custom/xap/elec/gen2_hz");
            jar_a320_neo_elec_gen2_line_on = XPLMFindDataRef("sim/custom/xap/elec/gen2line_on");  // absent ?
            jar_a320_neo_elec_gen2_per = XPLMFindDataRef("sim/custom/xap/elec/gen2_per");
            jar_a320_neo_elec_gen2_volt = XPLMFindDataRef("sim/custom/xap/elec/gen2_volt");
            jar_a320_neo_elec_apu_hz = XPLMFindDataRef("sim/custom/xap/elec/genAPU_hz");
            jar_a320_neo_elec_apu_per = XPLMFindDataRef("sim/custom/xap/elec/genAPU_per");
            jar_a320_neo_elec_apu_volt = XPLMFindDataRef("sim/custom/xap/elec/genAPU_volt");
            jar_a320_neo_elec_gen_emer_hz = XPLMFindDataRef("sim/custom/xap/elec/genEM_hz");
            jar_a320_neo_elec_gen_emer_volt = XPLMFindDataRef("sim/custom/xap/elec/genEM_volt");
            jar_a320_neo_elec_gpu_av = XPLMFindDataRef("sim/custom/xap/elec/gpu_av");
            jar_a320_neo_elec_gpu_on = XPLMFindDataRef("sim/custom/xap/elec/gpu_on");
            jar_a320_neo_elec_lft_gen_on = XPLMFindDataRef("sim/custom/xap/elec/lft_gen_on");
            jar_a320_neo_elec_man_rat_cover = XPLMFindDataRef("sim/custom/xap/elec/manRAT_cover");
            jar_a320_neo_elec_man_rat_on = XPLMFindDataRef("sim/custom/xap/elec/manRAT_on");
            jar_a320_neo_elec_rat_av = XPLMFindDataRef("sim/custom/xap/elec/RAT_av");
            jar_a320_neo_elec_rat_on = XPLMFindDataRef("sim/custom/xap/elec/RAT_on");
            jar_a320_neo_elec_rgh_gen_on = XPLMFindDataRef("sim/custom/xap/elec/rgh_gen_on");
            jar_a320_neo_elec_tr1_amp = XPLMFindDataRef("sim/custom/xap/elec/tr1_amp");
            jar_a320_neo_elec_tr1_volt = XPLMFindDataRef("sim/custom/xap/elec/tr1_volt");
            jar_a320_neo_elec_tr2_amp = XPLMFindDataRef("sim/custom/xap/elec/tr2_amp");
            jar_a320_neo_elec_tr2_volt = XPLMFindDataRef("sim/custom/xap/elec/tr2_volt");
            jar_a320_neo_elec_tr_em_amp = XPLMFindDataRef("sim/custom/xap/elec/trEM_amp");
            jar_a320_neo_elec_tr_em_volt = XPLMFindDataRef("sim/custom/xap/elec/trEM_volt");

            // Engines
            jar_a320_neo_eng_1_nac_temp = XPLMFindDataRef("sim/custom/xap/engines/nactemp_eng1");
            jar_a320_neo_eng_2_nac_temp = XPLMFindDataRef("sim/custom/xap/engines/nactemp_eng2");
            jar_a320_neo_eng_1_oil_press = XPLMFindDataRef("sim/custom/xap/engines/oilpres_eng1");
            jar_a320_neo_eng_2_oil_press = XPLMFindDataRef("sim/custom/xap/engines/oilpres_eng2");
            jar_a320_neo_eng_1_oil_qt = XPLMFindDataRef("sim/custom/xap/engines/oilquan_eng1");
            jar_a320_neo_eng_2_oil_qt = XPLMFindDataRef("sim/custom/xap/engines/oilquan_eng2");
            jar_a320_neo_eng_1_oil_temp = XPLMFindDataRef("sim/custom/xap/engines/oiltemp_eng1");
            jar_a320_neo_eng_2_oil_temp = XPLMFindDataRef("sim/custom/xap/engines/oiltemp_eng2");
            jar_a320_neo_eng_1_fire_pb = XPLMFindDataRef("sim/custom/xap/eng/left/firepb");
            jar_a320_neo_eng_1_fuel_valve = XPLMFindDataRef("sim/custom/xap/eng/left/fuelvalve");
            jar_a320_neo_eng_2_fire_pb = XPLMFindDataRef("sim/custom/xap/eng/rght/firepb");
            jar_a320_neo_eng_2_fuel_valve = XPLMFindDataRef("sim/custom/xap/eng/rght/fuelvalve");

            // EFIS
            jar_a320_neo_nd_mode = XPLMFindDataRef("sim/custom/xap/fcu/nd_mode");

            // FMS
            jar_a320_neo_fms_tr_alt = XPLMFindDataRef("sim/custom/xap/ap/trans_alt");
            jar_a320_neo_yoyo_on = XPLMFindDataRef("sim/custom/yoyo_on");
            jar_a320_neo_vdev = XPLMFindDataRef("sim/custom/vdev");

            // FUEL
            jar_a320_neo_fuel_all_flow = XPLMFindDataRef("sim/custom/xap/fuel/all_flow");
            jar_a320_neo_fuel_bus_left = XPLMFindDataRef("sim/custom/xap/fuel/bus_left");
            jar_a320_neo_fuel_bus_right = XPLMFindDataRef("sim/custom/xap/fuel/bus_rght");
            jar_a320_neo_fuel_bus_t1 = XPLMFindDataRef("sim/custom/xap/fuel/bus_t1");
            jar_a320_neo_fuel_bus_t3 = XPLMFindDataRef("sim/custom/xap/fuel/bus_t3");
            jar_a320_neo_fuel_cent_mode = XPLMFindDataRef("sim/custom/xap/fuel/cent_mode");
            jar_a320_neo_fuel_inn_out_left = XPLMFindDataRef("sim/custom/xap/fuel/inn_out_left");
            jar_a320_neo_fuel_inn_out_right = XPLMFindDataRef("sim/custom/xap/fuel/inn_out_rght");
            jar_a320_neo_fuel_t0 = XPLMFindDataRef("sim/custom/xap/fuel/t0");
            jar_a320_neo_fuel_t1 = XPLMFindDataRef("sim/custom/xap/fuel/t1");
            jar_a320_neo_fuel_t1_pump1 = XPLMFindDataRef("sim/custom/xap/fuel/t1_pump1");
            jar_a320_neo_fuel_t1_pump2 = XPLMFindDataRef("sim/custom/xap/fuel/t1_pump2");
            jar_a320_neo_fuel_t2 = XPLMFindDataRef("sim/custom/xap/fuel/t2");
            jar_a320_neo_fuel_t2_pump1 = XPLMFindDataRef("sim/custom/xap/fuel/t2_pump1");
            jar_a320_neo_fuel_t2_pump2 = XPLMFindDataRef("sim/custom/xap/fuel/t2_pump2");
            jar_a320_neo_fuel_t3 = XPLMFindDataRef("sim/custom/xap/fuel/t3");
            jar_a320_neo_fuel_t3_pump1 = XPLMFindDataRef("sim/custom/xap/fuel/t3_pump1");
            jar_a320_neo_fuel_t3_pump2 = XPLMFindDataRef("sim/custom/xap/fuel/t3_pump2");
            jar_a320_neo_fuel_t4 = XPLMFindDataRef("sim/custom/xap/fuel/t4");
            jar_a320_neo_fuel_used1 = XPLMFindDataRef("sim/custom/xap/fuel/used1");
            jar_a320_neo_fuel_used12 = XPLMFindDataRef("sim/custom/xap/fuel/used12");
            jar_a320_neo_fuel_used2 = XPLMFindDataRef("sim/custom/xap/fuel/used2");
            jar_a320_neo_fuel_xfeed = XPLMFindDataRef("sim/custom/xap/fuel/xfeed");

            // GPWS
            jar_a320_neo_gpws_flap = XPLMFindDataRef("sim/custom/xap/gpws_flap");
            jar_a320_neo_gpws_flap3 = XPLMFindDataRef("sim/custom/xap/gpws_flap3");
            jar_a320_neo_gpws_fr = XPLMFindDataRef("sim/custom/xap/gpws_fr");
            jar_a320_neo_gpws_gs = XPLMFindDataRef("sim/custom/xap/gpws_gs");
            jar_a320_neo_gpws_sys = XPLMFindDataRef("sim/custom/xap/gpws_sys");
            jar_a320_neo_gpws_terr = XPLMFindDataRef("sim/custom/xap/gpws_terr");

            // Hydraulics
            jar_a320_neo_hydr_b_elec_pump_fault_light = XPLMFindDataRef("sim/custom/xap/hydr/b/elpump/faultlight");
            jar_a320_neo_hydr_b_elec_pump_mode = XPLMFindDataRef("sim/custom/xap/hydr/b/elpump/mode");
            jar_a320_neo_hydr_b_press_aft_acc_old = XPLMFindDataRef("sim/custom/xap/hydr/b/press_aft_acc_old");
            jar_a320_neo_hydr_g_eng_pump_fault_light = XPLMFindDataRef("sim/custom/xap/hydr/g/engpump/faultlight");
            jar_a320_neo_hydr_g_eng_pump_mode = XPLMFindDataRef("sim/custom/xap/hydr/g/engpump/mode");
            jar_a320_neo_hydr_g_press_aft_acc_old = XPLMFindDataRef("sim/custom/xap/hydr/g/press_aft_acc_old");
            jar_a320_neo_hydr_ptu_delta = XPLMFindDataRef("sim/custom/xap/hydr/ptu/delta");
            jar_a320_neo_hydr_ptu_mode = XPLMFindDataRef("sim/custom/xap/hydr/ptu/mode");
            jar_a320_neo_hydr_y_elec_pump_fault_light = XPLMFindDataRef("sim/custom/xap/hydr/y/elpump/faultlight_el");
            jar_a320_neo_hydr_y_elec_pump_mode = XPLMFindDataRef("sim/custom/xap/hydr/y/elpump/mode");
            jar_a320_neo_hydr_y_eng_pump_fault_light = XPLMFindDataRef("sim/custom/xap/hydr/y/engpump/faultlight");
            jar_a320_neo_hydr_y_eng_pump_mode = XPLMFindDataRef("sim/custom/xap/hydr/y/engpump/mode");
            jar_a320_neo_hydr_y_press_aft_acc_old = XPLMFindDataRef("sim/custom/xap/hydr/y/press_aft_acc_old");

            // Ice and Rain
            jar_a320_neo_icerain_eng1 = XPLMFindDataRef("sim/custom/xap/icerain/eng1_knob");
            jar_a320_neo_icerain_eng2 = XPLMFindDataRef("sim/custom/xap/icerain/eng2_knob");
            jar_a320_neo_icerain_window = XPLMFindDataRef("sim/custom/xap/icerain/window");
            jar_a320_neo_icerain_wing = XPLMFindDataRef("sim/custom/xap/icerain/wing_knob");
            jar_a320_neo_icerain_wing_flt = XPLMFindDataRef("sim/custom/xap/icerain/wing_knob_flt");

            // OXY
            jar_a320_neo_oxy_crewsupp = XPLMFindDataRef("sim/custom/xap/oxy/crewsupp");
            jar_a320_neo_oxy_manon = XPLMFindDataRef("sim/custom/xap/oxy/manon");
            jar_a320_neo_oxy_sys_on = XPLMFindDataRef("sim/custom/xap/oxy/sys_on");
            jar_a320_neo_oxy_textoxy = XPLMFindDataRef("sim/custom/xap/oxy/testoxy");

            // PRESSURE
            jar_a320_neo_press_alt_rot = XPLMFindDataRef("sim/custom/xap/press/alt_rot");
            jar_a320_neo_press_mode = XPLMFindDataRef("sim/custom/xap/press/automode");
            jar_a320_neo_press_cab_alt = XPLMFindDataRef("sim/custom/xap/press/cab_alt");
            jar_a320_neo_press_cab_des = XPLMFindDataRef("sim/custom/xap/press/cab_des");
            jar_a320_neo_press_cab_vs = XPLMFindDataRef("sim/custom/xap/press/cab_vs");
            jar_a320_neo_press_delta_p = XPLMFindDataRef("sim/custom/xap/press/dif_psi");
            jar_a320_neo_press_outflow_valve = XPLMFindDataRef("sim/custom/xap/press/out_vlv");
            jar_a320_neo_press_safety_valve = XPLMFindDataRef("sim/custom/xap/press/saf_vlv");
            jar_a320_neo_press_sys1 = XPLMFindDataRef("sim/custom/xap/press/sys1");
            jar_a320_neo_press_sys2 = XPLMFindDataRef("sim/custom/xap/press/sys2");

            // VENTILATION
            jar_a320_neo_vent_blover = XPLMFindDataRef("sim/custom/xap/vent/blover");
            jar_a320_neo_vent_cab_fans = XPLMFindDataRef("sim/custom/xap/vent/cab_fans");
            jar_a320_neo_vent_extract = XPLMFindDataRef("sim/custom/xap/vent/extract");
            jar_a320_neo_vent_gnd_cool = XPLMFindDataRef("sim/custom/xap/vent/gnd_cool");
            jar_a320_neo_vent_inlet_valve = XPLMFindDataRef("sim/custom/xap/vent/in_vlv");
            jar_a320_neo_vent_outlet_valve = XPLMFindDataRef("sim/custom/xap/vent/out_vlv");

            // WHEELS
            jar_a320_neo_wheels_brake_fan = XPLMFindDataRef("sim/custom/xap/wheels/br_fun");
            jar_a320_neo_wheels_brake_hot = XPLMFindDataRef("sim/custom/xap/wheels/br_hot");
            jar_a320_neo_wheels_temp_l_1 = XPLMFindDataRef("sim/custom/xap/wheels/deg_l_1");
            jar_a320_neo_wheels_temp_l_2 = XPLMFindDataRef("sim/custom/xap/wheels/deg_l_2");
            jar_a320_neo_wheels_temp_r_1 = XPLMFindDataRef("sim/custom/xap/wheels/deg_r_1");
            jar_a320_neo_wheels_temp_r_2 = XPLMFindDataRef("sim/custom/xap/wheels/deg_r_2");
            jar_a320_neo_wheels_ped_disc = XPLMFindDataRef("sim/custom/xap/wheels/ped_disc");
            jar_a320_neo_wheels_anti_skid = XPLMFindDataRef("sim/custom/xap/wheels/ant_skeed");

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

            // SD_PAGE Display
            jar_a320_disp_sys_mode = XPLMFindDataRef("sim/custom/xap/disp/sys/mode");

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
		case JAR_A320NEO_SD_PAGE :
			if ((value >= 0) && (value <= 11)) XPLMSetDatai(jar_a320_disp_sys_mode,(int)value);
			break;
    }
}

