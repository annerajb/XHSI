package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import net.sourceforge.xhsi.model.ModelFactory;

public class ECAM_Flaps extends EICASSubcomponent {

    private Stroke original_stroke;

	public ECAM_Flaps(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		// TODO Auto-generated constructor stub
	}


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && eicas_gc.airbus_style  
        	 && this.preferences.get_eicas_primary_only() && ! this.preferences.get_eicas_draw_controls() ) {        	
        	if (eicas_gc.ecam_version ==1) { 
        		draw_flaps_slats_v1(g2); 
        	} else {
        		draw_flaps_slats_v2(g2); 
        	}
        	// draw_speed_brakes(g2);     		
        }
    }
	
    private void draw_flaps_slats_v1 (Graphics2D g2) {
        
        float flaps = this.aircraft.get_flap_position();
        float slats = this.aircraft.get_slat_position();
        float flapshandle = this.aircraft.get_flap_handle();
        int detents = this.aircraft.get_flap_detents();
        
        int flaps_w = eicas_gc.ecam_flaps_w;
        int flaps_h = eicas_gc.ecam_flaps_h;
        
        int slats_center_x = eicas_gc.ecam_slats_center_x;
        int slats_center_y = eicas_gc.ecam_slats_center_y;
        int slats_w = eicas_gc.ecam_slats_w;
        int slats_h = eicas_gc.ecam_slats_h;
                
        
        int[] flaps_triangle_x = {
        		eicas_gc.ecam_flaps_center_x - eicas_gc.ecam_flaps_box_w*13/100,
        		eicas_gc.ecam_flaps_center_x - eicas_gc.ecam_flaps_box_w*10/100,
        		eicas_gc.ecam_flaps_center_x
        };
        int[] flaps_triangle_y = {
        		eicas_gc.ecam_flaps_center_y,
        		eicas_gc.ecam_flaps_center_y - eicas_gc.ecam_flaps_box_h*10/100,
        		eicas_gc.ecam_flaps_center_y
        };
        
        int[] slats_triangle_x = {
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*33/100,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*30/100,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*30/100 - eicas_gc.ecam_flaps_box_w*7/100
        };
        int[] slats_triangle_y = {
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*38/100,
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*28/100,
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*38/100
        };
        
        AffineTransform original_at = g2.getTransform();
        // debug - draw the box
        // g2.drawRect(eicas_gc.ecam_flaps_box_x, eicas_gc.ecam_flaps_box_y, eicas_gc.ecam_flaps_box_w, eicas_gc.ecam_flaps_box_h);
        
        
        // wing 
        g2.setColor(eicas_gc.ecam_markings_color);

        int[] wing_section_x = {
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*30/100,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*46/100,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*43/100,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*33/100
        };
        int[] wing_section_y = {
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*28/100,
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*28/100,
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*38/100,
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*38/100
        };
        g2.fillPolygon(wing_section_x, wing_section_y, 4);
        // g2.drawPolygon(wing_section_x, wing_section_y, 4);
        	
        scalePen(g2,1.2f);
        
        // Flaps
        if ( flaps > 0.05f ) {

        	// flaps text
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.setFont(eicas_gc.font_l);     
        	g2.drawString("S", eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*10/100, eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_s*4/3);
        	g2.drawString("F", eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*70/100, eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_s*4/3);
        	String flaps_str="FLAP";
        	if (flapshandle != flaps) { g2.setColor(eicas_gc.ecam_action_color); }
        	g2.drawString(flaps_str, eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*40/100- eicas_gc.get_text_width(g2, eicas_gc.font_l, flaps_str)/2, eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_s*4/3);
        	
        	
        	// flaps bullets
        	g2.setColor(eicas_gc.ecam_markings_color);
        	/*
        	g2.drawArc(
        			flaps_center_x - flaps_w, 
        			flaps_center_y - flaps_h,
        			flaps_w*2 + 2,
        			flaps_h*2 + 2, 0, 80);
        	*/

        	for ( int i=1; i<=detents; i++) {
    			g2.fillOval(
    					eicas_gc.ecam_flaps_bullet[i].x,
    					eicas_gc.ecam_flaps_bullet[i].y,    					
    					eicas_gc.ecam_bullet_r,
    					eicas_gc.ecam_bullet_r
    					);
        	}

        	// flaps handle
        	if (flapshandle != flaps) {
        		double flaps_angle = Math.toRadians(80*flapshandle);
        		double flaps_angle_t = Math.toRadians(40*flapshandle);
        		double tx = flaps_w * Math.sin(flaps_angle); 
        	    double ty = -flaps_h * Math.cos(flaps_angle);
        		g2.setColor(eicas_gc.ecam_action_color);
                g2.rotate(flaps_angle_t, eicas_gc.ecam_flaps_center_x+tx, eicas_gc.ecam_flaps_center_y+ty);
                g2.translate(tx, ty);
                g2.drawPolygon(flaps_triangle_x, flaps_triangle_y, 3);
        		// g2.rotate(Math.toRadians(60*flapshandle), eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*46/100, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*28/100);
        		// g2.drawLine(eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*46/100 , eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*28/100 - 1, eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l, eicas_gc.wing_y + eicas_gc.wing_h/2);
        		g2.setTransform(original_at);
        	} else {
        		// Draw flap position in green
        		g2.setColor(eicas_gc.ecam_normal_color);
        	}
        	
        	if (flapshandle==1) { 
        		flaps_str="FULL";
        	} else if ( Math.round(flapshandle*detents) == 1 && slats > 0.01) {
        		// Strange behaviour with QPAC airbus
        		flaps_str = "1+F";
        	} else {
        		flaps_str = ""+Math.round(flapshandle*detents);
        	}
        	g2.drawString(flaps_str, eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*38/100 - eicas_gc.get_text_width(g2, eicas_gc.font_l, flaps_str)/2,
        			eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_l*4);
        }
        
        // Flaps position
        g2.setColor(eicas_gc.ecam_normal_color);
        double flaps_angle = Math.toRadians(80*flaps);
        double flaps_angle_t = Math.toRadians(40*flaps);
		int tx = (int) Math.round(flaps_w * Math.sin(flaps_angle)); 
	    int ty = (int) Math.round(-flaps_h * Math.cos(flaps_angle));
	    int dy = (int) Math.round(-flaps_h * Math.cos(flaps_angle) - flaps_h*25*Math.sin(flaps_angle)/100);
        g2.rotate(flaps_angle_t, eicas_gc.ecam_flaps_center_x+tx, eicas_gc.ecam_flaps_center_y+ty);
        g2.translate(tx, ty);
        g2.drawPolygon(flaps_triangle_x, flaps_triangle_y, 3);
        g2.setTransform(original_at);
        g2.drawLine(eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*43/100, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*38/100 - 1 ,
    			eicas_gc.ecam_flaps_center_x - eicas_gc.ecam_flaps_box_w*12/100 + tx, eicas_gc.ecam_flaps_center_y+dy);
           
        
        // Slats 
        if ( slats > 0.05f ) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	// slats arc
       	
        	/* g2.drawArc(
        			slats_center_x - slats_w, 
        			slats_center_y - slats_h,
        			slats_w*2 + 2,
        			slats_h*2 + 2, 90, 90);
        	*/
        	for ( int i=1; i<=detents; i++) {
    			g2.fillOval(
    					eicas_gc.ecam_slats_bullet[i].x,
    					eicas_gc.ecam_slats_bullet[i].y,    					
    					eicas_gc.ecam_bullet_r,
    					eicas_gc.ecam_bullet_r
    					);
        	}
        }
 
        // Slats position
        g2.setColor(eicas_gc.ecam_normal_color);
        double slats_angle = Math.toRadians(80*slats);
        double slats_angle_t = Math.toRadians(40*slats);
		int s_tx = (int) Math.round(slats_w * Math.sin(slats_angle)); 
	    int s_ty = (int) Math.round(-slats_h * Math.cos(slats_angle));
//	    int s_dy = (int) Math.round(-slats_h * Math.cos(slats_angle) + flaps_h*25*Math.sin(slats_angle)/100);
	    int s_dy = (int) Math.round(-slats_h * Math.cos(slats_angle));

        // double s_angle = -Math.toRadians(90*slats-90);
        // g2.translate(eicas_gc.ecam_slats_center_x + slats_w * Math.cos(s_angle), eicas_gc.ecam_slats_center_y + slats_h * Math.sin(s_angle));
        g2.rotate(-Math.toRadians(90*slats), eicas_gc.ecam_slats_center_x, eicas_gc.ecam_slats_center_y);
        g2.drawPolygon(slats_triangle_x, slats_triangle_y, 3);
        g2.setTransform(original_at);
        g2.drawLine(eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*33/100, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*38/100 - 1 ,
    			eicas_gc.ecam_slats_center_x - s_tx, eicas_gc.ecam_slats_center_y+s_dy);

        resetPen(g2);
    }
    
    
    private void draw_flaps_slats_v2 (Graphics2D g2) {
        
        float flaps = this.aircraft.get_flap_position();
        float slats = this.aircraft.get_slat_position();
        float flapshandle = this.aircraft.get_flap_handle();
        int detents = this.aircraft.get_flap_detents();
        
        int flaps_w = eicas_gc.ecam_flaps_box_w*40/100;
        int flaps_h = eicas_gc.ecam_flaps_box_h*62/100;
        
        int slats_center_x = eicas_gc.ecam_slats_center_x;
        int slats_center_y = eicas_gc.ecam_slats_center_y;
        int slats_w = eicas_gc.ecam_flaps_box_w*25/100;
        int slats_h = eicas_gc.ecam_flaps_box_h*50/100;
                
        // flaps_center (x,y) is the bottom right corner
        int[] flaps_lozange_x = {
        		eicas_gc.ecam_flaps_center_x - eicas_gc.ecam_flaps_box_w*78/1000, // -0.6 / 7.7 (upper left corner)
        		eicas_gc.ecam_flaps_center_x, // upper right corner 
        		eicas_gc.ecam_flaps_center_x, // bottom right corner
        		eicas_gc.ecam_flaps_center_x - eicas_gc.ecam_flaps_box_w*52/1000, // bottom left corner // -0.4 / 7,7
        };
        int[] flaps_lozange_y = {
        		eicas_gc.ecam_flaps_center_y - eicas_gc.ecam_flaps_box_h*260/1000, // 0.45 /1.7
        		eicas_gc.ecam_flaps_center_y - eicas_gc.ecam_flaps_box_h*176/1000,
        		eicas_gc.ecam_flaps_center_y,
        		eicas_gc.ecam_flaps_center_y - eicas_gc.ecam_flaps_box_h*40/1000
        };
        
        // slats_center (x,y) is the bottom left corner
        int[] slats_lozange_x = {
        		eicas_gc.ecam_slats_center_x + eicas_gc.ecam_flaps_box_w*78/1000, // upper right corner
        		eicas_gc.ecam_slats_center_x + eicas_gc.ecam_flaps_box_w*15/1000,  // upper left corner
        		eicas_gc.ecam_slats_center_x,  // bottom left corner 
        		eicas_gc.ecam_slats_center_x + eicas_gc.ecam_flaps_box_w*53/1000 // bottom right corner
        };
        int[] slats_lozange_y = {
        		eicas_gc.ecam_slats_center_y - eicas_gc.ecam_flaps_box_h*260/1000,
        		eicas_gc.ecam_slats_center_y - eicas_gc.ecam_flaps_box_h*176/1000,
        		eicas_gc.ecam_slats_center_y,
        		eicas_gc.ecam_slats_center_y - eicas_gc.ecam_flaps_box_h*59/1000,
        };
        
        AffineTransform original_at = g2.getTransform();
        // debug - draw the box
        // g2.drawRect(eicas_gc.ecam_flaps_box_x, eicas_gc.ecam_flaps_box_y, eicas_gc.ecam_flaps_box_w, eicas_gc.ecam_flaps_box_h);
        
        // wing 
        g2.setColor(eicas_gc.ecam_markings_color);

        int[] wing_section_x = {
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*345/1000, // 2,7 / 7,8 (up left corner)
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*375/1000, // 2,9 / 7,8 (up right corner)
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*395/1000, // 3.1 / 7,8 (down right corner)
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*325/1000  // 2,5 / 7,8 (down left corner)
        };
        int[] wing_section_y = {
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*155/1000, // 0.25 / 1.7
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*155/1000,
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*310/1000, // 0.55 / 1.7
        		eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*310/1000
        };
        g2.drawPolygon(wing_section_x, wing_section_y, 4);        	

        	
        
        // Flaps
        if ( flaps > 0.05f ) {

        	// flaps text
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.setFont(eicas_gc.font_l);     
        	g2.drawString("S", eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*10/100, eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_s*4/3);
        	g2.drawString("F", eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*70/100, eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_s*4/3);
        	
        	String flaps_str="FLAP";
        	// if (flapshandle != flaps) { g2.setColor(eicas_gc.ecam_action_color); }
        	// g2.drawString(flaps_str, eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*40/100- eicas_gc.get_text_width(g2, eicas_gc.font_l, flaps_str)/2, eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_s*4/3);
        	
        	
        	// flaps bullets
        	g2.setColor(eicas_gc.ecam_markings_color);
        	/*
        	g2.drawArc(
        			flaps_center_x - flaps_w, 
        			flaps_center_y - flaps_h,
        			flaps_w*2 + 2,
        			flaps_h*2 + 2, 0, 80);
        	*/

        	for ( int i=1; i<=detents; i++) {
    			g2.fillOval(
    					eicas_gc.ecam_flaps_bullet[i].x,
    					eicas_gc.ecam_flaps_bullet[i].y,    					
    					eicas_gc.ecam_bullet_r,
    					eicas_gc.ecam_bullet_r
    					);
        	}

        	// flaps handle
        	if (flapshandle != flaps) {
        		int tx = (int) Math.round(flaps_w*flapshandle); 
        	    int ty = (int) Math.round(flaps_h*flapshandle);
        		g2.setColor(eicas_gc.ecam_action_color);
                g2.translate(tx,ty);
                g2.drawPolygon(flaps_lozange_x, flaps_lozange_y, 4);
        		// g2.drawLine(eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*46/100 , eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*28/100 - 1, eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l, eicas_gc.wing_y + eicas_gc.wing_h/2);
        		g2.setTransform(original_at);
        	} else {
        		// Draw flap position in green
        		g2.setColor(eicas_gc.ecam_normal_color);
        	}
        	
        	if (flapshandle==1) { 
        		flaps_str="FULL";
        	} else if ( Math.round(flapshandle*detents) == 1 && slats > 0.01) {
        		// Strange behaviour with QPAC airbus
        		flaps_str = "1+F";
        	} else {
        		flaps_str = ""+Math.round(flapshandle*detents);
        	}
        	g2.drawString(flaps_str, eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*38/100 - eicas_gc.get_text_width(g2, eicas_gc.font_l, flaps_str)/2,
        			eicas_gc.ecam_flaps_box_y + eicas_gc.line_height_l*3);
        }
        
        // Flaps position
        g2.setColor(eicas_gc.ecam_normal_color);
		int tx = (int) Math.round(flaps_w*flaps); 
	    int ty = (int) Math.round(flaps_h*flaps);
	            
        g2.translate(tx,ty);
        g2.drawPolygon(flaps_lozange_x, flaps_lozange_y, 4);
        g2.setTransform(original_at);
        g2.drawLine(eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*395/1000, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*310/1000 - 1 ,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*398/1000 + tx, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*310/1000 + ty);
           
        
        // Slats 
        if ( slats > 0.05f ) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	// slats arc
       	
        	/* g2.drawArc(
        			slats_center_x - slats_w, 
        			slats_center_y - slats_h,
        			slats_w*2 + 2,
        			slats_h*2 + 2, 90, 90);
        	*/
        	for ( int i=1; i<=detents; i++) {
    			g2.fillOval(
    					eicas_gc.ecam_slats_bullet[i].x,
    					eicas_gc.ecam_slats_bullet[i].y,    					
    					eicas_gc.ecam_bullet_r,
    					eicas_gc.ecam_bullet_r
    					);
        	}
        }
 
        // Slats position
        g2.setColor(eicas_gc.ecam_normal_color);
		tx = (int) Math.round(-slats_w*slats); 
	    ty = (int) Math.round(slats_h*slats);
        g2.translate(tx,ty);
        g2.drawPolygon(slats_lozange_x, slats_lozange_y, 4);
        g2.setTransform(original_at);
        g2.drawLine(eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*325/1000, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*310/1000 - 1 ,
        		eicas_gc.ecam_flaps_box_x + eicas_gc.ecam_flaps_box_w*322/1000 + tx, eicas_gc.ecam_flaps_box_y + eicas_gc.ecam_flaps_box_h*310/1000 + ty);

    }
    
    
    
    
    private void draw_speed_brakes(Graphics2D g2) {

        
        float speedbrake = this.aircraft.get_speed_brake();
        boolean sbrk_armed = this.aircraft.speed_brake_armed();
        boolean sbrk_eq = this.aircraft.has_speed_brake();

        AffineTransform original_at = g2.getTransform();
        
        if ( sbrk_eq ) {
            
        	if ( speedbrake > 0.01f ) {
        		// speedbrake arc
        		g2.setColor(eicas_gc.dim_markings_color);
        		g2.drawArc(eicas_gc.spdbrk_x - eicas_gc.spdbrk_w - 1, eicas_gc.spdbrk_y - eicas_gc.spdbrk_w - 1, eicas_gc.spdbrk_w*2 + 2, eicas_gc.spdbrk_w*2 + 2, 0, 80);
        		g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.spdbrk_y, eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.controls_w/2*6/100, eicas_gc.spdbrk_y);
        		g2.rotate(Math.toRadians(-40), eicas_gc.spdbrk_x, eicas_gc.spdbrk_y);
        		g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.spdbrk_y, eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.controls_w/2*6/100, eicas_gc.spdbrk_y);
        		g2.rotate(Math.toRadians(-40), eicas_gc.spdbrk_x, eicas_gc.spdbrk_y);
        		g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.spdbrk_y, eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.controls_w/2*6/100, eicas_gc.spdbrk_y);
        		g2.setTransform(original_at);
        	}
        
            //speedbrake
            int[] spdbrk_triangle_x = {
                eicas_gc.spdbrk_x,
                eicas_gc.spdbrk_x,
                eicas_gc.spdbrk_x + eicas_gc.spdbrk_w
            };
            int[] spdbrk_triangle_y = {
                eicas_gc.spdbrk_y + eicas_gc.spdbrk_h/2,
                eicas_gc.spdbrk_y - eicas_gc.spdbrk_h/2,
                eicas_gc.spdbrk_y
            };
            if ( speedbrake > 0.51f ) {
                g2.setColor(eicas_gc.caution_color);
            } else if ( ( ( ! this.avionics.is_cl30() ) && ( speedbrake > 0.01f ) ) || ( ( this.avionics.is_cl30() ) && ( speedbrake > 0.05f ) ) ) {
                g2.setColor(eicas_gc.unusual_color);
            } else if ( sbrk_armed ) {
                g2.setColor(eicas_gc.normal_color);
            } else {
                g2.setColor(eicas_gc.markings_color);
            }
            g2.rotate(Math.toRadians(-80*speedbrake), eicas_gc.spdbrk_x, eicas_gc.spdbrk_y);
            g2.fillOval(eicas_gc.spdbrk_x - eicas_gc.spdbrk_h/2, eicas_gc.spdbrk_y - eicas_gc.spdbrk_h/2, eicas_gc.spdbrk_h, eicas_gc.spdbrk_h);
            g2.fillPolygon(spdbrk_triangle_x, spdbrk_triangle_y, 3);
            g2.setTransform(original_at);

            g2.setColor(eicas_gc.color_boeingcyan);
            g2.setFont(eicas_gc.font_s);
            g2.drawString("SPEEDBRK", eicas_gc.wing_x, eicas_gc.wing_y - eicas_gc.line_height_s*12/4);
        
        }

    }
    
    private void scalePen(Graphics2D g2, float factor) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(factor * eicas_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }
}
