package de.georg_gruetter.xhsi;

public interface ProgressObserver {
	
	void set_progress(String title, String task, float percent_complete);

}
