package helpers;

import java.io.File;
import java.io.FileWriter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;
import org.apache.commons.io.IOUtils;
import java.net.URL;

import java.io.IOException; 
import java.net.URISyntaxException;

import helpers.HttpReq;


public class FileHelpers {
  // assumes we're writing to resources folder
  public static void write(String filename, String content) {
    try {
			FileWriter writer = new FileWriter(getFilePath(filename));
			writer.write(content);
			writer.close();

			System.out.println("File created: " + filename);

    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  // assumes we're reading from resources folder
  public static String read(String filename)
		throws IOException {
			FileInputStream fis = new FileInputStream(getFilePath(filename));
    	String contents = IOUtils.toString(fis, "UTF-8");

			return contents;
	}
  
  // note that filename can also be a relative path, relative to the class path, eg "podcast-data/my-file.json"
  // https://stackoverflow.com/questions/17351043/how-to-get-absolute-path-to-file-in-resources-folder-of-your-project
  public static String getFilePath (String filename) 
    throws IOException { 
      try  {
        // TODO find more robust /dynamic way to access these files that were originally in the src/main/resources folder
        File file = new File("target/classes/" + filename);
        String absolutePath = file.getAbsolutePath();

        return absolutePath;

      } catch (Exception e) {
        // not sure what Exception would be caught; set that once I find out
        // TODO figure out how to handle this case...for now just throwing what its callers would throw, for ease of use
        throw new IOException();
      }
  }

	// returns a string of all files recursively, when given a directory
  public static ListArray<String> getFilenamesFor(File[] files) {
    fileNames = new ArrayList<String>();
    for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				showFiles(file.listFiles()); // Calls same method again.

			} else {
				System.out.println("File: " + file.getName());
			}
    }
	}

  // NOTE no longer using
  // returns path to src resource directory
  public static String getResourcesDir() {
    Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
    Path filePath = Paths.get(root.toString(), "target", "main", "resources");

    return filePath.toString();
  }

  // NOTE no longer using
  // finds a file from our source resource directory
  public static String getResourceFilePath(String filename) {
    return getResourcesDir() + "/" + filename;
  }

}
