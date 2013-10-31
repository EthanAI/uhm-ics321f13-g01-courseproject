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

import javax.swing.JButton;
import javax.swing.JTextField;

public class LoginDialog extends JDialog implements LoginPrompt {
	private char[] password;
	private String username;
	public LoginDialog() {
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
		username = usernameField.getText();
		
		JLabel lblPasswordTitle = new JLabel("Password:");
		lblPasswordTitle.setForeground(SystemColor.activeCaption);
		lblPasswordTitle.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		lblPasswordTitle.setBounds(10, 123, 126, 34);
		getContentPane().add(lblPasswordTitle);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(131, 129, 215, 27);
		getContentPane().add(passwordField);
		password = passwordField.getPassword();
		
		JButton button = new JButton("\u2794");
		button.setActionCommand("Enter");
		button.setContentAreaFilled(false);
		button.setBorder(null);
		button.setBackground(Color.WHITE);
		button.setForeground(Color.LIGHT_GRAY);
		button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
		button.setBounds(361, 119, 63, 44);
		getContentPane().add(button);
		
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getLoginInfo();
			}
		});
	}

	/** Serialization support */
	private static final long serialVersionUID = 1L;
	private JPasswordField passwordField;
	private JTextField usernameField;
	
	@Override
	//not quite sure if i did this right. I also don't know how i would test it sorry kyle
	public LoginInfo getLoginInfo() {
		DefaultLoginInfo log = new DefaultLoginInfo(username, password);
		return log;
	}
}
