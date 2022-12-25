package org.example.datsanta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Input extends JFrame implements ActionListener {
    static JTextArea textArea;
    static JFrame f;
    static JButton b;
    static JButton b1;

    Input() {
    }

    public static void run() {
        Input te = new Input();
        f = new JFrame("textfield");

        textArea = new JTextArea(
               GeneticRequest.workers.stream().collect(Collectors.joining("\n"))
        );
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        textArea.setLineWrap(false);
        textArea.setPreferredSize(new Dimension(800, 400));
        b = new JButton("submit");
        b.addActionListener(te);
        f.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        b.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        JPanel p = new JPanel();
        p.add(textArea);
        p.add(b);
        f.add(p);
        f.setSize(400, 400);
        f.pack();
        f.show();
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("submit")) {
            ArrayList<String> strings = new ArrayList<>();
            for (String line : textArea.getText().split("\n")) {
                line = line.strip();
                if (!line.isBlank()) {
                    strings.add(line);
                }
            }
            GeneticRequest.workers = strings;
            System.out.println("new workers: " + GeneticRequest.workers);
        }
        if (s.equals("saveall")) {

        }
    }
}