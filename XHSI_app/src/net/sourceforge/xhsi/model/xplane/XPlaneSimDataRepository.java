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
    public static final int GPU_GEN_AMP = 85;
    public static final int AUX_GEN_STATUS = 86;
    // Battery array [4]
    public static final int SIM_COCKPIT_ELECTRICAL_BATTERY_VOLT_ = 87;
    public static final int SIM_COCKPIT_ELECTRICAL_BATTERY_AMP_ = 91;
    
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
    public static final int DUPLICATE_THETA_FOR_PITCH = 176;
    public static final int DUPLICATE_PHI_FOR_BANK = 177;


    // Transponder
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_MODE = 180; // 0=OFF, 1=STDBY, 2=ON, 3=TEST
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_CODE = 181;
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_ID = 182;

    // FMS [ comes with X-Plane 11.35 ]
    public static final int SIM_FMS_KEY_PRESS = 183;
    public static final int XHSI_FMS_TYPE = 184;  // 0:Legacy XP10  1:XP11 737-800  2 and more: reserved

    // RTU
    public static final int XHSI_RTU_CONTACT_ATC = 185; // also works for PilotEdge
    public static final int XHSI_RTU_SELECTED_RADIO = 186; // 0:none, 1:COM1, 2:NAV1, 3:ADF1, 4:XPDR_CODE, 5:XPDR_MODE, 6:ADF2, 7:NAV2, 8:COM2
    public static final int XHSI_RTU_PILOTEDGE_RX = 187; // works only for PilotEdge
    
    
    // Clock mode
    public static final int SIM_COCKPIT2_CLOCK_TIMER_MODE = 190; // 0 = GMT, 1 = Local Time, 2 = Date, 3 = Timer
    public static final int XHSI_CHR_CONTROL = 191; // to send commands to the plugin 1=Start/Stop/Reset, 2=Start/Stop, 3=Reset

    
    // CDU source
    public static final int XHSI_CDU_SOURCE = 192;
    public static final int XHSI_CDU_SIDE = 193;
    
    
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
    public static final int XHSI_EFIS_PILOT_ELAPSED_TIME_SEC = 216;
    public static final int XHSI_EFIS_PILOT_TERRAIN = 217;
    public static final int XHSI_EFIS_PILOT_VP = 218;
    public static final int XHSI_EFIS_PILOT_WXR_TILT = 219;
    public static final int XHSI_EFIS_PILOT_WXR_GAIN = 220;
    public static final int XHSI_EFIS_PILOT_WXR_MODE = 221;
    public static final int XHSI_EFIS_PILOT_WXR_OPT = 222;
    public static final int XHSI_EFIS_PILOT_ILS = 223;
    public static final int XHSI_EFIS_PILOT_TRK_FPA = 224;
    public static final int XHSI_EFIS_PILOT_METRIC_ALT = 225;
    
    
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
    public static final int XHSI_EFIS_COPILOT_ELAPSED_TIME_SEC = 266;
    public static final int XHSI_EFIS_COPILOT_TERRAIN = 267;
    public static final int XHSI_EFIS_COPILOT_VP = 268;
    public static final int XHSI_EFIS_COPILOT_WXR_TILT = 269;
    public static final int XHSI_EFIS_COPILOT_WXR_GAIN = 270;
    public static final int XHSI_EFIS_COPILOT_WXR_MODE = 271;
    public static final int XHSI_EFIS_COPILOT_WXR_OPT = 272;
    public static final int XHSI_EFIS_COPILOT_ILS = 273;
    public static final int XHSI_EFIS_COPILOT_TRK_FPA = 274;
    public static final int XHSI_EFIS_COPILOT_METRIC_ALT = 275;
    
    // EICAS
    public static final int XHSI_ENGINE_TYPE = 280;
    public static final int XHSI_EICAS_TRQ_SCALE = 281;
    public static final int XHSI_FUEL_UNITS = 282;
    public static final int XHSI_TEMP_UNITS = 283;
    public static final int XHSI_FWC_PHASE = 284;
    
    // MFD
    public static final int XHSI_MFD_MODE = 290;
    public static final int XHSI_CREW_OXY_PSI = 291;

    // EGPWS
    public static final int XHSI_EPGWS_MODES = 295;
    
    // Environment
    public static final int SIM_WEATHER_WIND_SPEED_KT = 300;
    public static final int SIM_WEATHER_WIND_DIRECTION_DEGT = 301;
    public static final int SIM_TIME_ZULU_TIME_SEC = 302;
    public static final int SIM_TIME_LOCAL_TIME_SEC = 303;
    public static final int SIM_WEATHER_TEMPERATURE_AMBIENT_C = 304;
    public static final int SIM_WEATHER_SPEED_SOUND_MS = 305;
    public static final int SIM_WEATHER_TEMPERATURE_SEALEVEL_C = 309;
    public static final int SIM_WEATHER_TEMPERATURE_LE_C = 310;
    
    // Clock and Timers
    public static final int SIM_TIME_TIMER_IS_RUNNING_SEC = 306;
    public static final int SIM_TIME_TIMER_ELAPSED_TIME_SEC = 307;
    public static final int SIM_TIME_TOTAL_FLIGHT_TIME_SEC = 308;
    public static final int SIM_TIME_SHOW_DATE = 311;
    public static final int SIM_TIME_DATE = 312;
    public static final int XHSI_TIME_UTC_SOURCE = 313;
    public static final int XHSI_TIME_ET_RUNNING = 314;

     // Ambient Light
    public static final int SIM_GRAPHICS_MISC_COCKPIT_LIGHT_LEVEL_RGB = 315;
    public static final int SIM_GRAPHICS_MISC_OUTSIDE_LIGHT_LEVEL_RGB = 316;
    
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
    public static final int XHSI_AIRCRAFT_NOSE_TIRE_REF_PRESSURE = 345;
    public static final int XHSI_AIRCRAFT_MAIN_TIRE_REF_PRESSURE = 346;
    
    // Controls & annunciators
    public static final int SIM_COCKPIT2_ANNUNCIATORS_MASTER_ACCEPT = 349;
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
    public static final int SIM_COCKPIT2_ENGINE_FADEC = 419; // bit field
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_N1_ = 420; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_ = 430; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_EGT_C_ = 440; // array[8]
    public static final int SIM_FLIGHTMODEL2_ENGINES_THRUST_REVERSER_DEPLOY_RATIO_ = 450; //array[8]
    public static final int SIM_AIRCRAFT_OVERFLOW_ACF_TANK_RATIO_ = 460; //array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_N2_ = 470; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_FF_ = 480; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_ = 490; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_ = 500; // array[8]
    public static final int SIM_COCKPIT2_ENGINE_INDICATORS_OIL_QUANTITY_RATIO_ = 510; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_TEMP_C_ = 520; // array[8]
    public static final int SIM_FLIGHTMODEL_ENGINE_ENGN_OIL_PRESS_PSI_ = 530; // array[8]
    public static final int XHSI_FLIGHTMODEL_ENGINE_VIB_ = 540; // array[8]
    public static final int XHSI_FLIGHTMODEL_ENGINE_VIB_N2_ = 680; // array[8]
    public static final int SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO1 = 551;
    public static final int SIM_OPERATION_FAILURES_HYDRAULIC_PRESSURE_RATIO2 = 552;
    public static final int SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_1 = 553;
    public static final int SIM_COCKPIT2_HYDRAULICS_INDICATORS_HYDRAULIC_FLUID_RATIO_2 = 554;
    public static final int SIM_COCKPIT_FUEL_PUMPS = 555;
    public static final int SIM_AIRCRAFT_ENGINE_MAX_EGT = 556;
    public static final int SIM_COCKPIT_ENGINE_IGN_KEY = 557;
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
    public static final int SIM_COCKPIT2_PRESSURIZATION_CABIN_DELTA_P = 655;
    public static final int SIM_COCKPIT2_PRESSURIZATION_CABIN_ALT = 656;
    public static final int SIM_COCKPIT2_PRESSURIZATION_CABIN_VVI = 657;
    
    // Brakes, wheels, steering
    public static final int WHEEL_STATUS = 658;
    public static final int TIRE_STATUS = 659;
    
    // Generators and elec buses
    public static final int SIM_COCKPIT_ELECTRICAL_GENERATOR_AMPS_ = 660;
    public static final int SIM_COCKPIT_ELECTRICAL_GENERATOR_STATUS = 668;
    public static final int SIM_COCKPIT_ELECTRICAL_INV_BUS_STATUS = 669;
    public static final int SIM_COCKPIT_ELECTRICAL_BUS_LOAD_ = 670; // Array [6]
    // 680 to 688 used by VIB_N2_
    
    // Cabin Pressure actuators
    public static final int SIM_COCKPIT2_PRESSURIZATION_ACT_ALT = 690;
    public static final int SIM_COCKPIT2_PRESSURIZATION_ACT_VVI = 691;
    public static final int SIM_COCKPIT2_PRESSURIZATION_MODES = 692;
    public static final int SIM_COCKPIT2_PRESSURIZATION_MAX_ALT = 693;

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
    public static final int UFMC_KEY_PRESS = 711;


    // XFMC
    public static final int XFMC_KEYPATH = 750; // to send commands to the plugin

    // X-RAAS Runway Awareness and Advisory System
    public static final int X_RAAS_ND_ALERT = 760;
    public static final int X_RAAS_RWY_LEN_AVAIL = 761;
    
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
    public static final int X737_LVLCHANGE = 837;
    // EFIS
    public static final int X737_EFIS0_ND_RANGE_ENUM = 838;
    public static final int X737_EFIS0_FPV = 839;
    public static final int X737_EFIS0_MTR = 840;
    public static final int X737_EFIS0_TFC = 841;
    public static final int X737_EFIS0_CTR = 842;
    public static final int X737_EFIS0_WXR = 843;
    public static final int X737_EFIS0_STA = 844;
    public static final int X737_EFIS0_WPT = 845;
    public static final int X737_EFIS0_ARPT = 846;
    public static final int X737_EFIS0_DATA = 847;
    public static final int X737_EFIS0_POS = 848;
    public static final int X737_EFIS0_TERR = 849;
    public static final int X737_EFIS0_DH_SRC = 850;
    public static final int X737_EFIS0_DH_VAL = 851;
    public static final int X737_EFIS1_ND_RANGE_ENUM = 852;
    public static final int X737_EFIS1_FPV = 853;
    public static final int X737_EFIS1_MTR = 854;
    public static final int X737_EFIS1_TFC = 855;
    public static final int X737_EFIS1_CTR = 856;
    public static final int X737_EFIS1_WXR = 857;
    public static final int X737_EFIS1_STA = 858;
    public static final int X737_EFIS1_WPT = 859;
    public static final int X737_EFIS1_ARPT = 860;
    public static final int X737_EFIS1_DATA = 861;
    public static final int X737_EFIS1_POS = 862;
    public static final int X737_EFIS1_TERR = 863;
    public static final int X737_EFIS1_DH_SRC = 864;
    public static final int X737_EFIS1_DH_VAL = 865;

    
    // CL30
    public static final int CL30_STATUS = 880;
    // CL30 REF
    public static final int CL30_V1 = 881;
    public static final int CL30_VR = 882;
    public static final int CL30_V2 = 883;
    public static final int CL30_VT = 884;
    public static final int CL30_VGA = 885;
    public static final int CL30_VREF = 886;
    public static final int CL30_REFSPDS = 887;
    // CL30 ANNUN
    public static final int CL30_MAST_WARN = 888;
    public static final int CL30_MAST_CAUT = 889;
    // CL30 Thrust Mode
    public static final int CL30_CARETS = 890;
    public static final int CL30_TO_N1 = 891;
    public static final int CL30_CLB_N1 = 892;
    
    
    // Settings & Preferences
    // General instrument style
    public static final int XHSI_STYLE = 900;
    // Minimum runway length
    public static final int XHSI_RWY_LENGTH_MIN = 901; // Override with this value if it is > 0
    public static final int XHSI_RWY_UNITS = 902; // 0:Meters 1:Feet
    
    // Display Unit Brightness
    // Panel & Instruments

    public static final int SIM_COCKPIT_ELECTRICAL_INSTRUMENT_BRIGHTNESS = 910;
    public static final int SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHT = 911;
    public static final int SIM_COCKPIT_ELECTRICAL_HUD_BRIGHTNESS = 912;
    public static final int XHSI_DU_BRIGHT_PFD_CPT = 913;
    public static final int XHSI_DU_BRIGHT_ND_CPT = 914;
    public static final int XHSI_DU_BRIGHT_PFD_FO = 915;
    public static final int XHSI_DU_BRIGHT_ND_FO = 916;
    public static final int XHSI_DU_BRIGHT_EICAS = 917;
    public static final int XHSI_DU_BRIGHT_MFD = 918;
    public static final int XHSI_DU_BRIGHT_CLOCK = 919;
    public static final int XHSI_DU_BRIGHT_CDU_CPT = 920;
    public static final int XHSI_DU_BRIGHT_CDU_FO = 921;
    public static final int XHSI_DU_BRIGHT_CDU_OBS = 922;
    public static final int XHSI_DU_BRIGHT_FCU = 923;
    public static final int XHSI_DU_BRIGHT_OHP = 924;
    public static final int XHSI_DU_BRIGHT_PEDESTAL = 925;
    public static final int XHSI_DU_BRIGHT_DOME = 926;
    public static final int XHSI_DU_BRIGHT_TABLET = 927;
    // Reserved to Lights and Brightness 910 - 929
    
    // Side stick priority - xjoymap dual commands
    public static final int XJOYMAP_STICK_PRIORITY = 940; // mask 0x03 0=dual, 1=Capt, 2=F/O ; mask 0x40 : dual_input boolean
    
    // ATA 21 AIR CONDITIONING AND PRESSURIZATION
    // Trims
    public static final int XHSI_COND_COCKPIT_TRIM = 950;
    public static final int XHSI_COND_ZONE1_TRIM = 951;
    public static final int XHSI_COND_ZONE2_TRIM = 952;
    public static final int XHSI_COND_ZONE3_TRIM = 953;
    public static final int XHSI_COND_ZONE4_TRIM = 954;
    public static final int XHSI_COND_ZONE5_TRIM = 955;
    public static final int XHSI_COND_ZONE6_TRIM = 956;
    public static final int XHSI_COND_CARGO_FWD_TRIM = 957;
    public static final int XHSI_COND_CARGO_AFT_TRIM = 958;
    // Cabin temperatures
    public static final int XHSI_COND_COCKPIT_TEMP = 960;
    public static final int XHSI_COND_FWD_CABIN_TEMP = 961;
    public static final int XHSI_COND_MID_CABIN_TEMP = 962;
    public static final int XHSI_COND_AFT_CABIN_TEMP = 963;
    public static final int XHSI_COND_FWD_CARGO_TEMP = 964;
    public static final int XHSI_COND_AFT_CARGO_TEMP = 965;
    // Inlet air duct temperatures
    public static final int XHSI_COND_INLET_COCKPIT_TEMP = 970;
    public static final int XHSI_COND_INLET_FWD1_CABIN_TEMP = 971;
    public static final int XHSI_COND_INLET_FWD2_CABIN_TEMP = 972;
    public static final int XHSI_COND_INLET_MID1_CABIN_TEMP = 973;
    public static final int XHSI_COND_INLET_MID2_CABIN_TEMP = 974;
    public static final int XHSI_COND_INLET_AFT1_CABIN_TEMP = 975;
    public static final int XHSI_COND_INLET_AFT2_CABIN_TEMP = 976;
    public static final int XHSI_COND_INLET_CARGO_FWD_TEMP = 977;
    public static final int XHSI_COND_INLET_CARGO_AFT_TEMP = 978;

    // Hot air valves - bit arrays (2 bits per valve)
    // LSB order : Hot Air 1, Hot Air 2, Cargo 1, Cargo 2
    public static final int XHSI_COND_HOT_AIR_VALVES = 980;
    // Air conditioning packs
    // bit array (2 bits per valve) : LSB order : PACK 1, PACK 2, RAM AIR, CAB FAN1, CAB FAN2
    public static final int XHSI_COND_AIR_VALVES = 981;
    public static final int XHSI_COND_PACK1_FLOW = 982;
    public static final int XHSI_COND_PACK2_FLOW = 983;
    public static final int XHSI_COND_PACK1_COMP_OUTLET_TEMP = 984;
    public static final int XHSI_COND_PACK2_COMP_OUTLET_TEMP = 985;
    public static final int XHSI_COND_PACK1_BYPASS_RATIO = 986;
    public static final int XHSI_COND_PACK2_BYPASS_RATIO = 987;
    public static final int XHSI_COND_PACK1_TEMP = 988;
    public static final int XHSI_COND_PACK2_TEMP = 989;
    
    // QPAC AirbusFBW
    public static final int QPAC_STATUS = 1000;
    // Autopilot  
    public static final int QPAC_AP_FD = 1001;
    public static final int QPAC_AP_PHASE = 1002;
    public static final int QPAC_PRESEL_CRZ = 1003;
    public static final int QPAC_PRESEL_CLB = 1004;
    public static final int QPAC_PRESEL_MACH = 1005;
    public static final int QPAC_AP_VERTICAL_MODE = 1006;
    public static final int QPAC_AP_VERTICAL_ARMED = 1007;
    public static final int QPAC_AP_LATERAL_MODE = 1008;
    public static final int QPAC_AP_LATERAL_ARMED = 1009;
    public static final int QPAC_NPA_VALID = 1010;
    public static final int QPAC_NPA_NO_POINTS = 1011;
    public static final int QPAC_AP_APPR = 1012;
    public static final int QPAC_APPR_TYPE = 1013;
    public static final int QPAC_APPR_MDA = 1014;
    public static final int QPAC_ALT_IS_CSTR = 1015;
    public static final int QPAC_CONSTRAINT_ALT = 1016;
    // FCU
    public static final int QPAC_FCU = 1017;
    public static final int QPAC_FCU_BARO = 1018;
    // Auto-Thrust
    public static final int QPAC_ATHR_MODE = 1019;
    public static final int QPAC_ATHR_MODE2 = 1020;
    public static final int QPAC_ATHR_LIMITED = 1021;
    public static final int QPAC_THR_LEVER_MODE = 1022;
    public static final int QPAC_FMA_THR_WARNING = 1023;
    public static final int QPAC_FLEX_TEMP = 1024;
    public static final int QPAC_THR_RATING_TYPE = 1025;
    public static final int QPAC_THR_RATING_N1 = 1026;
    public static final int QPAC_THR_RATING_EPR = 1121;
    public static final int QPAC_THROTTLE_INPUT = 1027;
    // ILS Sig and Deviation Capt. and FO
    public static final int QPAC_ILS_FLAGS = 1028;
    public static final int QPAC_LOC_VAL_CAPT = 1029;
    public static final int QPAC_GS_VAL_CAPT = 1030;
    public static final int QPAC_LOC_VAL_FO = 1031;
    public static final int QPAC_GS_VAL_FO = 1032;
    public static final int QPAC_ILS_CRS = 1033;
    public static final int QPAC_ILS_FREQ = 1034;
    public static final int QPAC_ILS_ID = 11035;
    public static final int QPAC_ILS_DME = 1036;    
    // FD
    public static final int QPAC_FD1_VER_BAR = 1037;
    public static final int QPAC_FD1_HOR_BAR = 1038;
    public static final int QPAC_FD1_YAW_BAR = 1039;
    public static final int QPAC_FD2_VER_BAR = 1040;
    public static final int QPAC_FD2_HOR_BAR = 1041;
    public static final int QPAC_FD2_YAW_BAR = 1042;
    // V Speeds
    public static final int QPAC_V1_VALUE = 1043;
    public static final int QPAC_V1 = 1044;
    public static final int QPAC_VR = 1045;
    public static final int QPAC_VMO = 1046;
    public static final int QPAC_VLS = 1047;
    public static final int QPAC_VF = 1048;
    public static final int QPAC_VS = 1049;
    public static final int QPAC_V_GREEN_DOT = 1050;
    public static final int QPAC_ALPHA_PROT = 1051;
    public static final int QPAC_ALPHA_MAX = 1052;
    public static final int QPAC_VFE_NEXT = 1106;
    // EFIS
    public static final int QPAC_EFIS_ND_MODE_CAPT = 1053;
    public static final int QPAC_EFIS_ND_RANGE_CAPT = 1054;
    public static final int QPAC_EFIS_ND_MODE_FO = 1055;
    public static final int QPAC_EFIS_ND_RANGE_FO = 1056;  
    public static final int QPAC_EFIS_ND_SYMBOLS = 1057;
    // Failures flags
    public static final int QPAC_FAILURES = 1060;
    // Brakes & Tyres
    public static final int QPAC_AUTO_BRAKE_LEVEL = 1061;
    public static final int QPAC_BRAKE_ACCU_PSI = 1106;
    public static final int QPAC_BRAKE_LEFT_PSI = 1107;
    public static final int QPAC_BRAKE_RIGHT_PSI = 1108;
    public static final int QPAC_BRAKE_TEMP_ = 1110; // Array[5]
    public static final int QPAC_TYRE_PSI_ = 1115; // Array[5]
    public static final int QPAC_BRAKE_STATUS = 1120;
    // FLAPS and SLATS
    public static final int QPAC_FLAPS_REQ_POS = 1062;
    public static final int QPAC_SLATS_REQ_POS = 1063;
    // SPOILERS
    public static final int QPAC_SPOILERS_LEFT = 1064;
    public static final int QPAC_SPOILERS_RIGHT = 1065;
    // ELAC and SEC FCC
    public static final int QPAC_FCC = 1066;
    // Rudder limit
    public static final int QPAC_RUDDER_LIMIT = 1067;
    // Hydraulic
    public static final int QPAC_HYD_B_PRESS = 1068;
    public static final int QPAC_HYD_G_PRESS = 1069;
    public static final int QPAC_HYD_Y_PRESS = 1070;
    public static final int QPAC_HYD_B_QTY = 1071;
    public static final int QPAC_HYD_G_QTY = 1072;
    public static final int QPAC_HYD_Y_QTY = 1073;
    public static final int QPAC_HYD_PUMPS = 1074;
    // Cabin Pressure
    public static final int QPAC_CABIN_DELTA_P = 1075;
    public static final int QPAC_CABIN_ALT = 1076;
    public static final int QPAC_CABIN_VS = 1077;
    public static final int QPAC_AIR_VALVES = 1078;
    public static final int QPAC_OUTFLOW_VALVE = 1079;
    // ECAM System Display page
    public static final int QPAC_SD_PAGE = 1080;
    // BLEED AIR
    public static final int QPAC_BLEED_VALVES = 1081;
    public static final int QPAC_BLEED_LEFT_PRESS = 1082;
    public static final int QPAC_BLEED_RIGHT_PRESS = 1083;
    public static final int QPAC_BLEED_LEFT_PRESS_TEMP = 1084;
    public static final int QPAC_BLEED_RIGHT_PRESS_TEMP = 1085;
    // Anti-Ice
    public static final int QPAC_ANTI_ICE_STATUS = 1086;
    // APU
    public static final int QPAC_APU_EGT = 1087;
    // FUEL
    public static final int QPAC_FUEL_PUMPS = 1088;
    public static final int QPAC_FUEL_VALVES = 1089;
    // ELEC
    public static final int QPAC_ELEC_AC_CROSS = 1090;
    public static final int QPAC_ELEC_CX_LEFT = 1091;
    public static final int QPAC_ELEC_CX_CENTER = 1092;
    public static final int QPAC_ELEC_CX_RIGHT = 1093;
    public static final int QPAC_ELEC_BUTTONS = 1094;
    // Key press to commands
    public static final int QPAC_KEY_PRESS = 1095;
    // Triple pressure indicator
    public static final int QPAC_TPI_LEFT = 1096;
    public static final int QPAC_TPI_RIGHT = 1097;
    public static final int QPAC_TPI_ACCU = 1098;
    // Nacelle temperature [array 4]
    public static final int QPAC_NACELLE_TEMP_ = 1100; // Array [4]
    // Doors
    public static final int QPAC_DOOR_STATUS = 1104;
    // Engines Ignition
    public static final int QPAC_ENG_IGNITION = 1105;
    // IDs reserved for QPAC up to 1199
    
    // JarDesign Airbus A320neo [1200-1399]
    public static final int JAR_A320NEO_STATUS = 1200;
    
    // FMS
    public static final int JAR_A320NEO_PRESEL_CRZ = 1201;
    public static final int JAR_A320NEO_PRESEL_CLB = 1202;
    public static final int JAR_A320NEO_PRESEL_MACH = 1203;
    public static final int JAR_A320NEO_TRANS_ALT = 1204;
    // Approach
    public static final int JAR_A320NEO_APPR = 1205;
    public static final int JAR_A320NEO_APPR_TYPE = 1206;
    public static final int JAR_A320NEO_APPR_DH = 1207;
    public static final int JAR_A320NEO_APPR_MDA = 1208;
    public static final int JAR_A320NEO_FMA_CAT = 1209;
    public static final int JAR_A320NEO_FMA_DUAL = 1210;
    public static final int JAR_A320NEO_FMA_DH = 1211;
    // FCU
    public static final int JAR_A320NEO_FCU = 1212;
    public static final int JAR_A320NEO_FCU_BARO = 1213;
    // Auto-pilot
    public static final int JAR_A320NEO_AP_PHASE = 1214;
    public static final int JAR_A320NEO_AP_VERTICAL_MODE = 1215;
    public static final int JAR_A320NEO_AP_VERTICAL_ARMED = 1216;
    public static final int JAR_A320NEO_AP_LATERAL_MODE = 1217;
    public static final int JAR_A320NEO_AP_LATERAL_ARMED = 1218;
    public static final int JAR_A320NEO_AP_COMMON_MODE = 1219;
    public static final int JAR_A320NEO_ALT_IS_CSTR = 1220;
    public static final int JAR_A320NEO_CONSTRAINT_ALT = 1221;
    // Auto-Thrust
    public static final int JAR_A320NEO_ATHR_MODE = 1222;
    public static final int JAR_A320NEO_THR_MODE = 1223;
    public static final int JAR_A320NEO_ATHR_LIMITED = 1224;
    public static final int JAR_A320NEO_THR_LEVER_MODE = 1225;
    public static final int JAR_A320NEO_FMA_THR_WARNING = 1226;
    public static final int JAR_A320NEO_FLEX_TEMP = 1227;
    // ILS (1230 - 1239)
    // V Speeds
    public static final int JAR_A320NEO_V1 = 1240;
    public static final int JAR_A320NEO_VR = 1241;
    public static final int JAR_A320NEO_VMO = 1242;
    public static final int JAR_A320NEO_VLS = 1243;
    public static final int JAR_A320NEO_VF = 1244;
    public static final int JAR_A320NEO_VS = 1245;
    public static final int JAR_A320NEO_V_GREEN_DOT = 1246;
    public static final int JAR_A320NEO_ALPHA_PROT = 1247;
    public static final int JAR_A320NEO_ALPHA_MAX = 1248;
    // EFIS (1250-1254)
    public static final int JAR_A320NEO_ND_MODE = 1250;
    // FAILURES
    public static final int JAR_A320NEO_FAILURES = 1255;
    // BRAKES
    public static final int JAR_A320NEO_AUTO_BRAKE_LEVEL = 1256;
    public static final int JAR_A320NEO_BRAKE_ACCU_PSI = 1257;
    public static final int JAR_A320NEO_BRAKE_LEFT_PSI = 1258;
    public static final int JAR_A320NEO_BRAKE_RIGHT_PSI = 1259;
    public static final int JAR_A320NEO_BRAKE_TEMP_ = 1260; // Array [4]
    public static final int JAR_A320NEO_BRAKE_STATUS = 1265;
    // MCDU Clics
    public static final int JAR_A320NEO_MCDU_CLICK = 1270;
    // Hydraulic
    public static final int JAR_A320NEO_HYD_B_PRESS = 1271;
    public static final int JAR_A320NEO_HYD_G_PRESS = 1272;
    public static final int JAR_A320NEO_HYD_Y_PRESS = 1273;
    public static final int JAR_A320NEO_HYD_B_QTY = 1274;
    public static final int JAR_A320NEO_HYD_G_QTY = 1275;
    public static final int JAR_A320NEO_HYD_Y_QTY = 1276;
    public static final int JAR_A320NEO_HYD_PUMPS = 1277;
    public static final int JAR_A320NEO_HYD_PTU = 1278;
    // Doors
    public static final int JAR_A320NEO_DOOR_STATUS = 1279;
    // BLEED AIR
    public static final int JAR_A320NEO_BLEED_VALVES = 1280;
    public static final int JAR_A320NEO_BLEED_LEFT_PRESS = 1281;
    public static final int JAR_A320NEO_BLEED_RIGHT_PRESS = 1282;
    public static final int JAR_A320NEO_BLEED_LEFT_TEMP = 1287;
    public static final int JAR_A320NEO_BLEED_RIGHT_TEMP = 1288;
    // Anti-Ice
    public static final int JAR_A320NEO_ANTI_ICE_STATUS = 1289;
    // FUEL
    public static final int JAR_A320NEO_FUEL_PUMPS = 1290;
    public static final int JAR_A320NEO_FUEL_VALVES = 1291;

    // Nacelle
    public static final int JAR_A320NEO_NACELLE_TEMP_ = 1300;
    // System Display page
    public static final int JAR_A320NEO_SD_PAGE = 1304;
    // SPOILERS
    public static final int JAR_A320NEO_SPOILERS_LEFT = 1305;
    public static final int JAR_A320NEO_SPOILERS_RIGHT = 1306;
    // ATA24 ELEC
    public static final int JAR_A320NEO_EXT_HZ = 1314;
    public static final int JAR_A320NEO_EXT_VOLT = 1315;
    public static final int JAR_A320NEO_GEN1_HZ = 1316;
    public static final int JAR_A320NEO_GEN1_PER = 1317;
    public static final int JAR_A320NEO_GEN1_VOLT = 1318;
    public static final int JAR_A320NEO_GEN2_HZ = 1319;
    public static final int JAR_A320NEO_GEN2_PER = 1320;
    public static final int JAR_A320NEO_GEN2_VOLT = 1321;
    public static final int JAR_A320NEO_APU_HZ = 1322;
    public static final int JAR_A320NEO_APU_PER = 1323;
    public static final int JAR_A320NEO_APU_VOLT = 1324;
    public static final int JAR_A320NEO_GEN_EM_HZ = 1325;
    public static final int JAR_A320NEO_GEN_EM_VOLT = 1326;
    public static final int JAR_A320NEO_TR1_AMP = 1327;
    public static final int JAR_A320NEO_TR1_VOLT = 1328;
    public static final int JAR_A320NEO_TR2_AMP = 1329;
    public static final int JAR_A320NEO_TR2_VOLT = 1330;
    public static final int JAR_A320NEO_TR_EM_AMP = 1331;
    public static final int JAR_A320NEO_TR_EM_VOLT = 1332;
    public static final int JAR_A320NEO_DC_STATUS = 1333;
    public static final int JAR_A320NEO_AC_STATUS = 1334;
    public static final int JAR_A320NEO_GEN_STATUS = 1335;
    
    // Reserved range [1400-1499]
    
    // Flight Factor A320 Values [1500-1799]
    public static final int XFF_STATUS = 1500;
    public static final int XFF_APU_MASTER = 1600; //Unused currently
    public static final int XFF_APU_STARTER = 1601;
    public static final int XFF_APU_BLEED = 1602;
    public static final int XFF_APU_GEN = 1603;
    public static final int XFF_FCC = 1604;
    public static final int XFF_GEN_STATUS = 1605;
    public static final int XFF_RUD_TRIM_SWITCH = 1620;
    public static final int XFF_RUD_TRIM_RESET = 1621;
    public static final int XFF_ENG1_MASTER_SWITCH = 1645;
    public static final int XFF_ENG2_MASTER_SWITCH = 1646;
    public static final int XFF_ENG_MODE_SWITCH = 1647;
    public static final int XFF_THROTTLE_INPUT_L = 1648;
    public static final int XFF_THROTTLE_INPUT_R = 1649;
    public static final int XFF_EFIS_ND_SYMBOLS_CAPT = 1650;
    public static final int XFF_EFIS_ND_SYMBOLS_FO = 1651;
    public static final int XFF_STEER_L = 1652;
    public static final int XFF_STEER_R = 1653;
    public static final int XFF_STEER_L_PED_DISCO = 1654;
    public static final int XFF_STEER_R_PED_DISCO = 1655;
    public static final int XFF_CAPTAIN_PRIORITY = 1656;
    public static final int XFF_FO_PRIORITY = 1657;
    public static final int XFF_FCU_ALT_STEP = 1702;
    public static final int XFF_FCU_AP1 = 1703;
    public static final int XFF_FCU_AP2 = 1704;
    public static final int XFF_FCU_ATHR = 1705;
    public static final int XFF_FCU_APPR = 1706;
    public static final int XFF_FCU_LOC = 1707;
    public static final int XFF_FCU_EXPED = 1708;
    public static final int XFF_FCU_SPD_MACH = 1709;
    public static final int XFF_FCU_HDG_TRK = 1710;
    public static final int XFF_FCU_METRIC_ALT = 1711;
    public static final int XFF_FCU_FD_L = 1712;
    public static final int XFF_FCU_FD_R = 1713;
    public static final int XFF_FCU_LS_L = 1714;
    public static final int XFF_FCU_LS_R = 1715;
    public static final int XFF_FCU_CHRONO_L = 1716;
    public static final int XFF_FCU_CHRONO_R = 1717;
    public static final int XFF_FCU_SPD_MODE = 1718;
    public static final int XFF_FCU_ALT_MODE = 1719;
    public static final int XFF_FCU_HDG_MODE = 1720;
    public static final int XFF_FCU_VS_MODE = 1721;
    public static final int XFF_EFIS_NAV_MODE_C = 1722;
    public static final int XFF_EFIS_NAV_MODE_FO = 1723;
    public static final int XFF_AP_FD = 1724;
    public static final int XFF_AP_APPR = 1725;
    public static final int XFF_AP_DATA = 1726;
    public static final int XFF_AP_SPEED = 1727;
    public static final int XFF_AP_HEADING = 1728;
    public static final int XFF_AP_ALTITUDE = 1729;
    public static final int XFF_AP_VERTICAL = 1730;
    public static final int XFF_CHRONO_CAPT = 1731;
    public static final int XFF_CHRONO_FO = 1732;
    public static final int XFF_BARO_STATUS = 1733;
    public static final int XFF_BARO_C = 1734;
    public static final int XFF_BARO_FO = 1735;
    public static final int XFF_BARO_HPA = 1736;
    public static final int XFF_MFD_BUTTONS = 1737;
    
    public static final int Z737_STATUS = 1800;
    public static final int Z737_KEY_PRESS = 1801;
    public static final int Z737_ID_END = 1899;
    
    // Plugin Version
    public static final int PLUGIN_VERSION_ID = 999;


    // array with sim data for all sim data points defined above
    float[] sim_values_float = new float[1900];
    String[] sim_values_string = new String[1900];
    // updated status and timestamp for all sim data
    boolean[] sim_updated_float = new boolean[1900];
    long[] sim_timestamp_float = new long[1900];
    boolean[] sim_updated_string = new boolean[1900];
    long[] sim_timestamp_string = new long[1900];

    long updates = 0;
    ArrayList<Observer> observers;
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
        observers = new ArrayList<Observer>();
        for (int i=0; i<1800; i++) {
            sim_values_string[i] = "";
            sim_timestamp_string[i] = 0;
            sim_timestamp_float[i] = 0;
            sim_updated_string[i] = false;
            sim_updated_float[i] = false;
        }
        // Some data should be initialized with values different from 0

        sim_values_float[SIM_COCKPIT_ELECTRICAL_INSTRUMENT_BRIGHTNESS] = 0.8f;
        sim_values_float[SIM_COCKPIT_ELECTRICAL_COCKPIT_LIGHT] = 0.8f;
        sim_values_float[SIM_COCKPIT_ELECTRICAL_HUD_BRIGHTNESS] = 0.8f;
        sim_values_float[XHSI_DU_BRIGHT_PFD_CPT] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_ND_CPT] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_PFD_FO] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_ND_FO] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_EICAS] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_MFD] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_CLOCK] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_CDU_CPT] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_CDU_FO] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_CDU_OBS] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_FCU] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_OHP] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_PEDESTAL] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_DOME] = 1.0f;
        sim_values_float[XHSI_DU_BRIGHT_TABLET] = 1.0f;
        sim_values_float[SIM_COCKPIT_SWITCHES_EFIS_MAP_MODE] = 1; // 0=Centered, 1=Expanded
        sim_values_float[SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE] = 3; // 0=APP, 1=VOR, 2=MAP, 3=NAV, 4=PLN
        sim_values_float[SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR] = 1; // OFF
        sim_values_float[SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR] = 1; // OFF
        sim_values_float[XHSI_EFIS_COPILOT_MAP_CTR] = 1;
        sim_values_float[XHSI_EFIS_COPILOT_MAP_MODE] = 3;
        sim_values_float[SIM_COCKPIT_ELECTRICAL_AVIONICS_ON] = 1;
        sim_values_float[SIM_COCKPIT_ELECTRICAL_BATTERY_ON] = 1;
        sim_values_float[SIM_COCKPIT2_SWITCHES_AUTO_BRAKE_LEVEL] = 1; // Auto-brake OFF
        sim_values_float[SIM_GAUGES_FAILURES_PILOT] = 0x36DB6;    // start with all instruments failed
        sim_values_float[SIM_GAUGES_FAILURES_COPILOT] = 0x36DB6;  // start with all instruments failed
    }

    public boolean is_replaying() {
        return replaying;
    }

    public void add_observer(Observer observer) {
        this.observers.add(observer);
    }

    public void store_sim_float(int id, float value) {    	
    	if (sim_values_float[id] != value) {
    		sim_values_float[id] = value;
    		sim_updated_float[id] = true;
    		sim_timestamp_float[id] = System.currentTimeMillis();
    	}      
    }

    public void store_sim_string(int id, String value) {
        sim_values_string[id % 10000] = value;
    }

    public float get_sim_float(int id) {
        return sim_values_float[id];
    }

    public long get_sim_float_timestamp(int id) {
    	return sim_timestamp_float[id];
    }    
    
    public String get_sim_string(int id) {
        return sim_values_string[id % 10000];
    }
    
    public long get_sim_string_timestamp(int id) {
    	return sim_timestamp_string[id];
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
