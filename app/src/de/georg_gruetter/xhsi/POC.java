package de.georg_gruetter.xhsi;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.georg_gruetter.xhsi.util.XHSILogFormatter;

public class POC {
	
	public static Logger logger = Logger.getLogger("de.georg_gruetter.xhsi");
	
	public static void main(String args[]) throws Exception {
		String pv = "10100";
		String pv_displayed = pv.substring(0,1) + "." + pv.substring(1,2);
		if (pv.substring(2,3).equals("0") == false) {
			pv_displayed += "." + pv.substring(2,3);
		}
		
		if (pv.substring(3,5).equals("00") == false)
			pv_displayed += " Beta " + Integer.valueOf(pv.substring(3,5));
		
		System.out.println("Plugin text: '" + pv_displayed + "'");

	}

}
