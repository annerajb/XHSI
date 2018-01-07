/**
* AzimuthalEquidistantProjection.java
* 
* Projections used in the navigation displays
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



public class AzimuthalEquidistantProjection implements Projection {
    
    private double phi1;
    private double sin_phi1;
    private double cos_phi1;
    private double lambda0;
    
    private double phi;
    private double sin_phi;
    private double cos_phi;
    private double lambda;
    private double d_lambda;
    private double sin_d_lambda;
    private double cos_d_lambda;
    
    // private double cos_rho;
    // private double tan_theta;
    private double rho;
    private double theta;
    
    private float x;
    private float y;
    
    private float pixels_per_nm;
    private int map_center_x;
    private int map_center_y;
    
    public AzimuthalEquidistantProjection() {
    	// Set default values to avoid exceptions
    	pixels_per_nm=1;
    	map_center_x=0;
    	map_center_y=0;   	
    }
    
    public void setScale(float ppnm) {
    	pixels_per_nm = ppnm;
    }
    
    public void setCenter(int x, int y) {
    	map_center_x = x;
    	map_center_y = y;
    }

    public void setAcf(float acf_lat, float acf_lon) {
        phi1 = Math.toRadians(acf_lat);
        lambda0 = Math.toRadians(acf_lon);
        sin_phi1 = Math.sin(phi1);
        cos_phi1 = Math.cos(phi1);
    }
    
    public void setPoint(float lat, float lon) {
        phi = Math.toRadians(lat);
        lambda = Math.toRadians(lon);
        sin_phi = Math.sin(phi);
        cos_phi = Math.cos(phi);
        d_lambda = lambda - lambda0;
        sin_d_lambda = Math.sin(d_lambda);
        cos_d_lambda = Math.cos(d_lambda);
        // cos_rho = sin_phi1 * sin_phi + cos_phi1 * cos_phi * cos_d_lambda;
        // tan_theta = cos_phi * sin_d_lambda / ( cos_phi1 * sin_phi - sin_phi1 * cos_phi * cos_d_lambda );
        rho = Math.acos(sin_phi1 * sin_phi + cos_phi1 * cos_phi * cos_d_lambda);
        theta = Math.atan2(cos_phi1 * sin_phi - sin_phi1 * cos_phi * cos_d_lambda, cos_phi * sin_d_lambda);
        x = (float)(rho * Math.sin(theta));
        y = - (float)(rho * Math.cos(theta));
    }
    
    public int getX() {     
        return Math.round(this.map_center_x - y * 180.0f / (float)Math.PI * 60.0f * this.pixels_per_nm);    
    }
    
    public int getY() {
        return Math.round(this.map_center_y - x * 180.0f / (float)Math.PI * 60.0f * this.pixels_per_nm);
    }
    
}