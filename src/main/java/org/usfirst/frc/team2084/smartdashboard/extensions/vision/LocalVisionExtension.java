package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;
import org.usfirst.frc.team2084.CMonster2016.vision.HighGoalProcessor;
import org.usfirst.frc.team2084.CMonster2016.vision.ImageConvertor;

@SuppressWarnings("serial")
public class LocalVisionExtension extends VisionExtension {

    public static final String NAME = "Local Vision";

    private HighGoalProcessor processor;

    private final Mat outImage = new Mat();
    private final ImageConvertor imageConvertor = new ImageConvertor();

    @Override
    public void init() {
        processor = new HighGoalProcessor(null);

        super.init();
    }

    /**
     * @param image
     */
    @Override
    public void imageUpdated(BufferedImage image) {
        Mat inputImage = imageConvertor.toMat(image);
        processor.process(inputImage);

        BufferedImage outImageJava = imageConvertor.toBufferedImage(outImage);
        outImageJava.copyData(image.getRaster());
    }

}
