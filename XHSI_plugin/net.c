
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#if IBM
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <sys/errno.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <fcntl.h>
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


#if IBM
#define GET_ERRNO WSAGetLastError()
#else
#define GET_ERRNO errno
#endif

#if IBM
#if !defined(SIO_UDP_CONNRESET)
#define SIO_UDP_CONNRESET _WSAIOW(IOC_VENDOR,12)
#endif
#endif


// define global vars
#if IBM
SOCKET sockfd;
#else
int sockfd;
#endif
struct sockaddr_in recv_sockaddr, dest_sockaddr[NUM_DEST];

// define local vars
int xhsi_socket_open;


#if IBM
int startWinsock() {
	WSADATA wsa;
	WORD version;
	int rc;
	char debug_message[256];

	//version = (2<<8)+1;
	version = MAKEWORD(2, 2);

	rc = WSAStartup(version, &wsa);

	if ( rc ) {
		sprintf(debug_message, "XHSI failed: could not start winsock! errno: %d\n", WSAGetLastError());
		XPLMDebugString(debug_message);
		return 0;
	} else {
		XPLMDebugString("XHSI: winsock started\n");
		return 1;
	}
}
#endif


int openSocket() {

	char debug_message[256];

    XPLMDebugString("XHSI: opening socket\n");

#if IBM

	if ( startWinsock() == 0 ) {
	    XPLMDebugString("XHSI: start winsock failed\n");
		return 0;
	}

	// create socket
	//sockfd = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
	sockfd = socket(PF_INET, SOCK_DGRAM, 0);

	if ( sockfd == INVALID_SOCKET ) {
		sprintf(debug_message, "XHSI: failed - could not open socket! errno: %d\n", GET_ERRNO);
		XPLMDebugString(debug_message);
		return 0;
	} else {
		XPLMDebugString("XHSI: socket opened\n");
	}

#else

	// create socket
	//sockfd = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
	sockfd = socket(PF_INET, SOCK_DGRAM, 0);

	if ( sockfd < 0 ) {
		sprintf(debug_message, "XHSI: failed - could not open socket! errno: %d\n", GET_ERRNO);
		XPLMDebugString(debug_message);
		return 0;
	} else {
		XPLMDebugString("XHSI: socket created\n");
	}

#endif

    return 1;

}


void setAddresses() {

	char addr_cleartext[200]; // 1.0 Beta 8 : [100] was too small...
	int i;

	XPLMDebugString("XHSI: setting addresses\n");

    // receiver address
	recv_sockaddr.sin_family = AF_INET;
	recv_sockaddr.sin_port = htons(recv_port);
	recv_sockaddr.sin_addr.s_addr = INADDR_ANY;
	memset(&(recv_sockaddr.sin_zero), '\0', 8);

	sprintf(addr_cleartext, "XHSI: recv : Port=%d\n",
			recv_port);
	XPLMDebugString(addr_cleartext);

    // destination address
	for ( i=0; i<NUM_DEST; i++ ) {
		if (dest_enable[i]) {
			dest_sockaddr[i].sin_family = AF_INET;
			dest_sockaddr[i].sin_port = htons(dest_port[i]);
			dest_sockaddr[i].sin_addr.s_addr = inet_addr(dest_ip[i]);
			memset(&(dest_sockaddr[i].sin_zero), '\0', 8);

			sprintf(addr_cleartext, "XHSI: dest[%d] : IP=%s  Port=%d  SIMD-delay=%f  FMSR-delay=%f  TCAS-delay=%f\n",
					i, dest_ip[i], dest_port[i], adc_data_delay, fms_data_delay, tcas_data_delay);
			XPLMDebugString(addr_cleartext);
		}
	}

}


void closeSocket() {

	if ( xhsi_socket_open ) {

        XPLMDebugString("XHSI: closing socket\n");
#if IBM
		if ( closesocket(sockfd) == -1 ) {
#else
		if ( close(sockfd) == -1 ) {
#endif
			XPLMDebugString("XHSI: failed - caught error while closing socket! (");
			XPLMDebugString((char * const) strerror(GET_ERRNO));
			XPLMDebugString(")\n");
		} else {
			XPLMDebugString("XHSI: socket closed\n");
		}
#if IBM
        WSACleanup();
#endif
		xhsi_socket_open = 0;
		xhsi_send_enabled = 0;

	}

}


int bindSocket() {

	if ( ! xhsi_socket_open ) {

		XPLMDebugString("XHSI: binding socket\n");

		if (bind(sockfd, (struct sockaddr*)&recv_sockaddr, sizeof(struct sockaddr_in)) == -1) {
			XPLMDebugString("XHSI: caught error while binding socket (");
			XPLMDebugString((char * const) strerror(GET_ERRNO));
			XPLMDebugString(")\n");
			xhsi_socket_open = 0;
			return -1;
		} else {
			XPLMDebugString("XHSI: socket bound\n");
			xhsi_socket_open = 1;
			return 1;
		}

	} else {
		return 1;
	}

}


int nonBlocking() {

#if IBM
    DWORD nonBlocking = 1;
    DWORD dwBytesReturned = 0;
    BOOL bNewBehavior = FALSE;
#else
    int nonBlocking = 1;
#endif


    XPLMDebugString("XHSI: setting socket to non-blocking\n");

#if IBM

    if ( ioctlsocket( sockfd, FIONBIO, &nonBlocking ) != 0 )
    {
        XPLMDebugString("XHSI: failed to set socket non-blocking!\n");
        return 0;
    }
    // see http://support.microsoft.com/?kbid=263823
    if ( WSAIoctl(sockfd, SIO_UDP_CONNRESET, &bNewBehavior, sizeof(bNewBehavior), 0, 0, &dwBytesReturned, 0, 0) == SOCKET_ERROR )
    {
        XPLMDebugString("XHSI: Unable to ignore ICMP_Unreachable!\n");
    }

#else

    if ( fcntl( sockfd, F_SETFL, O_NONBLOCK, nonBlocking ) == -1 )
    {
        XPLMDebugString("XHSI: failed to set socket non-blocking!\n");
        return 0;
    }

#endif

    XPLMDebugString("XHSI: socket set to non-blocking\n");

    return 1;

}

