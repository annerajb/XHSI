/**
* WeatherRadar.java
* 
* Display the weather radar
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.WeatherRepository;

public class WeatherRadar extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    
	private static final Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private AffineTransform original_at;
    
    DecimalFormat coordinates_formatter;
    
    Area wxr_clip_1 = null;
    Area wxr_clip_2 = null;
    
    float map_up;
    float center_lon;
    float center_lat;
    float pixels_per_deg_lon;
    float pixels_per_deg_lat;
    float pixels_per_nm;
    
	boolean wxr_img_1_valid;
	boolean wxr_img_2_valid;
	int current_image;
	
	float peak_min;
	float peak_max;     
   
	Color colorAreaNotFound = new Color(192,64,64,64);
	Color colorSliceNotLoaded = new Color(128,128,64,64);
	Color colorBackGround = new Color(0,64,64,64);
	
    WeatherRepository weather_repository;
    
    private float sweep_angle = 0.0f;
    private float sweep_direction = 1.0f;
    private float sweep_max = 60.0f;
    private float sweep_min = -60.0f;

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
   
   
    
	public WeatherRadar(ModelFactory model_factory, NDGraphicsConfig nd_gc,
			Component parent_component) {
		super(model_factory, nd_gc, parent_component);
        this.weather_repository = WeatherRepository.get_instance();
        this.coordinates_formatter = new DecimalFormat("00.0");
        DecimalFormatSymbols symbols = coordinates_formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        coordinates_formatter.setDecimalFormatSymbols(symbols);
        initSweep(false);
    	wxr_img_1_valid = false;
    	wxr_img_2_valid = false;
    	current_image = 1;
	}

	public void paint(Graphics2D g2) {
 
		if (nd_gc.display_mode_change_msg() || nd_gc.display_range_change_msg()) {
	        initSweep(false);
			wxr_img_1_valid = false;
			wxr_img_2_valid = false;
		}
		
        if ( nd_gc.powered && (!( nd_gc.mode_app || nd_gc.mode_vor )) ) {
        	if ( avionics.efis_shows_wxr() && (!avionics.efis_shows_terrain()) && ( ! nd_gc.map_zoomin ) ) {
        		paintWeather(g2);   
        		drawInfoBox(g2);
            	drawSweepBars(g2, sweep_angle);

            	// TODO: Adjust angle based on system.time
            	sweep_angle += 0.7f * sweep_direction;
            	if (sweep_angle>=sweep_max) initSweep(true);    	            	
            	if (sweep_angle<=sweep_min) initSweep(false);   	
            	
            	if ( (! wxr_img_1_valid) && (current_image==1) ) {
            		// logger.info("Building weather radar in buffer 1");
            		Graphics2D g_terr = nd_gc.wxr_img_1.createGraphics();
        			// Clear the buffered Image first
            		g_terr.setComposite(AlphaComposite.Clear);
            		g_terr.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
            		g_terr.setComposite(AlphaComposite.SrcOver);
            		g_terr.setRenderingHints(nd_gc.rendering_hints);
            		g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            		g_terr.setColor(nd_gc.background_color);
            		drawWeather(g_terr,nd_gc.max_range);  
            		wxr_img_1_valid=true;
            	}
            	if ( (! wxr_img_2_valid) && (current_image==2) ) {
            		// logger.info("Building weather radar in buffer 2");
            		Graphics2D g_terr = nd_gc.wxr_img_2.createGraphics();
        			// Clear the buffered Image first
            		g_terr.setComposite(AlphaComposite.Clear);
            		g_terr.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
            		g_terr.setComposite(AlphaComposite.SrcOver);
            		g_terr.setRenderingHints(nd_gc.rendering_hints);
            		g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            		g_terr.setColor(nd_gc.background_color);
            		drawWeather(g_terr,nd_gc.max_range);  
            		wxr_img_2_valid=true;
            	}
        	}
        }
	}
	
	private void paintWeather(Graphics2D g2) {
		if (wxr_img_1_valid) {
		   wxr_clip_1 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
					nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90-sweep_min, sweep_min-sweep_angle, Arc2D.PIE));
		   Shape original_clip = g2.getClip();
		   g2.setClip(wxr_clip_1);
	       g2.drawImage( nd_gc.wxr_img_1, 0, 0, null);
	       g2.setClip(original_clip);
		}
		if (wxr_img_2_valid) {
			wxr_clip_2 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y - nd_gc.rose_radius,
					nd_gc.rose_radius*2, nd_gc.rose_radius*2, 90-sweep_max, sweep_max-sweep_angle, Arc2D.PIE));
			Shape original_clip = g2.getClip();
			g2.setClip(wxr_clip_2);
			g2.drawImage( nd_gc.wxr_img_2, 0, 0, null);
			g2.setClip(original_clip);
		}

	}
	
	private void drawInfoBox(Graphics2D g2) {

		if (nd_gc.airbus_style) {
			String tilt_str = "TILT";
			g2.setColor(nd_gc.color_airbus_selected);
			g2.setFont(nd_gc.font_xl);
			g2.drawString(tilt_str, nd_gc.terr_value_x, nd_gc.terr_label_y);
		}
	}
	
	private void drawWeather(Graphics2D g2, float radius_scale) {
		
		
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
        
		g2.setFont(nd_gc.font_xxxs);
		
		float gain = avionics.wxr_auto_gain() ? 0.7f : avionics.wxr_gain()*1.5f;
        
        for (float lat=lat_min; lat<= lat_max; lat+=lat_step) {
            for (float lon=lon_min; lon<=lon_max; lon+=lon_step) {
            	float storm_level = Math.min(9.0f, gain * weather_repository.get_storm_level(lat, lon));
                //String area_name = weather_repository.get_area_name(lat, lon);            	
                //int lat_offset = weather_repository.get_lat_offset(lat, lon);
                //int lon_offset = weather_repository.get_lon_offset(lat, lon);
                // String deb_str= coordinates_formatter.format(lat)+","+coordinates_formatter.format(lon)+"="+(int)storm_level;
                // String deb_str= ""+(int)storm_level;
            	g2.setColor(weather_color(storm_level));
  
            	map_projection.setPoint(lat, lon);
            	int x = map_projection.getX();
            	int y = map_projection.getY();
            	if (storm_level>0) g2.fillRect(x, y, tile_width+1, tile_height+1);
                // g2.setColor(Color.WHITE);
                // g2.drawString(deb_str, x, y+nd_gc.line_height_xxs);
                // g2.drawString(area_name, x, y+2*nd_gc.line_height_xxs);
                // g2.drawString("o="+lat_offset+","+lon_offset , x, y+3*nd_gc.line_height_xxs);
            }
        }
        
        g2.setTransform(original_at);    
       		
		// WEATHER indicator (debug)
		g2.setColor(nd_gc.terrain_label_color);
		g2.setFont(nd_gc.font_l);
		g2.drawString("WEATHER ON", nd_gc.map_center_x, nd_gc.frame_size.height*9/10);

	}
	
	/**
	 * storm level from 0 to 9
	 */
	public Color weather_color(float storm_level) {
		/*
		 * Error codes :
		 * -1 : slice not loaded
		 * -2 : area not found
		 * -3 :
		 */
		if (storm_level > 0 && storm_level < 10.0) {
			return nd_gc.wxr_colors[(int)storm_level];
		} else if (storm_level == -1) {
			return colorSliceNotLoaded;
		} else if (storm_level == -2) {
			return colorAreaNotFound;
		} else 
			return colorBackGround;
	}
	
	private void drawSweepBars(Graphics2D g2, float angle) {
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
	}
	
	private void initSweep(boolean start_from_right) {   	
    	sweep_direction = start_from_right ? -1.0f : 1.0f;
    	sweep_max = avionics.wxr_narrow() ? 30.0f : 60.0f;
    	sweep_min = avionics.wxr_narrow() ? -30.0f : -60.0f;
    	sweep_angle = start_from_right ? sweep_max : sweep_min;
		current_image = start_from_right ? 2 : 1;
		if (start_from_right) { 
			wxr_img_2_valid = false; 
		} else { 
			wxr_img_1_valid = false;
		}   
	}
	
}
