/**
* Localizer.java
* 
* Model class for a runways localizer
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

public class Localizer extends RadioNavigationObject {

	public static final int TYPE_ILS_LOCALIZER = 4;
	public static final int TYPE_STANDALONE_LOCALIZER = 5;
	public static final int TYPE_GLIDESCOPE = 6;
	public static final int TYPE_OUTER_MARKER = 7;
	public static final int TYPE_MIDDLE_MARKER = 8;
	public static final int TYPE_INNER_MARKER = 9;
	
	public int type;
	public float direction;
	public String airport;
	public String rwy;
	
	public Localizer(
			String name,
			String ilt,
			int type, 
			float lat, 
			float lon, 
			float elevation, 
			float frequency,
			int range,
			float direction,
			String airport,
			String rwy) {
		super(name, ilt, lat, lon, elevation, frequency, range);
		
		this.direction = direction;
		this.airport = airport;
		this.rwy = rwy;
		this.type = type;
	}
	
	public String toString() {
		return "ILS " + this.airport + " " + this.rwy + " @ (" + this.lat + "," + this.lon + ") freq=" + this.frequency;
	}	
}
