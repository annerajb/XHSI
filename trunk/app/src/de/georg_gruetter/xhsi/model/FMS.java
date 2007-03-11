/**
* FMS.java
* 
* Model class for a flight management system (FMS)
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

import java.util.ArrayList;

public class FMS {
	// TASK: remove singleton code! Have Avionics create instance of FMS
	private static FMS single_instance;
	private FMSEntry next_waypoint = null;;
	private ArrayList entries;
	
	public static FMS get_instance() {
		if (FMS.single_instance == null) {
			FMS.single_instance = new FMS();
		}
		return FMS.single_instance;
	}
	
	private FMS() {
		this.entries = new ArrayList();
	}

	/**
	 * @return boolean - true if FMS contains entries, false, otherwise
	 */
	public boolean is_active() {
		return (this.entries.isEmpty() == false);
	}
	
	/**
	 * Deletes all FMS entries
	 */
	public void clear() {
		this.next_waypoint = null;
		this.entries.clear();
	}

	/**
	 * Appends entry to the current list of entries
	 *  
	 * @param entry - the entry to be appended
	 */
	public void append_entry(FMSEntry entry) {
		this.entries.add(entry);
		if (entry.active) {
			this.next_waypoint = entry;
		}
	}

	/**
	 * @return FMSEntry - the currently selected waypoint in the FMS
	 */
	public FMSEntry get_next_waypoint() {
		return this.next_waypoint;
	}
	
	public int get_nb_of_entries() {
		return this.entries.size();
	}
	
	public FMSEntry get_entry(int position) {
		return (FMSEntry) this.entries.get(position);
	}
	
	public void print_entries() {
		System.out.println("==================================");
		System.out.println("FMS Entries:");
		for (int i=0;i<this.entries.size();i++) {
			System.out.println("#" + i + ": " + ((FMSEntry)this.entries.get(i)).toString());
		}
	}

}
