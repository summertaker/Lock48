package com.summertaker.lock48.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.summertaker.lock48.R;
import com.summertaker.lock48.data.Group;
import com.summertaker.lock48.data.Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private ArrayList<Group> mGroups = new ArrayList<>();
    private ArrayList<Member> mOshimembers = new ArrayList<>();

    private Member mMember = new Member();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mGroups.add(new Group("akb48", "AKB48", R.drawable.logo_akb48, "http://sp.akb48.co.jp/profile/", Config.USER_AGENT_MOBILE));
        mGroups.add(new Group("ske48", "SKE48", R.drawable.logo_ske48, "http://spn.ske48.co.jp/profile/list.php", Config.USER_AGENT_MOBILE));
        mGroups.add(new Group("nmb48", "NMB48", R.drawable.logo_nmb48, "http://spn2.nmb48.com/profile/list.php", Config.USER_AGENT_MOBILE));
        mGroups.add(new Group("hkt48", "HKT48", R.drawable.logo_hkt48, "http://sp.hkt48.jp/qhkt48_list", Config.USER_AGENT_MOBILE));
        mGroups.add(new Group("ngt48", "NGT48", R.drawable.logo_ngt48, "http://ngt48.jp/profile", Config.USER_AGENT_DESKTOP));
        mGroups.add(new Group("stu48", "STU48", R.drawable.logo_stu48, "http://www.stu48.com/feature/profile", Config.USER_AGENT_DESKTOP));
        mGroups.add(new Group("nogizaka46", "乃木坂46", R.drawable.logo_nogizaka46, "http://www.nogizaka46.com/smph/member/", Config.USER_AGENT_MOBILE));
        mGroups.add(new Group("keyakizaka46", "欅坂46", R.drawable.logo_keyakizaka46, "http://www.keyakizaka46.com/s/k46o/search/artist?ima=0000", Config.USER_AGENT_MOBILE));

        mGroups.add(new Group("snh48", "SNH48", R.drawable.logo_snh48, "http://h5.snh48.com/resource/jsonp/members.php?gid=10", Config.USER_AGENT_DESKTOP));
        mGroups.add(new Group("bej48", "BEJ48", R.drawable.logo_bej48, "http://h5.snh48.com/resource/jsonp/members.php?gid=20", Config.USER_AGENT_DESKTOP));
        mGroups.add(new Group("gnz48", "GNZ48", R.drawable.logo_gnz48, "http://h5.snh48.com/resource/jsonp/members.php?gid=30", Config.USER_AGENT_DESKTOP));
        mGroups.add(new Group("shy48", "SHY48", R.drawable.logo_shy48, "http://h5.snh48.com/resource/jsonp/members.php?gid=40", Config.USER_AGENT_DESKTOP));
        mGroups.add(new Group("ckg48", "CKG48", R.drawable.logo_ckg48, "http://h5.snh48.com/resource/jsonp/members.php?gid=50", Config.USER_AGENT_DESKTOP));

        mOshimembers = loadMember(Config.PREFERENCE_KEY_OSHIMEMBERS);
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ArrayList<Group> getGroups() {
        return mGroups;
    }

    public Group getGroupById(String id) {
        Group group = null;
        for (Group data : mGroups) {
            if (data.getId().equals(id)) {
                group = data;
                break;
            }
        }

        return group;
    }

    public Member getMember() {
        return mMember;
    }

    public void setMember(Member mMember) {
        this.mMember = mMember;
    }

    public ArrayList<Member> getOshimembers() {
        return mOshimembers;
    }

    public void setmOshimembers(ArrayList<Member> members) {
        mOshimembers = members;
    }

    public ArrayList<Member> loadMember(String key) {
        SharedPreferences mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        String jsonString = mSharedPreferences.getString(key, null);
        //Log.e(mTag, "jsonString: " + jsonString);

        ArrayList<Member> members = new ArrayList<>();

        if (jsonString != null) {
            JSONObject object = null;
            try {
                object = new JSONObject(jsonString);
                JSONArray array = object.getJSONArray("members");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Member m = new Member();
                    m.setGroupId(obj.getString("groupId"));
                    m.setGroupName(obj.getString("groupName"));
                    m.setTeamName(obj.getString("teamName"));
                    m.setName(obj.getString("name"));
                    m.setThumbnailUrl(obj.getString("thumbnailUrl"));
                    m.setPictureUrl(obj.getString("pictureUrl"));
                    m.setProfileUrl(obj.getString("profileUrl"));
                    m.setOshimember(obj.getBoolean("isOshimember"));
                    members.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return members;
    }

    public void saveMember(String key, ArrayList<Member> members) {
        SharedPreferences mSharedPreferences = getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor mSharedEditor = mSharedPreferences.edit();

        try {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();

            for (Member member : members) {
                JSONObject o = new JSONObject();
                o.put("groupId", member.getGroupId());
                o.put("groupName", member.getGroupName());
                o.put("teamName", member.getTeamName());
                o.put("name", member.getName());
                o.put("thumbnailUrl", member.getThumbnailUrl());
                o.put("pictureUrl", member.getPictureUrl());
                o.put("profileUrl", member.getProfileUrl());
                o.put("isOshimember", member.isOshimember());
                array.put(o);
            }

            object.put("members", array);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor.putString(key, object.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
