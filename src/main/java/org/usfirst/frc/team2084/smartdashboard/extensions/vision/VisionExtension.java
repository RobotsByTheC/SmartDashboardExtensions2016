/* 
 * Copyright (c) 2015 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.Field;

import org.usfirst.frc.team2084.CMonster2015.vision.OpenCVLoader;
import org.usfirst.frc.team2084.CMonster2015.vision.Range;
import org.usfirst.frc.team2084.CMonster2015.vision.VisionParameters;
import org.usfirst.frc.team2084.smartdashboard.extensions.vision.properties.RangeProperty;

import edu.wpi.first.smartdashboard.extensions.FileSniffer;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.Property;

/**
 * @author Ben Wolsieffer
 */
@SuppressWarnings("serial")
public abstract class VisionExtension extends MJPEGStreamViewerExtension {

    static {
        try {
            Field libraryDirsField = FileSniffer.class.getDeclaredField("LIBRARY_DIRS");
            libraryDirsField.setAccessible(true);

            OpenCVLoader.loadOpenCV((File[]) libraryDirsField.get(null));
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | UnsatisfiedLinkError e) {
            System.err.println("Unable to load OpenCV: " + e);
        }
    }

    /**
     * The range of values (0-255) that a color property can hold.
     */
    public static final Range COLOR_RANGE = new Range(0, 255);

    // Various properties that appear in the properties editor of the extension.
    public final RangeProperty hThreshold = new RangeProperty(this, "H Threshold", COLOR_RANGE);
    public final RangeProperty sThreshold = new RangeProperty(this, "S Threshold", COLOR_RANGE);
    public final RangeProperty vThreshold = new RangeProperty(this, "V Threshold", COLOR_RANGE);
    public final DoubleProperty minArea = new DoubleProperty(this, "Min Blob Area", 100);
    public final DoubleProperty approxPolyEpsilon = new DoubleProperty(this, "Approx Poly Epsilon", 10);

    /**
     * This method is called when the SmartDashboard is started or the extension
     * is added.
     */
    @Override
    public void init() {
        setPreferredSize(new Dimension(500, 500));

        propertyChanged(hThreshold);
        propertyChanged(sThreshold);
        propertyChanged(vThreshold);
        propertyChanged(minArea);
        propertyChanged(approxPolyEpsilon);

        super.init();
        
        revalidate();
        repaint();
    }

    public void propertyChanged(Property p) {
        if (p == hThreshold) {
            VisionParameters.setHThreshold(hThreshold.getValue());
        } else if (p == sThreshold) {
            VisionParameters.setSThreshold(sThreshold.getValue());
        } else if (p == vThreshold) {
            VisionParameters.setVThreshold(vThreshold.getValue());
        } else if (p == minArea) {
            VisionParameters.setMinBlobArea(minArea.getValue());
        } else if (p == approxPolyEpsilon) {
            VisionParameters.setApproxPolyEpsilon(approxPolyEpsilon.getValue());
        } else {
            super.propertyChanged(p);
        }
    }

}
