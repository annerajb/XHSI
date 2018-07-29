/**
 * MFDControlPanel is a clickable control panel for the lower ECAM (Airbus Terminology) / MFD (Boeing Terminology).
 *
 * Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
 * Copyright (C) 2016  Nicolas Carel
 * Copyright (C) 2017  Patrick Burkart (pburkartpublic@gmail.com) (Technische Hochschule Ingolstadt)
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

package net.sourceforge.xhsi.flightdeck.mfdcp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.xhsi.flightdeck.GraphicsConfig;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimCommand;
import net.sourceforge.xhsi.model.xplane.XPlaneCommand;
import net.sourceforge.xhsi.util.ColorUtilities;


public class MFDControlPanel extends MFDCPSubcomponent {

    private static final long serialVersionUID = 1L;

    // We'll need these here for the mouse events
    // RoundRectangle2D[] button = new RoundRectangle2D[21];
    // Rectangle2D[] buttonLight = new Rectangle2D[18];
    // private int clickMargin = 10;

    private static final String SOURCE_GROUND = "GND";
    private static final String SOURCE_NRST = "NRST";
    private static final String SOURCE_LOC = "LOC";
    private static final String SOURCE_FMS = "FMS";
    String source = SOURCE_GROUND;

    private static Logger logger = Logger.getLogger("xhsi");

    public MFDControlPanel(ModelFactory model_factory, MFDCPGraphicsConfig mfdcp_gc, Component parent_component) {
        super(model_factory, mfdcp_gc, parent_component);
    }

    @Override
    public void paint(Graphics2D g2) {
    	/*
        int x = mfdcp_gc.ecp_rectangle.x;
        int y = mfdcp_gc.ecp_rectangle.y;
        double r_w = mfdcp_gc.ecp_rectangle.width / 830.0;
        double r_h = mfdcp_gc.ecp_rectangle.height / 480.0;
        */

        double alpha = aircraft.cockpit_light_level();
        // double alpha = 0.6;
     		
        
        if (!avionics.is_ff_a320()) {
            // Draw a line to separate standard Airbus buttons from XHSI specific ones
            g2.setColor(mfdcp_gc.panel_main_text_color);
            g2.drawLine(mfdcp_gc.sep_line_x, mfdcp_gc.sep_line_top, mfdcp_gc.sep_line_x, mfdcp_gc.sep_line_bottom);

            // Draw Display for Nearest Airport ICAO
            g2.setColor(Color.BLACK);
            g2.fillRect(mfdcp_gc.dest_box_x, mfdcp_gc.dest_box_y, mfdcp_gc.dest_box_w, mfdcp_gc.dest_box_h);
            // Draw the text inside the display
            g2.setColor(mfdcp_gc.panel_display_main_color);
            g2.setFont(mfdcp_gc.font_7seg);
            g2.drawString(getDestination(), mfdcp_gc.dest_value_x, mfdcp_gc.dest_value_y);
            // Draw the text around the display
            g2.setColor(mfdcp_gc.panel_main_text_color);
            g2.setFont(mfdcp_gc.font_label_small);
            g2.drawString("GROUND", mfdcp_gc.label_ground_x, mfdcp_gc.label_ground_y);
            g2.drawString("NRST", mfdcp_gc.label_nrst_x, mfdcp_gc.label_nrst_y);
            g2.drawString("LOC", mfdcp_gc.label_loc_x, mfdcp_gc.label_loc_y);
            g2.drawString("FMS", mfdcp_gc.label_fms_x, mfdcp_gc.label_fms_y);
            g2.setColor(source.equalsIgnoreCase(SOURCE_GROUND) ? Color.GREEN : Color.decode("#383600"));
            g2.fillOval(mfdcp_gc.led_left, mfdcp_gc.led_top, mfdcp_gc.led_r_x, mfdcp_gc.led_r_y);
            g2.setColor(source.equalsIgnoreCase(SOURCE_NRST) ? Color.GREEN : Color.decode("#383600"));
            g2.fillOval(mfdcp_gc.led_left, mfdcp_gc.led_bottom, mfdcp_gc.led_r_x, mfdcp_gc.led_r_y);
            g2.setColor(source.equalsIgnoreCase(SOURCE_LOC) ? Color.GREEN : Color.decode("#383600"));
            g2.fillOval(mfdcp_gc.led_right, mfdcp_gc.led_top, mfdcp_gc.led_r_x, mfdcp_gc.led_r_y);
            g2.setColor(source.equalsIgnoreCase(SOURCE_FMS) ? Color.GREEN : Color.decode("#383600"));
            g2.fillOval(mfdcp_gc.led_right, mfdcp_gc.led_bottom, mfdcp_gc.led_r_x, mfdcp_gc.led_r_y);
        }

        // Draw the buttons
        
        // TODO: Warning, call to new inside display loop !!!
        for (int i = 0; i < mfdcp_gc.button.length; i++) {
            if (i == 1) {
                g2.setColor(ColorUtilities.blend(Color.decode("#FF5528"), new Color(81, 26, 11), alpha));
            } else {
                g2.setColor(ColorUtilities.blend(mfdcp_gc.color_airbus_button, new Color(2, 3, 2), alpha));
            }
            if (mfdcp_gc.button[i] != null) {
                g2.fill(mfdcp_gc.button[i]);
            }
        }
        for (int i = 0; i < mfdcp_gc.buttonLight.length; i++) {
        	if (i==12 || i==14) {
        		// Master Clear / Accept
        		if (aircraft.master_accept()) {
        			g2.setColor(ColorUtilities.blend(new Color(31, 255, 31), new Color(3, 252, 3), alpha));
        		} else {
        			g2.setColor(ColorUtilities.blend(new Color(25, 30, 27), new Color(12, 10, 12), alpha));
        		}
        		g2.fill(mfdcp_gc.buttonLight[i]);
        	} else if (i != 11 && i < 15) {//"ALL" has no light and only do standard airbus lights
        		if ((avionics.get_mfd_mode() - 3 == i && i < 12) || i == 13 && avionics.get_mfd_mode() - 2 == i) {
        			g2.setColor(ColorUtilities.blend(new Color(31, 255, 31), new Color(3, 252, 3), alpha));
        		} else {
        			g2.setColor(ColorUtilities.blend(new Color(25, 30, 27), new Color(12, 10, 12), alpha));
        		}
        		g2.fill(mfdcp_gc.buttonLight[i]);
        	} else if (i > 14 && !avionics.is_ff_a320()) {//do the XHSI specific lights
        		if ((avionics.get_mfd_mode() + 15) == i) {
        			g2.setColor(ColorUtilities.blend(new Color(3, 252, 3), new Color(31, 255, 31), alpha));
        		} else {
        			g2.setColor(ColorUtilities.blend(new Color(38, 56, 38), new Color(11, 17, 11), alpha));
        		}
        		if (mfdcp_gc.buttonLight[i] != null) {
        			g2.fill(mfdcp_gc.buttonLight[i]);
        		}

        	}
        }

  
        
        String[] names = new String[]{"T.O.\nCONFIG", "EMER\nCANC", "ENG", "BLEED", "PRESS", "ELEC", "HYD", "FUEL", "APU", "COND", "DOOR", "WHEEL", "F/CTL", "ALL", "CLR", "STS", "RCL", "CLR"};
        g2.setColor(mfdcp_gc.panel_main_text_color);
        g2.setFont(mfdcp_gc.font_label);
        g2.drawString("T.O.", mfdcp_gc.btn_label_to_x, mfdcp_gc.btn_label_to_y);
        g2.drawString("CONFIG", mfdcp_gc.btn_label_config_x, mfdcp_gc.btn_label_config_y);
        g2.drawString("EMER", mfdcp_gc.btn_label_emer_x, mfdcp_gc.btn_label_emer_y);
        g2.drawString("CANC", mfdcp_gc.btn_label_canc_x, mfdcp_gc.btn_label_canc_y);
        if (!avionics.is_ff_a320()) {
            g2.drawString("CHART", mfdcp_gc.btn_label_chart_x, mfdcp_gc.btn_label_chart_y);
            g2.drawString("F/PLN", mfdcp_gc.btn_label_fpln_x, mfdcp_gc.btn_label_fpln_y);
            g2.drawString("RTU", mfdcp_gc.btn_label_rtu_x, mfdcp_gc.btn_label_rtu_y);
        }

        for (int i = 0; i < 6; i++) {
            g2.drawString(names[i + 2], mfdcp_gc.ecp_rectangle.x + GraphicsConfig.inRel(mfdcp_gc.r_w, i * 111 + 53 + 46) - mfdcp_gc.get_text_width(g2, mfdcp_gc.font_label, names[i + 2]) / 2, mfdcp_gc.ecp_rectangle.y + GraphicsConfig.inRel(mfdcp_gc.r_h, 144 + 50));
            if (i == 5) {
                g2.setFont(mfdcp_gc.font_header);
            } else {
                g2.setFont(mfdcp_gc.font_label);
            }
            g2.drawString(names[i + 8], mfdcp_gc.ecp_rectangle.x + GraphicsConfig.inRel(mfdcp_gc.r_w, i * 111 + 53 + 46) - mfdcp_gc.get_text_width(g2, i == 5 ? mfdcp_gc.font_header : mfdcp_gc.font_label, names[i + 8]) / 2, mfdcp_gc.ecp_rectangle.y + GraphicsConfig.inRel(mfdcp_gc.r_h, 238 + 50 - (i == 5 ? 10 : 0)));
        }
        g2.setFont(mfdcp_gc.font_label);
        g2.drawString(names[14], mfdcp_gc.ecp_rectangle.x + GraphicsConfig.inRel(mfdcp_gc.r_w, 53 + 46) - mfdcp_gc.get_text_width(g2, mfdcp_gc.font_label, names[14]) / 2, mfdcp_gc.ecp_rectangle.y + GraphicsConfig.inRel(mfdcp_gc.r_h, 332 + 50));
        g2.drawString(names[15], mfdcp_gc.ecp_rectangle.x + GraphicsConfig.inRel(mfdcp_gc.r_w, 275 + 46) - mfdcp_gc.get_text_width(g2, mfdcp_gc.font_label, names[15]) / 2, mfdcp_gc.ecp_rectangle.y + GraphicsConfig.inRel(mfdcp_gc.r_h, 332 + 50));
        g2.drawString(names[17], mfdcp_gc.ecp_rectangle.x + GraphicsConfig.inRel(mfdcp_gc.r_w, 608 + 46) - mfdcp_gc.get_text_width(g2, mfdcp_gc.font_label, names[17]) / 2, mfdcp_gc.ecp_rectangle.y + GraphicsConfig.inRel(mfdcp_gc.r_h, 332 + 50));
        g2.setFont(mfdcp_gc.font_header);

        g2.drawString(names[16], mfdcp_gc.ecp_rectangle.x + GraphicsConfig.inRel(mfdcp_gc.r_w, 386 + 46) - mfdcp_gc.get_text_width(g2, mfdcp_gc.font_header, names[16]) / 2, mfdcp_gc.ecp_rectangle.y + GraphicsConfig.inRel(mfdcp_gc.r_h, 332 + 40));

        if (preferences.get_draw_mouse_areas()) drawMouseAreas(g2);
    }


    private String getDestination() {

        String dest_arpt_str = "";
        //Show that it is working if battery is switched on
        if (this.aircraft.battery()) {
            dest_arpt_str = "----";
        }

        if (this.aircraft.on_ground()) {
            // when we are on the ground, take the nearest airport
            // (which is the airport that we are really at in 99.99% of the cases)
            dest_arpt_str = this.aircraft.get_nearest_arpt();
            this.source = SOURCE_GROUND;
        } else if (!this.preferences.get_arpt_chart_nav_dest()) {
            // always display nearest
            dest_arpt_str = this.aircraft.get_nearest_arpt();
            this.source = SOURCE_NRST;
        } else {

            // search for a destination airport that corresponds to the nav-source
            if (dest_arpt_str.equals("----")) {
                // if not, get the airport of the LOC/ILS that we are we tuned to, and selected as NAV source
                dest_arpt_str = get_nav_dest();
                this.source = SOURCE_LOC;
            }

            if (dest_arpt_str.equals("----")) {
                // if not, the destination airport in the FMS
                dest_arpt_str = get_fms_dest();
                this.source = SOURCE_FMS;
            }

            if (dest_arpt_str.equals("----")) {
                // if not, the nearest airport...
                dest_arpt_str = this.aircraft.get_nearest_arpt();
                this.source = SOURCE_NRST;
            }

        }
        if (dest_arpt_str.equals("----") || dest_arpt_str.isEmpty()) {
            this.source = "NO SOURCE";
        }
        return dest_arpt_str;

    }

    private String get_nav_dest() {

        // if we are tuned to a localizer, find the airport for that localizer
        String dest_str = "----";

        Localizer dest_loc = null;
        int hsi_source = this.avionics.hsi_source();
        int bank = 0;
        // use the bank that is our reference source
        if (hsi_source == Avionics.HSI_SOURCE_NAV1) {
            bank = 1;
        } else if (hsi_source == Avionics.HSI_SOURCE_NAV2) {
            bank = 2;
        }
        if (bank > 0) {
            dest_loc = this.avionics.get_tuned_localizer(bank);
            if (dest_loc != null) {
                // we are tuned to a Localizer, now fetch the airport that goes with it
                dest_str = dest_loc.airport;
                this.source = "(NAV" + Integer.toString(bank) + ")";
            }
        }

        return dest_str;

    }

    private String get_fms_dest() {

        // see if the last waypoint in the FMS is an airport
        String dest_str = "----";

        if (this.avionics.hsi_source() == Avionics.HSI_SOURCE_GPS) {
            FMSEntry last_wpt = this.avionics.get_fms().get_last_waypoint();
            if ((last_wpt != null) && (last_wpt.type == FMSEntry.ARPT)) {
                dest_str = last_wpt.name;
                this.source = "(FMS)";
            }
        }

        return dest_str;

    }

    public void drawMouseAreas(Graphics2D g2) {
    	g2.setColor(Color.yellow);
    	for (int i = 0; i < mfdcp_gc.button.length; i++) {
    		g2.draw(mfdcp_gc.button[i]);
    	}
    }
    
    public void mouseClicked(Graphics2D g2, MouseEvent e) {

        for (int i = 0; i < mfdcp_gc.button.length; i++) {
            if (mfdcp_gc.button[i] != null && aircraft != null) {
                if ((e.getButton() == 1) && mfdcp_gc.button[i].intersects(e.getX() - mfdcp_gc.clickMargin, e.getY() - mfdcp_gc.clickMargin, 2 * mfdcp_gc.clickMargin, 2 * mfdcp_gc.clickMargin)) {
                    switch (i) {
                        case 0:
                            this.aircraft.get_sim_command().send(SimCommand.CMD_ECAM_TO_CFG);
                            break;
                        case 1:
                            this.aircraft.get_sim_command().send(SimCommand.CMD_ECAM_EMER_CANC);
                            break;
                        case 2:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_ENGINE);
                            break;
                        case 3:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_BLEED);
                            break;
                        case 4:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_CAB_PRESS);
                            break;
                        case 5:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_ELEC);
                            break;
                        case 6:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_HYDR);
                            break;
                        case 7:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_FUEL);
                            break;
                        case 8:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_APU);
                            break;
                        case 9:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_COND);
                            break;
                        case 10:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_DOOR_OXY);
                            break;
                        case 11:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_WHEELS);
                            break;
                        case 12:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_FCTL);
                            break;
                        case 13:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_SYS);
                            break;
                        case 14:
                            this.aircraft.get_sim_command().send(SimCommand.CMD_MASTER_ACC);
                            break;
                        case 15:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_STATUS);
                            break;
                        case 16:
                        	this.aircraft.get_sim_command().send(SimCommand.CMD_ECAM_RCL);
                            break;
                        case 17:
                            this.aircraft.get_sim_command().send(SimCommand.CMD_MASTER_ACC);
                            break;
                        case 18:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_ARPT);
                            break;
                        case 19:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_FPLN);
                            break;
                        case 20:
                            this.avionics.set_mfd_mode(Avionics.MFD_MODE_RTU);
                            break;
                    }
                }
            }
        }
    }

}
