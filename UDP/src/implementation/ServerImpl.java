package implementation;

import interfaces.Server;
import interfaces.ServerClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import constants.Port;
import constants.Role;

/**
 * Server Implementation of Server interface.
 * 
 * @author Vasco
 *
 */
public class ServerImpl implements Server {
	/**
	 * List of ServerClientHandler instantiated.
	 */
	private List<ServerClientHandler> serverClientHandler;
	
	/**
	 * The currently sending client.
	 */
	private ServerClientHandler clientSender;
	
	/**
	 * The Server Constructor.
	 */
	public ServerImpl() {
		serverClientHandler = new LinkedList<ServerClientHandler>();
	}
	
	/**
	 * The main.
	 * 
	 * @param args no args expected
	 */
	public static void main(String[] args) {
		Server server = new ServerImpl();
		server.run();
	}

	/**
	 * The Server runnable.
	 */
	@Override
	public void run() {
		// Open a new server socket for a new client wanting to connect.
		ServerSocket tcp_socket = null;
		try { 
			tcp_socket = new ServerSocket(Port.SERVER); 
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		}

		// Run forever
		while(true) {
			// Socket is now open. Await for a new Client to request a connection.
			Socket clientSocket = null;
			try { 
				clientSocket = tcp_socket.accept(); 
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
			}
			
			// A new connection was found and accepted. Add it to the Handler as sender if no sender yet.
			ServerClientHandler newClient = null;
			if ( clientSender == null ) { 
				clientSender = new ServerClientHandlerImpl(this, clientSocket, UUID.randomUUID(), Role.SENDER);
				newClient = clientSender;
			}
			else {
				newClient = new ServerClientHandlerImpl(this, clientSocket, UUID.randomUUID(), Role.RECEIVER);
				serverClientHandler.add(newClient);
			}
			
			// Create a new thread for the handler and start it.
			Thread newThread = new Thread(newClient);
			newThread.start();
			System.out.println("Added a new client: "+serverClientHandler.size());

// TODO
			// If requested to quit, exit.
//			if ( false ) break;
		}
		
		// Close the SERVER tcp socket.
//		try { 
//			tcp_socket.close(); 
//		} 
//		catch (IOException e) { 
//			e.printStackTrace(); 
//		}
	}
	
	/**
	 * Deletes a ServerClientHanlder from the list due to closed connection.
	 * 
	 * @param serverClientHandler
	 */
	public void deleteServerClientHander(ServerClientHandler serverClientHandler) {
		if ( serverClientHandler == null ) throw new IllegalArgumentException("Cannot delete a null ServerClientHandler.");
		// If this was a sender, it is required to delete it from the clientSender.
		if ( serverClientHandler.getRole().equals(Role.SENDER) ) {
			clientSender = null;
		}
		this.serverClientHandler.remove(serverClientHandler);
	}
	
	/**
	 * When this is called, the current clientSender will be sent to the end of the queue 
	 * and the first from the serverClientHandler list will be called to send instead.
	 */
	public void setNextSender() {
		if ( serverClientHandler.size() > 0 ) {
			clientSender.setRole(Role.RECEIVER);
			// Try to remove before inserting it as a receiver.
			serverClientHandler.remove(clientSender);
			serverClientHandler.add(clientSender);
			
			ServerClientHandler newServerClientHandler = serverClientHandler.remove(0);
			newServerClientHandler.setRole(Role.SENDER);
			clientSender = newServerClientHandler;
		}
	}
}
