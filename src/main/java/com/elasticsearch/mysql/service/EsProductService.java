package com.elasticsearch.mysql.service;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:51
 */
public interface EsProductService {
    int importAll();

    Object findByName(String name);

    Object findByKey(String key, Integer page, Integer rows);
}
