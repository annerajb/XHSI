/**
 * ui.c
 *
 */


#include <stdio.h>
#include <string.h>
#include <stdlib.h>

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


// Menu items
#define MENU_ITEM_SETTINGS 0
#define MENU_ITEM_RESET 1



// Define local vars
int			xhsi_dialog_shows;
int			xhsi_dialog_created;

XPLMMenuID	menu_id;
int			menu_item;

XPWidgetID	settings_dialog, settings_receiver_subwindow, settings_sender_subwindow, settings_dest_subwindow;
XPWidgetID	dest_ip_textbox[NUM_DEST], dest_port_textbox[NUM_DEST];
XPWidgetID  src_port_textbox, nav_rate_textbox, fms_rate_textbox, tcas_rate_textbox;
XPWidgetID	version_label, receiver_label, sender_label, dest_label, dest_enable_label, dest_ip_label, dest_port_label, dest_default_label;
XPWidgetID  src_port_label, data_rate_label, nav_data_label, fms_data_label, tcas_data_label;
XPWidgetID	src_port_default_label, nav_data_default_label, fms_data_default_label, tcas_data_default_label;
XPWidgetID  dest_enable_checkbox[NUM_DEST];
XPWidgetID  default_local_button, cancel_button, set_button;


void closeDialog() {

	if (xhsi_dialog_shows) {
		XPHideWidget(settings_dialog);
		xhsi_dialog_shows = 0;
		xhsi_send_enabled = 1;
	}

}


void setWidgetValues() {

	int i;
	char val_buf[80];

	// recv_port
	sprintf(val_buf, "%d", recv_port);
	XPSetWidgetDescriptor(src_port_textbox, val_buf);

	// nav_data_rate
	sprintf(val_buf, "%ld", nav_data_rate);
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

}


// define the handler before it is used
int settingsDialogHandler(
							   XPWidgetMessage		inMessage,
							   XPWidgetID			inWidget,
							   long					inParam1,
							   long					inParam2) {

	char buffer[256];
	int i;

	// we are not displaying the close (X) to avoid uncertainty about its logic (is it Cancel or OK?)
	//if (inMessage == xpMessage_CloseButtonPushed) {
	//	// close (X) clicked
	//	close_dialog();
	//	return 1;
	//}

	if (inMessage == xpMsg_PushButtonPressed) {

		if (inParam1 == (long)default_local_button) {
			// Default Local button pressed, set the first destination to local with default port 49020

			XPSetWidgetProperty(dest_enable_checkbox[0], xpProperty_ButtonState, 1);
			XPSetWidgetDescriptor(dest_ip_textbox[0], DEFAULT_DEST_IP);
			sprintf(buffer, "%d", DEFAULT_DEST_PORT);
			XPSetWidgetDescriptor(dest_port_textbox[0], buffer);

			return 1;

		} else if (inParam1 == (long)set_button) {
			// Set button pressed

			for (i=0; i<NUM_DEST; i++) {

				dest_enable[i] = XPGetWidgetProperty(dest_enable_checkbox[i], xpProperty_ButtonState, NULL);

				XPGetWidgetDescriptor(dest_ip_textbox[i], dest_ip[i], 20);

				XPGetWidgetDescriptor(dest_port_textbox[i], buffer, sizeof(buffer));
				dest_port[i] = (unsigned int)strtol(buffer, NULL, 10);

			}

			XPGetWidgetDescriptor(src_port_textbox, buffer, sizeof(buffer));
			recv_port = (unsigned int)strtol(buffer, NULL, 10);

			XPGetWidgetDescriptor(nav_rate_textbox, buffer, sizeof(buffer));
			nav_data_rate = strtol(buffer, NULL, 10);
			nav_data_delay = 1.0f / (float)nav_data_rate;

			XPGetWidgetDescriptor(fms_rate_textbox, buffer, sizeof(buffer));
			fms_data_rate = strtol(buffer, NULL, 10);
			fms_data_delay = 1.0f / (float)fms_data_rate;

			XPGetWidgetDescriptor(tcas_rate_textbox, buffer, sizeof(buffer));
			tcas_data_rate = strtol(buffer, NULL, 10);
			tcas_data_delay = 1.0f / (float)tcas_data_rate;

			// write the new config to file
			writeSettings();

			setAddresses();
			bindSocket();
			closeDialog();
			return 1;

		} else if (inParam1 == (long)cancel_button) {
			// Cancel button pressed
			closeDialog();
			return 1;
		}

	}

	return 0;

}



void createSettingsDialog(int x, int y) {

	// remark: parameter values like x0+75+100+10+50+20+50 might seem childish, but they are easier to maintain

	// x, y : lower left
	int w = 400;
	int h = 460; // was: 385 ; now : 460
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
	int h3 = 140;
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

	version_label = XPCreateWidget(x0+10, y0-20-2, x0+210, y0-20-2-15,
							  1, PLUGIN_VERSION_TEXT, 0, settings_dialog,
							  xpWidgetClass_Caption);

	// receiver subwindow
	y1 = y0-45;
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
	y1 = y0-45-h1-15;
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
	y1 = y0-45-h1-15-h2-15;
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

	default_local_button = XPCreateWidget(x0+75+100+10+50+20, y1, x0+75+100+10+50+20+50, y1-15,
								  1, "Local", 0, settings_dialog,
								  xpWidgetClass_Button);
	XPSetWidgetProperty(default_local_button, xpProperty_ButtonType, xpPushButton);

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

	// Cancel and Set buttons
	cancel_button = XPCreateWidget(x0+(w/4), y2+30, x0+(w/2)-10, y2+20,
										 1, "Cancel", 0, settings_dialog,
										 xpWidgetClass_Button);
	XPSetWidgetProperty(cancel_button, xpProperty_ButtonType, xpPushButton);

	set_button = XPCreateWidget(x0+(w/2)+10, y2+30, x0+((3*w)/4), y2+20,
								  1, "Set", 0, settings_dialog,
								  xpWidgetClass_Button);
	XPSetWidgetProperty(set_button, xpProperty_ButtonType, xpPushButton);

	XPAddWidgetCallback(settings_dialog, settingsDialogHandler);

}


void destroyDialog() {

	if (xhsi_dialog_created) {
		XPDestroyWidget(settings_dialog, 1);
		xhsi_dialog_created = 0;
	}

}


void menuHandler(void * mRef, void * iRef) {

	int item_num = (int) iRef;

	if (item_num == MENU_ITEM_SETTINGS) {
		if (xhsi_dialog_created == 0) {
			createSettingsDialog(300, 300);
			xhsi_dialog_created = 1;
		}
		setWidgetValues();
		if(!XPIsWidgetVisible(settings_dialog)) {
			XPShowWidget(settings_dialog);
		}
		xhsi_send_enabled = 0;
		xhsi_dialog_shows = 1;
//	} else if (item_num == MENU_ITEM_RESET) {
//	    resetSocket();
	}

}


void createUI() {

	menu_item = XPLMAppendMenuItem(XPLMFindPluginsMenu(), "XHSI", NULL, 1);
	menu_id = XPLMCreateMenu("XHSI", XPLMFindPluginsMenu(), menu_item, menuHandler, NULL);
	XPLMAppendMenuItem(menu_id, "Settings", (void *) MENU_ITEM_SETTINGS, 1);
//    #if IBM
//    XPLMAppendMenuItem(menu_id, "Reset net", (void *) MENU_ITEM_RESET, 1);
//    #endif

}


void destroyMenu() {

   	XPLMDestroyMenu(menu_id);

}


void destroyUI(void) {

    destroyDialog();
    destroyMenu();

}
