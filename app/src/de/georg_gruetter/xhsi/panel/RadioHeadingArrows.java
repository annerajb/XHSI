/**
* RadioHeadingArrows.java
* 
* Renders directional arrows indicating the direction to the currently 
* tuned radio navigation object. The arrows are rendered only if the 
* navigation radio has reception. Uses RadioHeadingArrowsHelper for
* actual rendering.
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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import de.georg_gruetter.xhsi.model.ModelFactory;
import de.georg_gruetter.xhsi.model.NavigationRadio;

public class RadioHeadingArrows extends HSISubcomponent {
	
	NavigationRadio selected_nav_radio1;
	NavigationRadio selected_nav_radio2;
	
	AffineTransform original_at;
	
	public RadioHeadingArrows(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}
	
	public void paint(Graphics2D g2) {
		
		int arrow_length = (int) Math.min(60, hsi_gc.scaling_factor * 60);
		int arrow_base_width = (int) Math.min(25, hsi_gc.scaling_factor * 25);

		// get currently tuned in radios
		this.selected_nav_radio1 = this.avionics.get_selected_radio1();
		this.selected_nav_radio2 = this.avionics.get_selected_radio2();
		
		if ((this.selected_nav_radio1 != null) && (this.selected_nav_radio1.receiving())) {
			if (this.selected_nav_radio1.freq_is_nav() && (this.selected_nav_radio1.freq_is_localizer() == false)) {
				g2.setColor(hsi_gc.color_lightgreen);
			    draw_nav1_arrow(g2, selected_nav_radio1.get_deflection() + this.aircraft.slip(),arrow_length,arrow_base_width);			
			} else if (this.selected_nav_radio1.freq_is_adf()) {
				g2.setColor(hsi_gc.color_lightblue);
			    draw_nav1_arrow(g2, selected_nav_radio1.get_deflection() + this.aircraft.slip(),arrow_length,arrow_base_width);			
			}
		}
		
		if ((this.selected_nav_radio2 != null) && (this.selected_nav_radio2.receiving())) {
			if (this.selected_nav_radio2.freq_is_nav() && (this.selected_nav_radio2.freq_is_localizer() == false)) {
				g2.setColor(hsi_gc.color_lightgreen);
			    draw_nav2_arrow(g2, selected_nav_radio2.get_deflection() + this.aircraft.slip(),arrow_length,arrow_base_width);			
			} else if (this.selected_nav_radio2.freq_is_adf()) {
				g2.setColor(hsi_gc.color_lightblue);
			    draw_nav2_arrow(g2, selected_nav_radio2.get_deflection() + this.aircraft.slip(),arrow_length,arrow_base_width);			
			}
		}
	}
	
	private void rotate(Graphics2D g2, double angle) {
		this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
        		Math.toRadians(angle), 
        		hsi_gc.plane_position_x, 
        		hsi_gc.plane_position_y);
        g2.transform(rotate);		
	}
	
	private void unrotate(Graphics2D g2) {
		g2.setTransform(original_at);
	}
	
	private void draw_nav1_arrow(Graphics2D g2, float deflection, int length, int base_width) {
	    rotate(g2, deflection);
        RadioHeadingArrowsHelper.draw_nav1_forward_arrow(g2, hsi_gc.plane_position_x, hsi_gc.rose_y_offset, length, base_width);
        RadioHeadingArrowsHelper.draw_nav1_backward_arrow(g2, hsi_gc.plane_position_x, hsi_gc.plane_position_y + hsi_gc.rose_radius, length, base_width);
	    unrotate(g2);		
	}
	
	private void draw_nav2_arrow(Graphics2D g2, float deflection, int length, int base_width) {
	    rotate(g2, deflection);
	    RadioHeadingArrowsHelper.draw_nav2_forward_arrow(g2, hsi_gc.plane_position_x, hsi_gc.rose_y_offset, length, base_width);
	    RadioHeadingArrowsHelper.draw_nav2_backward_arrow(g2, hsi_gc.plane_position_x, hsi_gc.plane_position_y + hsi_gc.rose_radius, length, base_width);
	    unrotate(g2);		
	}	
}
