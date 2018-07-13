package com.summertaker.lock48.common;

import android.content.Context;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected String mTag;
    protected String mVolleyTag;

    protected Context mContext;

    public BaseFragment() {
        this.mTag = "== " + this.getClass().getSimpleName();
        this.mVolleyTag = this.mTag;
    }
}

