package hsenfow.pongh.Network;

import java.io.IOException;

import javax.swing.JOptionPane;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Network.NetworkCommunications.MessageResult;

/**
 * This class exists as a base for the client and network threads. Its methods should be overridden as needed.
 */
public abstract class NetworkThread extends Thread{
	
	/**
	 * Opens any sockets required. This should be overridden.
	 */
	protected abstract void openSocket();
	
	/**
	 * Opens the socket(s) required then begins the network message loop.
	 */
	@Override
	public void run(){
		// Open the socket
		openSocket();
		
		// If we're not connected then something went wrong opening the sockets, so just exit here
		if(!NetworkUtils.connected){
			NetworkUtils.closeSockets();
			NetworkUtils.closeNetworkDialog();
			return;
		}
		
		// Close the network dialog if it's visible
		NetworkUtils.closeNetworkDialog();
		
		// Show the game panel
		Utils.mainFrame.showGamePanel();
		
		// Start a loop to keep processing all the data we receive
		try{
			while(NetworkUtils.connected){
				getAndProcessMessage();
			}
		} catch(IOException ioe){
			Utils.log("Error while processing messages: " + ioe.getMessage());
			JOptionPane.showMessageDialog(Utils.mainFrame, "Unexpected disconnect");
		}
		
		// TODO There are sometimes some problems when disconnecting. NPE when updating / whole window freezing.
		
		Utils.log("Disconnected");
		
		// Destroy the game panel to go back to the main menu
		Utils.mainFrame.destroyGamePanel();
		
		// Ensure all sockets are closed
		NetworkUtils.closeSockets();
	}
	
	/**
	 * Gets and processes the latest message received from the client/server.
	 * Note: This method is static as it is also accessed from the server thread, because once the
	 * server has been started up, the server thread becomes much the same as the client one.
	 */
	public void getAndProcessMessage() throws IOException{
		// Get and process the latest message received from the server
		MessageResult result = NetworkCommunications.processMessage(NetworkUtils.socketReader.readLine());
		
		// Handle the result
		switch(result){
		case REQUESTED_DC:
		case NO_DATA:
		case NO_KEY:
		case UNKNOWN_MESSAGE:
		case INVALID_MESSAGE:{
			// Some sort of error occurred or a proper disconnect message was sent by the
			// server, so inform the user
			JOptionPane.showMessageDialog(Utils.mainFrame, "Disconnected. Reason: " + result.toString());
			NetworkUtils.connected = false;
			break;
		}
		default:
			// Just continue as normal
			break;
		}
	}
	
}
