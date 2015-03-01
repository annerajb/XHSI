/**
* APHeadingReadout.java
* 
* Displays the AP heading at the top
* 
* Copyright (C) 2014  Marc Rogiers (marrog.123@gmail.com)
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavBeacon;
import net.sourceforge.xhsi.model.RadioNavigationObject;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class APHeadingReadout extends NDSubcomponent {

    private static final long serialVersionUID = 1L;


    public APHeadingReadout(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered
//                && ( ( this.avionics.map_submode() == Avionics.EFIS_MAP_APP ) || ( this.avionics.map_submode() == Avionics.EFIS_MAP_VOR ) )
                && ( nd_gc.panel_rect.width >= 560 ) ) {

            DecimalFormat degrees_formatter = new DecimalFormat("000");

            String hdg_text = degrees_formatter.format( Math.round(this.avionics.heading_bug()) ) + " H";

            int hdg_x = nd_gc.border_left + nd_gc.panel_rect.width*157/600;
            int hdg_y = nd_gc.border_top + nd_gc.line_height_xl;
            
            g2.setColor(nd_gc.heading_bug_color);
            g2.setFont(nd_gc.font_m);
            g2.drawString(hdg_text, hdg_x, hdg_y);

        }
        
    }


}
