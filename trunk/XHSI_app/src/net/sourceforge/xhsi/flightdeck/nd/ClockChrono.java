/**
* ClockChrono.java
* 
* Displays the Clock or Chronograph at the bottom
* 
* Copyright (C) 2015  Marc Rogiers (marrog.123@gmail.com)
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

//import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

//import net.sourceforge.xhsi.model.Avionics;
//import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.ModelFactory;



public class ClockChrono extends NDSubcomponent {

    private static final long serialVersionUID = 1L;


    public ClockChrono(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered && this.preferences.get_nd_show_clock() ) {

            DecimalFormat hms_formatter = new DecimalFormat("00");

            String time_label;
            String time_str;
            float chr_time = this.aircraft.timer_elapsed_time();

            if ( chr_time == 0.0f ) {

                time_label = this.avionics.clock_shows_utc() ? "UTC" : "LT";
                int current_time = this.avionics.clock_shows_utc() ? (int)this.aircraft.sim_time_zulu() : (int)this.aircraft.sim_time_local();
                int hh = current_time / 3600;
                int mm = ( current_time / 60 ) % 60;
                time_str = hms_formatter.format(hh) + ":" + hms_formatter.format(mm);

            } else {

                time_label = "CHR";
                int timer = (int)chr_time;
                int mins = timer / 60 % 60;
                int secs = timer % 60;
                time_str = hms_formatter.format(mins) + ":" + hms_formatter.format(secs);

            }

            g2.setColor(nd_gc.markings_color);
            g2.setFont(nd_gc.font_s);
            int time_x = nd_gc.map_center_x + nd_gc.digit_width_s/2;
            int time_y = nd_gc.panel_rect.y + nd_gc.panel_rect.height - nd_gc.line_height_s/2;
            g2.clearRect(nd_gc.map_center_x - nd_gc.digit_width_s*6, time_y - nd_gc.line_height_s, nd_gc.digit_width_s*12, nd_gc.line_height_s*2);
            g2.drawString(time_label, time_x - nd_gc.get_text_width(g2, nd_gc.font_s, time_label) - nd_gc.digit_width_s, time_y);
            g2.drawString(time_str, time_x, time_y);

        }
        
    }


}
