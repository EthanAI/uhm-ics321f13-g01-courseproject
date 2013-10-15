package edu.hawaii.ics321f13.view.interfaces;

import edu.hawaii.ics321f13.model.interfaces.LoginPrompt;

public interface ViewFactory {
	
	View createView();
	
	LoginPrompt createLoginPrompt();
	
}
