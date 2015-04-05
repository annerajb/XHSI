/**
* LowerEicas.java
* 
* Lower EICAS and ECAM
* 
* Copyright (C) 2015  Nicolas Carel
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


package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class Fuel extends MFDSubcomponent {


	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private Stroke original_stroke;

	public Fuel(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_FUEL) {
			// Page ID
			drawPageID(g2, "FUEL");
			drawFuel(g2);
			drawFuelFlow(g2);
			drawTotalFuel(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, page_str)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}

	
    private void drawFuelFlow(Graphics2D g2) {        

        scalePen(g2);

        // convert FF from kg/s to kg/h, lbs/h, usg/h or ltr/h
        float unit_multiplier = this.aircraft.fuel_multiplier() * 60;
        float ff_value;
        float ff_total=0.0f;

        int ff_y = mfd_gc.fuel_flow_y;
        int ff_r = mfd_gc.dial_r[mfd_gc.num_eng] * 85 / 100;
        
        String units_str;
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) units_str = XHSIPreferences.FUEL_UNITS_KG;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) units_str = XHSIPreferences.FUEL_UNITS_LBS;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) units_str = XHSIPreferences.FUEL_UNITS_USG;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ units_str = XHSIPreferences.FUEL_UNITS_LTR;
        units_str += "/MN"; 

        // value box
        // when in 4 engine config, engine 1 & 2 right align
        // when in 2 engine config, engine 1 right align
        // if engine is off, display amber "XX"
        ff_y -= ff_r/8;

        g2.setFont(mfd_gc.fuel_eng_font);
        String ff_str;
        String eng_str="1";
                
        // Draw Fuel Flow for each engines
        for (int eng=0; eng<mfd_gc.num_eng; eng++) {
        	ff_value = this.aircraft.get_FF(eng) * unit_multiplier;
        	ff_total += ff_value;
        
        	if (ff_value < 0.2) {
        		// engine is off ? get N2 Value ?
        		ff_str = "XX";
        		g2.setColor(mfd_gc.ecam_caution_color);
        	} else {
        		ff_str = Integer.toString( Math.round(ff_value) );
        		g2.setColor(mfd_gc.ecam_normal_color);
        	}
        	g2.drawString(ff_str,
        			mfd_gc.dial_x[eng] + mfd_gc.fuel_eng_font_w*20/10 - mfd_gc.get_text_width(g2, mfd_gc.fuel_eng_font, ff_str),
        			ff_y
        			);
        	g2.setColor(mfd_gc.ecam_action_color);
        	g2.drawString(units_str,
        			mfd_gc.dial_x[eng] + mfd_gc.fuel_eng_font_w*20/10 - mfd_gc.get_text_width(g2, mfd_gc.fuel_eng_font, units_str),
        			ff_y-mfd_gc.fuel_eng_font_h
        			);
        	g2.setColor(mfd_gc.ecam_markings_color);
        	g2.drawString("ENG."+eng,
        			mfd_gc.dial_x[eng] + mfd_gc.fuel_eng_font_w*20/10 - mfd_gc.get_text_width(g2, mfd_gc.fuel_eng_font, units_str),
        			ff_y-mfd_gc.fuel_eng_font_h*2
        			);
        	if (eng>0) { eng_str += "+" + (eng+1); }
        }
        eng_str += ":";
        String total_str = "" + Math.round(ff_total);
        g2.drawString("F.FLOW",  mfd_gc.fuel_total_x, mfd_gc.fuel_total_y - mfd_gc.line_height_l*3 );
    	g2.drawString(eng_str, mfd_gc.fuel_total_x, mfd_gc.fuel_total_y - mfd_gc.line_height_l*2 );
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(total_str, mfd_gc.fuel_total_x + mfd_gc.get_text_width(g2, mfd_gc.fuel_eng_font, eng_str), mfd_gc.fuel_total_y - mfd_gc.line_height_l*2 );
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.setFont(mfd_gc.font_m);
    	g2.drawString(units_str, mfd_gc.fuel_total_x + mfd_gc.get_text_width(g2, mfd_gc.fuel_eng_font, eng_str) + mfd_gc.fuel_eng_font_w*3, mfd_gc.fuel_total_y - mfd_gc.line_height_l*2 );
    	
    	
    	
        
        resetPen(g2);
        
    	
    }

    private void drawTotalFuel(Graphics2D g2) {
    	
    	// Airbus software v1 -> Fuel On Bord displayed on the right panel
    	// software v2 or nb_engines > 2, bellow the gauges
 
        g2.setFont(mfd_gc.font_xl);
        int x1 = mfd_gc.fuel_total_x + mfd_gc.digit_width_l * 12 ;
        int x2 = mfd_gc.fuel_total_x + mfd_gc.digit_width_l * 13 ;

        String units_str;
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) units_str = XHSIPreferences.FUEL_UNITS_KG;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) units_str = XHSIPreferences.FUEL_UNITS_LBS;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) units_str = XHSIPreferences.FUEL_UNITS_USG;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ units_str = XHSIPreferences.FUEL_UNITS_LTR;

        g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawString("FOB:", mfd_gc.fuel_total_x, mfd_gc.fuel_total_y);
        g2.drawRect(mfd_gc.fuel_total_x - mfd_gc.digit_width_l / 3 , mfd_gc.fuel_total_y - mfd_gc.line_height_l * 11/10 , mfd_gc.digit_width_l * 17, mfd_gc.line_height_l * 13/10);

        g2.setColor(mfd_gc.ecam_normal_color);
        String qty_str = "" + Math.round( this.aircraft.get_total_fuel() * this.aircraft.fuel_multiplier() );
        g2.drawString(qty_str, x1 - mfd_gc.get_text_width(g2, mfd_gc.font_m, qty_str), mfd_gc.fuel_total_y);

        g2.setFont(mfd_gc.font_l);
        g2.setColor(mfd_gc.ecam_action_color);
        g2.drawString(units_str, x2, mfd_gc.fuel_total_y);

    }

    
    private void drawFuel(Graphics2D g2) {

        int tanks = this.aircraft.num_tanks();

        //tanks = 3;
        if ( tanks == 3 ) {
            drawGauge(g2, 0, "CTR", this.aircraft.get_fuel(1), this.aircraft.get_tank_capacity(1));
            drawGauge(g2, 1, "1", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0));
            drawGauge(g2, 2, "2", this.aircraft.get_fuel(2), this.aircraft.get_tank_capacity(2));
        } else if ( tanks == 5 ) {
            drawGauge(g2, 0, "CTR", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0));
            drawGauge(g2, 1, "L1", this.aircraft.get_fuel(1), this.aircraft.get_tank_capacity(1));
            drawGauge(g2, 2, "R1", this.aircraft.get_fuel(2), this.aircraft.get_tank_capacity(2));
            drawGauge(g2, 3, "L2", this.aircraft.get_fuel(3), this.aircraft.get_tank_capacity(3));
            drawGauge(g2, 4, "R2", this.aircraft.get_fuel(4), this.aircraft.get_tank_capacity(4));
        } else if ( tanks == 4 ) {            
            drawGauge(g2, 1, "1", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0));
            drawGauge(g2, 2, "2", this.aircraft.get_fuel(1), this.aircraft.get_tank_capacity(1));
            drawGauge(g2, 3, "3", this.aircraft.get_fuel(2), this.aircraft.get_tank_capacity(2));
            drawGauge(g2, 4, "4", this.aircraft.get_fuel(3), this.aircraft.get_tank_capacity(3));
        } else if ( tanks == 2 ) {
            drawGauge(g2, 1, "1", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0));
            drawGauge(g2, 2, "2", this.aircraft.get_fuel(1), this.aircraft.get_tank_capacity(1));
        } else if ( tanks == 1 ) {
            drawGauge(g2, 0, "", this.aircraft.get_fuel(0), this.aircraft.get_tank_capacity(0));
        } else if ( tanks > 3 ) {
            drawGauge(g2, 0, "ALL", this.aircraft.get_total_fuel(), this.aircraft.get_fuel_capacity());
        }
        if ( tanks > 1 ) {
            g2.setColor(mfd_gc.color_boeingcyan);
            g2.setFont(mfd_gc.font_m);
//            String units_str = XHSISettings.get_instance().fuel_units.get_units();
//            String units_str = this.preferences.get_preference(XHSIPreferences.PREF_FUEL_UNITS);
            String units_str;
            if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) units_str = XHSIPreferences.FUEL_UNITS_KG;
            else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) units_str = XHSIPreferences.FUEL_UNITS_LBS;
            else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) units_str = XHSIPreferences.FUEL_UNITS_USG;
            else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ units_str = XHSIPreferences.FUEL_UNITS_LTR;

            g2.drawString("FUEL", mfd_gc.fuel_primary_x[0] - mfd_gc.get_text_width(g2, mfd_gc.font_m, "FUEL")/2, mfd_gc.fuel_primary_y[0] + mfd_gc.fuel_r - 2);
            g2.drawString(units_str, mfd_gc.fuel_primary_x[0] - mfd_gc.get_text_width(g2, mfd_gc.font_m, units_str)/2, mfd_gc.fuel_primary_y[0] + mfd_gc.fuel_r + mfd_gc.line_height_m - 2);
            
        }

    }


    private void drawGauge(Graphics2D g2, int tank, String tank_str, float quantity, float range) {
//quantity = 1750.0f;
//range = 2000.0f;

        int fuel_x = mfd_gc.fuel_primary_x[tank];
        int fuel_y = mfd_gc.fuel_primary_y[tank];

        g2.setColor(mfd_gc.dim_markings_color);
        AffineTransform original_at = g2.getTransform();
        g2.rotate(Math.toRadians(-225.0f), fuel_x, fuel_y);
        for (int i=0; i<=12; i++) {
            g2.drawLine(fuel_x + mfd_gc.fuel_r*13/16, fuel_y,
                    fuel_x + mfd_gc.fuel_r*1015/1000, fuel_y);
            g2.rotate(Math.toRadians(22.5f), fuel_x, fuel_y);
        }
        g2.setTransform(original_at);

        if ( this.aircraft.low_fuel() ) {
            g2.setColor(mfd_gc.caution_color);
        } else {
            g2.setColor(mfd_gc.markings_color);
        }
        Stroke orininal_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(6.0f * mfd_gc.scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.drawArc(fuel_x - mfd_gc.fuel_r, fuel_y - mfd_gc.fuel_r, mfd_gc.fuel_r*2, mfd_gc.fuel_r*2,
                -135, Math.round(-270*quantity/range));
        g2.setStroke(orininal_stroke);

//        String qty_str = "" + Math.round( quantity * XHSISettings.get_instance().fuel_units.get_multiplier() );
        String qty_str = "" + Math.round( quantity * this.aircraft.fuel_multiplier() );
        g2.setFont(mfd_gc.font_xl);
        g2.drawString(qty_str, fuel_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, qty_str)/2, fuel_y + mfd_gc.line_height_xl/2 - 2);

        g2.setColor(mfd_gc.color_boeingcyan);
        g2.setFont(mfd_gc.font_m);
        g2.drawString(tank_str, fuel_x - mfd_gc.get_text_width(g2, mfd_gc.font_m, tank_str)/2, fuel_y - mfd_gc.fuel_r*3/8 - 2);

    }
    
    private void scalePen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }
	
}