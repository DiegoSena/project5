package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ArticleDetailFragment.class.getSimpleName();

    public static final String ARG_ITEM_ID = "item_id";
    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private ImageView mImageView;
    private View mAppBar;
    private View mArticleDetailContainer;
    private TextView mArticleTitleTextView;
    private TextView mArticleAuthorTextView;
    private TextView mArticleBodyTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private SimpleDateFormat outputFormat = new SimpleDateFormat();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mRootView.setVisibility(View.INVISIBLE);
        return mRootView;
    }

    private void bindViews() {
        if (mRootView != null) {
            mImageView = (ImageView) mRootView.findViewById(R.id.detail_imageview);
            mAppBar = mRootView.findViewById(R.id.app_bar);
            mArticleDetailContainer = mRootView.findViewById(R.id.article_detail_container);
            mArticleTitleTextView = (TextView) mRootView.findViewById(R.id.article_detail_title);
            mArticleAuthorTextView = (TextView) mRootView.findViewById(R.id.article_detail_author);
            mArticleBodyTextView = (TextView) mRootView.findViewById(R.id.article_textbody);
            if (mCursor != null) {
                mRootView.setVisibility(View.VISIBLE);
                setImage();
                setTitle();
                setAuthor();
                setBody();

                FloatingActionButton fab = (FloatingActionButton) mRootView.findViewById(R.id.fab);
                fab.setOnClickListener(view -> startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)))
                        .getIntent(), getString(R.string.action_share))));
            }

        }

    }

    private void setBody() {
        mArticleBodyTextView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
    }

    private void setAuthor() {
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
             mArticleAuthorTextView.setText(DateUtils.getRelativeTimeSpanString(
                    publishedDate.getTime(),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString() + " by " + mCursor.getString(ArticleLoader.Query.AUTHOR));
        }else{
            mArticleAuthorTextView.setText(outputFormat.format(publishedDate) + " by " + mCursor.getString(ArticleLoader.Query.AUTHOR));
        }
    }

    private void setTitle() {
        mArticleTitleTextView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
    }

    private void setImage() {
        Glide.with(getActivity())
                .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                        int darkMutedColor = palette.getDarkMutedColor(defaultColor);
                        mAppBar.setBackgroundColor(darkMutedColor);
                        mArticleDetailContainer.setBackgroundColor(darkMutedColor);
                        return false;
                    }
                })
                .into(mImageView);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(LOG_TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            Log.i(LOG_TAG, "passing today's date");
            return new Date();
        }
    }
}
