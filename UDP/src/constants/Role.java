package constants;
/**
 * Constant identifiers for Clients on their role.
 * 
 * @author Vasco
 *
 */
public class Role {
    public static final String RECEIVER = "RECEIVER"; // Client is to receive UDP packets from Server.
    public static final String SENDER   = "SENDER";   // Client is to send UDP packets to Server.
    public static final String SHUTDOWN = "SHUTDOWN"; // Shutdown requested.
}
