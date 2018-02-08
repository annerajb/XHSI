/**
* datarefs_ufmc.c
*
* Collect UFMC/x737FMC datarefs based on v3.4 (2018)
* Thanks to Javier Cortes.
*
* Copyright (C) ????       Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2017-2018  Nicolas Carel
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
#include "datarefs_ufmc.h"
#include "ids.h"

// DataRefs for the UFMC / x737FMC

XPLMDataRef ufmc_plugin_status;
XPLMPluginID ufmcPluginId = XPLM_NO_PLUGIN_ID;

XPLMDataRef ufmc_v1;
XPLMDataRef ufmc_vr;
XPLMDataRef ufmc_v2;
XPLMDataRef ufmc_vref;
XPLMDataRef ufmc_vf30;
XPLMDataRef ufmc_vf40;

// Engine performance
XPLMDataRef ufmc_eng_n1_1;
XPLMDataRef ufmc_eng_n1_2;
XPLMDataRef ufmc_eng_n1_3;
XPLMDataRef ufmc_eng_n1_4;

XPLMDataRef ufmc_eng_x_1;
XPLMDataRef ufmc_eng_x_2;
XPLMDataRef ufmc_eng_x_3;
XPLMDataRef ufmc_eng_x_4;

XPLMDataRef ufmc_eng_z_1;
XPLMDataRef ufmc_eng_z_2;
XPLMDataRef ufmc_eng_z_3;
XPLMDataRef ufmc_eng_z_4;

XPLMDataRef ufmc_eng_rev_1;
XPLMDataRef ufmc_eng_rev_2;
XPLMDataRef ufmc_eng_rev_3;
XPLMDataRef ufmc_eng_rev_4;


XPLMDataRef ufmc_accel_altitude;
XPLMDataRef ufmc_dist_to_tc;
XPLMDataRef ufmc_dist_to_td;

XPLMDataRef ufmc_flight_phase;
XPLMDataRef ufmc_offset_on;
XPLMDataRef ufmc_thr_red_altitude;
XPLMDataRef ufmc_vdev;
XPLMDataRef ufmc_zfw;
XPLMDataRef ufmc_zfw_4500;

// Flight Plan
XPLMDataRef ufmc_waypt_altitude;
XPLMDataRef ufmc_waypt_dme_arc;
XPLMDataRef ufmc_waypt_dme_arc_dme;
XPLMDataRef ufmc_waypt_dme_arc_side;
XPLMDataRef ufmc_waypt_eta;
XPLMDataRef ufmc_waypt_fly_over;
XPLMDataRef ufmc_waypt_index;
XPLMDataRef ufmc_waypt_lat;
XPLMDataRef ufmc_waypt_lon;
XPLMDataRef ufmc_waypt_name;
XPLMDataRef ufmc_waypt_number;
XPLMDataRef ufmc_waypt_only_draw;
XPLMDataRef ufmc_waypt_pln_index;
XPLMDataRef ufmc_waypt_sid_star_app;
XPLMDataRef ufmc_waypt_speed;
XPLMDataRef ufmc_waypt_speed_tas;
XPLMDataRef ufmc_waypt_toc_tod;
XPLMDataRef ufmc_waypt_type_altitude;

XPLMDataRef ufmc_version;

// CDU Screen
XPLMDataRef ufmc_exec_light_on;
XPLMDataRef ufmc_line[NUM_UFMC_LINES];

// CDU Keys
XPLMDataRef ufmc_keys[UFMC_KEY_MAX];



int ufmc_ready = 0;


void findUFMCDataRefs(void) {
	int i;
	char c;
	char dataref_str[50];
	char umfc_debug_str[80];

	ufmcPluginId = XPLMFindPluginBySignature("FJCC.x737FMC");
	ufmc_plugin_status = XPLMFindDataRef("FJCC/UFMC/PRESENT");

	if ( ufmcPluginId == XPLM_NO_PLUGIN_ID ) {

        ufmc_ready = 0;

    } else {

        if ( ufmc_ready == 0 ) {

            ufmc_ready = 1;

            XPLMDebugString("XHSI: using FJCC UFMC/X737FMC DataRefs v3.4\n");

            ufmc_v1 = XPLMFindDataRef("FJCC/UFMC/V1");
            ufmc_vr = XPLMFindDataRef("FJCC/UFMC/Vr");
            ufmc_v2 = XPLMFindDataRef("FJCC/UFMC/V2");
            ufmc_vref = XPLMFindDataRef("FJCC/UFMC/VREFM1");
            ufmc_vf30 = XPLMFindDataRef("FJCC/UFMC/VF30");
            ufmc_vf40 = XPLMFindDataRef("FJCC/UFMC/VF40");

            ufmc_eng_n1_1 = XPLMFindDataRef("FJCC/Engine/N1_1");
            ufmc_eng_n1_2 = XPLMFindDataRef("FJCC/Engine/N1_2");
            ufmc_eng_n1_3 = XPLMFindDataRef("FJCC/Engine/N1_3");
            ufmc_eng_n1_4 = XPLMFindDataRef("FJCC/Engine/N1_4");

            ufmc_eng_x_1 = XPLMFindDataRef("FJCC/Engine/X_1");
            ufmc_eng_x_2 = XPLMFindDataRef("FJCC/Engine/X_2");
            ufmc_eng_x_3 = XPLMFindDataRef("FJCC/Engine/X_3");
            ufmc_eng_x_4 = XPLMFindDataRef("FJCC/Engine/X_4");

            ufmc_eng_z_1 = XPLMFindDataRef("FJCC/Engine/Z_1");
            ufmc_eng_z_2 = XPLMFindDataRef("FJCC/Engine/Z_2");
            ufmc_eng_z_3 = XPLMFindDataRef("FJCC/Engine/Z_3");
            ufmc_eng_z_4 = XPLMFindDataRef("FJCC/Engine/Z_4");

            ufmc_eng_rev_1 = XPLMFindDataRef("FJCC/Engine/reverse_1");
            ufmc_eng_rev_2 = XPLMFindDataRef("FJCC/Engine/reverse_2");
            ufmc_eng_rev_3 = XPLMFindDataRef("FJCC/Engine/reverse_3");
            ufmc_eng_rev_4 = XPLMFindDataRef("FJCC/Engine/reverse_4");

            ufmc_accel_altitude = XPLMFindDataRef("FJCC/UFMC/Acceleration_Altitude");
            ufmc_dist_to_tc = XPLMFindDataRef("FJCC/UFMC/Dist_to_TC");
            ufmc_dist_to_td = XPLMFindDataRef("FJCC/UFMC/Dist_to_TD");
            ufmc_exec_light_on = XPLMFindDataRef("FJCC/UFMC/Exec_Light_on");
            ufmc_flight_phase = XPLMFindDataRef("FJCC/UFMC/Flight_Phase");
            ufmc_offset_on = XPLMFindDataRef("FJCC/UFMC/Offset_on");
            ufmc_thr_red_altitude = XPLMFindDataRef("FJCC/UFMC/Thrust_Reduction_Altitude");
            ufmc_vdev = XPLMFindDataRef("FJCC/UFMC/Vertical_Deviation");
            ufmc_zfw = XPLMFindDataRef("FJCC/UFMC/ZFW");
            ufmc_zfw_4500 = XPLMFindDataRef("FJCC/UFMC/ZFW4500");

            ufmc_waypt_altitude = XPLMFindDataRef("FJCC/UFMC/Waypoint/Altitude");
            ufmc_waypt_dme_arc = XPLMFindDataRef("FJCC/UFMC/Waypoint/DME_ARC");
            ufmc_waypt_dme_arc_dme = XPLMFindDataRef("FJCC/UFMC/Waypoint/DME_ARC_DME");
            ufmc_waypt_dme_arc_side = XPLMFindDataRef("FJCC/UFMC/Waypoint/DME_ARC_Side");
            ufmc_waypt_eta = XPLMFindDataRef("FJCC/UFMC/Waypoint/Eta");
            ufmc_waypt_fly_over = XPLMFindDataRef("FJCC/UFMC/Waypoint/Fly_Over");
            ufmc_waypt_index = XPLMFindDataRef("FJCC/UFMC/Waypoint/Index");
            ufmc_waypt_lat = XPLMFindDataRef("FJCC/UFMC/Waypoint/Lat");
            ufmc_waypt_lon = XPLMFindDataRef("FJCC/UFMC/Waypoint/Lon");
            ufmc_waypt_name = XPLMFindDataRef("FJCC/UFMC/Waypoint/Name");
            ufmc_waypt_number = XPLMFindDataRef("FJCC/UFMC/Waypoint/Number_of_Waypoints");
            ufmc_waypt_only_draw = XPLMFindDataRef("FJCC/UFMC/Waypoint/Only_Draw");
            ufmc_waypt_pln_index = XPLMFindDataRef("FJCC/UFMC/Waypoint/PLN_Index");
            ufmc_waypt_sid_star_app = XPLMFindDataRef("FJCC/UFMC/Waypoint/SidStarApp");
            ufmc_waypt_speed = XPLMFindDataRef("FJCC/UFMC/Waypoint/Speed");
            ufmc_waypt_speed_tas = XPLMFindDataRef("FJCC/UFMC/Waypoint/Speed_TAS");
            ufmc_waypt_toc_tod = XPLMFindDataRef("FJCC/UFMC/Waypoint/Toctod");
            ufmc_waypt_type_altitude = XPLMFindDataRef("FJCC/UFMC/Waypoint/Type_Altitude");

            ufmc_version = XPLMFindDataRef("FJCC/UFMC/x737FMC_Version");

            for (i=0; i<NUM_UFMC_LINES; i++ ) {
            	sprintf(dataref_str,"FJCC/UFMC/LINE_%d",i+1);
            	ufmc_line[i] = XPLMFindDataRef(dataref_str);
            	sprintf(umfc_debug_str,"XHSI: getting %s\n", dataref_str);
            	XPLMDebugString(umfc_debug_str);
            }

            for (i=UFMC_KEY_CDU_A, c='A'; i<UFMC_KEY_CDU_Z; i++, c++ ) {
            	sprintf(dataref_str,"FJCC/UFMC/%c",c);
				ufmc_keys[i] = XPLMFindDataRef(dataref_str);
            }
            for (i=UFMC_KEY_CDU_0, c='0'; i<UFMC_KEY_CDU_9; i++, c++ ) {
            	sprintf(dataref_str,"FJCC/UFMC/%c",c);
				ufmc_keys[i] = XPLMFindDataRef(dataref_str);
            }
            ufmc_keys[UFMC_KEY_CDU_INIT] = XPLMFindDataRef("FJCC/UFMC/INITREF");
            ufmc_keys[UFMC_KEY_CDU_LSK1L] = XPLMFindDataRef("FJCC/UFMC/LK1");
            ufmc_keys[UFMC_KEY_CDU_LSK2L] = XPLMFindDataRef("FJCC/UFMC/LK2");
            ufmc_keys[UFMC_KEY_CDU_LSK3L] = XPLMFindDataRef("FJCC/UFMC/LK3");
            ufmc_keys[UFMC_KEY_CDU_LSK4L] = XPLMFindDataRef("FJCC/UFMC/LK4");
            ufmc_keys[UFMC_KEY_CDU_LSK5L] = XPLMFindDataRef("FJCC/UFMC/LK5");
            ufmc_keys[UFMC_KEY_CDU_LSK6L] = XPLMFindDataRef("FJCC/UFMC/LK6");
            ufmc_keys[UFMC_KEY_CDU_LSK1R] = XPLMFindDataRef("FJCC/UFMC/RK1");
            ufmc_keys[UFMC_KEY_CDU_LSK2R] = XPLMFindDataRef("FJCC/UFMC/RK2");
            ufmc_keys[UFMC_KEY_CDU_LSK3R] = XPLMFindDataRef("FJCC/UFMC/RK3");
            ufmc_keys[UFMC_KEY_CDU_LSK4R] = XPLMFindDataRef("FJCC/UFMC/RK4");
            ufmc_keys[UFMC_KEY_CDU_LSK5R] = XPLMFindDataRef("FJCC/UFMC/RK5");
            ufmc_keys[UFMC_KEY_CDU_LSK6R] = XPLMFindDataRef("FJCC/UFMC/RK6");

            ufmc_keys[UFMC_KEY_CDU_INIT] = XPLMFindDataRef("FJCC/UFMC/INITREF");
            ufmc_keys[UFMC_KEY_CDU_RTE] = XPLMFindDataRef("FJCC/UFMC/RTE");
            ufmc_keys[UFMC_KEY_CDU_DEP_ARR] = XPLMFindDataRef("FJCC/UFMC/DEPARR");
            ufmc_keys[UFMC_KEY_CDU_AP] = NULL;  //XPLMFindDataRef("FJCC/UFMC/");
            ufmc_keys[UFMC_KEY_CDU_VNAV] = XPLMFindDataRef("FJCC/UFMC/VNAV");
            ufmc_keys[UFMC_KEY_CDU_BRT] = NULL;  //XPLMFindDataRef("FJCC/UFMC/");
            ufmc_keys[UFMC_KEY_CDU_FIX] = XPLMFindDataRef("FJCC/UFMC/FIX");
            ufmc_keys[UFMC_KEY_CDU_LEGS] = XPLMFindDataRef("FJCC/UFMC/LEGS");
            ufmc_keys[UFMC_KEY_CDU_HOLD] = XPLMFindDataRef("FJCC/UFMC/HOLD");
            ufmc_keys[UFMC_KEY_CDU_PERF] = NULL;  //XPLMFindDataRef("FJCC/UFMC/");
            ufmc_keys[UFMC_KEY_CDU_PROG] = XPLMFindDataRef("FJCC/UFMC/PROG");
            ufmc_keys[UFMC_KEY_CDU_EXEC] = XPLMFindDataRef("FJCC/UFMC/EXEC");
            ufmc_keys[UFMC_KEY_CDU_MENU] = XPLMFindDataRef("FJCC/UFMC/MENU");
            ufmc_keys[UFMC_KEY_CDU_RAD_NAV] = XPLMFindDataRef("FJCC/UFMC/NAVRAD");
            ufmc_keys[UFMC_KEY_CDU_SLEW_LEFT] = XPLMFindDataRef("FJCC/UFMC/PREVPAGE");
            ufmc_keys[UFMC_KEY_CDU_SLEW_RIGHT] = XPLMFindDataRef("FJCC/UFMC/NEXTPAGE");

            ufmc_keys[UFMC_KEY_CDU_DEL] = XPLMFindDataRef("FJCC/UFMC/DEL");
            ufmc_keys[UFMC_KEY_CDU_SLASH] = XPLMFindDataRef("FJCC/UFMC/barra");
            ufmc_keys[UFMC_KEY_CDU_CLR] = XPLMFindDataRef("FJCC/UFMC/CLR");

            ufmc_keys[UFMC_KEY_CDU_DOT] = XPLMFindDataRef("FJCC/UFMC/punto");
            ufmc_keys[UFMC_KEY_CDU_PLUS_M] = XPLMFindDataRef("FJCC/UFMC/menos");
            ufmc_keys[UFMC_KEY_CDU_SPACE] = XPLMFindDataRef("FJCC/UFMC/espacio");

        }

    }

}


float checkUFMCCallback(
        float	inElapsedSinceLastCall,
        float	inElapsedTimeSinceLastFlightLoop,
        int		inCounter,
        void *	inRefcon) {

    findUFMCDataRefs();

    // come back in 5sec
    return 5.0;

}

void writeUFmcDataRef(int id, float value) {

    switch (id) {

    case UFMC_KEY_PRESS :
    	if ((int)value < UFMC_KEY_MAX) {
    		if (ufmc_keys[(int)value] != NULL) XPLMSetDataf(ufmc_keys[(int)value], 1.0f);
    	}

    	break;
    }

}
