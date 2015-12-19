package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import org.usfirst.frc.team2084.CMonster2015.vision.BallProcessor;
import org.usfirst.frc.team2084.CMonster2015.vision.Range;
import org.usfirst.frc.team2084.CMonster2015.vision.capture.CameraCapture;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;

@SuppressWarnings("serial")
public class LocalVisionExtension extends VisionExtension {

    public static final String NAME = "Local Vision";
    
    public final StringProperty cameraURL = new StringProperty(this, "Camera URL", "http://192.168.0.90/mjpg/video.mjpg");

    private CameraCapture capture;
    private BallProcessor processor;

    @Override
    public void init() {
        capture = new CameraCapture(cameraURL.getValue());
        processor = new BallProcessor(capture);

        processor.addImageHandler((image) -> {
            displayImage(image);
        });
                
        processor.start();
        
        super.init();
    }
    
    /**
     * 
     */
    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        super.disconnect();
    }
    
    @Override
    public void propertyChanged(Property p) {
        super.propertyChanged(p);
        
        if (p == cameraURL) {
            setCameraURL(cameraURL.getValue());
        }
    }
    
    public void setCameraURL(String url) {
        capture.setFilename(url);
    }

    @Override
    public void setHThreshold(Range r) {
        processor.setHThreshold(r);
    }

    @Override
    public void setSThreshold(Range r) {
        processor.setSThreshold(r);
    }

    @Override
    public void setVThreshold(Range r) {
        processor.setVThreshold(r);
    }

    @Override
    public void setMinSize(double size) {
    }

}
