/**
* InstrumentFrame.java
* 
* Renders the instrument frame of the HSI.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.ModelFactory;


public class InstrumentFrame extends NDSubcomponent {

    private static final long serialVersionUID = 1L;


    public InstrumentFrame(ModelFactory model_factory, NDGraphicsConfig hsi_gc) {
        super(model_factory, hsi_gc);
    }


    public void paint(Graphics2D g2) {

        if ( XHSIPreferences.get_instance().get_fancy_border() ) {
            // a rounded frame looks soo much nicer...
            g2.setPaint(nd_gc.border_gradient);
            g2.fill(nd_gc.instrument_frame);
            g2.setColor(nd_gc.border_color);
            g2.fill(nd_gc.instrument_outer_frame);
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(new BasicStroke(0.25f));
            // two fine lines add some depth to the inner side of the bevel
            g2.setColor(nd_gc.border_color);
            g2.drawRoundRect(
                    nd_gc.border_left,
                    nd_gc.border_top,
                    nd_gc.panel_size.width - (nd_gc.border_left + nd_gc.border_right),
                    nd_gc.panel_size.height - (nd_gc.border_top + nd_gc.border_bottom),
                    (int)(30 * nd_gc.grow_scaling_factor),
                    (int)(30 * nd_gc.grow_scaling_factor));
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(
                    nd_gc.border_left - 1,
                    nd_gc.border_top - 1,
                    nd_gc.panel_size.width - (nd_gc.border_left + nd_gc.border_right) + 2,
                    nd_gc.panel_size.height - (nd_gc.border_top + nd_gc.border_bottom) + 2,
                    (int)(30 * nd_gc.grow_scaling_factor),
                    (int)(30 * nd_gc.grow_scaling_factor));
            // and another fine line at the outer side of the bevel
            g2.setColor(nd_gc.border_color.darker().darker().darker());
            g2.drawRoundRect(
                    nd_gc.border_left - nd_gc.border_left / 3,
                    nd_gc.border_top - nd_gc.border_top / 3,
                    nd_gc.panel_size.width - (nd_gc.border_left + nd_gc.border_right) + nd_gc.border_left / 3 + nd_gc.border_right / 3,
                    nd_gc.panel_size.height - (nd_gc.border_top + nd_gc.border_bottom) + nd_gc.border_top / 3 + nd_gc.border_bottom / 3,
                    (int)(30 * nd_gc.grow_scaling_factor),
                    (int)(30 * nd_gc.grow_scaling_factor));
            g2.setStroke(original_stroke);
        } else {
            // the cheapest way is to paint the borders as rectangles
            g2.setColor(Color.BLACK);
            if ( XHSIPreferences.get_instance().get_border_color().equalsIgnoreCase("irongray") ) {
                g2.setColor(nd_gc.color_irongray);
            }
            g2.fillRect(0, 0, nd_gc.border_left, nd_gc.panel_size.height);
            g2.fillRect(nd_gc.panel_size.width - nd_gc.border_right, 0, nd_gc.border_right, nd_gc.panel_size.height);
            g2.fillRect(0, 0, nd_gc.panel_size.width, nd_gc.border_top);
            g2.fillRect(0, nd_gc.panel_size.height - nd_gc.border_bottom, nd_gc.panel_size.width, nd_gc.border_bottom);
        }

    }


}
