package com.summertaker.lock48.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.lock48.R;
import com.summertaker.lock48.common.BaseActivity;
import com.summertaker.lock48.common.BaseApplication;
import com.summertaker.lock48.common.BaseParser;
import com.summertaker.lock48.common.Config;
import com.summertaker.lock48.data.Group;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.data.Team;
import com.summertaker.lock48.parser.Akb48Parser;
import com.summertaker.lock48.parser.Hkt48Parser;
import com.summertaker.lock48.parser.Keyakizaka46Parser;
import com.summertaker.lock48.parser.Ngt48Parser;
import com.summertaker.lock48.parser.Nmb48Parser;
import com.summertaker.lock48.parser.Nogizaka46Parser;
import com.summertaker.lock48.parser.Ske48Parser;
import com.summertaker.lock48.parser.Snh48Parser;
import com.summertaker.lock48.parser.Stu48Parser;
import com.summertaker.lock48.util.SlidingTabLayout;
import com.summertaker.lock48.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamActivity extends BaseActivity implements TeamFragment.EventListener {

    private Group mGroup;

    RelativeLayout mLoLoading;
    LinearLayout mLoContent;

    private int mLoadCount = 0;
    private ArrayList<Team> mTeams = new ArrayList<>();
    private ArrayList<Member> mMembers = new ArrayList<>();

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private SectionsPagerAdapter mPagerAdapter;

    private boolean mIsCacheMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_activity);

        mContext = TeamActivity.this;

        Intent intent = getIntent();
        String groupId = intent.getStringExtra("groupId");
        mGroup = BaseApplication.getInstance().getGroupById(groupId);

        initToolbar(mGroup.getName());

        mLoLoading = findViewById(R.id.loLoading);
        mLoContent = findViewById(R.id.loContent);

        mViewPager = findViewById(R.id.viewpager);
        mSlidingTabLayout = findViewById(R.id.sliding_tabs);

        loadGroup();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadGroup() {
        //Log.e(mTag, "mGroup.getUrls().size(): " + mGroup.getUrls().size());

        if (mGroup.getUrl() == null) {
            Util.alert(mContext, getString(R.string.error), "URL is null.", TeamActivity.this);
        } else {
            if (mIsCacheMode) {
                String fileName = Util.getUrlToFileName(mGroup.getUrl()) + ".html";
                //Log.e(mTag, "fileName: " + fileName);

                File file = new File(Config.DATA_PATH, fileName);
                if (file.exists()) {
                    parseData(Util.readFile(fileName));
                } else {
                    requestData(mGroup.getUrl());
                }
            } else {
                requestData(mGroup.getUrl());
            }
        }
    }

    private void loadTeam() {
        //Log.e(mTag, "mLoadCount: " + mLoadCount + ", mTeams.size(): " + mTeams.size());

        if (mLoadCount <= mTeams.size()) {
            String url = mTeams.get(mLoadCount - 1).getUrl();

            if (mIsCacheMode) {
                String fileName = Util.getUrlToFileName(url) + ".html";
                File file = new File(Config.DATA_PATH, fileName);
                if (file.exists()) {
                    parseData(Util.readFile(fileName));
                } else {
                    requestData(url);
                }
            } else {
                requestData(url);
            }
        } else {
            //Log.e(mTag, "mTeams.size(): " + mTeams.size() + ", mMembers.size(): " + mMembers.size());
            //Util.alert(mContext, null, "Team load finished.", null);

            ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();

            for (Member m : mMembers) {
                for (Member o : oshiMembers) {
                    if (m.getProfileUrl().equals(o.getProfileUrl())) {
                        m.setOshimember(true);
                    }
                }
            }

            BaseApplication.getInstance().saveMember(mGroup.getId(), mMembers);

            //if (mIsRefreshMode) {
            //    refreshData();
            //} else {
                renderData();
            //}
        }
    }

    private void requestData(final String url) {
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                writeData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.alert(mContext, getString(R.string.error), error.getMessage(), null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", mGroup.getUserAgent());
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mTag);
    }

    private void writeData(String url, String response) {
        Util.writeToFile(Util.getUrlToFileName(url) + ".html", response);
        parseData(response);
    }

    private void parseData(String response) {
        if (response.isEmpty()) {
            Util.alert(mContext, getString(R.string.error), "response is empty.", null);
        } else {
            if (mGroup.getId().equals("akb48") || mGroup.getId().equals("ske48") || mGroup.getId().equals("nmb48")) {
                BaseParser parser = null;
                switch (mGroup.getId()) {
                    case "akb48":
                        parser = new Akb48Parser();
                        break;
                    case "ske48":
                        parser = new Ske48Parser();
                        break;
                    case "nmb48":
                        parser = new Nmb48Parser();
                        break;
                }
                if (parser != null) {
                    if (mLoadCount == 0) { // (mTeams.size() == 0) {
                        parser.parseTeam(response, mTeams);
                    } else {
                        parser.parseMember(response, mGroup, mTeams.get(mLoadCount - 1), mMembers);
                    }
                    mLoadCount++;
                    loadTeam();
                }
            } else {
                if (mGroup.getId().equals("hkt48")) {
                    Hkt48Parser hkt48Parser = new Hkt48Parser();
                    hkt48Parser.parse(response, mGroup, mTeams, mMembers);
                } else if (mGroup.getId().equals("ngt48")) {
                    Ngt48Parser ngt48Parser = new Ngt48Parser();
                    ngt48Parser.parse(response, mGroup, mTeams, mMembers);
                } else if (mGroup.getId().equals("stu48")) {
                    Stu48Parser stu48Parser = new Stu48Parser();
                    stu48Parser.parse(response, mGroup, mTeams, mMembers);
                } else if (mGroup.getId().equals("nogizaka46")) {
                    Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                    nogizaka46Parser.parse(response, mGroup, mTeams, mMembers);
                } else if (mGroup.getId().equals("keyakizaka46")) {
                    Keyakizaka46Parser keyakizaka46Parser = new Keyakizaka46Parser();
                    keyakizaka46Parser.parse(response, mGroup, mTeams, mMembers);
                } else { // if (mGroup.getName().equals("SNH48")) {
                    Snh48Parser snh48Parser = new Snh48Parser();
                    snh48Parser.parse(response, mGroup, mTeams, mMembers);
                }

                mLoadCount = mTeams.size() + 1;

                loadTeam();
            }
        }
    }

    private void renderData() {
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        //-------------------------------------------------------------------------------------------------------
        // 뷰페이저 간 이동 시 프레그먼트 자동으로 새로고침 방지
        // https://stackoverflow.com/questions/28494637/android-how-to-stop-refreshing-fragments-on-tab-change
        //-------------------------------------------------------------------------------------------------------
        mViewPager.setOffscreenPageLimit(mTeams.size());

        mSlidingTabLayout.setViewPager(mViewPager);

        //mPagerAdapter.notifyDataSetChanged();

        mLoLoading.setVisibility(View.GONE);
        mLoContent.setVisibility(View.VISIBLE);
    }

    private void refresh() {
        mIsCacheMode = false;

        mLoadCount = 0;
        mTeams.clear();
        mMembers.clear();

        mPagerAdapter.notifyDataSetChanged();
        mPagerAdapter = null;
        mViewPager.setAdapter(null);
        mSlidingTabLayout.setViewPager(mViewPager);

        mLoLoading.setVisibility(View.VISIBLE);
        mLoContent.setVisibility(View.GONE);

        loadGroup();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TeamFragment.newInstance(position, mGroup.getId(), mGroup.getName(), mTeams.get(position).getName(), mIsCacheMode);
        }

        @Override
        public int getCount() {
            return mTeams.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTeams.get(position).getName(); // "Tab " + position;
        }
    }

    public void refreshData() {
        runFragment("refresh");
    }

    public void runFragment(String command) {
        //--------------------------------------------------------------------------------------------
        // 프레그먼트에 이벤트 전달하기
        // https://stackoverflow.com/questions/34861257/how-can-i-set-a-tag-for-viewpager-fragments
        //--------------------------------------------------------------------------------------------
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());

        // based on the current position you can then cast the page to the correct Fragment class
        // and call some method inside that fragment to reload the data:
        //if (0 == mViewPager.getCurrentItem() && null != f) {
        if (fragment != null) {
            TeamFragment f = (TeamFragment) fragment;

            switch (command) {
                case "refresh":
                    f.refresh();
                    break;
            }
        }
    }

    @Override
    public void onTeamFragmentEvent(String event) {

    }
}
