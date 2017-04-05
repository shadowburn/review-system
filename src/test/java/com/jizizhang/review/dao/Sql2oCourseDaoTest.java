package com.jizizhang.review.dao;

import static org.junit.Assert.*;

import com.jizizhang.review.model.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 * Created by CalvinZhang on 2017-04-05.
 */
public class Sql2oCourseDaoTest {
  private Sql2oCourseDao dao;
  private Connection connexion;

  @Before
  public void setUp() throws Exception {
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    dao = new Sql2oCourseDao(sql2o);
    //Keep the connexion open throughout the test
    connexion = sql2o.open();

  }

  @After
  public void tearDown() throws Exception {
    connexion.close();

  }

  @Test
  public void addingCourseSetsId() throws Exception {
    Course course = new Course("Test", "http://test.com");
    int originalCourseId = course.getId();

    dao.add(course);

    assertNotEquals(originalCourseId, course.getId());

  }

  @Test
  public void addedCoursesAreReturnedFromFindAll() throws Exception {
    Course course = new Course("Test", "http://test.com");

    dao.add(course);

    assertEquals(1, dao.findAll().size());
  }

  @Test
  public void noCoursesReturnsEmptyList() throws Exception {
    assertEquals(0, dao.findAll().size());

  }
}