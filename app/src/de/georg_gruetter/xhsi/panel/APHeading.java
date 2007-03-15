/**
* APHeading.java
* 
* Renders autopilot heading bug and line from airplane symbol to heading bug.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi.panel;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class APHeading extends HSISubcomponent {
	
	float dash[] = { 10.0f };
	
	public APHeading(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}
		
	public void paint(Graphics2D g2) {

		GeneralPath polyline = null;
		float ap_heading_offset = aircraft.horizontal_path() - avionics.heading_bug();
		
		// rotate according to heading bug
		AffineTransform original_at = g2.getTransform();
        g2.rotate(
        		Math.toRadians((double) (-1 * ap_heading_offset)), 
        		hsi_gc.plane_position_x, 
        		hsi_gc.plane_position_y);
		
		// heading bug
        int heading_bug_width = (int) Math.min(38,40 * hsi_gc.scaling_factor);
        int heading_bug_height = (int) Math.min(14,16 * hsi_gc.scaling_factor);
        
		g2.setColor(hsi_gc.color_magenta);
	    polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 8);
	    polyline.moveTo (hsi_gc.plane_position_x - heading_bug_width/2, hsi_gc.rose_y_offset);
	    polyline.lineTo(hsi_gc.plane_position_x - heading_bug_width/2, hsi_gc.rose_y_offset - heading_bug_height);
	    polyline.lineTo(hsi_gc.plane_position_x - (heading_bug_width/6), hsi_gc.rose_y_offset - heading_bug_height);
	    polyline.lineTo(hsi_gc.plane_position_x, hsi_gc.rose_y_offset);
	    polyline.lineTo(hsi_gc.plane_position_x + (heading_bug_width/6), hsi_gc.rose_y_offset - heading_bug_height);
	    polyline.lineTo(hsi_gc.plane_position_x + heading_bug_width/2, hsi_gc.rose_y_offset - heading_bug_height);
	    polyline.lineTo(hsi_gc.plane_position_x + heading_bug_width/2, hsi_gc.rose_y_offset);
	    g2.draw(polyline);
		
	    // dotted line from plane to heading bug
		Stroke original_stroke = g2.getStroke();
	    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		g2.draw(new Line2D.Double(hsi_gc.plane_position_x, hsi_gc.plane_position_y,
								   hsi_gc.plane_position_x, hsi_gc.rose_y_offset));
		g2.setStroke(original_stroke);
		
		// reset transformation
        g2.setTransform(original_at);
	}	

}
