/**
 * CylindricalProjection.java
 *
 * This Class is not used. 
 * All unused components have been commented out.
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

package net.sourceforge.xhsi.util;

public class CylindricalProjection implements Projection {

   
    // private float c_lat;
    // private float c_lon;
 
    private int map_x;
    private int map_y;
	
    // private float pixels_per_nm;
    // private int map_center_x;
    // private int map_center_y;
    
	public CylindricalProjection() {
	}

	
    public void setScale(float ppnm) {
    	// pixels_per_nm = ppnm;
    }
    
    public void setCenter(int x, int y) {
    	// map_center_x = x;
    	// map_center_y = y;
    }
    

	public void setAcf(float acf_lat, float acf_lon) {
        // interesting, but not used...
        // c_lat = acf_lat;
        // c_lon = acf_lon;
	}

	public void setPoint(float lat, float lon) {
		// TODO: maths !!!
        // map_x = cylindrical_lon_to_x(lon);
        // map_y = cylindrical_lat_to_y(lat);
		map_x=0;
		map_y=0;
	}

	public int getX() {
		return map_x;
	}

	public int getY() {
		return map_y;
	}

}

