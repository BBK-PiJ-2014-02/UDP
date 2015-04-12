package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import implementation.ServerClientHandlerImpl;
import implementation.ServerImpl;
import interfaces.Server;
import interfaces.ServerClientHandler;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import constants.Role;

/**
 * Testing the ServerClientHandler implementation.
 * 
 * @author Vasco
 *
 */
public class TestServerClientHandler {
	/**
	 * The ServerClientHandler object handler.
	 */
	private ServerClientHandler serverClientHandler;
	
	/**
	 * The Server object handler
	 */
	private Server server;
	
	/**
	 * The unique ID.
	 */
	private UUID UNIQUE_ID = UUID.randomUUID();

	/**
	 * The Initialized role.
	 */
	private Role ROLE = Role.RECEIVER;

	/**
	 * Initializing all required variables before each test.
	 */
	@Before
	public void before() {
		server = new ServerImpl();
		serverClientHandler = new ServerClientHandlerImpl(server, UNIQUE_ID, ROLE);
	}
	
	/**
	 * Exception on null server.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullServerException() {
		serverClientHandler = new ServerClientHandlerImpl(null, UNIQUE_ID, ROLE);
	}

	/**
	 * Exception on null id.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullUniqueIDException() {
		serverClientHandler = new ServerClientHandlerImpl(server, null, ROLE);
	}

	/**
	 * Exception on null role.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullRoleException() {
		serverClientHandler = new ServerClientHandlerImpl(server, UNIQUE_ID, null);
	}

	/**
	 * Testing that the initialized UniqueID is still being returned.
	 */
	@Test
	public void testUniqueID() {
		UUID foundId = serverClientHandler.getUniqueID();
		
		assertNotNull(foundId);
		assertEquals(UNIQUE_ID,foundId);
	}
	
	/**
	 * Testing if returned Role matches the initialized.
	 */
	@Test
	public void testRole() {
		Role foundRole = serverClientHandler.getRole();
		
		assertNotNull(foundRole);
		assertEquals(ROLE, foundRole);
	}
}
