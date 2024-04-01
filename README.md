### SC4051-RemoteFileAccessSystem

## **Client Side (Top-Down then Bottom-Up)**

1. **Top-Down (Request Phase):**
    - **ClientService (Top Layer):** Initiates the action by creating a **`Request`** object based on the user's command or programmatic request.
    - **ClientNetwork (Middle Layer):** Receives the **`Request`** object, marshals it into a byte array, and sends it to the network layer using UDPClient. It also implements network strategies (like AtLeastOnce or AtMostOnce).
    - **UDPClient (Bottom Layer):** Sends the byte array over the network to the server.
2. **Bottom-Up (Response Phase):**
    - **UDPClient (Bottom Layer):** Receives the byte array response from the server.
    - **ClientNetwork (Middle Layer):** Unmarshals the byte array into a **`Response`** object and performs any necessary processing or error handling.
    - **ClientService (Top Layer):** Processes the **`Response`** object, updating the UI or state as necessary based on the response.

## **Server Side (Bottom-Up then Top-Down)**

1. **Bottom-Up (Request Processing Phase):**
    - **UDPServer (Bottom Layer):** Receives the byte array request from the client.
    - **ServerNetwork (Middle Layer):** Unmarshals the byte array into a **`Request`** object and forwards it to the service layer for processing.
    - **ServerService (Top Layer):** Processes the request (e.g., read/write/monitor files) and creates a **`Response`** object.
2. **Top-Down (Response Sending Phase):**
    - **ServerService (Top Layer):** Generates a **`Response`** object after processing the request.
    - **ServerNetwork (Middle Layer):** Marshals the **`Response`** object into a byte array and sends it to the network layer.
    - **UDPServer (Bottom Layer):** Sends the byte array response back to the client.
