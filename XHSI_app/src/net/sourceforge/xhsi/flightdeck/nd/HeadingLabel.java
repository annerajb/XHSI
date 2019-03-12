/**
* HeadingLabel.java
*
* Displays the current heading or track in a box at the top in the middle
*
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2019  Nicolas Carel
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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;


import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSIStatus;


import net.sourceforge.xhsi.flightdeck.nd.NDFramedElement.FE_Color;
import net.sourceforge.xhsi.model.ModelFactory;

public class HeadingLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    AffineTransform original_at = null;
    int old_hdg_text_length = 0;
    BufferedImage hdg_label_decoration_buf_img;

    private boolean failed_hdg;
    NDFramedElement failed_flag;

    public HeadingLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        this.hdg_label_decoration_buf_img = null;
        this.failed_hdg=false;
        failed_flag = new NDFramedElement(NDFramedElement.HDG_FLAG, 0, hsi_gc, FE_Color.FE_COLOR_ALARM);
        failed_flag.enableFlashing();
        failed_flag.disableFraming();
        failed_flag.setBigFont(true);
    }


    public void paint(Graphics2D g2) {
        
        if ( nd_gc.powered && ! nd_gc.mode_plan ) {
        	
            if (failed_hdg != (!this.avionics.hdg_valid() || (! XHSIStatus.receiving && nd_gc.airbus_style ))) {
            	// FCOM 1.31.40 p26 (18) 
            	// if the heading information fails, the HDG flag replaces the heading scale (red)           	
            	failed_hdg=!this.avionics.hdg_valid() || (! XHSIStatus.receiving && nd_gc.airbus_style );
            	if (failed_hdg) {
            		failed_flag.setText("HDG", nd_gc.airbus_style ? FE_Color.FE_COLOR_ALARM : FE_Color.FE_COLOR_CAUTION);
            		if (nd_gc.boeing_style)
            			failed_flag.enableFraming();
            		else
            			failed_flag.disableFraming();
            	} else {
            		failed_flag.clearText();
            	}
            }

            int rose_top_y = this.nd_gc.map_center_y - nd_gc.rose_radius;
            int center_x = this.nd_gc.map_center_x;
            int center_y = this.nd_gc.map_center_y;

            // heading or track
            String up_label;
            int mag_value;
            float map_up;
            float hdg_pointer;
            float trk_line;
            if ( nd_gc.hdg_up ) {
                // HDG UP
                mag_value = Math.round(this.aircraft.heading());
                map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
                up_label = "HDG";
                hdg_pointer = 0.0f;
                trk_line = this.aircraft.track() - this.aircraft.heading();
            } else if ( nd_gc.trk_up ) {
                // TRK UP
                mag_value = Math.round(this.aircraft.track());
                map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
                up_label = "TRK";
                hdg_pointer = this.aircraft.heading() - this.aircraft.track();
                trk_line = 0.0f;
            } else {
                // North UP
                mag_value = 999;
                map_up = 0.0f;
                up_label = " N ";
                hdg_pointer = this.aircraft.heading() - this.aircraft.magnetic_variation();
                trk_line = this.aircraft.track() - this.aircraft.magnetic_variation();
            }

    // buffered image not used (for now?)
    //        // create heading information decoration
    //        if (this.hdg_label_decoration_buf_img == null) {
    //                this.hdg_label_decoration_buf_img = create_buffered_image(200, 40);
    //                Graphics2D gImg = get_graphics(this.hdg_label_decoration_buf_img);
    //                render_heading_decoration(gImg, 200);
    //        }
    //        g2.drawImage(this.hdg_label_decoration_buf_img,
    //            nd_gc.map_center_x - (this.hdg_label_decoration_buf_img.getWidth()/2),
    //            nd_gc.border_top,
    //            null);

            if (nd_gc.boeing_style) {
            	int box_d_x = nd_gc.digit_width_xl*225/100;
            	int x_points_heading_box[] = { nd_gc.map_center_x - box_d_x, nd_gc.map_center_x - box_d_x, nd_gc.map_center_x + box_d_x, nd_gc.map_center_x + box_d_x };
            	int y_points_heading_box[] = { nd_gc.border_top + 2, nd_gc.heading_box_bottom_y, nd_gc.heading_box_bottom_y, nd_gc.border_top + 2 };

            	// TRK and MAG labels
            	g2.setColor(nd_gc.heading_labels_color);
            	g2.setFont(nd_gc.font_l);
            	g2.drawString(up_label , nd_gc.map_center_x - nd_gc.digit_width_xl*3 - nd_gc.get_text_width(g2, nd_gc.font_l, up_label), nd_gc.heading_text_y);
            	g2.drawString("MAG", nd_gc.map_center_x + nd_gc.digit_width_xl*3, nd_gc.heading_text_y);

            	// surrounding box and value
            	g2.setColor(nd_gc.top_text_color);
            	g2.drawPolyline(x_points_heading_box, y_points_heading_box, 4);
            	//g2.clearRect(center_x - 34, nd_gc.border_top, 68, heading_text_y - nd_gc.border_top);
            	g2.setFont(nd_gc.font_xxl);
            	DecimalFormat degrees_formatter = new DecimalFormat("000");
            	String text = degrees_formatter.format( mag_value );
            	if (failed_hdg) text = "- - -";
            	g2.drawString(text , center_x - 3*nd_gc.digit_width_xxl/2, nd_gc.heading_text_y);
            }

            // current heading pointer
            if ( ! nd_gc.mode_classic_hsi && ! failed_hdg) {
//                int hdg_pointer_height = (int) Math.min(16,18 * nd_gc.shrink_scaling_factor);
//                int hdg_pointer_width = (int) (10.0f * nd_gc.shrink_scaling_factor);
                int hdg_pointer_height = Math.round(30.0f * nd_gc.scaling_factor / 2.0f);
                int hdg_pointer_width = Math.round(30.0f * nd_gc.scaling_factor / 3.0f);
                int x_points_hdg_pointer[] = { center_x, center_x-hdg_pointer_width, center_x+hdg_pointer_width };
                int y_points_hdg_pointer[] = { nd_gc.rose_y_offset - 1, nd_gc.rose_y_offset - hdg_pointer_height, nd_gc.rose_y_offset - hdg_pointer_height };
                rotate(g2, hdg_pointer);
                g2.setColor(nd_gc.aircraft_color);
                if (nd_gc.boeing_style) 
                	g2.drawPolygon(x_points_hdg_pointer, y_points_hdg_pointer, 3);
                else {
                	int dx = Math.round(10.0f * nd_gc.scaling_factor / 3.0f);
                	int dy = Math.round(60.0f * nd_gc.scaling_factor / 2.0f);
                	g2.fillRect(center_x-dx, nd_gc.rose_y_offset-dy, dx*2, dy);
                }
                unrotate(g2);
            }

            

            // drift angle pointer or track line 
            // -- with map zoom indication 
            // Range label code moved to CompassRose Class
            if (!failed_hdg) {
            	rotate(g2, trk_line);
            	if ( nd_gc.mode_classic_hsi ) {
            		// old style HSI
            		// drift angle pointer in APP CTR and VOR CTR modes
            		//                int pointer_height = (int)(nd_gc.big_tick_length / 2);
            		//                int pointer_width = (int)(nd_gc.big_tick_length / 3);
            		int pointer_height = Math.round(20.0f * nd_gc.scaling_factor / 2.0f);
            		int pointer_width = Math.round(20.0f * nd_gc.scaling_factor / 3.0f);
            		g2.setColor(nd_gc.aircraft_color);
            		g2.drawLine(
            				nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius,
            				nd_gc.map_center_x + pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height
            				);
            		g2.drawLine(
            				nd_gc.map_center_x + pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height,
            				nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius + 2*pointer_height
            				);
            		g2.drawLine(
            				nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius + 2*pointer_height,
            				nd_gc.map_center_x - pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height
            				);
            		g2.drawLine(
            				nd_gc.map_center_x - pointer_width, nd_gc.map_center_y - nd_gc.rose_radius + pointer_height,
            				nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius
            				);
            	} else {
            		// map style ND
            		g2.setColor(nd_gc.heading_symbol_color);
            		int tick_halfwidth = (int)(5 * nd_gc.scaling_factor);
            		if (nd_gc.airbus_style) {
            			// Draw Drift Diamond
            			g2.drawImage(nd_gc.track_diamond_img, nd_gc.map_center_x - nd_gc.track_diamond_shift, nd_gc.heading_line_y, null);
            			// Draw line when not in NAV mode
            	        boolean fms_nav=this.avionics.ap_lnav_arm() || this.avionics.ap_lnav_on();
            	        if (this.avionics.is_qpac()) 
            	        	fms_nav = this.avionics.qpac_ap_lateral_armed() == 2 || 
            	        		this.avionics.qpac_ap_lateral_mode()== 2 || 
            	        		this.avionics.qpac_ap_lateral_mode()== 9;
            			if (!fms_nav) g2.drawLine(
            					nd_gc.map_center_x, nd_gc.map_center_y - (nd_gc.rose_radius*3/16),
            					nd_gc.map_center_x, nd_gc.track_diamond_bottom ); 	
            		} else {
            			g2.drawLine(
            					nd_gc.map_center_x, nd_gc.map_center_y - (nd_gc.rose_radius*3/16),
            					nd_gc.map_center_x, nd_gc.heading_line_y); 
            			if ( ! XHSIPreferences.get_instance().get_draw_range_arcs() ) {
            				g2.drawLine(
            						nd_gc.map_center_x - tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius*3/4),
            						nd_gc.map_center_x + tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius*3/4) );
            				g2.drawLine(
            						nd_gc.map_center_x - tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/2),
            						nd_gc.map_center_x + tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/2) );
            				g2.drawLine(
            						nd_gc.map_center_x - tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/4),
            						nd_gc.map_center_x + tick_halfwidth, nd_gc.map_center_y - (nd_gc.rose_radius/4) );
            			}
            		}
      
            	}
            	unrotate(g2);
            }

        }

    }


// buffered image not used (for now?)
//    private void render_heading_decoration(Graphics2D g2, int width) {
//
//        int x_points_heading_box[] = { (width/2)-36, (width/2)-36, (width/2)+36, (width/2)+36 };
//        int y_points_heading_box[] = { 0, 30, 30, 0 };
//
//        // TRK and MAG labels
//        g2.setColor(nd_gc.heading_labels_color);
//        g2.setFont(nd_gc.font_medium);
//        g2.drawString(new String("TRK") , (width/2) - 43 - nd_gc.get_text_width(g2, nd_gc.font_medium, "TRK"), nd_gc.line_height_medium);
//        g2.drawString("MAG" , (width/2) + 43, nd_gc.line_height_medium);
//
//        // surrounding box
//        g2.setColor(Color.LIGHT_GRAY);
//        g2.drawPolyline(x_points_heading_box, y_points_heading_box, 4);
//
//    }
 
    private void rotate(Graphics2D g2, double angle) {
        this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
            Math.toRadians(angle),
            nd_gc.map_center_x,
            nd_gc.map_center_y
        );
        g2.transform(rotate);
    }

    private void unrotate(Graphics2D g2) {
        g2.setTransform(original_at);
    }

}
