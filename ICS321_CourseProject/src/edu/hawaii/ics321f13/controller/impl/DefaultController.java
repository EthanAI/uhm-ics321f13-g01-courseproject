package edu.hawaii.ics321f13.controller.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import edu.hawaii.ics321f13.controller.interfaces.Controller;
import edu.hawaii.ics321f13.model.interfaces.DataModelFactory;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.LoginPrompt;
import edu.hawaii.ics321f13.model.interfaces.ResultConstraint;
import edu.hawaii.ics321f13.model.interfaces.SearchableModel;
import edu.hawaii.ics321f13.view.impl.ViewEventType;
import edu.hawaii.ics321f13.view.interfaces.View;
import edu.hawaii.ics321f13.view.interfaces.ViewFactory;

/**
 * Provides a communication channel between the <code>Model</code> and the <code>View</code>. Updates to or from 
 * either component is moderated by this class.
 * 
 * @author Kyle Twogood
 *
 */
public class DefaultController implements Controller {
	
	// Default mysql port.
	private static final int DEFAULT_PORT = 3306;
	private static final int MAX_PORT = 65535;
	private static final int MIN_PORT = 1;
	// Factory objects.
	private final DataModelFactory MODEL_FACTORY;
	private final ViewFactory VIEW_FACTORY;
	// Program components.
	private final SearchableModel MODEL;			// Application data model.
	private final View VIEW;						// Main user-facing GUI.
	
	/**
	 * Creates a new <code>DefaultController</code> instance, prompting the user for the required login credentials.
	 * 
	 * @param modelFactory - the <code>DataModelFactory</code> instance which will be used to initialize a new 
	 * <code>SearchableModel</code> instance.
	 * @param viewFactory - the <code>ViewFactory</code> instance which will be used to instantiate all GUI objects.
	 */
	public DefaultController(DataModelFactory modelFactory,	ViewFactory viewFactory) {
		this(modelFactory, viewFactory, null, DEFAULT_PORT);
	}
	
	/**
	 * Creates a new <code>DefaultController</code> instance using the specified login credentials.
	 * 
	 * @param modelFactory - the <code>DataModelFactory</code> instance which will be used to initialize a new 
	 * <code>SearchableModel</code> instance.
	 * @param viewFactory - the <code>ViewFactory</code> instance which will be used to instantiate all GUI objects.
	 * @param login - the login credentials used to connect to the underlying <code>Database</code> instance.
	 * @param port - the port used to connect to the underlying <code>Database</code>
	 */
	public DefaultController(DataModelFactory modelFactory,	ViewFactory viewFactory, LoginInfo login, int port) {
		LoginInfo m_login = login;
		MODEL_FACTORY = Objects.requireNonNull(modelFactory);
		VIEW_FACTORY = Objects.requireNonNull(viewFactory);
		if(port < MIN_PORT || port > MAX_PORT) {
			throw new IllegalArgumentException("port number out of valid range (1 - 65535)");
		}
		if(m_login == null) {
			LoginPrompt loginPrompt = VIEW_FACTORY.createLoginPrompt();
			m_login = loginPrompt.getLoginInfo();
		}
		MODEL = MODEL_FACTORY.fromLogin(m_login, port);
		try {
			// Dispose of sensitive information.
			m_login.close();
		} catch (IOException e) {
			// Should never happen.
			e.printStackTrace();
		}
		VIEW = VIEW_FACTORY.createView();
		VIEW.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() == ViewEventType.QUERY.getID()) {
					onQuery(Objects.requireNonNull(e.getActionCommand()));
				} else if(e.getID() == ViewEventType.CLOSE.getID()) {
					onClose();
				} else {
					throw new UnsupportedOperationException(
							String.format("unsupported message type received from view: %s (event ID: $d)", 
									(e.getActionCommand() == null ? "no command specified" : e.getActionCommand()), 
									e.getID()));
				}
			}
			
		});
	}
	
	/*
	 * Searches the database held by the <code>Model</code> for images related to a topic. 
	 * 
	 * @param searchTerm is a string of the topic the user wants pictures of
	 */
	@Override
	public void onQuery(String searchTerm) {
		MODEL.search(searchTerm, BufferedImage.class, ResultConstraint.CONTAINS);
	}

	@Override
	public void onClose() {
		try {
			MODEL.close();
		} catch (IOException e) {
			// If the attempt to manually close the connection to the database fails, the connection will
			// be closed automatically.
			e.printStackTrace();
		}
	}

}
