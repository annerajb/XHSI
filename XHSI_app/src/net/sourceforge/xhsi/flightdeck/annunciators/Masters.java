/**
* Masters.java
* 
* ...
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2018  Nicolas Carel
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
package net.sourceforge.xhsi.flightdeck.annunciators;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import java.util.logging.Logger;

import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimCommand;
import net.sourceforge.xhsi.util.ColorUtilities;


public class Masters extends AnnunSubcomponent {

    private static final long serialVersionUID = 1L;

    private SimCommand sim_command;
    
    Color off_warning;
    Color on_warning;
    Color off_caution;
    Color on_caution;
    Color off_border;
    Color on_border;

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    public Masters(ModelFactory model_factory, AnnunGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        this.sim_command = this.aircraft.get_sim_command();
        off_warning = annun_gc.warning_color.darker().darker().darker().darker().darker();
        on_warning = annun_gc.warning_color;
        off_caution = annun_gc.caution_color.darker().darker().darker().darker().darker();
        on_caution = annun_gc.caution_color;
        off_border = annun_gc.instrument_background_color.brighter();
        on_border = annun_gc.markings_color.darker();
        
    }


    public void paint(Graphics2D g2) {
        if ( true ) {
            drawMasters(g2);
        }
        if (preferences.get_draw_mouse_areas()) drawMouseAreas(g2);
    }

   
    
    private void drawMasters(Graphics2D g2) {

        double warning;
        double caution;
        
        if ( this.avionics.is_cl30() ) {
            warning = this.avionics.cl30_mast_warn();
            caution = this.avionics.cl30_mast_caut();
        } else {
            warning = ( this.aircraft.master_warning() && this.aircraft.battery() ) ? 1.0 : 0.0;
            caution = ( this.aircraft.master_caution() && this.aircraft.battery() ) ? 1.0 : 0.0;
        }

        Stroke original_stroke = g2.getStroke();
        g2.setStroke(annun_gc.master_stroke);
        g2.setFont(annun_gc.font_xxl);

        g2.setColor( ColorUtilities.blend(off_warning, on_warning, warning) );
        g2.fill(annun_gc.master_buttons[AnnunGraphicsConfig.BUTTON_MASTER_WARNING]);
        g2.setColor( ColorUtilities.blend(off_border, on_border, warning) );
        g2.draw(annun_gc.master_buttons[AnnunGraphicsConfig.BUTTON_MASTER_WARNING]);
        g2.drawString("MASTER", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "MASTER")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 - annun_gc.line_height_xxl*2 - annun_gc.line_height_xxl/2 - 2);
        g2.drawString("WARNING", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "WARNING")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 - annun_gc.line_height_xxl*1 - annun_gc.line_height_xxl/2 - 2);

        g2.setColor( ColorUtilities.blend(off_caution, on_caution, caution) );
        g2.fill(annun_gc.master_buttons[AnnunGraphicsConfig.BUTTON_MASTER_CAUTION]);
        g2.setColor( ColorUtilities.blend(off_border, on_border, caution) );
        g2.draw(annun_gc.master_buttons[AnnunGraphicsConfig.BUTTON_MASTER_CAUTION]);
        g2.drawString("MASTER", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "MASTER")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 + annun_gc.line_height_xxl/2 + annun_gc.line_height_xxl*2 - 2);
        g2.drawString("CAUTION", annun_gc.master_square.x + annun_gc.master_square.width/2 - annun_gc.get_text_width(g2, annun_gc.font_xxl, "CAUTION")/2, annun_gc.master_square.y + annun_gc.master_square.height/2 + annun_gc.line_height_xxl/2 + annun_gc.line_height_xxl*3 - 2);

        g2.setStroke(original_stroke);

    }

    public void drawMouseAreas(Graphics2D g2) {
    	g2.setColor(Color.yellow);
    	for (int i = 0; i < annun_gc.master_buttons.length; i++) {
    		g2.draw(annun_gc.master_buttons[i]);
    	}
    }
    
    public void mouseClicked(Graphics2D g2, MouseEvent e) {  	
    	for (int i = 0; i < annun_gc.master_buttons.length; i++) {
    		if (annun_gc.master_buttons[i].contains(e.getX(), e.getY())) {
    			switch (i) {
    			case AnnunGraphicsConfig.BUTTON_MASTER_WARNING:
    				sim_command.send(SimCommand.CMD_MASTER_WRN);
    				break;
    			case AnnunGraphicsConfig.BUTTON_MASTER_CAUTION:
    				sim_command.send(SimCommand.CMD_MASTER_CTN);
    				break;
    			}
    		}
    	}
    }
    
}
