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
 *
 */

#ifndef DATAREFS_X_RAAS_H_
#define DATAREFS_X_RAAS_H_

// plugin status and version
extern int x_raas_ready;
extern int x_raas_version;

// global functions
float checkXRaasCallback(float, float, int, void *);

#endif /* DATAREFS_X_RAAS_H_ */
