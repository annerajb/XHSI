/**
* CDUQpac.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CduLine;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.QpacMcduData;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPSender;


public class CDUQpac extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;
    
    List<ClickRegion> regions;
    boolean drawregions = false;
    XPlaneUDPSender udp_sender = null; 
    
    int displayunit_topleft_x = 76;
    int displayunit_topleft_y = 49;
    double row_coef = 19.8;
    int upper_y = 2;
    double scratch_y_coef = 13.0;
    double char_width_coef = 1.5; 
    
    // Key codes (values passed with QPAC_KEY_PRESS ID)
    public static final int QPAC_KEY_TO_CONFIG = 1;
    public static final int QPAC_KEY_PUSH_ALT  = 2;
    public static final int QPAC_KEY_PULL_ALT  = 3;
    public static final int QPAC_KEY_PUSH_VS   = 4;
    public static final int QPAC_KEY_PULL_VS   = 5;
    public static final int QPAC_KEY_PUSH_HDG  = 6;
    public static final int QPAC_KEY_PULL_HDG  = 7;
    public static final int QPAC_KEY_PUSH_SPD  = 8;
    public static final int QPAC_KEY_PULL_SPD  = 9;
    public static final int QPAC_KEY_ATHR      = 10;
    public static final int QPAC_KEY_APPR      = 11;
    public static final int QPAC_KEY_EXPED     = 12;
    public static final int QPAC_KEY_LOC       = 13;
    public static final int QPAC_KEY_ABRK_LOW  = 14;
    public static final int QPAC_KEY_ABRK_MED  = 15;
    public static final int QPAC_KEY_ABRK_MAX  = 16;
    // MCDU1 KEYS
    public static final int QPAC_KEY_MDCU1_INIT       = 43;
    public static final int QPAC_KEY_MDCU1_DATA       = 44;
    public static final int QPAC_KEY_MDCU1_MENU       = 45;
    public static final int QPAC_KEY_MDCU1_PERF       = 46;
    public static final int QPAC_KEY_MDCU1_PROG       = 47;
    public static final int QPAC_KEY_MDCU1_FPLN       = 48;
    public static final int QPAC_KEY_MDCU1_DIR_TO     = 49;
    public static final int QPAC_KEY_MDCU1_RAD_NAV    = 50;
    public static final int QPAC_KEY_MDCU1_AIRPORT    = 51;
    public static final int QPAC_KEY_MDCU1_SLEW_UP    = 52;
    public static final int QPAC_KEY_MDCU1_SLEW_DOWN  = 53;
    public static final int QPAC_KEY_MDCU1_SLEW_LEFT  = 54;
    public static final int QPAC_KEY_MDCU1_SLEW_RIGHT = 55;
    public static final int QPAC_KEY_MDCU1_LSK1L      = 56;
    public static final int QPAC_KEY_MDCU1_LSK2L      = 57;
    public static final int QPAC_KEY_MDCU1_LSK3L      = 58;
    public static final int QPAC_KEY_MDCU1_LSK4L      = 59;
    public static final int QPAC_KEY_MDCU1_LSK5L      = 60;
    public static final int QPAC_KEY_MDCU1_LSK6L      = 61;
    public static final int QPAC_KEY_MDCU1_LSK1R      = 62;
    public static final int QPAC_KEY_MDCU1_LSK2R      = 63;
    public static final int QPAC_KEY_MDCU1_LSK3R      = 64;
    public static final int QPAC_KEY_MDCU1_LSK4R      = 65;
    public static final int QPAC_KEY_MDCU1_LSK5R      = 66;
    public static final int QPAC_KEY_MDCU1_LSK6R      = 67;
    public static final int QPAC_KEY_MDCU1_DEL        = 68;
    public static final int QPAC_KEY_MDCU1_SPACE      = 69;
    public static final int QPAC_KEY_MDCU1_OVERFL     = 70;
    public static final int QPAC_KEY_MDCU1_PLUS_M     = 71;
    public static final int QPAC_KEY_MDCU1_DOT        = 72;
    public static final int QPAC_KEY_MDCU1_SLASH      = 73;
    public static final int QPAC_KEY_MDCU1_0          = 74;
    public static final int QPAC_KEY_MDCU1_A          = 84;

    
    // JarDesign A320neo MCDU KEYS
    public static final int JAR_A320_MCDU_CLICK_INIT       = 10;
    public static final int JAR_A320_MCDU_CLICK_DATA       = 1;
    public static final int JAR_A320_MCDU_CLICK_MENU       = 0;
    public static final int JAR_A320_MCDU_CLICK_PERF       = 14;
    public static final int JAR_A320_MCDU_CLICK_PROG       = 11;
    public static final int JAR_A320_MCDU_CLICK_FPLN       = 3;
    public static final int JAR_A320_MCDU_CLICK_DIR_TO     = 12;
    public static final int JAR_A320_MCDU_CLICK_RAD_NAV    = 13;
    public static final int JAR_A320_MCDU_CLICK_AIRPORT    = 2;
    public static final int JAR_A320_MCDU_CLICK_SLEW_UP    = 9;
    public static final int JAR_A320_MCDU_CLICK_SLEW_DOWN  = 7;
    public static final int JAR_A320_MCDU_CLICK_SLEW_LEFT  = 6;
    public static final int JAR_A320_MCDU_CLICK_SLEW_RIGHT = 8;
    public static final int JAR_A320_MCDU_CLICK_LSK1L      = 21;
    public static final int JAR_A320_MCDU_CLICK_LSK2L      = 22;
    public static final int JAR_A320_MCDU_CLICK_LSK3L      = 23;
    public static final int JAR_A320_MCDU_CLICK_LSK4L      = 24;
    public static final int JAR_A320_MCDU_CLICK_LSK5L      = 25;
    public static final int JAR_A320_MCDU_CLICK_LSK6L      = 26;
    public static final int JAR_A320_MCDU_CLICK_LSK1R      = 27;
    public static final int JAR_A320_MCDU_CLICK_LSK2R      = 28;
    public static final int JAR_A320_MCDU_CLICK_LSK3R      = 29;
    public static final int JAR_A320_MCDU_CLICK_LSK4R      = 30;
    public static final int JAR_A320_MCDU_CLICK_LSK5R      = 31;
    public static final int JAR_A320_MCDU_CLICK_LSK6R      = 32;
    public static final int JAR_A320_MCDU_CLICK_DEL        = 20;
    public static final int JAR_A320_MCDU_CLICK_SPACE      = 18;
    public static final int JAR_A320_MCDU_CLICK_OVERFL     = 19;
    public static final int JAR_A320_MCDU_CLICK_PLUS_M     = 17;
    public static final int JAR_A320_MCDU_CLICK_DOT        = 15;
    public static final int JAR_A320_MCDU_CLICK_SLASH      = 16;
    public static final int JAR_A320_MCDU_CLICK_0          = 33;
    public static final int JAR_A320_MCDU_CLICK_1          = 34;
    public static final int JAR_A320_MCDU_CLICK_2          = 35;
    public static final int JAR_A320_MCDU_CLICK_3          = 36;
    public static final int JAR_A320_MCDU_CLICK_4          = 37;
    public static final int JAR_A320_MCDU_CLICK_5          = 38;
    public static final int JAR_A320_MCDU_CLICK_6          = 39;
    public static final int JAR_A320_MCDU_CLICK_7          = 40;
    public static final int JAR_A320_MCDU_CLICK_8          = 41;
    public static final int JAR_A320_MCDU_CLICK_9          = 42;
    public static final int JAR_A320_MCDU_CLICK_A          = 43;

    
    
    public CDUQpac(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
        udp_sender = XPlaneUDPSender.get_instance();

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

    }

    public void paint(Graphics2D g2) {
    	if ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_LEGACY) && (this.avionics.is_qpac() || this.avionics.is_jar_a320neo() )
    			) {
    		if ( this.preferences.cdu_display_only() ) {
    			drawDisplayOnly(g2);
    		} else {
    			// drawFullPanel(g2);
    			drawDisplayOnly(g2);
    		}
    	}
    }

    
    private void drawDisplayOnly(Graphics2D g2) {
        
        if ( this.aircraft.battery() || this.avionics.is_jar_a320neo() ) {
        	String str_title = QpacMcduData.getLine(0);
            
        	if (str_title.isEmpty()) {
        		str_title = "QPAC MCDU";
            	g2.setColor(Color.MAGENTA);
            	g2.setFont(cdu_gc.font_xl);
        		g2.drawString(str_title, cdu_gc.cdu_middle_x - cdu_gc.get_text_width(g2, cdu_gc.font_xl, str_title), cdu_gc.cdu_first_line);
        	} 


        	
            scalex = (double)cdu_gc.panel_rect.width /363.0; //was: 343.0
            scaley = (double)cdu_gc.panel_rect.height/289.0;
            border_x = (double)cdu_gc.border_left;
            border_y = (double)cdu_gc.border_top;

            /*
            AffineTransform orig = g2.getTransform();
            g2.translate(border_x, border_y);
            g2.scale(scalex, scaley);
            g2.translate(large_font.getSize()/2, large_font.getSize());
            
            g2.setFont(large_font);
            double dy = row_coef;
            */

            // drawDisplayLines(g2,dy );
        	drawDisplayLines(g2);
        	
            // g2.setTransform(orig);            

        } else {
        	String str_title = "POWER OFF";
           	g2.setColor(cdu_gc.ecam_caution_color);
           	g2.setFont(cdu_gc.font_xl);
       		g2.drawString(str_title, cdu_gc.cdu_middle_x - cdu_gc.get_text_width(g2, cdu_gc.font_xl, str_title), cdu_gc.cdu_first_line);
        }
        
    }
    
    private void drawFullPanel(Graphics2D g2) {
    	
    }
    
    private void decodeColor(Graphics2D g2, char color_code) {
    	switch (color_code) {
    	case 'r' : g2.setColor(cdu_gc.ecam_warning_color); break;
    	case 'b' : g2.setColor(cdu_gc.ecam_action_color); break;
    	case 'w' : g2.setColor(cdu_gc.ecam_markings_color); break;
    	case 'y' : g2.setColor(Color.YELLOW); break;
    	case 'm' : g2.setColor(Color.MAGENTA); break;
    	case 'a' : g2.setColor(cdu_gc.ecam_caution_color); break;
    	case 'g' : g2.setColor(cdu_gc.ecam_normal_color); break;
        default : g2.setColor(Color.GRAY); break;
    	}
    }
    
    private void decodeFont(Graphics2D g2, char font_code) {
    	switch (font_code) {
    	case 'l' : g2.setFont(cdu_gc.font_fixed_zl); break;
    	case 's' : g2.setFont(cdu_gc.font_fixed_xxxl); break;
        default : g2.setFont(cdu_gc.font_fixed_zl); break;
    	}
    }
    
    private String translateCduLine(String str){
    	String result = "";
    	char c;
    	for (int i=0; i<str.length(); i++) {
    		switch ( str.charAt(i) ) {
    		case '`' : c = '°'; break;
    		case '|' : c = 'Δ'; break;
    		case '*' : c = '⎕'; break;
    		case '0' : c = 'O'; break;
    		case 1 : c='?'; break;
    		case 2 : c='?'; break;
    		case 3 : c='?'; break;
    		case 4 : c='?'; break;
    		case 5 : c='?'; break;
    		case 6 : c='?'; break;
    		case 7 : c='?'; break;
    		case 8 : c='?'; break;
    		case 9 : c='?'; break;
    		case 10 : c='?'; break;
    		case 11 : c='?'; break;
    		case 12 : c='?'; break;
    		case 13 : c='?'; break;
    		case 14 : c='?'; break;
    		case 15 : c='?'; break;
    		case 16 : c='?'; break;
    		case 17 : c='?'; break;
    		case 18 : c='?'; break;
    		case 19 : c='?'; break;
    		case 20 : c='?'; break;
    		case 21 : c='?'; break;
    		case 22 : c='?'; break;
    		case 23 : c='?'; break;
    		case 24 : c='?'; break;
    		case 25 : c='?'; break;
    		case 26 : c='?'; break;
    		case 27 : c='?'; break;
    		case 28 : c='?'; break;
    		case 29 : c='?'; break;
    		case 30 : c='?'; break;
    		case 31 : c='?'; break;
    		default : c = str.charAt(i);
    		}
    		result += c;
    	}
    	return result;
    }
    
    private void drawDisplayLines(Graphics2D g2) {
    	
        for(int i=0; i < 14; i++) {        

            int x = 0, yy = 0;
            if(i==0) {
                yy = cdu_gc.cdu_first_line;
            } else if ((i > 0) && (i < 13)){
                yy = cdu_gc.cdu_first_line + cdu_gc.cdu_dy_line*i;
            } else if(i == 13) { 
                yy = cdu_gc.cdu_scratch_line;
            }
            /* Debug */
            /*
            g2.setColor(Color.GRAY);
            g2.setFont(cdu_gc.font_s);
            g2.drawString(QpacMcduData.getLine(i), cdu_gc.cdu_middle_x, yy);
            */
            
            
            List<CduLine> l = QpacMcduData.decodeLine(QpacMcduData.getLine(i));
            for(CduLine o : l){                    
                    x = (int) Math.round( o.pos * cdu_gc.digit_width_fixed_zl);
                    decodeColor(g2, o.color );
                    decodeFont(g2, o.font );
                    g2.drawString(translateCduLine(o.text), x, yy);
            }
            
            
        }
        

    }
    
    
    public void mousePressed(MouseEvent e) {
    	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_LEGACY) &&  this.avionics.is_qpac() ) {
    		for(ClickRegion r : regions){
    			int w = r.check(e.getPoint(), scalex, scaley, border, border);
    			if(w > -1) {
    				udp_sender.sendDataPoint( XPlaneSimDataRepository.QPAC_KEY_PRESS, (float) w );
    			}
    		}
    	}
    	
    }

  
    public void keyPressed(KeyEvent k) {
    	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_LEGACY) &&  this.avionics.is_qpac() ) {
    		char key = k.getKeyChar();
    		int w = -1;
    		// Test KeyChar
    		if (key >= 'a' && key <= 'z') {
    			w = QPAC_KEY_MDCU1_A + (key - 'a'); 
    		} else if (key >= 'A' && key <= 'Z') {
    			w = QPAC_KEY_MDCU1_A + (key - 'A');
    		} else if (key >= '0' && key <= '9') { 
    			w = QPAC_KEY_MDCU1_0+ (key - '0'); 
    		} else switch (key) {
    		case '.' : w = QPAC_KEY_MDCU1_DOT; break;
    		case '/' : w = QPAC_KEY_MDCU1_SLASH; break;    		
    		case '+' : w = QPAC_KEY_MDCU1_PLUS_M; break;
    		case '*' : w = QPAC_KEY_MDCU1_OVERFL; break;
    		case 127 : w = QPAC_KEY_MDCU1_DEL; break; // DEL -> CLEAR
    		case 8   : w = QPAC_KEY_MDCU1_DEL; break; // BackSpace
    		case ' ' : w = QPAC_KEY_MDCU1_SPACE; break; // EXEC
    		case 27  : w = QPAC_KEY_MDCU1_MENU; break; // MENU
    		}
    		// Test KeyCodes
    		if (w == -1.0f) switch (k.getKeyCode()) {
    		case KeyEvent.VK_F1 : w = QPAC_KEY_MDCU1_LSK1L; break;// LSK 1
    		case KeyEvent.VK_F2 : w = QPAC_KEY_MDCU1_LSK2L; break;// LSK 2
    		case KeyEvent.VK_F3 : w = QPAC_KEY_MDCU1_LSK3L; break;// LSK 3
    		case KeyEvent.VK_F4 : w = QPAC_KEY_MDCU1_LSK4L; break;// LSK 4
    		case KeyEvent.VK_F5 : w = QPAC_KEY_MDCU1_LSK5L; break;// LSK 5
    		case KeyEvent.VK_F6 : w = QPAC_KEY_MDCU1_LSK6L; break;// LSK 6
    		case KeyEvent.VK_F7 : w = QPAC_KEY_MDCU1_LSK1R; break;// RSK 1
    		case KeyEvent.VK_F8 : w = QPAC_KEY_MDCU1_LSK2R; break;// RSK 2
    		case KeyEvent.VK_F9 : w = QPAC_KEY_MDCU1_LSK3R; break;// RSK 3
    		case KeyEvent.VK_F10 : w = QPAC_KEY_MDCU1_LSK4R; break;// RSK 4
    		case KeyEvent.VK_F11 : w = QPAC_KEY_MDCU1_LSK5R; break;// RSK 5
    		case KeyEvent.VK_F12 : w = QPAC_KEY_MDCU1_LSK6R; break;// RSK 6
    		case KeyEvent.VK_UP : w = QPAC_KEY_MDCU1_SLEW_UP; break;
    		case KeyEvent.VK_DOWN : w = QPAC_KEY_MDCU1_SLEW_DOWN; break;
    		case KeyEvent.VK_LEFT : w = QPAC_KEY_MDCU1_SLEW_LEFT; break;
    		case KeyEvent.VK_RIGHT : w = QPAC_KEY_MDCU1_SLEW_RIGHT; break; 
    		
    		case KeyEvent.VK_PAGE_UP : w = QPAC_KEY_MDCU1_PERF; break;
    		case KeyEvent.VK_PAGE_DOWN : w = QPAC_KEY_MDCU1_PROG; break;
    		case KeyEvent.VK_HOME : w = QPAC_KEY_MDCU1_INIT; break;
    		case KeyEvent.VK_END : w = QPAC_KEY_MDCU1_FPLN; break; 
    		case KeyEvent.VK_INSERT : w = QPAC_KEY_MDCU1_DIR_TO; break;
    		}

    		if (w > -0.5f) udp_sender.sendDataPoint( XPlaneSimDataRepository.QPAC_KEY_PRESS, (float) w );
    		// logger.info("MCDU Key pressed : " + k.getKeyChar() + " " + w);
    	}    
    
	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_LEGACY) &&  this.avionics.is_jar_a320neo() ) {
		char key = k.getKeyChar();
		int w = -1;
		// Test KeyChar
		if (key >= 'a' && key <= 'z') {
			w = JAR_A320_MCDU_CLICK_A + (key - 'a'); 
		} else if (key >= 'A' && key <= 'Z') {
			w = JAR_A320_MCDU_CLICK_A + (key - 'A');
		} else if (key >= '0' && key <= '9') { 
			w = JAR_A320_MCDU_CLICK_0+ (key - '0'); 
		} else switch (key) {
		case '.' : w = JAR_A320_MCDU_CLICK_DOT; break;
		case '/' : w = JAR_A320_MCDU_CLICK_SLASH; break;    		
		case '+' : w = JAR_A320_MCDU_CLICK_PLUS_M; break;
		case '*' : w = JAR_A320_MCDU_CLICK_OVERFL; break;
		case 127 : w = JAR_A320_MCDU_CLICK_DEL; break; // DEL -> CLEAR
		case 8   : w = JAR_A320_MCDU_CLICK_DEL; break; // BackSpace
		case ' ' : w = JAR_A320_MCDU_CLICK_SPACE; break; // EXEC
		case 27  : w = JAR_A320_MCDU_CLICK_MENU; break; // MENU
		}
		// Test KeyCodes
		if (w == -1.0f) switch (k.getKeyCode()) {
		case KeyEvent.VK_F1 : w = JAR_A320_MCDU_CLICK_LSK1L; break;// LSK 1
		case KeyEvent.VK_F2 : w = JAR_A320_MCDU_CLICK_LSK2L; break;// LSK 2
		case KeyEvent.VK_F3 : w = JAR_A320_MCDU_CLICK_LSK3L; break;// LSK 3
		case KeyEvent.VK_F4 : w = JAR_A320_MCDU_CLICK_LSK4L; break;// LSK 4
		case KeyEvent.VK_F5 : w = JAR_A320_MCDU_CLICK_LSK5L; break;// LSK 5
		case KeyEvent.VK_F6 : w = JAR_A320_MCDU_CLICK_LSK6L; break;// LSK 6
		case KeyEvent.VK_F7 : w = JAR_A320_MCDU_CLICK_LSK1R; break;// RSK 1
		case KeyEvent.VK_F8 : w = JAR_A320_MCDU_CLICK_LSK2R; break;// RSK 2
		case KeyEvent.VK_F9 : w = JAR_A320_MCDU_CLICK_LSK3R; break;// RSK 3
		case KeyEvent.VK_F10 : w = JAR_A320_MCDU_CLICK_LSK4R; break;// RSK 4
		case KeyEvent.VK_F11 : w = JAR_A320_MCDU_CLICK_LSK5R; break;// RSK 5
		case KeyEvent.VK_F12 : w = JAR_A320_MCDU_CLICK_LSK6R; break;// RSK 6
		case KeyEvent.VK_UP : w = JAR_A320_MCDU_CLICK_SLEW_UP; break;
		case KeyEvent.VK_DOWN : w = JAR_A320_MCDU_CLICK_SLEW_DOWN; break;
		case KeyEvent.VK_LEFT : w = JAR_A320_MCDU_CLICK_SLEW_LEFT; break;
		case KeyEvent.VK_RIGHT : w = JAR_A320_MCDU_CLICK_SLEW_RIGHT; break; 
		
		case KeyEvent.VK_PAGE_UP : w = JAR_A320_MCDU_CLICK_PERF; break;
		case KeyEvent.VK_PAGE_DOWN : w = JAR_A320_MCDU_CLICK_PROG; break;
		case KeyEvent.VK_HOME : w = JAR_A320_MCDU_CLICK_INIT; break;
		case KeyEvent.VK_END : w = JAR_A320_MCDU_CLICK_FPLN; break; 
		case KeyEvent.VK_INSERT : w = JAR_A320_MCDU_CLICK_DIR_TO; break;
		}

		if (w > -0.5f) udp_sender.sendDataPoint( XPlaneSimDataRepository.JAR_A320NEO_MCDU_CLICK, (float) w );
		// logger.info("MCDU Key pressed : " + k.getKeyChar() + " " + w);
	}    
}
    
	

}
