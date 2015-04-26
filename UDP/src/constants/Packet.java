package constants;

public class Packet {
    /**
     * The actual data size.
     */
    public static final int CHUNK_SIZE = 1024;

    /**
	 *  The max size for each packet between Server and Client.
	 *  Size of this will be for the serialized PacketData which
	 *  will include one chunck size.
	 */
    public static final int PACKET_SIZE = CHUNK_SIZE + 256;
}
