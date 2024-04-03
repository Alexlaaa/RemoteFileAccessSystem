package strategy;

import common.Request;
import common.Response;
import java.net.InetAddress;
import server.ServerService;

/**
 * Interface for server network strategies defining how server message handling is implemented.
 */
public interface ServerNetworkStrategy {

  /**
   * Process a request according to the server's network strategy.
   *
   * @param request       The request to process.
   * @param serverService The server service to handle the request.
   * @param clientAddress The IP address of the client.
   * @param clientPort    The port number of the client.
   * @return The response to be sent back to the client.
   */
  Response processRequest(Request request, ServerService serverService, InetAddress clientAddress,
      int clientPort);
}
