package com.jizizhang.review;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import com.google.gson.Gson;

import com.jizizhang.review.dao.CourseDao;
import com.jizizhang.review.dao.ReviewDao;
import com.jizizhang.review.dao.Sql2oCourseDao;
import com.jizizhang.review.dao.Sql2oReviewDao;
import com.jizizhang.review.exc.ApiError;
import com.jizizhang.review.exc.DaoException;
import com.jizizhang.review.model.Course;
import com.jizizhang.review.model.Review;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CalvinZhang on 2017-04-06.
 */
public class Api {
  public static void main(String[] args) {
    String datasource = "jdbc:h2:~/reviews.db";
    if(args.length > 0){
      if(args.length != 2){
        System.out.println("java Api <port> <datasource>");
        System.exit(0);
      }
      port(Integer.parseInt(args[0]));
      datasource = args[1];
    }

    Sql2o sql2o = new Sql2o(
        String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", datasource), "", "");
    CourseDao courseDao = new Sql2oCourseDao(sql2o);
    ReviewDao reviewDao = new Sql2oReviewDao(sql2o);
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
      if(course==null){
        throw new ApiError(404, "Could not find course with this ID: "+ id);
      }
      return course;
    },gson::toJson);

    post("/courses/:courseId/reviews", "application/json", (req, res)->{
      int courseId = Integer.parseInt(req.params("courseId"));
      Review review = gson.fromJson(req.body(), Review.class);
      review.setCourseId(courseId);
      try{
        reviewDao.add(review);
      }catch (DaoException exc){
        throw new ApiError(500, exc.getMessage());
      }
      res.status(201);
      return review;
    },gson::toJson);

    get("/courses/:courseId/reviews", "application/json", (req, res)->{
      int courseId = Integer.parseInt(req.params("courseId"));
      return reviewDao.findByCourseId(courseId);
    },gson::toJson);

    exception(ApiError.class, (exc, req, res)->{
      ApiError error = (ApiError) exc;
      Map<String, Object> jsonMap = new HashMap<>();
      jsonMap.put("status", error.getStatus());
      jsonMap.put("errorMessage", error.getMessage());
      res.type("application/json");
      res.status(error.getStatus());
      res.body(gson.toJson(jsonMap));
    });

    after((req, res)->{
      res.type("application/json");
    });
  }
}
