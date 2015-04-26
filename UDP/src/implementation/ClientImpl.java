package implementation;

import interfaces.Client;
import interfaces.PacketData;
import interfaces.PacketManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import constants.Message;
import constants.Port;
import constants.Role;
import constants.Timeout;

/**
 * The Client implementation.
 * 
 * @author Vasco
 *
 */
public class ClientImpl extends ClientFileManagerImpl implements Client {
    /**
     * The client Socket.
     */
    protected Socket clientSocket;
    

    /**
     * Data output Stream.
     */
    private DataOutputStream outToServer;

    /**
     * Buffer Reader for input Stream.
     */
    protected BufferedReader inFromServer;

    /**
     * Current client's role.
     */
    protected String role;

    /**
     * Client's id.
     */
    private String id;

    /**
     * The UDP Socket.
     */
    private DatagramSocket udpSocket = null;

    /**
     * The UDP receiving port.
     */
    private Integer receivingPort;

    /**
     * The UDP sending port.
     */
    private Integer sendingPort;

    /**
     * The default localHost
     */
    private InetAddress localHost;


    /**
     * The Main.
     * @param args
     */
    public static void main(String[] args) {
        // Educate the user on how which arguments are missing.
        if ( args == null ) 
            throw new IllegalArgumentException("Please supply: sender/folder receiver/folder");
        if ( args.length != 2 ) 
            throw new IllegalArgumentException("Invalid number of arguments."); 
        if ( ! args[0].contains("send") ) 
            throw new IllegalArgumentException("First argument must be the sender/ folder.");
        if ( ! args[1].contains("receive"))
            throw new IllegalArgumentException("Second argument must be the receiver/ folder.");

        Client client = new ClientImpl(args[0], args[1]);
        client.run();
    }

    /**
     * The Client constructor.
     */
    public ClientImpl(String sendingBucket, String receivingBucket) {
        super(sendingBucket, receivingBucket);

        // Initiate local Host InetAddress
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        // Startup the TCP client Socket
        try { 
            clientSocket = new Socket(InetAddress.getLocalHost(),Port.SERVER);
            clientSocket.setSoTimeout(Timeout.TCP_SOCKET_TIMEOUT_DELAY);
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        }

        // Initiate a Data Output Stream to the Server on the TCP connection
        try {
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
        } 
        catch (IOException e) { 
            e.printStackTrace();
        }

        // Initiate an input stream from the server on TCP connection.
        try {
            inFromServer = new BufferedReader(new InputStreamReader( clientSocket.getInputStream() )); 
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        // Initiate the UDP socket.
        try {
            // Create a new UDP datagram socket
            udpSocket = new DatagramSocket();

            // The created local port will become the receiving port
            receivingPort = udpSocket.getLocalPort();

            // Set the UDP time out
            udpSocket.setSoTimeout(Timeout.UDP_SOCKET_TIMEOUT_DELAY);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Load all data from the Handler in the order the Handler will be sending it.
        loadRole();              // Load Role given by the Server.
        loadUniqueId();          // Load unique Id given by the Server.
        setOurSendingPort();     // Load the Handler's UDP receiving port as our sending port.
        sendOurReceivingPort();  // Send our UDP sending port to the Handler.
    }

    /**
     * The runnable for the Client.
     */
    @Override
    public void run() {
        // Initiate listener to retrieve any messages sent by the server.
        Thread listener = new Thread(new TCPClientListener(this));
        listener.start();

        // Loop forever unless a shutdown was required.
        while(!role.equals(Role.SHUTDOWN)) { 

            // We are sending to the Server.
            if (role.equals(Role.SENDER)) {

                // Check if more files exist to be sent.
                if ( super.hasMoreFilesToSend() ) {

                    // Check if the end of the file has been reached 
                    // Send appropriate message to the server if true.
                    if ( super.isEOF() ) {

                        // File transfer complete.
                        sendTCPMessage(Message.FILE_TRANSFERRED);

                        // Load next file
                        super.loadNextFileToSend();
                    }
                    // Not yet the end of the file
                    else {
                        // Pick up next file chunk
                        PacketData packet = super.getNextChunk(id);

                        // .. and send it to the Server.
                        PacketManager.send(udpSocket, packet, localHost, sendingPort);
                    }
                }
                else {
                    // Tell server that there are no more files to send.
                    sendTCPMessage(Message.NO_FILES);
                }
            }

            // We are receiving from the Server.
            if (role.equals(Role.RECEIVER)) {

                // Wait for the packet to arrive.
                PacketData packet = PacketManager.receive(udpSocket);

                if ( packet == null ) {
                    if ( super.hasMoreFilesToSend() ) {
                        // Client has more files to send, thus requesting a new role
                        sendTCPMessage(Message.REQUEST_ROLE);
                    }
                    // packet became null because it was either a complete transfer
                    // or a time out. Thus do nothing.
                    System.out.println("CLIENT: Got a null pack.");
                }
                else {
                    // Save packet to be then saved to file on completion.
                    super.savePacket(packet);
                }
            }
        }

        System.out.println("Exiting Client " + id);

        // Require finalisations. 
        finalisations();
    }


    /**
     * Closing all loose ends.
     */
    private void finalisations() {
        // Close the client Socket.
        try { 
            clientSocket.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sending the receiving port to the Handler.
     * 
     * @return true if successful
     */
    private boolean sendOurReceivingPort() {
        // Sends the Client's receiving port Handler.
        sendTCPMessage(receivingPort.toString());

        // Awaits for acknowledgement
        String response = getTCPResponse();
        if ( response == null ) throw new IllegalStateException("Expected a successful response, instead got null.");
        if ( response.equals(Message.SUCCESS) ) return true;

        return false;
    }


    /**
     * Load Handler's receiving port as our sending port.
     */
    private void setOurSendingPort() {
        // Gets the Handler's listening port
        String port = getTCPResponse();

        // If port is null, throw exception.
        if ( port == null ) throw new IllegalStateException("Received a null port from Handler.");

        // Parse the port into integer.
        sendingPort = Integer.parseInt(port);

        // Send acknowledgement
        sendTCPMessage(Message.SUCCESS);
    }
    

    /**
     * Load the unique Id from the server.
     * 
     * @return true if successful
     */
    private boolean loadUniqueId() {
        // Expect unique id to be returned
        id = getTCPResponse();

        // Check if set correctly
        if ( id == null ) throw new IllegalStateException("Unique Id cannot be set to null.");

        sendTCPMessage(Message.SUCCESS);

        return true;
    }
    

    /**
     * Load Role from the server.
     * 
     * @return true if successful
     */
    private boolean loadRole() {
        // Expect a role to be returned
        role = getTCPResponse();

        // Check if set correctly
        if ( role == null ) throw new IllegalStateException("Cannot set a null role.");
        if ( !role.equals(Role.RECEIVER) && !role.equals(Role.SENDER) ) 
            throw new IllegalStateException("Role received is not expected: " + role);

        sendTCPMessage(Message.SUCCESS);

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

    /**
     * Listening for messages sent from the server.
     * Actions for these will be defined here.
     */
    @Override
    public void receivingTCPMessage(String message) {
        switch(message) {

            // Server requested to change role to RECEIVER
            case Role.RECEIVER : {
                role = Role.RECEIVER;
                break;
            }

            // Server requested to change role to SENDER
            case Role.SENDER : {
                System.out.println("CLIENT("+id+"): Got request to change to role SENDER");
                role = Role.SENDER;
                break;
            }

            // Server requested to shutdown.
            case Role.SHUTDOWN : {
                role = Role.SHUTDOWN;
                break;
            }

            // By default apply no action.
            default : break;
        }
    }
}
