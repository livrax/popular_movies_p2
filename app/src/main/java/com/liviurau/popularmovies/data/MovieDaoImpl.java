package com.liviurau.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.liviurau.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liviu Rau on 06-Mar-18.
 */

public class MovieDaoImpl implements MovieDao {

    private final SQLiteDatabase db;

    public MovieDaoImpl(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public boolean addFavoriteMovie(Movie movie) {
        if (db == null) {
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_ID, movie.getMovieId());
        cv.put(MovieContract.MovieEntry.COLUMN_NAME, movie.getTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_TAGLINE, movie.getTagline());
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getImage());
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getCover());
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
        cv.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        cv.put(MovieContract.MovieEntry.COLUMN_BUDGET, movie.getBudget());
        cv.put(MovieContract.MovieEntry.COLUMN_REVENUE, movie.getRevenue());

        try {
            db.beginTransaction();
            db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean removeFavoriteMovie(String movieId) {
        if (db == null) {
            return false;
        }

        try {
            db.beginTransaction();
            db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_ID + " = ?", new String[]{movieId});
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public Movie getFavoriteMovie(String movieId) throws SQLException {
        if (db == null) {
            return null;
        }

        Movie movie = new Movie();

        try {
            String selectQuery = "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_ID + "=?";
            db.beginTransaction();

            Cursor cursor = db.rawQuery(selectQuery, new String[]{movieId});

            if (cursor.moveToFirst()) {
                do {
                    movie = getMovieCursor(cursor);
                } while (cursor.moveToNext());
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return movie;
    }

    @Override
    public List<Movie> getFavoriteMovies() throws SQLException {
        if (db == null) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        try {
            String selectQuery = "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME;
            db.beginTransaction();

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    movies.add(getMovieCursor(cursor));
                } while (cursor.moveToNext());
            }

            cursor.close();

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return movies;
    }

    private Movie getMovieCursor(Cursor cursor) {
        Movie movie = new Movie();
        movie.setMovieId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
        movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME)));
        movie.setTagline(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TAGLINE)));
        movie.setImage(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
        movie.setCover(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP)));
        movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
        movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE)));
        movie.setRuntime(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME)));
        movie.setBudget(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BUDGET)));
        movie.setRevenue(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVENUE)));

        return movie;
    }
}
