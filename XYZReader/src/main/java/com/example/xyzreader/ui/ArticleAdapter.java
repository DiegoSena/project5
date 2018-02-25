package com.example.xyzreader.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.Utilities;
import com.example.xyzreader.data.ArticleLoader;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    public ArticleAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        setTitle(holder);
        setSubtitle(holder);
        setThumbnail(holder);
        setArticleClickListener(holder, getItemId(position));
    }

    private void setSubtitle(ArticleViewHolder holder) {
        holder.subtitleView.setText(Utilities.getSubtitleTextInHTML(mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE),
                mCursor.getString(ArticleLoader.Query.AUTHOR)));
    }

    private void setThumbnail(ArticleViewHolder holder) {
        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        Glide.with(holder.thumbnailView.getContext())
                .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = (((GlideBitmapDrawable) resource.getCurrent()).getBitmap());
                        Palette palette = Palette.from(bitmap).generate();
                        int defaultColor = 0xFF444444;
                        int vibrant = palette.getDarkMutedColor(defaultColor);
                        holder.itemView.setBackgroundColor(vibrant);

                        return false;

                    }
                })
                .into(holder.thumbnailView);
    }

    private void setTitle(ArticleViewHolder holder) {
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void setArticleClickListener(ArticleViewHolder articleViewHolder, long itemId) {
        articleViewHolder.setArticleClickListener((ArticleClickListener) mContext, itemId);
    }
}
