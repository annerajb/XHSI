/**
* RadioLabel.java
* 
* Renders the information about tuned navigation radios in the bottom left
* and right corners.
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
package net.sourceforge.xhsi.flightdeck.nd;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.sourceforge.xhsi.model.Avionics;
import net.sourceforge.xhsi.model.Localizer;
import net.sourceforge.xhsi.model.ModelFactory;
import net.sourceforge.xhsi.model.NavigationObjectRepository;
import net.sourceforge.xhsi.model.NavigationRadio;
import net.sourceforge.xhsi.model.RadioNavigationObject;
import net.sourceforge.xhsi.model.RadioNavBeacon;

//import net.sourceforge.xhsi.panel.GraphicsConfig;
//import net.sourceforge.xhsi.panel.Subcomponent;



public class RadioLabel extends NDSubcomponent {

    private static final long serialVersionUID = 1L;
    
    public static boolean USE_BUFFERED_IMAGE = true;

    
    private long left_refreshed_timestamp;
    private long right_refreshed_timestamp;
    
    private Graphics2D g2_left;
    private Graphics2D g2_right;
    
    BufferedImage left_radio_box_buf_image;
    BufferedImage right_radio_box_buf_image;

    NavigationRadio selected_radio1;
    NavigationRadio selected_radio2;
    RadioBoxInfo radio1_box_info;
    RadioBoxInfo radio2_box_info;

    public static DecimalFormat nav_freq_formatter;
    public static DecimalFormat adf_freq_formatter;
    public static DecimalFormat near_dme_formatter;
    public static DecimalFormat far_dme_formatter;
    public static DecimalFormat int_dme_formatter;
    public static DecimalFormat dec_dme_formatter;
    public static DecimalFormat degrees_formatter;

    NavigationObjectRepository nor;

    AffineTransform original_at;

    private class RadioBoxInfo {
        public String type;
        public String id;
        public String dme;
        public String dme_int; // Integer part of DME distance
        public String dme_dec; // Decimal part of DME distance (1 digit)
        public int radial;
        public int obs;
        public String radial_text;
        public String obs_text;
        public Color color;
        public Color unit_color;
        public Color value_color;
        public boolean draw_arrow;
        public float frequency;
        public RadioNavigationObject rnav_object;
        public Localizer loc_object;
        public boolean receiving;
        public boolean is_adf;
        public int distance_by_10;        // compare only the first fraction point

        public RadioBoxInfo(NavigationRadio radio) {

            draw_arrow = true;

            if (radio != null) {
                this.rnav_object = radio.get_radio_nav_object();
                this.frequency = radio.get_frequency();
                this.distance_by_10 = 0;

                this.is_adf = radio.freq_is_adf();

                this.receiving = radio.receiving();
                this.radial_text = "";
                this.obs = Math.round( (radio.get_bank()==1) ? radio.avionics.nav1_obs() : radio.avionics.nav2_obs() );
                
                this.obs_text = "CRS " + degrees_formatter.format( this.obs );

                // defaults, display the labels dimmed
                this.color = nd_gc.no_rcv_vor_color;
                if (radio.freq_is_nav()) {
                    this.type ="NAV" + nd_gc.rib_spacing + radio.get_bank();
                    this.id = RadioLabel.nav_freq_formatter.format(radio.get_frequency());
                    // even if we don't receive the VOR or LOC or ILS, maybe we receive a DME
                    float dme_distance = radio.get_distance();
                    if ( dme_distance == 0.0f ) {
                        this.dme = "---";
                        this.dme_int = "---";
                        this.dme_dec = "-";
                    } else {
                        if (dme_distance > 99.4f) {
                            this.dme = RadioLabel.far_dme_formatter.format(dme_distance);
                            this.dme_int = RadioLabel.int_dme_formatter.format(dme_distance);
                            this.dme_dec = "";
                        } else {
                            this.dme = RadioLabel.near_dme_formatter.format(dme_distance);
                            this.dme_int = RadioLabel.int_dme_formatter.format(dme_distance);
                            this.dme_dec = RadioLabel.dec_dme_formatter.format((dme_distance-(int)dme_distance)*10);
                        }
                    }
                } else /* if (radio.freq_is_adf()) */ {
                    this.obs_text = "";
                    this.color = nd_gc.no_rcv_ndb_color;
                    this.type ="ADF" + nd_gc.rib_spacing  + radio.get_bank();
                    this.id = RadioLabel.adf_freq_formatter.format(radio.get_frequency());
                    this.dme = "";
                }
                    
                if ( receiving ) {
                    
                    // we are receiving a signal; set the text of the label and its color
                    if (rnav_object instanceof RadioNavBeacon) {
                        
                        if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_NDB) {
                            this.obs_text = "";
                            this.type = "ADF" + nd_gc.rib_spacing + radio.get_bank();
                            this.color = nd_gc.tuned_ndb_color;
                        } else if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_VOR) {
                            this.type = "VOR" + nd_gc.rib_spacing  + radio.get_bank();
                            this.color = nd_gc.tuned_vor_color;
                            this.radial = Math.round(radio.get_radial());
                            this.radial_text = "R " + RadioLabel.degrees_formatter.format( this.radial );
                        } else if (((RadioNavBeacon) rnav_object).type == RadioNavBeacon.TYPE_STANDALONE_DME) {
                            this.type = "DME" + nd_gc.rib_spacing + radio.get_bank();
                            this.color = nd_gc.tuned_vor_color;
                            this.draw_arrow = false;
                        } else {
                            // unexpected RadioNavBeacon type
                            this.type = "(" + ((RadioNavBeacon) rnav_object).type + ")  " + radio.get_bank();
                            this.color = Color.RED;
                        }
                        this.id = rnav_object.ilt;
                        
                    } else if (rnav_object instanceof Localizer) {
                        
                        loc_object = (Localizer) rnav_object;
                        if ( loc_object.has_gs )
                                this.type = "ILS" + nd_gc.rib_spacing + radio.get_bank();
                        else
                                this.type = "LOC" + nd_gc.rib_spacing + radio.get_bank();
                        this.color = nd_gc.tuned_localizer_color;
                        this.id = rnav_object.ilt;
                        this.draw_arrow = false;
                        
                    } else {
                        
                        // we are receiving something, but it is not a RadioNavBeacon, and it is not a Localizer
                        this.type = (this.is_adf ? "ADF" + nd_gc.rib_spacing  : "NAV"+ nd_gc.rib_spacing ) + radio.get_bank();
                        this.id = radio.get_nav_id();
                        this.color = nd_gc.unknown_nav_color;
                        
                    }

                    if ( this.is_adf ) {
                        this.dme = "";
                    } else {
                        float dme_distance = radio.get_distance();
                        if (dme_distance != 0.0f) {
                            if (dme_distance > 99.4f) {
                                this.dme = RadioLabel.far_dme_formatter.format(dme_distance);
                            } else {
                                this.dme = RadioLabel.near_dme_formatter.format(dme_distance);
                            }
                            this.distance_by_10 = (int) (dme_distance * 10);
                        } else {
                            this.dme = "---";
                            this.distance_by_10 = 0;
                        }
                    }
                    
                }
                this.value_color = nd_gc.boeing_style ? this.color : nd_gc.ecam_normal_color;
                this.unit_color = nd_gc.boeing_style ? this.color : nd_gc.ecam_action_color;
                
            } else {
                
                // totally abnormal...
                this.type = "";
                this.id = "";
                this.dme = "";
                this.rnav_object = null;
                this.frequency = 0;
                this.receiving = false;
                
            }
        }

    }


    public RadioLabel(ModelFactory model_factory, NDGraphicsConfig hsi_gc, Component parent_component) {
        super(model_factory, hsi_gc, parent_component);
        this.nor = NavigationObjectRepository.get_instance();

        RadioLabel.nav_freq_formatter = new DecimalFormat("000.00");
        RadioLabel.adf_freq_formatter = new DecimalFormat("0000");
        RadioLabel.near_dme_formatter = new DecimalFormat("0.0");
        RadioLabel.far_dme_formatter = new DecimalFormat("0");
        RadioLabel.int_dme_formatter = new DecimalFormat("000");
        RadioLabel.dec_dme_formatter = new DecimalFormat("0");
        DecimalFormatSymbols symbols = nav_freq_formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        RadioLabel.nav_freq_formatter.setDecimalFormatSymbols(symbols);
        RadioLabel.near_dme_formatter.setDecimalFormatSymbols(symbols);
        RadioLabel.far_dme_formatter.setDecimalFormatSymbols(symbols);
        RadioLabel.degrees_formatter = new DecimalFormat("000");
        left_refreshed_timestamp = 0;
        right_refreshed_timestamp = 0;
    }


    public void paint(Graphics2D g2) {

        if ( nd_gc.powered ) {

            // get currently tuned in radios
            this.selected_radio1 = this.avionics.get_selected_radio(1);
            this.selected_radio2 = this.avionics.get_selected_radio(2);
          
            if (this.selected_radio1 != null) {
                this.radio1_box_info = new RadioBoxInfo(this.selected_radio1);
                boolean refresh_radio1 = true;
            	if (USE_BUFFERED_IMAGE) {
            		g2_left = nd_gc.left_radio_box_img.createGraphics();
            		g2_left.setRenderingHints(nd_gc.rendering_hints);
            		g2_left.setStroke(nd_gc.radio_arrow_stroke);
            		if (refresh_radio1) {
            			left_refreshed_timestamp = nd_gc.current_time_millis;
            			// Clear the buffered Image first
            			g2_left.setComposite(AlphaComposite.Clear);
            			g2_left.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
            			g2_left.setComposite(AlphaComposite.SrcOver);
                		draw_radio_box_info(g2_left, this.radio1_box_info, nd_gc.radio1_text_x, 1, nd_gc.radio1_arrow_x);
                		// gImg.dispose();
                		g2.drawImage(nd_gc.left_radio_box_img, nd_gc.radio1_box_x, nd_gc.radio_box_y, null);
            		}
            	} else {
                    // left_radio_box_img
            		this.left_radio_box_buf_image = create_buffered_image(nd_gc.rib_width, nd_gc.rib_height);
            		Graphics2D gImg = get_graphics(this.left_radio_box_buf_image);
            		draw_radio_box_info(gImg, this.radio1_box_info, nd_gc.radio1_text_x, 1, nd_gc.radio1_arrow_x);
            		gImg.dispose();
            		g2.drawImage(this.left_radio_box_buf_image, nd_gc.radio1_box_x, nd_gc.radio_box_y, null);
            	}
            }

            if (this.selected_radio2 != null) {
                this.radio2_box_info = new RadioBoxInfo(this.selected_radio2);
                boolean refresh_radio2 = true;
            	if (USE_BUFFERED_IMAGE) {
            		g2_right = nd_gc.right_radio_box_img.createGraphics();
            		g2_right.setRenderingHints(nd_gc.rendering_hints);
            		g2_right.setStroke(nd_gc.radio_arrow_stroke);
            		if (refresh_radio2) {
            			right_refreshed_timestamp = nd_gc.current_time_millis;
            			// Clear the buffered Image first
            			g2_right.setComposite(AlphaComposite.Clear);
            			g2_right.fillRect(0, 0, nd_gc.frame_size.width, nd_gc.frame_size.height);
            			g2_right.setComposite(AlphaComposite.SrcOver);
                		draw_radio_box_info(g2_right, this.radio2_box_info, nd_gc.radio2_text_x, 2, nd_gc.radio2_arrow_x);
                		// gImg.dispose();
                		g2.drawImage(nd_gc.right_radio_box_img, nd_gc.radio2_box_x, nd_gc.radio_box_y, null);
            		}
            	} else {
            		this.right_radio_box_buf_image = create_buffered_image(nd_gc.rib_width, nd_gc.rib_height);
            		Graphics2D gImg = get_graphics(this.right_radio_box_buf_image);
            		draw_radio_box_info(gImg, this.radio2_box_info, nd_gc.radio2_text_x, 2, nd_gc.radio2_arrow_x);
            		gImg.dispose();
            		g2.drawImage(this.right_radio_box_buf_image, nd_gc.radio2_box_x, nd_gc.radio_box_y, null);
            	}
            }

        }

    }


    private void draw_radio_box_info(Graphics2D g2, RadioBoxInfo radio_box_info, int text_x, int arrow_type, int arrow_x) {

        g2.setColor(radio_box_info.color);
        g2.setBackground(nd_gc.background_color);
        g2.clearRect(0, 0, nd_gc.rib_width, nd_gc.rib_height);
        g2.setFont(nd_gc.boeing_style ? nd_gc.font_l : nd_gc.font_xxl);
        g2.drawString(radio_box_info.type, text_x, nd_gc.rib_line_1);
        g2.drawString(radio_box_info.id, text_x, nd_gc.rib_line_2);
    
        if ( ! radio_box_info.dme.equals("") ) {
        	if (nd_gc.boeing_style) {
        		g2.setColor(radio_box_info.value_color);
        		g2.setFont(nd_gc.font_s);
        		g2.drawString(radio_box_info.dme, nd_gc.rib_dme_text_width + text_x, nd_gc.rib_line_3);
        		g2.setColor(radio_box_info.color);
        		g2.setFont(nd_gc.font_xs);
        		g2.drawString("DME", text_x, nd_gc.rib_line_3);
        	} else {
        		g2.setColor(radio_box_info.value_color);
        		g2.setFont(nd_gc.font_xxl);
        		g2.drawString(radio_box_info.dme, text_x, nd_gc.rib_line_3);
        		g2.setColor(nd_gc.pfd_selected_color);
        		g2.setFont(nd_gc.font_l);
        		g2.drawString("NM", text_x + nd_gc.rib_dme_text_width, nd_gc.rib_line_3);
        	}
        }
        
        if (nd_gc.boeing_style) {
        	g2.setFont(nd_gc.font_xs);
        	g2.setColor(radio_box_info.color);
        	if ( this.avionics.efis_shows_pos() )
        		g2.drawString(radio_box_info.radial_text, text_x, nd_gc.rib_line_4);
        	else
        		g2.drawString(radio_box_info.obs_text, text_x, nd_gc.rib_line_4);
        }
        if ( ! nd_gc.mode_plan && ( ! avionics.efis_shows_pos() || ( nd_gc.mode_classic_hsi ) ) ) {
            Stroke original_stroke = g2.getStroke();
            g2.setStroke(nd_gc.radio_arrow_stroke);
            g2.setColor(radio_box_info.color);
            int arrow_t = nd_gc.line_height_l/2;
            int arrow_l = nd_gc.line_height_l*2;
            int arrow_w = arrow_l*10/25;
            if ( radio_box_info.draw_arrow && arrow_type==1 && nd_gc.boeing_style)
                RadioHeadingArrowsHelper.draw_nav1_forward_arrow(g2, arrow_x, arrow_t, arrow_l, arrow_w);
            if ( radio_box_info.draw_arrow && arrow_type==2 && nd_gc.boeing_style)
                RadioHeadingArrowsHelper.draw_nav2_forward_arrow(g2, arrow_x, arrow_t, arrow_l, arrow_w);
            if ( radio_box_info.draw_arrow && arrow_type==1 && nd_gc.airbus_style && radio_box_info.receiving)
                RadioHeadingArrowsHelper.draw_nav1_airbus_forward_arrow(g2, arrow_x, arrow_t, arrow_l, arrow_w, selected_radio1.freq_is_adf());
            if ( radio_box_info.draw_arrow && arrow_type==2 && nd_gc.airbus_style && radio_box_info.receiving)
                RadioHeadingArrowsHelper.draw_nav2_airbus_forward_arrow(g2, arrow_x, arrow_t, arrow_l, arrow_w, selected_radio2.freq_is_adf());

            g2.setStroke(original_stroke);
        }

    }

}