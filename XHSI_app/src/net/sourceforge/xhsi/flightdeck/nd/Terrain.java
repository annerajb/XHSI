/**
* Terrain.java
* 
* Display Enhanced Ground Proximity Warning System Terrain (EGPWS)
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

package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.ElevationRepository;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.ModelFactory;

public class Terrain extends NDSubcomponent {

    private static final long serialVersionUID = 1L;

    private AffineTransform original_at;
    
    float map_up;
    float center_lon;
    float center_lat;
    float pixels_per_deg_lon;
    float pixels_per_deg_lat;
    float pixels_per_nm;
      
    ElevationRepository elevRepository;
    
    DecimalFormat coordinates_formatter;
    
    private interface Projection {
      public void setAcf(float acf_lat, float acf_lon);
      public void setPoint(float lat, float lon);
      public int getX();
      public int getY();
    }
    
    private class AzimuthalEquidistantProjection implements Projection {
        
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
      private double rho;
      private double theta;
      
      private float x;
      private float y;      
  
      public AzimuthalEquidistantProjection() {
          
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
          rho = Math.acos(sin_phi1 * sin_phi + cos_phi1 * cos_phi * cos_d_lambda);
          theta = Math.atan2(cos_phi1 * sin_phi - sin_phi1 * cos_phi * cos_d_lambda, cos_phi * sin_d_lambda);
          x = (float)(rho * Math.sin(theta));
          y = - (float)(rho * Math.cos(theta));
      }
      
      public int getX() {
          
          return Math.round(nd_gc.map_center_x - y * 180.0f / (float)Math.PI * 60.0f * pixels_per_nm);
      
      }
      
      public int getY() {
          
          return Math.round(nd_gc.map_center_y - x * 180.0f / (float)Math.PI * 60.0f * pixels_per_nm);
      
      }
      
  }
  
  private Projection map_projection = new AzimuthalEquidistantProjection();
 
    
	public Terrain(ModelFactory model_factory, NDGraphicsConfig nd_gc,
			Component parent_component) {
		super(model_factory, nd_gc, parent_component);
        this.elevRepository = ElevationRepository.get_instance();
        this.coordinates_formatter = new DecimalFormat("00.0");
        DecimalFormatSymbols symbols = coordinates_formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        coordinates_formatter.setDecimalFormatSymbols(symbols);
	}

	public void paint(Graphics2D g2) {
        if ( nd_gc.powered && (!( nd_gc.mode_app || nd_gc.mode_vor )) ) {
        	if ( avionics.efis_shows_terrain() && ( ! nd_gc.map_zoomin ) )
            drawTerrain(g2,nd_gc.max_range);        	
        }

	}
	
	private void drawTerrain(Graphics2D g2, float radius_scale) {
		
		float peak_min = 8500;
		float peak_max = 0;
		
		// TERRAIN indicator (debug)
		int debug_y = nd_gc.frame_size.height*6/10;
		g2.setColor(nd_gc.terrain_label_color);
		g2.drawString("TERRAIN ON", nd_gc.map_center_x, nd_gc.frame_size.height*6/10);
		debug_y += nd_gc.line_height_l;
		g2.setFont(nd_gc.font_s);
		
        this.center_lat = this.aircraft.lat();
        this.center_lon = this.aircraft.lon();

        // for the PLAN mode, the center of the map can be displayed or active FMS waypoint
        if ( nd_gc.mode_plan ) {
            if ( ( ! this.preferences.get_plan_aircraft_center() ) && this.fms.is_active() ) {
                FMSEntry entry = (FMSEntry) this.fms.get_displayed_waypoint();
                if ( entry == null ) {
                    entry = (FMSEntry) this.fms.get_active_waypoint();
                }
                if ( entry != null ) {
                    // center of plan mode
                    this.center_lat = entry.lat;
                    this.center_lon = entry.lon;
                }
            }
        }
        
        this.pixels_per_nm = (float)nd_gc.rose_radius / radius_scale; // float for better precision
        if ( nd_gc.map_zoomin ) this.pixels_per_nm *= 100.0f;

        this.map_projection.setAcf(this.center_lat, this.center_lon);
        
        // determine max and min lat/lon in viewport to only draw those
        // elements that can be displayed
        float delta_lat = radius_scale * CoordinateSystem.deg_lat_per_nm();
        float delta_lon = radius_scale * CoordinateSystem.deg_lon_per_nm(this.center_lat);
        float range_multiply = this.preferences.get_draw_only_inside_rose() ? 1.0f : 1.5f;
        // multiply by 1.5f to draw symbols outside the rose
        float lat_max = this.center_lat + delta_lat * range_multiply;
        float lat_min = this.center_lat - delta_lat * range_multiply;
        float lon_max = this.center_lon + delta_lon * range_multiply;
        float lon_min = this.center_lon - delta_lon * range_multiply;
        int nb_tile_x = 150;
        int nb_tile_y = 150;
        int tile_width = (int)(nd_gc.frame_size.width*range_multiply*1.5f/nb_tile_x);
        int tile_height = (int)(nd_gc.frame_size.height*range_multiply*1.5f/nb_tile_y);
        float lat_step = (lat_max - lat_min) / nb_tile_y;
        float lon_step = (lon_max - lon_min) / nb_tile_x;
        
        // rotate to TRUE! aircraft heading or track, or North
        AffineTransform original_at = g2.getTransform();
        if ( nd_gc.hdg_up ) {
            // HDG UP
            this.map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
        } else if ( nd_gc.trk_up ) {
            // TRK UP
            this.map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
        } else {
            // North UP
            this.map_up = 0.0f;
        }
        g2.transform( AffineTransform.getRotateInstance(
                Math.toRadians(-1.0f * this.map_up),
                nd_gc.map_center_x,
                nd_gc.map_center_y)
        );
        
        float ref_alt = ref_altitude();
        
        for (float lat=lat_min; lat<= lat_max; lat+=lat_step) {
            for (float lon=lon_min; lon<=lon_max; lon+=lon_step) {
            	float elevation = elevRepository.get_elevation(lat, lon)*3.2808f;
                String area_name = elevRepository.get_area_name(lat, lon);            	
                int area_offset = elevRepository.get_offset(lat, lon);
                String deb_str= "e("+coordinates_formatter.format(lat)+","+coordinates_formatter.format(lon)+")="+(int)elevation;
            	int elev_index = Math.min(255, (int) Math.abs(elevation) / 20); 

            	g2.setColor(terrain_color(ref_alt, elevation));
  
            	map_projection.setPoint(lat, lon);
            	int x = map_projection.getX();
            	int y = map_projection.getY();
            	g2.fillRect(x, y, tile_width+1, tile_height+1);

                // g2.setColor(Color.WHITE);
                // g2.drawString(deb_str, x, y);
                // g2.drawString(area_name, x, y+nd_gc.line_height_l);
                // g2.drawString("o="+area_offset, x, y+2*nd_gc.line_height_l);
            }
        }
        
        g2.setTransform(original_at);    

	}
	
	/**
	 * altitude and elevation in feet
	 */
	public Color terrain_color(float ref_altitude, float elevation) {
		// Peak mode
		if (elevation > ref_altitude+2000) {
			return Color.red;
		} else if (elevation > ref_altitude+1000) {
			return Color.yellow;
		} else if (elevation > ref_altitude-500) {
			return Color.yellow.brighter();
		} else if (elevation > ref_altitude-1000) {
			return Color.green.darker();
		} else if (elevation > ref_altitude-2000) {
			return Color.green;
		} else if (elevation <= 0) {
			return Color.blue;
		} else return Color.black;
	}
	
	/**
	 * Result in feet
	 * Predicted altitude after 1 mn
	 */
	public float ref_altitude() {
		float alt = this.aircraft.altitude_ind();
		float vvi = this.aircraft.vvi();
		float predicted_alt = alt + vvi;
		return predicted_alt;
	}


}
