/**
* LowerEicas.java
* 
* Lower EICAS and ECAM
* SYSTEM page on Boeing
* CRUIZE page on Airbus
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;

public class CruizeSystems extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;
    
	public CruizeSystems(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
        one_decimal_format = new DecimalFormat("##0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);

        two_decimals_format = new DecimalFormat("#0.00");
        format_symbols = two_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_format.setDecimalFormatSymbols(format_symbols);

	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_SYS) {
			// Page ID
			drawPageID(g2, "ENGINE", mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10);
			drawPageID(g2, "AIR", mfd_gc.panel_rect.x, mfd_gc.crz_air_legend_y);
			drawCabin(g2);
			drawCabinPressures(g2);
			drawLines(g2);
			drawEngineVib(g2);
			drawFuelUsed(g2);
			drawOilQuantity(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str, int page_id_x, int page_id_y) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_xxl/8);
	}

	private void drawLines(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.crz_line_x, mfd_gc.crz_line_top_y, mfd_gc.crz_line_x, mfd_gc.crz_line_bottom_y);
	}
	
	private void drawFuelUsed(Graphics2D g2) {
		String str_fuel_legend = "F. USED";
		String str_fuel_units = "KG";
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx1, mfd_gc.crz_fuel_top_y, mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx2, mfd_gc.crz_fuel_bottom_y);
		g2.drawLine(mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx1, mfd_gc.crz_fuel_top_y, mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx2, mfd_gc.crz_fuel_bottom_y);
	
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_fuel_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_legend)/2, mfd_gc.crz_fuel_legend_y);
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_fuel_units, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_units)/2, mfd_gc.crz_fuel_units_y);

		// Values
		String str_fuel_val="XX";
		g2.setFont(mfd_gc.font_xxl);
		g2.setColor(mfd_gc.ecam_caution_color);
		if (mfd_gc.num_eng > 0 ) {
			g2.drawString(str_fuel_val, mfd_gc.crz_eng_x[0]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_fuel_val), mfd_gc.crz_fuel_value_y);
		}
		if (mfd_gc.num_eng > 1 ) {
			g2.drawString(str_fuel_val, mfd_gc.crz_eng_x[1]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_fuel_val), mfd_gc.crz_fuel_value_y);
		}
	}

	private void drawOilQuantity(Graphics2D g2) {
		String str_oil_legend = "OIL";
		String str_oil_units = "QT";
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx1, mfd_gc.crz_oil_top_y, mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx2, mfd_gc.crz_oil_bottom_y);
		g2.drawLine(mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx1, mfd_gc.crz_oil_top_y, mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx2, mfd_gc.crz_oil_bottom_y);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_oil_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_oil_legend)/2, mfd_gc.crz_oil_legend_y);
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_oil_units, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_oil_units)/2, mfd_gc.crz_oil_units_y);
		
		// Values
		String str_oil_val="XX";
		g2.setFont(mfd_gc.font_xxl);
		g2.setColor(mfd_gc.ecam_normal_color);
		if (mfd_gc.num_eng > 0 ) {
			str_oil_val = one_decimal_format.format(this.aircraft.get_oil_quant_ratio(0)*100);
			g2.drawString(str_oil_val, mfd_gc.crz_eng_x[0]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_oil_val), mfd_gc.crz_oil_value_y);
		}
		if (mfd_gc.num_eng > 1 ) {
			str_oil_val = one_decimal_format.format(this.aircraft.get_oil_quant_ratio(1)*100);
			g2.drawString(str_oil_val, mfd_gc.crz_eng_x[1]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_oil_val), mfd_gc.crz_oil_value_y);
		}
		
	}

	
	private void drawEngineVib(Graphics2D g2) {
		String vib_n1_legend = "VIB  (N1)";
		String vib_n2_legend = "VIB  (N2)";
		String str_vib_val = "";
		// T
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.crz_vib_center_x - mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n1_t_y, mfd_gc.crz_vib_center_x + mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n1_t_y);
		g2.drawLine(mfd_gc.crz_vib_center_x - mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n2_t_y, mfd_gc.crz_vib_center_x + mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n2_t_y);
		g2.drawLine(mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n1_t_y, mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n1_t_y  + mfd_gc.crz_vib_t_dy);
		g2.drawLine(mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n2_t_y, mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n2_t_y  + mfd_gc.crz_vib_t_dy);
		// Legends
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(vib_n1_legend, mfd_gc.crz_vib_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n1_legend)/2, mfd_gc.crz_vib_n1_legend_y);
		g2.drawString(vib_n2_legend, mfd_gc.crz_vib_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n2_legend)/2, mfd_gc.crz_vib_n2_legend_y);
		// Values
		g2.setFont(mfd_gc.font_xxl);
		g2.setColor(mfd_gc.ecam_normal_color);
		if (mfd_gc.num_eng > 0 ) {
			str_vib_val = one_decimal_format.format(this.aircraft.get_vib(0)/10);
			g2.drawString(str_vib_val, mfd_gc.crz_vib_x[0]-mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_vib_val), mfd_gc.crz_vib_n1_value_y);
			g2.drawString(str_vib_val, mfd_gc.crz_vib_x[0]-mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_vib_val), mfd_gc.crz_vib_n2_value_y);
		}

		if (mfd_gc.num_eng > 1 ) {
			str_vib_val = one_decimal_format.format(this.aircraft.get_vib(1)/10);
			g2.drawString(str_vib_val, mfd_gc.crz_vib_x[1]-mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_vib_val), mfd_gc.crz_vib_n1_value_y);
			g2.drawString(str_vib_val, mfd_gc.crz_vib_x[1]-mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_vib_val), mfd_gc.crz_vib_n2_value_y);
		}

		if (mfd_gc.num_eng > 2 ) {
			String str_vib_warn = "DISP. LIMITED";
			g2.setColor(mfd_gc.ecam_caution_color);
			g2.setFont(mfd_gc.font_l);
			g2.drawString(str_vib_warn, mfd_gc.crz_vib_center_x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_vib_warn)/2 ,
					mfd_gc.crz_vib_n1_legend_y - mfd_gc.line_height_l);
		}
	}
	
	private void drawCabinPressures(Graphics2D g2) {
		// Legends
		String str_ldg_legend = "LDG  ELEV";
		String str_delta_p_legend = "ΔP";
		String str_vs_legend = "CAB  V/S";
		String str_alt_legend = "CAB  ALT";
		// Units
		String str_delta_p_units = "PSI";
		String str_vs_units = "FT/MN";
		String str_alt_units = "FT";
		// Values
		String str_alt = "" + Math.round(aircraft.cabin_altitude()/10)*10;
		String str_delta = one_decimal_format.format(aircraft.cabin_delta_p());
		String str_vs = "" + Math.round(aircraft.cabin_vs());
		String str_mode = "AUTO";
		String str_ldg = "XX";

	    // Draw legends
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_ldg_legend, mfd_gc.crz_ldg_legend_x, mfd_gc.crz_ldg_mode_y);
		g2.drawString(str_delta_p_legend, mfd_gc.crz_delta_p_legend_x, mfd_gc.crz_delta_p_y);
		g2.drawString(str_vs_legend , mfd_gc.crz_pres_legend_x, mfd_gc.crz_vs_legend_y);
		g2.drawString(str_alt_legend , mfd_gc.crz_pres_legend_x, mfd_gc.crz_alt_legend_y);

		// Draw units
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_alt_units, mfd_gc.crz_pres_units_x, mfd_gc.crz_ldg_mode_y);
		g2.drawString(str_delta_p_units, mfd_gc.crz_delta_p_units_x, mfd_gc.crz_delta_p_y);
		g2.drawString(str_vs_units, mfd_gc.crz_pres_units_x, mfd_gc.crz_vs_legend_y);
		g2.drawString(str_alt_units, mfd_gc.crz_pres_units_x, mfd_gc.crz_alt_legend_y);
		
		// Draw values
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.setFont(mfd_gc.font_xxl);
		g2.drawString(str_mode, mfd_gc.crz_ldg_mode_x, mfd_gc.crz_ldg_mode_y);
		g2.drawString(str_delta, mfd_gc.crz_cab_zone2_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_delta) , mfd_gc.crz_delta_p_y);
		g2.drawString(str_vs, mfd_gc.crz_pres_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_vs), mfd_gc.crz_vs_value_y);
		g2.drawString(str_alt, mfd_gc.crz_pres_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_alt) , mfd_gc.crz_alt_value_y);
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.drawString(str_ldg, mfd_gc.crz_pres_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_ldg), mfd_gc.crz_ldg_mode_y);
		
	}
	
    private void drawCabin(Graphics2D g2) {
    	// Cabin body
    	g2.setColor(mfd_gc.ecam_markings_color.darker());
    	g2.drawLine(mfd_gc.crz_cab_front_x, mfd_gc.crz_cab_top_y, mfd_gc.crz_cab_aft_x, mfd_gc.crz_cab_top_y);
    	g2.drawLine(mfd_gc.crz_cab_noose_front_x, mfd_gc.crz_cab_bottom_y, mfd_gc.crz_cab_aft_x, mfd_gc.crz_cab_bottom_y);
    	g2.drawLine(mfd_gc.crz_cab_zone1_x, mfd_gc.crz_cab_top_y, mfd_gc.crz_cab_zone1_x, mfd_gc.crz_cab_bottom_y);
    	g2.drawLine(mfd_gc.crz_cab_zone2_x, mfd_gc.crz_cab_zone2_y, mfd_gc.crz_cab_zone2_x, mfd_gc.crz_cab_bottom_y);
    	g2.drawArc(mfd_gc.crz_cab_cockpit_x, mfd_gc.crz_cab_top_y, (mfd_gc.crz_cab_front_x - mfd_gc.crz_cab_cockpit_x)*2, (mfd_gc.crz_cab_cockpit_y - mfd_gc.crz_cab_top_y)*2, 90, 30);
    	g2.drawArc(mfd_gc.crz_cab_noose_x, mfd_gc.crz_cab_cockpit_y, (mfd_gc.crz_cab_noose_front_x - mfd_gc.crz_cab_noose_x )*2, (mfd_gc.crz_cab_bottom_y - mfd_gc.crz_cab_cockpit_y), -90,-156);
    	// g2.drawArc(mfd_gc.crz_cab_aft_x-mfd_gc.crz_cab_aft_dx, mfd_gc.crz_cab_top_y, mfd_gc.crz_cab_aft_dx*2, (mfd_gc.crz_cab_bottom_y - mfd_gc.crz_cab_top_y), -90, 45);
    	g2.drawArc(mfd_gc.crz_cab_aft_x-mfd_gc.crz_cab_aft_dx, mfd_gc.crz_cab_top_y, mfd_gc.crz_cab_aft_dx*2, (mfd_gc.crz_cab_bottom_y - mfd_gc.crz_cab_top_y), 45, 45);

    	g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xl);
    	String legend_str = "CKPT ";
        g2.drawString(legend_str, mfd_gc.crz_cab_gauge1_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.crz_cab_temp_legend_y);
    	legend_str = "FWD ";
        g2.drawString(legend_str, mfd_gc.crz_cab_gauge2_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.crz_cab_temp_legend_y);
    	legend_str = "AFT ";
        g2.drawString(legend_str, mfd_gc.crz_cab_gauge3_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str), mfd_gc.crz_cab_temp_legend_y);

        // Units
        String unit_str="°C";
    	g2.setColor(mfd_gc.ecam_action_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawString(unit_str, mfd_gc.crz_cab_zone2_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, unit_str)/2 , mfd_gc.crz_cab_temp_legend_y-mfd_gc.line_height_xxl/5);
        
        // cabin air temperatures
        g2.setColor(mfd_gc.ecam_caution_color);
        String value_str="XX";
        g2.setFont(mfd_gc.font_xxl);
        g2.drawString(value_str, mfd_gc.crz_cab_gauge1_x - mfd_gc.line_height_xxl/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, value_str), mfd_gc.crz_cab_temp_y);
        g2.drawString(value_str, mfd_gc.crz_cab_gauge2_x - mfd_gc.line_height_xxl/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, value_str), mfd_gc.crz_cab_temp_y);
        g2.drawString(value_str, mfd_gc.crz_cab_gauge3_x - mfd_gc.line_height_xxl/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, value_str), mfd_gc.crz_cab_temp_y);
       
    }


}
