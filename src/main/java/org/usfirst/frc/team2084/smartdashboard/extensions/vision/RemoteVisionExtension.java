package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import org.usfirst.frc.team2084.CMonster2016.vision.VisionParameters;

import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;

@SuppressWarnings("serial")
public class RemoteVisionExtension extends VisionExtension {

    public static final String NAME = "Remote Vision";

    public final StringProperty cameraSource = new StringProperty(this, "Camera Source", "0");

    /**
     * 
     */
    @Override
    public void init() {
        propertyChanged(cameraSource);

        super.init();
    }

    /**
     * @param p
     */
    @Override
    public void propertyChanged(Property p) {
        if (p == cameraSource) {
            VisionParameters.setCameraSource(cameraSource.getValue());
        } else {
            super.propertyChanged(p);
        }
    }
}
