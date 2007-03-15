/**
* HSIComponent.java
* 
* The root awt component. HSIComponent creates and manages painting all
* elements of the HSI. HSIComponent also creates and updates HSIGraphicsContext
* which is used by all HSI elements to determine positions and sizes.
* 
* This component is notified and when new data packets from the flightsimulator
* have been received and performs a repaint. This component is also triggered
* by UIHeartbeat to detect situations without reception.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import de.georg_gruetter.xhsi.HSIStatus;
import de.georg_gruetter.xhsi.model.ModelFactory;
import de.georg_gruetter.xhsi.model.Observer;

public class HSIComponent extends Component implements Observer {
	
	private static final long serialVersionUID = 1L;
	public static boolean COLLECT_PROFILING_INFORMATION = false;
	public static long NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT = 100;
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");

	
	// subcomponents --------------------------------------------------------
	ArrayList subcomponents = new ArrayList();
	long[] subcomponent_paint_times = new long[15];
	long total_paint_times = 0;
	long nb_of_paints = 0;
	Graphics2D g2;
	HashMap rendering_hints = new HashMap();
	HSIGraphicsConfig hsi_gc;
	ModelFactory model_factory;
	boolean update_since_last_heartbeat = false;
	StatusMessage status_message_comp;
	
	public HSIComponent(ModelFactory model_factory) {
		hsi_gc = new HSIGraphicsConfig(this);
		this.model_factory = model_factory;
		addComponentListener(hsi_gc);
		subcomponents.add(new MovingMap(model_factory, hsi_gc));
		subcomponents.add(new CompassRose(model_factory, hsi_gc)); 
		subcomponents.add(new SpeedsLabel(model_factory, hsi_gc));
		subcomponents.add(new NextFMSEntryLabel(model_factory, hsi_gc));
		subcomponents.add(new HeadingLabel(model_factory, hsi_gc));
		subcomponents.add(new APHeading(model_factory, hsi_gc));
		subcomponents.add(new RadioHeadingArrows(model_factory, hsi_gc));
		subcomponents.add(new RadioLabel(model_factory, hsi_gc));
		subcomponents.add(new PositionTrendVector(model_factory, hsi_gc));
		subcomponents.add(new InstrumentFrame(model_factory, hsi_gc));
		subcomponents.add(new StatusBar(model_factory, hsi_gc));
		
		this.status_message_comp = new StatusMessage(model_factory, hsi_gc);		
		subcomponents.add(this.status_message_comp);
		
		rendering_hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rendering_hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);		
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(HSIGraphicsConfig.INITIAL_PANEL_WIDTH,HSIGraphicsConfig.INITIAL_PANEL_HEIGHT);
	}
	
	public void paint(Graphics g) {
		
		g2 = (Graphics2D)g;
		g2.setRenderingHints(rendering_hints);;
		g2.setStroke(new BasicStroke(2.0f));
		g2.setBackground(Color.BLACK);
		
		// send Graphics object to hsi_gc to recompute positions, if necessary
		hsi_gc.update_config(g2);
		
		g2.clearRect(0,0,hsi_gc.panel_size.width, hsi_gc.panel_size.height);
		
		long time = 0;
		long paint_time = 0;
		
		for (int i=0;i<this.subcomponents.size();i++) {
			if (HSIComponent.COLLECT_PROFILING_INFORMATION) {
				time = System.currentTimeMillis();
			}
			((HSISubcomponent) this.subcomponents.get(i)).paint(g2);
			
			if (HSIComponent.COLLECT_PROFILING_INFORMATION) {
				paint_time = System.currentTimeMillis() - time;
				this.subcomponent_paint_times[i] += paint_time;
				this.total_paint_times += paint_time;
			}
		}		
		
		this.nb_of_paints += 1;
		
		if (HSIComponent.COLLECT_PROFILING_INFORMATION) {
			if (this.nb_of_paints % HSIComponent.NB_OF_PAINTS_BETWEEN_PROFILING_INFO_OUTPUT == 0) {
				logger.info("Paint profiling info");
				logger.info("=[ Paint profile info begin ]=================================");
				for (int i=0;i<this.subcomponents.size();i++) {
					logger.info(this.subcomponents.get(i).toString() + ": " + 
							(this.subcomponent_paint_times[i]/this.nb_of_paints) + "ms " +
							"(" + ((this.subcomponent_paint_times[i] * 100) / this.total_paint_times) + "%)");
				}
				logger.info("Total					" + (this.total_paint_times/this.nb_of_paints) + "ms \n");
				logger.info("=[ Paint profile info end ]===================================");
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
			HSIStatus.status = HSIStatus.STATUS_NO_RECEPTION;
			repaint();
		} else {
			HSIStatus.status = HSIStatus.STATUS_RECEIVING;
			this.update_since_last_heartbeat = false;
			repaint();
		}
	}
	
	public void componentResized() {
	}
}
