public class Main {
    public static void main(String[] args) {
//        // Server should be running in a separate thread as it's a blocking operation.
//        Thread serverThread = new Thread(() -> {
//            try {
//                ServerUDP server = new ServerUDP(6789, 1, 1);
//                System.out.println("Server started.");
//                server.listen(); // Start listening for incoming messages.
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        serverThread.start(); // Start server thread.
//        System.out.println("Server thread started.");
//
//        try {
//            // Give the server a moment to start up.
//            Thread.sleep(1000);
//
//            // Now start the client.
//            ClientUDP theClient = new ClientUDP("localhost", 6789, 2000, 1, 1);
//            String message = "Hello from the client!";
//            System.out.println("Client sent: " + message);
//
//            // Send message to server and receive response from server.
//            String response = theClient.sendMessage(message);
//            if (response != null) {
//                System.out.println("Client received: " + response);
//            } else {
//                System.out.println("No response received or there was a timeout.");
//            }
//
//            // Close the server.
//            String message2 = "end";
//            System.out.println("Client sent: " + message2);
//            theClient.sendMessage(message2); // Send 'end' message to server to stop it.
//            Thread.sleep(1000); // Give the server a moment to close before closing the thread.
//            serverThread.interrupt();
//            System.out.println("Server thread stopped.");
//
//            // Close the client.
//            theClient.close();
//            System.out.println("Client closed.");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }

        // Instantiate in order!
        //ServerService serverService = new ServerService(); // Assuming ServerService doesn't depend on ServerUDP or ServerNetwork
        //ServerNetwork serverNetwork = new ServerNetwork(serverService); // Create ServerNetwork with reference to ServerService
        //ServerUDP serverUDP = new ServerUDP(port, receiveProb, sendProb, serverNetwork); // Create ServerUDP with reference to ServerNetwork
    }
}
