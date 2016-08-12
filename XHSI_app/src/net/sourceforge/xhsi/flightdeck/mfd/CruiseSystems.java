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
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Aircraft.CabinZone;
import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;


public class CruiseSystems extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;
    
	public CruiseSystems(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
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
			if (mfd_gc.num_eng<3) {
				drawPageID(g2, "ENGINE", mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10);
				drawPageID(g2, "AIR", mfd_gc.panel_rect.x, mfd_gc.crz_air_legend_y);
				drawLines(g2);
				drawEngineVib(g2);
				drawFuelUsed(g2);
				drawOilQuantity(g2);
			} else {
				drawPageID(g2, "CRUISE", mfd_gc.mfd_middle_x, mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10);
				drawPageID(g2, "ENG", mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 21/10);
				drawPageID(g2, "AIR", mfd_gc.panel_rect.x, mfd_gc.crz_air_legend_y);
		
				drawEngineVib(g2);				
				drawFuelUsed(g2);
				drawOilQuantity(g2);
								
			}
			drawCabin(g2);
			drawCabinPressures(g2);
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
		
		String str_fuel_units;
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) str_fuel_units = XHSIPreferences.FUEL_UNITS_KG;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) str_fuel_units = XHSIPreferences.FUEL_UNITS_LBS;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) str_fuel_units = XHSIPreferences.FUEL_UNITS_USG;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ str_fuel_units = XHSIPreferences.FUEL_UNITS_LTR;
	
		g2.setColor(mfd_gc.ecam_markings_color);
		if (mfd_gc.num_eng<3) {
			g2.drawLine(mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx1, mfd_gc.crz_fuel_top_y, mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx2, mfd_gc.crz_fuel_bottom_y);
			g2.drawLine(mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx1, mfd_gc.crz_fuel_top_y, mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx2, mfd_gc.crz_fuel_bottom_y);
		}
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_fuel_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_legend)/2, mfd_gc.crz_fuel_legend_y);
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_fuel_units, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_units)/2, mfd_gc.crz_fuel_units_y);

		// Values
		String str_fuel_val="XX";
		g2.setFont(mfd_gc.font_xxl);
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {		
			if (this.aircraft.fuel_used(eng) > 0.1f ) {
				g2.setColor(mfd_gc.ecam_normal_color);					
				str_fuel_val=""+Math.round(this.aircraft.fuel_used(eng) * this.aircraft.fuel_multiplier());
			} else {
				str_fuel_val="XX";
				g2.setColor(mfd_gc.ecam_caution_color);
			}
			g2.drawString(str_fuel_val, mfd_gc.crz_eng_x[eng]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_fuel_val), mfd_gc.crz_fuel_value_y);
		}
		
	}

	private void drawOilQuantity(Graphics2D g2) {
		String str_oil_legend = "OIL";
		String str_oil_units = "QT";
		g2.setColor(mfd_gc.ecam_markings_color);
		if (mfd_gc.num_eng<3) {
			g2.drawLine(mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx1, mfd_gc.crz_oil_top_y, mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx2, mfd_gc.crz_oil_bottom_y);
			g2.drawLine(mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx1, mfd_gc.crz_oil_top_y, mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx2, mfd_gc.crz_oil_bottom_y);
		}
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_oil_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_oil_legend)/2, mfd_gc.crz_oil_legend_y);
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_oil_units, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_oil_units)/2, mfd_gc.crz_oil_units_y);
		
		// Values
		String str_oil_val="XX";
		g2.setFont(mfd_gc.font_xxl);
		g2.setColor(mfd_gc.ecam_normal_color);
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {			
			str_oil_val = one_decimal_format.format(this.aircraft.get_oil_quant_ratio(eng)*100);
			// g2.drawString(str_oil_val, mfd_gc.crz_eng_x[eng]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_oil_val), mfd_gc.crz_oil_value_y);
			drawStringSmallOneDecimal(g2, mfd_gc.crz_eng_x[eng]+mfd_gc.digit_width_xxl, mfd_gc.crz_oil_value_y,mfd_gc.font_xxl,mfd_gc.font_xl, this.aircraft.get_oil_quant_ratio(eng)*100);
		}
		
	}

	
	private void drawEngineVib(Graphics2D g2) {
		String vib_n1_legend = "VIB  (N1)";
		String vib_n2_legend = "VIB  (N2)";
		String str_vib_val = "";
		
		// T
		if (mfd_gc.num_eng<3) {
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.drawLine(mfd_gc.crz_vib_center_x - mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n1_t_y, mfd_gc.crz_vib_center_x + mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n1_t_y);
			g2.drawLine(mfd_gc.crz_vib_center_x - mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n2_t_y, mfd_gc.crz_vib_center_x + mfd_gc.crz_vib_t_dx, mfd_gc.crz_vib_n2_t_y);
			g2.drawLine(mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n1_t_y, mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n1_t_y  + mfd_gc.crz_vib_t_dy);
			g2.drawLine(mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n2_t_y, mfd_gc.crz_vib_center_x, mfd_gc.crz_vib_n2_t_y  + mfd_gc.crz_vib_t_dy);
		}
		
		// Legends
		g2.setFont(mfd_gc.font_xl);
		if (mfd_gc.num_eng>2) {
			vib_n1_legend = "VIB  N1";
			vib_n2_legend = "      N2";
		}
		g2.drawString(vib_n1_legend, mfd_gc.crz_vib_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n1_legend)/2, mfd_gc.crz_vib_n1_legend_y);
		g2.drawString(vib_n2_legend, mfd_gc.crz_vib_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n2_legend)/2, mfd_gc.crz_vib_n2_legend_y);

		// Values
		g2.setFont(mfd_gc.font_xxl);
		g2.setColor(mfd_gc.ecam_normal_color);
		str_vib_val="XX";
		g2.setFont(mfd_gc.font_xxl);
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			str_vib_val = one_decimal_format.format(this.aircraft.get_vib(eng)/10);
			drawStringSmallOneDecimal(g2, mfd_gc.crz_vib_x[eng]+mfd_gc.digit_width_xxl, mfd_gc.crz_vib_n1_value_y,mfd_gc.font_xxl,mfd_gc.font_xl, this.aircraft.get_vib(eng)/10 );
			drawStringSmallOneDecimal(g2, mfd_gc.crz_vib_x[eng]+mfd_gc.digit_width_xxl, mfd_gc.crz_vib_n2_value_y,mfd_gc.font_xxl,mfd_gc.font_xl, this.aircraft.get_vib_n2(eng)/10 );
		}
	}
	
	private void drawCabinPressures(Graphics2D g2) {
		/*
		 * A320 FCOM 1.21.20 page 12
		 * 
		 * Cabin V/S amber when >= 2000 feet/minute
		 * Pulse when > 1800 feet/minute, reset at 1600 feet/minute
		 * 
		 * V/S is a gauge when in MAN mode
		 * 
		 * Cabin Alt in red if >= 9550 feet
		 * Cabin Alt Pulse if >= 8800 feet (reset at 8600 feet)
		 * 
		 * Delta P : amber if dp <= -0.4 psi or if dp >= 8.5 psi
		 */
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
		String str_vs = "" + Math.round(aircraft.cabin_vs()/10)*10;
		String str_mode = "AUTO";
		// String str_ldg = ""+Math.round(get_auto_ldg_elevation());
		
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
		// g2.drawString(str_ldg, mfd_gc.crz_pres_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_ldg), mfd_gc.crz_ldg_mode_y);
		drawAutoLDGElevation(g2);
		
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
        drawCabinTemp(g2, mfd_gc.crz_cab_gauge1_x, mfd_gc.crz_cab_temp_y, this.aircraft.cabin_temp(CabinZone.COCKPIT));
        drawCabinTemp(g2, mfd_gc.crz_cab_gauge2_x, mfd_gc.crz_cab_temp_y, this.aircraft.cabin_temp(CabinZone.FORWARD));
        drawCabinTemp(g2, mfd_gc.crz_cab_gauge3_x, mfd_gc.crz_cab_temp_y, this.aircraft.cabin_temp(CabinZone.AFT));
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
    
    private void drawStringSmallOneDecimal(Graphics2D g2, int x, int y, Font normalFont, Font smallFont, float value) {
    	// Value, decimal part in smaller font
    	// Justify Right
    	String valueStr =  one_decimal_format.format(value);
    	g2.setFont(normalFont);
    	String intStr = valueStr.substring(0, valueStr.length()-2);
    	String decStr = valueStr.substring(valueStr.length()-2,valueStr.length());
    	int len_n1_str1 = mfd_gc.get_text_width(g2, normalFont, intStr);
    	int len_n1_str2 = mfd_gc.get_text_width(g2, smallFont, decStr);
    	g2.drawString(intStr, x - len_n1_str2 - len_n1_str1, y);
    	g2.setFont(smallFont);
    	g2.drawString(decStr, x - len_n1_str2, y);
    }
    
    /* 
     * Functions from Destination Airport Class 
     */
    
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
		g2.drawString(str_ldg, mfd_gc.crz_pres_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_ldg), mfd_gc.crz_ldg_mode_y);
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

}
