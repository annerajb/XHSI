/**
* XPlaneDataPacketDecoder.java
* 
* Decodes the data contained in received data packets. Flight simulator data
* and FMS routes are extracted from received data packets and sent to the
* XPlaneSimDataRepository and the FMS respectively. XPlaneDataPacketDecoder
* also calls the tick_updates method of XPlaneSimDataRepository, which in turn
* triggers repainting of the UI.
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Logger;

import de.georg_gruetter.xhsi.model.FMS;
import de.georg_gruetter.xhsi.model.FMSEntry;

public class XPlaneDataPacketDecoder implements XPlaneDataPacketObserver {

	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	private boolean received_hsid_packet = false;
	private boolean received_fms_packet = false;
	
	XPlaneSimDataRepository xplane_data_repository = null;
	FMS fms = FMS.get_instance();
	
	public XPlaneDataPacketDecoder() {
		this.xplane_data_repository = XPlaneSimDataRepository.get_instance();
	}
	
	public void new_sim_data(byte[] sim_data) throws Exception {
		
		// identify the packet type (identified by the first four bytes)
		String packet_type = new String(sim_data,0,4).trim();
				
		if (packet_type.equals("HSID")) {
			if (this.received_hsid_packet == false)
				logger.fine("Received first HSID packet");
			
			DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));		
			data_stream.skipBytes(4);	// skip the bytes containing the packet type id
			int nb_of_data_points = (int) data_stream.readFloat();
			
			for (int i=0;i<nb_of_data_points;i++) {
				int data_point_id = (int) data_stream.readFloat();
				this.xplane_data_repository.store_sim_value(data_point_id, data_stream.readFloat());
			}
			
			if (this.received_hsid_packet == false) {				
				logger.config("... detected XHSI Plugin Version " + decode_plugin_version(this.xplane_data_repository.get_sim_value(XPlaneSimDataRepository.PLUGIN_VERSION_ID)));
			    logger.fine("... HSID packet contains " + nb_of_data_points + " sim data values");
			}
			
			this.received_hsid_packet = true;			
		} else if (packet_type.equals("FMSE")) {
			if (this.received_fms_packet == false)
				logger.fine("Received first FMSE packet");

			DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));		
			data_stream.skipBytes(4);	// skip the bytes containing the packet type id	

			this.fms.clear();
			
			int nb_of_entries = (int) data_stream.readFloat();
			
			if (this.received_fms_packet == false)
				logger.fine("... FMSE packet contains " + nb_of_entries + " FMS entries");
			
			int active_entry_index = (int) data_stream.readFloat();
			data_stream.skip(4);		// don't evaluate destination entry index. This info is determined by is_active flag"
			
			for (int i=0;i<nb_of_entries;i++) {
				int type = (int) data_stream.readFloat();
				String id = new String(sim_data,((i*24))+20,5).trim();	
				data_stream.skipBytes(8);
				float altitude = data_stream.readFloat();
				float lat = data_stream.readFloat();
				float lon = data_stream.readFloat();
				boolean is_active = (i == active_entry_index);
		
				this.fms.append_entry(new FMSEntry(id, type, lat, lon, altitude, is_active));
			}		
			
			this.received_fms_packet = true;
		}
		
		this.xplane_data_repository.tick_updates();
	}
	
	private String decode_plugin_version(float plugin_version_id) {
		
		if (plugin_version_id == 0.0f) {
			return "1.0 Beta 3 or earlier";
		} else {
			String pv = "" + (int) plugin_version_id;
			String pv_displayed = pv.substring(0,1) + "." + pv.substring(1,2);
			if (pv.substring(2,3).equals("0") == false) {
				pv_displayed += "." + pv.substring(2,3);
			}
			
			if (pv.substring(3,5).equals("00") == false)
				pv_displayed += " Beta " + Integer.valueOf(pv.substring(3,5));
			
			return pv_displayed;		
		}
	}
}
