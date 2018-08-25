package com.liviurau.popularmovies.view;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.liviurau.popularmovies.R;
import com.liviurau.popularmovies.data.MovieDaoImpl;
import com.liviurau.popularmovies.data.MovieDbHelper;
import com.liviurau.popularmovies.loader.MovieListTaskLoader;
import com.liviurau.popularmovies.model.Movie;
import com.liviurau.popularmovies.utils.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liviu Rau on 18-Feb-18.
 */

public class MainActivity extends AppCompatActivity {

    private static final String LIFECYCLE_MOVIE_TYPE_KEY = "movieTypeTask";
    private static final String LIFECYCLE_MOVIE_TASK_KEY = "movieListTask";
    private static final String LIST_TASK_PREFERENCE = "TASK";
    private static final String LIST_TYPE_PREFERENCE = "TYPE";

    private static final int POPULAR_MOVIE_TASK = 45;
    private static final int UPCOMING_MOVIE_TASK = 56;
    private static final int TOP_RATED_MOVIE_TASK = 67;
    private static final int NOW_PLAYING_MOVIE_TASK = 78;
    private static final int FAVORITE_MOVIE_TASK = 0;

    private boolean connected = false;

    @BindView(R.id.movie_list_rv)
    RecyclerView movieListRV;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private MoviesAdapter moviesAdapter;
    private LoaderManager mLoaderManager;

    private String movieListType;
    private int movieListTask;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        ButterKnife.bind(this);

        setTitle("");
        mLoaderManager = getLoaderManager();

        movieListType = NetworkUtils.POPULAR;

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        moviesAdapter = new MoviesAdapter(getApplicationContext());

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        movieListRV.setLayoutManager(mStaggeredLayoutManager);
        movieListRV.setAdapter(moviesAdapter);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        movieListTask = sharedPref.getInt(LIST_TASK_PREFERENCE, movieListTask);
        movieListType = sharedPref.getString(LIST_TYPE_PREFERENCE, movieListType);

        if (isInternetConnection()) {
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(LIFECYCLE_MOVIE_TASK_KEY)) {
                    movieListTask = savedInstanceState.getInt(LIFECYCLE_MOVIE_TASK_KEY);
                    movieListType = savedInstanceState.getString(LIFECYCLE_MOVIE_TYPE_KEY);
                }
            }
            saveListPreference(movieListType, movieListTask);

            showLoading();
            getSelectedMovies(movieListTask);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isInternetConnection() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
            connected = networkInfo != null &&
                    networkInfo.isAvailable() &&
                    networkInfo.isConnected();

            return connected;

        } catch (Exception e) {
            Log.v("NetworkConnection", e.toString());
        }
        return connected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_rated:
                getSelectedMovies(TOP_RATED_MOVIE_TASK);
                break;
            case R.id.upcoming:
                getSelectedMovies(UPCOMING_MOVIE_TASK);
                break;
            case R.id.now_playing:
                getSelectedMovies(NOW_PLAYING_MOVIE_TASK);
                break;
            case R.id.favorites:
                getSelectedMovies(FAVORITE_MOVIE_TASK);
                break;
            default:
                getSelectedMovies(POPULAR_MOVIE_TASK);
                break;
        }
        return true;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        movieListTask = savedInstanceState.getInt(LIFECYCLE_MOVIE_TASK_KEY);
        movieListType = savedInstanceState.getString(LIFECYCLE_MOVIE_TYPE_KEY);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        movieListTask = sharedPref.getInt(LIST_TASK_PREFERENCE, movieListTask);
        movieListType = sharedPref.getString(LIST_TYPE_PREFERENCE, movieListType);

        getSelectedMovies(movieListTask);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        movieListTask = sharedPref.getInt(LIST_TASK_PREFERENCE, movieListTask);
        movieListType = sharedPref.getString(LIST_TYPE_PREFERENCE, movieListType);

        outState.putInt(LIFECYCLE_MOVIE_TASK_KEY, movieListTask);
        outState.putString(LIFECYCLE_MOVIE_TYPE_KEY, movieListType);
        saveListPreference(movieListType, movieListTask);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        movieListTask = sharedPref.getInt(LIST_TASK_PREFERENCE, movieListTask);
        movieListType = sharedPref.getString(LIST_TYPE_PREFERENCE, movieListType);
        getSelectedMovies(movieListTask);
    }

    private void saveListPreference(String movieListType, int movieListTask) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LIST_TYPE_PREFERENCE, movieListType);
        editor.putInt(LIST_TASK_PREFERENCE, movieListTask);
        editor.apply();
    }

    private void getSelectedMovies(int movieListTask) {
        switch (movieListTask) {
            case POPULAR_MOVIE_TASK:
                getMovies(NetworkUtils.POPULAR, POPULAR_MOVIE_TASK);
                break;
            case UPCOMING_MOVIE_TASK:
                getMovies(NetworkUtils.UPCOMING, UPCOMING_MOVIE_TASK);
                break;
            case TOP_RATED_MOVIE_TASK:
                getMovies(NetworkUtils.TOP_RATED, TOP_RATED_MOVIE_TASK);
                break;
            case NOW_PLAYING_MOVIE_TASK:
                getMovies(NetworkUtils.NOW_PLAYING, NOW_PLAYING_MOVIE_TASK);
                break;
            case FAVORITE_MOVIE_TASK:
                getMovies(NetworkUtils.FAVORITES, FAVORITE_MOVIE_TASK);
                break;
            default:
                break;
        }
    }

    private void getMovies(String type, int task) {
        movieListType = type;
        movieListTask = task;
        showLoading();
        if (task != FAVORITE_MOVIE_TASK) {
            startMovieTask(movieListTask);
            saveListPreference(movieListType, movieListTask);
        } else {
            MovieDbHelper dbHelper = new MovieDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            List<Movie> movies = new MovieDaoImpl(db).getFavoriteMovies();
            if (movies.size() > 0) {
                showLoading();
                moviesAdapter.swapList(movies);
                showDataView();
            } else {
                Snackbar snackbar = Snackbar.make(movieListRV, R.string.empty_favorites_message, Snackbar.LENGTH_LONG);

                View sbView = snackbar.getView();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sbView.setBackgroundColor(getColor(R.color.colorAccent));
                }
                snackbar.show();
            }
            saveListPreference(movieListType, movieListTask);
        }
    }

    private void showDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        movieListRV.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        movieListRV.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void startMovieTask(int taskId) {
        if (isInternetConnection()) {
            loadMovies(taskId);
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void loadMovies(int taskId) {
        LoaderManager.LoaderCallbacks<List<Movie>> callback = new LoaderManager.LoaderCallbacks<List<Movie>>() {

            @Override
            public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
                return new MovieListTaskLoader(context, movieListType);
            }

            @Override
            public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
                moviesAdapter.swapList(movies);
                showDataView();
            }

            @Override
            public void onLoaderReset(Loader<List<Movie>> loader) {

            }
        };
        mLoaderManager.initLoader(taskId, null, callback);
    }
}
