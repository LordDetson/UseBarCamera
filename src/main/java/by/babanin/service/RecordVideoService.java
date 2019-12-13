package by.babanin.service;

import com.github.sarxos.webcam.Webcam;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordVideoService {
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
    private AtomicBoolean recording;
    private Webcam webcam;
    private RecordType type;
    private String path;
    private int timer;
    private int i;

    public RecordVideoService(Webcam webcam, String path, RecordType type, AtomicBoolean recording) {
        this.webcam = webcam;
        this.path = path;
        this.recording = recording;
        this.type = type;
    }

    public void start() {
        LocalDateTime now = LocalDateTime.now();
        File saveVideo = new File(path, String.format("%s - %s.%s", now.format(format),
                webcam.getName(), type.getFormat()));

        IMediaWriter writer = ToolFactory.makeWriter(saveVideo.getAbsolutePath());
        Dimension size = webcam.getViewSize();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
        long start = System.currentTimeMillis();

        if (timer != 0) {
            for (i = 0; i < timer; i++) {
                System.out.println(i);
                recording(writer, start);
            }
            recording.set(false);
        } else {
            for (i = 0; recording.get(); i++) {
                recording(writer, start);
            }
        }

        writer.close();
        System.out.println("Video recorded to the file: " + saveVideo.getAbsolutePath());
    }

    private void recording(IMediaWriter writer, long start) {
        int time = 1000;
        if (type == RecordType.MJPEG) time = 100;
        BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
        IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

        IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * time);
        frame.setKeyFrame(i == 0);
        frame.setQuality(100);

        writer.encodeVideo(0, frame);
        if (type != RecordType.MJPEG) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
