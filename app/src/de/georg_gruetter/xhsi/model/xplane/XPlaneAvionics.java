/**
* XPlaneAvionics.java
* 
* The X-Plane specific implementation of Avionics.
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
import de.georg_gruetter.xhsi.model.Avionics;
import de.georg_gruetter.xhsi.model.FMS;
import de.georg_gruetter.xhsi.model.Localizer;
import de.georg_gruetter.xhsi.model.NavigationRadio;
import de.georg_gruetter.xhsi.model.RadioNavigationObject;

public class XPlaneAvionics implements Avionics {

	private XPlaneSimDataRepository sim_data;
	private Aircraft aircraft;
	private FMS fms;
	
	private NavigationRadio nav1_radio;
	private NavigationRadio nav2_radio;
	private NavigationRadio adf1_radio;
	private NavigationRadio adf2_radio;
		
	public XPlaneAvionics(Aircraft aircraft) {
		this.sim_data = XPlaneSimDataRepository.get_instance();
		this.aircraft = aircraft;
		this.fms = FMS.get_instance();
				
		this.nav1_radio = new NavigationRadio(
				1,
				NavigationRadio.RADIO_TYPE_NAV,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_FREQ_HZ,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_DIR_DEGT,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_DME_DIST_M,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_CDI,
				this);
		
		this.nav2_radio = new NavigationRadio(
				2,
				NavigationRadio.RADIO_TYPE_NAV,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_FREQ_HZ,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_DIR_DEGT,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_DME_DIST_M,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_CDI,
				this);
		
		this.adf1_radio = new NavigationRadio(
				1,
				NavigationRadio.RADIO_TYPE_ADF,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_FREQ_HZ,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_DIR_DEGT,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF1_DME_DIST_M,
				-1,
				this);
		
		this.adf2_radio = new NavigationRadio(
				2,
				NavigationRadio.RADIO_TYPE_ADF,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_FREQ_HZ,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_DIR_DEGT,
				XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_ADF2_DME_DIST_M,
				-1,
				this);
	}
	
	public NavigationRadio get_selected_radio1() {
		if (efis1_setting() == EFIS_NAV) {
			return this.nav1_radio;
		} else if (efis1_setting() == EFIS_ADF) {
			return this.adf1_radio;
		} else {
			return null;
		}
	}
	
	public NavigationRadio get_selected_radio2() {
		if (efis2_setting() == EFIS_NAV) {
			return this.nav2_radio;
		} else if (efis2_setting() == EFIS_ADF) {
			return this.adf2_radio;
		} else {
			return null;
		}		
	}
	
	public Localizer get_selected_localizer() {
		NavigationRadio radio;
		RadioNavigationObject rnav_object;
		
		radio = get_selected_radio1();
		if ((radio != null) &&
		    (radio.freq_is_nav()) &&
		    (radio.receiving())) {
			rnav_object = radio.get_radio_nav_object();
			if ((rnav_object != null) && (rnav_object instanceof Localizer)) {
				return (Localizer) rnav_object;
			}
		} else {
			radio = get_selected_radio2();
			if ((radio != null) &&
				(radio.freq_is_nav()) &&
				(radio.receiving())) {
				rnav_object = radio.get_radio_nav_object();
				if ((rnav_object != null) && (rnav_object instanceof Localizer)) {
					return (Localizer) rnav_object;
				}
			}
		}
		
		return null;
	}
	
	public int map_range() { return (int) (Math.pow(2.0d, sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_MAP_RANGE_SELECTOR)) * 10);}	
	public int efis1_setting() { return (int) sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_1_SELECTOR); }
	public int efis2_setting() { return (int) sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_DME_2_SELECTOR);}
	public boolean efis_shows_waypoints() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_WAYPOINTS) == 1.0f);}
	public boolean efis_shows_ndbs() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_NDBS) == 1.0f);}
	public boolean efis_shows_vors() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_VORS) == 1.0f);}
	public boolean efis_shows_airports() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_AIRPORTS) == 1.0f);}
	public boolean efis_shows_tcas() { return (sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_SWITCHES_EFIS_SHOWS_TCAS) == 1.0f);}
	
	public float nav1_course() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV1_COURSE_DEGM);	}
	public float nav2_course() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_RADIOS_NAV2_COURSE_DEGM); }

	public float heading_bug() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_HEADING_MAG); }
	public int autpilot_state() {  return (int) sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_STATE);	}
	public float autopilot_vertical_velocity() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_VERTICAL_VELOCITY);	}
	public float autopilot_altitude() { return sim_data.get_sim_value(XPlaneSimDataRepository.SIM_COCKPIT_AUTOPILOT_ALTITUDE);	}

	public Aircraft get_aircraft() { return this.aircraft; }
	public FMS get_fms() { return this.fms; }
}
