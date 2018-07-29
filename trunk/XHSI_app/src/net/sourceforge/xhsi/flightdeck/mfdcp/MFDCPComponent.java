/**
 * MFDCPComponent.java
 *
 * The root awt component. MFDCPComponent creates and manages painting all
 * elements of the MFD Control Panel.
 * MFDCPComponent also creates and updates MFDCPGraphicsConfig
 * which is used by all HSI elements to determine positions and sizes.
 *
 * This component is notified when new data packets from the flightsimulator
 * have been received and performs a repaint. This component is also triggered
 * by UIHeartbeat to detect situations without reception.
 *
 * Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
 * Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.event.MouseInputListener;

import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.XHSIPreferences;

import net.sourceforge.xhsi.XHSIInstrument.DU;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Observer;
import net.sourceforge.xhsi.util.ColorUtilities;


public class MFDCPComponent extends Component implements Observer, PreferencesObserver, MouseInputListener {

    private static final long serialVersionUID = 1L;
    public static boolean COLLECT_PROFILING_INFORMATION = false;
    public static long NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT = 100;
    private static Logger logger = Logger.getLogger("xhsi");

    // subcomponents --------------------------------------------------------
    ArrayList<MFDCPSubcomponent> subcomponents = new ArrayList<MFDCPSubcomponent>();
    long[] subcomponent_paint_times = new long[15];
    long total_paint_times = 0;
    long nb_of_paints = 0;
    Graphics2D g2;
    MFDCPGraphicsConfig mfdcp_gc;
    ModelFactory model_factory;
    boolean update_since_last_heartbeat = false;
    //StatusMessage status_message_comp;

    Aircraft aircraft;
    Avionics avionics;
    DU display_unit;

    public MFDCPComponent(ModelFactory model_factory, DU du) {

        this.mfdcp_gc = new MFDCPGraphicsConfig(this, du.ordinal());
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();
        this.display_unit = du;

        mfdcp_gc.reconfig = true;

        this.addMouseListener(this);

        addComponentListener(mfdcp_gc);
        subcomponents.add(new MFDControlPanel(model_factory, mfdcp_gc, this));

        this.repaint();

    }

    public Dimension getPreferredSize() {
        return new Dimension(MFDCPGraphicsConfig.INITIAL_PANEL_SIZE + 2 * MFDCPGraphicsConfig.INITIAL_BORDER_SIZE, MFDCPGraphicsConfig.INITIAL_PANEL_SIZE + 2 * MFDCPGraphicsConfig.INITIAL_BORDER_SIZE);
    }

    public void paint(Graphics g) {
        mfdcp_gc.current_time_millis = System.currentTimeMillis();
        drawAll(g);

    }

    public void drawAll(Graphics g) {

        g2 = (Graphics2D) g;
        g2.setRenderingHints(mfdcp_gc.rendering_hints);
        g2.setStroke(new BasicStroke(2.0f));

        // double cockpit_light_level = aircraft.get_cockpit_light_level();
        // TODO
        double cockpit_light_level = 0.78;

        if (XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_LIGHT)
                || XHSIPreferences.get_instance().get_relief_border()) {
            g2.setBackground(ColorUtilities.get_color(ColorUtilities.AIRBUS_PANEL, cockpit_light_level));
        } else if (XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_DARK)) {
            g2.setBackground(ColorUtilities.get_color(ColorUtilities.AIRBUS_FRONT_PANEL, cockpit_light_level));
        } else {
            g2.setBackground(Color.BLACK);
        }

        // double thi_fcu = this.avionics.get_FCU_brightness();
        // TODO
        double thi_fcu = 0.7;

        // send Graphics object to mfdcp_gc to recompute positions, if necessary because the panel has been resized or a mode setting has been changed
        mfdcp_gc.update_config(g2, this.aircraft.battery(), this.avionics.get_instrument_style(), thi_fcu, cockpit_light_level);

        // rotate the display
        XHSIPreferences.Orientation orientation = XHSIPreferences.get_instance().get_panel_orientation(this.mfdcp_gc.display_unit);
        if (orientation == XHSIPreferences.Orientation.LEFT) {
            g2.rotate(-Math.PI / 2.0, mfdcp_gc.frame_size.width / 2, mfdcp_gc.frame_size.width / 2);
        } else if (orientation == XHSIPreferences.Orientation.RIGHT) {
            g2.rotate(Math.PI / 2.0, mfdcp_gc.frame_size.height / 2, mfdcp_gc.frame_size.height / 2);
        } else if (orientation == XHSIPreferences.Orientation.DOWN) {
            g2.rotate(Math.PI, mfdcp_gc.frame_size.width / 2, mfdcp_gc.frame_size.height / 2);
        }

        g2.clearRect(0, 0, mfdcp_gc.frame_size.width, mfdcp_gc.frame_size.height);

        long time = 0;
        long paint_time = 0;
        for (int i = 0; i < this.subcomponents.size(); i++) {
            if (MFDCPComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }

            // paint each of the subcomponents
            this.subcomponents.get(i).paint(g2);

            if (MFDCPComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        mfdcp_gc.reconfigured = false;

        this.nb_of_paints += 1;

        if (MFDCPComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % MFDCPComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
                logger.info("Paint profiling info");
                logger.info("=[ Paint profile info begin ]=================================");
                for (int i = 0; i < this.subcomponents.size(); i++) {
                    logger.info(this.subcomponents.get(i).toString() + ": "
                            + ((1.0f * this.subcomponent_paint_times[i]) / (this.nb_of_paints * 1.0f)) + "ms "
                            + "(" + ((this.subcomponent_paint_times[i] * 100) / this.total_paint_times) + "%)");
                    //    this.subcomponent_paint_times[i] = 0;
                }
                logger.info("Total                    " + (this.total_paint_times / this.nb_of_paints) + "ms \n");
                logger.info("=[ Paint profile info end ]===================================");
                //this.total_paint_times = 0;
                //this.nb_of_paints = 0;
            }
        }
    }

    public void update() {
        repaint();
        this.update_since_last_heartbeat = true;
    }

    public void heartbeat() {
/*
        //Called every watch_interval, set to 1000ms as of now (Feb 2018)
        double outside_light = aircraft.get_cockpit_light_level();

        double ohp_background_light_setting = aircraft.cockpit_lights_intensity();
        mfdcp_gc.update_mfd_panel_colors(ohp_background_light_setting, outside_light);
        */
    }

    public void componentResized() {
    }

    public void preference_changed(String key) {

        logger.finest("Preference changed");
        // if (key.equals(XHSIPreferences.PREF_USE_MORE_COLOR)) {
        // Don't bother checking the preference key that was changed, just reconfig...
        this.mfdcp_gc.reconfig = true;
        repaint();

    }

    public void forceReconfig() {

        componentResized();
        this.mfdcp_gc.reconfig = true;
        repaint();

    }

    /**
     * Puts absolute coordinates in relation to width. The method assumes for a
     * square of 200 * 200 px. The center is at 100|100.
     *
     * @param relation the actual width of the square / 2
     * @param coordinate the coordinate based from the center of a 200x200 px
     * square
     * @return a coordinate representing the same position in a square with the
     * size (2+relation)*(2*relation)
     */
    private static int inRel(double relation, int coordinate) {
        return (int) (relation * coordinate);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < subcomponents.size(); i++) {
            subcomponents.get(i).mouseClicked(g2, e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //nothing needs to be done here
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //nothing needs to be done here
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //nothing needs to be done here
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //nothing needs to be done here
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //nothing needs to be done here
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //nothing needs to be done here
    }
}
