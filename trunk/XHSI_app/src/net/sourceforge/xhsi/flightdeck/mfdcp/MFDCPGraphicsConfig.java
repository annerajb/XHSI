/**
 * MFDCPGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of MFDCPComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2017  Patrick Burkart (pburkartpublic@gmail.com) (Technische Hochschule Ingolstadt)
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

package net.sourceforge.xhsi.flightdeck.mfdcp;

import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;
import net.sourceforge.xhsi.model.Avionics;


public class MFDCPGraphicsConfig extends GraphicsConfig implements ComponentListener {

    private static Logger logger = Logger.getLogger("xhsi");

    public Rectangle ecp_rect;
    public Rectangle ecp_rectangle;
    public RoundRectangle2D[] button = new RoundRectangle2D[21];
    public Rectangle2D[] buttonLight = new Rectangle2D[18];
    public int buttonWidth;
    public int buttonHeight;
    public int buttonLightWidth;
    public int buttonLightHeight;
    public int buttonCorner;
    public int widthBetween; 
    
    public int buttonLine1;
    public int buttonLine2;
    public int buttonLine3;
    public int buttonLine4;
    public int buttonRow7;
    
    double r_w;
    double r_h;
    
    public int sep_line_x;
    public int sep_line_top;
    public int sep_line_bottom;
         
    public int label_ground_x;
    public int label_ground_y;
    public int label_nrst_x;
    public int label_nrst_y;
    public int label_loc_x;
    public int label_loc_y;
    public int label_fms_x;
    public int label_fms_y;

    public int led_r_x;
    public int led_r_y;
    public int led_top;
    public int led_bottom;
    public int led_left;
    public int led_right;
    
    public int dest_box_x;
    public int dest_box_y;
    public int dest_box_w;
    public int dest_box_h;
    
    public int dest_value_x;
    public int dest_value_y;
    
    public int btn_label_to_x;
    public int btn_label_to_y;
    public int btn_label_config_x;
    public int btn_label_config_y;
    
    public int btn_label_emer_x;
    public int btn_label_emer_y;
    public int btn_label_canc_x;
    public int btn_label_canc_y;
    
    public int btn_label_chart_x;
    public int btn_label_chart_y;
    
    public int btn_label_fpln_x;
    public int btn_label_fpln_y;
    
    public int btn_label_rtu_x;
    public int btn_label_rtu_y;
    
    public int clickMargin;
    
    public int gear_square_size;
    public GradientPaint gear_gradient;
    public Font font_header;
    public Font font_label;
    public Font font_label_small;
    public Font font_7seg;

    private static final double WIDTH = 830;
    private static final double HEIGHT = 480;

    public boolean airbus_style;
    public boolean boeing_style;


    private double thi_fcu;

    public MFDCPGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        init();
    }

   
    public void update_config(Graphics2D g2, boolean power, int instrument_style, double thi_fcu, double cockpit_light) {

        if (this.resized
                || this.reconfig
                || (this.powered != power)
                || this.style != instrument_style
                || this.thi_fcu != thi_fcu) {
            // one of the settings has been changed

            // remember the avionics power settings
            // actually, for the annunciators, we use battery power, not avionics power
            this.powered = power;
            this.style = instrument_style;
            super.update_config(g2);

            if (this.thi_fcu != thi_fcu) {
                //I don't see the need to update this when e.g. resizing has been done! PB
                update_mfd_panel_colors(thi_fcu, cockpit_light);
                this.thi_fcu = thi_fcu;
            }

            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

            int clusterWidth;
            int clusterHeight;
            if (panel_rect.width / WIDTH < panel_rect.height / HEIGHT) {
                clusterWidth = panel_rect.width;
                clusterHeight = (int) (panel_rect.width * HEIGHT / WIDTH);
                ecp_rect = new Rectangle(
                        panel_rect.x,
                        panel_rect.y,
                        clusterWidth,
                        clusterHeight
                );
            } else {
                clusterWidth = (int) ((panel_rect.height) * WIDTH / HEIGHT);
                clusterHeight = (panel_rect.height);
                ecp_rect = new Rectangle(
                        panel_rect.x,
                        panel_rect.y,
                        clusterWidth,
                        clusterHeight
                );
            }

            font_header = new Font("Arial", Font.BOLD, (int) ((clusterWidth / WIDTH + clusterHeight / HEIGHT) / 2 * 30));
            font_label = new Font("Arial", Font.BOLD, (int) ((clusterWidth / WIDTH + clusterHeight / HEIGHT) / 2 * 22));
            font_label_small = new Font("Arial", Font.BOLD, (int) ((clusterWidth / WIDTH + clusterHeight / HEIGHT) / 2 * 17));
            font_7seg = new Font("DSEG14 Classic", Font.ITALIC, (int) ((clusterWidth / WIDTH) * 30));

            ecp_rectangle = new Rectangle(
                    (int) (0.0 / WIDTH * clusterWidth),
                    (int) (0.0 / HEIGHT * clusterHeight),
                    (int) (WIDTH / WIDTH * clusterWidth),
                    (int) (HEIGHT / HEIGHT * clusterHeight));

            gear_gradient = new GradientPaint(
                    0, 0, frontpanel_color.brighter().brighter(),
                    ecp_rect.width, ecp_rect.height, frontpanel_color.darker().darker(),
                    false);

            int x = ecp_rectangle.x;
            int y = ecp_rectangle.y;
            r_w = ecp_rectangle.width / 830.0;
            r_h = ecp_rectangle.height / 480.0;
            
            buttonWidth = inRel(r_w, 93);
            buttonHeight = inRel(r_h, 59);
            buttonLightWidth = inRel(r_w, 69);
            buttonLightHeight = inRel(r_h, 16);
            buttonCorner = inRel(r_w, 20);
            widthBetween = inRel(r_w, 18); //35 vertical
            
            buttonLine1 = y + inRel(r_h, 50);
            buttonLine2 = y + inRel(r_h, 144);
            buttonLine3 = y + inRel(r_h, 238);
            buttonLine4 = y + inRel(r_h, 332);
            buttonRow7 = x + inRel(r_w, 740);
            
            // Draw the buttons
            // TO CONFIG
            button[0] = new RoundRectangle2D.Double(x + inRel(r_w, 164), buttonLine1, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
            // EMEC CANC
            button[1] = new RoundRectangle2D.Double(x + inRel(r_w, 386), buttonLine1, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
            for (int i = 2; i < 8; i++) {
                button[i] = new RoundRectangle2D.Double(x + inRel(r_w, (i - 2) * 111 + 53), buttonLine2, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
                buttonLight[i - 2] = new Rectangle2D.Double(x + inRel(r_w, (i - 2) * 111 + 53 + 12), y + inRel(r_h, 144 + 8), buttonLightWidth, buttonLightHeight);
            }
            for (int i = 8; i < 14; i++) {
                button[i] = new RoundRectangle2D.Double(x + inRel(r_w, (i - 8) * 111 + 53), buttonLine3, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
                buttonLight[i - 2] = new Rectangle2D.Double(x + inRel(r_w, (i - 8) * 111 + 53 + 12), y + inRel(r_h, 238 + 8), buttonLightWidth, buttonLightHeight);
            }
            // CLR LEFT
            button[14] = new RoundRectangle2D.Double(x + inRel(r_w, 53), buttonLine4, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
            buttonLight[12] = new Rectangle2D.Double(x + inRel(r_w, 53 + 12), y + inRel(r_h, 332 + 8), buttonLightWidth, buttonLightHeight);
            
            // STS
            button[15] = new RoundRectangle2D.Double(x + inRel(r_w, 275), buttonLine4, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
            buttonLight[13] = new Rectangle2D.Double(x + inRel(r_w, 275 + 12), y + inRel(r_h, 332 + 8), buttonLightWidth, buttonLightHeight);
            
            // RCL
            button[16] = new RoundRectangle2D.Double(x + inRel(r_w, 386), buttonLine4, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
            
            // CLR RIGHT
            button[17] = new RoundRectangle2D.Double(x + inRel(r_w, 608), buttonLine4, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
            buttonLight[14] = new Rectangle2D.Double(x + inRel(r_w, 608 + 12), y + inRel(r_h, 332 + 8), buttonLightWidth, buttonLightHeight);
            
            //if (!avionics.is_ff_a320()) {
                button[18] = new RoundRectangle2D.Double(buttonRow7, buttonLine2, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
                buttonLight[15] = new Rectangle2D.Double(x + inRel(r_w, 740 + 12), y + inRel(r_h, 144 + 8), buttonLightWidth, buttonLightHeight);
                button[19] = new RoundRectangle2D.Double(buttonRow7, buttonLine3, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
                buttonLight[16] = new Rectangle2D.Double(x + inRel(r_w, 740 + 12), y + inRel(r_h, 238 + 8), buttonLightWidth, buttonLightHeight);
                button[20] = new RoundRectangle2D.Double(buttonRow7, buttonLine4, buttonWidth, buttonHeight, buttonCorner, buttonCorner);
                buttonLight[17] = new Rectangle2D.Double(x + inRel(r_w, 740 + 12), y + inRel(r_h, 332 + 8), buttonLightWidth, buttonLightHeight);
                /*
            }else{
                button[18] = null;
                button[19] = null;
                button[20] = null;
                buttonLight[15] = null;
                buttonLight[16] = null;
                buttonLight[17] = null;
            }*/
            clickMargin = inRel(r_w, 10);
            
            sep_line_x = x + inRel(r_w, 720);
            sep_line_top = y + inRel(r_h, 144);
            sep_line_bottom = y + inRel(r_h, 391);
            
            label_ground_x = x + inRel(r_w, 555);
            label_ground_y = y + inRel(r_h, 70);
            label_nrst_x = x + inRel(r_w, 580);
            label_nrst_y = y + inRel(r_h, 100);
            label_loc_x = x + inRel(r_w, 830);
            label_loc_y = y + inRel(r_h, 70);
            label_fms_x = x + inRel(r_w, 830);
            label_fms_y = y + inRel(r_h, 100);

            led_r_x = inRel(r_w, 10);
            led_r_y = inRel(r_h, 10);
            led_top = y + inRel(r_h, 60);
            led_bottom = y + inRel(r_h, 90);
            led_left = x + inRel(r_w, 635);
            led_right = x + inRel(r_w, 805);
            
            dest_box_x = x + inRel(r_w, 650);
            dest_box_y = y + inRel(r_h, 55);
            dest_box_w = inRel(r_w, 150);
            dest_box_h = inRel(r_h, 50);
            
            dest_value_x = x + inRel(r_w, 660);
            dest_value_y = y + inRel(r_h, 95);
            
            btn_label_to_x = x + inRel(r_w, 164 + 28);
            btn_label_to_y = y + inRel(r_h, 50 + 24);
            btn_label_config_x =x + inRel(r_w, 164 + 6);
            btn_label_config_y = y + inRel(r_h, 50 + 24 + 7) + (int) (get_text_height(g2, font_label) * (3.0 / 4.0));
            
            btn_label_emer_x = x + inRel(r_w, 386 + 16);
            btn_label_emer_y = y + inRel(r_h, 50 + 24);
            btn_label_canc_x = x + inRel(r_w, 386 + 16);
            btn_label_canc_y = y + inRel(r_h, 50 + 24 + 7) + (int) (get_text_height(g2, font_label) * (3.0 / 4.0));
            
            btn_label_chart_x = x + inRel(r_w, 740 + 8);
            btn_label_chart_y = y + inRel(r_h, 144 + 50);
            
            btn_label_fpln_x = x + inRel(r_w, 740 + 16);
            btn_label_fpln_y = y + inRel(r_h, 238 + 50);
            
            btn_label_rtu_x = x + inRel(r_w, 740 + 23);
            btn_label_rtu_y = y + inRel(r_h, 332 + 50);
        }

    }

    /**
     * Recalculates the text and background colors on the OHP. It uses a matrix
     * to do linear interpolation upon tested data of the QPAC. The colors can
     * then be retrieved using
     * {@link #get_panel_background_light get_panel_background_light} or similar
     * methods.
     *
     *
     * @param brightness_setting A value between 0 and 1, representing the
     * setting on the OHP
     * @param outside_light A value between 0 and 180, with 180 being daylight
     * and 0 being complete darkness
     */
    public void update_mfd_panel_colors(double brightness_setting, double outside_light) {
        //Make sure ohp_background_setting is between 0 and 1!
        brightness_setting = brightness_setting < 0 ? 0 : brightness_setting >= 1 ? 1 : brightness_setting;
        panel_display_main_color = this.update_panel_display_color(brightness_setting, outside_light);
        panel_main_text_color = this.update_panel_text_color(brightness_setting, outside_light);
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
