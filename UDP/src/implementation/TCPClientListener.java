package implementation;

import java.io.IOException;

/**
 * Listener for the Client on any TCP messages.
 * 
 * @author Vasco
 *
 */
public class TCPClientListener implements Runnable {
	/**
	 * The ClientImpl handler
	 */
	private ClientImpl client;
	
	/**
	 * The Constructor
	 * 
	 * @param client the Client
	 */
	public TCPClientListener(ClientImpl client) {
		this.client = client;
	}

	/**
	 * Runnable
	 */
	@Override
	public void run() {
    	// Listens to any shutdown request.
		while(!(client.clientSocket.isClosed())){
		    try {
		    	// Waits for the next message to come through
		    	String message = client.inFromServer.readLine();

		    	// A null message means the socket was closed.
				if (message == null) break;

				// Relays the message to the Client to be processed.
				client.receivingTCPMessage(message);

			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		System.out.println("CLIENT Listener closed.");
	}
}
