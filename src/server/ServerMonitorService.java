package server;

import common.Constants;
import common.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import marshalling.Marshaller;

/**
 * Handles monitoring requests for files, tracking clients interested in file updates, and notifying
 * them when changes occur.
 */
public class ServerMonitorService {

  private final ServerUDP serverUDP; // The UDP server used for sending updates to clients
  private final ConcurrentHashMap<String, List<MonitorEntry>> monitors; // Maps file paths to lists of monitoring clients

  /**
   * Constructor for the ServerMonitorService.
   *
   * @param serverUDP The ServerUDP instance used for sending updates to clients.
   */
  public ServerMonitorService(ServerUDP serverUDP) {
    this.serverUDP = serverUDP;
    this.monitors = new ConcurrentHashMap<>();
  }

  /**
   * Registers a client to be notified of updates to a specific file.
   *
   * @param filePath        The path of the file to monitor.
   * @param clientAddress   The address of the client.
   * @param clientPort      The port number of the client.
   * @param monitorDuration The duration for which the file should be monitored.
   */
  public void registerForMonitoring(String filePath, InetAddress clientAddress, int clientPort,
      long monitorDuration) {
    MonitorEntry entry = new MonitorEntry(clientAddress, clientPort, System.currentTimeMillis(),
        monitorDuration);
    monitors.computeIfAbsent(filePath, k -> Collections.synchronizedList(new ArrayList<>()))
        .add(entry); // Add the client to the list of monitors for the file
  }

  /**
   * Checks the list of clients monitoring a file and sends them updates if the file has changed.
   *
   * @param filePath       The path of the updated file.
   * @param updatedContent The new content of the file.
   * @throws IOException If there's an issue with the network communication.
   */
  public void checkAndUpdateMonitors(String filePath, byte[] updatedContent,
      Constants.OperationType operationType) throws IOException {
    // Retrieves list of clients monitoring the file
    List<MonitorEntry> monitorList = monitors.get(filePath);

    // If there are clients monitoring the file
    if (monitorList != null) {
      // Iterate through the list of clients monitoring the file
      Iterator<MonitorEntry> iterator = monitorList.iterator();
      while (iterator.hasNext()) {
        // Retrieve the next monitor entry
        MonitorEntry entry = iterator.next();
        // If the monitoring duration has expired
        if (System.currentTimeMillis() - entry.startTime > entry.monitorDuration) {
          // Remove the entry from the list
          iterator.remove();
        }
        // Otherwise, the monitoring duration has not expired, and we notify the active clients with the update
        else {
          // Create a callback response with the updated content
          String message = "File update notification for " + filePath + ".\nOperation: "
              + operationType.getDescription();
          Response updateResponse = new Response(Constants.StatusCode.CALLBACK, updatedContent,
              message, -1);
          // Marshal the response to be transmitted through serverUDP to the client
          byte[] responseData = Marshaller.marshal(updateResponse);
          sendUpdate(entry.clientAddress, entry.clientPort, responseData);
        }
      }
      if (monitorList.isEmpty()) {
        monitors.remove(filePath); // Clean up empty monitor lists
      }
    }
  }

  /**
   * Sends an update message to a single client.
   *
   * @param clientAddress The address of the client to send the update to.
   * @param clientPort    The port number of the client.
   * @param responseData  The data to send to the client which is the marshalled callback response.
   * @throws IOException If there's an issue with the network communication.
   */
  private void sendUpdate(InetAddress clientAddress, int clientPort, byte[] responseData)
      throws IOException {
    serverUDP.send(responseData, clientAddress, clientPort);
  }

  /**
   * Represents an entry in the monitoring list, containing client information and monitoring
   * duration.
   */
  private static class MonitorEntry {

    InetAddress clientAddress;
    int clientPort;
    long startTime;
    long monitorDuration;

    /**
     * Constructs a MonitorEntry with the specified client information and monitoring duration.
     *
     * @param clientAddress   The address of the client.
     * @param clientPort      The port number of the client.
     * @param startTime       The time at which monitoring started.
     * @param monitorDuration The duration for which the file should be monitored.
     */
    public MonitorEntry(InetAddress clientAddress, int clientPort, long startTime,
        long monitorDuration) {
      this.clientAddress = clientAddress;
      this.clientPort = clientPort;
      this.startTime = startTime;
      this.monitorDuration = monitorDuration;
    }
  }
}
