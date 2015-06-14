/**
* XfmcDisplay.java
* 
* The root awt component. NDComponent creates and manages painting all
* elements of the HSI. NDComponent also creates and updates NDGraphicsConfig
* which is used by all HSI elements to determine positions and sizes.
* 
* This component is notified when new data packets from the flightsimulator
* have been received and performs a repaint. This component is also triggered
* by UIHeartbeat to detect situations without reception.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
* Copyright (C) 2009  Marc Rogiers (marrog.123@gmail.com)
* Copyright (C) 2014  qwerty 
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
package net.sourceforge.xhsi.flightdeck.cdu;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.XfmcData;
import net.sourceforge.xhsi.model.xplane.XPlaneUDPSender;

public class CDUXfmc extends CDUSubcomponent {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger("net.sourceforge.xhsi");
	
	private BufferedImage image = null;
	private BufferedImage ap_img = null;
	private BufferedImage athr_img = null;
	private BufferedImage exec_img = null;
	private BufferedImage lnav_img = null;
	private BufferedImage vnav_img = null;
	
    double scalex = 1;
	double scaley = 1;
	List<ClickRegion> regions;
	boolean drawregions = false;
	Font font;

	int translate_x = 76;
	int translate_y = 49;
	double row_coef=19.8;
	int upper_y=2;
	double scratch_y_coef=13.0;
	double char_width_coef=1.6;
	
	int xfmc_keypath_code = 750;
	
	XfmcData xfmcData = null;
	XPlaneUDPSender udp_sender = null; 
	
    public CDUXfmc(ModelFactory model_factory, CDUGraphicsConfig cdu_gc, Component parent_component) {
        super(model_factory, cdu_gc, parent_component);
        
        try {
        	image = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_800x480.png"));
			ap_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_ap_litv_m.png"));
			athr_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_athr_litv_m.png"));
			exec_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_exec_litv_m.png"));
			lnav_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_lnav_litv_m.png"));
			vnav_img = ImageIO.read(this.getClass().getResourceAsStream("img/xfmc_2_vnav_litv_m.png"));
        	
        } catch (IOException ioe){}
        
        
        font = new Font("Lucida Console",1, 18);
        

        regions = new ArrayList<ClickRegion>();

		regions.add(new ClickRegion(new Point(8,62), new Point(49,300), 1, 6, 
				new int[][] {{0}, {1}, {2}, {3}, {4}, {5}} ));
		regions.add(new ClickRegion(new Point(431,62), new Point(474,300), 1, 6, 
				new int[][] {{6}, {7}, {8}, {9}, {10}, {11}} ));

		regions.add(new ClickRegion(new Point(194, 452), new Point(432, 774), 5, 6,
				new int[][] {
					{27, 28, 29, 30, 31},
					{32, 33, 34, 35, 36},
					{37, 38, 39, 40, 41},
					{42, 43, 44, 45, 46},
					{47, 48, 49, 50, 51},
					{52, -1, 54, 55, 56}} ));

		regions.add(new ClickRegion(new Point(52,558), new Point(190,770), 3, 4, 
				new int[][] {{57,58,59}, {60,61,62}, {63,64,65}, {66,67,68}} ));
		
		regions.add(new ClickRegion(new Point(52,350), new Point(436,450), 6, 2, 
				new int[][] {{12,13,14,15,16,-1}, {17,18,19,20,21,22}} ));

		regions.add(new ClickRegion(new Point(52,456), new Point(180,552), 2, 2, 
				new int[][] {{23,24}, {25,26}} ));
		

		xfmcData = XfmcData.getInstance();
		udp_sender = XPlaneUDPSender.get_instance();
		
		xfmcData.setLine(0, "1/1,60,XFMCFRAME");	
        
    }
    
	public void paint(Graphics2D g2) {
		drawPanel(g2);
	}
	
	private void drawPanel(Graphics2D g2) {

		scalex = (double)cdu_gc.panel_rect.width /image.getWidth();
		scaley = (double)cdu_gc.panel_rect.height/image.getHeight();

		AffineTransform orig = g2.getTransform();
        g2.scale(scalex, scaley);
        g2.translate(4, 4);
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawImage(image, null, 0, 0);
		
		
		int stat = 0;
		try{
			stat = Integer.parseInt(xfmcData.getLine(14));
		} catch (Exception e){}
		
		
		if((stat & 1) == 1) {
			g2.drawImage(ap_img,null,250, 354);
		}
		if((stat & 2) == 2) {
			g2.drawImage(lnav_img,null,25, 541);
		}
		if(!((stat & 4) == 4)) {
			g2.drawImage(vnav_img,null,25, 612);
		}
		if((stat & 8) == 8) {
			g2.drawImage(athr_img,null,435, 541);
		}
		if((stat & 32) == 32) {
			g2.drawImage(exec_img,null, 383, 397);
		}

		g2.setTransform(orig);

		//g2.setColor(Color.GREEN);
        g2.scale(scalex, scaley);
        g2.setFont(font);
        g2.translate(translate_x, translate_y);
        double dy = row_coef;

    	for(int i=0; i < 14; i++){
    		int x=i, xx = 0, yy = 0;
    		if(i==0) {
    			xx = 0;
    			yy = upper_y;
    		} else if ((i > 0) && (i < 13)){
    			x = (((i+1) / 2) * 2) - ((i % 2) == 1 ? 0 : 1);
    			yy = new Double(dy*(i)).intValue();
    		} else if(i == 13) { 
    			xx = 0;
    			yy = new Double(dy*scratch_y_coef).intValue();
    		}
    		
    		
    		List l = xfmcData.decodeLine(xfmcData.getLine(x));
    		for(Object o : l){
    			Object[] pts = (Object[]) o;
    			xx = new Double((Integer)pts[1]*char_width_coef).intValue();

    			g2.setColor(((Integer)pts[0]).intValue() == 0 ? Color.WHITE : new Color(176,176,252));
    			g2.drawString((String)pts[2], xx, yy);
    		}
    	}
		
		g2.setTransform(orig);
		
		if(drawregions) {
	    	double[] sc = new double[]{scalex, scaley};
	        for(ClickRegion r2 : regions){
	        	r2.draw(g2, sc);
	        }
		}
		
	}

    public void mousePressed(MouseEvent e) {
    	for(ClickRegion r : regions){
			int w = r.check(e.getPoint(), new double[]{scalex, scaley});
			if(w > -1) {
				udp_sender.sendDataPoint( xfmc_keypath_code, (float) w );
			}
		}
    }

	
}