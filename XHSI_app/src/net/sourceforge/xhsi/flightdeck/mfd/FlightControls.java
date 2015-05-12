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

import net.sourceforge.xhsi.model.Aircraft.SpoilerStatus;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;

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
			if (this.avionics.is_qpac() || this.avionics.is_jar_a320neo() ) {
				draw_airbus_fcc(g2);
			} else {
				draw_airbus_roll_trim(g2);
			}
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

    private void draw_airbus_fcc(Graphics2D g2) {
    	// FCC = Flight Controls Computers
    	// On Airbus A330, there are 5 FCC : 3 primaries and 2 secondaries
    	// On Airbus A320, there are 5 FCC : 2 ELAC and 3 secondaries

        // Primaries
        draw_airbus_single_fcc(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_fcc_left, mfd_gc.fctl_y_fcc_bottom , 1, avionics.qpac_fcc(0), "ELAC");
        draw_airbus_single_fcc(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_fcc_left+mfd_gc.fctl_dx_fcc_step , mfd_gc.fctl_y_fcc_bottom + mfd_gc.fctl_dy_fcc_step, 2, avionics.qpac_fcc(1), "");
        // draw_airbus_single_fcc(g2, mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_fcc_left+mfd_gc.fctl_dx_fcc_step*2 , mfd_gc.fctl_y_fcc_bottom + mfd_gc.fctl_dy_fcc_step*2, 3, avionics.qpac_fcc(2), "");
        // Secondaries
        draw_airbus_single_fcc(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_fcc_right, mfd_gc.fctl_y_fcc_bottom , 1, avionics.qpac_fcc(2), "  SEC");
        draw_airbus_single_fcc(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_fcc_right + mfd_gc.fctl_dx_fcc_step , mfd_gc.fctl_y_fcc_bottom + mfd_gc.fctl_dy_fcc_step, 2, avionics.qpac_fcc(3), "");
        draw_airbus_single_fcc(g2, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_fcc_right + mfd_gc.fctl_dx_fcc_step*2 , mfd_gc.fctl_y_fcc_bottom + mfd_gc.fctl_dy_fcc_step*2, 3, avionics.qpac_fcc(4), "");
    }

    private void draw_airbus_single_fcc(Graphics2D g2, int x, int y, int num, boolean status, String legend) {
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xl);
        if (! legend.isEmpty()) {
        	g2.drawString(legend, x - mfd_gc.fctl_dx_fcc_bottom, y - mfd_gc.line_height_xl/4);
        }
        if (! status) { g2.setColor(mfd_gc.ecam_caution_color); }
        g2.drawLine(x - mfd_gc.fctl_dx_fcc_bottom, y, x, y);
        g2.drawLine(x - mfd_gc.fctl_dx_fcc_top,y - mfd_gc.fctl_dy_fcc_right , x, y - mfd_gc.fctl_dy_fcc_right);
        g2.drawLine(x, y, x, y- mfd_gc.fctl_dy_fcc_right);        
        if (status) { 
        	g2.setColor(mfd_gc.ecam_normal_color); 
        } else {
        	g2.setColor(mfd_gc.ecam_caution_color); 
        }
        String num_str = ""+num;
        g2.drawString(num_str, x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, num_str)*5/4, y - mfd_gc.line_height_xl/4);
    }

    
    private void draw_airbus_speedbrake(Graphics2D g2) {
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
    	boolean hyd_b = this.aircraft.get_hyd_press(2) > 0.4f;
    	Color col_g = hyd_g ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_y = hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_b = hyd_b ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	
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
    	int hyd_str_x = mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_box * 2/3 -  mfd_gc.digit_width_l/2;
    	int hyd_str_y = mfd_gc.fctl_y_wing_box + mfd_gc.line_height_l*11/12;
    	int hyd_str_step = mfd_gc.fctl_dx_wing_box * 2/3;
    	g2.setColor(col_g);
    	g2.drawString("G", hyd_str_x, hyd_str_y);
    	g2.setColor(col_b);
    	g2.drawString("B", hyd_str_x + hyd_str_step, hyd_str_y);
    	g2.setColor(col_y);
    	g2.drawString("Y", hyd_str_x + hyd_str_step*2, hyd_str_y);
    	
    	// indicators (not for airbus)
    	/*
    	float speedbrake = this.aircraft.get_speed_brake();
    	int spd_dy = Math.round(speedbrake * mfd_gc.mfd_size * 40/1000);
    	g2.setColor(mfd_gc.ecam_normal_color);
    	g2.drawLine(mfd_gc.fctl_mid_x, mfd_gc.fctl_y_spoiler_top, 
    				 mfd_gc.fctl_mid_x, mfd_gc.fctl_y_spoiler_top - spd_dy);
    	*/
    	
    	
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
    
    private void draw_airbus_aileron(Graphics2D g2) {
    	int aileron_range = Math.round(this.aircraft.get_aileron_max_up() + this.aircraft.get_aileron_max_down());
    	int aileron_mark = mfd_gc.fctl_y_ail_top + (mfd_gc.fctl_y_ail_bottom-mfd_gc.fctl_y_ail_top)*Math.round(this.aircraft.get_aileron_max_up())/aileron_range;
    	int left_aileron = aileron_mark + Math.round(this.aircraft.get_left_aileron_pos()*(mfd_gc.fctl_y_ail_bottom-mfd_gc.fctl_y_ail_top)/aileron_range);
    	int right_aileron = aileron_mark + Math.round(this.aircraft.get_right_aileron_pos()*(mfd_gc.fctl_y_ail_bottom-mfd_gc.fctl_y_ail_top)/aileron_range);
    	int hyd_text_y = mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l*11/12;
    	
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
    	boolean hyd_b = this.aircraft.get_hyd_press(2) > 0.4f;
    	boolean elac1 = avionics.is_qpac() ? avionics.qpac_fcc(0) : true;
    	boolean elac2 = avionics.is_qpac() ? avionics.qpac_fcc(1) : true;
    	
    	Color col_g = hyd_g ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_y = hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_b = hyd_b ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_ail_l = ((hyd_b && elac1) || (hyd_g && elac2)) ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_ail_r = ((hyd_b && elac2) || (hyd_g && elac1)) ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	
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
    	if (elac1 && elac2) {
    		g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	} else if (elac1) {
    		// only right side box
        	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} else if (elac2) {   		
    		// only left side box
        	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} 
    	if (!elac1) {
        	// draw amber mark on left side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top,
    			        mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);
    	}
    	if (!elac2) {
        	// draw amber mark on right side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1, mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1, mfd_gc.fctl_y_ail_box_top,
			            mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1, mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);   		
    	}
    	
    	g2.setFont(mfd_gc.font_l);    	
    	g2.setColor(col_b); 
    	g2.drawString("B", 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width*3/4 - mfd_gc.digit_width_l/2 ,
    			  hyd_text_y);
    	g2.setColor(col_g);
    	g2.drawString("G", 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box1 - mfd_gc.fctl_dx_box_width/4 - mfd_gc.digit_width_l/2,
    			  hyd_text_y);

    	// LEFT 2
    	/* 
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box2 - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);    	
    	g2.setColor(col_b);
    	g2.drawString("B", 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box2 - mfd_gc.fctl_dx_box_width/2  - mfd_gc.digit_width_l ,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);
    	g2.setColor(col_y);
    	g2.drawString("Y", 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_ail_box2 - mfd_gc.fctl_dx_box_width/2,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);
    	*/
    	

    	// RIGHT 1
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	if (elac1 && elac2) {
        	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1  , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	} else if (elac1) {
    		// only right side box
        	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1, mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} else if (elac2) {   		
    		// only left side box
        	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} 

    	if (!elac1) {
        	// draw amber mark on left side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1, mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top,
    			        mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);
    	}
    	if (!elac2) {
        	// draw amber mark on right side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width, mfd_gc.fctl_y_ail_box_top,
			            mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width, mfd_gc.fctl_y_ail_box_top+ mfd_gc.fctl_box_height);   		
    	}
    	
    	g2.setFont(mfd_gc.font_l);
    	g2.setColor(col_g);
    	g2.drawString("G", 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width/4 - mfd_gc.digit_width_l/2,
    			  hyd_text_y);
    	g2.setColor(col_b);
    	g2.drawString("B", 
  			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box1 + mfd_gc.fctl_dx_box_width*3/4 - mfd_gc.digit_width_l/2,
  			  hyd_text_y);


    	// RIGHT 2
    	/*
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box2  , mfd_gc.fctl_y_ail_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);    	    	
    	g2.setColor(col_b);
    	g2.drawString("B", 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box2 + mfd_gc.fctl_dx_box_width/2 - mfd_gc.digit_width_l ,
    			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);
    	g2.setColor(col_y);
    	g2.drawString("Y", 
  			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_ail_box2 + mfd_gc.fctl_dx_box_width/2,
  			  mfd_gc.fctl_y_ail_box_top + mfd_gc.line_height_l);
    	*/

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
    	g2.setColor(col_ail_r);
    	g2.drawPolygon(r_ail_tri_x, r_ail_tri_y, 3);
    	g2.setColor(col_ail_l);
    	g2.drawPolygon(l_ail_tri_x, l_ail_tri_y, 3);
    	
    	
    }

   
    private void draw_airbus_elevator(Graphics2D g2) {
    	int elevator_range = Math.round(this.aircraft.get_elev_max_up() + this.aircraft.get_elev_max_down());
    	int elevator_mark = mfd_gc.fctl_y_elev_top + (mfd_gc.fctl_y_elev_bottom-mfd_gc.fctl_y_elev_top)*Math.round(this.aircraft.get_elev_max_up())/elevator_range;
    	int left_elevator = elevator_mark + Math.round(this.aircraft.get_left_elev_pos()*(mfd_gc.fctl_y_elev_bottom-mfd_gc.fctl_y_elev_top)/elevator_range);
    	int right_elevator = elevator_mark + Math.round(this.aircraft.get_right_elev_pos()*(mfd_gc.fctl_y_elev_bottom-mfd_gc.fctl_y_elev_top)/elevator_range);
    	int hyd_text_y = mfd_gc.fctl_y_elev_box_top + mfd_gc.line_height_l*11/12;
    	
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
    	boolean hyd_b = this.aircraft.get_hyd_press(2) > 0.4f;
    	boolean elac1 = avionics.is_qpac() ? avionics.qpac_fcc(0) : true;
    	boolean elac2 = avionics.is_qpac() ? avionics.qpac_fcc(1) : true;
    	boolean sec1 = avionics.is_qpac() ? avionics.qpac_fcc(2) : true;
    	boolean sec2 = avionics.is_qpac() ? avionics.qpac_fcc(3) : true;
    	boolean fcc_ok = elac1 || elac2 || sec1 || sec2 ;
    	
    	// if hyd_b && elac2 && sec2 failure => left + right elev center "amber" 
    	
    	
    	Color col_g = hyd_g ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_y = hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_b = hyd_b ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_elev_l = ((hyd_g && (sec2 || elac2)) || (hyd_b && (sec1 || elac1))) ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_elev_r = ((hyd_b && (sec1 || elac1)) || (hyd_y && (sec2 || elac2))) ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	
    	if (!hyd_b && !elac2 && !sec2) { 
    		col_elev_l = mfd_gc.ecam_caution_color;
    		col_elev_r = mfd_gc.ecam_caution_color;
    	}
    	
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
    	// Hydrolic box "BG"
    	// Text in green is circuit ok, amber when low pressure (<2000 psi)
    	// LEFT
    	g2.setColor(mfd_gc.color_airbusgray.darker());
    	if ((elac1||sec1) && (elac2||sec2)) {
        	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	} else if (elac1||sec1) {
    		// only right side box
        	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} else if (elac2||sec2) {   		
    		// only left side box
        	g2.fillRect(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} 
    	
    	if (!(elac1 || sec1)) {
        	// draw amber mark on left side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top,
    			        mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);
    	}
    	if (!(elac2 || sec2)) {
        	// draw amber mark on right side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box, mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box, mfd_gc.fctl_y_elev_box_top,
			            mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box, mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);   		
    	}
    	
    	g2.setFont(mfd_gc.font_l);
    	String hyd_str="BG";
    	g2.setColor(col_b);
    	g2.drawString("B", 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width*3/4 - mfd_gc.digit_width_l/2 ,
    			  hyd_text_y);
    	g2.setColor(col_g);
    	g2.drawString("G", 
    			  mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_elev_box - mfd_gc.fctl_dx_box_width/4 - mfd_gc.digit_width_l/2 ,
    			  hyd_text_y);

    	// RIGHT
    	g2.setColor(mfd_gc.color_airbusgray.darker());

    	if ((elac1||sec1) && (elac2||sec2)) {
        	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	} else if (elac2||sec2) {
    		// only right side box
        	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box, mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} else if (elac1||sec1) {   		
    		// only left side box
        	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top,
    			    mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_box_height);
    	} 
    	if (!(elac2 || sec2)) {
        	// draw amber mark on left side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box, mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top,
    			        mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/2 , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);
    	}
    	if (!(elac1 || sec1)) {
        	// draw amber mark on right side
        	g2.setColor(mfd_gc.ecam_caution_color);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width , mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height,
        			    mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/2, mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);
        	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width, mfd_gc.fctl_y_elev_box_top,
			            mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width, mfd_gc.fctl_y_elev_box_top+ mfd_gc.fctl_box_height);   		
    	}
    	
    	g2.setFont(mfd_gc.font_l);
    	hyd_str="YB";
    	g2.setColor(col_y);
    	g2.drawString("Y", 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width/4 - mfd_gc.digit_width_l/2 ,
    			  hyd_text_y);   	
    	g2.setColor(col_b);
    	g2.drawString("B", 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_elev_box + mfd_gc.fctl_dx_box_width*3/4 - mfd_gc.digit_width_l/2,
    			  hyd_text_y);   	
    	
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
    	g2.setColor(col_elev_r);
    	g2.drawPolygon(r_elev_tri_x, r_elev_tri_y, 3);
    	g2.setColor(col_elev_l);
    	g2.drawPolygon(l_elev_tri_x, l_elev_tri_y, 3);
    	
    }
   
    
    private void draw_airbus_rudder(Graphics2D g2) {
    	//int rudder_range = Math.round(this.aircraft.get_rudder_max_lr());
    	int rudder_range = 30;
    	int yaw = Math.round( this.aircraft.get_yaw_trim() * -rudder_range );
    	int rudder = Math.round(this.aircraft.get_rudder_pos()*-45/rudder_range);
    	
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
    	boolean hyd_b = this.aircraft.get_hyd_press(2) > 0.4f;
    	Color col_g = hyd_g ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_y = hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_b = hyd_b ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_rudder = hyd_b || hyd_g || hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	
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
    	int hyd_str_x = mfd_gc.fctl_mid_x - mfd_gc.fctl_dx_wing_box * 2/3 -  mfd_gc.digit_width_l/2;
    	int hyd_str_y = mfd_gc.fctl_y_rud_box_top + mfd_gc.line_height_l*11/12;
    	int hyd_str_step = mfd_gc.fctl_dx_wing_box * 2/3;
    	g2.setColor(col_g);
    	g2.drawString("G", hyd_str_x, hyd_str_y);
    	g2.setColor(col_b);
    	g2.drawString("B", hyd_str_x + hyd_str_step, hyd_str_y);
    	g2.setColor(col_y);
    	g2.drawString("Y", hyd_str_x + hyd_str_step*2, hyd_str_y);
    
    	
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
    	g2.setColor(col_rudder);
    	g2.rotate(Math.toRadians(rudder), mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_top);
    	g2.drawArc(mfd_gc.fctl_mid_x - mfd_gc.fctl_r_rud_bullet/2 , mfd_gc.fctl_y_rud_bullet - mfd_gc.fctl_r_rud_bullet/2 , mfd_gc.fctl_r_rud_bullet, mfd_gc.fctl_r_rud_bullet, 0, 180);
    	g2.drawLine(mfd_gc.fctl_mid_x - mfd_gc.fctl_r_rud_bullet/2, mfd_gc.fctl_y_rud_bullet, mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_end );
    	g2.drawLine(mfd_gc.fctl_mid_x + mfd_gc.fctl_r_rud_bullet/2, mfd_gc.fctl_y_rud_bullet, mfd_gc.fctl_mid_x, mfd_gc.fctl_y_rud_arc_end );
    	g2.setTransform(original_at);
    	

    }
  
    private void draw_airbus_pitch_trim(Graphics2D g2) {
    	int pitch = Math.round( this.aircraft.get_pitch_trim() * 100.0f );
    	int hyd_text_y = mfd_gc.fctl_y_pitch_box_top + mfd_gc.line_height_l * 11/12;
    	
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;    	
    	Color col_g = hyd_g ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	Color col_y = hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;    	
    	Color col_pitch = hyd_g || hyd_y ? mfd_gc.ecam_normal_color : mfd_gc.ecam_caution_color;
    	
    	g2.setColor(mfd_gc.ecam_markings_color);
    	g2.setFont(mfd_gc.font_xl);
    	String pitch_str = "PITCH TRIM  ";
    	g2.drawString(pitch_str, mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box - mfd_gc.get_text_width(g2, mfd_gc.font_xl, pitch_str), mfd_gc.fctl_y_pitch_box_top + mfd_gc.line_height_l);
    	
    	// hydrolic box
       	g2.setColor(mfd_gc.color_airbusgray.darker());
    	g2.fillRect(mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box, mfd_gc.fctl_y_pitch_box_top,
    			    mfd_gc.fctl_dx_box_width, mfd_gc.fctl_box_height);
    	g2.setFont(mfd_gc.font_l);    	
    	g2.setColor(col_g);
    	g2.drawString("G", 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box + mfd_gc.fctl_dx_box_width/4 - mfd_gc.digit_width_l/2 ,
    			  hyd_text_y);
    	g2.setColor(col_y);
    	g2.drawString("Y", 
    			  mfd_gc.fctl_mid_x + mfd_gc.fctl_dx_pitch_box + mfd_gc.fctl_dx_box_width*3/4 - mfd_gc.digit_width_l/2,
    			  hyd_text_y);

    	// Pitch Value
    	g2.setColor(col_pitch);
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
