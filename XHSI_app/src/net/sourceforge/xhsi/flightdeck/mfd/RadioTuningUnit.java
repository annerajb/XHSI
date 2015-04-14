/**
* RadioTuningUnit.java
* 
* Displays the radio frequencies
* 
* Copyright (C) 2014  Marc Rogiers (marrog.123@gmail.com)
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
//import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Runway;



public class RadioTuningUnit extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private String xpdr_modes[] = { "OFF", "STBY", "ON", "TA", "TA/RA" };

    private DecimalFormat two_decimals_formatter;
    private DecimalFormat three_decimals_formatter;
    private DecimalFormatSymbols format_symbols;
    private DecimalFormat adf_formatter;
    private DecimalFormat xpdr_formatter;
     
    
    public RadioTuningUnit(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        two_decimals_formatter = new DecimalFormat("000.00");
        three_decimals_formatter = new DecimalFormat("000.000");
        format_symbols = two_decimals_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_formatter.setDecimalFormatSymbols(format_symbols);
        format_symbols = three_decimals_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        three_decimals_formatter.setDecimalFormatSymbols(format_symbols);
        adf_formatter = new DecimalFormat("0000");
        xpdr_formatter = new DecimalFormat("0000");

    }


    public void paint(Graphics2D g2) {
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_RTU ) ) {
            drawRadios(g2);
        }
    }


    private void drawRadios(Graphics2D g2) {

        int radio_select = this.avionics.rtu_selected_radio();
        
        g2.setColor(mfd_gc.dim_markings_color);
        g2.setFont(mfd_gc.font_xxl);

        int x_l = mfd_gc.panel_rect.x + mfd_gc.mfd_size*25/1000;
        int x_r = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width - mfd_gc.mfd_size/2 + mfd_gc.mfd_size*25/1000;
        int y_1 = mfd_gc.panel_rect.y + mfd_gc.mfd_size*50/1000;
        int y_2 = y_1 + mfd_gc.mfd_size/4;
        int y_3 = y_2 + mfd_gc.mfd_size/4;
        int y_4 = y_3 + mfd_gc.mfd_size/4;
        int x_w = mfd_gc.mfd_size*450/1000;
        int y_h = mfd_gc.mfd_size*175/1000;
        int r = mfd_gc.mfd_size*50/1000;
        
        Stroke original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.0f * mfd_gc.scaling_factor));

        // COM1 & 2
        g2.drawRoundRect(x_l, y_1, x_w, y_h, r, r);
        g2.drawRoundRect(x_r, y_1, x_w, y_h, r, r);

        // NAV1 & 2
        g2.drawRoundRect(x_l, y_2, x_w, y_h, r, r);
        g2.drawRoundRect(x_r, y_2, x_w, y_h, r, r);
        
        // ADF1 & 2
        g2.drawRoundRect(x_l, y_3, x_w, y_h, r, r);
        g2.drawRoundRect(x_r, y_3, x_w, y_h, r, r);
        
        // XPDR & TCAS
        g2.drawRoundRect(x_l, y_4, x_w, y_h, r, r);
        g2.drawRoundRect(x_r, y_4, x_w, y_h, r, r);
        
        g2.setStroke(original_stroke);

        int x_l_t = x_l + mfd_gc.mfd_size*75/1000;
        int x_r_t = x_r + mfd_gc.mfd_size*75/1000;
        int y_1_t = y_1 + mfd_gc.line_height_xxl*3/8;
        int y_2_t = y_1_t + mfd_gc.mfd_size/4;
        int y_3_t = y_2_t + mfd_gc.mfd_size/4;
        int y_4_t = y_3_t + mfd_gc.mfd_size/4;
        
        int char4_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, " UVW9 ");
        int char2_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, " XX ");
        int num3_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, "123");
        int num3dot_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, "123.");
        int num2_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, "45");
        int num_w = mfd_gc.digit_width_xxl;
        int num4_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, "7777");
        int line_h = mfd_gc.line_height_xxl;
        
        // COM1 & 2
        g2.clearRect(x_l_t, y_1-5, char4_w, 10);
        g2.clearRect(x_r_t, y_1-5, char4_w, 10);

        // NAV1 & 2
        g2.clearRect(x_l_t, y_2-5, char4_w, 10);
        g2.clearRect(x_r_t, y_2-5, char4_w, 10);

        // ADF1 & 2
        g2.clearRect(x_l_t, y_3-5, char4_w, 10);
        g2.clearRect(x_r_t, y_3-5, char4_w, 10);

        // XPDR & TCAS
        g2.clearRect(x_l_t, y_4-5, char4_w, 10);
        g2.clearRect(x_r_t, y_4-5, char4_w, 10);

        // COM1 & 2
        g2.drawString(" COM1 ", x_l_t, y_1_t);
        g2.drawString(" COM2 ", x_r_t, y_1_t);
        
        // NAV1 & 2
        g2.drawString(" NAV1 ", x_l_t, y_2_t);
        g2.drawString(" NAV2 ", x_r_t, y_2_t);

        // ADF1 & 2
        g2.drawString(" ADF1 ", x_l_t, y_3_t);
        g2.drawString(" ADF2 ", x_r_t, y_3_t);

        // XPDR & TCAS
        g2.drawString(" XPDR ", x_l_t, y_4_t);
        g2.drawString(" TCAS ", x_r_t, y_4_t);

        int x_l_fa = x_l + mfd_gc.mfd_size*30/1000;
        int x_l_fs = x_l + mfd_gc.mfd_size*235/1000;
        int x_r_fa = x_r + mfd_gc.mfd_size*35/1000;
        int x_r_fs = x_r + mfd_gc.mfd_size*230/1000;
        int y_1_f = y_1 + y_h/2 + mfd_gc.line_height_xxl*3/8;
        int y_2_f = y_1_f + mfd_gc.mfd_size/4;
        int y_3_f = y_2_f + mfd_gc.mfd_size/4;
        int y_4_f = y_3_f + mfd_gc.mfd_size/4;
        
        // COM1
        g2.setColor(mfd_gc.normal_color);
        if ( this.avionics.com1_is_833() ) {
            g2.drawString(three_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1_833)/1000.0f), x_l_fa, y_1_f);
        } else {
            g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1)/100.0f), x_l_fa, y_1_f);
        }
        g2.setColor(mfd_gc.dim_markings_color);
        if ( this.avionics.com1_standby_is_833() ) {
            g2.drawString(three_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1_STDBY_833)/1000.0f), x_l_fs, y_1_f);
        } else {
            g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1_STDBY)/100.0f), x_l_fs, y_1_f);
        }
        if ( radio_select == 1 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_l_fs - num_w/4, y_1_f - line_h, num3_w + num_w*3/8, line_h * 10/8);
            g2.drawRect(x_l_fs + num3dot_w - num_w/8, y_1_f - line_h, num3_w + num_w*3/8, line_h * 10/8);
        }

        // COM2
        g2.setColor(mfd_gc.normal_color);
        if ( this.avionics.com2_is_833() ) {
            g2.drawString(three_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM2_833)/1000.0f), x_r_fa, y_1_f);
        } else {
            g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM2)/100.0f), x_r_fa, y_1_f);
        }
        g2.setColor(mfd_gc.dim_markings_color);
        if ( this.avionics.com2_standby_is_833() ) {
            g2.drawString(three_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM2_STDBY_833)/1000.0f), x_r_fs, y_1_f);
        } else {
            g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM2_STDBY)/100.0f), x_r_fs, y_1_f);
        }
        if ( radio_select == 8 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_r_fs - num_w/4, y_1_f - line_h, num3_w + num_w*3/8, line_h * 10/8);
            g2.drawRect(x_r_fs + num3dot_w - num_w/4, y_1_f - line_h, num3_w + num_w*3/8, line_h * 10/8);
        }
        
        // NAV1
        g2.setColor(mfd_gc.normal_color);
        g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_NAV1)/100.0f), x_l_fa, y_2_f);
        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_NAV1_STDBY)/100.0f), x_l_fs, y_2_f);
        if ( radio_select == 2 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_l_fs - num_w/4, y_2_f - line_h, num3_w + num_w*3/8, line_h * 10/8);
            g2.drawRect(x_l_fs + num3dot_w - num_w/4, y_2_f - line_h, num2_w + num_w*3/8, line_h * 10/8);
        }

        // NAV2
        g2.setColor(mfd_gc.normal_color);
        g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_NAV2)/100.0f), x_r_fa, y_2_f);
        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawString(two_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_NAV2_STDBY)/100.0f), x_r_fs, y_2_f);
        if ( radio_select == 7 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_r_fs - num_w/4, y_2_f - line_h, num3_w + num_w*3/8, line_h * 10/8);
            g2.drawRect(x_r_fs + num3dot_w - num_w/4, y_2_f - line_h, num2_w + num_w*3/8, line_h * 10/8);
        }

        // ADF1
        g2.setColor(mfd_gc.normal_color);
        g2.drawString(adf_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF1)), x_l_fa, y_3_f);
        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawString(adf_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF1_STDBY)), x_l_fs, y_3_f);
        if ( radio_select == 3 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_l_fs - num_w/4, y_3_f - line_h, num2_w + num_w/4 - 1, line_h * 10/8);
            g2.drawRect(x_l_fs + num2_w, y_3_f - line_h, num2_w + num_w/4, line_h * 10/8);
        }

        // ADF2
        g2.setColor(mfd_gc.normal_color);
        g2.drawString(adf_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF2)), x_r_fa, y_3_f);
        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawString(adf_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_ADF2_STDBY)), x_r_fs, y_3_f);
        if ( radio_select == 6 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_r_fs - num_w/4, y_3_f - line_h, num2_w + num_w/4 - 1, line_h * 10/8);
            g2.drawRect(x_r_fs + num2_w, y_3_f - line_h, num2_w + num_w/4, line_h * 10/8);
        }

        // XPDR & TCAS
        g2.setColor(mfd_gc.normal_color);
        int code = this.avionics.transponder_code();
        String code_str = "" + (code / 1000 ) + " " + (code / 100 % 10) + " " + (code / 10 % 10) + " " + (code % 10);
        int mode = this.avionics.transponder_mode();
        String mode_str = xpdr_modes[mode];
        if ( mode > 1 )
            g2.setColor(mfd_gc.normal_color);
        else
            g2.setColor(mfd_gc.dim_markings_color);
        g2.drawString(code_str, x_l_fa, y_4_f);
        g2.drawString(mode_str, x_r_fa, y_4_f);
        if ( radio_select == 4 ) {
            g2.setColor(mfd_gc.unusual_color);
            int code_w = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, code_str);
            g2.drawRect(x_l_fa - num_w/2, y_4_f - line_h, code_w/2 + num_w/2 - 1, line_h * 10/8);
            g2.drawRect(x_l_fa + code_w/2 + 1, y_4_f - line_h, code_w/2 + num_w/2 - 1, line_h * 10/8);
        }
        if ( radio_select == 5 ) {
            g2.setColor(mfd_gc.unusual_color);
            g2.drawRect(x_r_fa - num_w/4, y_4_f - line_h, mfd_gc.get_text_width(g2, mfd_gc.font_xxl, mode_str) + num_w/2, line_h * 10/8);
        }
        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawString(this.aircraft.aircraft_registration(), x_r_fs, y_4_f);
        
        g2.setColor(mfd_gc.unusual_color);
        int activity_x = x_l + mfd_gc.mfd_size*275/1000;

        // TX light
        if ( this.avionics.contact_atc() ) {
            g2.clearRect(activity_x, y_1-5, char2_w, 10);
            g2.drawString(" TX ", activity_x, y_1_t);
        }

        // ID light
        if ( this.avionics.transponder_ident() && ( mode > 1 ) ) {
            g2.clearRect(activity_x, y_4-5, char2_w, 10);
            g2.drawString(" ID ", activity_x, y_4_t);
        }

    }
    
    
}
