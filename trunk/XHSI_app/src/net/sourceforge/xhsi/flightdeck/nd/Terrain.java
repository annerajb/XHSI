/**
* Terrain.java
* 
* Display Enhanced Ground Proximity Warning System Terrain (EGPWS)
* Airbus FCOM 1.31.45 and 1.34.70
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.ElevationRepository;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.ModelFactory;

public class Terrain extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    
	private static final Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private AffineTransform original_at;
    
    private Paint original_paint;
    
    float map_up;
    float center_lon;
    float center_lat;
    float pixels_per_deg_lon;
    float pixels_per_deg_lat;
    float pixels_per_nm;
    
	float peak_min;
	float peak_max;
	
	boolean terr_img_1_valid;
	boolean terr_img_2_valid;
	int current_image;
      
    private float sweep_angle;
    private float sweep_max = 60.0f;
    private float sweep_min = -60.0f;
	private long  sweep_timestamp;
    
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
    	terr_img_1_valid = false;
    	terr_img_2_valid = false;
    	current_image = 1;
    	sweep_angle = 0.0f;
    	sweep_timestamp = 0;
	}

	public void paint(Graphics2D g2) {
        if ( nd_gc.powered && (!( nd_gc.mode_app || nd_gc.mode_vor )) ) {
        	if ( avionics.efis_shows_terrain() && ( ! nd_gc.map_zoomin ) )
            paintTerrain(g2);   
        	drawInfoBox(g2);
        	// if (preferences.get_terrain_sweep_bar())  drawSweepBars(g2, sweep_angle);
        
        	float sweep_delta_t = System.currentTimeMillis() - sweep_timestamp; 
        	sweep_timestamp = System.currentTimeMillis();
        	sweep_angle += (nd_gc.wxr_sweep_step/sweep_delta_t);
        	if (sweep_angle>=90.0f) {
        		sweep_angle = 0.0f;
        		current_image = (current_image == 1) ? 2 : 1;
        		if (current_image==1) { 
        			terr_img_1_valid = false; 
        		} else { 
        			terr_img_2_valid = false;
        		}        	
        	}
        	if ( (! terr_img_1_valid) && (current_image==1) ) {
        		// logger.info("Building terrain in buffer 1");
        		Graphics2D g_terr = nd_gc.terr_img_1.createGraphics();
        		g_terr.setRenderingHints(nd_gc.rendering_hints);
        		g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        		g_terr.setColor(nd_gc.background_color);
        		drawTerrain(g_terr,nd_gc.max_range);  
        		terr_img_1_valid=true;
        	}
        	if ( (! terr_img_2_valid) && (current_image==2) ) {
        		// logger.info("Building terrain in buffer 2");
        		Graphics2D g_terr = nd_gc.terr_img_2.createGraphics();
        		g_terr.setRenderingHints(nd_gc.rendering_hints);
        		g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        		g_terr.setColor(nd_gc.background_color);
        		drawTerrain(g_terr,nd_gc.max_range);  
        		terr_img_2_valid=true;
        	}
        }
	}
	
	/* 
	 * Terrain auto-display feature 
	 *   A330 FCOM 1.31.45 p 24
	 *   A320 FCOM 1.31.45 p 17
	 * 
	 * When an alert is generated (either caution or warning), and TERR ON ND is
	 * not selected, the terrain is automatically displayed, and the TERR ON ND pushbutton ON
	 * light comes on.
	 * 
	 * TERR : CHANGE MODE
	 * Display in red (or amber), in case of a Terrain Awarness Display (TAD)
	 * warning (or caution) alter, if PLAN is the selected display mode.
	 */
	
	private void drawInfoBox(Graphics2D g2) {

		if (nd_gc.airbus_style) {
			String terr_str = "TERR";
			g2.setColor(nd_gc.color_airbus_selected);
			g2.setFont(nd_gc.font_xl);
			g2.drawString(terr_str, nd_gc.terr_value_x, nd_gc.terr_label_y);
		}
		
		float ref_alt = ref_altitude();
		if (peak_max > ref_alt + 2000 ) { 
			g2.setColor(Color.red);
		} else if (peak_max > ref_alt - 500 ) {
			g2.setColor(Color.yellow);
		} else {
			g2.setColor(Color.green);
		}
		g2.drawRect(nd_gc.terr_box_x, nd_gc.terr_max_box_y, nd_gc.terr_box_width, nd_gc.terr_box_height);
		if (nd_gc.boeing_style) 
			g2.setFont(nd_gc.font_xs); 
		else 
			g2.setFont(nd_gc.font_l);
		String max_str = ""+Math.round(peak_max/100);
		g2.drawString(max_str, nd_gc.terr_value_x, nd_gc.terr_max_value_y);
	
		if (peak_min > ref_alt + 2000 ) { 
			g2.setColor(Color.red);
		} else if (peak_min > ref_alt - 500 ) {
			g2.setColor(Color.yellow);
		} else {
			g2.setColor(Color.green);
		}
		g2.drawRect(nd_gc.terr_box_x, nd_gc.terr_min_box_y, nd_gc.terr_box_width, nd_gc.terr_box_height);
		g2.setFont(nd_gc.font_l);
		String min_str = ""+Math.round(peak_min/100);
		g2.drawString(min_str, nd_gc.terr_value_x, nd_gc.terr_min_value_y);
	}
	
	
	private void paintTerrain(Graphics2D g2) {
		//if (terr_img_1_valid && current_image == 1) {
	       g2.drawImage( nd_gc.terr_img_1, 0, 0, null);
		//}
	}
	
	/**
	 * Draw the terrain buffer image based on reference altitude
	 * Airbus documentation : FCOM 1.31.45 (Indications on ND)
	 * 
	 * @param g2
	 * @param radius_scale
	 */
	private void drawTerrain(Graphics2D g2, float radius_scale) {
		
		peak_min = 8500;
		peak_max = 0;
			
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

        float lat_max = this.center_lat + delta_lat * nd_gc.terr_range_multiply;
        float lat_min = this.center_lat - delta_lat * nd_gc.terr_range_multiply;
        float lon_max = this.center_lon + delta_lon * nd_gc.terr_range_multiply;
        float lon_min = this.center_lon - delta_lon * nd_gc.terr_range_multiply;

        float lat_step = (lat_max - lat_min) / nd_gc.terr_nb_tile_y;
        float lon_step = (lon_max - lon_min) / nd_gc.terr_nb_tile_x;
        
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
        original_paint = g2.getPaint();
        for (float lat=lat_min; lat<= lat_max; lat+=lat_step) {
            for (float lon=lon_min; lon<=lon_max; lon+=lon_step) {
            	float elevation = elevRepository.get_elevation(lat, lon)*3.2808f;
            	peak_min = Math.min(peak_min, elevation);
            	peak_max = Math.max(peak_max, elevation);

            	// g2.setColor(terrain_color(ref_alt, elevation));
            	g2.setPaint(terrain_texture(ref_alt, elevation));
            	map_projection.setPoint(lat, lon);
            	int x = map_projection.getX();
            	int y = map_projection.getY();
            	g2.fillRect(x, y, nd_gc.terr_tile_width, nd_gc.terr_tile_height);
            }
        }
        g2.setPaint(original_paint);
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
	 * altitude and elevation in feet
	 */
	public TexturePaint terrain_texture(float ref_altitude, float elevation) {
		// Peak mode
		if (elevation > ref_altitude+2000) {
			return nd_gc.terrain_tp_red;
		} else if (elevation > ref_altitude+1000) {
			return nd_gc.terrain_tp_yellow;
		} else if (elevation > ref_altitude-500) {
			return nd_gc.terrain_tp_yellow;
		} else if (elevation > ref_altitude-1000) {
			return nd_gc.terrain_tp_dark_green;
		} else if (elevation > ref_altitude-2000) {
			return nd_gc.terrain_tp_green;
		} else if (elevation <= 0) {
			return nd_gc.terrain_tp_blue;
		} else return nd_gc.terrain_tp_black;
	}

	
	/**
	 * Result in feet
	 * Predicted altitude after 1 mn
	 * 
	 * A330 FCOM 1.31.45 p23
	 * The reference altitude is computed based on the current aircraft altitude or,
	 * if descending more than 1000 fp/mn, the altitude expected in 30 seconds. 
	 * 
	 */
	public float ref_altitude() {
		float alt = this.aircraft.altitude_ind();
		float vvi = this.aircraft.vvi();
		return (vvi < -1000 ? alt + vvi/2 : alt);
	}


	public void drawSweepBars(Graphics2D g2, float angle) {
        double rotation_offset = angle;
        
        AffineTransform original_at = g2.getTransform();

        AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
                Math.toRadians(rotation_offset),
                nd_gc.map_center_x,
                nd_gc.map_center_y
        );
        g2.transform(rotate_to_heading);
        
        g2.setColor(nd_gc.markings_color);
        g2.drawLine(
                nd_gc.map_center_x ,
                nd_gc.map_center_y ,
                nd_gc.rose_radius + nd_gc.rose_radius,
                nd_gc.map_center_y
        );
        g2.setTransform(original_at);
	}
}
