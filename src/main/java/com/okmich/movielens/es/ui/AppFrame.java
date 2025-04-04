package com.okmich.movielens.es.ui;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppFrame {
    private final JFrame mainFrame;
    private JPanel panel1;
    private JButton button1;
    private JTextField textField1;
    private JTextArea textArea1;

    public AppFrame() {
        mainFrame = new JFrame("Elasticsearch MVC Search");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textField1 = new JTextField(20);
        button1 = new JButton("Search");
        textArea1 = new JTextArea(15, 40);
        textArea1.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(textField1);
        panel.add(button1);

        mainFrame.add(panel, BorderLayout.NORTH);
        mainFrame.add(new JScrollPane(textArea1), BorderLayout.CENTER);
        mainFrame.pack();
    }

    public void show() {
        mainFrame.setVisible(true);
    }

    // Getters for controller
    public JButton getButton1() {
        return button1;
    }

    public JTextField getTextField1() {
        return textField1;
    }

    public JTextArea gettextArea1() {
        return textArea1;
    }
}