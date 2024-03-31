package file;

import java.io.*;
import java.util.List;

/**
 * Manages file read and write operations, supporting chunk-based operations
 * to facilitate file transfers in distributed systems.
 */
public class FileManager {

  /**
   * Reads the contents of a file into a byte array.
   *
   * @param filePath The path of the file to read.
   * @return A byte array containing the file's data.
   * @throws IOException If an I/O error occurs.
   */
  public static byte[] readFile(String filePath) throws IOException {
    File file = new File(filePath); // create a new 'File' object with the specified file path
    byte[] data = new byte[(int) file.length()]; // create a byte array to store the file data read from the file input stream

    try (FileInputStream fis = new FileInputStream(file)) { // create a new 'FileInputStream' object with the file object
      int bytesRead = fis.read(data); // read the file data into the byte array
      if (bytesRead != data.length) { // check if the number of bytes read is equal to the expected file size
        throw new IOException("File read fully not completed. Expected bytes: " + data.length + ", but read: " + bytesRead);
      }
      return data;
    } catch (IOException e) {
      System.err.println("IOException in reading file: " + e.getMessage());
      throw e;  // Re-throw the exception to handle it in the caller method.
    }
  }

  /**
   * Writes data to a file.
   *
   * @param filePath The path of the file to write to.
   * @param data The data to write to the file.
   * @throws IOException If an I/O error occurs.
   */
  public static void writeFile(String filePath, byte[] data) throws IOException {
    File file = new File(filePath); // create a new 'File' object with the specified file path

    try (FileOutputStream fos = new FileOutputStream(file)) { // create a new 'FileOutputStream' object with the file object
      fos.write(data); // writes the byte array, 'data' to the file
    } catch (IOException e) {
      System.err.println("IOException in writing file: " + e.getMessage());
      throw e;  // Re-throw the exception to allow the caller to handle it
    }
  }

  /**
   * Writes data to a file in chunks.
   *
   * @param filePath The path of the file to write to.
   * @param chunks The list of file chunks to write to the file.
   * @throws IOException If an I/O error occurs.
   */
  public static void writeFileInChunks(String filePath, List<FileChunk> chunks) throws IOException {
    byte[] fileData = FileChunkUtils.assembleFile(chunks);
    writeFile(filePath, fileData);
  }

  /**
   * Reads a file and breaks it down into chunks.
   *
   * @param filePath The path of the file to read and chunk.
   * @return A list of FileChunk objects representing the chunks of the file.
   * @throws IOException If an I/O error occurs.
   */
  public static List<FileChunk> readFileInChunks(String filePath) throws IOException {
    byte[] fileData = readFile(filePath);
    return FileChunkUtils.chunkFile(fileData);
  }
}
