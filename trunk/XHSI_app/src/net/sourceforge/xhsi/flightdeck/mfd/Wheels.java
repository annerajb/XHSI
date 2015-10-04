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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.annunciators.GearStatus.COL;
import net.sourceforge.xhsi.flightdeck.annunciators.GearStatus.WHEEL;
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
	        if ( this.aircraft.has_retractable_gear() ) {
	            if ( this.aircraft.num_gears() == 3 ) {
	                drawTricycle(g2);
	                drawRelLegends(g2, mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx );
	                drawRelLegends(g2, mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx );
	                drawRelValues(g2, 0.0f, 0.0f, 1, mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx - mfd_gc.wheel_main_rel_dx);
	                drawRelValues(g2, 0.0f, 0.0f, 2, mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx + mfd_gc.wheel_main_rel_dx);
	                drawRelValues(g2, 0.0f, 0.0f, 3, mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx - mfd_gc.wheel_main_rel_dx);
	                drawRelValues(g2, 0.0f, 0.0f, 4, mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx + mfd_gc.wheel_main_rel_dx);
	            } else if ( this.aircraft.num_gears() > 0 ) {
	                // simple annunciators with an icon
	                drawAllGears(g2);
	            }
	        }
	        drawAutoBrakeStatus(g2);
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
    	String speed_brk_str = "SPEED BRAKES";
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr, mfd_gc.wheel_y_spoiler_top, 1, this.aircraft.get_spoiler_status_left(0));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr - mfd_gc.fctl_dx_spoiler_step, mfd_gc.wheel_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*1 , 2, this.aircraft.get_spoiler_status_left(1));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr - mfd_gc.fctl_dx_spoiler_step*2 , mfd_gc.wheel_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*2, 3, this.aircraft.get_spoiler_status_left(2));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_ctr - mfd_gc.fctl_dx_spoiler_step*3, mfd_gc.wheel_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*3, 4, this.aircraft.get_spoiler_status_left(3));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_spoiler_bdr, mfd_gc.wheel_y_spoiler_bottom, 5, this.aircraft.get_spoiler_status_left(4));
    	
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr, mfd_gc.wheel_y_spoiler_top, 1, this.aircraft.get_spoiler_status_right(0));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr + mfd_gc.fctl_dx_spoiler_step , mfd_gc.wheel_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*1, 2, this.aircraft.get_spoiler_status_right(1));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr + mfd_gc.fctl_dx_spoiler_step*2 , mfd_gc.wheel_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*2, 3, this.aircraft.get_spoiler_status_right(2));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_ctr + mfd_gc.fctl_dx_spoiler_step*3 , mfd_gc.wheel_y_spoiler_top + mfd_gc.fctl_dy_spoiler_step*3, 4, this.aircraft.get_spoiler_status_right(3));
    	draw_speed_brake_arrow(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_spoiler_bdr, mfd_gc.wheel_y_spoiler_bottom, 5, this.aircraft.get_spoiler_status_right(4));
        g2.setColor(mfd_gc.markings_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawString(speed_brk_str, mfd_gc.mfd_middle_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, speed_brk_str)/2, mfd_gc.wheel_y_spoiler_bottom + mfd_gc.line_height_l*2);
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

    private void drawAllGears(Graphics2D g2) {

        int gears = this.aircraft.num_gears();

        int firstleft;

        if ( gears % 2 == 1 ) {
            // odd number of gears; draw the first one at the top in the center
            drawAnyWheel(g2, 0, COL.Center, this.aircraft.get_gear(0));
            firstleft = 1;
        } else {
            firstleft = 0;
        }
        int row = 1;
        if ( gears > 1 ) {
            for ( int i=firstleft; i<gears; i+=2) {
                drawAnyWheel(g2, row, COL.Left, this.aircraft.get_gear(i));
                drawAnyWheel(g2, row, COL.Right, this.aircraft.get_gear(i+1));
                row++;
            }
        }

    }


    private void drawAnyWheel(Graphics2D g2, int row, COL col, float lowered) {

            int w_w = mfd_gc.wheel_tri_dy;
            int w_h = mfd_gc.line_height_l * 2;
            int w_x;
            int w_y;

            if ( col == COL.Center ) {
                w_x = mfd_gc.mfd_middle_x;
            } else if ( col == COL.Left ) {
                w_x = mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx;
            } else {
                w_x = mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx; 
            }
            w_y = mfd_gc.wheel_main_tri_y + (3-row)*(w_h+w_w/12);

            int tri_x [] = { w_x, w_x + mfd_gc.wheel_tri_dx, w_x - mfd_gc.wheel_tri_dx };
            int tri_y [] = { w_y + mfd_gc.wheel_tri_dy, w_y, w_y };
            g2.setColor(mfd_gc.instrument_background_color);
            g2.drawPolygon(tri_x, tri_y, 3);
            
            if ( ( ! mfd_gc.powered ) || (lowered == 0.0f) ) {
                g2.setColor(mfd_gc.instrument_background_color.brighter());
            } else if ( lowered == 1.0f ) {
                g2.setColor(mfd_gc.ecam_normal_color);
            } else {
                g2.setColor(mfd_gc.ecam_warning_color);
            }
            
            g2.fillPolygon(tri_x, tri_y, 3);

    }


    private void drawTricycle(Graphics2D g2) {

        g2.setStroke(new BasicStroke(3.0f * mfd_gc.grow_scaling_factor));

        drawTrikeWheel(g2, WHEEL.Nose, this.aircraft.get_gear(0));
        drawTrikeWheel(g2, WHEEL.Left, this.aircraft.get_gear(1));
        drawTrikeWheel(g2, WHEEL.Right, this.aircraft.get_gear(2));

    }

    private void drawRelValues(Graphics2D g2, float psi, float temp, int num, int x) {
    	String str_psi = "XX";
    	String str_temp = "XX";
    	String str_rel = ""+num;
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawArc(x - mfd_gc.wheel_main_rel_arc_r, mfd_gc.wheel_main_rel_center-mfd_gc.wheel_main_rel_arc_r , 
    				mfd_gc.wheel_main_rel_arc_r*2, mfd_gc.wheel_main_rel_arc_r*2, 65, 50);
    	g2.drawArc(x - mfd_gc.wheel_main_rel_arc_r, mfd_gc.wheel_main_rel_center-mfd_gc.wheel_main_rel_arc_r , 
				mfd_gc.wheel_main_rel_arc_r*2, mfd_gc.wheel_main_rel_arc_r*2, -65, -50);

    	g2.setColor(mfd_gc.ecam_caution_color);
    	g2.setFont(mfd_gc.font_xl);
    	g2.drawString(str_temp, x, mfd_gc.wheel_main_temp_value_y);
    	g2.setFont(mfd_gc.font_l);
    	g2.drawString(str_psi, x, mfd_gc.wheel_main_psi_value_y);
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawString(str_rel, x, mfd_gc.wheel_main_rel_value_y);
    }

    private void drawRelLegends(Graphics2D g2, int x) {
    	String str_psi = "PSI";
    	String str_temp = "°C";
    	String str_rel = "-REL-";
    	g2.setFont(mfd_gc.font_s);
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.drawString(str_psi, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_psi)/2, mfd_gc.wheel_main_psi_value_y);
    	g2.drawString(str_temp, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_temp)/2, mfd_gc.wheel_main_temp_value_y);
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawString(str_rel, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_rel)/2, mfd_gc.wheel_main_rel_value_y);
    }
    
    private void drawTrikeWheel(Graphics2D g2, WHEEL gearpos, float lowered) {

            int w_w = mfd_gc.wheel_tri_dy;
            int w_h = mfd_gc.line_height_l * 3;
            int w_x = 999;
            int w_y = 999;
            String w1_str = "ERROR";
            int w1_y = 999;
            String w2_str = "GEAR";
            int w2_y = 999;

            switch (gearpos) {
                case Nose:
                    w_x = mfd_gc.mfd_middle_x;
                    w_y = mfd_gc.wheel_nose_tri_y;
                    w1_str = "NOSE";
                    break;
                case Left:
                    w_x = mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx;
                    w_y = mfd_gc.wheel_main_tri_y;
                    w1_str = "LEFT";
                    break;
                case Right:
                    w_x = mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx;
                    w_y = mfd_gc.wheel_main_tri_y;
                    w1_str = "RIGHT";
                    break;
            }

            int tri_x [] = { w_x, w_x + mfd_gc.wheel_tri_dx, w_x - mfd_gc.wheel_tri_dx };
            int tri_y [] = { w_y + mfd_gc.wheel_tri_dy, w_y, w_y };
            g2.setColor(mfd_gc.instrument_background_color);
            g2.drawPolygon(tri_x, tri_y, 3);

            if ( ( ! mfd_gc.powered ) || (lowered == 0.0f) ) {
                g2.setColor(mfd_gc.instrument_background_color.brighter());
            } else if ( lowered == 1.0f ) {
                g2.setColor(mfd_gc.ecam_normal_color);
            } else {
                g2.setColor(mfd_gc.ecam_warning_color);
            }
            
            g2.fillPolygon(tri_x, tri_y, 3);
            g2.setFont(mfd_gc.font_l);
            w1_y = w_y + mfd_gc.wheel_tri_dy + mfd_gc.line_height_l*28/20;
            w2_y = w_y + mfd_gc.wheel_tri_dy + mfd_gc.line_height_l*48/20;
            g2.setColor(mfd_gc.markings_color);
            g2.drawString(w1_str, w_x  - mfd_gc.get_text_width(g2, mfd_gc.font_l, w1_str)/2, w1_y);
            g2.drawString(w2_str, w_x  - mfd_gc.get_text_width(g2, mfd_gc.font_l, w2_str)/2, w2_y);

    }
    
    private void drawAutoBrakeStatus(Graphics2D g2) {
        // AUTO BRK
        int autobrake = this.aircraft.auto_brake();
        String str_brake = ""+autobrake;
        String str_auto = "AUTO BRK";
        boolean auto_brake_on = false;
        switch (autobrake) {           
            case -1 :
            	str_brake = "RTO";
            	auto_brake_on = true;
                break;
            case 1 :
            	str_brake = "LOW";
            	auto_brake_on = true;
                break;
            case 2 :
            	str_brake = "MED";
            	auto_brake_on = true;
                break;
            case 3 :
            	str_brake = "MED";
            	auto_brake_on = true;
                break;
            case 4 :
            	str_brake = "MAX";
            	auto_brake_on = true;
                break;
            default :
                break;

        }
        if (auto_brake_on) {
        	g2.setFont(mfd_gc.font_l);
        	g2.setColor(mfd_gc.ecam_normal_color);
            g2.drawString(str_auto, mfd_gc.mfd_middle_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_auto)/2, mfd_gc.wheel_autobrk_legend_y);
            g2.drawString(str_brake, mfd_gc.mfd_middle_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_brake)/2, mfd_gc.wheel_autobrk_value_y);
        }

    }
	
}
