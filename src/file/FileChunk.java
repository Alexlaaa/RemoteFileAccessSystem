package file;

/**
 * Represents a chunk of a file. This class holds a part of the file's data along with a sequence number
 * to identify the order of this chunk in relation to the whole file.
 */
public class FileChunk {
  private int sequenceNumber; // The sequence number of this chunk in the file
  private byte[] data; // The data contained in this chunk

  /**
   * Constructs a new FileChunk with a sequence number and data.
   *
   * @param sequenceNumber The sequence number of this chunk, indicating its order in the file.
   * @param data The byte array containing the file data for this chunk.
   */
  public FileChunk(int sequenceNumber, byte[] data) {
    this.sequenceNumber = sequenceNumber;
    this.data = data;
  }

  /**
   * Gets the sequence number of this chunk.
   *
   * @return The sequence number indicating this chunk's order in the overall file.
   */
  public int getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * Sets the sequence number of this chunk.
   *
   * @param sequenceNumber The new sequence number for this chunk.
   */
  public void setSequenceNumber(int sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  /**
   * Gets the data of this chunk.
   *
   * @return A byte array containing the data of this chunk.
   */
  public byte[] getData() {
    return data;
  }

  /**
   * Sets the data of this chunk.
   *
   * @param data The byte array containing the new data for this chunk.
   */
  public void setData(byte[] data) {
    this.data = data;
  }
}
