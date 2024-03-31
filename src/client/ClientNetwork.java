package client;

import common.Request;
import common.Response;
import marshalling.Marshaller;
import marshalling.Unmarshaller;

import java.io.IOException;

/**
 * ClientNetwork acts as an intermediary between the network layer (ClientUDP)
 * and the service layer (ClientService), handling the transformation of data
 * and invocation of the appropriate ClientUDP methods.
 */
public class ClientNetwork {
  private final ClientUDP clientUDP;

  public ClientNetwork(ClientUDP clientUDP) {
    this.clientUDP = clientUDP;
  }

  /**
   * Sends a Request to the server and waits for a Response.
   * @param request The Request object to send.
   * @return The Response object received from the server.
   * @throws IOException if network communication fails.
   */
  public Response sendRequest(Request request) throws IOException {
    byte[] requestData = Marshaller.marshal(request);
    byte[] responseData = clientUDP.sendAndReceive(requestData);
    if (responseData == null || responseData.length == 0) {
      // Handle the case where no response is received
      return new Response(null, new byte[0], "No response received"); // TODO: Define a proper statusCode
    }
    return Unmarshaller.unmarshalResponse(responseData);
  }
}
