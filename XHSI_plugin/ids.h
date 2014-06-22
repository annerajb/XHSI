
// Sim value ids ===============================================================

// Aircraft position
#define SIM_FLIGHTMODEL_POSITION_GROUNDSPEED 0
#define SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED 1
#define SIM_FLIGHTMODEL_POSITION_MAGPSI 2
#define SIM_FLIGHTMODEL_POSITION_HPATH 3
#define SIM_FLIGHTMODEL_POSITION_LATITUDE 4		// double!
#define SIM_FLIGHTMODEL_POSITION_LONGITUDE 5 	// double!
#define SIM_FLIGHTMODEL_POSITION_PHI 6 			// roll angle
#define SIM_FLIGHTMODEL_POSITION_R 7 			// rotation rate
#define SIM_FLIGHTMODEL_POSITION_MAGVAR 8
#define SIM_FLIGHTMODEL_POSITION_ELEVATION 9
#define SIM_FLIGHTMODEL_POSITION_Y_AGL 10
#define SIM_FLIGHTMODEL_POSITION_THETA 11       // pitch
#define SIM_FLIGHTMODEL_POSITION_VPATH 12       // fpa
#define SIM_FLIGHTMODEL_POSITION_ALPHA 13       // aoa
#define SIM_FLIGHTMODEL_POSITION_BETA 14        // yaw ( = slip or drift ? )
#define SIM_FLIGHTMODEL_FAILURES_ONGROUND_ANY 15 // It was misplaced and is not really a failure, you can use that to indicate when the wheels are on the ground
#define XHSI_FLIGHTMODEL_POSITION_NEAREST_ARPT 10016 // Special case : 4 char


// Instruments
//#define SIM_FLIGHTMODEL_POSITION_VH_IND_FPM 50
//#define SIM_FLIGHTMODEL_MISC_H_IND 51
#define SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_PILOT 52
#define SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_COPILOT 53
#define SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_PILOT 54
#define SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_COPILOT 55
#define SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_PILOT 56
#define SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_COPILOT 57
#define SIM_COCKPIT2_GAUGES_INDICATORS_SIDESLIP_DEGREES 58
#define SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_PILOT 59
#define SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_COPILOT 60
#define SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT 61
#define SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT 62
#define SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_ACCELERATION 63
#define XHSI_EFIS_PILOT_DA_BUG 64
#define XHSI_EFIS_PILOT_MINS_MODE 65
#define XHSI_EFIS_COPILOT_DA_BUG 66
#define XHSI_EFIS_COPILOT_MINS_MODE 67
#define SIM_GAUGES_FAILURES_PILOT 68
#define SIM_GAUGES_FAILURES_COPILOT 69

// Electrical
#define SIM_COCKPIT_ELECTRICAL_AVIONICS_ON 80
#define SIM_COCKPIT_ELECTRICAL_BATTERY_ON 81
#define SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON 82


// General instrument style
#define XHSI_STYLE 99
	
	
// Radios
#define SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ 100
#define SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ 101
#define SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ 102
#define SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ 103
#define SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT 104
#define SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT 105
#define SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT 106
#define SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT 107
#define SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M 108
#define SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M 109
#define SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M 110
#define SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M 111
#define SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM 112
#define SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM 113
#define SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM 114
#define SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM 115
#define SIM_COCKPIT_RADIOS_NAV1_CDI 116
#define SIM_COCKPIT_RADIOS_NAV2_CDI 117
#define SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT 118
#define SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT 119
#define SIM_COCKPIT_RADIOS_NAV1_FROMTO 120
#define SIM_COCKPIT_RADIOS_NAV2_FROMTO 121
#define SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT 122
#define SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT 123

#define SIM_COCKPIT_RADIOS_GPS_DIR_DEGT 124
#define SIM_COCKPIT_RADIOS_GPS_DME_DIST_M 125
#define SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM 126
#define SIM_COCKPIT_RADIOS_GPS_HDEF_DOT 127
#define SIM_COCKPIT_RADIOS_GPS_FROMTO 128
#define SIM_COCKPIT_RADIOS_GPS_VDEF_DOT 129

#define SIM_COCKPIT_RADIOS_NAV1_DME_TIME_SECS 130
#define SIM_COCKPIT_RADIOS_NAV2_DME_TIME_SECS 131
#define SIM_COCKPIT_RADIOS_GPS_DME_TIME_SECS 132

#define SIM_COCKPIT2_RADIOS_INDICATORS_OUTER_MARKER_LIT 133
#define SIM_COCKPIT2_RADIOS_INDICATORS_MIDDLE_MARKER_LIT 134
#define SIM_COCKPIT2_RADIOS_INDICATORS_INNER_MARKER_LIT 135

#define SIM_COCKPIT_RADIOS_NAV1_STDBY_FREQ_HZ 136
#define SIM_COCKPIT_RADIOS_NAV2_STDBY_FREQ_HZ 137
#define SIM_COCKPIT_RADIOS_ADF1_STDBY_FREQ_HZ 138
#define SIM_COCKPIT_RADIOS_ADF2_STDBY_FREQ_HZ 139

#define SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID 10140
#define SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID 10141
#define SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID 10142
#define SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID 10143

#define SIM_COCKPIT_RADIOS_COM1_FREQ_HZ 144
#define SIM_COCKPIT_RADIOS_COM1_STDBY_FREQ_HZ 145
#define SIM_COCKPIT_RADIOS_COM2_FREQ_HZ 146
#define SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ 147
#define SIM_COCKPIT_RADIOS_STDBY_FLIP 148 // for incoming commands only


// AP
#define SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE 150
#define SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY 151
#define SIM_COCKPIT_AUTOPILOT_ALTITUDE 152
#define SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR 153
#define SIM_COCKPIT_AUTOPILOT_HEADING_MAG 154
#define SIM_COCKPIT_AUTOPILOT_AIRSPEED 155
#define SIM_COCKPIT_AUTOPILOT_AIRSPEED_IS_MACH 156
#define SIM_COCKPIT_AUTOPILOT_FD_PITCH 157
#define SIM_COCKPIT_AUTOPILOT_FD_ROLL 158
#define SIM_COCKPIT_AUTOPILOT_MODE 159
#define SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ENABLED 160
#define SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ON 161
#define SIM_COCKPIT2_AUTOPILOT_HEADING_STATUS 162
#define SIM_COCKPIT2_AUTOPILOT_NAV_STATUS 163
#define SIM_COCKPIT2_AUTOPILOT_VVI_STATUS 164
#define SIM_COCKPIT2_AUTOPILOT_SPEED_STATUS 165
#define SIM_COCKPIT2_AUTOPILOT_ALTITUDE_HOLD_STATUS 166
#define SIM_COCKPIT2_AUTOPILOT_GLIDESLOPE_STATUS 167
#define SIM_COCKPIT2_AUTOPILOT_VNAV_STATUS 168
#define SIM_COCKPIT2_AUTOPILOT_TOGA_STATUS 169
#define SIM_COCKPIT2_AUTOPILOT_TOGA_LATERAL_STATUS 170
#define SIM_COCKPIT2_AUTOPILOT_ROLL_STATUS 171
#define SIM_COCKPIT2_AUTOPILOT_PITCH_STATUS 172
#define SIM_COCKPIT2_AUTOPILOT_BACKCOURSE_STATUS 173
#define SIM_COCKPIT_AUTOPILOT_KEY_PRESS 174 // for incoming commands only
#define SIM_COCKPIT_AUTOPILOT_HEADING_ROLL_MODE 175

// Transponder
#define SIM_COCKPIT_RADIOS_TRANSPONDER_MODE 180
#define SIM_COCKPIT_RADIOS_TRANSPONDER_CODE 181


// Clock mode
#define SIM_COCKPIT2_CLOCK_TIMER_MODE 190


// EFIS
#define SIM_COCKPIT_SWITCHES_HSI_SELECTOR 200
#define SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR 201
#define SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR 202
#define SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR 203
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER 204
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS 205
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS 206
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS 207
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS 208
#define SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS 209
#define SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE 210
#define SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE 211
#define XHSI_EFIS_PILOT_STA 212
#define XHSI_EFIS_PILOT_DATA 213
#define XHSI_EFIS_PILOT_POS 214
#define XHSI_EFIS_PILOT_MAP_ZOOMIN 215


// Copilot EFIS
#define XHSI_EFIS_COPILOT_HSI_SOURCE 250
#define XHSI_EFIS_COPILOT_MAP_RANGE 251
#define XHSI_EFIS_COPILOT_RADIO1 252
#define XHSI_EFIS_COPILOT_RADIO2 253
#define XHSI_EFIS_COPILOT_WXR 254
#define XHSI_EFIS_COPILOT_TFC 255
#define XHSI_EFIS_COPILOT_ARPT 256
#define XHSI_EFIS_COPILOT_WPT 257
#define XHSI_EFIS_COPILOT_VOR 258
#define XHSI_EFIS_COPILOT_NDB 259
#define XHSI_EFIS_COPILOT_MAP_CTR 260
#define XHSI_EFIS_COPILOT_MAP_MODE 261
#define XHSI_EFIS_COPILOT_STA 262
#define XHSI_EFIS_COPILOT_DATA 263
#define XHSI_EFIS_COPILOT_POS 264
#define XHSI_EFIS_COPILOT_MAP_ZOOMIN 265

// EICAS
#define XHSI_ENGINE_TYPE 280
#define XHSI_EICAS_TRQ_SCALE 281
#define XHSI_FUEL_UNITS 282

// MFD
#define XHSI_MFD_MODE 290


// Environment
#define SIM_WEATHER_WIND_SPEED_KT 300
#define SIM_WEATHER_WIND_DIRECTION_DEGT 301
#define SIM_TIME_ZULU_TIME_SEC 302
#define SIM_TIME_LOCAL_TIME_SEC 303
#define SIM_WEATHER_TEMPERATURE_AMBIENT_C 304
#define SIM_WEATHER_SPEED_SOUND_MS 305
// Timers
#define SIM_TIME_TIMER_IS_RUNNING_SEC 306
#define SIM_TIME_TIMER_ELAPSED_TIME_SEC 307
#define SIM_TIME_TOTAL_FLIGHT_TIME_SEC 308


// Aircraft constants
#define SIM_AIRCRAFT_VIEW_ACF_VSO 320
#define SIM_AIRCRAFT_VIEW_ACF_VS 321
#define SIM_AIRCRAFT_VIEW_ACF_VFE 322
#define SIM_AIRCRAFT_VIEW_ACF_VNO 323
#define SIM_AIRCRAFT_VIEW_ACF_VNE 324
#define SIM_AIRCRAFT_VIEW_ACF_MMO 325
#define SIM_AIRCRAFT_OVERFLOW_ACF_VLE 326


// Controls & annunciators
#define SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION 350
#define SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING 351
#define SIM_COCKPIT2_CONTROLS_GEAR_HANDLE_DOWN 352
#define SIM_COCKPIT2_ANNUNCIATORS_GEAR_UNSAFE 353
#define XHSI_AIRCRAFT_GEAR_COUNT 354 // Calculated from sim/aircraft/parts/acf_gear_type int[10]
#define SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO 355
#define SIM_COCKPIT_LIGHTS 356 // bit-fields: taxi_light_on/strobe_lights_on/nav_lights_on/landing_lights_on/beacon_lights_on
#define SIM_COCKPIT2_CONTROLS_FLAP_RATIO 357 // this is supposed to be the handle location
#define SIM_COCKPIT2_CONTROLS_FLAP_HANDLE_DEPLOY_RATIO 358 // this is supposed to be the indicator
#define SIM_AIRCRAFT_CONTROLS_ACF_FLAP_DETENTS 359
#define SIM_COCKPIT2_ANNUNCIATORS_AUTOPILOT_DISCONNECT 360
#define SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY 361
#define SIM_COCKPIT2_ANNUNCIATORS_GPWS 362
#define SIM_COCKPIT2_ANNUNCIATORS_ICE 363
#define SIM_COCKPIT2_ANNUNCIATORS_PITOT_HEAT 364
#define SIM_COCKPIT2_ANNUNCIATORS_STALL_WARNING 365
#define SIM_COCKPIT2_ANNUNCIATORS_GEAR_WARNING 366
#define SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL 367
#define SIM_COCKPIT2_CONTROLS_SPEEDBRAKE_RATIO 368
#define SIM_FLIGHTMODEL2_CONTROLS_SPEEDBRAKE_RATIO 369
#define SIM_FLIGHTMODEL2_GEAR_DEPLOY_RATIO_ 370 // array of 10 floats
#define SIM_COCKPIT2_CONTROLS_YOKE_PITCH_RATIO 390
#define SIM_COCKPIT2_CONTROLS_YOKE_ROLL_RATIO 391


// Fuel, engines, etc...
// ids that end with an underscore represent arrays of up to 8 floats
#define SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS 400
#define SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES 401
#define SIM_COCKPIT2_ANNUNCIATORS_REVERSER_DEPLOYED 402
#define SIM_COCKPIT2_ANNUNCIATORS_OIL_PRESSURE 403
#define SIM_COCKPIT2_ANNUNCIATORS_OIL_TEMPERATURE 404
#define SIM_COCKPIT2_ANNUNCIATORS_FUEL_PRESSURE 405
#define SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL 406
#define SIM_AIRCRAFT_WEIGHT_ACF_M_FUEL_TOT 407
// 408, 409 : free
#define SIM_COCKPIT2_FUEL_QUANTITY_ 410
#define SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ 420
#define SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ 430
#define SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ 440
#define SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ 450
#define SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ 460
#define SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ 470
#define SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ 480
#define SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ 490
#define SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ 500
#define SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ 510
#define XHSI_FLIGHTMODEL_ENGINE_VIB_ 520
#define SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1 531
#define SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2 532
#define SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1 533
#define SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2 534
// 535, 536, 537 : free
#define XHSI_EICAS_OVERRIDE_TRQ_MAX 538
#define SIM_AIRCRAFT_CONTROLS_ACF_TRQ_MAX_ENG 539
#define SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ 540
#define SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_ 550
#define SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_C_ 560
#define SIM_AIRCRAFT_CONTROLS_ACF_RSC_REDLINE_PRP 569
#define SIM_COCKPIT2_ENGINE_INDICATORS_PROP_SPEED_RPM_ 570
#define SIM_FLIGHTMODEL_ENGINE_ENGN_PROPMODE_ 580
#define SIM_FLIGHTMODEL_ENGINE_ENGN_MPR_ 590
#define SIM_FLIGHTMODEL_ENGINE_ENGN_EPR_ 600


// UFMC
#define UFMC_STATUS 700
// UFMC REF
#define UFMC_V1 701
#define UFMC_VR 702
#define UFMC_V2 703
#define UFMC_VREF 704 // superceded by UFMC_F30 and UFMC_F40
#define UFMC_VF30 705
#define UFMC_VF40 706
// UFMC N1
#define UFMC_N1_1 707
#define UFMC_N1_2 708
#define UFMC_N1_3 709
#define UFMC_N1_4 710


//XFMC
#define XFMC_KEYPATH 750


// X737
#define X737_STATUS 800
// X737 AP
#define X737_AFDS_FD_A 801
#define X737_AFDS_FD_B 802
#define X737_AFDS_CMD_A 803
#define X737_AFDS_CMD_B 804
#define X737_PFD_MCPSPD 805
#define X737_PFD_FMCSPD 806
#define X737_PFD_RETARD 807
#define X737_PFD_THRHLD 808
#define X737_PFD_LNAVARMED 809
#define X737_PFD_VORLOCARMED 810
#define X737_PFD_PITCHSPD 811
#define X737_PFD_ALTHLD 812
#define X737_PFD_VSARMED 813
#define X737_PFD_VS 814
#define X737_PFD_VNAVALT 815
#define X737_PFD_VNAVPATH 816
#define X737_PFD_VNAVSPD 817
#define X737_PFD_GSARMED 818
#define X737_PFD_GS 819
#define X737_PFD_FLAREARMED 820
#define X737_PFD_FLARE 821
#define X737_PFD_TOGA 822
#define X737_PFD_LNAV 823
#define X737_PFD_HDG 824
#define X737_PFD_VORLOC 825
#define X737_PFD_N1 826
#define X737_AFDS_A_PITCH 827
#define X737_AFDS_B_PITCH 828
#define X737_AFDS_A_ROLL 829
#define X737_AFDS_B_ROLL 830
#define X737_ATHR_ARMED 831
// X737 N1
#define X737_N1_PHASE 832
#define X737_N1_LIMIT_ENG1 833
#define X737_N1_LIMIT_ENG2 834
#define X737_STBY_PWR 835
#define X737_PFD_PWR 836
#define X737_LVLCHANGE 837


// CL30
#define CL30_STATUS 850
// CL30 REF
#define CL30_V1 851
#define CL30_VR 852
#define CL30_V2 853
#define CL30_VT 854
#define CL30_VGA 855
#define CL30_VREF 856
#define CL30_REFSPDS 857
// CL30 ANNUN
#define CL30_MAST_WARN 858
#define CL30_MAST_CAUT 859
// CL30 Thrust Mode
#define CL30_CARETS 860
#define CL30_TO_N1 861
#define CL30_CLB_N1 862


// QPAC AirbusFBW
#define QPAC_STATUS 1000
// Autopilot
#define QPAC_AP_FD 1001
#define QPAC_AP_PHASE 1002
#define QPAC_PRESEL_CRZ 1003
#define QPAC_PRESEL_CLB 1004
#define QPAC_PRESEL_MACH 1005
#define QPAC_AP_VERTICAL_MODE 1006
#define QPAC_AP_VERTICAL_ARMED 1007
#define QPAC_AP_LATERAL_MODE 1008
#define QPAC_AP_LATERAL_ARMED 1009
#define QPAC_NPA_VALID 1010
#define QPAC_NPA_NO_POINTS 1011
#define QPAC_AP_APPR 1012
#define QPAC_APPR_TYPE 1013
#define QPAC_APPR_MDA 1014
#define QPAC_ALT_IS_CSTR 1015
#define QPAC_CONSTRAINT_ALT 1016
// FCU (bit field)
#define QPAC_FCU 1017
#define QPAC_FCU_BARO 1018
// Auto-Thrust
#define QPAC_ATHR_MODE 1019
#define QPAC_ATHR_MODE2 1020
#define QPAC_ATHR_LIMITED 1021
#define QPAC_THR_LEVER_MODE 1022
#define QPAC_FMA_THR_WARNING 1023
#define QPAC_FLEX_TEMP 1024
// ILS Sig and Deviation Capt. and FO
#define QPAC_ILS_FLAGS 1025
#define QPAC_LOC_VAL_CAPT 1026
#define QPAC_GS_VAL_CAPT 1027
#define QPAC_LOC_VAL_FO 1028
#define QPAC_GS_VAL_FO 1029
#define QPAC_ILS_CRS 1030
#define QPAC_ILS_FREQ 1031
#define QPAC_ILS_ID 11032
#define QPAC_ILS_DME 1033
// FD
#define QPAC_FD1_VER_BAR 1034
#define QPAC_FD1_HOR_BAR 1035
#define QPAC_FD1_YAW_BAR 1046
#define QPAC_FD2_VER_BAR 1037
#define QPAC_FD2_HOR_BAR 1038
#define QPAC_FD2_YAW_BAR 1039
// V Speeds
#define QPAC_V1_VALUE 1040
#define QPAC_V1 1041
#define QPAC_VR 1042
#define QPAC_VMO 1043
#define QPAC_VLS 1044
#define QPAC_VF 1045
#define QPAC_VS 1046
#define QPAC_V_GREEN_DOT 1047
#define QPAC_ALPHA_PROT 1048
#define QPAC_ALPHA_MAX 1049
//EFIS
#define QPAC_EFIS_ND_MODE_CAPT 1050
#define QPAC_EFIS_ND_RANGE_CAPT 1051
#define QPAC_EFIS_ND_MODE_FO 1052
#define QPAC_EFIS_ND_RANGE_FO 1053
// FAILURES
#define QPAC_FAILURES 1055
// IDs reserved for QPAC up to 1199

// JarDesign Airbus A320neo
#define JAR_A320NEO_STATUS 1200
#define JAR_A320NEO_FCU 1201
#define JAR_A320NEO_FCU_BARO 1202

// Plugin Version
#define PLUGIN_VERSION_ID 999

