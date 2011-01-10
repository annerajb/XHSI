/**
* XHSIPreferences.java
* 
* Provides read and write access to XHSI preferences. Encapsulates
* persistence mechanisms.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
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

    public static final String PREF_APTNAV_DIR = "aptnav.dir";
    public static final String PREF_REPLAY_DELAY_PER_FRAME = "replay.steps.delay";
    public static final String PREF_PORT = "port";
    public static final String PREF_LOGLEVEL = "loglevel";
    public static final String PREF_INSTRUMENT_POSITION = "instrument.position";
    public static final String PREF_DISPLAY_STATUSBAR = "display.statusbar";
    public static final String PREF_USE_AVIONICS_POWER = "use.avionics.power";
    public static final String PREF_ANTI_ALIAS = "anti-alias";
    public static final String PREF_BORDER_STYLE = "border.style";
    public static final String PREF_PANEL_SQUARE = "panel.square";
    public static final String PREF_MIN_RWY_LEN = "minimum.runway.length";
    public static final String PREF_RWY_LEN_UNITS = "runway.length.units";
    public static final String PREF_DRAW_RUNWAYS = "draw.runways";
    public static final String PREF_AIRBUS_MODES = "airbus.modes";
    public static final String PREF_DRAW_RANGE_ARCS = "draw.range.arcs";
    public static final String PREF_USE_MORE_COLOR = "use.more.color";
    public static final String PREF_MODE_MISMATCH_CAUTION = "mode.mismatch.caution";
    public static final String PREF_TCAS_ALWAYS_ON = "tcas.always.on";
    public static final String PREF_BOLD_FONTS = "bold.fonts";
    public static final String PREF_START_ONTOP = "start.ontop";
    public static final String PREF_CLASSIC_HSI = "classic.hsi";
    public static final String PREF_APPVOR_UNCLUTTER = "appvor.unclutter";
    public static final String PREF_PLAN_AIRCRAFT_CENTER = "plan.aircraft.center";

    public static final String PREF_HIDE_WINDOW_FRAME = "hide.window.frame";
    public static final String PREF_PANEL_LOCKED = "panel.locked";
    public static final String PREF_PANEL_POS_X = "panel.pos.x";
    public static final String PREF_PANEL_POS_Y = "panel.pos.y";
    public static final String PREF_PANEL_WIDTH = "panel.width";
    public static final String PREF_PANEL_HEIGHT = "panel.height";
    public static final String PREF_PANEL_BORDER = "panel.border";
    public static final String PREF_ND_ORIENTATION = "nd.orientation";

    public static final String PILOT = "pilot";
    public static final String COPILOT = "copilot";
    public static final String INSTRUCTOR = "instructor";

    public static final String ND_UP = "Normal";
    public static final String ND_LEFT = "Left";
    public static final String ND_RIGHT = "Right";
    public static final String ND_UPSIDE_DOWN = "Upside_down";

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
        notify_observers(key);
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


    /**
     * @return            - hide menu options that can be set by X-Plane or not
     *
     */
    public boolean get_use_avionics_power() {
        return get_preference(PREF_USE_AVIONICS_POWER).equalsIgnoreCase("true");
    }


    /**
     * @return            - hide menu options that can be set by X-Plane or not
     *
     */
    public boolean get_anti_alias() {
        return get_preference(PREF_ANTI_ALIAS).equalsIgnoreCase("true");
    }


    /**
     * @return            - return pilot/copilot/instructor
     *
     */
    public String get_instrument_operator() {
        return get_preference(PREF_INSTRUMENT_POSITION);
    }


    /**
     * @return            - the minimum length of runway that the airports should have in order to be dispalyed on the map
     *
     */
    public Float get_min_rwy_length() {
        Float min_rwy_length = Float.parseFloat(get_preference(PREF_MIN_RWY_LEN));
        String rwy_units = get_preference(PREF_RWY_LEN_UNITS);
        if (rwy_units.equals("feet")) min_rwy_length *= 0.3048f;

        return min_rwy_length;
    }


    /**
     * @return            - start with "Keep window on top" already on or not
     *
     */
    public boolean get_start_ontop() {
        return get_preference(PREF_START_ONTOP).equalsIgnoreCase("true");
    }


    /**
     * @return            - draw decorated window
     *
     */
    public boolean get_hide_window_frame() {
        return get_preference(PREF_HIDE_WINDOW_FRAME).equalsIgnoreCase("true");
    }


    /**
     * @return            - lock the window size and position
     *
     */
    public boolean get_panel_locked() {
        return get_preference(PREF_PANEL_LOCKED).equalsIgnoreCase("true");
    }


    /**
     * @return            - the border size
     *
     */
    public int get_panel_border() {
        return Integer.parseInt(get_preference(PREF_PANEL_BORDER));
    }


    /**
     * @return            - the panel width
     *
     */
    public int get_panel_pos_x() {
        return Integer.parseInt(get_preference(PREF_PANEL_POS_X));
    }


    /**
     * @return            - the panel height
     *
     */
    public int get_panel_pos_y() {
        return Integer.parseInt(get_preference(PREF_PANEL_POS_Y));
    }


    /**
     * @return            - the panel width
     *
     */
    public int get_panel_width() {
        return Integer.parseInt(get_preference(PREF_PANEL_WIDTH));
    }


    /**
     * @return            - the panel height
     *
     */
    public int get_panel_height() {
        return Integer.parseInt(get_preference(PREF_PANEL_HEIGHT));
    }


    /**
     * @return            - draw range arcs or not
     *
     */
    public boolean get_draw_range_arcs() {
        return get_preference(PREF_DRAW_RANGE_ARCS).equalsIgnoreCase("true");
    }


    /**
     * @return            - draw a fancy border or not
     *
     */
    public boolean get_fancy_border() {
        return get_preference(PREF_BORDER_STYLE).equalsIgnoreCase("fancy");
    }


    /**
     * @return            - border color
     *
     */
    public String get_border_color() {
        if ( get_fancy_border() ) {
            return "white";
        } else {
            return get_preference(PREF_BORDER_STYLE);
        }
    }


    /**
     * @return            - keep the panel square or not
     *
     */
    public boolean get_panel_square() {
        return get_preference(PREF_PANEL_SQUARE).equalsIgnoreCase("true");
    }


    /**
     * @return            - draw the runways or not
     *
     */
    public boolean get_draw_runways() {
        return get_preference(PREF_DRAW_RUNWAYS).equalsIgnoreCase("true");
    }


    /**
     * @return            - draw the runways or not
     *
     */
    public boolean get_airbus_modes() {
        return get_preference(PREF_AIRBUS_MODES).equalsIgnoreCase("true");
    }


    /**
     * @return            - use more color or not
     *
     */
    public boolean get_use_more_color() {
        return get_preference(PREF_USE_MORE_COLOR).equalsIgnoreCase("true");
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
     * @return            - Display Centered APP and VOR as classic HSI without map
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
     * @return            - use bold fonts or not
     *
     */
    public boolean get_bold_fonts() {
        return get_preference(PREF_BOLD_FONTS).equalsIgnoreCase("true");
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


        if ( ! this.preferences.containsKey(PREF_PORT) ) {
            this.preferences.setProperty(PREF_PORT, "49020");
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

        if ( ! this.preferences.containsKey(PREF_USE_AVIONICS_POWER) ) {
            this.preferences.setProperty(PREF_USE_AVIONICS_POWER, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ANTI_ALIAS) ) {
            this.preferences.setProperty(PREF_ANTI_ALIAS, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_INSTRUMENT_POSITION) ) {
            this.preferences.setProperty(PREF_INSTRUMENT_POSITION, PILOT);
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_START_ONTOP) ) {
            this.preferences.setProperty(PREF_START_ONTOP, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_HIDE_WINDOW_FRAME) ) {
            this.preferences.setProperty(PREF_HIDE_WINDOW_FRAME, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_ND_ORIENTATION) ) {
            this.preferences.setProperty(PREF_ND_ORIENTATION, "0");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_LOCKED) ) {
            this.preferences.setProperty(PREF_PANEL_LOCKED, "false");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_BORDER) ) {
            this.preferences.setProperty(PREF_PANEL_BORDER, "10");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_POS_X) ) {
            this.preferences.setProperty(PREF_PANEL_POS_X, "20");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_POS_Y) ) {
            this.preferences.setProperty(PREF_PANEL_POS_Y, "20");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_WIDTH) ) {
            this.preferences.setProperty(PREF_PANEL_WIDTH, "600");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_HEIGHT) ) {
            this.preferences.setProperty(PREF_PANEL_HEIGHT, "600");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_BORDER_STYLE) ) {
            this.preferences.setProperty(PREF_BORDER_STYLE, "fancy");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_PANEL_SQUARE) ) {
            this.preferences.setProperty(PREF_PANEL_SQUARE, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_LOGLEVEL) ) {
            this.preferences.setProperty(PREF_LOGLEVEL, "WARNING");
            this.unsaved_changes = true;
        }

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

        if ( ! this.preferences.containsKey(PREF_USE_MORE_COLOR) ) {
            this.preferences.setProperty(PREF_USE_MORE_COLOR, "true");
            this.unsaved_changes = true;
        }

        if ( ! this.preferences.containsKey(PREF_DRAW_RANGE_ARCS) ) {
            this.preferences.setProperty(PREF_DRAW_RANGE_ARCS, "true");
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

        if ( ! this.preferences.containsKey(PREF_BOLD_FONTS) ) {
            if ( isMac() ) {
                this.preferences.setProperty(PREF_BOLD_FONTS, "false");
            } else {
                this.preferences.setProperty(PREF_BOLD_FONTS, "true");
            }
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
            logger.warning("AptNav Resources directory not found. Will not read navigation data!");
            XHSIStatus.nav_db_status = XHSIStatus.STATUS_NAV_DB_NOT_FOUND;
        } else if ((new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default data/earth_nav.dat").exists() == false) ||
                    (new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default scenery/default apt dat/Earth nav data/apt.dat").exists() == false) ||
                    (new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default data/earth_fix.dat").exists() == false) ||
                    (new File(this.preferences.getProperty(PREF_APTNAV_DIR) + "/Resources/default data/earth_awy.dat").exists() == false)) {
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
            for (int i=0; i<observers.size(); i++)
                ((PreferencesObserver) observers.get(i)).preference_changed(key);
        }
    }


    private boolean isMac() {
        return (System.getProperty("mrj.version") != null);
    }


}
