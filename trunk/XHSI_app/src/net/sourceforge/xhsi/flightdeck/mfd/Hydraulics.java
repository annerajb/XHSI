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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;

public class Hydraulics extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	public Hydraulics(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_HYDR) {
			boolean airbus = this.avionics.is_qpac() || this.avionics.is_jar_a320neo();
			String str_circuit_legend;
		
			// Page ID
			drawPageID(g2, "HYD");
			
			// GREEN circuit
			str_circuit_legend = airbus ? "GREEN" : "1";
			drawHydraulicGauge(g2, str_circuit_legend, 5000*this.aircraft.get_hyd_press(0), mfd_gc.hyd_1_x );
			drawHydraulicPump(g2, aircraft.get_hyd_pump(0), mfd_gc.hyd_1_x, mfd_gc.hyd_1_2_pump_y);
			drawValveVert(g2, this.aircraft.fire_extinguisher(0) ? ValveStatus.VALVE_CLOSED : ValveStatus.VALVE_OPEN, mfd_gc.hyd_1_x, mfd_gc.hyd_valve_y);
			drawHydraulicQty(g2, this.aircraft.get_hyd_quant(0), mfd_gc.hyd_1_x);
			g2.setColor(mfd_gc.ecam_normal_color);
			g2.drawLine(mfd_gc.hyd_1_x, mfd_gc.hyd_1_2_pump_y + mfd_gc.hyd_pump_h, mfd_gc.hyd_1_x, mfd_gc.hyd_valve_y-mfd_gc.hyd_valve_r );
			g2.drawLine(mfd_gc.hyd_1_x, mfd_gc.hyd_valve_y+mfd_gc.hyd_valve_r, mfd_gc.hyd_1_x, mfd_gc.hyd_qty_top_y );
					
			// YELLOW circuit
			str_circuit_legend = airbus ? "YELLOW" : "2";
			drawHydraulicGauge(g2, str_circuit_legend, 5000*this.aircraft.get_hyd_press(1), mfd_gc.hyd_2_x );
			drawHydraulicPump(g2, aircraft.get_hyd_pump(1), mfd_gc.hyd_2_x, mfd_gc.hyd_1_2_pump_y);
			drawValveVert(g2, this.aircraft.fire_extinguisher(1) ? ValveStatus.VALVE_CLOSED : ValveStatus.VALVE_OPEN, mfd_gc.hyd_2_x, mfd_gc.hyd_valve_y);
			drawHydraulicQty(g2, this.aircraft.get_hyd_quant(1), mfd_gc.hyd_2_x);
			g2.setColor(mfd_gc.ecam_normal_color);
			g2.drawLine(mfd_gc.hyd_2_x, mfd_gc.hyd_1_2_pump_y + mfd_gc.hyd_pump_h, mfd_gc.hyd_2_x, mfd_gc.hyd_valve_y-mfd_gc.hyd_valve_r );
			g2.drawLine(mfd_gc.hyd_2_x, mfd_gc.hyd_valve_y+mfd_gc.hyd_valve_r, mfd_gc.hyd_2_x, mfd_gc.hyd_qty_top_y );
			
			// BLUE circuit
			if (airbus) {
				drawHydraulicGauge(g2, "BLUE", 5000*this.aircraft.get_hyd_press(2), mfd_gc.hyd_3_x );
				drawHydraulicPump(g2, aircraft.get_hyd_pump(2), mfd_gc.hyd_3_x, mfd_gc.hyd_3_pump_y);
				drawHydraulicQty(g2, this.aircraft.get_hyd_quant(2), mfd_gc.hyd_3_x);
				g2.setColor(mfd_gc.ecam_normal_color);
				g2.drawLine(mfd_gc.hyd_3_x, mfd_gc.hyd_3_pump_y + mfd_gc.hyd_pump_h, mfd_gc.hyd_3_x, mfd_gc.hyd_qty_top_y );
			}
			
			// Pumps legends
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.setFont(mfd_gc.font_xl);
			g2.drawString("1", mfd_gc.hyd_1_pump_legend_x, mfd_gc.hyd_3_pump_y + mfd_gc.hyd_pump_h/2);
			g2.drawString("2", mfd_gc.hyd_2_pump_legend_x, mfd_gc.hyd_3_pump_y + mfd_gc.hyd_pump_h/2);
			if (airbus) g2.drawString("ELEC", mfd_gc.hyd_3_pump_legend_x, mfd_gc.hyd_1_2_pump_y + mfd_gc.hyd_pump_h/3);
						
			// Power Transfer Unit
			drawPTU(g2, this.aircraft.get_hyd_ptu());
			
			// Yellow ELEC Pump
			if (airbus) drawElecPump(g2, aircraft.get_hyd_pump(3), mfd_gc.hyd_2_x, mfd_gc.hyd_rat_pump_y);
			
			// RAT
			if (airbus) drawRATPump(g2, aircraft.get_hyd_pump(4), mfd_gc.hyd_3_x, mfd_gc.hyd_rat_pump_y);
			
			// Units
			drawUnits(g2, "PSI", mfd_gc.hyd_psi_unit_x1);
			drawUnits(g2, "PSI", mfd_gc.hyd_psi_unit_x2);
			
		}
	}


	
	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.hyd_psi_unit_x1 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}

	private void drawUnits(Graphics2D g2, String units_str, int x) {
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(units_str, x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, units_str)/2, mfd_gc.hyd_psi_value_y);
	}

	
	private void drawHydraulicGauge(Graphics2D g2, String gauge_str, float pressure, int x) {
		String pressure_str = "" + Math.round(pressure);
		Color legendColor = pressure > 2000.0f ? mfd_gc.ecam_markings_color : mfd_gc.ecam_caution_color;
		Color pressureColor = pressure > 2000.0f ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
		g2.setColor(legendColor);
		
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(gauge_str, x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, gauge_str)/2, mfd_gc.hyd_psi_legend_y);
		g2.setFont(mfd_gc.font_xxl);
		g2.setColor(pressureColor);
		g2.drawString(pressure_str, x + mfd_gc.digit_width_xxl*2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, pressure_str), mfd_gc.hyd_psi_value_y);		
		g2.drawLine(x,mfd_gc.hyd_arrow_base_y,x,mfd_gc.hyd_arrow_bottom_y);
		int tri_x[] = { x - mfd_gc.hyd_arrow_dx, x, x + mfd_gc.hyd_arrow_dx};
		int tri_y[] = { mfd_gc.hyd_arrow_base_y, mfd_gc.hyd_arrow_top_y, mfd_gc.hyd_arrow_base_y};
		g2.drawPolygon(tri_x, tri_y, 3);
		
	}

	private void drawHydraulicQty(Graphics2D g2, float qty, int x) {
		String qty_str = "" + Math.round(qty*100);
		int qty_y = mfd_gc.hyd_qty_bottom_y - Math.round((mfd_gc.hyd_qty_bottom_y - mfd_gc.hyd_qty_top_y) * qty);
		int green_h = Math.round((mfd_gc.hyd_qty_bottom_y - mfd_gc.hyd_qty_top_y) * 0.20f);
		int limit_h = Math.round((mfd_gc.hyd_qty_bottom_y - mfd_gc.hyd_qty_top_y) * 0.33f);
		int limit_y = mfd_gc.hyd_qty_bottom_y - limit_h;
		
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(x,mfd_gc.hyd_qty_top_y,x,mfd_gc.hyd_qty_bottom_y);
		
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(qty_str, x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, qty_str)/2, mfd_gc.hyd_qty_bottom_y + mfd_gc.line_height_xl);

		// Green sector
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.fillRect(x, mfd_gc.hyd_qty_top_y, mfd_gc.hyd_qty_dx/2, green_h);
		// g2.drawLine(x,mfd_gc.hyd_arrow_base_y,x,mfd_gc.hyd_arrow_bottom_y);
		
		int gauge_x[] = { x - mfd_gc.hyd_qty_dx, x, x - mfd_gc.hyd_qty_dx, x - mfd_gc.hyd_qty_dx, x};
		int gauge_y[] = { qty_y - mfd_gc.hyd_qty_dx, qty_y, qty_y + mfd_gc.hyd_qty_dx, mfd_gc.hyd_qty_bottom_y, mfd_gc.hyd_qty_bottom_y};
		g2.drawPolyline(gauge_x, gauge_y, 5);
		
		// Alarm sector
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.fillRect(x, limit_y, mfd_gc.hyd_qty_dx/2, limit_h);
				
	}
	
	private void drawHydraulicPump(Graphics2D g2, Aircraft.HydPumpStatus pump_status, int x, int y) {
		// TODO : this line should be managed by drawHydraulicGauge
		g2.drawLine(x,mfd_gc.hyd_line_top_y,x,y);
		if (pump_status == Aircraft.HydPumpStatus.ON ) {
			g2.setColor(mfd_gc.ecam_normal_color);
			g2.drawLine(x, y, x, y+mfd_gc.hyd_pump_h);
		} else if (pump_status == Aircraft.HydPumpStatus.OFF ) {
			g2.setColor(mfd_gc.ecam_caution_color);
			g2.drawLine(x-mfd_gc.hyd_pump_w/2, y+mfd_gc.hyd_pump_h/2, x+mfd_gc.hyd_pump_w/2, y+mfd_gc.hyd_pump_h/2);	

		} else {
			String lo_str = "LO";
			g2.setColor(mfd_gc.ecam_caution_color);
			g2.setFont(mfd_gc.font_xxl);
			g2.drawString(lo_str, x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, lo_str)/2, y+mfd_gc.hyd_pump_h/2+mfd_gc.line_height_xxl/2);
		}
		g2.drawRect(x-mfd_gc.hyd_pump_w/2, y, mfd_gc.hyd_pump_w, mfd_gc.hyd_pump_h);	
	}

	private void drawElecPump(Graphics2D g2, Aircraft.HydPumpStatus pump_status, int x, int y) {
		String elec_str = "ELEC";
		int arrow_dx = mfd_gc.hyd_valve_r*5/4;
		int str_x = x+arrow_dx*2 + mfd_gc.digit_width_xl/4;
		
		int tri_x[] = { x+arrow_dx , x+arrow_dx*2, x+arrow_dx*2	};
		int tri_y[] = {	y, y-arrow_dx/2, y+arrow_dx/2 };
		g2.setFont(mfd_gc.font_xl);
		
		if (pump_status == Aircraft.HydPumpStatus.ON ) {
			g2.setColor(mfd_gc.ecam_normal_color);
			g2.drawPolygon(tri_x, tri_y, 3);
			g2.drawLine(x, y, x+arrow_dx, y);
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.drawString(elec_str, str_x, y);
		} else if (pump_status == Aircraft.HydPumpStatus.OFF ) {
			g2.setColor(mfd_gc.ecam_markings_color);			
			g2.drawPolygon(tri_x, tri_y, 3);
			g2.drawString(elec_str, str_x, y);

		} else {
			g2.setColor(mfd_gc.ecam_caution_color);
			g2.drawPolygon(tri_x, tri_y, 3);
			g2.drawString(elec_str, str_x, y);
		}
	}

	private void drawRATPump(Graphics2D g2, Aircraft.HydPumpStatus pump_status, int x, int y) {
		String rat_str = "RAT";
		int arrow_dx = mfd_gc.hyd_valve_r*5/4;
		int str_x = x - arrow_dx*2 - mfd_gc.digit_width_xl/4 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, rat_str);
		
		int tri_x[] = { x-arrow_dx , x-arrow_dx*2, x-arrow_dx*2	};
		int tri_y[] = {	y, y-arrow_dx/2, y+arrow_dx/2 };
		g2.setFont(mfd_gc.font_xl);
		
		if (pump_status == Aircraft.HydPumpStatus.ON ) {
			g2.setColor(mfd_gc.ecam_normal_color);
			g2.drawPolygon(tri_x, tri_y, 3);
			g2.drawLine(x, y, x-arrow_dx, y);
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.drawString(rat_str, str_x, y);
		} else if (pump_status == Aircraft.HydPumpStatus.OFF ) {
			g2.setColor(mfd_gc.ecam_markings_color);			
			g2.drawPolygon(tri_x, tri_y, 3);
			g2.drawString(rat_str, str_x, y);

		} else {
			g2.setColor(mfd_gc.ecam_caution_color);
			g2.drawPolygon(tri_x, tri_y, 3);
			g2.drawString(rat_str, str_x, y);
		}
	}
	
    private void drawValveVert(Graphics2D g2, ValveStatus valve_sts, int x, int y) {
    	int r = mfd_gc.hyd_valve_r;
    	
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
	
	private void drawPTU(Graphics2D g2, Aircraft.HydPTUStatus status) {
		String ptu_str = "PTU";
		int left_arrow_x = mfd_gc.hyd_3_x - mfd_gc.hyd_valve_r*6;
		int middle_arrow_x = mfd_gc.hyd_psi_unit_x2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ptu_str)*3/4;
		int right_arrow_x = mfd_gc.hyd_psi_unit_x2 + mfd_gc.get_text_width(g2, mfd_gc.font_xl, ptu_str)*3/4;
		int arrow_dx = mfd_gc.hyd_valve_r*5/4;
		int left_arrow_tri_x[];
		int middle_arrow_tri_x[];
		int right_arrow_tri_x[];
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(ptu_str, mfd_gc.hyd_psi_unit_x2-mfd_gc.get_text_width(g2, mfd_gc.font_xl, ptu_str)/2, mfd_gc.hyd_ptu_bottom_y);
		if (status == Aircraft.HydPTUStatus.OFF) {
			g2.setColor(mfd_gc.ecam_caution_color);
		} else {
			g2.setColor(mfd_gc.ecam_normal_color);
		}
		g2.drawArc(mfd_gc.hyd_3_x - mfd_gc.hyd_valve_r, mfd_gc.hyd_ptu_line_y - mfd_gc.hyd_valve_r, mfd_gc.hyd_valve_r*2, mfd_gc.hyd_valve_r*2, 0, -180);
		g2.drawLine(mfd_gc.hyd_3_x + mfd_gc.hyd_valve_r, mfd_gc.hyd_ptu_line_y , middle_arrow_x - arrow_dx, mfd_gc.hyd_ptu_line_y);
		g2.drawLine(mfd_gc.hyd_3_x - mfd_gc.hyd_valve_r, mfd_gc.hyd_ptu_line_y , left_arrow_x, mfd_gc.hyd_ptu_line_y);
		if (status == Aircraft.HydPTUStatus.RIGHT || status == Aircraft.HydPTUStatus.LEFT) {
			g2.drawLine(mfd_gc.hyd_1_x, mfd_gc.hyd_ptu_line_y, left_arrow_x - arrow_dx, mfd_gc.hyd_ptu_line_y);
			g2.drawLine(right_arrow_x + arrow_dx, mfd_gc.hyd_ptu_line_y, mfd_gc.hyd_2_x, mfd_gc.hyd_ptu_line_y);
		}
		if (status == Aircraft.HydPTUStatus.RIGHT) {
			left_arrow_tri_x = new int [] { left_arrow_x - arrow_dx, left_arrow_x - arrow_dx, left_arrow_x };
			middle_arrow_tri_x = new int [] { middle_arrow_x - arrow_dx, middle_arrow_x - arrow_dx, middle_arrow_x  };
			right_arrow_tri_x = new int [] { right_arrow_x, right_arrow_x, right_arrow_x + arrow_dx };			
		} else if (status == Aircraft.HydPTUStatus.LEFT) {
			left_arrow_tri_x = new int [] { left_arrow_x, left_arrow_x, left_arrow_x - arrow_dx };
			middle_arrow_tri_x = new int [] { middle_arrow_x, middle_arrow_x, middle_arrow_x - arrow_dx };
			right_arrow_tri_x = new int [] { right_arrow_x + arrow_dx, right_arrow_x + arrow_dx, right_arrow_x  };			
		} else {
			left_arrow_tri_x = new int [] { left_arrow_x, left_arrow_x, left_arrow_x - arrow_dx };
			middle_arrow_tri_x = new int [] { middle_arrow_x, middle_arrow_x, middle_arrow_x - arrow_dx };
			right_arrow_tri_x = new int [] { right_arrow_x, right_arrow_x, right_arrow_x + arrow_dx };
		}
		int arrow_tri_y[] = {  mfd_gc.hyd_ptu_bottom_y , mfd_gc.hyd_ptu_top_y, mfd_gc.hyd_ptu_line_y };
		g2.drawPolygon(left_arrow_tri_x, arrow_tri_y, 3 );
		g2.drawPolygon(middle_arrow_tri_x, arrow_tri_y, 3 );
		g2.drawPolygon(right_arrow_tri_x, arrow_tri_y, 3 );
	}
}
