package implementation;

import interfaces.PacketData;
import interfaces.PacketManager;
import interfaces.Server;
import interfaces.ServerClientHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import constants.Message;
import constants.Role;
import constants.Timeout;

/**
 * The ServerClientHandler interface implementation.
 * 
 * @author Vasco
 *
 */
public class ServerClientHandlerImpl implements ServerClientHandler {
    /**
     * The current Client's role.
     */
    protected String clientRole;

    /**
     * The Client's unique id.
     */
    private final UUID id;

    /**
     * The Server link handler
     */
    private final Server server;

    /**
     * The local Host.
     */
    private InetAddress localHost;

    //================================= TCP ==================================// 

    /**
     * The TCP Client socket.
     */
    protected final Socket clientTCPSocket;

    /**
     * The Buffered Reader input from the client TCP socket.
     */
    protected BufferedReader inputStream;

    /**
     * The output Stream to the client.
     */
    protected DataOutputStream outputStream;


    //================================= UDP ==================================// 

    /**
     * The Port the Client is listening to.
     */
    private Integer sendingPort;

    /**
     * The Port the Client is sending messages to.
     */
    private Integer receivingPort;

    /**
     * The UDP Handler Socket for sending / receiving messages to / from the Client.
     */
    private DatagramSocket handlerUDPSocket;

    /**
     * If the client sent message NO_FILES, this is set to false.
     * The flag tells the handler if this client is good to set as sender.
     */
    private boolean hasMoreFiles = true;

    /**
     * The packet manager.
     */
    private final PacketManager packetManager;


    //========================================================================// 

    /**
     * Constructor.
     * 
     * @param server the server socket
     * @param clientSocket the client socket
     * @param uniqueId the Client unique id
     * @param role the Client's role
     */
    public ServerClientHandlerImpl(Server server, Socket clientSocket, UUID uniqueId, String role) {
        // Validating given parameters
        if ( server == null ) throw new IllegalArgumentException("Server cannot be null.");
        if ( clientSocket == null ) throw new IllegalArgumentException("Client Socket cannot be null.");
        if ( uniqueId == null ) throw new IllegalArgumentException("UniqueId cannot be null.");
        if ( role == null ) throw new IllegalArgumentException("Role cannot be null.");

        this.server = server;
        this.clientTCPSocket = clientSocket;
        this.id   = uniqueId;
        this.clientRole = role;

        try {
            this.localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e3) {
            e3.printStackTrace();
        }

        // TCP timeout.
        try {
            this.clientTCPSocket.setSoTimeout(Timeout.TCP_SOCKET_TIMEOUT_DELAY);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        
        // Get the local host.
        try {
            InetAddress.getLocalHost();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        // Get the input stream ready to be read from.
        try {
            this.inputStream = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the output stream ready to be written into.
        try {
            this.outputStream = new DataOutputStream(clientTCPSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initiate the Handler UDP socket
        try {
            // Let a new port be chosen automatically
            handlerUDPSocket = new DatagramSocket();

            // Set the timeout for UDP
            handlerUDPSocket.setSoTimeout(Timeout.UDP_SOCKET_TIMEOUT_DELAY);

            // Set the Handler's receiving port
            receivingPort = handlerUDPSocket.getLocalPort();

        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Initialise an immutable Package Manager
        this.packetManager = new PacketManagerImpl(handlerUDPSocket, localHost, receivingPort);

        // Setup Client and Handler with ports, intial role and unique id
        setClientRole();         // Send Role to Client.
        setClientUniqueId();     // Send unique Id to Client
        sendOurReceivingPort();  // Ask client to listen to our sending port
        setOurSendingPort();     // Set our sending port with Client's receiving port.

    }


    //============================= Runnable =================================// 

    /**
     * The Runnable subroutine.
     */
    @Override
    public void run() {
        // Initiate listener for any TCP messages sent by the client.
        Thread listener = new Thread(new TCPServerListener(this));
        listener.start();

        // Endless loop until shutdown is requested.
        while(!clientRole.equals(Role.SHUTDOWN)) {

            // Required delay
            try {
                Thread.sleep(Timeout.SLEEP_DELAY);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            // If Client is sending, we are receiving.
            if ( clientRole.equals(Role.SENDER)) {
                // Wait for the next UDP packet to be sent to us.
                PacketData packetData = packetManager.receive();

                // PacletData null means that we have timed out.
                // Client did not send any more packets or has no more files.
                if ( packetData == null ) {
                    // TODO Make some other Client the sender.
                    pickNextSender();
                    continue;
                }

                // Sent to all clients receiving this, the received packetData.
                sendAll(packetData);
            }
        }

        // That's all folks...
        finalise();

        System.out.println("HANDLER: finished.");
    }


    //====================== Initialising and Finalising methods ==========================// 


    /**
     * Shutdown Handler.
     */
    private void finalise() {
        try {
            clientTCPSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set Client's sending port to the one Handler's receiving port.
     * 
     * @return true if successful
     */
    private boolean sendOurReceivingPort() {
        // Sends the Handler's receiving port to Client.
        sendTCPMessage(receivingPort.toString());

        // Awaits for acknowledgement
        if ( isReceivedMessage(Message.SUCCESS) ) {
            return true;
        }

        return false;
    }

    /**
     * Send the Role to the Client.
     * 
     * @return true if successful
     */
    private boolean setClientRole() {
        // Sends the role to Client.
        sendTCPMessage(clientRole.toString());

        // Awaits for acknowledgement
        if ( isReceivedMessage(Message.SUCCESS) ) {
            return true;
        }
        return false;
    }

    /**
     * Send the Unique ID to the Client.
     * 
     * @return true if successful
     */
    private boolean setClientUniqueId() {
        // Sends unique Id to Client.
        sendTCPMessage(id.toString());

        // Awaits for acknowledgement
        if ( isReceivedMessage(Message.SUCCESS) ) {
            return true;
        }

        return false;
    }

    /**
     * Loads the Client's UDP receiving port as our sending port.
     */
    private void setOurSendingPort() {
        // Gets the Client's receiving port
        String port = null;
        try {
            port = inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If port is null, throw exception.
        if ( port == null ) throw new IllegalStateException("Received a null port from client.");

        // Parse the port into integer.
        sendingPort = Integer.parseInt(port);

        // Send acknowledgement
        sendTCPMessage(Message.SUCCESS);
    }

    /**
     * Returns true if expected message is returned.
     * 
     * @param message the expected message
     * @return true if acknowledged
     */
    private boolean isReceivedMessage(String message) {
        if ( message == null ) throw new IllegalArgumentException("Cannot expect a null message.");

        String messageReturned = "";
        try {
            messageReturned = inputStream.readLine();
            if ( message.equals(messageReturned) ) return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //=========================== Running time methods ===============================// 

    /**
     * Send a message to Client.
     * 
     * @param message the message
     */
    private void sendTCPMessage(String message) {
        if ( message == null ) throw new IllegalArgumentException("Cannot send a null message.");
        try {
            outputStream.writeBytes(message+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The client's current role.
     */
    @Override
    public String getRole() {
        return clientRole;
    }

    /**
     * Setting a new client's role.
     */
    @Override
    public void setRole(String role) {
        if ( role == null ) throw new IllegalArgumentException("Cannot set a null Role.");
        this.clientRole = role;
        sendTCPMessage(this.clientRole.toString());
    }

    /**
     * The UDP port the client is sending messages to.
     */
    @Override
    public int getClientSendingPort() {
        return receivingPort;
    }

    /**
     * The UDP port the client is listening to.
     */
    @Override
    public int getClientReceivingPort() {
        return sendingPort;
    }



    /**
     * Listener messages will end up here to be processed.
     */
    @Override
    public void receivingTCPMessage(String message) {
        switch(message) {
            // Client has finished sending a file.
            case Message.FILE_TRANSFERRED : {
                // TODO: If any actions needs made after client sends on file.
                //       these should be added here. Currently no action is taken
                //       and is expected for the Client to carry on with next file
                //       until no more files are found to be sent.
                setRole(Role.RECEIVER);
                System.out.println("HANDLER("+id+"): file transferred, picking up next ender");
                pickNextSender();
                break; 
            }

            // Client has no more files to send. This will prevent the client from
            // being requested to role sender in the future.
            case Message.NO_FILES : {
                hasMoreFiles = false;
                System.out.println("HANLDER("+id+") NO FILES; setting role to RECEIVER");
                setRole(Role.RECEIVER);
                break;
            }

            // By default, do nothing about it.
            default : break;
        }
    }

    /**
     * Go over all handlers and pick the client that still has some files to send.
     * If all clients have no more files to send, issue a shutdown request.
     */
    private void pickNextSender() {

        // Flag to check if we have set any other client as sender
        boolean foundNewSender = false;

        // Go over all clients to find the next sender
        for( ServerClientHandler serverClientHandler : server.getAllHandlers() ) {

            // If this client still has files to send
            if ( serverClientHandler.hasFiles() ) {

                // Request a change of role to become sender
                serverClientHandler.setRole(Role.SENDER);

                // We have found one
                foundNewSender = true;

                // No need to keep searching
                break;
            }
        }

        // If no new sender Client set, shutdown the service.
        if (!foundNewSender) server.shutdown();
    }

    /**
     * Send to all receiver handlers the currently received packet.
     * 
     * @param packet PacketData
     */
    private void sendAll(PacketData packet) {
        // Deal with null packet.
        if ( packet == null ) throw new IllegalArgumentException("Cannot send a null packet.");

        // Go over all handlers currently running and send this packet to then if they are receivers.
        for ( ServerClientHandler serverClientHander : server.getAllHandlers() ) {
            if ( serverClientHander.getRole().equals(Role.RECEIVER)) {
                serverClientHander.sendUDPPacketToClient(packet);
                System.out.println("SENT PACKET: "+packet.getId());
            }
        }
    }

    /**
     * Sends the given packet to the Client.
     */
    @Override
    public void sendUDPPacketToClient(PacketData packet) {
        packetManager.send(packet);
    }

    /**
     * If the client sent at any point a NO_FILES message, this returns false thereafter.
     */
    @Override
    public boolean hasFiles() {
        return hasMoreFiles;
    }
}