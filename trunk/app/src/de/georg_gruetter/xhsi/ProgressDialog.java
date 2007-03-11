/**
 * ProgressDialog.java
 * 
 * Displays a progress bar if progress <> 100%. Is notified by classes through
 * the ProgressObserver interface.
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
 */package de.georg_gruetter.xhsi;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class ProgressDialog extends JFrame implements ProgressObserver {

	private static final long serialVersionUID = 1L;
	private JProgressBar progress_bar;
	private Graphics g;
	
	public ProgressDialog(Component parent_component) {			
		super("No Title");
		
		this.g = null;
		
		this.progress_bar = new JProgressBar(0,100);
		this.progress_bar.setStringPainted(true);
		this.progress_bar.setPreferredSize(new Dimension(300,30));
		this.progress_bar.setIndeterminate(true);
		
		Container content_pane = this.getContentPane();
		content_pane.setLayout(new FlowLayout(FlowLayout.CENTER,5,10));
		content_pane.add(this.progress_bar);
		
		Rectangle parent_bounds = parent_component.getBounds();
		setLocation(
				parent_bounds.x + ((parent_bounds.width - 300) / 2),
				parent_bounds.y + ((parent_bounds.height - 30) /2));
		pack();
	}

	public void set_progress(String title, String task, float percent_complete) {
		if ((percent_complete != 100.0f) && (isVisible() == false)) {
			setVisible(true);
		} 

		this.setTitle(title);
		this.progress_bar.setValue((int) percent_complete);
		this.progress_bar.setString(task);
		if (this.g != null) 
			this.progress_bar.paint(this.g);
		
		if (percent_complete == 100.0f) {
			setVisible(false);
		}
	}
	
	public void paintAll(Graphics g) {
		this.g = g;
		super.paintAll(g);
	}
	
}
