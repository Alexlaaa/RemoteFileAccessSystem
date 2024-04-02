package common;

public class Constants {

  /**
   * Enum representing the types of operations that can be performed in the remote file access
   * system.
   */
  public enum OperationType {
    SHUTDOWN_SERVER("Shutdown Server"),
    READ("Read"),
    WRITE_INSERT("Write Insert"),
    MONITOR("Monitor"),
    WRITE_DELETE("Write Delete"),
    FILE_INFO("File Info");

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
   */
  public enum StatusCode {
    SUCCESS(0, "Success"),
    GENERAL_ERROR(1, "General Error"),
    INVALID_OPERATION(2, "Invalid Operation"),
    SHUTDOWN(3, "Server Shutdown"),
    READ_SUCCESS(100, "Read Success"),
    READ_ERROR(101, "Read Error"),
    READ_INCOMPLETE(102, "Read Incomplete"),
    WRITE_INSERT_SUCCESS(200, "Write Insert Success"),
    WRITE_INSERT_ERROR(201, "Write Insert Error"),
    MONITOR_SUCCESS(300, "Monitor Success"),
    MONITOR_ERROR(301, "Monitor Error"),
    WRITE_DELETE_SUCCESS(400, "Write Delete Success"),
    WRITE_DELETE_ERROR(401, "Write Delete Error"),
    FILE_INFO_SUCCESS(500, "File Info Success"),
    FILE_INFO_ERROR(501, "File Info Error");

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

  /**
   * Enum for identifying the network strategy type to be used in ClientNetwork.
   */
  public enum NetworkStrategyType {
    AT_LEAST_ONCE,
    AT_MOST_ONCE
  }
}
