/**
* LowerEicas.java
* 
* Lower EICAS
* 
* Copyright (C) 2011  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.flightdeck.mfd;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Shape;
//import java.awt.Color;
//import java.awt.Color;
import java.awt.Component;
//import java.awt.GradientPaint;
import java.awt.Graphics2D;
//import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
//import java.awt.geom.Area;
//import java.awt.geom.Rectangle2D;
//import java.awt.geom.RoundRectangle2D;
//import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSISettings;

import net.sourceforge.xhsi.model.Aircraft;
import net.sourceforge.xhsi.model.Aircraft.IgnitionKeyPosition;
//import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.Avionics;
//import net.sourceforge.xhsi.model.ComRadio;
//import net.sourceforge.xhsi.model.FMS;
//import net.sourceforge.xhsi.model.FMSEntry;
//import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
//import net.sourceforge.xhsi.model.NavigationObjectRepository;
//import net.sourceforge.xhsi.model.Runway;
import net.sourceforge.xhsi.model.Aircraft.ValveStatus;



public class LowerEicas extends MFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private Stroke original_stroke;

    private boolean inhibit;

    private DecimalFormat one_decimal_format;
    private DecimalFormat two_decimals_format;
    private DecimalFormatSymbols format_symbols;

    private int dial_x[] = new int[8];
    private int tape_x[] = new int[8];
    
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

    public LowerEicas(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);

        one_decimal_format = new DecimalFormat("##0.0");
        format_symbols = one_decimal_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        one_decimal_format.setDecimalFormatSymbols(format_symbols);

        two_decimals_format = new DecimalFormat("#0.00");
        format_symbols = two_decimals_format.getDecimalFormatSymbols();
        format_symbols.setDecimalSeparator('.');
        two_decimals_format.setDecimalFormatSymbols(format_symbols);

    }


    public void paint(Graphics2D g2) {
        
        if ( mfd_gc.powered &&  ( this.avionics.get_mfd_mode() == Avionics.MFD_MODE_EICAS ) && ( this.aircraft.num_engines() > 0 ) ) {
        	if (mfd_gc.boeing_style) {

        		this.inhibit = ( this.aircraft.agl_m() < 1000.0f / 3.28084f );

        		boolean n1_style = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_N1 );
        		boolean epr_style = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_EPR );
        		boolean piston_style = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_MAP );

        		int num_eng = this.aircraft.num_engines();
        		//num_eng = 4;
        		int cols = Math.max(num_eng, 2);
        		if ( cols == 2 ) {
        			dial_x[0] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*30/100;
        			tape_x[0] = dial_x[0] + mfd_gc.dial_r[2]/2;
        			dial_x[1] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*70/100;
        			tape_x[1] = dial_x[1] - mfd_gc.dial_r[2]/2;
        		} else {
        			for (int i=0; i<cols; i++) {
        				dial_x[i] = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*50/100/cols + i*mfd_gc.panel_rect.width*9/10/cols;
        				tape_x[i] = dial_x[i] - mfd_gc.dial_r[cols]/2;
        			}
        		}

        		for (int i=0; i<num_eng; i++) {

        			if ( n1_style || epr_style ) {
        				drawN2(g2, i, num_eng);
        			}
        			if ( ! piston_style ) {
        				drawFF(g2, i, num_eng);
        			}
        			drawOilP(g2, i, num_eng);
        			drawOilT(g2, i, num_eng);
        			drawOilQ(g2, i, num_eng);
        			drawVIB(g2, i, num_eng);

        			String ind_str;
        			int ind_x;
        			g2.setColor(mfd_gc.color_boeingcyan);
        			g2.setFont(mfd_gc.font_m);

        			// N2
        			if ( n1_style || epr_style ) {
        				ind_str = "N2";
        				if ( cols == 2 ) {
        					ind_x = (dial_x[0] + dial_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
        				} else {
        					ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
        				}
        				g2.drawString(ind_str, ind_x, mfd_gc.dial_n2_y + mfd_gc.dial_r[2]);
        			}

        			// FF
        			if ( ! piston_style ) {
        				if ( num_eng < 5 ) {
        					ind_str = "FF";
        					if ( cols == 2 ) {
        						ind_x = (dial_x[0] + dial_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
        					} else {
        						ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
        					}
        					g2.drawString(ind_str, ind_x, mfd_gc.dial_ff_y + mfd_gc.line_height_m*5/8);
        				}
        			}

        			// OIL P
        			ind_str = "OIL P";
        			if ( cols == 2 ) {
        				ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
        			} else {
        				ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
        			}
        			g2.drawString(ind_str, ind_x, mfd_gc.dial_oilp_y + mfd_gc.line_height_m*5/8);

        			// OIL T
        			ind_str = "OIL T";
        			if ( cols == 2 ) {
        				ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
        			} else {
        				ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
        			}
        			g2.drawString(ind_str, ind_x, mfd_gc.dial_oilt_y + mfd_gc.line_height_m*5/8);

        			// OIL Q
        			if ( num_eng < 5 ) {
        				ind_str = "OIL Q";
        				if ( cols == 2 ) {
        					ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
        				} else {
        					ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
        				}
        				g2.drawString(ind_str, ind_x, mfd_gc.dial_oilq_y + mfd_gc.line_height_m*5/8);
        			}

        			// VIB
        			ind_str = "VIB";
        			if ( cols == 2 ) {
        				ind_x = (tape_x[0] + tape_x[1]) / 2 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str)/2;
        			} else {
        				ind_x = dial_x[num_eng-1] + mfd_gc.dial_r[num_eng]*245/100 - mfd_gc.get_text_width(g2, mfd_gc.font_m, ind_str);
        			}
        			g2.drawString(ind_str, ind_x, mfd_gc.dial_vib_y + mfd_gc.line_height_m*5/8);

        		} 
        	} else {
    			// Airbus style
            	// Page ID
        		boolean ignition = false;
        		for (int e=0; (e<this.aircraft.num_engines()) && !ignition; e++) { 
        			ignition = this.aircraft.get_igniter_on(e); 
        			}
        		boolean piston = ( this.avionics.get_engine_type() == XHSISettings.ENGINE_TYPE_MAP );
            	drawPageID(g2, "ENGINE");
    			drawEngineVib(g2);
    			drawFuelUsed(g2);          	
    			drawOilTemp(g2);
    			drawAirbusOilQ(g2);
    			drawAirbusOilP(g2);
            	if (mfd_gc.num_eng < 3) drawSeparationLine(g2);
            	if (piston) {
            		drawMagnetos(g2);
            	} else {
            		if (ignition) drawIgnitors(g2); else drawNacelleTemp(g2);
            	}
    		}
        }

    }

    private void drawN2(Graphics2D g2, int pos, int num) {

        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float n2_value = this.aircraft.get_N2(pos);
        float n2_dial = Math.min(n2_value, 110.0f) / 100.0f;

        int n2_y = mfd_gc.dial_n2_y;
        int n2_r = mfd_gc.dial_r[num];

        if ( ( n2_dial <= 1.0f ) || this.inhibit ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( n2_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        g2.fillArc(dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, 0, -Math.round(n2_dial*200.0f));

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawArc(dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, 0, -200);
        g2.setColor(mfd_gc.caution_color);
        g2.drawArc(dial_x[pos]-n2_r, n2_y-n2_r, 2*n2_r, 2*n2_r, -200, -20);
        g2.rotate(Math.toRadians(220), dial_x[pos], n2_y);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(dial_x[pos]+n2_r, n2_y, dial_x[pos]+n2_r*19/16, n2_y);
        g2.setTransform(original_at);

        //needle
        g2.rotate(Math.toRadians(Math.round(n2_dial*200.0f)), dial_x[pos], n2_y);
        g2.setColor(mfd_gc.markings_color);
        g2.drawLine(dial_x[pos], n2_y, dial_x[pos]+n2_r-2, n2_y);
        g2.setTransform(original_at);

        // value box
        n2_y -= n2_r/8;
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(dial_x[pos], n2_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*55/10, mfd_gc.dial_font_h[num]*140/100);
            if ( ( n2_dial <= 1.0f ) || this.inhibit ) {
                // inhibit caution or warning below 1000ft
                g2.setColor(mfd_gc.markings_color);
            } else if ( n2_dial < 1.1f ) {
                g2.setColor(mfd_gc.caution_color);
            } else {
                g2.setColor(mfd_gc.warning_color);
            }
            g2.setFont(mfd_gc.dial_font[num]);
            String n2_str = one_decimal_format.format(n2_value);
            g2.drawString(n2_str, dial_x[pos]+mfd_gc.dial_font_w[num]*51/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], n2_str), n2_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }


//    private void drawNG(Graphics2D g2, int pos, int num) {
//
//        AffineTransform original_at = g2.getTransform();
//        scalePen(g2);
//
//        float ng_value = this.aircraft.get_NG(pos);
//        float ng_dial = Math.min(ng_value, 110.0f) / 100.0f;
//
//        int ng_y = mfd_gc.dial_ng_y;
//        int ng_r = mfd_gc.dial_r[num];
//
////        if ( ( ng_dial <= 1.0f ) || this.inhibit ) {
////            // inhibit caution or warning below 1000ft
////            g2.setColor(mfd_gc.instrument_background_color);
////        } else if ( ng_dial < 1.1f ) {
////            g2.setColor(mfd_gc.caution_color.darker().darker());
////        } else {
////            g2.setColor(mfd_gc.warning_color.darker().darker());
////        }
//        g2.setColor(mfd_gc.instrument_background_color);
//        g2.fillArc(dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -Math.round(ng_dial*200.0f));
//
//        g2.setColor(mfd_gc.dim_markings_color);
//        g2.drawArc(dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, 0, -200);
//        g2.setColor(mfd_gc.caution_color);
//        g2.drawArc(dial_x[pos]-ng_r, ng_y-ng_r, 2*ng_r, 2*ng_r, -200, -20);
//        g2.rotate(Math.toRadians(220), dial_x[pos], ng_y);
//        g2.setColor(mfd_gc.warning_color);
//        g2.drawLine(dial_x[pos]+ng_r, ng_y, dial_x[pos]+ng_r*19/16, ng_y);
//        g2.setTransform(original_at);
//
//        //needle
//        g2.rotate(Math.toRadians(Math.round(ng_dial*200.0f)), dial_x[pos], ng_y);
//        g2.setColor(mfd_gc.markings_color);
//        g2.drawLine(dial_x[pos], ng_y, dial_x[pos]+ng_r-2, ng_y);
//        g2.setTransform(original_at);
//
//        // value box
//        ng_y -= ng_r/8;
//        if ( num < 5 ) {
//            g2.setColor(mfd_gc.dim_markings_color);
//            g2.drawRect(dial_x[pos], ng_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*55/10, mfd_gc.dial_font_h[num]*140/100);
//            if ( ( ng_dial <= 1.0f ) || this.inhibit ) {
//                // inhibit caution or warning below 1000ft
//                g2.setColor(mfd_gc.markings_color);
//            } else if ( ng_dial < 1.1f ) {
//                g2.setColor(mfd_gc.caution_color);
//            } else {
//                g2.setColor(mfd_gc.warning_color);
//            }
//            g2.setFont(mfd_gc.dial_font[num]);
//            String ng_str = one_decimal_format.format(ng_value);
//            g2.drawString(ng_str, dial_x[pos]+mfd_gc.dial_font_w[num]*51/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], ng_str), ng_y-mfd_gc.dial_font_h[num]*25/100-2);
//        }
//
//        resetPen(g2);
//
//    }


    private void drawFF(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        // convert FF from kg/s to kg/h, lbs/h, usg/h or ltr/h
        float unit_multiplier = this.aircraft.fuel_multiplier();
        float ff_value = this.aircraft.get_FF(pos) * 3600 * unit_multiplier;
//        float ff_max = this.aircraft.get_max_FF() * 3600 * unit_multiplier;

        int ff_y = mfd_gc.dial_ff_y + mfd_gc.dial_font_h[num]*5/8;
        int offset_x = mirror ? 0 : -mfd_gc.dial_font_w[num]*55/10;

        // value box
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(tape_x[pos] + offset_x,
                    ff_y - mfd_gc.dial_font_h[num]*140/100,
                    mfd_gc.dial_font_w[num]*55/10,
                    mfd_gc.dial_font_h[num]*140/100);
            g2.setColor(mfd_gc.markings_color);
            g2.setFont(mfd_gc.dial_font[num]);
//            if ( ff_max > 9999.9f ) {
//                ff_value /= 1000.0f;
//            }
            String ff_str;
//            if ( ff_value > 99.9f ) {
//                ff_str = Integer.toString( Math.round(ff_value) );
//            } else {
//                ff_str = one_decimal_format.format(ff_value);
//            }
            ff_str = Integer.toString( Math.round(ff_value) );
            g2.drawString(ff_str,
                    tape_x[pos] + offset_x + mfd_gc.dial_font_w[num]*51/10 - mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], ff_str),
                    ff_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }

    
    private void drawOilP(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        float oilp_dial = this.aircraft.get_oil_press_ratio(pos);

        int oilp_x = tape_x[pos];
        int oilp_y = mfd_gc.dial_oilp_y;
        int oilp_h = mfd_gc.tape_h;

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(oilp_x, oilp_y - oilp_h/2, oilp_x, oilp_y + oilp_h/2);
        g2.setColor(mfd_gc.caution_color);
        g2.drawLine(oilp_x, oilp_y + oilp_h*3/10, oilp_x + (mirror ? -oilp_h/20 : oilp_h/20), oilp_y + oilp_h*3/10);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(oilp_x - oilp_h/15, oilp_y + oilp_h/2, oilp_x + oilp_h/15, oilp_y + oilp_h/2);

        // arrow
        int a_x[] = {
            oilp_x,
            oilp_x + (mirror ? -oilp_h/4 : oilp_h/4),
            oilp_x + (mirror ? -oilp_h/4 : oilp_h/4)
        };
        int oilp_a = oilp_y + oilp_h/2 - Math.round(oilp_h * oilp_dial);
        int a_y[] = {
            oilp_a,
            oilp_a - oilp_h/10,
            oilp_a + oilp_h/10
        };
        if ( this.aircraft.oil_press_alert(pos) ) {
            g2.setColor(mfd_gc.warning_color);
        } else if ( oilp_dial < 0.1f ) {
            g2.setColor(mfd_gc.caution_color);
        } else {
            g2.setColor(mfd_gc.markings_color);
        }
        g2.fillPolygon(a_x, a_y, 3);

        resetPen(g2);

    }


    private void drawOilT(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        float oilt_dial = this.aircraft.get_oil_temp_ratio(pos);

        int oilt_x = tape_x[pos];
        int oilt_y = mfd_gc.dial_oilt_y;
        int oilt_h = mfd_gc.tape_h;

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(oilt_x, oilt_y - oilt_h/2, oilt_x, oilt_y + oilt_h/2);
        g2.setColor(mfd_gc.caution_color);
        g2.drawLine(oilt_x, oilt_y - oilt_h*3/10, oilt_x + (mirror ? -oilt_h/20 : oilt_h/20), oilt_y - oilt_h*3/10);
        g2.setColor(mfd_gc.warning_color);
        g2.drawLine(oilt_x - oilt_h/15, oilt_y - oilt_h/2, oilt_x + oilt_h/15, oilt_y - oilt_h/2);

        // arrow
        int a_x[] = {
            oilt_x,
            oilt_x + (mirror ? -oilt_h/4 : oilt_h/4),
            oilt_x + (mirror ? -oilt_h/4 : oilt_h/4)
        };
        int oilt_a = oilt_y + oilt_h/2 - Math.round(oilt_h * oilt_dial);
        int a_y[] = {
            oilt_a,
            oilt_a - oilt_h/10,
            oilt_a + oilt_h/10
        };
        if ( this.aircraft.oil_temp_alert(pos) ) {
            g2.setColor(mfd_gc.warning_color);
        } else if ( oilt_dial > 0.9f ) {
            g2.setColor(mfd_gc.caution_color);
        } else {
            g2.setColor(mfd_gc.markings_color);
        }
        g2.fillPolygon(a_x, a_y, 3);

        resetPen(g2);

    }


    private void drawOilQ(Graphics2D g2, int pos, int num) {

        scalePen(g2);
        
        boolean mirror = (pos==1) && (num==2);

        int oilq_val = Math.round( this.aircraft.get_oil_quant_ratio(pos) * 100.0f );

        int oilq_y = mfd_gc.dial_oilq_y + mfd_gc.dial_font_h[num]*5/8;
        int offset_x = mirror ? 0 : -mfd_gc.dial_font_w[num]*35/10;

        // value box
        if ( num < 5 ) {
            g2.setColor(mfd_gc.dim_markings_color);
            g2.drawRect(tape_x[pos] + offset_x,
                    oilq_y - mfd_gc.dial_font_h[num]*140/100, mfd_gc.dial_font_w[num]*35/10, mfd_gc.dial_font_h[num]*140/100);
            g2.setColor(mfd_gc.markings_color);
            g2.setFont(mfd_gc.dial_font[num]);
            String oilq_str = Integer.toString(oilq_val);
            g2.drawString(oilq_str,
                    tape_x[pos] + offset_x + mfd_gc.dial_font_w[num]*31/10 - mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], oilq_str),
                    oilq_y-mfd_gc.dial_font_h[num]*25/100-2);
        }

        resetPen(g2);

    }

    
    private void drawVIB(Graphics2D g2, int pos, int num) {

        scalePen(g2);

        boolean mirror = (pos==1) && (num==2);

        float vib_dial = this.aircraft.get_vib(pos) / 100.0f;
        int vib_x = tape_x[pos];
        int vib_y = mfd_gc.dial_vib_y;
        int vib_h = mfd_gc.tape_h;

        g2.setColor(mfd_gc.dim_markings_color);
        g2.drawLine(vib_x, vib_y - vib_h/2, vib_x, vib_y + vib_h/2);
        g2.setColor(mfd_gc.caution_color);
        g2.drawLine(vib_x - vib_h/20, vib_y - vib_h*3/10, vib_x + vib_h/20, vib_y - vib_h*3/10);

        // arrow
        int a_x[] = {
            vib_x,
            vib_x + (mirror ? -vib_h/4 : vib_h/4),
            vib_x + (mirror ? -vib_h/4 : vib_h/4)
        };
        int vib_a = vib_y + vib_h/2 - Math.round(vib_h * vib_dial);
        int a_y[] = {
            vib_a,
            vib_a - vib_h/10,
            vib_a + vib_h/10
        };
        g2.setColor(mfd_gc.markings_color);
        g2.fillPolygon(a_x, a_y, 3);

        resetPen(g2);

    }


    private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int text_width = mfd_gc.get_text_width(g2, mfd_gc.font_xxl, page_str);
		int page_id_x = mfd_gc.eng_page_id_x - text_width/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xxl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xxl/8, page_id_x + text_width, page_id_y + mfd_gc.line_height_m/8);
	}

	
	private void drawSeparationLine(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.eng_line_x,mfd_gc.eng_line_top,mfd_gc.eng_line_x,mfd_gc.eng_line_bottom);
	}

	
	private void drawAirbusOilQ(Graphics2D g2){
		// TODO: No red sector
		String str_fuel_legend = "OIL";
		String str_fuel_units = "QT%";
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_fuel_legend, mfd_gc.eng_dial_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_legend)/2, mfd_gc.eng_oilq_title_y);
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_fuel_units, mfd_gc.eng_dial_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_units)/2, mfd_gc.eng_oilq_legend_y);
		
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			int oilq_val = Math.round( this.aircraft.get_oil_quant_ratio(eng) * 100.0f );
	        int dial_x = mfd_gc.dial_x[eng];
	        int dial_y = mfd_gc.eng_oilq_dial_y;
	        int dial_r = mfd_gc.dial_r[mfd_gc.num_eng];
			drawAirbusGauge(g2, dial_x, dial_y, dial_r, oilq_val, 100.0f);
		}
	}

	private void drawAirbusOilP(Graphics2D g2){
		// TODO : Red sector < 14% ; amber needle < 20%
		// FCOM A320 : red sector below 40 psi / 300
		// FCOM A320 : amber needle between 40 and 60 psi /300 
		String str_fuel_units = "PSI";
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_fuel_units, mfd_gc.eng_dial_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_units)/2, mfd_gc.eng_oilp_legend_y);
		float max_oil_p = Math.round(10*((this.aircraft.get_oil_press_max()+5)/10));
		
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			int oilp_val = Math.round(  this.aircraft.get_oil_press_psi(eng));
	        int dial_x = mfd_gc.dial_x[eng];
	        int dial_y = mfd_gc.eng_oilp_dial_y;
	        int dial_r = mfd_gc.dial_r[mfd_gc.num_eng];
			drawAirbusGauge(g2, dial_x, dial_y, dial_r, oilp_val, max_oil_p);
		}
	}
    
    private void drawAirbusGauge(Graphics2D g2, int x, int y, int r, float value, float max) {
    	
        AffineTransform original_at = g2.getTransform();
        scalePen(g2);

        float egt_dial = value / max;
        int dial_value = Math.round(value);
        int max_limit = Math.round(max);
        boolean dial_disabled = false;
        int num = mfd_gc.num_eng;

        int dial_x = x;
        int dial_y = y;
        int dial_r = r;

        int deg_zero  = 225;	// Gauge zero (0%)
        int deg_start = 225; 	// Gauge starting position white sector
        int deg_full  = 50;     // Gauge full (100%)
        int deg_full_range = deg_zero-deg_full;   // Deg range from 0 to 100%
        // int deg_warning = stabilized ? 45 : 120;   // Gauge red sector (warning)
        int deg_warning = 60;   // Gauge red sector (warning) 
        int deg_end = 25;       // Gauge end
        int deg_norm_range = deg_start-deg_warning;
        int deg_warn_range = deg_warning-deg_end;
        
        if ( egt_dial <= 1.0f ) {
            // inhibit caution or warning below 1000ft
            g2.setColor(mfd_gc.instrument_background_color);
        } else if ( egt_dial < 1.1f ) {
            g2.setColor(mfd_gc.caution_color.darker().darker());
        } else {
            g2.setColor(mfd_gc.warning_color.darker().darker());
        }
        

        // White Arc
        g2.setColor(mfd_gc.ecam_markings_color);
        g2.drawArc(dial_x-dial_r, dial_y-dial_r, 2*dial_r, 2*dial_r, deg_start, -deg_norm_range);
        
        // Red Arc
       	int dial_r_red = dial_r * 98/100; 
        g2.setColor(mfd_gc.ecam_warning_color);
        original_stroke = g2.getStroke();
        g2.setStroke(new CompositeStroke( new BasicStroke( 3.0f * mfd_gc.grow_scaling_factor ), new BasicStroke( 2.0f * mfd_gc.grow_scaling_factor ) ));
        g2.drawArc(dial_x-dial_r_red, dial_y-dial_r_red, 2*dial_r_red, 2*dial_r_red, deg_warning, -deg_warn_range);
    	g2.setStroke(original_stroke);
        g2.setTransform(original_at);
        
        // EGT max target
        g2.setColor(mfd_gc.ecam_caution_color);
        g2.rotate(Math.toRadians(360-deg_warning), dial_x, dial_y);
        g2.drawLine(dial_x+dial_r*18/16, dial_y, dial_x+dial_r+1, dial_y);
        g2.setTransform(original_at);

        
        // scale markings every 50%
        
        g2.setColor(mfd_gc.dim_markings_color);
        g2.rotate(Math.toRadians(360-deg_zero), dial_x, dial_y);
        for (int i=0; i<=10; i++) {
        	if (i==0 || i==5 || i==10) {
        		g2.drawLine(dial_x+dial_r*14/16, dial_y, dial_x+dial_r-1, dial_y);
        	}
            g2.rotate(Math.toRadians(deg_full_range/10), dial_x, dial_y);
        }
        g2.setTransform(original_at);
        
        
        // scale number 0 and 10
        g2.setFont(mfd_gc.font_m);
        int n1_digit_x;
        int n1_digit_y;
        int n1_digit_angle = 360-deg_zero;
        // int n1_digit_angle = 360-deg_start;
        for (int i=0; i<=10; i+=1) {
        	n1_digit_x = dial_x + (int)(Math.cos(Math.toRadians(n1_digit_angle))*dial_r*9/16);
        	n1_digit_y = dial_y + (int)(Math.sin(Math.toRadians(n1_digit_angle))*dial_r*9/16);
        	if (i==0) {
        		g2.drawString(Integer.toString(i), n1_digit_x - mfd_gc.digit_width_m/2, n1_digit_y+mfd_gc.line_height_m*3/8);
        	} else if (i==10) {
        		g2.drawString(Integer.toString(Math.round(max)), n1_digit_x - mfd_gc.digit_width_m/2, n1_digit_y+mfd_gc.line_height_m*3/8);
        	}
        	
        	n1_digit_angle += deg_full_range/10;
        }
        
        // needle
        if (!dial_disabled) {
        	int egt_needle_deg = Math.max(Math.round(egt_dial*deg_full_range)-deg_zero , -deg_start);
        	g2.rotate(Math.toRadians(egt_needle_deg), dial_x, dial_y);
        	g2.setColor(mfd_gc.ecam_normal_color);
        	g2.drawLine(dial_x, dial_y, dial_x+dial_r-2, dial_y);
        	g2.setTransform(original_at);
        }
             
        
        // value
        String value_str = Integer.toString(dial_value);
        dial_y -= dial_r/8;       
        if ( egt_dial <= 1.0f ) {
        	// inhibit caution or warning below 1000ft
        	g2.setColor(mfd_gc.ecam_normal_color);
        } else if ( egt_dial < 1.1f ) {
        	g2.setColor(mfd_gc.ecam_caution_color);
        } else {
        	g2.setColor(mfd_gc.ecam_warning_color);
        }
        if (dial_disabled) { 
        	value_str="XX";
        	g2.setColor(mfd_gc.ecam_caution_color);
        }
        g2.setFont(mfd_gc.dial_font[num]);        
        g2.drawString(value_str, dial_x - mfd_gc.dial_font_w[num]*22/10 +mfd_gc.dial_font_w[num]*44/10-mfd_gc.get_text_width(g2, mfd_gc.dial_font[num], value_str),
        		dial_y+mfd_gc.dial_font_h[num]*140/100);

        resetPen(g2);
    }
  
    private void drawMagnetos(Graphics2D g2) {
    	// Piston engines : draw Magnetos status
    	g2.setColor(mfd_gc.ecam_markings_color);
    	String legend_str="MAGN";
    	g2.setFont(mfd_gc.font_xl);
        g2.drawString(legend_str, mfd_gc.eng_dial_center_x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str)/2, mfd_gc.eng_ing_title_y);        

    	String magneto_str="";
    	g2.setFont(mfd_gc.font_fixed_xl);
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			IgnitionKeyPosition key_pos = aircraft.get_ignition_key(eng);
	    	g2.setColor(mfd_gc.ecam_normal_color);	    
	    	switch (key_pos) {
	    	case OFF :   magneto_str = "OFF"; g2.setColor(mfd_gc.ecam_warning_color); break;  
	    	case RIGHT : magneto_str = "    R"; g2.setColor(mfd_gc.ecam_caution_color); break;
	    	case LEFT :  magneto_str = "L    "; g2.setColor(mfd_gc.ecam_caution_color); break;
	    	case BOTH :  magneto_str = "L + R"; g2.setColor(mfd_gc.ecam_normal_color); break;
	    	case START : magneto_str = "START"; g2.setColor(mfd_gc.ecam_normal_color); break;
	    	}   	
	    	g2.drawString(magneto_str, mfd_gc.dial_x[eng] -mfd_gc.get_text_width(g2, mfd_gc.font_fixed_xl, magneto_str)/2 , mfd_gc.eng_ing_valve_y);    	
		}    	
    }
    
    
    private void drawIgnitors(Graphics2D g2) {

    	// Title
    	g2.setColor(mfd_gc.ecam_markings_color);
    	String legend_str="IGN";
    	g2.setFont(mfd_gc.font_xl);
        g2.drawString(legend_str, mfd_gc.eng_dial_center_x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str)/2, mfd_gc.eng_ing_title_y);
        
        // Ignitors : A, B or AB
    	String letter_str="A";
    	int ignitor_letter_x = mfd_gc.eng_dial_center_x - mfd_gc.panel_rect.width*(318-268)/1000;
    	g2.setColor(mfd_gc.ecam_normal_color);
        g2.drawString(letter_str, ignitor_letter_x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, letter_str)/2, mfd_gc.eng_ing_legend_y);    	    	
    	
        /* A320 FCOM 1.70.90 p12 rev 23
         * Engine bleed pressure
         * The green numbers show the bleed pressure upstream of the precooler
         * They become amber when the pressure drops below 21 psi with N2>=10% or if the is an overpressure
         */
        if (mfd_gc.num_eng<3) {
        	// PSI legend  and value left engine
        	legend_str="PSI";
        	g2.setColor(mfd_gc.ecam_action_color);
        	g2.setFont(mfd_gc.font_l);
        	g2.drawString(legend_str, mfd_gc.dial_x[0]-mfd_gc.digit_width_l/2-mfd_gc.get_text_width(g2, mfd_gc.font_l, legend_str), mfd_gc.eng_ing_value_y);  

        	int left_bleed=Math.round(this.aircraft.bleed_air_press(Aircraft.BLEED_LEFT));
        	String value_str=""+left_bleed;
        	g2.setFont(mfd_gc.font_xl);
        	g2.setColor(left_bleed < 21 ? mfd_gc.ecam_caution_color: mfd_gc.ecam_normal_color);
        	g2.drawString(value_str, mfd_gc.dial_x[0], mfd_gc.eng_ing_value_y);

        	// PSI legend  and value right engine
        	legend_str="PSI";
        	g2.setColor(mfd_gc.ecam_action_color);
        	g2.setFont(mfd_gc.font_l);
        	g2.drawString(legend_str, mfd_gc.dial_x[1]+mfd_gc.digit_width_l/2, mfd_gc.eng_ing_value_y);

        	int right_bleed=Math.round(this.aircraft.bleed_air_press(Aircraft.BLEED_RIGHT));
        	value_str=""+right_bleed;
        	g2.setFont(mfd_gc.font_xl);
        	g2.setColor(right_bleed < 21 ? mfd_gc.ecam_caution_color: mfd_gc.ecam_normal_color);    	
        	g2.drawString(value_str, mfd_gc.dial_x[1]-mfd_gc.get_text_width(g2, mfd_gc.font_xl, value_str), mfd_gc.eng_ing_value_y);
        }
	
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
	    	g2.setColor(mfd_gc.ecam_normal_color);
	    	g2.drawLine(mfd_gc.eng_ing_bleed_x[eng], mfd_gc.eng_ing_valve_y, mfd_gc.eng_ing_bleed_x[eng], mfd_gc.eng_ing_bottom);    	
	    	g2.drawLine(mfd_gc.eng_ing_valve_in_x[eng], mfd_gc.eng_ing_valve_y, mfd_gc.eng_ing_bleed_x[eng], mfd_gc.eng_ing_valve_y);
			drawValveHoriz(g2, aircraft.get_ignition_bleed_valve(eng), mfd_gc.eng_ing_valve_x[eng], mfd_gc.eng_ing_valve_y);
			if (aircraft.get_ignition_bleed_valve(eng)== ValveStatus.VALVE_OPEN)
				g2.drawLine(mfd_gc.eng_ing_valve_out_x[eng], mfd_gc.eng_ing_valve_y, mfd_gc.eng_ing_edge_x[eng], mfd_gc.eng_ing_valve_y);
		}
    	
    }
	
    private void drawNacelleTemp(Graphics2D g2) {
    	/* A320 FCOM 1.70.90 p12 REV 23
    	 * The screen displays both nacelle temperatures if at least one of them is above 240°c
    	 * A nacelle temperature above 240° pulses green
    	 * During the start sequence, an ignition indication replaces these temperatures
    	 * (no gauge - only values)
    	 */
    	g2.setColor(mfd_gc.ecam_markings_color);
    	String legend_str="NAC";
    	g2.setFont(mfd_gc.font_xl);
        g2.drawString(legend_str, mfd_gc.eng_dial_center_x-mfd_gc.get_text_width(g2, mfd_gc.font_xl, legend_str)/2, mfd_gc.eng_ing_title_y);
		
		String str_units = "°C";
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_units, mfd_gc.eng_dial_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_units)/2, mfd_gc.eng_ing_legend_y);
		
		int dial_r = mfd_gc.dial_r[mfd_gc.num_eng];
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			int nac_temp_val = Math.round( this.aircraft.get_nac_temp(eng));	        
			drawAirbusGauge(g2, mfd_gc.dial_x[eng], mfd_gc.eng_nac_y, dial_r, nac_temp_val, 500.0f);
		}
    }
    
	private void drawEngineVib(Graphics2D g2) {
		String vib_n1_legend = "VIB   (N1)";
		String vib_n2_legend = "VIB   (N2)";
		String str_vib_val = "";
		if (mfd_gc.num_eng < 3) {
			// T
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.drawLine(mfd_gc.eng_vib_x - mfd_gc.eng_vib_t_dx, mfd_gc.eng_vib_n1_top, mfd_gc.eng_vib_x + mfd_gc.eng_vib_t_dx, mfd_gc.eng_vib_n1_top);
			g2.drawLine(mfd_gc.eng_vib_x - mfd_gc.eng_vib_t_dx, mfd_gc.eng_vib_n2_top, mfd_gc.eng_vib_x + mfd_gc.eng_vib_t_dx, mfd_gc.eng_vib_n2_top);
			g2.drawLine(mfd_gc.eng_vib_x, mfd_gc.eng_vib_n1_top, mfd_gc.eng_vib_x, mfd_gc.eng_vib_n1_top  + mfd_gc.eng_vib_t_dy);
			g2.drawLine(mfd_gc.eng_vib_x, mfd_gc.eng_vib_n2_top, mfd_gc.eng_vib_x, mfd_gc.eng_vib_n2_top  + mfd_gc.eng_vib_t_dy);
			// Legends
			g2.setFont(mfd_gc.font_xl);
			g2.drawString(vib_n1_legend, mfd_gc.eng_vib_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n1_legend)/2, mfd_gc.eng_vib_n1_title_y);
			g2.drawString(vib_n2_legend, mfd_gc.eng_vib_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n2_legend)/2, mfd_gc.eng_vib_n2_title_y);
		} else {
			// Legends
			vib_n1_legend = "VIB N1";
			vib_n2_legend = "     N2";	
			g2.setFont(mfd_gc.font_xl);
			g2.drawString(vib_n1_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n1_legend)/2, mfd_gc.eng_vib_n1_title_y);
			g2.drawString(vib_n2_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, vib_n2_legend)/2, mfd_gc.eng_vib_n2_title_y);

		}
				
		// Values
		g2.setColor(mfd_gc.ecam_normal_color);
		str_vib_val="XX";
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			drawStringSmallOneDecimal(g2, mfd_gc.crz_vib_x[eng]+mfd_gc.digit_width_xxl, mfd_gc.eng_vib_n1_value_y,mfd_gc.font_xxl,mfd_gc.font_xl, this.aircraft.get_vib(eng)/10 );
			drawStringSmallOneDecimal(g2, mfd_gc.crz_vib_x[eng]+mfd_gc.digit_width_xxl, mfd_gc.eng_vib_n2_value_y,mfd_gc.font_xxl,mfd_gc.font_xl, this.aircraft.get_vib_n2(eng)/10 );
		}
	}
    
	private void drawFuelUsed(Graphics2D g2) {
		String str_fuel_legend = "F. USED";
		
		String str_fuel_units;
        if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_KG ) str_fuel_units = XHSIPreferences.FUEL_UNITS_KG;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LBS ) str_fuel_units = XHSIPreferences.FUEL_UNITS_LBS;
        else if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_USG ) str_fuel_units = XHSIPreferences.FUEL_UNITS_USG;
        else /* if ( this.avionics.get_fuel_units() == XHSISettings.FUEL_UNITS_LTR ) */ str_fuel_units = XHSIPreferences.FUEL_UNITS_LTR;
	
		if (mfd_gc.num_eng<3) {
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.drawLine(mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx1, mfd_gc.crz_fuel_top_y, mfd_gc.crz_eng_center_x - mfd_gc.crz_eng_line_dx2, mfd_gc.crz_fuel_bottom_y);
			g2.drawLine(mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx1, mfd_gc.crz_fuel_top_y, mfd_gc.crz_eng_center_x + mfd_gc.crz_eng_line_dx2, mfd_gc.crz_fuel_bottom_y);
		}
	
		g2.setFont(mfd_gc.font_xl);
		g2.drawString(str_fuel_legend, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_legend)/2, mfd_gc.crz_fuel_legend_y);
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_fuel_units, mfd_gc.crz_eng_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_fuel_units)/2, mfd_gc.crz_fuel_units_y);

		// Values
		String str_fuel_val="XX";
		g2.setFont(mfd_gc.font_xxl);
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			if (this.aircraft.fuel_used(eng) > 0.1f ) {
				g2.setColor(mfd_gc.ecam_normal_color);					
				str_fuel_val=""+Math.round(this.aircraft.fuel_used(eng) * this.aircraft.fuel_multiplier());
			} else {
				str_fuel_val="XX";
				g2.setColor(mfd_gc.ecam_caution_color);
			}
			g2.drawString(str_fuel_val, mfd_gc.crz_eng_x[eng]-mfd_gc.get_text_width(g2, mfd_gc.font_xxl, str_fuel_val), mfd_gc.crz_fuel_value_y);
		}
		
	}
	
	private void drawOilTemp(Graphics2D g2) {
	
		String str_oil_units = "°C";
		if (mfd_gc.num_eng < 3) {
			g2.setColor(mfd_gc.ecam_markings_color);
			g2.drawLine(mfd_gc.eng_dial_center_x - mfd_gc.crz_eng_line_dx1, mfd_gc.eng_oilt_line_top, mfd_gc.eng_dial_center_x - mfd_gc.crz_eng_line_dx2, mfd_gc.eng_oilt_line_bottom);
			g2.drawLine(mfd_gc.eng_dial_center_x + mfd_gc.crz_eng_line_dx1, mfd_gc.eng_oilt_line_top, mfd_gc.eng_dial_center_x + mfd_gc.crz_eng_line_dx2, mfd_gc.eng_oilt_line_bottom);
		}
		g2.setColor(mfd_gc.ecam_action_color);
		g2.setFont(mfd_gc.font_l);
		g2.drawString(str_oil_units, mfd_gc.eng_dial_center_x -mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_oil_units)/2, mfd_gc.eng_oilt_legend_y);
		
		// Values
		g2.setFont(mfd_gc.font_xxl);
		for (int eng=0; eng<mfd_gc.num_eng; eng++) {
			if (this.aircraft.get_oil_temp_c(eng) > this.aircraft.get_oil_temp_max()) {
				g2.setColor(mfd_gc.ecam_caution_color);	
			} else {
				g2.setColor(mfd_gc.ecam_normal_color);	
			}
			drawStringSmallOneDecimal(g2, mfd_gc.crz_eng_x[eng]+mfd_gc.digit_width_xxl, mfd_gc.eng_oilt_value_y, mfd_gc.font_xxl,mfd_gc.font_xl, this.aircraft.get_oil_temp_c(eng) );
		}
		
	}
	
    private void drawValveHoriz(Graphics2D g2, ValveStatus valve_sts, int x, int y) {
    	// Ignitors valve (bleed air)
    	int r = mfd_gc.cond_valve_r;
    	
    	if (valve_sts == ValveStatus.VALVE_CLOSED || valve_sts == ValveStatus.VALVE_OPEN) {
            g2.setColor(mfd_gc.ecam_normal_color); 
    	} else {
    		g2.setColor(mfd_gc.ecam_caution_color); 
    	}
        g2.drawOval(x-r,y-r,r*2,r*2);
        
    	if (valve_sts == ValveStatus.VALVE_OPEN_FAILED || valve_sts == ValveStatus.VALVE_OPEN) {
    		g2.drawLine(x-r, y, x+r, y);
    	} else {
    		g2.drawLine(x, y-r, x, y+r);
    	}
    }    
    
    private void drawStringSmallOneDecimal(Graphics2D g2, int x, int y, Font normalFont, Font smallFont, float value) {
    	// Value, decimal part in smaller font
    	// Justify Right
    	String valueStr =  one_decimal_format.format(value);
    	g2.setFont(normalFont);
    	String intStr = valueStr.substring(0, valueStr.length()-2);
    	String decStr = valueStr.substring(valueStr.length()-2,valueStr.length());
    	int len_n1_str1 = mfd_gc.get_text_width(g2, normalFont, intStr);
    	int len_n1_str2 = mfd_gc.get_text_width(g2, smallFont, decStr);
    	g2.drawString(intStr, x - len_n1_str2 - len_n1_str1, y);
    	g2.setFont(smallFont);
    	g2.drawString(decStr, x - len_n1_str2, y);
    }
    
    private void scalePen(Graphics2D g2) {

        original_stroke = g2.getStroke();
        g2.setStroke(new BasicStroke(2.5f * mfd_gc.grow_scaling_factor, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

    }


    private void resetPen(Graphics2D g2) {

        g2.setStroke(original_stroke);

    }


}
