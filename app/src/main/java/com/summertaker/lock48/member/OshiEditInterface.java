package com.summertaker.lock48.member;

import android.widget.CheckBox;

import com.summertaker.lock48.data.Member;

public interface OshiEditInterface {

    void onPictureClick(Member member);

    void onLikeClick(CheckBox checkBox, Member member);
}
