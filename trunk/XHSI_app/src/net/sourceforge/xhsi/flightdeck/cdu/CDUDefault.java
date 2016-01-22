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
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.sourceforge.xhsi.XHSI;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.CduLine;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.QpacMcduData;


public class CDUDefault extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private Aircraft aircraft;
    private Avionics avionics;
    
    private BufferedImage image = null;

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;

    int displayunit_topleft_x = 81;
    int displayunit_topleft_y = 56;
    int displayunit_width = 338;
    int displayunit_heigth = 400;

    double row_coef = 19.8;
    int upper_y = 2;
    double scratch_y_coef = 13.0;
    double char_width_coef = 1.5; 
    
    // 14 lines
    String CduLine[] = { 
    		"lg10XHSI",    		
    		"sw00Software version",	"lb00"+XHSI.RELEASE,
    		"", "",
    		"sw00Engines", "", 
    		"sw00A/C Registration", "",
    		"sw00Gears", "",
    		"sw00Fuel tanks", "",
    		"", "",
    		""};

    
    public CDUDefault(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
        
        try {
        	image = ImageIO.read(this.getClass().getResourceAsStream("img/mcdu_a320_800x480.png"));      	
        } catch (IOException ioe){}
        
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();
        logger.finest("CDUDefault instanciated");
    }

    public void paint(Graphics2D g2) {
    	if ( (cdu_gc.cdu_source == Avionics.CDU_SOURCE_AIRCRAFT_OR_DUMMY) && (!this.avionics.is_qpac() && (!this.avionics.is_jar_a320neo()) )
    			) {
    		CduLine[6] = "lb00" + this.aircraft.num_engines();
    		CduLine[8] = "lb00" + this.aircraft.aircraft_registration();
    		if (this.aircraft.has_retractable_gear()) { 
    			CduLine[10] = "lb00" + this.aircraft.num_gears() + " RETRACTABLE";	
    		} else {
    			CduLine[10] = "lb00" + this.aircraft.num_gears() + " FIXED";	    			
    		}
    		CduLine[12] = "lb00" + this.aircraft.num_tanks();
    		
    		//  Compare plugin to application version. Display in amber if discrepancy.
    		CduLine[13] = "";
    		if ( XHSI.EXPECTED_PLUGIN != this.aircraft.plugin_version() ) {
    			CduLine[13] = "la00Plugin " + decode_plugin_version( this.aircraft.plugin_version() );
    		} 
    		if ( ! XHSIStatus.receiving ) {
    			CduLine[13] = "la00INDEPENDENT MODE";	
    		}

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
            drawDisplayLines(g2);
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
    		default :  c = str.charAt(i); 
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
            
            List<CduLine> l = QpacMcduData.decodeLine(CduLine[i]);
            for(CduLine o : l){                    
                    x = (int) Math.round( cdu_gc.cdu_screen_topleft_x + o.pos * cdu_gc.cdu_digit_width);
                    decodeColor(g2, o.color );
                    decodeFont(g2, o.font );
                    g2.drawString(translateCduLine(o.text), x, yy);
            }    
        }
    }

    
    public void mousePressed(Graphics2D g2, MouseEvent e) {
    }

  
    public void keyPressed(KeyEvent k) {          
    }    
    
    private String decode_plugin_version(int plugin_version) {
        if (plugin_version == 0.0f) {
            return "1.0 Beta ?";
        } else {
            String pv = "" + plugin_version; // example: 1.0 Beta 8 is "10008"
            String pv_displayed = pv.substring(0, 1) + "." + pv.substring(1, 2); // "major.minor" (example: "1.0")
            if (pv.substring(2, 3).equals("0") == false)
                pv_displayed += "." + pv.substring(2, 3); // "major.minor.bugfix" if bugfix!=0
            if (pv.substring(3, 5).equals("00") == false)
                if (pv.substring(3, 4).equals("9"))
                    pv_displayed += " RC" + Integer.valueOf(pv.substring(4, 5)); // "major.minor[.bugfix] RCx" if xy>=90
                else
                    pv_displayed += " Beta " + Integer.valueOf(pv.substring(3, 5)); // "major.minor[.bugfix] Beta xx" if xx!=00
            return pv_displayed;
        }

    }
    
}
