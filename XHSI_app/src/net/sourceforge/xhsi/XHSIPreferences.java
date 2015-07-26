/**
* XHSIPreferences.java
* 
* Provides read and write access to XHSI preferences. Encapsulates
* persistence mechanisms.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2010-2014  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;



public class XHSIPreferences {

    private static final String PROPERTY_FILENAME = "XHSI.properties";

    // SYSTEM
    public static final String PREF_APTNAV_DIR = "aptnav.dir";
    public static final String PREF_REPLAY_DELAY_PER_FRAME = "replay.steps.delay";
    public static final String PREF_PORT = "port";
    public static final String PREF_GROUP = "multicast.group";
    public static final String PREF_MULTICAST = "multicast.enable";
    public static final String PREF_LOGLEVEL = "loglevel";
    public static final String PREF_INSTRUMENT_POSITION = "instrument.position";
    public static final String PREF_DISPLAY_STATUSBAR = "display.statusbar";
    public static final String PREF_SIMCOM = "simulator.communication";

    // WINDOWS
    public static final String PREF_START_ONTOP = "windows.start.ontop";
    public static final String PREF_HIDE_WINDOW_FRAMES = "windows.hide.frame";
    public static final String PREF_PANELS_LOCKED = "windows.locked";
    // Settings for one window (8)
    // will have to be prepended by du.0, du.1, du.2 etc...
    public static final String PREF_DU_PREPEND = "du.";
    public static final String PREF_DU_ACTIVE = ".active";
    public static final String PREF_DU_POS_X = ".pos_x";
    public static final String PREF_DU_POS_Y = ".pos_y";
    public static final String PREF_DU_WIDTH = ".width";
    public static final String PREF_DU_HEIGHT = ".height";
    public static final String PREF_DU_BORDER = ".border";
    public static final String PREF_DU_SQUARE = ".square";
    public static final String PREF_DU_ORIENTATION = ".orientation";
    public static final String PREF_CONWIN_MINIMIZED = "conwin.minimized";

    // GRAPHICS
    public static final String PREF_INSTRUMENT_STYLE = "instrument.style";
    public static final String PREF_ANTI_ALIAS = "anti-alias";
    public static final String PREF_BORDER_STYLE = "border.style";
    public static final String PREF_BORDER_COLOR = "border.color";
    public static final String PREF_USE_MORE_COLOR = "use.more.color";
    public static final String PREF_BOLD_FONTS = "bold.fonts";
    public static final String PREF_DRAW_BEZIER_PAVEMENTS = "draw.bezier.pavements";

    // Avionics Options
    public static final String PREF_USE_POWER = "use.avionics.power";
    public static final String PREF_AUTO_FRONTCOURSE = "auto.frontcourse";
    public static final String PREF_HSI_SOURCE = "hsi.source";

    // ND options
    public static final String PREF_MIN_RWY_LEN = "minimum.runway.length";
    public static final String PREF_RWY_LEN_UNITS = "runway.length.units";
    public static final String PREF_DRAW_RUNWAYS = "draw.runways";
    public static final String PREF_AIRBUS_MODES = "airbus.modes";
    public static final String PREF_DRAW_RANGE_ARCS = "draw.range.arcs";
    public static final String PREF_LIMIT_ARCS_60DEG = "nd.limit.arcs.60deg";
    public static final String PREF_MODE_MISMATCH_CAUTION = "mode.mismatch.caution";
    public static final String PREF_TCAS_ALWAYS_ON = "tcas.always.on";
    public static final String PREF_CLASSIC_HSI = "classic.hsi";
    public static final String PREF_APPVOR_UNCLUTTER = "appvor.unclutter";
    public static final String PREF_PLAN_AIRCRAFT_CENTER = "plan.aircraft.center";
    public static final String PREF_DRAW_INSIDE_ROSE = "draw.inside.rose";
    public static final String PREF_COLORED_HSI_COURSE = "pfd.colored.hsi.course";
    public static final String PREF_ND_NAVAID_FREQ = "nd.navaid.frequencies";
    public static final String PREF_ND_WRITE_AP_HDG = "nd.write.ap.heading";
    public static final String PREF_ND_SHOW_CLOCK = "nd.show.clock";

    // PFD options
    public static final String PREF_HORIZON_STYLE = "horizon.style";
    public static final String PREF_DIAL_TRANSPARENCY = "pfd.dial.transparency";
    public static final String PREF_PFD_DRAW_HSI = "pfd.draw.hsi";
    public static final String PREF_SINGLE_CUE_FD = "single.cue.fd";
    public static final String PREF_DRAW_AOA = "draw.aoa";
    public static final String PREF_PFD_DRAW_RADIOS = "pfd.draw.radios";
    public static final String PREF_PFD_ADI_CENTERED = "pfd.adi.centered";
    public static final String PREF_PFD_DRAW_TWINSPEEDS = "pfd.draw.twinspeeds";
    public static final String PREF_PFD_DRAW_TURNRATE = "pfd.draw.turnrate";
    public static final String PREF_PFD_DRAW_GMETER = "pfd.draw.gmeter";
    public static final String PREF_PFD_DRAW_JOKE_INPUT = "pfd.draw.joke_input";

    // EICAS options
    public static final String PREF_EICAS_LAYOUT = "eicas.layout";
    public static final String PREF_OVERRIDE_ENGINE_COUNT = "override.engine.count";
    public static final String PREF_FUEL_UNITS = "fuel.units";
    public static final String PREF_ENGINE_TYPE = "eicas.engine.type";
    public static final String PREF_TRQ_SCALE = "eicas.trq.scale";

    // MFD options
    public static final String PREF_MFD_MODE = "mfd.mode";
    public static final String PREF_ARPT_CHART_COLOR = "arpt.chart.color";
    public static final String PREF_ARPT_CHART_NAV_DEST = "arpt.chart.nav.dest";
    
    // CDU options
    public static final String PREF_CDU_DISPLAY_ONLY = "cdu.display.only";
    public static final String PREF_CDU_SOURCE = "cdu.source";


    // constants

    // for PREF_SIMCOM
    public static final String XHSI_PLUGIN = "XHSI_plugin";
    // public static final String SCS = "Cross-Simulator_SCS"; if ever ...

    // for PREF_INSTRUMENT_POSITION
    public static final String PILOT = "pilot";
    public static final String COPILOT = "copilot";
    public static final String INSTRUCTOR = "instructor";

    // for PREF_INSTRUMENT_STYLE
    public static final String INSTRUMENT_STYLE_SWITCHABLE = "switchable";   
    public static final String INSTRUMENT_STYLE_BOEING = "boeing";   
    public static final String INSTRUMENT_STYLE_AIRBUS = "airbus";   
    
    // for PREF_PFD_DRAW_JOKE_INPUT
    public static final String JOKE_INPUT_NONE = "none"; 
    public static final String JOKE_INPUT_AUTO = "auto";
    public static final String JOKE_INPUT_RUDDER = "rudder";
    public static final String JOKE_INPUT_ALWAYS = "always";
    public static final String JOKE_INPUT_ALWAYS_RUDDER = "always+rudder";
    public enum DrawJokeInputMode { NONE, AUTO, AUTO_RUDDER, ALWAYS, ALWAYS_RUDDER };
    
    // for PREF_HSI_SOURCE
    public static final String USER = "user";
    public static final String NAV1 = "nav1";
    public static final String NAV2 = "nav2";

    // for PREF_HORIZON_STYLE
    public static final String HORIZON_SQUARE = "square";
    public static final String HORIZON_ROUNDED = "rounded";
    public static final String HORIZON_FULLWIDTH = "fullwidth";
    public static final String HORIZON_FULLSCREEN = "fullscreen";
    
    // for PREF_BORDER_STYLE
    public static final String BORDER_RELIEF = "relief";
    public static final String BORDER_DARK = "dark";
    public static final String BORDER_LIGHT = "light";
    public static final String BORDER_NONE = "none";

    // for PREF_BORDER_COLOR
    public static final String BORDER_GRAY = "gray";
    public static final String BORDER_BROWN = "brown";
    public static final String BORDER_BLUE = "blue";

    // for PREF_MFD_MODE
    public static final String MFD_MODE_SWITCHABLE = "switchable";
    public static final String MFD_MODE_LINKED = "linked";
    public static final String MFD_MODE_ARPT_CHART = "artp_chart";
    public static final String MFD_MODE_FPLN = "fpln";
    public static final String MFD_MODE_LOWER_EICAS = "lower_eicas";
    public static final String MFD_MODE_RTU = "rtu";
    public static final String MFD_MODE_SYS = "sys";
    public static final String MFD_MODE_FCTL = "fctl";
    public static final String MFD_MODE_APU = "apu";
    public static final String MFD_MODE_ELEC = "elec";
    public static final String MFD_MODE_WHEELS = "wheels";
    public static final String MFD_MODE_DOOR_OXY = "door_oxy";
    public static final String MFD_MODE_BLEED = "bleed";
    public static final String MFD_MODE_COND = "cond";
    public static final String MFD_MODE_FUEL = "fuel";
    public static final String MFD_MODE_CAB_PRESS = "cab_press";
    public static final String MFD_MODE_HYDR = "hydr";
    public static final String MFD_MODE_STATUS = "status";
    
    // for PREF_ARPT_CHART_COLOR
    public static final String ARPT_DIAGRAM_COLOR_AUTO = "auto";
    public static final String ARPT_DIAGRAM_COLOR_DAY = "day";
    public static final String ARPT_DIAGRAM_COLOR_NIGHT = "night";


    // for PREF_CDU_SOURCE
    public static final String CDU_SOURCE_SWITCHABLE = "switchable";
    public static final String CDU_SOURCE_LEGACY = "legacy";
    public static final String CDU_SOURCE_XFMC = "xfmc";
    public static final String CDU_SOURCE_UFMC = "ufmc";
    
    
    
    public static enum Orientation {
        UP ("Up"),
        LEFT ("Left"),
        RIGHT ("Right"),
        DOWN ("Down");
        private String rotation;
        Orientation(String rotation) {
            this.rotation = rotation;
        }
        public String get_rotation() {
            return this.rotation;
        }
    }

    // for PREF_EICAS_LAYOUT
    public static final String EICAS_LAYOUT_PRIMARY = "primary";
    public static final String EICAS_LAYOUT_PRIMARY_AND_CONTROLS = "primary+controls";
    public static final String EICAS_LAYOUT_FULL = "full";

    // for PREF_FUEL_UNITS
    public static final String FUEL_UNITS_SWITCHABLE = "switchable";
    public static final String FUEL_UNITS_KG = "KG";
    public static final String FUEL_UNITS_LBS = "LBS";
    public static final String FUEL_UNITS_USG = "USG";
    public static final String FUEL_UNITS_LTR = "LTR";

    // for PREF_ENGINE_TYPE
    public static final String ENGINE_TYPE_SWITCHABLE = "switchable";
    public static final String ENGINE_TYPE_N1 = "N1";
    public static final String ENGINE_TYPE_EPR = "EPR";
    public static final String ENGINE_TYPE_TRQ = "TRQ";
    public static final String ENGINE_TYPE_MAP = "MAP";
    
    // for PREF_TRQ_SCALE
    public static final String TRQ_SCALE_SWITCHABLE = "switchable";
    public static final String TRQ_SCALE_LBFT = "LbFt";
    public static final String TRQ_SCALE_NM = "Nm";
    public static final String TRQ_SCALE_PERCENT = "Percent";
    
    
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    /**
     * The properties object holding all preferences
     */
    private Properties preferences;


    /**
     * Singleton instance of this class
     */
    private static XHSIPreferences single_instance = null;


    /**
     * true if unsaved changes are present
     */
    private boolean unsaved_changes;


    /**
     * the map of preference keys and observers which observe changes of
     * preferences with this key.
     *
     * keys = preference key
     * values = ArrayList with PreferenceObserver instances
     */
    private HashMap subscriptions;


    /**
     * @return        XHSIPreferences - the single instance of XHSIPreferences
     */
    public static XHSIPreferences get_instance() {
        if (XHSIPreferences.single_instance == null) {
            XHSIPreferences.single_instance = new XHSIPreferences();
        }
        return XHSIPreferences.single_instance;
    }


    /**
     * @param key        - the key of the preference
     * @param value    - the new value of the preference
     *
     * @throws RuntimeException - in case key is null or empty
     */
    public void set_preference(String key, String value) {

        if ((key == null) || (key.trim().equals("")))
            throw new RuntimeException("key must not be null or empty!");

        this.preferences.setProperty(key, value);
        logger.config("set preference '" + key + "' = '" + value + "'");
        this.unsaved_changes = true;
        store_preferences();
        validate_preferences();
        if ( key.startsWith(PREF_DU_PREPEND) ) {
            notify_observers(PREF_DU_PREPEND);
        } else {
            notify_observers(key);
        }

    }


    /**
     * @param key        - the key of the preference to be returned
     * @return            - the value of the preference
     *
     * @throws RuntimeException    - in case no preference key is set
     * @throws RuntimeException    - in case key is null or empty
     */
    public String get_preference(String key) {
        if ((key == null) || (key.trim().equals("")))
            throw new RuntimeException("key must not be null or empty!");
        if (this.preferences.containsKey(key) == false)
            throw new RuntimeException("no preference with key '" + key + "' is known!");

        return this.preferences.getProperty(key);
    }


    // SYSTEM

    /**
     * @return            - return pilot/copilot/instructor
     *
     */
    public String get_instrument_operator() {
        return get_preference(PREF_INSTRUMENT_POSITION);
    }

    
    // WINDOWS

    public boolean get_start_ontop() {
        return get_preference(PREF_START_ONTOP).equalsIgnoreCase("true");
    }


    public boolean get_hide_window_frames() {
        return get_preference(PREF_HIDE_WINDOW_FRAMES).equalsIgnoreCase("true");
    }


    public boolean get_panels_locked() {
        return get_preference(PREF_PANELS_LOCKED).equalsIgnoreCase("true");
    }


    // one window

    /**
     * @return            - is the panel active
     *
     */
    public boolean get_panel_active(int i) {
        return get_preference(PREF_DU_PREPEND + i + PREF_DU_ACTIVE).equalsIgnoreCase("true");
    }


    /**
     * @return            - the panel position x
     *
     */
    public int get_panel_pos_x(int i) {
        return Integer.parseInt(get_preference(PREF_DU_PREPEND + i + PREF_DU_POS_X));
    }


    /**
     * @return            - the panel position y
     *
     */
    public int get_panel_pos_y(int i) {
        return Integer.parseInt(get_preference(PREF_DU_PREPEND + i + PREF_DU_POS_Y));
    }


    /**
     * @return            - the panel width
     *
     */
    public int get_panel_width(int i) {
        return Integer.parseInt(get_preference(PREF_DU_PREPEND + i + PREF_DU_WIDTH));
    }


    /**
     * @return            - the panel height
     *
     */
    public int get_panel_height(int i) {
        return Integer.parseInt(get_preference(PREF_DU_PREPEND + i + PREF_DU_HEIGHT));
    }


    /**
     * @return            - the border size
     *
     */
    public int get_panel_border(int i) {
        return Integer.parseInt(get_preference(PREF_DU_PREPEND + i + PREF_DU_BORDER));
    }


    /**
     * @return            - keep the panel square or not
     *
     */
    public boolean get_panel_square(int i) {
        return get_preference(PREF_DU_PREPEND + i + PREF_DU_SQUARE).equalsIgnoreCase("true");
    }


    /**
     * @return            - panel rotation
     *
     */
    public Orientation get_panel_orientation(int i) {
        String compass = get_preference(PREF_DU_PREPEND + i + PREF_DU_ORIENTATION);
        if ( compass.equals("Left") ) return Orientation.LEFT;
        else if ( compass.equals("Right") ) return Orientation.RIGHT;
        else if ( compass.equals("Down") ) return Orientation.DOWN;
        else return Orientation.UP;
    }


    public boolean get_conwin_minimized() {
        return get_preference(PREF_CONWIN_MINIMIZED).equalsIgnoreCase("true");
    }


    // GRAPHICS

//    /**
//     * @return            - instrument style boeing
//     *
//     */
//    public boolean get_instrument_style_boeing() {
//        return get_preference(PREF_INSTRUMENT_STYLE).equalsIgnoreCase(INSTRUMENT_STYLE_BOEING);
//    }

//    /**
//     * @return            - instrument style airbus
//     *
//     */
//    public boolean get_instrument_style_airbus() {
//        return get_preference(PREF_INSTRUMENT_STYLE).equalsIgnoreCase(INSTRUMENT_STYLE_AIRBUS);
//    }
    
    /**
     * @return            - use bold fonts or not
     *
     */
    public boolean get_bold_fonts() {
        return get_preference(PREF_BOLD_FONTS).equalsIgnoreCase("true");
    }


    /**
     * @return            - use more color or not
     *
     */
    public boolean get_use_more_color() {
        return get_preference(PREF_USE_MORE_COLOR).equalsIgnoreCase("true");
    }


    /**
     * @return            - anti-aliasing or not
     *
     */
    public boolean get_anti_alias() {
        return get_preference(PREF_ANTI_ALIAS).equalsIgnoreCase("true");
    }


    /**
     * @return            - draw a fancy border or not
     *
     */
    public boolean get_relief_border() {
        return get_preference(PREF_BORDER_STYLE).equalsIgnoreCase(BORDER_RELIEF);
    }


    /**
     * @return            - border style
     *
     */
    public String get_border_style() {
        return get_preference(PREF_BORDER_STYLE);
    }


    /**
     * @return            - border color
     *
     */
    public String get_border_color() {
        return get_preference(PREF_BORDER_COLOR);
    }


    /**
     * @return            - draw the pavements using bezier curves
     *
     */
    public boolean get_draw_bezier_pavements() {
        return get_preference(PREF_DRAW_BEZIER_PAVEMENTS).equalsIgnoreCase("true");
    }



    // Avionics Options

    /**
     * @return            - stay dark if there is no avionics power or always on
     *
     */
    public boolean get_use_power() {
        return get_preference(PREF_USE_POWER).equalsIgnoreCase("true");
    }

    /**
     * @return            - Set CRS automatically to Localizer's frontcourse
     *
     */
    public boolean is_auto_frontcourse_to_obs() {
        return get_preference(PREF_AUTO_FRONTCOURSE).equalsIgnoreCase("true");
    }

    /**
     * @return            - return 0: HSI source selected by pilot/copilot/instructor, 1: NAV1, 2: NAV2
     *
     */
    public int get_hsi_source() {
        int source;
        String source_str = get_preference(PREF_HSI_SOURCE);
        if ( source_str.equals(NAV1) ) {
            source = 1;
        } else if ( source_str.equals(NAV2) ) {
            source = 2;
        } else {
            source = 0;
        }
        return source;
    }


    // ND

    /**
     * @return            - the minimum length of runway that the airports should have in order to be dispalyed on the map
     *
     */
    public float get_min_rwy_length() {
        float min_rwy_length = (float)Float.parseFloat(get_preference(PREF_MIN_RWY_LEN));
        String rwy_units = get_preference(PREF_RWY_LEN_UNITS);
        if (rwy_units.equals("feet")) min_rwy_length *= 0.3048f;

        return min_rwy_length;
    }

    
    /**
     * @return            - draw range arcs or not
     *
     */
    public boolean get_draw_range_arcs() {
        return get_preference(PREF_DRAW_RANGE_ARCS).equalsIgnoreCase("true");
    }

    /**
     * @return            - Limit the arcs in Expanded mode to 60 degrees
     *
     */
    public boolean get_limit_arcs_at_60() {
        return get_preference(PREF_LIMIT_ARCS_60DEG).equalsIgnoreCase("true");
    }

    /**
     * @return            - draw the runways or not
     *
     */
    public boolean get_draw_runways() {
        return get_preference(PREF_DRAW_RUNWAYS).equalsIgnoreCase("true");
    }

    /**
     * @return            - limit to Airbus-style ND modes
     *
     */
    public boolean get_airbus_modes() {
        return get_preference(PREF_AIRBUS_MODES).equalsIgnoreCase("true");
    }

    /**
     * @return            - display EFIS MODE/NAV FREQ DISAGREE warnings
     *
     */
    public boolean get_mode_mismatch_caution() {
        return get_preference(PREF_MODE_MISMATCH_CAUTION).equalsIgnoreCase("true");
    }

    /**
     * @return            - TCAS always ON
     *
     */
    public boolean get_tcas_always_on() {
        return get_preference(PREF_TCAS_ALWAYS_ON).equalsIgnoreCase("true");
    }

    /**
     * @return            - Display Centered APP and VOR as classic HSI without map
     *
     */
    public boolean get_classic_hsi() {
        return get_preference(PREF_CLASSIC_HSI).equalsIgnoreCase("true");
    }

    /**
     * @return            - Display all map symbols in APP and VOR modes
     *
     */
    public boolean get_appvor_fullmap() {
        return get_preference(PREF_APPVOR_UNCLUTTER).equalsIgnoreCase("false");
    }

    /**
     * @return            - Center PLAN mode on next waypoint
     *
     */
    public boolean get_plan_aircraft_center() {
        return get_preference(PREF_PLAN_AIRCRAFT_CENTER).equalsIgnoreCase("true");
    }

    /**
     * @return            - Draw map only inside the compass rose
     *
     */
    public boolean get_draw_only_inside_rose() {
        return get_preference(PREF_DRAW_INSIDE_ROSE).equalsIgnoreCase("true");
    }

    /**
     * @return            - draw the HSI course pointer in the same color as the nav source
     *
     */
    public boolean get_draw_colored_hsi_course() {
        return get_preference(PREF_COLORED_HSI_COURSE).equalsIgnoreCase("true");
    }

    /**
     * @return            - Display the navaid frequencies on the map when DATA is on
     *
     */
    public boolean get_nd_navaid_frequencies() {
        return get_preference(PREF_ND_NAVAID_FREQ).equalsIgnoreCase("true");
    }

    /**
     * @return            - Write the AP HDG at the top
     *
     */
    public boolean get_nd_write_ap_hdg() {
        return get_preference(PREF_ND_WRITE_AP_HDG).equalsIgnoreCase("true");
    }

    
    /**
     * @return            - Display the Clock/Chronograph
     *
     */
    public boolean get_nd_show_clock() {
        return get_preference(PREF_ND_SHOW_CLOCK).equalsIgnoreCase("true");
    }

    
    // PFD

    /**
     * @return            - Draw horizon with a color gradient
     *
     */
    public boolean get_draw_colorgradient_horizon() {
        return get_preference(PREF_HORIZON_STYLE).equalsIgnoreCase(HORIZON_FULLSCREEN) || get_preference(PREF_HORIZON_STYLE).equalsIgnoreCase(HORIZON_FULLWIDTH);
    }

    /**
     * @return            - Draw full-width horizon
     *
     */
    public boolean get_draw_fullwidth_horizon() {
        return get_preference(PREF_HORIZON_STYLE).equalsIgnoreCase(HORIZON_FULLWIDTH);
    }

    /**
     * @return            - Draw full-width horizon
     *
     */
    public boolean get_draw_fullscreen_horizon() {
        return get_preference(PREF_HORIZON_STYLE).equalsIgnoreCase(HORIZON_FULLSCREEN);
    }

    /**
     * @return            - Draw rounded_square horizon
     *
     */
    public boolean get_draw_roundedsquare_horizon() {
        return get_preference(PREF_HORIZON_STYLE).equalsIgnoreCase(HORIZON_ROUNDED);
    }

    /**
     * @return            - PFD dial opacity
     *
     */
    public float get_pfd_dial_opacity() {
        String transparency = get_preference(PREF_DIAL_TRANSPARENCY);
        if ( transparency.equals("75") )
            return 0.25f;
        else if ( transparency.equals("50") )
            return 0.50f;
        else if ( transparency.equals("25") )
            return 0.75f;
        else
            return 1.00f;
    }

    /**
     * @return            - draw the HSI on the PFD as a black (instead of a gray) disc
     *
     */
    public boolean get_pfd_draw_hsi() {
        return get_preference(PREF_PFD_DRAW_HSI).equalsIgnoreCase("true");
    }

    /**
     * @return            - V-bar FD instead of crosshairs
     *
     */
    public boolean get_single_cue_fd() {
        return get_preference(PREF_SINGLE_CUE_FD).equalsIgnoreCase("true");
    }

    /**
     * @return            - Draw AOA
     *
     */
    public boolean get_draw_aoa() {
        return get_preference(PREF_DRAW_AOA).equalsIgnoreCase("true");
    }

    /**
     * @return            - Draw Radios
     *
     */
    public boolean get_pfd_draw_radios() {
        return get_preference(PREF_PFD_DRAW_RADIOS).equalsIgnoreCase("true");
    }
    
    /**
     * @return            - center ADI horizontally
     *
     */
    public boolean get_pfd_adi_centered() {
        return get_preference(PREF_PFD_ADI_CENTERED).equalsIgnoreCase("true");
    }

    /**
     * @return            - Draw Vmca and Vyse
     *
     */
    public boolean get_pfd_draw_twinspeeds() {
        return get_preference(PREF_PFD_DRAW_TWINSPEEDS).equalsIgnoreCase("true");
    }
    
    /**
     * @return            - Draw turn rate indicator
     *
     */
    public boolean get_draw_pfd_turnrate() {
        return get_preference(PREF_PFD_DRAW_TURNRATE).equalsIgnoreCase("true");
    }
    
    /**
     * @return            - Draw G-meter
     *
     */
    public boolean get_pfd_draw_gmeter() {
        return get_preference(PREF_PFD_DRAW_GMETER).equalsIgnoreCase("true");
    }

    /**
     * @return            - Draw Joke input
     *
     */
    public DrawJokeInputMode get_pfd_draw_joke_input() {
        if ( get_preference(PREF_PFD_DRAW_JOKE_INPUT).equalsIgnoreCase(JOKE_INPUT_NONE) ) { return DrawJokeInputMode.NONE; } 
        else if ( get_preference(PREF_PFD_DRAW_JOKE_INPUT).equalsIgnoreCase(JOKE_INPUT_AUTO) ) { return DrawJokeInputMode.AUTO; } 
        else if ( get_preference(PREF_PFD_DRAW_JOKE_INPUT).equalsIgnoreCase(JOKE_INPUT_RUDDER) ) { return DrawJokeInputMode.AUTO_RUDDER; }
        else if ( get_preference(PREF_PFD_DRAW_JOKE_INPUT).equalsIgnoreCase(JOKE_INPUT_ALWAYS) ) { return DrawJokeInputMode.ALWAYS; }
        else return DrawJokeInputMode.ALWAYS_RUDDER;
    }

    
    // EICAS

    /**
     * @return            - Draw only the primary engine indications
     *
     */
    public boolean get_eicas_primary_only() {
        String layout = get_preference(PREF_EICAS_LAYOUT);
        return ( layout.equalsIgnoreCase(EICAS_LAYOUT_PRIMARY) || layout.equalsIgnoreCase(EICAS_LAYOUT_PRIMARY_AND_CONTROLS) );
    }


    /**
     * @return            - Draw the position of trim, flaps, speedbrake, gear, autobrake, etc...
     *
     */
    public boolean get_eicas_draw_controls() {
        return get_preference(PREF_EICAS_LAYOUT).equalsIgnoreCase(EICAS_LAYOUT_PRIMARY_AND_CONTROLS);
    }


    /**
     * @return            - Override the (max) number of engines
     *
     */
    public int get_override_engine_count() {
        return Integer.parseInt( get_preference(PREF_OVERRIDE_ENGINE_COUNT) );
    }



    // MFD
    
    /**
     * @return            - Airport chart displays airport that is set as NAV destination
     *
     */
    public boolean get_arpt_chart_nav_dest() {
        return get_preference(PREF_ARPT_CHART_NAV_DEST).equalsIgnoreCase("true");
    }

    
    
    // CDU
    
    /**
     * @return            - CDU display only (without keyboard)
     *
     */
    public boolean cdu_display_only() {
        return get_preference(PREF_CDU_DISPLAY_ONLY).equalsIgnoreCase("true");
    }

    
    /**
     * Adds the given observer to the list of observers observing changes in the
     * preference addressed by key.
     *
     * @param observer    the observer observing key
     * @param key the key that identifies the observed preference
     */
    public void add_subsciption(PreferencesObserver observer, String key) {
        ArrayList observers;

        if (this.subscriptions.containsKey(key)) {
            observers = (ArrayList) this.subscriptions.get(key);
        } else {
            observers = new ArrayList();
        }

        observers.add(observer);

        this.subscriptions.put(key, observers);
    }

    // private methods ---------------------------------------------------------

    /**
     * Attempts to load preferences file. In case no preferences file can be
     * found, a new preferences file with default values is created.
     */
    private XHSIPreferences() {
        this.subscriptions = new HashMap();
        this.preferences = new Properties();
        this.unsaved_changes = false;
        load_preferences();
        ensure_preferences_complete();
        validate_preferences();
    }

    /**
     * Loads the properties file. If it does not exist, a new property file with
     * default values is created.
     */
    private void load_preferences() {
        logger.fine("Reading " + PROPERTY_FILENAME);
        try {
            FileInputStream fis = new FileInputStream(PROPERTY_FILENAME);
            this.preferences.load(fis);
            if ( fis != null ) fis.close();
        } catch (IOException e) {
            logger.warning("Could not read properties file. Creating a new property file with default values (" + e.toString() + ") ... ");
        }
    }

    /**
     * persistently stores the preferences
     */
    private void store_preferences() {
        if (this.unsaved_changes) {
            try {
                FileOutputStream fos = new FileOutputStream(PROPERTY_FILENAME);
                preferences.store(fos, null);
                if ( fos != null ) fos.close();
            } catch (IOException e2) {
                logger.warning("Could not store preferences file! (" + e2.toString() + ") ... ");
            }
            this.unsaved_changes = false;
        }
    }


    /**
     * Sets default values for all properties, that are not present in
     * this.preferences.
     *
     * @pre    preferences != null
     *
     * @throws RuntimeException in case preferences is not initialized
     */
    private void ensure_preferences_complete() {

        if (preferences == null)
            throw new RuntimeException("Prefereces object not initialized!");


        // SYSTEM

        if ( ! this.preferences.containsKey(PREF_PORT) ) {
            this.preferences.setProperty(PREF_PORT, "49020");
            this.unsaved_changes = true;
        }
        
        if ( ! this.preferences.containsKey(PREF_GROUP) ) {
            this.preferences.setProperty(PREF_GROUP, "239.255.0.120");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_MULTICAST) ) {
            this.preferences.setProperty(PREF_MULTICAST, "false");
            this.unsaved_changes = true;
        }
        
        if ( ! this.preferences.containsKey(PREF_APTNAV_DIR) ) {
            this.preferences.setProperty(PREF_APTNAV_DIR, ".");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_REPLAY_DELAY_PER_FRAME) ) {
            this.preferences.setProperty(PREF_REPLAY_DELAY_PER_FRAME, "50");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DISPLAY_STATUSBAR) ) {
            this.preferences.setProperty(PREF_DISPLAY_STATUSBAR, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_LOGLEVEL) ) {
            this.preferences.setProperty(PREF_LOGLEVEL, "CONFIG");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_INSTRUMENT_POSITION) ) {
            this.preferences.setProperty(PREF_INSTRUMENT_POSITION, PILOT);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_SIMCOM) ) {
            this.preferences.setProperty(PREF_SIMCOM, XHSI_PLUGIN);
            this.unsaved_changes = true;
        }

        // WINDOWS

        if ( ! this.preferences.containsKey(PREF_START_ONTOP) ) {
            this.preferences.setProperty(PREF_START_ONTOP, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_HIDE_WINDOW_FRAMES) ) {
            this.preferences.setProperty(PREF_HIDE_WINDOW_FRAMES, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANELS_LOCKED) ) {
            this.preferences.setProperty(PREF_PANELS_LOCKED, "false");
            this.unsaved_changes = true;
        }

        // one window

        for ( XHSIInstrument.DU du: XHSIInstrument.DU.values() ) {

            int i = du.get_id();

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_ACTIVE) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_ACTIVE, "true");
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_POS_X) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_POS_X, "" + (i * 20 + 20));
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_POS_Y) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_POS_Y, "" + (i * 20 + 20));
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_WIDTH) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_WIDTH, "480");
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_HEIGHT) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_HEIGHT, "480");
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_BORDER) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_BORDER, "8");
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_SQUARE) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_SQUARE, "false");
                this.unsaved_changes = true;
            }

            if ( ! this.preferences.containsKey(PREF_DU_PREPEND + i + PREF_DU_ORIENTATION) ) {
                this.preferences.setProperty(PREF_DU_PREPEND + i + PREF_DU_ORIENTATION, "N");
                this.unsaved_changes = true;
            }

        }

        if ( ! this.preferences.containsKey(PREF_CONWIN_MINIMIZED) ) {
            this.preferences.setProperty(PREF_CONWIN_MINIMIZED, "false");
            this.unsaved_changes = true;
        }

        // GRAPHICS

        if ( ! this.preferences.containsKey(PREF_INSTRUMENT_STYLE) ) {
            this.preferences.setProperty(PREF_INSTRUMENT_STYLE, INSTRUMENT_STYLE_SWITCHABLE);
            this.unsaved_changes = true;
        }
        
        if ( ! this.preferences.containsKey(PREF_BORDER_STYLE) ) {
            this.preferences.setProperty(PREF_BORDER_STYLE, BORDER_RELIEF);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_BORDER_COLOR) ) {
            this.preferences.setProperty(PREF_BORDER_COLOR, BORDER_GRAY);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_USE_MORE_COLOR) ) {
            this.preferences.setProperty(PREF_USE_MORE_COLOR, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ANTI_ALIAS) ) {
            this.preferences.setProperty(PREF_ANTI_ALIAS, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_BOLD_FONTS) ) {
//            if ( isMac() ) {
//                this.preferences.setProperty(PREF_BOLD_FONTS, "false");
//            } else {
                this.preferences.setProperty(PREF_BOLD_FONTS, "true");
//            }
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DRAW_BEZIER_PAVEMENTS) ) {
            this.preferences.setProperty(PREF_DRAW_BEZIER_PAVEMENTS, "true");
            this.unsaved_changes = true;
        }

        // Avionics

        if ( ! this.preferences.containsKey(PREF_USE_POWER) ) {
            this.preferences.setProperty(PREF_USE_POWER, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_AUTO_FRONTCOURSE) ) {
            this.preferences.setProperty(PREF_AUTO_FRONTCOURSE, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_HSI_SOURCE) ) {
            this.preferences.setProperty(PREF_HSI_SOURCE, USER);
            this.unsaved_changes = true;
        }

        // ND

        if ( ! this.preferences.containsKey(PREF_MIN_RWY_LEN) ) {
            this.preferences.setProperty(PREF_MIN_RWY_LEN, "100");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_RWY_LEN_UNITS) ) {
            this.preferences.setProperty(PREF_RWY_LEN_UNITS, "meters");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DRAW_RUNWAYS) ) {
            this.preferences.setProperty(PREF_DRAW_RUNWAYS, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DRAW_RANGE_ARCS) ) {
            this.preferences.setProperty(PREF_DRAW_RANGE_ARCS, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_LIMIT_ARCS_60DEG) ) {
            this.preferences.setProperty(PREF_LIMIT_ARCS_60DEG, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_AIRBUS_MODES) ) {
            this.preferences.setProperty(PREF_AIRBUS_MODES, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_MODE_MISMATCH_CAUTION) ) {
            this.preferences.setProperty(PREF_MODE_MISMATCH_CAUTION, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_TCAS_ALWAYS_ON) ) {
            this.preferences.setProperty(PREF_TCAS_ALWAYS_ON, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_CLASSIC_HSI) ) {
            this.preferences.setProperty(PREF_CLASSIC_HSI, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_APPVOR_UNCLUTTER) ) {
            this.preferences.setProperty(PREF_APPVOR_UNCLUTTER, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PLAN_AIRCRAFT_CENTER) ) {
            this.preferences.setProperty(PREF_PLAN_AIRCRAFT_CENTER, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DRAW_INSIDE_ROSE) ) {
            this.preferences.setProperty(PREF_DRAW_INSIDE_ROSE, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_COLORED_HSI_COURSE) ) {
            this.preferences.setProperty(PREF_COLORED_HSI_COURSE, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ND_NAVAID_FREQ) ) {
            this.preferences.setProperty(PREF_ND_NAVAID_FREQ, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ND_WRITE_AP_HDG) ) {
            this.preferences.setProperty(PREF_ND_WRITE_AP_HDG, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ND_SHOW_CLOCK) ) {
            this.preferences.setProperty(PREF_ND_SHOW_CLOCK, "true");
            this.unsaved_changes = true;
        }

        
        // PFD

        if ( ! this.preferences.containsKey(PREF_HORIZON_STYLE) ) {
            this.preferences.setProperty(PREF_HORIZON_STYLE, HORIZON_ROUNDED);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DIAL_TRANSPARENCY) ) {
            this.preferences.setProperty(PREF_DIAL_TRANSPARENCY, "50");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PFD_DRAW_HSI) ) {
            this.preferences.setProperty(PREF_PFD_DRAW_HSI, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_SINGLE_CUE_FD) ) {
            this.preferences.setProperty(PREF_SINGLE_CUE_FD, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DRAW_AOA) ) {
            this.preferences.setProperty(PREF_DRAW_AOA, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PFD_DRAW_RADIOS) ) {
            this.preferences.setProperty(PREF_PFD_DRAW_RADIOS, "false");
            this.unsaved_changes = true;
        }
       
        if ( ! this.preferences.containsKey(PREF_PFD_ADI_CENTERED) ) {
            this.preferences.setProperty(PREF_PFD_ADI_CENTERED, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PFD_DRAW_TWINSPEEDS) ) {
            this.preferences.setProperty(PREF_PFD_DRAW_TWINSPEEDS, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PFD_DRAW_TURNRATE) ) {
            this.preferences.setProperty(PREF_PFD_DRAW_TURNRATE, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PFD_DRAW_GMETER) ) {
            this.preferences.setProperty(PREF_PFD_DRAW_GMETER, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PFD_DRAW_JOKE_INPUT) ) {
            this.preferences.setProperty(PREF_PFD_DRAW_JOKE_INPUT, JOKE_INPUT_AUTO);
            this.unsaved_changes = true;
        }
        
        // EICAS
        
        if ( ! this.preferences.containsKey(PREF_EICAS_LAYOUT) ) {
            this.preferences.setProperty(PREF_EICAS_LAYOUT, EICAS_LAYOUT_FULL);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_OVERRIDE_ENGINE_COUNT) ) {
            this.preferences.setProperty(PREF_OVERRIDE_ENGINE_COUNT, "0");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ENGINE_TYPE) ) {
            this.preferences.setProperty(PREF_ENGINE_TYPE, ENGINE_TYPE_SWITCHABLE);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_TRQ_SCALE) ) {
            this.preferences.setProperty(PREF_TRQ_SCALE, TRQ_SCALE_SWITCHABLE);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_FUEL_UNITS) ) {
            this.preferences.setProperty(PREF_FUEL_UNITS, FUEL_UNITS_SWITCHABLE);
            this.unsaved_changes = true;
        }

        
        // MFD

        if ( ! this.preferences.containsKey(PREF_MFD_MODE) ) {
            this.preferences.setProperty(PREF_MFD_MODE, MFD_MODE_SWITCHABLE);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ARPT_CHART_COLOR) ) {
            this.preferences.setProperty(PREF_ARPT_CHART_COLOR, ARPT_DIAGRAM_COLOR_AUTO);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ARPT_CHART_NAV_DEST) ) {
            this.preferences.setProperty(PREF_ARPT_CHART_NAV_DEST, "true");
            this.unsaved_changes = true;
        }


        // CDU
        
        if ( ! this.preferences.containsKey(PREF_CDU_DISPLAY_ONLY) ) {
            this.preferences.setProperty(PREF_CDU_DISPLAY_ONLY, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_CDU_SOURCE) ) {
            this.preferences.setProperty(PREF_CDU_SOURCE, CDU_SOURCE_SWITCHABLE);
            this.unsaved_changes = true;
        }

        
        if (this.unsaved_changes) {
            store_preferences();
        }

    }

    /**
     * Validates the current preferences and adjusts program status accordingly.
     * The following validations are performed:
     *
     * - X-Plane home directory exists and contains earth nav databases.
     *
     */
    private void validate_preferences() {
        // verify that X-Plane directory exists
        if (new File(this.preferences.getProperty(PREF_APTNAV_DIR)).exists() == false) {
            logger.severe("AptNav Resources directory not found. Will not read navigation data!");
            XHSIStatus.nav_db_status = XHSIStatus.STATUS_NAV_DB_NOT_FOUND;
        } else if (
                ! ( new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default data/earth_nav.dat").exists() ||
                    new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/earth_nav.dat").exists() ) ||
                ! ( new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default data/earth_fix.dat").exists() ||
                    new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/earth_fix.dat").exists() ) ||
                ! ( new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default data/earth_awy.dat").exists() ||
                    new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/earth_awy.dat").exists() ) ||
                ! ( new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default scenery/default apt dat/Earth nav data/apt.dat").exists() ||
                    new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/apt.dat").exists() )
                )
        {
            logger.warning("One or more of the navigation databases (NAV, APT, FIX, AWY) could not be found!");
            XHSIStatus.nav_db_status = XHSIStatus.STATUS_NAV_DB_NOT_FOUND;
        } else {
            logger.fine("Navigation databases found");
            XHSIStatus.nav_db_status = XHSIStatus.STATUS_NAV_DB_NOT_LOADED;
        }
    }


    /**
     * Notifies all preferences observers which have subscribed to the
     * preference identified by key that a change occured.
     *
     * @param key  the changed preference
     */
    private void notify_observers(String key) {
        if (this.subscriptions.containsKey(key)) {
            ArrayList observers = (ArrayList) this.subscriptions.get(key);
            for (int i=0; i<observers.size(); i++) {
                PreferencesObserver pref_obs = (PreferencesObserver) observers.get(i);
                pref_obs.preference_changed(key);
            }
        }
    }


    private boolean isMac() {
        return (System.getProperty("mrj.version") != null);
    }


}
