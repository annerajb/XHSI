package net.sourceforge.xhsi.flightdeck.eicas;


import java.awt.Component;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.ModelFactory;

public class ECAM_Fuel extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public ECAM_Fuel(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
    	if ( eicas_gc.airbus_style && eicas_gc.powered ) {        
            drawFuel(g2);
        }
    }


    private void drawFuel(Graphics2D g2) {
    	
    	// Airbus software v1 -> Fuel On Bord displayed on the right panel
    	// software v2 or nb_engines > 2, bellow the gauges
    
        boolean primaries = this.preferences.get_eicas_primary_only();
        boolean draw_ctrl = this.preferences.get_eicas_draw_controls();

        g2.setFont(eicas_gc.font_xl);

        String units_str;
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) units_str = XHSIPreferences.FUEL_UNITS_KG;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) units_str = XHSIPreferences.FUEL_UNITS_LBS;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) units_str = XHSIPreferences.FUEL_UNITS_USG;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ units_str = XHSIPreferences.FUEL_UNITS_LTR;

        if (primaries && !draw_ctrl) {
        // if (eicas_gc.ecam_version==1) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.drawString("FOB:", eicas_gc.fuel_primary_x[0], eicas_gc.fuel_primary_y[0]);

        	g2.setColor(eicas_gc.ecam_normal_color);
        	String qty_str = "" + Math.round( this.aircraft.get_total_fuel() * this.aircraft.fuel_multiplier() );
        	g2.drawString(qty_str, eicas_gc.fuel_primary_x[1] - eicas_gc.get_text_width(g2, eicas_gc.font_m, qty_str), eicas_gc.fuel_primary_y[1]);

        	g2.setFont(eicas_gc.font_l);
        	g2.setColor(eicas_gc.ecam_action_color);
        	g2.drawString(units_str, eicas_gc.fuel_primary_x[2], eicas_gc.fuel_primary_y[2]);
        } else {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.drawString("FOB:", eicas_gc.fuel_compact_x[0], eicas_gc.fuel_compact_y[0]);

        	g2.setColor(eicas_gc.ecam_normal_color);
        	String qty_str = "" + Math.round( this.aircraft.get_total_fuel() * this.aircraft.fuel_multiplier() );
        	g2.drawString(qty_str, eicas_gc.fuel_compact_x[1] - eicas_gc.get_text_width(g2, eicas_gc.font_m, qty_str), eicas_gc.fuel_compact_y[1]);

        	g2.setFont(eicas_gc.font_l);
        	g2.setColor(eicas_gc.ecam_action_color);
        	g2.drawString(units_str, eicas_gc.fuel_compact_x[2], eicas_gc.fuel_compact_y[2]);
        }
    }

}
