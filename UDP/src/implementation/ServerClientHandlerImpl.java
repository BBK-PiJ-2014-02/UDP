package implementation;

import interfaces.Server;
import interfaces.ServerClientHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

import constants.Message;
import constants.Packet;
import constants.Role;

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
    private String role;
    
    /**
     * The Client's unique id.
     */
    private final UUID id;
    
    /**
     * Define data.
     */
    private byte[] data = new byte[Packet.PACKET_SIZE];
    
    /**
     * The Server link handler
     */
    private final Server server;
    
    /**
     * The Client socket.
     */
    private final Socket clientSocket;

    /**
     * The Buffered Reader input from the client TCP socket.
     */
    private BufferedReader inputStream;
    
    /**
     * The output Stream to the client.
     */
    private DataOutputStream outputStream;
    
    /**
     * Client must request Role and id before starting reading or sending UDP packets.
     */
    private boolean isRoleRequested = false;

    /**
     * Client must request Role and id before starting reading or sending UDP packets.
     */
    private boolean isIdRequested = false;
    

    /**
     * Constructor.
     * 
     * @param uniqueId the Client unique id
     * @param role the client's role
     */
    public ServerClientHandlerImpl(Server server, Socket clientSocket, UUID uniqueId, String role) {
        // Validating given parameters
        if ( server == null ) throw new IllegalArgumentException("Server cannot be null.");
        if ( clientSocket == null ) throw new IllegalArgumentException("Client Socket cannot be null.");
        if ( uniqueId == null ) throw new IllegalArgumentException("UniqueId cannot be null.");
        if ( role == null ) throw new IllegalArgumentException("Role cannot be null.");

        this.server = server;
        this.clientSocket = clientSocket;
        this.id   = uniqueId;
        this.role = role;
        
        // Get the input stream ready to be read from.
        try {
            this.inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Get the output stream ready to be written into.
        try {
            this.outputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Runnable subroutine.
     */
    @Override
    public void run() {
        // Ensure Client has Role and uniqueID set.
        initializeClient();

        while(true) {
            // If a signal to shutdown, exits loop.
            if (role.equals(Role.SHUTDOWN)) break;
        }

//        setRole(Role.RECEIVER);
//        tcpSendRole();

//        setRole(Role.SENDER);
//        tcpSendRole();

//        setRole(Role.SHUTDOWN);
//        tcpSendRole();
        
        finalise();
        System.out.println("HANDLER: finished.");
    }
    
    /**
     * Shutdown Handler.
     */
    private void finalise() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Ensures client gets Role and uniqueID set before continuing.
     */
    private void initializeClient() {
        // Wait until the Client requests Role and uniqueID
        while(!isIdRequested || !isRoleRequested) {

            // Get Client request
            String request = getTCPMessage();

            // Check the type of request.
            if ( request.equals(Message.REQUEST_ROLE) ) {

                // Sends the role to Client.
                tcpSendRole();
                
                // Awaits for acknowledgement
                if ( acknowledge(Message.SUCCESS) ) {
                    isRoleRequested = true;
                    continue;
                }
            }
            
            // Check if request is of type id
            if ( request.equals(Message.REQUEST_ID) ) {
                
                // Sends the id to Client.
                tcpSendId();
                
                // Awaits acknowledgement.
                if ( acknowledge(Message.SUCCESS) ) {
                    isIdRequested = true;
                    continue;
                }
            }
        }
    }

    /**
     * Returns true if expected message is returned.
     * 
     * @param message the expected message
     * @return true if acknowledged
     */
    private boolean acknowledge(String message) {
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

    /**
     * TCP-send the Role to the client.
     */
    private void tcpSendRole() { 
        try {
            outputStream.writeBytes(role+'\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TCP-send the Id to the client.
     */
    private void tcpSendId() { 
        try {
            outputStream.writeBytes(id.toString()+'\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * TCP-receive message sent by Client or Error.
     * 
     * @return String message
     */
    private String getTCPMessage() {
        try {
            return inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return Message.ERROR;
    }

    /**
     * The client's current role.
     */
    @Override
    public String getRole() {
        return role;
    }

    /**
     * Setting a new client's role.
     */
    @Override
    public void setRole(String role) {
        if ( role == null ) throw new IllegalArgumentException("Cannot set a null Role.");
        this.role = role;
        tcpSendRole();
    }    
}
