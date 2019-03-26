/**
* DestinationLabel.java
* 
* Renders the next waypoint information in the top right corner of the HSI
* with information about the name of the next waypoint, the time of arrival 
* in zulu time and the distance to the waypoint in nautical miles.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMS;
//import de.georg_gruetter.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObject;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.util.TimedFilter;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;


public class DestinationLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    DecimalFormat eta_hours_formatter;
    DecimalFormat eta_minutes_formatter;
    DecimalFormat ete_minutes_formatter;
    DecimalFormat integer_formatter;
    DecimalFormat one_decimal_formatter;
    DecimalFormatSymbols format_symbols;
    TimedFilter timed_filter;

    BufferedImage buf_image;
    boolean destination_active = false;


    public DestinationLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {

        super(model_factory, hsi_gc, parent_component);

        eta_hours_formatter = new DecimalFormat("00");
        eta_minutes_formatter = new DecimalFormat("00.0");
        ete_minutes_formatter = new DecimalFormat("0.0");
        integer_formatter = new DecimalFormat("0");
        one_decimal_formatter = new DecimalFormat("0.0");
        format_symbols = one_decimal_formatter.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_formatter.setDecimalFormatSymbols(format_symbols);
        eta_minutes_formatter.setDecimalFormatSymbols(format_symbols);
        ete_minutes_formatter.setDecimalFormatSymbols(format_symbols);

        timed_filter = new TimedFilter(250);      // recalculate data only every 250 ms

    }


    public void paint(Graphics2D g2) {

        NavigationObject dest_navobj = null;
        String dest_name = "";
        float dest_ete = 0.0f;
        long dest_eta = 0;
        float dest_dist = 0.0f;

        int buf_img_width = -1;
        int buf_img_height = -1;

        if (timed_filter.time_to_perform()) {
        	
            // create a new image when the timer triggers

            buf_img_width = nd_gc.max_char_advance_l * 8;
            buf_img_height = nd_gc.line_height_l * 5;

            int source = this.avionics.hsi_source();
            // The label should always show the next FMC waypoint in MAP, NAV and PLN modes
            if ( ( this.avionics.map_submode() == Avionics.EFIS_MAP_MAP ) ||( this.avionics.map_submode() == Avionics.EFIS_MAP_NAV ) || ( this.avionics.map_submode() == Avionics.EFIS_MAP_PLN ) ) {
                source = Avionics.HSI_SOURCE_GPS;
            }

            if ( source == Avionics.HSI_SOURCE_NAV1 ) {
                NavigationRadio radio = this.avionics.get_nav_radio(1);
                if ( radio.receiving() ) {
                    dest_navobj = radio.get_radio_nav_object();
                    dest_ete = radio.get_ete();
                    dest_dist = radio.get_distance();
                    if (dest_navobj != null) {
                        this.destination_active = true;
                        dest_name = ((RadioNavigationObject)dest_navobj).ilt;
                    }
                }
            } else if ( source == Avionics.HSI_SOURCE_NAV2 ) {
                NavigationRadio radio = this.avionics.get_nav_radio(2);
                if ( radio.receiving() ) {
                    dest_navobj = radio.get_radio_nav_object();
                    dest_ete = radio.get_ete();
                    dest_dist = radio.get_distance();
                    if (dest_navobj != null) {
                        this.destination_active = true;
                        dest_name = ((RadioNavigationObject)dest_navobj).ilt;
                    }
                }
            } else if ( source == Avionics.HSI_SOURCE_GPS ) {
//                // This is only valid for the legacy FMS
//                NavigationRadio radio = this.avionics.get_gps_radio();
//                FMS the_fms = FMS.get_instance();
//                if ( the_fms.is_active() ) {
//                    dest_navobj = (NavigationObject)the_fms.get_active_waypoint();
//                    dest_ete = radio.get_ete();
//                    dest_dist = radio.get_distance();
//                    if (dest_navobj != null) dest_name = dest_navobj.name;
//                }
                // This should work with the GNS 430/530 *and* the legacy FMS
                NavigationRadio radio = this.avionics.get_gps_radio();
                dest_ete = radio.get_ete();
                dest_dist = radio.get_distance();
                dest_name = this.avionics.gps_nav_id();
                this.destination_active = ( ! dest_name.equals("") );
            }

//            this.destination_active = (dest_navobj != null);

            if ( this.destination_active ) {

                //see above: dest_name = ...;
                //see above: dest_dist = this.aircraft.distance_to(dest_navobj);
                //see above: dest_ete = this.aircraft.ete_for_distance(dest_dist);
                dest_eta = this.aircraft.time_after_ete(dest_ete);

                this.buf_image = create_buffered_image(buf_img_width, buf_img_height);
                Graphics2D gImg = get_graphics(this.buf_image);
                render_destination_label(gImg, dest_name, dest_ete, dest_eta, dest_dist, (this.aircraft.ground_speed() < 50), source == Avionics.HSI_SOURCE_GPS );
                gImg.dispose();

            } else {
                this.buf_image = null;
            }

        }

        if (nd_gc.powered && nd_gc.airbus_style) drawApproachType(g2);
        
        // copy the buffered image to the screen on each invocation of paint()
        if ( nd_gc.powered && this.destination_active ) {

            // the buffered image has been created, and it has not been deliberately set to null
            int x = nd_gc.panel_rect.x + nd_gc.panel_rect.width - (nd_gc.max_char_advance_l * 6);
            int y = nd_gc.panel_rect.y;
            g2.drawImage(this.buf_image, x, y, null);

        }

    }


    public void render_destination_label(Graphics2D g2, String wpt_name, float wpt_ete, long wpt_eta, float wpt_dist, boolean too_slow, boolean fmc_dest) {

        int line1 = nd_gc.line_height_l;
        int line2 = line1 + nd_gc.line_height_xs;
        int line3 = line2 + nd_gc.line_height_l;
        int line4 = line3 + nd_gc.line_height_l;
        int box_height = line4 + nd_gc.line_height_xs/2;
        int dx = nd_gc.max_char_advance_l/2;

        g2.setBackground(nd_gc.background_color);
        g2.clearRect(0, 0, (nd_gc.max_char_advance_l * 7), box_height);
        if ( fmc_dest )
            g2.setColor(nd_gc.fmc_active_color);
        else
            g2.setColor(nd_gc.nav_needle_color);
        g2.setFont(nd_gc.font_l);
        g2.drawString(wpt_name, dx + 0, line1);

        g2.setColor(nd_gc.top_text_color);

        if ( ! too_slow && (wpt_ete != 0.0f) && (wpt_dist > 0.0f) ) {

            g2.setFont(nd_gc.font_xs);
            String ete_text = "" + ete_minutes_formatter.format(wpt_ete);
            g2.drawString(ete_text, dx + 0, line2);
            g2.setFont(nd_gc.font_xxs);
            String ete_label_text = "min";
            g2.drawString(ete_label_text, dx + nd_gc.get_text_width(g2, nd_gc.font_xs, ete_text), line2);

            long time_at_arrival_s = wpt_eta;
            long hours_at_arrival = time_at_arrival_s / 3600l;
            float minutes_at_arrival = (( (float)time_at_arrival_s / 3600.0f ) - (float)hours_at_arrival ) * 60.0f;
            hours_at_arrival %= 24l;

            String time_of_arrival_text = "" + eta_hours_formatter.format(hours_at_arrival) + eta_minutes_formatter.format(minutes_at_arrival);
            g2.setFont(nd_gc.font_l);
            g2.drawString(time_of_arrival_text, dx + 0, line3);
            g2.setFont(nd_gc.font_s);
            g2.drawString("Z", dx + nd_gc.get_text_width(g2, nd_gc.font_l, time_of_arrival_text), line3);

        } else {

            // we are too slow, or ETE=0, or distance=0
            g2.setFont(nd_gc.font_xs);
            String ete_text = "-.-";
            g2.drawString(ete_text, dx + 0, line2);
            g2.setFont(nd_gc.font_xxs);
            String ete_label_text = "min";
            g2.drawString(ete_label_text, dx + nd_gc.get_text_width(g2, nd_gc.font_xs, ete_text), line2);
            String time_of_arrival_text = "----.-";
            g2.setFont(nd_gc.font_l);
            g2.drawString(time_of_arrival_text, dx + 0, line3);
            g2.setFont(nd_gc.font_s);
            g2.drawString("Z", dx + nd_gc.get_text_width(g2, nd_gc.font_l, time_of_arrival_text), line3);

        }
        g2.setFont(nd_gc.font_l);
        String dist_text;
        if (wpt_dist == 0.0f) {
            dist_text = "---";
        } else if (wpt_dist < 99.5f) {
            dist_text = one_decimal_formatter.format(wpt_dist);
        } else {
            dist_text = integer_formatter.format(wpt_dist);
        }
        g2.drawString(dist_text, dx + 0, line4);
        g2.setFont(nd_gc.font_s);
        g2.drawString("NM", dx + nd_gc.get_text_width(g2, nd_gc.font_l, dist_text), line4);

    }

    public void drawApproachType(Graphics2D g2) {
    	if (this.avionics.is_qpac()) { 
    		int appr_type = this.avionics.qpac_appr_type();
    		String appr_str = "";
    		if (appr_type == 0) appr_str = "  ILS " ;
    		if (appr_type == 1) appr_str = "  RNAV ";
    		if (appr_type > 1) appr_str = " APPR " + appr_type;
    		g2.setFont(nd_gc.appr_type_font);
    		g2.setColor(nd_gc.pfd_active_color);
    		g2.drawString(appr_str, nd_gc.appr_type_x, nd_gc.appr_type_y);
    	}
    }

}
