/**
* DestinationAirport.java
* 
* Prints information about the destination airport
* 
* Copyright (C) 2011  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Runway;



public class DestinationAirport extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    private String surfaces[] = {"None0", "Asphalt", "Concrete", "Grass", "Dirt", "Gravel", "None6", "None7", "None8", "None9", "None10", "None11", "Dry lakebed", "Water", "Snow", "Transparent"};
    public static final int RWY_ASPHALT = 1;
    public static final int RWY_CONCRETE = 2;
    public static final int RWY_GRASS = 3;
    public static final int RWY_DIRT = 4;
    public static final int RWY_GRAVEL = 5;
    public static final int RWY_DRY_LAKEBED = 12;
    public static final int RWY_WATER = 13;
    public static final int RWY_SNOW = 14;
    public static final int RWY_TRNSPARENT = 15;

    private DecimalFormat freq_format;


    public DestinationAirport(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        freq_format = new DecimalFormat("000.00");
        DecimalFormatSymbols format_symbols = freq_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        freq_format.setDecimalFormatSymbols(format_symbols);

    }


    public void paint(Graphics2D g2) {
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_ARPT ) ) {
            drawDestination(g2);
        }
    }


    private String get_nav_dest() {

        // if we are tuned to a localizer, find the airport for that localizer

        String dest_str = "";

        Localizer dest_loc = null;
        int hsi_source = this.avionics.hsi_source();
        // try NAV1 first by default
        int bank = 1;
        if ( hsi_source == Avionics.HSI_SOURCE_NAV2 ) {
            // when NAV2 is our reference source, try that first
            bank = 2;
        }
        dest_loc = this.avionics.get_tuned_localizer(bank);
        if ( dest_loc == null ) {
            // try the other one now
            // (3-bank) to switch from 1 to 2 or from 2 to 1
            dest_loc = this.avionics.get_tuned_localizer(3 - bank);
        }
        if ( dest_loc != null ) {
            // we are tuned to a Localizer, now fetch the airport that goes with it
            dest_str = dest_loc.airport;
        }

        return dest_str;

    }


    private String get_fms_dest() {

       // see if the last waypoint in the FMS is an airport

        String dest_str = "";

        FMSEntry last_wpt = this.avionics.get_fms().get_last_waypoint();
         if ( ( last_wpt != null ) && ( last_wpt.type == FMSEntry.ARPT ) ) {
            dest_str = last_wpt.name;
        }

        return dest_str;

    }


    private void drawDestination(Graphics2D g2) {

        String dest_arpt_str = "";

        // are we tuned to a localizer?
        dest_arpt_str = get_nav_dest();

        if ( dest_arpt_str.equals("") ) {
            // if not, is there a destination airport in the FMS?
            dest_arpt_str = get_fms_dest();
        }

        if ( dest_arpt_str.equals("") ) {
            // if not, the nearest airport...
            dest_arpt_str = this.aircraft.get_nearest_arpt();
        }

        if ( ! dest_arpt_str.equals("") ) {

            g2.setColor(mfd_gc.efb_color);

            int arpt_size = Math.min(mfd_gc.panel_rect.width, mfd_gc.panel_rect.height);
            int arpt_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, dest_arpt_str);
            int arpt_y = mfd_gc.panel_rect.y + arpt_size/16;
            g2.setFont(mfd_gc.font_xxl);
            g2.drawString(dest_arpt_str, arpt_x, arpt_y);

            NavigationObjectRepository nor = NavigationObjectRepository.get_instance();
            Airport airport = nor.get_airport(dest_arpt_str.trim());

            if ( airport != null ) {
                g2.setFont(mfd_gc.font_xl);
                arpt_x = mfd_gc.panel_rect.x + arpt_size/16;
                g2.drawString(airport.name, arpt_x, arpt_y);
//                g2.drawLine(efb_gc.panel_rect.x + efb_gc.panel_rect.width*1/16, arpt_y + efb_gc.line_height_m/2, efb_gc.panel_rect.x + efb_gc.panel_rect.width*15/16, arpt_y + efb_gc.line_height_m/2);
                g2.drawLine(arpt_x, arpt_y + mfd_gc.line_height_m/2, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16, arpt_y + mfd_gc.line_height_m/2);
                g2.setFont(mfd_gc.font_s);
                arpt_y += mfd_gc.line_height_s*5/3;
                String elev_str = "elev: " + airport.elev + "ft";
                g2.drawString(elev_str, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16 - mfd_gc.get_text_width(g2, mfd_gc.font_s, elev_str), arpt_y);

                arpt_y += mfd_gc.line_height_s*1/2;
                if ( ! airport.runways.isEmpty() ) {
                    for (int i=0; i<airport.runways.size(); i++) {
                        Runway rwy = (Runway)(airport.runways.get(i));
                        g2.setFont(mfd_gc.font_l);
                        arpt_y += mfd_gc.line_height_l*3/2;
                        g2.drawString(rwy.rwy_num1 + "/" + rwy.rwy_num2, arpt_x + arpt_size*3/64, arpt_y);
//                        g2.drawString(Math.round(rwy.length) + "m", arpt_x + arpt_size*7/32, arpt_y);
                        g2.drawString(Math.round(rwy.length) + "x" + Math.round(rwy.width) + "m", arpt_x + arpt_size*14/64, arpt_y);
//                        g2.drawString(Math.round(rwy.length/0.3048f) + "ft", arpt_x + arpt_size*11/32, arpt_y);
                        g2.drawString(Math.round(rwy.length/0.3048f) + "x" + Math.round(rwy.width/0.3048f) + "ft", arpt_x + arpt_size*28/64, arpt_y);
//                        g2.drawString(surfaces[rwy.surface], arpt_x + arpt_size*16/32, arpt_y);
                        g2.drawString(surfaces[rwy.surface], arpt_x + arpt_size*44/64, arpt_y);
                        if ( ! rwy.localizers.isEmpty() ) {
                            for (int l=0; l<rwy.localizers.size(); l++) {
                                Localizer loc = rwy.localizers.get(l);
                                g2.setFont(mfd_gc.font_m);
                                arpt_y += mfd_gc.line_height_m*3/2;
                                g2.drawString(loc.rwy, arpt_x + arpt_size*6/64, arpt_y);
                                g2.drawString(loc.ilt, arpt_x + arpt_size*11/64, arpt_y);
                                g2.drawString(freq_format.format(loc.frequency), arpt_x + arpt_size*17/64, arpt_y);
                                g2.drawString(loc.description, arpt_x + arpt_size*26/64, arpt_y);
                            }
                        }
                    }
                }

                arpt_y += mfd_gc.line_height_m;
                if ( ! airport.com_radios.isEmpty() ) {
                    boolean column_right = false;
                    int com_x;
                    for (int c=0; c<airport.com_radios.size(); c++) {
                        ComRadio com_radio = airport.com_radios.get(c);
                        g2.setFont(mfd_gc.font_m);
                        if ( column_right ) {
                            com_x = arpt_x + mfd_gc.panel_rect.width/2;
                        } else {
                            com_x = arpt_x + mfd_gc.panel_rect.width/32;
                            arpt_y += mfd_gc.line_height_m*3/2;
                        }
                        g2.drawString(freq_format.format(com_radio.frequency) + "  " + com_radio.callsign, com_x, arpt_y);
                        column_right = ! column_right;
                    }
                }

            }

        }

    }


}
