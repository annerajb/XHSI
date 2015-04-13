/**
 * NDGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.eicas;

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


public class EICASGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    public static final int MAX_DETENTS = 12;
    public int num_eng = 0; 
    public int eng_label = 0; // engine at the left of engines labels 

    public boolean airbus_style;
    public boolean boeing_style;
    public int ecam_version = 1;
      
    public int eicas_size;
    public int dials_width;
    public int prim_dials_height;
    public int hyd_dials_height;
    public int dial_main1_y;
    public int dial_main2_y;
    public int dial_main3_y;
    public int dial_main4_y;
    public int dial_main5_y;
    //public int dial_ng_y;
    public int dial_oil_p_y;
    public int dial_oil_t_y;
    public int dial_oil_q_y;
    public int dial_vib_y;
    public int dial_hyd_p_y;
    public int dial_hyd_q_y;
    public int dial_r[] = new int[9];
    public Font dial_font[] = new Font[9];
    public int dial_font_w[] = new int[9];
    public int dial_font_h[] = new int[9];
    public int alerts_w;
    public int alerts_x0;
    public int alert_y[] = new int[3];
    public int alert_h;
    public int fuel_r;
    public int fuel_primary_x[] = new int[3];
    public int fuel_primary_y[] = new int[3];
    public int fuel_compact_x[] = new int[3];
    public int fuel_compact_y[] = new int[3];
    public int divtext_y;
    public int tat_x;
    public int thrustmode_x;
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
    public int prim_dial_x[] = new int[8];
    public int seco_dial_x[] = new int[8];
    public int message_x;
    public int memo_x;
    public int memo_y;
    public int detents=0;
    public int ecam_flaps_box_x;
    public int ecam_flaps_box_y;
    public int ecam_flaps_box_w;
    public int ecam_flaps_box_h;    
    public int ecam_bullet_r;
    public int ecam_flaps_center_x;
    public int ecam_flaps_center_y;
    public int ecam_flaps_w;
    public int ecam_flaps_h;    
    public Point ecam_flaps_bullet[] = new Point[MAX_DETENTS];
    public int ecam_slats_center_x;
    public int ecam_slats_center_y;
    public int ecam_slats_w;
    public int ecam_slats_h;
    public Point ecam_slats_bullet[] = new Point[MAX_DETENTS];
    public int ecam_messages_w;
    public int eng_label_x;
    public int ref_n1_x;
    public int ref_n1_y;
    

    public EICASGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
        for (int i=0; i<MAX_DETENTS; i++) {
        	ecam_flaps_bullet[i]= new Point(0,0);
        	ecam_slats_bullet[i]= new Point(0,0);
        }
    }



    public void update_config(Graphics2D g2, boolean power, int instrument_style, int nb_engines, int detents ) {

        if (this.resized
                || this.reconfig
                || (this.powered != power)
                || (this.style != instrument_style)
                || (this.num_eng !=  nb_engines)
                || (this.detents != detents)
            ) {
            // one of the settings has been changed

            // remember the avionics power settings
            this.powered = power;
            
            // remember the instrument style
            this.style = instrument_style;
            
            // remember the number of engines
            this.num_eng = nb_engines;
            
            // remember the number of flaps / slats detents
            this.detents = detents;

//logger.warning("EICAS update_config");
            // general instrument config
            super.update_config(g2);

            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

            // Setup instrument style
            airbus_style = ( instrument_style == Avionics.STYLE_AIRBUS );
            boeing_style = ! ( instrument_style == Avionics.STYLE_AIRBUS );
            
            // Airbus software version
            // TODO : add a preference panel option for version 2 with 2 engines
        	ecam_version = (this.num_eng <= 2) ? 1 : 2;
        	// force version, design mode
        	// ecam_version = 2;
            
            
            eicas_size = Math.min(panel_rect.width, panel_rect.height);

            int cols = Math.max(num_eng, 2);

            
            if (boeing_style) {
                for (int i=0; i<cols; i++) {
                    prim_dial_x[i] = panel_rect.x + dials_width*50/100/cols + i*dials_width/cols;
                    seco_dial_x[i] = alerts_x0 + i*alerts_w/cols + (alerts_w/cols*15/16)/2;
                }
            	// the dials take +/-60% of the width
            	dials_width = panel_rect.width*575/1000;
            	dial_main1_y = panel_rect.y + eicas_size*17/100;
            	dial_main2_y = panel_rect.y + eicas_size*36/100;
            	dial_main3_y = panel_rect.y + eicas_size*55/100;
            	dial_main4_y = panel_rect.y + eicas_size*73/100;
            	dial_main5_y = panel_rect.y + eicas_size*89/100;
                dial_r[0] = 0; // no radius for 0 engines
                dial_r[1] = Math.min(eicas_size*9/100, panel_rect.width*9/100); // dial radius when there is 1 engine
                dial_r[2] = Math.min(eicas_size*9/100, panel_rect.width*9/100); // dial radius when there are 2 engines
                dial_r[3] = Math.min(eicas_size*9/100, panel_rect.width*75/1000); // etc...
                dial_r[4] = Math.min(eicas_size*9/100, panel_rect.width*525/10000);
                dial_r[5] = Math.min(eicas_size*9/100, panel_rect.width*5/100);
                dial_r[6] = Math.min(eicas_size*9/100, panel_rect.width*45/1000);
                dial_r[7] = Math.min(eicas_size*9/100, panel_rect.width*4/100);
                dial_r[8] = Math.min(eicas_size*9/100, panel_rect.width*325/10000);
                prim_dials_height = eicas_size*73/100 + dial_r[2];
                // 644/1000 
                hyd_dials_height = eicas_size*25/100;
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

            } else {
            	// Airbus style

            	// the dials take +/-60% of the width in V1 software with 2 engines
            	// otherwise, dials takes 100% of the width
            	if (this.ecam_version == 1) {
            		// ECAM version 1
            		dials_width = panel_rect.width*600/1000; 
                    prim_dials_height = eicas_size*630/1000;
                    hyd_dials_height = eicas_size*25/100;
                    ecam_messages_w = panel_rect.width*600/1000; 
                    for (int i=0; i<cols; i++) {
                        prim_dial_x[i] = panel_rect.x + dials_width*65/1000 + dials_width*50/100/cols + i*dials_width/cols;
                        seco_dial_x[i] = alerts_x0 + i*alerts_w/cols + (alerts_w/cols*15/16)/2;
                    }
                    eng_label=0;
                    eng_label_x=(prim_dial_x[eng_label] + prim_dial_x[eng_label+1]) / 2;
                	dial_main1_y = panel_rect.y + eicas_size*17/100;
                	dial_main2_y = panel_rect.y + eicas_size*33/100;
                	dial_main3_y = panel_rect.y + eicas_size*48/100;
                	dial_main4_y = panel_rect.y + eicas_size*58/100;
                	dial_main5_y = panel_rect.y + eicas_size*65/100;
                    dial_r[0] = 0; // no radius for 0 engines
                    dial_r[1] = Math.min(eicas_size*9/100, dials_width*250/1000); // dial radius when there is 1 engine
                    dial_r[2] = Math.min(eicas_size*9/100, dials_width*250/1000); // dial radius when there are 2 engines
                    dial_r[3] = Math.min(eicas_size*9/100, dials_width*250/1000); // etc...
                    dial_r[4] = Math.min(eicas_size*9/100, dials_width*200/1000);
                    dial_r[5] = Math.min(eicas_size*7/100, dials_width*140/1000);
                    dial_r[6] = Math.min(eicas_size*7/100, dials_width*110/1000);
                    dial_r[7] = Math.min(eicas_size*6/100, dials_width* 90/1000);
                    dial_r[8] = Math.min(eicas_size*6/100, dials_width* 60/1000);
            	} else {
            		// ECAM version 2
            		dials_width = panel_rect.width;            		
                    prim_dials_height = eicas_size*670/1000;
                    hyd_dials_height = eicas_size*25/100;
                    ecam_messages_w = panel_rect.width*625/1000;
                    
                    // TODO : let a title spacing in the middle
                    int col_spacing = dials_width*50/1000;
                    for (int i=0; i<cols; i++) {
                        prim_dial_x[i] = panel_rect.x + (dials_width-col_spacing)*50/100/cols + i*(dials_width-col_spacing)/cols;
                        if (i>=(cols/2)) { prim_dial_x[i] += col_spacing; }
                        seco_dial_x[i] = alerts_x0 + i*alerts_w/cols + (alerts_w/cols*15/16)/2;
                    }
                    eng_label=(cols/2)-1;
                    eng_label_x=(prim_dial_x[eng_label] + prim_dial_x[eng_label+1]) / 2;
                	dial_main1_y = panel_rect.y + eicas_size*170/1000;
                	dial_main2_y = panel_rect.y + eicas_size*355/1000;
                	dial_main3_y = panel_rect.y + eicas_size*450/1000;
                	dial_main4_y = panel_rect.y + eicas_size*520/1000;
                	dial_main5_y = panel_rect.y + eicas_size*650/1000;
                    dial_r[0] = 0; // no radius for 0 engines
                    dial_r[1] = dials_width*90/1000; // dial radius when there is 1 engine
                    dial_r[2] = dials_width*90/1000; // dial radius when there are 2 engines
                    dial_r[3] = dials_width*85/1000; // etc...
                    dial_r[4] = dials_width*85/1000;
                    dial_r[5] = dials_width*70/1000;
                    dial_r[6] = dials_width*70/1000;
                    dial_r[7] = dials_width*50/1000;
                    dial_r[8] = dials_width*50/1000;
            	}


                dial_font[0] = null;
                dial_font[1] = font_xl;
                dial_font_w[1] = digit_width_xl;
                dial_font_h[1] = line_height_xl;
                dial_font[2] = font_xl;
                dial_font_w[2] = digit_width_xl;
                dial_font_h[2] = line_height_xl;
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
            }
            message_x = panel_rect.x + panel_rect.width*40/1000;
            memo_x = panel_rect.x + panel_rect.width*630/1000;
            memo_y = panel_rect.y + prim_dials_height + line_height_l * 2;
            		
            
            //dial_ng_y = panel_rect.y + eicas_size*87/100;
            dial_oil_p_y = panel_rect.y + eicas_size*29/100;
            dial_oil_t_y = panel_rect.y + eicas_size*44/100;
            dial_oil_q_y = panel_rect.y + eicas_size*54/100;
            dial_vib_y = panel_rect.y + eicas_size*65/100;
            dial_hyd_p_y = panel_rect.y + panel_rect.height - eicas_size*16/100;
            dial_hyd_q_y = panel_rect.y + panel_rect.height - eicas_size*6/100;


            // TAT and Thrust Mode
            divtext_y = panel_rect.y + eicas_size*40/1000;
            tat_x = panel_rect.x + dials_width*12/16;
            thrustmode_x = panel_rect.x + dials_width*7/16;
            
            // the annunciators and fuel take 40% of the width
            alerts_w = panel_rect.width*39/100;
            alerts_x0 = panel_rect.x + panel_rect.width - alerts_w;
            alert_h = line_height_xs*25/10;
            alert_y[0] = panel_rect.y + eicas_size*5/100;
            alert_y[1] = alert_y[0] + alert_h;
            alert_y[2] = alert_y[1] + alert_h;
            fuel_r = eicas_size*40/100*20/100;
            
            if (boeing_style) {
                fuel_primary_x[0] = alerts_x0 + alerts_w/2;
                fuel_primary_x[1] = alerts_x0 + alerts_w/2 - fuel_r*5/4;
                fuel_primary_x[2] = alerts_x0 + alerts_w/2 + fuel_r*5/4;
                fuel_primary_y[0] = panel_rect.y + panel_rect.height - fuel_r*3;
                fuel_primary_y[1] = panel_rect.y + panel_rect.height - fuel_r;
                fuel_primary_y[2] = fuel_primary_y[1];
                fuel_compact_x[0] = panel_rect.x + dials_width/2;
                fuel_compact_x[1] = fuel_compact_x[0] - fuel_r*9/4;
                fuel_compact_x[2] = fuel_compact_x[0] + fuel_r*9/4;
                fuel_compact_y[0] = panel_rect.y + panel_rect.height - fuel_r*19/16;
                fuel_compact_y[1] = panel_rect.y + panel_rect.height - fuel_r*14/16;
                fuel_compact_y[2] = fuel_compact_y[1];
                
                controls_x = panel_rect.x + panel_rect.width * 80/100 - eicas_size * 20/100;
                controls_w = eicas_size * 39/100;
                controls_y = alert_y[2] + alert_h + eicas_size * 4/100;
                controls_h = eicas_size * 39/100;
            	// Ecam flaps value set but not used
                ecam_flaps_box_x = panel_rect.x + panel_rect.width * 634/1000;
                ecam_flaps_box_y = panel_rect.y + panel_rect.height * 355/1000;
                ecam_flaps_box_w = panel_rect.width * 350/1000;
                ecam_flaps_box_h = panel_rect.height * 200/1000;
                // Ref N1 values set but not used
                ref_n1_x = panel_rect.x + panel_rect.width * 500/1000;
                ref_n1_y = panel_rect.y + panel_rect.height * 10/1000 + line_height_m;
            } else {
            	// Airbus style
            	// fuel_primary is the text coordinates

                if (ecam_version == 1) {
                	// Airbus software version 1
                	fuel_primary_x[0] = panel_rect.x + panel_rect.width * 634/1000;
                	fuel_primary_x[1] = panel_rect.x + panel_rect.width * 813/1000;
                	fuel_primary_x[2] = panel_rect.x + panel_rect.width * 860/1000;
                	fuel_primary_y[0] = panel_rect.y + prim_dials_height * 560/1000;
                	fuel_primary_y[1] = panel_rect.y + prim_dials_height * 560/1000;
                	fuel_primary_y[2] = panel_rect.y + prim_dials_height * 560/1000;
                	// Fuel position if controls displayed
                    fuel_compact_x[0] = panel_rect.x + panel_rect.width * 634/1000;
                    fuel_compact_x[1] = panel_rect.x + panel_rect.width * 813/1000;
                    fuel_compact_x[2] = panel_rect.x + panel_rect.width * 860/1000;
                    fuel_compact_y[0] = panel_rect.y + panel_rect.height * 200/1000;
                    fuel_compact_y[1] = panel_rect.y + panel_rect.height * 200/1000;
                    fuel_compact_y[2] = panel_rect.y + panel_rect.height * 200/1000;   
                	// Controls
                    controls_x = panel_rect.x + panel_rect.width * 80/100 - eicas_size * 20/100;
                    controls_w = eicas_size * 39/100;
                    controls_y = panel_rect.y + prim_dials_height - eicas_size * 40 /100;
                    controls_h = eicas_size * 39/100;
                	// Ecam flaps on the upper right panel 
                    ecam_flaps_box_x = panel_rect.x + panel_rect.width * 610/1000;
                    ecam_flaps_box_y = panel_rect.y + prim_dials_height * 635/1000;
                    ecam_flaps_box_w = panel_rect.width * 360/1000;
                    ecam_flaps_box_h = prim_dials_height * 350/1000;
                    ecam_bullet_r = ecam_flaps_box_w*3/100;
                    // 
                    ecam_flaps_center_x = ecam_flaps_box_x + ecam_flaps_box_w*56/100;
                    ecam_flaps_center_y = ecam_flaps_box_y + ecam_flaps_box_h*98/100;
                    ecam_flaps_w = ecam_flaps_box_w*40/100;
                    ecam_flaps_h = ecam_flaps_box_h*62/100;
                    
                    ecam_slats_center_x = ecam_flaps_box_x + ecam_flaps_box_w*30/100;
                    ecam_slats_center_y = ecam_flaps_box_y + ecam_flaps_box_h*80/100;
                    ecam_slats_w = ecam_flaps_box_w*25/100;
                    ecam_slats_h = ecam_flaps_box_h*50/100;
                                       
                	// detent marks (angular)
                    double f_angle = Math.toRadians(-90.0);
                    double s_angle = Math.toRadians(-90.0);
                    double s_rotang;
                    double f_rotang;
                    if (detents >1) {
                    	f_rotang = Math.toRadians(80.0 / detents);
                    	s_rotang = -Math.toRadians(90.0 / detents);
                    } else {
                       	f_rotang = Math.toRadians(80.0);
                       	s_rotang = -Math.toRadians(90.0);                    	
                    }
                	for ( int i=0; i<=detents; i++ ) {
                		ecam_flaps_bullet[i].setLocation(ecam_flaps_center_x + ecam_flaps_w * Math.cos(f_angle), ecam_flaps_center_y + ecam_flaps_h * Math.sin(f_angle));
                		ecam_slats_bullet[i].setLocation(ecam_slats_center_x + ecam_slats_w * Math.cos(s_angle), ecam_slats_center_y + ecam_slats_h * Math.sin(s_angle));
                		f_angle += f_rotang;
                		s_angle += s_rotang;
                	}
                    // Ref N1 values on the right panel
                    ref_n1_x = panel_rect.x + panel_rect.width * 610/1000;
                    ref_n1_y = panel_rect.y + prim_dials_height * 350/1000 + line_height_l;
                		              	
                } else {
                	// Airbus software version 2
                	fuel_primary_x[0] = panel_rect.x + panel_rect.width * 5/100;
                	fuel_primary_x[1] = panel_rect.x + panel_rect.width * 23/100;
                	fuel_primary_x[2] = panel_rect.x + panel_rect.width * 28/100;
                	fuel_primary_y[0] = panel_rect.y + prim_dials_height * 948/1000;
                	fuel_primary_y[1] = panel_rect.y + prim_dials_height * 948/1000;
                	fuel_primary_y[2] = panel_rect.y + prim_dials_height * 948/1000;
                	// Fuel position if controls displayed
                    fuel_compact_x[0] = panel_rect.x + panel_rect.width * 5/100;
                    fuel_compact_x[1] = panel_rect.x + panel_rect.width * 23/100;
                    fuel_compact_x[2] = panel_rect.x + panel_rect.width * 28/100;
                    fuel_compact_y[0] = panel_rect.y + prim_dials_height * 948/1000;
                    fuel_compact_y[1] = panel_rect.y + prim_dials_height * 948/1000;
                    fuel_compact_y[2] = panel_rect.y + prim_dials_height * 948/1000;   

                	// Controls                 
                    controls_x = panel_rect.x + ecam_messages_w + eicas_size * 1/100;
                    controls_w = eicas_size * 35/100;
                    controls_y = panel_rect.y + prim_dials_height + eicas_size * 1/100;;
                    controls_h = eicas_size * 31/100;                    
                	// Ecam flaps at the bottom main gauges panel (on the right) 
                    ecam_flaps_box_x = panel_rect.x + panel_rect.width * 510/1000;
                    ecam_flaps_box_y = panel_rect.y + prim_dials_height * 835/1000;
                    ecam_flaps_box_w = panel_rect.width * 470/1000;
                    ecam_flaps_box_h = prim_dials_height * 157/1000;
                    ecam_bullet_r = ecam_flaps_box_w*3/100;
                    // 
                    ecam_flaps_center_x = ecam_flaps_box_x + ecam_flaps_box_w*(395+61)/1000;
                    ecam_flaps_center_y = ecam_flaps_box_y + ecam_flaps_box_h*355/1000;
                    ecam_flaps_w = ecam_flaps_box_w*50/100;
                    ecam_flaps_h = ecam_flaps_box_h*65/100;
                    
                    ecam_slats_center_x = ecam_flaps_box_x + ecam_flaps_box_w*(325-61)/1000;
                    ecam_slats_center_y = ecam_flaps_box_y + ecam_flaps_box_h*355/1000;
                    ecam_slats_w = ecam_flaps_box_w*23/100;
                    ecam_slats_h = ecam_flaps_box_h*50/100;
                                       
                	// detent marks (linear)
                	for ( int i=0; i<=detents; i++) {
                		ecam_flaps_bullet[i].setLocation(ecam_flaps_center_x + ecam_flaps_w * i / (detents+1), ecam_flaps_center_y + ecam_flaps_h * i / (detents+1));
                		ecam_slats_bullet[i].setLocation(ecam_slats_center_x - ecam_slats_w * i / (detents+1), ecam_slats_center_y + ecam_slats_h * i / (detents+1));
                	}
                    // Ref N1 values
                    ref_n1_x = panel_rect.x + panel_rect.width * 500/1000;
                    ref_n1_y = panel_rect.y + panel_rect.height * 10/1000 + line_height_m;
                }
            }
            
            trim_txt_x = controls_x + controls_w*57/100;
            trim_txt_y = controls_y + controls_h*68/100;
            
//            lat_trim_x = controls_x + controls_w*6/100;
            lat_trim_x = controls_x + controls_w*2/100;
            lat_trim_w = controls_w/2;
            lat_trim_y = controls_y + controls_h*64/100;
            lat_trim_h = controls_h/2;
            
            yaw_trim_y = lat_trim_y + lat_trim_h*6/10;
            
//            pitch_trim_x = controls_x + controls_w*66/100;
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
