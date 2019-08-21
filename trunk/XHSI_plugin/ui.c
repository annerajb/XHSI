/**
 * ui.c
 *
 */


#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

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
#include "XPWidgets.h"
#include "XPStandardWidgets.h"


#include "globals.h"
#include "plugin.h"
#include "settings.h"
#include "net.h"
#include "sender.h"


// Menu items
#define MENU_ITEM_SETTINGS 0
#define MENU_REMOTE 1
//#define MENU_ITEM_RESET 2

#define REMOTE_TIP_TEXT1 "Clic to send command to remote XHSI"

// Define local vars
int			xhsi_settings_dialog_shows;
int			xhsi_settings_dialog_created;
int			xhsi_remote_dialog_shows;
int			xhsi_remote_dialog_created;

XPLMMenuID	menu_id;
int			menu_item;

// Settings box widgets
XPWidgetID	settings_dialog, settings_receiver_subwindow, settings_sender_subwindow, settings_dest_subwindow, settings_fms_subwindow;
XPWidgetID	dest_ip_textbox[NUM_DEST], dest_port_textbox[NUM_DEST];
XPWidgetID  src_port_textbox, nav_rate_textbox, fms_rate_textbox, tcas_rate_textbox;
XPWidgetID	version_label, receiver_label, sender_label, dest_label, dest_label1, dest_label2, dest_label3, dest_enable_label, dest_ip_label, dest_port_label, dest_default_label;
XPWidgetID  src_port_label, data_rate_label, nav_data_label, fms_data_label, tcas_data_label;
XPWidgetID	src_port_default_label, nav_data_default_label, fms_data_default_label, tcas_data_default_label;
XPWidgetID  dest_enable_checkbox[NUM_DEST];
XPWidgetID  default_local_button, default_multicast_button, cancel_button, set_button;
XPWidgetID  fms_label, fms_desc1_label, fms_desc2_label;
XPWidgetID  fms_auto_radiobutton, fms_legacy_radiobutton, fms_ufmc_radiobutton, fms_z737_fmc_radiobutton;
XPWidgetID  fms_auto_rb_label, fms_legacy_rb_label, fms_ufmc_rb_label, fms_z737_fmc_rb_label;

// Remote box widgets
XPWidgetID  remote_dialog;
XPWidgetID  rmt_tip;
XPWidgetID  rmt_close_subwindow, rmt_close_label, rmt_close_button;
XPWidgetID  rmt_shutdown_subwindow, rmt_shutdown_label, rmt_shutdown_button;
XPWidgetID  rmt_cancel_button;


void closeSettingsDialog() {

	if (xhsi_settings_dialog_shows) {
		XPHideWidget(settings_dialog);
		xhsi_settings_dialog_shows = 0;
		xhsi_send_enabled = 1;
	}

}

void closeRemoteDialog() {

	if (xhsi_remote_dialog_shows) {
		XPHideWidget(remote_dialog);
		xhsi_remote_dialog_shows = 0;
		xhsi_send_enabled = 1;
	}

}

void setWidgetValues() {

	int i;
	char val_buf[80];

	// recv_port
	sprintf(val_buf, "%d", recv_port);
	XPSetWidgetDescriptor(src_port_textbox, val_buf);

	// adc_data_rate
	sprintf(val_buf, "%ld", adc_data_rate);
	XPSetWidgetDescriptor(nav_rate_textbox, val_buf);

	// fms_data_rate
	sprintf(val_buf, "%ld", fms_data_rate);
	XPSetWidgetDescriptor(fms_rate_textbox, val_buf);

	// tcas_data_rate
	sprintf(val_buf, "%ld", tcas_data_rate);
	XPSetWidgetDescriptor(tcas_rate_textbox, val_buf);

	// dest
	for (i=0; i<NUM_DEST; i++) {
		// enable
		XPSetWidgetProperty(dest_enable_checkbox[i], xpProperty_ButtonState, dest_enable[i]);
		// ip
		XPSetWidgetDescriptor(dest_ip_textbox[i], dest_ip[i]);
		// port
		sprintf(val_buf, "%d", dest_port[i]);
		XPSetWidgetDescriptor(dest_port_textbox[i], val_buf);
	}

	XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonState, (fms_source==FMS_SOURCE_AUTO) ? 1 : 0);
	XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonState, (fms_source==FMS_SOURCE_LEGACY) ? 1: 0);
	XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonState, (fms_source==FMS_SOURCE_UFMC) ? 1 : 0);
	XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonState, (fms_source==FMS_SOURCE_Z737) ? 1 : 0);

}


// define the handler before it is used
XPWidgetFunc_t settingsDialogHandler(
							   XPWidgetMessage		inMessage,
							   XPWidgetID			inWidget,
							   intptr_t				inParam1,
							   intptr_t				inParam2) {

	char buffer[256];
	int i;

	// we are not displaying the close (X) to avoid uncertainty about its logic (is it Cancel or OK?)
	//if (inMessage == xpMessage_CloseButtonPushed) {
	//	// close (X) clicked
	//	close_dialog();
	//	return 1;
	//}

	if (inMessage == xpMsg_PushButtonPressed) {

		if (inParam1 == (intptr_t)default_local_button) {
			// Default Local button pressed, set the first destination to local with default port 49020

			XPSetWidgetProperty(dest_enable_checkbox[0], xpProperty_ButtonState, 1);
			XPSetWidgetDescriptor(dest_ip_textbox[0], DEFAULT_DEST_IP);
			sprintf(buffer, "%d", DEFAULT_DEST_PORT);
			XPSetWidgetDescriptor(dest_port_textbox[0], buffer);

			return (XPWidgetFunc_t)1;

		} else if (inParam1 == (intptr_t)default_multicast_button) {
			// Default Multicast button pressed, set the second destination to 239.255.0.120/49020

			XPSetWidgetProperty(dest_enable_checkbox[1], xpProperty_ButtonState, 1);
			XPSetWidgetDescriptor(dest_ip_textbox[1], DEFAULT_MULTICAST_IP);
			sprintf(buffer, "%d", DEFAULT_DEST_PORT);
			XPSetWidgetDescriptor(dest_port_textbox[1], buffer);

			return (XPWidgetFunc_t)1;

		} else if (inParam1 == (intptr_t)set_button) {
			// Set button pressed

			for (i=0; i<NUM_DEST; i++) {

				dest_enable[i] = (int)XPGetWidgetProperty(dest_enable_checkbox[i], xpProperty_ButtonState, NULL);

				XPGetWidgetDescriptor(dest_ip_textbox[i], dest_ip[i], 20);

				XPGetWidgetDescriptor(dest_port_textbox[i], buffer, sizeof(buffer));
				dest_port[i] = (unsigned int)strtol(buffer, NULL, 10);

			}

			XPGetWidgetDescriptor(src_port_textbox, buffer, sizeof(buffer));
			recv_port = (unsigned int)strtol(buffer, NULL, 10);

			XPGetWidgetDescriptor(nav_rate_textbox, buffer, sizeof(buffer));
			adc_data_rate = strtol(buffer, NULL, 10);
			adc_data_delay = 1.0f / (float)adc_data_rate;
            avionics_data_delay = adc_data_delay * 3.0f;
            engines_data_delay = adc_data_delay * 5.0f;
            static_data_delay = adc_data_delay * 15.0f;

			XPGetWidgetDescriptor(fms_rate_textbox, buffer, sizeof(buffer));
			fms_data_rate = strtol(buffer, NULL, 10);
			fms_data_delay = 1.0f / (float)fms_data_rate;

			XPGetWidgetDescriptor(tcas_rate_textbox, buffer, sizeof(buffer));
			tcas_data_rate = strtol(buffer, NULL, 10);
			tcas_data_delay = 1.0f / (float)tcas_data_rate;


			if ((int)XPGetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonState, NULL) ) {
				fms_source = FMS_SOURCE_AUTO;
			} else if ((int)XPGetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonState, NULL) ) {
				fms_source = FMS_SOURCE_LEGACY;
			} else if ((int)XPGetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonState, NULL) ) {
				fms_source = FMS_SOURCE_UFMC;
			} else if ((int)XPGetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonState, NULL) ) {
				fms_source = FMS_SOURCE_Z737;
			} else {
				// Unknown ? -> Auto
				fms_source = FMS_SOURCE_AUTO;
			}

			// write the new config to file
			writeSettings();

			setAddresses();
			bindSocket();
			closeSettingsDialog();
			return (XPWidgetFunc_t)1;

		} else if (inParam1 == (intptr_t)cancel_button) {
			// Cancel button pressed
			closeSettingsDialog();
			return (XPWidgetFunc_t)1;
		}

	}

	// Radio Buttons
    if (inMessage == xpMsg_ButtonStateChanged) {
    	if(inParam1 == (intptr_t)fms_auto_radiobutton) {
            XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty((XPWidgetID)inParam1, xpProperty_ButtonState, 1);
    	}
    	if(inParam1 == (intptr_t)fms_legacy_radiobutton) {
            XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty((XPWidgetID)inParam1, xpProperty_ButtonState, 1);
    	}
    	if(inParam1 == (intptr_t)fms_ufmc_radiobutton) {
            XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty((XPWidgetID)inParam1, xpProperty_ButtonState, 1);
    	}
    	if(inParam1 == (intptr_t)fms_z737_fmc_radiobutton) {
            XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonState, 0);
            XPSetWidgetProperty((XPWidgetID)inParam1, xpProperty_ButtonState, 1);
    	}
    }

	return 0;

}



void createSettingsDialog(int x, int y) {

	// remark: parameter values like x0+75+100+10+50+20+50 might seem childish, but they are easier to maintain

	// x, y : lower left
	int w = 400 + 30;
	int h = 460 + 20+2*15 + 20 + 20+ 140; // was: 385 ; now : 460
	// x0, y0 : top left
	int x0 = x;
	int y0 = y + h;
	// x2, y2 : bottom right
	int x2 = x + w;
	int y2 = y;
	// h1 : height of first subwindow
	int h1 = 60; // was: -15 ; now: 60
	// h2 : height of second subwindow
	int h2 = 120;
	// h3 : height of third subwindow
	int h3 = 140 + 20+2*15;
	// h4 : height of fouth subwindow
	int h4 = 140;
	// y1 : current line
	int y1;

	int i;

	// root window
	settings_dialog = XPCreateWidget(x0, y0, x2, y2,
										1,								// visible
										"XHSI Plugin Settings",		// window title
										1,								// root
										NULL,							// no container
										xpWidgetClass_MainWindow);
	// we are not displaying the close (X) to avoid uncertainty about its function login (is it Cancel or OK?)
	XPSetWidgetProperty(settings_dialog, xpProperty_MainWindowHasCloseBoxes, 0);

	version_label = XPCreateWidget(x0+80+10, y0-20-2-10, x0+80+210, y0-20-2-15-10,
							  1, PLUGIN_VERSION_TEXT, 0, settings_dialog,
							  xpWidgetClass_Caption);

	// receiver subwindow
	y1 = y0-45-20;
	settings_receiver_subwindow = XPCreateWidget(x0+20, y1, x2-20, y1-h1,
											   1,								// visible
											   "Receiver",						// window title
											   0,								// not root
											   settings_dialog,				// in container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(settings_receiver_subwindow, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	// receiver title and entries
	y1 -= 5;
	receiver_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "RECEIVER", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 25;
	src_port_label = XPCreateWidget(x0+30, y1+2, x0+30+55, y1+2-15,
										 1, "UDP Port :", 0, settings_dialog,
										 xpWidgetClass_Caption);
	src_port_textbox = XPCreateWidget(x0+90, y1, x0+90+50, y1-15,
											  1, "", 0, settings_dialog,
											  xpWidgetClass_TextField);
	src_port_default_label = XPCreateWidget(x0+145, y1+2, x0+145+220, y1+2-15,
		1, "(default: 49019)  (change requires restart)", 0, settings_dialog,
										 xpWidgetClass_Caption);

	// sender subwindow
	y1 = y0-45-20-h1-15;
	settings_sender_subwindow = XPCreateWidget(x0+20, y1, x2-20, y1-h2,
											   1,								// visible
											   "Sender",						// window title
											   0,								// not root
											   settings_dialog,				// in container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(settings_sender_subwindow, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	// sender title and entries
	y1 -= 5;
	sender_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "SENDER", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 25;
	data_rate_label = XPCreateWidget(x0+30, y1+2, x0+130, y1+2-15,
										 1, "Data transmissions per second", 0, settings_dialog,
										 xpWidgetClass_Caption);

	y1 -= 20;
	nav_data_label = XPCreateWidget(x0+75, y1+2, x0+105, y1+2-15,
										 1, "Sim", 0, settings_dialog,
										 xpWidgetClass_Caption);
	nav_rate_textbox = XPCreateWidget(x0+115, y1, x0+115+30, y1-15,
											  1, "", 0, settings_dialog,
											  xpWidgetClass_TextField);
	nav_data_default_label = XPCreateWidget(x0+150, y1+2, x0+150+100, y1+2-15,
										 1, "(default: 15)", 0, settings_dialog,
										 xpWidgetClass_Caption);

	y1 -= 20;
	fms_data_label = XPCreateWidget(x0+75, y1+2, x0+105, y1+2-15,
										 1, "FMS", 0, settings_dialog,
										 xpWidgetClass_Caption);
	fms_rate_textbox = XPCreateWidget(x0+115, y1, x0+115+30, y1-15,
										   1, "", 0, settings_dialog,
										   xpWidgetClass_TextField);
	fms_data_default_label = XPCreateWidget(x0+150, y1+2, x0+150+100, y1+2-15,
										 1, "(default: 2)", 0, settings_dialog,
										 xpWidgetClass_Caption);

	y1 -= 20;
	tcas_data_label = XPCreateWidget(x0+75, y1+2, x0+105, y1+2-15,
										 1, "TCAS", 0, settings_dialog,
										 xpWidgetClass_Caption);
	tcas_rate_textbox = XPCreateWidget(x0+115, y1, x0+115+30, y1-15,
										   1, "", 0, settings_dialog,
										   xpWidgetClass_TextField);
	tcas_data_default_label = XPCreateWidget(x0+150, y1+2, x0+150+100, y1+2-15,
										 1, "(default: 5)", 0, settings_dialog,
										 xpWidgetClass_Caption);

	// destination subwindow
	y1 = y0-45-20-h1-15-h2-15;
	settings_dest_subwindow = XPCreateWidget(x0+20, y1, x2-20, y1-h3,
											   1,								// visible
											   "Destinations",					// window title
											   0,								// not root
											   settings_dialog,				// in container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(settings_dest_subwindow, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	// destination title and entries
	y1 -= 5;
	dest_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "DESTINATIONS", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 20;
	dest_label1 = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "- can be the loopback address 127.0.0.1 for the local computer", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 15;
	dest_label2 = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "- can be the IP addresses of other computers on the network", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 15;
	dest_label3 = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "- can be a multicast address like the default 239.255.0.120", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 25;
	dest_enable_label = XPCreateWidget(x0+30, y1+2, x0+30+40, y1+2-15,
									   1, "Enable", 0, settings_dialog,
									   xpWidgetClass_Caption);

	dest_ip_label = XPCreateWidget(x0+75, y1+2, x0+75+100, y1+2-15,
									   1, "   IP address", 0, settings_dialog,
									   xpWidgetClass_Caption);

	dest_port_label = XPCreateWidget(x0+75+100+10, y1+2, x0+75+100+10+50, y1+2-15,
							  1, "UDP port", 0, settings_dialog,
							  xpWidgetClass_Caption);

	dest_default_label = XPCreateWidget(x0+75+100+10+50+20, y1+2, x0+75+100+10+50+20+50, y1+2-15,
							  1, "Default", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 20;
	dest_enable_checkbox[0] = XPCreateWidget(x0+30, y1, x0+30+30, y1-15,
									  1, "", 0, settings_dialog,
									  xpWidgetClass_Button);
	XPSetWidgetProperty(dest_enable_checkbox[0], xpProperty_ButtonType, xpRadioButton);
	XPSetWidgetProperty(dest_enable_checkbox[0], xpProperty_ButtonBehavior, xpButtonBehaviorCheckBox);
	XPSetWidgetProperty(dest_enable_checkbox[0], xpProperty_ButtonState, 1);

	dest_ip_textbox[0] = XPCreateWidget(x0+75, y1, x0+75+100, y1-15,
									  1, "", 0, settings_dialog,
									  xpWidgetClass_TextField);

	dest_port_textbox[0] = XPCreateWidget(x0+75+100+10, y1, x0+75+100+10+55, y1-15,
									   1, "", 0, settings_dialog,
									   xpWidgetClass_TextField);

	default_local_button = XPCreateWidget(x0+75+100+10+50+20, y1, x0+75+100+10+50+20+80, y1-15,
								  1, "Local", 0, settings_dialog,
								  xpWidgetClass_Button);
	XPSetWidgetProperty(default_local_button, xpProperty_ButtonType, xpPushButton);

	default_multicast_button = XPCreateWidget(x0+75+100+10+50+20, y1-20, x0+75+100+10+50+20+80, y1-20-15,
								  1, "Multicast", 0, settings_dialog,
								  xpWidgetClass_Button);
	XPSetWidgetProperty(default_multicast_button, xpProperty_ButtonType, xpPushButton);

	for (i=1; i<NUM_DEST; i++) {
		y1 -= 20;
		dest_enable_checkbox[i] = XPCreateWidget(x0+30, y1, x0+30+30, y1-15,
										  1, "", 0, settings_dialog,
										  xpWidgetClass_Button);
		XPSetWidgetProperty(dest_enable_checkbox[i], xpProperty_ButtonType, xpRadioButton);
		XPSetWidgetProperty(dest_enable_checkbox[i], xpProperty_ButtonBehavior, xpButtonBehaviorCheckBox);
		XPSetWidgetProperty(dest_enable_checkbox[i], xpProperty_ButtonState, 0);

		dest_ip_textbox[i] = XPCreateWidget(x0+75, y1, x0+75+100, y1-15,
										  1, "", 0, settings_dialog,
										  xpWidgetClass_TextField);

		dest_port_textbox[i] = XPCreateWidget(x0+75+100+10, y1, x0+75+100+10+55, y1-15,
										   1, "", 0, settings_dialog,
										   xpWidgetClass_TextField);
	}


	// FMS subwindow
	y1 = y0-45-20-h1-15-h2-15-h3-15;
	settings_fms_subwindow = XPCreateWidget(x0+20, y1, x2-20, y1-h4,
											   1,							// visible
											   "FMS",					    // window title
											   0,							// not root
											   settings_dialog,				// in container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(settings_fms_subwindow, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	// destination title and entries
	y1 -= 5;
	fms_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "FMS Source Selection", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 20;
	fms_desc1_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "X-Plane legacy FMS is not feed by enhanced Aircraft", 0, settings_dialog,
							  xpWidgetClass_Caption);
	y1 -= 20;
	fms_desc2_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "Auto send the first non empty FMS data, in the order bellow", 0, settings_dialog,
							  xpWidgetClass_Caption);

	y1 -= 20;
	fms_auto_radiobutton = XPCreateWidget(x0+30, y1, x0+30+30, y1-15,
									  1, "", 0, settings_dialog,
									  xpWidgetClass_Button);
	XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonType, xpRadioButton);
	XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonBehavior, xpButtonBehaviorRadioButton);
	XPSetWidgetProperty(fms_auto_radiobutton, xpProperty_ButtonState, 0);
	fms_auto_rb_label = XPCreateWidget(x0+75, y1, x0+75+100+10+55, y1-15,
									   1, "Automatic", 0, settings_dialog,
									   xpWidgetClass_Caption);

	y1 -= 20;
	fms_legacy_radiobutton = XPCreateWidget(x0+30, y1, x0+30+30, y1-15,
									  1, "", 0, settings_dialog,
									  xpWidgetClass_Button);
	XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonType, xpRadioButton);
	XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonBehavior, xpButtonBehaviorRadioButton);
	XPSetWidgetProperty(fms_legacy_radiobutton, xpProperty_ButtonState, 0);
	fms_legacy_rb_label = XPCreateWidget(x0+75, y1, x0+75+100+10+55, y1-15,
									   1, "X-Plane legacy", 0, settings_dialog,
									   xpWidgetClass_Caption);

	y1 -= 20;
	fms_ufmc_radiobutton = XPCreateWidget(x0+30, y1, x0+30+30, y1-15,
									  1, "", 0, settings_dialog,
									  xpWidgetClass_Button);
	XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonType, xpRadioButton);
	XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonBehavior, xpButtonBehaviorRadioButton);
	XPSetWidgetProperty(fms_ufmc_radiobutton, xpProperty_ButtonState, 0);
	fms_ufmc_rb_label = XPCreateWidget(x0+75, y1, x0+75+100+10+55, y1-15,
									   1, "UMFC x737FMC by Javier Cortes", 0, settings_dialog,
									   xpWidgetClass_Caption);

	y1 -= 20;
	fms_z737_fmc_radiobutton = XPCreateWidget(x0+30, y1, x0+30+30, y1-15,
									  1, "", 0, settings_dialog,
									  xpWidgetClass_Button);
	XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonType, xpRadioButton);
	XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonBehavior, xpButtonBehaviorRadioButton);
	XPSetWidgetProperty(fms_z737_fmc_radiobutton, xpProperty_ButtonState, 0);
	fms_z737_fmc_rb_label = XPCreateWidget(x0+75, y1, x0+75+100+10+55, y1-15,
									   1, "Laminar B737-800 with ZiboMod", 0, settings_dialog,
									   xpWidgetClass_Caption);

	// Cancel and Set buttons
	cancel_button = XPCreateWidget(x0+(w/4), y2+30, x0+(w/2)-10, y2+20,
										 1, "Cancel", 0, settings_dialog,
										 xpWidgetClass_Button);
	XPSetWidgetProperty(cancel_button, xpProperty_ButtonType, xpPushButton);

	set_button = XPCreateWidget(x0+(w/2)+10, y2+30, x0+((3*w)/4), y2+20,
								  1, "Set", 0, settings_dialog,
								  xpWidgetClass_Button);
	XPSetWidgetProperty(set_button, xpProperty_ButtonType, xpPushButton);

	XPAddWidgetCallback(settings_dialog, (XPWidgetFunc_t)settingsDialogHandler);

}


void destroySettingsDialog() {

	if (xhsi_settings_dialog_created) {
		XPDestroyWidget(settings_dialog, 1);
		xhsi_settings_dialog_created = 0;
	}

}



// define the handler before it is used
XPWidgetFunc_t remoteDialogHandler(
							   XPWidgetMessage		inMessage,
							   XPWidgetID			inWidget,
							   intptr_t				inParam1,
							   intptr_t				inParam2) {


	if (inMessage == xpMessage_CloseButtonPushed) {
		// close (X) clicked
		closeRemoteDialog();
		return (XPWidgetFunc_t)1;
	}

	if (inMessage == xpMsg_PushButtonPressed) {

		if (inParam1 == (intptr_t)rmt_close_button) {
			// Remote close button pressed
			sendRemoteCommand(1);
			closeRemoteDialog();
			return (XPWidgetFunc_t)1;

		} else if (inParam1 == (intptr_t)rmt_shutdown_button) {
			// Shutdown button pressed
			sendRemoteCommand(2);
			closeRemoteDialog();
			return (XPWidgetFunc_t)1;

		} else if (inParam1 == (intptr_t)rmt_cancel_button) {
			// Cancel button pressed
			closeRemoteDialog();
			return (XPWidgetFunc_t)1;
		}

	}

	return 0;

}

void createRemoteDialog(int x, int y) {

	// h1 : height of first subwindow
	int h1 = 60;
	// h2 : height of second subwindow
	int h2 = 60;
	// x, y : lower left
	int w = 400;
	int h = 150 + h1 + h2;
	// x0, y0 : top left
	int x0 = x;
	int y0 = y + h;
	// x2, y2 : bottom right
	int x2 = x + w;
	int y2 = y;

	// y1 : current line
	int y1;

	// root window
	remote_dialog = XPCreateWidget(x0, y0, x2, y2,
										1,								// visible
										"XHSI Remote Operations",		// window title
										1,								// root
										NULL,							// no container
										xpWidgetClass_MainWindow);
	// Set the close box
	// XPSetWidgetProperty(settings_dialog, xpProperty_MainWindowHasCloseBoxes, 1);

	rmt_tip = XPCreateWidget(x0+80+10, y0-20-2-10, x0+80+210, y0-20-2-15-10,
							  1, REMOTE_TIP_TEXT1, 0, remote_dialog,
							  xpWidgetClass_Caption);

	// remote close subwindow
	y1 = y0-45-20;
	rmt_close_subwindow = XPCreateWidget(x0+20, y1, x2-20, y1-h1,
											   1,							// visible
											   "Remote Close",				// window title
											   0,							// not root
											   remote_dialog,				// in container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(rmt_close_subwindow, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	// Close label
	y1 -= 5;
	rmt_close_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "Close all XHSI Applications", 0, remote_dialog,
							  xpWidgetClass_Caption);
	y1 -= 25;
	// Close button
	rmt_close_button = XPCreateWidget(x0+(w/4), y1, x2-(w/4), y1-10,
										 1, "SEND CLOSE", 0, remote_dialog,
										 xpWidgetClass_Button);
	XPSetWidgetProperty(rmt_close_button, xpProperty_ButtonType, xpPushButton);



	// shutdown subwindow
	y1 = y0-45-20-h1-15;
	rmt_shutdown_subwindow = XPCreateWidget(x0+20, y1, x2-20, y1-h2,
											   1,							// visible
											   "Remote Shutdown",			// window title
											   0,							// not root
											   remote_dialog,				// in container
											   xpWidgetClass_SubWindow);
	XPSetWidgetProperty(rmt_shutdown_subwindow, xpProperty_SubWindowType, xpSubWindowStyle_SubWindow);

	// shutdown label
	y1 -= 5;
	rmt_shutdown_label = XPCreateWidget(x0+50, y1, x2-50, y1-15,
							  1, "Shutdown all computers running XHSI Application", 0, remote_dialog,
							  xpWidgetClass_Caption);

	// shutdown button
	y1 -= 25;
	rmt_shutdown_button = XPCreateWidget(x0+(w/4), y1, x2-(w/4), y1-10,
										 1, "SEND SHUTDOWN", 0, remote_dialog,
										 xpWidgetClass_Button);
	XPSetWidgetProperty(rmt_shutdown_button, xpProperty_ButtonType, xpPushButton);


	// Cancel buttons
	rmt_cancel_button = XPCreateWidget(x0+(w/2)-40, y2+30, x0+(w/2)+40, y2+20,
										 1, "Cancel", 0, remote_dialog,
										 xpWidgetClass_Button);
	XPSetWidgetProperty(rmt_cancel_button, xpProperty_ButtonType, xpPushButton);


	XPAddWidgetCallback(remote_dialog, (XPWidgetFunc_t)remoteDialogHandler);

}

void destroyRemoteDialog() {

	if (xhsi_remote_dialog_created) {
		XPDestroyWidget(remote_dialog, 1);
		xhsi_remote_dialog_created = 0;
	}

}


void menuHandler(void * mRef, void * iRef) {

	intptr_t item_num = (intptr_t) iRef;

	if (item_num == MENU_ITEM_SETTINGS) {
		if (xhsi_settings_dialog_created == 0) {
			createSettingsDialog(300, 200);
			xhsi_settings_dialog_created = 1;
		}
		setWidgetValues();
		if(!XPIsWidgetVisible(settings_dialog)) {
			XPShowWidget(settings_dialog);
		}
		xhsi_send_enabled = 0;
		xhsi_settings_dialog_shows = 1;
	} else if (item_num == MENU_REMOTE) {
		if (xhsi_remote_dialog_created == 0) {
			createRemoteDialog(300, 200);
			xhsi_remote_dialog_created = 1;
		}
		if(!XPIsWidgetVisible(remote_dialog)) {
			XPShowWidget(remote_dialog);
		}
		xhsi_send_enabled = 0;
		xhsi_remote_dialog_shows = 1;
	//	} else if (item_num == MENU_ITEM_RESET) {
	//	    resetSocket();
	}

}


void createUI() {

    XPLMDebugString("XHSI: creating UI\n");

	menu_item = XPLMAppendMenuItem(XPLMFindPluginsMenu(), "XHSI", NULL, 1);
	menu_id = XPLMCreateMenu("XHSI", XPLMFindPluginsMenu(), menu_item, menuHandler, NULL);
	XPLMAppendMenuItem(menu_id, "Settings", (void *) MENU_ITEM_SETTINGS, 1);
	XPLMAppendMenuItem(menu_id, "Remote", (void *) MENU_REMOTE, 1);
//    #if IBM
//    XPLMAppendMenuItem(menu_id, "Reset net", (void *) MENU_ITEM_RESET, 1);
//    #endif

    XPLMDebugString("XHSI: UI created\n");

}


void destroyMenu() {

   	XPLMDestroyMenu(menu_id);

}


void destroyUI(void) {

    destroySettingsDialog();
    destroyRemoteDialog();
    destroyMenu();

}
