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
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JTextField;

public class LoginDialog extends JDialog implements LoginPrompt {
	
	/** Serialization support */
	private static final long serialVersionUID = 1L;
	// Synchronization constants.
	private final ReentrantLock LOCK = new ReentrantLock(true);
	// UI components. 
	private JPasswordField passwordField;
	private JTextField usernameField;
	
	
	/*
	 * Constructor for the <code>LoginDialog</code>
	 */
	public LoginDialog() {
		LOCK.lock(); // Acquire here to prevent other threads from acquiring after construction.
		setTitle("Login Dialog");
		getContentPane().setLayout(null);
		
		JLabel lblUsernameTitle = new JLabel("Username:");
		lblUsernameTitle.setForeground(SystemColor.activeCaption);
		lblUsernameTitle.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		lblUsernameTitle.setBounds(10, 67, 126, 34);
		getContentPane().add(lblUsernameTitle);
		
		usernameField = new JTextField();
		usernameField.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 11));
		usernameField.setBounds(139, 67, 285, 33);
		getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		JLabel lblPasswordTitle = new JLabel("Password:");
		lblPasswordTitle.setForeground(SystemColor.activeCaption);
		lblPasswordTitle.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		lblPasswordTitle.setBounds(10, 123, 126, 34);
		getContentPane().add(lblPasswordTitle);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(131, 129, 215, 27);
		getContentPane().add(passwordField);
		
		JButton button = new JButton("\u2794");
		button.setActionCommand("Enter");
		button.setContentAreaFilled(false);
		button.setBorder(null);
		button.setBackground(Color.WHITE);
		button.setForeground(Color.LIGHT_GRAY);
		button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
		button.setBounds(361, 119, 63, 44);
		getContentPane().add(button);
		
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
				LOCK.unlock();
			}
		});
		// Since the LoginPrompt is abstracted from any visual representation, the caller is freed from the 
		// responsibility of managing this view's visibility explicitly. That implies that the contract to which this
		// view must adhere is that it will manage its own visiblity in some logical manner that supports the
		// functionallity required by the LoginPrompt interface. 
		// So, in short, we need to make the dialog visible here and hide it when the user submits the credentials.
		setVisible(true);
	}
	
	/*
	 * Accessor that returns the <code>LoginInfo</code>
	 * @return <code>LoginInfo</code>
	 */
	@Override
	public LoginInfo getLoginInfo() {
		if(isVisible()) {
			// Ignore interrupted exceptions because it is unacceptable for this method to return spuriously(randomly).
			// The contract of this method is that it will only return after the user has clicked "OK", otherwise
			// the thread is disabled for thread-scheduling purposes.
			LOCK.lock();
			LOCK.unlock(); // Unlock the lock to allow other threads to retreive login info.
		}
		return new DefaultLoginInfo(usernameField.getText(), passwordField.getPassword());
	}
}
