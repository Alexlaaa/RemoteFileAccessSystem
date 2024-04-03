package strategy;

import common.Request;
import common.Response;
import java.net.InetAddress;
import server.ServerService;

/**
 * Server-side implementation of the at-least-once invocation semantics.
 */
public class AtLeastOnceStrategy implements ServerNetworkStrategy {

  /**
   * Process the request using at-least-once semantics. Every request is processed without checking
   * for duplication.
   *
   * @param request       The request to process.
   * @param serverService The server service to handle the request.
   * @param clientAddress The IP address of the client.
   * @param clientPort    The port number of the client.
   * @return The response to be sent back to the client.
   */
  @Override
  public Response processRequest(Request request, ServerService serverService,
      InetAddress clientAddress, int clientPort) {
    return serverService.processRequest(request, clientAddress, clientPort);
  }
}
