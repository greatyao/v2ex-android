package com.yaoyumeng.v2ex2.utils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by yw on 2015/5/28.
 */
public class ContentUtils {

    public static String formatContent(String content){
        return content.replace("href=\"/member/", "href=\"http://www.v2ex.com/member/")
                .replace("href=\"/i/", "href=\"https://i.v2ex.co/")
                .replace("href=\"/t/", "href=\"http://www.v2ex.com/t/")
                .replace("href=\"/go/", "href=\"http://www.v2ex.com/go/");
    }

    public static int[] parsePage(Element body){
        int currentPage = 1, totalPage = 1;
        Elements elements = body.getElementsByClass("page_current");
        for (Element el : elements) {
            String text = el.text();
            try {
                currentPage = Integer.parseInt(text);
                break;
            } catch (Exception e) {
            }
        }

        elements = body.getElementsByClass("page_normal");
        totalPage = currentPage;
        for (Element el : elements) {
            String text = el.text();
            try {
                int page = Integer.parseInt(text);
                if(totalPage < page)
                   totalPage = page;
            } catch (Exception e) {
            }
        }
        return new int[]{currentPage, totalPage};
    }
}
