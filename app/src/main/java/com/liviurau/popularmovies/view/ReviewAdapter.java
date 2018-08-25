package com.liviurau.popularmovies.view;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liviurau.popularmovies.R;
import com.liviurau.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liviu on 27-Feb-18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> reviews = new ArrayList<>();

    public ReviewAdapter() {
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ConstraintLayout layout;
        private final TextView author;
        private final TextView content;

        public ReviewViewHolder(View view) {
            super(view);

            layout = view.findViewById(R.id.review_layout);
            author = view.findViewById(R.id.author_tv);
            content = view.findViewById(R.id.content_tv);

            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        Review review = reviews.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void swapList(List<Review> list){
        if (list != null) {
            this.reviews.clear();
            this.reviews.addAll(list);
            this.notifyDataSetChanged();
        }
    }

}
