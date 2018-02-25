/*
 * ufmc.h
 *
 *  Created on: 4 févr. 2018
 *      Author: Nicolas Carel
 */

#ifndef UFMC_H_
#define UFMC_H_

#include "datarefs_ufmc.h"

extern struct UfmcLinesDataPacket ufmcPacket;


struct UFmcDisplayLine {
	int lineno;
	int len;
	char linestr[80];
};

struct UfmcLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	int status;
	struct UFmcDisplayLine lines[NUM_UFMC_LINES];
};


int createUfmcPacket(void);

float sendUfmcCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);

float sendUfmcExtendedFmsCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);

#endif /* UFMC_H_ */
