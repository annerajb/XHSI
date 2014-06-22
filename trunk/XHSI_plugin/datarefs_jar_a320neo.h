/*
 * datarefs_jar_a320neo.h
 *
 *  Created on: 9 juin 2014
 *      Author: carel
 */

#ifndef DATAREFS_JAR_A320NEO_H_
#define DATAREFS_JAR_A320NEO_H_

extern XPLMDataRef jar_a320_neo_plugin_status;
extern XPLMDataRef jar_a320_neo_baro_hpa;
extern XPLMDataRef jar_a320_neo_fcu_hdg_trk;

extern int jar_a320_neo_ready;

// global functions
float	checkJarA320NeoCallback(float, float, int, void *);


#endif /* DATAREFS_JAR_A320NEO_H_ */
