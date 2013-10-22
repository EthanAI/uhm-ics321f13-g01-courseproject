package edu.hawaii.ics321f13.view.impl;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import javax.swing.JButton;
import edu.hawaii.ics321f13.model.interfaces.LoginPrompt;
import edu.hawaii.ics321f13.model.interfaces.LoginInfo;

public class LoginBox extends JFrame implements LoginPrompt{

	/** Serialization support. */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userName;

	/**
	 * Create the frame.
	 */
	public LoginBox() {
		setMinimumSize(new Dimension(400, 300));
		setTitle("Login");
		setFont(new Font("Segoe UI", Font.PLAIN, 12));
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 775, 740);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		JPanel LoginWindow = new JPanel();
		LoginWindow.setBackground(Color.WHITE);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(106)
					.addComponent(LoginWindow, GroupLayout.PREFERRED_SIZE, 547, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(106, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(58)
					.addComponent(LoginWindow, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(367, Short.MAX_VALUE))
		);
		
		userName = new JTextField();
		userName.setToolTipText("Enter the username");
		userName.setSelectionColor(Color.LIGHT_GRAY);
		userName.setSelectedTextColor(Color.WHITE);
		userName.setForeground(Color.LIGHT_GRAY);
		userName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchFieldStateChanged(true);
				userName.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				searchFieldStateChanged(false);
			}
		});
		userName.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				searchFieldStateChanged(true);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				searchFieldStateChanged(false);
			}
		});
		
		userName.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), 
				BorderFactory.createEmptyBorder(0, 10, 0, 10)));
		userName.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		userName.setText("Username");
		userName.setColumns(10);
		
		JLabel Login = new JLabel("Login");
		Login.setFocusable(true);
		Login.setForeground(SystemColor.activeCaption);
		Login.setFont(new Font("Segoe UI Light", Font.PLAIN, 50));
		Login.setHorizontalAlignment(SwingConstants.CENTER);
		
		password = new JPasswordField();
		password.setColumns(10);
		password.setText("Password");
		password.setToolTipText("Enter the password");
		password.setForeground(Color.LIGHT_GRAY);
		
		JButton EnterLogin = new JButton("\u2794");
		EnterLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("hurray: " + userName +" "+ password);
			}
		});
		EnterLogin.setContentAreaFilled(false);
		EnterLogin.setBorderPainted(false);
		EnterLogin.setBorder(null);
		EnterLogin.setForeground(Color.LIGHT_GRAY);
		EnterLogin.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 70));
		GroupLayout gl_LoginWindow = new GroupLayout(LoginWindow);
		gl_LoginWindow.setHorizontalGroup(
			gl_LoginWindow.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_LoginWindow.createSequentialGroup()
					.addGap(143)
					.addComponent(Login, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
					.addGap(143))
				.addGroup(gl_LoginWindow.createSequentialGroup()
					.addGap(76)
					.addGroup(gl_LoginWindow.createParallelGroup(Alignment.LEADING, false)
						.addComponent(userName, GroupLayout.PREFERRED_SIZE, 394, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_LoginWindow.createSequentialGroup()
							.addComponent(password, GroupLayout.PREFERRED_SIZE, 313, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(EnterLogin, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)))
					.addGap(72))
		);
		gl_LoginWindow.setVerticalGroup(
			gl_LoginWindow.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_LoginWindow.createSequentialGroup()
					.addContainerGap()
					.addComponent(Login, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(userName, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addGap(14)
					.addGroup(gl_LoginWindow.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(EnterLogin, 0, 0, Short.MAX_VALUE)
						.addComponent(password, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
					.addGap(342))
		);
		LoginWindow.setLayout(gl_LoginWindow);
		contentPane.setLayout(gl_contentPane);
	}

	
	private String lastSearchFieldText = "";
	private boolean currentState = false;
	private JPasswordField password;
	private void searchFieldStateChanged(boolean active) {
		if(currentState != active && active) {
			currentState = active;
			userName.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			userName.setText(lastSearchFieldText);
		} else if(currentState != active && !active && userName.getMousePosition() == null && !userName.hasFocus()) {
			currentState = active;
			userName.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY), 
					BorderFactory.createEmptyBorder(0, 10, 0, 10)));
			lastSearchFieldText = userName.getText();
			//Username.setText("  Search images...");
		}
	}
	@Override
	public LoginInfo getLoginInfo() {
		// TODO Auto-generated method stub
		
		return (LoginInfo) userName;
	}
}
