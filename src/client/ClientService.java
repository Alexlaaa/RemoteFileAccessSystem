package client;

import common.Constants;
import common.Constants.OperationType;
import common.Constants.StatusCode;
import common.Request;
import common.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import marshalling.Unmarshaller;

/**
 * ClientService acts as the intermediary between the client UI and the network layer, facilitating
 * the sending of requests and receiving of responses.
 */
public class ClientService {

  private ClientNetwork clientNetwork;
  private final ClientCache clientCache;
  private final Random random = new Random();

  /**
   * Constructs a ClientService instance with a specified ClientNetwork. The freshness interval is
   * used to determine the validity of cached data. The ClientCache is initialized with the
   * freshness interval within this constructor.
   *
   * @param clientNetwork     The network layer this service will interact with.
   * @param freshnessInterval The freshness interval for the client cache.
   */
  public ClientService(ClientNetwork clientNetwork, long freshnessInterval) {
    this.clientNetwork = clientNetwork;
    this.clientCache = new ClientCache(freshnessInterval);
  }

  /**
   * Handles a request to read a file from the server. If the file content is cached, fresh and
   * within valid range, the cached content is returned without sending a request to the server.
   *
   * @param filePath    The path of the file to read.
   * @param bytesToRead The number of bytes to read from the file.
   * @param offset      The offset from where to start reading the file.
   * @return The string representation of the read data or an error message.
   */
  public String handleReadRequest(String filePath, long bytesToRead, long offset) {
    byte[] cachedContent = clientCache.getFileContentIfFresh(filePath, offset,
        bytesToRead); // Checks if cached content is fresh and within valid range
    long cachedLastModifiedTime = clientCache.getLastModifiedTime(filePath, offset,
        bytesToRead);  // -1 if not cached or out of valid range

    // Cache hit, content is fresh and within valid range, return cached content
    if (cachedLastModifiedTime != -1 && cachedContent != null && cachedContent.length > 0) {
      return "*Content retrieved from cache*\n" + new String(cachedContent);
    }

    // Cache miss, content is not cached, is outdated or is out of valid range, send read request to server
    Request request = new Request(generateRequestId(), Constants.OperationType.READ,
        filePath, bytesToRead, offset);
    try {
      Response response = clientNetwork.sendRequest(request);

      switch (response.getStatusCode()) {
        case READ_SUCCESS, READ_INCOMPLETE:
          long lastModifiedTimeByServer = response.getLastModifiedTimeAtServer();
          // If not previously cached or is out of valid range, cache the file content
          if (cachedLastModifiedTime == -1) {
            System.out.println(
                "File not previously cached or is out of valid range, caching content...\n");
            clientCache.cacheFileContent(filePath, response.getData(), lastModifiedTimeByServer,
                offset, bytesToRead);
          }
          // Else if previously cached and within valid range, but outdated, and still the same file as server, update validation time
          else if (lastModifiedTimeByServer == cachedLastModifiedTime) {
            System.out.println(
                "File previously cached is within valid range and outdated, but still the same file as server (lastModifiedTimeByServer == cacheLastModifiedTime), updating cache validation time...\n");
            // Update validation time of existing cache entry
            clientCache.updateValidationTime(filePath, lastModifiedTimeByServer);
          }
          // Else if previously cached and within range and outdated, but different file than server, invalidate cache and cache new content
          else {
            System.out.println(
                "File previously cached is within valid range and outdated, but is a different file from server (lastModifiedTimeByServer != cacheLastModifiedTime), invalidating cache and caching new content...\n");
            clientCache.invalidate(filePath);
            clientCache.cacheFileContent(filePath, response.getData(), lastModifiedTimeByServer,
                offset, bytesToRead);
          }
          return new String(response.getData()) + "\n" + response.getMessage();

        case NETWORK_ERROR:
          return "Error: Network issue encountered while trying to read the file. Please try again.";

        case GENERAL_ERROR:
          return "Error: An unexpected error occurred while processing your request. Please try again.";

        case READ_ERROR:
          return "Error: Read request failed. " + response.getMessage();

        default:
          return "Error: Unexpected status code " + response.getStatusCode();
      }
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
   * Sends a request to the server to monitor a specified file for a given duration. Upon receiving
   * a successful response, it starts listening for updates from the server regarding the file.
   *
   * @param filePath        The path of the file to monitor.
   * @param monitorDuration The duration (in milliseconds) for which the file should be monitored.
   */
  public void handleMonitorRequest(String filePath, long monitorDuration) {
    Request request = new Request(generateRequestId(), Constants.OperationType.MONITOR, filePath,
        monitorDuration);
    try {
      Response response = clientNetwork.sendRequest(request);
      if (!response.getMessage().isEmpty()) {
        System.out.println("Monitor File Result: " + response.getMessage());
        // Start listening for updates only if the request was successful
        listenForUpdates(monitorDuration);
      }
    } catch (IOException e) {
      System.out.println("Error sending monitor request: " + e.getMessage());
    }
  }

  /**
   * Listens for updates from the server about the monitored file until the monitor duration
   * expires. If updates are received, they are processed and the information is displayed to the
   * user.
   *
   * @param monitorDuration The duration (in milliseconds) for which to listen for updates.
   */
  private void listenForUpdates(long monitorDuration) {
    long startTime = System.currentTimeMillis();
    System.out.println("\n== Monitoring started. Waiting for updates... ==\n");
    while (System.currentTimeMillis() - startTime < monitorDuration) {
      try {
        byte[] update = clientNetwork.getClientUDP().listenForUpdates();
        if (update != null && update.length > 0) {
          Response response = Unmarshaller.unmarshalResponse(update);
          if (response.getStatusCode() == StatusCode.CALLBACK) {
            System.out.println("Update received: " + response.getMessage());
            if (response.getData() != null && response.getData().length > 0) {
              System.out.println("Updated data: " + new String(response.getData()) + "\n");
            }
          }
        }
      } catch (IOException e) {
        System.out.println("\nError while listening for updates: " + e.getMessage());
      }
      try {
        Thread.sleep(1000); // Sleep to avoid a tight loop
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        System.out.println("\nMonitoring interrupted.");
        break;
      }
    }
    System.out.println("\nMonitoring ended.");
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
      // Check if response data is null
      if (response.getData() == null || response.getData().length == 0) {
        return "Error: No data received in response.";
      }
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

