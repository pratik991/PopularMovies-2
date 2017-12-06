package com.example.pratik.popular_movies_stage_2.Fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.pratik.popular_movies_stage_2.R;
import com.example.pratik.popular_movies_stage_2.Adapter.RecyclerViewAdapter;
import com.example.pratik.popular_movies_stage_2.Data.MovieContract;
import com.example.pratik.popular_movies_stage_2.Model.MovieDetails;
import com.example.pratik.popular_movies_stage_2.Model.MovieListPresenter;
import com.example.pratik.popular_movies_stage_2.Model.MovieListInteractor;
import java.util.ArrayList;

/**
 * Created by Pratik on 11/8/17.
 */

public class MoviesPopularFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MoviesPopularFragment.class.getSimpleName();
    private String mlistType;
    private MovieListInteractor mMovieList;
    private ArrayList<MovieDetails.MovieDetail> mMovies;
    private RecyclerViewAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Parcelable stateParcelable;

    private final int LIST_LOADER = 0;
    public static final String LAYOUT_MANAGER_KEY = "layoutmanager";
    private final String KEY_RECYCLER_STATE = "movieRecycler";
    private final String KEY_MOVIE_ARRAY = "movieList";
    private final String KEY_LIST_TYPE = "movieListType";
    private final String TAG = "RecyclerViewFragment";

    private static final String[] LIST_COLUMNS =
            {
                    MovieContract.MovieEntry._ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                    MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                    MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE
            };

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter = new RecyclerViewAdapter(mMovies, new RecyclerViewAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(MovieDetails.MovieDetail movie) {
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });
        mRecyclerView.setAdapter(mMovieAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " ASC";
        Uri movieList = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieList,
                LIST_COLUMNS,
                MovieContract.MovieEntry.COLUMN_MOVIE_LIST_TYPE,
                new String[]{mlistType},
                sortOrder);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface Callback
    {
        void onItemSelected(MovieDetails.MovieDetail movie);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.d("Start", "Starting the Fragment: " + mMovies.size());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mType = sharedPrefs.getString(getString(R.string.pref_list_key),getString(R.string.pref_list_default));

        if (!mlistType.equals(mType))
        {
            mlistType = mType;
            mMovieList = new MovieListPresenter(this, mlistType);
            mMovies = new ArrayList<>();
            getLoaderManager().initLoader(LIST_LOADER, null, this);
        }

        if (mMovies.size() == 0) {
            mMovieList.loadMovieList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("SaveInstanceState", "Saving the instance state");
        stateParcelable = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, stateParcelable);
        outState.putParcelableArrayList(KEY_MOVIE_ARRAY, mMovies);
        outState.putString(KEY_LIST_TYPE, mlistType);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.d("ViewStateRestored", "Restoring the view");
        if (savedInstanceState != null)
        {
            stateParcelable = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
            ArrayList<MovieDetails.MovieDetail> parcelableArrayList = savedInstanceState.getParcelableArrayList(KEY_MOVIE_ARRAY);
            mMovies.addAll(parcelableArrayList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mRecyclerView.getLayoutManager().onRestoreInstanceState(stateParcelable);
        Log.d("RESUME", "calling on resume:" + mMovies.size());
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!mlistType.equals("favorites"))
            mMovieList.unSubscribeMovieList();

        Log.d("PAUSE", "calling pause");
    }


    public void addMovies(MovieDetails movies)
    {
        mMovies.addAll(movies.getResults());
        mMovieAdapter.notifyItemInserted(mMovies.size());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mlistType = sharedPrefs.getString(getString(R.string.pref_list_key),getString(R.string.pref_list_default));

        if (savedInstanceState != null)
        {
            mlistType = savedInstanceState.getString(KEY_LIST_TYPE, mlistType);
        }

        mMovieList = new MovieListPresenter(this, mlistType);

        mMovies = new ArrayList<>();
        getLoaderManager().initLoader(LIST_LOADER, null, this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        Log.d("CreateView", "creating the view.");
        View rootView = inflater.inflate(R.layout.movie_main, container, false);
        rootView.setTag(TAG);

        if (rootView.findViewById(R.id.recyclerViewDualPayne) != null) {
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDualPayne);
            mLinearLayoutManager = new GridLayoutManager(getActivity(), 1);
        } else {
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            mLinearLayoutManager = new GridLayoutManager(getActivity(), 3);
        }


        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMovieAdapter = new RecyclerViewAdapter(mMovies, new RecyclerViewAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(MovieDetails.MovieDetail movie) {
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });
        mRecyclerView.setAdapter(mMovieAdapter);
        return rootView;
    }


}
