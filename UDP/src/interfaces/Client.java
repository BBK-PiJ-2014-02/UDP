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
}
