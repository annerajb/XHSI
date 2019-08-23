/*
 * settings.c
 *
 *  Created on:
 *      Author:
 */


#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>

#define XPLM200 1

#include "XPLMProcessing.h"
#include "XPLMDataAccess.h"
#include "XPLMUtilities.h"
#include "XPLMNavigation.h"
#include "XPLMDisplay.h"
#include "XPLMMenus.h"


#include "globals.h"
#include "settings.h"


// config variables
int                 dest_enable[NUM_DEST];
char                dest_ip[NUM_DEST][20];
unsigned short int  dest_port[NUM_DEST];

unsigned short int  recv_port;
unsigned long int   recv_rate;
float               recv_delay;

unsigned long int   expert_settings;

unsigned long int   adc_data_rate;
unsigned long int   avionics_data_rate;
unsigned long int   aux_sys_data_rate;
unsigned long int   engines_data_rate;
unsigned long int   static_data_rate;
unsigned long int   fms_data_rate;
unsigned long int   tcas_data_rate;
unsigned long int   cdu_data_rate;
float               adc_data_delay;
float               avionics_data_delay;
float               aux_sys_data_delay;
float               engines_data_delay;
float               static_data_delay;
float               fms_data_delay;
float               tcas_data_delay;
float               cdu_data_delay;

unsigned long int  fms_source;


// Config settings ---------------------------------------------------------------------------

void defaultSettings() {

	int i;

    XPLMDebugString("XHSI: defining default settings\n");

	for (i=0; i<NUM_DEST; i++) {
		dest_enable[i] = i==0 ? 1 : 0;
		strcpy(dest_ip[i], DEFAULT_DEST_IP);
		dest_port[i] = DEFAULT_DEST_PORT + i;
	}
    
        strcpy(dest_ip[1], DEFAULT_MULTICAST_IP);
        dest_port[1] = DEFAULT_DEST_PORT;
        
        strcpy(dest_ip[2], "192.168.0.1");
        dest_port[2] = DEFAULT_DEST_PORT;
        
        strcpy(dest_ip[3], "192.168.0.1");
        dest_port[3] = DEFAULT_DEST_PORT+1;
        
	recv_port = DEFAULT_RECV_PORT;
	recv_rate = 20;
	recv_delay = 1.0f / (float)recv_rate;

	adc_data_rate = 15;
	adc_data_delay = 1.0f / (float)adc_data_rate;
	avionics_data_delay = adc_data_delay * 1.5f;
	aux_sys_data_delay = adc_data_delay * 2.5f;
	engines_data_delay = adc_data_delay * 2.0f;
	static_data_delay = adc_data_delay * 4.0f;
	fms_data_rate = 2;
	fms_data_delay = 1.0f / (float)fms_data_rate;
	tcas_data_rate = 5;
	tcas_data_delay = 1.0f / (float)tcas_data_rate;
	avionics_data_rate = 10;
	engines_data_rate = 7;
	aux_sys_data_rate = 5;
	static_data_rate = 1;
	cdu_data_rate = 15;
	cdu_data_delay = 1.0f / (float)cdu_data_rate;
	expert_settings = 0;

	fms_source = FMS_SOURCE_LEGACY;

    XPLMDebugString("XHSI: default settings defined\n");

}


void readConfig() {

	FILE	*cfg_file;
	char	cfg_line[120];
	char	param[40];
	char	dest_param[20];
	char	s_value[80];
	int		d_value;
	int i;

    XPLMDebugString("XHSI: reading settings file\n");

	cfg_file = fopen(CFG_FILE, "r");
	if (cfg_file != NULL) {
		XPLMDebugString("XHSI: reading ");
		XPLMDebugString(CFG_FILE);
		XPLMDebugString("\n");
		while (!feof(cfg_file)) {
			fgets(cfg_line, sizeof(cfg_line), cfg_file);
			sscanf(cfg_line, "%s %s", param, s_value);
			sscanf(s_value, "%d", &d_value);

			if (strcmp(param, "recv_port")==0) {
				if (d_value==0) {
					recv_port = DEFAULT_RECV_PORT;
				} else {
					recv_port = d_value;
				}
			}

			if (strcmp(param, "recv_rate")==0) {
				recv_rate = d_value;
				recv_delay = 1.0f / (float)recv_rate;
			}

			if (strcmp(param, "adc_data_rate")==0) {
				adc_data_rate = d_value;
				adc_data_delay = 1.0f / (float)adc_data_rate;
                avionics_data_delay = adc_data_delay * 5.0f;
                engines_data_delay = adc_data_delay * 3.0f;
                aux_sys_data_delay = adc_data_delay * 2.5f;
			}

			if (strcmp(param, "fms_data_rate")==0) {
				fms_data_rate = d_value;
				fms_data_delay = 1.0f / (float)fms_data_rate;
			}

			if (strcmp(param, "tcas_data_rate")==0) {
				tcas_data_rate = d_value;
				tcas_data_delay = 1.0f / (float)tcas_data_rate;
			}

			if (expert_settings && strcmp(param, "avionics_data_rate")==0) {
				avionics_data_rate = d_value;
				avionics_data_delay = 1.0f / (float)avionics_data_rate;
			}

			if (expert_settings && strcmp(param, "aux_sys_data_rate")==0) {
				aux_sys_data_rate = d_value;
				aux_sys_data_delay = 1.0f / (float)aux_sys_data_rate;
			}

			if (expert_settings && strcmp(param, "engine_data_rate")==0) {
				engines_data_rate = d_value;
				engines_data_delay = 1.0f / (float)engines_data_rate;
			}

			if (expert_settings && strcmp(param, "static_data_rate")==0) {
				static_data_rate = d_value;
				static_data_delay = 1.0f / (float)static_data_rate;
			}

			if (expert_settings && strcmp(param, "cdu_data_rate")==0) {
				cdu_data_rate = d_value;
				cdu_data_delay = 1.0f / (float)cdu_data_rate;
			}

			if (strcmp(param, "expert_settings")==0) {
				expert_settings = d_value;
			}

			if (strcmp(param, "fms_source")==0) {
				fms_source = d_value;
			}

			for (i=0; i<NUM_DEST; i++) {

				sprintf(dest_param, "dest_enable[%d]", i);
				if (strcmp(param, dest_param)==0) {
					dest_enable[i] = d_value;
				}

				sprintf(dest_param, "dest_ip[%d]", i);
				if (strcmp(param, dest_param)==0) {
					strcpy(dest_ip[i], s_value);
				}

				sprintf(dest_param, "dest_port[%d]", i);
				if (strcmp(param, dest_param)==0) {
					dest_port[i] = d_value;
				}

			}
		}
		fclose(cfg_file);
	}

    XPLMDebugString("XHSI: settings file read\n");

}


void initSettings() {

    defaultSettings();
    readConfig();

}



void writeSettings() {

	FILE	*cfg_file;
	int		i;

	XPLMDebugString("XHSI: writing ");
	XPLMDebugString(CFG_FILE);
	// for the "\n", see below...

	cfg_file = fopen(CFG_FILE, "w");
	if (cfg_file != NULL) {

		fprintf(cfg_file, "cfg_version %d\n", PLUGIN_VERSION_NUMBER);

		fprintf(cfg_file, "recv_port %d\n", recv_port);
		fprintf(cfg_file, "recv_rate %ld\n", recv_rate);

		fprintf(cfg_file, "adc_data_rate %ld\n", adc_data_rate);
		fprintf(cfg_file, "fms_data_rate %ld\n", fms_data_rate);
		fprintf(cfg_file, "tcas_data_rate %ld\n", tcas_data_rate);
		fprintf(cfg_file, "avionics_data_rate %ld\n", avionics_data_rate);
		fprintf(cfg_file, "engine_data_rate %ld\n", engines_data_rate);
		fprintf(cfg_file, "aux_sys_data_rate %ld\n", aux_sys_data_rate);
		fprintf(cfg_file, "static_data_rate %ld\n", static_data_rate);
		fprintf(cfg_file, "cdu_data_rate %ld\n", cdu_data_rate);
		fprintf(cfg_file, "expert_settings %ld\n", expert_settings);
		fprintf(cfg_file, "fms_source %ld\n", fms_source);

		for (i=0; i<NUM_DEST; i++) {
			fprintf(cfg_file, "dest_enable[%d] %d\n", i, dest_enable[i]);
			fprintf(cfg_file, "dest_ip[%d] %s\n", i, dest_ip[i]);
			fprintf(cfg_file, "dest_port[%d] %d\n", i, dest_port[i]);
		}

		fprintf(cfg_file, "\n");

		fclose(cfg_file);

		XPLMDebugString(" : OK\n");

	} else {
		XPLMDebugString(" : ERROR!\n");
	}

}


