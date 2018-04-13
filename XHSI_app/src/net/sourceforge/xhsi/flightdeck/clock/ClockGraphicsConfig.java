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

import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;
import net.sourceforge.xhsi.model.Avionics;


public class ClockGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public Rectangle clock_square;
    public Rectangle clock_rect;
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
    int button_run_x[];
    int button_run_y[];
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
    int et_string_x;
    int et_string_y;
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
    
    public ClockGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
        digital_clock_style=false;
        this.clock_style = -1;
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

            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

            int square_size;
            square_size = Math.min(panel_rect.width, panel_rect.height)/2;
            clock_square = new Rectangle(
                    panel_rect.x + panel_rect.width/2 - square_size,
                    panel_rect.y + panel_rect.height/2 - square_size,
                    2*square_size,
                    2*square_size
                );

            clock_gradient = new GradientPaint(
                    0, 0, frontpanel_color.brighter().brighter(),
                    clock_square.width, clock_square.height , frontpanel_color.darker().darker(),
                    false);
            
            if (analog_clock_style) {
    
                square_size = Math.min(panel_rect.width, panel_rect.height) / 2;
                clock_rect = new Rectangle(
                        panel_rect.x + panel_rect.width / 2 - square_size,
                        panel_rect.y + panel_rect.height / 2 - square_size,
                        2 * square_size,
                        2 * square_size
                );
                
            } else { // digital_clock_style
                int clusterWidth;
                int clusterHeight;
                if (panel_rect.width / 1064.0 < panel_rect.height / 994.0) {
                    clusterWidth = panel_rect.width;
                    clusterHeight = (int) (panel_rect.width * 994.0 / 1064.0);
                    clock_rect = new Rectangle(
                            panel_rect.x,
                            panel_rect.y,
                            clusterWidth,
                            clusterHeight
                    );
                } else {
                    clusterWidth = (int) ((panel_rect.height) * 1064.0 / 994.0);
                    clusterHeight = (panel_rect.height);
                    clock_rect = new Rectangle(
                            panel_rect.x,
                            panel_rect.y,
                            clusterWidth,
                            clusterHeight
                    );
                }
                
                // TODO: move font configuraion to global GraphicConfig
                font_header = new Font("Arial", Font.BOLD, (int) ((clock_rect.width / 1064.0) * 18));
                font_label_bold = new Font("Arial", Font.BOLD, (int) ((clock_rect.width / 1064.0) * 40));
                font_label = new Font("Arial", Font.PLAIN, (int) ((clock_rect.width / 1064.0) * 45));
                font_label_small_condensed = new Font("S1 F16 Panel Font V1.1", Font.PLAIN, (int) ((clock_rect.width / 1064.0) * 36));
                if (font_label_small_condensed.getFontName().equalsIgnoreCase("Dialog.plain")) {
                    font_label_small_condensed = new Font("Arial", Font.PLAIN, (int) ((clock_rect.width / 1064.0) * 26));
                }
                /*
                 * DSEG7 Classic is available from :
                 * http://fontsforweb.com/font/generatezip/?id=79190
                 */
                font_label_small = new Font("Arial", Font.PLAIN, (int) ((clock_rect.width / 1064.0) * 32));
                font_seven_seg_large = new Font("DSEG7 Classic", Font.ITALIC, (int) ((clusterWidth / 1064.0) * 90));
                font_seven_seg = new Font("DSEG7 Classic", Font.ITALIC, (int) ((clusterWidth / 1064.0) * 60));
            }

            clock_gradient = new GradientPaint(
                    0, 0, frontpanel_color.brighter().brighter(),
                    clock_rect.width, clock_rect.height, frontpanel_color.darker().darker(),
                    false);

            if (resized || clock_style_changed || !size_set) {
                double r_w = clock_rect.width / 1064.0;
                double r_h = clock_rect.height / 994.0;
                utc_time_string_x = inRel(r_w, 289);
                utc_time_string_y = inRel(r_h, 560);
                utc_second_string_x = inRel(r_w, 642);
                utc_second_string_y = inRel(r_h, 547);
                small_button_size_x = inRel(r_h, 90);
                small_button_size_y = inRel(r_h, 90);
                large_button_size_x = inRel(r_h, 120);
                large_button_size_y = inRel(r_h, 120);
                timer_string_x = inRel(r_w, 375);
                timer_string_y = inRel(r_h, 295);
                button_reset_x = inRel(r_w, 105);
                button_reset_y = inRel(r_h, 220);
                button_chr_x = inRel(r_w, 865);
                button_chr_y = inRel(r_h, 217);
                button_date_x = inRel(r_w, 90);
                button_date_y = inRel(r_h, 453);
                button_gps_x = new int[]{914, 885, 885, 914, 949, 949};
                button_gps_y = new int[]{558, 554, 492, 434, 492, 554};
                button_run_x = new int[]{914, 885, 885, 914, 949, 949};
                button_run_y = new int[]{798, 794, 732, 674, 732, 794};
                for (int i = 0; i < button_gps_x.length; i++) {
                    button_gps_x[i] = inRel(r_w, button_gps_x[i]);
                    button_gps_y[i] = inRel(r_h, button_gps_y[i]);
                    button_run_x[i] = inRel(r_w, button_run_x[i]);
                    button_run_y[i] = inRel(r_h, button_run_y[i]);
                }
                elapsed_time_string_x = inRel(r_w, 375);
                elapsed_time_string_y = inRel(r_h, 797);
                frame_x = new int[]{182, 883, 1009, 1009, 883, 182, 53, 53};
                frame_y = new int[]{47, 47, 176, 815, 945, 945, 815, 176};
                for (int i = 0; i < frame_x.length; i++) {
                    frame_x[i] = inRel(r_w, frame_x[i]);
                    frame_y[i] = inRel(r_h, frame_y[i]);
                }
                background_x = inRel(r_w, 231);
                background_y = inRel(r_h, 99);
                background_width = inRel(r_w, 608);
                background_height = inRel(r_h, 803);
                background_corner_size = inRel(r_w, 30);
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
                chr_string_x = inRel(r_w, 870);
                chr_string_y = inRel(r_h, 209);
                date_string_x = inRel(r_w, 100);
                date_string_y = inRel(r_h, 632);
                gps_string_x = inRel(r_w, 885);
                gps_string_y = inRel(r_h, 414);
                int_string_x = inRel(r_w, 961);
                int_string_y = inRel(r_h, 460);
                set_string_x = inRel(r_w, 961);
                set_string_y = inRel(r_h, 517);
                run_string_x = inRel(r_w, 883);
                run_string_y = inRel(r_h, 653);
                stp_string_x = inRel(r_w, 961);
                stp_string_y = inRel(r_h, 696);
                rst_run_string_x = inRel(r_w, 961);
                rst_run_string_y = inRel(r_h, 753);
                chr2_string_x = inRel(r_w, 486);
                chr2_string_y = inRel(r_h, 146);
                utc_string_x = inRel(r_w, 486);
                utc_string_y = inRel(r_h, 411);
                et_string_x = inRel(r_w, 508);
                et_string_y = inRel(r_h, 879);
                min_string_x = inRel(r_w, 397);
                min_string_y = inRel(r_h, 372);
                sec_string_x = inRel(r_w, 590);
                sec_string_y = inRel(r_h, 372);
                hr_mo_string_x = inRel(r_w, 289);
                hr_mo_string_y = inRel(r_h, 629);
                min_dy_string_x = inRel(r_w, 447);
                min_dy_string_y = inRel(r_h, 629);
                sec_y_string_x = inRel(r_w, 626);
                sec_y_string_y = inRel(r_h, 629);
                hr_string_x = inRel(r_w, 397);
                hr_string_y = inRel(r_h, 846);
                min2_string_x = inRel(r_w, 590);
                min2_string_y = inRel(r_h, 846);
            }
            
            
            logger.finest("ClockGraphicsConfig updated");

        }

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
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
