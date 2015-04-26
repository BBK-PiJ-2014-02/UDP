package implementation;

import interfaces.Server;
import interfaces.ServerClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import constants.Port;
import constants.Role;
import constants.Timeout;

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
     * The ServerSocket.
     */
    private ServerSocket serverSocket;

    /**
     * The Client Socket.
     */
    private Socket clientSocket;

    /**
     * Shutdown flag will be true whenever a shutdown is issued.
     */
    private boolean shutdown = false;

    /**
     * The Server Constructor.
     */
    public ServerImpl() {
        // Open a new server socket for a new client wanting to connect.
        try { 
            serverSocket = new ServerSocket(Port.SERVER); 
            serverSocket.setSoTimeout(Timeout.SERVER_SOCKET_TIMEOUT_DELAY);
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        }
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
            // Flag on when timeout occurred.
            boolean timeout = false;

            // Wait for next Client to connect
            clientSocket = new Socket();

            // Accept next connection.
            try { 
                clientSocket = serverSocket.accept();
                System.out.println("SERVER: Accepted connection to port: " + clientSocket.getPort());
            } 
            catch ( SocketException se ) {
                timeout = true;
            } catch (IOException e ) { 
                e.printStackTrace(); 
            }

            if ( !timeout ) {
                // A new connection was found and accepted. Add it to the Handler and set it as sender if no sender set yet.
                ServerClientHandler newClient = null;
                if ( serverClientHandlerList == null ) { 
                    serverClientHandlerList = new LinkedList<ServerClientHandler>();
                    newClient = new ServerClientHandlerImpl(this, clientSocket, UUID.randomUUID(), Role.SENDER);
                }
                else {
                    newClient = new ServerClientHandlerImpl(this, clientSocket, UUID.randomUUID(), Role.RECEIVER);
                }
                serverClientHandlerList.add(newClient);

                // Create a new thread for the handler and start it.
                Thread newThread = new Thread(newClient);
                newThread.start();

                System.out.println("SERVER: Added a new client: " + serverClientHandlerList.size());
            }

            // Check if a shutdown was issued.
            if ( shutdown ) break;
        }

        // Close the socket before exit.
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("SERVER shutdown successfully!");
    }

    /**
     * Shutdown the whole system.
     */
    @Override
    public void shutdown() {
        for(ServerClientHandler serverClientHandler : serverClientHandlerList) {
            System.out.println("Shutting down: "+serverClientHandler.toString());
            serverClientHandler.setRole(Role.SHUTDOWN);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.shutdown = true;
    }

    /**
     * Return a list of all currently live handlers.
     */
    @Override
    public List<ServerClientHandler> getAllHandlers() {
        return serverClientHandlerList;
    }
}
