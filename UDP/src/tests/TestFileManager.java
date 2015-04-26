package tests;

import static org.junit.Assert.*;
import interfaces.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * The FileManager unit tests.
 * 
 * @author Vasco
 *
 */
public class TestFileManager {
    /**
     * The good path to files.
     */
    private final String PATH = 
            "."         + File.separatorChar+
            "src"       + File.separatorChar+
            "tests"     + File.separatorChar+
            "resources" + File.separatorChar+
            "send"      + File.separatorChar;

    /**
     * The Final Manager using the abstract default methods
     */
    private final FileManager fileManager = new FileManager() { };

    /**
     * This expected file List.
     */
    private List<File> fileList;

    @Before
    public void before() {
        // Initialize fileList
        fileList = new LinkedList<File>();

        // Pull all test file from the default directory.
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(Paths.get(PATH));

            // Add files into the list
            for ( Path path : stream ) {
                fileList.add(path.toFile());
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Check null arguments throw exception.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGetFileListNull() {
        fileManager.getFileList(null);
    }
    
    /**
     * Test invalid path return exception.
     */
    @Test
    public void testGetFileListInvalidPath() {
        fileManager.getFileList("no path at all");
    }
    
    /**
     * Test list of files is returned from a valid path
     */
    @Test
    public void testGetFileList() {
        List<File> foundListFiles = fileManager.getFileList(PATH);
        assertNotNull(foundListFiles);
        verify(fileList, foundListFiles);
    }

    /**
     * Check if same elements of expected exist in found.
     * 
     * @param expected List File
     * @param found List File
     */
    private void verify(List<File> expected, List<File> found) {
        // Check same size.
        assertEquals(expected.size(),found.size());
        
        // Check each of the expected element is in found list.
        for( File expectedfile : expected ) {
            assertTrue(found.contains(expectedfile));
        }
    }
}
