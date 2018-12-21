/**
* Fail.java
* 
* Draws a red cross when reception from X-Plane is lost
* 
* Copyright (C) 2012  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;


public class NDFail extends NDSubcomponent {

    private static final long serialVersionUID = 1L;

    //  private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public NDFail(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }

    public void paint(Graphics2D g2) {
        if ( ! XHSIStatus.receiving ) {
            if ( nd_gc.boeing_style) {
                drawFailCross(g2);
            } else {
            	drawFailIndicator(g2);
            }
        }
    }

    private void drawFailCross(Graphics2D g2) {
        g2.setColor(nd_gc.xhsi_comm_lost_color);
        Stroke original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(8.0f * nd_gc.scaling_factor));
        g2.drawLine(nd_gc.border_left, nd_gc.border_top, nd_gc.frame_size.width - nd_gc.border_right, nd_gc.frame_size.height - nd_gc.border_bottom);
        g2.drawLine(nd_gc.frame_size.width - nd_gc.border_right, nd_gc.border_top, nd_gc.border_left, nd_gc.frame_size.height - nd_gc.border_bottom);
        g2.setStroke(original_stroke);
    }

    private void drawFailIndicator(Graphics2D g2) {
        g2.setColor(nd_gc.xhsi_comm_lost_color);
    	String failed_str = "XHSI COMM LOST";
        g2.setFont(nd_gc.font_xxl);
    	g2.drawString( failed_str, nd_gc.map_center_x - nd_gc.get_text_width(g2, nd_gc.font_xxl, failed_str)/2,  nd_gc.gps_message_y  - nd_gc.line_height_xxl );
    }

}
