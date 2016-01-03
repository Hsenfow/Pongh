package hsenfow.pongh.Network;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Network.Threads.ClientThread;
import hsenfow.pongh.Network.Threads.ServerThread;

public final class NetworkUtils {
	
	// The thread used for any client-server communications
	public static Thread networkThread = null;
	
	// The server socket
	public static ServerSocket serverSocket = null;
	
	// The client socket
	public static Socket clientSocket = null;
	
	// The PrintWriter used to send data through a socket
	public static PrintWriter socketWriter = null;
	// The BufferedReader used to read data from a socket
	public static BufferedReader socketReader = null;
	
	// The dialog that shows information about what network-related thing is currently happening.
	// Only displays if we're either connecting to a server or awaiting a connection from a client
	public static JDialog networkDialog = null;
	
	// Whether or not we're connected to either a server (as a client) or a client (as a server)
	public static volatile boolean connected = false;
	
	// Whether we're the server or not
	public static boolean isServer = false;
	
	// The length of the client-server key (including the prefix)
	public static final int CLIENT_SERVER_KEY_LENGTH = 32;
	// The client-server key prefix
	public static final String CLIENT_SERVER_KEY_PREFIX = "{Ponghline}";
	
	// The private client-server key
	public static String clientServerKey = null;
	
	/**
	 * Closes all active network connections.
	 */
	public static void closeAllConnections(){
		// Close any sockets
		NetworkUtils.closeSockets();
		
		// Wait for the server thread to die
		if(NetworkUtils.networkThread != null){
			try{
				NetworkUtils.networkThread.join();
			} catch(InterruptedException ie){ }
			NetworkUtils.networkThread = null;
		}
	}
	
	/**
	 * Closes all open sockets.
	 */
	public static void closeSockets(){
		// We're no longer connected
		NetworkUtils.connected = false;
		
		// We may have been a server once, but we certainly aren't now
		NetworkUtils.isServer = false;
		
		// Delete the client-server key
		NetworkUtils.clientServerKey = null;
		
		// Close the server socket
		if(NetworkUtils.serverSocket != null){
			try{
				NetworkUtils.serverSocket.close();
			} catch(IOException ioe){ Utils.log("Error closing server socket: " + ioe.toString()); }
			NetworkUtils.serverSocket = null;
		}

		// Close the client socket
		if(NetworkUtils.clientSocket != null){
			try{
				NetworkUtils.clientSocket.close();
			} catch(IOException ioe){ Utils.log("Error closing client socket: " + ioe.toString()); }
			NetworkUtils.clientSocket = null;
		}
	}
	
	/**
	 * Starts the server thread.
	 */
	public static void startServer(){
		// Prevent another server being started
		if(NetworkUtils.serverSocket != null){
			Utils.log("Server already started");
			return;
		}
		
		// Create and start the server thread.
		try{
			NetworkUtils.networkThread = new ServerThread();
			NetworkUtils.networkThread.start();
		} catch(IOException ioe){
			Utils.log("Error starting server thread: " + ioe.getMessage());
			return;
		}
		
		// We're a server
		NetworkUtils.isServer = true;
		
		// Show the user that the server is waiting
		NetworkUtils.showNetworkDialog("Awaiting connection",
				  "<html>Awaiting connection from a client.<br />"
					+ "<div style='margin:10px;'>"
						+ "IP address: <b>" + NetworkUtils.getLocalIP() + "</b><br />"
						+ "Port: <b>" + NetworkUtils.serverSocket.getLocalPort() + "</b>"
					+ "</div>"
				+ "</html>");
	}
	
	/**
	 * Connects to the specified server.
	 * @param host The hostname of the server to connect to
	 * @param port The port to use to connect
	 */
	public static void connectToServer(String host, int port){
		// Prevent connecting to another server if we're already connected to one
		if(NetworkUtils.clientSocket != null){
			Utils.log("Already connected to a server");
			return;
		}
		
		// Create the client thread
		NetworkUtils.networkThread = new ClientThread(host, port);
		NetworkUtils.networkThread.start();
		
		// Show the 'connecting' dialog
		NetworkUtils.showNetworkDialog("Connecting", "Connecting to server: " + host + ":" + Integer.toString(port));
	}
	
	/**
	 * Shows a dialog box with the given information and a cancel button that closes all network
	 * connections when clicked.
	 * @param title The title to give the dialog
	 * @param message The message to display on the dialog
	 */
	public static void showNetworkDialog(String title, String message){
		// Create the dialog
		NetworkUtils.networkDialog = new JDialog(Utils.mainFrame, title, true);
		NetworkUtils.networkDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		NetworkUtils.networkDialog.setLocation(Utils.mainFrame.getX(), Utils.mainFrame.getY());
		NetworkUtils.networkDialog.setResizable(false);
		
		// Create the label for the message
		JLabel messageLabel = new JLabel(message);
		
		// Create the 'cancel' button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				// Close all connections
				NetworkUtils.closeAllConnections();
				
				// Close the dialog
				closeNetworkDialog();
			}
		});
		
		// Create the dialog's JPanel
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new GridLayout(0, 1));
		
		// Add the components to the panel
		dialogPanel.add(messageLabel);
		dialogPanel.add(cancelButton);
		
		// Add the panel to the dialog
		NetworkUtils.networkDialog.add(dialogPanel);
		
		// Show the dialog
		NetworkUtils.networkDialog.pack();
		NetworkUtils.networkDialog.setVisible(true);
	}
	
	/**
	 * Closes the network dialog if it's currently displayed.
	 */
	public static void closeNetworkDialog(){
		if(NetworkUtils.networkDialog != null){
			// When the client connects to the server too quickly, I believe the dialog box doesn't
			// actually have chance to be shown before that, which causes it to become half
			// visible, preventing the user interacting with the game.
			// So just sleep for a few milliseconds.
			try{ Thread.sleep(100); } catch(InterruptedException ie){ /* Nothing */ }
			
			// Remove the dialog box
			NetworkUtils.networkDialog.setVisible(false);
			NetworkUtils.networkDialog = null;
		}
	}
	
	/**
	 * Generates a key used for communication between a client and server.
	 * @return The generated key
	 */
	public static String generateKey(){
		String key = CLIENT_SERVER_KEY_PREFIX;
		while(key.length() < CLIENT_SERVER_KEY_LENGTH) key += (char)((Math.random() * 94) + 32);
		return key;
	}
	
	/**
	 * Gets the local IP address assigned to the computer.
	 * @return The computer's local IP address
	 */
	public static String getLocalIP(){
		try{
			return InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException uhe){
			Utils.log("Error getting local IP: " + uhe.getMessage());
			return "Unknown";
		}
	}
	
}
