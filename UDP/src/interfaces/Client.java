package interfaces;
/**
 * Client connects to Server via TCP, then requests an unique ID, a role
 * and also a Socket to start receiving or sending UDP packets from/to.
 * 
 * The Role can be changed via a TCP request from the Server.
 * 
 * @author Vasco
 *
 */
public interface Client extends Runnable {
    /**
     * The Listener will receive messages and relay them to the Client
     * to be executed according to expectations.
     * 
     * @param message from TCPClientListener
     */
    public void receivingTCPMessage(String message);
}
