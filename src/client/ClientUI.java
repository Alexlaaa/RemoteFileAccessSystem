package client;

import common.Constants;
import java.io.IOException;
import java.util.Scanner;

/**
 * ClientUI provides a command-line interface for users to interact with the file service. It allows
 * users to read, write, and monitor files on the server, and change network strategies.
 */
public class ClientUI {

  private final ClientService clientService;
  private final Scanner scanner = new Scanner(System.in);
  private String serverAddress;
  private int serverPort;

  /**
   * Constructs a ClientUI with the specified ClientService.
   *
   * @param clientService The service layer this UI will interact with.
   * @throws IOException If an I/O error occurs during setup.
   */
  public ClientUI(ClientService clientService) throws IOException {
    this.clientService = clientService;
    initialize();
  }

  /**
   * Initializes the ClientUI by setting up the server configuration and network strategy.
   *
   * @throws IOException If an I/O error occurs during setup.
   */
  public void initialize() throws IOException {
    setServerConfig(); // Done once, at the start
    setupNetworkStrategy(); // Can be changed by the user
  }

  /**
   * Sets up the server configuration based on user input.
   */
  private void setServerConfig() {
    System.out.println("Enter server address:");
    serverAddress = scanner.nextLine();
    System.out.println("Enter server port:");
    serverPort = scanner.nextInt();
    scanner.nextLine();  // Consume newline
  }

  /**
   * Sets up the network strategy based on user input.
   *
   * @throws IOException If an I/O error occurs during network setup.
   */
  private void setupNetworkStrategy() throws IOException {
    System.out.println("Select Invocation Type:\n1. At Least Once\n2. At Most Once");
    int strategyChoice = scanner.nextInt();
    scanner.nextLine();  // Consume newline left-over
    Constants.NetworkStrategyType selectedStrategy =
        (strategyChoice == 1) ? Constants.NetworkStrategyType.AT_LEAST_ONCE
            : Constants.NetworkStrategyType.AT_MOST_ONCE;

    // Initialize ClientUDP
    ClientUDP clientUDP = new ClientUDP(serverAddress, serverPort, 1000, 1,
        1);
    // Initialize ClientNetwork with the selected strategy
    ClientNetwork clientNetwork = new ClientNetwork(clientUDP, selectedStrategy);
    clientService.setClientNetwork(clientNetwork);
  }

  /**
   * Starts the UI loop to accept user commands.
   *
   * @throws IOException If an I/O error occurs during command execution.
   */
  public void start() throws IOException {
    System.out.println("Client UI Started.");

    while (true) {
      System.out.println(
          "Enter command:\n1. Read File\n2. Write File\n3. Monitor File\n4. Change Network Strategy\n5. Exit");
      int command = scanner.nextInt();
      scanner.nextLine();  // Consume newline

      switch (command) { // TODO: Add cases for the 2 additional operations
        case 1:
          handleReadFile();
          break;
        case 2:
          handleWriteFile();
          break;
        case 3:
          handleMonitorFile();
          break;
        case 4:
          // Change network strategy
          setupNetworkStrategy();
          break;
        case 5:
          System.out.println("Exiting...");
          return;
        default:
          System.out.println("Invalid command.");
          break;
      }
    }
  }

  /**
   * Handles the read file command from the user.
   */
  private void handleReadFile() {
    System.out.println("Enter file path:");
    String filePath = scanner.nextLine();
    System.out.println("Enter number of bytes to read:");
    long bytesToRead = scanner.nextLong();
    scanner.nextLine(); // Consume newline
    System.out.println("Enter offset:");
    long offset = scanner.nextLong();
    scanner.nextLine();  // Consume newline
    String readResult = clientService.handleReadRequest(filePath, bytesToRead, offset);
    System.out.println("Read result: " + readResult);
  }

  /**
   * Handles the write file command from the user.
   */
  private void handleWriteFile() {
    System.out.println("Enter file path:");
    String filePath = scanner.nextLine();
    System.out.println("Enter offset:");
    long offset = scanner.nextLong();
    scanner.nextLine(); // Consume newline
    System.out.println("Enter data to write:");
    String data = scanner.nextLine();
    String writeResult = clientService.handleWriteRequest(filePath, offset, data);
    System.out.println("Write result: " + writeResult);
  }

  /**
   * Handles the monitor file command from the user.
   */
  private void handleMonitorFile() {
    System.out.println("Enter file path:");
    String filePath = scanner.nextLine();
    System.out.println("Enter monitor duration (in milliseconds):");
    long monitorDuration = scanner.nextLong();
    scanner.nextLine(); // Consume newline
    String monitorResult = clientService.handleMonitorRequest(filePath, monitorDuration);
    System.out.println("Monitor result: " + monitorResult);
  }

  // TODO: Implement methods for the 2 additional operations
}
