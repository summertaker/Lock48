package com.summertaker.lock48.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;

import com.summertaker.lock48.R;
import com.summertaker.lock48.common.BaseApplication;
import com.summertaker.lock48.common.BaseFragment;
import com.summertaker.lock48.common.Config;
import com.summertaker.lock48.data.Member;

import java.io.File;
import java.util.ArrayList;

public class TeamFragment extends BaseFragment implements TeamInterface {

    private int mPosition;
    private boolean mIsCacheMode;

    private TeamFragment.EventListener mListener;

    protected TeamAdapter mAdapter;

    private ArrayList<Member> mMembers = new ArrayList<>();

    // Container Activity must implement this interface
    public interface EventListener {
        public void onTeamFragmentEvent(String event);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mListener = (TeamFragment.EventListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    public static TeamFragment newInstance(int position, String groupId, String groupName, String teamName, boolean isCacheMode) {
        TeamFragment fragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("groupId", groupId);
        args.putString("groupName", groupName);
        args.putString("teamName", teamName);
        args.putBoolean("isCacheMode", isCacheMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.team_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPosition = getArguments().getInt("position", 0);
            String groupId = bundle.getString("groupId");
            String groupName = bundle.getString("groupName");
            String teamName = bundle.getString("teamName");
            mIsCacheMode = bundle.getBoolean("isCacheMode");

            //Log.e(mTag, "teamName: " + teamName);

            ArrayList<Member> members = BaseApplication.getInstance().loadMember(groupId);
            //Log.e(mTag, "members.size() = " + members.size());

            mMembers.clear(); // Activity 에서 Refresh 하는 경우에 대한 초기화 (초기화 안 하면 데이터가 중복으로 증가)

            for (Member member : members) {
                if (member.getTeamName().equals(teamName)) {
                    mMembers.add(member);
                }
            }

            String path = Config.DATA_PATH;
            File dir = new File(path);
            if (!dir.exists()) {
                boolean isSuccess = dir.mkdirs(); // 이미지 파일 저장 위치 생성 (권한은 MainActivity에서 미리 획득)
            }

            mAdapter = new TeamAdapter(getContext(), mMembers, mIsCacheMode, this);

            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
            gridView.setAdapter(mAdapter);

            /*
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Member member = (Member) parent.getItemAtPosition(position);
                    //Log.e(mTag, "name: " + member.getName());

                    member.setOshimember(!member.isOshimember());
                    boolean isOshimember = member.isOshimember();

                    ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();

                    if (isOshimember) { // 추가
                        boolean isExist = false;
                        for (Member m : oshiMembers) {
                            if (m.getUrl().equals(member.getUrl())) {
                                isExist = true;
                            }
                        }
                        if (!isExist) {
                            oshiMembers.add(member);
                        }
                    } else { // 제거
                        ArrayList<Member> members = new ArrayList<>();
                        for (Member m : oshiMembers) {
                            if (!m.getUrl().equals(member.getUrl())) {
                                members.add(m);
                            }
                        }
                        oshiMembers = members;
                    }

                    BaseApplication.getInstance().setmOshimembers(oshiMembers);
                    //Log.e(mTag, "mOshiMembers.size() = " + oshiMembers.size());

                    BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_OSHIMEMBERS, oshiMembers);

                    mAdapter.notifyDataSetChanged();
                }
            });
            */

            renderData();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void renderData() {
        //Log.d(mTag, "mMembers.size(): " + mMembers.size());

        mAdapter.notifyDataSetChanged();

        //mListener.onTeamFragmentEvent("onRefreshFinished");
    }

    @Override
    public void onStop() {
        super.onStop();

        BaseApplication.getInstance().cancelPendingRequests(mVolleyTag);

        ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();
        BaseApplication.getInstance().saveMember(Config.PREFERENCE_KEY_OSHIMEMBERS, oshiMembers);
    }

    public void refresh() {
        Log.e(mTag, "refresh()... Fragment(" + mPosition + ")");
    }

    @Override
    public void onPicutreClick(Member member) {
        saveData(member);
    }

    @Override
    public void onLikeClick(CheckBox checkBox, Member member) {
        saveData(member);
    }

    @Override
    public void onNameClick(Member member) {
        //Log.e(mTag, "member.getGroupId(): " + member.getGroupId());
        BaseApplication.getInstance().setMember(member);

        Intent intent = new Intent(getActivity(), MemberActivity.class);
        startActivity(intent);
    }

    private void saveData(Member member) {
        member.setOshimember(!member.isOshimember());
        boolean isOshimember = member.isOshimember();

        ArrayList<Member> oshiMembers = BaseApplication.getInstance().getOshimembers();

        if (isOshimember) { // 추가
            boolean isExist = false;
            for (Member m : oshiMembers) {
                if (m.getProfileUrl().equals(member.getProfileUrl())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                oshiMembers.add(member);
            }
        } else { // 제거
            ArrayList<Member> members = new ArrayList<>();
            for (Member m : oshiMembers) {
                if (!m.getProfileUrl().equals(member.getProfileUrl())) {
                    members.add(m);
                }
            }
            oshiMembers = members;
        }

        BaseApplication.getInstance().setmOshimembers(oshiMembers);
        //Log.e(mTag, "mOshiMembers.size() = " + oshiMembers.size());

        mAdapter.notifyDataSetChanged();
    }
}
