package client;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientCache manages the cache of file contents read by the client. It stores file content and
 * associated metadata such as last validation time.
 */
public class ClientCache {

  /**
   * Represents a single entry in the client cache.
   */
  private static class FileCacheEntry {

    byte[] content;
    long lastValidationTime;
    long lastModifiedTimeAtServer;
    long offset;
    long bytesToRead;

    /**
     * Constructs a cache entry for the specified file content and metadata.
     *
     * @param content                  The file content.
     * @param lastValidationTime       The time the cache entry was last validated.
     * @param lastModifiedTimeAtServer The time the file was last modified at the server.
     * @param offset                   The offset from where to start reading or deleting the file
     *                                 content.
     * @param bytesToRead              The number of bytes to read from the file content.
     */
    FileCacheEntry(byte[] content, long lastValidationTime, long lastModifiedTimeAtServer,
        long offset, long bytesToRead) {
      this.content = content;
      this.lastValidationTime = lastValidationTime;
      this.lastModifiedTimeAtServer = lastModifiedTimeAtServer;
      this.offset = offset;
      this.bytesToRead = bytesToRead;
    }
  }

  // Map to store cache entries keyed by file path.
  private final ConcurrentHashMap<String, FileCacheEntry> cache = new ConcurrentHashMap<>();
  private final long freshnessInterval; // in milliseconds

  /**
   * Constructs a client cache with the given freshness interval.
   *
   * @param freshnessInterval The freshness interval in milliseconds.
   */
  public ClientCache(long freshnessInterval) {
    this.freshnessInterval = freshnessInterval;
  }

  /**
   * Caches the file content with the given metadata.
   *
   * @param filePath                 The file path of the cached content.
   * @param content                  The content to be cached.
   * @param lastModifiedTimeAtServer The last modification time of the file at the server.
   * @param offset                   The offset from where to start reading or deleting the file
   *                                 content.
   * @param bytesToRead              The number of bytes to read from the file content.
   */
  public void cacheFileContent(String filePath, byte[] content, long lastModifiedTimeAtServer,
      long offset, long bytesToRead) {
    cache.put(filePath,
        new FileCacheEntry(content, System.currentTimeMillis(), lastModifiedTimeAtServer, offset,
            bytesToRead));
  }

  /**
   * Retrieves the cached content if it's fresh and valid, even if the offset and bytesToRead values
   * are different, as long as they fall within the range of the cached content. If the offset and
   * bytesToRead falls within the valid range, we will return the requested range from the cached
   * content, instead of the entire original cache.
   *
   * @param filePath    The file path of the content to retrieve.
   * @param offset      The offset from where to start reading or deleting the file content.
   * @param bytesToRead The number of bytes to read from the file content.
   * @return The content if it's in the cache and fresh, otherwise null.
   */
  public byte[] getFileContentIfFresh(String filePath, long offset, long bytesToRead) {
    FileCacheEntry entry = cache.get(filePath);
    // Check if cached entry is within freshness interval
    if (entry != null
        && (System.currentTimeMillis() - entry.lastValidationTime) < freshnessInterval) {
      // Check if the requested range overlaps with any cached range
      if (offset >= entry.offset && (offset + bytesToRead) <= (entry.offset + entry.bytesToRead)) {
        // Calculate the start index and end index for the requested range within the cached content
        int startIndex = (int) (offset - entry.offset);
        int endIndex = (int) (startIndex + bytesToRead);
        // Extract the requested range from the cached content
        System.out.println("In ClientCache: Cache Hit! Retrieving cached content...");
        return Arrays.copyOfRange(entry.content, startIndex, endIndex);
      }
    }
    System.out.println("In ClientCache: Cache Miss!");
    return null; // Return null to indicate that content needs to be fetched from the server
  }

  /**
   * Invalidates the cached entry for the specified file path.
   *
   * @param filePath The file path whose cache entry should be invalidated.
   */
  public void invalidate(String filePath) {
    cache.remove(filePath);
  }

  /**
   * Updates the last validation time and the server's last modified time for the given file path.
   *
   * @param filePath                 The file path to update.
   * @param lastModifiedTimeAtServer The last modification time at the server.
   */
  public void updateValidationTime(String filePath, long lastModifiedTimeAtServer) {
    FileCacheEntry entry = cache.get(filePath);
    if (entry != null) {
      entry.lastValidationTime = System.currentTimeMillis();
      entry.lastModifiedTimeAtServer = lastModifiedTimeAtServer;
    }
  }

  /**
   * Retrieves the last modified time of the file at the server for the given file path. Returns -1
   * if the file is not cached or the offset and bytesToRead are not within the valid cached range.
   *
   * @param filePath    The file path to retrieve the last modified time for.
   * @param offset      The offset from where to start reading or deleting the file content.
   * @param bytesToRead The number of bytes to read from the file content.
   * @return The last modified time of the file at the server, or -1 if the file is not cached.
   */
  public long getLastModifiedTime(String filePath, long offset, long bytesToRead) {
    FileCacheEntry entry = cache.get(filePath);
    if (entry != null && offset >= entry.offset && (offset + bytesToRead) <= (entry.offset
        + entry.bytesToRead)) {
      return entry.lastModifiedTimeAtServer;
    }
    return -1;  // Indicates not cached or the range is not covered by the cache
  }
  
}
