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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class LowerEcamCommon extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Stroke original_stroke;

    private boolean inhibit;

    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;

    public LowerEcamCommon(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
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
        
        if ( mfd_gc.powered && 
        	 mfd_gc.airbus_style && 
        	 this.avionics.get_mfd_mode() != Avionics.MFD_MODE_ARPT &&
        	 this.avionics.get_mfd_mode() != Avionics.MFD_MODE_FPLN  
        	) {
            
            // this.inhibit = ( this.aircraft.agl_m() < 1000.0f / 3.28084f );
        	this.inhibit = false;
 
            // Draw separation lines
            // Version 1 have 2 text lines
            // Version 2 have 3 text lines
            g2.setColor(mfd_gc.ecam_markings_color);
            g2.setFont(mfd_gc.font_l);
            g2.drawLine(mfd_gc.panel_rect.x, mfd_gc.ec_base_line,
            		    mfd_gc.panel_rect.x+mfd_gc.panel_rect.width, mfd_gc.ec_base_line);
            g2.drawLine(mfd_gc.ec_sep1, mfd_gc.ec_base_line,
            		mfd_gc.ec_sep1, mfd_gc.panel_rect.y + mfd_gc.panel_rect.height);
            g2.drawLine(mfd_gc.ec_sep2, mfd_gc.ec_base_line,
            		mfd_gc.ec_sep2, mfd_gc.panel_rect.y + mfd_gc.panel_rect.height);            
            
            // Temperatures (left)
            // line 1 : TAT
            g2.setFont(mfd_gc.font_l);
            g2.setColor(mfd_gc.ecam_markings_color);
            g2.drawString("TAT", mfd_gc.ec_col1, mfd_gc.ec_line1);
            String tat_str = "" + Math.round(this.aircraft.tat());
            g2.setColor(mfd_gc.ecam_normal_color);
            g2.drawString(tat_str, mfd_gc.ec_col1_val, mfd_gc.ec_line1);
            g2.setFont(mfd_gc.font_m);
            g2.setColor(mfd_gc.ecam_action_color);
            g2.drawString("°c", mfd_gc.ec_col1_unit, mfd_gc.ec_line1);
            
            // line 2 : SAT
            g2.setFont(mfd_gc.font_l);
            g2.setColor(mfd_gc.ecam_markings_color);
            g2.drawString("SAT", mfd_gc.ec_col1, mfd_gc.ec_line2);
            String oat_str = "" + Math.round(this.aircraft.oat());
            g2.setColor(mfd_gc.ecam_normal_color);
            g2.drawString(oat_str, mfd_gc.ec_col1_val, mfd_gc.ec_line2);
            g2.setFont(mfd_gc.font_m);
            g2.setColor(mfd_gc.ecam_action_color);
            g2.drawString("°c", mfd_gc.ec_col1_unit, mfd_gc.ec_line2);

            // line 3 : ISA (version 2 only)
            g2.setFont(mfd_gc.font_l);
            g2.setColor(mfd_gc.ecam_markings_color);
            g2.drawString("ISA", mfd_gc.ec_col1, mfd_gc.ec_line3);
            String isa_str = "" + Math.round(this.aircraft.isa());
            g2.setColor(mfd_gc.ecam_normal_color);
            g2.drawString(isa_str, mfd_gc.ec_col1_val, mfd_gc.ec_line3);
            g2.setFont(mfd_gc.font_m);
            g2.setColor(mfd_gc.ecam_action_color);
            g2.drawString("°c", mfd_gc.ec_col1_unit, mfd_gc.ec_line3);
            
            
            // Clock and G.Load (middle)            
            // G.Load inhibited on ground (Airbus AP_Phase > 1)
            // line 1 : G.Load
            String g_load_str = "G.LOAD  "+one_decimal_format.format(this.aircraft.g_load())+" g";
            if ((this.aircraft.g_load() > 1.4 || this.aircraft.g_load() < 0.7) && !inhibit) {
            	g2.setColor(mfd_gc.ecam_caution_color);
            	g2.drawString (g_load_str, mfd_gc.ec_col2_ctr - mfd_gc.get_text_width(g2, mfd_gc.font_l, g_load_str)/2, mfd_gc.ec_line1);
            }           
            
            // line 2 : Clock
            DecimalFormat hms_formatter = new DecimalFormat("00");
            int current_time = this.avionics.clock_shows_utc() ? (int)this.aircraft.sim_time_zulu() : (int)this.aircraft.sim_time_local();
            int hh = current_time / 3600;
            int mm = ( current_time / 60 ) % 60;
            
            String h_str = "XX";
            String m_str = "XX";
            g2.setFont(mfd_gc.font_l);
            g2.setColor(mfd_gc.ecam_caution_color);
            h_str = hms_formatter.format(hh);
            g2.setColor(mfd_gc.ecam_normal_color);           
            g2.drawString (h_str, mfd_gc.ec_col2_ctr - mfd_gc.digit_width_l*3, mfd_gc.ec_line2);
            g2.setFont(mfd_gc.font_m);
            g2.setColor(mfd_gc.ecam_action_color);
            g2.drawString ("H", mfd_gc.ec_col2_ctr, mfd_gc.ec_line2);
            g2.setColor(mfd_gc.ecam_caution_color);
            m_str = hms_formatter.format(mm);
            g2.setColor(mfd_gc.ecam_normal_color);
            g2.drawString (m_str, mfd_gc.ec_col2_ctr + mfd_gc.digit_width_l*2, mfd_gc.ec_line2);
                        
            // GW (right)
            // line 1 : GW
            g2.setFont(mfd_gc.font_l);
            g2.setColor(mfd_gc.ecam_markings_color);
            g2.drawString("GW", mfd_gc.ec_col3, mfd_gc.ec_line1);
            String gw_str = "XX";
            g2.setColor(mfd_gc.ecam_caution_color);
            gw_str=""+Math.round(this.aircraft.gross_weight());
            g2.setColor(mfd_gc.ecam_normal_color);
            g2.drawString(gw_str, mfd_gc.ec_col3_val, mfd_gc.ec_line1);
            g2.setFont(mfd_gc.font_m);
            g2.setColor(mfd_gc.ecam_action_color);
            g2.drawString("KG", mfd_gc.ec_col3_unit, mfd_gc.ec_line1);

            // line 2 : GW CG
        }

    }



    private void scalePen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }

	
}
