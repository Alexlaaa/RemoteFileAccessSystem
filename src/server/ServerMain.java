package server;

import common.Constants;
import java.io.IOException;

/**
 * The ServerMain class serves as the entry point for the server-side application. It initializes
 * the server's components and starts listening for client requests.
 */
public class ServerMain {

  /**
   * The entry point for the server-side application. Initializes the server's components and starts
   * listening for client requests.
   *
   * @param args The command-line arguments.
   * @throws IOException if an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    // Initialize the ServerUI to prompt user for configurations
    ServerUI serverUI = new ServerUI();
    int port = serverUI.selectPort();
    Constants.NetworkStrategyType strategyType = serverUI.selectStrategyType();
    double requestReceiveProbability = serverUI.selectRequestReceiveProbability();
    double replySendProbability = serverUI.selectReplySendProbability();

    ServerService serverService = new ServerService();

    ServerNetwork serverNetwork = new ServerNetwork(serverService, strategyType);

    ServerUDP serverUDP = new ServerUDP(port, requestReceiveProbability, replySendProbability,
        serverNetwork);

    ServerMonitorService serverMonitorService = new ServerMonitorService(serverUDP);

    serverService.setMonitorService(serverMonitorService);

    // Start the server to listen for incoming requests
    System.out.println(
        "\n**Server is starting on port " + port + " with " + strategyType + " strategy "
            + "and probabilities:\nrequest receive = " + requestReceiveProbability
            + ", response send = "
            + replySendProbability + ".**");
    serverUDP.listen();
  }
}
