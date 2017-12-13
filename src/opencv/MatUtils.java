package opencv;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * TODO Description!
 * <p>
 * Creator: Ambrozie
 * Info: MatUtils.class
 * Date: 12/09/2017 19:02
 */
public class MatUtils {

    /**
     * Functions that crops the rectangle from the input image and returns the matrix of data corresponding to it
     *
     * @param source    the input image
     * @param rectangle the rectangle
     * @return the Mat corresponding with the rectangle data
     */
    public static Mat cropRectangleFromMat(Mat source, Rect rectangle) {
        Mat ROI = source.submat(rectangle.y, rectangle.y + rectangle.height, rectangle.x, rectangle.x + rectangle.width);
//        Imgproc.resize(ROI, ROI, new Size(30, 80), 0, 0, Imgproc.INTER_LINEAR);
        return ROI;
    }
}
