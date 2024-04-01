package server;

import java.util.Scanner;

/**
 * ServerUI provides a user interface for configuring server settings, specifically allowing the
 * user to set the port number on which the server should listen for incoming connections.
 */
public class ServerUI {

  private final Scanner scanner = new Scanner(System.in);

  /**
   * Prompts the user to enter the port number for the server to listen on.
   *
   * @return The port number entered by the user.
   */
  public int configurePort() {
    System.out.println("Enter the port number for the server to listen on:");
    int port = scanner.nextInt();
    scanner.nextLine(); // Consume newline left-over to handle next input correctly
    return port;
  }
}
