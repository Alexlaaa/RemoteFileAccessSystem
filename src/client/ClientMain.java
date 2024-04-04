package client;

import java.io.IOException;

/**
 * The ClientMain class is the entry point for the client application. It initializes the ClientUI
 * and prompts the user for configurations, then creates the necessary client components and starts
 * the client.
 */
public class ClientMain {

  /**
   * The entry point for the client application. Initializes the client's components and starts the
   * client.
   *
   * @param args The command-line arguments.
   * @throws IOException if an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    // Initialize ClientUI to prompt user for configurations
    ClientUI clientUI = new ClientUI();
    String serverAddress = clientUI.selectServerAddress();
    int serverPort = clientUI.selectPort();
    int timeout = clientUI.selectTimeout();
    double requestSendProbability = 1; // Assumption: Client will always send requests
    double replyReceiveProbability = 1; // Assumption: Client will always receive replies
    int maxRetries = clientUI.selectMaxRetries();
    long freshnessInterval = clientUI.selectFreshnessInterval();

    ClientUDP clientUDP = new ClientUDP(serverAddress, serverPort, timeout, requestSendProbability,
        replyReceiveProbability);

    ClientNetwork clientNetwork = new ClientNetwork(clientUDP, maxRetries);

    ClientService clientService = new ClientService(clientNetwork, freshnessInterval);

    clientUI.setClientService(clientService);

    // Start the client to accept user commands and interact with the server
    System.out.println(
        "\n**Client is starting with server address " + serverAddress + " and port " + serverPort
            + " with default probabilities:\nrequest send = " + requestSendProbability
            + ", response receive = "
            + replyReceiveProbability
            + ", maxRetries = " + maxRetries
            + "and freshness interval = " + freshnessInterval
            + "ms.**");
    clientUI.start();
  }
}

