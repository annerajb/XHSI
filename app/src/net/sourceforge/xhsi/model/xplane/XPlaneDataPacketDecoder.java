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
* Copyright (C) 2009-2010  Marc Rogiers (marrog.123@gmail.com)
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Logger;

//import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.CoordinateSystem;
//import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.FMS;
import net.sourceforge.xhsi.model.FMSEntry;
import net.sourceforge.xhsi.model.TCAS;


public class XPlaneDataPacketDecoder implements XPlaneDataPacketObserver {

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private boolean received_adc_packet = false;
    private boolean received_fms_packet = false;
    private boolean received_tcas_packet = false;

    // list of sim data id's that need the anti-jitter filter
    private int[] jitter_id = { XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_MAGPSI,
        XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_HPATH,
        XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE,
        XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE };
    // ... and the same number of vars to store previous value and delta
    private float[] last_value = { 0.0f, 0.0f, 0.0f, 0.0f };
    private float[] last_delta = { 0.0f, 0.0f, 0.0f, 0.0f };

    XPlaneSimDataRepository xplane_data_repository = null;
    FMS fms = FMS.get_instance();
    TCAS tcas = TCAS.get_instance();


    public XPlaneDataPacketDecoder() {
        this.xplane_data_repository = XPlaneSimDataRepository.get_instance();
    }


    private float anti_jitter( int id, float value ) {

        // somewhere between the encoding in the plugin and the decoding here,
        // the latitude, longitude and others make big jumps, causing the map to shake
        // this function tries to filter those out

        float new_value = value;

        int i;
        for ( i=0; i<this.jitter_id.length; i++ ) {

            if ( id == this.jitter_id[i] ) {
                float new_delta = Math.abs(value - this.last_value[i]);
                if (  new_delta > this.last_delta[i] * 5.0f ) {
                    // delta suddenly bigger; keep the old value
                    new_value = this.last_value[i];
                }
                this.last_delta[i] = new_delta;
                this.last_value[i] = value;
            }

        }

        return new_value;

    }


    public void new_sim_data( byte[] sim_data ) throws Exception {

        // these vars will be re-used several times, so define them here and not in a for-loop
        int data_point_id;
        //int int_data;
        float float_data;
        String string_data;

        // identify the packet type (identified by the first four bytes)
        String packet_type = new String(sim_data, 0, 4).trim();

        if ( packet_type.equals("SIMD") ) {

            // Simulator Air Data Computer data packet
            // new packet format with 4-byte integers ( XHSI_plugin 1.0 Beta 10 or later )

            if (this.received_adc_packet == false)
                logger.fine("Received first SIMD packet");
            logger.finest("Receiving SIMD packet");

            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
            data_stream.skipBytes(4);    // skip the bytes containing the packet type id
            int nb_of_data_points = data_stream.readInt();

            for (int i=0; i<nb_of_data_points; i++) {
                data_point_id = data_stream.readInt();
                if ( data_point_id >= 10000 ) {
                    // a string of 4 bytes
                    string_data = new String(sim_data, 8+(i*8)+4, 4).trim();
                    data_stream.skipBytes(4);
                    this.xplane_data_repository.store_sim_string(data_point_id, string_data);
// We will see about integer sim_data values later...
//                } else if ( data_point_id >= 1000 ) {
//                    // Int
//                    int_data = data_stream.readInt();
//                    this.xplane_data_repository.store_sim_int(data_point_id, int_data);
                } else {
                    // Float
                    float_data = anti_jitter(data_point_id, data_stream.readFloat());
                    this.xplane_data_repository.store_sim_float(data_point_id, float_data);
                }
            }

            // logger.warning("" + this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE) + ";" + this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE));

            if (this.received_adc_packet == false) {
                logger.warning("Receiving from XHSI_plugin version " + decode_plugin_version(this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.PLUGIN_VERSION_ID)));
                logger.fine("... SIMD packet contains " + nb_of_data_points + " sim data values");
            }

            this.received_adc_packet = true;

            this.xplane_data_repository.tick_updates();

//        } else if (packet_type.equals("HSID")) {
//
//            // old packet format with floats ( before XHSI_plugin 1.0 Beta 10 )
//
//            if (this.received_adc_packet == false)
//                logger.fine("Received first HSID packet");
//           logger.finest("Receiving HSID packet");
//
//            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
//            data_stream.skipBytes(4);    // skip the bytes containing the packet type id
//            int nb_of_data_points = (int) data_stream.readFloat();
//
//            for (int i=0; i<nb_of_data_points; i++) {
//                data_point_id = (int) data_stream.readFloat();
//                float_data = anti_jitter( data_point_id, data_stream.readFloat() );
//                this.xplane_data_repository.store_sim_float(data_point_id, float_data);
//            }
//
//            // logger.warning("" + this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LATITUDE) + ";" + this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.SIM_FLIGHTMODEL_POSITION_LONGITUDE));
//
//            if (this.received_adc_packet == false) {
//                logger.warning("Receiving from XHSI_plugin version " + decode_plugin_version(this.xplane_data_repository.get_sim_float(XPlaneSimDataRepository.PLUGIN_VERSION_ID)));
//                logger.fine("... HSID packet contains " + nb_of_data_points + " sim data values");
//            }
//
//            this.received_adc_packet = true;
//
//            this.xplane_data_repository.tick_updates();

        } else if (packet_type.equals("FMSR")) {

            // FMS route data packet
            // new packet format with 4-byte integers ( XHSI_plugin 1.0 Beta 10 or later )

            if (this.received_fms_packet == false)
                logger.fine("Received first FMSR packet");
           logger.finest("Receiving FMSR packet");

            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
            data_stream.skipBytes(4);    // skip the bytes containing the packet type id

            this.fms.clear();

            float ete_for_active = data_stream.readFloat(); // min
            float groundspeed = data_stream.readFloat() * 1.9438445f; // kts

            int nb_of_entries = data_stream.readInt();

            if (this.received_fms_packet == false)
                logger.fine("... FMSR packet contains " + nb_of_entries + " FMS entries");

            int displayed_entry_index = data_stream.readInt();
            int active_entry_index = data_stream.readInt();

            boolean beyond_active = false;
            float last_lat = 0;
            float last_lon = 0;
            // define these once, outside the for-loop
            int type;
            float altitude;
            float lat;
            float lon;
            boolean is_displayed;
            boolean is_active;
            float leg_dist;
            float total_ete = 0.0f;

            for (int i=0; i<nb_of_entries; i++) {
                type = data_stream.readInt();
                String id = new String(sim_data, (i*24)+28, 8).trim();
                // ( i * ( type_int + id_char8 + alt_float + lat_float + lon_float) ) + packet_char4 + ete_float + groundspeed_float + nb_int + displayed_int + active_int + type_int
                data_stream.skipBytes(8);
                altitude = data_stream.readFloat();
                lat = data_stream.readFloat();
                lon = data_stream.readFloat();
                // do not append empty entries
                if ((lat != 0.0f) || (lon != 0.0f)) {

                    is_displayed = (i == displayed_entry_index);
                    is_active = (i == active_entry_index);

                    if ( groundspeed > 50.0f ) {
                        if ( beyond_active ) {
                            leg_dist = CoordinateSystem.rough_distance(lat, lon, last_lat, last_lon);
                            total_ete += leg_dist / groundspeed * 60.0f;
                            last_lat = lat;
                            last_lon = lon;
                        }
                        if ( is_active ) {
                            beyond_active = true;
                            total_ete = ete_for_active;
                            last_lat = lat;
                            last_lon = lon;
                        }
                    } else {
                        total_ete = 0.0f;
                    }

                    this.fms.append_entry(new FMSEntry(id, type, lat, lon, altitude, total_ete, is_active, is_displayed));

                }
            }

            this.received_fms_packet = true;

//        } else if (packet_type.equals("FMSE")) {
//
//            // FMS route data packet
//            // old packet format with floats ( before XHSI_plugin 1.0 Beta 10 )
//
//            if (this.received_fms_packet == false)
//                logger.fine("Received first FMSE packet");
//           logger.finest("Receiving FMSE packet");
//
//            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
//            data_stream.skipBytes(4);    // skip the bytes containing the packet type id
//
//            this.fms.clear();
//
//            int nb_of_entries = (int) data_stream.readFloat();
//
//            if (this.received_fms_packet == false)
//                logger.fine("... FMSE packet contains " + nb_of_entries + " FMS entries");
//
//            int displayed_entry_index = (int) data_stream.readFloat();
//            int active_entry_index = (int) data_stream.readFloat();
//
//            // define these once, outside the for-loop
//            int type;
//            float altitude;
//            float lat;
//            float lon;
//            boolean is_displayed;
//            boolean is_active;
//
//            for (int i=0; i<nb_of_entries; i++) {
//                type = (int) data_stream.readFloat();
//                String id = new String(sim_data, (i*24)+20, 8).trim();
//                data_stream.skipBytes(8);
//                altitude = data_stream.readFloat();
//                lat = data_stream.readFloat();
//                lon = data_stream.readFloat();
//                is_displayed = (i == displayed_entry_index);
//                is_active = (i == active_entry_index);
//
//                // do not append empty entries (this is redundant, we should not receive them anymore from the plugin)
//                if ((lat != 0.0f) || (lon != 0.0f))
//                    this.fms.append_entry(new FMSEntry(id, type, lat, lon, altitude, is_active, is_displayed));
//            }
//
//            this.received_fms_packet = true;

//        } else if (packet_type.equals("TCAS")) {
//
//            // TCAS data packet
//
//            if (this.received_tcas_packet == false)
//                logger.fine("Received first TCAS packet");
//            logger.finest("Receiving TCAS packet");
//
//            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
//            data_stream.skipBytes(4);    // skip the bytes containing the packet type id
//
//            // first float contains our own altitude
//            this.tcas.new_data_start( data_stream.readFloat() );
//
//            for (int i=0; i<TCAS.MAX_ENTRIES; i++) {
//                this.tcas.tcas_update(
//                        i,
//                        data_stream.readFloat(), // rel bearing
//                        data_stream.readFloat(), // rel dist
//                        data_stream.readFloat() // rel alt
//                        );
//            }
//
//            this.received_tcas_packet = true;

        } else if (packet_type.equals("MPAC")) {

            // multi-player aircraft data packet

            if (this.received_tcas_packet == false)
                logger.fine("Received first MPAC packet");
            logger.finest("Receiving MPAC packet");

            DataInputStream data_stream = new DataInputStream(new ByteArrayInputStream(sim_data));
            data_stream.skipBytes(4);    // skip the bytes containing the packet type id

            // maximum number of MP planes
            int mp_total = data_stream.readInt();
            // active number of MP planes
            int mp_active = data_stream.readInt();
            //logger.fine("MP total / active: " + mp_total + " / " + mp_active);
            // pfff... active seems always to be equal to total
            
            // precaution
            mp_total = Math.min(mp_total, TCAS.MAX_ENTRIES);
            mp_active = Math.min(mp_active, TCAS.MAX_ENTRIES);

            // then 4 floats with our own radar altitude, lat, lon and msl altitude
            this.tcas.new_data_start( mp_total, mp_active, data_stream.readFloat(), data_stream.readFloat(), data_stream.readFloat(),data_stream.readFloat() );

            if ( mp_total > 1 ) {
                for (int i = 1; i < mp_total; i++) {
                    this.tcas.mp_update(
                            i,
                            data_stream.readFloat(), // lat
                            data_stream.readFloat(), // lon
                            data_stream.readFloat() // msl alt
                            );
                }
            }

            this.received_tcas_packet = true;

        }

        // no, only for sim data packets
        //this.xplane_data_repository.tick_updates();

    }


    private String decode_plugin_version(float plugin_version) {

        logger.config("Plugin version " + plugin_version);

        if (plugin_version == 0.0f) {
            return "1.0 Beta ?";
        } else {
            String pv = "" + (int) plugin_version; // example: 1.0 Beta 8 is "10008"
            String pv_displayed = pv.substring(0, 1) + "." + pv.substring(1, 2); // "major.minor" (example: "1.0")
            if (pv.substring(2, 3).equals("0") == false)
                pv_displayed += "." + pv.substring(2, 3); // "major.minor.bugfix" if bugfix!=0
            if (pv.substring(3, 5).equals("00") == false)
                if (pv.substring(3, 4).equals("9"))
                    pv_displayed += " RC" + Integer.valueOf(pv.substring(4, 5)); // "major.minor[.bugfix] RCx" if xy>=90
                else
                    pv_displayed += " Beta " + Integer.valueOf(pv.substring(3, 5)); // "major.minor[.bugfix] Beta xx" if xx!=00
            return pv_displayed;
        }

    }


}
