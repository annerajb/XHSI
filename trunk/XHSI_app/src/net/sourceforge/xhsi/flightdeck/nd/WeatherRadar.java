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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.WeatherRepository;
import net.sourceforge.xhsi.util.AzimuthalEquidistantProjection;
import net.sourceforge.xhsi.util.Projection;

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
	boolean wxr_on;
	int current_image;
	long sweep_timestamp;
	
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
    
    private Projection map_projection = new AzimuthalEquidistantProjection();
   
    private static DecimalFormat gain_formatter;
    private static DecimalFormat tilt_formatter;
    
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
    	sweep_timestamp = 0;
    	wxr_on=false;
    	
        gain_formatter = new DecimalFormat("-#0.0");
        DecimalFormatSymbols gain_symbols = gain_formatter.getDecimalFormatSymbols();
        gain_symbols.setDecimalSeparator('.');
        gain_formatter.setDecimalFormatSymbols(gain_symbols);

        tilt_formatter = new DecimalFormat("##0.0");
        DecimalFormatSymbols tilt_symbols = tilt_formatter.getDecimalFormatSymbols();
        tilt_symbols.setDecimalSeparator('.');
        tilt_formatter.setPositivePrefix("+");
        tilt_formatter.setDecimalFormatSymbols(tilt_symbols);

	}

	public void paint(Graphics2D g2) {
 	
        if ( nd_gc.powered && 
        		(!( nd_gc.mode_app || nd_gc.mode_vor )) && 
        		avionics.wxr_active() && 
        		(!avionics.efis_shows_terrain()) && 
        		(!nd_gc.display_inhibit()) &&
        		// No weather radar in map_zoomin or plan mode
        		(!nd_gc.map_zoomin) &&
        		(!nd_gc.mode_plan)) {
        	if (!wxr_on) {
        		prepareInfoBox();
        		// initSweep(false);
        		wxr_img_1_valid = false;
        		wxr_img_2_valid = false;
        		wxr_on=true;
        	}
        	paintWeather(g2);   
        	
        	if (preferences.get_nd_wxr_sweep_bar()) drawSweepBars(g2, sweep_angle);

        	long sweep_delta_t = nd_gc.current_time_millis - sweep_timestamp;
        	sweep_timestamp = nd_gc.current_time_millis;
        	sweep_angle += (nd_gc.wxr_sweep_step*sweep_delta_t) * sweep_direction;
        	if (sweep_angle>=sweep_max) initSweep(true);    	            	
        	if (sweep_angle<=sweep_min) initSweep(false);   	

        	if ( (! wxr_img_1_valid) && (current_image==1) ) {
        		Graphics2D g_terr = nd_gc.wxr_img_1.createGraphics();
        		// Clear the buffered Image first
        		g_terr.setComposite(AlphaComposite.Clear);
        		g_terr.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
        		g_terr.setComposite(AlphaComposite.SrcOver);
        		g_terr.setRenderingHints(nd_gc.rendering_hints);
        		g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        		g_terr.setColor(nd_gc.background_color);
        		// g_terr.setClip(nd_gc.wxr_clip);
        		// too slow - better to clip out after drawWeather
        		drawWeather(g_terr,nd_gc.max_range);  
        		wxr_img_1_valid=true;
        		prepareInfoBox();
        	}
        	if ( (! wxr_img_2_valid) && (current_image==2) ) {
        		Graphics2D g_terr = nd_gc.wxr_img_2.createGraphics();
        		// Clear the buffered Image first
        		g_terr.setComposite(AlphaComposite.Clear);
        		g_terr.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
        		g_terr.setComposite(AlphaComposite.SrcOver);
        		g_terr.setRenderingHints(nd_gc.rendering_hints);
        		g_terr.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        		g_terr.setColor(nd_gc.background_color);
        		// g_terr.setClip(nd_gc.wxr_clip);
        		drawWeather(g_terr,nd_gc.max_range);  
        		wxr_img_2_valid=true;
        		prepareInfoBox();
        	}

        } else {
        	if (wxr_on) prepareInfoBox();
        	wxr_on = false;
        }
	}
	
	private void prepareInfoBox() {
		// logger.info("Building weather info buffer");
		Graphics2D g_winfo = nd_gc.wxr_info_img.createGraphics();
		g_winfo.setRenderingHints(nd_gc.rendering_hints);
		g_winfo.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g_winfo.setColor(nd_gc.background_color);
		// g_winfo.setColor(nd_gc.instrument_background_color);
		g_winfo.fillRect(0, 0, nd_gc.wxr_info_width, nd_gc.wxr_info_height);
		drawInfoBox(g_winfo);  
	}
	
	private void paintWeather(Graphics2D g2) {
		Shape original_clip = g2.getClip();
		if (preferences.get_nd_wxr_sweep()) {
			if (wxr_img_1_valid) {
				wxr_clip_1 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.wxr_radius, nd_gc.map_center_y - nd_gc.wxr_radius,
						nd_gc.wxr_radius*2, nd_gc.wxr_radius*2, 90-sweep_min, sweep_min-sweep_angle, Arc2D.PIE));

				g2.setClip(wxr_clip_1);
				g2.drawImage( nd_gc.wxr_img_1, nd_gc.panel_rect.x, nd_gc.panel_rect.y, null);
			}
			if (wxr_img_2_valid) {
				wxr_clip_2 = new Area(new Arc2D.Float(nd_gc.map_center_x - nd_gc.wxr_radius, nd_gc.map_center_y - nd_gc.wxr_radius,
						nd_gc.wxr_radius*2, nd_gc.wxr_radius*2, 90-sweep_max, sweep_max-sweep_angle, Arc2D.PIE));
				g2.setClip(wxr_clip_2);
				g2.drawImage( nd_gc.wxr_img_2, nd_gc.panel_rect.x, nd_gc.panel_rect.y, null);
			}
		} else {
			g2.setClip(nd_gc.wxr_clip);
			if (wxr_img_1_valid && current_image==1) g2.drawImage( nd_gc.wxr_img_1, nd_gc.panel_rect.x, nd_gc.panel_rect.y, null);
			if (wxr_img_2_valid && current_image==2) g2.drawImage( nd_gc.wxr_img_2, nd_gc.panel_rect.x, nd_gc.panel_rect.y, null);
		}
		g2.setClip(original_clip);
	}
	
	private void drawInfoBox(Graphics2D g2) {
		String label_str;
		g2.setFont(nd_gc.wxr_label_font);
        int wxr_mode = this.avionics.wxr_mode();
        boolean on_ground = this.aircraft.on_ground();

		if (nd_gc.airbus_style) {
			if ((wxr_mode>0) && this.avionics.efis_shows_wxr()) {
				if (avionics.wxr_auto_tilt()) {
					g2.setColor(nd_gc.color_airbus_selected);	
					float tilt_value = avionics.wxr_auto_tilt_value(this.aircraft.altitude_ind(),100);				
					String tilt_str = tilt_formatter.format(tilt_value)+"°";
					int tilt_str_width=1+nd_gc.get_text_width(g2, nd_gc.wxr_label_font, tilt_str);
					g2.drawString(tilt_str, nd_gc.wxr_info_width-tilt_str_width, nd_gc.wxr_label2_y);
				} else {
					String tilt_str = "MAN " + tilt_formatter.format(avionics.wxr_tilt())+"°";
					g2.setColor(nd_gc.color_airbus_selected);	
					int tilt_str_width=1+nd_gc.get_text_width(g2, nd_gc.wxr_label_font, tilt_str);
					g2.drawString(tilt_str, nd_gc.wxr_info_width-tilt_str_width, nd_gc.wxr_label2_y);
				}
				if (!avionics.wxr_auto_gain()) {
					String gain_str = "MAN GAIN";
					int gain_str_width=1+nd_gc.get_text_width(g2, nd_gc.wxr_label_font, gain_str);
					g2.setColor(nd_gc.pfd_markings_color);				
					g2.drawString(gain_str,  nd_gc.wxr_info_width-gain_str_width, nd_gc.wxr_label1_y);
				} else {
					g2.setColor(nd_gc.color_airbus_selected);	
					String tilt_str = "TILT";
					int tilt_str_width=1+nd_gc.get_text_width(g2, nd_gc.wxr_label_font, tilt_str);
					g2.drawString(tilt_str, nd_gc.wxr_info_width-tilt_str_width, nd_gc.wxr_label1_y);
				}
			}
		} else {
			/*
			 * Boeing 737
			 */

            if ( (wxr_mode>0) && this.avionics.efis_shows_wxr() && (!this.avionics.efis_shows_terrain()) ) {
                label_str = "WX";
                if (wxr_mode==2) label_str="WX+T";
                if (wxr_mode==3) label_str="MAP";
                
                // g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_terrain_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if (wxr_mode<4 && on_ground) {
                	g2.setColor(nd_gc.caution_color);
                	label_str = "WX STBY";
                } else if ( ! nd_gc.map_zoomin ) {
                	g2.setColor(nd_gc.terrain_label_color);
                } else {
                	g2.setColor(nd_gc.dim_label_color);
                }
                	
                g2.drawString(label_str, 0, nd_gc.wxr_label1_y);
            }
		}
	}
	
	private void drawWeather(Graphics2D g2, float radius_scale) {
		
		// Turbulence zones are displayed in magenta.
		boolean turbulence_mode = this.avionics.wxr_mode()==Avionics.WXR_MODE_TURB;
		
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

        this.map_projection.setScale(nd_gc.pixels_per_nm);
        this.map_projection.setCenter(nd_gc.map_center_x+nd_gc.border_left,nd_gc.map_center_y+nd_gc.border_bottom);
        this.map_projection.setAcf(this.center_lat, this.center_lon);
        
        // determine max and min lat/lon in viewport to only draw those
        // elements that can be displayed
        float delta_lat = radius_scale * CoordinateSystem.deg_lat_per_nm();
        float delta_lon = radius_scale * CoordinateSystem.deg_lon_per_nm(this.center_lat);
        float lat_max = this.center_lat + delta_lat * nd_gc.wxr_range_multiply;
        float lat_min = this.center_lat - delta_lat * nd_gc.wxr_range_multiply;
        float lon_max = this.center_lon + delta_lon * nd_gc.wxr_range_multiply;
        float lon_min = this.center_lon - delta_lon * nd_gc.wxr_range_multiply;
        float lat_step = (lat_max - lat_min) / nd_gc.wxr_nb_tile_y;
        float lon_step = (lon_max - lon_min) / nd_gc.wxr_nb_tile_x;
        
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
		
		float gain = avionics.wxr_auto_gain() ? (avionics.wxr_mode()==2 ? 1.16f : 0.82f) : avionics.wxr_gain()*1.5f;
		boolean fine = preferences.get_nd_wxr_resolution() == 0;
		float storm_level;
        
        for (float lat=lat_min; lat<= lat_max; lat+=lat_step) {
            for (float lon=lon_min; lon<=lon_max; lon+=lon_step) {
            	if (fine)
            		storm_level = Math.min(9.0f, gain * weather_repository.get_interpolated_storm_level(lat, lon));
            	else
            		storm_level = Math.min(9.0f, gain * weather_repository.get_storm_level(lat, lon));
            	g2.setColor(weather_color(storm_level));
  
            	map_projection.setPoint(lat, lon);
            	int x = map_projection.getX();
            	int y = map_projection.getY();
            	if (storm_level>0) g2.fillRect(x, y, nd_gc.wxr_tile_width, nd_gc.wxr_tile_height);
            }
        }
        
        g2.setTransform(original_at);    
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
                nd_gc.map_center_y - nd_gc.wxr_radius
        );
        g2.setTransform(original_at);
	}
	
	private void initSweep(boolean start_from_right) {
		// Radar cannot exceed 140° scan
		float full_range = nd_gc.limit_arcs ? nd_gc.arc_limit_deg : 70.0f;
		if (full_range>70) full_range=70;
    	sweep_direction = start_from_right ? -1.0f : 1.0f;
    	sweep_max = avionics.wxr_narrow() ? full_range/2 : full_range;
    	sweep_min = avionics.wxr_narrow() ? -full_range/2 : -full_range;
    	sweep_angle = start_from_right ? sweep_max : sweep_min;
		current_image = start_from_right ? 2 : 1;
		if (start_from_right) { 
			wxr_img_2_valid = false; 
		} else { 
			wxr_img_1_valid = false;
		}   
	}
	
}
