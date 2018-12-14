/**
* PFDFramedElement.java
* 
* Manage framing and flashing elements for PFD 
* 
* Copyright (C) 2018  Nicolas Carel
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

package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


public class NDFramedElement {

	private NDGraphicsConfig nd_gc;
	private boolean framed ;  // True if framed
	private boolean framing;  // True if framing active
	private boolean cleared;  // True if there is nothing to be display
	private boolean flashing; // True text should flash when displayed
	private boolean flash;    // True text is flashing
	private boolean frame_flashing; // True if frame should flash when displayed
	private boolean frame_flash;    // True if frame is flashing
	private boolean big_font;
	private boolean config_changed;
	
	public enum FE_Align {LEFT, CENTER, RIGHT};
	public enum FE_Style {ONE_LINE, ONE_LINE_LR, TWO_LINES, THREE_LINES, TWO_LINES_LR };
	public final static int MAP_FLAG = 10;
	public final static int LOC_FLAG = 11;
	public final static int HDG_FLAG = 12;
	public final static int DME1_FLAG = 13;
	public final static int DME2_FLAG = 14;
	public final static int GS_FLAG  = 15;
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
	
    public enum FE_Color { FE_COLOR_MARK, FE_COLOR_ACTIVE, FE_COLOR_ARMED, FE_COLOR_MANAGED, FE_COLOR_CAUTION, FE_COLOR_ALARM };
    FE_Color text_color;
    FE_Color value_color;
    FE_Color frame_color;
    FE_Style text_style;
    FE_Align text_align;

	public NDFramedElement(int col, int raw, NDGraphicsConfig nd_gc, FE_Color default_fe_color, FE_Align default_text_align ) {
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
		this.nd_gc = nd_gc;
		paint_start = 0;
		framed_milli = 10000;
		flashed_milli = 10000;
		frame_flashed_milli = 10000;
		text_color = default_fe_color;
		value_color = FE_Color.FE_COLOR_ARMED;
		frame_color = FE_Color.FE_COLOR_MARK;
		text_style = FE_Style.ONE_LINE;
		text_align = default_text_align;
		big_font = false;		
		config_changed = true; // force reconfig at first paint
	}
    
	public NDFramedElement(int col, int raw, NDGraphicsConfig nd_gc, FE_Color default_fe_color) {
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
		this.nd_gc = nd_gc;
		paint_start = 0;
		framed_milli = 10000;
		flashed_milli = 10000;
		frame_flashed_milli = 10000;
		text_color = default_fe_color;
		value_color = FE_Color.FE_COLOR_ARMED;
		frame_color = FE_Color.FE_COLOR_MARK;
		text_style = FE_Style.ONE_LINE;
		text_align = FE_Align.CENTER;
		big_font = false;
		config_changed = true; // force reconfig at first paint
	}

	public void paint(Graphics2D g2) {    	 
		 // Check GraphicConfig reconfiguration timestamp
		 if (nd_gc.reconfigured_timestamp > this.reconfigured_timestamp ) config_changed = true;
	     if (config_changed) update_config(g2);
    	 
    	 if (framed) {
    		 if (nd_gc.current_time_millis > paint_start + framed_milli ) framed = false;
    	 }
    	 if (flash) {
    		 if (nd_gc.current_time_millis > paint_start + flashed_milli ) flash = false;    		 
    	 }
    	 boolean display_text = (flash && flashing) ? ((nd_gc.current_time_millis % 1000) < 500) : true;    		 

    	 if (!cleared) {
    		 if (display_text) {
    			 switch (text_style) {
    			 	case ONE_LINE 		: draw1Mode(g2, 0, str_line1_left); break;
    			 	case ONE_LINE_LR 	: draw2Mode(g2, 0, str_line1_left, str_line1_right); break;
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
    
    public void setText ( String text, FE_Color color ) {    	
    	if ((! str_line1_left.equals(text)) || (color != text_color) ) {
    		if (text.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = nd_gc.current_time_millis;    		
    		str_line1_left = text;
    		text_color = color;
    		frame_color = FE_Color.FE_COLOR_MARK;
    		cleared = false;
    		text_style = FE_Style.ONE_LINE;
    		config_changed=true;
    	}    	
    }
    
    public void setText ( String text1, String text2, FE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (! str_line2_left.equals(text2)) || (color != text_color) ) {
    		if (text1.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = nd_gc.current_time_millis; 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		text_color = color;
    		frame_color = FE_Color.FE_COLOR_MARK;
    		cleared = false;
    		text_style = FE_Style.TWO_LINES;
    		config_changed=true;
    	}    	
    }
   
    public void setText ( String text1, String text2, String text3, FE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (! str_line2_left.equals(text2)) || (color != text_color) ) {
    		if (text1.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = nd_gc.current_time_millis; 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		str_line3_left = text3;
    		text_color = color;
    		frame_color = FE_Color.FE_COLOR_MARK;
    		cleared = false;
    		text_style = FE_Style.THREE_LINES;
    		config_changed=true;
    	}    	
    }
    
    public void setTextValue ( String text, String value, FE_Color color ) {    	
    	if ((! str_line1_left.equals(text)) || (color != text_color) || (! str_line1_right.equals(value))) {
    		if (text.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = nd_gc.current_time_millis; 
    		str_line1_left = text;
    		str_line1_right = value;
    		text_color = color;
    		frame_color = FE_Color.FE_COLOR_MARK;
    		cleared = false;
    		text_style = FE_Style.ONE_LINE_LR;
    		config_changed=true;
    	}    	
    }
    
    public void setTextValue ( String text1, String text2, String value, FE_Color color ) {    	
    	if ((! str_line1_left.equals(text1)) || (color != text_color) || (! str_line2_right.equals(value)) || (! str_line2_left.equals(text2))) {
    		if (text1.equals("")) { framed=false; flash=false; } else { framed=true; flash=true;}
    		paint_start = nd_gc.current_time_millis; 
    		str_line1_left = text1;
    		str_line2_left = text2;
    		str_line2_right = value;
    		text_color = color;
    		frame_color = FE_Color.FE_COLOR_MARK;
    		cleared = false;
    		text_style = FE_Style.TWO_LINES_LR;
    		config_changed=true;
    	}    	
    }
    
   
    public void setFrameColor(FE_Color color) {
    	frame_color = color;
    }
    
    public void setFrame() {
    	framed = true;
		paint_start = nd_gc.current_time_millis; 
    }
    
    public void setFrameFlash() {
    	frame_flash = true;
		paint_start = nd_gc.current_time_millis; 
    }
    
    public void setFlash() {
    	flash = true;
		paint_start = nd_gc.current_time_millis; 
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
    	config_changed=true;
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
    
    private Color getColor(FE_Color pfe_color) {
    	Color color = nd_gc.pfd_alarm_color;
        switch (pfe_color) {
    		case FE_COLOR_MARK: 	color = nd_gc.pfd_markings_color; 	break;
    		case FE_COLOR_ACTIVE: 	color = nd_gc.pfd_active_color; 	break;
    		case FE_COLOR_ARMED: 	color = nd_gc.pfd_armed_color; 	break;
    		case FE_COLOR_CAUTION:  color = nd_gc.pfd_caution_color; 	break;
    		case FE_COLOR_MANAGED:  color = nd_gc.pfd_managed_color; 	break;
    		case FE_COLOR_ALARM: 	color = nd_gc.pfd_alarm_color; 	break;    		    		
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
        int mode_w = nd_gc.get_text_width(g2, text_font, mode);
        int mode_x = text_x;  
        
        if ( text_align == FE_Align.CENTER ) {
        	mode_x = text_c  - mode_w/2;
        }
        	
        setTextColor(g2);      
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);
    }
    
    private void draw2Mode(Graphics2D g2, int raw, String mode, String value) {
        int mode_w1 = nd_gc.get_text_width(g2, text_font, mode);
        int mode_w2 = nd_gc.get_text_width(g2, text_font, value);
        int mode_w = mode_w1 + mode_w2;
        int mode_x = text_c - mode_w/2;
        int mode_x2 = mode_x + mode_w1;        
        setTextColor(g2);          
        g2.setFont(text_font);
        g2.drawString(mode, mode_x, text_y[raw]);        
        setValueColor(g2);  
        g2.drawString(value, mode_x2, text_y[raw]);
    }
    
    
    private void update_config (Graphics2D g2) {
    	reconfigured_timestamp = nd_gc.current_time_millis;
    	
        text_font=big_font ? nd_gc.font_xxl : nd_gc.font_xl;
        
    	/*
    	 * Default Frame position
    	 */
    	int mode_w = nd_gc.get_text_width(g2, text_font, str_line1_left);
    	int hdg_message_y;
    	if (nd_gc.boeing_style)
    		hdg_message_y = (nd_gc.rose_y_offset + (nd_gc.frame_size.height  - nd_gc.border_bottom - NDGraphicsConfig.INITIAL_CENTER_BOTTOM)) / 2;
    	else
    		hdg_message_y = nd_gc.range_mode_message_y - nd_gc.line_height_xl * 4;
    	
    	frame_x = nd_gc.map_center_x - mode_w/2 - nd_gc.digit_width_xl / 4;
    	frame_y = hdg_message_y + nd_gc.line_height_xl/2 - 2 - nd_gc.line_height_xl*15/16; 
    	frame_w = 0;
    	frame_h = nd_gc.line_height_xl*18/16;
    	
    	/*
    	 * Default Text position
    	 */
        text_c = nd_gc.map_center_x;
        text_x = nd_gc.map_center_x - mode_w/2;
        text_y[0] = hdg_message_y + nd_gc.line_height_xl/2 + nd_gc.line_height_xl - 2;
        text_y[1] = hdg_message_y + nd_gc.line_height_xl/2 + nd_gc.line_height_xl*2 - 2;
        text_y[2] = hdg_message_y + nd_gc.line_height_xl/2 + nd_gc.line_height_xl*3 - 2;
        
        /*
        switch (col) {
    		case MAP_FLAG:
    			frame_x = pfd_gc.hdg_left;
    			frame_w = pfd_gc.hdg_width;
    			text_c = pfd_gc.adi_cx;
    			text_x = frame_x; 
    			text_y[0] = pfd_gc.hdg_top + pfd_gc.line_height_xxl*5/4;			
    			break; 		   			    			
    		default: 
    			frame_w = pfd_gc.fma_col_1 - pfd_gc.digit_width_xl / 2;
    			text_c += pfd_gc.fma_col_1 / 2 ;
    			break;
        } 
        */   

        // TODO : WARNING : text_style is not part of Graphic Context        
        if (text_style == FE_Style.TWO_LINES || text_style == FE_Style.TWO_LINES_LR ) frame_h += nd_gc.line_height_xl*18/16; 

        config_changed = false;
    }
}
