/**
* AltiTape_A320.java
* 
* This is the Airbus A320 family version of AltiTape.java
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  Nicolas Carel
* Adapted for Airbus by Nicolas Carel
* Reference : A320 FCOM 1.31.40 page 11-14 REV 36
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;

public class AltiTape_A320 extends PFDSubcomponent {


    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public AltiTape_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
    	if ( pfd_gc.airbus_style ) {
    		if ( ! XHSIStatus.receiving || ! this.avionics.alt_valid() ) {
    			// FCOM 1.31.40 p26 (10) 
    			// if the altitude information fails, the ALT flag (red) replaces the altitude scale
    			if ( pfd_gc.powered ) drawFailedTape(g2);
    		} else if ( pfd_gc.powered ) {
    			drawTape(g2);
    		}
    	}
    }

    private void drawFailedTape(Graphics2D g2) {
        // Global style
        
        
        int altitape_right = pfd_gc.altitape_left + pfd_gc.tape_width*60/100;
        g2.setColor(pfd_gc.pfd_instrument_background_color);
        g2.fillRect(pfd_gc.altitape_left, pfd_gc.tape_top, 
        		    altitape_right-pfd_gc.altitape_left, pfd_gc.tape_height);
        g2.setColor(pfd_gc.pfd_alarm_color);
        g2.drawLine(altitape_right, pfd_gc.tape_top ,altitape_right, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top ,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top  );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top + pfd_gc.tape_height + 1,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
    	g2.setColor(pfd_gc.background_color);   	
    	g2.fillRect(pfd_gc.altitape_left - pfd_gc.tape_width*3/50, pfd_gc.adi_cy - pfd_gc.line_height_xxl*6/9,
    			pfd_gc.tape_width, pfd_gc.line_height_xxl*12/9);
        g2.setColor(pfd_gc.pfd_alarm_color);    	
    	String failed_str = "ALT";
        g2.setFont(pfd_gc.font_xxl);
    	g2.drawString( failed_str, pfd_gc.altitape_left,  pfd_gc.adi_cy + pfd_gc.line_height_l/2 );
    }
    
    private void drawTape(Graphics2D g2) {
        // Global style
        // boolean pfd_airbus = this.avionics.get_instrument_style() == Avionics.STYLE_AIRBUS;
        boolean display_metric = false;
        if (this.avionics.is_qpac() && this.avionics.qpac_fcu_metric_alt() ) { display_metric = true; } 
        
        // int altitape_right = pfd_gc.altitape_left + pfd_gc.digit_width_xxl*14/5;
        int altitape_right = pfd_gc.altitape_left + pfd_gc.tape_width*60/100;       
        
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.altitape_left - 1, pfd_gc.tape_top - 1, altitape_right - pfd_gc.altitape_left + 1, pfd_gc.tape_height + 2);	
        g2.setColor(pfd_gc.markings_color);
        g2.drawLine(altitape_right, pfd_gc.tape_top ,altitape_right, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top, pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top + pfd_gc.tape_height + 1,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top + pfd_gc.tape_height + 1 );            
        pfd_gc.setOpaque(g2);

        Shape original_clipshape = g2.getClip();
        // left and right don't matter...
        g2.clipRect(pfd_gc.altitape_left - pfd_gc.tape_width, pfd_gc.tape_top, pfd_gc.tape_width*3, pfd_gc.tape_height);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_xxl);

      
        // Altitude scale
        float alt = this.aircraft.altitude_ind();
        int alt_range = 500;
        int alt_modulo = 500;
        float alt_f_range = 1100.0f;
        
        // int alt_range = pfd_airbus ? 500 : 400;
        // int alt_modulo = pfd_airbus ? 500 : 200;
        // float alt_f_range = pfd_airbus ? 1100.0f : 800.0f;
        
//alt = 39660;
//float utc_time = this.aircraft.sim_time_zulu();
//alt = (utc_time) % 10000 -5300;


        // Landing altitude
        boolean loc_receive = false;
        NavigationRadio nav_radio;
        RadioNavigationObject nav_object;
        int dest_alt = 0;

        int source = this.avionics.hsi_source();
        if (source == Avionics.HSI_SOURCE_NAV1) {
            nav_radio = this.avionics.get_nav_radio(1);
            if ( nav_radio.receiving() ) {
                nav_object = nav_radio.get_radio_nav_object();
                if (nav_object instanceof Localizer) {
                    loc_receive = true;
                    dest_alt = nav_object.elevation;
                }
            }
        } else if(source == Avionics.HSI_SOURCE_NAV2) {
            nav_radio = this.avionics.get_nav_radio(2);
            if ( nav_radio.receiving() ) {
                nav_object = nav_radio.get_radio_nav_object();
                if (nav_object instanceof Localizer) {
                    loc_receive = true;
                    dest_alt = nav_object.elevation;
                }
            }
        }
        int gnd_y;
        float radio_altitude = this.aircraft.agl_m() * 3.28084f;
        float mda=400f;
        int ra_y = pfd_gc.adi_cy + Math.round( radio_altitude * pfd_gc.tape_height / alt_f_range );
        if ( loc_receive && ! this.aircraft.on_ground()) {
        	Stroke original_stroke = g2.getStroke();
        	int halfstroke = pfd_gc.tape_width/16;
        	// These indicators are not FCOM compliant, inherited from Boeing and adapter to Airbus style
        	// It should be a graphic option in the preference panel
            int loc_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt - alt) * pfd_gc.tape_height / alt_f_range );
            int h1000_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt + (float)1000 - alt) * pfd_gc.tape_height / alt_f_range );
            int mda_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt + mda - alt) * pfd_gc.tape_height / alt_f_range );
            // between 500 and 1000ft
            g2.setColor(pfd_gc.pfd_caution_color);
            g2.drawLine(altitape_right + pfd_gc.tape_width / 12, h1000_y, altitape_right + pfd_gc.tape_width / 12, mda_y);
            g2.drawLine(altitape_right+1, h1000_y, altitape_right + pfd_gc.tape_width / 12, h1000_y);
            //g2.fillRect(altitape_right+1,  h1000_y, pfd_gc.tape_width / 7 ,  pfd_gc.tape_top + pfd_gc.tape_height + h1000_y - h500_y );
            // between 500 and 1000ft            

        	g2.setStroke(new BasicStroke(2.0f * halfstroke));
            float red_dashes[] = { halfstroke*2.0f, halfstroke*2.0f };
            g2.setStroke(new BasicStroke(2.0f * halfstroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, red_dashes, 0.0f));
            g2.drawLine(altitape_right+1+halfstroke, mda_y, altitape_right+1+halfstroke, ra_y+2);
            // g2.fillRect(altitape_right+1, h500_y, pfd_gc.tape_width / 7 ,  pfd_gc.tape_top + pfd_gc.tape_height + h500_y - loc_y + 2 );
            // localizer altitude
            g2.setStroke(original_stroke);
            gnd_y = loc_y;
        } else {
            // ground elevation from the radar altitude
            gnd_y = pfd_gc.adi_cy + Math.round( radio_altitude * pfd_gc.tape_height / alt_f_range );
        }
        
        // Ground reference        
        // TODO : ??? on Airbus, flight phase 7 and 8 with QNH mode, display DH zone in amber
        // Airbus FCOM 1.31.40 p.13 (3) Ground reference - display below 570 ft
        
        if ( radio_altitude < 570f ) { 
            // Ground bar on Airbus
        	g2.setColor(pfd_gc.pfd_alarm_color); 
        	g2.fillRect(altitape_right+1, ra_y, pfd_gc.tape_width / 7 ,  pfd_gc.tape_top + pfd_gc.tape_height - ra_y + 2);
            // TODO : Airbus FCOM 1.31.40 p.13 (2) Landing elevation, an horizontal blue line indicates barometric Ground that may differ from radio altimeter ground
            g2.setColor(Color.blue);
            g2.drawLine(pfd_gc.altitape_left, gnd_y, altitape_right, gnd_y);
        } 

        		
        // scale markings
        // round to nearest multiple of 100
        int alt100 = Math.round(alt / 100.0f) * 100;
        
        // From there, go 500ft up and down on Airbus     
        for (int alt_mark = alt100 - alt_range; alt_mark <= alt100 + alt_range; alt_mark += 100) {

            int alt_y = pfd_gc.adi_cy - Math.round( ((float)alt_mark - alt) * pfd_gc.tape_height / alt_f_range );
            
            // Airbus marks are on the right
           	g2.setColor(pfd_gc.markings_color);
           	g2.drawLine(altitape_right, alt_y, altitape_right - pfd_gc.tape_width*1/12, alt_y);
 
            if (alt_mark % alt_modulo == 0) {
            	g2.setFont(pfd_gc.font_xl);
            	DecimalFormat markform = new DecimalFormat("000");

            	String mark_str = markform.format( Math.abs(alt_mark / 100));
            	g2.drawString(mark_str, pfd_gc.altitape_left, alt_y + pfd_gc.line_height_xl/2 - 2);

            	// Little triangle before mark
            	g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*1/20, alt_y, pfd_gc.altitape_left - pfd_gc.tape_width*4/40, alt_y + pfd_gc.tape_width*2/40);
            	g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*1/20, alt_y, pfd_gc.altitape_left - pfd_gc.tape_width*4/40, alt_y - pfd_gc.tape_width*2/40);
            }
        }

        // Yellow reference line
        g2.setClip(original_clipshape);
        g2.setColor(pfd_gc.pfd_reference_color);
        Stroke original_stroke = g2.getStroke();        
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*9/16, pfd_gc.tape_top + pfd_gc.tape_height / 2 , pfd_gc.altitape_left - pfd_gc.tape_width*3/16,  pfd_gc.tape_top + pfd_gc.tape_height / 2);
        g2.setStroke(original_stroke);

        
        // DA arrow
        // Code for Boeing, not modified for Airbus -- let's see
        /*
        if ( this.aircraft.mins_is_baro() ) {

            int da_bug = this.aircraft.da_bug();
            if ( da_bug > 0 ) {
                float da = (float)da_bug;
                int da_y = pfd_gc.adi_cy - Math.round( (da - alt) * pfd_gc.tape_height / alt_f_range );
                if ( ( alt > da ) || this.aircraft.on_ground() ) {
                    g2.setColor(pfd_gc.color_lime);
                } else {
                    g2.setColor(pfd_gc.color_amber);
                }
                g2.drawLine(pfd_gc.altitape_left - 2, da_y, pfd_gc.altitape_left + pfd_gc.tape_width - 1, da_y);
                int[] da_triangle_x = {
                    pfd_gc.altitape_left - 1,
                    pfd_gc.altitape_left - 1 - pfd_gc.tape_width*5/20,
                    pfd_gc.altitape_left - 1 - pfd_gc.tape_width*5/20
                };
                int[] da_triangle_y = {
                    da_y,
                    da_y + pfd_gc.tape_width*6/20,
                    da_y - pfd_gc.tape_width*6/20
                };
                g2.drawPolygon(da_triangle_x, da_triangle_y, 3);
            }

        }
        */




//        // a small bug with the _current_ AP Alt
//        alt_y = pfd_gc.adi_cy - Math.round( (this.avionics.autopilot_current_altitude() - alt) * pfd_gc.tape_height / 800.0f );
//        if ( alt_y < pfd_gc.tape_top ) {
//            alt_y = pfd_gc.tape_top;
//        } else if ( alt_y > pfd_gc.tape_top + pfd_gc.tape_height ) {
//            alt_y = pfd_gc.tape_top + pfd_gc.tape_height;
//        }
//        int[] cur_bug_x = {
//            pfd_gc.altitape_left - pfd_gc.tape_width*1/16,
//            pfd_gc.altitape_left,
//            pfd_gc.altitape_left - pfd_gc.tape_width*1/16
//        };
//        int[] cur_bug_y = {
//            alt_y - pfd_gc.tape_width*3/40,
//            alt_y,
//            alt_y + pfd_gc.tape_width*3/40
//        };
//        g2.drawPolyline(cur_bug_x, cur_bug_y, 3);

        

        // QNH setting
        // TODO : On Airbus QNH flashes when transition altitude is missed
        // TODO : On Airbus Capt or F/O can select display in Inches or HPa     
        int qnh = this.aircraft.qnh();
        boolean qnh_display = true;
        boolean qnh_is_hpa = true;
        float alt_inhg = this.aircraft.altimeter_in_hg();
        boolean std = ( Math.round(alt_inhg * 100.0f) == 2992 );
        if (this.avionics.is_qpac()) { 
        	std = this.avionics.qpac_baro_std(); 
        	qnh_display = this.avionics.qpac_baro_hide();
        	qnh_is_hpa = this.avionics.qpac_baro_unit();
        }
        String qnh_str;
        if ( std ) {
            qnh_str = "STD";
        } else {
            if (qnh_is_hpa) { 
            	qnh_str = "" + qnh; 
            } else {
            	qnh_str = "" + this.aircraft.altimeter_in_hg();
            }
        }
        
        if (qnh_display) {
        	g2.setColor(pfd_gc.fmc_disp_color);
        	g2.setFont(pfd_gc.font_xl);
        	if ( ! std ) {
        		g2.drawString("QNH", pfd_gc.altitape_left, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*19/8);
        		g2.setColor(pfd_gc.pfd_selected_color);
        		g2.drawString(qnh_str, pfd_gc.altitape_left + 4*pfd_gc.digit_width_xl, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*19/8);

        	} else {
        		g2.setColor(pfd_gc.pfd_selected_color);
        		g2.drawString("STD", pfd_gc.altitape_left + pfd_gc.digit_width_xl*18/8, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*19/8);
        		g2.setColor(pfd_gc.pfd_reference_color);
        		g2.drawRect(pfd_gc.altitape_left + 2*pfd_gc.digit_width_xl, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*11/8, pfd_gc.digit_width_xl*29/8, pfd_gc.line_height_xl*10/8);
        	}
        }

        

        // AP ALT preselect
        // On Airbus ALT preselect depends on QNH setting
        // FL xxx if STD QNH
        // Position on top if alt preselect setting above alt
        // on bottom if alt preselect setting below alt
    	int ap_alt = Math.round(this.avionics.autopilot_altitude());
    	g2.setColor(pfd_gc.pfd_selected_color);
    	if (this.avionics.is_qpac()) {
    		if (this.avionics.qpac_alt_is_cstr()) {
    	    	g2.setColor(pfd_gc.pfd_managed_color);
    	    	ap_alt = this.avionics.qpac_constraint_alt();
    		}
    	}
    	
        // AP Alt bug
        int alt_y = Math.round(pfd_gc.adi_cy - (ap_alt - alt) * pfd_gc.tape_height / alt_f_range );
        boolean hide_bug = false;
        if ( alt_y < pfd_gc.tape_top ) {
            alt_y = pfd_gc.tape_top;
            hide_bug = true;
        } else if ( alt_y > pfd_gc.tape_top + pfd_gc.tape_height ) {
            alt_y = pfd_gc.tape_top + pfd_gc.tape_height;
            hide_bug = true;
        }

        int[] bug_x = {
        		pfd_gc.altitape_left - 2,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/24,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/24,
        		pfd_gc.altitape_left + pfd_gc.tape_width*4/20,
        		pfd_gc.altitape_left + pfd_gc.tape_width*4/20,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/24,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/24
        };
        int[] bug_y = {
        		alt_y,
        		alt_y + pfd_gc.tape_width*2/21,
        		alt_y + pfd_gc.tape_height*2/18,
        		alt_y + pfd_gc.tape_height*2/18,
        		alt_y - pfd_gc.tape_height*2/18,
        		alt_y - pfd_gc.tape_height*2/18,
        		alt_y - pfd_gc.tape_width*2/21
        };       
        if (! hide_bug ) { g2.drawPolygon(bug_x, bug_y, 7); }      
    	
    	String alt_str = "FL";
    	int alt_str_x = pfd_gc.altitape_left + pfd_gc.tape_width - pfd_gc.tape_width*1/16 - 5*pfd_gc.digit_width_l;
    	// top or bottom  pfd_gc.get_text_width(g2, pfd_gc.font_m, mark_str)
    	int alt_str_y = (ap_alt > alt) ? pfd_gc.tape_top - pfd_gc.tape_width/16 : pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_l*9/8 ;
    	// follow bug 
    	if ( ! hide_bug ) { 
    		alt_str_y = alt_y + pfd_gc.line_height_l / 2;
    		alt_str = "";
    	}

    	if (std) {
    		// Use flight level text
    		DecimalFormat feet_format = new DecimalFormat("000");
    		alt_str += feet_format.format(ap_alt /100);
    		alt_str_x = pfd_gc.altitape_left + pfd_gc.tape_width*1/16;
    	} else {
    		// Use all digits
    		DecimalFormat feet_format = new DecimalFormat("##000");
    		alt_str = feet_format.format(ap_alt);
    		alt_str = String.format("%1$5s", alt_str);
    		// Right align
    		alt_str_x =  pfd_gc.altitape_left + pfd_gc.tape_width - pfd_gc.tape_width*2/16 - pfd_gc.get_text_width(g2, pfd_gc.font_l, alt_str);

    	}
    	int alt_str_w = pfd_gc.get_text_width(g2, pfd_gc.font_l, alt_str);
    	g2.clearRect(pfd_gc.altitape_left-1, alt_str_y - pfd_gc.line_height_l*9/10, alt_str_w + pfd_gc.digit_width_l*2/3, pfd_gc.line_height_l);
    	g2.setFont(pfd_gc.font_l);
    	g2.drawString(alt_str, alt_str_x, alt_str_y);       	

        // AP Alt metric display
        if (display_metric) {        	
        	String metric_ap_str = Math.round(ap_alt / 328.08f)*100 + " M";
        	g2.drawString(metric_ap_str, pfd_gc.altitape_left - pfd_gc.digit_width_l - pfd_gc.get_text_width(g2, pfd_gc.font_l, metric_ap_str), pfd_gc.tape_top - pfd_gc.tape_width/12);
        	String metric_alt_str = ""+Math.round(alt / 32.808f)*10;
        	g2.setFont(pfd_gc.font_xl);
        	g2.setColor(pfd_gc.pfd_active_color);
        	g2.drawString(metric_alt_str, pfd_gc.altitape_left + pfd_gc.digit_width_xl*38/8 - pfd_gc.get_text_width(g2, pfd_gc.font_l, metric_alt_str), pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*31/8);
    		g2.setColor(pfd_gc.pfd_selected_color);
    		g2.drawString("M", pfd_gc.altitape_left + pfd_gc.digit_width_xl*46/8, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*31/8);
    		g2.setColor(pfd_gc.pfd_reference_color);
    		g2.drawRect(pfd_gc.altitape_left - 1*pfd_gc.digit_width_xl, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_xl*23/8, pfd_gc.digit_width_xl*69/8, pfd_gc.line_height_xl*10/8);

        }

    	// Altitude Indication
    	// Airbus FCOM 1.31.40 p11 (1)
    	// 
    	int[] box_x = {
    			pfd_gc.altitape_left - pfd_gc.tape_width*2/50,
    			altitape_right,
    			altitape_right,
    			pfd_gc.altitape_left + pfd_gc.digit_width_xxl*3 + pfd_gc.digit_width_xl * 9/4,
    			pfd_gc.altitape_left + pfd_gc.digit_width_xxl*3 + pfd_gc.digit_width_xl * 9/4,
    			altitape_right,
    			altitape_right,
    			pfd_gc.altitape_left - pfd_gc.tape_width*2/50,
    	};
    	int[] box_y = {
    			pfd_gc.adi_cy - pfd_gc.line_height_xxl*6/9,
    			pfd_gc.adi_cy - pfd_gc.line_height_xxl*6/9,
    			pfd_gc.adi_cy - pfd_gc.line_height_l*3/2,
    			pfd_gc.adi_cy - pfd_gc.line_height_l*3/2,
    			pfd_gc.adi_cy + pfd_gc.line_height_l*3/2,
    			pfd_gc.adi_cy + pfd_gc.line_height_l*3/2,
    			pfd_gc.adi_cy + pfd_gc.line_height_xxl*6/9,
    			pfd_gc.adi_cy + pfd_gc.line_height_xxl*6/9,
    	};
    	//Composite oricomp = g2.getComposite();
    	//g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    	g2.setColor(pfd_gc.background_color);
    	g2.fillPolygon(box_x, box_y, 8);
    	//g2.setComposite(oricomp);
    	
    	// TODO : Airbus FCOM 1.31.40 p11 (1) amber bold when deviation from FCU selected altitude or flight level
    	// The altitude window changes from yellow to amber, if the aircraft deviates from the FCU selected altitude or flight level
    	g2.setColor(pfd_gc.pfd_reference_color);
    	g2.drawPolyline(box_x, box_y, 8);

    	Area alt_ind_area = new Area ( new Polygon(box_x, box_y, 8) );
    	g2.clip(alt_ind_area);
        // g2.clipRect(pfd_gc.altitape_left, pfd_gc.adi_cy - pfd_gc.line_height_xxl, pfd_gc.tape_width*2, 2 * pfd_gc.line_height_xxl);

        // TODO : flight phase + MDA MH settings
        if (radio_altitude >= mda) {	
        	g2.setColor(pfd_gc.pfd_alti_color); 
        } else {
        	if (this.avionics.is_qpac() && (this.avionics.qpac_ap_phase() < 3)) {
        		g2.setColor(pfd_gc.pfd_alti_color); 
        	} else {
        		g2.setColor(pfd_gc.pfd_caution_color);
        	}
        }
    	
        if ( alt >= 0.0f ) {

//            int alt_int = alt.intValue();
            int alt_int = (int)alt;
            int alt_20 = alt_int / 20 * 20;
            float alt_frac = (alt - (float)alt_20) / 20.0f;
            int alt_100 = (alt_int / 100) % 10;
            int alt_1k = (alt_int / 1000) % 10;
            int alt_10k = (alt_int / 10000) % 10;
       
            
            int	x10k = pfd_gc.altitape_left;
            int	x1k = x10k + pfd_gc.digit_width_xxl;
            int	x100 = x1k + pfd_gc.digit_width_xxl;
            //int	x20 = x100 + pfd_gc.digit_width_xxl;
            int x20 = altitape_right + pfd_gc.digit_width_l*2/10 ;

             
            int ydelta = Math.round( pfd_gc.line_height_l*alt_frac );

            DecimalFormat decaform = new DecimalFormat("00");
            g2.setFont(pfd_gc.font_l);
            g2.drawString(decaform.format( (alt_20 + 40) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta - pfd_gc.line_height_l*2);
            g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta - pfd_gc.line_height_l);
            g2.drawString(decaform.format( alt_20 % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta);
            if (alt_20 == 0) {
                g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta + pfd_gc.line_height_l);
            } else {
                g2.drawString(decaform.format( (alt_20 - 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 + ydelta + pfd_gc.line_height_l);
            }

            alt_20 %= 100;

            // hundreds (bigger on Airbus)
            g2.setFont(pfd_gc.font_xxl);
            
            if ( alt_20 == 80 ) {
                ydelta = Math.round( pfd_gc.line_height_l*alt_frac );
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 3 + ydelta);
                g2.drawString("" + (alt_100 + 1) % 10, x100, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 3 + ydelta - pfd_gc.line_height_l);
            } else {
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 3);
            }

            // thousands
            g2.setFont(pfd_gc.font_xxl);
            if ( ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta);
                g2.drawString("" + (alt_1k + 1) % 10, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl);
            } else {
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
            }

            // ten-thousands
            if ( ( alt_1k == 9 ) && ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                // already done: ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                if ( alt_10k != 0) {                                   
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta);
                }
                g2.drawString("" + (alt_10k + 1) % 10, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl);
            } else {
                if ( alt_10k != 0) {                    
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
                }
            }

        } else {

            // the same for negative altitudes, except that the vertical positions have to be reversed

//            int alt_int = -alt.intValue();
            int alt_int = - (int)alt;
            int alt_20 = alt_int / 20 * 20;
            float alt_frac = (-alt - (float)alt_20) / 20.0f;
            int alt_100 = (alt_int / 100) % 10;
            int alt_1k = (alt_int / 1000) % 10;
            int alt_10k = (alt_int / 10000) % 10;

          
          
            int	x10k = pfd_gc.altitape_left;
            int	x1k = x10k + pfd_gc.digit_width_xxl;
            int	x100 = x1k + pfd_gc.digit_width_xxl;
            int	x20 = x100 + pfd_gc.digit_width_xxl;
                         
            int ydelta = Math.round( pfd_gc.line_height_l*alt_frac );

            DecimalFormat decaform = new DecimalFormat("00");
            g2.setFont(pfd_gc.font_l);
            g2.drawString(decaform.format( (alt_20 + 40) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta + pfd_gc.line_height_l*2);
            g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta + pfd_gc.line_height_l);
            g2.drawString(decaform.format( alt_20 % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta);
            if (alt_20 == 0) {
                g2.drawString(decaform.format( (alt_20 + 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta - pfd_gc.line_height_l);
            } else {
                g2.drawString(decaform.format( (alt_20 - 20) % 100 ), x20, pfd_gc.adi_cy + pfd_gc.line_height_l/2 - 2 - ydelta - pfd_gc.line_height_l);
            }

            alt_20 %= 100;

            // hundreds (bigger on Airbus)
            g2.setFont(pfd_gc.font_xxl);
            
            if ( alt_20 == 80 ) {
                ydelta = Math.round( pfd_gc.line_height_xl*alt_frac );
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_xl/2 - 3 - ydelta);
                g2.drawString("" + (alt_100 + 1) % 10, x100, pfd_gc.adi_cy + pfd_gc.line_height_xl/2 - 3 - ydelta + pfd_gc.line_height_xl);
            } else {
                g2.drawString("" + alt_100, x100, pfd_gc.adi_cy + pfd_gc.line_height_xl/2 - 3);
            }

            g2.setFont(pfd_gc.font_xxl);
            if ( ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                g2.drawString("" + (alt_1k + 1) % 10, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta + pfd_gc.line_height_xxl);
            } else {
                g2.drawString("" + alt_1k, x1k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
            }

            if ( ( alt_1k == 9 ) && ( alt_100 == 9 ) && ( alt_20 == 80 ) ) {
                // already done: ydelta = Math.round( pfd_gc.line_height_xxl*alt_frac );
                if ( alt_10k == 0) {
                    g2.setColor(pfd_gc.heading_labels_color);
                    g2.drawString("\u25CF", x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                    g2.setColor(pfd_gc.pfd_markings_color);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta);
                }
                g2.drawString("" + (alt_10k + 1) % 10, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 - ydelta + pfd_gc.line_height_xxl);
            } else {
                if ( alt_10k == 0) {
                    g2.drawString(" ", x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
                }
            }
            
            // Mark NEG in white letters
            g2.setColor(pfd_gc.pfd_markings_color);
            g2.setClip(original_clipshape);
            g2.drawString("N", pfd_gc.altitape_left, pfd_gc.adi_cy - pfd_gc.line_height_xxl/2 - 4);
            g2.drawString("E", pfd_gc.altitape_left, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
            g2.drawString("G", pfd_gc.altitape_left, pfd_gc.adi_cy + pfd_gc.line_height_xxl*3/2 - 4);

        }

        g2.setClip(original_clipshape);

    }



}
