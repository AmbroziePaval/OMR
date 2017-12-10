package opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Template matching algorithms used to identify templates inside a source image Mat.
 * <p>
 * Creator: Ambrozie
 * Info: MatchingTemplate.class
 * Date: 12/11/2017 00:05
 */
public class MatchingTemplate {


    /**
     * Detection algorithm using TEMPLATE MATCHING using:
     * - element Mat as a template
     * - stave elements as a source image
     * <p>
     * Find all elements from the source with >90% match percentage
     *
     * @param source
     */
    public List<Rect> detectQuarters(Mat source) {
        Mat quartedTemplate = Imgcodecs.imread("C:\\Users\\Ambrozie\\IdeaProjects\\OpenCVStart\\outputs\\elements\\2.png");
        Imgproc.cvtColor(quartedTemplate, quartedTemplate, Imgproc.COLOR_BGR2GRAY);
        Mat result = new Mat();

        source.convertTo(source, CvType.CV_32FC1);
        quartedTemplate.convertTo(quartedTemplate, CvType.CV_32FC1);
        int result_cols = source.cols() - quartedTemplate.cols() + 1;
        int result_rows = source.rows() - quartedTemplate.rows() + 1;
        result.create(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(source, quartedTemplate, result, Imgproc.TM_CCOEFF_NORMED);

        double threshold = 0.9;
        Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_TOZERO);

        List<Point> quarterPoints = new ArrayList<>();

        while (true) {

            Core.MinMaxLocResult locResult = Core.minMaxLoc(result);
            if (locResult.maxVal > threshold) {
                quarterPoints.add(locResult.maxLoc);

                int x = (int) locResult.maxLoc.x;
                int y = (int) locResult.maxLoc.y;
                Imgproc.floodFill(result, new Mat(), locResult.maxLoc, new Scalar(0));
                result.put(x, y, 0);
                System.out.println("Found match at: " + x + " " + y);
            } else {
                break;
            }
        }

        List<Rect> allRecognisedRectangles = new ArrayList<>();
        quarterPoints.forEach(point -> allRecognisedRectangles.add(new Rect((int) point.x, (int) point.y, quartedTemplate.width(), quartedTemplate.height())));

        return StaveElementDetection.generifyTheRectangleContours(allRecognisedRectangles);
    }

    /**
     * Detection algorithm using TEMPLATE MATCHING using:
     * - every found element Mat as the template
     * - dataset Mat as a source image
     * <p>
     * Find all elements from the source with >90% match percentage
     */
    public List<Rect> detectAllElementsUsingDataset(String datasetPath, Mat matchingSource, List<Rect> elementRectangles) {
        Mat datasetMat = Imgcodecs.imread(datasetPath);
        if (datasetMat.channels() == 3) {
            Imgproc.cvtColor(datasetMat, datasetMat, Imgproc.COLOR_BGR2GRAY);
        }
        datasetMat.convertTo(datasetMat, CvType.CV_32FC1);
        Mat result = new Mat();
        result.create(datasetMat.width(), datasetMat.height(), CvType.CV_32FC1);
        double threshold = 0.9;

        List<Rect> matchingElements = new ArrayList<>();
        elementRectangles.forEach(elementRect -> {
            Mat elementMat = MatUtils.cropRectangleFromMat(matchingSource, elementRect);
            elementMat.convertTo(elementMat, CvType.CV_32FC1);


            Imgproc.matchTemplate(datasetMat, elementMat, result, Imgproc.TM_CCOEFF_NORMED);
            Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_TOZERO);

            Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
            if (minMaxLocResult.maxVal > threshold) {
                matchingElements.add(elementRect);
            }
        });

        return matchingElements;
    }
}
