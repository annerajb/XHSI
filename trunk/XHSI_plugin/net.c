
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#if IBM
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <sys/errno.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#endif


#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"
#include "XPWidgets.h"
#include "XPStandardWidgets.h"


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



// define global vars
#if IBM
SOCKET sockfd;
#else
int sockfd;
#endif
struct sockaddr_in recv_sockaddr, dest_sockaddr[NUM_DEST];
//struct sockaddr    orig_sockaddr;

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

	#if IBM
	if ( startWinsock() == 0 ) {
	    XPLMDebugString("XHSI: start winsock failed\n");
		return 0;
	}
	#endif

	// create socket
	//sockfd = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
	sockfd = socket(PF_INET, SOCK_DGRAM, 0);

    #if IBM
	if ( sockfd == INVALID_SOCKET ) {
    #else
	if ( sockfd < 0 ) {
    #endif
		sprintf(debug_message, "XHSI: failed - could not open socket! errno: %d\n", GET_ERRNO);
		XPLMDebugString(debug_message);
		return 0;
	} else {
		XPLMDebugString("XHSI: socket created\n");
	}

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

//        // doesn't seem to help...
//        #if IBM
//            // Set the re-use address option
//            int optval = 1;
//            int optresult = setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR,
//                         (char *) &optval, sizeof (optval));
//            if (optresult == SOCKET_ERROR) {
//                XPLMDebugString("XHSI: setsockopt for SO_REUSEADDR failed!\n");
//                //WSAGetLastError();
//            }
//        #endif

		if (bind(sockfd, (struct sockaddr*)&recv_sockaddr, sizeof(struct sockaddr_in)) == -1) {
			XPLMDebugString("XHSI: caught error while binding socket (");
			XPLMDebugString((char * const) strerror(GET_ERRNO));
			XPLMDebugString(")\n");
			xhsi_socket_open = 0;
			return -1;
		} else {
			XPLMDebugString("XHSI: socket bound!\n");
			xhsi_socket_open = 1;
			return 1;
		}

	} else {
		return 1;
	}

}


int pollReceive() {

    int             res;
    fd_set          sready;
    struct timeval  nowait;

    FD_ZERO(&sready);
    FD_SET((unsigned int)sockfd, &sready);
    nowait.tv_sec = 0;    // specify how many seconds you would like to wait for timeout
    nowait.tv_usec = 0;   // how many microseconds? If both is zero, select will return immediately

    res = select(sockfd+1, &sready, NULL, NULL, &nowait);
    if( FD_ISSET(sockfd, &sready) )
        return 1;
    else
        return 0;
}


void resetSocket() {
    closeSocket();
    openSocket();
    setAddresses();
    bindSocket();
}

