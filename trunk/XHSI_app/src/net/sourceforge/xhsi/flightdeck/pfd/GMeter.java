/**
* Gmeter.java
* 
* Digital readout of G
* 
* Copyright (C) 2015  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.pfd;

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

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class GMeter extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public GMeter(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( pfd_gc.boeing_style && pfd_gc.powered && ! this.aircraft.on_ground() && this.preferences.get_pfd_draw_gmeter() ) {
            drawGmeter(g2);
        }
    }


    private void drawGmeter(Graphics2D g2) {


        DecimalFormat one_decimal_formatter = new DecimalFormat("0.0");
        DecimalFormatSymbols format_symbols = one_decimal_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_formatter.setDecimalFormatSymbols(format_symbols);

        float g = this.aircraft.g_load();
        String g_str = one_decimal_formatter.format(g) + "g";
        if ( (g > 6.0f) || (g < -3.0f) ) {
            g2.setColor(pfd_gc.warning_color);
        } else if ( (g > 3.8f) || (g < -1.52f) ) {
            g2.setColor(pfd_gc.caution_color);
        } else {
            g2.setColor(pfd_gc.normal_color);
        }
        g2.setFont(pfd_gc.font_s);
        g2.drawString(g_str, pfd_gc.adi_cx + pfd_gc.adi_size_right/4, pfd_gc.ra_high_y - pfd_gc.ra_r/2);

    }


}
