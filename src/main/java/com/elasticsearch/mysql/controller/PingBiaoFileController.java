package com.elasticsearch.mysql.controller;

import com.elasticsearch.mysql.dao.CaiDanDao;
import com.elasticsearch.mysql.entity.CaiDan;
import com.elasticsearch.mysql.entity.CaiDans;
import com.elasticsearch.mysql.entity.EsProduct;
import com.elasticsearch.mysql.entity.EsProducts;
import com.elasticsearch.mysql.repository.CaiDanRepository;
import com.elasticsearch.mysql.service.CaiDanService;
import com.elasticsearch.mysql.service.EsProductService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:39
 */
@RestController
@RequestMapping("/esProduct")
public class PingBiaoFileController {


    @Autowired
    private EsProductService esProductService;

    @Autowired
    private CaiDanRepository caiDanRepository;

    @Autowired
    private CaiDanDao caiDanDao;

    @Autowired
    private CaiDanService caiDanService;

    @RequestMapping(value = "/importAll", method = RequestMethod.GET)
    @ResponseBody
    public Object importAllList() {
        int count = esProductService.importAll();
        return count;
    }

    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    @ResponseBody
    public Object findByName(String name){
        return esProductService.findByName(name);
    }

    @RequestMapping(value = "/findByKey", method = RequestMethod.GET)
    @ResponseBody
    public Object findByKey(String key, Integer page, Integer rows){
        return esProductService.findByKey(key, page, rows);
    }

    @RequestMapping(value = "/importAlls", method = RequestMethod.GET)
    @ResponseBody
    public Object importAllLists() {
        List<CaiDan> list = caiDanDao.findAll();

        List<CaiDans> esProducts = Lists.newArrayList();
        for (CaiDan caiDan : list) {
            CaiDans es = new CaiDans();
            es.setId(caiDan.getID());
            es.setAddress(caiDan.getAddress());
            es.setCai_liao(caiDan.getCaiLiao());
            es.setCai_ming(caiDan.getCaiMing());
            es.setLiu_lan(caiDan.getLiuLan());
            es.setSou_cang(caiDan.getSouCang());
            es.setTu_pian_address(caiDan.getTuPianAddress());
            esProducts.add(es);
        }

        Iterable<CaiDans> count = caiDanRepository.saveAll(esProducts);
        return count;
    }

    @RequestMapping(value = "/findByKeys", method = RequestMethod.GET)
    @ResponseBody
    public Object findByKey(String key) throws IOException {
        return caiDanService.findByKey(key);
    }

    @RequestMapping(value = "/multiSearch", method = RequestMethod.GET)
    @ResponseBody
    public Object multiSearch(String key) throws IOException {
        return caiDanService.multiSearch(key);
    }

    @RequestMapping(value = "/searchTemplate", method = RequestMethod.GET)
    @ResponseBody
    public Object searchTemplate(String key) throws IOException {
        return caiDanService.searchTemplate(key);
    }

    @RequestMapping(value = "/searchTemplates", method = RequestMethod.GET)
    @ResponseBody
    public Object searchTemplates(String key) throws IOException {
        return caiDanService.searchTemplates(key);
    }

    @RequestMapping(value = "/explainRequest", method = RequestMethod.GET)
    @ResponseBody
    public Object explainRequest(String key) throws IOException {
        return caiDanService.explainRequest(key);
    }

    @RequestMapping(value = "/countRequest", method = RequestMethod.GET)
    @ResponseBody
    public Object countRequest(String key) throws IOException{
        return caiDanService.countRequest(key);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Object info() throws IOException {
        return caiDanService.info();
    }

    @RequestMapping(value = "/xPackInfoRequest", method = RequestMethod.GET)
    @ResponseBody
    public Object xPackInfoRequest() throws Exception{
        return caiDanService.xPackInfoRequest();
    }

    @RequestMapping(value = "/analyzeRequest", method = RequestMethod.GET)
    @ResponseBody
    public Object analyzeRequest() throws Exception{
        return caiDanService.analyzeRequest();
    }

    @RequestMapping(value = "/createIndexRequest", method = RequestMethod.GET)
    @ResponseBody
    public Object createIndexRequest(String index) throws Exception{
        return caiDanService.createIndexRequest(index);
    }
}
