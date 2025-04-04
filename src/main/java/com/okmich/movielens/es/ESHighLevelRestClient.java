/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.movielens.es;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 *
 * @author michael.enudi
 */
public class ESHighLevelRestClient {

    private static final Logger LOG = Logger.getLogger(ESHighLevelRestClient.class.getName());
    private final RestHighLevelClient restHighLevelClient;

    //private TransportClient transportClient;
    public ESHighLevelRestClient() {
        LOG.info("trying to instantiate the high level client");
        this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        LOG.log(Level.INFO, "Done instantiating the high level client {0}", this.restHighLevelClient);
    }

    public void performBulkIndexRequest(String index, String type, String idKey,
            List<Map<String, Object>> data) throws IOException {
        BulkRequest request = new BulkRequest();
        String id;
        for (Map<String, Object> record : data) {
            if (idKey == null || idKey.isEmpty() || !record.containsKey(idKey)) {
                id = Long.toString(System.nanoTime());
            } else {
                id = record.get(idKey).toString();
            }
// pre 6.x api with type still included
//            request.add(new IndexRequest(index, type, id).
//                    source(record)
//            );
//
            request.add(new IndexRequest(index).id(id).
                    source(record)
            );
        }

        int indexCount = 0, updateCount = 0;

        BulkResponse bulkResponse = this.restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        //check for failures
        if (bulkResponse.hasFailures()) {
            LOG.log(Level.SEVERE, bulkResponse.buildFailureMessage());
        }

        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();

            if (itemResponse.getResult() == Result.CREATED) {
                indexCount++;
            }
            if (itemResponse.getResult() == Result.UPDATED) {
                updateCount++;
            }
        }

        if (indexCount > 0) {
            LOG.log(Level.INFO, "{0} documents created in {1}.", new Object[]{indexCount, index});
        }
        if (updateCount > 0) {
            LOG.log(Level.INFO, "{0} documents updated. in {1}.", new Object[]{updateCount, index});
        }
    }

    /**
     *
     * @param index
     * @param type
     * @param idKey
     * @param data
     * @throws IOException
     */
    public void performIndexRequest(String index, String type, String idKey,
            Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type, idKey).source(data);

        IndexResponse indexResponse = this.restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        Result result = indexResponse.getResult();

        if (result != Result.CREATED || result != Result.UPDATED) {
            throw new RuntimeException("Failed to index document " + data);
        }
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return this.restHighLevelClient;
    }

    public SearchHits getHits(String index, String name, String searchWord) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index); // Change to your index
//        searchRequest.source().query(QueryBuilders.matchQuery(name, searchWord));   // выбор поля поиска
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.multiMatchQuery(searchWord)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS));

        searchRequest.source(sourceBuilder);
        // Execute search
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // Display results
        if (response.getHits().getHits().length == 0) {
            LOG.log(Level.INFO, "No results found for: " + searchWord);
        }
        return  response.getHits();

    }

    public String[] catIndices(String indices) throws IOException {
        // 1. Create a request to get all indices
        GetIndexRequest request = new GetIndexRequest(indices);
        // 2. Execute the request
        GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        return  response.getIndices();

    }

    public void close() throws IOException {
        LOG.log(Level.INFO, "Closing the http client");
        this.restHighLevelClient.close();
    }

}
