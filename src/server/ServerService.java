package server;

import common.Constants.StatusCode;
import common.Request;
import common.Response;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
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
  public Response processRequest(Request request) {
    return switch (request.getOperationType()) {
      case READ -> handleReadRequest(request);
      case WRITE_INSERT -> handleWriteInsertRequest(request);
      case MONITOR ->
        // Monitoring logic will be implemented separately.
          handleMonitorRequest(request);
      case WRITE_DELETE -> handleWriteDeleteRequest(request);
      case FILE_INFO -> handleFileInfoRequest(request);
      default -> new Response(StatusCode.INVALID_OPERATION, null, "Invalid operation type.", -1);
    };
  }

  /**
   * Handles a read request by reading a specific segment of a file.
   *
   * @param request The read request containing the file path, offset, and the number of bytes to
   *                read.
   * @return A response object containing the read data or an error message.
   */
  private Response handleReadRequest(Request request) {
    File file = new File(request.getFilePath());
    if (!file.exists()) {
      return new Response(StatusCode.READ_ERROR, null, "File does not exist.", -1);
    }

    try (RandomAccessFile sourceFile = new RandomAccessFile(file, "r")) {
      if (request.getOffset() >= sourceFile.length()) {
        return new Response(StatusCode.READ_ERROR, null,
            "Offset is beyond the file length.", -1);
      }

      // Sets the file pointer to the specified offset, determining where to start reading the file
      sourceFile.seek(request.getOffset());

      // Creates a byte array to store the read data
      byte[] data = new byte[(int) request.getBytesToReadOrDelete()];

      // Stores the number of bytes read from the file
      int bytesRead = sourceFile.read(data);

      // Check for possibility of incomplete operation due to reaching EOF before reading expected number of bytes
      if (bytesRead != request.getBytesToReadOrDelete()) {
        String message = "Read partially successful // File Length -> " + sourceFile.length();
        return new Response(StatusCode.READ_INCOMPLETE, Arrays.copyOf(data, bytesRead),
            message, -1);
      }

      // Include the file length in the response message
      String message = "Read successful // File Length -> " + sourceFile.length();

      // Read operation was successful
      return new Response(StatusCode.READ_SUCCESS, data,
          message, file.lastModified()); // Include the last modified time of the file
    } catch (IOException e) {
      return new Response(StatusCode.READ_ERROR, null,
          "Error reading file: " + e.getMessage(), -1);
    }
  }

  /**
   * Handles a write insert request by inserting data into a file at a specified offset.
   *
   * @param request The write request containing the file path, offset, and the data to write.
   * @return A response object indicating the success or failure of the write operation.
   */
  private Response handleWriteInsertRequest(Request request) {
    File file = new File(request.getFilePath());
    if (!file.exists()) {
      return new Response(StatusCode.READ_ERROR, null, "File does not exist.", -1);
    }

    try (RandomAccessFile sourceFile = new RandomAccessFile(file, "rw")) {
      long offset = request.getOffset(); // The offset at which to start writing the data
      byte[] data = request.getData(); // The data to be written to the file

      // Idea: To read the content after the offset and stores it temporarily to avoid overwriting during data insertion

      byte[] buffer = new byte[1024]; // Buffer to store the read data
      int bytesRead; // Number of bytes read

      // Sets the file pointer to the specified offset
      sourceFile.seek(offset);

      // Create a temporary file to store the content after the offset
      File tempFile = new File(request.getFilePath() + ".tmp");
      try (RandomAccessFile temp = new RandomAccessFile(tempFile, "rw")) {
        // Reads the content after the offset
        while ((bytesRead = sourceFile.read(buffer)) != -1) {
          // Writes the read content to the temporary file
          temp.write(buffer, 0, bytesRead);
        }

        // Sets the file pointer back to the offset
        sourceFile.seek(offset);

        // Writes the new data to the file
        sourceFile.write(data);

        // Sets the file pointer to the beginning of the temporary file
        temp.seek(0);

        // Reads the content from the temporary file
        while ((bytesRead = temp.read(buffer)) != -1) {
          // Writes the content back to the original file
          sourceFile.write(buffer, 0, bytesRead);
        }
      }

      // Deletes the temporary file
      if (tempFile.exists()) {
        tempFile.delete();
      }

      // Include the updated file length in the response message
      String message = "Write successful // Updated File Length -> " + sourceFile.length();
      return new Response(StatusCode.WRITE_INSERT_SUCCESS, null, message, file.lastModified());
    } catch (IOException e) {
      return new Response(StatusCode.WRITE_INSERT_ERROR, null,
          "Error writing to file: " + e.getMessage(), -1);
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
    return new Response(StatusCode.MONITOR_ERROR, null, "Monitor functionality not implemented.",
        -1);
  }

  /**
   * Handles a write delete request by deleting a specific segment of a file.
   *
   * @param request The write delete request containing the file path, offset, and the number of
   *                bytes to delete.
   * @return A response object indicating the success or failure of the delete operation.
   */
  private Response handleWriteDeleteRequest(Request request) {
    File file = new File(request.getFilePath());
    if (!file.exists()) {
      return new Response(StatusCode.READ_ERROR, null, "File does not exist.", -1);
    }

    try (RandomAccessFile sourceFile = new RandomAccessFile(file, "rw")) {
      // Cannot delete beyond the file size
      if (request.getOffset() + request.getBytesToReadOrDelete() > sourceFile.length()) {
        return new Response(StatusCode.WRITE_DELETE_ERROR, null, "Cannot delete beyond EOF.", -1);
      }

      // Calculate the starting point of the remaining content after deletion
      long startOfRemaining = request.getOffset() + request.getBytesToReadOrDelete();

      // Calculate the length of the content remaining after the deleted segment
      int remainingLengthAfterDeletion = (int) (sourceFile.length() - startOfRemaining);

      // Create a buffer to hold the remaining content after the deletion
      byte[] remaining = new byte[remainingLengthAfterDeletion];

      // Move the file pointer to the start of the remaining content
      sourceFile.seek(startOfRemaining);

      // Read the remaining content into the buffer
      sourceFile.readFully(remaining);

      // Truncate the file to remove the deleted segment specified by the truncation point (offset)
      sourceFile.setLength(request.getOffset());

      // Move the file pointer back to the truncation point (offset) to write the remaining content
      sourceFile.seek(request.getOffset());

      // Write the remaining content back to the file
      sourceFile.write(remaining);

      // Include the updated file length in the response message
      String message = "Delete successful // Updated File Length -> " + sourceFile.length();
      return new Response(StatusCode.WRITE_DELETE_SUCCESS, null, message, file.lastModified());
    } catch (IOException e) {
      return new Response(StatusCode.WRITE_DELETE_ERROR, null,
          "Error deleting content: " + e.getMessage(), -1);
    }
  }

  /**
   * Handles a request to fetch information about a file on the server.
   *
   * @param request The file info request containing the file path.
   * @return A response object containing the file information or an error message.
   */
  private Response handleFileInfoRequest(Request request) {
    // Fetch the file
    File file = new File(request.getFilePath());

    if (!file.exists()) {
      return new Response(StatusCode.FILE_INFO_ERROR, null, "File does not exist.", -1);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String lastModifiedReadable = sdf.format(new java.util.Date(file.lastModified()));

    // Constructing JSON-like structure for various file properties to be returned
    String fileInfo = "{\n" +
        "name: " + file.getName() + ",\n" +
        "size: " + file.length() + " bytes,\n" +
        "lastModified: " + lastModifiedReadable + ",\n" +
        "readable: " + file.canRead() + ",\n" +
        "writable: " + file.canWrite() + ",\n" +
        "executable: " + file.canExecute() + ",\n" +
        "hidden: " + file.isHidden() + ",\n" +
        "absolutePath: " + file.getAbsolutePath() + ",\n" +
        "parent: " + file.getParent() + "\n" +
        "}";

    return new Response(StatusCode.FILE_INFO_SUCCESS, fileInfo.getBytes(),
        "File info retrieved successfully.", file.lastModified());
  }

}
