/**
* Heliport.java
* 
* Model class for an heliport
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2019  Nicolas Carel
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

import java.util.ArrayList;

public class Heliport extends NavigationObject {

	public String icao_code;
	public ArrayList<Helipad> helipads;
	public int elev;
	public ArrayList<ComRadio> com_radios;
	
	public Heliport(String name, String icao_code, 
			float lat, float lon, 
			ArrayList<Helipad> helipads,
			int elev, 
			ArrayList<ComRadio> com_radios) {
		super(name, lat, lon);
		this.icao_code = icao_code.trim();
		this.helipads = helipads;
		this.elev = elev;
		this.com_radios = com_radios;
	}
	
	public String toString() {
		return "Heliport '" + this.name + "' @ (" + this.lat + "," + this.lon + ")";
	}	
}
