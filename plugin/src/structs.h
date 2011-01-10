
// This is because in X-Plane V9 there are 500 FMS entries.
// This will cause the fms_packet size to be larger than can be sent using sendto().
// So force the old X-Plane V8 limit
#define MAX_FMS_ENTRIES_ALLOWED 100
#define MAX_FMS_ENTRIES_POSSIBLE 500

// number of TCAS entries
#define NUM_TCAS 20


// Packet data structures
struct SimDataPoint {
	long	id;
	float	value;
};

struct SimDataPacket {
	char				packet_id[4];
	long				nb_of_sim_data_points;
	struct SimDataPoint sim_data_points[100];
};

struct FmsEntry {
	long    type;
	char	id[8];
	float	altitude;
	float	lat;
	float	lon;
};

struct FmsDataPacket {
	char				packet_id[4];
	float               ete_for_active;
	float               groundspeed;
	long				nb_of_entries;
	long				displayed_entry_index;
	long				active_entry_index;
	struct FmsEntry     entries[MAX_FMS_ENTRIES_ALLOWED];
};

struct TcasDataPoint {
	float		latitude;
	float		longitude;
	float		elevation;
};

struct TcasDataPacket {
	char					packet_id[4];
	int                     mp_total;
	int                     mp_active;
	float                   radar_altitude;
	/*float					reference_altitude;*/
	struct	TcasDataPoint   tcas_entries[NUM_TCAS];
};

struct CommandPacket {
	long				nb_of_command_points;
	struct SimDataPoint command_points[100];
};

