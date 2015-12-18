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

public class Door {
	public static final int PAX = 0;
	public static final int CARGO = 1;
	public static final int MAINTENANCE = 2;
	public static final int EMERGENCY = 3;
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean closed;
	public int door_type;
		
	public Door (int x, int y, int width, int height, int door_type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.closed = true;
		this.door_type = door_type;
	}
}
