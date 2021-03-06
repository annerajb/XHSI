/**
 * NDGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2019  Nicolas Carel
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.RadioNavBeacon;
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
    public float arc_limit_deg;
    public int big_tick_length;
    public int medium_tick_length;
    public int small_tick_length;
    public int tick_text_y_offset;
    public Area inner_rose_area;
    public Area clip_rose_area;
    public int sixty_deg_hlimit;
    public BufferedImage clip_rose_area_img;

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
    
    // Preferences
    public boolean mode_mismatch_caution;
    public boolean tcas_always_on;
    public boolean draw_only_inside_rose;
    public boolean limit_arcs;
    
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
    public int range_label_3_4_y;
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
    
    // Heading bug
    
	int heading_bug_width;
	int heading_bug_height;
	public int heading_bug_value_x;
	public int heading_bug_value_y;
	public double heading_bug_display_limit;
	Stroke heading_bug_stroke;
	GeneralPath heading_bug_polyline = null;
    
	// Heading Label / symbol / line
	public int heading_line_y;
	public int heading_text_y;
	public int heading_box_bottom_y;
	public int track_diamond_shift;
	public int track_diamond_size;
	public int track_diamond_bottom;
	public BufferedImage track_diamond_img;
	
    // Moving Map Symbols
    public Font navaid_font;
    public Font data_font;

    public int fix_shift_x;
    public int fix_shift_y;
    public int fix_name_x;
    public int fix_name_y;
    public BufferedImage fix_awy_symbol_img;
    public BufferedImage fix_term_symbol_img;
    public BufferedImage fix_fpln_symbol_img;
    public BufferedImage fix_to_wpt_symbol_img;
    
    public int airport_shift_x;
    public int airport_shift_y;
    public int airport_name_x;
    public int airport_name_y;
    public int airport_data_y;
    public BufferedImage airport_symbol_img;
    
    public int ndb_shift_x;
    public int ndb_shift_y;
    public int ndb_name_x;
    public int ndb_name_y;
    public int ndb_data_y;
    public BufferedImage ndb_symbol_img;
    public BufferedImage ndb_tuned_symbol_img;
    public BufferedImage ndb_fpln_symbol_img;
    public BufferedImage ndb_to_wpt_symbol_img;
    
    public int dme_shift_x;
    public int dme_shift_y;
    public int dme_name_x;
    public int dme_name_y;
    public int dme_data_y;
    public BufferedImage dme_symbol_img;
    public BufferedImage dme_tuned_symbol_img;
    public BufferedImage dme_fpln_symbol_img;
    public BufferedImage dme_to_wpt_symbol_img;
    
    public int vor_shift_x;
    public int vor_shift_y;
    public int vor_name_x;
    public int vor_name_y;
    public int vor_data_y;
    public BufferedImage vor_symbol_img;
    public BufferedImage vor_tuned_symbol_img;
    public BufferedImage vor_fpln_symbol_img;
    public BufferedImage vor_to_wpt_symbol_img;

    public Stroke vor_longdashes_1_stroke;
    public Stroke vor_longdashes_2_stroke;
    public Stroke vor_shortdashes_1_stroke;
    public Stroke vor_shortdashes_2_stroke;
    
    public int vordme_shift_x;
    public int vordme_shift_y;
    public int vordme_name_x;
    public int vordme_name_y;
    public int vordme_data_y;
    public BufferedImage vordme_symbol_img;
    public BufferedImage vordme_tuned_symbol_img;
    public BufferedImage vordme_fpln_symbol_img;
    public BufferedImage vordme_to_wpt_symbol_img;

    
    public int loc_shift_x;
    public int loc_shift_y;
    public int loc_name_x;
    public int loc_name_y;
    public int loc_data_y;
    public BufferedImage loc_symbol_img;
    public BufferedImage loc_tuned_symbol_img;
    public BufferedImage loc_fpln_symbol_img;
    public BufferedImage loc_to_wpt_symbol_img;    
    public Stroke loc_longdashes_1_stroke;
    public Stroke loc_longdashes_2_stroke;
    public Stroke loc_shortdashes_1_stroke;
    public Stroke loc_shortdashes_2_stroke;
    public Stroke loc_basic_stroke;
    
    public Stroke map_dashdots_stroke;
    public Stroke map_dashdotdots_stroke;
    
    // Moving Map FMS
    public Stroke fmc_stroke_active;
    public Stroke fmc_stroke_inactive;
    public int fmc_entry_shift_x;
    public int fmc_entry_shift_y;
    public int fmc_name_x;
    public int fmc_name_y;
    public int fmc_data_y;
    public BufferedImage fmc_entry_active_fix;
    public BufferedImage fmc_entry_fix;
    
    // Destination label
    public int dl_id_line;
    public int dl_min_line;
    public int dl_eta_line;
    public int dl_dist_line;
    /*
    public int dl_line1;
    public int dl_line2;
    public int dl_line3;
    public int dl_line4;
    */
    public int dl_box_height;
    public int dl_dx;
    public int dl_waypoint_id_x;
    public int dl_course_x;
    public int dl_dist_unit_x;
    public int dl_dist_value_x;
    public int dl_img_width;
    public int dl_img_height;
    public int dl_img_x;
    public int dl_img_y;
    public BufferedImage dl_buf_image;
    
    // Destination and approach type
    public Font appr_type_font;
	public int appr_type_x;
	public int appr_type_y;
	
    // RadioLabel & Radio info box -> rib prefix
    public int rib_width;
    public int rib_line_1;
    public int rib_line_2;
    public int rib_line_3;
    public int rib_line_4;
    public int rib_height;
    public int rib_dme_text_width;
    public String rib_spacing;
    public int radio1_text_x;
    public int radio2_text_x;
    public int radio1_arrow_x;
    public int radio2_arrow_x;
    public int radio1_box_x;
    public int radio2_box_x;
    public int radio_box_y;
    public Stroke radio_arrow_stroke;
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
    public Font sl_font_text;
    public Font sl_font_value;
    public int sl_line_height;
    public int sl_gs_label_x;
    public int sl_gs_x;
    public int sl_tas_label_x;
    public int sl_tas_x;
    public int sl_speeds_y;    
    public int sl_wind_x;
    public int sl_wind_slash_x;
    public int sl_wind_speed_x;
    public int sl_wind_y;
    public int sl_wind_dir_arrow_length;
    public int sl_arrow_head;
    public int sl_wind_dir_arrow_cx;
    public int sl_wind_dir_arrow_cy;
    public int sl_box_x;
    public int sl_box_y;
    public int sl_box_h;
    public int sl_box_w;
    public Stroke sl_stroke;
    public BufferedImage sl_img;
    
    public int arrow_length;
    public int arrow_base_width;
    
    public float max_range;

    public int range_mode_message_y;
    public int gps_message_y;
    
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
    public int terr_label_x;
    public int terr_label_y;
    public int terr_label_rect_y;
    public int terr_info_width;
    public int terr_info_height;
    public int terr_info_x;
    public int terr_info_y;
    public BufferedImage terr_info_img;
    public BufferedImage terr_img_1;
    public BufferedImage terr_img_2;   
    public float terr_sweep_step;
    public float terr_range_multiply;
    public int terr_nb_tile_x;
    public int terr_nb_tile_y;
    public int terr_tile_width;
    public int terr_tile_height;
    public boolean terr_peaks_mode;
	public Area terr_clip; 
	public Color terr_label_color;
	public Font terr_label_font;
    
    // Weather radar
    public int wxr_info_width;
    public int wxr_info_height;
    public int wxr_info_x;
    public int wxr_info_y;
    public int wxr_label1_y;
    public int wxr_label2_y;
	public BufferedImage wxr_info_img;
    public BufferedImage wxr_img_1;
    public BufferedImage wxr_img_2;
    public float wxr_sweep_step;
    public float wxr_range_multiply;
    public int wxr_nb_tile_x;
    public int wxr_nb_tile_y;
    public int wxr_tile_width;
    public int wxr_tile_height;
    public int wxr_radius;
	public Area wxr_clip;
	public Font wxr_label_font;



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
        mode_mismatch_caution=false;
    }


//    public void init() {
//
//        super.init();
//
//    }


    public void update_config(Graphics2D g2, int mode, int submode, int range, boolean zoomin,
    		boolean power, int instrument_style, float du_brightness) {
    	// TODO: add boolean narrow_mode

    	// Update colors if du_brightness changed
    	colors_updated = update_colors(du_brightness);

    	boolean settings_updated = (this.resized
                || this.reconfig
                || (this.map_mode != mode)
                || (this.map_submode != submode)
                || (this.map_range != range)
                || (this.map_zoomin != zoomin)
                || (this.powered != power)
                || (this.style != instrument_style)
            );
    	
        if (settings_updated) {
            // one of the settings has been changed

            // remember the new settings
            this.powered = power;
            this.style = instrument_style;
            
            this.terr_peaks_mode = preferences.get_preference(XHSIPreferences.PREF_TERRAIN_PEAKS_MODE).equals("true");
            
            super.update_config(g2);
            
            // Timestamp mode/submode/range settings
            if (this.map_mode != mode) map_mode_timestamp = current_time_millis;            	
            if (this.map_submode != submode) map_submode_timestamp = current_time_millis;
            if (this.map_range != range) map_range_timestamp = current_time_millis;
            if (this.map_zoomin != zoomin) map_zoomin_timestamp = current_time_millis;

            // remember the mode/submode/range settings
            this.map_mode = mode;
            this.map_submode = submode;
            this.map_range = range;
            this.map_zoomin = zoomin;
            
            // remember preferences
            // avoid CPU expensive string.equal function
            this.mode_mismatch_caution = this.preferences.get_mode_mismatch_caution();
            this.tcas_always_on = this.preferences.get_tcas_always_on();
            this.draw_only_inside_rose = this.preferences.get_draw_only_inside_rose();
            this.arc_limit_deg = this.preferences.get_limit_arcs_deg();
            this.limit_arcs = (arc_limit_deg != 0);
            

            // compute radio info box 
            if (boeing_style) {
                rib_line_1 = line_height_l + line_height_l/5;
                rib_line_2 = rib_line_1 + line_height_l;
                rib_line_3 = rib_line_2 + line_height_s;
                rib_line_4 = rib_line_3 + line_height_s;
                rib_height = rib_line_4 + line_height_l/2;
                rib_width = digit_width_l * 9;
                rib_dme_text_width = get_text_width(g2, font_xs, "DME ");
                rib_spacing = " ";
            	radio1_text_x = digit_width_l;
            	radio2_text_x = digit_width_l * 25/10;
            	radio1_arrow_x = rib_width - digit_width_l;
            	radio2_arrow_x = digit_width_l;
            	radio1_box_x = border_left;
            	radio2_box_x = frame_size.width - border_right - rib_width;
            	radio_box_y = frame_size.height - rib_height - border_bottom;
            	radio_arrow_stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            } else {
                rib_line_1 = line_height_xxl + line_height_l/5;
                rib_line_2 = rib_line_1 + line_height_xxl;
                rib_line_3 = rib_line_2 + line_height_xxl;
                rib_line_4 = rib_line_3 + line_height_s; // Unused (obs)
                rib_height = rib_line_3 + line_height_l/4;
                rib_width = digit_width_xl * 9;                
                rib_dme_text_width = get_text_width(g2, font_xxl, "0000");
                rib_spacing = "";
                radio2_text_x = digit_width_l;
                radio1_text_x = digit_width_l * 25/10;
                radio2_arrow_x = rib_width - digit_width_l;
                radio1_arrow_x = digit_width_l;
                radio1_box_x = border_left;
                radio2_box_x = frame_size.width - border_right - rib_width;
                radio_box_y = frame_size.height - rib_height - border_bottom;
                radio_arrow_stroke = new BasicStroke(2.0f*scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
            }
            left_radio_box_img = new BufferedImage(rib_width, rib_height, BufferedImage.TYPE_INT_ARGB);
            right_radio_box_img = new BufferedImage(rib_width, rib_height, BufferedImage.TYPE_INT_ARGB);
            
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
            
            // For plan mode i.e. north mode, hdg_up and trk_up = false
            if (airbus_style) {
            	// Airbus is always in heading up mode.
            	hdg_up = ! mode_plan;
            	trk_up = false;
            } else {
            	hdg_up = mode_app || mode_vor;
            	trk_up = ! ( hdg_up || mode_plan );
            }


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
            this.left_label_terrain_y = this.left_label_xpdr_y + this.line_height_xs*10/8;

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
            	this.tick_text_y_offset = rose_y_offset + big_tick_length*12/8;
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
            // this.sixty_deg_hlimit = (int)(Math.sin(Math.PI/3.0) * rose_radius);
            this.sixty_deg_hlimit = (int)(Math.sin(arc_limit_deg*Math.PI/180.0) * rose_radius);
            
            clip_rose_area = new Area(new Rectangle2D.Float(0,0, frame_size.width, frame_size.height));
            clip_rose_area.subtract(inner_rose_area);
            clip_rose_area_img = createClipRoseAreaImage();

            // Range and Mode change message
            range_mode_message_y = this.frame_size.height*38/100;
            
            // GPS Message 
            gps_message_y = this.frame_size.height*97/100;

            // Fonts and graphics settings for compass rose
            if (boeing_style) {
                compass_text_font=this.font_m;
                compass_small_text_font=this.font_s;
                range_label_font=this.font_xs;               
                range_label_half_y = map_center_y - rose_radius/2 + line_height_xs;
                range_label_half_x = map_center_x;
                range_label_full_y = map_center_y - rose_radius + line_height_xs;
                range_label_full_x = map_center_x;
                if (!mode_centered) range_label_half_y = map_center_y - (rose_radius / 2) - (line_height_m / 2) + 5;
                range_label_3_4_y = map_center_y - (rose_radius *3 / 4) + (line_height_xl * 15 / 8); // unused for Boeing
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
                compass_text_font=this.font_xxxl;
                compass_small_text_font=this.font_l;
                range_label_font=this.font_xl;
                cardinal_labels_font=this.font_l;
                range_label_half_y = map_center_y + rose_radius*66/200 - line_height_xs;
                range_label_half_x = map_center_x - rose_radius*66/200;
                range_label_full_y = map_center_y + rose_radius*68/100 - line_height_xs;
                range_label_full_x = map_center_x - rose_radius*68/100;
                if (!mode_centered) range_label_half_y = map_center_y - (rose_radius / 2) + (line_height_xl * 9 / 8);
                range_label_3_4_y = map_center_y - (rose_radius * 3 / 4) + (line_height_xl * 9 / 8);
                cardinal_vert_x = map_center_x - max_char_advance_m/2;
                cardinal_N_y = map_center_y - rose_radius + line_height_l*15/8;
                cardinal_S_y = map_center_y + rose_radius - line_height_l*10/8;
                cardinal_horiz_y = map_center_y + line_height_m/2;
                cardinal_E_x = map_center_x + rose_radius - max_char_advance_l*15/8;
                cardinal_W_x = map_center_x - rose_radius + max_char_advance_l*10/8;
                int tri_dx = max_char_advance_l/2;
                int tri_dy = line_height_l/2;
                if (mode_plan) {
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
                } else {
                    tri_dx = max_char_advance_l/3;
                    tri_dy = line_height_l/3;
                	cardinal_tri_N.reset();
                	cardinal_tri_N.addPoint(map_center_x, map_center_y - rose_radius);
                	cardinal_tri_N.addPoint(map_center_x+tri_dx, map_center_y - rose_radius - line_height_l*2/3);
                	cardinal_tri_N.addPoint(map_center_x-tri_dx, map_center_y - rose_radius - line_height_l*2/3);
                	cardinal_tri_S.reset();
                	cardinal_tri_S.addPoint(map_center_x, map_center_y + rose_radius);
                	cardinal_tri_S.addPoint(map_center_x+tri_dx, map_center_y + rose_radius + line_height_l*2/3);
                	cardinal_tri_S.addPoint(map_center_x-tri_dx, map_center_y + rose_radius + line_height_l*2/3);
                	cardinal_tri_E.reset();
                	cardinal_tri_E.addPoint(map_center_x + rose_radius, map_center_y);
                	cardinal_tri_E.addPoint(map_center_x + rose_radius + max_char_advance_l*2/3, map_center_y - tri_dy);
                	cardinal_tri_E.addPoint(map_center_x + rose_radius + max_char_advance_l*2/3, map_center_y + tri_dy);
                	cardinal_tri_W.reset();
                	cardinal_tri_W.addPoint(map_center_x - rose_radius, map_center_y);
                	cardinal_tri_W.addPoint(map_center_x - rose_radius - max_char_advance_l*2/3, map_center_y - tri_dy);
                	cardinal_tri_W.addPoint(map_center_x - rose_radius - max_char_advance_l*2/3, map_center_y + tri_dy);
                }

            }
            
            // compute text widths and heights
            compass_two_digit_hdg_text_width = (int) this.get_text_width(g2, this.compass_text_font, "33");
            compass_one_digit_hdg_text_width = (int) this.get_text_width(g2, this.compass_text_font, "8");
            compass_two_digit_hdg_small_text_width = (int) this.get_text_width(g2, this.compass_small_text_font, "33");
            compass_one_digit_hdg_small_text_width = (int) this.get_text_width(g2, this.compass_small_text_font, "8");
            compass_hdg_text_height = (int) (this.get_text_height(g2, this.compass_text_font)*0.8f);
            compass_hdg_small_text_height = (int) (this.get_text_height(g2, this.compass_small_text_font)*0.8f);
            
            // BufferedImage to cache CompassRose SubComponent
            compass_rose_img = new BufferedImage(this.frame_size.width,this.frame_size.height,BufferedImage.TYPE_INT_ARGB);
            
            /*
             * Create MovingMap Strokes
             */
            // float range_dashes[] = { 10.0f, 10.0f };
            
            float longdashes_1[] = { 16.0f, 6.0f };
            float longdashes_2[] = { 10.0f, 2.0f, 10.0f, 8.0f };
            
            float shortdashes_1[] = { 4.0f, 12.0f };
            float shortdashes_2[] = { 3.0f, 2.0f, 3.0f, 14.0f };
            
            float dashdots[] = { 18.0f, 5.0f, 4.0f, 5.0f };
            float dashdotdots[] = { 18.0f, 5.0f, 4.0f, 5.0f, 4.0f, 5.0f };
            float dots[] = { 1.0f, 2.0f };
            
            vor_longdashes_1_stroke  = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes_1, 0.0f);
            vor_longdashes_2_stroke  = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes_2, 0.0f);            
            vor_shortdashes_1_stroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, shortdashes_1, 0.0f);
            vor_shortdashes_2_stroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, shortdashes_2, 0.0f);
            
            loc_longdashes_1_stroke  = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes_1, 0.0f);
            loc_longdashes_2_stroke  = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longdashes_2, 0.0f);            
            loc_shortdashes_1_stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, shortdashes_1, 0.0f);
            loc_shortdashes_2_stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, shortdashes_2, 0.0f);
            
            loc_basic_stroke = new BasicStroke(2.0f);
            
            map_dashdots_stroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashdots, 0.0f);
            map_dashdotdots_stroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashdotdots, 0.0f);
            /*
             *  Moving Map Symbols
             */
            
            // Font selection 
            navaid_font = (boeing_style ? font_s : font_s);
            data_font = (boeing_style ? font_xs : font_xs);
            
            // Fix symbol
            fix_awy_symbol_img = create_fix_symbol(awy_wpt_color);
            fix_term_symbol_img = create_fix_symbol(term_wpt_color);
            fix_shift_x = Math.round(5.0f*scaling_factor) + 2;
            fix_shift_y = Math.round(6.0f*scaling_factor) + 2;
            fix_name_x = Math.round((boeing_style ? 12.0f : 10.5f)*scaling_factor);
            fix_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : 0);
            
            // Airport symbol
            airport_shift_x = Math.round(9.0f*scaling_factor) + 3;
            airport_shift_y = Math.round(9.0f*scaling_factor) + 3;
            airport_name_x = Math.round((boeing_style ? 12.0f : 11.0f)*scaling_factor);
            airport_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            airport_data_y = (boeing_style ? airport_name_y - line_height_s : Math.round(12.0f*scaling_factor) );
            
            // NDB symbol
            ndb_shift_x = Math.round(9.0f*scaling_factor) + 3;
            ndb_shift_y = Math.round(9.0f*scaling_factor) + 3;
            ndb_name_x = Math.round((boeing_style ? 12.0f : 11.0f)*scaling_factor);
            ndb_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            ndb_data_y = (boeing_style ? ndb_name_y - line_height_s : Math.round(12.0f*scaling_factor) );
            
            // DME symbol
            dme_shift_x = Math.round(9.0f*scaling_factor) + 3;
            dme_shift_y = Math.round(9.0f*scaling_factor) + 3;
            dme_name_x = Math.round((boeing_style ? 12.0f : 13.0f)*scaling_factor);
            dme_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            dme_data_y = (boeing_style ? dme_name_y - line_height_s : Math.round(12.0f*scaling_factor) );            
            
            // VOR symbol
            vor_shift_x = Math.round(9.0f*scaling_factor) + 3;
            vor_shift_y = Math.round(9.0f*scaling_factor) + 3;
            vor_name_x = Math.round((boeing_style ? 12.0f : 11.0f)*scaling_factor);
            vor_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            vor_data_y = (boeing_style ? vor_name_y - line_height_s : Math.round(12.0f*scaling_factor) );            
            
            // VORDME symbol
            vordme_shift_x = Math.round(11.0f*scaling_factor) + 3;
            vordme_shift_y = Math.round(11.0f*scaling_factor) + 3;
            vordme_name_x = Math.round((boeing_style ? 12.0f : 13.0f)*scaling_factor);
            vordme_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            vordme_data_y = (boeing_style ? vordme_name_y - line_height_s : Math.round(12.0f*scaling_factor) );            

            // LOC symbol
            loc_shift_x = Math.round(9.0f*scaling_factor) + 3;
            loc_shift_y = Math.round(9.0f*scaling_factor) + 3;
            loc_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            loc_name_x = Math.round((boeing_style ? 12.0f : 11.0f)*scaling_factor);
            loc_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            loc_data_y = (boeing_style ? loc_name_y - line_height_s : Math.round(12.0f*scaling_factor) ); 
            
            // FMC Flight plan and waypoints
            fmc_stroke_active = new BasicStroke(boeing_style ? 1.5f : 2.5f);
            float range_dashes[] = { 8.0f, 8.0f };
            fmc_stroke_inactive =  new BasicStroke(boeing_style ? 1.5f : 2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 8.0f, range_dashes, 0.0f);
            fmc_name_x = Math.round((boeing_style ? 12.0f : 11.0f)*scaling_factor);
            fmc_name_y = (boeing_style ? Math.round(12.0f*scaling_factor) : -Math.round(1.0f*scaling_factor) );
            fmc_data_y = (boeing_style ? loc_name_y - line_height_s : Math.round(12.0f*scaling_factor) ); 
            if (boeing_style) {
            	fmc_entry_shift_x = Math.round(11.0f*scaling_factor) + 3;
            	fmc_entry_shift_y = Math.round(11.0f*scaling_factor) + 3;
            } else {
            	fmc_entry_shift_x = fix_shift_x;
            	fmc_entry_shift_y = fix_shift_y;
            }
            
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
            
            /*
             * Heading Bug
             */            
            // heading_bug_display_limit = 40 in a square ND from real A320          
            float x_limit_ratio = Math.min(1.0f, (map_center_x-border_left)/(float)rose_radius);            
            heading_bug_display_limit = Math.min(Math.min(68, arc_limit_deg),Math.toDegrees(Math.asin(x_limit_ratio)))-8;
            heading_bug_value_x = map_center_x - get_text_width(g2, font_xxl, "000")/2;
            heading_bug_value_y = rose_y_offset - (int)(4*shrink_scaling_factor);
            float dash[] = { 11.0f, 22.0f };
            heading_bug_stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        	if (boeing_style) {
            	heading_bug_width = Math.round(30.0f * scaling_factor);
            	heading_bug_height = Math.round(12.0f * scaling_factor);
        		heading_bug_polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 9);
        		heading_bug_polyline.moveTo(map_center_x - heading_bug_width/2, rose_y_offset);
        		heading_bug_polyline.lineTo(map_center_x - heading_bug_width/2, rose_y_offset - heading_bug_height);
        		heading_bug_polyline.lineTo(map_center_x - (heading_bug_width/3 - 1), rose_y_offset - heading_bug_height);
        		heading_bug_polyline.lineTo(map_center_x, rose_y_offset);
        		heading_bug_polyline.lineTo(map_center_x + (heading_bug_width/3 - 1), rose_y_offset - heading_bug_height);
        		heading_bug_polyline.lineTo(map_center_x + heading_bug_width/2, rose_y_offset - heading_bug_height);
        		heading_bug_polyline.lineTo(map_center_x + heading_bug_width/2, rose_y_offset);
        		heading_bug_polyline.lineTo (map_center_x - heading_bug_width/2, rose_y_offset);
        	} else {
            	heading_bug_width = Math.round(12.5f * shrink_scaling_factor);
            	// heading_bug_height = Math.round(25.0f * scaling_factor);
            	heading_bug_height = Math.round(27.0f * shrink_scaling_factor);            	
        		heading_bug_polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
        		heading_bug_polyline.moveTo(map_center_x, rose_y_offset);
        		heading_bug_polyline.lineTo(map_center_x - heading_bug_width, rose_y_offset - heading_bug_height);
        		heading_bug_polyline.lineTo(map_center_x + heading_bug_width, rose_y_offset - heading_bug_height);
        		heading_bug_polyline.lineTo(map_center_x, rose_y_offset);
        	}
        	
        	// Heading line and symbols
            heading_text_y = border_top + line_height_xl*10/8;
            heading_box_bottom_y = border_top + line_height_xl*12/8;
        	heading_line_y = boeing_style ? heading_box_bottom_y : map_center_y - rose_radius; 
        	track_diamond_shift = (int) (10 * shrink_scaling_factor);
        	track_diamond_size = (int) (20 * shrink_scaling_factor);
        	track_diamond_bottom = heading_line_y + track_diamond_size*3/2-2;
        	
        	
            // Speed Labels (and wind arrow)
        	if (boeing_style) {
        		sl_font_text = font_s;
        		sl_font_value = font_l;
        		sl_line_height = line_height_l;
        		sl_gs_label_x = border_left + (int)(10*scaling_factor);
        		sl_gs_x = sl_gs_label_x + 2 + get_text_width(g2, font_s,"GS") + digit_width_s + get_text_width(g2, sl_font_value, "000");;
        		sl_tas_label_x = sl_gs_x + digit_width_fixed_l*4; //  gs_x + nd_gc.get_text_width(g2, nd_gc.font_l, "999   "); // \u00A0 is Unicode non-breaking space
        		sl_tas_x = sl_tas_label_x + 2 + get_text_width(g2, font_s,"TAS") + digit_width_s + get_text_width(g2, sl_font_value, "000");
        		sl_speeds_y = border_top + sl_line_height;

        		sl_wind_x = sl_gs_label_x;
        		sl_wind_slash_x = sl_wind_x + this.digit_width_xl*3;
        		sl_wind_speed_x = sl_wind_x + this.digit_width_xl*5;
        		
        		sl_wind_y = border_top + sl_line_height*24/10;
        		sl_wind_dir_arrow_length = Math.round(40.0f * scaling_factor);
        		sl_arrow_head = Math.round(3.0f * scaling_factor);
        		sl_wind_dir_arrow_cx = sl_wind_x + sl_wind_dir_arrow_length/2;
        		sl_wind_dir_arrow_cy = sl_wind_y + sl_line_height*2/10 + sl_wind_dir_arrow_length*1/8 + sl_wind_dir_arrow_length/2;
        		sl_stroke = new BasicStroke(2.0f * scaling_factor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        	} else {
        		sl_font_text = font_l;
        		sl_font_value = font_xxl;
        		sl_line_height = line_height_xxl;
        		sl_gs_label_x = border_left + digit_width_l*35/10; // (int)(30*scaling_factor);
        		sl_gs_x = sl_gs_label_x + get_text_width(g2, sl_font_text, "GS") + digit_width_l / 2 + get_text_width(g2, sl_font_value, "000");
        		sl_tas_label_x = sl_gs_x + digit_width_l; 
        		sl_tas_x = sl_tas_label_x + get_text_width(g2, sl_font_text, "TAS") + digit_width_l / 2 + get_text_width(g2, sl_font_value, "000");
        		sl_speeds_y = border_top + sl_line_height*25/20;

        		sl_wind_x = sl_gs_label_x;
        		sl_wind_slash_x = sl_wind_x + this.digit_width_xxl*33/10;
        		sl_wind_speed_x = sl_wind_x + this.digit_width_xxl*9/2;
        		
        		sl_wind_y = border_top + sl_line_height*45/20;
        		sl_wind_dir_arrow_length = Math.round(28.0f * scaling_factor);
        		sl_arrow_head = Math.round(5.0f * scaling_factor);
        		sl_wind_dir_arrow_cx = sl_wind_x + this.digit_width_xxl*15/10; // + sl_wind_dir_arrow_length/2;
        		sl_wind_dir_arrow_cy = sl_wind_y + sl_line_height*2/10 + sl_wind_dir_arrow_length*1/8 + sl_wind_dir_arrow_length/2;
        		sl_stroke = new BasicStroke(3.0f * scaling_factor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        	}
            
            sl_box_x = border_left;
            sl_box_y = border_top;
            sl_box_w = border_left + digit_width_m*15;
            sl_box_h = sl_wind_y + sl_line_height*2/10;
            sl_img = new BufferedImage(sl_box_w,sl_box_h,BufferedImage.TYPE_INT_ARGB);
            
            /*
             * EGPWS - Terrain
             */
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
            	terr_label_rect_y = line_height_xs*1/8;
            	terr_label_y = line_height_xs*9/8;
            	terr_label_x = 0;
            	terr_box_x = this.digit_width_xs*5;
            	terr_value_x = this.digit_width_xs*11/2;
            	terr_max_box_y = terr_label_rect_y;
            	terr_max_value_y = terr_max_box_y + line_height_xs*11/10;
            	terr_min_box_y = terr_label_y + line_height_xs/2;
            	terr_min_value_y = terr_min_box_y + line_height_xs;
            	terr_box_height = line_height_xs * 12/10;
            	terr_box_width = digit_width_xs * 12/3;
            	terr_label_color = map_zoomin ? dim_label_color : terrain_label_color;
            	terr_label_font = font_s;
            	
            	terr_info_width = this.digit_width_xs*5 + terr_box_width+2;
            	terr_info_height = terr_min_box_y + terr_box_height + line_height_xs/5+1;
            	terr_info_x = left_label_x;
            	terr_info_y = left_label_xpdr_y + line_height_xs*1/8;
            } else {
            	terr_label_rect_y = left_label_xpdr_y + line_height_xs*1/8;
            	terr_label_y = line_height_l*9/8;
            	terr_label_x = panel_rect.width * 6/1000;
            	
            	terr_box_x = panel_rect.width * 7/1000;
            	terr_value_x = panel_rect.width * 14/1000;
            	terr_max_box_y = terr_label_y + line_height_l*2/8;
            	terr_max_value_y = terr_max_box_y + line_height_l*9/8;
            	
            	terr_min_box_y = terr_max_box_y + line_height_l*11/8;
            	terr_min_value_y = terr_min_box_y + line_height_l*9/8;
            	terr_box_height = line_height_l * 12/10;
            	terr_box_width = digit_width_l * 41/10;
            	terr_label_color = map_zoomin ? ecam_caution_color : color_airbus_selected; 
            	terr_label_font = font_xl;
            	
            	terr_info_width = this.digit_width_xl*5;
            	terr_info_height = terr_min_box_y + terr_box_height + line_height_l/5+1;
            	// terr_info_x = panel_rect.x + panel_rect.width * 855/1000;
            	// terr_info_y = this.frame_size.height*745/1000;
            	terr_info_x = frame_size.width - border_right - terr_box_width - digit_width_xl;            	
            	terr_info_y = frame_size.height - border_bottom - rib_height - terr_info_height;
            	
            }
            terr_info_img = new BufferedImage(terr_info_width,terr_info_height,BufferedImage.TYPE_INT_ARGB);          
        	float terr_sweep_max = 75.0f;
        	terr_clip = new Area(new Arc2D.Float(map_center_x - rose_radius, map_center_y - rose_radius,
					rose_radius*2, rose_radius*2, 90-terr_sweep_max, terr_sweep_max*2, Arc2D.PIE));
            
            /*
             *  Weather radar
             */
            // wxr_radius limits the weather radar range to 100nm
            wxr_radius = Math.min(rose_radius, (int)(pixels_per_nm*100));
            wxr_img_1 = new BufferedImage(panel_rect.width,panel_rect.height,BufferedImage.TYPE_INT_ARGB);
            wxr_img_2 = new BufferedImage(panel_rect.width,panel_rect.height,BufferedImage.TYPE_INT_ARGB);
            wxr_sweep_step = 120.0f/(preferences.get_nd_wxr_sweep_duration()*1000); 
            switch (preferences.get_nd_wxr_resolution()) {
            case 0: // Fine - up to pixel with interpolation - CPU expensive
                wxr_nb_tile_x = Math.min(320, panel_rect.width);
                wxr_nb_tile_y = Math.min(320, panel_rect.height);
                break;
            case 1: // Medium
                wxr_nb_tile_x = 160;
                wxr_nb_tile_y = 160;   
                break;
            default: // Coarse - CPU saver
                wxr_nb_tile_x = 80;
                wxr_nb_tile_y = 80;
                break;
            }            
            wxr_range_multiply = this.preferences.get_draw_only_inside_rose() ? 1.0f : 1.5f;
            wxr_tile_width = 2+(int)(frame_size.width*wxr_range_multiply*1.5f/wxr_nb_tile_x);
            wxr_tile_height = 2+(int)(frame_size.height*wxr_range_multiply*1.5f/wxr_nb_tile_y);  
            // TODO: should depends on narrow mode - fix constructor
        	float wxr_sweep_max = 60.0f;
        	float wxr_sweep_min = -60.0f;
        	wxr_clip = new Area(new Arc2D.Float(map_center_x - wxr_radius, map_center_y - wxr_radius,
        			wxr_radius*2, wxr_radius*2, 90-wxr_sweep_min, wxr_sweep_min-wxr_sweep_max, Arc2D.PIE));
        	if (boeing_style) {
        		wxr_info_width = this.digit_width_xs*11/2 + terr_box_width;
        		wxr_info_height = terr_box_height *3;
            	wxr_info_x = left_label_x;
            	wxr_info_y = left_label_xpdr_y + line_height_xs*1/8;
            	wxr_label1_y = line_height_xs*9/8;
            	wxr_label2_y = line_height_xs*20/8;
            	wxr_label_font = font_s;
        	} else {
            	wxr_info_width = this.digit_width_xl*16/2;
            	wxr_info_height = line_height_xl * 5/2;
            	wxr_info_x = panel_rect.x + panel_rect.width *990/1000 - wxr_info_width;
            	wxr_info_y = this.frame_size.height*700/1000;
            	wxr_label1_y = line_height_l*9/8;
            	wxr_label2_y = line_height_l*20/8;
            	wxr_label_font = font_l;
        	}
	
        	wxr_info_img = new BufferedImage(wxr_info_width,wxr_info_height,BufferedImage.TYPE_INT_ARGB);
        	
        	/*
        	 * Approach type message
        	 */
        	appr_type_font = font_xxxl;
        	appr_type_x = panel_rect.x + panel_rect.width / 2 - digit_width_xxxl*4;
        	appr_type_y = panel_rect.y + line_height_xl;
        	
        	/*
        	 * Destination Label
        	 */
        	if (boeing_style) {
        		dl_id_line = line_height_l;
        		dl_min_line = dl_id_line + line_height_xs;
        		dl_eta_line = dl_min_line + line_height_l;
        		dl_dist_line = dl_eta_line + line_height_l;
        		dl_box_height = dl_dist_line + line_height_xs/2;
        		dl_dx = max_char_advance_l/2;
        		dl_waypoint_id_x = dl_dx;
        	    dl_course_x = dl_dx;
        	    dl_dist_unit_x = dl_dx + max_char_advance_l * 5;
        	    dl_dist_value_x = dl_dx;
                dl_img_width = max_char_advance_l * 8;
                dl_img_height = line_height_l * 5;
                dl_img_x = panel_rect.x + panel_rect.width - dl_img_width;
                dl_img_y = panel_rect.y;
        	} else {
        		dl_id_line = line_height_xl;
        		dl_min_line = dl_id_line;
        		dl_dist_line = dl_id_line + line_height_xl;
                dl_eta_line = dl_dist_line + line_height_xl;
                dl_box_height = dl_eta_line + line_height_xs/2;
                dl_dx = max_char_advance_xxl * 5;
                dl_waypoint_id_x = 0;
        	    dl_course_x = max_char_advance_xxl * 11/2;
        	    dl_dist_unit_x = max_char_advance_xxl * 9;
        	    dl_dist_value_x = max_char_advance_xxl * 5;
                dl_img_width = max_char_advance_xxl * 11;
                dl_img_height = line_height_xl * 3;
                dl_img_x = panel_rect.x + panel_rect.width - dl_img_width ;
                dl_img_y = panel_rect.y + line_height_xl/2;
        	}
        	dl_buf_image = new BufferedImage(dl_img_width, dl_img_height,BufferedImage.TYPE_INT_ARGB);
        	
            // clear the flags
            this.resized = false;
            this.reconfig = false;
            // some subcomponents need to be reminded to redraw immediately
            this.reconfigured = true;

        }
        
        if (colors_updated || settings_updated) {
            fix_awy_symbol_img = create_fix_symbol(awy_wpt_color);
            fix_term_symbol_img = create_fix_symbol(term_wpt_color);
            airport_symbol_img = create_airport_symbol(arpt_color);
            ndb_symbol_img = create_NDB_symbol(navaid_color);
            ndb_tuned_symbol_img = create_NDB_symbol(boeing_style ? tuned_ndb_color : tuned_navaid_color);
            dme_symbol_img = create_DME_symbol(navaid_color);
            dme_tuned_symbol_img = create_DME_symbol(boeing_style ? tuned_vor_color : tuned_navaid_color);
            vor_symbol_img = create_VOR_symbol(navaid_color);
            vor_tuned_symbol_img = create_VOR_symbol(boeing_style ? tuned_vor_color : tuned_navaid_color);
            vordme_symbol_img = create_VORDME_symbol(navaid_color);
            vordme_tuned_symbol_img = create_VORDME_symbol(boeing_style ? tuned_vor_color: tuned_navaid_color);
            loc_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
            loc_tuned_symbol_img = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);

            if (boeing_style) {
            	fmc_entry_active_fix = createFMSEntryStar(fmc_active_color);
            	fmc_entry_fix = createFMSEntryStar(fmc_disp_color);
            } else {
            	fmc_entry_active_fix = create_fix_symbol(fmc_active_color);
            	fmc_entry_fix = create_fix_symbol(fmc_disp_color);
            }
            	
            track_diamond_img = createTrackDiamond();
            createTerrainTextures();        	
        }
    }

    private void createTerrainTextures() {
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
        terrain_tp_black = create_solid_terrain_texture(terrain_black_color,terr_text_size);
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
    	// Boeing : triangle
    	// Airbus : diamond
    	    	
    	int x = fix_shift_x;
    	int y = fix_shift_y;	
    	
    	int x5 = Math.round(5.0f*scaling_factor);
    	int y3 = Math.round(3.0f*scaling_factor);
    	int y5 = Math.round(5.0f*scaling_factor);
    	int y6 = Math.round(6.0f*scaling_factor);
    	
        int shift=2; // Shift to avoid rendering being clipped 
    	
    	int x_points_triangle[] = { x-x5, x+x5, x };
    	int y_points_triangle[] = { y6+y3+shift, y6+y3+shift, shift };

    	int x_points_diamond[] = { x-x5, x, x+x5, x };
    	int y_points_diamond[] = { y, y+y5, y, y-y5 };
    	
    	BufferedImage fix_image = new BufferedImage(x*2+shift*2+1,y*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	
    	Graphics2D g_fix = fix_image.createGraphics();
    	g_fix.setRenderingHints(rendering_hints);
    	g_fix.setColor(fix_color);
    	if (boeing_style) {
    		g_fix.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    		g_fix.drawPolygon(x_points_triangle, y_points_triangle, 3);
    	} else { 
    		g_fix.setStroke(new BasicStroke(1.5f*scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    		g_fix.drawPolygon(x_points_diamond, y_points_diamond, 4);
    	}
    	return fix_image;
    }
    
    
    private BufferedImage create_airport_symbol(Color airport_color) {
        int c9 = Math.round(9.0f*scaling_factor);
        int c2 = Math.round(2.0f*scaling_factor);
        int c16 = Math.round(16.0f*scaling_factor);
        int c18 = Math.round(18.0f*scaling_factor);

        int shift=2; // Shift to avoid rendering being clipped 
    	
    	BufferedImage arpt_image = new BufferedImage(c18+shift*2+1,c18*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_arpt = arpt_image.createGraphics();
    	g_arpt.setRenderingHints(rendering_hints);
    	g_arpt.setColor(airport_color);
    	
    	if (boeing_style) { 
    		// Boeing Airport symbol (circle)
            g_arpt.setStroke(new BasicStroke(2.5f));
            g_arpt.drawOval(shift,shift, 2*c9, 2*c9); // with a thicker line and somewhat bigger symbol than the navaids...
    	} else {
    		// Airbus Airport symbol (star)
    		g_arpt.setStroke(new BasicStroke(1.5f*scaling_factor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    		g_arpt.drawLine(shift, shift+c9, shift+c18,shift+c9);
    		g_arpt.drawLine(shift+c9, shift, shift+c9,shift+c18);
    		g_arpt.drawLine(shift+c2, shift+c2, shift+c16,shift+c16);
    		g_arpt.drawLine(shift+c16, shift+c2, shift+c2,shift+c16);
    	}
    	return arpt_image;
    }
    
    private BufferedImage create_VOR_symbol(Color vor_color) {
    	// Boeing : just a big hexagon for VOR without DME
    	// Airbus : + sign

    	int x = vor_shift_x;
    	int y = vor_shift_y;	
        int c9 = Math.round(9.0f*scaling_factor);
        int x4 = Math.round(4.0f*scaling_factor);
        int x8 = Math.round(8.0f*scaling_factor);
        int y7 = Math.round(7.0f*scaling_factor);
        
        int shift=2; // Shift to avoid rendering being clipped 
        
        int x_points_hexagon[] = { x-x4, x+x4, x+x8, x+x4, x-x4, x-x8 };
        int y_points_hexagon[] = { y-y7, y-y7, y, y+y7, y+y7, y };

    	BufferedImage vor_image = new BufferedImage(x*2+shift*2+1,y*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_vor = vor_image.createGraphics();
    	g_vor.setRenderingHints(rendering_hints);
    	g_vor.setColor(vor_color);
        
    	if (boeing_style) { 
    		g_vor.setStroke(new BasicStroke(2.5f));
    		g_vor.drawPolygon(x_points_hexagon, y_points_hexagon, 6);
    	} else {
    		g_vor.setStroke(new BasicStroke(1.8f*scaling_factor));    		   		
    		g_vor.drawLine(x, y-c9, x, y+c9);
    		g_vor.drawLine(x-c9,y, x+c9, y);
    	}        
        return vor_image;
    }
    
    private BufferedImage create_VORDME_symbol(Color vordme_color) {
    	// Boeing : Hexagon with 3 leaves
    	// Airbus : Circle over a + sing    	

        // a somewhat smaller hexagon with 3 leaves for VOR with DME
    	int x = vordme_shift_x;
    	int y = vordme_shift_y;
    	int c5 = Math.round(5.0f*scaling_factor);
        int c9 = Math.round(9.0f*scaling_factor);
        int x3 = Math.round(3.0f*scaling_factor);
        int x6 = Math.round(6.0f*scaling_factor);
        int x8 = Math.round(8.0f*scaling_factor);
        int x11 = Math.round(11.0f*scaling_factor);
        int y3 = x3;
        int y5 = Math.round(5.0f*scaling_factor);
        int y8 = x8;
        int y11 = x11;
        int x_points_hexagon[] = { x-x3, x+x3, x+x6, x+x3, x-x3, x-x6 };
        int y_points_hexagon[] = { y-y5, y-y5, y, y+y5, y+y5, y };
        int x_points_ul_leaf[] = { x-x6, x-x3, x-x8, x-x11 };
        int y_points_ul_leaf[] = { y,   y-y5, y-y8, y-y3 };
        int x_points_ur_leaf[] = { x+x6, x+x3, x+x8, x+x11 };
        int y_points_ur_leaf[] = { y,   y-y5, y-y8, y-y3 };
        int x_points_b_leaf[] =  { x-x3, x+x3, x+x3, x-x3 };
        int y_points_b_leaf[] =  { y+y5, y+y5, y+y11, y+y11 };
       
        int shift=2; // Shift to avoid rendering being clipped 
        
    	BufferedImage vordme_image = new BufferedImage(x*2+shift*2+1,y*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_vordme = vordme_image.createGraphics();
    	g_vordme.setRenderingHints(rendering_hints);

    	if (boeing_style) { 
    		g_vordme.setColor(vordme_color);
    		g_vordme.setStroke(new BasicStroke(2.5f));
        	g_vordme.drawPolygon(x_points_hexagon, y_points_hexagon, 6);
        	g_vordme.drawPolygon(x_points_ul_leaf, y_points_ul_leaf, 4);
        	g_vordme.drawPolygon(x_points_ur_leaf, y_points_ur_leaf, 4);
        	g_vordme.drawPolygon(x_points_b_leaf, y_points_b_leaf, 4);
    	} else {
    		g_vordme.setStroke(new BasicStroke(1.5f*scaling_factor));
    		g_vordme.setColor(vordme_color);  
    		g_vordme.drawLine(x, y-c9, x, y+c9);
    		g_vordme.drawLine(x-c9, y, x+c9, y);
    		g_vordme.setColor(background_color);
    		g_vordme.fillOval(x-c5, y-c5, 2*c5, 2*c5);
    		g_vordme.setColor(vordme_color);
    		g_vordme.drawOval(x-c5, y-c5, 2*c5, 2*c5);
    	}
    	return vordme_image;
    }
   
    private BufferedImage create_DME_symbol(Color dme_color) {
        // Boeing : a sort-of-Y-symbol for a standalone DME or TACAN
    	// Airbus : circle
    	
    	int x = dme_shift_x;
    	int y = dme_shift_y;

        int x3 = Math.round(3.0f*scaling_factor);
        int x6 = Math.round(6.0f*scaling_factor);
        int x8 = Math.round(8.0f*scaling_factor);
        int x11 = Math.round(11.0f*scaling_factor);
        int c9 = Math.round(9.0f*scaling_factor);

        int y3 = x3;
        int y5 = Math.round(5.0f*scaling_factor);
        int y8 = x8;
        int y11 = Math.round(11.0f*scaling_factor);

        int x_points[] = { x+x6, x+x11, x+x8, x+x3, x-x3, x-x8, x-x11, x-x6, x-x3, x-x3,  x+x3,  x+x3 };
        int y_points[] = { y,    y-y3,  y-y8, y-y5, y-y5, y-y8, y-y3,  y,    y+y5, y+y11, y+y11, y+y5 };

        int shift=2; // Shift to avoid rendering being clipped 
        
    	BufferedImage dme_image = new BufferedImage(x*2+shift*2+1,y*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_dme = dme_image.createGraphics();
    	g_dme.setRenderingHints(rendering_hints);
    	
    	g_dme.setColor(dme_color);
    	if (boeing_style) { 
    		g_dme.setStroke(new BasicStroke(2.5f));	
    		g_dme.drawPolygon(x_points, y_points, 12);
    	} else {
    		g_dme.setStroke(new BasicStroke(1.5f*scaling_factor));
    		g_dme.drawOval(x-c9, y-c9, 2*c9, 2*c9);
    	}
        return dme_image;
    }

    
    private BufferedImage create_NDB_symbol(Color ndb_color) {
    	// Boeing : a small circle surrounded by dots
    	// Airbus : a triangle
    	
        // alternative 1: two concentric circles
        // g.drawOval(x-2,y-2,4,4);
        // g.drawOval(x-8,y-8,16,16);

        // alternative 2: a small circle surrounded by dots
    	int x = ndb_shift_x;
    	int y = ndb_shift_y;
    	float dots[] = { 1.0f, 2.0f };
        int c4 = Math.round(3.0f*scaling_factor);
        int c7 = Math.round(5.5f*scaling_factor);
        int c10 = Math.round(8.0f*scaling_factor);
        int x7 = Math.round(6.93f*scaling_factor);
        int y4 = Math.round(4.0f*scaling_factor);
        
        int shift=2; // Shift to avoid rendering being clipped 
        
    	BufferedImage ndb_image = new BufferedImage(x*2+shift*2+1,y*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_ndb = ndb_image.createGraphics();
    	g_ndb.setRenderingHints(rendering_hints);
    	g_ndb.setColor(ndb_color);
    	
    	if (boeing_style) { 
    		g_ndb.setStroke(new BasicStroke(2.0f));
    		g_ndb.drawOval(x-c4, y-c4, 2*c4, 2*c4);
    		g_ndb.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dots, 0.0f));
    		g_ndb.drawOval(x-c7, y-c7, 2*c7, 2*c7);
    		g_ndb.drawOval(x-c10, y-c10, 2*c10, 2*c10);
    	} else {
            int x_points_triangle[] = { x, x-x7, x+x7 };
            int y_points_triangle[] = { y-c10, y+y4, y+y4 };
    		g_ndb.setStroke(new BasicStroke(1.7f*scaling_factor));
    		g_ndb.drawPolygon(x_points_triangle, y_points_triangle, 3);
    	}
        return ndb_image;
    }
    
    private BufferedImage createFMSEntryStar(Color star_color) {
    	int x = ndb_shift_x;
    	int y = ndb_shift_y;
    	int s3 = Math.round(2.0f*scaling_factor);
    	int s13 = Math.round(12.0f*scaling_factor);
    	int c4 = Math.round(4.0f*scaling_factor);
    	int c6 = Math.round(6.0f*scaling_factor);
    	int x12 = Math.round(12.0f*scaling_factor);
    	int y12 = Math.round(12.0f*scaling_factor);
    	int x_points_star[] = { x-s3, x, x+s3, x+s13, x+s3, x, x-s3, x-s13, x-s3 };
    	int y_points_star[] = { y-s3, y-s13, y-s3, y, y+s3, y+s13, y+s3, y, y-s3 };
    	
        int shift=2; // Shift to avoid rendering being clipped 
        
    	BufferedImage star_image = new BufferedImage(x*2+shift*2+1,y*2+shift*2+1,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_star = star_image.createGraphics();
    	g_star.setRenderingHints(rendering_hints);
    	g_star.setColor(star_color);
    	g_star.drawPolygon(x_points_star, y_points_star, 9);
    	return star_image;
    }
    
    private BufferedImage createClipRoseAreaImage() {
    	BufferedImage clip_image = new BufferedImage(frame_size.width,frame_size.height,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_clip = clip_image.createGraphics();
    	g_clip.setRenderingHints(rendering_hints);
    	
		// Clear the buffered Image first
    	g_clip.setComposite(AlphaComposite.Clear);
    	g_clip.fillRect(0, 0, frame_size.width, frame_size.height);
    	g_clip.setComposite(AlphaComposite.SrcOver);
		
        if ( draw_only_inside_rose ) {
        	g_clip.setColor(background_color);
        	g_clip.fill(clip_rose_area);
            if ( limit_arcs && ! mode_plan && ! mode_centered ) {
            	g_clip.fillRect(0, 0, map_center_x - sixty_deg_hlimit, frame_size.height);
            	g_clip.fillRect(map_center_x + sixty_deg_hlimit, 0, map_center_x - sixty_deg_hlimit, frame_size.height);
            }
        } else {
            // leave at least the top of the window uncluttered
        	g_clip.setColor(background_color);
        	g_clip.fillRect(0,0, frame_size.width, rose_y_offset);
        }
        return clip_image;
    }
    
    private BufferedImage createTrackDiamond() {   
    	BufferedImage diamond_image = new BufferedImage(track_diamond_size,track_diamond_size*3/2,BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g_dmd = diamond_image.createGraphics();
    	// diamond
    	int d_d = track_diamond_shift - 1;
    	int track_x[] = {
    			track_diamond_shift,
    			track_diamond_shift + d_d,
    			track_diamond_shift,
    			track_diamond_shift - d_d
    	};
    	int track_y[] = {
    			1 ,
    			1 + d_d*3/2,
    			1 + 3*d_d,
    			1 + d_d*3/2	
    	};
    	g_dmd.setColor(pfd_active_color);
    	g_dmd.setStroke(new BasicStroke(2.0f * scaling_factor));
    	g_dmd.drawPolygon(track_x, track_y, 4);
    	return diamond_image;
    }
    
    public boolean display_inhibit() {
    	long current_time=current_time_millis-change_msg_duration;
    	boolean range_msg = map_range_timestamp > current_time;
    	boolean zoomin_msg = map_zoomin_timestamp > current_time; 
    	boolean mode_msg = map_mode_timestamp > current_time;
    	boolean submode_msg = map_submode_timestamp > current_time;
    	return range_msg || zoomin_msg || mode_msg || submode_msg;
    }
    
    public boolean display_range_change_msg() {
    	boolean range_msg = (map_range_timestamp+change_msg_duration) > current_time_millis;
    	boolean zoomin_msg = (map_zoomin_timestamp+change_msg_duration) > current_time_millis;
    	return range_msg || zoomin_msg;
    }
    
    public boolean display_mode_change_msg() {
    	boolean mode_msg = (map_mode_timestamp+change_msg_duration) > current_time_millis;
    	boolean submode_msg = (map_submode_timestamp+change_msg_duration) > current_time_millis;
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
