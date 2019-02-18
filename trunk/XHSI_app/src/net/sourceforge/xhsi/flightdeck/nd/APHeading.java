/**
* APHeading.java
* 
* Renders autopilot heading bug and line from airplane symbol to heading bug.
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

import java.awt.Graphics2D;
import java.awt.Stroke;

import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;


public class APHeading extends NDSubcomponent {

	private static final long serialVersionUID = 1L;
	float dash[] = { 11.0f, 22.0f };
	private boolean failed_hdg;
	private DecimalFormat degrees_formatter;
	
	public APHeading(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
        this.failed_hdg=false;
        degrees_formatter = new DecimalFormat("000");
        
	}
		
	public void paint(Graphics2D g2) {

            if ( nd_gc.powered && ! nd_gc.mode_plan ) {
            	
                if (failed_hdg != (!this.avionics.hdg_valid() || (! XHSIStatus.receiving && nd_gc.airbus_style ))) {        	
                	failed_hdg=!this.avionics.hdg_valid() || (! XHSIStatus.receiving && nd_gc.airbus_style );                	
                }

                // GeneralPath polyline = null;

                float map_up;
                if ( nd_gc.hdg_up ) {
                    // HDG UP
                    map_up = this.aircraft.heading() - this.aircraft.magnetic_variation();
                } else if ( nd_gc.trk_up ) {
                    // TRK UP
                    map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
                } else {
                    // North UP
                    map_up = 0.0f;
                }
                float ap_heading_offset = map_up - ( avionics.heading_bug() - this.aircraft.magnetic_variation() );


                if (!failed_hdg) {
                    // rotate according to heading bug
                    AffineTransform original_at = g2.getTransform();
                    g2.rotate(
                            Math.toRadians((double) (-1 * ap_heading_offset)),
                            nd_gc.map_center_x,
                            nd_gc.map_center_y
                    );
                    
                    boolean hdg_bug_on = true;
                    
            		if (this.avionics.is_qpac() && this.avionics.qpac_fcu_hdg_managed()) {
            			hdg_bug_on = false;
            			g2.setColor(nd_gc.pfd_managed_color);
            		} else if (this.avionics.is_jar_a320neo() && this.avionics.jar_a320neo_fcu_hdg_managed()) {
            			hdg_bug_on = false;
            			g2.setColor(nd_gc.pfd_managed_color);			
            		} else {
            			g2.setColor(nd_gc.heading_bug_color);	
            		}
            		
            		if (nd_gc.airbus_style && !nd_gc.mode_centered && ( ap_heading_offset > nd_gc.heading_bug_display_limit || ap_heading_offset < -nd_gc.heading_bug_display_limit )) {
            			hdg_bug_on = false;
            		}
            		
                	// heading bug
            		if (hdg_bug_on) {
            			
            			g2.setColor(nd_gc.heading_bug_color);
            			g2.draw(nd_gc.heading_bug_polyline);
            			if ( this.avionics.ap_hdg_sel_on() || ( this.avionics.is_x737() && this.avionics.x737_hdg() > 0 ) ) {
            				g2.fill(nd_gc.heading_bug_polyline);
            			}

            		}

                	if ( ! nd_gc.mode_classic_hsi && nd_gc.boeing_style ) {
                		// dotted line from plane to heading bug, not for APP CTR or VOR CTR
                		Stroke original_stroke = g2.getStroke();
                		g2.setStroke(nd_gc.heading_bug_stroke);
                		g2.drawLine(nd_gc.map_center_x, nd_gc.map_center_y, nd_gc.map_center_x, nd_gc.rose_y_offset);
                		g2.setStroke(original_stroke);
                	}

                	// heading value
                	String str_bug = degrees_formatter.format(Math.round(this.avionics.heading_bug()));
                	if (ap_heading_offset > nd_gc.heading_bug_display_limit && nd_gc.airbus_style && !nd_gc.mode_centered ) {
                		g2.setFont(nd_gc.font_xxl);
                		g2.setTransform(original_at);
                		g2.rotate(Math.toRadians(-nd_gc.heading_bug_display_limit),nd_gc.map_center_x,nd_gc.map_center_y);
                		g2.drawString(str_bug, nd_gc.heading_bug_value_x, nd_gc.heading_bug_value_y);
                	} else if (ap_heading_offset < -nd_gc.heading_bug_display_limit && nd_gc.airbus_style && !nd_gc.mode_centered ) {
                		g2.setFont(nd_gc.font_xxl);
                		g2.setTransform(original_at);
                		g2.rotate(Math.toRadians(nd_gc.heading_bug_display_limit),nd_gc.map_center_x,nd_gc.map_center_y);
                		g2.drawString(str_bug, nd_gc.heading_bug_value_x, nd_gc.heading_bug_value_y);
                	} 
    				
                	// reset transformation
                	g2.setTransform(original_at);
                }

            }

	}	

}
