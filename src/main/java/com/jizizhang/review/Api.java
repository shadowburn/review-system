package com.jizizhang.review;

import static spark.Spark.after;
import static spark.Spark.post;
import static spark.Spark.get;

import com.google.gson.Gson;

import com.jizizhang.review.dao.CourseDao;
import com.jizizhang.review.dao.Sql2oCourseDao;
import com.jizizhang.review.model.Course;
import org.sql2o.Sql2o;

/**
 * Created by CalvinZhang on 2017-04-06.
 */
public class Api {
  public static void main(String[] args) {
    Sql2o sql2o = new Sql2o("jdbc:h2:~/reviews.db;INIT=RUNSCRIPT from 'classpath:db/init.sql'","", "");
    CourseDao courseDao = new Sql2oCourseDao(sql2o);
    Gson gson = new Gson();

    post("/courses", "application/json", (req, res) -> {
      Course course = gson.fromJson(req.body(), Course.class);
      courseDao.add(course);
      res.status(201);
      res.type("application/json");
      return course;
    }, gson::toJson);

    get("/courses", "application/json", (req, res)-> courseDao.findAll(), gson::toJson);

    get("/courses/:id", "application/json", (req, res)->{
      int id = Integer.parseInt(req.params("id"));
      Course course = courseDao.findById(id);
      return course;
    },gson::toJson);

    after((req, res)->{
      res.type("application/json");
    });
  }
}
