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
import net.sourceforge.xhsi.model.CduLine;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.QpacMcduData;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPSender;


public class CDUQpac extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private BufferedImage image = null;

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;
    
    List<ClickRegion> qpac_regions;

    boolean drawregions = false;
    XPlaneUDPSender udp_sender = null; 
    QpacMcduData qpac_mcdu_data;
    
    int displayunit_topleft_x = 81;
    int displayunit_topleft_y = 56;
    int displayunit_width = 338;
    int displayunit_heigth = 400;

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

    
    public CDUQpac(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
        
        try {
        	image = ImageIO.read(this.getClass().getResourceAsStream("img/mcdu_a320_800x480.png"));      	
        } catch (IOException ioe){}
        
        udp_sender = XPlaneUDPSender.get_instance();
        
        qpac_mcdu_data = QpacMcduData.getInstance();

        qpac_regions = new ArrayList<ClickRegion>();        

        // QPAC MCDU Keyboard mapping
        // LSK
        qpac_regions.add(new ClickRegion(new Point(6, 95), new Point(48+26, 365), 1, 6, 
                        new int[][] {
        	{QPAC_KEY_MDCU1_LSK1L}, 
        	{QPAC_KEY_MDCU1_LSK2L},
        	{QPAC_KEY_MDCU1_LSK3L},
        	{QPAC_KEY_MDCU1_LSK4L},
        	{QPAC_KEY_MDCU1_LSK5L},
        	{QPAC_KEY_MDCU1_LSK6L}} ));

        // RSK
        qpac_regions.add(new ClickRegion(new Point(432-26, 95), new Point(474, 365), 1, 6, 
                        new int[][] {
        	{QPAC_KEY_MDCU1_LSK1R},
        	{QPAC_KEY_MDCU1_LSK2R},
        	{QPAC_KEY_MDCU1_LSK3R},
        	{QPAC_KEY_MDCU1_LSK4R},
        	{QPAC_KEY_MDCU1_LSK5R},
        	{QPAC_KEY_MDCU1_LSK6R}} ));

        // A..Z, SP, DEL, /, CLR
        qpac_regions.add(new ClickRegion(new Point(192, 490), new Point(440, 785), 5, 6,
                        new int[][] {
            {84, 85, 86, 87, 88},
            {89, 90, 91, 92, 93},
            {94, 95, 96, 97, 98},
            {99, 100, 101, 102, 103},
            {104, 105, 106, 107, 108},
            {109, QPAC_KEY_MDCU1_SLASH, QPAC_KEY_MDCU1_SPACE, QPAC_KEY_MDCU1_OVERFL, QPAC_KEY_MDCU1_DEL}} ));

        // 1..9, ., 0, +/-
        qpac_regions.add(new ClickRegion(new Point(43, 608), new Point(186, 785), 3, 4, 
                        new int[][] {
        	{75, 76, 77},
        	{78, 79, 80},
        	{81, 82, 83}, 
        	{QPAC_KEY_MDCU1_DOT, QPAC_KEY_MDCU1_0, QPAC_KEY_MDCU1_PLUS_M}} ));

        
        // DIR, PROG, PERF, INIT, DATA, blank
        // F-PLN, RAD-NAV, FUEL-PRED, SEC-FPLN, ATC-COMM, MCDU MENU
        qpac_regions.add(new ClickRegion(new Point(46, 400), new Point(402, 485), 6, 2, 
                        new int[][] {
        	{QPAC_KEY_MDCU1_DIR_TO, QPAC_KEY_MDCU1_PROG, QPAC_KEY_MDCU1_PERF, QPAC_KEY_MDCU1_INIT, QPAC_KEY_MDCU1_DATA, -1},
        	{QPAC_KEY_MDCU1_FPLN, QPAC_KEY_MDCU1_RAD_NAV, -1, -1, -1, QPAC_KEY_MDCU1_MENU}} ));

        // AIRPORT, blank
        // LEFT, UP
        // RIGHT, DOWN
        qpac_regions.add(new ClickRegion(new Point(46, 486), new Point(170, 607), 2, 3, 
                        new int[][] {
        		{QPAC_KEY_MDCU1_AIRPORT, -1}, 
        		{QPAC_KEY_MDCU1_SLEW_LEFT, QPAC_KEY_MDCU1_SLEW_UP},
        		{QPAC_KEY_MDCU1_SLEW_RIGHT, QPAC_KEY_MDCU1_SLEW_DOWN}} ));
        
        logger.finest("CDUQpac instanciated");
    }

    public void paint(Graphics2D g2) {
    	if ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && (this.avionics.is_qpac()  )
    			) {
    		if ( this.preferences.cdu_display_only() ) {
    			drawDisplayOnly(g2);
    		} else {
    			drawFullPanel(g2);
    		}
    	}
    }

    
    private void drawDisplayOnly(Graphics2D g2) {
        
        if ( cdu_gc.powered ) {
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

            drawDisplayLines(g2);

        } else {
        	String str_title = "POWER OFF";
           	g2.setColor(cdu_gc.ecam_caution_color);
           	g2.setFont(cdu_gc.font_xl);
       		g2.drawString(str_title, cdu_gc.cdu_middle_x - cdu_gc.get_text_width(g2, cdu_gc.font_xl, str_title), cdu_gc.cdu_first_line);
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
        g2.setTransform(orig);
        if ( cdu_gc.powered ) {
        	drawDisplayLines(g2);
        }
        g2.setTransform(orig);
        // for debugging
        if ( drawregions ) {
            g2.setColor(cdu_gc.dim_markings_color);
            for(ClickRegion r2 : qpac_regions){
                    r2.draw(g2, scalex, scaley, border, border);
            }
        }
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
    	case 'l' : g2.setFont(cdu_gc.cdu_normal_font); break;
    	case 's' : g2.setFont(cdu_gc.cdu_small_font); break;
        default : g2.setFont(cdu_gc.cdu_normal_font); break;
    	}
    }
    
    private String translateCduLine(String str){
    	String result = "";
    	char c;
    	for (int i=0; i<str.length(); i++) {
    		switch ( str.charAt(i) ) {
    		case '`' : c = '°'; break;
    		case '|' : c = 'Δ'; break;    		
    		case '0' : c = 'O'; break;
    		case 30 : c = '←'; break; 
    		case 31 : c = '→'; break;
    		case 26 : c = '↑'; break;
    		case 27 : c = '↓'; break;
    		case 28 : c = '⎕'; break;
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
                    x = (int) Math.round( cdu_gc.cdu_screen_topleft_x + o.pos * cdu_gc.cdu_digit_width);
                    decodeColor(g2, o.color );
                    decodeFont(g2, o.font );
                    g2.drawString(translateCduLine(o.text), x, yy);
            }    
        }
        qpac_mcdu_data.updated = false;
    }

    
    public void mousePressed(Graphics2D g2, MouseEvent e) {
		Point true_click = e.getPoint();
		AffineTransform current_transform = g2.getTransform();
		try {
			current_transform.invert();
			current_transform.transform(e.getPoint(), true_click);
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		
		// logger.info("MCDU Click x="+ true_click.x + " y="+true_click.y+ "   /  mouse x="+e.getPoint().x+ "  y="+e.getPoint().y);
    	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) &&  this.avionics.is_qpac() ) {
    		for(ClickRegion r : qpac_regions){
    			int w = r.check(true_click, scalex, scaley, border, border);
    			if(w > -1) {
    				udp_sender.sendDataPoint( XPlaneSimDataRepository.QPAC_KEY_PRESS, (float) w );
    			}
    		}
    	}
    }

  
    public void keyPressed(KeyEvent k) {
    	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) &&  this.avionics.is_qpac() ) {
    		char key = k.getKeyChar();
    		int w = -1;
    		// Test KeyChar
    		if (key >= 'a' && key <= 'z') {
    			w = QPAC_KEY_MDCU1_A + (key - 'a'); 
    		} else if (key >= 'A' && key <= 'Z') {
    			w = QPAC_KEY_MDCU1_A + (key - 'A');
    		} else if (key >= '0' && key <= '9') { 
    			w = QPAC_KEY_MDCU1_0+ (key - '0'); 
    		} else 
    			switch (key) {
    			case '.' : w = QPAC_KEY_MDCU1_DOT; break;
    			case '/' : w = QPAC_KEY_MDCU1_SLASH; break;    		
    			case '+' : w = QPAC_KEY_MDCU1_PLUS_M; break;
    			case '*' : w = QPAC_KEY_MDCU1_OVERFL; break;
    			case 127 : w = QPAC_KEY_MDCU1_DEL; break; // DEL -> CLEAR
    			case 8   : w = QPAC_KEY_MDCU1_DEL; break; // BackSpace
    			case ' ' : w = QPAC_KEY_MDCU1_SPACE; break; 
    			case 27  : w = QPAC_KEY_MDCU1_MENU; break; // ESCAPE -> MENU MENU
    		}
    		// Test KeyCodes
    		if (w == -1.0f) 
    			switch (k.getKeyCode()) {
    			case KeyEvent.VK_F1 : w = QPAC_KEY_MDCU1_LSK1L; break;
    			case KeyEvent.VK_F2 : w = QPAC_KEY_MDCU1_LSK2L; break;
    			case KeyEvent.VK_F3 : w = QPAC_KEY_MDCU1_LSK3L; break;
    			case KeyEvent.VK_F4 : w = QPAC_KEY_MDCU1_LSK4L; break;
    			case KeyEvent.VK_F5 : w = QPAC_KEY_MDCU1_LSK5L; break;
    			case KeyEvent.VK_F6 : w = QPAC_KEY_MDCU1_LSK6L; break;
    			case KeyEvent.VK_F7 : w = QPAC_KEY_MDCU1_LSK1R; break;
    			case KeyEvent.VK_F8 : w = QPAC_KEY_MDCU1_LSK2R; break;
    			case KeyEvent.VK_F9 : w = QPAC_KEY_MDCU1_LSK3R; break;
    			case KeyEvent.VK_F10 : w = QPAC_KEY_MDCU1_LSK4R; break;
    			case KeyEvent.VK_F11 : w = QPAC_KEY_MDCU1_LSK5R; break;
    			case KeyEvent.VK_F12 : w = QPAC_KEY_MDCU1_LSK6R; break;
    			case KeyEvent.VK_UP : w = QPAC_KEY_MDCU1_SLEW_UP; break;
    			case KeyEvent.VK_DOWN : w = QPAC_KEY_MDCU1_SLEW_DOWN; break;
    			case KeyEvent.VK_LEFT : w = QPAC_KEY_MDCU1_SLEW_LEFT; break;
    			case KeyEvent.VK_RIGHT : w = QPAC_KEY_MDCU1_SLEW_RIGHT; break; 

    			case KeyEvent.VK_PAGE_UP : w = QPAC_KEY_MDCU1_PERF; break;
    			case KeyEvent.VK_PAGE_DOWN : w = QPAC_KEY_MDCU1_PROG; break;
    			case KeyEvent.VK_HOME : w = QPAC_KEY_MDCU1_INIT; break;
    			case KeyEvent.VK_END : w = QPAC_KEY_MDCU1_FPLN; break; 
    			case KeyEvent.VK_INSERT : w = QPAC_KEY_MDCU1_DIR_TO; break;
    			case KeyEvent.VK_SCROLL_LOCK : w = QPAC_KEY_MDCU1_DATA; break;
    			case KeyEvent.VK_PAUSE : w = QPAC_KEY_MDCU1_RAD_NAV; break;
    		}

    		if (w > -0.5f) udp_sender.sendDataPoint( XPlaneSimDataRepository.QPAC_KEY_PRESS, (float) w );
    	}    
       
    }    

}
