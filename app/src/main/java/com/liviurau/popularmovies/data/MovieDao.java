package com.liviurau.popularmovies.data;

import com.liviurau.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by Liviu Rau on 06-Mar-18.
 */

interface MovieDao {

    boolean addFavoriteMovie(Movie movie);

    boolean removeFavoriteMovie(String movieId);

    Movie getFavoriteMovie(String movieId);

    List<Movie> getFavoriteMovies();

}
