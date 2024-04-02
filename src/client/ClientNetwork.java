package client;

import common.Constants;
import common.Constants.NetworkStrategyType;
import common.Request;
import common.Response;
import java.io.IOException;
import marshalling.Marshaller;
import marshalling.Unmarshaller;
import strategy.AtLeastOnceStrategy;
import strategy.AtMostOnceStrategy;
import strategy.NetworkStrategy;

/**
 * ClientNetwork acts as an intermediary between the network layer (ClientUDP) and the service layer
 * (ClientService), handling the transformation of data and invocation of the appropriate ClientUDP
 * methods.
 */
public class ClientNetwork {

  private final ClientUDP clientUDP;
  private NetworkStrategy networkStrategy;

  /**
   * Constructs a ClientNetwork with the specified ClientUDP and network strategy type.
   *
   * @param clientUDP           The UDP client used for network communication.
   * @param networkStrategyType The type of network strategy to use.
   */
  public ClientNetwork(ClientUDP clientUDP, NetworkStrategyType networkStrategyType) {
    this.clientUDP = clientUDP;
    setStrategy(networkStrategyType);
  }

  public void setStrategy(NetworkStrategyType strategyType) {
    switch (strategyType) {
      case AT_LEAST_ONCE:
        this.networkStrategy = new AtLeastOnceStrategy(this.clientUDP, 10);
        break;
      case AT_MOST_ONCE:
        this.networkStrategy = new AtMostOnceStrategy(this.clientUDP);
        break;
      default:
        throw new IllegalArgumentException("Unknown strategy type");
    }
  }

  /**
   * Sends a Request to the server and waits for a Response.
   *
   * @param request The Request object to send.
   * @return The Response object received from the server.
   * @throws IOException if network communication fails.
   */
  public Response sendRequest(Request request) throws IOException {
    System.out.println(
        "In ClientNetwork: Sending request to server:\n" + request.toString() + "\n");
    byte[] requestData = Marshaller.marshal(request);
    byte[] responseData = networkStrategy.sendAndReceive(requestData);
    if (responseData == null || responseData.length == 0) {
      // Handle the case where no response is received
      return new Response(Constants.StatusCode.GENERAL_ERROR, null,
          "No response received"); // TODO: Handle this better, perhaps with PACKET_LOSS_FROM_SERVER kind of error code?
    }
    Response response = Unmarshaller.unmarshalResponse(responseData);
    System.out.println(
        "In ClientNetwork: Received response from server:\n" + response.toString() + "\n");
    return response;
  }
}
