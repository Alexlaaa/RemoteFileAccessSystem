package marshalling;

import common.Request;
import common.Response;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Handles the marshalling of Request and Response objects to byte arrays.
 */
public class Marshaller {

  private Marshaller() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Marshals a Request object into a byte array. Used by the client to convert Request objects into
   * byte arrays for sending to the server.
   *
   * @param request The Request object to marshal.
   * @return A byte array representing the marshalled Request.
   */
  public static byte[] marshal(Request request) {
    ByteBuffer buffer = ByteBuffer.allocate(1024); // Allocate more if needed

    buffer.putLong(request.getRequestId());
    buffer.putInt(request.getOperationType().ordinal());
    buffer.putLong(request.getBytesToReadOrDelete());
    buffer.putLong(request.getOffset());
    buffer.putLong(request.getMonitorDuration());

    byte[] filePathBytes = request.getFilePath().getBytes();
    buffer.putInt(filePathBytes.length);
    buffer.put(filePathBytes);

    if (request.getData() != null) {
      // For WRITE operation, include data
      buffer.putInt(request.getData().length);
      buffer.put(request.getData());
    } else {
      // For READ and MONITOR, there's no data
      buffer.putInt(0);
    }

    return Arrays.copyOf(buffer.array(), buffer.position());
  }

  /**
   * Marshals a Response object into a byte array. Used by the server to convert Response objects
   * into byte arrays for sending to the client.
   *
   * @param response The Response object to marshal.
   * @return A byte array representing the marshalled Response.
   */
  public static byte[] marshal(Response response) {
    ByteBuffer buffer = ByteBuffer.allocate(1024); // Allocate more if needed

    buffer.putInt(response.getStatusCode().getCode());

    if (response.getData() != null) {
      // Include data if present
      buffer.putInt(response.getData().length);
      buffer.put(response.getData());
    } else {
      // Indicate no data
      buffer.putInt(0);
    }

    byte[] messageBytes = response.getMessage().getBytes();
    buffer.putInt(messageBytes.length);
    buffer.put(messageBytes);

    return Arrays.copyOf(buffer.array(), buffer.position());
  }
}
