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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.SpoilerStatus;

public class Wheels extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	public Wheels(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_WHEELS) {
			// Page ID
			drawPageID(g2, "WHEELS");
			draw_airbus_speedbrake(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, page_str)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}

	   
    private void draw_airbus_speedbrake(Graphics2D g2) {    	
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr, mfd_gc.fctl_y_spoiler_top, 1, this.aircraft.get_spoiler_status_left(0));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr - mfd_gc.fctl_dx_spoiler_step, mfd_gc.fctl_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*1 , 2, this.aircraft.get_spoiler_status_left(1));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr - mfd_gc.fctl_dx_spoiler_step*2 , mfd_gc.fctl_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*2, 3, this.aircraft.get_spoiler_status_left(2));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr - mfd_gc.fctl_dx_spoiler_step*3, mfd_gc.fctl_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*3, 4, this.aircraft.get_spoiler_status_left(3));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_bdr, mfd_gc.fctl_y_spoiler_bottom, 5, this.aircraft.get_spoiler_status_left(4));
    	
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr, mfd_gc.fctl_y_spoiler_top, 1, this.aircraft.get_spoiler_status_right(0));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr + mfd_gc.fctl_dx_spoiler_step , mfd_gc.fctl_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*1, 2, this.aircraft.get_spoiler_status_right(1));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr + mfd_gc.fctl_dx_spoiler_step*2 , mfd_gc.fctl_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*2, 3, this.aircraft.get_spoiler_status_right(2));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr + mfd_gc.fctl_dx_spoiler_step*3 , mfd_gc.fctl_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*3, 4, this.aircraft.get_spoiler_status_right(3));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_bdr, mfd_gc.fctl_y_spoiler_bottom, 5, this.aircraft.get_spoiler_status_right(4));
    }
    
    private void draw_speed_brake_arrow(Graphics2D g2, int x, int y, int num, SpoilerStatus status) {
    	switch (status) {
    	case RETRACTED :
    		g2.setColor(mfd_gc.ecam_normal_color);
    		g2.drawLine(x-mfd_gc.fctl_dx_spoiler,y,x+mfd_gc.fctl_dx_spoiler,y);
    		break;
    	case EXTENDED :	
    		g2.setColor(mfd_gc.ecam_normal_color);
    		g2.drawLine(x-mfd_gc.fctl_dx_spoiler,y,x+mfd_gc.fctl_dx_spoiler,y);
    		g2.drawLine(x,y,x,y-mfd_gc.fctl_dy_spoiler);
    		g2.drawLine(x-mfd_gc.fctl_dx_spoiler_arrow,y-mfd_gc.fctl_dy_spoiler+mfd_gc.fctl_dy_spoiler_arrow,x,y-mfd_gc.fctl_dy_spoiler);
    		g2.drawLine(x+mfd_gc.fctl_dx_spoiler_arrow,y-mfd_gc.fctl_dy_spoiler+mfd_gc.fctl_dy_spoiler_arrow,x,y-mfd_gc.fctl_dy_spoiler);
    		break;
    	case FAILED :
    		g2.setColor(mfd_gc.ecam_caution_color);
    		g2.drawLine(x-mfd_gc.fctl_dx_spoiler,y,x+mfd_gc.fctl_dx_spoiler,y);
    		g2.setFont(mfd_gc.font_m);
    		g2.drawString(""+num,x-mfd_gc.digit_width_m/2,y-2);
    		break;
    	case JAMMED :
    		g2.setColor(mfd_gc.ecam_caution_color);
    		g2.drawLine(x-mfd_gc.fctl_dx_spoiler,y,x+mfd_gc.fctl_dx_spoiler,y);
    		g2.setFont(mfd_gc.font_m);
    		g2.drawString(""+num,x-mfd_gc.digit_width_m/2,y-2);
    		g2.drawLine(x-mfd_gc.fctl_dx_spoiler_arrow,y-mfd_gc.fctl_dy_spoiler+mfd_gc.fctl_dy_spoiler_arrow,x,y-mfd_gc.fctl_dy_spoiler);
    		g2.drawLine(x+mfd_gc.fctl_dx_spoiler_arrow,y-mfd_gc.fctl_dy_spoiler+mfd_gc.fctl_dy_spoiler_arrow,x,y-mfd_gc.fctl_dy_spoiler);
    		break;		
    	}
    }
 
	
}
