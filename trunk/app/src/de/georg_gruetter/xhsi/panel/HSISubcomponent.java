/**
* HSISubcomponent.java
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
package de.georg_gruetter.xhsi.panel;

import java.awt.Graphics2D;

import de.georg_gruetter.xhsi.model.Aircraft;
import de.georg_gruetter.xhsi.model.AircraftEnvironment;
import de.georg_gruetter.xhsi.model.Avionics;
import de.georg_gruetter.xhsi.model.FMS;
import de.georg_gruetter.xhsi.model.ModelFactory;

public abstract class HSISubcomponent {

	HSIGraphicsConfig hsi_gc;
	ModelFactory model_factory;
	Aircraft aircraft;
	Avionics avionics;
	AircraftEnvironment aircraft_environment;
	FMS fms;
	
	public HSISubcomponent(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		this.hsi_gc = hsi_gc;
		this.model_factory = model_factory;
		this.aircraft = this.model_factory.get_aircraft_instance();
		this.avionics = this.aircraft.get_avionics();
		this.aircraft_environment = this.aircraft.get_environment();
		this.fms = this.avionics.get_fms();
	}	
	
	public abstract void paint(Graphics2D g2);
	
	public  String toString() {
		return this.getClass().getName();
	}
}
