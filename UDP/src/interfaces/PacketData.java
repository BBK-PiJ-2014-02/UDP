package interfaces;
/**
 * The packet data for a chunk of a file being streamed.
 * 
 * @author Vasco
 *
 */
public interface PacketData {
	/**
	 * The Packet id.
	 * Packet ids refer to the order they should be
	 * processed at the receiver side.
	 * 
	 * @return int id
	 */
	public int getId();
	
	/**
	 * The total packets to be transmitted.
	 * 
	 * @return int total packets
	 */
	public int getTotalPackets();
	
	/**
	 * The packet data.
	 * 
	 * @return byte array
	 */
	public byte[] getData();

	/**
	 * The unique UID group this packet belong to.
	 * Used to identify to which file this chunk 
	 * belongs.
	 * 
	 * @return UID string
	 */
	public String getUID();
}
