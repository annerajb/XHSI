/**
* CDUZiboMod737.java
* 
* Displays the Boeing 737-800 CDU by Laminar (X-Plane 11) with Zibo Mod
* 
* ZiboMod FMC uses QPAC MCDU message packet encoding
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2019  Nicolas Carel
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
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedString;
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


public class CDUZiboMod737 extends CDUSubcomponent {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	private BufferedImage image = null;
	private BufferedImage msg_img = null;
	private BufferedImage exec_img = null;

	boolean drawregions = false;
	Font font;

	int displayunit_topleft_x = 76;
	int displayunit_topleft_y = 49;
	double row_coef = 19.8;
	int upper_y = 2;
	double scratch_y_coef = 13.0;
	double char_width_coef = 1.5; 

	XPlaneUDPSender udp_sender = null; 

	double scalex = 1;
	double scaley = 1;
	double border;
	double border_x;
	double border_y;
	
	boolean reverse; // Reverse video string

	List<ClickRegion> regions;

	/*
	 *  ZiboMod 737 CDU KEYS
	 */
	public static final int Z737_KEY_FMC1_LSK1L      = 0;
	public static final int Z737_KEY_FMC1_LSK2L      = 1;
	public static final int Z737_KEY_FMC1_LSK3L      = 2;
	public static final int Z737_KEY_FMC1_LSK4L      = 3;
	public static final int Z737_KEY_FMC1_LSK5L      = 4;
	public static final int Z737_KEY_FMC1_LSK6L      = 5;
	public static final int Z737_KEY_FMC1_LSK1R      = 6;
	public static final int Z737_KEY_FMC1_LSK2R      = 7;
	public static final int Z737_KEY_FMC1_LSK3R      = 8;
	public static final int Z737_KEY_FMC1_LSK4R      = 9;
	public static final int Z737_KEY_FMC1_LSK5R      = 10;
	public static final int Z737_KEY_FMC1_LSK6R      = 11;

	public static final int Z737_KEY_FMC1_INIT       = 12;
	public static final int Z737_KEY_FMC1_RTE        = 13;
	public static final int Z737_KEY_FMC1_DEP_ARR    = 14;
	public static final int Z737_KEY_FMC1_AP         = 15;
	public static final int Z737_KEY_FMC1_VNAV       = 16;

	public static final int Z737_KEY_FMC1_FIX        = 17;
	public static final int Z737_KEY_FMC1_LEGS       = 18;
	public static final int Z737_KEY_FMC1_HOLD       = 19;
	public static final int Z737_KEY_FMC1_PERF       = 20;
	public static final int Z737_KEY_FMC1_PROG       = 21;
	public static final int Z737_KEY_FMC1_EXEC       = 22;
	public static final int Z737_KEY_FMC1_MENU       = 23;
	public static final int Z737_KEY_FMC1_RAD_NAV    = 24;
	public static final int Z737_KEY_FMC1_SLEW_LEFT  = 25;
	public static final int Z737_KEY_FMC1_SLEW_RIGHT = 26;

	public static final int Z737_KEY_FMC1_A          = 27;
	public static final int Z737_KEY_FMC1_B          = 28;
	public static final int Z737_KEY_FMC1_C          = 29;
	public static final int Z737_KEY_FMC1_D          = 30;
	public static final int Z737_KEY_FMC1_E          = 31;
	public static final int Z737_KEY_FMC1_F          = 32;
	public static final int Z737_KEY_FMC1_G          = 33;
	public static final int Z737_KEY_FMC1_H          = 34;
	public static final int Z737_KEY_FMC1_I          = 35;
	public static final int Z737_KEY_FMC1_J          = 36;
	public static final int Z737_KEY_FMC1_K          = 37;
	public static final int Z737_KEY_FMC1_L          = 38;
	public static final int Z737_KEY_FMC1_M          = 39;
	public static final int Z737_KEY_FMC1_N          = 40;
	public static final int Z737_KEY_FMC1_O          = 41;
	public static final int Z737_KEY_FMC1_P          = 42;
	public static final int Z737_KEY_FMC1_Q          = 43;
	public static final int Z737_KEY_FMC1_R          = 44;
	public static final int Z737_KEY_FMC1_S          = 45;
	public static final int Z737_KEY_FMC1_T          = 46;
	public static final int Z737_KEY_FMC1_U          = 47;
	public static final int Z737_KEY_FMC1_V          = 48;
	public static final int Z737_KEY_FMC1_W          = 49;
	public static final int Z737_KEY_FMC1_X          = 50;
	public static final int Z737_KEY_FMC1_Y          = 51;
	public static final int Z737_KEY_FMC1_Z          = 52;

	public static final int Z737_KEY_FMC1_DEL        = 54;
	public static final int Z737_KEY_FMC1_SLASH      = 55;

	public static final int Z737_KEY_FMC1_0          = 56;
	public static final int Z737_KEY_FMC1_1          = 57;
	public static final int Z737_KEY_FMC1_2          = 58;
	public static final int Z737_KEY_FMC1_3          = 59;
	public static final int Z737_KEY_FMC1_4          = 60;
	public static final int Z737_KEY_FMC1_5          = 61;
	public static final int Z737_KEY_FMC1_6          = 62;
	public static final int Z737_KEY_FMC1_7          = 63;
	public static final int Z737_KEY_FMC1_8          = 64;
	public static final int Z737_KEY_FMC1_9          = 65;
	public static final int Z737_KEY_FMC1_DOT        = 66;
	public static final int Z737_KEY_FMC1_CLR        = 67;
	public static final int Z737_KEY_FMC1_PLUS_M     = 68;
	public static final int Z737_KEY_FMC1_SPACE      = 69;
	public static final int Z737_KEY_FMC1_CLB        = 70;

	public static final int Z737_KEY_FMC1_CRZ        = 71;
	public static final int Z737_KEY_FMC1_DES        = 72;
	public static final int Z737_KEY_FMC1_FMC_COMM   = 73;
	public static final int Z737_KEY_FMC1_ATC        = 74;
	public static final int Z737_KEY_FMC1_BRT        = 75;



	public CDUZiboMod737(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
		super(model_factory, cdu_gc, parent_component);


		try {
			image = ImageIO.read(this.getClass().getResourceAsStream("img/z737cdu_800x480.png"));
			msg_img = ImageIO.read(this.getClass().getResourceAsStream("img/z737cdu_msg.png"));
			exec_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_exec_litv_m.png"));
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
			{52, 69, 54, 55, 67}} ));

		// 1..9, ., 0, +/-
		regions.add(new ClickRegion(new Point(52, 562), new Point(186, 768), 3, 4, 
				new int[][] {{57, 58, 59}, {60, 61, 62}, {63, 64, 65}, {66, 56, 68}} ));

		// INIT REF, RTE, CLB, CRZ, DES, BRT, MENU, LEGS, DEP ARR, HOLD, PROG, EXEC
		regions.add(new ClickRegion(new Point(52, 348), new Point(436, 452), 6, 2, 
				new int[][] {{12, 13, 70, 71, 72, -1}, {23, 18, 14, 19, 21, 22}} ));

		// MENU, NAV RAD, PREV PAGE, NEXT PAGE
		regions.add(new ClickRegion(new Point(52, 454), new Point(180, 554), 2, 2, 
				new int[][] {{20, 17}, {25, 26}} ));

		udp_sender = XPlaneUDPSender.get_instance();

		logger.finest("CDU ZiboMod 737 instanciated");
	}

	public void paint(Graphics2D g2) {
		if ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && this.avionics.is_zibo_mod_737() ) {
			if ( this.preferences.cdu_display_only() ) {
				drawDisplayOnly(g2);
			} else {
				drawFullPanel(g2);
			}
		}
	}


	private void drawDisplayOnly(Graphics2D g2) {

		if ( cdu_gc.powered ) {
			String str_title = QpacMcduData.getLine(Avionics.CDU_LEFT,0);

			if (str_title.isEmpty()) {
				str_title = "B737-800X";
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
		
		int stat = QpacMcduData.getStatus(avionics.get_cdu_side());
		
        if((stat & 1) == 1) {
            g2.drawImage(exec_img,null, 383, 397);
        }
        if((stat & 2) == 2) {
            g2.drawImage(msg_img,null,435, 541);
        }
        
		g2.setTransform(orig);
		if ( cdu_gc.powered ) {
			drawDisplayLines(g2);
		}
		g2.setTransform(orig);
		// for debugging
		if ( drawregions ) {
			g2.setColor(cdu_gc.dim_markings_color);
			for(ClickRegion r2 : regions){
				r2.draw(g2, scalex, scaley, border, border);
			}
		}
	}

	private void decodeColor(Graphics2D g2, char color_code) {
		switch (color_code) {
		case 'r' : g2.setColor(cdu_gc.ecam_warning_color); reverse=false; break;
		case 'b' : g2.setColor(cdu_gc.ecam_action_color); reverse=false; break;
		case 'w' : g2.setColor(cdu_gc.ecam_markings_color); reverse=false; break;
		case 'y' : g2.setColor(cdu_gc.ecam_reference_color); reverse=false; break;
		case 'm' : g2.setColor(cdu_gc.ecam_special_color); reverse=false; break;
		case 'a' : g2.setColor(cdu_gc.ecam_caution_color); reverse=false; break;
		case 'g' : g2.setColor(cdu_gc.ecam_normal_color); reverse=false; break;
		case 'i' : g2.setColor(cdu_gc.ecam_reference_color); reverse=true; break; // Inverted (green)
		default : g2.setColor(Color.GRAY); reverse=false; break;
		}
	}

	private void decodeFont(Graphics2D g2, char font_code) {
		switch (font_code) {
		case 'l' : g2.setFont(cdu_gc.cdu_24_normal_font); break;
		case 's' : g2.setFont(cdu_gc.cdu_24_small_font); break;
		default : g2.setFont(cdu_gc.cdu_24_normal_font); break;
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
		int cdu_side = avionics.get_cdu_side();

		for(int i=0; i < 14; i++) {        
			int x = 0;
			List<CduLine> l = QpacMcduData.decodeLine(QpacMcduData.getLine(cdu_side,i));
			for(CduLine o : l){                    
				x = (int) Math.round( cdu_gc.cdu_screen_topleft_x + o.pos * cdu_gc.cdu_25_digit_width);
				decodeColor(g2, o.color);
				decodeFont(g2, o.font);
				/*
				if (reverse) {
					AttributedString asCduLine = new AttributedString(translateCduLine(o.text));
					asCduLine.addAttribute(TextAttribute.BACKGROUND, cdu_gc.ecam_markings_color, 2, 9);
					g2.drawString(asCduLine.getIterator(), x, cdu_gc.cdu_xfmc_line[i]);
				} else {
				*/
					g2.drawString(translateCduLine(o.text), x, cdu_gc.cdu_xfmc_line[i]);
				//}
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

		if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) &&  this.avionics.is_zibo_mod_737() ) {
			for(ClickRegion r : regions){
				int w = r.check(true_click, scalex, scaley, border, border);
				if(w > -1) {    				
					int mcdu_shift=avionics.get_cdu_side()*80;
					udp_sender.sendDataPoint( XPlaneSimDataRepository.Z737_KEY_PRESS, (float) (w+mcdu_shift) );
				}
			}
		}
	}

	public void keyPressed(KeyEvent k) {  
		if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) &&  this.avionics.is_zibo_mod_737() ) {
			char key = k.getKeyChar();
			int w = -1;
			// Test KeyChar
			if (key >= 'a' && key <= 'z') {
				w = Z737_KEY_FMC1_A + (key - 'a'); 
			} else if (key >= 'A' && key <= 'Z') {
				w = Z737_KEY_FMC1_A + (key - 'A');
			} else if (key >= '0' && key <= '9') { 
				w = Z737_KEY_FMC1_0+ (key - '0'); 
			} else 
				switch (key) {
				case '.' : w = Z737_KEY_FMC1_DOT; break;
				case '/' : w = Z737_KEY_FMC1_SLASH; break;    		
				case '+' : w = Z737_KEY_FMC1_PLUS_M; break;
				// case '*' : w = Z737_KEY_FMC1_OVERFL; break;
				case 127 : w = Z737_KEY_FMC1_CLR; break; // DEL -> CLEAR
				case 8   : w = Z737_KEY_FMC1_DEL; break; // BackSpace
				case ' ' : w = Z737_KEY_FMC1_SPACE; break; 
				case 27  : w = Z737_KEY_FMC1_MENU; break; // ESCAPE -> MCDU_MENU
				}
			// Test KeyCodes
			if (w == -1.0f) 
				switch (k.getKeyCode()) {
				case KeyEvent.VK_F1 : w = Z737_KEY_FMC1_LSK1L; break;
				case KeyEvent.VK_F2 : w = Z737_KEY_FMC1_LSK2L; break;
				case KeyEvent.VK_F3 : w = Z737_KEY_FMC1_LSK3L; break;
				case KeyEvent.VK_F4 : w = Z737_KEY_FMC1_LSK4L; break;
				case KeyEvent.VK_F5 : w = Z737_KEY_FMC1_LSK5L; break;
				case KeyEvent.VK_F6 : w = Z737_KEY_FMC1_LSK6L; break;
				case KeyEvent.VK_F7 : w = Z737_KEY_FMC1_LSK1R; break;
				case KeyEvent.VK_F8 : w = Z737_KEY_FMC1_LSK2R; break;
				case KeyEvent.VK_F9 : w = Z737_KEY_FMC1_LSK3R; break;
				case KeyEvent.VK_F10 : w = Z737_KEY_FMC1_LSK4R; break;
				case KeyEvent.VK_F11 : w = Z737_KEY_FMC1_LSK5R; break;
				case KeyEvent.VK_F12 : w = Z737_KEY_FMC1_LSK6R; break;
				case KeyEvent.VK_UP : w = Z737_KEY_FMC1_SLEW_LEFT; break;
				case KeyEvent.VK_DOWN : w = Z737_KEY_FMC1_SLEW_RIGHT; break;
				case KeyEvent.VK_LEFT : w = Z737_KEY_FMC1_SLEW_LEFT; break;
				case KeyEvent.VK_RIGHT : w = Z737_KEY_FMC1_SLEW_RIGHT; break; 

				case KeyEvent.VK_PAGE_UP : w = Z737_KEY_FMC1_PERF; break;
				case KeyEvent.VK_PAGE_DOWN : w = Z737_KEY_FMC1_PROG; break;
				case KeyEvent.VK_HOME : w = Z737_KEY_FMC1_INIT; break;
				case KeyEvent.VK_END : w = Z737_KEY_FMC1_LEGS; break; 
				case KeyEvent.VK_INSERT : w = Z737_KEY_FMC1_RTE; break;
				case KeyEvent.VK_SCROLL_LOCK : w = Z737_KEY_FMC1_DEP_ARR; break;		
				case KeyEvent.VK_PAUSE : w = Z737_KEY_FMC1_HOLD; break;
				case KeyEvent.VK_ENTER : w = Z737_KEY_FMC1_EXEC; break;
				}

			if (w > -0.5f) {
				int mcdu_shift=avionics.get_cdu_side()*80;
				udp_sender.sendDataPoint( XPlaneSimDataRepository.Z737_KEY_PRESS, (float) (w+mcdu_shift) );
			}
		}    
	}

}
