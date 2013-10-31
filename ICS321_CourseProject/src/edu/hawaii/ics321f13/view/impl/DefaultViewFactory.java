package edu.hawaii.ics321f13.view.impl;

import java.io.IOException;

import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.LoginPrompt;
import edu.hawaii.ics321f13.view.interfaces.View;
import edu.hawaii.ics321f13.view.interfaces.ViewFactory;

public class DefaultViewFactory implements ViewFactory {

	@Override
	public View createView() {
		DefaultView view = new DefaultView(new SynchronousImageLoader());
		view.setVisible(true);
		return view;
	}

	@Override
	public LoginPrompt createLoginPrompt() {
		// TODO Debug: placeholder dummy code.
		return new LoginPrompt() {

			@Override
			public LoginInfo getLoginInfo() {
				return new LoginInfo() {

					@Override
					public void close() throws IOException {
						// TODO Auto-generated method stub
						
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
