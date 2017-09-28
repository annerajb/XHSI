/**
* SpeedsLabel.java
* 
* Renders aircaft groundspeed and true airspeed as well as wind direction and
* speed.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class SpeedsLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    //int wind_dir_arrow_x;
    //int wind_dir_arrow_y;
    //int wind_dir_arrow_length = 30;
    float wind_direction;
    int wind_speed;

    private DecimalFormat degrees_formatter;


    public SpeedsLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
        super(model_factory, hsi_gc);
        degrees_formatter = new DecimalFormat("000");
        // Out of range value that must be refreshed
        wind_direction = 361.0f;
        wind_speed = -1;
    }


    public void paint(Graphics2D g2) {
        if ( nd_gc.powered ) {
        	
            if ( ( wind_speed != (int) Math.round( aircraft_environment.wind_speed() )) || 
                 (Math.abs(wind_direction - aircraft_environment.wind_direction())>1.0f) ) {
            	// Must refresh display
                wind_speed = (int) Math.round( aircraft_environment.wind_speed() );
                wind_direction = aircraft_environment.wind_direction();
            }
            drawSpeeds(g2);
        }
    }


    private void drawSpeeds(Graphics2D g2) {

        wind_speed = (int) Math.round( aircraft_environment.wind_speed() );
        wind_direction = aircraft_environment.wind_direction();
        float map_up;
        if ( nd_gc.hdg_up ) {
            // HDG UP
            map_up = this.aircraft.heading();
        } else if ( nd_gc.trk_up ) {
            // TRK UP
            map_up = this.aircraft.track() - this.aircraft.magnetic_variation();
        } else {
            // North UP
            map_up = 0.0f;
        }

        g2.setColor(nd_gc.top_text_color);
        g2.setFont(this.nd_gc.font_s);
        g2.drawString("GS", nd_gc.sl_gs_label_x, nd_gc.sl_speeds_y);
        g2.setFont(this.nd_gc.font_l);
        g2.drawString("" + Math.round(aircraft.ground_speed()), nd_gc.sl_gs_x, nd_gc.sl_speeds_y);

        g2.setFont(this.nd_gc.font_s);
        g2.drawString("TAS", nd_gc.sl_tas_label_x, nd_gc.sl_speeds_y);
        g2.setFont(this.nd_gc.font_l);
        g2.drawString("" + Math.round(aircraft.true_air_speed()), nd_gc.sl_tas_x, nd_gc.sl_speeds_y);

        g2.setColor(nd_gc.wind_color);
        String wind_text = null;
        int wind_dir = Math.round(wind_direction + this.aircraft.magnetic_variation());
        if (wind_dir < 0) {
            wind_dir += 360;
        }
        wind_dir %= 360;
        if (wind_dir == 0) {
            wind_dir = 360;
        }
        if (wind_speed > 4) {
            wind_text = degrees_formatter.format(wind_dir) + "\u00B0" + "/" + wind_speed;
        } else {
            wind_text = "---\u00B0" + "/--";
        }
        g2.drawString(wind_text, nd_gc.sl_wind_x, nd_gc.sl_wind_y);

        // wind direction arrow
        if (wind_speed > 4) {
            
            g2.clearRect(0, nd_gc.sl_wind_y, nd_gc.sl_wind_x + nd_gc.sl_wind_dir_arrow_length*10/8, nd_gc.sl_wind_dir_arrow_length*11/8);

            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2.0f * nd_gc.scaling_factor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            AffineTransform original_at = null;
            original_at = g2.getTransform();
            AffineTransform rotate = AffineTransform.getRotateInstance(Math.toRadians((double) (wind_direction - map_up + this.aircraft.magnetic_variation())),
            		nd_gc.sl_wind_dir_arrow_cx,
            		nd_gc.sl_wind_dir_arrow_cy);
            g2.transform(rotate);

//            GeneralPath polyline;
//            polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 2);
//            polyline.moveTo (wind_dir_arrow_cx, wind_dir_arrow_cy - (wind_dir_arrow_length/2));
//            polyline.lineTo(wind_dir_arrow_cx, wind_dir_arrow_cy + (wind_dir_arrow_length/2));
//            polyline.lineTo(wind_dir_arrow_cx + 5, wind_dir_arrow_cy + (wind_dir_arrow_length/2) - 5);
//            g2.draw(polyline);
//            polyline = new GeneralPath(GeneralPath.WIND_NON_ZERO, 2);
//            polyline.moveTo (wind_dir_arrow_cx, wind_dir_arrow_cy - (wind_dir_arrow_length/2));
//            polyline.lineTo(wind_dir_arrow_cx, wind_dir_arrow_cy + (wind_dir_arrow_length/2));
//            polyline.lineTo(wind_dir_arrow_cx - 5, wind_dir_arrow_cy + (wind_dir_arrow_length/2) - 5);
//            g2.draw(polyline);
            g2.drawLine(nd_gc.sl_wind_dir_arrow_cx, nd_gc.sl_wind_dir_arrow_cy - (nd_gc.sl_wind_dir_arrow_length/2), nd_gc.sl_wind_dir_arrow_cx, nd_gc.sl_wind_dir_arrow_cy + (nd_gc.sl_wind_dir_arrow_length/2));
            g2.drawLine(nd_gc.sl_wind_dir_arrow_cx, nd_gc.sl_wind_dir_arrow_cy + (nd_gc.sl_wind_dir_arrow_length/2), nd_gc.sl_wind_dir_arrow_cx + nd_gc.sl_arrow_head, nd_gc.sl_wind_dir_arrow_cy + (nd_gc.sl_wind_dir_arrow_length/2) - nd_gc.sl_arrow_head);
            g2.drawLine(nd_gc.sl_wind_dir_arrow_cx, nd_gc.sl_wind_dir_arrow_cy + (nd_gc.sl_wind_dir_arrow_length/2), nd_gc.sl_wind_dir_arrow_cx - nd_gc.sl_arrow_head, nd_gc.sl_wind_dir_arrow_cy + (nd_gc.sl_wind_dir_arrow_length/2) - nd_gc.sl_arrow_head);
            
            g2.setTransform(original_at);
            g2.setStroke(original_stroke);

        }

    }


}
