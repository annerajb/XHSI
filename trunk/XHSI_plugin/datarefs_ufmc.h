/*
 * datarefs_ufmc.h
 *
 *  Created on:
 *      Author:
 */

#ifndef DATAREFS_UFMC_H_
#define DATAREFS_UFMC_H_


#define NUM_UFMC_LINES 14

// global vars

extern XPLMDataRef ufmc_plugin_status;
extern XPLMPluginID ufmcPluginId;

extern XPLMDataRef ufmc_v1;
extern XPLMDataRef ufmc_vr;
extern XPLMDataRef ufmc_v2;
extern XPLMDataRef ufmc_vref;
extern XPLMDataRef ufmc_vf30;
extern XPLMDataRef ufmc_vf40;

// Engine performance
extern XPLMDataRef ufmc_eng_n1_1;
extern XPLMDataRef ufmc_eng_n1_2;
extern XPLMDataRef ufmc_eng_n1_3;
extern XPLMDataRef ufmc_eng_n1_4;

extern XPLMDataRef ufmc_eng_x_1;
extern XPLMDataRef ufmc_eng_x_2;
extern XPLMDataRef ufmc_eng_x_3;
extern XPLMDataRef ufmc_eng_x_4;

extern XPLMDataRef ufmc_eng_z_1;
extern XPLMDataRef ufmc_eng_z_2;
extern XPLMDataRef ufmc_eng_z_3;
extern XPLMDataRef ufmc_eng_z_4;

extern XPLMDataRef ufmc_eng_rev_1;
extern XPLMDataRef ufmc_eng_rev_2;
extern XPLMDataRef ufmc_eng_rev_3;
extern XPLMDataRef ufmc_eng_rev_4;


extern XPLMDataRef ufmc_accel_altitude;
extern XPLMDataRef ufmc_dist_to_tc;
extern XPLMDataRef ufmc_dist_to_td;

extern XPLMDataRef ufmc_flight_phase;
extern XPLMDataRef ufmc_offset_on;
extern XPLMDataRef ufmc_thr_red_altitude;
extern XPLMDataRef ufmc_vdev;
extern XPLMDataRef ufmc_zfw;
extern XPLMDataRef ufmc_zfw_4500;

// Flight Plan
extern XPLMDataRef ufmc_waypt_altitude;
extern XPLMDataRef ufmc_waypt_dme_arc;
extern XPLMDataRef ufmc_waypt_dme_arc_dme;
extern XPLMDataRef ufmc_waypt_dme_arc_side;
extern XPLMDataRef ufmc_waypt_eta;
extern XPLMDataRef ufmc_waypt_fly_over;
extern XPLMDataRef ufmc_waypt_index;
extern XPLMDataRef ufmc_waypt_lat;
extern XPLMDataRef ufmc_waypt_lon;
extern XPLMDataRef ufmc_waypt_name;
extern XPLMDataRef ufmc_waypt_number;
extern XPLMDataRef ufmc_waypt_only_draw;
extern XPLMDataRef ufmc_waypt_pln_index;
extern XPLMDataRef ufmc_waypt_sid_star_app;
extern XPLMDataRef ufmc_waypt_speed;
extern XPLMDataRef ufmc_waypt_speed_tas;
extern XPLMDataRef ufmc_waypt_toc_tod;
extern XPLMDataRef ufmc_waypt_type_altitude;

extern XPLMDataRef ufmc_version;

// CDU Screen
extern XPLMDataRef ufmc_exec_light_on;
extern XPLMDataRef ufmc_line[NUM_UFMC_LINES];


extern int ufmc_ready;

// UFMC CDU KEYS
#define UFMC_KEY_CDU_LSK1L      0
#define UFMC_KEY_CDU_LSK2L      1
#define UFMC_KEY_CDU_LSK3L      2
#define UFMC_KEY_CDU_LSK4L      3
#define UFMC_KEY_CDU_LSK5L      4
#define UFMC_KEY_CDU_LSK6L      5
#define UFMC_KEY_CDU_LSK1R      6
#define UFMC_KEY_CDU_LSK2R      7
#define UFMC_KEY_CDU_LSK3R      8
#define UFMC_KEY_CDU_LSK4R      9
#define UFMC_KEY_CDU_LSK5R      10
#define UFMC_KEY_CDU_LSK6R      11

#define UFMC_KEY_CDU_INIT       12
#define UFMC_KEY_CDU_RTE        13
#define UFMC_KEY_CDU_DEP_ARR    14
#define UFMC_KEY_CDU_AP         15
#define UFMC_KEY_CDU_VNAV       16
#define UFMC_KEY_CDU_BRT        -1
#define UFMC_KEY_CDU_FIX        17
#define UFMC_KEY_CDU_LEGS       18
#define UFMC_KEY_CDU_HOLD       19
#define UFMC_KEY_CDU_PERF       20
#define UFMC_KEY_CDU_PROG       21
#define UFMC_KEY_CDU_EXEC       22
#define UFMC_KEY_CDU_MENU       23
#define UFMC_KEY_CDU_RAD_NAV    24
#define UFMC_KEY_CDU_SLEW_LEFT  25
#define UFMC_KEY_CDU_SLEW_RIGHT 26

#define UFMC_KEY_CDU_A          27
#define UFMC_KEY_CDU_B          28
#define UFMC_KEY_CDU_C          29
#define UFMC_KEY_CDU_D          30
#define UFMC_KEY_CDU_E          31
#define UFMC_KEY_CDU_F          32
#define UFMC_KEY_CDU_G          33
#define UFMC_KEY_CDU_H          34
#define UFMC_KEY_CDU_I          35
#define UFMC_KEY_CDU_J          36
#define UFMC_KEY_CDU_K          37
#define UFMC_KEY_CDU_L          38
#define UFMC_KEY_CDU_M          39
#define UFMC_KEY_CDU_N          40
#define UFMC_KEY_CDU_O          41
#define UFMC_KEY_CDU_P          42
#define UFMC_KEY_CDU_Q          43
#define UFMC_KEY_CDU_R          44
#define UFMC_KEY_CDU_S          45
#define UFMC_KEY_CDU_T          46
#define UFMC_KEY_CDU_U          47
#define UFMC_KEY_CDU_V          48
#define UFMC_KEY_CDU_W          49
#define UFMC_KEY_CDU_X          50
#define UFMC_KEY_CDU_Y          51
#define UFMC_KEY_CDU_Z          52
#define UFMC_KEY_CDU_DEL        54
#define UFMC_KEY_CDU_SLASH      55

#define UFMC_KEY_CDU_0          56
#define UFMC_KEY_CDU_1          57
#define UFMC_KEY_CDU_2          58
#define UFMC_KEY_CDU_3          59
#define UFMC_KEY_CDU_4          60
#define UFMC_KEY_CDU_5          61
#define UFMC_KEY_CDU_6          62
#define UFMC_KEY_CDU_7          63
#define UFMC_KEY_CDU_8          64
#define UFMC_KEY_CDU_9          65
#define UFMC_KEY_CDU_DOT        66
#define UFMC_KEY_CDU_CLR        67
#define UFMC_KEY_CDU_PLUS_M     68

#define UFMC_KEY_CDU_SPACE      69
#define UFMC_KEY_CDU_OVERFL     70

// Unused
#define UFMC_KEY_CDU_DATA       71
#define UFMC_KEY_CDU_FPLN       72
#define UFMC_KEY_CDU_DIR_TO     73
#define UFMC_KEY_CDU_AIRPORT    74
#define UFMC_KEY_CDU_SLEW_UP    75
#define UFMC_KEY_CDU_SLEW_DOWN  76
// END
#define UFMC_KEY_MAX            77

// CDU Keys
extern XPLMDataRef ufmc_keys[UFMC_KEY_MAX];

// global functions
float checkUFMCCallback(float, float, int, void *);
void writeUFmcDataRef(int, float);

#endif /* DATAREFS_UFMC_H_ */
