/**
* XPlaneSimDataRepository.java
* 
* Stores and provides access to all simulation data variables. This repository
* is updated by XPlaneDataPacketDecoder. Observers can subscribe to changes
* in this repository. All observers are updated by calling the tick_updates
* method of this repository.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.sourceforge.xhsi.model.xplane;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Observer;
import net.sourceforge.xhsi.model.SimDataRepository;

public class XPlaneSimDataRepository implements SimDataRepository {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    // Aircraft position
    public static final int SIM_FLIGHTMODEL_POSITION_GROUNDSPEED = 0;
    public static final int SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED = 1;
    public static final int SIM_FLIGHTMODEL_POSITION_MAGPSI = 2; // mag heading
    public static final int SIM_FLIGHTMODEL_POSITION_HPATH = 3; // track (true or mag?)
    public static final int SIM_FLIGHTMODEL_POSITION_LATITUDE = 4;
    public static final int SIM_FLIGHTMODEL_POSITION_LONGITUDE = 5;
    public static final int SIM_FLIGHTMODEL_POSITION_PHI = 6;            // roll angle = bank
    public static final int SIM_FLIGHTMODEL_POSITION_R = 7;            // yaw rotation rate in deg/sec
    public static final int SIM_FLIGHTMODEL_POSITION_MAGVAR = 8; // XPSDK says that this dataref should be part of the environment, not the aircraft
    public static final int SIM_FLIGHTMODEL_POSITION_ELEVATION = 9; // meters
    public static final int SIM_FLIGHTMODEL_POSITION_Y_AGL = 10; // meters
    public static final int SIM_FLIGHTMODEL_POSITION_THETA = 11; // pitch (deg)
    public static final int SIM_FLIGHTMODEL_POSITION_VPATH = 12; // fpa
    public static final int SIM_FLIGHTMODEL_POSITION_ALPHA = 13; // aoa
    public static final int SIM_FLIGHTMODEL_POSITION_BETA = 14; // yaw ( = slip or drift ? )
    public static final int SIM_FLIGHTMODEL_FAILURES_ONGROUND_ANY = 15; // It was misplaced and is not really a failure, you can use that to indicate when the wheels are on the ground
    public static final int XHSI_FLIGHTMODEL_POSITION_NEAREST_ARPT = 10016;
    public static final int SIM_FLIGHTMODEL_FORCES_G_LOAD = 17;
    public static final int SIM_FLIGHTMODEL_MISC_TURNRATE_NOROLL = 18;



    // Instruments
//    public static final int SIM_FLIGHTMODEL_POSITION_VH_IND_FPM = 50; // vertical velocity
//    public static final int SIM_FLIGHTMODEL_MISC_H_IND = 51; // indicated barometric altitude
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_PILOT = 52;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_KTS_COPILOT = 53;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_PILOT = 54;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_ALTITUDE_FT_COPILOT = 55;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_PILOT = 56;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_VVI_FPM_COPILOT = 57;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_SIDESLIP_DEGREES = 58;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_PILOT = 59;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_RADIO_ALTIMETER_BUG_FT_COPILOT = 60;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT = 61;
    public static final int SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT = 62;
    public static final int SIM_COCKPIT2_GAUGES_INDICATORS_AIRSPEED_ACCELERATION = 63;
    public static final int XHSI_EFIS_PILOT_DA_BUG = 64;
    public static final int XHSI_EFIS_PILOT_MINS_MODE = 65;
    public static final int XHSI_EFIS_COPILOT_DA_BUG = 66;
    public static final int XHSI_EFIS_COPILOT_MINS_MODE = 67;
    public static final int SIM_GAUGES_FAILURES_PILOT = 68;
    public static final int SIM_GAUGES_FAILURES_COPILOT = 69;

    // Electrical
    public static final int SIM_COCKPIT_ELECTRICAL_AVIONICS_ON = 80;
    public static final int SIM_COCKPIT_ELECTRICAL_BATTERY_ON = 81;
    public static final int SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHTS_ON = 82;
    public static final int APU_N1 = 83;
    public static final int APU_GEN_AMP = 84;
    public static final int APU_STATUS = 85;

    // Radios
    public static final int SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ = 100;
    public static final int SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ = 101;
    public static final int SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ = 102;
    public static final int SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ = 103;
    public static final int SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT = 104;
    public static final int SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT = 105;
    public static final int SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT = 106;
    public static final int SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT = 107;
    public static final int SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M = 108;
    public static final int SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M = 109;
    public static final int SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M = 110; // wtf?
    public static final int SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M = 111; // wtf?
    public static final int SIM_COCKPIT_RADIOS_NAV1_OBS_DEGM = 112; // OBS is set manually
    public static final int SIM_COCKPIT_RADIOS_NAV2_OBS_DEGM = 113;
    public static final int SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM = 114; // Course=OBS for VORs; Course=localizer_frontcourse for LOC, ILS & IGS
    public static final int SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM = 115;
    public static final int SIM_COCKPIT_RADIOS_NAV1_CDI = 116; // CDI is not Course Deviation Indication, but "Receiving an expected glide slope"
    public static final int SIM_COCKPIT_RADIOS_NAV2_CDI = 117;
    public static final int SIM_COCKPIT_RADIOS_NAV1_HDEF_DOT = 118;
    public static final int SIM_COCKPIT_RADIOS_NAV2_HDEF_DOT = 119;
    public static final int SIM_COCKPIT_RADIOS_NAV1_FROMTO = 120;
    public static final int SIM_COCKPIT_RADIOS_NAV2_FROMTO = 121;
    public static final int SIM_COCKPIT_RADIOS_NAV1_VDEF_DOT = 122;
    public static final int SIM_COCKPIT_RADIOS_NAV2_VDEF_DOT = 123;

    public static final int SIM_COCKPIT_RADIOS_GPS_DIR_DEGT = 124;
    public static final int SIM_COCKPIT_RADIOS_GPS_DME_DIST_M = 125;
    public static final int SIM_COCKPIT_RADIOS_GPS_COURSE_DEGTM = 126;
    public static final int SIM_COCKPIT_RADIOS_GPS_HDEF_DOT = 127;
    public static final int SIM_COCKPIT_RADIOS_GPS_FROMTO = 128;
    public static final int SIM_COCKPIT_RADIOS_GPS_VDEF_DOT = 129;

    public static final int SIM_COCKPIT_RADIOS_NAV1_DME_TIME_SECS = 130;
    public static final int SIM_COCKPIT_RADIOS_NAV2_DME_TIME_SECS = 131;
    public static final int SIM_COCKPIT_RADIOS_GPS_DME_TIME_SECS = 132;

    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_OUTER_MARKER_LIT = 133;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_MIDDLE_MARKER_LIT = 134;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_INNER_MARKER_LIT = 135;

    public static final int SIM_COCKPIT_RADIOS_NAV1_STDBY_FREQ_HZ = 136;
    public static final int SIM_COCKPIT_RADIOS_NAV2_STDBY_FREQ_HZ = 137;
    public static final int SIM_COCKPIT_RADIOS_ADF1_STDBY_FREQ_HZ = 138;
    public static final int SIM_COCKPIT_RADIOS_ADF2_STDBY_FREQ_HZ = 139;

    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID = 10140;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID = 10141;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID = 10142;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID = 10143;

    public static final int SIM_COCKPIT_RADIOS_COM1_FREQ_HZ = 144;
    public static final int SIM_COCKPIT_RADIOS_COM1_STDBY_FREQ_HZ = 145;
    public static final int SIM_COCKPIT_RADIOS_COM2_FREQ_HZ = 146;
    public static final int SIM_COCKPIT_RADIOS_COM2_STDBY_FREQ_HZ = 147;
    // for the 8.33KHz spacing, see 194-197 below
    public static final int SIM_COCKPIT_RADIOS_STDBY_FLIP = 148; // to send commands to the plugin

    public static final int SIM_COCKPIT_RADIOS_GPS_HAS_GLIDESLOPE = 149;

    // AP
    public static final int SIM_COCKPIT_AUTOPILOT_AUTOPILOT_STATE = 150;
    public static final int SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY = 151;
    public static final int SIM_COCKPIT_AUTOPILOT_ALTITUDE = 152;
    public static final int SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR = 153;
    public static final int SIM_COCKPIT_AUTOPILOT_HEADING_MAG = 154;
    public static final int SIM_COCKPIT_AUTOPILOT_AIRSPEED = 155;
    public static final int SIM_COCKPIT_AUTOPILOT_AIRSPEED_IS_MACH = 156;
    public static final int SIM_COCKPIT_AUTOPILOT_FD_PITCH = 157;
    public static final int SIM_COCKPIT_AUTOPILOT_FD_ROLL = 158;
    public static final int SIM_COCKPIT_AUTOPILOT_MODE = 159;
    public static final int SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ENABLED = 160;
    public static final int SIM_COCKPIT2_AUTOPILOT_AUTOTHROTTLE_ON = 161;
    public static final int SIM_COCKPIT2_AUTOPILOT_HEADING_STATUS = 162;
    public static final int SIM_COCKPIT2_AUTOPILOT_NAV_STATUS = 163;
    public static final int SIM_COCKPIT2_AUTOPILOT_VVI_STATUS = 164;
    public static final int SIM_COCKPIT2_AUTOPILOT_SPEED_STATUS = 165;
    public static final int SIM_COCKPIT2_AUTOPILOT_ALTITUDE_HOLD_STATUS = 166;
    public static final int SIM_COCKPIT2_AUTOPILOT_GLIDESLOPE_STATUS = 167;
    public static final int SIM_COCKPIT2_AUTOPILOT_VNAV_STATUS = 168;
    public static final int SIM_COCKPIT2_AUTOPILOT_TOGA_STATUS = 169;
    public static final int SIM_COCKPIT2_AUTOPILOT_TOGA_LATERAL_STATUS = 170;
    public static final int SIM_COCKPIT2_AUTOPILOT_ROLL_STATUS = 171;
    public static final int SIM_COCKPIT2_AUTOPILOT_PITCH_STATUS = 172;
    public static final int SIM_COCKPIT2_AUTOPILOT_BACKCOURSE_STATUS = 173;
    public static final int SIM_COCKPIT_AUTOPILOT_KEY_PRESS = 174; // to send commands to the plugin
    public static final int SIM_COCKPIT_AUTOPILOT_HEADING_ROLL_MODE = 175;


    // Transponder
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_MODE = 180; // 0=OFF, 1=STDBY, 2=ON, 3=TEST
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_CODE = 181;
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_ID = 182;


    // RTU
    public static final int XHSI_RTU_CONTACT_ATC = 185; // also works for PilotEdge
    public static final int XHSI_RTU_SELECTED_RADIO = 186; // 0:none, 1:COM1, 2:NAV1, 3:ADF1, 4:XPDR_CODE, 5:XPDR_MODE, 6:ADF2, 7:NAV2, 8:COM2
    public static final int XHSI_RTU_PILOTEDGE_RX = 187; // works only for PilotEdge
    
    
    // Clock mode
    public static final int SIM_COCKPIT2_CLOCK_TIMER_MODE = 190; // 0 = GMT, 1 = Local Time, 2 = Date, 3 = Timer
    public static final int XHSI_CHR_CONTROL = 191; // to send commands to the plugin 1=Start/Stop/Reset, 2=Start/Stop, 3=Reset

    
    // CDU source
    public static final int XHSI_CDU_SOURCE = 192;
    
    
    // COM1 & 2 using 8.33kHz spacing
    public static final int SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_FREQUENCY_HZ_833 = 194;
    public static final int SIM_COCKPIT2_RADIOS_ACTUATORS_COM1_STANDBY_FREQUENCY_HZ_833 = 195;
    public static final int SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_FREQUENCY_HZ_833 = 196;
    public static final int SIM_COCKPIT2_RADIOS_ACTUATORS_COM2_STANDBY_FREQUENCY_HZ_833 = 197;
    
    // GPS next waypoint ID
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_GPS_NAV_ID_0_3 = 10198; // the 4 first characters
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_GPS_NAV_ID_4_7 = 10199; // the next 4 characters
   
    
    // EFIS
    public static final int SIM_COCKPIT_SWITCHES_HSI_SELECTOR = 200; // 0=NAV1, 1=NAV2, 2=GPS
    public static final int SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR = 201; // 0=10NM, 1=20, 2=40, etc...
    public static final int SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR = 202;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR = 203;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WEATHER = 204;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS = 205;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS = 206;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS = 207;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS = 208;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS = 209;
    public static final int SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE = 210; // 0=Centered, 1=Expanded
    public static final int SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE = 211; // 0=APP, 1=VOR, 2=MAP, 3=NAV, 4=PLN
    public static final int XHSI_EFIS_PILOT_STA = 212;
    public static final int XHSI_EFIS_PILOT_DATA = 213;
    public static final int XHSI_EFIS_PILOT_POS = 214;
    public static final int XHSI_EFIS_PILOT_MAP_ZOOMIN = 215;


    // Copilot EFIS
    public static final int XHSI_EFIS_COPILOT_HSI_SOURCE = 250;
    public static final int XHSI_EFIS_COPILOT_MAP_RANGE = 251;
    public static final int XHSI_EFIS_COPILOT_RADIO1 = 252;
    public static final int XHSI_EFIS_COPILOT_RADIO2 = 253;
    public static final int XHSI_EFIS_COPILOT_WXR = 254;
    public static final int XHSI_EFIS_COPILOT_TFC = 255;
    public static final int XHSI_EFIS_COPILOT_ARPT = 256;
    public static final int XHSI_EFIS_COPILOT_WPT = 257;
    public static final int XHSI_EFIS_COPILOT_VOR = 258;
    public static final int XHSI_EFIS_COPILOT_NDB = 259;
    public static final int XHSI_EFIS_COPILOT_MAP_CTR = 260;
    public static final int XHSI_EFIS_COPILOT_MAP_MODE = 261;
    public static final int XHSI_EFIS_COPILOT_STA = 262;
    public static final int XHSI_EFIS_COPILOT_DATA = 263;
    public static final int XHSI_EFIS_COPILOT_POS = 264;
    public static final int XHSI_EFIS_COPILOT_MAP_ZOOMIN = 265;


    // EICAS
    public static final int XHSI_ENGINE_TYPE = 280;
    public static final int XHSI_EICAS_TRQ_SCALE = 281;
    public static final int XHSI_FUEL_UNITS = 282;
    

    // MFD
    public static final int XHSI_MFD_MODE = 290;

    
    // Environment
    public static final int SIM_WEATHER_WIND_SPEED_KT = 300;
    public static final int SIM_WEATHER_WIND_DIRECTION_DEGT = 301;
    public static final int SIM_TIME_ZULU_TIME_SEC = 302;
    public static final int SIM_TIME_LOCAL_TIME_SEC = 303;
    public static final int SIM_WEATHER_TEMPERATURE_AMBIENT_C = 304;
    public static final int SIM_WEATHER_SPEED_SOUND_MS = 305;
    public static final int SIM_WEATHER_TEMPERATURE_SEALEVEL_C = 309;
    public static final int SIM_WEATHER_TEMPERATURE_LE_C = 310;
    // Timers
    public static final int SIM_TIME_TIMER_IS_RUNNING_SEC = 306;
    public static final int SIM_TIME_TIMER_ELAPSED_TIME_SEC = 307;
    public static final int SIM_TIME_TOTAL_FLIGHT_TIME_SEC = 308;


    // Aircraft constants
    public static final int SIM_AIRCRAFT_VIEW_ACF_VSO = 320;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VS = 321;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VFE = 322;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VNO = 323;
    public static final int SIM_AIRCRAFT_VIEW_ACF_VNE = 324;
    public static final int SIM_AIRCRAFT_VIEW_ACF_MMO = 325;
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_VLE = 326;
    public static final int SIM_AIRCRAFT_PARTS_ACF_SBRKEQ = 327;
    public static final int SIM_AIRCRAFT_GEAR_ACF_GEAR_RETRACT = 328;
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_VMCA = 329;
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_VYSE = 330;
    public static final int SIM_AIRCRAFT_VIEW_ACF_TAILNUM_0_3 = 10331; // the 4 first characters of the aircraft registration
    public static final int SIM_AIRCRAFT_VIEW_ACF_TAILNUM_4_7 = 10332; // the next 4 characters of the aircraft registration
    public static final int SIM_AIRCRAFT_CONTROLS_ACL_ELEV_DN = 333;
    public static final int SIM_AIRCRAFT_CONTROLS_ACL_ELEV_UP = 334;
    public static final int SIM_AIRCRAFT_CONTROLS_ACL_AIL_UP = 335;
    public static final int SIM_AIRCRAFT_CONTROLS_ACL_AIL_DN = 336;
    public static final int SIM_AIRCRAFT_CONTROLS_ACL_RUDDER_LR = 337;
    public static final int SIM_AIRCRAFT_ELECTRICAL_NUM_BATTERIES = 338;
    public static final int SIM_AIRCRAFT_ELECTRICAL_NUM_BUSES = 339;
    public static final int SIM_AIRCRAFT_ELECTRICAL_NUM_GENERATORS = 340;
    public static final int SIM_AIRCRAFT_ELECTRICAL_NUM_INVERTERS = 341;
    public static final int SIM_AIRCRAFT_ENGINE_RED_OIL_P = 342;
    public static final int SIM_AIRCRAFT_ENGINE_RED_OIL_T = 343;
    public static final int SIM_AIRCRAFT_ENGINE_RED_EPR = 344;
    
    // Controls & annunciators
    public static final int SIM_COCKPIT2_ANNUNCIATORS_MASTER_CAUTION = 350;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_MASTER_WARNING = 351;
    public static final int SIM_COCKPIT2_CONTROLS_GEAR_HANDLE_DOWN = 352;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_GEAR_UNSAFE = 353;
    public static final int XHSI_AIRCRAFT_GEAR_COUNT = 354; // Calculated from sim/aircraft/parts/acf_gear_type int[10]
    public static final int SIM_COCKPIT2_CONTROLS_PARKING_BRAKE_RATIO = 355;
    public static final int SIM_COCKPIT_LIGHTS = 356; // bit-fields: taxi_light_on/strobe_lights_on/nav_lights_on/landing_lights_on/beacon_lights_on
    public static final int SIM_COCKPIT2_CONTROLS_FLAP_RATIO = 357; // this is supposed to be the handle location
    public static final int SIM_COCKPIT2_CONTROLS_FLAP_HANDLE_DEPLOY_RATIO = 358; // this is supposed to be the indicator
    public static final int SIM_AIRCRAFT_CONTROLS_ACF_FLAP_DETENTS = 359;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_AUTOPILOT_DISCONNECT = 360;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_FUEL_QUANTITY = 361;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_GPWS = 362;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_ICE = 363;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_PITOT_HEAT = 364;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_STALL_WARNING = 365;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_GEAR_WARNING = 366;
    public static final int SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL = 367;
    public static final int SIM_COCKPIT2_CONTROLS_SPEEDBRAKE_RATIO = 368;
    public static final int SIM_FLIGHTMODEL2_CONTROLS_SPEEDBRAKE_RATIO = 369;
    public static final int SIM_FLIGHTMODEL2_GEAR_DEPLOY_RATIO_ = 370; // array of 10 floats
    public static final int XHSI_AIRCRAFT_GEAR_DOOR_DEPLOY_RATIO_ = 380; // array of 10 floats
    public static final int SIM_COCKPIT2_CONTROLS_YOKE_PITCH_RATIO = 390;
    public static final int SIM_COCKPIT2_CONTROLS_YOKE_ROLL_RATIO = 391;
    public static final int SIM_COCKPIT2_CONTROLS_YOKE_HDG_RATIO = 396;
    public static final int SIM_COCKPIT2_CONTROLS_ELEVATOR_TRIM = 392;
    public static final int SIM_COCKPIT2_CONTROLS_AILERON_TRIM = 393;
    public static final int SIM_COCKPIT2_CONTROLS_RUDDER_TRIM = 394;
    public static final int SIM_FLIGHTMODEL_CONTROLS_SLATRAT = 395;
    public static final int SIM_COCKPIT2_CONTROLS_LEFT_BRK_RATIO = 397;
    public static final int SIM_COCKPIT2_CONTROLS_RIGHT_BRK_RATIO = 398;
    public static final int XHSI_AIRCRAFT_GEAR_DOOR_COUNT = 399;
    
    // Fuel, engines, etc...
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_NUM_TANKS = 400;
    public static final int SIM_AIRCRAFT_ENGINE_ACF_NUM_ENGINES = 401;
    public static final int SIM_COCKPIT2_ANNUNCIATORS_REVERSER_DEPLOYED = 402;  // bitfield
    public static final int SIM_COCKPIT2_ANNUNCIATORS_OIL_PRESSURE = 403;  // bitfield
    public static final int SIM_COCKPIT2_ANNUNCIATORS_OIL_TEMPERATURE = 404;  // bitfield
    public static final int SIM_COCKPIT2_ANNUNCIATORS_FUEL_PRESSURE = 405;  // bitfield
    public static final int SIM_FLIGHTMODEL_WEIGHT_M_FUEL_TOTAL = 406;
    public static final int SIM_AIRCRAFT_WEIGHT_ACF_M_FUEL_TOT = 407;
    public static final int SIM_FLIGHTMODEL_WEIGHT_M_TOTAL = 408; // Gross Weight
    public static final int SIM_COCKPIT2_ENGINE_FIRE_EXTINGUISHER = 409; // Bit field
    public static final int SIM_COCKPIT2_FUEL_QUANTITY_ = 410; // 410..418 : array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ = 420; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ = 430; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ = 440; // array
    public static final int SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ = 450; //array
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ = 460; //array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ = 470; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ = 480; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ = 490; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ = 500; // array
    public static final int SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ = 510; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_C_ = 520; // array
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_PSI_ = 530; // array
    public static final int XHSI_FLIGHTMODEL_ENGINE_VIB_ = 540; // array
    public static final int SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1 = 551;
    public static final int SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2 = 552;
    public static final int SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1 = 553;
    public static final int SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2 = 554;
    public static final int SIM_COCKPIT_FUEL_PUMPS = 555;
    public static final int SIM_AIRCRAFT_ENGINE_MAX_EGT = 556;
    // 557 : free
    public static final int XHSI_EICAS_OVERRIDE_TRQ_MAX = 558;
    public static final int SIM_AIRCRAFT_CONTROLS_ACF_TRQ_MAX_ENG = 559;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_TRQ_ = 560;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_ = 570;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_ITT_C_ = 580;
    public static final int SIM_AIRCRAFT_CONTROLS_ACF_RSC_REDLINE_PRP = 589;
    public static final int SIM_COCKPIT2_ENGINE_INDICATORS_PROP_SPEED_RPM_ = 590;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_PROPMODE_ = 600;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_MPR_ = 610;
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EPR_ = 620;
    public static final int SIM_COCKPIT2_ENGINE_ACTUATORS_THROTTLE_RATIO_ = 630;
    public static final int XHSI_FUEL_USED_ = 640;
    
    // Control surface positions : rudder, elevators, ailerons, spoilers
    public static final int SIM_FLIGHTMODEL_CONTROLS_RUDDER = 650;
    public static final int SIM_FLIGHTMODEL_CONTROLS_LEFT_ELEV = 651;
    public static final int SIM_FLIGHTMODEL_CONTROLS_RIGHT_ELEV = 652;
    public static final int SIM_FLIGHTMODEL_CONTROLS_LEFT_AIL = 653;
    public static final int SIM_FLIGHTMODEL_CONTROLS_RIGHT_AIL = 654;
    
    // Cabin Pressure
    public static final int SIM_COCKPIT2_PRESSURIZATION_CABIN_DELTA_P=655;
    public static final int SIM_COCKPIT2_PRESSURIZATION_CABIN_ALT=656;
    public static final int SIM_COCKPIT2_PRESSURIZATION_CABIN_VVI=657;
    
    // UFMC
    public static final int UFMC_STATUS = 700;
    // UFMC V-speeds
    public static final int UFMC_V1 = 701;
    public static final int UFMC_VR = 702;
    public static final int UFMC_V2 = 703;
    public static final int UFMC_VREF = 704;
    public static final int UFMC_VF30 = 705;
    public static final int UFMC_VF40 = 706;
    // UFMC N1
    public static final int UFMC_N1_1 = 707;
    public static final int UFMC_N1_2 = 708;
    public static final int UFMC_N1_3 = 709;
    public static final int UFMC_N1_4 = 710;


    //XFMC
    public static final int XFMC_KEYPATH = 750; // to send commands to the plugin

    
    // X737
    public static final int X737_STATUS = 800;
    // X737 AP
    public static final int X737_AFDS_FD_A = 801;
    public static final int X737_AFDS_FD_B = 802;
    public static final int X737_AFDS_CMD_A = 803;
    public static final int X737_AFDS_CMD_B = 804;
    public static final int X737_PFD_MCPSPD = 805;
    public static final int X737_PFD_FMCSPD = 806;
    public static final int X737_PFD_RETARD = 807;
    public static final int X737_PFD_THRHLD = 808;
    public static final int X737_PFD_LNAVARMED = 809;
    public static final int X737_PFD_VORLOCARMED = 810;
    public static final int X737_PFD_PITCHSPD = 811;
    public static final int X737_PFD_ALTHLD = 812;
    public static final int X737_PFD_VSARMED = 813;
    public static final int X737_PFD_VS = 814;
    public static final int X737_PFD_VNAVALT = 815;
    public static final int X737_PFD_VNAVPATH = 816;
    public static final int X737_PFD_VNAVSPD = 817;
    public static final int X737_PFD_GSARMED = 818;
    public static final int X737_PFD_GS = 819;
    public static final int X737_PFD_FLAREARMED = 820;
    public static final int X737_PFD_FLARE = 821;
    public static final int X737_PFD_TOGA = 822;
    public static final int X737_PFD_LNAV = 823;
    public static final int X737_PFD_HDG = 824;
    public static final int X737_PFD_VORLOC = 825;
    public static final int X737_PFD_N1 = 826;
    public static final int X737_AFDS_A_PITCH = 827;
    public static final int X737_AFDS_B_PITCH = 828;
    public static final int X737_AFDS_A_ROLL = 829;
    public static final int X737_AFDS_B_ROLL = 830;
    public static final int X737_ATHR_ARMED = 831;
// X737 N1
    public static final int X737_N1_PHASE = 832;
    public static final int X737_N1_LIMIT_ENG1 = 833;
    public static final int X737_N1_LIMIT_ENG2 = 834;
    public static final int X737_STBY_PWR = 835;
    public static final int X737_PFD_PWR = 836;

    
    // CL30
    public static final int CL30_STATUS = 850;
    // CL30 REF
    public static final int CL30_V1 = 851;
    public static final int CL30_VR = 852;
    public static final int CL30_V2 = 853;
    public static final int CL30_VT = 854;
    public static final int CL30_VGA = 855;
    public static final int CL30_VREF = 856;
    public static final int CL30_REFSPDS = 857;
    // CL30 ANNUN
    public static final int CL30_MAST_WARN = 858;
    public static final int CL30_MAST_CAUT = 859;
    // CL30 Thrust Mode
    public static final int CL30_CARETS = 860;
    public static final int CL30_TO_N1 = 861;
    public static final int CL30_CLB_N1 = 862;
    
    
    // Settings & Preferences
    // General instrument style
    public static final int XHSI_STYLE = 900;
    // Minimum runway length
    public static final int XHSI_RWY_LENGTH_MIN = 901; // Override with this value if it is > 0
    public static final int XHSI_RWY_UNITS = 902; // 0:Meters 1:Feet
    
    
    // QPAC AirbusFBW
    public static final int QPAC_STATUS=1000;
    // Autopilot  
    public static final int QPAC_AP_FD=1001;
    public static final int QPAC_AP_PHASE=1002;
    public static final int QPAC_PRESEL_CRZ=1003;
    public static final int QPAC_PRESEL_CLB=1004;
    public static final int QPAC_PRESEL_MACH=1005;
    public static final int QPAC_AP_VERTICAL_MODE=1006;
    public static final int QPAC_AP_VERTICAL_ARMED=1007;
    public static final int QPAC_AP_LATERAL_MODE=1008;
    public static final int QPAC_AP_LATERAL_ARMED=1009;
    public static final int QPAC_NPA_VALID=1010;
    public static final int QPAC_NPA_NO_POINTS=1011;
    public static final int QPAC_AP_APPR=1012;
    public static final int QPAC_APPR_TYPE=1013;
    public static final int QPAC_APPR_MDA=1014;
    public static final int QPAC_ALT_IS_CSTR=1015;
    public static final int QPAC_CONSTRAINT_ALT=1016;
    // FCU
    public static final int QPAC_FCU=1017;
    public static final int QPAC_FCU_BARO=1018;
    // Auto-Thrust
    public static final int QPAC_ATHR_MODE=1019;
    public static final int QPAC_ATHR_MODE2=1020;
    public static final int QPAC_ATHR_LIMITED=1021;
    public static final int QPAC_THR_LEVER_MODE=1022;
    public static final int QPAC_FMA_THR_WARNING=1023;
    public static final int QPAC_FLEX_TEMP=1024;
    public static final int QPAC_THR_RATING_TYPE=1059;
    public static final int QPAC_THR_RATING_N1=1060;
    public static final int QPAC_THROTTLE_INPUT=1061;
    // ILS Sig and Deviation Capt. and FO
    public static final int QPAC_ILS_FLAGS=1025;
    public static final int QPAC_LOC_VAL_CAPT=1026;
    public static final int QPAC_GS_VAL_CAPT=1027;
    public static final int QPAC_LOC_VAL_FO=1027;
    public static final int QPAC_GS_VAL_FO=1029;
    public static final int QPAC_ILS_CRS=1030;
    public static final int QPAC_ILS_FREQ=1031;
    public static final int QPAC_ILS_ID=11032;
    public static final int QPAC_ILS_DME=1033;
    
    // FD
    public static final int QPAC_FD1_VER_BAR=1034;
    public static final int QPAC_FD1_HOR_BAR=1035;
    public static final int QPAC_FD1_YAW_BAR=1036;
    public static final int QPAC_FD2_VER_BAR=1037;
    public static final int QPAC_FD2_HOR_BAR=1038;
    public static final int QPAC_FD2_YAW_BAR=1039;
    // V Speeds
    public static final int QPAC_V1_VALUE=1040;
    public static final int QPAC_V1=1041;
    public static final int QPAC_VR=1042;
    public static final int QPAC_VMO=1043;
    public static final int QPAC_VLS=1044;
    public static final int QPAC_VF=1045;
    public static final int QPAC_VS=1046;
    public static final int QPAC_V_GREEN_DOT=1047;
    public static final int QPAC_ALPHA_PROT=1048;
    public static final int QPAC_ALPHA_MAX=1049;
    // EFIS
    public static final int QPAC_EFIS_ND_MODE_CAPT=1050;
    public static final int QPAC_EFIS_ND_RANGE_CAPT=1051;
    public static final int QPAC_EFIS_ND_MODE_FO=1052;
    public static final int QPAC_EFIS_ND_RANGE_FO=1053;  
    public static final int QPAC_EFIS_ND_SYMBOLS=1054;
    // Failures flags
    public static final int QPAC_FAILURES=1055;
    // Brakes
    public static final int QPAC_AUTO_BRAKE_LEVEL=1056;
    // FLAPS and SLATS
    public static final int QPAC_FLAPS_REQ_POS=1057;
    public static final int QPAC_SLATS_REQ_POS=1058;
    // SPOILERS
    public static final int QPAC_SPOILERS_LEFT=1062;
    public static final int QPAC_SPOILERS_RIGHT=1063;
    // ELAC and SEC FCC
    public static final int QPAC_FCC=1064;
    // Rudder limit
    public static final int QPAC_RUDDER_LIMIT=1065;
    // Hydraulic
    public static final int QPAC_HYD_B_PRESS=1066;
    public static final int QPAC_HYD_G_PRESS=1067;
    public static final int QPAC_HYD_Y_PRESS=1068;
    public static final int QPAC_HYD_B_QTY=1069;
    public static final int QPAC_HYD_G_QTY=1070;
    public static final int QPAC_HYD_Y_QTY=1071;
    public static final int QPAC_HYD_PUMPS=1077;
    // Cabin Pressure
    public static final int QPAC_CABIN_DELTA_P=1072;
    public static final int QPAC_CABIN_ALT=1073;
    public static final int QPAC_CABIN_VS=1074;
    public static final int QPAC_AIR_VALVES=1075;
    // ECAM System Display page
    public static final int QPAC_SD_PAGE=1076;
    public static final int QPAC_COND_COCKPIT_TRIM=1078;
    public static final int QPAC_COND_ZONE1_TRIM=1079;
    public static final int QPAC_COND_ZONE2_TRIM=1080;
    // BLEED AIR
    public static final int QPAC_BLEED_VALVES=1081;
    // APU
    public static final int QPAC_APU_EGT=1082;
    // FUEL
    public static final int QPAC_FUEL_PUMPS=1083;
    public static final int QPAC_FUEL_VALVES=1084;
    // ELEC
    public static final int QPAC_ELEC_AC_CROSS=1085;
    public static final int QPAC_ELEC_CX_LEFT=1086;
    public static final int QPAC_ELEC_CX_CENTER=1087;
    public static final int QPAC_ELEC_CX_RIGHT=1088;
    // Key press to commands
    public static final int QPAC_KEY_PRESS=1089;
    // IDs reserved for QPAC up to 1199
    
    // JarDesign Airbus A320neo
    public static final int JAR_A320NEO_STATUS=1200;
    
    // FMS
    public static final int JAR_A320NEO_PRESEL_CRZ=1201;
    public static final int JAR_A320NEO_PRESEL_CLB=1202;
    public static final int JAR_A320NEO_PRESEL_MACH=1203;
    public static final int JAR_A320NEO_TRANS_ALT=1204;
    // Approach
    public static final int JAR_A320NEO_APPR=1205;
    public static final int JAR_A320NEO_APPR_TYPE=1206;
    public static final int JAR_A320NEO_APPR_DH=1207;
    public static final int JAR_A320NEO_APPR_MDA=1208;
    public static final int JAR_A320NEO_FMA_CAT=1209;
    public static final int JAR_A320NEO_FMA_DUAL=1210;
    public static final int JAR_A320NEO_FMA_DH=1211;
    // FCU
    public static final int JAR_A320NEO_FCU=1212;
    public static final int JAR_A320NEO_FCU_BARO=1213;
    // Auto-pilot
    public static final int JAR_A320NEO_AP_PHASE=1214;
    public static final int JAR_A320NEO_AP_VERTICAL_MODE=1215;
    public static final int JAR_A320NEO_AP_VERTICAL_ARMED=1216;
    public static final int JAR_A320NEO_AP_LATERAL_MODE=1217;
    public static final int JAR_A320NEO_AP_LATERAL_ARMED=1218;
    public static final int JAR_A320NEO_AP_COMMON_MODE=1219;
    public static final int JAR_A320NEO_ALT_IS_CSTR=1220;
    public static final int JAR_A320NEO_CONSTRAINT_ALT=1221;
    // Auto-Thrust
    public static final int JAR_A320NEO_ATHR_MODE=1222;
    public static final int JAR_A320NEO_THR_MODE=1223;
    public static final int JAR_A320NEO_ATHR_LIMITED=1224;
    public static final int JAR_A320NEO_THR_LEVER_MODE=1225;
    public static final int JAR_A320NEO_FMA_THR_WARNING=1226;
    public static final int JAR_A320NEO_FLEX_TEMP=1227;
    // ILS (1230 - 1239)
    // V Speeds
    public static final int JAR_A320NEO_V1=1240;
    public static final int JAR_A320NEO_VR=1241;
    public static final int JAR_A320NEO_VMO=1242;
    public static final int JAR_A320NEO_VLS=1243;
    public static final int JAR_A320NEO_VF=1244;
    public static final int JAR_A320NEO_VS=1245;
    public static final int JAR_A320NEO_V_GREEN_DOT=1246;
    public static final int JAR_A320NEO_ALPHA_PROT=1247;
    public static final int JAR_A320NEO_ALPHA_MAX=1248;
    // EFIS (1250-1254)
    public static final int JAR_A320NEO_ND_MODE=1250;
    // FAILURES
    public static final int JAR_A320NEO_FAILURES=1255;
    // BRAKES
    public static final int JAR_A320NEO_AUTO_BRAKE_LEVEL=1256;
    // MCDU Clics
    public static final int JAR_A320NEO_MCDU_CLICK=1257;

    
    // Plugin Version
    public static final int PLUGIN_VERSION_ID = 999;


    // array with sim data for all sim data points defined above
    float[] sim_values_float = new float[1300];
    // int[] sim_values_int = new int[1000];
    String[] sim_values_string = new String[1300];

    long updates = 0;
    ArrayList observers;
    public static boolean replaying = false;
  
//    private static XPlaneSimDataRepository single_instance;
//
//    public static XPlaneSimDataRepository get_instance() {
//        if (XPlaneSimDataRepository.single_instance == null) {
//            XPlaneSimDataRepository.single_instance = new XPlaneSimDataRepository();
//        }
//        return XPlaneSimDataRepository.single_instance;
//    }

    public XPlaneSimDataRepository() {
        observers = new ArrayList();
        for (int i=0; i<1300; i++) {
            sim_values_string[i] = "";
        }
    }

    public boolean is_replaying() {
        return replaying;
    }

    public void add_observer(Observer observer) {
        this.observers.add(observer);
    }

    public void store_sim_float(int id, float value) {
        sim_values_float[id] = value;
    }

//    public void store_sim_int(int id, int value) {
//        sim_values_int[id % 5000] = value;
//    }

    public void store_sim_string(int id, String value) {
        sim_values_string[id % 10000] = value;
    }

    public float get_sim_float(int id) {
        return sim_values_float[id];
    }

//    public int get_sim_int(int id) {
//        return sim_values_int[id % 5000];
//    }

    public String get_sim_string(int id) {
        return sim_values_string[id % 10000];
    }

    public void tick_updates() {
        this.updates += 1;
        for (int i=0; i<this.observers.size(); i++) {
            logger.finest("Updating observer "+i);
            ((Observer) this.observers.get(i)).update();
        }
    }

    public long get_nb_of_updates() {
        return this.updates;
    }

}
