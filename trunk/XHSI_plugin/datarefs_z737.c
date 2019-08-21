/*
 * datarefs_z737.c
 *
 * Manage Laminar Boeing 737-800 + Zibo Mod
 * X-Plane 11 only
 *
 *  Created on: 2 aug. 2019
 *      Author: Nicolas Carel
 *
 * Copyright (C) 2019 Nicolas Carel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */


#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#define XPLM200 1

#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMPlugin.h"

#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"
#include "structs.h"
#include "datarefs.h"
#include "ids.h"
#include "datarefs_z737.h"
#include "qpac_msg.h"

int z737_ready = 0;
int z737_version = 0;
int z737_cdu_ready = 0;

float z737_msg_delay;

// Plugin signature
XPLMPluginID z737_PluginId = XPLM_NO_PLUGIN_ID;

XPLMCommandRef z737_command[Z737_KEY_MAX];


/**
 * Structures & datarefs for CDU
 * We are using the Qpac MCDU packet to encode the data
 */
struct QpacMcduMsgLinesDataPacket zibo737CduMsgPacket;

// Store previous CDU packets to compare before sending
struct QpacMcduMsgLinesDataPacket zibo737Cdu1PreviousMsgPacket;
struct QpacMcduMsgLinesDataPacket zibo737Cdu2PreviousMsgPacket;

float zibo737_msg_delay;

// CDU1
XPLMDataRef z737_fmc1_title_white;
XPLMDataRef z737_fmc1_title_small;
XPLMDataRef z737_fmc1_title_inverted;
XPLMDataRef z737_fmc1_title_magenta;
XPLMDataRef z737_fmc1_title_green;

XPLMDataRef z737_fmc1_label_white[Z737_FMC_LINES];
XPLMDataRef z737_fmc1_content_white[Z737_FMC_LINES];
XPLMDataRef z737_fmc1_content_small[Z737_FMC_LINES];
XPLMDataRef z737_fmc1_content_inverted[Z737_FMC_LINES];
XPLMDataRef z737_fmc1_content_green[Z737_FMC_LINES];
XPLMDataRef z737_fmc1_content_magenta[Z737_FMC_LINES];

XPLMDataRef z737_fmc1_scratch_white;
XPLMDataRef z737_fmc1_scratch_inverted;

// CDU2
XPLMDataRef z737_fmc2_title_white;
XPLMDataRef z737_fmc2_title_small;
XPLMDataRef z737_fmc2_title_inverted;
XPLMDataRef z737_fmc2_title_magenta;
XPLMDataRef z737_fmc2_title_green;

XPLMDataRef z737_fmc2_label_white[Z737_FMC_LINES];
XPLMDataRef z737_fmc2_content_white[Z737_FMC_LINES];
XPLMDataRef z737_fmc2_content_small[Z737_FMC_LINES];
XPLMDataRef z737_fmc2_content_inverted[Z737_FMC_LINES];
XPLMDataRef z737_fmc2_content_green[Z737_FMC_LINES];
XPLMDataRef z737_fmc2_content_magenta[Z737_FMC_LINES];

XPLMDataRef z737_fmc2_scratch_white;
XPLMDataRef z737_fmc2_scratch_inverted;


/*
 * FMS
 */
XPLMDataRef laminar_B738_fms_legs; // laminar/B738/fms/legs [String]
XPLMDataRef laminar_B738_fms_legs_lat; // laminar/B738/fms/legs_lat float[128]
XPLMDataRef laminar_B738_fms_legs_lon; // laminar/B738/fms/legs_lon float[128]
XPLMDataRef laminar_B738_fms_legs_alt_calc; // laminar/B738/fms/legs_alt_calc float[128]
XPLMDataRef laminar_B738_fms_legs_alt_rest1; // laminar/B738/fms/legs_alt_rest1 float[128]
XPLMDataRef laminar_B738_fms_legs_spd; // laminar/B738/fms/legs_spd float[128]

XPLMDataRef laminar_B738_fms_num_of_wpts; // laminar/B738/fms/num_of_wpts float

/* Unused Datarefs grabbed by DRT
 *
 *
737u/cabin/lightP
737u/cabin/lights1
737u/cabin/lights2
737u/cabin/lights3
737u/cabin/lights4
737u/cabin/lightsA
737u/cabin/lightsB
737u/cabin/lightsC
737u/cabin/lightsD
737u/cabin/lightsE
737u/cabin/lightsF
737u/cabin/lightsG
737u/cabin/lightsH
737u/cabin/lightsI
737u/cabin/lightsJ
737u/cabin/lightsK
737u/cabin/lightsL
737u/cabin/lightsM
737u/cabin/lightsN
737u/cabin/lightsO
737u/cabin/lightsQ
737u/cabin/lightsR
737u/cabin/lightsS
737u/cabin/lightsT
737u/cabin/lightsU
737u/cabin/lightsV
737u/cabin/lightsW
737u/cabin/lightsX
737u/cabin/lightsZ
737u/cabin/lightsZZ
737u/cabin/lightY
737u/cabin/set_entry_lighting
737u/doors/aft_Cargo
737u/doors/emerg1
737u/doors/emerg2
737u/doors/emerg3
737u/doors/emerg4
737u/doors/Fwd_Cargo
737u/doors/L1
737u/doors/L2
737u/doors/R1
737u/doors/R2
737x/cabin/entry_lights_x
737x/cabin/lights_x
737x/cabin/set_pax_lighting

laminar/autopilot/alt_hold_mem
laminar/autopilot/ap_on
laminar/autopilot/yoke_shake_cpt
laminar/autopilot/yoke_shake_fo
laminar/B738/73x
laminar/B738/ac_freq_mode0
laminar/B738/ac_freq_mode1
laminar/B738/ac_freq_mode2
laminar/B738/ac_freq_mode3
laminar/B738/ac_freq_mode4
laminar/B738/ac_freq_mode5
laminar/B738/ac_volt_mode1
laminar/B738/ac_volt_mode2
laminar/B738/ac_volt_mode3
laminar/B738/ac_volt_mode4
laminar/B738/ac_volt_mode5
laminar/B738/adjust/capt/handle_pos
laminar/B738/adjust/capt/map_light/x_pos
laminar/B738/adjust/capt/map_light/y_pos
laminar/B738/adjust/fo/handle_pos
laminar/B738/adjust/fo/map_light/x_pos
laminar/B738/adjust/fo/map_light/y_pos
laminar/B738/air/aft_cab_temp/rheostat
laminar/B738/air/apu/bleed_valve_pos
laminar/B738/air/apu_psi
laminar/B738/air/capt/eyeball_vent/x_pos
laminar/B738/air/capt/eyeball_vent/y_pos
laminar/B738/air/cont_cab_temp/rheostat
laminar/B738/air/engine1/bleed_valve_pos
laminar/B738/air/engine1/starter_valve
laminar/B738/air/engine1_psi
laminar/B738/air/engine2/bleed_valve_pos
laminar/B738/air/engine2/starter_valve
laminar/B738/air/engine2_psi
laminar/B738/air/fo/eyeball_vent/x_pos
laminar/B738/air/fo/eyeball_vent/y_pos
laminar/B738/air/fwd_cab_temp/rheostat
laminar/B738/air/isolation/bleed_valve_pos
laminar/B738/air/isolation_valve_pos
laminar/B738/air/jump/eyeball_vent/x_pos
laminar/B738/air/jump/eyeball_vent/y_pos
laminar/B738/air/land_alt_sel/rheostat
laminar/B738/air/l_pack/bleed_valve_pos
laminar/B738/air/l_pack_pos
laminar/B738/air/l_recirc_fan_pos
laminar/B738/air/r_pack/bleed_valve_pos
laminar/B738/air/r_pack_pos
laminar/B738/air/r_recirc_fan_pos
laminar/B738/air/trim_air_pos
laminar/B738/alert/alt_horn_cut_disable
laminar/B738/alert/below_gs_disable
laminar/B738/alert/gear_horn_cut_disable
laminar/b738/alert/pfd_pull_up
laminar/b738/alert/pfd_windshear
laminar/B738/annunciator/aft_cargo
laminar/B738/annunciator/aft_entry
laminar/B738/annunciator/aft_service
laminar/B738/annunciator/altn_press
laminar/B738/annunciator/anti_skid_inop
laminar/B738/annunciator/apu_bottle_discharge
laminar/B738/annunciator/apu_fault
laminar/B738/annunciator/apu_fire
laminar/B738/annunciator/apu_gen_off_bus
laminar/B738/annunciator/apu_low_oil
laminar/B738/annunciator/ap_disconnect
laminar/B738/annunciator/ap_disconnect1
laminar/B738/annunciator/ap_disconnect2
laminar/B738/annunciator/at_disconnect
laminar/B738/annunciator/at_disconnect1
laminar/B738/annunciator/at_disconnect2
laminar/B738/annunciator/at_fms_disconnect1
laminar/B738/annunciator/at_fms_disconnect2
laminar/B738/annunciator/autofail
laminar/B738/annunciator/auto_brake_disarm
laminar/B738/annunciator/auto_slat_fail
laminar/B738/annunciator/bat_discharge
laminar/B738/annunciator/below_gs
laminar/B738/annunciator/bleed_trip_1
laminar/B738/annunciator/bleed_trip_2
laminar/B738/annunciator/bypass_filter_1
laminar/B738/annunciator/bypass_filter_2
laminar/B738/annunciator/cabin_alt
laminar/B738/annunciator/capt_aoa_off
laminar/B738/annunciator/capt_pitot_off
laminar/B738/annunciator/cargo_fault_detector
laminar/B738/annunciator/cargo_fire
laminar/B738/annunciator/cowl_ice_0
laminar/B738/annunciator/cowl_ice_1
laminar/B738/annunciator/cowl_ice_on_0
laminar/B738/annunciator/cowl_ice_on_1
laminar/B738/annunciator/crossfeed
laminar/B738/annunciator/door_auto_unlk
laminar/B738/annunciator/door_lock_fail
laminar/B738/annunciator/drive1
laminar/B738/annunciator/drive2
laminar/B738/annunciator/dual_bleed
laminar/B738/annunciator/elt
laminar/B738/annunciator/el_hyd_ovht_1
laminar/B738/annunciator/el_hyd_ovht_2
laminar/B738/annunciator/emer_exit
laminar/B738/annunciator/eng1_valve_closed
laminar/B738/annunciator/eng2_valve_closed
laminar/B738/annunciator/engine1_fire
laminar/B738/annunciator/engine1_ovht
laminar/B738/annunciator/engine2_fire
laminar/B738/annunciator/engine2_ovht
laminar/B738/annunciator/equip_door
laminar/B738/annunciator/extinguisher_circuit_annun2
laminar/B738/annunciator/extinguisher_circuit_annun_apu
laminar/B738/annunciator/extinguisher_circuit_annun_left
laminar/B738/annunciator/extinguisher_circuit_annun_right
laminar/B738/annunciator/fadec1_off
laminar/B738/annunciator/fadec2_off
laminar/B738/annunciator/fadec_fail_0
laminar/B738/annunciator/fadec_fail_1
laminar/B738/annunciator/fdr_off
laminar/B738/annunciator/feel_diff_press
laminar/B738/annunciator/fire_bell_annun
laminar/B738/annunciator/fire_fault_inop
laminar/B738/annunciator/flaps_test
laminar/B738/annunciator/fo_aoa_off
laminar/B738/annunciator/fo_pitot_off
laminar/B738/annunciator/fwd_cargo
laminar/B738/annunciator/fwd_entry
laminar/B738/annunciator/fwd_service
laminar/B738/annunciator/generic
laminar/B738/annunciator/gen_off_bus1
laminar/B738/annunciator/gen_off_bus2
laminar/B738/annunciator/gps
laminar/B738/annunciator/gpws
laminar/B738/annunciator/ground_power_avail
laminar/B738/annunciator/hyd_A_rud
laminar/B738/annunciator/hyd_B_rud
laminar/B738/annunciator/hyd_el_press_a
laminar/B738/annunciator/hyd_el_press_b
laminar/B738/annunciator/hyd_press_a
laminar/B738/annunciator/hyd_press_b
laminar/B738/annunciator/hyd_stdby_rud
laminar/B738/annunciator/irs/align_fail_left
laminar/B738/annunciator/irs/align_fail_right
laminar/B738/annunciator/irs_align_fail_left
laminar/B738/annunciator/irs_align_fail_right
laminar/B738/annunciator/irs_align_left
laminar/B738/annunciator/irs_align_left2
laminar/B738/annunciator/irs_align_right
laminar/B738/annunciator/irs_align_right2
laminar/B738/annunciator/irs_dc_fail_left
laminar/B738/annunciator/irs_dc_fail_right
laminar/B738/annunciator/irs_left_fail
laminar/B738/annunciator/irs_on_dc_left
laminar/B738/annunciator/irs_on_dc_right
laminar/B738/annunciator/irs_right_fail
laminar/B738/annunciator/left_aft_overwing
laminar/B738/annunciator/left_fwd_overwing
laminar/B738/annunciator/left_gear_safe
laminar/B738/annunciator/left_gear_transit
laminar/B738/annunciator/low_fuel_press_c1
laminar/B738/annunciator/low_fuel_press_c2
laminar/B738/annunciator/low_fuel_press_l1
laminar/B738/annunciator/low_fuel_press_l2
laminar/B738/annunciator/low_fuel_press_r1
laminar/B738/annunciator/low_fuel_press_r2
laminar/B738/annunciator/l_bottle_discharge
laminar/B738/annunciator/manual_press
laminar/B738/annunciator/master_caution_light
laminar/B738/annunciator/nose_gear_safe
laminar/B738/annunciator/nose_gear_transit
laminar/B738/annunciator/off_sched_descent
laminar/B738/annunciator/pack_left
laminar/B738/annunciator/pack_right
laminar/B738/annunciator/parking_brake
laminar/B738/annunciator/pax_oxy
laminar/B738/annunciator/ram_door_open1
laminar/B738/annunciator/ram_door_open2
laminar/B738/annunciator/reverser_fail_0
laminar/B738/annunciator/reverser_fail_1
laminar/B738/annunciator/right_aft_overwing
laminar/B738/annunciator/right_fwd_overwing
laminar/B738/annunciator/right_gear_safe
laminar/B738/annunciator/right_gear_transit
laminar/B738/annunciator/r_bottle_discharge
laminar/B738/annunciator/six_pack_air_cond
laminar/B738/annunciator/six_pack_apu
laminar/B738/annunciator/six_pack_doors
laminar/B738/annunciator/six_pack_elec
laminar/B738/annunciator/six_pack_eng
laminar/B738/annunciator/six_pack_fire
laminar/B738/annunciator/six_pack_flt_cont
laminar/B738/annunciator/six_pack_fuel
laminar/B738/annunciator/six_pack_hyd
laminar/B738/annunciator/six_pack_ice
laminar/B738/annunciator/six_pack_irs
laminar/B738/annunciator/six_pack_overhead
laminar/B738/annunciator/slats_extend
laminar/B738/annunciator/slats_transit
laminar/B738/annunciator/smoke
laminar/B738/annunciator/source_off1
laminar/B738/annunciator/source_off2
laminar/B738/annunciator/spar1_valve_closed
laminar/B738/annunciator/spar2_valve_closed
laminar/B738/annunciator/spd_brk_not_arm
laminar/B738/annunciator/speedbrake_armed
laminar/B738/annunciator/speedbrake_extend
laminar/B738/annunciator/stab_out_of_trim
laminar/B738/annunciator/standby_pwr_off
laminar/B738/annunciator/std_rud_on
laminar/B738/annunciator/takeoff_config
laminar/B738/annunciator/test
laminar/B738/annunciator/trans_bus_off1
laminar/B738/annunciator/trans_bus_off2
laminar/B738/annunciator/wheel_well_fire
laminar/B738/annunciator/window_heat_l_fwd
laminar/B738/annunciator/window_heat_l_side
laminar/B738/annunciator/window_heat_ovht_lf
laminar/B738/annunciator/window_heat_ovht_ls
laminar/B738/annunciator/window_heat_ovht_rf
laminar/B738/annunciator/window_heat_ovht_rs
laminar/B738/annunciator/window_heat_r_fwd
laminar/B738/annunciator/window_heat_r_side
laminar/B738/annunciator/wing_body_ovht_left
laminar/B738/annunciator/wing_body_ovht_right
laminar/B738/annunciator/wing_ice_on_L
laminar/B738/annunciator/wing_ice_on_R
laminar/B738/annunciator/yaw_damp
laminar/B738/aoa_ra
laminar/B738/ap/fac_engaged
laminar/B738/ap/ils_active
laminar/B738/ap/loc_gp_engaged
laminar/B738/ap_colour
laminar/B738/audio/capt/mic_button1
laminar/B738/audio/capt/mic_button2
laminar/B738/audio/capt/mic_button3
laminar/B738/audio/capt/mic_button4
laminar/B738/audio/capt/mic_button5
laminar/B738/audio/capt/mic_button6
laminar/B738/audio/capt/mic_indicator1
laminar/B738/audio/capt/mic_indicator2
laminar/B738/audio/capt/mic_indicator3
laminar/B738/audio/capt/mic_indicator4
laminar/B738/audio/capt/mic_indicator5
laminar/B738/audio/capt/mic_indicator6
laminar/B738/audio/fo/mic_button1
laminar/B738/audio/fo/mic_button2
laminar/B738/audio/fo/mic_button3
laminar/B738/audio/fo/mic_button4
laminar/B738/audio/fo/mic_button5
laminar/B738/audio/fo/mic_button6
laminar/B738/audio/fo/mic_indicator1
laminar/B738/audio/fo/mic_indicator2
laminar/B738/audio/fo/mic_indicator3
laminar/B738/audio/fo/mic_indicator4
laminar/B738/audio/fo/mic_indicator5
laminar/B738/audio/fo/mic_indicator6
laminar/B738/audio/indicators/audio_marker_enabled
laminar/B738/audio/indicators/audio_selection_com1
laminar/B738/audio/indicators/audio_selection_com2
laminar/B738/audio/indicators/audio_selection_nav1
laminar/B738/audio/indicators/audio_selection_nav2
laminar/B738/audio/indicators/com1_avail
laminar/B738/audio/indicators/com2_avail
laminar/B738/audio/indicators/mark_avail
laminar/B738/audio/indicators/nav1_avail
laminar/B738/audio/indicators/nav2_avail
laminar/B738/audio/obs/mic_button1
laminar/B738/audio/obs/mic_button2
laminar/B738/audio/obs/mic_button3
laminar/B738/audio/obs/mic_button4
laminar/B738/audio/obs/mic_button5
laminar/B738/audio/obs/mic_button6
laminar/B738/audio/obs/mic_indicator1
laminar/B738/audio/obs/mic_indicator2
laminar/B738/audio/obs/mic_indicator3
laminar/B738/audio/obs/mic_indicator4
laminar/B738/audio/obs/mic_indicator5
laminar/B738/audio/obs/mic_indicator6
laminar/B738/autobrake/autobrake_arm
laminar/B738/autobrake/autobrake_disarm
laminar/B738/autobrake/autobrake_pos
laminar/B738/autobrake/autobrake_pos2
laminar/B738/autobrake/autobrake_RTO_arm
laminar/B738/autobrake/autobrake_RTO_test
laminar/B738/autobrake_1
laminar/B738/autobrake_2
laminar/B738/autobrake_3
laminar/B738/autobrake_max
laminar/B738/autogate_gpu
laminar/B738/autogate_nearest
laminar/B738/autopilot/airspeed
laminar/B738/autopilot/airspeed_mach
laminar/B738/autopilot/altitude
laminar/B738/autopilot/altitude_mode
laminar/B738/autopilot/altitude_mode2
laminar/B738/autopilot/alt_acq_pfd
laminar/B738/autopilot/alt_disagree
laminar/B738/autopilot/alt_hld_pos
laminar/B738/autopilot/alt_hld_status
laminar/B738/autopilot/alt_interv_pos
laminar/B738/autopilot/alt_mode_pfd
laminar/B738/autopilot/app_pos
laminar/B738/autopilot/app_status
laminar/B738/autopilot/ap_goaround
laminar/B738/autopilot/ap_vvi_pos
laminar/B738/autopilot/ap_warn
laminar/B738/autopilot/autoland_status
laminar/B738/autopilot/autopilot_source
laminar/B738/autopilot/autothrottle_arm_pos
laminar/B738/autopilot/autothrottle_on_pfd
laminar/B738/autopilot/autothrottle_pfd
laminar/B738/autopilot/autothrottle_status
laminar/B738/autopilot/bank_angle_pos
laminar/B738/autopilot/blink
laminar/B738/autopilot/change_over_pos
laminar/B738/autopilot/cmd_a_pos
laminar/B738/autopilot/cmd_a_status
laminar/B738/autopilot/cmd_b_pos
laminar/B738/autopilot/cmd_b_status
laminar/B738/autopilot/course_copilot
laminar/B738/autopilot/course_pilot
laminar/B738/autopilot/cws_a_pos
laminar/B738/autopilot/cws_a_status
laminar/B738/autopilot/cws_b_pos
laminar/B738/autopilot/cws_b_status
laminar/B738/autopilot/cws_p_status
laminar/B738/autopilot/cws_r_status
laminar/B738/autopilot/cws_status
laminar/B738/autopilot/dev_alt_alert
laminar/B738/autopilot/disconnect_button
laminar/B738/autopilot/disconnect_pos
laminar/B738/autopilot/fac_horizont
laminar/B738/autopilot/fd_goaround
laminar/B738/autopilot/fd_pitch_copilot_show
laminar/B738/autopilot/fd_pitch_pilot_show
laminar/B738/autopilot/fd_roll_copilot_show
laminar/B738/autopilot/fd_roll_pilot_show
laminar/B738/autopilot/flare_status
laminar/B738/autopilot/flight_director_fo_pos
laminar/B738/autopilot/flight_director_pos
laminar/B738/autopilot/flight_direct_on
laminar/B738/autopilot/fmc_approach_alt
laminar/B738/autopilot/fmc_climb_r_alt1
laminar/B738/autopilot/fmc_climb_r_alt2
laminar/B738/autopilot/fmc_climb_r_speed1
laminar/B738/autopilot/fmc_climb_r_speed2
laminar/B738/autopilot/fmc_climb_speed
laminar/B738/autopilot/fmc_climb_speed_l
laminar/B738/autopilot/fmc_climb_speed_mach
laminar/B738/autopilot/fmc_cruise_alt
laminar/B738/autopilot/fmc_cruise_speed
laminar/B738/autopilot/fmc_cruise_speed_mach
laminar/B738/autopilot/fmc_descent_alt
laminar/B738/autopilot/fmc_descent_now
laminar/B738/autopilot/fmc_descent_r_alt1
laminar/B738/autopilot/fmc_descent_r_alt2
laminar/B738/autopilot/fmc_descent_r_speed1
laminar/B738/autopilot/fmc_descent_r_speed2
laminar/B738/autopilot/fmc_descent_speed
laminar/B738/autopilot/fmc_descent_speed_mach
laminar/B738/autopilot/fmc_mode
laminar/B738/autopilot/fmc_spd_pfd
laminar/B738/autopilot/fmc_speed
laminar/B738/autopilot/fmc_was_on_cruise
laminar/B738/autopilot/ga_pfd
laminar/B738/autopilot/gps_horizont
laminar/B738/autopilot/gs_armed_pfd
laminar/B738/autopilot/hdg_mode_pfd
laminar/B738/autopilot/hdg_sel_pos
laminar/B738/autopilot/hdg_sel_status
laminar/B738/autopilot/heading_mode
laminar/B738/autopilot/hnav_armed
laminar/B738/autopilot/ias_disagree
laminar/B738/autopilot/ils_pointer_disable
laminar/B738/autopilot/kts_disable
laminar/B738/autopilot/left_at_diseng_pos
laminar/B738/autopilot/left_toga_pos
laminar/B738/autopilot/lnav_ap_wca
laminar/B738/autopilot/lnav_engaged
laminar/B738/autopilot/lnav_pos
laminar/B738/autopilot/lnav_status
laminar/B738/autopilot/lnav_status2
laminar/B738/autopilot/lock_throttle
laminar/B738/autopilot/lvl_chg_mode
laminar/B738/autopilot/lvl_chg_pos
laminar/B738/autopilot/lvl_chg_status
laminar/B738/autopilot/mach_disable
laminar/B738/autopilot/master_capt_status
laminar/B738/autopilot/master_fo_status
laminar/B738/autopilot/mcp_alt_dial
laminar/B738/autopilot/mcp_alt_dial2
laminar/B738/autopilot/mcp_alt_dial2_fo
laminar/B738/autopilot/mcp_hdg_dial
laminar/B738/autopilot/mcp_speed_dial_kts
laminar/B738/autopilot/mcp_speed_dial_kts2
laminar/B738/autopilot/mcp_speed_dial_kts2_fo
laminar/B738/autopilot/mcp_speed_dial_kts_mach
laminar/B738/autopilot/n1_pfd
laminar/B738/autopilot/n1_pos
laminar/B738/autopilot/n1_status
laminar/B738/autopilot/pfd_alt_mode
laminar/B738/autopilot/pfd_alt_mode_arm
laminar/B738/autopilot/pfd_fd_cmd
laminar/B738/autopilot/pfd_fd_cmd_fo
laminar/B738/autopilot/pfd_fd_cmd_fo_show
laminar/B738/autopilot/pfd_fd_cmd_show
laminar/B738/autopilot/pfd_hdg_mode
laminar/B738/autopilot/pfd_hdg_mode_arm
laminar/B738/autopilot/pfd_mode
laminar/B738/autopilot/pfd_mode_fo
laminar/B738/autopilot/pfd_spd_mode
laminar/B738/autopilot/pfd_vorloc_lnav
laminar/B738/autopilot/rec_alt_alert
laminar/B738/autopilot/rec_alt_modes
laminar/B738/autopilot/rec_app_alert
laminar/B738/autopilot/rec_cmd_modes
laminar/B738/autopilot/rec_cmd_modes_fo
laminar/B738/autopilot/rec_cwr_p_mode
laminar/B738/autopilot/rec_cwr_r_mode
laminar/B738/autopilot/rec_hdg_modes
laminar/B738/autopilot/rec_sch_modes
laminar/B738/autopilot/rec_thr2_modes
laminar/B738/autopilot/rec_thr_modes
laminar/B738/autopilot/retard_status
laminar/B738/autopilot/right_at_diseng_pos
laminar/B738/autopilot/right_toga_pos
laminar/B738/autopilot/rollout_status
laminar/B738/autopilot/show_ias
laminar/B738/autopilot/single_ch_status
laminar/B738/autopilot/source_arm_pilot
laminar/B738/autopilot/source_pilot
laminar/B738/autopilot/spd_interv_pos
laminar/B738/autopilot/spd_interv_status
laminar/B738/autopilot/speed_mode
laminar/B738/autopilot/speed_pos
laminar/B738/autopilot/speed_status1
laminar/B738/autopilot/thr_hld_pfd
laminar/B738/autopilot/to_ga_pfd
laminar/B738/autopilot/vnav_active
laminar/B738/autopilot/vnav_alt_mode
laminar/B738/autopilot/vnav_alt_pfd
laminar/B738/autopilot/vnav_arm_pfd
laminar/B738/autopilot/vnav_pos
laminar/B738/autopilot/vnav_pth_pfd
laminar/B738/autopilot/vnav_spd_pfd
laminar/B738/autopilot/vnav_speed_trg
laminar/B738/autopilot/vnav_status1
laminar/B738/autopilot/vnav_status2
laminar/B738/autopilot/vorloc_pos
laminar/B738/autopilot/vorloc_status
laminar/B738/autopilot/vs_pos
laminar/B738/autopilot/vs_status
laminar/B738/autopilot/vvi
laminar/B738/autopilot/vvi_dial_show
laminar/B738/autopilot/vvi_status_pfd
laminar/B738/axis/flap_lever
laminar/B738/axis/heading
laminar/B738/axis/heading2
laminar/B738/axis/left_toe_brake
laminar/B738/axis/nosewheel
laminar/B738/axis/pitch
laminar/B738/axis/pitch2
laminar/B738/axis/right_toe_brake
laminar/B738/axis/roll
laminar/B738/axis/roll2
laminar/B738/axis/speedbrake_lever
laminar/B738/axis/throttle1
laminar/B738/axis/throttle2
laminar/B738/brake/brake_press
laminar/B738/brightness_level2
laminar/B738/brunner_enable
laminar/B738/buttons/autopilot/alt_hld
laminar/B738/buttons/autopilot/alt_intv
laminar/B738/buttons/autopilot/app
laminar/B738/buttons/autopilot/cmd_a
laminar/B738/buttons/autopilot/cmd_b
laminar/B738/buttons/autopilot/co
laminar/B738/buttons/autopilot/cws_a
laminar/B738/buttons/autopilot/cws_b
laminar/B738/buttons/autopilot/hdg_sel
laminar/B738/buttons/autopilot/lnav
laminar/B738/buttons/autopilot/lvl_chg
laminar/B738/buttons/autopilot/n1
laminar/B738/buttons/autopilot/spd_intv
laminar/B738/buttons/autopilot/speed
laminar/B738/buttons/autopilot/vnav
laminar/B738/buttons/autopilot/vor_loc
laminar/B738/buttons/autopilot/vs
laminar/B738/buttons/capt_6_pack_pos
laminar/B738/buttons/fmc/capt/0
laminar/B738/buttons/fmc/capt/1
laminar/B738/buttons/fmc/capt/1L
laminar/B738/buttons/fmc/capt/1R
laminar/B738/buttons/fmc/capt/2
laminar/B738/buttons/fmc/capt/2L
laminar/B738/buttons/fmc/capt/2R
laminar/B738/buttons/fmc/capt/3
laminar/B738/buttons/fmc/capt/3L
laminar/B738/buttons/fmc/capt/3R
laminar/B738/buttons/fmc/capt/4
laminar/B738/buttons/fmc/capt/4L
laminar/B738/buttons/fmc/capt/4R
laminar/B738/buttons/fmc/capt/5
laminar/B738/buttons/fmc/capt/5L
laminar/B738/buttons/fmc/capt/5R
laminar/B738/buttons/fmc/capt/6
laminar/B738/buttons/fmc/capt/6L
laminar/B738/buttons/fmc/capt/6R
laminar/B738/buttons/fmc/capt/7
laminar/B738/buttons/fmc/capt/8
laminar/B738/buttons/fmc/capt/9
laminar/B738/buttons/fmc/capt/a
laminar/B738/buttons/fmc/capt/b
laminar/B738/buttons/fmc/capt/c
laminar/B738/buttons/fmc/capt/clb
laminar/B738/buttons/fmc/capt/clr
laminar/B738/buttons/fmc/capt/crz
laminar/B738/buttons/fmc/capt/d
laminar/B738/buttons/fmc/capt/del
laminar/B738/buttons/fmc/capt/dep_arr
laminar/B738/buttons/fmc/capt/des
laminar/B738/buttons/fmc/capt/e
laminar/B738/buttons/fmc/capt/exec
laminar/B738/buttons/fmc/capt/f
laminar/B738/buttons/fmc/capt/fix
laminar/B738/buttons/fmc/capt/g
laminar/B738/buttons/fmc/capt/h
laminar/B738/buttons/fmc/capt/hold
laminar/B738/buttons/fmc/capt/i
laminar/B738/buttons/fmc/capt/init_ref
laminar/B738/buttons/fmc/capt/j
laminar/B738/buttons/fmc/capt/k
laminar/B738/buttons/fmc/capt/l
laminar/B738/buttons/fmc/capt/legs
laminar/B738/buttons/fmc/capt/m
laminar/B738/buttons/fmc/capt/menu
laminar/B738/buttons/fmc/capt/minus
laminar/B738/buttons/fmc/capt/n
laminar/B738/buttons/fmc/capt/n1_limit
laminar/B738/buttons/fmc/capt/next
laminar/B738/buttons/fmc/capt/o
laminar/B738/buttons/fmc/capt/p
laminar/B738/buttons/fmc/capt/period
laminar/B738/buttons/fmc/capt/prev
laminar/B738/buttons/fmc/capt/prog
laminar/B738/buttons/fmc/capt/q
laminar/B738/buttons/fmc/capt/r
laminar/B738/buttons/fmc/capt/rte
laminar/B738/buttons/fmc/capt/s
laminar/B738/buttons/fmc/capt/slash
laminar/B738/buttons/fmc/capt/sp
laminar/B738/buttons/fmc/capt/t
laminar/B738/buttons/fmc/capt/u
laminar/B738/buttons/fmc/capt/v
laminar/B738/buttons/fmc/capt/w
laminar/B738/buttons/fmc/capt/x
laminar/B738/buttons/fmc/capt/y
laminar/B738/buttons/fmc/capt/z
laminar/B738/buttons/fo_6_pack_pos
laminar/B738/buttons/mfd_eng_pos
laminar/B738/buttons/mfd_sys_pos
laminar/B738/button_switch/cover_position
laminar/B738/cabin_alt
laminar/B738/cabin_altitude_actual
laminar/B738/cabin_pressure_diff
laminar/B738/cabin_temp
laminar/B738/cabin_vvi
laminar/B738/capt/yoke_ap_disengage
laminar/B738/capt/yoke_checklist
laminar/B738/checklist/manip_enable
laminar/B738/checklist/pos_x
laminar/B738/checklist/pos_y
laminar/B738/checklist/pos_z
laminar/B738/checklist/rot_x
laminar/B738/checklist/rot_y
laminar/B738/clock/captain/chr
laminar/B738/clock/captain/chrono_minutes
laminar/B738/clock/captain/chrono_mode
laminar/B738/clock/captain/chrono_seconds
laminar/B738/clock/captain/chrono_seconds_needle
laminar/B738/clock/captain/et
laminar/B738/clock/captain/et_hours
laminar/B738/clock/captain/et_minutes
laminar/B738/clock/captain/et_mode
laminar/B738/clock/captain/et_seconds
laminar/B738/clock/captain/reset
laminar/B738/clock/captain/time
laminar/B738/clock/chrono_display_mode_capt
laminar/B738/clock/chrono_display_mode_fo
laminar/B738/clock/clock_capt_power
laminar/B738/clock/clock_display_mode_capt
laminar/B738/clock/clock_display_mode_fo
laminar/B738/clock/clock_fo_power
laminar/B738/clock/fo/chr
laminar/B738/clock/fo/chrono_minutes
laminar/B738/clock/fo/chrono_mode
laminar/B738/clock/fo/chrono_seconds
laminar/B738/clock/fo/chrono_seconds_needle
laminar/B738/clock/fo/et
laminar/B738/clock/fo/et_hours
laminar/B738/clock/fo/et_minutes
laminar/B738/clock/fo/et_mode
laminar/B738/clock/fo/et_seconds
laminar/B738/clock/fo/reset
laminar/B738/clock/fo/time
laminar/B738/comm/audio_sel_com1
laminar/B738/comm/audio_sel_com2
laminar/B738/comm/audio_sel_marker
laminar/B738/comm/audio_sel_nav1
laminar/B738/comm/audio_sel_nav2
laminar/B738/comm/com1_audio_sel_pos
laminar/B738/comm/com2_audio_sel_pos
laminar/B738/comm/mark_audio_sel_pos
laminar/B738/comm/nav1_audio_sel_pos
laminar/B738/comm/nav1_status
laminar/B738/comm/nav2_audio_sel_pos
laminar/B738/comm/nav2_status
laminar/B738/comm/push_button/rtp_L_freq_swap
laminar/B738/comm/push_button/rtp_R_freq_swap
laminar/B738/comm/rtp_L/am_status
laminar/B738/comm/rtp_L/freq_khz/sel_dial_pos
laminar/B738/comm/rtp_L/freq_MHz/sel_dial_pos
laminar/B738/comm/rtp_L/hf_1_status
laminar/B738/comm/rtp_L/hf_2_status
laminar/B738/comm/rtp_L/hf_sens_ctrl/rheostat
laminar/B738/comm/rtp_L/lcd_to_display
laminar/B738/comm/rtp_L/offside_tuning_status
laminar/B738/comm/rtp_L/off_status
laminar/B738/comm/rtp_L/vhf_1_status
laminar/B738/comm/rtp_L/vhf_2_status
laminar/B738/comm/rtp_L/vhf_3_status
laminar/B738/comm/rtp_R/am_status
laminar/B738/comm/rtp_R/freq_khz/sel_dial_pos
laminar/B738/comm/rtp_R/freq_MHz/sel_dial_pos
laminar/B738/comm/rtp_R/hf_1_status
laminar/B738/comm/rtp_R/hf_2_status
laminar/B738/comm/rtp_R/hf_sens_ctrl/rheostat
laminar/B738/comm/rtp_R/lcd_to_display
laminar/B738/comm/rtp_R/offside_tuning_status
laminar/B738/comm/rtp_R/off_status
laminar/B738/comm/rtp_R/vhf_1_status
laminar/B738/comm/rtp_R/vhf_2_status
laminar/B738/comm/rtp_R/vhf_3_status
laminar/B738/controls/gear_handle_down
laminar/B738/decimals_show
laminar/B738/de_ice_truck/act
laminar/B738/de_ice_truck/hide
laminar/B738/de_ice_truck/holdover_time
laminar/B738/de_ice_truck/pos
laminar/B738/de_ice_truck_available
laminar/B738/display_units/idu_cpt
laminar/B738/display_units/idu_fo
laminar/B738/display_units/ldu
laminar/B738/display_units/odu_cpt
laminar/B738/display_units/odu_fo
laminar/B738/display_units/udu
laminar/B738/door/flt_dk_door_knob_pos
laminar/B738/door/flt_dk_door_ratio
laminar/B738/dspl_light_test
laminar/B738/effect/gauges_reflection
laminar/B738/effect/hide_glass
laminar/B738/effect/hide_windows_xt
laminar/B738/effect/nd_rings
laminar/B738/effect/rain_angle_left
laminar/B738/effect/rain_life
laminar/B738/effect/rain_rate1
laminar/B738/effect/rain_rate2
laminar/B738/effect/rain_ratio
laminar/B738/effect/rain_size
laminar/B738/effect/rain_speed
laminar/B738/effect/tire_blown
laminar/B738/effect/vortex
laminar/B738/effect/vortex2
laminar/B738/effect/windowL
laminar/B738/effect/windowL02
laminar/B738/effect/windowL03
laminar/B738/effect/windowL04
laminar/B738/effect/windowL05
laminar/B738/effect/windowLS
laminar/B738/effect/windowLS02
laminar/B738/effect/windowLS03
laminar/B738/effect/windowLS04
laminar/B738/effect/windowLS05
laminar/B738/effect/windowR
laminar/B738/effect/windowR02
laminar/B738/effect/windowR03
laminar/B738/effect/windowR04
laminar/B738/effect/windowR05
laminar/B738/effect/windowRS
laminar/B738/effect/windowRS02
laminar/B738/effect/windowRS03
laminar/B738/effect/windowRS04
laminar/B738/effect/windowRS05
laminar/B738/effect/window_animL
laminar/B738/effect/window_animL02
laminar/B738/effect/window_animR
laminar/B738/effect/window_animR02
laminar/B738/effect/window_cabin
laminar/B738/effects/backlight_color
laminar/B738/effects/brake_temp
laminar/B738/effects/com_decimal
laminar/B738/effects/flight_control
laminar/B738/effects/fuelgauge
laminar/B738/effects/hardware_thr_lever
laminar/B738/effects/irs_ding
laminar/B738/effects/long_led_strobes
laminar/B738/effects/nosewheel
laminar/B738/effects/passengers
laminar/B738/effects/pulse_lite
laminar/B738/effects/smart_ap_knob
laminar/B738/effects/sync_pilot
laminar/B738/EFIS/baro_box_copilot_show
laminar/B738/EFIS/baro_box_pilot_show
laminar/B738/EFIS/baro_sel_copilot_show
laminar/B738/EFIS/baro_sel_in_hg_copilot
laminar/B738/EFIS/baro_sel_in_hg_pilot
laminar/B738/EFIS/baro_sel_pilot_show
laminar/B738/EFIS/baro_set_std_copilot
laminar/B738/EFIS/baro_set_std_pilot
laminar/B738/EFIS/baro_std_box_copilot_show
laminar/B738/EFIS/baro_std_box_pilot_show
laminar/B738/EFIS/capt/data_status
laminar/B738/EFIS/capt/map_range
laminar/B738/EFIS/EFIS_airport_on
laminar/B738/EFIS/EFIS_fix_on
laminar/B738/EFIS/EFIS_vor_on
laminar/B738/EFIS/EFIS_wx_on
laminar/B738/EFIS/fo/data_status
laminar/B738/EFIS/fo/EFIS_airport_on
laminar/B738/EFIS/fo/EFIS_fix_on
laminar/B738/EFIS/fo/EFIS_vor_on
laminar/B738/EFIS/fo/EFIS_wx_on
laminar/B738/EFIS/fo/map_range
laminar/B738/EFIS/green_arc
laminar/B738/EFIS/green_arc_fo
laminar/B738/EFIS/green_arc_fo_show
laminar/B738/EFIS/green_arc_show
laminar/B738/EFIS/ta_only_show
laminar/B738/EFIS/ta_only_show_fo
laminar/B738/EFIS/tcas_ai_show
laminar/B738/EFIS/tcas_ai_show_fo
laminar/B738/EFIS/tcas_fail_show
laminar/B738/EFIS/tcas_fail_show_fo
laminar/B738/EFIS/tcas_off_show
laminar/B738/EFIS/tcas_off_show_fo
laminar/B738/EFIS/tcas_on
laminar/B738/EFIS/tcas_on_fo
laminar/B738/EFIS/tcas_show
laminar/B738/EFIS/tcas_show_fo
laminar/B738/EFIS/tcas_test_show
laminar/B738/EFIS/tcas_test_show_fo
laminar/B738/EFIS/tfc_show
laminar/B738/EFIS/tfc_show_fo
laminar/B738/EFIS_control/capt/baro_in_hpa
laminar/B738/EFIS_control/capt/baro_in_hpa_pfd
laminar/B738/EFIS_control/capt/exp_map
laminar/B738/EFIS_control/capt/map_mode_pos
laminar/B738/EFIS_control/capt/minimums_dh
laminar/B738/EFIS_control/capt/minimums_dh_pfd
laminar/B738/EFIS_control/capt/push_button/arpt
laminar/B738/EFIS_control/capt/push_button/ctr
laminar/B738/EFIS_control/capt/push_button/data
laminar/B738/EFIS_control/capt/push_button/fpv
laminar/B738/EFIS_control/capt/push_button/mtrs
laminar/B738/EFIS_control/capt/push_button/pos
laminar/B738/EFIS_control/capt/push_button/rst
laminar/B738/EFIS_control/capt/push_button/sta
laminar/B738/EFIS_control/capt/push_button/std
laminar/B738/EFIS_control/capt/push_button/terr
laminar/B738/EFIS_control/capt/push_button/tfc
laminar/B738/EFIS_control/capt/push_button/wpt
laminar/B738/EFIS_control/capt/push_button/wxr
laminar/B738/EFIS_control/capt/rings
laminar/B738/EFIS_control/capt/terr_on
laminar/B738/EFIS_control/capt/vor1_off_pfd
laminar/B738/EFIS_control/capt/vor1_off_pos
laminar/B738/EFIS_control/capt/vor2_off_pfd
laminar/B738/EFIS_control/capt/vor2_off_pos
laminar/B738/EFIS_control/capt/vsd_map
laminar/B738/EFIS_control/cpt/minimums
laminar/B738/EFIS_control/cpt/minimums_pfd
laminar/B738/EFIS_control/fo/baro_in_hpa
laminar/B738/EFIS_control/fo/baro_in_hpa_fo
laminar/B738/EFIS_control/fo/exp_map
laminar/B738/EFIS_control/fo/map_mode_pos
laminar/B738/EFIS_control/fo/minimums
laminar/B738/EFIS_control/fo/minimums_dh
laminar/B738/EFIS_control/fo/minimums_dh_pfd
laminar/B738/EFIS_control/fo/minimums_pfd
laminar/B738/EFIS_control/fo/push_button/arpt
laminar/B738/EFIS_control/fo/push_button/ctr
laminar/B738/EFIS_control/fo/push_button/data
laminar/B738/EFIS_control/fo/push_button/fpv
laminar/B738/EFIS_control/fo/push_button/mtrs
laminar/B738/EFIS_control/fo/push_button/pos
laminar/B738/EFIS_control/fo/push_button/rst
laminar/B738/EFIS_control/fo/push_button/sta
laminar/B738/EFIS_control/fo/push_button/std
laminar/B738/EFIS_control/fo/push_button/terr
laminar/B738/EFIS_control/fo/push_button/tfc
laminar/B738/EFIS_control/fo/push_button/wpt
laminar/B738/EFIS_control/fo/push_button/wxr
laminar/B738/EFIS_control/fo/rings
laminar/B738/EFIS_control/fo/terr_on
laminar/B738/EFIS_control/fo/vor1_off_pfd
laminar/B738/EFIS_control/fo/vor1_off_pos
laminar/B738/EFIS_control/fo/vor2_off_pfd
laminar/B738/EFIS_control/fo/vor2_off_pos
laminar/B738/EFIS_control/fo/vsd_map
laminar/B738/eicas/assum_temp_show
laminar/B738/eicas/eng1_tai
laminar/B738/eicas/eng2_tai
laminar/B738/electric/ac_stdbus_status
laminar/B738/electric/ac_tnsbus1_status
laminar/B738/electric/ac_tnsbus2_status
laminar/B738/electric/apu_start_load
laminar/B738/electric/batbus_status
laminar/B738/electric/dc_bus1_status
laminar/B738/electric/dc_bus2_status
laminar/B738/electric/dc_stdbus_status
laminar/B738/electric/gen1_available
laminar/B738/electric/gen2_available
laminar/B738/electric/hot_batbus_status
laminar/B738/electric/instrument_brightness
laminar/B738/electric/panel_brightness
laminar/B738/electric/standby_bat_pos
laminar/B738/electrical/apu_bus_enable
laminar/B738/electrical/apu_door
laminar/B738/electrical/apu_gen1_pos
laminar/B738/electrical/apu_gen2_pos
laminar/B738/electrical/apu_low_oil
laminar/B738/electrical/apu_power_bus1
laminar/B738/electrical/apu_power_bus2
laminar/B738/electrical/apu_temp
laminar/B738/electrical/gen1_pos
laminar/B738/electrical/gen2_pos
laminar/B738/electrical/gpu_pos
laminar/B738/emergency/landgear_cover_pos
laminar/B738/emergency/landgear_pos
laminar/B738/engine/calc/cruise_max_alt
laminar/B738/engine/calc/cruise_opt_alt
laminar/B738/engine/calc/thr_climb_N1
laminar/B738/engine/calc/thr_cont_N1
laminar/B738/engine/calc/thr_cruise_N1
laminar/B738/engine/calc/thr_goaround_N1
laminar/B738/engine/calc/thr_takeoff_N1
laminar/B738/engine/eng1_egt
laminar/B738/engine/eng1_N1_bug
laminar/B738/engine/eng1_N1_bug_dig
laminar/B738/engine/eng1_N1_lim
laminar/B738/engine/eng1_N1_lim2
laminar/B738/engine/eng1_oil_filter_bypass
laminar/B738/engine/eng1_oil_press
laminar/B738/engine/eng1_req_n1
laminar/B738/engine/eng1_req_pie
laminar/B738/engine/eng1_start_disable
laminar/B738/engine/eng2_egt
laminar/B738/engine/eng2_N1_bug
laminar/B738/engine/eng2_N1_bug_dig
laminar/B738/engine/eng2_N1_lim
laminar/B738/engine/eng2_N1_lim2
laminar/B738/engine/eng2_oil_filter_bypass
laminar/B738/engine/eng2_oil_press
laminar/B738/engine/eng2_req_n1
laminar/B738/engine/eng2_req_pie
laminar/B738/engine/eng2_start_disable
laminar/B738/engine/engine_air_n2_rise1
laminar/B738/engine/engine_air_n2_rise2
laminar/B738/engine/engine_air_start
laminar/B738/engine/engine_air_starter
laminar/B738/engine/hide_asu
laminar/B738/engine/idle_mode
laminar/B738/engine/idle_mode_request
laminar/B738/engine/ignition1
laminar/B738/engine/ignition2
laminar/B738/engine/indicators/N1_percent_1
laminar/B738/engine/indicators/N1_percent_2
laminar/B738/engine/indicators/N2_percent_1
laminar/B738/engine/indicators/N2_percent_2
laminar/B738/engine/mixture_ratio1
laminar/B738/engine/mixture_ratio2
laminar/B738/engine/N1_mode_man
laminar/B738/engine/prop_mode_sync
laminar/B738/engine/rot_ang0
laminar/B738/engine/rot_ang1
laminar/B738/engine/rot_spd0
laminar/B738/engine/rot_spd1
laminar/B738/engine/starter
laminar/B738/engine/starter1_pos
laminar/B738/engine/starter1_pos2
laminar/B738/engine/starter2_pos
laminar/B738/engine/starter2_pos2
laminar/B738/engine/start_valve1
laminar/B738/engine/start_valve2
laminar/B738/engine/test_idle
laminar/B738/engine/test_idle2
laminar/B738/engine/thrust12_leveler
laminar/B738/engine/thrust1_leveler
laminar/B738/engine/thrust2_leveler
laminar/B738/engine/thrust_high_idle
laminar/B738/engine/thrust_low_idle
laminar/B738/engine/thr_lvr1
laminar/B738/engine/thr_lvr1_show
laminar/B738/engine/thr_lvr2
laminar/B738/engine/thr_lvr2_show
laminar/B738/ew_show
laminar/B738/find_icao
laminar/B738/find_icao_cmd
laminar/B738/find_icao_height
laminar/B738/find_icao_lat
laminar/B738/find_icao_lon
laminar/B738/fire/apu/ext_bottle/psi
laminar/B738/fire/apu/ext_switch/pos_arm
laminar/B738/fire/apu/ext_switch/pos_disch
laminar/B738/fire/apu/fire
laminar/B738/fire/apu/fire_ext
laminar/B738/fire/apu/was_fire
laminar/B738/fire/engine01/ext_switch/pos_arm
laminar/B738/fire/engine01/ext_switch/pos_disch
laminar/B738/fire/engine01_02L/ext_bottle/psi
laminar/B738/fire/engine01_02R/ext_bottle/psi
laminar/B738/fire/engine02/ext_switch/pos_arm
laminar/B738/fire/engine02/ext_switch/pos_disch
laminar/B738/flare/fd_target
laminar/B738/flare/flare_offset
laminar/B738/flare/flare_ratio
laminar/B738/flare/pitch_last
laminar/B738/flare/pitch_offset
laminar/B738/flare/pitch_ratio
laminar/B738/flare/thrust_ratio_1
laminar/B738/flare/thrust_ratio_2
laminar/B738/flare/thrust_vvi_1
laminar/B738/flare/thrust_vvi_2
laminar/B738/flare/vvi_last
laminar/B738/flare/vvi_trend
laminar/B738/flt_ctrls/aileron_trim/switch_pos
laminar/B738/flt_ctrls/flaps
laminar/B738/flt_ctrls/flaps_mapped
laminar/B738/flt_ctrls/flaps_reverse
laminar/B738/flt_ctrls/flap_lever
laminar/B738/flt_ctrls/joy_heading
laminar/B738/flt_ctrls/joy_override
laminar/B738/flt_ctrls/joy_pitch
laminar/B738/flt_ctrls/joy_roll
laminar/B738/flt_ctrls/landgear
laminar/B738/flt_ctrls/landgear_mapped
laminar/B738/flt_ctrls/landgear_off
laminar/B738/flt_ctrls/landgear_reverse
laminar/B738/flt_ctrls/lock_reverse1
laminar/B738/flt_ctrls/lock_reverse12
laminar/B738/flt_ctrls/lock_reverse2
laminar/B738/flt_ctrls/mixture1
laminar/B738/flt_ctrls/mixture1_mapped
laminar/B738/flt_ctrls/mixture1_reverse
laminar/B738/flt_ctrls/mixture2
laminar/B738/flt_ctrls/mixture2_mapped
laminar/B738/flt_ctrls/mixture2_reverse
laminar/B738/flt_ctrls/nosewheel
laminar/B738/flt_ctrls/reverse_lever1
laminar/B738/flt_ctrls/reverse_lever12
laminar/B738/flt_ctrls/reverse_lever2
laminar/B738/flt_ctrls/rudder_trim/sel_dial_pos
laminar/B738/flt_ctrls/speedbrake
laminar/B738/flt_ctrls/speedbrake_arm
laminar/B738/flt_ctrls/speedbrake_flight
laminar/B738/flt_ctrls/speedbrake_lever
laminar/B738/flt_ctrls/speedbrake_lever_anim
laminar/B738/flt_ctrls/speedbrake_lever_stop
laminar/B738/flt_ctrls/speedbrake_mapped
laminar/B738/flt_ctrls/speedbrake_noise
laminar/B738/flt_ctrls/speedbrake_reverse
laminar/B738/flt_ctrls/toe_left
laminar/B738/flt_ctrls/toe_right
laminar/B738/flt_ctrls/trim_wheel
laminar/B738/flt_ctrls/trim_wheel_man
laminar/B738/flt_ctrls/trim_wheel_quick
laminar/B738/flt_ctrls/trim_wheel_quick_ap
laminar/B738/flt_ctrls/trim_wheel_slow
laminar/B738/flt_ctrls/trim_wheel_slow_ap
laminar/B738/fmc/decode_value
laminar/B738/fmc/DR_test
laminar/B738/fmc/DR_test2
laminar/B738/fmc/fmc_message
laminar/B738/fmc/fmc_message_warn
laminar/B738/fmc/index_pos
laminar/B738/fmc/vnav/DRindex
laminar/B738/fmc_freeze
laminar/b738/fmod/callouts_10
laminar/b738/fmod/callouts_100
laminar/b738/fmod/callouts_1000
laminar/b738/fmod/callouts_1000s
laminar/b738/fmod/callouts_20
laminar/b738/fmod/callouts_200
laminar/b738/fmod/callouts_2500
laminar/b738/fmod/callouts_30
laminar/b738/fmod/callouts_300
laminar/b738/fmod/callouts_40
laminar/b738/fmod/callouts_400
laminar/b738/fmod/callouts_50
laminar/b738/fmod/callouts_500
laminar/B738/fmod/dh_minimum_copilot
laminar/B738/fmod/dh_minimum_copilot2
laminar/B738/fmod/dh_minimum_pilot
laminar/B738/fmod/dh_minimum_pilot2
laminar/B738/fmod/flap_sound
laminar/B738/fmod/fms_key
laminar/B738/fmod/fms_message
laminar/B738/fmod/gpu_deployed
laminar/B738/fmod/oxygen_test
laminar/b738/fmodpack/ac_established
laminar/b738/fmodpack/after_takeoff_played
laminar/b738/fmodpack/appro_mins
laminar/b738/fmodpack/arrival_timer
laminar/b738/fmodpack/axp_FA_dep_timer
laminar/b738/fmodpack/axp_FA_leo_timer
laminar/b738/fmodpack/axp_fire_handles_lights
laminar/b738/fmodpack/axp_load_effect
laminar/b738/fmodpack/axp_N1_current
laminar/b738/fmodpack/axp_N1_factor
laminar/b738/fmodpack/axp_sink_factor
laminar/b738/fmodpack/below_gs_alert
laminar/b738/fmodpack/belts_fastened
laminar/b738/fmodpack/cabindoor_closed
laminar/b738/fmodpack/climb_to_alt
laminar/b738/fmodpack/cruise_msg_played
laminar/b738/fmodpack/departure_timer
laminar/b738/fmodpack/descend_to_alt
laminar/b738/fmodpack/descent_msg_played
laminar/b738/fmodpack/eqcooling
laminar/b738/fmodpack/flightphase_boarding
laminar/b738/fmodpack/flightphase_climb
laminar/b738/fmodpack/flightphase_cruise
laminar/b738/fmodpack/flightphase_descent
laminar/b738/fmodpack/flightphase_landed
laminar/b738/fmodpack/flightphase_pax_leave
laminar/b738/fmodpack/flightphase_pre_takeoff
laminar/b738/fmodpack/flightphase_taxi
laminar/b738/fmodpack/flt_dk_door_lock
laminar/b738/fmodpack/fmod_airport_set
laminar/b738/fmodpack/fmod_alert_fo_1000_played
laminar/b738/fmodpack/fmod_alert_fo_400_played
laminar/b738/fmodpack/fmod_announcement_set
laminar/b738/fmodpack/fmod_ap_disconnect
laminar/b738/fmodpack/fmod_chatter_on
laminar/b738/fmodpack/fmod_crew_on
laminar/b738/fmodpack/fmod_enable_airport_ambience
laminar/b738/fmodpack/fmod_enable_fmc_mute_on
laminar/b738/fmodpack/fmod_end_leg
laminar/b738/fmodpack/fmod_eng_LEAP1B
laminar/b738/fmodpack/fmod_eq_enable
laminar/b738/fmodpack/fmod_eq_high
laminar/b738/fmodpack/fmod_eq_low
laminar/b738/fmodpack/fmod_eq_mid
laminar/b738/fmodpack/fmod_eq_use
laminar/b738/fmodpack/fmod_gpwstest_long_on
laminar/b738/fmodpack/fmod_gpwstest_short_on
laminar/b738/fmodpack/fmod_mutetrim_on
laminar/b738/fmodpack/fmod_mute_gpwstest
laminar/b738/fmodpack/fmod_pax_applause_on
laminar/b738/fmodpack/fmod_pax_boarding_on
laminar/b738/fmodpack/fmod_playfuelpumps
laminar/b738/fmodpack/fmod_play_cargo
laminar/b738/fmodpack/fmod_play_firebells
laminar/b738/fmodpack/fmod_play_positive_rate
laminar/b738/fmodpack/fmod_positive_rate_played
laminar/b738/fmodpack/fmod_start_leg
laminar/b738/fmodpack/fmod_vol_airport
laminar/b738/fmodpack/fmod_vol_computer
laminar/b738/fmodpack/fmod_vol_crew
laminar/b738/fmodpack/fmod_vol_FAC
laminar/b738/fmodpack/fmod_vol_int_ac
laminar/b738/fmodpack/fmod_vol_int_bump
laminar/b738/fmodpack/fmod_vol_int_ducker
laminar/b738/fmodpack/fmod_vol_int_eng
laminar/b738/fmodpack/fmod_vol_int_gyro
laminar/b738/fmodpack/fmod_vol_int_pax
laminar/b738/fmodpack/fmod_vol_int_roll
laminar/b738/fmodpack/fmod_vol_int_start
laminar/b738/fmodpack/fmod_vol_int_trim
laminar/b738/fmodpack/fmod_vol_int_wind
laminar/b738/fmodpack/fmod_vol_PM
laminar/b738/fmodpack/fmod_vol_weather
laminar/b738/fmodpack/fmod_woodpecker_on
laminar/b738/fmodpack/fmod_xp_int_vol
laminar/b738/fmodpack/gate_arrival_initialized
laminar/b738/fmodpack/gate_departure_initialized
laminar/b738/fmodpack/gate_departure_played
laminar/b738/fmodpack/horn_alert
laminar/b738/fmodpack/hotstart
laminar/b738/fmodpack/leg_ended
laminar/b738/fmodpack/leg_started
laminar/b738/fmodpack/msg_adj
laminar/b738/fmodpack/msg_adj_vert_spd
laminar/b738/fmodpack/msg_airspeed_low
laminar/b738/fmodpack/msg_alt_callouts
laminar/b738/fmodpack/msg_app_minimums
laminar/b738/fmodpack/msg_bank_angle
laminar/b738/fmodpack/msg_caution_terrain
laminar/b738/fmodpack/msg_clear_of_conflict
laminar/b738/fmodpack/msg_climb
laminar/b738/fmodpack/msg_climb_now
laminar/b738/fmodpack/msg_cross_climb
laminar/b738/fmodpack/msg_cross_descent
laminar/b738/fmodpack/msg_descent
laminar/b738/fmodpack/msg_descent_now
laminar/b738/fmodpack/msg_dont_sink
laminar/b738/fmodpack/msg_gpws_alert
laminar/b738/fmodpack/msg_gpws_inop
laminar/b738/fmodpack/msg_increase_climb
laminar/b738/fmodpack/msg_increase_descent
laminar/b738/fmodpack/msg_maint_vert_spd
laminar/b738/fmodpack/msg_minimums
laminar/b738/fmodpack/msg_mon_vert_spd
laminar/b738/fmodpack/msg_obstacle
laminar/b738/fmodpack/msg_obstacle_pull_up
laminar/b738/fmodpack/msg_pull_up
laminar/b738/fmodpack/msg_sink_rate
laminar/b738/fmodpack/msg_tcas_alert
laminar/b738/fmodpack/msg_tcas_fail
laminar/b738/fmodpack/msg_tcas_pass
laminar/b738/fmodpack/msg_terrain
laminar/b738/fmodpack/msg_terrain_ahead
laminar/b738/fmodpack/msg_too_low_flaps
laminar/b738/fmodpack/msg_too_low_gear
laminar/b738/fmodpack/msg_too_low_terrain
laminar/b738/fmodpack/msg_traffic
laminar/b738/fmodpack/msg_windshear
laminar/b738/fmodpack/packs_L
laminar/b738/fmodpack/packs_R
laminar/b738/fmodpack/pax_board
laminar/b738/fmodpack/pax_talk
laminar/b738/fmodpack/pa_announ_set
laminar/b738/fmodpack/play_80
laminar/b738/fmodpack/play_after_takeoff
laminar/b738/fmodpack/play_arrival_crosscheck
laminar/b738/fmodpack/play_belts
laminar/b738/fmodpack/play_cruise_msg
laminar/b738/fmodpack/play_descent_msg
laminar/b738/fmodpack/play_dta
laminar/b738/fmodpack/play_fo_1000
laminar/b738/fmodpack/play_fo_400
laminar/b738/fmodpack/play_gate_departure
laminar/b738/fmodpack/play_landed
laminar/b738/fmodpack/play_preland_msg
laminar/b738/fmodpack/play_seatbelt
laminar/b738/fmodpack/play_seatbelt10k
laminar/b738/fmodpack/play_spd_dial_sound
laminar/b738/fmodpack/play_taxi_to_gate
laminar/b738/fmodpack/play_tcas_alert
laminar/b738/fmodpack/play_TO
laminar/b738/fmodpack/play_turbulence_msg
laminar/b738/fmodpack/play_V1
laminar/b738/fmodpack/play_Vr
laminar/b738/fmodpack/play_vvi_dial_sound
laminar/b738/fmodpack/play_welcome_msg
laminar/b738/fmodpack/real_groundspeed
laminar/b738/fmodpack/recirc_L
laminar/b738/fmodpack/recirc_R
laminar/b738/fmodpack/taxi_to_gate_played
laminar/b738/fmodpack/TO_trigger
laminar/b738/fmodpack/vol_int_paxpremaster
laminar/b738/fmodpack/welcome_msg_played
laminar/B738/fmod_release
laminar/B738/FMS/accel_height
laminar/B738/fms/act_wpt_gp
laminar/B738/FMS/afs_spd_limit_max
laminar/B738/FMS/air_on_acf_ratio
laminar/B738/FMS/align_time
laminar/B738/FMS/altitude_pilot_ratio
laminar/B738/fms/alt_ed
laminar/B738/fms/alt_type_ed
laminar/B738/fms/anp
laminar/B738/FMS/approach_flaps
laminar/B738/FMS/approach_flaps_set
laminar/B738/FMS/approach_speed
laminar/B738/FMS/approach_wind_corr
laminar/B738/FMS/bank_angle
laminar/B738/fms/bank_angle
laminar/B738/fms/baro_in_hpa
laminar/B738/FMS/calc_spd_enable
laminar/B738/fms/calc_to_time
laminar/B738/FMS/calc_trim
laminar/B738/FMS/calc_vspd
laminar/B738/fms/calc_wpt_alt
laminar/B738/fms/calc_wpt_spd
laminar/B738/fms/center_line
laminar/B738/fms/chock_on_startup
laminar/B738/fms/chock_status
laminar/B738/FMS/climb_mode
laminar/B738/fms/cross_wind
laminar/B738/FMS/cruise_mode
laminar/B738/fms/crz_exec
laminar/B738/fms/ctr_line_min_delta
laminar/B738/FMS/descent_mode
laminar/B738/FMS/descent_now
laminar/B738/fms/dest_icao
laminar/B738/fms/dest_runway_alt
laminar/B738/fms/dest_runway_crs
laminar/B738/fms/dest_runway_end_lat
laminar/B738/fms/dest_runway_end_lon
laminar/B738/fms/dest_runway_len
laminar/B738/fms/dest_runway_start_lat
laminar/B738/fms/dest_runway_start_lon
laminar/B738/fms/des_icao_alt
laminar/B738/FMS/detail_cl
laminar/B738/FMS/dist_dest
laminar/B738/fms/dist_to_ed
laminar/B738/fms/ed_alt
laminar/B738/fms/ed_dist
laminar/B738/fms/ed_idx
laminar/B738/fms/ed_to_dist
laminar/B738/fms/ed_type
laminar/B738/fms/ed_vpa
laminar/B738/fms/end_route
laminar/B738/FMS/eng1_N1_ratio
laminar/B738/FMS/eng1_out
laminar/B738/FMS/eng2_N1_ratio
laminar/B738/FMS/eng2_out
laminar/B738/fms/engine_no_running_state
laminar/B738/FMS/eng_out
laminar/B738/FMS/eng_out_disable
laminar/B738/FMS/eo_accel_height
laminar/B738/fms/fac_crs
laminar/B738/fms/fac_trk
laminar/B738/fms/fac_xtrack
laminar/B738/FMS/flaps_speed
laminar/B738/fms/flap_detents
laminar/B738/fms/flap_detents_thr
laminar/B738/FMS/flight_phase
laminar/B738/FMS/fmc_cg
laminar/B738/FMS/fmc_gw
laminar/B738/FMS/fmc_gw_app
laminar/B738/FMS/fmc_isa_dev_c
laminar/B738/FMS/fmc_oat_temp
laminar/B738/FMS/fmc_rw_cond
laminar/B738/FMS/fmc_sel_temp
laminar/B738/FMS/fmc_trans_alt
laminar/B738/FMS/fmc_trans_lvl
laminar/B738/FMS/fmc_units
laminar/B738/fms/fpln_acive
laminar/B738/fms/fpln_acive_fo
laminar/B738/FMS/fpln_dist
laminar/B738/FMS/fpln_dist2
laminar/B738/fms/fpln_format
laminar/B738/fms/fpln_nav_id
laminar/B738/fms/geo_des
laminar/B738/fms/glidepath_type
laminar/B738/fms/gps_course_degtm
laminar/B738/fms/gps_track2_degtm
laminar/B738/fms/gps_track3_degtm
laminar/B738/fms/gps_track_degtm
laminar/B738/fms/gps_track_turn
laminar/B738/fms/gps_track_turn2
laminar/B738/fms/gps_wpt_path
laminar/B738/fms/gp_alt_err
laminar/B738/fms/gp_alt_err2
laminar/B738/fms/gp_alt_err_ratio
laminar/B738/fms/gp_alt_err_ratio2
laminar/B738/fms/gp_err_pfd
laminar/B738/fms/gp_pth_alt
laminar/B738/fms/gp_vvi
laminar/B738/fms/gp_vvi_corr
laminar/B738/fms/hold_phase
laminar/B738/fms/idle_path
laminar/B738/fms/idle_path_kts
laminar/B738/fms/idle_path_mach
laminar/B738/fms/idx_corr
laminar/B738/fms/idx_corr2
laminar/B738/fms/idx_corr3
laminar/B738/fms/idx_corr4
laminar/B738/fms/idx_corr5
laminar/B738/fms/idx_ed
laminar/B738/fms/id_ed
laminar/B738/fms/id_eta
laminar/B738/FMS/ils_disable
laminar/B738/fms/ils_freq_nav
laminar/B738/fms/intdir
laminar/B738/fms/intdir_act
laminar/B738/fms/intdir_crs
laminar/B738/fms/intdir_crs2
laminar/B738/fms/intdir_idx
laminar/B738/FMS/irs2_hdg_fmc_set
laminar/B738/FMS/irs2_pos_fmc_set
laminar/B738/FMS/irs_hdg_fmc
laminar/B738/FMS/irs_hdg_fmc_set
laminar/B738/FMS/irs_pos_fmc
laminar/B738/FMS/irs_pos_fmc_set
laminar/B738/FMS/last_pos_str

XPLMDataRef laminar_B738_fms_legs; // laminar/B738/fms/legs [String]
laminar/B738/fms/legs_2

XPLMDataRef laminar_B738_fms_legs_alt_calc; // laminar/B738/fms/legs_alt_calc float[128]

laminar/B738/fms/legs_alt_calc_2

XPLMDataRef laminar_B738_fms_legs_alt_rest1 // laminar/B738/fms/legs_alt_rest1 float[128]

laminar/B738/fms/legs_alt_rest1_2
laminar/B738/fms/legs_alt_rest2
laminar/B738/fms/legs_alt_rest2_2
laminar/B738/fms/legs_alt_rest_type
laminar/B738/fms/legs_alt_rest_type_2
laminar/B738/fms/legs_brg_mag
laminar/B738/fms/legs_brg_mag_2
laminar/B738/fms/legs_brg_true
laminar/B738/fms/legs_brg_true_2
laminar/B738/fms/legs_change
laminar/B738/fms/legs_change2
laminar/B738/fms/legs_crs_mag
laminar/B738/fms/legs_crs_mag_2
laminar/B738/fms/legs_df_crs
laminar/B738/fms/legs_df_crs_2
laminar/B738/fms/legs_dist
laminar/B738/fms/legs_dist_2
laminar/B738/fms/legs_eta
laminar/B738/fms/legs_hold_dist
laminar/B738/fms/legs_hold_dist_2
laminar/B738/fms/legs_hold_time
laminar/B738/fms/legs_hold_time_2
XPLMDataRef laminar_B738_fms_legs_lat; // laminar/B738/fms/legs_lat float[128]

laminar/B738/fms/legs_lat_2
XPLMDataRef laminar_B738_fms_legs_lon; // laminar/B738/fms/legs_lon float[128]
laminar/B738/fms/legs_lon_2
laminar/B738/fms/legs_mod_active
laminar/B738/fms/legs_num2
laminar/B738/fms/legs_pth
laminar/B738/fms/legs_pth_2
laminar/B738/fms/legs_pth_before
laminar/B738/fms/legs_pth_before_2
laminar/B738/fms/legs_radii_ctr_lat
laminar/B738/fms/legs_radii_ctr_lon
laminar/B738/fms/legs_radii_dta
laminar/B738/fms/legs_radii_dta2
laminar/B738/fms/legs_radii_lat2
laminar/B738/fms/legs_radii_lon2
laminar/B738/fms/legs_radii_radius
laminar/B738/fms/legs_radius
laminar/B738/fms/legs_radius_2
laminar/B738/fms/legs_rad_lat
laminar/B738/fms/legs_rad_lat_2
laminar/B738/fms/legs_rad_lon
laminar/B738/fms/legs_rad_lon_2
laminar/B738/fms/legs_rad_turn
laminar/B738/fms/legs_rad_turn_2
laminar/B738/fms/legs_rnp
XPLMDataRef laminar_B738_fms_legs_spd; // laminar/B738/fms/legs_spd float[128]
laminar/B738/fms/legs_spd_2
laminar/B738/fms/legs_spd_calc
laminar/B738/fms/legs_step_ctr
laminar/B738/fms/legs_step_ctr_fo
laminar/B738/fms/legs_turn
laminar/B738/fms/legs_type
laminar/B738/fms/legs_type_2
laminar/B738/fms/legs_wpt_type_2
laminar/B738/fms/lnav_disconnect
laminar/B738/fms/lnav_dist2_next
laminar/B738/fms/lnav_dist_next
laminar/B738/fms/lnav_prior_arm
laminar/B738/fms/lock_idle_thrust
laminar/B738/fms/lock_thrust
laminar/B738/fms/min_baro_radio
laminar/B738/fms/missed_app_act
laminar/B738/fms/missed_app_alt
laminar/B738/fms/missed_app_wpt_idx
laminar/B738/fms/missed_app_wpt_idx2
laminar/B738/FMS/N1_mode
laminar/B738/FMS/N1_mode_thrust
laminar/B738/FMS/N1_mode_to_sel
laminar/B738/fms/nav_mode
laminar/B738/fms/next_predict
laminar/B738/fms/no_perf
laminar/B738/fms/num_of_wpts
laminar/B738/fms/num_of_wpts_2
laminar/B738/fms/parkbrake_remove_chock
laminar/B738/fms/pause_td
laminar/B738/fms/pfd_fac_horizontal
laminar/B738/fms/pfd_fac_horizontal_fo
laminar/B738/fms/pfd_gp_path
laminar/B738/fms/radii_final_crs
laminar/B738/fms/radii_turn
laminar/B738/fms/radii_turn_act
laminar/B738/FMS/radio_height_ratio
laminar/B738/FMS/refuel_time
laminar/B738/fms/ref_icao_alt
laminar/B738/fms/restrict_fms
laminar/B738/fms/rest_wpt_alt
laminar/B738/fms/rest_wpt_alt_dist
laminar/B738/fms/rest_wpt_alt_id
laminar/B738/fms/rest_wpt_alt_idx
laminar/B738/fms/rest_wpt_alt_t
laminar/B738/fms/rest_wpt_spd
laminar/B738/fms/rest_wpt_spd2
laminar/B738/fms/rest_wpt_spd_id
laminar/B738/fms/rest_wpt_spd_idx
laminar/B738/fms/rnav_alt
laminar/B738/fms/rnav_enable
laminar/B738/fms/rnav_idx_first
laminar/B738/fms/rnav_idx_last
laminar/B738/fms/rnav_idx_last_mod
laminar/B738/fms/rnav_vpa
laminar/B738/fms/rnp
laminar/B738/fms/rw_hdg
laminar/B738/fms/rw_slope
laminar/B738/fms/rw_wind_dir
laminar/B738/fms/rw_wind_spd
laminar/B738/FMS/speed_ratio
laminar/B738/FMS/takeoff_flaps
laminar/B738/FMS/takeoff_flaps_set
laminar/B738/fms/td_dist
laminar/B738/fms/td_dist99
laminar/B738/fms/td_dist_mod
laminar/B738/fms/test
laminar/B738/fms/test1
laminar/B738/fms/test3
laminar/B738/fms/test4
laminar/B738/fms/throttle_noise
laminar/B738/FMS/throttle_red_alt
laminar/B738/FMS/time_dest
laminar/B738/fms/toe_brakes_ovr
laminar/B738/fms/track_up
laminar/B738/fms/track_up_active
laminar/B738/FMS/trim_calc
laminar/B738/FMS/trim_set
laminar/B738/FMS/v1
laminar/B738/FMS/v1r_bugs
laminar/B738/FMS/v1_calc
laminar/B738/FMS/v1_set
laminar/B738/FMS/v2
laminar/B738/FMS/v2_15
laminar/B738/FMS/v2_bugs
laminar/B738/FMS/v2_calc
laminar/B738/FMS/v2_set
laminar/B738/FMS/vdot_ratio
laminar/B738/fms/vnav_alt_err
laminar/B738/fms/vnav_alt_err2
laminar/B738/fms/vnav_alt_err99
laminar/B738/fms/vnav_alt_err_ratio
laminar/B738/fms/vnav_alt_err_ratio2
laminar/B738/fms/vnav_app_active
laminar/B738/fms/vnav_decel_before_idx
laminar/B738/fms/vnav_decel_dist
laminar/B738/fms/vnav_decel_dist2
laminar/B738/fms/vnav_decel_dist2_mod
laminar/B738/fms/vnav_decel_dist_mod
laminar/B738/fms/vnav_decel_idx
laminar/B738/fms/vnav_decel_idx_mod
laminar/B738/fms/vnav_desc_spd_disable
laminar/B738/FMS/vnav_disable
laminar/B738/fms/vnav_disconnect
laminar/B738/fms/vnav_ed_fix_num
laminar/B738/fms/vnav_err_pfd
laminar/B738/fms/vnav_fl100_desc_dist
laminar/B738/fms/vnav_fl100_desc_dist_mod
laminar/B738/fms/vnav_fl100_dist
laminar/B738/fms/vnav_fl100_dist_mod
laminar/B738/fms/vnav_fl100_idx
laminar/B738/fms/vnav_fl100_idx_mod
laminar/B738/fms/vnav_gp_active
laminar/B738/fms/vnav_idx
laminar/B738/fms/vnav_pth_alt
laminar/B738/fms/vnav_pth_alt99
laminar/B738/fms/vnav_pth_show
laminar/B738/FMS/vnav_speed
laminar/B738/fms/vnav_tc_disco
laminar/B738/fms/vnav_tc_disco_mod
laminar/B738/fms/vnav_tc_dist
laminar/B738/fms/vnav_tc_dist_mod
laminar/B738/fms/vnav_tc_idx
laminar/B738/fms/vnav_tc_idx_mod
laminar/B738/fms/vnav_td_alt2
laminar/B738/fms/vnav_td_alt2_hold
laminar/B738/fms/vnav_td_dist
laminar/B738/fms/vnav_td_dist2
laminar/B738/fms/vnav_td_fix_alt
laminar/B738/fms/vnav_td_fix_dist
laminar/B738/fms/vnav_td_fix_ed
laminar/B738/fms/vnav_td_fix_idx
laminar/B738/fms/vnav_td_idx
laminar/B738/fms/vnav_td_idx2
laminar/B738/fms/vnav_td_idx_mod
laminar/B738/fms/vnav_vvi
laminar/B738/fms/vnav_vvi99
laminar/B738/fms/vnav_vvi_const
laminar/B738/fms/vnav_vvi_corr
laminar/B738/fms/vor_radial
laminar/B738/FMS/vr
laminar/B738/FMS/vref
laminar/B738/FMS/vref_15
laminar/B738/FMS/vref_25
laminar/B738/FMS/vref_30
laminar/B738/FMS/vref_40
laminar/B738/FMS/vref_bugs
laminar/B738/FMS/vref_bugs_fo
laminar/B738/FMS/vr_calc
laminar/B738/FMS/vr_set
laminar/B738/FMS/vvi_pilot_ratio
laminar/B738/FMS/v_speed_ratio
laminar/B738/fms/xfirst_time2
laminar/B738/fms/xtrack
laminar/B738/fms/xtrack2
laminar/B738/fm_release
laminar/B738/fo/yoke_ap_disengage
laminar/B738/fo/yoke_checklist
laminar/B738/fps
laminar/B738/fps_enable
laminar/B738/freeze_eng1
laminar/B738/freeze_eng2
laminar/B738/freeze_monster
laminar/B738/freeze_wing1
laminar/B738/freeze_wing2
laminar/B738/fuel/center_status
laminar/B738/fuel/center_tank_kgs
laminar/B738/fuel/center_tank_kgs_c
laminar/B738/fuel/center_tank_lbs
laminar/B738/fuel/cross_feed_valve
laminar/B738/fuel/fuel_tank_pos_ctr1
laminar/B738/fuel/fuel_tank_pos_ctr2
laminar/B738/fuel/fuel_tank_pos_lft1
laminar/B738/fuel/fuel_tank_pos_lft2
laminar/B738/fuel/fuel_tank_pos_rgt1
laminar/B738/fuel/fuel_tank_pos_rgt2
laminar/B738/fuel/left_status
laminar/B738/fuel/left_tank_kgs
laminar/B738/fuel/left_tank_lbs
laminar/B738/fuel/right_status
laminar/B738/fuel/right_tank_kgs
laminar/B738/fuel/right_tank_lbs
laminar/B738/fuel/total_tank_kgs
laminar/B738/fuel/total_tank_lbs
laminar/B738/fuel_flow_used1
laminar/B738/fuel_flow_used2
laminar/B738/fuel_flow_used_show
laminar/B738/gauges/standby_altimeter_baro
laminar/B738/gauges/standby_altitude_ft
laminar/B738/gauges/standby_alt_mode
laminar/B738/gauges/standby_alt_std_mode
laminar/B738/gauges/standby_mode
laminar/B738/gear_lock_ovrd/position
laminar/B738/gpu_available
laminar/B738/gpu_hide
laminar/B738/gpws/terr_dist
laminar/B738/handles/flap_lever/stop_pos
laminar/B738/hud/align_horizon
laminar/B738/hud/align_horizon2
laminar/B738/hud/align_horizon3
laminar/B738/hud/gnd_trk_tape
laminar/B738/hud/hdg_bug_tape
laminar/B738/hud/horizon
laminar/B738/HUD/HUD_stow
laminar/B738/hud_disable
laminar/B738/hud_displ_disable
laminar/B738/hud_displ_vr_disable
laminar/B738/hydraulic/A_pressure
laminar/B738/hydraulic/A_press_el_trg
laminar/B738/hydraulic/A_press_trg
laminar/B738/hydraulic/A_rudder
laminar/B738/hydraulic/A_status
laminar/B738/hydraulic/B_pressure
laminar/B738/hydraulic/B_press_el_trg
laminar/B738/hydraulic/B_press_trg
laminar/B738/hydraulic/B_rudder
laminar/B738/hydraulic/B_status
laminar/B738/hydraulic/hyd_A_qty
laminar/B738/hydraulic/hyd_B_qty
laminar/B738/hydraulic/standby_on
laminar/B738/hydraulic/standby_pressure
laminar/B738/hydraulic/standby_status
laminar/B738/ice/eng1_heat_pos
laminar/B738/ice/eng2_heat_pos
laminar/B738/ice/l_fwd_temp
laminar/B738/ice/l_side_temp
laminar/B738/ice/monster_icing
laminar/B738/ice/r_fwd_temp
laminar/B738/ice/r_side_temp
laminar/B738/ice/window_heat_l_fwd_pos
laminar/B738/ice/window_heat_l_side_pos
laminar/B738/ice/window_heat_r_fwd_pos
laminar/B738/ice/window_heat_r_side_pos
laminar/B738/ice/wing_heat_pos
laminar/B738/ice/wing_l_heat_on
laminar/B738/ice/wing_r_heat_on
laminar/B738/indicators/duct_press_L
laminar/B738/indicators/duct_press_R
laminar/B738/indicators/fmc_exec_lights
laminar/B738/indicators/fmc_exec_lights_fo
laminar/B738/indicators/fms_exec_light_copilot
laminar/B738/indicators/fms_exec_light_pilot
laminar/B738/irs/alignment_left_remain
laminar/B738/irs/alignment_right_remain
laminar/B738/irs/gps2_pos
laminar/B738/irs/gps_pos
laminar/B738/irs/irs2_mode
laminar/B738/irs/irs2_pos
laminar/B738/irs/irs2_pos_set
laminar/B738/irs/irs2_status
laminar/B738/irs/irs_mode
laminar/B738/irs/irs_pos
laminar/B738/irs/irs_pos_set
laminar/B738/irs/irs_status
laminar/B738/irs/last_pos_lat
laminar/B738/irs/last_pos_lon
laminar/B738/irs/nd_allign
laminar/B738/irs/pfd_allign
laminar/B738/irs/pfd_allign_hdg
laminar/B738/irs/test
laminar/B738/irs/test2
laminar/B738/irs_allign_show
laminar/B738/irs_clr_light
laminar/B738/irs_entry
laminar/B738/irs_entry_hdg_show
laminar/B738/irs_entry_len
laminar/B738/irs_entry_pos_show
laminar/B738/irs_ent_light
laminar/B738/irs_left1
laminar/B738/irs_left1_show
laminar/B738/irs_left2
laminar/B738/irs_left2_show
laminar/B738/irs_right1
laminar/B738/irs_right1_show
laminar/B738/irs_right2
laminar/B738/irs_right2_show
laminar/B738/joy_override
laminar/B738/kill_led_lights
laminar/B738/knob/ac_power
laminar/B738/knob/dc_power
laminar/B738/knob/transponder_pos
laminar/B738/knobs/cross_feed
laminar/B738/knobs/cross_feed_pos
laminar/B738/knobs/standby_alt_app_pos
laminar/B738/knobs/standby_alt_baro
laminar/B738/knobs/standby_alt_hpin_pos
laminar/B738/knobs/standby_alt_minus_pos
laminar/B738/knobs/standby_alt_plus_pos
laminar/B738/knobs/standby_alt_rst_pos
laminar/B738/knobs/standby_alt_std_pos
laminar/B738/latitude_deg
laminar/B738/latitude_min
laminar/B738/latitude_NS
laminar/B738/lat_deg_show
laminar/B738/lat_min_show
laminar/B738/led_lights
laminar/B738/light/spill/ratio/apu_avail_lights_spill
laminar/B738/light/spill/ratio/cabin_emergency_lights_spill
laminar/B738/light/spill/ratio/cabin_lights_spill
laminar/B738/light/spill/ratio/crossfeed_lights_spill
laminar/B738/light/spill/ratio/eng1_spar_lights_spill
laminar/B738/light/spill/ratio/eng1_valve_lights_spill
laminar/B738/light/spill/ratio/eng1_wings_lights_spill
laminar/B738/light/spill/ratio/eng2_spar_lights_spill
laminar/B738/light/spill/ratio/eng2_valve_lights_spill
laminar/B738/light/spill/ratio/eng2_wings_lights_spill
laminar/B738/light/spill/ratio/entry_lights_spill
laminar/B738/light/spill/ratio/extinguisher_circuit_spill2
laminar/B738/light/spill/ratio/extinguisher_circuit_spill_apu
laminar/B738/light/spill/ratio/extinguisher_circuit_spill_left
laminar/B738/light/spill/ratio/extinguisher_circuit_spill_right
laminar/B738/light/spill/ratio/extinguisher_leveler_spill_apu
laminar/B738/light/spill/ratio/extinguisher_leveler_spill_eng1
laminar/B738/light/spill/ratio/extinguisher_leveler_spill_eng2
laminar/B738/light/spill/ratio/fire_warn_spill
laminar/B738/light/spill/ratio/gen1_avail_lights_spill
laminar/B738/light/spill/ratio/gen2_avail_lights_spill
laminar/B738/light/spill/ratio/gpu_avail_lights_spill
laminar/B738/light/spill/ratio/l_wings_lights_spill
laminar/B738/light/spill/ratio/master_caution_spill
laminar/B738/light/spill/ratio/parking_brake
laminar/B738/light/spill/ratio/radio_com1_lights_spill
laminar/B738/light/spill/ratio/radio_com2_lights_spill
laminar/B738/light/spill/ratio/radio_mark_lights_spill
laminar/B738/light/spill/ratio/radio_nav1_lights_spill
laminar/B738/light/spill/ratio/radio_nav2_lights_spill
laminar/B738/light/spill/ratio/ram1_full_lights_spill
laminar/B738/light/spill/ratio/ram2_full_lights_spill
laminar/B738/light/spill/ratio/r_wings_lights_spill
laminar/B738/lights/land_ret_left_pos
laminar/B738/lights/land_ret_right_pos
laminar/B738/longitude_deg
laminar/B738/longitude_EW
laminar/B738/longitude_min
laminar/B738/lon_deg_show
laminar/B738/lon_min_show
laminar/B738/mcp/digit_8
laminar/B738/mcp/digit_A
laminar/B738/mcp/over_spd
laminar/B738/mcp/under_spd
laminar/B738/metar_cmd
laminar/B738/metar_data
laminar/B738/metar_icao
laminar/B738/nd/acf_fo_show
laminar/B738/nd/acf_rot
laminar/B738/nd/acf_rot_fo
laminar/B738/nd/acf_show
laminar/B738/nd/acf_x
laminar/B738/nd/acf_x_fo
laminar/B738/nd/acf_y
laminar/B738/nd/acf_y_fo
laminar/B738/nd/apt_enable
laminar/B738/nd/apt_fo_enable
laminar/B738/nd/apt_fo_id00
laminar/B738/nd/apt_fo_id01
laminar/B738/nd/apt_fo_id02
laminar/B738/nd/apt_fo_id03
laminar/B738/nd/apt_fo_id04
laminar/B738/nd/apt_fo_id05
laminar/B738/nd/apt_fo_id06
laminar/B738/nd/apt_fo_id07
laminar/B738/nd/apt_fo_id08
laminar/B738/nd/apt_fo_id09
laminar/B738/nd/apt_fo_id10
laminar/B738/nd/apt_fo_id11
laminar/B738/nd/apt_fo_id12
laminar/B738/nd/apt_fo_id13
laminar/B738/nd/apt_fo_id14
laminar/B738/nd/apt_fo_id15
laminar/B738/nd/apt_fo_id16
laminar/B738/nd/apt_fo_id17
laminar/B738/nd/apt_fo_id18
laminar/B738/nd/apt_fo_id19
laminar/B738/nd/apt_fo_id20
laminar/B738/nd/apt_fo_id21
laminar/B738/nd/apt_fo_id22
laminar/B738/nd/apt_fo_id23
laminar/B738/nd/apt_fo_id24
laminar/B738/nd/apt_fo_id25
laminar/B738/nd/apt_fo_id26
laminar/B738/nd/apt_fo_id27
laminar/B738/nd/apt_fo_id28
laminar/B738/nd/apt_fo_id29
laminar/B738/nd/apt_fo_x
laminar/B738/nd/apt_fo_y
laminar/B738/nd/apt_id00
laminar/B738/nd/apt_id01
laminar/B738/nd/apt_id02
laminar/B738/nd/apt_id03
laminar/B738/nd/apt_id04
laminar/B738/nd/apt_id05
laminar/B738/nd/apt_id06
laminar/B738/nd/apt_id07
laminar/B738/nd/apt_id08
laminar/B738/nd/apt_id09
laminar/B738/nd/apt_id10
laminar/B738/nd/apt_id11
laminar/B738/nd/apt_id12
laminar/B738/nd/apt_id13
laminar/B738/nd/apt_id14
laminar/B738/nd/apt_id15
laminar/B738/nd/apt_id16
laminar/B738/nd/apt_id17
laminar/B738/nd/apt_id18
laminar/B738/nd/apt_id19
laminar/B738/nd/apt_id20
laminar/B738/nd/apt_id21
laminar/B738/nd/apt_id22
laminar/B738/nd/apt_id23
laminar/B738/nd/apt_id24
laminar/B738/nd/apt_id25
laminar/B738/nd/apt_id26
laminar/B738/nd/apt_id27
laminar/B738/nd/apt_id28
laminar/B738/nd/apt_id29
laminar/B738/nd/apt_x
laminar/B738/nd/apt_y
laminar/B738/nd/capt/efis_disagree
laminar/B738/nd/capt/fmc_source
laminar/B738/nd/crs_mag
laminar/B738/nd/crs_mag_fo
laminar/B738/nd/decel_fo_id
laminar/B738/nd/decel_fo_show
laminar/B738/nd/decel_fo_x
laminar/B738/nd/decel_fo_y
laminar/B738/nd/decel_id
laminar/B738/nd/decel_lat
laminar/B738/nd/decel_lon
laminar/B738/nd/decel_show
laminar/B738/nd/decel_x
laminar/B738/nd/decel_y
laminar/B738/nd/disable_apt
laminar/B738/nd/disable_navaid
laminar/B738/nd/ed_fo_id
laminar/B738/nd/ed_fo_show
laminar/B738/nd/ed_fo_x
laminar/B738/nd/ed_fo_y
laminar/B738/nd/ed_id
laminar/B738/nd/ed_show
laminar/B738/nd/ed_x
laminar/B738/nd/ed_y
laminar/B738/nd/fix_dist_0
laminar/B738/nd/fix_dist_0_nm
laminar/B738/nd/fix_dist_1
laminar/B738/nd/fix_dist_1_nm
laminar/B738/nd/fix_dist_2
laminar/B738/nd/fix_dist_2_nm
laminar/B738/nd/fix_fo_dist_0
laminar/B738/nd/fix_fo_dist_0_nm
laminar/B738/nd/fix_fo_dist_1
laminar/B738/nd/fix_fo_dist_1_nm
laminar/B738/nd/fix_fo_dist_2
laminar/B738/nd/fix_fo_dist_2_nm
laminar/B738/nd/fix_fo_id00
laminar/B738/nd/fix_fo_id01
laminar/B738/nd/fix_fo_id02
laminar/B738/nd/fix_fo_id03
laminar/B738/nd/fix_fo_id04
laminar/B738/nd/fix_fo_rad00_0
laminar/B738/nd/fix_fo_rad00_1
laminar/B738/nd/fix_fo_rad00_2
laminar/B738/nd/fix_fo_rad01_0
laminar/B738/nd/fix_fo_rad01_1
laminar/B738/nd/fix_fo_rad01_2
laminar/B738/nd/fix_fo_rad02_0
laminar/B738/nd/fix_fo_rad02_1
laminar/B738/nd/fix_fo_rad02_2
laminar/B738/nd/fix_fo_rad03_0
laminar/B738/nd/fix_fo_rad03_1
laminar/B738/nd/fix_fo_rad03_2
laminar/B738/nd/fix_fo_rad04_0
laminar/B738/nd/fix_fo_rad04_1
laminar/B738/nd/fix_fo_rad04_2
laminar/B738/nd/fix_fo_rad_dist_0
laminar/B738/nd/fix_fo_rad_dist_0a
laminar/B738/nd/fix_fo_rad_dist_1
laminar/B738/nd/fix_fo_rad_dist_1a
laminar/B738/nd/fix_fo_rad_dist_2
laminar/B738/nd/fix_fo_rad_dist_2a
laminar/B738/nd/fix_fo_rot_0
laminar/B738/nd/fix_fo_rot_1
laminar/B738/nd/fix_fo_rot_2
laminar/B738/nd/fix_fo_show
laminar/B738/nd/fix_fo_type
laminar/B738/nd/fix_fo_x
laminar/B738/nd/fix_fo_y
laminar/B738/nd/fix_id00
laminar/B738/nd/fix_id01
laminar/B738/nd/fix_id02
laminar/B738/nd/fix_id03
laminar/B738/nd/fix_id04
laminar/B738/nd/fix_rad00_0
laminar/B738/nd/fix_rad00_1
laminar/B738/nd/fix_rad00_2
laminar/B738/nd/fix_rad01_0
laminar/B738/nd/fix_rad01_1
laminar/B738/nd/fix_rad01_2
laminar/B738/nd/fix_rad02_0
laminar/B738/nd/fix_rad02_1
laminar/B738/nd/fix_rad02_2
laminar/B738/nd/fix_rad03_0
laminar/B738/nd/fix_rad03_1
laminar/B738/nd/fix_rad03_2
laminar/B738/nd/fix_rad04_0
laminar/B738/nd/fix_rad04_1
laminar/B738/nd/fix_rad04_2
laminar/B738/nd/fix_rad_dist_0
laminar/B738/nd/fix_rad_dist_0a
laminar/B738/nd/fix_rad_dist_1
laminar/B738/nd/fix_rad_dist_1a
laminar/B738/nd/fix_rad_dist_2
laminar/B738/nd/fix_rad_dist_2a
laminar/B738/nd/fix_rot_0
laminar/B738/nd/fix_rot_1
laminar/B738/nd/fix_rot_2
laminar/B738/nd/fix_show
laminar/B738/nd/fix_type
laminar/B738/nd/fix_x
laminar/B738/nd/fix_y
laminar/B738/nd/fo/efis_disagree
laminar/B738/nd/fo/fmc_source
laminar/B738/nd/hdg_bug_line
laminar/B738/nd/hdg_bug_line_fo
laminar/B738/nd/hdg_bug_nd
laminar/B738/nd/hdg_bug_nd_fo
laminar/B738/nd/hdg_mag
laminar/B738/nd/hdg_mag_fo
laminar/B738/nd/hold_crs
laminar/B738/nd/hold_dist
laminar/B738/nd/hold_fo_crs
laminar/B738/nd/hold_fo_dist
laminar/B738/nd/hold_fo_type
laminar/B738/nd/hold_fo_x
laminar/B738/nd/hold_fo_y
laminar/B738/nd/hold_type
laminar/B738/nd/hold_x
laminar/B738/nd/hold_y
laminar/B738/nd/mcp_hdg_dial
laminar/B738/nd/mcp_hdg_dial_fo
laminar/B738/nd/mcp_hdg_dial_fo_show
laminar/B738/nd/mcp_hdg_dial_show
laminar/B738/nd/object_fo_id00
laminar/B738/nd/object_fo_id00w
laminar/B738/nd/object_fo_id01
laminar/B738/nd/object_fo_id01w
laminar/B738/nd/object_fo_id02
laminar/B738/nd/object_fo_id02w
laminar/B738/nd/object_fo_id03
laminar/B738/nd/object_fo_id03w
laminar/B738/nd/object_fo_id04
laminar/B738/nd/object_fo_id04w
laminar/B738/nd/object_fo_id05
laminar/B738/nd/object_fo_id05w
laminar/B738/nd/object_fo_id06
laminar/B738/nd/object_fo_id06w
laminar/B738/nd/object_fo_id07
laminar/B738/nd/object_fo_id07w
laminar/B738/nd/object_fo_id08
laminar/B738/nd/object_fo_id08w
laminar/B738/nd/object_fo_id09
laminar/B738/nd/object_fo_id09w
laminar/B738/nd/object_fo_id10
laminar/B738/nd/object_fo_id10w
laminar/B738/nd/object_fo_id11
laminar/B738/nd/object_fo_id11w
laminar/B738/nd/object_fo_id12
laminar/B738/nd/object_fo_id12w
laminar/B738/nd/object_fo_id13
laminar/B738/nd/object_fo_id13w
laminar/B738/nd/object_fo_id14
laminar/B738/nd/object_fo_id14w
laminar/B738/nd/object_fo_id15
laminar/B738/nd/object_fo_id15w
laminar/B738/nd/object_fo_id16
laminar/B738/nd/object_fo_id16w
laminar/B738/nd/object_fo_id17
laminar/B738/nd/object_fo_id17w
laminar/B738/nd/object_fo_id18
laminar/B738/nd/object_fo_id18w
laminar/B738/nd/object_fo_id19
laminar/B738/nd/object_fo_id19w
laminar/B738/nd/object_fo_id20
laminar/B738/nd/object_fo_id20w
laminar/B738/nd/object_fo_id21
laminar/B738/nd/object_fo_id21w
laminar/B738/nd/object_fo_id22
laminar/B738/nd/object_fo_id22w
laminar/B738/nd/object_fo_id23
laminar/B738/nd/object_fo_id23w
laminar/B738/nd/object_fo_id24
laminar/B738/nd/object_fo_id24w
laminar/B738/nd/object_fo_id25
laminar/B738/nd/object_fo_id25w
laminar/B738/nd/object_fo_id26
laminar/B738/nd/object_fo_id26w
laminar/B738/nd/object_fo_id27
laminar/B738/nd/object_fo_id27w
laminar/B738/nd/object_fo_id28
laminar/B738/nd/object_fo_id28w
laminar/B738/nd/object_fo_id29
laminar/B738/nd/object_fo_id29w
laminar/B738/nd/object_fo_id30
laminar/B738/nd/object_fo_id30w
laminar/B738/nd/object_fo_id31
laminar/B738/nd/object_fo_id31w
laminar/B738/nd/object_fo_id32
laminar/B738/nd/object_fo_id32w
laminar/B738/nd/object_fo_id33
laminar/B738/nd/object_fo_id33w
laminar/B738/nd/object_fo_id34
laminar/B738/nd/object_fo_id34w
laminar/B738/nd/object_fo_id35
laminar/B738/nd/object_fo_id35w
laminar/B738/nd/object_fo_id36
laminar/B738/nd/object_fo_id36w
laminar/B738/nd/object_fo_id37
laminar/B738/nd/object_fo_id37w
laminar/B738/nd/object_fo_id38
laminar/B738/nd/object_fo_id38w
laminar/B738/nd/object_fo_id39
laminar/B738/nd/object_fo_id39w
laminar/B738/nd/object_fo_id40
laminar/B738/nd/object_fo_id40w
laminar/B738/nd/object_fo_id41
laminar/B738/nd/object_fo_id41w
laminar/B738/nd/object_fo_id42
laminar/B738/nd/object_fo_id42w
laminar/B738/nd/object_fo_id43
laminar/B738/nd/object_fo_id43w
laminar/B738/nd/object_fo_id44
laminar/B738/nd/object_fo_id44w
laminar/B738/nd/object_fo_id45
laminar/B738/nd/object_fo_id45w
laminar/B738/nd/object_fo_id46
laminar/B738/nd/object_fo_id46w
laminar/B738/nd/object_fo_id47
laminar/B738/nd/object_fo_id47w
laminar/B738/nd/object_fo_id48
laminar/B738/nd/object_fo_id48w
laminar/B738/nd/object_fo_id49
laminar/B738/nd/object_fo_id49w
laminar/B738/nd/object_fo_type00
laminar/B738/nd/object_fo_type01
laminar/B738/nd/object_fo_type02
laminar/B738/nd/object_fo_type03
laminar/B738/nd/object_fo_type04
laminar/B738/nd/object_fo_type05
laminar/B738/nd/object_fo_type06
laminar/B738/nd/object_fo_type07
laminar/B738/nd/object_fo_type08
laminar/B738/nd/object_fo_type09
laminar/B738/nd/object_fo_type10
laminar/B738/nd/object_fo_type11
laminar/B738/nd/object_fo_type12
laminar/B738/nd/object_fo_type13
laminar/B738/nd/object_fo_type14
laminar/B738/nd/object_fo_type15
laminar/B738/nd/object_fo_type16
laminar/B738/nd/object_fo_type17
laminar/B738/nd/object_fo_type18
laminar/B738/nd/object_fo_type19
laminar/B738/nd/object_fo_type20
laminar/B738/nd/object_fo_type21
laminar/B738/nd/object_fo_type22
laminar/B738/nd/object_fo_type23
laminar/B738/nd/object_fo_type24
laminar/B738/nd/object_fo_type25
laminar/B738/nd/object_fo_type26
laminar/B738/nd/object_fo_type27
laminar/B738/nd/object_fo_type28
laminar/B738/nd/object_fo_type29
laminar/B738/nd/object_fo_type30
laminar/B738/nd/object_fo_type31
laminar/B738/nd/object_fo_type32
laminar/B738/nd/object_fo_type33
laminar/B738/nd/object_fo_type34
laminar/B738/nd/object_fo_type35
laminar/B738/nd/object_fo_type36
laminar/B738/nd/object_fo_type37
laminar/B738/nd/object_fo_type38
laminar/B738/nd/object_fo_type39
laminar/B738/nd/object_fo_type40
laminar/B738/nd/object_fo_type41
laminar/B738/nd/object_fo_type42
laminar/B738/nd/object_fo_type43
laminar/B738/nd/object_fo_type44
laminar/B738/nd/object_fo_type45
laminar/B738/nd/object_fo_type46
laminar/B738/nd/object_fo_type47
laminar/B738/nd/object_fo_type48
laminar/B738/nd/object_fo_type49
laminar/B738/nd/object_fo_x
laminar/B738/nd/object_fo_y
laminar/B738/nd/object_id00
laminar/B738/nd/object_id00w
laminar/B738/nd/object_id01
laminar/B738/nd/object_id01w
laminar/B738/nd/object_id02
laminar/B738/nd/object_id02w
laminar/B738/nd/object_id03
laminar/B738/nd/object_id03w
laminar/B738/nd/object_id04
laminar/B738/nd/object_id04w
laminar/B738/nd/object_id05
laminar/B738/nd/object_id05w
laminar/B738/nd/object_id06
laminar/B738/nd/object_id06w
laminar/B738/nd/object_id07
laminar/B738/nd/object_id07w
laminar/B738/nd/object_id08
laminar/B738/nd/object_id08w
laminar/B738/nd/object_id09
laminar/B738/nd/object_id09w
laminar/B738/nd/object_id10
laminar/B738/nd/object_id10w
laminar/B738/nd/object_id11
laminar/B738/nd/object_id11w
laminar/B738/nd/object_id12
laminar/B738/nd/object_id12w
laminar/B738/nd/object_id13
laminar/B738/nd/object_id13w
laminar/B738/nd/object_id14
laminar/B738/nd/object_id14w
laminar/B738/nd/object_id15
laminar/B738/nd/object_id15w
laminar/B738/nd/object_id16
laminar/B738/nd/object_id16w
laminar/B738/nd/object_id17
laminar/B738/nd/object_id17w
laminar/B738/nd/object_id18
laminar/B738/nd/object_id18w
laminar/B738/nd/object_id19
laminar/B738/nd/object_id19w
laminar/B738/nd/object_id20
laminar/B738/nd/object_id20w
laminar/B738/nd/object_id21
laminar/B738/nd/object_id21w
laminar/B738/nd/object_id22
laminar/B738/nd/object_id22w
laminar/B738/nd/object_id23
laminar/B738/nd/object_id23w
laminar/B738/nd/object_id24
laminar/B738/nd/object_id24w
laminar/B738/nd/object_id25
laminar/B738/nd/object_id25w
laminar/B738/nd/object_id26
laminar/B738/nd/object_id26w
laminar/B738/nd/object_id27
laminar/B738/nd/object_id27w
laminar/B738/nd/object_id28
laminar/B738/nd/object_id28w
laminar/B738/nd/object_id29
laminar/B738/nd/object_id29w
laminar/B738/nd/object_id30
laminar/B738/nd/object_id30w
laminar/B738/nd/object_id31
laminar/B738/nd/object_id31w
laminar/B738/nd/object_id32
laminar/B738/nd/object_id32w
laminar/B738/nd/object_id33
laminar/B738/nd/object_id33w
laminar/B738/nd/object_id34
laminar/B738/nd/object_id34w
laminar/B738/nd/object_id35
laminar/B738/nd/object_id35w
laminar/B738/nd/object_id36
laminar/B738/nd/object_id36w
laminar/B738/nd/object_id37
laminar/B738/nd/object_id37w
laminar/B738/nd/object_id38
laminar/B738/nd/object_id38w
laminar/B738/nd/object_id39
laminar/B738/nd/object_id39w
laminar/B738/nd/object_id40
laminar/B738/nd/object_id40w
laminar/B738/nd/object_id41
laminar/B738/nd/object_id41w
laminar/B738/nd/object_id42
laminar/B738/nd/object_id42w
laminar/B738/nd/object_id43
laminar/B738/nd/object_id43w
laminar/B738/nd/object_id44
laminar/B738/nd/object_id44w
laminar/B738/nd/object_id45
laminar/B738/nd/object_id45w
laminar/B738/nd/object_id46
laminar/B738/nd/object_id46w
laminar/B738/nd/object_id47
laminar/B738/nd/object_id47w
laminar/B738/nd/object_id48
laminar/B738/nd/object_id48w
laminar/B738/nd/object_id49
laminar/B738/nd/object_id49w
laminar/B738/nd/object_type00
laminar/B738/nd/object_type01
laminar/B738/nd/object_type02
laminar/B738/nd/object_type03
laminar/B738/nd/object_type04
laminar/B738/nd/object_type05
laminar/B738/nd/object_type06
laminar/B738/nd/object_type07
laminar/B738/nd/object_type08
laminar/B738/nd/object_type09
laminar/B738/nd/object_type10
laminar/B738/nd/object_type11
laminar/B738/nd/object_type12
laminar/B738/nd/object_type13
laminar/B738/nd/object_type14
laminar/B738/nd/object_type15
laminar/B738/nd/object_type16
laminar/B738/nd/object_type17
laminar/B738/nd/object_type18
laminar/B738/nd/object_type19
laminar/B738/nd/object_type20
laminar/B738/nd/object_type21
laminar/B738/nd/object_type22
laminar/B738/nd/object_type23
laminar/B738/nd/object_type24
laminar/B738/nd/object_type25
laminar/B738/nd/object_type26
laminar/B738/nd/object_type27
laminar/B738/nd/object_type28
laminar/B738/nd/object_type29
laminar/B738/nd/object_type30
laminar/B738/nd/object_type31
laminar/B738/nd/object_type32
laminar/B738/nd/object_type33
laminar/B738/nd/object_type34
laminar/B738/nd/object_type35
laminar/B738/nd/object_type36
laminar/B738/nd/object_type37
laminar/B738/nd/object_type38
laminar/B738/nd/object_type39
laminar/B738/nd/object_type40
laminar/B738/nd/object_type41
laminar/B738/nd/object_type42
laminar/B738/nd/object_type43
laminar/B738/nd/object_type44
laminar/B738/nd/object_type45
laminar/B738/nd/object_type46
laminar/B738/nd/object_type47
laminar/B738/nd/object_type48
laminar/B738/nd/object_type49
laminar/B738/nd/object_x
laminar/B738/nd/object_y
laminar/B738/nd/rte_dist
laminar/B738/nd/rte_dist_act
laminar/B738/nd/rte_edit
laminar/B738/nd/rte_edit_dist_act
laminar/B738/nd/rte_edit_rot_act
laminar/B738/nd/rte_edit_show_act
laminar/B738/nd/rte_edit_type_act
laminar/B738/nd/rte_edit_x_act
laminar/B738/nd/rte_edit_y_act
laminar/B738/nd/rte_fo_dist
laminar/B738/nd/rte_fo_dist_act
laminar/B738/nd/rte_fo_edit
laminar/B738/nd/rte_fo_edit_dist_act
laminar/B738/nd/rte_fo_edit_rot_act
laminar/B738/nd/rte_fo_edit_show_act
laminar/B738/nd/rte_fo_edit_type_act
laminar/B738/nd/rte_fo_edit_x_act
laminar/B738/nd/rte_fo_edit_y_act
laminar/B738/nd/rte_fo_rot
laminar/B738/nd/rte_fo_rot_act
laminar/B738/nd/rte_fo_show
laminar/B738/nd/rte_fo_show_act
laminar/B738/nd/rte_fo_x
laminar/B738/nd/rte_fo_x_act
laminar/B738/nd/rte_fo_y
laminar/B738/nd/rte_fo_y_act
laminar/B738/nd/rte_rot
laminar/B738/nd/rte_rot_act
laminar/B738/nd/rte_show
laminar/B738/nd/rte_show_act
laminar/B738/nd/rte_x
laminar/B738/nd/rte_x_act
laminar/B738/nd/rte_y
laminar/B738/nd/rte_y_act
laminar/B738/nd/tcas_ring
laminar/B738/nd/tcas_ring_fo
laminar/B738/nd/tc_fo_id
laminar/B738/nd/tc_fo_show
laminar/B738/nd/tc_fo_x
laminar/B738/nd/tc_fo_y
laminar/B738/nd/tc_id
laminar/B738/nd/tc_lat
laminar/B738/nd/tc_lon
laminar/B738/nd/tc_show
laminar/B738/nd/tc_x
laminar/B738/nd/tc_y
laminar/B738/nd/td_fo_id
laminar/B738/nd/td_fo_show
laminar/B738/nd/td_fo_x
laminar/B738/nd/td_fo_y
laminar/B738/nd/td_id
laminar/B738/nd/td_lat
laminar/B738/nd/td_lon
laminar/B738/nd/td_show
laminar/B738/nd/td_x
laminar/B738/nd/td_y
laminar/B738/nd/track_nd
laminar/B738/nd/track_nd_fo
laminar/B738/nd/wind_show
laminar/B738/nd/wpt_alt00m
laminar/B738/nd/wpt_alt00w
laminar/B738/nd/wpt_alt01m
laminar/B738/nd/wpt_alt01w
laminar/B738/nd/wpt_alt02m
laminar/B738/nd/wpt_alt02w
laminar/B738/nd/wpt_alt03m
laminar/B738/nd/wpt_alt03w
laminar/B738/nd/wpt_alt04m
laminar/B738/nd/wpt_alt04w
laminar/B738/nd/wpt_alt05m
laminar/B738/nd/wpt_alt05w
laminar/B738/nd/wpt_alt06m
laminar/B738/nd/wpt_alt06w
laminar/B738/nd/wpt_alt07m
laminar/B738/nd/wpt_alt07w
laminar/B738/nd/wpt_alt08m
laminar/B738/nd/wpt_alt08w
laminar/B738/nd/wpt_alt09m
laminar/B738/nd/wpt_alt09w
laminar/B738/nd/wpt_alt10m
laminar/B738/nd/wpt_alt10w
laminar/B738/nd/wpt_alt11m
laminar/B738/nd/wpt_alt11w
laminar/B738/nd/wpt_alt12m
laminar/B738/nd/wpt_alt12w
laminar/B738/nd/wpt_alt13m
laminar/B738/nd/wpt_alt13w
laminar/B738/nd/wpt_alt14m
laminar/B738/nd/wpt_alt14w
laminar/B738/nd/wpt_alt15m
laminar/B738/nd/wpt_alt15w
laminar/B738/nd/wpt_alt16m
laminar/B738/nd/wpt_alt16w
laminar/B738/nd/wpt_alt17m
laminar/B738/nd/wpt_alt17w
laminar/B738/nd/wpt_alt18m
laminar/B738/nd/wpt_alt18w
laminar/B738/nd/wpt_alt19m
laminar/B738/nd/wpt_alt19w
laminar/B738/nd/wpt_eta00m
laminar/B738/nd/wpt_eta00w
laminar/B738/nd/wpt_eta01m
laminar/B738/nd/wpt_eta01w
laminar/B738/nd/wpt_eta02m
laminar/B738/nd/wpt_eta02w
laminar/B738/nd/wpt_eta03m
laminar/B738/nd/wpt_eta03w
laminar/B738/nd/wpt_eta04m
laminar/B738/nd/wpt_eta04w
laminar/B738/nd/wpt_eta05m
laminar/B738/nd/wpt_eta05w
laminar/B738/nd/wpt_eta06m
laminar/B738/nd/wpt_eta06w
laminar/B738/nd/wpt_eta07m
laminar/B738/nd/wpt_eta07w
laminar/B738/nd/wpt_eta08m
laminar/B738/nd/wpt_eta08w
laminar/B738/nd/wpt_eta09m
laminar/B738/nd/wpt_eta09w
laminar/B738/nd/wpt_eta10m
laminar/B738/nd/wpt_eta10w
laminar/B738/nd/wpt_eta11m
laminar/B738/nd/wpt_eta11w
laminar/B738/nd/wpt_eta12m
laminar/B738/nd/wpt_eta12w
laminar/B738/nd/wpt_eta13m
laminar/B738/nd/wpt_eta13w
laminar/B738/nd/wpt_eta14m
laminar/B738/nd/wpt_eta14w
laminar/B738/nd/wpt_eta15m
laminar/B738/nd/wpt_eta15w
laminar/B738/nd/wpt_eta16m
laminar/B738/nd/wpt_eta16w
laminar/B738/nd/wpt_eta17m
laminar/B738/nd/wpt_eta17w
laminar/B738/nd/wpt_eta18m
laminar/B738/nd/wpt_eta18w
laminar/B738/nd/wpt_eta19m
laminar/B738/nd/wpt_eta19w
laminar/B738/nd/wpt_fo_alt00m
laminar/B738/nd/wpt_fo_alt00w
laminar/B738/nd/wpt_fo_alt01m
laminar/B738/nd/wpt_fo_alt01w
laminar/B738/nd/wpt_fo_alt02m
laminar/B738/nd/wpt_fo_alt02w
laminar/B738/nd/wpt_fo_alt03m
laminar/B738/nd/wpt_fo_alt03w
laminar/B738/nd/wpt_fo_alt04m
laminar/B738/nd/wpt_fo_alt04w
laminar/B738/nd/wpt_fo_alt05m
laminar/B738/nd/wpt_fo_alt05w
laminar/B738/nd/wpt_fo_alt06m
laminar/B738/nd/wpt_fo_alt06w
laminar/B738/nd/wpt_fo_alt07m
laminar/B738/nd/wpt_fo_alt07w
laminar/B738/nd/wpt_fo_alt08m
laminar/B738/nd/wpt_fo_alt08w
laminar/B738/nd/wpt_fo_alt09m
laminar/B738/nd/wpt_fo_alt09w
laminar/B738/nd/wpt_fo_alt10m
laminar/B738/nd/wpt_fo_alt10w
laminar/B738/nd/wpt_fo_alt11m
laminar/B738/nd/wpt_fo_alt11w
laminar/B738/nd/wpt_fo_alt12m
laminar/B738/nd/wpt_fo_alt12w
laminar/B738/nd/wpt_fo_alt13m
laminar/B738/nd/wpt_fo_alt13w
laminar/B738/nd/wpt_fo_alt14m
laminar/B738/nd/wpt_fo_alt14w
laminar/B738/nd/wpt_fo_alt15m
laminar/B738/nd/wpt_fo_alt15w
laminar/B738/nd/wpt_fo_alt16m
laminar/B738/nd/wpt_fo_alt16w
laminar/B738/nd/wpt_fo_alt17m
laminar/B738/nd/wpt_fo_alt17w
laminar/B738/nd/wpt_fo_alt18m
laminar/B738/nd/wpt_fo_alt18w
laminar/B738/nd/wpt_fo_alt19m
laminar/B738/nd/wpt_fo_alt19w
laminar/B738/nd/wpt_fo_eta00m
laminar/B738/nd/wpt_fo_eta00w
laminar/B738/nd/wpt_fo_eta01m
laminar/B738/nd/wpt_fo_eta01w
laminar/B738/nd/wpt_fo_eta02m
laminar/B738/nd/wpt_fo_eta02w
laminar/B738/nd/wpt_fo_eta03m
laminar/B738/nd/wpt_fo_eta03w
laminar/B738/nd/wpt_fo_eta04m
laminar/B738/nd/wpt_fo_eta04w
laminar/B738/nd/wpt_fo_eta05m
laminar/B738/nd/wpt_fo_eta05w
laminar/B738/nd/wpt_fo_eta06m
laminar/B738/nd/wpt_fo_eta06w
laminar/B738/nd/wpt_fo_eta07m
laminar/B738/nd/wpt_fo_eta07w
laminar/B738/nd/wpt_fo_eta08m
laminar/B738/nd/wpt_fo_eta08w
laminar/B738/nd/wpt_fo_eta09m
laminar/B738/nd/wpt_fo_eta09w
laminar/B738/nd/wpt_fo_eta10m
laminar/B738/nd/wpt_fo_eta10w
laminar/B738/nd/wpt_fo_eta11m
laminar/B738/nd/wpt_fo_eta11w
laminar/B738/nd/wpt_fo_eta12m
laminar/B738/nd/wpt_fo_eta12w
laminar/B738/nd/wpt_fo_eta13m
laminar/B738/nd/wpt_fo_eta13w
laminar/B738/nd/wpt_fo_eta14m
laminar/B738/nd/wpt_fo_eta14w
laminar/B738/nd/wpt_fo_eta15m
laminar/B738/nd/wpt_fo_eta15w
laminar/B738/nd/wpt_fo_eta16m
laminar/B738/nd/wpt_fo_eta16w
laminar/B738/nd/wpt_fo_eta17m
laminar/B738/nd/wpt_fo_eta17w
laminar/B738/nd/wpt_fo_eta18m
laminar/B738/nd/wpt_fo_eta18w
laminar/B738/nd/wpt_fo_eta19m
laminar/B738/nd/wpt_fo_eta19w
laminar/B738/nd/wpt_fo_id00m
laminar/B738/nd/wpt_fo_id00w
laminar/B738/nd/wpt_fo_id01m
laminar/B738/nd/wpt_fo_id01w
laminar/B738/nd/wpt_fo_id02m
laminar/B738/nd/wpt_fo_id02w
laminar/B738/nd/wpt_fo_id03m
laminar/B738/nd/wpt_fo_id03w
laminar/B738/nd/wpt_fo_id04m
laminar/B738/nd/wpt_fo_id04w
laminar/B738/nd/wpt_fo_id05m
laminar/B738/nd/wpt_fo_id05w
laminar/B738/nd/wpt_fo_id06m
laminar/B738/nd/wpt_fo_id06w
laminar/B738/nd/wpt_fo_id07m
laminar/B738/nd/wpt_fo_id07w
laminar/B738/nd/wpt_fo_id08m
laminar/B738/nd/wpt_fo_id08w
laminar/B738/nd/wpt_fo_id09m
laminar/B738/nd/wpt_fo_id09w
laminar/B738/nd/wpt_fo_id10m
laminar/B738/nd/wpt_fo_id10w
laminar/B738/nd/wpt_fo_id11m
laminar/B738/nd/wpt_fo_id11w
laminar/B738/nd/wpt_fo_id12m
laminar/B738/nd/wpt_fo_id12w
laminar/B738/nd/wpt_fo_id13m
laminar/B738/nd/wpt_fo_id13w
laminar/B738/nd/wpt_fo_id14m
laminar/B738/nd/wpt_fo_id14w
laminar/B738/nd/wpt_fo_id15m
laminar/B738/nd/wpt_fo_id15w
laminar/B738/nd/wpt_fo_id16m
laminar/B738/nd/wpt_fo_id16w
laminar/B738/nd/wpt_fo_id17m
laminar/B738/nd/wpt_fo_id17w
laminar/B738/nd/wpt_fo_id18m
laminar/B738/nd/wpt_fo_id18w
laminar/B738/nd/wpt_fo_id19m
laminar/B738/nd/wpt_fo_id19w
laminar/B738/nd/wpt_fo_type
laminar/B738/nd/wpt_fo_type00
laminar/B738/nd/wpt_fo_type01
laminar/B738/nd/wpt_fo_type02
laminar/B738/nd/wpt_fo_type03
laminar/B738/nd/wpt_fo_type04
laminar/B738/nd/wpt_fo_type05
laminar/B738/nd/wpt_fo_type06
laminar/B738/nd/wpt_fo_type07
laminar/B738/nd/wpt_fo_type08
laminar/B738/nd/wpt_fo_type09
laminar/B738/nd/wpt_fo_type10
laminar/B738/nd/wpt_fo_type11
laminar/B738/nd/wpt_fo_type12
laminar/B738/nd/wpt_fo_type13
laminar/B738/nd/wpt_fo_type14
laminar/B738/nd/wpt_fo_type15
laminar/B738/nd/wpt_fo_type16
laminar/B738/nd/wpt_fo_type17
laminar/B738/nd/wpt_fo_type18
laminar/B738/nd/wpt_fo_type19
laminar/B738/nd/wpt_fo_x
laminar/B738/nd/wpt_fo_y
laminar/B738/nd/wpt_id00m
laminar/B738/nd/wpt_id00w
laminar/B738/nd/wpt_id01m
laminar/B738/nd/wpt_id01w
laminar/B738/nd/wpt_id02m
laminar/B738/nd/wpt_id02w
laminar/B738/nd/wpt_id03m
laminar/B738/nd/wpt_id03w
laminar/B738/nd/wpt_id04m
laminar/B738/nd/wpt_id04w
laminar/B738/nd/wpt_id05m
laminar/B738/nd/wpt_id05w
laminar/B738/nd/wpt_id06m
laminar/B738/nd/wpt_id06w
laminar/B738/nd/wpt_id07m
laminar/B738/nd/wpt_id07w
laminar/B738/nd/wpt_id08m
laminar/B738/nd/wpt_id08w
laminar/B738/nd/wpt_id09m
laminar/B738/nd/wpt_id09w
laminar/B738/nd/wpt_id10m
laminar/B738/nd/wpt_id10w
laminar/B738/nd/wpt_id11m
laminar/B738/nd/wpt_id11w
laminar/B738/nd/wpt_id12m
laminar/B738/nd/wpt_id12w
laminar/B738/nd/wpt_id13m
laminar/B738/nd/wpt_id13w
laminar/B738/nd/wpt_id14m
laminar/B738/nd/wpt_id14w
laminar/B738/nd/wpt_id15m
laminar/B738/nd/wpt_id15w
laminar/B738/nd/wpt_id16m
laminar/B738/nd/wpt_id16w
laminar/B738/nd/wpt_id17m
laminar/B738/nd/wpt_id17w
laminar/B738/nd/wpt_id18m
laminar/B738/nd/wpt_id18w
laminar/B738/nd/wpt_id19m
laminar/B738/nd/wpt_id19w
laminar/B738/nd/wpt_type
laminar/B738/nd/wpt_type00
laminar/B738/nd/wpt_type01
laminar/B738/nd/wpt_type02
laminar/B738/nd/wpt_type03
laminar/B738/nd/wpt_type04
laminar/B738/nd/wpt_type05
laminar/B738/nd/wpt_type06
laminar/B738/nd/wpt_type07
laminar/B738/nd/wpt_type08
laminar/B738/nd/wpt_type09
laminar/B738/nd/wpt_type10
laminar/B738/nd/wpt_type11
laminar/B738/nd/wpt_type12
laminar/B738/nd/wpt_type13
laminar/B738/nd/wpt_type14
laminar/B738/nd/wpt_type15
laminar/B738/nd/wpt_type16
laminar/B738/nd/wpt_type17
laminar/B738/nd/wpt_type18
laminar/B738/nd/wpt_type19
laminar/B738/nd/wpt_x
laminar/B738/nd/wpt_y
laminar/B738/nosewheel_steer_override
laminar/B738/nosewheel_steer_ratio
laminar/B738/ns_show
laminar/B738/one_way_switch/drive_disconnect1_pos
laminar/B738/one_way_switch/drive_disconnect2_pos
laminar/B738/one_way_switch/pax_oxy_pos
laminar/B738/others/left_wiper_ratio
laminar/B738/others/left_wiper_up
laminar/B738/others/right_wiper_ratio
laminar/B738/others/right_wiper_up
laminar/B738/outflow_valve
laminar/B738/parkbrake_set
laminar/B738/parking_brake_pos
laminar/B738/particle_rain
laminar/B738/perf/kill_annun
laminar/B738/perf/kill_anti_ice
laminar/B738/perf/kill_calc
laminar/B738/perf/kill_effect
laminar/B738/perf/kill_fms
laminar/B738/perf/kill_fms_navaid
laminar/B738/perf/kill_fms_navaid2
laminar/B738/perf/kill_fms_navaid3
laminar/B738/perf/kill_glareshield
laminar/B738/perf/kill_lat_lon
laminar/B738/perf/kill_systems
laminar/B738/perf/kill_windshield
laminar/B738/pfd/adf1_arrow
laminar/B738/pfd/adf1_arrow_fo
laminar/B738/pfd/adf2_arrow
laminar/B738/pfd/adf2_arrow_fo
laminar/B738/PFD/agl_copilot
laminar/B738/PFD/agl_pilot
laminar/B738/pfd/airspeed_warn
laminar/B738/pfd/bank_angle_warn
laminar/B738/PFD/capt/alt_mode_is_meters
laminar/B738/PFD/capt/fpv_on
laminar/B738/pfd/capt/nav_vor
laminar/B738/pfd/capt/nav_vor_fo
laminar/B738/pfd/cpt_nav_txt1
laminar/B738/pfd/cpt_nav_txt2
laminar/B738/pfd/des_rwy_altitude
laminar/B738/pfd/dh_copilot
laminar/B738/pfd/dh_pilot
laminar/B738/PFD/fac_exp
laminar/B738/PFD/fac_exp_fo
laminar/B738/pfd/flaps_1
laminar/B738/pfd/flaps_10
laminar/B738/pfd/flaps_15
laminar/B738/pfd/flaps_15_show
laminar/B738/pfd/flaps_1_show
laminar/B738/pfd/flaps_2
laminar/B738/pfd/flaps_25
laminar/B738/pfd/flaps_25_show
laminar/B738/pfd/flaps_2_show
laminar/B738/pfd/flaps_5
laminar/B738/pfd/flaps_5_show
laminar/B738/pfd/flaps_up
laminar/B738/pfd/flaps_up_show
laminar/B738/pfd/flight_director_pitch_copilot
laminar/B738/pfd/flight_director_pitch_pilot
laminar/B738/PFD/fo/alt_mode_is_meters
laminar/B738/PFD/fo/fpv_on
laminar/B738/pfd/fo_nav_txt1
laminar/B738/pfd/fo_nav_txt2
laminar/B738/pfd/fpv_horiz
laminar/B738/pfd/fpv_horiz_fo
laminar/B738/pfd/gp_status
laminar/B738/PFD/h_dots_pilot
laminar/B738/PFD/h_dots_pilot_show
laminar/B738/pfd/ils_copilot_show
laminar/B738/pfd/ils_copilot_show0
laminar/B738/pfd/ils_fo_copilot_show
laminar/B738/pfd/ils_fo_copilot_show0
laminar/B738/pfd/ils_fo_rotate
laminar/B738/pfd/ils_fo_rotate0
laminar/B738/pfd/ils_fo_runway
laminar/B738/pfd/ils_fo_runway0
laminar/B738/pfd/ils_fo_show
laminar/B738/pfd/ils_fo_show0
laminar/B738/pfd/ils_fo_x
laminar/B738/pfd/ils_fo_x0
laminar/B738/pfd/ils_fo_y
laminar/B738/pfd/ils_fo_y0
laminar/B738/PFD/ils_pilot_show
laminar/B738/pfd/ils_rotate
laminar/B738/pfd/ils_rotate0
laminar/B738/pfd/ils_runway
laminar/B738/pfd/ils_runway0
laminar/B738/pfd/ils_show
laminar/B738/pfd/ils_show0
laminar/B738/pfd/ils_x
laminar/B738/pfd/ils_x0
laminar/B738/pfd/ils_y
laminar/B738/pfd/ils_y0
laminar/B738/PFD/loc_exp
laminar/B738/PFD/loc_exp_fo
laminar/B738/pfd/max_maneuver_speed
laminar/B738/pfd/max_maneuver_speed_show
laminar/B738/pfd/max_speed
laminar/B738/pfd/min_maneuver_speed
laminar/B738/pfd/min_maneuver_speed_show
laminar/B738/pfd/min_speed
laminar/B738/pfd/min_speed_show
laminar/B738/PFD/nav1_copilot
laminar/B738/PFD/nav1_pilot
laminar/B738/PFD/nav2_copilot
laminar/B738/PFD/nav2_pilot
laminar/B738/pfd/nd_vert_path
laminar/B738/pfd/no_vspd
laminar/B738/pfd/pfd_stall
laminar/B738/pfd/pfd_stall_show
laminar/B738/pfd/pfd_trk_path
laminar/B738/pfd/pfd_trk_path_fo
laminar/B738/pfd/pfd_vert_path
laminar/B738/pfd/pfd_vert_path_fo
laminar/B738/pfd/ra_dial
laminar/B738/pfd/ref_rwy_altitude
laminar/B738/pfd/rwy_altitude
laminar/B738/pfd/rwy_show
laminar/B738/pfd/spd_80
laminar/B738/pfd/spd_80_show
laminar/B738/pfd/spd_of_snd
laminar/B738/pfd/track_line
laminar/B738/pfd/vor1_arrow
laminar/B738/pfd/vor1_arrow_fo
laminar/B738/pfd/vor1_copilot_show
laminar/B738/pfd/vor1_line_copilot_show
laminar/B738/pfd/vor1_line_show
laminar/B738/pfd/vor1_sel_bcrs
laminar/B738/pfd/vor1_sel_bcrs_fo
laminar/B738/pfd/vor1_sel_crs
laminar/B738/pfd/vor1_sel_crs_fo
laminar/B738/pfd/vor1_sel_id
laminar/B738/pfd/vor1_sel_id_fo
laminar/B738/pfd/vor1_sel_pos
laminar/B738/pfd/vor1_sel_pos_brg
laminar/B738/pfd/vor1_sel_pos_brg_fo
laminar/B738/pfd/vor1_sel_pos_dist
laminar/B738/pfd/vor1_sel_pos_dist_fo
laminar/B738/pfd/vor1_sel_pos_fo
laminar/B738/pfd/vor1_sel_pos_show
laminar/B738/pfd/vor1_sel_pos_show_fo
laminar/B738/pfd/vor1_sel_rotate
laminar/B738/pfd/vor1_sel_rotate_fo
laminar/B738/pfd/vor1_sel_x
laminar/B738/pfd/vor1_sel_x_fo
laminar/B738/pfd/vor1_sel_y
laminar/B738/pfd/vor1_sel_y_fo
laminar/B738/pfd/vor1_show
laminar/B738/pfd/vor2_arrow
laminar/B738/pfd/vor2_arrow_fo
laminar/B738/pfd/vor2_copilot_show
laminar/B738/pfd/vor2_line_copilot_show
laminar/B738/pfd/vor2_line_show
laminar/B738/pfd/vor2_sel_bcrs
laminar/B738/pfd/vor2_sel_bcrs_fo
laminar/B738/pfd/vor2_sel_crs
laminar/B738/pfd/vor2_sel_crs_fo
laminar/B738/pfd/vor2_sel_id
laminar/B738/pfd/vor2_sel_id_fo
laminar/B738/pfd/vor2_sel_pos
laminar/B738/pfd/vor2_sel_pos_brg
laminar/B738/pfd/vor2_sel_pos_brg_fo
laminar/B738/pfd/vor2_sel_pos_dist
laminar/B738/pfd/vor2_sel_pos_dist_fo
laminar/B738/pfd/vor2_sel_pos_fo
laminar/B738/pfd/vor2_sel_pos_show
laminar/B738/pfd/vor2_sel_pos_show_fo
laminar/B738/pfd/vor2_sel_rotate
laminar/B738/pfd/vor2_sel_rotate_fo
laminar/B738/pfd/vor2_sel_x
laminar/B738/pfd/vor2_sel_x_fo
laminar/B738/pfd/vor2_sel_y
laminar/B738/pfd/vor2_sel_y_fo
laminar/B738/pfd/vor2_show
laminar/B738/pfd/vor_dme1
laminar/B738/pfd/vor_dme2
laminar/B738/pfd/vor_dme_id1
laminar/B738/pfd/vor_dme_id2
laminar/B738/pfd/vspeed
laminar/B738/pfd/vspeed_digit_show
laminar/B738/pfd/vspeed_digit_show_fo
laminar/B738/pfd/vspeed_man_v1
laminar/B738/pfd/vspeed_man_vr
laminar/B738/pfd/vspeed_man_vref
laminar/B738/pfd/vspeed_mode
laminar/B738/pfd/vspeed_show
laminar/B738/pfd/vspeed_vref_show
laminar/B738/pfd/vspeed_vref_show_fo
laminar/B738/PFD/v_dots_pilot
laminar/B738/PFD/v_dots_pilot_show
laminar/B738/plane_2k_4k
laminar/B738/pressurization/knobs/landing_alt
laminar/B738/pressurization_mode
laminar/B738/pressurization_mode2
laminar/B738/push_botton/cargo_fire_test
laminar/B738/push_button/acdc_maint_pos
laminar/B738/push_button/alt_horn_cutout_pos
laminar/B738/push_button/ap_light_fo
laminar/B738/push_button/ap_light_pilot
laminar/B738/push_button/attend_pos
laminar/B738/push_button/at_light_fo
laminar/B738/push_button/at_light_pilot
laminar/B738/push_button/below_gs_copilot_pos
laminar/B738/push_button/below_gs_pilot_pos
laminar/B738/push_button/duct_ovht_test_pos
laminar/B738/push_button/fire_bell_cutout1
laminar/B738/push_button/fire_bell_cutout2
laminar/B738/push_button/flaps_test_pos
laminar/B738/push_button/fms_light_fo
laminar/B738/push_button/fms_light_pilot
laminar/B738/push_button/gear_horn_cutout_pos
laminar/B738/push_button/gpws_test_pos
laminar/B738/push_button/grd_call_pos
laminar/B738/push_button/irs_key_0
laminar/B738/push_button/irs_key_1
laminar/B738/push_button/irs_key_2
laminar/B738/push_button/irs_key_3
laminar/B738/push_button/irs_key_4
laminar/B738/push_button/irs_key_5
laminar/B738/push_button/irs_key_6
laminar/B738/push_button/irs_key_7
laminar/B738/push_button/irs_key_8
laminar/B738/push_button/irs_key_9
laminar/B738/push_button/irs_key_clr
laminar/B738/push_button/irs_key_ent
laminar/B738/push_button/mach_warn1_pos
laminar/B738/push_button/mach_warn2_pos
laminar/B738/push_button/master_caution_accept1
laminar/B738/push_button/master_caution_accept2
laminar/B738/push_button/mic_pos
laminar/B738/push_button/oxy_test_cpt2_pos
laminar/B738/push_button/oxy_test_cpt_pos
laminar/B738/push_button/oxy_test_fo2_pos
laminar/B738/push_button/oxy_test_fo_pos
laminar/B738/push_button/stall_test1
laminar/B738/push_button/stall_test2
laminar/B738/push_button/stall_test_active1
laminar/B738/push_button/stall_test_active2
laminar/B738/push_button/switch_freq_adf1
laminar/B738/push_button/switch_freq_adf2
laminar/B738/push_button/switch_freq_nav1
laminar/B738/push_button/switch_freq_nav2
laminar/B738/push_button/tat_test_pos
laminar/B738/push_button/transponder_ident
laminar/B738/push_button/trip_reset
laminar/B738/radio/adf1_act_freq_frac
laminar/B738/radio/adf1_act_freq_int
laminar/B738/radio/adf1_freq_frac
laminar/B738/radio/adf1_freq_int
laminar/B738/radio/adf2_act_freq_frac
laminar/B738/radio/adf2_act_freq_int
laminar/B738/radio/adf2_freq_frac
laminar/B738/radio/adf2_freq_int
laminar/B738/radio/arrow1
laminar/B738/radio/arrow1_no_available
laminar/B738/radio/arrow2
laminar/B738/radio/arrow2_no_available
laminar/B738/release
laminar/B738/rotary/autopilot/bank_angle
laminar/B738/satcom_variant
laminar/B738/spring_switch/landing_lights_all_on
laminar/B738/spring_toggle_switch/APU_start_pos
laminar/B738/state_cmd
laminar/B738/state_cmd2
laminar/B738/state_cmd3
laminar/B738/steer_speed
laminar/B738/strobe_cockpit
laminar/B738/switch/capt_trim_pos
laminar/B738/switch/fo_trim_pos
laminar/B738/switch/land_lights_left_pos
laminar/B738/switch/land_lights_ret_left_pos
laminar/B738/switch/land_lights_ret_right_pos
laminar/B738/switch/land_lights_right_pos
laminar/B738/switches/alt_flaps_cover_pos
laminar/B738/switches/alt_flaps_pos
laminar/B738/switches/apu_start
laminar/B738/switches/autopilot/ap_disconnect
laminar/B738/switches/autopilot/at_arm
laminar/B738/switches/autopilot/fd_ca
laminar/B738/switches/autopilot/fd_fo
laminar/B738/switches/fdr_cover_pos
laminar/B738/switches/fdr_pos
laminar/B738/switches/flt_ctr_A_cover_pos
laminar/B738/switches/flt_ctr_A_pos
laminar/B738/switches/flt_ctr_B_cover_pos
laminar/B738/switches/flt_ctr_B_pos
laminar/B738/switches/landing_gear
laminar/B738/switches/left_wiper_pos
laminar/B738/switches/nose_steer_pos
laminar/B738/switches/right_wiper_pos
laminar/B738/switches/spoiler_A_cover_pos
laminar/B738/switches/spoiler_A_pos
laminar/B738/switches/spoiler_B_cover_pos
laminar/B738/switches/spoiler_B_pos
laminar/B738/system/below_gs_annun
laminar/B738/system/below_gs_warn
laminar/B738/system/gpws_test_running
laminar/B738/system/oxy_mask_capt
laminar/B738/system/oxy_mask_fo
laminar/B738/system/takeoff_config_warn
laminar/B738/systems/brake_temp_left_in
laminar/B738/systems/brake_temp_left_out
laminar/B738/systems/brake_temp_l_in
laminar/B738/systems/brake_temp_l_out
laminar/B738/systems/brake_temp_right_in
laminar/B738/systems/brake_temp_right_out
laminar/B738/systems/brake_temp_r_in
laminar/B738/systems/brake_temp_r_out
laminar/B738/systems/cabin_gear_wrn
laminar/B738/systems/cpt_nd_shift_x
laminar/B738/systems/cpt_nd_shift_y
laminar/B738/systems/cpt_pfd_shift_x
laminar/B738/systems/cpt_pfd_shift_y
laminar/B738/systems/egt_redline1
laminar/B738/systems/egt_redline2
laminar/B738/systems/EICAS_page
laminar/B738/systems/eicas_shift_x
laminar/B738/systems/eicas_shift_y
laminar/B738/systems/fo_nd_shift_x
laminar/B738/systems/fo_nd_shift_y
laminar/B738/systems/fo_pfd_shift_x
laminar/B738/systems/fo_pfd_shift_y
laminar/B738/systems/lost_fo_inertial
laminar/B738/systems/lost_hyd_B
laminar/B738/systems/lowerDU_page
laminar/B738/systems/lower_du_shift_x
laminar/B738/systems/lower_du_shift_y
laminar/B738/systems/mach_test_enable
laminar/B738/systems/tire_speed0
laminar/B738/systems/tire_speed1
laminar/B738/systems/tire_speed2
laminar/B738/system_test
laminar/B738/system_test2
laminar/B738/system_test3
laminar/B738/system_test4
laminar/B738/system_test5
laminar/B738/system_test6
laminar/B738/system_test7
laminar/B738/system_test8
laminar/B738/tab/adjust_x
laminar/B738/tab/adjust_y
laminar/B738/tab/adjust_z
laminar/B738/tab/arrows_enable
laminar/B738/tab/bgnd_fuel
laminar/B738/tab/boot_active
laminar/B738/tab/boot_show
laminar/B738/tab/cg_pos
laminar/B738/tab/credits
laminar/B738/tab/item
laminar/B738/tab/item_checked
laminar/B738/tab/line00
laminar/B738/tab/line00g
laminar/B738/tab/line00s
laminar/B738/tab/line01
laminar/B738/tab/line01g
laminar/B738/tab/line01s
laminar/B738/tab/line02
laminar/B738/tab/line02g
laminar/B738/tab/line02s
laminar/B738/tab/line03
laminar/B738/tab/line03g
laminar/B738/tab/line03s
laminar/B738/tab/line04
laminar/B738/tab/line04g
laminar/B738/tab/line04s
laminar/B738/tab/line05
laminar/B738/tab/line05g
laminar/B738/tab/line05s
laminar/B738/tab/line06
laminar/B738/tab/line06g
laminar/B738/tab/line06s
laminar/B738/tab/line07
laminar/B738/tab/line07g
laminar/B738/tab/line07s
laminar/B738/tab/line08
laminar/B738/tab/line08g
laminar/B738/tab/line08s
laminar/B738/tab/line09
laminar/B738/tab/line09g
laminar/B738/tab/line09s
laminar/B738/tab/menu_mark
laminar/B738/tab/menu_page
laminar/B738/tab/numpad_act
laminar/B738/tab/numpad_val
laminar/B738/tab/page
laminar/B738/tab/page_info_pos
laminar/B738/tab/page_info_time
laminar/B738/tab/pos_x
laminar/B738/tab/pos_y
laminar/B738/tab/pos_z
laminar/B738/tab/power
laminar/B738/tab/rot_x
laminar/B738/tab/rot_y
laminar/B738/tab/static
laminar/B738/tab_manip/info
laminar/B738/tab_manip/line
laminar/B738/tab_manip/menu
laminar/B738/tab_manip/nav
laminar/B738/tab_manip/nav2
laminar/B738/TCAS/alt
laminar/B738/TCAS/alt10
laminar/B738/TCAS/alt11
laminar/B738/TCAS/alt12
laminar/B738/TCAS/alt13
laminar/B738/TCAS/alt14
laminar/B738/TCAS/alt15
laminar/B738/TCAS/alt16
laminar/B738/TCAS/alt17
laminar/B738/TCAS/alt18
laminar/B738/TCAS/alt19
laminar/B738/TCAS/alt2
laminar/B738/TCAS/alt3
laminar/B738/TCAS/alt4
laminar/B738/TCAS/alt5
laminar/B738/TCAS/alt6
laminar/B738/TCAS/alt7
laminar/B738/TCAS/alt8
laminar/B738/TCAS/alt9
laminar/B738/TCAS/alt_dn_up_show
laminar/B738/TCAS/alt_dn_up_show_fo
laminar/B738/TCAS/alt_fo
laminar/B738/TCAS/alt_fo10
laminar/B738/TCAS/alt_fo11
laminar/B738/TCAS/alt_fo12
laminar/B738/TCAS/alt_fo13
laminar/B738/TCAS/alt_fo14
laminar/B738/TCAS/alt_fo15
laminar/B738/TCAS/alt_fo16
laminar/B738/TCAS/alt_fo17
laminar/B738/TCAS/alt_fo18
laminar/B738/TCAS/alt_fo19
laminar/B738/TCAS/alt_fo2
laminar/B738/TCAS/alt_fo3
laminar/B738/TCAS/alt_fo4
laminar/B738/TCAS/alt_fo5
laminar/B738/TCAS/alt_fo6
laminar/B738/TCAS/alt_fo7
laminar/B738/TCAS/alt_fo8
laminar/B738/TCAS/alt_fo9
laminar/B738/TCAS/arrow_dn_up_show
laminar/B738/TCAS/arrow_dn_up_show_fo
laminar/B738/TCAS/nearest_plane_m
laminar/B738/TCAS/test
laminar/B738/TCAS/test2
laminar/B738/TCAS/traffic_ra
laminar/B738/TCAS/traffic_ra_fo
laminar/B738/TCAS/traffic_ta
laminar/B738/TCAS/traffic_ta_fo
laminar/B738/TCAS/type_show
laminar/B738/TCAS/type_show_fo
laminar/B738/TCAS/x
laminar/B738/TCAS/x_fo
laminar/B738/TCAS/y
laminar/B738/TCAS/y_fo
laminar/B738/tcas_test_test
laminar/B738/test_glare
laminar/B738/test_test
laminar/B738/test_test2
laminar/B738/test_test3
laminar/B738/test_test4
laminar/B738/throttle_override
laminar/B738/toe_brake_override
laminar/B738/toggle_switch/adf_ant1
laminar/B738/toggle_switch/adf_ant2
laminar/B738/toggle_switch/air_temp_source
laminar/B738/toggle_switch/air_valve_ctrl
laminar/B738/toggle_switch/air_valve_manual
laminar/B738/toggle_switch/alt_flaps_ctrl
laminar/B738/toggle_switch/ap_discon_test1
laminar/B738/toggle_switch/ap_discon_test2
laminar/B738/toggle_switch/ap_trim_lock_pos
laminar/B738/toggle_switch/ap_trim_pos
laminar/B738/toggle_switch/bleed_air_1_pos
laminar/B738/toggle_switch/bleed_air_2_pos
laminar/B738/toggle_switch/bleed_air_apu_pos
laminar/B738/toggle_switch/bright_test
laminar/B738/toggle_switch/cab_util_pos
laminar/B738/toggle_switch/capt_probes_pos
laminar/B738/toggle_switch/cockpit_dome_pos
laminar/B738/toggle_switch/compass_brightness_pos
laminar/B738/toggle_switch/dspl_ctrl_pnl
laminar/B738/toggle_switch/dspl_source
laminar/B738/toggle_switch/electric_hydro_pumps1_pos
laminar/B738/toggle_switch/electric_hydro_pumps2_pos
laminar/B738/toggle_switch/elt
laminar/B738/toggle_switch/el_trim_lock_pos
laminar/B738/toggle_switch/el_trim_pos
laminar/B738/toggle_switch/emer_exit_lights
laminar/B738/toggle_switch/eng_start_source
laminar/B738/toggle_switch/eq_cool_exhaust
laminar/B738/toggle_switch/eq_cool_supply
laminar/B738/toggle_switch/extinguisher_circuit_test
laminar/B738/toggle_switch/fire_test
laminar/B738/toggle_switch/flt_dk_door
laminar/B738/toggle_switch/fmc_source
laminar/B738/toggle_switch/fo_probes_pos
laminar/B738/toggle_switch/fuel_flow_pos
laminar/B738/toggle_switch/gpws_flap_cover_pos
laminar/B738/toggle_switch/gpws_flap_pos
laminar/B738/toggle_switch/gpws_gear_cover_pos
laminar/B738/toggle_switch/gpws_gear_pos
laminar/B738/toggle_switch/gpws_terr_cover_pos
laminar/B738/toggle_switch/gpws_terr_pos
laminar/B738/toggle_switch/hydro_pumps1_pos
laminar/B738/toggle_switch/hydro_pumps2_pos
laminar/B738/toggle_switch/ife_pass_seat_pos
laminar/B738/toggle_switch/irs2_restart
laminar/B738/toggle_switch/irs_dspl_sel
laminar/B738/toggle_switch/irs_dspl_sel_brt
laminar/B738/toggle_switch/irs_left
laminar/B738/toggle_switch/irs_restart
laminar/B738/toggle_switch/irs_right
laminar/B738/toggle_switch/irs_source
laminar/B738/toggle_switch/irs_sys_dspl
laminar/B738/toggle_switch/lower_du_capt
laminar/B738/toggle_switch/lower_du_fo
laminar/B738/toggle_switch/main_pnl_du_capt
laminar/B738/toggle_switch/main_pnl_du_fo
laminar/B738/toggle_switch/n1_set_adjust
laminar/B738/toggle_switch/n1_set_source
laminar/B738/toggle_switch/no_smoking_pos
laminar/B738/toggle_switch/off_on1
laminar/B738/toggle_switch/off_on2
laminar/B738/toggle_switch/position_light_pos
laminar/B738/toggle_switch/seatbelt_sign_pos
laminar/B738/toggle_switch/service_interphone
laminar/B738/toggle_switch/spd_ref
laminar/B738/toggle_switch/spd_ref_adjust
laminar/B738/toggle_switch/taxi_light_brightness_pos
laminar/B738/toggle_switch/vhf_nav_source
laminar/B738/toggle_switch/window_ovht_test
laminar/B738/toggle_switch/yaw_dumper_pos
laminar/B738/transponder/indicators/xpond_fail
laminar/B738/vnav/legs_num
laminar/B738/vnav/legs_num_before
laminar/B738/vnav/legs_num_first
laminar/B738/window/window0
laminar/B738/wings_variant
laminar/B738/yoke_disco_ap
laminar/B738/yoke_to_center
laminar/B738/zone_temp

laminar/pid/bias
laminar/pid/d
laminar/pid/i
laminar/pid/kd
laminar/pid/kf
laminar/pid/ki
laminar/pid/kp
laminar/pid/out
laminar/pid/p
laminar/pid/predict
laminar/radios/copilot/nav_bearing
laminar/radios/copilot/nav_course
laminar/radios/copilot/nav_dme
laminar/radios/copilot/nav_flag_ft
laminar/radios/copilot/nav_flag_gs
laminar/radios/copilot/nav_has_dme
laminar/radios/copilot/nav_horz_dsp
laminar/radios/copilot/nav_nav_id
laminar/radios/copilot/nav_obs
laminar/radios/copilot/nav_type
laminar/radios/copilot/nav_vert_dsp
laminar/radios/pilot/nav_bearing
laminar/radios/pilot/nav_course
laminar/radios/pilot/nav_dme
laminar/radios/pilot/nav_flag_ft
laminar/radios/pilot/nav_flag_gs
laminar/radios/pilot/nav_has_dme
laminar/radios/pilot/nav_horz_dsp
laminar/radios/pilot/nav_nav_id
laminar/radios/pilot/nav_obs
laminar/radios/pilot/nav_type
laminar/radios/pilot/nav_vert_dsp
laminar/yoke/pitch
laminar/yoke/pitch2
laminar/yoke/roll
*/




void findZibo737DataRefs(void) {
    char        buf[100];
    int i;

    // Find Zibo Mod 737 plugin by signature
    z737_PluginId = XPLMFindPluginBySignature("zibomod.by.Zibo");

	if ( z737_PluginId == XPLM_NO_PLUGIN_ID ) {
		z737_ready = 0;
		z737_version = 0;
    } else {
        if ( z737_ready == 0 ) {
            sprintf(buf, "XHSI: Zibo Mod Boeing 737-800 plugin found - loading datarefs\n");
            XPLMDebugString(buf);
            z737_ready = 1;

            /*
             * CDU 1 buttons
             */

            z737_command[Z737_KEY_FMC1_LSK1L] = XPLMFindCommand("laminar/B738/button/fmc1_1L");
            z737_command[Z737_KEY_FMC1_LSK2L] = XPLMFindCommand("laminar/B738/button/fmc1_2L");
            z737_command[Z737_KEY_FMC1_LSK3L] = XPLMFindCommand("laminar/B738/button/fmc1_3L");
            z737_command[Z737_KEY_FMC1_LSK4L] = XPLMFindCommand("laminar/B738/button/fmc1_4L");
            z737_command[Z737_KEY_FMC1_LSK5L] = XPLMFindCommand("laminar/B738/button/fmc1_5L");
            z737_command[Z737_KEY_FMC1_LSK6L] = XPLMFindCommand("laminar/B738/button/fmc1_6L");
            z737_command[Z737_KEY_FMC1_LSK1R] = XPLMFindCommand("laminar/B738/button/fmc1_1R");
            z737_command[Z737_KEY_FMC1_LSK2R] = XPLMFindCommand("laminar/B738/button/fmc1_2R");
            z737_command[Z737_KEY_FMC1_LSK3R] = XPLMFindCommand("laminar/B738/button/fmc1_3R");
            z737_command[Z737_KEY_FMC1_LSK4R] = XPLMFindCommand("laminar/B738/button/fmc1_4R");
            z737_command[Z737_KEY_FMC1_LSK5R] = XPLMFindCommand("laminar/B738/button/fmc1_5R");
            z737_command[Z737_KEY_FMC1_LSK6R] = XPLMFindCommand("laminar/B738/button/fmc1_6R");

            z737_command[Z737_KEY_FMC1_INIT] = XPLMFindCommand("laminar/B738/button/fmc1_init_ref");
            z737_command[Z737_KEY_FMC1_RTE] = XPLMFindCommand("laminar/B738/button/fmc1_rte");
            z737_command[Z737_KEY_FMC1_CLB] = XPLMFindCommand("laminar/B738/button/fmc1_clb");
            z737_command[Z737_KEY_FMC1_CRZ] = XPLMFindCommand("laminar/B738/button/fmc1_crz");
            z737_command[Z737_KEY_FMC1_DES] = XPLMFindCommand("laminar/B738/button/fmc1_des");

            z737_command[Z737_KEY_FMC1_MENU] = XPLMFindCommand("laminar/B738/button/fmc1_menu");
            z737_command[Z737_KEY_FMC1_LEGS] = XPLMFindCommand("laminar/B738/button/fmc1_legs");
            z737_command[Z737_KEY_FMC1_DEP_ARR] = XPLMFindCommand("laminar/B738/button/fmc1_dep_app");
            z737_command[Z737_KEY_FMC1_HOLD] = XPLMFindCommand("laminar/B738/button/fmc1_hold");
            z737_command[Z737_KEY_FMC1_PROG] = XPLMFindCommand("laminar/B738/button/fmc1_prog");
            z737_command[Z737_KEY_FMC1_EXEC] = XPLMFindCommand("laminar/B738/button/fmc1_exec");

            z737_command[Z737_KEY_FMC1_PERF] = XPLMFindCommand("laminar/B738/button/fmc1_n1_lim");
            z737_command[Z737_KEY_FMC1_FIX] = XPLMFindCommand("laminar/B738/button/fmc1_fix");
            z737_command[Z737_KEY_FMC1_PREV_PAGE] = XPLMFindCommand("laminar/B738/button/fmc1_prev_page");
            z737_command[Z737_KEY_FMC1_NEXT_PAGE] = XPLMFindCommand("laminar/B738/button/fmc1_next_page");

            z737_command[Z737_KEY_FMC1_DEL] = XPLMFindCommand("laminar/B738/button/fmc1_del");
            z737_command[Z737_KEY_FMC1_CLR] = XPLMFindCommand("laminar/B738/button/fmc1_clr");
            z737_command[Z737_KEY_FMC1_SPACE] = XPLMFindCommand("laminar/B738/button/fmc1_SP");
            z737_command[Z737_KEY_FMC1_PLUS_M] = XPLMFindCommand("laminar/B738/button/fmc1_minus");
            z737_command[Z737_KEY_FMC1_DOT] = XPLMFindCommand("laminar/B738/button/fmc1_period");
            z737_command[Z737_KEY_FMC1_SLASH] = XPLMFindCommand("laminar/B738/button/fmc1_slash");

            z737_command[Z737_KEY_FMC1_0] = XPLMFindCommand("laminar/B738/button/fmc1_0");
            z737_command[Z737_KEY_FMC1_1] = XPLMFindCommand("laminar/B738/button/fmc1_1");
            z737_command[Z737_KEY_FMC1_2] = XPLMFindCommand("laminar/B738/button/fmc1_2");
            z737_command[Z737_KEY_FMC1_3] = XPLMFindCommand("laminar/B738/button/fmc1_3");
            z737_command[Z737_KEY_FMC1_4] = XPLMFindCommand("laminar/B738/button/fmc1_4");
            z737_command[Z737_KEY_FMC1_5] = XPLMFindCommand("laminar/B738/button/fmc1_5");
            z737_command[Z737_KEY_FMC1_6] = XPLMFindCommand("laminar/B738/button/fmc1_6");
            z737_command[Z737_KEY_FMC1_7] = XPLMFindCommand("laminar/B738/button/fmc1_7");
            z737_command[Z737_KEY_FMC1_8] = XPLMFindCommand("laminar/B738/button/fmc1_8");
            z737_command[Z737_KEY_FMC1_9] = XPLMFindCommand("laminar/B738/button/fmc1_9");
            z737_command[Z737_KEY_FMC1_A] = XPLMFindCommand("laminar/B738/button/fmc1_A");
            z737_command[Z737_KEY_FMC1_B] = XPLMFindCommand("laminar/B738/button/fmc1_B");
            z737_command[Z737_KEY_FMC1_C] = XPLMFindCommand("laminar/B738/button/fmc1_C");
            z737_command[Z737_KEY_FMC1_D] = XPLMFindCommand("laminar/B738/button/fmc1_D");
            z737_command[Z737_KEY_FMC1_E] = XPLMFindCommand("laminar/B738/button/fmc1_E");
            z737_command[Z737_KEY_FMC1_F] = XPLMFindCommand("laminar/B738/button/fmc1_F");
            z737_command[Z737_KEY_FMC1_G] = XPLMFindCommand("laminar/B738/button/fmc1_G");
            z737_command[Z737_KEY_FMC1_H] = XPLMFindCommand("laminar/B738/button/fmc1_H");
            z737_command[Z737_KEY_FMC1_I] = XPLMFindCommand("laminar/B738/button/fmc1_I");
            z737_command[Z737_KEY_FMC1_J] = XPLMFindCommand("laminar/B738/button/fmc1_J");
            z737_command[Z737_KEY_FMC1_K] = XPLMFindCommand("laminar/B738/button/fmc1_K");
            z737_command[Z737_KEY_FMC1_L] = XPLMFindCommand("laminar/B738/button/fmc1_L");
            z737_command[Z737_KEY_FMC1_M] = XPLMFindCommand("laminar/B738/button/fmc1_M");
            z737_command[Z737_KEY_FMC1_N] = XPLMFindCommand("laminar/B738/button/fmc1_N");
            z737_command[Z737_KEY_FMC1_O] = XPLMFindCommand("laminar/B738/button/fmc1_O");
            z737_command[Z737_KEY_FMC1_P] = XPLMFindCommand("laminar/B738/button/fmc1_P");
            z737_command[Z737_KEY_FMC1_Q] = XPLMFindCommand("laminar/B738/button/fmc1_Q");
            z737_command[Z737_KEY_FMC1_R] = XPLMFindCommand("laminar/B738/button/fmc1_R");
            z737_command[Z737_KEY_FMC1_S] = XPLMFindCommand("laminar/B738/button/fmc1_S");
            z737_command[Z737_KEY_FMC1_T] = XPLMFindCommand("laminar/B738/button/fmc1_T");
            z737_command[Z737_KEY_FMC1_U] = XPLMFindCommand("laminar/B738/button/fmc1_U");
            z737_command[Z737_KEY_FMC1_V] = XPLMFindCommand("laminar/B738/button/fmc1_V");
            z737_command[Z737_KEY_FMC1_W] = XPLMFindCommand("laminar/B738/button/fmc1_W");
            z737_command[Z737_KEY_FMC1_X] = XPLMFindCommand("laminar/B738/button/fmc1_X");
            z737_command[Z737_KEY_FMC1_Y] = XPLMFindCommand("laminar/B738/button/fmc1_Y");
            z737_command[Z737_KEY_FMC1_Z] = XPLMFindCommand("laminar/B738/button/fmc1_Z");

            /*
             * CDU 2 buttons
             */

            z737_command[Z737_KEY_FMC2_LSK1L] = XPLMFindCommand("laminar/B738/button/fmc2_1L");
            z737_command[Z737_KEY_FMC2_LSK2L] = XPLMFindCommand("laminar/B738/button/fmc2_2L");
            z737_command[Z737_KEY_FMC2_LSK3L] = XPLMFindCommand("laminar/B738/button/fmc2_3L");
            z737_command[Z737_KEY_FMC2_LSK4L] = XPLMFindCommand("laminar/B738/button/fmc2_4L");
            z737_command[Z737_KEY_FMC2_LSK5L] = XPLMFindCommand("laminar/B738/button/fmc2_5L");
            z737_command[Z737_KEY_FMC2_LSK6L] = XPLMFindCommand("laminar/B738/button/fmc2_6L");
            z737_command[Z737_KEY_FMC2_LSK1R] = XPLMFindCommand("laminar/B738/button/fmc2_1R");
            z737_command[Z737_KEY_FMC2_LSK2R] = XPLMFindCommand("laminar/B738/button/fmc2_2R");
            z737_command[Z737_KEY_FMC2_LSK3R] = XPLMFindCommand("laminar/B738/button/fmc2_3R");
            z737_command[Z737_KEY_FMC2_LSK4R] = XPLMFindCommand("laminar/B738/button/fmc2_4R");
            z737_command[Z737_KEY_FMC2_LSK5R] = XPLMFindCommand("laminar/B738/button/fmc2_5R");
            z737_command[Z737_KEY_FMC2_LSK6R] = XPLMFindCommand("laminar/B738/button/fmc2_6R");

            z737_command[Z737_KEY_FMC2_INIT] = XPLMFindCommand("laminar/B738/button/fmc2_init_ref");
            z737_command[Z737_KEY_FMC2_RTE] = XPLMFindCommand("laminar/B738/button/fmc2_rte");
            z737_command[Z737_KEY_FMC2_CLB] = XPLMFindCommand("laminar/B738/button/fmc2_clb");
            z737_command[Z737_KEY_FMC2_CRZ] = XPLMFindCommand("laminar/B738/button/fmc2_crz");
            z737_command[Z737_KEY_FMC2_DES] = XPLMFindCommand("laminar/B738/button/fmc2_des");

            z737_command[Z737_KEY_FMC2_MENU] = XPLMFindCommand("laminar/B738/button/fmc2_menu");
            z737_command[Z737_KEY_FMC2_LEGS] = XPLMFindCommand("laminar/B738/button/fmc2_legs");
            z737_command[Z737_KEY_FMC2_DEP_ARR] = XPLMFindCommand("laminar/B738/button/fmc2_dep_app");
            z737_command[Z737_KEY_FMC2_HOLD] = XPLMFindCommand("laminar/B738/button/fmc2_hold");
            z737_command[Z737_KEY_FMC2_PROG] = XPLMFindCommand("laminar/B738/button/fmc2_prog");
            z737_command[Z737_KEY_FMC2_EXEC] = XPLMFindCommand("laminar/B738/button/fmc2_exec");

            z737_command[Z737_KEY_FMC2_PERF] = XPLMFindCommand("laminar/B738/button/fmc2_n1_lim");
            z737_command[Z737_KEY_FMC2_FIX] = XPLMFindCommand("laminar/B738/button/fmc2_fix");
            z737_command[Z737_KEY_FMC2_PREV_PAGE] = XPLMFindCommand("laminar/B738/button/fmc2_prev_page");
            z737_command[Z737_KEY_FMC2_NEXT_PAGE] = XPLMFindCommand("laminar/B738/button/fmc2_next_page");

            z737_command[Z737_KEY_FMC2_DEL] = XPLMFindCommand("laminar/B738/button/fmc2_del");
            z737_command[Z737_KEY_FMC2_CLR] = XPLMFindCommand("laminar/B738/button/fmc2_clr");
            z737_command[Z737_KEY_FMC2_SPACE] = XPLMFindCommand("laminar/B738/button/fmc2_SP");
            z737_command[Z737_KEY_FMC2_PLUS_M] = XPLMFindCommand("laminar/B738/button/fmc2_minus");
            z737_command[Z737_KEY_FMC2_DOT] = XPLMFindCommand("laminar/B738/button/fmc2_period");
            z737_command[Z737_KEY_FMC2_SLASH] = XPLMFindCommand("laminar/B738/button/fmc2_slash");

            z737_command[Z737_KEY_FMC2_0] = XPLMFindCommand("laminar/B738/button/fmc2_0");
            z737_command[Z737_KEY_FMC2_1] = XPLMFindCommand("laminar/B738/button/fmc2_1");
            z737_command[Z737_KEY_FMC2_2] = XPLMFindCommand("laminar/B738/button/fmc2_2");
            z737_command[Z737_KEY_FMC2_3] = XPLMFindCommand("laminar/B738/button/fmc2_3");
            z737_command[Z737_KEY_FMC2_4] = XPLMFindCommand("laminar/B738/button/fmc2_4");
            z737_command[Z737_KEY_FMC2_5] = XPLMFindCommand("laminar/B738/button/fmc2_5");
            z737_command[Z737_KEY_FMC2_6] = XPLMFindCommand("laminar/B738/button/fmc2_6");
            z737_command[Z737_KEY_FMC2_7] = XPLMFindCommand("laminar/B738/button/fmc2_7");
            z737_command[Z737_KEY_FMC2_8] = XPLMFindCommand("laminar/B738/button/fmc2_8");
            z737_command[Z737_KEY_FMC2_9] = XPLMFindCommand("laminar/B738/button/fmc2_9");
            z737_command[Z737_KEY_FMC2_A] = XPLMFindCommand("laminar/B738/button/fmc2_A");
            z737_command[Z737_KEY_FMC2_B] = XPLMFindCommand("laminar/B738/button/fmc2_B");
            z737_command[Z737_KEY_FMC2_C] = XPLMFindCommand("laminar/B738/button/fmc2_C");
            z737_command[Z737_KEY_FMC2_D] = XPLMFindCommand("laminar/B738/button/fmc2_D");
            z737_command[Z737_KEY_FMC2_E] = XPLMFindCommand("laminar/B738/button/fmc2_E");
            z737_command[Z737_KEY_FMC2_F] = XPLMFindCommand("laminar/B738/button/fmc2_F");
            z737_command[Z737_KEY_FMC2_G] = XPLMFindCommand("laminar/B738/button/fmc2_G");
            z737_command[Z737_KEY_FMC2_H] = XPLMFindCommand("laminar/B738/button/fmc2_H");
            z737_command[Z737_KEY_FMC2_I] = XPLMFindCommand("laminar/B738/button/fmc2_I");
            z737_command[Z737_KEY_FMC2_J] = XPLMFindCommand("laminar/B738/button/fmc2_J");
            z737_command[Z737_KEY_FMC2_K] = XPLMFindCommand("laminar/B738/button/fmc2_K");
            z737_command[Z737_KEY_FMC2_L] = XPLMFindCommand("laminar/B738/button/fmc2_L");
            z737_command[Z737_KEY_FMC2_M] = XPLMFindCommand("laminar/B738/button/fmc2_M");
            z737_command[Z737_KEY_FMC2_N] = XPLMFindCommand("laminar/B738/button/fmc2_N");
            z737_command[Z737_KEY_FMC2_O] = XPLMFindCommand("laminar/B738/button/fmc2_O");
            z737_command[Z737_KEY_FMC2_P] = XPLMFindCommand("laminar/B738/button/fmc2_P");
            z737_command[Z737_KEY_FMC2_Q] = XPLMFindCommand("laminar/B738/button/fmc2_Q");
            z737_command[Z737_KEY_FMC2_R] = XPLMFindCommand("laminar/B738/button/fmc2_R");
            z737_command[Z737_KEY_FMC2_S] = XPLMFindCommand("laminar/B738/button/fmc2_S");
            z737_command[Z737_KEY_FMC2_T] = XPLMFindCommand("laminar/B738/button/fmc2_T");
            z737_command[Z737_KEY_FMC2_U] = XPLMFindCommand("laminar/B738/button/fmc2_U");
            z737_command[Z737_KEY_FMC2_V] = XPLMFindCommand("laminar/B738/button/fmc2_V");
            z737_command[Z737_KEY_FMC2_W] = XPLMFindCommand("laminar/B738/button/fmc2_W");
            z737_command[Z737_KEY_FMC2_X] = XPLMFindCommand("laminar/B738/button/fmc2_X");
            z737_command[Z737_KEY_FMC2_Y] = XPLMFindCommand("laminar/B738/button/fmc2_Y");
            z737_command[Z737_KEY_FMC2_Z] = XPLMFindCommand("laminar/B738/button/fmc2_Z");

            /*
             * FMS
             */
            laminar_B738_fms_legs = XPLMFindCommand("laminar/B738/fms/legs");
            laminar_B738_fms_legs_lat = XPLMFindCommand("laminar/B738/fms/legs_lat");
            laminar_B738_fms_legs_lon = XPLMFindCommand("laminar/B738/fms/legs_lon");
            laminar_B738_fms_legs_alt_calc = XPLMFindCommand("laminar/B738/fms/legs_alt_calc");
            laminar_B738_fms_legs_alt_rest1 = XPLMFindCommand("laminar/B738/fms/legs_alt_rest1");
            laminar_B738_fms_legs_spd = XPLMFindCommand("laminar/B738/fms/legs_spd");

            laminar_B738_fms_num_of_wpts = XPLMFindCommand("laminar/B738/fms/num_of_wpts");


            sprintf(buf, "XHSI: Z737 plugin found - loading CDU datarefs\n");
            XPLMDebugString(buf);
            z737_cdu_ready = 1;

            for (i=0; i<Z737_FMC_LINES; i++) {
            	// CDU1

            	// Label (always small)
            	sprintf(buf, "laminar/B738/fmc1/Line%02d_X", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc1_label_white[i] = XPLMFindDataRef(buf);

            	// Content Small
            	sprintf(buf, "laminar/B738/fmc1/Line%02d_S", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc1_content_small[i] = XPLMFindDataRef(buf);

            	// Content Line White
            	sprintf(buf, "laminar/B738/fmc1/Line%02d_L", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc1_content_white[i] = XPLMFindDataRef(buf);

            	// Content I : inverted
            	sprintf(buf, "laminar/B738/fmc1/Line%02d_I", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc1_content_inverted[i] = XPLMFindDataRef(buf);

            	// Content G : unused
            	sprintf(buf, "laminar/B738/fmc1/Line%02d_G", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc1_content_green[i] = XPLMFindDataRef(buf);

            	// Content M : unused
            	sprintf(buf, "laminar/B738/fmc1/Line%02d_M", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc1_content_magenta[i] = XPLMFindDataRef(buf);


            	// CDU2
            	// Label (always small)
            	sprintf(buf, "laminar/B738/fmc2/Line%02d_X", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc2_label_white[i] = XPLMFindDataRef(buf);

            	// Content Small
            	sprintf(buf, "laminar/B738/fmc2/Line%02d_S", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc2_content_small[i] = XPLMFindDataRef(buf);

            	// Content White
            	sprintf(buf, "laminar/B738/fmc2/Line%02d_L", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc2_content_white[i] = XPLMFindDataRef(buf);

            	// Content I : inverted
            	sprintf(buf, "laminar/B738/fmc2/Line%02d_I", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc2_content_inverted[i] = XPLMFindDataRef(buf);

            	// Content G : unused
            	sprintf(buf, "laminar/B738/fmc2/Line%02d_G", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc2_content_green[i] = XPLMFindDataRef(buf);

            	// Content M : unused
            	sprintf(buf, "laminar/B738/fmc2/Line%02d_M", i+1);
            	XPLMDebugString(buf); XPLMDebugString("\n");
            	z737_fmc2_content_magenta[i] = XPLMFindDataRef(buf);

            }

            // CDU1
            z737_fmc1_scratch_inverted = XPLMFindDataRef("laminar/B738/fmc1/Line_entry_I");
            z737_fmc1_scratch_white = XPLMFindDataRef("laminar/B738/fmc1/Line_entry");

            z737_fmc1_title_inverted = XPLMFindDataRef("laminar/B738/fmc1/Line00_I");  // Unused
            z737_fmc1_title_magenta = XPLMFindDataRef("laminar/B738/fmc1/Line00_M");  // Unused
            z737_fmc1_title_green = XPLMFindDataRef("laminar/B738/fmc1/Line00_G");  // Unused

            z737_fmc1_title_white = XPLMFindDataRef("laminar/B738/fmc1/Line00_L");
            z737_fmc1_title_small = XPLMFindDataRef("laminar/B738/fmc1/Line00_S");

            // CDU2
            z737_fmc2_scratch_inverted = XPLMFindDataRef("laminar/B738/fmc2/Line_entry_I");
            z737_fmc2_scratch_white = XPLMFindDataRef("laminar/B738/fmc2/Line_entry");

            z737_fmc2_title_inverted = XPLMFindDataRef("laminar/B738/fmc2/Line00_I");  // Unused
            z737_fmc2_title_magenta = XPLMFindDataRef("laminar/B738/fmc2/Line00_M");  // Unused
            z737_fmc2_title_green = XPLMFindDataRef("laminar/B738/fmc2/Line00_G");  // Unused

            z737_fmc2_title_white = XPLMFindDataRef("laminar/B738/fmc2/Line00_L");
            z737_fmc2_title_small = XPLMFindDataRef("laminar/B738/fmc2/Line00_S");


        }
    }
}


float checkZibo737Callback(
    float	inElapsedSinceLastCall,
    float	inElapsedTimeSinceLastFlightLoop,
    int		inCounter,
    void *	inRefcon) {

	findZibo737DataRefs();

	// come back in 5sec
	return 5.0;
}


void writeZibo737DataRef(int id, float value) {
    char info_string[80];
    switch (id) {
		case Z737_KEY_PRESS :
			if (value>=0 && value <= Z737_KEY_MAX && z737_ready) {
				if (z737_command[(int)value] == NULL) {
					sprintf(info_string, "XHSI: Null pointer Z737 Command Once VALUE=%d\n", (int) value);
					XPLMDebugString(info_string);
				} else {
					XPLMCommandOnce(z737_command[(int)value]);
				}
			}
			break;
    }
}


/**
 * ZiboMod FMC Strings does not terminate with 0x00 but 0xd0
 * This function create a proper C String, 0x00 terminated
 * Ensuring that there will be not buffer overrun by limiting
 * string length to Z737_CDU_LINE_WIDTH
 * Return: String length
 */
int getZiboFMCString(char buffer[], XPLMDataRef fmcDataRef) {
	int datalen;
	int i;
	// Ensure that the buffer is filled by zero before grabbing the dataref content
	memset( buffer, '\0', Z737_CDU_LINE_WIDTH );
	// Grab the dataref
	datalen = XPLMGetDatab(fmcDataRef,buffer,0,Z737_CDU_LINE_WIDTH);
	// Ensure that the buffer is correctly 0 terminated
    buffer[Z737_CDU_LINE_WIDTH]=0;
    // Replace 0xd0 with 0x00
    i=0;
    while (buffer[i] != 0 && i < Z737_CDU_LINE_WIDTH) {
    	if (buffer[i] == 0xd0) buffer[i] = 0;
    	i++;
    }
    return (datalen > 0) ? (int)strlen(buffer) : 0;
}


/**
 * cdu_id should be 0 for left, 1 for right. Any value different from 0 is treated as right
 */
int createZibo737CduPacket(int cdu_id) {

   int i,l;
   int j;
   int p;
   char color = 'u';
   int label_len, small_len, white_len, inverted_len, magenta_len, green_len;
   int space = 0;
   char label_buffer[Z737_CDU_BUF_LEN];
   char inverted_buffer[Z737_CDU_BUF_LEN];
   char white_buffer[Z737_CDU_BUF_LEN];
   char small_buffer[Z737_CDU_BUF_LEN];
   char magenta_buffer[Z737_CDU_BUF_LEN];
   char green_buffer[Z737_CDU_BUF_LEN];

   char encoded_string[Z737_CDU_BUF_LEN];

   /*
    * Packet header
    */
   memset( &zibo737CduMsgPacket, 0, sizeof(zibo737CduMsgPacket));
   strncpy(zibo737CduMsgPacket.packet_id, "QPAM", 4);
   zibo737CduMsgPacket.nb_of_lines = custom_htoni(Z737_CDU_LINES);
   zibo737CduMsgPacket.side = custom_htoni(cdu_id);

   // Line count (0 to 13)
   l=0;

   /*
    *  Page title
    */
   if (cdu_id) {
	   white_len =    getZiboFMCString(white_buffer,    z737_fmc2_title_white);
	   small_len =    getZiboFMCString(small_buffer,    z737_fmc2_title_small);
	   inverted_len = getZiboFMCString(inverted_buffer, z737_fmc2_title_inverted);
   } else {
	   white_len =    getZiboFMCString(white_buffer,    z737_fmc1_title_white);
	   small_len =    getZiboFMCString(small_buffer,    z737_fmc1_title_small);
	   inverted_len = getZiboFMCString(inverted_buffer, z737_fmc1_title_inverted);
   }

   color = 'u';
   memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
   // encoded_string[0] = 0;
   space=0;
   for (j=0,p=0; (j<45) && (p<(Z737_CDU_BUF_LEN-9)); j++) {
  	 if ((j < white_len) && (white_buffer[j] != ' ') ) {
  		 if (color != 'W') {
  			 color = 'W';
				 space=0;
  			 if (p>0) { encoded_string[p++] = ';'; }
  			 encoded_string[p++] = 'l';
  			 encoded_string[p++] = 'w';
  			 encoded_string[p++] = '0' + j/10;
  			 encoded_string[p++] = '0' + j%10;
  		 }
  		 if (white_buffer[j] < ' ') white_buffer[j] = '?';
  		 encoded_string[p++]= white_buffer[j];
  	 } else if ((j < small_len) && (small_buffer[j] != ' ') ) {
  		 if (color != 'w') {
  			 color = 'w';
				 space=0;
  			 if (p>0) { encoded_string[p++] = ';'; }
  			 encoded_string[p++] = 's';
  			 encoded_string[p++] = 'w';
  			 encoded_string[p++] = '0' + j/10;
  			 encoded_string[p++] = '0' + j%10;
  		 }
  		 if (small_buffer[j] < ' ') small_buffer[j] = '?';
  		 encoded_string[p++]= small_buffer[j];
  	 } else if ((j < inverted_len) && (inverted_buffer[j] != ' ') ) {
  		 if (color != 'I') {
  			 color = 'I';
				 space=0;
  			 if (p>0) { encoded_string[p++] = ';'; }
  			 encoded_string[p++] = 'l';
  			 encoded_string[p++] = 'i';
  			 encoded_string[p++] = '0' + j/10;
  			 encoded_string[p++] = '0' + j%10;
  		 }
  		 if (inverted_buffer[j] < ' ') inverted_buffer[j] = '?';
  		 encoded_string[p++]= inverted_buffer[j];
  	 } else if (( (j < white_len) || (j<small_len) || (j<inverted_len) )
  			  && (space<2) ) {
  		 encoded_string[p++]=' ';
  		 space++;
  	 } else if (space>1) {
  		 color = 'u';
  	 } else {
  	 	 // color = 'u';
  		 break;
  	 }

   }
   encoded_string[p] = 0;

   strcpy(zibo737CduMsgPacket.lines[l].linestr,encoded_string);
   zibo737CduMsgPacket.lines[l].len = custom_htoni((int)strlen(zibo737CduMsgPacket.lines[l].linestr));
   zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
   l++;

   for(i=0; i<Z737_FMC_LINES; i++){

	   if (cdu_id) {
		     label_len =     getZiboFMCString(label_buffer,    z737_fmc2_label_white[i]);
	    	 white_len =     getZiboFMCString(white_buffer,    z737_fmc2_content_white[i]);
		     small_len =     getZiboFMCString(small_buffer,    z737_fmc2_content_small[i]);
	    	 inverted_len =  getZiboFMCString(inverted_buffer, z737_fmc2_content_inverted[i]);
	    	 magenta_len =   getZiboFMCString(magenta_buffer,  z737_fmc2_content_magenta[i]);
	    	 green_len =     getZiboFMCString(green_buffer,    z737_fmc2_content_green[i]);
	   } else {
		     label_len =     getZiboFMCString(label_buffer,    z737_fmc1_label_white[i]);
	    	 white_len =     getZiboFMCString(white_buffer,    z737_fmc1_content_white[i]);
	    	 small_len =     getZiboFMCString(small_buffer,    z737_fmc1_content_small[i]);
	    	 inverted_len =  getZiboFMCString(inverted_buffer, z737_fmc1_content_inverted[i]);
	    	 magenta_len =   getZiboFMCString(magenta_buffer,  z737_fmc1_content_magenta[i]);
	    	 green_len =     getZiboFMCString(green_buffer,    z737_fmc1_content_green[i]);
	   }

	 /*
	  * Label line
	  */
     color = 'u';
     memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
     // encoded_string[0] = 0;
     space = 0;

     for (j=0,p=0; (j<45) && (p<(Z737_CDU_BUF_LEN-9)); j++) {
    	 if ((j < label_len) && (label_buffer[j] > 32)) {
    		 if (color != 'w') {
    			 color = 'w';
				 space = 0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = color;
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 encoded_string[p++] = label_buffer[j];
    	 } else if (((j < label_len)) && (space<2) ) {
    		 encoded_string[p++] = ' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    	 	 // color = 'u';
    		 break;
    	 }
     }

     encoded_string[p] = 0;

     strcpy(zibo737CduMsgPacket.lines[l].linestr,encoded_string);
     zibo737CduMsgPacket.lines[l].len = custom_htoni(p);
     zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
     l++;


	 /*
	  * Content line
	  */

     color = 'u';
     memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
     // encoded_string[0] = 0;
     space=0;
     for (j=0,p=0; (j<45) && (p<(Z737_CDU_BUF_LEN-9)); j++) {
    	 if ((j < white_len) && (white_buffer[j] != ' ') ) {
    		 if (color != 'W') {
    			 color = 'W';
				 space = 0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'w';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (white_buffer[j] < ' ') white_buffer[j] = '?';
    		 encoded_string[p++] = white_buffer[j];
    	 } else if ((j < small_len) && (small_buffer[j] != ' ') ) {
    		 if (color != 'w') {
    			 color = 'w';
				 space = 0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 's';
    			 encoded_string[p++] = 'w';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (small_buffer[j] < ' ') small_buffer[j] = '?';
    		 encoded_string[p++] = small_buffer[j];
    	 }  else if ((j < inverted_len) && (inverted_buffer[j] != ' ') ) {
    		 if (color != 'I') {
    			 color = 'I';
				 space = 0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'i';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (inverted_buffer[j] < ' ') inverted_buffer[j] = '?';
    		 encoded_string[p++] = inverted_buffer[j];
    	 } else if ((j < magenta_len) && (magenta_buffer[j] != ' ') ) {
    		 if (color != 'M') {
    			 color = 'M';
				 space = 0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'm';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (magenta_buffer[j] < ' ') magenta_buffer[j] = '?';
    		 encoded_string[p++] = magenta_buffer[j];
    	 } else if ((j < green_len) && (green_buffer[j] != ' ') ) {
    		 if (color != 'G') {
    			 color = 'G';
				 space = 0;
    			 if (p>0) { encoded_string[p++] = ';'; }
    			 encoded_string[p++] = 'l';
    			 encoded_string[p++] = 'g';
    			 encoded_string[p++] = '0' + j/10;
    			 encoded_string[p++] = '0' + j%10;
    		 }
    		 if (green_buffer[j] < ' ') green_buffer[j] = '?';
    		 encoded_string[p++] = green_buffer[j];
    	 } else if (( (j < white_len) || (j<small_len) || (j<inverted_len) || (j<magenta_len) || (j<green_len))
    			  && (space<2) ) {
    		 encoded_string[p++]=' ';
    		 space++;
    	 } else if (space>1) {
    		 color = 'u';
    	 } else {
    	 	 // color = 'u';
    		 break;
    	 }

     }
     encoded_string[p] = 0;
     strcpy(zibo737CduMsgPacket.lines[l].linestr,encoded_string);
     zibo737CduMsgPacket.lines[l].len = custom_htoni(p);
     zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
     l++;

   }

   // Scratch pad line
   zibo737CduMsgPacket.lines[l].lineno = custom_htoni(l);
   if (cdu_id) {
	   inverted_len =  getZiboFMCString(inverted_buffer,  z737_fmc2_scratch_inverted);
	   white_len =   getZiboFMCString(white_buffer,   z737_fmc2_scratch_white);
   } else {
	   inverted_len =  getZiboFMCString(inverted_buffer,  z737_fmc1_scratch_inverted);
	   white_len =   getZiboFMCString(white_buffer,   z737_fmc1_scratch_white);
   }

   color = 'u';
   memset( encoded_string, '\0', Z737_CDU_BUF_LEN );
   // encoded_string[0] = 0;
   p=0;
   if ((white_len>0) && (white_buffer[0] > 32)) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'w';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], white_buffer);
   } else if ((inverted_len>0) && (inverted_buffer[0] > 32)) {
	   encoded_string[p++] = 'l';
	   encoded_string[p++] = 'i';
	   encoded_string[p++] = '0';
	   encoded_string[p++] = '0';
	   strcpy(&encoded_string[p], inverted_buffer);
   }
   strcpy(zibo737CduMsgPacket.lines[l].linestr, encoded_string);
   zibo737CduMsgPacket.lines[l].len = custom_htoni((int)strlen(zibo737CduMsgPacket.lines[l].linestr));

   return 4 + 4 + Z737_CDU_LINES * 88;
}

float sendZibo737MsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int cdu_packet_size;


	// TODO: Store previous packet / Send if different
	// TODO: Force sending CDU packets every 2 seconds (application startup time)
    // TODO: adjust packet delay. Set to adc * 3.0
	z737_msg_delay = adc_data_delay * 3.0f;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && z737_cdu_ready)  {

		cdu_packet_size = createZibo737CduPacket(XPLMGetDatai(cdu_pilot_side));
		if ( cdu_packet_size > 0 ) {
			for (i=0; i<NUM_DEST; i++) {
				if (dest_enable[i]) {
					if (sendto(sockfd, (const char*)&zibo737CduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
						XPLMDebugString("XHSI: caught error while sending Zibo737McduMsg left CDU packet! (");
						XPLMDebugString((char * const) strerror(GET_ERRNO));
						XPLMDebugString(")\n");
					}
				}
			}
			if (XPLMGetDatai(cdu_pilot_side)==0)
				zibo737Cdu1PreviousMsgPacket = zibo737CduMsgPacket;
			else
				zibo737Cdu2PreviousMsgPacket = zibo737CduMsgPacket;
		}
		if (XPLMGetDatai(cdu_pilot_side) != XPLMGetDatai(cdu_copilot_side)) {
			cdu_packet_size = createZibo737CduPacket(XPLMGetDatai(cdu_copilot_side));
			if ( cdu_packet_size > 0 ) {
				for (i=0; i<NUM_DEST; i++) {
					if (dest_enable[i]) {
						if (sendto(sockfd, (const char*)&zibo737CduMsgPacket, cdu_packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
							XPLMDebugString("XHSI: caught error while sending Zibo737McduMsg right CDU packet! (");
							XPLMDebugString((char * const) strerror(GET_ERRNO));
							XPLMDebugString(")\n");
						}
					}
				}
			}
			if (XPLMGetDatai(cdu_copilot_side)==0)
				zibo737Cdu1PreviousMsgPacket = zibo737CduMsgPacket;
			else
				zibo737Cdu2PreviousMsgPacket = zibo737CduMsgPacket;
		}

        return z737_msg_delay;

	} else {
		return 10.0f;
	}

}
