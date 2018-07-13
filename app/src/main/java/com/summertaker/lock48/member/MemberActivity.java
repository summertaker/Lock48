package com.summertaker.lock48.member;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.summertaker.lock48.R;
import com.summertaker.lock48.common.BaseActivity;
import com.summertaker.lock48.common.BaseApplication;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.data.WebData;

import java.util.ArrayList;

public class MemberActivity extends BaseActivity {

    private Member mMember;

    private ArrayList<WebData> mYahooList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activity);

        mContext = MemberActivity.this;

        mMember = BaseApplication.getInstance().getMember();

        initToolbar(mMember.getName());

        //ImageView ivPicture = findViewById(R.id.ivPicture);
        //Picasso.with(mContext).load(mMember.getPicture()).into(ivPicture);

        ImageView ivPicture;
        if (mMember.getGroupId().equals("akb48")) {
            ivPicture = findViewById(R.id.ivPictureCrop);
        } else {
            ivPicture = findViewById(R.id.ivPictureWrap);
        }
        ivPicture.setVisibility(View.VISIBLE);

        Picasso.with(mContext).load(mMember.getPictureUrl()).into(ivPicture);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
