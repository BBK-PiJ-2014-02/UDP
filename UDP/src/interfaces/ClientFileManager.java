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
}
