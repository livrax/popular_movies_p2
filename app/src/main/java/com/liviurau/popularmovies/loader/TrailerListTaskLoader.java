package com.liviurau.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.liviurau.popularmovies.model.Trailer;
import com.liviurau.popularmovies.utils.JsonUtils;
import com.liviurau.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Liviu Rau on 21-Feb-18.
 */

public class TrailerListTaskLoader extends AsyncTaskLoader<List<Trailer>> {

    private final String param;

    public TrailerListTaskLoader(Context context, String param) {
        super(context);
        this.param = param;
    }

    @Override
    public List<Trailer> loadInBackground() {
        URL path = NetworkUtils.buildTrailerPath(param);
        try {
            String response = NetworkUtils.getResponse(path);

            if (response != null) {
                return JsonUtils.parseTrailers(response);
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
    public void deliverResult(List<Trailer> data) {
        super.deliverResult(data);
    }
}
