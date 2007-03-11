package de.georg_gruetter.xhsi.util;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class XHSILogFormatter extends Formatter {
	
		
	public void Formatter() {
		
	}

	public String format(LogRecord record) {
		return 
		    new String("[" + new Date(record.getMillis()) + "] " + 
//		    		record.getLevel().getName() + " " + 
//		    		record.getClass().getName() + " " + 
		    		record.getMessage() + "\n"
		    		);
	}

}
