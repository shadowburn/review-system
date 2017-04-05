package com.jizizhang.review.exc;

/**
 * Created by CalvinZhang on 2017-04-05.
 */
public class DaoException extends Exception {

  private final Exception originalException;

  public DaoException(Exception originalException, String msg){
    super(msg);
    this.originalException = originalException;
  }
}
