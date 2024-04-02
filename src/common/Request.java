package common;

/**
 * Represents a request from the client to the server in a remote file access system. Encapsulates
 * details about the operation to be performed on the server-side file system.
 */
public class Request {

  private final long requestId; // Unique ID for the request
  private final Constants.OperationType operationType; // Defines the type of operation
  private final String filePath; // The file path on the server
  private final long bytesToRead; // Number of bytes to read for read operations
  private final long offset; // Offset for read/write operations
  private final byte[] data; // Data to be written to the file for write operations
  private final long monitorDuration; // Duration for monitoring file updates for monitoring operations (in milliseconds)

  /**
   * Constructor for read operations.
   *
   * @param operationType The type of operation (READ).
   * @param filePath      The path of the file on the server.
   * @param bytesToRead   The number of bytes to read from the file.
   * @param offset        The offset for the read operation (in bytes).
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      long bytesToRead,
      long offset) {
    this(requestId, operationType, filePath, bytesToRead, offset, null,
        0);  // data and monitorDuration are not applicable for read operations
  }

  /**
   * Constructor for write operations.
   *
   * @param operationType The type of operation (WRITE).
   * @param filePath      The path of the file on the server.
   * @param offset        The offset for the write operation (in bytes).
   * @param data          The data to be written to the file.
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      long offset, byte[] data) {
    this(requestId, operationType, filePath, 0, offset, data,
        0);  // monitorDuration is not applicable for write operations
  }

  /**
   * Constructor for the monitor operation.
   *
   * @param operationType   The type of operation (MONITOR).
   * @param filePath        The path of the file on the server to be monitored.
   * @param monitorDuration The duration for which file updates should be monitored (in
   *                        milliseconds).
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      boolean isMonitor,
      long monitorDuration) {
    this(requestId, operationType, filePath, 0, 0, null,
        monitorDuration); // bytesToRead, offset and data are not applicable for monitor operations
  }

  // Private constructor to handle all initializations
  private Request(long requestId, Constants.OperationType operationType, String filePath,
      long bytesToRead,
      long offset, byte[] data,
      long monitorDuration) {
    this.requestId = requestId;
    this.operationType = operationType;
    this.filePath = filePath;
    this.bytesToRead = bytesToRead;
    this.offset = offset;
    this.data = data;
    this.monitorDuration = monitorDuration;
  }

  //TODO: Implement 2 more constructors for the 2 custom operations

  // Getters
  public long getRequestId() {
    return requestId;
  }

  public Constants.OperationType getOperationType() {
    return operationType;
  }

  public String getFilePath() {
    return filePath;
  }

  public long getBytesToRead() {
    return bytesToRead;
  }

  public long getOffset() {
    return offset;
  }

  public byte[] getData() {
    return data;
  }

  public long getMonitorDuration() {
    return monitorDuration;
  }

  // toString() method for sanity check
  @Override
  public String toString() {
    return "Request{" +
        "requestId=" + requestId +
        ", operationType=" + operationType +
        ", filePath='" + filePath + '\'' +
        ", bytesToRead=" + bytesToRead +
        ", offset=" + offset +
        ", data=" + (data == null ? "null" : new String(data)) +
        ", monitorDuration=" + monitorDuration +
        '}';
  }
}
