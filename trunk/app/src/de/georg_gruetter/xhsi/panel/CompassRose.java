/**
* CompassRose.java
* 
* Renders the visible compass rose.
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


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.logging.Logger;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class CompassRose extends HSISubcomponent {
	
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	private int two_digit_hdg_text_width = 0;
	private int one_digit_hdg_text_width = 0;
	private int hdg_text_height = 0;
	
	public CompassRose(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);

	}
	
	public int round_to_ten(float number) {
		return Math.round(number / 10) * 10;
	}
	
	public void paint(Graphics2D g2) {	
		// calculate text widths and heights only once
		if (this.hdg_text_height == 0) {
			two_digit_hdg_text_width = (int) hsi_gc.get_text_width(g2, hsi_gc.font_medium, "33");
			one_digit_hdg_text_width = (int) hsi_gc.get_text_width(g2, hsi_gc.font_medium, "8");
			hdg_text_height = (int) (hsi_gc.get_text_height(g2, hsi_gc.font_medium)*0.8f);
		}
		
		int min_visible_heading = round_to_ten(this.aircraft.horizontal_path() - hsi_gc.half_view_angle);
		int max_visible_heading = round_to_ten(this.aircraft.horizontal_path() + hsi_gc.half_view_angle) + 5;
		double rotation_offset = (-1 * hsi_gc.half_view_angle)  + (min_visible_heading - (this.aircraft.horizontal_path() - hsi_gc.half_view_angle));
		
		
		
		g2.setColor(Color.WHITE);
        g2.drawArc(hsi_gc.plane_position_x - hsi_gc.rose_radius, 
    		   hsi_gc.plane_position_y - hsi_gc.rose_radius,
    		   hsi_gc.rose_radius*2,
     		   hsi_gc.rose_radius*2,
     		   (int) ((hsi_gc.half_view_angle + 90)),
     		   (int) (hsi_gc.half_view_angle * -2)
    		);        
 		AffineTransform original_at = g2.getTransform();
	    
		// rotate according to horziontal path
        AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
        		Math.toRadians(rotation_offset), 
        		hsi_gc.plane_position_x, 
        		hsi_gc.plane_position_y);
        g2.transform(rotate_to_heading);
				
        Graphics g = (Graphics) g2;
        /*
        g.drawOval(hsi_gc.plane_position_x - hsi_gc.rose_radius, hsi_gc.plane_position_y - hsi_gc.rose_radius,
        			hsi_gc.rose_radius * 2, hsi_gc.rose_radius * 2);
        			*/
		int tick_length = 0;
        g2.setFont(hsi_gc.font_medium);
        for (int angle=min_visible_heading;angle<= max_visible_heading; angle += 5) {
			if (angle % 10 == 0) {
				tick_length = hsi_gc.big_tick_length;
			} else {
				tick_length = hsi_gc.small_tick_length;
			}
			g.drawLine(hsi_gc.plane_position_x, hsi_gc.rose_y_offset,
					   hsi_gc.plane_position_x, hsi_gc.rose_y_offset + tick_length);
	        
			String text = "";
			if (angle < 0) {
		        text = "" + (angle + 360)/10;
			} else if (angle >=360) {
				text = "" + (angle - 360)/10;
			} else {
				text = "" + angle/10;
			}
	        if (angle % 30 == 0) {
	        	int text_width;
	        	if (text.length() == 1)
	        		text_width = one_digit_hdg_text_width;
	        	else
	        		text_width = two_digit_hdg_text_width;
	        	
		        g.drawString(
		        		text, 
		        		hsi_gc.plane_position_x - (text_width/2), 
		        		hsi_gc.rose_y_offset + tick_length + hdg_text_height);	        		
	        }
	        
			 AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(5.0), 
	        		hsi_gc.plane_position_x, 
	        		hsi_gc.plane_position_y);
	        g2.transform(rotate);
		}
        g2.setTransform(original_at);
	}
}
