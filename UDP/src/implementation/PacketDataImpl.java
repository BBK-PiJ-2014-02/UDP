package implementation;

import interfaces.PacketData;

/**
 * The Packet Data implementation
 * 
 * @author Vasco
 *
 */
public class PacketDataImpl implements PacketData {
	/**
	 * This packet data.
	 */
	private final byte[] data;
	
	/**
	 * This packet sequence id.
	 */
	private final int id;
	
	/**
	 * The total packets.
	 */
	private final int totalIds;
	
	/**
	 * The unique UID group for which this packet belongs to.
	 */
	private final String UID;
	
	/**
	 * Constructor.
	 * 
	 * @param data to be transferred
	 * @param id sequence for this packet
	 * @param totalIds for this file.
	 */
	public PacketDataImpl(byte[] data, int id, int totalIds, String UID) {
		this.data = data;
		this.id = id;
		this.totalIds = totalIds;
		this.UID = UID;
	}

	/**
	 * The packet id.
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * The total packets to be transmitted.
	 */
	@Override
	public int getTotalPackets() {
		return totalIds;
	}

	/**
	 * This packet data.
	 */
	@Override
	public byte[] getData() {
		return data;
	}

	/**
	 * The unique ID group for this packet.
	 */
	@Override
	public String getUID() {
		return UID;
	}

}