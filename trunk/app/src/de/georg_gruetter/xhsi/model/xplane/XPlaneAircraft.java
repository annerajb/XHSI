/**
* XPlaneAircraft.java
* 
* The X-Plane specific implementation of Aircraft.
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
package de.georg_gruetter.xhsi.model.xplane;

import de.georg_gruetter.xhsi.model.Aircraft;
import de.georg_gruetter.xhsi.model.AircraftEnvironment;
import de.georg_gruetter.xhsi.model.Avionics;
import de.georg_gruetter.xhsi.model.CoordinateSystem;
import de.georg_gruetter.xhsi.model.NavigationObject;

public class XPlaneAircraft implements Aircraft {
	
	private XPlaneSimDataRepository sim_data;
	private Avionics avionics;
	private AircraftEnvironment environment;
	
	public XPlaneAircraft() {
		this.sim_data = XPlaneSimDataRepository.get_instance();
		this.environment = new XPlaneAircraftEnvironment();
		this.avionics = new XPlaneAvionics(this);
	}
	
	public float lat() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE); }			// degree
	public float lon() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE); }			// degree	
	public float ground_speed() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_GROUNDSPEED) * 1.94385f); }  	// knots
	public float true_air_speed() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_TRUE_AIRSPEED) * 1.94385f); } // knots
	public float heading() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGPSI); }		// degree radians

	public float horizontal_path() {
		if (ground_speed() < 5) {
			return heading();
		} else {
			float path = (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH) + 
			               sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGVAR));
			if (path < 0)
				path += 360;
			else if (path > 360)
				path -= 360;
			
			return path;
		}
	}
	
	public float slip() {
		return heading() - horizontal_path();
	}
	
	public float turn_speed() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_R); }		// degree per seconds
	public float roll_angle() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_PHI); }		// degree radians

	public float distance_to(NavigationObject nav_object) {
		return CoordinateSystem.distance(lat(), lon(), nav_object.lat, nav_object.lon);
	}

	public long time_when_arriving_at(NavigationObject nav_object) {
		return time_after_distance(distance_to(nav_object));
	}
	
	public long time_after_distance(float distance_nm) {
		return (long) (sim_time_zulu() + (distance_nm / ground_speed()) * 3600.0f);
	}

	public float sim_time_zulu() {
		return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_TIME_ZULU_TIME_SEC);
	}

	public Avionics get_avionics() {
		return this.avionics;
	}

	public AircraftEnvironment get_environment() {
		return this.environment;
	}

	public float magnetic_variation() {
		return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGVAR);
	}
}
