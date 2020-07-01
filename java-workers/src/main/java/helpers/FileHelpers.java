package helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
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
      System.out.println("An error occurred while writing.");
      System.out.println(e);
      e.printStackTrace();
    }
  }

  // assumes we're reading from resources folder
  public static String read (String filename)
		throws IOException {
			System.out.println("about to get file input stream");
			String filepath = getFilePath(filename);

			FileInputStream fis = new FileInputStream(filepath);
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
				System.out.println("Error getting file path");
				System.out.println(e);
        throw new IOException();
      }
  }

	// returns a string of all files recursively, when given a directory
	// TODO untested
  public static List<String> getFilenamesFor (List<File> files) {
    ArrayList<String> fileNames = new ArrayList<String>();
    for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());

        List<File> nextLayerFiles = Arrays.asList(file.listFiles());
				getFilenamesFor(nextLayerFiles); // Calls same method again.

			} else {
				fileNames.add(file.getName());
				System.out.println("File: " + file.getName());
			}
    }

    return fileNames;
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
