package com.example.pratik.popular_movies_stage_2.Activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pratik.popular_movies_stage_2.Fragment.MoviesPopularFragment;
import com.example.pratik.popular_movies_stage_2.R;
import com.example.pratik.popular_movies_stage_2.Model.MovieDetails;
import com.example.pratik.popular_movies_stage_2.Fragment.MovieDetailFragment;

public class MainActivity extends AppCompatActivity implements MoviesPopularFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DetailFragment";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.moive_detail) != null)
        {
            Bundle args = new Bundle();
            args.putString(MoviesPopularFragment.LAYOUT_MANAGER_KEY, "twoPayne");
            MoviesPopularFragment frag = new MoviesPopularFragment();
            frag.setArguments(args);

            mTwoPane = true;

            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.moive_detail,
                                new MovieDetailFragment(),
                                DETAILFRAGMENT_TAG).commit();
        }
        else
        {
            mTwoPane = false;
        }

    }


    @Override
    public void onItemSelected(MovieDetails.MovieDetail movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, movie);
            MovieDetailFragment frag = new MovieDetailFragment();
            frag.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.moive_detail, frag, DETAILFRAGMENT_TAG).commit();
        } else {
            Intent movieDetailIntent = new Intent(this, DetailsActivity.class);
            movieDetailIntent.putExtra(getString(R.string.movie_details_data), (Parcelable) movie);
            startActivity(movieDetailIntent);
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            if (id == R.id.action_settings)
            {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        @Override
        protected void onStart() {
            super.onStart();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
}

