/**
* CompassRose.java
* 
* Renders the visible compass rose in all modes
* Including : 
*    - Aircraft Symbol
*    - Range arc or circle
*    - Range labels
*  
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2017  Nicolas Carel
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


import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.flightdeck.nd.NDFramedElement.FE_Color;
import net.sourceforge.xhsi.flightdeck.pfd.PFDFramedElement;
import net.sourceforge.xhsi.flightdeck.pfd.PFDFramedElement.PFE_Color;
import net.sourceforge.xhsi.model.ModelFactory;


public class CompassRose extends NDSubcomponent {
    
    private static final long serialVersionUID = 1L;

    public static boolean USE_BUFFERED_IMAGE = true;
    
    private Graphics2D g2_rose; 
    
    private float drawn_map_up;
    private long refreshed_timestamp;
    private boolean failed_hsi;
    NDFramedElement failed_flag;
    
    float range_dashes[] = { 10.0f, 10.0f };
    
    public CompassRose(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
        super(model_factory, hsi_gc);
        this.refreshed_timestamp=0;
        this.drawn_map_up=0.0f;
        this.failed_hsi=false;
        failed_flag = new NDFramedElement(NDFramedElement.HDG_FLAG, 0, hsi_gc, FE_Color.FE_COLOR_ALARM);
        failed_flag.enableFlashing();
        failed_flag.disableFraming();
        failed_flag.setBigFont(true);
    }


    public int round_to_five(float number) {
        return Math.round(number / 5) * 5;
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {
        	
        	boolean refresh_rose = (nd_gc.colors_updated | (nd_gc.reconfigured_timestamp > this.refreshed_timestamp));
            float map_up;
            if ( nd_gc.hdg_up ) {
                // HDG UP
                map_up = this.aircraft.heading();
            } else if ( nd_gc.trk_up ) {
                // TRK UP
                map_up = this.aircraft.track();
            } else {
                // North UP
                map_up = 0.0f;
            }
            
            if (Math.abs(map_up-this.drawn_map_up)*nd_gc.pixels_per_deg > 0.9f) {
            	refresh_rose=true;
            	this.drawn_map_up=map_up;
            } 
        	
			
            if (failed_hsi != (!this.avionics.hdg_valid() || (! XHSIStatus.receiving && nd_gc.airbus_style ))) {
            	// FCOM 1.31.40 p26 (18) 
            	// if the heading information fails, the HDG flag replaces the heading scale (red)
            	refresh_rose=true;
            	failed_hsi=!this.avionics.hdg_valid() || (! XHSIStatus.receiving && nd_gc.airbus_style );
            	if (failed_hsi) {
            		failed_flag.setText(nd_gc.airbus_style ? "HDG" : "MAP", nd_gc.airbus_style ? FE_Color.FE_COLOR_ALARM : FE_Color.FE_COLOR_CAUTION);
            		if (nd_gc.boeing_style)
            			failed_flag.enableFraming();
            		else
            			failed_flag.disableFraming();
            	} else {
            		failed_flag.clearText();
            	}
            }
            
        	if (USE_BUFFERED_IMAGE) {
        		g2_rose = nd_gc.compass_rose_img.createGraphics();
        		g2_rose.setRenderingHints(nd_gc.rendering_hints);
        		g2_rose.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        		if (refresh_rose) {
        			// Clear the buffered Image first
        			g2_rose.setComposite(AlphaComposite.Clear);
        			g2_rose.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
        			g2_rose.setComposite(AlphaComposite.SrcOver);
        		}
        	} else {
        		g2_rose=g2;
        		refresh_rose=true;
        	}
        	
        	if (refresh_rose) {

        		this.refreshed_timestamp =  nd_gc.current_time_millis;
        				
        		// draw the scale rings before drawing the map
        		if ( ( ! nd_gc.mode_plan ) && ( this.preferences.get_draw_range_arcs() ) && ! nd_gc.mode_classic_hsi )
        			draw_scale_rings(g2_rose);


        		float left_right_angle = nd_gc.half_view_angle;
        		if ( ! nd_gc.mode_plan && ! nd_gc.mode_centered && this.preferences.get_draw_only_inside_rose() && this.preferences.get_limit_arcs_at_60() ) {
        			left_right_angle = 60.0f;
        		}
        		if ( nd_gc.mode_centered ) left_right_angle = 180.0f;
     			
        		g2_rose.setColor(nd_gc.markings_color);
        		
        		if ( ! nd_gc.mode_centered ) {
        			draw_main_arc(g2_rose, left_right_angle);
        		} else if (nd_gc.mode_plan || nd_gc.airbus_style){
        			draw_main_circles(g2_rose);
        		}

        		if ( ! nd_gc.mode_plan ) {

        			if ( !failed_hsi ) draw_compass_ticks( g2_rose, left_right_angle, this.drawn_map_up);

        			if ( nd_gc.mode_centered ) {
        				draw_45_deg_marks(g2_rose);
        			} else {
        				// in expanded mode, clip left and right
        				if ( this.preferences.get_draw_only_inside_rose() && this.preferences.get_limit_arcs_at_60() ) {
        					g2_rose.clearRect(0, 0, nd_gc.map_center_x - nd_gc.sixty_deg_hlimit, nd_gc.frame_size.height);
        					g2_rose.clearRect(nd_gc.map_center_x + nd_gc.sixty_deg_hlimit, 0, nd_gc.map_center_x - nd_gc.sixty_deg_hlimit, nd_gc.frame_size.height);
        				}
        			}

        			if (failed_hsi && nd_gc.airbus_style) {    
        				g2_rose.setColor(nd_gc.warning_color);	
        		        g2.drawOval(
        		                nd_gc.map_center_x - nd_gc.rose_radius/20,
        		                nd_gc.map_center_y - nd_gc.rose_radius/20,
        		                nd_gc.rose_radius/10,
        		                nd_gc.rose_radius/10
        		        );        				
        			} else {
        				draw_plane_symbol(g2_rose);
        			}
        			

        			if (!nd_gc.mode_classic_hsi || nd_gc.airbus_style) draw_range_label(g2_rose);  

        		} else { 
        			// nd_gc.mode_plan
        			draw_range_label_plan(g2_rose);
        			draw_cardinal_winds(g2_rose);
        		}
        	}
        	
        	if (USE_BUFFERED_IMAGE) {
        		// paint buffer
        		g2.drawImage(nd_gc.compass_rose_img, 0, 0, null);
        	}
        	
        	if (failed_hsi) failed_flag.paint(g2);

        }

    }

    private void draw_main_arc(Graphics2D g2, float left_right_angle) {
		if (failed_hsi && !nd_gc.mode_plan && nd_gc.airbus_style) {
			g2_rose.setColor(nd_gc.warning_color);	
		} else {        			
			g2_rose.setColor(nd_gc.markings_color);
		}      	
    	g2.drawArc(
    			nd_gc.map_center_x - nd_gc.rose_radius,
    			nd_gc.map_center_y - nd_gc.rose_radius,
    			nd_gc.rose_radius*2,
    			nd_gc.rose_radius*2,
    			(int)(left_right_angle + 90.0f),
    			(int)(left_right_angle * -2.0f)
    			);
    }
    
    private void draw_main_circles(Graphics2D g2) {
        // circle
		if (failed_hsi && !nd_gc.mode_plan && nd_gc.airbus_style) {
			g2_rose.setColor(nd_gc.warning_color);	
		} else {        			
			g2_rose.setColor(nd_gc.markings_color);
		}  
        g2.drawOval(
                nd_gc.map_center_x - nd_gc.rose_radius,
                nd_gc.map_center_y - nd_gc.rose_radius,
                nd_gc.rose_radius*2,
                nd_gc.rose_radius*2
        );
        // inner circle
		if (failed_hsi && !nd_gc.mode_plan && nd_gc.airbus_style) {
			g2_rose.setColor(nd_gc.warning_color);	
		} else {        			
			g2_rose.setColor(nd_gc.range_arc_color);
		}  
        Stroke original_stroke = g2.getStroke();
        if (nd_gc.airbus_style) g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, range_dashes, 0.0f));
        g2.drawOval(
                nd_gc.map_center_x - nd_gc.rose_radius/2,
                nd_gc.map_center_y - nd_gc.rose_radius/2,
                nd_gc.rose_radius,
                nd_gc.rose_radius
        );
        g2.setStroke(original_stroke);	
    }
    
    private void draw_compass_ticks(Graphics2D g2, float left_right_angle, float map_up){
        // Compass rose for all modes except PLAN

        int min_visible_heading = round_to_five(this.aircraft.track() - left_right_angle + 2.5f);
        int max_visible_heading = round_to_five(this.aircraft.track() + left_right_angle - 2.5f);

        /*
        float map_up;
        if ( nd_gc.hdg_up ) {
            // HDG UP
            map_up = this.aircraft.heading();
        } else if ( nd_gc.trk_up ) {
            // TRK UP
            map_up = this.aircraft.track();
        } else {
            // North UP
            map_up = 0.0f;
        }
        */

        double rotation_offset = (-1.0f * left_right_angle)  + (min_visible_heading - (map_up - left_right_angle));

        AffineTransform original_at = g2.getTransform();

        // rotate according to horizontal path
        AffineTransform rotate_to_heading = AffineTransform.getRotateInstance(
                Math.toRadians(rotation_offset),
                nd_gc.map_center_x,
                nd_gc.map_center_y
        );
        g2.transform(rotate_to_heading);

        Graphics g = (Graphics) g2;
        int tick_length = 0;
        g2.setFont(nd_gc.font_m); // was: medium
        for (int angle = min_visible_heading; angle <= max_visible_heading; angle += 5) {
        	if (angle % 30 == 0) {
        		tick_length = nd_gc.big_tick_length;
        	} else if (angle % 10 == 0) {
                tick_length = nd_gc.medium_tick_length;
            } else {
                tick_length = nd_gc.small_tick_length;
            }
            g.drawLine(nd_gc.map_center_x, nd_gc.rose_y_offset + 1,
                       nd_gc.map_center_x, nd_gc.rose_y_offset + tick_length);

            String text = "";
            if (angle < 0) {
                text = "" + (angle + 360)/10;
            } else if (angle >=360) {
                text = "" + (angle - 360)/10;
            } else {
                text = "" + angle/10;
            }
            if (angle % 30 == 0) {
                int text_width;
                if (text.length() == 1)
                    text_width = nd_gc.compass_one_digit_hdg_text_width;
                else
                    text_width = nd_gc.compass_two_digit_hdg_text_width;
                g2.setFont(nd_gc.compass_text_font);

                g.drawString(
                        text,
                        nd_gc.map_center_x - (text_width/2),
                        //nd_gc.rose_y_offset + tick_length + nd_gc.compass_hdg_text_height
                        nd_gc.tick_text_y_offset);
            } else if (nd_gc.airbus_style && (angle % 10 == 0) && !nd_gc.mode_centered) {                    	
            	// Draw each 10° text label in smaller font (Airbus only) for mode ARC
                int text_width;
                if (text.length() == 1)
                    text_width = nd_gc.compass_one_digit_hdg_small_text_width;
                else
                    text_width = nd_gc.compass_two_digit_hdg_small_text_width;
                g2.setFont(nd_gc.compass_small_text_font);

                g.drawString(
                        text,
                        nd_gc.map_center_x - (text_width/2),
                        //nd_gc.rose_y_offset + tick_length + nd_gc.compass_hdg_small_text_height
                        nd_gc.tick_text_y_offset);
            	
            }

            AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians(5.0),
                    nd_gc.map_center_x,
                    nd_gc.map_center_y);
            g2.transform(rotate);

        }
        g2.setTransform(original_at);
    }
    
    private void draw_45_deg_marks(Graphics2D g2) {
        // 45 degrees marks for APP CTR, VOR CTR and MAP CTR
        int mark_length = nd_gc.big_tick_length;
    	AffineTransform original_at = g2.getTransform();
        g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius - mark_length, nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius);
        g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius + mark_length);
        g2.drawLine(nd_gc.map_center_x - nd_gc.rose_radius - mark_length, nd_gc.map_center_y, nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y);
        g2.drawLine(nd_gc.map_center_x + nd_gc.rose_radius, nd_gc.map_center_y, nd_gc.map_center_x + nd_gc.rose_radius + mark_length, nd_gc.map_center_y);
        g2.transform(AffineTransform.getRotateInstance(Math.toRadians(45.0), nd_gc.map_center_x, nd_gc.map_center_y));
        g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius - mark_length, nd_gc.map_center_x, nd_gc.map_center_y - nd_gc.rose_radius);
        g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius, nd_gc.map_center_x, nd_gc.map_center_y + nd_gc.rose_radius + mark_length);
        g2.drawLine(nd_gc.map_center_x - nd_gc.rose_radius - mark_length, nd_gc.map_center_y, nd_gc.map_center_x - nd_gc.rose_radius, nd_gc.map_center_y);
        g2.drawLine(nd_gc.map_center_x + nd_gc.rose_radius, nd_gc.map_center_y, nd_gc.map_center_x + nd_gc.rose_radius + mark_length, nd_gc.map_center_y);
        g2.setTransform(original_at);
    }
    
    
    
    private void draw_cardinal_winds(Graphics2D g2){
    	g2.setColor(nd_gc.cardinal_labels_color);
    	g2.setFont(nd_gc.cardinal_labels_font);
    	g2.drawString("N", nd_gc.cardinal_vert_x, nd_gc.cardinal_N_y);
    	g2.drawString("E", nd_gc.cardinal_E_x, nd_gc.cardinal_horiz_y);
    	g2.drawString("S", nd_gc.cardinal_vert_x, nd_gc.cardinal_S_y);
    	g2.drawString("W", nd_gc.cardinal_W_x, nd_gc.cardinal_horiz_y);
    	if (nd_gc.airbus_style) {
    		// draw triangles
    		g2.fill(nd_gc.cardinal_tri_N);
    		g2.fill(nd_gc.cardinal_tri_S);
    		g2.fill(nd_gc.cardinal_tri_E);
    		g2.fill(nd_gc.cardinal_tri_W);
    	}
    }

    private void draw_plane_symbol(Graphics2D g2) {
        // plane symbol
        int center_x = this.nd_gc.map_center_x;
        int center_y = this.nd_gc.map_center_y;
        int plane_width = Math.round(20.0f * nd_gc.scaling_factor);
        int plane_height = Math.round(30.0f * nd_gc.scaling_factor);
        g2.setColor(nd_gc.aircraft_color);
        if (nd_gc.airbus_style) {
        	draw_airbus_aircraft_symbol(g2, center_x, center_y);
        } else { 
        	if ( nd_gc.mode_classic_hsi ) {
        		g2.drawLine(center_x - plane_width/4, center_y - plane_height, center_x - plane_width/4, center_y + plane_height);
        		g2.drawLine(center_x + plane_width/4, center_y - plane_height, center_x + plane_width/4, center_y + plane_height);
        		g2.drawLine(center_x - plane_height, center_y, center_x - plane_width/4, center_y);
        		g2.drawLine(center_x + plane_width/4, center_y, center_x + plane_height, center_y);
        		g2.drawLine(center_x - plane_width/2 - plane_width/4, center_y + plane_height, center_x - plane_width/4, center_y + plane_height);
        		g2.drawLine(center_x + plane_width/4, center_y + plane_height, center_x + plane_width/2 + plane_width/4, center_y + plane_height);
        	} else {
        		int x_points_airplane_symbol[] = { center_x, center_x - (plane_width/2), center_x + (plane_width/2) };
        		int y_points_airplane_symbol[] = { center_y, center_y + plane_height, center_y + plane_height };
        		g2.drawPolygon(x_points_airplane_symbol, y_points_airplane_symbol, 3);
        	}
        }
    }
    
    private void draw_airbus_aircraft_symbol(Graphics2D g2, int px, int py) {
        int ps = Math.round(14.0f * nd_gc.scaling_factor);
        int cy = 80;
        
        int dx = 10;
        int dy = 20;
        int wing_x = 95;
        int tail_x = 35;
        int wing_y = cy-dy/2;
        int tail_y = 165;
        int b_y = 205;

        int plan_x[] = {
        	 dx * ps / 50 + px,
             dx * ps / 50 + px,
             wing_x * ps / 50 + px,
             wing_x * ps / 50 + px,
             dx * ps / 50 + px,
             dx * ps / 50 + px,
             tail_x * ps / 50 + px,
             tail_x * ps / 50 + px,
             dx * ps / 50 + px,
             dx * ps / 50 + px,
            -dx * ps / 50 + px,
            -dx * ps / 50 + px,
            -tail_x * ps / 50 + px,
            -tail_x * ps / 50 + px,
            -dx * ps / 50 + px,
            -dx * ps / 50 + px,
            -wing_x * ps / 50 + px,
            -wing_x * ps / 50 + px,
            -dx * ps / 50 + px,
            -dx * ps / 50 + px
        };
        int plan_y[] = {
            ( 0 - cy ) * ps / 50 + py,
            ( wing_y - cy ) * ps / 50 + py,
            ( wing_y - cy ) * ps / 50 + py,
            ( wing_y + dy - cy ) * ps / 50 + py,
            ( wing_y + dy - cy ) * ps / 50 + py,
            ( tail_y - cy ) * ps / 50 + py,
            ( tail_y - cy ) * ps / 50 + py,
            ( tail_y + dy - cy ) * ps / 50 + py,
            ( tail_y + dy - cy ) * ps / 50 + py,
            ( b_y - cy ) * ps / 50 + py,
            ( b_y - cy ) * ps / 50 + py,
            ( tail_y + dy - cy ) * ps / 50 + py,
            ( tail_y + dy- cy ) * ps / 50 + py,
            ( tail_y  - cy ) * ps / 50 + py,
            ( tail_y  - cy ) * ps / 50 + py,
            ( wing_y + dy - cy ) * ps / 50 + py,
            ( wing_y + dy - cy ) * ps / 50 + py,
            ( wing_y - cy ) * ps / 50 + py,
            ( wing_y - cy ) * ps / 50 + py,
            ( 0 - cy ) * ps / 50 + py
        };
        g2.setColor(nd_gc.aircraft_color);
        g2.fillPolygon(plan_x, plan_y, 20);
    }

    
    private void draw_range_label_plan(Graphics2D g2) {
        String ctr_ranges[] = {"2.5", "5", "10", "20", "40", "80", "160"};
        String exp_ranges[] = {"5", "10", "20", "40", "80", "160", "320"};
        String zoomin_ctr_ranges[] = {"0.025", "0.05", "0.10", "0.20", "0.40", "0.80", "1.60"};
        String zoomin_exp_ranges[] = {"0.05", "0.10", "0.20", "0.40", "0.80", "1.60", "3.20"};
        String range_text;
        g2.setFont(nd_gc.range_label_font);
        g2.setColor(nd_gc.range_label_color);
        // g2.setColor(nd_gc.dim_markings_color);
        int range_index = this.avionics.map_range_index();
        if ( nd_gc.map_zoomin )
            range_text = zoomin_exp_ranges[range_index];
        else
            range_text = exp_ranges[range_index];
        int text_half_witdh = nd_gc.get_text_width(g2, nd_gc.range_label_font, range_text) / 2;
        
        g2.drawString(
        			range_text,
        			nd_gc.range_label_full_x - text_half_witdh,
        			nd_gc.range_label_full_y
        			);
    
        if ( nd_gc.map_zoomin )
            range_text = zoomin_ctr_ranges[range_index];
        else
            range_text = ctr_ranges[range_index];
        g2.drawString(
            range_text,
            nd_gc.range_label_half_x - text_half_witdh,
            nd_gc.range_label_half_y
        );
    }
    
    private void draw_range_label(Graphics2D g2) {

    	// conditions ! nd_gc.mode_classic_hsi && ! nd_gc.mode_plan 
    	
        // a label at half the range
        g2.setFont(nd_gc.range_label_font);
        g2.setColor(nd_gc.range_label_color);

//        int range = nd_gc.map_range;
        
        String ctr_ranges[] = {"2.5", "5", "10", "20", "40", "80", "160"};
        String exp_ranges[] = {"5", "10", "20", "40", "80", "160", "320"};
        String zoomin_ctr_ranges[] = {"0.025", "0.05", "0.10", "0.20", "0.40", "0.80", "1.60"};
        String zoomin_exp_ranges[] = {"0.05", "0.10", "0.20", "0.40", "0.80", "1.60", "3.20"};
        
        String x737_ctr_ranges[] = {"1.25", "2.5", "5", "10", "20", "40", "80", "160"};
        String x737_exp_ranges[] = {"2.5", "5", "10", "20", "40", "80", "160", "320"};
        String x737_zoomin_ctr_ranges[] = {"0.0125", "0.025", "0.05", "0.10", "0.20", "0.40", "0.80", "1.60"};
        String x737_zoomin_exp_ranges[] = {"0.025", "0.05", "0.10", "0.20", "0.40", "0.80", "1.60", "3.20"};
        
        String range_text;
        int range_index = this.avionics.map_range_index();
        if ( this.avionics.is_x737() ) {
            if ( nd_gc.mode_centered ) {
                if ( nd_gc.map_zoomin ) {
                   range_text = x737_zoomin_ctr_ranges[range_index];
                } else {
                   range_text = x737_ctr_ranges[range_index];
                }
            } else {
                if ( nd_gc.map_zoomin ) {
                    range_text = x737_zoomin_exp_ranges[range_index];
                } else {
                    range_text = x737_exp_ranges[range_index];
                }
            }
        } else {
            if ( nd_gc.mode_centered ) {
                if ( nd_gc.map_zoomin ) {
                   range_text = zoomin_ctr_ranges[range_index];
                } else {
                   range_text = ctr_ranges[range_index];
                }
            } else {
                if ( nd_gc.map_zoomin ) {
                    range_text = zoomin_exp_ranges[range_index];
                } else {
                    range_text = exp_ranges[range_index];
                }
            }
        }
        g2.drawString(
            range_text,
            nd_gc.map_center_x - nd_gc.get_text_width(g2, nd_gc.font_xs, range_text) - 4,
            nd_gc.map_center_y - (nd_gc.rose_radius / 2) - (nd_gc.get_text_height(g2, g2.getFont()) / 2) + 5
        );
    }

    private void draw_scale_rings(Graphics2D g2) {

        // dim or dash the scale rings in Arc mode
		if (failed_hsi && !nd_gc.mode_plan) {
			g2_rose.setColor(nd_gc.warning_color);	
		} else {        			
			g2_rose.setColor(nd_gc.range_arc_color);
		}    
        
        Stroke original_stroke = g2.getStroke();
        if (nd_gc.airbus_style) g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, range_dashes, 0.0f));

        for ( int i=1; i<=3; i++ ) {
            int radius = i * nd_gc.rose_radius/4;
            if ( nd_gc.mode_centered || nd_gc.mode_plan ) {
                if (nd_gc.boeing_style || i==2) g2.drawOval( nd_gc.map_center_x - radius, nd_gc.map_center_y - radius, radius*2, radius*2 );
            } else {
                if ( this.preferences.get_draw_only_inside_rose() && this.preferences.get_limit_arcs_at_60() ) {
                    g2.draw(new Arc2D.Float( nd_gc.map_center_x - radius, nd_gc.map_center_y - radius, radius*2, radius*2, 30.0f, 120.0f, Arc2D.OPEN ) );
                } else {
                    g2.draw(new Arc2D.Float( nd_gc.map_center_x - radius, nd_gc.map_center_y - radius, radius*2, radius*2, 0.0f, 180.0f, Arc2D.OPEN ) );
                }
            }
        }
        g2.setStroke(original_stroke);

    }
    
}
