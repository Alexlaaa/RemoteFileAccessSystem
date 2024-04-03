package client;

import java.io.IOException;

/**
 * The ClientMain class serves as the entry point for the client-side application. It initializes
 * the ClientService and ClientUI and starts the user interface.
 */
public class ClientMain {

  public static void main(String[] args) throws IOException {
    // Initialization of ClientNetwork and ClientUDP are deferred to ClientUI
    // Network strategy selection for ClientService is also deferred to ClientUI
    ClientService clientService = new ClientService(
        null, 20000);  // Initially null, will be set up later in ClientUI based on user input
    ClientUI clientUI = new ClientUI(clientService);
    clientUI.start();
  }
}

