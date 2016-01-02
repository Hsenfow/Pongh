package hsenfow.pongh.Network.Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

import hsenfow.pongh.Utils;
import hsenfow.pongh.Network.NetworkThread;
import hsenfow.pongh.Network.NetworkUtils;

public class ServerThread extends NetworkThread{
	
	/**
	 * Creates the server thread ready to accept a client connection.
	 * @throws IOException Thrown if the server socket could not be opened
	 */
	public ServerThread() throws IOException{
		// Create the server socket
		NetworkUtils.serverSocket = new ServerSocket(0);
	}
	
	/**
	 * Opens the server socket and awaits a connection from a client.
	 */
	protected void openSocket(){
		try{
			Utils.log("Starting server");
			
			// Wait for a client
			NetworkUtils.clientSocket = NetworkUtils.serverSocket.accept();
			
			// Create the socket writer
			NetworkUtils.socketWriter = new PrintWriter(NetworkUtils.clientSocket.getOutputStream(), true);
			
			// Create the socket reader
			NetworkUtils.socketReader = new BufferedReader(new InputStreamReader(NetworkUtils.clientSocket.getInputStream()));
			
			// Generate and share client-server key
			NetworkUtils.clientServerKey = NetworkUtils.generateKey();
			NetworkUtils.socketWriter.println(NetworkUtils.clientServerKey);
			
			// Wait for a response from the client, telling us that it got the key
			String response = NetworkUtils.socketReader.readLine();
			// If the response isn't the key we sent, then something went wrong
			if(response == null || !response.equals(NetworkUtils.clientServerKey)){
				Utils.log("Invalid key returned by client: " + NetworkUtils.clientServerKey);
				NetworkUtils.connected = false;
			}
			else{
				// We're connected and ready to play
				NetworkUtils.connected = true;
				
				Utils.log("Connected");
			}
		} catch(IOException ioe){
			Utils.log("Error creating server: " + ioe.getMessage());
		}
	}
	
}
