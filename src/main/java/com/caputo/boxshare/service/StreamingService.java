package com.caputo.boxshare.service;

import com.caputo.boxshare.service.torrent.TorrentClient;
import com.caputo.boxshare.service.torrent.TorrentFileTailer;
import com.caputo.boxshare.service.torrent.TorrentFileTailerListener;
import com.caputo.boxshare.service.torrent.TorrentMetadata;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * A class that is responsible for downloading and streaming torrent media files. For more
 * information about the overridden interface method, see {@link TorrentFileTailer}.
 */
@Service
public class StreamingService implements TorrentFileTailerListener {
  private final TorrentMetadata torrentMetadata;
  private final TorrentClient torrentClient;
  private final TorrentFileTailer torrentFileTailer;
  private byte[] fileData = new byte[0];
  private String fileType;
  private int fileSize;

  public StreamingService(
      TorrentClient torrentClient,
      TorrentFileTailer torrentFileTailer,
      TorrentMetadata torrentMetadata) {
    this.torrentClient = torrentClient;
    this.torrentFileTailer = torrentFileTailer;
    this.torrentMetadata = torrentMetadata;
  }

  /**
   * Downloads a torrent file from a magnet URL, then returns the requested range of bytes.
   *
   * <p>This function follows the standard protocol for HTTP range requests, so it supports requests
   * for either the whole file or partial content. Requests that don't include a range will be
   * served the whole file by default, while range requests will receive the requested subset of
   * bytes with a 206 HTTP status code.
   *
   * @param magnetUrl the magnet URL from which the torrent should be downloaded.
   * @param range the HTTP range header of the incoming request.
   * @return the response entity with the requested bytes in its body.
   */
  public ResponseEntity<StreamingResponseBody> getStream(String magnetUrl, String range) {
    torrentClient.get(magnetUrl);
    torrentFileTailer.setListener(this);
    getMetadata();
    if (range == null) {
      return ResponseEntity.status(HttpStatus.OK)
          .header(HttpHeaders.CONTENT_TYPE, fileType)
          .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
          .body(readByteRange(0, fileSize));
    }
    String[] ranges = range.split("-");
    int rangeStart = Integer.parseInt(ranges[0].substring(6));
    int rangeEnd;
    if (ranges.length > 1) {
      rangeEnd = Integer.parseInt(ranges[1]);
    } else {
      rangeEnd = fileSize;
    }
    if (fileSize < rangeEnd) {
      rangeEnd = fileSize;
    }
    String contentLength = String.valueOf((rangeEnd - rangeStart));
    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
        .header(HttpHeaders.CONTENT_TYPE, fileType)
        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
        .header(HttpHeaders.CONTENT_TYPE, contentLength)
        .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize)
        .body(readByteRange(rangeStart, rangeEnd + 1));
  }

  /**
   * Loads the necessary metadata for the torrent that will be downloaded. Since the metadata is
   * only available after the {@link TorrentClient} successfully downloads the first piece, this
   * function intentionally blocks the current thread.
   */
  private void getMetadata() {
    while (!torrentMetadata.isReady()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    fileSize = torrentMetadata.getSize();
    fileType = torrentMetadata.getExtension().mimeType;
  }

  /**
   * Reads a range of bytes from the available data, then writes it to the response's output stream.
   * If the requested range includes some bytes that have not been downloaded yet, then this
   * function will block the thread until the {@link TorrentFileTailer} makes it available.
   *
   * @param start the start of the range.
   * @param end the end of the range.
   * @return a response body with the bytes in the range.
   */
  private StreamingResponseBody readByteRange(int start, int end) {
    while (end > fileData.length) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return outputStream -> {
      byte[] requestedData = Arrays.copyOfRange(fileData, start, end);
      outputStream.write(requestedData);
      outputStream.flush();
    };
  }

  /**
   * Stores the newly-found bytes by the tailer.
   *
   * @param bytes the new bytes received by the tailer.
   */
  @Override
  public void newBytes(byte[] bytes) {
    this.fileData = ArrayUtils.addAll(fileData, bytes);
  }
}
