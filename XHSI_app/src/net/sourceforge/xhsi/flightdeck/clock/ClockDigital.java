/**
 * ClockDigital.java
 *
 * Digital Clock version. Thanks to the Technische Hochschule Ingolstadt
 * for providing the first version.
 *
 * Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2018  Nicolas Carel
 * Copyright (C) 2018  the Technische Hochschule Ingolstadt 
 *                     - Patrick Burkart
 *                     - Tim Drouven
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
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;


public class ClockDigital extends ClockSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private enum ButtonRunPosition { RUN, STOP, RESET };
    private enum ButtonGPSPosition { GPS, INT, SET };
    private ButtonRunPosition button_run_position = ButtonRunPosition.RUN;
    private ButtonGPSPosition button_gps_position = ButtonGPSPosition.GPS;
 
    private boolean button_gps_pressed = false;
    private boolean button_run_pressed = false;
    int mouse_pressed_x;
    int mouse_pressed_y;
    private boolean button_date_pressed;
    private boolean chr_is_running = false;
    
    private DecimalFormat hms_formatter;
   

    public ClockDigital(ModelFactory model_factory, ClockGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        hms_formatter = new DecimalFormat("00");
    }

    public void paint(Graphics2D g2) {
    	if (  clock_gc.digital_clock_style ) {

    		g2.drawImage(clock_gc.background_img, clock_gc.clock_square.x , clock_gc.clock_square.y , null);
    		
    		drawSelectors(g2);
    		// TODO: link with DU configured ELEC bus
    		if (this.aircraft.battery()) {
    			draw_time(g2);
    			draw_timer(g2);
    		}

    		if (preferences.get_draw_mouse_areas()) drawMouseAreas(g2);
    	}
    }


	public void draw_time(Graphics2D g2) {

        int current_time_utc = (int) this.aircraft.sim_time_zulu();
        int elapsed_time = (int) this.aircraft.total_flight_time();
        int hh_utc = current_time_utc / 3600;
        int mm_utc = (current_time_utc / 60) % 60;
        int sc_utc = current_time_utc % 60;
        int hh_et = elapsed_time / 3600;
        int mm_et = (elapsed_time / 60) % 60;

        String current_time_utc_str;
        String current_time_utc_sec_str;
        if ( // button_date_pressed
        		this.avionics.clock_date_show()
        		&& (this.avionics.clock_utc_source() != Avionics.CLOCK_SRC_SET)) {
        	current_time_utc_str = hms_formatter.format(this.avionics.clock_date_month())
        			+ " " 
        			+ hms_formatter.format(this.avionics.clock_date_day());
        	current_time_utc_sec_str = hms_formatter.format(this.avionics.clock_date_year() % 100);
        } else {
        	current_time_utc_str = hms_formatter.format(hh_utc) + ":" + hms_formatter.format(mm_utc);
        	current_time_utc_sec_str = hms_formatter.format(sc_utc);
        }
        String current_time_lcl_str = hms_formatter.format(hh_et) + ":" + hms_formatter.format(mm_et); // + ":" + hms_formatter.format(ss);

        g2.setColor(clock_gc.clock_color);
        g2.setFont(clock_gc.font_seven_seg_large);
        g2.drawString(current_time_utc_str, clock_gc.utc_time_string_x, clock_gc.utc_time_string_y);
        g2.drawString(current_time_lcl_str, clock_gc.elapsed_time_string_x, clock_gc.elapsed_time_string_y);
        g2.setFont(clock_gc.font_seven_seg);
        g2.drawString(current_time_utc_sec_str, clock_gc.utc_second_string_x, clock_gc.utc_second_string_y);

    }

    public void draw_timer(Graphics2D g2) {
        float chr_time = this.aircraft.timer_elapsed_time();
        int timer = (int)chr_time;
        int mins = timer / 60;
        int secs = timer % 60;
        
        g2.setColor(clock_gc.clock_color);
        String timer_str = hms_formatter.format(mins) + ":" + hms_formatter.format(secs);

        g2.setFont(clock_gc.font_seven_seg_large);
        g2.drawString(timer_str, clock_gc.timer_string_x, clock_gc.timer_string_y);
    }

    
    private void drawSelectors(Graphics2D g2) {
    	g2.setColor(Color.decode("#D8D8D8"));
    	AffineTransform original_at = g2.getTransform();
    	
    	switch (this.avionics.clock_utc_source()) {
    	case Avionics.CLOCK_SRC_GPS:
    		break;
    	case Avionics.CLOCK_SRC_INT:
    		g2.rotate(45.0 / 180 * Math.PI, clock_gc.button_gps_center_x, clock_gc.button_gps_center_y);
    		break;
    	case Avionics.CLOCK_SRC_SET:
    		g2.rotate(90.0 / 180 * Math.PI, clock_gc.button_gps_center_x, clock_gc.button_gps_center_y);
    		break;
    	}
    	g2.fill(clock_gc.button_gps);
    	g2.setTransform(original_at);
    	
    	if (button_run_position != ButtonRunPosition.RESET) {
    		button_run_position = (avionics.clock_et_selector() == Avionics.CLOCK_ET_STOP) ? ButtonRunPosition.STOP : ButtonRunPosition.RUN;
    	}
    	
    	switch (button_run_position) {
    	case RUN:
    		break;
    	case STOP:
    		g2.rotate(45.0 / 180 * Math.PI, clock_gc.button_run_center_x, clock_gc.button_run_center_y);
    		break;
    	case RESET:
    		g2.rotate(90.0 / 180 * Math.PI, clock_gc.button_run_center_x, clock_gc.button_run_center_y);
    		break;
    	}
    	g2.fill(clock_gc.button_run);
    	g2.setTransform(original_at);
    }
    
    public void drawMouseAreas(Graphics2D g2) {
    	g2.setColor(Color.yellow);
    	for (int i = 0; i < clock_gc.buttons.length; i++) {
    		g2.draw(clock_gc.buttons[i]);
    	}
    }
    
    public void mouseClicked(Graphics2D g2, MouseEvent e) {  
    	if ( clock_gc.digital_clock_style ) {
    		for (int i = 0; i < clock_gc.buttons.length; i++) {
    			if (clock_gc.buttons[i].contains(e.getX(), e.getY())) {
    				switch (i) {
    				case ClockGraphicsConfig.BUTTON_RESET:
    					this.avionics.chr_control(Avionics.CHR_CONTROL_RESET);
    					break;
    				case ClockGraphicsConfig.BUTTON_CHR:
    					this.avionics.chr_control(Avionics.CHR_CONTROL_START_STOP);    					
    					chr_is_running = !chr_is_running;
    					break;
    				case ClockGraphicsConfig.BUTTON_DATE:
    					if ((this.avionics.clock_utc_source() != Avionics.CLOCK_SRC_SET)) {
    						this.avionics.set_clock_show_date(!this.avionics.clock_date_show());
    						button_date_pressed = !button_date_pressed;
    					}
    					break;
    				case ClockGraphicsConfig.BUTTON_GPS:
    					break;
    				case ClockGraphicsConfig.BUTTON_RUN:
    					break;
    				}
    			}
    		}
    	}
    }


    public void mousePressed(Graphics2D g2, MouseEvent e) {
    	if ( clock_gc.digital_clock_style ) {
    		for (int i = 0; i < clock_gc.buttons.length; i++) {
    			if (clock_gc.buttons[i].contains(e.getX(), e.getY())) {
    				switch (i) {
    				case ClockGraphicsConfig.BUTTON_RESET:
    					break;
    				case ClockGraphicsConfig.BUTTON_CHR:
    					break;
    				case ClockGraphicsConfig.BUTTON_DATE:
    					break;
    				case ClockGraphicsConfig.BUTTON_GPS:
    					button_gps_pressed = true;
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    					break;
    				case ClockGraphicsConfig.BUTTON_RUN:
    					button_run_pressed = true;
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    					break;

    				}
    			}
    		}
    	}
    }


    public void mouseDragged(Graphics2D g2, MouseEvent e) {
    	if ( clock_gc.digital_clock_style ) {
    		int xMovement = mouse_pressed_x - e.getX();
    		int yMovement = mouse_pressed_y - e.getY();
    		int movement = (int) Math.sqrt((xMovement * xMovement) + (yMovement * yMovement));
    		movement = xMovement < 0 ? movement : -movement;
    		if (button_gps_pressed) {
    			// Turn cw
    			if (movement > clock_gc.button_mvt_x) {
    				if (button_gps_position == ButtonGPSPosition.GPS) {
    					this.avionics.set_clock_utc_source(Avionics.CLOCK_SRC_INT);
    					button_gps_position = ButtonGPSPosition.INT;
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_gps_position == ButtonGPSPosition.INT) {
    					this.avionics.set_clock_utc_source(Avionics.CLOCK_SRC_SET);
    					button_gps_position = ButtonGPSPosition.SET;
    				}
    			}
    			// Turn ccw
    			if (movement < -clock_gc.button_mvt_x) {
    				if (button_gps_position == ButtonGPSPosition.SET) {
    					this.avionics.set_clock_utc_source(Avionics.CLOCK_SRC_INT);
    					button_gps_position = ButtonGPSPosition.INT;
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_gps_position == ButtonGPSPosition.INT) {
    					this.avionics.set_clock_utc_source(Avionics.CLOCK_SRC_GPS);
    					button_gps_position = ButtonGPSPosition.GPS;
    				}
    			}
    		} else if (button_run_pressed) {
    			// Turn cw
    			if (movement > clock_gc.button_mvt_x) {
    				if (button_run_position == ButtonRunPosition.RUN) {
    					button_run_position = ButtonRunPosition.STOP;
    					this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_STOP);
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_run_position == ButtonRunPosition.STOP) {
    					button_run_position = ButtonRunPosition.RESET;
    					this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_RESET);
    				}
    			}
    			// Turn ccw
    			if (movement < -clock_gc.button_mvt_x) {
    				if (button_run_position == ButtonRunPosition.RESET) {
    					button_run_position = ButtonRunPosition.STOP;
    					this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_STOP);
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_run_position == ButtonRunPosition.STOP) {
    					this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_RUN);
    					button_run_position = ButtonRunPosition.RUN;
    				}
    			}
    		}
    	}
    }

    public void mouseReleased(Graphics2D g2, MouseEvent e) {
    	if ( clock_gc.digital_clock_style ) {
    		button_run_pressed = false;
    		button_gps_pressed = false;
    		if (button_run_position == ButtonRunPosition.RESET) {
    			this.avionics.set_clock_et_selector(Avionics.CLOCK_ET_STOP);
    			button_run_position = ButtonRunPosition.STOP;
    		}
    	}
    }

}
