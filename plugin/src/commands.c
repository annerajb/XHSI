
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

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


#define UP -99
#define DOWN -1
#define TOGGLE -333
#define ON 1
#define OFF 0

#define B737CL_MAP 73701
#define B737CL_CTR_MAP 73702
#define B737CL_EXP_VOR_ILS 73703
#define B737CL_FULL_VOR_ILS 73704
#define B737CL_PLAN 73705

#define SUBMODE_APP 0
#define SUBMODE_VOR 1
#define SUBMODE_MAP 2
#define SUBMODE_NAV 3
#define SUBMODE_PLN 4

#define MODE_CENTERED 0
#define MODE_EXPANDED 1

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


XPLMCommandRef mode_app;
XPLMCommandRef mode_vor;
XPLMCommandRef mode_map;
XPLMCommandRef mode_nav;
XPLMCommandRef mode_pln;
XPLMCommandRef mode_down;
XPLMCommandRef mode_up;

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

XPLMCommandRef radio1_adf;
XPLMCommandRef radio1_off;
XPLMCommandRef radio1_nav;
XPLMCommandRef radio1_down;
XPLMCommandRef radio1_up;

XPLMCommandRef radio2_adf;
XPLMCommandRef radio2_off;
XPLMCommandRef radio2_nav;
XPLMCommandRef radio2_down;
XPLMCommandRef radio2_up;

XPLMCommandRef source_nav1;
XPLMCommandRef source_nav2;
XPLMCommandRef source_fmc;
XPLMCommandRef source_down;
XPLMCommandRef source_up;

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

XPLMCommandRef data_toggle;
XPLMCommandRef data_on;
XPLMCommandRef data_off;

XPLMCommandRef pos_toggle;
XPLMCommandRef pos_on;
XPLMCommandRef pos_off;


XPLMCommandRef copilot_mode_app;
XPLMCommandRef copilot_mode_vor;
XPLMCommandRef copilot_mode_map;
XPLMCommandRef copilot_mode_nav;
XPLMCommandRef copilot_mode_pln;
XPLMCommandRef copilot_mode_down;
XPLMCommandRef copilot_mode_up;

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

XPLMCommandRef copilot_radio1_adf;
XPLMCommandRef copilot_radio1_off;
XPLMCommandRef copilot_radio1_nav;
XPLMCommandRef copilot_radio1_down;
XPLMCommandRef copilot_radio1_up;

XPLMCommandRef copilot_radio2_adf;
XPLMCommandRef copilot_radio2_off;
XPLMCommandRef copilot_radio2_nav;
XPLMCommandRef copilot_radio2_down;
XPLMCommandRef copilot_radio2_up;

XPLMCommandRef copilot_source_nav1;
XPLMCommandRef copilot_source_nav2;
XPLMCommandRef copilot_source_fmc;
XPLMCommandRef copilot_source_down;
XPLMCommandRef copilot_source_up;

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

XPLMCommandRef copilot_data_toggle;
XPLMCommandRef copilot_data_on;
XPLMCommandRef copilot_data_off;

XPLMCommandRef copilot_pos_toggle;
XPLMCommandRef copilot_pos_on;
XPLMCommandRef copilot_pos_off;


char debug_string[80];


// ctr
int ctr_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_map_mode);
        XPLMSetDatai(efis_map_mode, i);
    }
    return 1;
}

// range
int range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_map_range_selector, i);
    }
    return 1;
}

// mode
int mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_map_submode, i);
    }
    return 1;
}

// B737-Classic modes
int b737cl_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;

        if ( i == B737CL_FULL_VOR_ILS )
        {
            XPLMSetDatai(efis_map_submode, SUBMODE_APP);
            XPLMSetDatai(efis_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_EXP_VOR_ILS )
        {
            XPLMSetDatai(efis_map_submode, SUBMODE_APP);
            XPLMSetDatai(efis_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_MAP )
        {
            XPLMSetDatai(efis_map_submode, SUBMODE_MAP);
            XPLMSetDatai(efis_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_CTR_MAP )
        {
            XPLMSetDatai(efis_map_submode, SUBMODE_MAP);
            XPLMSetDatai(efis_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_PLAN )
        {
            XPLMSetDatai(efis_map_submode, SUBMODE_PLN);
        }
    }
    return 1;
}

// radio1
int radio1_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_dme_1_selector, i);
    }
    return 1;
}

// radio2
int radio2_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_dme_2_selector, i);
    }
    return 1;
}


// source
int source_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(hsi_selector, i);
    }
    return 1;
}

// tfc
int tfc_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_tcas);
        XPLMSetDatai(efis_shows_tcas, i);
    }
    return 1;
}

// arpt
int arpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_airports);
        XPLMSetDatai(efis_shows_airports, i);
    }
    return 1;
}

// wpt
int wpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_waypoints);
        XPLMSetDatai(efis_shows_waypoints, i);
    }
    return 1;
}

// vor
int vor_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_vors);
        XPLMSetDatai(efis_shows_vors, i);
    }
    return 1;
}

// ndb
int ndb_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_shows_ndbs);
        XPLMSetDatai(efis_shows_ndbs, i);
    }
    return 1;
}

// sta = vor + ndb
int sta_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_shows_stas);
        XPLMSetDatai(efis_pilot_shows_stas, i);
        XPLMSetDatai(efis_shows_vors, i);
        XPLMSetDatai(efis_shows_ndbs, i);
    }
    return 1;
}

// data = route data
int data_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_shows_data);
        XPLMSetDatai(efis_pilot_shows_data, i);
    }
    return 1;
}

// pos
int pos_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_pilot_shows_pos);
        XPLMSetDatai(efis_pilot_shows_pos, i);
    }
    return 1;
}


// copilot command handlers
// copilot ctr
int copilot_ctr_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_map_mode);
        XPLMSetDatai(efis_copilot_map_mode, i);
    }
    return 1;
}


// copilot range
int copilot_range_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_copilot_map_range_selector, i);
    }
    return 1;
}

// copilot mode
int copilot_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_copilot_map_submode, i);
    }
    return 1;
}

// copilot B737-Classic modes
int b737cl_copilot_mode_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;

        if ( i == B737CL_FULL_VOR_ILS )
        {
            XPLMSetDatai(efis_copilot_map_submode, SUBMODE_APP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_EXP_VOR_ILS )
        {
            XPLMSetDatai(efis_copilot_map_submode, SUBMODE_APP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_MAP )
        {
            XPLMSetDatai(efis_copilot_map_submode, SUBMODE_MAP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_EXPANDED);
        }
        else if ( i == B737CL_CTR_MAP )
        {
            XPLMSetDatai(efis_copilot_map_submode, SUBMODE_MAP);
            XPLMSetDatai(efis_copilot_map_mode, MODE_CENTERED);
        }
        else if ( i == B737CL_PLAN )
        {
            XPLMSetDatai(efis_copilot_map_submode, SUBMODE_PLN);
        }
    }
    return 1;
}

// copilot radio1
int copilot_radio1_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_copilot_dme_1_selector, i);
    }
    return 1;
}

// copilot radio2
int copilot_radio2_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(efis_copilot_dme_2_selector, i);
    }
    return 1;
}

// copilot source
int copilot_source_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
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
        XPLMSetDatai(copilot_hsi_selector, i);
    }
    return 1;
}

// copilot tfc
int copilot_tfc_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_tcas);
        XPLMSetDatai(efis_copilot_shows_tcas, i);
    }
    return 1;
}

// copilot arpt
int copilot_arpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_airports);
        XPLMSetDatai(efis_copilot_shows_airports, i);
    }
    return 1;
}

// copilot wpt
int copilot_wpt_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_waypoints);
        XPLMSetDatai(efis_copilot_shows_waypoints, i);
    }
    return 1;
}

// copilot vor
int copilot_vor_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_vors);
        XPLMSetDatai(efis_copilot_shows_vors, i);
    }
    return 1;
}

// copilot ndb
int copilot_ndb_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_ndbs);
        XPLMSetDatai(efis_copilot_shows_ndbs, i);
    }
    return 1;
}

// copilot sta = vor + ndb
int copilot_sta_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_stas);
        XPLMSetDatai(efis_copilot_shows_stas, i);
        XPLMSetDatai(efis_copilot_shows_vors, i);
        XPLMSetDatai(efis_copilot_shows_ndbs, i);
    }
    return 1;
}

// copilot data = route data
int copilot_data_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_data);
        XPLMSetDatai(efis_copilot_shows_data, i);
    }
    return 1;
}

// copilot pos
int copilot_pos_handler(XPLMCommandRef inCommand, XPLMCommandPhase inPhase, void * inRefcon)
{
    if (inPhase == xplm_CommandBegin)
    {
        int i = (int)inRefcon;
        if ( i == TOGGLE ) i = ! XPLMGetDatai(efis_copilot_shows_pos);
        XPLMSetDatai(efis_copilot_shows_pos, i);
    }
    return 1;
}



void registerCommands(void) {

    // pilot commands
    // mode
    mode_app = XPLMCreateCommand("xhsi/efis/pilot/mode_app", "EFIS mode APP");
    XPLMRegisterCommandHandler(mode_app, mode_handler, 1, (void *) SUBMODE_APP);
    mode_vor = XPLMCreateCommand("xhsi/efis/pilot/mode_vor", "EFIS mode VOR");
    XPLMRegisterCommandHandler(mode_vor, mode_handler, 1, (void *) SUBMODE_VOR);
    mode_map = XPLMCreateCommand("xhsi/efis/pilot/mode_map", "EFIS mode MAP");
    XPLMRegisterCommandHandler(mode_map, mode_handler, 1, (void *) SUBMODE_MAP);
    mode_nav = XPLMCreateCommand("xhsi/efis/pilot/mode_nav", "EFIS mode NAV");
    XPLMRegisterCommandHandler(mode_nav, mode_handler, 1, (void *) SUBMODE_NAV);
    mode_pln = XPLMCreateCommand("xhsi/efis/pilot/mode_pln", "EFIS mode PLN");
    XPLMRegisterCommandHandler(mode_pln, mode_handler, 1, (void *) SUBMODE_PLN);
    mode_down = XPLMCreateCommand("xhsi/efis/pilot/mode_down", "Previous EFIS mode");
    XPLMRegisterCommandHandler(mode_down, mode_handler, 1, (void *) DOWN);
    mode_up = XPLMCreateCommand("xhsi/efis/pilot/mode_up", "Next EFIS mode");
    XPLMRegisterCommandHandler(mode_up, mode_handler, 1, (void *) UP);

    // B737-Classic modes
    b737cl_mode_fullvorils = XPLMCreateCommand("xhsi/efis/b737_classic/pilot_mode_fullvorils", "B737-Classic mode FULL VOR/ILS");
    XPLMRegisterCommandHandler(b737cl_mode_fullvorils, b737cl_mode_handler, 1, (void *) B737CL_FULL_VOR_ILS);
    b737cl_mode_expvorils = XPLMCreateCommand("xhsi/efis/b737_classic/pilot_mode_expvorils", "B737-Classic mode EXP VOR/ILS");
    XPLMRegisterCommandHandler(b737cl_mode_expvorils, b737cl_mode_handler, 1, (void *) B737CL_EXP_VOR_ILS);
    b737cl_mode_map = XPLMCreateCommand("xhsi/efis/b737_classic/pilot_mode_map", "B737-Classic mode MAP");
    XPLMRegisterCommandHandler(b737cl_mode_map, b737cl_mode_handler, 1, (void *) B737CL_MAP);
    b737cl_mode_ctrmap = XPLMCreateCommand("xhsi/efis/b737_classic/pilot_mode_ctrmap", "B737-Classic mode CTR MAP");
    XPLMRegisterCommandHandler(b737cl_mode_ctrmap, b737cl_mode_handler, 1, (void *) B737CL_CTR_MAP);
    b737cl_mode_plan = XPLMCreateCommand("xhsi/efis/b737_classic/pilot_mode_plan", "B737-Classic mode PLAN");
    XPLMRegisterCommandHandler(b737cl_mode_plan, b737cl_mode_handler, 1, (void *) B737CL_PLAN);

    // ctr
    ctr_toggle = XPLMCreateCommand("xhsi/efis/pilot/mode_ctr_toggle", "Toggle EFIS map CTR");
    XPLMRegisterCommandHandler(ctr_toggle, ctr_handler, 1, (void *) TOGGLE);
    ctr_on = XPLMCreateCommand("xhsi/efis/pilot/mode_ctr_on", "EFIS map CTR on");
    XPLMRegisterCommandHandler(ctr_on, ctr_handler, 1, (void *) MODE_CENTERED);
    ctr_off = XPLMCreateCommand("xhsi/efis/pilot/mode_ctr_off", "EFIS map CTR off");
    XPLMRegisterCommandHandler(ctr_off, ctr_handler, 1, (void *) MODE_EXPANDED);

    // range
    range_10 = XPLMCreateCommand("xhsi/efis/pilot/range_10", "EFIS map range 10");
    XPLMRegisterCommandHandler(range_10, range_handler, 1, (void *) RANGE_10);
    range_20 = XPLMCreateCommand("xhsi/efis/pilot/range_20", "EFIS map range 20");
    XPLMRegisterCommandHandler(range_20, range_handler, 1, (void *) RANGE_20);
    range_40 = XPLMCreateCommand("xhsi/efis/pilot/range_40", "EFIS map range 40");
    XPLMRegisterCommandHandler(range_40, range_handler, 1, (void *) RANGE_40);
    range_80 = XPLMCreateCommand("xhsi/efis/pilot/range_80", "EFIS map range 80");
    XPLMRegisterCommandHandler(range_80, range_handler, 1, (void *) RANGE_80);
    range_160 = XPLMCreateCommand("xhsi/efis/pilot/range_160", "EFIS map range 160");
    XPLMRegisterCommandHandler(range_160, range_handler, 1, (void *) RANGE_160);
    range_320 = XPLMCreateCommand("xhsi/efis/pilot/range_320", "EFIS map range 320");
    XPLMRegisterCommandHandler(range_320, range_handler, 1, (void *) RANGE_320);
    range_640 = XPLMCreateCommand("xhsi/efis/pilot/range_640", "EFIS map range 640");
    XPLMRegisterCommandHandler(range_640, range_handler, 1, (void *) RANGE_640);
    range_down = XPLMCreateCommand("xhsi/efis/pilot/range_down", "Decrease EFIS map range");
    XPLMRegisterCommandHandler(range_down, range_handler, 1, (void *) DOWN);
    range_up = XPLMCreateCommand("xhsi/efis/pilot/range_up", "Increase EFIS map range");
    XPLMRegisterCommandHandler(range_up, range_handler, 1, (void *) UP);

    // radio1
    radio1_adf = XPLMCreateCommand("xhsi/efis/pilot/radio1_adf", "EFIS radio1 ADF");
    XPLMRegisterCommandHandler(radio1_adf, radio1_handler, 1, (void *) RADIO_ADF);
    radio1_off = XPLMCreateCommand("xhsi/efis/pilot/radio1_off", "EFIS radio1 OFF");
    XPLMRegisterCommandHandler(radio1_off, radio1_handler, 1, (void *) RADIO_OFF);
    radio1_nav = XPLMCreateCommand("xhsi/efis/pilot/radio1_nav", "EFIS radio1 NAV");
    XPLMRegisterCommandHandler(radio1_nav, radio1_handler, 1, (void *) RADIO_NAV);
    radio1_down = XPLMCreateCommand("xhsi/efis/pilot/radio1_down", "Previous EFIS radio1");
    XPLMRegisterCommandHandler(radio1_down, radio1_handler, 1, (void *) DOWN);
    radio1_up = XPLMCreateCommand("xhsi/efis/pilot/radio1_up", "Next EFIS radio1");
    XPLMRegisterCommandHandler(radio1_up, radio1_handler, 1, (void *) UP);

    // radio2
    radio2_adf = XPLMCreateCommand("xhsi/efis/pilot/radio2_adf", "EFIS radio2 ADF");
    XPLMRegisterCommandHandler(radio2_adf, radio2_handler, 1, (void *) RADIO_ADF);
    radio2_off = XPLMCreateCommand("xhsi/efis/pilot/radio2_off", "EFIS radio2 OFF");
    XPLMRegisterCommandHandler(radio2_off, radio2_handler, 1, (void *) RADIO_OFF);
    radio2_nav = XPLMCreateCommand("xhsi/efis/pilot/radio2_nav", "EFIS radio2 NAV");
    XPLMRegisterCommandHandler(radio2_nav, radio2_handler, 1, (void *) RADIO_NAV);
    radio2_down = XPLMCreateCommand("xhsi/efis/pilot/radio2_down", "Previous EFIS radio2");
    XPLMRegisterCommandHandler(radio2_down, radio2_handler, 1, (void *) DOWN);
    radio2_up = XPLMCreateCommand("xhsi/efis/pilot/radio2_up", "Next EFIS radio2");
    XPLMRegisterCommandHandler(radio2_up, radio2_handler, 1, (void *) UP);

    // source
    source_nav1 = XPLMCreateCommand("xhsi/efis/pilot/source_nav1", "EFIS source NAV1");
    XPLMRegisterCommandHandler(source_nav1, source_handler, 1, (void *) SOURCE_NAV1);
    source_nav2 = XPLMCreateCommand("xhsi/efis/pilot/source_nav2", "EFIS source NAV2");
    XPLMRegisterCommandHandler(source_nav2, source_handler, 1, (void *) SOURCE_NAV2);
    source_fmc = XPLMCreateCommand("xhsi/efis/pilot/source_fmc", "EFIS source FMC");
    XPLMRegisterCommandHandler(source_fmc, source_handler, 1, (void *) SOURCE_FMC);
    source_down = XPLMCreateCommand("xhsi/efis/pilot/source_down", "Previous EFIS source");
    XPLMRegisterCommandHandler(source_down, source_handler, 1, (void *) DOWN);
    source_up = XPLMCreateCommand("xhsi/efis/pilot/source_up", "Next EFIS source");
    XPLMRegisterCommandHandler(source_up, source_handler, 1, (void *) UP);

    // tfc
    tfc_toggle = XPLMCreateCommand("xhsi/efis/pilot/tfc_toggle", "Toggle EFIS TFC");
    XPLMRegisterCommandHandler(tfc_toggle, tfc_handler, 1, (void *) TOGGLE);
    tfc_on = XPLMCreateCommand("xhsi/efis/pilot/tfc_on", "EFIS TFC on");
    XPLMRegisterCommandHandler(tfc_on, tfc_handler, 1, (void *) ON);
    tfc_off = XPLMCreateCommand("xhsi/efis/pilot/tfc_off", "EFIS TFC off");
    XPLMRegisterCommandHandler(tfc_off, tfc_handler, 1, (void *) OFF);

    // arpt
    arpt_toggle = XPLMCreateCommand("xhsi/efis/pilot/arpt_toggle", "Toggle EFIS ARPT");
    XPLMRegisterCommandHandler(arpt_toggle, arpt_handler, 1, (void *) TOGGLE);
    arpt_on = XPLMCreateCommand("xhsi/efis/pilot/arpt_on", "EFIS ARPT on");
    XPLMRegisterCommandHandler(arpt_on, arpt_handler, 1, (void *) ON);
    arpt_off = XPLMCreateCommand("xhsi/efis/pilot/arpt_off", "EFIS ARPT off");
    XPLMRegisterCommandHandler(arpt_off, arpt_handler, 1, (void *) OFF);

    // wpt
    wpt_toggle = XPLMCreateCommand("xhsi/efis/pilot/wpt_toggle", "Toggle EFIS WPT");
    XPLMRegisterCommandHandler(wpt_toggle, wpt_handler, 1, (void *) TOGGLE);
    wpt_on = XPLMCreateCommand("xhsi/efis/pilot/wpt_on", "EFIS WPT on");
    XPLMRegisterCommandHandler(wpt_on, wpt_handler, 1, (void *) ON);
    wpt_off = XPLMCreateCommand("xhsi/efis/pilot/wpt_off", "EFIS WPT off");
    XPLMRegisterCommandHandler(wpt_off, wpt_handler, 1, (void *) OFF);

    // vor
    vor_toggle = XPLMCreateCommand("xhsi/efis/pilot/vor_toggle", "Toggle EFIS VOR");
    XPLMRegisterCommandHandler(vor_toggle, vor_handler, 1, (void *) TOGGLE);
    vor_on = XPLMCreateCommand("xhsi/efis/pilot/vor_on", "EFIS VOR on");
    XPLMRegisterCommandHandler(vor_on, vor_handler, 1, (void *) ON);
    vor_off = XPLMCreateCommand("xhsi/efis/pilot/vor_off", "EFIS VOR off");
    XPLMRegisterCommandHandler(vor_off, vor_handler, 1, (void *) OFF);

    // ndb
    ndb_toggle = XPLMCreateCommand("xhsi/efis/pilot/ndb_toggle", "Toggle EFIS NDB");
    XPLMRegisterCommandHandler(ndb_toggle, ndb_handler, 1, (void *) TOGGLE);
    ndb_on = XPLMCreateCommand("xhsi/efis/pilot/ndb_on", "EFIS NDB on");
    XPLMRegisterCommandHandler(ndb_on, ndb_handler, 1, (void *) ON);
    ndb_off = XPLMCreateCommand("xhsi/efis/pilot/ndb_off", "EFIS NDB off");
    XPLMRegisterCommandHandler(ndb_off, ndb_handler, 1, (void *) OFF);

    // sta = vor + ndb
    sta_toggle = XPLMCreateCommand("xhsi/efis/pilot/sta_toggle", "Toggle EFIS STA");
    XPLMRegisterCommandHandler(sta_toggle, sta_handler, 1, (void *) TOGGLE);
    sta_on = XPLMCreateCommand("xhsi/efis/pilot/sta_on", "EFIS STA on");
    XPLMRegisterCommandHandler(sta_on, sta_handler, 1, (void *) ON);
    sta_off = XPLMCreateCommand("xhsi/efis/pilot/sta_off", "EFIS STA off");
    XPLMRegisterCommandHandler(sta_off, sta_handler, 1, (void *) OFF);

    // data = route data
    data_toggle = XPLMCreateCommand("xhsi/efis/pilot/data_toggle", "Toggle EFIS DATA");
    XPLMRegisterCommandHandler(data_toggle, data_handler, 1, (void *) TOGGLE);
    data_on = XPLMCreateCommand("xhsi/efis/pilot/data_on", "EFIS DATA on");
    XPLMRegisterCommandHandler(data_on, data_handler, 1, (void *) ON);
    data_off = XPLMCreateCommand("xhsi/efis/pilot/data_off", "EFIS DATA off");
    XPLMRegisterCommandHandler(data_off, data_handler, 1, (void *) OFF);

    // pos
    pos_toggle = XPLMCreateCommand("xhsi/efis/pilot/pos_toggle", "Toggle EFIS POS");
    XPLMRegisterCommandHandler(pos_toggle, pos_handler, 1, (void *) TOGGLE);
    pos_on = XPLMCreateCommand("xhsi/efis/pilot/pos_on", "EFIS POS on");
    XPLMRegisterCommandHandler(pos_on, pos_handler, 1, (void *) ON);
    pos_off = XPLMCreateCommand("xhsi/efis/pilot/pos_off", "EFIS POS off");
    XPLMRegisterCommandHandler(pos_off, pos_handler, 1, (void *) OFF);


    // copilot commands
    // copilot mode
    copilot_mode_app = XPLMCreateCommand("xhsi/efis/copilot/mode_app", "EFIS mode APP - copilot");
    XPLMRegisterCommandHandler(copilot_mode_app, copilot_mode_handler, 1, (void *) SUBMODE_APP);
    copilot_mode_vor = XPLMCreateCommand("xhsi/efis/copilot/mode_vor", "EFIS mode VOR - copilot");
    XPLMRegisterCommandHandler(copilot_mode_vor, copilot_mode_handler, 1, (void *) SUBMODE_VOR);
    copilot_mode_map = XPLMCreateCommand("xhsi/efis/copilot/mode_map", "EFIS mode MAP - copilot");
    XPLMRegisterCommandHandler(copilot_mode_map, copilot_mode_handler, 1, (void *) SUBMODE_MAP);
    copilot_mode_nav = XPLMCreateCommand("xhsi/efis/copilot/mode_nav", "EFIS mode NAV - copilot");
    XPLMRegisterCommandHandler(copilot_mode_nav, copilot_mode_handler, 1, (void *) SUBMODE_NAV);
    copilot_mode_pln = XPLMCreateCommand("xhsi/efis/copilot/mode_pln", "EFIS mode PLN - copilot");
    XPLMRegisterCommandHandler(copilot_mode_pln, copilot_mode_handler, 1, (void *) SUBMODE_PLN);
    copilot_mode_down = XPLMCreateCommand("xhsi/efis/copilot/mode_down", "Previous EFIS mode - copilot");
    XPLMRegisterCommandHandler(copilot_mode_down, copilot_mode_handler, 1, (void *) DOWN);
    copilot_mode_up = XPLMCreateCommand("xhsi/efis/copilot/mode_up", "Next EFIS mode - copilot");
    XPLMRegisterCommandHandler(copilot_mode_up, copilot_mode_handler, 1, (void *) UP);

    // copilot B737-Classic modes
    b737cl_copilot_mode_fullvorils = XPLMCreateCommand("xhsi/efis/b737_classic/copilot_mode_fullvorils", "B737-Classic mode FULL VOR/ILS - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_fullvorils, b737cl_copilot_mode_handler, 1, (void *) B737CL_FULL_VOR_ILS);
    b737cl_copilot_mode_expvorils = XPLMCreateCommand("xhsi/efis/b737_classic/copilot_mode_expvorils", "B737-Classic mode EXP VOR/ILS - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_expvorils, b737cl_copilot_mode_handler, 1, (void *) B737CL_EXP_VOR_ILS);
    b737cl_copilot_mode_map = XPLMCreateCommand("xhsi/efis/b737_classic/copilot_mode_map", "B737-Classic mode MAP - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_map, b737cl_copilot_mode_handler, 1, (void *) B737CL_MAP);
    b737cl_copilot_mode_ctrmap = XPLMCreateCommand("xhsi/efis/b737_classic/copilot_mode_ctrmap", "B737-Classic mode CTR MAP - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_ctrmap, b737cl_copilot_mode_handler, 1, (void *) B737CL_CTR_MAP);
    b737cl_copilot_mode_plan = XPLMCreateCommand("xhsi/efis/b737_classic/copilot_mode_plan", "B737-Classic mode PLAN - copilot");
    XPLMRegisterCommandHandler(b737cl_copilot_mode_plan, b737cl_copilot_mode_handler, 1, (void *) B737CL_PLAN);

    // copilot ctr
    copilot_ctr_toggle = XPLMCreateCommand("xhsi/efis/copilot/mode_ctr_toggle", "Toggle EFIS map CTR - copilot");
    XPLMRegisterCommandHandler(copilot_ctr_toggle, copilot_ctr_handler, 1, (void *) TOGGLE);
    copilot_ctr_on = XPLMCreateCommand("xhsi/efis/copilot/mode_ctr_on", "EFIS map CTR on - copilot");
    XPLMRegisterCommandHandler(copilot_ctr_on, copilot_ctr_handler, 1, (void *) MODE_CENTERED);
    copilot_ctr_off = XPLMCreateCommand("xhsi/efis/copilot/mode_ctr_off", "EFIS map CTR off - copilot");
    XPLMRegisterCommandHandler(copilot_ctr_off, copilot_ctr_handler, 1, (void *) MODE_EXPANDED);

    // copilot range
    copilot_range_10 = XPLMCreateCommand("xhsi/efis/copilot/range_10", "EFIS map range 10 - copilot");
    XPLMRegisterCommandHandler(copilot_range_10, copilot_range_handler, 1, (void *) RANGE_10);
    copilot_range_20 = XPLMCreateCommand("xhsi/efis/copilot/range_20", "EFIS map range 20 - copilot");
    XPLMRegisterCommandHandler(copilot_range_20, copilot_range_handler, 1, (void *) RANGE_20);
    copilot_range_40 = XPLMCreateCommand("xhsi/efis/copilot/range_40", "EFIS map range 40 - copilot");
    XPLMRegisterCommandHandler(copilot_range_40, copilot_range_handler, 1, (void *) RANGE_40);
    copilot_range_80 = XPLMCreateCommand("xhsi/efis/copilot/range_80", "EFIS map range 80 - copilot");
    XPLMRegisterCommandHandler(copilot_range_80, copilot_range_handler, 1, (void *) 3);
    copilot_range_160 = XPLMCreateCommand("xhsi/efis/copilot/range_160", "EFIS map range 160 - copilot");
    XPLMRegisterCommandHandler(copilot_range_160, copilot_range_handler, 1, (void *) RANGE_160);
    copilot_range_320 = XPLMCreateCommand("xhsi/efis/copilot/range_320", "EFIS map range 320 - copilot");
    XPLMRegisterCommandHandler(copilot_range_320, copilot_range_handler, 1, (void *) RANGE_320);
    copilot_range_640 = XPLMCreateCommand("xhsi/efis/copilot/range_640", "EFIS map range 640 - copilot");
    XPLMRegisterCommandHandler(copilot_range_640, copilot_range_handler, 1, (void *) RANGE_640);
    copilot_range_down = XPLMCreateCommand("xhsi/efis/copilot/range_down", "Decrease EFIS map range - copilot");
    XPLMRegisterCommandHandler(copilot_range_down, copilot_range_handler, 1, (void *) DOWN);
    copilot_range_up = XPLMCreateCommand("xhsi/efis/copilot/range_up", "Increase EFIS map range - copilot");
    XPLMRegisterCommandHandler(copilot_range_up, copilot_range_handler, 1, (void *) UP);

    // copilot radio1
    copilot_radio1_adf = XPLMCreateCommand("xhsi/efis/copilot/radio1_adf", "EFIS radio1 ADF - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_adf, copilot_radio1_handler, 1, (void *) RADIO_ADF);
    copilot_radio1_off = XPLMCreateCommand("xhsi/efis/copilot/radio1_off", "EFIS radio1 OFF - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_off, copilot_radio1_handler, 1, (void *) RADIO_OFF);
    copilot_radio1_nav = XPLMCreateCommand("xhsi/efis/copilot/radio1_nav", "EFIS radio1 NAV - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_nav, copilot_radio1_handler, 1, (void *) RADIO_NAV);
    copilot_radio1_down = XPLMCreateCommand("xhsi/efis/copilot/radio1_down", "Previous EFIS radio1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_down, copilot_radio1_handler, 1, (void *) DOWN);
    copilot_radio1_up = XPLMCreateCommand("xhsi/efis/copilot/radio1_up", "Next EFIS radio1 - copilot");
    XPLMRegisterCommandHandler(copilot_radio1_up, copilot_radio1_handler, 1, (void *) UP);

    // copilot radio2
    copilot_radio2_adf = XPLMCreateCommand("xhsi/efis/copilot/radio2_adf", "EFIS radio2 ADF - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_adf, copilot_radio2_handler, 1, (void *) RADIO_ADF);
    copilot_radio2_off = XPLMCreateCommand("xhsi/efis/copilot/radio2_off", "EFIS radio2 OFF - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_off, copilot_radio2_handler, 1, (void *) RADIO_OFF);
    copilot_radio2_nav = XPLMCreateCommand("xhsi/efis/copilot/radio2_nav", "EFIS radio2 NAV - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_nav, copilot_radio2_handler, 1, (void *) RADIO_NAV);
    copilot_radio2_down = XPLMCreateCommand("xhsi/efis/copilot/radio2_down", "Previous EFIS radio2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_down, copilot_radio2_handler, 1, (void *) DOWN);
    copilot_radio2_up = XPLMCreateCommand("xhsi/efis/copilot/radio2_up", "Next EFIS radio2 - copilot");
    XPLMRegisterCommandHandler(copilot_radio2_up, copilot_radio2_handler, 1, (void *) UP);

    // copilot source
    copilot_source_nav1 = XPLMCreateCommand("xhsi/efis/copilot/source_nav1", "EFIS source NAV1 - copilot");
    XPLMRegisterCommandHandler(copilot_source_nav1, copilot_source_handler, 1, (void *) SOURCE_NAV1);
    copilot_source_nav2 = XPLMCreateCommand("xhsi/efis/copilot/source_nav2", "EFIS source NAV2 - copilot");
    XPLMRegisterCommandHandler(copilot_source_nav2, copilot_source_handler, 1, (void *) SOURCE_NAV2);
    copilot_source_fmc = XPLMCreateCommand("xhsi/efis/copilot/source_fmc", "EFIS source FMC - copilot");
    XPLMRegisterCommandHandler(copilot_source_fmc, copilot_source_handler, 1, (void *) SOURCE_FMC);
    copilot_source_down = XPLMCreateCommand("xhsi/efis/copilot/source_down", "Previous EFIS source - copilot");
    XPLMRegisterCommandHandler(copilot_source_down, copilot_source_handler, 1, (void *) DOWN);
    copilot_source_up = XPLMCreateCommand("xhsi/efis/copilot/source_up", "Next EFIS source - copilot");
    XPLMRegisterCommandHandler(copilot_source_up, copilot_source_handler, 1, (void *) UP);

    // copilot tfc
    copilot_tfc_toggle = XPLMCreateCommand("xhsi/efis/copilot/tfc_toggle", "Toggle EFIS TFC - copilot");
    XPLMRegisterCommandHandler(copilot_tfc_toggle, copilot_tfc_handler, 1, (void *) TOGGLE);
    copilot_tfc_on = XPLMCreateCommand("xhsi/efis/copilot/tfc_on", "EFIS TFC on - copilot");
    XPLMRegisterCommandHandler(copilot_tfc_on, copilot_tfc_handler, 1, (void *) ON);
    copilot_tfc_off = XPLMCreateCommand("xhsi/efis/copilot/tfc_off", "EFIS TFC off - copilot");
    XPLMRegisterCommandHandler(copilot_tfc_off, copilot_tfc_handler, 1, (void *) OFF);

    // copilot arpt
    copilot_arpt_toggle = XPLMCreateCommand("xhsi/efis/copilot/arpt_toggle", "Toggle EFIS ARPT - copilot");
    XPLMRegisterCommandHandler(copilot_arpt_toggle, copilot_arpt_handler, 1, (void *) TOGGLE);
    copilot_arpt_on = XPLMCreateCommand("xhsi/efis/copilot/arpt_on", "EFIS ARPT on - copilot");
    XPLMRegisterCommandHandler(copilot_arpt_on, copilot_arpt_handler, 1, (void *) ON);
    copilot_arpt_off = XPLMCreateCommand("xhsi/efis/copilot/arpt_off", "EFIS ARPT off - copilot");
    XPLMRegisterCommandHandler(copilot_arpt_off, copilot_arpt_handler, 1, (void *) OFF);

    // copilot wpt
    copilot_wpt_toggle = XPLMCreateCommand("xhsi/efis/copilot/wpt_toggle", "Toggle EFIS WPT - copilot");
    XPLMRegisterCommandHandler(copilot_wpt_toggle, copilot_wpt_handler, 1, (void *) TOGGLE);
    copilot_wpt_on = XPLMCreateCommand("xhsi/efis/copilot/wpt_on", "EFIS WPT on - copilot");
    XPLMRegisterCommandHandler(copilot_wpt_on, copilot_wpt_handler, 1, (void *) ON);
    copilot_wpt_off = XPLMCreateCommand("xhsi/efis/copilot/wpt_off", "EFIS WPT off - copilot");
    XPLMRegisterCommandHandler(copilot_wpt_off, copilot_wpt_handler, 1, (void *) OFF);

    // copilot vor
    copilot_vor_toggle = XPLMCreateCommand("xhsi/efis/copilot/vor_toggle", "Toggle EFIS VOR - copilot");
    XPLMRegisterCommandHandler(copilot_vor_toggle, copilot_vor_handler, 1, (void *) TOGGLE);
    copilot_vor_on = XPLMCreateCommand("xhsi/efis/copilot/vor_on", "EFIS VOR on - copilot");
    XPLMRegisterCommandHandler(copilot_vor_on, copilot_vor_handler, 1, (void *) ON);
    copilot_vor_off = XPLMCreateCommand("xhsi/efis/copilot/vor_off", "EFIS VOR off - copilot");
    XPLMRegisterCommandHandler(copilot_vor_off, copilot_vor_handler, 1, (void *) OFF);

    // copilot ndb
    copilot_ndb_toggle = XPLMCreateCommand("xhsi/efis/copilot/ndb_toggle", "Toggle EFIS NDB - copilot");
    XPLMRegisterCommandHandler(copilot_ndb_toggle, copilot_ndb_handler, 1, (void *) TOGGLE);
    copilot_ndb_on = XPLMCreateCommand("xhsi/efis/copilot/ndb_on", "EFIS NDB on - copilot");
    XPLMRegisterCommandHandler(copilot_ndb_on, copilot_ndb_handler, 1, (void *) ON);
    copilot_ndb_off = XPLMCreateCommand("xhsi/efis/copilot/ndb_off", "EFIS NDB off - copilot");
    XPLMRegisterCommandHandler(copilot_ndb_off, copilot_ndb_handler, 1, (void *) OFF);

    // copilot sta = vor + ndb
    copilot_sta_toggle = XPLMCreateCommand("xhsi/efis/copilot/sta_toggle", "Toggle EFIS STA - copilot");
    XPLMRegisterCommandHandler(copilot_sta_toggle, copilot_sta_handler, 1, (void *) TOGGLE);
    copilot_sta_on = XPLMCreateCommand("xhsi/efis/copilot/sta_on", "EFIS STA on - copilot");
    XPLMRegisterCommandHandler(copilot_sta_on, copilot_sta_handler, 1, (void *) ON);
    copilot_sta_off = XPLMCreateCommand("xhsi/efis/copilot/sta_off", "EFIS STA off - copilot");
    XPLMRegisterCommandHandler(copilot_sta_off, copilot_sta_handler, 1, (void *) OFF);

    // copilot data = route data
    copilot_data_toggle = XPLMCreateCommand("xhsi/efis/copilot/data_toggle", "Toggle EFIS DATA - copilot");
    XPLMRegisterCommandHandler(copilot_data_toggle, copilot_data_handler, 1, (void *) TOGGLE);
    copilot_data_on = XPLMCreateCommand("xhsi/efis/copilot/data_on", "EFIS DATA on - copilot");
    XPLMRegisterCommandHandler(copilot_data_on, copilot_data_handler, 1, (void *) ON);
    copilot_data_off = XPLMCreateCommand("xhsi/efis/copilot/data_off", "EFIS DATA off - copilot");
    XPLMRegisterCommandHandler(copilot_data_off, copilot_data_handler, 1, (void *) OFF);

    // copilot pos
    copilot_pos_toggle = XPLMCreateCommand("xhsi/efis/copilot/pos_toggle", "Toggle EFIS POS - copilot");
    XPLMRegisterCommandHandler(copilot_pos_toggle, copilot_pos_handler, 1, (void *) TOGGLE);
    copilot_pos_on = XPLMCreateCommand("xhsi/efis/copilot/pos_on", "EFIS POS on - copilot");
    XPLMRegisterCommandHandler(copilot_pos_on, copilot_pos_handler, 1, (void *) ON);
    copilot_pos_off = XPLMCreateCommand("xhsi/efis/copilot/pos_off", "EFIS POS off - copilot");
    XPLMRegisterCommandHandler(copilot_pos_off, copilot_pos_handler, 1, (void *) OFF);


    XPLMDebugString("XHSI: custom commands created and handlers registered\n");

}


void unregisterCommands(void) {

    // pilot commands
    // ctr
    XPLMUnregisterCommandHandler(ctr_toggle, ctr_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(ctr_on, ctr_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(ctr_off, ctr_handler, 1, (void *) OFF);

    // range
    XPLMUnregisterCommandHandler(range_10, range_handler, 1, (void *) RANGE_10);
    XPLMUnregisterCommandHandler(range_20, range_handler, 1, (void *) RANGE_20);
    XPLMUnregisterCommandHandler(range_40, range_handler, 1, (void *) RANGE_40);
    XPLMUnregisterCommandHandler(range_80, range_handler, 1, (void *) RANGE_80);
    XPLMUnregisterCommandHandler(range_160, range_handler, 1, (void *) RANGE_160);
    XPLMUnregisterCommandHandler(range_320, range_handler, 1, (void *) RANGE_320);
    XPLMUnregisterCommandHandler(range_640, range_handler, 1, (void *) RANGE_640);
    XPLMUnregisterCommandHandler(range_down, range_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(range_up, range_handler, 1, (void *) UP);

    // mode
    XPLMUnregisterCommandHandler(mode_app, mode_handler, 1, (void *) SUBMODE_APP);
    XPLMUnregisterCommandHandler(mode_vor, mode_handler, 1, (void *) SUBMODE_VOR);
    XPLMUnregisterCommandHandler(mode_map, mode_handler, 1, (void *) SUBMODE_MAP);
    XPLMUnregisterCommandHandler(mode_nav, mode_handler, 1, (void *) SUBMODE_NAV);
    XPLMUnregisterCommandHandler(mode_pln, mode_handler, 1, (void *) SUBMODE_PLN);
    XPLMUnregisterCommandHandler(mode_down, mode_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(mode_up, mode_handler, 1, (void *) UP);

    // B737-Classic modes
    XPLMUnregisterCommandHandler(b737cl_mode_fullvorils, b737cl_mode_handler, 1, (void *) B737CL_FULL_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_mode_expvorils, b737cl_mode_handler, 1, (void *) B737CL_EXP_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_mode_map, b737cl_mode_handler, 1, (void *) B737CL_MAP);
    XPLMUnregisterCommandHandler(b737cl_mode_ctrmap, b737cl_mode_handler, 1, (void *) B737CL_CTR_MAP);
    XPLMUnregisterCommandHandler(b737cl_mode_plan, b737cl_mode_handler, 1, (void *) B737CL_PLAN);

    // radio1
    XPLMUnregisterCommandHandler(radio1_adf, radio1_handler, 1, (void *) RADIO_ADF);
    XPLMUnregisterCommandHandler(radio1_off, radio1_handler, 1, (void *) RADIO_OFF);
    XPLMUnregisterCommandHandler(radio1_nav, radio1_handler, 1, (void *) RADIO_NAV);
    XPLMUnregisterCommandHandler(radio1_down, radio1_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(radio1_up, radio1_handler, 1, (void *) UP);

    // radio2
    XPLMUnregisterCommandHandler(radio2_adf, radio2_handler, 1, (void *) RADIO_ADF);
    XPLMUnregisterCommandHandler(radio2_off, radio2_handler, 1, (void *) RADIO_OFF);
    XPLMUnregisterCommandHandler(radio2_nav, radio2_handler, 1, (void *) RADIO_NAV);
    XPLMUnregisterCommandHandler(radio2_down, radio2_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(radio2_up, radio2_handler, 1, (void *) UP);

    // source
    XPLMUnregisterCommandHandler(source_nav1, source_handler, 1, (void *) SOURCE_NAV1);
    XPLMUnregisterCommandHandler(source_nav2, source_handler, 1, (void *) SOURCE_NAV2);
    XPLMUnregisterCommandHandler(source_fmc, source_handler, 1, (void *) SOURCE_FMC);
    XPLMUnregisterCommandHandler(source_down, source_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(source_up, source_handler, 1, (void *) UP);

    // tfc
    XPLMUnregisterCommandHandler(tfc_toggle, tfc_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(tfc_on, tfc_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(tfc_off, tfc_handler, 1, (void *) OFF);

    // arpt
    XPLMUnregisterCommandHandler(arpt_toggle, arpt_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(arpt_on, arpt_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(arpt_off, arpt_handler, 1, (void *) OFF);

    // wpt
    XPLMUnregisterCommandHandler(wpt_toggle, wpt_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(wpt_on, wpt_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(wpt_off, wpt_handler, 1, (void *) OFF);

    // vor
    XPLMUnregisterCommandHandler(vor_toggle, vor_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(vor_on, vor_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(vor_off, vor_handler, 1, (void *) OFF);

    // ndb
    XPLMUnregisterCommandHandler(ndb_toggle, ndb_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(ndb_on, ndb_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(ndb_off, ndb_handler, 1, (void *) OFF);

    // sta = vor + ndb
    XPLMUnregisterCommandHandler(sta_toggle, sta_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(sta_on, sta_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(sta_off, sta_handler, 1, (void *) OFF);

    // data = route data
    XPLMUnregisterCommandHandler(data_toggle, data_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(data_on, data_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(data_off, data_handler, 1, (void *) OFF);

    // pos
    XPLMUnregisterCommandHandler(pos_toggle, pos_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(pos_on, pos_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(pos_off, pos_handler, 1, (void *) OFF);


    // copilot commands
    // copilot ctr
    XPLMUnregisterCommandHandler(copilot_ctr_toggle, copilot_ctr_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_ctr_on, copilot_ctr_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_ctr_off, copilot_ctr_handler, 1, (void *) OFF);

    // copilot range
    XPLMUnregisterCommandHandler(copilot_range_10, copilot_range_handler, 1, (void *) RANGE_10);
    XPLMUnregisterCommandHandler(copilot_range_20, copilot_range_handler, 1, (void *) RANGE_20);
    XPLMUnregisterCommandHandler(copilot_range_40, copilot_range_handler, 1, (void *) RANGE_40);
    XPLMUnregisterCommandHandler(copilot_range_80, copilot_range_handler, 1, (void *) RANGE_80);
    XPLMUnregisterCommandHandler(copilot_range_160, copilot_range_handler, 1, (void *) RANGE_160);
    XPLMUnregisterCommandHandler(copilot_range_320, copilot_range_handler, 1, (void *) RANGE_320);
    XPLMUnregisterCommandHandler(copilot_range_640, copilot_range_handler, 1, (void *) RANGE_640);
    XPLMUnregisterCommandHandler(copilot_range_down, copilot_range_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(copilot_range_up, copilot_range_handler, 1, (void *) UP);

    // copilot mode
    XPLMUnregisterCommandHandler(copilot_mode_app, copilot_mode_handler, 1, (void *) SUBMODE_APP);
    XPLMUnregisterCommandHandler(copilot_mode_vor, copilot_mode_handler, 1, (void *) SUBMODE_VOR);
    XPLMUnregisterCommandHandler(copilot_mode_map, copilot_mode_handler, 1, (void *) SUBMODE_MAP);
    XPLMUnregisterCommandHandler(copilot_mode_nav, copilot_mode_handler, 1, (void *) SUBMODE_NAV);
    XPLMUnregisterCommandHandler(copilot_mode_pln, copilot_mode_handler, 1, (void *) SUBMODE_PLN);
    XPLMUnregisterCommandHandler(copilot_mode_down, copilot_mode_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(copilot_mode_up, copilot_mode_handler, 1, (void *) UP);

    // copilot B737-Classic modes
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_fullvorils, b737cl_copilot_mode_handler, 1, (void *) B737CL_FULL_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_expvorils, b737cl_copilot_mode_handler, 1, (void *) B737CL_EXP_VOR_ILS);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_map, b737cl_copilot_mode_handler, 1, (void *) B737CL_MAP);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_ctrmap, b737cl_copilot_mode_handler, 1, (void *) B737CL_CTR_MAP);
    XPLMUnregisterCommandHandler(b737cl_copilot_mode_plan, b737cl_copilot_mode_handler, 1, (void *) B737CL_PLAN);

    // copilot radio1
    XPLMUnregisterCommandHandler(copilot_radio1_adf, copilot_radio1_handler, 1, (void *) RADIO_ADF);
    XPLMUnregisterCommandHandler(copilot_radio1_off, copilot_radio1_handler, 1, (void *) RADIO_OFF);
    XPLMUnregisterCommandHandler(copilot_radio1_nav, copilot_radio1_handler, 1, (void *) RADIO_NAV);
    XPLMUnregisterCommandHandler(copilot_radio1_down, copilot_radio1_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(copilot_radio1_up, copilot_radio1_handler, 1, (void *) UP);

    // copilot radio2
    XPLMUnregisterCommandHandler(copilot_radio2_adf, copilot_radio2_handler, 1, (void *) RADIO_ADF);
    XPLMUnregisterCommandHandler(copilot_radio2_off, copilot_radio2_handler, 1, (void *) RADIO_OFF);
    XPLMUnregisterCommandHandler(copilot_radio2_nav, copilot_radio2_handler, 1, (void *) RADIO_NAV);
    XPLMUnregisterCommandHandler(copilot_radio2_down, copilot_radio2_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(copilot_radio2_up, copilot_radio2_handler, 1, (void *) UP);

    // copilot source
    XPLMUnregisterCommandHandler(copilot_source_nav1, copilot_source_handler, 1, (void *) SOURCE_NAV1);
    XPLMUnregisterCommandHandler(copilot_source_nav2, copilot_source_handler, 1, (void *) SOURCE_NAV2);
    XPLMUnregisterCommandHandler(copilot_source_fmc, copilot_source_handler, 1, (void *) SOURCE_FMC);
    XPLMUnregisterCommandHandler(copilot_source_down, copilot_source_handler, 1, (void *) DOWN);
    XPLMUnregisterCommandHandler(copilot_source_up, copilot_source_handler, 1, (void *) UP);

    // copilot tfc
    XPLMUnregisterCommandHandler(copilot_tfc_toggle, copilot_tfc_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_tfc_on, copilot_tfc_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_tfc_off, copilot_tfc_handler, 1, (void *) OFF);

    // copilot arpt
    XPLMUnregisterCommandHandler(copilot_arpt_toggle, copilot_arpt_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_arpt_on, copilot_arpt_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_arpt_off, copilot_arpt_handler, 1, (void *) OFF);

    // copilot wpt
    XPLMUnregisterCommandHandler(copilot_wpt_toggle, copilot_wpt_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_wpt_on, copilot_wpt_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_wpt_off, copilot_wpt_handler, 1, (void *) OFF);

    // copilot vor
    XPLMUnregisterCommandHandler(copilot_vor_toggle, copilot_vor_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_vor_on, copilot_vor_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_vor_off, copilot_vor_handler, 1, (void *) OFF);

    // copilot ndb
    XPLMUnregisterCommandHandler(copilot_ndb_toggle, copilot_ndb_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_ndb_on, copilot_ndb_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_ndb_off, copilot_ndb_handler, 1, (void *) OFF);

    // copilot sta = vor + ndb
    XPLMUnregisterCommandHandler(copilot_sta_toggle, copilot_sta_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_sta_on, copilot_sta_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_sta_off, copilot_sta_handler, 1, (void *) OFF);

    // copilot data
    XPLMUnregisterCommandHandler(copilot_data_toggle, copilot_data_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_data_on, copilot_data_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_data_off, copilot_data_handler, 1, (void *) OFF);

    // copilot pos
    XPLMUnregisterCommandHandler(copilot_pos_toggle, copilot_pos_handler, 1, (void *) TOGGLE);
    XPLMUnregisterCommandHandler(copilot_pos_on, copilot_pos_handler, 1, (void *) ON);
    XPLMUnregisterCommandHandler(copilot_pos_off, copilot_pos_handler, 1, (void *) OFF);


    XPLMDebugString("XHSI: custom command handlers unregistered\n");

}
