/**
* Aircraft.java
* 
* Model class for an aircraft.
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
package net.sourceforge.xhsi.model;

public interface Aircraft {

    /**
     * @return float - latitude in degrees
     */
    public float lat();

    /**
     * @return float - longitude in degrees
     */
    public float lon();

    /**
     * @return float - MSL in meters
     */
    public float msl_m();

    /**
     * @return float - AGL in meters
     */
    public float agl_m();

    /**
     * @return float - turn speed in degrees per second
     */
    public float turn_speed();

    /**
     * @return float - ground speed in knots
     */
    public float ground_speed();

    /**
     * @return float - true air speed in knots
     */
    public float true_air_speed();

    /**
     * @return float - magnetic heading of the aircraft in degrees
     */
    public float heading();

    /**
     * @return float - hpath in degrees
     */
    public float hpath();

    /**
     * @return float - altitude
     */
    public float indicated_altitude();

    /**
     * @return float - vertical velocity
     */
    public float indicated_vv();

    /**
     * Returns the magnetic track of the aircraft in degrees. If ground_speed
     * is lower than 5 knots, returns heading of aircraft.
     *
     * @return float - magnetic track of the aircraft in degrees
     */
    public float track();

    /**
     * Returns the difference between the track and the heading
     * of the aircraft.
     *
     * @return float - difference of horizontal path and heading in degrees
     */
    public float drift();

    /**
     * @return float - roll angle of the aircraft in degrees
     */
    public float roll_angle();

    /**
     * @return float - magnetic variation at current position
     */
    public float magnetic_variation();

    /**
     * Returns the distance between the aircraft and the given navigation
     * object in nautical miles.
     *
     * @param nav_object - the navigation object to which the distance should be computed
     * @return float - distance to nav_object in nautical miles
     */
    public float distance_to(NavigationObject nav_object);

    /**
     * Returns a rough distance between the aircraft and the given navigation
     * object in nautical miles. This calculation is less accurate but much
     * faster than <code>distance_to</code>.
     *
     * @param nav_object - the navigation object to which the distance should be computed
     * @return float - distance to nav_object in nautical miles
     */
    public float rough_distance_to(NavigationObject nav_object);

    /**
     * Returns the EET to arive at the given
     * navigation object based on the distance to the aircraft and its
     * current ground speed. Note: we assume the aircraft is on a direct
     * course to nav_object. I do not consider the actual closing speed.
     *
     * @param nav_object - the navigation object
     * @return long - the EET to the nav_object
     */
    public long ete_to(NavigationObject nav_object);

    /**
     * Returns the zulu ETA time when the aircraft will arive at the given
     * navigation object based on the distance to the aircraft and its
     * current ground speed. Note: we assume the aircraft is on a direct
     * course to nav_object. I do not consider the actual closing speed.
     *
     * @param nav_object - the navigation object
     * @return long - the arrival time at nav_object in zulu time
     */
    public long time_when_arriving_at(NavigationObject nav_object);

    /**
     * Returns the EET to fly the given distance at the current
     * ground speed.
     *
     * @param distance - the distance to cover
     * @return long - the time to fly the distance
     */
    public long ete_for_distance(float distance);

    /**
     * Returns the time in hours to fly the given distance at the current
     * ground speed.
     *
     * @param distance - the distance to cover
     * @return long - the time to fly the distance
     */
    public long time_after_distance(float distance);

    /**
     * Returns the current time + EET
     *
     * @param eet - the EET in minutes
     * @return long - current time + EET
     */
    public long time_after_ete(float eet);

    /**
     * @return float - simulator zulu time in seconds
     */
    public float sim_time_zulu();

    /**
     * @return Avionics - reference to avionics model of this aircraft
     */
    public Avionics get_avionics();

    /**
     * @return AircraftEnvironment - reference to environment model of this aircraft
     */
    public AircraftEnvironment get_environment();


}
