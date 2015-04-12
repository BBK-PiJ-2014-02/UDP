package tests;

import static org.junit.Assert.*;
import implementation.ServerClientHandlerImpl;
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
		serverClientHandler = new ServerClientHandlerImpl(UNIQUE_ID, ROLE);
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
