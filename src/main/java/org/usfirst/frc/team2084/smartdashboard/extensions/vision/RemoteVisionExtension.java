package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import org.opencv.core.Mat;
import org.usfirst.frc.team2084.CMonster2015.vision.Range;
import org.usfirst.frc.team2084.CMonster2015.vision.VisionParameters;
import org.usfirst.frc.team2084.CMonster2015.vision.capture.CameraCapture;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;

@SuppressWarnings("serial")
public class RemoteVisionExtension extends VisionExtension {

    public static final String NAME = "Remote Vision";

    public final StringProperty viewerURL = new StringProperty(this, "Viewer URL", "http://localhost:8080");

    private CameraCapture capture;

    private volatile boolean running;

    private class CaptureThread implements Runnable {

        private Mat image = new Mat();

        @Override
        public void run() {
            while (running) {
                capture.capture(image);

                displayImage(image);
            }
            capture.stop();
        }
    }

    @Override
    public void init() {
        capture = new CameraCapture(viewerURL.getValue());
        capture.start();

        running = true;

        Thread captureThread = new Thread(new CaptureThread());
        captureThread.setDaemon(true);
        captureThread.start();

        super.init();
    }

    @Override
    public void disconnect() {
        running = false;
    }

    @Override
    public void propertyChanged(Property p) {
        super.propertyChanged(p);
        if (p == viewerURL) {
            setViewerURL(viewerURL.getValue());
        }
    }

    public void setViewerURL(String url) {
        capture.setFilename(url);
    }
}
