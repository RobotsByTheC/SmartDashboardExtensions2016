/* 
 * Copyright (c) 2015 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import static org.usfirst.frc.team2084.CMonster2016.vision.VisionParameters.*;

import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.Field;

import org.usfirst.frc.team2084.CMonster2016.vision.OpenCVLoader;
import org.usfirst.frc.team2084.CMonster2016.vision.Range;
import org.usfirst.frc.team2084.smartdashboard.extensions.vision.properties.RangeProperty;

import edu.wpi.first.smartdashboard.extensions.FileSniffer;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
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
    public final RangeProperty hThreshold = new RangeProperty(this, "H Threshold", COLOR_RANGE, DEFAULT_H_THRESHOLD);
    public final RangeProperty sThreshold = new RangeProperty(this, "S Threshold", COLOR_RANGE, DEFAULT_S_THRESHOLD);
    public final RangeProperty vThreshold = new RangeProperty(this, "V Threshold", COLOR_RANGE, DEFAULT_V_THRESHOLD);
    public final DoubleProperty minArea = new DoubleProperty(this, "Min Blob Area", DEFAULT_MIN_BLOB_SIZE);
    public final DoubleProperty minAspectRatioScore =
            new DoubleProperty(this, "Min Aspect Ratio Score", DEFAULT_MIN_ASPECT_RATIO_SCORE);
    public final DoubleProperty minRectangularityWidthScore =
            new DoubleProperty(this, "Min Rect Width Score", DEFAULT_MIN_RECTANGULARITY_WIDTH_SCORE);
    public final DoubleProperty minRectangularityHeightScore =
            new DoubleProperty(this, "Min Rect Height Score", DEFAULT_MIN_RECTANGULARITY_HEIGHT_SCORE);
    public final DoubleProperty approxPolyEpsilon =
            new DoubleProperty(this, "Approx Poly Epsilon", DEFAULT_APPROX_POLY_EPSILON);
    public final IntegerProperty blurSize = new IntegerProperty(this, "Blur Size", DEFAULT_BLUR_SIZE);
    public final DoubleProperty fovAngle = new DoubleProperty(this, "FOV Angle", Math.toDegrees(DEFAULT_FOV_ANGLE));
    public final DoubleProperty exposure = new DoubleProperty(this, "Exposure", DEFAULT_EXPOSURE);
    public final IntegerProperty streamQuality = new IntegerProperty(this, "Stream Quality", DEFAULT_STREAM_QUALITY);

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
        propertyChanged(minAspectRatioScore);
        propertyChanged(minRectangularityWidthScore);
        propertyChanged(minRectangularityHeightScore);
        propertyChanged(approxPolyEpsilon);
        propertyChanged(blurSize);
        propertyChanged(fovAngle);
        propertyChanged(exposure);
        propertyChanged(streamQuality);

        super.init();

        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property p) {
        if (p == hThreshold) {
            setHThreshold(hThreshold.getValue());
        } else if (p == sThreshold) {
            setSThreshold(sThreshold.getValue());
        } else if (p == vThreshold) {
            setVThreshold(vThreshold.getValue());
        } else if (p == minArea) {
            setMinBlobArea(minArea.getValue());
        } else if (p == minAspectRatioScore) {
            setMinAspectRatioScore(minAspectRatioScore.getValue());
        } else if (p == minRectangularityWidthScore) {
            setMinAspectRatioScore(minRectangularityWidthScore.getValue());
        } else if (p == minRectangularityHeightScore) {
            setMinAspectRatioScore(minRectangularityHeightScore.getValue());
        } else if (p == approxPolyEpsilon) {
            setApproxPolyEpsilon(approxPolyEpsilon.getValue());
        } else if (p == blurSize) {
            setBlurSize(blurSize.getValue());
        } else if (p == fovAngle) {
            setFOVAngleDegrees(fovAngle.getValue());
        } else if (p == exposure) {
            setExposure(exposure.getValue());
        } else if (p == streamQuality) {
            setStreamQuality(streamQuality.getValue());
        } else {
            super.propertyChanged(p);
        }
    }

}
