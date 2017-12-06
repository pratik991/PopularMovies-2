package com.example.pratik.popular_movies_stage_2.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pratik on 11/8/17.
 */

//https://stackoverflow.com/questions/9243361/what-is-a-contract-class-and-how-is-it-used

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.pratik.popular_movies_stage_2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_VIDEO = "videos";
    public static final String PATH_REVIEW = "reviews";

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" +
                PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" +
                PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";

        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_MOVIE_LIST_TYPE = "list_type";

        public static Uri buildMovieUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithVideosUri(long movieId)
        {

            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).appendPath(PATH_VIDEO).build();
        }

        public static String getMovieIdFromUri(Uri uri)
        {

            return uri.getPathSegments().get(1);
        }
    }
}
