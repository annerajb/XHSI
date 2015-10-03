/**
* XPlaneNearestAirport.java
* 
* Continuously find the nearest airport in the background
* 
* Copyright (C) 2014  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.model.xplane;

import java.util.logging.Logger;

import net.sourceforge.xhsi.StoppableThread;
import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.NavigationObjectRepository;


public class XPlaneNearestAirport extends StoppableThread {

    private static final Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private Aircraft aircraft;
    private NavigationObjectRepository nor;
    private XHSIPreferences preferences;


    public XPlaneNearestAirport(Aircraft my_acf) throws Exception {
        super();
        
        this.aircraft = my_acf;
        this.nor = NavigationObjectRepository.get_instance();
        this.preferences = XHSIPreferences.get_instance();
        
        this.keep_running = true;
    }


    public void run() {

        while (this.keep_running) {
            
            if ( (this.aircraft.lat() != 0.0f) || (this.aircraft.lon() != 0.0f) ) {
                this.aircraft.set_nearest_arpt(
                    nor.find_nrst_arpt(this.aircraft.lat(), this.aircraft.lon(), this.aircraft.get_min_rwy_length(), true)
                );
            }

            try { Thread.sleep(100l); } catch(Exception e) {}
            
        }
        
        logger.fine("XPlaneNearestAirport stopped");
        
    }

}