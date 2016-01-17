/* 
 * Copyright (c) 2015 RobotsByTheC. All rights reserved.
 *
 * Open Source Software - may be modified and shared by FRC teams. The code must
 * be accompanied by the BSD license file in the root directory of the project.
 */
package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

import org.opencv.core.Mat;
import org.usfirst.frc.team2084.CMonster2015.vision.ImageConvertor;
import org.usfirst.frc.team2084.CMonster2015.vision.OpenCVLoader;
import org.usfirst.frc.team2084.CMonster2015.vision.Range;
import org.usfirst.frc.team2084.CMonster2015.vision.VisionParameters;
import org.usfirst.frc.team2084.smartdashboard.extensions.vision.properties.RangeProperty;

import edu.wpi.first.smartdashboard.extensions.FileSniffer;
import edu.wpi.first.smartdashboard.gui.DisplayElement;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.DoubleProperty;
import edu.wpi.first.smartdashboard.properties.Property;

/**
 * @author Ben Wolsieffer
 */
@SuppressWarnings("serial")
public abstract class VisionExtension extends StaticWidget {

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
     * Image used to transfer data between the processing loop and the UI
     * drawing thread. It probably should be made more thread-safe but I haven't
     * had a problem and it doesn't matter that much if a few frames get
     * corrupted.
     */
    private BufferedImage imageToDraw;

    private final ImageConvertor convertor = new ImageConvertor();

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

        revalidate();
        repaint();
    }

    protected void displayImage(Mat image) {
        synchronized (this) {
            if (image != null) {
                imageToDraw = convertor.toBufferedImage(image);
            } else {
                imageToDraw = null;
            }
        }
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
        }
    }

    /**
     * Paints the image and the frame rate onto the SmartDashobard.
     *
     * @param g the {@link Graphics} object to paint onto
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // If the capture thread is connected and there is a image to draw, draw
        // it.
        synchronized (this) {
            if (imageToDraw != null) {
                int width = getBounds().width;
                int height = getBounds().height;
                // Scale the image to fit in the component and draw it.
                double scale = Math.min((double) width / (double) imageToDraw.getWidth(),
                        (double) height / (double) imageToDraw.getHeight());

                g2d.drawImage(imageToDraw,
                        (int) (width - (scale * imageToDraw.getWidth())) / 2,
                        (int) (height - (scale * imageToDraw.getHeight())) / 2,
                        (int) ((width + scale * imageToDraw.getWidth()) / 2),
                        (int) (height + scale * imageToDraw.getHeight()) / 2,
                        0, 0, imageToDraw.getWidth(), imageToDraw.getHeight(), null);
            } else {
                // If the camera is not connected, make the background pink and
                // say "NO CONNECTION"
                g2d.setColor(Color.PINK);
                g2d.fillRect(0, 0, getBounds().width, getBounds().height);
                g2d.setColor(Color.BLACK);
                g2d.drawString("NO CONNECTION", 10, 15);
            }
        }
    }

}
