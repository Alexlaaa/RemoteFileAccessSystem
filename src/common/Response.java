package common;

public class Response {

  private final Constants.StatusCode statusCode; // The status code indicating success or failure
  private final byte[] data; // The data returned by the server, if any
  private final String message; // Additional message or information about the operation
  private final long lastModifiedTimeAtServer; // The last modified time of the file at the server

  /**
   * Constructs a Response object.
   *
   * @param statusCode               The status code indicating the result of the operation.
   * @param data                     The data returned from the server, if any. For read operations,
   *                                 this would be the file data.
   * @param message                  Additional information or message about the operation or its
   *                                 result.
   * @param lastModifiedTimeAtServer The last modified time of the file at the server.
   */
  public Response(Constants.StatusCode statusCode, byte[] data, String message,
      long lastModifiedTimeAtServer) {
    this.statusCode = statusCode;
    this.data = data;
    this.message = message;
    this.lastModifiedTimeAtServer = lastModifiedTimeAtServer;
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

  /**
   * Gets the last modified time of the file at the server.
   *
   * @return The last modified time in milliseconds.
   */
  public long getLastModifiedTimeAtServer() {
    return lastModifiedTimeAtServer;
  }

  /**
   * Returns a string representation of the Response object. For logging/debugging purposes.
   */
  @Override
  public String toString() {
    return "Response{" +
        "statusCode=" + statusCode +
        ", data=" + (data == null ? "null" : new String(data)) +
        ", message='" + message + '\'' +
        ", lastModifiedTimeAtServer=" + lastModifiedTimeAtServer +
        '}';
  }
}
