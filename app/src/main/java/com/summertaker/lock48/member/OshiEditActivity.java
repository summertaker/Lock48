package com.summertaker.lock48.member;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;

import com.summertaker.lock48.R;
import com.summertaker.lock48.common.BaseActivity;
import com.summertaker.lock48.common.BaseApplication;
import com.summertaker.lock48.common.Config;
import com.summertaker.lock48.data.Member;

import java.util.ArrayList;

public class OshiEditActivity extends BaseActivity implements OshiEditInterface {

    private ArrayList<Member> mOshiMembers = new ArrayList<>();
    private OshiEditAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oshiedit_activity);

        mContext = OshiEditActivity.this;

        initToolbar(null);

        initGridView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.oshiedit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            //saveData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();

        saveData();
    }

    private void initGridView() {
        //mOshiMembers = BaseApplication.getInstance().getOshimembers();
        mOshiMembers = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_OSHIMEMBERS);
        for (Member member : mOshiMembers) {
            member.setSelected(true);
        }

        mAdapter = new OshiEditAdapter(mContext, mOshiMembers, this);

        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member member = (Member) parent.getItemAtPosition(position);
            }
        });
    }

    @Override
    public void onPictureClick(Member member) {
        //Log.e(mTag, member.getName());
        member.setSelected(!member.isSelected());
        saveData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLikeClick(CheckBox checkBox, Member member) {
        //Log.e(mTag, member.getName());
        member.setSelected(checkBox.isChecked());
        saveData();
    }

    private void saveData() {
        ArrayList<Member> members = new ArrayList<>();

        for (Member member : mOshiMembers) {
            if (member.isSelected()) {
                members.add(member);
            }
        }

        BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_OSHIMEMBERS, members);
        BaseApplication.getInstance().setmOshimembers(members);
    }
}
