/**
* SystemsStatus.java
* 
* Lower EICAS and ECAM
* 
* Copyright (C) 2015  Nicolas Carel
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.logging.Logger;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.ModelFactory;

public class SystemsStatus extends MFDSubcomponent {

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    // List extracted from A320 FCOM 3.02 (rev 36)
	private String inop_list[] = { 
			"PACK 1",
			"PACK 2",
			"PACK 1 + 2",
			"PACK 1 REGUL",
			"PACK 2 REGUL",
			"HOT AIR",
			"AFT CRG VENT", // A320 option
			"AFT CRG HEAT", // A320 option
			"FWD CRG VENT", // A320 option
			"FWD CRG HEAT", // A320 option
			"CRG VENT", // A320 option
			"ZONE REGUL",
			"L + R CAB FAN",
			"GALLEY FAN",
			"ENG 1 BLEED",
			"ENG 2 BLEED",
			"X BLEED",
			"APU BLEED",
			"L LEAK DET",
			"R LEAK DET",
			"BMC 1 + 2", // BMC - Bleed Monitoring Computer
			// Pressurisation systems
			"CAB PR 1",
			"CAB PR 2",
			"CAB PR 1 + 2",
			// Ventilation systems
			"VENT BLOWER",
			"VENT EXTRACT",
			"AVNCS VALVE",
			"AVNCS VENT",
			// Landing capabilities
			"CAT 2",
			"CAT 3",
			"CAT 3 DUAL",
			// Autopilot
			"AP 1",
			"AP 2",
			"AP 1 + 2",
			// 
			"FAC 1", // FAC - Flight Augmentation Computer
			"FAC 2",
			"FAC 1 + 2",
			"F/CTL PROT",

			"REAC W/S DET",
			"PRED W/S DET",
			"WINDSHEAR DET",
			"A/THR",
			"FCU 1",
			"FCU 2",
			"FCU 1 + 2",
			"CIDS",
			"ACARS",
			// Electric systems
			"MAIN GALLEY", 
			"GALLEY",
			"GEN 1",
			"GEN 2",
			"GEN 3", // A330 
			"GEN 4", // A330			
			"APU GEN",
			"APU",
			"BAT 1",
			"BAT 2",
			"BAT 3", // Extra A/C
			"BAT 4", // Extra A/C
			"BCL 1", 
			"BCL 2",
			"EMER GEN",
			// Hydraulic systems
			"BLUE HYD",
			"GREEN HYD",
			"YELLOW HYD",
			"G + B HYD",
			"G + Y HYD",
			"B + Y HYD",
			"B ELEC PUMP",
			"Y ELEC PUMP",
			"G ENG 1 PUMP",
			"Y ENG 2 PUMP",
			"PTU",
			"RAT",
			// Radio altimeters
			"RA 1",
			"RA 2",
			"RA 1+2",
			// 
			"ADR 1",  // ADR - Air Data Reference
			"ADR 2",
			"ADR 3",
			"ADR 2+3",
			"ADR 1+3",
			"ADR 1+2",			
			"ADR 1+2+3",
			"ILS 1",
			"ILS 2",
			"GPS 1",
			"GPS 2",
			"IR 1",
			"IR 2",
			"IR 3",
			"IR 1+2",
			"IR 1+3",
			"IR 2+3",
			"IR 1+2+3",
			"--",
			"TCAS",
			// Windows and Windshield
			"L WSHLD HEAT",
			"R WSHLD HEAT",
			"WSHLD HEAT",
			"L WNDW HEAT",
			"R WNDW HEAT",
			"WNDW HEAT",
			// Fuel
			"L+R TK PUMP 1",
			"L+R TK PUMP 2",
			"R TK PUMP 1",
			"R TK PUMP 2",
			"L TK PUMP 1",
			"L TK PUMP 2",
			"CTR TK PUMP 1",
			"CTR TK PUMP 2",
			"CTRS TK PUMPS",
			"FUEL PUMPS",
			"L CELL VALVE",
			"R CELL VALVE",
			"FUEL X FEED",
			// 
			"GND COOL", // A320 option

			"BSCU CH 1",
			"BSCU CH 2",
			"DMC 1", // DMC - Display Management Computer
			"DMC 2",
			"DMC 3",
			"GPWS",

			"SDAC 1", // SDAC - System Data Acquisition Concentrator
			"SDAC 2",
			"FWC 1", // FWC - Flight Warning Computer
			"FWC 2",
			"FDIU", // FDIU - Flight Data Interface Unit
			"F/O PITOT",
			"CAPT PITOT",
			"F/O AOA",
			"CAPT AOA",
			"F/O TAT",
			"CAPT TAT",
			"F/O STAT",
			"CAPT STAT", 
			"L STAT",
			"R STAT",
			"CAPT PROBES",
			"F/O PROBES",
			"STBY PROBES",
			"REVERSER 1",
			"REVERSER 2",
			"REVERSER 1+2",
			"ACP 1+2",
			"ACP 3",
			"CAPT STAT HEAT",
			"F/O STAT HEAT",
			"STBY STAT HEAT",

			"ELAC 1", // ELAC - Elevator Aileron Computer
			"ELAC 2",
			"ELAC 1+2",
			"ELAC PITCH", // if ELAC 1 and 2 pitch fault
			"SEC 2 + 3",
			"VHF 2",
			"--",
			// Fire detection systems
			"ENG 1 LOOP A",
			"ENG 1 LOOP B",
			"ENG 2 LOOP A",
			"ENG 2 LOOP B",
			"FIRE DET 1",
			"FIRE DET 2",		
			"APU LOOP A",
			"APU LOOP B",
			"APU FIRE DET",
			// Smoke
			"LAV DET",
			"FWD CRG DET",
			"AFT CRG DET",
			"CRG DET",
			"SDCU", // SDCU - Smoke Detection Control Unit
			//
			"FCDC 1", // FCDC - Flight Control Data Concentrator
			"FCDC 2",
			"FCDC 1+2",
			"B HYD",
			"WING A. ICE",
			"WAI REGUL",
			"ENG 1 A. ICE",
			"ENG 2 A. ICE",
			"ENG 1 START",
			"ENG 2 START",
			"ENG 1 IGN A",
			"ENG 1 IGN B",
			"ENG 1 IGN",
			"ENG 2 IGN A",
			"ENG 2 IGN B",
			"ENG 2 IGN",	
			"ENG 1 THR",
			"ENG 2 THR",			
			"ANTI SKID",
			"A/CALL OUT",
			"ESS TR",
			"TR 1",
			"TR 2",
			// Flight surfaces
			"FLAPS",
			"SLATS",
			"L AIL",
			"R AIL",
			"L + R AIL",
			"SPLR 1+2+5",
			"SPLR 1+2+4+5",
			"SPLR 1+3+5",
			"SPLR 1+5",
			"SPLR 2+3+4",
			"SPLR 2+4",
			"SPLR 3",
			"GND SPLR 1+2",
			"GND SPLR 3+4",
			"SPD BRK 3+4", // surfaces position not in agreement with handle position
			"SPD BRK 2+3+4",
			"L ELEV",
			"R ELEV",
			"L + R ELEV",
			"LAF", // A320 option ; LAF - Load Alleviation Function
			"STABILIZER",
			// Rudder 
			"RUD TRIM 1",
			"RUD TRIM 2",
			"RUD TRIM",
			"RUD TRV LIM 1",
			"RUD TRV LIM 2",
			"RUD TRV LIM 1+2",
			"YAW DAMPER 1",
			"YAW DAMPER 2",
			// Brakes / Wheels
			"LGCIU 1", // LGCIU - Landing Gear Control Interface Unit
			"LGCIU 2",
			"N.W. STEER",
			"AUTO BRK",
			"NORM BRK",
			"ALTN BRK",
			"L/G RETRACT",
			"L/G DOOR",
			"CARGO DOOR",
			"DFDR"	
		 };
	
	private static final int INOP_PACK_1 = 0;
	private static final int INOP_PACK_2 = 1;
	private static final int INOP_PACK_1_P_2 = 2;	
	private static final int INOP_PACK_1_REGUL = 3;
	private static final int INOP_PACK_2_REGUL = 4;
	private static final int INOP_HOT_AIR = 5;
	private static final int INOP_AFT_CRG_VENT = 6;
	private static final int INOP_AFT_CRG_HEAT = 7;
	private static final int INOP_FWD_CRG_VENT = 8;
	private static final int INOP_FWD_CRG_HEAT = 9;
	private static final int INOP_CRG_VENT = 10;
	private static final int INOP_ZONE_REGUL = 11;
	private static final int INOP_L_R_CAB_FAN = 12;
	private static final int INOP_GALLEY_FAN = 13;
	private static final int INOP_ENG_1_BLEED = 14;
	private static final int INOP_ENG_2_BLEED = 15;
	private static final int INOP_X_BLEED = 16;
	private static final int INOP_APU_BLEED = 17;
	private static final int INOP_L_LEAK_DET = 18;
	private static final int INOP_R_LEAK_DET = 19;
	private static final int INOP_BMC_1_P_2 = 20;
	private static final int INOP_CAB_PR_1 = 21;
	private static final int INOP_CAB_PR_2 = 22;
	private static final int INOP_CAB_PR_1_P_2 = 23;
	// Ventilation systems
	private static final int INOP_VENT_BLOWER = 24;
	private static final int INOP_VENT_EXTRACT = 25;
	private static final int INOP_AVNCS_VALVE = 26;
	private static final int INOP_AVNCS_VENT = 27;
	// Landing capabilities
	private static final int INOP_CAT_2 = 28;
	private static final int INOP_CAT_3 = 29;
	private static final int INOP_CAT_3_DUAL = 30;
	// Autopilot
	private static final int INOP_AP_1 = 31;
	private static final int INOP_AP_2 = 32;
	private static final int INOP_AP_1_P_2 = 33;
	// Flight Aug. Computers
	private static final int INOP_FAC_1 = 34;
	private static final int INOP_FAC_2 = 35;
	private static final int INOP_FAC_1_P_2 = 36;
	private static final int INOP_F_CTL_PROT = 37;
	private static final int INOP_REAC_W_S_DET = 38;
	private static final int INOP_PRED_W_S_DET = 39;
	private static final int INOP_WINDSHEAR_DET = 40;
	private static final int INOP_A_THR  = 41;
	private static final int INOP_FCU_1 = 42;
	private static final int INOP_FCU_2 = 43;
	private static final int INOP_FCU_1_P_2 = 44;
	private static final int INOP_CIDS = 45;
	private static final int INOP_ACARS = 46;
	// Electric systems
	private static final int INOP_MAIN_GALLEY = 47; 
	private static final int INOP_GALLEY = 48;
	private static final int INOP_GEN_1 = 49;
	private static final int INOP_GEN_2 = 50;
	private static final int INOP_GEN_3 = 51; 
	private static final int INOP_GEN_4 = 52;			
	private static final int INOP_APU_GEN = 53;
	private static final int INOP_APU = 54;
	private static final int INOP_BAT_1 = 55;
	private static final int INOP_BAT_2 = 56;
	private static final int INOP_BAT_3 = 57;
	private static final int INOP_BAT_4 = 58;
	private static final int INOP_BCL_1 = 59;
	private static final int INOP_BCL_2 = 60;
	private static final int INOP_EMER_GEN = 61;
	// Hydraulic systems
	private static final int INOP_BLUE_HYD = 62;
	private static final int INOP_GREEN_HYD = 63;
	private static final int INOP_YELLOW_HYD = 64;
	private static final int INOP_G_P_B_HYD = 65;
	private static final int INOP_G_P_Y_HYD = 66;
	private static final int INOP_B_P_Y_HYD = 67;
	private static final int INOP_B_ELEC_PUMP = 68;
	private static final int INOP_Y_ELEC_PUMP = 69;
	private static final int INOP_G_ENG_1_PUMP = 70;
	private static final int INOP_Y_ENG_2_PUMP = 71;
	private static final int INOP_PTU = 72;
	private static final int INOP_RAT = 73;
	// Radio altimeters
	private static final int INOP_RA_1 = 74;
	private static final int INOP_RA_2 = 75;
	private static final int INOP_RA_1_P_2 = 76;
	// 
	private static final int INOP_ADR_1 = 77;
	private static final int INOP_ADR_2 = 78;
	private static final int INOP_ADR_3 = 79;
	private static final int INOP_ADR_2_P_3 = 80;
	private static final int INOP_ADR_1_P_3 = 81;
	private static final int INOP_ADR_1_P_2 = 82;		
	private static final int INOP_ADR_1_P_2_P_3 = 83;
	private static final int INOP_ILS_1 = 84;
	private static final int INOP_ILS_2 = 85;
	private static final int INOP_GPS_1 = 86;
	private static final int INOP_GPS_2 = 87;
	private static final int INOP_IR_1 = 88;
	private static final int INOP_IR_2 = 89;
	private static final int INOP_IR_3 = 90;
	private static final int INOP_IR_1_P_2 = 91;
	private static final int INOP_IR_1_P_3 = 92;
	private static final int INOP_IR_2_P_3 = 93;
	private static final int INOP_IR_1_P_2_P_3 = 94;
	private static final int INOP_FREE_SLOT_A1 = 95;
	private static final int INOP_TCAS = 96;
	// Windows and Windshield
	private static final int INOP_L_WSHLD_HEAT = 97;
	private static final int INOP_R_WSHLD_HEAT = 98;
	private static final int INOP_WSHLD_HEAT = 99;
	private static final int INOP_L_WNDW_HEAT = 100;
	private static final int INOP_R_WNDW_HEAT = 101;
	private static final int INOP_WNDW_HEAT = 102;
	// Fuel
	private static final int INOP_L_R_TK_PUMP_1 = 103;
	private static final int INOP_L_R_TK_PUMP_2 = 104;
	private static final int INOP_R_TK_PUMP_1 = 105;
	private static final int INOP_R_TK_PUMP_2 = 106;
	private static final int INOP_L_TK_PUMP_1 = 107;
	private static final int INOP_L_TK_PUMP_2 = 108;
	private static final int INOP_CTR_TK_PUMP_1 = 109;
	private static final int INOP_CTR_TK_PUMP_2 = 110;
	private static final int INOP_CTRS_TK_PUMPS = 111;
	private static final int INOP_FUEL_PUMPS = 112;
	private static final int INOP_L_CELL_VALVE = 113;
	private static final int INOP_R_CELL_VALVE = 114;
	private static final int INOP_FUEL_X_FEED = 115;
	// 
	private static final int INOP_GND_COOL = 116;

	private static final int INOP_BSCU_CH_1 = 117;
	private static final int INOP_BSCU_CH_2 = 118;
	private static final int INOP_DMC_1 = 119;
	private static final int INOP_DMC_2 = 120;
	private static final int INOP_DMC_3 = 121;
	private static final int INOP_GPWS = 122;

	private static final int INOP_SDAC_1 = 123;
	private static final int INOP_SDAC_2 = 124;
	private static final int INOP_FWC_1 = 125;
	private static final int INOP_FWC_2 = 126;
	private static final int INOP_FDIU = 127;
	private static final int INOP_FO_PITOT = 128;
	private static final int INOP_CAPT_PITOT = 129;
	private static final int INOP_FO_AOA = 130;
	private static final int INOP_CAPT_AOA = 131;
	private static final int INOP_FO_TAT = 132;
	private static final int INOP_CAPT_TAT = 133;
	private static final int INOP_FO_STAT = 134;
	private static final int INOP_CAPT_STAT = 135;
	private static final int INOP_L_STAT = 136;
	private static final int INOP_R_STAT = 137;
	private static final int INOP_CAPT_PROBES = 138;
	private static final int INOP_FO_PROBES = 139;
	private static final int INOP_STBY_PROBES = 140;
	private static final int INOP_REVERSER_1 = 141;
	private static final int INOP_REVERSER_2 = 142;
	private static final int INOP_REVERSER_1_2 = 143;
	private static final int INOP_ACP_1_2 = 144;
	private static final int INOP_ACP_3 = 145;
	private static final int INOP_CAPT_STAT_HEAT = 146;
	private static final int INOP_FO_STAT_HEAT = 147;
	private static final int INOP_STBY_STAT_HEAT = 148;

	private static final int INOP_ELAC_1 = 149;
	private static final int INOP_ELAC_2 = 150;
	private static final int INOP_ELAC_1_2 = 151;
	private static final int INOP_ELAC_PITCH = 152;
	private static final int INOP_SEC_2_3 = 153;
	private static final int INOP_VHF_2 = 154;
	private static final int INOP_FREE_SLOT2= 155;
	// Fire detection systems
	private static final int INOP_ENG_1_LOOP_A = 156;
	private static final int INOP_ENG_1_LOOP_B = 157;
	private static final int INOP_ENG_2_LOOP_A = 158;
	private static final int INOP_ENG_2_LOOP_B = 159;
	private static final int INOP_FIRE_DET_1 = 160;
	private static final int INOP_FIRE_DET_2 = 161;
	private static final int INOP_APU_LOOP_A = 162;
	private static final int INOP_APU_LOOP_B = 163;
	private static final int INOP_APU_FIRE_DET = 164;
	// Smoke
	private static final int INOP_LAV_DET = 165;
	private static final int INOP_FWD_CRG_DET = 166;
	private static final int INOP_AFT_CRG_DET = 167;
	private static final int INOP_CRG_DET = 168;
	private static final int INOP_SDCU = 169;
	//
	private static final int INOP_FCDC_1 = 170;
	private static final int INOP_FCDC_2 = 171;
	private static final int INOP_FCDC_1_2 = 172;
	private static final int INOP_B_HYD = 173;
	private static final int INOP_WING_A_ICE = 174;
	private static final int INOP_WAI_REGUL = 175;
	private static final int INOP_ENG_1_A_ICE = 176;
	private static final int INOP_ENG_2_A_ICE = 177;
	private static final int INOP_ENG_1_START = 178;
	private static final int INOP_ENG_2_START = 179;
	private static final int INOP_ENG_1_IGN_A = 180;
	private static final int INOP_ENG_1_IGN_B = 181;
	private static final int INOP_ENG_1_IGN = 182;
	private static final int INOP_ENG_2_IGN_A = 183;
	private static final int INOP_ENG_2_IGN_B = 184;
	private static final int INOP_ENG_2_IGN = 185;
	private static final int INOP_ENG_1_THR = 186;
	private static final int INOP_ENG_2_THR = 187;	
	private static final int INOP_ANTI_SKID = 188;
	private static final int INOP_A_CALL_OUT = 189;
	private static final int INOP_ESS_TR = 190;
	private static final int INOP_TR_1 = 191;
	private static final int INOP_TR_2 = 192;
	// Flight surfaces
	private static final int INOP_FLAPS = 193;
	private static final int INOP_SLATS = 194;
	private static final int INOP_L_AIL = 195;
	private static final int INOP_R_AIL = 196;
	private static final int INOP_L_R_AIL = 197;
	private static final int INOP_SPLR_1_2_5 = 198;
	private static final int INOP_SPLR_1_2_4_5 = 199;
	private static final int INOP_SPLR_1_3_5 = 200;
	private static final int INOP_SPLR_1_5 = 201;
	private static final int INOP_SPLR_2_3_4 = 202;
	private static final int INOP_SPLR_2_4 = 203;
	private static final int INOP_SPLR_3 = 204;
	private static final int INOP_GND_SPLR_1_2 = 205;
	private static final int INOP_GND_SPLR_3_4 = 206;
	private static final int INOP_SPD_BRK_3_4 = 207;
	private static final int INOP_SPD_BRK_2_3_4 = 208;
	private static final int INOP_L_ELEV = 209;
	private static final int INOP_R_ELEV = 210;
	private static final int INOP_L_R_ELEV = 211;
	private static final int INOP_LAF = 212;
	private static final int INOP_STABILIZER = 213;
	// Rudder 
	private static final int INOP_RUD_TRIM_1 = 214;
	private static final int INOP_RUD_TRIM_2 = 215;
	private static final int INOP_RUD_TRIM = 216;
	private static final int INOP_RUD_TRV_LIM_1 = 217;
	private static final int INOP_RUD_TRV_LIM_2 = 218;
	private static final int INOP_RUD_TRV_LIM_1_2 = 219;
	private static final int INOP_YAW_DAMPER_1 = 220;
	private static final int INOP_YAW_DAMPER_2 = 221;
	// Brakes / Wheels
	private static final int INOP_LGCIU_1 = 222;
	private static final int INOP_LGCIU_2 = 223;
	private static final int INOP_N_W_STEER = 224;
	private static final int INOP_AUTO_BRK = 225;
	private static final int INOP_NORM_BRK = 226;
	private static final int INOP_ALTN_BRK = 227;
	private static final int INOP_L_G_RETRACT = 228;
	private static final int INOP_L_G_DOOR = 229;
	private static final int INOP_CARGO_DOOR = 230;
	private static final int INOP_DFDR = 231;

	
	private static final int MAX_INOP_MSG = 231;

 
	private boolean inop_status[];
	private Color inop_color[];

	public SystemsStatus(ModelFactory model_factory, MFDGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		inop_status = new boolean[MAX_INOP_MSG];
		inop_color = new Color[MAX_INOP_MSG];
		for (int i=0; i< MAX_INOP_MSG; i++) {
			inop_status[i]= false;
			inop_color[i] = mfd_gc.ecam_caution_color;
		}
	}

	public void paint(Graphics2D g2) {
		// aircraft.num_batteries() 
		// aircraft.num_buses()
		// aircraft.num_generators()
		// aircraft.num_inverters()

		if ( mfd_gc.powered && avionics.get_mfd_mode() == Avionics.MFD_MODE_STATUS) {
			// Page ID
			drawPageID(g2, "STATUS");
			drawSeparation(g2);
			drawInopSystems(g2);
		}
	}
	
	private void drawPageID(Graphics2D g2, String page_str) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.setFont(mfd_gc.font_xxl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width/2 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, page_str)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 11/10;     	
		g2.drawString(page_str, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, page_str), page_id_y + mfd_gc.line_height_m/8);
	}

	private void drawSeparation(Graphics2D g2) {
		g2.setColor(mfd_gc.ecam_markings_color);
		g2.drawLine(mfd_gc.sys_line_x, mfd_gc.sys_line_top_y, mfd_gc.sys_line_x, mfd_gc.sys_line_bottom_y);
		
	}

	
	private void drawInopSystems(Graphics2D g2) {
		String str_title = "INOP SYS";
		String inop_str;
		g2.setColor(mfd_gc.ecam_caution_color);
		g2.setFont(mfd_gc.font_xl);
		int page_id_x = mfd_gc.panel_rect.x + mfd_gc.panel_rect.width*3/4 - mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_title)/2;
		int page_id_y = mfd_gc.panel_rect.y + mfd_gc.line_height_xl * 21/10;     	
		g2.drawString(str_title, page_id_x, page_id_y);
		g2.drawLine(page_id_x, page_id_y + mfd_gc.line_height_xl/8, page_id_x + mfd_gc.get_text_width(g2, mfd_gc.font_xl, str_title), page_id_y + mfd_gc.line_height_m/8);
		
		// Flight Controls
    	boolean hyd_g = this.aircraft.get_hyd_press(0) > 0.4f;
    	boolean hyd_y = this.aircraft.get_hyd_press(1) > 0.4f;
    	boolean hyd_b = this.aircraft.get_hyd_press(2) > 0.4f;
    	boolean elac1 = avionics.is_qpac() ? avionics.qpac_fcc(0) : true;
    	boolean elac2 = avionics.is_qpac() ? avionics.qpac_fcc(1) : true;
    	boolean sec1 = avionics.is_qpac() ? avionics.qpac_fcc(2) : true;
    	boolean sec2 = avionics.is_qpac() ? avionics.qpac_fcc(3) : true;
    	boolean fcc_ok = elac1 || elac2 || sec1 || sec2 ;
    	// TODO : Surface status should be computed in Aircraft Class.
    	boolean l_ail_ok = ((hyd_b && elac1) || (hyd_g && elac2));
    	boolean r_ail_ok = ((hyd_b && elac2) || (hyd_g && elac1));
    	boolean l_elev_ok = ((hyd_g && (sec2 || elac2)) || (hyd_b && (sec1 || elac1)));
    	boolean r_elev_ok = ((hyd_b && (sec1 || elac1)) || (hyd_y && (sec2 || elac2)));
    	boolean rudder_ok = hyd_b || hyd_g || hyd_y;

    	if ( (! l_ail_ok) && (! r_ail_ok) ) {
        	inop_status[INOP_L_R_AIL] = true;
        	inop_color[INOP_L_R_AIL] = mfd_gc.ecam_caution_color;
        	inop_status[INOP_L_AIL] = false;
        	inop_status[INOP_R_AIL] = false;
        } else {
        	inop_status[INOP_L_R_AIL] = false;
        	if ( ! l_ail_ok ) {
        		inop_status[INOP_L_AIL] = true;
        		inop_color[INOP_L_AIL] = mfd_gc.ecam_caution_color;
        	} else {
        		inop_status[INOP_L_AIL] = false;
        	}
        	if ( ! r_ail_ok ) {
        		inop_status[INOP_R_AIL] = true;
        		inop_color[INOP_R_AIL] = mfd_gc.ecam_caution_color;
        	} else {
        		inop_status[INOP_R_AIL] = false;
        	}
        }

    	if ( (! l_elev_ok) && (! r_elev_ok) ) {
        	inop_status[INOP_L_R_ELEV] = true;
        	inop_color[INOP_L_R_ELEV] = mfd_gc.ecam_caution_color;
        	inop_status[INOP_L_ELEV] = false;
        	inop_status[INOP_R_ELEV] = false;
        } else {
        	inop_status[INOP_L_R_ELEV] = false;
        	if ( ! l_elev_ok ) {
        		inop_status[INOP_L_ELEV] = true;
        		inop_color[INOP_L_ELEV] = mfd_gc.ecam_caution_color;
        	} else {
        		inop_status[INOP_L_ELEV] = false;
        	}

        	if ( ! r_elev_ok ) {
        		inop_status[INOP_R_ELEV] = true;
        		inop_color[INOP_R_ELEV] = mfd_gc.ecam_caution_color;
        	} else {
        		inop_status[INOP_R_ELEV] = false;
        	}
        }

        
        // display the first 8 lines of the pop list
        for (int i=0, line=0; i < MAX_INOP_MSG && line < 8; i++) {
        	if (inop_status[i]) { 
        			inop_str = inop_list[i];
        			g2.setColor(inop_color[i]);
        			g2.drawString(inop_str, mfd_gc.sys_inop_x,mfd_gc.sys_inop_top_y + line * mfd_gc.line_height_l);          			
        			line++;
        	}
        	
        }
	}
}
