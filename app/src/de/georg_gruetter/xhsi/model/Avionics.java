/**
* Avionics.java
* 
* Model for an aircrafts avionics systems
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
package de.georg_gruetter.xhsi.model;

public interface Avionics {
	
	public static final int EFIS_ADF = 0;
	public static final int EFIS_OFF = 1;
	public static final int EFIS_NAV = 2;
	
	/**
	 * @return int - selected range of map display in kilometers
	 */
	public int map_range();
	
	/**
	 * @return int - EFIS1 setting - either EFIS_ADF, EFIS_NAV or EFIS_OFF
	 */
	public int efis1_setting();

	/**
	 * @return int - EFIS2 setting - either EFIS_ADF, EFIS_NAV or EFIS_OFF
	 */
	public int efis2_setting();

	/**
	 * @return boolean - true if EFIS displays waypoints, false otherwise
	 */
	public boolean efis_shows_waypoints();

	/**
	 * @return boolean - true if EFIS displays VORs, false otherwise
	 */
	public boolean efis_shows_vors();

	/**
	 * @return boolean - true if EFIS displays NDBs, false otherwise
	 */
	public boolean efis_shows_ndbs();

	/**
	 * @return boolean - true if EFIS displays airports, false otherwise
	 */
	public boolean efis_shows_airports();

	/**
	 * @return boolean - true if EFIS displays TCAS information, false otherwise
	 */
	public boolean efis_shows_tcas();
	
	/**
	 * @return NavigationRadio - model class representing the currently selected radio on bank 1 or null, if none is selected
	 */
	public NavigationRadio get_selected_radio1();

	/**
	 * @return NavigationRadio - model class representing the currently selected radio on bank 2 or null, if none is selected
	 */
	public NavigationRadio get_selected_radio2();

	/**
	 * @return Localizer - model class representing the currently selected localizer or null, if none is selected
	 */	
	public Localizer get_selected_localizer();
	

	/**
	 * @return float - selected course for NAV1 in degrees
	 */
	public float nav1_course();

	/**
	 * @return float - selected course for NAV2 in degrees
	 */
	public float nav2_course();

	/**
     * TODO: constants for autopilot states need to be defined
     * 
	 * @return int - bitmask for autopilot state
	 */
	public int autpilot_state();
	
	/**
	 * @return float - vertical velocity in feet per minute selected in autopilot
	 */
	public float autopilot_vertical_velocity();
	
	/**
	 * @return float - altitude in feed selected in autopilot
	 */
	public float autopilot_altitude();

	/**
	 * @return float - heading in degrees selected in autopilot
	 */
	public float heading_bug();		// degree radians

	/**
	 * @return Aircraft - reference to aircraft model class to which avionics belongs
	 */
	public Aircraft get_aircraft();
	
	/**
	 * @return FMS - reference to flight management system model class
	 */
	public FMS get_fms();
}
