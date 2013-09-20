#include <stdio.h>
#include <string.h>

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif


#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"

#include "xfmc.h"
#include "endianess.h"
#include "plugin.h"
#include "globals.h"
#include "settings.h"
#include "net.h"


XPLMDataRef xfmc_panel_lines_ref[NUM_XFMC_LINES];
XPLMDataRef xfmc_keypath_ref;
XPLMDataRef xfmc_status_ref;

struct XfmcLinesDataPacket xfmcPacket;

float xfmc_delay;

void findXfmcDataRefs(void) {
	int 		i;
	char		buf[100];

	xfmc_panel_lines_ref[0] = XPLMFindDataRef("xfmc/Upper");

	for (i=1; i<NUM_XFMC_LINES-1; i++) {
		sprintf(buf, "xfmc/Panel_%d", i);
		xfmc_panel_lines_ref[i] = XPLMFindDataRef(buf);
	}

	xfmc_panel_lines_ref[13] = XPLMFindDataRef("xfmc/Scratch");
	xfmc_keypath_ref = XPLMFindDataRef("xfmc/Keypath");
	xfmc_status_ref = XPLMFindDataRef("xfmc/Status");
}


int createXfmcPacket(void) {

   int i;

   strncpy(xfmcPacket.packet_id, "XFMC", 4);
   xfmcPacket.nb_of_lines = custom_htoni( NUM_XFMC_LINES);
   xfmcPacket.status = custom_htoni(XPLMGetDatai(xfmc_status_ref));
   for(i=0; i<NUM_XFMC_LINES; i++){
     xfmcPacket.lines[i].lineno = custom_htoni(i);
     XPLMGetDatab(xfmc_panel_lines_ref[i],xfmcPacket.lines[i].linestr,0,sizeof(xfmcPacket.lines[i].linestr));
     xfmcPacket.lines[i].len = custom_htoni((int)strlen(xfmcPacket.lines[i].linestr));
   }

  return 4 + 4 + 4 + 14 * 88;

}



float sendXfmcCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int packet_size;


	xfmc_delay = fms_data_delay;

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open)  {

		packet_size = createXfmcPacket();

        if ( packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    if (sendto(sockfd, (const char*)&xfmcPacket, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr)) == -1) {
                        XPLMDebugString("XHSI: caught error while sending XFMC packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
                }
            }
            return xfmc_delay;
        } else {
            return xfmc_delay;
        }

	} else {
		return xfmc_delay;
	}

}



