package client;

import common.Constants;
import common.Request;
import common.Response;
import java.io.IOException;
import marshalling.Marshaller;
import marshalling.Unmarshaller;

/**
 * ClientNetwork acts as an intermediary between the network layer (ClientUDP) and the service layer
 * (ClientService), handling the sending of requests and receiving of responses.
 */
public class ClientNetwork {

  private final ClientUDP clientUDP;
  private final int maxRetries; // The maximum number of retries for sending a message.

  /**
   * Constructs a ClientNetwork with the specified ClientUDP.
   *
   * @param clientUDP  The UDP client used for network communication.
   * @param maxRetries The maximum number of retries for sending a message.
   */
  public ClientNetwork(ClientUDP clientUDP, int maxRetries) {
    this.clientUDP = clientUDP;
    this.maxRetries = maxRetries;
  }

  /**
   * Sends a Request to the server and waits for a Response, retrying as necessary.
   *
   * @param request The Request object to send.
   * @return The Response object received from the server.
   * @throws IOException if network communication fails.
   */
  public Response sendRequest(Request request) throws IOException {
    System.out.println("\nIn ClientNetwork: Sending request to server:\n" + request);

    byte[] requestData = Marshaller.marshal(request);
    byte[] responseData = null;
    int attempts = 0;

    // Retry sending the request until a valid response (non-null and non-empty) is received or the maximum number of retries
    while ((responseData == null || responseData.length == 0) && attempts < maxRetries) {
      responseData = clientUDP.sendAndReceive(requestData);
      if (responseData == null || responseData.length == 0) {
        System.out.println("\nNo or empty response received, retrying attempt " + (attempts + 1));
        attempts++;
      }
    }

    // If no valid response is received after maxRetries, return a NETWORK_ERROR response
    if (responseData == null || responseData.length == 0) {
      System.out.println(
          "\nIn ClientNetwork: No valid response received after " + maxRetries + " attempts.");
      return new Response(Constants.StatusCode.NETWORK_ERROR, null, "No valid response received.",
          -1);
    }

    // Else there is a non-null and potentially valid response, unmarshal it
    Response response = Unmarshaller.unmarshalResponse(responseData);
    // Validate the response
    if (!isValidResponse(response)) {
      System.out.println(
          "\nIn ClientNetwork: Received an invalid response:\n" + response.toString() + "\n");
      return new Response(Constants.StatusCode.GENERAL_ERROR, null, "Invalid response received.",
          -1);
    }
    // Response is validated
    System.out.println(
        "\nIn ClientNetwork: Received a valid response from server:\n" + response + "\n");
    return response;
  }

  /**
   * Gets the ClientUDP instance associated with this network.
   *
   * @return The ClientUDP instance.
   */
  public ClientUDP getClientUDP() {
    return clientUDP;
  }

  /**
   * Checks if the response is valid.
   *
   * @param response The response to validate.
   * @return True if the response is valid, false otherwise.
   */
  private boolean isValidResponse(Response response) {
    return response != null && response.getStatusCode() != null;
  }
}
