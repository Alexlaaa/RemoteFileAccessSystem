package client;

import common.Constants;
import common.Constants.OperationType;
import common.Request;
import common.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * ClientService acts as the intermediary between the client UI and the network layer, facilitating
 * the sending of requests and receiving of responses.
 */
public class ClientService {

  private ClientNetwork clientNetwork;
  private final Random random = new Random();

  /**
   * Constructs a ClientService instance with a specified ClientNetwork.
   *
   * @param clientNetwork The network layer this service will interact with.
   */
  public ClientService(ClientNetwork clientNetwork) {
    this.clientNetwork = clientNetwork;
  }

  /**
   * Sets the network layer this service interacts with.
   *
   * @param clientNetwork The ClientNetwork to be used by this service.
   */
  public void setClientNetwork(ClientNetwork clientNetwork) {
    this.clientNetwork = clientNetwork;
  }

  /**
   * Handles a request to read a file from the server.
   *
   * @param filePath    The path of the file to read.
   * @param bytesToRead The number of bytes to read from the file.
   * @param offset      The offset from where to start reading the file.
   * @return The string representation of the read data or an error message.
   */
  public String handleReadRequest(String filePath, long bytesToRead, long offset) {
    Request request = new Request(generateRequestId(), Constants.OperationType.READ, filePath,
        bytesToRead,
        offset);
    try {
      Response response = clientNetwork.sendRequest(request);
      return new String(response.getData()) + "\n"
          + response.getMessage(); // Return the response for the UI to display
    } catch (IOException e) {
      return "Error sending read request: " + e.getMessage();
    }
  }

  /**
   * Handles a request to write data to a file on the server.
   *
   * @param filePath The path of the file where data needs to be written.
   * @param offset   The offset at which data should be written in the file.
   * @param data     The data to write to the file.
   * @return Acknowledgement message or an error message.
   */
  public String handleWriteInsertRequest(String filePath, long offset, String data) {
    Request request = new Request(generateRequestId(), OperationType.WRITE_INSERT, filePath,
        offset, data.getBytes());
    try {
      Response response = clientNetwork.sendRequest(request);
      return response.getMessage(); // Return the server's response message
    } catch (IOException e) {
      return "Error sending write insert request: " + e.getMessage();
    }
  }

  /**
   * Handles a request to monitor a file on the server for changes.
   *
   * @param filePath        The path of the file to monitor.
   * @param monitorDuration The duration for which to monitor the file.
   * @return A message indicating the status of the monitor request or an error message.
   */
  public String handleMonitorRequest(String filePath, long monitorDuration) {
    Request request = new Request(generateRequestId(), Constants.OperationType.MONITOR, filePath,
        monitorDuration);
    try {
      Response response = clientNetwork.sendRequest(request);
      return response.getMessage(); // Return the server's response message
    } catch (IOException e) {
      return "Error sending monitor request: " + e.getMessage();
    }
  }

  /**
   * Handles a request to delete data from a file on the server.
   *
   * @param filePath      The path of the file from which data needs to be deleted.
   * @param bytesToDelete The number of bytes to delete from the file.
   * @param offset        The offset from where to start deleting the data in the file.
   * @return Acknowledgement message or an error message.
   */
  public String handleWriteDeleteRequest(String filePath, long bytesToDelete, long offset) {
    Request request = new Request(generateRequestId(), OperationType.WRITE_DELETE,
        filePath, bytesToDelete, offset, true);
    try {
      Response response = clientNetwork.sendRequest(request);
      return response.getMessage();
    } catch (IOException e) {
      return "Error sending write delete request: " + e.getMessage();
    }
  }

  /**
   * Handles a request to fetch information about a file on the server.
   *
   * @param filePath The path of the file for which to fetch information.
   * @return A string containing the file information or an error message.
   */
  public String handleFileInfoRequest(String filePath) {
    Request request = new Request(generateRequestId(), Constants.OperationType.FILE_INFO, filePath);
    try {
      Response response = clientNetwork.sendRequest(request);
      return new String(response.getData());
    } catch (IOException e) {
      return "Error fetching file info: " + e.getMessage();
    }
  }

  /**
   * Generates a unique request ID based on the current system time and the local host's IP
   * address.
   *
   * @return A unique request ID.
   */
  private long generateRequestId() {
    // Get the current system time in nanoseconds
    long nanoTimePart = System.nanoTime();

    long ipPart; // Variable to hold the numeric value of the IP address
    try {
      // Retrieve the local host's IP address in byte array form
      byte[] ipBytes = InetAddress.getLocalHost().getAddress();
      ipPart = 0; // Initialize ipPart for bitwise operations

      // Convert the byte array to a long value
      for (byte b : ipBytes) {
        // Shift ipPart 8 bits to the left to make room for the next byte
        // and add the byte value to ipPart.
        // The bitwise AND with 0xFF ensures that the byte is treated as an unsigned value.
        ipPart = (ipPart << 8) | (b & 0xFF);
      }
    } catch (UnknownHostException e) {
      // If the IP address cannot be determined, use a random long value as a fallback.
      ipPart = random.nextLong();
    }

    // Combine the nanoTime and IP address components to form a unique ID.
    // XOR is used here to combine both parts while maintaining a reasonable chance of uniqueness.
    return nanoTimePart ^ ipPart;
  }

}

