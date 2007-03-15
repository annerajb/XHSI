/**
* SpeedsLabel.java
* 
* Renders aircaft groundspeed and true airspeed as well as wind direction and
* speed.
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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import de.georg_gruetter.xhsi.model.ModelFactory;


public class SpeedsLabel extends HSISubcomponent {
	
	int wind_dir_arrow_x;
	int wind_dir_arrow_y;
	int wind_dir_arrow_length = 30;
	float relative_wind_direction;
	
	public SpeedsLabel(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}
		
	public void paint(Graphics2D g2) {
		int line_height_medium = hsi_gc.line_height_medium;
		
		float wind_speed = aircraft_environment.wind_speed();
		float wind_direction = aircraft_environment.wind_direction();
		float heading = aircraft.heading();
		int three_digits_width = hsi_gc.max_char_advance_medium * 3;		
		
		int gs_label_x = hsi_gc.border_left;
		int gs_x = gs_label_x + 2 + hsi_gc.get_text_width(g2, hsi_gc.font_small,"GS");
		int tas_label_x = gs_x + three_digits_width + 5;
		int tas_x = tas_label_x + 2 + hsi_gc.get_text_width(g2, hsi_gc.font_small,"TAS");
		int wind_dir_x = gs_label_x;
		int wind_speed_x = wind_dir_x + three_digits_width + 10;
		
		int wind_dir_arrow_x = hsi_gc.border_left + (wind_dir_arrow_length / 2) + 5;
		int wind_dir_arrow_y = hsi_gc.border_top + (2* hsi_gc.line_height_medium) + (wind_dir_arrow_length/2) + 10;
		
		g2.clearRect(0,0,140,45);

		g2.setColor(Color.white);
		g2.setFont(this.hsi_gc.font_small); 
		g2.drawString("GS", gs_label_x, hsi_gc.border_top + line_height_medium);
		g2.drawString("TAS", tas_label_x, hsi_gc.border_top + line_height_medium);
		g2.setFont(this.hsi_gc.font_medium);
	    g2.drawString("" + (int) Math.round(aircraft.ground_speed()), gs_x,hsi_gc.border_top + line_height_medium);
	    g2.drawString("" + (int) Math.round(aircraft.true_air_speed()), tas_x,hsi_gc.border_top + line_height_medium);
	    
	    String wind_dir_text = null;
	    String wind_speed_text = null;
	    if (wind_direction != 90.0f) {
	    	wind_dir_text = "" + (int) Math.round(wind_direction + this.aircraft.magnetic_variation());
	    	wind_speed_text = "/ " + (int) Math.round(wind_speed);
	    } else {
	    	wind_dir_text = "---";
	    	wind_speed_text="/ ---";
	    }
	    g2.drawString(
	    		wind_dir_text,
	    		wind_dir_x,
	    		hsi_gc.border_top + (int) (line_height_medium * 2)); 
	    g2.drawString(
	    		wind_speed_text,
	    		wind_speed_x,
	    		hsi_gc.border_top + (int) (line_height_medium * 2)); 
	    // degree arrow
	    // Todo: find out how to specify the degree character independent of OS
	    g2.drawOval(wind_dir_x + three_digits_width + 2, hsi_gc.border_top + (int) (line_height_medium) + 4,5,5);
	    
	    // wind direction arrow
	    if (wind_speed > 4) {
		    AffineTransform original_at = null;
		    if (wind_speed > 0) {
				original_at = g2.getTransform();
		        AffineTransform rotate = AffineTransform.getRotateInstance(
		        		Math.toRadians((double) (wind_direction - heading + this.aircraft.magnetic_variation())), 
		        		wind_dir_arrow_x, 
		        		wind_dir_arrow_y);
		        g2.transform(rotate);
		    }    
	 
			GeneralPath polyline;		
			g2.draw(new Line2D.Double(wind_dir_arrow_x, wind_dir_arrow_y - (wind_dir_arrow_length/2),
									   wind_dir_arrow_x, wind_dir_arrow_y + (wind_dir_arrow_length/2)));
			polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
			polyline.moveTo (wind_dir_arrow_x - 5, wind_dir_arrow_y + (wind_dir_arrow_length/2) - 5);
			polyline.lineTo(wind_dir_arrow_x, wind_dir_arrow_y + (wind_dir_arrow_length/2));
			polyline.lineTo(wind_dir_arrow_x + 5, wind_dir_arrow_y + (wind_dir_arrow_length/2) - 5);
			g2.draw(polyline);		
			g2.setTransform(original_at);	
		}
	}
}
