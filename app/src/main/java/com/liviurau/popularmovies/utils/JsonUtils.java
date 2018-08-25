package com.liviurau.popularmovies.utils;

import com.liviurau.popularmovies.model.Movie;
import com.liviurau.popularmovies.model.Review;
import com.liviurau.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liviu Rau on 17-Feb-18.
 */

public class JsonUtils {

    private static final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String TITLE = "title";
    private static final String RELEASE_DATE = "release_date";
    private static final String TAGLINE = "tagline";
    private static final String OVERVIEW = "overview";
    private static final String RUNTIME = "runtime";
    private static final String BUDGET = "budget";
    private static final String REVENUE = "revenue";
    private static final String POSTER_PATH = "poster_path";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String GENRES = "genres";
    private static final String PRODUCTION_COMPANIES = "production_companies";
    private static final String PRODUCTION_ID = "id";
    private static final String PRODUCTION_NAME = "name";

    private static final String TRAILER_ID = "id";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";
    private static final String TRAILER_SITE = "site";
    private static final String TRAILER_TYPE = "type";

    private static final String REVIEW_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";

    public static ArrayList<Movie> parseMovies(String param) {

        ArrayList<Movie> list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(param);
            JSONArray results = jsonObject.optJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject movieInfo = results.optJSONObject(i);
                Movie movie = new Movie();
                movie.setMovieId(movieInfo.optString(MOVIE_ID));
                movie.setTitle(movieInfo.optString(TITLE));
                movie.setReleaseDate(movieInfo.optString(RELEASE_DATE));
                movie.setImage(movieInfo.optString(POSTER_PATH));
                list.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Movie parseMovie(String param) {
        Movie movie = new Movie();

        try {
            JSONObject jsonObject = new JSONObject(param);

            movie.setMovieId(jsonObject.optString(MOVIE_ID));
            movie.setTitle(jsonObject.optString(TITLE));
            movie.setTagline(jsonObject.optString(TAGLINE));
            movie.setImage(jsonObject.optString(POSTER_PATH));
            movie.setCover(jsonObject.optString(BACKDROP_PATH));
            movie.setOverview(jsonObject.optString(OVERVIEW));
            movie.setReleaseDate(jsonObject.optString(RELEASE_DATE));
            movie.setRuntime(jsonObject.optString(RUNTIME));
            movie.setBudget(jsonObject.optString(BUDGET));
            movie.setRevenue(jsonObject.optString(REVENUE));

            JSONArray genres = jsonObject.optJSONArray(GENRES);
            movie.setGenres(jsonToMap(genres));

            JSONArray productionCompanies = jsonObject.optJSONArray(PRODUCTION_COMPANIES);
            movie.setProductionCompanies(jsonToMap(productionCompanies));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return movie;

    }

    private static Map<Integer, String> jsonToMap(JSONArray jsonArray) {
        Map<Integer, String> list = new LinkedHashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject genre = jsonArray.optJSONObject(i);
            list.put(genre.optInt(PRODUCTION_ID), genre.optString(PRODUCTION_NAME));
        }

        return list;
    }

    public static List<Trailer> parseTrailers(String param) {
        ArrayList<Trailer> list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(param);
            JSONArray results = jsonObject.optJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject trailerInfo = results.optJSONObject(i);
                Trailer trailer = new Trailer();
                trailer.setTrailerId(trailerInfo.optString(TRAILER_ID));
                trailer.setKey(trailerInfo.optString(TRAILER_KEY));
                trailer.setName(trailerInfo.optString(TRAILER_NAME));
                trailer.setSite(trailerInfo.optString(TRAILER_SITE));
                trailer.setType(trailerInfo.optString(TRAILER_TYPE));
                list.add(trailer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }

    public static List<Review> parseReviews(String param) {
        ArrayList<Review> list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(param);
            JSONArray results = jsonObject.optJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                JSONObject reviewInfo = results.optJSONObject(i);
                Review review = new Review();
                review.setReviewId(reviewInfo.optString(REVIEW_ID));
                review.setAuthor(reviewInfo.optString(REVIEW_AUTHOR));
                review.setContent(reviewInfo.optString(REVIEW_CONTENT));
                review.setUrl(reviewInfo.optString(REVIEW_URL));
                list.add(review);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }
}
