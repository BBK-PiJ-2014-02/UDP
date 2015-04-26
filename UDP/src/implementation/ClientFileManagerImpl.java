package implementation;

import interfaces.ClientFileManager;
import interfaces.FileManager;
import interfaces.PacketData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import constants.Packet;

/**
 * Manages files to send, dealing with chunks to be built.
 * 
 * @author Vasco
 *
 */
public class ClientFileManagerImpl implements ClientFileManager {
    /**
     * The list of files available to send whenever required.
     */
    protected List<File> sendingFileList;
    
    /**
     * The path to the receiving bucket
     */
    protected final String receivingBucketPath;
    
    /**
     * The current file being streamed in chunks.
     */
    private File streamingFile;

    /**
     * Streaming file from the disk in small chunks.
     */
    private FileInputStream fileInputStream;

    /**
     * Flag to tell if end of file was reached.
     */
    private boolean isEOF = true;
    
    /**
     * Flag to tell if Client is sending a file or receiving one.
     */
    private boolean isSending = false;
    
    /**
     * The current chunk id being extracted.
     */
    private int chunkId;

    /**
     * Total chunks required to stream current file.
     */
    private int totalChunks;

    /**
     * List of packets received stored in order.
     * File will be saved to disk when completed.
     */
    private List<PacketData> receivedPacketList;

    /**
     * FileManager for this Client.
     */
    private final FileManager fileManager = new FileManager(){};
    
    /**
     * Constructor
     * 
     * @param sendingBucket the sending bucket path
     * @param receivingBucket the receiving bucket path
     */
    public ClientFileManagerImpl(String sendingBucket, String receivingBucket) {
        // Initialize the sending file list
        this.sendingFileList = fileManager.getFileList(sendingBucket);
        this.receivingBucketPath = receivingBucket;
        this.chunkId = 1;
        if ( hasMoreFilesToSend() ) loadNextFileToSend();
    }

    /**
     * Quick check if there are more files left in queue to send.
     */
    @Override
    public boolean hasMoreFilesToSend() {
        // If more files to send, say yes.
        if ( sendingFileList.size() > 0 ) return true;

        // If no more files to be sent, set mode to receive.
        setToReceive();
        
        // No more files to send, mode set to receive.
        return false;
    }

    /**
     * Loads the next file in queue and removes it from queue.
     */
    @Override
    public void loadNextFileToSend() {
        // Cannot call this without first checking if there are more files to send.
        if ( ! hasMoreFilesToSend() ) throw new IllegalStateException("No more files to send.");
        
        // Loads into the streaming file the next file to send
        // and removes it from the list.
        this.streamingFile = sendingFileList.remove(0);
        
        // reset flags, close handlers, etc for new file
        resetToNewFileToSend();

        // Get the File Input Stream ready.
        try {
            fileInputStream = new FileInputStream(streamingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Calculate the total chunks required for this file.
        calculateTotalChunks(streamingFile);
    }

    /**
     * Gets the next Chunk bytes from currently loading file and 
     * returns a PacketData ready to be sent object.
     */
    @Override
    public PacketData getNextChunk(String UID) {
        // Ensure developers make the right thing all the time.
        if ( isEOF() ) throw new IllegalStateException("No more chunks to pull. End of file reached.");

        // The data to be returned with the universally set chunk size.
        byte[] dataToReturn = new byte[Packet.CHUNK_SIZE];

        // This is to check if we are getting to the end of the file.
        int bytesRead = 0;

        // Read the next chunk
        try {
            bytesRead = fileInputStream.read(dataToReturn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if we have reached the end of the file
        if ( bytesRead < dataToReturn.length ) {
            this.isEOF = true;
        }

        // Instantiate a new PacketData Object
        PacketData packet = new PacketDataImpl(dataToReturn, chunkId, totalChunks, UID, streamingFile.toString());

        // Increment chunk for next to come
        this.chunkId++;

        // Return the PacketData object.
        return packet;
    }

    /**
     * Return true if end of file was reached.
     */
    @Override
    public boolean isEOF() {
        return isEOF;
    }

    /**
     * Takes the given file and calculates the amount of chunks required to be sent
     * if each chunk would have the universally set Packet.CHUNK_SIZE
     * 
     * @param streamingFile the file
     */
    private void calculateTotalChunks(File streamingFile) {
        try {
            // Total bytes
            double totalBytes = fileInputStream.available();

            // Chuck size set
            double chunkSize  = Packet.CHUNK_SIZE;

            // Round-up on how many chunks are required to send all bytes
            totalChunks = (int) Math.ceil( ( totalBytes / chunkSize ) );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * When a new file is to be loaded from beginning, 
     * reset all required flags from old file as well close any open hanlders.
     */
    private void resetToNewFileToSend() {
        // Reset the end of file flag.
        this.isEOF = false;
        
        // Set the sending flat
        this.isSending = true;
        
        // Reset the chunk counter
        this.chunkId = 1;

        // Close the FileInputStream if open
        if ( fileInputStream != null ) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return true if sending a file.
     */
    @Override
    public boolean isSending() {
        return isSending;
    }

    /**
     * Saves packet data into file if all packets have been received in 
     * sequence from packet 1 to current, and will close file whenever
     * the last packet is received.
     */
    @Override
    public void savePacket(PacketData packetData) {
        // Deal with null packet
        if ( packetData == null ) throw new IllegalArgumentException("Cannot save a null packet");

        // First time running, initialize the received packet list.
        if ( this.receivedPacketList == null ) this.receivedPacketList = new LinkedList<PacketData>();

        // If no first element, the list is empty. Add the new packet and job done.
        if ( receivedPacketList.size() == 0 ) receivedPacketList.add(packetData);
        else {
            // Get the first element if exists.
            PacketData firstElement = receivedPacketList.get(0);
            String currentUID = firstElement.getUID();
            String receivedUID = packetData.getUID();

            // This is the same file still. Add it in the sequence.
            if ( currentUID.equals(receivedUID) ) {

                // The index to add this new packet to.
                int addAtIndex = 0;

                // This packet id.
                int packetId = packetData.getId();

                // Find the first index higher than current packet id
                for ( PacketData currentPacket : receivedPacketList ) {

                    // Our new packet should go on a higher index
                    if ( packetId > currentPacket.getId() ) addAtIndex++;
                    // This element already exists.
                    else if ( packetId == packetData.getId() )  {
                        addAtIndex = -1;
                        break;
                    }
                    // This is the first element with higher packet id.
                    else break;
                }

                // Add the packet on the correct index to keep packets sorted unless already exists.
                if ( addAtIndex >= 0 ) receivedPacketList.add(addAtIndex, packetData);
            }
            // This is a new file, start new.
            else {
                receivedPacketList = new LinkedList<PacketData>();
                receivedPacketList.add(packetData);
            }
        }

        // Check if complete and ready to be saved.
        if ( receivedPacketList.get(0).getTotalPackets() == receivedPacketList.size() ) 
            fileManager.saveFile(receivedPacketList);
    }

    /**
     * Whenever the Server decides to change the role of the client,
     * the client should change to receive a file instead of continuing
     * sending the current. 
     * 
     * Also this could be used by the Client when a file has been finished,
     * or automatically by the ClientFileManager when no more files exist
     * to be sent.
     */
    @Override
    public void setToReceive() {
        // Add file back to sending list as a special case 
        // when the file was not yet sent completely.
        if ( !isEOF() ) this.sendingFileList.add(streamingFile);
        
        // Set sending flag to false.
        isSending = false;

        // Reset the streaming file
        streamingFile = null;
    }
}