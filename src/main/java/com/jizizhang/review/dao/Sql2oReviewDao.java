package com.jizizhang.review.dao;

import com.jizizhang.review.exc.DaoException;
import com.jizizhang.review.model.Course;
import com.jizizhang.review.model.Review;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

/**
 * Created by CalvinZhang on 2017-04-13.
 */
public class Sql2oReviewDao implements ReviewDao {

  private final Sql2o sql2o;

  public Sql2oReviewDao(Sql2o sql2o){
    this.sql2o = sql2o;
  }

  @Override
  public void add(Review review) throws DaoException {
    String sql = "INSERT INTO reviews(course_id, rating, comment) VALUES (:courseId, :rating, :comment)";
    try(Connection connection = sql2o.open()){
      int id = (int)connection.createQuery(sql).bind(review).executeUpdate().getKey();
      review.setId(id);
    }catch (Sql2oException ex){
      throw new DaoException(ex, "Sorry, we are unable to add this review.");
    }
  }

  @Override
  public List<Review> findAll() {
    try(Connection connection = sql2o.open()) {
      return connection.createQuery("SELECT * FROM reviews").executeAndFetch(Review.class);
    }

  }

  @Override
  public List<Review> findByCourseId(int courseId) {
    try (Connection connection = sql2o.open()){
      return connection.createQuery("SELECT * from reviews WHERE course_id = :courseId")
          .addColumnMapping("COURSE_ID", "courseId")
          .addParameter("courseId", courseId)
          .executeAndFetch(Review.class);
    }
  }


}
