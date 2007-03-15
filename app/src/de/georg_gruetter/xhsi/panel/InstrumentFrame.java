/**
* InstrumentFrame.java
* 
* Renders the instrument frame of the HSI.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi.panel;

import java.awt.Graphics2D;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class InstrumentFrame extends HSISubcomponent {

	public InstrumentFrame(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}

	public void paint(Graphics2D g2) {
		//g2.setColor(Color.DARK_GRAY);
		//g2.fill(hsi_gc.instrument_frame);	
		g2.clearRect(0,0,hsi_gc.border_right, hsi_gc.panel_size.height);
		g2.clearRect(hsi_gc.panel_size.width - hsi_gc.border_right, 0, hsi_gc.border_right, hsi_gc.panel_size.height);
	}
}
