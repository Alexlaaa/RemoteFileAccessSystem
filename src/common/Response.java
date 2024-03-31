package common;

public class Response {
  private final Constants.StatusCode statusCode; // The status code indicating success or failure
  private final byte[] data; // The data returned by the server, if any
  private final String message; // Additional message or information about the operation

  /**
   * Constructs a Response object.
   *
   * @param statusCode The status code indicating the result of the operation.
   * @param data       The data returned from the server, if any. For read operations, this would be the file data.
   * @param message    Additional information or message about the operation or its result.
   */
  public Response(Constants.StatusCode statusCode, byte[] data, String message) {
    this.statusCode = statusCode;
    this.data = data;
    this.message = message;
  }

  /**
   * Gets the status code of the response.
   *
   * @return The status code.
   */
  public Constants.StatusCode getStatusCode() {
    return statusCode;
  }

  /**
   * Gets the data included in the response.
   *
   * @return The data byte array.
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Gets the message included in the response.
   *
   * @return The message string.
   */
  public String getMessage() {
    return message;
  }
}
