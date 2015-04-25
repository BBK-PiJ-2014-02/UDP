package interfaces;

/**
 * Receives and Sends PacketData objects via UDP
 * 
 * @author Vasco
 *
 */
public interface PacketManager {
	/**
	 * Receives via UDP the object PacketData from given hostName and port
	 * 
	 * @param packetData the PacketData
	 * @param hostName the host
	 * @param port
	 */
	public static PacketData receive(PacketData packetData, String hostName, int port) {
		return null;
	}
	

	/**
	 * Sends via UDP the object PacketData to hostName on port
	 * 
	 * @param packetData the PacketData
	 * @param hostName the host
	 * @param port
	 */
	public static void send(PacketData packetData, String hostName, int port) { }

}
