package com.jizizhang.review.dao;

import com.jizizhang.review.exc.DaoException;
import com.jizizhang.review.model.Review;

import java.util.List;

/**
 * Created by CalvinZhang on 2017-04-05.
 */
public interface ReviewDao {

  void add(Review review) throws DaoException;

  List<Review> findAll();

  List<Review> findByCourseId(int courseId);
}
