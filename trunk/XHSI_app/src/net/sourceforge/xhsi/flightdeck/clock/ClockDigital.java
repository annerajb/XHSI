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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import java.text.DecimalFormat;
/* Java 1.8 
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
*/
import java.util.Date;
import java.util.Calendar;

import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;


public class ClockDigital extends ClockSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private double r_w;
    private double r_h;
    // private LocalTime timerStart;

    private final static int BUTTON_RESET = 0;
    private final static int BUTTON_CHR = 1;
    private final static int BUTTON_DATE = 2;
    private final static int BUTTON_GPS = 3;
    private final static int BUTTON_RUN = 4;

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
    private long chrono_elapsed_time = 0;
    private long chrono_start_time;

    // TODO: Date display
    // private Date date = new Date();
    
    private Shape[] buttons = new Shape[5];

    public ClockDigital(ModelFactory model_factory, ClockGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        logger.finest("ClockDial instanciated");
    }

    public void paint(Graphics2D g2) {
    	if (  clock_gc.digital_clock_style ) {

    		// TODO: Should be computed in ClockGraphicsConfig
    		r_w = clock_gc.clock_rect.width / 1064.0;
    		r_h = clock_gc.clock_rect.height / 994.0;

    		drawDial(g2);
    		// TODO: link with DU configured ELEC bus
    		if (this.aircraft.battery()) {
    			draw_time(g2);
    			draw_timer(g2);
    		}
    	}
    }


	public void draw_time(Graphics2D g2) {

        DecimalFormat hms_formatter = new DecimalFormat("00");

        int current_time_utc = (int) this.aircraft.sim_time_zulu();
        int elapsed_time = (int) this.aircraft.total_flight_time();
        int hh_utc = current_time_utc / 3600;
        int mm_utc = (current_time_utc / 60) % 60;
        int sc_utc = current_time_utc % 60;
        int hh_et = elapsed_time / 3600;
        int mm_et = (elapsed_time / 60) % 60;

        int hh = 0;
        int mm = 0;

        // int ss = current_time % 60;
        DecimalFormat s_formatter = new DecimalFormat("00");
        String current_time_utc_str;
        String current_time_utc_sec_str;
        if (button_date_pressed
        		&& (button_gps_position == ButtonGPSPosition.GPS 
        				|| button_gps_position == ButtonGPSPosition.INT )) {
        	/*
        	int day = (int) (aircraft.sim_time_zulu() / (60 * 60 * 24));
        	date.setDate(day);
        	current_time_utc_str = LocalDate.now().format(DateTimeFormatter.ofPattern("MM.dd."));
        	current_time_utc_sec_str = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
        	*/
        	current_time_utc_str = "--.--";
        	current_time_utc_sec_str = "--";
        } else {
        	current_time_utc_str = hms_formatter.format(hh_utc) + ":" + hms_formatter.format(mm_utc);
        	current_time_utc_sec_str = s_formatter.format(sc_utc);
        }
        String current_time_lcl_str = hms_formatter.format(hh_et) + ":" + hms_formatter.format(mm_et); // + ":" + hms_formatter.format(ss);
        String timer = hms_formatter.format(hh) + ":" + hms_formatter.format(mm);
        g2.setColor(Color.WHITE);
        g2.setFont(clock_gc.font_seven_seg_large);
        g2.drawString(current_time_utc_str, clock_gc.utc_time_string_x, clock_gc.utc_time_string_y);
        g2.drawString(current_time_lcl_str, clock_gc.elapsed_time_string_x, clock_gc.elapsed_time_string_y);
        g2.setFont(clock_gc.font_seven_seg);
        g2.drawString(current_time_utc_sec_str, clock_gc.utc_second_string_x, clock_gc.utc_second_string_y);

    }

    public void draw_timer(Graphics2D g2) {

        DecimalFormat hms_formatter = new DecimalFormat("00");

        g2.setColor(clock_gc.clock_color);

        float chr_time = this.aircraft.timer_elapsed_time();
        int timer = (int)chr_time;
        int mins = timer / 60;
        int secs = timer % 60;
        
        g2.setColor(Color.WHITE);
        String timer_str = "";
        long chr_et = chrono_elapsed_time + (chr_is_running ? (System.currentTimeMillis() - chrono_start_time) : 0);
        chr_et = chr_et / 1000;

        long mm = (chr_et / 60) % 60;
        long ss = chr_et % 60;
        // timer_str = hms_formatter.format(mm) + ":" + hms_formatter.format(ss);
        timer_str = hms_formatter.format(mins) + ":" + hms_formatter.format(secs);

        g2.setFont(clock_gc.font_seven_seg_large);
        g2.drawString(timer_str, clock_gc.timer_string_x, clock_gc.timer_string_y);

    }

    private void drawDial(Graphics2D g2) {
    	/*
    	 * General rule : avoid using new in paint loop 
    	 * ColorUtilities.multiply calls new at every call
    	 */
    	// g2.setColor(ColorUtilities.multiply(Color.decode("#54616E"), aircraft.get_cockpit_light_color()));
    	g2.setColor(clock_gc.clock_digital_54616E_color);
    	g2.fillPolygon(clock_gc.frame_x, clock_gc.frame_y, clock_gc.frame_x.length);
    	// g2.setColor(ColorUtilities.multiply(Color.decode("#54616E").darker().darker(), aircraft.get_cockpit_light_color()));
    	g2.setColor(clock_gc.clock_digital_54616E_dark_color);
    	g2.drawPolygon(clock_gc.frame_x, clock_gc.frame_y, clock_gc.frame_x.length);

    	// g2.setColor(ColorUtilities.multiply(Color.decode("#080808"), aircraft.get_cockpit_light_color()));
    	g2.setColor(clock_gc.clock_digital_dark_gray_color);

    	g2.fillRoundRect(clock_gc.background_x, clock_gc.background_y, clock_gc.background_width, clock_gc.background_height, clock_gc.background_corner_size, clock_gc.background_corner_size);
    	g2.setColor(Color.BLACK);
    	g2.fillRoundRect(clock_gc.chr_background_x, clock_gc.chr_background_y, clock_gc.chr_background_width, clock_gc.chr_background_height, clock_gc.times_background_corner_size, clock_gc.times_background_corner_size);
    	g2.fillRoundRect(clock_gc.utc_background_x, clock_gc.utc_background_y, clock_gc.utc_background_width, clock_gc.utc_background_height, clock_gc.times_background_corner_size, clock_gc.times_background_corner_size);
    	g2.fillRoundRect(clock_gc.et_background_x, clock_gc.et_background_y, clock_gc.et_background_width, clock_gc.et_background_height, clock_gc.times_background_corner_size, clock_gc.times_background_corner_size);

    	g2.setColor(Color.RED);
    	// TODO: avoid calling new !
    	g2.setStroke(new BasicStroke(1));
    	g2.drawRoundRect(clock_gc.chr_background_x, clock_gc.chr_background_y, clock_gc.chr_background_width, clock_gc.chr_background_height, clock_gc.times_background_corner_size, clock_gc.times_background_corner_size);
    	g2.drawRoundRect(clock_gc.utc_background_x, clock_gc.utc_background_y, clock_gc.utc_background_width, clock_gc.utc_background_height, clock_gc.times_background_corner_size, clock_gc.times_background_corner_size);
    	g2.drawRoundRect(clock_gc.et_background_x, clock_gc.et_background_y, clock_gc.et_background_width, clock_gc.et_background_height, clock_gc.times_background_corner_size, clock_gc.times_background_corner_size);

    	g2.setFont(clock_gc.font_label_bold);
    	// g2.setColor(clock_gc.getInstumentMarkerColor(aircraft.get_cockpit_light_color()));
    	g2.setColor(clock_gc.markings_color);
    	g2.drawString("RST", clock_gc.rst_string_x, clock_gc.rst_string_y);
    	g2.drawString("CHR", clock_gc.chr_string_x, clock_gc.chr_string_y);
    	g2.drawString("DATE", clock_gc.date_string_x, clock_gc.date_string_y);
    	g2.drawString("GPS", clock_gc.gps_string_x, clock_gc.gps_string_y);
    	g2.drawString("RUN", clock_gc.run_string_x, clock_gc.run_string_y);
    	g2.setColor(Color.WHITE);
    	g2.setFont(clock_gc.font_label);
    	g2.drawString("CHR", clock_gc.chr2_string_x, clock_gc.chr2_string_y);
    	g2.drawString("UTC", clock_gc.utc_string_x, clock_gc.utc_string_y);
    	g2.drawString("ET", clock_gc.et_string_x, clock_gc.et_string_y);
    	g2.setFont(clock_gc.font_label_small_condensed);
    	g2.drawString("INT", clock_gc.int_string_x, clock_gc.int_string_y);
    	g2.drawString("SET", clock_gc.set_string_x, clock_gc.set_string_y);
    	g2.drawString("STP", clock_gc.stp_string_x, clock_gc.stp_string_y);
    	g2.drawString("RST", clock_gc.rst_run_string_x, clock_gc.rst_run_string_y);
    	g2.setFont(clock_gc.font_label_small);
    	g2.setColor(Color.decode("#009EDE"));
    	g2.drawString("MIN", clock_gc.min_string_x, clock_gc.min_string_y);
    	g2.drawString("SEC", clock_gc.sec_string_x, clock_gc.sec_string_y);
    	g2.drawString("HR/MO", clock_gc.hr_mo_string_x, clock_gc.hr_mo_string_y);
    	g2.drawString("MIN/DY", clock_gc.min_dy_string_x, clock_gc.min_dy_string_y);
    	g2.drawString("SEC/Y", clock_gc.sec_y_string_x, clock_gc.sec_y_string_y);
    	g2.drawString("HR", clock_gc.hr_string_x, clock_gc.hr_string_y);
    	g2.drawString("MIN", clock_gc.min2_string_x, clock_gc.min2_string_y);

    	g2.setColor(Color.decode("#D8D8D8"));
    	buttons[BUTTON_RESET] = new Ellipse2D.Float(clock_gc.button_reset_x, clock_gc.button_reset_y, clock_gc.small_button_size_x, clock_gc.small_button_size_y);
    	buttons[BUTTON_CHR] = new Ellipse2D.Float(clock_gc.button_chr_x, clock_gc.button_chr_y, clock_gc.small_button_size_x, clock_gc.small_button_size_y);
    	buttons[BUTTON_DATE] = new Ellipse2D.Float(clock_gc.button_date_x, clock_gc.button_date_y, clock_gc.large_button_size_x, clock_gc.large_button_size_y);
    	buttons[BUTTON_GPS] = new Polygon(clock_gc.button_gps_x, clock_gc.button_gps_y, clock_gc.button_gps_x.length);
    	buttons[BUTTON_RUN] = new Polygon(clock_gc.button_run_x, clock_gc.button_run_y, clock_gc.button_run_x.length);
    	g2.fill(buttons[BUTTON_RESET]);
    	g2.fill(buttons[BUTTON_CHR]);
    	g2.fill(buttons[BUTTON_DATE]);
    	AffineTransform original_at = g2.getTransform();
    	switch (button_gps_position) {
    	case GPS:
    		break;
    	case INT:
    		g2.rotate(45.0 / 180 * Math.PI, inRel(r_w, 916), inRel(r_h, 494));
    		break;
    	case SET:
    		g2.rotate(90.0 / 180 * Math.PI, inRel(r_w, 916), inRel(r_h, 494));
    		break;
    	}
    	g2.fill(buttons[BUTTON_GPS]);
    	g2.setTransform(original_at);

    	switch (button_run_position) {
    	case RUN:
    		break;
    	case STOP:
    		g2.rotate(45.0 / 180 * Math.PI, inRel(r_w, 916), inRel(r_h, 730));
    		break;
    	case RESET:
    		g2.rotate(90.0 / 180 * Math.PI, inRel(r_w, 916), inRel(r_h, 730));
    		break;
    	}
    	g2.fill(buttons[BUTTON_RUN]);
    	g2.setTransform(original_at);

    }

    
    private static int inRel(double relation, int coordinate) {
        return (int) (relation * coordinate);
    }

    
    public void mouseClicked(Graphics2D g2, MouseEvent e) {  
    	if (  clock_gc.digital_clock_style ) {
    		for (int i = 0; i < buttons.length; i++) {
    			if (buttons[i].contains(e.getX(), e.getY())) {
    				switch (i) {
    				case BUTTON_RESET:
    					this.avionics.chr_control(Avionics.CHR_CONTROL_RESET);
    					chrono_elapsed_time = 0;
    					break;
    				case BUTTON_CHR:
    					this.avionics.chr_control(Avionics.CHR_CONTROL_START_STOP);    					
    					if (chr_is_running) {
    						long et = System.currentTimeMillis() - chrono_start_time;
    						chrono_elapsed_time += et;
    					} else {
    						chrono_start_time = System.currentTimeMillis();
    					}
    					chr_is_running = !chr_is_running;
    					break;
    				case BUTTON_DATE:
    					if (button_gps_position != ButtonGPSPosition.SET) {
    						button_date_pressed = !button_date_pressed;
    					}
    					break;
    				case BUTTON_GPS:
    					break;
    				case BUTTON_RUN:
    					break;
    				}
    			}
    		}
    	}
    }


    public void mousePressed(Graphics2D g2, MouseEvent e) {
    	if (  clock_gc.digital_clock_style ) {
    		for (int i = 0; i < buttons.length; i++) {
    			if (buttons[i].contains(e.getX(), e.getY())) {
    				switch (i) {
    				case BUTTON_RESET:
    					break;
    				case BUTTON_CHR:
    					break;
    				case BUTTON_DATE:
    					break;
    				case BUTTON_GPS:
    					button_gps_pressed = true;
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    					break;
    				case BUTTON_RUN:
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
    	if (  clock_gc.digital_clock_style ) {
    		int xMovement = mouse_pressed_x - e.getX();
    		int yMovement = mouse_pressed_y - e.getY();
    		int movement = (int) Math.sqrt((xMovement * xMovement) + (yMovement * yMovement));
    		movement = xMovement < 0 ? movement : -movement;
    		if (button_gps_pressed) {
    			// Turn cw
    			if (movement > inRel(r_w, 80)) {
    				if (button_gps_position == ButtonGPSPosition.GPS) {
    					button_gps_position = ButtonGPSPosition.INT;
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_gps_position == ButtonGPSPosition.INT) {
    					button_gps_position = ButtonGPSPosition.SET;
    				}
    			}
    			// Turn ccw
    			if (movement < inRel(r_w, -80)) {
    				if (button_gps_position == ButtonGPSPosition.SET) {
    					button_gps_position = ButtonGPSPosition.INT;
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_gps_position == ButtonGPSPosition.INT) {
    					button_gps_position = ButtonGPSPosition.GPS;
    				}
    			}
    		} else if (button_run_pressed) {
    			// Turn cw
    			if (movement > inRel(r_w, 80)) {
    				if (button_run_position == ButtonRunPosition.RUN) {
    					button_run_position = ButtonRunPosition.STOP;
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_run_position == ButtonRunPosition.STOP) {
    					// TODO WARNING: check behaviour - bug in original code
    					// button_gps_position = BUTTON_RUN_RST;
    					button_run_position = ButtonRunPosition.RESET;
    				}
    			}
    			// Turn ccw
    			if (movement < inRel(r_w, -80)) {
    				if (button_run_position == ButtonRunPosition.RESET) {
    					button_run_position = ButtonRunPosition.STOP;
    					// Reset origin so that if user continues to swipe, button keeps turning
    					mouse_pressed_x = e.getX();
    					mouse_pressed_y = e.getY();
    				} else if (button_run_position == ButtonRunPosition.STOP) {
    					button_run_position = ButtonRunPosition.RUN;
    				}
    			}
    		}
    	}
    }

    public void mouseReleased(Graphics2D g2, MouseEvent e) {
    	if (  clock_gc.digital_clock_style ) {
    		button_run_pressed = false;
    		button_gps_pressed = false;
    		if (button_run_position == ButtonRunPosition.RESET) {
    			button_run_position = ButtonRunPosition.STOP;
    		}
    	}
    }

}
