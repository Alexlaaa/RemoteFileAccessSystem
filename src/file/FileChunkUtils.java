package file;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for handling file chunk operations, such as breaking down a file into chunks
 * and reassembling a file from its chunks.
 */
public class FileChunkUtils {

  public static final int CHUNK_SIZE = 1024;  // Example chunk size, adjust as needed

  private FileChunkUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Breaks down the file data into chunks.
   *
   * @param fileData The complete file data as a byte array.
   * @return A list of FileChunk objects representing the chunks of the file.
   */
  public static List<FileChunk> chunkFile(byte[] fileData) {
    List<FileChunk> chunks = new ArrayList<>(); // create a list to store the individual chunks
    int totalChunks = (int) Math.ceil(fileData.length / (double) CHUNK_SIZE); // calculate the total number of chunks needed to break down the file

    for (int i = 0; i < totalChunks; i++) { // iterate over the total number of chunks, creating a new chunk in each iteration
      int start = i * CHUNK_SIZE; // calculate the start index of the current chunk in fileData byte array
      int length = Math.min(fileData.length - start, CHUNK_SIZE); // calculate the length of the current chunk, which may be less than CHUNK_SIZE for the last chunk
      byte[] chunkData = new byte[length]; // create a new byte array to store the current chunk data
      System.arraycopy(fileData, start, chunkData, 0, length); // copy the current chunk data from fileData to chunkData
      chunks.add(new FileChunk(i, chunkData)); // add the current chunk to the list of chunks
    }

    return chunks;
  }

  /**
   * Reassembles the file from its chunks.
   *
   * @param chunks The list of FileChunk objects to be assembled into a file.
   * @return The complete file data as a byte array.
   */
  public static byte[] assembleFile(List<FileChunk> chunks) {
    chunks.sort(Comparator.comparingInt(FileChunk::getSequenceNumber)); // Sort chunks by their sequence number to ensure they are in order

    int totalSize = chunks.stream().mapToInt(chunk -> chunk.getData().length).sum(); // calculate the total size of the reassembled file by summing the sizes of all chunks
    byte[] fileData = new byte[totalSize]; // create a new byte array to store the reassembled file data with the total size
    int pointer = 0; // create a pointer to keep track of the current position in the fileData array where the next chunk data should be copied

    for (FileChunk chunk : chunks) { // iterate over the list of chunks
      System.arraycopy(chunk.getData(), 0, fileData, pointer, chunk.getData().length); // copy the data of the current chunk to the fileData array at the current pointer position
      pointer += chunk.getData().length; // update the pointer to the next position for the next chunk data
    }

    return fileData;
  }
}
