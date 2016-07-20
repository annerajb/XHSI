package net.sourceforge.xhsi.flightdeck.eicas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;
import net.sourceforge.xhsi.model.ModelFactory;


public class ECAM_Engines extends EICASSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Stroke original_stroke;

    // This Stroke is used by the double lined value boxes
	private class CompositeStroke implements Stroke {
		private Stroke stroke1, stroke2;

		public CompositeStroke( Stroke stroke1, Stroke stroke2 ) {
			this.stroke1 = stroke1;
			this.stroke2 = stroke2;
		}

		public Shape createStrokedShape( Shape shape ) {
			return stroke2.createStrokedShape( stroke1.createStrokedShape( shape ) );
		}
	}
   
    private boolean inhibit;
    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormat three_decimals_format;
    private DecimalFormatSymbols format_symbols;

    
    public ECAM_Engines(ModelFactory model_factory, EICASGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_format = new DecimalFormat("##0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);

        two_decimals_format = new DecimalFormat("#0.00");
        format_symbols = two_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_format.setDecimalFormatSymbols(format_symbols);
        
        three_decimals_format = new DecimalFormat("#0.000");
        format_symbols = three_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        three_decimals_format.setDecimalFormatSymbols(format_symbols);
    }


    public void paint(Graphics2D g2) {

        if ( eicas_gc.airbus_style && eicas_gc.powered && ( this.aircraft.num_engines() > 0 ) ) {

            this.inhibit = ( this.aircraft.agl_m() < 1000.0f / 3.28084f );

            boolean piston = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_MAP );
            boolean turboprop = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_TRQ );
            boolean epr_jet = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_EPR );            
            
            int num_eng = eicas_gc.num_eng;
            
            int cols = Math.max(num_eng, 2);

            if ( piston ) {
            	if (!this.preferences.get_eicas_primary_only() || !this.preferences.get_eicas_draw_controls()) drawRefMAP(g2);
                for (int i=0; i<num_eng; i++) {
                    drawMAP(g2, i, num_eng);
                    drawPROP(g2, i, num_eng);
                    drawEGT(g2, i, num_eng, 3);
                    drawFF(g2, i, num_eng, 4);
                    /* TODO : for beta 9 / Arrange secondary instruments
                    if ( ! this.preferences.get_eicas_primary_only() ) {
                        //drawCHT(g2, i, num_eng);
                        drawOilP(g2, i, num_eng);
                        drawOilT(g2, i, num_eng);
                        drawOilQ(g2, i);
                    }
                    */
                }

            } else if ( turboprop ) {

                for (int i=0; i<num_eng; i++) {
                    drawTRQ(g2, i, num_eng);
                    drawPROP(g2, i, num_eng);
                    drawITT(g2, i, num_eng);
                    drawNG(g2, i, num_eng);
                    /* TODO : for beta 9 / Arrange secondary instruments
                    if ( ! this.preferences.get_eicas_primary_only() ) {
                        //drawFF(g2, i, num_eng);
                        drawOilP(g2, i, num_eng);
                        drawOilT(g2, i, num_eng);
                        drawOilQ(g2, i);
                    } else {
                        // this is exceptional: for a turboprop, there are actually more engine instruments in primary-only mode !
                        // drawFF(g2, i, num_eng, 5);
                    }
                    */
                }

            } else /* most be jet */ {
            	if (!this.preferences.get_eicas_primary_only() || !this.preferences.get_eicas_draw_controls()) drawRefN1(g2,0, epr_jet);

                for (int i=0; i<num_eng; i++) {
                	if (epr_jet) {
                		drawEPR(g2, i, num_eng);
                		if (num_eng<3) {
                			drawEGT(g2, i, num_eng, 2);
                			drawN1(g2, i, num_eng, 3);
                			drawN2(g2, i, num_eng, 4);
                			if (!this.preferences.get_eicas_primary_only() || !this.preferences.get_eicas_draw_controls()) drawFF_EPR_V1(g2, i, num_eng);
                		} else {
                			drawN1_V(g2, i, num_eng, 5);
                    		drawEGT(g2, i, num_eng, 6);
                    		drawN2(g2, i, num_eng, 3);
                    		drawFF(g2, i, num_eng, 4);
                		}
                			
                	} else {
                        drawN1(g2, i, num_eng, 1);
                        drawEGT(g2, i, num_eng, 2);
                        drawN2(g2, i, num_eng, 3);
                        drawFF(g2, i, num_eng, 4);                		
                	}
                    /* TODO : for beta 9 / Arrange secondary instruments
                    if ( ! this.preferences.get_eicas_primary_only() ) {
                        drawOilP(g2, i, num_eng);
                        drawOilT(g2, i, num_eng);
                        drawOilQ(g2, i);
                        drawVIB(g2, i, num_eng);
                    }
                    */
                }
                
            }
            
            drawMainLegends(g2, cols, num_eng, piston, turboprop, epr_jet);
            
            /* TODO : for beta 9 / Arrange secondary instruments
            if ( ! this.preferences.get_eicas_primary_only() ) {
            	drawSecondaryLegends(g2, cols, num_eng, piston, turboprop, epr_jet);            	
            }
            */

        }

    }


    private void drawMainLegends(Graphics2D g2, int cols, int num_eng, boolean piston, boolean turboprop, boolean epr_jet) {

        String ind_str1;
        String ind_str2;
        int ind_x1;
        int ind_x2;
        int ind_middle;
        int eng_l=0;
        int eng_r=1; // place the text between eng_l and eng_r
        if (cols==4 || cols==5) {
        	eng_l=1;
        	eng_r=2;
        } else if (cols==6 || cols==7) {
        	eng_l=2;
        	eng_r=3;
        } else if (cols==8) {
        	eng_l=3;
        	eng_r=4;            	
        }
        ind_middle = (eicas_gc.prim_dial_x[eicas_gc.eng_label] + eicas_gc.prim_dial_x[eicas_gc.eng_label+1]) / 2;
        g2.setFont(eicas_gc.font_l);
        
        // main1
        String trq_unit_str;
        if ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_NM ) {
            trq_unit_str = "Nm";
        } else if ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_PERCENT ) {
            trq_unit_str = "%";
        } else /* if ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_LBFT ) */ {
            trq_unit_str = "LbFt";
        }
        ind_str1 = piston ? "MAP" : ( turboprop ? "TRQ" : ( epr_jet ? "EPR" : "N1" ) );
        ind_str2 = piston ? "In Hg" : ( turboprop ? trq_unit_str : ( epr_jet ? " " : "%" ) );
        drawLegend(g2, ind_str1, ind_str2, ind_middle, eicas_gc.dial_main1_y);

        
        // main2       
        if ( ! piston && !turboprop && epr_jet && num_eng>2 && this.preferences.get_eicas_primary_only() ) {
        	// N1 for EPR display with more than 2 engines 
            // main2 bis
        	drawLegend(g2, "N1", "%", ind_middle, eicas_gc.dial_main5_y);
        	drawLegend(g2, "EGT", "°c", ind_middle, eicas_gc.dial_main6_y);
        } else {
            ind_str1 = piston ? "RPM" : ( turboprop ? "PROP" : "EGT" );
            ind_str2 = piston ? "tr/m" : ( turboprop ? "rpm" : "°c" );
            drawLegend(g2, ind_str1, ind_str2, ind_middle, eicas_gc.dial_main2_y);
        }
        
        
        // main3
        ind_str1 = piston ? "EGT" : ( turboprop ? "ITT" : "N2" );
        ind_str2 = piston ? "°c" : ( turboprop ? " " : "%" );
        drawLegend(g2, ind_str1, ind_str2, ind_middle, eicas_gc.dial_main3_y - eicas_gc.line_height_l/2);

        ind_x1 = ind_middle - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str1)/2;
        if (! piston && ! turboprop && (!epr_jet || num_eng>2)) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.drawLine(ind_x1 - eicas_gc.dial_font_w[3]*3/4, 
        			eicas_gc.dial_main3_y - eicas_gc.line_height_l*3/4,
        			eicas_gc.prim_dial_x[eng_l] + eicas_gc.dial_font_w[3]*7/2,
        			eicas_gc.dial_main3_y - eicas_gc.line_height_l/2);
        	if ( num_eng > 1 ) { 
        		g2.drawLine(ind_x1 + eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str1) + eicas_gc.dial_font_w[3]*3/4, 
        				eicas_gc.dial_main3_y - eicas_gc.line_height_l*3/4, 
        				eicas_gc.prim_dial_x[eng_r] - eicas_gc.dial_font_w[3]*7/2, 
        				eicas_gc.dial_main3_y - eicas_gc.line_height_l/2
        				);
        	}
        }
        
      
        // main4
        String units_str;
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) units_str = XHSIPreferences.FUEL_UNITS_KG;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) units_str = XHSIPreferences.FUEL_UNITS_LBS;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) units_str = XHSIPreferences.FUEL_UNITS_USG;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ units_str = XHSIPreferences.FUEL_UNITS_LTR;
        ind_str1 = turboprop ? "NG" : "F.F";
        ind_str2 = turboprop ? " " : units_str+"/H";
       	ind_x1 = ind_middle  - eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str1)/2;
       	drawLegend(g2, ind_str1, ind_str2, ind_middle, eicas_gc.dial_main4_y- eicas_gc.line_height_l/2);
        if (! turboprop) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.drawLine(ind_x1 - eicas_gc.dial_font_w[4]*3/4, 
        			eicas_gc.dial_main4_y - eicas_gc.line_height_l*3/4,
        			eicas_gc.prim_dial_x[eng_l] + eicas_gc.dial_font_w[4]*7/2,
        			eicas_gc.dial_main4_y - eicas_gc.line_height_l/2);
        	if ( num_eng > 1 ) { 
        		g2.drawLine(ind_x1 + eicas_gc.get_text_width(g2, eicas_gc.font_m, ind_str1) + eicas_gc.dial_font_w[4]*3/4, 
        				eicas_gc.dial_main4_y - eicas_gc.line_height_l*3/4, 
        				eicas_gc.prim_dial_x[eng_r] - eicas_gc.dial_font_w[4]*7/2, 
        				eicas_gc.dial_main4_y - eicas_gc.line_height_l/2
        				);
        	}
        }

        
        if ( ! piston && !turboprop && epr_jet && num_eng<3 && this.preferences.get_eicas_primary_only() ) {
        	// Fuel Flow legend on the right panel 
            // main5
        	ind_str1 = "F.F";
        	ind_str2 = units_str+"/H";        	
        	g2.setColor(eicas_gc.ecam_markings_color);
            g2.setFont(eicas_gc.font_xl);
        	g2.drawString(ind_str1, eicas_gc.ecam_v1_ff_legend_x-eicas_gc.get_text_width(g2, eicas_gc.font_xl, ind_str1), eicas_gc.ecam_v1_ff_legend_y);
        	g2.setColor(eicas_gc.ecam_action_color);
            g2.setFont(eicas_gc.font_l);
        	g2.drawString(ind_str2, eicas_gc.ecam_v1_ff_unit_x, eicas_gc.ecam_v1_ff_legend_y);
        }

    }
   
    private void drawLegend(Graphics2D g2, String leg_str, String units_str, int x, int y){
        int ind_x1 = x - eicas_gc.get_text_width(g2, eicas_gc.font_m, leg_str)/2;
        int ind_x2 = x - eicas_gc.get_text_width(g2, eicas_gc.font_m, units_str)/2;
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.setFont(eicas_gc.font_l);
        g2.drawString(leg_str, ind_x1, y - 2);
        g2.setColor(eicas_gc.ecam_action_color);
        g2.setFont(eicas_gc.font_m);
        g2.drawString(units_str, ind_x2, y + eicas_gc.line_height_m);
    }
    
    private void drawSecondaryLegends(Graphics2D g2, int cols, int num_eng, boolean piston, boolean turboprop, boolean epr_jet) {
    	
        String ind_str1;
        String ind_str2;
        int ind_x1;
        int ind_x2;        
        
        // OIL P
        ind_str1 = "OIL";
        ind_str2 = "PSI";
        if ( cols == 2 ) {
            ind_x1 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str1)/2;
            ind_x2 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str2)/2;
        } else {
            ind_x1 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
            ind_x2 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
        }
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.drawString(ind_str1, ind_x1, eicas_gc.dial_oil_p_y + eicas_gc.dial_r[2]*70/100 + 2*eicas_gc.line_height_xs);
        g2.setColor(eicas_gc.ecam_action_color);
        g2.drawString(ind_str2, ind_x2, eicas_gc.dial_oil_p_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);

        // OIL T
        ind_str1 = "OIL";
        ind_str2 = "°c";

        if ( cols == 2 ) {
            ind_x1 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str1)/2;
            ind_x2 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str2)/2;
        } else {
            ind_x1 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
            ind_x2 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
        }
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.drawString(ind_str1, ind_x1, eicas_gc.dial_oil_t_y + eicas_gc.dial_r[2]*70/100 + 2*eicas_gc.line_height_xs);
        g2.setColor(eicas_gc.ecam_action_color);
        g2.drawString(ind_str2, ind_x2, eicas_gc.dial_oil_t_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);
        
        // OIL Q
        ind_str1 = "OIL";
        ind_str2 = "QT";

        if ( cols == 2 ) {
            ind_x1 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str1)/2;
            ind_x2 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str2)/2;
        } else {
            ind_x1 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
            ind_x2 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;

        }
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.drawString(ind_str1, ind_x1, eicas_gc.dial_oil_q_y + 2 * eicas_gc.line_height_xs*35/20);
        g2.setColor(eicas_gc.ecam_action_color);
        g2.drawString(ind_str2, ind_x2, eicas_gc.dial_oil_q_y + eicas_gc.line_height_xs*35/20);

        // VIB or NG or CHT
        // CHT not implemented, NG goes to primary
        if ( ! piston && ! turboprop ) {
            ind_str1 = piston ? "CHT" : ( turboprop ? "NG" : "VIB" );
            ind_str2 = piston ? " " : ( turboprop ? " " : "%" );
            if ( cols == 2 ) {
                ind_x1 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str1)/2;
                ind_x2 = (eicas_gc.seco_dial_x[0] + eicas_gc.seco_dial_x[1]) / 2 - eicas_gc.get_text_width(g2, eicas_gc.font_xs, ind_str2)/2;
            } else {
                ind_x1 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
                ind_x2 = eicas_gc.seco_dial_x[0] - eicas_gc.dial_r[num_eng]*85/100;
            }
            g2.setColor(eicas_gc.ecam_markings_color);
            g2.drawString(ind_str1, ind_x1, eicas_gc.dial_vib_y + eicas_gc.dial_r[2]*70/100 + 2*eicas_gc.line_height_xs);
            g2.setColor(eicas_gc.ecam_action_color);
            g2.drawString(ind_str2, ind_x2, eicas_gc.dial_vib_y + eicas_gc.dial_r[2]*70/100 + eicas_gc.line_height_xs);
        }

    }
    
    private void drawEPR(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float n1_value = this.aircraft.get_N1(pos);
        float n2_value = this.aircraft.get_N2(pos);       
        float epr_value = this.aircraft.get_EPR(pos);
        float throttle_value = this.aircraft.get_throttle(pos)*100.0f;
        float n1_dial = Math.min(n1_value, 110.0f) / 100.0f;
        float epr_dial = Math.min(epr_value-1.0f, 0.65f) / 0.6f;
        float throttle_dial = Math.min(throttle_value, 110.0f) / 100.0f;
        boolean engine_on = (n1_value > 1.0f) || (n2_value > 1.0f);        
        
        int epr_y = eicas_gc.dial_main1_y;
        int epr_r = eicas_gc.dial_r[num];
        int epr_box_y = epr_y - epr_r/8;
        
        int deg_arc_start = 215;
        int deg_start = 210;
        int deg_caution = 10;
        int deg_warning = 10;
        int deg_arc_range = deg_arc_start - deg_caution;
        // int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;
        int deg_dial_range = deg_start - deg_caution - 10;
        int deg_throttle_start = deg_start;
        int deg_throttle_range = deg_throttle_start - deg_caution - 10;

        if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( n1_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        

        // scale markings every 1/6
        
        g2.setColor(eicas_gc.ecam_markings_color);
        if (engine_on) {
        	g2.rotate(Math.toRadians(360-deg_start), eicas_gc.prim_dial_x[pos], epr_y);
        	for (int i=0; i<=6; i++) {
        		g2.drawLine(eicas_gc.prim_dial_x[pos]+epr_r*18/20, epr_y, eicas_gc.prim_dial_x[pos]+epr_r-1, epr_y);
        		g2.rotate(Math.toRadians(deg_dial_range/6), eicas_gc.prim_dial_x[pos], epr_y);
        	}
        	g2.setTransform(original_at);
        }
        
        // scale numbers 1, 1.2, 1.4, 1.6
        String mark_str;
        if ( num <= 4 && engine_on) {
            g2.setFont(eicas_gc.font_s);
            int n1_digit_x;
            int n1_digit_y;
            int n1_digit_angle = 360-deg_start;
            for (int i=0; i<=6; i+=2) {
                n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*epr_r*10/16);
                n1_digit_y = epr_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*epr_r*10/16);
                mark_str = (i==0) ? "1" : "1."+Integer.toString(i);
                g2.drawString(mark_str, n1_digit_x - eicas_gc.get_text_width(g2, eicas_gc.font_s, mark_str)/2, n1_digit_y+eicas_gc.line_height_s*3/8);                
                n1_digit_angle += deg_dial_range*2/6;
            }
        }

        // Gauge Arc
        g2.setColor(engine_on ? eicas_gc.ecam_markings_color : eicas_gc.ecam_caution_color);  
        g2.drawArc(eicas_gc.prim_dial_x[pos]-epr_r, epr_y-epr_r, 2*epr_r, 2*epr_r, deg_arc_start, -deg_arc_range);
        if (engine_on) {
        	g2.setColor(eicas_gc.ecam_warning_color);
        	original_stroke = g2.getStroke();
        	g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        	// EPR radius red arc 
        	int epr_r_red = epr_r * 98/100; 
        	g2.drawArc(eicas_gc.prim_dial_x[pos]-epr_r_red, epr_y-epr_r_red, 2*epr_r_red, 2*epr_r_red, deg_caution-2, -deg_warn_range);
        	g2.setStroke(original_stroke);
        }
        resetPen(g2);

        // needle
        if (engine_on) {
        	scalePen(g2,3.0f);
        	g2.rotate(Math.toRadians(Math.round(epr_dial*deg_dial_range)-deg_start), eicas_gc.prim_dial_x[pos], epr_y);
        	g2.setColor(eicas_gc.ecam_normal_color);
        	g2.drawLine(eicas_gc.prim_dial_x[pos], epr_y, eicas_gc.prim_dial_x[pos]+epr_r*11/10, epr_y);
        	g2.setTransform(original_at);
        	resetPen(g2);
        }

        // Throttle
        if (engine_on) {
        	g2.rotate(Math.toRadians(Math.round(throttle_dial*deg_throttle_range)-deg_start), eicas_gc.prim_dial_x[pos], epr_y);
        	g2.setColor(eicas_gc.ecam_action_color);
        	g2.drawOval(eicas_gc.prim_dial_x[pos]+epr_r*11/10, epr_y, epr_r/8, epr_r/8);
        	g2.setTransform(original_at);
        }
        
        // value box
    	// Don't draw the box when more than 4 engines
        if ( num < 5 && engine_on) {
            g2.setColor(eicas_gc.ecam_markings_color);
            drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num], epr_box_y + eicas_gc.dial_font_h[num]*45/100,
            		    eicas_gc.dial_font_w[num]*45/10, eicas_gc.dial_box_h[num]);
        }
        if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else if ( n1_dial < 1.1f ) {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	g2.setColor(eicas_gc.ecam_warning_color);
        }

        if (engine_on) {
        	drawStringSmallThreeDecimals(g2, eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] + eicas_gc.dial_font_w[num]*44/10,
        			epr_box_y+eicas_gc.dial_font_h[num]*140/100, eicas_gc.dial_font[num], eicas_gc.dial_font_s[num], epr_value);
        } else {
        	g2.setColor(eicas_gc.ecam_caution_color);
        	g2.setFont(eicas_gc.dial_font[num]);
            g2.drawString("XX", eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] + eicas_gc.dial_font_w[num]*14/10,
            		epr_box_y+eicas_gc.dial_font_h[num]*140/100);	
        }

        // Reverser
        float rev = this.aircraft.reverser_position(pos);
        if ( rev > 0.0f ) {
            if ( rev == 1.0f ) {
                g2.setColor(eicas_gc.ecam_normal_color);
            } else {
                g2.setColor(eicas_gc.ecam_caution_color);
            }
            g2.drawRect(eicas_gc.prim_dial_x[pos]-eicas_gc.dial_font_w[num]*18/10, epr_y-eicas_gc.dial_font_h[num]*12/10, eicas_gc.dial_font_w[num]*36/10, eicas_gc.dial_font_h[num]*120/100);
            g2.drawString("REV", eicas_gc.prim_dial_x[pos]-eicas_gc.dial_font_w[num]*16/10, epr_y-eicas_gc.dial_font_h[num]*15/100-2);
        }

        // target N1 bug not for reverse
        if ( (rev==0.0f) && engine_on) {
            float ref_n1 = this.aircraft.get_ref_N1(pos);
            if ( ref_n1 > 0.0f ) {
            	if ( ref_n1 <= 1.0f ) {
            		// logger.warning("UFMC N1 is probably ratio, not percent");
            		ref_n1 *= 100.0f;
            	}
            	float ref_n1_dial = Math.min(ref_n1, 110.0f) / 100.0f;
                g2.setColor(eicas_gc.ecam_caution_color);
                g2.rotate(Math.toRadians(Math.round(ref_n1_dial*deg_dial_range)-deg_start), eicas_gc.prim_dial_x[pos], epr_y);
                g2.drawLine(eicas_gc.prim_dial_x[pos]+epr_r-epr_r/10, epr_y, eicas_gc.prim_dial_x[pos]+epr_r+epr_r/10, epr_y);
                g2.setTransform(original_at);
            }
        }
        resetPen(g2);
    }


    private void drawN1(Graphics2D g2, int pos, int num, int line) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float n1_value = this.aircraft.get_N1(pos);    
        float throttle_dial = this.aircraft.get_throttle(pos);
        float n1_dial = Math.min(n1_value-20.0f, 90.0f) / 80.0f;
        n1_dial = Math.max(0.0f, n1_dial);
        float n2_value = this.aircraft.get_N2(pos);       
        boolean engine_on = (n1_value > 1.0f) || (n2_value > 1.0f);
        boolean draw_box = (line != 3);
        
        int n1_y = line==1 ? eicas_gc.dial_main1_y : (line==2 ? eicas_gc.dial_main2_y : eicas_gc.dial_main3_y);
        int n1_r = eicas_gc.dial_r[num];
        int n1_box_y = n1_y - n1_r/8;
        
        int deg_arc_start = 215;
        int deg_start = 210;
        int deg_caution = 10;
        int deg_warning = -10;
        if (line==3) {
        	deg_caution = 35;
        	deg_warning = 20;
        }
        int deg_arc_range = deg_arc_start - deg_caution;
        // int deg_norm_range = deg_start - deg_caution;
        int deg_dial_range = deg_start - deg_caution - 10;
        int deg_warn_range = deg_caution - deg_warning;
        int deg_throttle_start = deg_start;
        int deg_throttle_range = deg_throttle_start - deg_caution - 10;

        // scale markings every 1/8, starting from N1=20%
        if (engine_on) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.rotate(Math.toRadians(360-deg_start), eicas_gc.prim_dial_x[pos], n1_y);
        	for (int i=0; i<=8; i++) {
        		if (i==0 || (i>2 && draw_box) || (i%2==0 && !draw_box)) g2.drawLine(eicas_gc.prim_dial_x[pos]+n1_r*18/20, n1_y, eicas_gc.prim_dial_x[pos]+n1_r-1, n1_y);
        		g2.rotate(Math.toRadians(deg_dial_range/8), eicas_gc.prim_dial_x[pos], n1_y);
        	}
        	g2.setTransform(original_at);
        }
        
        // N1 : scale number 5 and 10, EPR : scale number 4 and 10
        if ( num <= 4 && engine_on) {       	
            g2.setFont(eicas_gc.font_s);
            int n1_digit_x;
            int n1_digit_y;
            int n1_digit_angle;
            if (draw_box) {
            	// 5
            	n1_digit_angle = 360-deg_start + deg_dial_range*3/8;
            	n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	g2.drawString("5", n1_digit_x - eicas_gc.digit_width_s/2, n1_digit_y+eicas_gc.line_height_s*3/8);
            	// 10
            	n1_digit_angle += deg_dial_range*5/8;
            	n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	g2.drawString("10", n1_digit_x - eicas_gc.digit_width_s, n1_digit_y+eicas_gc.line_height_s*3/8);            
            } else {
            	// 4
            	n1_digit_angle = 360-deg_start + deg_dial_range*2/8;
            	n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	g2.drawString("4", n1_digit_x - eicas_gc.digit_width_s/2, n1_digit_y+eicas_gc.line_height_s*3/8);
            	// 10
            	n1_digit_angle += deg_dial_range*6/8;
            	n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            	g2.drawString("10", n1_digit_x - eicas_gc.digit_width_s, n1_digit_y+eicas_gc.line_height_s*3/8);            

            }
        }

        // Gauge Arc
        g2.setColor(engine_on ? eicas_gc.ecam_markings_color : eicas_gc.ecam_caution_color);  
        g2.drawArc(eicas_gc.prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, deg_arc_start, -deg_arc_range);
        if (engine_on) {
        	g2.setColor(eicas_gc.ecam_warning_color);
        	original_stroke = g2.getStroke();
        	g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        	// N1 radius red arc 
        	int n1_r_red = n1_r * 98/100; 
        	g2.drawArc(eicas_gc.prim_dial_x[pos]-n1_r_red, n1_y-n1_r_red, 2*n1_r_red, 2*n1_r_red, deg_caution-2, -deg_warn_range);
        	g2.setStroke(original_stroke);
        }
        
        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);
        if (engine_on) {
        	g2.rotate(Math.toRadians(Math.round(n1_dial*deg_dial_range)-deg_start), eicas_gc.prim_dial_x[pos], n1_y);
        	g2.setColor(eicas_gc.ecam_normal_color);
        	g2.drawLine(eicas_gc.prim_dial_x[pos], n1_y, eicas_gc.prim_dial_x[pos]+n1_r*11/10, n1_y);
        	g2.setTransform(original_at);
        }
        resetPen(g2);

        // Throttle
        if (engine_on && (line == 1)) {
        	g2.rotate(Math.toRadians(Math.round(throttle_dial*deg_throttle_range)-deg_throttle_start), eicas_gc.prim_dial_x[pos], n1_y);
        	g2.setColor(eicas_gc.ecam_action_color);
        	g2.drawOval(eicas_gc.prim_dial_x[pos]+n1_r*11/10, n1_y-n1_r/16, n1_r/8, n1_r/8);
        	g2.setTransform(original_at);
        }
        
        // value box - present for A340 CRT, none for A340 LCD
       	// Don't draw the box when more than 4 engines
        if (engine_on && draw_box) {
        	if ( num < 4 ) {     
        		g2.setColor(eicas_gc.ecam_markings_color);
        		drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num]*2, n1_box_y + eicas_gc.dial_font_h[num]*45/100,
        				eicas_gc.dial_font_w[num]*49/10, eicas_gc.dial_box_h[num]);
        	} else if (num == 4) {
        		g2.setColor(eicas_gc.ecam_markings_color);
        		drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num], n1_box_y + eicas_gc.dial_font_h[num]*80/100,
        				eicas_gc.dial_font_w[num]*49/10, eicas_gc.dial_box_h[num]);        	
        	}
        }

        // Value color
        if ( ( n1_dial <= 1.0f ) || this.inhibit ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else if ( n1_dial < 1.1f ) {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	g2.setColor(eicas_gc.ecam_warning_color);
        }
        
        // Value, decimal part in smaller font
        if (engine_on) {
        	if ( num < 4 ) {
        		drawStringSmallOneDecimal(g2,
        				eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num]*2 +eicas_gc.dial_font_w[num]*47/10,
        				n1_box_y+eicas_gc.dial_font_h[num]*140/100,
        				eicas_gc.dial_font[num], eicas_gc.dial_font_s[num], n1_value);
        	} else {
        		drawStringSmallOneDecimal(g2,
        				eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] +eicas_gc.dial_font_w[num]*47/10,
        				n1_box_y+eicas_gc.dial_font_h[num]*180/100,
        				eicas_gc.dial_font[num], eicas_gc.dial_font_s[num], n1_value);       	
        	}
        } else {
        	g2.setColor(eicas_gc.ecam_caution_color);
        	g2.setFont(eicas_gc.dial_font[num]);
            g2.drawString("XX", eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] + eicas_gc.dial_font_w[num]*14/10,
            		n1_box_y+eicas_gc.dial_font_h[num]*140/100);	
        	
        }

        
        // Reverser
        float rev = this.aircraft.reverser_position(pos);
        if ( rev > 0.0f && engine_on && (line == 1)) {
            if ( rev == 1.0f ) {
                g2.setColor(eicas_gc.ecam_normal_color);
            } else {
                g2.setColor(eicas_gc.ecam_caution_color);
            }
            g2.drawRect(eicas_gc.prim_dial_x[pos]-eicas_gc.dial_font_w[num]*18/10, n1_y-eicas_gc.dial_font_h[num]*12/10, eicas_gc.dial_font_w[num]*36/10, eicas_gc.dial_font_h[num]*120/100);
            g2.drawString("REV", eicas_gc.prim_dial_x[pos]-eicas_gc.dial_font_w[num]*16/10, n1_y-eicas_gc.dial_font_h[num]*15/100-2);
        }

        
        // target N1 bug not for reverse
        if ( (rev==0.0f) && engine_on ) {
            float ref_n1 = this.aircraft.get_ref_N1(pos);
            if ( ref_n1 > 0.0f ) {
            	if ( ref_n1 <= 1.0f ) {
            		// logger.warning("UFMC N1 is probably ratio, not percent");
            		ref_n1 *= 100.0f;
            	}
            	float ref_n1_dial = Math.min(ref_n1, 110.0f) / 100.0f;
            	ref_n1_dial = Math.max(0.2f, ref_n1_dial);
                g2.setColor(Color.yellow);
                g2.rotate(Math.toRadians(Math.round(ref_n1_dial*deg_dial_range)-deg_start), eicas_gc.prim_dial_x[pos], n1_y);
                g2.drawLine(eicas_gc.prim_dial_x[pos]+n1_r-n1_r/7, n1_y, eicas_gc.prim_dial_x[pos]+n1_r+n1_r*22/100, n1_y);
                g2.fillRect(eicas_gc.prim_dial_x[pos]+n1_r, n1_y, n1_r/4, n1_r/7);
                g2.setTransform(original_at);
            }
        }

        resetPen(g2);

    }

    private void drawMAP(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float mpr_value = this.aircraft.get_MPR(pos);
        // Dial start at +5
        float mpr_dial = Math.min(mpr_value-5, 44.0f) / 40.0f;
        mpr_dial = Math.max(mpr_dial, 0.03f);
        boolean mpr_alive = mpr_value > 0;

        // Outside static pressure - max Manifold pressure without turbo
        float alt_meter = this.aircraft.altitude_ind() / 3.2808f;
        float isa_kelvin = 273.15f + this.aircraft.isa();
        float osp_hpa = 1013.25f * (float) Math.pow(  1.0f - (0.0065f*alt_meter/isa_kelvin), 5.25588f) ;
        float osp_inhg = osp_hpa * 0.029528744f; //  29.92f / 1013.25f
        float ref_map_dial = Math.min(osp_inhg-5, 44.0f) / 40.0f;
        
        float throttle_value = this.aircraft.get_throttle(pos)*100.0f;
        float throttle_dial = Math.min(throttle_value, 110.0f) / 100.0f;        
        
        int n1_y = eicas_gc.dial_main1_y;
        int n1_r = eicas_gc.dial_r[num];
        int n1_box_y = n1_y - n1_r/8;

        int deg_start = 225;
        int deg_caution = 25;
        int deg_warning = 0;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;
        // Throttle start at 10 In Hg
        int deg_throttle_start = deg_start - 15;
        // int deg_throttle_range = deg_throttle_start - deg_caution - 25;
        int deg_throttle_range = Math.round((deg_throttle_start - deg_caution )*ref_map_dial);

        
        if ( ( mpr_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( mpr_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }

        // Gauge Marks
        // 8 marks, 7 sectors
        if (mpr_alive) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.rotate(Math.toRadians(360-deg_start), eicas_gc.prim_dial_x[pos], n1_y);
        	for (int i=0; i<8; i++) {
        		g2.drawLine(eicas_gc.prim_dial_x[pos]+n1_r*18/20, n1_y, eicas_gc.prim_dial_x[pos]+n1_r-1, n1_y);
        		g2.rotate(Math.toRadians(deg_norm_range/7), eicas_gc.prim_dial_x[pos], n1_y);
        	}
        	g2.setTransform(original_at);
        }
        
        // Gauge marks digits
        if ( num <= 4 && mpr_alive) {
            g2.setFont(eicas_gc.font_s);
            int n1_digit_x;
            int n1_digit_y;
            int n1_digit_angle = 360-deg_start;
            // 5
            n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            g2.drawString("5", n1_digit_x - eicas_gc.digit_width_s/2, n1_digit_y+eicas_gc.line_height_s*3/8);
            // 20
            n1_digit_angle += 3*deg_norm_range/7;
            n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            g2.drawString("20", n1_digit_x - eicas_gc.digit_width_s/2, n1_digit_y+eicas_gc.line_height_s*3/8);
            n1_digit_angle += 4*deg_norm_range/7;
            // 40
            n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            n1_digit_y = n1_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*n1_r*11/16);
            g2.drawString("40", n1_digit_x - eicas_gc.digit_width_s, n1_digit_y+eicas_gc.line_height_s*3/8);
        }  
        
        // Gauge Arc
        g2.setColor(mpr_alive ? eicas_gc.ecam_markings_color : eicas_gc.ecam_caution_color);
        g2.drawArc(eicas_gc.prim_dial_x[pos]-n1_r, n1_y-n1_r, 2*n1_r, 2*n1_r, deg_start, -deg_norm_range);
        // N1 radius red arc 
        if (mpr_alive) {
        	g2.setColor(eicas_gc.ecam_warning_color);
        	original_stroke = g2.getStroke();
        	g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        	int n1_r_red = n1_r * 98/100; 
        	g2.drawArc(eicas_gc.prim_dial_x[pos]-n1_r_red, n1_y-n1_r_red, 2*n1_r_red, 2*n1_r_red, deg_caution-2, -deg_warn_range);
        	g2.setStroke(original_stroke);
        }
        
        // Throttle
        if (mpr_alive) {
        	g2.rotate(Math.toRadians(Math.round(throttle_dial*deg_throttle_range)-deg_throttle_start), eicas_gc.prim_dial_x[pos], n1_y);
        	g2.setColor(eicas_gc.ecam_action_color);
        	g2.drawOval(eicas_gc.prim_dial_x[pos]+n1_r*11/10, n1_y, n1_r/8, n1_r/8);
        	g2.setTransform(original_at);
        }
      
        // Needle
        resetPen(g2);
        if (mpr_alive) {
        	scalePen(g2,3.0f);
        	g2.rotate(Math.toRadians(Math.round(mpr_dial*deg_norm_range)-deg_start), eicas_gc.prim_dial_x[pos], n1_y);
        	g2.setColor(eicas_gc.ecam_normal_color);
        	g2.drawLine(eicas_gc.prim_dial_x[pos], n1_y, eicas_gc.prim_dial_x[pos]+n1_r*11/10, n1_y);
        	g2.setTransform(original_at);
        	resetPen(g2);
        }

        // value box 
       	// Don't draw the box when more than 4 engines
        if ( num < 5 && mpr_alive) {     
            g2.setColor(eicas_gc.ecam_markings_color);
            drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num], n1_box_y + eicas_gc.dial_font_h[num]*45/100,
            		    eicas_gc.dial_font_w[num]*41/10, eicas_gc.dial_box_h[num]);
        } 

        // Value Color
        if ( ( mpr_dial <= 1.0f ) || this.inhibit ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else if ( mpr_dial < 1.1f ) {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	g2.setColor(eicas_gc.ecam_warning_color);
        }
        
        // Value, decimal part in smaller font
        if ( num < 5 ) {
        	if (mpr_alive) {
        		drawStringSmallOneDecimal(g2,
        				eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] + eicas_gc.dial_font_w[num]*39/10,
        				n1_box_y+eicas_gc.dial_font_h[num]*140/100,
        				eicas_gc.dial_font[num], eicas_gc.dial_font_s[num], mpr_value);
        	} else {
        		g2.setColor(eicas_gc.ecam_caution_color);
        		g2.setFont(eicas_gc.dial_font[num]);
        		g2.drawString("XX", eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] + eicas_gc.dial_font_w[num]*19/10, n1_box_y+eicas_gc.dial_font_h[num]*140/100);
        	}
        } 

        // Reverser
        float rev = this.aircraft.reverser_position(pos);

        if ( rev > 0.0f ) {
            if ( rev == 1.0f ) {
                g2.setColor(eicas_gc.ecam_normal_color);
            } else {
                g2.setColor(eicas_gc.ecam_caution_color);
            }
            g2.drawString("REV", eicas_gc.prim_dial_x[pos]+eicas_gc.dial_font_w[num], n1_box_y-eicas_gc.dial_font_h[num]*165/100-2);
        }

        // target MAP bug not for reverse
        if ( rev==0.0f && mpr_alive) {
        	g2.setColor(Color.yellow);
        	g2.rotate(Math.toRadians(Math.round(ref_map_dial*deg_norm_range)-deg_start), eicas_gc.prim_dial_x[pos], n1_y);
        	g2.drawLine(eicas_gc.prim_dial_x[pos]+n1_r-n1_r/7, n1_y, eicas_gc.prim_dial_x[pos]+n1_r+n1_r*22/100, n1_y);
        	g2.fillRect(eicas_gc.prim_dial_x[pos]+n1_r, n1_y, n1_r/4, n1_r/7);
        	g2.setTransform(original_at);
        }

        resetPen(g2);

    }

    private void drawTRQ(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float trq_value = Math.max(this.aircraft.get_TRQ_LbFt(pos), 0.0f);
        float trq_max = this.aircraft.get_max_TRQ_LbFt();
        float trq_dial = Math.min(trq_value/trq_max, 1.1f);

        int deg_start = 225;
        int deg_caution = 20;
        int deg_warning = 0;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;

        
        String trq_str;
        if ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_NM ) {
            // display TRQ in Nm x100
            trq_str = Integer.toString(Math.round(Math.max(this.aircraft.get_TRQ_Nm(pos), 0.0f)));
        } else if ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_PERCENT ) {
            // display TRQ in %
            trq_str = one_decimal_format.format(trq_value/trq_max*100.0f);
        } else /* if ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_LBFT ) */ {
            // display TRQ in LbFt x100
            trq_str = Integer.toString(Math.round(trq_value));
        }
        
        int trq_y = eicas_gc.dial_main1_y;
        int trq_r = eicas_gc.dial_r[num];

        if ( trq_dial <= 1.0f ) {
            g2.setColor(eicas_gc.instrument_background_color);
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }

        // Gauge Marks
        // 11 marks, 10 sectors
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.rotate(Math.toRadians(360-deg_start), eicas_gc.prim_dial_x[pos], trq_y);
        for (int i=0; i<11; i++) {
            g2.drawLine(eicas_gc.prim_dial_x[pos]+trq_r*18/20, trq_y, eicas_gc.prim_dial_x[pos]+trq_r-1, trq_y);
            g2.rotate(Math.toRadians(deg_norm_range/10), eicas_gc.prim_dial_x[pos], trq_y);
        }
        g2.setTransform(original_at);
       
        
        // scale numbers 2, 4, 6, 8 and 10 for the scale in %
        if ( ( this.avionics.get_trq_scale() == XHSISettings.TRQ_SCALE_PERCENT ) && ( num <= 4 ) ) {
            g2.setFont(eicas_gc.font_xs);
            int n1_digit_x;
            int n1_digit_y;
            int n1_digit_angle = 360-deg_start + deg_norm_range/5;
            for (int i=2; i<=10; i+=2) {
                n1_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(n1_digit_angle))*trq_r*11/16);
                n1_digit_y = trq_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*trq_r*11/16);
                g2.drawString(Integer.toString(i), n1_digit_x - eicas_gc.digit_width_xs/2, n1_digit_y+eicas_gc.line_height_xs*3/8);
                n1_digit_angle += deg_norm_range/5;
            }
        }

        
        // Gauge Arc
        g2.drawArc(eicas_gc.prim_dial_x[pos]-trq_r, trq_y-trq_r, 2*trq_r, 2*trq_r, deg_start, -deg_norm_range);
        g2.setColor(eicas_gc.ecam_warning_color);
        original_stroke = g2.getStroke();
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        // N1 radius red arc 
        int trq_r_red = trq_r * 98/100; 
        g2.drawArc(eicas_gc.prim_dial_x[pos]-trq_r_red, trq_y-trq_r_red, 2*trq_r_red, 2*trq_r_red, deg_caution-3, -deg_warn_range);
        g2.setStroke(original_stroke);


        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);
        g2.rotate(Math.toRadians(Math.round(trq_dial*deg_norm_range)-deg_start), eicas_gc.prim_dial_x[pos], trq_y);
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.drawLine(eicas_gc.prim_dial_x[pos], trq_y, eicas_gc.prim_dial_x[pos]+trq_r*11/10, trq_y);
        g2.setTransform(original_at);
        resetPen(g2);
        
        // value box 
       	// Don't draw the box when more than 4 engines
        int trq_box_y = trq_y - trq_r/8;
        if ( num < 5 ) {     
            g2.setColor(eicas_gc.ecam_markings_color);
            drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num], trq_box_y + eicas_gc.dial_font_h[num]*45/100,
            		    eicas_gc.dial_font_w[num]*49/10, eicas_gc.dial_box_h[num]);
        } 
        
        // Value
        //String trq_str = Integer.toString(Math.round(trq_value));
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.setFont(eicas_gc.dial_font[num]);
        if ( num < 5 ) {
        	g2.drawString(trq_str,
        		eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num] +eicas_gc.dial_font_w[num]*47/10 -eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], trq_str),
        		trq_box_y +eicas_gc.dial_font_h[num]*140/100);        	
        } 
        

        // Prop mode
        int prop_mode = this.aircraft.get_prop_mode(pos);
        String prop_mode_str = "";
        if ( prop_mode != 1 ) {
            if ( prop_mode == 0 ) {
                g2.setColor(eicas_gc.ecam_warning_color);
                prop_mode_str = "FTHR";
            } else if ( prop_mode == 2 ) {
                g2.setColor(eicas_gc.ecam_caution_color);
                prop_mode_str = "BETA";
            } else if ( prop_mode == 3 ) {
                g2.setColor(eicas_gc.ecam_normal_color);
                prop_mode_str = "REV";
            }
            g2.drawString(prop_mode_str, eicas_gc.prim_dial_x[pos]+eicas_gc.dial_font_w[num], trq_y-eicas_gc.dial_font_h[num]*165/100-2);
        }

        resetPen(g2);

    }

    
    private void drawEGT(Graphics2D g2, int pos, int num, int line) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float egt_max = this.aircraft.get_EGT_max();
        // If EGT Max not defined or inconsistent, fix it to 900°c
        if (egt_max < 100.0f) egt_max=900.0f;
        float egt_range = egt_max < 400.0f && egt_max > 1.0f ? 400 : (float) (Math.ceil((egt_max + 100)/200)*200);
        int egt_value = Math.round(this.aircraft.get_EGT_value(pos));
        float egt_dial = Math.min(this.aircraft.get_EGT_value(pos), egt_range) / egt_range;
    	float max_egt_dial = Math.min(egt_max, egt_range) / egt_range;
    	
        float n2_value = this.aircraft.get_N2(pos);       
        float n1_value = this.aircraft.get_N1(pos);
        float rpm_value = this.aircraft.get_prop_RPM(pos);
        boolean engine_on = (n1_value > 1.0f) || (n2_value > 1.0f) || (rpm_value > 1.0f);
        boolean full_arc = (line == 2) && (num<3);

        int egt_x = eicas_gc.prim_dial_x[pos];
        int egt_y;
        switch (line) {
        	case 1 : egt_y = eicas_gc.dial_main1_y; break;
        	case 2 : egt_y = eicas_gc.dial_main2_y; break;
        	case 3 : egt_y = eicas_gc.dial_main3_y; break;
        	case 4 : egt_y = eicas_gc.dial_main4_y; break;
        	case 5 : egt_y = eicas_gc.dial_main5_y; break;
        	case 6 : egt_y = eicas_gc.dial_main6_y; break;
        	default: egt_y = eicas_gc.dial_main3_y; break;
        }       
        int egt_r = eicas_gc.dial_r[num];
        
        int deg_start = full_arc ? 225 : 180;
        int deg_warning = full_arc ? 25 : 0;
        int deg_full_range = deg_start-deg_warning;
        int deg_caution = deg_start-Math.round(max_egt_dial*deg_full_range)-10;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;


        if ( ( egt_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( egt_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        
        // EGT Gauge Arc
        g2.setColor(engine_on ? eicas_gc.ecam_markings_color : eicas_gc.ecam_caution_color);  
        g2.drawArc(egt_x-egt_r, egt_y-egt_r, 2*egt_r, 2*egt_r, deg_start, -deg_norm_range);
        if (engine_on) {
        	g2.setColor(eicas_gc.ecam_warning_color);
        	// EGT radius red arc 
        	original_stroke = g2.getStroke();
        	g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        	int egt_r_red = egt_r * 98/100; 
        	g2.drawArc(egt_x-egt_r_red, egt_y-egt_r_red, 2*egt_r_red, 2*egt_r_red, deg_caution, -deg_warn_range);
        	g2.setTransform(original_at);
        	g2.setStroke(original_stroke);
        }

        // scale markings every 50%
        if (engine_on) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.rotate(Math.toRadians(360-deg_start), eicas_gc.prim_dial_x[pos], egt_y);
        	for (int i=0; i<2; i++) {
        		g2.drawLine(eicas_gc.prim_dial_x[pos]+egt_r*18/20, egt_y, eicas_gc.prim_dial_x[pos]+egt_r-1, egt_y);
        		g2.rotate(Math.toRadians(deg_full_range/2), eicas_gc.prim_dial_x[pos], egt_y);
        	}
        	g2.setTransform(original_at);
        }

        // EGT : scale number
        String mid_str = ""+Math.round(egt_range/200);
        String max_str = ""+Math.round(egt_range/100);
        if ( num <= 4 && engine_on) {
            g2.setFont(eicas_gc.font_s);
            g2.setColor(eicas_gc.ecam_markings_color);
            int egt_digit_x;
            int egt_digit_y;
            int egt_digit_angle = 360-deg_start + deg_full_range/2;
            // mid
            egt_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(egt_digit_angle))*egt_r*11/16);
            egt_digit_y = egt_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*egt_r*11/16);
            g2.drawString(mid_str, egt_digit_x - eicas_gc.digit_width_s/2, egt_digit_y+eicas_gc.line_height_s*3/8);
            egt_digit_angle += deg_full_range/2;
            // max
            egt_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(egt_digit_angle))*egt_r*12/16);
            egt_digit_y = egt_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*egt_r*12/16);
            g2.drawString(max_str, egt_digit_x - eicas_gc.get_text_width(g2, eicas_gc.font_s, max_str)/2, egt_digit_y+eicas_gc.line_height_s*3/8);
        }
        
        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);
        if (engine_on) {
        	int needle_x = full_arc ? egt_x : egt_x+egt_r/2;
        	g2.rotate(Math.toRadians(Math.round(egt_dial*deg_full_range)-deg_start), eicas_gc.prim_dial_x[pos], egt_y);
        	if ((egt_value < egt_max) || this.inhibit)
        		g2.setColor(eicas_gc.ecam_normal_color);
        	else
        		g2.setColor(eicas_gc.ecam_caution_color);
        	g2.drawLine(needle_x, egt_y, egt_x+egt_r*11/10, egt_y);
        	g2.setTransform(original_at);
        }
             
        // EGT MAX
        if (engine_on) {
        	g2.setColor(Color.yellow);
        	g2.rotate(Math.toRadians(Math.round(max_egt_dial*deg_full_range)-deg_start), eicas_gc.prim_dial_x[pos], egt_y);
        	g2.fillRect(eicas_gc.prim_dial_x[pos]+egt_r, egt_y, egt_r/4, egt_r/7);
        	/* Draw Max EGT Value (debug)
        	g2.setFont(eicas_gc.font_s);
        	g2.drawString(""+Math.round(egt_max), eicas_gc.prim_dial_x[pos]+egt_r, egt_y);
        	*/
        	g2.setTransform(original_at);
        }
        
        // value box
        egt_y -= egt_r/8;       
        int egt_txt_y = full_arc ? egt_y+eicas_gc.dial_font_h[num]*180/100 : egt_y+eicas_gc.dial_font_h[num]*52/100;
        if ( num < 5 ) {
        	// Don't draw the box when more than 4 engines
            g2.setColor(eicas_gc.ecam_markings_color);
            if (egt_range>950) {
            	drawDoubleRect(g2, egt_x - eicas_gc.dial_font_w[num]*26/10, egt_txt_y - eicas_gc.dial_font_h[num]*95/100, 
            		    eicas_gc.dial_font_w[num]*48/10, eicas_gc.dial_box_h[num]);           	
            } else {
            	drawDoubleRect(g2, egt_x - eicas_gc.dial_font_w[num]*16/10, egt_txt_y - eicas_gc.dial_font_h[num]*95/100, 
            		    eicas_gc.dial_font_w[num]*38/10, eicas_gc.dial_box_h[num]);
            }
        }
        if ( ( egt_value < egt_max ) || this.inhibit ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } 
        
        if (engine_on) {
            g2.setFont(eicas_gc.dial_font[num]);
            String egt_str = Integer.toString(egt_value);
            g2.drawString(egt_str, egt_x + eicas_gc.dial_font_w[num]*19/10 - eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], egt_str),
            		egt_txt_y);
        } else {
        	g2.setColor(eicas_gc.ecam_caution_color);
        	g2.setFont(eicas_gc.dial_font[num]);
            g2.drawString("XX", egt_x + eicas_gc.dial_font_w[num]*19/10 - eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], "XX"),
            		egt_txt_y);	
        }
        
        resetPen(g2);
    }
    
    private void drawITT(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float itt_percent = this.aircraft.get_ITT_percent(pos);
        float itt_dial = Math.min(itt_percent, 110.0f) / 100.0f;
        int itt_value = Math.round(this.aircraft.get_ITT_value(pos));

        int itt_x = eicas_gc.prim_dial_x[pos];
        int itt_y = eicas_gc.dial_main3_y - eicas_gc.dial_r[num] * 15 / 100;
        int itt_r = eicas_gc.dial_r[num] * 85 / 100;

        int deg_start = 180;
        int deg_caution = 25;
        int deg_warning = 0;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;
        
        // Gauge Arc
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.drawArc(itt_x-itt_r, itt_y-itt_r, 2*itt_r, 2*itt_r, deg_start, -deg_norm_range);
        g2.setColor(eicas_gc.ecam_warning_color);
        original_stroke = g2.getStroke();
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        // ITT radius red arc 
        int itt_r_red = itt_r * 98/100; 
        g2.drawArc(eicas_gc.prim_dial_x[pos]-itt_r_red, itt_y-itt_r_red, 2*itt_r_red, 2*itt_r_red, deg_caution-2, -deg_warn_range);
        g2.setStroke(original_stroke);
      
        // Needle
        resetPen(g2);
        scalePen(g2,3.0f);
        g2.rotate(Math.toRadians(Math.round(itt_dial*deg_norm_range)-deg_start), eicas_gc.prim_dial_x[pos], itt_y);
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.drawLine(itt_x+itt_r/2, itt_y, itt_x+itt_r-2, itt_y);
        g2.setTransform(original_at);
        resetPen(g2);
        
        // value box
        itt_y -= itt_r/8;       
        if ( num < 3 ) {
        	// Don't draw the box when more than 4 engines
            g2.setColor(eicas_gc.ecam_markings_color);
            drawDoubleRect(g2, itt_x - eicas_gc.dial_font_w[num]*22/10, itt_y - eicas_gc.dial_font_h[num]*45/100, 
            		    eicas_gc.dial_font_w[num]*47/10, eicas_gc.dial_box_h[num]);
        }
        if ( ( itt_dial <= 1.0f ) || this.inhibit ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else if ( itt_dial < 1.1f ) {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	g2.setColor(eicas_gc.ecam_warning_color);
        }
        g2.setFont(eicas_gc.dial_font[num]);
        String egt_str = Integer.toString(itt_value);
        g2.drawString(egt_str, itt_x - eicas_gc.dial_font_w[num]*22/10 +eicas_gc.dial_font_w[num]*44/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], egt_str),
        		itt_y+eicas_gc.dial_font_h[num]*52/100);
        
        resetPen(g2);
    }
    

    private void drawN2(Graphics2D g2, int pos, int num, int line) {

    	// On Airbus, N2 is always displayed as numbers, no gauges nor needles.
    	// decimals are in smaller fonts
        scalePen(g2, 2.2f);

        float n2_value = this.aircraft.get_N2(pos);
        float n1_value = this.aircraft.get_N1(pos);
        boolean attn_box = (n1_value < 18.0f) && (n2_value > 1.0f);

        int n2_y = ( line == 3 ) ? eicas_gc.dial_main3_y :eicas_gc.dial_main4_y;
        int n2_r = eicas_gc.dial_r[num];
        String n2_str;

        // value box
        n2_y -= n2_r/8;
        if (attn_box) {
        	g2.setColor(eicas_gc.pfd_instrument_background_color);
        	g2.fillRect(eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num]*25/10, n2_y-eicas_gc.dial_font_h[num], eicas_gc.dial_font_w[num]*5, eicas_gc.dial_font_h[num]*13/10);
        }
        
        g2.setColor(eicas_gc.ecam_normal_color);
        
        if ( n2_value <= 1.0f ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_caution_color);
        	n2_str= "XX";
        	g2.drawString(n2_str,eicas_gc.prim_dial_x[pos],n2_y);
        } else {
        	g2.setColor(eicas_gc.ecam_normal_color);
            drawStringSmallOneDecimal(g2,         		
            		eicas_gc.prim_dial_x[pos]+eicas_gc.dial_font_w[num]*20/10,
            		n2_y,
            		eicas_gc.dial_font[num],
            		eicas_gc.dial_font_s[num],
            		n2_value);
        }

        resetPen(g2);
    }

    private void drawN1_V(Graphics2D g2, int pos, int num, int line) {

    	// Draw N1 value only
    	// decimals are in smaller fonts
        scalePen(g2, 2.2f);

        float n2_value = this.aircraft.get_N2(pos);
        float n1_value = this.aircraft.get_N1(pos);
        boolean attn_box = (n1_value < 18.0f) && (n2_value > 1.0f);

        int n1_y = ( line == 5 ) ? eicas_gc.dial_main5_y :eicas_gc.dial_main6_y;
        int n1_r = eicas_gc.dial_r[num];
        String n1_str;

        // value box
        n1_y -= n1_r/8;
        if (attn_box) {
        	g2.setColor(eicas_gc.pfd_instrument_background_color);
        	g2.fillRect(eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num]*25/10, n1_y-eicas_gc.dial_font_h[num], eicas_gc.dial_font_w[num]*5, eicas_gc.dial_font_h[num]*13/10);
        }
        
        g2.setColor(eicas_gc.ecam_normal_color);
        
        if ( n1_value <= 1.0f ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_caution_color);
        	n1_str= "XX";
        	g2.drawString(n1_str,eicas_gc.prim_dial_x[pos],n1_y);
        } else {
        	g2.setColor(eicas_gc.ecam_normal_color);
            drawStringSmallOneDecimal(g2,         		
            		eicas_gc.prim_dial_x[pos]+eicas_gc.dial_font_w[num]*20/10,
            		n1_y,
            		eicas_gc.dial_font[num],
            		eicas_gc.dial_font_s[num],
            		n1_value);
        }

        resetPen(g2);
    }

    private void drawPROP(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2,2.2f);

        float rpm_max = this.aircraft.get_max_prop_RPM();
        float rpm_value = this.aircraft.get_prop_RPM(pos);
        float rpm_dial = Math.min(rpm_value/rpm_max, 1.1f);
        boolean rpm_alive = rpm_value>1;
        
        int rpm_y = eicas_gc.dial_main2_y;
        int rpm_r = eicas_gc.dial_r[num];

        int deg_start = 225;
        int deg_caution = 10;
        int deg_warning = 0;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;
        
        // scale markings every 50%
        if (rpm_alive) {
        	g2.setColor(eicas_gc.ecam_markings_color);
        	g2.rotate(Math.toRadians(360-deg_start), eicas_gc.prim_dial_x[pos], rpm_y);
        	for (int i=0; i<2; i++) {
        		g2.drawLine(eicas_gc.prim_dial_x[pos]+rpm_r*18/20, rpm_y, eicas_gc.prim_dial_x[pos]+rpm_r-1, rpm_y);
        		g2.rotate(Math.toRadians(deg_norm_range/2), eicas_gc.prim_dial_x[pos], rpm_y);
        	}
        	g2.setTransform(original_at);

            String mid_str = ""+Math.round(rpm_max/200);
            String max_str = ""+Math.round(rpm_max/100);

            if ( num <= 4 ) {
                g2.setFont(eicas_gc.font_s);
                g2.setColor(eicas_gc.ecam_markings_color);
                int egt_digit_x;
                int egt_digit_y;
                int egt_digit_angle = 360-deg_start + deg_norm_range/2;
                // mid
                egt_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(egt_digit_angle))*rpm_r*11/16);
                egt_digit_y = rpm_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*rpm_r*11/16);
                g2.drawString(mid_str, egt_digit_x - eicas_gc.digit_width_s/2, egt_digit_y+eicas_gc.line_height_s*3/8);
                egt_digit_angle += deg_norm_range/2;
                // max
                egt_digit_x = eicas_gc.prim_dial_x[pos] + (int)(Math.cos(Math.toRadians(egt_digit_angle))*rpm_r*12/16);
                egt_digit_y = rpm_y + (int)(Math.sin(Math.toRadians(egt_digit_angle))*rpm_r*12/16);
                g2.drawString(max_str, egt_digit_x - eicas_gc.get_text_width(g2, eicas_gc.font_s, max_str), egt_digit_y+eicas_gc.line_height_s*3/8);
            }
        }
        
        // Gauge Arc
        g2.setColor(rpm_alive ? eicas_gc.ecam_markings_color : eicas_gc.ecam_caution_color);
        g2.drawArc(eicas_gc.prim_dial_x[pos]-rpm_r, rpm_y-rpm_r, 2*rpm_r, 2*rpm_r, deg_start, -deg_norm_range);
        // RPM radius red arc 
        if (rpm_alive) {
        	g2.setColor(eicas_gc.ecam_warning_color);
        	original_stroke = g2.getStroke();
        	g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        	int rpm_r_red = rpm_r * 98/100; 
        	g2.drawArc(eicas_gc.prim_dial_x[pos]-rpm_r_red, rpm_y-rpm_r_red, 2*rpm_r_red, 2*rpm_r_red, deg_caution-2, -deg_warn_range);
        	g2.setStroke(original_stroke);
        }
        

        // Needle
        resetPen(g2);
        if (rpm_alive) {
        	scalePen(g2,3.0f);
        	g2.rotate(Math.toRadians(Math.round(rpm_dial*deg_norm_range)-deg_start), eicas_gc.prim_dial_x[pos], rpm_y);
        	g2.setColor(eicas_gc.ecam_normal_color);
        	g2.drawLine(eicas_gc.prim_dial_x[pos], rpm_y, eicas_gc.prim_dial_x[pos]+rpm_r*11/10, rpm_y);
        	g2.setTransform(original_at);
        	resetPen(g2);
        }
        
        // value box 
       	// Don't draw the box when more than 4 engines
        if ( num < 5 && rpm_alive) {     
            g2.setColor(eicas_gc.ecam_markings_color);
            drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num]*2,
            		    eicas_gc.dial_main2_y + eicas_gc.dial_font_h[num]*45/100,
            		    eicas_gc.dial_font_w[num]*49/10, 
            		    eicas_gc.dial_box_h[num]);
        } 

        // Value color
        if ( rpm_dial <= 1.1f && rpm_alive) {
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } 

        // Value
        String rpm_str = Integer.toString(Math.round(rpm_value));   
        if (!rpm_alive) rpm_str="XX";
        g2.setFont(eicas_gc.dial_font[num]);
        if ( num < 5 ) {
        	g2.drawString(rpm_str,
        		eicas_gc.prim_dial_x[pos]- eicas_gc.dial_font_w[num]*2 +eicas_gc.dial_font_w[num]*47/10 -eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], rpm_str),
        		eicas_gc.dial_main2_y +eicas_gc.dial_font_h[num]*140/100);
        } 

        resetPen(g2);

    }


    private void drawFF(Graphics2D g2, int pos, int num, int row) {
    	
    	// Fuel Flow is only a text on the ECAM, without any box
    	// V1 : On EPR display, Fuel flow is displayed on the right panel
    	// V1 : On N1 display, Fuel flow is displayed on the left panel 
   	
        scalePen(g2, 2.2f);

        // convert FF from kg/s to kg/h, lbs/h, usg/h or ltr/h
        float unit_multiplier = this.aircraft.fuel_multiplier();
        float ff_value = this.aircraft.get_FF(pos) * 3600 * unit_multiplier;
    
        int ff_y = row==5 ? eicas_gc.dial_main5_y : eicas_gc.dial_main4_y;
        int ff_r = eicas_gc.dial_r[num] * 85 / 100;

        // value box
        // when in 4 engine config, engine 1 & 2 right align
        // when in 2 engine config, engine 1 right align
        // if engine is off, display amber "XX"
        ff_y -= ff_r/8;
                    
        g2.setFont(eicas_gc.dial_font[num]);
        String ff_str;
        if (ff_value < 1.0) {
        	// engine is off ? get N2 Value ?
        	ff_str = "XX";
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	ff_str = Integer.toString( Math.round(ff_value) );
        	g2.setColor(eicas_gc.ecam_normal_color);
        }
        g2.drawString(ff_str,
        		eicas_gc.prim_dial_x[pos] + eicas_gc.dial_font_w[num]*20/10 - eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], ff_str),
        		//            		ff_y-eicas_gc.dial_font_h[num]*25/100-2
        		ff_y
        		);
        

        resetPen(g2);
    }

    private void drawFF_EPR_V1(Graphics2D g2, int pos, int num) {
    	// V1 : On EPR display, Fuel flow is displayed on the right panel

        // convert FF from kg/s to kg/h, lbs/h, usg/h or ltr/h
        float unit_multiplier = this.aircraft.fuel_multiplier();
        float ff_value = this.aircraft.get_FF(pos) * 3600 * unit_multiplier;
   
        int ff_y = eicas_gc.ecam_v1_ff_value_y;
        int ff_x = (pos==0) ? eicas_gc.ecam_v1_ff_eng1_x :eicas_gc.ecam_v1_ff_eng2_x;

        // if engine is off, display amber "XX"              
        //g2.setFont(eicas_gc.dial_font[num]);
        g2.setFont(eicas_gc.font_xl);
        String ff_str;
        if (ff_value < 0.5) {
        	ff_str = "XX";
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	ff_str = Integer.toString( Math.round(ff_value) );
        	g2.setColor(eicas_gc.ecam_normal_color);
        }
        g2.drawString(ff_str, ff_x - eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], ff_str), ff_y);
    }

    
    private void drawNG(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float ng_value = this.aircraft.get_NG(pos);
        float ng_dial = Math.min(ng_value, 110.0f) / 100.0f;

        int ng_y = eicas_gc.dial_main4_y;
        int ng_r = eicas_gc.dial_r[num] * 85 / 100;
        
        int deg_start = 180;
        int deg_caution = 25;
        int deg_warning = 0;
        int deg_norm_range = deg_start-deg_caution;
        int deg_warn_range = deg_caution-deg_warning;
        
        if ( ( ng_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(eicas_gc.instrument_background_color);
        } else if ( ng_dial < 1.1f ) {
            g2.setColor(eicas_gc.caution_color.darker().darker());
        } else {
            g2.setColor(eicas_gc.warning_color.darker().darker());
        }
        

        // Gauge Arc
        g2.setColor(eicas_gc.ecam_markings_color);
        g2.drawArc(eicas_gc.prim_dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, deg_start, -deg_norm_range);
        g2.setColor(eicas_gc.ecam_warning_color);
        original_stroke = g2.getStroke();
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * eicas_gc.grow_scaling_factor ), new BasicStroke( 2.0f * eicas_gc.grow_scaling_factor ) ));
        // ITT radius red arc 
        int ng_r_red = ng_r * 98/100; 
        g2.drawArc(eicas_gc.prim_dial_x[pos]-ng_r_red, ng_y-ng_r_red, 2*ng_r_red, 2*ng_r_red, deg_caution-2, -deg_warn_range);
        g2.setStroke(original_stroke);
      
        // Needle
        resetPen(g2);
        scalePen(g2,4.0f);
        g2.rotate(Math.toRadians(Math.round(ng_dial*deg_norm_range)-deg_start), eicas_gc.prim_dial_x[pos], ng_y);
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.drawLine(eicas_gc.prim_dial_x[pos] + ng_r/2, ng_y, eicas_gc.prim_dial_x[pos] + ng_r - 2, ng_y);
        g2.setTransform(original_at);
        resetPen(g2);
              
        // no value box
        /*
        ng_y -= ng_r/8;       
        if ( num < 3 ) {
        	// Don't draw the box when more than 4 engines
            g2.setColor(eicas_gc.ecam_markings_color);
            drawDoubleRect(g2, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num]*22/10, ng_y - eicas_gc.dial_font_h[num]*60/100, 
            		    eicas_gc.dial_font_w[num]*47/10, eicas_gc.dial_box_h[num]);
        }
        */
        if ( ( ng_dial <= 1.0f ) || this.inhibit ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(eicas_gc.ecam_normal_color);
        } else if ( ng_dial < 1.1f ) {
        	g2.setColor(eicas_gc.ecam_caution_color);
        } else {
        	g2.setColor(eicas_gc.ecam_warning_color);
        }
        g2.setFont(eicas_gc.dial_font[num]);
        String ng_str = ""+ Math.round(ng_value);
        g2.drawString(ng_str, eicas_gc.prim_dial_x[pos] - eicas_gc.dial_font_w[num]*22/10 +eicas_gc.dial_font_w[num]*44/10-eicas_gc.get_text_width(g2, eicas_gc.dial_font[num], ng_str),
        		ng_y+eicas_gc.dial_font_h[num]*32/100);

        resetPen(g2);
    }


    private void drawOilP(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float oil_p_dial = this.aircraft.get_oil_press_ratio(pos);

        int oil_p_x = eicas_gc.seco_dial_x[pos];
        int oil_p_y = eicas_gc.dial_oil_p_y;
        int oil_p_r = eicas_gc.dial_r[num] * 70 /100;

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(oil_p_x-oil_p_r, oil_p_y-oil_p_r, 2*oil_p_r, 2*oil_p_r, -30-90, -300+90);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(oil_p_x-oil_p_r, oil_p_y-oil_p_r, 2*oil_p_r, 2*oil_p_r, -30-45, -45);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(oil_p_x-oil_p_r, oil_p_y-oil_p_r, 2*oil_p_r, 2*oil_p_r, -30, -45);

        // needle
        g2.rotate(Math.toRadians( Math.round(oil_p_dial*300.0f) + 30 ), oil_p_x, oil_p_y);
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.drawLine(oil_p_x, oil_p_y, oil_p_x+oil_p_r-2, oil_p_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void drawOilT(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float oil_t_dial = this.aircraft.get_oil_temp_ratio(pos);

        int oil_t_x = eicas_gc.seco_dial_x[pos];
        int oil_t_y = eicas_gc.dial_oil_t_y;
        int oil_t_r = eicas_gc.dial_r[num] * 70 /100;

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(oil_t_x-oil_t_r, oil_t_y-oil_t_r, 2*oil_t_r, 2*oil_t_r, -45, -180);
        g2.setColor(eicas_gc.caution_color);
        g2.drawArc(oil_t_x-oil_t_r, oil_t_y-oil_t_r, 2*oil_t_r, 2*oil_t_r, -45-180, -15);
        g2.setColor(eicas_gc.warning_color);
        g2.drawArc(oil_t_x-oil_t_r, oil_t_y-oil_t_r, 2*oil_t_r, 2*oil_t_r, -45-180-15, -45);

        // needle
        g2.rotate(Math.toRadians( Math.round(oil_t_dial*240.0f) + 45), oil_t_x, oil_t_y);
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.drawLine(oil_t_x, oil_t_y, oil_t_x+oil_t_r-2, oil_t_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


    private void drawOilQ(Graphics2D g2, int pos) {

        int oil_q_val = Math.round( this.aircraft.get_oil_quant_ratio(pos) * 100.0f );
        String oil_q_str = "" + oil_q_val;

        int oil_q_x = eicas_gc.seco_dial_x[pos] - eicas_gc.get_text_width(g2, eicas_gc.font_l, oil_q_str)/2;
        int oil_q_y = eicas_gc.dial_oil_q_y + eicas_gc.line_height_l/2;

        g2.setColor(eicas_gc.markings_color);
        g2.setFont(eicas_gc.font_l);
        g2.drawString(oil_q_str, oil_q_x, oil_q_y);

    }


    private void drawVIB(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2, 2.2f);

        float vib_dial = this.aircraft.get_vib(pos) / 100.0f;

        int vib_x = eicas_gc.seco_dial_x[pos];
        int vib_y = eicas_gc.dial_vib_y;
        int vib_r = eicas_gc.dial_r[num] * 70 /100;

        g2.setColor(eicas_gc.instrument_background_color);
        g2.fillArc(vib_x-vib_r, vib_y-vib_r, 2*vib_r, 2*vib_r, -120, -Math.round(vib_dial*270.0f));

        g2.setColor(eicas_gc.dim_markings_color);
        g2.drawArc(vib_x-vib_r, vib_y-vib_r, 2*vib_r, 2*vib_r, -120, -270);

        // needle
        g2.rotate(Math.toRadians( Math.round(vib_dial*270.0f) + 120 ), vib_x, vib_y);
        g2.setColor(eicas_gc.ecam_normal_color);
        g2.drawLine(vib_x, vib_y, vib_x+vib_r-2, vib_y);
        g2.setTransform(original_at);

        resetPen(g2);

    }


//    private void drawCHT(Graphics2D g2, int pos, int num) {
//
//        AffineTransform original_at = g2.getTransform();
//        scalePen(g2);
//
//        float ng_dial = this.aircraft.get_NG(pos) / 100.0f;
////ng_dial = 80.0f / 100.0f;
//
//        int ng_x;
//        int ng_y;
//        int ng_r = eicas_gc.dial_r[num] * 70 /100;
//        if ( this.preferences.get_eicas_primary() ) {
//            // left column
//            ng_x = eicas_gc.prim_dial_x[pos];
//            ng_y = eicas_gc.dial_ng_y;
//        } else {
//            // right column
//            ng_x = eicas_gc.seco_dial_x[pos];
//            ng_y = eicas_gc.dial_vib_y;
//        }
//
//        if ( ng_dial <= 1.0f ) {
//            g2.setColor(eicas_gc.instrument_background_color);
//        } else {
//            g2.setColor(eicas_gc.warning_color.darker().darker());
//        }
//        g2.fillArc(ng_x-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -Math.round(ng_dial*200.0f));
//
//        g2.setColor(eicas_gc.dim_markings_color);
//        g2.drawArc(ng_x-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -200);
//        g2.setColor(eicas_gc.warning_color);
//        g2.drawArc(ng_x-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, -200, -20);
//        g2.rotate(Math.toRadians(200), ng_x, ng_y);
//        g2.drawLine(ng_x+ng_r, ng_y, ng_x+ng_r*19/16, ng_y);
//        g2.setTransform(original_at);
//
//        //needle
//        g2.rotate(Math.toRadians(Math.round(ng_dial*200.0f)), ng_x, ng_y);
//        g2.setColor(eicas_gc.markings_color);
//        g2.drawLine(ng_x, ng_y, ng_x+ng_r-2, ng_y);
//        g2.setTransform(original_at);
//
//        resetPen(g2);
//
//    }

    private void drawRefN1(Graphics2D g2, int pos, boolean with_epr) {
        int ref_n1_val = Math.round( this.aircraft.get_ref_N1(pos));        
        int ref_x = eicas_gc.ref_n1_x + eicas_gc.digit_width_xl*9;
        int ref_y = with_epr ? eicas_gc.ref_n1_epr_y : eicas_gc.ref_n1_y;
        int ref_underline_y = with_epr ? eicas_gc.ref_n1_epr_y + eicas_gc.line_height_l * 2/10 : eicas_gc.ref_n1_y + eicas_gc.line_height_l * 2/10;
        
        String mode_str= this.aircraft.get_thrust_mode();
        int mod_x = eicas_gc.ref_n1_x;
        int mod_y = with_epr ? eicas_gc.ref_n1_epr_y : eicas_gc.ref_n1_y;
 
        // N1 value
        if (ref_n1_val > 0.1f) {
        	g2.setColor(eicas_gc.ecam_normal_color);
        	if (with_epr) {
        		// drawStringSmallOneDecimal(g2, ref_x, ref_y, eicas_gc.font_xxl,eicas_gc.font_l, this.aircraft.get_ref_N1(pos));
        		// No EPR limit dataref available, let it blank 
        	} else {
        		drawStringSmallOneDecimal(g2, ref_x, ref_y, eicas_gc.font_xxl,eicas_gc.font_l, this.aircraft.get_ref_N1(pos));

        		// %
        		g2.setFont(eicas_gc.font_l);
        		g2.setColor(eicas_gc.ecam_action_color);
        		g2.drawString("%", ref_x+eicas_gc.digit_width_l/4, ref_y);       	
        	}
        }
        
        // Thrust mode
        if (!mode_str.equals("")) {
        	g2.setColor(eicas_gc.ecam_action_color);
        	g2.setFont(eicas_gc.font_l);
        	g2.drawString(mode_str, mod_x, mod_y);
        	g2.drawLine(mod_x, ref_underline_y, mod_x+ eicas_gc.get_text_width(g2, eicas_gc.font_l, mode_str), ref_underline_y);
        }
        
        // Flex temp
        if (mode_str.equals("FLX")) {
        	g2.setColor(eicas_gc.ecam_normal_color);
        	g2.setFont(eicas_gc.font_xl);
        	String str_temp_val = ""+this.avionics.qpac_flex_temp();
        	g2.drawString(str_temp_val, eicas_gc.ref_temp_x, ref_y);
            g2.setColor(eicas_gc.ecam_action_color);
            g2.setFont(eicas_gc.font_l);
            g2.drawString("°c", eicas_gc.ref_temp_x + eicas_gc.get_text_width(g2, eicas_gc.font_l, str_temp_val) +eicas_gc.digit_width_l/4, ref_y);
        }
    }

    private void drawRefMAP(Graphics2D g2) {
    	
        // Outside static pressure - max Manifold pressure without turbo
        float alt_meter = this.aircraft.altitude_ind() / 3.2808f;
        float isa_kelvin = 273.15f + this.aircraft.isa();
        float osp_hpa = 1013.25f * (float) Math.pow(  1.0f - (0.0065f*alt_meter/isa_kelvin), 5.25588f) ;
        float osp_inhg = osp_hpa * 0.029528744f; //  29.92f / 1013.25f
        String osp_str;
        String unit_str;

    	if ( this.avionics.pfd_show_baro_hpa() ) {
    		osp_str = ""+ Math.round(osp_hpa);
    		unit_str="HPa";
    	} else {
    		osp_str = two_decimals_format.format(osp_inhg);
    		unit_str = "In Hg";
    	}

        
        // String ref_n1_str = one_decimal_format.format(ref_n1_val);
        int ref_x = eicas_gc.ref_n1_x + eicas_gc.digit_width_xl*9;
        int ref_y = eicas_gc.ref_n1_y;
        int ref_underline_y = eicas_gc.ref_n1_y + eicas_gc.line_height_l * 2/10;
        
        String mode_str= "OSP";
        int mod_x = eicas_gc.ref_n1_x;
        int mod_y = eicas_gc.ref_n1_y;
 
        // N1 value
        g2.setColor(eicas_gc.ecam_normal_color);

        g2.setFont(eicas_gc.font_xl);
        g2.drawString(osp_str, ref_x- eicas_gc.get_text_width(g2, eicas_gc.font_xl, osp_str), ref_y);

        g2.setFont(eicas_gc.font_l);
        g2.setColor(eicas_gc.ecam_action_color);
        g2.drawString(unit_str, ref_x+eicas_gc.digit_width_l/4, ref_y);       	

        
        // Thrust mode
        g2.setColor(eicas_gc.ecam_action_color);
        g2.setFont(eicas_gc.font_l);
        g2.drawString(mode_str, mod_x, mod_y);
        g2.drawLine(mod_x, ref_underline_y, mod_x+ eicas_gc.get_text_width(g2, eicas_gc.font_l, mode_str), ref_underline_y);
        

    }

    
    private void drawStringSmallOneDecimal(Graphics2D g2, int x, int y, Font normalFont, Font smallFont, float value) {
    	// Value, decimal part in smaller font
    	// Justify Right
    	String valueStr =  one_decimal_format.format(value);
    	g2.setFont(normalFont);
    	String intStr = valueStr.substring(0, valueStr.length()-2);
    	String decStr = valueStr.substring(valueStr.length()-2,valueStr.length());
    	int len_n1_str1 = eicas_gc.get_text_width(g2, normalFont, intStr);
    	int len_n1_str2 = eicas_gc.get_text_width(g2, smallFont, decStr);
    	g2.drawString(intStr, x - len_n1_str2 - len_n1_str1, y);
    	g2.setFont(smallFont);
    	g2.drawString(decStr, x - len_n1_str2, y);
    }

    private void drawStringSmallThreeDecimals(Graphics2D g2, int x, int y, Font normalFont, Font smallFont, float value) {
    	// Value, decimal part in smaller font
    	// Justify Right
    	String valueStr =  three_decimals_format.format(value);
    	g2.setFont(normalFont);
    	String intStr = valueStr.substring(0, valueStr.length()-4);
    	String decStr = valueStr.substring(valueStr.length()-4,valueStr.length());
    	int len_n1_str1 = eicas_gc.get_text_width(g2, normalFont, intStr);
    	int len_n1_str2 = eicas_gc.get_text_width(g2, smallFont, decStr);
    	g2.drawString(intStr, x - len_n1_str2 - len_n1_str1, y);
    	g2.setFont(smallFont);
    	g2.drawString(decStr, x - len_n1_str2, y);
    }    
    
    private void drawDoubleRect(Graphics2D g2, int x, int y, int w, int h) {
        int rect_r = Math.round(4.0f * eicas_gc.scaling_factor);
        Stroke original_stroke;
        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(0.9f * eicas_gc.scaling_factor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int box_dx = Math.round(2.1f * eicas_gc.scaling_factor);
       
        g2.drawRoundRect(x, y, w, h,rect_r,rect_r);
        g2.drawRoundRect(x - box_dx , y - box_dx, w+ 2*box_dx, h+ 2*box_dx,rect_r,rect_r);
        g2.setStroke(original_stroke);
    }
    
	private void scalePen(Graphics2D g2, float factor) {
		original_stroke = g2.getStroke();
		g2.setStroke(new BasicStroke(factor * eicas_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
	}
	
    private void resetPen(Graphics2D g2) {
        g2.setStroke(original_stroke);
    }
}
