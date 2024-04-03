package server;

import java.io.IOException;

/**
 * The ServerMain class serves as the entry point for the server-side application. It initializes
 * the server's components and starts listening for client requests.
 */
public class ServerMain {

  /**
   * The main method that starts the server.
   *
   * @param args Command line arguments (not used).
   * @throws IOException If an I/O error occurs during server initialization or execution.
   */
  public static void main(String[] args) throws IOException {
    // Initialize the server UI to configure the port
    ServerUI serverUI = new ServerUI();
    int port = serverUI.configurePort();

    // Initialize the server service that will process client requests
    ServerService serverService = new ServerService();

    // Initialize the network layer that handles incoming and outgoing network communication
    ServerNetwork serverNetwork = new ServerNetwork(serverService);

    // Initialize the UDP server that listens for incoming client requests
    ServerUDP serverUDP = new ServerUDP(port, 1.0, 1.0, serverNetwork);

    // Initialize the monitor service that manages file monitoring for clients
    ServerMonitorService serverMonitorService = new ServerMonitorService(serverUDP);

    // Set the monitor service in the server service
    serverService.setMonitorService(serverMonitorService);

    // Start the server to listen for incoming requests
    System.out.println("Server is starting on port " + port);
    serverUDP.listen();
  }
}
