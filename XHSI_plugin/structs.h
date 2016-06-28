
// This is because in X-Plane V9 there are 500 FMS entries.
// This will cause the fms_packet size to be larger than can be sent using sendto().
// So force the old X-Plane V8 limit
#define MAX_FMS_ENTRIES_ALLOWED 50
#define MAX_FMS_ENTRIES_POSSIBLE 500

// number of TCAS entries
#define NUM_TCAS 20


// Packet data structures
struct SimDataPoint {
	int	id;
	float	value;
};

struct SimDataPacket {
	char			packet_id[4];
	int			nb_of_sim_data_points;
	struct SimDataPoint     sim_data_points[250]; // only 180 should be used, but by reserving more, we can avoid some inadvertent buffer overflows
};

struct FmsEntry {
	int     type;
	char	id[8];
	int 	altitude;
	float	lat;
	float	lon;
};

struct FmsDataPacket {
	char				packet_id[4];
	float               ete_for_active;
	float               groundspeed;
	int 				nb_of_entries;
	int 				displayed_entry_index;
	int 				active_entry_index;
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

struct IncomingPacket {
	int                     nb_of_data_points;
	struct  SimDataPoint    data_points[100];
};

