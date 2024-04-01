package strategy;

import client.ClientUDP;
import java.io.IOException;

/**
 * Implements the at-least-once invocation semantics for network communication. This strategy
 * ensures that a message is delivered and processed at least once by the server. It is suitable for
 * idempotent operations where duplicate processing does not affect the system adversely.
 */
public class AtLeastOnceStrategy implements NetworkStrategy {

  private final ClientUDP clientUDP; // The UDP client used for sending and receiving messages.
  private final int maxRetries; // The maximum number of retries for sending a message.

  /**
   * Constructs an instance of AtLeastOnceStrategy with a specified UDPClient and max retries.
   *
   * @param clientUDP  The UDP client used for network communication.
   * @param maxRetries The maximum number of times to retry sending a message.
   */
  public AtLeastOnceStrategy(ClientUDP clientUDP, int maxRetries) {
    this.clientUDP = clientUDP;
    this.maxRetries = maxRetries;
  }

  /**
   * Sends a message and waits for a response, retrying up to maxRetries times if necessary. This
   * method implements the at-least-once invocation semantics.
   *
   * @param request The request message to send as a byte array.
   * @return The response message as a byte array or null if all attempts fail.
   */
  @Override
  public byte[] sendAndReceive(byte[] request) {
    byte[] response = null;
    int attempts = 0;

    // Loop until a response is received or the maximum number of retries is reached.
    while (response == null && attempts < maxRetries) {
      try {
        response = clientUDP.sendAndReceive(request);
        // If the response is empty, increment the attempt counter and retry.
        if (response == null || response.length == 0) {
          attempts++;
          continue;
        }
        // Return the response if it's received successfully.
        return response;
      } catch (IOException e) {
        // If an IOException occurs, log it or handle it as necessary.
        attempts++;
      }
    }
    // Return the response, which could be null if all retries failed.
    return response;
  }
}
