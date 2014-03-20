/**
* VSI_A320.java
* 
* This is the Airbus A320 family version of VSI.java
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  Nicolas Carel
* Adapted for Airbus by Nicolas Carel
* Reference : A320 FCOM 1.31.40 page 5-10 REV 36
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
import java.awt.Shape;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;

public class VSI_A320 extends PFDSubcomponent {


    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public VSI_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
    	if ( pfd_gc.airbus_style ) {
    		if ( ! XHSIStatus.receiving ) {
    			// FCOM 1.31.40 p28 (13) 
    			// if the vertical speed information fails, the V/S flag (red) replaces the vertical speed scale
    			drawFailedDial(g2);
    		} else if ( pfd_gc.powered ) {
    			drawDial(g2);
    		}
    	}
    }

    private void drawFailedDial(Graphics2D g2) {  	
        g2.setFont(pfd_gc.font_xxl);
    	g2.setColor(pfd_gc.warning_color);
        g2.drawString("V", pfd_gc.vsi_left, pfd_gc.adi_cy - pfd_gc.line_height_xxl/2 - 4);
        g2.drawString("/", pfd_gc.vsi_left, pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4);
        g2.drawString("S", pfd_gc.vsi_left, pfd_gc.adi_cy + pfd_gc.line_height_xxl*3/2 - 4);
    }

    private void drawDial(Graphics2D g2) {

        int up_down;
        int vvabs;
        int vvy;

        int l_x = pfd_gc.vsi_left;
//        int lm_x = pfd_gc.vsi_left + pfd_gc.vsi_width/3;
        int lm_x = pfd_gc.vsi_left + pfd_gc.digit_width_s + pfd_gc.vsi_width/10;
        
//        int m_x = pfd_gc.vsi_left + pfd_gc.vsi_width/2;
        int m_x = pfd_gc.vsi_left +  pfd_gc.digit_width_s + pfd_gc.vsi_width/10+ pfd_gc.vsi_width/6;
        int r_x = pfd_gc.vsi_left + pfd_gc.vsi_width;
        int rm_x = pfd_gc.vsi_left + pfd_gc.vsi_width*4/5 ;

        int t_y = pfd_gc.vsi_top;
        int tr_y = pfd_gc.vsi_top + pfd_gc.vsi_height/6;
        int tl_y = pfd_gc.vsi_top + pfd_gc.vsi_height*320/350;
        int tm_y = pfd_gc.vsi_top + pfd_gc.vsi_height*320/350;

        int b_y = pfd_gc.vsi_top + pfd_gc.vsi_height;
        int br_y = pfd_gc.vsi_top + pfd_gc.vsi_height - pfd_gc.vsi_height/6;
        int bl_y = pfd_gc.vsi_top + pfd_gc.vsi_height - pfd_gc.vsi_height*320/350;
        int bm_y = pfd_gc.vsi_top + pfd_gc.vsi_height - pfd_gc.vsi_height*230/350;

        int[] vsi_scale_x = {
        	l_x,	
            l_x,
            l_x,
            m_x,
            rm_x,
            rm_x,
            m_x,
            l_x,
            l_x,
            l_x
        };
        int[] vsi_scale_y = {
            tm_y,
            tl_y,
            t_y,
            t_y,
            tr_y,
            br_y,
            b_y,
            b_y,
            bl_y,
            bm_y
        };
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillPolygon(vsi_scale_x, vsi_scale_y, 10);
        pfd_gc.setOpaque(g2);

        int vvi = Math.round( this.aircraft.vvi() );
        //vvi = Math.round( this.aircraft.indicated_vv() );
        /* Code for Boeing
        if ( Math.abs(vvi) > 400 ) {
            g2.setColor(pfd_gc.markings_color);
            g2.setFont(pfd_gc.font_m);
            int vvipos_y = vvi > 0 ? t_y - 4: b_y + pfd_gc.line_height_m;
            g2.drawString("" + Math.abs(Math.round(vvi/10.0f)*10), pfd_gc.vsi_left, vvipos_y);
        }
        */

        int y_1000 = pfd_gc.vsi_height/2*33/75;
        int y_500 = y_1000 / 2;
        int y_2000 = pfd_gc.vsi_height/2*55/75;
        int y_1500 = y_1000 + (y_2000-y_1000)/2;
        int y_6000 = pfd_gc.vsi_height/2*70/75;
        int y_4000 = y_2000 + (y_6000-y_2000)/2;

        g2.setColor(pfd_gc.pfd_reference_color);
        g2.setFont(pfd_gc.font_s);
        g2.drawLine(l_x + 1, pfd_gc.adi_cy, m_x + 3, pfd_gc.adi_cy);
        g2.setColor(pfd_gc.pfd_markings_color);       
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy + y_500, m_x - 3, pfd_gc.adi_cy + y_500);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy - y_500, m_x - 3, pfd_gc.adi_cy - y_500);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy + y_1000, m_x - 3, pfd_gc.adi_cy + y_1000);
        g2.drawString("1", lm_x - pfd_gc.digit_width_s - 2, pfd_gc.adi_cy + y_1000 + pfd_gc.line_height_s/2 - 2);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy - y_1000, m_x - 3, pfd_gc.adi_cy - y_1000);
        g2.drawString("1", lm_x - pfd_gc.digit_width_s - 2, pfd_gc.adi_cy - y_1000 + pfd_gc.line_height_s/2 - 2);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy + y_1500, m_x - 3, pfd_gc.adi_cy + y_1500);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy - y_1500, m_x - 3, pfd_gc.adi_cy - y_1500);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy + y_2000, m_x - 3, pfd_gc.adi_cy + y_2000);
        g2.drawString("2", lm_x - pfd_gc.digit_width_s - 2, pfd_gc.adi_cy + y_2000 + pfd_gc.line_height_s/2 - 2);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy - y_2000, m_x - 3, pfd_gc.adi_cy - y_2000);
        g2.drawString("2", lm_x - pfd_gc.digit_width_s - 2, pfd_gc.adi_cy - y_2000 + pfd_gc.line_height_s/2 - 2);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy + y_4000, m_x - 3, pfd_gc.adi_cy + y_4000);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy - y_4000, m_x - 3, pfd_gc.adi_cy - y_4000);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy + y_6000, m_x - 3, pfd_gc.adi_cy + y_6000);
        g2.drawString("6", lm_x - pfd_gc.digit_width_s - 2, pfd_gc.adi_cy + y_6000 + pfd_gc.line_height_s/2 - 2);
        g2.drawLine(lm_x + 1, pfd_gc.adi_cy - y_6000, m_x - 3, pfd_gc.adi_cy - y_6000);
        g2.drawString("6", lm_x - pfd_gc.digit_width_s - 2, pfd_gc.adi_cy - y_6000 + pfd_gc.line_height_s/2 - 2);


        Stroke original_stroke = g2.getStroke();


        // AP VS bug
        int ap_vv = Math.round(this.avionics.autopilot_vv());
        if ( ap_vv != 0 ) {
            up_down = (int)Math.signum(ap_vv);
            vvabs = Math.abs(ap_vv);
            vvabs = Math.min(vvabs, 6250);
            if ( vvabs > 2000 ) {
                vvy = y_2000 + (vvabs-2000)*(y_6000-y_2000)/4000;
            } else if ( vvabs > 1000 ) {
                vvy = y_1000 + (vvabs-1000)*(y_2000-y_1000)/1000;
            } else {
                vvy = (vvabs)*(y_1000)/1000;
            }
            g2.setColor(pfd_gc.heading_bug_color);
            g2.setStroke(new BasicStroke(4.0f * pfd_gc.grow_scaling_factor));
            //g2.drawLine(lm_x, pfd_gc.adi_cy - vvy * up_down, m_x, pfd_gc.adi_cy - vvy * up_down);
            g2.drawLine(lm_x +1, pfd_gc.adi_cy - vvy * up_down, m_x - 2, pfd_gc.adi_cy - vvy * up_down);
            g2.setStroke(original_stroke);
            /* VV value is displayed on the FMA on Airbus A320
            g2.setFont(pfd_gc.font_m);
            String ap_vv_str = "" + ap_vv;
            int ap_vv_y = pfd_gc.vsi_top - pfd_gc.line_height_xl - pfd_gc.tape_width/6;
            g2.clearRect(pfd_gc.vsi_left - pfd_gc.digit_width_m/2 - pfd_gc.digit_width_m/3, ap_vv_y - pfd_gc.line_height_m*7/8, pfd_gc.get_text_width(g2, pfd_gc.font_m, ap_vv_str) + pfd_gc.digit_width_m*2/3, pfd_gc.line_height_m);
            g2.drawString(ap_vv_str, pfd_gc.vsi_left - pfd_gc.digit_width_m/2, ap_vv_y);
            */
        }


        // VSI needle
        up_down = (int)Math.signum(vvi);
        vvabs = Math.abs(vvi);
        vvabs = Math.min(vvabs, 6250);
        if ( vvabs > 2000 ) {
            vvy = y_2000 + (vvabs-2000)*(y_6000-y_2000)/4000;
        } else if ( vvabs > 1000 ) {
            vvy = y_1000 + (vvabs-1000)*(y_2000-y_1000)/1000;
        } else {
            vvy = (vvabs)*(y_1000)/1000;
        }
        Shape original_clipshape = g2.getClip();
        g2.clipRect(pfd_gc.vsi_left, pfd_gc.tape_top, pfd_gc.vsi_width - 1, pfd_gc.tape_height);
        if (vvabs > 6000 ) { 
        	g2.setColor(pfd_gc.pfd_caution_color); 
        } else {
           	g2.setColor(pfd_gc.pfd_vsi_needle_color);
        }
        g2.setStroke(new BasicStroke(2.5f * pfd_gc.grow_scaling_factor));
        g2.drawLine(m_x - 2, pfd_gc.adi_cy - vvy * up_down, pfd_gc.vsi_left + pfd_gc.vsi_width*150/100, pfd_gc.adi_cy);
        g2.setClip(original_clipshape);

        
        g2.setStroke(original_stroke);
        // VSI numeric indicator
        DecimalFormat vsi_format = new DecimalFormat("00");
        String vsi_str = "" + vsi_format.format(Math.abs(Math.round(vvi/100.0f)));

        if (vvabs > 100) {
            g2.setFont(pfd_gc.font_m);
            if (up_down > 0 ) {
            	g2.clearRect(m_x, pfd_gc.adi_cy - vvy * up_down - (pfd_gc.line_height_m * 7/8) , pfd_gc.digit_width_m * 2, pfd_gc.line_height_m);
            	g2.drawString(vsi_str, m_x , pfd_gc.adi_cy - vvy * up_down);
            } else {
                g2.clearRect(m_x, pfd_gc.adi_cy - vvy * up_down + (pfd_gc.line_height_m /8) , pfd_gc.digit_width_m * 2, pfd_gc.line_height_m);
                g2.drawString(vsi_str, m_x , pfd_gc.adi_cy - vvy * up_down + (pfd_gc.line_height_m * 7/8) );       
            }
        }
    }

}





