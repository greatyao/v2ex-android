package com.yaoyumeng.v2ex2.model;

import com.yaoyumeng.v2ex2.utils.ContentUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by yw on 2015/6/6.
 */
public class NotificationListModel extends ArrayList<NotificationModel> {

    private static final long serialVersionUID = 2015050109L;

    public int totalPage = 1;
    public int currentPage = 1;

    public void parse(String responseBody) throws Exception {
        Document doc = Jsoup.parse(responseBody);
        Element body = doc.body();
        Elements elements = body.getElementsByAttributeValue("class", "cell");
        for (Element el : elements) {
            NotificationModel notification = new NotificationModel();
            if (notification.parse(el))
                add(notification);
        }

        int[] pages = ContentUtils.parsePage(body);
        currentPage = pages[0];
        totalPage = pages[1];
    }
}
