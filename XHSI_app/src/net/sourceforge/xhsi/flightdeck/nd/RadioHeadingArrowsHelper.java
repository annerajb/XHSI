/**
* RadioHeadingArrowsHelper.java
* 
* Renders the radio navigatio object pointers. Used by RadioHeadingArrows.
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
package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public class RadioHeadingArrowsHelper {
    public static void draw_nav1_forward_arrow(Graphics2D g2, int x, int y, int length, int base_width) {
        GeneralPath polyline;
        int arrow_top_y = y - (base_width/3);
        int arrow_bottom_y = arrow_top_y + length;

        g2.drawLine(
            x,arrow_top_y,
            x,arrow_bottom_y);
        g2.drawLine(
            x-(base_width/4),arrow_bottom_y - length/8,
            x+(base_width/4),arrow_bottom_y - length/8);

        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
        polyline.moveTo (x - (base_width/2), y);
        polyline.lineTo( x , arrow_top_y);
        polyline.lineTo( x + (base_width/2), y);
        g2.draw(polyline);
    }

    public static void draw_nav1_backward_arrow(Graphics2D g2, int x, int y, int length, int base_width) {
        GeneralPath polyline;
        int arrow_top_y = y - length + (base_width/3);
        int arrow_bottom_y = arrow_top_y + length;

        g2.drawLine(
                x,arrow_top_y,
                x,arrow_bottom_y);
        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
        polyline.moveTo (x - (base_width/2), arrow_bottom_y);
        polyline.lineTo( x , y);
        polyline.lineTo( x + (base_width/2), arrow_bottom_y);
        g2.draw(polyline);
    }

    public static void draw_nav2_forward_arrow(Graphics2D g2, int x, int y, int length, int base_width) {
        GeneralPath polyline;
        int arrow_top_y = y - (base_width/4);
        int arrow_bottom_y = arrow_top_y + length;

        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
        polyline.moveTo(x, y);
        polyline.lineTo(x, arrow_top_y);
        polyline.lineTo(x + (base_width/4), y);
        polyline.lineTo(x + (base_width/4), arrow_bottom_y - (base_width/4));
        polyline.lineTo(x + (base_width/2), arrow_bottom_y - (base_width/4));
        polyline.lineTo(x + (base_width/2), arrow_bottom_y);
        polyline.lineTo(x - (base_width/2), arrow_bottom_y);
        polyline.lineTo(x - (base_width/2), arrow_bottom_y - (base_width/4));
        polyline.lineTo(x - (base_width/4), arrow_bottom_y - (base_width/4));
        polyline.lineTo(x - (base_width/4), y);
        polyline.lineTo(x, arrow_top_y);
        g2.draw(polyline);
    }

    public static void draw_nav2_backward_arrow(Graphics2D g2, int x, int y, int length, int base_width) {
        GeneralPath polyline;
        int arrow_top_y = y - length + (base_width/3);
        int arrow_bottom_y = arrow_top_y + length;

        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 9);
        polyline.moveTo(x - (base_width/2), arrow_bottom_y);
        polyline.lineTo(x - (base_width/2), y);
        polyline.lineTo(x - (base_width/4), y - (base_width/4));
        polyline.lineTo(x - (base_width/4), arrow_top_y + (base_width/4));
        polyline.lineTo(x, arrow_top_y);
        polyline.lineTo(x + (base_width/4), arrow_top_y + (base_width/4));
        polyline.lineTo(x + (base_width/4), y - (base_width/4));
        polyline.lineTo(x + (base_width/2), y);
        polyline.lineTo(x + (base_width/2), arrow_bottom_y);
        polyline.lineTo(x , y);
        polyline.lineTo(x - (base_width/2), arrow_bottom_y);
        g2.draw(polyline);
    }

    public static void draw_nav1_airbus_forward_arrow(Graphics2D g2, int x, int y, int length, int base_width, boolean adf) {
        GeneralPath polyline;
        int arrow_top_y = y + base_width;
        int arrow_bottom_y = arrow_top_y + base_width*2/3;

        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
        if (adf) {
        	// ADF - NDB
        	g2.drawLine( x,y,  x,y+length);
            polyline.moveTo (x - (base_width/2), arrow_bottom_y);
            polyline.lineTo( x , arrow_top_y);
            polyline.lineTo( x + (base_width/2), arrow_bottom_y);
        } else {
        	// VOR/DME
            g2.drawLine( x,y,  x,arrow_top_y);
            g2.drawLine( x,arrow_bottom_y, x,y+length);
            polyline.moveTo (x - (base_width/2), arrow_bottom_y);
            polyline.lineTo( x , arrow_top_y);
            polyline.lineTo( x + (base_width/2), arrow_bottom_y);
            polyline.lineTo (x - (base_width/2), arrow_bottom_y);        
        }
        g2.draw(polyline);
    }

    public static void draw_nav1_airbus_backward_arrow(Graphics2D g2, int x, int y, int length, int base_width, boolean adf) {
        GeneralPath polyline;
        int arrow_top_y = y - base_width*5/3;
        int arrow_bottom_y = arrow_top_y + base_width*2/3;

        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2);
        if (adf) {
        	// ADF - NDB
        	g2.drawLine( x,y,  x,y-length);
            polyline.moveTo (x - (base_width/2), arrow_bottom_y);
            polyline.lineTo( x , arrow_top_y);
            polyline.lineTo( x + (base_width/2), arrow_bottom_y);
        } else {
        	// VOR/DME
            g2.drawLine( x,y-length, x,arrow_top_y);
            g2.drawLine( x,arrow_bottom_y,  x,y);
            polyline.moveTo (x - (base_width/2), arrow_bottom_y);
            polyline.lineTo( x , arrow_top_y);
            polyline.lineTo( x + (base_width/2), arrow_bottom_y);
            polyline.lineTo (x - (base_width/2), arrow_bottom_y);           	
        }
    
        g2.draw(polyline);

    }

    public static void draw_nav2_airbus_forward_arrow(Graphics2D g2, int x, int y, int length, int base_width, boolean adf) {
        GeneralPath polyline;
        int arrow_top_y = y + (length/2);
        int arrow_base_y = arrow_top_y + base_width*2/3;
        int arrow_bottom_y = y + length;
        int bx = (base_width/2);
        int dx = (base_width*3/10);

        g2.drawLine(
                x,y,
                x,arrow_top_y);
        
        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
        if (adf) {
        	// ADF - NDB
            polyline.moveTo(x-bx, arrow_bottom_y);
            polyline.lineTo(x-bx, arrow_base_y);
            polyline.lineTo(x, arrow_top_y);
            polyline.lineTo(x+bx, arrow_base_y);
            polyline.lineTo(x+bx, arrow_bottom_y);        	
        } else {
        	// VOR/DME
            polyline.moveTo(x-dx, arrow_bottom_y);
            polyline.lineTo(x-dx, arrow_base_y);
            polyline.lineTo(x-bx, arrow_base_y);
            polyline.lineTo(x, arrow_top_y);
            polyline.lineTo(x+bx, arrow_base_y);
            polyline.lineTo(x+dx, arrow_base_y);
            polyline.lineTo(x+dx, arrow_bottom_y);   	
        }
        g2.draw(polyline);
    }

    public static void draw_nav2_airbus_backward_arrow(Graphics2D g2, int x, int y, int length, int base_width, boolean adf) {
        GeneralPath polyline;
        int arrow_top_y = y - length;
        int arrow_bottom_y = y - length/2; 
        int arrow_base_y = arrow_bottom_y - base_width/2;
        int dx = adf ? (base_width/2) : (base_width*3/10);
        
        g2.drawLine(
                x,arrow_base_y,
                x,y);

        polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 9);
        polyline.moveTo(x-dx, arrow_top_y);
        polyline.lineTo(x-dx, arrow_bottom_y);
        polyline.lineTo(x, arrow_base_y);
        polyline.lineTo(x+dx, arrow_bottom_y);
        polyline.lineTo(x+dx, arrow_top_y);
        g2.draw(polyline);
    }
}
