package server;

import common.Constants;
import java.util.InputMismatchException;
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
  public int selectPort() {
    return promptForInt("\nEnter the port number for the server to listen on:");
  }

  /**
   * Prompts the user to select the invocation strategy for the server.
   *
   * @return The selected invocation strategy.
   */
  public Constants.NetworkStrategyType selectStrategyType() {
    int strategyChoice = promptForInt(
        "\nSelect Invocation Type:\n1. At Least Once\n2. At Most Once");
    while (strategyChoice != 1 && strategyChoice != 2) {
      strategyChoice = promptForInt(
          "\nInvalid choice. Select Invocation Type:\n1. At Least Once\n2. At Most Once");
    }

    return strategyChoice == 1 ? Constants.NetworkStrategyType.AT_LEAST_ONCE
        : Constants.NetworkStrategyType.AT_MOST_ONCE;
  }

  /**
   * Prompts the user to enter the probability for the server to receive requests.
   *
   * @return The probability for the server to receive requests.
   */
  public double selectRequestReceiveProbability() {
    return promptForDouble(
        "\nEnter the probability (0.0 to 1.0) for the server to receive requests:");
  }

  /**
   * Prompts the user to enter the probability for the server to send responses.
   *
   * @return The probability for the server to send responses.
   */
  public double selectReplySendProbability() {
    return promptForDouble(
        "\nEnter the probability (0.0 to 1.0) for the server to send responses:");
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
}
