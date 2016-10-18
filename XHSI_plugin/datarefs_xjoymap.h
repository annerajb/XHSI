/*
 * datarefs_xjoymap.h
 *
 *  Created on: 22 sept. 2016
 *      Author: Nicolas Carel
 */

#ifndef DATAREFS_XJOYMAP_H_
#define DATAREFS_XJOYMAP_H_

extern XPLMDataRef xjoymap_side_stick_priority;
extern XPLMDataRef xjoymap_dual_input;

extern XPLMCommandRef xjoymap_stick_capt;
extern XPLMCommandRef xjoymap_stick_dual;
extern XPLMCommandRef xjoymap_stick_fo;

extern int xjoymap_ready;

// global functions
float checkXjoymapCallback(float, float, int, void *);

#endif /* DATAREFS_XJOYMAP_H_ */
