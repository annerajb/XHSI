/**
* QpacEwdData.java
* 
* This is the Airbus A320 Qpac Engine Warning Display data class
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2015  Nicolas Carel
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

package net.sourceforge.xhsi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class QpacEwdData {
	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	private static QpacEwdData instance = null;
	
	static List qpacEwdLines = new ArrayList();
	
	public static QpacEwdData getInstance(){
		if(instance == null){
			instance = new QpacEwdData(); 
		}
		return instance; 
	}
	
	public QpacEwdData(){
		qpacEwdLines = new ArrayList();
		
		for(int i=0; i < 15; i++){
			qpacEwdLines.add(new HashMap());
		}
		
        Map m = (Map) qpacEwdLines.get(14);
        m.put("decode", 1);
	}

	
	public static String getLine(int i){
		Map m = (Map) qpacEwdLines.get(i);
		if(m != null) {
			String s = (String) m.get("content");
			if(s != null) return s;
		}
		return "";
	}	

	public void setLine(int i, String s){
		Map m = (Map) qpacEwdLines.get(i);
		if(m == null) m = new HashMap();
		m.put("content", s);
	}

	public static List<CduLine> decodeLine(String ln) {
		List<CduLine> w = new ArrayList<CduLine>(); 
		CduLine ql;
		String[] pts = ln.split(";");
		for(String s1 : pts){
			if(s1.length()>0){
				try{
					ql = new CduLine('l', s1.charAt(0), Integer.parseInt(s1.substring(1, 3)), s1.substring(3));
					w.add(ql);					
				}catch (Exception e){
					//System.out.println("decodeLine ERROR: >" + s1 + "< " + pts.length );
				}
			}
		}
		return w;
	}
	
}