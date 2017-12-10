/**
* WeatherArea.java
* 
* Grant acces to a weather area
* 
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

import java.util.logging.Logger;

public class WeatherArea {

	public String name;
	public float min_lat;
	public float max_lat;
	public float min_lon; 
	public float max_lon;
	public int number_of_rows;
	public int number_of_columns;
	public float grid_size_x; 
	public float grid_size_y; 
	public float grid_step_x;
	public float grid_step_y;
	private boolean slice_valid[];
	private byte data[][];
	
	private long last_update;
	
    private static final Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	public WeatherArea(int columns, int rows, float min_lat, float max_lat, float min_lon, float max_lon) {
		// elevation_data = buffer;
		// elevation_data.order(this.data_byte_order);
		name="Lat "+(int)min_lat + " Lon " + (int)min_lon;
		this.min_lat = min_lat;
		this.max_lat = max_lat;
		this.min_lon = min_lon;
		this.max_lon = max_lon;
		this.number_of_rows = rows;
		this.number_of_columns = columns;
		this.grid_size_x = (this.max_lon - this.min_lon) / this.number_of_columns;
		this.grid_size_y = (this.max_lat - this.min_lat) / this.number_of_rows;
		grid_step_x = 1/grid_size_x;
		grid_step_y = 1/grid_size_y;
		slice_valid = new boolean[number_of_rows];
		data = new byte[number_of_rows][number_of_columns];
		last_update = 0;
		for (int i=0; i<number_of_rows; i++ ) {
			slice_valid[i]=false;
		}
	}
	
	/**
	 * 
	 * @param lat : latitude
	 * @param lon : longitude
	 * @return : integer - 0=Calm to 9=huricane
	 * 
	 */
	public int get_storm_level(float lat, float lon) {
		if (lat >= min_lat && lat <= max_lat && lon >= min_lon && lon <= max_lon) {
			int lat_index = (int)((lat-min_lat) * grid_step_y) % number_of_rows; 				
			int lon_index = (int)((lon-min_lon) * grid_step_x) % number_of_columns;
			if (slice_valid[lat_index]) 
				return data[lat_index][lon_index]; 
			else
				return -1;
		} else {
			return -1;
		}	
	}
	
	/**
	 * 
	 * @param lat : latitude
	 * @param lon : longitude
	 * @return : float - 0=Calm to 9=huricane
	 * 
	 */
	public float get_interpolated_storm_level(float lat, float lon) {
		if (lat >= min_lat && lat <= max_lat && lon >= min_lon && lon <= max_lon) {
			int lat_index = (int)((lat-min_lat) * grid_step_y) % number_of_rows; 				
			int lon_index = (int)((lon-min_lon) * grid_step_x) % number_of_columns;
			if (slice_valid[lat_index]) 
				if ((lon_index+1)<number_of_columns) {
					// we can interpolate longitude
					int data_left = data[lat_index][lon_index];
					int data_right = data[lat_index][lon_index+1];
					float lon_shift = ((lon-min_lon) * grid_step_x) - lon_index; // this should be a float between 0 and 1
					if ((lat_index+1)<number_of_rows) {
						int data_bottom_left = data[lat_index+1][lon_index];
						int data_bottom_right = data[lat_index+1][lon_index];
						float lat_shift = ((lat-min_lat) * grid_step_y) - lat_index; // this should be a float between 0 and 1
						float data_top = data_left*(1-lon_shift)+data_right*lon_shift;
						float data_bottom = data_bottom_left*(1-lon_shift)+data_bottom_right*lon_shift;
						return data_top*(1-lat_shift)+data_bottom*lat_shift;
					} else 
						return data_left*(1-lon_shift)+data_right*lon_shift;
				} else {
					return data[lat_index][lon_index];	
				}
				 
			else
				return -1;
		} else {
			return -1;
		}	
	}
	
	public int get_lat_offset( float lat, float lon ) {
		if (lat >= min_lat && lat <= max_lat && lon >= min_lon && lon <= max_lon) {
			int offset = Math.round((lat-min_lat) * grid_step_y); 
			return offset;
		} else {
			return -1;
		}		
	}
	
	public int get_lon_offset( float lat, float lon ) {
		if (lat >= min_lat && lat <= max_lat && lon >= min_lon && lon <= max_lon) {
			int offset = Math.round((lon-min_lon) * grid_step_x); 
			return offset;
		} else {
			return -1;
		}		
	}
	
	public void write_slice(int slice_pos, byte[] slice) {
		slice_valid[slice_pos]=true;
		last_update = System.currentTimeMillis();
		// System.arraycopy(slice, 0, data, slice_pos*this.number_of_columns,  this.number_of_rows);
		for (int i=0; i<this.number_of_columns; i++) data[slice_pos][i] = slice[i];
		if (slice_pos==0) logger.fine("area lat:" + this.min_lat + " lon:" + this.min_lon+ "  weather slice " + slice_pos + " updated" );
	}
	
	public boolean updated() {
		return System.currentTimeMillis() - last_update < 60000;
	}

}
