package net.sourceforge.xhsi.model.xplane;

import java.util.logging.Logger;
import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimCommand;
import net.sourceforge.xhsi.model.Avionics.InstrumentSide;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPSender;

public class XPlaneCommand implements SimCommand {
    public static final int QPAC_KEY_TO_CONFIG = 1;
    public static final int QPAC_KEY_PUSH_ALT  = 2;
    public static final int QPAC_KEY_PULL_ALT  = 3;
    public static final int QPAC_KEY_PUSH_VS   = 4;
    public static final int QPAC_KEY_PULL_VS   = 5;
    public static final int QPAC_KEY_PUSH_HDG  = 6;
    public static final int QPAC_KEY_PULL_HDG  = 7;
    public static final int QPAC_KEY_PUSH_SPD  = 8;
    public static final int QPAC_KEY_PULL_SPD  = 9;
    public static final int QPAC_KEY_ATHR      = 10;
    public static final int QPAC_KEY_APPR      = 11;
    public static final int QPAC_KEY_EXPED     = 12;
    public static final int QPAC_KEY_LOC       = 13;
    public static final int QPAC_KEY_ABRK_LOW  = 14;
    public static final int QPAC_KEY_ABRK_MED  = 15;
    public static final int QPAC_KEY_ABRK_MAX  = 16;
    public static final int QPAC_KEY_AP1_PUSH =  177;
    public static final int QPAC_KEY_AP2_PUSH =  178;
    public static final int QPAC_KEY_FD1_PUSH =  179;
    public static final int QPAC_KEY_FD2_PUSH =  180;
    
    public static final int QPAC_SD_ENGINE = 0; 
    public static final int QPAC_SD_BLEED = 1; 
    public static final int QPAC_SD_CAB_PRESS = 2; 
    public static final int QPAC_SD_ELEC = 3; 
    public static final int QPAC_SD_HYDR = 4; 
    public static final int QPAC_SD_FUEL = 5; 
    public static final int QPAC_SD_APU = 6; 
    public static final int QPAC_SD_COND = 7; 
    public static final int QPAC_SD_DOOR_OXY = 8; 
    public static final int QPAC_SD_WHEEL = 9; 
    public static final int QPAC_SD_FCTL = 10; 
    public static final int QPAC_SD_SYS = 11; 
    public static final int QPAC_SD_STATUS = 12; 
    
    public static final int JAR_A320_SD_HYDR = 0; 
    public static final int JAR_A320_SD_FUEL = 1; 
    public static final int JAR_A320_SD_APU = 2; 
    public static final int JAR_A320_SD_CAB_PRESS = 3; 
    public static final int JAR_A320_SD_FCTL = 4; 
    public static final int JAR_A320_SD_WHEELS = 5; 
    public static final int JAR_A320_SD_ELEC = 6; 
    public static final int JAR_A320_SD_BLEED = 7; 
    public static final int JAR_A320_SD_COND = 8; 
    public static final int JAR_A320_SD_DOOR_OXY = 9; 
    public static final int JAR_A320_SD_SYS = 10; 
    public static final int JAR_A320_SD_ENGINE = 11;    
    
    // Values for SIM_COCKPIT_AUTOPILOT_KEY_PRESS
    // Must be in sync with datarefs.h
    // MCP Buttons
    public static final int AP_KEY_IS_MACH = 1;
    public static final int AP_KEY_CMD_A = 2;
    public static final int AP_KEY_CMD_B = 2; // TODO : CMD B
    public static final int AP_KEY_SPD_TOGGLE = 3;
    public static final int AP_KEY_LVL_CHG_TOGGLE = 4;
    public static final int AP_KEY_HDG_SEL_TOGGLE = 5;
    public static final int AP_KEY_VS_TOGGLE = 6;
    public static final int AP_KEY_NAV_TOGGLE = 7;
    public static final int AP_KEY_APPR_TOGGLE = 8;
    public static final int AP_KEY_GLIDE_SLOPE = 9;
    public static final int AP_KEY_BACK_COURSE = 10;
    public static final int AP_KEY_ALT_HOLD_TOGGLE = 11;
    public static final int AP_KEY_ILS_CAPT_TOGGLE = 12;
    public static final int AP_KEY_ILS_FO_TOGGLE = 13;
    public static final int AP_KEY_WLV = 14;
    // Lights
    public static final int AP_KEY_NAV_LIGHTS_TOGGLE = 20;
    public static final int AP_KEY_BEACON_LIGHTS_TOGGLE = 21;
    public static final int AP_KEY_TAXI_LIGHTS_TOGGLE = 22;
    public static final int AP_KEY_STROBE_LIGHTS_TOGGLE = 23;
    public static final int AP_KEY_LDG_LIGHTS_TOGGLE = 24; 
    // Flight controls
    public static final int AP_KEY_LDG_GEAR_TOGGLE = 30;
    public static final int AP_KEY_FLAPS_DOWN = 31;
    public static final int AP_KEY_FLAPS_UP = 32;
    public static final int AP_KEY_SPD_BREAK_DOWN = 33;
    public static final int AP_KEY_SPD_BREAK_UP = 34;
    public static final int AP_KEY_LDG_GEAR_DOWN = 35;
    public static final int AP_KEY_LDG_GEAR_UP = 36;
    // Master caution, warning, accept
    public static final int AP_KEY_CLR_MASTER_WARNING = 50;
    public static final int AP_KEY_CLR_MASTER_CAUTION = 51;
    public static final int AP_KEY_CLR_MASTER_ACCEPT = 52;
    // Systems
    public static final int AP_KEY_PITOT_HEAT_TOGGLE = 60;
    

    ModelFactory model_factory;
    Aircraft aircraft;
    Avionics avionics;
    XHSIPreferences preferences;
    private XPlaneUDPSender udp_sender;
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public XPlaneCommand(Aircraft aircraft, ModelFactory model_factory) {
        this.model_factory = model_factory;
        this.aircraft = aircraft;
        this.avionics = this.aircraft.get_avionics();
        this.preferences = XHSIPreferences.get_instance();
        this.udp_sender = XPlaneUDPSender.get_instance();
    }
    
    public void send(int button_id) {
        switch (button_id) {
            case CMD_EFIS_CAPT_CSTR:
            	this.avionics.set_show_data(!this.avionics.efis_shows_data());
                break;
            
            case CMD_EFIS_CAPT_WPT:
                this.udp_sender.sendDataPoint(207, !this.avionics.efis_shows_wpt(InstrumentSide.PILOT) ? 1.0f : 0.0f);
                break;
            
            case CMD_EFIS_CAPT_VOR:
                this.udp_sender.sendDataPoint(208, !this.avionics.efis_shows_vor(InstrumentSide.PILOT) ? 1.0f : 0.0f);
                break;
            
            case CMD_EFIS_CAPT_NDB:
                this.udp_sender.sendDataPoint(209, !this.avionics.efis_shows_ndb(InstrumentSide.PILOT) ? 1.0f : 0.0f);
                break;
            
            case CMD_EFIS_CAPT_APT:
                this.udp_sender.sendDataPoint(206, !this.avionics.efis_shows_arpt(InstrumentSide.PILOT) ? 1.0f : 0.0f);
                break;
            
            case CMD_EFIS_CAPT_FD:
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_FD1_PUSH);
                break;
            
            case CMD_EFIS_CAPT_ILS:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_ILS_CAPT_TOGGLE);
                break;
            
            case CMD_EFIS_CAPT_INHG:
                break;
            
            case CMD_EFIS_CAPT_HPA: 
                break;
            
            case CMD_EFIS_CAPT_NAVAID1_ADF:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR, 0.0f);
                break;
            
            case CMD_EFIS_CAPT_NAVAID1_OFF:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR, 1.0f);
                break;
            
            case CMD_EFIS_CAPT_NAVAID1_VOR:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR, 2.0f);
                break;
            
            case CMD_EFIS_CAPT_NAVAID2_ADF:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR, 0.0f);
                break;
            
            case CMD_EFIS_CAPT_NAVAID2_OFF:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR, 1.0f);
                break;
            
            case CMD_EFIS_CAPT_NAVAID2_VOR:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR, 2.0f);
                break;
            
            case CMD_EFIS_CAPT_BARO_STD:
            	// XPlaneSimDataRepository.QPAC_FCU_BARO & 0x04
                break;
            
            case CMD_EFIS_CAPT_BARO_INC:
            	// aircraft.qnh (hPa) or altimeter_in_hg(boolean pilot)
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT, aircraft.altimeter_in_hg(true) + 0.01f);
                break;
            
            case CMD_EFIS_CAPT_BARO_DEC:
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_PILOT, aircraft.altimeter_in_hg(true) - 0.01f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_INC:
                if (this.avionics.map_range_index(InstrumentSide.PILOT) >= 5) break;
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, (float)this.avionics.map_range_index(InstrumentSide.PILOT) + 1.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_DEC:
                if (this.avionics.map_range_index(InstrumentSide.PILOT) <= 0) break;
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, (float)this.avionics.map_range_index(InstrumentSide.PILOT) - 1.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_10:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 0.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_20:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 1.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_40:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 2.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_80:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 3.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_160: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 4.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_320:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 5.0f);
                break;
            
            case CMD_EFIS_CAPT_RANGE_640:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR, 6.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_ILS: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, 1.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_VOR: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, 2.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_NAV: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, 3.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_ARC: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, 4.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_PLN: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, 5.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_INC: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, (float)this.avionics.map_submode(InstrumentSide.PILOT) + 1.0f);
                break;
            
            case CMD_EFIS_CAPT_MODE_DEC: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_SUBMODE, (float)this.avionics.map_submode(InstrumentSide.PILOT) - 1.0f);
                break;
            
            case CMD_EFIS_CAPT_CHRONO: 
            	this.avionics.chr_control(Avionics.CHR_ACT_CAPT_START_RESET);
                break;
            
            case CMD_EFIS_CAPT_STICK: 
                break;
            
            case CMD_EFIS_CAPT_TERRAIN_ND: 
            	this.avionics.set_show_terrain(!this.avionics.efis_shows_terrain(InstrumentSide.PILOT),InstrumentSide.PILOT);
                break;
            
            case CMD_EFIS_FO_CSTR:
            	//TODO: Data and constraint are slightly different
                this.avionics.set_show_data(!this.avionics.efis_shows_data());
                break;
            	
            case CMD_EFIS_FO_WPT: 
                this.avionics.set_show_wpt(!this.avionics.efis_shows_wpt(InstrumentSide.COPILOT));
                break;
            
            case CMD_EFIS_FO_VOR: 
                this.avionics.set_show_vor(!this.avionics.efis_shows_vor(InstrumentSide.COPILOT));
                break;
            
            case CMD_EFIS_FO_NDB: 
                this.avionics.set_show_ndb(!this.avionics.efis_shows_ndb(InstrumentSide.COPILOT));
                break;
            
            case CMD_EFIS_FO_APT: 
                this.avionics.set_show_arpt(!this.avionics.efis_shows_arpt(InstrumentSide.COPILOT));
                break;
            
            case CMD_EFIS_FO_FD: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_FD2_PUSH);
            	break;            
            
            case CMD_EFIS_FO_ILS: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_ILS_FO_TOGGLE);
                break;
                
            case CMD_EFIS_FO_INHG: break;
            case CMD_EFIS_FO_HPA: break;
            case CMD_EFIS_FO_NAVAID1_ADF: break;
            case CMD_EFIS_FO_NAVAID1_OFF: break;
            case CMD_EFIS_FO_NAVAID1_VOR: break;
            case CMD_EFIS_FO_NAVAID2_ADF: break;
            case CMD_EFIS_FO_NAVAID2_OFF: break;
            case CMD_EFIS_FO_NAVAID2_VOR: break;
            case CMD_EFIS_FO_BARO_STD: break;
            case CMD_EFIS_FO_BARO_INC:
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT, aircraft.altimeter_in_hg(true) + 0.01f);
            	break;
            case CMD_EFIS_FO_BARO_DEC: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT2_GAUGES_ACTUATORS_BAROMETER_SETTING_IN_HG_COPILOT, aircraft.altimeter_in_hg(true) - 0.01f);
            	break;
            case CMD_EFIS_FO_RANGE_INC: break;
            case CMD_EFIS_FO_RANGE_DEC: break;
            case CMD_EFIS_FO_RANGE_10: break;
            case CMD_EFIS_FO_RANGE_20: break;
            case CMD_EFIS_FO_RANGE_40: break;
            case CMD_EFIS_FO_RANGE_80: break;
            case CMD_EFIS_FO_RANGE_160: break;
            case CMD_EFIS_FO_RANGE_320: break;
            case CMD_EFIS_FO_RANGE_640: break;
            case CMD_EFIS_FO_MODE_ILS: break;
            case CMD_EFIS_FO_MODE_VOR: break;
            case CMD_EFIS_FO_MODE_NAV: break;
            case CMD_EFIS_FO_MODE_ARC: break;
            case CMD_EFIS_FO_MODE_PLN: break;
            case CMD_EFIS_FO_MODE_INC: break;
            case CMD_EFIS_FO_MODE_DEC: break;
            case CMD_EFIS_FO_CHRONO: 
            	this.avionics.chr_control(Avionics.CHR_ACT_FO_START_RESET);
            	break;
            case CMD_EFIS_FO_STICK: break;
            
            case CMD_EFIS_FO_TERRAIN_ND:
            	this.avionics.set_show_terrain(!this.avionics.efis_shows_terrain(InstrumentSide.COPILOT),InstrumentSide.COPILOT);
            	break;
            
            case CMD_ECAM_TO_CFG: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_TO_CONFIG);
                break;
                      
            case CMD_ECAM_EMER_CANC:
                if (this.avionics.is_ff_a320()) {
                    this.udp_sender.sendDataPoint(XPlaneSimDataRepository.XFF_MFD_BUTTONS, (float) 0x02);
                }
                break;
            
            case CMD_ECAM_ENG:  
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_ENGINE);
                break;
            
            case CMD_ECAM_BLEED: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_BLEED);
                break;
            
            case CMD_ECAM_PRESS: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_CAB_PRESS);           	
                break;
            
            case CMD_ECAM_ELEC: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_ELEC);            	
                break;
            
            case CMD_ECAM_HYD: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_HYDR); 
                break;
            
            case CMD_ECAM_FUEL: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_FUEL); 
                break;
            
            case CMD_ECAM_APU: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_APU); 
                break;
            
            case CMD_ECAM_COND: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_COND); 
                break;
            
            case CMD_ECAM_DOOR: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_DOOR_OXY); 
                break;
            
            case CMD_ECAM_WHEEL: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_WHEEL); 
                break;
            
            case CMD_ECAM_FCTL: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_FCTL); 
                break;
            
            case CMD_ECAM_ALL: 
                break;
            
            case CMD_ECAM_CLR: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_CLR_MASTER_ACCEPT);
                break;
            
            case CMD_ECAM_STS: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_SD_PAGE, QPAC_SD_STATUS); 
                break;
            
            case CMD_ECAM_RCL: 
                if (this.avionics.is_ff_a320()) {
                    udp_sender.sendDataPoint(XPlaneSimDataRepository.XFF_MFD_BUTTONS, (float) 0x10000);
                }
                break;
            
            case CMD_ECAM_APT_CHART: 
                break;
            
            case CMD_ECAM_RTU: 
                break;
            
            case CMD_ECAM_FPLN: 
                break;
            
            case CMD_FCU_AP1: // CMD A on Boeing
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_AP1_PUSH);
                break;
            
            case CMD_FCU_AP2: // CMD B on Boeing
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_AP2_PUSH);
                break;
            
            case CMD_FCU_ATHR: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_ATHR);
                break;
            
            case CMD_FCU_LOC: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_LOC);
                break;
            
            case CMD_FCU_APPR: 
            	if (this.avionics.is_qpac()) {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_APPR);
            	} else {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_APPR_TOGGLE );
            	}
                break;
            
            case CMD_FCU_EXP: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_EXPED);
                break;
            
            case CMD_FCU_METRIC: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.XHSI_EFIS_PILOT_METRIC_ALT, this.avionics.pfd_shows_metric_alt() ? 0.0f : 1.0f );
                break;
            
            case CMD_FCU_TRK_FPA: 
            	// this.udp_sender.sendDataPoint(XPlaneSimDataRepository.XHSI_EFIS_PILOT_TRK_FPA, this.avionics.??? );            	
                break;
            
            case CMD_FCU_MACH: 
                break;
            
            case CMD_FCU_SPD_UP: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AIRSPEED, this.avionics.autopilot_speed() + 1.0f);
                break;
            
            case CMD_FCU_SPD_DOWN: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_AIRSPEED, this.avionics.autopilot_speed() - 1.0f);
                break;
            
            case CMD_FCU_SPD_MNG: 
            	if (this.avionics.is_qpac()) {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PUSH_SPD);
            	} else {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_SPD_TOGGLE );
            	}
                break;
            
            case CMD_FCU_SPD_SEL:
            	if (this.avionics.is_qpac()) {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PULL_SPD);
            	} else {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_SPD_TOGGLE );
            	}
                break;
            
            case CMD_FCU_HDG_UP: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_HEADING_MAG, this.avionics.heading_bug() + 1.0f);
                break;
            
            case CMD_FCU_HDG_DOWN: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_HEADING_MAG, this.avionics.heading_bug() - 1.0f);
                break;   
                
            case CMD_FCU_WLV: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_WLV );
                break; 
                
            case CMD_FCU_HDG_MNG: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PUSH_HDG);
                break;
            
            case CMD_FCU_HDG_SEL: 
            	if (this.avionics.is_qpac()) {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PULL_HDG);
            	} else {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_HDG_SEL_TOGGLE );
            	}

                break;
            
            case CMD_FCU_ALT_UP: 
                break;
            
            case CMD_FCU_ALT_DOWN: 
                break;
            
            case CMD_FCU_ALT_MNG: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PUSH_ALT);
                break;
            
            case CMD_FCU_ALT_SEL: 
            	if (this.avionics.is_qpac()) {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PULL_ALT);
            	} else {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_LVL_CHG_TOGGLE);
            	}
                break;
            
            case CMD_FCU_ALT_FINE: 
                break;
            
            case CMD_FCU_ALT_COARSE: 
                break;
            
            case CMD_FCU_VS_UP: 
                break;
            
            case CMD_FCU_VS_DOWN: 
                break;
            
            case CMD_FCU_VS_SEL: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PULL_VS);
                break;
            
            case CMD_FCU_VS_LVLOFF:
            	if (this.avionics.is_qpac()) {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_PUSH_VS);
            	}  else {
            		this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_ALT_HOLD_TOGGLE );
            	}
                break;
            
            case CMD_MASTER_WRN: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_CLR_MASTER_WARNING);
                break;
            
            case CMD_MASTER_CTN: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_CLR_MASTER_CAUTION);
                break;     
                
            case CMD_MASTER_ACC:
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_CLR_MASTER_ACCEPT);
                break;  
                
            case CMD_A_SKID_OFF: 
                break;
            
            case CMD_A_SKID_ON: 
                break;
            
            case CMD_ABRK_LOW: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_ABRK_LOW);
                break;
            
            case CMD_ABRK_MED: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_ABRK_MED);
                break;
            
            case CMD_ABRK_MAX: 
                this.udp_sender.sendDataPoint(XPlaneSimDataRepository.QPAC_KEY_PRESS, QPAC_KEY_ABRK_MAX);
                break;
            
            case CMD_BRK_FAN: 
                break;
            
            case CMD_GEAR_UP: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_LDG_GEAR_UP); 
                break;
            
            case CMD_GEAR_DOWN: 
            	this.udp_sender.sendDataPoint(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_KEY_PRESS, AP_KEY_LDG_GEAR_DOWN);
            	break;
            	
            case CMD_CHRONO_START_STOP_RESET:
            	this.avionics.chr_control(Avionics.CHR_CONTROL_START_STOP_RESET);
            	break;

            case CMD_CHRONO_START_STOP:
            	this.avionics.chr_control(Avionics.CHR_CONTROL_START_STOP);
            	break;

            case CMD_CHRONO_RESET:
            	this.avionics.chr_control(Avionics.CHR_CONTROL_RESET);
            	break;

        }
    }
}
