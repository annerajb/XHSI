#ifndef COMMANDS_H_
#define COMMANDS_H_

// commands that will be used by the writeDataRef
extern XPLMCommandRef nav1_standy_flip;
extern XPLMCommandRef nav2_standy_flip;
extern XPLMCommandRef com1_standy_flip;
extern XPLMCommandRef com2_standy_flip;
extern XPLMCommandRef adf1_standy_flip;
extern XPLMCommandRef adf2_standy_flip;
extern XPLMCommandRef sim_transponder_transponder_ident;
extern XPLMCommandRef stby_com1_coarse_down;
extern XPLMCommandRef stby_com1_coarse_up;
extern XPLMCommandRef stby_com1_fine_down_833;
extern XPLMCommandRef stby_com1_fine_up_833;
extern XPLMCommandRef stby_com2_coarse_down;
extern XPLMCommandRef stby_com2_coarse_up;
extern XPLMCommandRef stby_com2_fine_down_833;
extern XPLMCommandRef stby_com2_fine_up_833;
extern XPLMCommandRef stby_nav1_coarse_down;
extern XPLMCommandRef stby_nav1_coarse_up;
extern XPLMCommandRef stby_nav1_fine_down;
extern XPLMCommandRef stby_nav1_fine_up;
extern XPLMCommandRef stby_nav2_coarse_down;
extern XPLMCommandRef stby_nav2_coarse_up;
extern XPLMCommandRef stby_nav2_fine_down;
extern XPLMCommandRef stby_nav2_fine_up;

extern XPLMCommandRef sim_autopilot_fdir_servos_toggle;
extern XPLMCommandRef sim_autopilot_autothrottle_toggle;
extern XPLMCommandRef sim_autopilot_level_change;
extern XPLMCommandRef sim_autopilot_heading;
extern XPLMCommandRef sim_autopilot_vertical_speed;
extern XPLMCommandRef sim_autopilot_nav;
extern XPLMCommandRef sim_autopilot_approach;
extern XPLMCommandRef sim_autopilot_glide_slope;
extern XPLMCommandRef sim_autopilot_back_course;
extern XPLMCommandRef sim_autopilot_altitude_hold;
extern XPLMCommandRef sim_autopilot_wing_leveler;

extern XPLMCommandRef sim_lights_nav_lights_toggle;
extern XPLMCommandRef sim_lights_beacon_lights_toggle;
extern XPLMCommandRef sim_lights_taxi_lights_toggle;
extern XPLMCommandRef sim_lights_strobe_lights_toggle;
extern XPLMCommandRef sim_lights_landing_lights_toggle;

extern XPLMCommandRef sim_instruments_timer_show_date;

extern XPLMCommandRef sim_flight_controls_flaps_down;
extern XPLMCommandRef sim_flight_controls_flaps_up;
extern XPLMCommandRef sim_flight_controls_landing_gear_toggle;
extern XPLMCommandRef sim_flight_controls_landing_gear_up;
extern XPLMCommandRef sim_flight_controls_landing_gear_down;
extern XPLMCommandRef sim_flight_controls_speed_brakes_down_one;
extern XPLMCommandRef sim_flight_controls_speed_brakes_up_one;

extern XPLMCommandRef x737_cmda_toggle;
extern XPLMCommandRef x737_mcpspd_toggle;
extern XPLMCommandRef x737_lvlchange_toggle;
extern XPLMCommandRef x737_hdgsel_toggle;
extern XPLMCommandRef x737_lnav_toggle;
extern XPLMCommandRef x737_vorloc_toggle;
extern XPLMCommandRef x737_app_toggle;
extern XPLMCommandRef x737_althld_toggle;
extern XPLMCommandRef x737_vs_toggle;

extern XPLMCommandRef chr_start_stop_reset;
extern XPLMCommandRef chr_start_stop;
extern XPLMCommandRef chr_reset;

extern XPLMCommandRef et_reset;
extern XPLMCommandRef et_stop;
extern XPLMCommandRef et_run;

extern XPLMCommandRef clock_utc_gps;
extern XPLMCommandRef clock_utc_int;
extern XPLMCommandRef clock_utc_set;

extern XPLMCommandRef pilot_chrono_stop_reset;
extern XPLMCommandRef pilot_chrono_start_stop;
extern XPLMCommandRef pilot_chrono_reset;

extern XPLMCommandRef copilot_chrono_stop_reset;
extern XPLMCommandRef copilot_chrono_start_stop;
extern XPLMCommandRef copilot_chrono_reset;


extern XPLMCommandRef sim_annunciator_clear_master_warning;
extern XPLMCommandRef sim_annunciator_clear_master_caution;
extern XPLMCommandRef sim_annunciator_clear_master_accept;

// X-Plane Legacy FMS commands

// FMC1 KEYS
#define SIM_FMS1_LS_1L      0
#define SIM_FMS1_LS_2L      1
#define SIM_FMS1_LS_3L      2
#define SIM_FMS1_LS_4L      3
#define SIM_FMS1_LS_5L      4
#define SIM_FMS1_LS_6L      5
#define SIM_FMS1_LS_1R      6
#define SIM_FMS1_LS_2R      7
#define SIM_FMS1_LS_3R      8
#define SIM_FMS1_LS_4R      9
#define SIM_FMS1_LS_5R      10
#define SIM_FMS1_LS_6R      11

#define SIM_FMS1_INIT       12
#define SIM_FMS1_RTE        13
#define SIM_FMS1_DEP_ARR    14
#define SIM_FMS1_AP         15
#define SIM_FMS1_NAVRAD     16

#define SIM_FMS1_FIX        17
#define SIM_FMS1_LEGS       18
#define SIM_FMS1_HOLD       19
#define SIM_FMS1_PERF       20
#define SIM_FMS1_PROG       21
#define SIM_FMS1_EXEC       22
#define SIM_FMS1_DIR_INTC   23

#define SIM_FMS1_PREV_PAGE  25
#define SIM_FMS1_NEXT_PAGE  26

#define SIM_FMS1_A          27
#define SIM_FMS1_B          28
#define SIM_FMS1_C          29
#define SIM_FMS1_D          30
#define SIM_FMS1_E          31
#define SIM_FMS1_F          32
#define SIM_FMS1_G          33
#define SIM_FMS1_H          34
#define SIM_FMS1_I          35
#define SIM_FMS1_J          36
#define SIM_FMS1_K          37
#define SIM_FMS1_L          38
#define SIM_FMS1_M          39
#define SIM_FMS1_N          40
#define SIM_FMS1_O          41
#define SIM_FMS1_P          42
#define SIM_FMS1_Q          43
#define SIM_FMS1_R          44
#define SIM_FMS1_S          45
#define SIM_FMS1_T          46
#define SIM_FMS1_U          47
#define SIM_FMS1_V          48
#define SIM_FMS1_W          49
#define SIM_FMS1_X          50
#define SIM_FMS1_Y          51
#define SIM_FMS1_Z          52

#define SIM_FMS1_BACK       53
#define SIM_FMS1_DEL        54
#define SIM_FMS1_SLASH      55

#define SIM_FMS1_0          56
#define SIM_FMS1_1          57
#define SIM_FMS1_2          58
#define SIM_FMS1_3          59
#define SIM_FMS1_4          60
#define SIM_FMS1_5          61
#define SIM_FMS1_6          62
#define SIM_FMS1_7          63
#define SIM_FMS1_8          64
#define SIM_FMS1_9          65
#define SIM_FMS1_DOT        66
#define SIM_FMS1_KEY_CLR    67
#define SIM_FMS1_PLUS_M     68
#define SIM_FMS1_SPACE      69

#define SIM_FMS1_CLB        70
#define SIM_FMS1_CRZ        71
#define SIM_FMS1_DES        72
#define SIM_FMS1_FMC_COMM   73
#define SIM_FMS1_ATC        74
#define SIM_FMS1_BRT        75

#define SIM_FMS1_CDU_POPUP  76
#define SIM_FMS1_CDU_POPOUT 77

// Exists only for FMS1
#define SIM_FMS1_CLEAR      76
#define SIM_FMS1_DIRECT     77
#define SIM_FMS1_SIGN       78
#define SIM_FMS1_TYPE_APT   79
#define SIM_FMS1_TYPE_VOR   80
#define SIM_FMS1_TYPE_NDB   81
#define SIM_FMS1_TYPE_FIX   82
#define SIM_FMS1_TYPE_LATLON 83
#define SIM_FMS1_FIX_NEXT   84
#define SIM_FMS1_FIX_PREV   85
#define SIM_FMS1_KEY_LOAD   86
#define SIM_FMS1_KEY_SAVE   87

// FMC2 KEYS
#define SIM_FMS2_LS_1L      90
#define SIM_FMS2_LS_2L      91
#define SIM_FMS2_LS_3L      92
#define SIM_FMS2_LS_4L      93
#define SIM_FMS2_LS_5L      94
#define SIM_FMS2_LS_6L      95
#define SIM_FMS2_LS_1R      96
#define SIM_FMS2_LS_2R      97
#define SIM_FMS2_LS_3R      98
#define SIM_FMS2_LS_4R      99
#define SIM_FMS2_LS_5R      100
#define SIM_FMS2_LS_6R      101

#define SIM_FMS2_INIT       102
#define SIM_FMS2_RTE        103
#define SIM_FMS2_DEP_ARR    104
#define SIM_FMS2_AP         105
#define SIM_FMS2_NAVRAD     106

#define SIM_FMS2_FIX        107
#define SIM_FMS2_LEGS       108
#define SIM_FMS2_HOLD       109
#define SIM_FMS2_PERF       110
#define SIM_FMS2_PROG       111
#define SIM_FMS2_EXEC       112
#define SIM_FMS2_DIR_INTC   113

#define SIM_FMS2_PREV_PAGE  115
#define SIM_FMS2_NEXT_PAGE  116

#define SIM_FMS2_A          117
#define SIM_FMS2_B          118
#define SIM_FMS2_C          119
#define SIM_FMS2_D          120
#define SIM_FMS2_E          121
#define SIM_FMS2_F          122
#define SIM_FMS2_G          123
#define SIM_FMS2_H          124
#define SIM_FMS2_I          125
#define SIM_FMS2_J          126
#define SIM_FMS2_K          127
#define SIM_FMS2_L          128
#define SIM_FMS2_M          129
#define SIM_FMS2_N          130
#define SIM_FMS2_O          131
#define SIM_FMS2_P          132
#define SIM_FMS2_Q          133
#define SIM_FMS2_R          134
#define SIM_FMS2_S          135
#define SIM_FMS2_T          136
#define SIM_FMS2_U          137
#define SIM_FMS2_V          138
#define SIM_FMS2_W          139
#define SIM_FMS2_X          140
#define SIM_FMS2_Y          141
#define SIM_FMS2_Z          142

#define SIM_FMS2_BACK       143
#define SIM_FMS2_DEL        144
#define SIM_FMS2_SLASH      145

#define SIM_FMS2_0          146
#define SIM_FMS2_1          147
#define SIM_FMS2_2          148
#define SIM_FMS2_3          149
#define SIM_FMS2_4          150
#define SIM_FMS2_5          151
#define SIM_FMS2_6          152
#define SIM_FMS2_7          153
#define SIM_FMS2_8          154
#define SIM_FMS2_9          155
#define SIM_FMS2_DOT        156
#define SIM_FMS2_CLR        157
#define SIM_FMS2_PLUS_M     158
#define SIM_FMS2_SPACE      159

#define SIM_FMS2_CLB        160
#define SIM_FMS2_CRZ        161
#define SIM_FMS2_DES        162
#define SIM_FMS2_FMC_COMM   163
#define SIM_FMS2_ATC        164
#define SIM_FMS2_BRT        165

#define SIM_FMS2_CDU_POPUP  166
#define SIM_FMS2_CDU_POPOUT 167

// END
#define SIM_FMS_KEY_MAX     170

// FMS Keys
extern XPLMCommandRef sim_fms[SIM_FMS_KEY_MAX];

void registerCommands(void);
void unregisterCommands(void);

#endif /* COMMANDS_H_ */
