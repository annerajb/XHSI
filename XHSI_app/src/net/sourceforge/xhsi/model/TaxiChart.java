/**
* TaxiChart.java
* 
* Manages and provides access to navigation objects (VORs, NDBs, fixes,
* arpts via various accessors and search methods.
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
package net.sourceforge.xhsi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


public class TaxiChart {


    public class Segment {
        public float lat;
        public float lon;
        public float orientation;
        public float length;
        public float width;
        public Segment(float n_lat, float n_lon, float n_heading, float n_length, float n_width) {
            this.lat = n_lat;
            this.lon = n_lon;
            this.orientation = n_heading;
            this.length = n_length;
            this.width = n_width;
        }
    }
    
    
    public class Node {
        public float lat;
        public float lon;
        public boolean bezier_node;
        public float bz_lat;
        public float bz_lon;
        public Node(float n_lat, float n_lon) {
            this.lat = n_lat;
            this.lon = n_lon;
            bezier_node = false;
        }
        public Node(float n_lat, float n_lon, float cp_lat, float cp_lon) {
            this.lat = n_lat;
            this.lon = n_lon;
            this.bz_lat = cp_lat;
            this.bz_lon = cp_lon;
            bezier_node = true;
        }
    }
    

    public class Pavement {
        
        public int surface;
        public String name;
        public ArrayList<Node> nodes;
        public ArrayList<Pavement> holes;
        
        public Pavement() {
            holes = new ArrayList();
            nodes = new ArrayList();
        }
        
    }
    
    public Airport airport;

    public String icao;
    
    public boolean ready;

    public float north_lat;
    public float south_lat;
    public float west_lon;
    public float east_lon;
    public float lon_scale;

    private Pavement current_pavement;
    private Pavement current_loop;
    private boolean loop_is_open;
    
    public ArrayList<Pavement> pavements;
    public Pavement border;
    public ArrayList<Segment> segments;

    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");

    private static TaxiChart single_instance;

    public static TaxiChart get_instance() {
        if (TaxiChart.single_instance == null) {
            TaxiChart.single_instance = new TaxiChart();
        }
        return TaxiChart.single_instance;
    }


    
    private TaxiChart() {
        init();
    }


    public void init() {

        airport = null;
        border = null;
        pavements = new ArrayList();
        segments = new ArrayList();
        icao = "";
        ready = true;

    }

    
    public void new_chart(String new_icao) {

        init();
        this.ready = false;
        this.icao = new_icao;
//logger.warning("New Chart:"+icao);

    }

    
    public void new_segment(float lat, float lon, float heading, int length, int width) {

        this.segments.add( new Segment(lat, lon, heading, (float)length * 0.3048f, (float)width * 0.3048f) );
//logger.warning("New Segment");

    }

    
    public void new_pavement(int sfc, String apron) {

        this.current_pavement = new Pavement();
        this.pavements.add(current_pavement);
        this.current_pavement.surface = sfc;
        this.current_pavement.name = apron;
        this.current_loop = this.current_pavement;
        this.loop_is_open = true;
//logger.warning("New Pavement:"+sfc);

    }


    public void new_border() {

        this.current_pavement = new Pavement();
        this.border = this.current_pavement;
        this.current_loop = this.current_pavement;
        this.loop_is_open = true;
//logger.warning("New Border");

    }


    public void new_node(float new_lat, float new_lon) {
        
        if ( this.current_pavement != null ) {

            if ( ! this.loop_is_open ) {
                // this must be the first node of a new hole
                this.loop_is_open = true;
                this.current_loop = new Pavement();
                this.current_pavement.holes.add(current_loop);
            }
            this.current_loop.nodes.add( new Node(new_lat, new_lon) );
//logger.warning("New Node: lat="+new_lat+" lon="+new_lon);

        }
        
    }
    
    
    public void new_bezier_node(float new_lat, float new_lon, float new_cp_lat, float new_cp_lon) {

        if ( this.current_pavement != null ) {

            if ( ! this.loop_is_open ) {
                // this must be the first node of a new hole
                this.loop_is_open = true;
                this.current_loop = new Pavement();
                this.current_pavement.holes.add(current_loop);
            }
            this.current_loop.nodes.add( new Node(new_lat, new_lon, new_cp_lat, new_cp_lon) );
//logger.warning("New Bezier Node: lat="+new_lat+" lon="+new_lon + " / lat="+new_cp_lat+" lon="+new_cp_lon);
        
        }

    }
    
    
    public void close_pavement() {

        this.current_loop = null;
        this.current_pavement = null;
        this.loop_is_open = false;
//logger.warning("Close Pavement");

    }


    public void close_loop() {

        this.current_loop = null;
        this.loop_is_open = false;
//logger.warning("Close Loop");

    }


    private void init_minmax() {

        this.north_lat = -90.0f;
        this.south_lat = 90.0f;
        this.east_lon = -180.0f;
        this.west_lon = 180.0f;

    }


    private void update_minmax( float update_lat, float update_lon ) {

        if ( update_lat > this.north_lat ) this.north_lat = update_lat;
        if ( update_lat < this.south_lat ) this.south_lat = update_lat;
        if ( update_lon > this.east_lon ) this.east_lon = update_lon;
        if ( update_lon < this.west_lon ) this.west_lon = update_lon;

    }


    public void not_found() {
logger.warning("Failed to find "+ icao);

        this.ready = true;

    }


    public void close_chart() {

//logger.warning("Closing "+ icao);
        this.airport = NavigationObjectRepository.get_instance().get_airport(this.icao);
        if ( this.airport == null ) logger.warning("We have a taxichart object for "+this.icao+", but no airport navigation object!");
//logger.warning("NOR : "+ this.airport.name);

        init_minmax();

        if ( this.border != null ) {

            // if an airport boundary has been defined, take it
            for (int n=0; n<this.border.nodes.size(); n++) {
                Node point = this.border.nodes.get(n);
                update_minmax( point.lat, point.lon);
            }

        } else {

            if ( ! this.pavements.isEmpty() ) {
                for (int i=0; i<this.pavements.size(); i++) {
                    Pavement taxiramp = this.pavements.get(i);
                    for (int j=0; j<taxiramp.nodes.size(); j++) {
                        Node point = taxiramp.nodes.get(j);
                        update_minmax( point.lat, point.lon);
                    }
                }
            }

//logger.warning("runways : "+ this.airport.runways.size());
            if ( this.airport != null ) {
                if ( ! this.airport.runways.isEmpty() ) {
                    for (int r=0; r<this.airport.runways.size(); r++) {
                        Runway rwy0 = this.airport.runways.get(r);
//logger.warning("rwy" + r + " " + rwy0.lat1 + "/" + rwy0.lon1 + " " + rwy0.lat2 + "/" + rwy0.lon2);
                        update_minmax( rwy0.lat1, rwy0.lon1);
                        update_minmax( rwy0.lat2, rwy0.lon2);
                    }
                }
            }

        }

//logger.warning("Chart north="+this.north_lat);
//logger.warning("Chart south="+this.south_lat);
//logger.warning("Chart east="+this.east_lon);
//logger.warning("Chart west="+this.west_lon);
        this.lon_scale = (float)Math.cos( Math.toRadians( ( this.north_lat + this.south_lat ) / 2.0f ) );

        this.ready = true;
//logger.warning("Chart scale="+this.lon_scale);

    }
    
    
}
