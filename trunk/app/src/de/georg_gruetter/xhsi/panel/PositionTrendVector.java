/**
* PositionTrendVector.java
* 
* Renders the position trend vector indicating the estimated flight path of
* the aircraft in 30, 60 and 90 seconds depending on the chosen map range.
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
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class PositionTrendVector extends HSISubcomponent {

	public PositionTrendVector(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}

	public void paint(Graphics2D g2) {
		if (Math.abs(aircraft.roll_angle()) >= 2) {
		
			long  pixels_per_kilometer = hsi_gc.rose_radius / avionics.map_range();
			float turn_speed = aircraft.turn_speed();						// turn speed in deg/s
			float ground_speed = aircraft.ground_speed() * 0.51444f;		// ground speed in m/s
			float turn_radius = turn_radius(turn_speed, ground_speed);		// turn radius in m
			int map_range = avionics.map_range();
			
			g2.setColor(Color.WHITE);
			draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_kilometer, 1); 
			if (map_range >= 20) { draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_kilometer, 2); }
			if (map_range > 20) { draw_position_trend_vector_segment(g2, turn_radius, turn_speed, pixels_per_kilometer, 3); }
		}
	}
	
	public void draw_position_trend_vector_segment(Graphics2D g2, float turn_radius, float turn_speed, long pixels_per_kilometer, int segment_index) {
		float turn_radius_pixels = ((turn_radius/1000) * pixels_per_kilometer);
		if (turn_speed >= 0) {
			g2.draw(new Arc2D.Float(
					(float) hsi_gc.plane_position_x, 
					(float) (hsi_gc.plane_position_y - turn_radius_pixels),
					turn_radius_pixels * 2.0f,
					turn_radius_pixels * 2.0f,
					(180.0f - (30.0f * segment_index * turn_speed)),
					(25.0f * turn_speed),
					Arc2D.OPEN));	
		} else {
			
			g2.draw(new Arc2D.Float(
					(float) ((hsi_gc.plane_position_x) - (turn_radius_pixels * 2)), 
					(float) (hsi_gc.plane_position_y - turn_radius_pixels),
					turn_radius_pixels * 2.0f,
					turn_radius_pixels * 2.0f,
					(30.0f * (segment_index-1.0f) * -1.0f * turn_speed),
					(25.0f * -1.0f * turn_speed),
					Arc2D.OPEN));				
		}
	}

	public float turn_radius(float turn_speed, float speed) {
		return Math.abs((speed * 180.0f) / ( (turn_speed * 2.2f)* (float) Math.PI));
	}	
}
