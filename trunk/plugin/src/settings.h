
// settings variables
extern int					dest_enable[];
extern char					dest_ip[][20];
extern unsigned short int	dest_port[];

extern unsigned short int   recv_port;
extern unsigned long int    recv_rate;
extern float                recv_delay;

extern unsigned long int    nav_data_rate;
extern unsigned long int    fms_data_rate;
extern unsigned long int    tcas_data_rate;
extern float                nav_data_delay;
extern float                fms_data_delay;
extern float                tcas_data_delay;

// settings public functions
void	initSettings();
void	writeSettings();
