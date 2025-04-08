/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.movielens.es.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author michael.enudi
 */
public class Rating extends Model {

    private final long userId;
    private final long movieId;
    private final float rating;
    private final Date timestamp;

    private Movie movie;

    public Rating(long userId, long movieId, float rating, long timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
//        this.timestamp = secondsTsToDateString(timestamp);
        this.timestamp = new Date(timestamp*1000);
    }

    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @return the movieId
     */
    public long getMovieId() {
        return movieId;
    }

    /**
     * @return the rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * @return the timestamp
     */
//    public String getTimestamp() {
//        return timestamp;
//    }

    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param movie
     */
    public void joinMovie(Movie movie) {
        this.movie = movie;
    }

    /**
     * @return the movie
     */
    public Movie getMovie() {
        return movie;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>(4);

        map.put("userId", this.getUserId());
        map.put("movieId", this.getMovieId());
        if (this.movie != null) {
            joinMovie(movie, map);
        }
        map.put("rating", this.getRating());
//        map.put("ts", this.getTimestamp());
        map.put("timestamp", this.getTimestamp());

        return map;
    }

}
