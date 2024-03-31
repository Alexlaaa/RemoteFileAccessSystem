package marshalling;

import common.Constants;
import common.Request;
import common.Response;

import java.nio.ByteBuffer;

/**
 * Handles the unmarshalling of data from byte arrays to Request and Response objects.
 */
public class Unmarshaller {

  private Unmarshaller() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Unmarshal a byte array into a Request object.
   * Used by the server to convert incoming byte arrays from client into Request objects.
   *
   * @param data The byte array to unmarshal.
   * @return The unmarshalled Request object.
   */
  public static Request unmarshalRequest(byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(data); // wrap the byte array in a ByteBuffer to provide a structured way to access the data

    // Extract the operation type, offset, and monitor duration from the byte buffer.
    Constants.OperationType operationType = Constants.OperationType.values()[buffer.getInt()]; // integer is used to access the corresponding OperationType enum value
    long offset = buffer.getLong();
    long monitorDuration = buffer.getLong();

    // Extract the file path from the byte buffer.
    int filePathLength = buffer.getInt(); // read the length of the file path
    byte[] filePathBytes = new byte[filePathLength]; // create a byte array to store the file path
    buffer.get(filePathBytes);  // read the file path bytes from the buffer
    String filePath = new String(filePathBytes); // convert the file path bytes to a string

    // Depending on the operation type, construct and return the appropriate Request object.
    switch (operationType) {
      case READ:
        return new Request(operationType, filePath, offset);
      case WRITE:
        int dataSize = buffer.getInt();
        byte[] fileData = null;
        if (dataSize > 0) {
          fileData = new byte[dataSize];
          buffer.get(fileData);
        }
        return new Request(operationType, filePath, offset, fileData);
      case MONITOR:
        return new Request(operationType, filePath, true, monitorDuration);
      default:
        throw new IllegalArgumentException("Unrecognized operation type for unmarshalling Request");
    }
  }

  /**
   * Unmarshal a byte array into a Response object.
   * Used by the client to convert incoming byte arrays from server into Response objects.
   *
   * @param data The byte array to unmarshal.
   * @return The unmarshalled Response object.
   */
  public static Response unmarshalResponse(byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(data); // wrap the byte array in a ByteBuffer to provide a structured way to access the data

    // Extract the status code from the byte buffer.
    Constants.StatusCode statusCode = Constants.StatusCode.values()[buffer.getInt()]; // integer is used to access the corresponding StatusCode enum value

    // Extract the file data from the byte buffer.
    int dataSize = buffer.getInt(); // read the length of the file data
    byte[] fileData = null;
    if (dataSize > 0) { // if there is file data, read it from the buffer
      fileData = new byte[dataSize]; // create a byte array to store the file data
      buffer.get(fileData); // read the file data bytes from the buffer
    }

    // Extract the message from the byte buffer.
    int messageLength = buffer.getInt(); // read the length of the message
    byte[] messageBytes = new byte[messageLength]; // create a byte array to store the message
    buffer.get(messageBytes); // read the message bytes from the buffer
    String message = new String(messageBytes); // convert the message bytes to a string

    return new Response(statusCode, fileData, message); // construct and return the Response object
  }
}
