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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
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
    		if ( ! XHSIStatus.receiving ) {
    			// FCOM 1.31.40 p26 (10) 
    			// if the altitude information fails, the ALT flag (red) replaces the altitude scale
    			drawFailedTape(g2);
    		} else if ( pfd_gc.powered ) {
    			drawTape(g2);
    		}
    	}
    }

    private void drawFailedTape(Graphics2D g2) {
        // Global style
        
        int altitape_right = pfd_gc.altitape_left + pfd_gc.digit_width_xxl*14/5;
        g2.setColor(pfd_gc.warning_color);
        g2.drawLine(altitape_right, pfd_gc.tape_top ,altitape_right, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top ,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top  );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top + pfd_gc.tape_height + 1,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
    	int[] box_x = {
    			pfd_gc.altitape_left - pfd_gc.tape_width*3/50,
    			altitape_right,
    			altitape_right,
    			pfd_gc.altitape_left + pfd_gc.digit_width_xxl*3 + pfd_gc.digit_width_l * 9/4,
    			pfd_gc.altitape_left + pfd_gc.digit_width_xxl*3 + pfd_gc.digit_width_l * 9/4,
    			altitape_right,
    			altitape_right,
    			pfd_gc.altitape_left - pfd_gc.tape_width*3/50,
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
    	g2.setColor(pfd_gc.background_color);
    	g2.fillPolygon(box_x, box_y, 8);
        g2.setColor(pfd_gc.warning_color);    	
    	g2.drawPolyline(box_x, box_y, 8);
    	String failed_str = "ALT";
        g2.setFont(pfd_gc.font_xxl);
    	g2.drawString( failed_str, pfd_gc.altitape_left,  pfd_gc.adi_cy + pfd_gc.line_height_l/2 );
    }
    
    private void drawTape(Graphics2D g2) {
        // Global style
        boolean pfd_airbus = this.preferences.get_pfd_style_airbus();
        int altitape_right = pfd_gc.altitape_left + pfd_gc.digit_width_xxl*14/5;
        
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.altitape_left - 1, pfd_gc.tape_top - 1, altitape_right - pfd_gc.altitape_left + 1, pfd_gc.tape_height + 2);	
        g2.setColor(pfd_gc.markings_color);
        g2.drawLine(altitape_right, pfd_gc.tape_top ,altitape_right, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top ,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top  );
        g2.drawLine(pfd_gc.altitape_left, pfd_gc.tape_top + pfd_gc.tape_height + 1,pfd_gc.altitape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top + pfd_gc.tape_height + 1 );            
        pfd_gc.setOpaque(g2);

        Shape original_clipshape = g2.getClip();
        // left and right don't matter...
        g2.clipRect(pfd_gc.altitape_left - pfd_gc.tape_width, pfd_gc.tape_top, pfd_gc.tape_width*3, pfd_gc.tape_height);

        g2.setColor(pfd_gc.markings_color);
        g2.setFont(pfd_gc.font_xxl);

      
        // Altitude scale
        float alt = this.aircraft.altitude_ind();
        int alt_range = pfd_airbus ? 500 : 400;
        int alt_modulo = pfd_airbus ? 500 : 200;
        float alt_f_range = pfd_airbus ? 1100.0f : 800.0f;
        
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
        if ( loc_receive ) {
            int loc_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt - alt) * pfd_gc.tape_height / alt_f_range );
            int h1000_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt + (float)1000 - alt) * pfd_gc.tape_height / alt_f_range );
            int h500_y = pfd_gc.adi_cy - Math.round( ((float)dest_alt + (float)500 - alt) * pfd_gc.tape_height / alt_f_range );
            // between 500 and 1000ft
            g2.setColor(pfd_gc.markings_color);
            g2.drawLine(pfd_gc.altitape_left - 4, h1000_y, pfd_gc.altitape_left - 4, h500_y);
            // between 500 and 1000ft
            g2.setColor(pfd_gc.caution_color);
            g2.drawLine(pfd_gc.altitape_left - 4, h500_y, pfd_gc.altitape_left - 4, loc_y);
            // localizer altitude
            gnd_y = loc_y;
        } else {
            // ground elevation from the radar altitude
            gnd_y = pfd_gc.adi_cy + Math.round( (this.aircraft.agl_m() * 3.28084f) * pfd_gc.tape_height / alt_f_range );
        }
        
        // Ground reference        
        // TODO : ??? on Airbus, flight phase 7 and 8 with QNH mode, display DH zone in amber
        // TODO : Airbus FCOM 1.31.40 p.13 (3) Ground reference - display below 570 ft
        if ( (this.aircraft.agl_m() * 3.28084f) < 570f ) { 
            // Ground bar on Airbus
        	g2.setColor(pfd_gc.warning_color); 
        	g2.fillRect(altitape_right+1, gnd_y, pfd_gc.tape_width / 7 , gnd_y - pfd_gc.tape_top + 2);
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
           	g2.drawLine(altitape_right, alt_y, altitape_right - pfd_gc.tape_width*1/8, alt_y);
 
            if (alt_mark % alt_modulo == 0) {
            	g2.setFont(pfd_gc.font_l);
            	DecimalFormat markform = new DecimalFormat("000");

            	String mark_str = markform.format( Math.abs(alt_mark / 100));
            	g2.drawString(mark_str, pfd_gc.altitape_left, alt_y + pfd_gc.line_height_m/2 - 2);

            	// Little triangle before mark
            	g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*1/16, alt_y, pfd_gc.altitape_left - pfd_gc.tape_width*3/20, alt_y + pfd_gc.tape_width*2/22);
            	g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*1/16, alt_y, pfd_gc.altitape_left - pfd_gc.tape_width*3/20, alt_y - pfd_gc.tape_width*2/22);
            }
        }

        
        // DA arrow
        // Code for Boeing, not modified for Airbus -- let's see
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


        // AP Alt bug
        int alt_y = pfd_gc.adi_cy - Math.round( (this.avionics.autopilot_altitude() - alt) * pfd_gc.tape_height / alt_f_range );
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
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/16,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/16,
        		pfd_gc.altitape_left + pfd_gc.tape_width*4/16,
        		pfd_gc.altitape_left + pfd_gc.tape_width*4/16,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/16,
        		pfd_gc.altitape_left - pfd_gc.tape_width*3/16
        };
        int[] bug_y = {
        		alt_y,
        		alt_y + pfd_gc.tape_width*2/20,
        		alt_y + pfd_gc.line_height_l * 2,
        		alt_y + pfd_gc.line_height_l * 2,
        		alt_y - pfd_gc.line_height_l * 2,
        		alt_y - pfd_gc.line_height_l * 2,
        		alt_y - pfd_gc.tape_width*2/20
        };
        g2.setColor(pfd_gc.heading_bug_color);
        if (! hide_bug ) { g2.drawPolygon(bug_x, bug_y, 7); }
        g2.setColor(pfd_gc.pfd_reference_color);
        g2.drawLine(pfd_gc.altitape_left - pfd_gc.tape_width*9/16, pfd_gc.tape_top + pfd_gc.tape_height / 2 , pfd_gc.altitape_left - pfd_gc.tape_width*3/16,  pfd_gc.tape_top + pfd_gc.tape_height / 2);


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

        g2.setClip(original_clipshape);

        // QNH setting
        // TODO : On Airbus QNH flashes when transition altitude is missed
        // TODO : On Airbus Capt or F/O can select display in Inches or HPa     
        int qnh = this.aircraft.qnh();
        boolean qnh_display = true;
        boolean qnh_is_hpa = true;
        float alt_inhg = this.aircraft.altimeter_in_hg();
        boolean std = ( Math.round(alt_inhg * 100.0f) == 2992 );
        if (this.avionics.is_qpac()) { 
        	std = this.avionics.qpac_baro_std_capt(); 
        	qnh_display = this.avionics.qpac_baro_hide_capt();
        	qnh_is_hpa = this.avionics.qpac_baro_unit_capt();
        }
        String qnh_str;
        if ( std ) {
            qnh_str = "STD";
        } else {
            qnh_str = "" + qnh;
        }
        
        if (qnh_display) {
        	g2.setColor(pfd_gc.fmc_disp_color);
        	g2.setFont(pfd_gc.font_l);
        	if ( ! std ) {
        		g2.drawString("QNH", pfd_gc.altitape_left, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_l*25/8);
        		g2.setColor(pfd_gc.color_boeingcyan);
        		g2.drawString(qnh_str, pfd_gc.altitape_left + 4*pfd_gc.digit_width_l, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_l*25/8);

        	} else {
        		g2.setColor(pfd_gc.color_boeingcyan);
        		g2.drawString("STD", pfd_gc.altitape_left + pfd_gc.digit_width_l*18/8, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_l*25/8);
        		g2.setColor(pfd_gc.fmc_ll_active_color);
        		g2.drawRect(pfd_gc.altitape_left + 2*pfd_gc.digit_width_l, pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_l*17/8, pfd_gc.digit_width_l*29/8, pfd_gc.line_height_l*10/8);
        	}
        }

        

        // AP ALT preselect
        // On Airbus ALT preselect depends on QNH setting
        // FL xxx if STD QNH
        // Position on top if alt preselect setting above alt
        // on bottom if alt preselect setting below alt
    	int ap_alt = Math.round(this.avionics.autopilot_altitude());
    	g2.setColor(pfd_gc.heading_bug_color);
    	if (this.avionics.is_qpac()) {
    		if (this.avionics.qpac_alt_is_cstr()) {
    	    	g2.setColor(pfd_gc.pfd_managed_color);
    	    	ap_alt = this.avionics.qpac_constraint_alt();
    		}
    	}
    	g2.setFont(pfd_gc.font_l);
    	
    	// Airbus
    	String alt_str = "FL ";
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
    		alt_str_x =  pfd_gc.altitape_left + pfd_gc.tape_width - pfd_gc.tape_width*1/16 - pfd_gc.get_text_width(g2, pfd_gc.font_l, alt_str);

    	}
    	int alt_str_w = pfd_gc.get_text_width(g2, pfd_gc.font_l, alt_str);
    	g2.clearRect(pfd_gc.altitape_left-1, alt_str_y - pfd_gc.line_height_l*9/10, alt_str_w + pfd_gc.digit_width_l*2/3, pfd_gc.line_height_l);
    	g2.drawString(alt_str, alt_str_x, alt_str_y);       	


    	// Altitude Indication
    	// Airbus FCOM 1.31.40 p11 (1)
    	// 
    	int[] box_x = {
    			pfd_gc.altitape_left - pfd_gc.tape_width*3/50,
    			altitape_right,
    			altitape_right,
    			pfd_gc.altitape_left + pfd_gc.digit_width_xxl*3 + pfd_gc.digit_width_l * 9/4,
    			pfd_gc.altitape_left + pfd_gc.digit_width_xxl*3 + pfd_gc.digit_width_l * 9/4,
    			altitape_right,
    			altitape_right,
    			pfd_gc.altitape_left - pfd_gc.tape_width*3/50,
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
    	g2.setColor(pfd_gc.fmc_ll_active_color);
    	g2.drawPolyline(box_x, box_y, 8);

    	Area alt_ind_area = new Area ( new Polygon(box_x, box_y, 8) );
    	g2.clip(alt_ind_area);
        // g2.clipRect(pfd_gc.altitape_left, pfd_gc.adi_cy - pfd_gc.line_height_xxl, pfd_gc.tape_width*2, 2 * pfd_gc.line_height_xxl);

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
            int	x20 = x100 + pfd_gc.digit_width_xxl;
            if (alt >= 400f) {	
            	g2.setColor(pfd_gc.pfd_alti_color); 
            } else {
            	g2.setColor(pfd_gc.pfd_caution_color);
            }
             
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
            if (pfd_airbus) { 
            	g2.setFont(pfd_gc.font_xxl);
            } else {
            	g2.setFont(pfd_gc.font_l);
            }
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
                if ( alt_10k == 0) {
                    g2.setColor(pfd_gc.heading_labels_color.darker());
                    if (! pfd_airbus) { 
                    	g2.fillRoundRect(x10k + pfd_gc.digit_width_xxl/8, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - pfd_gc.line_height_xxl*3/4 - 4 + ydelta, pfd_gc.digit_width_xxl*3/4, pfd_gc.line_height_xxl*3/4, (int)(8.0f*pfd_gc.scaling_factor), (int)(8.0f*pfd_gc.scaling_factor)); 
                    }
                    g2.setColor(pfd_gc.pfd_alti_color);
                } else {
                    g2.drawString("" + alt_10k, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta);
                }
                g2.drawString("" + (alt_10k + 1) % 10, x10k, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4 + ydelta - pfd_gc.line_height_xxl);
            } else {
                if ( alt_10k == 0) {
                    g2.setColor(pfd_gc.heading_labels_color.darker());
                    if (! pfd_airbus ) { g2.fillRoundRect(x10k + pfd_gc.digit_width_xxl/8, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - pfd_gc.line_height_xxl*3/4 - 4, pfd_gc.digit_width_xxl*3/4, pfd_gc.line_height_xxl*3/4, (int)(8.0f*pfd_gc.scaling_factor), (int)(8.0f*pfd_gc.scaling_factor));
                    }                    
                    g2.setColor(pfd_gc.markings_color);
                } else {
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
            g2.setColor(Color.GREEN);
             
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
                    g2.setColor(pfd_gc.markings_color);
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
            g2.setColor(pfd_gc.markings_color);
            g2.setClip(original_clipshape);
            g2.drawString("N", pfd_gc.altitape_left, pfd_gc.adi_cy - pfd_gc.line_height_xxl/2 - 4);
            g2.drawString("E", pfd_gc.altitape_left, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
            g2.drawString("G", pfd_gc.altitape_left, pfd_gc.adi_cy + pfd_gc.line_height_xxl*3/2 - 4);

        }

        g2.setClip(original_clipshape);

    }



}
