package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import org.opencv.core.Mat;
import org.usfirst.frc.team2084.CMonster2015.vision.Range;
import org.usfirst.frc.team2084.CMonster2015.vision.capture.CameraCapture;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

@SuppressWarnings("serial")
public class RemoteVisionExtension extends VisionExtension {

    public static final String NAME = "Remote Vision";
    
    public static final String H_MIN_KEY = "hMin";
    public static final String H_MAX_KEY = "hMax";

    public static final String S_MIN_KEY = "sMin";
    public static final String S_MAX_KEY = "sMax";

    public static final String V_MIN_KEY = "vMin";
    public static final String V_MAX_KEY = "vMax";

    public static final String MIN_SIZE_KEY = "minSize";

    public final StringProperty viewerURL = new StringProperty(this, "Viewer URL", "http://localhost:8080");

    private final ITable visionTable = NetworkTable.getTable("SmartDashboard").getSubTable(
            "Vision");

    private CameraCapture capture;
    
    private class CaptureThread implements Runnable {

        private Mat image = new Mat();

        @Override
        public void run() {
            while (true) {
                capture.capture(image);

                displayImage(image);
            }
        }
    }

    @Override
    public void init() {
        capture = new CameraCapture(viewerURL.getValue());
        capture.start();

        Thread captureThread = new Thread(new CaptureThread());
        captureThread.setDaemon(true);
        captureThread.start();
        
        super.init();
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

    @Override
    public void setHThreshold(Range r) {
        visionTable.putNumber(H_MIN_KEY, r.getMin());
        visionTable.putNumber(H_MAX_KEY, r.getMax());
    }

    @Override
    public void setSThreshold(Range r) {
        visionTable.putNumber(S_MIN_KEY, r.getMin());
        visionTable.putNumber(S_MAX_KEY, r.getMax());
    }

    @Override
    public void setVThreshold(Range r) {
        visionTable.putNumber(V_MIN_KEY, r.getMin());
        visionTable.putNumber(V_MAX_KEY, r.getMax());
    }

    @Override
    public void setMinSize(double size) {
        visionTable.putNumber(MIN_SIZE_KEY, size);
    }
}
