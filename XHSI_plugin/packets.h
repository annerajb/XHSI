
// packet creation functions
int createADCPacket(void);
int createAvionicsPacket(void);
int createCustomAvionicsPacket(void);
int createEnginesPacket(void);
int createStaticPacket(void);
int createFmsPackets(void);
int createTcasPacket(void);
int createRemoteCommandPacket(int command);

// command packet decode function
void decodeIncomingPacket(void);

// The data packets
extern struct SimDataPacket       sim_packet;
extern struct FmsDataPacket       fms_packet[10];
extern struct TcasDataPacket      tcas_packet;
extern struct IncomingPacket      efis_packet;
extern struct RemoteCommandPacket rcmd_packet;
