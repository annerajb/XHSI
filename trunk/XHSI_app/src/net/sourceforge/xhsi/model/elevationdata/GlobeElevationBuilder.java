/**
* GlobeElevationBuilder.java
*
* Reads Globe elevation database and maps each file in memory
*
* Copyright (C) 2017 Nicolas Carel
* 
* Globe Manual:
* https://ngdc.noaa.gov/mgg/topo/report/globedocumentationmanual.pdf
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
package net.sourceforge.xhsi.model.elevationdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import net.sourceforge.xhsi.XHSIPreferences;
import net.sourceforge.xhsi.XHSIStatus;
import net.sourceforge.xhsi.PreferencesObserver;
import net.sourceforge.xhsi.ProgressObserver;
import net.sourceforge.xhsi.model.ElevationArea;
import net.sourceforge.xhsi.model.ElevationRepository;
import net.sourceforge.xhsi.model.NavigationObjectRepository;

public class GlobeElevationBuilder implements PreferencesObserver {
	
    private String globe_file[] = {
    		"/a10g", "/b10g", "/c10g", "/d10g",
    		"/e10g", "/f10g", "/g10g", "/h10g", 
    		"/i10g", "/j10g", "/k10g", "/l10g",
    		"/m10g", "/n10g", "/o10g", "/p10g" };
    
    private int globe_size[] = {
    		103680000, 103680000, 103680000, 103680000, 
    		129600000, 129600000, 129600000, 129600000, 
    		129600000, 129600000, 129600000, 129600000,
    		103680000, 103680000, 103680000, 103680000 };
    
    private int globe_rows[] = {
    		4800, 4800, 4800, 4800, 
    		6000, 6000, 6000, 6000, 
    		6000, 6000, 6000, 6000,
    		4800, 4800, 4800, 4800 };
    
    private float min_lat[] = {
    		50.0f, 50.0f, 50.0f, 50.0f, 
    		0.0f, 0.0f, 0.0f, 0.0f, 
    		-50.0f, -50.0f, -50.0f, -50.0f,
    		-90.0f, -90.0f, -90.0f, -90.0f };

    private float max_lat[] = {
    		90.0f, 90.0f, 90.0f, 90.0f, 
    		50.0f, 50.0f, 50.0f, 50.0f,
    		0.0f, 0.0f, 0.0f, 0.0f,
    		-50.0f, -50.0f, -50.0f, -50.0f };

    private float min_lon[] = {
    		-180.0f, -90.0f, 0.0f, 90.0f,
    		-180.0f, -90.0f, 0.0f, 90.0f,
    		-180.0f, -90.0f, 0.0f, 90.0f,
    		-180.0f, -90.0f, 0.0f, 90.0f };

    private float max_lon[] = {
    		-90.0f, 0.0f, 90.0f, 180.0f,
    		-90.0f, 0.0f, 90.0f, 180.0f,
    		-90.0f, 0.0f, 90.0f, 180.0f,
    		-90.0f, 0.0f, 90.0f, 180.0f };
    
    private int globe_columns = 10800;
    
    private String pathname_to_globe_db;
    
    private ProgressObserver progressObserver;
    
    private ElevationRepository elevation_repository;
    
    private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
    
    public void set_progress_observer(ProgressObserver observer) {
        this.progressObserver = observer;
    }

    public GlobeElevationBuilder(String pathname_to_globe_db) throws Exception {
        this.pathname_to_globe_db = pathname_to_globe_db;
        this.elevation_repository = ElevationRepository.get_instance();
        this.progressObserver = null;
    }
    
    public void preference_changed(String key) {

        logger.config("Preference "+key+" changed");
        if (key.equals(XHSIPreferences.PREF_EGPWS_DB_DIR)) {
            // reload navigation databases
            this.pathname_to_globe_db = XHSIPreferences.get_instance().get_preference(XHSIPreferences.PREF_EGPWS_DB_DIR);
            if (XHSIStatus.egpws_db_status.equals(XHSIStatus.STATUS_EGPWS_DB_NOT_FOUND) == false) {
                try {
                    logger.config("Reload GLOBE database");
                    map_database();
                } catch (Exception e) {
                    logger.warning("Could not read GLOBE files! (" + e.toString() + ")");
                }
            } else {
                logger.warning("Could not find GLOBE Resources! (Status:" + XHSIStatus.egpws_db_status + ")");
            }
        }

    }
    
    public void map_database() throws Exception {
    	

        if (new File(this.pathname_to_globe_db).exists()) {
            logger.info("Start mapping GLOBE database files in " + XHSIPreferences.PREF_EGPWS_DB_DIR);

            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Loading EGPWS elevation data ...", 0.0f);
            }
            for (int i=0; i<16; i++) {
                if (this.progressObserver != null) {
                    this.progressObserver.set_progress("Loading databases", "Mapping set #"+i, (i+1)*100.0f/18.0f);
                }
                logger.info("Mapping GLOBE database files " + pathname_to_globe_db + globe_file[i]);
                File file = new File(pathname_to_globe_db + globe_file[i]);
                logger.fine("Getting file channel " + globe_file[i]);
                FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
                logger.fine("Mapping byteBuffer " + globe_file[i]);
                MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileChannel.size());
                logger.fine("Creating area " + globe_file[i]);
                ElevationArea area = new ElevationArea(byteBuffer, globe_columns, globe_rows[i], min_lat[i],  max_lat[i],  min_lon[i],  max_lon[i], globe_file[i]);
                logger.fine("Add area " + globe_file[i]);
                elevation_repository.addElevationArea(area);
            }           
            if (this.progressObserver != null) {
                this.progressObserver.set_progress("Loading databases", "Done!", 100.0f);
            }
            elevation_repository.dumpAreas();
        } else {
            logger.warning("GLOBE resources directory is wrong!");
        }
    }
}
