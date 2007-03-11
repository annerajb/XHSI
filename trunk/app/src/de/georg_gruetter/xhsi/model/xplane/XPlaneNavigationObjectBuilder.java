/**
* XPlaneNavigationObjectBuilder.java
* 
* Reads X-Planes earth nav data databases nav.dat, fix.dat and apt.dat and 
* stores extracted data in NavigationObjectRepository.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

import de.georg_gruetter.xhsi.HSIPreferences;
import de.georg_gruetter.xhsi.HSIStatus;
import de.georg_gruetter.xhsi.PreferencesObserver;
import de.georg_gruetter.xhsi.ProgressObserver;
import de.georg_gruetter.xhsi.model.Airport;
import de.georg_gruetter.xhsi.model.Fix;
import de.georg_gruetter.xhsi.model.Localizer;
import de.georg_gruetter.xhsi.model.NavigationObjectRepository;
import de.georg_gruetter.xhsi.model.VOR;

public class XPlaneNavigationObjectBuilder implements PreferencesObserver {
	
	private String pathname_to_xplane;
	private NavigationObjectRepository nor;
	private ProgressObserver progressObserver;
	
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	public XPlaneNavigationObjectBuilder(String pathname_to_xplane) throws Exception {
		this.pathname_to_xplane = pathname_to_xplane;
		this.nor = NavigationObjectRepository.get_instance();						
		this.progressObserver = null;
	}
	
	public void set_progress_observer(ProgressObserver observer) {
		this.progressObserver = observer;
	}
	
	public void read_all_tables() throws Exception {
		if (new File(this.pathname_to_xplane).exists()) {
			logger.fine("Start reading X-Plane ressource files in " + HSIPreferences.PREF_XPLANE_DIR);

			this.nor.init();
			
			if (this.progressObserver != null) {
				this.progressObserver.set_progress("Loading Earth nav databases", "loading nav.dat",0.0f);
			}		
			read_nav_table();
			
			if (this.progressObserver != null) {
				this.progressObserver.set_progress("Loading Earth nav databases", "loading fix.dat",33.3f);
			}
			read_fix_table();
			
			if (this.progressObserver != null) {
				this.progressObserver.set_progress("Loading Earth nav databases", "loading apt.dat",66.6f);
			}		
			read_apt_table();
			
			if (this.progressObserver != null) {
				this.progressObserver.set_progress("Loading Earth nav databases", "done",100.0f);
			}
		} else {
			logger.warning("Cannot read tables because path to X-Plane is wrong!");
		}
	}
	
	public void read_nav_table() throws Exception {
		logger.fine("... read navigation database (nav.dat)");
		File file = new File( this.pathname_to_xplane + "/Resources/Earth nav data/nav.dat");
		BufferedReader reader = new BufferedReader( new FileReader( file ));
		String line;
		int info_type;
		String[] tokens;
		long line_number = 0;
		
		while ((line = reader.readLine()) != null) {
			line_number++;

			while ((line = reader.readLine()) != null) {
				line_number++;
				line = line.trim();
				if ((line_number > 2) && (line.equals("") == false) && (line.equals("99") == false)){
					try {
						info_type = Integer.parseInt(line.substring(0,2).trim());
						if ((info_type ==2) || (info_type == 3)) {
							tokens = line.split("\\s+",9);
							nor.add_nav_object(new VOR(
								tokens[8],
								tokens[7], 
								info_type,
								Float.parseFloat(tokens[1]),
								Float.parseFloat(tokens[2]),
								Float.parseFloat(tokens[3]),
								Float.parseFloat(tokens[4]),
								Integer.parseInt(tokens[5])));
						} else if ((info_type == 4) || (info_type == 5)) {
							tokens = line.split("\\s+",11);
							nor.add_nav_object(new Localizer(
									tokens[8] + " " + tokens[9],
									tokens[7], 
									info_type,
									Float.parseFloat(tokens[1]),
									Float.parseFloat(tokens[2]),
									Float.parseFloat(tokens[3]),
									Float.parseFloat(tokens[4]),
									Integer.parseInt(tokens[5]),
									Float.parseFloat(tokens[6]),
									tokens[8],
									tokens[9]));							
						}
					} catch (Exception e) {
						logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
					}
				}
			}
		}
	}

	public void read_fix_table() throws Exception {
		logger.fine("... read fix database (fix.dat)");
		File file = new File( this.pathname_to_xplane + "/Resources/Earth nav data/fix.dat");
		BufferedReader reader = new BufferedReader( new FileReader( file ));
		String line;
		String[] tokens;
		long line_number = 0;
		
		while ((line = reader.readLine()) != null) {
			line_number++;
			line = line.trim();
			if ((line_number > 2) && (line.equals("") == false) && (line.equals("99") == false)){
				try {
					tokens = line.split("\\s+",3);
					nor.add_nav_object(new Fix(
							tokens[2],
							Float.parseFloat(tokens[0]), 
							Float.parseFloat(tokens[1])));
				} catch (Exception e) {
					logger.warning("/nParse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
				}
			}
		}
	}		

	public void read_apt_table() throws Exception {
		logger.fine("... read airport database (apt.dat)");
		
		File file = new File( this.pathname_to_xplane + "/Resources/Earth nav data/apt.dat");
		BufferedReader reader = new BufferedReader( new FileReader( file ));
		String line;
		long line_number = 0;
		int info_type;
		String airport_icao_code = "";
		String airport_name = "";
		boolean current_airport_saved = true;
		
		while ((line = reader.readLine()) != null) {
			line_number++;
			if ((line.trim() != "") && (line.length() > 2)) {
				try {
					info_type = Integer.parseInt(line.substring(0,2).trim());
					if (info_type == 1) {
						// airport header
						airport_icao_code = line.substring(13,17);
						airport_name = line.substring(18);
						current_airport_saved = false;
					} else if ((info_type == 10) && (current_airport_saved == false)) {
						// runway
						float lat = Float.parseFloat(line.substring(3,13));
						float lon = Float.parseFloat(line.substring(14,24));
						nor.add_nav_object(new Airport(airport_name, airport_icao_code, lat, lon));
						current_airport_saved = true;
					}
				} catch (Exception e) {
					logger.warning("/nParse error in " +file.getName() + ":" + line_number + "(" + e + ")");
				}
			}
		}
	}

	public void preference_changed(String key) {
		logger.finest("preference changed");
		if (key.equals(HSIPreferences.PREF_XPLANE_DIR)) {
			// reload navigation databases
			this.pathname_to_xplane = HSIPreferences.get_instance().get_preference(HSIPreferences.PREF_XPLANE_DIR);
			if (HSIStatus.nav_db_status.equals(HSIStatus.STATUS_NAV_DB_NOT_FOUND) == false) {
				try {
					logger.fine("reload navigation tables");
					read_all_tables();
				} catch (Exception e) {
					logger.warning("Could not read navigation tables! (" + e.toString() + ")");
				}
			} else {
				logger.warning("Could not find X-Plane homedir! (Status:" + HSIStatus.nav_db_status + ")");
			}
		}
		
	}		
}
