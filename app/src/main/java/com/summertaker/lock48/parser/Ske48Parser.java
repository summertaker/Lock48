package com.summertaker.lock48.parser;

import android.util.Log;

import com.summertaker.lock48.common.BaseParser;
import com.summertaker.lock48.data.Group;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.data.Team;
import com.summertaker.lock48.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Ske48Parser extends BaseParser {

    public void parseTeam(String html, ArrayList<Team> teamList) {
        /*
        <ul class="team_navi">
            <li><a href="?team=s"><img src="imgList/teams_on.png" alt="Team S" /></a></li>
            <li><a href="?team=k2"><img src="imgList/teamk2.png" alt="Team KⅡ" /></a></li>
            <li><a href="?team=e"><img src="imgList/teame.png" alt="Team E" /></a></li>
            <li><a href="?team=ken"><img src="imgList/kenkyu.png" alt="研究生" /></a></li>
        </ul>
        */

        html = Util.getJapaneseString(html, "8859_1");

        if (html == null || html.isEmpty()) {
            return;
        }

        Document doc = Jsoup.parse(html);
        Element root = doc.select(".team_navi").first();

        if (root == null) {
            return;
        }

        for (Element element : root.select("li")) {
            String name;
            String url;

            Element a = element.select("a").first();
            url = a.attr("href");
            url = url.replace("./", "");
            url = "http://spn.ske48.co.jp/profile/list.php" + url;

            Element img = a.select("img").first();
            name = img.attr("alt");

            //Log.e(mTag, name + " / " + url);

            Team team = new Team();
            team.setName(name);
            team.setUrl(url);

            teamList.add(team);
        }
    }

    public void parseMember(String html, Group group, Team team, ArrayList<Member> members) {
        //Log.e(mTag, "html: " + html);

        /*
        <ul class="team_list">
            <li>
                <a href="index.php?id=isshiki_rena">
                    <span class="photo_guard"></span>
                    <img src="http://sp.ske48.co.jp/img/400x400/isshiki_rena.jpg" alt="一色嶺奈"/>
                    一色嶺奈
                    <span class="english">RENA ISSHIKI</span>
                </a>
            </li>
        */

        if (html == null || html.isEmpty()) {
            return;
        }

        html = Util.getJapaneseString(html, "8859_1");

        Document doc = Jsoup.parse(html);
        Element root = doc.select(".team_list").first();
        if (root == null) {
            Log.e(mTag, "[HTML ERROR] root is null.");
            return;
        }

        for (Element li : root.select("li")) {
            String name;
            String thumbnailUrl;
            String pictureUrl;
            String profileUrl;

            Element el;

            Element a = li.select("a").first();
            profileUrl = a.attr("href");
            profileUrl = "http://spn.ske48.co.jp/profile/" + profileUrl;

            Element img = a.select("img").first();
            if (img == null) {
                continue;
            }
            String src = img.attr("src");
            thumbnailUrl = src;
            pictureUrl = src;

            name = img.attr("alt");

            //Log.e(mTag, group.getName() + " / " + team.getName() + " / " + name + " / " + profileUrl);

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

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="memberDetail">
            <div class="memberDetailPhoto">
                <img src="//cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_A_png%2Firiyama_anna.png" width="170" height="170" alt="入山 杏奈" />
            </div>
            <div class="memberDetailProfile">
                <p class="memberDetailProfileHurigana">イリヤマ アンナ</p>
                <h3 class="memberDetailProfileName">入山 杏奈</h3>
                <p class="memberDetailProfileEName">Anna Iriyama</p>
                <div class="memberDetailProfileWrapper">
                    <ul>
                        <li>
                            <h4 class="memberDetailProfileLeft">Office</h4>
                            <p class="memberDetailProfileRight">太田プロダクション</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">Nickname</h4>
                            <p class="memberDetailProfileRight">あんにん</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">Date of birth</h4>
                            <p class="memberDetailProfileRight">1995.12.03</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">From</h4>
                            <p class="memberDetailProfileRight">Chiba</p>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        */
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".memberDetail").first();
        if (root == null) {
            return hashMap;
        }

        String imageUrl = "";
        Element photo = root.select(".memberDetailPhoto > img").first();
        if (photo == null) {
            return hashMap;
        }
        imageUrl = "http:" + photo.attr("src");
        hashMap.put("imageUrl", imageUrl);

        Element profile = doc.select(".memberDetailProfile").first();
        if (profile == null) {
            return hashMap;
        }
        //Log.i("##### root", root.toString());

        Element el;

        el = profile.select(".memberDetailProfileHurigana").first();
        if (el == null) {
            return hashMap;
        }
        hashMap.put("furigana", el.text().trim());

        el = profile.select(".memberDetailProfileName").first();
        if (el == null) {
            return hashMap;
        }
        String name = el.text().trim();
        hashMap.put("name", name);

        el = profile.select(".memberDetailProfileEName").first();
        if (el == null) {
            return hashMap;
        }
        String nameEn = el.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        String html = "";
        Element subdetail = profile.select(".memberDetailProfileWrapper").first();
        if (subdetail != null) {
            Element ul = subdetail.select("ul").first();
            if (ul != null) {
                int count = 0;
                for (Element li : ul.select("li")) {
                    String title = li.child(0).text().trim();
                    String value = li.child(1).text().trim();
                    if (count > 0) {
                        html += "<br>";
                    }
                    html += title + "：" + value;
                    count++;
                }
            }
        }
        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }
}