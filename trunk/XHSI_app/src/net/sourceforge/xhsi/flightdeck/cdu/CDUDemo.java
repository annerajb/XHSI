/**
* CDUDemo.java
* 
* ...
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
package net.sourceforge.xhsi.flightdeck.cdu;

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

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class CDUDemo extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private int clock_x;
    private int clock_y;
    private int clock_r;


    public CDUDemo(ModelFactory model_factory, CDUGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        clock_x = cdu_gc.raised_panel.x + cdu_gc.raised_panel.width/2;
        clock_y = cdu_gc.raised_panel.y + cdu_gc.raised_panel.height/2;
        clock_r = Math.min(cdu_gc.raised_panel.width/2*7/8, cdu_gc.raised_panel.height/2*7/8);
        
        drawDial(g2); // just for demo...
        if ( this.aircraft.battery() ) {
            // 
        }
        
    }


    private void drawDial(Graphics2D g2) {

        g2.setColor(cdu_gc.knobs_color);
        g2.fillOval(clock_x-clock_r, clock_y-clock_r, 2*clock_r, 2*clock_r);

        g2.setColor(cdu_gc.background_color);
        g2.fillOval(clock_x-clock_r*280/300, clock_y-clock_r*280/300, 2*clock_r*280/300, 2*clock_r*280/300);

    }


}
