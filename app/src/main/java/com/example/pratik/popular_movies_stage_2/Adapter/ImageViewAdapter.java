package com.example.pratik.popular_movies_stage_2.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.pratik.popular_movies_stage_2.Model.MovieDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pratik on 11/8/17.
 */

public class ImageViewAdapter extends ArrayAdapter<MovieDetails.MovieDetail> {

    private final String LOG_TAG = ImageViewAdapter.class.getSimpleName();

    public ImageViewAdapter(Context context, List<MovieDetails.MovieDetail> movieDetails)
    {
        super(context, 0, movieDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieDetails.MovieDetail details = getItem(position);

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(10, 10, 10, 10);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(parent.getContext()).load(buildPosterPath(details.getPosterPath())).into(imageView);
        return imageView;
    }

    private String buildPosterPath(String key)
    {
        StringBuilder sb = new StringBuilder("http://image.tmdb.org/t/p/")
                .append("w185")
                .append(key);

        return sb.toString();
    }
}
