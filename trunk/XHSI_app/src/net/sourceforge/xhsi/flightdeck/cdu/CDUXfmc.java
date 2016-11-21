/**
* XfmcDisplay.java
* 
* The root awt component. NDComponent creates and manages painting all
* elements of the HSI. NDComponent also creates and updates NDGraphicsConfig
* which is used by all HSI elements to determine positions and sizes.
* 
* This component is notified when new data packets from the flightsimulator
* have been received and performs a repaint. This component is also triggered
* by UIHeartbeat to detect situations without reception.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  qwerty 
* Copyright (C) 2015  Nicolas Carel
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


import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.XfmcData;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPSender;

public class CDUXfmc extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private BufferedImage image = null;
    private BufferedImage ap_img = null;
    private BufferedImage athr_img = null;
    private BufferedImage exec_img = null;
    private BufferedImage lnav_img = null;
    private BufferedImage vnav_img = null;

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;
    List<ClickRegion> regions;
    boolean drawregions = false;
    Font font;

    int displayunit_topleft_x = 76;
    int displayunit_topleft_y = 49;
    double row_coef = 19.8;
    int upper_y = 2;
    double scratch_y_coef = 13.0;
    double char_width_coef = 1.5; 

    XfmcData xfmcData = null;
    XPlaneUDPSender udp_sender = null; 

    
    
    public CDUXfmc(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
        
        try {
        	image = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_800x480.png"));
		ap_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_ap_litv_m.png"));
		athr_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_athr_litv_m.png"));
		exec_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_exec_litv_m.png"));
		lnav_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_lnav_litv_m.png"));
		vnav_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_vnav_litv_m.png"));
        	
        } catch (IOException ioe){}
        
        // Andale Mono is one of the Core Webfonts, Lucida Console is not! https://en.wikipedia.org/wiki/Core_fonts_for_the_Web
        font = new Font("Andale Mono",1, 18);        

        regions = new ArrayList<ClickRegion>();

        // LSK
        regions.add(new ClickRegion(new Point(6, 62), new Point(48+26, 300), 1, 6, 
                        new int[][] {{0}, {1}, {2}, {3}, {4}, {5}} ));

        // RSK
        regions.add(new ClickRegion(new Point(432-26, 62), new Point(474, 300), 1, 6, 
                        new int[][] {{6}, {7}, {8}, {9}, {10}, {11}} ));

        // A..Z, SP, DEL, /, CLR
        regions.add(new ClickRegion(new Point(192, 452), new Point(432, 774), 5, 6,
                        new int[][] {
                                {27, 28, 29, 30, 31},
                                {32, 33, 34, 35, 36},
                                {37, 38, 39, 40, 41},
                                {42, 43, 44, 45, 46},
                                {47, 48, 49, 50, 51},
                                {52, -1, 54, 55, 56}} ));

        // 1..9, ., 0, +/-
        regions.add(new ClickRegion(new Point(52, 562), new Point(186, 768), 3, 4, 
                        new int[][] {{57, 58, 59}, {60, 61, 62}, {63, 64, 65}, {66, 67, 68}} ));

        // INIT REF, RTE, DEP ARR, AP, VNAV, BRT, FIX, LEGS, HOLD, PERF, PROG, EXEC
        regions.add(new ClickRegion(new Point(52, 348), new Point(436, 452), 6, 2, 
                        new int[][] {{12, 13, 14, 15, 16, -1}, {17, 18, 19, 20, 21, 22}} ));

        // MENU, NAV RAD, PREV PAGE, NEXT PAGE
        regions.add(new ClickRegion(new Point(52, 454), new Point(180, 554), 2, 2, 
                        new int[][] {{23, 24}, {25, 26}} ));

        xfmcData = XfmcData.getInstance();
        udp_sender = XPlaneUDPSender.get_instance();

        xfmcData.setLine(0, "1/1,38,Remote CDU for X-FMC");
        
        logger.finest("CDUXfmc instanciated");
    }
    
    
    public void paint(Graphics2D g2) {
    	if (cdu_gc.cdu_source == Avionics.CDU_SOURCE_XFMC) {
    		if ( this.preferences.cdu_display_only() ) {
    			drawDisplayOnly(g2);
    		} else {
    			drawFullPanel(g2);
    		}
    	}
    }

    
    private void drawDisplayOnly(Graphics2D g2) {
        
        if ( cdu_gc.powered ) {
            
            scalex = (double)cdu_gc.panel_rect.width /363.0; //was: 343.0
            scaley = (double)cdu_gc.panel_rect.height/289.0;
            border_x = (double)cdu_gc.border_left;
            border_y = (double)cdu_gc.border_top;

            AffineTransform orig = g2.getTransform();
            g2.translate(border_x, border_y);
            g2.scale(scalex, scaley);
            g2.translate(font.getSize()/2, font.getSize());
            
            g2.setFont(font);
            double dy = row_coef;

            drawDisplayLines(g2, dy);

            g2.setTransform(orig);

        }
        
    }
    
    
    private void drawFullPanel(Graphics2D g2) {

        scalex = (double)cdu_gc.panel_rect.width /image.getWidth();
        scaley = (double)cdu_gc.panel_rect.height/image.getHeight();
        border = (double)cdu_gc.border;

        AffineTransform orig = g2.getTransform();
        g2.translate(border, border);
        g2.scale(scalex, scaley);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(image, null, 0, 0);


        if ( cdu_gc.powered ) {

            int stat = 0;
            try{
                stat = Integer.parseInt(xfmcData.getLine(14));
            } catch (Exception e) {}


            if((stat & 1) == 1) {
                g2.drawImage(ap_img,null,250, 354);
            }
            if((stat & 2) == 2) {
                g2.drawImage(lnav_img,null,25, 541);
            }
            if(!((stat & 4) == 4)) {
                g2.drawImage(vnav_img,null,25, 612);
            }
            if((stat & 8) == 8) {
                g2.drawImage(athr_img,null,435, 541);
            }
            if((stat & 32) == 32) {
                g2.drawImage(exec_img,null, 383, 397);
            }

            g2.translate(displayunit_topleft_x, displayunit_topleft_y);
            g2.setFont(font);
            double dy = row_coef;

            drawDisplayLines(g2, dy);

            g2.setTransform(orig);

            // for debugging
            if ( drawregions ) {
                g2.setColor(cdu_gc.dim_markings_color);
                for(ClickRegion r2 : regions){
                        r2.draw(g2, scalex, scaley, border, border);
                }
            }

        }
        
    }

    
    private void drawDisplayLines(Graphics2D g2, double dy) {
    
            for(int i=0; i < 14; i++){
                int x=i, xx = 0, yy = 0;
                if(i==0) {
                    // xx = 0;
                    yy = upper_y;
                } else if ((i > 0) && (i < 13)){
                    x = (((i+1) / 2) * 2) - ((i % 2) == 1 ? 0 : 1);
                    yy = new Double(dy*(i)).intValue();
                } else if(i == 13) { 
                    // xx = 0;
                    yy = new Double(dy*scratch_y_coef).intValue();
                }

                List l = xfmcData.decodeLine(xfmcData.getLine(x));
                for(Object o : l){
                        Object[] pts = (Object[]) o;
                        xx = new Double((Integer)pts[1]*char_width_coef).intValue();
                        if (i ==13) {
                        	g2.setColor(cdu_gc.cdu_scratch_pad_color);
                        } else {
                        	g2.setColor(((Integer)pts[0]).intValue() == 0 ? cdu_gc.cdu_title_color : cdu_gc.cdu_data_color);	
                        }                      
                        g2.drawString((String)pts[2], xx, yy);
                 }
            }
            xfmcData.updated=false;
    }
    
    public void mousePressed(Graphics2D g2, MouseEvent e) {
    	if (cdu_gc.cdu_source == Avionics.CDU_SOURCE_XFMC) {
    		Point true_click = e.getPoint();
    		AffineTransform current_transform = g2.getTransform();
    		try {
    			current_transform.invert();
    			current_transform.transform(e.getPoint(), true_click);
    		} catch (NoninvertibleTransformException e1) {
    		}
    		for(ClickRegion r : regions){
    			int w = r.check(true_click, scalex, scaley, border, border);
    			if(w > -1) {
    				udp_sender.sendDataPoint( XPlaneSimDataRepository.XFMC_KEYPATH, (float) w );
    			}
    		}
    	}
    	
    }

  
    public void keyPressed(KeyEvent k) {
    	if (cdu_gc.cdu_source == Avionics.CDU_SOURCE_XFMC) {
    		char key = k.getKeyChar();
    		float w = -1.0f;
    		// Test KeyChar
    		if (key >= 'a' && key <= 'z') {
    			w = 27.0f + (key - 'a'); 
    		} else if (key >= 'A' && key <= 'Z') {
    			w = 27.0f + (key - 'A');
    		} else if (key >= '1' && key <= '9') { 
    			w = 57.0f + (key - '1'); 
    		} else switch (key) {
    		case '.' : w = 66.0f; break;
    		case '/' : w = 55.0f; break;
    		case '0' : w = 67.0f; break;
    		case '+' : w = 68.0f; break;
    		case 127 : w = 56.0f; break; // DEL -> CLEAR
    		case 8   : w = 54.0f; break; // BackSpace
    		case 13  : w = 22.0f; break; // EXEC
    		case 27  : w = 23.0f; break; // MENU
    		}
    		// Test KeyCodes
    		if (w == -1.0f) switch (k.getKeyCode()) {
    		case KeyEvent.VK_F1 : w = 0.0f; break;// LSK 1
    		case KeyEvent.VK_F2 : w = 1.0f; break;// LSK 2
    		case KeyEvent.VK_F3 : w = 2.0f; break;// LSK 3
    		case KeyEvent.VK_F4 : w = 3.0f; break;// LSK 4
    		case KeyEvent.VK_F5 : w = 4.0f; break;// LSK 5
    		case KeyEvent.VK_F6 : w = 5.0f; break;// LSK 6
    		case KeyEvent.VK_F7 : w = 6.0f; break;// RSK 1
    		case KeyEvent.VK_F8 : w = 7.0f; break;// RSK 2
    		case KeyEvent.VK_F9 : w = 8.0f; break;// RSK 3
    		case KeyEvent.VK_F10 : w = 9.0f; break;// RSK 4
    		case KeyEvent.VK_F11 : w = 10.0f; break;// RSK 5
    		case KeyEvent.VK_F12 : w = 11.0f; break;// RSK 6
    		case KeyEvent.VK_PAGE_UP : w = 26.0f; break;
    		case KeyEvent.VK_PAGE_DOWN : w = 25.0f; break;
    		case KeyEvent.VK_HOME : w = 12.0f; break;// INIT
    		case KeyEvent.VK_END : w = 13.0f; break; // RTE
    		}

    		if (w > -0.5f) udp_sender.sendDataPoint( XPlaneSimDataRepository.XFMC_KEYPATH, (float) w );
    		// logger.info("MCDU Key pressed : " + k.getKeyChar() + " " + w);
    	}    
    }
    
	
}
