package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.model.ModelFactory;

public class ECAM_Memo extends EICASSubcomponent {

	private String memo_list[] = { 
			"LAND ASAP", // (amber or red)
			"APU AVAIL",
			"APU BLEED",
			"AUDIO 3 XFRD",
			"AUTO BRK LO",
			"AUTO BRK MAX",
			"AUTO BRK MED",
			"GND SPLRS ARMED",
			"GPWS FLAP MODE OFF",
			"IRS IN ALIGN",
			"IRS IN ALIGN..MN",
			"NO SMOKING",
			"OUTER TK FUEL XFRD",
			"OUTR TK FUEL XFRD",
			"REFUELG",
			"SEAT BELTS",
			"STROBE LT OFF",
			"BRK FAN",
			"CTR TK FEEDG",
			"ENG A.ICE",
			"FUEL X FEED",
			"GPWS FLAP 3",
			"HYD PTU",
			"ICE NOT DET",
			"LDG INHIBIT", // (magenta)
			"LDG LT",
			"NW STRG DISC",
			"PARK BRK",
			"RAT OUT",
			"SPEED BRK",
			"SWITCHG PNL",
			"T.O. INHIBIT", // (magenta)
			"TCAS STBY",
			"WING A.ICE"
		 };
	
	private static final int EMEMO_LAND_ASAP = 0;
	private static final int EMEMO_APU_AVAIL = 1;
	private static final int EMEMO_APU_BLEED = 2;
	private static final int EMEMO_AUDIO_3_XFRD = 3;
	private static final int EMEMO_AUTO_BRK_LO = 4;
	private static final int EMEMO_AUTO_BRK_MAX = 5;
	private static final int EMEMO_AUTO_BRK_MED = 6;
	private static final int EMEMO_GND_SPLRS_ARMED = 7;
	private static final int EMEMO_GPWS_FLAP_MODE_OFF = 8;
	private static final int EMEMO_IRS_IN_ALIGN = 9;
	private static final int EMEMO_IRS_IN_ALIGN_MN = 10;
	private static final int EMEMO_NO_SMOKING = 11;
	private static final int EMEMO_OUTER_TK_FUEL_XFRD = 12;
	private static final int EMEMO_OUTR_TK_FUEL_XFRD = 13;
	private static final int EMEMO_REFUELG = 14;
	private static final int EMEMO_SEAT_BELTS = 15;
	private static final int EMEMO_STROBE_LT_OFF = 16;
	private static final int EMEMO_BRK_FAN = 17;
	private static final int EMEMO_CTR_TK_FEEDG = 18;
	private static final int EMEMO_ENG_A_ICE = 19;
	private static final int EMEMO_FUEL_X_FEED = 20;
	private static final int EMEMO_GPWS_FLAP_3 = 21;
	private static final int EMEMO_HYD_PTU = 22;
	private static final int EMEMO_ICE_NOT_DET = 23;
	private static final int EMEMO_LDG_INHIBIT = 24; // (magenta)
	private static final int EMEMO_LDG_LT = 25;
	private static final int EMEMO_NW_STRG_DISC = 26;
	private static final int EMEMO_PARK_BRK = 27;
	private static final int EMEMO_RAT_OUT = 28;
	private static final int EMEMO_SPEED_BRK = 29;
	private static final int EMEMO_SWITCHG_PNL = 30;
	private static final int EMEMO_T_O_INHIBIT = 31;// (magenta)
	private static final int EMEMO_TCAS_STBY = 32;
	private static final int EMEMO_WING_A_ICE = 33;
	private static final int MAX_EMEMO_MSG = 33;
 
	private boolean ememo_status[];
	private Color ememo_color[];

	public ECAM_Memo(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		// TODO Auto-generated constructor stub
		ememo_status = new boolean[MAX_EMEMO_MSG];
		ememo_color = new Color[MAX_EMEMO_MSG];
		for (int i=0; i< MAX_EMEMO_MSG; i++) {
			ememo_status[i]= false;
			ememo_color[i] = eicas_gc.ecam_normal_color;
		}
	}


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && eicas_gc.airbus_style ) {        	
        	DrawXHSIEcamMemo(g2);
        	/*
        	if (this.avionics.is_qpac()) {
        		DrawQpacEcamMemo(g2);
        	} else if (this.avionics.is_jar_a320neo()) {
        		DrawJarA320neoEcamMemo(g2);
        	} else {
        		DrawXHSIEcamMemo(g2);
        	}
        	*/
        		
        }
    }
    
    private void DrawQpacEcamMemo(Graphics2D g2) {
    	
    }
 
    private void DrawJarA320neoEcamMemo(Graphics2D g2) {
    	
    }
    
    private void DrawXHSIEcamMemo(Graphics2D g2) {
    	
    	String memo_str;
    	g2.setFont(eicas_gc.font_l);        
    	
        // PARK BRK
        if ( this.aircraft.battery() ) {
            float parking_brake = this.aircraft.get_parking_brake();
            if ( ! this .aircraft.on_ground() && ( parking_brake > 0.01f ) && ! this.aircraft.gear_is_up() ) {
                ememo_color[EMEMO_PARK_BRK] = eicas_gc.ecam_normal_color;
                ememo_status[EMEMO_PARK_BRK] = true;
            } else if ( ( parking_brake > 0.51f ) && ! this.aircraft.gear_is_up() ) {               
                ememo_color[EMEMO_PARK_BRK] = eicas_gc.ecam_normal_color;
                ememo_status[EMEMO_PARK_BRK] = true;
            } else if ( ( parking_brake > 0.01f ) && ! this.aircraft.gear_is_up() ) {                
                ememo_color[EMEMO_PARK_BRK] = eicas_gc.ecam_normal_color;
                ememo_status[EMEMO_PARK_BRK] = true;
            } else {
            	ememo_status[EMEMO_PARK_BRK] = false;               
            }
        } else {
        	ememo_status[EMEMO_PARK_BRK] = false; 
        }
        
        // SPEED BRK
        if ( this.aircraft.battery() ) {
            float speed_brake = this.aircraft.get_speed_brake();
            if ( speed_brake > 0.51f ) {
                ememo_color[EMEMO_SPEED_BRK ] = eicas_gc.ecam_warning_color;
                ememo_status[EMEMO_SPEED_BRK ] = true;
            } else if ( ( ( ! this.avionics.is_cl30() ) && ( speed_brake > 0.01f ) ) || ( ( this.avionics.is_cl30() ) && ( speed_brake > 0.033f ) ) ) {
                ememo_color[EMEMO_SPEED_BRK ] = eicas_gc.ecam_caution_color;
                ememo_status[EMEMO_SPEED_BRK ] = true;
            } else if ( this.aircraft.speed_brake_armed() ) {
                ememo_color[EMEMO_SPEED_BRK ] = eicas_gc.ecam_normal_color;
                ememo_status[EMEMO_SPEED_BRK ] = true;
            } else {
                ememo_status[EMEMO_SPEED_BRK ] = false;
            }
        } else {
        	ememo_status[EMEMO_SPEED_BRK ] = false;
        }
        
        // AUTO BRK
        int autobrake = this.aircraft.auto_brake();        
        switch (autobrake) {           
            case -1 :
            	ememo_status[EMEMO_AUTO_BRK_LO] = false;
            	ememo_status[EMEMO_AUTO_BRK_MED] = false;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = true; 
                break;
            case 1 :
            	ememo_status[EMEMO_AUTO_BRK_LO] = true;
            	ememo_status[EMEMO_AUTO_BRK_MED] = false;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = false;
                break;
            case 2 :
            	ememo_status[EMEMO_AUTO_BRK_LO] = false;
            	ememo_status[EMEMO_AUTO_BRK_MED] = true;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = false;
                break;
            case 3 :
            	ememo_status[EMEMO_AUTO_BRK_LO] = false;
            	ememo_status[EMEMO_AUTO_BRK_MED] = true;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = false;
                break;
            case 4 :
            	ememo_status[EMEMO_AUTO_BRK_LO] = false;
            	ememo_status[EMEMO_AUTO_BRK_MED] = false;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = true; 
                break;
            default :
            	ememo_status[EMEMO_AUTO_BRK_LO] = false;
            	ememo_status[EMEMO_AUTO_BRK_MED] = false;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = false;
                break;

        }
        if ( this.aircraft.battery() ) {
            if ( ( ! this.aircraft.on_ground() && ( this.aircraft.auto_brake() == -1 ) ) ||
                    ( this.aircraft.on_ground() && ( this.aircraft.auto_brake() > 0 ) ) ) {
                // RTO in the air or 1,2,3,max on the ground : caution
            	ememo_color[EMEMO_AUTO_BRK_LO] = eicas_gc.ecam_caution_color;
            	ememo_color[EMEMO_AUTO_BRK_MED] = eicas_gc.ecam_caution_color;
                ememo_color[EMEMO_AUTO_BRK_MAX] = eicas_gc.ecam_normal_color;
                               
            } else if ( ( this.aircraft.on_ground() && ( this.aircraft.auto_brake() == -1 ) ) ||
                    ( ! this.aircraft.on_ground() && ( this.aircraft.auto_brake() > 0 ) ) ) {
                // RTO on the ground or 1,2,3,max in the air : armed
            	ememo_color[EMEMO_AUTO_BRK_LO] = eicas_gc.ecam_normal_color;
            	ememo_color[EMEMO_AUTO_BRK_MED] = eicas_gc.ecam_normal_color;
                ememo_color[EMEMO_AUTO_BRK_MAX] = eicas_gc.ecam_normal_color;
                             
            } else { 
            	ememo_status[EMEMO_AUTO_BRK_LO] = false;
            	ememo_status[EMEMO_AUTO_BRK_MED] = false;
            	ememo_status[EMEMO_AUTO_BRK_MAX] = false;               
            }
        } else {
        	ememo_status[EMEMO_AUTO_BRK_LO] = false;
        	ememo_status[EMEMO_AUTO_BRK_MED] = false;
        	ememo_status[EMEMO_AUTO_BRK_MAX] = false;
        }
    	

        // display the first 8 lines of the pop list
        for (int i=0, line=0; i < MAX_EMEMO_MSG && line < 8; i++) {
        	if (ememo_status[i]) { 
        			memo_str = memo_list[i];
        			g2.setColor(ememo_color[i]);
        			g2.drawString(memo_str, eicas_gc.memo_x,eicas_gc.memo_y + line * eicas_gc.line_height_l);          			
        			line++;
        	}
        	
        }
    	

        
    } 

    
}
