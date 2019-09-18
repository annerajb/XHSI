/**
* AptNavXP900DatNavigationObjectBuilder.java
*
* Reads X-Planes earth nav data databases nav.dat, fix.dat and apt.dat and
* stores extracted data in NavigationObjectRepository.
*
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
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
package net.sourceforge.xhsi.model.aptnavdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.ProgressObserver;

import net.sourceforge.xhsi.model.Airport;
import net.sourceforge.xhsi.model.ComRadio;
import net.sourceforge.xhsi.model.CoordinateSystem;
import net.sourceforge.xhsi.model.Fix;
import net.sourceforge.xhsi.model.Helipad;
import net.sourceforge.xhsi.model.Heliport;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;
import net.sourceforge.xhsi.model.Runway;


public class AptNavXP900DatNavigationObjectBuilder implements PreferencesObserver {

    private String NAV_file = "/earth_nav.dat";
    private String NAV_xplane = "/Resources/default data" + NAV_file;
    private String NAV_custom = "/Custom Data" + NAV_file;
    private String FIX_file = "/earth_fix.dat";
    private String FIX_xplane = "/Resources/default data" + FIX_file;
    private String FIX_custom = "/Custom Data" + FIX_file;
    private String AWY_file = "/earth_awy.dat";
    private String AWY_xplane = "/Resources/default data" + AWY_file;
    private String AWY_custom = "/Custom Data" + AWY_file;
    private String APT_file = "/apt.dat";
    private String APT_xplane = "/Resources/default scenery/default apt dat/Earth nav data" + "/apt.dat";
    private String pathname_to_aptnav;
    private NavigationObjectRepository nor;
    private ProgressObserver progressObserver;
    private Fix fix;
    
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public AptNavXP900DatNavigationObjectBuilder(String pathname_to_aptnav) throws Exception {
        this.pathname_to_aptnav = pathname_to_aptnav;
        this.nor = NavigationObjectRepository.get_instance();
        this.progressObserver = null;
    }


    public void set_progress_observer(ProgressObserver observer) {
        this.progressObserver = observer;
    }


    public void read_all_tables() throws Exception {

        if (new File(this.pathname_to_aptnav).exists()) {
            logger.info("Start reading AptNav resource files in " + XHSIPreferences.PREF_APTNAV_DIR);

//            this.nor.init();

            // read the "<aptnavdir>/Custom Scenery/<pack>/Earth nav data/apt.dat" in the other specified by scenery_packs.ini
            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading Custom Scenery APT ...", 0.0f);
            }
            File scenery_packs_ini = new File( this.pathname_to_aptnav + "/Custom Scenery/scenery_packs.ini");
            if ( scenery_packs_ini.exists() ) {
                // There is an ini-file that defines the load order of custom scenery
                BufferedReader reader = new BufferedReader( new FileReader( scenery_packs_ini ));
                String line;
                String[] tokens;

                while ( (line = reader.readLine()) != null ) {
                    tokens = line.split("\\s+", 2);
                    if ( (tokens.length == 2) && tokens[0].equals("SCENERY_PACK") ) {
                        File custom_apt_file = new File( this.pathname_to_aptnav + "/" + tokens[1] + "/Earth nav data/apt.dat" );
                        if ( custom_apt_file.exists() ) {

                            // We have a custom apt.dat
                            logger.config("Loading Custom Scenery APT " + custom_apt_file.getPath());
                            read_an_apt_file(custom_apt_file);

                        } // else logger.warning("No custom apt.dat found at " + custom_apt_dat.getPath());
                    }
                }
                //  ressource leak fix
                reader.close();
            }

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading Global Scenery APT ...", 10.0f);
            }
            // read the "<aptnavdir>/Global Scenery/<pack>/Earth nav data/apt.dat" in alphabetical other
            scan_apt_files("Global Scenery");
            
            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading Default Scenery APT ...", 20.0f);
            }
            if ( new File( this.pathname_to_aptnav + this.APT_file ).exists() ) {
                logger.info("Reading APT database ( " + this.pathname_to_aptnav + this.APT_file + " ) Reading from a single directory is DEPRECATED !");
                File aptnav_apt_file = new File( this.pathname_to_aptnav + this.APT_file );
                read_an_apt_file(aptnav_apt_file);
            }
            // read the "<aptnavdir>/Resources/default scenery/<pack>/Earth nav data/apt.dat" in alphabetical other
            scan_apt_files("Resources/default scenery");

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading NAV ...", 40.0f);
            }
            read_nav_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading FIX ...", 60.0f);
            }
            read_fix_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading AWY ...", 80.0f);
            }
            read_awy_table();

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Done!", 100.0f);
            }
        } else {
            logger.warning("AptNav resources directory is wrong!");
        }

    }


    private void scan_apt_files(String basedir) throws Exception {

        File scenery_dir = new File( this.pathname_to_aptnav + "/" + basedir);
        // get the list of packs in scenery_dir
        String[] scenery_packs = scenery_dir.list();
        if ( ( scenery_packs != null ) && ( scenery_packs.length > 0 ) ) {
            // sort alphabetically
            Arrays.sort(scenery_packs);
            for ( int i=0; i!=scenery_packs.length; i++ ) {
                // check if we have a scenery pack directory
                if ( new File( this.pathname_to_aptnav + "/" + basedir + "/" + scenery_packs[i] ).isDirectory() ) {
                    File apt_file = new File( this.pathname_to_aptnav + "/" + basedir + "/" + scenery_packs[i] + "/Earth nav data/apt.dat");
                    // check if we have an apt.dat file in this pack
                    if ( apt_file.exists() ) {
                        logger.config("Loading " + basedir + " APT " + apt_file.getPath());
                        read_an_apt_file(apt_file);
                    }
                }
            }
        }

    }
    
    
    private void read_an_apt_file(File apt_file) throws Exception {

        BufferedReader reader = new BufferedReader( new FileReader( apt_file ));
        String line;
        long line_number = 0;
        int info_type;
        String[] tokens;
        String airport_icao_code = "";
        String airport_name = "";
        boolean is_heliport=false;
        boolean current_airport_saved = true; // this is a trick to say that there is no previous airport when starting to read the first 
        ArrayList<Runway> runways = new ArrayList<Runway>();
        ArrayList<Helipad> helipads = new ArrayList<Helipad>();
        float width;
        int surface;
        String rwy_num1;
        float thr1_lat;
        float thr1_lon;
        String rwy_num2;
        float thr2_lat;
        float thr2_lon;
        float length;
        float lat = 0;
        float lon = 0;
        float tower_lat = 0;
        float tower_lon = 0;
        float arp_lat = 0;
        float arp_lon = 0;
        float longest = 0;
        float rwy_count = 0;
        float lat_sum = 0;
        float lon_sum = 0;
        float hard_rwy_count = 0;
        float hard_lat_sum = 0;
        float hard_lon_sum = 0;
        boolean tower = false;
        int elev = 0;
        ArrayList<ComRadio> comms = new ArrayList<ComRadio>();

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0 ) {

                line_number++;

                line = line.trim();
                if ((line_number > 2) /* && ( ! line.equals("99") ) */ ) {
                    try {
                        if ( line.equals("99") ) {
                            // a line with a fake airport to force saving the last
                            tokens = "1 9999 0 0 XXXX Fake Airport to force saving the last".split("\\s+",6);
                            info_type = Integer.parseInt(tokens[0]);
                        } else {
                            tokens = line.split("\\s+",6);
                            info_type = Integer.parseInt(tokens[0]);
                        }
                        if (info_type == 1 || info_type == 17) {
                        	// TODO: type 16 : Seaplane base
                        	// TODO: type 17 : heliport
                            // hold it, save the previous airport before proceeding with this one...
                            if ( ! current_airport_saved ) {
                                // when this is the first airport that whe read, there is no previous airport
                                // that is why current_airport_saved is initialized to true
                                // position of the ARP (Aerodrome Reference Point)
                                if (tower) {
                                    // ARP = tower position
                                    arp_lat = tower_lat;
                                    arp_lon = tower_lon;
                                } else if (hard_rwy_count == 1) {
                                    // ARP = the center of the one and only hard runway
                                    arp_lat = hard_lat_sum;
                                    arp_lon = hard_lon_sum;
                                } else if (rwy_count == 1) {
                                    // ARP = the center of the one and only non-hard runway
                                    arp_lat = lat_sum;
                                    arp_lon = lon_sum;
                                } else if (hard_rwy_count > 1) {
                                    // no tower, but several hard runways
                                    // ARP = center of all hard runways
                                    arp_lat = hard_lat_sum / hard_rwy_count;
                                    arp_lon = hard_lon_sum / hard_rwy_count;
                                } else {
                                    // no hard runways and no tower
                                    // ARP = center of all non-hard runways
                                    arp_lat = lat_sum / rwy_count;
                                    arp_lon = lon_sum / rwy_count;
                                }
                                if (is_heliport) {
                                    nor.add_nav_object(new Heliport(airport_name, airport_icao_code, arp_lat, arp_lon, helipads, elev, comms));                               	
                                } else {
                                	nor.add_nav_object(new Airport(airport_name, airport_icao_code, arp_lat, arp_lon, runways, helipads, longest, elev, comms));
                                }
                            }
                            // process the new airport header
                            //elev = Integer.parseInt(line.substring(5, 10).trim());
                            elev = Integer.parseInt(tokens[1]);
                            //airport_icao_code = line.substring(15, 19);
                            airport_icao_code = tokens[4];
                            //airport_name = line.substring(20);
                            airport_name = tokens[5];
                            current_airport_saved = false;
                            runways = new ArrayList<Runway>();
                            helipads = new ArrayList<Helipad>();
                            arp_lat = 0;
                            arp_lon = 0;
                            longest = 0;
                            rwy_count = 0;
                            lat_sum = 0;
                            lon_sum = 0;
                            hard_rwy_count = 0;
                            hard_lat_sum = 0;
                            hard_lon_sum = 0;
                            tower = false;
                            comms = new ArrayList<ComRadio>();
                            is_heliport = (info_type == 17);
                            // we dont't save this airport right away, we collect information about the runways first
                        } else if (info_type == 100) {
                            // a runway
                            // we need more tokens
                            tokens = line.split("\\s+",26);
                            width = Float.parseFloat(tokens[1]);
                            surface = Integer.parseInt(tokens[2]);
                            rwy_num1 = tokens[8];
                            thr1_lat = Float.parseFloat(tokens[9]);
                            thr1_lon = Float.parseFloat(tokens[10]);
                            rwy_num2 = tokens[17];
                            thr2_lat = Float.parseFloat(tokens[18]);
                            thr2_lon = Float.parseFloat(tokens[19]);
                            length = CoordinateSystem.rough_distance(thr1_lat, thr1_lon, thr2_lat, thr2_lon) * 1851.852f; // meters!
                            lat = ( thr1_lat + thr2_lat ) / 2;
                            lon = ( thr1_lon + thr2_lon ) / 2;
                            Runway new_rwy = new Runway(airport_icao_code, length, width, surface, rwy_num1, thr1_lat, thr1_lon, rwy_num2, thr2_lat, thr2_lon);
                            nor.add_nav_object(new_rwy);
                            //runways.add( nor.get_runway(airport_icao_code, lat, lon) );
                            runways.add(new_rwy);
                            // find the longest runway for this airport
                            if (length > longest) longest = length;
                            // calculate an average of all threshold lats and lons to define the ARP
                            rwy_count += 1;
                            lat_sum += lat;
                            lon_sum += lon;
                            if ( (surface==Runway.RWY_ASPHALT) || (surface==Runway.RWY_CONCRETE) ) {
                                hard_rwy_count += 1;
                                hard_lat_sum += lat;
                                hard_lon_sum += lon;
                            }
                            //lat = lat_sum / rwy_count;
                            //lon = lon_sum / rwy_count;
                        } else if (info_type == 102) {
                            // an helipad
                            // we need more tokens
                        	// 102 H1  35.59011673 -117.63945737   0.00 24.40 24.40 5 0 0 0.00 1
                        	// 102 <designator> <lat> <lon> <orientation> <length> <width> <surface code> <marking> <shoulder surface type> <smoothnesse> <edge lighting>
                            tokens = line.split("\\s+",13);
                            rwy_num1 = tokens[1];
                            lat = Float.parseFloat(tokens[2]);
                            lon = Float.parseFloat(tokens[3]);
                            length = Float.parseFloat(tokens[5]);
                            width = Float.parseFloat(tokens[6]);
                            surface = Integer.parseInt(tokens[7]);
                            Helipad new_helipad = new Helipad(airport_icao_code, length, width, surface, rwy_num1, lat, lon);
                            nor.add_nav_object(new_helipad);                            
                            helipads.add(new_helipad);                            
                        } else if (info_type == 14) {
                            // if defined in the file, the tower position can be used as the ARP
                            tower = true;
                            // we already have enough tokens tokens = line.split("\\s+",3);
                            tower_lat = Float.parseFloat(tokens[1]);
                            tower_lon = Float.parseFloat(tokens[2]);
                        } else if ( (info_type >= 50) && (info_type < 60) ) {
                            // COM Radio
                            // we need the name, which can include spaces, in the third token
                            tokens = line.split("\\s+",3);
                            comms.add(new ComRadio(airport_icao_code, tokens.length==3?tokens[2]:"", (float)Float.parseFloat(tokens[1]) / 100.0f));
//                        } else if ((info_type == 99) && (current_airport_saved == false)) {
//                            // end of file, save the last airport
//                            if ( ! current_airport_saved ) {
//                                // position of the ARP (Aerodrome Reference Point)
//                                if (tower) {
//                                    // ARP = tower position
//                                    arp_lat = tower_lat;
//                                    arp_lon = tower_lon;
//                                } else if (hard_rwy_count == 1) {
//                                    // ARP = the center of the one and only hard runway
//                                    arp_lat = hard_lat_sum;
//                                    arp_lon = hard_lon_sum;
//                                } else if (rwy_count == 1) {
//                                    // ARP = the center of the one and only non-hard runway
//                                    arp_lat = lat_sum;
//                                    arp_lon = lon_sum;
//                                } else if (hard_rwy_count > 1) {
//                                    // no tower, but several hard runways
//                                    // ARP = center of all hard runways
//                                    arp_lat = hard_lat_sum / hard_rwy_count;
//                                    arp_lon = hard_lon_sum / hard_rwy_count;
//                                } else {
//                                    // no hard runways and no tower
//                                    // ARP = center of all non-hard runways
//                                    arp_lat = lat_sum / rwy_count;
//                                    arp_lon = lon_sum / rwy_count;
//                                }
//                                nor.add_nav_object(new Airport(airport_name, airport_icao_code, lat, lon, runways, longest, elev, comms));
//                                current_airport_saved = true;
//                            }
                        }
                    } catch (Exception e) {
                        logger.warning("\nParse error in " +apt_file.getName() + ":" + line_number + "(" + e + ") " + line);
                    }
                }

            } // line !isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void read_nav_table() throws Exception {

        File file = null;
        if ( new File( this.pathname_to_aptnav + this.NAV_custom ).exists() ) {
            logger.config("Reading NAV database ( " + this.pathname_to_aptnav + this.NAV_custom + " )");
            file = new File( this.pathname_to_aptnav + this.NAV_custom );
        } else if ( new File( this.pathname_to_aptnav + this.NAV_xplane ).exists() ) {
            logger.config("Reading NAV database ( " + this.pathname_to_aptnav + this.NAV_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.NAV_xplane );
        } else if ( new File( this.pathname_to_aptnav + this.NAV_file ).exists() ) {
            logger.info("Reading NAV database ( " + this.pathname_to_aptnav + this.NAV_file + " ) Reading from a single directory is DEPRECATED !");
            file = new File( this.pathname_to_aptnav + this.NAV_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        int info_type;
        String[] tokens;
        long line_number = 0;
        boolean version11 = false;
        int rowcode_field = 0;
        int lat_field = 1;
        int lon_field = 2;
        int elev_field = 3;
        int freq_field = 4;
        int range_field = 5;
        int bearing_field = 6;
        int ident_field = 7;
        int arpt_field = 8;
        int region_field;
        int rwy_field;
        int name_field;
        
        RadioNavigationObject coupled_rno;
        Localizer coupled_loc;
        RadioNavigationObject twin_rno;
        Localizer twin_loc;
        boolean has_a_twin;
        String twin_ilt;
        Localizer new_loc;

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0) {
            // line.isEmpty() doesn't work on java 1.5

                line_number++;

                line = line.trim();

                if ( (line_number == 2) && (line.length() >= 32) ) {
                    
                    // the file format version and cycle number info is on line 2
                    tokens = line.split("\\s+",8);
                    logger.info("NAV file format : "+ tokens[0]);
                    version11 = tokens[0].equals("1100");
                    if (version11) logger.info("X-Plane 11");
                    if ( (tokens[5].length()>2) && (tokens[5].charAt(tokens[5].length()-1)==',') ) {
                        // usually, the cycle number is followed by a comma
                        XHSIStatus.nav_db_cycle = tokens[5].substring(0, tokens[5].length()-1);
                    } else {
                        XHSIStatus.nav_db_cycle = tokens[5];
                    }
                    
                } else if ( (line_number > 2)  && ( ! line.equals("99") ) ) {
                    try {
                        
                        // a generic split that works for types 2, 3 and 13
                        tokens = line.split("\\s+",version11 ? 11 : 9);
                        info_type = Integer.parseInt(tokens[0]);
                        
                        if ( (info_type ==2) || (info_type == 3) || (info_type == 13) ) {
                    
                            if (version11) {
                                name_field = 10;
                            } else {
                                name_field = 8;
                            }
                            // 2=NDB, 3=VOR (VOR, VOR-DME, VORTAC) 13=DME (Standalone DME, TACAN)
                            nor.add_nav_object(new RadioNavBeacon(
                                    tokens[name_field], // name
                                    tokens[ident_field], // ident
                                    info_type,
                                    Float.parseFloat(tokens[lat_field]), // lat
                                    Float.parseFloat(tokens[lon_field]), // lon
                                    Integer.parseInt(tokens[elev_field]), // elev MSL
                                    Float.parseFloat(tokens[freq_field]), // freq
                                    Integer.parseInt(tokens[range_field]), // range
                                    Float.parseFloat(tokens[bearing_field])  // NDB: zero , VOR: offset
                                ));
                            
                        } else if ((info_type == 4) || (info_type == 5)) {
                            
                            // ILS or LOC
                            tokens = line.split("\\s+",version11 ? 12 : 11);
                            if (version11) {
                                rwy_field = 10;
                                name_field = 11;
                            } else {
                                rwy_field = 9;
                                name_field = 10;
                            }

                            // search for a twin, i.e. an ILS with the same frequency at the same airport
                            twin_rno = nor.find_tuned_nav_object(Float.parseFloat(tokens[lat_field]), Float.parseFloat(tokens[lon_field]), Float.parseFloat(tokens[freq_field])/100.0f, "");
                            has_a_twin = ( (twin_rno != null) && (twin_rno instanceof Localizer) );
                            twin_ilt = "";
                            if ( has_a_twin ) {
                                twin_loc = (Localizer) twin_rno;
                                twin_loc.has_twin = true;
                                twin_loc.twin_ilt = tokens[ident_field];
                                twin_ilt = twin_loc.ilt;
                            }
                            new_loc = new Localizer(
                                    tokens[arpt_field] + " " + tokens[rwy_field], // arpt ICAO + RWY
                                    tokens[ident_field], // ident
                                    info_type,
                                    Float.parseFloat(tokens[lat_field]), // lat
                                    Float.parseFloat(tokens[lon_field]), // lon
                                    Integer.parseInt(tokens[elev_field]), // elev MSL
                                    Float.parseFloat(tokens[freq_field]), // freq
                                    Integer.parseInt(tokens[range_field]), // range
                                    Float.parseFloat(tokens[bearing_field]), // bearing, true degrees
                                    tokens[arpt_field], // ICAO
                                    tokens[rwy_field], // RWY,
                                    tokens[name_field],
                                    has_a_twin,
                                    twin_ilt
                                );
                            nor.add_nav_object(new_loc);
                            // add this localizer to the runway
                            if (nor.get_airport(tokens[arpt_field]) == null) {
                                logger.warning("Error NAV.dat: no AIRPORT found for ILS/LOC " + tokens[arpt_field] + " " + tokens[rwy_field] + " " + tokens[ident_field]);
                            } else {
                                Runway rwy = nor.get_runway(tokens[arpt_field], tokens[rwy_field], Float.parseFloat(tokens[lat_field]), Float.parseFloat(tokens[lon_field]), true);
                                if ( rwy != null ) {
                                    rwy.localizers.add(new_loc);
                                } else {
                                    rwy = nor.get_runway(tokens[arpt_field], tokens[rwy_field].substring(1), Float.parseFloat(tokens[lat_field]), Float.parseFloat(tokens[lon_field]), true);
                                    if ( rwy != null ) {
                                        rwy.localizers.add(new_loc);
                                    } else {
                                        logger.warning("Error NAV.dat: no RUNWAY  found for ILS/LOC " + tokens[arpt_field] + " " + tokens[rwy_field] + " " + tokens[ident_field]);
                                    }
                                }
                            }

                        } else if (info_type == 6) {

                            // update the ILS (or IGS) with this GS
                            // (we can do this in the same loop, since the file is sorted by info_type; the ILS will already be stored)
                            tokens = line.split("\\s+",version11 ? 12 : 11);
                            if (version11) {
                                rwy_field = 10;
                                name_field = 11;
                            } else {
                                rwy_field = 9;
                                name_field = 10;
                            }
                            
                            // tokens[] 0=type, 1=lat, 2=lon, 3=elev, 4=freq, 5=range, 6=glide_angle*100000+course, 7=ident, 8=arpt, 9=rwy, 10="GS"
                            coupled_rno = nor.find_tuned_nav_object(Float.parseFloat(tokens[lat_field]), Float.parseFloat(tokens[lon_field]), Float.parseFloat(tokens[freq_field])/100.0f, tokens[ident_field]);
                            if ( (coupled_rno != null) && (coupled_rno instanceof Localizer) ) {
                                coupled_loc = (Localizer) coupled_rno;
                                coupled_loc.has_gs = true;
                                // when an ILS has a GS, we are more interested in the elev of the GS than the LOC
                                coupled_loc.elevation = Integer.parseInt(tokens[elev_field]);
                            } else {
                                logger.warning("Error NAV.dat: no ILS for GS " + tokens[ident_field] + " " + tokens[freq_field]);
                            }
                            
                        } else if (info_type == 12) {
                            
                            // update the VOR, LOC, ILS or IGS with this DME
                            // (we can do this in the same loop, since the file is sorted by info_type)
                            tokens = line.split("\\s+",9);
                            // tokens[] 0=type, 1=lat, 2=lon, 3=elev, 4=freq, 5=range, 6=bias, 7=ident, 8=name
                            coupled_rno = nor.find_tuned_nav_object(Float.parseFloat(tokens[lat_field]), Float.parseFloat(tokens[lon_field]), Float.parseFloat(tokens[freq_field])/100.0f, tokens[ident_field]);
                            if (coupled_rno != null) {
                                coupled_rno.has_dme = true;
                                coupled_rno.dme_lat = Float.parseFloat(tokens[lat_field]);
                                coupled_rno.dme_lon = Float.parseFloat(tokens[lon_field]);
                            } else {
                                logger.warning("Error NAV.dat: no VOR or LOC for DME " + tokens[ident_field] + " " + tokens[freq_field]);
                            }

// Not used yet ...
//                        } else if ((info_type == 14) || (info_type == 16)) {
//                            
//                            // LP, LPV, WAAS or EGNOS
//                            tokens = line.split("\\s+",version11 ? 12 : 11);
//                            if (version11) {
//                                rwy_field = 10;
//                                name_field = 11;
//                            } else {
//                                // not used before X-Plane 11 !
//                                rwy_field = 9;
//                                name_field = 10;
//                            }
//
//                            // make sure that there is a runway for this LP, LPV, WAAS or EGNOS
//                            if (nor.get_airport(tokens[arpt_field]) == null) {
//                                logger.warning("Error NAV.dat: no AIRPORT found for " + tokens[arpt_field] + " " + tokens[rwy_field] + " " + tokens[ident_field] + " " + tokens[name_field]);
//                            } else {
//                                if (nor.get_runway(tokens[arpt_field], tokens[rwy_field], Float.parseFloat(tokens[lat_field]), Float.parseFloat(tokens[lon_field]), true) == null) {
//                                    logger.warning("Error NAV.dat: no RUNWAY  found for " + tokens[arpt_field] + " " + tokens[rwy_field] + " " + tokens[ident_field] + " " + tokens[name_field]);
//                                }
//                            }

                        }
                        
                    } catch (Exception e) {
                        logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
                    }
                    
                }

            } // line ! isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void read_fix_table() throws Exception {

        File file = null;
        if ( new File( this.pathname_to_aptnav + this.FIX_custom ).exists() ) {
            logger.config("Reading FIX database ( " + this.pathname_to_aptnav + this.FIX_custom + " )");
            file = new File( this.pathname_to_aptnav + this.FIX_custom );
        } else if ( new File( this.pathname_to_aptnav + this.FIX_xplane ).exists() ) {
            logger.config("Reading FIX database ( " + this.pathname_to_aptnav + this.FIX_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.FIX_xplane );
        } else if ( new File( this.pathname_to_aptnav + this.FIX_file ).exists() ) {
            logger.info("Reading FIX database ( " + this.pathname_to_aptnav + this.FIX_file + " ) Reading from a single directory is DEPRECATED !");
            file = new File( this.pathname_to_aptnav + this.FIX_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        String[] tokens;
        long line_number = 0;
        boolean version11 = false;

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0 ) {

                line_number++;

                line = line.trim();
                
                if ( (line_number == 2) && (line.length() >= 32) ) {
                    
                    tokens = line.split("\\s+",2);
                    logger.info("FIX file format : "+ tokens[0]);
                    version11 = tokens[0].equals("1100");
                    
                } else if ( (line_number > 2) && ( ! line.equals("99") ) ) {
                    try {
                        tokens = line.split("\\s+",5);
                        nor.add_nav_object(new Fix(
                                tokens[2],
                                Float.parseFloat(tokens[0]),
                                Float.parseFloat(tokens[1]),
                                version11 && tokens[3].equals("ENRT")));
                    } catch (Exception e) {
                        logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
                    }
                }

            } // line !isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void read_awy_table() throws Exception {

        File file = null;
        if ( new File( this.pathname_to_aptnav + this.AWY_custom ).exists() ) {
            logger.config("Reading AWY database ( " + this.pathname_to_aptnav + this.AWY_custom + " )");
            file = new File( this.pathname_to_aptnav + this.AWY_custom );
        } else if ( new File( this.pathname_to_aptnav + this.AWY_xplane ).exists() ) {
            logger.config("Reading AWY database ( " + this.pathname_to_aptnav + this.AWY_xplane + " )");
            file = new File( this.pathname_to_aptnav + this.AWY_xplane );
        } else if ( new File( this.pathname_to_aptnav + this.AWY_file ).exists() ) {
            logger.info("Reading AWY database ( " + this.pathname_to_aptnav + this.AWY_file + " ) Reading from a single directory is DEPRECATED !");
            file = new File( this.pathname_to_aptnav + this.AWY_file );
        }
        BufferedReader reader = new BufferedReader( new FileReader( file ));
        String line;
        String[] tokens;
        long line_number = 0;
        boolean version11 = false;

        while ((line = reader.readLine()) != null) {

            if ( line.length() > 0 ) {

                line_number++;

                line = line.trim();
                if ( (line_number == 2) && (line.length() >= 32) ) {
                    
                    tokens = line.split("\\s+",2);
                    logger.info("AWY file format : "+ tokens[0]);
                    version11 = tokens[0].equals("1100");
                    
                } else if ( (!version11) && (line_number > 2) && (!line.equals("99")) ) {
                    // if the AWY file is version 1100, then the FIX file will be version 1100 too, and we will already know which fixes are terminal or enroute
                    try {
                        tokens = line.split("\\s+",10);
                        // tokens[] 0=WPT1, 1=lat1, 2=lon1, 3=WPT2, 4=lat2, 5=lon2, 6=low(1)/high(2), 7=bottom, 8=top, 9=ID(s)
                        fix = nor.get_fix( tokens[0], Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]) );
                        if ( fix != null ) fix.on_awy = true;
                    } catch (Exception e) {
                        logger.warning("Parse error in " + file.getName() + ":" + line_number + " '" + line + "' (" + e + ")");
                    }
                }

            } // line !isEmpty

        } // while readLine

        if (reader != null) {
            reader.close();
        }

    }


    public void preference_changed(String key) {

        logger.config("Preference "+key+" changed");
        if (key.equals(XHSIPreferences.PREF_APTNAV_DIR)) {
            // reload navigation databases
            this.pathname_to_aptnav = XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_APTNAV_DIR);
            if (XHSIStatus.nav_db_status.equals(XHSIStatus.STATUS_NAV_DB_NOT_FOUND) == false) {
                try {
                    logger.config("Reload navigation tables");
                    read_all_tables();
                } catch (Exception e) {
                    logger.warning("Could not read navigation tables! (" + e.toString() + ")");
                }
            } else {
                logger.warning("Could not find AptNav Resources! (Status:" + XHSIStatus.nav_db_status + ")");
            }
        }

    }


}
