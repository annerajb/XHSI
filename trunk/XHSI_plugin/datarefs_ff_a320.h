/*
 * datarefs_ff_a320.h
 *
 *  Created on: 5 march 2018
 *      Author: https://www.thi.de/en/
 */

#ifndef DATAREFS_FF_A320_H_
#define DATAREFS_FF_A320_H_

#define XPLM_FF_SIGNATURE "unknown"

extern XPLMDataRef ff_a320_plugin_status;

extern int ff_a320_ready;

// global functions
float checkFlightFactorA320Callback(float, float, int, void *);
void findFlightFactorA320DataRefs();
void writeFlightFactorA320Data(int, float);


#define FF_APU_MASTER_CONTACTS 4889
#define FF_APU_MASTER_POSITION 4890
#define FF_APU_MASTER_PRESSED 4891
#define FF_APU_MASTER_TARGET 4892
#define FF_APU_MASTER_STATE 4893
#define FF_APU_START_CONTACTS 4895
#define FF_APU_START_POSITION 4895
#define FF_APU_START_TARGET 4895
#define FF_APU_START_CLICK 4895
#define FF_APU_START_STATE 4895


#endif /* DATAREFS_FF_A320_H_ */
