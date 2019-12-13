package by.babanin.view;

import com.github.sarxos.webcam.Webcam;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebcamListPanel extends JPanel {
    private List<WebcamConfigPanel> configPanels;
    private GridBagConstraints c;

    public WebcamListPanel(List<Webcam> webcams) {
        webcams.forEach(System.out::println);
        setLayout(new GridBagLayout());


        configPanels = new ArrayList<>();
        webcams.forEach(webcam -> configPanels.add(new WebcamConfigPanel(webcam)));

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(5, 5, 5, 5);
        for (int i = 0; i < configPanels.size(); i++, c.gridx++) {
            addPanel(configPanels.get(i));
        }
    }

    public void addPanel(WebcamConfigPanel webcamConfigPanel) {
        webcamConfigPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        add(webcamConfigPanel, c);
        if (c.gridx == 3) {
            c.gridx = 0;
            c.gridy++;
        }
    }
}
