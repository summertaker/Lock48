package com.summertaker.lock48.common;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.summertaker.lock48.R;

public class ImageViewerActivity extends BaseActivity {

    LinearLayout mLoLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer_activity);

        mContext = ImageViewerActivity.this;

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String url = bundle.getString("url");
        //Log.e(mTag, "URL: " + url);

        initToolbar(title);

        mLoLoading = findViewById(R.id.loLoading);

        ImageView ivPicture = findViewById(R.id.ivPicture);

        Picasso.with(mContext).load(url).into(ivPicture, new Callback() {
            @Override
            public void onSuccess() {
                mLoLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                mLoLoading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
