package server;

import common.Constants.StatusCode;
import common.Request;
import common.Response;

public class ServerService {

  public Response processRequest(Request request) {
    return new Response(StatusCode.READ_SUCCESS, new byte[0], "hi"); // TODO: Implement this
  }
}
