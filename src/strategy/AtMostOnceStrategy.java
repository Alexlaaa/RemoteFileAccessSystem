package strategy;

import common.Request;
import common.Response;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import server.ServerService;

/**
 * Server-side implementation of the at-most-once invocation semantics. Duplicate requests are
 * filtered out and the server will not re-execute the operation for a duplicate request. Instead,
 * the server will return the cached response. This ensures that the operation is executed at most
 * once.
 */
public class AtMostOnceStrategy implements ServerNetworkStrategy {

  private final Map<Long, Response> responseCache = new ConcurrentHashMap<>();

  /**
   * Process the request using at-most-once semantics. Checks if the request has been processed
   * before and uses the cached response.
   *
   * @param request       The request to process.
   * @param serverService The server service to handle the request.
   * @param clientAddress The IP address of the client.
   * @param clientPort    The port number of the client.
   * @return The response to be sent back to the client, or null if it's a duplicate request.
   */
  @Override
  public Response processRequest(Request request, ServerService serverService,
      InetAddress clientAddress, int clientPort) {
    // Use request identifier to check for duplicates
    long requestId = request.getRequestId();

    System.out.println("Received request with ID: " + requestId);
    if (responseCache.containsKey(requestId)) {
      // Return the cached response for a duplicate request
      System.out.println("Duplicate request received. Returning cached response:\n"
          + responseCache.get(requestId).toString() + "\n");
      return responseCache.get(requestId);
    } else {
      // Process the request as it is seen the first time and cache the response
      Response response = serverService.processRequest(request, clientAddress, clientPort);
      System.out.println("**New request received. Processing and caching response:**\n"
          + response.toString() + "\n");
      responseCache.put(requestId, response);
      return response;
    }
  }
}
