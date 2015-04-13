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
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class FlightControls extends MFDSubcomponent {
	
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private DecimalFormat one_decimal_format;
    private DecimalFormatSymbols format_symbols;
    
	public FlightControls(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
        one_decimal_format = new DecimalFormat("#0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);
	}
	
	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_FCTL) {
			// Page ID
			drawPageID(g2, "FCTL");
			/* 
            g2.setColor(mfd_gc.instrument_background_color);
            g2.drawRect(mfd_gc.controls_x, mfd_gc.controls_y, mfd_gc.controls_w, mfd_gc.controls_h);
            g2.drawLine(mfd_gc.controls_x, mfd_gc.controls_y + mfd_gc.controls_h/2, mfd_gc.controls_x + mfd_gc.controls_w, mfd_gc.controls_y + mfd_gc.controls_h/2);
            g2.drawLine(mfd_gc.controls_x + mfd_gc.controls_w*60/100, mfd_gc.controls_y, mfd_gc.controls_x + mfd_gc.controls_w*60/100, mfd_gc.controls_y + mfd_gc.controls_h/2);
            g2.drawLine(mfd_gc.controls_x + mfd_gc.controls_w*60/100, mfd_gc.controls_y + mfd_gc.controls_h*30/100, mfd_gc.controls_x + mfd_gc.controls_w, mfd_gc.controls_y + mfd_gc.controls_h*30/100);
            */
			draw_airbus_speedbrake(g2);
			draw_airbus_aileron(g2);
			draw_airbus_elevator(g2);
			draw_airbus_rudder(g2);
			draw_airbus_pitch_trim(g2);
			draw_airbus_roll_trim(g2);
            //draw_trim(g2);
            //draw_flaps_speedbrake(g2);
            //draw_gears(g2);
            //draw_parkbrake(g2);
            //draw_autobrake(g2);
		}
	}

    
    private void drawPageID(Graphics2D g2, String page_str) {
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xxl);
    	int page_id_x = mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str)/2;
    	int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10;     	
        g2.drawString(page_str, page_id_x, page_id_y);
        g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_m/8);
    }
  
    private void draw_airbus_speedbrake(Graphics2D g2) {
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_b, mfd_gc.fctl_y_wing_top, 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid1 );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_b, mfd_gc.fctl_y_wing_top, 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_b, mfd_gc.fctl_y_wing_top + mfd_gc.fctl_dy_wing_mark);
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid1, 
			        mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid1 + mfd_gc.fctl_dy_wing_mark );
    	
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_a, mfd_gc.fctl_y_wing_mid2, 
			        mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_bottom );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_a, mfd_gc.fctl_y_wing_mid2, 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_a, mfd_gc.fctl_y_wing_mid2 - mfd_gc.fctl_dy_wing_mark );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_bottom , 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_bottom - mfd_gc.fctl_dy_wing_mark );
    	
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_b, mfd_gc.fctl_y_wing_top, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid1 );
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_b, mfd_gc.fctl_y_wing_top, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_b, mfd_gc.fctl_y_wing_top + mfd_gc.fctl_dy_wing_mark);
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid1, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid1 + mfd_gc.fctl_dy_wing_mark );

    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_a, mfd_gc.fctl_y_wing_mid2, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_bottom );
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_a, mfd_gc.fctl_y_wing_mid2, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_a, mfd_gc.fctl_y_wing_mid2 - mfd_gc.fctl_dy_wing_mark );
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_bottom , 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_bottom - mfd_gc.fctl_dy_wing_mark );
    	
    	// "SPD BRK"
    	g2.setFont(mfd_gc.font_xl);
    	String spd_brk_str="SPD BRK";
    	g2.drawString(spd_brk_str, mfd_gc.fctl_mid_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, spd_brk_str)/2 , mfd_gc.fctl_y_ail_top);

    	// Hydrolic box "GBY"
    	// Text in green is circuit ok, amber when low pressure (<2000 psi)
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_box , mfd_gc.fctl_y_wing_box, mfd_gc.fctl_dx_wing_box*2, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	String hyd_str="GBY";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_wing_box + mfd_gc.line_height_l);
    	
    	// indicators
    	float speedbrake = this.aircraft.get_speed_brake();
    	int spd_dy = Math.round(speedbrake * mfd_gc.mfd_size * 50/1000);
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c , mfd_gc.fctl_y_wing_mid2, 
    				 mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_c, mfd_gc.fctl_y_wing_mid2 - spd_dy);
    }
    
    private void draw_speed_brake_arrow(Graphics2D g2, int pos, int status){
    	if (status > 1) {
    		g2.setColor(mfd_gc.ecam_caution_color);
    	} else {
    		g2.setColor(mfd_gc.ecam_normal_color);
    	}    	
    }
    
    private void draw_airbus_aileron(Graphics2D g2) {
    	int aileron_range = Math.round(this.aircraft.get_aileron_max_up() + this.aircraft.get_aileron_max_down());
    	int aileron_mark = mfd_gc.fctl_y_ail_top + (mfd_gc.fctl_y_ail_bottom-mfd_gc.fctl_y_ail_top)*Math.round(this.aircraft.get_aileron_max_up())/aileron_range;
    	int left_aileron = aileron_mark + Math.round(this.aircraft.get_left_aileron_pos()*(mfd_gc.fctl_y_ail_bottom-mfd_gc.fctl_y_ail_top)/aileron_range);
    	int right_aileron = aileron_mark + Math.round(this.aircraft.get_right_aileron_pos()*(mfd_gc.fctl_y_ail_bottom-mfd_gc.fctl_y_ail_top)/aileron_range);
    	
    	g2.setColor(mfd_gc.ecam_markings_color);
    	// right
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail, mfd_gc.fctl_y_ail_top + mfd_gc.fctl_dy_ail_end, 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail, mfd_gc.fctl_y_ail_bottom - mfd_gc.fctl_dy_ail_end);
    	g2.drawRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_ail_end/2, mfd_gc.fctl_y_ail_top, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_ail_end/2, mfd_gc.fctl_y_ail_bottom - mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_ail_end/3, aileron_mark,
    				mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail + mfd_gc.fctl_dx_ail_end/3, aileron_mark);
    	// "R AIL"
    	g2.setFont(mfd_gc.font_xl);
    	String ail_str="R";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_ail_top + mfd_gc.line_height_l * 6/4);
    	ail_str="AIL";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_ail_top + mfd_gc.line_height_l * 11/4);
    	
    	// left
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail, mfd_gc.fctl_y_ail_top + mfd_gc.fctl_dy_ail_end, 
    			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail, mfd_gc.fctl_y_ail_bottom - mfd_gc.fctl_dy_ail_end);
    	g2.drawRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_ail_end/2, mfd_gc.fctl_y_ail_top, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_ail_end/2, mfd_gc.fctl_y_ail_bottom - mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_ail_end/3, aileron_mark,
					mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail + mfd_gc.fctl_dx_ail_end/3, aileron_mark);

    	// "L AIL"
    	g2.setFont(mfd_gc.font_xl);
    	ail_str="L";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_ail_top + mfd_gc.line_height_l * 6/4);
    	ail_str="AIL";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_ail_top + mfd_gc.line_height_l * 11/4);
    	
    	// 4 hydrolic box
    	// Hydrolic box "GBY"
    	// Text in green is circuit ok, amber when low pressure (<2000 psi)
    	// LEFT 1
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	String hyd_str="BG";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);

    	// LEFT 2
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box2 - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	hyd_str="BY";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box2 - mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);

    	// RIGHT 1
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1  , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	hyd_str="BG";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);

    	// RIGHT 2
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box2  , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	hyd_str="BY";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box2 + mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);

    	// Aileron position
    	int r_ail_tri_x [] = { 
    			mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_tri, 
    			mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail, 
    			mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail - mfd_gc.fctl_dx_tri
    	};
    	int l_ail_tri_x [] = { 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail + mfd_gc.fctl_dx_tri, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail + mfd_gc.fctl_dx_tri
    	};
    	int r_ail_tri_y [] = {
    			right_aileron + mfd_gc.fctl_dy_tri,
    			right_aileron,
    			right_aileron - mfd_gc.fctl_dy_tri
    	};
    	int l_ail_tri_y [] = {
    			left_aileron + mfd_gc.fctl_dy_tri,
    			left_aileron,
    			left_aileron - mfd_gc.fctl_dy_tri
    	};
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawPolygon(r_ail_tri_x, r_ail_tri_y, 3);
    	g2.drawPolygon(l_ail_tri_x, l_ail_tri_y, 3);
    	
    	
    }

   
    private void draw_airbus_elevator(Graphics2D g2) {
    	int elevator_range = Math.round(this.aircraft.get_elev_max_up() + this.aircraft.get_elev_max_down());
    	int elevator_mark = mfd_gc.fctl_y_elev_top + (mfd_gc.fctl_y_elev_bottom-mfd_gc.fctl_y_elev_top)*Math.round(this.aircraft.get_elev_max_up())/elevator_range;
    	int left_elevator = elevator_mark + Math.round(this.aircraft.get_left_elev_pos()*(mfd_gc.fctl_y_elev_bottom-mfd_gc.fctl_y_elev_top)/elevator_range);
    	int right_elevator = elevator_mark + Math.round(this.aircraft.get_right_elev_pos()*(mfd_gc.fctl_y_elev_bottom-mfd_gc.fctl_y_elev_top)/elevator_range);
    	
    	g2.setColor(mfd_gc.ecam_markings_color);
    	// right elevator
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev, mfd_gc.fctl_y_elev_top, 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev, mfd_gc.fctl_y_elev_bottom );
    	g2.drawRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev, mfd_gc.fctl_y_elev_top, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev, mfd_gc.fctl_y_elev_bottom - mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev - mfd_gc.fctl_dx_ail_end/3, elevator_mark,
    				mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev + mfd_gc.fctl_dx_ail_end/3, elevator_mark);

    	// "R EVEL"
    	g2.setFont(mfd_gc.font_xl);
    	String ail_str="R";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_elev_top + mfd_gc.line_height_l * 6/4);
    	ail_str="ELEV";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_elev_top + mfd_gc.line_height_l * 11/4);
    	
    	// left elevator
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev, mfd_gc.fctl_y_elev_top, 
    			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev, mfd_gc.fctl_y_elev_bottom );
    	g2.drawRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev - mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_y_elev_top, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev - mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_y_elev_bottom - mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end, mfd_gc.fctl_dy_ail_end);
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev - mfd_gc.fctl_dx_ail_end/3, elevator_mark,
				    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev + mfd_gc.fctl_dx_ail_end/3, elevator_mark);
    	
    	// "L EVEL"
    	g2.setFont(mfd_gc.font_xl);
    	ail_str="L";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_elev_top + mfd_gc.line_height_l * 6/4);
    	ail_str="ELEV";
    	g2.drawString(ail_str, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_txt - mfd_gc.get_text_width(g2, mfd_gc.font_xl, ail_str)/2 , mfd_gc.fctl_y_elev_top + mfd_gc.line_height_l * 11/4);
    	
    	// 2 hydrolic box
    	// Hydrolic box "GY"
    	// Text in green is circuit ok, amber when low pressure (<2000 psi)
    	// LEFT
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	String hyd_str="BG";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_elev_box_top + mfd_gc.line_height_l);
    	// RIGHT
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	hyd_str="YB";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_elev_box_top + mfd_gc.line_height_l);   	
    	
    	// Elevators position
    	int r_elev_tri_x [] = { 
    			mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev - mfd_gc.fctl_dx_tri, 
    			mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev, 
    			mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev - mfd_gc.fctl_dx_tri
    	};
    	int l_elev_tri_x [] = { 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev + mfd_gc.fctl_dx_tri, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev, 
    			mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev + mfd_gc.fctl_dx_tri
    	};
    	int r_elev_tri_y [] = {
    			right_elevator + mfd_gc.fctl_dy_tri,
    			right_elevator,
    			right_elevator - mfd_gc.fctl_dy_tri
    	};
    	int l_elev_tri_y [] = {
    			left_elevator + mfd_gc.fctl_dy_tri,
    			left_elevator,
    			left_elevator - mfd_gc.fctl_dy_tri
    	};
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawPolygon(r_elev_tri_x, r_elev_tri_y, 3);
    	g2.drawPolygon(l_elev_tri_x, l_elev_tri_y, 3);
    	
    }
   
    
    private void draw_airbus_rudder(Graphics2D g2) {
    	//int rudder_range = Math.round(this.aircraft.get_rudder_max_lr());
    	int rudder_range = 30;
    	int yaw = Math.round( this.aircraft.get_yaw_trim() * -rudder_range );
    	int rudder = Math.round(this.aircraft.get_rudder_pos()*-45/rudder_range);
    	AffineTransform original_at = g2.getTransform();
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_xl);
    	String rudder_str = "RUD";
    	g2.drawString(rudder_str, mfd_gc.fctl_mid_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, rudder_str)/2, mfd_gc.fctl_y_rud_box_top - mfd_gc.line_height_l);

    	// Hydrolic box "GBY"
    	// Text in green is circuit ok, amber when low pressure (<2000 psi)
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_box , mfd_gc.fctl_y_rud_box_top, mfd_gc.fctl_dx_wing_box*2, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	String hyd_str="GBY";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_rud_box_top + mfd_gc.line_height_l);
    
    	
    	// right elevator
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_rud_b, mfd_gc.fctl_y_rud_top, 
    			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_mid1 );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_mid1, 
			        mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_bottom );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_bottom, 
			        mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_rud_a, mfd_gc.fctl_y_rud_mid2 );
    	// left elevator
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_rud_b, mfd_gc.fctl_y_rud_top, 
    			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_mid1 );
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_mid1, 
			        mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_bottom );
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_rud_c, mfd_gc.fctl_y_rud_bottom, 
			        mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_rud_a, mfd_gc.fctl_y_rud_mid2 );
    	
    	// Rudder arc
    	g2.drawArc(mfd_gc.fctl_mid_x - mfd_gc.fctl_r_rud_arc, mfd_gc.fctl_y_rud_arc_top - mfd_gc.fctl_r_rud_arc,
    			mfd_gc.fctl_r_rud_arc*2, mfd_gc.fctl_r_rud_arc*2,
    			-45, -90);
    	
        // Rudder arc ends
    	g2.rotate(Math.toRadians(45), mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_top);
    	g2.drawRect(mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_top + mfd_gc.fctl_r_rud_arc, mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end );
    	g2.rotate(Math.toRadians(-90), mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_top);
    	g2.drawRect(mfd_gc.fctl_mid_x-mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_y_rud_arc_top + mfd_gc.fctl_r_rud_arc, mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end );
    	g2.setTransform(original_at);
    	
    	// Trim (blue target)
    	g2.setColor(mfd_gc.ecam_action_color);
    	g2.rotate(Math.toRadians(yaw), mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_top);
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dy_ail_end/2, mfd_gc.fctl_y_rud_arc_top + mfd_gc.fctl_r_rud_arc, mfd_gc.fctl_dy_ail_end, mfd_gc.fctl_dx_ail_end+2 );
    	g2.setTransform(original_at);
    	
    	// Rudder position
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.rotate(Math.toRadians(rudder), mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_top);
    	g2.drawArc(mfd_gc.fctl_mid_x - mfd_gc.fctl_r_rud_bullet/2 , mfd_gc.fctl_y_rud_bullet - mfd_gc.fctl_r_rud_bullet/2 , mfd_gc.fctl_r_rud_bullet, mfd_gc.fctl_r_rud_bullet, 0, 180);
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_r_rud_bullet/2, mfd_gc.fctl_y_rud_bullet, mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_end );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_r_rud_bullet/2, mfd_gc.fctl_y_rud_bullet, mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_end );
    	g2.setTransform(original_at);
    	

    }
  
    private void draw_airbus_pitch_trim(Graphics2D g2) {
    	int pitch = Math.round( this.aircraft.get_pitch_trim() * 100.0f );
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_xl);
    	String pitch_str = "PITCH TRIM  ";
    	g2.drawString(pitch_str, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box - mfd_gc.get_text_width(g2, mfd_gc.font_xl, pitch_str), mfd_gc.fctl_y_pitch_box_top + mfd_gc.line_height_l);
    	
    	// hydrolic box
       	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box, mfd_gc.fctl_y_pitch_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);
    	String hyd_str="GY";
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawString(hyd_str, 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box + mfd_gc.fctl_dx_box_width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, hyd_str)/2 ,
    			  mfd_gc.fctl_y_pitch_box_top + mfd_gc.line_height_l);

    	// Pitch Value
    	g2.setColor(mfd_gc.ecam_normal_color);
    	String pitch_val_str = one_decimal_format.format(Math.abs(pitch/10.0f));
        g2.drawString(pitch_val_str, mfd_gc.fctl_mid_x - mfd_gc.get_text_width(g2, mfd_gc.font_l, pitch_val_str), mfd_gc.fctl_y_pitch_txt);
        g2.setColor(mfd_gc.ecam_action_color);        
        g2.drawString("°", mfd_gc.fctl_mid_x, mfd_gc.fctl_y_pitch_txt);
        g2.setColor(mfd_gc.ecam_normal_color);
        if ( pitch > 0 ) g2.drawString(" UP", mfd_gc.fctl_mid_x + mfd_gc.digit_width_l, mfd_gc.fctl_y_pitch_txt);
        if ( pitch < 0 ) g2.drawString(" DN", mfd_gc.fctl_mid_x + mfd_gc.digit_width_l, mfd_gc.fctl_y_pitch_txt);
  
    }
    
    private void draw_airbus_roll_trim(Graphics2D g2) {
    	// For Airbus style - but not for Airbus aircrafts 
    	// Airbus does not have roll pitch
    	int roll = Math.round( this.aircraft.get_roll_trim() * 100.0f );
    	int rtrim_x = mfd_gc.fctl_mid_x - mfd_gc.lat_trim_w/2;
    	int rtrim_y = mfd_gc.fctl_y_ail_box_top - mfd_gc.line_height_l;
    	AffineTransform original_at = g2.getTransform();
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_xl);
    	String pitch_str = "ROLL TRIM";
    	g2.drawString(pitch_str, mfd_gc.fctl_mid_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, pitch_str)/2, mfd_gc.fctl_y_ail_box_top - mfd_gc.line_height_l*2);
    	
        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawArc(rtrim_x - 1, rtrim_y - 1, mfd_gc.lat_trim_w + 2, mfd_gc.lat_trim_h + 2, 45, 90);
        g2.drawLine(mfd_gc.fctl_mid_x, rtrim_y, mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h*8/100);
        g2.rotate(Math.toRadians(45), mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.fctl_mid_x, rtrim_y, mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h*8/100);
        g2.rotate(Math.toRadians(-90), mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.fctl_mid_x, rtrim_y, mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h*8/100);
        g2.setTransform(original_at);
        g2.rotate(Math.toRadians(23), mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.fctl_mid_x, rtrim_y, mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h*4/100);
        g2.rotate(Math.toRadians(-46), mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.fctl_mid_x, rtrim_y, mfd_gc.fctl_mid_x, rtrim_y + mfd_gc.lat_trim_h*4/100);
        g2.setTransform(original_at);

        int[] roll_triangle_x = {
            mfd_gc.fctl_mid_x,
            mfd_gc.fctl_mid_x - mfd_gc.lat_trim_w*4/100,
            mfd_gc.fctl_mid_x + mfd_gc.lat_trim_w*4/100
        };
        int[] roll_triangle_y = {
        		mfd_gc.fctl_y_ail_box_top,
        		mfd_gc.fctl_y_ail_box_top + mfd_gc.lat_trim_w*8/100,
        		mfd_gc.fctl_y_ail_box_top + mfd_gc.lat_trim_w*8/100
        };
        g2.rotate(Math.toRadians(roll*45/100), mfd_gc.fctl_mid_x, mfd_gc.fctl_y_ail_box_top + mfd_gc.lat_trim_h/2);
        g2.setColor(mfd_gc.normal_color);
        g2.drawPolygon(roll_triangle_x, roll_triangle_y, 3);
        g2.setTransform(original_at);

    }    
    
    private void draw_trim(Graphics2D g2) {
        
        int pitch = Math.round( this.aircraft.get_pitch_trim() * 100.0f );
        int roll = Math.round( this.aircraft.get_roll_trim() * 100.0f );
        int yaw = Math.round( this.aircraft.get_yaw_trim() * 100.0f );
        
        AffineTransform original_at = g2.getTransform();
        
        g2.setColor(mfd_gc.color_boeingcyan);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("T", mfd_gc.trim_txt_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "T")/2, mfd_gc.trim_txt_y + mfd_gc.line_height_s*0);
        g2.drawString("R", mfd_gc.trim_txt_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "R")/2, mfd_gc.trim_txt_y + mfd_gc.line_height_s*1);
        g2.drawString("I", mfd_gc.trim_txt_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "I")/2, mfd_gc.trim_txt_y + mfd_gc.line_height_s*2);
        g2.drawString("M", mfd_gc.trim_txt_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "M")/2, mfd_gc.trim_txt_y + mfd_gc.line_height_s*3);

        //g2.setColor(mfd_gc.color_boeingcyan);
        //g2.setFont(mfd_gc.font_s);
        g2.drawString("ROLL", mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_s, "ROLL")/2, mfd_gc.lat_trim_y - mfd_gc.line_height_s);

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawArc(mfd_gc.lat_trim_x - 1, mfd_gc.lat_trim_y - 1, mfd_gc.lat_trim_w + 2, mfd_gc.lat_trim_h + 2, 45, 90);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y, mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h*8/100);
        g2.rotate(Math.toRadians(45), mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y, mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h*8/100);
        g2.rotate(Math.toRadians(-90), mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y, mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h*8/100);
        g2.setTransform(original_at);
        g2.rotate(Math.toRadians(23), mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y, mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h*4/100);
        g2.rotate(Math.toRadians(-46), mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h/2);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y, mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h*4/100);
        g2.setTransform(original_at);

        int[] roll_triangle_x = {
            mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2,
            mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.lat_trim_w*4/100,
            mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*4/100
        };
        int[] roll_triangle_y = {
            mfd_gc.lat_trim_y,
            mfd_gc.lat_trim_y + mfd_gc.lat_trim_w*8/100,
            mfd_gc.lat_trim_y + mfd_gc.lat_trim_w*8/100
        };
        g2.rotate(Math.toRadians(roll*45/100), mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2, mfd_gc.lat_trim_y + mfd_gc.lat_trim_h/2);
        g2.setColor(mfd_gc.normal_color);
        g2.drawPolygon(roll_triangle_x, roll_triangle_y, 3);
        g2.setTransform(original_at);
        
        
        g2.setColor(mfd_gc.color_boeingcyan);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("YAW", mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_s, "YAW")/2, mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*8/100 - mfd_gc.line_height_s);

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.lat_trim_w*30/100, mfd_gc.yaw_trim_y + 1, mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*30/100, mfd_gc.yaw_trim_y + 1);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2,                              mfd_gc.yaw_trim_y,     mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2,                              mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*8/100);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.lat_trim_w*30/100, mfd_gc.yaw_trim_y,     mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.lat_trim_w*30/100, mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*8/100);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*30/100, mfd_gc.yaw_trim_y,     mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*30/100, mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*8/100);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.lat_trim_w*15/100, mfd_gc.yaw_trim_y,     mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 - mfd_gc.lat_trim_w*15/100, mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*4/100);
        g2.drawLine(mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*15/100, mfd_gc.yaw_trim_y,     mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*15/100, mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*4/100);

        int[] yaw_triangle_x = {
            mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*yaw*30/100/100,
            mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*yaw*30/100/100 - mfd_gc.lat_trim_w*4/100,
            mfd_gc.lat_trim_x + mfd_gc.lat_trim_w/2 + mfd_gc.lat_trim_w*yaw*30/100/100 + mfd_gc.lat_trim_w*4/100
        };
        int[] yaw_triangle_y = {
            mfd_gc.yaw_trim_y,
            mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*8/100,
            mfd_gc.yaw_trim_y - mfd_gc.lat_trim_h*8/100
        };
        g2.setColor(mfd_gc.normal_color);
        g2.drawPolygon(yaw_triangle_x, yaw_triangle_y, 3);

        
        g2.setColor(mfd_gc.color_boeingcyan);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("PITCH", mfd_gc.pitch_trim_x, mfd_gc.pitch_trim_y - mfd_gc.line_height_s);

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(mfd_gc.pitch_trim_x - 1, mfd_gc.pitch_trim_y,                                                     mfd_gc.pitch_trim_x - 1,                           mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h);
        g2.drawLine(mfd_gc.pitch_trim_x    , mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2,                           mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*8/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2);
        g2.drawLine(mfd_gc.pitch_trim_x    , mfd_gc.pitch_trim_y,                                                     mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*8/100, mfd_gc.pitch_trim_y);
        g2.drawLine(mfd_gc.pitch_trim_x    , mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h,                             mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*8/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h);
        g2.drawLine(mfd_gc.pitch_trim_x    , mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 - mfd_gc.pitch_trim_h/4, mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*4/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 - mfd_gc.pitch_trim_h/4);
        g2.drawLine(mfd_gc.pitch_trim_x    , mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.pitch_trim_h/4, mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*4/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.pitch_trim_h/4);
        
        int[] pitch_triangle_x = {
            mfd_gc.pitch_trim_x,
            mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*8/100,
            mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*8/100
        };
        int[] pitch_triangle_y = {
            mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.pitch_trim_h*pitch/200,
            mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.pitch_trim_h*pitch/200 - mfd_gc.pitch_trim_w*4/100,
            mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.pitch_trim_h*pitch/200 + mfd_gc.pitch_trim_w*4/100
        };
        g2.setColor(mfd_gc.normal_color);
        g2.drawPolygon(pitch_triangle_x, pitch_triangle_y, 3);

        g2.setColor(mfd_gc.normal_color);
        g2.drawString(one_decimal_format.format(Math.abs(pitch/10.0f)), mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*24/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.line_height_s*3/8);
        if ( pitch > 0 ) g2.drawString("UP", mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*24/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.line_height_s*3/8 + mfd_gc.line_height_s);
        if ( pitch < 0 ) g2.drawString("DN", mfd_gc.pitch_trim_x + mfd_gc.pitch_trim_w*24/100, mfd_gc.pitch_trim_y + mfd_gc.pitch_trim_h/2 + mfd_gc.line_height_s*3/8 - mfd_gc.line_height_s);
        
    }
    
    
    private void draw_flaps_speedbrake(Graphics2D g2) {
        
        float flaps = this.aircraft.get_flap_position();
        float flapshandle = this.aircraft.get_flap_handle();
        int detents = this.aircraft.get_flap_detents();
        float speedbrake = this.aircraft.get_speed_brake();
        boolean sbrk_armed = this.aircraft.speed_brake_armed();
        boolean sbrk_eq = this.aircraft.has_speed_brake();
        float slats = this.aircraft.get_slat_position();

        AffineTransform original_at = g2.getTransform();
        
        // wing
        g2.setColor(mfd_gc.dim_markings_color);
        g2.fillOval(mfd_gc.wing_x - mfd_gc.wing_h/2, mfd_gc.wing_y - mfd_gc.wing_h/2, mfd_gc.wing_h, mfd_gc.wing_h);
        int[] wing_section_x = {
            mfd_gc.wing_x,
            mfd_gc.wing_x + mfd_gc.wing_w/8,
            mfd_gc.wing_x + mfd_gc.wing_w/3,
            mfd_gc.wing_x + mfd_gc.wing_w - mfd_gc.spdbrk_w,
            mfd_gc.wing_x + mfd_gc.wing_w,
            mfd_gc.wing_x + mfd_gc.wing_w,
            mfd_gc.wing_x
        };
        int[] wing_section_y = {
            mfd_gc.wing_y - mfd_gc.wing_h/2,
            mfd_gc.wing_y - mfd_gc.wing_h*7/8,
            mfd_gc.wing_y - mfd_gc.wing_h,
            mfd_gc.wing_y - mfd_gc.wing_h,
            mfd_gc.wing_y - mfd_gc.wing_h/2,
            mfd_gc.wing_y + mfd_gc.wing_h/2,
            mfd_gc.wing_y + mfd_gc.wing_h/2
        };
        g2.fillPolygon(wing_section_x, wing_section_y, 7);

        
        if ( flaps > 0.0f ) {
            // flaps arc
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawArc(mfd_gc.wing_x + mfd_gc.wing_w - mfd_gc.flaps_l - 1, mfd_gc.wing_y - mfd_gc.flaps_l - 1, mfd_gc.flaps_l*2 + 2, mfd_gc.flaps_l*2 + 2, 0-5, -60);
            g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l, mfd_gc.wing_y + mfd_gc.wing_h/2, mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l - mfd_gc.controls_w/2*8/100, mfd_gc.wing_y + mfd_gc.wing_h/2);
            if ( detents >= 2 ) {
                double rotang = Math.toRadians(60.0d / detents);
                for ( int i=0; i!=detents; i++) {
                    g2.rotate(rotang, mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.wing_y);
                    g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l, mfd_gc.wing_y + mfd_gc.wing_h/2, mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l - mfd_gc.controls_w/2*4/100, mfd_gc.wing_y + mfd_gc.wing_h/2);
                }
                g2.setTransform(original_at);
            }
            g2.rotate(Math.toRadians(60), mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.wing_y);
            g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l, mfd_gc.wing_y + mfd_gc.wing_h/2, mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l - mfd_gc.controls_w/2*8/100, mfd_gc.wing_y + mfd_gc.wing_h/2);
            g2.setTransform(original_at);
        }
        
        // flaps handle
        g2.setColor(mfd_gc.dim_markings_color);
        g2.rotate(Math.toRadians(60*flapshandle), mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.wing_y);
        g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w , mfd_gc.wing_y + mfd_gc.wing_h/2 - 1, mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l, mfd_gc.wing_y + mfd_gc.wing_h/2);
        g2.setTransform(original_at);
        
        // flaps
        int[] flaps_triangle_x = {
            mfd_gc.wing_x + mfd_gc.wing_w,
            mfd_gc.wing_x + mfd_gc.wing_w,
            mfd_gc.wing_x + mfd_gc.wing_w + mfd_gc.flaps_l
        };
        int[] flaps_triangle_y = {
            mfd_gc.wing_y + mfd_gc.wing_h/2,
            mfd_gc.wing_y - mfd_gc.wing_h/2,
            mfd_gc.wing_y + mfd_gc.wing_h/2
        };
        g2.setColor(mfd_gc.normal_color);
        g2.fillOval(mfd_gc.wing_x + mfd_gc.wing_w - mfd_gc.wing_h/2, mfd_gc.wing_y - mfd_gc.wing_h/2, mfd_gc.wing_h, mfd_gc.wing_h);
        g2.rotate(Math.toRadians(60*flaps), mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.wing_y);
        g2.fillPolygon(flaps_triangle_x, flaps_triangle_y, 3);
        g2.setTransform(original_at);
        
        g2.setColor(mfd_gc.color_boeingcyan);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("FLAPS", mfd_gc.wing_x, mfd_gc.wing_y + mfd_gc.line_height_s*10/4);

        if ( slats > 0.0f ) {
            if ( slats == 1.0f ) g2.setColor(mfd_gc.normal_color);
            else g2.setColor(mfd_gc.caution_color);
            g2.setFont(mfd_gc.font_xxs);
            g2.drawString("S", mfd_gc.wing_x - mfd_gc.max_char_advance_xxs, mfd_gc.wing_y + mfd_gc.line_height_xxs);
        }
        
        if ( sbrk_eq ) {
            
            if ( speedbrake > 0.01f ) {
                // speedbrake arc
                g2.setColor(mfd_gc.dim_markings_color);
                g2.drawArc(mfd_gc.spdbrk_x - mfd_gc.spdbrk_w - 1, mfd_gc.spdbrk_y - mfd_gc.spdbrk_w - 1, mfd_gc.spdbrk_w*2 + 2, mfd_gc.spdbrk_w*2 + 2, 0, 90);
                g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.spdbrk_y, mfd_gc.wing_x + mfd_gc.wing_w - mfd_gc.controls_w/2*6/100, mfd_gc.spdbrk_y);
                g2.rotate(Math.toRadians(-30), mfd_gc.spdbrk_x, mfd_gc.spdbrk_y);
                g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.spdbrk_y, mfd_gc.wing_x + mfd_gc.wing_w - mfd_gc.controls_w/2*6/100, mfd_gc.spdbrk_y);
                g2.rotate(Math.toRadians(-30), mfd_gc.spdbrk_x, mfd_gc.spdbrk_y);
                g2.drawLine(mfd_gc.wing_x + mfd_gc.wing_w, mfd_gc.spdbrk_y, mfd_gc.wing_x + mfd_gc.wing_w - mfd_gc.controls_w/2*6/100, mfd_gc.spdbrk_y);
                g2.setTransform(original_at);
            }
        
            //speedbrake
            int[] spdbrk_triangle_x = {
                mfd_gc.spdbrk_x,
                mfd_gc.spdbrk_x,
                mfd_gc.spdbrk_x + mfd_gc.spdbrk_w
            };
            int[] spdbrk_triangle_y = {
                mfd_gc.spdbrk_y + mfd_gc.spdbrk_h/2,
                mfd_gc.spdbrk_y - mfd_gc.spdbrk_h/2,
                mfd_gc.spdbrk_y
            };
            if ( speedbrake > 0.51f ) {
                g2.setColor(mfd_gc.caution_color);
            } else if ( ( ( ! this.avionics.is_cl30() ) && ( speedbrake > 0.01f ) ) || ( ( this.avionics.is_cl30() ) && ( speedbrake > 0.05f ) ) ) {
                g2.setColor(mfd_gc.unusual_color);
            } else if ( sbrk_armed ) {
                g2.setColor(mfd_gc.normal_color);
            } else {
                g2.setColor(mfd_gc.markings_color);
            }
            g2.rotate(Math.toRadians(-60*speedbrake), mfd_gc.spdbrk_x, mfd_gc.spdbrk_y);
            g2.fillOval(mfd_gc.spdbrk_x - mfd_gc.spdbrk_h/2, mfd_gc.spdbrk_y - mfd_gc.spdbrk_h/2, mfd_gc.spdbrk_h, mfd_gc.spdbrk_h);
            g2.fillPolygon(spdbrk_triangle_x, spdbrk_triangle_y, 3);
            g2.setTransform(original_at);

            g2.setColor(mfd_gc.color_boeingcyan);
            g2.setFont(mfd_gc.font_s);
            g2.drawString("SPEEDBRK", mfd_gc.wing_x, mfd_gc.wing_y - mfd_gc.line_height_s*12/4);
        
        }
        
    }

    
    private void draw_1_gear(Graphics2D g2, int pos, String g_char, int g_x, int g_y) {

        g2.setFont(mfd_gc.font_s);

        float g_ext = this.aircraft.get_gear( pos );
        int circle_y = g_y - mfd_gc.line_height_s*3/8;
        int circle_r = mfd_gc.max_char_advance_s*4/6;

        if ( g_ext > 0.0f ) {
            if ( g_ext == 1.0f ) {
                g2.setColor(mfd_gc.normal_color);
            } else {
                g2.setColor(mfd_gc.caution_color);
            }
            g2.fillOval(g_x - circle_r, circle_y - circle_r, circle_r*2, circle_r*2);
        }
        if ( g_ext == 0.0f ) g2.setColor(mfd_gc.dim_markings_color);
        else g2.setColor(mfd_gc.background_color);
        g2.drawString(g_char, g_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, g_char)/2, g_y);

    }

    private void draw_gears(Graphics2D g2) {
        
        if ( this.aircraft.has_retractable_gear() ) {
            
            g2.setFont(mfd_gc.font_s);
            g2.setColor(mfd_gc.color_boeingcyan);
            g2.drawString("GEAR", mfd_gc.gear_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "GEAR")/2, mfd_gc.gear_y);

            draw_1_gear(g2, 0, "N", mfd_gc.gear_x, mfd_gc.gear_y + mfd_gc.line_height_s*13/8);

            draw_1_gear(g2, 1, "L", mfd_gc.gear_x - mfd_gc.gear_w, mfd_gc.gear_y + mfd_gc.line_height_s*13/8 + mfd_gc.line_height_s*11/8);

            draw_1_gear(g2, 2, "R", mfd_gc.gear_x + mfd_gc.gear_w, mfd_gc.gear_y + mfd_gc.line_height_s*13/8 + mfd_gc.line_height_s*11/8);
        
        }
        
    }

    
    private void draw_parkbrake(Graphics2D g2) {
        
        Color park_color = null;
        
        float parking_brake = this.aircraft.get_parking_brake();
        if ( ! this .aircraft.on_ground() && ( parking_brake > 0.01f ) &&  ! this.aircraft.gear_is_up() ) {
            park_color = mfd_gc.warning_color;
        } else if ( ( parking_brake > 0.51f ) && ! this.aircraft.gear_is_up() ) {
            park_color = mfd_gc.caution_color;
        } else if ( ( parking_brake > 0.01f ) && ! this.aircraft.gear_is_up() ) {
            park_color = mfd_gc.unusual_color;
        }

        if ( park_color != null ) {
            g2.setFont(mfd_gc.font_s);
            g2.setColor(park_color);
            if ( ! this.aircraft.has_retractable_gear() ) {
                g2.drawString("PARKBRK", mfd_gc.gear_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "PARKBRK")/2, mfd_gc.gear_y + mfd_gc.line_height_s);
            } else {
                g2.drawString("P", mfd_gc.gear_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "P")/2, mfd_gc.gear_y + mfd_gc.line_height_s*26/8);
            }
        }
    }

    
    private void draw_autobrake(Graphics2D g2) {

        int autobrake = this.aircraft.auto_brake();
        boolean on_ground = this.aircraft.on_ground();

        String autbrk_str;
        switch (autobrake) {
            
            case -1 :
                autbrk_str = "RTO";
                break;
            case 1 :
                autbrk_str = "1";
                break;
            case 2 :
                autbrk_str = "2";
                break;
            case 3 :
                autbrk_str = "3";
                break;
            case 4 :
                autbrk_str = "MAX";
                break;
            default :
                autbrk_str = "OFF";
                break;

        }

        g2.setFont(mfd_gc.font_s);
        g2.setColor(mfd_gc.color_boeingcyan);
        g2.drawString("AUTOBRK", mfd_gc.autbrk_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, "AUTOBRK")/2, mfd_gc.autbrk_y);
 
        if ( ( ! on_ground && ( autobrake == -1 ) ) || ( on_ground && ( autobrake > 0 ) ) ) {
            // RTO in the air or 1,2,3,max on the ground : caution
            g2.setColor(mfd_gc.caution_color);
        } else if ( ( on_ground && ( autobrake == -1 ) ) || ( ! on_ground && ( autobrake > 0 ) ) ) {
            // RTO on the ground or 1,2,3,max in the air : armed
            g2.setColor(mfd_gc.normal_color);
        } else {
            g2.setColor(mfd_gc.dim_markings_color);
        }
        g2.drawString(autbrk_str, mfd_gc.autbrk_x - mfd_gc.get_text_width(g2, mfd_gc.font_s, autbrk_str)/2, mfd_gc.autbrk_y + mfd_gc.line_height_s*3/2);

    }
    
    
}
