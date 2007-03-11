/**
* XPlaneUDPReceiver.java
* 
* Establishes a datagram socket and receives flight simulator data packages
* send by the XHSI plugin for X-Plane. The received data is forwarded to 
* XPlaneDataPacketDecoder. 
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

import java.net.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.*;

import de.georg_gruetter.xhsi.StoppableThread;

public class XPlaneUDPReceiver extends StoppableThread {
	
	DatagramSocket datagram_socket;
	byte[] receive_buffer;
	ArrayList reception_observers;
	boolean has_reception;

	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	public XPlaneUDPReceiver(int port) throws Exception {
		super();
		this.receive_buffer = new byte[5000];
		this.datagram_socket = new DatagramSocket(port);
		this.datagram_socket.setSoTimeout(1000);
		this.reception_observers = new ArrayList();
		this.keep_running = true;
		this.has_reception = true;
	}
	
	public void add_reception_observer(XPlaneDataPacketObserver observer) {
		this.reception_observers.add(observer);
	}
	
	public DatagramPacket receive() throws IOException { 	
		DatagramPacket packet = new DatagramPacket(receive_buffer, receive_buffer.length);
		datagram_socket.receive(packet);
		return packet;
	}
	
	public void run() {
		logger.fine("X-Plane receiver listening on port " + datagram_socket.getLocalPort());
		DatagramPacket packet = null;
		while (this.keep_running) {
			try {
				packet = receive();
				
				if  (this.has_reception == false) {
					this.has_reception = true;
					logger.info("UDP reception reestablished");
				}
				
				for (int i=0;i<this.reception_observers.size();i++) {
					((XPlaneDataPacketObserver)this.reception_observers.get(i)).new_sim_data(packet.getData());
				}
			} catch (SocketTimeoutException ste) {
				if (this.has_reception == true) {
					logger.info("No UDP reception");
					this.has_reception = false;
				}	
			} catch(Exception e) {
				logger.warning("Caught error while waiting for UDP packets! (" + e.toString() + ")");
			}
		}
		logger.fine("X-Plane receiver stopped");
	}
}