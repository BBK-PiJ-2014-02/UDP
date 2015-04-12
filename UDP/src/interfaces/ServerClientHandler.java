package interfaces;

import java.util.UUID;

import constants.Role;

/**
 * Server-Client Handler is initiated by the Server and
 * will handle Clients on their role.
 * 
 * @author Vasco
 *
 */
public interface ServerClientHandler extends Runnable {
	/**
	 * The unique client identifier.
	 * 
	 * @return
	 */
    UUID getUniqueID();
    
    /**
     * The Client's current role.
     * Client's can be receivers or senders.
     * 
     * @return
     */
    Role getRole();
}
