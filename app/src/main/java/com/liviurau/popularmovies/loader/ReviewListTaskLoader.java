package com.liviurau.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.liviurau.popularmovies.model.Review;
import com.liviurau.popularmovies.utils.JsonUtils;
import com.liviurau.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Liviu Rau on 21-Feb-18.
 */

public class ReviewListTaskLoader extends AsyncTaskLoader<List<Review>> {

    private final String param;

    public ReviewListTaskLoader(Context context, String param) {
        super(context);
        this.param = param;
    }

    @Override
    public List<Review> loadInBackground() {
        URL path = NetworkUtils.buildReviewPath(param);
        try {
            String response = NetworkUtils.getResponse(path);

            if (response != null) {
                return JsonUtils.parseReviews(response);
            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public void deliverResult(List<Review> data) {
        super.deliverResult(data);
    }
}
