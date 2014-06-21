/**
* PFDFramedElement.java
* 
* Manage the element framing for FMA 
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

import java.awt.Color;
import java.awt.Graphics2D;

public class PFDFramedElement {	
    private PFDGraphicsConfig pfd_gc;
	private boolean framed ; // True if framed
	private boolean framing; // True if framing active
	private boolean cleared; // True if there is nothing to be display	
	public enum PFE_Align {LEFT, CENTER, RIGHT};
	public enum PFE_Style {ONE_LINE, ONE_LINE_LR, TWO_LINES, TWO_LINES_LR, TWO_COLUMNS};
	private int raw; // FMA Raw
	private int col; // FMA Column
	private String str_line1_left;
	private String str_line1_right;
	private String str_line2_left;
	private String str_line2_right;
	private long paint_start;	
	private long framed_milli;
	
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
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";	
		this.col = col;
		this.raw = raw;		
		this.pfd_gc = pfd_gc;
		paint_start = 0;
		framed_milli = 10000;
		text_color = default_pfe_color;
		value_color = PFE_Color.PFE_COLOR_ARMED;
		frame_color = PFE_Color.PFE_COLOR_MARK;
		text_style = PFE_Style.ONE_LINE;
		text_align = default_text_align;
	}
    
	public PFDFramedElement(int col, int raw, PFDGraphicsConfig pfd_gc, PFE_Color default_pfe_color) {
		framed = false;
		framing = true;
		cleared = true;
		str_line1_left = "";
		str_line1_right = "";
		str_line2_left = "";
		str_line2_right = "";	
		this.col = col;
		this.raw = raw;		
		this.pfd_gc = pfd_gc;
		paint_start = 0;
		framed_milli = 10000;
		text_color = default_pfe_color;
		value_color = PFE_Color.PFE_COLOR_ARMED;
		frame_color = PFE_Color.PFE_COLOR_MARK;
		text_style = PFE_Style.ONE_LINE;
		text_align = PFE_Align.CENTER;
	}

	public void paint(Graphics2D g2) {    	 
    	 
    	 if (framed) {
    		 if (System.currentTimeMillis() > paint_start + framed_milli ) framed = false;
    	 }
    	 if (!cleared) { 
    		 switch (text_style) {
    			 case ONE_LINE 		: draw1Mode(g2, raw, str_line1_left); break;
    			 case ONE_LINE_LR 	: draw2Mode(g2, raw, str_line1_left, str_line1_right); break;
    			 case TWO_COLUMNS 	: drawFinalMode(g2, raw, str_line1_left); break;
    			 case TWO_LINES 	: draw1Mode(g2, raw, str_line1_left); draw1Mode(g2, raw+1, str_line2_left); break;
    			 case TWO_LINES_LR 	: draw1Mode(g2, raw, str_line1_left); draw2Mode(g2, raw+1, str_line2_left, str_line2_right); break;
    		} 
    		if (framed && framing) drawFrame(g2); 
    	 }
    }
    
    public void setText ( String text, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text)) || (color != text_color) ) {
    		if (text.equals("")) framed=false; else framed=true;
    		paint_start = System.currentTimeMillis();    		
    		str_line1_left = text;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.ONE_LINE;
    	}    	
    }
    
    public void setText ( String text1, String text2, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (! str_line2_left.equals(text2)) || (color != text_color) ) {
    		if (text1.equals("")) framed=false; else framed=true;
    		paint_start = System.currentTimeMillis(); 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		text_color = color;
    		frame_color = PFE_Color.PFE_COLOR_MARK;
    		cleared = false;
    		text_style = PFE_Style.TWO_LINES;
    	}    	
    }
    
    public void setTextValue ( String text, String value, PFE_Color color ) {    	
    	if ((! str_line1_left.equals(text)) || (color != text_color) || (! str_line1_right.equals(value))) {
    		if (text.equals("")) framed=false; else framed=true;
    		paint_start = System.currentTimeMillis(); 
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
    		if (text1.equals("")) framed=false; else framed=true;
    		paint_start = System.currentTimeMillis(); 
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
		paint_start = System.currentTimeMillis(); 
    }
    
    public void clearFrame() {
    	framed = false;
    }   
    
    public void enableFraming() {
    	framing = true;
    }
    
    public void disableFraming() {
    	framing = false;
    }
    
    public void clearText ( ) {    	
    	framed = false;
    	cleared = true;
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
        int box_x = pfd_gc.fma_left + pfd_gc.digit_width_xl / 4;
        int box_w = 0; 
        int box_h = pfd_gc.line_height_xl*18/16;
        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        switch (col) {
    	case 1:  
    			box_x += pfd_gc.fma_col_1;
    			box_w = (pfd_gc.fma_col_2 - pfd_gc.fma_col_1) - pfd_gc.digit_width_xl / 2;
    			break;
    	case 2: 
    			box_x += pfd_gc.fma_col_2; 
    			box_w = (pfd_gc.fma_col_3 - pfd_gc.fma_col_2) - pfd_gc.digit_width_xl / 2;
    			break;
    	case 3: 
    			box_x += pfd_gc.fma_col_3;
    			box_w = (pfd_gc.fma_col_4 - pfd_gc.fma_col_3) - pfd_gc.digit_width_xl / 2;
    			break;
    	case 4: 
    			box_x += pfd_gc.fma_col_4;
    			box_w = (pfd_gc.fma_width  - pfd_gc.fma_col_4) - pfd_gc.digit_width_xl / 2;
    			break;
    	default: 
    			box_w = pfd_gc.fma_col_1 - pfd_gc.digit_width_xl / 2;
    			break;
        }
        // TODO : concerns only columns 2+3 but should work for any col position
        if (text_style == PFE_Style.TWO_COLUMNS) { box_w += (pfd_gc.fma_col_3 - pfd_gc.fma_col_2); }
        if (text_style == PFE_Style.TWO_LINES || text_style == PFE_Style.TWO_LINES_LR ) box_h += pfd_gc.line_height_xl*18/16;       
    	g2.setColor(getColor(frame_color));        	        
        g2.drawRect(box_x, mode_y - pfd_gc.line_height_xl*15/16, box_w, box_h);       
    }
    
    private void draw1Mode(Graphics2D g2, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_xl, mode);
        int mode_x = pfd_gc.fma_left;  // + pfd_gc.fma_width/10 + col*pfd_gc.fma_width/5 - mode_w/2;

        if ( text_align== PFE_Align.CENTER ) {
        switch (col) {
        	case 1: mode_x += pfd_gc.fma_col_1 + (pfd_gc.fma_col_2 - pfd_gc.fma_col_1)/2 - mode_w/2; 
        			break;
        	case 2: mode_x += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2 - mode_w/2;
        			break;
        	case 3: mode_x += pfd_gc.fma_col_3 + (pfd_gc.fma_col_4 - pfd_gc.fma_col_3)/2 - mode_w/2;
        			break;
        	case 4: mode_x += pfd_gc.fma_col_4 + (pfd_gc.fma_width - pfd_gc.fma_col_4)/2 - mode_w/2;
        			break;
        	default: mode_x += pfd_gc.fma_col_1 /2 - mode_w/2;
        			break;
        }
        } else {
        	mode_x += pfd_gc.digit_width_xl/2;
            switch (col) {
        	case 1: mode_x += pfd_gc.fma_col_1;
					break;
        	case 2: mode_x += pfd_gc.fma_col_2;
        			mode_x += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2 - mode_w/2;
        			break;
        	case 3: mode_x += pfd_gc.fma_col_3;
					break;
        	case 4: mode_x += pfd_gc.fma_col_4; 
         			break;       	
        	default: 
        			break;
        }
        }
        	
        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        setTextColor(g2);      
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(mode, mode_x, mode_y);
    }
    
    private void draw2Mode(Graphics2D g2, int raw, String mode, String value) {
        int mode_w1 = pfd_gc.get_text_width(g2, pfd_gc.font_xl, mode);
        int mode_w2 = pfd_gc.get_text_width(g2, pfd_gc.font_xl, value);
        int mode_w = mode_w1 + mode_w2;
        int mode_x = pfd_gc.fma_left; 
        switch (col) {
        	case 1: mode_x += pfd_gc.fma_col_1 + (pfd_gc.fma_col_2 - pfd_gc.fma_col_1)/2 - mode_w/2; break;
        	case 2: mode_x += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2 - mode_w/2; break;
        	case 3: mode_x += pfd_gc.fma_col_3 + (pfd_gc.fma_col_4 - pfd_gc.fma_col_3)/2 - mode_w/2; break;
        	case 4: mode_x += pfd_gc.fma_col_4 + (pfd_gc.fma_width - pfd_gc.fma_col_4)/2 - mode_w/2; break;
        	default: mode_x += pfd_gc.fma_col_1 /2 - mode_w/2; break;
        }        
        int mode_x2 = mode_x + mode_w1;        
        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        setTextColor(g2);          
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(mode, mode_x, mode_y);        
        setValueColor(g2);  
        g2.drawString(value, mode_x2, mode_y);
    }
    
    private void drawFinalMode(Graphics2D g2, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_xl, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_col_1 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_1)/2 - mode_w/2;
        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        // Erase middle line
        g2.setColor(pfd_gc.background_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top + pfd_gc.fma_height * 2/3);
        setTextColor(g2);  
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(mode, mode_x, mode_y);
    }
    
}
