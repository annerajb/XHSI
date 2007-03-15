package de.georg_gruetter.xhsi.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class HeadingLabel extends HSISubcomponent {

	AffineTransform original_at = null;
	
	public HeadingLabel(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
	}
		
	public void paint(Graphics2D g2) {
		int y = hsi_gc.border_top + 35;
		int center_x = this.hsi_gc.plane_position_x;
		
		// heading
		float indicated_horizontal_path = aircraft.horizontal_path();		
		float heading = aircraft.heading();	
		
		g2.clearRect(center_x-40,hsi_gc.border_top, 80, y-hsi_gc.border_top);			
		g2.setColor(Color.white);
		g2.setFont(hsi_gc.font_large); 
		String text = "" + (int) Math.round(indicated_horizontal_path);
	    g2.drawString(text , center_x - (hsi_gc.get_text_width(g2, hsi_gc.font_large, text)/2), y-5);
	    
	    // slip deviation tick
	    rotate(g2, heading - indicated_horizontal_path);
	    g2.setColor(Color.WHITE);
	    GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
	    polyline.moveTo (hsi_gc.plane_position_x, hsi_gc.rose_y_offset);
	    polyline.lineTo(hsi_gc.plane_position_x - 10, y);
	    polyline.lineTo(hsi_gc.plane_position_x + 10, y);
	    polyline.lineTo(hsi_gc.plane_position_x, hsi_gc.rose_y_offset);
	    g2.draw(polyline);
	    unrotate(g2);
	    	    
	    // TRK and MAG labels	    
	    g2.setColor(hsi_gc.color_lightgreen);
	    g2.setFont(hsi_gc.font_medium);
		text = "TRK";
	    g2.drawString(text , center_x - 45 - hsi_gc.get_text_width(g2, hsi_gc.font_medium, text), y-12);
		text = "MAG";
	    g2.drawString(text , center_x + 45, y-12);
	    
	    // surrounding box
	    g2.setColor(Color.WHITE);
	    polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
	    polyline.moveTo (center_x - 40, hsi_gc.border_top);
	    polyline.lineTo(center_x - 40, y);
	    polyline.lineTo(center_x + 40, y);
	    polyline.lineTo(center_x + 40, hsi_gc.border_top);
	    g2.draw(polyline);
	    
	    // plane symbol and line to heading box
	    int triangle_width = (int) (20 * hsi_gc.scaling_factor);
	    int triangle_height = (int) (triangle_width * 1.5);
	    polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
	    polyline.moveTo (hsi_gc.plane_position_x , hsi_gc.plane_position_y);
	    polyline.lineTo(hsi_gc.plane_position_x - (triangle_width/2), hsi_gc.plane_position_y + triangle_height);
	    polyline.lineTo(hsi_gc.plane_position_x + (triangle_width/2), hsi_gc.plane_position_y + triangle_height);
	    polyline.lineTo(hsi_gc.plane_position_x , hsi_gc.plane_position_y);
	    g2.draw(polyline);

	    // distance line with map zoom indication
	    int tick_halfwidth = (int) (5 * hsi_gc.scaling_factor); 
	    g2.drawLine(
	    	hsi_gc.plane_position_x, hsi_gc.plane_position_y - (hsi_gc.rose_radius/4) + tick_halfwidth, 
	    	hsi_gc.plane_position_x, y);
	    
	    g2.setFont(hsi_gc.font_medium);
	    String range_text = "" + this.avionics.map_range()/2; 
	    g2.drawString(
	    	range_text, 
	    	hsi_gc.plane_position_x - hsi_gc.get_text_width(g2, hsi_gc.font_medium,range_text) - 4, 
	    	hsi_gc.plane_position_y - (hsi_gc.rose_radius / 2) - (hsi_gc.get_text_height(g2, g2.getFont()) / 2) + 5);
	    		
	}	
	
	private void rotate(Graphics2D g2, double angle) {
		this.original_at = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(
        		Math.toRadians(angle), 
        		hsi_gc.plane_position_x, 
        		hsi_gc.plane_position_y);
        g2.transform(rotate);		
	}
	
	private void unrotate(Graphics2D g2) {
		g2.setTransform(original_at);
	}

}
