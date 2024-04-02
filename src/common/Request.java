package common;

/**
 * Represents a request from the client to the server in a remote file access system. Encapsulates
 * details about the operation to be performed on the server-side file system.
 */
public class Request {

  private final long requestId; // Unique ID for the request
  private final Constants.OperationType operationType; // Defines the type of operation to be performed
  private final String filePath; // The file path on the server
  private final long bytesToReadOrDelete; // Number of bytes to read/delete for READ/WRITE_DELETE operations
  private final long offset; // Offset for READ/WRITE_INSERT/WRITE_DELETE operations
  private final byte[] data; // Data to be written to the file for WRITE_INSERT operation
  private final long monitorDuration; // Duration for monitoring file updates for MONITOR operation (in milliseconds)

  /**
   * Constructor for READ operation.
   *
   * @param requestId           The unique ID for the request.
   * @param operationType       The type of operation (READ).
   * @param filePath            The path of the file on the server.
   * @param bytesToReadOrDelete The number of bytes to read from the file.
   * @param offset              The offset from where to start reading the data (in bytes).
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      long bytesToReadOrDelete,
      long offset) {
    this(requestId, operationType, filePath, bytesToReadOrDelete, offset, null,
        0);  // data and monitorDuration are not applicable
  }

  /**
   * Constructor for WRITE_INSERT operation.
   *
   * @param requestId     The unique ID for the request.
   * @param operationType The type of operation (WRITE_INSERT).
   * @param filePath      The path of the file on the server.
   * @param offset        The offset from where to start inserting the data (in bytes).
   * @param data          The data to be written to the file.
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      long offset, byte[] data) {
    this(requestId, operationType, filePath, 0, offset, data,
        0);  // bytesToReadOrDelete and monitorDuration are not applicable
  }

  /**
   * Constructor for the MONITOR operation.
   *
   * @param requestId       The unique ID for the request.
   * @param operationType   The type of operation (MONITOR).
   * @param filePath        The path of the file on the server to be monitored.
   * @param monitorDuration The duration for which file updates should be monitored (in
   *                        milliseconds).
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      long monitorDuration) {
    this(requestId, operationType, filePath, 0, 0, null,
        monitorDuration); // bytesToReadOrDelete, offset and data are not applicable
  }

  /**
   * Constructor for WRITE_DELETE operation (non-idempotent).
   *
   * @param requestId           The unique ID for the request.
   * @param operationType       The type of operation (WRITE_DELETE).
   * @param filePath            The path of the file on the server.
   * @param bytesToReadOrDelete The number of bytes to delete from file.
   * @param offset              The offset from where to start deleting the data (in bytes).
   * @param isDelete            A flag to distinguish between READ and WRITE_DELETE operations'
   *                            constructor calls due to constructor overloading.
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath,
      long bytesToReadOrDelete, long offset, boolean isDelete) {
    this(requestId, operationType, filePath, bytesToReadOrDelete, offset, null,
        0); // data and monitorDuration are not applicable
  }

  /**
   * Constructor for FILE_INFO operations (idempotent).
   *
   * @param requestId     The unique ID for the request.
   * @param operationType The type of operation (FILE_INFO).
   * @param filePath      The path of the file on the server.
   */
  public Request(long requestId, Constants.OperationType operationType, String filePath) {
    this(requestId, operationType, filePath, 0, 0, null, 0);
  }

  /**
   * Private constructor to initialize the Request object with all fields. This constructor is
   * called by the public constructors to set the values of the fields.
   *
   * @param requestId           The unique ID for the request.
   * @param operationType       The type of operation.
   * @param filePath            The path of the file on the server.
   * @param bytesToReadOrDelete The number of bytes to read/delete for READ/WRITE_DELETE
   *                            operations.
   * @param offset              The offset from where to start reading/writing/deleting the data (in
   *                            bytes) for READ/WRITE_INSERT/WRITE_DELETE operations.
   * @param data                The data to be written to the file for WRITE_INSERT operation.
   * @param monitorDuration     The duration for monitoring file updates for MONITOR operation (in
   *                            milliseconds).
   */
  private Request(long requestId, Constants.OperationType operationType, String filePath,
      long bytesToReadOrDelete,
      long offset, byte[] data,
      long monitorDuration) {
    this.requestId = requestId;
    this.operationType = operationType;
    this.filePath = filePath;
    this.bytesToReadOrDelete = bytesToReadOrDelete;
    this.offset = offset;
    this.data = data;
    this.monitorDuration = monitorDuration;
  }

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

  public long getBytesToReadOrDelete() {
    return bytesToReadOrDelete;
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

  /**
   * Returns a string representation of the Request object. For logging/debugging purposes.
   */
  @Override
  public String toString() {
    return "Request{" +
        "requestId=" + requestId +
        ", operationType=" + operationType +
        ", filePath='" + filePath + '\'' +
        ", bytesToReadOrDelete=" + bytesToReadOrDelete +
        ", offset=" + offset +
        ", data=" + (data == null ? "null" : new String(data)) +
        ", monitorDuration=" + monitorDuration +
        '}';
  }
}
