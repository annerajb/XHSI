/*
 * datarefs_qpac.h
 *
 *  Created on: 19 févr. 2014
 *      Author: Nicolas Carel
 */

#ifndef DATAREFS_QPAC_H_
#define DATAREFS_QPAC_H_

// Key codes (values passed with QPAC_KEY_PRESS ID)
#define QPAC_KEY_TO_CONFIG 1
#define QPAC_KEY_PUSH_ALT  2
#define QPAC_KEY_PULL_ALT  3
#define QPAC_KEY_PUSH_VS   4
#define QPAC_KEY_PULL_VS   5
#define QPAC_KEY_PUSH_HDG  6
#define QPAC_KEY_PULL_HDG  7
#define QPAC_KEY_PUSH_SPD  8
#define QPAC_KEY_PULL_SPD  9
#define QPAC_KEY_ATHR      10
#define QPAC_KEY_APPR      11
#define QPAC_KEY_EXPED     12
#define QPAC_KEY_LOC       13
#define QPAC_KEY_ABRK_LOW  14
#define QPAC_KEY_ABRK_MED  15
#define QPAC_KEY_ABRK_MAX  16
#define QPAC_KEY_CHECK_CAB 17
#define QPAC_KEY_RMP1_VHF1 18
#define QPAC_KEY_RMP1_VHF2 19
#define QPAC_KEY_RMP1_FREQ_DOWN_LRG 20
#define QPAC_KEY_RMP1_FREQ_DOWN_SML 21
#define QPAC_KEY_RMP1_FREQ_UP_LRG 22
#define QPAC_KEY_RMP1_FREQ_UP_SML 23
#define QPAC_KEY_RMP1_SWAP 24
#define QPAC_KEY_RMP2_VHF1 25
#define QPAC_KEY_RMP2_VHF2 26
#define QPAC_KEY_RMP2_FREQ_DOWN_LRG 27
#define QPAC_KEY_RMP2_FREQ_DOWN_SML 28
#define QPAC_KEY_RMP2_FREQ_UP_LRG 29
#define QPAC_KEY_RMP2_FREQ_UP_SML 30
#define QPAC_KEY_RMP2_SWAP 31
#define QPAC_KEY_RMP3_VHF1 32
#define QPAC_KEY_RMP3_VHF2 33
#define QPAC_KEY_RMP3_FREQ_DOWN_LRG 34
#define QPAC_KEY_RMP3_FREQ_DOWN_SML 35
#define QPAC_KEY_RMP3_FREQ_UP_LRG 36
#define QPAC_KEY_RMP3_FREQ_UP_SML 37
#define QPAC_KEY_RMP3_SWAP 38
#define QPAC_KEY_CAB_VS_UP 39
#define QPAC_KEY_CAB_VS_DOWN 40
#define QPAC_KEY_CHRONO_CAPT 41
#define QPAC_KEY_CHRONO_FO 42

// MCDU1 KEYS
#define QPAC_KEY_MDCU1_INIT       43
#define QPAC_KEY_MDCU1_DATA       44
#define QPAC_KEY_MDCU1_MENU       45
#define QPAC_KEY_MDCU1_PERF       46
#define QPAC_KEY_MDCU1_PROG       47
#define QPAC_KEY_MDCU1_FPLN       48
#define QPAC_KEY_MDCU1_DIR_TO     49
#define QPAC_KEY_MDCU1_RAD_NAV    50
#define QPAC_KEY_MDCU1_AIRPORT    51
#define QPAC_KEY_MDCU1_SLEW_UP    52
#define QPAC_KEY_MDCU1_SLEW_DOWN  53
#define QPAC_KEY_MDCU1_SLEW_LEFT  54
#define QPAC_KEY_MDCU1_SLEW_RIGHT 55
#define QPAC_KEY_MDCU1_LSK1L      56
#define QPAC_KEY_MDCU1_LSK2L      57
#define QPAC_KEY_MDCU1_LSK3L      58
#define QPAC_KEY_MDCU1_LSK4L      59
#define QPAC_KEY_MDCU1_LSK5L      60
#define QPAC_KEY_MDCU1_LSK6L      61
#define QPAC_KEY_MDCU1_LSK1R      62
#define QPAC_KEY_MDCU1_LSK2R      63
#define QPAC_KEY_MDCU1_LSK3R      64
#define QPAC_KEY_MDCU1_LSK4R      65
#define QPAC_KEY_MDCU1_LSK5R      66
#define QPAC_KEY_MDCU1_LSK6R      67
#define QPAC_KEY_MDCU1_DEL        68
#define QPAC_KEY_MDCU1_SPACE      69
#define QPAC_KEY_MDCU1_OVERFL     70
#define QPAC_KEY_MDCU1_PLUS_M     71
#define QPAC_KEY_MDCU1_DOT        72
#define QPAC_KEY_MDCU1_SLASH      73
#define QPAC_KEY_MDCU1_0          74
#define QPAC_KEY_MDCU1_1          75
#define QPAC_KEY_MDCU1_2          76
#define QPAC_KEY_MDCU1_3          77
#define QPAC_KEY_MDCU1_4          78
#define QPAC_KEY_MDCU1_5          79
#define QPAC_KEY_MDCU1_6          80
#define QPAC_KEY_MDCU1_7          81
#define QPAC_KEY_MDCU1_8          82
#define QPAC_KEY_MDCU1_9          83
#define QPAC_KEY_MDCU1_A          84
#define QPAC_KEY_MDCU1_B          85
#define QPAC_KEY_MDCU1_C          86
#define QPAC_KEY_MDCU1_D          87
#define QPAC_KEY_MDCU1_E          88
#define QPAC_KEY_MDCU1_F          89
#define QPAC_KEY_MDCU1_G          90
#define QPAC_KEY_MDCU1_H          91
#define QPAC_KEY_MDCU1_I          92
#define QPAC_KEY_MDCU1_J          93
#define QPAC_KEY_MDCU1_K          94
#define QPAC_KEY_MDCU1_L          95
#define QPAC_KEY_MDCU1_M          96
#define QPAC_KEY_MDCU1_N          97
#define QPAC_KEY_MDCU1_O          98
#define QPAC_KEY_MDCU1_P          99
#define QPAC_KEY_MDCU1_Q          100
#define QPAC_KEY_MDCU1_R          101
#define QPAC_KEY_MDCU1_S          102
#define QPAC_KEY_MDCU1_T          103
#define QPAC_KEY_MDCU1_U          104
#define QPAC_KEY_MDCU1_V          105
#define QPAC_KEY_MDCU1_W          106
#define QPAC_KEY_MDCU1_X          107
#define QPAC_KEY_MDCU1_Y          108
#define QPAC_KEY_MDCU1_Z          109
// MCDU2 KEYS
#define QPAC_KEY_MDCU2_INIT       110
#define QPAC_KEY_MDCU2_DATA       111
#define QPAC_KEY_MDCU2_MENU       112
#define QPAC_KEY_MDCU2_PERF       113
#define QPAC_KEY_MDCU2_PROG       114
#define QPAC_KEY_MDCU2_FPLN       115
#define QPAC_KEY_MDCU2_DIR_TO     116
#define QPAC_KEY_MDCU2_RAD_NAV    117
#define QPAC_KEY_MDCU2_AIRPORT    118
#define QPAC_KEY_MDCU2_SLEW_UP    119
#define QPAC_KEY_MDCU2_SLEW_DOWN  120
#define QPAC_KEY_MDCU2_SLEW_LEFT  121
#define QPAC_KEY_MDCU2_SLEW_RIGHT 122
#define QPAC_KEY_MDCU2_LSK1L      123
#define QPAC_KEY_MDCU2_LSK2L      124
#define QPAC_KEY_MDCU2_LSK3L      125
#define QPAC_KEY_MDCU2_LSK4L      126
#define QPAC_KEY_MDCU2_LSK5L      127
#define QPAC_KEY_MDCU2_LSK6L      128
#define QPAC_KEY_MDCU2_LSK1R      129
#define QPAC_KEY_MDCU2_LSK2R      130
#define QPAC_KEY_MDCU2_LSK3R      131
#define QPAC_KEY_MDCU2_LSK4R      132
#define QPAC_KEY_MDCU2_LSK5R      133
#define QPAC_KEY_MDCU2_LSK6R      134
#define QPAC_KEY_MDCU2_DEL        135
#define QPAC_KEY_MDCU2_SPACE      136
#define QPAC_KEY_MDCU2_OVERFL     137
#define QPAC_KEY_MDCU2_PLUS_M     138
#define QPAC_KEY_MDCU2_DOT        139
#define QPAC_KEY_MDCU2_SLASH      140
#define QPAC_KEY_MDCU2_0          141
#define QPAC_KEY_MDCU2_1          142
#define QPAC_KEY_MDCU2_2          143
#define QPAC_KEY_MDCU2_3          144
#define QPAC_KEY_MDCU2_4          145
#define QPAC_KEY_MDCU2_5          146
#define QPAC_KEY_MDCU2_6          147
#define QPAC_KEY_MDCU2_7          148
#define QPAC_KEY_MDCU2_8          149
#define QPAC_KEY_MDCU2_9          150
#define QPAC_KEY_MDCU2_A          151
#define QPAC_KEY_MDCU2_B          152
#define QPAC_KEY_MDCU2_C          153
#define QPAC_KEY_MDCU2_D          154
#define QPAC_KEY_MDCU2_E          155
#define QPAC_KEY_MDCU2_F          156
#define QPAC_KEY_MDCU2_G          157
#define QPAC_KEY_MDCU2_H          158
#define QPAC_KEY_MDCU2_I          159
#define QPAC_KEY_MDCU2_J          160
#define QPAC_KEY_MDCU2_K          161
#define QPAC_KEY_MDCU2_L          162
#define QPAC_KEY_MDCU2_M          163
#define QPAC_KEY_MDCU2_N          164
#define QPAC_KEY_MDCU2_O          165
#define QPAC_KEY_MDCU2_P          166
#define QPAC_KEY_MDCU2_Q          167
#define QPAC_KEY_MDCU2_R          168
#define QPAC_KEY_MDCU2_S          169
#define QPAC_KEY_MDCU2_T          170
#define QPAC_KEY_MDCU2_U          171
#define QPAC_KEY_MDCU2_V          172
#define QPAC_KEY_MDCU2_W          173
#define QPAC_KEY_MDCU2_X          174
#define QPAC_KEY_MDCU2_Y          175
#define QPAC_KEY_MDCU2_Z          176
// Commands on airbus_qpac/.. dataref tree
#define QPAC_KEY_AP1_PUSH         177
#define QPAC_KEY_AP2_PUSH         178
#define QPAC_KEY_FD1_PUSH         179
#define QPAC_KEY_FD2_PUSH         180
// END
#define QPAC_KEY_MAX              181

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
// Compass
extern XPLMDataRef qpac_true_mag;
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
extern XPLMDataRef qpac_vfe_next;
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
extern XPLMDataRef qpac_left_brake_release;
extern XPLMDataRef qpac_right_brake_release;
extern XPLMDataRef qpac_nw_anti_skid;
// Gears indicators
extern XPLMDataRef qpac_left_gear_ind;
extern XPLMDataRef qpac_nose_gear_ind;
extern XPLMDataRef qpac_right_gear_ind;
// Triple brake indicator
extern XPLMDataRef qpac_brake_accu;
extern XPLMDataRef qpac_tot_right_brake;
extern XPLMDataRef qpac_tot_left_brake;
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
// ENG lower ECAM (ignition and nacelles)
extern XPLMDataRef qpac_eng_mode_switch;
extern XPLMDataRef qpac_ewd_start_mode;
extern XPLMDataRef qpac_start_valve_array;
extern XPLMDataRef qpac_nacelle_temp_array;
// COND
extern XPLMDataRef qpac_cond_hot_air_valve;
extern XPLMDataRef qpac_cond_cockpit_trim;
extern XPLMDataRef qpac_cond_zone1_trim;
extern XPLMDataRef qpac_cond_zone2_trim;
extern XPLMDataRef qpac_cond_cockpit_temp;
extern XPLMDataRef qpac_cond_aft_cabin_temp;
extern XPLMDataRef qpac_cond_fwd_cabin_temp;
// Doors - Oxygen
extern XPLMDataRef qpac_door_pax_array;
extern XPLMDataRef qpac_door_cargo_array;
extern XPLMDataRef qpac_door_bulk_door;
extern XPLMDataRef qpac_crew_oxy_mask;
// Bleed
extern XPLMDataRef qpac_bleed_intercon;
extern XPLMDataRef qpac_bleed_x;
extern XPLMDataRef qpac_bleed_apu;
extern XPLMDataRef qpac_bleed_eng1;
extern XPLMDataRef qpac_bleed_eng2;
extern XPLMDataRef qpac_bleed_eng1_hp;
extern XPLMDataRef qpac_bleed_eng2_hp;
extern XPLMDataRef qpac_bleed_pack1_fcv;
extern XPLMDataRef qpac_bleed_pack2_fcv;
extern XPLMDataRef qpac_bleed_pack1_flow;
extern XPLMDataRef qpac_bleed_pack2_flow;
extern XPLMDataRef qpac_bleed_pack1_temp;
extern XPLMDataRef qpac_bleed_pack2_temp;
extern XPLMDataRef qpac_bleed_ram_air;
extern XPLMDataRef qpac_bleed_ram_air_valve;
extern XPLMDataRef qpac_bleed_left_press;
extern XPLMDataRef qpac_bleed_right_press;
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
extern XPLMDataRef qpac_elec_battery_volt;
// FUEL
extern XPLMDataRef qpac_fuel_pump_array;
extern XPLMDataRef qpac_fuel_xfv_array ;
extern XPLMDataRef qpac_fuel_eng_lp_valve_array;
extern XPLMDataRef qpac_fuel_tv_array;
// ECAM SD page selection
extern XPLMDataRef qpac_sd_page;
extern XPLMDataRef qpac_clear_illuminated;
extern XPLMDataRef qpac_sd_eng;
extern XPLMDataRef qpac_sd_bleed;
extern XPLMDataRef qpac_sd_press;
extern XPLMDataRef qpac_sd_elec;
extern XPLMDataRef qpac_sd_hyd;
extern XPLMDataRef qpac_sd_fuel;
extern XPLMDataRef qpac_sd_apu;
extern XPLMDataRef qpac_sd_cond;
extern XPLMDataRef qpac_sd_door;
extern XPLMDataRef qpac_sd_wheel;
extern XPLMDataRef qpac_sd_fctl;
extern XPLMDataRef qpac_sd_status;

// ECAM SD lines (a,b,g,r,w)
#define QPAC_SD_LINES 18
extern XPLMDataRef qpac_sd_line_amber[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_blue[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_green[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_red[QPAC_SD_LINES];
extern XPLMDataRef qpac_sd_line_white[QPAC_SD_LINES];

// Anti-ice
extern XPLMDataRef qpac_wing_anti_ice;
extern XPLMDataRef qpac_wing_anti_ice_lights;
extern XPLMDataRef qpac_eng1_anti_ice;
extern XPLMDataRef qpac_eng1_anti_ice_lights;
extern XPLMDataRef qpac_eng2_anti_ice;
extern XPLMDataRef qpac_eng2_anti_ice_lights;

//qpac FCU toggles, push/pull commands, RMP, MCDU
extern XPLMCommandRef qpac_command[QPAC_KEY_MAX];

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
