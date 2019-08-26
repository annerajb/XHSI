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
#define QPAC_MAX_EWD_MSG_COUNT 4

// ECAM
extern XPLMDataRef qpac_ewd_red[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_blue[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_amber[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_green[QPAC_EWD_LINES];
extern XPLMDataRef qpac_ewd_white[QPAC_EWD_LINES];

extern int qpac_mcdu_ready;
extern int qpac_ewd_ready;

extern int qpac_mcdu_msg_count;
extern int qpac_ewd_msg_count;
extern int qpac_mcdu_keypressed;

// MCDU
#define QPAC_MCDU_LINES 14
#define QPAC_MAX_MCDU_MSG_COUNT 7

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
extern XPLMDataRef qpac_mcdu1_scratch_amber;

// MCDU2
extern XPLMDataRef qpac_mcdu2_title_yellow;
extern XPLMDataRef qpac_mcdu2_title_blue;
extern XPLMDataRef qpac_mcdu2_title_green;
extern XPLMDataRef qpac_mcdu2_title_white;

extern XPLMDataRef qpac_mcdu2_label_yellow[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_label_blue[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_label_amber[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_label_green[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_label_white[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_label_magenta[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_label_special[QPAC_MCDU_LINES];

extern XPLMDataRef qpac_mcdu2_small_yellow[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_small_blue[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_small_amber[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_small_green[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_small_white[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_small_magenta[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_small_special[QPAC_MCDU_LINES];

extern XPLMDataRef qpac_mcdu2_content_yellow[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_content_blue[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_content_amber[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_content_green[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_content_white[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_content_magenta[QPAC_MCDU_LINES];
extern XPLMDataRef qpac_mcdu2_content_special[QPAC_MCDU_LINES];

extern XPLMDataRef qpac_mcdu2_scratch_yellow;
extern XPLMDataRef qpac_mcdu2_scratch_green;
extern XPLMDataRef qpac_mcdu2_scratch_white;
extern XPLMDataRef qpac_mcdu2_scratch_amber;

// FLAGS
#define MCDU_FLAG_EXEC 0x01
#define MCDU_FLAG_MSG  0x02


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

/**
 * The QPAC MCDU Msg data packet format contains:
 * packet_id : QPAM
 * nb_of_lines : 0 to 14 lines
 * side : 0=left MCDU 1=right MCDU 2=observer MCDU
 * status : bit field
 *       - MCDU_FLAG_EXEC
 *       - MCDU_FLAG_MSG
 */
struct QpacMcduMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	int side;
	int status;
	struct qpacMcduDisplayLine lines[QPAC_MCDU_LINES];
};


int createQpacEwdPacket(void);
int createQpacMcduPacket(int mcdu_id);


float sendQpacMsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);

void findQpacMsgDataRefs(void);

#endif /* QPAC_MSG_H_ */
