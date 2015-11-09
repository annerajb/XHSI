/**
* CDUJarDesign.java
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


public class CDUJarDesign extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private BufferedImage image = null;

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;
    
    List<ClickRegion> jar_a320_regions;

    boolean drawregions = false;
    XPlaneUDPSender udp_sender = null; 
    
    int displayunit_topleft_x = 81;
    int displayunit_topleft_y = 56;
    int displayunit_width = 338;
    int displayunit_heigth = 400;

    double row_coef = 19.8;
    int upper_y = 2;
    double scratch_y_coef = 13.0;
    double char_width_coef = 1.5; 
     
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
    public static final int JAR_A320_MCDU_CLICK_FUEL       = 5;   
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

    
    
    public CDUJarDesign(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
        
        try {
        	image = ImageIO.read(this.getClass().getResourceAsStream("img/mcdu_a320_800x480.png"));      	
        } catch (IOException ioe){}
        
        udp_sender = XPlaneUDPSender.get_instance();
        jar_a320_regions = new ArrayList<ClickRegion>();

        // JAR Design A320neo MCDU Keyboard mapping
        // LSK
        jar_a320_regions.add(new ClickRegion(new Point(6, 95), new Point(48+26, 365), 1, 6, 
                        new int[][] {
        	{JAR_A320_MCDU_CLICK_LSK1L}, 
        	{JAR_A320_MCDU_CLICK_LSK2L},
        	{JAR_A320_MCDU_CLICK_LSK3L},
        	{JAR_A320_MCDU_CLICK_LSK4L},
        	{JAR_A320_MCDU_CLICK_LSK5L},
        	{JAR_A320_MCDU_CLICK_LSK6L}} ));

        // RSK
        jar_a320_regions.add(new ClickRegion(new Point(432-26, 95), new Point(474, 365), 1, 6, 
                        new int[][] {
        	{JAR_A320_MCDU_CLICK_LSK1R},
        	{JAR_A320_MCDU_CLICK_LSK2R},
        	{JAR_A320_MCDU_CLICK_LSK3R},
        	{JAR_A320_MCDU_CLICK_LSK4R},
        	{JAR_A320_MCDU_CLICK_LSK5R},
        	{JAR_A320_MCDU_CLICK_LSK6R}} ));

        // A..Z, SP, DEL, /, CLR
        jar_a320_regions.add(new ClickRegion(new Point(192, 490), new Point(440, 785), 5, 6,
                        new int[][] {
            {43, 44, 45, 46, 47},
            {48, 49, 50, 51, 52},
            {53, 54, 55, 56, 57},
            {58, 59, 60, 61, 62},
            {63, 64, 65, 66, 67},
            {68, JAR_A320_MCDU_CLICK_SLASH, JAR_A320_MCDU_CLICK_SPACE, JAR_A320_MCDU_CLICK_OVERFL, JAR_A320_MCDU_CLICK_DEL}} ));

        // 1..9, ., 0, +/-
        jar_a320_regions.add(new ClickRegion(new Point(43, 608), new Point(186, 785), 3, 4, 
                        new int[][] {
        	{34, 35, 36},
        	{37, 38, 39},
        	{40, 41, 42}, 
        	{JAR_A320_MCDU_CLICK_DOT, JAR_A320_MCDU_CLICK_0, JAR_A320_MCDU_CLICK_PLUS_M}} ));

        
        // DIR, PROG, PERF, INIT, DATA, blank
        // F-PLN, RAD-NAV, FUEL-PRED, SEC-FPLN, ATC-COMM, MCDU MENU
        jar_a320_regions.add(new ClickRegion(new Point(46, 400), new Point(402, 485), 6, 2, 
                        new int[][] {
        	{JAR_A320_MCDU_CLICK_DIR_TO, JAR_A320_MCDU_CLICK_PROG, JAR_A320_MCDU_CLICK_PERF, JAR_A320_MCDU_CLICK_INIT, JAR_A320_MCDU_CLICK_DATA, -1},
        	{JAR_A320_MCDU_CLICK_FPLN, JAR_A320_MCDU_CLICK_RAD_NAV, JAR_A320_MCDU_CLICK_FUEL, -1, -1, JAR_A320_MCDU_CLICK_MENU}} ));

        // AIRPORT, blank
        // LEFT, UP
        // RIGHT, DOWN
        jar_a320_regions.add(new ClickRegion(new Point(46, 486), new Point(170, 607), 2, 3, 
                        new int[][] {
        		{JAR_A320_MCDU_CLICK_AIRPORT, -1}, 
        		{JAR_A320_MCDU_CLICK_SLEW_LEFT, JAR_A320_MCDU_CLICK_SLEW_UP},
        		{JAR_A320_MCDU_CLICK_SLEW_RIGHT, JAR_A320_MCDU_CLICK_SLEW_DOWN}} ));

    }

    public void paint(Graphics2D g2) {
    	if ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && this.avionics.is_jar_a320neo() ) {
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
        		str_title = "JarDesign A320";
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
            for(ClickRegion r2 : jar_a320_regions){
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
    		case '*' : c = '⎕'; break;
    		case '0' : c = 'O'; break;
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
         
            List<CduLine> l = QpacMcduData.decodeLine(QpacMcduData.getLine(i));
            for(CduLine o : l){                    
                    x = (int) Math.round( cdu_gc.cdu_screen_topleft_x + o.pos * cdu_gc.cdu_digit_width);
                    decodeColor(g2, o.color );
                    decodeFont(g2, o.font );
                    g2.drawString(translateCduLine(o.text), x, yy);
            }    
        }
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

    	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) &&  this.avionics.is_jar_a320neo() ) {
    		for(ClickRegion r : jar_a320_regions){
    			int w = r.check(true_click, scalex, scaley, border, border);
    			if(w > -1) {
    				udp_sender.sendDataPoint( XPlaneSimDataRepository.JAR_A320NEO_MCDU_CLICK, (float) w );
    			}
    		}
    	}
    }

  
    public void keyPressed(KeyEvent k) {  
	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) &&  this.avionics.is_jar_a320neo() ) {
		char key = k.getKeyChar();
		int w = -1;
		// Test KeyChar
		if (key >= 'a' && key <= 'z') {
			w = JAR_A320_MCDU_CLICK_A + (key - 'a'); 
		} else if (key >= 'A' && key <= 'Z') {
			w = JAR_A320_MCDU_CLICK_A + (key - 'A');
		} else if (key >= '0' && key <= '9') { 
			w = JAR_A320_MCDU_CLICK_0+ (key - '0'); 
		} else 
			switch (key) {
			case '.' : w = JAR_A320_MCDU_CLICK_DOT; break;
			case '/' : w = JAR_A320_MCDU_CLICK_SLASH; break;    		
			case '+' : w = JAR_A320_MCDU_CLICK_PLUS_M; break;
			case '*' : w = JAR_A320_MCDU_CLICK_OVERFL; break;
			case 127 : w = JAR_A320_MCDU_CLICK_DEL; break; // DEL -> CLEAR
			case 8   : w = JAR_A320_MCDU_CLICK_DEL; break; // BackSpace
			case ' ' : w = JAR_A320_MCDU_CLICK_SPACE; break; 
			case 27  : w = JAR_A320_MCDU_CLICK_MENU; break; // ESCAPE -> MCDU_MENU
		}
		// Test KeyCodes
		if (w == -1.0f) 
			switch (k.getKeyCode()) {
			case KeyEvent.VK_F1 : w = JAR_A320_MCDU_CLICK_LSK1L; break;
			case KeyEvent.VK_F2 : w = JAR_A320_MCDU_CLICK_LSK2L; break;
			case KeyEvent.VK_F3 : w = JAR_A320_MCDU_CLICK_LSK3L; break;
			case KeyEvent.VK_F4 : w = JAR_A320_MCDU_CLICK_LSK4L; break;
			case KeyEvent.VK_F5 : w = JAR_A320_MCDU_CLICK_LSK5L; break;
			case KeyEvent.VK_F6 : w = JAR_A320_MCDU_CLICK_LSK6L; break;
			case KeyEvent.VK_F7 : w = JAR_A320_MCDU_CLICK_LSK1R; break;
			case KeyEvent.VK_F8 : w = JAR_A320_MCDU_CLICK_LSK2R; break;
			case KeyEvent.VK_F9 : w = JAR_A320_MCDU_CLICK_LSK3R; break;
			case KeyEvent.VK_F10 : w = JAR_A320_MCDU_CLICK_LSK4R; break;
			case KeyEvent.VK_F11 : w = JAR_A320_MCDU_CLICK_LSK5R; break;
			case KeyEvent.VK_F12 : w = JAR_A320_MCDU_CLICK_LSK6R; break;
			case KeyEvent.VK_UP : w = JAR_A320_MCDU_CLICK_SLEW_UP; break;
			case KeyEvent.VK_DOWN : w = JAR_A320_MCDU_CLICK_SLEW_DOWN; break;
			case KeyEvent.VK_LEFT : w = JAR_A320_MCDU_CLICK_SLEW_LEFT; break;
			case KeyEvent.VK_RIGHT : w = JAR_A320_MCDU_CLICK_SLEW_RIGHT; break; 

			case KeyEvent.VK_PAGE_UP : w = JAR_A320_MCDU_CLICK_PERF; break;
			case KeyEvent.VK_PAGE_DOWN : w = JAR_A320_MCDU_CLICK_PROG; break;
			case KeyEvent.VK_HOME : w = JAR_A320_MCDU_CLICK_INIT; break;
			case KeyEvent.VK_END : w = JAR_A320_MCDU_CLICK_FPLN; break; 
			case KeyEvent.VK_INSERT : w = JAR_A320_MCDU_CLICK_DIR_TO; break;
			case KeyEvent.VK_SCROLL_LOCK : w = JAR_A320_MCDU_CLICK_DATA; break;
			case KeyEvent.VK_PAUSE : w = JAR_A320_MCDU_CLICK_RAD_NAV; break;
		}

		if (w > -0.5f) udp_sender.sendDataPoint( XPlaneSimDataRepository.JAR_A320NEO_MCDU_CLICK, (float) w );
	}    
}
    
	

}
