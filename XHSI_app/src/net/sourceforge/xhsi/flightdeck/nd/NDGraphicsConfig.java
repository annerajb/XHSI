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
package net.sourceforge.xhsi.flightdeck.nd;

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


public class NDGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static int INITIAL_EXPANDED_PLANE_Y_OFFSET = 100;
    public static int INITIAL_CENTER_BOTTOM = 55;
    
    public static long change_msg_duration = 1000; // Message display 1.0s

    public boolean airbus_style;
    public boolean boeing_style;
    
    public int left_label_x;
    public int left_label_arpt_y;
    public int left_label_wpt_y;
    public int left_label_vor_y;
    public int left_label_ndb_y;
    public int left_label_pos_y;
    public int left_label_tfc_y;
    public int left_label_data_y;
    public int left_label_taonly_y;
    public int left_label_xpdr_y;
    public int right_label_x;
    public int right_label_tcas_y;
    public int right_label_disagree_y;

    public int rose_radius;
    public int rose_thickness;
    public int rose_y_offset;
    public int plane_y_offset;
    public int map_center_x;
    public int map_center_y;

    public float pixels_per_nm;

    public int pixel_distance_plane_bottom_screen;
    public int pixel_distance_plane_lower_left_corner;
    public float half_view_angle;
    public int big_tick_length;
    public int medium_tick_length;
    public int small_tick_length;
    public int tick_text_y_offset;
    public Area inner_rose_area;
    public int sixty_deg_hlimit;

    private int map_mode;
    private int map_submode;
    
    private long map_mode_timestamp;
    private long map_submode_timestamp;
    private long map_range_timestamp;
    private long map_zoomin_timestamp;

    public boolean mode_app;
    public boolean mode_vor;
    public boolean hdg_up;
    public boolean mode_classic_hsi;
    public boolean mode_map;
    public boolean mode_fullmap;
    public boolean mode_centered;
    public boolean mode_plan;
    public boolean trk_up;
    public int map_range;
    public boolean map_zoomin;

    public Font compass_text_font;
    public Font compass_small_text_font;
    public int compass_two_digit_hdg_text_width = 0;
    public int compass_one_digit_hdg_text_width = 0;
    public int compass_two_digit_hdg_small_text_width = 0;
    public int compass_one_digit_hdg_small_text_width = 0;
    public int compass_hdg_text_height = 0;
    public int compass_hdg_small_text_height = 0;
    
    public int arrow_length;
    public int arrow_base_width;
    
    public float max_range;

    public int range_mode_message_y;

    public NDGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
        map_mode_timestamp=0;
        map_submode_timestamp=0;
        map_range_timestamp=0;
        map_zoomin_timestamp=0;
    }


//    public void init() {
//
//        super.init();
//
//    }


    public void update_config(Graphics2D g2, int mode, int submode, int range, boolean zoomin, boolean power, int instrument_style) {

        if (this.resized
                || this.reconfig
                || (this.map_mode != mode)
                || (this.map_submode != submode)
                || (this.map_range != range)
                || (this.map_zoomin != zoomin)
                || (this.powered != power)
                || (this.style != instrument_style)
            ) {
            // one of the settings has been changed

            // remember the new settings
            this.powered = power;
            this.style = instrument_style;
            
//logger.warning("ND update_config");
            super.update_config(g2);
            
            // Timestamp mode/submode/range settings
            if (this.map_mode != mode) map_mode_timestamp = System.currentTimeMillis();            	
            if (this.map_submode != submode) map_submode_timestamp = System.currentTimeMillis();
            if (this.map_range != range) map_range_timestamp = System.currentTimeMillis();
            if (this.map_zoomin != zoomin) map_zoomin_timestamp = System.currentTimeMillis();

            // remember the mode/submode/range settings
            this.map_mode = mode;
            this.map_submode = submode;
            this.map_range = range;
            this.map_zoomin = zoomin;

            // remember instrument style settings
            airbus_style = instrument_style == Avionics.STYLE_AIRBUS;
            boeing_style = instrument_style == Avionics.STYLE_BOEING;
            	
            // Fonts and graphics settings for compass rose
            if (boeing_style) {
                compass_text_font=this.font_m;
                compass_small_text_font=this.font_s;
            } else {
                compass_text_font=this.font_xxl;
                compass_small_text_font=this.font_l;
            }
            // calculate text widths and heights
            compass_two_digit_hdg_text_width = (int) this.get_text_width(g2, this.compass_text_font, "33");
            compass_one_digit_hdg_text_width = (int) this.get_text_width(g2, this.compass_text_font, "8");
            compass_two_digit_hdg_small_text_width = (int) this.get_text_width(g2, this.compass_small_text_font, "33");
            compass_one_digit_hdg_small_text_width = (int) this.get_text_width(g2, this.compass_small_text_font, "8");
            compass_hdg_text_height = (int) (this.get_text_height(g2, this.compass_text_font)*0.8f);
            compass_hdg_small_text_height = (int) (this.get_text_height(g2, this.compass_small_text_font)*0.8f);
            
            
            // set some booleans for easy checking
            if ( preferences.get_airbus_modes() ) {
                // limit to Airbus display modes
                mode_plan = (submode == Avionics.EFIS_MAP_PLN);
                mode_app = (submode == Avionics.EFIS_MAP_APP);
                mode_vor = (submode == Avionics.EFIS_MAP_VOR);
                mode_classic_hsi = ( (mode_app || mode_vor) && preferences.get_classic_hsi() );
                //mode_classic_hsi = false;
                mode_map = ( (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) || ! preferences.get_classic_hsi() );
                //mode_map = true;
                mode_fullmap = preferences.get_appvor_fullmap() || (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) || mode_plan;
                mode_centered = ( mode_app || mode_vor || (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_PLN) );
            } else {
                mode_plan = (submode == Avionics.EFIS_MAP_PLN);
                mode_app = (submode == Avionics.EFIS_MAP_APP);
                mode_vor = (submode == Avionics.EFIS_MAP_VOR);
                mode_classic_hsi = ( (mode_app || mode_vor) && (mode == Avionics.EFIS_MAP_CENTERED) && preferences.get_classic_hsi() );
                // mode_map = ( ( (submode == Avionics.EFIS_MAP_APP) || (submode == Avionics.EFIS_MAP_VOR) || (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) ) && ! mode_classic_hsi);
                mode_map = ( ! mode_classic_hsi ) && ( ! mode_plan );
                mode_fullmap = ( ( ( (submode == Avionics.EFIS_MAP_MAP) || (submode == Avionics.EFIS_MAP_NAV) ) && ! mode_classic_hsi) || mode_plan );
                if ( mode_map && preferences.get_appvor_fullmap() ) {
                    // APP and VOR are normally not fullmap, this is an override
                    mode_fullmap = true;
                }
                // mode_centered = ( ( (mode_app || mode_vor) && (mode == Avionics.EFIS_MAP_CENTERED) ) || ( (submode == Avionics.EFIS_MAP_MAP) && (mode == Avionics.EFIS_MAP_CENTERED) ) || (submode == Avionics.EFIS_MAP_PLN) );
                mode_centered = ( (mode == Avionics.EFIS_MAP_CENTERED) || (submode == Avionics.EFIS_MAP_PLN) );
                // for NAV submode, invert the centered/expanded
                if ( submode == Avionics.EFIS_MAP_NAV ) mode_centered = ! mode_centered;
            }
            hdg_up = mode_app || mode_vor;
            trk_up = ! ( hdg_up || mode_plan );


            // position of the plane and size of the rose
            this.map_center_x = this.frame_size.width / 2;
//            this.rose_y_offset = 50 + 4 + this.border_top;
            this.rose_y_offset = (airbus_style ? 5 : 3) * this.line_height_m + this.border_top;
            //if ( ( (this.map_mode == Avionics.EFIS_MAP_CENTERED) && (this.map_submode != Avionics.EFIS_MAP_NAV) ) || (this.map_submode == Avionics.EFIS_MAP_PLN) ) {
            if ( this.mode_centered || this.mode_plan ) {
                // CENTERED (or PLAN)
                this.max_range = (float)this.map_range / 2.0f;
                this.map_center_y = (this.rose_y_offset + (this.frame_size.height  - this.border_bottom - INITIAL_CENTER_BOTTOM)) / 2;
                this.rose_radius = this.map_center_y - this.rose_y_offset;
                this.plane_y_offset = INITIAL_CENTER_BOTTOM + this.rose_radius;
            } else {
                // EXPANDED
                this.max_range = (float)this.map_range;
                this.plane_y_offset = (int) (INITIAL_EXPANDED_PLANE_Y_OFFSET * this.scaling_factor);
                this.map_center_y = this.frame_size.height - this.border_bottom - this.plane_y_offset;
                this.rose_radius = this.map_center_y - this.rose_y_offset;
            }
            this.pixels_per_nm = (float)this.rose_radius / this.max_range; // float for better precision
            if ( zoomin ) this.pixels_per_nm *= 100.0f;


            // labels at the left
            this.left_label_x = this.border_left + Math.round(10.0f * this.scaling_factor);
            //this.left_label_arpt_y = Math.max( this.frame_size.height - this.border_bottom - 240, this.frame_size.height/2 + 24 );
            this.left_label_arpt_y = this.frame_size.height*6/10;
            this.left_label_wpt_y = this.left_label_arpt_y + this.line_height_xs*10/8;
            this.left_label_vor_y = this.left_label_wpt_y + this.line_height_xs*10/8;
            this.left_label_ndb_y = this.left_label_vor_y + this.line_height_xs*10/8;
            this.left_label_pos_y = this.left_label_ndb_y + this.line_height_xs*10/8;
            this.left_label_data_y = this.left_label_pos_y + this.line_height_xs*10/8;
            this.left_label_tfc_y = this.left_label_data_y + this.line_height_xs*10/8;
            this.left_label_taonly_y = this.left_label_tfc_y + this.line_height_xxs*10/8;
            this.left_label_xpdr_y = this.left_label_taonly_y + this.line_height_xxs*10/8;

            // labels at the right
            this.right_label_x = this.frame_size.width - this.border_right - Math.round(20.0f * this.scaling_factor);
            this.right_label_tcas_y = this.frame_size.height * 7 / 16;
            this.right_label_disagree_y = this.frame_size.height / 3;

            // calculate pixel distances. Needed for determining which
            // part of the rose needs to be drawn
            pixel_distance_plane_bottom_screen = this.frame_size.height - this.map_center_y ;
            pixel_distance_plane_lower_left_corner =
                    (int) Math.sqrt(
                    Math.pow(this.pixel_distance_plane_bottom_screen, 2) +
                    Math.pow(this.frame_size.width / 2, 2));

            
            // compass rose ticks get shorter when the frame is smaller than 600px
            if (boeing_style) {
            	this.big_tick_length = (int) (20 * shrink_scaling_factor);
            	this.medium_tick_length = this.big_tick_length / 3;
            	this.small_tick_length = this.big_tick_length / 3;
            	this.tick_text_y_offset = rose_y_offset + big_tick_length + compass_hdg_text_height;
            } else {
            	this.big_tick_length = (int) (-20 * shrink_scaling_factor);
            	this.medium_tick_length = this.big_tick_length / 2;
            	this.small_tick_length = this.big_tick_length / 3;
            	this.tick_text_y_offset = rose_y_offset + big_tick_length*10/8;
            }

            // NDB/VOR arrows
            if (boeing_style) {
            	arrow_length = (int) Math.min(60, shrink_scaling_factor * 60);
            	arrow_base_width = (int) Math.min(25, shrink_scaling_factor * 25);
            } else {
            	arrow_length = mode_centered ? rose_radius/2 : rose_radius/4;
            	arrow_base_width = (int) Math.min(25, shrink_scaling_factor * 25);           	
            }
            	
            
            // what is all this about?
            //if (this.pixel_distance_plane_bottom_screen >= (this.rose_radius - this.big_tick_length)) {
            //    // Complete rose
            //    this.half_view_angle = 180.0f;
            //} else if (this.pixel_distance_plane_lower_left_corner > (this.rose_radius - this.big_tick_length)) {
            //    // Rose visible below aircraft position
            //    half_view_angle = (float) (180.0f - Math.toDegrees(Math.acos((1.0f * pixel_distance_plane_bottom_screen) / (1.0f * (this.rose_radius - this.big_tick_length)))));
            //} else {
            //    // Rose visible only above aircraft position
            //    half_view_angle = (float) (90.0f - Math.toDegrees(Math.acos((1.0f * this.frame_size.width) / (2.0f * (this.rose_radius - this.big_tick_length)))));
            //}
            // let's keep it simple!
            half_view_angle = 90.0f;

            rose_thickness = 2;
            this.inner_rose_area = new Area(new Ellipse2D.Float(
                    map_center_x - rose_radius + rose_thickness,
                    map_center_y - rose_radius + rose_thickness,
                    (rose_radius * 2) - (rose_thickness * 2),
                    (rose_radius * 2) - (rose_thickness * 2)));
            this.sixty_deg_hlimit = (int)(Math.sin(Math.PI/3.0) * rose_radius);

            // Range and Mode change message
            range_mode_message_y = this.frame_size.height*38/100;

            // clear the flags
            this.resized = false;
            this.reconfig = false;
            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

        }

    }

    public boolean display_range_change_msg() {
    	boolean range_msg = (map_range_timestamp+change_msg_duration) > System.currentTimeMillis();
    	boolean zoomin_msg = (map_zoomin_timestamp+change_msg_duration) > System.currentTimeMillis();
    	return range_msg || zoomin_msg;
    }
    
    public boolean display_mode_change_msg() {
    	boolean mode_msg = (map_mode_timestamp+change_msg_duration) > System.currentTimeMillis();
    	boolean submode_msg = (map_submode_timestamp+change_msg_duration) > System.currentTimeMillis();
    	return mode_msg || submode_msg;
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
