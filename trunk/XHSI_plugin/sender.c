/*
 * sender.c
 *
 * callback functions to send the packets
 *
 * used by: plugin.c
 * uses: net.c, packets.c
 *
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#define XPLM200 1

// mingw-64 demands that winsock2.h is loaded before windows.h
#if IBM
#include <winsock2.h>
#endif

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
//#include "XPWidgets.h"
//#include "XPStandardWidgets.h"


#include "globals.h"
#include "plugin.h"
#include "settings.h"
#include "structs.h"
#include "packets.h"
#include "net.h"
#include "datarefs.h"
#include "datarefs_qpac.h"

//char pack_msg[200];

float fwc_chrono=0;
int   fwc_chrono_running=0;

float sendADCCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createADCPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending ADCD packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending ADCD packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		if ( send_error )
			return 1.0f;
		else
			return adc_data_delay;

	} else {
		return 1.0f;
	}

}


float sendAuxiliarySystemsCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createAuxiliarySystemsPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending AUXS packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending AUXS packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		if ( send_error )
			return 2.0f;
		else
			return aux_sys_data_delay;

	} else {
		return 1.0f;
	}

}

/**
 * Compute total fuel flow
 */
float totalFuelFlow() {
	float tab_fuel_flow[8];
	float total_fuel_flow=0;
	int engines,e;
    engines = XPLMGetDatai(num_engines);
    XPLMGetDatavf(fuel_flow, tab_fuel_flow, 0, engines);
    for (e=0; e<engines; e++) {
    	total_fuel_flow += tab_fuel_flow[e];
    }
    return total_fuel_flow;
}


float sendAvionicsCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif
	int sim_run = !XPLMGetDatai(sim_paused);
    int engines = XPLMGetDatai(num_engines);
    float engifloat[8];

    /*
     * Compute Avionics custom datarefs
     */
	if (xhsi_plugin_enabled && sim_run) {
		// Compute internal chrono
	    if (fwc_chrono_running) {
	    	fwc_chrono+=inElapsedSinceLastCall;
	    }
		// FWC : Flight Warning Computer
		// Final State Automate
		switch (XPLMGetDatai(fwc_phase)) {
		case 0: // Off
			if (XPLMGetDatai(avionics_on)==1) XPLMSetDatai(fwc_phase, 1);
			// If power is on, switch to 1
			break;
		case 1:
			// if one engine started, switch to 2
			// positive fuel flow or without engine
		    if (engines==0 || (totalFuelFlow()>0)) XPLMSetDatai(fwc_phase, 2);
			break;
		case 2:
			if (engines==0 && XPLMGetDataf(airspeed_pilot)>20) {
				// no engine and speed > 20 kts, switch to 3
				XPLMSetDatai(fwc_phase, 3);
			} else {
				// take off thrust (75%), switch to 3
				XPLMGetDatavf(engine_n1, engifloat, 0, engines);
				if ( engifloat[0] > 75.0f ) XPLMSetDatai(fwc_phase, 3);
			}
			break;
		case 3:
			// 80 kts on jet engine, 40 kts on piston engine, switch to 4
			if (XPLMGetDataf(airspeed_pilot)>80.0f) XPLMSetDatai(fwc_phase, 4);
			// Rejected take off, back to phase 2
			XPLMGetDatavf(engine_n1, engifloat, 0, engines);
			if ( (engifloat[0] < 25.0f) && (XPLMGetDataf(airspeed_pilot)<30.0f)) XPLMSetDatai(fwc_phase, 2);
			break;
		case 4:
			// lift off, switch to 5
			if (XPLMGetDatai(on_ground)!=1) {
				XPLMSetDatai(fwc_phase, 5);
				fwc_chrono_running = 1;
				fwc_chrono = 0;
			}
			// Rejected take off, back to phase 2
			XPLMGetDatavf(engine_n1, engifloat, 0, engines);
			if ( (engifloat[0] < 25.0f) && (XPLMGetDataf(airspeed_pilot)<30.0f)) XPLMSetDatai(fwc_phase, 2);
			break;
		case 5:
			// 1500 ft jet engine, 300 ft piston engine, or 2mn, switch to 6
			if (XPLMGetDataf(agl)* 3.28084f>1500.0f || (fwc_chrono>120)) {
				XPLMSetDatai(fwc_phase, 6);
				fwc_chrono_running = 0;
			}
			break;
		case 6:
			// 800 ft jet engine, 150 ft piston, switch to 7
			if (XPLMGetDataf(agl)* 3.28084f<800.0f) XPLMSetDatai(fwc_phase, 7);
			break;
		case 7:
			// touch down, switch to 8
			// > 1500 ft, back to 6
			if (XPLMGetDataf(agl)* 3.28084f>1500.0f) XPLMSetDatai(fwc_phase, 6);
			if (XPLMGetDatai(on_ground)==1) XPLMSetDatai(fwc_phase, 8);
			// Go around thrust (75%), switch to 5
			XPLMGetDatavf(engine_n1, engifloat, 0, engines);
			if ( engifloat[0] > 75.0f ) XPLMSetDatai(fwc_phase, 5);
			break;
		case 8:
			// below 80 kts jet engine, below 40 kts piston engine, switch to 9
			if (XPLMGetDataf(airspeed_pilot)<80.0f) XPLMSetDatai(fwc_phase, 9);
			// Touch and go thrust (75%), switch to 4
			XPLMGetDatavf(engine_n1, engifloat, 0, engines);
			if ( engifloat[0] > 75.0f ) XPLMSetDatai(fwc_phase, 4);
			break;
		case 9:
			// all engine shut down, switch to 10
			if (engines==0 || (totalFuelFlow()==0)) {
				XPLMSetDatai(fwc_phase, 10);
				fwc_chrono_running = 1;
				fwc_chrono = 0;
			}
			break;
		case 10:
			// 5 mn after, back to 0
			// If engine relight, back to 2
			if ((totalFuelFlow()>0) ) {
				XPLMSetDatai(fwc_phase, 2);
				fwc_chrono_running = 0;
			}
			if (fwc_chrono>300) {
				XPLMSetDatai(fwc_phase, 0);
				fwc_chrono_running = 0;
			}
			break;
		default:
			// Unknown state, reset to 0
			XPLMSetDatai(fwc_phase,0);
			break;
		}
	}

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createAvionicsPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending AVIO packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending AVIO packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		packet_size = createCustomAvionicsPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending AVIO packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending AVIO packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		if ( send_error )
			return 2.0f;
		else
			return avionics_data_delay;

	} else {
		return 1.0f;
	}

}


float sendEnginesCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif
	int engines;
	int e;
	float tab_fuel_flow[8];
	float tab_fuel_used[8];
	float crew_oxygen;
	float cabin_alt;
	float oxy_ratio=100.0f;
    int sim_run = !XPLMGetDatai(sim_paused);

    /*
     * Compute Engine custom datarefs
     */
	if (xhsi_plugin_enabled && sim_run) {
		// Compute fuel used
	    engines = XPLMGetDatai(num_engines);
	    XPLMGetDatavf(fuel_flow, tab_fuel_flow, 0, engines);
	    XPLMGetDatavf(mfd_fuel_used, tab_fuel_used, 0, engines);
	    for (e=0; e<engines; e++) {
	    	tab_fuel_used[e] += tab_fuel_flow[e] * inElapsedSinceLastCall;
	    }
	    XPLMSetDatavf(mfd_fuel_used, tab_fuel_used, 0, engines);
	    // Compute Oxygen Crew
	    crew_oxygen = XPLMGetDataf(mfd_crew_oxy_psi);
	    if (crew_oxygen > 0.0f) {

	    	// Small leak : 1 psi per hour
	    	crew_oxygen -= 0.001f * inElapsedSinceLastCall;
	    	if (qpac_ready && (qpac_crew_oxy_mask!=NULL)) {
	    		// cabin_alt = XPLMGetDataf(qpac_cabin_alt);
	    		cabin_alt = XPLMGetDataf(cabin_altitude);
	    		if (XPLMGetDatai(qpac_crew_oxy_mask) == 1){
	    			// O2 is diluted when cabin is below 35000 feet
	    			if (cabin_alt<35000.0f)
	    				oxy_ratio = (cabin_alt-8000.0f > 6500.0f ? cabin_alt-8000.0f : 6500.0f)/27000.0f;
	    			else
	    				oxy_ratio = 100.0f;
	    			crew_oxygen -= (0.4794f * oxy_ratio - 0.05827f) * inElapsedSinceLastCall;
	    		}
	    	}
	    	XPLMSetDataf(mfd_crew_oxy_psi, crew_oxygen);

	    	/* Airbus with mask on : 800 psi for 15 mn at cabin altitude of 8000 feet 100% O2
	    	 *  - checked / coherent with formula
	    	 * if qpac, rely on qpac_crew_oxy_mask = 1 and on cabin altitude
	    	 * A320 AMM 35.11.00 page 1:
	    	 *   The cylinder has a capacity of 2183 l (77 cubic feet) (NTPD: Normal
	    	 *   Temperature Pressure Dry) at a pressure of 127.5 bar (1850 psig).
	    	 * A330 AMM 35.11.00 page 7 : Same cylinder
	    	 * Medical table for 1 adult body :
	    	 * 100% O2 = 15 l/mn = 0.250 l/s [Maximum]
	    	 * 80% O2 = 10 l/mn = 0.166 l/s
	    	 * 40% O2 = 6 l/mn = 0.100 l/s
	    	 * 24% O2 = 2 l/mn = 0.033 l/s [Minimum]
	    	 * Formula from oxygen ratio to l/s (linear approximation)
	    	 * f(x) = 0.283 * x - 0.0334
	    	 * Formula from oxygen ratio to psi/s for 2 pilot based on A320 cylinder capacity
	    	 * f(x) = 0.4794 * x - 0.05827
	    	 * TODO : temperature correction (in flight Ref. temp. = cabin temp. - 10° c)
	    	 */
	    }


	}


	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createEnginesPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending ENGI packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending ENGI packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		if ( send_error )
			return 3.0f;
		else
			return engines_data_delay;

	} else {
		return 1.0f;
	}

}


float sendStaticCallback(
                                   float	inElapsedSinceLastCall,
                                   float	inElapsedTimeSinceLastFlightLoop,
                                   int		inCounter,
                                   void *	inRefcon) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open) {

		packet_size = createStaticPacket();

		for (i=0; i<NUM_DEST; i++) {
			if (dest_enable[i]) {
				res = sendto(sockfd, (const char*)&sim_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
				if ( res == SOCKET_ERROR ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending STAT packet! (");
                    sprintf(msg, "%d", WSAGetLastError());
					XPLMDebugString(msg);
					XPLMDebugString(")\n");
				}
#else
				if ( res < 0 ) {
					send_error = 1;
					XPLMDebugString("XHSI: caught error while sending STAT packet! (");
					XPLMDebugString((char * const) strerror(GET_ERRNO));
					XPLMDebugString(")\n");
				}
#endif
			}
		}

		if ( send_error )
			return 4.0f;
		else
			return static_data_delay;

	} else {
		return 1.0f;
	}

}


float sendFmsCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int j;
	int waypoint_count;
	int packet_size;
	int last_pack;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif


	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open && fms_source==FMS_SOURCE_LEGACY)  {

		waypoint_count = createFmsPackets();
        last_pack = ( waypoint_count - 1 ) / MAX_FMS_ENTRIES_ALLOWED;

        for (j=0; j<=last_pack; j++) {

            // packet size : char[4] + float + float + int + int + int + ( # * ( int + char[8] + float + float + float ) )
            // packet size : 24 + # * 24;
            if ( j == last_pack ) {
                packet_size = 24 + ( waypoint_count % MAX_FMS_ENTRIES_ALLOWED ) * 24;
            } else {
                packet_size = 24 + ( MAX_FMS_ENTRIES_ALLOWED ) * 24;
            }
//sprintf(pack_msg, "XHSI: sending %c%c%c%c \n",fms_packet[j].packet_id[0],fms_packet[j].packet_id[1],fms_packet[j].packet_id[2],fms_packet[j].packet_id[3]);
//XPLMDebugString(pack_msg);

            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    res = sendto(sockfd, (const char*)&fms_packet[j], packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
                    if ( res == SOCKET_ERROR ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending FMSx packet! (");
                        sprintf(msg, "%d", WSAGetLastError());
                        XPLMDebugString(msg);
                        XPLMDebugString(")\n");
                    }
#else
                    if ( res < 0 ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending FMSx packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
#endif
                }
            }

        }

		if ( send_error )
			return 5.0f;
		else
			return fms_data_delay;

	} else {
		return 1.0f;
	}

}


float sendTcasCallback(
									float	inElapsedSinceLastCall,
									float	inElapsedTimeSinceLastFlightLoop,
									int		inCounter,
									void *	inRefcon) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif

	if (xhsi_plugin_enabled && xhsi_send_enabled && xhsi_socket_open)  {

		packet_size = createTcasPacket();

        if ( packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    res = sendto(sockfd, (const char*)&tcas_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
                    if ( res == SOCKET_ERROR ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending TCAS packet! (");
                        sprintf(msg, "%d", WSAGetLastError());
                        XPLMDebugString(msg);
                        XPLMDebugString(")\n");
                    }
#else
                    if ( res < 0 ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending TCAS packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
#endif
                }
            }

			if ( send_error )
				return 6.0f;
			else
				return tcas_data_delay;

        } else {
            return 1.0f;
        }

	} else {
		return 1.0f;
	}

}

int sendRemoteCommand(int cmd) {

	int i;
	int packet_size;
	int res;
	int send_error = 0;
#if IBM
	char msg[80];
#endif

	if (xhsi_plugin_enabled && xhsi_socket_open)  {

		packet_size = createRemoteCommandPacket(cmd);

        if ( packet_size > 0 ) {
            for (i=0; i<NUM_DEST; i++) {
                if (dest_enable[i]) {
                    res = sendto(sockfd, (const char*)&rcmd_packet, packet_size, 0, (struct sockaddr *)&dest_sockaddr[i], sizeof(struct sockaddr));
#if IBM
                    if ( res == SOCKET_ERROR ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending RCMD packet! (");
                        sprintf(msg, "%d", WSAGetLastError());
                        XPLMDebugString(msg);
                        XPLMDebugString(")\n");
                    }
#else
                    if ( res < 0 ) {
						send_error = 1;
                        XPLMDebugString("XHSI: caught error while sending RCMD packet! (");
                        XPLMDebugString((char * const) strerror(GET_ERRNO));
                        XPLMDebugString(")\n");
                    }
#endif
                }
            }

        }

	}
	return send_error;
}


