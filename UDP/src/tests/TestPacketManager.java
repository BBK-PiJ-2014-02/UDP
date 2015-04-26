package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import implementation.PacketDataImpl;
import interfaces.PacketData;
import interfaces.PacketManager;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Test;

/**
 * Testing the PacketManager static methods.
 * 
 * @author Vasco
 *
 */
public class TestPacketManager {
    /**
     * The data to test with.
     */
    private final byte[] DATA = "This is the data to be sent".getBytes();
    
    /**
     * The id.
     */
    private final int ID = 24;
    
    /**
     * The total packets
     */
    private final int TOTAL_PACKETS = 212;
    
    /**
     * The UID.
     */
    private final String UID = "filename.txt";

    /**
     * The path file name.
     */
    private final String PATH_FILENAME = "something/to/something";

    /**
     * Testing both sending and receiving.
     */
    @Test
    public void testPacketManager() {
        // Open a Datagram socket for sending and another for receiving
        DatagramSocket sendingSocket = null;
        DatagramSocket receivingSocket = null;
        try {
            sendingSocket = new DatagramSocket();
            receivingSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // Get the localHost
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Get the receiving port where the datagram should be sent from the sending socket.
        int recevingPort = receivingSocket.getLocalPort();

        // Create a PacketData object with the data to be sent
        PacketData packet = new PacketDataImpl(DATA, ID, TOTAL_PACKETS, UID, PATH_FILENAME);

        PacketManager.send(sendingSocket, packet, localHost, recevingPort);

        // Collect the packet at the receiving socket 
        PacketData foundPacket = PacketManager.receive(receivingSocket);

        assertNotNull(foundPacket);
        assertEquals(new String(DATA), new String(foundPacket.getData()));
        assertEquals(ID, foundPacket.getId());
        assertEquals(TOTAL_PACKETS, foundPacket.getTotalPackets());
        assertEquals(UID, foundPacket.getUID());
        
        receivingSocket.close();
        sendingSocket.close();
    }
}
