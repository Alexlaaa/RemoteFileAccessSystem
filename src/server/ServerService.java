package server;

import common.Constants.StatusCode;
import common.Request;
import common.Response;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * ServerService handles the processing of different types of requests from the client, including
 * reading from, writing to, and monitoring files based on the details provided in the request.
 */
public class ServerService {

  /**
   * Processes incoming requests from clients and generates appropriate responses based on the
   * operation type specified in the request.
   *
   * @param request The request from the client.
   * @return A response object that contains the result of the operation.
   */
  public Response processRequest(Request request) { // TODO: Add the 2 additional custom operations
    switch (request.getOperationType()) {
      case READ:
        return handleReadRequest(request);
      case WRITE:
        return handleWriteRequest(request);
      case MONITOR:
        // Monitoring logic will be implemented separately.
        return handleMonitorRequest(request);
      default:
        return new Response(StatusCode.INVALID_OPERATION, null, "Invalid operation type.");
    }
  }

  /**
   * Handles a read request by reading a specific segment of a file.
   *
   * @param request The read request containing the file path, offset, and the number of bytes to
   *                read.
   * @return A response object containing the read data or an error message.
   */
  private Response handleReadRequest(Request request) {
    try (RandomAccessFile file = new RandomAccessFile(request.getFilePath(), "r")) {
      file.seek(
          request.getOffset());  // Sets the file pointer to the specified offset, determining where to start reading
      byte[] data = new byte[(int) request.getBytesToRead()]; // Creates a byte array to store the read data
      int bytesRead = file.read(data); // Stores the number of bytes read from the file
      if (bytesRead
          != request.getBytesToRead()) { // Indicates that the read operation was incomplete
        // Possible due to reaching the end of the file before reading the expected number of bytes
        return new Response(StatusCode.READ_INCOMPLETE, Arrays.copyOf(data, bytesRead),
            "Read partially successful.");
      }
      return new Response(StatusCode.READ_SUCCESS, data,
          "Read successful."); // Read operation was successful
    } catch (IOException e) {
      return new Response(StatusCode.READ_ERROR, null,
          "Error reading file: " + e.getMessage()); // Error occurred during read operation
    }
  }

  /**
   * Handles a write request by inserting data into a file at a specified offset.
   *
   * @param request The write request containing the file path, offset, and the data to write.
   * @return A response object indicating the success or failure of the write operation.
   */
  private Response handleWriteRequest(Request request) {
    try (RandomAccessFile sourceFile = new RandomAccessFile(request.getFilePath(), "rw")) {
      long offset = request.getOffset(); // The offset at which to start writing the data
      byte[] data = request.getData(); // The data to be written to the file
      long fileSize = sourceFile.length(); // The size of the file in bytes
      if (offset > fileSize) { // Checks if the offset exceeds the file size
        return new Response(StatusCode.WRITE_ERROR, null, "Offset exceeds file size.");
      }
      // Reads the content after the offset and stores it temporarily to avoid overwriting during data insertion
      byte[] buffer = new byte[1024]; // Buffer to store the read data
      int bytesRead; // Number of bytes read
      sourceFile.seek(offset); // Sets the file pointer to the specified offset
      File tempFile = new File(
          request.getFilePath() + ".tmp"); // Temporary file to store the content after the offset
      try (RandomAccessFile temp = new RandomAccessFile(tempFile,
          "rw")) { // Creates a temporary file
        while ((bytesRead = sourceFile.read(buffer)) != -1) { // Reads the content after the offset
          temp.write(buffer, 0, bytesRead); // Writes the read content to the temporary file
        }
        sourceFile.seek(offset); // Sets the file pointer back to the offset
        sourceFile.write(data); // Writes the new data to the file
        temp.seek(0); // Sets the file pointer to the beginning of the temporary file
        while ((bytesRead = temp.read(buffer)) != -1) { // Reads the content from the temporary file
          sourceFile.write(buffer, 0, bytesRead); // Writes the content back to the original file
        }
      }
      if (tempFile.exists()) {
        tempFile.delete();
      }
      return new Response(StatusCode.WRITE_SUCCESS, null, "Write successful.");
    } catch (IOException e) {
      return new Response(StatusCode.WRITE_ERROR, null, "Error writing to file: " + e.getMessage());
    }
  }

  /**
   * Placeholder for handling monitor requests... TODO!!
   *
   * @param request The monitoring request.
   * @return A response indicating that monitoring is not yet implemented.
   */
  private Response handleMonitorRequest(Request request) {
    // Placeholder response until monitor implementation is complete.
    return new Response(StatusCode.MONITOR_ERROR, null, "Monitor functionality not implemented.");
  }

  // TODO: Implement the 2 additional custom operations
}
