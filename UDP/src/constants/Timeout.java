package constants;

public class Timeout {
    public static int UDP_SOCKET_TIMEOUT_DELAY    = 1000;  // The UDP global socket timeout.
    public static int TCP_SOCKET_TIMEOUT_DELAY    = 5000;  // The TCP global socket timeout.
    public static int SERVER_SOCKET_TIMEOUT_DELAY = 5000; // The time after which the Server gives up waiting for new client to connect.
    public static int SLEEP_DELAY                 = 1000;  // The time delay per loop  
}
