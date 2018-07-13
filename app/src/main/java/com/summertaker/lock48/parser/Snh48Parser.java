package com.summertaker.lock48.parser;

import com.summertaker.lock48.common.BaseParser;
import com.summertaker.lock48.data.Group;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.data.Team;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Snh48Parser extends BaseParser {

    public void parse(String html, Group group, ArrayList<Team> teams, ArrayList<Member> members) {
        /*
        <section id="profile">
            <ul class="profileList clearfix">
                <li>
                    <a href="/feature/ishida_chiho_fs">
                        <p class="ph">
                            <img src="/static/stu48/official/common/dummy.png" style="background-image:url(http://sp.stu48.com/image/profile/ishida_chiho.jpg);">
                        </p>
                        <div class="txtSide">
                            <p class="name">石田 千穂</p>
                            <p class="yomi">ISHIDA CHIHO</p>
                            <dl>
                                <dt>生年月日</dt><dd>2002年03月17日</dd>
                                <dt>出身地</dt><dd>広島県</dd>
                                <dt>血液型</dt><dd>O型</dd>
                            </dl>
                        </div>
                    </a>
                </li>
        */

        if (html == null || html.isEmpty()) {
            return;
        }

        //response = Util.getJapaneseString(response, "8859_1");

        try {
            JSONObject object = new JSONObject(html);
            JSONArray array = object.getJSONArray("rows");

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);

                String teamName = o.getString("tname");

                boolean isExist = false;
                for (Team team : teams) {
                    if (team.getName().equals(teamName)) {
                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {
                    Team team = new Team();
                    team.setName(teamName);
                    teams.add(team);
                }
            }

            String domain = "http://www." + group.getName().toLowerCase() + ".com";

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);

                String sid = "";
                String teamName = "";
                String name = "";
                String thumbnailUrl = "";
                String pictureUrl = "";
                String profileUrl = "";

                sid = o.getString("sid");
                teamName = o.getString("tname");
                name = o.getString("fname");

                thumbnailUrl = domain + "/images/member/mp_" + sid + ".jpg";
                pictureUrl = domain + "/images/member/zp_" + sid + ".jpg";

                profileUrl = domain + "/member_details.html?sid=" + sid;

                Member member = new Member();
                member.setGroupId(group.getId());
                member.setGroupName(group.getName());
                member.setTeamName(teamName);
                member.setName(name);
                member.setThumbnailUrl(thumbnailUrl);
                member.setPictureUrl(pictureUrl);
                member.setProfileUrl(profileUrl);

                members.add(member);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
