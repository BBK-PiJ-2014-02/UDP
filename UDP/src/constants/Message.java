package constants;
/**
 * Available messages to be used between Server and Client.
 * 
 * @author Vasco
 *
 */
public class Message {
    public static final String REQUEST_ROLE = "REQUEST_ROLE";                 // Sent by the client requesting the role to play
    public static final String RESPONSE_ROLE = "RESPONSE_ROLE";               // The Role response
    public static final String REQUEST_ID = "REQUEST_ID";                     // The Client ID requested to the server
    public static final String RESPONSE_ID = "RESPONSE_ID";                   // The Id response
    public static final String RETRY = "RETRY";                               // Retry message on last action
    public static final String ERROR = "ERROR";                               // Default message for error
    public static final String SUCCESS = "SUCCESS";                           // Successfully processed message 
    public static final String REQUEST_SENDING_UDP_PORT = "SENDING_PORT";     // Requested by the Handler to Client 
    public static final String REQUEST_RECEIVING_UDP_PORT = "RECEIVING_PORT"; // Requested by Client to the Handler 
    public static final String FILE_TRANSFERRED = "FILE_TRANSFERRED";         // All UDP packets have been successfully transfered for a file
    public static final String NO_FILES = "NO_FILES";                         // No more files to be transfered
}
