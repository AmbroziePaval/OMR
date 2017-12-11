package opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static utils.OutputPaths.DIL_EDGES_VERTICAL_OBJ_IMAGE_PATH;
import static utils.OutputPaths.EDGES_VERTICAL_OBJ_IMAGE_PATH;
import static utils.OutputPaths.SMOOTH_VERTICAL_OBJ_IMAGE_PATH;

/**
 * Stave Utils Class used for OpenCV modification and algorithms application on a stave input image.
 * <p>
 * Creator: Ambrozie
 * Info: opencv.StaveImageProcessing.class
 * Date: 12/06/2017 01:12
 */
public class StaveImageProcessing {
    /**
     * Get BINARY Mat from an input GRAY Mat
     *
     * @param staveMat input GRAY Mat
     * @return binary MAT
     */
    public Mat getBinaryMat(Mat staveMat) {
        Mat binaryStaveMat = new Mat();
        int blockSize = 15;
        int constant = -2;
        Imgproc.adaptiveThreshold(staveMat, binaryStaveMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, blockSize, constant);

        return binaryStaveMat;
    }

    /**
     * Get Mat that contains the horizontal objects withing a binary Mat
     *
     * @param staveMat the binary input Mat
     * @return Mat containing only the horizontal objects
     */
    public Mat getHorizontalObjectsMat(Mat staveMat) {
        Mat horizontalObjectsMat = staveMat.clone();
        int horizontalSize = horizontalObjectsMat.cols() / 30;
        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalSize, 1));

        // defaults: Point(-1,-1), iterations = 1
        Imgproc.erode(horizontalObjectsMat, horizontalObjectsMat, horizontalStructure);
        Imgproc.dilate(horizontalObjectsMat, horizontalObjectsMat, horizontalStructure);

        // change to the original bitwise
//        Core.bitwise_not(horizontalObjectsMat, horizontalObjectsMat);
        return horizontalObjectsMat;
    }

    /**
     * Get Mat that contains the vertical objects withing a binary Mat
     *
     * @param staveMat the binary input Mat
     * @return Mat containing only the vertical objects
     */
    public Mat getVerticalObjectsMat(Mat staveMat) {
        Mat verticalObjectsMat = staveMat.clone();
        int verticalSize = verticalObjectsMat.rows() / 30;
        Mat vertialStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, verticalSize));

        // defaults: Point(-1,-1), iterations = 1
        Imgproc.erode(verticalObjectsMat, verticalObjectsMat, vertialStructure);
        Imgproc.dilate(verticalObjectsMat, verticalObjectsMat, vertialStructure);

        // change to the original bitwise
//        Core.bitwise_not(verticalObjectsMat, verticalObjectsMat);
        return verticalObjectsMat;
    }

    /**
     * Method that refines the edges of the vertical objects.
     * Steps:
     * 1. extract edges
     * 2. dilate edges
     * 3.
     *
     * @param verticalObjectsMat the Mat with
     * @return the refined inverse image of the vertical Objects Mat
     */
    public Mat getRefinedObjectsMat(Mat verticalObjectsMat) {
        Mat verticalMat = verticalObjectsMat.clone();

        // 0. change back to the original bitwise values
        Core.bitwise_not(verticalMat, verticalMat);

        // 1. extract edges
        Mat edgesMat = new Mat();
        Imgproc.adaptiveThreshold(verticalMat, edgesMat, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, -2);
        saveImage(edgesMat, EDGES_VERTICAL_OBJ_IMAGE_PATH.getPath());

        // 2. dilate edges
        Mat kernel = Mat.ones(2, 2, CvType.CV_8UC1);
        Imgproc.dilate(edgesMat, edgesMat, kernel);
        saveImage(edgesMat, DIL_EDGES_VERTICAL_OBJ_IMAGE_PATH.getPath());

        Mat smoothEdgesMat = new Mat();
        verticalMat.copyTo(smoothEdgesMat);
        Imgproc.blur(smoothEdgesMat, smoothEdgesMat, new Size(2, 2));
        saveImage(smoothEdgesMat, SMOOTH_VERTICAL_OBJ_IMAGE_PATH.getPath());

        smoothEdgesMat.copyTo(verticalMat, edgesMat);
        return verticalMat;
    }

    public static boolean pointInsideRect(Rect rect, Point point) {
        return point.x >= rect.x && point.y >= rect.y && point.x <= (rect.x + rect.width) && point.y <= (rect.y + rect.height);
    }

    /**
     * Save the Mat object as a .png
     *
     * @param mat  Mat to save
     * @param file full file path
     */
    public void saveImage(Mat mat, String file) {
        System.out.println(String.format("Saving Mat to png: %s", file));
        Imgcodecs.imwrite(file, mat);
    }
}