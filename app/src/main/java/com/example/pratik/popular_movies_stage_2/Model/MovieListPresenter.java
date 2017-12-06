package com.example.pratik.popular_movies_stage_2.Model;

import android.database.Cursor;
import android.net.Uri;
import com.example.pratik.popular_movies_stage_2.Data.MovieContract;
import com.example.pratik.popular_movies_stage_2.Fragment.MoviesPopularFragment;
import com.example.pratik.popular_movies_stage_2.NetworkService;
import static com.example.pratik.popular_movies_stage_2.BuildConfig.MOVIE_DB_API_KEY;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Pratik on 11/8/17.
 */

public class MovieListPresenter implements MovieListInteractor {

    private MoviesPopularFragment fragment;
    private String mListType;
    private NetworkService service;
    private Subscription subscription;

    private static final int MOVIE_ID = 1;
    private static final int MOVIE_TITLE = 2;
    private static final int MOIVE_OVERVIEW = 3;
    private static final int MOVIE_RELEASE_DATE = 4;
    private static final int MOVIE_POSTER_PATH = 5;
    private static final int MOVIE_VOTE_AVERAGE = 6;

    private static final String[] LIST_COLUMNS =
            {
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                    MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                    MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                    MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE
            };

    @Override
    public void loadMovieList() {
        if (mListType.equals("favorites"))
            pullDataBaseData();
        else
            pullNetworkData();
    }

    @Override
    public void unSubscribeMovieList() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    @Override
    public void setListType(String listType) {
        this.mListType = listType;
    }

    private void pullDataBaseData() {
        Uri movies = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cursor = fragment.getActivity().getContentResolver().query(movies,
                LIST_COLUMNS,
                MovieContract.MovieEntry.COLUMN_MOVIE_LIST_TYPE,
                new String[]{"FAVORITE"},
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " ASC");

        List<MovieDetails.MovieDetail> movieList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {

                MovieDetails.MovieDetail movieDetail = new MovieDetails.MovieDetail();

                movieDetail.setId(cursor.getInt(MOVIE_ID));
                movieDetail.setTitle(cursor.getString(MOVIE_TITLE));
                movieDetail.setOverview(cursor.getString(MOIVE_OVERVIEW));
                movieDetail.setReleaseDate(cursor.getString(MOVIE_RELEASE_DATE));
                movieDetail.setPosterPath(cursor.getString(MOVIE_POSTER_PATH));
                movieDetail.setVoteAverage(cursor.getDouble(MOVIE_VOTE_AVERAGE));

                movieDetail.setAdult(false);
                movieDetail.setGenreIds(new ArrayList<>());
                movieDetail.setOriginalTitle("");
                movieDetail.setOriginalLanguage("");
                movieDetail.setBackdropPath("");
                movieDetail.setPopularity(0.0);
                movieDetail.setVoteCount(0);
                movieDetail.setVideo(false);

                movieList.add(movieDetail);

            } while (cursor.moveToNext());
        }

        MovieDetails movieResults = new MovieDetails();
        movieResults.setResults(movieList);
        fragment.addMovies(movieResults);
    }

    private void pullNetworkData()
    {
        final String API_KEY_PARM = "api_key";
        final String LANG_PARAM = "language";
        final String LANG = "en-US";
        Map<String, String> params = new HashMap<>();
        params.put(API_KEY_PARM, MOVIE_DB_API_KEY);
        params.put(LANG_PARAM, LANG);

        Observable<MovieDetails> moviesObservable =
                (Observable<MovieDetails>)service
                        .getPreparedObservable(service.getAPI()
                                        .getMovies(mListType, params)
                                ,MovieDetails.class, true, true );
        subscription = moviesObservable.subscribe(new Observer<MovieDetails>() {
            @Override
            public void onCompleted()
            {

            }

            @Override
            public void onError(Throwable e)
            {

            }

            @Override
            public void onNext(MovieDetails movieDetailResults)
            {
                fragment.addMovies(movieDetailResults);
            }
        });
    }
    public MovieListPresenter(MoviesPopularFragment fragment, String listType)
    {
        this.fragment = fragment;
        this.service = new NetworkService();
        this.mListType = listType;
    }

}
