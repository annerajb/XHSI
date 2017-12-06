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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;

public class CabinPressure extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private Stroke original_stroke;
    
    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;
    
    enum PressAlert { NORMAL, PULSING };
    private PressAlert vs_alert_status;
    private PressAlert alt_alert_status;
    private PressAlert delta_p_alert_status;

    // This Stroke is used by the double lined value boxes
	private class CompositeStroke implements Stroke {
		private Stroke stroke1, stroke2;

		public CompositeStroke( Stroke stroke1, Stroke stroke2 ) {
			this.stroke1 = stroke1;
			this.stroke2 = stroke2;
		}

		public Shape createStrokedShape( Shape shape ) {
			return stroke2.createStrokedShape( stroke1.createStrokedShape( shape ) );
		}
	}
	
	public CabinPressure(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
        one_decimal_format = new DecimalFormat("##0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);

        two_decimals_format = new DecimalFormat("#0.00");
        format_symbols = two_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_format.setDecimalFormatSymbols(format_symbols);
        
        vs_alert_status = PressAlert.NORMAL;
        delta_p_alert_status = PressAlert.NORMAL;
        alt_alert_status = PressAlert.NORMAL;

	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_CAB_PRESS) {
			// Page ID
			drawPageID(g2, "CAB PRESS");
			// drawPressures(g2);
			drawLDGElevation(g2);
			drawVS(g2);
			drawCabinAlt(g2);
			drawDeltaP(g2);
			drawInletValve(g2,this.aircraft.cabin_inlet_valve());
			drawExtractValve(g2,this.aircraft.cabin_extract_valve());
			drawSafetyValve(g2,this.aircraft.cabin_safety_valve());
			drawOutflowValve(g2,this.aircraft.cabin_outflow_valve());
			drawCabin(g2);
			drawVentFlags(g2);
			
			boolean pack1_flow_status = (aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK1) == ValveStatus.VALVE_OPEN && this.aircraft.bleed_air_press(Aircraft.BLEED_LEFT) > 10);  
			boolean pack2_flow_status =	(aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK2) == ValveStatus.VALVE_OPEN && this.aircraft.bleed_air_press(Aircraft.BLEED_RIGHT) > 10); 
			drawPackStatus(g2,1,pack1_flow_status);
			drawPackStatus(g2,2,pack2_flow_status);
			
			drawSystemStatus(g2,this.aircraft.cabin_vs_man_mode() ? 0 : 1);
			
			// Legends
			String str_unit = "PSI";
			String str_legend = "ΔP";
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.setFont(mfd_gc.font_l);
			g2.drawString(str_legend, mfd_gc.press_delta_p_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_legend)/2 , mfd_gc.press_gauges_legend_y);
			g2.setColor(mfd_gc.ecam_action_color);
			g2.setFont(mfd_gc.font_m);
			g2.drawString(str_unit, mfd_gc.press_delta_p_x - mfd_gc.get_text_width(g2, mfd_gc.font_m, str_unit)/2 , mfd_gc.press_gauges_legend_y + mfd_gc.line_height_xl);
	
			// Legends
			str_unit = "FT/MN";
			str_legend = "V/S";
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.setFont(mfd_gc.font_l);
			g2.drawString(str_legend, mfd_gc.press_vs_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_legend)/2 , mfd_gc.press_gauges_legend_y);
			g2.setColor(mfd_gc.ecam_action_color);
			g2.setFont(mfd_gc.font_m);
			g2.drawString(str_unit, mfd_gc.press_vs_x - mfd_gc.get_text_width(g2, mfd_gc.font_m, str_unit)/2 , mfd_gc.press_gauges_legend_y + mfd_gc.line_height_xl);

			// Legends
			str_unit = "FT";
			str_legend = "CAB ALT";
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.setFont(mfd_gc.font_l);
			g2.drawString(str_legend, mfd_gc.press_cab_alt_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_legend)/2 , mfd_gc.press_gauges_legend_y);
			g2.setColor(mfd_gc.ecam_action_color);
			g2.setFont(mfd_gc.font_m);
			g2.drawString(str_unit, mfd_gc.press_cab_alt_x - mfd_gc.get_text_width(g2, mfd_gc.font_m, str_unit)/2 , mfd_gc.press_gauges_legend_y + mfd_gc.line_height_xl);

			
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.press_id_x;
		int page_id_y = mfd_gc.press_id_y;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_xl/8);
	}
	
	private void drawPackStatus(Graphics2D g2, int pack, boolean opened) {
		int pack_x = (pack == 1) ? mfd_gc.press_pack1_x : mfd_gc.press_pack2_x;
		String pack_str = (pack == 1) ? "PACK  1" : "PACK  2";
		int tri_x[] = { pack_x,pack_x-mfd_gc.press_pack_arrow_dx/2,pack_x+mfd_gc.press_pack_arrow_dx/2 };
		int tri_y[] = { mfd_gc.press_pack_arrow_y,mfd_gc.press_pack_arrow_y + mfd_gc.press_pack_arrow_dy ,mfd_gc.press_pack_arrow_y + mfd_gc.press_pack_arrow_dy };
		g2.setFont(mfd_gc.font_xl);
		if (opened) g2.setColor(mfd_gc.ecam_markings_color); else g2.setColor(mfd_gc.ecam_caution_color);
		g2.drawString(pack_str, pack_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, pack_str)/2 , mfd_gc.press_pack_legend_y);		
		if (opened) g2.setColor(mfd_gc.ecam_normal_color); else g2.setColor(mfd_gc.ecam_caution_color);
		g2.drawPolygon(tri_x, tri_y, 3);		
	}
	
	private void drawSystemStatus(Graphics2D g2, int sys_status) {
		g2.setFont(mfd_gc.font_xl);
		String str_sys1 = "SYS1";	
		String str_sys2 = "SYS2";
		String str_man = "MAN";
		g2.setColor(mfd_gc.ecam_normal_color);
		switch (sys_status) {
		case 1: g2.drawString(str_sys1, mfd_gc.press_sys1_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_sys1)/2 , mfd_gc.press_sys_legend_y);
			break;
		case 2: g2.drawString(str_sys2, mfd_gc.press_sys2_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_sys2)/2 , mfd_gc.press_sys_legend_y);
			break;
		default:
			g2.drawString(str_man, mfd_gc.press_man_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_man)/2 , mfd_gc.press_man_legend_y);
			break;
		}
	}
	
	
	private void drawVentFlags(Graphics2D g2) {
		/*
		 * A320 FCOM 1.21.30 p11
		 * VENT appears in white. Amber if BLOWER FAULT, EXTRACT FAULT or AVNCS SYS FAULT
		 * INLET appears in white. Amber if BLOWER FAULT
		 * EXTRACT appears in white. Amber if EXTRACT FAULT
		 */
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		String str_inlet = "INLET";
		String str_extract = "EXTRACT";
		String str_vent = "VENT";
		g2.drawString(str_inlet, mfd_gc.press_inlet_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_inlet)/2 , mfd_gc.press_inlet_legend_y);
		g2.drawString(str_extract, mfd_gc.press_extract_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_extract)/2 , mfd_gc.press_inlet_legend_y);
		g2.drawString(str_vent, mfd_gc.press_vent_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_vent)/2 , mfd_gc.press_vent_legend_y);
	}
	
	private void drawCabin(Graphics2D g2) {
		AffineTransform original_at = g2.getTransform();
		g2.setColor(mfd_gc.ecam_markings_color);
		scalePen(g2,1.2f);
		g2.drawLine(mfd_gc.press_box_left_x, mfd_gc.press_box_top_y, mfd_gc.press_box_left_x, mfd_gc.press_box_bottom_y);
		g2.drawLine(mfd_gc.press_box_left_x, mfd_gc.press_box_top_y, mfd_gc.press_box_right_x, mfd_gc.press_box_top_y);
		g2.drawLine(mfd_gc.press_box_left_x, mfd_gc.press_box_bottom_y, mfd_gc.press_box_inlet_x, mfd_gc.press_box_bottom_y);
		g2.drawLine(mfd_gc.press_box_inlet_x, mfd_gc.press_box_bottom_y, mfd_gc.press_box_inlet_x, mfd_gc.press_box_inlet_y );
		g2.drawLine(mfd_gc.press_box_inlet_x + mfd_gc.press_box_inlet_dx, mfd_gc.press_box_bottom_y, mfd_gc.press_box_inlet_x+ mfd_gc.press_box_inlet_dx, mfd_gc.press_box_inlet_y );
		g2.drawLine(mfd_gc.press_box_inlet_x + mfd_gc.press_box_inlet_dx, mfd_gc.press_box_bottom_y, mfd_gc.press_box_extract_x, mfd_gc.press_box_bottom_y);
		g2.drawLine(mfd_gc.press_box_extract_x, mfd_gc.press_box_bottom_y, mfd_gc.press_box_extract_x, mfd_gc.press_box_inlet_y);
		g2.drawLine(mfd_gc.press_box_extract_x + mfd_gc.press_box_inlet_dx, mfd_gc.press_box_bottom_y, mfd_gc.press_box_extract_x + mfd_gc.press_box_inlet_dx, mfd_gc.press_box_inlet_y);
		g2.drawLine(mfd_gc.press_box_extract_x  + mfd_gc.press_box_inlet_dx, mfd_gc.press_box_bottom_y, mfd_gc.press_box_outflow_left, mfd_gc.press_box_bottom_y);
		g2.drawLine(mfd_gc.press_box_outflow_right, mfd_gc.press_box_bottom_y, mfd_gc.press_box_right_x, mfd_gc.press_box_bottom_y);
		g2.drawLine(mfd_gc.press_box_right_x, mfd_gc.press_box_top_y, mfd_gc.press_box_right_x, mfd_gc.press_safety_legend_y - mfd_gc.line_height_l);
		g2.drawLine(mfd_gc.press_box_right_x, mfd_gc.press_safety_legend_y, mfd_gc.press_box_right_x, mfd_gc.press_box_safety_top);
		g2.drawLine(mfd_gc.press_box_right_x, mfd_gc.press_box_safety_top, mfd_gc.press_box_safety_x, mfd_gc.press_box_safety_top);
		g2.drawLine(mfd_gc.press_box_right_x, mfd_gc.press_box_safety_bottom, mfd_gc.press_box_safety_x, mfd_gc.press_box_safety_bottom);
		g2.drawLine(mfd_gc.press_box_right_x, mfd_gc.press_box_safety_bottom, mfd_gc.press_box_right_x, mfd_gc.press_box_bottom_y);
		
		// Outflow valve arc
		g2.drawArc(mfd_gc.press_box_outflow_left, mfd_gc.press_box_bottom_y - mfd_gc.press_box_outflow_r, mfd_gc.press_box_outflow_r*2, mfd_gc.press_box_outflow_r*2, 90, 90);	      
        // scale markings every 25%
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.rotate(Math.toRadians(180), mfd_gc.press_box_outflow_right, mfd_gc.press_box_bottom_y);
        for (int i=0; i<4; i++) {
        	g2.drawLine(mfd_gc.press_box_outflow_right+mfd_gc.press_box_outflow_r, mfd_gc.press_box_bottom_y, mfd_gc.press_box_outflow_right+mfd_gc.press_box_outflow_r*23/20, mfd_gc.press_box_bottom_y);
        	g2.rotate(Math.toRadians(90/4), mfd_gc.press_box_outflow_right, mfd_gc.press_box_bottom_y);
        }
        g2.fillRect(mfd_gc.press_box_outflow_right+mfd_gc.press_box_outflow_r, mfd_gc.press_box_bottom_y, mfd_gc.press_box_outflow_r*3/20, mfd_gc.press_box_outflow_r*2/20);
        g2.setTransform(original_at);

		
		g2.drawOval(mfd_gc.press_box_inlet_x + mfd_gc.press_box_inlet_dx - mfd_gc.press_valve_bullet_r, mfd_gc.press_box_inlet_y  - mfd_gc.press_valve_bullet_r, 
				  mfd_gc.press_valve_bullet_r * 2, mfd_gc.press_valve_bullet_r * 2);
		g2.drawOval(mfd_gc.press_box_extract_x - mfd_gc.press_valve_bullet_r, mfd_gc.press_box_inlet_y  - mfd_gc.press_valve_bullet_r, 
				  mfd_gc.press_valve_bullet_r * 2, mfd_gc.press_valve_bullet_r * 2);
		g2.drawOval(mfd_gc.press_box_outflow_right- mfd_gc.press_valve_bullet_r, mfd_gc.press_box_bottom_y  - mfd_gc.press_valve_bullet_r, 
				  mfd_gc.press_valve_bullet_r * 2, mfd_gc.press_valve_bullet_r * 2);
		g2.drawOval(mfd_gc.press_box_safety_x- mfd_gc.press_valve_bullet_r, mfd_gc.press_box_safety_top  - mfd_gc.press_valve_bullet_r, 
				  mfd_gc.press_valve_bullet_r * 2, mfd_gc.press_valve_bullet_r * 2);
		resetPen(g2);
	}
	
	private void drawInletValve(Graphics2D g2, float ratio) {
		AffineTransform original_at = g2.getTransform();
		g2.setColor(mfd_gc.ecam_normal_color);
		int valve_x = mfd_gc.press_box_inlet_x + mfd_gc.press_box_inlet_dx;
		int valve_y =  mfd_gc.press_box_inlet_y;
      
        g2.rotate(Math.toRadians(-180-Math.round(ratio*90)), valve_x, valve_y);
        /* 
         * A320 FCOM 1.21.30 p11
         * green if fully open or close, amber in transit
         * XX amber is failed in transit
         */
        if (ratio < 0.05f || ratio > 0.95f )
        	g2.setColor(mfd_gc.ecam_normal_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawLine(valve_x , valve_y, valve_x+mfd_gc.press_box_inlet_dx*9/10, valve_y);
        g2.setTransform(original_at);
		
	}
	
	private void drawExtractValve(Graphics2D g2, float ratio) {
		AffineTransform original_at = g2.getTransform();
		g2.setColor(mfd_gc.ecam_normal_color);
		int valve_x = mfd_gc.press_box_extract_x;
		int valve_y =  mfd_gc.press_box_inlet_y;
      
        g2.rotate(Math.toRadians(Math.round(ratio*90)), valve_x, valve_y);
        /* 
         * A320 FCOM 1.21.30 p11
         * green if fully open or close, amber in transit
         * XX amber is failed in transit
         */
        if (ratio < 0.05f || ratio > 0.95f )
        	g2.setColor(mfd_gc.ecam_normal_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawLine(valve_x , valve_y, valve_x+mfd_gc.press_box_inlet_dx*9/10, valve_y);
        g2.setTransform(original_at);
		
	}
	
	private void drawSafetyValve(Graphics2D g2, float ratio) {
		AffineTransform original_at = g2.getTransform();
		g2.setColor(mfd_gc.ecam_normal_color);
		int valve_x = mfd_gc.press_box_safety_x;
		int valve_y =  mfd_gc.press_box_safety_top;
      
        g2.rotate(Math.toRadians(90-Math.round(ratio*90)), valve_x, valve_y);
        /* 
         * A320 FCOM 1.21.20 p11
         * green if fully open or close, amber in transit
         * XX amber is failed in transit
         */
        if (ratio < 0.05f)
        	g2.setColor(mfd_gc.ecam_normal_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawLine(valve_x , valve_y, valve_x+mfd_gc.press_box_inlet_dx*9/10, valve_y);
        g2.setTransform(original_at);
		
        String str_safety = "SAFETY";
        g2.setFont(mfd_gc.font_xl);
        if (ratio < 0.05f)
        	g2.setColor(mfd_gc.ecam_markings_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawString(str_safety, mfd_gc.press_safety_legend_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_safety)/2 , mfd_gc.press_safety_legend_y);
	}
	
	private void drawOutflowValve(Graphics2D g2, float ratio) {
		AffineTransform original_at = g2.getTransform();
		g2.setColor(mfd_gc.ecam_normal_color);
		int valve_x = mfd_gc.press_box_outflow_right;
		int valve_y =  mfd_gc.press_box_bottom_y;
		
        g2.rotate(Math.toRadians(180+Math.round(ratio*90)), valve_x, valve_y);
        /* 
         * A320 FCOM 1.21.20 p11
         * green if fully open or close, amber if in flight and ratio > 0.95
         * 
         */
        if ( ratio > 0.95f )
        	g2.setColor(mfd_gc.ecam_caution_color);
        else
        	g2.setColor(mfd_gc.ecam_normal_color);
        g2.drawLine(valve_x , valve_y, valve_x+mfd_gc.press_box_outflow_r*95/100, valve_y);
        g2.setTransform(original_at);
		
	}
	
	private void drawPressures(Graphics2D g2) {
		String str_alt = "" + Math.round(aircraft.cabin_altitude()/10)*10;
		String str_delta = one_decimal_format.format(aircraft.cabin_delta_p());
		String str_vs = "" + Math.round(aircraft.cabin_vs());
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);

		g2.drawString("Cabin Altitude:", mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + mfd_gc.line_height_xl*4);
		g2.drawString("Delta Pressure:", mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + mfd_gc.line_height_xl*5);
		g2.drawString("Vertical Speed:", mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + mfd_gc.line_height_xl*6);

		g2.setColor(mfd_gc.ecam_normal_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_alt, mfd_gc.cab_zone1_x, mfd_gc.panel_rect.y + mfd_gc.line_height_xl*4);
		g2.drawString(str_delta, mfd_gc.cab_zone1_x, mfd_gc.panel_rect.y + mfd_gc.line_height_xl*5);
		g2.drawString(str_vs, mfd_gc.cab_zone1_x, mfd_gc.panel_rect.y + mfd_gc.line_height_xl*6);

		
	}
	
    private void drawVS(Graphics2D g2) {
    	 
    	/*
    	 *  A320 FCOM 1.21.20 p10
    	 *  Cabin Vertical Speed
    	 *  Normal range : -2000 to 2000 - needle and value in green
    	 *  Needle and value in amber when V/S >= 2000 feet/minute
    	 *  Value pulse when V/S > 1800 feet/minute, reset at 1600 feet/minute
    	 */

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);
        int vs_max = 2000;
        int vs_min = -2000;
        
        float vs_range = vs_max-vs_min;
        int vs_value = Math.round(aircraft.cabin_vs());	
        float dial_value = Math.min(aircraft.cabin_vs(), vs_max);
        dial_value = Math.max(dial_value, vs_min);
        float vs_dial = dial_value/vs_range + 0.5f;
        
        int vs_x = mfd_gc.press_vs_x;
        int vs_y = mfd_gc.press_gauges_y;
        int vs_r = mfd_gc.press_dial_r;
        
        int deg_start = -90;
        int deg_full_range = 180;
        int deg_norm_range = 180;
        int deg_caution = 7;
        int deg_gauge_end = 90;
        
        // V/S Gauge Arc
        g2.setColor(mfd_gc.ecam_markings_color);  
        g2.drawArc(vs_x-vs_r, vs_y-vs_r, 2*vs_r, 2*vs_r, deg_start-deg_caution, -(deg_norm_range-deg_caution*2));   
        // Min and Max amber arcs
        original_stroke = g2.getStroke();
        g2.setColor(mfd_gc.ecam_caution_color);
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * mfd_gc.grow_scaling_factor ), new BasicStroke( 2.0f * mfd_gc.grow_scaling_factor ) ));
        int vs_r_amber = vs_r * 98/100; 
        g2.drawArc(vs_x-vs_r_amber, vs_y-vs_r_amber, 2*vs_r_amber, 2*vs_r_amber, deg_start, 1-deg_caution);
        g2.drawArc(vs_x-vs_r_amber, vs_y-vs_r_amber, 2*vs_r_amber, 2*vs_r_amber, deg_gauge_end, deg_caution-1);
        g2.setTransform(original_at);
        g2.setStroke(original_stroke);
        
        // V/S arc arrows        
        int arrow_dx = vs_r / 10;
        int arrow_dy = vs_r / 10;
        /*
        g2.drawLine(vs_x, vs_y - vs_r, vs_x - arrow_dx, vs_y - vs_r - arrow_dy);
        g2.drawLine(vs_x, vs_y - vs_r, vs_x - arrow_dx, vs_y - vs_r + arrow_dy);
        g2.drawLine(vs_x, vs_y + vs_r, vs_x - arrow_dx, vs_y + vs_r - arrow_dy);
        g2.drawLine(vs_x, vs_y + vs_r, vs_x - arrow_dx, vs_y + vs_r + arrow_dy);
        */
        // UP and DN marks
        g2.setFont(mfd_gc.font_l);
        g2.setColor(mfd_gc.ecam_markings_color);  
        g2.drawString("UP", vs_x + arrow_dx*2, vs_y - vs_r + mfd_gc.line_height_l);
        g2.drawString("DN", vs_x + arrow_dx*2, vs_y + vs_r );

        
        // scale markings every 25%
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.rotate(Math.toRadians(360-deg_start), vs_x, vs_y);
        g2.rotate(Math.toRadians(deg_full_range/4), vs_x, vs_y);
        for (int i=0; i<3; i++) {
        	g2.drawLine(vs_x+vs_r*18/20, vs_y, vs_x+vs_r-1, vs_y);
        	g2.rotate(Math.toRadians(deg_full_range/4), vs_x, vs_y);
        }
        g2.setTransform(original_at);


        // V/S : scale number
        String mid_str = "0";
        String max_str = "2";
        g2.setFont(mfd_gc.font_s);
        g2.setColor(mfd_gc.ecam_markings_color);
        int vs_digit_x;
        int vs_digit_y;
        int vs_digit_angle = deg_start - deg_full_range;
        // max positive
        vs_digit_x = vs_x + (int)(Math.cos(Math.toRadians(vs_digit_angle))*vs_r*12/16);
        vs_digit_y = vs_y + (int)(Math.sin(Math.toRadians(vs_digit_angle))*vs_r*12/16);
        g2.drawString(max_str, vs_digit_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, max_str)/2, vs_digit_y+mfd_gc.line_height_s*3/8);
        vs_digit_angle += deg_full_range/2;
        // middle
        vs_digit_x = vs_x + (int)(Math.cos(Math.toRadians(vs_digit_angle))*vs_r*11/16);
        vs_digit_y = vs_y + (int)(Math.sin(Math.toRadians(vs_digit_angle))*vs_r*11/16);
        g2.drawString(mid_str, vs_digit_x - mfd_gc.digit_width_s/2, vs_digit_y+mfd_gc.line_height_s*3/8);
        vs_digit_angle += deg_full_range/2;
        // max negative
        vs_digit_x = vs_x + (int)(Math.cos(Math.toRadians(vs_digit_angle))*vs_r*12/16);
        vs_digit_y = vs_y + (int)(Math.sin(Math.toRadians(vs_digit_angle))*vs_r*12/16);
        g2.drawString(max_str, vs_digit_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, max_str)/2, vs_digit_y+mfd_gc.line_height_s*3/8);

        
        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);        
        g2.rotate(Math.toRadians(Math.round(vs_dial*deg_full_range)-deg_start), vs_x, vs_y);
        if (Math.abs(vs_value) < vs_max )
        	g2.setColor(mfd_gc.ecam_normal_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawLine(vs_x , vs_y, vs_x+vs_r*11/10, vs_y);
        g2.setTransform(original_at);
        
            
        // Value
        if ( Math.abs(vs_value) > 1800 ) { 
        	vs_alert_status = PressAlert.PULSING;
        } else if (Math.abs(vs_value) <= 1600) {
        	vs_alert_status = PressAlert.NORMAL;
        }
    	switch (vs_alert_status) {
    	case NORMAL : 
    		g2.setColor(mfd_gc.ecam_normal_color);
    		break;
    	case PULSING : 
    		if ((mfd_gc.current_time_millis % 1000) < 500) 
    			g2.setColor(mfd_gc.ecam_caution_color); 
    		else 
    			g2.setColor(mfd_gc.ecam_caution_color.darker());
    		break;
    	}
    	/*
        if ( Math.abs(vs_value) < vs_max ) {
        	g2.setColor(mfd_gc.ecam_normal_color);
        } else {
        	g2.setColor(mfd_gc.ecam_caution_color);
        } 
        if ( (vs_alert_status == PressAlert.PULSING) && ((mfd_gc.current_time_millis % 1000) < 500) ) 
           { g2.setColor(mfd_gc.ecam_markings_color); }
        */
        g2.setFont(mfd_gc.font_xl);
        String vs_str = Integer.toString(Math.round(vs_value/10)*10);
        int vs_txt_y = vs_y + mfd_gc.line_height_xl /2;
        g2.drawString(vs_str, vs_x + mfd_gc.digit_width_xl*49/10 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, vs_str),
        		vs_txt_y);
        
        resetPen(g2);
    }
  
  
    private void drawCabinAlt(Graphics2D g2) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float alt_max = 10000;
        float alt_warn = 9550;
        float alt_thr = 8800;
        float alt_reset = 8600;

        float alt_range = 11000;
        int alt_value = Math.round(aircraft.cabin_altitude());
        float alt_dial = Math.min(alt_value, alt_range) / alt_range;
    	float max_alt_dial = Math.min(alt_max, alt_range) / alt_range;
		
        boolean full_arc = true;
        
        int alt_x = mfd_gc.press_cab_alt_x;
        int alt_y = mfd_gc.press_gauges_y;
        int alt_r = mfd_gc.press_dial_r;        
        
        int deg_start = full_arc ? 225 : 180;
        int deg_warning = full_arc ? 25 : 0;
        int deg_full_range = deg_start-deg_warning;
        int deg_caution = deg_start-Math.round(max_alt_dial*deg_full_range)-10;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;


        // Cabin ALT Gauge Arc
        g2.setColor(mfd_gc.ecam_markings_color);  
        g2.drawArc(alt_x-alt_r, alt_y-alt_r, 2*alt_r, 2*alt_r, deg_start, -deg_norm_range);
        g2.setColor(mfd_gc.ecam_warning_color);
        // Cabin ALT red arc 
        original_stroke = g2.getStroke();
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * mfd_gc.grow_scaling_factor ), new BasicStroke( 2.0f * mfd_gc.grow_scaling_factor ) ));
        int alt_r_red = alt_r * 98/100; 
        g2.drawArc(alt_x-alt_r_red, alt_y-alt_r_red, 2*alt_r_red, 2*alt_r_red, deg_caution, -deg_warn_range);
        g2.setTransform(original_at);
        g2.setStroke(original_stroke);


        // scale markings every 50%
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.rotate(Math.toRadians(360-deg_start), alt_x, alt_y);
        for (int i=0; i<2; i++) {
        	g2.drawLine(alt_x+alt_r*18/20, alt_y, alt_x+alt_r-1, alt_y);
        	g2.rotate(Math.toRadians(deg_full_range/2), alt_x, alt_y);
        }
        g2.setTransform(original_at);
        

        // ALT : scale number 0 and 10
        String min_str = "0";
        String max_str = "10";
        g2.setFont(mfd_gc.font_s);
        g2.setColor(mfd_gc.ecam_markings_color);
        int egt_digit_x;
        int egt_digit_y;
        int egt_digit_angle = 360-deg_start;
        // mid
        egt_digit_x = alt_x + (int)(Math.cos(Math.toRadians(egt_digit_angle))*alt_r*11/16);
        egt_digit_y = alt_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*alt_r*11/16);
        g2.drawString(min_str, egt_digit_x - mfd_gc.digit_width_s/2, egt_digit_y+mfd_gc.line_height_s*3/8);
        egt_digit_angle += deg_full_range;
        // max
        egt_digit_x = alt_x + (int)(Math.cos(Math.toRadians(egt_digit_angle))*alt_r*12/16);
        egt_digit_y = alt_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*alt_r*12/16);
        g2.drawString(max_str, egt_digit_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, max_str)/2, egt_digit_y+mfd_gc.line_height_s*3/8);
        
        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);
        int needle_x = full_arc ? alt_x : alt_x+alt_r/2;
        g2.rotate(Math.toRadians(Math.round(alt_dial*deg_full_range)-deg_start), alt_x, alt_y);
        if (alt_value < alt_max)
        	g2.setColor(mfd_gc.ecam_normal_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawLine(needle_x, alt_y, alt_x+alt_r*11/10, alt_y);
        g2.setTransform(original_at);
             
        // ALT MAX
        /*
        g2.setColor(Color.yellow);
        g2.rotate(Math.toRadians(Math.round(max_alt_dial*deg_full_range)-deg_start), alt_x, alt_y);
        g2.fillRect(alt_x+alt_r, alt_y, alt_r/4, alt_r/7);
        g2.setTransform(original_at);
        */
        
        // Value
        if ( alt_value < alt_warn ) {
        	g2.setColor(mfd_gc.ecam_normal_color);
        } else {
        	g2.setColor(mfd_gc.ecam_warning_color);
        } 

        g2.setFont(mfd_gc.font_xl);
        int alt_txt_y = alt_y + mfd_gc.line_height_xl*5/4;
        String alt_str = Integer.toString(Math.round(alt_value/10)*10);        
        g2.drawString(alt_str, alt_x + mfd_gc.digit_width_xl*30/10 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, alt_str),
        		alt_txt_y);
        
        resetPen(g2);
    }
     
    private void drawDeltaP(Graphics2D g2) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float dp_max = 8.5f;
        float dp_warn = 8.5f;
        float dp_thr = 1.5f;
        float dp_reset = 0.1f;

        float dp_range = 8.8f;
        int dp_value = Math.round(aircraft.cabin_delta_p());
        float dp_dial = Math.min(dp_value, dp_range) / dp_range;
    	float max_dp_dial = Math.min(dp_max, dp_range) / dp_range;
		
        boolean full_arc = true;
        
        int dp_x = mfd_gc.press_delta_p_x;
        int dp_y = mfd_gc.press_gauges_y;
        int dp_r = mfd_gc.press_dial_r;        
        
        int deg_gauge_start = full_arc ? 210 : 180;
        int deg_gauge_end = full_arc ? 30 : 0;
        int deg_min_caution_range = 7; 
        int deg_max_caution_range = 7; 
        int deg_min_caution = deg_gauge_start - deg_min_caution_range;
        int deg_max_caution = deg_gauge_end + deg_max_caution_range;
        int deg_zero = 195; 
        int deg_max_mark = 45;
        int deg_marking_range = deg_zero - deg_max_mark;
        
        int deg_full_range = deg_zero-deg_gauge_end;  // Full range is the needle range starting from zero
        int deg_white_range = deg_min_caution - deg_max_caution; 
        
        // int deg_caution = deg_start-Math.round(max_dp_dial*deg_full_range)-20;

        // int deg_norm_range = deg_start-deg_caution*2;
        // int deg_warn_range = deg_caution-deg_warning;
       

        // Delta P Gauge Arc
        g2.setColor(mfd_gc.ecam_markings_color);  
        g2.drawArc(dp_x-dp_r, dp_y-dp_r, 2*dp_r, 2*dp_r, deg_min_caution, -deg_white_range);

        // Min and Max amber arcs
        g2.setColor(mfd_gc.ecam_caution_color);
        original_stroke = g2.getStroke();
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * mfd_gc.grow_scaling_factor ), new BasicStroke( 2.0f * mfd_gc.grow_scaling_factor ) ));
        int dp_r_red = dp_r * 98/100; 
        g2.drawArc(dp_x-dp_r_red, dp_y-dp_r_red, 2*dp_r_red, 2*dp_r_red, deg_min_caution+1, deg_min_caution_range);
        g2.drawArc(dp_x-dp_r_red, dp_y-dp_r_red, 2*dp_r_red, 2*dp_r_red, deg_gauge_end-1, deg_max_caution_range);
        g2.setTransform(original_at);
        g2.setStroke(original_stroke);


        // scale markings every 50%
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.rotate(Math.toRadians(360-deg_zero), dp_x, dp_y);
        for (int i=0; i<3; i++) {
        	g2.drawLine(dp_x+dp_r*18/20, dp_y, dp_x+dp_r-1, dp_y);
        	g2.rotate(Math.toRadians(deg_marking_range/2), dp_x, dp_y);
        }
        g2.setTransform(original_at);
        

        // Delta P : scale number 0 and 10
        String min_str = "0";
        String max_str = "8";
        g2.setFont(mfd_gc.font_s);
        g2.setColor(mfd_gc.ecam_markings_color);
        int egt_digit_x;
        int egt_digit_y;
        int egt_digit_angle = 360-deg_zero;
        // min
        egt_digit_x = dp_x + (int)(Math.cos(Math.toRadians(egt_digit_angle))*dp_r*11/16);
        egt_digit_y = dp_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*dp_r*11/16);
        g2.drawString(min_str, egt_digit_x - mfd_gc.digit_width_s/2, egt_digit_y+mfd_gc.line_height_s*3/8);
        egt_digit_angle += deg_marking_range;
        // max
        egt_digit_x = dp_x + (int)(Math.cos(Math.toRadians(egt_digit_angle))*dp_r*12/16);
        egt_digit_y = dp_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*dp_r*12/16);
        g2.drawString(max_str, egt_digit_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, max_str)/2, egt_digit_y+mfd_gc.line_height_s*3/8);
        
        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);
        int needle_x = full_arc ? dp_x : dp_x+dp_r/2;
        g2.rotate(Math.toRadians(Math.round(dp_dial*deg_full_range)-deg_zero), dp_x, dp_y);
        if (dp_value < dp_max)
        	g2.setColor(mfd_gc.ecam_normal_color);
        else
        	g2.setColor(mfd_gc.ecam_caution_color);
        g2.drawLine(needle_x, dp_y, dp_x+dp_r*11/10, dp_y);
        g2.setTransform(original_at);
             
        // DeltaP MAX
        /*
        g2.setColor(Color.yellow);
        g2.rotate(Math.toRadians(Math.round(max_dp_dial*deg_full_range)-deg_start), dp_x, dp_y);
        g2.fillRect(dp_x+dp_r, dp_y, dp_r/4, dp_r/7);
        g2.setTransform(original_at);
        */
        
        // Value
        if ( dp_value < dp_warn ) {
        	g2.setColor(mfd_gc.ecam_normal_color);
        } else {
        	g2.setColor(mfd_gc.ecam_caution_color);
        } 

        g2.setFont(mfd_gc.font_xl);
        int dp_txt_y = dp_y + mfd_gc.line_height_xl*5/4;        
        String dp_str = one_decimal_format.format(aircraft.cabin_delta_p());
        g2.drawString(dp_str, dp_x + mfd_gc.digit_width_xl*30/10 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, dp_str),
        		dp_txt_y);
        
        resetPen(g2);
    }
     
    
    
    /* 
     * Functions from Destination Airport Class 
     */
    
    private void drawLDGElevation(Graphics2D g2) {
		String str_ldg_legend = "LDG  ELEV";
		String str_alt_units = "FT";
		String str_mode = "AUTO";
	    // Draw legends
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_ldg_legend, mfd_gc.press_ldg_legend_x, mfd_gc.press_ldg_mode_y);
		// Draw units
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_alt_units, mfd_gc.press_ldg_units_x, mfd_gc.press_ldg_mode_y);
		// Mode
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.setFont(mfd_gc.font_xxl);
		g2.drawString(str_mode, mfd_gc.press_ldg_mode_x, mfd_gc.press_ldg_mode_y);
		drawAutoLDGElevation(g2);
    }
    
    private void drawAutoLDGElevation(Graphics2D g2) {
		String str_ldg = "XX";
    	String airport_icao = getDestination();
    	NavigationObjectRepository nor = NavigationObjectRepository.get_instance();
    	Airport airport = nor.get_airport(airport_icao.trim());
    	if ( airport != null ) {   
    		g2.setColor(mfd_gc.ecam_normal_color);
    		g2.setFont(mfd_gc.font_xxl);
    		str_ldg = ""+Math.round(airport.elev);
    	} else {
    		g2.setColor(mfd_gc.ecam_caution_color);	
    	}
		g2.drawString(str_ldg, mfd_gc.press_ldg_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_ldg), mfd_gc.press_ldg_mode_y);
    }
    
    private String get_nav_dest() {
        String dest_str = "";
        Localizer dest_loc = null;
        int hsi_source = this.avionics.hsi_source();
        int bank = 0;
        if ( hsi_source == Avionics.HSI_SOURCE_NAV1 ) {
            bank = 1;
        } else if ( hsi_source == Avionics.HSI_SOURCE_NAV2 ) {
            bank = 2;
        }
        if ( bank > 0 ) {
            dest_loc = this.avionics.get_tuned_localizer(bank);
            if ( dest_loc != null ) dest_str = dest_loc.airport;
        }
        return dest_str;
    }


    private String get_fms_dest() {
        String dest_str = "";
        if ( this.avionics.hsi_source() == Avionics.HSI_SOURCE_GPS ) {
            FMSEntry last_wpt = this.avionics.get_fms().get_last_waypoint();
            if ( ( last_wpt != null ) && ( last_wpt.type == FMSEntry.ARPT ) ) {
                dest_str = last_wpt.name;                
            }
        }
        return dest_str;
    }


    private String getDestination() {
        String dest_arpt_str = "";
        if ( this.aircraft.on_ground() ) {
            dest_arpt_str = this.aircraft.get_nearest_arpt();           
        } else if ( ! this.preferences.get_arpt_chart_nav_dest() ) {
            dest_arpt_str = this.aircraft.get_nearest_arpt();            
        } else {
            if ( dest_arpt_str.equals("") ) dest_arpt_str = get_nav_dest();
            if ( dest_arpt_str.equals("") ) dest_arpt_str = get_fms_dest();
            if ( dest_arpt_str.equals("") ) dest_arpt_str = this.aircraft.get_nearest_arpt();  
        }
        return dest_arpt_str;
    }

    private void scalePen(Graphics2D g2) {
        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
    }

	private void scalePen(Graphics2D g2, float factor) {
		original_stroke = g2.getStroke();
		g2.setStroke(new BasicStroke(factor * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
	}

    private void resetPen(Graphics2D g2) {
        g2.setStroke(original_stroke);
    }
}
