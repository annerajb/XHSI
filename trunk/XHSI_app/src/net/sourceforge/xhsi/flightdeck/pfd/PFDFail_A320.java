/**
* Fail.java
* 
* Draws a red cross when reception from X-Plane is lost
* 
* Copyright (C) 2012  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;


public class PFDFail_A320 extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

//    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public PFDFail_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
        if ( XHSIStatus.status.equals(XHSIStatus.STATUS_NO_RECEPTION) ) {
            drawFailCross(g2);
        }
    }


    private void drawFailCross(Graphics2D g2) {

        g2.setColor(pfd_gc.caution_color);
    	String failed_str = "XHSI COMM LOST";
        g2.setFont(pfd_gc.font_xxl);
    	g2.drawString( failed_str, pfd_gc.adi_cx - pfd_gc.get_text_width(g2, pfd_gc.font_xxl, failed_str)/2,  pfd_gc.tape_top - pfd_gc.line_height_xxl );

    }


}



