/**
* QpacMcduData.java
* 
* This is the Airbus A320 Qpac MCDU data class
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



public class QpacMcduData {
	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	private static QpacMcduData instance = null;
	
	public boolean updated = false;
	
	static List qpacMcdu1Lines = new ArrayList();
	static List qpacMcdu2Lines = new ArrayList();
	
	public static QpacMcduData getInstance(){
		if(instance == null){
			instance = new QpacMcduData(); 
		}
		return instance; 
	}
	
	public QpacMcduData(){
		qpacMcdu1Lines = new ArrayList();
		qpacMcdu2Lines = new ArrayList();
		
		for(int i=0; i < 15; i++){
			qpacMcdu1Lines.add(new HashMap());
			qpacMcdu2Lines.add(new HashMap());
		}
		
        Map m1 = (Map) qpacMcdu1Lines.get(14);
        Map m2 = (Map) qpacMcdu2Lines.get(14);

        m1.put("decode", 1);
        m2.put("decode", 1);

	}

	
	public static String getLine(int mcdu_id, int i){
		Map m = (mcdu_id==0) ? (Map) qpacMcdu1Lines.get(i) : (Map) qpacMcdu2Lines.get(i);
		if(m != null) {
			String s = (String) m.get("content");
			if(s != null) return s;
		}
		return "";
	}	

	public void setLine(int mcdu_id, int i, String s){
		Map m = (mcdu_id==0) ? (Map) qpacMcdu1Lines.get(i) : (Map) qpacMcdu2Lines.get(i);
		if(m == null) { 
			m = new HashMap();
			m.put("content", s);
			updated = true;
		} else {
			String cs = (String) m.get("content");
			if ( ! s.equals(cs)) {
				m.put("content", s);
				updated = true;				
			}
		}
	}


	/*
	 * LINE COMPRESSION PROTOCOL FOR QPAC MESSAGES
	 *
	 * Compressed output format:
	 * -------------------------
	 * f,c,pp,”text1 “;f,c,pp,”text2”;f,c,pp,”text3”; etc etc
	 * f : (1 char) font s=small, l=large
	 * c : (1 char) color r=red, b=blue, m=magenta, y=yellow, g=green, a=amber, w=white
	 * pp : (2 char) column position of embedded string
	 * text : string to be displayed
	 *
	 * The asterix char (*) has been translated from 176(d) to 30(d).
	 * The box char [] has been translated to 31 (d)
	 */
	
	public static List<CduLine> decodeLine(String ln) {
		List<CduLine> w = new ArrayList<CduLine>(); 
		CduLine ql;
		String[] pts = ln.split(";");
		for(String s1 : pts){
			if(s1.length()>0){
				try{
					ql = new CduLine(s1.charAt(0), s1.charAt(1), Integer.parseInt(s1.substring(2, 4)), s1.substring(4));
					w.add(ql);					
				}catch (Exception e){
					//System.out.println("decodeLine ERROR: >" + s1 + "< " + pts.length );
				}
			}
		}
		return w;
	}
	
}