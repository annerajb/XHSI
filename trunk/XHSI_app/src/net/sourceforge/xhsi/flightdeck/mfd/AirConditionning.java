/**
* LowerEicas.java
* 
* Lower EICAS
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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.CabinZone;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;

public class AirConditionning extends MFDSubcomponent {
	
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	
	public AirConditionning(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}
	
	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_COND) {
			// Page ID
			drawPageID(g2, "COND");
			drawTempLegend(g2);
			drawCabin(g2);
			// TODO : Display only cargo with jarDesign A320 or Create preferences		
			drawCargoAft(g2);
			drawCargoForward(g2);
		}
	}

    
    private void drawPageID(Graphics2D g2, String page_str) {
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xxl);
    	int page_id_x = mfd_gc.cond_page_legend_x;
    	int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10;     	
        g2.drawString(page_str, page_id_x, page_id_y);
        g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_xl/8);
    }
 
    private void drawTempLegend(Graphics2D g2) {
    	String temp_str="TEMP:";
    	String temp_unit="°c";
    	int unit_legend_x = mfd_gc.cond_temp_legend_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, temp_str);
    	g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xl);
        g2.drawString(temp_str, mfd_gc.cond_temp_legend_x, mfd_gc.cond_temp_legend_y);
        g2.setColor(mfd_gc.ecam_action_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawString(temp_unit, unit_legend_x, mfd_gc.cond_temp_legend_y);

    	
    }
    
    private void drawCabin(Graphics2D g2) {
    	// Cabin body
    	g2.setColor(mfd_gc.ecam_markings_color.darker());
    	g2.drawLine(mfd_gc.cab_front_x, mfd_gc.cab_top_y, mfd_gc.cab_aft_x, mfd_gc.cab_top_y);
    	g2.drawLine(mfd_gc.cab_seg1_x1, mfd_gc.cab_bottom_y, mfd_gc.cab_seg1_x2, mfd_gc.cab_bottom_y);
    	g2.drawLine(mfd_gc.cab_seg2_x1, mfd_gc.cab_bottom_y, mfd_gc.cab_seg2_x2, mfd_gc.cab_bottom_y);
    	g2.drawLine(mfd_gc.cab_zone1_x, mfd_gc.cab_top_y, mfd_gc.cab_zone1_x, mfd_gc.cab_bottom_y);
    	g2.drawLine(mfd_gc.cab_zone2_x, mfd_gc.cab_zone2_y, mfd_gc.cab_zone2_x, mfd_gc.cab_bottom_y);
    	g2.drawArc(mfd_gc.cab_cockpit_x, mfd_gc.cab_top_y, (mfd_gc.cab_front_x - mfd_gc.cab_cockpit_x)*2, (mfd_gc.cab_cockpit_y - mfd_gc.cab_top_y)*2, 90, 90);
    	g2.drawArc(mfd_gc.cab_noose_x, mfd_gc.cab_cockpit_y, (mfd_gc.cab_front_x - mfd_gc.cab_noose_x )*2, (mfd_gc.cab_bottom_y - mfd_gc.cab_noose_y)*2, -90,-156);
    	g2.drawArc(mfd_gc.cab_aft_x-mfd_gc.cab_aft_dx, mfd_gc.cab_top_y, mfd_gc.cab_aft_dx*2, (mfd_gc.cab_bottom_y - mfd_gc.cab_top_y), -90, 45);
    	g2.drawArc(mfd_gc.cab_aft_x-mfd_gc.cab_aft_dx, mfd_gc.cab_top_y, mfd_gc.cab_aft_dx*2, (mfd_gc.cab_bottom_y - mfd_gc.cab_top_y), 45, 45);

    	g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xl);
    	String legend_str = "CKPT ";
        g2.drawString(legend_str, mfd_gc.cab_gauge1_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.cab_noose_y);
    	legend_str = "FWD ";
        g2.drawString(legend_str, mfd_gc.cab_gauge2_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.cab_noose_y);
    	legend_str = "AFT ";
        g2.drawString(legend_str, mfd_gc.cab_gauge3_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.cab_noose_y);

        // cabin air temperatures
        drawCabinTemp(g2, mfd_gc.cab_zone1_x, mfd_gc.cab_noose_y, this.aircraft.cabin_temp(CabinZone.COCKPIT));
        drawCabinTemp(g2, mfd_gc.cab_zone2_x, mfd_gc.cab_noose_y, this.aircraft.cabin_temp(CabinZone.FORWARD));
        drawCabinTemp(g2, mfd_gc.cab_aft_x + mfd_gc.line_height_xxl*14/8, mfd_gc.cab_noose_y, this.aircraft.cabin_temp(CabinZone.AFT));
        
        // inlet air temperatures (after trim valves)
        int cab_inlet_air_y =  mfd_gc.cab_bottom_y + mfd_gc.line_height_l /2;  
        drawInletTemp(g2, mfd_gc.cab_gauge1_x , cab_inlet_air_y, this.aircraft.cabin_inlet_temp(CabinZone.COCKPIT));
        drawInletTemp(g2, mfd_gc.cab_gauge2_x, cab_inlet_air_y, this.aircraft.cabin_inlet_temp(CabinZone.FORWARD));
        drawInletTemp(g2, mfd_gc.cab_gauge3_x, cab_inlet_air_y, this.aircraft.cabin_inlet_temp(CabinZone.AFT));
       
    	// Hot air circuit
        g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawLine(mfd_gc.cab_gauge1_x, mfd_gc.cab_hot_air_y, mfd_gc.cab_hot_air_valve_x-mfd_gc.cond_valve_r, mfd_gc.cab_hot_air_y);
    	g2.drawLine(mfd_gc.cab_hot_air_valve_x+mfd_gc.cond_valve_r, mfd_gc.cab_hot_air_y, mfd_gc.cab_hot_air_edge_x, mfd_gc.cab_hot_air_y);
    	g2.drawLine(mfd_gc.cab_gauge1_x, mfd_gc.cab_hot_air_y, mfd_gc.cab_gauge1_x, mfd_gc.cab_gauge_y);
    	g2.drawLine(mfd_gc.cab_gauge2_x, mfd_gc.cab_hot_air_y, mfd_gc.cab_gauge2_x, mfd_gc.cab_gauge_y);
    	g2.drawLine(mfd_gc.cab_gauge3_x, mfd_gc.cab_hot_air_y, mfd_gc.cab_gauge3_x, mfd_gc.cab_gauge_y);
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawString("HOT", mfd_gc.cab_hot_air_edge_x, mfd_gc.cab_hot_air_y-mfd_gc.line_height_l/6);
    	g2.drawString("AIR", mfd_gc.cab_hot_air_edge_x, mfd_gc.cab_hot_air_y+mfd_gc.line_height_l);
        
        // Hot air pressure regulation valve
        drawValveHoriz(g2, this.aircraft.cabin_hot_air_valve(CabinZone.COCKPIT), mfd_gc.cab_hot_air_valve_x, mfd_gc.cab_hot_air_y);

        // trim gauges
        drawGauge(g2, this.aircraft.cabin_hot_air_trim(CabinZone.COCKPIT), mfd_gc.cab_gauge1_x, mfd_gc.cab_gauge_y);
        drawGauge(g2, this.aircraft.cabin_hot_air_trim(CabinZone.FORWARD), mfd_gc.cab_gauge2_x, mfd_gc.cab_gauge_y);
        drawGauge(g2, this.aircraft.cabin_hot_air_trim(CabinZone.AFT), mfd_gc.cab_gauge3_x, mfd_gc.cab_gauge_y);

    }
    
    private void drawCabinTemp(Graphics2D g2, int x, int y, float temp) {        
        String value_str="XX";
        g2.setFont(mfd_gc.font_xxl);
        if (temp > -99.0f) {
            g2.setColor(mfd_gc.ecam_normal_color);
            value_str="" + Math.round(temp);
        } else {
            g2.setColor(mfd_gc.ecam_caution_color);
            value_str="XX";	
        }
        g2.drawString(value_str, x - mfd_gc.line_height_xxl/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, value_str), y);
    }
    
    private void drawInletTemp(Graphics2D g2, int x, int y, float temp) {        
        String value_str="XX";
        g2.setFont(mfd_gc.font_l);
        if (temp > -99.0f) {
            g2.setColor(mfd_gc.ecam_normal_color);
            value_str="" + Math.round(temp);
        } else {
            g2.setColor(mfd_gc.ecam_caution_color);
            value_str="XX";	
        }
        g2.drawString(value_str, x - mfd_gc.get_text_width(g2, mfd_gc.font_l, value_str)/2, y);
    }
    
    private void drawCargoAft(Graphics2D g2) {
    	// Cargo body
    	g2.setColor(mfd_gc.ecam_markings_color.darker());
    	g2.drawLine(mfd_gc.cargo_front_x, mfd_gc.cargo_top_y, mfd_gc.cargo_front_x, mfd_gc.cargo_bottom_y);
    	g2.drawLine(mfd_gc.cargo_front_x, mfd_gc.cargo_top_y, mfd_gc.cargo_aft_x, mfd_gc.cargo_top_y);
    	g2.drawLine(mfd_gc.cargo_front_x, mfd_gc.cargo_bottom_y, mfd_gc.cargo_x1, mfd_gc.cargo_bottom_y);
    	g2.drawLine(mfd_gc.cargo_x2, mfd_gc.cargo_bottom_y, mfd_gc.cargo_aft_x, mfd_gc.cargo_bottom_y);
    	g2.drawLine(mfd_gc.cargo_aft_x, mfd_gc.cargo_top_y, mfd_gc.cargo_aft_x, mfd_gc.cargo_y1);
    	g2.drawLine(mfd_gc.cargo_aft_x, mfd_gc.cargo_y2, mfd_gc.cargo_aft_x, mfd_gc.cargo_bottom_y);

    	// Hot air circuit
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawLine(mfd_gc.cargo_gauge_x, mfd_gc.cargo_hot_air_y, mfd_gc.cargo_hot_air_valve_x-mfd_gc.cond_valve_r, mfd_gc.cargo_hot_air_y);
    	g2.drawLine(mfd_gc.cargo_hot_air_valve_x+mfd_gc.cond_valve_r, mfd_gc.cargo_hot_air_y, mfd_gc.cargo_hot_air_aft_x, mfd_gc.cargo_hot_air_y);  
    	g2.drawLine(mfd_gc.cargo_gauge_x, mfd_gc.cargo_gauge_y - mfd_gc.cond_gauge_r, mfd_gc.cargo_gauge_x, mfd_gc.cargo_inlet_valve_y + mfd_gc.cond_valve_r);
    	g2.drawLine(mfd_gc.cargo_gauge_x, mfd_gc.cargo_inlet_top_y, mfd_gc.cargo_gauge_x, mfd_gc.cargo_inlet_valve_y - mfd_gc.cond_valve_r);
    	g2.drawLine(mfd_gc.cargo_gauge_x, mfd_gc.cargo_gauge_y, mfd_gc.cargo_gauge_x, mfd_gc.cargo_hot_air_y);
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawString("HOT", mfd_gc.cab_hot_air_edge_x, mfd_gc.cargo_hot_air_y-mfd_gc.line_height_l/6);
    	g2.drawString("AIR", mfd_gc.cab_hot_air_edge_x, mfd_gc.cargo_hot_air_y+mfd_gc.line_height_l);
    	
    	// inlet valve
    	drawValveVert(g2, ValveStatus.VALVE_OPEN, mfd_gc.cargo_gauge_x, mfd_gc.cargo_inlet_valve_y);
    	
    	// outlet valve : cargo_outlet_valve_y
    	drawValveHoriz(g2, ValveStatus.VALVE_OPEN, mfd_gc.cargo_aft_x, mfd_gc.cargo_outlet_valve_y);
    	
        // Hot air pressure regulation valve    	
    	drawValveHoriz(g2, this.aircraft.cabin_hot_air_valve(CabinZone.CARGO_AFT), mfd_gc.cargo_hot_air_valve_x, mfd_gc.cargo_hot_air_y);

        g2.setFont(mfd_gc.font_xl);
        g2.setColor(mfd_gc.ecam_markings_color);
    	String legend_str = "AFT ";
        g2.drawString(legend_str, mfd_gc.cargo_x1 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.cargo_temp_y);

        // cargo air temperature
        /*
        g2.setColor(mfd_gc.ecam_caution_color);
        String value_str="XX";
        g2.setFont(mfd_gc.font_xxl);
        g2.drawString(value_str, mfd_gc.cargo_aft_x - mfd_gc.cond_valve_r * 9/6 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, value_str), mfd_gc.cargo_temp_y);
        */
        drawCabinTemp(g2, mfd_gc.cargo_aft_x - mfd_gc.cond_valve_r * 9/6  , mfd_gc.cargo_temp_y, this.aircraft.cabin_temp(CabinZone.CARGO_AFT));
        
        // inlet air temperature
        // g2.setFont(mfd_gc.font_l);
        int cargo_inlet_air_y =  mfd_gc.cargo_bottom_y + mfd_gc.line_height_l /2;
        // g2.drawString(value_str, mfd_gc.cargo_gauge_x + mfd_gc.digit_width_l * 3 / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, value_str), cargo_inlet_air_y);
        drawInletTemp(g2, mfd_gc.cargo_gauge_x , cargo_inlet_air_y, this.aircraft.cabin_inlet_temp(CabinZone.CARGO_AFT));  
        
        // cargo trim gauge
        drawGauge(g2, this.aircraft.cabin_hot_air_trim(CabinZone.CARGO_AFT), mfd_gc.cargo_gauge_x, mfd_gc.cargo_gauge_y);        
        
    }

    private void drawCargoForward(Graphics2D g2) {
    	// Cargo body
    	g2.setColor(mfd_gc.ecam_markings_color.darker());
    	g2.drawLine(mfd_gc.cargo_fwd_front_x, mfd_gc.cargo_top_y, mfd_gc.cargo_fwd_front_x, mfd_gc.cargo_bottom_y);
    	g2.drawLine(mfd_gc.cargo_fwd_front_x, mfd_gc.cargo_top_y, mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_top_y);
    	g2.drawLine(mfd_gc.cargo_fwd_front_x, mfd_gc.cargo_bottom_y, mfd_gc.cargo_fwd_x1, mfd_gc.cargo_bottom_y);
    	g2.drawLine(mfd_gc.cargo_fwd_x2, mfd_gc.cargo_bottom_y, mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_bottom_y);
    	g2.drawLine(mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_top_y, mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_y1);
    	g2.drawLine(mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_y2, mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_bottom_y);

    	// Hot air circuit
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawLine(mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_hot_air_y, mfd_gc.cargo_fwd_hot_air_aft_x, mfd_gc.cargo_hot_air_y);
    	g2.drawLine(mfd_gc.cargo_fwd_hot_air_aft_x, mfd_gc.cab_hot_air_y, mfd_gc.cargo_fwd_hot_air_aft_x, mfd_gc.cargo_hot_air_y);  
    	g2.drawLine(mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_gauge_y - mfd_gc.cond_gauge_r, mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_inlet_valve_y + mfd_gc.cond_valve_r);
    	g2.drawLine(mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_inlet_top_y, mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_inlet_valve_y - mfd_gc.cond_valve_r);
    	g2.drawLine(mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_gauge_y, mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_hot_air_y);
    	
    	// inlet valve
    	drawValveVert(g2, ValveStatus.VALVE_OPEN, mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_inlet_valve_y);
    	
    	// outlet valve : cargo_outlet_valve_y
    	drawValveHoriz(g2, ValveStatus.VALVE_OPEN, mfd_gc.cargo_fwd_aft_x, mfd_gc.cargo_outlet_valve_y);
    	
        // Hot air pressure regulation valve    	
    	// drawValveHoriz(g2, this.aircraft.cabin_hot_air_valve(CabinZone.CARGO_FWD), mfd_gc.cargo_hot_air_valve_x, mfd_gc.cargo_hot_air_y);

        g2.setFont(mfd_gc.font_xl);
        g2.setColor(mfd_gc.ecam_markings_color);
    	String legend_str = "FWD";
        g2.drawString(legend_str, mfd_gc.cargo_fwd_x1 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.cargo_temp_y);

        // cargo air temperature
        /*
        g2.setColor(mfd_gc.ecam_caution_color);
        String value_str="XX";
        g2.setFont(mfd_gc.font_xxl);
        g2.drawString(value_str, mfd_gc.cargo_aft_x - mfd_gc.cond_valve_r * 9/6 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, value_str), mfd_gc.cargo_temp_y);
        */
        drawCabinTemp(g2, mfd_gc.cargo_fwd_aft_x - mfd_gc.cond_valve_r * 9/6  , mfd_gc.cargo_temp_y, this.aircraft.cabin_temp(CabinZone.CARGO_FWD));
        
        // inlet air temperature
        // g2.setFont(mfd_gc.font_l);
        int cargo_inlet_air_y =  mfd_gc.cargo_bottom_y + mfd_gc.line_height_l /2;
        // g2.drawString(value_str, mfd_gc.cargo_gauge_x + mfd_gc.digit_width_l * 3 / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, value_str), cargo_inlet_air_y);
        drawInletTemp(g2, mfd_gc.cargo_fwd_gauge_x , cargo_inlet_air_y, this.aircraft.cabin_inlet_temp(CabinZone.CARGO_FWD));  
        
        // cargo trim gauge
        drawGauge(g2, this.aircraft.cabin_hot_air_trim(CabinZone.CARGO_FWD), mfd_gc.cargo_fwd_gauge_x, mfd_gc.cargo_gauge_y);        
        
    }
    
    private void drawValveVert(Graphics2D g2, ValveStatus valve_sts, int x, int y) {
    	int r = mfd_gc.cond_valve_r;
    	
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_OPEN) {
            g2.setColor(mfd_gc.ecam_normal_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	}
        g2.drawOval(x-r,y-r,r*2,r*2);
        
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_CLOSED_FAILED) {
    		g2.drawLine(x-r, y, x+r, y);
    	} else {
    		g2.drawLine(x, y-r, x, y+r);
    	}
    }    

    private void drawValveHoriz(Graphics2D g2, ValveStatus valve_sts, int x, int y) {
    	int r = mfd_gc.cond_valve_r;
    	
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_OPEN) {
            g2.setColor(mfd_gc.ecam_normal_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	}
        g2.drawOval(x-r,y-r,r*2,r*2);
        
    	if (valve_sts == ValveStatus.VALVE_OPEN_FAILED || valve_sts == ValveStatus.VALVE_OPEN) {
    		g2.drawLine(x-r, y, x+r, y);
    	} else {
    		g2.drawLine(x, y-r, x, y+r);
    	}
    }    

    private void drawGauge(Graphics2D g2, float value, int x, int y) {
    	AffineTransform original_at = g2.getTransform();
    	int r = mfd_gc.cond_gauge_r;
    	int needle = Math.round(value*84)-42; 
    	g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawArc(x-r, y-r, r*2, r*2, 45, 90);
        g2.setFont(mfd_gc.font_l);
        g2.drawString("C",x-r*5/6-mfd_gc.digit_width_l,y-r/2);
        g2.drawString("H",x+r*5/6,y-r/2);

        // debug
        // g2.drawString(""+Math.round(value*1000)/1000.0f,x,y+mfd_gc.line_height_l);

        g2.rotate(Math.toRadians(needle), x, y);
        
        g2.setColor(mfd_gc.ecam_normal_color);
        int dx = r/12;
        int arrow_base = y-r*4/5;
        g2.drawLine(x, y, x, arrow_base);
        g2.drawLine(x, y-r, x+dx, arrow_base);
        g2.drawLine(x, y-r, x-dx, arrow_base);
        g2.drawLine(x-dx, arrow_base, x+dx, arrow_base);
        
        g2.setTransform(original_at);
        
    }
    
}
