/**
* MovingMap.java
* 
* Renders all elements of the moving map display: fixes, VORs, NDBs, 
* Airports, Localizers and the programmed FMS route if any. This component
* also renders the range marker rings.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import de.georg_gruetter.xhsi.model.Airport;
import de.georg_gruetter.xhsi.model.CoordinateSystem;
import de.georg_gruetter.xhsi.model.FMSEntry;
import de.georg_gruetter.xhsi.model.Fix;
import de.georg_gruetter.xhsi.model.Localizer;
import de.georg_gruetter.xhsi.model.ModelFactory;
import de.georg_gruetter.xhsi.model.NavigationObject;
import de.georg_gruetter.xhsi.model.NavigationObjectRepository;
import de.georg_gruetter.xhsi.model.VOR;

public class MovingMap extends HSISubcomponent {
	
	private static final boolean DRAW_LAT_LON_GRID = false;

	NavigationObjectRepository nor;
	float pixels_per_deg_lon_at_current_position;
	float pixels_per_deg_lat_at_current_position;
    int pixels_per_km;
	float dash[] = { 10.0f };
	Area panel = null;

	public MovingMap(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
		this.nor = NavigationObjectRepository.get_instance();
	}

	public void paint(Graphics2D g2) {
		drawMap(g2, this.avionics.map_range());
		
		// draw range circles
		draw_scale_rings(g2);
		
		// blank out outer rose area
		panel = new Area(new Rectangle2D.Float(0,0, hsi_gc.panel_size.width, hsi_gc.panel_size.height));
		panel.subtract(hsi_gc.inner_rose_area);
		g2.setColor(Color.BLACK);
		g2.fill(panel);
	}
	
	/*
	 * map_scale	: the number of kilometers spanned by the diameter of the rose
	 */
	private void drawMap(Graphics2D g2, int map_scale) {

		float lat_aircraft = this.aircraft.lat();
		float lon_aircraft = this.aircraft.lon();
		this.pixels_per_km = (int) (hsi_gc.rose_radius / map_scale);
					
		// pixels per degree
		this.pixels_per_deg_lon_at_current_position = (hsi_gc.rose_radius / 2.0f) / ((float)map_scale * CoordinateSystem.deg_lon_per_km(lat_aircraft));
		this.pixels_per_deg_lat_at_current_position = (hsi_gc.rose_radius / 2.0f) / ((float)map_scale * CoordinateSystem.deg_lat_per_km());
		
		// determine max and min lat/lon in viewport to only draw those
		// elements that can be displayed
		float delta_lat = (hsi_gc.rose_radius / this.pixels_per_deg_lat_at_current_position);
		float delta_lon = (hsi_gc.rose_radius / this.pixels_per_deg_lon_at_current_position);
		
		float lat_max = lat_aircraft + delta_lat;
		float lat_min = lat_aircraft - delta_lat;
		float lon_max = lon_aircraft + delta_lon;
		float lon_min = lon_aircraft - delta_lon;

		// rotate to aircraft heading
		AffineTransform original_at = g2.getTransform();
		AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
				Math.toRadians(-1.0f * (this.aircraft.horizontal_path() - this.aircraft.magnetic_variation())), 
				hsi_gc.plane_position_x, 
				hsi_gc.plane_position_y);
	    g2.transform(rotate_to_heading);
		//g2.rotate(Math.toRadians(-1.0f * this.aircraft.horizontal_path()), hsi_gc.plane_position_x, hsi_gc.plane_position_y);
		if (DRAW_LAT_LON_GRID) {
			g2.setColor(Color.GRAY);
			for (float i=(float)Math.round(lon_aircraft)- 2.0f;i<= (float)Math.round(lon_aircraft) + 2.0f;i+=0.1f) {
				g2.drawLine(
				    lon_to_x(i), -300,
					lon_to_x(i), hsi_gc.panel_size.height+300);		
				g2.drawString("" + Math.round(i*10)/10.0f + "¡", lon_to_x(i) + 2, (int)(hsi_gc.panel_size.height *0.7));
			}
			for (float i=(float)Math.round(lat_aircraft)- 2.0f;i<= (float)Math.round(lat_aircraft) + 2.0f;i+=0.1f) {
				g2.drawLine(
				    -300, lat_to_y(i),
					hsi_gc.panel_size.width+300, lat_to_y(i));		
				g2.drawString("" + Math.round(i*10)/10.0f + "¡", (int)(hsi_gc.panel_size.height*0.7), lat_to_y(i) -2);
			}	
		}		
		g2.setFont(hsi_gc.font_small);
		
		for (int lat=(int)lat_min;lat<=(int) lat_max;lat++) {
			for (int lon=(int)lon_min;lon<=(int)lon_max;lon++) {
				
				if (avionics.efis_shows_waypoints() && (avionics.map_range() <= 40)) {
					draw_nav_objects(
							g2, 
							NavigationObject.NO_TYPE_FIX, 
							nor.get_nav_objects(NavigationObject.NO_TYPE_FIX,lat,lon));					
				}

				if (avionics.efis_shows_ndbs() && (avionics.map_range() <= 80)) {
					draw_nav_objects(
							g2, 
							NavigationObject.NO_TYPE_NDB, 
							nor.get_nav_objects(NavigationObject.NO_TYPE_NDB,lat,lon));
				}
				
				if (avionics.efis_shows_vors() && (avionics.map_range() <= 80)) {
					draw_nav_objects(
							g2, 
							NavigationObject.NO_TYPE_VOR, 
							nor.get_nav_objects(NavigationObject.NO_TYPE_VOR,lat,lon));					
				}
				
				if (avionics.efis_shows_airports() && (avionics.map_range() <= 80)) {
					draw_nav_objects(
							g2, 
							NavigationObject.NO_TYPE_AIRPORT, 
							nor.get_nav_objects(NavigationObject.NO_TYPE_AIRPORT,lat,lon));					
				}
			}
		}
		
		// draw FMS route
		if (this.fms.is_active()) {			
			int nb_of_entries = this.fms.get_nb_of_entries();
			FMSEntry entry;
			FMSEntry next_entry = null;
			for (int i=0;i<nb_of_entries;i++) {
				entry = (FMSEntry) this.fms.get_entry(i);
				if (i<(nb_of_entries-1)) {
					next_entry = (FMSEntry) this.fms.get_entry(i+1);
				} else {
					next_entry = null;
				}
				draw_FMS_entry(
						g2, 
						entry,
						next_entry);
			}
		}
		
		g2.setTransform(original_at);		
	}
	
	private void draw_scale_rings(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		for (int radius=(hsi_gc.rose_radius/4);radius<hsi_gc.rose_radius;radius+=(hsi_gc.rose_radius/4)) {
			g2.draw(new Arc2D.Float(
				hsi_gc.plane_position_x - radius,
				hsi_gc.plane_position_y - radius,
				radius*2,
				radius*2,
				0,
				180.0f,
				Arc2D.OPEN));
		}
	}
		
	private void draw_nav_objects(Graphics2D g2, int type, ArrayList nav_objects) {
		NavigationObject no;
		Localizer localizer = null;
		String localized_airport = null;
		no = this.avionics.get_selected_localizer();
		if (no != null) {
			localized_airport = ((Localizer) no).airport;
			localizer = (Localizer) no;
		}

		for (int i=0;i<nav_objects.size();i++) {
			no = (NavigationObject) nav_objects.get(i);
			int x = lon_to_x(no.lon);
			int y = lat_to_y(no.lat);
			double dist = 
				Math.sqrt(
					Math.pow(x-hsi_gc.plane_position_x,2) + 
					Math.pow(y-hsi_gc.plane_position_y,2));		
			if ((dist < (hsi_gc.rose_radius - 35)) || (localized_airport != null)) {
				if (type == NavigationObject.NO_TYPE_NDB)
					drawNDB(g2, x, y, (VOR) no);
				else if (type == NavigationObject.NO_TYPE_VOR) 
					drawVOR(g2, x, y, (VOR) no);
				else if (type == NavigationObject.NO_TYPE_FIX)
					drawFix(g2, x, y, (Fix) no);
				else if (type == NavigationObject.NO_TYPE_AIRPORT) {
					if ((localized_airport != null) && (((Airport)no).icao_code.equals(localized_airport))) {
						drawLocalizer(g2, lon_to_x(localizer.lon), lat_to_y(localizer.lat), localizer);						
					} else {
						drawAirport(g2, x, y, (Airport) no);
					}
				}
			}
		}
	}
	
	private int lon_to_x(float lon) {
		return Math.round(hsi_gc.plane_position_x + ((lon - this.aircraft.lon())*pixels_per_deg_lon_at_current_position));
	}
	
	private int lat_to_y(float lat) {
		return Math.round(hsi_gc.plane_position_y + ((this.aircraft.lat() - lat)*pixels_per_deg_lat_at_current_position));
	}
		
	
	private void drawVOR(Graphics2D g2, int x, int y, VOR vor) {
		int x_points_hexagon[] = { x-3, x+3, x+6, x+3, x-3, x-6 };
		int y_points_hexagon[] = { y-5, y-5, y, y+5, y+5, y };
		
		int x_points_ul_leaf[] = { x-6, x-3, x-8, x-11 };
		int y_points_ul_leaf[] = { y,   y-5, y-8, y-3 };
		
		int x_points_ur_leaf[] = { x+6, x+3, x+8, x+11 };
		int y_points_ur_leaf[] = { y,   y-5, y-8, y-3 };

		int x_points_l_leaf[] =  { x-3, x+3, x+3, x-3 };
		int y_points_l_leaf[] =  { y+5, y+5, y+11, y+11 };
		
		AffineTransform original_at = g2.getTransform();
		g2.rotate(Math.toRadians(this.aircraft.horizontal_path() - this.aircraft.magnetic_variation()), x,y);		
		Graphics g = (Graphics) g2;
		g.setColor(hsi_gc.color_lightgreen);
		g.drawPolygon(x_points_hexagon, y_points_hexagon, 6);
		g.drawPolygon(x_points_ul_leaf, y_points_ul_leaf, 4);
		g.drawPolygon(x_points_ur_leaf, y_points_ur_leaf, 4);
		g.drawPolygon(x_points_l_leaf, y_points_l_leaf, 4);
		g.drawString(vor.ilt, x + 10, y + 13);
		g2.setTransform(original_at);
	}

	private void drawNDB(Graphics2D g2, int x, int y, VOR vor) {		
		int x_points_hexagon[] = { x-5, x+5, x+10, x+5, x-5, x-10 };
		int y_points_hexagon[] = { y-8, y-8, y, y+8, y+8, y };
		
		AffineTransform original_at = g2.getTransform();
		g2.rotate(Math.toRadians(this.aircraft.horizontal_path()  - this.aircraft.magnetic_variation()), x,y);		
		Graphics g = (Graphics) g2;
		g.setColor(hsi_gc.color_lightblue);
		g.drawPolygon(x_points_hexagon, y_points_hexagon, 6);
		g.drawString(vor.ilt, x + 10, y + 13);
		g2.setTransform(original_at);
	}
	
	private void drawFix(Graphics2D g2, int x, int y, Fix fix) {
		int x_points_triangle[] = { x-5, x+5, x };
		int y_points_triangle[] = { y+5, y+5, y-5  };
		
		AffineTransform original_at = g2.getTransform();
		g2.rotate(Math.toRadians(this.aircraft.horizontal_path() - this.aircraft.magnetic_variation()), x,y);		
		Graphics g = (Graphics) g2;
		g.setColor(Color.WHITE);
		g.drawPolygon(x_points_triangle, y_points_triangle, 3);
		g.drawString(fix.name, x + 10, y + 13);
		g2.setTransform(original_at);
	}
	
	private void drawLocalizer(Graphics2D g2, int x, int y, Localizer localizer) {
		int localizer_extension = (int) (13*this.pixels_per_km);
		
		AffineTransform original_at = g2.getTransform();
		g2.rotate(Math.toRadians(this.aircraft.horizontal_path() - this.aircraft.magnetic_variation()), x,y);		
		g2.setColor(hsi_gc.color_lightblue);
		g2.drawString(localizer.airport, x - (hsi_gc.get_text_width(g2, hsi_gc.font_small,localizer.airport) / 2), y);
		g2.setColor(Color.WHITE);
		g2.drawString(localizer.rwy, x - (hsi_gc.get_text_width(g2, hsi_gc.font_small,localizer.rwy) / 2), y + hsi_gc.line_height_small);
		g2.rotate(Math.toRadians(localizer.direction - this.aircraft.horizontal_path() + this.aircraft.magnetic_variation()),x,y);
		g2.drawLine(x-(localizer_extension/6),y-(localizer_extension/3),x-(localizer_extension/6),y+(localizer_extension/3));
		g2.drawLine(x+(localizer_extension/6),y-(localizer_extension/3),x+(localizer_extension/6),y+(localizer_extension/3));
		
		Stroke original_stroke = g2.getStroke();
	    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		g2.drawLine(x,y+(localizer_extension/3),x,y+localizer_extension);
		g2.drawLine(x,y-(localizer_extension/3),x,y-localizer_extension);
		g2.setStroke(original_stroke);
		g2.setTransform(original_at);			

	}
	
	private void drawAirport(Graphics2D g2, int x, int y, Airport airport) {
		AffineTransform original_at = g2.getTransform();
		g2.rotate(Math.toRadians(this.aircraft.horizontal_path()  - this.aircraft.magnetic_variation()), x,y);		
		Graphics g = (Graphics) g2;
		g.setColor(hsi_gc.color_lightblue);
		g.drawOval(x-8,y-8,16,16);
		g.drawString(airport.icao_code, x + 10, y + 13);
		g2.setTransform(original_at);		
	}
	
	private void draw_FMS_entry(Graphics2D g2, FMSEntry entry, FMSEntry next_entry) {
		int x = lon_to_x(entry.lon);
		int y = lat_to_y(entry.lat);
		
		if ((next_entry != null) && (next_entry.name.equals("NTFND") == false)) {
			g2.setColor(hsi_gc.color_magenta);
			g2.drawLine(x,y, lon_to_x(next_entry.lon), lat_to_y(next_entry.lat));
		}
		
		double dist = 
			Math.sqrt(
				Math.pow(x-hsi_gc.plane_position_x,2) + 
				Math.pow(y-hsi_gc.plane_position_y,2));		
		if (dist < (hsi_gc.rose_radius - 35)) {

			int x_points_star[] = { x-2, x, x+2, x+8, x+2, x, x-2,x-8,x-2 };
			int y_points_star[] = { y-1, y-8, y-2, y, y+2, y+8, y+2,y,y-2 };
	
			AffineTransform original_at = g2.getTransform();
			g2.rotate(Math.toRadians(this.aircraft.horizontal_path()  - this.aircraft.magnetic_variation()), x,y);		
			
			Graphics g = (Graphics) g2;
			if (entry.active) {
				g.setColor(hsi_gc.color_magenta);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawPolygon(x_points_star, y_points_star, 9);
			g.drawString(entry.name, x + 10, y + 13);
			g.drawString("" + (int) entry.altitude, x + 10, y + 25);		
			g2.setTransform(original_at);				
		}
	}
}

