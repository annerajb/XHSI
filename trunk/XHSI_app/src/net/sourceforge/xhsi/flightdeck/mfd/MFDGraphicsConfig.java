/**
 * MFDGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009-2011  Marc Rogiers (marrog.123@gmail.com)
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

import net.sourceforge.xhsi.XHSIInstrument;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Avionics;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class MFDGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public boolean airbus_style;
    public boolean boeing_style;
    public int ecam_version = 1;
    public int num_eng = 0; 
    
    public int mfd_size;

    public int dial_n2_y;
    public int dial_ng_y;
    public int dial_ff_y;
    public int dial_oilp_y;
    public int dial_oilt_y;
    public int dial_oilq_y;
    public int dial_vib_y;
    public int dial_hyd_p_y;
    public int dial_hyd_q_y;
    public int dial_r[] = new int[9];
    public Font dial_font[] = new Font[9];
    public int dial_font_w[] = new int[9];
    public int dial_font_h[] = new int[9];
    public int tape_h;
    public int dial_x[] = new int[8];
    public int tape_x[] = new int[8];

    
    // ECAM Lower : Common indicators
    public int ec_base_line;  
    public int ec_line1; 
    public int ec_line2;
    public int ec_line3;
    public int ec_sep1;
    public int ec_sep2;
    public int ec_col1;
    public int ec_col1_val;            
    public int ec_col1_unit;
    public int ec_col2;
    public int ec_col2_ctr;
    public int ec_col3;
    public int ec_col3_val;
    public int ec_col3_unit;
    
    // ECAM Lower : Fuel
    public int fuel_r;
    public int fuel_primary_x[] = new int[5];
    public int fuel_primary_y[] = new int[5];
    public int fuel_total_x;
    public int fuel_total_y;
    public int fuel_flow_y;
    public Font fuel_eng_font;
    public int fuel_eng_font_w;
    public int fuel_eng_font_h;
    
    // ECAM Lower : Flight controls
    public int controls_x;
    public int controls_w;
    public int controls_y;
    public int controls_h;
    public int trim_txt_x;
    public int trim_txt_y;
    public int lat_trim_x;
    public int lat_trim_w;
    public int lat_trim_y;
    public int lat_trim_h;
    public int yaw_trim_y;
    public int pitch_trim_x;
    public int pitch_trim_w;
    public int pitch_trim_y;
    public int pitch_trim_h;
    public int wing_x;
    public int wing_y;
    public int wing_h;
    public int wing_w;
    public int flaps_l;
    public int spdbrk_h;
    public int spdbrk_w;
    public int spdbrk_x;
    public int spdbrk_y;
    public int gear_x;
    public int gear_w;
    public int gear_y;
    public int autbrk_x;
    public int autbrk_y;
    // Airbus Flight Controls
    public int fctl_mid_x;  
    public int fctl_dx_wing_box;
    public int fctl_dx_wing_a; 
    public int fctl_dx_wing_b; 
    public int fctl_dx_wing_c; 
    public int fctl_dx_ail; 
    public int fctl_dx_elev; 
    public int fctl_dx_mid_box;
    public int fctl_dx_elev_box; 
    public int fctl_dx_elev_txt;
    public int fctl_dx_ail_box1; 
    public int fctl_dx_ail_box2;
    public int fctl_dx_ail_txt;
    public int fctl_dx_ail_end;
    public int fctl_dx_rud_a; 
    public int fctl_dx_rud_b; 
    public int fctl_dx_rud_c; 
    public int fctl_dx_pitch_box; 
    public int fctl_dx_box_width;
    public int fctl_dx_tri;
    public int fctl_y_wing_box;
    public int fctl_y_wing_top;
    public int fctl_y_wing_mid1; 
    public int fctl_y_wing_mid2; 
    public int fctl_y_wing_bottom;
    public int fctl_dy_wing_mark;
    public int fctl_y_ail_top;
    public int fctl_y_ail_bottom;
    public int fctl_y_ail_box_top; 
    public int fctl_dy_ail_end;
    public int fctl_y_pitch_box_top;
    public int fctl_y_pitch_txt;
    public int fctl_y_elev_top;
    public int fctl_y_elev_bottom;
    public int fctl_y_rud_top;
    public int fctl_y_rud_mid1;
    public int fctl_y_rud_mid2;
    public int fctl_y_rud_bottom;
    public int fctl_y_rud_box_top;
    public int fctl_y_elev_box_top;
    public int fctl_box_height;
    public int fctl_y_rud_bullet;
    public int fctl_r_rud_arc;
    public int fctl_r_rud_bullet;
    public int fctl_y_rud_arc_top;
    public int fctl_y_rud_arc_end;
    public int fctl_dy_tri;
    
    
    public MFDGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }


//    public void init() {
//
//        super.init();
//
//    }


    public void update_config(Graphics2D g2, boolean power, int instrument_style, int nb_engines) {

        if (this.resized
                || this.reconfig
                || (this.style != instrument_style)
                || (this.num_eng !=  nb_engines)
                || (this.powered != power)
            ) {
            // one of the settings has been changed
        	
            //logger.warning("MFD update_config");
            super.update_config(g2);
            
            // remember the instrument style
            this.style = instrument_style;
            
            // remember the avionics power settings
            this.powered = power;
            
            // remember the number of engines
            this.num_eng = nb_engines;

            // Setup instrument style
            airbus_style = ( instrument_style == Avionics.STYLE_AIRBUS );
            boeing_style = ! ( instrument_style == Avionics.STYLE_AIRBUS );
            
            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

            mfd_size = Math.min(panel_rect.width, panel_rect.height);
            
            if (airbus_style) { mfd_size = Math.min(panel_rect.width, panel_rect.height- line_height_l*4); }
            
            // ECAM Lower Common indicators
            ec_base_line = panel_rect.y + panel_rect.height - line_height_l*4;
            
            ec_line1 = ec_base_line + line_height_l; 
            ec_line2 = ec_base_line + line_height_l*2;
            ec_line3 = ec_base_line + line_height_l*3;
            ec_sep1 = panel_rect.x + panel_rect.width * 330/1000;
            ec_sep2 = panel_rect.x + panel_rect.width * 660/1000;
            ec_col1 = panel_rect.x + digit_width_l*2;
            ec_col1_val = panel_rect.x + digit_width_l*8;            
            ec_col1_unit = panel_rect.x + digit_width_l*12;
            ec_col2 = ec_sep1 + digit_width_l;
            ec_col2_ctr = ec_sep1 + (ec_sep2-ec_sep1)/2;
            ec_col3 = ec_sep2 + digit_width_l;
            ec_col3_val = ec_col3 + digit_width_l*4;
            ec_col3_unit = ec_col3 + digit_width_l*14;
            
            // FUEL
            fuel_r = mfd_size*40/100*20/100;
            fuel_primary_x[0] = panel_rect.x + panel_rect.width/2;
            fuel_primary_x[1] = panel_rect.x + panel_rect.width/2 - fuel_r*6/4;
            fuel_primary_x[2] = panel_rect.x + panel_rect.width/2 + fuel_r*6/4;
            fuel_primary_x[3] = panel_rect.x + panel_rect.width/2 - fuel_r*17/4;
            fuel_primary_x[4] = panel_rect.x + panel_rect.width/2 + fuel_r*17/4;
            fuel_primary_y[0] = panel_rect.y + mfd_size/2 - fuel_r*3;
            fuel_primary_y[1] = panel_rect.y + mfd_size/2 - fuel_r;
            fuel_primary_y[2] = fuel_primary_y[1];
            fuel_primary_y[3] = fuel_primary_y[1];
            fuel_primary_y[4] = fuel_primary_y[1];
            fuel_total_x = panel_rect.x + panel_rect.width*1/20;
            fuel_total_y = panel_rect.y + mfd_size*90/100;
            fuel_flow_y = panel_rect.y + mfd_size*70/100;
            if (nb_engines <5) {
            	fuel_eng_font = font_l;
                fuel_eng_font_w = digit_width_l;
                fuel_eng_font_h = line_height_l;
            } else {
            	fuel_eng_font = font_s;
                fuel_eng_font_w = digit_width_s;
                fuel_eng_font_h = line_height_s;
            }
            
            
            // Lower EICAS dials
            
            int cols = Math.max(nb_engines, 2);
            if ( cols == 2 ) {
                dial_x[0] = panel_rect.x + panel_rect.width*30/100;
                tape_x[0] = dial_x[0] + dial_r[2]/2;
                dial_x[1] = panel_rect.x + panel_rect.width*70/100;
                tape_x[1] = dial_x[1] - dial_r[2]/2;
            } else {
                for (int i=0; i<cols; i++) {
                    dial_x[i] = panel_rect.x + panel_rect.width*50/100/cols + i*panel_rect.width*9/10/cols;
                    tape_x[i] = dial_x[i] - dial_r[cols]/2;
                }
            }

            dial_n2_y = panel_rect.y + mfd_size*12/100;
            dial_ng_y = dial_n2_y;
            dial_ff_y = panel_rect.y + mfd_size*30/100;
            dial_oilp_y = panel_rect.y + mfd_size*44/100;
            dial_oilt_y = panel_rect.y + mfd_size*62/100;
            dial_oilq_y = panel_rect.y + mfd_size*76/100;
            dial_vib_y = panel_rect.y + mfd_size*90/100;
            dial_r[0] = 0; // no radius for 0 engines
            dial_r[1] = Math.min(mfd_size*9/100, panel_rect.width*9/100); // dial radius when there is 1 engine
            dial_r[2] = Math.min(mfd_size*9/100, panel_rect.width*9/100); // dial radius when there are 2 engines
            dial_r[3] = Math.min(mfd_size*9/100, panel_rect.width*75/1000); // etc...
            dial_r[4] = Math.min(mfd_size*9/100, panel_rect.width*525/10000);
            dial_r[5] = Math.min(mfd_size*9/100, panel_rect.width*5/100);
            dial_r[6] = Math.min(mfd_size*9/100, panel_rect.width*45/1000);
            dial_r[7] = Math.min(mfd_size*9/100, panel_rect.width*4/100);
            dial_r[8] = Math.min(mfd_size*9/100, panel_rect.width*325/10000);
            dial_font[0] = null;
            dial_font[1] = font_xxl;
            dial_font_w[1] = digit_width_xxl;
            dial_font_h[1] = line_height_xxl;
            dial_font[2] = font_xxl;
            dial_font_w[2] = digit_width_xxl;
            dial_font_h[2] = line_height_xxl;
            dial_font[3] = font_m;
            dial_font_w[3] = digit_width_m;
            dial_font_h[3] = line_height_m;
            dial_font[4] = font_s;
            dial_font_w[4] = digit_width_s;
            dial_font_h[4] = line_height_s;
            dial_font[5] = font_s;
            dial_font_w[5] = digit_width_s;
            dial_font_h[5] = line_height_s;
            dial_font[6] = font_xs;
            dial_font_w[6] = digit_width_xs;
            dial_font_h[6] = line_height_xs;
            dial_font[7] = font_xs;
            dial_font_w[7] = digit_width_xs;
            dial_font_h[7] = line_height_xs;
            dial_font[8] = font_xxs;
            dial_font_w[8] = digit_width_xxs;
            dial_font_h[8] = line_height_xxs;
            tape_h = mfd_size*14/100;
            
            // Flight controls
            controls_x = panel_rect.x + panel_rect.width * 30/100 ;
            controls_w = panel_rect.width * 40/100;
            controls_y = panel_rect.y + mfd_size * 50/100;
            controls_h = mfd_size * 45/100;
            
            trim_txt_x = controls_x + controls_w*57/100;
            trim_txt_y = controls_y + controls_h*68/100;
            
            lat_trim_x = controls_x + controls_w*2/100;
            lat_trim_w = controls_w/2;
            lat_trim_y = controls_y + controls_h*64/100;
            lat_trim_h = controls_h/2;
            
            yaw_trim_y = lat_trim_y + lat_trim_h*6/10;
            
            pitch_trim_x = controls_x + controls_w*70/100;
            pitch_trim_w = controls_w/2;
            pitch_trim_y = controls_y + controls_h*64/100;
            pitch_trim_h = controls_h*5/16;
            
            wing_x = controls_x + controls_w*8/100;
            wing_y = controls_y + controls_h*9/32;
            wing_h = controls_h*3/100;
            wing_w = controls_w*26/100;
            flaps_l = controls_w*18/100;
            spdbrk_h = controls_h*3/100;
            spdbrk_w = controls_w*12/100;
            spdbrk_x = wing_x + wing_w - spdbrk_w;
            spdbrk_y = wing_y - wing_h/2;
            
            gear_x = controls_x + controls_w*80/100;
            gear_w = controls_w*10/100;
            gear_y = controls_y + controls_h*1/16;
            autbrk_x = gear_x;
            autbrk_y = controls_y + controls_h*6/16;
            
            // Airbus Flight Controls
            fctl_mid_x = panel_rect.x + panel_rect.width / 2;  // middle axis
            fctl_dx_wing_box = panel_rect.width * 53/1000; // middle text box
            fctl_dx_wing_a = panel_rect.width * 38/1000; // wing
            fctl_dx_wing_b = panel_rect.width * 113/1000; // wing
            fctl_dx_wing_c = panel_rect.width * 421/1000; // wing
            fctl_dx_ail = panel_rect.width * 383/1000; // aileron
            fctl_dx_elev = panel_rect.width * 226/1000; // elevators
            fctl_dx_elev_txt = panel_rect.width * 316/1000; // elevators legend
            fctl_dx_mid_box = panel_rect.width * 53/1000; // wing
            fctl_dx_elev_box = panel_rect.width * 278/1000; // elevator text box
            fctl_dx_ail_box1 = panel_rect.width * 267/1000; // inner aileron text box
            fctl_dx_ail_box2 = panel_rect.width * 429/1000; // outer aileron text box
            fctl_dx_ail_txt = panel_rect.width * 458/1000; // aileron legend 
            fctl_dx_ail_end = panel_rect.width * 9/1000; // aileron end box
            fctl_dx_rud_a = panel_rect.width * 60/1000; // rudder
            fctl_dx_rud_b = panel_rect.width * 120/1000; // rudder
            fctl_dx_rud_c = panel_rect.width * 173/1000; // rudder
            fctl_dx_pitch_box = panel_rect.width * 120/1000; // rudder
            fctl_dx_box_width = panel_rect.width * 67/1000; // hydrolic box width
            fctl_dx_tri = mfd_size * 30/1000;
            fctl_y_wing_box = panel_rect.y + mfd_size * 25/1000;
            fctl_y_wing_top = panel_rect.y + mfd_size * 67/1000;
            fctl_y_wing_mid1 = panel_rect.y + mfd_size * 133/1000; 
            fctl_y_wing_mid2 = panel_rect.y + mfd_size * 150/1000; 
            fctl_y_wing_bottom = panel_rect.y + mfd_size * 200/1000;
            fctl_dy_wing_mark = mfd_size * 8/1000;
            fctl_y_ail_top = panel_rect.y + mfd_size * 229/1000;
            fctl_y_ail_bottom = panel_rect.y + mfd_size * 492/1000;
            fctl_y_ail_box_top = panel_rect.y + mfd_size * 433/1000;            
            fctl_dy_ail_end = mfd_size * 20/1000;
            fctl_y_pitch_box_top = panel_rect.y + mfd_size * 508/1000;
            fctl_y_pitch_txt = panel_rect.y + mfd_size * 583/1000;
            fctl_y_elev_top = panel_rect.y + mfd_size * 592/1000;
            fctl_y_elev_bottom = panel_rect.y + mfd_size * 842/1000;
            fctl_y_rud_top = panel_rect.y + mfd_size * 591/1000;
            fctl_y_rud_mid1 = panel_rect.y + mfd_size * 625/1000;
            fctl_y_rud_mid2 = panel_rect.y + mfd_size * 642/1000;
            fctl_y_rud_bottom = panel_rect.y + mfd_size * 667/1000;
            fctl_y_rud_box_top = panel_rect.y + mfd_size * 692/1000;
            fctl_y_rud_bullet = panel_rect.y + mfd_size * 808/1000;
            fctl_y_elev_box_top = panel_rect.y + mfd_size * 742/1000;
            fctl_box_height = mfd_size * 42/1000;  // font_heigth
            fctl_r_rud_arc = mfd_size * 200/1000;
            fctl_r_rud_bullet = mfd_size * 29/1000;
            fctl_y_rud_arc_top = panel_rect.y + mfd_size * 717/1000;
            fctl_y_rud_arc_end = panel_rect.y + mfd_size * 912/1000; 
            fctl_dy_tri = mfd_size * 20/1000;

            
        }

    }


//    public int get_text_width(Graphics graphics, Font font, String text) {
//        return graphics.getFontMetrics(font).stringWidth(text);
//    }


//    public int get_text_height(Graphics graphics, Font font) {
//        return graphics.getFontMetrics(font).getHeight();
//    }


    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.frame_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent event) {
        this.component_topleft = event.getComponent().getLocation();
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}

