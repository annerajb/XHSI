#ifndef DATAREFS_Z_737_H_
#define DATAREFS_Z_737_H_

#define Z737_FMC_LINES 6
#define Z737_CDU_LINES 14

#define Z737_CDU_BUF_LEN 80
#define Z737_CDU_LINE_WIDTH 24

// Default rate is 15 Callbacks par second
// Send CDU Message 2 times per second
#define Z737_MAX_MSG_COUNT 7

// global vars

extern XPLMDataRef z737_plugin_status;
extern XPLMPluginID z737_PluginId;

// z737/systems/afds/...
extern XPLMDataRef z737_fdA_status;
extern XPLMDataRef z737_fdB_status;
extern XPLMDataRef z737_CMD_A;
extern XPLMDataRef z737_CMD_B;
extern XPLMDataRef z737_A_PITCH;
extern XPLMDataRef z737_B_PITCH;
extern XPLMDataRef z737_A_ROLL;
extern XPLMDataRef z737_B_ROLL;

// z737/systems/PFD/...
extern XPLMDataRef z737_MCPSPD_mode;
extern XPLMDataRef z737_FMCSPD_mode;
extern XPLMDataRef z737_RETARD_mode;
extern XPLMDataRef z737_THRHLD_mode;
extern XPLMDataRef z737_LNAVARMED_mode;
extern XPLMDataRef z737_VORLOCARMED_mode;
extern XPLMDataRef z737_PITCHSPD_mode;
extern XPLMDataRef z737_ALTHLD_mode;
extern XPLMDataRef z737_VSARMED_mode;
extern XPLMDataRef z737_VS_mode;
extern XPLMDataRef z737_VNAVALT_mode;
extern XPLMDataRef z737_VNAVPATH_mode;
extern XPLMDataRef z737_VNAVSPD_mode;
extern XPLMDataRef z737_GSARMED_mode;
extern XPLMDataRef z737_GS_mode;
extern XPLMDataRef z737_FLAREARMED_mode;
extern XPLMDataRef z737_FLARE_mode;
extern XPLMDataRef z737_TOGA_mode;
extern XPLMDataRef z737_LNAV_mode;
extern XPLMDataRef z737_HDG_mode;
extern XPLMDataRef z737_VORLOC_mode;
extern XPLMDataRef z737_N1_mode;
extern XPLMDataRef z737_PFD_pwr;

// z737/systems/athr/...
extern XPLMDataRef z737_ATHR_armed;
extern XPLMDataRef z737_lvlchange;

// z737/systems/eec/...
extern XPLMDataRef z737_N1_phase;
extern XPLMDataRef z737_N1_limit_eng1;
extern XPLMDataRef z737_N1_limit_eng2;

// z737/systems/electrics/...
extern XPLMDataRef z737_stby_pwr;

//mcp st

extern XPLMDataRef z737_MCPSPD_spd;
extern XPLMDataRef z737_HDG_magnhdg;
extern XPLMDataRef z737_ALTHLD_baroalt;
extern XPLMDataRef z737_VS_vvi;

extern XPLMDataRef z737_left_fixed_land_light_switch;
extern XPLMDataRef z737_right_fixed_land_light_switch;
extern XPLMDataRef z737_left_retr_land_light_switch;
extern XPLMDataRef z737_rigth_retr_land_light_switch;
extern XPLMDataRef z737_taxi_light_switch;
extern XPLMDataRef z737_left_turnoff_light_switch;
extern XPLMDataRef z737_right_turnoff_light_switch;
extern XPLMDataRef z737_position_light_switch;
extern XPLMDataRef z737_beacon_light_switch;

// EFIS
extern XPLMDataRef z737_efis0_nd_range_enum;
extern XPLMDataRef z737_efis0_FPV;
extern XPLMDataRef z737_efis0_MTR;
extern XPLMDataRef z737_efis0_TFC;
extern XPLMDataRef z737_efis0_CTR;
extern XPLMDataRef z737_efis0_WXR;
extern XPLMDataRef z737_efis0_STA;
extern XPLMDataRef z737_efis0_WPT;
extern XPLMDataRef z737_efis0_ARPT;
extern XPLMDataRef z737_efis0_DATA;
extern XPLMDataRef z737_efis0_POS;
extern XPLMDataRef z737_efis0_TERR;
extern XPLMDataRef z737_efis0_DH_source;
extern XPLMDataRef z737_efis0_DH_value;
            
extern XPLMDataRef z737_efis1_nd_range_enum;
extern XPLMDataRef z737_efis1_FPV;
extern XPLMDataRef z737_efis1_MTR;
extern XPLMDataRef z737_efis1_TFC;
extern XPLMDataRef z737_efis1_CTR;
extern XPLMDataRef z737_efis1_WXR;
extern XPLMDataRef z737_efis1_STA;
extern XPLMDataRef z737_efis1_WPT;
extern XPLMDataRef z737_efis1_ARPT;
extern XPLMDataRef z737_efis1_DATA;
extern XPLMDataRef z737_efis1_POS;
extern XPLMDataRef z737_efis1_TERR;
extern XPLMDataRef z737_efis1_DH_source;
extern XPLMDataRef z737_efis1_DH_value;

/*
 * CDU 1
 */
extern XPLMDataRef z737_fmc1_title_white;
extern XPLMDataRef z737_fmc1_title_small;
extern XPLMDataRef z737_fmc1_title_inverted;
extern XPLMDataRef z737_fmc1_title_magenta;
extern XPLMDataRef z737_fmc1_title_green;

extern XPLMDataRef z737_fmc1_label_white[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc1_content_white[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc1_content_small[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc1_content_inverted[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc1_content_green[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc1_content_magenta[Z737_FMC_LINES];

extern XPLMDataRef z737_fmc1_scratch_white;
extern XPLMDataRef z737_fmc1_scratch_inverted;

extern XPLMDataRef z737_fmc_exec_lights;       // laminar/B738/indicators/fmc_exec_lights
extern XPLMDataRef z737_fms_exec_light_pilot;  // laminar/B738/indicators/fms_exec_light_pilot

extern XPLMDataRef z737_fmc_message;      // laminar/B738/fmc/fmc_message
extern XPLMDataRef z737_fmc_message_warn; // laminar/B738/fmc/fmc_message_warn

/*
 *  CDU2
 */
extern XPLMDataRef z737_fmc2_title_white;
extern XPLMDataRef z737_fmc2_title_small;
extern XPLMDataRef z737_fmc2_title_inverted;
extern XPLMDataRef z737_fmc2_title_magenta;
extern XPLMDataRef z737_fmc2_title_green;

extern XPLMDataRef z737_fmc2_label_white[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc2_content_white[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc2_content_small[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc2_content_inverted[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc2_content_green[Z737_FMC_LINES];
extern XPLMDataRef z737_fmc2_content_magenta[Z737_FMC_LINES];

extern XPLMDataRef z737_fmc2_scratch_white;
extern XPLMDataRef z737_fmc2_scratch_inverted;

extern XPLMDataRef z737_fmc_exec_lights_fo;     // laminar/B738/indicators/fmc_exec_lights_fo
extern XPLMDataRef z737_fms_exec_light_copilot; // laminar/B738/indicators/fms_exec_light_copilot

/*
 * FMS
 */
extern XPLMDataRef laminar_B738_fms_legs;           // laminar/B738/fms/legs [String]
extern XPLMDataRef laminar_B738_fms_legs_lat;       // laminar/B738/fms/legs_lat float[128]
extern XPLMDataRef laminar_B738_fms_legs_lon;       // laminar/B738/fms/legs_lon float[128]
extern XPLMDataRef laminar_B738_fms_legs_alt_calc;  // laminar/B738/fms/legs_alt_calc float[128]
extern XPLMDataRef laminar_B738_fms_legs_alt_rest1; // laminar/B738/fms/legs_alt_rest1 float[128]
extern XPLMDataRef laminar_B738_fms_legs_spd;       // laminar/B738/fms/legs_spd float[128]
extern XPLMDataRef laminar_B738_fms_num_of_wpts;    // laminar/B738/fms/num_of_wpts float


extern int z737_ready;
extern int z737_version;
extern int z737_cdu_ready;
extern int z737_msg_count;
extern int z737_fmc_keypressed;


/*
 *  Key codes (values passed with Z737_KEY_PRESS ID)
 */

// FMC1 KEYS
#define Z737_KEY_FMC1_LSK1L      0
#define Z737_KEY_FMC1_LSK2L      1
#define Z737_KEY_FMC1_LSK3L      2
#define Z737_KEY_FMC1_LSK4L      3
#define Z737_KEY_FMC1_LSK5L      4
#define Z737_KEY_FMC1_LSK6L      5
#define Z737_KEY_FMC1_LSK1R      6
#define Z737_KEY_FMC1_LSK2R      7
#define Z737_KEY_FMC1_LSK3R      8
#define Z737_KEY_FMC1_LSK4R      9
#define Z737_KEY_FMC1_LSK5R      10
#define Z737_KEY_FMC1_LSK6R      11

#define Z737_KEY_FMC1_INIT       12
#define Z737_KEY_FMC1_RTE        13
#define Z737_KEY_FMC1_DEP_ARR    14
#define Z737_KEY_FMC1_AP         15
#define Z737_KEY_FMC1_VNAV       16

#define Z737_KEY_FMC1_FIX        17
#define Z737_KEY_FMC1_LEGS       18
#define Z737_KEY_FMC1_HOLD       19
#define Z737_KEY_FMC1_PERF       20
#define Z737_KEY_FMC1_PROG       21
#define Z737_KEY_FMC1_EXEC       22
#define Z737_KEY_FMC1_MENU       23

#define Z737_KEY_FMC1_PREV_PAGE  25
#define Z737_KEY_FMC1_NEXT_PAGE  26

#define Z737_KEY_FMC1_A          27
#define Z737_KEY_FMC1_B          28
#define Z737_KEY_FMC1_C          29
#define Z737_KEY_FMC1_D          30
#define Z737_KEY_FMC1_E          31
#define Z737_KEY_FMC1_F          32
#define Z737_KEY_FMC1_G          33
#define Z737_KEY_FMC1_H          34
#define Z737_KEY_FMC1_I          35
#define Z737_KEY_FMC1_J          36
#define Z737_KEY_FMC1_K          37
#define Z737_KEY_FMC1_L          38
#define Z737_KEY_FMC1_M          39
#define Z737_KEY_FMC1_N          40
#define Z737_KEY_FMC1_O          41
#define Z737_KEY_FMC1_P          42
#define Z737_KEY_FMC1_Q          43
#define Z737_KEY_FMC1_R          44
#define Z737_KEY_FMC1_S          45
#define Z737_KEY_FMC1_T          46
#define Z737_KEY_FMC1_U          47
#define Z737_KEY_FMC1_V          48
#define Z737_KEY_FMC1_W          49
#define Z737_KEY_FMC1_X          50
#define Z737_KEY_FMC1_Y          51
#define Z737_KEY_FMC1_Z          52

#define Z737_KEY_FMC1_DEL        54
#define Z737_KEY_FMC1_SLASH      55

#define Z737_KEY_FMC1_0          56
#define Z737_KEY_FMC1_1          57
#define Z737_KEY_FMC1_2          58
#define Z737_KEY_FMC1_3          59
#define Z737_KEY_FMC1_4          60
#define Z737_KEY_FMC1_5          61
#define Z737_KEY_FMC1_6          62
#define Z737_KEY_FMC1_7          63
#define Z737_KEY_FMC1_8          64
#define Z737_KEY_FMC1_9          65
#define Z737_KEY_FMC1_DOT        66
#define Z737_KEY_FMC1_CLR        67
#define Z737_KEY_FMC1_PLUS_M     68
#define Z737_KEY_FMC1_SPACE      69

#define Z737_KEY_FMC1_CLB        70
#define Z737_KEY_FMC1_CRZ        71
#define Z737_KEY_FMC1_DES        72
#define Z737_KEY_FMC1_FMC_COMM   73
#define Z737_KEY_FMC1_ATC        74
#define Z737_KEY_FMC1_BRT        75


// FMC2 KEYS

// FMC1 KEYS
#define Z737_KEY_FMC2_LSK1L      80
#define Z737_KEY_FMC2_LSK2L      81
#define Z737_KEY_FMC2_LSK3L      82
#define Z737_KEY_FMC2_LSK4L      83
#define Z737_KEY_FMC2_LSK5L      84
#define Z737_KEY_FMC2_LSK6L      85
#define Z737_KEY_FMC2_LSK1R      86
#define Z737_KEY_FMC2_LSK2R      87
#define Z737_KEY_FMC2_LSK3R      88
#define Z737_KEY_FMC2_LSK4R      89
#define Z737_KEY_FMC2_LSK5R      90
#define Z737_KEY_FMC2_LSK6R      91

#define Z737_KEY_FMC2_INIT       92
#define Z737_KEY_FMC2_RTE        93
#define Z737_KEY_FMC2_DEP_ARR    94
#define Z737_KEY_FMC2_AP         95
#define Z737_KEY_FMC2_VNAV       96

#define Z737_KEY_FMC2_FIX        97
#define Z737_KEY_FMC2_LEGS       98
#define Z737_KEY_FMC2_HOLD       99
#define Z737_KEY_FMC2_PERF       100
#define Z737_KEY_FMC2_PROG       101
#define Z737_KEY_FMC2_EXEC       102
#define Z737_KEY_FMC2_MENU       103

#define Z737_KEY_FMC2_PREV_PAGE  105
#define Z737_KEY_FMC2_NEXT_PAGE  106

#define Z737_KEY_FMC2_A          107
#define Z737_KEY_FMC2_B          108
#define Z737_KEY_FMC2_C          109
#define Z737_KEY_FMC2_D          110
#define Z737_KEY_FMC2_E          111
#define Z737_KEY_FMC2_F          112
#define Z737_KEY_FMC2_G          113
#define Z737_KEY_FMC2_H          114
#define Z737_KEY_FMC2_I          115
#define Z737_KEY_FMC2_J          116
#define Z737_KEY_FMC2_K          117
#define Z737_KEY_FMC2_L          118
#define Z737_KEY_FMC2_M          119
#define Z737_KEY_FMC2_N          120
#define Z737_KEY_FMC2_O          121
#define Z737_KEY_FMC2_P          122
#define Z737_KEY_FMC2_Q          123
#define Z737_KEY_FMC2_R          124
#define Z737_KEY_FMC2_S          125
#define Z737_KEY_FMC2_T          126
#define Z737_KEY_FMC2_U          127
#define Z737_KEY_FMC2_V          128
#define Z737_KEY_FMC2_W          129
#define Z737_KEY_FMC2_X          130
#define Z737_KEY_FMC2_Y          131
#define Z737_KEY_FMC2_Z          132

#define Z737_KEY_FMC2_DEL        134
#define Z737_KEY_FMC2_SLASH      135

#define Z737_KEY_FMC2_0          136
#define Z737_KEY_FMC2_1          137
#define Z737_KEY_FMC2_2          138
#define Z737_KEY_FMC2_3          139
#define Z737_KEY_FMC2_4          140
#define Z737_KEY_FMC2_5          141
#define Z737_KEY_FMC2_6          142
#define Z737_KEY_FMC2_7          143
#define Z737_KEY_FMC2_8          144
#define Z737_KEY_FMC2_9          145
#define Z737_KEY_FMC2_DOT        146
#define Z737_KEY_FMC2_CLR        147
#define Z737_KEY_FMC2_PLUS_M     148
#define Z737_KEY_FMC2_SPACE      149

#define Z737_KEY_FMC2_CLB        150
#define Z737_KEY_FMC2_CRZ        151
#define Z737_KEY_FMC2_DES        152
#define Z737_KEY_FMC2_FMC_COMM   153
#define Z737_KEY_FMC2_ATC        154
#define Z737_KEY_FMC2_BRT        155

// END
#define Z737_KEY_MAX              160

// CDU Keys
extern XPLMCommandRef z737_command[Z737_KEY_MAX];

// global functions
float	checkZibo737Callback(float, float, int, void *);
float	sendZibo737MsgCallback(float, float, int, void *);

void writeZibo737DataRef(int, float);

#endif /* DATAREFS_Z_737_H_ */
