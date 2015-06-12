/**
* ClockDial.java
* 
* ...
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.cdu;

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;

import java.text.DecimalFormat;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class CDUDial extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private int clock_x;
    private int clock_y;
    private int clock_r;


    public CDUDial(ModelFactory model_factory, CDUGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        clock_x = cdu_gc.clock_square.x + cdu_gc.clock_square.width/2;
        clock_y = cdu_gc.clock_square.y + cdu_gc.clock_square.height/2;
        clock_r = cdu_gc.clock_square.width/2*7/8;
        
        drawDial(g2);
        if ( this.aircraft.battery() ) {
            draw_time(g2);
            draw_timer(g2);
        }
        
    }


    public void draw_time(Graphics2D g2) {

        DecimalFormat hms_formatter = new DecimalFormat("00");

        g2.setColor(cdu_gc.clock_color);

        int current_time = this.avionics.clock_shows_utc() ? (int)this.aircraft.sim_time_zulu() : (int)this.aircraft.sim_time_local();
        int hh = current_time / 3600;
        int mm = ( current_time / 60 ) % 60;
        int ss = current_time % 60;
        String current_time_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm); // + ":" + hms_formatter.format(ss);

        int current_time_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_zl, current_time_str)/2;
        int current_time_y = clock_y + cdu_gc.line_height_zl*38/100 - cdu_gc.line_height_zl*3/4;
        g2.setFont(cdu_gc.font_zl);
        g2.drawString(current_time_str, current_time_x, current_time_y);

        g2.setFont(cdu_gc.font_xxl);
//        utc_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_xxl, "UTC")/2;
        current_time_y -= cdu_gc.line_height_zl*8/10 + cdu_gc.line_height_xxl/2;
        if ( this.avionics.clock_shows_utc() ) {
            current_time_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_xxl, "UTC") - cdu_gc.digit_width_xxl;
            g2.drawString("UTC", current_time_x, current_time_y);
        } else {
            current_time_x = clock_x + cdu_gc.digit_width_xxl;
            g2.drawString("LT", current_time_x, current_time_y);
        }

//// test
//AffineTransform original_at = g2.getTransform();
//g2.rotate(Math.toRadians(ss*6), clock_x, clock_y);
//Stroke original_stroke = g2.getStroke();
//g2.setStroke(new BasicStroke(8.0f * cdu_gc.scaling_factor));
//g2.drawLine(clock_x, clock_y - cdu_gc.clock_square.width*14/64, clock_x, clock_y - cdu_gc.clock_square.width*25/64);
//g2.setStroke(original_stroke);
//g2.setTransform(original_at);

    }

    public void draw_timer(Graphics2D g2) {

        DecimalFormat hms_formatter = new DecimalFormat("00");

        g2.setColor(cdu_gc.clock_color);

        float chr_time = this.aircraft.timer_elapsed_time();
//chr_time = 3599.0f;

        if ( chr_time == 0.0f ) {
            
            int timer = (int)this.aircraft.total_flight_time();
            int hh = ( timer / 3600 ) % 24;
            int mm = ( timer / 60 ) % 60;
            String timer_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm); // + ":" + hms_formatter.format(ss);
            int timer_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_zl, timer_str)/2;
            int timer_y = clock_y + cdu_gc.line_height_zl*38/100 + cdu_gc.line_height_zl*3/4;
            g2.setFont(cdu_gc.font_zl);
            g2.drawString(timer_str, timer_x, timer_y);
            
            //timer_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_xxl, "ET")/2;
            timer_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_xxl, "ET") - cdu_gc.digit_width_xxl;
            timer_y += cdu_gc.line_height_xxl*5/4 + cdu_gc.line_height_zl*1/10;
            g2.setFont(cdu_gc.font_xxl);
            g2.drawString("ET", timer_x, timer_y);
            
        } else {
            
            int timer = (int)chr_time;
            int mins = timer / 60;
            int secs = timer % 60;
            String timer_str = (mins==0?"":mins) + ":" + hms_formatter.format(secs);
//            int timer_x = clock_x + cdu_gc.digit_width_zl*2 - cdu_gc.get_text_width(g2, cdu_gc.font_zl, timer_str);
            int timer_x = clock_x + cdu_gc.get_text_width(g2, cdu_gc.font_zl, "99:99")/2 - cdu_gc.get_text_width(g2, cdu_gc.font_zl, timer_str);
            int timer_y = clock_y + cdu_gc.line_height_zl*38/100 + cdu_gc.line_height_zl*3/4;
            g2.setFont(cdu_gc.font_zl);
            g2.drawString(timer_str, timer_x, timer_y);

            //timer_x = clock_x - cdu_gc.get_text_width(g2, cdu_gc.font_xxl, "CHR")/2;
            timer_x = clock_x + cdu_gc.digit_width_xxl;
            timer_y += cdu_gc.line_height_xxl*5/4 + cdu_gc.line_height_zl*1/10;
            g2.setFont(cdu_gc.font_xxl);
            g2.drawString("CHR", timer_x, timer_y);
            
            AffineTransform original_at = g2.getTransform();
            g2.rotate(Math.toRadians(secs*6), clock_x, clock_y);
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(8.0f * cdu_gc.scaling_factor));
            g2.drawLine(clock_x, clock_y - cdu_gc.clock_square.width*14/64, clock_x, clock_y - cdu_gc.clock_square.width*25/64);
            g2.setStroke(original_stroke);
            g2.setTransform(original_at);
            
        }

    }

    private void drawDial(Graphics2D g2) {

        int cl_i = cdu_gc.clock_square.width*21/64;
        int cl_m = cdu_gc.clock_square.width*23/64;
        int cl_o = cdu_gc.clock_square.width*25/64;

        g2.setColor(cdu_gc.knobs_color);
        g2.fillOval(clock_x-clock_r, clock_y-clock_r, 2*clock_r, 2*clock_r);

        g2.setColor(cdu_gc.background_color);
        g2.fillOval(clock_x-clock_r*280/300, clock_y-clock_r*280/300, 2*clock_r*280/300, 2*clock_r*280/300);

        AffineTransform original_at = g2.getTransform();
        Stroke original_stroke = g2.getStroke();

        g2.setColor(cdu_gc.dim_markings_color);
        //g2.setStroke(new BasicStroke(2.0f * cdu_gc.scaling_factor));
        for ( int i=0; i<15; i++ ) {
            g2.rotate(Math.toRadians(i*6), clock_x, clock_y);
            if ( i % 5 == 0 ) {
                g2.setStroke(new BasicStroke(4.0f * cdu_gc.scaling_factor));
                g2.drawLine(clock_x, clock_y - cl_i, clock_x, clock_y - cl_o);
                g2.drawLine(clock_x, clock_y + cl_i, clock_x, clock_y + cl_o);
                g2.drawLine(clock_x - cl_i, clock_y, clock_x - cl_o, clock_y);
                g2.drawLine(clock_x + cl_i, clock_y, clock_x + cl_o, clock_y);
                g2.setStroke(new BasicStroke(2.0f * cdu_gc.scaling_factor));
            } else {
                g2.drawLine(clock_x, clock_y - cl_i, clock_x, clock_y - cl_m);
                g2.drawLine(clock_x, clock_y + cl_i, clock_x, clock_y + cl_m);
                g2.drawLine(clock_x - cl_i, clock_y, clock_x - cl_m, clock_y);
                g2.drawLine(clock_x + cl_i, clock_y, clock_x + cl_m, clock_y);
            }
            g2.setTransform(original_at);
        }
        g2.setStroke(original_stroke);

        int d_r = cdu_gc.clock_square.width*18/64;
        int d_rx = d_r * 82 /100;
        int d_ry = d_r / 2;
        int d_h = cdu_gc.line_height_xxxl/2;
        int d_f = cdu_gc.line_height_xxxl*1/8;
        int d_w = cdu_gc.digit_width_xxxl;
        g2.setFont(cdu_gc.font_xxxl);
        g2.drawString("60", clock_x - d_w, clock_y - d_r + d_h - d_f);
        g2.drawString("10", clock_x + d_rx - d_w, clock_y - d_ry + d_h - d_f);
        g2.drawString("20", clock_x + d_rx - d_w, clock_y + d_ry + d_h - d_f);
        g2.drawString("30", clock_x - d_w, clock_y + d_r + d_h - d_f);
        g2.drawString("40", clock_x - d_rx - d_w, clock_y + d_ry + d_h - d_f);
        g2.drawString("50", clock_x - d_rx - d_w, clock_y - d_ry + d_h - d_f);
        

    }


}
