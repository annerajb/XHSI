/**
* PFDFramedElement.java
* 
* Manage framing and flashing elements for PFD 
* 
* Copyright (C) 2014  Nicolas Carel
* 
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class PFDFramedElement {	
    private PFDGraphicsConfig pfd_gc;
	private boolean framed ;  // True if framed
	private boolean framing;  // True if framing active
	private boolean cleared;  // True if there is nothing to be display
	private boolean flashing; // True text should flash when displayed
	private boolean flash;    // True text is flashing
	private boolean frame_flashing; // True if frame should flash when displayed
	private boolean frame_flash;    // True if frame is flashing
	private boolean big_font;
	
	public enum PFE_Align {LEFT, CENTER, RIGHT};
	public enum PFE_Style {ONE_LINE, ONE_LINE_LR, TWO_LINES, THREE_LINES, TWO_LINES_LR, TWO_COLUMNS};
	public final static int ALT_FLAG = 5;
	public final static int ATT_FLAG = 6;
	public final static int HDG_FLAG = 7;
	public final static int SPD_FLAG = 8;
	public final static int VS_FLAG  = 9;
	private int raw; // FMA Raw
	private int col; // FMA Column
	private String str_line1_left;
	private String str_line1_right;
	private String str_line2_left;
	private String str_line2_right;
	private String str_line3_left;
	private String str_line3_right;
	private long paint_start;	
	private long framed_milli;		
	private long flashed_milli;
	private long frame_flashed_milli;
	
	long reconfigured_timestamp=0;
	int frame_x;
	int frame_y;
	int frame_w;
	int frame_h;
	int text_x;
	int text_y[] = new int[4];
	int text_c;
	Font text_font;
	
    public enum PFE_Color { PFE_COLOR_MARK, PFE_COLOR_ACTIVE, PFE_COLOR_ARMED, PFE_COLOR_MANAGED, PFE_COLOR_CAUTION, PFE_COLOR_ALARM };
    PFE_Color text_color;
    PFE_Color value_color;
    PFE_Color frame_color;
    PFE_Style text_style;
    PFE_Align text_align;
    
	public PFDFramedElement(int col, int raw, PFDGraphicsConfig pfd_gc, PFE_Color default_pfe_color, PFE_Align default_text_align ) {
		framed = false;
		framing = true;
		cleared = true;
		flashing = false;
		flash = false;
		frame_flashing = false;
		frame_flash = false;
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";	
		this.col = col;
		this.raw = raw;		
		this.pfd_gc = pfd_gc;
		paint_start = 0;
		framed_milli = 10000;
		flashed_milli = 10000;
		frame_flashed_milli = 10000;
		text_color = default_pfe_color;
		value_color = PFE_Color.PFE_COLOR_ARMED;
		frame_color = PFE_Color.PFE_COLOR_MARK;
		text_style = PFE_Style.ONE_LINE;
		text_align = default_text_align;
		big_font = false;		
	}
    
	public PFDFramedElement(int col, int raw, PFDGraphicsConfig pfd_gc, PFE_Color default_pfe_color) {
		framed = false;
		framing = true;
		cleared = true;
		flashing = false;
		flash = false;
		frame_flashing = false;
		frame_flash = false;
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";	
		this.col = col;
		this.raw = raw;		
		this.pfd_gc = pfd_gc;
		paint_start = 0;
		framed_milli = 10000;
		flashed_milli = 10000;
		frame_flashed_milli = 10000;
		text_color = default_pfe_color;
		value_color = PFE_Color.PFE_COLOR_ARMED;
		frame_color = PFE_Color.PFE_COLOR_MARK;
		text_style = PFE_Style.ONE_LINE;
		text_align = PFE_Align.CENTER;
		big_font = false;
	}

	public void paint(Graphics2D g2) {    	 
		 // Check GraphicConfig reconfiguration timestamp
		 if (pfd_gc.reconfigured_timestamp > this.reconfigured_timestamp ) update_config();
    	 
    	 if (framed) {
    		 if (pfd_gc.current_time_millis > paint_start + framed_milli ) framed = false;
    	 }
    	 if (flash) {
    		 if (pfd_gc.current_time_millis > paint_start + flashed_milli ) flash = false;    		 
    	 }
    	 boolean display_text = (flash && flashing) ? ((pfd_gc.current_time_millis % 1000) < 500) : true;    		 

    	 if (!cleared) {
    		 if (display_text) {
    			 switch (text_style) {
    			 	case ONE_LINE 		: draw1Mode(g2, 0, str_line1_left); break;
    			 	case ONE_LINE_LR 	: draw2Mode(g2, 0, str_line1_left, str_line1_right); break;
    			 	case TWO_COLUMNS 	: drawFinalMode(g2, 0, str_line1_left); break;
    			 	case TWO_LINES 		: draw1Mode(g2, 0, str_line1_left); draw1Mode(g2, 1, str_line2_left); break;
    			 	case THREE_LINES 	: draw1Mode(g2, 0, str_line1_left); 
    			 						  draw1Mode(g2, 1, str_line2_left); 
    			 						  draw1Mode(g2, 2, str_line3_left);
    			 						  break;
    			 	case TWO_LINES_LR 	: draw1Mode(g2, 0, str_line1_left); draw2Mode(g2, 1, str_line2_left, str_line2_right); break;
    			 } 
    		 }
    		 if (framed && framing) drawFrame(g2); 
    	 }
    }
    
    public void setText ( String text, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text)) || (color != text_color) ) {
    		if (text.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = pfd_gc.current_time_millis;    		
    		str_line1_left = text;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.ONE_LINE;
    	}    	
    }
    
    public void setText ( String text1, String text2, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (! str_line2_left.equals(text2)) || (color != text_color) ) {
    		if (text1.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = pfd_gc.current_time_millis; 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.TWO_LINES;
    	}    	
    }
   
    public void setText ( String text1, String text2, String text3, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (! str_line2_left.equals(text2)) || (color != text_color) ) {
    		if (text1.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = pfd_gc.current_time_millis; 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		str_line3_left = text3;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.THREE_LINES;
    	}    	
    }
    
    public void setTextValue ( String text, String value, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text)) || (color != text_color) || (! str_line1_right.equals(value))) {
    		if (text.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = pfd_gc.current_time_millis; 
    		str_line1_left = text;
    		str_line1_right = value;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.ONE_LINE_LR;
    	}    	
    }
    
    public void setTextValue ( String text1, String text2, String value, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (color != text_color) || (! str_line2_right.equals(value)) || (! str_line2_left.equals(text2))) {
    		if (text1.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = pfd_gc.current_time_millis; 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		str_line2_right = value;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.TWO_LINES_LR;
    	}    	
    }
    
    public void setTwoColumns ( ) {
    	text_style = PFE_Style.TWO_COLUMNS;
    }
    
    public void setFrameColor(PFE_Color color) {
    	frame_color = color;
    }
    
    public void setFrame() {
    	framed = true;
		paint_start = pfd_gc.current_time_millis; 
    }
    
    public void setFrameFlash() {
    	frame_flash = true;
		paint_start = pfd_gc.current_time_millis; 
    }
    
    public void setFlash() {
    	flash = true;
		paint_start = pfd_gc.current_time_millis; 
    }
    
    public void clearFrame() {
    	framed = false;
    }  
    
    public void clearFrameFlash() {
    	frame_flash = false;
    }
    
    public void clearFlash() {
    	flash = false;
    } 
    
    public void enableFraming() {
    	framing = true;
    }
    
    public void disableFraming() {
    	framing = false;
    }
    
    public void enableFlashing() {
    	flashing = true;
    }

    public void disableFlashing() {
    	flashing = false;
    }

    public void enableFrameFlashing() {
    	frame_flashing = true;
    }

    public void disableFrameFlashing() {
    	frame_flashing = false;
    }
    
    public void setBigFont(boolean new_font_status) {
    	big_font = new_font_status;
    	this.update_config();
    }
    
    public void clearText ( ) {    	
    	framed = false;
    	cleared = true;
    	flash = false;
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";   	   	
    }
    
    private Color getColor(PFE_Color pfe_color) {
    	Color color = pfd_gc.pfd_alarm_color;
        switch (pfe_color) {
    		case PFE_COLOR_MARK: 	color = pfd_gc.pfd_markings_color; 	break;
    		case PFE_COLOR_ACTIVE: 	color = pfd_gc.pfd_active_color; 	break;
    		case PFE_COLOR_ARMED: 	color = pfd_gc.pfd_armed_color; 	break;
    		case PFE_COLOR_CAUTION: color = pfd_gc.pfd_caution_color; 	break;
    		case PFE_COLOR_MANAGED: color = pfd_gc.pfd_managed_color; 	break;
    		case PFE_COLOR_ALARM: 	color = pfd_gc.pfd_alarm_color; 	break;    		    		
        }	
        return color;
    }
    
    private void setTextColor(Graphics2D g2) {
   		g2.setColor(getColor(text_color));        	
    }

    private void setValueColor(Graphics2D g2) {
   		g2.setColor(getColor(value_color)); 
    }

    private void drawFrame(Graphics2D g2) {
    	g2.setColor(getColor(frame_color));        	        
        g2.drawRect(frame_x, frame_y, frame_w, frame_h);     	
    }
    
    /*
    private void drawFlag(Graphics2D g2,  String mode) {
        int mode_w = pfd_gc.get_text_width(g2, text_font, mode);
        setTextColor(g2);      
        g2.setFont(text_font);
        g2.drawString(mode, text_c  - mode_w/2, text_y1);
    }
    */
    
    private void draw1Mode(Graphics2D g2, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, text_font, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.digit_width_xl/2;;  
        
        if ( text_align == PFE_Align.CENTER ) {
        	mode_x = text_c  - mode_w/2;
        } else {
        	
            switch (col) {
        		case 2: mode_x += pfd_gc.fma_col_2;
        			mode_x += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2 - mode_w/2;
        			break;
        		default: 
        			mode_x = text_x;
        			break;
        }
        }
        	
        setTextColor(g2);      
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);
    }
    
    private void draw2Mode(Graphics2D g2, int raw, String mode, String value) {
        int mode_w1 = pfd_gc.get_text_width(g2, text_font, mode);
        int mode_w2 = pfd_gc.get_text_width(g2, text_font, value);
        int mode_w = mode_w1 + mode_w2;
        int mode_x = text_c - mode_w/2;
        int mode_x2 = mode_x + mode_w1;        
        setTextColor(g2);          
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);        
        setValueColor(g2);  
        g2.drawString(value, mode_x2, text_y[raw]);
    }
    
    private void drawFinalMode(Graphics2D g2, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, text_font, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_col_1 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_1)/2 - mode_w/2;
        // Erase middle line
        g2.setColor(pfd_gc.background_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top + pfd_gc.fma_height * 2/3);
        setTextColor(g2);  
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);
    }
    
    private void update_config () {
    	reconfigured_timestamp = pfd_gc.current_time_millis;
    	frame_x = pfd_gc.fma_left + pfd_gc.digit_width_xl / 4;
    	frame_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2 - pfd_gc.line_height_xl*15/16; 
    	frame_w = 0;
    	frame_h = pfd_gc.line_height_xl*18/16;
        text_c = pfd_gc.fma_left;
        text_x = pfd_gc.fma_left + pfd_gc.digit_width_xl/2;
        text_y[0] = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        text_y[1] = pfd_gc.fma_top + pfd_gc.fma_height*(raw+1)/3 + pfd_gc.line_height_xl - 2;
        text_y[2] = pfd_gc.fma_top + pfd_gc.fma_height*(raw+2)/3 + pfd_gc.line_height_xl - 2;
        
        switch (col) {
    		case 1:  
    			frame_x += pfd_gc.fma_col_1;
    			frame_w = (pfd_gc.fma_col_2 - pfd_gc.fma_col_1) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_1 + (pfd_gc.fma_col_2 - pfd_gc.fma_col_1)/2; 
    			text_x += pfd_gc.fma_col_1;
    			break;
    		case 2: 
    			frame_x += pfd_gc.fma_col_2; 
    			frame_w = (pfd_gc.fma_col_3 - pfd_gc.fma_col_2) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2;
    			text_x += pfd_gc.fma_col_2;    			
    			break;
    		case 3: 
    			frame_x += pfd_gc.fma_col_3;
    			frame_w = (pfd_gc.fma_col_4 - pfd_gc.fma_col_3) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_3 + (pfd_gc.fma_col_4 - pfd_gc.fma_col_3)/2;
    			text_x += pfd_gc.fma_col_3;
    			break;
    		case 4: 
    			frame_x += pfd_gc.fma_col_4;
    			frame_w = (pfd_gc.fma_width  - pfd_gc.fma_col_4) - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_4 + (pfd_gc.fma_width - pfd_gc.fma_col_4)/2;
    			text_x += pfd_gc.fma_col_4; 
    			break;
    		case ALT_FLAG:
    			frame_x = pfd_gc.altitape_left;
    			frame_w = pfd_gc.tape_width*60/100;
    			text_c = frame_x + frame_w/2;
    			text_x = pfd_gc.altitape_left; 
    			text_y[0] = pfd_gc.adi_cy + pfd_gc.line_height_l/2;			
    			break;
    		case HDG_FLAG:
    			frame_x = pfd_gc.hdg_left;
    			frame_w = pfd_gc.hdg_width;
    			text_c = pfd_gc.adi_cx;
    			text_x = frame_x; 
    			text_y[0] = pfd_gc.hdg_top + pfd_gc.line_height_xxl*5/4;			
    			break; 		
    		case ATT_FLAG:
    			frame_x = pfd_gc.hdg_left;  // stub
    			frame_w = pfd_gc.hdg_width; // stub
    			text_c = pfd_gc.adi_cx;
    			text_x = frame_x;  // stub
    			text_y[0] = pfd_gc.adi_cy;			
    			break; 	    						
    		case SPD_FLAG:
    			frame_x = pfd_gc.speedtape_left;  
    			frame_w = pfd_gc.tape_width*6/8;
    			text_c = frame_x + frame_w/2;
    			text_x = frame_x;  
    			text_y[0] = pfd_gc.adi_cy + pfd_gc.line_height_l/2;			
    			break; 	     			    			
    		case VS_FLAG:
    			frame_x = pfd_gc.vsi_left+pfd_gc.vsi_width/5;  
    			frame_w = pfd_gc.vsi_width;
    			text_c = pfd_gc.vsi_left+pfd_gc.vsi_width/5;
    			text_x = frame_x;  
    			text_y[0] = pfd_gc.adi_cy - pfd_gc.line_height_xxl/2 - 4;
    			text_y[1] = pfd_gc.adi_cy + pfd_gc.line_height_xxl/2 - 4;
    			text_y[2] = pfd_gc.adi_cy + pfd_gc.line_height_xxl*3/2 - 4;
    			break; 	     			    			
    		default: 
    			frame_w = pfd_gc.fma_col_1 - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_1 / 2 ;
    			break;
        }    


        // TODO : WARNING : text_style is not part of Graphic Context
        if (text_style == PFE_Style.TWO_COLUMNS) { frame_w += (pfd_gc.fma_col_3 - pfd_gc.fma_col_2); }
        if (text_style == PFE_Style.TWO_LINES || text_style == PFE_Style.TWO_LINES_LR ) frame_h += pfd_gc.line_height_xl*18/16; 
        
        text_font=big_font ? pfd_gc.font_xxl : pfd_gc.font_xl;
    }
}
