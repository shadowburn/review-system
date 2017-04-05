package com.jizizhang.review.dao;

import com.jizizhang.review.exc.DaoException;
import com.jizizhang.review.model.Course;

import java.util.List;

/**
 * Created by CalvinZhang on 2017-04-05.
 */
public interface CourseDao {
  void add(Course course) throws DaoException;

  List<Course> findAll();
}
