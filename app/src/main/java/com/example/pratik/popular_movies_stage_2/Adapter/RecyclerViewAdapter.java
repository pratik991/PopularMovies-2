package com.example.pratik.popular_movies_stage_2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.pratik.popular_movies_stage_2.R;
import com.example.pratik.popular_movies_stage_2.Fragment.MoviesPopularFragment;
import com.example.pratik.popular_movies_stage_2.Model.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Pratik on 11/8/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MovieHolder> {

    private ArrayList<MovieDetails.MovieDetail> mMovies;
    private final OnItemClickListener mListener;

    public RecyclerViewAdapter(ArrayList<MovieDetails.MovieDetail> movies, OnItemClickListener listener) {
        mMovies = movies;
        mListener = listener;
    }

    @Override
    public RecyclerViewAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_list,
                        parent,
                        false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MovieHolder holder, int position) {
        holder.bindMovie(mMovies.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public interface OnItemClickListener
    {
        void onItemClick(MovieDetails.MovieDetail movie);
    }

    public static class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView mMovieImage;
        private MovieDetails.MovieDetail mMovieDetail;

        private static final String MOVIE_KEY = "MOVIE";

        public MovieHolder(View view)
        {
            super(view);
            mMovieImage = (ImageView) view.findViewById(R.id.movie_poster);
            mMovieImage.setAdjustViewBounds(true);
        }

        @Override
        public void onClick(View v)
        {
            Context context = v.getContext();
            Intent showMovieIntent = new Intent(context, MoviesPopularFragment.class);
            showMovieIntent.putExtra(MOVIE_KEY, mMovieDetail);
            context.startActivity(showMovieIntent);
        }

        public void bindMovie(MovieDetails.MovieDetail movieDetail, final OnItemClickListener listener)
        {
            mMovieDetail = movieDetail;
            Picasso.with(mMovieImage.getContext())
                    .load(buildPosterPath(mMovieDetail.getPosterPath()))
                    .into(mMovieImage);

            mMovieImage.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movieDetail);
                }
            });

        }

        private String buildPosterPath(String imageKey)
        {
            StringBuilder sb = new StringBuilder("http://image.tmdb.org/t/p/")
                    .append("w185")
                    .append(imageKey);

            return sb.toString();
        }
    }
}
