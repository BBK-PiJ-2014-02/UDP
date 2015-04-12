package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import implementation.ServerClientHandlerImpl;
import implementation.ServerImpl;
import interfaces.Server;
import interfaces.ServerClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import org.junit.After;
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
	 * The Client Socket
	 */
	private Socket clientSocket;
	
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
		clientSocket = new Socket();
		serverClientHandler = new ServerClientHandlerImpl(server, clientSocket, UNIQUE_ID, ROLE);
	}
	
	@After
	public void after() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Exception on null server.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullServerException() {
		serverClientHandler = new ServerClientHandlerImpl(null, clientSocket, UNIQUE_ID, ROLE);
	}

	/**
	 * Exception on null client Socket.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullClientSocketException() {
		serverClientHandler = new ServerClientHandlerImpl(null, clientSocket, UNIQUE_ID, ROLE);
	}

	/**
	 * Exception on null id.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullUniqueIDException() {
		serverClientHandler = new ServerClientHandlerImpl(server, clientSocket, null, ROLE);
	}

	/**
	 * Exception on null role.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testNullRoleException() {
		serverClientHandler = new ServerClientHandlerImpl(server, clientSocket, UNIQUE_ID, null);
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
