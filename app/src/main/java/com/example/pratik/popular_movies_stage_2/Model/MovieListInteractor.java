package com.example.pratik.popular_movies_stage_2.Model;

/**
 * Created by Pratik on 11/8/17.
 */

public interface MovieListInteractor {
    void loadMovieList();
    void unSubscribeMovieList();
    void setListType(String listType);
}
