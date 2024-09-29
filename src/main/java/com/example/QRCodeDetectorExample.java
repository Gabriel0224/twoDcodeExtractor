package com.example;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class QRCodeDetectorExample {
    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // Set the path to the QR code image
        String imagePath = "C:/Users/Administrator/Desktop/picture/2.jpg"; // Ensure the path is correct

        try {
            // Read the image
            Mat image = Imgcodecs.imread(imagePath);

            if (image.empty()) {
                System.err.println("Unable to read the image: " + imagePath);
                return;
            } else {
                System.out.println("Image loaded successfully: " + imagePath);
            }

            // Create a QRCodeDetector object
            QRCodeDetector qrDetector = new QRCodeDetector();

            // Variable to hold the decoded text
            String decodedText = "";

            // Variable to hold the points of the QR code corners
            Mat points = new Mat();

            // Detect and decode the QR code
            decodedText = qrDetector.detectAndDecode(image, points);

            if (points.empty()) {
                System.out.println("No QR code detected.");
                return;
            } else {
                // Get the corner points
                Point[] cornerPoints = new Point[4];
                for (int i = 0; i < 4; i++) {
                    double[] data = points.get(0, i);
                    cornerPoints[i] = new Point(data[0], data[1]);
                }

                // Output the corner points
                System.out.println("QR code corner points:");
                for (int i = 0; i < cornerPoints.length; i++) {
                    System.out.printf("Corner %d: (%.2f, %.2f)%n", i + 1, cornerPoints[i].x, cornerPoints[i].y);
                }

                // Draw the corner points on the image
                for (Point point : cornerPoints) {
                    Imgproc.circle(image, point, 10, new Scalar(0, 0, 255), -1);
                }

                // Draw lines connecting the corners
                for (int i = 0; i < cornerPoints.length; i++) {
                    Imgproc.line(image, cornerPoints[i], cornerPoints[(i + 1) % cornerPoints.length], new Scalar(255, 0, 0), 2);
                }

                // Save the result image
                String outputPath = "C:/Users/Administrator/Desktop/picture/output_with_corners.png";
                Imgcodecs.imwrite(outputPath, image);
                System.out.println("Result image saved as " + outputPath);

                // Display the result image
                displayImage(matToBufferedImage(image), "QR Code Detection Result");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Convert Mat to BufferedImage
    public static BufferedImage matToBufferedImage(Mat mat) {
        if (mat.channels() == 1) {
            int width = mat.cols();
            int height = mat.rows();
            byte[] data = new byte[width * height];
            mat.get(0, 0, data);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            image.getRaster().setDataElements(0, 0, width, height, data);
            return image;
        } else if (mat.channels() == 3) {
            int width = mat.cols();
            int height = mat.rows();
            byte[] data = new byte[width * height * (int) mat.elemSize()];
            mat.get(0, 0, data);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            image.getRaster().setDataElements(0, 0, width, height, data);
            return image;
        } else {
            return null;
        }
    }

    // Display image using Swing
    public static void displayImage(BufferedImage img, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(img.getWidth(), img.getHeight());
        JLabel lbl = new JLabel(new ImageIcon(img));
        frame.add(lbl);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
