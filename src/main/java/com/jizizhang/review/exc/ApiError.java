package com.jizizhang.review.exc;

/**
 * Created by CalvinZhang on 2017-04-13.
 */
public class ApiError extends RuntimeException{
  private final int status;

  public ApiError(int status, String message){
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
