package interfaces;


/**
 * Server-Client Handler initiated by the Server to handle with a Client.
 * Will be sending the role via TCP as well as setting up the UDP Socket
 * to which the Client should communicate with ServerClientHandler.
 * Relays UDP packets between the Server and the Client.
 * 
 * @author Vasco
 *
 */
public interface ServerClientHandler extends Runnable {
	/**
	 * The new Client's role to be set via TCP, requested by the server.
	 * 
	 * @param role
	 */
	public void setRole(String role);
	
	/**
	 * Current Client's role.
	 * 
	 * @return String role
	 */
	public String getRole();
}
