package interfaces;
/**
 * Server creates new instances of ServerClientHandler at each new Client connection.
 * 
 * First connected Client is set to be the sender of UDP packets to be sent to all
 * other CLients. When sender stops sending packets, the next Client is elected to 
 * be the next sender, and so on.
 * 
 * ServerClientHandler relays all UDP packet data flow between the Server and the
 * Client. 
 * 
 * @author Vasco
 *
 */
public interface Server extends Runnable {
	/**
	 * Sends a shutdown signal.
	 */
	public void shutdown();
}
