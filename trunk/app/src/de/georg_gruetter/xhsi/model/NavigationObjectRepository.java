/**
* NavigationObjectRepository.java
* 
* Manages and provides access to navigation objects (VORs, NDBs, fixes,
* airports via various accessors and search methods.
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

import java.util.ArrayList;
import java.util.HashMap;

public class NavigationObjectRepository {
	
	private ArrayList vors[][];
	private ArrayList ndbs[][];
	private ArrayList fixes[][];
	private ArrayList airports[][];
	private HashMap frequencies;
	
	private static NavigationObjectRepository single_instance; 
	
	public static NavigationObjectRepository get_instance() {
		if (NavigationObjectRepository.single_instance == null) {
			NavigationObjectRepository.single_instance = new NavigationObjectRepository();
		}
		return NavigationObjectRepository.single_instance;
	}
	
	private NavigationObjectRepository() {
		init();
	}

	public void init() {
		 vors = new ArrayList[181][361];
		 ndbs = new ArrayList[181][361];
		 fixes = new ArrayList[181][361];
		 airports = new ArrayList[181][361];
		 frequencies = new HashMap();

		 for (int lat=0;lat<181;lat++) {
			 for (int lon=0;lon<361;lon++) {
				 vors[lat][lon] = new ArrayList();
				 ndbs[lat][lon] = new ArrayList();
				 fixes[lat][lon] = new ArrayList();
				 airports[lat][lon] = new ArrayList();
			 }
		 }
	}
	
	public ArrayList get_nav_objects(int type, float lat, float lon) {
		if (type == NavigationObject.NO_TYPE_VOR) {
			return this.vors[get_lat_index(lat)][get_lon_index(lon)];
		} else if (type == NavigationObject.NO_TYPE_NDB) {
			return this.ndbs[get_lat_index(lat)][get_lon_index(lon)];			
		} else if (type == NavigationObject.NO_TYPE_FIX) {
			return this.fixes[get_lat_index(lat)][get_lon_index(lon)];			
		} else if (type == NavigationObject.NO_TYPE_AIRPORT) {
			return this.airports[get_lat_index(lat)][get_lon_index(lon)];			
		} else {
			return new ArrayList();
		}
	}

	public ArrayList get_nav_objects(int type, NavigationObject nav_object) {
		return get_nav_objects(type, nav_object.lat, nav_object.lon);
	}
		
	public void add_nav_object(NavigationObject nav_object) {
		if (nav_object instanceof VOR) {
			VOR vor = (VOR) nav_object;
			if (vor.type == VOR.TYPE_NDB) {
				get_nav_objects(NavigationObject.NO_TYPE_NDB, nav_object).add(nav_object);
			} else if (vor.type == VOR.TYPE_VOR) {
				get_nav_objects(NavigationObject.NO_TYPE_VOR, nav_object).add(nav_object);
			}
		} else if (nav_object instanceof Fix) {
			get_nav_objects(NavigationObject.NO_TYPE_FIX, nav_object).add(nav_object);
		} else if (nav_object instanceof Airport) {
			get_nav_objects(NavigationObject.NO_TYPE_AIRPORT, nav_object).add(nav_object);
		}
		
		if (nav_object instanceof RadioNavigationObject) {
			RadioNavigationObject rno = (RadioNavigationObject) nav_object;
			if (rno.frequency != 0.0) {
				add_freq(rno.frequency, rno); 
			}			
		}
	}
	
	private void add_freq(float frequency, NavigationObject nav_object) {
		Float freq_key = new Float(frequency);
		ArrayList nos;
		if (this.frequencies.containsKey(freq_key)) {
			nos = (ArrayList) frequencies.get(freq_key);
		} else {
			nos = new ArrayList();
		}
		nos.add(nav_object);
		frequencies.put(freq_key, nos);
	}
	
	private ArrayList get_nav_objects(float freq) {
		Float freq_key = new Float(freq);
		if (this.frequencies.containsKey(freq_key)) {
			return (ArrayList) this.frequencies.get(freq_key);
		} else {
			return new ArrayList();
		}
	}
		
	public RadioNavigationObject find_tuned_nav_object(float aircraft_lat, float aircraft_lon, float freq) {
		ArrayList nos = get_nav_objects(freq);
		RadioNavigationObject rno = null;
		boolean found_rno = false;
		int index = 0;
		float distance;
		
		if (nos.isEmpty() == false) {
			while ((found_rno == false) && (index<nos.size())) {
				rno = (RadioNavigationObject) nos.get(index);
				distance = CoordinateSystem.distance(aircraft_lat, aircraft_lon, rno.lat, rno.lon);
				if (distance < ((rno.range + 2.0) * 1.85319f)) {	
					found_rno = true;
				} else {
					index += 1;
				}
			}
		}
		
		if (found_rno)
			return rno;
		else
			return null;		
	}

	private int get_lat_index(float lat) {
		return (int) lat + 90;
	}
	
	private int get_lon_index(float lon) {
		return (int) lon + 180;
	}	
}
