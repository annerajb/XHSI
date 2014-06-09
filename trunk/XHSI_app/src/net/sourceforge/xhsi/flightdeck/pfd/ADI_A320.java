/**
* ADI_A320.java
* 
* This is the Airbus A320 family version of ADI.java Attitude & Director Indicator
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  Nicolas Carel
* Adapted for Airbus by Nicolas Carel
* Reference : A320 FCOM 1.31.40 page 1 REV 36
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
package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;


public class ADI_A320 extends PFDSubcomponent {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


	public ADI_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
	}


	public void paint(Graphics2D g2) {
		if ( pfd_gc.airbus_style ) {
			if ( ! XHSIStatus.receiving  || ! this.avionics.att_valid() ) {
				// FCOM 1.31.40 p26 (1) 
				// if the PFD loses attitude data, its entire sphere is cleared to display the ATT flag (red)
				if ( pfd_gc.powered ) drawFailedADI(g2);
			} else if ( pfd_gc.powered ) {
				drawADI(g2);
				drawMarker(g2);
			} 
		}       
	}

	private void drawFailedADI(Graphics2D g2) {
		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		/*
		int left = pfd_gc.adi_size_left;
		int right = pfd_gc.adi_size_right;
		int up = pfd_gc.adi_size_up;
		int down = pfd_gc.adi_size_down;	
		Area airbus_horizon_area = new Area ( new Arc2D.Float ( (float) cx - left, (float) cy - up, (float) left + right, (float) up + down, 0.0f,360.0f,Arc2D.CHORD));
		Area square_horizon_area = new Area ( new Rectangle(cx - left*9/10, cy - up*11/10, left*9/10 + right*9/10, up + down*12/10) );
		airbus_horizon_area.intersect( square_horizon_area );
		*/
		g2.setColor(pfd_gc.pfd_alarm_color);
		// g2.draw(airbus_horizon_area);
		g2.setFont(pfd_gc.font_xxl);
		String failed_str = "ATT";
		g2.drawString(failed_str, cx - pfd_gc.get_text_width(g2, pfd_gc.font_xxl, failed_str)/2, cy);
	}

	private void drawADI(Graphics2D g2) {

		int cx = pfd_gc.adi_cx;
		int cy = pfd_gc.adi_cy;
		int left = pfd_gc.adi_size_left;
		int right = pfd_gc.adi_size_right;
		int up = pfd_gc.adi_size_up;
		int down = pfd_gc.adi_size_down;
		int p_90 = pfd_gc.adi_pitch90;
		int scale = pfd_gc.adi_pitchscale;
        int ra = Math.round(this.aircraft.agl_m() * 3.28084f); // Radio altitude
        boolean airborne = ! this.aircraft.on_ground();
        boolean protections = this.avionics.is_qpac();
        int mark_size = left * 6/10;
        float alt_f_range = 1100.0f;
        int gnd_y = pfd_gc.adi_cy + Math.round( (this.aircraft.agl_m() * 3.28084f) * pfd_gc.tape_height / alt_f_range );
        
        // Get engines status : Slideslip Blue target and ground stick orders
		boolean engine_started = false;
		boolean engine_takeoff = false;
		boolean beta_target = false;
		float max_n1=0.0f;
		float min_n1=1000.0f;
		float n1;
		for (int pos=0; pos<this.aircraft.num_engines(); pos++) {
			n1 = this.aircraft.get_N1(pos);
			if (n1 > max_n1) max_n1 = n1; 
			if (n1 < min_n1) min_n1 = n1;
			if (n1 > 5.0f) engine_started = true;
			if (n1 > 80.0f) engine_takeoff = true;
		}
		if (((max_n1 - min_n1) > 35.0f) && engine_takeoff && this.aircraft.get_flap_handle() > 0.0f ) beta_target=true;
        
		boolean colorgradient_horizon = this.preferences.get_draw_colorgradient_horizon();

		float pitch = this.aircraft.pitch(); // radians? no, degrees!
		
		float bank = this.aircraft.bank(); // degrees
		//logger.warning("pitch: " + pitch + " / " + Math.toDegrees(pitch));
		//bank *= 2.0f;
		//pitch = 11.75f;

		// full-scale pitch down = adi_pitchscale (eg: 22°)
		int pitch_y = cy + (int)(down * pitch / scale);

		// Memorize original graphic settings
		Shape original_clipshape = g2.getClip();
		AffineTransform original_at = g2.getTransform();
		Stroke original_stroke = g2.getStroke();
		
		// Compute bottom ADI line for clipping
		int bottom_adi_y_max = cy + down*37/48 - 2; // this is the max and the min is cy.
		int bottom_adi_y;
		if (ra < 570) {
			int positive_ra = (ra>0) ? ra : 0;
			bottom_adi_y = pitch_y + ((bottom_adi_y_max - cy) * positive_ra / 180);
			if (bottom_adi_y > bottom_adi_y_max ) { bottom_adi_y = bottom_adi_y_max; }
		} else {
			bottom_adi_y = bottom_adi_y_max;
		}
		
		if ( ! colorgradient_horizon ) {
			g2.clipRect(cx - left, cy - up, left + right, up + down);
		} else if ( this.preferences.get_draw_fullwidth_horizon() ) {
			if ( pfd_gc.draw_hsi ) {
				g2.clipRect(
						pfd_gc.panel_rect.x,
						pfd_gc.panel_rect.y, 
						pfd_gc.panel_rect.width,
						pfd_gc.dg_cy - pfd_gc.dg_radius - pfd_gc.hsi_tick_w - pfd_gc.line_height_xl*3/2 - pfd_gc.panel_rect.y
						);
			} else {
				g2.clipRect(
						pfd_gc.panel_rect.x, 
						pfd_gc.panel_rect.y + pfd_gc.panel_offset_y, 
						pfd_gc.panel_rect.width, 
						pfd_gc.panel_rect.width
						);
			}
		}

		// g2.rotate(Math.toRadians(-bank), cx, cy);

		int diagonal = colorgradient_horizon ?
				(int)Math.hypot( Math.max(cx, pfd_gc.panel_rect.width - cx), Math.max(cy, pfd_gc.panel_rect.height - cy) ) :
					(int)Math.hypot( Math.max(left, right), Math.max(up, down) );

		Area airbus_horizon_area = new Area ( new Arc2D.Float ( (float) cx - left, (float) cy - up, (float) left + right, (float) up + down, 0.0f,360.0f,Arc2D.CHORD));
		Area square_horizon_area = new Area ( new Rectangle(cx - left*9/10, cy - up*11/10, left*9/10 + right*9/10, up + down*12/10) );
		airbus_horizon_area.intersect( square_horizon_area );				
				
//		if ( colorgradient_horizon ) {
//			g2.rotate(Math.toRadians(-bank), cx, cy);
//			GradientPaint up_gradient = new GradientPaint(
//					cx - diagonal, pitch_y - p_90, pfd_gc.background_color,
//					cx - diagonal, pitch_y - p_90/2, pfd_gc.sky_color,
//					false);
//			GradientPaint sky_gradient = new GradientPaint(
//					cx - diagonal, pitch_y - p_90/2, pfd_gc.sky_color,
//					cx - diagonal, pitch_y, pfd_gc.brightsky_color,
//					false);
//			GradientPaint ground_gradient = new GradientPaint(
//					cx - diagonal, pitch_y, pfd_gc.brightground_color,
//					cx - diagonal, pitch_y + p_90/2, pfd_gc.ground_color,
//					false);
//			GradientPaint down_gradient = new GradientPaint(
//					cx - diagonal, pitch_y + p_90/2, pfd_gc.ground_color,
//					cx - diagonal, pitch_y + p_90 , pfd_gc.background_color,
//					false);
//
//			g2.setPaint(up_gradient);
//			g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90/2 + 2);
//			g2.setPaint(sky_gradient);
//			g2.fillRect(cx - diagonal, pitch_y - p_90/2, 2 * diagonal, p_90);
//
//			g2.setPaint(ground_gradient);
//			g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90/2 + 2);
//			g2.setPaint(down_gradient);
//			g2.fillRect(cx - diagonal, pitch_y + p_90/2, 2 * diagonal, p_90/2);
//			g2.setColor(pfd_gc.markings_color);
//			g2.drawLine(cx - diagonal, pitch_y, cx + diagonal, pitch_y);			
//		} else if (this.preferences.get_draw_airbus_horizon()) {
			// g2.clipRect(cx - left, cy - up, left + right, up + down)
			// With Airbus shape, bank mark area is always blue and RA area is always brown
			g2.setClip(airbus_horizon_area);
			g2.rotate(Math.toRadians(-bank), cx, cy);
			
			int pitch_y_min = cy - up*37/48;
			int pitch_y_max = cy + down * 37/48;
			int pitch_y_airbus = pitch_y;
			if (pitch_y > pitch_y_max) pitch_y_airbus = pitch_y_max;
			if (pitch_y < pitch_y_min) pitch_y_airbus = pitch_y_min;
			g2.setColor(pfd_gc.sky_color);
			g2.fillRect(cx - diagonal, pitch_y_airbus - p_90, 2 * diagonal, p_90);
			g2.setColor(pfd_gc.ground_color);
			g2.fillRect(cx - diagonal, pitch_y_airbus, 2 * diagonal, p_90);	
			g2.setColor(pfd_gc.markings_color);
			g2.drawLine(cx - diagonal, pitch_y_airbus, cx + diagonal, pitch_y_airbus);
//		} else {
//			g2.rotate(Math.toRadians(-bank), cx, cy);
//			g2.setColor(pfd_gc.sky_color);
//			g2.fillRect(cx - diagonal, pitch_y - p_90, 2 * diagonal, p_90);
//			g2.setColor(pfd_gc.ground_color);
//			g2.fillRect(cx - diagonal, pitch_y, 2 * diagonal, p_90);
//			g2.setColor(pfd_gc.markings_color);
//			g2.drawLine(cx - diagonal, pitch_y, cx + diagonal, pitch_y);
//		}

		g2.setTransform(original_at);


		if ( this.preferences.get_draw_roundedsquare_horizon() ) {
			g2.setColor(pfd_gc.background_color);
			Area adi_roundrectarea = new Area(new RoundRectangle2D.Float(
					cx - left, cy - up, left + right, up + down,
					(int)(60 * pfd_gc.scaling_factor),
					(int)(60 * pfd_gc.scaling_factor)));
			Area adi_area = new Area(new Rectangle2D.Float(cx - left, cy - up, left + right, up + down));
			adi_area.subtract(adi_roundrectarea);
			g2.fill(adi_area);
		}
		
		g2.rotate(Math.toRadians(-bank), cx, cy);
			
		// pitch marks
		Area pitchmark_area = new Area ( new Rectangle(
				cx - left + left/16,
				cy - up*37/48,
				left - left/16 + right - right/16,
				up*37/48 + down*37/48 - (bottom_adi_y_max - bottom_adi_y) 
				) );

		// intersect with the previous clip
		g2.clip( pitchmark_area );
		

		// Top and bottom lines
		// The bottom lines moves up between ground and 120ft AGL.
		g2.setColor(pfd_gc.pfd_markings_color);
		g2.drawLine(cx - left, cy - up*37/48   + 2, cx + right, cy - up*37/48   + 2 );
		g2.drawLine(cx - left, bottom_adi_y, cx + right, bottom_adi_y );
	
		// Heading marks (draw before full clipping)
		float hdg = this.aircraft.heading();
		int hdg10 = (int)Math.round( hdg / 10.0f ) * 10;
        for (int hdg_mark = hdg10 - 30; hdg_mark <= hdg10 + 30; hdg_mark += 10) {
        	int hdg_x = pfd_gc.adi_cx + Math.round( ((float)hdg_mark - hdg) * pfd_gc.hdg_width / 50.0f );
        	g2.drawLine(hdg_x, pitch_y, hdg_x, pitch_y + pfd_gc.adi_size_down/20);
        }
		
		drawPitchmark(g2, pitch, +175, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, +150, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, +125, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, +100, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  +75, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  +50, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  +25, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,    0, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  -25, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  -50, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch,  -75, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -100, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -125, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -150, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -175, pitch_y, p_90, cx, cy, mark_size, protections);
		drawPitchmark(g2, pitch, -200, pitch_y, p_90, cx, cy, mark_size, protections);

		// no more clipping ...
		g2.setClip(original_clipshape);
		// ... unless
		if ( ! colorgradient_horizon ) {
			g2.setTransform(original_at);
			g2.clipRect(cx - left, cy - up, left + right, up + down);
			g2.rotate(Math.toRadians(-bank), cx, cy);
		}


		// bank pointer
		int[] bank_pointer_x = {
				cx,
				cx - left/14,
				cx + right/14 };
		int[] bank_pointer_y = {
				cy - up + 1,
				cy - up*29/32 + 1 ,
				cy - up*29/32 + 1 };
		g2.setColor(pfd_gc.pfd_reference_color);
		if ( Math.abs(bank) > 35.0f ) {
			if ( Math.abs(bank) > 70.0f ) {
				g2.setColor(pfd_gc.warning_color);
			} else {
				g2.setColor(pfd_gc.caution_color);
			}
			g2.fillPolygon(bank_pointer_x, bank_pointer_y, 3);
		}
		g2.drawPolygon(bank_pointer_x, bank_pointer_y, 3);

		// slip/skid indicator => Beta target on airbus
		float ss = - this.aircraft.sideslip();
		int ss_x;
		if ( beta_target ) 	g2.setColor(Color.blue);
		if ( Math.abs(ss) > 10.0f ) {
			ss = 10.0f * Math.signum(ss);
			ss_x = cx - Math.round(ss * left/6 / 10.0f);
			g2.fillRect(ss_x - left/12, cy - up + up/16 + up/8, left/12 + right/12, up/24);
		} else {
			ss_x = cx - Math.round(ss * left/6 / 10.0f);
		}
		int[] slip_pointer_x = {
				ss_x + right/12,
				ss_x - left/12,
				ss_x - left/8,
				ss_x + right/8 };
		int[] slip_pointer_y = {
				cy - up*28/32 + 1,
				cy - up*28/32 + 1,
				cy - up*26/32 + 1,
				cy - up*26/32 + 1 };
		g2.drawPolygon(slip_pointer_x, slip_pointer_y, 4);

	
		// Display Radar Altitude
		if  ( ra < 2500 )  {
			int caution_ra = 400; // default value when no DH set
			int d_ra_txt; // delta font width to align with the middle
			
			// above 50ft, round to 10
			// bellow 50 ft, round to 5
			// bellow 10 fr, don't round
			if ( ra > 50 ) {
				ra = ( ra + 5 ) / 10 * 10;
		
			} else if ( ra > 10 ) {
				ra = ( ra + 2 ) / 5 * 5;
			}

			// amber and bigger when ra < dh + 100 otherwise green
			String ra_str = "" + ra;
			if ( ra < caution_ra ) {
				g2.setColor(pfd_gc.pfd_caution_color);
				g2.setFont(pfd_gc.font_xxl);
				d_ra_txt = pfd_gc.get_text_width(g2, pfd_gc.font_xxl, ra_str) / 2;
			} else {
				g2.setColor(pfd_gc.pfd_active_color);
				g2.setFont(pfd_gc.font_xl);
				d_ra_txt = pfd_gc.get_text_width(g2, pfd_gc.font_xl, ra_str) / 2 ;
			}

			// digital readout of the current RA
			g2.drawString(ra_str, cx - d_ra_txt, pfd_gc.adi_cy + pfd_gc.adi_size_down - pfd_gc.line_height_xl/2);
		}


		g2.setTransform(original_at);


		// FPV Flight Path Vector or Bird 
		boolean fpv_on = ! this.aircraft.on_ground();	
		if ( this.avionics.is_qpac()) { 
			fpv_on = this.avionics.qpac_fcu_hdg_trk() && ! this.aircraft.on_ground();						
		}
		if ( fpv_on ) {
			int dx = (int)(down * this.aircraft.drift() / scale);
			int dy = (int)(down * this.aircraft.aoa() / scale);
			if ( (Math.abs(dx) < down) && (Math.abs(dy) < down) ) {
				int fpv_x = cx - dx;
				int fpv_y = cy + dy;
				int fpv_r = down/17;
				g2.setColor(pfd_gc.pfd_active_color);
				g2.setStroke(new BasicStroke(2.0f * pfd_gc.grow_scaling_factor));
				g2.drawOval(fpv_x - fpv_r, fpv_y - fpv_r, fpv_r*2, fpv_r*2);
				g2.drawLine(fpv_x, fpv_y - fpv_r, fpv_x, fpv_y - fpv_r*17/10);
				g2.drawLine(fpv_x - fpv_r, fpv_y, fpv_x - fpv_r*26/10, fpv_y);
				g2.drawLine(fpv_x + fpv_r, fpv_y, fpv_x + fpv_r*26/10, fpv_y);
				g2.setStroke(original_stroke);
			}
		}


		// airplane symbol
		// TODO : Should be dimmed with FPV on and FD bars off
		int wing_t = Math.round(3 * pfd_gc.grow_scaling_factor);
		int wing_i = left * 13 / 24;
		int wing_o = left * 21 / 24;
		int wing_h = down * 3 / 24;
		int left_wing_x[] = {
				cx - wing_i + wing_t,
				cx - wing_i + wing_t,
				cx - wing_i - wing_t,
				cx - wing_i - wing_t,
				cx - wing_o,
				cx - wing_o
		};
		int right_wing_x[] = {
				cx + wing_i - wing_t,
				cx + wing_i - wing_t,
				cx + wing_i + wing_t,
				cx + wing_i + wing_t,
				cx + wing_o,
				cx + wing_o
		};
		int wing_y[] = {
				cy - wing_t,
				cy + wing_h,
				cy + wing_h,
				cy + wing_t,
				cy + wing_t,
				cy - wing_t
		};
		g2.setColor(pfd_gc.background_color);
		g2.fillPolygon(left_wing_x, wing_y, 6);
		g2.fillPolygon(right_wing_x, wing_y, 6);
		g2.setColor(pfd_gc.pfd_reference_color);
		g2.drawPolygon(left_wing_x, wing_y, 6);
		g2.drawPolygon(right_wing_x, wing_y, 6);
		// small square in the center (that's the rule on Airbus A320)
		// int wing_t = Math.round(4 * pfd_gc.grow_scaling_factor);
		g2.setColor(pfd_gc.background_color);
		g2.fillRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
		g2.setColor(pfd_gc.pfd_reference_color);
		g2.drawRect(cx - wing_t, cy - wing_t, wing_t * 2, wing_t * 2);
		
	
		// Stick orders : on ground / bellow 30 ft AGL
		if  ((! airborne) || (ra < 30))  {

			if (engine_started) {
				g2.setColor(pfd_gc.pfd_markings_color);
				int st_width = left / 8;
				int st_left = cx - left*14/20;
				int st_right = cx + right*14/20;
				int st_up = cy - up/2;
				int st_down = cy + down/2;
				int st_x = cx + Math.round(this.aircraft.yoke_roll() * left*14/20);
				int st_y = cy - Math.round(this.aircraft.yoke_pitch() * up/2);
				int st_d = left/60;
				int st_w = left/10;
				// Stick box
				// top left
				g2.drawLine(st_left, st_up, st_left + st_width, st_up);
				g2.drawLine(st_left, st_up, st_left, st_up + st_width);
				// top right
				g2.drawLine(st_right, st_up, st_right - st_width, st_up);
				g2.drawLine(st_right, st_up, st_right, st_up + st_width);
				// bottom left
				g2.drawLine(st_left, st_down, st_left + st_width, st_down);
				g2.drawLine(st_left, st_down, st_left, st_down - st_width);
				// bottom right
				g2.drawLine(st_right, st_down, st_right - st_width, st_down);
				g2.drawLine(st_right, st_down, st_right, st_down - st_width);

				// Stick marker
				// top left
				g2.drawLine(st_x - st_d - st_w , st_y - st_d, st_x - st_d, st_y - st_d);
				g2.drawLine(st_x - st_d, st_y - st_d, st_x - st_d, st_y - st_d - st_w);
				// top right
				g2.drawLine(st_x + st_d + st_w , st_y - st_d, st_x + st_d, st_y - st_d);
				g2.drawLine(st_x + st_d, st_y - st_d, st_x + st_d, st_y - st_d - st_w);
				// bottom left
				g2.drawLine(st_x - st_d - st_w , st_y + st_d, st_x - st_d, st_y + st_d);
				g2.drawLine(st_x - st_d, st_y + st_d, st_x - st_d, st_y + st_d + st_w);
				// bottom right
				g2.drawLine(st_x + st_d + st_w , st_y + st_d, st_x + st_d, st_y + st_d);
				g2.drawLine(st_x + st_d, st_y + st_d, st_x + st_d, st_y + st_d + st_w);
			}
		}

		
		// Flight Director (VS/HDG mode)
		boolean fd_on = this.avionics.autopilot_mode() >= 1 ? true : false;	
		if ( this.avionics.is_qpac()) { 
			fd_on = this.avionics.qpac_fd_on();
			// if (this.avionics.qpac_fd1_hor_bar() == -1.0f) fd_on = false;
			if (this.avionics.qpac_fcu_hdg_trk()) fd_on = false;
		}
		// if ( this.aircraft.on_ground() ) { fd_on = false; }
		if ( fd_on ) {
			int fd_y;
			int fd_x = cx + (int)(down * (-bank+this.avionics.fd_roll()) / scale) / 3; // divide by 3 to limit deflection
			int fd_bar = left * 12 / 24;
			int fd_yaw = -10000;
			
			if ( this.avionics.is_x737() ) {
				fd_y = cy + (int)(down * (-this.avionics.fd_pitch()) / scale);
			} else if ( this.avionics.is_qpac() ) {
				fd_y = cy - (int)(fd_bar * (this.avionics.qpac_fd_hor_bar() - 1.0f ) );
				fd_x = cx + (int)(fd_bar * (this.avionics.qpac_fd_ver_bar() - 1.0f ) );
				fd_yaw = cx + (int)(fd_bar * (this.avionics.qpac_fd_yaw_bar() * 2.0f) );
			} else {
				fd_y = cy + (int)(down * (pitch-this.avionics.fd_pitch()) / scale);
			}
	
			// FD bars
			g2.setColor(pfd_gc.pfd_active_color);
			original_stroke = g2.getStroke();
			g2.setStroke(new BasicStroke(3.0f * pfd_gc.scaling_factor));
			// horizontal
			if (fd_y < (cy+left)) g2.drawLine(cx - fd_bar, fd_y, cx + fd_bar, fd_y);
			// vertical
			if (fd_x > (cx-left)) g2.drawLine(fd_x, cy - fd_bar, fd_x, cy + fd_bar);
			if (fd_yaw > (cx-left)) {
				g2.setStroke(new BasicStroke(8.0f * pfd_gc.scaling_factor));
				g2.drawLine(fd_yaw, cy, fd_yaw, cy + fd_bar);
			}
			g2.setStroke(original_stroke);
		}



		// bank marks
		// on AirBus, marks are outside
		g2.setClip(original_clipshape);		
		int level_triangle_x[] = { cx, cx - left/14, cx + right/14 };
		int level_triangle_y[] = { cy - up, cy - up*35/32, cy - up*35/32 };
		int bank_mark_thick = up/25;
		int bank_mark_heigth = up/20;
		int bank_mark_2heigth = up/13;
		int bank_mark_x = cx - bank_mark_thick / 2;
		int bank_mark_y = cy - up - bank_mark_heigth;
		int bank_mark_2y = cy - up - bank_mark_2heigth;
		
		g2.setColor(pfd_gc.pfd_reference_color);
		g2.drawPolygon(level_triangle_x, level_triangle_y, 3);
		
		g2.setColor(pfd_gc.pfd_markings_color);
		original_stroke = g2.getStroke();
		g2.setStroke(new BasicStroke(1.5f * pfd_gc.scaling_factor));
		g2.drawArc(  cx - left,  cy - up, left + right, up + down, 59, 62);
		
		g2.rotate(Math.toRadians(+10), cx, cy);	
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(-10-10), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(+10+20), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(-20-20), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_y, bank_mark_thick, bank_mark_heigth);
		g2.rotate(Math.toRadians(+20+45), cx, cy);
		g2.drawLine(cx, cy - up, cx, cy - up - bank_mark_2heigth);
		g2.rotate(Math.toRadians(-45-45), cx, cy);
		g2.drawLine(cx, cy - up, cx, cy - up - bank_mark_2heigth);
		g2.rotate(Math.toRadians(+45+30), cx, cy);		
		g2.drawRect(bank_mark_x, bank_mark_2y, bank_mark_thick, bank_mark_2heigth);
		g2.rotate(Math.toRadians(-30-30), cx, cy);
		g2.drawRect(bank_mark_x, bank_mark_2y, bank_mark_thick, bank_mark_2heigth);

		
		// Airbus max bank protection mark is at 67 deg. (normal law)
		// double strikes become amber crosses with alternate & direct laws
		if (protections) {
			g2.setColor(pfd_gc.pfd_active_color);
			g2.rotate(Math.toRadians(+30+67), cx, cy);
			g2.drawLine(cx-2, cy - up - up/40, cx-2, cy - up + up/20);
			g2.drawLine(cx+2, cy - up - up/50, cx+2, cy - up + up/17);
			g2.rotate(Math.toRadians(-67-67), cx, cy);
			g2.drawLine(cx-2, cy - up - up/50, cx-2, cy - up + up/17);
			g2.drawLine(cx+2, cy - up - up/40, cx+2, cy - up + up/20);		
		} else {
			g2.setColor(pfd_gc.pfd_caution_color);
			g2.rotate(Math.toRadians(+30+67), cx, cy);
			g2.drawLine(cx-up/30, cy - up - up/30, cx+up/30, cy - up + up/30);
			g2.drawLine(cx+up/30, cy - up - up/30, cx-up/30, cy - up + up/30);
			g2.rotate(Math.toRadians(-67-67), cx, cy);
			g2.drawLine(cx-up/30, cy - up - up/30, cx+up/30, cy - up + up/30);
			g2.drawLine(cx+up/30, cy - up - up/30, cx-up/30, cy - up + up/30);		
		}
		g2.setStroke(original_stroke);
		g2.setTransform(original_at);
	}



	private void drawPitchmark(Graphics2D g2, float pitch, int pitchmark, int p_y, int p_90, int cx, int cy, int size, boolean protections) {

		int p_m = Math.round(pitch / 2.5f) * 25 + pitchmark;
		int m_y = p_y - p_90 * p_m / 900;
		int p_w = size;
		int prot_m_w = size / 6 ;
		Stroke original_stroke;
		if ( p_m % 100 == 0 ) {
			p_w = size * 7 / 16;
		} else if ( p_m % 50 == 0 ) {
			p_w = size / 4;
		} else if ( p_m % 25 == 0 ) {
			p_w = size / 12;
		}

		// FCOM 1.31.40 p4 (4) Pitch Scale (White)
		// Markers every 10° between -80° and +80°
		// every 2.5° between -10° and +30°
		
		if ( (( p_m <= 300 ) && ( p_m != 0 ) && ( p_m >= -200 )) 
				|| (p_m > 300 && p_m <= 800 && (p_m % 100) == 0) 
				|| ( p_m < -200 && p_m > -800 && (p_m % 100) == 0)) {
			g2.drawLine(cx - p_w, m_y, cx + p_w, m_y);
			if ( ( p_m != 0 ) && ( p_m % 100 == 0 ) ) {
				g2.setFont(pfd_gc.font_m);
				int f_w = pfd_gc.get_text_width(g2, pfd_gc.font_m, "00");
				int f_y = pfd_gc.line_height_m / 2 - 1;
				String pitch_str = "" + Math.abs(p_m/10);
				g2.drawString(pitch_str, cx - p_w - f_w - size/8, m_y + f_y);
				g2.drawString(pitch_str, cx + p_w + size/8, m_y + f_y);
			}
		}
		// protection marks
		if (( p_m/10 == -15) || (p_m/10 == 30)) {
			if ( ! protections) {
				// amber cross
				g2.setColor(pfd_gc.pfd_caution_color);
				g2.drawLine(cx - p_w - prot_m_w, m_y + p_w/4, cx - p_w, m_y - p_w/4);
				g2.drawLine(cx - p_w - prot_m_w, m_y - p_w/4, cx - p_w, m_y + p_w/4);
				g2.drawLine(cx + p_w + prot_m_w, m_y + p_w/4, cx + p_w, m_y - p_w/4);
				g2.drawLine(cx + p_w + prot_m_w, m_y - p_w/4, cx + p_w, m_y + p_w/4);
			} else {
				// green lines
				g2.setColor(pfd_gc.pfd_active_color);
				g2.drawLine(cx - p_w - prot_m_w, m_y+2, cx - p_w, m_y+2);
				g2.drawLine(cx - p_w - prot_m_w, m_y-2, cx - p_w, m_y-2);
				g2.drawLine(cx + p_w + prot_m_w, m_y+2, cx + p_w, m_y+2);
				g2.drawLine(cx + p_w + prot_m_w, m_y-2, cx + p_w, m_y-2);	
			}
			g2.setColor(pfd_gc.pfd_markings_color);
		}
		if (p_m == 350 || p_m == -250 || p_m == 500 || p_m == -400 ) {
			// Don't draw pitch mark, but arrow
			
			int pa_d = p_m > 0 ? 1 : -1;
			// BIG ARROW (pitch > 80°)
			// TODO : draw Big Arrow in pitch upset more than 80°
			int pa_x[] = { 
					cx - p_w,
					cx,
					cx+p_w,
					cx+p_w/4,
					cx+p_w/4,
					cx+p_w/3,
					cx,
					cx-p_w/3,
					cx-p_w/4,
					cx-p_w/4
			};
		
			int pa_y[] = {
					m_y - pa_d * p_w / 2,
					m_y + pa_d * p_w ,
					m_y - pa_d * p_w / 2,
					m_y - pa_d * p_w / 2,
					m_y - pa_d * p_w / 3,
					m_y - pa_d * p_w / 3,
					m_y,
					m_y - pa_d * p_w / 3,
					m_y - pa_d * p_w / 3,
					m_y - pa_d * p_w / 2			
			};
			// NORMAL ARROW
			int sa_x[] = {
					cx,
					cx - p_w/2,
					cx - p_w,
					cx,
					cx + p_w,
					cx + p_w/2
			};
			
			int sa_y[] = {
					m_y,
					m_y - pa_d * p_w,
					m_y - pa_d * p_w,
					m_y + pa_d * p_w,
					m_y - pa_d * p_w,
					m_y - pa_d * p_w					
			};
			g2.setColor(pfd_gc.warning_color);
			original_stroke = g2.getStroke();
			g2.setStroke(new BasicStroke(3.0f * pfd_gc.scaling_factor));
			g2.drawPolygon(sa_x, sa_y, 6);	
			g2.setColor(pfd_gc.pfd_markings_color);
			g2.setStroke(original_stroke);

		}
	}


	// This function should be in the ILS_A320 class
	private void drawMarker(Graphics2D g2) {

		if ( this.avionics.outer_marker() || this.avionics.middle_marker() || this.avionics.inner_marker() ) {

			//int m_r = pfd_gc.adi_size_right*2/16;
			int m_x = pfd_gc.adi_cx + pfd_gc.adi_size_right;
			int m_y = pfd_gc.adi_cy + pfd_gc.adi_size_down; // - pfd_gc.line_height_xl/2

			//g2.setColor(pfd_gc.background_color);
			//g2.fillOval(m_x, m_y, 2*m_r, 2*m_r);

			String mstr = "";
			if ( this.avionics.outer_marker() ) {
				g2.setColor(pfd_gc.pfd_selected_color);
				mstr = "OM";
			} else if ( this.avionics.middle_marker() ) {
				g2.setColor(pfd_gc.pfd_caution_color);
				mstr = "MM";
			} else {
				g2.setColor(pfd_gc.pfd_markings_color);
				mstr = "IM";
			}

			g2.setFont(pfd_gc.font_xl);
			g2.drawString(mstr, m_x - pfd_gc.get_text_width(g2, pfd_gc.font_xl, mstr), m_y  );

		}

	}


}
