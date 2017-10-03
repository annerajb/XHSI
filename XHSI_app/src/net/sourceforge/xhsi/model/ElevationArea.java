/**
* ElevationArea.java
* 
* Grant acces to a memory map elevation area
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

import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ElevationArea {
	
	public enum ElevationUnit { METER, FEET };

	// Description from GeoVu header file .hdr
	
	public String file_tile = "Tile A Elevation";
	public int number_of_display_color;
	// public palette = TOPO_LAND_256;
	// public data_type = image;
	public ByteOrder data_byte_order = ByteOrder.LITTLE_ENDIAN;
	public float min_lat;
	public float max_lat;
	public float min_lon; 
	public float max_lon;
	public int number_of_rows = 4800;
	public int number_of_columns = 10800;
	public float grid_size_x = 0.00833333f; // (upper_map_y - lower_map_y) / number_of_rows
	public float grid_size_y = 0.00833333f; // (right_map_x - left_map_x) / number_of_columns
	public float grid_step_x;
	public float grid_step_y;
	// public grid_unit = degrees;
	// public grid_origin = upperleft_x;
	// public grid_cell_registration = upperleft;
	// public map_projection = lat/lon;
	public ElevationUnit elevation_unit = ElevationUnit.METER;
    int missing_flag = -500;
    float elevation_max = 8572;
    float elevation_min = -432;
    String comment_1 = "Globe file";
    String comment_2 = "";
	
	MappedByteBuffer elevation_data;

	public ElevationArea (MappedByteBuffer buffer, int columns, int rows, float min_lat, float max_lat, float min_lon, float max_lon, String area_name) {
		elevation_data = buffer;
		elevation_data.order(this.data_byte_order);
		this.min_lat = min_lat;
		this.max_lat = max_lat;
		this.min_lon = min_lon;
		this.max_lon = max_lon;
		this.number_of_rows = rows;
		this.number_of_columns = columns;
		this.grid_size_x = (this.max_lon - this.min_lon) / this.number_of_columns;
		this.grid_size_y = (this.max_lat - this.min_lat) / this.number_of_rows;
		this.file_tile = area_name;
		grid_step_x = 1/grid_size_x;
		grid_step_y = 1/grid_size_y;
	}
	
	public float get_elevation( float lat, float lon ) {
		if (lat >= min_lat && lat <= max_lat && lon >= min_lon && lon <= max_lon) {
			int offset = get_offset(lat,lon); 
			int elevation = elevation_data.getShort(2*offset);
			return elevation;
		} else {
			return missing_flag*2;
		}
		
	}
	
	public int get_offset( float lat, float lon ) {
		if (lat >= min_lat && lat <= max_lat && lon >= min_lon && lon <= max_lon) {
			int offset = Math.round((max_lat-lat) * grid_step_y)*number_of_columns 
					+ Math.round((lon-min_lon) * grid_step_x); 
			return offset;
		} else {
			return -1;
		}		
	}
	
	public int check_bof () {
		return elevation_data.getShort(0);
	}
}

