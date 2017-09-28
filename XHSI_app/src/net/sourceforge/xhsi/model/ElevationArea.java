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
	
	public float min_lat;
	public float min_lon;
	public float max_lat;
	public float max_lon;
	
	// Description from GeoVu header file .hdr
	
	public String file_tile = "Tile A Elevation";
	public int number_of_display_color;
	// public palette = TOPO_LAND_256;
	// public data_type = image;
	public ByteOrder data_byte_order = ByteOrder.LITTLE_ENDIAN;
	public float upper_map_y = 90.0f;
	public float lower_map_y = 50.0f;
	public float left_map_x = -180.0f; 
	public float right_map_x = -90.0f;
	public int number_of_rows = 4800;
	public int number_of_columns = 10800;
	public float grid_size_x = 0.00833333f; // (upper_map_y - lower_map_y) / number_of_rows
	public float grid_size_y = 0.00833333f; // (right_map_x - left_map_x) / number_of_columns
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

	public ElevationArea (MappedByteBuffer buffer, int columns, int rows, float left_map_x, float upper_map_y, float right_map_x, float lower_map_y, String area_name) {
		elevation_data = buffer;
		elevation_data.order(this.data_byte_order);
		this.left_map_x = left_map_x;
		this.upper_map_y = upper_map_y;
		this.right_map_x = right_map_x;
		this.lower_map_y = lower_map_y;
		this.number_of_rows = rows;
		this.number_of_columns = columns;
		this.grid_size_x = (this.upper_map_y - this.lower_map_y) / this.number_of_rows;
		this.grid_size_y = (this.right_map_x - this.left_map_x) / this.number_of_columns;
		this.file_tile = area_name;
	}
	
	public float get_elevation( float lat, float lon ) {
		if ((lat > upper_map_y ) || ( lat < lower_map_y) || (lon <left_map_x ) || (lon > right_map_x) ) {
			return missing_flag;
		} else {
			int offset = Math.round((lat-upper_map_y) * grid_size_y)*number_of_columns 
					+ Math.round((lon-left_map_x) * grid_size_x); 
			int elevation = elevation_data.getShort(2*offset);
			return elevation;
		}
		
	}
	
}

