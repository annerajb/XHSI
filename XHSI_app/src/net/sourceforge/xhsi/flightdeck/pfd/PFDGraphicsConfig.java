/**
 * PFDGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2016  Nicolas Carel
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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.flightdeck.GraphicsConfig;



public class PFDGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Composite orig_cmpst;
    private boolean draw_transparent;
    
    public int instrument_size;
    public int panel_offset_y;
    public int adi_cx;
    public int adi_cy;
    public int adi_size_left;
    public int adi_size_right;
    public int adi_size_up;
    public int adi_size_down;
    public int adi_pitchscale;
    public int adi_pitch90;
    public int tape_top;
    public int tape_height;
    public int tape_width;
    public int speedtape_left;
    public int altitape_left;
    public int fma_left;
    public int fma_width;
    public int fma_top;
    public int fma_height;
    public int fma_col_1;
    public int fma_col_2;
    public int fma_col_3;
    public int fma_col_4;
    public int dg_radius;
    public int dg_cx;
    public int dg_cy;
    public boolean full_rose;
    public boolean draw_hsi;
    public int hsi_tick_w;
    public int vsi_left;
    public int vsi_width;
    public int vsi_top;
    public int vsi_height;
    public int gs_width;
    public int gs_height;
    public int cdi_width;
    public int cdi_height;
    public int ra_x;
    public int ra_high_y;
    public int ra_low_y;
    public int ra_r;
    public int aoa_x;
    public int aoa_y;
    public int aoa_r;
    public int radios_top;
    public int radios_width;
    public int navradios_left;
    public int comradios_left;
    public int radios_height;
    
    // Only for Airbus
    // Heading tape
    public int hdg_top;
    public int hdg_left;
    public int hdg_height;
    public int hdg_width;
    // ILS Data
    public int ils_line1;
    public int ils_line2;
    public int ils_line3;
    public int ils_x;
    
    

    public PFDGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }

    
    public void update_config(Graphics2D g2, boolean power, int instrument_style, float du_brightness) {

     	// Update colors if du_brightness changed
     	update_colors(du_brightness);
     	
        if (this.resized
                || this.reconfig
                || (this.powered != power)
                || (this.style != instrument_style)
            ) {
            // one of the settings has been changed

            // remember the new settings
            this.powered = power;
            this.style = instrument_style;
            
            // general instrument config
            super.update_config(g2);

            // specific instrument config
            instrument_size = Math.min(this.panel_rect.width, this.panel_rect.height);
            panel_offset_y = 0;
            if ( this.panel_rect.height * 100 / this.panel_rect.width > 180 ) {
                panel_offset_y = ( this.panel_rect.height - this.panel_rect.width*180/100 ) / 2;
            }
            // expanded rose scale when only the top of the DG is visible
            full_rose =  ( this.panel_rect.height > this.panel_rect.width * 105/100 );
            // HSI mode when the display is much higher than wide
            //draw_hsi =  ( this.panel_rect.height > this.panel_rect.width * 131/100 );
            // no longer automatic...
            draw_hsi = this.preferences.get_pfd_draw_hsi();
            //adi_cx = this.panel_rect.x + this.panel_rect.width * 435 / 1000;
            if ( this.preferences.get_pfd_adi_centered() ) {
                if ( this.panel_rect.height >= this.panel_rect.width )
                    // square or tall window : ADI is left of center
                    adi_cx = this.panel_rect.x + this.panel_rect.width/2 - instrument_size*65/1000;
                else if ( this.panel_rect.width > this.panel_rect.height+2*instrument_size*65/1000 )
                    // ADI centered when window is wide enough (1065/1000)
                    adi_cx = this.panel_rect.x + this.panel_rect.width/2;
                else
                    // ADI as close to the center as possible
                    adi_cx = this.panel_rect.x + this.panel_rect.width/2 - instrument_size*65/1000 + (this.panel_rect.width-this.panel_rect.height)/2;
            } else {
                adi_cx = this.panel_rect.x + this.panel_rect.width/2 - instrument_size*65/1000;
            }
            adi_cy = panel_offset_y + this.panel_rect.y + instrument_size * 510 / 1000;
            adi_size_left = instrument_size * 250 / 1000;
            adi_size_right = instrument_size * 250 / 1000;
            adi_size_up = instrument_size * 260 / 1000;
            adi_size_down = instrument_size * 240 / 1000;
            adi_pitchscale = 22; // max scale down
            adi_pitch90 = adi_size_down * 90 / adi_pitchscale;
            tape_width = instrument_size * 120 / 1000;
            speedtape_left = adi_cx - adi_size_left - (instrument_size * 50 / 1000) - tape_width;
            altitape_left = adi_cx + adi_size_right + (instrument_size * 73 / 1000);

            if ( instrument_style == Avionics.STYLE_AIRBUS ) {
            	airbus_style = true;
            	boeing_style = false;
            	adi_cy = panel_offset_y + this.panel_rect.y + instrument_size * 515 / 1000;
            	// On Airbus, tape height is align with horizon
                adi_size_left = instrument_size * 250 / 1000;
                adi_size_right = instrument_size * 250 / 1000;
                adi_size_up = instrument_size * 260 / 1000;
                adi_size_down = instrument_size * 260 / 1000;
                adi_pitchscale = 22; // max scale down
                adi_pitch90 = adi_size_down * 90 / adi_pitchscale;
                tape_width = instrument_size * 140 / 1000;
                speedtape_left = adi_cx - adi_size_left - (instrument_size * 30 / 1000) - tape_width;
                altitape_left = adi_cx + adi_size_right + (instrument_size * 65 / 1000);
            	tape_height = instrument_size * 530 / 1000;
                vsi_height = instrument_size * 640 / 1000;
                fma_width =  instrument_size * 980 / 1000; // full width on A320 
                fma_left = speedtape_left;
                fma_height = instrument_size * 135 / 1000;
                fma_col_1 = fma_width*100/500;
                fma_col_2 = fma_width*206/500;
                fma_col_3 = fma_width*325/500;
                fma_col_4 = fma_width*419/500;
            } else {
            	airbus_style = false;
            	boeing_style = true;
                tape_height = instrument_size * 750 / 1000;
                vsi_height = instrument_size * 525 / 1000;
                fma_width = instrument_size * 560 / 1000; // was 546
                fma_left = adi_cx - fma_width/2;
                fma_height = instrument_size * 80 / 1000;
                fma_col_1 = fma_width*100/500;
                fma_col_2 = fma_width*206/500;
                fma_col_3 = fma_width*325/500;
                fma_col_4 = fma_width*419/500;
            }
            
            tape_top = adi_cy - tape_height/2;

            fma_top = panel_offset_y + this.panel_rect.y + instrument_size * 15 / 1000;
            dg_radius =  instrument_size * 350 / 1000;
            hsi_tick_w = dg_radius / 12;
            dg_cx = adi_cx;
            dg_cy = panel_offset_y + this.panel_rect.y + instrument_size * 880 / 1000 + dg_radius*102/100;
            if ( draw_hsi ) dg_cy += dg_radius*29/100 + line_height_xl*3/2;
            if ( this.preferences.get_pfd_adi_centered() ) {
                // if we want the ADI to be centered horizontally, it means that we will draw the ND below the PFD
                // so we can get rid of the DG or HSI
                dg_cy += 8; // whatever, but far away...
            }
            if ( instrument_style == Avionics.STYLE_AIRBUS ) dg_cy += instrument_size * 80 / 1000;
            
            vsi_width = instrument_size * 85 / 1000;
            vsi_left = altitape_left + tape_width + (instrument_size * 30 / 1000);

            vsi_top = adi_cy - vsi_height/2;
            gs_width = adi_size_right * 5 / 32;
            gs_height = 2 * adi_size_down;
            cdi_width = 2 * adi_size_left;
            cdi_height = adi_size_down * 5 / 32;
            ra_r = adi_size_right / 4;
            ra_x = adi_cx + adi_size_right - ra_r;
            ra_high_y = ( ( adi_cy - adi_size_up ) + ( fma_top + fma_height ) ) / 2;
            ra_low_y = adi_cy + adi_size_down + cdi_height + ra_r/2;
            aoa_r = ra_r;
            aoa_x = ra_x;
            aoa_y = ra_high_y;
            radios_top = fma_top;
            radios_width = instrument_size * 125 / 1000;
            // navradios_left = this.panel_rect.x + this.panel_rect.width/2 - instrument_size/2 - (instrument_size * 30 / 1000) - radios_width;
            navradios_left = speedtape_left - (instrument_size * 30 / 1000) - radios_width;
            // comradios_left = this.panel_rect.x + this.panel_rect.width/2 + instrument_size/2 + (instrument_size * 30 / 1000);
            comradios_left = vsi_left + vsi_width + (instrument_size * 25 / 1000);
            radios_height = instrument_size * 440 / 1000;
            
            // HDG Tape on Airbus
            hdg_top = adi_cy + instrument_size * 405 / 1000;
            hdg_left = adi_cx - adi_size_left*9/10;
            hdg_height = instrument_size * 65 / 1000;
            hdg_width = (adi_size_left + adi_size_right)*9/10;
            
            // ILS data on Airbus
            ils_line3 = hdg_top + hdg_width; 
            ils_line2 = ils_line3 + line_height_l;
            ils_line1 = ils_line2 + line_height_l;
            ils_x = speedtape_left;

        }

    }


    public void componentResized(ComponentEvent event) {
        this.component_size = event.getComponent().getSize();
        this.frame_size = event.getComponent().getSize();
        this.resized = true;
    }


    public void componentMoved(ComponentEvent event) {
        this.component_topleft = event.getComponent().getLocation();
    }


    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }


    public void setTransparent(Graphics2D g2, boolean is_transparent) {
        draw_transparent = ( is_transparent && ( this.preferences.get_pfd_dial_opacity() != 1.0f ) );
        if ( draw_transparent ) {
            orig_cmpst = g2.getComposite();
            float alpha = this.preferences.get_pfd_dial_opacity();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
        }
    }

    public void setOpaque(Graphics2D g2) {
        // should only be called after a call to setTransparent
        if ( draw_transparent ) {
            g2.setComposite(orig_cmpst);
        }
    }

}
