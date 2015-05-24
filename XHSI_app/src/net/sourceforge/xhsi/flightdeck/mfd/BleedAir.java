/**
* LowerEicas.java
* 
* Lower EICAS and ECAM
* 
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

package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.Component;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;

public class BleedAir extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

	public BleedAir(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}

	public void paint(Graphics2D g2) {

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_BLEED ) {
			// Page ID
			drawPageID(g2, "BLEED");
			drawBleedValvesStatus(g2);
		}
	}


	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.panel_rect.x;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}
	
	private void drawBleedValvesStatus(Graphics2D g2) {
		int dy = mfd_gc.line_height_xl * 2;
		int x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2;
		g2.setFont(mfd_gc.font_xl);
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_CROSS), x, dy * 2);
		g2.drawString("X-Bleed",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 2 );
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_APU), x, dy * 3);
		g2.drawString("APU",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 3 );
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1), x, dy * 4);
		g2.drawString("ENG 1",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 4 );
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG2), x, dy * 5);
		g2.drawString("ENG 2",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 5 );

		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1_HP), x, dy * 6);
		g2.drawString("ENG 1 HP",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy  * 6 );
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG2_HP), x, dy * 7);		
		g2.drawString("ENG 2 HP",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy  * 7 );
		
		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK1), x, dy * 8);
		g2.drawString("PACK 1",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 8 );

		drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_PACK2), x, dy * 9);
		g2.drawString("PACK 2",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 9 );

		g2.drawString("RAM AIR",mfd_gc.panel_rect.x, mfd_gc.panel_rect.y + dy * 10 );
		// drawValveVert(g2, aircraft.bleed_valve(Aircraft.BLEED_VALVE_ENG1), x, dy * 6);
	}

    private void drawValveVert(Graphics2D g2, ValveStatus valve_sts, int x, int y) {
    	int r = mfd_gc.hyd_valve_r;
    	
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_OPEN) {
            g2.setColor(mfd_gc.ecam_normal_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	}
        g2.drawOval(x-r,y-r,r*2,r*2);
        
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_CLOSED_FAILED) {
    		g2.drawLine(x-r, y, x+r, y);
    	} else {
    		g2.drawLine(x, y-r, x, y+r);
    	}
    } 
}
