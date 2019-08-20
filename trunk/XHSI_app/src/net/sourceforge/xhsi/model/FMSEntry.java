/**
* FMSEntry.java
* 
* Model class for an entry in the flight management system (FMS)
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
package net.sourceforge.xhsi.model;

public class FMSEntry extends NavigationObject {

    public static final int ARPT = 1;
    public static final int NDB = 2;
    public static final int VOR = 4;
    public static final int FIX = 512;
    public static final int LATLON = 2048;


    public int index;
    public int type;
    public int altitude;
    public int speed;
    public int wind_mag;
    public int wind_speed;
    public float bearing_to;
    public float leg_dist;
    public float total_ete;
    public boolean active;
    public boolean displayed;
    public boolean discontinuity;
    public boolean overfly;
    // Holding pattern
    public boolean holding;
    public int hold_track;
    public boolean hold_left; // True for non standard (left) turn
    public float hold_dist;   // holding distance in Nm
    // DME ARC
    
    /**
     * Distance of turn anticipation in NM
     */    
    public float dta;
    
    /**
     * Maximum Distance of turn anticipation in NM
     * This is LEG distance from the previous point
     * minus the stabilization distance
     */
    public float max_dta;
    
    /**
     * radius of turn, in NM
     * Rot = (V+Vw)² / (68626 * tan (bank_angle))
     * V = True Air Speed
     * Vw = Max Wind Speed (default to 20 kts / Waypoint wind speed if specified in FMS data)
     */
    public float radius_of_turn;

    /**
     * This point is on the actual leg, ie terminating at this point
     */
    public NavigationObject turn_wp;
    
    /**
     * For fly by waypoint, this point equals this interception waypoint.
     * For fly over waypoint, this point is at the end of the turn
     * 
     */
    public NavigationObject backturn_wp;
    
    /**
     * This point is on the next leg, ie starting from this point 
     */
    public NavigationObject interception_wp;
    public NavigationObject turn_center;
    public NavigationObject back_turn_center;
    
    /**
     * Turn angle in degrees
     */
    public float turn_angle;
    
    /**
     * Back turn angle in degrees
     */
    public float backturn_angle;
    
    /**
     * True if a back turn is forecast on this poin
     */
    public boolean back_turn;
    
    public FMSEntry() {
        super("", 0.0f, 0.0f);
        this.index = 0;
        this.type = 0;
        this.altitude = 0;
        this.speed = 0;
        this.wind_mag = 0;
        this.wind_speed = 0;
        this.bearing_to = 0;
        this.leg_dist = 0.0f;
        this.total_ete = 0.0f;
        this.active = false;
        this.displayed = false;
        this.discontinuity = false;
        this.overfly = false;
        this.holding = false;
        this.hold_track = 0;
        this.hold_left = false;
        this.hold_dist = 1;
        dta=0;
        max_dta=0;
        radius_of_turn=1; // Default to 1 NM, avoiding division by 0 exceptions
        turn_wp = null;
        
        
    }


    public FMSEntry(int index, String name, int type, float lat, float lon, int altitude, float bearing_to, float leg_dist, float total_ete, boolean active, boolean displayed) {
        super(name, lat, lon);
        this.index = index;
        this.type = type;
        this.altitude = altitude;
        this.speed = 0;
        this.wind_mag = 0;
        this.wind_speed = 0;
        this.bearing_to = bearing_to;
        this.leg_dist = leg_dist;
        this.total_ete = total_ete;
        this.active = active;
        this.displayed = displayed;
        this.discontinuity = (lat==0.0) && (lon == 0.0);
        this.holding = false;
        this.hold_track = 0;
        this.hold_left = false;
        this.hold_dist = 1;
// "L/L" is already set by the plugin
//        if (type == 2048) {
//            this.name = "L/L";
//        }
    }

    
    public String toString() {
        return this.name + " @ (" + this.lat + "\u00B0, " + this.lon + "\u00B0)";
    }

}
