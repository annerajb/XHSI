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
import net.sourceforge.xhsi.flightdeck.pfd.PFDFramedElement.PFE_Align;
import net.sourceforge.xhsi.flightdeck.pfd.PFDFramedElement.PFE_Color;
import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.SimDataRepository;
import net.sourceforge.xhsi.model.xplane.XPlaneSimDataRepository;


public class FMA_A320 extends PFDSubcomponent {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
        
    PFDFramedElement pfe_thrust;
    PFDFramedElement pfe_thrust_message;
    PFDFramedElement pfe_ap;
    PFDFramedElement pfe_fd;
    PFDFramedElement pfe_athr;
    PFDFramedElement pfe_vert_mode;
    PFDFramedElement pfe_vert_armed;
    PFDFramedElement pfe_lat_mode;
    PFDFramedElement pfe_lat_armed;
    PFDFramedElement pfe_land_cat;
    PFDFramedElement pfe_land_mode;
    PFDFramedElement pfe_land_minimums;
          
    public FMA_A320(ModelFactory model_factory, PFDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        pfe_thrust = new PFDFramedElement(0,0, hsi_gc, PFE_Color.PFE_COLOR_ACTIVE);
        pfe_thrust_message = new PFDFramedElement(0,2, hsi_gc, PFE_Color.PFE_COLOR_MARK, PFE_Align.LEFT);
        pfe_ap = new PFDFramedElement(4,0, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_fd = new PFDFramedElement(4,1, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_athr = new PFDFramedElement(4,2, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_vert_mode = new PFDFramedElement(1,0, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_vert_armed = new PFDFramedElement(1,1, hsi_gc, PFE_Color.PFE_COLOR_MARK, PFE_Align.LEFT);
        pfe_lat_mode = new PFDFramedElement(2,0, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_lat_armed = new PFDFramedElement(2,1, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_land_cat = new PFDFramedElement(3,0, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_land_mode = new PFDFramedElement(3,1, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_land_minimums = new PFDFramedElement(3,2, hsi_gc, PFE_Color.PFE_COLOR_MARK);
        pfe_thrust_message.disableFraming();
        pfe_land_minimums.disableFraming();
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
				} else if (this.avionics.is_jar_a320neo() ) {
					drawA320_NEO_FMA(g2);
				} else {
					drawSystemStatus(g2);
					drawFMA(g2);
				}
			}
        }
    }


    private void drawBox(Graphics2D g2) {
        // pfd_gc.setTransparent(g2, this.preferences.get_draw_fullscreen_horizon() || ( this.preferences.get_draw_fullwidth_horizon() && pfd_gc.draw_hsi ) );
        // g2.fillRect(pfd_gc.fma_left - 1, pfd_gc.fma_top - 1, pfd_gc.fma_width + 3, pfd_gc.fma_height + 3);
        pfd_gc.setOpaque(g2);
        g2.setColor(pfd_gc.instrument_background_color);
        //g2.setColor(pfd_gc.pfd_box_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_1, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_1, pfd_gc.fma_top + pfd_gc.fma_height);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top + pfd_gc.fma_height * 2/3);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_3, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_3, pfd_gc.fma_top + pfd_gc.fma_height);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_4, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_4, pfd_gc.fma_top + pfd_gc.fma_height);
    }


    private void drawSystemStatus(Graphics2D g2) {

        int ap_mode = this.avionics.autopilot_mode();
        
        // AP Engaged
        if (ap_mode == 2) {
        	pfe_ap.setText("AP 1", PFE_Color.PFE_COLOR_MARK);
        }  else pfe_ap.clearText();  
        pfe_ap.paint(g2);
              
        // FD Engaged
        if ( ap_mode > 0 ) {
        	pfe_fd.setText("1 FD 1", PFE_Color.PFE_COLOR_MARK);
        } else pfe_fd.clearText();
        pfe_fd.paint(g2);
    }



    private void drawFinalMode(Graphics2D g2, int raw, String mode, boolean framed, Color color) {
        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_xl, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_col_1 + (pfd_gc.fma_col_3 - pfd_gc.fma_col_1)/2 - mode_w/2;
        int mode_y = pfd_gc.fma_top + pfd_gc.fma_height*raw/3 + pfd_gc.line_height_xl - 2;
        // Erase middle line
        g2.setColor(pfd_gc.background_color);
        g2.drawLine(pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top, pfd_gc.fma_left + pfd_gc.fma_col_2, pfd_gc.fma_top + pfd_gc.fma_height * 2/3);
        g2.setColor(color);
        g2.setFont(pfd_gc.font_xl);
        g2.drawString(mode, mode_x, mode_y);
        if ( framed ) {
        	g2.setColor(pfd_gc.pfd_markings_color);
        	g2.drawRect(mode_x-pfd_gc.digit_width_xl/2, mode_y - pfd_gc.line_height_xl*15/16, mode_w+pfd_gc.digit_width_xl, pfd_gc.line_height_xl*18/16);
        }
    }
    
    
    private void drawDMode(Graphics2D g2, int col, int raw, String mode) {
        int mode_w = pfd_gc.get_text_width(g2, pfd_gc.font_m, mode);
        int mode_x = pfd_gc.fma_left + pfd_gc.fma_width/10 + col*pfd_gc.fma_width/5 - mode_w/2;       
        int mode_y = pfd_gc.instrument_size*1050/1000 + pfd_gc.fma_height*raw/5 + pfd_gc.line_height_m - 2;
        g2.setColor(pfd_gc.pfd_markings_color);
        g2.setFont(pfd_gc.font_s);
        g2.drawString(mode, mode_x, mode_y);
    }
    

    private void drawX737FMA(Graphics2D g2) {
        int ap_vv = Math.round(this.avionics.autopilot_vv());
        
        // A/T
        if ( this.avionics.x737_mcp_spd() > 0 ) {
        	// speed selected
        	pfe_thrust.setText("MCP SPD", PFE_Color.PFE_COLOR_ACTIVE);
        	pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_MARK);
        } else if ( this.avionics.x737_fmc_spd() > 0 ) {
        	// speed managed
        	pfe_thrust.setText("FMC SPD", PFE_Color.PFE_COLOR_ACTIVE);
        	pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_MARK);
        } else if ( this.avionics.x737_retard() > 0 ) {
        	pfe_thrust.setText("RETARD", PFE_Color.PFE_COLOR_ACTIVE);
        	pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_MARK);
        } else if ( this.avionics.x737_thr_hld() > 0 ) {
        	pfe_thrust.setText("THR HOLD", PFE_Color.PFE_COLOR_ACTIVE);
        	pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_MARK);
        } else if ( this.avionics.x737_n1() > 0 ) {
        	pfe_thrust.setText("N1", PFE_Color.PFE_COLOR_ACTIVE);
        	pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_MARK);
        } else if ( this.avionics.x737_athr_armed() ) {
        	pfe_thrust.clearText();
        	pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_ARMED);
        } else {
        	pfe_thrust.clearText();
        	pfe_athr.clearText();
        }
        pfe_thrust.paint(g2);
        pfe_athr.paint(g2);

        // Lateral
        if ( this.avionics.x737_vorloc() > 0 ) {
        	pfe_lat_mode.setText("VOR/LOC", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_hdg() > 0 ) {
        	pfe_lat_mode.setText("HDG", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_lnav() > 0 ) {
        	pfe_lat_mode.setText("NAV", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_toga() > 0 ) {
        	pfe_lat_mode.setText("TO/GA", PFE_Color.PFE_COLOR_ACTIVE);
        } else pfe_lat_mode.clearText();
        // Lateral Armed
        if ( this.avionics.x737_lnav_armed() > 0 ) {
        	pfe_lat_armed.setText("NAV", PFE_Color.PFE_COLOR_ARMED);
        } else if ( this.avionics.x737_vorloc_armed() > 0 ) {
        	pfe_lat_armed.setText("VOR/LOC", PFE_Color.PFE_COLOR_ARMED);
        } else pfe_lat_armed.clearText();
        pfe_lat_mode.paint(g2);
        pfe_lat_armed.paint(g2);

        // Vertical
        if ( this.avionics.x737_pitch_spd() > 0 ) {
        	pfe_vert_mode.setText("PTCH SPD", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_alt_hld() > 0 ) {
        	pfe_vert_mode.setText("ALT", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_vs() > 0 ) {
        	pfe_vert_mode.setTextValue("V/S"," "+ap_vv, PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_vnav_alt() > 0 ) {
        	pfe_vert_mode.setText("VNAV ALT", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_vnav_path() > 0 ) {
        	pfe_vert_mode.setText("VNAV PTH", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_vnav_spd() > 0 ) {
        	pfe_vert_mode.setText("VNAV SPD", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_gs() > 0 ) {
        	pfe_vert_mode.setText("G/S", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_flare() > 0 ) {
        	pfe_vert_mode.setText("FLARE", PFE_Color.PFE_COLOR_ACTIVE);
        } else if ( this.avionics.x737_toga() > 0 ) {
        	pfe_vert_mode.setText("TO/GA", PFE_Color.PFE_COLOR_ACTIVE);
        } else pfe_vert_mode.clearText();
        // Vertical Armed
        if ( this.avionics.x737_vs_armed() > 0 ) {
        	pfe_vert_armed.setText("V/S", PFE_Color.PFE_COLOR_ARMED);
        } else if ( this.avionics.x737_gs_armed() > 0 ) {
        	pfe_vert_armed.setText("G/S", PFE_Color.PFE_COLOR_ARMED);
        } else if ( this.avionics.x737_flare_armed() > 0 ) {
        	pfe_vert_armed.setText("FLARE", PFE_Color.PFE_COLOR_ARMED);
        } else pfe_vert_armed.clearText();
        pfe_vert_mode.paint(g2);
        pfe_vert_armed.paint(g2); 
        
        // TODO: Display MDA / DH        
    }


    private void drawFMA(Graphics2D g2) {
    	// This function displays an Airbus FMA with X-Plane builtin autopilot

        String fma_str = "ERROR";

        // Autothrottle
        if ( this.avionics.autothrottle_on() ) {        
            pfe_thrust.setText("SPEED", PFE_Color.PFE_COLOR_ACTIVE);
            pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_MARK);
        } else if ( this.avionics.autothrottle_enabled() ) {           
            pfe_thrust.clearText();
            pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_ARMED);
        } else { 
        	pfe_athr.clearText();
        	pfe_thrust.clearText();
        }
        pfe_athr.paint(g2);
        pfe_thrust.paint(g2);

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
                    fma_str = "HDG";
                } else if ( vorloc_on ) {
                    fma_str = "VOR/LOC";
                } else if ( bc_on ) {
                    fma_str = "B/C";
                } else if ( lnav_on ) {
                    fma_str = "NAV";
                } else if ( ltoga_on ) {
                    fma_str = "TO/GA";
                } else /* if ( roll_on ) */ {
                    fma_str = "WLV";
                }
                pfe_lat_mode.setText(fma_str, PFE_Color.PFE_COLOR_ACTIVE);                
            } else pfe_lat_mode.clearText();
            
            pfe_lat_mode.paint(g2);

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
                pfe_lat_armed.setText(fma_str, PFE_Color.PFE_COLOR_ARMED);                
                // draw1Mode(g2, 2, 1, fma_str, false, pfd_gc.pfd_armed_color);
            } else pfe_lat_armed.clearText();
            pfe_lat_armed.paint(g2);

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
                    pfe_vert_mode.setText("VNAV PTH", PFE_Color.PFE_COLOR_ACTIVE);
                } else if ( alt_hold_on ) {                   
                    pfe_vert_mode.setText("ALT", PFE_Color.PFE_COLOR_ACTIVE);
                } else if ( vs_on ) {
                    fma_str = " "+ap_vv;
                    pfe_vert_mode.setTextValue("V/S", fma_str, PFE_Color.PFE_COLOR_ACTIVE);
                } else if ( gs_on ) {                    
                    pfe_vert_mode.setText("G/S", PFE_Color.PFE_COLOR_ACTIVE);
                } else if ( vtoga_on ) {                    
                    pfe_vert_mode.setText("TO/GA", PFE_Color.PFE_COLOR_ACTIVE);
                } else if ( flch_on ) {
                    // Flight level change
                	// TODO : OP CLB or OP DES depending on current altitude and targer altitude
                    pfe_vert_mode.setText("CLB/DES", PFE_Color.PFE_COLOR_ACTIVE);
                } else /* if ( pitch_on ) */ {                    
                    pfe_vert_mode.setText("PITCH", PFE_Color.PFE_COLOR_ACTIVE);
                }                
            } else pfe_vert_mode.clearText();
            pfe_vert_mode.paint(g2);

            boolean alt_hold_arm = this.avionics.ap_alt_hold_arm();
            boolean vs_arm = this.avionics.ap_vs_arm();
            boolean gs_arm = this.avionics.ap_gs_arm();
            boolean vnav_arm = this.avionics.ap_vnav_arm();
            boolean vtoga_arm = this.avionics.ap_vtoga_arm();
            

            if ( alt_hold_arm || vs_arm || gs_arm || vnav_arm || vtoga_arm ) {
                if ( vnav_arm ) {
                    fma_str = "VNAV PTH";
                    pfe_vert_armed.setText("VNAV PTH", PFE_Color.PFE_COLOR_ARMED);
                } else if ( alt_hold_arm ) {
                    fma_str = "ALT HOLD";
                    pfe_vert_armed.setText("ALT", PFE_Color.PFE_COLOR_ARMED);
                } else if ( vs_arm ) {
                    fma_str = "V/S "+ap_vv;
                    pfe_vert_armed.setText(fma_str, PFE_Color.PFE_COLOR_ARMED);
                } else if ( gs_arm ) {
                    fma_str = "G/S";
                    pfe_vert_armed.setText("G/S", PFE_Color.PFE_COLOR_ARMED);
                } else /* if ( vtoga_arm ) */ {
                    fma_str = "TO/GA";
                    pfe_vert_armed.setText("TO/GA", PFE_Color.PFE_COLOR_ARMED);
                }               
            } else pfe_vert_armed.clearText();
            pfe_vert_armed.paint(g2);
        }
        
    }

    private void drawA320FMA(Graphics2D g2) {

        String fma_str = "ERROR";
        /*
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
        
        fma_str = "Range " + this.avionics.map_range();
        drawDMode(g2,1,3,fma_str);
        fma_str = "Mode " + this.avionics.map_submode();
        drawDMode(g2,1,4,fma_str);
        
        fma_str = "vF " + this.avionics.qpac_vf();
        drawDMode(g2,2,0,fma_str);
        fma_str = "vS " + this.avionics.qpac_vs();
        drawDMode(g2,2,1,fma_str);
        fma_str = "Qpac " + this.avionics.qpac_version();
        drawDMode(g2,2,2,fma_str);        
        fma_str = "VMO " + this.avionics.qpac_vmo();
        
        drawDMode(g2,3,0,fma_str);
        fma_str = "VLS " + this.avionics.qpac_vls();
        drawDMode(g2,3,1,fma_str);
        fma_str = "ra bug " + this.aircraft.ra_bug();
        drawDMode(g2,3,2,fma_str);
        fma_str = "qpac_da " + this.avionics.qpac_appr_mda();
        drawDMode(g2,3,3,fma_str);        
        fma_str = "vso " + this.aircraft.get_Vso();
        drawDMode(g2,3,4,fma_str);        
        fma_str = "fd_v " + this.avionics.qpac_fd_ver_bar();
        drawDMode(g2,3,5,fma_str);        
        fma_str = "fd h " + this.avionics.qpac_fd_hor_bar();
        drawDMode(g2,3,6,fma_str);
        fma_str = "ils on " + this.avionics.qpac_ils_on();
        drawDMode(g2,3,7,fma_str);
        
        
        fma_str = "aProt " + this.avionics.qpac_alpha_prot();
        drawDMode(g2,4,0,fma_str);
        fma_str = "aMax " + this.avionics.qpac_alpha_max();
        drawDMode(g2,4,1,fma_str);
        fma_str = "vno " + this.aircraft.get_Vno();
        drawDMode(g2,4,3,fma_str);   
        fma_str = "vle " + this.aircraft.get_Vle();
        drawDMode(g2,4,4,fma_str);
        fma_str = "vfe " + this.aircraft.get_Vfe();
        drawDMode(g2,4,5,fma_str);
        fma_str = "APPR " + this.avionics.qpac_appr_illuminated();
        drawDMode(g2,0,3,fma_str); 
        fma_str = "NPA Valid " + this.avionics.qpac_npa_valid();
        drawDMode(g2,0,4,fma_str); 
        fma_str = "NPA No Points " + this.avionics.qpac_npa_no_points();
        drawDMode(g2,0,5,fma_str); 
        fma_str = "ApprType " + this.avionics.qpac_appr_type();
        drawDMode(g2,0,6,fma_str);
        */
        
        
        // AP Engaged
        String ap_str = "";       
        boolean dual_ap = this.avionics.qpac_ap1() && this.avionics.qpac_ap2();
        boolean single_ap = (this.avionics.qpac_ap1() || this.avionics.qpac_ap2()) && ! dual_ap;       
        if (dual_ap) {
        	ap_str="AP 1+2";
        } else if (this.avionics.qpac_ap1()) {
        	ap_str="AP 1";
        } else if (this.avionics.qpac_ap2()) {
        	ap_str="AP 2";
        }
        pfe_ap.setText(ap_str, PFE_Color.PFE_COLOR_MARK);
        pfe_ap.paint(g2);
 
        
        // FD Engaged
        String fd_str = "";
        if (this.avionics.qpac_fd1()) {fd_str="1";} else {fd_str="-";}
        fd_str+=" FD ";
        if (this.avionics.qpac_fd2()) {fd_str+="2";} else {fd_str+="-";}
        pfe_fd.setText(fd_str, PFE_Color.PFE_COLOR_MARK);
        pfe_fd.paint(g2);        

        
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
        // drawDMode(g2,1,0,str_ap_phase);
        
        // Autopilote vertical mode
        boolean col_2_3 = false; // Column 2 + 3
        String final_mode = "";
        String ap_vertical_mode="Vert " + this.avionics.qpac_ap_vertical_mode();
        int ap_vv = Math.round(this.avionics.autopilot_vv());
        switch (this.avionics.qpac_ap_vertical_mode()) {
        	case -1 : ap_vertical_mode=""; pfe_vert_mode.clearText(); break;
        	case 0 : ap_vertical_mode="SRS"; break;
        	case 1 : ap_vertical_mode="CLB"; break;
        	case 2 : ap_vertical_mode="DES"; break;
        	case 3 : ap_vertical_mode="ALT CST*"; break;
        	case 4 : ap_vertical_mode="ALT CST"; break;
        	case 6 : ap_vertical_mode="G/S*"; break;
        	case 7 : ap_vertical_mode="G/S"; break;
           	case 8 : ap_vertical_mode="F-G/S"; break;            	
        	case 10 : ap_vertical_mode="FLARE"; final_mode="FLARE"; col_2_3 = true; break;
        	case 11 : ap_vertical_mode="LAND"; final_mode="LAND"; col_2_3 = true; break;
        	case 101 : ap_vertical_mode="OP CLB"; break;
        	case 102 : ap_vertical_mode="OP DES"; break;
        	case 103 : ap_vertical_mode="ALT*"; break; // or ALT CRZ*
        	case 104 : 
        	case 105 : if (ap_phase==3) ap_vertical_mode="ALT CRZ"; else ap_vertical_mode="ALT"; break; 
        	case 107 : if (this.avionics.qpac_fcu_hdg_trk()) ap_vertical_mode="FPA "; else ap_vertical_mode="V/S "; break; //or FPA + value sim/cockpit2/autopilot/vvi_dial_fpm
        	case 112 : ap_vertical_mode="EXP CLB"; break;
        	case 113 : ap_vertical_mode="EXP DES"; break;       
        }
        if (! col_2_3) { 
        	if (this.avionics.qpac_ap_vertical_mode()==107 ) {
        		// TODO : fix the FPA value display 
        		String str_vv = "" + ap_vv;
        		if ( ap_vv >0 ) { str_vv = "+" + ap_vv; }
        		pfe_vert_mode.setTextValue(ap_vertical_mode, str_vv, PFE_Color.PFE_COLOR_ACTIVE);
        		pfe_vert_mode.paint(g2);
        	} else {
        		pfe_vert_mode.setText(ap_vertical_mode, PFE_Color.PFE_COLOR_ACTIVE);
        		pfe_vert_mode.paint(g2);
        	}
        }

        // Autopilote vertical armed mode
        switch (this.avionics.qpac_ap_vertical_armed()) {
    		case 0 : pfe_vert_armed.clearText(); break;
    		case 1 : if (this.avionics.qpac_npa_valid() == 0) 
    					pfe_vert_armed.setText("G/S", PFE_Color.PFE_COLOR_ARMED); 
    				 else 
    					pfe_vert_armed.setText("FINAL", PFE_Color.PFE_COLOR_ARMED);
    				 break;
    		case 2 : pfe_vert_armed.setText("CLB", PFE_Color.PFE_COLOR_ARMED); break;
    		case 4 : pfe_vert_armed.setText("DES", PFE_Color.PFE_COLOR_ARMED); break;
    		case 5 : if (this.avionics.qpac_npa_valid() == 0) 
    					pfe_vert_armed.setText("DES G/S", PFE_Color.PFE_COLOR_ARMED); 
			 		 else 
			 			pfe_vert_armed.setText("DES FNL", PFE_Color.PFE_COLOR_ARMED);
			 		 break;    			    			    		
        	case 6 : pfe_vert_armed.setText("ALT", PFE_Color.PFE_COLOR_ARMED); break;
        	case 7 : if (this.avionics.qpac_npa_valid() == 0) 
        				pfe_vert_armed.setText("ALT G/S", PFE_Color.PFE_COLOR_ARMED); 
			 		 else 
			 			pfe_vert_armed.setText("ALT FNL", PFE_Color.PFE_COLOR_ARMED);
			 		 break;         		 
        	case 8 : pfe_vert_armed.setText("ALT", PFE_Color.PFE_COLOR_MANAGED); break;
        	case 9 : if (this.avionics.qpac_npa_valid() == 0) 
        			 	pfe_vert_armed.setTextValue("ALT", " G/S", PFE_Color.PFE_COLOR_MANAGED); 
	 		 		 else 
	 		 			pfe_vert_armed.setTextValue("ALT", " FNL", PFE_Color.PFE_COLOR_MANAGED);
	 		 		 break; 			 		 		 
        	case 10 : pfe_vert_armed.setText("OP CLB", PFE_Color.PFE_COLOR_ARMED); break;        	
        }       
        pfe_vert_armed.paint(g2);
        
        // TODO : if APPRilluminated and this.avionics.qpac_npa_valid()==1 => DES FNL or ALT FNL (may be ALT CST)
        // TODO : FLS Armed F-G/S this.avionics.qpac_npa_valid()==1 and this.avionics.qpac_npa_no_points()==2 and this.avionics.qpac_appr_illuminated()==1 and APVerticalMode between 2 and 4 (DES or ALT CST)
        // TODO : "LOC B/C" nav_status=2 & backcourse_on=1 & HSI_source_select_pilot <= 1,5
        // TODO : "F-LOC" si AP Vert Mode = 8 & NPANoPoints=2
        // Autopilote lateral mode
        String ap_lateral_mode="Lat " + this.avionics.qpac_ap_lateral_mode();
        switch (this.avionics.qpac_ap_lateral_mode()) {
			case -1 : pfe_lat_mode.clearText(); break; 
			case 0 : pfe_lat_mode.setText("RWY", PFE_Color.PFE_COLOR_ACTIVE); break; 
       		case 1 : pfe_lat_mode.setText("RWY TRK", PFE_Color.PFE_COLOR_ACTIVE);break; 
    		case 2 : pfe_lat_mode.setText("NAV", PFE_Color.PFE_COLOR_ACTIVE); break; 
    		case 6 : pfe_lat_mode.setText("LOC*", PFE_Color.PFE_COLOR_ACTIVE); break; 
    		case 7 : pfe_lat_mode.setText("LOC", PFE_Color.PFE_COLOR_ACTIVE); break; 
    		case 9 : pfe_lat_mode.setText("APP NAV", PFE_Color.PFE_COLOR_ACTIVE); break;
    		case 10 : final_mode="ROLL OUT"; col_2_3 = true; pfe_lat_mode.clearText(); break; 
    		case 11 : col_2_3 = true; pfe_lat_mode.clearText(); break; // or FLARE
    		case 12 : pfe_lat_mode.setText("GA TRK", PFE_Color.PFE_COLOR_ACTIVE); break;  
    		case 101 : if (this.avionics.qpac_fcu_hdg_trk()) {    						
    						pfe_lat_mode.setText("TRACK", PFE_Color.PFE_COLOR_ACTIVE);}
    					else {    						
    						pfe_lat_mode.setText("HDG", PFE_Color.PFE_COLOR_ACTIVE);
    					}
    					break;
    		default : pfe_lat_mode.setText(ap_lateral_mode, PFE_Color.PFE_COLOR_ACTIVE); 
    		}
        
        if (col_2_3) { 
        	drawFinalMode(g2, 0, final_mode, false, pfd_gc.pfd_active_color);
        } else {
        	pfe_lat_mode.paint(g2);       	
        }

        // Autopilote lateral armed mode
        switch (this.avionics.qpac_ap_lateral_armed()) {
    		case 0 : pfe_lat_armed.clearText(); break;
    		case 1 : pfe_lat_armed.setText("LOC", PFE_Color.PFE_COLOR_ARMED); break;   		
    		case 2 : pfe_lat_armed.setText("NAV", PFE_Color.PFE_COLOR_ARMED); break;
    		default : pfe_lat_armed.setText("? "+this.avionics.qpac_ap_lateral_armed(), PFE_Color.PFE_COLOR_ARMED);
        }
        pfe_lat_armed.paint(g2);
    
        
        // A/THR LIMITED (on ECAM - this is not FCOM)     
        if (this.avionics.qpac_athr_limited()!=0 ) {       	
        	pfe_thrust_message.setText("A/THR LIMITED", PFE_Color.PFE_COLOR_CAUTION);
        }

        // TODO : TOGA LK and A.FLOOR
        String str_thr_warning = "THR MSG " + this.avionics.qpac_fma_thr_warning();       
        if (this.avionics.qpac_fma_thr_warning()==1) { 
        	str_thr_warning = "LVR CLB";        	
        	pfe_thrust_message.setText("LVR CLB", PFE_Color.PFE_COLOR_MARK);
        } else if (this.avionics.qpac_fma_thr_warning()==4) { 
        	str_thr_warning = "THR LK";
        	pfe_thrust_message.setText("THR LK", PFE_Color.PFE_COLOR_CAUTION);
        } else if (this.avionics.qpac_fma_thr_warning()==2) { 
        	str_thr_warning = "LVR MCT";
        	pfe_thrust_message.setText("LVR MCT", PFE_Color.PFE_COLOR_MARK);
        } else if (this.avionics.qpac_fma_thr_warning()==3) { 
        	str_thr_warning = "LVR ASYM";
        	pfe_thrust_message.setText("LVR ASYM", PFE_Color.PFE_COLOR_CAUTION);
        } else if  (this.avionics.qpac_fma_thr_warning()>4) {
        	pfe_thrust_message.setText(str_thr_warning, PFE_Color.PFE_COLOR_CAUTION);
        } else if (this.avionics.qpac_athr_limited()!=0 ) {     
            // A/THR LIMITED (on ECAM - this is not FCOM)
            pfe_thrust_message.setText("A/THR LIMITED", PFE_Color.PFE_COLOR_CAUTION);
        } else if (this.avionics.qpac_presel_clb() > 0 && (ap_phase == 1)) {
        	str_thr_warning = "SPEED SEL: "+this.avionics.qpac_presel_clb();
        	pfe_thrust_message.setText(str_thr_warning, PFE_Color.PFE_COLOR_ARMED);
       	} else if (this.avionics.qpac_presel_crz() > 0 && (ap_phase == 2)) {
        	str_thr_warning = "SPEED SEL: "+this.avionics.qpac_presel_crz();
        	pfe_thrust_message.setText(str_thr_warning, PFE_Color.PFE_COLOR_ARMED);
       	} else pfe_thrust_message.clearText();
        pfe_thrust_message.paint(g2);
        // TODO : display : qpac_presel_mach()
        
        // Manual Lever modes
        String str_speed_mode = "LVR " + this.avionics.qpac_thr_lever_mode();
        String str_man = "MAN";
        if (this.avionics.qpac_thr_lever_mode()>0) {
        	pfe_thrust.setFrame();
        	if (this.avionics.qpac_athr_mode()==1) {       		
        		pfe_athr.setText("A/THR", PFE_Color.PFE_COLOR_ARMED);       		
        	} 
        }   
        if (this.avionics.qpac_thr_lever_mode()==3) {
        	str_speed_mode = "TOGA";
        	pfe_thrust.setText(str_man, "TOGA", PFE_Color.PFE_COLOR_MARK);        	
        } else  if (this.avionics.qpac_thr_lever_mode()==2) {
        	str_speed_mode = "FLX ";
        	String str_speed_val = "+"+this.avionics.qpac_flex_temp();
        	pfe_thrust.setTextValue(str_man, "FLX ", str_speed_val, PFE_Color.PFE_COLOR_MARK);
        } else  if (this.avionics.qpac_thr_lever_mode()==1) {
        	str_speed_mode = "THR";
        	pfe_thrust.setText(str_man, "THR", PFE_Color.PFE_COLOR_MARK);
        	pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_ALARM);
        } else  if (this.avionics.qpac_thr_lever_mode()==4) {
        	str_speed_mode = "MCT";
        	pfe_thrust.setText(str_man, "MCT", PFE_Color.PFE_COLOR_MARK);
        	pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_CAUTION);
        } else if (this.avionics.qpac_thr_lever_mode()>4) {
        	pfe_thrust.setText(str_man, str_speed_mode, PFE_Color.PFE_COLOR_ALARM);
        } 
        
        // Autothrust (it's not autothrottle !!!)
        // TODO: "THR DES" ???
        String str_athr_mode = "A/THR";
        String str_athr_mode2 = "M2 "+this.avionics.qpac_athr_mode2();  
        switch (this.avionics.qpac_athr_mode2()) {
        case 0 : str_athr_mode2 = "THR MCT"; break;
        case 1 : str_athr_mode2 = "THR CLB"; break;
        case 2 : str_athr_mode2 = "THR IDLE"; break;
        case 3 : str_athr_mode2 = "THR IDLE"; break;
        case 4 : str_athr_mode2 = "SPEED"; break;
        case 5 : str_athr_mode2 = "MACH"; break;
        case 11 : str_athr_mode2 = "A FLOOR"; break; 
        case 12 : str_athr_mode2 = "TOGA LK"; break; 
        }
        if (this.avionics.qpac_thr_lever_mode()==0) {       	              
        	if (this.avionics.qpac_athr_mode()==1) {
        		pfe_athr.setText(str_athr_mode, PFE_Color.PFE_COLOR_ARMED);
        		pfe_thrust.clearText();
        	}  else if (this.avionics.qpac_athr_mode()==2) {
        		pfe_athr.setText(str_athr_mode, PFE_Color.PFE_COLOR_MARK);
        		pfe_thrust.setText(str_athr_mode2, PFE_Color.PFE_COLOR_ACTIVE);
        		if (this.avionics.qpac_athr_mode2()>10) pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_CAUTION);
        	} else {
        		pfe_athr.clearText();
        		pfe_thrust.clearText();
        	}
        }
    	pfe_thrust.paint(g2);
    	pfe_athr.paint(g2);
        
  
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
    	// On QPAC v2.02 - MDA shown when npa_valid=1 and ap_phase == (4 or 5)
        int appr_type = this.avionics.qpac_appr_type();
        if (ap_phase == 4 || ap_phase == 5 ) {
        	String str_dh_mda ="";       
        	String str_dh_mda_value ="";             
        	switch (appr_type) {
        	case 0: 
        		if (this.aircraft.ra_bug() != -10.0f) { 
        			str_dh_mda = "DH ";
        			str_dh_mda_value = "" + this.aircraft.ra_bug();
        			// draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        			pfe_land_minimums.setTextValue("DH ", str_dh_mda_value, PFE_Color.PFE_COLOR_MARK);
        		} else {
        			pfe_land_minimums.setText("NO DH", PFE_Color.PFE_COLOR_MARK);
        		}
        		break;
        	case 1: 
        		str_dh_mda = "MDA "; 
        		str_dh_mda_value = "" + (int)Math.round(this.avionics.qpac_appr_mda());         	
        		// draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		pfe_land_minimums.setTextValue("MDA ", str_dh_mda_value, PFE_Color.PFE_COLOR_MARK);
        		break;
        	case 2: 
        		str_dh_mda = "BARO ";
        		str_dh_mda_value = "" + this.aircraft.da_bug(); 
        		// draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		pfe_land_minimums.setTextValue("BARO ", str_dh_mda_value, PFE_Color.PFE_COLOR_MARK);
        		break;
        	}      	
        } else pfe_land_minimums.clearText();
        pfe_land_minimums.paint(g2);
        
        // Landing capabilities
        String ldg_cap_1 = "";
        String ldg_cap_2 = "";
        if ( this.avionics.qpac_npa_no_points()==2 && this.avionics.qpac_npa_valid()>0 && this.avionics.qpac_ap_vertical_mode()==8 ) {
        	pfe_land_cat.setText("F-APP", PFE_Color.PFE_COLOR_MARK);
        	pfe_land_mode.setText("+RAW", PFE_Color.PFE_COLOR_MARK);
            ldg_cap_1 = "F-APP";
            ldg_cap_2 = "+RAW";
        } else if ( (ap_phase < 6) && (appr_type == 0) && (this.avionics.qpac_appr_illuminated() || this.avionics.qpac_loc_illuminated()) ) {
        	if (dual_ap) { 
            	pfe_land_cat.setText("CAT 3", PFE_Color.PFE_COLOR_MARK);
            	pfe_land_mode.setText("DUAL", PFE_Color.PFE_COLOR_MARK);
        		ldg_cap_2="DUAL"; ldg_cap_1="CAT 3";
        	} else {
        		if (single_ap) { 
        			ldg_cap_2="SINGLE";
        			pfe_land_mode.setText("SINGLE", PFE_Color.PFE_COLOR_MARK);
        		} else { 
        			pfe_land_mode.clearText(); 
        		}
        		ldg_cap_1="CAT 1";
            	pfe_land_cat.setText("CAT 1", PFE_Color.PFE_COLOR_MARK);
            	
        	}
        } else {
        	pfe_land_cat.clearText();
        	pfe_land_mode.clearText();
        }
        pfe_land_cat.paint(g2);
        pfe_land_mode.paint(g2);
        
        
        
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

    private void drawA320_NEO_FMA(Graphics2D g2) {

    	// JAR Design A320 Neo
    	
        String fma_str = "ERROR";       
        
        // AP Engaged
        String ap_str = "";       
        boolean dual_ap = this.avionics.jar_a320neo_ap1() && this.avionics.jar_a320neo_ap2();
        boolean single_ap = (this.avionics.jar_a320neo_ap1() || this.avionics.jar_a320neo_ap2()) && ! dual_ap;       
        if (dual_ap) {
        	ap_str="AP 1+2";
        } else if (this.avionics.jar_a320neo_ap1()) {
        	ap_str="AP 1";
        } else if (this.avionics.jar_a320neo_ap2()) {
        	ap_str="AP 2";
        }
        pfe_ap.setText(ap_str, PFE_Color.PFE_COLOR_MARK);
        pfe_ap.paint(g2);
 
        
        // FD Engaged
        String fd_str = "";
        if (this.avionics.jar_a320neo_fd()) {fd_str="1";} else {fd_str="-";}
        fd_str+=" FD ";
        if (this.avionics.jar_a320neo_fd()) {fd_str+="2";} else {fd_str+="-";}
        pfe_fd.setText(fd_str, PFE_Color.PFE_COLOR_MARK);
        pfe_fd.paint(g2);        

        
        // Autopilote phase
        String str_ap_phase="Phase " + this.avionics.jar_a320neo_ap_phase();
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
        // drawDMode(g2,1,0,str_ap_phase);
        
        // Autopilote vertical mode
        boolean col_2_3 = this.avionics.jar_a320neo_ap_common_mode() > 0; 
        String final_mode = "";
        String ap_vertical_mode="Vert " + this.avionics.jar_a320neo_ap_vertical_mode();
        int ap_vv = Math.round(this.avionics.autopilot_vv());
        switch (this.avionics.jar_a320neo_ap_vertical_mode()) {
        	case 0 : ap_vertical_mode=""; pfe_vert_mode.clearText(); break;
        	case 1 : ap_vertical_mode="SRS"; break;
        	case 2 : ap_vertical_mode="CLB"; break;
        	case 3 : ap_vertical_mode="OP CLB"; break;
        	case 4 : ap_vertical_mode="ALT*"; break;
        	case 5 : ap_vertical_mode="ALT CST*"; break;
        	case 6 : ap_vertical_mode="ALT*"; break;
        	case 7 : ap_vertical_mode="ALT CST"; break;
           	case 8 : ap_vertical_mode="ALT CRZ"; break;            	
        	case 9 : ap_vertical_mode="DES"; break;
        	case 10 : ap_vertical_mode="OP DES"; break;
        	case 11 : ap_vertical_mode="GS*"; break;
        	case 12 : ap_vertical_mode="GS"; break;
        	case 13 : ap_vertical_mode="V/S"; break;
        	case 14 : ap_vertical_mode="FPA"; break;
        	case 15 :  
        	case 16 : if (this.avionics.jar_a320neo_fcu_hdg_trk()) ap_vertical_mode="FPA "; else ap_vertical_mode="V/S "; break; //or FPA + value sim/cockpit2/autopilot/vvi_dial_fpm
        	case 17 : ap_vertical_mode="EXP CLB"; break;
        	case 18 : ap_vertical_mode="EXP DES"; break;       
        }
        if (! col_2_3) { 
        	if (this.avionics.jar_a320neo_ap_vertical_mode()==13 ) {
        		// TODO : fix the FPA value display 
        		String str_vv = "" + ap_vv;
        		if ( ap_vv >0 ) { str_vv = "+" + ap_vv; }
        		pfe_vert_mode.setTextValue(ap_vertical_mode, str_vv, PFE_Color.PFE_COLOR_ACTIVE);
        		pfe_vert_mode.paint(g2);
        	} else {
        		pfe_vert_mode.setText(ap_vertical_mode, PFE_Color.PFE_COLOR_ACTIVE);
        		pfe_vert_mode.paint(g2);
        	}
        }

        // Autopilote vertical armed mode
        switch (this.avionics.jar_a320neo_ap_vertical_armed()) {
    		case 0 : pfe_vert_armed.clearText(); break;
    		case 1 : pfe_vert_armed.setText("CLB", PFE_Color.PFE_COLOR_ARMED); break;
    		case 2 : pfe_vert_armed.setText("ALT", PFE_Color.PFE_COLOR_ARMED); break;
    		case 3 : pfe_vert_armed.setText("ALT", PFE_Color.PFE_COLOR_MANAGED); break;
    		case 4 : pfe_vert_armed.setText("DES", PFE_Color.PFE_COLOR_ARMED); break;    			    			    		
        	case 5 : pfe_vert_armed.setText("G/S", PFE_Color.PFE_COLOR_ARMED); break;
        	case 6 : pfe_vert_armed.setText("FINAL", PFE_Color.PFE_COLOR_ARMED); break;         		 
        	case 7 : pfe_vert_armed.setText("ALT G/S", PFE_Color.PFE_COLOR_ARMED); break;
        	case 8 : pfe_vert_armed.setTextValue("ALT", " G/S", PFE_Color.PFE_COLOR_MANAGED); break;
        	case 9 : pfe_vert_armed.setText("ALT FINAL", PFE_Color.PFE_COLOR_ARMED); break;        	
        	case 10 : pfe_vert_armed.setTextValue("ALT", " FINAL", PFE_Color.PFE_COLOR_MANAGED); break;
        	case 11 : pfe_vert_armed.setText("DES G/S", PFE_Color.PFE_COLOR_ARMED); break;
        	case 12 : pfe_vert_armed.setText("DES FINAL", PFE_Color.PFE_COLOR_ARMED); break;
        }       
        pfe_vert_armed.paint(g2);
        

        // Autopilote lateral mode
        String ap_lateral_mode="Lat " + this.avionics.jar_a320neo_ap_lateral_mode();
        switch (this.avionics.jar_a320neo_ap_lateral_mode()) {
			case 0 : pfe_lat_mode.clearText(); break; 
			case 1 : pfe_lat_mode.setText("RWY", PFE_Color.PFE_COLOR_ACTIVE); break; 
       		case 2 : pfe_lat_mode.setText("RWY TRK", PFE_Color.PFE_COLOR_ACTIVE);break; 
    		case 3 : pfe_lat_mode.setText("HDG", PFE_Color.PFE_COLOR_ACTIVE); break; 
    		case 4 : pfe_lat_mode.setText("TRACK", PFE_Color.PFE_COLOR_ACTIVE); break; 
    		case 5 : pfe_lat_mode.setText("NAV", PFE_Color.PFE_COLOR_ACTIVE); break; 
    		case 6 : pfe_lat_mode.setText("LOC*", PFE_Color.PFE_COLOR_ACTIVE); break;
    		case 7 : pfe_lat_mode.setText("LOC", PFE_Color.PFE_COLOR_ACTIVE); break;
    		case 8 : pfe_lat_mode.setText("APP NAV", PFE_Color.PFE_COLOR_ACTIVE); break;
    		case 9 : pfe_lat_mode.setText("GA TRK", PFE_Color.PFE_COLOR_ACTIVE); break;
    		default : pfe_lat_mode.setText(ap_lateral_mode, PFE_Color.PFE_COLOR_ACTIVE); 
    		}
        
        if (col_2_3) { 
        	// integer enum_values {"", "LAND", "FLARE", "ROLL OUT", "FINAL APP"}
        	switch (this.avionics.jar_a320neo_ap_common_mode()) {
        		case 1 : final_mode = "LAND";
        		case 2 : final_mode = "FLARE";
        		case 3 : final_mode = "ROLL OUT";
        		case 4 : final_mode = "FINAL APP";
        		default : final_mode = "";
        	}
        	drawFinalMode(g2, 0, final_mode, false, pfd_gc.pfd_active_color);
        } else {
        	pfe_lat_mode.paint(g2);       	
        }

        // Autopilote lateral armed mode
        switch (this.avionics.jar_a320neo_ap_lateral_armed()) {
    		case 0 : pfe_lat_armed.clearText(); break;
    		case 1 : pfe_lat_armed.setText("NAV", PFE_Color.PFE_COLOR_ARMED); break;   		
    		case 2 : pfe_lat_armed.setText("LOC", PFE_Color.PFE_COLOR_ARMED); break;
    		case 3 : pfe_lat_armed.setText("APP NAV", PFE_Color.PFE_COLOR_ARMED); break;
    		case 4 : pfe_lat_armed.setText("NAV", PFE_Color.PFE_COLOR_ARMED); break;
    		case 5 : pfe_lat_armed.setText("LOC", PFE_Color.PFE_COLOR_ARMED); break;
    		case 6 : pfe_lat_armed.setText("APP NAV", PFE_Color.PFE_COLOR_ARMED); break;
    		case 7 : pfe_lat_armed.setText("NAV", PFE_Color.PFE_COLOR_ARMED); break;
    		case 8 : pfe_lat_armed.setText("LOC", PFE_Color.PFE_COLOR_ARMED); break;
    		case 9 : pfe_lat_armed.setText("APP NAV", PFE_Color.PFE_COLOR_ARMED); break;   		
    		default : pfe_lat_armed.setText("? "+this.avionics.qpac_ap_lateral_armed(), PFE_Color.PFE_COLOR_ARMED);
        }
        pfe_lat_armed.paint(g2);
    
        
        // A/THR LIMITED (on ECAM - this is not FCOM)
        /*
        if (this.avionics.jar_a320neo_athr_limited()!=0 ) {       	
        	pfe_thrust_message.setText("A/THR LIMITED", PFE_Color.PFE_COLOR_CAUTION);
        }
        */

        // TODO : TOGA LK and A.FLOOR
        /*
        String str_thr_warning = "THR MSG " + this.avionics.qpac_fma_thr_warning();       
        if (this.avionics.jar_a320neo_fma_thr_warning()==1) { 
        	str_thr_warning = "LVR CLB";        	
        	pfe_thrust_message.setText("LVR CLB", PFE_Color.PFE_COLOR_MARK);
        } else if (this.avionics.jar_a320neo_fma_thr_warning()==4) { 
        	str_thr_warning = "THR LK";
        	pfe_thrust_message.setText("THR LK", PFE_Color.PFE_COLOR_CAUTION);
        } else if (this.avionics.jar_a320neo_fma_thr_warning()==2) { 
        	str_thr_warning = "LVR MCT";
        	pfe_thrust_message.setText("LVR MCT", PFE_Color.PFE_COLOR_MARK);
        } else if (this.avionics.jar_a320neo_fma_thr_warning()==3) { 
        	str_thr_warning = "LVR ASYM";
        	pfe_thrust_message.setText("LVR ASYM", PFE_Color.PFE_COLOR_CAUTION);
        } else if  (this.avionics.jar_a320neo_fma_thr_warning()>4) {
        	pfe_thrust_message.setText(str_thr_warning, PFE_Color.PFE_COLOR_CAUTION);
        } else if (this.avionics.jar_a320neo_athr_limited()!=0 ) {     
            // A/THR LIMITED (on ECAM - this is not FCOM)
            pfe_thrust_message.setText("A/THR LIMITED", PFE_Color.PFE_COLOR_CAUTION);
//        } else if (this.avionics.jar_a320neo_presel_clb() > 0 && (ap_phase == 1)) {
//        	str_thr_warning = "SPEED SEL: "+this.avionics.qpac_presel_clb();
//        	pfe_thrust_message.setText(str_thr_warning, PFE_Color.PFE_COLOR_ARMED);
//       	} else if (this.avionics.jar_a320neo_presel_crz() > 0 && (ap_phase == 2)) {
//        	str_thr_warning = "SPEED SEL: "+this.avionics.qpac_presel_crz();
//        	pfe_thrust_message.setText(str_thr_warning, PFE_Color.PFE_COLOR_ARMED);
       	} else pfe_thrust_message.clearText();
        pfe_thrust_message.paint(g2);
        */
        // TODO : display : qpac_presel_mach()
        
        // Manual Lever modes
        String str_man = "MAN";
        
        // Autothrust (it's not autothrottle !!!)
        String str_thr_mode = "A/THR"; 
        switch (this.avionics.jar_a320neo_athr_mode()) {
        case 0 : pfe_thrust.clearText(); break;
        case 1 : pfe_thrust.setText(str_man, "TOGA", PFE_Color.PFE_COLOR_MARK); 
        		 pfe_thrust.setFrame();
        		 break; 
        case 2 : 
    			 String str_speed_val = "+"+this.avionics.jar_a320neo_flex_temp();
    			 pfe_thrust.setTextValue(str_man, "FLX ", str_speed_val, PFE_Color.PFE_COLOR_MARK);
    			 pfe_thrust.setFrame();
    			 break;
        case 3 : pfe_thrust.setText(str_man, "MCT", PFE_Color.PFE_COLOR_MARK); 
        		 pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_CAUTION);
        		 pfe_thrust.setFrame();
        		 break;
        case 4 : pfe_thrust.setText(str_man, "THR", PFE_Color.PFE_COLOR_MARK);
        		 pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_ALARM); 
        		 pfe_thrust.setFrame();
        		 break;
        case 5 : pfe_thrust.setText("THR MCT", PFE_Color.PFE_COLOR_ACTIVE); break;
        case 6 : pfe_thrust.setText("THR CLB", PFE_Color.PFE_COLOR_ACTIVE); break;
        case 7 : pfe_thrust.setText("THR IDLE", PFE_Color.PFE_COLOR_ACTIVE); break;
        case 8 : pfe_thrust.setText("THR LVR", PFE_Color.PFE_COLOR_ACTIVE); break;
        case 9 : pfe_thrust.setText("SPEED", PFE_Color.PFE_COLOR_ACTIVE); break;
        case 10 : pfe_thrust.setText("MACH", PFE_Color.PFE_COLOR_ACTIVE); break;
        case 11 : pfe_thrust.setText("A FLOOR", PFE_Color.PFE_COLOR_ACTIVE);  
        		  pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_CAUTION);
        		  break; 
        case 12 : pfe_thrust.setText("TOGA LK", PFE_Color.PFE_COLOR_ACTIVE); 
        		  pfe_thrust.setFrameColor(PFE_Color.PFE_COLOR_CAUTION);
        		  break; 
        }
    	pfe_thrust.paint(g2);
    	
    	// A/THR display on column 5
    	if (this.avionics.jar_a320neo_thr_mode()==1) {
    		pfe_athr.setText(str_thr_mode, PFE_Color.PFE_COLOR_ARMED);
    	}  else if (this.avionics.jar_a320neo_thr_mode()==2) {
    		pfe_athr.setText(str_thr_mode, PFE_Color.PFE_COLOR_MARK);
    	} else {
    		pfe_athr.clearText();
    	}
    	pfe_athr.paint(g2);
        
        
        // Minimums
    	
        // int appr_type = this.avionics.jar_a320neo_appr_type();
        int appr_type = 0;
        if (ap_phase == 4 || ap_phase == 5 ) {
        	String str_dh_mda ="";       
        	String str_dh_mda_value ="";             
        	switch (appr_type) {
        	case 0: 
        		if (this.avionics.jar_a320neo_appr_dh() > 0.0f) { 
        			str_dh_mda = "DH ";
        			str_dh_mda_value = "" + (int)Math.round(this.avionics.jar_a320neo_appr_dh());
        			// draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        			pfe_land_minimums.setTextValue("DH ", str_dh_mda_value, PFE_Color.PFE_COLOR_MARK);
        		} else {
        			pfe_land_minimums.setText("NO DH", PFE_Color.PFE_COLOR_MARK);
        		}
        		break;
        	case 1: 
        		str_dh_mda = "MDA "; 
        		str_dh_mda_value = "" + (int)Math.round(this.avionics.jar_a320neo_appr_mda());         	
        		// draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		pfe_land_minimums.setTextValue("MDA ", str_dh_mda_value, PFE_Color.PFE_COLOR_MARK);
        		break;
        	case 2: 
        		str_dh_mda = "BARO ";
        		str_dh_mda_value = "" + this.aircraft.da_bug(); 
        		// draw2Mode(g2, 3, 2, str_dh_mda, str_dh_mda_value, false, pfd_gc.pfd_markings_color, pfd_gc.pfd_armed_color);
        		pfe_land_minimums.setTextValue("BARO ", str_dh_mda_value, PFE_Color.PFE_COLOR_MARK);
        		break;
        	}      	
        } else pfe_land_minimums.clearText();
        pfe_land_minimums.paint(g2);
        
        // Landing capabilities
        /*
        String ldg_cap_1 = "";
        String ldg_cap_2 = "";
        if ( this.avionics.qpac_npa_no_points()==2 && this.avionics.qpac_npa_valid()>0 && this.avionics.qpac_ap_vertical_mode()==8 ) {
        	pfe_land_cat.setText("F-APP", PFE_Color.PFE_COLOR_MARK);
        	pfe_land_mode.setText("+RAW", PFE_Color.PFE_COLOR_MARK);
            ldg_cap_1 = "F-APP";
            ldg_cap_2 = "+RAW";
        } else if ( (ap_phase < 6) && (appr_type == 0) && (this.avionics.qpac_appr_illuminated() || this.avionics.qpac_loc_illuminated()) ) {
        	if (dual_ap) { 
            	pfe_land_cat.setText("CAT 3", PFE_Color.PFE_COLOR_MARK);
            	pfe_land_mode.setText("DUAL", PFE_Color.PFE_COLOR_MARK);
        		ldg_cap_2="DUAL"; ldg_cap_1="CAT 3";
        	} else {
        		if (single_ap) { 
        			ldg_cap_2="SINGLE";
        			pfe_land_mode.setText("SINGLE", PFE_Color.PFE_COLOR_MARK);
        		} else { 
        			pfe_land_mode.clearText(); 
        		}
        		ldg_cap_1="CAT 1";
            	pfe_land_cat.setText("CAT 1", PFE_Color.PFE_COLOR_MARK);
            	
        	}
        } else {
        	pfe_land_cat.clearText();
        	pfe_land_mode.clearText();
        }
        pfe_land_cat.paint(g2);
        pfe_land_mode.paint(g2);
        */
  
        
    }

    
}
