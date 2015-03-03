package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.model.ModelFactory;

public class ECAM_Messages extends EICASSubcomponent {

	public ECAM_Messages(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		// TODO Auto-generated constructor stub
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
        
    } 

}
