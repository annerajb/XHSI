/**
* NavigationObject.java
* 
* Model class for a navigation object (VOR, NDB, FIX, AIRPORT, Localizer)
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2019  Nicolas Carel
* 
* Maths from: http://edwilliams.org/avform.htm
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

import java.text.DecimalFormat;

public class NavigationObject {

	public String name;
	public float lat;
	public float lon;
	
	public static int NO_TYPE_RUNWAY = 4;
	public static int NO_TYPE_AIRPORT = 3;
	public static int NO_TYPE_FIX = 2;
	public static int NO_TYPE_NDB = 1;
	public static int NO_TYPE_VOR = 0;
	
    private DecimalFormat lat_formatter;
    private DecimalFormat lon_formatter;
    
	public NavigationObject(String name, float lat, float lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	    lat_formatter = new DecimalFormat("00");
	    lon_formatter = new DecimalFormat("000");
	}
	
	public NavigationObject(float lat, float lon) {		
		this.lat = lat;
		this.lon = lon;
	    lat_formatter = new DecimalFormat("00");
	    lon_formatter = new DecimalFormat("000");
	    this.name = standardName();
	}
	
	public String toString() {
		return "Navigation object '" + this.name + " @ (" + this.lat + "," + this.lon + ") ";
	}
	
	/**
	 * Distance to destination NavigationOject in NM
	 * This is a distance at FL250
	 * @param destination
     * @return distance in NM
	 */
    public float distanceTo(NavigationObject destination) {

        double a1 = Math.toRadians(this.lat);
        double b1 = Math.toRadians(this.lon);
        double a2 = Math.toRadians(destination.lat);
        double b2 = Math.toRadians(destination.lon);

        // why 3443.9f? Wikipedia says that the average radius of the earth is 3440.07NM
        // because we are not on ground !!! This is FL250 radius. To be exact, we should
        // take the average flight level as the earth radius reference. 

        return (float) Math.acos(
                    (Math.cos(a1) * Math.cos(b1) * Math.cos(a2) * Math.cos(b2)) +
                    (Math.cos(a1) * Math.sin(b1) * Math.cos(a2) * Math.sin(b2)) +
                    (Math.sin(a1) * Math.sin(a2))
                ) * 3443.9f;
    }
    
    /**
     * Bearing to destination NavigationObject 
     * Bearing is defined as direction or an angle, 
     * between the north-south line of earth or meridian 
     * and the line connecting the target and the reference point.
     * @param destination
     * @return true course in degrees (i.e. not magnetic !)
     */
    public float bearingTo(NavigationObject destination) {
    	/*
        Let ‘R’ be the radius of Earth,
        ‘L’ be the longitude,
        ‘θ’ be latitude,
        ‘β‘ be Bearing.    	
    	
    	X = cos θb * sin ∆L;
    	Y = cos θa * sin θb – sin θa * cos θb * cos ∆L;
    	*/
    	double delta_L = this.lon - destination.lon;
    	double X = Math.cos(destination.lat) * Math.sin(delta_L);
    	double Y = Math.cos(this.lat) * Math.sin(destination.lat) - Math.sin(this.lat) * Math.cos(destination.lat) * Math.cos (delta_L);
    	double β = Math.atan2(X,Y);
    	
    	return (float) β;
    }
    
    /**
     * Bearing to from (lat,lon) 
     * Bearing is defined as direction or an angle, 
     * between the north-south line of earth or meridian 
     * and the line connecting the target and the reference point.
     * @param latitude and longitude
     * @return true course in degrees (i.e. not magnetic !)
     */
    public float bearingFrom(float lat, float lon) {
    	/*
        Let ‘R’ be the radius of Earth,
        ‘L’ be the longitude,
        ‘θ’ be latitude,
        ‘β‘ be Bearing.    	
    	
    	X = cos θb * sin ∆L;
    	x= sin(long2−long1)∗cos(lat2)
    	
    	Y = cos θa * sin θb – sin θa * cos θb * cos ∆L;
    	y=cos(lat1)∗sin(lat2)−sin(lat1)∗cos(lat2)∗cos(long2−long1)
    	*/
    	
        double lat1 = Math.toRadians(lat);
        double lon1 = Math.toRadians(lon);
        double lat2 = Math.toRadians(this.lat);
        double lon2 = Math.toRadians(this.lon);
        
    	double delta_L = lon2 - lon1;
    	double x = Math.cos(lat2) * Math.sin(delta_L);
    	
    	double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos (delta_L);
    	double bearing = Math.toDegrees(Math.atan2(x,y));
    	if (bearing < 0) bearing += 360;
    	return (float) bearing;
    }
    
    public NavigationObject bearingDistancePoint(float bearing, float distance) {
        /*
        Let first point latitude be la1,
        longitude as lo1,
        d be distance,
        R as radius of Earth,
        Ad be the angular distance i.e d/R and
        θ or tc (true course) be the bearing,

         Here is the formula to find the second point, when first point, bearing and distance is known:

        latitude of second point = la2 =  asin(sin la1 * cos Ad  + cos la1 * sin Ad * cos θ), and
        longitude  of second point = lo2 = lo1 + atan2(sin θ * sin Ad * cos la1 , cos Ad – sin la1 * sin la2)
        
        other math source: 
          double lat  = asin(sin(lat1)*cos(d)+cos(lat1)*sin(d)*cos(tc));
          double dlon = atan2(sin(tc)*sin(d)*cos(lat1),cos(d)-sin(lat1)*sin(lat));
          double lon  = mod( lon1-dlon +pi,2*pi )-pi;
        */
        double lat1 = Math.toRadians(this.lat);
        double lon1 = Math.toRadians(this.lon);
        double tc = Math.toRadians(bearing);
        double d = distance;  // What unit ??? NM or meters ???
        
        double lat  = Math.asin(Math.sin(lat1)*Math.cos(d)+Math.cos(lat1)*Math.sin(d)*Math.cos(tc));
        double dlon = Math.atan2(Math.sin(tc)*Math.sin(d)*Math.cos(lat1),Math.cos(d)-Math.sin(lat1)*Math.sin(lat));
        double lon  = ( lon1-dlon + Math.PI % 2*Math.PI ) - Math.PI;
        
    	NavigationObject waypoint = new NavigationObject((float) Math.toDegrees(lat),(float) Math.toDegrees(lon));
    	return waypoint;
    }
    
    /**
     * This give a name to unnamed LAT/LON Waypoint
     * @return : String - Waypoint 6 characters standard name N00W000 
     */
    public String standardName() {
    	String std_name= (this.lat >= 0 ? "N" :"S") + lat_formatter.format(Math.abs(this.lat)) +
    			(this.lon >= 0 ? "E" :"W") + lon_formatter.format(Math.abs(this.lon));
    	return std_name;    	
    }
}

