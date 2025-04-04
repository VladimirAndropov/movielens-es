/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okmich.movielens.es;

import java.io.IOException;
// SearchController.java
import com.okmich.movielens.es.ui.AppFrame;

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
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.pack();
        view.setLocationRelativeTo(null); // Center on screen

        // Setup controller
        setupController();

        // Show the view
        view.setVisible(true);
    }

    private void setupController() {
        view.getButton1().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        view.getButton2().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                catIndices();
            }
        });
    }

    private void catIndices(){
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String[] indices = model.catIndices("ml-*");

                    if (indices.length < 1 ) {
                        publish("No results found\n");
                    } else {
                        for ( String index : indices) {
                            publish(index);
                        }
                    }
                } catch (IOException ex) {
                    publish("Error: " + ex.getMessage() + "\n");
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(chunk ->
//                        view.getTextArea2().append(chunk + "\n")
                        view.getComboBox2().addItem(chunk)
                );
            }
        }.execute();
    }


    private void performSearch() {
        String searchWord = view.getTextField1().getText().trim();
        String index = (String) view.getComboBox2().getSelectedItem();
        if (searchWord.isEmpty()) {
            return;
        }

        // Use SwingWorker for async operation
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Searching...\n");

                try {
                    SearchHits hits = model.getHits(index, "tag", searchWord);

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
            protected void process(List<String> chunks) {
                chunks.forEach(chunk ->
                        view.getTextArea1().append(chunk + "\n")
                );
            }
        }.execute();
    }
}
