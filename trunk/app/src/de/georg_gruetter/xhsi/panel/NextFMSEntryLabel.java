/**
* NextFMSEntryLabel.java
* 
* Renders the next waypoint information box on the top right corner of the HSI
* with information about the name of the next waypoint, the time of arrival 
* in zulu time and the distance to the waypoint in nautical miles.
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import de.georg_gruetter.xhsi.model.FMS;
import de.georg_gruetter.xhsi.model.FMSEntry;
import de.georg_gruetter.xhsi.model.ModelFactory;

public class NextFMSEntryLabel extends HSISubcomponent {
	
	DecimalFormat hours_formatter;
	DecimalFormat minutes_formatter;
	DecimalFormat dist_formatter;
	DecimalFormatSymbols format_symbols;
	
	public NextFMSEntryLabel(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
		hours_formatter = new DecimalFormat("00");
		minutes_formatter = new DecimalFormat("00.0");
		dist_formatter = new DecimalFormat("0.0");
		format_symbols = dist_formatter.getDecimalFormatSymbols();
		format_symbols.setDecimalSeparator('.');
		dist_formatter.setDecimalFormatSymbols(format_symbols);
		minutes_formatter.setDecimalFormatSymbols(format_symbols);
	}

	public void paint(Graphics2D g2) {
		FMSEntry next_waypoint = FMS.get_instance().get_next_waypoint();
		if (next_waypoint != null) {
			int x = hsi_gc.panel_size.width - hsi_gc.border_right - (hsi_gc.max_char_advance_medium * 7);
			int y = hsi_gc.border_top + hsi_gc.line_height_medium;
					
			g2.clearRect(x,0,(hsi_gc.max_char_advance_medium * 7),hsi_gc.line_height_medium*3);
			g2.setColor(hsi_gc.color_magenta);
			g2.setFont(hsi_gc.font_medium);
			g2.drawString(next_waypoint.name,x,y);

			g2.setColor(Color.WHITE);
			
			float distance = this.aircraft.distance_to(next_waypoint);
			if (this.aircraft.ground_speed() > 50) {
				
				long time_at_arrival_s = this.aircraft.time_after_distance(distance);
				long hours_at_arrival = (long) time_at_arrival_s / 3600;
				float minutes_at_arrival = (((float) (time_at_arrival_s / 3600.0f) - hours_at_arrival) * 60.0f);
				
				String time_of_arrival_text = "" + hours_formatter.format(hours_at_arrival) + minutes_formatter.format(minutes_at_arrival);
				g2.drawString(time_of_arrival_text,x,y + hsi_gc.line_height_medium);
				g2.setFont(hsi_gc.font_small);
				g2.drawString("Z",x + hsi_gc.get_text_width(g2, hsi_gc.font_medium,time_of_arrival_text), y + hsi_gc.line_height_medium);
			}			
			g2.setFont(hsi_gc.font_medium);
			String dist_text = dist_formatter.format(distance);
			g2.drawString(dist_text,x,y + (2*hsi_gc.line_height_medium));
			g2.setFont(hsi_gc.font_small);
			g2.drawString("NM",x + hsi_gc.get_text_width(g2, hsi_gc.font_medium,dist_text), y + (2 * hsi_gc.line_height_medium));
		}
	}
}
