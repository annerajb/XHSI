/**
 * AnnunGraphicsConfig.java
 *
 * Calculates and provides access to screen positions and sizes based on the
 * size of HSIComponent.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.annunciators;


import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.RoundRectangle2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;


public class AnnunGraphicsConfig extends GraphicsConfig implements ComponentListener {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public Rectangle cluster_rect;
    public Rectangle master_square;
    public Rectangle annun_square;
    public Rectangle flaps_square;
    public Rectangle gear_square;
    public int annun_square_size;
    public GradientPaint annun_gradient;

    /*
     * Master Warning and Caution
     */
    public Stroke master_stroke;

    public final static int BUTTON_MASTER_WARNING = 0;
    public final static int BUTTON_MASTER_CAUTION = 1;
    public Shape[] master_buttons;
    
    public AnnunGraphicsConfig(Component root_component, int du) {
        super(root_component);
        this.display_unit = du;
        master_buttons = new Shape[2];
        init();
    }


    public void update_config(Graphics2D g2, boolean power) {

        if (this.resized
                || this.reconfig
                || (this.powered != power)
            ) {
            // one of the settings has been changed

            // remember the avionics power settings
            // actually, for the annunciators, we use battery power, not avionics power
            this.powered = power;
            super.update_config(g2);

            // some subcomponents need to be reminded to redraw imediately
            this.reconfigured = true;

            int square_size;
            if ( this.preferences.get_panel_square(this.display_unit) || ( (panel_rect.width <= panel_rect.height*2) && (panel_rect.height <= panel_rect.width*2) ) ) {
                // calculate positions of the 4 squares in a big square
                square_size = Math.min(panel_rect.width, panel_rect.height)/2;
                cluster_rect = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size,
                        panel_rect.y + panel_rect.height/2 - square_size,
                        2*square_size,
                        2*square_size
                    );
                master_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size,
                        panel_rect.y + panel_rect.height/2 - square_size,
                        square_size,
                        square_size
                    );
                annun_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size,
                        panel_rect.y + panel_rect.height/2,
                        square_size,
                        square_size
                    );
                flaps_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2,
                        panel_rect.y + panel_rect.height/2 - square_size,
                        square_size,
                        square_size
                    );
                gear_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2,
                        panel_rect.y + panel_rect.height/2,
                        square_size,
                        square_size
                    );
            } else if (panel_rect.width > panel_rect.height*2 ) {
                // calculate positions of the 4 squares in a line
                square_size = Math.min(panel_rect.width/4, panel_rect.height);
                cluster_rect = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - 2*square_size,
                        panel_rect.y + panel_rect.height/2 - square_size/2,
                        4*square_size,
                        square_size
                    );
                master_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - 2*square_size,
                        panel_rect.y + panel_rect.height/2 - square_size/2,
                        square_size,
                        square_size
                    );
                annun_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size,
                        panel_rect.y + panel_rect.height/2 - square_size/2,
                        square_size,
                        square_size
                    );
                flaps_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2,
                        panel_rect.y + panel_rect.height/2 - square_size/2,
                        square_size,
                        square_size
                    );
                gear_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 + square_size,
                        panel_rect.y + panel_rect.height/2 - square_size/2,
                        square_size,
                        square_size
                    );
                // override font sizes
                set_fonts(g2, (float)square_size / 300.0f);
            } else {
                // calculate positions of the 4 squares in a column
                square_size = Math.min(panel_rect.width, panel_rect.height/4);
                cluster_rect = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size/2,
                        panel_rect.y + panel_rect.height/2 - 2*square_size,
                        square_size,
                        4*square_size
                    );
                master_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size/2,
                        panel_rect.y + panel_rect.height/2 - 2*square_size,
                        square_size,
                        square_size
                    );
                annun_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size/2,
                        panel_rect.y + panel_rect.height/2 - square_size,
                        square_size,
                        square_size
                    );
                flaps_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size/2,
                        panel_rect.y + panel_rect.height/2,
                        square_size,
                        square_size
                    );
                gear_square = new Rectangle(
                        panel_rect.x + panel_rect.width/2 - square_size/2,
                        panel_rect.y + panel_rect.height/2 + square_size,
                        square_size,
                        square_size
                    );
                // override font sizes
                set_fonts(g2, (float)square_size / 300.0f);
            }
            
            /*
             * Master Warning and Caution
             */
            int master_x = master_square.x + master_square.width/2 - master_square.width/2*3/4;
            int master_w = master_square.width*3/4;
            int warning_y = master_square.y + master_square.height/2 - line_height_xxl*4 - line_height_xxl/2;
            int caution_y = master_square.y + master_square.height/2 + line_height_xxl/2;
            int master_h = line_height_xxl*4;
            int master_r = master_h/16;
            master_stroke = new BasicStroke(2.0f * master_r);
            master_buttons[BUTTON_MASTER_WARNING] = new RoundRectangle2D.Double(master_x, warning_y, master_w, master_h, master_r, master_r);
            master_buttons[BUTTON_MASTER_CAUTION] = new RoundRectangle2D.Double(master_x, caution_y, master_w, master_h, master_r, master_r);
            
            annun_square_size = square_size;

//            annun_gradient = new GradientPaint(
//                    0, 0, color_irongray.brighter().brighter(),
//                    cluster_rect.width, cluster_rect.height , color_irongray.darker().darker(),
//                    false);
            annun_gradient = new GradientPaint(
                    0, 0, frontpanel_color.brighter().brighter(),
                    cluster_rect.width, cluster_rect.height , frontpanel_color.darker().darker(),
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
