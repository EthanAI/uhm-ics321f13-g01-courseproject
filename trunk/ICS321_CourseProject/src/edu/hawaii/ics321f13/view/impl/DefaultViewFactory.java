package edu.hawaii.ics321f13.view.impl;

import java.io.IOException;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.LoginPrompt;
import edu.hawaii.ics321f13.view.interfaces.View;
import edu.hawaii.ics321f13.view.interfaces.ViewFactory;

public class DefaultViewFactory implements ViewFactory {

	/**
	 * Constructor uses the <code>SynchronousImageLoader</code> to generate the <code>DefaultView</code>
	 */
	@Override
	public View createView() {
		DefaultView view = new DefaultView(new SynchronousImageLoader());
		view.setVisible(true);
		return view;
	}
	
	/**
	 * Creates the <code>LoginPrompt</code>
	 * @return <code>LoginPrompt</code>
	 */
	@Override
	public LoginPrompt createLoginPrompt() {
		//return new LoginDialog(); // FIXME Totally broken under concurrency.
		// TODO Replace with LoginDialog.
		return new LoginPrompt() {

			@Override
			public LoginInfo getLoginInfo() {
				return new LoginInfo() {

					@Override
					public void close() throws IOException {
						// Do nothing.
					}

					@Override
					public String getUserName() {
						return "root"; //TODO currently hardcoded to root. Will work for now
					}

					@Override
					public char[] getPassword() {
						return new char[0];
					}
					
				};
			};
			
		};
	}

}
