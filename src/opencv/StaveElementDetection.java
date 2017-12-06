package opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * tave Utils Class used for detection algorithms on a stave input image.
 * <p>
 * Creator: Ambrozie
 * Info: opencv.StaveElementDetection.class
 * Date: 12/06/2017 22:42
 */
public class StaveElementDetection {
    /**
     * Functions that finds the element contours separated rectangles
     *
     * @param inputImageMat the image Mat with the elements
     * @return a list of Rect objects for each element
     */
    public static List<Rect> getImageElementContourRectangles(Mat inputImageMat) {
        Mat inputMat = inputImageMat.clone();
//        Imgproc.cvtColor(inputImageMat, inputImageMat, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.GaussianBlur(inputImageMat, inputImageMat, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(inputMat, inputMat, 255, 1, 1, 11, 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(inputMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> allContourRectangles = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            allContourRectangles.add(rect);
        }

        return generifyTheRectangleContours(allContourRectangles);
    }

    /**
     * Functions that generifies the contours that intersect to a contour that contains all intersections
     *
     * @param rectangles the list of rectangles
     * @return a new list of rectangles that do not intersect
     */
    private static List<Rect> generifyTheRectangleContours(List<Rect> rectangles) {
        for (int i = 0; i < rectangles.size(); i++) {
            int j = 0;
            while (j < rectangles.size()) {
                Rect rect1 = rectangles.get(i);
                Rect rect2 = rectangles.get(j);
                if (i != j && (rectanglesIntersect(rect1, rect2) || rectanglesIntersect(rect2, rect1))) {
                    rect1.width = Math.max(rect1.x + rect1.width, rect2.x + rect2.width) - Math.min(rect1.x, rect2.x);
                    rect1.x = Math.min(rect1.x, rect2.x);
                    rect1.height = Math.max(rect1.y + rect1.height, rect2.y + rect2.height) - Math.min(rect1.y, rect2.y);
                    rect1.y = Math.min(rect1.y, rect2.y);
                    rectangles.remove(j);
                    i = j = 0;
                } else {
                    j++;
                }
            }
        }
        return rectangles;
    }

    /**
     * Function that sorts the list of given rectangles from the top left to the right downwards
     *
     * @param rects the given rectangles
     * @return the sorted list of Rect objects
     */
    public static List<Rect> sortElementsRectangles(List<Rect> rects) {
        rects.sort((rectFirst, rectSecond) -> {
            Integer first, second;
            if (Math.abs(rectFirst.y - rectSecond.y) < 50) {
                first = rectFirst.x;
                second = rectSecond.x;
                return first.compareTo(second);
            } else {
                first = rectFirst.y;
                second = rectSecond.y;
            }
            return first.compareTo(second);
        });
        return rects;
    }

    /**
     * Function that gets the input image and contours every separate element from it
     *
     * @param inputImage the image bitmap
     * @param rectangles the list of needed rectangles
     * @return an image bitmap with all the elements contoured in a red rectangle
     */
    public static Mat findNotationContours(Mat inputImage, List<Rect> rectangles) {
        Mat inputMat = inputImage.clone();
        Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_GRAY2BGR);
        Scalar contourScalar = new Scalar(0, 0, 255);

        for (Rect rect : rectangles) {
            Imgproc.rectangle(inputMat,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    contourScalar, 1, Imgproc.LINE_8, 0);
//            System.out.println("contour" + " x:" + rect.x + " y:" + rect.y);
        }

        return inputMat;
    }

    /**
     * Function that checks if 2 rectangles intersect
     *
     * @param rect1 rectangle1
     * @param rect2 rectangle2
     * @return boolean that represents if the 2 rectangles intersect
     */
    private static boolean rectanglesIntersect(Rect rect1, Rect rect2) {
        if (rect1.contains(new Point(rect2.x, rect2.y)))
            return true;
        else if (rect1.contains(new Point(rect2.x, rect2.y + rect2.height)))
            return true;
        else if (rect1.contains(new Point(rect2.x + rect2.width, rect2.y)))
            return true;
        else return rect1.contains(new Point(rect2.x + rect2.width, rect2.y + rect2.height));
    }
}
