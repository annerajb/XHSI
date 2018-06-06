/**
 * ClockGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of ClockComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2018  Nicolas Carel
 * Copyright (C) 2018  the Technische Hochschule Ingolstadt 
 *                     - Patrick Burkart
 *                     - Tim Drouven
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
package net.sourceforge.xhsi.flightdeck.clock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;
import net.sourceforge.xhsi.model.Avionics;


public class ClockGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public Rectangle clock_square;
    // public Rectangle clock_rect;
    public GradientPaint clock_gradient;

    public boolean digital_clock_style;
    public boolean analog_clock_style;
    private int clock_style;

    public Font font_header;
    public Font font_label;
    public Font font_label_small;
    public Font font_label_small_condensed;
    public Font font_label_bold;
    public Font font_seven_seg_large;
    public Font font_seven_seg;
    
    // Needle Stoke
    BasicStroke needle_stroke; 

    int clock_x;
    int clock_y;
    int clock_r;
    
    //Coordinates for Clock
    boolean size_set = false;
    int utc_time_string_x;
    int utc_time_string_y;
    int utc_second_string_x;
    int utc_second_string_y;
    int timer_string_x;
    int timer_string_y;
    int small_button_size_x;
    int small_button_size_y;
    int large_button_size_x;
    int large_button_size_y;
    int button_reset_x;
    int button_reset_y;
    int button_chr_x;
    int button_chr_y;
    int button_date_x;
    int button_date_y;
    int button_gps_x[];
    int button_gps_y[];
    int button_gps_center_x;
    int button_gps_center_y;
    int button_run_x[];
    int button_run_y[];
    int button_run_center_x;
    int button_run_center_y;
    int button_set_x;
    int button_set_y;
    int button_mvt_x;
    int elapsed_time_string_x;
    int elapsed_time_string_y;
    int frame_x[] = new int[8];
    int frame_y[] = new int[8];
    int background_x;
    int background_y;
    int background_width;
    int background_height;
    int background_corner_size;
    int chr_background_x;
    int chr_background_y;
    int chr_background_width;
    int chr_background_height;
    int utc_background_x;
    int utc_background_y;
    int utc_background_width;
    int utc_background_height;
    int et_background_x;
    int et_background_y;
    int et_background_width;
    int et_background_height;
    int times_background_corner_size;
    int rst_string_x;
    int rst_string_y;
    int chr_string_x;
    int chr_string_y;
    int run_hold_string_x;
    int run_hold_string_y;
    int date_string_x;
    int date_string_y;
    int gps_string_x;
    int gps_string_y;
    int int_string_x;
    int int_string_y;
    int set_string_x;
    int set_string_y;
    int run_string_x;
    int run_string_y;
    int stp_string_x;
    int stp_string_y;
    int rst_run_string_x;
    int rst_run_string_y;
    int chr2_string_x;
    int chr2_string_y;
    int utc_string_x;
    int utc_string_y;
    int lt_string_x;
    int lt_string_y;
    int et_string_x;
    int et_string_y;
    int et2_string_x;
    int et2_string_y;
    int min_string_x;
    int min_string_y;
    int sec_string_x;
    int sec_string_y;
    int hr_mo_string_x;
    int hr_mo_string_y;
    int min_dy_string_x;
    int min_dy_string_y;
    int sec_y_string_x;
    int sec_y_string_y;
    int hr_string_x;
    int hr_string_y;
    int min2_string_x;
    int min2_string_y;
    
    public BufferedImage background_img;
    public BufferedImage button_round_img;
    public BufferedImage button_selector_img;
    
    public final static int BUTTON_RESET = 0;
    public final static int BUTTON_CHR   = 1;
    public final static int BUTTON_DATE  = 2;
    public final static int BUTTON_GPS   = 3;
    public final static int BUTTON_RUN   = 4;
    public Shape[] buttons;
    public Shape button_gps;
    public Shape button_run;
    
    public ClockGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
        digital_clock_style=false;
        this.clock_style = -1;
        buttons = new Shape[5];
    }


    public void update_config(Graphics2D g2, boolean power, int clock_style) {
    	
    	 boolean clock_style_changed = this.clock_style != clock_style;

        if (this.resized
                || this.reconfig
                || (this.powered != power
                || (clock_style_changed))
            ) {
            // one of the settings has been changed

            // Setup clock style
            this.clock_style = clock_style;            
            digital_clock_style = (this.clock_style == Avionics.STYLE_CLOCK_DIGITAL);
            analog_clock_style = (this.clock_style == Avionics.STYLE_CLOCK_ANALOG);
            
        	
            // remember the avionics power settings
            // actually, for the annunciators, we use battery power, not avionics power
            this.powered = power;
            super.update_config(g2);

            // some subcomponents need to be reminded to redraw immediately
            this.reconfigured = true;
            
            /*
             * clock_square is the reference square with absolute window coordinates.
             * all the graphics elements must fits in.
             */

            int square_size;
            square_size = Math.min(panel_rect.width, panel_rect.height)/2;
            clock_square = new Rectangle(
                    panel_rect.x + panel_rect.width/2 - square_size,
                    panel_rect.y + panel_rect.height/2 - square_size,
                    2*square_size,
                    2*square_size
                );

    		clock_x = clock_square.x + clock_square.width/2;
    		clock_y = clock_square.y + clock_square.height/2;
    		clock_r = clock_square.width/2*7/8;
    		           
            int clusterWidth;
            int clusterHeight;

            if (panel_rect.width / 1064.0 < panel_rect.height / 994.0) {
            	// Panel width is narrow
            	clusterWidth = panel_rect.width;
            	clusterHeight = (int) (panel_rect.width * 994.0 / 1064.0);

            } else {
            	// Panel height is narrow
            	clusterWidth = (int) ((panel_rect.height) * 1064.0 / 994.0);
            	clusterHeight = (panel_rect.height);
            }


            // TODO: move font configuration to global GraphicConfig
            font_header = new Font("Arial", Font.BOLD, (int) ((clusterWidth / 1064.0) * 18));
            font_label_bold = new Font("Arial", Font.BOLD, (int) ((clusterWidth / 1064.0) * 40));
            font_label = new Font("Arial", Font.PLAIN, (int) ((clusterWidth / 1064.0) * 45));
            font_label_small = new Font("Arial", Font.PLAIN, (int) ((clusterWidth / 1064.0) * 32));
            font_label_small_condensed = new Font("S1 F16 Panel Font V1.1", Font.PLAIN, (int) ((clusterWidth / 1064.0) * 36));
            if (font_label_small_condensed.getFontName().equalsIgnoreCase("Dialog.plain")) {
            	font_label_small_condensed = new Font("Arial", Font.PLAIN, (int) ((clusterWidth / 1064.0) * 26));
            }

            /*
             * DSEG7 Classic is available from :
             * http://fontsforweb.com/font/generatezip/?id=79190
             */
            font_seven_seg_large = new Font("DSEG7 Classic", Font.ITALIC, (int) ((clusterWidth / 1064.0) * 90));
            font_seven_seg = new Font("DSEG7 Classic", Font.ITALIC, (int) ((clusterWidth / 1064.0) * 60));
            
            // Unused
            clock_gradient = new GradientPaint(
                    0, 0, frontpanel_color.brighter().brighter(),
                    clusterWidth, clusterHeight, frontpanel_color.darker().darker(),
                    false);

            if (resized || clock_style_changed || !size_set) {

            	/*
            	 * Reference measure from original picture
            	 */
                double r_w;
                double r_h;
                
                /*
                 * Digital display
                 */ 
                if (digital_clock_style) {
                	// Digital clock
                    r_w = clock_square.width / 1064.0;
                    r_h = clock_square.height / 994.0;
                    
                	utc_time_string_x = clock_square.x + inRel(r_w, 289);
                	utc_time_string_y = clock_square.y + inRel(r_h, 560);
                	utc_second_string_x = clock_square.x + inRel(r_w, 642);
                	utc_second_string_y = clock_square.y + inRel(r_h, 547);
                	timer_string_x = clock_square.x + inRel(r_w, 369); // was 375
                	timer_string_y = clock_square.y + inRel(r_h, 295);
                	elapsed_time_string_x = clock_square.x + inRel(r_w, 369);  // was 375
                	elapsed_time_string_y = clock_square.y + inRel(r_h, 797);
                } else {
                	// Analog clock
                    r_w = clock_square.width / 1000.0;
                    r_h = clock_square.height / 1000.0;
                	
                	utc_time_string_y = clock_y + line_height_zl*38/100 - line_height_zl*3/4;
                	timer_string_x = clock_x - get_text_width(g2, font_xxl, "ET") - digit_width_xxl;
                	timer_string_y = clock_y + line_height_zl*38/100 + line_height_zl*3/4;     
                	elapsed_time_string_y = timer_string_y;
                }
                
                /*
                 * Needle
                 */
                needle_stroke = new BasicStroke(8.0f * scaling_factor);
                

                background_x = inRel(r_w, 231);
                background_y = inRel(r_h, 99);
                background_width = inRel(r_w, 608);
                background_height = inRel(r_h, 803);
                background_corner_size = inRel(r_w, 70); // was 30
                chr_background_x = inRel(r_w, 365);
                chr_background_y = inRel(r_h, 178);
                chr_background_width = inRel(r_w, 350);
                chr_background_height = inRel(r_h, 136);
                utc_background_x = inRel(r_w, 271);
                utc_background_y = inRel(r_h, 438);
                utc_background_width = inRel(r_w, 536);
                utc_background_height = inRel(r_h, 136);
                et_background_x = inRel(r_w, 365);
                et_background_y = inRel(r_h, 674);
                et_background_width = inRel(r_w, 350);
                et_background_height = inRel(r_h, 136);
                times_background_corner_size = inRel(r_w, 30);
                rst_string_x = inRel(r_w, 114);
                rst_string_y = inRel(r_h, 203);
                date_string_x = inRel(r_w, 100);
                date_string_y = inRel(r_h, 632);
                gps_string_x = inRel(r_w, 879);  // was 885
                gps_string_y = inRel(r_h, 414);
                int_string_x = inRel(r_w, 955);  // was 961
                int_string_y = inRel(r_h, 460);
                set_string_x = inRel(r_w, 955);  // was 961
                set_string_y = inRel(r_h, 517);
                run_string_x = inRel(r_w, 877);  // was 883
                run_string_y = inRel(r_h, 653);
                stp_string_x = inRel(r_w, 955);  // was 961
                stp_string_y = inRel(r_h, 696);
                rst_run_string_x = inRel(r_w, 955); // was 961
                rst_run_string_y = inRel(r_h, 753);
                chr2_string_x = inRel(r_w, 486);
                chr2_string_y = inRel(r_h, 146);
                
                if (digital_clock_style) {
                	// Digital clock
                	
                    /*
                     * Clock frame and background
                     */
                    frame_x = new int[]{182, 883, 1009, 1009, 883, 182, 53, 53};
                    frame_y = new int[]{47, 47, 176, 815, 945, 945, 815, 176};
                    for (int i = 0; i < frame_x.length; i++) {
                        frame_x[i] = inRel(r_w, frame_x[i]);
                        frame_y[i] = inRel(r_h, frame_y[i]);
                    }
                    
                    chr_string_x = inRel(r_w, 870);
                    chr_string_y = inRel(r_h, 209);
                	utc_string_x = inRel(r_w, 486);
                	utc_string_y = inRel(r_h, 411);
                	et_string_x = inRel(r_w, 508);
                	et_string_y = inRel(r_h, 879);                	
                    min_string_x = inRel(r_w, 397);
                    min_string_y = inRel(r_h, 358); // was 372
                    sec_string_x = inRel(r_w, 590);
                    sec_string_y = inRel(r_h, 358); // was 372
                    hr_mo_string_x = inRel(r_w, 289);
                    hr_mo_string_y = inRel(r_h, 618); // was 629
                    min_dy_string_x = inRel(r_w, 447);
                    min_dy_string_y = inRel(r_h, 618); // was 629
                    sec_y_string_x = inRel(r_w, 626);
                    sec_y_string_y = inRel(r_h, 618); // was 629
                    hr_string_x = inRel(r_w, 397);
                    hr_string_y = inRel(r_h, 846);
                    min2_string_x = inRel(r_w, 590);
                    min2_string_y = inRel(r_h, 846);
                } else {
                	// Analog clock   
                	
                    /*
                     * Clock frame and background
                     */
                    frame_x = new int[]{150, 850, 975, 975, 850, 150, 25, 25};
                    frame_y = new int[]{25, 25, 150, 850, 975, 975, 850, 150};
                    for (int i = 0; i < frame_x.length; i++) {
                        frame_x[i] = inRel(r_w, frame_x[i]);
                        frame_y[i] = inRel(r_h, frame_y[i]);
                    }
                	             	
                	utc_string_x = clock_x - get_text_width(g2, font_xxl, "UTC") - digit_width_xxl;
                    utc_string_y = utc_time_string_y - (line_height_zl*8/10 + line_height_xxl/2);
                    lt_string_x = clock_x + digit_width_xxl;
                    lt_string_y = utc_string_y;
                    run_hold_string_x= clock_x - get_text_width(g2, font_xxl, "RUN")/2;
                    run_hold_string_y = timer_string_y + line_height_xxl*5/4 + line_height_zl*1/10;
                    et_string_x = run_hold_string_x - get_text_width(g2, font_xxl, "ET") - digit_width_xl;
                    et_string_y = timer_string_y + line_height_xxl*5/4 + line_height_zl*1/10;
                    chr_string_x = clock_x + digit_width_xxl*3/2;
                    chr_string_y = et_string_y;

                    // "TIME/DATE"
                    date_string_x = inRel(r_w, 650);
                    date_string_y = inRel(r_h, 80);
                    // "CHR"
                    chr2_string_x = inRel(r_w, 150);
                    chr2_string_y = inRel(r_h, 80);
                    // "ET"
                    et2_string_x = inRel(r_w, 150);
                    et2_string_y = inRel(r_h, 960);
                    // "HOLD TO RESET"
                    rst_run_string_x = et2_string_x + get_text_width(g2, font_xl, "ET "); 
                    rst_run_string_y = inRel(r_h, 960);
                    // "SET"
                    set_string_x = inRel(r_w, 760);
                    set_string_y = inRel(r_h, 960);
                    // "PRESS TO"
                    int_string_x = set_string_x - get_text_width(g2, font_m, "PRESS TO ");
                    int_string_y = inRel(r_h, 960);
                }
                

                
                /*
                 * buttons array defines buttons drawing areas and mouse regions 
                 */
                small_button_size_x = inRel(r_h, 90);
                small_button_size_y = inRel(r_h, 90);
                large_button_size_x = inRel(r_h, 120);
                large_button_size_y = inRel(r_h, 120);
                
                button_reset_x = inRel(r_w, 105);
                button_reset_y = inRel(r_h, 220);
                button_chr_x = inRel(r_w, 865);
                button_chr_y = inRel(r_h, 217);
                button_date_x = inRel(r_w, 90);
                button_date_y = inRel(r_h, 453);
                
                button_gps_x = new int[]{914, 885, 885, 914, 949, 949};
                button_gps_y = new int[]{558, 554, 492, 434, 492, 554};
                button_gps_center_x = clock_square.x + inRel(r_w, 916);
                button_gps_center_y = clock_square.y + inRel(r_w, 535); // was 494
                
                button_run_x = new int[]{914, 885, 885, 914, 949, 949};
                button_run_y = new int[]{798, 794, 732, 674, 732, 794};
                button_run_center_x = clock_square.x + inRel(r_w, 916);
                button_run_center_y = clock_square.y + inRel(r_w, 785); // was 730                
                for (int i = 0; i < button_gps_x.length; i++) {
                    button_gps_x[i] = clock_square.x + inRel(r_w, button_gps_x[i]);
                    button_gps_y[i] = clock_square.y + inRel(r_h, button_gps_y[i]);
                    button_run_x[i] = clock_square.x + inRel(r_w, button_run_x[i]);
                    button_run_y[i] = clock_square.y + inRel(r_h, button_run_y[i]);
                }
                
                if (digital_clock_style) {
                	button_gps = new Polygon(button_gps_x, button_gps_y, button_gps_x.length);
                	button_run = new Polygon(button_run_x, button_run_y, button_run_x.length);
                	int btn_area_x=small_button_size_x*2;
                	int btn_area_xl=small_button_size_x*270/120;
                	int btn_delta=small_button_size_x/2;
                	int sel_delta=large_button_size_x*3/5;
                	buttons[BUTTON_RESET] = new Rectangle(clock_square.x+button_reset_x-btn_delta, clock_square.y+button_reset_y-btn_delta, btn_area_x, btn_area_x);
                	buttons[BUTTON_CHR] = new Rectangle(clock_square.x+button_chr_x-btn_delta, clock_square.y+button_chr_y-btn_delta, btn_area_x, btn_area_x);
                	buttons[BUTTON_DATE] = new Rectangle(clock_square.x+button_date_x-btn_delta, clock_square.y+button_date_y-btn_delta, btn_area_xl, btn_area_xl);
                	buttons[BUTTON_GPS] = new Rectangle(button_gps_x[0]-sel_delta, button_gps_y[0]-sel_delta*2, btn_area_x, btn_area_x);
                	buttons[BUTTON_RUN] = new Rectangle(button_run_x[0]-sel_delta, button_run_y[0]-sel_delta*2, btn_area_x, btn_area_x);
                } else {
                	int btn_dr=clock_r*83/100;
                	small_button_size_x=clock_r*17/100;
                	int btn_hsz=small_button_size_x;
                	int btn_area_x=small_button_size_x*2;
                	int btn_ref_x = clock_square.x + clock_square.width/2 - btn_hsz;
                	int btn_ref_y = clock_square.y + clock_square.height/2 - btn_hsz;
                	buttons[BUTTON_RESET] = new Rectangle(btn_ref_x + btn_dr , btn_ref_y + btn_dr, btn_area_x,btn_area_x);
                	buttons[BUTTON_CHR] = new Rectangle(btn_ref_x - btn_dr , btn_ref_y - btn_dr, btn_area_x,btn_area_x);
                	buttons[BUTTON_DATE] = new Rectangle(btn_ref_x + btn_dr , btn_ref_y - btn_dr, btn_area_x,btn_area_x);
                	buttons[BUTTON_GPS] = new Rectangle(btn_ref_x, btn_ref_y, btn_area_x,btn_area_x);
                	buttons[BUTTON_RUN] = new Rectangle(btn_ref_x - btn_dr, btn_ref_y + btn_dr, btn_area_x,btn_area_x);               	
                }
                
            	button_mvt_x = inRel(r_w, 40);
            }

            if (analog_clock_style) {
                background_img = createAnalogDial(clock_square.width/2, clock_square.height/2, clock_r);
            } else {
            	background_img = createDigitalDial();            	
            }
            
            button_round_img = createRoundButton(Color.decode("#D8D8D8"));
            button_selector_img = createRoundButton(Color.decode("#D8D8D8"));            
            
            logger.finest("ClockGraphicsConfig updated");

        }

    }

    private BufferedImage createRoundButton(Color button_color) {

    	BufferedImage button_img = new BufferedImage(small_button_size_x,small_button_size_y,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_btn = button_img.createGraphics();
    	g_btn.setRenderingHints(rendering_hints);
    	g_btn.setColor(button_color);
    	g_btn.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));   	
    	Shape button_shape = new Ellipse2D.Float(0, 0, small_button_size_x, small_button_size_y);
    	g_btn.fill(button_shape);
    	
    	return button_img;
    }
    
    /**
     * createAnalogDial creates a BufferedImage containing fix graphic elements
     * for the analog clock. The BufferedImage is the clock background.    
     */
    private BufferedImage createAnalogDial(int x, int y, int r) {
    	
    	BufferedImage dial_img = new BufferedImage(clock_square.width,clock_square.width,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_dial = dial_img.createGraphics();
    	g_dial.setRenderingHints(rendering_hints);
    	
    	g_dial.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));   
    	
        int cl_i = clock_square.width*21/64;
        int cl_m = clock_square.width*23/64;
        int cl_o = clock_square.width*25/64;

    	g_dial.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    	
    	// g2.setColor(ColorUtilities.multiply(Color.decode("#54616E"), aircraft.get_cockpit_light_color()));
    	g_dial.setColor(clock_digital_54616E_color);
    	g_dial.fillPolygon(frame_x, frame_y, frame_x.length);
    	// g2.setColor(ColorUtilities.multiply(Color.decode("#54616E").darker().darker(), aircraft.get_cockpit_light_color()));
    	g_dial.setColor(clock_digital_54616E_dark_color);
    	g_dial.drawPolygon(frame_x, frame_y, frame_x.length);
        
        g_dial.setColor(knobs_color);
        g_dial.fillOval(x-r, y-r, 2*r, 2*r);

        g_dial.setColor(background_color);
        g_dial.fillOval(x-r*280/300, y-r*280/300, 2*r*280/300, 2*r*280/300);

        AffineTransform original_at = g_dial.getTransform();
        Stroke original_stroke = g_dial.getStroke();

        g_dial.setColor(dim_markings_color);
        //g2.setStroke(new BasicStroke(2.0f * clock_gc.scaling_factor));
        for ( int i=0; i<15; i++ ) {
        	g_dial.rotate(Math.toRadians(i*6), x, y);
            if ( i % 5 == 0 ) {
            	g_dial.setStroke(new BasicStroke(4.0f * scaling_factor));
            	g_dial.drawLine(x, y - cl_i, x, y - cl_o);
            	g_dial.drawLine(x, y + cl_i, x, y + cl_o);
            	g_dial.drawLine(x - cl_i, y, x - cl_o, y);
            	g_dial.drawLine(x + cl_i, y, x + cl_o, y);
            	g_dial.setStroke(new BasicStroke(2.0f * scaling_factor));
            } else {
            	g_dial.drawLine(x, y - cl_i, x, y - cl_m);
            	g_dial.drawLine(x, y + cl_i, x, y + cl_m);
            	g_dial.drawLine(x - cl_i, y, x - cl_m, y);
            	g_dial.drawLine(x + cl_i, y, x + cl_m, y);
            }
            g_dial.setTransform(original_at);
        }
        g_dial.setStroke(original_stroke);

        int d_r = clock_square.width*18/64;
        int d_rx = d_r * 82 /100;
        int d_ry = d_r / 2;
        int d_h = line_height_xxxl/2;
        int d_f = line_height_xxxl*1/8;
        int d_w = digit_width_xxxl;
        g_dial.setFont(font_xxxl);
        g_dial.drawString("60", x - d_w, y - d_r + d_h - d_f);
        g_dial.drawString("10", x + d_rx - d_w, y - d_ry + d_h - d_f);
        g_dial.drawString("20", x + d_rx - d_w, y + d_ry + d_h - d_f);
        g_dial.drawString("30", x - d_w, y + d_r + d_h - d_f);
        g_dial.drawString("40", x - d_rx - d_w, y + d_ry + d_h - d_f);
        g_dial.drawString("50", x - d_rx - d_w, y - d_ry + d_h - d_f);
        
        /*
         * Control buttons
         */
    	g_dial.setFont(font_xl);
    	g_dial.setColor(markings_color);    	
    	g_dial.drawString("CHR", chr2_string_x, chr2_string_y);
    	g_dial.drawString("TIME/DATE", date_string_x, date_string_y);
    	g_dial.drawString("ET", et2_string_x, et2_string_y);
    	g_dial.drawString("SET", set_string_x, set_string_y);
    	g_dial.setFont(font_m);
    	g_dial.drawString("PRESS TO", int_string_x, int_string_y);
    	g_dial.drawString("HOLD TO RESET", rst_run_string_x, rst_run_string_y);
    	g_dial.setColor(Color.decode("#D8D8D8"));
    	int btn_dr=r*83/100;
    	int btn_r=r*17/100;
    	int btn_hsz=btn_r/2;    	
    	g_dial.fill( new Ellipse2D.Float(x-btn_dr-btn_hsz, y-btn_dr-btn_hsz, btn_r, btn_r));
    	g_dial.fill( new Ellipse2D.Float(x+btn_dr-btn_hsz, y-btn_dr-btn_hsz, btn_r, btn_r));
    	g_dial.fill( new Ellipse2D.Float(x-btn_dr-btn_hsz, y+btn_dr-btn_hsz, btn_r, btn_r));
    	g_dial.fill( new Ellipse2D.Float(x+btn_dr-btn_hsz, y+btn_dr-btn_hsz, btn_r, btn_r));
        return dial_img;
    }


    private BufferedImage createDigitalDial() {
    	
    	BufferedImage dial_img = new BufferedImage(clock_square.width,clock_square.width,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_dial = dial_img.createGraphics();
    	g_dial.setRenderingHints(rendering_hints);
    	
    	g_dial.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    	
    	// g2.setColor(ColorUtilities.multiply(Color.decode("#54616E"), aircraft.get_cockpit_light_color()));
    	g_dial.setColor(clock_digital_54616E_color);
    	g_dial.fillPolygon(frame_x, frame_y, frame_x.length);
    	// g2.setColor(ColorUtilities.multiply(Color.decode("#54616E").darker().darker(), aircraft.get_cockpit_light_color()));
    	g_dial.setColor(clock_digital_54616E_dark_color);
    	g_dial.drawPolygon(frame_x, frame_y, frame_x.length);

    	// g2.setColor(ColorUtilities.multiply(Color.decode("#080808"), aircraft.get_cockpit_light_color()));
    	g_dial.setColor(clock_digital_dark_gray_color);

    	g_dial.fillRoundRect(background_x, background_y, background_width, background_height, background_corner_size, background_corner_size);
    	g_dial.setColor(Color.BLACK);
    	g_dial.fillRoundRect(chr_background_x, chr_background_y, chr_background_width, chr_background_height, times_background_corner_size, times_background_corner_size);
    	g_dial.fillRoundRect(utc_background_x, utc_background_y, utc_background_width, utc_background_height, times_background_corner_size, times_background_corner_size);
    	g_dial.fillRoundRect(et_background_x, et_background_y, et_background_width, et_background_height, times_background_corner_size, times_background_corner_size);

    	g_dial.setColor(Color.RED);
    	
    	g_dial.setStroke(new BasicStroke(1));
    	g_dial.drawRoundRect(chr_background_x, chr_background_y, chr_background_width, chr_background_height, times_background_corner_size, times_background_corner_size);
    	g_dial.drawRoundRect(utc_background_x, utc_background_y, utc_background_width, utc_background_height, times_background_corner_size, times_background_corner_size);
    	g_dial.drawRoundRect(et_background_x, et_background_y, et_background_width, et_background_height, times_background_corner_size, times_background_corner_size);

    	g_dial.setFont(font_label_bold);
    	// g2.setColor(clock_gc.getInstumentMarkerColor(aircraft.get_cockpit_light_color()));
    	g_dial.setColor(markings_color);
    	g_dial.drawString("RST", rst_string_x, rst_string_y);
    	g_dial.drawString("CHR", chr_string_x, chr_string_y);
    	g_dial.drawString("DATE", date_string_x, date_string_y);
    	g_dial.drawString("GPS", gps_string_x, gps_string_y);
    	g_dial.drawString("RUN", run_string_x, run_string_y);
    	g_dial.setColor(Color.WHITE);
    	g_dial.setFont(font_label);
    	g_dial.drawString("CHR", chr2_string_x, chr2_string_y);
    	g_dial.drawString("UTC", utc_string_x, utc_string_y);
    	g_dial.drawString("ET", et_string_x, et_string_y);
    	g_dial.setFont(font_label_small_condensed);
    	g_dial.drawString("INT", int_string_x, int_string_y);
    	g_dial.drawString("SET", set_string_x, set_string_y);
    	g_dial.drawString("STP", stp_string_x, stp_string_y);
    	g_dial.drawString("RST", rst_run_string_x, rst_run_string_y);
    	g_dial.setFont(font_label_small);
    	g_dial.setColor(Color.decode("#009EDE"));
    	g_dial.drawString("MIN", min_string_x, min_string_y);
    	g_dial.drawString("SEC", sec_string_x, sec_string_y);
    	g_dial.drawString("HR/MO", hr_mo_string_x, hr_mo_string_y);
    	g_dial.drawString("MIN/DY", min_dy_string_x, min_dy_string_y);
    	g_dial.drawString("SEC/Y", sec_y_string_x, sec_y_string_y);
    	g_dial.drawString("HR", hr_string_x, hr_string_y);
    	g_dial.drawString("MIN", min2_string_x, min2_string_y);

    	g_dial.setColor(Color.decode("#D8D8D8"));
    	g_dial.fill( new Ellipse2D.Float(button_reset_x, button_reset_y, small_button_size_x, small_button_size_y));
    	g_dial.fill( new Ellipse2D.Float(button_chr_x, button_chr_y, small_button_size_x, small_button_size_y));
    	g_dial.fill( new Ellipse2D.Float(button_date_x, button_date_y, large_button_size_x, large_button_size_y));
    	
        return dial_img;
    }

    
    private static int inRel(double relation, int coordinate) {
        return (int) (relation * coordinate);
    }

    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.frame_size = event.getComponent().getSize();
        this.resized = true;
    }

    public void componentMoved(ComponentEvent event) {
        this.component_topleft = event.getComponent().getLocation();
    }

    public void componentShown(ComponentEvent arg0) {
    }

    public void componentHidden(ComponentEvent arg0) {
    }


}
