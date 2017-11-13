/**
* XPlaneWeatherReceiver.java
* 
* Establishes a datagram socket and receives flight simulator data packages
* send X-Plane (pad controller). The received data is forwarded to 
* XPlaneWeatherPacketDecoder. 
* 
* Copyright (C) 2017  Nicolas Carel
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

import java.net.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.*;


import net.sourceforge.xhsi.StoppableThread;
import net.sourceforge.xhsi.XHSIStatus;


public class XPlaneWeatherReceiver extends StoppableThread {

    MulticastSocket datagram_socket;
    byte[] receive_buffer;
    ArrayList<XPlaneDataPacketObserver> reception_observers;
    boolean has_reception;
    boolean sender_known;
    boolean multicast_recv;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    
    public XPlaneWeatherReceiver(int listen_port, boolean multicast, String group_str) throws Exception {
        super();
        // Standard Ethernet MTU is 1524
        this.receive_buffer = new byte[1600];

        this.datagram_socket = new MulticastSocket(listen_port);
        this.datagram_socket.setSoTimeout(1000);
        this.reception_observers = new ArrayList<XPlaneDataPacketObserver>();
        this.keep_running = true;
        this.has_reception = true;
        this.sender_known = false;
        this.multicast_recv = multicast;
        if ( multicast ) {
            logger.config("Joining multicast group " + group_str);
            InetAddress group = InetAddress.getByName(group_str);        	
            this.datagram_socket.joinGroup(group);
        } 
    }

    public DatagramPacket receiveXPlanePacket() throws IOException {
        DatagramPacket packet = new DatagramPacket(receive_buffer, receive_buffer.length);
        datagram_socket.receive(packet);
        logger.finest("Receiving weather from port " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        /*
         * Dont't notify XPlaneUDPSender, weather data can be send by a separate X-Plane instance
         * 
         
        if ( ! sender_known ) {
            // intercept the sender's (X-Plane's) address and port
            InetAddress orig_address = packet.getAddress();
            int orig_port = packet.getPort();
            XPlaneUDPSender.get_instance().setDestination(datagram_socket, orig_address, orig_port);
            sender_known = true;
        }
        */
        return packet;
    }
    
    public void run() {
        logger.fine("X-Plane receiver listening weather on port " + datagram_socket.getLocalPort());
        DatagramPacket packet = null;
        while (this.keep_running) {
            try {
                // wait for packet or time-out
                packet = receiveXPlanePacket();

                XHSIStatus.weather_receiving = true;
                
                if  (this.has_reception == false) {
                    this.has_reception = true;
                    logger.info("Weather UDP reception re-established");
                }

                // this must be some sort of subscription mechanism...
                // Todo: XPlaneWeatherPacketObserver ?? 
                for (int i=0; i<this.reception_observers.size(); i++) {               	
                    ((XPlaneDataPacketObserver)this.reception_observers.get(i)).new_sim_data(packet.getData(), packet.getLength());
                }
            } catch (SocketTimeoutException ste) {
                XHSIStatus.weather_receiving = false;
                
                if (this.has_reception == true) {
                    logger.warning("No weather UDP reception");
                    this.has_reception = false;
                }
            } catch(IOException ioe) {
                logger.warning("Caught I/O error while waiting for weather UDP packets! (" + ioe.toString() + ")");
            } catch(Exception e) {
                logger.warning("Caught error while waiting for weather UDP packets! (" + e.toString() + " / " + e.getMessage() + ")");
            }
        }
        logger.fine("X-Plane receiver stopped");
    }
    
    public void add_reception_observer(XPlaneDataPacketObserver observer) {
        this.reception_observers.add(observer);
    }


}
