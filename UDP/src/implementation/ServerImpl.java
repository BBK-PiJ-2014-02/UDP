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
    private List<ServerClientHandler> serverClientHandlerList;

    /**
     * The currently sending client.
     */
    private ServerClientHandler serverClientHandler;

    /**
     * The ServerSocket.
     */
    private ServerSocket serverSocket;
    
    /**
     * The Client Socket.
     */
    private Socket clientSocket;


    /**
     * The Server Constructor.
     */
    public ServerImpl() {
        // Open a new server socket for a new client wanting to connect.
        try { 
            serverSocket = new ServerSocket(Port.SERVER); 
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        }

        // Instantiate a new LinkedList for all ServerClientHandlers.
        serverClientHandlerList = new LinkedList<ServerClientHandler>();
    }
    
    /**
     * The Server startup main method.
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
        // Run forever
        while(true) {
            // Wait for next Client to connect
            clientSocket = new Socket();
            
            // Accept next connection.
            try { 
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection to port: "+clientSocket.getPort());
            } 
            catch (IOException e) { 
                e.printStackTrace(); 
            }
            
            // A new connection was found and accepted. Add it to the Handler as sender if no sender yet.
            ServerClientHandler newClient = null;
            if ( serverClientHandler == null ) { 
                serverClientHandler = new ServerClientHandlerImpl(this, clientSocket, UUID.randomUUID(), Role.SENDER);
                newClient = serverClientHandler;
            }
            else {
                newClient = new ServerClientHandlerImpl(this, clientSocket, UUID.randomUUID(), Role.RECEIVER);
            }
            serverClientHandlerList.add(newClient);
            
            // Create a new thread for the handler and start it.
            Thread newThread = new Thread(newClient);
            newThread.start();
            System.out.println("Added a new client: "+serverClientHandlerList.size());


// TODO
            // If requested to quit, exit.
//            if ( false ) break;
        }
        
        // Shutdown Server and any open Clients.
//        shutdown();
        
//        System.out.println("All done.");
    }
    
    /**
     * Shutdown.
     */
    private void shutdown() {
        for(ServerClientHandler serverClientHandler : serverClientHandlerList) {
            System.out.println("Shutting down: "+serverClientHandler.toString());
            serverClientHandler.setRole(Role.SHUTDOWN);
        }
        
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
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
            serverClientHandler = null;
        }
        this.serverClientHandlerList.remove(serverClientHandler);
    }
    
    /**
     * When this is called, the current clientSender will be sent to the end of the queue 
     * and the first from the serverClientHandler list will be called to send instead.
     */
    public void setNextSender() {
        if ( serverClientHandlerList.size() > 0 ) {
            serverClientHandler.setRole(Role.RECEIVER);
            // Try to remove before inserting it as a receiver.
            serverClientHandlerList.remove(serverClientHandler);
            serverClientHandlerList.add(serverClientHandler);
            
            ServerClientHandler newServerClientHandler = serverClientHandlerList.remove(0);
            newServerClientHandler.setRole(Role.SENDER);
            serverClientHandler = newServerClientHandler;
        }
    }
}
