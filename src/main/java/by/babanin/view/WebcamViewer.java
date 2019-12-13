package by.babanin.view;

import by.babanin.service.RecordType;
import by.babanin.service.RecordVideoService;
import com.github.sarxos.webcam.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebcamViewer extends JFrame implements Runnable, WindowListener, WebcamMotionListener, WebcamPanel.Painter {

    private int INTERVAL = 100;
    private HashMap<Point, Integer> motionPoints = new HashMap<>();
    /**
     * Used to render the effect for the motion points.
     */
    private final Stroke STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);

    /**
     * The amount of time each point should be rendered for before being removed.
     */
    public int renderTime = 3;

    /**
     * The actual size of the rendered effect for each point.
     */
    public final int renderSize = 10;

    private WebcamPanel webcamPanel;
    private WebcamPanel.Painter painter;
    private WebcamMotionDetector detector;
    private boolean isRecordMotion;
    private AtomicBoolean recording;
    private String path;
    private RecordType type;

    public WebcamViewer(WebcamPanel webcamPanel, String path, RecordType type) throws HeadlessException {
        this.webcamPanel = webcamPanel;
        this.path = path;
        this.type = type;
        recording = new AtomicBoolean();
        recording.set(false);

        webcamPanel.setPainter(this);
        painter = webcamPanel.getDefaultPainter();
        createDetector(webcamPanel);
    }

    private void createDetector(WebcamPanel webcamPanel) {
        detector = new WebcamMotionDetector(webcamPanel.getWebcam());
        // Sets the max amount of motion points to 300 and the minimum range between them to 40
        detector.setMaxMotionPoints(300);
        detector.setPointRange(20);

        detector.setInterval(INTERVAL);
        detector.addMotionListener(this);
    }

    @Override
    public void run() {
        setTitle(webcamPanel.getWebcam().getName());

        addWindowListener(this);

        add(webcamPanel);

        Dimension size = webcamPanel.getSize();
        size.width += 18;
        size.height += 47;
        setSize(size);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        webcamPanel.start();
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        Webcam webcam = webcamPanel.getWebcam();
        webcam.close();
        detector.stop();
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("webcam viewer paused");
        webcamPanel.pause();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("webcam viewer resumed");
        webcamPanel.resume();
    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void motionDetected(WebcamMotionEvent wme) {
        for (Point p : wme.getPoints()) {
            motionPoints.put(p, 0);
        }
        RecordVideoService recorder;
        if (isRecordMotion && !recording.get()) {
            recording.set(true);
            recorder = new RecordVideoService(webcamPanel.getWebcam(), path, type, recording);
            recorder.setTimer(100);
            new Thread(recorder::start).start();
        }
    }

    @Override
    public void paintPanel(WebcamPanel panel, Graphics2D g2) {
        if (painter != null) {
            painter.paintPanel(panel, g2);
        }
    }

    @Override
    public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {
        if (painter != null) {
            painter.paintImage(panel, image, g2);
        }

        // Gets all the points and updates the amount of time they have been rendered for
        // And removes the ones that exceed the renderTime variable

        ArrayList<Point> rem = new ArrayList<>();

        for (Map.Entry<Point, Integer> ent : motionPoints.entrySet()) {
            Point p = ent.getKey();

            if (ent.getValue() != null) {
                int tt = ent.getValue();
                if (tt >= renderTime) {
                    rem.add(ent.getKey());

                } else {
                    int temp = ent.getValue() + 1;
                    motionPoints.put(p, temp);
                }

            }
        }

        for (Point p : rem) {
            motionPoints.remove(p);
        }

        // Gets all the remaining points after removing the exceeded ones and then renders the
        // current ones as a red square

        for (Map.Entry<Point, Integer> ent : motionPoints.entrySet()) {

            Point p = ent.getKey();
            int xx = p.x - (renderSize / 2), yy = p.y - (renderSize / 2);

            Rectangle bounds = new Rectangle(xx, yy, renderSize, renderSize);

            int dx = (int) (0.1 * bounds.width);
            int dy = (int) (0.2 * bounds.height);
            int x = bounds.x - dx;
            int y = bounds.y - dy;
            int w = bounds.width + 2 * dx;
            int h = bounds.height + dy;

            g2.setStroke(STROKE);
            g2.setColor(Color.RED);
            g2.drawRect(x, y, w, h);
        }
    }

    public void motionDetection(boolean isMotionDetection) {
        if (isMotionDetection) {
            detector.start();
        } else {
            detector.stop();
            webcamPanel.start();
            createDetector(webcamPanel);
        }
    }

    public AtomicBoolean getRecording() {
        return recording;
    }

    public boolean isRecordMotion() {
        return isRecordMotion;
    }

    public void setRecordMotion(boolean recordMotion) {
        isRecordMotion = recordMotion;
    }
}
