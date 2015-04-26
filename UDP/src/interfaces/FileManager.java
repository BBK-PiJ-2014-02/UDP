package interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the required processes linked to managing files.
 * 
 * @author Vasco
 *
 */
public interface FileManager {
    /**
     * Reads from the bucket path String for all files found
     * and returns them all in a form of a List.
     * 
     * @param bucket the path to files
     * @return List of Files
     */
    public static List<File> getFileList(String bucket) {
        if ( bucket == null ) throw new IllegalArgumentException("No bucket supplied.");
        Path dir = Paths.get(bucket);

        List<File> fileListInBucket = new LinkedList<File>();

        // Pull all files from given bucket directory.
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir);

            // Add files into the list
            for ( Path path : stream ) {
                fileListInBucket.add(path.toFile());
            }
            stream.close();

        } catch (NoSuchFileException e) {
            e.printStackTrace();

        } catch (IOException e ) {
            e.printStackTrace();
        }

        return fileListInBucket;
    }

    /**
     * From a list of packets, saves the data of each into given file.
     * 
     * @param packetList the list of packets
     * @param file the to store info into
     */
    public static void saveFile(List<PacketData> packetList) {
        // Dealing with all exceptions.
        if ( packetList == null ) throw new IllegalArgumentException("Cannot save a null packet list to file.");
        if ( packetList.size() == 0 ) throw new IllegalArgumentException("Cannot save an emtpy packet list to file.");

        // Get the original path and file names
        String originalPathFile = packetList.get(0).getUID();

        // Convert it into a File type object
        File originalFile = new File(originalPathFile);

        // Collect the original path, move up to parent twice such that:
        // ../something/send/file.txt becomes ../something/receive/file.txt 
        Path originalPath = originalFile.toPath().getParent().getParent();
        String fullPath = originalPath.toString() + File.separatorChar + "receive" + File.separatorChar;

        // Collect only the file name.
        String originalFileName = originalFile.getName();

        // Append the file name to use to the calculated path
        File file = new File(fullPath + originalFileName);

        // Create the file if it does not exists.
        if ( !file.exists() ) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for( PacketData packet : packetList ) {
            byte[] data = packet.getData();
            try {
                fileOutputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    };
}
