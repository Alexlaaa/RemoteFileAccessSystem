package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * The ClientUDP class is responsible for sending data to and receiving data from a server via UDP
 * protocol. It allows for sending a string message to a specified server address and port and then
 * waits for a response.
 */
public class ClientUDP {

  private final DatagramSocket socket;
  private final InetAddress serverAddress;
  private final int serverPort;
  private final double requestSendProbability;
  private final double replyReceiveProbability;

  /**
   * Constructs a ClientUDP with the specified server address and port.
   *
   * @param serverAddress           The address of the UDP server.
   * @param serverPort              The port number of the UDP server.
   * @param timeout                 The timeout in milliseconds for receiving a response.
   * @param requestSendProbability  The probability of successfully sending a message from client to
   *                                server (0.0 to 1.0).
   * @param replyReceiveProbability The probability of successfully receiving a message from server
   *                                to client (0.0 to 1.0).
   * @throws IOException if an I/O error occurs.
   */
  public ClientUDP(String serverAddress, int serverPort, int timeout, double requestSendProbability,
      double replyReceiveProbability) throws IOException {
    this.serverAddress = InetAddress.getByName(serverAddress);
    this.serverPort = serverPort;
    this.socket = new DatagramSocket();
    this.socket.setSoTimeout(timeout); // Set the timeout for receive
    this.requestSendProbability = requestSendProbability;
    this.replyReceiveProbability = replyReceiveProbability;
  }

  /**
   * Sends a byte array to the server and awaits a response, considering the probabilities of
   * sending and receiving.
   *
   * @param data Byte array to send.
   * @return Received byte array from the server or null if sending/receiving failed.
   * @throws IOException if an I/O error occurs.
   */
  public byte[] sendAndReceive(byte[] data) throws IOException {
    // Preparing to send
    if (!toSend()) {
      System.out.println("Packet loss from client to server.");
      return null;
    }
    DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, serverPort);
    socket.send(sendPacket);

    // Preparing to receive
    if (!toReceive()) {
      System.out.println("Packet loss from server to client.");
      return new byte[0]; // Returns an empty array to indicate no data received due to packet loss
    }
    byte[] receiveData = new byte[1024]; // Allocate a byte array to store the incoming data
    DatagramPacket receivePacket = new DatagramPacket(receiveData,
        receiveData.length); // Create a packet to store the incoming data with the allocated byte array

    try {
      socket.receive(
          receivePacket); // Attempt to receive the packet, this call is blocking and will wait until a packet is received or the timeout is reached
    } catch (SocketTimeoutException e) {
      System.err.println("Timeout reached: " + e.getMessage());
      close(); // to remove?
      return new byte[0]; // Returns an empty array to indicate no data received due to timeout
    }
    byte[] responseData = new byte[receivePacket.getLength()]; // Allocate a byte array to store the received data from the receivePacket, with length equal to that of the received data
    System.arraycopy(receiveData, 0, responseData, 0,
        receivePacket.getLength()); // Copy the received data from the receivePacket to the responseData array
    return responseData;
  }

  /**
   * Listens for updates from the server. This method blocks until a packet is received or a timeout
   * occurs.
   *
   * @return The data received in the update, or null if no data is received within the timeout
   * period.
   * @throws IOException if an I/O error occurs while receiving the data.
   */
  public byte[] listenForUpdates() throws IOException {
    // Create a DatagramPacket to receive the data
    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

    try {
      // Wait for a packet to be received. This call blocks until either a packet is received
      // or a timeout occurs, as set by socket.setSoTimeout(timeout).
      socket.receive(packet);

      // Copy the received data into a new array, trimming any excess bytes
      return Arrays.copyOf(packet.getData(), packet.getLength());
    } catch (SocketTimeoutException e) {
      // If a timeout occurs, return null to indicate that no data was received
      return null;
    }
  }

  /**
   * Closes the socket and releases any system resources associated with it.
   */
  public void close() {
    if (!socket.isClosed()) {
      socket.close();
    }
  }

  /**
   * Determines if a packet should be sent based on the request send probability.
   *
   * @return true if a packet should be sent, false otherwise
   */
  public boolean toSend() {
    return Math.random() < requestSendProbability;
  }

  /**
   * Determines if a packet should be received based on the reply receive probability.
   *
   * @return true if a packet should be received, false otherwise
   */
  public boolean toReceive() {
    return Math.random() < replyReceiveProbability;
  }
}
