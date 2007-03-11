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
package de.georg_gruetter.xhsi.model;

public class FMSEntry extends NavigationObject {

	public int type;
	public float altitude;
	public boolean active;
	
	public FMSEntry(String name, int type, float lat, float lon, float altitude, boolean active) {
		super(name, lat, lon);
		this.type = type;
		this.altitude = altitude;
		this.active = active;
	}
	
	public String toString() {
		return this.name + " @ (" + this.lat + "¡, " + this.lon + "¡)";
	}
}
