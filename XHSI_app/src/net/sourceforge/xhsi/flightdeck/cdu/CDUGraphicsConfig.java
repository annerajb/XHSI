/**
 * CDUGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2015  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2015  Nicoals Carel
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
package net.sourceforge.xhsi.flightdeck.cdu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

import net.sourceforge.xhsi.XHSIInstrument;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.model.Avionics;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class CDUGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public int cdu_size;
    public int cdu_middle_x;
    public int cdu_first_line;
    public int cdu_dy_line;
    public int cdu_scratch_line; 
    
    int cdu_screen_topleft_x = 81;
    int cdu_screen_topleft_y = 56;
    int cdu_screen_width = 338;
    int cdu_screen_height = 400;
    
    Font cdu_small_font;
    Font cdu_normal_font;

    int cdu_digit_width;
    
    public Rectangle raised_panel;
    public GradientPaint panel_gradient;

    boolean display_only;

    public CDUGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }

    public void update_config(Graphics2D g2, boolean power, int source, boolean cdu_display_only) {

        if (this.resized
                || this.reconfig
                || (this.powered != power)
                || (this.cdu_source != source)
                || (this.display_only != cdu_display_only)
            ) {
            // one of the settings has been changed

            // for the CDU, we use battery power, not avionics power
            this.powered = power;
            
            // the FMC for this CDU
            this.cdu_source = source;
            
            // Full CDU (false) - text lines only (true)
            this.display_only = cdu_display_only;
            
            super.update_config(g2);

            // some subcomponents need to be reminded to redraw immediately
            this.reconfigured = true;
            
      
            if (display_only) {
            	cdu_screen_topleft_x = panel_rect.x;
            	cdu_screen_topleft_y = panel_rect.y;
            	cdu_screen_width = panel_rect.width;
            	cdu_screen_height = panel_rect.height;
            	cdu_normal_font = font_fixed_zl;
            	// Align font text spacing with normal font
            	Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            	attributes.put(TextAttribute.TRACKING, 0.145);
            	cdu_small_font= font_fixed_xxxl.deriveFont(attributes);
            	// cdu_small_font = font_fixed_xxxl;
            	cdu_digit_width = digit_width_fixed_zl;
                cdu_size = Math.min(cdu_screen_width, cdu_screen_height);
                cdu_middle_x = cdu_screen_topleft_x + cdu_screen_width / 2;
                cdu_dy_line = cdu_size / 14 ;
            	cdu_first_line = cdu_screen_topleft_y + line_height_fixed_zl;
            	cdu_scratch_line = cdu_screen_topleft_y + cdu_size - line_height_fixed_zl/10;
            } else {
            	cdu_screen_topleft_x = panel_rect.x+panel_rect.width*81/480;
            	cdu_screen_topleft_y = panel_rect.y+panel_rect.height*65/800;
            	cdu_screen_width = panel_rect.width*338/480;
            	cdu_screen_height = panel_rect.height*315/800;
            	cdu_normal_font = font_fixed_xxl;
            	// Align font text spacing with normal font
            	Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            	attributes.put(TextAttribute.TRACKING, 0.145);
            	cdu_small_font= font_fixed_xl.deriveFont(attributes);
            	// cdu_small_font = font_fixed_xl;
            	cdu_digit_width = digit_width_fixed_xxl;
                cdu_size = Math.min(cdu_screen_width, cdu_screen_height);
                cdu_middle_x = cdu_screen_topleft_x + cdu_screen_width / 2;
                cdu_dy_line = cdu_screen_height / 14 ;
            	cdu_first_line = cdu_screen_topleft_y + line_height_fixed_xxl;
            	cdu_scratch_line = cdu_screen_topleft_y + cdu_screen_height - line_height_fixed_xxl/10;
            }
            
                 
            float cdu_panel_aspect;
            switch (source) {
                case Avionics.CDU_SOURCE_LEGACY :
                    cdu_panel_aspect = 1.0f;
                    break;
                case Avionics.CDU_SOURCE_XFMC :
                    cdu_panel_aspect = 3.0f / 4.0f;
                    break;
                case Avionics.CDU_SOURCE_UFMC :
                    cdu_panel_aspect = 4.0f / 3.0f;
                    break;
                default :
                    cdu_panel_aspect = 1.0f;
                    break;
            }

            if ( ( (float)panel_rect.width / (float)panel_rect.height ) > cdu_panel_aspect ) {
                // window is wider than necessary, my_height is OK
                int my_height = panel_rect.height;
                int my_width = (int)(my_height * cdu_panel_aspect);
                raised_panel = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - my_width/2,
                        panel_rect.y,
                        my_width,
                        my_height
                    );
            } else {
                // window is higher than necessary, my_width is OK
                int my_width = panel_rect.width;
                int my_height = (int)(my_width / cdu_panel_aspect);
                raised_panel = new Rectangle(
                        panel_rect.x,
                        panel_rect.y + panel_rect.height/2 - my_height/2,
                        my_width,
                        my_height
                    );
            }

            panel_gradient = new GradientPaint(
                    0, 0, frontpanel_color.brighter().brighter(),
                    raised_panel.width, raised_panel.height , frontpanel_color.darker().darker(),
                    false);

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


}
