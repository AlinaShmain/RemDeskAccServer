package com.company;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageSender implements Runnable {

    private Socket socket;
    private OutputStream out;

    File outputfile;

    private BufferedImage capture;

    public ImageSender(Socket socket) {
        this.socket = socket;
    }

    public void setImage(BufferedImage image)
    {
        capture = image;
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            ByteArrayOutputStream bScrn = new ByteArrayOutputStream();

            System.out.println("Sending...");
            try {
                if(capture != null) {

                    if (outputfile == null) {
                        outputfile = new File("image.jpg");
                        ImageIO.write(capture, "jpg", outputfile);
                    } else {
                        BufferedImage oldImage = ImageIO.read(Files.newInputStream(Paths.get("image.jpg")));
                        System.out.println(oldImage.getHeight());
                        BufferedImage difference = getDifferenceImage(oldImage, capture);
                        System.out.println(difference.getHeight());
                        File file = new File("difference.jpg");
                        ImageIO.write(difference, "jpg", file);
                    }

                    ImageIO.write(capture, "jpg", bScrn);
                    bScrn.close();
                    System.out.println("Screen captured");
                }
            } catch (IOException e) {
                System.out.println(e);
                return;
            }

//            try{
//                ByteArrayOutputStream tmp = compressImage(capture, 1.0f);
//                ImageIO.write(capture, "jpeg", tmp);
//                tmp.close();
//
//                int contentLength = tmp.size();
//                float compress = 64000.0f/contentLength;
//                System.out.println("Compress size "+compress);
//
//                if(compress > 1.0) {
//                    bScrn = tmp;
//                } else {
//                    bScrn = compressImage(capture, compress);
//                }
//
//            }catch(IOException e){
//                System.out.println(e);
//                return;
//            }

            byte[] imgBytes = bScrn.toByteArray();

            try {
                out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);

                if (out != null) {
                    dos.writeInt(imgBytes.length);
                    dos.write(imgBytes, 0, imgBytes.length);
                    dos.flush();
                }
                System.out.println("Sent to client");
//                bScrn.close();

                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Connection closed by peer");
                System.exit(-1);
            } catch (IOException e) {
                System.out.println("Data Length = " + imgBytes.length);
                e.printStackTrace();
            }
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
