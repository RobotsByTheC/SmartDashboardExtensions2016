package org.usfirst.frc.team2084.smartdashboard.extensions.vision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import org.usfirst.frc.team2084.CMonster2016.vision.UDPVideoServer;
import org.usfirst.frc.team2084.CMonster2016.vision.VisionParameters;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;

/**
 *
 * @author Ben Wolsieffer
 */
@SuppressWarnings("serial")
public class UDPStreamViewerExtension extends StaticWidget {

    public static final String NAME = "UDP Stream Viewer";

    private double rotateAngleRad = 0;
    private long lastFPSCheck = 0;
    private int lastFPS = 0;
    private int fpsCounter = 0;

    private DatagramSocket socket;
    private final byte[] buffer = new byte[100000];

    public class BGThread extends Thread {

        boolean destroyed = false;

        public BGThread() {
            super("Camera Viewer Background");
        }

        long lastRepaint = 0;

        @Override
        public void run() {
            while (!destroyed) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    fpsCounter++;
                    long currTime = System.currentTimeMillis();
                    long elapsedTime = currTime - lastFPSCheck;
                    if (elapsedTime > 500) {
                        if(elapsedTime > 2000) {
                            updateIP();
                        }
                        lastFPSCheck = currTime;
                        lastFPS = fpsCounter * 2;
                        fpsCounter = 0;
                    }

                    lastRepaint = System.currentTimeMillis();
                    ByteArrayInputStream tmpStream = new ByteArrayInputStream(buffer, 0, packet.getLength());
                    imageToDraw = ImageIO.read(tmpStream);

                    imageUpdated(imageToDraw);

                    repaint();

                } catch (Exception e) {
                    imageToDraw = null;
                    repaint();
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }

    private BufferedImage imageToDraw;
    private BGThread bgThread = new BGThread();
    public final IntegerProperty rotateProperty = new IntegerProperty(this, "Degrees Rotation", 0);

    /**
     * 
     */
    public UDPStreamViewerExtension() {
        try {
            socket = new DatagramSocket(UDPVideoServer.PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void updateIP() {
        try {
            VisionParameters.setStreamIP(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
    }
    
    @Override
    public void init() {
        setPreferredSize(new Dimension(320, 240));
        propertyChanged(rotateProperty);
        bgThread.start();
        updateIP();
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == rotateProperty) {
            rotateAngleRad = Math.toRadians(rotateProperty.getValue());
        }

    }

    @Override
    public void disconnect() {
        bgThread.destroy();
        super.disconnect();
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage drawnImage = imageToDraw;

        if (drawnImage != null) {
            // cast the Graphics context into a Graphics2D
            Graphics2D g2d = (Graphics2D) g;

            // get the existing Graphics transform and copy it so that we can
            // perform scaling and rotation
            AffineTransform origXform = g2d.getTransform();
            AffineTransform newXform = (AffineTransform) (origXform.clone());

            // find the center of the original image
            int origImageWidth = drawnImage.getWidth();
            int origImageHeight = drawnImage.getHeight();
            int imageCenterX = origImageWidth / 2;
            int imageCenterY = origImageHeight / 2;

            // perform the desired scaling
            double panelWidth = getBounds().width;
            double panelHeight = getBounds().height;
            double panelCenterX = panelWidth / 2.0;
            double panelCenterY = panelHeight / 2.0;
            double rotatedImageWidth = origImageWidth * Math.abs(Math.cos(rotateAngleRad)) + origImageHeight * Math.abs(Math.sin(rotateAngleRad));
            double rotatedImageHeight = origImageWidth * Math.abs(Math.sin(rotateAngleRad)) + origImageHeight * Math.abs(Math.cos(rotateAngleRad));

            // compute scaling needed
            double scale = Math.min(panelWidth / rotatedImageWidth, panelHeight / rotatedImageHeight);

            // set the transform before drawing the image
            // 1 - translate the origin to the center of the panel
            // 2 - perform the desired rotation (rotation will be about origin)
            // 3 - perform the desired scaling (will scale centered about
            // origin)
            newXform.translate(panelCenterX, panelCenterY);
            newXform.rotate(rotateAngleRad);
            newXform.scale(scale, scale);
            g2d.setTransform(newXform);

            // draw image so that the center of the image is at the "origin";
            // the transform will take care of the rotation and scaling
            g2d.drawImage(drawnImage, -imageCenterX, -imageCenterY, null);

            // restore the original transform
            g2d.setTransform(origXform);

            g.setColor(Color.PINK);
            g.drawString("FPS: " + lastFPS, 10, 10);
        } else {
            g.setColor(Color.PINK);
            g.fillRect(0, 0, getBounds().width, getBounds().height);
            g.setColor(Color.BLACK);
            g.drawString("NO CONNECTION", 10, 10);
        }
    }

    public void imageUpdated(BufferedImage image) {
    }
}