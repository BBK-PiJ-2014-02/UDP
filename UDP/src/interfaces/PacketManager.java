package interfaces;
/**
 * Receives and Sends PacketData objects via UDP
 * 
 * @author Vasco
 *
 */
public interface PacketManager {
    /**
     * Receives via UDP the object PacketData from given hostName and port.
     */
    public PacketData receive();

    /**
     * Sends via UDP the object PacketData.
     * 
     * @param packetData the PacketData
     */
    public void send(PacketData packetData);
}
