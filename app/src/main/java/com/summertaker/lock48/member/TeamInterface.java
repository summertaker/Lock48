package com.summertaker.lock48.member;

import android.widget.CheckBox;

import com.summertaker.lock48.data.Member;

public interface TeamInterface {

    void onPicutreClick(Member member);

    void onLikeClick(CheckBox checkBox, Member member);

    void onNameClick(Member member);
}
