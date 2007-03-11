/**
* HSIPreferences.java
* 
* Provides read and write access to XHSI preferences. Encapsulates
* persistence mechanisms.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;


public class HSIPreferences {
	
	private static final String PROPERTY_FILENAME = "XHSI.properties";
	
	public static final String PREF_XPLANE_DIR = "x-plane.dir";
	public static final String PREF_REPLAY_DELAY_PER_FRAME = "flight.session.replay.delay.between.steps";
	public static final String PREF_PORT = "port";
	public static final String PREF_DISPLAY_STATUSBAR = "display.statusbar";
	public static final String PREF_LOGLEVEL = "loglevel";
	
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	/**
	 * The properties object holding all preferences
	 */
	private Properties preferences;
	
	/**
	 * Singleton instance of this class
	 */
	private static HSIPreferences single_instance = null;
	
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
	 * @return		HSIPreferences - the single instance of HSIPreferences
	 */
	public static HSIPreferences get_instance() {
		if (HSIPreferences.single_instance == null) {
			HSIPreferences.single_instance = new HSIPreferences();
		}
		return HSIPreferences.single_instance;
	}
	
	/**
	 * @param key		- the key of the preference
	 * @param value	- the new value of the preference
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
	 * @param key		- the key of the preference to be returned
	 * @return			- the value of the preference
	 * 
	 * @throws RuntimeException	- in case no preference key is set
	 * @throws RuntimeException	- in case key is null or empty
	 */
	public String get_preference(String key) {
		if ((key == null) || (key.trim().equals("")))
			throw new RuntimeException("key must not be null or empty!");
		if (this.preferences.containsKey(key) == false)
			throw new RuntimeException("no preference with key '" + key + "' is known!");
		
		return this.preferences.getProperty(key);
	}
	
	/**
	 * Adds the given observer to the list of observers observing changes in the
	 * preference addressed by key.
	 * 
	 * @param observer	the observer observing key
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
	private HSIPreferences() {
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
		logger.fine("reading preferences");
		try {
			this.preferences.load(new FileInputStream(PROPERTY_FILENAME));
		} catch (IOException e) {
			logger.warning("could not read properties file. will create and use new property file with default values (" + e.toString() + ") ... ");
		}
	}
	
	/**
	 * persistently stores the preferences
	 */
	private void store_preferences() {
		if (this.unsaved_changes) {
			try {
				preferences.store(new FileOutputStream(PROPERTY_FILENAME),null);
			} catch (IOException e2) {
				logger.warning("could not store preferences file! (" + e2.toString() + ") ... ");
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
			throw new RuntimeException("prefereces object not initialized!");
		
		
		if (this.preferences.containsKey(PREF_PORT) == false) {
			this.preferences.setProperty(PREF_PORT,"49001");
			this.unsaved_changes = true;
		}
		
		if (this.preferences.containsKey(PREF_XPLANE_DIR) == false) {
			this.preferences.setProperty(PREF_XPLANE_DIR,"<Put your X-Plane Directory here!>");
			this.unsaved_changes = true;
		}
		
		if (this.preferences.containsKey(PREF_REPLAY_DELAY_PER_FRAME) == false) {
			this.preferences.setProperty(PREF_REPLAY_DELAY_PER_FRAME,"50");	
			this.unsaved_changes = true;
		}
		
		if (this.preferences.containsKey(PREF_DISPLAY_STATUSBAR) == false) {
			this.preferences.setProperty(PREF_DISPLAY_STATUSBAR, "true");
			this.unsaved_changes = true;
		}
		
		if (this.preferences.containsKey(PREF_LOGLEVEL) == false) {
			this.preferences.setProperty(PREF_LOGLEVEL,"WARNING");
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
		if (new File(this.preferences.getProperty(PREF_XPLANE_DIR)).exists() == false) {
			logger.warning("X-Plane home directory not found. will not read navigation data!");
			HSIStatus.nav_db_status = HSIStatus.STATUS_NAV_DB_NOT_FOUND;
		} else if ((new File(this.preferences.getProperty(PREF_XPLANE_DIR) + "/Resources/Earth Nav Data/nav.dat").exists() == false) ||
		            (new File(this.preferences.getProperty(PREF_XPLANE_DIR) + "/Resources/Earth Nav Data/apt.dat").exists() == false) ||
		            (new File(this.preferences.getProperty(PREF_XPLANE_DIR) + "/Resources/Earth Nav Data/fix.dat").exists() == false)) {
			logger.warning("One or more of X-Planes earth nav databases (nav.dat, apt.dat, fix.dat) could not be found!");
			HSIStatus.nav_db_status = HSIStatus.STATUS_NAV_DB_NOT_FOUND;
		} else {
			logger.fine("navigation datbases found");
			HSIStatus.nav_db_status = HSIStatus.STATUS_NAV_DB_NOT_LOADED;
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
			for (int i=0;i<observers.size();i++) 
				((PreferencesObserver) observers.get(i)).preference_changed(key);
		}
	}


}
