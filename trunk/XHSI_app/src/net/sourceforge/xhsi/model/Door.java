/**
* Door.java
* 
* Model class for a door.
* 
* Copyright (C) 2015 Nicolas Carel
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

import net.sourceforge.xhsi.model.Aircraft.DoorId;

public class Door {
	public static final int PAX = 0;
	public static final int CARGO = 1;
	public static final int BULK = 2;
	public static final int MAINTENANCE = 3;
	public static final int EMERGENCY = 4;
	public static final int EMERGENCY_WITH_SLIDER = 5;
	public static final int LEGEND_NONE = 0;
	public static final int LEGEND_LEFT = 1;
	public static final int LEGEND_RIGHT = 2;
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean closed;
	public int door_type;
	public String legend;
	public boolean has_slider;
	public boolean slider_armed;
	public int legend_side;
	public int legend_m;
	public DoorId id;
		
	public Door (int x, int y, int width, int height, int door_type, int legend_side, DoorId id) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.closed = true;
		this.door_type = door_type;
		this.legend_side = legend_side;
		this.legend_m = y+width/2;
		this.id = id;		
		switch (door_type) {
		case PAX : 
			this.legend = "CABIN";
			this.has_slider = true;
			this.slider_armed = false;
			break;
		case CARGO : 
			this.legend = "CARGO";
			this.has_slider = false;
			this.slider_armed = false;
			break;	
		case BULK : 
			this.legend = "BULK";
			this.has_slider = false;
			this.slider_armed = false;
			break;	
		case MAINTENANCE : 
			this.legend = "AVIONIC";
			this.has_slider = false;
			this.slider_armed = false;
			break;
		case EMERGENCY : 
			this.legend = "EMER EXIT";
			this.has_slider = false;
			this.slider_armed = false;
			break;
		case EMERGENCY_WITH_SLIDER : 
			this.legend = "EMER EXIT";
			this.has_slider = true;
			this.slider_armed = true;
			break;
		}		
	}
}
