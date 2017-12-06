package com.example.pratik.popular_movies_stage_2.Model;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import com.example.pratik.popular_movies_stage_2.Fragment.MovieDetailFragment;
import com.example.pratik.popular_movies_stage_2.NetworkService;
import static com.example.pratik.popular_movies_stage_2.BuildConfig.MOVIE_DB_API_KEY;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by Pratik on 11/8/17.
 */

public class MovieDetailsPresenter implements MovieDetailsInteractor {

    private MovieDetailFragment fragment;
    private NetworkService service;
    private Retrofit retrofit;
    private Subscription mReviewSubscrip;
    private int mMovieId;

    @Override
    public void loadMovieReviews() {
        final String API_KEY_PARM = "api_key";
        final String LANG_PARAM = "language";
        final String LANG = "en-US";
        Map<String, String> params = new HashMap<>();
        params.put(API_KEY_PARM, MOVIE_DB_API_KEY);
        params.put(LANG_PARAM, LANG);


        Observable<VideoDetails> trailerResults = retrofit
                .create(NetworkService.TheMovieDB.class)
                .getVideos(mMovieId, "videos", params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());


        Observable<ReviewDetails> reviewResults = retrofit
                .create(NetworkService.TheMovieDB.class)
                .getReviews(mMovieId, "reviews", params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<TrailersAndReviews> combined  = Observable.zip(reviewResults, trailerResults, new Func2<ReviewDetails,VideoDetails, TrailersAndReviews>(){
            @Override
            public TrailersAndReviews call(ReviewDetails reviewDetailResults, VideoDetails videoDetailResults) {
                return new TrailersAndReviews(reviewDetailResults,videoDetailResults);
            }
        });

        mReviewSubscrip = combined.subscribe(new Subscriber<TrailersAndReviews>() {
            @Override
            public void onNext(TrailersAndReviews trailersAndReviews) {

                fragment.loadRetroData(trailersAndReviews);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }
        });

    }

    @Override
    public void unSubscribeMovieReviews() {

        if(mReviewSubscrip !=null && !mReviewSubscrip.isUnsubscribed())
            mReviewSubscrip.unsubscribe();

    }

    public class TrailersAndReviews
    {
        public ReviewDetails mReviews;
        public VideoDetails mTrailers;

        public TrailersAndReviews(ReviewDetails reviews, VideoDetails trailers)
        {
            this.mReviews = reviews;
            this.mTrailers = trailers;
        }
    }

    public MovieDetailsPresenter(MovieDetailFragment fragment, NetworkService service, int mMovieId)
    {
        this.fragment = fragment;
        this.service = new NetworkService();
        this.mMovieId = mMovieId;
        this.retrofit = this.service.getRetrofit();
    }
}
