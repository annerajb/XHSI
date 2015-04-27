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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class CabinPressure extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;

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

	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_CAB_PRESS) {
			// Page ID
			drawPageID(g2, "CABIN PRESSURE");
			drawPressures(g2);
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
	

}
