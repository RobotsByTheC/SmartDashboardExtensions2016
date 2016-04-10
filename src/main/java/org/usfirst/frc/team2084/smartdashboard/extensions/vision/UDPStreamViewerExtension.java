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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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

    private final int RECEIVE_TIMEOUT = 1000;

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
                    try {
                        socket.receive(packet);
                    } catch (SocketTimeoutException t) {
                        updateIP();
                    }

                    fpsCounter++;
                    long currTime = System.currentTimeMillis();
                    long elapsedTime = currTime - lastFPSCheck;
                    if (elapsedTime > 500) {
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
            VisionParameters.setStreamIP(getLocalHostLANAddress().getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println(e);
        }
    }

    @Override
    public void init() {
        setPreferredSize(new Dimension(320, 240));
        propertyChanged(rotateProperty);
        try {
            socket.setSoTimeout(RECEIVE_TIMEOUT);
        } catch (SocketException e) {
            System.err.println("Could not set socket timeout: " + e);
        }
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
            double rotatedImageWidth = origImageWidth * Math.abs(Math.cos(rotateAngleRad))
                    + origImageHeight * Math.abs(Math.sin(rotateAngleRad));
            double rotatedImageHeight = origImageWidth * Math.abs(Math.sin(rotateAngleRad))
                    + origImageHeight * Math.abs(Math.cos(rotateAngleRad));

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

    /**
     * Returns an <code>InetAddress</code> object encapsulating what is most
     * likely the machine's LAN IP address.
     * <p/>
     * This method is intended for use as a replacement of JDK method
     * <code>InetAddress.getLocalHost</code>, because that method is ambiguous
     * on Linux systems. Linux systems enumerate the loopback network interface
     * the same way as regular LAN network interfaces, but the JDK
     * <code>InetAddress.getLocalHost</code> method does not specify the
     * algorithm used to select the address returned under such circumstances,
     * and will often return the loopback address, which is not valid for
     * network communication. Details
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here
     * </a>.
     * <p/>
     * This method will scan all IP addresses on all network interfaces on the
     * host machine to determine the IP address most likely to be the machine's
     * LAN address. If the machine has multiple IP addresses, this method will
     * prefer a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually
     * IPv4) if the machine has one (and will return the first site-local
     * address if the machine has more than one), but if the machine does not
     * hold a site-local address, this method will return simply the first
     * non-loopback address found (IPv4 or IPv6).
     * <p/>
     * If this method cannot find a non-loopback address using this selection
     * algorithm, it will fall back to calling and returning the result of JDK
     * method <code>InetAddress.getLocalHost</code>.
     * <p/>
     *
     * @throws UnknownHostException If the LAN address of the machine cannot be
     *         found.
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                    .hasMoreElements();) {
                NetworkInterface iface = ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it
                            // immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily
                            // site-local.
                            // Store it as a candidate to be returned if
                            // site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback
                            // non-site-local addresses as candidates,
                            // only the first. For subsequent iterations,
                            // candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other
                // non-loopback address.
                // Server might have a non-site-local address assigned to its
                // NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost()
            // returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException =
                    new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}