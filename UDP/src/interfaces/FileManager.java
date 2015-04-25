package interfaces;

import java.io.File;
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

        // Pull all test file from the default directory.
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
}
