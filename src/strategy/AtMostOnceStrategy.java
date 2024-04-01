package strategy;

import client.ClientUDP;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the at-most-once invocation semantics for network communication. This strategy ensures
 * that a message is delivered and processed no more than once by the server, which is crucial for
 * non-idempotent operations to avoid duplicate processing.
 */
public class AtMostOnceStrategy implements NetworkStrategy {

  private final ClientUDP clientUDP; // The UDP client used for sending and receiving messages.
  private final Map<String, byte[]> responseCache; // Cache to store responses for deduplication.

  /**
   * Constructs an instance of AtMostOnceStrategy with a specified UDPClient.
   *
   * @param clientUDP The UDP client used for network communication.
   */
  public AtMostOnceStrategy(ClientUDP clientUDP) {
    this.clientUDP = clientUDP;
    this.responseCache = new HashMap<>();
  }

  /**
   * Sends a message and waits for a response, ensuring no duplicate requests are processed. It uses
   * a cache to remember previous responses and avoids reprocessing of requests.
   *
   * @param request The request message to send as a byte array.
   * @return The response message as a byte array, fetched from cache if available.
   */
  @Override
  public byte[] sendAndReceive(byte[] request) {
    // Generate a key for the cache based on the request content.
    String cacheKey = new String(
        request); // A simplistic approach; consider a more robust method for real applications.

    // Check if the response for this request is already in the cache.
    if (responseCache.containsKey(cacheKey)) {
      return responseCache.get(cacheKey);
    }

    // If not in the cache, send the request and wait for the response.
    byte[] response = null;
    try {
      response = clientUDP.sendAndReceive(request);
      // If a response is received, store it in the cache.
      if (response != null && response.length > 0) {
        responseCache.put(cacheKey, response);
      }
    } catch (IOException e) {
      // Handle IOExceptions as necessary; the response will remain null.
    }

    // Return the received response, which could be null in case of an error.
    return response;
  }
}
