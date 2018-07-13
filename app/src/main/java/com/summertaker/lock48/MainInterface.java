package com.summertaker.lock48;

import android.widget.CheckBox;

import com.summertaker.lock48.data.Member;

public interface MainInterface {

    void onPictureClick(Member member);

    void onLikeClick(CheckBox checkBox, Member member);
}
