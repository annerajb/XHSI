package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import net.sourceforge.xhsi.model.ModelFactory;

public class ECAM_Flaps extends EICASSubcomponent {


	
	public ECAM_Flaps(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		// TODO Auto-generated constructor stub
	}


    public void paint(Graphics2D g2) {

        if ( eicas_gc.powered && eicas_gc.airbus_style ) {        	
        	draw_flaps_slats(g2);
        	draw_speed_brakes(g2);
        	/*
        	if (this.avionics.is_qpac()) {
        		DrawQpacEcamMemo(g2);
        	} else if (this.avionics.is_jar_a320neo()) {
        		DrawJarA320neoEcamMemo(g2);
        	} else {
        		DrawXHSIEcamMemo(g2);
        	}
        	*/
        		
        }
    }
	
    private void draw_flaps_slats(Graphics2D g2) {
        
        float flaps = this.aircraft.get_flap_position();
        float flapshandle = this.aircraft.get_flap_handle();
        int detents = this.aircraft.get_flap_detents();
        
        AffineTransform original_at = g2.getTransform();
        
        // wing
        g2.setColor(eicas_gc.dim_markings_color);
        g2.fillOval(eicas_gc.wing_x - eicas_gc.wing_h/2, eicas_gc.wing_y - eicas_gc.wing_h/2, eicas_gc.wing_h, eicas_gc.wing_h);
        int[] wing_section_x = {
            eicas_gc.wing_x,
            eicas_gc.wing_x + eicas_gc.wing_w/8,
            eicas_gc.wing_x + eicas_gc.wing_w/3,
            eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.spdbrk_w,
            eicas_gc.wing_x + eicas_gc.wing_w,
            eicas_gc.wing_x + eicas_gc.wing_w,
            eicas_gc.wing_x
        };
        int[] wing_section_y = {
            eicas_gc.wing_y - eicas_gc.wing_h/2,
            eicas_gc.wing_y - eicas_gc.wing_h*7/8,
            eicas_gc.wing_y - eicas_gc.wing_h,
            eicas_gc.wing_y - eicas_gc.wing_h,
            eicas_gc.wing_y - eicas_gc.wing_h/2,
            eicas_gc.wing_y + eicas_gc.wing_h/2,
            eicas_gc.wing_y + eicas_gc.wing_h/2
        };
        g2.fillPolygon(wing_section_x, wing_section_y, 7);

        
        // flaps arc
        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.flaps_l - 1, eicas_gc.wing_y - eicas_gc.flaps_l - 1, eicas_gc.flaps_l*2 + 2, eicas_gc.flaps_l*2 + 2, 0-5, -60);
        g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l, eicas_gc.wing_y + eicas_gc.wing_h/2, eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l - eicas_gc.controls_w/2*8/100, eicas_gc.wing_y + eicas_gc.wing_h/2);
        if ( detents >= 2 ) {
            double rotang = Math.toRadians(60.0d / detents);
            for ( int i=0; i!=detents; i++) {
                g2.rotate(rotang, eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.wing_y);
                g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l, eicas_gc.wing_y + eicas_gc.wing_h/2, eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l - eicas_gc.controls_w/2*4/100, eicas_gc.wing_y + eicas_gc.wing_h/2);
            }
            g2.setTransform(original_at);
        }
        g2.rotate(Math.toRadians(60), eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.wing_y);
        g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l, eicas_gc.wing_y + eicas_gc.wing_h/2, eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l - eicas_gc.controls_w/2*8/100, eicas_gc.wing_y + eicas_gc.wing_h/2);
        g2.setTransform(original_at);
        
        // flaps handle
        g2.setColor(eicas_gc.dim_markings_color);
        g2.rotate(Math.toRadians(60*flapshandle), eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.wing_y);
        g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w , eicas_gc.wing_y + eicas_gc.wing_h/2 - 1, eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l, eicas_gc.wing_y + eicas_gc.wing_h/2);
        g2.setTransform(original_at);
        
        // flaps
        int[] flaps_triangle_x = {
            eicas_gc.wing_x + eicas_gc.wing_w,
            eicas_gc.wing_x + eicas_gc.wing_w,
            eicas_gc.wing_x + eicas_gc.wing_w + eicas_gc.flaps_l
        };
        int[] flaps_triangle_y = {
            eicas_gc.wing_y + eicas_gc.wing_h/2,
            eicas_gc.wing_y - eicas_gc.wing_h/2,
            eicas_gc.wing_y + eicas_gc.wing_h/2
        };
        g2.setColor(eicas_gc.normal_color);
        g2.fillOval(eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.wing_h/2, eicas_gc.wing_y - eicas_gc.wing_h/2, eicas_gc.wing_h, eicas_gc.wing_h);
        g2.rotate(Math.toRadians(60*flaps), eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.wing_y);
        g2.fillPolygon(flaps_triangle_x, flaps_triangle_y, 3);
        g2.setTransform(original_at);
        
        g2.setColor(eicas_gc.color_boeingcyan);
        g2.setFont(eicas_gc.font_s);
        g2.drawString("FLAPS", eicas_gc.wing_x, eicas_gc.wing_y + eicas_gc.line_height_s*10/4);

        
        
    }
    
    private void draw_speed_brakes(Graphics2D g2) {

        
        float speedbrake = this.aircraft.get_speed_brake();
        boolean sbrk_armed = this.aircraft.speed_brake_armed();
        boolean sbrk_eq = this.aircraft.has_speed_brake();

        AffineTransform original_at = g2.getTransform();
        
        if ( sbrk_eq ) {
            
            // speedbrake arc
            g2.setColor(eicas_gc.dim_markings_color);
            g2.drawArc(eicas_gc.spdbrk_x - eicas_gc.spdbrk_w - 1, eicas_gc.spdbrk_y - eicas_gc.spdbrk_w - 1, eicas_gc.spdbrk_w*2 + 2, eicas_gc.spdbrk_w*2 + 2, 0, 80);
            g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.spdbrk_y, eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.controls_w/2*6/100, eicas_gc.spdbrk_y);
            g2.rotate(Math.toRadians(-40), eicas_gc.spdbrk_x, eicas_gc.spdbrk_y);
            g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.spdbrk_y, eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.controls_w/2*6/100, eicas_gc.spdbrk_y);
            g2.rotate(Math.toRadians(-40), eicas_gc.spdbrk_x, eicas_gc.spdbrk_y);
            g2.drawLine(eicas_gc.wing_x + eicas_gc.wing_w, eicas_gc.spdbrk_y, eicas_gc.wing_x + eicas_gc.wing_w - eicas_gc.controls_w/2*6/100, eicas_gc.spdbrk_y);
            g2.setTransform(original_at);
        
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

}
