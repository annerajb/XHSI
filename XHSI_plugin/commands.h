
// commands that will be used by the writeDataRef
extern XPLMCommandRef nav1_standy_flip;
extern XPLMCommandRef nav2_standy_flip;
extern XPLMCommandRef com1_standy_flip;
extern XPLMCommandRef com2_standy_flip;
extern XPLMCommandRef adf1_standy_flip;
extern XPLMCommandRef adf2_standy_flip;
extern XPLMCommandRef sim_transponder_transponder_ident;

extern XPLMCommandRef sim_autopilot_fdir_servos_toggle;
extern XPLMCommandRef sim_autopilot_autothrottle_toggle;
extern XPLMCommandRef sim_autopilot_level_change;
extern XPLMCommandRef sim_autopilot_heading;
extern XPLMCommandRef sim_autopilot_vertical_speed;
extern XPLMCommandRef sim_autopilot_nav;
extern XPLMCommandRef sim_autopilot_approach;
extern XPLMCommandRef sim_autopilot_glide_slope;
extern XPLMCommandRef sim_autopilot_back_course;
extern XPLMCommandRef sim_autopilot_altitude_hold;

extern XPLMCommandRef sim_lights_nav_lights_toggle;
extern XPLMCommandRef sim_lights_beacon_lights_toggle;
extern XPLMCommandRef sim_lights_taxi_lights_toggle;
extern XPLMCommandRef sim_lights_strobe_lights_toggle;
extern XPLMCommandRef sim_lights_landing_lights_toggle;

extern XPLMCommandRef sim_flight_controls_flaps_down;
extern XPLMCommandRef sim_flight_controls_flaps_up;
extern XPLMCommandRef sim_flight_controls_landing_gear_toggle;
extern XPLMCommandRef sim_flight_controls_speed_brakes_down_one;
extern XPLMCommandRef sim_flight_controls_speed_brakes_up_one;


void registerCommands(void);
void unregisterCommands(void);
