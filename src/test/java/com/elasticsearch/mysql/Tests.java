package com.elasticsearch.mysql;

import com.elasticsearch.mysql.entity.CaiDan;
import com.elasticsearch.mysql.dao.CaiDanDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/27 18:26
 */
@SpringBootTest
public class Tests {
    String URL = "https://www.xiangha.com/caipu/c-jiachang/hot-3/";

    @Autowired
    private CaiDanDao caiDanRepository;

    @Test
    public void getXiangHa() throws Exception {
        Document document = Jsoup.connect(URL).get();

        Elements elements = document.getElementsByClass("s_list").select("li");
        int i = 34;
        for (Element element : elements) {
            CaiDan caiDan = new CaiDan();
            caiDan.setID(i++);
            caiDan.setCaiMing(element.getElementsByTag("a").text());
            caiDan.setAddress(element.getElementsByTag("a").attr("href"));
            caiDan.setCaiLiao(element.getElementsByTag("p").get(1).text());
            caiDan.setTuPianAddress(element.getElementsByTag("img").attr("src"));
            caiDan.setLiuLan(Integer.parseInt(element.getElementsByTag("p").get(2).getElementsByTag("span").text().substring(0, element.getElementsByTag("p").get(2).getElementsByTag("span").text().length() - 2)));
            caiDan.setSouCang(Integer.parseInt(element.getElementsByTag("p").get(2).text().substring(element.getElementsByTag("p").get(2).text().indexOf("浏览") + 2, element.getElementsByTag("p").get(2).text().length() - 2)));
            caiDanRepository.save(caiDan);
        }
    }
}
