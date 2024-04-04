package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The ServerUDP class listens for UDP packets from clients, processes them, and sends responses. It
 * continuously listens on a specified port, and upon receiving a message, it sends a response back
 * to the client.
 */
public class ServerUDP {

  private final DatagramSocket socket;
  private boolean running;
  private final byte[] buf = new byte[1024];  // Buffer size for incoming messages
  private final double requestReceiveProbability;
  private final double replySendProbability;
  private final ServerNetwork serverNetwork;

  /**
   * Constructs a ServerUDP to listen on the specified port.
   *
   * @param port The port number on which the server will listen.
   * @throws SocketException if the socket could not be opened.
   */
  public ServerUDP(int port, double requestReceiveProbability, double replySendProbability,
      ServerNetwork serverNetwork) throws SocketException {
    this.socket = new DatagramSocket(port);
    this.requestReceiveProbability = requestReceiveProbability;
    this.replySendProbability = replySendProbability;
    this.serverNetwork = serverNetwork;
  }

  /**
   * Listens for incoming datagrams (marshalled Requests), processes them, and sends Responses.
   */
  public void listen() {
    running = true;
    try {
      while (running) {
        running = receiveAndSend(); // Continue running if no shutdown signal is received
      }
    } catch (IOException e) {
      System.err.println("IOException in listen: " + e.getMessage());
    } finally {
      close();
    }
  }

  /**
   * Receives a UDP packet, processes the message, and decides whether to continue running. If the
   * message is successfully processed, a response is sent back to the client.
   *
   * @return A boolean indicating whether the server should continue running.
   * @throws IOException if an I/O error occurs.
   */
  private boolean receiveAndSend() throws IOException {
    if (!toReceive()) {
      System.out.println("Packet loss from client to server.");
      return true; // Continue running even if there's packet loss
    }

    DatagramPacket packet = new DatagramPacket(buf,
        buf.length); // Create a packet to store the incoming data
    socket.receive(packet); // Receive the incoming data and store it in the packet

    byte[] responseData = serverNetwork.processRequest(
        packet.getData(), packet.getAddress(),
        packet.getPort()); // Process the incoming data and get a response
    if (responseData != null) {
      if (serverNetwork.isShutdownResponse(responseData)) {
        return false; // Stop running if shutdown signal is received
      } else {
        send(responseData, packet.getAddress(), packet.getPort());
      }
    }
    return true; // Continue running by default
  }

  /**
   * Sends a UDP packet to the specified client address and port. Also used to send updates to
   * clients from the monitoring service (directly called from ServerMonitorService, hence needs to
   * be public visibility).
   *
   * @param responseData  The byte array containing the response data.
   * @param clientAddress The client's IP address.
   * @param clientPort    The client's port number.
   * @throws IOException if an I/O error occurs.
   */
  public void send(byte[] responseData, InetAddress clientAddress, int clientPort)
      throws IOException {
    if (!toSend()) {
      System.out.println("Packet loss from server to client.\n");
      return;
    }

    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
        clientAddress, clientPort); // Create a packet to store the response data
    socket.send(responsePacket); // Send the response data to the client
    System.out.println(
        "Packet of length " + responsePacket.getLength() + " bytes sent to client.\n");
  }

  /**
   * Stops the server from listening for further messages and releases any system resources
   * associated with it.
   */
  public void close() {
    System.out.println("Server closed.");
    running = false;
    if (!socket.isClosed()) {
      socket.close();
    }
  }

  /**
   * Determines if a packet should be received based on the request receive probability.
   *
   * @return true if a packet should be received, false otherwise
   */
  public boolean toReceive() {
    return Math.random() < requestReceiveProbability;
  }

  /**
   * Determines if a packet should be sent based on the reply send probability.
   *
   * @return true if a packet should be sent, false otherwise
   */
  public boolean toSend() {
    return Math.random() < replySendProbability;
  }
}
