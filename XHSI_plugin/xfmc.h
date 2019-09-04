/*
 * xfmc.h
 *
 *
 */

#ifndef XFMC_H_
#define XFMC_H_

#define NUM_XFMC_LINES 14
#define XFMC_MAX_MSG_COUNT 7

extern XPLMDataRef xfmc_panel_lines_ref[NUM_XFMC_LINES];
extern XPLMDataRef xfmc_keypath_ref;
extern struct XfmcLinesDataPacket xfmcPacket;

extern int xfmc_keypressed;
extern int xfmc_msg_count;

struct XFmcDisplayLine {
	int lineno;
	int len;
	char linestr[80];
};

struct XfmcLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	int status;
	struct XFmcDisplayLine lines[NUM_XFMC_LINES];
};

void findXfmcDataRefs(void);
int createXfmcPacket(void);

float sendXfmcCallback(
						float	inElapsedSinceLastCall,
						float	inElapsedTimeSinceLastFlightLoop,
						int		inCounter,
						void *	inRefcon);

#endif /* XFMC_H_ */

