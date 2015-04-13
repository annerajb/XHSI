package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.model.ModelFactory;

public class ECAM_Messages extends EICASSubcomponent {
	
	private String message_list[] = { 		
			"APU AVAIL",
			"APU BLEED",
			"AUDIO 3 XFRD",
			"AUTO BRK LO",
			"AUTO BRK MAX",
			"AUTO BRK MED",
			"GND SPOILERS ARMED",
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
			"LANDING LT",
			"NW STRG DISC",
			"PARK BRK",
			"RAT OUT",
			"SPEED BRK",
			"SWITCHG PNL",			
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
	private static final int MAX_EMSG_MSG = 33;
 
	private boolean emsg_status[];
	private Color emsg_color[];

	public ECAM_Messages(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		// TODO Auto-generated constructor stub
		emsg_status = new boolean[MAX_EMSG_MSG];
		emsg_color = new Color[MAX_EMSG_MSG];
		for (int i=0; i< MAX_EMSG_MSG; i++) {
			emsg_status[i]= false;
			emsg_color[i] = eicas_gc.ecam_normal_color;
		}
	}
	
    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && eicas_gc.airbus_style ) {        	
        	
        	if (this.avionics.is_qpac()) {
        		DrawQpacEcamMessages(g2);
        	} else if (this.avionics.is_jar_a320neo()) {
        		DrawJarA320neoEcamMessages(g2);
        	} else {
        		DrawXHSIEcamMessages(g2);
        	}
        		
        }
    }
    
    
    private void DrawQpacEcamMessages(Graphics2D g2) {
    	
    }
 
    private void DrawJarA320neoEcamMessages(Graphics2D g2) {
    	
    }
    
    private void DrawXHSIEcamMessages(Graphics2D g2) {
    	
    	String message_str;
		boolean takeoff_inhibit = false;
		boolean landing_inhibit = false;
		boolean takeoff_memo = false;
		boolean landing_memo = false;

		boolean all_ok = true;
		
    	g2.setFont(eicas_gc.font_l);        
    	
    	// T.O. INHIBIT
    	// T.O. INHIBIT from takeoff thrust up to 1500 feet AGL on Airbus 
    	// T.O. INHIBIT for General Aviation should be up to 500 feet

    	// T.O. Memo comes up when all conditions are reached
    	// - on ground
		// - at least one engine on
		// - wasn't airbone since last engine off
		// - more than 120 seconds since last engine start
		// - throttle lever percent < 40%
    	
    	if (this.aircraft.on_ground() ) {
    		// Display Takeoff Memo
    		takeoff_inhibit = true;
    		
    		// DrawString "T.O   "
    		String to_str = "T.O   ";
    		int x_shift = eicas_gc.get_text_width(g2, eicas_gc.font_l, to_str);
    		g2.setColor(eicas_gc.ecam_normal_color);
    		g2.drawString(to_str, eicas_gc.message_x, eicas_gc.memo_y + eicas_gc.line_height_l);
    		g2.drawLine(eicas_gc.message_x, eicas_gc.memo_y + eicas_gc.line_height_l, x_shift, eicas_gc.memo_y + eicas_gc.line_height_l);
    		
    	} else if ( !this.aircraft.on_ground() ) {
    		// Display LDG Memo
    		// DrawString "LDG   "
    		String ldg_str = "LDG   ";
    		g2.setColor(eicas_gc.ecam_normal_color);
    		int x_shift = eicas_gc.get_text_width(g2, eicas_gc.font_l, ldg_str);
    		g2.setColor(eicas_gc.ecam_normal_color);
    		g2.drawString(ldg_str, eicas_gc.message_x, eicas_gc.memo_y + eicas_gc.line_height_l);
    		g2.drawLine(eicas_gc.message_x, eicas_gc.memo_y + eicas_gc.line_height_l, x_shift, eicas_gc.memo_y + eicas_gc.line_height_l);
  		
    	} else {
    		// Display ECAM messages
    	}
    		
    	
    	// Speed brakes (armed or extended)
    	// Parking brake (amber if in flight)
    	// autobrake
    	// AP disconnect
    	// Low fluel
    	
    	// Alarms on the left panel
    	// Scan engines alerts
    	String alert_str;
        int num_eng = this.aircraft.num_engines();
    	
        for (int i=0; i<num_eng; i++) {
        	if (this.aircraft.fuel_press_alert(i))	alert_str= "ENG "+i+" FUEL LO PR";
        	if (this.aircraft.oil_temp_alert(i))   	alert_str= "ENG "+i+" OIL HI TEMP";
        	if (this.aircraft.oil_press_alert(i))  	alert_str= "ENG "+i+" OIL LO PR";
        }
    	
        // display the first 8 lines of the pop list
        // display the first 8 lines of the pop list
        for (int i=0, line=0; i < MAX_EMSG_MSG && line < 8; i++) {
        	if (emsg_status[i]) { 
        			message_str = message_list[i];
        			g2.setColor(emsg_color[i]);
        			g2.drawString(message_str, eicas_gc.message_x, eicas_gc.memo_y + line * eicas_gc.line_height_l);          			
        			line++;
        	}
        	
        }
    } 

}
