package strategy;

/**
 * Interface for network strategies defining how message transmissions are handled.
 */
public interface NetworkStrategy {

  /**
   * Sends a request and waits for a response, implementing a specific network strategy.
   *
   * @param request The request to send.
   * @return The response received.
   */
  byte[] sendAndReceive(byte[] request);
}
