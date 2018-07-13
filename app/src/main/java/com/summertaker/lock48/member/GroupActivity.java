package com.summertaker.lock48.member;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.summertaker.lock48.R;
import com.summertaker.lock48.common.BaseActivity;
import com.summertaker.lock48.common.BaseApplication;
import com.summertaker.lock48.data.Group;

import java.util.ArrayList;

public class GroupActivity extends BaseActivity {

    private Group mGroup;

    private ArrayList<Group> mGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);

        mContext = GroupActivity.this;

        //Intent intent = getIntent();
        //mGroupId = intent.getStringExtra("groupId");

        mGroups = BaseApplication.getInstance().getGroups();

        initToolbar(null);
        //initToolbarProgressBar();

        initGridView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initGridView() {
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new GroupAdapter(mContext, mGroups));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGroup = (Group) parent.getItemAtPosition(position);
                //showToolbarProgressBar();
                //loadGroup();

                Intent intent = new Intent(mContext, TeamActivity.class);
                intent.putExtra("groupId", mGroup.getId());
                startActivityForResult(intent, 100);
            }
        });
    }
}
