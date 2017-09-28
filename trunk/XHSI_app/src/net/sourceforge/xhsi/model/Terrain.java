/**
* Terrain.java
* 
* Model class for Terrain elevation.
* 
* Copyright (C) 2017  Nicolas Carel
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

public class Terrain {
	
	public enum EGPWSTerrainColor { Magenta, Black, LD_Green, HD_Green, LD_Yellow, HD_Yellow, Red };
	
	public float get_elevation(float lat, float lon) {
		return 0.0f;
	}

	/**
	 * 
	 * @param lat : latitude
	 * @param lon : longitude
	 * @param alt : predicted altitude in 60 seconds (please take care of negative V/S)
	 * @return
	 */
	public EGPWSTerrainColor get_egpws_level(float lat, float lon, float alt) {
		float terrain_alt = get_elevation(lat, lon);
		float delta_alt = alt-terrain_alt;
		if (terrain_alt == -32767) 
			return EGPWSTerrainColor.Magenta;
		else if (delta_alt > 2000) 
			return EGPWSTerrainColor.Red;
		else if (delta_alt > 1000)		
			return EGPWSTerrainColor.HD_Yellow;
		else if (delta_alt > -500)		
			return EGPWSTerrainColor.LD_Yellow;
		else if (delta_alt > -1000)		
			return EGPWSTerrainColor.HD_Green;
		else if (delta_alt > -2000)		
			return EGPWSTerrainColor.LD_Green;
		else 
			return EGPWSTerrainColor.Black;

	}

}
