package com.yaoyumeng.v2ex.model;

import com.yaoyumeng.v2ex.api.SafeHandler;
import com.yaoyumeng.v2ex.api.V2EXErrorType;

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

        elements = body.getElementsByAttributeValue("class", "inner");
        for (Element el : elements) {
            Elements tds = el.getElementsByTag("td");
            if (tds.size() != 3) continue;

            String pageString = el.getElementsByAttributeValue("align", "center").text();
            String[] arrayString = pageString.split("/");
            if (arrayString.length != 2) continue;

            try {
                totalPage = Integer.parseInt(arrayString[1]);
                currentPage = Integer.parseInt(arrayString[0]);
            } catch (Exception e) {
            }
            break;
        }
    }
}
