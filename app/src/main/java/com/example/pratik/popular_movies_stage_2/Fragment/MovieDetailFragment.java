package com.example.pratik.popular_movies_stage_2.Fragment;

import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pratik.popular_movies_stage_2.RxApplication;
import com.example.pratik.popular_movies_stage_2.Model.MovieDetails;
import com.example.pratik.popular_movies_stage_2.R;
import com.example.pratik.popular_movies_stage_2.Model.VideoDetails;
import com.example.pratik.popular_movies_stage_2.Model.ReviewDetails;
import com.squareup.picasso.Picasso;
import com.example.pratik.popular_movies_stage_2.NetworkService;
import com.example.pratik.popular_movies_stage_2.Adapter.ExpandableListAdapter;
import com.example.pratik.popular_movies_stage_2.Model.MovieDetailsInteractor;
import com.example.pratik.popular_movies_stage_2.Model.MovieDetailsPresenter;
import com.example.pratik.popular_movies_stage_2.Data.MovieDbHelper;
import com.example.pratik.popular_movies_stage_2.Data.MovieContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pratik on 11/8/17.
 */

public class MovieDetailFragment extends Fragment {
    private final String LOG_TAG_FRAG = MovieDetailFragment.class.getSimpleName();
    private MovieDetails.MovieDetail mMovie;
    private List<String> mGroupHeaders;
    private HashMap<String, List<String>> listChildren;
    private ExpandableListAdapter mExpandableListAdapter;
    private NetworkService mService;
    private MovieDetailsInteractor mDetailsInteractor;
    private List<ReviewDetails.ReviewDetail> mReviewResults;
    private List<VideoDetails.VideoDetail> mTrailerResults;
    public static final String MOVIE_DETAIL_URI = "URI";
    private final int TRAILERS = 0;
    private final int REVIEWS = 1;

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void onPause() {
        mDetailsInteractor.unSubscribeMovieReviews();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.movie_details,container,false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mlistType = sharedPrefs.getString(getString(R.string.pref_list_key),getString(R.string.pref_list_default));
        String movieDetailData = getString(R.string.movie_details_data);

        Bundle args = getArguments();
        if(args != null) {
            mMovie = getArguments().getParcelable(MovieDetailFragment.MOVIE_DETAIL_URI);
        }
        if (mMovie == null)
        {
            Intent intent = getActivity().getIntent();
            mMovie = intent.getParcelableExtra(movieDetailData);
        }

        if (mMovie != null)
        {
            PackageManager packageManager = getActivity().getPackageManager();

            CheckBox chBox = (CheckBox)rootView.findViewById(R.id.checkbox_favorite);
            chBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (chBox.isChecked())
                        addFavorite(mMovie.getTitle());
                    else
                        removeFavorite(mMovie.getTitle(), mMovie.getId());
                }
            });

            if (mlistType.equals("favorites"))
                chBox.setChecked(true);

            ((TextView)rootView.findViewById(R.id.movie_text_view)).setText(mMovie.getTitle());

            ((RatingBar)rootView.findViewById(R.id.vote_average_bar)).setNumStars(5);
            ((RatingBar)rootView.findViewById(R.id.vote_average_bar)).setRating(calculateVoterAverage(mMovie.getVoteAverage()));

            ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_image_view);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(10, 10, 10, 10);
            Picasso.with(getContext()).load(buildPosterPath(mMovie.getPosterPath())).into(imageView);

            ((TextView)rootView.findViewById(R.id.date_text_view)).setText(mMovie.getReleaseDate());

            ((TextView)rootView.findViewById(R.id.overview_text_view)).setText(mMovie.getOverview());

            ExpandableListView exListView = (ExpandableListView) rootView.findViewById(R.id.detail_expand_view);
            mExpandableListAdapter = new ExpandableListAdapter(getContext(),  new ArrayList<>(), new HashMap<String, List<String>>());


            exListView.setAdapter(mExpandableListAdapter);

            exListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
                {
                    return false;
                }
            });

            exListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {

                }
            });

            exListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {

                }
            });

            exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    switch (groupPosition)
                    {
                        case TRAILERS:
                        {
                            String key = mTrailerResults.get(childPosition).getKey();
                            Uri trailerUri = new Uri.Builder().scheme("https")
                                    .authority("www.youtube.com")
                                    .appendPath("watch")
                                    .appendQueryParameter("v", key)
                                    .build();
                            Intent trailerIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
                            List<ResolveInfo> intentList = packageManager.queryIntentActivities(trailerIntent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (intentList.size() > 0) {
                                trailerIntent.putExtra("force_fullscreen", true);
                                startActivity(trailerIntent);
                            }
                            else
                            {
                                Toast.makeText(getContext(), "No available player is available for this trailer.",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case REVIEWS:
                        {
                            break;
                        }
                    }
                    return false;
                }
            });
        }
        else
            Log.e(LOG_TAG_FRAG, "Intent was null or data was not sent.");
        return rootView;
    }

    private void loadData()
    {
        mGroupHeaders =new ArrayList<>();

        mService = new RxApplication().getNetorkService();
        mGroupHeaders.add(0, "Trailers");
        mGroupHeaders.add(1, "Reviews");
        listChildren = new HashMap<>();

        mDetailsInteractor = new MovieDetailsPresenter(this, mService, mMovie.getId());
        mDetailsInteractor.loadMovieReviews();
    }

    public void loadRetroData(MovieDetailsPresenter.TrailersAndReviews trailersAndReviews)
    {
        mReviewResults = trailersAndReviews.mReviews.getResults();
        mTrailerResults = trailersAndReviews.mTrailers.getResults();

        List<String> reviews = new ArrayList<>();
        for(ReviewDetails.ReviewDetail rd : mReviewResults)
        {
            reviews.add(rd.getContent());
        }

        List<String> trailers = new ArrayList<>();
        for(VideoDetails.VideoDetail vd : mTrailerResults)
        {
            trailers.add(vd.getName());
        }

        mExpandableListAdapter.setReviewData(reviews);
        mExpandableListAdapter.setTrailerData(trailers);
    }

    private void addFavorite(String title)
    {
        MovieDbHelper helper = new MovieDbHelper(getContext());

        ContentValues mCV = getMovieContentValues();
        SQLiteDatabase db = helper.getWritableDatabase();
        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, mCV);

        if (movieId != 0)
            Toast.makeText(getContext(), title +  " was added to your Favorites list.",Toast.LENGTH_SHORT).show();

        db.close();
    }

    private ContentValues getMovieContentValues()
    {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, mMovie.getVoteAverage());
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_LIST_TYPE, "FAVORITE");
        return cv;
    }

    private void removeFavorite(String title, int movieId)
    {
        ContentResolver cr = getContext().getContentResolver();
        int delete = cr.delete(MovieContract.MovieEntry.buildMovieUri(movieId),
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{Integer.toString(movieId)});

        if (delete != 0)
            Toast.makeText(getContext(), title +  " was removed to your Favorites list.",Toast.LENGTH_SHORT).show();
    }

    private float calculateVoterAverage(double average)
    {
        return (float)average/2;
    }

    private String buildPosterPath(String imageKey)
    {
        StringBuilder sb = new StringBuilder("http://image.tmdb.org/t/p/")
                .append("w185")
                .append(imageKey);

        return sb.toString();
    }


}
