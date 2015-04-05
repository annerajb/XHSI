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
            g2.setColor(mfd_gc.instrument_background_color);
            g2.drawRect(mfd_gc.controls_x, mfd_gc.controls_y, mfd_gc.controls_w, mfd_gc.controls_h);
            g2.drawLine(mfd_gc.controls_x, mfd_gc.controls_y + mfd_gc.controls_h/2, mfd_gc.controls_x + mfd_gc.controls_w, mfd_gc.controls_y + mfd_gc.controls_h/2);
            g2.drawLine(mfd_gc.controls_x + mfd_gc.controls_w*60/100, mfd_gc.controls_y, mfd_gc.controls_x + mfd_gc.controls_w*60/100, mfd_gc.controls_y + mfd_gc.controls_h/2);
            g2.drawLine(mfd_gc.controls_x + mfd_gc.controls_w*60/100, mfd_gc.controls_y + mfd_gc.controls_h*30/100, mfd_gc.controls_x + mfd_gc.controls_w, mfd_gc.controls_y + mfd_gc.controls_h*30/100);
			
            draw_trim(g2);
            draw_flaps_speedbrake(g2);
            draw_gears(g2);
            draw_parkbrake(g2);
            draw_autobrake(g2);
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
