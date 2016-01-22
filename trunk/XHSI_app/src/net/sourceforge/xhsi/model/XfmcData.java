package net.sourceforge.xhsi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class XfmcData {
	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	private static XfmcData instance = null;
	
	public boolean updated = true;
	
	List xfmcLines = new ArrayList();
	
	public static XfmcData getInstance(){
		if(instance == null){
			instance = new XfmcData(); 
		}
		return instance; 
	}
	
	public XfmcData(){
		xfmcLines = new ArrayList();
		
		for(int i=0; i < 15; i++){
			xfmcLines.add(new HashMap());
		}
		
        Map m = (Map) xfmcLines.get(14);
        m.put("decode", 1);
	}

	
	public String getLine(int i){
		Map m = (Map) xfmcLines.get(i);
		if(m != null) {
			String s = (String) m.get("content");
			if(s != null) return s;
		}
		return "";
	}	

	public void setLine(int i, String s){
		Map m = (Map) xfmcLines.get(i);
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