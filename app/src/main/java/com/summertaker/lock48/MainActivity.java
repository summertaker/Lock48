package com.summertaker.lock48;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;

import com.summertaker.lock48.common.BaseApplication;
import com.summertaker.lock48.common.Config;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.member.GroupActivity;
import com.summertaker.lock48.member.OshiEditActivity;
import com.summertaker.lock48.member.OshiEditAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements MainInterface {

    private static final int REQUEST_PERMISSION_CODE = 100;

    private ArrayList<Member> mOshiMembers = new ArrayList<>();
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //----------------------------------------------------------------------------
        // 런타임에 권한 요청
        // https://developer.android.com/training/permissions/requesting.html?hl=ko
        //----------------------------------------------------------------------------
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start();
                } else {
                    // permission denied
                    onPermissionDenied();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void onPermissionDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("권한이 거부되었습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    void start() {
        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);

        //mOshiMembers = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_OSHIMEMBERS);
        //BaseApplication.getInstance().setmOshimembers(mOshiMembers);

        initGridView();
    }

    @Override
    public void onResume() {
        super.onResume();

        mOshiMembers = BaseApplication.getInstance().getOshimembers();
        Collections.shuffle(mOshiMembers);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_edit) {
            Intent intent = new Intent(MainActivity.this, OshiEditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initGridView() {
        //mOshiMembers = BaseApplication.getInstance().getOshimembers();
        mOshiMembers = BaseApplication.getInstance().loadMember(Config.PREFERENCE_KEY_OSHIMEMBERS);
        for (Member member : mOshiMembers) {
            member.setSelected(true);
        }

        mAdapter = new MainAdapter(this, mOshiMembers, this);

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
