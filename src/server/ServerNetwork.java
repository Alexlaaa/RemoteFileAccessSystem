package server;

import common.Constants;
import common.Request;
import common.Response;
import java.io.IOException;
import marshalling.Unmarshaller;
import marshalling.Marshaller;

/**
 * ServerNetwork acts as an intermediary between the network layer (ServerUDP)
 * and the service layer (ServerService), handling the transformation of data
 * and invocation of the appropriate service methods.
 */
public class ServerNetwork {

  private final ServerService serverService;

  /**
   * Constructs a ServerNetwork with the specified ServerService.
   *
   * @param serverService The service layer that will process the requests.
   */
  public ServerNetwork(ServerService serverService) {
    this.serverService = serverService;
  }

  /**
   * Processes the received request, invokes the appropriate service method, and prepares the response.
   *
   * @param data The received byte array.
   * @return The byte array to be sent as a response.
   */
  public byte[] processRequest(byte[] data) throws IOException {
    Request request = Unmarshaller.unmarshalRequest(data);
    Response response;

    if (request.getOperationType() == Constants.OperationType.SHUTDOWN_SERVER) {
      response = new Response(Constants.StatusCode.SHUTDOWN, null, "Server will shutdown");
    } else {
      response = serverService.processRequest(request);
    }

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
