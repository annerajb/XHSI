/**
* CDUComponent.java
* 
* The root awt component. CDUComponent creates and manages painting all
* elements of the CDU inside XHSI. CDUComponent also creates and updates CDUGraphicsConfig
* which is used by all CDU XHSI elements to determine positions and sizes.
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
package net.sourceforge.xhsi.flightdeck.cdu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import net.sourceforge.xhsi.model.QpacMcduData;
import net.sourceforge.xhsi.model.XfmcData;


public class CDUComponent extends Component implements Observer, PreferencesObserver, MouseInputListener, KeyListener {

    private static final long serialVersionUID = 1L;
    public static boolean COLLECT_PROFILING_INFORMATION = false;
    public static long NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT = 100;
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    // subcomponents --------------------------------------------------------
    ArrayList<CDUSubcomponent> subcomponents = new ArrayList<CDUSubcomponent>();
    long[] subcomponent_paint_times = new long[15];
    long total_paint_times = 0;
    long nb_of_paints = 0;
    long paint_timestamp =0;
    Graphics2D g2;
    CDUGraphicsConfig cdu_gc;
    ModelFactory model_factory;
    XHSIPreferences preferences;
    boolean update_since_last_heartbeat = false;

    Aircraft aircraft;
    Avionics avionics;
    QpacMcduData qpac_mcdu_data;
    XfmcData xfmc_data;
    DU display_unit;


    public CDUComponent(ModelFactory model_factory, DU du) {

        this.cdu_gc = new CDUGraphicsConfig(this, du.ordinal());
        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();
        this.preferences = XHSIPreferences.get_instance();
        this.qpac_mcdu_data = QpacMcduData.getInstance();
        this.xfmc_data = XfmcData.getInstance();
        this.display_unit = du;

        cdu_gc.reconfig = true;

        addMouseListener(this);
        addKeyListener(this);
        
        addComponentListener(cdu_gc);
        subcomponents.add(new CDUFrame(model_factory, cdu_gc, this));
        subcomponents.add(new CDUXfmc(model_factory, cdu_gc, this));
        subcomponents.add(new CDUUfmc(model_factory, cdu_gc, this));
        subcomponents.add(new CDUQpac(model_factory, cdu_gc, this));
        subcomponents.add(new CDUJarDesign(model_factory, cdu_gc, this));
        subcomponents.add(new CDUZiboMod737(model_factory, cdu_gc, this));
        subcomponents.add(new CDUDefault(model_factory, cdu_gc, this));

        this.repaint();
        this.setFocusable(true);
        this.requestFocus();
        
        paint_timestamp = System.currentTimeMillis();

    }


    public Dimension getPreferredSize() {
        return new Dimension(CDUGraphicsConfig.INITIAL_PANEL_SIZE + 2*CDUGraphicsConfig.INITIAL_BORDER_SIZE, CDUGraphicsConfig.INITIAL_PANEL_SIZE + 2*CDUGraphicsConfig.INITIAL_BORDER_SIZE);
    }


    public void paint(Graphics g) {
    	cdu_gc.current_time_millis=System.currentTimeMillis();
        drawAll(g);
    }


    public void drawAll(Graphics g) {

        g2 = (Graphics2D)g;
        g2.setRenderingHints(cdu_gc.rendering_hints);
        g2.setStroke(new BasicStroke(2.0f));

        if ( XHSIPreferences.get_instance().cdu_display_only() ) {
            g2.setBackground(cdu_gc.background_color);
        } else {
            if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_LIGHT) ||
                    XHSIPreferences.get_instance().get_relief_border() ) {
                g2.setBackground(cdu_gc.backpanel_color);
            } else if ( XHSIPreferences.get_instance().get_border_style().equalsIgnoreCase(XHSIPreferences.BORDER_DARK) ) {
                g2.setBackground(cdu_gc.frontpanel_color);
            } else {
                g2.setBackground(Color.BLACK);
            }
        }

        // send Graphics object to annun_gc to recompute positions, if necessary because the panel has been resized or a mode setting has been changed
        cdu_gc.update_config( g2, this.avionics.power(), this.avionics.get_cdu_source(), this.preferences.cdu_display_only(), this.avionics.du_brightness(display_unit) );

        // rotate the display
        XHSIPreferences.Orientation orientation = XHSIPreferences.get_instance().get_panel_orientation( this.cdu_gc.display_unit );
        if ( orientation == XHSIPreferences.Orientation.LEFT ) {
            g2.rotate(-Math.PI/2.0, cdu_gc.frame_size.width/2, cdu_gc.frame_size.width/2);
        } else if ( orientation == XHSIPreferences.Orientation.RIGHT ) {
            g2.rotate(Math.PI/2.0, cdu_gc.frame_size.height/2, cdu_gc.frame_size.height/2);
        } else if ( orientation == XHSIPreferences.Orientation.DOWN ) {
            g2.rotate(Math.PI, cdu_gc.frame_size.width/2, cdu_gc.frame_size.height/2);
        }

        g2.clearRect(0, 0, cdu_gc.frame_size.width, cdu_gc.frame_size.height);

        long time = 0;
        long paint_time = 0;
        long elapsed_time = 0; 

        for (int i=0; i<this.subcomponents.size(); i++) {
            if (CDUComponent.COLLECT_PROFILING_INFORMATION) {
                time = System.currentTimeMillis();
            }

            // paint each of the subcomponents
            this.subcomponents.get(i).paint(g2);

            if (CDUComponent.COLLECT_PROFILING_INFORMATION) {
                paint_time = System.currentTimeMillis() - time;
                this.subcomponent_paint_times[i] += paint_time;
                this.total_paint_times += paint_time;
            }
        }

        cdu_gc.reconfigured = false;

        this.nb_of_paints += 1;

        if (CDUComponent.COLLECT_PROFILING_INFORMATION) {
            if (this.nb_of_paints % CDUComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
            	elapsed_time = System.currentTimeMillis() - paint_timestamp;
            	float refresh_rate = nb_of_paints*1000.0f/elapsed_time;
                logger.info("Paint profiling info");
                logger.info("=[ Paint profile info begin ]=================================");
                for (int i=0;i<this.subcomponents.size();i++) {
                    logger.info(this.subcomponents.get(i).toString() + ": " +
                            ((1.0f*this.subcomponent_paint_times[i])/(this.nb_of_paints*1.0f)) + "ms " +
                            "(" + ((this.subcomponent_paint_times[i] * 100) / this.total_paint_times) + "%)");
                //    this.subcomponent_paint_times[i] = 0;
                }
                logger.info("Refresh rate             " + refresh_rate + "fps");
                logger.info("Total                    " + (this.total_paint_times/this.nb_of_paints) + "ms");
                logger.info("=[ Paint profile info end ]===================================");
                this.total_paint_times = 0;
                this.nb_of_paints = 0;
                this.paint_timestamp = System.currentTimeMillis();
            }
        }
    }


    /*
     * update() is called by XPlaneSimDataRepository
     * update() is triggered by xplane_data_repository.tick_updates();
     */
    public void update() {
    	// TODO: repaint only if cdu_packet received or if mcdu_packet received
    	if ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && avionics.is_qpac() && qpac_mcdu_data.updated) {
    		repaint();
    	} else if (cdu_gc.cdu_source == Avionics.CDU_SOURCE_XFMC && this.xfmc_data.updated) {	
    		repaint();
    	}
        
        this.update_since_last_heartbeat = true;
    }

    public void heartbeat() {
        repaint();
    }

    public void componentResized() {
    }


    public void preference_changed(String key) {
        logger.finest("Preference changed");
        this.cdu_gc.reconfig = true;
        repaint();
    }


    public void forceReconfig() {
        componentResized();
        this.cdu_gc.reconfig = true;
        repaint();
    }
    
	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
        for (int i=0;i<this.subcomponents.size();i++) {
            ((CDUSubcomponent)this.subcomponents.get(i)).mousePressed(g2, e);
        }
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}    
	
	public void keyPressed(KeyEvent k) {
        for (int i=0;i<this.subcomponents.size();i++) {
            ((CDUSubcomponent)this.subcomponents.get(i)).keyPressed(k);
        }
	}

	public void keyTyped(KeyEvent k) {
	}

	public void keyReleased(KeyEvent k) {
	}
	
}
