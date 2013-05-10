
#define NUM_XFMC_LINES 14

extern XPLMDataRef xfmc_panel_lines_ref[NUM_XFMC_LINES];
extern XPLMDataRef xfmc_keypath_ref;
extern struct XfmcLinesDataPacket xfmcPacket;


struct XFmcDisplayLine {
	long lineno;
	long len;
	char linestr[80];
};

struct XfmcLinesDataPacket {
    char packet_id[4];
	long nb_of_lines;
	long status;
	struct XFmcDisplayLine lines[NUM_XFMC_LINES];
};

void findXfmcDataRefs(void);
int createXfmcPacket(void);

float sendXfmcCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon);


