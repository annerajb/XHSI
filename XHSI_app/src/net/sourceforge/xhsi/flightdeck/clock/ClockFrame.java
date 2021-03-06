/**
* ClockFrame.java
* 
* ...
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2018  the Technische Hochschule Ingolstadt 
*                     - Patrick Burkart
*                     - Tim Drouven
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
package net.sourceforge.xhsi.flightdeck.clock;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.ModelFactory;


public class ClockFrame extends ClockSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public ClockFrame(ModelFactory model_factory, ClockGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        logger.finest("ClockFrame instanciated");
    }


    public void paint(Graphics2D g2) {
        if ( XHSIPreferences.get_instance().get_relief_border() ) {
            drawRaisedPanel(g2);
        }
    }


    private void drawRaisedPanel(Graphics2D g2) {

        Stroke original_stroke = g2.getStroke();

        g2.setColor(clock_gc.frontpanel_color);
        g2.fillRoundRect(clock_gc.clock_square.x, clock_gc.clock_square.y, clock_gc.clock_square.width, clock_gc.clock_square.height, clock_gc.clock_square.width/8, clock_gc.clock_square.height/8);
        g2.setStroke(new BasicStroke(8.0f * clock_gc.scaling_factor));
        g2.setPaint(clock_gc.clock_gradient);
        g2.drawRoundRect(clock_gc.clock_square.x, clock_gc.clock_square.y, clock_gc.clock_square.width, clock_gc.clock_square.height, clock_gc.clock_square.width/8, clock_gc.clock_square.height/8);

        g2.setStroke(original_stroke);

    }

    /**
     * No Mouse click action for the Clock Frame
     */
    public void mouseClicked(Graphics2D g2, MouseEvent e) {
    }

    /**
     * No Mouse drag action for the Clock Frame
     */
    public void mouseDragged(Graphics2D g2, MouseEvent e) {
    }
    
    /**
     * No Mouse released action for the Clock Frame
     */
    public void mouseReleased(Graphics2D g2, MouseEvent e) {
    }

    /**
     * No Mouse pressed action for the Clock Frame
     */
    public void mousePressed(Graphics2D g2, MouseEvent e) {
    }


}
