
// global vars

extern XPLMDataRef x737_plugin_status;

// x737/systems/afds/...
extern XPLMDataRef x737_fdA_status;
extern XPLMDataRef x737_fdB_status;
extern XPLMDataRef x737_CMD_A;
extern XPLMDataRef x737_CMD_B;
extern XPLMDataRef x737_A_PITCH;
extern XPLMDataRef x737_B_PITCH;
extern XPLMDataRef x737_A_ROLL;
extern XPLMDataRef x737_B_ROLL;

// x737/systems/PFD/...
extern XPLMDataRef x737_MCPSPD_mode;
extern XPLMDataRef x737_FMCSPD_mode;
extern XPLMDataRef x737_RETARD_mode;
extern XPLMDataRef x737_THRHLD_mode;
extern XPLMDataRef x737_LNAVARMED_mode;
extern XPLMDataRef x737_VORLOCARMED_mode;
extern XPLMDataRef x737_PITCHSPD_mode;
extern XPLMDataRef x737_ALTHLD_mode;
extern XPLMDataRef x737_VSARMED_mode;
extern XPLMDataRef x737_VS_mode;
extern XPLMDataRef x737_VNAVALT_mode;
extern XPLMDataRef x737_VNAVPATH_mode;
extern XPLMDataRef x737_VNAVSPD_mode;
extern XPLMDataRef x737_GSARMED_mode;
extern XPLMDataRef x737_GS_mode;
extern XPLMDataRef x737_FLAREARMED_mode;
extern XPLMDataRef x737_FLARE_mode;
extern XPLMDataRef x737_TOGA_mode;
extern XPLMDataRef x737_LNAV_mode;
extern XPLMDataRef x737_HDG_mode;
extern XPLMDataRef x737_VORLOC_mode;
extern XPLMDataRef x737_N1_mode;
extern XPLMDataRef x737_PFD_pwr;

// x737/systems/athr/...
extern XPLMDataRef x737_ATHR_armed;
extern XPLMDataRef x737_lvlchange;

// x737/systems/eec/...
extern XPLMDataRef x737_N1_phase;
extern XPLMDataRef x737_N1_limit_eng1;
extern XPLMDataRef x737_N1_limit_eng2;

// x737/systems/electrics/...
extern XPLMDataRef x737_stby_pwr;

//mcp st

extern XPLMDataRef x737_MCPSPD_spd;
extern XPLMDataRef x737_HDG_magnhdg;
extern XPLMDataRef x737_ALTHLD_baroalt;
extern XPLMDataRef x737_VS_vvi;

extern XPLMDataRef x737_left_fixed_land_light_switch;
extern XPLMDataRef x737_right_fixed_land_light_switch;
extern XPLMDataRef x737_left_retr_land_light_switch;
extern XPLMDataRef x737_rigth_retr_land_light_switch;
extern XPLMDataRef x737_taxi_light_switch;
extern XPLMDataRef x737_left_turnoff_light_switch;
extern XPLMDataRef x737_right_turnoff_light_switch;
extern XPLMDataRef x737_position_light_switch;
extern XPLMDataRef x737_beacon_light_switch;

// EFIS
extern XPLMDataRef x737_efis0_nd_range_enum;
extern XPLMDataRef x737_efis0_FPV;
extern XPLMDataRef x737_efis0_MTR;
extern XPLMDataRef x737_efis0_TFC;
extern XPLMDataRef x737_efis0_CTR;
extern XPLMDataRef x737_efis0_WXR;
extern XPLMDataRef x737_efis0_STA;
extern XPLMDataRef x737_efis0_WPT;
extern XPLMDataRef x737_efis0_ARPT;
extern XPLMDataRef x737_efis0_DATA;
extern XPLMDataRef x737_efis0_POS;
extern XPLMDataRef x737_efis0_TERR;
            
extern XPLMDataRef x737_efis1_nd_range_enum;
extern XPLMDataRef x737_efis1_FPV;
extern XPLMDataRef x737_efis1_MTR;
extern XPLMDataRef x737_efis1_TFC;
extern XPLMDataRef x737_efis1_CTR;
extern XPLMDataRef x737_efis1_WXR;
extern XPLMDataRef x737_efis1_STA;
extern XPLMDataRef x737_efis1_WPT;
extern XPLMDataRef x737_efis1_ARPT;
extern XPLMDataRef x737_efis1_DATA;
extern XPLMDataRef x737_efis1_POS;
extern XPLMDataRef x737_efis1_TERR;


extern int x737_ready;


// global functions
float	checkX737Callback(float, float, int, void *);

