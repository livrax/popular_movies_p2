package com.liviurau.popularmovies.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liviurau.popularmovies.R;
import com.liviurau.popularmovies.data.MovieDaoImpl;
import com.liviurau.popularmovies.data.MovieDbHelper;
import com.liviurau.popularmovies.fragment.OnTrailerKeyPass;
import com.liviurau.popularmovies.fragment.ReviewFragment;
import com.liviurau.popularmovies.fragment.TrailerFragment;
import com.liviurau.popularmovies.loader.MovieTaskLoader;
import com.liviurau.popularmovies.model.Movie;
import com.liviurau.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.liviurau.popularmovies.R.drawable.favorite_off;
import static com.liviurau.popularmovies.R.drawable.favorite_on;

/**
 * Created by Liviu Rau on 18-Feb-18.
 */

public class MovieActivity extends AppCompatActivity implements OnTrailerKeyPass {

    public static final String SELECTED_MOVIE = "selected_movie";
    private static final int DEFAULT_POSITION = -1;

    private static final int TASK_MOVIE = 54;

    private String movieId;
    private Context context;

    private Movie currentMovie;

    @BindView(R.id.movie_coordinator_layout)
    CoordinatorLayout movieCoordinatorLayout;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.mainTrailerBtn)
    ImageButton mainTrailerBtn;
    @BindView(R.id.cover_iv)
    ImageView coverIV;
    @BindView(R.id.poster_iv)
    ImageView posterIV;
    @BindView(R.id.movie_title_tv)
    TextView originalTitleTV;
    @BindView(R.id.tagline_tv)
    TextView taglineTV;
    @BindView(R.id.genres_tv)
    TextView genresTV;
    @BindView(R.id.overview_tv)
    TextView overviewTV;
    @BindView(R.id.production_tv)
    TextView productionTV;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTV;
    @BindView(R.id.budget_tv)
    TextView budgetTV;
    @BindView(R.id.revenue_tv)
    TextView revenueTV;
    @BindView(R.id.runtime_tv)
    TextView runtimeTV;
    @BindView(R.id.favoriteFAB)
    FloatingActionButton favoriteFAB;

    private LoaderManager mLoaderManager;
    private String mainTrailerKey;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        context = getApplicationContext();

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mLoaderManager = getLoaderManager();

        Movie movie = getIntent().getParcelableExtra(SELECTED_MOVIE);
        if (movie.getListPosition() == DEFAULT_POSITION) {
            closeOnError();
            return;
        } else {
            movieId = movie.getMovieId();
            loadMovie();

        }

        if (mLoaderManager.getLoader(1) != null) {
            loadMovie();
        }

        pushFragment(new TrailerFragment(), R.id.trailerFragment);
        pushFragment(new ReviewFragment(), R.id.reviewFragment);

    }

    protected void pushFragment(Fragment fragment, int attribute) {
        if (fragment == null)
            return;

        Bundle bundle = new Bundle();
        bundle.putString(SELECTED_MOVIE, movieId);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                fragmentTransaction.replace(attribute, fragment);
                fragmentTransaction.commit();
            }
        }
    }

    private void loadContent(){
        coverIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTrailer();
            }
        });

        mainTrailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTrailer();
            }
        });

        favoriteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavorite) {
                    isFavorite = false;
                    if (removeFavoriteMovie(currentMovie)) {
                        favoriteFAB.setImageDrawable(getResources().getDrawable(favorite_off));
                        Snackbar snackbar = popNotification(getString(R.string.remove_favorite_message));
                        snackbar.show();
                    }
                } else {
                    if (addFavoriteMovie(currentMovie)) {
                        isFavorite = true;
                        favoriteFAB.setImageDrawable(getResources().getDrawable(favorite_on));
                        Snackbar snackbar = popNotification(getString(R.string.add_favorite_message));
                        snackbar.show();
                    } else {
                        Snackbar snackbar = popNotification(getString(R.string.warn_add_favorite_message));
                        snackbar.show();
                    }
                }
            }
        });
    }

    private void openTrailer(){
        if (mainTrailerKey != null) {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mainTrailerKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + mainTrailerKey));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        }
    }

    private Snackbar popNotification(String message) {
        Snackbar snackbar = Snackbar.make(movieCoordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sbView.setBackgroundColor(getColor(R.color.colorAccent));
        }

        return snackbar;
    }

    private boolean addFavoriteMovie(Movie movie) {
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        return new MovieDaoImpl(mDb).addFavoriteMovie(movie);
    }

    private boolean removeFavoriteMovie(Movie movie) {
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        return new MovieDaoImpl(mDb).removeFavoriteMovie(movie.getMovieId());
    }

    private boolean isFavoriteMovie(Movie movie) {
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        Movie favoriteMovie = new MovieDaoImpl(mDb).getFavoriteMovie(movie.getMovieId());
        String isExistent = favoriteMovie.getMovieId();
        if (isExistent != null) {
            favoriteFAB.setImageDrawable(getResources().getDrawable(favorite_on));
            return true;
        }

        return false;

    }

    private void loadMovie() {
        LoaderManager.LoaderCallbacks<Movie> callback = new LoaderManager.LoaderCallbacks<Movie>() {
            @Override
            public Loader<Movie> onCreateLoader(int id, Bundle args) {
                return new MovieTaskLoader(context, movieId);
            }

            @Override
            public void onLoadFinished(Loader<Movie> loader, Movie movie) {
                if (movie == null) {
                    closeOnError();
                } else {
                    loadContent();
                    populateMovieSection(movie);
                }
            }

            @Override
            public void onLoaderReset(Loader<Movie> loader) {
            }
        };
        mLoaderManager.initLoader(TASK_MOVIE, null, callback);
    }

    private void populateMovieSection(Movie selectedMovie) {
        currentMovie = selectedMovie;

        isFavorite = isFavoriteMovie(currentMovie);

        setTitle(selectedMovie.getTitle());
        String cover = selectedMovie.getCover();
        String coverPath = NetworkUtils.POSTER_PATH + cover;
        if (mainTrailerKey != null) {
            String img_url = NetworkUtils.YOUTUBE_THUMBNAIL_PATH + mainTrailerKey + "/0.jpg";
            Picasso.with(getApplicationContext()).load(img_url).into(coverIV);
        } else {
            Picasso.with(getApplicationContext()).load(coverPath).into(coverIV);
        }

        String poster = selectedMovie.getImage();
        String posterPath = NetworkUtils.POSTER_PATH + poster;
        Picasso.with(getApplicationContext()).load(posterPath).into(posterIV);

        String title = selectedMovie.getTitle();
        originalTitleTV.setText(title);

        String tagline = selectedMovie.getTagline();
        if (!tagline.isEmpty()) {
            taglineTV.setText(tagline);
        } else {
            taglineTV.setVisibility(View.GONE);
        }

        Map<Integer, String> genres = selectedMovie.getGenres();
        StringBuilder genresSB = new StringBuilder();
        for (Map.Entry m : genres.entrySet()) {
            genresSB.append(m.getValue()).append("  ");
        }
        genresTV.setText(genresSB);

        String overview = selectedMovie.getOverview();
        overviewTV.setText(overview);

        Map<Integer, String> production = selectedMovie.getProductionCompanies();
        StringBuilder productionCompaniesSB = new StringBuilder();
        productionCompaniesSB.append("Production: \n");
        for (Map.Entry m : production.entrySet()) {
            productionCompaniesSB.append(m.getValue()).append("\n");
        }
        productionTV.setText(productionCompaniesSB);

        String releaseDate = selectedMovie.getReleaseDate();
        if (!releaseDate.isEmpty()) {
            releaseDateTV.setText(releaseDate.substring(0, 4));
        } else {
            releaseDateTV.setVisibility(View.GONE);
        }

        String budget = selectedMovie.getBudget();
        if (!budget.isEmpty()) {
            budgetTV.setText(String.format("Budget\n$ %s", budget));
        } else {
            budgetTV.setVisibility(View.GONE);
        }

        String revenue = selectedMovie.getRevenue();
        if (!revenue.isEmpty()) {
            revenueTV.setText(String.format("Revenue\n$ %s", revenue));
        } else {
            revenueTV.setVisibility(View.GONE);
        }

        String runtime = selectedMovie.getRuntime();
        if (!runtime.isEmpty()) {
            runtimeTV.setText(String.format("%s min", runtime));
        } else {
            runtimeTV.setVisibility(View.GONE);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.movie_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onKeyPass(String key) {
        mainTrailerKey = key;
    }
}
