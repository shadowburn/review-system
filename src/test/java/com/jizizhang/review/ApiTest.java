package com.jizizhang.review;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;

import com.jizizhang.review.dao.Sql2oCourseDao;
import com.jizizhang.review.dao.Sql2oReviewDao;
import com.jizizhang.review.model.Course;
import com.jizizhang.review.model.Review;
import com.jizizhang.testing.ApiClient;
import com.jizizhang.testing.ApiResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;
import spark.Spark;

/**
 * Created by CalvinZhang on 2017-04-12.
 */
public class ApiTest {

  public static final String PORT = "4567";
  public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
  private Connection connecton;
  private ApiClient client;
  private Gson gson;
  private Sql2oCourseDao courseDao;
  private Sql2oReviewDao reviewDao;

  @BeforeClass
  public static void startServer(){
    String[] args = {PORT, TEST_DATASOURCE};
    Api.main(args);
  }

  @AfterClass
  public static void stopServer(){
    Spark.stop();
  }

  @Before
  public void setUp() throws Exception {
    Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
    courseDao = new Sql2oCourseDao(sql2o);
    reviewDao = new Sql2oReviewDao(sql2o);
    connecton = sql2o.open();
    client = new ApiClient("http://localhost:"+PORT);
    gson = new Gson();
  }

  @After
  public void tearDown() throws Exception {
    connecton.close();
  }

  @Test
  public void addingCoursesReturnsCreatedStatus() throws Exception {
    Map<String, String> values = new HashMap<>();
    values.put("name", "test");
    values.put("url", "http://test.com");

    ApiResponse res = client.request("POST", "/courses", gson.toJson(values));

    assertEquals(201, res.getStatus());
  }

  @Test
  public void coursesCanBeAccessedById() throws Exception {
    Course course = newTestCourse();
    courseDao.add(course);

    ApiResponse res = client.request("GET", "/courses/" + course.getId());
    Course retrieved = gson.fromJson(res.getBody(), Course.class);

    assertEquals(course, retrieved);
  }

  @Test
  public void missingCoursesReturnNotFoundStatus() throws Exception {
    ApiResponse res = client.request("GET", "/course/42");

    assertEquals(404, res.getStatus());
  }

  @Test
  public void addingReviewGivesCreatedStatus() throws Exception {
    Course course = newTestCourse();
    courseDao.add(course);
    Map<String, Object> values = new HashMap<>();
    values.put("rating", 5);
    values.put("comment", "test comment");

    ApiResponse res = client.request("POST",
        String.format("/courses/%d/reviews", course.getId()), gson.toJson(values));

    assertEquals(201, res.getStatus());
  }

  @Test
  public void addingReviewToUnknownCourseThrowsError() throws Exception {
    Map<String, Object> values = new HashMap<>();
    values.put("rating", 5);
    values.put("comment", "test comment");

    ApiResponse res = client.request("POST","/courses/100/reviews", gson.toJson(values));

    assertEquals(500, res.getStatus());
  }

  @Test
  public void multipleReviewsReturnedForCourse() throws Exception {
    Course course = newTestCourse();
    courseDao.add(course);
    reviewDao.add(new Review(course.getId(), 5, "Test comment 1"));
    reviewDao.add(new Review(course.getId(), 4, "Test comment 2"));

    ApiResponse res = client.request("GET",
        String.format("/courses/%d/reviews", course.getId()));
    Review[] reviews = gson.fromJson(res.getBody(), Review[].class);

    assertEquals(2, reviews.length);
  }

  private Course newTestCourse() {
    return new Course("Test", "http://test.com");
  }
}