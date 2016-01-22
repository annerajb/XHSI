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
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.annunciators.GearStatus.COL;
import net.sourceforge.xhsi.flightdeck.annunciators.GearStatus.WHEEL;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.SpoilerStatus;

public class Wheels extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    // If true, Wheel steering and braking status depends on hydraulic circuit status
    // If false, wheels and brakes are controlled without hydraulic, mainly mechanic
    boolean hydraulic_controls = false;
    // Airbus controls are based on 3 hydraulic circuits Blue, Green and Yellow
    boolean airbus_controls = false;
    // Boeing controls are base on 2 hydraulic circuits 1 and 2, plus backup reservoirs
    boolean boeing_controls = false;
    
    private Stroke original_stroke;
    
	public Wheels(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_WHEELS) {
			airbus_controls = this.avionics.is_qpac() || this.avionics.is_jar_a320neo();
			hydraulic_controls = airbus_controls || boeing_controls;
			// Page ID
			drawPageID(g2, "WHEEL");
			draw_airbus_speedbrake(g2);			
	        if ( this.aircraft.has_retractable_gear() ) {
	        	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
	        	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
	        	boolean a_skid = (hyd_g || hyd_y) && this.aircraft.nose_wheel_steering(); 
	        	boolean ri = !this.aircraft.on_ground() && this.aircraft.gear_is_down() && a_skid;
	        	boolean ri_left = ri || this.aircraft.brake_release_left();
	        	boolean ri_right = ri || this.aircraft.brake_release_right();
	        	
	            if ( this.aircraft.num_gears() == 3 ) {
	                drawTricycle(g2);
	                // Nose Gear (tire_id = 0)
	                drawNoseRelLegends(g2, mfd_gc.mfd_middle_x );
	                drawNoseRelValues(g2, this.aircraft.tire_psi(0), this.aircraft.tire_ref_psi(0), mfd_gc.mfd_middle_x - mfd_gc.wheel_nose_rel_dx);
	                drawNoseRelValues(g2, this.aircraft.tire_psi(0), this.aircraft.tire_ref_psi(0), mfd_gc.mfd_middle_x + mfd_gc.wheel_nose_rel_dx);
	                drawMainRelLegends(g2, mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx );
	                drawMainRelLegends(g2, mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx );
	                // Left Gear (tire_id = 1)
	                drawMainRelValues(g2, this.aircraft.tire_psi(1), this.aircraft.tire_ref_psi(1), this.aircraft.brake_temp(1), 1, mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx - mfd_gc.wheel_main_rel_dx, ri_left);
	                drawMainRelValues(g2, this.aircraft.tire_psi(1), this.aircraft.tire_ref_psi(1), this.aircraft.brake_temp(1), 2, mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx + mfd_gc.wheel_main_rel_dx, ri_left);
	                // Right Gear (tire_id = 2)
	                drawMainRelValues(g2, this.aircraft.tire_psi(2), this.aircraft.tire_ref_psi(2), this.aircraft.brake_temp(2), 3, mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx - mfd_gc.wheel_main_rel_dx, ri_right);
	                drawMainRelValues(g2, this.aircraft.tire_psi(2), this.aircraft.tire_ref_psi(2), this.aircraft.brake_temp(2), 4, mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx + mfd_gc.wheel_main_rel_dx, ri_right);
	            } else if ( this.aircraft.num_gears() > 0 ) {
	                // simple annunciators with an icon
	                drawAllGears(g2);
	            }	            
	        }
	        drawAutoBrakeStatus(g2);
	        drawNWSteeringStatus(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/4 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_xl/8);
	}

	/* for debug only
	private void drawGearDoor(Graphics2D g2) {
		int doors = this.aircraft.num_gear_doors();
		if (doors > 0) {
			for (int i=0; i<doors; i++) {
				g2.setColor(mfd_gc.ecam_markings_color);
				// if (i>=doors)g2.setColor(mfd_gc.ecam_caution_color); 
				g2.setFont(mfd_gc.font_xl);
				String str_door = i + ": " + Math.round(Math.abs(this.aircraft.get_gear_door(i))*100);
				g2.drawString(str_door, mfd_gc.panel_rect.x , mfd_gc.panel_rect.y + mfd_gc.line_height_xl + mfd_gc.line_height_xl * i);
			}
		}
	}
	*/
	   
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
    	/*
        g2.setColor(mfd_gc.markings_color);
        g2.setFont(mfd_gc.font_l);
        g2.drawString(speed_brk_str, mfd_gc.mfd_middle_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, speed_brk_str)/2, mfd_gc.wheel_y_spoiler_bottom + mfd_gc.line_height_l*2);
        */
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
        float door_nose=0;
        float door_left=0;
        float door_right=0;
        if (this.aircraft.num_gear_doors()>3) {
        	// Nose may be door 0 and door 1
        	door_nose = this.aircraft.get_gear_door(0);
        	door_left = this.aircraft.get_gear_door(2);
        	door_right = this.aircraft.get_gear_door(3);
        }
        drawTrikeWheel(g2, WHEEL.Nose, this.aircraft.get_gear(0), door_nose, false);
        drawTrikeWheel(g2, WHEEL.Left, this.aircraft.get_gear(1), door_left, false);
        drawTrikeWheel(g2, WHEEL.Right, this.aircraft.get_gear(2), door_right, false);
    }

    
    private void drawNoseRelValues(Graphics2D g2, float psi, float ref_psi, int x) {
    	String str_psi = ""+Math.round(psi);
    	g2.setColor(mfd_gc.ecam_markings_color);
    	scalePen(g2);
    	g2.drawArc(x - mfd_gc.wheel_main_rel_arc_r, mfd_gc.wheel_nose_rel_center-mfd_gc.wheel_main_rel_arc_r , 
				mfd_gc.wheel_main_rel_arc_r*2, mfd_gc.wheel_main_rel_arc_r*2, -65, -50);
    	resetPen(g2);
    	    	
    	g2.setFont(mfd_gc.font_l);
    	if ((psi < ref_psi*0.8)  || 
    		(psi > ref_psi*1.05)) { 
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_normal_color);
    	}    	
    	g2.drawString(str_psi, x+mfd_gc.digit_width_l*3/2-mfd_gc.get_text_width(g2, mfd_gc.font_l, str_psi), mfd_gc.wheel_nose_psi_value_y);    	
    }

    private void drawMainRelValues(Graphics2D g2, float psi, float ref_psi, float temp, int num, int x, boolean release_indicator) {
    	String str_psi = ""+Math.round(psi);
    	String str_temp = ""+Math.round(temp);
    	String str_rel = ""+num;
    	if (temp > 300.0f) { 
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_markings_color);
    	}
    	scalePen(g2);
    	g2.drawArc(x - mfd_gc.wheel_main_rel_arc_r, mfd_gc.wheel_main_rel_center-mfd_gc.wheel_main_rel_arc_r , 
    				mfd_gc.wheel_main_rel_arc_r*2, mfd_gc.wheel_main_rel_arc_r*2, 65, 50);
    	g2.drawArc(x - mfd_gc.wheel_main_rel_arc_r, mfd_gc.wheel_main_rel_center-mfd_gc.wheel_main_rel_arc_r , 
				mfd_gc.wheel_main_rel_arc_r*2, mfd_gc.wheel_main_rel_arc_r*2, -65, -50);
    	resetPen(g2);
    	
    	g2.setFont(mfd_gc.font_xl);    	
    	if (temp > 300.0f) { 
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_normal_color);
    	}
    	g2.drawString(str_temp, x+mfd_gc.digit_width_xl*3/2-mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_temp), mfd_gc.wheel_main_temp_value_y);
    	
    	g2.setFont(mfd_gc.font_l);
    	if ((psi < ref_psi*0.8)  || 
    		(psi > ref_psi*1.05)) { 
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_normal_color);
    	}
    	
    	g2.drawString(str_psi, x+mfd_gc.digit_width_l*3/2-mfd_gc.get_text_width(g2, mfd_gc.font_l, str_psi), mfd_gc.wheel_main_psi_value_y);
    	
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawString(str_rel, x-mfd_gc.get_text_width(g2, mfd_gc.font_l, str_rel)/2, mfd_gc.wheel_main_rel_value_y);
    	
    	// Release indicator (anti-skid active)
    	if (release_indicator) {
    		g2.setColor(mfd_gc.ecam_normal_color);
    		int x1 = x - mfd_gc.wheel_main_rel_arc_r*3/5;
    		int dx = mfd_gc.wheel_main_rel_arc_r/4;
    		int x2 = x1+dx;
    		int dy = mfd_gc.wheel_main_rel_arc_r/10;
    		int y1 = mfd_gc.wheel_main_rel_center - dy;
    		int y2 = mfd_gc.wheel_main_rel_center;
    		int y3 = mfd_gc.wheel_main_rel_center + dy;
    		int y4 = mfd_gc.wheel_main_rel_center + dy*2;

    		scalePen(g2);
    		g2.drawLine(x1, y1, x2, y1);
    		g2.drawLine(x1, y2, x2, y2);
    		g2.drawLine(x1, y3, x2, y3);
    		g2.drawLine(x1, y4, x2, y4);
    		x1 = x + mfd_gc.wheel_main_rel_arc_r*3/5;
    		x2 = x1-dx;
    		g2.drawLine(x1, y1, x2, y1);
    		g2.drawLine(x1, y2, x2, y2);
    		g2.drawLine(x1, y3, x2, y3);
    		g2.drawLine(x1, y4, x2, y4);
    		resetPen(g2);
    	}
    }
    
    private void drawNoseRelLegends(Graphics2D g2, int x) {
    	String str_psi = "PSI";
    	g2.setFont(mfd_gc.font_s);
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.drawString(str_psi, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_psi)/2, mfd_gc.wheel_nose_psi_legend_y);
    }
    
    private void drawMainRelLegends(Graphics2D g2, int x) {
    	String str_psi = "PSI";
    	String str_temp = "°C";
    	String str_rel = "-REL-";
    	g2.setFont(mfd_gc.font_s);
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.drawString(str_psi, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_psi)/2, mfd_gc.wheel_main_psi_legend_y);
    	g2.drawString(str_temp, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_temp)/2, mfd_gc.wheel_main_temp_legend_y);    	
    	g2.drawString(str_rel, x - mfd_gc.get_text_width(g2, mfd_gc.font_s, str_rel)/2, mfd_gc.wheel_main_rel_legend_y);
    }
    
    private void drawTrikeWheel(Graphics2D g2, WHEEL gearpos, float lowered, float door_pos, boolean legend) {

            int w_w = mfd_gc.wheel_tri_dy;
            int w_h = mfd_gc.line_height_l * 3;
            int w_x = 999;
            int w_y = 999;
            String w1_str = "ERROR";
            int w1_y = 999;
            String w2_str = "GEAR";
            int w2_y = 999;
            int door_y = 999;
            int door_dx = mfd_gc.wheel_tri_dx*5/4;
            scalePen(g2);
            AffineTransform original_at = g2.getTransform();
            Paint storedPaint = g2.getPaint();
            boolean gear_pos_display = this.aircraft.gear_indicators() || this.aircraft.gear_actuators();

            if (door_pos>0.0) g2.setColor(mfd_gc.ecam_caution_color); else g2.setColor(mfd_gc.ecam_normal_color);
            
            switch (gearpos) {
                case Nose:
                    w_x = mfd_gc.mfd_middle_x;
                    w_y = mfd_gc.wheel_nose_tri_y;
                    door_y = w_y - mfd_gc.wheel_tri_dy/2; 
                    w1_str = "NOSE";
                    g2.drawArc(w_x-door_dx-mfd_gc.wheel_door_axis_r, door_y-mfd_gc.wheel_door_axis_r, mfd_gc.wheel_door_axis_r*2, mfd_gc.wheel_door_axis_r*2, 0, 360);
                    g2.drawArc(w_x+door_dx-mfd_gc.wheel_door_axis_r, door_y-mfd_gc.wheel_door_axis_r, mfd_gc.wheel_door_axis_r*2, mfd_gc.wheel_door_axis_r*2, 0, 360);
                    g2.rotate(Math.toRadians(door_pos*100), w_x - door_dx, door_y );
                    g2.drawLine(w_x - door_dx, door_y, w_x- mfd_gc.wheel_tri_dx/4,  door_y );
                    g2.setTransform(original_at);
                    g2.rotate(Math.toRadians(door_pos*-100), w_x + door_dx, door_y );
                    g2.drawLine(w_x + mfd_gc.wheel_tri_dx/4, door_y, w_x+ door_dx,  door_y );
                    break;
                case Left:
                    w_x = mfd_gc.mfd_middle_x - mfd_gc.wheel_main_tri_dx;
                    w_y = mfd_gc.wheel_main_tri_y;
                    door_y = w_y - mfd_gc.wheel_tri_dy/2; 
                    w1_str = "LEFT";
                    g2.drawArc(w_x+door_dx-mfd_gc.wheel_door_axis_r, door_y-mfd_gc.wheel_door_axis_r, mfd_gc.wheel_door_axis_r*2, mfd_gc.wheel_door_axis_r*2, 0, 360);
                    g2.rotate(Math.toRadians(door_pos*-100), w_x+ door_dx, door_y );
                    g2.drawLine(w_x - mfd_gc.wheel_tri_dx*3/4, door_y, w_x+ door_dx,  door_y );
                    break;
                case Right:
                    w_x = mfd_gc.mfd_middle_x + mfd_gc.wheel_main_tri_dx;
                    w_y = mfd_gc.wheel_main_tri_y;
                    door_y = w_y - mfd_gc.wheel_tri_dy/2; 
                    w1_str = "RIGHT";
                    g2.drawArc(w_x-door_dx-mfd_gc.wheel_door_axis_r, door_y-mfd_gc.wheel_door_axis_r, mfd_gc.wheel_door_axis_r*2, mfd_gc.wheel_door_axis_r*2, 0, 360);
                    g2.rotate(Math.toRadians(door_pos*100), w_x - door_dx, door_y );
                    g2.drawLine(w_x - door_dx, door_y, w_x+ mfd_gc.wheel_tri_dx*3/4,  door_y );
                    break;
            }
            g2.setTransform(original_at);
            int wd_x = w_x+mfd_gc.wheel_main_tri_shift_x;
            int wd_y = w_y+mfd_gc.wheel_main_tri_shift_y;
            int tri1_x [] = { w_x, w_x + mfd_gc.wheel_tri_dx, w_x - mfd_gc.wheel_tri_dx };
            int tri1_y [] = { w_y + mfd_gc.wheel_tri_dy, w_y, w_y };
            int tri2_x [] = { wd_x, wd_x + mfd_gc.wheel_tri_dx, wd_x - mfd_gc.wheel_tri_dx };
            int tri2_y [] = { wd_y + mfd_gc.wheel_tri_dy, wd_y, wd_y };

            g2.setColor(mfd_gc.instrument_background_color);
            // g2.drawPolygon(tri_x, tri_y, 3);

            if ( lowered == 1.0f && gear_pos_display) {
                if (this.aircraft.gear_indicators()) {
                	g2.setColor(mfd_gc.ecam_normal_color);
                	g2.drawPolygon(tri1_x, tri1_y, 3);
                }
                if (this.aircraft.gear_actuators()) {
                	g2.setPaint(mfd_gc.wheel_paint_g);                
                	g2.fillPolygon(tri2_x, tri2_y, 3);
                	g2.setColor(mfd_gc.ecam_normal_color);
                	g2.drawPolygon(tri2_x, tri2_y, 3);
                }
            } else if (lowered > 0.01f && gear_pos_display) {
                if (this.aircraft.gear_indicators()) {
                    g2.setColor(mfd_gc.ecam_warning_color);
                    g2.drawPolygon(tri1_x, tri1_y, 3);
                }
                if (this.aircraft.gear_actuators()) {
                	g2.setPaint(mfd_gc.wheel_paint_r);
                	g2.fillPolygon(tri2_x, tri2_y, 3);
                	g2.setColor(mfd_gc.ecam_warning_color);
                	g2.drawPolygon(tri2_x, tri2_y, 3);
                }
            }
            g2.setPaint(storedPaint);
                
            g2.setColor(mfd_gc.ecam_markings_color);
            if (legend) {
            	g2.setFont(mfd_gc.font_l);
            	w1_y = w_y + mfd_gc.wheel_tri_dy + mfd_gc.line_height_l*28/20;
            	w2_y = w_y + mfd_gc.wheel_tri_dy + mfd_gc.line_height_l*48/20;            	
            	g2.drawString(w1_str, w_x  - mfd_gc.get_text_width(g2, mfd_gc.font_l, w1_str)/2, w1_y);
            	g2.drawString(w2_str, w_x  - mfd_gc.get_text_width(g2, mfd_gc.font_l, w2_str)/2, w2_y);
            }

            // Wheel door               
            g2.drawLine(w_x - mfd_gc.wheel_tri_dx*2, door_y, w_x - door_dx,  door_y );
            g2.drawLine(w_x + door_dx, door_y, w_x+ mfd_gc.wheel_tri_dx*2,  door_y );
            resetPen(g2);
            
            // Failures
            if (!this.aircraft.gear_indicators()) {
            	g2.setColor(mfd_gc.ecam_caution_color);
            	g2.setFont(mfd_gc.font_xxl);
            	g2.drawString("X", w_x - door_dx + mfd_gc.digit_width_xxl/2,door_y+mfd_gc.line_height_xxl*10/8);
            }
            if (!this.aircraft.gear_actuators()) {
            	g2.setColor(mfd_gc.ecam_caution_color);
            	g2.setFont(mfd_gc.font_xxl);
            	g2.drawString("X", w_x + door_dx - mfd_gc.digit_width_xxl*3/2,door_y+mfd_gc.line_height_xxl*10/8);
            }
            
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
	
    private void drawNWSteeringStatus(Graphics2D g2) {
    	// On A320, N/W Steering relies on the Yellow circuit.
    	// float green_circuit = this.aircraft.get_hyd_press(0);
    	
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
    	boolean a_skid = (hyd_g || hyd_y) && this.aircraft.nose_wheel_steering(); // or nw_steering/a.skid off
    	boolean nw_steering = (hyd_y || ! airbus_controls) && this.aircraft.nose_wheel_steering(); // or nw_steering/a.skid off
    	boolean norm_brk = (!airbus_controls) || ( hyd_g && this.aircraft.nose_wheel_steering());
    	Color col_g = hyd_g ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_y = hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_a_skid = a_skid ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_nw_steering = nw_steering ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_norm_brk = norm_brk ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;

    	// N/W Steering indicator
    	String nw_str = "N/W STEERING";
        g2.setColor(col_nw_steering);
        g2.setFont(mfd_gc.font_l);
        g2.drawString(nw_str, mfd_gc.wheel_nw_steering_msg_x, mfd_gc.wheel_nw_steering_msg_y);
       
    	// 1 hydraulic box
    	// Hydraulic box "Y"
    	// Text in green is circuit ok, amber when low pressure (<2000 psi)
    	if (airbus_controls) {

    		g2.setColor(mfd_gc.color_airbusgray.darker());

    		g2.fillRect(mfd_gc.wheel_nw_steering_hydr_x , mfd_gc.wheel_nw_steering_msg_y - mfd_gc.fctl_box_height*7/8,
    				mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    		g2.fillRect(mfd_gc.wheel_brake_hydr_x , mfd_gc.wheel_norm_brake_msg_y - mfd_gc.fctl_box_height*7/8,
    				mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    		g2.fillRect(mfd_gc.wheel_brake_hydr_x , mfd_gc.wheel_alt_brake_msg_y - mfd_gc.fctl_box_height*7/8,
    				mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);

    		g2.setFont(mfd_gc.font_l);
    		String hyd_str_y="Y";
    		String hyd_str_g="G";
    		g2.setColor(col_y);
    		g2.drawString(hyd_str_y, 
    				mfd_gc.wheel_nw_steering_hydr_x + mfd_gc.fctl_dx_box_width*1/8,
    				mfd_gc.wheel_nw_steering_msg_y);
    		g2.drawString(hyd_str_y, 
    				mfd_gc.wheel_brake_hydr_x + mfd_gc.fctl_dx_box_width*1/8,
    				mfd_gc.wheel_alt_brake_msg_y);
    		g2.setColor(col_g);
    		g2.drawString(hyd_str_g, 
    				mfd_gc.wheel_brake_hydr_x + mfd_gc.fctl_dx_box_width*1/8,
    				mfd_gc.wheel_norm_brake_msg_y);
    	
    		// A320 FCOM 1.32.30 p1 The anti-skid deactivates when ground speed is less than 20 kts.
    		g2.setColor(col_a_skid);
    		String a_skid_str = "ANTI SKID";
    		g2.drawString(a_skid_str, mfd_gc.wheel_nw_askid_msg_x , mfd_gc.wheel_nw_askid_msg_y);

    		g2.setColor(col_norm_brk);
    		String norm_brk_str = "NORM BRK";
    		g2.drawString(norm_brk_str, mfd_gc.wheel_brake_msg_x , mfd_gc.wheel_norm_brake_msg_y);

    		// depends on ACCU PR
    		// A320. FCOM 1.32.30 Accumulator on Y circuit
    		// A330. FCOM 1.32.30 Accumulator on B circuit
    		g2.setColor(this.aircraft.brake_pressure_accu() > 0.1f ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color);
    		String alt_brk_str = "ALTN BRK";
    		g2.drawString(alt_brk_str, mfd_gc.wheel_brake_msg_x , mfd_gc.wheel_alt_brake_msg_y);
    		
    		// Brake accumulator pressure
    		scalePen(g2);
    		if (hyd_y) {
    			g2.setColor(mfd_gc.ecam_normal_color);
    			String accu_str = "ACCU PR";
    			g2.drawString(accu_str, mfd_gc.wheel_accu_brake_msg_x , mfd_gc.wheel_accu_brake_msg_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x1, mfd_gc.wheel_accu_arrow_y, mfd_gc.wheel_accu_arrow_x1 + mfd_gc.wheel_accu_arrow_w, mfd_gc.wheel_accu_arrow_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x1, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_h, mfd_gc.wheel_accu_arrow_x1, mfd_gc.wheel_accu_arrow_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x1 + mfd_gc.wheel_accu_arrow_w - mfd_gc.wheel_accu_arrow_dx, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_dy, mfd_gc.wheel_accu_arrow_x1 + mfd_gc.wheel_accu_arrow_w, mfd_gc.wheel_accu_arrow_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x1 + mfd_gc.wheel_accu_arrow_w - mfd_gc.wheel_accu_arrow_dx, mfd_gc.wheel_accu_arrow_y+mfd_gc.wheel_accu_arrow_dy, mfd_gc.wheel_accu_arrow_x1 + mfd_gc.wheel_accu_arrow_w, mfd_gc.wheel_accu_arrow_y);
    		} else if (this.aircraft.brake_pressure_accu() > 0.1f) {
    			g2.setColor(mfd_gc.ecam_normal_color);
        		String accu_str = "ACCU ONLY";
        		g2.drawString(accu_str, mfd_gc.wheel_accu_brake_msg_x + mfd_gc.digit_width_l*5/3 , mfd_gc.wheel_accu_brake_msg_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x2, mfd_gc.wheel_accu_arrow_y, mfd_gc.wheel_accu_arrow_x2+mfd_gc.digit_width_l, mfd_gc.wheel_accu_arrow_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x2, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_h, mfd_gc.wheel_accu_arrow_x2, mfd_gc.wheel_accu_arrow_y);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x2, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_h, mfd_gc.wheel_accu_arrow_x2 - mfd_gc.wheel_accu_arrow_dx, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_h+mfd_gc.wheel_accu_arrow_dy);
    			g2.drawLine(mfd_gc.wheel_accu_arrow_x2, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_h, mfd_gc.wheel_accu_arrow_x2 + mfd_gc.wheel_accu_arrow_dx, mfd_gc.wheel_accu_arrow_y-mfd_gc.wheel_accu_arrow_h+mfd_gc.wheel_accu_arrow_dy);
    		} else {
    			g2.setColor(mfd_gc.ecam_caution_color);
    			String accu_str = "ACCU PR";
    			g2.drawString(accu_str, mfd_gc.wheel_accu_brake_msg_x , mfd_gc.wheel_accu_brake_msg_y);   			
    		}
    		resetPen(g2);
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
