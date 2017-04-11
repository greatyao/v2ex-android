package com.yaoyumeng.v2ex2.model;

import com.yaoyumeng.v2ex2.api.V2EXManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by yw on 2017/4/11.
 */
public class NodeListModel extends ArrayList<NodeModel> {

    private static final long serialVersionUID = 2017041112L;

    public void parse(String responseBody) throws Exception {
        Document doc = Jsoup.parse(responseBody);
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValue("class", "grid_item");
        for (Element el : elements) {
            NodeModel node = new NodeModel();

            node.title = el.text();
            String[] tmp = node.title.split(" ");
            if(tmp.length == 2)
                node.title = tmp[0];

            node.url = el.attr("href");
            tmp = node.url.split("/");
            if(tmp.length >= 1)
                node.name = tmp[tmp.length-1];
            else
                node.name = node.title;
            if (node.url.startsWith("//"))
                node.url = "http:" + node.url;
            else
                node.url = V2EXManager.getBaseUrl() + node.url;

            add(node);
        }
    }
}
