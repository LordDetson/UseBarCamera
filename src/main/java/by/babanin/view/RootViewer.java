package by.babanin.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class RootViewer extends JFrame implements Runnable {

    private final URLPanel urlPanel;
    private final WebcamListPanel webcamListPanel;

    public RootViewer(URLPanel urlPanel, WebcamListPanel webcamListPanel) {
        this.urlPanel = urlPanel;
        this.webcamListPanel = webcamListPanel;
    }

    @Override
    public void run() {
        setLayout(new BorderLayout(5, 5));

        add(urlPanel, BorderLayout.NORTH);
        add(new JScrollPane(webcamListPanel), BorderLayout.CENTER);

        setLocationRelativeTo(null);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
