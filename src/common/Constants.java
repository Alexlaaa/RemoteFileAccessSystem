package common;
public class Constants {

  /**
   * Enum representing the types of operations that can be performed in the remote file access system.
   */
  public enum OperationType {
    SHUTDOWN_SERVER("Shutdown Server"),
    READ("Read"),
    WRITE("Write"),
    MONITOR("Monitor"),
    CUSTOM_OPERATION_1("CustomOperation1"), // TODO: Modify for custom operation 1
    CUSTOM_OPERATION_2("CustomOperation2"); // TODO: Modify for custom operation 2

    private final String description;

    OperationType(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /**
   * Enum representing various status codes used in the response to denote operation outcomes.
   * TODO: Add status codes for the 2 custom operations.
   */
  public enum StatusCode {
    SUCCESS(0, "Success"),
    GENERAL_ERROR(1, "General Error"),
    INVALID_OPERATION(2, "Invalid Operation"),
    SHUTDOWN(3, "Server Shutdown"),
    READ_SUCCESS(100, "Read Success"),
    READ_ERROR(101, "Read Error"),
    READ_INCOMPLETE(102, "Read Incomplete"),
    WRITE_SUCCESS(200, "Write Success"),
    WRITE_ERROR(201, "Write Error"),
    WRITE_INCOMPLETE(202, "Write Incomplete"),

    MONITOR_SUCCESS(300, "Monitor Success"),
    MONITOR_ERROR(301, "Monitor Error");

    private final int code;
    private final String description;

    StatusCode(int code, String description) {
      this.code = code;
      this.description = description;
    }

    public int getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }
  }
}
