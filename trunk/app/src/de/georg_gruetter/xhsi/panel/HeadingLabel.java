package de.georg_gruetter.xhsi.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.awt.Component;

import de.georg_gruetter.xhsi.model.ModelFactory;

public class HeadingLabel extends HSISubcomponent {

	private static final long serialVersionUID = 1L;
	AffineTransform original_at = null;
	int old_hdg_text_length = 0;
	float old_hdg_text_width = 0.0f;
	BufferedImage hdg_label_decoration_buf_img;
	
	public HeadingLabel(ModelFactory model_factory, HSIGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		this.hdg_label_decoration_buf_img = null;
	}
		
	public void paint(Graphics2D g2) {
		int y = hsi_gc.border_top + hsi_gc.line_height_large;
		int center_x = this.hsi_gc.plane_position_x;
	    int triangle_width = (int) (20 * hsi_gc.scaling_factor);
	    int triangle_height = (int) (triangle_width * 1.5);
	    
	    int slip_deviation_tick_height = (int) Math.min(14,16 * hsi_gc.scaling_factor);
		int x_points_slip_deviation_tick[] = { center_x, center_x-10, center_x+10 };
		int y_point_slip_deviation_tick[] = { hsi_gc.rose_y_offset, hsi_gc.rose_y_offset - slip_deviation_tick_height, hsi_gc.rose_y_offset - slip_deviation_tick_height };

		int x_points_airplane_symbol[] = { center_x, center_x - (triangle_width/2), center_x + (triangle_width/2) };
	    int y_points_airplane_symbol[] = { hsi_gc.plane_position_y, hsi_gc.plane_position_y + triangle_height, hsi_gc.plane_position_y + triangle_height };
	    
	    // heading
		float indicated_horizontal_path = aircraft.horizontal_path();		
		float heading = aircraft.heading();	
		
		// create heading information decoration
		if (this.hdg_label_decoration_buf_img == null) {
			this.hdg_label_decoration_buf_img = create_buffered_image(150, 40);
			Graphics2D gImg = get_graphics(this.hdg_label_decoration_buf_img);
			render_heading_decoration(gImg, 150);
		}
		
		g2.drawImage(this.hdg_label_decoration_buf_img, 
				     hsi_gc.plane_position_x - (this.hdg_label_decoration_buf_img.getWidth()/2),
				     hsi_gc.border_top,
				     null);
		
		g2.clearRect(center_x-34,hsi_gc.border_top, 68, y-hsi_gc.border_top);			
		g2.setColor(Color.white);
		g2.setFont(hsi_gc.font_large); 
		String text = "" + (int) Math.round(indicated_horizontal_path);
		
		if (text.length() != old_hdg_text_length) {
			old_hdg_text_width =  hsi_gc.get_text_width(g2, hsi_gc.font_large, text)/2;	
			old_hdg_text_length = text.length();
		}
	    g2.drawString(text , center_x - old_hdg_text_width, y-3);
	    
	    // slip deviation tick
	    rotate(g2, heading - indicated_horizontal_path);
	    g2.setColor(Color.WHITE);
	    g2.drawPolygon(x_points_slip_deviation_tick, y_point_slip_deviation_tick,3);
	    unrotate(g2);
	  	    
	    // plane symbol and line to heading box
	    g2.drawPolygon(x_points_airplane_symbol, y_points_airplane_symbol, 3);

	    // distance line with map zoom indication
	    int tick_halfwidth = (int) (5 * hsi_gc.scaling_factor); 
	    g2.drawLine(
	    	hsi_gc.plane_position_x, hsi_gc.plane_position_y - (hsi_gc.rose_radius/4) + tick_halfwidth, 
	    	hsi_gc.plane_position_x, y+7);
	    
	    g2.setFont(hsi_gc.font_medium);
	    String range_text = "" + this.avionics.map_range()/2; 
	    g2.drawString(
	    	range_text, 
	    	hsi_gc.plane_position_x - hsi_gc.get_text_width(g2, hsi_gc.font_medium,range_text) - 4, 
	    	hsi_gc.plane_position_y - (hsi_gc.rose_radius / 2) - (hsi_gc.get_text_height(g2, g2.getFont()) / 2) + 5);	    		
	}	
	
	private void render_heading_decoration(Graphics2D g2, int width) {
		int x_points_heading_box[] = { (width/2)-35, (width/2)-35, (width/2)+35, (width/2)+35};
		int y_points_heading_box[] = { 0, 33, 33, 0 };
		
	    // TRK and MAG labels	    
	    g2.setColor(hsi_gc.color_lightgreen);
	    g2.setFont(hsi_gc.font_medium);
	    g2.drawString(new String("TRK") , (width/2) - 40 - hsi_gc.get_text_width(g2, hsi_gc.font_medium, "TRK"), hsi_gc.line_height_medium);
	    g2.drawString("MAG" , (width/2) + 40, hsi_gc.line_height_medium);
		
		// surrounding box
	    g2.setColor(Color.WHITE);
	    g2.drawPolyline(x_points_heading_box, y_points_heading_box, 4);
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
