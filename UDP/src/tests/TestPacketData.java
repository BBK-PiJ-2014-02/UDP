package tests;

import static org.junit.Assert.*;
import implementation.PacketDataImpl;
import interfaces.PacketData;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the PacketData Implementation.
 * 
 * @author Vasco
 *
 */
public class TestPacketData {
	/**
	 * The data.
	 */
	private final byte[] DATA = "Some data".getBytes();
	
	/**
	 * The packet id.
	 */
	private final int ID = 2;
	
	/**
	 * The total packets.
	 */
	private final int TOTAL_IDS = 10;
	
	/**
	 * The unique ID.
	 */
	private final String UID = "qwer-24-er";

	/**
	 * The path filename.
	 */
	private final String PATH_FILENAME = "blah/bleh/bliss.ping";

	/**
	 * The PacketData handler.
	 */
	private PacketData packetDataHanlder;

	/**
	 * Test initializations.
	 */
	@Before
	public void before() {
		this.packetDataHanlder = new PacketDataImpl(DATA, ID, TOTAL_IDS, UID, PATH_FILENAME);
	}
	
	/**
	 * Check same Data.
	 */
	@Test
	public void test() {
		byte[] found = packetDataHanlder.getData();
		assertNotNull(found);
		assertEquals(DATA,found);
	}
	
	/**
	 * Check id.
	 */
	@Test
	public void testID() {
		int found = packetDataHanlder.getId();
		assertEquals(ID, found);
	}

	/**
	 * Check total ids.
	 */
	@Test
	public void testTotalIds() {
		int found = packetDataHanlder.getTotalPackets();
		assertEquals(TOTAL_IDS, found);
	}

	/**
	 * Check unique id.
	 */
	@Test
	public void testUniqueId() {
		String found = packetDataHanlder.getUID();
		assertEquals(UID, found);
	}

	/**
	 * Check path filename.
	 */
	@Test
	public void testPathFilename() {
		String found = packetDataHanlder.getPathFilename();
		assertEquals(PATH_FILENAME, found);
	}
}
