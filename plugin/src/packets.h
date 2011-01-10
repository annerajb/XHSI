
// packet creation functions
int		createSimPacket(void);
int	 	createFmsPacket(void);
int 	createTcasPacket(void);

// command packet decode function
void decodeCommandPacket(void);

// The data packets
extern struct SimDataPacket     sim_packet;
extern struct FmsDataPacket		fms_packet;
extern struct TcasDataPacket	tcas_packet;
extern struct CommandPacket     efis_packet;
