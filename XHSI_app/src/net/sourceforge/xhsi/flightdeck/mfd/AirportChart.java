/**
* AirportChart.java
* 
* Draw TaxiChart
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
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSIStatus;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.Runway;
import net.sourceforge.xhsi.model.TaxiChart;

import net.sourceforge.xhsi.model.aptnavdata.AptNavXP900DatTaxiChartBuilder;



public class AirportChart extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public AirportChart(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);


    }


    public void paint(Graphics2D g2) {
        if ( mfd_gc.powered && ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_TAXI ) ) {
            drawChart(g2);
        }
    }


    private void drawChart(Graphics2D g2) {

        TaxiChart taxi = TaxiChart.get_instance();

        String nearest_arpt_str = this.aircraft.get_nearest_arpt();
//nearest_arpt_str = "LOWI";

        boolean daylight;
        if ( this.preferences.get_preference(XHSIPreferences.PREF_TAXICHART_COLOR).equals(XHSIPreferences.TAXICHART_COLOR_AUTO) ) {
            daylight = ! this.aircraft.cockpit_lights();
        } else if ( this.preferences.get_preference(XHSIPreferences.PREF_TAXICHART_COLOR).equals(XHSIPreferences.TAXICHART_COLOR_DAY) ) {
            daylight = true;
        } else {
            daylight = false;
        }
        Color text = daylight ? mfd_gc.background_color : Color.WHITE;
        Color paper = daylight ? Color.WHITE : mfd_gc.background_color;
        Color field = daylight ? mfd_gc.color_verypalegreen : mfd_gc.color_verydarkgreen; // Color.GREEN.darker().darker().darker().darker().darker(); // mfd_gc.color_lavender; //new Color(0xF0F0F0);
        Color taxi_ramp = mfd_gc.hard_color;
        Color hard_rwy = daylight ? mfd_gc.background_color : Color.WHITE;
        g2.setColor(paper);
        g2.fillRect(mfd_gc.panel_rect.x, mfd_gc.panel_rect.y, mfd_gc.panel_rect.width, mfd_gc.panel_rect.height);


        if (nearest_arpt_str.length() >= 3) {

            if ( taxi.ready && ! taxi.icao.equals(nearest_arpt_str) /*&& (XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_LOADED))*/ ) {
                
                // we need to load another airport chart
                
//logger.warning("I have "+taxi.icao);
//logger.warning("I request "+nearest_arpt_str+" ("+nearest_arpt_str.length()+" char)");
                // redundant: taxi.ready = false;
                try {
                    AptNavXP900DatTaxiChartBuilder cb = new AptNavXP900DatTaxiChartBuilder(this.preferences.get_preference(XHSIPreferences.PREF_APTNAV_DIR));
                    cb.get_chart(nearest_arpt_str);
                } catch (Exception e) {
                    logger.warning("\nProblem requesting TaxiChartBuilder "+nearest_arpt_str);
                }
                
            } else if ( taxi.ready && (taxi.airport!=null) && taxi.airport.icao_code.equals(nearest_arpt_str) ) {
                
//                if ( false ) {
//
//                    float arpt_lon_width = taxi.east_lon - taxi.west_lon;
//                    float arpt_lat_height = taxi.north_lat - taxi.south_lat;
//
//                    float chart_lon_scale;
//                    float chart_lat_scale;
//                    float chart_metric_scale;
//                    int map_width = efb_gc.panel_rect.width * 9 / 10;
//                    int map_height = efb_gc.panel_rect.height * 81 / 100;
//                    Point topleft;
//
//                    if ( ( arpt_lon_width * taxi.lon_scale / map_width ) > ( arpt_lat_height / map_height ) ) {
//                        // chart fits horizontally
//                        chart_lon_scale = map_width / arpt_lon_width;
//                        chart_lat_scale = chart_lon_scale / taxi.lon_scale;
//                        topleft = new Point( efb_gc.panel_rect.x + efb_gc.panel_rect.width/10/2, efb_gc.panel_rect.y + efb_gc.panel_rect.height/10 + efb_gc.panel_rect.height*9/100/2 + ( map_height - (int)(arpt_lat_height*chart_lat_scale) ) / 2 );
//                        chart_metric_scale = map_width / arpt_lon_width / taxi.lon_scale / 60.0f / 1851.851f;
//                    } else {
//                        // chart fits vertically
//                        chart_lat_scale = map_height / arpt_lat_height;
//                        chart_lon_scale = chart_lat_scale * taxi.lon_scale;
//                        topleft = new Point( efb_gc.panel_rect.x + efb_gc.panel_rect.width/10/2 + ( map_width - (int)(arpt_lon_width*chart_lon_scale) ) / 2, efb_gc.panel_rect.y + efb_gc.panel_rect.height/10 + efb_gc.panel_rect.height*9/100/2 );
//                        chart_metric_scale = map_height / arpt_lat_height / 60.0f / 1851.851f;
//                    }
//
//
//                    if ( taxi.border != null ) {
//
//                        int poly_x[] = new int[taxi.border.nodes.size()];
//                        int poly_y[] = new int[taxi.border.nodes.size()];
//
//                            for (int h=0; h<taxi.border.nodes.size(); h++) {
//                                TaxiChart.Node node1 = taxi.border.nodes.get(h);
//                                poly_x[h] = topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale);
//                                poly_y[h] = topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale);
//                            }
//                            g2.setColor(efb_gc.hard_color.darker().darker());
//                            g2.fillPolygon(poly_x, poly_y, taxi.border.nodes.size());
//
//                            if ( ! taxi.border.holes.isEmpty() ) {
//
//                                for (int k=0; k<taxi.border.holes.size(); k++) {
//
//                                    TaxiChart.Pavement hole1 = taxi.border.holes.get(k);
//        //                            for (int l=0+1; l<hole1.nodes.size()+1; l++) {
//        //                                TaxiChart.Node node1;
//        //                                if ( l == hole1.nodes.size() ) {
//        //                                    node1 = hole1.nodes.get(0);
//        //                                } else {
//        //                                    node1 = hole1.nodes.get(l);
//        //                                }
//        //                                TaxiChart.Node node1 = hole1.nodes.get(l-1);
//        //                                if ( node1.bezier_node && false ) {
//        //                                    QuadCurve2D bezier = new QuadCurve2D.Float();
//        //                                    bezier.setCurve( topleft.x + ((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + ((taxi.north_lat - node1.lat)*chart_lat_scale),
//        //                                        topleft.x + ((node1.bz_lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + ((taxi.north_lat - node1.bz_lat)*chart_lat_scale),
//        //                                        topleft.x + ((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + ((taxi.north_lat - node1.lat)*chart_lat_scale));
//        //                                    g2.draw(bezier);
//        //                                } else {
//        //                                    g2.drawLine( topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale),
//        //                                        topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale));
//        //                                }
//                                    poly_x = new int[hole1.nodes.size()];
//                                    poly_y = new int[hole1.nodes.size()];
//                                    for (int l=0; l<hole1.nodes.size(); l++) {
//                                        TaxiChart.Node node1 = hole1.nodes.get(l);
//                                        poly_x[l] = topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale);
//                                        poly_y[l] = topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale);
//                                    }
//                                    g2.setColor(efb_gc.background_color);
//                                    g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
//
//                                } // list of holes
//
//                            } // ! empty list of holes
//
//                    } // ! null border
//
//                    if ( ! taxi.pavements.isEmpty() ) {
//
//                        for (int i=0; i<taxi.pavements.size(); i++) {
//
//                            TaxiChart.Pavement ramp1 = taxi.pavements.get(i);
//                            int poly_x[] = new int[ramp1.nodes.size()];
//                            int poly_y[] = new int[ramp1.nodes.size()];
//        //                    for (int h=0+1; h<ramp1.nodes.size()+1; h++) {
//        //                        TaxiChart.Node node1;
//        //                        if ( h == ramp1.nodes.size() ) {
//        //                            node1 = ramp1.nodes.get(0);
//        //                        } else {
//        //                            node1 = ramp1.nodes.get(h);
//        //                        }
//        //                        TaxiChart.Node node1 = ramp1.nodes.get(h-1);
//        //                        if ( node1.bezier_node && false ) {
//        //                            QuadCurve2D bezier = new QuadCurve2D.Float();
//        //                            bezier.setCurve( topleft.x + ((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                topleft.y + ((taxi.north_lat - node1.lat)*chart_lat_scale),
//        //                                topleft.x + ((node1.bz_lon-taxi.west_lon)*chart_lon_scale),
//        //                                topleft.y + ((taxi.north_lat - node1.bz_lat)*chart_lat_scale),
//        //                                topleft.x + ((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                topleft.y + ((taxi.north_lat - node1.lat)*chart_lat_scale));
//        //                            g2.draw(bezier);
//        //                        } else {
//        //                            g2.drawLine( topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale),
//        //                                topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale));
//        //                        }
//                            for (int j=0; j<ramp1.nodes.size(); j++) {
//                                TaxiChart.Node node1 = ramp1.nodes.get(j);
//                                poly_x[j] = topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale);
//                                poly_y[j] = topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale);
//                            }
//                            g2.setColor(efb_gc.hard_color);
//                            g2.fillPolygon(poly_x, poly_y, ramp1.nodes.size());
//
//                            if ( ! ramp1.holes.isEmpty() ) {
//
//                                for (int k=0; k<ramp1.holes.size(); k++) {
//
//                                    TaxiChart.Pavement hole1 = ramp1.holes.get(k);
//        //                            for (int l=0+1; l<hole1.nodes.size()+1; l++) {
//        //                                TaxiChart.Node node1;
//        //                                if ( l == hole1.nodes.size() ) {
//        //                                    node1 = hole1.nodes.get(0);
//        //                                } else {
//        //                                    node1 = hole1.nodes.get(l);
//        //                                }
//        //                                TaxiChart.Node node1 = hole1.nodes.get(l-1);
//        //                                if ( node1.bezier_node && false ) {
//        //                                    QuadCurve2D bezier = new QuadCurve2D.Float();
//        //                                    bezier.setCurve( topleft.x + ((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + ((taxi.north_lat - node1.lat)*chart_lat_scale),
//        //                                        topleft.x + ((node1.bz_lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + ((taxi.north_lat - node1.bz_lat)*chart_lat_scale),
//        //                                        topleft.x + ((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + ((taxi.north_lat - node1.lat)*chart_lat_scale));
//        //                                    g2.draw(bezier);
//        //                                } else {
//        //                                    g2.drawLine( topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale),
//        //                                        topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale),
//        //                                        topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale));
//        //                                }
//                                    poly_x = new int[hole1.nodes.size()];
//                                    poly_y = new int[hole1.nodes.size()];
//                                    for (int l=0; l<hole1.nodes.size(); l++) {
//                                        TaxiChart.Node node1 = hole1.nodes.get(l);
//                                        poly_x[l] = topleft.x + (int)((node1.lon-taxi.west_lon)*chart_lon_scale);
//                                        poly_y[l] = topleft.y + (int)((taxi.north_lat - node1.lat)*chart_lat_scale);
//                                    }
//                                    if ( taxi.border == null ) {
//                                        g2.setColor(efb_gc.background_color);
//                                    } else {
//                                        g2.setColor(efb_gc.hard_color.darker().darker());
//                                    }
//                                    g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
//
//                                } // list of holes
//
//                            } // ! empty list of holes
//
//                        } // list of pavements
//
//                    } // ! empty list of pavements
//
//
//                    // APT810-style segments
//                    if ( taxi.airport != null ) {
//                        AffineTransform original_at = g2.getTransform();
//                        Stroke original_stroke = g2.getStroke();
//                        g2.setColor(efb_gc.hard_color.darker());
//                        for (int s=0; s<taxi.segments.size(); s++) {
//                            TaxiChart.Segment seg0 = taxi.segments.get(s);
//                            int s_x = topleft.x + (int)((seg0.lon-taxi.west_lon)*chart_lon_scale);
//                            int s_y = topleft.y + (int)((taxi.north_lat - seg0.lat)*chart_lat_scale);
//                            int s_l = (int)(seg0.length*chart_metric_scale/2.0f);
//                            int s_y1 = s_y - s_l;
//                            int s_y2 = s_y + + s_l;
//                            g2.setStroke(new BasicStroke(seg0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
//                            g2.rotate( Math.toRadians( seg0.orientation ), s_x, s_y );
//                            g2.drawLine( s_x, s_y1, s_x, s_y2 );
//                            g2.setTransform(original_at);
//                        }
//                        g2.setStroke(original_stroke);
//                    }
//
//
//                    // runways
//                    if ( taxi.airport != null ) {
//    //logger.warning("Drawing the runways for "+taxi.airport.name);
//    //logger.warning("Drawing the runways for "+taxi.airport.name);
//                        for (int i=0; i<taxi.airport.runways.size(); i++) {
//                            Runway rwy0 = taxi.airport.runways.get(i);
//    //logger.warning("RWY "+rwy1.rwy_num1+"/"+rwy1.rwy_num2+" "+rwy1.width);
//                            if ( (rwy0.surface==Runway.RWY_ASPHALT) || (rwy0.surface==Runway.RWY_CONCRETE) )
//                                g2.setColor(efb_gc.hard_color.brighter().brighter());
//                            else if (rwy0.surface==Runway.RWY_GRASS)
//                                g2.setColor(efb_gc.grass_color);
//                            else if ( (rwy0.surface==Runway.RWY_DIRT) || (rwy0.surface==Runway.RWY_GRAVEL) || (rwy0.surface==Runway.RWY_DRY_LAKEBED) )
//                                g2.setColor(efb_gc.sand_color);
//                            else if (rwy0.surface==Runway.RWY_SNOW)
//                                g2.setColor(efb_gc.snow_color);
//                            else
//                                g2.setColor(efb_gc.hard_color.brighter());
//                            Stroke original_stroke = g2.getStroke();
//                            g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
//                            g2.drawLine( topleft.x + (int)((rwy0.lon1-taxi.west_lon)*chart_lon_scale),
//                                topleft.y + (int)((taxi.north_lat - rwy0.lat1)*chart_lat_scale),
//                                topleft.x + (int)((rwy0.lon2-taxi.west_lon)*chart_lon_scale),
//                                topleft.y + (int)((taxi.north_lat - rwy0.lat2)*chart_lat_scale));
//                            g2.setStroke(original_stroke);
//                        }
//
//                    }
//
//
//                    // moving aircraft symbol
//                    int px = topleft.x + (int)( ( this.aircraft.lon() - taxi.west_lon ) * chart_lon_scale );
//                    int py = topleft.y + (int)( ( taxi.north_lat - this.aircraft.lat() ) * chart_lat_scale );
//                    float ps = 1.0f * efb_gc.grow_scaling_factor;
//                    float cy = 105.0f;
//                    int plan_x[] = {
//                        Math.round(   0.0f * ps / 10.0f ) + px,
//                        Math.round(  15.0f * ps / 10.0f ) + px,
//                        Math.round(  15.0f * ps / 10.0f ) + px,
//                        Math.round(  95.0f * ps / 10.0f ) + px,
//                        Math.round(  95.0f * ps / 10.0f ) + px,
//                        Math.round(  35.0f * ps / 10.0f ) + px,
//                        Math.round(  15.0f * ps / 10.0f ) + px,
//                        Math.round(  15.0f * ps / 10.0f ) + px,
//                        Math.round(  30.0f * ps / 10.0f ) + px,
//                        Math.round(  30.0f * ps / 10.0f ) + px,
//                        Math.round(   0.0f * ps / 10.0f ) + px,
//                        Math.round( -30.0f * ps / 10.0f ) + px,
//                        Math.round( -30.0f * ps / 10.0f ) + px,
//                        Math.round( -15.0f * ps / 10.0f ) + px,
//                        Math.round( -15.0f * ps / 10.0f ) + px,
//                        Math.round( -35.0f * ps / 10.0f ) + px,
//                        Math.round( -95.0f * ps / 10.0f ) + px,
//                        Math.round( -95.0f * ps / 10.0f ) + px,
//                        Math.round( -15.0f * ps / 10.0f ) + px,
//                        Math.round( -15.0f * ps / 10.0f ) + px
//                    };
//                    int plan_y[] = {
//                        Math.round( (   0.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( (  25.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( (  75.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 140.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 155.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 185.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 215.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 215.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 185.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 155.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( ( 140.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( (  75.0f - cy ) * ps / 10.0f ) + py,
//                        Math.round( (  25.0f - cy ) * ps / 10.0f ) + py
//                    };
//    //                g2.setColor(efb_gc.color_magenta);
//    //                g2.drawOval(px-10, py-10, 20, 20);
//                    AffineTransform original_at = g2.getTransform();
//                    g2.rotate(
//                            Math.toRadians( this.aircraft.heading() - this.aircraft.magnetic_variation() ),
//                            px,
//                            py
//                    );
//                    g2.setColor(Color.BLACK);
//                    g2.fillPolygon(plan_x, plan_y, 20);
//                    g2.setColor(Color.YELLOW);
//                    g2.drawPolygon(plan_x, plan_y, 20);
//                    g2.setTransform(original_at);
//                    
//                } else {
                    
                float chart_lon_scale;
                float chart_lat_scale;
                float chart_metric_scale;

                int map_width = mfd_gc.panel_rect.width;
                int map_height = mfd_gc.panel_rect.height;
                int map_size_px = Math.min(map_width, map_height);

                Point map_c;

                AffineTransform original_at = g2.getTransform();

                double true_heading = Math.toRadians( this.aircraft.heading() - this.aircraft.magnetic_variation() );

                boolean centered = ( ( this.avionics.map_mode() == Avionics.EFIS_MAP_CENTERED ) && ( this.avionics.map_submode() != Avionics.EFIS_MAP_NAV ) )
                        || ( ( this.avionics.map_mode() == Avionics.EFIS_MAP_EXPANDED ) && ( this.avionics.map_submode() == Avionics.EFIS_MAP_NAV ) )
                        || ( this.avionics.map_submode() == Avionics.EFIS_MAP_PLN );
                if ( centered ) {
                    map_c = new Point( mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2, mfd_gc.panel_rect.y + mfd_gc.panel_rect.height/2 );
                } else {
                    map_c = new Point( mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2, mfd_gc.panel_rect.y + mfd_gc.panel_rect.height*7/8 );
                }
                if ( this.avionics.map_submode() != Avionics.EFIS_MAP_PLN ) {
                    g2.rotate( -true_heading,  map_c.x, map_c.y );
                }

                float map_range = (float)this.avionics.map_range() / 100.0f;

                chart_lat_scale = map_size_px / map_range * 60.0f;
                chart_lon_scale = chart_lat_scale * taxi.lon_scale;
                chart_metric_scale = chart_lat_scale / 60.0f / 1851.851f;

                float acf_lat = this.aircraft.lat();
                float acf_lon = this.aircraft.lon();

                if ( this.avionics.map_mode() == Avionics.EFIS_MAP_EXPANDED ) {
                }

                if ( taxi.border != null ) {

                    int poly_x[] = new int[taxi.border.nodes.size()];
                    int poly_y[] = new int[taxi.border.nodes.size()];

                        for (int h=0; h<taxi.border.nodes.size(); h++) {
                            TaxiChart.Node node1 = taxi.border.nodes.get(h);
                            poly_x[h] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                            poly_y[h] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                        }
                        g2.setColor(field);
                        g2.fillPolygon(poly_x, poly_y, taxi.border.nodes.size());
                        g2.drawPolygon(poly_x, poly_y, taxi.border.nodes.size());

                        if ( ! taxi.border.holes.isEmpty() ) {

                            for (int k=0; k<taxi.border.holes.size(); k++) {

                                TaxiChart.Pavement hole1 = taxi.border.holes.get(k);
                                poly_x = new int[hole1.nodes.size()];
                                poly_y = new int[hole1.nodes.size()];
                                for (int l=0; l<hole1.nodes.size(); l++) {
                                    TaxiChart.Node node1 = hole1.nodes.get(l);
                                    poly_x[l] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                                    poly_y[l] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                                }
                                g2.setColor(paper);
                                g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
                                g2.drawPolygon(poly_x, poly_y, hole1.nodes.size());

                            } // list of holes

                        } // ! empty list of holes

                } // ! null border


                if ( ! taxi.pavements.isEmpty() ) {

                    for (int i=0; i<taxi.pavements.size(); i++) {

                        TaxiChart.Pavement ramp1 = taxi.pavements.get(i);
                        int poly_x[] = new int[ramp1.nodes.size()];
                        int poly_y[] = new int[ramp1.nodes.size()];
                        for (int j=0; j<ramp1.nodes.size(); j++) {
                            TaxiChart.Node node1 = ramp1.nodes.get(j);
                            poly_x[j] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                            poly_y[j] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                        }
                        g2.setColor(taxi_ramp);
                        g2.fillPolygon(poly_x, poly_y, ramp1.nodes.size());
                        g2.drawPolygon(poly_x, poly_y, ramp1.nodes.size());

                        if ( ! ramp1.holes.isEmpty() ) {

                            for (int k=0; k<ramp1.holes.size(); k++) {

                                TaxiChart.Pavement hole1 = ramp1.holes.get(k);
                                poly_x = new int[hole1.nodes.size()];
                                poly_y = new int[hole1.nodes.size()];
                                for (int l=0; l<hole1.nodes.size(); l++) {
                                    TaxiChart.Node node1 = hole1.nodes.get(l);
                                    poly_x[l] = map_c.x + (int)((node1.lon - acf_lon)*chart_lon_scale);
                                    poly_y[l] = map_c.y - (int)((node1.lat - acf_lat)*chart_lat_scale);
                                }
                                if ( taxi.border == null ) {
                                    g2.setColor(paper);
                                } else {
                                    g2.setColor(field);
                                }
                                g2.fillPolygon(poly_x, poly_y, hole1.nodes.size());
                                g2.drawPolygon(poly_x, poly_y, hole1.nodes.size());

                            } // list of holes

                        } // ! empty list of holes

                    } // list of pavements

                } // ! empty list of pavements


                // APT810-style segments
                if ( taxi.airport != null ) {
                    AffineTransform current_at = g2.getTransform();
                    Stroke original_stroke = g2.getStroke();
                    g2.setColor(taxi_ramp);
                    for (int s=0; s<taxi.segments.size(); s++) {
                        TaxiChart.Segment seg0 = taxi.segments.get(s);
                        int s_x = map_c.x + (int)((seg0.lon - acf_lon)*chart_lon_scale);
                        int s_y = map_c.y - (int)((seg0.lat - acf_lat)*chart_lat_scale);
                        int s_l = (int)(seg0.length*chart_metric_scale/2.0f);
                        int s_y1 = s_y - s_l - 1;
                        int s_y2 = s_y + s_l + 1;
                        g2.setStroke(new BasicStroke(seg0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                        g2.rotate( Math.toRadians( seg0.orientation ), s_x, s_y );
                        g2.drawLine( s_x, s_y1, s_x, s_y2 );
                        g2.setTransform(current_at);
                    }
                    g2.setStroke(original_stroke);
                }


                // runways
                if ( taxi.airport != null ) {
                    
                    // make sure to draw the paved runways OVER the non-paved runways
                    
                    // non-paved
                    for (int i=0; i<taxi.airport.runways.size(); i++) {
                        Runway rwy0 = taxi.airport.runways.get(i);
                        if ( (rwy0.surface!=Runway.RWY_ASPHALT) && (rwy0.surface!=Runway.RWY_CONCRETE) ) {
                            if (rwy0.surface==Runway.RWY_GRASS)
                                g2.setColor(mfd_gc.grass_color);
                            else if ( (rwy0.surface==Runway.RWY_DIRT) || (rwy0.surface==Runway.RWY_GRAVEL) || (rwy0.surface==Runway.RWY_DRY_LAKEBED) )
                                g2.setColor(mfd_gc.sand_color);
                            else if (rwy0.surface==Runway.RWY_SNOW)
                                g2.setColor(mfd_gc.snow_color);
                            else
                                g2.setColor(mfd_gc.hard_color.darker());
                            Stroke original_stroke = g2.getStroke();
                            g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            g2.drawLine( map_c.x + (int)((rwy0.lon1 - acf_lon)*chart_lon_scale),
                                map_c.y - (int)((rwy0.lat1 - acf_lat)*chart_lat_scale),
                                map_c.x + (int)((rwy0.lon2 - acf_lon)*chart_lon_scale),
                                map_c.y - (int)((rwy0.lat2 - acf_lat)*chart_lat_scale));
                            g2.setStroke(original_stroke);
                        }
                    }

                    // paved
                    for (int i=0; i<taxi.airport.runways.size(); i++) {
                        Runway rwy0 = taxi.airport.runways.get(i);
                        if ( (rwy0.surface==Runway.RWY_ASPHALT) || (rwy0.surface==Runway.RWY_CONCRETE) ) {
                            g2.setColor(hard_rwy);
                            Stroke original_stroke = g2.getStroke();
                            g2.setStroke(new BasicStroke(rwy0.width * chart_metric_scale, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                            g2.drawLine( map_c.x + (int)((rwy0.lon1 - acf_lon)*chart_lon_scale),
                                map_c.y - (int)((rwy0.lat1 - acf_lat)*chart_lat_scale),
                                map_c.x + (int)((rwy0.lon2 - acf_lon)*chart_lon_scale),
                                map_c.y - (int)((rwy0.lat2 - acf_lat)*chart_lat_scale));
                            g2.setStroke(original_stroke);
                        }
                    }

                }

                // moving aircraft symbol
                int px = map_c.x;
                int py = map_c.y;
                float ps = 1.5f * mfd_gc.grow_scaling_factor;
                float cy = 105.0f;
                int plan_x[] = {
                    Math.round(   0.0f * ps / 10.0f ) + px,
                    Math.round(  15.0f * ps / 10.0f ) + px,
                    Math.round(  15.0f * ps / 10.0f ) + px,
                    Math.round(  95.0f * ps / 10.0f ) + px,
                    Math.round(  95.0f * ps / 10.0f ) + px,
                    Math.round(  35.0f * ps / 10.0f ) + px,
                    Math.round(  15.0f * ps / 10.0f ) + px,
                    Math.round(  15.0f * ps / 10.0f ) + px,
                    Math.round(  30.0f * ps / 10.0f ) + px,
                    Math.round(  30.0f * ps / 10.0f ) + px,
                    Math.round(   0.0f * ps / 10.0f ) + px,
                    Math.round( -30.0f * ps / 10.0f ) + px,
                    Math.round( -30.0f * ps / 10.0f ) + px,
                    Math.round( -15.0f * ps / 10.0f ) + px,
                    Math.round( -15.0f * ps / 10.0f ) + px,
                    Math.round( -35.0f * ps / 10.0f ) + px,
                    Math.round( -95.0f * ps / 10.0f ) + px,
                    Math.round( -95.0f * ps / 10.0f ) + px,
                    Math.round( -15.0f * ps / 10.0f ) + px,
                    Math.round( -15.0f * ps / 10.0f ) + px
                };
                int plan_y[] = {
                    Math.round( (   0.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( (  25.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( (  75.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 140.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 155.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 185.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 215.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 215.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 200.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 185.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 125.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 155.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( ( 140.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( (  75.0f - cy ) * ps / 10.0f ) + py,
                    Math.round( (  25.0f - cy ) * ps / 10.0f ) + py
                };
                if ( this.avionics.map_submode() == Avionics.EFIS_MAP_PLN ) {
                    g2.rotate( true_heading, map_c.x, map_c.y );
                } else if ( centered ) {
                    g2.rotate( true_heading, map_c.x, map_c.y );
                } else {
                    g2.setTransform(original_at);
                }
                g2.setColor(Color.MAGENTA.brighter());
                g2.fillPolygon(plan_x, plan_y, 20);
                g2.setColor(Color.MAGENTA.darker());
                g2.drawPolygon(plan_x, plan_y, 20);
                g2.setTransform(original_at);
            }

        }

        // title
        int arpt_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16 - mfd_gc.get_text_width(g2, mfd_gc.font_xxl, nearest_arpt_str);
        int arpt_y = mfd_gc.panel_rect.y + mfd_gc.panel_rect.height/16;
        g2.setFont(mfd_gc.font_xxl);
        g2.setColor(text);
        g2.drawString(nearest_arpt_str, arpt_x, arpt_y);
        arpt_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/16;
        if ( taxi.ready && (taxi.airport!=null) ) {
            g2.setFont(mfd_gc.font_xl);
            g2.drawString(taxi.airport.name, arpt_x, arpt_y);
        }
        g2.drawLine(arpt_x, arpt_y + mfd_gc.line_height_m/2, mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*15/16, arpt_y + mfd_gc.line_height_m/2);
            
    }
    

}
