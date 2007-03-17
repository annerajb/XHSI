/**
* RadioLabel.java
* 
* Renders the information about tuned navigation radios in the bottom left
* and right corners.
* 
* Copyright (C) 2007  Georg Gruetter (gruetter@gmail.com)
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
package de.georg_gruetter.xhsi.panel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import de.georg_gruetter.xhsi.model.Localizer;
import de.georg_gruetter.xhsi.model.ModelFactory;
import de.georg_gruetter.xhsi.model.NavigationObjectRepository;
import de.georg_gruetter.xhsi.model.NavigationRadio;
import de.georg_gruetter.xhsi.model.RadioNavigationObject;
import de.georg_gruetter.xhsi.model.VOR;

public class RadioLabel extends HSISubcomponent {

	private static final long serialVersionUID = 1L;
	BufferedImage left_radio_box_buf_image;
	BufferedImage right_radio_box_buf_image;
	
	NavigationRadio selected_nav_radio1;
	NavigationRadio selected_nav_radio2;
	RadioBoxInfo radio1_box_info;
	RadioBoxInfo radio2_box_info;
	
	public static DecimalFormat nav_freq_formatter;
	public static DecimalFormat adf_freq_formatter;
	public static DecimalFormat dme_formatter;
	
	int radio_label_y;
	int dme_text_width = 0;

	VOR tuned_vor = null;
	Localizer tuned_localizer = null;
	NavigationObjectRepository nor;
	
	AffineTransform original_at;
	
	private class RadioBoxInfo {
		public String type;
		public String id;
		public String dme;
		public Color color;
		public boolean draw_arrow;
		public float frequency;
		public RadioNavigationObject rnav_object;
		public boolean receiving;
		public int distance_by_10;		// compare only the first fraction point
		
		public RadioBoxInfo(NavigationRadio radio) {
			
			draw_arrow = true;
			
			if (radio != null) {
				this.rnav_object = radio.get_radio_nav_object();
				this.frequency = radio.get_frequency();
				this.distance_by_10 = 0;
				
				if (radio.receiving() == false) {
					this.receiving = false;
					this.color = Color.DARK_GRAY;
					if (radio.freq_is_nav()) {
						this.type ="NAV" + radio.get_bank();
						this.id = RadioLabel.nav_freq_formatter.format(radio.get_frequency());
					} else if (radio.freq_is_adf()) {
						this.type ="ADF" + radio.get_bank();
						this.id = RadioLabel.adf_freq_formatter.format(radio.get_frequency());
					}
					this.dme = "---";					
				} else {
					this.receiving = true;
					if (rnav_object instanceof VOR) {
						if (((VOR) rnav_object).type == VOR.TYPE_NDB) {
							this.type = "ADF" + radio.get_bank();
							this.color = hsi_gc.color_lightblue;
						} else if (((VOR) rnav_object).type == VOR.TYPE_VOR) {
							this.type = "VOR" + radio.get_bank();
							this.color = hsi_gc.color_lightgreen;
						} else {
							this.type ="Err:Unknown VOR type";
							this.color = Color.RED;
						}
						this.id = rnav_object.ilt;						
					} else if (rnav_object instanceof Localizer) {
						this.type = "LOC" + radio.get_bank();
						this.color = Color.WHITE;
						this.id = rnav_object.ilt;
						this.draw_arrow = false;
						
					} else {
						this.type = "Err:Unknown rnav object";
						this.id = "";
						this.dme = "";
						this.color = Color.RED;
					}
					
					float dme_distance = radio.get_distance();
					if (dme_distance != 0) {
						this.dme = RadioLabel.dme_formatter.format(dme_distance);
						this.distance_by_10 = (int) (dme_distance * 10);
					} else {
						this.dme = "---";
						this.distance_by_10 = 0;
					}
				}
			} else {
				this.type = "";
				this.id = "";
				this.dme = "";
				this.rnav_object = null;
				this.frequency = 0;
				this.receiving = false;
			}
		}
		
		public boolean equals(NavigationRadio radio) {
			if (radio != null) {
				return ((this.rnav_object == radio.get_radio_nav_object()) &&
						(this.frequency == radio.get_frequency()) &&
						(this.receiving == radio.receiving()) &&
						(this.distance_by_10 == ((int) (radio.get_distance() * 10)))
						);
			} else {
				return ((this.rnav_object == null) &&
				        (this.frequency == 0.0f) &&
				        (this.receiving == false));
			}
		}
	}
	
	public RadioLabel(ModelFactory model_factory, HSIGraphicsConfig hsi_gc, Component parent_component) {
		super(model_factory, hsi_gc, parent_component);
		this.nor = NavigationObjectRepository.get_instance();
		
		RadioLabel.nav_freq_formatter = new DecimalFormat("000.00");
		RadioLabel.adf_freq_formatter = new DecimalFormat("000");
		RadioLabel.dme_formatter = new DecimalFormat("0.0");
		DecimalFormatSymbols symbols = nav_freq_formatter.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		RadioLabel.nav_freq_formatter.setDecimalFormatSymbols(symbols);
		RadioLabel.dme_formatter.setDecimalFormatSymbols(symbols);
	}
		
	public void paint(Graphics2D g2) {
		
		int line_height = hsi_gc.line_height_medium;	
		if (dme_text_width == 0)
			dme_text_width = hsi_gc.get_text_width(g2, hsi_gc.font_small, "DME");
		radio_label_y = hsi_gc.panel_size.height - (3 * line_height) - hsi_gc.status_bar_height - hsi_gc.border_bottom;

		// get currently tuned in radios
		this.selected_nav_radio1 = this.avionics.get_selected_radio1();
		this.selected_nav_radio2 = this.avionics.get_selected_radio2();
		
		if (this.selected_nav_radio1 != null) {
			if ((this.left_radio_box_buf_image == null) ||
			    (radio1_box_info == null) ||
				(radio1_box_info.equals(this.selected_nav_radio1) == false)) {
				this.radio1_box_info = new RadioBoxInfo(this.selected_nav_radio1);
				
	            GraphicsConfiguration gc = this.parent_component.getGraphicsConfiguration();
	            this.left_radio_box_buf_image = gc.createCompatibleImage(100, line_height*3, Transparency.BITMASK);
	            Graphics2D gImg = (Graphics2D)this.left_radio_box_buf_image.getGraphics();
	            gImg.setComposite(AlphaComposite.Src);
	            gImg.setColor(new Color(0, 0, 0, 0));
	            gImg.fillRect(0, 0, 100, line_height*3);
				draw_radio1_box_info(gImg, line_height, this.radio1_box_info);
				gImg.dispose();
			}
	        g2.drawImage(this.left_radio_box_buf_image, 0, radio_label_y, null);
		}
		
		if (this.selected_nav_radio2 != null) {
			if ((this.right_radio_box_buf_image == null) ||
				    (radio2_box_info == null) ||
					(radio2_box_info.equals(this.selected_nav_radio2) == false)) {
					this.radio2_box_info = new RadioBoxInfo(this.selected_nav_radio2);
					
		            GraphicsConfiguration gc = this.parent_component.getGraphicsConfiguration();
		            this.right_radio_box_buf_image = gc.createCompatibleImage(100, line_height*3, Transparency.BITMASK);
		            Graphics2D gImg = (Graphics2D)this.right_radio_box_buf_image.getGraphics();
		            gImg.setComposite(AlphaComposite.Src);
		            gImg.setColor(new Color(0, 0, 0, 0));
		            gImg.fillRect(0, 0, 100, line_height*3);
					draw_radio2_box_info(gImg, line_height, this.radio2_box_info);
					gImg.dispose();
				}
		        g2.drawImage(this.right_radio_box_buf_image, this.hsi_gc.panel_size.width - hsi_gc.border_right - 100, radio_label_y, null);
		}
	}

	private void draw_radio1_box_info(Graphics2D g2, int line_height, RadioBoxInfo radio_box_info) {
		g2.setFont(hsi_gc.font_medium);
		g2.setColor(radio_box_info.color);
		g2.clearRect(0, 0, 100, line_height * 3);
		g2.drawString(radio_box_info.type, hsi_gc.border_left, line_height);
	    g2.drawString(radio_box_info.id, hsi_gc.border_left, line_height * 2);
	    if (radio_box_info.dme.equals("") == false) {
	    	g2.drawString(radio_box_info.dme, dme_text_width + hsi_gc.border_left, line_height * 3);
	    	g2.setFont(hsi_gc.font_small);
	    	g2.drawString("DME", hsi_gc.border_left, line_height * 3);
	    }	    
	    if (radio_box_info.draw_arrow)
	    	RadioHeadingArrowsHelper.draw_nav1_forward_arrow(g2, 90, line_height - 10, 25,10);	
	}
	
	private void draw_radio2_box_info(Graphics2D g2, int line_height, RadioBoxInfo radio_box_info) {
		g2.setFont(hsi_gc.font_medium);
		g2.setColor(radio_box_info.color);
		g2.clearRect(0,0,100, line_height * 3);
	    g2.drawString(radio_box_info.type, 100 - hsi_gc.get_text_width(g2,g2.getFont(), radio_box_info.type), line_height);
	    g2.drawString(radio_box_info.id, 100 - hsi_gc.get_text_width(g2,g2.getFont(), radio_box_info.id), line_height * 2);
	    if (radio_box_info.dme.equals("") == false) {
	    	int distance_text_width = hsi_gc.get_text_width(g2,hsi_gc.font_medium, radio_box_info.dme);
	    	g2.drawString(radio_box_info.dme, 100 - distance_text_width, line_height * 3);
	    	g2.setFont(hsi_gc.font_small);
	    	g2.drawString("DME", 100 - (hsi_gc.max_char_advance_medium*5) - dme_text_width , line_height * 3);
	    }	    
	    if (radio_box_info.draw_arrow)
	    	RadioHeadingArrowsHelper.draw_nav2_forward_arrow(g2, 10, line_height - 10, 25,10);
	}		
}
