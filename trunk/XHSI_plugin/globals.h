
// Global constants ============================

// version info
#define PLUGIN_VERSION_TEXT "XHSI Plugin 2.0 Beta 7 Alpha 14"
#define PLUGIN_VERSION_NUMBER 20007


// max number of IP/UDP destinations
#define NUM_DEST 4


// defaults for local IP address and ports
#define DEFAULT_DEST_IP "127.0.0.1"
#define DEFAULT_DEST_PORT 49020
#define DEFAULT_RECV_PORT 49019


// config file name
#if IBM
#define CFG_FILE "Resources\\plugins\\XHSI_plugin.cfg"
#else
#define CFG_FILE "Resources/plugins/XHSI_plugin.cfg"
#endif
