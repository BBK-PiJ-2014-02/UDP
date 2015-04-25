package interfaces;

import java.io.File;
import java.util.List;

/**
 * Manages the required processes linked to managing files.
 * 
 * @author Vasco
 *
 */
public abstract interface FileManager {
	/**
	 * Reads from the bucket path String for all files found
	 * and returns them all in a form of a List.
	 * 
	 * @param bucket the path to files
	 * @return List of Files
	 */
    public static List<File> getFileList(String bucket) {
//    	if ( bucket)
    	return null;
    }
}
