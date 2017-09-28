/**
* SimCommand.java
* 
* Model class for an sending commands to simulators.
* 
* Copyright (C) 2016  Nicolas Carel
* 
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
package net.sourceforge.xhsi.model;

public interface SimCommand {
    public static final int CMD_EFIS_CAPT_CSTR = 100;
    public static final int CMD_EFIS_CAPT_TFC = 137;
    public static final int CMD_EFIS_CAPT_WPT = 101;
    public static final int CMD_EFIS_CAPT_VOR = 102;
    public static final int CMD_EFIS_CAPT_NDB = 103;
    public static final int CMD_EFIS_CAPT_APT = 104;
    public static final int CMD_EFIS_CAPT_FD = 105;
    public static final int CMD_EFIS_CAPT_ILS = 106;
    public static final int CMD_EFIS_CAPT_INHG = 107;
    public static final int CMD_EFIS_CAPT_HPA = 108;
    public static final int CMD_EFIS_CAPT_NAVAID1_ADF = 109;
    public static final int CMD_EFIS_CAPT_NAVAID1_OFF = 110;
    public static final int CMD_EFIS_CAPT_NAVAID1_VOR = 111;
    public static final int CMD_EFIS_CAPT_NAVAID2_ADF = 112;
    public static final int CMD_EFIS_CAPT_NAVAID2_OFF = 113;
    public static final int CMD_EFIS_CAPT_NAVAID2_VOR = 114;
    public static final int CMD_EFIS_CAPT_BARO_STD = 115;
    public static final int CMD_EFIS_CAPT_BARO_INC = 116;
    public static final int CMD_EFIS_CAPT_BARO_DEC = 117;
    public static final int CMD_EFIS_CAPT_RANGE_INC = 118;
    public static final int CMD_EFIS_CAPT_RANGE_DEC = 119;
    public static final int CMD_EFIS_CAPT_RANGE_10 = 120;
    public static final int CMD_EFIS_CAPT_RANGE_20 = 121;
    public static final int CMD_EFIS_CAPT_RANGE_40 = 122;
    public static final int CMD_EFIS_CAPT_RANGE_80 = 123;
    public static final int CMD_EFIS_CAPT_RANGE_160 = 124;
    public static final int CMD_EFIS_CAPT_RANGE_320 = 125;
    public static final int CMD_EFIS_CAPT_RANGE_640 = 126;
    public static final int CMD_EFIS_CAPT_MODE_ILS = 127;
    public static final int CMD_EFIS_CAPT_MODE_VOR = 128;
    public static final int CMD_EFIS_CAPT_MODE_NAV = 129;
    public static final int CMD_EFIS_CAPT_MODE_ARC = 130;
    public static final int CMD_EFIS_CAPT_MODE_PLN = 131;
    public static final int CMD_EFIS_CAPT_MODE_INC = 132;
    public static final int CMD_EFIS_CAPT_MODE_DEC = 133;
    public static final int CMD_EFIS_CAPT_CHRONO = 134;
    public static final int CMD_EFIS_CAPT_STICK = 135;
    public static final int CMD_EFIS_CAPT_TERRAIN_ND = 136;
    
    public static final int CMD_EFIS_FO_CSTR = 200;
    public static final int CMD_EFIS_FO_TFC = 237;
    public static final int CMD_EFIS_FO_WPT = 201;
    public static final int CMD_EFIS_FO_VOR = 202;
    public static final int CMD_EFIS_FO_NDB = 203;
    public static final int CMD_EFIS_FO_APT = 204;
    public static final int CMD_EFIS_FO_FD = 205;
    public static final int CMD_EFIS_FO_ILS = 206;
    public static final int CMD_EFIS_FO_INHG = 207;
    public static final int CMD_EFIS_FO_HPA = 208;
    public static final int CMD_EFIS_FO_NAVAID1_ADF = 209;
    public static final int CMD_EFIS_FO_NAVAID1_OFF = 210;
    public static final int CMD_EFIS_FO_NAVAID1_VOR = 211;
    public static final int CMD_EFIS_FO_NAVAID2_ADF = 212;
    public static final int CMD_EFIS_FO_NAVAID2_OFF = 213;
    public static final int CMD_EFIS_FO_NAVAID2_VOR = 214;
    public static final int CMD_EFIS_FO_BARO_STD = 215;
    public static final int CMD_EFIS_FO_BARO_INC = 216;
    public static final int CMD_EFIS_FO_BARO_DEC = 217;
    public static final int CMD_EFIS_FO_RANGE_INC = 218;
    public static final int CMD_EFIS_FO_RANGE_DEC = 219;
    public static final int CMD_EFIS_FO_RANGE_10 = 220;
    public static final int CMD_EFIS_FO_RANGE_20 = 221;
    public static final int CMD_EFIS_FO_RANGE_40 = 222;
    public static final int CMD_EFIS_FO_RANGE_80 = 223;
    public static final int CMD_EFIS_FO_RANGE_160 = 224;
    public static final int CMD_EFIS_FO_RANGE_320 = 225;
    public static final int CMD_EFIS_FO_RANGE_640 = 226;
    public static final int CMD_EFIS_FO_MODE_ILS = 227;
    public static final int CMD_EFIS_FO_MODE_VOR = 228;
    public static final int CMD_EFIS_FO_MODE_NAV = 229;
    public static final int CMD_EFIS_FO_MODE_ARC = 230;
    public static final int CMD_EFIS_FO_MODE_PLN = 231;
    public static final int CMD_EFIS_FO_MODE_INC = 232;
    public static final int CMD_EFIS_FO_MODE_DEC = 233;
    public static final int CMD_EFIS_FO_CHRONO = 234;
    public static final int CMD_EFIS_FO_STICK = 235;
    public static final int CMD_EFIS_FO_TERRAIN_ND = 236;
    
    public static final int CMD_ECAM_TO_CFG = 300;
    public static final int CMD_ECAM_EMER_CANC = 301;
    public static final int CMD_ECAM_ENG = 302;
    public static final int CMD_ECAM_BLEED = 303;
    public static final int CMD_ECAM_PRESS = 304;
    public static final int CMD_ECAM_ELEC = 305;
    public static final int CMD_ECAM_HYD = 306;
    public static final int CMD_ECAM_FUEL = 307;
    public static final int CMD_ECAM_APU = 308;
    public static final int CMD_ECAM_COND = 309;
    public static final int CMD_ECAM_DOOR = 310;
    public static final int CMD_ECAM_WHEEL = 311;
    public static final int CMD_ECAM_FCTL = 312;
    public static final int CMD_ECAM_ALL = 313;
    public static final int CMD_ECAM_CLR = 314;
    public static final int CMD_ECAM_STS = 315;
    public static final int CMD_ECAM_RCL = 316;
    public static final int CMD_ECAM_APT_CHART = 317;
    public static final int CMD_ECAM_RTU = 318;
    public static final int CMD_ECAM_FPLN = 319;
    public static final int CMD_FCU_AP1 = 330;
    public static final int CMD_FCU_AP2 = 331;
    public static final int CMD_FCU_ATHR = 332;
    public static final int CMD_FCU_LOC = 333;
    public static final int CMD_FCU_APPR = 334;
    public static final int CMD_FCU_EXP = 335;
    public static final int CMD_FCU_METRIC = 336;
    public static final int CMD_FCU_TRK_FPA = 337;
    public static final int CMD_FCU_MACH = 338;
    public static final int CMD_FCU_SPD_UP = 339;
    public static final int CMD_FCU_SPD_DOWN = 340;
    public static final int CMD_FCU_SPD_MNG = 341;
    public static final int CMD_FCU_SPD_SEL = 342;
    public static final int CMD_FCU_HDG_UP = 343;
    public static final int CMD_FCU_HDG_DOWN = 344;
    public static final int CMD_FCU_WLV = 368; 
    public static final int CMD_FCU_HDG_MNG = 345;
    public static final int CMD_FCU_HDG_SEL = 346;
    public static final int CMD_FCU_ALT_UP = 347;
    public static final int CMD_FCU_ALT_DOWN = 348;
    public static final int CMD_FCU_ALT_MNG = 349;
    public static final int CMD_FCU_ALT_SEL = 350;
    public static final int CMD_FCU_ALT_FINE = 351;
    public static final int CMD_FCU_ALT_COARSE = 352;
    public static final int CMD_FCU_VS_UP = 353;
    public static final int CMD_FCU_VS_DOWN = 354;
    public static final int CMD_FCU_VS_SEL = 355;
    public static final int CMD_FCU_VS_LVLOFF = 356;
    public static final int CMD_MASTER_WRN = 357;
    public static final int CMD_MASTER_CTN = 358;
    public static final int CMD_A_SKID_OFF = 360;
    public static final int CMD_A_SKID_ON = 361;
    public static final int CMD_ABRK_LOW = 362;
    public static final int CMD_ABRK_MED = 363;
    public static final int CMD_ABRK_MAX = 364;
    public static final int CMD_BRK_FAN = 365;
    public static final int CMD_GEAR_UP = 366;
    public static final int CMD_GEAR_DOWN = 367;
    // Next is 369
    public void send(int var1);
}
