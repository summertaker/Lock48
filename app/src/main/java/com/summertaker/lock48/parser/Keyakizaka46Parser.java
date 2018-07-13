package com.summertaker.lock48.parser;

import com.summertaker.lock48.common.BaseParser;
import com.summertaker.lock48.data.Group;
import com.summertaker.lock48.data.Member;
import com.summertaker.lock48.data.Team;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class Keyakizaka46Parser extends BaseParser {

    public void parse(String html, Group group, ArrayList<Team> teams, ArrayList<Member> members) {

        if (html == null || html.isEmpty()) {
            return;
        }

        //response = Util.getJapaneseString(response, "8859_1");

        Document doc = Jsoup.parse(html);
        Element root = doc.select(".sort-default").first();

        if (root != null) {
            //Log.d(mTag, root.text());

            for (Element element : root.select(".box-member")) {

                Element el = element.select("h3").first();
                if (el == null) {
                    continue;
                }

                String teamName = el.text();
                //Log.e(mTag, teamName);
                Team team = new Team();
                team.setName(teamName);
                teams.add(team);

                Element ul = element.select("ul").first();
                if (ul == null) {
                    continue;
                }

                ul = ul.select("ul").first();
                if (ul == null) {
                    continue;
                }

                for (Element row : ul.select("li")) {
                    String name;
                    String thumbnailUrl;
                    String pictureUrl;
                    String profileUrl;

                    Element a = row.select("a").first();
                    profileUrl = "http://www.keyakizaka46.com" + a.attr("href");

                    Element img = a.select("img").first();
                    if (img == null) {
                        continue;
                    }

                    String src = img.attr("src");
                    thumbnailUrl = src;
                    pictureUrl = src;

                    name = a.select("p.name").text();

                    //Log.e(mTag, teamName + " / " + name);

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
}
