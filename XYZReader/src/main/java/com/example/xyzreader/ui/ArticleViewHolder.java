package com.example.xyzreader.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.xyzreader.R;

/**
 * Created by diego on 18/02/18.
 */

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public DynamicHeightNetworkImageView thumbnailView;
    public TextView titleView;
    public TextView subtitleView;

    public ArticleViewHolder(View view) {
        super(view);
        this.view = view;
        thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
        titleView = (TextView) view.findViewById(R.id.article_title);
        subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
    }

    public void setArticleClickListener(final ArticleClickListener articleClickListener, long itemId) {
        view.setOnClickListener(v -> articleClickListener.onArticleClick(itemId));
    }
}