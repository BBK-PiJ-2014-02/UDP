package implementation;

import interfaces.Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

import constants.Message;
import constants.Port;

/**
 * The Client implementation.
 * 
 * @author Vasco
 *
 */
public class ClientImpl implements Client {
	/**
	 * The client Socket.
	 */
	private Socket clientSocket;
	
	/**
	 * Data output Stream.
	 */
	private DataOutputStream outToServer;
	
	/**
	 * Buffer Reader for input Stream.
	 */
    private BufferedReader inFromServer;

    /**
     * Current client's role.
     */
    private String role;
    
    /**
     * Client's id.
     */
    private String id;
    
	/**
	 * The Main.
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new ClientImpl();
		client.run();
	}
	
	/**
	 * The Client constructor.
	 */
	public ClientImpl() {
		try { 
			clientSocket = new Socket(InetAddress.getLocalHost(),Port.SERVER);
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		}

		try {
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}

		try {
			inFromServer = new BufferedReader(new InputStreamReader( clientSocket.getInputStream() )); 
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO: Need to save into file or upload from file: Which file?
		
		// Request Role
		while (!requestRole());
		
		// Request Id
		while (!requestId());

try {
	Thread.sleep(10000L);
} catch (InterruptedException e) {
	e.printStackTrace();
}
        try { clientSocket.close();} 
        catch (IOException e) {e.printStackTrace();}
        System.out.println("Socket closed.");
	}

	/**
	 * Request the Id from the server.
	 * 
	 * @return true if successful
	 */
	private boolean requestId() {
	    sendTCPMessage(Message.REQUEST_ID);
	    id = getTCPResponse();
        sendTCPMessage(Message.SUCCESS);
        if ( id == null ) return false;
        return true;
	}
	
	/**
	 * Request Role from the server.
	 * 
	 * @return true if successful
	 */
	private boolean requestRole() {
	    sendTCPMessage(Message.REQUEST_ROLE);
	    role = getTCPResponse();
        sendTCPMessage(Message.SUCCESS);

        if ( role == null ) return false;

        return true;
	}
	
	/**
	 * Send a message to Server.
	 * 
	 * @param message the message
	 */
	private void sendTCPMessage(String message) {
		if ( message == null ) throw new IllegalArgumentException("Cannot send a null message.");
		try {
			outToServer.writeBytes(message+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a response from the server.
	 * 
	 * @return server String response
	 */
	private String getTCPResponse() {
		String message = null;
		try { 
	    	message = inFromServer.readLine(); 
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
		}
		
		return message;
	}
}
