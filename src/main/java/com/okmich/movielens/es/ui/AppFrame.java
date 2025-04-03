package com.okmich.movielens.es.ui;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import static com.okmich.movielens.es.GetData.*;

public class AppFrame extends JFrame {
    private JPanel panel;
    private JButton button1;
    private JTextField textField1;
    private JTextArea textArea1;


    public AppFrame() {
        add(panel);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchWord = textField1.getText().trim();
                try {
                    StringBuilder results = new StringBuilder();
                    SearchHits hits = GetHits("ml-tags","tag",searchWord);
                    results.append(hits);

                    SearchHit firstHit = hits.getAt(1);
                    results.append(firstHit);
//получим объект
                    for (SearchHit hit : hits) {
                        results.append(hit.getSourceAsString()).append("\n\n");
                    }
//выводим как json
                    for (SearchHit hit : hits) {
                        Map<String, Object> source = hit.getSourceAsMap();
                        String title = (String) source.get("title");
                        Number movieId = (Number) source.get("movieId");
                        results.append(title).append(" - ").append(movieId).append("\n\n");
                    }
//выводим отдельным полем

                    textArea1.setText(results.toString());
                    } catch (Exception ex) {
                    textArea1.setText("Error searching: " + ex.getMessage());
                    }
                }
        });
    }
}
