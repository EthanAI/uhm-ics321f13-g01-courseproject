/**
 * 
 */
package edu.hawaii.ics321f13.controller.impl;

import edu.hawaii.ics321f13.controller.interfaces.Controller;
import edu.hawaii.ics321f13.model.impl.DefaultDataModelFactory;
import edu.hawaii.ics321f13.model.interfaces.DataModelFactory;
import edu.hawaii.ics321f13.view.impl.DefaultViewFactory;
import edu.hawaii.ics321f13.view.interfaces.ViewFactory;

/**
 * This is the project Main-Class. Minimally functions to establish program "ready" state, after which control is
 * passed to appropriate <code>Controller</code> instance.
 * 
 * @see Controller
 * 
 * @author Kyle Twogood
 *
 */
public class ProjectMain {

	/**
	 * Program entry point. Serves as initializer and subsequently passes control to appropriate <code>Controller</code>
	 * instance.
	 * 
	 * @param args - command-line supplied argument list
	 */
	public static void main(String[] args) {
		init();
	}
	
	/**
	 * Initializes the application component factories and surrenders control to the <code>Controller</code>.
	 * The factory changes the data that is in the model and figures out the logic behind it
	 */
	private static void init() {
		ViewFactory defaultViewFactory = new DefaultViewFactory();
		DataModelFactory defaultDataModelFactory = new DefaultDataModelFactory();
		new DefaultController(defaultDataModelFactory, defaultViewFactory); // RAII.
	}

}
