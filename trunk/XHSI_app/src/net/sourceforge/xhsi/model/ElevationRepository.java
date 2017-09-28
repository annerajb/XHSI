/**
* ElevationRepository.java
* 
* Manages and provides access to elevation database
* via various accessors and search methods.
* 
* Copyright (C) 2017 Nicolas Carel
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
import java.util.logging.Logger;


public class ElevationRepository {

    private static final Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static ElevationRepository single_instance;

    private ArrayList<ElevationArea> elevation_areas;

    public static ElevationRepository get_instance() {
        if (ElevationRepository.single_instance == null) {
        	ElevationRepository.single_instance = new ElevationRepository();
        }
        return ElevationRepository.single_instance;
    }

    private ElevationRepository() {
        init();
    }
    
    public void AddElevationArea(ElevationArea area) {
    	elevation_areas.add(area);
    }
    
    public void init() { 
    	elevation_areas = new ArrayList<ElevationArea>();
    }
    
	public float get_elevation( float lat, float lon ) {
		float elevation = 0.0f;
		// find the aera containing the coordinates
		for (ElevationArea area:elevation_areas ) {
			if (lat >= area.lower_map_y && lat <= area.upper_map_y && lon >= area.left_map_x && lon <= area.right_map_x) {
				elevation = area.get_elevation(lat, lon);
			}
		}
		return elevation;
	}
    
}
