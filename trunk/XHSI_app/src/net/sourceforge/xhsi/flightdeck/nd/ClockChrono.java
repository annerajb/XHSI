/**
* ClockChrono.java
* 
* Displays the Clock or Chronograph at the bottom
* 
* Copyright (C) 2015  Marc Rogiers (marrog.123@gmail.com)
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

//import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

//import net.sourceforge.xhsi.model.Avionics;
//import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.ModelFactory;



public class ClockChrono extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    
    public static boolean USE_BUFFERED_IMAGE = true;
    private Graphics2D g2_clock;
    private Graphics2D g2_chrono;

    private DecimalFormat hms_formatter;
    
    private float timer_elapsed_time;
    private float chrono_elapsed_time;
    private boolean clock_utc;
    private int clock_time;
    private long refreshed_timestamp;

    public ClockChrono(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        hms_formatter = new DecimalFormat("00");
        refreshed_timestamp=0;
        chrono_elapsed_time=0;
        timer_elapsed_time=0;
        clock_utc=true;
        clock_time=0;
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {
        	boolean refresh_all = (nd_gc.colors_updated | (nd_gc.reconfigured_timestamp > this.refreshed_timestamp));
        	this.refreshed_timestamp =  nd_gc.current_time_millis;
        	
        	// Navigation Display Chrono
        	float chrono_time = this.avionics.efis_chrono_elapsed_time();
        	boolean refresh_chrono = (refresh_all || (Math.abs((int)chrono_elapsed_time - (int)chrono_time) > 0 )) && (chrono_time>0.0f);

        	
        	// Main clock & chrono
        	float timer = this.aircraft.timer_elapsed_time();
        	boolean utc = this.avionics.clock_shows_utc();
        	int current_time = utc ? (int)this.aircraft.sim_time_zulu() : (int)this.aircraft.sim_time_local();
        	boolean refresh_clock = (refresh_all || (Math.abs((int)timer_elapsed_time - (int)timer) > 0 ) || (Math.abs(clock_time-current_time)>59) || (clock_utc!=utc));
        			

        	
        	if (USE_BUFFERED_IMAGE) {
        		if (this.preferences.get_nd_show_clock()) {
        			if (refresh_clock) {
        	        	timer_elapsed_time = timer;
        	        	clock_time = current_time;
        	        	clock_utc = utc;
        	        	
        				g2_clock = nd_gc.clock_img.createGraphics();
        				g2_clock.setRenderingHints(nd_gc.rendering_hints);
        				g2_clock.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        				// Clear the buffered Image first
        				g2_clock.setComposite(AlphaComposite.Clear);
        				g2_clock.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
        				g2_clock.setComposite(AlphaComposite.SrcOver);


        				draw_clock(g2_clock, 0, 0, timer_elapsed_time, clock_utc, clock_time);
        			}
        			g2.drawImage(nd_gc.clock_img, nd_gc.clock_box_x, nd_gc.clock_box_y, null);
        		}
        		if ((nd_gc.boeing_style && this.preferences.get_nd_show_clock()) || nd_gc.airbus_style) {
        			if (refresh_chrono) {
        	        	chrono_elapsed_time = chrono_time;
        	        	
        				g2_chrono = nd_gc.chrono_img.createGraphics();
        				g2_chrono.setRenderingHints(nd_gc.rendering_hints);
        				g2_chrono.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        				// Clear the buffered Image first
        				g2_chrono.setComposite(AlphaComposite.Clear);
        				g2_chrono.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
        				g2_chrono.setComposite(AlphaComposite.SrcOver);

        				draw_chrono(g2_chrono, 0, 0, chrono_elapsed_time);
        			}
        			if (chrono_time>0.0f) g2.drawImage(nd_gc.chrono_img, nd_gc.chrono_box_x, nd_gc.chrono_box_y, null); 
        		}
        		
        	} else {        	
	        	timer_elapsed_time = Math.round(timer);
	        	clock_time = current_time;
	        	clock_utc = utc;
	        	chrono_elapsed_time = Math.round(chrono_time);
        		if (this.preferences.get_nd_show_clock()) 
        			draw_clock(g2,nd_gc.clock_box_x, nd_gc.clock_box_y, timer_elapsed_time, clock_utc, clock_time);
        		if ((chrono_time>0.0f) && ((nd_gc.boeing_style && this.preferences.get_nd_show_clock()) || nd_gc.airbus_style)) 
        			draw_chrono(g2, nd_gc.chrono_box_x, nd_gc.chrono_box_y, chrono_elapsed_time);
        	}
        }
        
    }

    void draw_clock(Graphics2D g2, int x, int y, float elapsed_time, boolean utc, int current_time) {
        String time_label;
        String time_str;
        // float chr_time = this.aircraft.timer_elapsed_time();

        if ( elapsed_time == 0.0f ) {
            time_label = utc ? "UTC" : "LT";
            // int current_time = this.avionics.clock_shows_utc() ? (int)this.aircraft.sim_time_zulu() : (int)this.aircraft.sim_time_local();
            int hh = current_time / 3600;
            int mm = ( current_time / 60 ) % 60;
            time_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm);
        } else {
            time_label = "CHR";
            int timer = (int)elapsed_time;
            int mins = timer / 60 % 60;
            int secs = timer % 60;
            time_str = hms_formatter.format(mins) + ":" + hms_formatter.format(secs);
        }

        g2.setColor(nd_gc.chrono_background_color);
        g2.setFont(nd_gc.clock_font);
        
        /*
         * clearRect is time consuming
         */
        // g2.clearRect(nd_gc.clock_box_x, nd_gc.clock_box_y, nd_gc.clock_box_w, nd_gc.clock_box_h);
        g2.fillRect(x, y, nd_gc.clock_box_w, nd_gc.clock_box_h);
        g2.setColor(nd_gc.chrono_color);
        g2.drawString(time_label, x + nd_gc.clock_time_x - nd_gc.get_text_width(g2, nd_gc.clock_font, time_label) - nd_gc.digit_width_s, y + nd_gc.clock_time_y);
        g2.drawString(time_str, x + nd_gc.clock_time_x, y + nd_gc.clock_time_y);

    }
    
    void draw_chrono(Graphics2D g2, int x, int y, float elapsed_time) {
        String time_label = "CHR";
        String time_str;
        int timer = (int)elapsed_time;
        int hours = timer / 3600 % 60;
        int mins = timer / 60 % 60;
        int secs = timer % 60;
        
        if (nd_gc.boeing_style) {
        	if (hours>0) 
        		time_str = hms_formatter.format(hours) + "h" + hms_formatter.format(mins);
        	else
        		time_str = hms_formatter.format(mins) + ":" + hms_formatter.format(secs);
        } else {
        	if (hours>0) 
        		time_str = hms_formatter.format(hours) + "h" + hms_formatter.format(mins) + "'";
        	else 
        		time_str = hms_formatter.format(mins) + "'" + hms_formatter.format(secs) + '"';        		
        }

        g2.setColor(nd_gc.chrono_background_color);
        g2.setFont(nd_gc.clock_font);
        g2.fillRect(x, y, nd_gc.chrono_box_w, nd_gc.chrono_box_h);
        g2.setColor(nd_gc.chrono_color);
        if (nd_gc.boeing_style) g2.drawString(time_label, x + nd_gc.chrono_time_x - nd_gc.get_text_width(g2, nd_gc.clock_font, time_label) - nd_gc.digit_width_s, y + nd_gc.chrono_time_y);
        g2.drawString(time_str, x + nd_gc.chrono_time_x, y + nd_gc.chrono_time_y);
    }

}
