/*
 * settings.h
 *
 *  Created on:
 *      Author:
 */

#ifndef SETTINGS_H_
#define SETTINGS_H_

// settings variables
extern int					dest_enable[];
extern char					dest_ip[][20];
extern unsigned short int	dest_port[];

extern unsigned short int   recv_port;
extern unsigned long int    recv_rate;
extern float                recv_delay;

extern unsigned long int    expert_settings;

extern unsigned long int    adc_data_rate;
extern unsigned long int    avionics_data_rate;
extern unsigned long int    aux_sys_data_rate;
extern unsigned long int    engines_data_rate;
extern unsigned long int    static_data_rate;
extern unsigned long int    fms_data_rate;
extern unsigned long int    tcas_data_rate;
extern unsigned long int    cdu_data_rate;
extern float                adc_data_delay;
extern float                avionics_data_delay;
extern float                aux_sys_data_delay;
extern float                engines_data_delay;
extern float                static_data_delay;
extern float                fms_data_delay;
extern float                tcas_data_delay;
extern float                cdu_data_delay;

extern unsigned long int   fms_source;

#define FMS_SOURCE_AUTO   0
#define FMS_SOURCE_LEGACY 1
#define FMS_SOURCE_UFMC   2
#define FMS_SOURCE_Z737   3



// settings public functions
void	initSettings();
void	writeSettings();


#endif /* UFMC_H_ */
