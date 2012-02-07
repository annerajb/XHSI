
// packet creation functions
int		createADCPacket(void);
int		createAvionicsPacket(void);
int		createEnginesPacket(void);
int		createStaticPacket(void);
int	 	createFmsPackets(void);
int 	createTcasPacket(void);

// command packet decode function
void decodeCommandPacket(void);

// The data packets
extern struct SimDataPacket     sim_packet;
extern struct FmsDataPacket		fms_packet[10];
extern struct TcasDataPacket	tcas_packet;
extern struct CommandPacket     efis_packet;
