/**
 * HSI.java
 * 
 * Main class starting and controlls the UI and all threads of the horizontal 
 * situation indicator display.
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
package de.georg_gruetter.xhsi;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import de.georg_gruetter.xhsi.model.xplane.XPlaneDataPacketDecoder;
import de.georg_gruetter.xhsi.model.xplane.XPlaneFlightSessionPlayer;
import de.georg_gruetter.xhsi.model.xplane.XPlaneFlightSessionRecorder;
import de.georg_gruetter.xhsi.model.xplane.XPlaneModelFactory;
import de.georg_gruetter.xhsi.model.xplane.XPlaneNavigationObjectBuilder;
import de.georg_gruetter.xhsi.model.xplane.XPlaneSimDataRepository;
import de.georg_gruetter.xhsi.model.xplane.XPlaneUDPReceiver;
import de.georg_gruetter.xhsi.panel.HSIComponent;
import de.georg_gruetter.xhsi.panel.UIHeartbeat;
import de.georg_gruetter.xhsi.util.XHSILogFormatter;

public class HSI implements ActionListener {
	
	private static final String RELEASE = "1.0 Beta 6";
	
	private static final String MODE_REPLAY = "replay";
	private static final String MODE_RECEIVE = "receive";
	
	public static final String ACTION_QUIT  = "Quit";
	public static final String ACTION_PREFERENCES = "Preferences";
	public static final String ACTION_ABOUT = "About XHSI";
	
	private HSIPreferences preferences;
	private ArrayList running_threads;
	private HSIComponent hsi_ui;
	private JFrame hsi_frame;
	private JFrame preferences_frame;
	private JFrame nob_progress_frame;
	
	private static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	public static void main(String args[]) throws Exception {
		
		Handler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		handler.setFormatter(new XHSILogFormatter());
		handler.setFilter(null);		
		logger.addHandler(handler);
		
		handler = new FileHandler("XHSI.log");
		handler.setLevel(Level.ALL);
		handler.setFormatter(new XHSILogFormatter());
		handler.setFilter(null);		
		logger.addHandler(handler);
		
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		
		logger.config("XHSI " + HSI.RELEASE + " started");
		
		HSIStatus.status = HSIStatus.STATUS_STARTUP;
		
        if ((args.length >= 2) && (args[0].equals("--record"))) {
			logger.fine("recording flight session to '" + args[1] + "' ...");
			int recording_rate = 1;
			if (args.length == 3)
				recording_rate = Integer.parseInt(args[2]);			
			XPlaneFlightSessionRecorder recorder = new XPlaneFlightSessionRecorder(args[1],recording_rate);
			XPlaneUDPReceiver udp_receiver = new XPlaneUDPReceiver(49001);
			udp_receiver.add_reception_observer(recorder);
			recorder.start();			
			udp_receiver.start();
		} else if ((args.length == 2) && (args[0].equals("--replay"))) {
				new HSI(MODE_REPLAY, args[1]);
		} else if ((args.length == 1) && (args[0].equals("--help"))) {
			display_usage_info();
		} else if ((args.length == 1) && (args[0].equals("--version"))) {
			System.out.println("Version of XHSI is " + HSI.RELEASE);
		} else if (args.length == 0) {
			new HSI(MODE_RECEIVE);
		} else {
			display_usage_info();
		}

	}
	
	public static void display_usage_info() {
		System.out.println(
		"Usage: java -jar XHSI.jar [--options]\n\n" +
		"where options include:\n" +
		"   --record <filename> [<frame_rate>] to record the current datastream\n" +
		"                                      received from X-Plane in the file\n" +
		"                                      <filename>. If <frame_rate>\n" +
		"                                      is given, records every <frame_rate>'th\n" +
		"                                      received data frame to save space.\n" +
		"   --replay <filename>                to replay the recording stored\n" +
		"                                      in <filename>\n" +
		"   --version                          to display the version of XHSI\n" +
		"   --help                             to display this help\n"
		);
	}
	
	public HSI(String mode, String filename) throws Exception {
		
		init();
		
		if (mode.equals(MODE_REPLAY)) {
			logger.fine("playing flight session recording from '" + filename + "' ...");
			XPlaneFlightSessionPlayer player = new XPlaneFlightSessionPlayer(filename,Long.parseLong(this.preferences.get_preference(HSIPreferences.PREF_REPLAY_DELAY_PER_FRAME)));
			XPlaneDataPacketDecoder decoder = new XPlaneDataPacketDecoder();
			player.add_sim_data_observer(decoder);
			this.running_threads.add(player);

			XPlaneSimDataRepository.source_is_recording = true;
			HSIStatus.status = HSIStatus.STATUS_PLAYING_RECORDING;
			player.start();	        	
        }
	}
	
	public HSI(String mode) throws Exception {
        
		init();
		
        if (mode.equals(MODE_RECEIVE)) {
			XPlaneUDPReceiver udp_receiver = new XPlaneUDPReceiver(Integer.parseInt(preferences.get_preference(HSIPreferences.PREF_PORT)));
			XPlaneDataPacketDecoder decoder = new XPlaneDataPacketDecoder();
			udp_receiver.add_reception_observer(decoder);
			XPlaneSimDataRepository.source_is_recording = false;			
			this.running_threads.add(udp_receiver);
			HSIStatus.status = HSIStatus.STATUS_RECEIVING;			
			udp_receiver.start();        	
        } 
	}
	
	private void init() throws Exception {
		this.running_threads = new ArrayList();
		
		// load properties and create a new properties file, if none exists
		this.preferences = HSIPreferences.get_instance();
		
		// set loglevel
		logger.config("Selected loglevel: " + this.preferences.get_preference(HSIPreferences.PREF_LOGLEVEL));
		logger.setLevel(Level.parse(this.preferences.get_preference(HSIPreferences.PREF_LOGLEVEL)));

        // create user interface
        create_UI();	
				
		// load X-Plane Earth nav databases
		XPlaneNavigationObjectBuilder nob = new XPlaneNavigationObjectBuilder(this.preferences.get_preference(HSIPreferences.PREF_XPLANE_DIR));
		HSIPreferences.get_instance().add_subsciption(nob, HSIPreferences.PREF_XPLANE_DIR);
		nob.set_progress_observer((ProgressObserver) this.nob_progress_frame);
		if (HSIStatus.nav_db_status.equals(HSIStatus.STATUS_NAV_DB_NOT_FOUND) == false) {
			nob.read_all_tables();
			HSIStatus.nav_db_status = HSIStatus.STATUS_NAV_DB_LOADED;
		}
        
        // add ui update watchdog
        UIHeartbeat ui_heartbeat = new UIHeartbeat(this.hsi_ui, 1000);
        ui_heartbeat.start();
        this.running_threads.add(ui_heartbeat);
        
        
	}
	
	private void shutdown_threads() {
		StoppableThread thread;
		
		HSIStatus.status = HSIStatus.STATUS_SHUTDOWN;
		
		for (int i=0;i<this.running_threads.size();i++) {
			thread = (StoppableThread) this.running_threads.get(i);
			thread.signal_stop();
			try {
				thread.join(1000);
			} catch (Exception e) {
				logger.warning("Could not shutdown thread. (" + e.toString());
			}
		}
	}
		
	private boolean isMac() {
		return (System.getProperty("mrj.version") != null);
	}
	
	private void create_UI() throws Exception {
		
		boolean ui_specialization = true;
				
        if (isMac()) {
            logger.config("Mac detected. Create Menubar with Mac look and feel");
            
        	System.setProperty("apple.laf.useScreenMenuBar", "true");
        	System.setProperty("com.apple.mrj.application.apple.menu.about.name","XHSI");
        	
        	// try to load apple specific classes dynamically in order to avoid
        	// compilation problems on non-mac platforms
        	try {
        		Class Application = Class.forName("com.apple.eawt.Application");
        		Class ApplicationListener = Class.forName("com.apple.eawt.ApplicationListener");
        		Class ApplicationEvent = Class.forName("com.apple.eawt.ApplicationEvent");
        		
        		Method getApplication = Application.getMethod("getApplication", new Class[0]);
        		Method addApplicationListener = Application.getMethod("addApplicationListener", new Class[] { ApplicationListener });
        		final Method setHandled = ApplicationEvent.getMethod("setHandled", new Class[] { Boolean.TYPE });
        		Method setEnabledPreferencesMenu = Application.getMethod("setEnabledPreferencesMenu", new Class[] { Boolean.TYPE });
        		
        		InvocationHandler listenerHandler = new InvocationHandler() {
        			public Object invoke(Object proxy, Method method, Object[] args) {
        				String name = method.getName();
        				if (name.equals("handleAbout")) {
        					actionPerformed(new ActionEvent(this,0, HSI.ACTION_ABOUT));
        				} else if (name.equals("handlePreferences")) {
        					actionPerformed(new ActionEvent(this,0, HSI.ACTION_PREFERENCES));
        				} else if (name.equals("handleQuit")) {
        					actionPerformed(new ActionEvent(this,0, HSI.ACTION_QUIT));
        				} else {
        					return null;
        				}
        				
        				try {
        					setHandled.invoke(args[0], new Object[] { Boolean.TRUE });
        				} catch (Exception ex) {
        					// Ignore
        				}
        				return null;
        			}
        		};
        		
        		Object application = getApplication.invoke(null, (Object[]) null);
        		setEnabledPreferencesMenu.invoke(application, new Object[] { Boolean.TRUE });
        		Object listener = Proxy.newProxyInstance(HSI.class.getClassLoader(),
        												  new Class[] { ApplicationListener },
        												  listenerHandler);
        		addApplicationListener.invoke(application, new Object[] { listener });
        		
        	} catch (Exception e) {
        		logger.warning("Could not create Mac specific UI! (" + e.toString() + ")");
        		ui_specialization = false;
        	}
        }
        
        this.hsi_frame = new JFrame("XHSI " + HSI.RELEASE + " on " + InetAddress.getLocalHost());
                
        if ((isMac() == false) || (ui_specialization == false)) {
        	this.hsi_frame.setJMenuBar(createMenu());
        }
        
        this.hsi_ui = new HSIComponent(new XPlaneModelFactory());
        XPlaneSimDataRepository.get_instance().add_observer(hsi_ui); 
        
        this.hsi_frame.getContentPane().add(hsi_ui);
        this.hsi_frame.pack();
        this.hsi_frame.setBackground(Color.BLACK);
        this.hsi_frame.setVisible(true);		
        
        this.preferences_frame = new PreferencesDialog();
        this.nob_progress_frame = new ProgressDialog(this.hsi_frame);
	}
	
	private  JMenuBar createMenu() {
		JMenuBar menu_bar = new JMenuBar();
		JMenu big_hsi_menu = new JMenu("XHSI");
		
		JMenuItem menu_item = new JMenuItem("About XHSI");
		menu_item.addActionListener(this);
		menu_item.setMnemonic(KeyEvent.VK_A);
		big_hsi_menu.add(menu_item);
		
		big_hsi_menu.addSeparator();
		
		menu_item = new JMenuItem("Preferences");
		menu_item.addActionListener(this);
		menu_item.setMnemonic(KeyEvent.VK_S);
		big_hsi_menu.add(menu_item);
		
		big_hsi_menu.addSeparator();

		menu_item = new JMenuItem("Quit");
		menu_item.setMnemonic(KeyEvent.VK_Q);
		menu_item.addActionListener(this);
		big_hsi_menu.add(menu_item);
				
		menu_bar.add(big_hsi_menu);
		
		return menu_bar;
	}
	

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals(ACTION_QUIT)) {
			logger.fine("stopping threads");
			shutdown_threads();
			logger.fine("clean exit from threads");
			System.exit(0);
		} else if (event.getActionCommand().equals(ACTION_PREFERENCES)) {
			// choose X-Plane directory
			this.preferences_frame.setVisible(true);
			this.preferences_frame.pack();
		} else if (event.getActionCommand().equals(ACTION_ABOUT)) {
			JOptionPane.showMessageDialog(this.hsi_frame,
				    "XHSI " + HSI.RELEASE + 
				    "\nby Georg Gruetter in 2007" +
				    "\n\ngruetter@users.sourceforge.net" +
				    "\nhttp://www.g16g.de/x-plane\n\n" +
				    "Special thanks for beta testing to\n" +
				    "Ansorg, Brandon, Elvis, Mark Steele,\n" +
				    "Mueli and Schleich!",
				    "About XHSI",
				    JOptionPane.INFORMATION_MESSAGE);		  
		}
	}
}
