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

    // Loop until a response is received or the maximum number of retries is reached.
    while (responseData == null && attempts < maxRetries) {
      try {
        responseData = clientUDP.sendAndReceive(requestData);
        // If the response is empty, it is considered a failed attempt.
        if (responseData == null || responseData.length == 0) {
          System.out.println("\nNo response received, retrying attempt " + (attempts + 1));
          attempts++;
          responseData = null; // Reset response data for the next attempt
        }
      } catch (IOException e) {
        System.out.println("\nIOException occurred: " + e.getMessage());
        attempts++;
      }
    }

    // If after all retries no response is received, return an error response.
    if (responseData == null) {
      System.out.println(
          "\nIn ClientNetwork: Failed to receive a response after " + maxRetries + " attempts.");
      return new Response(Constants.StatusCode.GENERAL_ERROR, null,
          "No response received after maximum retries.", -1);
    }

    // Unmarshal the response and return it.
    Response response = Unmarshaller.unmarshalResponse(responseData);
    System.out.println("\nIn ClientNetwork: Received response from server.");
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
}
