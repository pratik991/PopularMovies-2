package com.example.pratik.popular_movies_stage_2.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Pratik on 11/8/17.
 */

//https://code.tutsplus.com/tutorials/android-fundamentals-working-with-content-providers--mobile-5549

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDb;
    static final int MOVIES = 100;
    static final int MOVIE_ID = 101;

    @Override
    public boolean onCreate()
    {
        mMovieDb = new MovieDbHelper(getContext());
        return false;
    }

    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_ID);

        return matcher;
    }

    @Override
    public String getType(Uri uri)
    {
        final int matchUri = sUriMatcher.match(uri);

        switch (matchUri)
        {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        int matchedUri = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (matchedUri)
        {
            case MOVIES:
            {
                returnCursor = mMovieDb.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null);
                break;
            }
            case MOVIE_ID:
            {
                returnCursor = mMovieDb.getReadableDatabase()
                        .query(MovieContract.MovieEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                null);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = mMovieDb.getWritableDatabase();
        final int uriMatch = sUriMatcher.match(uri);

        Uri returnUri;

        switch (uriMatch)
        {
            case MOVIES:
            {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mMovieDb.getWritableDatabase();
        final int uriMatch = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (uriMatch)
        {
            case MOVIES:
            {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = mMovieDb.getWritableDatabase();
        final int uriMatch = sUriMatcher.match(uri);
        int deletedRow = 0;

        switch (uriMatch)
        {
            case MOVIES:
            {
                deletedRow = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection, selectionArgs);
                break;
            }
            case MOVIE_ID:
            {
                deletedRow = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        if (deletedRow != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return deletedRow;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        return -1;
    }

    @Override
    public void shutdown()
    {
        mMovieDb.close();
        super.shutdown();
    }
}
