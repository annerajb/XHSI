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
#define MCDU_BUF_LEN 80
#define EWD_BUF_LEN 80


// ECAM
extern XPLMDataRef qpac_ewd_red[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_blue[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_amber[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_green[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_white[QPAC_EWD_LINES];

extern int qpac_mcdu_ready;
extern int qpac_ewd_ready;

// MCDU

#define QPAC_MCDU_LINES 14

// MCDU1
extern XPLMDataRef qpac_mcdu1_title_yellow;
extern XPLMDataRef qpac_mcdu1_title_blue;
extern XPLMDataRef qpac_mcdu1_title_green;
extern XPLMDataRef qpac_mcdu1_title_white;

extern XPLMDataRef qpac_mcdu1_label_yellow[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_label_blue[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_label_amber[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_label_green[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_label_white[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_label_magenta[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_label_special[QPAC_MCDU_LINES];

extern XPLMDataRef qpac_mcdu1_small_yellow[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_small_blue[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_small_amber[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_small_green[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_small_white[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_small_magenta[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_small_special[QPAC_MCDU_LINES];

extern XPLMDataRef qpac_mcdu1_content_yellow[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_content_blue[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_content_amber[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_content_green[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_content_white[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_content_magenta[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu1_content_special[QPAC_MCDU_LINES];

extern XPLMDataRef qpac_mcdu1_scratch_yellow;
extern XPLMDataRef qpac_mcdu1_scratch_green;
extern XPLMDataRef qpac_mcdu1_scratch_white;

extern struct QpacEwdMsgLinesDataPacket qpacEwdMsgPacket;

extern struct QpacMcduMsgLinesDataPacket qpacMcduMsgPacket;

struct qpacEwdDisplayLine {
	int lineno;
	int len;
	char linestr[EWD_BUF_LEN];
};

struct qpacMcduDisplayLine {
	int lineno;
	int len;
	char linestr[MCDU_BUF_LEN];
};

struct QpacEwdMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	struct qpacEwdDisplayLine lines[QPAC_EWD_LINES];
};

struct QpacMcduMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	struct qpacMcduDisplayLine lines[QPAC_MCDU_LINES];
};


int createQpacEwdPacket(void);
int createQpacMcduPacket(void);


float sendQpacMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);

void findQpacMsgDataRefs(void);

#endif /* QPAC_MSG_H_ */
