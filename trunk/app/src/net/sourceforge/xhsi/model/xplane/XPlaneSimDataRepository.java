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

import net.sourceforge.xhsi.model.Observer;

public class XPlaneSimDataRepository {

    // Aircraft position
    public static final int SIM_FLIGHTMODEL_POSITION_GROUNDSPEED = 0;
    public static final int SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED = 1;
    public static final int SIM_FLIGHTMODEL_POSITION_MAGPSI = 2; // mag heading
    public static final int SIM_FLIGHTMODEL_POSITION_HPATH = 3; // track (true or mag?)
    public static final int SIM_FLIGHTMODEL_POSITION_LATITUDE = 4;
    public static final int SIM_FLIGHTMODEL_POSITION_LONGITUDE = 5;
    public static final int SIM_FLIGHTMODEL_POSITION_PHI = 6;            // roll angle
    public static final int SIM_FLIGHTMODEL_POSITION_R = 7;            // yaw rotation rate in deg/sec
    public static final int SIM_FLIGHTMODEL_POSITION_MAGVAR = 8; // XPSDK says that this dataref should be part of the environment, not the aircraft
    public static final int SIM_FLIGHTMODEL_POSITION_ELEVATION = 9; // meters
    public static final int SIM_FLIGHTMODEL_POSITION_Y_AGL = 10; // meters
    
    // Instruments
    public static final int SIM_FLIGHTMODEL_POSITION_VH_IND_FPM = 50; // vertical velocity
    public static final int SIM_FLIGHTMODEL_MISC_H_IND = 51; // indicated barometric altitude

    // Electrical
    public static final int SIM_COCKPIT_ELECTRICAL_AVIONICS_ON = 80;
    //public static final int SIM_COCKPIT2_SWITCHES_AVIONICS_ON = 81;

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

    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_NAV1_NAV_ID = 10140;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_NAV2_NAV_ID = 10141;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_ADF1_NAV_ID = 10142;
    public static final int SIM_COCKPIT2_RADIOS_INDICATORS_ADF2_NAV_ID = 10143;

    // AP
    public static final int SIM_COCKPIT_AUTOPILOT_STATE = 150;
    public static final int SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY = 151;
    public static final int SIM_COCKPIT_AUTOPILOT_ALTITUDE = 152;
    public static final int SIM_COCKPIT_AUTOPILOT_APPROACH_SELECTOR = 153;
    public static final int SIM_COCKPIT_AUTOPILOT_HEADING_MAG = 154;

    // Transponder
    public static final int SIM_COCKPIT_RADIOS_TRANSPONDER_MODE = 180; // 0=OFF, 1=STDBY, 2=ON, 3=TEST

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

    // Environment
    public static final int SIM_WEATHER_WIND_SPEED_KT = 300;
    public static final int SIM_WEATHER_WIND_DIRECTION_DEGT = 301;
    public static final int SIM_TIME_ZULU_TIME_SEC = 302;
    public static final int SIM_TIME_LOCAL_TIME_SEC = 303;

    // Plugin Version
    public static final int PLUGIN_VERSION_ID = 400;


    // array with sim data for all sim data points defined above
    float[] sim_values_float = new float[512];
    //int[] sim_values_int = new int[512];
    String[] sim_values_string = new String[512];

    long updates = 0;
    ArrayList observers;
    public static boolean source_is_recording = false;

    private static XPlaneSimDataRepository single_instance;

    public static XPlaneSimDataRepository get_instance() {
        if (XPlaneSimDataRepository.single_instance == null) {
            XPlaneSimDataRepository.single_instance = new XPlaneSimDataRepository();
        }
        return XPlaneSimDataRepository.single_instance;
    }

    private XPlaneSimDataRepository() {
        observers = new ArrayList();
        for (int i=0; i<512; i++) {
            sim_values_string[i] = "";
        }
    }

    public void add_observer(Observer observer) {
        this.observers.add(observer);
    }

    public void store_sim_float(int id, float value) {
        sim_values_float[id] = value;
    }

// We will see about integer sim_data values later...
//    public void store_sim_int(int id, int value) {
//        sim_values_int[id % 1000] = value;
//    }

    public void store_sim_string(int id, String value) {
        sim_values_string[id % 10000] = value;
    }

    public float get_sim_float(int id) {
        return sim_values_float[id];
    }

// We will see about integer sim_data values later...
//    public int get_sim_int(int id) {
//        return sim_values_int[id % 1000];
//    }

    public String get_sim_string(int id) {
        return sim_values_string[id % 10000];
    }

    public void tick_updates() {
        this.updates += 1;
        for (int i=0; i<this.observers.size(); i++) {
            ((Observer) this.observers.get(i)).update();
        }
    }

    public long get_nb_of_updates() {
        return this.updates;
    }
}
