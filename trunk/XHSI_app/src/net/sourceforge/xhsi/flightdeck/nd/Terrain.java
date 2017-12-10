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
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
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
	float terrain_max;
	float high_band; 
	float middle_band;
	float low_band; 
	boolean peaks_mode_on;
	
    Area terr_clip_1 = null;
    Area terr_clip_2 = null;
    
	boolean terr_img_1_valid;
	boolean terr_img_2_valid;
	int current_image;
      
    private float sweep_angle;
    private float sweep_max = 60.0f;
    private float sweep_min = -60.0f;
	private long  sweep_timestamp;
	
	private float gear_altitude;
    
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
    	initSweep(false);
    	current_image = 1;
    	sweep_angle = 0.0f;
    	sweep_timestamp = 0;
    	peaks_mode_on = false;
	}

	public void paint(Graphics2D g2) {
        if ( nd_gc.powered && (!( nd_gc.mode_app || nd_gc.mode_vor )) && avionics.efis_shows_terrain()) {
        	drawInfoBox(g2);
        	if ( ! nd_gc.map_zoomin ) {
        		paintTerrain(g2);   
        		
        		if (preferences.get_nd_terrain_sweep_bar()) drawSweepBars(g2, sweep_angle);

        		long sweep_delta_t = nd_gc.current_time_millis - sweep_timestamp; 
        		sweep_timestamp = nd_gc.current_time_millis;
        		sweep_angle += nd_gc.terr_sweep_step*sweep_delta_t;
        		if (sweep_angle>=sweep_max)	initSweep(current_image==1);     	

        		if ( (! terr_img_1_valid) && (current_image==1) ) {
        			// logger.info("Building terrain in buffer 1");
        			Graphics2D g_terr = nd_gc.terr_img_1.createGraphics();
        			g_terr.setRenderingHints(nd_gc.rendering_hints);
        			g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        			g_terr.setColor(nd_gc.background_color);
        			g_terr.fillRect(0, 0, nd_gc.panel_rect.width,nd_gc.panel_rect.height);
        			drawTerrain(g_terr,nd_gc.max_range);  
        			terr_img_1_valid=true;
        		}
        		if ( (! terr_img_2_valid) && (current_image==2) ) {
        			// logger.info("Building terrain in buffer 2");
        			Graphics2D g_terr = nd_gc.terr_img_2.createGraphics();
        			g_terr.setRenderingHints(nd_gc.rendering_hints);
        			g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        			g_terr.setColor(nd_gc.background_color);
        			g_terr.fillRect(0, 0, nd_gc.panel_rect.width,nd_gc.panel_rect.height);
        			drawTerrain(g_terr,nd_gc.max_range);  
        			terr_img_2_valid=true;
        		}
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

		// TERR indicator
		g2.setFont(nd_gc.terr_label_font);
		if (nd_gc.airbus_style) {
			String terr_str = "TERR";
			g2.clearRect(nd_gc.terr_value_x- nd_gc.digit_width_s/2, nd_gc.terr_label_rect_y, nd_gc.terr_box_width, nd_gc.terr_min_box_y +nd_gc.terr_box_height);
			g2.setColor(nd_gc.terrain_label_color);
			g2.drawString(terr_str, nd_gc.terr_label_x, nd_gc.terr_label_y);
		}

        
		float ref_alt = ref_altitude();
		
		// Max terrain altitude box (unit = Flight Level)
		if (peak_max > ref_alt + 2000 ) { 
			g2.setColor(Color.red);
		} else if (peak_max > ref_alt - 500 ) {
			g2.setColor(Color.yellow);
		} else {
			g2.setColor(Color.green);
		}
		g2.drawRect(nd_gc.terr_box_x, nd_gc.terr_max_box_y, nd_gc.terr_box_width, nd_gc.terr_box_height);
		String max_str = ""+Math.round(peak_max/100);
		g2.drawString(max_str, nd_gc.terr_value_x, nd_gc.terr_max_value_y);

		// Min terrain altitude box (unit = Flight Level)
		if (peak_min > ref_alt + 2000 ) { 
			g2.setColor(Color.red);
		} else if (peak_min > ref_alt - 500 ) {
			g2.setColor(Color.yellow);
		} else {
			g2.setColor(Color.green);
		}
		g2.drawRect(nd_gc.terr_box_x, nd_gc.terr_min_box_y, nd_gc.terr_box_width, nd_gc.terr_box_height);
		String min_str = ""+Math.round(peak_min/100);
		g2.drawString(min_str, nd_gc.terr_value_x, nd_gc.terr_min_value_y);
	}
	
	
	private void paintTerrain(Graphics2D g2) {
		Shape original_clip = g2.getClip();
		if (preferences.get_nd_terrain_sweep()) {
			if (terr_img_1_valid) {
				if (current_image==1) {
					terr_clip_1 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
						nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90-sweep_angle, sweep_angle*2, Arc2D.PIE));
				} else {
					terr_clip_1 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
						nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90-sweep_max, sweep_max-sweep_angle, Arc2D.PIE));
					
					terr_clip_1.add(new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
							nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90+sweep_max, sweep_angle-sweep_max, Arc2D.PIE)));
				}
				g2.setClip(terr_clip_1);
				g2.drawImage( nd_gc.terr_img_1, 0, 0, null);
			}
			
			if (terr_img_2_valid) {
				if (current_image==2) {
					terr_clip_2 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
						nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90-sweep_angle, sweep_angle*2, Arc2D.PIE));
				} else {
					terr_clip_2 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
						nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90-sweep_max, sweep_max-sweep_angle, Arc2D.PIE));
					
					terr_clip_2.add(new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
							nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90+sweep_max, sweep_angle-sweep_max, Arc2D.PIE)));
				}		
				g2.setClip(terr_clip_2);
				g2.drawImage( nd_gc.terr_img_2, 0, 0, null);
			}
			
		} else {
			g2.setClip(nd_gc.terr_clip);
			if (terr_img_1_valid && current_image==1) g2.drawImage( nd_gc.terr_img_1, 0, 0, null);
			if (terr_img_2_valid && current_image==2) g2.drawImage( nd_gc.terr_img_2, 0, 0, null);
		}
		g2.setClip(original_clip);
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

        // Reference altitude differs on gear position
        // value in feet
        gear_altitude = this.aircraft.gear_is_down() ? 250 : 500;
        
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
        
        if (peak_min<0) peak_min=0; 

        /*
         * Peaks mode - value in feet
         */
        if (nd_gc.terr_peaks_mode) {
        	peaks_mode_on = (peak_max < (ref_alt -500));
        	terrain_max = peak_max;
        	high_band = terrain_max - 500;
        	middle_band = peak_min + (peak_max-peak_min)/2; 
        	low_band = peak_min + (peak_max-peak_min)/4;
        } else peaks_mode_on = false;
	}
	
	/**
	 * altitude and elevation in feet
	 */
	public Color terrain_color(float ref_altitude, float elevation) {
		// Peak mode off - Solid style
		if (elevation > ref_altitude+2000) {
			return nd_gc.terrain_red_color;
		} else if (elevation > ref_altitude+1000) {
			return nd_gc.terrain_bright_yellow_color;
		} else if (elevation > ref_altitude-500) {
			return nd_gc.terrain_yellow_color;
		} else if (elevation > ref_altitude-1000) {
			return nd_gc.terrain_green_color;
		} else if (elevation > ref_altitude-2000) {
			return nd_gc.terrain_dark_green_color;
		} else if (elevation <= 0) {
			return nd_gc.terrain_blue_color;
		} else return nd_gc.terrain_black_color;
	}

	/**
	 * altitude and elevation in feet
	 */
	public TexturePaint terrain_texture(float ref_altitude, float elevation) {
		if (peaks_mode_on) {
			// Peak mode on - Textured style
			if (elevation > ref_altitude+2000) {
				return nd_gc.terrain_tp_hd_red;
			} else if (elevation > ref_altitude+1000) {
				return nd_gc.terrain_tp_hd_yellow;
			} else if (elevation > ref_altitude-gear_altitude) {
				return nd_gc.terrain_tp_md_yellow;
			} else if (elevation > ref_altitude-1000) {
				return nd_gc.terrain_tp_hd_green;
			} else if (elevation > ref_altitude-2000) {
				return nd_gc.terrain_tp_ld_green;
			} else if (elevation <= 0) {
				return nd_gc.terrain_tp_blue;
			} else return nd_gc.terrain_tp_black;			
		} else {
			// Peak mode off - Textured style
			if (elevation > ref_altitude+2000) {
				return nd_gc.terrain_tp_hd_red;
			} else if (elevation > ref_altitude+1000) {
				return nd_gc.terrain_tp_hd_yellow;
			} else if (elevation > ref_altitude-gear_altitude) {
				return nd_gc.terrain_tp_md_yellow;
			} else if (elevation > ref_altitude-1000) {
				return nd_gc.terrain_tp_hd_green;
			} else if (elevation > ref_altitude-2000) {
				return nd_gc.terrain_tp_ld_green;
			} else if (elevation <= 0) {
				return nd_gc.terrain_tp_blue;
			} else return nd_gc.terrain_tp_black;
		}
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
        
        g2.setColor(nd_gc.instrument_background_color);

        g2.drawLine(
                nd_gc.map_center_x ,
                nd_gc.map_center_y ,
                nd_gc.map_center_x ,
                nd_gc.map_center_y - nd_gc.rose_radius
        );
        g2.setTransform(original_at);
        
        rotate_to_heading = AffineTransform.getRotateInstance(
                Math.toRadians(-rotation_offset),
                nd_gc.map_center_x,
                nd_gc.map_center_y
        );
        g2.transform(rotate_to_heading);
        g2.drawLine(
                nd_gc.map_center_x ,
                nd_gc.map_center_y ,
                nd_gc.map_center_x ,
                nd_gc.map_center_y - nd_gc.rose_radius
        );
      

        g2.setTransform(original_at);
	}
	
	private void initSweep(boolean current_is_one) {   	
    	sweep_max = 75.0f;
    	sweep_min = 0.0f;
    	sweep_angle = sweep_min;
		current_image = current_is_one ? 2 : 1;
		if (current_is_one) { 
			terr_img_2_valid = false; 
		} else { 
			terr_img_1_valid = false;
		}   
	}
}
