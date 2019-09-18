/**
* Helipad.java
* 
* Model class for an helipad.
* 
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

public class Helipad extends NavigationObject {

    public static final int RWY_ASPHALT = 1;
    public static final int RWY_CONCRETE = 2;
    public static final int RWY_GRASS = 3;
    public static final int RWY_DIRT = 4;
    public static final int RWY_GRAVEL = 5;
    public static final int RWY_DRY_LAKEBED = 12;
    public static final int RWY_WATER = 13;
    public static final int RWY_SNOW = 14;
    public static final int RWY_TRNSPARENT = 15;

    public float length;
    public float width;
    public int surface;
    public String pad_identifier;

    public Helipad(String name, float length, float width, int surface, String pad_identifier, float lat, float lon) {
        super(name, lat, lon);
        this.length = length;
        this.width = width;
        this.surface = surface;
        this.pad_identifier = pad_identifier;
    }

    public String toString() {
        return "PAD " + this.name + " " + this.pad_identifier + "' @ (" + this.lat + "," + this.lon + ")";
    }
    
}