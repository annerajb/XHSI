/**
* CoordinateSystem.java
* 
* Provides various computation methods for calculating distances.
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

public class CoordinateSystem {
	
	public static float km_per_deg_lon(float lat) {
		return (float) (Math.cos(Math.toRadians(lat)) * 2.0f * Math.PI * (6370.0f/360.0f)); 		
	}
	
	public static float nm_per_deg_lon(float lat) {
		return (km_per_deg_lon(lat) / 1.852f);
	}
	
	public static float deg_lon_per_km(float lat) {
		return (float) (1.0f / km_per_deg_lon(lat));
	}
	
	public static float deg_lat_per_km() {
		return 1.0f / (60.0f * 1.852f);
	}
	
	public static float km_per_deg_lat() {
		return 1.0f / deg_lat_per_km();
	}
	
	public static float nm_per_deg_lat() {
		return 60.0f;
	}
	
	public static float distance(float lat1, float lon1, float lat2, float lon2) {

		double a1 = Math.toRadians(lat1);
		double b1 = Math.toRadians(lon1);
		double a2 = Math.toRadians(lat2);
		double b2 = Math.toRadians(lon2);
		
		return (float) (Math.acos
				(
					(Math.cos(a1) * Math.cos(b1) * Math.cos(a2) * Math.cos(b2)) +
					(Math.cos(a1) * Math.sin(b1) * Math.cos(a2) * Math.sin(b2)) +
					(Math.sin(a1) * Math.sin(a2))
				) * 3443.9f);
	}
}
