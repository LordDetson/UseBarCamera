package by.babanin.view;

import by.babanin.service.RecordType;
import by.babanin.service.RecordVideoService;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebcamConfigPanel extends JPanel {
    private WebcamPanel webcamPanel;
    private WebcamViewer viewer;
    private JLabel recordTypeLabel;
    private RecordType type;

    private JLabel webcamName;
    private JLabel sizeLabel;

    private ButtonGroup group;
    private Map<String, Dimension> sizeMap;

    private String path;
    private JLabel pathLabel;
    private JButton pathBtn;
    private JFileChooser pathField;

    private JComboBox<String> recordTypeBox;

    private JCheckBox mirroredCheck;
    private JCheckBox sizeCheck;
    private JCheckBox fpsCheck;
    private JCheckBox recordVideoMotion;
    private JCheckBox motionDetection;

    private JButton showBtn;

    private AtomicBoolean recording = new AtomicBoolean(false);
    private JButton recordVideoBtn;

    private void actionRecordMotion(ActionEvent e) {
        viewer.setRecordMotion(recordVideoMotion.isSelected());
        if (recordVideoMotion.isSelected())
            viewer.motionDetection(recordVideoMotion.isSelected());
        if (!motionDetection.isSelected()) {
            motionDetection.setSelected(true);
        }
    }

    private void actionMotionDetection(ActionEvent e) {
        if (!recordVideoMotion.isSelected() && !viewer.getRecording().get() && !recording.get()) {
            viewer.motionDetection(motionDetection.isSelected());
        } else {
            motionDetection.setSelected(true);
        }
    }

    private class WebcamPanelMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            recordVideoBtn.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            recordVideoBtn.setVisible(false);
        }
    }

    public WebcamConfigPanel(Webcam webcam) {
        webcamPanel = new WebcamPanel(webcam, false);

        createComponents(webcam.getViewSizes(), webcam.getViewSize());

        setLayout(new GridBagLayout());
        addComponents();

        createAndAddBtnRecordVideo(new WebcamPanelMouseListener());
    }

    private void createComponents(Dimension[] viewSizes, Dimension webcamSize) {
        webcamName = new JLabel(webcamPanel.getWebcam().getName());
        sizeLabel = new JLabel("View sizes:");

        sizeMap = new HashMap<>();
        List<JRadioButton> sizeBtn = new ArrayList<>();
        JRadioButton jRadioButton;
        String format;
        for (Dimension size : viewSizes) {
            format = String.format("%dx%d", size.width, size.height);
            sizeMap.put(format, size);
            jRadioButton = new JRadioButton(format);
            if (webcamSize.equals(size)) {
                webcamPanel.getWebcam().setViewSize(size);
                webcamPanel.setSize(size);
                jRadioButton.setSelected(true);
            }
            jRadioButton.addActionListener(this::actionChangeSize);
            sizeBtn.add(jRadioButton);
        }
        group = new ButtonGroup();
        sizeBtn.forEach(group::add);

        path = "C:\\Temp\\video";
        pathLabel = new JLabel(path);
        pathBtn = new JButton("Select");
        pathField = new JFileChooser(path);
        pathField.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        pathBtn.addActionListener(e -> {
            int ret = pathField.showDialog(null, "Select a directory");
            try {
                if (ret == JFileChooser.APPROVE_OPTION) {
                    path = pathField.getSelectedFile().getAbsolutePath();
                    pathLabel.setText(path);
                }
            } catch (Exception e1) {

            }
        });

        recordTypeLabel = new JLabel("Compression:");
        Vector<String> items = new Vector<>();
        for (RecordType t : RecordType.values()) items.add(t.name());
        recordTypeBox = new JComboBox<>(items);
        recordTypeBox.setSelectedItem(RecordType.H264.name());
        type = RecordType.H264;
        recordTypeBox.addActionListener(e -> {
            String item = (String)recordTypeBox.getSelectedItem();
            type = RecordType.valueOf(item);
        });

        mirroredCheck = new JCheckBox("Mirrored");
        mirroredCheck.setSelected(webcamPanel.isMirrored());
        mirroredCheck.addActionListener(this::actionMirrored);

        sizeCheck = new JCheckBox("SizeDisplayed");
        sizeCheck.setSelected(webcamPanel.isImageSizeDisplayed());
        sizeCheck.addActionListener(this::actionImageSizeDisplayed);

        fpsCheck = new JCheckBox("FPSDisplayed");
        fpsCheck.setSelected(webcamPanel.isFPSDisplayed());
        fpsCheck.addActionListener(this::actionFpsDisplayed);

        recordVideoMotion = new JCheckBox("Record by motion");
        recordVideoMotion.setSelected(false);
        recordVideoMotion.addActionListener(this::actionRecordMotion);

        motionDetection = new JCheckBox("Motion detection");
        motionDetection.setSelected(false);
        motionDetection.addActionListener(this::actionMotionDetection);

        showBtn = new JButton("Show");
        showBtn.addActionListener(this::actionShowCamera);
    }

    private void createAndAddBtnRecordVideo(MouseListener mouseListener) {
        recordVideoBtn = new JButton("Record");
        recordVideoBtn.setVisible(false);
        recordVideoBtn.addActionListener(this::actionRecordVideo);
        recordVideoBtn.addMouseListener(mouseListener);
        webcamPanel.add(recordVideoBtn);
        webcamPanel.addMouseListener(mouseListener);
    }

    private void addComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(webcamName, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(sizeLabel, c);

        int y = 2;
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = y;
        Enumeration<AbstractButton> elements = group.getElements();
        while (elements.hasMoreElements()) {
            c.gridy = y;
            add(elements.nextElement(), c);
            y++;
        }

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = ++y;
        add(pathLabel, c);
        c.gridx = 1;
        add(pathBtn, c);

        c.gridx = 0;
        c.gridy = ++y;
        add(recordTypeLabel, c);
        c.gridx = 1;
        add(recordTypeBox, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = ++y;
        c.gridwidth = 2;
        add(mirroredCheck, c);

        c.gridy = ++y;
        add(sizeCheck, c);

        c.gridy = ++y;
        add(fpsCheck, c);

        c.gridy = ++y;
        add(recordVideoMotion, c);

        c.gridy = ++y;
        add(motionDetection, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = ++y;
        c.weightx = 2;
        c.insets = new Insets(0, 0, 0, 5);
        add(showBtn, c);
    }

    private void actionChangeSize(ActionEvent e) {
        Dimension size = sizeMap.get(e.getActionCommand());
        webcamPanel.getWebcam().setViewSize(size);
        webcamPanel.setSize(size);
    }

    private void actionMirrored(ActionEvent e) {
        webcamPanel.setMirrored(mirroredCheck.isSelected());
    }

    private void actionImageSizeDisplayed(ActionEvent e) {
        webcamPanel.setImageSizeDisplayed(sizeCheck.isSelected());
    }

    private void actionFpsDisplayed(ActionEvent e) {
        webcamPanel.setFPSDisplayed(fpsCheck.isSelected());
    }

    private void actionShowCamera(ActionEvent e) {
        viewer = new WebcamViewer(webcamPanel, path, type);
        SwingUtilities.invokeLater(viewer);
    }

    private void actionRecordVideo(ActionEvent actionEvent) {
        RecordVideoService recorder;
        if (!recording.get()) {
            recording.set(true);
            recorder = new RecordVideoService(webcamPanel.getWebcam(), path, type, recording);
            recordVideoBtn.setText("Stop");
            new Thread(recorder::start).start();
        } else {
            recording.set(false);
            recordVideoBtn.setText("Record");
        }
    }


}
