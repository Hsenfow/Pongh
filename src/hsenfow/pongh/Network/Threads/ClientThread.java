package hsenfow.pongh.Network.Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Network.NetworkThread;
import hsenfow.pongh.Network.NetworkUtils;

public class ClientThread extends NetworkThread{
	
	// The host name and port of the server we'll be connecting to
	private String host;
	private int port;
	
	/**
	 * Sets up the client thread, ready to connect to a server.
	 */
	public ClientThread(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Opens the socket to the server.
	 */
	protected void openSocket(){
		try{
			Utils.log("Connecting to server: " + host + ":" + port);
			
			// Connect to the server
			NetworkUtils.clientSocket = new Socket(host, port);
			
			// Create the socket writer
			NetworkUtils.socketWriter = new PrintWriter(NetworkUtils.clientSocket.getOutputStream(), true);
			
			// Create the socket reader
			NetworkUtils.socketReader = new BufferedReader(new InputStreamReader(NetworkUtils.clientSocket.getInputStream()));
			
			// Get the key the server should give us
			NetworkUtils.clientServerKey = NetworkUtils.socketReader.readLine();
			
			// Make sure the key is valid
			if(NetworkUtils.clientServerKey == null || NetworkUtils.clientServerKey.length() != NetworkUtils.CLIENT_SERVER_KEY_LENGTH
					|| NetworkUtils.clientServerKey.indexOf(NetworkUtils.CLIENT_SERVER_KEY_PREFIX) != 0){
				// We're not connected to a proper server
				Utils.log("Invalid key returned by server: " + NetworkUtils.clientServerKey);
				NetworkUtils.connected = false;
			}
			else{
				// The key is valid, so send it back to the server so it knows we got it
				NetworkUtils.socketWriter.println(NetworkUtils.clientServerKey);
				// We're connected and ready to play
				NetworkUtils.connected = true;
				
				Utils.log("Connected");
			}
		} catch(IOException ioe){
			Utils.log("Error connecting to server [" + host + ":" + port + "]: " + ioe.getMessage());
		}
	}
	
}
