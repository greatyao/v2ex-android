package com.yaoyumeng.v2ex.model;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 话题列表类,网页解析得到
 * Created by yw on 2015/5/19.
 */
public class TopicListModel extends ArrayList<TopicModel> {

    private static final long serialVersionUID = 2015050107L;

    private int mPage = 1;
    private int mTotalPage = 1;

    public void parse(String responseBody) {
        Document doc = Jsoup.parse(responseBody);
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValue("class", "cell item");
        for (Element el : elements) {
            try {
                TopicModel model = parseTopicModel(el, true, null);
                add(model);
            } catch (Exception e) {
            }
        }
    }

    public void parseFromNodeEntry(String responseBody, String nodeName) {
        Document doc = Jsoup.parse(responseBody);
        String title = doc.title();
        title = title.replace("V2EX ›", "").trim();
        NodeModel node = new NodeModel();
        node.name = nodeName;
        node.title = title;

        Element body = doc.body();
        //Elements elements = body.getElementsByAttributeValue("id", "TopicsNode");
        //if(elements.size() != 1) return;
        Elements elements = body.getElementsByAttributeValueMatching("class", Pattern.compile("cell from_(.*)"));
        for (Element el : elements) {
            try {
                TopicModel topic = parseTopicModel(el, false, node);
                add(topic);
            } catch (Exception e) {
                Log.e("err", e.toString());
            }
        }

        elements = body.getElementsByAttributeValue("class", "inner");
        mPage = mTotalPage = 1;
        for (Element el : elements) {
            Elements tds = el.getElementsByTag("td");
            if (tds.size() != 3) continue;

            String pageString = el.getElementsByAttributeValue("align", "center").text();
            pageString = pageString.split(" · ")[0];
            String[] arrayString = pageString.split("/");
            if (arrayString.length != 2) continue;

            try {
                mTotalPage = Integer.parseInt(arrayString[1]);
                mPage = Integer.parseInt(arrayString[0]);
            } catch (Exception e) {
            }
            break;
        }

        Log.i("page", String.format("%d/%d", mPage, mTotalPage));
    }

    private TopicModel parseTopicModel(Element el, boolean parseNode, NodeModel node) throws Exception {
        Elements tdNodes = el.getElementsByTag("td");
        TopicModel topic = new TopicModel();
        MemberModel member = new MemberModel();
        if (parseNode) node = new NodeModel();
        for (Element tdNode : tdNodes) {
            String content = tdNode.toString();
            if (content.indexOf("class=\"avatar\"") >= 0) {
                Elements userIdNode = tdNode.getElementsByTag("a");
                if (userIdNode != null) {
                    String idUrlString = userIdNode.attr("href");
                    member.username = idUrlString.replace("/member/", "");
                }

                Elements avatarNode = tdNode.getElementsByTag("img");
                if (avatarNode != null) {
                    String avatarString = avatarNode.attr("src");
                    if (avatarString.startsWith("//")) {
                        avatarString = "http:" + avatarString;
                    }
                    member.avatar = avatarString;
                }
            } else if (content.indexOf("class=\"item_title\"") >= 0) {
                Elements aNodes = tdNode.getElementsByTag("a");
                for (Element aNode : aNodes) {
                    if (parseNode && aNode.attr("class").equals("node")) {
                        String nodeUrlString = aNode.attr("href");
                        node.name = nodeUrlString.replace("/go/", "");
                        node.title = aNode.text();
                    } else {
                        if (aNode.toString().indexOf("reply") >= 0) {
                            topic.title = aNode.text();
                            String topicIdString = aNode.attr("href");
                            String[] subArray = topicIdString.split("#");
                            topic.id = Integer.parseInt(subArray[0].replace("/t/", ""));
                            topic.replies = Integer.parseInt(subArray[1].replace("reply", ""));
                        }
                    }
                }

                Elements spanNodes = tdNode.getElementsByTag("span");
                for (Element spanNode : spanNodes) {
                    String contentString = spanNode.text();
                    if (contentString.indexOf("最后回复") >= 0
                            || contentString.indexOf("前") >= 0
                            || contentString.indexOf("  •  ") >= 0) {
                        String[] components = contentString.split("  •  ");
                        if (parseNode && components.length <= 2) continue;
                        String dateString = parseNode ? components[2] : components[1];
                        long created = System.currentTimeMillis() / 1000;
                        String[] stringArray = dateString.split(" ");
                        if (stringArray.length > 1) {
                            String unitString = "";
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date date = sdf.parse(dateString);
                                created = date.getTime() / 1000;
                                topic.created = created;
                                break;
                            } catch (Exception e){
                            }

                            int how = Integer.parseInt(stringArray[0]);
                            String subString = stringArray[1].substring(0, 1);
                            if (subString.equals("分")) {
                                unitString = "分钟前";
                                created -= 60 * how;
                            } else if (subString.equals("小")) {
                                unitString = "小时前";
                                created -= 3600 * how;
                            } else if (subString.equals("天")) {
                                created -= 24 * 3600 * how;
                                unitString = "天前";
                            }
                            dateString = String.format("%s%s", stringArray[0], unitString);
                        } else {
                            dateString = "刚刚";
                        }
                        topic.created = created;
                    }
                }
            }
        }

        topic.member = member;
        topic.node = node;

        return topic;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public int getTotalPage() {
        return mTotalPage;
    }
}
