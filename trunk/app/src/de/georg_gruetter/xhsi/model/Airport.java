/**
* Airport.java
* 
* Model class for an airport
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

public class Airport extends NavigationObject {

	public String icao_code;
	
	public Airport(String name, String icao_code, float lat, float lon) {
		super(name, lat, lon);
		this.icao_code = icao_code;
	}
	
	public String toString() {
		return "Airport '" + this.name + "' @ (" + this.lat + "," + this.lon + ")";
	}	
}
