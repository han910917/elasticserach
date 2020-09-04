package com.elasticsearch.mysql.repository;

import com.elasticsearch.mysql.entity.CaiDans;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:53
 */
public interface CaiDanRepository extends ElasticsearchRepository<CaiDans, Long> {
}
