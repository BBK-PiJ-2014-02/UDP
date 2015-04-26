package implementation;

import java.io.IOException;

/**
 * Listener for any TCP messages and passes it onto the ServerClientHandler.
 * 
 * @author Vasco
 *
 */
public class TCPServerListener implements Runnable {
    /**
     * The ServerClientHandlerImpl hander.
     */
    private ServerClientHandlerImpl handler;
    
    /**
     * The Constructor.
     * 
     * @param server 
     */
    public TCPServerListener(ServerClientHandlerImpl server) {
        this.handler = server;
    }

    /**
     * Runnable
     */
    @Override
    public void run() {
        String message = "";

        // Check if shutdown signal was issued.
        while(handler.clientTCPSocket.isConnected()) {
            try {
                // Waits until a message is received.
                message = handler.inputStream.readLine();

                // A null message only means the socket closed.
                if (message == null) break;

                // Relays the message to the hander to be processed.
                handler.receivingTCPMessage(message);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("SERVER Listener closed.");
    }
}
