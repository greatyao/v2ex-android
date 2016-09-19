package com.yaoyumeng.v2ex2.model;

import com.yaoyumeng.v2ex2.utils.ContentUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yw on 2015/5/14.
 */
public class NotificationModel extends V2EXModel {

    private static final long serialVersionUID = 2015050106L;

    public TopicModel notificationTopic;
    public MemberModel notificationMember;
    public String notificationId;
    public String notificationDescriptionBefore;
    public String notificationDescriptionAfter;

    public void parse(JSONObject jsonObject) throws JSONException {
        jsonObject.get("notification");
    }

    public boolean parse(Element element) {
        Elements tds = element.getElementsByTag("td");
        if (tds.size() != 2) return false;

        notificationMember = new MemberModel();
        Element first = tds.get(0);
        String avatarUrl = first.getElementsByClass("avatar").attr("src");
        String avatarMember = first.getElementsByTag("a").attr("href");
        notificationMember.avatar = avatarUrl.startsWith("//")
                ? "http:" + avatarUrl
                : "http://www.v2ex.com" + avatarUrl;
        notificationMember.username = avatarMember.replace("/member/", "");

        notificationTopic = new TopicModel();
        Element second = tds.get(1);
        String rawContents = second.toString();

        Pattern pattern = Pattern.compile("deleteNotification\\((.*?),");
        Matcher matcher = pattern.matcher(rawContents);
        if (matcher.find()) {
            notificationId = matcher.group(1);
        }

        Elements aNodes = second.getElementsByTag("a");
        for (Element aNode : aNodes) {
            if (aNode.toString().contains("reply")) {
                notificationTopic.title = aNode.html();

                String topicURLString = aNode.attr("href");
                String[] ss = topicURLString.substring(3).split("#");

                try {
                    notificationTopic.id = Integer.parseInt(ss[0]);
                    notificationTopic.replies = Integer.parseInt(ss[1].substring(5));
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
        }

        String dateString = second.getElementsByClass("snow").html();
        dateString = dateString.replace("ago", "");
        notificationTopic.url = dateString;

        Elements contentNodes = second.getElementsByClass("payload");
        notificationTopic.content = contentNodes.text();
        notificationTopic.contentRendered = ContentUtils.formatContent(contentNodes.html());

        if (rawContents.contains("里提到了你")) {
            notificationDescriptionBefore = " 在 ";
            notificationDescriptionAfter = " 里提到了你";
        }
        if (rawContents.contains("里回复了你")) {
            notificationDescriptionBefore = " 在 ";
            notificationDescriptionAfter = " 里回复了你";
        }
        if (rawContents.contains("时提到了你")) {
            notificationDescriptionBefore = " 在回复 ";
            notificationDescriptionAfter = " 时提到了你";
        }
        if (rawContents.contains("感谢了你发布的主题")) {
            notificationDescriptionBefore = " 感谢了你发布的主题 ";
            notificationDescriptionAfter = "";
        }
        if (rawContents.contains("感谢了你在主题")) {
            notificationDescriptionBefore = " 感谢了你在主题 ";
            notificationDescriptionAfter = " 里的回复";
        }
        if (rawContents.contains("收藏了你发布的主题")) {
            notificationDescriptionBefore = " 收藏了你发布的主题 ";
            notificationDescriptionAfter = "";
        }

        return true;
    }

}
