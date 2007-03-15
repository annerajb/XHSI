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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import de.georg_gruetter.xhsi.model.Localizer;
import de.georg_gruetter.xhsi.model.ModelFactory;
import de.georg_gruetter.xhsi.model.NavigationObjectRepository;
import de.georg_gruetter.xhsi.model.NavigationRadio;
import de.georg_gruetter.xhsi.model.RadioNavigationObject;
import de.georg_gruetter.xhsi.model.VOR;

public class RadioLabel extends HSISubcomponent {
	
	NavigationRadio selected_nav_radio1;
	NavigationRadio selected_nav_radio2;
	RadioBoxInfo radio1_box_info;
	RadioBoxInfo radio2_box_info;
	
	public static DecimalFormat nav_freq_formatter;
	public static DecimalFormat adf_freq_formatter;
	public static DecimalFormat dme_formatter;
	
	int radio_label_y;
	int dme_text_width;
	

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
		
		public RadioBoxInfo(NavigationRadio radio) {
			
			RadioNavigationObject rnav_object;
			draw_arrow = true;
			
			if (radio != null) {
				rnav_object = radio.get_radio_nav_object();
				
				if (radio.receiving() == false) {
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
					
					if (radio.get_distance() != 0) {
						this.dme = RadioLabel.dme_formatter.format(radio.get_distance());
					} else {
						this.dme = "---";
					}
				}
			} else {
				this.type = "";
				this.id = "";
				this.dme = "";
			}
		}
	}
	
	public RadioLabel(ModelFactory model_factory, HSIGraphicsConfig hsi_gc) {
		super(model_factory, hsi_gc);
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
		dme_text_width = hsi_gc.get_text_width(g2, hsi_gc.font_small, "DME");
		radio_label_y = hsi_gc.panel_size.height - (3 * line_height) - hsi_gc.status_bar_height - hsi_gc.border_bottom;

		// get currently tuned in radios
		this.selected_nav_radio1 = this.avionics.get_selected_radio1();
		this.selected_nav_radio2 = this.avionics.get_selected_radio2();
		
		if (this.selected_nav_radio1 != null) {
			this.radio1_box_info = new RadioBoxInfo(this.selected_nav_radio1);
			draw_radio1_box_info(g2, line_height, this.radio1_box_info);
		}
		
		if (this.selected_nav_radio2 != null) {
			this.radio2_box_info = new RadioBoxInfo(this.selected_nav_radio2);
			draw_radio2_box_info(g2, line_height, this.radio2_box_info);
		}
	}

	private void draw_radio1_box_info(Graphics2D g2, int line_height, RadioBoxInfo radio_box_info) {
		g2.setFont(hsi_gc.font_medium);
		g2.setColor(radio_box_info.color);
		g2.clearRect(0, radio_label_y, 100, hsi_gc.panel_size.height);
		g2.drawString(radio_box_info.type, hsi_gc.border_left, radio_label_y + line_height);
	    g2.drawString(radio_box_info.id, hsi_gc.border_left, radio_label_y + line_height * 2);
	    if (radio_box_info.dme.equals("") == false) {
	    	g2.drawString(radio_box_info.dme, dme_text_width + hsi_gc.border_left, radio_label_y + line_height * 3);
	    	g2.setFont(hsi_gc.font_small);
	    	g2.drawString("DME", hsi_gc.border_left, radio_label_y+ line_height * 3);
	    }	    
	    if (radio_box_info.draw_arrow)
	    	RadioHeadingArrowsHelper.draw_nav1_forward_arrow(g2, 90, radio_label_y + line_height - 10, 25,10);	
	}
	
	private void draw_radio2_box_info(Graphics2D g2, int line_height, RadioBoxInfo radio_box_info) {
		g2.setFont(hsi_gc.font_medium);
		g2.setColor(radio_box_info.color);
		g2.clearRect(hsi_gc.panel_size.width - 100, radio_label_y, hsi_gc.panel_size.width, hsi_gc.panel_size.height);
	    g2.drawString(radio_box_info.type, hsi_gc.panel_size.width - hsi_gc.border_right - hsi_gc.get_text_width(g2,g2.getFont(), radio_box_info.type), radio_label_y + line_height);
	    g2.drawString(radio_box_info.id, hsi_gc.panel_size.width - hsi_gc.border_right - hsi_gc.get_text_width(g2,g2.getFont(), radio_box_info.id), radio_label_y + line_height * 2);
	    if (radio_box_info.dme.equals("") == false) {
	    	int distance_text_width = hsi_gc.get_text_width(g2,hsi_gc.font_medium, radio_box_info.dme);
	    	g2.drawString(radio_box_info.dme, hsi_gc.panel_size.width - hsi_gc.border_right - distance_text_width, radio_label_y+line_height * 3);
	    	g2.setFont(hsi_gc.font_small);
	    	g2.drawString("DME", hsi_gc.panel_size.width - 11 - (hsi_gc.max_char_advance_medium*5) - dme_text_width , radio_label_y+line_height * 3);
	    }	    
	    if (radio_box_info.draw_arrow)
	    	RadioHeadingArrowsHelper.draw_nav2_forward_arrow(g2, hsi_gc.panel_size.width - 90, radio_label_y + line_height - 10, 25,10);
	}		
}
