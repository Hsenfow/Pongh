package hsenfow.pongh;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hsenfow.pongh.Network.NetworkUtils;

public class MultiplayerPanel extends JPanel{
	private static final long serialVersionUID = -1353818935066361277L;
	
	public MultiplayerPanel(){
		setBackground(Color.DARK_GRAY);
		
		// Create the 'Multiplayer' title label
		JLabel titleLabel = new JLabel("Multipongh");
		titleLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 100));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Create the JPanel for the 'connect' fields
		JPanel addressFieldPanel = new JPanel();
		addressFieldPanel.setOpaque(false);
		
		// Create the server address field
		JTextField addressTextField = new JTextField();
		addressTextField.setPreferredSize(new Dimension(130, 30));
		// Create the 'Connect' button
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				// Split the content of the address text field
				String[] serverAddress = addressTextField.getText().split(":");
				// Ensure the server address array is the right length
				if(serverAddress.length != 2){
					JOptionPane.showMessageDialog(Utils.mainFrame, "Invalid server address");
					return;
				}
				
				// Connect to the server
				NetworkUtils.connectToServer(serverAddress[0], Integer.parseInt(serverAddress[1]));
			}
		});
		
		// Add the 'connect' fields to the address field panel
		addressFieldPanel.add(addressTextField);
		addressFieldPanel.add(connectButton);
		
		// Create the 'Create Game' button
		JButton createGameButton = new JButton("Create Game");
		createGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		createGameButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				NetworkUtils.startServer();
			}
		});
		// Create the 'Back' button
		JButton backButton = new JButton("Back");
		backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				Utils.mainFrame.toggleMultiplayerMenu();
			}
		});
		
		// Create the box for the components
		Box menuBox = Box.createVerticalBox();
		
		// Add the components to the box
		menuBox.add(Box.createVerticalStrut(50));
		menuBox.add(titleLabel);
		menuBox.add(Box.createVerticalStrut(30));
		menuBox.add(addressFieldPanel);
		menuBox.add(Box.createVerticalStrut(20));
		menuBox.add(createGameButton);
		menuBox.add(Box.createVerticalStrut(40));
		menuBox.add(backButton);
		
		// Add the menu box to the panel
		add(menuBox);
	}
	
}
