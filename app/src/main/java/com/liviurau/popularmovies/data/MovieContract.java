package com.liviurau.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by Liviu Rau on 05-Mar-18.
 */

class MovieContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_BUDGET = "budget";
        public static final String COLUMN_REVENUE = "revenue";
    }
}
