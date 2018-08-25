package com.liviurau.popularmovies.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.liviurau.popularmovies.R;
import com.liviurau.popularmovies.model.Trailer;
import com.liviurau.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liviu on 27-Feb-18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private final Context mContext;
    private final List<Trailer> trailers = new ArrayList<>();

    public TrailerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ConstraintLayout layout;
        private final ImageView video;
        private final TextView title;
        private final ImageButton playTrailerBtn;

        public TrailerViewHolder(View view) {
            super(view);

            layout = view.findViewById(R.id.trailer_layout);
            video = view.findViewById(R.id.trailer_VV);
            title = view.findViewById(R.id.trailer_TV);
            playTrailerBtn = view.findViewById(R.id.playTrailerBtn);

            layout.setOnClickListener(this);
            playTrailerBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String videoKey = video.getTag().toString();
            watchYoutubeVideo(mContext, videoKey);
        }
    }

    private void watchYoutubeVideo(Context mContext, String videoKey) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(NetworkUtils.YOUTUBE_VIDEO_PATH + videoKey));
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        Trailer trailer = trailers.get(position);

        holder.title.setText(trailer.getName());

        String img_url = NetworkUtils.YOUTUBE_THUMBNAIL_PATH + trailer.getKey() + "/0.jpg";

        Picasso.with(mContext).load(img_url).into(holder.video);
        holder.video.setTag(trailer.getKey());

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void swapList(List<Trailer> list){
        if (list != null) {
            this.trailers.clear();
            this.trailers.addAll(list);
            this.notifyDataSetChanged();
        }
    }

}
