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
import com.liviurau.popularmovies.loader.TrailerListTaskLoader;
import com.liviurau.popularmovies.model.Trailer;
import com.liviurau.popularmovies.view.TrailerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.liviurau.popularmovies.view.MovieActivity.SELECTED_MOVIE;

/**
 * Created by Liviu Rau on 07-Mar-18.
 */

public class TrailerFragment extends Fragment {

    private Context mContext;
    private String movieId;
    private OnTrailerKeyPass dataPasser;

    private static final int TASK_TRAILERS = 76;

    private TrailerAdapter trailerAdapter;
    private LoaderManager mLoaderManager;

    @BindView(R.id.trailer_linear_layout)
    LinearLayout trailerLayout;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView trailerRV;

    public TrailerFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnTrailerKeyPass) context;
    }

    private void passKey(String key) {
        dataPasser.onKeyPass(key);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_trailer, container, false);

        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mContext = getContext();
        }

        movieId = getArguments().getString(SELECTED_MOVIE);

        mLoaderManager = getLoaderManager();

        trailerAdapter = new TrailerAdapter(mContext);

        StaggeredGridLayoutManager mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        trailerRV.setLayoutManager(mStaggeredLayoutManager);
        trailerRV.setAdapter(trailerAdapter);

        loadTrailers();

        return rootView;
    }

    private void loadTrailers() {
        LoaderManager.LoaderCallbacks<List<Trailer>> callback = new LoaderManager.LoaderCallbacks<List<Trailer>>() {

            @Override
            public Loader<List<Trailer>> onCreateLoader(int i, Bundle bundle) {
                return new TrailerListTaskLoader(mContext, movieId);
            }

            @Override
            public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> trailers) {
                trailerAdapter.swapList(trailers);
                if (trailers != null && trailers.size() > 0) {
                    passKey(trailers.get(0).getKey());
                } else {
                    trailerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Trailer>> loader) {

            }
        };
        mLoaderManager.initLoader(TASK_TRAILERS, null, callback);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
