package com.elasticsearch.mysql.service.impl;

import com.elasticsearch.mysql.dao.EsProductDao;
import com.elasticsearch.mysql.entity.EsProduct;
import com.elasticsearch.mysql.entity.EsProducts;
import com.elasticsearch.mysql.repository.EsProductRepository;
import com.elasticsearch.mysql.service.EsProductService;
import com.google.common.collect.Lists;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/17 16:52
 */
@Service
public class EsProductServiceImpl implements EsProductService {

    @Autowired
    private EsProductDao esProductDao;

    @Autowired
    private EsProductRepository esProductRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public int importAll() {
        List<EsProduct> esProductList = esProductDao.getAllEsProductList(null);
        List<EsProducts> esProducts = Lists.newArrayList();
        for (EsProduct esProduct : esProductList) {
            EsProducts es = new EsProducts();
            es.setId(esProduct.getId());
            es.setName(esProduct.getName());
            es.setTitle(esProduct.getTitle());
            esProducts.add(es);
        }
        Iterable<EsProducts> esProductIterator = esProductRepository.saveAll(esProducts);
        Iterator<EsProducts> iterator = esProductIterator.iterator();
        int result = 0;
        while (iterator.hasNext()){
            result++;
            iterator.next();
        }
        return result;
    }

    @Override
    public Object findByName(String name) {
        return esProductRepository.findByName(name, null);
    }

    @Override
    public Object findByKey(String key, Integer page, Integer rows) {
        Pageable pageable = PageRequest.of(page, rows);
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        // 添加分页
        builder.withPageable(pageable);
        // 搜索
        if(StringUtils.isEmpty(key)){
            builder.withQuery(QueryBuilders.matchAllQuery());
        }else{
            List<FunctionScoreQueryBuilder.FilterFunctionBuilder> filterFunctionBuilders = Lists.newArrayList();
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("name", key).analyzer("ik_max_word"),
                    ScoreFunctionBuilders.weightFactorFunction(10)));
            filterFunctionBuilders.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.matchQuery("title", key).analyzer("ik_max_word"),
                    ScoreFunctionBuilders.weightFactorFunction(5)));
            FunctionScoreQueryBuilder.FilterFunctionBuilder[] builders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[filterFunctionBuilders.size()];
            filterFunctionBuilders.toArray(builders);
            FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(builders)
                    .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                    .setMinScore(5);
            builder.withQuery(functionScoreQueryBuilder);
        }

        builder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));

        NativeSearchQuery searchQuery = builder.build();

        SearchHits<EsProducts> searchHits = elasticsearchRestTemplate.search(searchQuery, EsProducts.class);

        List<EsProducts> searchProductList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(searchProductList, pageable, searchHits.getTotalHits());
    }
}
