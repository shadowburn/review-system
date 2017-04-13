package com.jizizhang.testing;

/**
 * Created by CalvinZhang on 2017-04-12.
 */
public class ApiResponse {
  private final int status;
  private final String body;

  public ApiResponse(int status, String body) {
    this.status = status;
    this.body = body;
  }

  public int getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }
}

