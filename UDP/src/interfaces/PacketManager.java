package interfaces;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import constants.Packet;

/**
 * Receives and Sends PacketData objects via UDP
 * 
 * @author Vasco
 *
 */
public interface PacketManager {
    /**
     * Receives via UDP the object PacketData from given hostName and port.
     * 
     * @param socket DatagramSocket
     */
    public static PacketData receive(DatagramSocket socket) {
        try {
            // Allocate a dataStream byte array to load.
            byte[] dataStream = new byte[Packet.PACKET_SIZE];

            // Get a Datagram Packet ready for it.
            DatagramPacket packetStream = new DatagramPacket(dataStream, dataStream.length);

            // Receive the packet.
            socket.receive(packetStream);

            // Prepare a new byte array input stream to be received via UDP
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            // Convert the stream into the expected PacketData Object.
            PacketData packetData = (PacketData) objectInputStream.readObject();

            // Job done.
            return packetData;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // For whenever the above goes South.
        return null;
    }

    /**
     * Sends via UDP the object PacketData to hostName on port.
     * 
     * @param packetData the PacketData
     * @param hostName the host
     * @param port the port
     */
    public static void send(DatagramSocket socket, PacketData packetData, InetAddress localHost, int sendingPort) { 
        // Prepare a new byte array output stream to be sent via UDP
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Convert the PacketData object into a stream.
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream));
            objectOutputStream.flush();
            objectOutputStream.writeObject(packetData);
            objectOutputStream.flush();

            // Get the array of bytes to be sent.
            byte[] packetStream = byteArrayOutputStream.toByteArray();

            // Send
            socket.send(new DatagramPacket(packetStream, packetStream.length, localHost, sendingPort));

            // Close Output Stream
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
