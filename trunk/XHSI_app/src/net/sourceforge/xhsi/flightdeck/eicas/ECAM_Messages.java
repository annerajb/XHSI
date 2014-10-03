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
        	// Draw separation lines
        	DrawSeparationLines(g2);
        	if (this.avionics.is_qpac()) {
        		DrawQpacEcamMessages(g2);
        	} else if (this.avionics.is_jar_a320neo()) {
        		DrawJarA320neoEcamMessages(g2);
        	} else {
        		DrawXHSIEcamMessages(g2);
        	}
        		
        }
    }
    
    private void DrawSeparationLines(Graphics2D g2) {
    	
    }
    
    private void DrawQpacEcamMessages(Graphics2D g2) {
    	
    }
 
    private void DrawJarA320neoEcamMessages(Graphics2D g2) {
    	
    }
    
    private void DrawXHSIEcamMessages(Graphics2D g2) {
    	
    } 

}
