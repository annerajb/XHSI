/*
 * datarefs_pa_a320.h
 *
 *  Created on: 1 may 2014
 *      Author: Nicolas Carel
 */

#ifndef DATAREFS_PA_A320_H_
#define DATAREFS_PA_A320_H_

extern XPLMDataRef pa_a320_plugin_status;
extern XPLMDataRef pa_a320_baro_hide;
extern XPLMDataRef pa_a320_fcu_metric_alt;
extern XPLMDataRef pa_a320_fcu_hdg_trk;
extern XPLMDataRef pa_a320_ir_capt_avail;
extern XPLMDataRef pa_a320_pa1_horiz_bar;
extern XPLMDataRef pa_a320_alt_cruize;
extern XPLMDataRef pa_a320_fcu_fpa_sel;

extern int pa_a320_ready;

// global functions
float	checkPaA320Callback(float, float, int, void *);

#endif /* DATAREFS_PA_A320_H_ */
