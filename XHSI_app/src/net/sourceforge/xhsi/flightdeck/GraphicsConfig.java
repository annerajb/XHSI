/**
 * GraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 * General graphics config:
 * - fonts
 * - colors
 * - ...
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2018 Nicolas Carel
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
package net.sourceforge.xhsi.flightdeck;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
// import java.util.logging.Logger;
import java.util.Map;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.util.ColorUtilities;


public class GraphicsConfig implements ComponentListener {

    // private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public static int INITIAL_PANEL_SIZE = 560;
    public static int INITIAL_BORDER_SIZE = 16;


    public XHSIPreferences preferences;
    public XHSISettings settings;

    // for color inspiration: http://en.wikipedia.org/wiki/Internet_colors and http://en.wikipedia.org/wiki/X11_color_names

    /*
     * ====================================================================
     * This is the static color definitions section for XHSI Display Units
     * These values should not be use in display units classes
     * and are reserved for this.set_colors() method. 
     * ====================================================================
     */
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
    public Color color_palegreen = new Color(0x98FB98);
    public Color color_verypalegreen = new Color(0xCCFFCC);
    public Color color_darkpalegreen = new Color(0x227722);
    public Color color_verydarkgreen = new Color(0x003400);

    // red, orange, brown
    public Color color_darkred = new Color(0x8B0000);
    public Color color_saddlebrown = new Color(0x8B4513);
    public Color color_brown = new Color(0xA52A2A);
    public Color color_maroon = new Color(0x800000);
    public Color color_tomato = new Color(0xFF6347);
    public Color color_rosybrown = new Color(0xBC8F8F);

    // magenta
    public Color color_magenta = new Color(0xFF00FF);
    public Color color_orchid = new Color(0xDA70D6);
    public Color color_darkorchid = new Color(0x9933CC);
    public Color color_violet = new Color(0xEE82EE);
    public Color color_hotpink = new Color(0xFF69B4);
    public Color color_deeppink = new Color(0xFF1493);
    public Color color_lightpink = new Color(0xFFB6C1);
    public Color color_indigo = new Color(0x4B0082);
    public Color color_slateblue = new Color(0x6A5ACD);

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
    public Color color_midnightblue = new Color(0x191970);
    public Color color_cadetblue = new Color(0x5F9EA0);
    public Color color_labelblue = new Color(0x009EDE);

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
    public Color color_slategray = new Color(0x708090);
    public Color color_khaki = new Color(0xF0E68C);
    public Color color_darkkhaki = new Color(0xBDB76B);
    public Color color_olive = new Color(0x808000);
    public Color color_tan = new Color(0xD2B48C);
    public Color color_darktan = color_tan.darker(); // custom color
    public Color color_amber = new Color(0xFFB400);
    public Color color_sky = new Color(0x0088FF); // custom color, was 0x0066CC
    public Color color_ground = new Color(0x884400); // custom color, was 0x006633
    public Color color_redmagenta = new Color(0xFF00A0);
    public Color color_mediumviolet = new Color(0xC020FF);
    public Color color_darkermediumviolet = new Color(0xB800FD);
    public Color color_pastelhotpink = new Color(0xC8A0B4); // hotpink, but with saturation at 60
    public Color color_purplegray = new Color(0x6E5A6E); // custom color
    public Color color_bluegray = new Color(0x404068); // custom color, was 0x5A5A78, 0x57608B
    public Color color_brightspringgreen = color_springgreen.brighter();

    // Gray
    public Color color_verylightgray = new Color(0xD8D8D8);

    public Color color_poweroff =  Color.BLACK;

    // B747-NG gray
    // RAL colors as found on http://www.tikkurila.com/industrial_coatings/metal_surfaces/ral_colour_cards/ral_classic_colour_card
    // Boeing doesn't use RAL colors, but RAL 7011 and 7040 come close enough
    public Color color_irongray = new Color(0x5A6066); // 737NG panel RAL7011
    public Color color_darkirongray = color_irongray.darker().darker();
    public Color color_windowgray = new Color(0x9CA2AA); // 737NG knobs RAL7040

    // B747-400 brown
    public Color color_jumbobrown = new Color(0x947D5B);
    public Color color_jumbodarkbrown = new Color(0x302720);
    public Color color_jumbolightbrown = new Color(0xA9A397);
    
    // Airbus blue
    public Color color_airbusback = new Color(0x99B0B3); // was : 0x98B0BC
    public Color color_airbusfront = new Color(0x516B7D); // was : 0x878E89
    public Color color_airbusknob = new Color(0xD1D2D3); // was : 0xEDEDEB
    
    // Airbus grey
    public Color color_airbusgray = new Color(0x585860); // was: 0x7f7f87

    // Airbus PFD Colors
    public Color color_airbus_selected = Color.cyan;
    public Color color_airbus_armed = Color.cyan;
    public Color color_airbus_managed = Color.magenta;
    public Color color_airbussky = new Color(0x10A0FF); 
 
    // Airbus Misc Colors
    public Color color_airbus_button = new Color(0x131917);
    public Color color_airbus_button_on = new Color(0x50A0C7); // was: 2F3C80 (too dark)
    public Color color_airbus_button_off = new Color(0xFFFFFF);
    public Color color_airbus_button_avail = new Color(0x6F7F4D);
    public Color color_airbus_button_smoke = new Color(0xFF0000);
    public Color color_airbus_button_fault = new Color(0x988148);
    
    // Failed Red Cross Color - XHSI COMM LOST
    public Color xhsi_comm_lost_color;
    
    /*
     * =========================================================================
     * This is the variable color definitions section for XHSI Display Units
     * These values must used in display units classes. 
     * These values may be adjusted in brightness by each DU brightness setting
     * Some of them may be adjusted with ambient and cockpit lights for panels
     * =========================================================================
     */
    
    // Weather Radar Colors
    public Color wxr_colors[] = new Color[10];
     
    // variables
    public Color backpanel_color;
    public Color frontpanel_color;
    public Color knobs_color;
    public Color background_color;
//    public Color border_color;
    public GradientPaint border_gradient;
    public Color tuned_vor_color;
    public Color tuned_localizer_color;
    public Color reference_localizer_color;
    public Color receiving_localizer_color;
    public Color silent_localizer_color;
    public Color tuned_ndb_color;
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
    public Color terrain_label_color;
    public Color fmc_active_color;
    public Color fmc_disp_color;
    public Color fmc_other_color;
    public Color altitude_arc_color;
    public Color fmc_ll_active_color;
    public Color fmc_ll_disp_color;
    public Color fmc_ll_other_color;
    public Color heading_labels_color;
    public Color cardinal_labels_color;
    public Color nav_needle_color;
    public Color deviation_scale_color;
    public Color markings_color;
    public Color dim_markings_color;
    public Color range_arc_color;
    public Color range_label_color;
    public Color label_color;
    public Color dim_label_color;
    public Color no_rcv_ndb_color;
    public Color no_rcv_vor_color;
    public Color unknown_nav_color;
    public Color normal_color;
    public Color unusual_color;
    public Color caution_color;
    public Color warning_color;
    public Color aircraft_color;
    public Color heading_bug_color;
    public Color wind_color;
    public Color efb_color;
    public Color top_text_color;
    public Color grass_color;
    public Color hard_color;
    public Color sand_color;
    public Color snow_color;
    public Color sky_color;
    public Color brightsky_color;
    public Color ground_color;
    public Color brightground_color;
    public Color instrument_background_color;
    public Color fpv_color;
    public Color chrono_background_color;
    public Color chrono_color;
    
    // ECAM COLORS - used for ECAM/EICAS and MFD
    public Color ecam_caution_color;
    public Color ecam_warning_color;
    public Color ecam_normal_color;
    public Color ecam_markings_color;
    public Color ecam_action_color;
    public Color ecam_special_color;
    public Color ecam_reference_color;
    public Color ecam_box_bg_color;
    
    // PFD colors - used to managed PFD brightness
    public Color pfd_armed_color;
    public Color pfd_managed_color;
    public Color pfd_selected_color;
    public Color pfd_box_color;
    public Color pfd_ils_color;    
    public Color pfd_vsi_needle_color;
    public Color pfd_instrument_background_color;
    public Color pfd_markings_color;
    public Color pfd_sky_color;
    public Color pfd_ground_color;
    public Color pfd_radio_alti_color;
    public Color pfd_reference_color;
    public Color pfd_mach_color;
    public Color pfd_alti_color;
    public Color pfd_active_color;
    public Color pfd_caution_color;
    public Color pfd_alarm_color;
    
    // Adjusting Colors     
    public Color panel_background_color;
    public Color panel_ohp_text_color;
    public Color panel_main_text_color;
    public Color panel_display_ohp_color;
    public Color panel_display_main_color;
    public Color panel_display_color;
    public Color ohp_green_lines_color;
    
    // CDU colors
    public Color cdu_title_color;
    public Color cdu_data_color;
    public Color cdu_scratch_pad_color;
    
    // Terrain colors
    public Color terrain_red_color;
    public Color terrain_bright_yellow_color;
    public Color terrain_yellow_color;
    public Color terrain_green_color;
    public Color terrain_dark_green_color;
    public Color terrain_bright_green_color;
    public Color terrain_blue_color;
    public Color terrain_black_color;
    
    // Clock colors
    public Color clock_color;
    public Color clock_markings_color;
    public Color clock_label_color;
    public Color clock_digital_54616E_color;
    public Color clock_digital_54616E_dark_color;
    public Color clock_digital_dark_gray_color;

    /*
     * =========================================================================
     * This is the font definitions section for XHSI Display Units
     * These values must used in display units classes. 
     * =========================================================================
     */

    String font_name = "Verdana";   
    public Font font_statusbar;

    public Font font_tiny;
    public int line_height_tiny;
    public int max_char_advance_tiny;
    public int digit_width_tiny;

    public Font font_small;
    public int line_height_small;
    public int max_char_advance_small;
    public int digit_width_small;

    public Font font_medium;
    public int line_height_medium;
    public int max_char_advance_medium;
    public int digit_width_medium;

    public Font font_large;
    public int line_height_large;
    public int max_char_advance_large;
    public int digit_width_large;

    public Font font_zl;
    public int line_height_zl;
    public int max_char_advance_zl;
    public int digit_width_zl;

    public Font font_xxxl;
    public int line_height_xxxl;
    public int max_char_advance_xxxl;
    public int digit_width_xxxl;

    public Font font_xxl;
    public int line_height_xxl;
    public int max_char_advance_xxl;
    public int digit_width_xxl;

    public Font font_xl;
    public int line_height_xl;
    public int max_char_advance_xl;
    public int digit_width_xl;

    public Font font_l;
    public int line_height_l;
    public int max_char_advance_l;
    public int digit_width_l;

    public Font font_m;
    public int line_height_m;
    public int max_char_advance_m;
    public int digit_width_m;

    public Font font_s;
    public int line_height_s;
    public int max_char_advance_s;
    public int digit_width_s;

    public Font font_xs;
    public int line_height_xs;
    public int max_char_advance_xs;
    public int digit_width_xs;

    public Font font_xxs;
    public int line_height_xxs;
    public int max_char_advance_xxs;
    public int digit_width_xxs;

    public Font font_xxxs;
    public int line_height_xxxs;
    public int max_char_advance_xxxs;
    public int digit_width_xxxs;

    public Font font_normal;
    public int line_height_normal;
    public int max_char_advance_normal;
    public int digit_width_normal;

    // Fixed font 
    String font_fixed_name = "Andale Mono";   
    
    public Font font_fixed_zl;
    public int line_height_fixed_zl;
    public int max_char_advance_fixed_zl;
    public int digit_width_fixed_zl;

    public Font font_fixed_xxxl;
    public int line_height_fixed_xxxl;
    public int max_char_advance_fixed_xxxl;
    public int digit_width_fixed_xxxl;
    
    public Font font_fixed_xxl;
    public int line_height_fixed_xxl;
    public int max_char_advance_fixed_xxl;
    public int digit_width_fixed_xxl;

    public Font font_fixed_xl;
    public int line_height_fixed_xl;
    public int max_char_advance_fixed_xl;
    public int digit_width_fixed_xl;

    public Font font_fixed_l;
    public int line_height_fixed_l;
    public int max_char_advance_fixed_l;
    public int digit_width_fixed_l;

    public Font font_fixed_m;
    public int line_height_fixed_m;
    public int max_char_advance_fixed_m;
    public int digit_width_fixed_m;
    
    public Dimension component_size;
    public Dimension frame_size;
    public Point component_topleft;
    public Rectangle panel_rect;

    public float scaling_factor;
    public float shrink_scaling_factor;
    public float grow_scaling_factor;

    public int border = 0;
    public int border_left = border;
    public int border_right = border;
    public int border_top = border;
    public int border_bottom = border;

    public Area instrument_frame;
    public RoundRectangle2D inner_round_rect;
    public Area instrument_outer_frame;
    public Map<RenderingHints.Key, Object> rendering_hints;

    public boolean resized = false;
    public boolean reconfig = true;
    public boolean reconfigured = true;
    public long reconfigured_timestamp=0;
    public boolean colors_updated = false;

    public int display_unit;
    
    /*
     * Light and Brightness
     */
    public float du_brightness;
    // TODO: Cockpit light_color ?
    public float cockpit_light;
    public float outside_light;

    public boolean powered;
    public int cdu_source;
    
    public int style;
    public boolean airbus_style;
    public boolean boeing_style;
    public boolean custom_style;
    public boolean unknown_style;

    public boolean symbols_multiselection;
    
    /*
     *  Avoid calling System.currentTimeMillis() more than one time per DU refresh
     *  This system call costs CPU / this variable acts as a cached value
     *  This value is updated in the DU component paint() function
     */
    public long current_time_millis;
    


    public GraphicsConfig(Component root_component) {
        // our children will call init()
        //init();
    }


    public void init() {

        this.preferences = XHSIPreferences.get_instance();
        this.settings = XHSISettings.get_instance();
        
        // Setup startup brightness
        du_brightness=1.0f;
        
        // Setup instrument style
        style = this.settings.style;
        airbus_style = ( style == Avionics.STYLE_AIRBUS );
        boeing_style = ( style == Avionics.STYLE_BOEING );
        unknown_style = ( !airbus_style & !boeing_style); // possible if dataref tool used to set an unknown value 
        
        // Symbols color schema
        symbols_multiselection = preferences.get_symbols_multiselection();
        
        // Reset system timer cache 
        current_time_millis = 0;
        
        set_colors(false, XHSIPreferences.BORDER_GRAY);

        this.rendering_hints = new HashMap<RenderingHints.Key, Object>();
        this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        // VALUE_TEXT_ANTIALIAS_LCD_HRGB uses sub-pixel anti-aliasing, and is supposed to looks better than VALUE_TEXT_ANTIALIAS_ON on modern LCD dispalys
        // but I don't see any difference, and it doesn't work on JRE 5.

        if ( preferences.get_panels_locked() ) {
            this.component_size = new Dimension( preferences.get_panel_width(this.display_unit), preferences.get_panel_height(this.display_unit) );
            this.frame_size = new Dimension( preferences.get_panel_width(this.display_unit), preferences.get_panel_height(this.display_unit) );
        } else {
            this.component_size = new Dimension(INITIAL_PANEL_SIZE + border_left + border_right, INITIAL_PANEL_SIZE + border_top + border_bottom);
            this.frame_size = new Dimension(INITIAL_PANEL_SIZE + border_left + border_right, INITIAL_PANEL_SIZE + border_top + border_bottom);
        }

//        border_color = backpanel_color;
        border_gradient = new GradientPaint(
                0, 0, backpanel_color.darker().darker().darker(),
                this.frame_size.width, this.frame_size.height , backpanel_color.brighter(),
                true);

    }


    public void set_fonts(Graphics2D g2, float scale) {

            // fonts
            // Verdana is easier to read than Lucida Sans, and available on Win, Mac and Lin
            if ( XHSIPreferences.get_instance().get_bold_fonts() ) {
                this.font_statusbar = new Font(this.font_name, Font.PLAIN, 9);
                this.font_tiny = new Font( this.font_name, Font.BOLD, 10);
                this.font_small = new Font( this.font_name, Font.BOLD, 12);
                this.font_medium = new Font( this.font_name, Font.BOLD, 16);
                this.font_large = new Font( this.font_name, Font.PLAIN, 24);
                this.font_zl = new Font( this.font_name, Font.PLAIN, Math.round(64.0f * scale));
                this.font_xxxl = new Font( this.font_name, Font.PLAIN, Math.round(32.0f * scale));
                this.font_xxl = new Font( this.font_name, Font.BOLD, Math.round(24.0f * scale));
                this.font_xl = new Font( this.font_name, Font.BOLD, Math.round(21.0f * scale));
                this.font_l = new Font( this.font_name, Font.BOLD, Math.round(18.0f * scale));
                this.font_m = new Font( this.font_name, Font.BOLD, Math.round(16.0f * scale));
                this.font_s = new Font( this.font_name, Font.BOLD, Math.round(14.0f * scale));
                this.font_xs = new Font( this.font_name, Font.BOLD, Math.round(12.0f * scale));
                this.font_xxs = new Font( this.font_name, Font.BOLD, Math.round(10.0f * scale));
                this.font_xxxs = new Font( this.font_name, Font.BOLD, Math.round(8.0f * scale));
                this.font_normal = new Font( this.font_name, Font.BOLD, Math.round(14.0f * scale));
            } else {
                this.font_statusbar = new Font(this.font_name, Font.PLAIN, 9);
                this.font_tiny = new Font( this.font_name, Font.PLAIN, 10);
                this.font_small = new Font( this.font_name, Font.PLAIN, 12);
                this.font_medium = new Font( this.font_name, Font.PLAIN, 16);
                this.font_large = new Font( this.font_name, Font.PLAIN, 24);
                this.font_zl = new Font( this.font_name, Font.PLAIN, Math.round(64.0f * scale));
                this.font_xxxl = new Font( this.font_name, Font.PLAIN, Math.round(32.0f * scale));
                this.font_xxl = new Font( this.font_name, Font.PLAIN, Math.round(24.0f * scale));
                this.font_xl = new Font( this.font_name, Font.PLAIN, Math.round(21.0f * scale));
                this.font_l = new Font( this.font_name, Font.PLAIN, Math.round(18.0f * scale));
                this.font_m = new Font( this.font_name, Font.PLAIN, Math.round(16.0f * scale));
                this.font_s = new Font( this.font_name, Font.PLAIN, Math.round(14.0f * scale));
                this.font_xs = new Font( this.font_name, Font.PLAIN, Math.round(12.0f * scale));
                this.font_xxs = new Font( this.font_name, Font.PLAIN, Math.round(10.0f * scale));
                this.font_xxxs = new Font( this.font_name, Font.PLAIN, Math.round(8.0f * scale));
                this.font_normal = new Font( this.font_name, Font.PLAIN, Math.round(14.0f * scale));
            }

            // calculate font metrics
            // W is probably the largest characher...
            FontMetrics fm;

            fm = g2.getFontMetrics(this.font_large);
            this.line_height_large = fm.getAscent();
            this.max_char_advance_large = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_large =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_medium);
            this.line_height_medium = fm.getAscent();
            this.max_char_advance_medium = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_medium =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_small);
            this.line_height_small = fm.getAscent();
            this.max_char_advance_small = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_small =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_tiny);
            this.line_height_tiny = fm.getAscent();
            this.max_char_advance_tiny = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_tiny =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_zl);
            this.line_height_zl = fm.getAscent();
            this.max_char_advance_zl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_zl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xxxl);
            this.line_height_xxxl = fm.getAscent();
            this.max_char_advance_xxxl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xxxl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xxl);
            this.line_height_xxl = fm.getAscent();
            this.max_char_advance_xxl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xxl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xl);
            this.line_height_xl = fm.getAscent();
            this.max_char_advance_xl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_l);
            this.line_height_l = fm.getAscent();
            this.max_char_advance_l = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_l =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_m);
            this.line_height_m = fm.getAscent();
            this.max_char_advance_m = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_m =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_s);
            this.line_height_s = fm.getAscent();
            this.max_char_advance_s = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_s =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xs);
            this.line_height_xs = fm.getAscent();
            this.max_char_advance_xs = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xs =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xxs);
            this.line_height_xxs = fm.getAscent();
            this.max_char_advance_xxs = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xxs =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_xxxs);
            this.line_height_xxxs = fm.getAscent();
            this.max_char_advance_xxxs = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_xxxs =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_normal);
            this.line_height_normal = fm.getAscent();
            this.max_char_advance_normal = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_normal =  fm.stringWidth("88") - fm.stringWidth("8");

            // Fixed fonts
            this.font_fixed_zl = new Font( this.font_fixed_name, Font.PLAIN, Math.round(41.0f * scale));
            this.font_fixed_xxxl = new Font( this.font_fixed_name, Font.PLAIN, Math.round(31.0f * scale));
            this.font_fixed_xxl = new Font( this.font_fixed_name, Font.PLAIN, Math.round(27.0f * scale));
            this.font_fixed_xl = new Font( this.font_fixed_name, Font.PLAIN, Math.round(23.0f * scale));
            this.font_fixed_l = new Font( this.font_fixed_name, Font.PLAIN, Math.round(20.0f * scale));
            this.font_fixed_m = new Font( this.font_fixed_name, Font.PLAIN, Math.round(16.0f * scale));
            
            // Get metrics for fixed fonts
            fm = g2.getFontMetrics(this.font_fixed_zl);
            this.line_height_fixed_zl = fm.getAscent();
            this.max_char_advance_fixed_zl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_fixed_zl =  fm.stringWidth("88") - fm.stringWidth("8");
            
            fm = g2.getFontMetrics(this.font_fixed_xxxl);
            this.line_height_fixed_xxxl = fm.getAscent();
            this.max_char_advance_fixed_xxxl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_fixed_xxxl =  fm.stringWidth("88") - fm.stringWidth("8");
            
            fm = g2.getFontMetrics(this.font_fixed_xxl);
            this.line_height_fixed_xxl = fm.getAscent();
            this.max_char_advance_fixed_xxl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_fixed_xxl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_fixed_xl);
            this.line_height_fixed_xl = fm.getAscent();
            this.max_char_advance_fixed_xl = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_fixed_xl =  fm.stringWidth("88") - fm.stringWidth("8");

            fm = g2.getFontMetrics(this.font_fixed_l);
            this.line_height_fixed_l = fm.getAscent();
            this.max_char_advance_fixed_l = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_fixed_l =  fm.stringWidth("88") - fm.stringWidth("8");
            
            fm = g2.getFontMetrics(this.font_fixed_m);
            this.line_height_fixed_m = fm.getAscent();
            this.max_char_advance_fixed_m = fm.stringWidth("WW") - fm.stringWidth("W");
            this.digit_width_fixed_m =  fm.stringWidth("88") - fm.stringWidth("8");
    }


    public void set_colors(boolean custom_colors, String border_color) {

        // Weather radar colors
        if ( preferences.get_nd_wxr_color_gradient() ) {
        	wxr_colors[0] = setDUBrightness(new Color(0,0,0));
        	wxr_colors[1] = setDUBrightness(new Color(0,5,0));
        	wxr_colors[2] = setDUBrightness(new Color(0,40,0));
        	wxr_colors[3] = setDUBrightness(new Color(0,100,0));
        	wxr_colors[4] = setDUBrightness(new Color(0,120,0));
        	wxr_colors[5] = setDUBrightness(new Color(120,120,0));
        	wxr_colors[6] = setDUBrightness(new Color(140,140,0));
        	wxr_colors[7] = setDUBrightness(new Color(140,0,0));
        	wxr_colors[8] = setDUBrightness(new Color(160,0,0));
        	wxr_colors[9] = setDUBrightness(new Color(160,0,160));       	
        } else {
        	wxr_colors[0] = setDUBrightness(new Color(0,0,0));
        	wxr_colors[1] = setDUBrightness(new Color(0,0,0));
        	wxr_colors[2] = setDUBrightness(new Color(0,0,0));
        	wxr_colors[3] = setDUBrightness(new Color(0,110,0));
        	wxr_colors[4] = setDUBrightness(new Color(0,140,0));
        	wxr_colors[5] = setDUBrightness(new Color(140,140,0));
        	wxr_colors[6] = setDUBrightness(new Color(150,150,0));
        	wxr_colors[7] = setDUBrightness(new Color(150,0,0));
        	wxr_colors[8] = setDUBrightness(new Color(150,0,0));
        	wxr_colors[9] = setDUBrightness(new Color(160,0,160));
        }
        
        // XHSI COMM LOST Failed red cross or communications lost message (Full Brightness)
        xhsi_comm_lost_color = Color.red;
        
        // Terrain colors
        terrain_red_color = setDUBrightness(new Color(110,0,0));
        terrain_bright_yellow_color = setDUBrightness(new Color(105,105,0));
        terrain_yellow_color = setDUBrightness(new Color(75,75,0));        
        terrain_green_color = setDUBrightness(new Color(0,70,0));
        terrain_dark_green_color = setDUBrightness(new Color(0,40,0));
        terrain_bright_green_color = setDUBrightness(new Color(0,105,0));
        terrain_blue_color = setDUBrightness(new Color(0,0,80));
        terrain_black_color = setDUBrightness(Color.black);
        
        if ( custom_colors ) {
            background_color = Color.BLACK;
                       
            /*
             *  Custom colors: Navigation Display
             */
            if ( airbus_style ) {
            	/*
            	 * Custom colors Airbus Style 
            	 */
            	
                navaid_color = setDUBrightness(color_boeingcyan);
                term_wpt_color = setDUBrightness(Color.magenta);
                wpt_color = setDUBrightness(color_cornflowerblue);
                awy_wpt_color = setDUBrightness(Color.magenta.brighter());
                arpt_color = setDUBrightness(Color.magenta);
                
                tuned_localizer_color = setDUBrightness(color_aquamarine);
                silent_localizer_color = setDUBrightness(color_mediumaquamarine.darker().darker());
                reference_localizer_color = setDUBrightness(color_lightaquamarine);
                receiving_localizer_color = setDUBrightness(color_aquamarine.darker());
                tuned_ndb_color = setDUBrightness(Color.GREEN);
                no_rcv_ndb_color = setDUBrightness(Color.GREEN.darker());
                tuned_vor_color = setDUBrightness(Color.WHITE);
                no_rcv_vor_color = setDUBrightness(Color.WHITE.darker());
                unknown_nav_color = setDUBrightness(color_cadetblue);
                holding_color = setDUBrightness(color_deeppink);
                traffic_color = setDUBrightness(color_lightsteelblue);
                faraway_color = setDUBrightness(color_lightsteelblue.darker().darker());
                pos_label_color = setDUBrightness(color_boeingcyan.darker());
                tcas_label_color = setDUBrightness(color_lightsteelblue);
                data_label_color = setDUBrightness(color_pastelhotpink);
                terrain_label_color = setDUBrightness(Color.cyan);
                fmc_active_color = setDUBrightness(Color.GREEN);
                fmc_disp_color = setDUBrightness(Color.WHITE);
                fmc_other_color = setDUBrightness(Color.GREEN).darker();
                altitude_arc_color = setDUBrightness(color_yellowgreen);
                fmc_ll_active_color = setDUBrightness(color_yellowgreen.brighter());
                fmc_ll_disp_color = setDUBrightness(color_yellowgreen);
                fmc_ll_other_color = setDUBrightness(color_yellowgreen.darker());
                heading_labels_color = setDUBrightness(color_limegreen);
                cardinal_labels_color = setDUBrightness(Color.WHITE);
                nav_needle_color = setDUBrightness(color_mediumviolet);
                deviation_scale_color = setDUBrightness(Color.LIGHT_GRAY);
                range_arc_color = setDUBrightness(Color.WHITE); 
                range_label_color = setDUBrightness(Color.cyan);
                aircraft_color = setDUBrightness(Color.YELLOW);
                chrono_background_color = setDUBrightness(color_airbusgray); // color_darkpalegreen.darker();
                chrono_color = setDUBrightness(Color.GREEN.brighter());
                
            } else {
            	/*
            	 * Custom colors - Boeing Style
            	 */

                navaid_color = setDUBrightness(color_boeingcyan);
                term_wpt_color = setDUBrightness(color_cornflowerblue.darker());
                wpt_color = setDUBrightness(color_cornflowerblue);
                awy_wpt_color = setDUBrightness(color_cornflowerblue.brighter());
                arpt_color = setDUBrightness(color_mediumaquamarine);
                tuned_localizer_color = setDUBrightness(color_aquamarine);
                silent_localizer_color = setDUBrightness(color_mediumaquamarine.darker().darker());
                reference_localizer_color = setDUBrightness(color_lightaquamarine);
                receiving_localizer_color = setDUBrightness(color_aquamarine.darker());
                tuned_ndb_color = setDUBrightness(color_dodgerblue);
                no_rcv_ndb_color = setDUBrightness(color_dodgerblue.darker());
                tuned_vor_color = setDUBrightness(color_lime);
                no_rcv_vor_color = setDUBrightness(color_lime.darker());
                unknown_nav_color = setDUBrightness(color_cadetblue);
                holding_color = setDUBrightness(color_deeppink);
                traffic_color = setDUBrightness(color_lightsteelblue);
                faraway_color = setDUBrightness(color_lightsteelblue.darker().darker());
                pos_label_color = setDUBrightness(color_boeingcyan.darker());
                tcas_label_color = setDUBrightness(color_lightsteelblue);
                data_label_color = setDUBrightness(color_pastelhotpink);
                terrain_label_color = setDUBrightness(color_dodgerblue);
                fmc_active_color = setDUBrightness(color_hotpink);
                fmc_disp_color = setDUBrightness(Color.WHITE);
                fmc_other_color = setDUBrightness(Color.LIGHT_GRAY);
                altitude_arc_color = setDUBrightness(color_yellowgreen);
                fmc_ll_active_color = setDUBrightness(color_yellowgreen.brighter());
                fmc_ll_disp_color = setDUBrightness(color_yellowgreen);
                fmc_ll_other_color = setDUBrightness(color_yellowgreen.darker());
                heading_labels_color = setDUBrightness(color_limegreen);
                cardinal_labels_color = setDUBrightness(color_limegreen);
                nav_needle_color = setDUBrightness(color_mediumviolet);
                deviation_scale_color = setDUBrightness(Color.LIGHT_GRAY);
                range_arc_color = setDUBrightness(Color.DARK_GRAY); // was: Color.GRAY
                range_label_color = setDUBrightness(dim_markings_color);
                aircraft_color = setDUBrightness(Color.WHITE);
                chrono_background_color = setDUBrightness(Color.BLACK);
                chrono_color = setDUBrightness(Color.WHITE);
            }

            
            markings_color = setDUBrightness(Color.WHITE);
//                float hsb[] = new float[3];
//                Color.RGBtoHSB(markings_color.getRed(), markings_color.getGreen(), markings_color.getBlue(), hsb);
//                hsb[2] *= 0.25f;
//                markings_color = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
            dim_markings_color = setDUBrightness(Color.LIGHT_GRAY);

            label_color = setDUBrightness(color_boeingcyan);
            dim_label_color = setDUBrightness(Color.DARK_GRAY);
            normal_color = setDUBrightness(color_lime);
            unusual_color = setDUBrightness(color_deepskyblue);
            caution_color = setDUBrightness(color_amber);
            warning_color = setDUBrightness(Color.RED);
            
            heading_bug_color = setDUBrightness(color_magenta);
            wind_color = setDUBrightness(color_palegreen); // was color_lavender
            efb_color = setDUBrightness(color_lavender);
            top_text_color = setDUBrightness(Color.WHITE);
            grass_color = setDUBrightness(color_darkgreen);
            hard_color = setDUBrightness(Color.GRAY);
            sand_color = setDUBrightness(color_darktan);
            snow_color = setDUBrightness(Color.LIGHT_GRAY);
            sky_color = setDUBrightness(color_sky);
            
            ground_color = setDUBrightness(color_ground);
            brightground_color = setDUBrightness(color_ground.brighter());
            instrument_background_color = setDUBrightness(color_bluegray);
            fpv_color = setDUBrightness(Color.LIGHT_GRAY);
            
            /*
             * Custom colors - Clock 
             */
            clock_color = setDUBrightness(color_khaki);
            clock_label_color = setDUBrightness(color_labelblue);
            clock_markings_color = setDUBrightness(Color.white);
            // Clock panel colors (should depends on cockpit light) 
            clock_digital_54616E_color = Color.decode("#54616E");
            clock_digital_54616E_dark_color = Color.decode("#54616E").darker().darker();
            clock_digital_dark_gray_color = Color.decode("#080808");
            
            /*
             * Custom colors - ECAM 
             */
        	ecam_warning_color   = setDUBrightness(Color.red);
        	ecam_caution_color   = setDUBrightness(color_amber);
        	ecam_normal_color    = setDUBrightness(Color.green);
        	ecam_markings_color  = setDUBrightness(Color.white);
        	ecam_action_color    = setDUBrightness(Color.cyan);
        	ecam_special_color   = setDUBrightness(Color.magenta);
        	ecam_reference_color = setDUBrightness(Color.yellow);
        	ecam_box_bg_color    = setDUBrightness(color_airbusgray.darker()); // was new Color(0x0f1c60);
            
            /*
             *  PFD colors
             */
            if ( airbus_style ) {
                /*
                 * Custom colors - PFD Airbus Style 
                 */
                pfd_armed_color = setDUBrightness(Color.cyan);
                pfd_managed_color = setDUBrightness(Color.magenta);
                pfd_selected_color = setDUBrightness(Color.cyan);
                pfd_box_color = setDUBrightness(Color.white);
                pfd_vsi_needle_color = setDUBrightness(Color.green);
                pfd_instrument_background_color = setDUBrightness(color_airbusgray);
                pfd_markings_color = setDUBrightness(Color.white);
                pfd_radio_alti_color = setDUBrightness(Color.green);
                pfd_reference_color = setDUBrightness(Color.yellow);
                pfd_mach_color = setDUBrightness(Color.green);
                pfd_alti_color = setDUBrightness(Color.green);
                pfd_ils_color = setDUBrightness(Color.magenta);
            	instrument_background_color = setDUBrightness(color_airbusgray);
            	heading_bug_color = setDUBrightness(Color.cyan);           	
            	pfd_sky_color = setDUBrightness(color_airbussky);
            } else {
                /*
                 *  Custom colors - PFD Boeing Style (default)
                 */
                pfd_armed_color = setDUBrightness(Color.cyan);
                pfd_managed_color = setDUBrightness(Color.magenta);
                pfd_selected_color = setDUBrightness(Color.magenta);
                pfd_box_color = setDUBrightness(Color.white);
                pfd_vsi_needle_color = setDUBrightness(Color.white);
                pfd_instrument_background_color = setDUBrightness(color_bluegray);
                pfd_markings_color = setDUBrightness(Color.white);
                pfd_radio_alti_color = setDUBrightness(Color.white);
                pfd_reference_color = setDUBrightness(Color.white);
                pfd_mach_color = setDUBrightness(Color.green);
                pfd_alti_color = setDUBrightness(Color.white);
                pfd_active_color = setDUBrightness(Color.green);
                pfd_ils_color = setDUBrightness(Color.white);
            	instrument_background_color = setDUBrightness(color_bluegray);
            	heading_bug_color = setDUBrightness(color_magenta);             	
            	pfd_sky_color = setDUBrightness(sky_color);
            }
            
            pfd_ground_color = setDUBrightness(ground_color);
            pfd_active_color = setDUBrightness(Color.green);
            pfd_caution_color = setDUBrightness(color_amber);
            pfd_alarm_color = setDUBrightness(Color.red);
            
            // CDU colors
            cdu_title_color = setDUBrightness(Color.green);
            cdu_data_color = setDUBrightness(Color.white);
            cdu_scratch_pad_color = setDUBrightness(Color.white);
        
            
        } else { 
        	/*
        	 * STANDARD COLORS (i.e. not customed)
        	 */
        	
            background_color = Color.BLACK;

            
            /*
             *  STANDARD COLORS: Navigation Display
             */
            if ( this.settings.style == Avionics.STYLE_AIRBUS ) {
                /*
                 *  STANDARD COLORS: Navigation Display Airbus Style
                 */
                navaid_color = setDUBrightness(color_boeingcyan);
                term_wpt_color = setDUBrightness(color_boeingcyan);
                wpt_color = setDUBrightness(color_boeingcyan);
                awy_wpt_color = setDUBrightness(color_boeingcyan);
                arpt_color = setDUBrightness(color_boeingcyan);
                tuned_localizer_color = setDUBrightness(color_lime);
                silent_localizer_color = setDUBrightness(Color.GRAY);
                reference_localizer_color = setDUBrightness(Color.WHITE);
                receiving_localizer_color = setDUBrightness(Color.LIGHT_GRAY);
                tuned_ndb_color = setDUBrightness(color_dodgerblue);
                no_rcv_ndb_color = setDUBrightness(color_dodgerblue);
                tuned_vor_color = setDUBrightness(color_lime);
                no_rcv_vor_color = setDUBrightness(color_lime);
                unknown_nav_color = setDUBrightness(color_lime);
                holding_color = setDUBrightness(color_magenta);
                traffic_color = setDUBrightness(Color.WHITE);
                faraway_color = setDUBrightness(Color.DARK_GRAY);
                pos_label_color = setDUBrightness(color_boeingcyan);
                tcas_label_color = setDUBrightness(color_boeingcyan);
                data_label_color = setDUBrightness(color_boeingcyan);
                terrain_label_color = setDUBrightness(Color.cyan);
                fmc_active_color = setDUBrightness(Color.GREEN);
                fmc_disp_color = setDUBrightness(Color.WHITE);
                fmc_other_color = setDUBrightness(Color.GREEN);
                altitude_arc_color = setDUBrightness(color_lime);
                fmc_ll_active_color = setDUBrightness(color_lime);
                fmc_ll_disp_color = setDUBrightness(color_lime);
                fmc_ll_other_color = setDUBrightness(color_lime);
                heading_labels_color = setDUBrightness(color_lime);
                cardinal_labels_color = setDUBrightness(color_limegreen);
                nav_needle_color = setDUBrightness(color_magenta);
                deviation_scale_color = setDUBrightness(Color.LIGHT_GRAY);
                markings_color = setDUBrightness(Color.WHITE);
                dim_markings_color = setDUBrightness(Color.LIGHT_GRAY);
                range_arc_color = setDUBrightness(Color.GRAY); // was: Color.GRAY.brighter()
                range_label_color = dim_markings_color;
                label_color = setDUBrightness(color_boeingcyan);
                dim_label_color = Color.BLACK;
                normal_color = setDUBrightness(color_lime);
                unusual_color = setDUBrightness(color_deepskyblue);
                caution_color = setDUBrightness(color_amber);
                warning_color = setDUBrightness(Color.RED);
                aircraft_color = setDUBrightness(Color.WHITE);           
                wind_color = setDUBrightness(Color.WHITE);
                efb_color = setDUBrightness(Color.WHITE);
                top_text_color = setDUBrightness(Color.WHITE);
                grass_color = setDUBrightness(color_darkgreen);
                hard_color = setDUBrightness(Color.GRAY);
                sand_color = setDUBrightness(color_darktan);
                snow_color = setDUBrightness(Color.LIGHT_GRAY);
                sky_color = setDUBrightness(color_sky);
                brightsky_color = setDUBrightness(color_sky.brighter());
                ground_color = setDUBrightness(color_ground);
                brightground_color = setDUBrightness(color_ground.brighter());            
                fpv_color = setDUBrightness(Color.WHITE);
                clock_color = setDUBrightness(Color.WHITE);
                chrono_background_color = setDUBrightness(color_darkpalegreen);
                chrono_color = setDUBrightness(Color.GREEN);
            } else {
                /*
                 *  STANDARD COLORS: Navigation Display Boeing Style
                 */
                navaid_color = setDUBrightness(color_boeingcyan);
                term_wpt_color = setDUBrightness(color_boeingcyan);
                wpt_color = setDUBrightness(color_boeingcyan);
                awy_wpt_color = setDUBrightness(color_boeingcyan);
                arpt_color = setDUBrightness(color_boeingcyan);
                tuned_localizer_color = setDUBrightness(color_lime);
                silent_localizer_color = setDUBrightness(Color.GRAY);
                reference_localizer_color = setDUBrightness(Color.WHITE);
                receiving_localizer_color = setDUBrightness(Color.LIGHT_GRAY);
                tuned_ndb_color = setDUBrightness(color_dodgerblue);
                no_rcv_ndb_color = setDUBrightness(color_dodgerblue);
                tuned_vor_color = setDUBrightness(color_lime);
                no_rcv_vor_color = setDUBrightness(color_lime);
                unknown_nav_color = setDUBrightness(color_lime);
                holding_color = setDUBrightness(color_magenta);
                traffic_color = setDUBrightness(Color.WHITE);
                faraway_color = setDUBrightness(Color.DARK_GRAY);
                pos_label_color = setDUBrightness(color_boeingcyan);
                tcas_label_color = setDUBrightness(color_boeingcyan);
                data_label_color = setDUBrightness(color_boeingcyan);
                terrain_label_color = setDUBrightness(Color.cyan);
                fmc_active_color = setDUBrightness(color_magenta);
                fmc_disp_color = setDUBrightness(Color.WHITE);
                fmc_other_color = setDUBrightness(Color.LIGHT_GRAY);
                altitude_arc_color = setDUBrightness(color_lime);
                fmc_ll_active_color = setDUBrightness(color_lime);
                fmc_ll_disp_color = setDUBrightness(color_lime);
                fmc_ll_other_color = setDUBrightness(color_lime);
                heading_labels_color = setDUBrightness(color_lime);
                cardinal_labels_color = setDUBrightness(color_limegreen);
                nav_needle_color = setDUBrightness(color_magenta);
                deviation_scale_color = setDUBrightness(Color.LIGHT_GRAY);
                markings_color = setDUBrightness(Color.WHITE);
                dim_markings_color = setDUBrightness(Color.LIGHT_GRAY);
                range_arc_color = setDUBrightness(Color.GRAY); // was: Color.GRAY.brighter()
                range_label_color = dim_markings_color;
                label_color = setDUBrightness(color_boeingcyan);
                dim_label_color = Color.BLACK;
                normal_color = setDUBrightness(color_lime);
                unusual_color = setDUBrightness(color_deepskyblue);
                caution_color = setDUBrightness(color_amber);
                warning_color = setDUBrightness(Color.RED);
                aircraft_color = setDUBrightness(Color.WHITE);           
                wind_color = setDUBrightness(Color.WHITE);
                efb_color = setDUBrightness(Color.WHITE);
                top_text_color = setDUBrightness(Color.WHITE);
                grass_color = setDUBrightness(color_darkgreen);
                hard_color = setDUBrightness(Color.GRAY);
                sand_color = setDUBrightness(color_darktan);
                snow_color = setDUBrightness(Color.LIGHT_GRAY);
                sky_color = setDUBrightness(color_sky);
                brightsky_color = setDUBrightness(color_sky.brighter());
                ground_color = setDUBrightness(color_ground);
                brightground_color = setDUBrightness(color_ground.brighter());            
                fpv_color = setDUBrightness(Color.WHITE);
                clock_color = setDUBrightness(Color.WHITE);
                chrono_background_color = Color.BLACK;
                chrono_color = setDUBrightness(Color.WHITE);
        	}
            
            /*
             * STANDARD COLORS: Clock 
             */            
            clock_color = setDUBrightness(Color.white);
            clock_label_color = setDUBrightness(color_labelblue);
            clock_markings_color = setDUBrightness(Color.white);
            // Clock panel colors (should depends on cockpit light) 
            clock_digital_54616E_color = Color.decode("#54616E");
            clock_digital_54616E_dark_color = Color.decode("#54616E").darker().darker();
            clock_digital_dark_gray_color = Color.decode("#080808");
            
            /*
             * STANDARD COLORS: ECAM 
             */
        	ecam_warning_color = setDUBrightness(Color.red);
        	ecam_caution_color = setDUBrightness(color_amber);
        	ecam_normal_color = setDUBrightness(Color.green);
        	ecam_markings_color = setDUBrightness(Color.white);
        	ecam_action_color = setDUBrightness(Color.cyan);
        	ecam_special_color = setDUBrightness(Color.magenta);
        	ecam_reference_color = setDUBrightness(Color.yellow);
        	ecam_box_bg_color = setDUBrightness(color_airbusgray); // was new Color(0x0f1c60);
        	
            /*
             * STANDARD COLORS: PFD 
             */
            if ( airbus_style ) {
                /*
                 * STANDARD COLORS - PFD colors Airbus Style 
                 */
                pfd_armed_color = setDUBrightness(Color.cyan);
                pfd_managed_color = setDUBrightness(Color.magenta);
                pfd_selected_color = setDUBrightness(Color.cyan);                
                pfd_vsi_needle_color = setDUBrightness(Color.green);
                pfd_instrument_background_color = setDUBrightness(color_airbusgray);
                pfd_markings_color = setDUBrightness(Color.white);
                pfd_radio_alti_color = setDUBrightness(Color.green);
                pfd_reference_color = setDUBrightness(Color.yellow);
                pfd_mach_color = setDUBrightness(Color.green);
                pfd_alti_color = setDUBrightness(Color.green);
                pfd_ils_color = setDUBrightness(Color.magenta);
            	instrument_background_color = setDUBrightness(color_airbusgray);
            	heading_bug_color = setDUBrightness(Color.cyan);
            	pfd_sky_color = setDUBrightness(color_airbussky);
            } else {
            	/*
            	 * STANDARD COLORS - PFD  Boeing Style (default)
            	 */
            	pfd_armed_color = setDUBrightness(Color.cyan);
            	pfd_managed_color = setDUBrightness(Color.magenta);
            	pfd_selected_color = setDUBrightness(Color.magenta);            	
            	pfd_vsi_needle_color = setDUBrightness(Color.white);
            	pfd_instrument_background_color = setDUBrightness(color_bluegray);
            	pfd_markings_color = setDUBrightness(Color.white);
            	pfd_radio_alti_color = setDUBrightness(Color.white);
            	pfd_reference_color = setDUBrightness(Color.yellow);
            	pfd_mach_color = setDUBrightness(Color.green);
            	pfd_alti_color = setDUBrightness(Color.white);
            	pfd_ils_color = setDUBrightness(Color.white);
            	instrument_background_color = setDUBrightness(color_bluegray);
            	heading_bug_color = setDUBrightness(color_magenta);
            	pfd_sky_color = setDUBrightness(sky_color);
            }
            pfd_box_color = setDUBrightness(Color.white);            
            pfd_ground_color = setDUBrightness(ground_color);
            pfd_active_color = setDUBrightness(Color.green);
            pfd_caution_color = setDUBrightness(color_amber);
            pfd_alarm_color = setDUBrightness(Color.red);
            
            // CDU colors
            cdu_title_color = setDUBrightness(Color.white);
            cdu_data_color = setDUBrightness(Color.white);
            cdu_scratch_pad_color = setDUBrightness(Color.white);         
           
        }
       
        if ( border_color.equals(XHSIPreferences.BORDER_BROWN) ) {
            backpanel_color = color_jumbobrown;
            frontpanel_color = color_jumbodarkbrown;
            knobs_color = color_jumbolightbrown;
        } else if ( border_color.equals(XHSIPreferences.BORDER_BLUE) ) {
            backpanel_color = color_airbusback;
            frontpanel_color = color_airbusfront;
            knobs_color = color_airbusknob;
        } else {
            backpanel_color = color_irongray;
            frontpanel_color = color_darkirongray;
            knobs_color = color_windowgray;
        }
        
    }

    public boolean update_colors(float du_brightness) {
    	boolean colors_updated = false;
    	
    	if (this.du_brightness != du_brightness) {
    		// Remember the display unit brightness
    		this.du_brightness = du_brightness;
    		
            // define the colors
            set_colors( preferences.get_use_more_color(), preferences.get_border_color() );
            border_gradient = new GradientPaint(
                    0, 0, backpanel_color.darker().darker().darker(),
                    frame_size.width, frame_size.height, backpanel_color.brighter(),
                    true);
            
            colors_updated = true;
    	}
    	return colors_updated;
    }

    public void update_config(Graphics2D g2) {

        // we got here because one of the settings has been changed
    	
    	// remember the time of the graphic reconfiguration
    	// some object might check this time to update their cached image
    	reconfigured_timestamp = System.currentTimeMillis();

        // clear the flags
        this.resized = false;
        this.reconfig = false;

        // Setup instrument style
        airbus_style = ( style == Avionics.STYLE_AIRBUS );
        boeing_style = ! ( style == Avionics.STYLE_AIRBUS );
        
        // Symbols color schema
        symbols_multiselection = preferences.get_symbols_multiselection();
        
        // anti-aliasing
        this.rendering_hints.put(RenderingHints.KEY_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, preferences.get_anti_alias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // define the colors
        // TODO: may be transfered to update_colors(...)
        set_colors( preferences.get_use_more_color(), preferences.get_border_color() );
        border_gradient = new GradientPaint(
                0, 0, backpanel_color.darker().darker().darker(),
                frame_size.width, frame_size.height , backpanel_color.brighter(),
                true);

        // switch width and height if the instrument is displayed on its side
        if ( ( preferences.get_panel_orientation(this.display_unit) == XHSIPreferences.Orientation.RIGHT )
                || ( preferences.get_panel_orientation(this.display_unit) == XHSIPreferences.Orientation.LEFT ) ) {
            this.frame_size.height = this.component_size.width;
            this.frame_size.width = this.component_size.height;
        }

        // calculate coordinates
        this.border_left = this.border;
        this.border_right = this.border;
        this.border_top = this.border;
        this.border_bottom = this.border;
        if ( preferences.get_panel_square(this.display_unit) ) {
            if ( this.frame_size.width > (this.frame_size.height ) ) {
                this.border_left += ( this.frame_size.width - ( this.frame_size.height  ) ) / 2;
                this.border_right = this.border_left;
            } else {
                this.border_top += ( this.frame_size.height  - this.frame_size.width ) / 2;
                this.border_bottom = this.border_top;
            }
        }
        //this.scaling_factor = Math.min( (float)this.frame_size.height, (float)this.frame_size.width ) / 600.0f;
        this.scaling_factor = Math.min( (float)(frame_size.width - border_left - border_right), (float)(frame_size.height - border_top - border_bottom) ) / 600.0f;
        // if the panel gets smaller than 600px, we _should_ try to reduce the size of fonts and images
        this.shrink_scaling_factor = Math.min(1.0f, this.scaling_factor);
        // things like the line thickness can grow when the panel gets bigger than 600px
        this.grow_scaling_factor = Math.max(1.0f, this.scaling_factor);
        // some screen elements like line widths and the border can grow with the power of 2
        if ( preferences.get_panels_locked() ) {
            this.border = preferences.get_panel_border(this.display_unit);
        } else {
            this.border = (int) (INITIAL_BORDER_SIZE * Math.max(1.0f, Math.pow(Math.min((float) this.frame_size.width, (float) this.frame_size.height) / 600.0f, 2)));
        }

        // the rectangle for our instrument
        panel_rect = new Rectangle(
                border_left,
                border_top,
                frame_size.width - border_left - border_right,
                frame_size.height - border_top - border_bottom
            );

        // a nice frame with rounded corners; it will be painted in InstrumentFrame.java
        Area inner_frame = new Area(new RoundRectangle2D.Float(
                border_left,
                border_top,
                frame_size.width - (border_left + border_right),
                frame_size.height - (border_top + border_bottom),
                (int)(30 * this.grow_scaling_factor),
                (int)(30 * this.grow_scaling_factor)));
        instrument_frame = new Area(new Rectangle2D.Float(0, 0, frame_size.width, frame_size.height));
        instrument_frame.subtract(inner_frame);
        // if the cpu can handle it: a double border
        Area outer_frame = new Area(new RoundRectangle2D.Float(
                border_left - border_left / 3,
                border_top - border_top / 3,
                frame_size.width - (border_left + border_right) + border_left / 3 + border_right / 3,
                frame_size.height - (border_top + border_bottom) + border_top / 3 + border_bottom / 3,
                (int)(30 * this.grow_scaling_factor),
                (int)(30 * this.grow_scaling_factor)));
        instrument_outer_frame = new Area(new Rectangle2D.Float(0, 0, frame_size.width, frame_size.height));
        instrument_outer_frame.subtract(outer_frame);

        // fonts
        set_fonts(g2, this.scaling_factor);

    }
    
    /**
     * Puts absolute coordinates in relation to width. The method assumes for a
     * square of 200 * 200 px. The center is at 100|100.
     *
     * @param relation the actual width of the square / 2
     * @param coordinate the coordinate based from the center of a 200x200 px
     * square
     * @return a coordinate representing the same position in a square with the
     * size (2+relation)*(2*relation)
     */
    public static int inRel(double relation, int coordinate) {
        return (int) (relation * coordinate);
    }


    /**
     * set color brightness level by darkening the color by (1-du_brightness)
     * If du_brightness is 0, then this will result in
     * a black return color.
     * If du_brightness is 1, then the color will be kept as it was.
     * If du_brightness is 2, then the will result in a nearly white return color.
     *
     * @param inputColor the color that the factor will be applied to
     * @return The Color brightness set by the factor
     */
    public Color setDUBrightness(Color inputColor) {
    	double factor = 1 - du_brightness;
    	int r,g,b;
    	
        if (factor < -1) {
            factor = -1;
        }
        if (factor > 1) {
            factor = 1;
        }
        if (factor < 0 ) {
        	factor = du_brightness - 1;

        	/*
            int maxFactorR = (int) (255.0 / inputColor.getRed() - 1);
            int maxFactorG = (int) (255.0 / inputColor.getGreen() - 1);
            int maxFactorB = (int) (255.0 / inputColor.getBlue() - 1);
            factor = Math.min(factor, Math.min(maxFactorR, Math.min(maxFactorG, maxFactorB)));
            */
            
            r = (int) Math.min(255, inputColor.getRed() + (factor * inputColor.getRed()));
            g = (int) Math.min(255, inputColor.getGreen() + (factor * inputColor.getGreen()));
            b = (int) Math.min(255, inputColor.getBlue() + (factor * inputColor.getBlue()));

            // decrease saturation by 40% max
        	if ( r == 0 ) r = (int) Math.min(255, 96*factor);
        	if ( g == 0 ) g = (int) Math.min(255, 96*factor);
        	if ( b == 0 ) b = (int) Math.min(255, 96*factor);

        } else {
            r = (int) (inputColor.getRed() - (factor * inputColor.getRed()));
            g = (int) (inputColor.getGreen() - (factor * inputColor.getGreen()));
            b = (int) (inputColor.getBlue() - (factor * inputColor.getBlue()));
        }

        return new Color(r, g, b, inputColor.getAlpha());
    }
    
    /**
     * Recalculates the color of the background light in the OHP. It uses a
     * matrix to do linear interpolation upon tested data of the QPAC. The color
     * can then be retrieved using
     * {@link #get_ohp_background_light get_ohp_background_light}.
     *
     *
     * @param ohp_background_setting A value between 0 and 1, representing the
     * setting on the OHP
     * @param outside_light A value between 0 and 180, with 180 being daylight
     * and 0 being complete darkness
     */
    public static Color update_panel_background_color(double ohp_background_setting, double outside_light) {
        //For interpolation, a method as described in this paper is used:
        //http://bmia.bmt.tue.nl/people/BRomeny/Courses/8C080/Interpolation.pdf

        //3D Array, where first dimension is outside light (night / day), 
        //second dimension is ohp background light intensity (0%/25%/50%/75%/100%)
        //and third dimension is the color channel (red/green/blue)
        int[][][] data = new int[2][5][3];


        //                     NIGHT
        //                               { r ,  g ,  b }
        /*   0% */ data[0][0] = new int[]{1, 2, 3};
        /*  25% */ data[0][1] = new int[]{65, 66, 39};
        /*  50% */ data[0][2] = new int[]{128, 129, 76};
        /*  75% */ data[0][3] = new int[]{192, 193, 114};
        /* 100% */ data[0][4] = new int[]{212, 213, 126};


        //                     DAY
        //                               { r ,  g ,  b }
        /*   0% */ data[1][0] = new int[]{13, 24, 26};
        /*  25% */ data[1][1] = new int[]{130, 133, 100};
        /*  50% */ data[1][2] = new int[]{164, 181, 131};
        /*  75% */ data[1][3] = new int[]{228, 245, 169};
        /* 100% */ data[1][4] = new int[]{237, 253, 167};

        int x = (int) (ohp_background_setting * 100);
        int y = (int) (outside_light * 100);
        return ColorUtilities.interpolate_color(data, x, y);
    }

    /**
     * Recalculates the color of the texts on the panels. It uses a matrix to do
     * linear interpolation upon tested data of the QPAC. The color can then be
     * retrieved using {@link #get_ohp_text_color get_ohp_text_color}.
     *
     *
     * @param ohp_background_setting A value between 0 and 1, representing the
     * setting on the OHP
     * @param outside_light A value between 0 and 180, with 180 being daylight
     * and 0 being complete darkness
     */
    public static Color update_panel_text_color(double ohp_background_setting, double outside_light) {
        //For interpolation, a method as described in this paper is used:
        //http://bmia.bmt.tue.nl/people/BRomeny/Courses/8C080/Interpolation.pdf

        //3D Array, where first dimension is outside light (night / day), 
        //second dimension is ohp background light intensity (0%/25%/50%/75%/100%)
        //and third dimension is the color channel (red/green/blue)
        int[][][] data = new int[2][5][3];


        //                     NIGHT
        //                               { r ,  g ,  b }
        /*   0% */ data[0][0] = new int[]{23, 23, 23};
        /*  25% */ data[0][1] = new int[]{87, 87, 60};
        /*  50% */ data[0][2] = new int[]{145, 145, 92};
        /*  75% */ data[0][3] = new int[]{209, 209, 130};
        /* 100% */ data[0][4] = new int[]{255, 255, 167};


        //                     DAY
        //                               { r ,  g ,  b }
        /*   0% */ data[1][0] = new int[]{197, 198, 202};
        /*  25% */ data[1][1] = new int[]{255, 255, 239};
        /*  50% */ data[1][2] = new int[]{255, 255, 255};
        /*  75% */ data[1][3] = new int[]{255, 255, 255};
        /* 100% */ data[1][4] = new int[]{255, 255, 255};

        int x = (int) (ohp_background_setting * 100);
        int y = (int) (outside_light * 100);
        return ColorUtilities.interpolate_color(data, x, y);
    }

    /**
     * Recalculates the color of the texts in displays on the panels. It uses a
     * matrix to do linear interpolation upon tested data of the QPAC. The color
     * can then be retrieved using
     * {@link #get_ohp_text_color get_ohp_text_color}.
     *
     *
     * @param ohp_background_setting A value between 0 and 1, representing the
     * setting on the OHP
     * @param outside_light A value between 0 and 180, with 180 being daylight
     * and 0 being complete darkness
     */
    public static Color update_panel_display_color(double ohp_background_setting, double outside_light) {
        //For interpolation, a method as described in this paper is used:
        //http://bmia.bmt.tue.nl/people/BRomeny/Courses/8C080/Interpolation.pdf

        //3D Array, where first dimension is outside light (night / day), 
        //second dimension is ohp background light intensity (0%/25%/50%/75%/100%)
        //and third dimension is the color channel (red/green/blue)
        int[][][] data = new int[2][5][3];


        //                     NIGHT
        //                               { r ,  g ,  b }
        /*   0% */ data[0][0] = new int[]{55, 55, 55};
        /*  25% */ data[0][1] = new int[]{105, 105, 105};
        /*  50% */ data[0][2] = new int[]{155, 155, 155};
        /*  75% */ data[0][3] = new int[]{205, 205, 205};
        /* 100% */ data[0][4] = new int[]{255, 255, 255};


        //                     DAY
        //                               { r ,  g ,  b }
        /*   0% */ data[1][0] = new int[]{197, 198, 202};
        /*  25% */ data[1][1] = new int[]{255, 255, 239};
        /*  50% */ data[1][2] = new int[]{255, 255, 255};
        /*  75% */ data[1][3] = new int[]{255, 255, 255};
        /* 100% */ data[1][4] = new int[]{255, 255, 255};

        int x = (int) (ohp_background_setting * 100);
        int y = (int) (outside_light * 100);
        return ColorUtilities.interpolate_color(data, x, y);
    }

    /**
     * Recalculates the color of the green arrows, representing pipes, on the
     * OHP. It uses a matrix to do linear interpolation upon tested data of the
     * QPAC. The color can then be retrieved using
     * {@link #get_green_arrow_color get_green_arrow_color}.
     *
     *
     * @param ohp_background_setting A value between 0 and 1, representing the
     * setting on the OHP
     * @param outside_light A value between 0 and 180, with 180 being daylight
     * and 0 being complete darkness
     */
    public static Color update_ohp_green_lines_color(double ohp_background_setting, double outside_light) {
        //For interpolation, a method as described in this paper is used:
        //http://bmia.bmt.tue.nl/people/BRomeny/Courses/8C080/Interpolation.pdf

        //3D Array, where first dimension is outside light (night / day), 
        //second dimension is ohp background light intensity (0%/25%/50%/75%/100%)
        //and third dimension is the color channel (red/green/blue)
        int[][][] data = new int[2][5][3];


        //                     NIGHT
        //                               { r ,  g ,  b }
        /*   0% */ data[0][0] = new int[]{3, 24, 14};
        /*  25% */ data[0][1] = new int[]{5, 88, 16};
        /*  50% */ data[0][2] = new int[]{7, 151, 18};
        /*  75% */ data[0][3] = new int[]{9, 215, 20};
        /* 100% */ data[0][4] = new int[]{11, 255, 22};


        //                     DAY
        //                               { r ,  g ,  b }
        /*   0% */ data[1][0] = new int[]{25, 202, 119};
        /*  25% */ data[1][1] = new int[]{27, 255, 125};
        /*  50% */ data[1][2] = new int[]{30, 255, 117};
        /*  75% */ data[1][3] = new int[]{31, 255, 125};
        /* 100% */ data[1][4] = new int[]{32, 255, 126};

        int x = (int) (ohp_background_setting * 100);
        int y = (int) (outside_light * 100);
        return ColorUtilities.interpolate_color(data, x, y);
    }
    
    public int get_text_width(Graphics graphics, Font font, String text) {
        return graphics.getFontMetrics(font).stringWidth(text);
    }


    public int get_text_height(Graphics graphics, Font font) {
        return graphics.getFontMetrics(font).getHeight();
    }


    public void componentResized(ComponentEvent event) {
    }


    public void componentMoved(ComponentEvent event) {
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


}
