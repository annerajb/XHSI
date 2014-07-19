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
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.sourceforge.xhsi.XHSIPreferences;
//import net.sourceforge.xhsi.XHSIStatus;
//import net.sourceforge.xhsi.PreferencesObserver;
//import net.sourceforge.xhsi.ProgressObserver;

//import net.sourceforge.xhsi.model.Airport;
//import net.sourceforge.xhsi.model.ComRadio;
//import net.sourceforge.xhsi.model.CoordinateSystem;
//import net.sourceforge.xhsi.model.Fix;
//import net.sourceforge.xhsi.model.Localizer;
//import net.sourceforge.xhsi.model.NavigationObjectRepository;
//import net.sourceforge.xhsi.model.RadioNavigationObject;
//import net.sourceforge.xhsi.model.RadioNavBeacon;
//import net.sourceforge.xhsi.model.Runway;
import net.sourceforge.xhsi.model.TaxiChart;


public class AptNavXP900DatTaxiChartBuilder extends Thread {

//    private String NAV_file = "/Resources/default data/earth_nav.dat";
//    private String FIX_file = "/Resources/default data/earth_fix.dat";
//    private String AWY_file = "/Resources/default data/earth_awy.dat";
    private String APT_file = "/apt.dat";
    private String APT_xplane = "/Resources/default scenery/default apt dat/Earth nav data/apt.dat";
    private String pathname_to_aptnav;
    private TaxiChart taxi_chart;
    private String requested_chart;
//    private NavigationObjectRepository nor;
//    private ProgressObserver progressObserver;
//    private Fix fix;
    

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");


    public AptNavXP900DatTaxiChartBuilder(TaxiChart taxi) throws Exception {
        this.pathname_to_aptnav = XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_APTNAV_DIR);
//        this.nor = NavigationObjectRepository.get_instance();
        //this.taxi_chart = TaxiChart.get_instance();
        this.taxi_chart = taxi;
//        this.progressObserver = null;
    }


    public void get_chart(String icao) throws Exception {

        if ( this.isAlive() ) {

            logger.warning("Already searching for an Airport!");

        } else {

            if (new File(this.pathname_to_aptnav).exists()) {
                logger.fine("Start reading AptNav resource files in " + XHSIPreferences.PREF_APTNAV_DIR);

                this.requested_chart = icao.trim();
                this.start();

            } else {
                logger.warning("AptNav resources directory is wrong!");
            }

        }

    }


    public void run() {

        try {
            search_arpt(this.requested_chart);
        } catch (Exception e) {
            logger.warning("Problem loading AirportChart "+this.requested_chart);
        }

    }


    private void search_arpt(String icao) throws Exception {
        
        this.taxi_chart.new_chart(icao);

        boolean chart_found = false;
        
        File scenery_packs_ini = new File( this.pathname_to_aptnav + "/Custom Scenery/scenery_packs.ini");
        if ( scenery_packs_ini.exists() ) {
//logger.warning("Found: " + scenery_packs_ini.getPath());
            // There is an ini-file that defines the load order of custom scenery
            BufferedReader reader = new BufferedReader( new FileReader( scenery_packs_ini ));
            String line;
            String[] tokens;

            while ( ( (line = reader.readLine()) != null ) && ! chart_found ) {
                tokens = line.split("\\s+", 2);
                if ( (tokens.length == 2) && tokens[0].equals("SCENERY_PACK") ) {
                    File custom_apt_dat = new File( this.pathname_to_aptnav + "/" + tokens[1] + "/Earth nav data/apt.dat" );
                    if ( custom_apt_dat.exists() ) {
                        // We have a custom apt.dat
                        //logger.warning("Custom scenery " + custom_apt_dat.getPath());
                        chart_found = read_apt_file(custom_apt_dat, icao);
                    } // else logger.warning("No custom apt.dat found at " + custom_apt_dat.getPath());
                }
            }
            
            if (reader != null) {
                reader.close();
            }

        }
        
        if ( ! chart_found ) {
            // search in all Global Scenery packs
            chart_found = scan_apt_files("Global Scenery", icao);
        }

        if ( ! chart_found ) {
            // search in all Global Scenery packs
            chart_found = scan_apt_files("Resources/default scenery", icao);
        }

        if ( ! chart_found ) {
            // DEPRECATED : a single file in the aptnav dir
            File single_apt_dat = new File( this.pathname_to_aptnav + this.APT_file );
            if ( single_apt_dat.exists() ) {
                chart_found = read_apt_file(single_apt_dat, icao);
            }
        }

        if ( ! chart_found ) {
            // send a message that we could't find the airport
            this.taxi_chart.not_found();
        }

    }
    
    
    private boolean scan_apt_files(String basedir, String icao) throws Exception {

        boolean found = false;
        
        File scenery_dir = new File( this.pathname_to_aptnav + "/" + basedir);
        // get the list of packs in scenery_dir
        String[] scenery_packs = scenery_dir.list();
        if ( ( scenery_packs != null ) && ( scenery_packs.length > 0 ) ) {
            // sort alphabetically
            Arrays.sort(scenery_packs);
            int i = 0;
            while ( ( ! found ) && (i!=scenery_packs.length ) ) {
                // check if we have a scenery pack directory
                if ( new File( this.pathname_to_aptnav + "/" + basedir + "/" + scenery_packs[i] ).isDirectory() ) {
                    File apt_file = new File( this.pathname_to_aptnav + "/" + basedir + "/" + scenery_packs[i] + "/Earth nav data/apt.dat");
                    // check if we have an apt.dat file in this pack
                    if ( apt_file.exists() ) {
                        found = read_apt_file(apt_file, icao);
                    }
                }
                i++;
            }
        }
        
        return found;

    }
    
    
    private boolean read_apt_file(File current_file, String icao) throws Exception {

        BufferedReader reader = new BufferedReader( new FileReader( current_file ));
        String line;
        long line_number = 0;

        String[] tokens;
        int info_type;

        boolean arpt_hit = false;
        boolean finish = false;
        
        while ( ! arpt_hit && ( (line = reader.readLine()) != null ) ) {

            if ( line.startsWith("1 ") ) {
                tokens = line.split("\\s+",6);
                arpt_hit = tokens[4].equalsIgnoreCase(icao);
if (arpt_hit) logger.config("Found " + icao + " in " + current_file.getPath());
            }

        }
        
        while ( arpt_hit && ( (line = reader.readLine()) != null ) && ! finish ) {

            if ( line.length() > 0 ) {

                line = line.trim();

                try {

                    tokens = line.split("\\s+",10);
                    info_type = Integer.parseInt(tokens[0]);
//logger.warning("Info type : "+ info_type);
                    if (info_type == 1) {

                        // we got to a new airport header; close and exit...
//logger.warning("Must have reached the end of "+icao);
                        this.taxi_chart.close_chart();
                        finish = true;

                    } else if (info_type == 10) {

                        // a new taxiway or ramp in old APT810 format
//logger.warning("ARPT "+icao+" segemnt type 10 : "+tokens[1]+" "+tokens[2]+" "+tokens[4]+" "+tokens[5]+" "+tokens[8]);
                        this.taxi_chart.new_segment( Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[4]),
                                Integer.parseInt(tokens[5]),
                                Integer.parseInt(tokens[8])
                                );

                    } else if (info_type == 110) {

                        // a new taxiway or ramp
                        this.taxi_chart.new_pavement( Integer.parseInt(tokens[1]) );

                    } else if (info_type == 120) {

                        // a new "Lineair feature" means that our pavement is complete
                        this.taxi_chart.close_pavement();
                        // for the rest, we don't use this

                    } else if (info_type == 130) {

                        this.taxi_chart.new_border();

                    } else if ( (info_type == 111) || (info_type == 113) ) {

                        // a node
                        this.taxi_chart.new_node( Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]) );

                        if (info_type == 113) this.taxi_chart.close_loop();

                    } else if ( (info_type == 112) || (info_type == 114) ) {

                        // a node with bezier control point
                        this.taxi_chart.new_bezier_node( Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]) );

                        if (info_type == 114) this.taxi_chart.close_loop();

                    } else if (info_type == 99) {

                        // end of file; close and exit
                        this.taxi_chart.close_chart();
                        finish = true;

                    }

                } catch (Exception e) {
                    logger.warning("Parse error in " +current_file.getName() + ":" + line_number + "(" + e + ") " + line);
                }

            } // line !isEmpty

        } // while arpt_hit && readLine

        if (reader != null) {
            reader.close();
        }
        
        return arpt_hit;

    }


}
