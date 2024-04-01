package client;

import common.Constants;
import common.Request;
import common.Response;
import java.io.IOException;

/**
 * ClientService acts as the intermediary between the client UI and the network layer, facilitating
 * the sending of requests and receiving of responses.
 */
public class ClientService {

  private ClientNetwork clientNetwork;

  /**
   * Constructs a ClientService instance with a specified ClientNetwork.
   *
   * @param clientNetwork The network layer this service will interact with.
   */
  public ClientService(ClientNetwork clientNetwork) {
    this.clientNetwork = clientNetwork;
  }

  /**
   * Sets the network layer this service interacts with.
   *
   * @param clientNetwork The ClientNetwork to be used by this service.
   */
  public void setClientNetwork(ClientNetwork clientNetwork) {
    this.clientNetwork = clientNetwork;
  }

  /**
   * Handles a request to read a file from the server.
   *
   * @param filePath    The path of the file to read.
   * @param bytesToRead The number of bytes to read from the file.
   * @param offset      The offset from where to start reading the file.
   * @return The string representation of the read data or an error message.
   */
  public String handleReadRequest(String filePath, long bytesToRead, long offset) {
    Request request = new Request(System.nanoTime(), Constants.OperationType.READ, filePath,
        bytesToRead,
        offset);
    try {
      Response response = clientNetwork.sendRequest(request);
      return new String(response.getData()); // Return the response for the UI to display
    } catch (IOException e) {
      return "Error sending read request: " + e.getMessage();
    }
  }

  /**
   * Handles a request to write data to a file on the server.
   *
   * @param filePath The path of the file where data needs to be written.
   * @param offset   The offset at which data should be written in the file.
   * @param data     The data to write to the file.
   * @return Acknowledgement message or an error message.
   */
  public String handleWriteRequest(String filePath, long offset, String data) {
    Request request = new Request(System.nanoTime(), Constants.OperationType.WRITE, filePath,
        offset, data.getBytes());
    try {
      Response response = clientNetwork.sendRequest(request);
      return response.getMessage(); // Return the server's response message
    } catch (IOException e) {
      return "Error sending write request: " + e.getMessage();
    }
  }

  /**
   * Handles a request to monitor a file on the server for changes.
   *
   * @param filePath        The path of the file to monitor.
   * @param monitorDuration The duration for which to monitor the file.
   * @return A message indicating the status of the monitor request or an error message.
   */
  public String handleMonitorRequest(String filePath, long monitorDuration) {
    Request request = new Request(System.nanoTime(), Constants.OperationType.MONITOR, filePath,
        true, monitorDuration);
    try {
      Response response = clientNetwork.sendRequest(request);
      return response.getMessage(); // Return the server's response message
    } catch (IOException e) {
      return "Error sending monitor request: " + e.getMessage();
    }
  }

  // TODO: Add methods for the 2 additional operations

}

