package client;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * ClientUI provides a command-line interface for users to interact with the file service. It allows
 * users to read, write, and monitor files on the server, and change network strategies.
 */
public class ClientUI {

  private ClientService clientService;
  private final Scanner scanner = new Scanner(System.in);

  /**
   * Setter for the client service.
   *
   * @param clientService The client service to use for handling client requests.
   */
  public void setClientService(ClientService clientService) {
    this.clientService = clientService;
  }

  /**
   * Starts the UI loop to accept user commands.
   *
   * @throws IOException If an I/O error occurs during command execution.
   */
  public void start() throws IOException {
    while (true) {
      System.out.println(
          "\nEnter command:\n1. Read File Content\n2. Write Content in File\n3. Monitor File\n4. Delete Content in File\n5. Read File Info\n6. Exit");
      int command = scanner.nextInt();
      scanner.nextLine();  // Consume newline

      switch (command) {
        case 1 -> handleReadFile();
        case 2 -> handleWriteInsertFile();
        case 3 -> handleMonitorFile();
        case 4 -> handleWriteDeleteContent();
        case 5 -> handleFileInfo();
        case 6 -> {
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
    String filePath = promptForNonEmptyString("\nEnter file path:");
    long offset = promptForLong("Enter offset:");
    long bytesToRead = promptForLong("Enter number of bytes to read:");
    String readResult = clientService.handleReadRequest(filePath, bytesToRead, offset);
    System.out.println(
        readResult.isEmpty() ? "\nError reading file." : "\n== Read Result ==\n" + readResult);
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
        : "== Write Content In File Result ==\n" + writeInsertResult);
  }

  /**
   * Handles the monitor file command from the user.
   */
  private void handleMonitorFile() {
    String filePath = promptForNonEmptyString("Enter file path:");
    long monitorDuration = promptForLong("Enter monitor duration (in milliseconds):");
    clientService.handleMonitorRequest(filePath, monitorDuration);
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
        : "== Delete Content In File Result ==\n" + writeDeleteResult);
  }


  /**
   * Handles the file info command from the user.
   */

  private void handleFileInfo() {
    String filePath = promptForNonEmptyString("Enter file path:");
    String infoResult = clientService.handleFileInfoRequest(filePath);
    System.out.println(
        infoResult.isEmpty() ? "Error reading file info."
            : "\n== File Info Result ==\n" + infoResult);
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

  /**
   * Prompts the user for an int value with a specific message and validates the input.
   *
   * @param message The message to display to the user.
   * @return The validated int value inputted by the user.
   */
  private int promptForInt(String message) {
    while (true) {
      try {
        System.out.println(message);
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return value;
      } catch (InputMismatchException e) {
        System.out.println("\nInvalid input. Please enter a numerical value.\n");
        scanner.nextLine(); // Consume the invalid input
      }
    }
  }

  /**
   * Prompts the user for a double value within the range of [0.0, 1.0] with a specific message and
   * validates the input.
   *
   * @param message The message to display to the user.
   * @return The validated double value inputted by the user.
   */
  private double promptForDouble(String message) {
    while (true) {
      try {
        System.out.println(message);
        double value = scanner.nextDouble();
        if (value >= 0.0 && value <= 1.0) {
          scanner.nextLine(); // Consume newline
          return value;
        } else {
          System.out.println("\nPlease enter a value between 0.0 and 1.0.");
        }
      } catch (InputMismatchException e) {
        System.out.println("\nInvalid input. Please enter a numerical value.");
        scanner.nextLine(); // Consume the invalid input
      }
    }
  }

  /**
   * Prompts the user to enter the port number for client to connect to the server.
   *
   * @return The port number entered by the user.
   */
  public int selectPort() {
    return promptForInt("\nEnter the port number for client to connect to the server:");
  }

  /**
   * Prompts the user to enter the server address.
   *
   * @return The server address entered by the user.
   */
  public String selectServerAddress() {
    return promptForNonEmptyString("\nEnter server address:");
  }

  /**
   * Prompts the user to enter the timeout for the client to wait for a response.
   *
   * @return The timeout in milliseconds for the client to wait for a response.
   */
  public int selectTimeout() {
    return promptForInt(
        "\nEnter the timeout in milliseconds for the client to wait for a response (e.g., 5000ms):");
  }

  /**
   * Prompts the user to enter the probability for the server to receive requests.
   *
   * @return The probability for the server to receive requests.
   */
  public double selectRequestSendProbability() {
    return promptForDouble(
        "\nEnter the probability (0.0 to 1.0) for the client to send requests:");
  }

  /**
   * Prompts the user to enter the probability for the server to send responses.
   *
   * @return The probability for the server to send responses.
   */
  public double selectReplyReceiveProbability() {
    return promptForDouble(
        "\nEnter the probability (0.0 to 1.0) for the client to receive responses:");
  }

  /**
   * Prompts the user to enter the maximum number of retries for sending a request on client
   * network.
   *
   * @return The maximum number of retries for sending a request on client network.
   */
  public int selectMaxRetries() {
    return promptForInt(
        "\nEnter the maximum number of retries for sending a request on client network (e.g., 10):");
  }

  /**
   * Prompts the user to enter the freshness interval for the client to consider a file fresh in the
   * cache.
   *
   * @return The freshness interval in milliseconds for the client to consider a file fresh.
   */
  public long selectFreshnessInterval() {
    return promptForLong(
        "\nEnter the freshness interval in milliseconds for the client to consider a file fresh in the cache (e.g., 30,000ms):");
  }
}
