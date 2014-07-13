/**
* Controls.java
* 
* Draw the position of trim, gear, flaps, autobrake, spoilers, etc...
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
package net.sourceforge.xhsi.flightdeck.eicas;

//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
//import java.awt.Stroke;
import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.ModelFactory;



public class Controls extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private DecimalFormat one_decimal_format;
    private DecimalFormatSymbols format_symbols;

    
    public Controls(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_format = new DecimalFormat("#0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);
        
    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && this.preferences.get_eicas_primary_only() && this.preferences.get_eicas_draw_controls() ) {

//            g2.setColor(eicas_gc.color_boeingcyan);
//            g2.drawRect(eicas_gc.controls_x, eicas_gc.controls_y, eicas_gc.controls_w, eicas_gc.controls_h);
//            g2.drawLine(eicas_gc.controls_x, eicas_gc.controls_y + eicas_gc.controls_h/2, eicas_gc.controls_x + eicas_gc.controls_w, eicas_gc.controls_y + eicas_gc.controls_h/2);

            draw_trim(g2);
            
        }

    }

    
    private void draw_trim(Graphics2D g2) {
        
        int pitch = Math.round( this.aircraft.get_pitch_trim() * 100.0f );
        int roll = Math.round( this.aircraft.get_roll_trim() * 100.0f );
        int yaw = Math.round( this.aircraft.get_yaw_trim() * 100.0f );
        
        AffineTransform original_at = g2.getTransform();
        
        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_s);
        g2.drawString("ROLL", eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.get_text_width(g2, eicas_gc.font_s, "ROLL")/2, eicas_gc.lat_trim_y - eicas_gc.line_height_s);

        g2.setColor(eicas_gc.markings_color);
        g2.drawArc(eicas_gc.lat_trim_x - 1, eicas_gc.lat_trim_y - 1, eicas_gc.lat_trim_w + 2, eicas_gc.lat_trim_h + 2, 45, 90);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y, eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h*8/100);
        g2.rotate(Math.toRadians(45), eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h/2);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y, eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h*8/100);
        g2.rotate(Math.toRadians(-90), eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h/2);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y, eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h*8/100);
        g2.setTransform(original_at);
        g2.rotate(Math.toRadians(23), eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h/2);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y, eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h*4/100);
        g2.rotate(Math.toRadians(-46), eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h/2);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y, eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h*4/100);
        g2.setTransform(original_at);

        int[] roll_triangle_x = {
            eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2,
            eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.lat_trim_w*4/100,
            eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*4/100
        };
        int[] roll_triangle_y = {
            eicas_gc.lat_trim_y,
            eicas_gc.lat_trim_y + eicas_gc.lat_trim_w*8/100,
            eicas_gc.lat_trim_y + eicas_gc.lat_trim_w*8/100
        };
        g2.rotate(Math.toRadians(roll*45/100), eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2, eicas_gc.lat_trim_y + eicas_gc.lat_trim_h/2);
        g2.setColor(eicas_gc.color_lime);
        g2.drawPolygon(roll_triangle_x, roll_triangle_y, 3);
        g2.setTransform(original_at);
        
        
        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_s);
        g2.drawString("YAW", eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.get_text_width(g2, eicas_gc.font_s, "YAW")/2, eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*8/100 - eicas_gc.line_height_s);

        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.lat_trim_w*30/100, eicas_gc.yaw_trim_y + 1, eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*30/100, eicas_gc.yaw_trim_y + 1);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2,                              eicas_gc.yaw_trim_y,     eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2,                              eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*8/100);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.lat_trim_w*30/100, eicas_gc.yaw_trim_y,     eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.lat_trim_w*30/100, eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*8/100);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*30/100, eicas_gc.yaw_trim_y,     eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*30/100, eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*8/100);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.lat_trim_w*15/100, eicas_gc.yaw_trim_y,     eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 - eicas_gc.lat_trim_w*15/100, eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*4/100);
        g2.drawLine(eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*15/100, eicas_gc.yaw_trim_y,     eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*15/100, eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*4/100);

        int[] yaw_triangle_x = {
            eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*yaw*30/100/100,
            eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*yaw*30/100/100 - eicas_gc.lat_trim_w*4/100,
            eicas_gc.lat_trim_x + eicas_gc.lat_trim_w/2 + eicas_gc.lat_trim_w*yaw*30/100/100 + eicas_gc.lat_trim_w*4/100
        };
        int[] yaw_triangle_y = {
            eicas_gc.yaw_trim_y,
            eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*8/100,
            eicas_gc.yaw_trim_y - eicas_gc.lat_trim_h*8/100
        };
        g2.setColor(eicas_gc.color_lime);
        g2.drawPolygon(yaw_triangle_x, yaw_triangle_y, 3);

        
        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_s);
        g2.drawString("PITCH", eicas_gc.pitch_trim_x, eicas_gc.pitch_trim_y - eicas_gc.line_height_s);

        g2.setColor(eicas_gc.markings_color);
        g2.drawLine(eicas_gc.pitch_trim_x - 1, eicas_gc.pitch_trim_y,                                                     eicas_gc.pitch_trim_x - 1,                           eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h);
        g2.drawLine(eicas_gc.pitch_trim_x    , eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2,                           eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*8/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2);
        g2.drawLine(eicas_gc.pitch_trim_x    , eicas_gc.pitch_trim_y,                                                     eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*8/100, eicas_gc.pitch_trim_y);
        g2.drawLine(eicas_gc.pitch_trim_x    , eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h,                             eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*8/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h);
        g2.drawLine(eicas_gc.pitch_trim_x    , eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 - eicas_gc.pitch_trim_h/4, eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*4/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 - eicas_gc.pitch_trim_h/4);
        g2.drawLine(eicas_gc.pitch_trim_x    , eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.pitch_trim_h/4, eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*4/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.pitch_trim_h/4);
        
        int[] pitch_triangle_x = {
            eicas_gc.pitch_trim_x,
            eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*8/100,
            eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*8/100
        };
        int[] pitch_triangle_y = {
            eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.pitch_trim_h*pitch/200,
            eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.pitch_trim_h*pitch/200 - eicas_gc.pitch_trim_w*4/100,
            eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.pitch_trim_h*pitch/200 + eicas_gc.pitch_trim_w*4/100
        };
        g2.setColor(eicas_gc.color_lime);
        g2.drawPolygon(pitch_triangle_x, pitch_triangle_y, 3);

        g2.setColor(eicas_gc.color_lime);
        g2.drawString(one_decimal_format.format(Math.abs(pitch/10.0f)), eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*24/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.line_height_s*3/8);
        if ( pitch > 0 ) g2.drawString("UP", eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*24/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.line_height_s*3/8 + eicas_gc.line_height_s);
        if ( pitch < 0 ) g2.drawString("DN", eicas_gc.pitch_trim_x + eicas_gc.pitch_trim_w*24/100, eicas_gc.pitch_trim_y + eicas_gc.pitch_trim_h/2 + eicas_gc.line_height_s*3/8 - eicas_gc.line_height_s);
        
    }

}
