package com.summertaker.lock48.parser;

import android.util.Log;

import com.summertaker.lock48.common.BaseParser;
import com.summertaker.lock48.data.Group;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.data.Team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class Ngt48Parser extends BaseParser {

    public void parse(String html, Group group, ArrayList<Team> teams, ArrayList<Member> members) {
        //Log.e(mTag, "html: " + html);

        /*
        <ul class="team_list">
            <li>
                <a href="/profile/?id=azuma_yuki">
                    <span class="photo_guard"></span>
                    <img src="../img/profile/n/azuma_yuki.jpg" alt="東由樹"/>
                    東由樹
                    <span class="english">YUKI AZUMA</span>
                </a>
            </li>
        */

        if (html == null || html.isEmpty()) {
            return;
        }

        //html = Util.getJapaneseString(html, "8859_1");

        Document doc = Jsoup.parse(html);

        Element root = doc.select("#misc").first();
        if (root == null) {
            Log.e(mTag, "root is null...");
            return;
        }

        for (Element h4 : root.select("h4")) {
            String teamName;

            teamName = h4.text();

            //Log.e(mTag, "teamName: " + teamName);

            Team team = new Team();
            team.setName(teamName);
            teams.add(team);

            Element sibling = h4.nextElementSibling();
            for (Element element : sibling.select(".profile2")) {
                String name;
                String thumbnailUrl;
                String pictureUrl;
                String profileUrl;

                Element a = element.select("a").first();
                if (a == null) {
                    continue;
                }
                profileUrl = a.attr("href");
                profileUrl = "http://ngt48.jp" + profileUrl;

                // http://cache.hkt48sp.qw.to/img/profile/member/15-thumb.jpg?cache=20171228090418
                // http://cache.hkt48sp.qw.to/img/profile/member/15-large.jpg?cache=20171228093645
                Element img = a.select("img").first();
                if (img == null) {
                    continue;
                }
                String src = img.attr("src");
                thumbnailUrl = src;
                pictureUrl = src;

                Element n = element.select(".profile2_name").first();
                if (n == null) {
                    continue;
                }
                name = n.text();

                //Log.e(mTag, teamName + ", " + name + ", " + thumbnail + ", " + picture);

                Member member = new Member();
                member.setGroupId(group.getId());
                member.setGroupName(group.getName());
                member.setTeamName(team.getName());
                member.setName(name);
                member.setThumbnailUrl(thumbnailUrl);
                member.setPictureUrl(pictureUrl);
                member.setProfileUrl(profileUrl);

                members.add(member);
            }
        }
    }
}


