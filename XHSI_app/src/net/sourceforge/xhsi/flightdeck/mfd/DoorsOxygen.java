/**
* LowerEicas.java
* 
* Lower EICAS and ECAM
* 
* Copyright (C) 2015  Nicolas Carel
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

package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Door;
import net.sourceforge.xhsi.model.ModelFactory;

public class DoorsOxygen extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Stroke original_stroke;
    
  
	public DoorsOxygen(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_DOOR_OXY) {
			// Page ID
			drawPageID(g2, "DOOR/OXY");
			drawAircraft(g2);
			drawSlides(g2, true);
			drawCabinVS(g2);
			drawCabinOxy(g2);
			drawDoors(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.digit_width_xxl;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_xl/8);
	}

	private void drawAircraft(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_markings_color);
		scalePen(g2);
		g2.drawLine(mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx, mfd_gc.door_cabin_top_y,
				    mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx, mfd_gc.door_cabin_bottom_y);
		g2.drawLine(mfd_gc.door_center_x + mfd_gc.door_cabin_width_dx, mfd_gc.door_cabin_top_y,
			        mfd_gc.door_center_x + mfd_gc.door_cabin_width_dx, mfd_gc.door_cabin_bottom_y);
		g2.drawArc(mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx, mfd_gc.door_noose_y,
				mfd_gc.door_cabin_width_dx*2, (mfd_gc.door_cabin_top_y-mfd_gc.door_noose_y)*2,
				0, 180);
		g2.drawArc(mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx, mfd_gc.door_cabin_bottom_y-(mfd_gc.door_tail_y-mfd_gc.door_cabin_bottom_y)*2,
				mfd_gc.door_cabin_width_dx*2, (mfd_gc.door_tail_y-mfd_gc.door_cabin_bottom_y)*4,
				180, 45);
		g2.drawArc(mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx, mfd_gc.door_cabin_bottom_y-(mfd_gc.door_tail_y-mfd_gc.door_cabin_bottom_y)*2,
				mfd_gc.door_cabin_width_dx*2, (mfd_gc.door_tail_y-mfd_gc.door_cabin_bottom_y)*4,
				0, -45);
		g2.drawLine(mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx-mfd_gc.door_wing_width_dx, mfd_gc.door_wing_bottom_y,
			    mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx, mfd_gc.door_wing_top_y);
		g2.drawLine(mfd_gc.door_center_x + mfd_gc.door_cabin_width_dx+mfd_gc.door_wing_width_dx, mfd_gc.door_wing_bottom_y,
			    mfd_gc.door_center_x + mfd_gc.door_cabin_width_dx, mfd_gc.door_wing_top_y);
		resetPen(g2);
	}

	private void drawSlides(Graphics2D g2, boolean slide_armed){
		String slide_str = "SLIDE";
		int slide_left_x = mfd_gc.door_center_x - mfd_gc.door_cabin_width_dx - mfd_gc.door_slide_legend_dx - mfd_gc.get_text_width(g2, mfd_gc.font_l, slide_str);
		int slide_right_x = mfd_gc.door_center_x + mfd_gc.door_cabin_width_dx + mfd_gc.door_slide_legend_dx;
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(slide_str, slide_left_x, mfd_gc.door_slide_wing_y);
		g2.drawString(slide_str, slide_right_x, mfd_gc.door_slide_wing_y);
		if (slide_armed) {
			g2.drawString(slide_str, slide_left_x, mfd_gc.door_slide_top_y);
			g2.drawString(slide_str, slide_right_x, mfd_gc.door_slide_top_y);
			g2.drawString(slide_str, slide_left_x, mfd_gc.door_slide_aft_y);
			g2.drawString(slide_str, slide_right_x, mfd_gc.door_slide_aft_y);
		}
	}
	
	private void drawCabinVS(Graphics2D g2) {
		// Legends
		String str_vs_legend = "V/S";
		// Units
		String str_vs_units = "FT/MN";
		// Values
		String str_vs = "" + Math.round(aircraft.cabin_vs()/10)*10;
		
	    // Draw legends
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_vs_legend , mfd_gc.door_vs_legend_x, mfd_gc.door_vs_y);

		// Draw units
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_vs_units, mfd_gc.door_vs_unit_x, mfd_gc.door_vs_y);
		
		// Draw values
		g2.setColor(mfd_gc.ecam_normal_color);
		g2.setFont(mfd_gc.font_xxl);
		g2.drawString(str_vs, mfd_gc.door_vs_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_vs), mfd_gc.door_vs_y);	
	}

	private void drawCabinOxy(Graphics2D g2) {
		// Legends
		String str_oxy_legend = "OXY";
		// Units
		String str_oxy_units = "PSI";
		// Values
		String str_oxy = "XX";
		
	    // Draw legends
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_oxy_legend , mfd_gc.door_oxy_legend_x, mfd_gc.door_oxy_y);

		// Draw units
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_oxy_units, mfd_gc.door_oxy_unit_x, mfd_gc.door_oxy_y);
		
		// Draw values
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.setFont(mfd_gc.font_xxl);
		g2.drawString(str_oxy, mfd_gc.door_oxy_value_x - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_oxy), mfd_gc.door_oxy_y);	
	}
	
	private void drawDoors(Graphics2D g2) {
		for (int i = 0; i < mfd_gc.door_num; i++) {
			if (mfd_gc.doors[i].closed) { 
				g2.setColor(mfd_gc.ecam_normal_color); 
			} else {
				g2.setColor(mfd_gc.ecam_caution_color);
			}
			g2.drawRect(mfd_gc.doors[i].x, mfd_gc.doors[i].y, mfd_gc.doors[i].width, mfd_gc.doors[i].height);
		}
	}
	
    private void scalePen(Graphics2D g2) {
        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(1.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
    }


    private void resetPen(Graphics2D g2) {
        g2.setStroke(original_stroke);
    }
}
