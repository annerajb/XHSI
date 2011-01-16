/**
* XHSIComponent.java
* 
* The root awt component. XHSIComponent creates and manages painting all
* elements of the HSI. XHSIComponent also creates and updates XHSIGraphicsConfig
* which is used by all HSI elements to determine positions and sizes.
* 
* This component is notified when new data packets from the flightsimulator
* have been received and performs a repaint. This component is also triggered
* by UIHeartbeat to detect situations without reception.
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
package net.sourceforge.xhsi.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Observer;


public class XHSIComponent extends Component implements Observer, PreferencesObserver {

    private static final long serialVersionUID = 1L;
    public static boolean COLLECT_PROFILING_INFORMATION = false;
    public static long NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT = 100;
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    // subcomponents --------------------------------------------------------
    ArrayList<Component> subcomponents = new ArrayList<Component>();
    long[] subcomponent_paint_times = new long[15];
    long total_paint_times = 0;
    long nb_of_paints = 0;
    Graphics2D g2;
    XHSIGraphicsConfig xhsi_gc;
    ModelFactory model_factory;
    boolean update_since_last_heartbeat = false;
    StatusMessage status_message_comp;

    Aircraft aircraft;
    Avionics avionics;


    public XHSIComponent(ModelFactory model_factory) {

        this.xhsi_gc = new XHSIGraphicsConfig(this);
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();

        addComponentListener(xhsi_gc);
        subcomponents.add(new StatusBar(model_factory, xhsi_gc, this));

    }


    public Dimension getPreferredSize() {
        return new Dimension(XHSIGraphicsConfig.INITIAL_PANEL_WIDTH, XHSIGraphicsConfig.INITIAL_PANEL_HEIGHT);
    }


    public void paint(Graphics g) {

        g2 = (Graphics2D)g;
        g2.setRenderingHints(xhsi_gc.rendering_hints);
        g2.setStroke(new BasicStroke(2.0f));
        g2.setBackground(xhsi_gc.background_color);

        // send Graphics object to xhsi_gc to recompute positions, if necessary because the panel has been resized or a mode setting has been changed
        xhsi_gc.update_config( g2, this.avionics.map_mode(), this.avionics.map_submode(), this.avionics.map_range() );

        g2.clearRect(0,0,xhsi_gc.panel_size.width, xhsi_gc.panel_size.height);

        long time = 0;
        long paint_time = 0;

        for (int i=0; i<this.subcomponents.size(); i++) {
            if (XHSIComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }
            // paint each of the subcomponents
            ((XHSISubcomponent) this.subcomponents.get(i)).paint(g2);

            if (XHSIComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        this.nb_of_paints += 1;

        if (XHSIComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % XHSIComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
                logger.info("Paint profiling info");
                logger.info("=[ Paint profile info begin ]=================================");
                for (int i=0;i<this.subcomponents.size();i++) {
                    logger.info(this.subcomponents.get(i).toString() + ": " +
                            ((1.0f*this.subcomponent_paint_times[i])/(this.nb_of_paints*1.0f)) + "ms " +
                            "(" + ((this.subcomponent_paint_times[i] * 100) / this.total_paint_times) + "%)");
                //    this.subcomponent_paint_times[i] = 0;
                }
                logger.info("Total                    " + (this.total_paint_times/this.nb_of_paints) + "ms \n");
                logger.info("=[ Paint profile info end ]===================================");
                //this.total_paint_times = 0;
                //this.nb_of_paints = 0;
            }
        }
    }


    public void show_status_message(String text) {
        this.status_message_comp.set_message(text);
        repaint(100);
    }


    public void hide_status_message() {
        this.status_message_comp.clear();
        repaint();
    }


    public void update() {
        repaint();
        this.update_since_last_heartbeat = true;
    }


    public void heartbeat() {
        if (this.update_since_last_heartbeat == false) {
            XHSIStatus.status = XHSIStatus.STATUS_NO_RECEPTION;
            repaint();
        } else {
            XHSIStatus.status = XHSIStatus.STATUS_RECEIVING;
            this.update_since_last_heartbeat = false;
            repaint();
        }
    }


    public void componentResized() {
    }


    public void preference_changed(String key) {

        logger.finest("Preference changed");
        // if (key.equals(XHSIPreferences.PREF_USE_MORE_COLOR)) {
        // Don't bother checking the preference key that was changed, just reconfig...
        this.xhsi_gc.reconfig = true;

    }

}
