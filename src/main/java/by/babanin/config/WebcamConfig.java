package by.babanin.config;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamCompositeDriver;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebcamConfig {

    @Bean
    public static WebcamCompositeDriver getWebcamCompositeDriver() {
        WebcamCompositeDriver webcamCompositeDriver = new WebcamCompositeDriver();
        webcamCompositeDriver.add(new WebcamDefaultDriver());
        webcamCompositeDriver.add(new IpCamDriver());
        return webcamCompositeDriver;
    }

    @Bean
    public static List<Webcam> getWebcams(WebcamCompositeDriver webcamCompositeDriver) {
        Webcam.setDriver(webcamCompositeDriver);
        return Webcam.getWebcams();
    }
}
