package net.sourceforge.xhsi.model;

public class CduLine {
	public int pos;
	public char font;
	public char color;
	public String text;		
	public CduLine( char font, char color, int pos, String text) {			
		this.font = font;
		this.color = color;
		this.pos = pos;
		this.text = text;
	}
}
