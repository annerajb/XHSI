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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;

public class BleedAir extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;
	
	private long a_ice_ground_start;	
	private boolean a_ice_warning;
	private boolean a_ice_count_on;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	public BleedAir(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {		
		super(model_factory, hsi_gc, parent_component);
		this.a_ice_ground_start = 0;
		this.a_ice_warning = false;
		this.a_ice_count_on = false;
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_BLEED ) {
			// Page ID
			drawPageID(g2, "BLEED");
			drawPackMix(g2, mfd_gc.bleed_circuit1_x, aircraft.pack_comp_temp(1), aircraft.pack_out_temp(1), aircraft.pack_flow(1), aircraft.pack_bypass_ratio(1) );
			drawPackMix(g2, mfd_gc.bleed_circuit2_x, aircraft.pack_comp_temp(2), aircraft.pack_out_temp(2), aircraft.pack_flow(2), aircraft.pack_bypass_ratio(2) );
			drawBleedValvesStatus(g2);
			drawPackOutFlow(g2);
			drawEngineFlow(g2);
			drawXBleed(g2);
			drawAPUBleed(g2);
			drawAntiIce(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.panel_rect.x;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}

	private void drawBleedValvesStatus(Graphics2D g2) {
		g2.setFont(mfd_gc.font_xl);
		
		drawValveHoriz(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_CROSS), mfd_gc.bleed_cross_valve_x, mfd_gc.bleed_cross_valve_y);
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_APU), mfd_gc.bleed_apu_valve_x, mfd_gc.bleed_apu_valve_y);

		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1), mfd_gc.bleed_circuit1_x, mfd_gc.bleed_eng_valve_y);
		drawValveHoriz(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1_HP), mfd_gc.bleed_hp_valve_x1, mfd_gc.bleed_hp_valve_y);

		if (mfd_gc.num_eng>1) {
			drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG2), mfd_gc.bleed_circuit2_x, mfd_gc.bleed_eng_valve_y);
			drawValveHoriz(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG2_HP), mfd_gc.bleed_hp_valve_x2, mfd_gc.bleed_hp_valve_y);
		}
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK1), mfd_gc.bleed_circuit1_x, mfd_gc.bleed_pack_valve_y);
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK2), mfd_gc.bleed_circuit2_x, mfd_gc.bleed_pack_valve_y);

		// RAM AIR
		/* FCOM A320 1.21.10 p13 rev 23
		 * Ram air inlet : green fully open in flight, amber fully open on ground, amber transit, green fully closed 
		 */
		g2.setColor(mfd_gc.ecam_markings_color);
		String ram_str = "RAM";
		String air_str = "AIR";
		g2.drawString(ram_str, mfd_gc.bleed_ram_air_valve_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ram_str)/2 , mfd_gc.bleed_ram_air_legend_y1 );
		g2.drawString(air_str, mfd_gc.bleed_ram_air_valve_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, air_str)/2 , mfd_gc.bleed_ram_air_legend_y2 );
		drawValveVert(g2, aircraft.ram_air_valve(), mfd_gc.bleed_ram_air_valve_x, mfd_gc.bleed_ram_air_valve_y);
		
		// Ground Bleed (GPU)
		g2.setColor(mfd_gc.ecam_normal_color);
		String gnd_str = "GND";
		g2.setFont(mfd_gc.font_m);
		g2.drawString(gnd_str, mfd_gc.bleed_gnd_x - mfd_gc.get_text_width(g2, mfd_gc.font_m, gnd_str)/2 , mfd_gc.bleed_gnd_legend_y );
		int gnd_tri_x[] = { mfd_gc.bleed_gnd_x - mfd_gc.bleed_out_tri_dx, mfd_gc.bleed_gnd_x, mfd_gc.bleed_gnd_x + mfd_gc.bleed_out_tri_dx };
		int gnd_tri_y[] = { mfd_gc.bleed_gnd_y, mfd_gc.bleed_gnd_y - mfd_gc.bleed_out_tri_dy, mfd_gc.bleed_gnd_y };
		g2.drawPolygon(gnd_tri_x, gnd_tri_y, 3);

	}

	private void drawPackOutFlow(Graphics2D g2) {	
		boolean flow_status = (aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK1) == ValveStatus.VALVE_OPEN && this.aircraft.bleed_air_press(Aircraft.BLEED_LEFT) > 10) || 
				(aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK2) == ValveStatus.VALVE_OPEN && this.aircraft.bleed_air_press(Aircraft.BLEED_RIGHT) > 10) || 
				aircraft.ram_air_valve() == ValveStatus.VALVE_OPEN;
		g2.setColor(flow_status ? mfd_gc.ecam_markings_color : mfd_gc.ecam_caution_color);
		int tri_x1[] = { mfd_gc.bleed_out_tri_x1 - mfd_gc.bleed_out_tri_dx, mfd_gc.bleed_out_tri_x1, mfd_gc.bleed_out_tri_x1 + mfd_gc.bleed_out_tri_dx };
		int tri_x2[] = { mfd_gc.bleed_out_tri_x2 - mfd_gc.bleed_out_tri_dx, mfd_gc.bleed_out_tri_x2, mfd_gc.bleed_out_tri_x2 + mfd_gc.bleed_out_tri_dx };
		int tri_x3[] = { mfd_gc.bleed_out_tri_x3 - mfd_gc.bleed_out_tri_dx, mfd_gc.bleed_out_tri_x3, mfd_gc.bleed_out_tri_x3 + mfd_gc.bleed_out_tri_dx };
		int tri_y[]  = { mfd_gc.bleed_out_tri_y, mfd_gc.bleed_out_tri_y - mfd_gc.bleed_out_tri_dy, mfd_gc.bleed_out_tri_y };
		g2.drawPolygon(tri_x1, tri_y, 3);
		g2.drawPolygon(tri_x2, tri_y, 3);
		g2.drawPolygon(tri_x3, tri_y, 3);		
		g2.setColor(flow_status ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color);
		g2.drawLine(mfd_gc.bleed_circuit1_x, mfd_gc.bleed_out_line_y, mfd_gc.bleed_circuit2_x, mfd_gc.bleed_out_line_y);
		g2.drawLine(mfd_gc.bleed_circuit1_x, mfd_gc.bleed_out_line_y, mfd_gc.bleed_circuit1_x, mfd_gc.bleed_mix_box_top);
		g2.drawLine(mfd_gc.bleed_circuit2_x, mfd_gc.bleed_out_line_y, mfd_gc.bleed_circuit2_x, mfd_gc.bleed_mix_box_top);		
		// RAM AIR
		g2.setColor(aircraft.ram_air_valve() != ValveStatus.VALVE_OPEN_FAILED ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color);
		g2.drawLine(mfd_gc.bleed_ram_air_valve_x, mfd_gc.bleed_ram_air_valve_y + mfd_gc.hyd_valve_r, mfd_gc.bleed_ram_air_valve_x, mfd_gc.bleed_ram_air_valve_y + mfd_gc.hyd_valve_r*2);
		if (aircraft.ram_air_valve() == ValveStatus.VALVE_OPEN || aircraft.ram_air_valve() == ValveStatus.VALVE_OPEN_FAILED) {
			g2.setColor(aircraft.ram_air_valve() == ValveStatus.VALVE_OPEN ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color);
			g2.drawLine(mfd_gc.bleed_ram_air_valve_x, mfd_gc.bleed_ram_air_valve_y - mfd_gc.hyd_valve_r, mfd_gc.bleed_ram_air_valve_x, mfd_gc.bleed_out_line_y);
		}
	}

	private void drawXBleed(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_normal_color);
		if (aircraft.bleed_valve(Aircraft.BLEED_VALVE_CROSS) == ValveStatus.VALVE_OPEN) {
			g2.drawLine(mfd_gc.bleed_circuit1_x , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_cross_valve_x - mfd_gc.hyd_valve_r, mfd_gc.bleed_cross_valve_y);
			g2.drawLine(mfd_gc.bleed_circuit2_x , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_cross_valve_x + mfd_gc.hyd_valve_r, mfd_gc.bleed_cross_valve_y);
		} else {
			g2.drawLine(mfd_gc.bleed_cross_valve_x - mfd_gc.hyd_valve_r , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_cross_valve_x - mfd_gc.hyd_valve_r*2, mfd_gc.bleed_cross_valve_y);
			g2.drawLine(mfd_gc.bleed_cross_valve_x + mfd_gc.hyd_valve_r , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_cross_valve_x + mfd_gc.hyd_valve_r*2, mfd_gc.bleed_cross_valve_y);
			
		}

	}

	private void drawAPUBleed(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.drawLine(mfd_gc.bleed_apu_valve_x, mfd_gc.bleed_apu_valve_y + mfd_gc.hyd_valve_r, mfd_gc.bleed_apu_valve_x, mfd_gc.bleed_apu_valve_y + mfd_gc.hyd_valve_r*2);
		if (aircraft.bleed_valve(Aircraft.BLEED_VALVE_APU) == ValveStatus.VALVE_OPEN) {
			g2.drawLine(mfd_gc.bleed_apu_valve_x , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_apu_valve_x, mfd_gc.bleed_apu_valve_y + mfd_gc.hyd_valve_r);
			g2.drawLine(mfd_gc.bleed_circuit1_x , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_cross_valve_x - mfd_gc.hyd_valve_r, mfd_gc.bleed_cross_valve_y);			
		} 
		g2.setColor(mfd_gc.ecam_markings_color);
		String apu_str = "APU";
		g2.drawString(apu_str,mfd_gc.bleed_apu_valve_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, apu_str)/2, mfd_gc.bleed_apu_legend_y );

	}
	
	private void drawPackMix(Graphics2D g2, int x, float comp_out_temp, float out_temp, float flow, float bypass_ratio) {
		int r = mfd_gc.cond_gauge_r*11/10;
		int y = mfd_gc.bleed_pack_valve_y;
		g2.setColor(mfd_gc.ecam_box_bg_color);
		g2.fillRect(x-mfd_gc.bleed_mix_box_dx, mfd_gc.bleed_mix_box_top,
					mfd_gc.bleed_mix_box_dx*2, mfd_gc.bleed_pack_valve_y-mfd_gc.hyd_valve_r-mfd_gc.bleed_mix_box_top);
		g2.setColor(mfd_gc.background_color);		
		g2.fillArc(x-r, y-r, r*2, r*2, 0, 180);
	
		drawGauge(g2, bypass_ratio, x, mfd_gc.bleed_mix_gauge_y, true);
		drawGauge(g2, flow, x, mfd_gc.bleed_pack_valve_y, false);
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.drawLine(x, mfd_gc.bleed_pack_valve_y + mfd_gc.hyd_valve_r, x, mfd_gc.bleed_eng_box_top_y);

		// Units
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_m);
		String unit_str= this.avionics.get_temp_units() == XHSISettings.TEMP_UNITS_C ? "°c" : "°F";
		g2.drawString(unit_str, x+mfd_gc.bleed_mix_box_dx-mfd_gc.get_text_width(g2, mfd_gc.font_m, unit_str), mfd_gc.bleed_ram_air_valve_y);
		g2.drawString(unit_str, x+mfd_gc.bleed_mix_box_dx-mfd_gc.get_text_width(g2, mfd_gc.font_m, unit_str), mfd_gc.bleed_flow_temp_y);

		// Compressor output flow temperature
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.setFont(mfd_gc.font_xxl);
		String flow_temp_str="XX";
		if (comp_out_temp>-99.00f) {
			g2.setColor(mfd_gc.ecam_normal_color);
			flow_temp_str = ""+Math.round(this.avionics.convert_temperature(comp_out_temp));
		} else {
			g2.setColor(mfd_gc.ecam_caution_color);
		}
		g2.drawString(flow_temp_str, x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, flow_temp_str)/2, mfd_gc.bleed_flow_temp_y);

		// Pack output flow temperature
		String mix_temp_str="XX";
		if (out_temp>-99.00f) {
			g2.setColor(mfd_gc.ecam_normal_color);
			mix_temp_str = ""+Math.round(this.avionics.convert_temperature(out_temp));
		} else {
			g2.setColor(mfd_gc.ecam_caution_color);
		}
		g2.drawString(mix_temp_str, x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, mix_temp_str)/2, mfd_gc.bleed_ram_air_valve_y);
		
	}
	
	
	private void drawEngineFlow(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_normal_color);
		// Engine 1
		if ( aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1) == ValveStatus.VALVE_OPEN ) {
			g2.drawLine(mfd_gc.bleed_circuit1_x, mfd_gc.bleed_circuit_eng_top_y, mfd_gc.bleed_circuit1_x, mfd_gc.bleed_eng_valve_y - mfd_gc.hyd_valve_r);
		}
		g2.drawLine(mfd_gc.bleed_circuit1_x, mfd_gc.bleed_eng_valve_y + mfd_gc.hyd_valve_r, mfd_gc.bleed_circuit1_x, mfd_gc.bleed_circuit_bottom_y);
		if ( aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1_HP) == ValveStatus.VALVE_OPEN ) {
			g2.drawLine(mfd_gc.bleed_circuit1_x, mfd_gc.bleed_hp_valve_y, mfd_gc.bleed_hp_valve_x1 - mfd_gc.hyd_valve_r, mfd_gc.bleed_hp_valve_y);
		}
		g2.drawLine(mfd_gc.bleed_hp_valve_x1 + mfd_gc.hyd_valve_r, mfd_gc.bleed_hp_valve_y, mfd_gc.bleed_hp_x1, mfd_gc.bleed_hp_valve_y);
		g2.drawLine(mfd_gc.bleed_hp_x1, mfd_gc.bleed_hp_valve_y, mfd_gc.bleed_hp_x1, mfd_gc.bleed_circuit_bottom_y);
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString("HP",mfd_gc.bleed_hp_x1+mfd_gc.digit_width_xl/2, mfd_gc.bleed_ip_hp_legend_y );
		g2.drawString("IP",mfd_gc.bleed_circuit1_x - mfd_gc.digit_width_xl*5/2, mfd_gc.bleed_ip_hp_legend_y );
		g2.setFont(mfd_gc.font_xxl);
		g2.drawString("1",mfd_gc.bleed_circuit1_x - mfd_gc.digit_width_xxl*3, mfd_gc.bleed_eng_valve_y);
		g2.setColor(mfd_gc.ecam_box_bg_color);
		g2.fillRect(mfd_gc.bleed_circuit1_x-mfd_gc.bleed_eng_box_outer_dx, mfd_gc.bleed_eng_box_top_y,
					mfd_gc.bleed_eng_box_outer_dx+mfd_gc.bleed_eng_box_inner_dx, mfd_gc.bleed_circuit_eng_top_y-mfd_gc.bleed_eng_box_top_y);
		// Units
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_m);
		String t_unit_str=this.avionics.get_temp_units() == XHSISettings.TEMP_UNITS_C ? "°c" : "°F";
		// TODO : Temperature unit switching to "°F"
		g2.drawString(t_unit_str, mfd_gc.bleed_circuit1_x+mfd_gc.bleed_eng_box_inner_dx-mfd_gc.get_text_width(g2, mfd_gc.font_m, t_unit_str), mfd_gc.bleed_eng_flow_temp_y);
		String psi_unit_str="PSI";
		g2.drawString(psi_unit_str, mfd_gc.bleed_circuit1_x+mfd_gc.bleed_eng_box_inner_dx-mfd_gc.get_text_width(g2, mfd_gc.font_m, psi_unit_str), mfd_gc.bleed_eng_flow_press_y);

		// flow temperature
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.setFont(mfd_gc.font_xxl);
		String flow_temp_str="XX";
		if (this.aircraft.bleed_air_temp(Aircraft.BLEED_LEFT)>-99.0) {
			flow_temp_str = ""+Math.round(this.avionics.convert_temperature(this.aircraft.bleed_air_temp(Aircraft.BLEED_LEFT)));
			g2.setColor(mfd_gc.ecam_normal_color);
		}
		g2.drawString(flow_temp_str, mfd_gc.bleed_circuit1_x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, flow_temp_str)/2, mfd_gc.bleed_eng_flow_temp_y);

		// flow pressure
		String psi_str="XX";
		if (this.aircraft.bleed_air_press(Aircraft.BLEED_LEFT)>-99.0) {
			psi_str = ""+Math.round(this.aircraft.bleed_air_press(Aircraft.BLEED_LEFT));
			g2.setColor(mfd_gc.ecam_normal_color);
		}
		g2.drawString(psi_str, mfd_gc.bleed_circuit1_x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, psi_str)/2, mfd_gc.bleed_eng_flow_press_y);

		
		
		if (mfd_gc.num_eng>1) {
			g2.setColor(mfd_gc.ecam_normal_color);
			if ( aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG2) == ValveStatus.VALVE_OPEN ) {
				g2.drawLine(mfd_gc.bleed_circuit2_x, mfd_gc.bleed_circuit_eng_top_y, mfd_gc.bleed_circuit2_x, mfd_gc.bleed_eng_valve_y - mfd_gc.hyd_valve_r);
			}
			g2.drawLine(mfd_gc.bleed_circuit2_x, mfd_gc.bleed_eng_valve_y + mfd_gc.hyd_valve_r, mfd_gc.bleed_circuit2_x, mfd_gc.bleed_circuit_bottom_y);
			if ( aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG2_HP) == ValveStatus.VALVE_OPEN ) {
				g2.drawLine(mfd_gc.bleed_circuit2_x, mfd_gc.bleed_hp_valve_y, mfd_gc.bleed_hp_valve_x2 + mfd_gc.hyd_valve_r, mfd_gc.bleed_hp_valve_y);
			}			
			g2.drawLine(mfd_gc.bleed_hp_valve_x2 - mfd_gc.hyd_valve_r, mfd_gc.bleed_hp_valve_y, mfd_gc.bleed_hp_x2, mfd_gc.bleed_hp_valve_y);
			g2.drawLine(mfd_gc.bleed_hp_x2, mfd_gc.bleed_hp_valve_y, mfd_gc.bleed_hp_x2, mfd_gc.bleed_circuit_bottom_y);
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.setFont(mfd_gc.font_xl);
			g2.drawString("HP",mfd_gc.bleed_hp_x2-mfd_gc.digit_width_xl*5/2, mfd_gc.bleed_ip_hp_legend_y );
			g2.drawString("IP",mfd_gc.bleed_circuit2_x+mfd_gc.digit_width_xl/2, mfd_gc.bleed_ip_hp_legend_y );
			g2.setFont(mfd_gc.font_xxl);
			g2.drawString("2",mfd_gc.bleed_circuit2_x + mfd_gc.digit_width_xxl*2, mfd_gc.bleed_eng_valve_y);
			g2.setColor(mfd_gc.ecam_box_bg_color);
			g2.fillRect(mfd_gc.bleed_circuit2_x-mfd_gc.bleed_eng_box_inner_dx, mfd_gc.bleed_eng_box_top_y,
						mfd_gc.bleed_eng_box_outer_dx+mfd_gc.bleed_eng_box_inner_dx, mfd_gc.bleed_circuit_eng_top_y-mfd_gc.bleed_eng_box_top_y);
			// Units
			g2.setColor(mfd_gc.ecam_action_color);
			g2.setFont(mfd_gc.font_m);
			g2.drawString(t_unit_str, mfd_gc.bleed_circuit2_x-mfd_gc.bleed_eng_box_inner_dx, mfd_gc.bleed_eng_flow_temp_y);
			
			g2.drawString(psi_unit_str, mfd_gc.bleed_circuit2_x-mfd_gc.bleed_eng_box_inner_dx, mfd_gc.bleed_eng_flow_press_y);

			// flow temperature
			g2.setColor(mfd_gc.ecam_caution_color);
			g2.setFont(mfd_gc.font_xxl);
			flow_temp_str="XX";
			if (this.aircraft.bleed_air_temp(Aircraft.BLEED_RIGHT)>-99) {
				flow_temp_str = ""+Math.round(this.avionics.convert_temperature(this.aircraft.bleed_air_temp(Aircraft.BLEED_RIGHT)));
				g2.setColor(mfd_gc.ecam_normal_color);
			}
			g2.drawString(flow_temp_str, mfd_gc.bleed_circuit2_x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, flow_temp_str)/2, mfd_gc.bleed_eng_flow_temp_y);

			// flow pressure
			psi_str="XX";
			if (this.aircraft.bleed_air_press(Aircraft.BLEED_RIGHT)>-99) {
				psi_str = ""+Math.round(this.aircraft.bleed_air_press(Aircraft.BLEED_RIGHT));
				g2.setColor(mfd_gc.ecam_normal_color);
			}
			g2.drawString(psi_str, mfd_gc.bleed_circuit2_x-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, psi_str)/2, mfd_gc.bleed_eng_flow_press_y);

		}
	}
	
	private void drawAntiIce(Graphics2D g2) {
		if (this.aircraft.get_wing_anti_ice()) {
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.setFont(mfd_gc.font_xl);
			String anti_str="ANTI";
			String ice_str="ICE";
			g2.drawString(anti_str, mfd_gc.bleed_anti_ice_left_x, mfd_gc.bleed_anti_ice_1_y);
			g2.drawString(ice_str, mfd_gc.bleed_anti_ice_left_x, mfd_gc.bleed_anti_ice_2_y);
			g2.drawString(anti_str, mfd_gc.bleed_anti_ice_right_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, anti_str), mfd_gc.bleed_anti_ice_1_y);
			g2.drawString(ice_str, mfd_gc.bleed_anti_ice_right_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, ice_str), mfd_gc.bleed_anti_ice_2_y);
			int tri_y[] = { mfd_gc.bleed_anti_ice_tri_1_y, mfd_gc.bleed_anti_ice_tri_2_y, mfd_gc.bleed_cross_valve_y };
			int tri_x_left[] = { mfd_gc.bleed_anti_ice_left_tri_2_x, mfd_gc.bleed_anti_ice_left_tri_2_x, mfd_gc.bleed_anti_ice_left_tri_1_x }; 
			int tri_x_right[] = { mfd_gc.bleed_anti_ice_right_tri_2_x, mfd_gc.bleed_anti_ice_right_tri_2_x, mfd_gc.bleed_anti_ice_right_tri_1_x };
			
			/* Anti-ice arrow
			 * FCOM A320 1.36.20 p5 rev 23
			 * not displayed when the corresponding valve is close
			 * displayed in green when the corresponding valve is close
			 * displayed in amber when :
			 * 	- valve open and air pressure low or high
			 *  - valve open on ground more than 10 seconds
			 */
			
	    	if (this.aircraft.on_ground()) {
	    		 if (!a_ice_warning && !a_ice_count_on) { 
	    			 a_ice_ground_start = mfd_gc.current_time_millis;
	    			 a_ice_count_on=true;
	    		 }
	    		 if (a_ice_count_on && (mfd_gc.current_time_millis >  a_ice_ground_start + 10000)) a_ice_warning = true;
	    	} else {
	    		 a_ice_warning = false;
	    		 a_ice_count_on = false;
	    	}
			
			// LEFT anti ice
			g2.setColor(this.aircraft.bleed_air_press(Aircraft.BLEED_LEFT)<4 || a_ice_warning ? mfd_gc.ecam_caution_color :mfd_gc.ecam_normal_color) ;
			g2.drawPolygon(tri_x_left, tri_y, 3);
			g2.drawLine(mfd_gc.bleed_circuit1_x , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_anti_ice_left_tri_2_x, mfd_gc.bleed_cross_valve_y);
			// RIGHT anti ice
			g2.setColor(this.aircraft.bleed_air_press(Aircraft.BLEED_RIGHT)<4 || a_ice_warning ? mfd_gc.ecam_caution_color :mfd_gc.ecam_normal_color) ;			
			g2.drawPolygon(tri_x_right, tri_y, 3);			
			g2.drawLine(mfd_gc.bleed_circuit2_x , mfd_gc.bleed_cross_valve_y, mfd_gc.bleed_anti_ice_right_tri_2_x, mfd_gc.bleed_cross_valve_y);
		} else {
			a_ice_warning = false;
			a_ice_count_on = false;
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
    	} else if (valve_sts == ValveStatus.TRANSIT) {
    		int r2=(int)Math.round(0.707*r);
    		g2.drawLine(x-r2, y+r2, x+r2, y-r2);
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
    
    /**
     * Value in range 0.0 to 1.0. Middle is 0.5 
     */
    private void drawGauge(Graphics2D g2, float value, int x, int y, boolean temp_mode) {
    	AffineTransform original_at = g2.getTransform();
    	int r = mfd_gc.cond_gauge_r;
    	int ty = y-r*3/12;
    	g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawArc(x-r, y-r, r*2, r*2, 23, 134);
        g2.setFont(mfd_gc.font_l);
        if (temp_mode) {
        	g2.drawString("C",x-r*7/5-mfd_gc.digit_width_l,ty);
        	g2.drawString("H",x+r*7/5,ty);
        } else {
        	g2.drawString("LO",x-r*7/5-mfd_gc.digit_width_l*2,ty);
        	g2.drawString("HI",x+r*7/5,ty);        	
        }
        // middle mark
        g2.drawLine(x, y-r, x, y-r*11/10);
        
        // Needle
        if (value<0) value=0;
        g2.rotate(Math.toRadians(value-0.5f)*134, x, y);
        g2.setColor(mfd_gc.ecam_normal_color);
        int dx = r/12;
        int arrow_base = y-r*4/5;
        g2.drawLine(x, y-mfd_gc.cond_valve_r, x, arrow_base);
        g2.drawLine(x, y-r, x+dx, arrow_base);
        g2.drawLine(x, y-r, x-dx, arrow_base);
        g2.drawLine(x-dx, arrow_base, x+dx, arrow_base);      
        g2.setTransform(original_at);        
    }
}
