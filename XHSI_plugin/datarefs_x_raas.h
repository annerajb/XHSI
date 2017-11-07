/*
 * datarefs_x_raas.h
 *
 *  Created on: 7 nov. 2017
 *      Author: Nicolas Carel
 *
 *  X-RAAS is an open-source plugin that simulates
 *  the Honeywell Runway Awareness and Advisory System
 *  by Skiselkov [Copyright (c) 2016 Saso Kiselkov]
 *  http://forums.x-pilot.com/files/file/1047-x-raas-runway-awareness-and-advisory-system/
 *
 *  Starting support in XHSI since version 2.0.3 (10 april 2017)
 *  Updated : Version 2.1 (2 nov 2017)
 *
 */

#ifndef DATAREFS_X_RAAS_H_
#define DATAREFS_X_RAAS_H_

// plugin status and version
extern int x_raas_ready;
extern int x_raas_version;

// DataRefs for X-RAAS
// GPWS integration
extern XPLMDataRef x_raas_gpws_prio;
extern XPLMDataRef x_raas_gpws_prio_act;
extern XPLMDataRef x_raas_gpws_inop;
extern XPLMDataRef x_raas_gpws_inop_act;
extern XPLMDataRef x_raas_gpws_flaps;
extern XPLMDataRef x_raas_gpws_flaps_act;
extern XPLMDataRef x_raas_gpws_terr;
extern XPLMDataRef x_raas_gpws_terr_act;
// Approach speed monitor
extern XPLMDataRef x_raas_vapp;
extern XPLMDataRef x_raas_vapp_act;
extern XPLMDataRef x_raas_vref;
extern XPLMDataRef x_raas_vref_act;
// Exact flaps setting checks
extern XPLMDataRef x_raas_to_flaps;
extern XPLMDataRef x_raas_to_flaps_act;
extern XPLMDataRef x_raas_ldg_flaps;
extern XPLMDataRef x_raas_ldg_flaps_act;
extern XPLMDataRef flaps_request;
// ND Alert
extern XPLMDataRef x_raas_nd_alert;
extern XPLMDataRef x_raas_nd_alert_disabled;
// Air data fault
extern XPLMDataRef x_raas_input_faulted;

// global functions
float checkXRaasCallback(float, float, int, void *);

#endif /* DATAREFS_X_RAAS_H_ */
