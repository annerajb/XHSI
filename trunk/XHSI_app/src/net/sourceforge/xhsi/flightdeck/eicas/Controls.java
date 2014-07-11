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
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.ModelFactory;



public class Controls extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public Controls(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && this.preferences.get_eicas_primary_only() && this.preferences.get_eicas_draw_controls() ) {

            g2.setColor(eicas_gc.color_boeingcyan);
            g2.drawRect(eicas_gc.controls_x, eicas_gc.controls_y, eicas_gc.controls_w, eicas_gc.controls_h);

            draw_trim(g2);
            
        }

    }

    
    private void draw_trim(Graphics2D g2) {
        
        float pitch = this.aircraft.get_pitch_trim();
        float roll = this.aircraft.get_roll_trim();
        float yaw = this.aircraft.get_yaw_trim();
        
        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_xs);
        g2.drawString("PITCH : " + Integer.toString(Math.round(pitch*100.0f)), eicas_gc.controls_x + eicas_gc.controls_w*5/10, eicas_gc.controls_y + eicas_gc.controls_h*5/10);
        g2.drawString("ROLL  : " + Integer.toString(Math.round(roll*100.0f)), eicas_gc.controls_x + eicas_gc.controls_w*5/10, eicas_gc.controls_y + eicas_gc.controls_h*6/10);
        g2.drawString("YAW   : " + Integer.toString(Math.round(yaw*100.0f)), eicas_gc.controls_x + eicas_gc.controls_w*5/10, eicas_gc.controls_y + eicas_gc.controls_h*7/10);
        
    }

}
