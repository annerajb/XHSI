package net.sourceforge.xhsi.flightdeck.pfd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;

public class SpeedTape_A320 extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    private boolean ias_trend_active = false;

    public SpeedTape_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
    }


    public void paint(Graphics2D g2) {
    	if ( pfd_gc.airbus_style ) {
    		if ( ! XHSIStatus.receiving || ! this.avionics.ias_valid() ) {
    			// FCOM 1.31.40 p26 (6) 
    			// if the speed information fails, the SPD flag (red) replaces the speed scale
    			if ( pfd_gc.powered ) drawFailedTape(g2);
    		} else if ( pfd_gc.powered ) {
    			drawTape(g2);
    		}
        }
    }

    private void drawFailedTape(Graphics2D g2) {
    	// A320 tape with white line border
    	int speedtape_right = pfd_gc.speedtape_left + pfd_gc.tape_width*6/8;
    	g2.setColor(pfd_gc.pfd_instrument_background_color);
    	g2.fillRect(pfd_gc.speedtape_left, pfd_gc.tape_top, speedtape_right-pfd_gc.speedtape_left, pfd_gc.tape_height);
    	g2.setColor(pfd_gc.pfd_alarm_color);
    	g2.drawLine(speedtape_right, pfd_gc.tape_top ,speedtape_right, pfd_gc.tape_top + pfd_gc.tape_height + 1 );
    	g2.drawLine(pfd_gc.speedtape_left, pfd_gc.tape_top ,pfd_gc.speedtape_left + pfd_gc.tape_width, pfd_gc.tape_top  );
    	g2.drawLine(pfd_gc.speedtape_left, pfd_gc.tape_top + pfd_gc.tape_height + 1,pfd_gc.speedtape_left + pfd_gc.tape_width, pfd_gc.tape_top + pfd_gc.tape_height + 1 );       
    	String failed_str = "SPD";
        g2.setFont(pfd_gc.font_xxl);
    	g2.drawString( failed_str, pfd_gc.speedtape_left,  pfd_gc.adi_cy + pfd_gc.line_height_l/2 );
    	failed_str = "SPD SEL";   	
    	g2.setFont(pfd_gc.font_l);
    	int str_w = pfd_gc.get_text_width(g2, pfd_gc.font_l, failed_str);
    	int str_x = pfd_gc.speedtape_left + pfd_gc.tape_width - str_w;
    	int str_y =  pfd_gc.tape_top - pfd_gc.tape_width/16 ;
    	g2.clearRect(str_x - pfd_gc.digit_width_l/3, str_y - pfd_gc.line_height_l*7/8, str_w + pfd_gc.digit_width_l*2/3, pfd_gc.line_height_l);
    	g2.drawString(failed_str, str_x, str_y); 
    }

    private void drawTape(Graphics2D g2) {

        DecimalFormat mach_format = new DecimalFormat("#.000");
        DecimalFormatSymbols format_symbols = mach_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        mach_format.setDecimalFormatSymbols(format_symbols);


        // speeds
        float ias = this.aircraft.airspeed_ind();
        if (ias < 30.0f) { ias = 30.0f; }
        float tas = this.aircraft.true_air_speed();
        float sound_speed = this.aircraft.sound_speed();
        float mach = this.aircraft.mach();


        // A320 tape with white line border
        int speedtape_right = pfd_gc.speedtape_left + pfd_gc.tape_width*6/8;
        pfd_gc.setTransparent(g2, this.preferences.get_draw_colorgradient_horizon());
        g2.setColor(pfd_gc.instrument_background_color);
        g2.fillRect(pfd_gc.speedtape_left - 1, pfd_gc.tape_top - 1, speedtape_right - pfd_gc.speedtape_left + 1 , pfd_gc.tape_height + 2);
        pfd_gc.setOpaque(g2);
        g2.setColor(pfd_gc.markings_color);
        if (ias >= 70) 
        	g2.drawLine(speedtape_right, pfd_gc.tape_top ,speedtape_right, pfd_gc.tape_top + pfd_gc.tape_height + 1 ); 
        else { 
        	int line_height = pfd_gc.tape_top + pfd_gc.tape_height/2 + Math.round( (ias-30)/40*pfd_gc.tape_height/2 ) + 1;
        	g2.drawLine(speedtape_right, pfd_gc.tape_top ,speedtape_right, line_height );
        }
        	
        g2.drawLine(pfd_gc.speedtape_left, pfd_gc.tape_top ,pfd_gc.speedtape_left + pfd_gc.tape_width, pfd_gc.tape_top  );
        if (ias >= 70) g2.drawLine(pfd_gc.speedtape_left, pfd_gc.tape_top + pfd_gc.tape_height + 1,pfd_gc.speedtape_left + pfd_gc.tape_width, pfd_gc.tape_top + pfd_gc.tape_height + 1 );            


        // Yellow line and triangle for actual IAS
        g2.setColor(Color.yellow);
        g2.drawLine(pfd_gc.speedtape_left- pfd_gc.tape_width/8, pfd_gc.tape_top + pfd_gc.tape_height/2, pfd_gc.speedtape_left + 1, pfd_gc.tape_top + pfd_gc.tape_height/2  );
        g2.drawLine(speedtape_right - pfd_gc.tape_width*3/16, pfd_gc.tape_top + pfd_gc.tape_height/2, speedtape_right + pfd_gc.tape_width*3/16, pfd_gc.tape_top + pfd_gc.tape_height/2  );
        int[] speed_tri_x = {
                speedtape_right + pfd_gc.tape_width/10,
           		speedtape_right + pfd_gc.tape_width*3/12,
                speedtape_right + pfd_gc.tape_width*3/12
            };
        int[] speed_tri_y = {
        		pfd_gc.tape_top + pfd_gc.tape_height/2,
        		pfd_gc.tape_top + pfd_gc.tape_height/2 - pfd_gc.tape_width*1/10,
        		pfd_gc.tape_top + pfd_gc.tape_height/2 + pfd_gc.tape_width*1/10
        };
        g2.setColor(Color.yellow);
        g2.fillPolygon(speed_tri_x, speed_tri_y, 3);
        
        
        Shape original_clipshape = g2.getClip();
        g2.clipRect(pfd_gc.speedtape_left, pfd_gc.tape_top, pfd_gc.tape_width*2, pfd_gc.tape_height);


        // scale markings
        g2.setColor(pfd_gc.markings_color);
        // round to nearest multiple of 10
        int ias5 = Math.round(ias / 10.0f) * 10;
        // From there, go 50kts up and down
        for (int ias_mark = ias5 - 50; ias_mark <= ias5 + 50; ias_mark += 10) {
            if (ias_mark >= 30) {

                int ias_y = pfd_gc.adi_cy - Math.round( ((float)ias_mark - ias) * pfd_gc.tape_height / 80.0f );
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*5/8, ias_y, speedtape_right - 1, ias_y);
                
                if (ias_mark % 20 == 0) {
                    g2.setFont(pfd_gc.font_xl);
                    DecimalFormat speed_format = new DecimalFormat("000");            		
                    String mark_str = speed_format.format(ias_mark);
                    g2.drawString(mark_str, pfd_gc.speedtape_left + pfd_gc.tape_width*9/16 - pfd_gc.get_text_width(g2, pfd_gc.font_xl, mark_str),  ias_y + pfd_gc.line_height_l/2 - 2);
                }

            }
        }


        // 10sec speed trend vector
        // Drawn in yellow on airbus
        float ias_trend = this.aircraft.airspeed_acceleration() * 4.0f;
        // TODO : set class var ias_trend_active ; set true if trend > 2 knots, set false when trend < 1 knot
        // Reference : FCOM 1.31.40 page 5 (2)
        if ( Math.abs(ias_trend) > 5.0f ) {
        	ias_trend_active = true;
        } else if (	ias_trend_active && (Math.abs(ias_trend) < 2.5f)) {
        	ias_trend_active = false; 
        }
        if ( Math.abs(ias_trend) > 5.0f ) {

            if ( ( ias + ias_trend ) < 0.0f ) {
                ias_trend = - ias;
            }
            int asi10_y = pfd_gc.adi_cy - Math.round( ias_trend * pfd_gc.tape_height / 80.0f );
            g2.setColor(Color.yellow);
            g2.drawLine(speedtape_right - pfd_gc.tape_width/12, pfd_gc.adi_cy, speedtape_right - pfd_gc.tape_width/12 , asi10_y);
            int arrow_dx = pfd_gc.tape_width*1/16;
            int arrow_dy = pfd_gc.tape_width*2/16 * (int)Math.signum(ias_trend);
            int[] arrow_x = {
            	speedtape_right - pfd_gc.tape_width/12,
            	speedtape_right - pfd_gc.tape_width/12 - arrow_dx,
            	speedtape_right - pfd_gc.tape_width/12 + arrow_dx
            };
            int[] arrow_y = {
                asi10_y,
                asi10_y + arrow_dy,
                asi10_y + arrow_dy
            };
            g2.drawPolygon(arrow_x, arrow_y, 3);
            g2.fillPolygon(arrow_x, arrow_y, 3);

        }

        

        // red max
        Stroke original_stroke = g2.getStroke();
        int halfstroke = pfd_gc.tape_width/16;
        float vmmo = 999.9f;
        float mmo = this.aircraft.get_Mmo();
        if ( ( ias > 100.0f ) && ( mmo > 0.1f ) ) {
            vmmo = mmo * sound_speed * ias / tas;
        }
        float vmax = Math.min(this.aircraft.get_Vne(), vmmo);
        if ( this.aircraft.get_flap_position() > 0.0f ) {
            // flaps extended
            //float vfe = this.aircraft.get_Vfe() + ( 1.0f - this.aircraft.get_flap_position() ) * ( this.aircraft.get_Vno() - this.aircraft.get_Vfe() );
            vmax = Math.min(vmax, this.aircraft.get_Vfe());
        }
        if ( ! this.aircraft.gear_is_up() ) {
            // landing gear extended
            vmax = Math.min(vmax, this.aircraft.get_Vle());
        }
        if ( this.avionics.is_qpac() && (this.avionics.qpac_vmo()>100)) {
        	// QPAC VMO
        	vmax = Math.min(vmax, this.avionics.qpac_vmo());
        }
        int red_max_y = pfd_gc.adi_cy - Math.round( (vmax - ias) * pfd_gc.tape_height / 80.0f );
        if ( red_max_y > pfd_gc.tape_top ) {
            // draw a thick red dashed line *from* red_max_y *to* the top
            g2.setColor(pfd_gc.warning_color);
            g2.setStroke(new BasicStroke(2.0f * halfstroke));
            float red_dashes[] = { halfstroke*2.0f, halfstroke*2.0f };
            g2.setStroke(new BasicStroke(2.0f * halfstroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, red_dashes, 0.0f));
            g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, red_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, pfd_gc.tape_top);
            g2.setStroke(original_stroke);
            g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke * 2, red_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke * 2, pfd_gc.tape_top);

        } else {
            // we don't draw, but set the top for the amber line
            red_max_y = pfd_gc.tape_top;
        }

        // amber max
        float vno = this.aircraft.get_Vno();
        if ( (vno < vmax) && (vno > 0) ) {
            int amber_max_y = pfd_gc.adi_cy - Math.round( (vno - ias) * pfd_gc.tape_height / 80.0f );
            if ( amber_max_y > pfd_gc.tape_top ) {
                // draw an amber line between red_max_y and amber_max_y
                g2.setColor(pfd_gc.caution_color);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 2, red_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 2, amber_max_y);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + 1, amber_max_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, amber_max_y);
            }
        }


        // red min and amber min only when airborne
        float vs_est = this.aircraft.get_Vso() + ( 1.0f - this.aircraft.get_flap_position() ) * ( this.aircraft.get_Vs() - this.aircraft.get_Vso() );
        // VLS Minimum select speed, 1.25 * stalling speed
        float vls = vs_est*1.1f;
        float v_aprot = vls;
        if ( this.avionics.is_qpac() ) { 
        	if ( this.avionics.qpac_vls() > 0 ) vls=this.avionics.qpac_vls();
        	if ( this.avionics.qpac_alpha_max() > 0 ) vs_est=this.avionics.qpac_alpha_max();
        	if ( this.avionics.qpac_alpha_prot() > 0 ) v_aprot=this.avionics.qpac_alpha_prot();
        }

        if ( ! this.aircraft.on_ground() ) {

            // red min
            // estimate stall speed by interpolating between Vs (clean) and Vso (landing configuration)
        	// on Airbus, the line in plain red not dashed for the stall speed limit (V alpha max)
        	// Bar between V alpha prot and V alpha max is red dashed
            int red_min_y = pfd_gc.adi_cy - Math.round( (vs_est - ias) * pfd_gc.tape_height / 80.0f );
            if ( red_min_y < pfd_gc.tape_top + pfd_gc.tape_height ) {
                // draw a thick red dashed line *from* red_min_y *to* zero
                int red_zero_y = pfd_gc.adi_cy - Math.round( (0.0f - ias) * pfd_gc.tape_height / 80.0f );
                g2.setColor(pfd_gc.pfd_alarm_color);
                g2.setStroke(new BasicStroke(2.0f * halfstroke));
                //float red_dashes[] = { halfstroke*2.0f, halfstroke*2.0f };
                //g2.setStroke(new BasicStroke(2.0f * halfstroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, red_dashes, 0.0f));
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, red_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, red_zero_y);
                g2.setStroke(original_stroke);
            } else {
                // we don't draw, but set the top for the amber line
                red_min_y = pfd_gc.tape_top + pfd_gc.tape_height;
            }
            
            // Alpha prot
            int a_dashed_min_y = pfd_gc.adi_cy - Math.round( (v_aprot - ias) * pfd_gc.tape_height / 80.0f );
            if ( a_dashed_min_y < pfd_gc.tape_top + pfd_gc.tape_height ) {
                // draw a thick amber dashed line *from* red_min_y *to* a_dashed_min_y
                // int red_zero_y = pfd_gc.adi_cy - Math.round( (0.0f - ias) * pfd_gc.tape_height / 80.0f );
                g2.setColor(pfd_gc.pfd_caution_color);
                g2.setStroke(new BasicStroke(2.0f * halfstroke));
                float red_dashes[] = { halfstroke*2.0f, halfstroke*2.0f };
                g2.setStroke(new BasicStroke(2.0f * halfstroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, red_dashes, 0.0f));
//                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, red_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, a_dashed_min_y);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, a_dashed_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, red_min_y);
                g2.setStroke(original_stroke);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke * 2, red_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke * 2, a_dashed_min_y);
            } else {
                // we don't draw, but set the top for the amber line
            	a_dashed_min_y = pfd_gc.tape_top + pfd_gc.tape_height;
            }
            
            // Amber min lien is VLS Minimum select speed, 1.25 * stalling speed
            int amber_min_y = pfd_gc.adi_cy - Math.round( (vls - ias) * pfd_gc.tape_height / 80.0f );
            if ( amber_min_y < pfd_gc.tape_top + pfd_gc.tape_height ) {
                // draw an amber line between red_min_y and amber_min_y
                g2.setColor(pfd_gc.pfd_caution_color);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 2, a_dashed_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 2, amber_min_y);
                g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + 1, amber_min_y, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8 + halfstroke + 1, amber_min_y);
            }

        }

        // V-speeds
        // V1 is labelled "1" in CYAN with a mark
        // VR is green dot speed
        // V2 is target speed
        boolean display_v1 = this.aircraft.on_ground() ||
                ( ( this.aircraft.agl_m() < 100.0f /* 30ft */ ) && ( this.aircraft.vvi() > 250.0f ) );
        boolean take_off = this.aircraft.on_ground() ||
                ( ( this.aircraft.agl_m() < 762.0f /* 2500ft */ ) && ( this.aircraft.vvi() > 250.0f ) );
        boolean landing = ! this.aircraft.on_ground() &&
                ( ( this.aircraft.agl_m() < 1524.0f /* 5000ft */ ) && ( this.aircraft.vvi() < -250.0f ) );
        float v1;
        if ( this.avionics.has_ufmc() ) {

            // V-speeds from UFMC

            if ( take_off ) {
                v1 = this.avionics.ufmc_v1();
                if ( display_v1 && (v1 > 0.0f) ) drawV1speed(g2, v1, ias);
                float vr = this.avionics.ufmc_vr();
                if ( vr > 0.0f ) drawVRSpeed(g2, vr, ias);
                float v2 = this.avionics.ufmc_v2();
                if ( v2 > 0.0f ) drawVspeed(g2, v2, ias, "V2");
            }
            if ( landing ) {
                float vref = this.avionics.ufmc_vref();
                if ( vref > 0.0f ) {
                    drawVspeed(g2, vref, ias, "REF");
                } else {
                    vref = this.avionics.ufmc_vf30();
                    if ( vref > 0.0f ) drawVspeed(g2, vref, ias, "F30");
                    vref = this.avionics.ufmc_vf40();
                    if ( vref > 0.0f ) drawVspeed(g2, vref, ias, "F40");
                }
            }
            
        } else if ( this.avionics.is_cl30() ) {
            
            // V-speeds from CL30 (without UFMC)
            
            if ( this.avionics.cl30_refspds() == 1 ) {
            	v1 = (float) this.avionics.cl30_v1();
                if (display_v1) drawV1speed(g2, (float)this.avionics.cl30_v1(), ias);
                drawVRSpeed(g2, (float)this.avionics.cl30_vr(), ias);
                drawVspeed(g2, (float)this.avionics.cl30_v2(), ias, "V2");
                
            } else {
                drawVspeed(g2, (float)this.avionics.cl30_vt(), ias, "VT");
                drawVspeed(g2, (float)this.avionics.cl30_vga(), ias, "VGA");
                drawVspeed(g2, (float)this.avionics.cl30_vref(), ias, "REF");
            }

        } else if ( this.avionics.is_qpac() ) {

            // estimate V-speeds
            // with no QPAC integration, that would be the default
            if ( take_off ) {
                v1 = (float) this.avionics.qpac_v1_value();
                if ( display_v1 && (v1 > 0.0f) ) drawV1speed(g2, v1, ias);
                float vr = this.avionics.qpac_vr(); 
                drawVRSpeed(g2, vr, ias);
                
            }
            /*
            if ( landing ) {
                float vref = vs_est * 1.3f; // rough estimate
                drawVspeed(g2, vref, ias, "REF");
            }
            */
            
            drawVspeed(g2, this.avionics.qpac_vf() , ias, "F");
            drawVspeed(g2, this.avionics.qpac_vs() , ias, "S");
            drawGDotSpeed(g2, this.avionics.qpac_v_green_dot() , ias);          
            /*
             * draw Vso (or V alpha max) - debug purpose only
            drawVspeed(g2, this.aircraft.get_Vso(), ias, "VSO");
            drawVspeed(g2, vno, ias, "VNO");
            drawVspeed(g2, this.aircraft.get_Vle(), ias, "VLE");
            */
            drawFlapsLimit(g2, this.aircraft.get_Vfe(), ias);           
            
        } else {

            // estimate V-speeds
            // with no QPAC integration, that would be the default
            if ( take_off ) {
                float vr = this.avionics.autopilot_speed() * 0.95f; // very rough estimate based on V2 - 5%
                drawVRSpeed(g2, vr, ias);
                //drawGDotSpeed(g2,vr, ias);
            }
            if ( landing ) {
                float vref = vs_est * 1.3f; // rough estimate
                drawVspeed(g2, vref, ias, "REF");
            }
                  
        }

        
        // AP speed bug and value readout
        // A320 Target speed : Cyan when selected, Magenta when Managed, Mach Green in cruize
        
        float ap_ias;
        String ap_spd_str;
        if ( this.avionics.autopilot_speed_is_mach() ) {
            // AP SPD is Mach
            float ap_tas = this.avionics.autopilot_speed() * sound_speed;
            if ( ( ias < 10.0f ) || ( tas < 10.0f ) ) {
                ap_ias = ap_tas;
            } else {
                ap_ias = ap_tas * ias / tas;
            }
            ap_spd_str = mach_format.format( this.avionics.autopilot_speed() );
        } else {
            // AP SPD is Kts
            ap_ias = this.avionics.autopilot_speed();
            ap_spd_str = "" + Math.round( ap_ias );
        }

        int ap_spdbug_y = pfd_gc.adi_cy - Math.round( (ap_ias - ias) * pfd_gc.tape_height / 80.0f );
        boolean ap_spdbug_show = true;
        // Managed or selected speed 
       	g2.setColor(pfd_gc.pfd_selected_color); 
       	if (this.avionics.is_qpac() && this.avionics.qpac_fcu_spd_dashed()) g2.setColor(pfd_gc.pfd_managed_color); 
        if ( ap_spdbug_y < pfd_gc.tape_top ) {
            ap_spdbug_y = pfd_gc.tape_top;
            ap_spdbug_show = false;
        } else if ( ap_spdbug_y > pfd_gc.tape_top + pfd_gc.tape_height ) {
            ap_spdbug_y = pfd_gc.tape_top + pfd_gc.tape_height;
            ap_spdbug_show = false;
        } else if ( ap_ias == 0 ) {
        	g2.setColor(pfd_gc.pfd_alarm_color);
        	ap_spd_str = "SPD SEL";
        	ap_spdbug_y = pfd_gc.tape_top;
        	ap_spdbug_show = false;
        }
        if (ap_spdbug_show) {
        	int[] bug_x = {
        			speedtape_right,
        			speedtape_right + pfd_gc.tape_width*2/8 + 1,
        			speedtape_right + pfd_gc.tape_width*2/8 +1
        	};
        	int[] bug_y = {
        			ap_spdbug_y,
        			ap_spdbug_y - pfd_gc.tape_width*1/8 - 1,
        			ap_spdbug_y + pfd_gc.tape_width*1/8 + 1
        	};
 
        	g2.drawPolygon(bug_x, bug_y, 3);

        	g2.setClip(original_clipshape);        	
        } else {
            g2.setClip(original_clipshape);  
        	// Write on top 
        	g2.setFont(pfd_gc.font_l);
        	int str_w = pfd_gc.get_text_width(g2, pfd_gc.font_l, ap_spd_str);
        	int str_x = pfd_gc.speedtape_left + pfd_gc.tape_width - str_w;
        	int str_y = (ap_spdbug_y == pfd_gc.tape_top) ? pfd_gc.tape_top - pfd_gc.tape_width/16 : pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.line_height_l+ pfd_gc.tape_width/16 ;
        	g2.clearRect(str_x - pfd_gc.digit_width_l/3, str_y - pfd_gc.line_height_l*7/8, str_w + pfd_gc.digit_width_l*2/3, pfd_gc.line_height_l);
        	g2.drawString(ap_spd_str, str_x, str_y);
        }


        g2.setClip(original_clipshape);

        // Mach value, displayed in green
        if ( mach >= 0.50f ) {
            String mach_str = mach_format.format( mach );
            g2.setFont(pfd_gc.font_xl);
            g2.setColor(Color.green);
            g2.drawString(mach_str, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8 - pfd_gc.get_text_width(g2, pfd_gc.font_xxl, mach_str), pfd_gc.tape_top + pfd_gc.tape_height + pfd_gc.tape_width/8 + 2*pfd_gc.line_height_xl - 3);
        }
        
    }

    
    private void drawVspeed(Graphics2D g2, float v, float ias, String v_str) {
        if (v > 0) {
        	int v_y = pfd_gc.adi_cy - Math.round( (v - ias) * pfd_gc.tape_height / 80.0f );

        	g2.setColor(pfd_gc.pfd_active_color);
        	g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8, v_y, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, v_y);
        	g2.setFont(pfd_gc.font_l);
        	g2.drawString(v_str, pfd_gc.speedtape_left + pfd_gc.tape_width * 9/10, v_y + pfd_gc.line_height_l/2); 
        }
    }

    private void drawV1speed(Graphics2D g2, float v, float ias) {   
        int v_y = pfd_gc.adi_cy - Math.round( (v - ias) * pfd_gc.tape_height / 80.0f );
        String v1_str = "1";
        if (v>0) {
        	g2.setColor(pfd_gc.pfd_armed_color);
        	if ( v_y > pfd_gc.tape_top) {
        		g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8, v_y, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, v_y);
        		g2.setFont(pfd_gc.font_l);
        		g2.drawString(v1_str, pfd_gc.speedtape_left + pfd_gc.tape_width + 2, v_y + pfd_gc.line_height_l/2);
        	} else {
        		v1_str = "" + Math.round(v);
        		g2.setFont(pfd_gc.font_l);
        		g2.drawString(v1_str, pfd_gc.speedtape_left + pfd_gc.tape_width*7/8, pfd_gc.tape_top + pfd_gc.line_height_l*6/5);       	
        	}
        }
    }

    
    private void drawGDotSpeed(Graphics2D g2, float v, float ias) {
        int v_y = pfd_gc.adi_cy - Math.round( (v - ias) * pfd_gc.tape_height / 80.0f );
        if (v>0) {
        	g2.setColor(pfd_gc.pfd_active_color);
        	g2.drawArc(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8, v_y, pfd_gc.tape_width/8, pfd_gc.tape_width/8, 0, 360);
        }
    }
    
    private void drawVRSpeed(Graphics2D g2, float v, float ias) {
        int v_y = pfd_gc.adi_cy - Math.round( (v - ias) * pfd_gc.tape_height / 80.0f );
        if (v>0) {
        	g2.setColor(pfd_gc.pfd_selected_color);
        	g2.drawArc(pfd_gc.speedtape_left + pfd_gc.tape_width*6/8, v_y, pfd_gc.tape_width/8, pfd_gc.tape_width/8, 0, 360);
        }
    }
    
    private void drawFlapsLimit(Graphics2D g2, float v, float ias) {
        int v_y = pfd_gc.adi_cy - Math.round( (v - ias) * pfd_gc.tape_height / 80.0f );
        if (v>0) {
        	g2.setColor(pfd_gc.pfd_caution_color);
        	g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*5/8, v_y-2, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8, v_y-2);
        	g2.drawLine(pfd_gc.speedtape_left + pfd_gc.tape_width*5/8, v_y+2, pfd_gc.speedtape_left + pfd_gc.tape_width*6/8, v_y+2);  
        }
    }
    
    
}
