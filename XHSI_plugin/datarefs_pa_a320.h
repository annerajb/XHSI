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
extern XPLMDataRef pa_a320_thr_a_floor;
extern XPLMDataRef pa_a320_fd1_yaw_bar;
extern XPLMDataRef pa_a320_fd2_yaw_bar;
extern XPLMDataRef pa_a320_fd_yaw_bar_show;

extern int pa_a320_ready;
extern int pa_a320_version;

// global functions
float	checkPaA320Callback(float, float, int, void *);

#endif /* DATAREFS_PA_A320_H_ */
