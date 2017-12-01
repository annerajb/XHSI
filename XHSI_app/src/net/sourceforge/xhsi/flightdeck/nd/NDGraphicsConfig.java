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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
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
    public int left_label_terrain_y;
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
    public float pixels_per_deg;

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
    
    // Compass Rose 
    public BufferedImage compass_rose_img;
    public Font compass_text_font;
    public Font compass_small_text_font;
    public int compass_two_digit_hdg_text_width = 0;
    public int compass_one_digit_hdg_text_width = 0;
    public int compass_two_digit_hdg_small_text_width = 0;
    public int compass_one_digit_hdg_small_text_width = 0;
    public int compass_hdg_text_height = 0;
    public int compass_hdg_small_text_height = 0;
    // Range labels
    public Font range_label_font;
    public int range_label_half_y;
    public int range_label_half_x;
    public int range_label_full_y;
    public int range_label_full_x;
    // Cardinal winds
    public Font cardinal_labels_font;
    public int cardinal_vert_x;
    public int cardinal_N_y;
    public int cardinal_S_y;
    public int cardinal_horiz_y;
    public int cardinal_E_x;
    public int cardinal_W_x;
    public Polygon cardinal_tri_N;
    public Polygon cardinal_tri_S;
    public Polygon cardinal_tri_E;
    public Polygon cardinal_tri_W;
    
    // Moving Map Symbols
    public Font navaid_font;
    public BufferedImage fix_awy_symbol_img;
    public BufferedImage fix_term_symbol_img;
    public int fix_shift_x;
    public int fix_shift_y;
    public int fix_name_x;
    public int fix_name_y;
    public BufferedImage ndb_symbol_img;
    public BufferedImage dme_symbol_img;
    public BufferedImage vor_symbol_img;
    public BufferedImage vordme_symbol_img;
    public BufferedImage loc_symbol_img;
    public BufferedImage airport_symbol_img;
    
    // RadioLabel & Radio info box -> rib prefix
    public int rib_width;
    public int rib_line_1;
    public int rib_line_2;
    public int rib_line_3;
    public int rib_line_4;
    public int rib_height;
    public int radio1_text_x;
    public int radio2_text_x;
    public int radio1_arrow_x;
    public int radio2_arrow_x;
    public int radio1_box_x;
    public int radio2_box_x;
    public int radio_box_y;
    public BufferedImage left_radio_box_img;
    public BufferedImage right_radio_box_img;

    // Clock - Time - Chrono
    public Font clock_font;
    public int clock_time_x;
    public int clock_time_y;
    public int clock_box_x;
    public int clock_box_y;
    public int clock_box_w;
    public int clock_box_h;
    public int chrono_time_x;
    public int chrono_time_y;
    public int chrono_box_x;
    public int chrono_box_y;
    public int chrono_box_w;
    public int chrono_box_h;
    public BufferedImage clock_img;
    public BufferedImage chrono_img;
    
    // Speed Labels (and wind arrow)
    public int sl_line_height;
    public int sl_gs_label_x;
    public int sl_gs_x;
    public int sl_tas_label_x;
    public int sl_tas_x;
    public int sl_speeds_y;    
    public int sl_wind_x;
    public int sl_wind_y;
    public int sl_wind_dir_arrow_length;
    public int sl_arrow_head;
    public int sl_wind_dir_arrow_cx;
    public int sl_wind_dir_arrow_cy;
    public int sl_box_x;
    public int sl_box_y;
    public int sl_box_h;
    public int sl_box_w;
    public BufferedImage sl_img;
    
    public int arrow_length;
    public int arrow_base_width;
    
    public float max_range;

    public int range_mode_message_y;
    
    // Terrain    
    public TexturePaint terrain_tp_hd_red;
    public TexturePaint terrain_tp_hd_yellow;
    public TexturePaint terrain_tp_md_yellow;
    public TexturePaint terrain_tp_ld_yellow;
    public TexturePaint terrain_tp_solid_green;
    public TexturePaint terrain_tp_hd_green;
    public TexturePaint terrain_tp_md_green;
    public TexturePaint terrain_tp_ld_green;
    public TexturePaint terrain_tp_blue;
    public TexturePaint terrain_tp_black;
    public int terr_box_x;
    public int terr_value_x;
    public int terr_max_box_y;
    public int terr_max_value_y;
    public int terr_min_box_y;
    public int terr_min_value_y;
    public int terr_box_height;
    public int terr_box_width;
    public int terr_label_y;
    public BufferedImage terr_img_1;
    public BufferedImage terr_img_2;
    public float terr_sweep_step;
    public float terr_range_multiply;
    public int terr_nb_tile_x;
    public int terr_nb_tile_y;
    public int terr_tile_width;
    public int terr_tile_height;
    public boolean terr_peaks_mode;
    
    // Weather radar
    public BufferedImage wxr_img_1;
    public BufferedImage wxr_img_2;
    public float wxr_sweep_step;
    public float wxr_range_multiply;
    public int wxr_nb_tile_x;
    public int wxr_nb_tile_y;
    public int wxr_tile_width;
    public int wxr_tile_height;


    public NDGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
        map_mode_timestamp=0;
        map_submode_timestamp=0;
        map_range_timestamp=0;
        map_zoomin_timestamp=0;
        cardinal_tri_N=new Polygon();
        cardinal_tri_S=new Polygon();
        cardinal_tri_E=new Polygon();
        cardinal_tri_W=new Polygon();
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
            
            this.terr_peaks_mode = preferences.get_preference(XHSIPreferences.PREF_TERRAIN_PEAKS_MODE).equals("true");
            
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

            // compute radio info box 
            rib_line_1 = line_height_l + line_height_l/5;
            rib_line_2 = rib_line_1 + line_height_l;
            rib_line_3 = rib_line_2 + line_height_s;
            rib_line_4 = rib_line_3 + line_height_s;
            rib_height = rib_line_4 + line_height_l/2;
            rib_width = digit_width_l * 9;
            left_radio_box_img = new BufferedImage(rib_width, rib_height, BufferedImage.TYPE_INT_ARGB);
            right_radio_box_img = new BufferedImage(rib_width, rib_height, BufferedImage.TYPE_INT_ARGB);
            if (boeing_style) {
            	radio1_text_x = digit_width_l;
            	radio2_text_x = digit_width_l * 25/10;
            	radio1_arrow_x = rib_width - digit_width_l;
            	radio2_arrow_x = digit_width_l;
            	radio1_box_x = border_left;
            	radio2_box_x = frame_size.width - border_right - rib_width;
            	radio_box_y = frame_size.height - rib_height - border_bottom;
            } else {
                radio2_text_x = digit_width_l;
                radio1_text_x = digit_width_l * 25/10;
                radio2_arrow_x = rib_width - digit_width_l;
                radio1_arrow_x = digit_width_l;
                radio1_box_x = border_left;
                radio2_box_x = frame_size.width - border_right - rib_width;
                radio_box_y = frame_size.height - rib_height - border_bottom;
            }
            
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
            this.pixels_per_deg = (float)Math.PI*this.rose_radius/180.0f;
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
            this.left_label_terrain_y = this.left_label_xpdr_y + this.line_height_xxs*10/8;

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

            // Fonts and graphics settings for compass rose
            if (boeing_style) {
                compass_text_font=this.font_m;
                compass_small_text_font=this.font_s;
                range_label_font=this.font_xs;               
                range_label_half_y = map_center_y - rose_radius/2 + line_height_xs;
                range_label_half_x = map_center_x;
                range_label_full_y = map_center_y - rose_radius + line_height_xs;
                range_label_full_x = map_center_x;
                cardinal_labels_font=this.font_s;
                cardinal_vert_x = map_center_x - max_char_advance_xs/2;
                cardinal_N_y = map_center_y - rose_radius - 10;
                cardinal_S_y = map_center_y + rose_radius + 10 + line_height_s - 3;
                cardinal_horiz_y = map_center_y + line_height_s/2;
                cardinal_E_x = map_center_x + rose_radius + 10;
                cardinal_W_x = map_center_x - rose_radius - 10 - max_char_advance_s;
                // We don't need cardinal winds triangle in Boeing style
                cardinal_tri_N.reset();
                cardinal_tri_S.reset();
                cardinal_tri_E.reset();
                cardinal_tri_W.reset();
            } else {
                compass_text_font=this.font_xxl;
                compass_small_text_font=this.font_l;
                range_label_font=this.font_l;
                cardinal_labels_font=this.font_l;
                range_label_half_y = map_center_y + rose_radius*66/200 - line_height_xs;
                range_label_half_x = map_center_x - rose_radius*66/200;
                range_label_full_y = map_center_y + rose_radius*68/100 - line_height_xs;
                range_label_full_x = map_center_x - rose_radius*68/100;
                cardinal_vert_x = map_center_x - max_char_advance_m/2;
                cardinal_N_y = map_center_y - rose_radius + line_height_l*15/8;
                cardinal_S_y = map_center_y + rose_radius - line_height_l*10/8;
                cardinal_horiz_y = map_center_y + line_height_m/2;
                cardinal_E_x = map_center_x + rose_radius - max_char_advance_l*15/8;
                cardinal_W_x = map_center_x - rose_radius + max_char_advance_l*10/8;
                int tri_dx = max_char_advance_l/2;
                int tri_dy = line_height_l/2;
                cardinal_tri_N.reset();
                cardinal_tri_N.addPoint(map_center_x, map_center_y - rose_radius);
                cardinal_tri_N.addPoint(map_center_x+tri_dx, map_center_y - rose_radius + line_height_l);
                cardinal_tri_N.addPoint(map_center_x-tri_dx, map_center_y - rose_radius + line_height_l);
                cardinal_tri_S.reset();
                cardinal_tri_S.addPoint(map_center_x, map_center_y + rose_radius);
                cardinal_tri_S.addPoint(map_center_x+tri_dx, map_center_y + rose_radius - line_height_l);
                cardinal_tri_S.addPoint(map_center_x-tri_dx, map_center_y + rose_radius - line_height_l);
                cardinal_tri_E.reset();
                cardinal_tri_E.addPoint(map_center_x + rose_radius, map_center_y);
                cardinal_tri_E.addPoint(map_center_x + rose_radius - max_char_advance_l, map_center_y - tri_dy);
                cardinal_tri_E.addPoint(map_center_x + rose_radius - max_char_advance_l, map_center_y + tri_dy);
                cardinal_tri_W.reset();
                cardinal_tri_W.addPoint(map_center_x - rose_radius, map_center_y);
                cardinal_tri_W.addPoint(map_center_x - rose_radius + max_char_advance_l, map_center_y - tri_dy);
                cardinal_tri_W.addPoint(map_center_x - rose_radius + max_char_advance_l, map_center_y + tri_dy);

            }
            
            // compute text widths and heights
            compass_two_digit_hdg_text_width = (int) this.get_text_width(g2, this.compass_text_font, "33");
            compass_one_digit_hdg_text_width = (int) this.get_text_width(g2, this.compass_text_font, "8");
            compass_two_digit_hdg_small_text_width = (int) this.get_text_width(g2, this.compass_small_text_font, "33");
            compass_one_digit_hdg_small_text_width = (int) this.get_text_width(g2, this.compass_small_text_font, "8");
            compass_hdg_text_height = (int) (this.get_text_height(g2, this.compass_text_font)*0.8f);
            compass_hdg_small_text_height = (int) (this.get_text_height(g2, this.compass_small_text_font)*0.8f);
            
            // BufferedImage to cache CompassRose SubComponenet
            compass_rose_img = new BufferedImage(this.frame_size.width,this.frame_size.height,BufferedImage.TYPE_INT_ARGB);
            
            
            // Moving Map Symbols
            navaid_font = (boeing_style ? font_s : font_l);
            fix_awy_symbol_img = create_fix_symbol(awy_wpt_color);
            fix_term_symbol_img = create_fix_symbol(term_wpt_color);
            // Shift = 2 pixels
            fix_shift_x = Math.round(5.0f*scaling_factor) - 2;
            fix_shift_y = Math.round(6.0f*scaling_factor) - 2;
            fix_name_x = Math.round((boeing_style ? 12.0f : 10.5f)*scaling_factor);
            fix_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : 0);
            
            ndb_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            dme_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            vor_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            vordme_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            loc_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            airport_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            
            
            // Clock - Time - Chrono
            if (boeing_style) {
            	clock_font = font_s;
            	
            	clock_box_x = map_center_x - digit_width_s*6;
            	clock_box_y	= panel_rect.y + panel_rect.height - line_height_s*3/2;
            	clock_box_w = digit_width_s*12;
            	clock_box_h = line_height_s*5/4;
            	clock_time_y = line_height_s;
            	clock_time_x = digit_width_s*13/2;           
            	
            	chrono_box_x = map_center_x - digit_width_s*6;
            	chrono_box_y= panel_rect.y + panel_rect.height - line_height_s*5/2;
            	chrono_box_w = digit_width_s*12;
            	chrono_box_h = line_height_s*5/4;
            	chrono_time_y = line_height_s;
            	chrono_time_x = digit_width_s*13/2;
            } else {
            	clock_font = font_xl;
      
            	clock_box_x = map_center_x - digit_width_xl*11/2;
            	clock_box_y	= panel_rect.y + panel_rect.height - line_height_xl*3/2;
            	clock_box_w = digit_width_xl*11;
            	clock_box_h = line_height_xl*2;
            	clock_time_y = line_height_xl;
            	clock_time_x = digit_width_s*16/2;   
            	
            	chrono_box_x = border_left;
            	chrono_box_y = radio_box_y - line_height_xl*5/4;
            	chrono_box_w = digit_width_xl*7;
            	chrono_box_h = line_height_xl*5/4;
            	chrono_time_y = line_height_xl;
            	chrono_time_x = digit_width_xl;
            }
            clock_img = new BufferedImage(clock_box_w,clock_box_h,BufferedImage.TYPE_INT_ARGB);
            chrono_img = new BufferedImage(chrono_box_w,chrono_box_h,BufferedImage.TYPE_INT_ARGB);
            
            // Speed Labels (and wind arrow)
            sl_line_height = line_height_l;
            sl_gs_label_x = border_left + (int)(10*scaling_factor);
            sl_gs_x = sl_gs_label_x + 2 + get_text_width(g2, font_s,"GS");
            sl_tas_label_x = sl_gs_x + digit_width_fixed_l*4; //  gs_x + nd_gc.get_text_width(g2, nd_gc.font_l, "999   "); // \u00A0 is Unicode non-breaking space
            sl_tas_x = sl_tas_label_x + 2 + get_text_width(g2, font_s,"TAS");
            sl_speeds_y = border_top + sl_line_height;
            
            sl_wind_x = sl_gs_label_x;
            sl_wind_y = border_top + sl_line_height*24/10;
            sl_wind_dir_arrow_length = Math.round(40.0f * scaling_factor);
            sl_arrow_head = Math.round(3.0f * scaling_factor);
            sl_wind_dir_arrow_cx = sl_wind_x + sl_wind_dir_arrow_length/2;
            sl_wind_dir_arrow_cy = sl_wind_y + sl_line_height*2/10 + sl_wind_dir_arrow_length*1/8 + sl_wind_dir_arrow_length/2;
            
            sl_box_x = border_left;
            sl_box_y = border_top;
            sl_box_w = border_left + digit_width_m*15;
            sl_box_h = sl_wind_y + sl_line_height*2/10;
            sl_img = new BufferedImage(sl_box_w,sl_box_h,BufferedImage.TYPE_INT_ARGB);
            
            // Terrain
            terr_img_1 = new BufferedImage(panel_rect.width,panel_rect.height,BufferedImage.TYPE_INT_ARGB);
            terr_img_2 = new BufferedImage(panel_rect.width,panel_rect.height,BufferedImage.TYPE_INT_ARGB);
            terr_sweep_step = 75.0f/(preferences.get_nd_terrain_sweep_duration()*1000); 
            switch (preferences.get_terrain_resolution()) {
            case 0: // Fine - up to pixel
            	terr_nb_tile_x = Math.min(240, panel_rect.width);
            	terr_nb_tile_y = Math.min(240, panel_rect.height);
            	break;
            case 1: // Medium
            	terr_nb_tile_x = 150;
            	terr_nb_tile_y = 150;
            	break;
            default: // Coarse - CPU saver
            	terr_nb_tile_x = 80;
            	terr_nb_tile_y = 80;
            	break;
            }
            terr_range_multiply = this.preferences.get_draw_only_inside_rose() ? 1.0f : 1.5f;
            terr_tile_width = 2+(int)(frame_size.width*terr_range_multiply*1.5f/terr_nb_tile_x);
            terr_tile_height = 2+(int)(frame_size.height*terr_range_multiply*1.5f/terr_nb_tile_y);            		

            // min and max boxes, label, egpws message
            
            if (boeing_style) {
            	terr_label_y = left_label_xpdr_y + line_height_xxs*10/8;
            	terr_box_x = this.border_left + Math.round(8.0f * this.scaling_factor);
            	terr_value_x = this.border_left + Math.round(10.0f * this.scaling_factor);
            	terr_max_box_y = terr_label_y + line_height_xs*10/8;
            	terr_max_value_y = terr_max_box_y + line_height_xs;
            	terr_min_box_y = terr_max_box_y + line_height_xs*10/8;;
            	terr_min_value_y = terr_min_box_y + line_height_xs;
            	terr_box_height = line_height_xs * 12/10;
            	terr_box_width = digit_width_xs * 11/3;
            } else {
            	terr_label_y = left_label_xpdr_y - line_height_l*2;
            	terr_box_x = this.border_right - Math.round(8.0f * this.scaling_factor) - digit_width_l * 6;
            	terr_value_x = this.border_right - Math.round(10.0f * this.scaling_factor) - digit_width_l * 6;;
            	terr_max_box_y = terr_label_y + line_height_l*10/8;
            	terr_max_value_y = terr_max_box_y + line_height_l;
            	terr_min_box_y = terr_max_box_y + line_height_l*10/8;;
            	terr_min_value_y = terr_min_box_y + line_height_l;
            	terr_box_height = line_height_l * 12/10;
            	terr_box_width = digit_width_l * 11/3;
            }

            /*
             * Terrain textures hd = high density, md = medium density, ld = low density
             */
            int terr_text_size=16;
            terrain_tp_hd_red = create_regular_terrain_texture(terrain_red_color,terr_text_size,0);
            terrain_tp_hd_yellow = create_regular_terrain_texture(terrain_bright_yellow_color,terr_text_size,0);
            terrain_tp_md_yellow = create_regular_terrain_texture(terrain_yellow_color,terr_text_size,1);
            terrain_tp_ld_yellow = create_regular_terrain_texture(terrain_yellow_color,terr_text_size,2);
            terrain_tp_solid_green = create_solid_terrain_texture(terrain_green_color,terr_text_size);
            terrain_tp_hd_green = create_regular_terrain_texture(terrain_green_color,terr_text_size,0);
            terrain_tp_md_green = create_regular_terrain_texture(terrain_green_color,terr_text_size,1);
            terrain_tp_ld_green = create_regular_terrain_texture(terrain_dark_green_color,terr_text_size,2);
            terrain_tp_blue = create_regular_terrain_texture(terrain_blue_color,terr_text_size,1);
            terrain_tp_black = create_regular_terrain_texture(terrain_black_color,terr_text_size,2);
            
            // Weather radar
            wxr_img_1 = new BufferedImage(panel_rect.width,panel_rect.height,BufferedImage.TYPE_INT_ARGB);
            wxr_img_2 = new BufferedImage(panel_rect.width,panel_rect.height,BufferedImage.TYPE_INT_ARGB);
            wxr_sweep_step = 120.0f/(preferences.get_nd_wxr_sweep_duration()*1000); 
            switch (preferences.get_nd_wxr_resolution()) {
            case 0: // Fine - up to pixel
                wxr_nb_tile_x = Math.min(250, panel_rect.width);
                wxr_nb_tile_y = Math.min(250, panel_rect.height);
                break;
            case 1: // Medium
                wxr_nb_tile_x = 150;
                wxr_nb_tile_y = 150;   
                break;
            default: // Coarse - CPU saver
                wxr_nb_tile_x = 80;
                wxr_nb_tile_y = 80;
                break;
            }            
            wxr_range_multiply = this.preferences.get_draw_only_inside_rose() ? 1.0f : 1.5f;
            wxr_tile_width = 2+(int)(frame_size.width*wxr_range_multiply*1.5f/wxr_nb_tile_x);
            wxr_tile_height = 2+(int)(frame_size.height*wxr_range_multiply*1.5f/wxr_nb_tile_y);            		
            
            // clear the flags
            this.resized = false;
            this.reconfig = false;
            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

        }

    }

    /**
     * Creates random terrain textures for the EPGWS terrain map on ND
     * @param texture_color : Color
     * @param size : in pixel
     * @param density : 0=high, 1=medium, 2=light
     * @return : TexturePaint
     */
    private TexturePaint create_random_terrain_texture(Color texture_color, int size, int density) {
       	BufferedImage texture_image = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
    	int rgb=texture_color.getRGB();
    	float threshold;
    	switch (density) {
    		case 0 : threshold=0.9f; break;
    		case 1 : threshold=0.7f; break;
    		default : threshold=0.5f; break;
    	}
    	for (int x=0; x<size; x+=2) {
    		for (int y=0; y<size; y+=2) {    			
    			if (Math.random()>threshold) texture_image.setRGB(x,y,rgb);
    		}
    	}
    	TexturePaint paint = new TexturePaint( texture_image, new Rectangle(0,0,size, size));
    	return paint;
    }
    
    /**
     * Creates regular terrain textures for the EPGWS terrain map on ND
     * @param texture_color : Color
     * @param size : in pixel
     * @param density : 0=high, 1=medium, 2=light
     * @return : TexturePaint
     */
    private TexturePaint create_regular_terrain_texture(Color texture_color, int size, int density) {
       	BufferedImage texture_image = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
    	int rgb=texture_color.getRGB();
    	switch (density) {
    		case 0 : 
    	    	for (int x=0; x<size; x++) {
    	    		for (int y=(x%2); y<size; y+=2) {    			
    	    			texture_image.setRGB(x,y,rgb);
    	    		}
    	    	} 
    	    	break;
    		case 1 :
    	    	for (int x=0; x<size; x+=2) {
    	    		for (int y=(x%2); y<size; y+=2) {    			
    	    			texture_image.setRGB(x,y,rgb);
    	    		}
    	    	}
    	    	break;
    		default :
    	    	for (int x=0; x<size; x+=4) {
    	    		for (int y=2; y<size; y+=4) {
    	    			texture_image.setRGB(x,y,rgb);
    	    		}
    	    	} 
    	    	break;
    	}
    	TexturePaint paint = new TexturePaint( texture_image, new Rectangle(0,0,size, size));
    	return paint;
    }
        
    /**
     * Creates solid terrain textures for the EPGWS terrain map on ND
     * @param solid_color : Color
     * @param size : in pixel
     * @return : TexturePaint
     */
    private TexturePaint create_solid_terrain_texture(Color solid_color, int size) {
       	BufferedImage texture_image = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_fix = texture_image.createGraphics();
    	g_fix.setRenderingHints(rendering_hints);
    	g_fix.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    	g_fix.setColor(solid_color);
   		g_fix.fillRect(0,0,size,size);
    	TexturePaint paint = new TexturePaint( texture_image, new Rectangle(0,0,size, size));
    	return paint;
    }
    
    
    private BufferedImage create_fix_symbol(Color fix_color) {
    	
    	// fix_symbol_img.setBackground(new Color(255, 255, 255, 0));
    	int x4 = Math.round(4.0f*scaling_factor);
    	int x5 = Math.round(5.0f*scaling_factor);
    	int y3 = Math.round(3.0f*scaling_factor);
    	int y4 = Math.round(4.0f*scaling_factor);
    	int y5 = Math.round(5.0f*scaling_factor);
    	int y6 = Math.round(6.0f*scaling_factor);
        int shift=2; // Shift to avoid rendering being clipped 
    	
    	int x_points_triangle[] = { shift, x5*2+shift, x5+shift };
    	int y_points_triangle[] = { y6+y3+shift, y6+y3+shift, shift };

    	int x_points_diamond[] = { shift, x5+shift, x5*2+shift, x5+shift };
    	int y_points_diamond[] = { y5+shift, y5*2+shift, y5+shift, shift };

    	BufferedImage fix_image = new BufferedImage(x5*2+shift*2,y5*2+shift*2,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_fix = fix_image.createGraphics();
    	g_fix.setRenderingHints(rendering_hints);
    	g_fix.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    	g_fix.setColor(fix_color);
    	if (boeing_style) 
    		g_fix.drawPolygon(x_points_triangle, y_points_triangle, 3);
    	else 
    		g_fix.drawPolygon(x_points_diamond, y_points_diamond, 4);
    	return fix_image;
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
