/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.movielens.es;

import java.io.IOException;
// SearchController.java
import com.okmich.movielens.es.ui.AppFrame;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class SearchController {
    private final AppFrame view;
    private final ESHighLevelRestClient model;

    public SearchController() {
        // Initialize MVC components
        this.view = new AppFrame();
        this.model = new ESHighLevelRestClient();
        // Setup controller
        setupController();
    }

    private void setupController() {
        view.getButton1().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String searchWord = view.getTextField1().getText().trim();
        if (searchWord.isEmpty()) {
            return;
        }

        // Use SwingWorker for async operation
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Searching...\n");

                try {
                    SearchHits hits = model.getHits("ml-tags", "tag", searchWord);

                    if (hits.getHits().length < 1 ) {
                        publish("No results found\n");
                    } else {
                        for ( SearchHit hit : hits) {
                        Map<String, Object> source = hit.getSourceAsMap();
                        String title = (String) source.get("title");
                        Number movieId = (Number) source.get("movieId");
                        publish(title);
                        }
                    }
                } catch (IOException ex) {
                    publish("Error: " + ex.getMessage() + "\n");
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                chunks.forEach(chunk ->
                        view.gettextArea1().append(chunk + "\n")
                );
            }
        }.execute();
    }

    public void showView() {
        view.show();
    }
}
