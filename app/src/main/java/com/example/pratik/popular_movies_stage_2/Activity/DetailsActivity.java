package com.example.pratik.popular_movies_stage_2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.example.pratik.popular_movies_stage_2.R;
import com.example.pratik.popular_movies_stage_2.Fragment.MovieDetailFragment;

/**
 * Created by Pratik on 11/8/17.
 */

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null)
        {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_DETAIL_URI, getIntent().getData());

            MovieDetailFragment frag = new MovieDetailFragment();
            frag.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.moive_detail, frag).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
