package edu.hawaii.ics321f13.view.impl;

import javax.swing.JDialog;

import edu.hawaii.ics321f13.model.impl.DefaultLoginInfo;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;
import edu.hawaii.ics321f13.model.interfaces.LoginPrompt;

import java.awt.Font;

import javax.swing.JPasswordField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Semaphore;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class LoginDialog extends JDialog implements LoginPrompt {
	
	/** Serialization support */
	private static final long serialVersionUID = 1L;
	// Synchronization constants.
	private final Semaphore LOCK = new Semaphore(1, true);
	// UI components. 
	private JPasswordField passwordField;
	private JTextField usernameField;
	
	
	/**
	 * Creates the login Gui
	 * Asks for the username and password to login to the 
	 * Constructor for the <code>LoginDialog</code>
	 */
	public LoginDialog() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		LOCK.acquireUninterruptibly(); // Acquire here to prevent other threads from acquiring after construction.
		setTitle("Login");
		
		//the username Title
		JLabel lblUsernameTitle = new JLabel("Username:");
		lblUsernameTitle.setForeground(SystemColor.activeCaption);
		lblUsernameTitle.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		
		// the userNameField where the user enters the username
		usernameField = new JTextField();
		usernameField.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		usernameField.setColumns(10);
		
		JLabel lblPasswordTitle = new JLabel("Password:");
		lblPasswordTitle.setForeground(SystemColor.activeCaption);
		lblPasswordTitle.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		
		passwordField = new JPasswordField();
		
		JButton button = new JButton("\u2794");
		button.setActionCommand("Enter");
		button.setContentAreaFilled(false);
		button.setBorder(null);
		button.setBackground(Color.WHITE);
		button.setForeground(Color.LIGHT_GRAY);
		button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 75));
		
		JLabel lblNewLabel = new JLabel("Enter database login credentals:");
		lblNewLabel.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPasswordTitle, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblUsernameTitle, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(usernameField, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
								.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGap(10)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lblUsernameTitle, 0, 0, Short.MAX_VALUE)
								.addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
							.addGap(19)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPasswordTitle)
								.addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(button)
							.addPreferredGap(ComponentPlacement.RELATED, 1, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Handle window closing.
				// I don't have time to finish writing this. I have to study for my midterm but we need to do two things here:
				// 1) Indicate that the window was closed and no credentials were entered.
				// 2) Release the lock. 
			}
			
		});
		
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				LOCK.release();
			}
		});
	}
	
	/**
	 * Accessor that returns the <code>LoginInfo</code>
	 * Sends the data from the over to the model which will enable us to access the data stored in the sql database
	 * @return <code>LoginInfo</code>
	 */
	@Override
	public LoginInfo getLoginInfo() {
		if(isVisible()) {
			// Ignore interrupted exceptions because it is unacceptable for this method to return spuriously(randomly).
			// The contract of this method is that it will only return after the user has clicked "OK", otherwise
			// the thread is disabled for thread-scheduling purposes.
			LOCK.acquireUninterruptibly();
			LOCK.release(); // Unlock the lock to allow other threads to retreive login info.
		}
		return new DefaultLoginInfo(usernameField.getText(), passwordField.getPassword());
	}
}
