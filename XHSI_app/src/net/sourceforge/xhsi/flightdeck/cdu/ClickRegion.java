package net.sourceforge.xhsi.flightdeck.cdu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;


public class ClickRegion {
	
	private Point p1;
	private Point p2;
	private int cols;
	private int rows;
	int [][] tab;
	
	
	public ClickRegion(Point ap1, Point ap2, int acols, int arows, int[][] atab) {
		p1 = ap1;
		p2 = ap2;
		cols = acols;
		rows = arows;
		tab = atab;
	}
	
        
	public int check(Point clickpoint, double sc_x, double sc_y, double border_x, double border_y) {
		
		double dx = (double)(p2.x - p1.x) * sc_x / (double)cols;
		double dy = (double)(p2.y - p1.y) * sc_y / (double)rows;
		//System.out.println("dx = " + dx + ", dy = " + dy);
		for (int y=0; y < rows; y++) {
			for (int x=0; x < cols; x++) {
				Rectangle2D.Double r2 = new Rectangle2D.Double(p1.x * sc_x + x * dx + border_x, p1.y * sc_y + y * dy + border_y, dx, dy);
				if ( r2.contains(clickpoint) ) {
					return tab[y][x];
				}
			}
		}
		return -1;
	}
	
        
	public void draw(Graphics g, double sc_x, double sc_y, double border_x, double border_y) {
		Graphics2D g2 = (Graphics2D) g;
		double dx = (double)(p2.x - p1.x) * sc_x / (double)cols;
		double dy = (double)(p2.y - p1.y) * sc_y / (double)rows;
		//System.out.println("dx = " + dx + ", dy = " + dy);
		for (int y=0; y < rows; y++) {
			for (int x=0; x < cols; x++) {
				Rectangle2D.Double r2 = new Rectangle2D.Double(p1.x * sc_x + x * dx + border_x, p1.y * sc_y + y * dy + border_y, dx, dy);
				g2.draw(r2);
			}
		}
	}

}