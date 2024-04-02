package marshalling;

import common.Constants;
import common.Request;
import common.Response;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Handles the unmarshalling of data from byte arrays to Request and Response objects.
 */
public class Unmarshaller {

  private Unmarshaller() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Unmarshal a byte array into a Request object. Used by the server to convert incoming byte
   * arrays from client into Request objects.
   *
   * @param data The byte array to unmarshal.
   * @return The unmarshalled Request object.
   */
  public static Request unmarshalRequest(byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(
        data); // wrap the byte array in a ByteBuffer to provide a structured way to access the data

    // Extract the requestId, operation type, bytesToRead, offset, and monitor duration from the byte buffer.
    long requestId = buffer.getLong();
    Constants.OperationType operationType = Constants.OperationType.values()[buffer.getInt()]; // integer is used to access the corresponding OperationType enum value
    long bytesToRead = buffer.getLong();
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
        return new Request(requestId, operationType, filePath, bytesToRead, offset);
      case WRITE:
        int dataSize = buffer.getInt();
        byte[] fileData = null;
        if (dataSize > 0) {
          fileData = new byte[dataSize];
          buffer.get(fileData);
        }
        return new Request(requestId, operationType, filePath, offset, fileData);
      case MONITOR:
        return new Request(requestId, operationType, filePath, true, monitorDuration);
      default:
        throw new IllegalArgumentException("Unrecognized operation type for unmarshalling Request");
    }
  }

  /**
   * Unmarshal a byte array into a Response object. Used by the client to convert incoming byte
   * arrays from server into Response objects.
   *
   * @param data The byte array to unmarshal.
   * @return The unmarshalled Response object.
   */
  public static Response unmarshalResponse(byte[] data) {
    // Wrap the byte array data in a ByteBuffer for structured access
    ByteBuffer buffer = ByteBuffer.wrap(data);

    // Check if there's enough data to read the status code
    if (buffer.remaining() < Integer.BYTES) {
      return new Response(Constants.StatusCode.GENERAL_ERROR, null,
          "Incomplete data: Unable to extract status code.");
    }

    // Read the status code value from the buffer
    int statusCodeValue = buffer.getInt();

    // Convert the integer status code value to the StatusCode enum
    Constants.StatusCode statusCode = Arrays.stream(Constants.StatusCode.values())
        .filter(sc -> sc.getCode() == statusCodeValue)
        .findFirst()
        .orElse(Constants.StatusCode.GENERAL_ERROR);

    // Check if there's enough data to read the size of the data part
    if (buffer.remaining() < Integer.BYTES) {
      return new Response(statusCode, null, "Incomplete data: Unable to extract data size.");
    }

    // Read the data size
    int dataSize = buffer.getInt();

    // Prepare a byte array to hold the data part
    byte[] fileData = new byte[dataSize];

    // Check if there's enough data to read the file data
    if (buffer.remaining() < dataSize) {
      return new Response(statusCode, null, "Incomplete data: Insufficient file data.");
    }

    // Read the file data into the byte array
    buffer.get(fileData);

    // Check if there's enough data to read the size of the message part
    if (buffer.remaining() < Integer.BYTES) {
      return new Response(statusCode, fileData,
          "Incomplete data: Unable to extract message length.");
    }

    // Read the message size
    int messageLength = buffer.getInt();

    // Prepare a byte array to hold the message part
    byte[] messageBytes = new byte[messageLength];

    // Check if there's enough data to read the message data
    if (buffer.remaining() < messageLength) {
      return new Response(statusCode, fileData, "Incomplete data: Insufficient message data.");
    }

    // Read the message data into the byte array
    buffer.get(messageBytes);

    // Convert the message byte array to a string
    String message = new String(messageBytes);

    // Return the constructed Response object
    return new Response(statusCode, fileData, message);
  }
}
