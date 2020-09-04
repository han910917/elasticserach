package com.elasticsearch.mysql.service.impl;

import com.alibaba.fastjson.JSON;
import com.elasticsearch.mysql.entity.CaiDan;
import com.elasticsearch.mysql.entity.CaiDans;
import com.elasticsearch.mysql.service.CaiDanService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jdk.internal.org.objectweb.asm.tree.analysis.Analyzer;
import org.apache.lucene.search.Explanation;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilities;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.client.xpack.XPackInfoRequest;
import org.elasticsearch.client.xpack.XPackInfoResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author hgm
 * @Time 2020/8/28 9:19
 */
@Service
public class CaiDanServiceImpl implements CaiDanService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Object findByKey(String key) throws IOException {
        SearchRequest searchRequest = new SearchRequest("caidan");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("cai_liao", key).analyzer("ik_max_word"));

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field field = new HighlightBuilder.Field("cai_liao");
        highlightBuilder.field(field);
        searchSourceBuilder.highlighter(highlightBuilder);

        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(key).defaultField("cai_liao").analyzer("ik_max_word").defaultOperator(Operator.AND);
        boolQueryBuilder.should(queryStringQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        List<CaiDans> list = Lists.newArrayList();
        for (SearchHit hit : hits.getHits()) {
            // highlight为高亮显示的结果 但是由于获取的是Source里面的结果 所以显示的结果没有高亮显示
            CaiDans caiDans = JSON.parseObject(JSON.toJSONString(hit.getSourceAsMap()), CaiDans.class);

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("cai_liao");
            Text[] text = highlightField.fragments();

            caiDans.setCai_liao(text[0].string());
            list.add(caiDans);
        }
        return list;
    }

    @Override
    public Object multiSearch(String key) throws IOException {
        MultiSearchRequest multiSearchRequest = new MultiSearchRequest();

        // 第一个查询
        SearchRequest firstSearchRequest = new SearchRequest("caidan");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("cai_liao", key).analyzer("ik_smart"));
        firstSearchRequest.source(searchSourceBuilder);
        multiSearchRequest.add(firstSearchRequest);

        // 第二个查询
        SearchRequest secondSearchRequest = new SearchRequest("caidan");
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(QueryBuilders.matchQuery("cai_ming", "牛肉").analyzer("ik_smart"));
        secondSearchRequest.source(searchSourceBuilder1);
        multiSearchRequest.add(secondSearchRequest);

        MultiSearchResponse response = restHighLevelClient.msearch(multiSearchRequest, RequestOptions.DEFAULT);

        List<CaiDan> list = Lists.newArrayList();
        // 获取查询结果 指定几个查询 数组就为几
        MultiSearchResponse.Item[] responses = response.getResponses();
        for (MultiSearchResponse.Item item : responses) {
            SearchResponse itemResponse = item.getResponse();
            for (SearchHit hit : itemResponse.getHits().getHits()) {
                CaiDan caiDan = JSON.parseObject(JSON.toJSONString(hit.getSourceAsMap()), CaiDan.class);
                list.add(caiDan);
            }
        }
        return list;
    }

    @Override
    public Object searchTemplate(String key) throws IOException {
        SearchTemplateRequest searchTemplateRequest = new SearchTemplateRequest();
        SearchRequest searchRequest = new SearchRequest("caidan");
        searchTemplateRequest.setRequest(searchRequest);
        searchTemplateRequest.setScriptType(ScriptType.INLINE);

        searchTemplateRequest.setScript(
                "{" +
                "  \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                "  \"size\" : \"{{size}}\"" +
                "}");

        Map<String, Object> scriptParams = Maps.newHashMap();
        scriptParams.put("field", "cai_ming");
        scriptParams.put("value", key);
        scriptParams.put("size", 5);

        searchTemplateRequest.setScriptParams(scriptParams);

        SearchTemplateResponse searchTemplateResponse = restHighLevelClient.searchTemplate(searchTemplateRequest, RequestOptions.DEFAULT);

        SearchResponse response = searchTemplateResponse.getResponse();
        List<CaiDan> list = Lists.newArrayList();
        for (SearchHit hit : response.getHits().getHits()) {
            CaiDan caiDan = JSON.parseObject(JSON.toJSONString(hit.getSourceAsMap()), CaiDan.class);
            list.add(caiDan);
        }

        return list;
    }

    @Override
    public Object searchTemplates(String key) throws IOException {
        Request scriptRequest = new Request("POST", "_scripts/title_search");
        scriptRequest.setJsonEntity(
            "{" +
                    "  \"script\": {" +
                    "    \"lang\": \"mustache\"," +
                    "    \"source\": {" +
                    "      \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                    "      \"size\" : \"{{size}}\"" +
                    "    }" +
                    "  }" +
                    "}");

        FieldCapabilitiesRequest request = new FieldCapabilitiesRequest();
        request.fields("查询字段").indices("index1", "index2"); // indices可多个索引
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        FieldCapabilitiesResponse fieldCapabilitiesResponse = restHighLevelClient.fieldCaps(request, RequestOptions.DEFAULT);
        Map<String, FieldCapabilities> field = fieldCapabilitiesResponse.getField("查询字段");
        FieldCapabilities keyword = field.get("keyword");
        return null;
    }

    @Override
    public Object explainRequest(String key) throws IOException {
        ExplainRequest explainRequest = new ExplainRequest("caidan", "1");
        explainRequest.query(QueryBuilders.matchQuery("cai_liao", key));
        ExplainResponse response = restHighLevelClient.explain(explainRequest, RequestOptions.DEFAULT);

        String index = response.getIndex();
        String id = response.getId();
        boolean exists = response.isExists();
        boolean match = response.isMatch();
        boolean hasExplanation = response.hasExplanation();
        Explanation explanation = response.getExplanation();
        GetResult getResult = response.getGetResult();
        System.out.println(index + "  " + id + "  " + exists + "  " + match + "  " + hasExplanation);
        System.out.println(explanation);
        System.out.println(getResult);
        return response;
    }

    @Override
    public Object countRequest(String key) throws IOException {
        CountRequest countRequest = new CountRequest("caidan");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        countRequest.source(searchSourceBuilder);
        CountResponse response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        System.out.println(response);
        return response;
    }

    @Override
    public Object info() throws IOException {
        MainResponse response = restHighLevelClient.info(RequestOptions.DEFAULT);
        MainResponse.Version version = response.getVersion();
        System.out.println(version.getNumber());
        System.out.println(version.getBuildFlavor());
        System.out.println(version.getBuildHash());
        System.out.println(version.getBuildDate());
        System.out.println(version.getLuceneVersion());
        System.out.println(version.getMinimumIndexCompatibilityVersion());
        System.out.println(version.getMinimumWireCompatibilityVersion());
        System.out.println(response.getNodeName());
        System.out.println(response.getClusterName());
        System.out.println(response.getClusterUuid());
        System.out.println(response.getTagline());
        return response;
    }

    @Override
    public Object xPackInfoRequest() throws IOException {
        XPackInfoRequest request = new XPackInfoRequest();
        request.setVerbose(true);
        request.setCategories(EnumSet.of(XPackInfoRequest.Category.BUILD,
                XPackInfoRequest.Category.LICENSE,
                XPackInfoRequest.Category.FEATURES));
        XPackInfoResponse response = restHighLevelClient.xpack().info(request, RequestOptions.DEFAULT);
        XPackInfoResponse.BuildInfo buildInfo = response.getBuildInfo();
        XPackInfoResponse.FeatureSetsInfo featureSetsInfo = response.getFeatureSetsInfo();
        XPackInfoResponse.LicenseInfo licenseInfo = response.getLicenseInfo();
        System.out.println(buildInfo);
        System.out.println(featureSetsInfo);
        System.out.println(licenseInfo);
        return buildInfo;
    }

    @Override
    public Object analyzeRequest() throws IOException {
//        AnalyzeRequest analyzeRequest = AnalyzeRequest.withGlobalAnalyzer( "ik_max_word","河北工大科雅能源科技股份有限公司");
//        AnalyzeResponse response = restHighLevelClient.indices().analyze(analyzeRequest, RequestOptions.DEFAULT);
//        return response;

        Map<String, Object> stopFilter = Maps.newHashMap();
        stopFilter.put("type", "stop");
        stopFilter.put("stopwords", new String[]{"科雅"});
        AnalyzeRequest request = AnalyzeRequest.buildCustomAnalyzer("ik_max_word")
                .addCharFilter("html_strip")
                .addTokenFilter("lowercase")
                .addTokenFilter(stopFilter)
                .build("河北工大科雅能源科技股份有限公司");
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        return response;
    }

    @Override
    public Object createIndexRequest(String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder()
        .put("index.number_of_shards", 3)
        .put("index.number_of_replicas", 3));
        return restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }
}