/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.movielens.es;

import org.elasticsearch.search.SearchHits;
import java.io.IOException;
import java.util.logging.Logger;


/**
 *
 * @author michael.enudi
 */
public class GetData implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(GetData.class.getName());

    private static final ESHighLevelRestClient highLevelRestClient = new ESHighLevelRestClient();

    public static SearchHits GetHits(String index, String name, String searchWord) throws IOException {
        LOG.info("Preparing to get data ....");
        LOG.info("Get data ....\n");
        return  highLevelRestClient.getHits(index, name, searchWord);
    }

    @Override
    public void close() throws Exception {
        highLevelRestClient.close();
    }
}
