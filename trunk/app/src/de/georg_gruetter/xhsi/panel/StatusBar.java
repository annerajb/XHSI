/**
* StatusBar.java
* 
* Renders the status bar with data source and frame rate indicators.
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

import de.georg_gruetter.xhsi.HSIStatus;
import de.georg_gruetter.xhsi.model.ModelFactory;
import de.georg_gruetter.xhsi.model.xplane.XPlaneSimDataRepository;
import de.georg_gruetter.xhsi.util.RunningAverager;

public class StatusBar extends HSISubcomponent {

	long time_of_last_call;
	float[] last_frame_rates = new float[10];
	RunningAverager averager = new RunningAverager(100);

	int fps_x;
	int fps_y;
	int fps_pixels_per_frame;
	
	int src_x;
	int src_y;
	
	int nav_db_status_x;
	int nav_db_status_y;
	int nav_db_text_width = 0;
	
	public StatusBar(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
		this.time_of_last_call = System.currentTimeMillis();
	}

	public void paint(Graphics2D g2) {
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, hsi_gc.panel_size.height - hsi_gc.status_bar_height, hsi_gc.panel_size.width, hsi_gc.status_bar_height);
		
		g2.setBackground(Color.BLACK);

		// compute nav db text width only once
		if (nav_db_text_width == 0) {
			nav_db_text_width = this.hsi_gc.get_text_width(g2, this.hsi_gc.font_small, "NAV DB");
		}
		fps_x = hsi_gc.border_left + 120;
		fps_y = hsi_gc.panel_size.height - 25;
		fps_pixels_per_frame = 2;
		
		src_x = hsi_gc.border_left;
		src_y = hsi_gc.panel_size.height - 25;
		
		nav_db_status_x = hsi_gc.panel_size.width - hsi_gc.border_right - nav_db_text_width;
		nav_db_status_y = hsi_gc.panel_size.height - 25;
		
		draw_data_source(g2);
		draw_frame_rate(g2);
		draw_nav_db_status(g2);		
	}
	
	public void draw_data_source(Graphics2D g2) {
		int x_offs = src_x + 50;
		int x_points_caret[] = { x_offs + 4, 	x_offs+10,	x_offs+16,	x_offs+10,	x_offs+4 };
		int y_points_caret[] = { src_y+6,  	src_y,		src_y+6,	src_y+12,	src_y+6};
		
		int x_points_arrowhead[] = { x_offs + 8, 	x_offs+13,	x_offs+8, x_offs+8};
		int y_points_arrowhead[] = { src_y+3,  src_y+6,		src_y+9, src_y+3};

		g2.setColor(Color.BLACK);
		g2.setFont(hsi_gc.font_small);
		g2.drawPolygon(x_points_caret, y_points_caret, 5);
		g2.drawLine(x_offs, src_y+6, x_offs + 8, src_y+6);		
		g2.fillPolygon(x_points_arrowhead, y_points_arrowhead, 4);
		if (XPlaneSimDataRepository.source_is_recording) {
			g2.drawString("REC", src_x+25, src_y+11);
			g2.setColor(Color.RED);
			g2.fillOval(src_x+10,src_y,11,11);
		} else {			
			g2.drawString("X-Plane", src_x, src_y+11);
			if (HSIStatus.status == HSIStatus.STATUS_NO_RECEPTION) {
				g2.setColor(Color.RED);
				// cross out text
				g2.drawLine(src_x+15, src_y+12, src_x+30, src_y);
				g2.drawLine(src_x+15, src_y, src_x+30, src_y+12);
			}
		}
	}
	
	public void draw_nav_db_status(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.setFont(hsi_gc.font_small);
		g2.drawString("NAV DB", nav_db_status_x, nav_db_status_y+11);

		if (HSIStatus.nav_db_status.equals(HSIStatus.STATUS_NAV_DB_NOT_FOUND)) {
			g2.setColor(Color.RED);
			// cross out text
			g2.drawLine(nav_db_status_x+15, nav_db_status_y+12, nav_db_status_x+30, nav_db_status_y);
			g2.drawLine(nav_db_status_x+15, nav_db_status_y, nav_db_status_x+30, nav_db_status_y+12);
		}
	}

	public void draw_frame_rate(Graphics2D g2) {
		
		if ((HSIStatus.status == HSIStatus.STATUS_RECEIVING) || (HSIStatus.status == HSIStatus.STATUS_PLAYING_RECORDING)) {
			long current_time = System.currentTimeMillis();
			int frame_rate = (int) this.averager.running_average(1000.0f/(current_time - time_of_last_call));
			g2.setColor(Color.BLACK);
			g2.setFont(hsi_gc.font_small);
			
			// draw scale
			g2.drawString("FPS",fps_x, fps_y + 11);
			for (int i=0;i<=40;i+=10) {
				g2.drawLine(
						fps_x + 30 + (i*fps_pixels_per_frame), 
						fps_y + 12,
						fps_x + 30 + (i*fps_pixels_per_frame), 
						fps_y + 15);
			}
			if (frame_rate > 40) {
				frame_rate = 40;
				g2.drawString("+", fps_x + 33 + (40*fps_pixels_per_frame), fps_y + 11);
			}
			
			g2.setFont(hsi_gc.font_tiny);
			g2.drawString("0", fps_x + 28, fps_y + 23);
			g2.drawString("20", fps_x + 25 + (20*fps_pixels_per_frame), fps_y + 23);
			g2.drawString("40", fps_x + 25 + (40*fps_pixels_per_frame), fps_y + 23);
	
			// draw bar
			if (frame_rate > 15) {
				g2.setColor(Color.BLACK);
			} else if (frame_rate > 10) {
				g2.setColor(Color.YELLOW);		
			} else if (frame_rate > 5) {
				g2.setColor(Color.ORANGE);			
			} else {
				g2.setColor(Color.RED);
			}
			g2.fillRoundRect(
					fps_x+30,
					fps_y,
					(frame_rate * fps_pixels_per_frame),
					10,
					4,
					4);
			this.time_of_last_call = current_time;
		}
	}
}
