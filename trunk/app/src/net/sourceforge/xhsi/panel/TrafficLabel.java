/**
* TrafficLabel.java
* 
* Draw the strings for TCAS status info and TA's
* 
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.panel;

import java.awt.Component;
import java.awt.Graphics2D;

import net.sourceforge.xhsi.XHSISettings;

//import de.georg_gruetter.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.TCAS;


public class TrafficLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;


    public TrafficLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        int tfc_label_x = nd_gc.border_left + 15;
        int tfc_label_y = nd_gc.panel_size.height - nd_gc.border_bottom - 150;
        g2.setColor(nd_gc.traffic_color);
//        if ( ! XHSISettings.hide_tfc ) {
            g2.setFont(nd_gc.font_small);
            g2.drawString("TFC", tfc_label_x, tfc_label_y);
//        }
        tfc_label_y += 15;
        g2.setFont(nd_gc.font_tiny);
        g2.drawString("TA ONLY", tfc_label_x, tfc_label_y);

    }


}