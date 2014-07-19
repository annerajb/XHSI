
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#define XPLM200 1

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPWidgets.h"
#include "XPStandardWidgets.h"


#include "structs.h"
#include "datarefs.h"
#include "datarefs_x737.h"


#define UP -99
#define DOWN -1
#define SHUTTLE -22
#define TOGGLE -333
#define CYCLE -4444
#define ON 1
#define OFF 0

#define B737CL_MAP 73701
#define B737CL_CTR_MAP 73702
#define B737CL_EXP_VOR_ILS 73703
#define B737CL_FULL_VOR_ILS 73704
#define B737CL_PLAN 73705

#define EFIS_PILOT 0
#define EFIS_COPILOT 1

#define MODE_APP 0
#define MODE_VOR 1
#define MODE_MAP 2
#define MODE_NAV 3
#define MODE_PLN 4

#define MODE_CENTERED 0
#define MODE_EXPANDED 1

#define ZOOMIN_OFF 0
#define ZOOMIN_ON 1

#define RANGE_10 0
#define RANGE_20 1
#define RANGE_40 2
#define RANGE_80 3
#define RANGE_160 4
#define RANGE_320 5
#define RANGE_640 6

#define RADIO_ADF 0
#define RADIO_OFF 1
#define RADIO_NAV 2

#define SOURCE_NAV1 0
#define SOURCE_NAV2 1
#define SOURCE_FMC 2

#define MINS_RADIO 0
#define MINS_BARO 1

//#define MFD_TAXI 0
#define MFD_ARPT 0
#define MFD_FPLN 1
#define MFD_EICAS 2


XPLMCommandRef mode_app;
XPLMCommandRef mode_vor;
XPLMCommandRef mode_map;
XPLMCommandRef mode_nav;
XPLMCommandRef mode_pln;
XPLMCommandRef mode_down;
XPLMCommandRef mode_up;
XPLMCommandRef mode_cycle;
XPLMCommandRef mode_shuttle;

XPLMCommandRef b737cl_mode_map;
XPLMCommandRef b737cl_mode_ctrmap;
XPLMCommandRef b737cl_mode_expvorils;
XPLMCommandRef b737cl_mode_fullvorils;
XPLMCommandRef b737cl_mode_plan;

XPLMCommandRef ctr_toggle;
XPLMCommandRef ctr_on;
XPLMCommandRef ctr_off;

XPLMCommandRef range_10;
XPLMCommandRef range_20;
XPLMCommandRef range_40;
XPLMCommandRef range_80;
XPLMCommandRef range_160;
XPLMCommandRef range_320;
XPLMCommandRef range_640;
XPLMCommandRef range_down;
XPLMCommandRef range_up;
XPLMCommandRef range_cycle;
XPLMCommandRef range_shuttle;

XPLMCommandRef ext_range_0_10;
XPLMCommandRef ext_range_0_20;
XPLMCommandRef ext_range_0_40;
XPLMCommandRef ext_range_0_80;
XPLMCommandRef ext_range_1_60;
XPLMCommandRef ext_range_3_20;
XPLMCommandRef ext_range_6_40;
XPLMCommandRef ext_range_010;
XPLMCommandRef ext_range_020;
XPLMCommandRef ext_range_040;
XPLMCommandRef ext_range_080;
XPLMCommandRef ext_range_160;
XPLMCommandRef ext_range_320;
XPLMCommandRef ext_range_640;
XPLMCommandRef ext_range_down;
XPLMCommandRef ext_range_up;
XPLMCommandRef ext_range_cycle;
XPLMCommandRef ext_range_shuttle;

XPLMCommandRef zoom_x100_on;
XPLMCommandRef zoom_x100_off;
XPLMCommandRef zoom_x100_toggle;

XPLMCommandRef radio1_adf;
XPLMCommandRef radio1_off;
XPLMCommandRef radio1_nav;
XPLMCommandRef radio1_down;
XPLMCommandRef radio1_up;
//XPLMCommandRef radio1_cycle;
XPLMCommandRef radio1_shuttle;

XPLMCommandRef radio2_adf;
XPLMCommandRef radio2_off;
XPLMCommandRef radio2_nav;
XPLMCommandRef radio2_down;
XPLMCommandRef radio2_up;
//XPLMCommandRef radio2_cycle;
XPLMCommandRef radio2_shuttle;

XPLMCommandRef source_nav1;
XPLMCommandRef source_nav2;
XPLMCommandRef source_fmc;
XPLMCommandRef source_down;
XPLMCommandRef source_up;
XPLMCommandRef source_cycle;

XPLMCommandRef tfc_toggle;
XPLMCommandRef tfc_on;
XPLMCommandRef tfc_off;

XPLMCommandRef arpt_toggle;
XPLMCommandRef arpt_on;
XPLMCommandRef arpt_off;

XPLMCommandRef wpt_toggle;
XPLMCommandRef wpt_on;
XPLMCommandRef wpt_off;

XPLMCommandRef vor_toggle;
XPLMCommandRef vor_on;
XPLMCommandRef vor_off;

XPLMCommandRef ndb_toggle;
XPLMCommandRef ndb_on;
XPLMCommandRef ndb_off;

XPLMCommandRef sta_toggle;
XPLMCommandRef sta_on;
XPLMCommandRef sta_off;
XPLMCommandRef sta_cycle;

XPLMCommandRef data_toggle;
XPLMCommandRef data_on;
XPLMCommandRef data_off;

XPLMCommandRef pos_toggle;
XPLMCommandRef pos_on;
XPLMCommandRef pos_off;

XPLMCommandRef mins_toggle;
XPLMCommandRef mins_radio;
XPLMCommandRef mins_baro;

XPLMCommandRef mins_reset;
XPLMCommandRef mins_down;
XPLMCommandRef mins_up;


XPLMCommandRef copilot_mode_app;
XPLMCommandRef copilot_mode_vor;
XPLMCommandRef copilot_mode_map;
XPLMCommandRef copilot_mode_nav;
XPLMCommandRef copilot_mode_pln;
XPLMCommandRef copilot_mode_down;
XPLMCommandRef copilot_mode_up;
XPLMCommandRef copilot_mode_cycle;
XPLMCommandRef copilot_mode_shuttle;

XPLMCommandRef b737cl_copilot_mode_map;
XPLMCommandRef b737cl_copilot_mode_ctrmap;
XPLMCommandRef b737cl_copilot_mode_expvorils;
XPLMCommandRef b737cl_copilot_mode_fullvorils;
XPLMCommandRef b737cl_copilot_mode_plan;

XPLMCommandRef copilot_ctr_toggle;
XPLMCommandRef copilot_ctr_on;
XPLMCommandRef copilot_ctr_off;

XPLMCommandRef copilot_range_10;
XPLMCommandRef copilot_range_20;
XPLMCommandRef copilot_range_40;
XPLMCommandRef copilot_range_80;
XPLMCommandRef copilot_range_160;
XPLMCommandRef copilot_range_320;
XPLMCommandRef copilot_range_640;
XPLMCommandRef copilot_range_down;
XPLMCommandRef copilot_range_up;
XPLMCommandRef copilot_range_cycle;
XPLMCommandRef copilot_range_shuttle;

XPLMCommandRef copilot_ext_range_0_10;
XPLMCommandRef copilot_ext_range_0_20;
XPLMCommandRef copilot_ext_range_0_40;
XPLMCommandRef copilot_ext_range_0_80;
XPLMCommandRef copilot_ext_range_1_60;
XPLMCommandRef copilot_ext_range_3_20;
XPLMCommandRef copilot_ext_range_6_40;
XPLMCommandRef copilot_ext_range_010;
XPLMCommandRef copilot_ext_range_020;
XPLMCommandRef copilot_ext_range_040;
XPLMCommandRef copilot_ext_range_080;
XPLMCommandRef copilot_ext_range_160;
XPLMCommandRef copilot_ext_range_320;
XPLMCommandRef copilot_ext_range_640;
XPLMCommandRef copilot_ext_range_down;
XPLMCommandRef copilot_ext_range_up;
XPLMCommandRef copilot_ext_range_cycle;
XPLMCommandRef copilot_ext_range_shuttle;

XPLMCommandRef copilot_zoom_x100_on;
XPLMCommandRef copilot_zoom_x100_off;
XPLMCommandRef copilot_zoom_x100_toggle;

XPLMCommandRef copilot_radio1_adf;
XPLMCommandRef copilot_radio1_off;
XPLMCommandRef copilot_radio1_nav;
XPLMCommandRef copilot_radio1_down;
XPLMCommandRef copilot_radio1_up;
//XPLMCommandRef copilot_radio1_cycle;
XPLMCommandRef copilot_radio1_shuttle;

XPLMCommandRef copilot_radio2_adf;
XPLMCommandRef copilot_radio2_off;
XPLMCommandRef copilot_radio2_nav;
XPLMCommandRef copilot_radio2_down;
XPLMCommandRef copilot_radio2_up;
//XPLMCommandRef copilot_radio2_cycle;
XPLMCommandRef copilot_radio2_shuttle;

XPLMCommandRef copilot_source_nav1;
XPLMCommandRef copilot_source_nav2;
XPLMCommandRef copilot_source_fmc;
XPLMCommandRef copilot_source_down;
XPLMCommandRef copilot_source_up;
XPLMCommandRef copilot_source_cycle;

XPLMCommandRef copilot_tfc_toggle;
XPLMCommandRef copilot_tfc_on;
XPLMCommandRef copilot_tfc_off;

XPLMCommandRef copilot_arpt_toggle;
XPLMCommandRef copilot_arpt_on;
XPLMCommandRef copilot_arpt_off;

XPLMCommandRef copilot_wpt_toggle;
XPLMCommandRef copilot_wpt_on;
XPLMCommandRef copilot_wpt_off;

XPLMCommandRef copilot_vor_toggle;
XPLMCommandRef copilot_vor_on;
XPLMCommandRef copilot_vor_off;

XPLMCommandRef copilot_ndb_toggle;
XPLMCommandRef copilot_ndb_on;
XPLMCommandRef copilot_ndb_off;

XPLMCommandRef copilot_sta_toggle;
XPLMCommandRef copilot_sta_on;
XPLMCommandRef copilot_sta_off;
XPLMCommandRef copilot_sta_cycle;

XPLMCommandRef copilot_data_toggle;
XPLMCommandRef copilot_data_on;
XPLMCommandRef copilot_data_off;

XPLMCommandRef copilot_pos_toggle;
XPLMCommandRef copilot_pos_on;
XPLMCommandRef copilot_pos_off;

XPLMCommandRef copilot_mins_toggle;
XPLMCommandRef copilot_mins_radio;
XPLMCommandRef copilot_mins_baro;

XPLMCommandRef copilot_mins_reset;
XPLMCommandRef copilot_mins_down;
XPLMCommandRef copilot_mins_up;

XPLMCommandRef nav1_sync;
XPLMCommandRef nav2_sync;

XPLMCommandRef auto_range_pilot;
XPLMCommandRef auto_range_copilot;

XPLMCommandRef auto_ext_range_pilot;
XPLMCommandRef auto_ext_range_copilot;

XPLMCommandRef chr_start_stop_reset;
XPLMCommandRef chr_start_stop;
XPLMCommandRef chr_reset;

XPLMCommandRef timer_start_stop;
XPLMCommandRef timer_reset;

//XPLMCommandRef mfd_mode_taxi;
XPLMCommandRef mfd_mode_arpt;
XPLMCommandRef mfd_mode_fpln;
XPLMCommandRef mfd_mode_eicas;
XPLMCommandRef mfd_mode_down;
XPLMCommandRef mfd_mode_up;
XPLMCommandRef mfd_mode_cycle;


// for an RTU
XPLMCommandRef nav1_standy_flip;
XPLMCommandRef nav2_standy_flip;
XPLMCommandRef com1_standy_flip;
XPLMCommandRef com2_standy_flip;
XPLMCommandRef adf1_standy_flip;
XPLMCommandRef adf2_standy_flip;
XPLMCommandRef sim_transponder_transponder_ident;


// for an MCP
XPLMCommandRef sim_autopilot_fdir_servos_toggle;
XPLMCommandRef sim_autopilot_autothrottle_toggle;
XPLMCommandRef sim_autopilot_level_change;
XPLMCommandRef sim_autopilot_heading;
XPLMCommandRef sim_autopilot_vertical_speed;
XPLMCommandRef sim_autopilot_nav;
XPLMCommandRef sim_autopilot_approach;
XPLMCommandRef sim_autopilot_glide_slope;
XPLMCommandRef sim_autopilot_back_course;
XPLMCommandRef sim_autopilot_altitude_hold;

// lights
XPLMCommandRef sim_lights_nav_lights_toggle;
XPLMCommandRef sim_lights_beacon_lights_toggle;
XPLMCommandRef sim_lights_taxi_lights_toggle;
XPLMCommandRef sim_lights_strobe_lights_toggle;
XPLMCommandRef sim_lights_landing_lights_toggle;

// flaps, gear and speedbrake
XPLMCommandRef sim_flight_controls_flaps_down;
XPLMCommandRef sim_flight_controls_flaps_up;
XPLMCommandRef sim_flight_controls_landing_gear_toggle;
XPLMCommandRef sim_flight_controls_speed_brakes_down_one;
XPLMCommandRef sim_flight_controls_speed_brakes_up_one;

//x737 mcp toggles
XPLMCommandRef x737_cmda_toggle;
XPLMCommandRef x737_mcpspd_toggle;
XPLMCommandRef x737_lvlchange_toggle;
XPLMCommandRef x737_hdgsel_toggle;
XPLMCommandRef x737_lnav_toggle;
XPLMCommandRef x737_vorloc_toggle;
XPLMCommandRef x737_app_toggle;
XPLMCommandRef x737_althld_toggle;
XPLMCommandRef x737_vs_toggle;


char debug_string[80];


// ctr
XPLMCommandCallback_f ctr_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_map_mode);
        XPLMSetDatai(efis_map_mode, i);
    }
    return (XPLMCommandCallback_f)1;
}

// zoomin
XPLMCommandCallback_f zoom_x100_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_map_zoomin);
        XPLMSetDatai(efis_pilot_map_zoomin, i);
    }
    return (XPLMCommandCallback_f)1;
}

// range
XPLMCommandCallback_f range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_map_range_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_map_range_selector) + 1;
            if (i>6) i = 6;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(efis_map_range_selector) + 1;
            if (i>6) i = 0;
        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_map_range_selector) + (shuttle_up ? +1 : -1);
            if (i>6)
            {
                i = 5;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_map_range_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// ext range
XPLMCommandCallback_f ext_range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        int z = XPLMGetDatai(efis_pilot_map_zoomin);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_map_range_selector) - 1;
            if (i<0)
            {
                if ( z )
                {
                    i = 0;
                }
                else
                {
                    i = 6;
                    z = 1;
                }
            }
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_map_range_selector) + 1;
            if (i>6)
            {
                if ( ! z )
                {
                    i = 6;
                }
                else
                {
                    i = 0;
                    z = 0;
                }
            }
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(efis_map_range_selector) + 1;
            if (i>6)
            {
                i = 0;
                z = !z;
            }
        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_map_range_selector) + (shuttle_up ? +1 : -1);
            if (i>6)
            {
                if ( z )
                {
                    i = 0;
                    z = ! z;
                }
                else
                {
                    i = 5;
                    shuttle_up = 0;
                }
            }
            else if (i<0)
            {
                if ( z )
                {
                    i = 1;
                    shuttle_up = 1;
                }
                else
                {
                    i = 6;
                    z = ! z;;
                }
            }
        }
        if (i>=1100)
        {
            z = 1;
        }
        else if (i>=1000)
        {
            z = 0;
        }
        XPLMSetDatai(efis_map_range_selector, i % 10);
        XPLMSetDatai(efis_pilot_map_zoomin, z);
    }
    return (XPLMCommandCallback_f)1;
}

// mode
XPLMCommandCallback_f mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_map_submode) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_map_submode) + 1;
            if (i>4) i = 4;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(efis_map_submode) + 1;
            if (i>4) i = 0;
        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_map_submode) + (shuttle_up ? +1 : -1);
            if (i>4)
            {
                i = 3;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_map_submode, i);
    }
    return (XPLMCommandCallback_f)1;
}

// B737-Classic modes
XPLMCommandCallback_f b737cl_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);

        if ( i == B737CL_FULL_VOR_ILS )
        {
            XPLMSetDatai(efis_map_submode, MODE_APP);
            XPLMSetDatai(efis_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_EXP_VOR_ILS )
        {
            XPLMSetDatai(efis_map_submode, MODE_APP);
            XPLMSetDatai(efis_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_MAP )
        {
            XPLMSetDatai(efis_map_submode, MODE_MAP);
            XPLMSetDatai(efis_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_CTR_MAP )
        {
            XPLMSetDatai(efis_map_submode, MODE_MAP);
            XPLMSetDatai(efis_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_PLAN )
        {
            XPLMSetDatai(efis_map_submode, MODE_PLN);
        }
    }
    return (XPLMCommandCallback_f)1;
}

// radio1
XPLMCommandCallback_f radio1_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_dme_1_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_dme_1_selector) + 1;
            if (i>2) i = 2;
        }
//        else if ( i == CYCLE )
//        {
//            i = XPLMGetDatai(efis_dme_1_selector) + 1;
//            if (i>2) i = 0;
//        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_dme_1_selector) + (shuttle_up ? +1 : -1);
            if (i>2)
            {
                i = 1;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_dme_1_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// radio2
XPLMCommandCallback_f radio2_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_dme_2_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_dme_2_selector) + 1;
            if (i>2) i = 2;
        }
//        else if ( i == CYCLE )
//        {
//            i = XPLMGetDatai(efis_dme_2_selector) + 1;
//            if (i>2) i = 0;
//        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_dme_2_selector) + (shuttle_up ? +1 : -1);
            if (i>2)
            {
                i = 1;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_dme_2_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}


// source
XPLMCommandCallback_f source_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(hsi_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(hsi_selector) + 1;
            if (i>2) i = 2;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(hsi_selector) + 1;
            if (i>2) i = 0;
        }
        XPLMSetDatai(hsi_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// tfc
XPLMCommandCallback_f tfc_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_tcas);
        XPLMSetDatai(efis_shows_tcas, i);
    }
    return (XPLMCommandCallback_f)1;
}

// arpt
XPLMCommandCallback_f arpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_airports);
        XPLMSetDatai(efis_shows_airports, i);
    }
    return (XPLMCommandCallback_f)1;
}

// wpt
XPLMCommandCallback_f wpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_waypoints);
        XPLMSetDatai(efis_shows_waypoints, i);
    }
    return (XPLMCommandCallback_f)1;
}

// vor
XPLMCommandCallback_f vor_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_vors);
        XPLMSetDatai(efis_shows_vors, i);
    }
    return (XPLMCommandCallback_f)1;
}

// ndb
XPLMCommandCallback_f ndb_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_ndbs);
        XPLMSetDatai(efis_shows_ndbs, i);
    }
    return (XPLMCommandCallback_f)1;
}

// sta = vor + ndb
XPLMCommandCallback_f sta_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == CYCLE )
        {
            int vors = XPLMGetDatai(efis_shows_vors);
            int ndbs = XPLMGetDatai(efis_shows_ndbs);
            if ( ! vors && ! ndbs )
            {
                XPLMSetDatai(efis_shows_vors, 1);
            }
            else if ( vors && ! ndbs )
            {
                XPLMSetDatai(efis_shows_ndbs, 1);
            }
            else if ( vors && ndbs )
            {
                XPLMSetDatai(efis_shows_vors, 0);
            }
            else if ( ! vors && ndbs )
            {
                XPLMSetDatai(efis_shows_ndbs, 0);
            }
        }
        else
        {
            if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_shows_stas);
            XPLMSetDatai(efis_pilot_shows_stas, i);
            XPLMSetDatai(efis_shows_vors, i);
            XPLMSetDatai(efis_shows_ndbs, i);
        }
    }
    return (XPLMCommandCallback_f)1;
}

// data = route data
XPLMCommandCallback_f data_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_shows_data);
        XPLMSetDatai(efis_pilot_shows_data, i);
    }
    return (XPLMCommandCallback_f)1;
}

// pos
XPLMCommandCallback_f pos_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_shows_pos);
        XPLMSetDatai(efis_pilot_shows_pos, i);
    }
    return (XPLMCommandCallback_f)1;
}

// mins mode
XPLMCommandCallback_f mins_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_mins_mode);
        XPLMSetDatai(efis_pilot_mins_mode, i);
    }
    return (XPLMCommandCallback_f)1;
}

// mins value
XPLMCommandCallback_f mins_value_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        int m = XPLMGetDatai(efis_pilot_mins_mode);
        if ( m )
        {
            int da = 0;
            if ( i == DOWN )
            {
                da = XPLMGetDatai(efis_pilot_da_bug) - 50;
                if (da<0) da = 0;
            }
            else if ( i == UP )
            {
                da = XPLMGetDatai(efis_pilot_da_bug) + 50;
            }
            else if ( i == OFF )
            {
                da = 0;
            }
            XPLMSetDatai(efis_pilot_da_bug, da);
        }
        else
        {
            float dh = 0.0f;
            if ( i == DOWN )
            {
                dh = XPLMGetDataf(ra_bug_pilot) - 10.0f;
                if (dh<0.0f) dh = 0.0f;
            }
            else if ( i == UP )
            {
                dh = XPLMGetDataf(ra_bug_pilot) + 10.0f;
                if (dh>2500.0f) dh = 2500.0f;
            }
            else if ( i == OFF )
            {
                dh = 0.0f;
            }
            XPLMSetDataf(ra_bug_pilot, dh);
        }
    }
    return (XPLMCommandCallback_f)1;
}


// copilot command handlers
// copilot ctr
XPLMCommandCallback_f copilot_ctr_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_map_mode);
        XPLMSetDatai(efis_copilot_map_mode, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot zoomin
XPLMCommandCallback_f copilot_zoom_x100_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_map_zoomin);
        XPLMSetDatai(efis_copilot_map_zoomin, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot range
XPLMCommandCallback_f copilot_range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) + 1;
            if (i>6) i = 6;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) + 1;
            if (i>6) i = 0;
        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) + (shuttle_up ? +1 : -1);
            if (i>6)
            {
                i = 5;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_copilot_map_range_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot ext range
XPLMCommandCallback_f copilot_ext_range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        int z = XPLMGetDatai(efis_copilot_map_zoomin);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) - 1;
            if (i<0)
            {
                if ( z )
                {
                    i = 0;
                }
                else
                {
                    i = 6;
                    z = 1;
                }
            }
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) + 1;
            if (i>6)
            {
                if ( ! z )
                {
                    i = 6;
                }
                else
                {
                    i = 0;
                    z = 0;
                }
            }
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) + 1;
            if (i>6)
            {
                i = 0;
                z = !z;
            }
        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_copilot_map_range_selector) + (shuttle_up ? +1 : -1);
            if (i>6)
            {
                if ( z )
                {
                    i = 0;
                    z = ! z;
                }
                else
                {
                    i = 5;
                    shuttle_up = 0;
                }
            }
            else if (i<0)
            {
                if ( z )
                {
                    i = 1;
                    shuttle_up = 1;
                }
                else
                {
                    i = 6;
                    z = ! z;;
                }
            }
        }
        if (i>=1100)
        {
            z = 1;
        }
        else if (i>=1000)
        {
            z = 0;
        }
        XPLMSetDatai(efis_copilot_map_range_selector, i % 10);
        XPLMSetDatai(efis_copilot_map_zoomin, z);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot mode
XPLMCommandCallback_f copilot_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_copilot_map_submode) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_copilot_map_submode) + 1;
            if (i>4) i = 4;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(efis_copilot_map_submode) + 1;
            if (i>4) i = 0;
        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_copilot_map_submode) + (shuttle_up ? +1 : -1);
            if (i>4)
            {
                i = 3;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_copilot_map_submode, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot B737-Classic modes
XPLMCommandCallback_f b737cl_copilot_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);

        if ( i == B737CL_FULL_VOR_ILS )
        {
            XPLMSetDatai(efis_copilot_map_submode, MODE_APP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_EXP_VOR_ILS )
        {
            XPLMSetDatai(efis_copilot_map_submode, MODE_APP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_MAP )
        {
            XPLMSetDatai(efis_copilot_map_submode, MODE_MAP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_CTR_MAP )
        {
            XPLMSetDatai(efis_copilot_map_submode, MODE_MAP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_PLAN )
        {
            XPLMSetDatai(efis_copilot_map_submode, MODE_PLN);
        }
    }
    return (XPLMCommandCallback_f)1;
}

// copilot radio1
XPLMCommandCallback_f copilot_radio1_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_copilot_dme_1_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_copilot_dme_1_selector) + 1;
            if (i>2) i = 2;
        }
//        else if ( i == CYCLE )
//        {
//            i = XPLMGetDatai(efis_copilot_dme_1_selector) + 1;
//            if (i>2) i = 0;
//        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_copilot_dme_1_selector) + (shuttle_up ? +1 : -1);
            if (i>2)
            {
                i = 1;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_copilot_dme_1_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot radio2
XPLMCommandCallback_f copilot_radio2_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    static int shuttle_up = 1;

    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(efis_copilot_dme_2_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(efis_copilot_dme_2_selector) + 1;
            if (i>2) i = 2;
        }
//        else if ( i == CYCLE )
//        {
//            i = XPLMGetDatai(efis_copilot_dme_2_selector) + 1;
//            if (i>2) i = 0;
//        }
        else if ( i == SHUTTLE )
        {
            i = XPLMGetDatai(efis_copilot_dme_2_selector) + (shuttle_up ? +1 : -1);
            if (i>2)
            {
                i = 1;
                shuttle_up = 0;
            }
            else if (i<0)
            {
                i = 1;
                shuttle_up = 1;
            }
        }
        XPLMSetDatai(efis_copilot_dme_2_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot source
XPLMCommandCallback_f copilot_source_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(copilot_hsi_selector) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(copilot_hsi_selector) + 1;
            if (i>2) i = 2;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(copilot_hsi_selector) + 1;
            if (i>2) i = 0;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(copilot_hsi_selector) + 1;
            if (i>2) i = 0;
        }
        XPLMSetDatai(copilot_hsi_selector, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot tfc
XPLMCommandCallback_f copilot_tfc_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_tcas);
        XPLMSetDatai(efis_copilot_shows_tcas, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot arpt
XPLMCommandCallback_f copilot_arpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_airports);
        XPLMSetDatai(efis_copilot_shows_airports, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot wpt
XPLMCommandCallback_f copilot_wpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_waypoints);
        XPLMSetDatai(efis_copilot_shows_waypoints, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot vor
XPLMCommandCallback_f copilot_vor_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_vors);
        XPLMSetDatai(efis_copilot_shows_vors, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot ndb
XPLMCommandCallback_f copilot_ndb_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_ndbs);
        XPLMSetDatai(efis_copilot_shows_ndbs, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot sta = vor + ndb
XPLMCommandCallback_f copilot_sta_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == CYCLE )
        {
            int vors = XPLMGetDatai(efis_copilot_shows_vors);
            int ndbs = XPLMGetDatai(efis_copilot_shows_ndbs);
            if ( ! vors && ! ndbs )
            {
                XPLMSetDatai(efis_copilot_shows_vors, 1);
            }
            else if ( vors && ! ndbs )
            {
                XPLMSetDatai(efis_copilot_shows_ndbs, 1);
            }
            else if ( vors && ndbs )
            {
                XPLMSetDatai(efis_copilot_shows_vors, 0);
            }
            else if ( ! vors && ndbs )
            {
                XPLMSetDatai(efis_copilot_shows_ndbs, 0);
            }
        }
        else
        {
            if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_stas);
            XPLMSetDatai(efis_copilot_shows_stas, i);
            XPLMSetDatai(efis_copilot_shows_vors, i);
            XPLMSetDatai(efis_copilot_shows_ndbs, i);
        }
    }
    return (XPLMCommandCallback_f)1;
}

// copilot data = route data
XPLMCommandCallback_f copilot_data_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_data);
        XPLMSetDatai(efis_copilot_shows_data, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot pos
XPLMCommandCallback_f copilot_pos_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_pos);
        XPLMSetDatai(efis_copilot_shows_pos, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot mins mode
XPLMCommandCallback_f copilot_mins_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_mins_mode);
        XPLMSetDatai(efis_copilot_mins_mode, i);
    }
    return (XPLMCommandCallback_f)1;
}

// copilot mins value
XPLMCommandCallback_f copilot_mins_value_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        int m = XPLMGetDatai(efis_copilot_mins_mode);
        if ( m )
        {
            int da = 0;
            if ( i == DOWN )
            {
                da = XPLMGetDatai(efis_copilot_da_bug) - 50;
                if (da<0) da = 0;
            }
            else if ( i == UP )
            {
                da = XPLMGetDatai(efis_copilot_da_bug) + 50;
            }
            else if ( i == OFF )
            {
                da = 0;
            }
            XPLMSetDatai(efis_copilot_da_bug, da);
        }
        else
        {
            float dh = 0.0f;
            if ( i == DOWN )
            {
                dh = XPLMGetDataf(ra_bug_copilot) - 10.0f;
                if (dh<0.0f) dh = 0.0f;
            }
            else if ( i == UP )
            {
                dh = XPLMGetDataf(ra_bug_copilot) + 10.0f;
                if (dh>2500.0f) dh = 2500.0f;
            }
            else if ( i == OFF )
            {
                dh = 0.0f;
            }
            XPLMSetDataf(ra_bug_copilot, dh);
        }
    }
    return (XPLMCommandCallback_f)1;
}


// MFD
XPLMCommandCallback_f mfd_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == DOWN )
        {
            i = XPLMGetDatai(mfd_mode) - 1;
            if (i<0) i = 0;
        }
        else if ( i == UP )
        {
            i = XPLMGetDatai(mfd_mode) + 1;
            if (i>2) i = 2;
        }
        else if ( i == CYCLE )
        {
            i = XPLMGetDatai(mfd_mode) + 1;
            if (i>2) i = 0;
        }
        XPLMSetDatai(mfd_mode, i);
    }
    return (XPLMCommandCallback_f)1;
}


// Sync NAV*
XPLMCommandCallback_f nav_sync_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandContinue)
    {
	    int navaid[6];
        float heading;
        float bearing;
        float course;
        int i = (int)((intptr_t)inRefcon);
	    XPLMGetDatavi(nav_type_, navaid, 0, 2);
        if ( i == 1 )
        {
			// NAV1
			if ( ( navaid[0] == 4 ) || ( navaid[0] == 5 ) ) {
				// LOC/ILS
				XPLMSetDataf( nav1_obs_degm, XPLMGetDataf( nav1_course_degm ) );
			} else {
				// VOR
					heading = XPLMGetDataf(magpsi);
					bearing = XPLMGetDataf(nav1_dir_degt);
					course = heading + bearing;
					if ( course < 0.0f ) course += 360.0f;
					XPLMSetDataf(nav1_obs_degm, course );
			}
        } else /* i == 2 */ {
			// NAV2
			if ( ( navaid[1] == 4 ) || ( navaid[1] == 5 ) ) {
				XPLMSetDataf( nav2_obs_degm, XPLMGetDataf( nav2_course_degm ) );
			} else {
				heading = XPLMGetDataf(magpsi);
				bearing = XPLMGetDataf(nav2_dir_degt);
				course = heading + bearing;
				if ( course < 0.0f ) course += 360.0f;
				XPLMSetDataf(nav2_obs_degm, course );
			}
        }
    }
    return (XPLMCommandCallback_f)1;
}


// Auto Range
XPLMCommandCallback_f auto_range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandContinue)
    {
        float dist = 0.0f;
        int i = (int)((intptr_t)inRefcon);
		XPLMDataRef nd_range = NULL;
		int mode = 0;
		int ctr = 0;
		switch ( i )
		{
		case EFIS_PILOT :
			dist = XPLMGetDataf(hsi_dme_nm_pilot);
			mode = XPLMGetDatai(efis_map_submode);
			ctr = XPLMGetDatai(efis_map_mode);
			// the copilot dataref that we will modify
			nd_range = efis_map_range_selector;
			break;
		case EFIS_COPILOT :
			dist = XPLMGetDataf(hsi_dme_nm_copilot);
			mode = XPLMGetDatai(efis_copilot_map_submode);
			ctr = XPLMGetDatai(efis_copilot_map_mode);
			// the copilot dataref that we will modify
			nd_range = efis_copilot_map_range_selector;
			break;
		}

		// half the range when centered
		// actually, we pretend that the distance doubles, otherwise we would have to change all the range values below
		if ( ( !ctr && ((mode==0)||(mode==1)||(mode==2)) ) || ( ctr && (mode==3) ) || (mode==4) )
			dist *= 2.0f;

        if ( ( dist < 999.9f ) && ( dist >= 640.0f * 0.4f ) )
			XPLMSetDatai(nd_range, RANGE_640);
		else if ( ( dist < 320.0f * 0.8f ) && ( dist >= 320.0f * 0.4f ) )
			XPLMSetDatai(nd_range, RANGE_320);
		else if ( ( dist < 160.0f * 0.8f ) && ( dist >= 160.0f * 0.4f ) )
			XPLMSetDatai(nd_range, RANGE_160);
		else if ( ( dist < 80.0f * 0.8f ) && ( dist >= 80.0f * 0.4f ) )
			XPLMSetDatai(nd_range, RANGE_80);
		else if ( ( dist < 40.0f * 0.8f ) && ( dist >= 40.0f * 0.4f ) )
			XPLMSetDatai(nd_range, RANGE_40);
		else if ( ( dist < 20.0f * 0.8f ) && ( dist >= 20.0f * 0.4f ) )
			XPLMSetDatai(nd_range, RANGE_20);
		else if ( ( dist < 10.0f * 0.8f ) && ( dist > 0.0f ) )
			XPLMSetDatai(nd_range, RANGE_10);
    }
    return (XPLMCommandCallback_f)1;
}


// Auto Extended Range
XPLMCommandCallback_f auto_ext_range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandContinue)
    {
        float dist = 0.0f;
        int i = (int)((intptr_t)inRefcon);
		XPLMDataRef nd_range = NULL;
		XPLMDataRef nd_zoomin = NULL;
		int mode = 0;
		int ctr = 0;
		switch ( i )
		{
		case EFIS_PILOT :
			dist = XPLMGetDataf(hsi_dme_nm_pilot);
			mode = XPLMGetDatai(efis_map_submode);
			ctr = XPLMGetDatai(efis_map_mode);
			// the copilot datarefs that we will modify
			nd_range = efis_map_range_selector;
			nd_zoomin = efis_pilot_map_zoomin;
			break;
		case EFIS_COPILOT :
			dist = XPLMGetDataf(hsi_dme_nm_copilot);
			mode = XPLMGetDatai(efis_copilot_map_submode);
			ctr = XPLMGetDatai(efis_copilot_map_mode);
			// the copilot datarefs that we will modify
			nd_range = efis_copilot_map_range_selector;
			nd_zoomin = efis_copilot_map_zoomin;
			break;
		}

		// half the range when centered
		// actually, we pretend that the distance doubles, otherwise we would have to change all the range values below
		if ( ( !ctr && ((mode==0)||(mode==1)||(mode==2)) ) || ( ctr && (mode==3) ) || (mode==4) )
			dist *= 2.0f;

        if ( ( dist < 999.9f ) && ( dist >= 640.0f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_640);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 320.0f * 0.8f ) && ( dist >= 320.0f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_320);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 160.0f * 0.8f ) && ( dist >= 160.0f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_160);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 80.0f * 0.8f ) && ( dist >= 80.0f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_80);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 40.0f * 0.8f ) && ( dist >= 40.0f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_40);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 20.0f * 0.8f ) && ( dist >= 20.0f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_20);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 10.0f * 0.8f ) && ( dist >= 5.0f ) ) {
			XPLMSetDatai(nd_range, RANGE_10);
			XPLMSetDatai(nd_zoomin, ZOOMIN_OFF);
		} else if ( ( dist < 5.0f ) && ( dist >= 6.40f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_640);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		} else if ( ( dist < 3.20f * 0.8f ) && ( dist >= 3.20f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_320);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		} else if ( ( dist < 1.60f * 0.8f ) && ( dist >= 1.60f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_160);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		} else if ( ( dist < 0.80f * 0.8f ) && ( dist >= 0.80f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_80);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		} else if ( ( dist < 0.40f * 0.8f ) && ( dist >= 0.40f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_40);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		} else if ( ( dist < 0.20f * 0.8f ) && ( dist >= 0.20f * 0.4f ) ) {
			XPLMSetDatai(nd_range, RANGE_20);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		} else if ( ( dist < 0.10f * 0.8f ) && ( dist > 0.0f ) ) {
			XPLMSetDatai(nd_range, RANGE_10);
			XPLMSetDatai(nd_zoomin, ZOOMIN_ON);
		}
    }
    return (XPLMCommandCallback_f)1;
}


// clock
XPLMCommandCallback_f clock_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)((intptr_t)inRefcon);
        if ( i == 0 )
        {
            if ( ( XPLMGetDataf(elapsed_time_sec) == 0.0f ) || XPLMGetDatai(timer_is_running) )
                XPLMCommandOnce(timer_start_stop);
            else
                XPLMCommandOnce(timer_reset);
        }
        else if ( i == 1 )
            XPLMCommandOnce(timer_start_stop);
        else if ( i == 2 )
            XPLMCommandOnce(timer_reset);
    }
    return (XPLMCommandCallback_f)1;
}


void registerCommands(void) {

    XPLMDebugString("XHSI: creating custom commands and registering custom command handlers\n");


    // xhsi/nd_pilot/...

    // source
    source_nav1 = XPLMCreateCommand("xhsi/nd_pilot/source_nav1", "NAV source NAV1");
    XPLMRegisterCommandHandler(source_nav1, (XPLMCommandCallback_f)source_handler, 1, (void *)SOURCE_NAV1);
    source_nav2 = XPLMCreateCommand("xhsi/nd_pilot/source_nav2", "NAV source NAV2");
    XPLMRegisterCommandHandler(source_nav2, (XPLMCommandCallback_f)source_handler, 1, (void *)SOURCE_NAV2);
    source_fmc = XPLMCreateCommand("xhsi/nd_pilot/source_fmc", "NAV source FMC");
    XPLMRegisterCommandHandler(source_fmc, (XPLMCommandCallback_f)source_handler, 1, (void *)SOURCE_FMC);
    source_down = XPLMCreateCommand("xhsi/nd_pilot/source_down", "Previous NAV source");
    XPLMRegisterCommandHandler(source_down, (XPLMCommandCallback_f)source_handler, 1, (void *)DOWN);
    source_up = XPLMCreateCommand("xhsi/nd_pilot/source_up", "Next NAV source");
    XPLMRegisterCommandHandler(source_up, (XPLMCommandCallback_f)source_handler, 1, (void *)UP);
    source_cycle = XPLMCreateCommand("xhsi/nd_pilot/source_cycle", "Cycle through NAV sources");
    XPLMRegisterCommandHandler(source_cycle, (XPLMCommandCallback_f)source_handler, 1, (void *)CYCLE);

    // mode
    mode_app = XPLMCreateCommand("xhsi/nd_pilot/mode_app", "ND mode APP");
    XPLMRegisterCommandHandler(mode_app, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_APP);
    mode_vor = XPLMCreateCommand("xhsi/nd_pilot/mode_vor", "ND mode VOR");
    XPLMRegisterCommandHandler(mode_vor, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_VOR);
    mode_map = XPLMCreateCommand("xhsi/nd_pilot/mode_map", "ND mode MAP");
    XPLMRegisterCommandHandler(mode_map, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_MAP);
    mode_nav = XPLMCreateCommand("xhsi/nd_pilot/mode_nav", "ND mode NAV");
    XPLMRegisterCommandHandler(mode_nav, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_NAV);
    mode_pln = XPLMCreateCommand("xhsi/nd_pilot/mode_pln", "ND mode PLN");
    XPLMRegisterCommandHandler(mode_pln, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_PLN);
    mode_down = XPLMCreateCommand("xhsi/nd_pilot/mode_down", "Previous ND mode");
    XPLMRegisterCommandHandler(mode_down, (XPLMCommandCallback_f)mode_handler, 1, (void *)DOWN);
    mode_up = XPLMCreateCommand("xhsi/nd_pilot/mode_up", "Next ND mode");
    XPLMRegisterCommandHandler(mode_up, (XPLMCommandCallback_f)mode_handler, 1, (void *)UP);
    mode_cycle = XPLMCreateCommand("xhsi/nd_pilot/mode_cycle", "Cycle through ND modes");
    XPLMRegisterCommandHandler(mode_cycle, (XPLMCommandCallback_f)mode_handler, 1, (void *)CYCLE);
    mode_shuttle = XPLMCreateCommand("xhsi/nd_pilot/mode_shuttle", "Shuttle back and forth through ND modes");
    XPLMRegisterCommandHandler(mode_shuttle, (XPLMCommandCallback_f)mode_handler, 1, (void *)SHUTTLE);

    // ctr
    ctr_toggle = XPLMCreateCommand("xhsi/nd_pilot/mode_ctr_toggle", "Toggle ND map CTR");
    XPLMRegisterCommandHandler(ctr_toggle, (XPLMCommandCallback_f)ctr_handler, 1, (void *)TOGGLE);
    ctr_on = XPLMCreateCommand("xhsi/nd_pilot/mode_ctr_on", "ND map CTR on");
    XPLMRegisterCommandHandler(ctr_on, (XPLMCommandCallback_f)ctr_handler, 1, (void *)MODE_CENTERED);
    ctr_off = XPLMCreateCommand("xhsi/nd_pilot/mode_ctr_off", "ND map CTR off");
    XPLMRegisterCommandHandler(ctr_off, (XPLMCommandCallback_f)ctr_handler, 1, (void *)MODE_EXPANDED);

    // range
    range_10 = XPLMCreateCommand("xhsi/nd_pilot/range_10", "ND map range 10");
    XPLMRegisterCommandHandler(range_10, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_10);
    range_20 = XPLMCreateCommand("xhsi/nd_pilot/range_20", "ND map range 20");
    XPLMRegisterCommandHandler(range_20, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_20);
    range_40 = XPLMCreateCommand("xhsi/nd_pilot/range_40", "ND map range 40");
    XPLMRegisterCommandHandler(range_40, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_40);
    range_80 = XPLMCreateCommand("xhsi/nd_pilot/range_80", "ND map range 80");
    XPLMRegisterCommandHandler(range_80, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_80);
    range_160 = XPLMCreateCommand("xhsi/nd_pilot/range_160", "ND map range 160");
    XPLMRegisterCommandHandler(range_160, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_160);
    range_320 = XPLMCreateCommand("xhsi/nd_pilot/range_320", "ND map range 320");
    XPLMRegisterCommandHandler(range_320, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_320);
    range_640 = XPLMCreateCommand("xhsi/nd_pilot/range_640", "ND map range 640");
    XPLMRegisterCommandHandler(range_640, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_640);
    range_down = XPLMCreateCommand("xhsi/nd_pilot/range_down", "Decrease ND map range");
    XPLMRegisterCommandHandler(range_down, (XPLMCommandCallback_f)range_handler, 1, (void *)DOWN);
    range_up = XPLMCreateCommand("xhsi/nd_pilot/range_up", "Increase ND map range");
    XPLMRegisterCommandHandler(range_up, (XPLMCommandCallback_f)range_handler, 1, (void *)UP);
    range_cycle = XPLMCreateCommand("xhsi/nd_pilot/range_cycle", "Cycle through ND map ranges");
    XPLMRegisterCommandHandler(range_cycle, (XPLMCommandCallback_f)range_handler, 1, (void *)CYCLE);
    range_shuttle = XPLMCreateCommand("xhsi/nd_pilot/range_shuttle", "Shuttle back and forth through ND map ranges");
    XPLMRegisterCommandHandler(range_shuttle, (XPLMCommandCallback_f)range_handler, 1, (void *)SHUTTLE);

    // radio1
    radio1_adf = XPLMCreateCommand("xhsi/nd_pilot/radio1_adf", "ND radio1 ADF1");
    XPLMRegisterCommandHandler(radio1_adf, (XPLMCommandCallback_f)radio1_handler, 1, (void *)RADIO_ADF);
    radio1_off = XPLMCreateCommand("xhsi/nd_pilot/radio1_off", "ND radio1 OFF");
    XPLMRegisterCommandHandler(radio1_off, (XPLMCommandCallback_f)radio1_handler, 1, (void *)RADIO_OFF);
    radio1_nav = XPLMCreateCommand("xhsi/nd_pilot/radio1_nav", "ND radio1 NAV1");
    XPLMRegisterCommandHandler(radio1_nav, (XPLMCommandCallback_f)radio1_handler, 1, (void *)RADIO_NAV);
    radio1_down = XPLMCreateCommand("xhsi/nd_pilot/radio1_down", "Previous ND radio1");
    XPLMRegisterCommandHandler(radio1_down, (XPLMCommandCallback_f)radio1_handler, 1, (void *)DOWN);
    radio1_up = XPLMCreateCommand("xhsi/nd_pilot/radio1_up", "Next ND radio1");
    XPLMRegisterCommandHandler(radio1_up, (XPLMCommandCallback_f)radio1_handler, 1, (void *)UP);
//    radio1_cycle = XPLMCreateCommand("xhsi/nd_pilot/radio1_cycle", "Cycle ND radio1");
//    XPLMRegisterCommandHandler(radio1_cycle, (XPLMCommandCallback_f)radio1_handler, 1, (void *)CYCLE);
    radio1_shuttle = XPLMCreateCommand("xhsi/nd_pilot/radio1_shuttle", "Shuttle ND radio1");
    XPLMRegisterCommandHandler(radio1_shuttle, (XPLMCommandCallback_f)radio1_handler, 1, (void *)SHUTTLE);

    // radio2
    radio2_adf = XPLMCreateCommand("xhsi/nd_pilot/radio2_adf", "ND radio2 ADF");
    XPLMRegisterCommandHandler(radio2_adf, (XPLMCommandCallback_f)radio2_handler, 1, (void *)RADIO_ADF);
    radio2_off = XPLMCreateCommand("xhsi/nd_pilot/radio2_off", "ND radio2 OFF");
    XPLMRegisterCommandHandler(radio2_off, (XPLMCommandCallback_f)radio2_handler, 1, (void *)RADIO_OFF);
    radio2_nav = XPLMCreateCommand("xhsi/nd_pilot/radio2_nav", "ND radio2 NAV");
    XPLMRegisterCommandHandler(radio2_nav, (XPLMCommandCallback_f)radio2_handler, 1, (void *)RADIO_NAV);
    radio2_down = XPLMCreateCommand("xhsi/nd_pilot/radio2_down", "Previous ND radio2");
    XPLMRegisterCommandHandler(radio2_down, (XPLMCommandCallback_f)radio2_handler, 1, (void *)DOWN);
    radio2_up = XPLMCreateCommand("xhsi/nd_pilot/radio2_up", "Next ND radio2");
    XPLMRegisterCommandHandler(radio2_up, (XPLMCommandCallback_f)radio2_handler, 1, (void *)UP);
//    radio2_cycle = XPLMCreateCommand("xhsi/nd_pilot/radio2_cycle", "Cycle ND radio2");
//    XPLMRegisterCommandHandler(radio2_cycle, (XPLMCommandCallback_f)radio2_handler, 1, (void *)CYCLE);
    radio2_shuttle = XPLMCreateCommand("xhsi/nd_pilot/radio2_shuttle", "Shuttle ND radio2");
    XPLMRegisterCommandHandler(radio2_shuttle, (XPLMCommandCallback_f)radio2_handler, 1, (void *)SHUTTLE);

    // tfc
    tfc_toggle = XPLMCreateCommand("xhsi/nd_pilot/tfc_toggle", "Toggle ND symbols TFC");
    XPLMRegisterCommandHandler(tfc_toggle, (XPLMCommandCallback_f)tfc_handler, 1, (void *)TOGGLE);
    tfc_on = XPLMCreateCommand("xhsi/nd_pilot/tfc_on", "ND symbols TFC on");
    XPLMRegisterCommandHandler(tfc_on, (XPLMCommandCallback_f)tfc_handler, 1, (void *)ON);
    tfc_off = XPLMCreateCommand("xhsi/nd_pilot/tfc_off", "ND symbols TFC off");
    XPLMRegisterCommandHandler(tfc_off, (XPLMCommandCallback_f)tfc_handler, 1, (void *)OFF);

    // arpt
    arpt_toggle = XPLMCreateCommand("xhsi/nd_pilot/arpt_toggle", "Toggle ND symbols ARPT");
    XPLMRegisterCommandHandler(arpt_toggle, (XPLMCommandCallback_f)arpt_handler, 1, (void *)TOGGLE);
    arpt_on = XPLMCreateCommand("xhsi/nd_pilot/arpt_on", "ND symbols ARPT on");
    XPLMRegisterCommandHandler(arpt_on, (XPLMCommandCallback_f)arpt_handler, 1, (void *)ON);
    arpt_off = XPLMCreateCommand("xhsi/nd_pilot/arpt_off", "ND symbols ARPT off");
    XPLMRegisterCommandHandler(arpt_off, (XPLMCommandCallback_f)arpt_handler, 1, (void *)OFF);

    // wpt
    wpt_toggle = XPLMCreateCommand("xhsi/nd_pilot/wpt_toggle", "Toggle ND symbols WPT");
    XPLMRegisterCommandHandler(wpt_toggle, (XPLMCommandCallback_f)wpt_handler, 1, (void *)TOGGLE);
    wpt_on = XPLMCreateCommand("xhsi/nd_pilot/wpt_on", "ND symbols WPT on");
    XPLMRegisterCommandHandler(wpt_on, (XPLMCommandCallback_f)wpt_handler, 1, (void *)ON);
    wpt_off = XPLMCreateCommand("xhsi/nd_pilot/wpt_off", "ND symbols WPT off");
    XPLMRegisterCommandHandler(wpt_off, (XPLMCommandCallback_f)wpt_handler, 1, (void *)OFF);

    // vor
    vor_toggle = XPLMCreateCommand("xhsi/nd_pilot/vor_toggle", "Toggle ND symbols VOR");
    XPLMRegisterCommandHandler(vor_toggle, (XPLMCommandCallback_f)vor_handler, 1, (void *)TOGGLE);
    vor_on = XPLMCreateCommand("xhsi/nd_pilot/vor_on", "ND symbols VOR on");
    XPLMRegisterCommandHandler(vor_on, (XPLMCommandCallback_f)vor_handler, 1, (void *)ON);
    vor_off = XPLMCreateCommand("xhsi/nd_pilot/vor_off", "ND symbols VOR off");
    XPLMRegisterCommandHandler(vor_off, (XPLMCommandCallback_f)vor_handler, 1, (void *)OFF);

    // ndb
    ndb_toggle = XPLMCreateCommand("xhsi/nd_pilot/ndb_toggle", "Toggle ND symbols NDB");
    XPLMRegisterCommandHandler(ndb_toggle, (XPLMCommandCallback_f)ndb_handler, 1, (void *)TOGGLE);
    ndb_on = XPLMCreateCommand("xhsi/nd_pilot/ndb_on", "ND symbols NDB on");
    XPLMRegisterCommandHandler(ndb_on, (XPLMCommandCallback_f)ndb_handler, 1, (void *)ON);
    ndb_off = XPLMCreateCommand("xhsi/nd_pilot/ndb_off", "ND symbols NDB off");
    XPLMRegisterCommandHandler(ndb_off, (XPLMCommandCallback_f)ndb_handler, 1, (void *)OFF);

    // sta = vor + ndb
    sta_toggle = XPLMCreateCommand("xhsi/nd_pilot/sta_toggle", "Toggle ND symbols STA");
    XPLMRegisterCommandHandler(sta_toggle, (XPLMCommandCallback_f)sta_handler, 1, (void *)TOGGLE);
    sta_on = XPLMCreateCommand("xhsi/nd_pilot/sta_on", "ND symbols STA on");
    XPLMRegisterCommandHandler(sta_on, (XPLMCommandCallback_f)sta_handler, 1, (void *)ON);
    sta_off = XPLMCreateCommand("xhsi/nd_pilot/sta_off", "ND symbols STA off");
    XPLMRegisterCommandHandler(sta_off, (XPLMCommandCallback_f)sta_handler, 1, (void *)OFF);
    sta_toggle = XPLMCreateCommand("xhsi/nd_pilot/sta_cycle", "Cycle ND symbols STA");
    XPLMRegisterCommandHandler(sta_toggle, (XPLMCommandCallback_f)sta_handler, 1, (void *)CYCLE);

    // data = route data
    data_toggle = XPLMCreateCommand("xhsi/nd_pilot/data_toggle", "Toggle ND symbols DATA");
    XPLMRegisterCommandHandler(data_toggle, (XPLMCommandCallback_f)data_handler, 1, (void *)TOGGLE);
    data_on = XPLMCreateCommand("xhsi/nd_pilot/data_on", "ND symbols DATA on");
    XPLMRegisterCommandHandler(data_on, (XPLMCommandCallback_f)data_handler, 1, (void *)ON);
    data_off = XPLMCreateCommand("xhsi/nd_pilot/data_off", "ND symbols DATA off");
    XPLMRegisterCommandHandler(data_off, (XPLMCommandCallback_f)data_handler, 1, (void *)OFF);

    // pos
    pos_toggle = XPLMCreateCommand("xhsi/nd_pilot/pos_toggle", "Toggle ND symbols POS");
    XPLMRegisterCommandHandler(pos_toggle, (XPLMCommandCallback_f)pos_handler, 1, (void *)TOGGLE);
    pos_on = XPLMCreateCommand("xhsi/nd_pilot/pos_on", "ND symbols POS on");
    XPLMRegisterCommandHandler(pos_on, (XPLMCommandCallback_f)pos_handler, 1, (void *)ON);
    pos_off = XPLMCreateCommand("xhsi/nd_pilot/pos_off", "ND symbols POS off");
    XPLMRegisterCommandHandler(pos_off, (XPLMCommandCallback_f)pos_handler, 1, (void *)OFF);



    // xhsi/nd_ext_range_pilot/...

    // zoomin
    zoom_x100_toggle = XPLMCreateCommand("xhsi/nd_ext_range_pilot/zoom_x100_toggle", "Toggle ND map zoom x100");
    XPLMRegisterCommandHandler(zoom_x100_toggle, (XPLMCommandCallback_f)zoom_x100_handler, 1, (void *)TOGGLE);
    zoom_x100_on = XPLMCreateCommand("xhsi/nd_ext_range_pilot/zoom_x100_on", "ND map range zoom in x100");
    XPLMRegisterCommandHandler(zoom_x100_on, (XPLMCommandCallback_f)zoom_x100_handler, 1, (void *)ON);
    zoom_x100_off = XPLMCreateCommand("xhsi/nd_ext_range_pilot/zoom_x100_off", "ND map range normal");
    XPLMRegisterCommandHandler(zoom_x100_off, (XPLMCommandCallback_f)zoom_x100_handler, 1, (void *)OFF);

    // ext range
    ext_range_0_10 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_0.10", "ND extended map range 0.10");
    XPLMRegisterCommandHandler(ext_range_0_10, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_10 + 1100));
    ext_range_0_20 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_0.20", "ND extended map range 0.20");
    XPLMRegisterCommandHandler(ext_range_0_20, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_20 + 1100));
    ext_range_0_40 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_0.40", "ND extended map range 0.40");
    XPLMRegisterCommandHandler(ext_range_0_40, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_40 + 1100));
    ext_range_0_80 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_0.80", "ND extended map range 0.80");
    XPLMRegisterCommandHandler(ext_range_0_80, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_80 + 1100));
    ext_range_1_60 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_1.60", "ND extended map range 1.60");
    XPLMRegisterCommandHandler(ext_range_1_60, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_160 + 1100));
    ext_range_3_20 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_3.20", "ND extended map range 3.20");
    XPLMRegisterCommandHandler(ext_range_3_20, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_320 + 1100));
    ext_range_6_40 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_6.40", "ND extended map range 6.40");
    XPLMRegisterCommandHandler(ext_range_6_40, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_640 + 1100));
    ext_range_010 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_010", "ND extended map range 10");
    XPLMRegisterCommandHandler(ext_range_010, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_10 + 1000));
    ext_range_020 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_020", "ND extended map range 20");
    XPLMRegisterCommandHandler(ext_range_020, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_20 + 1000));
    ext_range_040 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_040", "ND extended map range 40");
    XPLMRegisterCommandHandler(ext_range_040, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_40 + 1000));
    ext_range_080 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_080", "ND extended map range 80");
    XPLMRegisterCommandHandler(ext_range_080, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_80 + 1000));
    ext_range_160 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_160", "ND extended map range 160");
    XPLMRegisterCommandHandler(ext_range_160, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_160 + 1000));
    ext_range_320 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_320", "ND extended map range 320");
    XPLMRegisterCommandHandler(ext_range_320, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_320 + 1000));
    ext_range_640 = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_640", "ND extended map range 640");
    XPLMRegisterCommandHandler(ext_range_640, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_640 + 1000));
    ext_range_down = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_down", "Decrease ND extended map range");
    XPLMRegisterCommandHandler(ext_range_down, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)DOWN);
    ext_range_up = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_up", "Increase ND extended map range");
    XPLMRegisterCommandHandler(ext_range_up, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)UP);
    ext_range_cycle = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_cycle", "Cycle through ND extended map ranges");
    XPLMRegisterCommandHandler(ext_range_cycle, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)CYCLE);
    ext_range_shuttle = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_shuttle", "Shuttle back and forth through ND extended map ranges");
    XPLMRegisterCommandHandler(ext_range_shuttle, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)SHUTTLE);



    // xhsi/nd_b737classic_pilot/...

    // B737-Classic modes
    b737cl_mode_fullvorils = XPLMCreateCommand("xhsi/nd_b737classic_pilot/mode_fullvorils", "B737-Classic mode FULL VOR/ILS");
    XPLMRegisterCommandHandler(b737cl_mode_fullvorils, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_FULL_VOR_ILS);
    b737cl_mode_expvorils = XPLMCreateCommand("xhsi/nd_b737classic_pilot/mode_expvorils", "B737-Classic mode EXP VOR/ILS");
    XPLMRegisterCommandHandler(b737cl_mode_expvorils, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_EXP_VOR_ILS);
    b737cl_mode_map = XPLMCreateCommand("xhsi/nd_b737classic_pilot/mode_map", "B737-Classic mode MAP");
    XPLMRegisterCommandHandler(b737cl_mode_map, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_MAP);
    b737cl_mode_ctrmap = XPLMCreateCommand("xhsi/nd_b737classic_pilot/mode_ctrmap", "B737-Classic mode CTR MAP");
    XPLMRegisterCommandHandler(b737cl_mode_ctrmap, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_CTR_MAP);
    b737cl_mode_plan = XPLMCreateCommand("xhsi/nd_b737classic_pilot/mode_plan", "B737-Classic mode PLAN");
    XPLMRegisterCommandHandler(b737cl_mode_plan, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_PLAN);



    // xhsi/pfd_pilot/...

    // mins mode
    mins_toggle = XPLMCreateCommand("xhsi/pfd_pilot/mins_toggle", "Toggle MINS selector");
    XPLMRegisterCommandHandler(mins_toggle, (XPLMCommandCallback_f)mins_mode_handler, 1, (void *)TOGGLE);
    mins_radio = XPLMCreateCommand("xhsi/pfd_pilot/mins_radio", "Select RADIO MINS");
    XPLMRegisterCommandHandler(mins_radio, (XPLMCommandCallback_f)mins_mode_handler, 1, (void *)MINS_RADIO);
    mins_baro = XPLMCreateCommand("xhsi/pfd_pilot/mins_baro", "Select BARO MINS");
    XPLMRegisterCommandHandler(mins_baro, (XPLMCommandCallback_f)mins_mode_handler, 1, (void *)MINS_BARO);

    // mins value
    mins_reset = XPLMCreateCommand("xhsi/pfd_pilot/mins_reset", "Reset MINS value");
    XPLMRegisterCommandHandler(mins_reset, (XPLMCommandCallback_f)mins_value_handler, 1, (void *)OFF);
    mins_down = XPLMCreateCommand("xhsi/pfd_pilot/mins_down", "Decrease MINS value");
    XPLMRegisterCommandHandler(mins_down, (XPLMCommandCallback_f)mins_value_handler, 1, (void *)DOWN);
    mins_up = XPLMCreateCommand("xhsi/pfd_pilot/mins_up", "Increase MINS value");
    XPLMRegisterCommandHandler(mins_up, (XPLMCommandCallback_f)mins_value_handler, 1, (void *)UP);



    // copilot commands

    // xhsi/nd_copilot/...

    // copilot source
    copilot_source_nav1 = XPLMCreateCommand("xhsi/nd_copilot/source_nav1", "NAV source NAV1 - copilot");
    XPLMRegisterCommandHandler(copilot_source_nav1, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)SOURCE_NAV1);
    copilot_source_nav2 = XPLMCreateCommand("xhsi/nd_copilot/source_nav2", "NAV source NAV2 - copilot");
    XPLMRegisterCommandHandler(copilot_source_nav2, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)SOURCE_NAV2);
    copilot_source_fmc = XPLMCreateCommand("xhsi/nd_copilot/source_fmc", "NAV source FMC - copilot");
    XPLMRegisterCommandHandler(copilot_source_fmc, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)SOURCE_FMC);
    copilot_source_down = XPLMCreateCommand("xhsi/nd_copilot/source_down", "Previous NAV source - copilot");
    XPLMRegisterCommandHandler(copilot_source_down, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)DOWN);
    copilot_source_up = XPLMCreateCommand("xhsi/nd_copilot/source_up", "Next NAV source - copilot");
    XPLMRegisterCommandHandler(copilot_source_up, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)UP);
    copilot_source_cycle = XPLMCreateCommand("xhsi/nd_copilot/source_cycle", "Cycle through NAV sources - copilot");
    XPLMRegisterCommandHandler(copilot_source_cycle, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)CYCLE);

    // copilot mode
    copilot_mode_app = XPLMCreateCommand("xhsi/nd_copilot/mode_app", "ND mode APP - copilot");
    XPLMRegisterCommandHandler(copilot_mode_app, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_APP);
    copilot_mode_vor = XPLMCreateCommand("xhsi/nd_copilot/mode_vor", "ND mode VOR - copilot");
    XPLMRegisterCommandHandler(copilot_mode_vor, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_VOR);
    copilot_mode_map = XPLMCreateCommand("xhsi/nd_copilot/mode_map", "ND mode MAP - copilot");
    XPLMRegisterCommandHandler(copilot_mode_map, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_MAP);
    copilot_mode_nav = XPLMCreateCommand("xhsi/nd_copilot/mode_nav", "ND mode NAV - copilot");
    XPLMRegisterCommandHandler(copilot_mode_nav, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_NAV);
    copilot_mode_pln = XPLMCreateCommand("xhsi/nd_copilot/mode_pln", "ND mode PLN - copilot");
    XPLMRegisterCommandHandler(copilot_mode_pln, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_PLN);
    copilot_mode_down = XPLMCreateCommand("xhsi/nd_copilot/mode_down", "Previous ND mode - copilot");
    XPLMRegisterCommandHandler(copilot_mode_down, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)DOWN);
    copilot_mode_up = XPLMCreateCommand("xhsi/nd_copilot/mode_up", "Next ND mode - copilot");
    XPLMRegisterCommandHandler(copilot_mode_up, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)UP);
    copilot_mode_cycle = XPLMCreateCommand("xhsi/nd_copilot/mode_cycle", "Cycle through ND modes - copilot");
    XPLMRegisterCommandHandler(copilot_mode_cycle, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)CYCLE);
    copilot_mode_shuttle = XPLMCreateCommand("xhsi/nd_copilot/mode_shuttle", "Shuttle back and forth through ND modes - copilot");
    XPLMRegisterCommandHandler(copilot_mode_shuttle, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)SHUTTLE);

    // copilot ctr
    copilot_ctr_toggle = XPLMCreateCommand("xhsi/nd_copilot/mode_ctr_toggle", "Toggle ND map CTR - copilot");
    XPLMRegisterCommandHandler(copilot_ctr_toggle, (XPLMCommandCallback_f)copilot_ctr_handler, 1, (void *)TOGGLE);
    copilot_ctr_on = XPLMCreateCommand("xhsi/nd_copilot/mode_ctr_on", "ND map CTR on - copilot");
    XPLMRegisterCommandHandler(copilot_ctr_on, (XPLMCommandCallback_f)copilot_ctr_handler, 1, (void *)MODE_CENTERED);
    copilot_ctr_off = XPLMCreateCommand("xhsi/nd_copilot/mode_ctr_off", "ND map CTR off - copilot");
    XPLMRegisterCommandHandler(copilot_ctr_off, (XPLMCommandCallback_f)copilot_ctr_handler, 1, (void *)MODE_EXPANDED);

    // copilot range
    copilot_range_10 = XPLMCreateCommand("xhsi/nd_copilot/range_10", "ND map range 10 - copilot");
    XPLMRegisterCommandHandler(copilot_range_10, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_10);
    copilot_range_20 = XPLMCreateCommand("xhsi/nd_copilot/range_20", "ND map range 20 - copilot");
    XPLMRegisterCommandHandler(copilot_range_20, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_20);
    copilot_range_40 = XPLMCreateCommand("xhsi/nd_copilot/range_40", "ND map range 40 - copilot");
    XPLMRegisterCommandHandler(copilot_range_40, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_40);
    copilot_range_80 = XPLMCreateCommand("xhsi/nd_copilot/range_80", "ND map range 80 - copilot");
    XPLMRegisterCommandHandler(copilot_range_80, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_80);
    copilot_range_160 = XPLMCreateCommand("xhsi/nd_copilot/range_160", "ND map range 160 - copilot");
    XPLMRegisterCommandHandler(copilot_range_160, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_160);
    copilot_range_320 = XPLMCreateCommand("xhsi/nd_copilot/range_320", "ND map range 320 - copilot");
    XPLMRegisterCommandHandler(copilot_range_320, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_320);
    copilot_range_640 = XPLMCreateCommand("xhsi/nd_copilot/range_640", "ND map range 640 - copilot");
    XPLMRegisterCommandHandler(copilot_range_640, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_640);
    copilot_range_down = XPLMCreateCommand("xhsi/nd_copilot/range_down", "Decrease ND map range - copilot");
    XPLMRegisterCommandHandler(copilot_range_down, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)DOWN);
    copilot_range_up = XPLMCreateCommand("xhsi/nd_copilot/range_up", "Increase ND map range - copilot");
    XPLMRegisterCommandHandler(copilot_range_up, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)UP);
    copilot_range_cycle = XPLMCreateCommand("xhsi/nd_copilot/range_cycle", "Cycle through ND map ranges - copilot");
    XPLMRegisterCommandHandler(copilot_range_cycle, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)CYCLE);
    copilot_range_shuttle = XPLMCreateCommand("xhsi/nd_copilot/range_shuttle", "Shuttle back and forth through ND map ranges - copilot");
    XPLMRegisterCommandHandler(copilot_range_shuttle, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)SHUTTLE);

    // copilot radio1
    copilot_radio1_adf = XPLMCreateCommand("xhsi/nd_copilot/radio1_adf", "ND radio1 ADF1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_adf, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)RADIO_ADF);
    copilot_radio1_off = XPLMCreateCommand("xhsi/nd_copilot/radio1_off", "ND radio1 OFF - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_off, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)RADIO_OFF);
    copilot_radio1_nav = XPLMCreateCommand("xhsi/nd_copilot/radio1_nav", "ND radio1 NAV1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_nav, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)RADIO_NAV);
    copilot_radio1_down = XPLMCreateCommand("xhsi/nd_copilot/radio1_down", "Previous ND radio1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_down, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)DOWN);
    copilot_radio1_up = XPLMCreateCommand("xhsi/nd_copilot/radio1_up", "Next ND radio1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_up, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)UP);
//    copilot_radio1_cycle = XPLMCreateCommand("xhsi/nd_copilot/radio1_cycle", "Cycle ND radio1 - copilot");
//    XPLMRegisterCommandHandler(copilot_radio1_cycle, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)CYCLE);
    copilot_radio1_shuttle = XPLMCreateCommand("xhsi/nd_copilot/radio1_shuttle", "Shuttle ND radio1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_shuttle, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)SHUTTLE);

    // copilot radio2
    copilot_radio2_adf = XPLMCreateCommand("xhsi/nd_copilot/radio2_adf", "ND radio2 ADF2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_adf, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)RADIO_ADF);
    copilot_radio2_off = XPLMCreateCommand("xhsi/nd_copilot/radio2_off", "ND radio2 OFF - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_off, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)RADIO_OFF);
    copilot_radio2_nav = XPLMCreateCommand("xhsi/nd_copilot/radio2_nav", "ND radio2 NAV2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_nav, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)RADIO_NAV);
    copilot_radio2_down = XPLMCreateCommand("xhsi/nd_copilot/radio2_down", "Previous ND radio2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_down, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)DOWN);
    copilot_radio2_up = XPLMCreateCommand("xhsi/nd_copilot/radio2_up", "Next ND radio2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_up, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)UP);
//    copilot_radio2_cycle = XPLMCreateCommand("xhsi/nd_copilot/radio2_cycle", "Cycle ND radio2 - copilot");
//    XPLMRegisterCommandHandler(copilot_radio2_cycle, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)CYCLE);
    copilot_radio2_shuttle = XPLMCreateCommand("xhsi/nd_copilot/radio2_shuttle", "Shuttle ND radio2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_shuttle, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)SHUTTLE);

    // copilot tfc
    copilot_tfc_toggle = XPLMCreateCommand("xhsi/nd_copilot/tfc_toggle", "Toggle ND symbols TFC - copilot");
    XPLMRegisterCommandHandler(copilot_tfc_toggle, (XPLMCommandCallback_f)copilot_tfc_handler, 1, (void *)TOGGLE);
    copilot_tfc_on = XPLMCreateCommand("xhsi/nd_copilot/tfc_on", "ND symbols TFC on - copilot");
    XPLMRegisterCommandHandler(copilot_tfc_on, (XPLMCommandCallback_f)copilot_tfc_handler, 1, (void *)ON);
    copilot_tfc_off = XPLMCreateCommand("xhsi/nd_copilot/tfc_off", "ND symbols TFC off - copilot");
    XPLMRegisterCommandHandler(copilot_tfc_off, (XPLMCommandCallback_f)copilot_tfc_handler, 1, (void *)OFF);

    // copilot arpt
    copilot_arpt_toggle = XPLMCreateCommand("xhsi/nd_copilot/arpt_toggle", "Toggle ND symbols ARPT - copilot");
    XPLMRegisterCommandHandler(copilot_arpt_toggle, (XPLMCommandCallback_f)copilot_arpt_handler, 1, (void *)TOGGLE);
    copilot_arpt_on = XPLMCreateCommand("xhsi/nd_copilot/arpt_on", "ND symbols ARPT on - copilot");
    XPLMRegisterCommandHandler(copilot_arpt_on, (XPLMCommandCallback_f)copilot_arpt_handler, 1, (void *)ON);
    copilot_arpt_off = XPLMCreateCommand("xhsi/nd_copilot/arpt_off", "ND symbols ARPT off - copilot");
    XPLMRegisterCommandHandler(copilot_arpt_off, (XPLMCommandCallback_f)copilot_arpt_handler, 1, (void *)OFF);

    // copilot wpt
    copilot_wpt_toggle = XPLMCreateCommand("xhsi/nd_copilot/wpt_toggle", "Toggle ND symbols WPT - copilot");
    XPLMRegisterCommandHandler(copilot_wpt_toggle, (XPLMCommandCallback_f)copilot_wpt_handler, 1, (void *)TOGGLE);
    copilot_wpt_on = XPLMCreateCommand("xhsi/nd_copilot/wpt_on", "ND symbols WPT on - copilot");
    XPLMRegisterCommandHandler(copilot_wpt_on, (XPLMCommandCallback_f)copilot_wpt_handler, 1, (void *)ON);
    copilot_wpt_off = XPLMCreateCommand("xhsi/nd_copilot/wpt_off", "ND symbols WPT off - copilot");
    XPLMRegisterCommandHandler(copilot_wpt_off, (XPLMCommandCallback_f)copilot_wpt_handler, 1, (void *)OFF);

    // copilot vor
    copilot_vor_toggle = XPLMCreateCommand("xhsi/nd_copilot/vor_toggle", "Toggle ND symbols VOR - copilot");
    XPLMRegisterCommandHandler(copilot_vor_toggle, (XPLMCommandCallback_f)copilot_vor_handler, 1, (void *)TOGGLE);
    copilot_vor_on = XPLMCreateCommand("xhsi/nd_copilot/vor_on", "ND symbols VOR on - copilot");
    XPLMRegisterCommandHandler(copilot_vor_on, (XPLMCommandCallback_f)copilot_vor_handler, 1, (void *)ON);
    copilot_vor_off = XPLMCreateCommand("xhsi/nd_copilot/vor_off", "ND symbols VOR off - copilot");
    XPLMRegisterCommandHandler(copilot_vor_off, (XPLMCommandCallback_f)copilot_vor_handler, 1, (void *)OFF);

    // copilot ndb
    copilot_ndb_toggle = XPLMCreateCommand("xhsi/nd_copilot/ndb_toggle", "Toggle ND symbols NDB - copilot");
    XPLMRegisterCommandHandler(copilot_ndb_toggle, (XPLMCommandCallback_f)copilot_ndb_handler, 1, (void *)TOGGLE);
    copilot_ndb_on = XPLMCreateCommand("xhsi/nd_copilot/ndb_on", "ND symbols NDB on - copilot");
    XPLMRegisterCommandHandler(copilot_ndb_on, (XPLMCommandCallback_f)copilot_ndb_handler, 1, (void *)ON);
    copilot_ndb_off = XPLMCreateCommand("xhsi/nd_copilot/ndb_off", "ND symbols NDB off - copilot");
    XPLMRegisterCommandHandler(copilot_ndb_off, (XPLMCommandCallback_f)copilot_ndb_handler, 1, (void *)OFF);

    // copilot sta = vor + ndb
    copilot_sta_toggle = XPLMCreateCommand("xhsi/nd_copilot/sta_toggle", "Toggle ND symbols STA - copilot");
    XPLMRegisterCommandHandler(copilot_sta_toggle, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)TOGGLE);
    copilot_sta_on = XPLMCreateCommand("xhsi/nd_copilot/sta_on", "ND symbols STA on - copilot");
    XPLMRegisterCommandHandler(copilot_sta_on, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)ON);
    copilot_sta_off = XPLMCreateCommand("xhsi/nd_copilot/sta_off", "ND symbols STA off - copilot");
    XPLMRegisterCommandHandler(copilot_sta_off, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)OFF);
    copilot_sta_toggle = XPLMCreateCommand("xhsi/nd_copilot/sta_cycle", "Cycle ND symbols STA - copilot");
    XPLMRegisterCommandHandler(copilot_sta_cycle, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)CYCLE);

    // copilot data = route data
    copilot_data_toggle = XPLMCreateCommand("xhsi/nd_copilot/data_toggle", "Toggle ND symbols DATA - copilot");
    XPLMRegisterCommandHandler(copilot_data_toggle, (XPLMCommandCallback_f)copilot_data_handler, 1, (void *)TOGGLE);
    copilot_data_on = XPLMCreateCommand("xhsi/nd_copilot/data_on", "ND symbols DATA on - copilot");
    XPLMRegisterCommandHandler(copilot_data_on, (XPLMCommandCallback_f)copilot_data_handler, 1, (void *)ON);
    copilot_data_off = XPLMCreateCommand("xhsi/nd_copilot/data_off", "ND symbols DATA off - copilot");
    XPLMRegisterCommandHandler(copilot_data_off, (XPLMCommandCallback_f)copilot_data_handler, 1, (void *)OFF);

    // copilot pos
    copilot_pos_toggle = XPLMCreateCommand("xhsi/nd_copilot/pos_toggle", "Toggle ND symbols POS - copilot");
    XPLMRegisterCommandHandler(copilot_pos_toggle, (XPLMCommandCallback_f)copilot_pos_handler, 1, (void *)TOGGLE);
    copilot_pos_on = XPLMCreateCommand("xhsi/nd_copilot/pos_on", "ND symbols POS on - copilot");
    XPLMRegisterCommandHandler(copilot_pos_on, (XPLMCommandCallback_f)copilot_pos_handler, 1, (void *)ON);
    copilot_pos_off = XPLMCreateCommand("xhsi/nd_copilot/pos_off", "ND symbols POS off - copilot");
    XPLMRegisterCommandHandler(copilot_pos_off, (XPLMCommandCallback_f)copilot_pos_handler, 1, (void *)OFF);


    // xhsi/nd_ext_range_copilot/...

    // copilot zoomin
    copilot_zoom_x100_toggle = XPLMCreateCommand("xhsi/nd_ext_range_copilot/zoom_x100_toggle", "Toggle ND map zoom x100 - copilot");
    XPLMRegisterCommandHandler(copilot_zoom_x100_toggle, (XPLMCommandCallback_f)copilot_zoom_x100_handler, 1, (void *)TOGGLE);
    copilot_zoom_x100_on = XPLMCreateCommand("xhsi/nd_ext_range_copilot/zoom_x100_on", "ND map range zoom in x100 - copilot");
    XPLMRegisterCommandHandler(copilot_zoom_x100_on, (XPLMCommandCallback_f)copilot_zoom_x100_handler, 1, (void *)ON);
    copilot_zoom_x100_off = XPLMCreateCommand("xhsi/nd_ext_range_copilot/zoom_x100_off", "ND map range normal - copilot");
    XPLMRegisterCommandHandler(copilot_zoom_x100_off, (XPLMCommandCallback_f)copilot_zoom_x100_handler, 1, (void *)OFF);

    // copilot ext range
    copilot_ext_range_0_10 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_0.10", "ND extended map range 0.10 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_0_10, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_10 + 1100));
    copilot_ext_range_0_20 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_0.20", "ND extended map range 0.20 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_0_20, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_20 + 1100));
    copilot_ext_range_0_40 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_0.40", "ND extended map range 0.40 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_0_40, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_40 + 1100));
    copilot_ext_range_0_80 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_0.80", "ND extended map range 0.80 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_0_80, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_80 + 1100));
    copilot_ext_range_1_60 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_1.60", "ND extended map range 1.60 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_1_60, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_160 + 1100));
    copilot_ext_range_3_20 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_3.20", "ND extended map range 3.20 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_3_20, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_320 + 1100));
    copilot_ext_range_6_40 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_6.40", "ND extended map range 6.40 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_6_40, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_640 + 1100));
    copilot_ext_range_010 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_010", "ND extended map range 10 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_010, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_10 + 1000));
    copilot_ext_range_020 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_020", "ND extended map range 20 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_020, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_20 + 1000));
    copilot_ext_range_040 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_040", "ND extended map range 40 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_040, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_40 + 1000));
    copilot_ext_range_080 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_080", "ND extended map range 80 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_080, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_80 + 1000));
    copilot_ext_range_160 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_160", "ND extended map range 160 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_160, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_160 + 1000));
    copilot_ext_range_320 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_320", "ND extended map range 320 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_320, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_320 + 1000));
    copilot_ext_range_640 = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_640", "ND extended map range 640 - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_640, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_640 + 1000));
    copilot_ext_range_down = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_down", "Decrease ND extended map range - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_down, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)DOWN);
    copilot_ext_range_up = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_up", "Increase ND extended map range - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_up, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)UP);
    copilot_ext_range_cycle = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_cycle", "Cycle through ND extended map ranges - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_cycle, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)CYCLE);
    copilot_ext_range_shuttle = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_shuttle", "Shuttle back and forth through ND extended map ranges - copilot");
    XPLMRegisterCommandHandler(copilot_ext_range_shuttle, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)SHUTTLE);


    // xhsi/nd_b737classic_copilot/...

    // copilot B737-Classic modes
    b737cl_copilot_mode_fullvorils = XPLMCreateCommand("xhsi/nd_b737classic_copilot/mode_fullvorils", "B737-Classic mode FULL VOR/ILS - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_fullvorils, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_FULL_VOR_ILS);
    b737cl_copilot_mode_expvorils = XPLMCreateCommand("xhsi/nd_b737classic_copilot/mode_expvorils", "B737-Classic mode EXP VOR/ILS - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_expvorils, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_EXP_VOR_ILS);
    b737cl_copilot_mode_map = XPLMCreateCommand("xhsi/nd_b737classic_copilot/mode_map", "B737-Classic mode MAP - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_map, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_MAP);
    b737cl_copilot_mode_ctrmap = XPLMCreateCommand("xhsi/nd_b737classic_copilot/mode_ctrmap", "B737-Classic mode CTR MAP - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_ctrmap, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_CTR_MAP);
    b737cl_copilot_mode_plan = XPLMCreateCommand("xhsi/nd_b737classic_copilot/mode_plan", "B737-Classic mode PLAN - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_plan, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_PLAN);



    // xhsi/pfd_copilot/...

    // copilot mins mode
    copilot_mins_toggle = XPLMCreateCommand("xhsi/pfd_copilot/mins_toggle", "Toggle MINS selector - copilot");
    XPLMRegisterCommandHandler(copilot_mins_toggle, (XPLMCommandCallback_f)copilot_mins_mode_handler, 1, (void *)TOGGLE);
    copilot_mins_radio = XPLMCreateCommand("xhsi/pfd_copilot/mins_radio", "Select RADIO MINS - copilot");
    XPLMRegisterCommandHandler(copilot_mins_radio, (XPLMCommandCallback_f)copilot_mins_mode_handler, 1, (void *)MINS_RADIO);
    copilot_mins_baro = XPLMCreateCommand("xhsi/pfd_copilot/mins_baro", "Select BARO MINS - copilot");
    XPLMRegisterCommandHandler(copilot_mins_baro, (XPLMCommandCallback_f)copilot_mins_mode_handler, 1, (void *)MINS_BARO);

    // copilot mins value
    copilot_mins_reset = XPLMCreateCommand("xhsi/pfd_copilot/mins_reset", "Reset MINS value - copilot");
    XPLMRegisterCommandHandler(copilot_mins_reset, (XPLMCommandCallback_f)copilot_mins_value_handler, 1, (void *)OFF);
    copilot_mins_down = XPLMCreateCommand("xhsi/pfd_copilot/mins_down", "Decrease MINS value - copilot");
    XPLMRegisterCommandHandler(copilot_mins_down, (XPLMCommandCallback_f)copilot_mins_value_handler, 1, (void *)DOWN);
    copilot_mins_up = XPLMCreateCommand("xhsi/pfd_copilot/mins_up", "Increase MINS value - copilot");
    XPLMRegisterCommandHandler(copilot_mins_up, (XPLMCommandCallback_f)copilot_mins_value_handler, 1, (void *)UP);



    // xhsi/mfd/...

    // MFD mode
//    mfd_mode_taxi = XPLMCreateCommand("xhsi/mfd/mode_taxi", "MFD mode Taxi Chart");
//    XPLMRegisterCommandHandler(mfd_mode_taxi, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_TAXI);
    mfd_mode_arpt = XPLMCreateCommand("xhsi/mfd/mode_arpt", "MFD mode Airport Chart");
    XPLMRegisterCommandHandler(mfd_mode_arpt, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_ARPT);
    mfd_mode_fpln = XPLMCreateCommand("xhsi/mfd/mode_fpln", "MFD mode Flight Plan");
    XPLMRegisterCommandHandler(mfd_mode_fpln, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_FPLN);
    mfd_mode_eicas = XPLMCreateCommand("xhsi/mfd/mode_eicas", "MFD mode Lower EICAS");
    XPLMRegisterCommandHandler(mfd_mode_eicas, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_EICAS);
    mfd_mode_down = XPLMCreateCommand("xhsi/mfd/mode_down", "Previous MFD mode");
    XPLMRegisterCommandHandler(mfd_mode_down, (XPLMCommandCallback_f)mfd_handler, 1, (void *)DOWN);
    mfd_mode_up = XPLMCreateCommand("xhsi/mfd/mode_up", "Next MFD mode");
    XPLMRegisterCommandHandler(mfd_mode_up, (XPLMCommandCallback_f)mfd_handler, 1, (void *)UP);
    mfd_mode_cycle = XPLMCreateCommand("xhsi/mfd/mode_cycle", "Cycle MFD modes");
    XPLMRegisterCommandHandler(mfd_mode_cycle, (XPLMCommandCallback_f)mfd_handler, 1, (void *)CYCLE);



    // xhsi/radios/...

    // nav1_sync
    nav1_sync = XPLMCreateCommand("xhsi/radios/nav1_sync", "Sync NAV1 to LOC1/ILS1 or Direct-To VOR1");
    XPLMRegisterCommandHandler(nav1_sync, (XPLMCommandCallback_f)nav_sync_handler, 1, (void *)1);

    // nav2_sync
    nav2_sync = XPLMCreateCommand("xhsi/radios/nav2_sync", "Sync NAV2 to LOC2/ILS2 or Direct-To VOR2");
    XPLMRegisterCommandHandler(nav2_sync, (XPLMCommandCallback_f)nav_sync_handler, 1, (void *)2);


	// xhsi/nd_.../range_auto

	// xhsi/nd_pilot/range_auto
	auto_range_pilot = XPLMCreateCommand("xhsi/nd_pilot/range_auto", "Auto ND map range");
	XPLMRegisterCommandHandler(auto_range_pilot, (XPLMCommandCallback_f)auto_range_handler, 1, (void *)EFIS_PILOT);
	// xhsi/nd_copilot/range_auto
	auto_range_copilot = XPLMCreateCommand("xhsi/nd_copilot/range_auto", "Auto ND map range - copilot");
	XPLMRegisterCommandHandler(auto_range_copilot, (XPLMCommandCallback_f)auto_range_handler, 1, (void *)EFIS_COPILOT);


	// xhsi/nd_ext_range_.../ext_range_auto

	// xhsi/nd_ext_range_pilot/ext_range_auto
	auto_ext_range_pilot = XPLMCreateCommand("xhsi/nd_ext_range_pilot/ext_range_auto", "Auto ND extended map range");
	XPLMRegisterCommandHandler(auto_ext_range_pilot, (XPLMCommandCallback_f)auto_ext_range_handler, 1, (void *)EFIS_PILOT);
	// xhsi/nd_ext_range_copilot/ext_range_auto
	auto_ext_range_copilot = XPLMCreateCommand("xhsi/nd_ext_range_copilot/ext_range_auto", "Auto ND extended map range - copilot");
	XPLMRegisterCommandHandler(auto_ext_range_copilot, (XPLMCommandCallback_f)auto_ext_range_handler, 1, (void *)EFIS_COPILOT);


    // xhsi/clock/...

    // clock
    chr_start_stop_reset = XPLMCreateCommand("xhsi/clock/chr_start_stop_reset", "Chronograph start/stop/reset");
    XPLMRegisterCommandHandler(chr_start_stop_reset, (XPLMCommandCallback_f)clock_handler, 1, (void *)0);
    chr_start_stop = XPLMCreateCommand("xhsi/clock/chr_start_stop", "Chronograph start/stop");
    XPLMRegisterCommandHandler(chr_start_stop, (XPLMCommandCallback_f)clock_handler, 1, (void *)1);
    chr_reset = XPLMCreateCommand("xhsi/clock/chr_reset", "Chronograph reset");
    XPLMRegisterCommandHandler(chr_reset, (XPLMCommandCallback_f)clock_handler, 1, (void *)2);



    // special case: use these existing commands to control the chronometer
    timer_start_stop = XPLMFindCommand("sim/instruments/timer_start_stop");
    timer_reset = XPLMFindCommand("sim/instruments/timer_reset");


    // special case: use these existing commands to flip active/standby radios
    // (typos are Laminar Research's, not ours!)
    nav1_standy_flip = XPLMFindCommand("sim/radios/nav1_standy_flip");
    nav2_standy_flip = XPLMFindCommand("sim/radios/nav2_standy_flip");
    com1_standy_flip = XPLMFindCommand("sim/radios/com1_standy_flip");
    com2_standy_flip = XPLMFindCommand("sim/radios/com2_standy_flip");
    adf1_standy_flip = XPLMFindCommand("sim/radios/adf1_standy_flip");
    adf2_standy_flip = XPLMFindCommand("sim/radios/adf2_standy_flip");
	sim_transponder_transponder_ident = XPLMFindCommand("sim/transponder/transponder_ident");

    // special case: use these existing commands for an MCP
    sim_autopilot_fdir_servos_toggle = XPLMFindCommand("sim/autopilot/fdir_servos_toggle");
    sim_autopilot_autothrottle_toggle = XPLMFindCommand("sim/autopilot/autothrottle_toggle");
    sim_autopilot_level_change = XPLMFindCommand("sim/autopilot/level_change");
    sim_autopilot_heading = XPLMFindCommand("sim/autopilot/heading");
    sim_autopilot_vertical_speed = XPLMFindCommand("sim/autopilot/vertical_speed");
    sim_autopilot_nav = XPLMFindCommand("sim/autopilot/NAV");
    sim_autopilot_approach = XPLMFindCommand("sim/autopilot/approach");
    sim_autopilot_glide_slope = XPLMFindCommand("sim/autopilot/glide_slope");
    sim_autopilot_back_course = XPLMFindCommand("sim/autopilot/back_course");
    sim_autopilot_altitude_hold = XPLMFindCommand("sim/autopilot/altitude_hold");


    // special case: use these existing commands for lights
    sim_lights_nav_lights_toggle = XPLMFindCommand("sim/lights/nav_lights_toggle");
    sim_lights_beacon_lights_toggle = XPLMFindCommand("sim/lights/beacon_lights_toggle");
    sim_lights_taxi_lights_toggle = XPLMFindCommand("sim/lights/taxi_lights_toggle");
    sim_lights_strobe_lights_toggle = XPLMFindCommand("sim/lights/strobe_lights_toggle");
    sim_lights_landing_lights_toggle = XPLMFindCommand("sim/lights/landing_lights_toggle");

    // special case: use these existing commands for flaps, gear and speedbrake
    sim_flight_controls_flaps_down = XPLMFindCommand("sim/flight_controls/flaps_down");
    sim_flight_controls_flaps_up = XPLMFindCommand("sim/flight_controls/flaps_up");
    sim_flight_controls_landing_gear_toggle = XPLMFindCommand("sim/flight_controls/landing_gear_toggle");
    sim_flight_controls_speed_brakes_down_one = XPLMFindCommand("sim/flight_controls/speed_brakes_down_one");
    sim_flight_controls_speed_brakes_up_one = XPLMFindCommand("sim/flight_controls/speed_brakes_up_one");


    XPLMDebugString("XHSI: custom commands created and custom command handlers registered\n");

}


void unregisterCommands(void) {

    XPLMDebugString("XHSI: unregistering custom command handlers\n");

    // pilot commands
    // ctr
    XPLMUnregisterCommandHandler(ctr_toggle, (XPLMCommandCallback_f)ctr_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(ctr_on, (XPLMCommandCallback_f)ctr_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(ctr_off, (XPLMCommandCallback_f)ctr_handler, 1, (void *)OFF);

    // zoomin
    XPLMUnregisterCommandHandler(zoom_x100_toggle, (XPLMCommandCallback_f)zoom_x100_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(zoom_x100_on, (XPLMCommandCallback_f)zoom_x100_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(zoom_x100_off, (XPLMCommandCallback_f)zoom_x100_handler, 1, (void *)OFF);

    // range
    XPLMUnregisterCommandHandler(range_10, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_10);
    XPLMUnregisterCommandHandler(range_20, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_20);
    XPLMUnregisterCommandHandler(range_40, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_40);
    XPLMUnregisterCommandHandler(range_80, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_80);
    XPLMUnregisterCommandHandler(range_160, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_160);
    XPLMUnregisterCommandHandler(range_320, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_320);
    XPLMUnregisterCommandHandler(range_640, (XPLMCommandCallback_f)range_handler, 1, (void *)RANGE_640);
    XPLMUnregisterCommandHandler(range_down, (XPLMCommandCallback_f)range_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(range_up, (XPLMCommandCallback_f)range_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(range_cycle, (XPLMCommandCallback_f)range_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(range_shuttle, (XPLMCommandCallback_f)range_handler, 1, (void *)SHUTTLE);

    // ext range
    XPLMUnregisterCommandHandler(ext_range_0_10, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_10 + 1100));
    XPLMUnregisterCommandHandler(ext_range_0_20, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_20 + 1100));
    XPLMUnregisterCommandHandler(ext_range_0_40, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_40 + 1100));
    XPLMUnregisterCommandHandler(ext_range_0_80, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_80 + 1100));
    XPLMUnregisterCommandHandler(ext_range_1_60, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_160 + 1100));
    XPLMUnregisterCommandHandler(ext_range_3_20, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_320 + 1100));
    XPLMUnregisterCommandHandler(ext_range_6_40, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_640 + 1100));
    XPLMUnregisterCommandHandler(ext_range_010, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_10 + 1000));
    XPLMUnregisterCommandHandler(ext_range_020, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_20 + 1000));
    XPLMUnregisterCommandHandler(ext_range_040, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_40 + 1000));
    XPLMUnregisterCommandHandler(ext_range_080, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_80 + 1000));
    XPLMUnregisterCommandHandler(ext_range_160, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_160 + 1000));
    XPLMUnregisterCommandHandler(ext_range_320, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_320 + 1000));
    XPLMUnregisterCommandHandler(ext_range_640, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)(RANGE_640 + 1000));
    XPLMUnregisterCommandHandler(ext_range_down, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(ext_range_up, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(ext_range_cycle, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(ext_range_shuttle, (XPLMCommandCallback_f)ext_range_handler, 1, (void *)SHUTTLE);

    // mode
    XPLMUnregisterCommandHandler(mode_app, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_APP);
    XPLMUnregisterCommandHandler(mode_vor, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_VOR);
    XPLMUnregisterCommandHandler(mode_map, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_MAP);
    XPLMUnregisterCommandHandler(mode_nav, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_NAV);
    XPLMUnregisterCommandHandler(mode_pln, (XPLMCommandCallback_f)mode_handler, 1, (void *)MODE_PLN);
    XPLMUnregisterCommandHandler(mode_down, (XPLMCommandCallback_f)mode_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(mode_up, (XPLMCommandCallback_f)mode_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(mode_cycle, (XPLMCommandCallback_f)mode_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(mode_shuttle, (XPLMCommandCallback_f)mode_handler, 1, (void *)SHUTTLE);

    // B737-Classic modes
    XPLMUnregisterCommandHandler(b737cl_mode_fullvorils, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_FULL_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_mode_expvorils, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_EXP_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_mode_map, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_MAP);
    XPLMUnregisterCommandHandler(b737cl_mode_ctrmap, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_CTR_MAP);
    XPLMUnregisterCommandHandler(b737cl_mode_plan, (XPLMCommandCallback_f)b737cl_mode_handler, 1, (void *)B737CL_PLAN);

    // radio1
    XPLMUnregisterCommandHandler(radio1_adf, (XPLMCommandCallback_f)radio1_handler, 1, (void *)RADIO_ADF);
    XPLMUnregisterCommandHandler(radio1_off, (XPLMCommandCallback_f)radio1_handler, 1, (void *)RADIO_OFF);
    XPLMUnregisterCommandHandler(radio1_nav, (XPLMCommandCallback_f)radio1_handler, 1, (void *)RADIO_NAV);
    XPLMUnregisterCommandHandler(radio1_down, (XPLMCommandCallback_f)radio1_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(radio1_up, (XPLMCommandCallback_f)radio1_handler, 1, (void *)UP);
//    XPLMUnregisterCommandHandler(radio1_cycle, (XPLMCommandCallback_f)radio1_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(radio1_shuttle, (XPLMCommandCallback_f)radio1_handler, 1, (void *)SHUTTLE);

    // radio2
    XPLMUnregisterCommandHandler(radio2_adf, (XPLMCommandCallback_f)radio2_handler, 1, (void *)RADIO_ADF);
    XPLMUnregisterCommandHandler(radio2_off, (XPLMCommandCallback_f)radio2_handler, 1, (void *)RADIO_OFF);
    XPLMUnregisterCommandHandler(radio2_nav, (XPLMCommandCallback_f)radio2_handler, 1, (void *)RADIO_NAV);
    XPLMUnregisterCommandHandler(radio2_down, (XPLMCommandCallback_f)radio2_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(radio2_up, (XPLMCommandCallback_f)radio2_handler, 1, (void *)UP);
//    XPLMUnregisterCommandHandler(radio2_cycle, (XPLMCommandCallback_f)radio2_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(radio2_shuttle, (XPLMCommandCallback_f)radio2_handler, 1, (void *)SHUTTLE);

    // source
    XPLMUnregisterCommandHandler(source_nav1, (XPLMCommandCallback_f)source_handler, 1, (void *)SOURCE_NAV1);
    XPLMUnregisterCommandHandler(source_nav2, (XPLMCommandCallback_f)source_handler, 1, (void *)SOURCE_NAV2);
    XPLMUnregisterCommandHandler(source_fmc, (XPLMCommandCallback_f)source_handler, 1, (void *)SOURCE_FMC);
    XPLMUnregisterCommandHandler(source_down, (XPLMCommandCallback_f)source_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(source_up, (XPLMCommandCallback_f)source_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(source_cycle, (XPLMCommandCallback_f)source_handler, 1, (void *)CYCLE);

    // tfc
    XPLMUnregisterCommandHandler(tfc_toggle, (XPLMCommandCallback_f)tfc_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(tfc_on, (XPLMCommandCallback_f)tfc_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(tfc_off, (XPLMCommandCallback_f)tfc_handler, 1, (void *)OFF);

    // arpt
    XPLMUnregisterCommandHandler(arpt_toggle, (XPLMCommandCallback_f)arpt_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(arpt_on, (XPLMCommandCallback_f)arpt_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(arpt_off, (XPLMCommandCallback_f)arpt_handler, 1, (void *)OFF);

    // wpt
    XPLMUnregisterCommandHandler(wpt_toggle, (XPLMCommandCallback_f)wpt_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(wpt_on, (XPLMCommandCallback_f)wpt_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(wpt_off, (XPLMCommandCallback_f)wpt_handler, 1, (void *)OFF);

    // vor
    XPLMUnregisterCommandHandler(vor_toggle, (XPLMCommandCallback_f)vor_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(vor_on, (XPLMCommandCallback_f)vor_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(vor_off, (XPLMCommandCallback_f)vor_handler, 1, (void *)OFF);

    // ndb
    XPLMUnregisterCommandHandler(ndb_toggle, (XPLMCommandCallback_f)ndb_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(ndb_on, (XPLMCommandCallback_f)ndb_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(ndb_off, (XPLMCommandCallback_f)ndb_handler, 1, (void *)OFF);

    // sta = vor + ndb
    XPLMUnregisterCommandHandler(sta_toggle, (XPLMCommandCallback_f)sta_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(sta_on, (XPLMCommandCallback_f)sta_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(sta_off, (XPLMCommandCallback_f)sta_handler, 1, (void *)OFF);
    XPLMUnregisterCommandHandler(sta_cycle, (XPLMCommandCallback_f)sta_handler, 1, (void *)CYCLE);

    // data = route data
    XPLMUnregisterCommandHandler(data_toggle, (XPLMCommandCallback_f)data_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(data_on, (XPLMCommandCallback_f)data_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(data_off, (XPLMCommandCallback_f)data_handler, 1, (void *)OFF);

    // pos
    XPLMUnregisterCommandHandler(pos_toggle, (XPLMCommandCallback_f)pos_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(pos_on, (XPLMCommandCallback_f)pos_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(pos_off, (XPLMCommandCallback_f)pos_handler, 1, (void *)OFF);


    // copilot commands
    // copilot ctr
    XPLMUnregisterCommandHandler(copilot_ctr_toggle, (XPLMCommandCallback_f)copilot_ctr_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_ctr_on, (XPLMCommandCallback_f)copilot_ctr_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_ctr_off, (XPLMCommandCallback_f)copilot_ctr_handler, 1, (void *)OFF);

    // copilot zoomin
    XPLMUnregisterCommandHandler(copilot_zoom_x100_toggle, (XPLMCommandCallback_f)copilot_zoom_x100_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_zoom_x100_on, (XPLMCommandCallback_f)copilot_zoom_x100_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_zoom_x100_off, (XPLMCommandCallback_f)copilot_zoom_x100_handler, 1, (void *)OFF);

    // copilot range
    XPLMUnregisterCommandHandler(copilot_range_10, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_10);
    XPLMUnregisterCommandHandler(copilot_range_20, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_20);
    XPLMUnregisterCommandHandler(copilot_range_40, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_40);
    XPLMUnregisterCommandHandler(copilot_range_80, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_80);
    XPLMUnregisterCommandHandler(copilot_range_160, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_160);
    XPLMUnregisterCommandHandler(copilot_range_320, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_320);
    XPLMUnregisterCommandHandler(copilot_range_640, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)RANGE_640);
    XPLMUnregisterCommandHandler(copilot_range_down, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(copilot_range_up, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(copilot_range_cycle, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(copilot_range_shuttle, (XPLMCommandCallback_f)copilot_range_handler, 1, (void *)SHUTTLE);

    // copilot ext range
    XPLMUnregisterCommandHandler(copilot_ext_range_0_10, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_10 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_0_20, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_20 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_0_40, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_40 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_0_80, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_80 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_1_60, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_160 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_3_20, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_320 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_6_40, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_640 + 1100));
    XPLMUnregisterCommandHandler(copilot_ext_range_010, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_10 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_020, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_20 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_040, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_40 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_080, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_80 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_160, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_160 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_320, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_320 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_640, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)(RANGE_640 + 1000));
    XPLMUnregisterCommandHandler(copilot_ext_range_down, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(copilot_ext_range_up, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(copilot_ext_range_cycle, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(copilot_ext_range_shuttle, (XPLMCommandCallback_f)copilot_ext_range_handler, 1, (void *)SHUTTLE);

    // copilot mode
    XPLMUnregisterCommandHandler(copilot_mode_app, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_APP);
    XPLMUnregisterCommandHandler(copilot_mode_vor, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_VOR);
    XPLMUnregisterCommandHandler(copilot_mode_map, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_MAP);
    XPLMUnregisterCommandHandler(copilot_mode_nav, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_NAV);
    XPLMUnregisterCommandHandler(copilot_mode_pln, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)MODE_PLN);
    XPLMUnregisterCommandHandler(copilot_mode_down, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(copilot_mode_up, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(copilot_mode_cycle, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(copilot_mode_shuttle, (XPLMCommandCallback_f)copilot_mode_handler, 1, (void *)SHUTTLE);

    // copilot B737-Classic modes
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_fullvorils, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_FULL_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_expvorils, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_EXP_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_map, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_MAP);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_ctrmap, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_CTR_MAP);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_plan, (XPLMCommandCallback_f)b737cl_copilot_mode_handler, 1, (void *)B737CL_PLAN);

    // copilot radio1
    XPLMUnregisterCommandHandler(copilot_radio1_adf, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)RADIO_ADF);
    XPLMUnregisterCommandHandler(copilot_radio1_off, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)RADIO_OFF);
    XPLMUnregisterCommandHandler(copilot_radio1_nav, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)RADIO_NAV);
    XPLMUnregisterCommandHandler(copilot_radio1_down, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(copilot_radio1_up, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)UP);
//    XPLMUnregisterCommandHandler(copilot_radio1_cycle, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(copilot_radio1_shuttle, (XPLMCommandCallback_f)copilot_radio1_handler, 1, (void *)SHUTTLE);

    // copilot radio2
    XPLMUnregisterCommandHandler(copilot_radio2_adf, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)RADIO_ADF);
    XPLMUnregisterCommandHandler(copilot_radio2_off, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)RADIO_OFF);
    XPLMUnregisterCommandHandler(copilot_radio2_nav, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)RADIO_NAV);
    XPLMUnregisterCommandHandler(copilot_radio2_down, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(copilot_radio2_up, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)UP);
//    XPLMUnregisterCommandHandler(copilot_radio2_cycle, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)CYCLE);
    XPLMUnregisterCommandHandler(copilot_radio2_shuttle, (XPLMCommandCallback_f)copilot_radio2_handler, 1, (void *)SHUTTLE);

    // copilot source
    XPLMUnregisterCommandHandler(copilot_source_nav1, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)SOURCE_NAV1);
    XPLMUnregisterCommandHandler(copilot_source_nav2, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)SOURCE_NAV2);
    XPLMUnregisterCommandHandler(copilot_source_fmc, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)SOURCE_FMC);
    XPLMUnregisterCommandHandler(copilot_source_down, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(copilot_source_up, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(copilot_source_cycle, (XPLMCommandCallback_f)copilot_source_handler, 1, (void *)CYCLE);

    // copilot tfc
    XPLMUnregisterCommandHandler(copilot_tfc_toggle, (XPLMCommandCallback_f)copilot_tfc_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_tfc_on, (XPLMCommandCallback_f)copilot_tfc_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_tfc_off, (XPLMCommandCallback_f)copilot_tfc_handler, 1, (void *)OFF);

    // copilot arpt
    XPLMUnregisterCommandHandler(copilot_arpt_toggle, (XPLMCommandCallback_f)copilot_arpt_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_arpt_on, (XPLMCommandCallback_f)copilot_arpt_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_arpt_off, (XPLMCommandCallback_f)copilot_arpt_handler, 1, (void *)OFF);

    // copilot wpt
    XPLMUnregisterCommandHandler(copilot_wpt_toggle, (XPLMCommandCallback_f)copilot_wpt_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_wpt_on, (XPLMCommandCallback_f)copilot_wpt_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_wpt_off, (XPLMCommandCallback_f)copilot_wpt_handler, 1, (void *)OFF);

    // copilot vor
    XPLMUnregisterCommandHandler(copilot_vor_toggle, (XPLMCommandCallback_f)copilot_vor_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_vor_on, (XPLMCommandCallback_f)copilot_vor_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_vor_off, (XPLMCommandCallback_f)copilot_vor_handler, 1, (void *)OFF);

    // copilot ndb
    XPLMUnregisterCommandHandler(copilot_ndb_toggle, (XPLMCommandCallback_f)copilot_ndb_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_ndb_on, (XPLMCommandCallback_f)copilot_ndb_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_ndb_off, (XPLMCommandCallback_f)copilot_ndb_handler, 1, (void *)OFF);

    // copilot sta = vor + ndb
    XPLMUnregisterCommandHandler(copilot_sta_toggle, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_sta_on, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_sta_off, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)OFF);
    XPLMUnregisterCommandHandler(copilot_sta_cycle, (XPLMCommandCallback_f)copilot_sta_handler, 1, (void *)CYCLE);

    // copilot data
    XPLMUnregisterCommandHandler(copilot_data_toggle, (XPLMCommandCallback_f)copilot_data_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_data_on, (XPLMCommandCallback_f)copilot_data_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_data_off, (XPLMCommandCallback_f)copilot_data_handler, 1, (void *)OFF);

    // copilot pos
    XPLMUnregisterCommandHandler(copilot_pos_toggle, (XPLMCommandCallback_f)copilot_pos_handler, 1, (void *)TOGGLE);
    XPLMUnregisterCommandHandler(copilot_pos_on, (XPLMCommandCallback_f)copilot_pos_handler, 1, (void *)ON);
    XPLMUnregisterCommandHandler(copilot_pos_off, (XPLMCommandCallback_f)copilot_pos_handler, 1, (void *)OFF);


    // MFD mode
//    XPLMUnregisterCommandHandler(mfd_mode_taxi, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_TAXI);
    XPLMUnregisterCommandHandler(mfd_mode_arpt, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_ARPT);
    XPLMUnregisterCommandHandler(mfd_mode_fpln, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_FPLN);
    XPLMUnregisterCommandHandler(mfd_mode_eicas, (XPLMCommandCallback_f)mfd_handler, 1, (void *)MFD_EICAS);
    XPLMUnregisterCommandHandler(mfd_mode_down, (XPLMCommandCallback_f)mfd_handler, 1, (void *)DOWN);
    XPLMUnregisterCommandHandler(mfd_mode_up, (XPLMCommandCallback_f)mfd_handler, 1, (void *)UP);
    XPLMUnregisterCommandHandler(mfd_mode_cycle, (XPLMCommandCallback_f)mfd_handler, 1, (void *)CYCLE);


    // nav1_sync
    XPLMUnregisterCommandHandler(nav1_sync, (XPLMCommandCallback_f)nav_sync_handler, 1, (void *)1);
    // nav2_sync
    XPLMUnregisterCommandHandler(nav2_sync, (XPLMCommandCallback_f)nav_sync_handler, 1, (void *)2);


	// range_auto
	XPLMUnregisterCommandHandler(auto_range_pilot, (XPLMCommandCallback_f)auto_range_handler, 1, (void *)EFIS_PILOT);
	XPLMUnregisterCommandHandler(auto_range_copilot, (XPLMCommandCallback_f)auto_range_handler, 1, (void *)EFIS_COPILOT);

	// ext_range_auto
	XPLMUnregisterCommandHandler(auto_ext_range_pilot, (XPLMCommandCallback_f)auto_ext_range_handler, 1, (void *)EFIS_PILOT);
	XPLMUnregisterCommandHandler(auto_ext_range_copilot, (XPLMCommandCallback_f)auto_ext_range_handler, 1, (void *)EFIS_COPILOT);


    // chronometer
    XPLMUnregisterCommandHandler(chr_start_stop_reset, (XPLMCommandCallback_f)clock_handler, 1, (void *)0);
    XPLMUnregisterCommandHandler(chr_start_stop, (XPLMCommandCallback_f)clock_handler, 1, (void *)1);
    XPLMUnregisterCommandHandler(chr_reset, (XPLMCommandCallback_f)clock_handler, 1, (void *)2);


    XPLMDebugString("XHSI: custom command handlers unregistered\n");

}
