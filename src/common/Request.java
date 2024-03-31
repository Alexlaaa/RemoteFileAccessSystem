package common;

/**
 * Represents a request from the client to the server in a remote file access system.
 * Encapsulates details about the operation to be performed on the server-side file system.
 */
public class Request {
  private final Constants.OperationType operationType; // Defines the type of operation
  private final String filePath; // The file path on the server
  private final long offset; // Offset for read/write operations
  private final byte[] data; // Data to be written to the file for write operations
  private final long monitorDuration; // Duration for monitoring file updates for monitoring operations (in milliseconds)

  /**
   * Constructor for read operations.
   *
   * @param operationType The type of operation (READ).
   * @param filePath      The path of the file on the server.
   * @param offset        The offset for the read operation (in bytes).
   */
  public Request(Constants.OperationType operationType, String filePath, long offset) {
    this(operationType, filePath, offset, null, 0);  // Data and monitor duration are not applicable for read operations
  }

  /**
   * Constructor for write operations.
   *
   * @param operationType The type of operation (WRITE).
   * @param filePath      The path of the file on the server.
   * @param offset        The offset for the write operation (in bytes).
   * @param data          The data to be written to the file.
   */
  public Request(Constants.OperationType operationType, String filePath, long offset, byte[] data) {
    this(operationType, filePath, offset, data, 0);  // Monitor duration is not applicable for write operations
  }

  /**
   * Constructor for the monitor operation.
   *
   * @param operationType   The type of operation (MONITOR).
   * @param filePath        The path of the file on the server to be monitored.
   * @param monitorDuration The duration for which file updates should be monitored (in milliseconds).
   */
  public Request(Constants.OperationType operationType, String filePath, boolean isMonitor, long monitorDuration) {
    this(operationType, filePath, 0, null, monitorDuration); // Offset and data are not applicable for monitor operations
  }

  // Private constructor to handle all initializations
  private Request(Constants.OperationType operationType, String filePath, long offset, byte[] data, long monitorDuration) {
    this.operationType = operationType;
    this.filePath = filePath;
    this.offset = offset;
    this.data = data;
    this.monitorDuration = monitorDuration;
  }

  //TODO: Implement 2 more constructors for the 2 custom operations

  // Getters
  public Constants.OperationType getOperationType() { return operationType; }
  public String getFilePath() { return filePath; }
  public long getOffset() { return offset; }
  public byte[] getData() { return data; }
  public long getMonitorDuration() { return monitorDuration; }
}
