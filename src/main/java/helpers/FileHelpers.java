package helpers;

import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;

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
  public String read(String filename)
		throws IOException {
			FileInputStream fis = new FileInputStream(getFilePath(filename));
    	String contents = IOUtils.toString(fis, "UTF-8");

			return contents;
	}
  
  // note that filename can also be a relative path, relative to the class path, eg "podcast-data/my-file.json"
  public static String getFilePath (String filename) {
    return Paths.get(ClassLoader.getSystemResource(filename).toURI()).toString();
  }
  // finds a file from the classpath (ie target/classes)
  // this is where resources and our src/main/java files get copied into, so this is more general
	public static String getFile(String filepath) {
		// gets the classpath
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// finds file (relative to class path, eg target/classes/...)
		InputStream input = classLoader.getResourceAsStream(filepath);

		return input;
  }

  // NOTE no longer using
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
