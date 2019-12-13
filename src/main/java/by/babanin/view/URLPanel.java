package by.babanin.view;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;

@Component
public class URLPanel extends JPanel {
    private JLabel webcamNameLabel;
    private JLabel urlLabel;
    private JTextField webcamNameField;
    private JTextField urlField;
    private JButton addUrlIpCameraBtn;

    public URLPanel(WebcamListPanel webcamListPanel) {
        webcamNameLabel = new JLabel("Camera name:");
        urlLabel = new JLabel("URL IP Camera:");
        webcamNameField = new JTextField(10);
        urlField = new JTextField(25);
        addUrlIpCameraBtn = new JButton("Add");

        addUrlIpCameraBtn.addActionListener(e -> {
            String webcamName = webcamNameField.getText();
            String url = urlField.getText();
            if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(webcamName)) {
                try {
                    IpCamDeviceRegistry.register(webcamName,
                            urlField.getText(), IpCamMode.PUSH);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                webcamListPanel.addPanel(new WebcamConfigPanel(Webcam.getWebcamByName(webcamName)));
                webcamListPanel.revalidate();
            }
            webcamNameField.setText("");
            urlField.setText("");
        });

        setLayout(new FlowLayout());

        add(webcamNameLabel);
        add(webcamNameField);
        add(urlLabel);
        add(urlField);
        add(addUrlIpCameraBtn);
    }
}
