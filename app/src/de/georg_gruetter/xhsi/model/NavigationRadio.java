/**
* NavigationRadio.java
* 
* Facade for all flight simulator variables and aggregated information that 
* belongs to a navigation radio, like e. g. frequency, distance to tuned
* navigation object or reception flag.
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
package de.georg_gruetter.xhsi.model;

import de.georg_gruetter.xhsi.model.xplane.XPlaneSimDataRepository;

public class NavigationRadio {
		
	public static final int RADIO_TYPE_NAV = 0;
	public static final int RADIO_TYPE_ADF = 1;
	
	/**
	 * number of bank. either 1 or 2
	 */
	private int bank;
	
	/**
	 * type of radio - must be a RADIO_TYPE constant
	 */
	private int type;
	
	/**
	 * frequency tuned into the radio
	 */
	private float frequency;			
		
	private int sim_data_id_freq;
	private int sim_data_id_deflection;
	private int sim_data_id_dme_distance;
	private int sim_data_id_nav_cdi;
	
	private XPlaneSimDataRepository sim_data_repository;
	private Aircraft aircraft;
	private NavigationObjectRepository no_repository;
	private RadioNavigationObject rnav_object;
	private Avionics avionics;
	
	// -----------------------------------------------------------------------
	public NavigationRadio(int bank, int radio_type, int sim_data_id_freq, int sim_data_id_deflection, int sim_data_id_dme_distance, int sim_data_id_nav_cdi, Avionics avionics) {
		this.bank = bank;
		this.type = radio_type;
		this.frequency = 0;
		this.sim_data_id_freq = sim_data_id_freq;
		this.sim_data_id_deflection = sim_data_id_deflection;
		this.sim_data_id_dme_distance = sim_data_id_dme_distance;
		this.sim_data_id_nav_cdi = sim_data_id_nav_cdi;
		
		this.sim_data_repository = XPlaneSimDataRepository.get_instance();		// TODO: a reference to the flightsim.xplane package should not occur from within flightsim!
		this.no_repository = NavigationObjectRepository.get_instance();
		this.avionics = avionics;
		this.aircraft = this.avionics.get_aircraft();					
	}

	// -----------------------------------------------------------------------
	
	public boolean receiving() {
		update_radio_data();
		if (this.rnav_object instanceof Localizer) {
			boolean result = ((int) this.sim_data_repository.get_sim_value(this.sim_data_id_nav_cdi) == 1);
			return result;
		} else {
			return ((this.rnav_object != null) && (get_deflection() != 90.0f));
		}
	}	

	public RadioNavigationObject get_radio_nav_object() {
		update_radio_data();
		return this.rnav_object;
	}
	
	public float get_deflection() {
		update_radio_data();
		return this.sim_data_repository.get_sim_value(this.sim_data_id_deflection);
	}
	
	public float get_frequency() {
		update_radio_data();
		return this.frequency;
	}
	
	public float get_distance() {
		if (this.rnav_object instanceof Localizer) {
			return 0;
		} else {
			return this.sim_data_repository.get_sim_value(this.sim_data_id_dme_distance);
		}
	}	
	
	public int get_bank() {
		return this.bank;
	}
	
	public boolean freq_is_nav() {
		return (this.type == RADIO_TYPE_NAV);
	}
	
	public boolean freq_is_localizer() {
		return ((this.type == RADIO_TYPE_NAV) && (this.rnav_object != null) && (this.rnav_object instanceof Localizer));		
	}
	
	public boolean freq_is_adf() {
		return (this.type == RADIO_TYPE_ADF);
	}
	
	// -----------------------------------------------------------------------
	private void update_radio_data() {
		float current_freq = this.sim_data_repository.get_sim_value(this.sim_data_id_freq);
		if (current_freq > 1000.0f) {
			current_freq = (current_freq/100.0f);
		}
		if ((this.frequency != current_freq) || 
			(this.rnav_object == null) ||
			(this.aircraft.distance_to(this.rnav_object) > this.rnav_object.range)) {
			this.frequency = current_freq;
			this.rnav_object = this.no_repository.find_tuned_nav_object(this.aircraft.lat(), this.aircraft.lon(), current_freq);	
		}
	}
}
