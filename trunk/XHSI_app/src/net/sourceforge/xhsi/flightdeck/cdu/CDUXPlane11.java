/**
* CDUXPlane11.java
* 
* Displays the Legacy X-Plane 11.35 CDU
* 
* CDUXPlane11 FMC uses QPAC MCDU message packet encoding
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


public class CDUXPlane11 extends CDUSubcomponent {

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

	// X-Plane Legacy FMS commands

	// FMC1 KEYS
	public static final int SIM_FMS1_LS_1L      = 0;
	public static final int SIM_FMS1_LS_2L      = 1;
	public static final int SIM_FMS1_LS_3L      = 2;
	public static final int SIM_FMS1_LS_4L      = 3;
	public static final int SIM_FMS1_LS_5L      = 4;
	public static final int SIM_FMS1_LS_6L      = 5;
	public static final int SIM_FMS1_LS_1R      = 6;
	public static final int SIM_FMS1_LS_2R      = 7;
	public static final int SIM_FMS1_LS_3R      = 8;
	public static final int SIM_FMS1_LS_4R      = 9;
	public static final int SIM_FMS1_LS_5R      = 10;
	public static final int SIM_FMS1_LS_6R      = 11;

	public static final int SIM_FMS1_INIT       = 12;
	public static final int SIM_FMS1_RTE        = 13;
	public static final int SIM_FMS1_DEP_ARR    = 14;
	public static final int SIM_FMS1_AP         = 15;
	public static final int SIM_FMS1_NAVRAD     = 16;

	public static final int SIM_FMS1_FIX        = 17;
	public static final int SIM_FMS1_LEGS       = 18;
	public static final int SIM_FMS1_HOLD       = 19;
	public static final int SIM_FMS1_PERF       = 20;
	public static final int SIM_FMS1_PROG       = 21;
	public static final int SIM_FMS1_EXEC       = 22;
	public static final int SIM_FMS1_DIR_INTC   = 23;

	public static final int SIM_FMS1_PREV_PAGE  = 25;
	public static final int SIM_FMS1_NEXT_PAGE  = 26;

	public static final int SIM_FMS1_A          = 27;
	public static final int SIM_FMS1_B          = 28;
	public static final int SIM_FMS1_C          = 29;
	public static final int SIM_FMS1_D          = 30;
	public static final int SIM_FMS1_E          = 31;
	public static final int SIM_FMS1_F          = 32;
	public static final int SIM_FMS1_G          = 33;
	public static final int SIM_FMS1_H          = 34;
	public static final int SIM_FMS1_I          = 35;
	public static final int SIM_FMS1_J          = 36;
	public static final int SIM_FMS1_K          = 37;
	public static final int SIM_FMS1_L          = 38;
	public static final int SIM_FMS1_M          = 39;
	public static final int SIM_FMS1_N          = 40;
	public static final int SIM_FMS1_O          = 41;
	public static final int SIM_FMS1_P          = 42;
	public static final int SIM_FMS1_Q          = 43;
	public static final int SIM_FMS1_R          = 44;
	public static final int SIM_FMS1_S          = 45;
	public static final int SIM_FMS1_T          = 46;
	public static final int SIM_FMS1_U          = 47;
	public static final int SIM_FMS1_V          = 48;
	public static final int SIM_FMS1_W          = 49;
	public static final int SIM_FMS1_X          = 50;
	public static final int SIM_FMS1_Y          = 51;
	public static final int SIM_FMS1_Z          = 52;

	public static final int SIM_FMS1_BACK       = 53;
	public static final int SIM_FMS1_DEL        = 54;
	public static final int SIM_FMS1_SLASH      = 55;

	public static final int SIM_FMS1_0          = 56;
	public static final int SIM_FMS1_1          = 57;
	public static final int SIM_FMS1_2          = 58;
	public static final int SIM_FMS1_3          = 59;
	public static final int SIM_FMS1_4          = 60;
	public static final int SIM_FMS1_5          = 61;
	public static final int SIM_FMS1_6          = 62;
	public static final int SIM_FMS1_7          = 63;
	public static final int SIM_FMS1_8          = 64;
	public static final int SIM_FMS1_9          = 65;
	public static final int SIM_FMS1_DOT        = 66;
	public static final int SIM_FMS1_KEY_CLR    = 67;
	public static final int SIM_FMS1_PLUS_M     = 68;
	public static final int SIM_FMS1_SPACE      = 69;

	public static final int SIM_FMS1_CLB        = 70;
	public static final int SIM_FMS1_CRZ        = 71;
	public static final int SIM_FMS1_DES        = 72;
	public static final int SIM_FMS1_FMC_COMM   = 73;
	public static final int SIM_FMS1_ATC        = 74;
	public static final int SIM_FMS1_BRT        = 75;

	public static final int SIM_FMS1_CDU_POPUP  = 76;
	public static final int SIM_FMS1_CDU_POPOUT = 77;

	// Exists only for FMS1
	public static final int SIM_FMS1_CLEAR      = 76;
	public static final int SIM_FMS1_DIRECT     = 77;
	public static final int SIM_FMS1_SIGN       = 78;
	public static final int SIM_FMS1_TYPE_APT   = 79;
	public static final int SIM_FMS1_TYPE_VOR   = 80;
	public static final int SIM_FMS1_TYPE_NDB   = 81;
	public static final int SIM_FMS1_TYPE_FIX   = 82;
	public static final int SIM_FMS1_TYPE_LATLON = 83;
	public static final int SIM_FMS1_FIX_NEXT   = 84;
	public static final int SIM_FMS1_FIX_PREV   = 85;
	public static final int SIM_FMS1_KEY_LOAD   = 86;
	public static final int SIM_FMS1_KEY_SAVE   = 87;

	// FMC2 KEYS
	public static final int SIM_FMS2_LS_1L      = 90;
	public static final int SIM_FMS2_LS_2L      = 91;
	public static final int SIM_FMS2_LS_3L      = 92;
	public static final int SIM_FMS2_LS_4L      = 93;
	public static final int SIM_FMS2_LS_5L      = 94;
	public static final int SIM_FMS2_LS_6L      = 95;
	public static final int SIM_FMS2_LS_1R      = 96;
	public static final int SIM_FMS2_LS_2R      = 97;
	public static final int SIM_FMS2_LS_3R      = 98;
	public static final int SIM_FMS2_LS_4R      = 99;
	public static final int SIM_FMS2_LS_5R      = 100;
	public static final int SIM_FMS2_LS_6R      = 101;

	public static final int SIM_FMS2_INIT       = 102;
	public static final int SIM_FMS2_RTE        = 103;
	public static final int SIM_FMS2_DEP_ARR    = 104;
	public static final int SIM_FMS2_AP         = 105;
	public static final int SIM_FMS2_NAVRAD     = 106;

	public static final int SIM_FMS2_FIX        = 107;
	public static final int SIM_FMS2_LEGS       = 108;
	public static final int SIM_FMS2_HOLD       = 109;
	public static final int SIM_FMS2_PERF       = 110;
	public static final int SIM_FMS2_PROG       = 111;
	public static final int SIM_FMS2_EXEC       = 112;
	public static final int SIM_FMS2_DIR_INTC   = 113;

	public static final int SIM_FMS2_PREV_PAGE  = 115;
	public static final int SIM_FMS2_NEXT_PAGE  = 116;

	public static final int SIM_FMS2_A          = 117;
	public static final int SIM_FMS2_B          = 118;
	public static final int SIM_FMS2_C          = 119;
	public static final int SIM_FMS2_D          = 120;
	public static final int SIM_FMS2_E          = 121;
	public static final int SIM_FMS2_F          = 122;
	public static final int SIM_FMS2_G          = 123;
	public static final int SIM_FMS2_H          = 124;
	public static final int SIM_FMS2_I          = 125;
	public static final int SIM_FMS2_J          = 126;
	public static final int SIM_FMS2_K          = 127;
	public static final int SIM_FMS2_L          = 128;
	public static final int SIM_FMS2_M          = 129;
	public static final int SIM_FMS2_N          = 130;
	public static final int SIM_FMS2_O          = 131;
	public static final int SIM_FMS2_P          = 132;
	public static final int SIM_FMS2_Q          = 133;
	public static final int SIM_FMS2_R          = 134;
	public static final int SIM_FMS2_S          = 135;
	public static final int SIM_FMS2_T          = 136;
	public static final int SIM_FMS2_U          = 137;
	public static final int SIM_FMS2_V          = 138;
	public static final int SIM_FMS2_W          = 139;
	public static final int SIM_FMS2_X          = 140;
	public static final int SIM_FMS2_Y          = 141;
	public static final int SIM_FMS2_Z          = 142;

	public static final int SIM_FMS2_BACK       = 143;
	public static final int SIM_FMS2_DEL        = 144;
	public static final int SIM_FMS2_SLASH      = 145;

	public static final int SIM_FMS2_0          = 146;
	public static final int SIM_FMS2_1          = 147;
	public static final int SIM_FMS2_2          = 148;
	public static final int SIM_FMS2_3          = 149;
	public static final int SIM_FMS2_4          = 150;
	public static final int SIM_FMS2_5          = 151;
	public static final int SIM_FMS2_6          = 152;
	public static final int SIM_FMS2_7          = 153;
	public static final int SIM_FMS2_8          = 154;
	public static final int SIM_FMS2_9          = 155;
	public static final int SIM_FMS2_DOT        = 156;
	public static final int SIM_FMS2_CLR        = 157;
	public static final int SIM_FMS2_PLUS_M     = 158;
	public static final int SIM_FMS2_SPACE      = 159;

	public static final int SIM_FMS2_CLB        = 160;
	public static final int SIM_FMS2_CRZ        = 161;
	public static final int SIM_FMS2_DES        = 162;
	public static final int SIM_FMS2_FMC_COMM   = 163;
	public static final int SIM_FMS2_ATC        = 164;
	public static final int SIM_FMS2_BRT        = 165;

	public static final int SIM_FMS2_CDU_POPUP  = 166;
	public static final int SIM_FMS2_CDU_POPOUT = 167;

	// END
	public static final int SIM_FMS_KEY_MAX     = 170;

	public CDUXPlane11(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
		super(model_factory, cdu_gc, parent_component);


		try {
			image = ImageIO.read(this.getClass().getResourceAsStream("img/b737_800_cdu_800x480.png"));
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

		// FIX, NAV RAD, PREV PAGE, NEXT PAGE
		regions.add(new ClickRegion(new Point(52, 454), new Point(180, 554), 2, 2, 
				new int[][] {{17, 16}, {25, 26}} ));

		udp_sender = XPlaneUDPSender.get_instance();

		logger.finest("CDU X-Plane 11");
	}

	public void paint(Graphics2D g2) {
		if ( ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && (!this.avionics.is_qpac() && !this.avionics.is_jar_a320neo() 
    			&& !this.avionics.is_zibo_mod_737() && (this.avionics.get_fms_type() > 0)) ) ) {
			QpacMcduData.updated = false;
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
				str_title = "Legacy X-Plane 11";
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
		case 'n' : g2.setColor(Color.black); reverse=false; break; 
		case 'R' : g2.setColor(cdu_gc.ecam_warning_color); reverse=true; break;
		case 'B' : g2.setColor(cdu_gc.ecam_action_color); reverse=true; break;
		case 'W' : g2.setColor(cdu_gc.ecam_markings_color); reverse=true; break;
		case 'Y' : g2.setColor(cdu_gc.ecam_reference_color); reverse=true; break;
		case 'M' : g2.setColor(cdu_gc.ecam_special_color); reverse=true; break;
		case 'A' : g2.setColor(cdu_gc.ecam_caution_color); reverse=true; break;
		case 'G' : g2.setColor(cdu_gc.ecam_normal_color); reverse=true; break;
		case 'N' : g2.setColor(Color.black); reverse=true; break; 
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
			case 0x1C : c = '←'; break;
			case 0x1D : c = '↑'; break;
			case 0x1E : c = '→'; break;
			case 0x1F : c = '↓'; break;
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
				if (reverse) {
					int text_width=cdu_gc.get_text_width(g2, cdu_gc.cdu_24_normal_font, o.text);
					g2.fillRect(x, cdu_gc.cdu_xfmc_line[i], text_width, cdu_gc.line_height_fixed_xl);
					g2.setColor(Color.black);
				}				
				decodeFont(g2, o.font);
				g2.drawString(translateCduLine(o.text), x, cdu_gc.cdu_xfmc_line[i]);
				
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

		if ( ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && (!this.avionics.is_qpac() && !this.avionics.is_jar_a320neo() 
    			&& !this.avionics.is_zibo_mod_737() && (this.avionics.get_fms_type() > 0)) ) )  {
			for(ClickRegion r : regions){
				int w = r.check(true_click, scalex, scaley, border, border);
				if(w > -1) {    				
					int mcdu_shift=avionics.get_cdu_side()*90;
					udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_FMS_KEY_PRESS, (float) (w+mcdu_shift) );
				}
			}
		}
	}

	public void keyPressed(KeyEvent k) {
		if ( ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && (!this.avionics.is_qpac() && !this.avionics.is_jar_a320neo() 
    			&& !this.avionics.is_zibo_mod_737() && (this.avionics.get_fms_type() > 0)) ) ) {
			char key = k.getKeyChar();
			int w = -1;
			// Test KeyChar
			if (key >= 'a' && key <= 'z') {
				w = SIM_FMS1_A + (key - 'a'); 
			} else if (key >= 'A' && key <= 'Z') {
				w = SIM_FMS1_A + (key - 'A');
			} else if (key >= '0' && key <= '9') { 
				w = SIM_FMS1_0+ (key - '0'); 
			} else 
				switch (key) {
				case '.' : w = SIM_FMS1_DOT; break;
				case '/' : w = SIM_FMS1_SLASH; break;    		
				case '+' : w = SIM_FMS1_PLUS_M; break;
				// case '*' : w = Z737_KEY_FMC1_OVERFL; break;
				case 127 : w = SIM_FMS1_DEL; break;
				case 8   : w = SIM_FMS1_KEY_CLR;; break;
				case ' ' : w = SIM_FMS1_SPACE; break; 
				case 27  : w = SIM_FMS1_DIR_INTC; break; // ESCAPE -> MCDU_MENU
				}
			// Test KeyCodes
			if (w == -1.0f) 
				switch (k.getKeyCode()) {
				case KeyEvent.VK_F1 : w = SIM_FMS1_LS_1L; break;
				case KeyEvent.VK_F2 : w = SIM_FMS1_LS_2L; break;
				case KeyEvent.VK_F3 : w = SIM_FMS1_LS_3L; break;
				case KeyEvent.VK_F4 : w = SIM_FMS1_LS_4L; break;
				case KeyEvent.VK_F5 : w = SIM_FMS1_LS_5L; break;
				case KeyEvent.VK_F6 : w = SIM_FMS1_LS_6L; break;
				case KeyEvent.VK_F7 : w = SIM_FMS1_LS_1R; break;
				case KeyEvent.VK_F8 : w = SIM_FMS1_LS_2R; break;
				case KeyEvent.VK_F9 : w = SIM_FMS1_LS_3R; break;
				case KeyEvent.VK_F10 : w = SIM_FMS1_LS_4R; break;
				case KeyEvent.VK_F11 : w = SIM_FMS1_LS_5R; break;
				case KeyEvent.VK_F12 : w = SIM_FMS1_LS_6R; break;
				case KeyEvent.VK_UP : w = SIM_FMS1_PREV_PAGE; break;
				case KeyEvent.VK_DOWN : w = SIM_FMS1_NEXT_PAGE; break;
				case KeyEvent.VK_LEFT : w = SIM_FMS1_PREV_PAGE; break;
				case KeyEvent.VK_RIGHT : w = SIM_FMS1_NEXT_PAGE; break; 

				case KeyEvent.VK_PAGE_UP : w = SIM_FMS1_NAVRAD; break;
				case KeyEvent.VK_PAGE_DOWN : w = SIM_FMS1_PROG; break;
				case KeyEvent.VK_HOME : w = SIM_FMS1_INIT; break;
				case KeyEvent.VK_END : w = SIM_FMS1_LEGS; break; 
				case KeyEvent.VK_INSERT : w = SIM_FMS1_RTE; break;
				case KeyEvent.VK_SCROLL_LOCK : w = SIM_FMS1_DEP_ARR; break;		
				case KeyEvent.VK_PAUSE : w = SIM_FMS1_HOLD; break;
				case KeyEvent.VK_ENTER : w = SIM_FMS1_EXEC; break;
				}

			if (w > -0.5f) {
				int mcdu_shift=avionics.get_cdu_side()*90;
				udp_sender.sendDataPoint( XPlaneSimDataRepository.SIM_FMS_KEY_PRESS, (float) (w+mcdu_shift) );
			}
		}  
	}
		

}
