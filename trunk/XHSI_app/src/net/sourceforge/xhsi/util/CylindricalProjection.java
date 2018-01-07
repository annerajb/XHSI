package net.sourceforge.xhsi.util;

public class CylindricalProjection implements Projection {

   
    private float c_lat;
    private float c_lon;
 
    private int map_x;
    private int map_y;
	
    private float pixels_per_nm;
    private int map_center_x;
    private int map_center_y;
    
	public CylindricalProjection() {
	}

	
    public void setScale(float ppnm) {
    	pixels_per_nm = ppnm;
    }
    
    public void setCenter(int x, int y) {
    	map_center_x = x;
    	map_center_y = y;
    }
    

	public void setAcf(float acf_lat, float acf_lon) {
        // interesting, but not used...
        c_lat = acf_lat;
        c_lon = acf_lon;
	}

	public void setPoint(float lat, float lon) {
		// TODO: maths !!!
        // map_x = cylindrical_lon_to_x(lon);
        // map_y = cylindrical_lat_to_y(lat);
		map_x=0;
		map_y=0;
	}

	public int getX() {
		return map_x;
	}

	public int getY() {
		return map_y;
	}

}

