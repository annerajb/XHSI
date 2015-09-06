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

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class CDUQpac extends CDUSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    double scalex = 1;
    double scaley = 1;
    double border;
    double border_x;
    double border_y;
   
    boolean drawregions = false;
    Font font;
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
    	if ((cdu_gc.cdu_source == Avionics.CDU_SOURCE_LEGACY) && this.avionics.is_qpac()) {
    		if ( this.preferences.cdu_display_only() ) {
    			drawDisplayOnly(g2);
    		} else {
    			drawFullPanel(g2);
    		}
    	}
    }

    
    private void drawDisplayOnly(Graphics2D g2) {
        
        if ( this.aircraft.battery() ) {
            
            scalex = (double)cdu_gc.panel_rect.width /363.0; //was: 343.0
            scaley = (double)cdu_gc.panel_rect.height/289.0;
            border_x = (double)cdu_gc.border_left;
            border_y = (double)cdu_gc.border_top;

            AffineTransform orig = g2.getTransform();
            g2.translate(border_x, border_y);
            g2.scale(scalex, scaley);
            g2.translate(font.getSize()/2, font.getSize());
            
            g2.setFont(font);
            double dy = row_coef;

            drawDisplayLines(g2, dy);

            g2.setTransform(orig);

        }
        
    }
    
    private void drawFullPanel(Graphics2D g2) {
    	
    }
    
    private void drawDisplayLines(Graphics2D g2, double dy) {
        
        for(int i=0; i < 14; i++){
            int x=i, xx = 0, yy = 0;
            if(i==0) {
                xx = 0;
                yy = upper_y;
            } else if ((i > 0) && (i < 13)){
                x = (((i+1) / 2) * 2) - ((i % 2) == 1 ? 0 : 1);
                yy = new Double(dy*(i)).intValue();
            } else if(i == 13) { 
                xx = 0;
                yy = new Double(dy*scratch_y_coef).intValue();
            }
            /*
            List l = xfmcData.decodeLine(xfmcData.getLine(x));
            for(Object o : l){
                    Object[] pts = (Object[]) o;
                    xx = new Double((Integer)pts[1]*char_width_coef).intValue();

                    g2.setColor(((Integer)pts[0]).intValue() == 0 ? cdu_gc.markings_color : cdu_gc.color_boeingcyan);
                    g2.drawString((String)pts[2], xx, yy);
            }
            */
        }

}
}
