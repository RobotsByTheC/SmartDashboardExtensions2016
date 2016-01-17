package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import org.usfirst.frc.team2084.CMonster2015.vision.Range;
import org.usfirst.frc.team2084.CMonster2015.vision.HighGoalProcessor;
import org.usfirst.frc.team2084.CMonster2015.vision.capture.CameraCapture;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;

@SuppressWarnings("serial")
public class LocalVisionExtension extends VisionExtension {

    public static final String NAME = "Local Vision";

    public final StringProperty cameraURL = new StringProperty(this, "Camera URL", "http://192.168.0.90/mjpg/video.mjpg");

    private CameraCapture capture;
    private HighGoalProcessor processor;

    @Override
    public void init() {
        capture = new CameraCapture(cameraURL.getValue());

        processor = new HighGoalProcessor(capture);

        processor.addImageHandler((image) -> {
            displayImage(image);
        });

        processor.start();

        super.init();
    }

    public void disconnect() {
        processor.stop();
    }

    @Override
    public void propertyChanged(Property p) {
        if (p == cameraURL) {
            setCameraURL(cameraURL.getValue());
        }
    }

    public void setCameraURL(String url) {
        capture.setFilename(url);
    }

}
