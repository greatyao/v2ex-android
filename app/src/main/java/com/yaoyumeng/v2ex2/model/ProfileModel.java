package com.yaoyumeng.v2ex2.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yw on 2015/6/12.
 */
public class ProfileModel extends V2EXModel implements Parcelable {

    private static final long serialVersionUID = 2015050110L;

    public String username;
    public String avatar;
    public int nodes;
    public int topics;
    public int followings;
    public int notifications;

    public ProfileModel() {
    }

    public void parse(JSONObject jsonObject) throws JSONException {

    }

    public void parse(String responseBody) throws Exception {
        Document doc = Jsoup.parse(responseBody);
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValue("id", "Rightbar");
        int[] found = {0, 0, 0, 0};
        for (Element el : elements) {
            if (found[0] == 1 && found[1] == 1 && found[2] == 1 && found[3] == 1)
                break;
            Elements tdNodes = el.getElementsByTag("td");
            for (Element tdNode : tdNodes) {
                String content = tdNode.toString();
                if (found[0] == 0 && content.contains("a href=\"/member/")) {
                    Elements aNode = tdNode.getElementsByTag("a");
                    username = aNode.attr("href").replace("/member/", "");
                    Elements avatarNode = tdNode.getElementsByTag("img");
                    if (avatarNode != null) {
                        String avatarString = avatarNode.attr("src");
                        if (avatarString.startsWith("//")) {
                            avatarString = "http:" + avatarString;
                        }
                        avatar = avatarString;
                        found[0] = 1;
                    }
                } else if (found[1] == 0 && content.contains("a href=\"/my/nodes\"")) {
                    //text = 20 节点收藏
                    String text = tdNode.text();
                    text = text.split(" ")[0];
                    try {
                        nodes = Integer.parseInt(text);
                        found[1] = 1;
                    } catch (Exception e) {

                    }
                } else if (found[2] == 0 && content.contains("a href=\"/my/topics\"")) {
                    //text = 20 主题收藏
                    String text = tdNode.text();
                    text = text.split(" ")[0];
                    try {
                        topics = Integer.parseInt(text);
                        found[2] = 1;
                    } catch (Exception e) {

                    }
                } else if (found[3] == 0 && content.contains("a href=\"/my/following\"")) {
                    //text = 20 特别关注
                    String text = tdNode.text();
                    text = text.split(" ")[0];
                    try {
                        followings = Integer.parseInt(text);
                        found[3] = 1;
                    } catch (Exception e) {
                    }
                }
            }
        }

        Pattern pattern = Pattern.compile("<a href=\"/notifications\"([^>]*)>([0-9]+) 条未读提醒</a>");
        Matcher matcher = pattern.matcher(responseBody);
        if (matcher.find()) {
            notifications = Integer.parseInt(matcher.group(2));
        }
    }

    public static final Creator<ProfileModel> CREATOR = new Creator<ProfileModel>() {
        @Override
        public ProfileModel createFromParcel(Parcel source) {
            return new ProfileModel(source);
        }

        @Override
        public ProfileModel[] newArray(int size) {
            return new ProfileModel[size];
        }
    };

    private ProfileModel(Parcel in) {
        String[] ss = new String[2];
        in.readStringArray(ss);
        username = ss[0];
        avatar = ss[1];

        int[] ints = new int[4];
        in.readIntArray(ints);
        nodes = ints[0];
        topics = ints[1];
        followings = ints[2];
        notifications = ints[3];
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                username,
                avatar
        });

        dest.writeIntArray(new int[]{
                nodes,
                topics,
                followings,
                notifications
        });
    }
}
