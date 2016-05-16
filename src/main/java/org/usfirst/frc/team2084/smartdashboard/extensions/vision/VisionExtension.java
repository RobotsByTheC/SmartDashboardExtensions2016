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
public abstract class VisionExtension extends UDPStreamViewerExtension {

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
    public final RangeProperty goalHThreshold =
            new RangeProperty(this, "Goal H Threshold", COLOR_RANGE, DEFAULT_GOAL_H_THRESHOLD);
    public final RangeProperty goalSThreshold =
            new RangeProperty(this, "Goal S Threshold", COLOR_RANGE, DEFAULT_GOAL_S_THRESHOLD);
    public final RangeProperty goalVThreshold =
            new RangeProperty(this, "Goal V Threshold", COLOR_RANGE, DEFAULT_GOAL_V_THRESHOLD);
    public final DoubleProperty goalMinArea =
            new DoubleProperty(this, "Goal Min Blob Area", DEFAULT_GOAL_MIN_BLOB_AREA);
    public final DoubleProperty goalMinAspectRatioScore =
            new DoubleProperty(this, "Goal Min Aspect Ratio Score", DEFAULT_GOAL_MIN_ASPECT_RATIO_SCORE);
    public final DoubleProperty goalMinRectangularityWidthScore =
            new DoubleProperty(this, "Goal Min Rect Width Score", DEFAULT_GOAL_MIN_RECTANGULARITY_WIDTH_SCORE);
    public final DoubleProperty goalMinRectangularityHeightScore =
            new DoubleProperty(this, "Goal Min Rect Height Score", DEFAULT_GOAL_MIN_RECTANGULARITY_HEIGHT_SCORE);
    public final DoubleProperty goalMaxDistance =
            new DoubleProperty(this, "Goal Max Distance", DEFAULT_GOAL_MAX_DISTANCE);
    public final DoubleProperty goalMinDistance =
            new DoubleProperty(this, "Goal Min Distance", DEFAULT_GOAL_MIN_DISTANCE);
    public final DoubleProperty goalApproxPolyEpsilon =
            new DoubleProperty(this, "Goal Approx Poly Epsilon", DEFAULT_GOAL_APPROX_POLY_EPSILON);
    public final IntegerProperty goalBlurSize = new IntegerProperty(this, "Goal Blur Size", DEFAULT_GOAL_BLUR_SIZE);
    public final DoubleProperty aimingExposure = new DoubleProperty(this, "Aiming Exposure", DEFAULT_AIMING_EXPOSURE);

    public final RangeProperty boulderHThreshold =
            new RangeProperty(this, "Boulder H Threshold", COLOR_RANGE, DEFAULT_GOAL_H_THRESHOLD);
    public final RangeProperty boulderSThreshold =
            new RangeProperty(this, "Boulder S Threshold", COLOR_RANGE, DEFAULT_GOAL_S_THRESHOLD);
    public final RangeProperty boulderVThreshold =
            new RangeProperty(this, "Boulder V Threshold", COLOR_RANGE, DEFAULT_GOAL_V_THRESHOLD);
    public final DoubleProperty boulderMinBlobArea =
            new DoubleProperty(this, "Boulder Min Blob Area", DEFAULT_GOAL_MIN_BLOB_AREA);

    public final DoubleProperty fovAngle = new DoubleProperty(this, "FOV Angle", Math.toDegrees(DEFAULT_FOV_ANGLE));
    public final IntegerProperty streamQuality = new IntegerProperty(this, "Stream Quality", DEFAULT_STREAM_QUALITY);

    /**
     * This method is called when the SmartDashboard is started or the extension
     * is added.
     */
    @Override
    public void init() {
        setPreferredSize(new Dimension(500, 500));

        propertyChanged(goalHThreshold);
        propertyChanged(goalSThreshold);
        propertyChanged(goalVThreshold);
        propertyChanged(goalMinArea);
        propertyChanged(goalMinAspectRatioScore);
        propertyChanged(goalMinRectangularityWidthScore);
        propertyChanged(goalMinRectangularityHeightScore);
        propertyChanged(goalMaxDistance);
        propertyChanged(goalMinDistance);
        propertyChanged(goalApproxPolyEpsilon);
        propertyChanged(goalBlurSize);
        propertyChanged(aimingExposure);

        propertyChanged(boulderHThreshold);
        propertyChanged(boulderSThreshold);
        propertyChanged(boulderVThreshold);
        propertyChanged(boulderMinBlobArea);

        propertyChanged(fovAngle);
        propertyChanged(streamQuality);

        super.init();

        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property p) {
        ////////////////////////////////////////// Goal parameters
        if (p == goalHThreshold) {
            setGoalHThreshold(goalHThreshold.getValue());
        } else if (p == goalSThreshold) {
            setGoalSThreshold(goalSThreshold.getValue());
        } else if (p == goalVThreshold) {
            setGoalVThreshold(goalVThreshold.getValue());
        } else if (p == goalMinArea) {
            setGoalMinBlobArea(goalMinArea.getValue());
        } else if (p == goalMinAspectRatioScore) {
            setGoalMinAspectRatioScore(goalMinAspectRatioScore.getValue());
        } else if (p == goalMinRectangularityWidthScore) {
            setGoalMinAspectRatioScore(goalMinRectangularityWidthScore.getValue());
        } else if (p == goalMinRectangularityHeightScore) {
            setGoalMinAspectRatioScore(goalMinRectangularityHeightScore.getValue());
        } else if (p == goalMaxDistance) {
            setGoalMaxDistance(goalMaxDistance.getValue());
        } else if (p == goalMinDistance) {
            setGoalMinDistance(goalMinDistance.getValue());
        } else if (p == goalApproxPolyEpsilon) {
            setApproxPolyEpsilon(goalApproxPolyEpsilon.getValue());
        } else if (p == goalBlurSize) {
            setGoalBlurSize(goalBlurSize.getValue());
        } else if (p == aimingExposure) {
            setAimingExposure(aimingExposure.getValue());
        }
        ////////////////////////////////////////// Boulder parameters
        else if (p == boulderHThreshold) {
            setBoulderHThreshold(boulderHThreshold.getValue());
        } else if (p == boulderSThreshold) {
            setBoulderSThreshold(boulderSThreshold.getValue());
        } else if (p == boulderVThreshold) {
            setBoulderVThreshold(boulderVThreshold.getValue());
        } else if (p == boulderMinBlobArea) {
            setBoulderMinBlobArea(boulderMinBlobArea.getValue());
        }
        ////////////////////////////////////////// General parameters
        else if (p == fovAngle) {
            setFOVAngleDegrees(fovAngle.getValue());
        } else if (p == streamQuality) {
            setStreamQuality(streamQuality.getValue());
        } else {
            super.propertyChanged(p);
        }
    }

}
