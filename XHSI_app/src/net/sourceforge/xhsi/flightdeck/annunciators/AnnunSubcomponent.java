/**
* AnnunSubcomponent.java
* 
* Superclass for all elements of the HSI which are subcomponents of
* HSIComponent.
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
package net.sourceforge.xhsi.flightdeck.annunciators;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.flightdeck.Subcomponent;


public abstract class AnnunSubcomponent extends Subcomponent {
	
	private static final long serialVersionUID = 1L;

    AnnunGraphicsConfig annun_gc;

    public AnnunSubcomponent(ModelFactory model_factory, AnnunGraphicsConfig annun_gc) {
        super(model_factory, annun_gc);

        this.annun_gc = annun_gc;

        this.model_factory = model_factory;
        this.aircraft = this.model_factory.get_aircraft_instance();
        this.avionics = this.aircraft.get_avionics();
        this.aircraft_environment = this.aircraft.get_environment();
        this.fms = this.avionics.get_fms();
        this.tcas = this.avionics.get_tcas();
        this.preferences = XHSIPreferences.get_instance();

        this.parent_component = null;

    }


    public AnnunSubcomponent(ModelFactory model_factory, AnnunGraphicsConfig annun_gc, Component parent_component) {
        this(model_factory, annun_gc);
        this.parent_component = parent_component;
    }


    public abstract void paint(Graphics2D g2);

    public abstract void mouseClicked(Graphics2D g2, MouseEvent e);

}
