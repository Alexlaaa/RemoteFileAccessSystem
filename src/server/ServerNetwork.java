package server;

import common.Constants;
import common.Request;
import common.Response;
import java.net.InetAddress;
import marshalling.Marshaller;
import marshalling.Unmarshaller;
import strategy.AtLeastOnceStrategy;
import strategy.AtMostOnceStrategy;
import strategy.ServerNetworkStrategy;

/**
 * ServerNetwork acts as an intermediary between the network layer (ServerUDP) and the service layer
 * (ServerService), handling the transformation of data and invocation of the appropriate service
 * methods.
 */
public class ServerNetwork {

  private final ServerService serverService;
  private final ServerNetworkStrategy networkStrategy;

  /**
   * Constructs a ServerNetwork with the specified ServerService.
   *
   * @param serverService The service layer that will process the requests.
   * @param strategyType  The network strategy to use, either at-least-once or at-most-once.
   */
  public ServerNetwork(ServerService serverService, Constants.NetworkStrategyType strategyType) {
    this.serverService = serverService;
    // Initialize the network strategy based on the type
    if (strategyType == Constants.NetworkStrategyType.AT_LEAST_ONCE) {
      this.networkStrategy = new AtLeastOnceStrategy();
    } else {
      this.networkStrategy = new AtMostOnceStrategy();
    }
  }

  /**
   * Processes the received request, invokes the appropriate service method, and prepares the
   * response using the specified network strategy.
   *
   * @param data          The received byte array
   * @param clientAddress The IP address of the client.
   * @param clientPort    The port number of the client.
   * @return The byte array to be sent as a response.
   */
  public byte[] processRequest(byte[] data, InetAddress clientAddress, int clientPort) {
    Request request = Unmarshaller.unmarshalRequest(data);
    Response response = networkStrategy.processRequest(request, serverService, clientAddress,
        clientPort);
    System.out.println(
        "Marshalling the response to be sent to client:\n" + response.toString());

    return Marshaller.marshal(response);
  }

  /**
   * Checks if the response signals the server to shut down.
   *
   * @param responseData The byte array containing the response data.
   * @return True if the server should shut down, false otherwise.
   */
  public boolean isShutdownResponse(byte[] responseData) {
    // Unmarshal the response to check if it signals the server to shut down
    Response response = Unmarshaller.unmarshalResponse(responseData);
    // Assuming we check for a specific status code to signal shutdown
    return response.getStatusCode() == Constants.StatusCode.SHUTDOWN;
  }
}
