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
import java.awt.Color;
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

    private DecimalFormat one_decimal_formatter;
    private DecimalFormat two_decimals_formatter;
    private DecimalFormat three_decimals_formatter;
    private DecimalFormatSymbols format_symbols;
     
    
    public RadioTuningUnit(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_formatter = new DecimalFormat("0.0");
        two_decimals_formatter = new DecimalFormat("000.00");
        three_decimals_formatter = new DecimalFormat("000.000");
        format_symbols = one_decimal_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_formatter.setDecimalFormatSymbols(format_symbols);
        two_decimals_formatter.setDecimalFormatSymbols(format_symbols);
        three_decimals_formatter.setDecimalFormatSymbols(format_symbols);

    }


    public void paint(Graphics2D g2) {
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_RTU ) ) {
            drawRadios(g2);
        }
    }


    private void drawRadios(Graphics2D g2) {

        int x1 = mfd_gc.mfd_size/4;
        int x2 = x1 + 8*mfd_gc.digit_width_xxl;
        int y1 = mfd_gc.mfd_size/4;
        g2.setColor(mfd_gc.markings_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawString("COM1", x1, y1);
        y1 += mfd_gc.line_height_xxl;
        g2.setFont(mfd_gc.font_xxl);
        g2.drawString(three_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1_833)/1000.0f), x1, y1);
        g2.drawString(three_decimals_formatter.format(this.avionics.get_radio_freq(Avionics.RADIO_COM1_STDBY_833)/1000.0f), x2, y1);

    }
    
    
}
