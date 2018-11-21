/**
* ClockDial.java
* 
* Dial clock version
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2018  Nicolas Carel
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
package net.sourceforge.xhsi.flightdeck.clock;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;


public class ClockDial extends ClockSubcomponent {

    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private DecimalFormat hms_formatter;
    
	private boolean et_button_pressed;
	private long et_button_timestamp;

    public ClockDial(ModelFactory model_factory, ClockGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        hms_formatter = new DecimalFormat("00");
    	et_button_pressed = false;
    	et_button_timestamp = 0;
    }


    public void paint(Graphics2D g2) {
    	if ( ! clock_gc.digital_clock_style ) {

    		g2.drawImage(clock_gc.background_img, clock_gc.clock_square.x , clock_gc.clock_square.y , null);
    		
    		// TODO: link with DU configured ELEC bus
    		if ( this.aircraft.battery() ) {
    			draw_time(g2);
    			draw_timer(g2);
    		}
    		
    		if (preferences.get_draw_mouse_areas()) drawMouseAreas(g2);
    	}
    }


    public void draw_time(Graphics2D g2) {

        g2.setColor(clock_gc.clock_color);

        int current_time = this.avionics.clock_shows_utc() ? (int)this.aircraft.sim_time_zulu() : (int)this.aircraft.sim_time_local();
        int hh = current_time / 3600;
        int mm = ( current_time / 60 ) % 60;
        // int ss = current_time % 60;
        String current_time_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm); // + ":" + hms_formatter.format(ss);

        if ( // button_date_pressed
        		this.avionics.clock_date_show()
        		&& (this.avionics.clock_utc_source() != Avionics.CLOCK_SRC_SET)) {
        	current_time_str = hms_formatter.format(this.avionics.clock_date_month())
        			+ " " 
        			+ hms_formatter.format(this.avionics.clock_date_day());
        	// current_time_utc_sec_str = hms_formatter.format(this.avionics.clock_date_year() % 100);
        } else {
        	current_time_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm); // + ":" + hms_formatter.format(ss);
        }
        
        int current_time_x = clock_gc.clock_x - clock_gc.get_text_width(g2, clock_gc.font_zl, current_time_str)/2;
        g2.setFont(clock_gc.font_zl);
        g2.drawString(current_time_str, current_time_x, clock_gc.utc_time_string_y);

        g2.setFont(clock_gc.font_xxl);
        if ( this.avionics.clock_shows_utc() ) {
            g2.drawString("UTC", clock_gc.utc_string_x, clock_gc.utc_string_y);
        } else {
            g2.drawString("LT", clock_gc.lt_string_x, clock_gc.lt_string_y);
        }

    }

    public void draw_timer(Graphics2D g2) {

        DecimalFormat hms_formatter = new DecimalFormat("00");

        g2.setColor(clock_gc.clock_color);

        float chr_time = this.aircraft.timer_elapsed_time();

        if ( chr_time == 0.0f ) {
            /*
             * Display ET (Elapsed Time)
             */
            int timer = (int)this.aircraft.total_flight_time();
            int hh = ( timer / 3600 ) % 24;
            int mm = ( timer / 60 ) % 60;
            String timer_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm); // + ":" + hms_formatter.format(ss);
            int timer_x = clock_gc.clock_x - clock_gc.get_text_width(g2, clock_gc.font_zl, timer_str)/2;
            
            g2.setFont(clock_gc.font_zl);            
            g2.drawString(timer_str, timer_x, clock_gc.elapsed_time_string_y);
            
            g2.setFont(clock_gc.font_xxl);
            g2.drawString("ET", clock_gc.et_string_x, clock_gc.et_string_y );
            
            if (this.avionics.clock_et_selector() == Avionics.CLOCK_ET_STOP) {
            	g2.drawString("HLD", clock_gc.run_hold_string_x, clock_gc.run_hold_string_y);
            } else {
            	g2.drawString("RUN", clock_gc.run_hold_string_x, clock_gc.run_hold_string_y);
            }
            
        } else {
            /*
             * Display Main Chrono
             */
            int timer = (int)chr_time;
            int mins = timer / 60;
            int secs = timer % 60;
            String timer_str = (mins==0?"":mins) + ":" + hms_formatter.format(secs);
            int timer_x = clock_gc.clock_x + clock_gc.get_text_width(g2, clock_gc.font_zl, "99:99")/2 - clock_gc.get_text_width(g2, clock_gc.font_zl, timer_str);

            g2.setFont(clock_gc.font_zl);
            g2.drawString(timer_str, timer_x, clock_gc.timer_string_y);

            g2.setFont(clock_gc.font_xxl);
            g2.drawString("CHR", clock_gc.chr_string_x, clock_gc.chr_string_y);
            
            AffineTransform original_at = g2.getTransform();
            g2.rotate(Math.toRadians(secs*6), clock_gc.clock_x, clock_gc.clock_y);
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(clock_gc.needle_stroke);
            g2.drawLine(clock_gc.clock_x, clock_gc.clock_y - clock_gc.clock_square.width*14/64, clock_gc.clock_x, clock_gc.clock_y - clock_gc.clock_square.width*25/64);
            g2.setStroke(original_stroke);
            g2.setTransform(original_at);            
        }
    }
    
    public void drawMouseAreas(Graphics2D g2) {
    	g2.setColor(Color.yellow);
    	for (int i = 0; i < clock_gc.buttons.length; i++) {
    		g2.draw(clock_gc.buttons[i]);
    	}
    }

    public void mouseClicked(Graphics2D g2, MouseEvent e) {
    	if ( clock_gc.analog_clock_style ) {
    		for (int i = 0; i < clock_gc.buttons.length; i++) {
    			if (clock_gc.buttons[i].contains(e.getX(), e.getY())) {
    				switch (i) {
    				case ClockGraphicsConfig.BUTTON_RESET:
    					// SET button - INOP  					
    					break;
    				case ClockGraphicsConfig.BUTTON_CHR:
    					this.avionics.chr_control(Avionics.CHR_CONTROL_START_STOP_RESET);    					
    					break;
    				case ClockGraphicsConfig.BUTTON_DATE:
    					/*
    					 * mode order:
    					 * - UTC Time
    					 * - UTC Date
    					 * - LT  Time
    					 * - LT  Date (same as UTC date)
    					 * This differs from the B737 behavior, LT is replace by INTERNAL clock reference.
    					 */
    					if ((this.avionics.clock_utc_source() != Avionics.CLOCK_SRC_SET)) {
    						if (!this.avionics.clock_date_show() && this.avionics.clock_shows_utc()) {
    							this.avionics.set_clock_show_date(true);
    						} else if (this.avionics.clock_date_show() && this.avionics.clock_shows_utc()) {
    							this.avionics.set_clock_mode(Avionics.CLOCK_MODE_LT);
    							this.avionics.set_clock_show_date(false);
    						} else if (!this.avionics.clock_date_show() && !this.avionics.clock_shows_utc()) {
    							this.avionics.set_clock_show_date(true);
    						} else {
    							// default mode
    							this.avionics.set_clock_mode(Avionics.CLOCK_MODE_UTC);
    							this.avionics.set_clock_show_date(false);
    						}
    					}
    					break;
    				case ClockGraphicsConfig.BUTTON_GPS:
    					// GPS button - INOP
    					break;
    				case ClockGraphicsConfig.BUTTON_RUN:
    					// ET button
    					if (et_button_pressed && (System.currentTimeMillis()>et_button_timestamp)) {
    						this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_RESET);	
    					} else {
    						if (this.avionics.clock_et_selector() == Avionics.CLOCK_ET_STOP) {
    							this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_RUN);
    						} else {
    							this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_STOP);
    						}
    					}
    					et_button_pressed=false;
    					break;
    				}
    			}
    		}
    	}  	
    }

    public void mouseDragged(Graphics2D g2, MouseEvent e) {
    }  

    public void mouseReleased(Graphics2D g2, MouseEvent e) {
    }

    public void mousePressed(Graphics2D g2, MouseEvent e) {
    	if ( clock_gc.analog_clock_style ) {
    		if (clock_gc.buttons[ClockGraphicsConfig.BUTTON_RUN].contains(e.getX(), e.getY())) {
    			// if ET button is hold more than 1 sec, then reset ET
    			et_button_pressed=true;
    			et_button_timestamp=System.currentTimeMillis()+1000;
    		}
    	}   	
    }

}
