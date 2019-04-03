/**
* ClockComponent.java
* 
* The root awt component. ClockComponent creates and manages painting all
* elements of the HSI. ClockComponent also creates and updates ClockGraphicsConfig
* which is used by all Clokc elements to determine positions and sizes.
* 
* This component is notified when new data packets from the flight simulator
* have been received and performs a repaint. This component is also triggered
* by UIHeartbeat to detect situations without reception.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2018  Nicolas Carel
* Copyright (C) 2018  the Technische Hochschule Ingolstadt 
*                     - Patrick Burkart
*                     - Tim Drouven
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
package net.sourceforge.xhsi.flightdeck.clock;

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


public class ClockComponent extends Component implements Observer, PreferencesObserver, MouseInputListener {

    private static final long serialVersionUID = 1L;
    public static boolean COLLECT_PROFILING_INFORMATION = false;
    public static long NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT = 100;
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    // subcomponents --------------------------------------------------------
    ArrayList<ClockSubcomponent> subcomponents = new ArrayList<ClockSubcomponent>();
    long[] subcomponent_paint_times = new long[15];
    long total_paint_times = 0;
    long nb_of_paints = 0;
    Graphics2D g2;
    ClockGraphicsConfig clock_gc;
    ModelFactory model_factory;
    boolean update_since_last_heartbeat = false;
    //StatusMessage status_message_comp;

    Aircraft aircraft;
    Avionics avionics;
    DU display_unit;


    public ClockComponent(ModelFactory model_factory, DU du) {

        this.clock_gc = new ClockGraphicsConfig(this, du.ordinal());
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();
        this.display_unit = du;

        clock_gc.reconfig = true;
        
        addMouseListener(this);
        addMouseMotionListener(this);
        // addKeyListener(this);

        addComponentListener(clock_gc);
        subcomponents.add(new ClockFrame(model_factory, clock_gc, this));
        subcomponents.add(new ClockDial(model_factory, clock_gc, this));
        subcomponents.add(new ClockDigital(model_factory, clock_gc, this));

        this.repaint();

    }


    public Dimension getPreferredSize() {
        return new Dimension(ClockGraphicsConfig.INITIAL_PANEL_SIZE + 2*ClockGraphicsConfig.INITIAL_BORDER_SIZE, ClockGraphicsConfig.INITIAL_PANEL_SIZE + 2*ClockGraphicsConfig.INITIAL_BORDER_SIZE);
    }


    public void paint(Graphics g) {
    	clock_gc.current_time_millis=System.currentTimeMillis();
        drawAll(g);
    }


    public void drawAll(Graphics g) {

        g2 = (Graphics2D)g;
        g2.setRenderingHints(clock_gc.rendering_hints);
        g2.setStroke(new BasicStroke(2.0f));

        if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_LIGHT) ||
                XHSIPreferences.get_instance().get_relief_border() ) {
            g2.setBackground(clock_gc.backpanel_color);
        } else if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_DARK) ) {
            g2.setBackground(clock_gc.frontpanel_color);
        } else {
            g2.setBackground(Color.BLACK);
        }

        // send Graphics object to annun_gc to recompute positions, if necessary because the panel has been resized or a mode setting has been changed
        // TODO: this.avionics.get_clock_style()
        clock_gc.update_config( g2, this.aircraft.battery(), this.avionics.get_clock_style(), this.avionics.du_brightness(display_unit) );

        // rotate the display
        XHSIPreferences.Orientation orientation = XHSIPreferences.get_instance().get_panel_orientation( this.clock_gc.display_unit );
        if ( orientation == XHSIPreferences.Orientation.LEFT ) {
            g2.rotate(-Math.PI/2.0, clock_gc.frame_size.width/2, clock_gc.frame_size.width/2);
        } else if ( orientation == XHSIPreferences.Orientation.RIGHT ) {
            g2.rotate(Math.PI/2.0, clock_gc.frame_size.height/2, clock_gc.frame_size.height/2);
        } else if ( orientation == XHSIPreferences.Orientation.DOWN ) {
            g2.rotate(Math.PI, clock_gc.frame_size.width/2, clock_gc.frame_size.height/2);
        }

        g2.clearRect(0, 0, clock_gc.frame_size.width, clock_gc.frame_size.height);

        long time = 0;
        long paint_time = 0;

        for (int i=0; i<this.subcomponents.size(); i++) {
            if (ClockComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }

            // paint each of the subcomponents
            this.subcomponents.get(i).paint(g2);

            if (ClockComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        clock_gc.reconfigured = false;

        this.nb_of_paints += 1;

        if (ClockComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % ClockComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
                logger.info("Paint profiling info");
                logger.info("=[ Paint profile info begin ]=================================");
                for (int i=0;i<this.subcomponents.size();i++) {
                    logger.info(this.subcomponents.get(i).toString() + ": " +
                            ((1.0f*this.subcomponent_paint_times[i])/(this.nb_of_paints*1.0f)) + "ms " +
                            "(" + ((this.subcomponent_paint_times[i] * 100) / this.total_paint_times) + "%)");
                    this.subcomponent_paint_times[i] = 0;
                }
                logger.info("Total                    " + (1.0f*this.total_paint_times/this.nb_of_paints*1.0f) + "ms \n");
                logger.info("=[ Paint profile info end ]===================================");
                this.total_paint_times = 0;
                this.nb_of_paints = 0;
            }
        }
    }


    public void update() {
        repaint();
        this.update_since_last_heartbeat = true;
    }


    public void componentResized() {
    }


    public void preference_changed(String key) {
        this.clock_gc.reconfig = true;
        repaint();
    }


    public void forceReconfig() {
        componentResized();
        this.clock_gc.reconfig = true;
        repaint();      
    }
    

    @Override
    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < subcomponents.size(); i++) {
            ((ClockSubcomponent) subcomponents.get(i)).mouseClicked(g2, e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (int i = 0; i < subcomponents.size(); i++) {
            ((ClockSubcomponent) subcomponents.get(i)).mousePressed(g2, e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (int i = 0; i < subcomponents.size(); i++) {
            ((ClockSubcomponent) subcomponents.get(i)).mouseReleased(g2, e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (int i = 0; i < subcomponents.size(); i++) {
            ((ClockSubcomponent) subcomponents.get(i)).mouseDragged(g2, e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
