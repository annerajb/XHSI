/**
* FMA_A320.java
* 
* This is the Airbus A320 family version of FMA.java
* 
* Copyright (C) 2010  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  Nicolas Carel
* Adapted for Airbus by Nicolas Carel
* Reference : A320 FCOM 1.31.40 page 23 REV 36
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;


public class FMA_A320 extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
        
    int box_c1_r0_time=0;
    int box_c1_r0_state=0;
    int box_c1_r1_time=0;
    int box_c1_r1_state=0;


    public FMA_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        
    }


    public void paint(Graphics2D g2) {
		if ( pfd_gc.airbus_style ) {
			if ( ! XHSIStatus.receiving ) {
				drawBox(g2);
			} else if ( pfd_gc.powered ) {      
				drawBox(g2);            
				if ( this.avionics.is_x737() ) {
					drawSystemStatus(g2);
					drawX737FMA(g2);
				} else if (this.avionics.is_qpac() ) {
					drawA320FMA(g2);
				} else {
					drawSystemStatus(g2);
					drawFMA(g2);
				}
			}
        }
    }


    private void drawBox(Graphics2D g2) {
        // pfd_gc.setTransparent(g2, this.preferences.get_draw_fullscreen_horizon() || ( this.preferences.get_draw_fullwidth_horizon() && pfd_gc.draw_hsi ) );
        // g2.setColor(pfd_gc.instrument_background_color);
        // g2.fillRect(pfd_gc.fma_left - 1, pfd_gc.fma_top - 1, pfd_gc.fma_width + 3, pfd_gc.fma_height + 3);
        pfd_gc.setOpaque(g2);
        g2.setColor(pfd_gc.pfd_box_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_1, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_1, pfd_gc.fma_top + pfd_gc.fma_height);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top + pfd_gc.fma_height * 2/3);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_3, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_3, pfd_gc.fma_top + pfd_gc.fma_height);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_4, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_4, pfd_gc.fma_top + pfd_gc.fma_height);
    }


    private void drawSystemStatus(Graphics2D g2) {

        int ap_mode = this.avionics.autopilot_mode();
        
        // AP Engaged
        String ap_str = "";
        if (ap_mode == 2) {
        	ap_str="AP 1";
        	draw1Mode(g2,4,0,ap_str,false, pfd_gc.pfd_markings_color);
        }    
              
        // FD Engaged
        String fd_str = "";
        if ( ap_mode > 0 ) {
        	fd_str="1 FD 1";
        	draw1Mode(g2,4,1,fd_str,false, pfd_gc.pfd_markings_color);
        }       
    }


    private void draw1Mode(Graphics2D g2, int col, int raw, String mode, boolean framed, Color color) {
        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_xl, mode);
        int mode_x = pfd_gc.fma_left;  // + pfd_gc.fma_width/10 + col*pfd_gc.fma_width/5 - mode_w/2;
        switch (col) {
        	case 1: mode_x += pfd_gc.fma_col_1 + (pfd_gc.fma_col_2 - pfd_gc.fma_col_1)/2 - mode_w/2; break;
        	case 2: mode_x += pfd_gc.fma_col_2 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_2)/2 - mode_w/2; break;
        	case 3: mode_x += pfd_gc.fma_col_3 + (pfd_gc.fma_col_4 - pfd_gc.fma_col_3)/2 - mode_w/2; break;
        	case 4: mode_x += pfd_gc.fma_col_4 + (pfd_gc.fma_width - pfd_gc.fma_col_4)/2 - mode_w/2; break;
        	default: mode_x += pfd_gc.fma_col_1 /2 - mode_w/2; break;
        }

        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        g2.setColor(color);
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(mode, mode_x, mode_y);
        if ( framed ) {
        	g2.setColor(pfd_gc.pfd_markings_color);
        	g2.drawRect(mode_x-pfd_gc.digit_width_xl/2, mode_y - pfd_gc.line_height_xl*15/16, mode_w+pfd_gc.digit_width_xl, pfd_gc.line_height_xl*18/16);
        }
    }

    private void draw2Mode(Graphics2D g2, int col, int raw, String mode, String value, boolean framed, Color color_mode, Color color_value) {
        int mode_w1 = pfd_gc.get_text_width(g2, pfd_gc.font_xl, mode);
        int mode_w2 = pfd_gc.get_text_width(g2, pfd_gc.font_xl, value);
        int mode_w = mode_w1 + mode_w2;
        int mode_x1 = pfd_gc.fma_left + pfd_gc.fma_width/10 + col*pfd_gc.fma_width/5 - mode_w/2;
        int mode_x2 = pfd_gc.fma_left + pfd_gc.fma_width/10 + col*pfd_gc.fma_width/5 - mode_w/2 + mode_w1;        
        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        g2.setColor(color_mode);
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(mode, mode_x1, mode_y);
        g2.setColor(color_value);
        g2.drawString(value, mode_x2, mode_y);
        if ( framed ) {
        	g2.setColor(pfd_gc.pfd_markings_color);
        	g2.drawRect(mode_x1-pfd_gc.digit_width_xl/2, mode_y - pfd_gc.line_height_xl*15/16, mode_w+pfd_gc.digit_width_xl, pfd_gc.line_height_xl*18/16);
        }
    }
    
    private void drawDMode(Graphics2D g2, int col, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_m, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_width/10 + col*pfd_gc.fma_width/5 - mode_w/2;       
        int mode_y = pfd_gc.instrument_size*1050/1000 + pfd_gc.fma_height*raw/5 + pfd_gc.line_height_m - 2;
        g2.setColor(pfd_gc.pfd_markings_color);
        g2.setFont(pfd_gc.font_m);
        g2.drawString(mode, mode_x, mode_y);
    }
    

    private void drawX737FMA(Graphics2D g2) {

        // A/T
        if ( this.avionics.x737_mcp_spd() > 0 ) {
            draw1Mode(g2, 0, 0, "MCP SPD", this.avionics.x737_mcp_spd()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_fmc_spd() > 0 ) {
            draw1Mode(g2, 0, 0, "FMC SPD", this.avionics.x737_fmc_spd()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_retard() > 0 ) {
            draw1Mode(g2, 0, 0, "RETARD", this.avionics.x737_retard()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_thr_hld() > 0 ) {
            draw1Mode(g2, 0, 0, "THR HOLD", this.avionics.x737_thr_hld()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_n1() > 0 ) {
            draw1Mode(g2, 0, 0, "N1", this.avionics.x737_n1()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_athr_armed() ) {
            draw1Mode(g2, 0, 1, "ARM", false, pfd_gc.pfd_armed_color);
        }

//logger.warning("HDG:"+this.avionics.x737_hdg());
        // Lateral
        if ( this.avionics.x737_vorloc() > 0 ) {
            draw1Mode(g2, 2, 0, "VOR/LOC", this.avionics.x737_vorloc()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_hdg() > 0 ) {
            draw1Mode(g2, 2, 0, "HDG SEL", this.avionics.x737_hdg()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_lnav() > 0 ) {
            draw1Mode(g2, 2, 0, "LNAV", this.avionics.x737_lnav()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_toga() > 0 ) {
            draw1Mode(g2, 2, 0, "TO/GA", this.avionics.x737_toga()==2, pfd_gc.pfd_active_color);
        }
        if ( this.avionics.x737_lnav_armed() > 0 ) {
            draw1Mode(g2, 2, 1, "LNAV", this.avionics.x737_lnav_armed()==2, pfd_gc.pfd_armed_color);
        } else if ( this.avionics.x737_vorloc_armed() > 0 ) {
            draw1Mode(g2, 2, 1, "VOR/LOC", this.avionics.x737_vorloc_armed()==2, pfd_gc.pfd_armed_color);
        }


//logger.warning("ALT:"+this.avionics.x737_alt_hld());
        // Vertical
        if ( this.avionics.x737_pitch_spd() > 0 ) {
            draw1Mode(g2, 1, 0, "PTCH SPD", this.avionics.x737_pitch_spd()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_alt_hld() > 0 ) {
            draw1Mode(g2, 1, 0, "ALT HOLD", this.avionics.x737_alt_hld()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_vs() > 0 ) {
            draw1Mode(g2, 1, 0, "V/S", this.avionics.x737_vs()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_vnav_alt() > 0 ) {
            draw1Mode(g2, 1, 0, "VNAV ALT", this.avionics.x737_vnav_alt()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_vnav_path() > 0 ) {
            draw1Mode(g2, 1, 0, "VNAV PTH", this.avionics.x737_vnav_path()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_vnav_spd() > 0 ) {
            draw1Mode(g2, 1, 0, "VNAV SPD", this.avionics.x737_vnav_spd()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_gs() > 0 ) {
            draw1Mode(g2, 1, 0, "G/S", this.avionics.x737_gs()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_flare() > 0 ) {
            draw1Mode(g2, 1, 0, "FLARE", this.avionics.x737_flare()==2, pfd_gc.pfd_active_color);
        } else if ( this.avionics.x737_toga() > 0 ) {
            draw1Mode(g2, 1, 0, "TO/GA", this.avionics.x737_toga()==2, pfd_gc.pfd_active_color);
        }
        if ( this.avionics.x737_vs_armed() > 0 ) {
            draw1Mode(g2, 2, 1, "V/S", this.avionics.x737_vs_armed()==2, pfd_gc.pfd_armed_color);
        } else if ( this.avionics.x737_gs_armed() > 0 ) {
            draw1Mode(g2, 2, 1, "G/S", this.avionics.x737_gs_armed()==2, pfd_gc.pfd_armed_color);
        } else if ( this.avionics.x737_flare_armed() > 0 ) {
            draw1Mode(g2, 2, 1, "FLARE", this.avionics.x737_flare_armed()==2, pfd_gc.pfd_armed_color);
        }

    }


    private void drawFMA(Graphics2D g2) {

        String fma_str = "ERROR";

        // Autothrottle
        if ( this.avionics.autothrottle_on() ) {
            fma_str = "MCP SPD";
            draw1Mode(g2, 4, 2, fma_str, false, pfd_gc.pfd_active_color);
        } else if ( this.avionics.autothrottle_enabled() ) {
            fma_str = "ARM";
            draw1Mode(g2, 4, 2, fma_str, false, pfd_gc.pfd_armed_color);
        }

        if ( this.avionics.autopilot_mode() > 0 ) {

            // Lateral

            boolean hdg_sel_on = this.avionics.ap_hdg_sel_on();
            boolean vorloc_on = this.avionics.ap_vorloc_on();
            boolean bc_on = this.avionics.ap_bc_on();
            boolean lnav_on = this.avionics.ap_lnav_on();
            boolean ltoga_on = this.avionics.ap_ltoga_on();
            boolean roll_on = this.avionics.ap_roll_on();

            if ( hdg_sel_on || vorloc_on || bc_on || lnav_on || ltoga_on || roll_on ) {
                if ( hdg_sel_on ) {
                    fma_str = "HDG SEL";
                } else if ( vorloc_on ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_on ) {
                    fma_str = "B/C";
                } else if ( lnav_on ) {
                    fma_str = "LNAV";
                } else if ( ltoga_on ) {
                    fma_str = "TO/GA";
                } else /* if ( roll_on ) */ {
                    fma_str = "WLV";
                }
                draw1Mode(g2, 2, 0, fma_str, false, pfd_gc.pfd_active_color);
            }

            boolean vorloc_arm = this.avionics.ap_vorloc_arm();
            boolean bc_arm = this.avionics.ap_bc_arm();
            boolean lnav_arm = this.avionics.ap_lnav_arm();
            boolean ltoga_arm = this.avionics.ap_ltoga_arm(); // huh?

            if ( vorloc_arm || bc_arm || lnav_arm || ltoga_arm ) {
                if ( vorloc_arm ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_arm ) {
                    fma_str = "B/C";
                } else if ( lnav_arm ) {
                    fma_str = "LNAV";
                } else /* if ( ltoga_arm ) */ {
                    fma_str = "TO/GA";
                }
                draw1Mode(g2, 2, 1, fma_str, false, pfd_gc.pfd_armed_color);
            }

            // Vertical

            boolean alt_hold_on = this.avionics.ap_alt_hold_on();
            boolean vs_on = this.avionics.ap_vs_on();
            boolean gs_on = this.avionics.ap_gs_on();
            boolean vnav_on = this.avionics.ap_vnav_on();
            boolean vtoga_on = this.avionics.ap_vtoga_on();
            boolean flch_on = this.avionics.ap_flch_on();
            boolean pitch_on = this.avionics.ap_pitch_on();
            int ap_vv = Math.round(this.avionics.autopilot_vv());

            if ( alt_hold_on || vs_on || gs_on || vnav_on || vtoga_on || flch_on || pitch_on ) {
                if ( vnav_on )  {
                    fma_str = "VNAV PTH";
                } else if ( alt_hold_on ) {
                    fma_str = "ALT HOLD";
                } else if ( vs_on ) {
                    fma_str = "V/S "+ap_vv;
                } else if ( gs_on ) {
                    fma_str = "G/S";
                } else if ( vtoga_on ) {
                    fma_str = "TO/GA";
                } else if ( flch_on ) {
                    fma_str = "MCP SPD";
                } else /* if ( pitch_on ) */ {
                    fma_str = "PTCH";
                }
                draw1Mode(g2, 1, 0, fma_str, false, pfd_gc.pfd_active_color);
            }

            boolean alt_hold_arm = this.avionics.ap_alt_hold_arm();
            boolean vs_arm = this.avionics.ap_vs_arm();
            boolean gs_arm = this.avionics.ap_gs_arm();
            boolean vnav_arm = this.avionics.ap_vnav_arm();
            boolean vtoga_arm = this.avionics.ap_vtoga_arm();
            

            if ( alt_hold_arm || vs_arm || gs_arm || vnav_arm || vtoga_arm ) {
                if ( vnav_arm ) {
                    fma_str = "VNAV PTH";
                } else if ( alt_hold_arm ) {
                    fma_str = "ALT HOLD";
                } else if ( vs_arm ) {
                    fma_str = "V/S "+ap_vv;
                } else if ( gs_arm ) {
                    fma_str = "G/S";
                } else /* if ( vtoga_arm ) */ {
                    fma_str = "TO/GA";
                }
                draw1Mode(g2, 1, 1, fma_str, false, pfd_gc.pfd_armed_color);
            }

        }
        
    }

    private void drawA320FMA(Graphics2D g2) {

        String fma_str = "ERROR";
        
        fma_str = "CRZ " + this.avionics.qpac_presel_crz();
        drawDMode(g2,0,0,fma_str);
        fma_str = "CLB " + this.avionics.qpac_presel_clb();
        drawDMode(g2,0,1,fma_str);
        fma_str = "MACH " + this.avionics.qpac_presel_mach();
        drawDMode(g2,0,2,fma_str);
        fma_str = "GD " + this.avionics.qpac_v_green_dot();
        drawDMode(g2,1,1,fma_str);
        fma_str = "VR " + this.avionics.qpac_vr();
        drawDMode(g2,1,2,fma_str);
        fma_str = "vF " + this.avionics.qpac_vf();
        drawDMode(g2,2,0,fma_str);
        fma_str = "vS " + this.avionics.qpac_vs();
        drawDMode(g2,2,1,fma_str);
        fma_str = "ra bug " + this.aircraft.ra_bug();
        drawDMode(g2,3,1,fma_str);
        fma_str = "da_bug " + this.aircraft.da_bug();
        drawDMode(g2,3,2,fma_str);
        fma_str = "qp_fail " + this.avionics.qpac_failures();
        drawDMode(g2,3,0,fma_str);
               
      
        
        // AP Engaged
        String ap_str = "";
        boolean dual_ap = this.avionics.qpac_ap1() && this.avionics.qpac_ap2();
        if (dual_ap) {
        	ap_str="AP 1+2";
        } else if (this.avionics.qpac_ap1()) {
        	ap_str="AP 1";
        } else if (this.avionics.qpac_ap2()) {
        	ap_str="AP 2";
        }    
        draw1Mode(g2,4,0,ap_str,false, pfd_gc.pfd_markings_color);
        
        // FD Engaged
        String fd_str = "";
        if (this.avionics.qpac_fd1()) {fd_str="1";} else {fd_str="-";}
        fd_str+=" FD ";
        if (this.avionics.qpac_fd2()) {fd_str+="2";} else {fd_str+="-";}
        draw1Mode(g2,4,1,fd_str,false, pfd_gc.pfd_markings_color);
        
        // Autopilote phase
        String str_ap_phase="Phase " + this.avionics.qpac_ap_phase();
        int ap_phase = this.avionics.qpac_ap_phase();
        switch (ap_phase) {
        	case 0 : str_ap_phase="PREFLIGHT"; break;
    		case 1 : str_ap_phase="TAKEOFF"; break;
        	case 2 : str_ap_phase="CLIMB"; break;
        	case 3 : str_ap_phase="CRUIZE"; break;
        	case 4 : str_ap_phase="DESCENT"; break;
        	case 5 : str_ap_phase="APPR"; break;
        	case 6 : str_ap_phase="GO ARROUND"; break;
      	   	case 7 : str_ap_phase="DONE"; break; 	
        }
        drawDMode(g2,1,0,str_ap_phase);
        
        // Autopilote vertical mode
        String ap_vertical_mode="Vert " + this.avionics.qpac_ap_vertical_mode();
        int ap_vv = Math.round(this.avionics.autopilot_vv());
        switch (this.avionics.qpac_ap_vertical_mode()) {
        	case -1 : ap_vertical_mode=""; break;
        	case 0 : ap_vertical_mode="SRS"; break;
        	case 1 : ap_vertical_mode="CLB"; break;
        	case 2 : ap_vertical_mode="DES"; break;
        	case 3 : ap_vertical_mode="ALT CST*"; break;
        	case 4 : ap_vertical_mode="ALT CST"; break;
        	case 6 : ap_vertical_mode="G/S*"; break;
        	case 7 : ap_vertical_mode="G/S"; break;
           	case 8 : ap_vertical_mode="F-G/S"; break;            	
        	case 10 : ap_vertical_mode="FLARE"; break;
        	case 11 : ap_vertical_mode="LAND"; break;
        	case 101 : ap_vertical_mode="OP CLB"; break;
        	case 102 : ap_vertical_mode="OP DES"; break;
        	case 103 : ap_vertical_mode="ALT*"; break; // or ALT CRZ*
        	case 104 : ap_vertical_mode="ALT"; break; // or ALT CRZ
        	case 105 : ap_vertical_mode="ALT"; break;
        	case 107 : ap_vertical_mode="V/S "; break; //or FPA + value sim/cockpit2/autopilot/vvi_dial_fpm
        	case 112 : ap_vertical_mode="EXP CLB"; break;
        	case 113 : ap_vertical_mode="EXP DES"; break;       
        }
        if (this.avionics.qpac_ap_vertical_mode()==107 ) {
        	String str_vv = "" + ap_vv;
        	if ( ap_vv >0 ) { str_vv = "+" + ap_vv; }
        	draw2Mode(g2, 1, 0, ap_vertical_mode, str_vv, false, pfd_gc.pfd_active_color, pfd_gc.pfd_armed_color);        	
        } else {
        	draw1Mode(g2, 1, 0, ap_vertical_mode, false, pfd_gc.pfd_active_color);
        }

        // Autopilote vertical armed mode
        String ap_vertical_armed="Vert " + this.avionics.qpac_ap_vertical_armed();
        Color ap_vert_armed_color= pfd_gc.pfd_armed_color;
        switch (this.avionics.qpac_ap_vertical_armed()) {
    		case 0 : ap_vertical_armed = ""; break;
    		case 1 : ap_vertical_armed = "G/S"; break;
    		case 2 : ap_vertical_armed = "CLB"; break;    		
        	case 6 : ap_vertical_armed = "ALT"; break;
        	case 7 : ap_vertical_armed = "ALT G/S"; break;
        	case 8 : ap_vertical_armed = "ALT"; 
        			 ap_vert_armed_color= pfd_gc.pfd_managed_color;
        			 break;        	
        	case 10 : ap_vertical_armed = "OP CLB"; break;        	
        }
        draw1Mode(g2, 1, 1, ap_vertical_armed, false, ap_vert_armed_color);        
        
        // Autopilote lateral mode
        String ap_lateral_mode="Lat " + this.avionics.qpac_ap_lateral_mode();
        switch (this.avionics.qpac_ap_lateral_mode()) {
			case -1 : ap_lateral_mode=""; break; 
       		case 1 : ap_lateral_mode="RWY TRK"; break; 
    		case 2 : ap_lateral_mode="NAV"; break; 
    		case 6 : ap_lateral_mode="LOC*"; break; 
    		case 7 : ap_lateral_mode="LOC"; break; 
    		case 10 : ap_lateral_mode="ROLL OUT"; break; 
    		case 11 : ap_lateral_mode="LAND"; break; // or FLARE
    		case 12 : ap_lateral_mode="GA TRK"; break;  
    		case 101 : ap_lateral_mode="HDG"; break;
        }
        draw1Mode(g2, 2, 0, ap_lateral_mode, false, pfd_gc.pfd_active_color);

        // Autopilote lateral armed mode
        String ap_lateral_armed="Lat " + this.avionics.qpac_ap_lateral_armed();
        switch (this.avionics.qpac_ap_lateral_armed()) {
    		case 0 : ap_lateral_armed=""; break;
    		case 1 : ap_lateral_armed="LOC"; break;   		
    		case 2 : ap_lateral_armed="NAV"; break;
        }
        draw1Mode(g2, 2, 1, ap_lateral_armed, false, pfd_gc.pfd_armed_color);

                
        
        // Autothrust (it's not autothrottle !!!)
        String str_athr_mode = "A/THR";
        String str_athr_mode2 = "M2 "+this.avionics.qpac_athr_mode2();  
        switch (this.avionics.qpac_athr_mode2()) {
        case 0 : str_athr_mode2 = "THR MCT"; break;
        case 1 : str_athr_mode2 = "THR CLB"; break;
        case 2 : str_athr_mode2 = "THR IDLE"; break;
        case 3 : str_athr_mode2 = "THR IDLE"; break;
        case 4 : str_athr_mode2 = "SPEED"; break;
        case 5 : str_athr_mode2 = "MACH"; break;
        }
        if (this.avionics.qpac_athr_mode()==1) {
        	draw1Mode(g2, 4, 2, str_athr_mode, false, pfd_gc.pfd_armed_color);
        }  else if (this.avionics.qpac_athr_mode()==2) {
        	draw1Mode(g2, 4, 2, str_athr_mode, false, pfd_gc.pfd_markings_color);
        	draw1Mode(g2, 0, 0, str_athr_mode2, false, pfd_gc.pfd_active_color);
        }
       
        
        
        String str_athr_limited = "A/T Lim" + this.avionics.qpac_athr_limited();
        if (this.avionics.qpac_athr_limited()!=0 ) {
        	draw1Mode(g2, 3, 0, str_athr_limited, false, pfd_gc.pfd_markings_color);
        }

        String str_thr_warning = "TWarn " + this.avionics.qpac_fma_thr_warning();       
        if (this.avionics.qpac_fma_thr_warning()==1) { 
        	str_thr_warning = "LVR CLB";
        	draw1Mode(g2, 0, 2, str_thr_warning, false, pfd_gc.pfd_markings_color);
        } else if (this.avionics.qpac_fma_thr_warning()==4) { 
        	str_thr_warning = "THR LK";
        	draw1Mode(g2, 0, 2, str_thr_warning, false, pfd_gc.pfd_caution_color);
        } else if (this.avionics.qpac_fma_thr_warning()==2) { 
        	str_thr_warning = "LVR MCT";
        	draw1Mode(g2, 0, 2, str_thr_warning, false, pfd_gc.pfd_markings_color);
        } else if (this.avionics.qpac_fma_thr_warning()==3) { 
        	str_thr_warning = "LVR ASYM";
        	draw1Mode(g2, 0, 2, str_thr_warning, false, pfd_gc.pfd_caution_color);
        } else if  (this.avionics.qpac_fma_thr_warning()>4) {
        	draw1Mode(g2, 0, 2, str_thr_warning, false, pfd_gc.pfd_caution_color);
        }
        
        // Manual Lever modes
        String str_speed_mode = "LVR " + this.avionics.qpac_thr_lever_mode();
        String str_man = "MAN";
        if (this.avionics.qpac_thr_lever_mode()>0) {
        	draw1Mode(g2, 0, 0, str_man, false, pfd_gc.pfd_markings_color);        	
        }   
        if (this.avionics.qpac_thr_lever_mode()==3) {
        	str_speed_mode = "TOGA";
        	draw1Mode(g2, 0, 1, str_speed_mode, false, pfd_gc.pfd_markings_color);
        } else  if (this.avionics.qpac_thr_lever_mode()==2) {
        	str_speed_mode = "FLX ";
        	String str_speed_val = "+"+this.avionics.qpac_flex_temp();
        	draw2Mode(g2, 0, 1, str_speed_mode, str_speed_val, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        } else  if (this.avionics.qpac_thr_lever_mode()==1) {
        	str_speed_mode = "THR";
        	draw1Mode(g2, 0, 1, str_speed_mode, false, pfd_gc.pfd_markings_color);
        } else  if (this.avionics.qpac_thr_lever_mode()==4) {
        	str_speed_mode = "MCT";
        	draw1Mode(g2, 0, 1, str_speed_mode, false, pfd_gc.pfd_markings_color);
        } else if (this.avionics.qpac_thr_lever_mode()>4) {
        	draw1Mode(g2, 0, 0, str_speed_mode, false, pfd_gc.pfd_caution_color);        	
        }
  
        // engagement status show if A_Floor = 0 and/or athr_mode >= 2
        // THR LVR if athrmode2 = 1 and/or athrlimited = 1
        // SPEED  athrmode2  = 4
        // MACH  athrmode2  = 5
        // THR IDLE athrmode2  = 3 (Flare mode)
        // THR IDLE athrmode2  = 2 and athr limited = 0        
        // THR CLB athrmode2  = 1 and athr limited = 0
        // THR MCT athrmode2  = 0 and athr limited = 0
        // THR DES ???
        
        // Minimums
        if (ap_phase == 4 || ap_phase == 5 ) {
        	String str_dh_mda ="";       
        	String str_dh_mda_value ="";             
        	switch (this.avionics.qpac_appr_type()) {
        	case 0: 
        		if (this.aircraft.ra_bug() != -10.0f) { 
        			str_dh_mda = "DH ";
        			str_dh_mda_value = "" + this.aircraft.ra_bug();
        			draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		} else {
        			str_dh_mda ="NO DH";
        		}
        		break;
        	case 1: 
        		str_dh_mda = "MDA "; 
        		str_dh_mda_value = "" + this.aircraft.da_bug();         	
        		draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		break;
        	case 2: 
        		str_dh_mda = "BARO ";
        		str_dh_mda_value = "" + this.aircraft.da_bug(); 
        		draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		break;
        	}      	
        }
        
        
        
        if ( this.avionics.autothrottle_on() ) {
            fma_str = "A/THR";
            draw1Mode(g2, 4, 2, fma_str, false, pfd_gc.pfd_markings_color);
        } else if ( this.avionics.autothrottle_enabled() ) {
            fma_str = "A/THR";
            draw1Mode(g2, 4, 2, fma_str, false, pfd_gc.pfd_armed_color);
        }

        if ( this.avionics.autopilot_mode() > 0 ) {

            // Lateral

            boolean hdg_sel_on = this.avionics.ap_hdg_sel_on();
            boolean vorloc_on = this.avionics.ap_vorloc_on();
            boolean bc_on = this.avionics.ap_bc_on();
            boolean lnav_on = this.avionics.ap_lnav_on();
            boolean ltoga_on = this.avionics.ap_ltoga_on();
            boolean roll_on = this.avionics.ap_roll_on();

            if ( hdg_sel_on || vorloc_on || bc_on || lnav_on || ltoga_on || roll_on ) {
                if ( hdg_sel_on ) {
                    fma_str = "HDG SEL";
                } else if ( vorloc_on ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_on ) {
                    fma_str = "B/C";
                } else if ( lnav_on ) {
                    fma_str = "LNAV";
                } else if ( ltoga_on ) {
                    fma_str = "TO/GA";
                } else /* if ( roll_on ) */ {
                    fma_str = "WLV";
                }
                draw1Mode(g2, 2, 0, fma_str, false, pfd_gc.pfd_markings_color);
            }

            boolean vorloc_arm = this.avionics.ap_vorloc_arm();
            boolean bc_arm = this.avionics.ap_bc_arm();
            boolean lnav_arm = this.avionics.ap_lnav_arm();
            boolean ltoga_arm = this.avionics.ap_ltoga_arm(); // huh?

            if ( vorloc_arm || bc_arm || lnav_arm || ltoga_arm ) {
                if ( vorloc_arm ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_arm ) {
                    fma_str = "B/C";
                } else if ( lnav_arm ) {
                    fma_str = "LNAV";
                } else /* if ( ltoga_arm ) */ {
                    fma_str = "TO/GA";
                }
                draw1Mode(g2, 2, 1, fma_str, false,  pfd_gc.pfd_armed_color);
            }

            // Vertical

            boolean alt_hold_on = this.avionics.ap_alt_hold_on();
            boolean vs_on = this.avionics.ap_vs_on();
            boolean gs_on = this.avionics.ap_gs_on();
            boolean vnav_on = this.avionics.ap_vnav_on();
            boolean vtoga_on = this.avionics.ap_vtoga_on();
            boolean flch_on = this.avionics.ap_flch_on();
            boolean pitch_on = this.avionics.ap_pitch_on();

            if ( alt_hold_on || vs_on || gs_on || vnav_on || vtoga_on || flch_on || pitch_on ) {
                if ( vnav_on )  {
                    fma_str = "VNAV PTH";
                } else if ( alt_hold_on ) {
                    fma_str = "ALT HOLD";
                } else if ( vs_on ) {
                    fma_str = "V/S";
                } else if ( gs_on ) {
                    fma_str = "G/S";
                } else if ( vtoga_on ) {
                    fma_str = "TO/GA";
                } else if ( flch_on ) {
                    fma_str = "MCP SPD";
                } else /* if ( pitch_on ) */ {
                    fma_str = "PTCH";
                }
                draw1Mode(g2, 1, 0, fma_str, false, pfd_gc.pfd_active_color);
            }

            boolean alt_hold_arm = this.avionics.ap_alt_hold_arm();
            boolean vs_arm = this.avionics.ap_vs_arm();
            boolean gs_arm = this.avionics.ap_gs_arm();
            boolean vnav_arm = this.avionics.ap_vnav_arm();
            boolean vtoga_arm = this.avionics.ap_vtoga_arm();

            if ( alt_hold_arm || vs_arm || gs_arm || vnav_arm || vtoga_arm ) {
                if ( vnav_arm ) {
                    fma_str = "VNAV PTH";
                } else if ( alt_hold_arm ) {
                    fma_str = "ALT HOLD";
                } else if ( vs_arm ) {
                    fma_str = "V/S";
                } else if ( gs_arm ) {
                    fma_str = "G/S";
                } else /* if ( vtoga_arm ) */ {
                    fma_str = "TO/GA";
                }
                draw1Mode(g2, 1, 1, fma_str, false, pfd_gc.pfd_armed_color);
            }
            
            // col 0, raw 3
            // Presel speed : show if show if AirbusFBW/FMATHRWarning = 0 and com/peteraircraft/airbus/PreSelSPD_on = 1
            // MACH:SEL .00 show if AirbusFBW/FMATHRWarning = 0 and sim/cockpit2/autopilot/airspeed_is_mach =1 and ap_phase = 2
            // value is AirbusFBW/PreselMach

            // SPEED SEL 000 show if AirbusFBW/FMATHRWarning = 0 and sim/cockpit2/autopilot/airspeed_is_mach =0 and ap_phase = 1 or 2
            // ap_phase = 1 value is AirbusFBW/Presel_CLB
            // ap_phase = 2 value is AirbusFBW/Presel_CRZ            

            // if (NPA_NO_POINTS==2 and/or NPAValid == 1) and ap_vertical_mode == 8
            // Column 3, raw 0 : F-APP
            // Column 3, raw 1 : +RAW
            

        }
        
    }

    
}
