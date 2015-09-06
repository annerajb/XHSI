/*
 * qpac_msg.h
 *
 *  Created on: 17 july 2015
 *      Author: Nicolas Carel
 */

#ifndef QPAC_MSG_H_
#define QPAC_MSG_H_



// ECAM Memo & Messages
#define QPAC_EWD_LINES 7
#define QPAC_SD_LINES 18

extern XPLMDataRef qpac_emsg_red[QPAC_EWD_LINES];
extern XPLMDataRef qpac_emsg_blue[QPAC_EWD_LINES];
extern XPLMDataRef qpac_emsg_amber[QPAC_EWD_LINES];
extern XPLMDataRef qpac_emsg_green[QPAC_EWD_LINES];

// MCDU

#define QPAC_MCDU_LINES 6

extern struct QpacMsgLinesDataPacket qpacMsgPacket;


struct qpacDisplayLine {
	int lineno;
	int len;
	char linestr[80];
};

struct QpacMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	struct qpacDisplayLine lines[QPAC_EWD_LINES];
};

int createQpacMsgPacket(void);

float sendQpacMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);


#endif /* QPAC_MSG_H_ */
