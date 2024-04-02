package client;

import common.Constants;
import java.io.IOException;
import java.util.InputMismatchException;
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
    serverAddress = promptForNonEmptyString(
        "Enter server address:");
    serverPort = (int) promptForLong("Enter server port:");
  }

  /**
   * Sets up the network strategy based on user input.
   *
   * @throws IOException If an I/O error occurs during network setup.
   */
  private void setupNetworkStrategy() throws IOException {
    int strategyChoice = (int) promptForLong(
        "Select Invocation Type:\n1. At Least Once\n2. At Most Once");
    while (strategyChoice != 1 && strategyChoice != 2) {
      strategyChoice = (int) promptForLong(
          "Invalid choice. Select Invocation Type:\n1. At Least Once\n2. At Most Once");
    }

    Constants.NetworkStrategyType selectedStrategy =
        strategyChoice == 1 ? Constants.NetworkStrategyType.AT_LEAST_ONCE
            : Constants.NetworkStrategyType.AT_MOST_ONCE;

    // Initialize ClientUDP
    ClientUDP clientUDP = new ClientUDP(serverAddress, serverPort, 10000, 1, 1);
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
          "Enter command:\n1. Read File Content\n2. Write Content in File\n3. Monitor File\n4. Delete Content in File\n5. Read File Info\n6. Change Network Strategy\n7. Exit");
      int command = scanner.nextInt();
      scanner.nextLine();  // Consume newline

      switch (command) {
        case 1 -> handleReadFile();
        case 2 -> handleWriteInsertFile();
        case 3 -> handleMonitorFile();
        case 4 -> handleWriteDeleteContent();
        case 5 -> handleFileInfo();
        case 6 -> setupNetworkStrategy(); // Change network strategy
        case 7 -> {
          System.out.println("Exiting...");
          return;
        }
        default -> System.out.println("Invalid command.");
      }
    }
  }

  /**
   * Handles the read file command from the user.
   */
  private void handleReadFile() {
    String filePath = promptForNonEmptyString("Enter file path:");
    long offset = promptForLong("Enter offset:");
    long bytesToRead = promptForLong("Enter number of bytes to read:");
    String readResult = clientService.handleReadRequest(filePath, bytesToRead, offset);
    System.out.println(readResult.isEmpty() ? "Error reading file." : "Read Result: " + readResult);
  }

  /**
   * Handles the write insert file command from the user.
   */
  private void handleWriteInsertFile() {
    String filePath = promptForNonEmptyString("Enter file path:");
    long offset = promptForLong("Enter offset:");
    System.out.println("Enter data to write:");
    String data = scanner.nextLine();  // Data can include any character, so no validation needed.
    String writeInsertResult = clientService.handleWriteInsertRequest(filePath, offset, data);
    System.out.println(writeInsertResult.isEmpty() ? "Error writing content in file."
        : "Write Content In File Result: " + writeInsertResult);
  }

  /**
   * Handles the monitor file command from the user.
   */
  private void handleMonitorFile() {
    String filePath = promptForNonEmptyString("Enter file path:");
    long monitorDuration = promptForLong("Enter monitor duration (in milliseconds):");
    String monitorResult = clientService.handleMonitorRequest(filePath, monitorDuration);
    System.out.println(monitorResult.isEmpty() ? "Error monitoring file."
        : "Monitor File Result: " + monitorResult);
  }


  /**
   * Handles the write delete content command from the user.
   */
  private void handleWriteDeleteContent() {
    String filePath = promptForNonEmptyString("Enter file path:");
    long offset = promptForLong("Enter offset:");
    long bytesToDelete = promptForLong("Enter number of bytes to delete:");
    String writeDeleteResult = clientService.handleWriteDeleteRequest(filePath, bytesToDelete,
        offset);
    System.out.println(writeDeleteResult.isEmpty() ? "Error deleting content in file."
        : "Delete Content In File Result: " + writeDeleteResult);
  }


  /**
   * Handles the file info command from the user.
   */

  private void handleFileInfo() {
    String filePath = promptForNonEmptyString("Enter file path:");
    String infoResult = clientService.handleFileInfoRequest(filePath);
    System.out.println(
        infoResult.isEmpty() ? "Error reading file info." : "== File Info ==\n" + infoResult);
  }

  /**
   * Prompts the user for a long value with a specific message and validates the input.
   *
   * @param message The message to display to the user.
   * @return The validated long value inputted by the user.
   */
  private long promptForLong(String message) {
    while (true) {
      try {
        System.out.println(message);
        long value = scanner.nextLong();
        scanner.nextLine(); // Consume newline
        return value;
      } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a numerical value.");
        scanner.nextLine(); // Consume the invalid input
      }
    }
  }

  /**
   * Prompts the user for a non-empty string with a specific message and validates the input.
   *
   * @param message The message to display to the user.
   * @return The non-empty string inputted by the user.
   */
  private String promptForNonEmptyString(String message) {
    String input;
    do {
      System.out.println(message);
      input = scanner.nextLine().trim();
      if (input.isEmpty()) {
        System.out.println("Input cannot be empty. Please try again.");
      }
    } while (input.isEmpty());
    return input;
  }
}
