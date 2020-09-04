package com.elasticsearch.mysql.dao;

import com.elasticsearch.mysql.entity.EsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:52
 */
@Repository
public interface EsProductDao extends JpaRepository<EsProduct, Long> {

    @Query(" select  a from EsProduct a ")
    List<EsProduct> getAllEsProductList(String o);
}
