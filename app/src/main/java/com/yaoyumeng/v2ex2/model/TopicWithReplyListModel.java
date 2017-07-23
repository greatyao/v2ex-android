package com.yaoyumeng.v2ex2.model;

import com.yaoyumeng.v2ex2.utils.ContentUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by yw on 2015/5/26.
 */
public class TopicWithReplyListModel extends V2EXModel {
    private static final long serialVersionUID = 2015050108L;

    public TopicModel topic;
    public ArrayList<ReplyModel> replies;
    public int totalPage;
    public int currentPage;

    public void parse(JSONObject jsonObject) throws JSONException {

    }

    public void parse(String responseBody, boolean parseTopic, int id) throws Exception {
        Document doc = Jsoup.parse(responseBody);
        Element body = doc.body();

        topic = new TopicModel();
        topic.id = id;

        if (parseTopic) {
            try {
                parseTopicModel(doc, body);
            } catch (Exception e) {
                android.util.Log.w("parse_topic", e.toString());
            }
        }

        replies = new ArrayList<ReplyModel>();
        Elements elements = body.getElementsByAttributeValueMatching("id", Pattern.compile("r_(.*)"));
        for (Element el : elements) {
            try {
                ReplyModel reply = parseReplyModel(el);
                replies.add(reply);
            } catch (Exception e) {
                android.util.Log.w("parse_reply", e.toString());
            }
        }

        int[] pages = ContentUtils.parsePage(body);
        currentPage = pages[0];
        totalPage = pages[1];
        android.util.Log.d("page", String.format("%d/%d", currentPage, totalPage));
    }

    ReplyModel parseReplyModel(Element element) {
        ReplyModel reply = new ReplyModel();
        reply.member = new MemberModel();

        Elements tdNodes = element.getElementsByTag("td");
        for (Element tdNode : tdNodes) {
            Elements avatars = tdNode.getElementsByClass("avatar");
            if (avatars.size() > 0) {
                Elements avatarNode = tdNode.getElementsByTag("img");
                if (avatarNode != null) {
                    String avatarString = avatarNode.attr("src");
                    if (avatarString.startsWith("//")) {
                        avatarString = "http:" + avatarString;
                    }
                    reply.member.avatar = avatarString;
                }
            }

            Elements replyElements = tdNode.getElementsByClass("reply_content");
            if(replyElements.size() > 0) {
                reply.content = replyElements.text();
                reply.contentRendered = ContentUtils.formatContent(replyElements.html());
            }

            Elements agos = tdNode.getElementsByClass("ago");
            if(agos.size() > 0) {
                reply.created = V2EXDateModel.toLong(agos.text());
            }

            Elements aNodes = tdNode.getElementsByTag("a");
            for (Element aNode : aNodes) {
                if (aNode.toString().contains("/member/")) {
                    reply.member.username = aNode.attr("href").replace("/member/", "");
                    break;
                }
            }
        }

        return reply;
    }

    void parseTopicModel(Document doc, Element body) throws Exception {
        String title = doc.title();
        if (title.endsWith("- V2EX"))
            title = title.substring(0, title.length() - 6).trim();
        topic.title = title;

        Elements header = body.getElementsByClass("header");
        if (header.size() == 0) throw new Exception("fail to parse topic");

        topic.member = new MemberModel();
        topic.node = new NodeModel();
        Elements aNodes = header.get(0).getElementsByTag("a");
        for (Element aNode : aNodes) {
            String content = aNode.toString();
            if (content.contains("/member/")) {
                String member = aNode.attr("href");
                member = member.replace("/member/", "");
                topic.member.username = member;

                Elements avatarNode = aNode.getElementsByTag("img");
                if (avatarNode != null &&
                        (topic.member.avatar == null || topic.member.avatar.isEmpty())) {
                    String avatar = avatarNode.attr("src");
                    if (avatar.startsWith("//"))
                        avatar = "http:" + avatar;
                    topic.member.avatar = avatar;
                }
            } else if (content.contains("/go/")) {
                String node = aNode.attr("href");
                topic.node.name = node.replace("/go/", "");
                topic.node.title = aNode.text();
            }
        }

        String dateString = header.get(0).getElementsByClass("gray").text();
        String[] components = dateString.split("·");
        if (components.length >= 2) {
            dateString = components[1].trim();
            topic.created = V2EXDateModel.toLong(dateString);
        }

        Elements hNodes = header.get(0).getElementsByTag("h1");
        if (hNodes != null) {
            topic.title = hNodes.text();
        }

        Elements contentNodes = body.getElementsByClass("topic_content");
        if (contentNodes != null && contentNodes.size() > 0) {
            topic.content = contentNodes.get(0).text();
            topic.contentRendered = ContentUtils.formatContent(contentNodes.get(0).html());
        } else {
            topic.content = topic.contentRendered = "";
        }

        Elements boxNodes = body.getElementsByClass("box");
        boolean got = false;
        for (Element boxNode : boxNodes) {
            if (got) break;
            Elements spanNodes = boxNode.getElementsByTag("span");
            if (spanNodes != null) {
                for (Element spanNode : spanNodes) {
                    String spanString = spanNode.text();
                    if (!spanString.contains("回复"))
                        continue;
                    String[] components2 = spanString.split("  \\|  ");
                    if (components2.length < 2) {
                        topic.replies = 0;
                    } else {
                        String replyCount = components2[0].replace("回复", "");
                        replyCount = replyCount.trim();
                        topic.replies = Integer.parseInt(replyCount);
                    }
                    got = true;
                    break;
                }
            }
        }
    }
}
