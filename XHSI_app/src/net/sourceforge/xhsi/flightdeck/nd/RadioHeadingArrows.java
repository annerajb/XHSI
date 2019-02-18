/**
* RadioHeadingArrows.java
* 
* Renders directional arrows indicating the direction to the currently 
* tuned radio navigation object. The arrows are rendered only if the 
* navigation radio has reception. Uses RadioHeadingArrowsHelper for
* actual rendering.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class RadioHeadingArrows extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    NavigationRadio selected_nav_radio1;
    NavigationRadio selected_nav_radio2;

    AffineTransform original_at;
    Shape original_clip;
    boolean clipped;
    

    public RadioHeadingArrows(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
        super(model_factory, hsi_gc);
    }

    public void paint(Graphics2D g2) {

        if ( nd_gc.powered && ! nd_gc.mode_plan && ( ! avionics.efis_shows_pos() || ( nd_gc.mode_classic_hsi ) ) ) {

            if ( this.preferences.get_draw_only_inside_rose() && nd_gc.limit_arcs && ! nd_gc.mode_centered ) {
                clip(g2, nd_gc.map_center_x - nd_gc.sixty_deg_hlimit, 0, nd_gc.sixty_deg_hlimit*2, nd_gc.frame_size.height);
                clipped = true;
            } else clipped = false;
               
            
            // int arrow_length = (int) Math.min(60, nd_gc.shrink_scaling_factor * 60);
            // int arrow_base_width = (int) Math.min(25, nd_gc.shrink_scaling_factor * 25);
            float drift;
            float deflect;
            if ( nd_gc.hdg_up ) drift = 0.0f;
            else drift = this.aircraft.drift();

            // get currently selected radios
            this.selected_nav_radio1 = this.avionics.get_selected_radio(1);
            this.selected_nav_radio2 = this.avionics.get_selected_radio(2);

            Stroke original_stroke = g2.getStroke();

            g2.setStroke(new BasicStroke(2.5f));

            if ( (this.selected_nav_radio1 != null) && (this.selected_nav_radio1.receiving()) ) {
                deflect = selected_nav_radio1.get_rel_bearing() + drift;
                if ( this.selected_nav_radio1.freq_is_nav() && ( ! this.selected_nav_radio1.freq_is_localizer() )) {
                    g2.setColor(nd_gc.tuned_vor_color);
                    draw_nav1_arrow(g2, deflect, nd_gc.arrow_length, nd_gc.arrow_base_width);
                } else if (this.selected_nav_radio1.freq_is_adf()) {
                    g2.setColor(nd_gc.tuned_ndb_color);
                    draw_nav1_arrow(g2, deflect, nd_gc.arrow_length, nd_gc.arrow_base_width);
                }
            }

            g2.setStroke(new BasicStroke(2.0f));

            if ( (this.selected_nav_radio2 != null) && (this.selected_nav_radio2.receiving()) ) {
                deflect = selected_nav_radio2.get_rel_bearing() + drift;
                if ( this.selected_nav_radio2.freq_is_nav() && ( ! this.selected_nav_radio2.freq_is_localizer() )) {
                    g2.setColor(nd_gc.tuned_vor_color);
                    draw_nav2_arrow(g2, deflect, nd_gc.arrow_length, nd_gc.arrow_base_width);
                } else if (this.selected_nav_radio2.freq_is_adf()) {
                    g2.setColor(nd_gc.tuned_ndb_color);
                    draw_nav2_arrow(g2, deflect, nd_gc.arrow_length, nd_gc.arrow_base_width);
                }
            }

            g2.setStroke(original_stroke);

        }

        if ( clipped ) unclip(g2);
        
    }


    private void rotate(Graphics2D g2, double angle) {
        this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
                Math.toRadians(angle),
                nd_gc.map_center_x,
                nd_gc.map_center_y);
        g2.transform(rotate);
    }


    private void unrotate(Graphics2D g2) {
        g2.setTransform(original_at);
    }


    private void clip(Graphics2D g2, int clip_x, int clip_y, int clip_w, int clip_h) {
        this.original_clip = g2.getClip();
        g2.setClip(clip_x, clip_y, clip_w, clip_h);
    }
    
    
    private void unclip(Graphics2D g2) {
        g2.setClip(original_clip);
    }
    
    
    private void draw_nav1_arrow(Graphics2D g2, float deflection, int length, int base_width) {
        rotate(g2, deflection);
        if (nd_gc.boeing_style) {
        	RadioHeadingArrowsHelper.draw_nav1_forward_arrow(g2, nd_gc.map_center_x, nd_gc.rose_y_offset, length, base_width);
        	RadioHeadingArrowsHelper.draw_nav1_backward_arrow(g2, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, length, base_width);
        } else {
        	RadioHeadingArrowsHelper.draw_nav1_airbus_forward_arrow(g2, nd_gc.map_center_x, nd_gc.rose_y_offset, length, base_width, selected_nav_radio1.freq_is_adf());
        	RadioHeadingArrowsHelper.draw_nav1_airbus_backward_arrow(g2, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, length, base_width, selected_nav_radio1.freq_is_adf());
        }
        unrotate(g2);
    }


    private void draw_nav2_arrow(Graphics2D g2, float deflection, int length, int base_width) {
        rotate(g2, deflection);
        if (nd_gc.boeing_style) {
        	RadioHeadingArrowsHelper.draw_nav2_forward_arrow(g2, nd_gc.map_center_x, nd_gc.rose_y_offset, length, base_width);
        	RadioHeadingArrowsHelper.draw_nav2_backward_arrow(g2, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, length, base_width);
        } else {
            RadioHeadingArrowsHelper.draw_nav2_airbus_forward_arrow(g2, nd_gc.map_center_x, nd_gc.rose_y_offset, length, base_width, selected_nav_radio2.freq_is_adf());
            RadioHeadingArrowsHelper.draw_nav2_airbus_backward_arrow(g2, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, length, base_width, selected_nav_radio2.freq_is_adf());        	
        }
        unrotate(g2);
    }


}
