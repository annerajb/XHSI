/**
* UdmcData.java
* 
* Manages and provides access to the UFMC/x737FMC by Javier Cortes
* 
* 
* Copyright (C) 2017 Nicolas Carel
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

public class UfmcData {
	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	private static UfmcData instance = null;
	
	public boolean updated = true;
	
	List ufmcLines = new ArrayList();
	
	public static UfmcData getInstance(){
		if(instance == null){
			instance = new UfmcData(); 
		}
		return instance; 
	}
	
	public UfmcData(){
		ufmcLines = new ArrayList();
		
		for(int i=0; i < 15; i++){
			ufmcLines.add(new HashMap());
		}
		
        Map m = (Map) ufmcLines.get(14);
        m.put("decode", 1);
	}

	
	public String getLine(int i){
		Map m = (Map) ufmcLines.get(i);
		if(m != null) {
			String s = (String) m.get("content");
			if(s != null) return s;
		}
		return "";
	}	

	public void setLine(int i, String s){
		Map m = (Map) ufmcLines.get(i);
		if(m == null) {
			m = new HashMap();
			updated=true;
			m.put("content", s);
		} else {
			String cs = (String) m.get("content");
			if ( ! s.equals(cs)) {
				m.put("content", s);
				updated=true;
			}
		}
	}

	public static List decodeLine(String ln) {
		List w = new ArrayList(); 
		int p1 = ln.indexOf("/");
		if(p1>-1){
			ln = ln.substring(p1+1, ln.length()); 
		}
		String[] pts = ln.split(";");
		for(String s1 : pts){
			if(s1.length()>0){ 
				String[] pts2 = s1.split(",");
				if(pts2.length > 3) {
					String s2 = pts2[2];
					for(int i=3; i < pts2.length; i++){
						s2 += "," + pts2[i];
					}
					pts2[2] = s2;
				}
				try{
					w.add(new Object[]{Integer.parseInt(pts2[0]), Integer.parseInt(pts2[1]), pts2[2], false});
				}catch (Exception e){
					//System.out.println("decodeLine ERROR: >" + s1 + "< " + pts.length );
				}
			}
		}
		return w;
	}
	
}