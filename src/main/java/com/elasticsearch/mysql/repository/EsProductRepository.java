package com.elasticsearch.mysql.repository;

import com.elasticsearch.mysql.entity.EsProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:53
 */
public interface EsProductRepository extends ElasticsearchRepository<EsProducts, Long> {

    Page findByName(String name, Pageable page);
}
