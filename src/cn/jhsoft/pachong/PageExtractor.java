package cn.jhsoft.pachong;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by chen on 2017/7/20.
 */
public class PageExtractor {

    public static void main(String[] args) throws IOException {

        Connection connect = Jsoup.connect("http://v.youku.com/v_show/id_XMjkwMDE4NDQyNA==.html?spm=a2hww.20023042.m_223465.5~5~5~5~5~5~A");
        Document document = connect.get();
        Elements select = document.select("[id=sMain] [class=title]").select("span");
        for (Element e:select) {
            System.out.println(e.text());
        }

        //System.out.println(select.get(1));

    }

}
