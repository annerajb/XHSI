/**
* ForegroundMessages.java
* 
* Write the symbol selection labels, TCAS messages for TA and RA
* and the EFIS MODE/NAV FREQ DISAGREE message
* Write the mode and range change message (Airbus)
* 
* Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2009-2017  Nicolas Carel
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

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;
//import net.sourceforge.xhsi.model.TCAS;
import net.sourceforge.xhsi.model.Avionics.EPGWSAlertLevel;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class ForegroundMessages extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    
    
    public ForegroundMessages(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {
            if (nd_gc.boeing_style) {
            	drawSymbolLabels(g2);
            }
            drawTerrainInfoBox(g2);
            drawTrafficMessage(g2);
            drawDisagree(g2);
            if (nd_gc.airbus_style) {
                if (nd_gc.display_mode_change_msg()) {
                	displayGlobalMessage(g2,"MODE CHANGED");
                } else if (nd_gc.display_range_change_msg()) {
                    displayGlobalMessage(g2,"RANGE CHANGED");
                } else 
                	displayEGPWSMessage(g2);       	
            } else 
            	displayEGPWSMessage(g2);
        }

    }


    private void drawSymbolLabels(Graphics2D g2) {

        // I know, this is not very clean, there is too much code duplication...

        String label_str;
        boolean tcas_on = ( (this.avionics.transponder_mode() >= Avionics.XPDR_TA) || this.preferences.get_tcas_always_on() );

        if ( ! nd_gc.mode_classic_hsi ) {

            // EFIS symbols
            g2.setFont(nd_gc.font_s);

            // ARPT
            if ( this.avionics.efis_shows_arpt() ) {
                label_str = "ARPT";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_arpt_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if ( ( (nd_gc.map_range <= 160) || nd_gc.map_zoomin ) && nd_gc.mode_fullmap ) {
                    g2.setColor(nd_gc.arpt_color);
                } else {
                    g2.setColor(nd_gc.dim_label_color);
                }
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_arpt_y);
            }
            // WPT
            if ( this.avionics.efis_shows_wpt() ) {
                label_str = "WPT";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_wpt_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if ( ( (nd_gc.map_range <= 40) || nd_gc.map_zoomin ) && nd_gc.mode_fullmap ) {
                    g2.setColor(nd_gc.wpt_color);
                } else {
                    g2.setColor(nd_gc.dim_label_color);
                }
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_wpt_y);
            }
            //VOR
            if ( this.avionics.efis_shows_vor() ) {
                label_str = "VOR";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_vor_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if ( ( (nd_gc.map_range <= 80) || nd_gc.map_zoomin ) && nd_gc.mode_fullmap ) {
                    g2.setColor(nd_gc.navaid_color);
                } else {
                    g2.setColor(nd_gc.dim_label_color);
                }
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_vor_y);
            }
            // NDB
            if ( this.avionics.efis_shows_ndb() ) {
                label_str = "NDB";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_ndb_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if ( ( (nd_gc.map_range <= 80) || nd_gc.map_zoomin ) && nd_gc.mode_fullmap ) {
                    g2.setColor(nd_gc.navaid_color);
                } else {
                    g2.setColor(nd_gc.dim_label_color);
                }
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_ndb_y);
            }

            // POS
            if ( this.avionics.efis_shows_pos() ) {
                label_str = "POS";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_pos_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if ( ! nd_gc.mode_plan ) {
                    g2.setColor(nd_gc.pos_label_color);
                } else {
                    g2.setColor(nd_gc.dim_label_color);
                }
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_pos_y);
            }
            // DATA
            if ( this.avionics.efis_shows_data() ) {
                label_str = "DATA";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_data_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if ( this.fms.is_active() && ( nd_gc.mode_fullmap || ( this.avionics.hsi_source() == Avionics.HSI_SOURCE_GPS ) ) ) {
                    g2.setColor(nd_gc.data_label_color);
                } else {
                    g2.setColor(nd_gc.dim_label_color);
                }
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_data_y);
            }
            // TFC
            if ( ! nd_gc.mode_plan && tcas_on && this.avionics.efis_shows_tfc() ) {
                label_str = "TFC";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_tfc_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                g2.setColor(nd_gc.tcas_label_color);
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_tfc_y);
            }
            // TERRAIN
            if ( this.avionics.efis_shows_terrain() ) {
                label_str = "TERR";
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_terrain_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
               	g2.setColor(nd_gc.terrain_label_color);
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_terrain_y);
            }
            // WEATHER RADAR
            int wxr_mode = this.avionics.wxr_mode();
            boolean on_ground = this.aircraft.on_ground();
            if ( (wxr_mode>0) && this.avionics.efis_shows_wxr() && (!this.avionics.efis_shows_terrain()) ) {
                label_str = "WX";
                if (wxr_mode==2) label_str="WX+T";
                if (wxr_mode==3) label_str="MAP";
                
                g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_s/2, nd_gc.left_label_terrain_y - nd_gc.line_height_s, g2.getFontMetrics(nd_gc.font_s).stringWidth(label_str) + nd_gc.digit_width_s, nd_gc.line_height_s*10/8);
                if (wxr_mode<4 && on_ground) {
                	g2.setColor(nd_gc.caution_color);
                	label_str = "WX STBY";
                } else if ( ! nd_gc.map_zoomin ) {
                	g2.setColor(nd_gc.terrain_label_color);
                } else {
                	g2.setColor(nd_gc.dim_label_color);
                }
                	
                g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_terrain_y);
            }

        }

        // TA ONLY or TCAS OFF
        label_str = tcas_on ? "TA ONLY" : "TCAS OFF";
        g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_xs/2, nd_gc.left_label_taonly_y - nd_gc.line_height_xs, g2.getFontMetrics(nd_gc.font_xs).stringWidth(label_str) + nd_gc.digit_width_xs, nd_gc.line_height_xs*10/8);
        if ( tcas_on )
            g2.setColor(nd_gc.tcas_label_color);
        else
            g2.setColor(nd_gc.caution_color);
        g2.setFont(nd_gc.font_xs);
        g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_taonly_y);

        // XPDR
        if ( this.avionics.transponder_mode() == Avionics.XPDR_OFF ) {
            label_str = "XPDR OFF";
            g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_xs/2, nd_gc.left_label_xpdr_y - nd_gc.line_height_xs, g2.getFontMetrics(nd_gc.font_xs).stringWidth(label_str) + nd_gc.digit_width_xs, nd_gc.line_height_xs*10/8);
            g2.setColor(nd_gc.caution_color);
        } else if ( this.avionics.transponder_mode() == Avionics.XPDR_STBY ) {
            label_str = "XPDR STBY";
            g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_xs/2, nd_gc.left_label_xpdr_y - nd_gc.line_height_xs, g2.getFontMetrics(nd_gc.font_xs).stringWidth(label_str) + nd_gc.digit_width_xs, nd_gc.line_height_xs*10/8);
            g2.setColor(nd_gc.caution_color);
        } else if ( this.avionics.transponder_mode() == Avionics.XPDR_ON ) {
            label_str = "XPDR ON";
            g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_xs/2, nd_gc.left_label_xpdr_y - nd_gc.line_height_xs, g2.getFontMetrics(nd_gc.font_xs).stringWidth(label_str) + nd_gc.digit_width_xs, nd_gc.line_height_xs*10/8);
            g2.setColor(nd_gc.unusual_color);
// no, dark cockpit concept ...
//        } else if ( this.avionics.transponder_mode() == Avionics.XPDR_TA ) {
//            message_str = "XPDR TA";
//            g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_xs/2, nd_gc.left_label_xpdr_y - nd_gc.line_height_xs, g2.getFontMetrics(nd_gc.font_xs).stringWidth(message_str) + nd_gc.digit_width_xs, nd_gc.line_height_xs*10/8);
//            g2.setColor(nd_gc.normal_color);
//        } else if ( this.avionics.transponder_mode() == Avionics.XPDR_TARA ) {
//            message_str = "XPDR TA/RA";
//            g2.clearRect(nd_gc.left_label_x - nd_gc.digit_width_xs/2, nd_gc.left_label_xpdr_y - nd_gc.line_height_xs, g2.getFontMetrics(nd_gc.font_xs).stringWidth(message_str) + nd_gc.digit_width_xs, nd_gc.line_height_xs*10/8);
//            g2.setColor(nd_gc.normal_color);
        }
        if ( this.avionics.transponder_mode() < Avionics.XPDR_TA ) {
            g2.setFont(nd_gc.font_xs);
            g2.drawString(label_str, nd_gc.left_label_x, nd_gc.left_label_xpdr_y);
        }

    }


    private void drawTrafficMessage(Graphics2D g2) {

        // TRAFFIC
        if ( this.aircraft.agl_m() >= 1000.0f / 3.28084f ) {
            // inhibit below 1000ft AGL

            boolean tcas_on = ( (this.avionics.transponder_mode() >= Avionics.XPDR_TA) || nd_gc.tcas_always_on );

            if ( tcas_on && ( this.avionics.get_tcas().ra || this.avionics.get_tcas().ta ) ) {
                g2.setFont(nd_gc.font_xl);
                String message_str = "TRAFFIC";
                int traffic_width = g2.getFontMetrics(nd_gc.font_xl).stringWidth(message_str);
                if (this.avionics.get_tcas().ra) {
                    g2.setColor(nd_gc.warning_color);
                } else {
                    g2.setColor(nd_gc.caution_color);
                }
                int tr = Math.round(40.0f * nd_gc.scaling_factor);
                g2.drawString(message_str, nd_gc.right_label_x - traffic_width - tr, nd_gc.right_label_tcas_y);
                g2.drawRect(nd_gc.right_label_x - traffic_width - tr - nd_gc.digit_width_xl/2, nd_gc.right_label_tcas_y - nd_gc.line_height_xl, traffic_width + nd_gc.digit_width_xl, nd_gc.line_height_xl*10/8);
            }
        }

    }


    private void drawDisagree(Graphics2D g2) {

        // EFIS MODE/NAV FREQ DISAGREE
        if ( nd_gc.mode_mismatch_caution && ( nd_gc.mode_app || nd_gc.mode_vor ) ) {

            int source = this.avionics.hsi_source();
            int refnavradio = 0;
            if ( source == Avionics.HSI_SOURCE_NAV1 ) refnavradio = 1;
            if ( source == Avionics.HSI_SOURCE_NAV2 ) refnavradio = 2;
            if ( ( refnavradio == 1 ) || ( refnavradio == 2 ) ) {
                NavigationRadio radio = this.avionics.get_nav_radio(refnavradio);
                RadioNavigationObject rnav_object;
                if (radio != null) {
                    rnav_object = radio.get_radio_nav_object();
                    if (radio.receiving()) {
                        boolean disagree = false;
                        if ( nd_gc.mode_app && ( (rnav_object instanceof RadioNavBeacon) && ( (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_VOR) || (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_STANDALONE_DME) ) ) ) {
                            disagree = true;
                        }
                        if ( nd_gc.mode_vor && (rnav_object instanceof Localizer) ) {
                            disagree = true;
                        }
                        if (disagree) {
                            // Freq / EFIS mode disagree
                            g2.setColor(nd_gc.caution_color);
                            g2.setFont(nd_gc.font_l);
                            String disagree_str = "EFIS MODE/NAV FREQ DISAGREE";
                            int disagree_width = g2.getFontMetrics(nd_gc.font_l).stringWidth(disagree_str);
                            g2.drawString(disagree_str, nd_gc.map_center_x - disagree_width / 2, nd_gc.right_label_disagree_y);
                        }
                    }
                }
            }


        }

    }


    private void displayGlobalMessage(Graphics2D g2, String message){
        g2.setColor(nd_gc.color_lime);
        g2.setFont(nd_gc.font_xxxl);
        int message_width = g2.getFontMetrics(nd_gc.font_xxxl).stringWidth(message);
        // int box_width = message_width + 2*nd_gc.digit_width_xxxl;
        // int box_height = nd_gc.line_height_xxxl * 12/8;
        int message_x = nd_gc.map_center_x - message_width / 2;
        int message_y = nd_gc.range_mode_message_y + nd_gc.line_height_xxxl / 2;
        // int box_x = nd_gc.map_center_x - box_width / 2;
        // int box_y = nd_gc.range_mode_message_y - (box_height * 3 / 8);
        g2.drawString(message, message_x, message_y);
        // g2.setColor(nd_gc.markings_color);
        // g2.drawRect(box_x, box_y, box_width, box_height);    	
    }
    
    private void displayEGPWSMessage(Graphics2D g2){
    	if (avionics.egpws_alert_level()==EPGWSAlertLevel.NORMAL) {
    		g2.setColor(nd_gc.color_lime);
    	} else {
    		g2.setColor(nd_gc.ecam_caution_color);
    	}
        g2.setFont(nd_gc.font_xxxl);
        String message = avionics.egpws_alert_message();
        int message_width = g2.getFontMetrics(nd_gc.font_xxxl).stringWidth(message);

        int message_x = nd_gc.map_center_x - message_width / 2;
        int message_y = nd_gc.range_mode_message_y + nd_gc.line_height_xxxl / 2;

        g2.drawString(message, message_x, message_y);
 	
    }
 
    private void drawTerrainInfoBox(Graphics2D g2){
    	
    }
}
