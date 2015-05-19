package com.yaoyumeng.v2ex.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by yw on 2015/5/19.
 */
public class TopicListModel extends ArrayList<TopicModel>{

    private static final long serialVersionUID = 2015050107L;

    public void parse(String responseBody){
        Document doc = Jsoup.parse(responseBody);
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValue("class", "cell item");
        for(Element el : elements){
            try {
                TopicModel model = parseTopicModel(el);
                add(model);
            } catch (Exception e){
            }
        }
    }

    private TopicModel parseTopicModel(Element el) throws Exception{
        Elements tdNodes = el.getElementsByTag("td");
        TopicModel topic = new TopicModel();
        MemberModel member = new MemberModel();
        NodeModel node = new NodeModel();
        for (Element tdNode: tdNodes) {
            String content = tdNode.toString();
            if (content.indexOf("class=\"avatar\"") >= 0) {
                Elements userIdNode = tdNode.getElementsByTag("a");
                if (userIdNode != null) {
                    String idUrlString = userIdNode.attr("href");
                    member.username = idUrlString.replace("/member/","");
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
                    if (aNode.attr("class").equals("node")) {
                        String  nodeUrlString = aNode.attr("href");
                        node.name = nodeUrlString.replace("/go/", "");
                        node.title = aNode.text();
                    } else {
                        if (aNode.toString().indexOf("reply") >= 0) {
                            topic.title = aNode.text();
                            String topicIdString = aNode.attr("href");
                            String [] subArray = topicIdString.split("#");
                            topic.id = Integer.parseInt(subArray[0].replace("/t/", ""));
                            topic.replies = Integer.parseInt(subArray[1].replace("reply", ""));
                        }
                    }
                }

                Elements spanNodes = tdNode.getElementsByTag("span");
                for (Element spanNode : spanNodes) {
                    String ss = spanNode.toString();
                    if (ss.indexOf("最后回复") >= 0 || ss.indexOf("前") >= 0) {
                        String contentString = spanNode.text();
                        String [] components = contentString.split("  •  ");
                        String dateString;
                        if (components.length <=2 )  continue;
                        dateString = components[2];
                        long created = System.currentTimeMillis() / 1000;
                        String[] stringArray = dateString.split(" ");
                        if (stringArray.length > 1) {
                            String unitString = "";
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


}
