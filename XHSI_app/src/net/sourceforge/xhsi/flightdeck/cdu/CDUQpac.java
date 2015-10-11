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
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CduLine;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.QpacMcduData;


public class CDUQpac extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;
   
    boolean drawregions = false;
    int displayunit_topleft_x = 76;
    int displayunit_topleft_y = 49;
    double row_coef = 19.8;
    int upper_y = 2;
    double scratch_y_coef = 13.0;
    double char_width_coef = 1.5; 
    
    public CDUQpac(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
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
}
