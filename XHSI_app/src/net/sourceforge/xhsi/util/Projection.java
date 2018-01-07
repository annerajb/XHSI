/**
* Projection.java
* 
* Interface for projections used in the navigation displays
*  - MovingMap
*  - WeatherRadar
*  - Terrain
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009-2014  Marc Rogiers (marrog.123@gmail.com)
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

public interface Projection {

  /**
   * Set scale in pixels per nautic mile; 
   * default is 1 pixel per nm (very coarse)
   */
  public void setScale(float ppnm);

  /**
   * pixel position of the aircraft in the graphic context; mandatory before using any projection
   * @param x : integer
   * @param y : integer
   */
  public void setCenter(int x, int y);
  
  /**
   * Set aircraft geographic position; mandatory before using any projection
   * @param acf_lat : float - latitude
   * @param acf_lon : float - longitude
   */
  public void setAcf(float acf_lat, float acf_lon);
  
  /**
   * Set geographical point to project
   * @param lat : float - latitude
   * @param lon : float - longitude
   */
  public void setPoint(float lat, float lon);
  
  /**
   * Read projected point x coordinate
   * @return : integer x 
   */   
  public int getX();
  
  /**
   * Read projected point y coordinate
   * @return : integer y
   */    
  public int getY();
}
