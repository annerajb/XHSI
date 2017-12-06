/**
* ILS_A320.java
* 
* ILS CDI, GS and label on the PFD / Airbus A320 Version
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  Nicolas Carel
* Adapted for Airbus by Nicolas Carel
* Reference : A320 FCOM 1.31.40
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
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;

public class ILS_A320 extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;
    
    // used to manage scale and symbols flashing
    private long loc_exceeded_start_time = 0;
    private long gs_exceeded_start_time = 0;
    private long two_dots_exceeded_start_time = 0;
    private boolean two_dots_exceeded = false;
    private boolean loc_exceeded = false;
    private boolean gs_exceeded = false;
    private boolean loc_scale_flashing = false;
    private boolean gs_scale_flashing = false;
    private boolean symbols_flashing = false;


    public ILS_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        loc_exceeded_start_time = 0;
        gs_exceeded_start_time = 0;
        two_dots_exceeded_start_time = 0;
        two_dots_exceeded = false;
        loc_exceeded = false;
        gs_exceeded = false;
        loc_scale_flashing = false;
        gs_scale_flashing = false;
        symbols_flashing = false;
    }

    public void paint(Graphics2D g2) {
        if ( pfd_gc.airbus_style && pfd_gc.powered && XHSIStatus.receiving ) {
            drawILS(g2);
        }
    }

    void drawILSBug(Graphics2D g2, int course) {
    	DecimalFormat degrees_formatter = new DecimalFormat("000");
		// keep the bug in the range -180° to 180°
		float bug = course - this.aircraft.heading();
		int hdg_right  = pfd_gc.hdg_left + pfd_gc.hdg_width;
		int hdg_bug_line = pfd_gc.hdg_top + pfd_gc.hdg_height;
		int hdg_bug_top = pfd_gc.hdg_top + pfd_gc.hdg_height / 4;
		int ils_box_x = pfd_gc.hdg_left - pfd_gc.digit_width_l * 9/8;
		int ils_box_y =  hdg_bug_line - pfd_gc.line_height_l * 9/8;	
		int ils_box_h =  pfd_gc.line_height_l*10/8;	
		int ils_box_w =  pfd_gc.digit_width_l * 25/8 ;  
		int ils_text_x = pfd_gc.hdg_left - pfd_gc.digit_width_l;
		int ils_text_y = hdg_bug_line - pfd_gc.line_height_l*1/8;
		if ( bug >  180.0f ) bug -= 360.0f;
		if ( bug < -180.0f ) bug += 360.0f;
		
        int bug_cx = pfd_gc.adi_cx + Math.round( bug * pfd_gc.hdg_width / 50.0f );
        int d_w = pfd_gc.hdg_height/4; 
        if (bug_cx > hdg_right ) {
			ils_box_x += pfd_gc.hdg_width - pfd_gc.digit_width_l;
			ils_text_x += pfd_gc.hdg_width - pfd_gc.digit_width_l; 
		}

		String str_bug = "" + degrees_formatter.format(course);
		if ((bug_cx < pfd_gc.hdg_left) || (bug_cx > hdg_right ) ) {
			g2.setColor(pfd_gc.background_color);
			g2.fillRect(ils_box_x, ils_box_y, ils_box_w , ils_box_h);
			g2.setColor(pfd_gc.pfd_markings_color);
			g2.drawRect(ils_box_x, ils_box_y, ils_box_w, ils_box_h);
			g2.setFont(pfd_gc.font_l);
			g2.setColor(pfd_gc.pfd_ils_color);
	        g2.drawString(str_bug, ils_text_x, ils_text_y);			
		} else {
			g2.setColor(pfd_gc.pfd_ils_color);
			g2.drawLine(bug_cx, hdg_bug_top, bug_cx, hdg_bug_line);
			g2.drawLine(bug_cx - d_w, hdg_bug_line - d_w, bug_cx + d_w, hdg_bug_line - d_w);
		}

    }

    public void drawILS(Graphics2D g2) {

        //int diamond_w = Math.round(7.0f * pfd_gc.scaling_factor); // half-width
    	int diamond_w = Math.round(9.0f * pfd_gc.scaling_factor); // half-width
        int diamond_h = Math.round(12.5f * pfd_gc.scaling_factor); // half-height
        int dot_r = Math.round(4.0f * pfd_gc.scaling_factor);

        boolean nav_receive = false;
        boolean nav1_receive = false;
        boolean nav2_receive = false;
        float cdi_value = 0.0f;
        float cdi1_value = 0.0f;
        float cdi2_value = 0.0f;
        NavigationRadio nav_radio;
        String nav_id = "";
        String nav1_id = "";
        String nav2_id = "";
        RadioNavigationObject nav_object;
        String nav_type = "";
        String nav1_type = "";
        String nav2_type = "";
        boolean gs_active = false;
        boolean gs1_active = false;
        boolean gs2_active = false;
        float gs_value = 0.0f;
        float gs1_value = 0.0f;
        float gs2_value = 0.0f;
        int obs = 999;
        int obs1 = 999;
        int obs2 = 999;
        int crs = 999999;
        int crs1 = 999999;
        int crs2 = 999999;
        float dme = 999.999f;
        float dme1 = 999.999f;
        float dme2 = 999.999f;
        float freq = 0.0f;
        float freq1 = 0.0f;
        float freq2 = 0.0f;
        
        boolean mismatch = false;

        nav_radio = this.avionics.get_nav_radio(1);
        if ( nav_radio.receiving() ) {
            nav_object = nav_radio.get_radio_nav_object();
            if (nav_object instanceof Localizer) {
                nav1_receive = true;
                nav1_id = nav_radio.get_nav_id();
                cdi1_value = this.avionics.nav1_hdef_dot();
                obs1 = Math.round( Math.round(this.avionics.nav1_obs()) );
                crs1 = Math.round( Math.round(this.avionics.nav1_course()) );
                freq1 = this.avionics.get_radio_freq(1);
                if ( ((Localizer) nav_object).has_gs ) {
                    nav1_type = "ILS 1";
                    gs1_active = this.avionics.nav1_gs_active();
                    gs1_value = this.avionics.nav1_vdef_dot();
                } else {
                    nav1_type = "LOC 1";
                }
//                if ( ((Localizer) nav_object).has_dme ) {
                    dme1 = nav_radio.get_distance();
//                }
            }
        }

        nav_radio = this.avionics.get_nav_radio(2);
        if ( nav_radio.receiving() ) {
            nav_object = nav_radio.get_radio_nav_object();
            if (nav_object instanceof Localizer) {
                nav2_receive = true;
                nav2_id = nav_radio.get_nav_id();
                cdi2_value = this.avionics.nav2_hdef_dot();
                obs2 = Math.round( Math.round(this.avionics.nav2_obs()) );
                crs2 = Math.round( Math.round(this.avionics.nav2_course()) );
                freq2 = this.avionics.get_radio_freq(2);
                if ( ((Localizer) nav_object).has_gs ) {
                    nav2_type = "ILS 2";
                    gs2_active = this.avionics.nav2_gs_active();
                    gs2_value = this.avionics.nav2_vdef_dot();
                } else {
                    nav2_type = "LOC 2";
                }
//                if ( ((Localizer) nav_object).has_dme ) {
                    dme2 = nav_radio.get_distance();
//                }
            }
        }

        int source = this.avionics.hsi_source();

        if (source == Avionics.HSI_SOURCE_NAV1) {
            if ( nav1_receive ) {
                nav_receive = true;
                nav_id = nav1_id;
                cdi_value = cdi1_value;
                obs = obs1;
                crs = crs1;
                nav_type = nav1_type;
                gs_active = gs1_active;
                gs_value = gs1_value;
                dme = dme1;
                freq = freq1;
            } else if ( nav2_receive ) {
                mismatch = true;
                nav_type = nav2_type;
            }
        } else if (source == Avionics.HSI_SOURCE_NAV2) {
            if ( nav2_receive ) {
                nav_receive = true;
                nav_id = nav2_id;
                cdi_value = cdi2_value;
                obs = obs2;
                crs = crs2;
                nav_type = nav2_type;
                gs_active = gs2_active;
                gs_value = gs2_value;
                dme = dme2;
                freq = freq2;
            } else if ( nav1_receive ) {
                mismatch = true;
                nav_type = nav1_type;
            }
        } else /* if (source == Avionics.HSI_SOURCE_GPS) */ {
            if ( nav1_receive ) {
                mismatch = true;
                nav_type = nav1_type;
            } else if ( nav2_receive ) {
                mismatch = true;
                nav_type = nav2_type;
            }
        }

        if (this.avionics.qpac_version() > 110) {
        	// The first QPAC AirbusFBW 1.1 relies on X-Plane NAV1 for ILS
        	nav_receive = this.avionics.qpac_loc_on();
        	gs_active = this.avionics.qpac_gs_on();
        	gs_value = this.avionics.qpac_gs_val();
        	cdi_value = this.avionics.qpac_loc_val();
        	obs = Math.round(this.avionics.qpac_ils_crs());
        	crs = Math.round(this.avionics.qpac_ils_crs()); 
        	dme = this.avionics.qpac_ils_dme();
        	nav_id = this.avionics.qpac_ils_id();
        	nav_type = "ILS 3";
        	freq=this.avionics.qpac_ils_freq();
        }

//mismatch = false;
//nav_receive = true;
//nav_type = "ILS 0";
//cdi_value = 2.45f;
//crs = 271;
//obs = 269;

        // we are receiving a LOC or ILS, but not on the selected source
        if ( mismatch ) {

            g2.setFont(pfd_gc.font_l);
            int ref_h = pfd_gc.line_height_l;
            int ref_x = pfd_gc.adi_cx - pfd_gc.adi_size_left*7/8;
            int ref_y = pfd_gc.adi_cy - pfd_gc.adi_size_up - 1*ref_h;
            g2.setColor(pfd_gc.caution_color);
            g2.drawString(nav_type, ref_x, ref_y);

        }
        
        // TODO : FLS mode qpac_npa_no_points = 2 and qpac_npa_valid = 1
        boolean display_fls = false;
        if (this.avionics.qpac_npa_valid() >= 1 && this.avionics.qpac_npa_no_points() == 2) display_fls = true;
        // FLS line 1 : runway ID "RWY xx" (sim/cockpit/radios/gps_course_degtm)
        // FLS line 2 : Slope (this.avionics.qpac_npa_slope())
        // FLS line 3 : distance (sim/cockpit2/radios/indicators/gps_dme_distance_nm)
        
        // qpac_appr_type values : 0 = DH et ILS, 1 = MDA et GPS, 2 = BARO DH, 3 = RNAV
        
        // Scales and symbols flashing management
        // on non QPAC aircraft, we assume that flashing should be active below 1000ft RA
        // for QPAC aircraft, flashing is active on lateral navigation "LOC" and vertical navigation "G/S" above 1000 ft        
        int ra = Math.round(this.aircraft.agl_m() * 3.28084f); // Radio altitude
        boolean on_loc=false; 
        // Test lateral navigation mode
        if (this.avionics.is_qpac()) { 
        	on_loc = this.avionics.qpac_ap_lateral_mode() == 7;
        } else if (this.avionics.is_jar_a320neo()) {
        	on_loc = this.avionics.jar_a320neo_ap_lateral_mode() == 7;
        } else {        
        	on_loc = ra < 1000; 
        }
        // Do not flash GS if vertical mode not on GS
        boolean on_gs=false;
        if (this.avionics.is_qpac()) { 
    		on_gs = this.avionics.qpac_ap_vertical_mode() == 7;
        } else if (this.avionics.is_jar_a320neo()) {
        	on_gs = this.avionics.jar_a320neo_ap_vertical_mode() == 12;
        }
        	
        if ((Math.abs(cdi_value) > 2.0f || Math.abs(gs_value) > 2.0f) && (! two_dots_exceeded ) && on_gs && (ra>15)) {
        	two_dots_exceeded = true;
        	two_dots_exceeded_start_time = pfd_gc.current_time_millis;        	
        } 
        if ((Math.abs(cdi_value) < 2.0f && Math.abs(gs_value) < 2.0f) || (!on_gs) || (ra <= 15)) {
        	two_dots_exceeded = false;
        	symbols_flashing = false;
        }
        
        if ( (! loc_exceeded ) && on_loc && ((Math.abs(cdi_value) > 0.25f) && (ra > 15))) {
        	loc_exceeded = true;
        	loc_exceeded_start_time = pfd_gc.current_time_millis;
        } 
        if ((Math.abs(cdi_value) < 0.25f) || (!on_loc) || (ra <= 15)) {
        	loc_exceeded = false;
        	loc_scale_flashing = false; 
        }
        if (loc_exceeded && (pfd_gc.current_time_millis  > loc_exceeded_start_time + 2000 ) ) {        	
            loc_scale_flashing = true;          
        }  

        if ((! gs_exceeded ) && on_gs && ((Math.abs(gs_value) > 1.0f) && (ra > 100)) ) {
        	gs_exceeded = true;
        	gs_exceeded_start_time = pfd_gc.current_time_millis;
        } 
        if ((Math.abs(gs_value) < 1.0f) || (!on_gs) || (ra <= 15)) {
        	gs_exceeded = false;
        	gs_scale_flashing = false;
        }
        if (gs_exceeded && (pfd_gc.current_time_millis  > (gs_exceeded_start_time + 2000) )) {        	
            gs_scale_flashing = true;          
        }  
        
        if (two_dots_exceeded && (pfd_gc.current_time_millis > (two_dots_exceeded_start_time + 2000) )) {
        	symbols_flashing = true;
            loc_scale_flashing = true;
            gs_scale_flashing = true;
        } 
        
      
        
        // boolean display_loc_scale = (!loc_scale_flashing) || ((pfd_gc.current_time_millis % 1000) > 500);
        // boolean display_gs_scale = (!gs_scale_flashing) || ((pfd_gc.current_time_millis % 1000) > 500);
        boolean display_loc_scale = true;
        if (loc_scale_flashing && ((pfd_gc.current_time_millis % 1000) < 500) ) display_loc_scale = false;
        boolean display_gs_scale = true;
        if (gs_scale_flashing && ((pfd_gc.current_time_millis % 1000) < 500) ) display_gs_scale = false;
        boolean display_symbols = true;
        if (symbols_flashing && ((pfd_gc.current_time_millis % 1000) < 500) ) display_symbols = false;
        
        boolean display_vdev = false;
        boolean display_ldev = false;
        // conditions to display V/DEV : NPA Valid + APPR illuminated + ILS pushbutton OFF
        // ILS has always priority over V/DEV
        if (this.avionics.qpac_npa_valid() == 1 && !this.avionics.qpac_ils_on() && this.avionics.qpac_appr_illuminated() ) display_vdev = true;
        
        // ILS scales (LOC & G/S)
        // LOC scale
        // TODO: the LOC scale flashes when deviation exceeds 1/4 for 2 secs (above 15 feet RA). 
        // TODO : LOC and glide scale flashes when deviation exceeds one dot for 2 secs. 
        if ((nav_receive || this.avionics.qpac_ils_on() || display_ldev ) && display_loc_scale ) {
        	int dot_dist = pfd_gc.cdi_width*4/22;
        	int cdi_x = pfd_gc.adi_cx;
        	int cdi_y = pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height;
        	g2.setColor(pfd_gc.pfd_reference_color);
        	Stroke original_stroke = g2.getStroke();
        	g2.setStroke(new BasicStroke(4.0f));
        	g2.drawLine(pfd_gc.adi_cx, pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height/2,
        			pfd_gc.adi_cx, pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height*3/2);
        	g2.setColor(pfd_gc.pfd_markings_color);          	  
        	g2.setStroke(original_stroke);
        	if (display_ldev) {
        		g2.drawLine(cdi_x - dot_dist, cdi_y - dot_r, cdi_x - dot_dist, cdi_y + dot_r);
        		g2.drawLine(cdi_x + dot_dist, cdi_y - dot_r, cdi_x + dot_dist, cdi_y + dot_r);
        		g2.drawLine(cdi_x - 2*dot_dist, cdi_y - dot_r, cdi_x - 2*dot_dist, cdi_y + dot_r);
        		g2.drawLine(cdi_x + 2*dot_dist, cdi_y - dot_r, cdi_x + 2*dot_dist, cdi_y + dot_r);
        		g2.setColor(pfd_gc.pfd_managed_color);
        		g2.setFont(pfd_gc.font_l);
        		g2.drawString("L/DEV", cdi_x - dot_dist * 3, cdi_y);
        		
        	} else {
        		g2.drawOval(cdi_x - dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
        		g2.drawOval(cdi_x + dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
        		g2.drawOval(cdi_x - 2*dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
        		g2.drawOval(cdi_x + 2*dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
        	}
        }
        // GS Scale
        if ( ((nav_receive && gs_active) || this.avionics.qpac_ils_on() || display_vdev ) && display_gs_scale) {
            int dot_dist = pfd_gc.gs_height*4/21;
            int gs_x = pfd_gc.adi_cx + pfd_gc.adi_size_right + pfd_gc.gs_width/2;
            int gs_y = pfd_gc.adi_cy;
            
            g2.setColor(pfd_gc.pfd_markings_color);            
            // g2.drawLine(pfd_gc.adi_cx + pfd_gc.adi_size_right + 1, gs_y, pfd_gc.adi_cx + pfd_gc.adi_size_right + pfd_gc.gs_width - 1, gs_y);
        	if (display_vdev) {
        		g2.drawLine(gs_x - dot_r, gs_y - dot_dist, gs_x + dot_r, gs_y - dot_dist);
        		g2.drawLine(gs_x - dot_r, gs_y + dot_dist, gs_x + dot_r, gs_y + dot_dist);
        		g2.drawLine(gs_x - dot_r, gs_y - 2*dot_dist, gs_x + dot_r, gs_y - 2*dot_dist);
        		g2.drawLine(gs_x - dot_r, gs_y + 2*dot_dist, gs_x + dot_r, gs_y + 2*dot_dist);
        		g2.setColor(pfd_gc.pfd_managed_color);
        		g2.setFont(pfd_gc.font_l);
        		g2.drawString("V/DEV", gs_x - 5 * pfd_gc.digit_width_l , gs_y - dot_dist*9/4);       		
        	} else {
        		g2.drawOval(gs_x - dot_r, gs_y - dot_dist - dot_r, 2*dot_r, 2*dot_r);
        		g2.drawOval(gs_x - dot_r, gs_y + dot_dist - dot_r, 2*dot_r, 2*dot_r);
        		g2.drawOval(gs_x - dot_r, gs_y - 2*dot_dist - dot_r, 2*dot_r, 2*dot_r);
        		g2.drawOval(gs_x - dot_r, gs_y + 2*dot_dist - dot_r, 2*dot_r, 2*dot_r);
        	}
        }
        
        
        // Localizer
        if ( nav_receive ) {

            // Approach reference label
            DecimalFormat degrees_formatter = new DecimalFormat("000");
            DecimalFormat dme_formatter = new DecimalFormat("0.0");
            DecimalFormatSymbols format_symbols = dme_formatter.getDecimalFormatSymbols();
            format_symbols.setDecimalSeparator('.');
            dme_formatter.setDecimalFormatSymbols(format_symbols);

            g2.setFont(pfd_gc.font_l);
            int ref_h_m = pfd_gc.line_height_m;
            int ref_h_l = pfd_gc.line_height_l;
            int ref_x = pfd_gc.speedtape_left;
            int ref_y = pfd_gc.adi_cy + pfd_gc.instrument_size * 415/1000;
            
            // LOC id
            g2.setColor(pfd_gc.pfd_ils_color);
            g2.drawString(nav_id, ref_x, ref_y);
            
            // LOC Course and bug
            int ref_x1 = ref_x + pfd_gc.get_text_width(g2, pfd_gc.font_l, nav_id) + pfd_gc.digit_width_l;
            //String crs_str = degrees_formatter.format( obs ) + "\u00B0  ";
            //g2.drawString(crs_str, ref_x1, ref_y);           
            drawILSBug(g2, obs);
            
            // Not for A320, but usefull for other A/C
            if ( crs != obs ) {
                g2.setColor(pfd_gc.pfd_caution_color);
                //int ref_x2 = ref_x1 + pfd_gc.get_text_width(g2, pfd_gc.font_m, crs_str);
                int ref_x2 = ref_x1;
                g2.setFont(pfd_gc.font_s);
                g2.drawString("F/C", ref_x2, ref_y);
                int ref_x3 = ref_x2 + pfd_gc.get_text_width(g2, pfd_gc.font_m, "F/C");
                g2.setFont(pfd_gc.font_m);
                g2.drawString(degrees_formatter.format( crs ) + "\u00B0", ref_x3, ref_y);
                g2.setColor(pfd_gc.pfd_ils_color);
            }
            
            // Type or frequency
            ref_y += ref_h_l;
            g2.setFont(pfd_gc.font_l);
            g2.setColor(pfd_gc.pfd_ils_color);
            // g2.drawString(nav_type, ref_x, ref_y);
            g2.drawString("" + (int)(freq/100) , ref_x, ref_y);
            g2.setFont(pfd_gc.font_s);
            g2.drawString("." + Math.round(freq % 100), ref_x+pfd_gc.digit_width_l*3, ref_y );
            
            // DME
            ref_y += ref_h_l;
            String dme_str = "-.-";
            if ( ( dme == 0.0f ) || ( dme >= 99.0f ) ) {
                g2.drawString(dme_str, ref_x, ref_y);
            } else {
            	g2.setFont(pfd_gc.font_l);
            	dme_str = "" + (int) dme;
                g2.drawString(dme_str, ref_x, ref_y);
                g2.setFont(pfd_gc.font_s);
                g2.drawString("." + (int) ((dme*10)%10), ref_x + pfd_gc.get_text_width(g2, pfd_gc.font_l, dme_str), ref_y);
                g2.setColor(pfd_gc.pfd_selected_color);
                g2.drawString(" NM", ref_x + pfd_gc.get_text_width(g2, pfd_gc.font_l, dme_str) + 2*pfd_gc.digit_width_l, ref_y);                              
            }


            // CDI
            int dot_dist = pfd_gc.cdi_width*4/22;
            int cdi_pixels = Math.round(cdi_value * (float)dot_dist);
            float cdi_dev_max = 2.2f;
            int cdi_pixels_max = Math.round(cdi_dev_max * (float)dot_dist  * Math.signum(cdi_value));

            int cdi_x = pfd_gc.adi_cx;
            int cdi_y = pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height;

            if ( this.preferences.get_draw_colorgradient_horizon() ) {
                pfd_gc.setTransparent(g2, true);
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillRect(pfd_gc.adi_cx - pfd_gc.cdi_width/2, pfd_gc.adi_cy + pfd_gc.adi_size_down, pfd_gc.cdi_width, pfd_gc.cdi_height);
                pfd_gc.setOpaque(g2);
            }

            int diamond_x[] = { cdi_x + cdi_pixels, cdi_x + cdi_pixels + diamond_h, cdi_x + cdi_pixels, cdi_x + cdi_pixels - diamond_h };
            int diamond_y[] = { cdi_y - diamond_w, cdi_y, cdi_y + diamond_w, cdi_y };
            int r_diamond_x[] = { cdi_x + cdi_pixels_max + diamond_h, cdi_x + cdi_pixels_max, cdi_x + cdi_pixels_max + diamond_h  };
            int l_diamond_x[] = { cdi_x + cdi_pixels_max - diamond_h, cdi_x + cdi_pixels_max, cdi_x + cdi_pixels_max - diamond_h };    
            
            if (display_symbols) {
            	g2.setColor(pfd_gc.pfd_ils_color);            	
            	if (display_vdev) {
            		g2.drawRect(cdi_x + cdi_pixels, cdi_y-diamond_h, diamond_w, diamond_h*2);
            	} else {
            		if (cdi_value < -cdi_dev_max) {
            			g2.drawPolyline(r_diamond_x, diamond_y, 3);                
            		} else if (cdi_value > cdi_dev_max) {
            			g2.drawPolyline(l_diamond_x, diamond_y, 3);
            		} else {
            			g2.drawPolygon(diamond_x, diamond_y, 4);
            		}
            	}
            }

            /*
            g2.setColor(pfd_gc.pfd_reference_color);
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(4.0f));
            g2.drawLine(pfd_gc.adi_cx, pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height/2,
            		 pfd_gc.adi_cx, pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height*3/2);
            g2.setColor(pfd_gc.pfd_markings_color);
            g2.setStroke(original_stroke);
            g2.drawOval(cdi_x - dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(cdi_x + dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(cdi_x - 2*dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(cdi_x + 2*dot_dist - dot_r, cdi_y - dot_r, 2*dot_r, 2*dot_r);
            */

        }


        // Glideslope
        if ( nav_receive && gs_active ) {

            int dot_dist = pfd_gc.gs_height*4/21;
            int gs_pixels = Math.round(gs_value * (float)dot_dist);
            float gs_dev_max = 2.05f;
            int gs_pixels_max = Math.round(gs_dev_max * (float)dot_dist  * Math.signum(gs_value));

            int gs_x = pfd_gc.adi_cx + pfd_gc.adi_size_right + pfd_gc.gs_width/2;
            int gs_y = pfd_gc.adi_cy;

            if ( this.preferences.get_draw_colorgradient_horizon() ) {
                pfd_gc.setTransparent(g2, true);
                g2.setColor(pfd_gc.instrument_background_color);
                g2.fillRect(pfd_gc.adi_cx + pfd_gc.adi_size_right, pfd_gc.adi_cy - pfd_gc.gs_height/2, pfd_gc.gs_width, pfd_gc.gs_height);
                pfd_gc.setOpaque(g2);
            }

            int diamond_x[] = { gs_x - diamond_w, gs_x, gs_x + diamond_w, gs_x };
            int diamond_y[] = { gs_y + gs_pixels, gs_y + gs_pixels + diamond_h, gs_y + gs_pixels, gs_y + gs_pixels - diamond_h };
            int t_diamond_y[] = { gs_y + gs_pixels_max, gs_y + gs_pixels_max + diamond_h, gs_y + gs_pixels_max };
            int b_diamond_y[] = { gs_y + gs_pixels_max, gs_y + gs_pixels_max - diamond_h, gs_y + gs_pixels_max };

            if (display_symbols) {
            	g2.setColor(pfd_gc.pfd_ils_color);
            	if (display_vdev) {
            		g2.drawRect(gs_x - diamond_w, gs_y + gs_pixels, diamond_w*2, diamond_h);
            	} else {
            		if (gs_value < -gs_dev_max) {
            			g2.drawPolyline(diamond_x, b_diamond_y, 3);                
            		} else if (gs_value > gs_dev_max) {
            			g2.drawPolyline(diamond_x, t_diamond_y, 3);
            		} else {
            			g2.drawPolygon(diamond_x, diamond_y, 4);
            		}
            	}
            }

            /*
            g2.setColor(pfd_gc.pfd_markings_color);
            // g2.drawLine(pfd_gc.adi_cx + pfd_gc.adi_size_right + 1, gs_y, pfd_gc.adi_cx + pfd_gc.adi_size_right + pfd_gc.gs_width - 1, gs_y);
            g2.drawOval(gs_x - dot_r, gs_y - dot_dist - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(gs_x - dot_r, gs_y + dot_dist - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(gs_x - dot_r, gs_y - 2*dot_dist - dot_r, 2*dot_r, 2*dot_r);
            g2.drawOval(gs_x - dot_r, gs_y + 2*dot_dist - dot_r, 2*dot_r, 2*dot_r);
            */

        }

        // ILS Amber flashing warning (QPAC only) when APPR mode armed without ILS
        // V/DEV Amber flashing warning (QPAC only) when APPR mode armed in NPA Approach and ILS displayed
        if (this.avionics.is_qpac()) {
        	if ( 
        			(this.avionics.qpac_ap_vertical_armed() == 9 || 
        			 this.avionics.qpac_ap_vertical_armed() == 7 || 
        			 this.avionics.qpac_ap_vertical_armed() == 5 || 
        			 this.avionics.qpac_ap_vertical_armed() == 1 ||
        			 this.avionics.qpac_ap_vertical_mode() == 6  ||
        			 this.avionics.qpac_ap_vertical_mode() == 7     ) ) {
        		if (pfd_gc.current_time_millis % 1000 < 500) {    			
        		    int ils_wrn_x = pfd_gc.adi_cx + pfd_gc.cdi_width*9/22; ;
                	int ils_wrn_y = pfd_gc.adi_cy + pfd_gc.adi_size_down + pfd_gc.cdi_height + pfd_gc.line_height_l*3/8;
        			g2.setColor(pfd_gc.pfd_caution_color);
        			g2.setFont(pfd_gc.font_l);
        			if (this.avionics.qpac_npa_valid()==0 && !this.avionics.qpac_ils_on() ) 
        				g2.drawString("ILS",ils_wrn_x,ils_wrn_y);
        			else if (this.avionics.qpac_npa_valid()==1 && this.avionics.qpac_ils_on() )
        				g2.drawString("V/DEV",ils_wrn_x,ils_wrn_y);
        		}
        	}
        }
        

    }


}