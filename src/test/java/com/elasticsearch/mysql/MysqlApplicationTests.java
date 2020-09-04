package com.elasticsearch.mysql;

import com.alibaba.fastjson.JSON;
import com.elasticsearch.mysql.entity.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
class MysqlApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Test
    public void testJsonIndex(){
        IndexRequest indexRequest = new IndexRequest("posts");
        indexRequest.id("1");
        String jsonString =  "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(indexRequest);
        System.out.println(indexResponse.status());
    }

    @Test
    public void testMapIndex() throws IOException {
        IndexRequest indexRequest = new IndexRequest("posts");

        Map<String, Object> map = Maps.newHashMap();
        map.put("user", "han");
        map.put("postDate", new Date());
        map.put("message", "this map to index");

        indexRequest.source(map);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexRequest);
        System.out.println(indexResponse.status());
    }

    @Test
    public void testXContentBuilderIndex() throws IOException{
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
        xContentBuilder.startObject();
        {
            xContentBuilder.field("user", "hans");
            xContentBuilder.field("postDate", new Date());
            xContentBuilder.field("message", "this XContentBuilder to index");
        }
        xContentBuilder.endObject();
        IndexRequest indexRequest = new IndexRequest("posts");
        indexRequest.id("2");
        indexRequest.source(xContentBuilder);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexRequest);
        System.out.println(indexResponse.status());
    }

    @Test
    public void testKeyPairsIndex() throws IOException{
        IndexRequest indexRequest = new IndexRequest("posts");
        indexRequest.id("3");
        indexRequest.source("user", "han",
                "postDate", new Date(),
                "message", " test Key Pairs to index ");
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexRequest);
        System.out.println(indexResponse.status());
    }

    @Test
    public void testYiBuKeyPairsIndex() throws IOException{
        IndexRequest indexRequest = new IndexRequest("posts");
        indexRequest.id("3");
        indexRequest.source("user", "han",
                "postDate", new Date(),
                "message", " test Key Pairs to index ");
        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                String index = indexResponse.getIndex();
                String id = indexResponse.getId();
                if(indexResponse.getResult() == DocWriteResponse.Result.CREATED){
                    System.out.println(indexResponse.getResult());
                }else if(indexResponse.getResult() == DocWriteResponse.Result.UPDATED){
                    System.out.println(indexResponse.getResult());
                }

                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if(shardInfo.getTotal() != shardInfo.getSuccessful()){
                    System.out.println(shardInfo.getTotal());
                }

                if(shardInfo.getFailed() > 0){
                    System.out.println(shardInfo.getFailed());
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        Cancellable indexResponses = client.indexAsync(indexRequest, RequestOptions.DEFAULT, listener);
        System.out.println(indexResponses);
    }

    @Test
    public void testBultIndex() throws IOException {
        List<User> list = Lists.newArrayList();
        list.add(new User("ceshi", 10));
        list.add(new User("ceshi1", 15));
        list.add(new User("ceshi2", 20));
        list.add(new User("ceshi3", 25));
        list.add(new User("ceshi0", 30));

        BulkRequest indexRequest = new BulkRequest();
        for (int i = 0; i < list.size(); i++) {
            indexRequest.add(new IndexRequest("postss").id("" + i)
                    .source(JSON.toJSONString(list.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = client.bulk(indexRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse);
        System.out.println(bulkResponse.status());
    }

    @Test
    public void testGet() throws Exception{
        GetRequest getRequest = new GetRequest("postss", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse);
        System.out.println(getResponse.getSource());
    }

    @Test
    public void testGetParam() throws Exception {
        String[] excludes = new String[]{"age"};
        String[] includes = new String[]{"name", "age"};
        GetRequest request = new GetRequest("postss" ,"1");
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(response.getSource());
        System.out.println(response.getField("name"));
    }

    @Test
    public void testBulkGet() throws Exception{
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("postss")
                .source(XContentType.JSON,"name", "ceshi"));

        BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);

        for (BulkItemResponse itemResponse : bulkResponse) {
            IndexResponse indexResponse = (IndexResponse)itemResponse.getResponse();
            System.out.println(indexResponse);
            System.out.println(indexResponse.status());
            System.out.println(indexResponse.getShardInfo());
        }
    }

    @Test
    public void testMultiGet() throws Exception{
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        multiGetRequest.add(new MultiGetRequest.Item("postss", "1"));
        multiGetRequest.add(new MultiGetRequest.Item("postss", "2"));

        MultiGetResponse response = client.mget(multiGetRequest, RequestOptions.DEFAULT);
        MultiGetItemResponse[] itemResponses = response.getResponses();

        for (MultiGetItemResponse itemRespons : itemResponses) {
            GetResponse indexResponse = itemRespons.getResponse();
            System.out.println(indexResponse);
        }
    }

    @Test
    public void test() throws Exception{
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices("postss");
        reindexRequest.setDestIndex("post");

        BulkByScrollResponse reindex = client.reindex(reindexRequest, RequestOptions.DEFAULT);
        System.out.println(reindex.getStatus());
    }

    @Test
    public void testUpdateByQueryRequest() throws Exception{
        DeleteByQueryRequest updateByQueryRequest = new DeleteByQueryRequest("postss");
        updateByQueryRequest.setQuery(new TermQueryBuilder("name", "ceshi"));
        BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(updateByQueryRequest, RequestOptions.DEFAULT);

        System.out.println(bulkByScrollResponse);
    }
}
