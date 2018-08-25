package com.liviurau.popularmovies.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liviurau.popularmovies.R;
import com.liviurau.popularmovies.loader.ReviewListTaskLoader;
import com.liviurau.popularmovies.model.Review;
import com.liviurau.popularmovies.view.ReviewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.liviurau.popularmovies.view.MovieActivity.SELECTED_MOVIE;

/**
 * Created by Liviu Rau on 07-Mar-18.
 */

public class ReviewFragment extends Fragment {

    private Context mContext;
    private String movieId;

    private static final int TASK_REVIEWS = 98;

    private ReviewAdapter reviewAdapter;
    private LoaderManager mLoaderManager;

    @BindView(R.id.review_linear_layout)
    LinearLayout reviewLayout;
    @BindView(R.id.review_recycler_view)
    RecyclerView reviewRV;

    public ReviewFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);

        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mContext = getContext();
        }

        movieId = getArguments().getString(SELECTED_MOVIE);

        mLoaderManager = getLoaderManager();

        reviewAdapter = new ReviewAdapter();

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        reviewRV.setLayoutManager(mStaggeredLayoutManager);
        reviewRV.setAdapter(reviewAdapter);

        loadReviews();

        return rootView;
    }

    private void loadReviews() {
        LoaderManager.LoaderCallbacks<List<Review>> callback = new LoaderManager.LoaderCallbacks<List<Review>>() {

            @Override
            public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {
                return new ReviewListTaskLoader(mContext, movieId);
            }

            @Override
            public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
                if (reviews != null && reviews.size() > 0) {
                    reviewAdapter.swapList(reviews);
                } else {
                    reviewLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Review>> loader) {

            }
        };
        mLoaderManager.initLoader(TASK_REVIEWS, null, callback);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
