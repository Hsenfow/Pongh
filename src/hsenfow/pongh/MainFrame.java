package hsenfow.pongh;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MainFrame extends JFrame{
	private static final long serialVersionUID = -3449865393849618303L;
	
	// TODO Add the ability to specify an avatar for multiplayer
	
	// The title of the window
	public static final String WINDOW_TITLE = "Pongh";
	// The default window size
	// TODO Add the ability to resize the window without anything messing up
	public static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;
	
	// The title displayed on the main menu
	public static final String MAIN_MENU_TITLE = "Pongh";
	
	// The menu JPanel
	private JPanel menuPanel;
	
	// The game JPanel
	public GamePanel gamePanel;
	
	// The multiplayer menu JPanel
	private MultiplayerPanel multiplayerPanel;
	
	/**
	 * The entry point for the program.
	 * @param args Command line arguments
	 */
	public static void main(String[] args){
		Utils.mainFrame = new MainFrame(WINDOW_TITLE);
	}
	
	/**
	 * Creates the essential objects needed to start the program.
	 */
	public MainFrame(String title){
		super(title);
		
		// Get the screen size
		Utils.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// Set up the window
		setupWindow();
	}
	
	/**
	 * Sets up the main JFrame and everything inside it.
	 */
	private void setupWindow(){
		// We'll be using the Nimbus look and feel if possible
		Utils.setLookAndFeel("Nimbus");
		
		/* Set up the main frame */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setLocation(((Utils.screenSize.width / 2) - (WINDOW_WIDTH / 2)),
				((Utils.screenSize.height / 2) - (WINDOW_HEIGHT / 2)));
		
		/* Create the menu JPanel */
		menuPanel = new JPanel();
		menuPanel.setBackground(Color.GRAY);
		// We'll be using a box so that everything is positioned vertically
		Box menuBox = Box.createVerticalBox();
		
		// Create the Pongh title label
		JLabel titleLabel = new JLabel(MAIN_MENU_TITLE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 100));
		// Add the title JLabel to the menu box
		menuBox.add(Box.createVerticalStrut(50));
		menuBox.add(titleLabel);
		menuBox.add(Box.createVerticalStrut(30));
		
		// Create the menu buttons
		createMenuButtons(menuBox);
		
		// Add the menu box to the menu panel
		menuPanel.add(menuBox);
		
		// Add the menu panel to the main frame
		add(menuPanel);
		
		// Display the window
		setVisible(true);
	}
	
	/**
	 * Creates the menu buttons and adds them to the given box.
	 * @param menuBox The box that contains all of the menu components
	 */
	private void createMenuButtons(Box menuBox){
		// Create the 'Single Player' button
		JButton singlePlayerButton = new JButton("Single Player");
		singlePlayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		singlePlayerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				// Show the game panel
				showGamePanel();
			}
		});
		// Create the 'Multiplayer' button
		JButton multiplayerButton = new JButton("Multiplayer");
		multiplayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		multiplayerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				toggleMultiplayerMenu();
			}
		});
		// Create the 'Options' button
		JButton optionsButton = new JButton("Options");
		optionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				JOptionPane.showMessageDialog(Utils.mainFrame, "Not yet implemented");
			}
		});
		// Create the 'Exit' button
		JButton exitButton = new JButton("Exit");
		exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				// Get the user to confirm they want to close the window
				int confirm = JOptionPane.showConfirmDialog(Utils.mainFrame, "Close?", "Confirm close", JOptionPane.YES_NO_OPTION);
				if(confirm == JOptionPane.YES_OPTION){
					// Close the window
					Utils.mainFrame.dispatchEvent(new WindowEvent(Utils.mainFrame, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		
		// Add the menu buttons to the menu box
		menuBox.add(singlePlayerButton);
		menuBox.add(Box.createVerticalStrut(20));
		menuBox.add(multiplayerButton);
		menuBox.add(Box.createVerticalStrut(20));
		menuBox.add(optionsButton);
		menuBox.add(Box.createVerticalStrut(50));
		menuBox.add(exitButton);
	}
	
	/**
	 * Toggles between the main menu and the multiplayer screen.
	 */
	public void toggleMultiplayerMenu(){
		// If the multiplayer panel doesn't exist, then create and show it
		if(multiplayerPanel == null){
			multiplayerPanel = new MultiplayerPanel();
			add(multiplayerPanel);
			
			// Hide the menu panel
			menuPanel.setVisible(false);
		}
		else{
			// Remove and delete the multiplayer panel
			remove(multiplayerPanel);
			multiplayerPanel = null;
			
			// Show the menu panel
			menuPanel.setVisible(true);
		}
	}
	
	/**
	 * Shows the game panel.
	 */
	public void showGamePanel(){
		// Don't create the game panel again if it already exists
		if(gamePanel == null){
			gamePanel = new GamePanel();
			add(gamePanel);
			gamePanel.requestFocus();
			
			// Hide the menu panel
			menuPanel.setVisible(false);
			if(multiplayerPanel != null) multiplayerPanel.setVisible(false);
		}
	}
	
	/**
	 * Destroys the game panel.
	 */
	public void destroyGamePanel(){
		if(gamePanel != null){
			// Remove and delete the game panel
			remove(gamePanel);
			gamePanel = null;
			
			// Show the menu panel again
			menuPanel.setVisible(true);
		}
	}
	
	/**
	 * Used to get the game panel's width.
	 * @return The width of the game panel
	 */
	public int getGamePanelWidth(){
		if(gamePanel != null) return gamePanel.getWidth();
		else return 0;
	}
	
	/**
	 * Used to get the game panel's height.
	 * @return The height of the game panel
	 */
	public int getGamePanelHeight(){
		if(gamePanel != null) return gamePanel.getHeight();
		else return 0;
	}
	
}
