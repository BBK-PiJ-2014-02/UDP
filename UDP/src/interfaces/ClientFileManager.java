package interfaces;

/**
 * Client's File Manager to work out with bucket of files to send 
 * and bucket to where files will be saved into.
 * 
 * Will deal with streaming files, and would aid Client to manage
 * what to stream etc.
 * 
 * @author Vasco
 *
 */
public interface ClientFileManager {
    /**
     * Checks loaded pool and returns false if
     * no more files exist.
     * 
     * @return true if more files
     */
    public boolean hasMoreFilesToSend();

    /**
     * Load the next file from pool to send,
     * and prepares it to be processed in chunks.
     */
    public void loadNextFileToSend();

    /**
     * Get the next chunk converted into a ready-to-send PackageData.
     * 
     * @return PackageData object with chunk.
     */
    public PacketData getNextChunk();

    /**
     * Checks if the file pulling chunks 
     * has reached the end.
     * 
     * @return true if EOF
     */
    public boolean isEOF();

    /**
     * Returns true if Client is sending a file.
     * 
     * @return true if sending
     */
    public boolean isSending();

    /**
     * Saves packet data into file if all packets have been received in 
     * sequence from packet 0 to current, and will close file whenever
     * the last packet is received.
     * 
     * @param packetData
     */
    public void savePacket(PacketData packetData);

    /**
     * Whenever the Server decides to change the role of the client,
     * the client should change to receive a file instead of continuing
     * sending the current. 
     * 
     * Also this could be used by the Client when a file has been finished,
     * or automatically by the ClientFileManager when no more files exist
     * to be sent.
     */
    public void setToReceive();
}
