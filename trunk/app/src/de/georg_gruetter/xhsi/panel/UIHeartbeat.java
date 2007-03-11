/**
* UIHeartbeat.java
* 
* Periodically calls the hearbeat method of HSIComponent to trigger periodic
* tasks.
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
package de.georg_gruetter.xhsi.panel;

import java.util.logging.Logger;

import de.georg_gruetter.xhsi.StoppableThread;

public class UIHeartbeat extends StoppableThread {
	
	private int watch_interval;
	private HSIComponent hsi;
	
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	/**
	 * 
	 * @param watch_interval	time in ms between invocations of this watchdog
	 */
	public UIHeartbeat(HSIComponent hsi, int watch_interval) {
		this.watch_interval = watch_interval;
		this.hsi = hsi;
		this.keep_running = true;
	}
	
	public void run() {
		while (this.keep_running) {
			try {
				Thread.sleep(this.watch_interval);
				this.hsi.heartbeat();
			} catch (Exception e) {
				this.keep_running = false;
				logger.warning("Caught exception in SimDataWatchdog! Stopping. (" + e.toString());
			}
		}
		logger.fine("UI-Heartbeat stopped");
	}
}
