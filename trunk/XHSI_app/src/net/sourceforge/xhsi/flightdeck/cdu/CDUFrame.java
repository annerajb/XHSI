/**
* CDUFrame.java
* 
* ...
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class CDUFrame extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public CDUFrame(ModelFactory model_factory, CDUGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( this.preferences.cdu_display_only() ) {
            drawSunkenDisplay(g2);
        } else {
            // Nothing as long as only the X-FMC CDU is implemented
            //drawRaisedPanel(g2);
        }
    }


    private void drawRaisedPanel(Graphics2D g2) {

        Stroke original_stroke = g2.getStroke();

        g2.setColor(cdu_gc.frontpanel_color);
        g2.fillRoundRect(cdu_gc.raised_panel.x, cdu_gc.raised_panel.y, cdu_gc.raised_panel.width, cdu_gc.raised_panel.height, cdu_gc.raised_panel.width/16, cdu_gc.raised_panel.height/16);
        g2.setStroke(new BasicStroke(8.0f * cdu_gc.scaling_factor));
        g2.setPaint(cdu_gc.panel_gradient);
        g2.drawRoundRect(cdu_gc.raised_panel.x, cdu_gc.raised_panel.y, cdu_gc.raised_panel.width, cdu_gc.raised_panel.height, cdu_gc.raised_panel.width/16, cdu_gc.raised_panel.height/16);

        g2.setStroke(original_stroke);

    }

    private void drawSunkenDisplay(Graphics2D g2) {

        if ( XHSIPreferences.get_instance().get_relief_border() ) {
            // a rounded frame looks soo much nicer...
            g2.setPaint(cdu_gc.border_gradient);
            g2.fill(cdu_gc.instrument_frame);
            g2.setColor(cdu_gc.backpanel_color);
            g2.fill(cdu_gc.instrument_outer_frame);
        } else {
            // the cheapest way is to paint the borders as rectangles
            if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_DARK) ) {
                g2.setColor(cdu_gc.frontpanel_color);
            } else if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_LIGHT) ) {
                g2.setColor(cdu_gc.backpanel_color);
            } else {
                g2.setColor(Color.BLACK);
            }
            g2.fillRect(0, 0, cdu_gc.border_left, cdu_gc.frame_size.height);
            g2.fillRect(cdu_gc.frame_size.width - cdu_gc.border_right, 0, cdu_gc.border_right, cdu_gc.frame_size.height);
            g2.fillRect(0, 0, cdu_gc.frame_size.width, cdu_gc.border_top);
            g2.fillRect(0, cdu_gc.frame_size.height - cdu_gc.border_bottom, cdu_gc.frame_size.width, cdu_gc.border_bottom);
        }
        
    }


}
