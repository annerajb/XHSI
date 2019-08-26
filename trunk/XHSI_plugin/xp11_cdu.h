/*
 * xp11_cdu.h
 *
 *  Created on: 25 août 2019
 *      Author: carel
 */

#ifndef XP11_CDU_H_
#define XP11_CDU_H_

// XP11_CDU_BUF_LEN = QPAC_CDU_BUF_LEN = 80
#define XP11_CDU_BUF_LEN 80
#define XP11_CDU_MAX_MSG_COUNT 7
#define XP11_CDU_LINES 16

// CDU TEXT LINE is UTF-8 encoded, may be up to 48 bytes
#define XP11_CDU_TEXT_LINE_WIDTH 48
#define XP11_CDU_LINE_WIDTH 24
#define XP11_CDU_BUF_LEN 80

struct xp11CduDisplayLine {
	int lineno;
	int len;
	char linestr[XP11_CDU_BUF_LEN];
};

/**
 * The XP11 CDU Msg data packet format contains:
 * packet_id : QPAM (Compatible with Qpac MCDU data packet
 * nb_of_lines : 0 to 16 lines (QPAC MCDU is limit to 14 lines)
 * side : 0=left MCDU 1=right MCDU 2=observer MCDU
 * status : bit field
 *       - MCDU_FLAG_EXEC
 *       - MCDU_FLAG_MSG
 */
struct xp11CduMsgLinesDataPacket {
    char packet_id[4];
	int nb_of_lines;
	int side;
	int status;
	struct xp11CduDisplayLine lines[XP11_CDU_LINES];
};

float sendXP11CduMsgCallback(float, float, int, void *);

#endif /* XP11_CDU_H_ */
