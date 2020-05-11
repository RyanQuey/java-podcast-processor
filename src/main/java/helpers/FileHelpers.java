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

  public static String getResourcesDir() {
    Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
    Path filePath = Paths.get(root.toString(), "src", "main", "resources");

    return filePath.toString();
  }

  public static String getFilePath(String filename) {
    return getResourcesDir() + "/" + filename;
  }
}
