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
package net.sourceforge.xhsi.panel;

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

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.Avionics;


public class NDGraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private XHSIPreferences preferences;

    public static int INITIAL_PANEL_SIZE = 600;
    public static int INITIAL_BORDER_SIZE = 10;
    public static int INITIAL_EXPANDED_PLANE_Y_OFFSET = 100;
    public static int INITIAL_CENTER_BOTTOM = 55;

    public Font font_statusbar;
    public Font font_tiny;
    public Font font_small;
    public Font font_medium;
    public Font font_large;

    // for color inspiration: http://en.wikipedia.org/wiki/Internet_colors and http://en.wikipedia.org/wiki/X11_color_names

    // green
    public Color color_lime = new Color(0x00FF00);
    public Color color_limegreen = new Color(0x32CD32);
    public Color color_mediumspringgreen = new Color(0x00FA9A);
    public Color color_springgreen = new Color(0x00FF7F);
    public Color color_springbluegreen = new Color(0x00FF9F); // custom color
    public Color color_greenyellow = new Color(0xADFF2F);
    public Color color_yellowgreen = new Color(0x9ACD32);
    public Color color_green = new Color(0x008000);
    public Color color_lightgreen = new Color(0x90EE90);
    public Color color_darkgreen = new Color(0x006400);
    public Color color_seagreen = new Color(0x2E8B57);

    // orange
    public Color color_tomato = new Color(0xFF6347);

    // magenta
    public Color color_magenta = new Color(0xFF00FF);
    public Color color_orchid = new Color(0xDA70D6);
    public Color color_darkorchid = new Color(0x9933CC);
    public Color color_violet = new Color(0xEE82EE);
    public Color color_hotpink = new Color(0xFF69B4);
    public Color color_deeppink = new Color(0xFF1493);
    public Color color_lightpink = new Color(0xFFB6C1);

    // blue
    public Color color_dodgerblue = new Color(0x1E90FF);
    public Color color_powderblue = new Color(0xB0E0E6);
    public Color color_lavender = new Color(0xE6E6FA);
    public Color color_lightskyblue = new Color(0x87CEFA);
    public Color color_skyblue = new Color(0x87CEEB);
    public Color color_deepskyblue = new Color(0x00BFFF);
    public Color color_mediumslateblue = new Color(0x7B68EE);
    public Color color_mediumpurple = new Color(0x9370DB);
    public Color color_lightsteelblue = new Color(0xB0C4DE);
    public Color color_cornflowerblue = new Color(0x6495ED);

    // cyan
    public Color color_lightcyan = new Color(0xE0FFFF);
    public Color color_mediumcyan = new Color(0x80FFFF);
    public Color color_cyan = new Color(0x00FFFF);
    public Color color_darkcyan = new Color(0x008B8B);
    public Color color_teal = new Color(0x008080);
    public Color color_paleturquoise = new Color(0xAFEEEE);
    public Color color_turquoise = new Color(0x40E0D0);
    public Color color_mediumturquoise = new Color(0x48D1CC);
    public Color color_darkturquoise = new Color(0x00CED1);

    // the cyan on the Boeing displays is blue-er than our RGB cyan
    public Color color_boeingcyan = color_darkturquoise; // color_darkturquoise !

    // aquamarine
    public Color color_aquamarine = new Color(0x7FFFD4);
    public Color color_mediumaquamarine = new Color(0x66CDAA);
    public Color color_lightaquamarine = new Color(0xCCFFDD);

    // other
    public Color color_khaki = new Color(0xF0E68C);
    public Color color_darkkhaki = new Color(0xBDB76B);
    public Color color_olive = new Color(0x808000);
    public Color color_tan = new Color(0xD2B48C);
    public Color color_darktan = color_tan.darker(); // custom color
    public Color color_amber = new Color(0xFFB400);
    public Color color_sky = new Color(0x0066CC); // custom color
    public Color color_ground = new Color(0x663300); // custom color
    public Color color_redmagenta = new Color(0xFF00A0);
    public Color color_mediumviolet = new Color(0xC020FF);
    public Color color_darkermediumviolet = new Color(0xB800FD);
    public Color color_pastelhotpink = new Color(0xC8A0B4); // hotpink, with saturation at 60

    public Color color_poweroff =  Color.BLACK;

    // gray
    // RAL colors as found on http://www.tikkurila.com/industrial_coatings/metal_surfaces/ral_colour_cards/ral_classic_colour_card
    // Boeing doesn't use RAL colors, but RAL 7011 and 7040 come close enough
    public Color color_irongray = new Color(0x5A6066); // 737NG panel RAL7011
    public Color color_windowgray = new Color(0x9CA2AA); // 737NG knobs RAL7040

    // variables
    public Color background_color;
    public Color border_color;
    public GradientPaint border_gradient;
    public Color tuned_vor_color;
    public Color tuned_localizer_color;
    public Color reference_localizer_color;
    public Color receiving_localizer_color;
    public Color silent_localizer_color;
    public Color tuned_ndb_color;
    //public Color map_symbol_color;
    public Color navaid_color;
    public Color term_wpt_color;
    public Color wpt_color;
    public Color awy_wpt_color;
    public Color arpt_color;
    public Color holding_color;
    public Color traffic_color;
    public Color faraway_color;
    public Color pos_label_color;
    public Color tcas_label_color;
    public Color data_label_color;
    public Color fmc_active_color;
    public Color fmc_disp_color;
    public Color fmc_other_color;
    public Color altitude_arc_color;
    public Color fmc_ll_active_color;
    public Color fmc_ll_disp_color;
    public Color fmc_ll_other_color;
    public Color heading_labels_color;
    public Color vor_course_color;
    public Color needle_color;
    public Color deviation_scale_color;
    public Color rose_color;
    public Color range_arc_color;
    public Color dim_label_color;
    public Color no_rcv_ndb_color;
    public Color no_rcv_vor_color;
    public Color normal_color;
    public Color unusual_color;
    public Color caution_color;
    public Color warning_color;
    public Color aircraft_color;
    public Color heading_bug_color;
    public Color wind_color;
    public Color top_text_color;
    public Color grass_color;
    public Color hard_color;
    public Color sand_color;
    public Color snow_color;

    public int line_height_tiny;
    public int max_char_advance_tiny;
    public int line_height_small;
    public int max_char_advance_small;
    public int line_height_medium;
    public int max_char_advance_medium;
    public int line_height_large;
    public int max_char_advance_large;

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

    public Dimension component_size;
    public Dimension panel_size;
    public Point panel_topleft;
    public int rose_radius;
    public int rose_thickness;
    public int rose_y_offset;
    public int plane_y_offset;
    public int map_center_x;
    public int map_center_y;
    public float scaling_factor;
    public float shrink_scaling_factor;
    public float grow_scaling_factor;
    public float pixels_per_nm;
    public int border = INITIAL_BORDER_SIZE;
    public int border_left = border;
    public int border_right = border;
    public int border_top = border;
    public int border_bottom = border;
    public int pixel_distance_plane_bottom_screen;
    public int pixel_distance_plane_lower_left_corner;
    public float half_view_angle;
    public int big_tick_length;
    public int small_tick_length;
    public Area inner_rose_area;
    public Area instrument_frame;
    public RoundRectangle2D inner_round_rect;
    public Area instrument_outer_frame;
    public Map rendering_hints;

    private int map_mode;
    private int map_submode;

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
    public boolean avionics_power;

    public float max_range;

    private boolean resized = false;
    public boolean reconfig = true;
    public boolean reconfigured = true;



    public NDGraphicsConfig(Component root_component) {
        init();
    }


    public void init() {

        this.preferences = XHSIPreferences.get_instance();

        if ( preferences.get_panel_locked() ) {
            this.component_size = new Dimension( preferences.get_panel_width(), preferences.get_panel_height() );
            this.panel_size = new Dimension( preferences.get_panel_width(), preferences.get_panel_height() );
        } else {
            this.component_size = new Dimension(INITIAL_PANEL_SIZE + border_left + border_right, INITIAL_PANEL_SIZE + border_top + border_bottom);
            this.panel_size = new Dimension(INITIAL_PANEL_SIZE + border_left + border_right, INITIAL_PANEL_SIZE + border_top + border_bottom);
        }

        set_colors(false, false);
        
        border_color = color_irongray;
        border_gradient = new GradientPaint(
                0, 0, color_irongray.darker().darker(),
                panel_size.width/2, panel_size.height/2 , color_irongray.brighter().brighter(),
                true);

        this.rendering_hints = new HashMap();
        this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // VALUE_TEXT_ANTIALIAS_LCD_HRGB uses sub-pixel anti-aliasing, and is supposed to looks better than VALUE_TEXT_ANTIALIAS_ON on modern LCD dispalys
        // but I don't see any difference, and it doesn't work on JRE 5.
    }


    private void set_colors(boolean power, boolean custom_colors) {

        if ( ! power && preferences.get_use_avionics_power() ) {
            background_color = color_poweroff;
            navaid_color = color_poweroff;
            term_wpt_color = color_poweroff;
            wpt_color = color_poweroff;
            awy_wpt_color = color_poweroff;
            arpt_color = color_poweroff;
            tuned_localizer_color = color_poweroff;
            silent_localizer_color = color_poweroff;
            reference_localizer_color = color_poweroff;
            receiving_localizer_color = color_poweroff;
            tuned_ndb_color = color_poweroff;
            no_rcv_ndb_color = color_poweroff;
            tuned_vor_color = color_poweroff;
            no_rcv_vor_color = color_poweroff;
            holding_color = color_poweroff;
            traffic_color = color_poweroff;
            faraway_color = color_poweroff;
            pos_label_color = color_poweroff;
            tcas_label_color = color_poweroff;
            data_label_color = color_poweroff;
            fmc_active_color = color_poweroff;
            fmc_disp_color = color_poweroff;
            fmc_other_color = color_poweroff;
            altitude_arc_color = color_poweroff;
            fmc_ll_active_color = color_poweroff;
            fmc_ll_disp_color = color_poweroff;
            fmc_ll_other_color = color_poweroff;
            heading_labels_color = color_poweroff;
            vor_course_color = color_poweroff;
            needle_color = color_poweroff;
            deviation_scale_color = color_poweroff;
            rose_color = color_poweroff;
            range_arc_color = color_poweroff;
            dim_label_color = color_poweroff;
            normal_color = color_poweroff;
            unusual_color = color_poweroff;
            caution_color = color_poweroff;
            warning_color = color_poweroff;
            aircraft_color = color_poweroff;
            heading_bug_color = color_poweroff;
            wind_color = color_poweroff;
            top_text_color = color_poweroff;
            grass_color = color_poweroff;
            hard_color = color_poweroff;
            sand_color = color_poweroff;
            snow_color = color_poweroff;
        } else if ( custom_colors ) {
            background_color = Color.BLACK;
            navaid_color = color_boeingcyan; // color_cyan !
            term_wpt_color = color_cornflowerblue.darker(); // color_mediumslateblue.darker(); // color_deepskyblue.darker().darker(); // color_teal; ?
            wpt_color = color_cornflowerblue;
            awy_wpt_color = color_cornflowerblue.brighter(); // color_mediumslateblue.brighter(); // color_deepskyblue; // color_mediumcyan; ?
            arpt_color = color_mediumaquamarine; // color_lightskyblue; // color_mediumslateblue; // color_aquamarine ?
            tuned_localizer_color = color_aquamarine;
            silent_localizer_color = color_mediumaquamarine.darker().darker(); // color_mediumaquamarine.darker() ?
            reference_localizer_color = color_lightaquamarine; // color_lightaquamarine ?
            receiving_localizer_color = color_aquamarine.darker(); // color_aquamarine ?
            tuned_ndb_color = color_dodgerblue; // color_dodgerblue ?
            no_rcv_ndb_color = color_dodgerblue.darker();
            tuned_vor_color = color_lime; // color_lime !
            no_rcv_vor_color = color_lime.darker();
            holding_color = color_deeppink; // color_violet ?
            traffic_color = color_lightsteelblue; // color_cyan; // color_deepskyblue ?
            faraway_color = color_lightsteelblue.darker().darker();
            pos_label_color = color_boeingcyan.darker();
            tcas_label_color = color_lightsteelblue; // color_cyan; // color_deepskyblue ?
            data_label_color = color_pastelhotpink; // was: Color.LIGHT_GRAY // was: color_hotpink.darker()
            fmc_active_color = color_hotpink;
            fmc_disp_color = Color.WHITE;
            fmc_other_color = Color.LIGHT_GRAY;
            altitude_arc_color = color_yellowgreen;
            fmc_ll_active_color = color_yellowgreen.brighter();
            fmc_ll_disp_color = color_yellowgreen;
            fmc_ll_other_color = color_yellowgreen.darker();
            heading_labels_color = color_limegreen;
            vor_course_color = color_mediumviolet;
            needle_color = color_mediumviolet.darker();
            deviation_scale_color = Color.LIGHT_GRAY;
            rose_color = Color.WHITE;
            range_arc_color = Color.GRAY.darker();
            dim_label_color = Color.DARK_GRAY;
            normal_color = color_lightgreen;
            unusual_color = color_deepskyblue;
            caution_color = color_amber;
            warning_color = Color.RED;
            aircraft_color = Color.WHITE;
            heading_bug_color = color_magenta;
            wind_color = color_lavender.darker();
            top_text_color = Color.WHITE;
            grass_color = color_darkgreen;
            hard_color = Color.GRAY;
            sand_color = color_darktan;
            snow_color = Color.LIGHT_GRAY;
        } else {
            background_color = Color.BLACK;
            navaid_color = color_boeingcyan;
            term_wpt_color = color_boeingcyan;
            wpt_color = color_boeingcyan;
            awy_wpt_color = color_boeingcyan;
            arpt_color = color_boeingcyan;
            tuned_localizer_color = color_lime;
            silent_localizer_color = Color.GRAY;
            reference_localizer_color = Color.WHITE;
            receiving_localizer_color = Color.LIGHT_GRAY;
            tuned_ndb_color = color_dodgerblue;
            no_rcv_ndb_color = color_dodgerblue;
            tuned_vor_color = color_lime;
            no_rcv_vor_color = color_lime;
            holding_color = color_magenta;
            traffic_color = Color.WHITE;
            faraway_color = Color.DARK_GRAY;
            pos_label_color = color_boeingcyan;
            tcas_label_color = color_boeingcyan;
            data_label_color = color_boeingcyan;
            fmc_active_color = color_magenta;
            fmc_disp_color = Color.WHITE;
            fmc_other_color = Color.LIGHT_GRAY;
            altitude_arc_color = color_lime;
            fmc_ll_active_color = color_lime;
            fmc_ll_disp_color = color_lime;
            fmc_ll_other_color = color_lime;
            heading_labels_color = color_lime;
            vor_course_color = color_magenta;
            needle_color = color_magenta;
            deviation_scale_color = Color.LIGHT_GRAY;
            rose_color = Color.WHITE;
            range_arc_color = Color.GRAY.brighter();
            dim_label_color = Color.BLACK;
            normal_color = color_lightgreen;
            unusual_color = color_deepskyblue;
            caution_color = color_amber;
            warning_color = Color.RED;
            aircraft_color = Color.WHITE;
            heading_bug_color = color_magenta;
            wind_color = Color.WHITE;
            top_text_color = Color.WHITE;
            grass_color = color_darkgreen;
            hard_color = Color.GRAY;
            sand_color = color_darktan;
            snow_color = Color.LIGHT_GRAY;
        }

    }


    public void update_config(Graphics2D g2, int mode, int submode, int range, boolean power) {

        if (this.resized
                || this.reconfig
                || (this.map_mode != mode)
                || (this.map_submode != submode)
                || (this.map_range != range)
                || (this.avionics_power != power)
            ) {
            // one of the settings has been changed

            // remember the mode/submode/range and avionics power settings
            this.map_mode = mode;
            this.map_submode = submode;
            this.map_range = range;
            this.avionics_power = power;

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
            }
            hdg_up = mode_app || mode_vor;
            trk_up = ! ( hdg_up || mode_plan );

            // anti-aliasing
            this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            // define the colors
            set_colors(power, preferences.get_use_more_color());
            border_gradient = new GradientPaint(
                    0, 0, color_irongray.darker().darker(),
                    panel_size.width/2, panel_size.height/2 , color_irongray.brighter(),
                    true);

            // switch width and height if the instrument is displayed on its side
            if ( preferences.get_preference(XHSIPreferences.PREF_ND_ORIENTATION).equals(XHSIPreferences.ND_LEFT)
                    || preferences.get_preference(XHSIPreferences.PREF_ND_ORIENTATION).equals(XHSIPreferences.ND_RIGHT) ) {
                this.panel_size.height = this.component_size.width;
                this.panel_size.width = this.component_size.height;
            }

            // calculate coordinates
            this.scaling_factor = Math.min( (float)this.panel_size.height, (float)this.panel_size.width ) / 600.0f;
            // if the panel gets smaller than 600px, we _should_ try to reduce the size of fonts and images
            this.shrink_scaling_factor = Math.min(1.0f, this.scaling_factor);
            // things like the line thickness can grow when the panel gets bigger than 600px
            this.grow_scaling_factor = Math.max(1.0f, this.scaling_factor);
            // some screen elements like line widths and the border can grow with the power of 2
            if ( preferences.get_panel_locked() ) {
                this.border = preferences.get_panel_border();
            } else {
                this.border = (int) (INITIAL_BORDER_SIZE * Math.max(1.0f, Math.pow(Math.min((float) this.panel_size.width, (float) this.panel_size.height) / 600.0f, 2)));
            }
            this.border_left = this.border;
            this.border_right = this.border;
            this.border_top = this.border;
            this.border_bottom = this.border;
            if ( preferences.get_panel_square() ) {
                if ( this.panel_size.width > (this.panel_size.height ) ) {
                    this.border_left += ( this.panel_size.width - ( this.panel_size.height  ) ) / 2;
                    this.border_right = this.border_left;
                } else {
                    this.border_top += ( this.panel_size.height  - this.panel_size.width ) / 2;
                    this.border_bottom = this.border_top;
                }
            }

            // position of the plane and size of the rose
            this.map_center_x = this.panel_size.width / 2;
            this.rose_y_offset = 50 + 4 + this.border_top;
            //if ( ( (this.map_mode == Avionics.EFIS_MAP_CENTERED) && (this.map_submode != Avionics.EFIS_MAP_NAV) ) || (this.map_submode == Avionics.EFIS_MAP_PLN) ) {
            if ( this.mode_centered || this.mode_plan ) {
                // CENTERED (or PLAN)
                this.max_range = (float)this.map_range / 2.0f;
                this.map_center_y = (this.rose_y_offset + (this.panel_size.height  - this.border_bottom - INITIAL_CENTER_BOTTOM)) / 2;
                this.rose_radius = this.map_center_y - this.rose_y_offset;
                this.plane_y_offset = INITIAL_CENTER_BOTTOM + this.rose_radius;
            } else {
                // EXPANDED
                this.max_range = (float)this.map_range;
                this.plane_y_offset = (int) (INITIAL_EXPANDED_PLANE_Y_OFFSET * this.scaling_factor);
                this.map_center_y = this.panel_size.height - this.border_bottom - this.plane_y_offset;
                this.rose_radius = this.map_center_y - this.rose_y_offset;
            }
            this.pixels_per_nm = (float)this.rose_radius / this.max_range; // float for better precision

            // fonts
            // Verdana is easier to read than Lucida Sans, and available on Win, Mac and Lin
            if ( preferences.get_bold_fonts() ) {
                this.font_statusbar = new Font("Verdana", Font.PLAIN, 9);
                this.font_tiny = new Font( "Verdana", Font.BOLD, 10);
                this.font_small = new Font( "Verdana", Font.BOLD, 12);
                this.font_medium = new Font( "Verdana", Font.BOLD, 16);
                this.font_large = new Font( "Verdana", Font.PLAIN, 24);
            } else {
                this.font_statusbar = new Font("Verdana", Font.PLAIN, 9);
                this.font_tiny = new Font( "Verdana", Font.PLAIN, 10);
                this.font_small = new Font( "Verdana", Font.PLAIN, 12);
                this.font_medium = new Font( "Verdana", Font.PLAIN, 16);
                this.font_large = new Font( "Verdana", Font.PLAIN, 24);
            }

            // calculate font metrics
            // W is probably the largest characher...
            FontMetrics fm = g2.getFontMetrics(this.font_large);
            this.line_height_large = fm.getAscent();
            this.max_char_advance_large = fm.stringWidth("WW") - fm.stringWidth("W");
            fm = g2.getFontMetrics(this.font_medium);
            this.line_height_medium = fm.getAscent();
            this.max_char_advance_medium = fm.stringWidth("WW") - fm.stringWidth("W");
            fm = g2.getFontMetrics(this.font_small);
            this.line_height_small = fm.getAscent();
            this.max_char_advance_small = fm.stringWidth("WW") - fm.stringWidth("W");
            fm = g2.getFontMetrics(this.font_tiny);
            this.line_height_tiny = fm.getAscent();
            this.max_char_advance_tiny = fm.stringWidth("WW") - fm.stringWidth("W");

            // labels at the left
            this.left_label_x = this.border_left + 10;
            this.left_label_arpt_y = Math.max( this.panel_size.height - this.border_bottom - 240, this.panel_size.height/2 + 24 );
            this.left_label_wpt_y = this.left_label_arpt_y + this.line_height_small + 3;
            this.left_label_vor_y = this.left_label_wpt_y + this.line_height_small + 3;
            this.left_label_ndb_y = this.left_label_vor_y + this.line_height_small + 3;
            this.left_label_pos_y = this.left_label_ndb_y + this.line_height_small + 3;
            this.left_label_data_y = this.left_label_pos_y + this.line_height_small + 3;
            this.left_label_tfc_y = this.left_label_data_y + this.line_height_small + 3;
            this.left_label_taonly_y = this.left_label_tfc_y + this.line_height_tiny + 2;
            this.left_label_xpdr_y = this.left_label_taonly_y + this.line_height_tiny + 2;

            // labels at the right
            this.right_label_x = this.panel_size.width - this.border_right - 20;
            this.right_label_tcas_y = this.panel_size.height * 7 / 16;
            this.right_label_disagree_y = this.panel_size.height / 3;

            // calculate pixel distances. Needed for determining which
            // part of the rose needs to be drawn
            pixel_distance_plane_bottom_screen = this.panel_size.height - this.map_center_y ;
            pixel_distance_plane_lower_left_corner =
                    (int) Math.sqrt(
                    Math.pow(this.pixel_distance_plane_bottom_screen, 2) +
                    Math.pow(this.panel_size.width / 2, 2));

            // compass rose ticks get shorter when the frame is smaller than 600px
            this.big_tick_length = (int) (20 * shrink_scaling_factor);
            this.small_tick_length = this.big_tick_length / 3;

            if (this.pixel_distance_plane_bottom_screen >= (this.rose_radius - this.big_tick_length)) {
                // Complete rose
                this.half_view_angle = 180.0f;
            } else if (this.pixel_distance_plane_lower_left_corner > (this.rose_radius - this.big_tick_length)) {
                // Rose visible below aircraft position
                half_view_angle = (float) (180.0f - Math.toDegrees(Math.acos((1.0f * pixel_distance_plane_bottom_screen) / (1.0f * (this.rose_radius - this.big_tick_length)))));
            } else {
                // Rose visible only above aircraft position
                half_view_angle = (float) (90.0f - Math.toDegrees(Math.acos((1.0f * this.panel_size.width) / (2.0f * (this.rose_radius - this.big_tick_length)))));
            }

            rose_thickness = 4;
            this.inner_rose_area = new Area(new Ellipse2D.Float(
                    map_center_x - rose_radius + rose_thickness,
                    map_center_y - rose_radius + rose_thickness,
                    (rose_radius * 2) - (rose_thickness * 2),
                    (rose_radius * 2) - (rose_thickness * 2)));

            // a nice frame with rounded corners; it will be painted in InstrumentFrame.java
            Area inner_frame = new Area(new RoundRectangle2D.Float(
                    border_left,
                    border_top,
                    panel_size.width - (border_left + border_right),
                    panel_size.height - (border_top + border_bottom),
                    (int)(30 * this.grow_scaling_factor),
                    (int)(30 * this.grow_scaling_factor)));
            instrument_frame = new Area(new Rectangle2D.Float(0, 0, panel_size.width, panel_size.height));
            instrument_frame.subtract(inner_frame);
            // if the cpu can handle it: a double border
            Area outer_frame = new Area(new RoundRectangle2D.Float(
                    border_left - border_left / 3,
                    border_top - border_top / 3,
                    panel_size.width - (border_left + border_right) + border_left / 3 + border_right / 3,
                    panel_size.height - (border_top + border_bottom) + border_top / 3 + border_bottom / 3,
                    (int)(30 * this.grow_scaling_factor),
                    (int)(30 * this.grow_scaling_factor)));
            instrument_outer_frame = new Area(new Rectangle2D.Float(0, 0, panel_size.width, panel_size.height));
            instrument_outer_frame.subtract(outer_frame);

            // clear the flags
            this.resized = false;
            this.reconfig = false;
            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

        }

    }


    public int get_text_width(Graphics graphics, Font font, String text) {
        return graphics.getFontMetrics(font).stringWidth(text);
    }


    public int get_text_height(Graphics graphics, Font font) {
        return graphics.getFontMetrics(font).getHeight();
    }


    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.panel_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent event) {
        this.panel_topleft = event.getComponent().getLocation();
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
