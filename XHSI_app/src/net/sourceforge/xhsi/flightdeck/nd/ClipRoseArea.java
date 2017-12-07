/**
* ClipRoseArea.java
* 
* Erase everything that falls outside the rose area
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2017  Nicolas Carel
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

import java.awt.Component;
import java.awt.Graphics2D;

import net.sourceforge.xhsi.model.ModelFactory;


public class ClipRoseArea extends NDSubcomponent {

    // private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static final long serialVersionUID = 1L;

    public ClipRoseArea(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {
            if ( nd_gc.draw_only_inside_rose ) {
                g2.setColor(nd_gc.background_color);
                g2.fill(nd_gc.clip_rose_area);
                if ( nd_gc.limit_arcs_at_60 && ! nd_gc.mode_plan && ! nd_gc.mode_centered ) {
                    g2.fillRect(0, 0, nd_gc.map_center_x - nd_gc.sixty_deg_hlimit, nd_gc.frame_size.height);
                    g2.fillRect(nd_gc.map_center_x + nd_gc.sixty_deg_hlimit, 0, nd_gc.map_center_x - nd_gc.sixty_deg_hlimit, nd_gc.frame_size.height);
                }
            } else {
                // leave at least the top of the window uncluttered
            	g2.setColor(nd_gc.background_color);
                g2.fillRect(0,0, nd_gc.frame_size.width, nd_gc.rose_y_offset);
            }
        }

    }

}

