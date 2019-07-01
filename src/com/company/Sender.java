package com.company;

import com.github.romankh3.image.comparison.ImageComparison;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Sender implements Runnable {

    private Server server;
    private Socket socket;
    private DataOutputStream dos;

    private BufferedImage capture;

    File outputfile;

    public Sender(Server server) {
        this.server = server;
        this.socket = server.client;
    }

    @Override
    public void run() {
        try {
            dos = new DataOutputStream(socket.getOutputStream());

            while (!socket.isClosed()) {
                ByteArrayOutputStream bScrn = new ByteArrayOutputStream();

                System.out.println("Sending...");
                capture = server.bot.getScreenCap();
                if (capture != null) {
//                    if (outputfile == null) {
//                        outputfile = new File("image.jpg");
//                        ImageIO.write(capture, "jpg", outputfile);
//                    } else {
//                        File file1 = new File("image2.jpg");
//                        ImageIO.write(capture, "jpg", file1);
//
//                        BufferedImage oldImage = ImageIO.read(Files.newInputStream(Paths.get("image.jpg")));
//                        File result = new File("difference.jpg");
//                        BufferedImage drawnDifferences = new ImageComparison( oldImage, capture, result )
//                                .compareImages()
//                                .getResult();

//                        BufferedImage difference = getDifferenceImage(oldImage, capture);
//                        File file = new File("difference.jpg");
//                        ImageIO.write(difference, "jpg", file);
//                    }
                    ImageIO.write(capture, "jpg", bScrn);
                    bScrn.close();
                    System.out.println("Screen captured");

                    byte[] imgBytes = bScrn.toByteArray();

                    try {
                        if (dos != null) {
                            dos.writeInt(imgBytes.length);
                            dos.write(imgBytes, 0, imgBytes.length);
                            dos.flush();
                        }
                        System.out.println("Sent to client");

                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("Connection closed by peer");
                        System.exit(-1);
                    }
                }
            }
        } catch (IOException e) {
            server.setListenerMessage("Communication is broken");
            server.setConnectButtonEnabled(true);
            server.lThread.interrupt();
            server.stopServer();
            Thread.currentThread().interrupt();
        }
    }

    public static BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2) {
        // convert images to pixel arrays...
        final int w = img1.getWidth(),
                h = img1.getHeight(),
                highlight = Color.MAGENTA.getRGB();
        final int[] p1 = img1.getRGB(0, 0, w, h, null, 0, w);
        final int[] p2 = img2.getRGB(0, 0, w, h, null, 0, w);
        // compare img1 to img2, pixel by pixel. If different, highlight img1's pixel...
        for (int i = 0; i < p1.length; i++) {
            if (p1[i] != p2[i]) {
                p1[i] = highlight;
            }
        }
        // save img1's pixels to a new BufferedImage, and return it...
        // (May require TYPE_INT_ARGB)
        final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        out.setRGB(0, 0, w, h, p1, 0, w);
        return out;
    }
}
