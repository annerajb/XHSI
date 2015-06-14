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
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Aircraft.ElecBus;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class Electrics extends MFDSubcomponent  {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	public Electrics(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {
		// aircraft.num_batteries() 
		// aircraft.num_buses()
		// aircraft.num_generators()
		// aircraft.num_inverters()

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_ELEC) {
			// Page ID
			drawPageID(g2, "ELEC");
			drawBusBox(g2, "DC BAT", 0, false, mfd_gc.elec_dc_bat_ess_box_x, mfd_gc.elec_dc_bat_box_y, mfd_gc.elec_dc_bat_ess_box_w, mfd_gc.elec_bus_box_h);
			drawBusBox(g2, "DC ESS", 0, false, mfd_gc.elec_dc_bat_ess_box_x, mfd_gc.elec_dc_ess_box_y, mfd_gc.elec_dc_bat_ess_box_w, mfd_gc.elec_bus_box_h);
			drawBusBox(g2, "DC ", 1, false, mfd_gc.elec_dc1_box_x, mfd_gc.elec_dc_box_y, mfd_gc.elec_dc_box_w, mfd_gc.elec_bus_box_h);
			drawBusBox(g2, "DC ", 2, false, mfd_gc.elec_dc2_box_x, mfd_gc.elec_dc_box_y, mfd_gc.elec_dc_box_w, mfd_gc.elec_bus_box_h);
			drawBusBox(g2, "AC ", 1, false, mfd_gc.elec_ac1_box_x, mfd_gc.elec_ac_box_y, mfd_gc.elec_ac_box_w, mfd_gc.elec_bus_box_h);
			drawBusBox(g2, "AC ", 2, false, mfd_gc.elec_ac2_box_x, mfd_gc.elec_ac_box_y, mfd_gc.elec_ac_box_w, mfd_gc.elec_bus_box_h);
			drawBusBox(g2, "AC ESS", 0, false, mfd_gc.elec_ac_ess_box_x, mfd_gc.elec_ac_ess_box_y, mfd_gc.elec_ac_ess_box_w, mfd_gc.elec_bus_box_h);
			
			drawElecGen(g2, "GEN 1", true, 0, 115, 400, mfd_gc.elec_gen1_x, mfd_gc.elec_gen_y );
			if (aircraft.num_generators()>1) {
				drawElecGen(g2, "GEN 2", true, 0, 115, 400, mfd_gc.elec_gen2_x, mfd_gc.elec_gen_y );
			}
			
			drawElecBatTr(g2, "BAT 1", true, 28, 0, mfd_gc.elec_bat1_x, mfd_gc.elec_bat_y );
			drawElecBatTr(g2, "BAT 2", true, 28, 0, mfd_gc.elec_bat2_x, mfd_gc.elec_bat_y );
			drawElecBatTr(g2, "TR 1", true, 28, 0, mfd_gc.elec_tr1_x, mfd_gc.elec_tr_y );
			drawElecBatTr(g2, "TR 2", true, 28, 0, mfd_gc.elec_tr2_x, mfd_gc.elec_tr_y );
			drawElecEmerGen(g2, "EMER GEN", false, 28, 0, mfd_gc.elec_emerg_x, mfd_gc.elec_ess_tr_emerg_y );
			drawElecEmerTr(g2, "ESS TR", false, 28, 0, mfd_gc.elec_ess_tr_x, mfd_gc.elec_ess_tr_emerg_y );
			
        	if (aircraft.has_apu()) {
        		// Elec Bloc
        		// Use the X-Plane noise generator to put some noise on freq
        		boolean v_avail = (aircraft.apu_n1()>89.0f);
        		boolean start=aircraft.apu_starter()>0;        		 
        		int load = Math.round(aircraft.apu_gen_amp());
        		int volt = v_avail ? Math.round(aircraft.apu_n1()/100*115) : 0;
        		int freq = v_avail ? Math.round(aircraft.apu_n1()/100*400) : 0;
        		drawElecAux(g2,"APU GEN", start, load, volt, freq, mfd_gc.elec_apugen_x, mfd_gc.elec_ext_apu_y);

        	}
			
			drawElecAux(g2,"EXT PWR", true, (int)aircraft.gpu_gen_amps(), 115, 400, mfd_gc.elec_extpwr_x, mfd_gc.elec_ext_apu_y);
			
			if (aircraft.apu_gen_on()) {
				drawElecAuxOnBus (g2, mfd_gc.elec_apugen_x + mfd_gc.elec_ext_apu_w/2, mfd_gc.elec_aux_y, mfd_gc.elec_ext_apu_y, aircraft.apu_on_bus());
			} else if (aircraft.gpu_gen_on()) {
				drawElecAuxOnBus (g2, mfd_gc.elec_extpwr_x + mfd_gc.elec_ext_apu_w/2, mfd_gc.elec_aux_y, mfd_gc.elec_ext_apu_y, aircraft.gpu_on_bus());
			} 
			
			drawDCbusLines(g2);
			drawACbusLines(g2);
			drawIDGTemperatures(g2);
			
		}
	}

	private void drawIDGTemperatures(Graphics2D g2) {
		// IDG Temperatures
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString("IDG 1", mfd_gc.elec_gen1_x + mfd_gc.elec_gen_w/2 - mfd_gc.digit_width_xl*2, mfd_gc.elec_idg_y);
		g2.drawString("IDG 2", mfd_gc.elec_gen2_x + mfd_gc.elec_gen_w/2 - mfd_gc.digit_width_xl*2, mfd_gc.elec_idg_y);
		String unit_str ="°C";
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_m);
		g2.drawString(unit_str, mfd_gc.elec_gen1_x + mfd_gc.elec_gen_w, mfd_gc.elec_idg_y);
		g2.drawString(unit_str, mfd_gc.elec_gen2_x - mfd_gc.digit_width_m*2, mfd_gc.elec_idg_y);
	}
	
	private void drawDCbusLines(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_normal_color);
		// TR to DC bus
		g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_dc_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac1_x, mfd_gc.elec_tr_y);
		g2.drawLine(mfd_gc.elec_ac2_x, mfd_gc.elec_dc_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac2_x, mfd_gc.elec_tr_y);
		// DC1 to DC BAT bus
		g2.drawLine(mfd_gc.elec_dc1_box_x + mfd_gc.elec_dc_box_w, mfd_gc.elec_dc_ess_y , mfd_gc.elec_dc_bat_x, mfd_gc.elec_dc_ess_y);
		g2.drawLine(mfd_gc.elec_dc_bat_x, mfd_gc.elec_dc_bat_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_dc_bat_x, mfd_gc.elec_dc_ess_box_y);
		// DC2 to DC BAT bus
		// g2.drawLine(mfd_gc.elec_dc_bat_x, mfd_gc.elec_dc_ess_y , mfd_gc.elec_dc2_box_x, mfd_gc.elec_dc_ess_y);
		// DC BAT bus to batteries
		g2.drawLine(mfd_gc.elec_bat1_x + mfd_gc.elec_bat_w, mfd_gc.elec_dc_bat_y, mfd_gc.elec_dc_bat_ess_box_x, mfd_gc.elec_dc_bat_y);
		g2.drawLine(mfd_gc.elec_dc_bat_ess_box_x + mfd_gc.elec_dc_bat_ess_box_w, mfd_gc.elec_dc_bat_y, mfd_gc.elec_bat2_x, mfd_gc.elec_dc_bat_y);
 
	}

	private void drawACbusLines(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_normal_color);
		// TR to AC bus
		g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_tr_y+ mfd_gc.elec_tr_h, mfd_gc.elec_ac1_x, mfd_gc.elec_ac_box_y );
		g2.drawLine(mfd_gc.elec_ac2_x, mfd_gc.elec_tr_y+ mfd_gc.elec_tr_h, mfd_gc.elec_ac2_x, mfd_gc.elec_ac_box_y );
		// GEN to AC bus
		if (aircraft.eng_gen_on_bus(0)) {
			g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_ac_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac1_x, mfd_gc.elec_gen_y);
		}
		if (aircraft.eng_gen_on_bus(1) && (aircraft.num_generators()>1)) {
			g2.drawLine(mfd_gc.elec_ac2_x, mfd_gc.elec_ac_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac2_x, mfd_gc.elec_gen_y);
		}
		// AC1 to AC ESS
		if (aircraft.ac_ess_on_bus() == ElecBus.BUS_1 ) {
			g2.drawLine(mfd_gc.elec_ac1_box_x + mfd_gc.elec_ac_box_w, mfd_gc.elec_ac_ess_y, mfd_gc.elec_ac_ess_box_x, mfd_gc.elec_ac_ess_y);
		} else if (aircraft.ac_ess_on_bus() == ElecBus.BUS_2 ) {
			g2.drawLine(mfd_gc.elec_ac2_box_x, mfd_gc.elec_ac_ess_y, mfd_gc.elec_ac_ess_box_x + mfd_gc.elec_ac_ess_box_w , mfd_gc.elec_ac_ess_y);
		}
		// Bus Tie
		if (aircraft.ac_bus_tie()) {
			g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_aux_y, mfd_gc.elec_ac2_x, mfd_gc.elec_aux_y);
			g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_ac_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac1_x, mfd_gc.elec_aux_y);
			g2.drawLine(mfd_gc.elec_ac2_x, mfd_gc.elec_ac_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac2_x, mfd_gc.elec_aux_y);
		}
	}

	
	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.elec_page_legend_x;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}
	
	/*
	 * num : circuit number ( 0 = do not display )
	 */
	private void drawBusBox(Graphics2D g2, String bus_str, int num, boolean failed, int x, int y, int w, int h) {
		int str_x;
		// TODO : center text on box vertically
		int str_y = y + mfd_gc.line_height_xl;
        g2.setColor(mfd_gc.ecam_box_bg_color);
        g2.setFont(mfd_gc.font_xl);
		g2.fillRect(x, y, w, h);
		if (failed) {
			g2.setColor(mfd_gc.ecam_caution_color);
		} else {
			g2.setColor(mfd_gc.ecam_normal_color);
		}
		if (num == 0) {
			str_x = x+ w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, bus_str)/2;
			g2.drawString(bus_str, str_x, str_y);
		} else {
			String circuit_str = ""+num;
			str_x = x+ w/2 - (mfd_gc.get_text_width(g2, mfd_gc.font_xl, bus_str) + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, circuit_str)) /2;
			g2.drawString(bus_str, str_x, str_y);
			g2.setFont(mfd_gc.font_xxl);
			g2.drawString(circuit_str, str_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, bus_str), str_y);
		}
		
	}

    private void drawElecGen(Graphics2D g2, String bloc_str, boolean display_values, int load, int volt, int freq, int x, int y) {
    	// int w = mfd_gc.digit_width_m * 8;
    	// int h = mfd_gc.line_height_l * 9/2;
    	int w = mfd_gc.elec_gen_w;
    	int h = mfd_gc.elec_gen_h;
    	int line1 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl;
    	int line2 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*2;
    	int line3 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*3;
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawRect(x,y,w,h);
        if (display_values && ( freq<390 || volt < 105)) { g2.setColor(mfd_gc.ecam_caution_color); }
        g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, bloc_str)/2, y + mfd_gc.line_height_l );
        
        // Triangle
        /*
        int tri_x[] = { x + w/2 -w/15,x + w/2, x + w/2 + w/15 };
        int tri_y[] = { y-h/18, y-h/9, y-h/18 };
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawPolygon(tri_x, tri_y, 3);
        */
        
        // Legends
        g2.setColor(mfd_gc.ecam_action_color);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("%",  x+w-mfd_gc.digit_width_s*3, line1);
        g2.drawString("V",  x+w-mfd_gc.digit_width_s*3, line2);
        g2.drawString("HZ", x+w-mfd_gc.digit_width_s*3, line3);
        
        // Values
        if (display_values) {
        	String str_volt = ""+volt;
        	String str_freq = ""+freq;
        	String str_load = ""+load;
        	if (freq==0) { str_freq = "XX"; }
        	g2.setFont(mfd_gc.font_xxl);
        	g2.setColor(mfd_gc.ecam_normal_color);       
        	g2.drawString(str_load,  x+w-mfd_gc.digit_width_s*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_load), line1);
        	if (volt<105) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.drawString(str_volt,  x+w-mfd_gc.digit_width_s*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_volt), line2);
        	if (freq<390) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.drawString(str_freq, x+w-mfd_gc.digit_width_s*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_freq), line3);
        }
    }
	
    private void drawElecAuxOnBus(Graphics2D g2, int x, int top, int bottom, ElecBus bus) {
    	int h = mfd_gc.mfd_size * 26/1000;
    	int w = mfd_gc.mfd_size * 14/1000;
    	int tri_x[] = { x - w ,x, x + w };
    	int tri_y[] = { bottom-3, bottom-h, bottom-3 };
    	g2.setColor(mfd_gc.ecam_normal_color);
    	
		if (bus  == ElecBus.BUS_1) {
			g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_aux_y, x, mfd_gc.elec_aux_y);
			g2.drawLine(mfd_gc.elec_ac1_x, mfd_gc.elec_ac_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac1_x, mfd_gc.elec_aux_y);
		} else if (bus == ElecBus.BUS_2) {
			g2.drawLine(mfd_gc.elec_ac2_x, mfd_gc.elec_aux_y, x, mfd_gc.elec_aux_y);
			g2.drawLine(mfd_gc.elec_ac2_x, mfd_gc.elec_ac_box_y + mfd_gc.elec_bus_box_h, mfd_gc.elec_ac2_x, mfd_gc.elec_aux_y);
		}
    	if (bus != ElecBus.NONE) {
    		g2.drawPolygon(tri_x, tri_y, 3);
    		g2.drawLine(x, top, x, bottom-h);
    	}
    }
    
    private void drawElecAux(Graphics2D g2, String bloc_str, boolean display_values, int load, int volt, int freq, int x, int y) {
    	// int w = mfd_gc.digit_width_m * 8;
    	// int h = mfd_gc.line_height_l * 9/2;
    	int w = mfd_gc.elec_ext_apu_w;
    	int h = mfd_gc.elec_ext_apu_h;
    	int line1 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl;
    	int line2 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*2;
    	int line3 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*3;
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawRect(x,y,w,h);
        if (display_values && ( freq<390 || volt < 105)) { g2.setColor(mfd_gc.ecam_caution_color); }
        g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, bloc_str)/2, y + mfd_gc.line_height_l );
         
        // Legends
        g2.setColor(mfd_gc.ecam_action_color);
        g2.setFont(mfd_gc.font_s);
        // g2.drawString("%",  x+w-mfd_gc.digit_width_s*3, line1);
        g2.drawString("V",  x+w-mfd_gc.digit_width_s*3, line1);
        g2.drawString("HZ", x+w-mfd_gc.digit_width_s*3, line2);
        
        // Values
        if (display_values) {
        	String str_volt = ""+volt;
        	String str_freq = ""+freq;
        	String str_load = ""+load;
        	if (freq==0) { str_freq = "XX"; }
        	g2.setFont(mfd_gc.font_xxl);
        	g2.setColor(mfd_gc.ecam_normal_color);       
        	// g2.drawString(str_load,  x+w-mfd_gc.digit_width_s*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_load), line1);
        	if (volt<105) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.drawString(str_volt,  x+w-mfd_gc.digit_width_s*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_volt), line1);
        	if (freq<390) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.drawString(str_freq, x+w-mfd_gc.digit_width_s*4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_freq), line2);
        }
    }


    private void drawElecBatTr(Graphics2D g2, String bloc_str, boolean display_values, int volt, int amp, int x, int y) {
    	int w = mfd_gc.elec_bat_w;
    	int h = mfd_gc.elec_bat_h;
    	int line1 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl;
    	int line2 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*2;
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_l);
    	g2.drawRect(x,y,w,h);
    	// if (display_values && ( freq<390 || volt < 105)) { g2.setColor(mfd_gc.ecam_caution_color); }
    	g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, bloc_str)/2, y + mfd_gc.line_height_l );

    	// Legends
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.setFont(mfd_gc.font_s);
    	g2.drawString("V",  x+w-mfd_gc.digit_width_s*2, line1);
    	g2.drawString("A",  x+w-mfd_gc.digit_width_s*2, line2);

    	// Values
    	if (display_values) {
    		String str_volt = ""+volt;
    		String str_amp = ""+amp;
    		g2.setFont(mfd_gc.font_xxl);
    		if (volt<22) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
    		g2.drawString(str_volt,  x+w-mfd_gc.digit_width_s*3 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_volt) , line1);
    		g2.setColor(mfd_gc.ecam_normal_color);     
    		g2.drawString(str_amp,  x+w-mfd_gc.digit_width_s*3 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_amp), line2);
    	}
    }

    private void drawElecEmerTr(Graphics2D g2, String bloc_str, boolean display_values, int volt, int amp, int x, int y) {
    	int w = mfd_gc.elec_ess_tr_w;
    	int h = mfd_gc.elec_bat_h;
    	int line1 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl;
    	int line2 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*2;
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_l);
    	g2.drawRect(x,y,w,h);
    	// if (display_values && ( freq<390 || volt < 105)) { g2.setColor(mfd_gc.ecam_caution_color); }
    	g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, bloc_str)/2, y + mfd_gc.line_height_l );

    	// Legends
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.setFont(mfd_gc.font_s);
    	g2.drawString("V",  x+w-mfd_gc.digit_width_s*2, line1);
    	g2.drawString("A",  x+w-mfd_gc.digit_width_s*2, line2);

    	// Values
    	if (display_values) {
    		String str_volt = ""+volt;
    		String str_amp = ""+amp;
    		g2.setFont(mfd_gc.font_xxl);
    		if (volt<22) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
    		g2.drawString(str_volt,  x+w-mfd_gc.digit_width_s*3 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_volt) , line1);
    		g2.setColor(mfd_gc.ecam_normal_color);     
    		g2.drawString(str_amp,  x+w-mfd_gc.digit_width_s*3 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_amp), line2);
    	}
    }
    
    private void drawElecEmerGen(Graphics2D g2, String bloc_str, boolean display_values, int volt, int amp, int x, int y) {
    	int w = mfd_gc.elec_emerg_w;
    	int h = mfd_gc.elec_bat_h;
    	int line1 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl;
    	int line2 = y + mfd_gc.line_height_l + mfd_gc.line_height_xxl*2;
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_l);
    	g2.drawRect(x,y,w,h);
    	// if (display_values && ( freq<390 || volt < 105)) { g2.setColor(mfd_gc.ecam_caution_color); }
    	g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, bloc_str)/2, y + mfd_gc.line_height_l );

    	// Legends
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.setFont(mfd_gc.font_s);
    	g2.drawString("V",  x+w-mfd_gc.digit_width_s*2, line1);
    	g2.drawString("HZ",  x+w-mfd_gc.digit_width_s*2, line2);

    	// Values
    	if (display_values) {
    		String str_volt = ""+volt;
    		String str_amp = ""+amp;
    		g2.setFont(mfd_gc.font_xxl);
    		if (volt<105) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
    		g2.drawString(str_volt,  x+w-mfd_gc.digit_width_s*3 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_volt) , line2);
    		g2.setColor(mfd_gc.ecam_normal_color);     
    		g2.drawString(str_amp,  x+w-mfd_gc.digit_width_s*3 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_amp), line1);
    	}
    }
    
}

