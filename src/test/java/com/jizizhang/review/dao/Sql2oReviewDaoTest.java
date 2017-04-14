package com.jizizhang.review.dao;

import static org.junit.Assert.*;

import com.jizizhang.review.exc.DaoException;
import com.jizizhang.review.model.Course;
import com.jizizhang.review.model.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * Created by CalvinZhang on 2017-04-14.
 */
public class Sql2oReviewDaoTest {
  private Connection connection;
  private Sql2oCourseDao courseDao;
  private Sql2oReviewDao reviewDao;
  private Course course;

  @Before
  public void setUp() throws Exception {
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    connection = sql2o.open();
    courseDao = new Sql2oCourseDao(sql2o);
    reviewDao = new Sql2oReviewDao(sql2o);
    course = new Course("Test", "http://test.com");
    courseDao.add(course);
  }

  @After
  public void tearDown() throws Exception {
    connection.close();
  }

  @Test
  public void addingReviewSetsNewId() throws Exception {
    Review review = new Review(course.getId(), 5, "Comment");
    int originalId = review.getId();

    reviewDao.add(review);

    assertNotEquals(originalId, review.getId());
  }

  @Test
  public void multipleReviewsAreFoundWhenTheyExistForACourse() throws Exception {
    reviewDao.add(new Review(course.getId(), 5, "Test comment 1"));
    reviewDao.add(new Review(course.getId(), 4, "Test comment 2"));

    List<Review> reviews = reviewDao.findByCourseId(course.getId());

    assertEquals(2, reviews.size());
  }

  @Test(expected = DaoException.class)
  public void addingAReviewToANonExistingCourseFails() throws Exception {
    Review review = new Review(40, 5, "Testing");

    reviewDao.add(review);
  }
}