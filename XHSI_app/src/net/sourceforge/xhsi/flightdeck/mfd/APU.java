/**
* LowerEicas.java
* 
* Lower EICAS
* 
* Copyright (C) 2015  Nicolas Carel
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


package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Aircraft.ValveStatus;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class APU extends MFDSubcomponent {
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Stroke original_stroke;
    
    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;
    
    private int prim_dial_x[] = new int[8];    

    // This Stroke is used by the double lined value boxes
	private class CompositeStroke implements Stroke {
		private Stroke stroke1, stroke2;

		public CompositeStroke( Stroke stroke1, Stroke stroke2 ) {
			this.stroke1 = stroke1;
			this.stroke2 = stroke2;
		}

		public Shape createStrokedShape( Shape shape ) {
			return stroke2.createStrokedShape( stroke1.createStrokedShape( shape ) );
		}
	}
    
    public APU(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_format = new DecimalFormat("##0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);

        two_decimals_format = new DecimalFormat("#0.00");
        format_symbols = two_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_format.setDecimalFormatSymbols(format_symbols);

    }
    
    public void paint(Graphics2D g2) {
        
        if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_APU) {
        	
        	boolean avail=aircraft.apu_gen_on();
        	boolean start=aircraft.apu_starter()>0;
        	boolean running=aircraft.apu_running();
        	// Stater pos 2
        	boolean flap= start || (aircraft.apu_n1()>20.0f);
        	boolean v_avail = (aircraft.apu_n1()>89.0f);
        	
        	// Page ID
        	drawPageID(g2, "APU");
        	
        	if (aircraft.has_apu()) {
        		// Elec Bloc
        		// Use the X-Plane noise generator to put some noise on freq
        		int load = Math.round(aircraft.apu_gen_amp());
        		int volt = v_avail ? Math.round(aircraft.apu_n1()/100*115) : 0;
        		int freq = v_avail ? Math.round(aircraft.apu_n1()/100*400) : 0;
        		int apu_elec_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*150/1000;
        		int apu_elec_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*180/1000;        	
        		drawElec(g2,"APU GEN", start, load, volt, freq, apu_elec_x, apu_elec_y);

        		// Bleed 
        		if (aircraft.has_bleed_air()){
        			int psi = Math.round(aircraft.apu_bleed_psi());
        			int apu_bleed_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*700/1000;
        			int apu_bleed_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*250/1000;
        			ValveStatus apu_bleed_status = avail ? ValveStatus.VALVE_OPEN : ValveStatus.VALVE_CLOSED ;
        			if (avionics.is_qpac()) { 
        				apu_bleed_status = aircraft.bleed_valve(Aircraft.BLEED_VALVE_APU);
        				if (apu_bleed_status == ValveStatus.VALVE_CLOSED) psi=0;
        			}
        			
        			// Bleed Bloc
        			drawBleed(g2,"BLEED", start, psi, apu_bleed_x, apu_bleed_y);

        			// Bleed Valve Bloc
        			int apu_bleed_valve_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*700/1000 + mfd_gc.digit_width_l * 4;
        			int apu_bleed_valve_y = apu_bleed_y - mfd_gc.cond_valve_r*2;

        			drawValve(g2, apu_bleed_status, apu_bleed_valve_x, apu_bleed_valve_y);
        			g2.setColor(mfd_gc.ecam_normal_color);
        			g2.drawLine(apu_bleed_valve_x,apu_bleed_valve_y + mfd_gc.cond_valve_r, apu_bleed_valve_x,apu_bleed_y);

        		}


        		// Separation line
        		g2.setColor(mfd_gc.ecam_markings_color);
        		int sep_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*370/1000;
        		int sep_y2 = sep_y +  mfd_gc.mfd_size*30/1000;
        		int sep_x1 = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*100/1000;
        		int sep_x2 = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*900/1000;
        		g2.drawLine(sep_x1, sep_y, sep_x2, sep_y);
        		g2.drawLine(sep_x1, sep_y, sep_x1, sep_y2);
        		g2.drawLine(sep_x2, sep_y, sep_x2, sep_y2);

        		// Legends
                // N1
                String ind_str1 =  "N";
                String ind_str2 =  "%" ;
                int ind_middle = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*365/1000;
                int ind_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*490/1000;
                int ind_x1 = ind_middle - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str1)/2;
                int ind_x2 = ind_middle - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str2)/2;
                g2.setFont(mfd_gc.font_l);
                g2.setColor(mfd_gc.ecam_markings_color);
                g2.drawString(ind_str1, ind_x1, ind_y - 2);
                g2.setColor(mfd_gc.ecam_action_color);
                g2.drawString(ind_str2, ind_x2, ind_y + mfd_gc.line_height_l);

                // EGT
                ind_str1 =  "EGT";
                ind_str2 =  "°c" ;                
                ind_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*800/1000;
                ind_x1 = ind_middle - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str1)/2;
                ind_x2 = ind_middle - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str2)/2;
                g2.setFont(mfd_gc.font_l);
                g2.setColor(mfd_gc.ecam_markings_color);
                g2.drawString(ind_str1, ind_x1, ind_y - 2);
                g2.setColor(mfd_gc.ecam_action_color);
                g2.drawString(ind_str2, ind_x2, ind_y + mfd_gc.line_height_l);
        		
        		// N1 Gauge
        		int cols=2;
        		for (int i=0; i<cols; i++) {
        			prim_dial_x[i] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*50/100/cols + i*mfd_gc.panel_rect.width/cols;
        		}
        		if (mfd_gc.airbus_style) { drawAirbusN1(g2,0,1,false); } else { drawBoeingN1(g2,0,1,false); }

        		// EGT Gauge
        		if (mfd_gc.airbus_style) { drawAirbusEGT(g2,0,1, !avail); } else { drawBoeingEGT(g2,0,1); }


        		// AVAIL indicator
        		// TODO : if A/C is not an Airbus, use "ON BUS" indicator
        		// A320 FCOM 1.49.20 p2 : Avail when N1 > 99,5% or N1 > 95% for 2 seconds
        		if (avail) {
        			String avail_str="AVAIL";
        			int avail_x=mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2;
        			int avail_y=mfd_gc.panel_rect.y + mfd_gc.line_height_xl*32/10;
        			g2.setColor(mfd_gc.ecam_normal_color);
        			g2.setFont(mfd_gc.font_xl);
        			g2.drawString( avail_str, avail_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, avail_str)/2, avail_y);		
        		}

        		// FLAP indicator
        		// A320 FCOM 1.29.20 p4
        		if (flap) {
        			String avail_str="FLAP OPEN";
        			int flap_x=mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*3/4;
        			int flap_y=mfd_gc.panel_rect.y + mfd_gc.mfd_size*70/100;
        			g2.setColor(mfd_gc.ecam_normal_color);
        			g2.setFont(mfd_gc.font_xl);
        			g2.drawString( avail_str, flap_x- mfd_gc.get_text_width(g2, mfd_gc.font_xl, avail_str)/2, flap_y);		
        		}     

        		// STARTER indicator (not for airbus...)
        		if (this.aircraft.apu_starter()>1) {
        			String avail_str="START";
        			int flap_x=mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*3/4;
        			int flap_y=mfd_gc.panel_rect.y + mfd_gc.mfd_size*65/100;
        			g2.setColor(mfd_gc.ecam_normal_color);
        			g2.setFont(mfd_gc.font_xl);
        			g2.drawString( avail_str, flap_x - mfd_gc.get_text_width(g2, mfd_gc.font_xl, avail_str)/2, flap_y);	
        			
        		// TODO : LOW OIL LEVEL
        		// TODO : FUEL LO PR
        		}     
        	} else {
        		// No APU
    			String no_apu_str="NOT INSTALLED";
    			int flap_x=mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, no_apu_str)/2;
    			int flap_y=mfd_gc.panel_rect.y + mfd_gc.mfd_size*50/100;
    			g2.setColor(mfd_gc.ecam_markings_color);
    			g2.setFont(mfd_gc.font_xl);
    			g2.drawString( no_apu_str, flap_x, flap_y);
        	}
        	
        }

    }

    private void drawElec(Graphics2D g2, String bloc_str, boolean display_values, int load, int volt, int freq, int x, int y) {
    	int w = mfd_gc.digit_width_m * 8;
    	int h = mfd_gc.line_height_l * 9/2;
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_m);
        g2.drawRect(x,y,w,h);
        if (display_values && ( freq<390 || volt < 105)) { g2.setColor(mfd_gc.ecam_caution_color); }
        g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, bloc_str)/2, y + mfd_gc.line_height_m );
        
        // Triangle
        int tri_x[] = { x + w/2 -w/15,x + w/2, x + w/2 + w/15 };
        int tri_y[] = { y-h/18, y-h/9, y-h/18 };
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawPolygon(tri_x, tri_y, 3);
        
        // Legends
        g2.setColor(mfd_gc.ecam_action_color);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("%",  x+w-mfd_gc.digit_width_m*2, y + mfd_gc.line_height_l *2);
        g2.drawString("V",  x+w-mfd_gc.digit_width_m*2, y + mfd_gc.line_height_l *3);
        g2.drawString("Hz", x+w-mfd_gc.digit_width_m*2, y + mfd_gc.line_height_l *4);
        
        // Values
        if (display_values) {
        	String str_volt = ""+volt;
        	String str_freq = ""+freq;
        	String str_load = ""+load;
        	if (freq==0) { str_freq = "XX"; }
        	g2.setFont(mfd_gc.font_l);
        	g2.setColor(mfd_gc.ecam_normal_color);       
        	g2.drawString(str_load,  x+w-mfd_gc.digit_width_m*5/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_load), y + mfd_gc.line_height_l *2);
        	if (volt<105) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.drawString(str_volt,  x+w-mfd_gc.digit_width_m*5/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_volt) , y + mfd_gc.line_height_l *3);
        	if (freq<390) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.drawString(str_freq, x+w-mfd_gc.digit_width_m*5/2 - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_freq), y + mfd_gc.line_height_l *4);
        }
    }
    
    private void drawBleed(Graphics2D g2, String bloc_str, boolean display_values, int psi, int x, int y) {
    	int w = mfd_gc.digit_width_m * 8;
    	int h = mfd_gc.line_height_m * 3;
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_m);
        g2.drawRect(x,y,w,h);
        g2.drawString( bloc_str, x + w/2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, bloc_str)/2, y + mfd_gc.line_height_m );
               
        // Legends
        g2.setColor(mfd_gc.ecam_action_color);
        g2.setFont(mfd_gc.font_s);
        g2.drawString("PSI",  x+w-mfd_gc.digit_width_m*3, y + mfd_gc.line_height_l *2);
        
        // Values
        if (display_values) {
        	String str_psi = ""+psi;
        	g2.setColor(mfd_gc.ecam_normal_color);       
        	if (psi==0) { str_psi = "XX"; }
        	if (psi<10) {g2.setColor(mfd_gc.ecam_caution_color); } else { g2.setColor(mfd_gc.ecam_normal_color); }
        	g2.setFont(mfd_gc.font_l);
        	g2.drawString(str_psi,  x+w-mfd_gc.digit_width_m*4 - mfd_gc.get_text_width(g2, mfd_gc.font_l, str_psi) , y + mfd_gc.line_height_l *2);
        }
    }
 
    private void drawValve(Graphics2D g2, ValveStatus valve_sts, int x, int y) {
    	int r = mfd_gc.cond_valve_r;
    	
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_OPEN) {
            g2.setColor(mfd_gc.ecam_normal_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	}
        g2.drawOval(x-r,y-r,r*2,r*2);
        
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_CLOSED_FAILED) {
    		g2.drawLine(x-r, y, x+r, y);
    	} else {
    		g2.drawLine(x, y-r, x, y+r);
    	}
    }
    
    private void drawPageID(Graphics2D g2, String page_str) {
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.setFont(mfd_gc.font_xxl);
    	int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str)/2;
    	int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10;     	
        g2.drawString(page_str, page_id_x, page_id_y);
        g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_l/8);
    }

    
    private void drawBoeingN1(Graphics2D g2, int pos, int num, boolean with_epr) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n1_value = this.aircraft.apu_n1();
        float n1_dial = Math.min(n1_value, 110.0f) / 100.0f;
        int epr_value = Math.round( this.aircraft.get_EPR(pos) * 100.0f );
        String n1_str = with_epr ? Integer.toString(epr_value) : one_decimal_format.format(n1_value);

        int n1_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*550/1000;
        int n1_r = mfd_gc.dial_r[num];
        int n1_box_y = n1_y - n1_r/8;

        if ( n1_dial <= 1.02f )  {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( n1_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        g2.fillArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -Math.round(n1_dial*200.0f));

        // scale markings every 10%
        g2.setColor(mfd_gc.dim_markings_color);
        for (int i=0; i<=10; i++) {
            g2.drawLine(prim_dial_x[pos]+n1_r*14/16, n1_y, prim_dial_x[pos]+n1_r-1, n1_y);
            g2.rotate(Math.toRadians(20), prim_dial_x[pos], n1_y);
        }
        g2.setTransform(original_at);
        
        // scale numbers 2, 4, 6, 8 and 10
        if ( num <= 4 ) {
            g2.setFont(mfd_gc.font_xs);
            int n1_digit_x;
            int n1_digit_y;
            int n1_digit_angle = 40;
            for (int i=2; i<=10; i+=2) {
                n1_digit_x = prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
                n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
                g2.drawString(Integer.toString(i), n1_digit_x - mfd_gc.digit_width_xs/2, n1_digit_y+mfd_gc.line_height_xs*3/8);
                n1_digit_angle += 40;
            }
        }

        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -200);
        g2.setColor(mfd_gc.caution_color);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, -200, -20);
        g2.rotate(Math.toRadians(220), prim_dial_x[pos], n1_y);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(prim_dial_x[pos]+n1_r, n1_y, prim_dial_x[pos]+n1_r*19/16, n1_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(n1_dial*200.0f)), prim_dial_x[pos], n1_y);
        g2.setColor(mfd_gc.markings_color);
        g2.drawLine(prim_dial_x[pos], n1_y, prim_dial_x[pos]+n1_r-2, n1_y);
        g2.setTransform(original_at);


        // value box
        if ( num < 5 ) {
            //g2.setColor(mfd_gc.markings_color);
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(prim_dial_x[pos], n1_box_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*55/10, mfd_gc.dial_font_h[num]*140/100);
            if ( n1_dial <= 1.02f ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(mfd_gc.markings_color);
            } else if ( n1_dial < 1.1f ) {
                g2.setColor(mfd_gc.caution_color);
            } else {
                g2.setColor(mfd_gc.warning_color);
            }
            g2.setFont(mfd_gc.dial_font[num]);
            g2.drawString(n1_str, prim_dial_x[pos]+mfd_gc.dial_font_w[num]*51/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], n1_str), n1_box_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }

    
    private void drawBoeingEGT(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float egt_percent = this.aircraft.apu_egt() / 10.0f;
        float egt_dial = Math.min(egt_percent, 110.0f) / 100.0f;
        int egt_value = Math.round(this.aircraft.apu_egt());
//egt_value=500;

        int egt_x = prim_dial_x[pos];
        // int egt_y = ( line == 2 ) ? mfd_gc.dial_main2_y :mfd_gc.dial_main3_y;
        int egt_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*850/1000;
        int egt_r = mfd_gc.dial_r[num];

        if ( egt_dial <= 1.0f ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( egt_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        g2.fillArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, 0, -Math.round(egt_dial*200.0f));

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, 0, -200);
        g2.setColor(mfd_gc.caution_color);
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, -200, -20);
        g2.rotate(Math.toRadians(220), egt_x, egt_y);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(egt_x+egt_r, egt_y, egt_x+egt_r*19/16, egt_y);
        g2.setTransform(original_at);

        // needle
        g2.rotate(Math.toRadians(Math.round(egt_dial*200.0f)), egt_x, egt_y);
        g2.setColor(mfd_gc.markings_color);
        g2.drawLine(egt_x, egt_y, egt_x+egt_r-2, egt_y);
        g2.setTransform(original_at);

        // value box
        egt_y -= egt_r/8;
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(egt_x, egt_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*47/10, mfd_gc.dial_font_h[num]*140/100);
            if ( egt_dial <= 1.0f )  {
                // inhibit caution or warning below 1000ft
                g2.setColor(mfd_gc.markings_color);
            } else if ( egt_dial < 1.1f ) {
                g2.setColor(mfd_gc.caution_color);
            } else {
                g2.setColor(mfd_gc.warning_color);
            }
            g2.setFont(mfd_gc.dial_font[num]);
            String egt_str = Integer.toString(egt_value);
            g2.drawString(egt_str, egt_x+mfd_gc.dial_font_w[num]*44/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], egt_str), egt_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }
    
    private void drawAirbusN1(Graphics2D g2, int pos, int num, boolean with_epr) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n1_value = this.aircraft.apu_n1();        
        float n1_dial = Math.min(n1_value, 110.0f) / 100.0f;    
        boolean n1_disabled = this.aircraft.apu_n1() < 1.0 && aircraft.apu_starter()==0;
        
        // N1 String - no decimal
        String n1_str = "" + Math.round(n1_value);

        int n1_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*550/1000;;
        int n1_r = mfd_gc.dial_r[num];
        int n1_box_y = n1_y - n1_r/8;
        
        int deg_start = 225; 	// Gauge starting position
        int deg_full  = 75;     // Gauge full (100%)
        int deg_warning = 70;   // Gauge red sector (warning) 
        int deg_end = 50;       // Gauge end
        int deg_norm_range = deg_start-deg_warning;
        int deg_warn_range = deg_warning-deg_end;
        int deg_full_range = deg_start-deg_full;   // Deg range from 0 to 100%

        if ( n1_dial <= 1.02f ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( n1_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        // g2.fillArc(mfd_gc.prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -Math.round(n1_dial*200.0f));

        // scale markings every 50%
        g2.setColor(mfd_gc.dim_markings_color);
        g2.rotate(Math.toRadians(360-deg_start), prim_dial_x[pos], n1_y);
        for (int i=0; i<=2; i++) {
            g2.drawLine(prim_dial_x[pos]+n1_r*14/16, n1_y, prim_dial_x[pos]+n1_r-1, n1_y);
            g2.rotate(Math.toRadians(deg_full_range/2), prim_dial_x[pos], n1_y);
        }
        g2.setTransform(original_at);
        
        // scale number 0 and 10
        g2.setFont(mfd_gc.font_m);
        int n1_digit_x;
        int n1_digit_y;
        // int n1_digit_angle = 360-deg_start+deg_norm_range/2;
        int n1_digit_angle = 360-deg_start;
        for (int i=0; i<=10; i+=10) {
        	n1_digit_x = prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*9/16);
        	n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*9/16);
        	g2.drawString(Integer.toString(i), n1_digit_x - mfd_gc.digit_width_m/2, n1_digit_y+mfd_gc.line_height_m*3/8);
        	n1_digit_angle += deg_full_range;
        }


        // g2.drawArc(mfd_gc.prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, 0, -200);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, deg_start, -deg_norm_range);
        g2.setColor(mfd_gc.ecam_warning_color);
        // g2.drawArc(mfd_gc.prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, -200, -20);
        g2.drawArc(prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, deg_warning, -deg_warn_range);
        // N1 max target
        g2.setColor(mfd_gc.ecam_caution_color);
        g2.rotate(Math.toRadians(360-deg_warning), prim_dial_x[pos], n1_y);
        g2.drawLine(prim_dial_x[pos]+n1_r*18/16, n1_y, prim_dial_x[pos]+n1_r+1, n1_y);
        g2.setTransform(original_at);
        
        //g2.rotate(Math.toRadians(220), mfd_gc.prim_dial_x[pos], n1_y);
        //g2.setColor(mfd_gc.warning_color);
        //g2.drawLine(mfd_gc.prim_dial_x[pos]+n1_r, n1_y, mfd_gc.prim_dial_x[pos]+n1_r*19/16, n1_y);
        //g2.setTransform(original_at);

        // needle
        if (!n1_disabled) {
        	g2.rotate(Math.toRadians(Math.round(n1_dial*deg_full_range)-deg_start), prim_dial_x[pos], n1_y);
        	g2.setColor(mfd_gc.ecam_normal_color);
        	g2.drawLine(prim_dial_x[pos], n1_y, prim_dial_x[pos]+n1_r*12/10, n1_y);
        	g2.setTransform(original_at);
        }
        
        // no value box for APU gauges in Airbus style

        if ( n1_dial <= 1.02f  ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(mfd_gc.ecam_normal_color);
        } else if ( n1_dial < 1.1f ) {
        	g2.setColor(mfd_gc.ecam_caution_color);
        } else {
        	g2.setColor(mfd_gc.ecam_warning_color);
        }
        if (n1_disabled) { 
        	n1_str="XX";
        	g2.setColor(mfd_gc.ecam_caution_color);
        }
        g2.setFont(mfd_gc.dial_font[num]);
        g2.drawString(n1_str, prim_dial_x[pos]- mfd_gc.dial_font_w[num] + mfd_gc.dial_font_w[num]*31/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], n1_str),
        		n1_box_y+mfd_gc.dial_font_h[num]*140/100);


        resetPen(g2);

    }
    
    private void drawAirbusEGT(Graphics2D g2, int pos, int num, boolean stabilized) {

    	// A320 FCOM 1.49.20 p 5
        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float egt_percent = this.aircraft.apu_egt() / 10.0f;
        float egt_dial = Math.min(egt_percent, 110.0f) / 100.0f;
        int egt_value = Math.round(this.aircraft.apu_egt());
        int egt_limit = Math.round(this.aircraft.apu_egt_limit());
        boolean egt_disabled = this.aircraft.apu_n1() < 1.0 && aircraft.apu_starter()==0;

        int egt_x = prim_dial_x[pos];
        int egt_y = mfd_gc.panel_rect.y + mfd_gc.mfd_size*850/1000;
        int egt_r = mfd_gc.dial_r[num];
        /*
        int deg_start = 180;
        int deg_caution = stabilized ? 60 : 25;
        int deg_warning = 0;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;
        */

        int deg_zero  = 300;	// Gauge zero (0%)
        int deg_start = 225; 	// Gauge starting position white sector
        int deg_full  = 50;     // Gauge full (100%)
        int deg_full_range = deg_zero-deg_full;   // Deg range from 0 to 100%
        // int deg_warning = stabilized ? 45 : 120;   // Gauge red sector (warning)
        int deg_warning = deg_zero - egt_limit*deg_full_range/1000;   // Gauge red sector (warning) 
        int deg_end = 25;       // Gauge end
        int deg_norm_range = deg_start-deg_warning;
        int deg_warn_range = deg_warning-deg_end;
        
        if ( egt_dial <= 1.0f ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( egt_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        

        g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, deg_start, -deg_norm_range);
        g2.setColor(mfd_gc.ecam_warning_color);
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, deg_warning, -deg_warn_range);
        g2.setTransform(original_at);
        // EGT max target
        g2.setColor(mfd_gc.ecam_caution_color);
        g2.rotate(Math.toRadians(360-deg_warning), prim_dial_x[pos], egt_y);
        g2.drawLine(prim_dial_x[pos]+egt_r*18/16, egt_y, prim_dial_x[pos]+egt_r+1, egt_y);
        g2.setTransform(original_at);

        
        // scale markings every 50%
        g2.setColor(mfd_gc.dim_markings_color);
        g2.rotate(Math.toRadians(360-deg_zero), prim_dial_x[pos], egt_y);
        for (int i=0; i<=10; i++) {
        	if (i==3 || i==7 || i==10) {
        		g2.drawLine(prim_dial_x[pos]+egt_r*14/16, egt_y, prim_dial_x[pos]+egt_r-1, egt_y);
        	}
            g2.rotate(Math.toRadians(deg_full_range/10), prim_dial_x[pos], egt_y);
        }
        g2.setTransform(original_at);
        
        // scale number 3, 7 and 10
        g2.setFont(mfd_gc.font_m);
        int n1_digit_x;
        int n1_digit_y;
        int n1_digit_angle = 360-deg_zero;
        // int n1_digit_angle = 360-deg_start;
        for (int i=0; i<=10; i+=1) {
        	n1_digit_x = prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*egt_r*9/16);
        	n1_digit_y = egt_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*egt_r*9/16);
        	if (i==3 || i==7 || i==10) {
        		g2.drawString(Integer.toString(i), n1_digit_x - mfd_gc.digit_width_m/2, n1_digit_y+mfd_gc.line_height_m*3/8);
        	}
        	n1_digit_angle += deg_full_range/10;
        }
        
        // needle
        if (!egt_disabled) {
        	// TODO: amber when EGT MAX - 33°C
        	// TODO: red when EGT >= EGT MAX
        	int egt_needle_deg = Math.max(Math.round(egt_dial*deg_full_range)-deg_zero , -deg_start);
        	g2.rotate(Math.toRadians(egt_needle_deg), prim_dial_x[pos], egt_y);
        	g2.setColor(mfd_gc.ecam_normal_color);
        	g2.drawLine(egt_x, egt_y, egt_x+egt_r-2, egt_y);
        	g2.setTransform(original_at);
        }
             
        
        // value
        String egt_str = Integer.toString(egt_value);
        egt_y -= egt_r/8;       
        if ( egt_dial <= 1.0f ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(mfd_gc.ecam_normal_color);
        } else if ( egt_dial < 1.1f ) {
        	g2.setColor(mfd_gc.ecam_caution_color);
        } else {
        	g2.setColor(mfd_gc.ecam_warning_color);
        }
        if (egt_disabled) { 
        	egt_str="XX";
        	g2.setColor(mfd_gc.ecam_caution_color);
        }
        g2.setFont(mfd_gc.dial_font[num]);        
        g2.drawString(egt_str, egt_x - mfd_gc.dial_font_w[num]*22/10 +mfd_gc.dial_font_w[num]*44/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], egt_str),
        		egt_y+mfd_gc.dial_font_h[num]*140/100);
        

        resetPen(g2);

    }
     
    
    
    private void scalePen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }
}
