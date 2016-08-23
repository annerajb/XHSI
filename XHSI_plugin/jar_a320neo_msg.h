/*
 * jar_a320_msg.h
 *
 *  Created on: 10 oct 2015
 *      Author: Nicolas Carel
 */

#ifndef JAR_A320NEO_MSG_H_
#define JAR_A320NEO_MSG_H_


// ECAM Memo & Messages
#define JAR_A320_EWD_LINES 7
#define JAR_A320_SD_LINES 18
#define MCDU_BUF_LEN 80
#define EWD_BUF_LEN 80

extern int jar_a320_mcdu_ready;
extern int jar_a320_ewd_ready;

// MCDU

#define JAR_A320_MCDU_LINES 14

extern struct jar_a320EwdMsgLinesDataPacket jar_a320EwdMsgPacket;

extern struct jar_a320McduMsgLinesDataPacket jar_a320McduMsgPacket;

struct jar_a320EwdDisplayLine {
	int lineno;
	int len;
	char linestr[EWD_BUF_LEN];
};

struct jar_a320McduDisplayLine {
	int lineno;
	int len;
	char linestr[MCDU_BUF_LEN];
};

struct jar_a320EwdMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	struct jar_a320EwdDisplayLine lines[JAR_A320_EWD_LINES];
};

struct jar_a320McduMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	int side;
	struct jar_a320McduDisplayLine lines[JAR_A320_MCDU_LINES];
};


int createJar_a320EwdPacket(void);
int createJar_a320McduPacket(void);


float sendJar_a320MsgCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);

void findJar_a320MsgDataRefs(void);

#endif /* JAR_A320NEO_MSG_H_ */
