/**
* HSIGraphicsConfig.java
* 
* Calculates and provides access to screen positions and sizes based on the
* size of HSIComponent.
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class HSIGraphicsConfig implements ComponentListener {
	
	public static int INITIAL_PANEL_WIDTH = 600;
	public static int INITIAL_PANEL_HEIGHT = 700;
	
	public Font font_tiny;
	public Font font_small;
	public Font font_medium;
	public Font font_large;
	
	public Color color_lightgreen;
	public Color color_magenta;
	public Color color_lightblue;

	public int line_height_small;
	public int max_char_advance_small;
	public int line_height_medium;
	public int max_char_advance_medium;
	
	public Dimension panel_size;
	public int rose_radius;
	public int rose_y_offset;
	public int radio_bar_height; 
	public int plane_position_x;
	public int plane_position_y;
	public float scaling_factor;
	public float pixels_per_kilometer;
	public int status_bar_height = 30;
	public int border_left = 15;
	public int border_right = 15;
	public int border_top = 15;
	public int border_bottom = 15;
	public int pixel_distance_plane_bottom_screen;
	public int pixel_distance_plane_lower_left_corner;
	public float half_view_angle;
	public int big_tick_length;
	public int small_tick_length;
	public Area inner_rose_area;
	public Area instrument_frame;
	
	private boolean resized = true;;
	
	public HSIGraphicsConfig(Component root_component) {
		init();
	}
	
	public void init() {
		this.panel_size = new Dimension(INITIAL_PANEL_WIDTH, INITIAL_PANEL_HEIGHT);
		float[] hsb_values = new float[3];
		Color.RGBtoHSB(255,164,235, hsb_values);
		color_magenta = Color.getHSBColor(hsb_values[0], hsb_values[1], hsb_values[2]);

		Color.RGBtoHSB(0,255,153, hsb_values);
		color_lightgreen = Color.getHSBColor(hsb_values[0], hsb_values[1], hsb_values[2]);

		Color.RGBtoHSB(98,177,223, hsb_values);
		color_lightblue = Color.getHSBColor(hsb_values[0], hsb_values[1], hsb_values[2]);	
		
		color_lightgreen = Color.GREEN.brighter();
		color_lightblue = Color.CYAN; //.brighter();
		color_magenta = Color.MAGENTA.brighter();
	}
	
	public void update_config(Graphics2D g2) {
		if (resized) {		
			this.scaling_factor = Math.min(1.0f,((float)this.panel_size.height / (float) 600.0));		
			this.font_tiny = new Font("Lucida Sans", Font.PLAIN, 8);		
			this.font_small = new Font("Lucida Sans", Font.PLAIN, 11);
			this.font_medium = new Font("Lucida Sans", Font.PLAIN, 16);
			this.font_large = new Font("Lucida Sans", Font.PLAIN, 28);
			this.plane_position_x = this.panel_size.width / 2;
			this.radio_bar_height = 100;
			this.rose_y_offset = 50 + border_top;
			this.plane_position_y = (int) (this.panel_size.height - radio_bar_height - rose_y_offset);
			this.rose_radius = plane_position_y - this.rose_y_offset;		
			resized = false;
			
			// calculate font metrics
			FontMetrics fm = g2.getFontMetrics(this.font_medium);
			this.line_height_medium = fm.getAscent();
			this.max_char_advance_medium = fm.stringWidth("XX") - fm.stringWidth("X");

			fm = g2.getFontMetrics(this.font_small);
			this.line_height_small = fm.getAscent();
			this.max_char_advance_small = fm.stringWidth("XX") - fm.stringWidth("X");
			
			// calculate pixel distances. Needed for determining which
			// part of the rose needs to be drawn
			pixel_distance_plane_bottom_screen = this.panel_size.height - this.plane_position_y - this.status_bar_height;
			pixel_distance_plane_lower_left_corner = 
				(int) Math.sqrt(
					Math.pow(this.pixel_distance_plane_bottom_screen,2) +
					Math.pow(this.panel_size.width/2,2));
			
			this.big_tick_length = (int) (20 * scaling_factor);
			this.small_tick_length = this.big_tick_length / 2;
			
			if (this.pixel_distance_plane_bottom_screen >= (this.rose_radius - this.big_tick_length)) {
				// Complete rose
				this.half_view_angle = 180.0f;
			} else if (this.pixel_distance_plane_lower_left_corner > (this.rose_radius - this.big_tick_length)) {
				// Rose visible below aircraft position
				half_view_angle = (float) (180.0f - Math.toDegrees(Math.acos((1.0f * pixel_distance_plane_bottom_screen)/(1.0f*(this.rose_radius - this.big_tick_length)))));
			} else {
				// Rose visible only above aircraft position
				half_view_angle = (float) (90.0f - Math.toDegrees(Math.acos((1.0f*this.panel_size.width)/(2.0f*(this.rose_radius - this.big_tick_length)))));
			}
									
			this.inner_rose_area = new Area(new Ellipse2D.Float(
					plane_position_x - rose_radius + 40,
					plane_position_y - rose_radius + 40,
					(rose_radius * 2) - 80,
					(rose_radius * 2) - 80));	
			
			Area inner_frame = new Area(new RoundRectangle2D.Float(
					border_left - 10,
					border_top - 10,
					panel_size.width - (border_left + border_right) + 20,
					panel_size.height - border_top - status_bar_height + 10,
					30,
					30));
			instrument_frame = new Area(new Rectangle2D.Float(0,0, panel_size.width, panel_size.height));
			instrument_frame.subtract(inner_frame);
			
		}
	}
	
	public int get_text_width(Graphics graphics, Font font, String text) {
		return graphics.getFontMetrics(font).stringWidth(text);
	}

	public int get_text_height(Graphics graphics, Font font) {
		return graphics.getFontMetrics(font).getHeight();
	}	
	
	public void componentResized(ComponentEvent event) {
		this.panel_size = event.getComponent().getSize();
		this.resized = true;
	}

	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}	
}
