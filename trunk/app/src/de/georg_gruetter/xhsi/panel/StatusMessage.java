/**
* StatusMessage.java
* 
* Renders status messages.
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
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class StatusMessage extends HSISubcomponent {

	private static final int BORDER = 10;
	
	private ArrayList status_message_lines;
	private int message_width;
	private int message_height;
	
	public StatusMessage(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
		this.status_message_lines = new ArrayList();
		this.message_width = 0;
	}

	public void paint(Graphics2D g2) {
		
		int ul_x = hsi_gc.plane_position_x - (message_width/2);
		int ul_y = (hsi_gc.panel_size.height - message_height)/2;
		
		if (this.status_message_lines.isEmpty() == false) {
			
			g2.setColor(Color.GREEN);
			g2.setFont(hsi_gc.font_medium);
			
			RoundRectangle2D.Float message_rectangle = new RoundRectangle2D.Float(
					ul_x - BORDER,
					ul_y - BORDER,
					message_width + (2*BORDER),
					message_height + (2*BORDER),
					30,
					30);
			g2.draw(message_rectangle);
			g2.setColor(Color.DARK_GRAY);
			g2.fill(message_rectangle);
			g2.setColor(Color.GREEN);
			
			//Area message_box = new Area(message_rectangle);
			g2.draw(
					new RoundRectangle2D.Float(
							ul_x - BORDER,
							ul_y - BORDER,
							message_width + (2*BORDER),
							message_height + (2*BORDER),
							30,
							30));
			for (int i=0;i<this.status_message_lines.size();i++) {
				if (this.status_message_lines.get(i) instanceof String) {
					g2.drawString((String) this.status_message_lines.get(i), ul_x, (ul_y + ((i+1) * hsi_gc.line_height_medium)));
				}
			}
		}
	}

	public void set_message_dimensions(int width, int height) {
		this.message_width = width;
		this.message_height = height;
	}
	
	public void set_message(String message) {
		this.status_message_lines.clear();
		this.status_message_lines.add(message);
	}
	
	public void append_message(String message) {
		this.status_message_lines.add(message);
	}
	
	public void clear() {
		this.status_message_lines.clear();
	}
}
