package opencv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import utils.DatasetPaths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public Map<Rect, Mat> detectAllElementsUsingDataset(String datasetPath, Mat matchingSource, List<Rect> elementRectangles) {
        Mat datasetMat = Imgcodecs.imread(datasetPath);
        if (datasetMat.channels() == 3) {
            Imgproc.cvtColor(datasetMat, datasetMat, Imgproc.COLOR_BGR2GRAY);
        }
        datasetMat.convertTo(datasetMat, CvType.CV_32FC1);
        Mat result = new Mat();
        result.create(datasetMat.width(), datasetMat.height(), CvType.CV_32FC1);
        double threshold = 0.9;

        Map<Rect, Mat> matchingElements = new HashMap<>();
        elementRectangles.forEach(elementRect -> {
            Mat elementMat = MatUtils.cropRectangleFromMat(matchingSource, elementRect);
            elementMat.convertTo(elementMat, CvType.CV_32FC1);


            Imgproc.matchTemplate(datasetMat, elementMat, result, Imgproc.TM_CCOEFF_NORMED);
            Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_TOZERO);

            Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
            if (minMaxLocResult.maxVal > threshold) {
                matchingElements.put(elementRect, elementMat);
            }
        });

        return matchingElements;
    }

    public Point getAproximateCenterNoteHeadPoint(Mat noteMat) {
        noteMat.convertTo(noteMat, CvType.CV_32FC1);

        Mat fullNoteHeadMat = Imgcodecs.imread(DatasetPaths.FULL_HEAD_TEMPLATE.getPath());
        if (fullNoteHeadMat.channels() == 3) {
            Imgproc.cvtColor(fullNoteHeadMat, fullNoteHeadMat, Imgproc.COLOR_BGR2GRAY);
        }
        fullNoteHeadMat.convertTo(fullNoteHeadMat, CvType.CV_32FC1);

        Mat result = new Mat();
        result.create(noteMat.width(), noteMat.height(), CvType.CV_32FC1);
        double threshold = 0.7;

        Imgproc.matchTemplate(noteMat, fullNoteHeadMat, result, Imgproc.TM_CCOEFF_NORMED);
        Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_TOZERO);

        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
        if (minMaxLocResult.maxVal > threshold) {
            Point maxLoc = minMaxLocResult.maxLoc;
            return new Point(maxLoc.x + fullNoteHeadMat.width() / 2, maxLoc.y + fullNoteHeadMat.height() / 2);
        }
        return null;
    }

    /**
     * Find all center point from all the note heads.
     * Elements location has already been found, so we need to provide where should the recognition find a center if exist (is a note)
     * Only one point inside a rectangle! (here the use of foundCenterFor)
     *
     * @param imageMat          image source with the elements
     * @param elementRectangles detected element rectangles
     * @return the list of note-head center points
     */
    public List<Point> findAllNoteHeadCenters(Mat imageMat, List<Rect> elementRectangles) {
        imageMat.convertTo(imageMat, CvType.CV_32FC1);

        Mat fullNoteHeadMat = Imgcodecs.imread(DatasetPaths.FULL_HEAD_TEMPLATE.getPath());
        if (fullNoteHeadMat.channels() == 3) {
            Imgproc.cvtColor(fullNoteHeadMat, fullNoteHeadMat, Imgproc.COLOR_BGR2GRAY);
        }
        fullNoteHeadMat.convertTo(fullNoteHeadMat, CvType.CV_32FC1);

        Mat result = new Mat();
        result.create(imageMat.width(), imageMat.height(), CvType.CV_32FC1);
        double threshold = 0.75;

        Imgproc.matchTemplate(imageMat, fullNoteHeadMat, result, Imgproc.TM_CCOEFF_NORMED);
        Imgproc.threshold(result, result, threshold, 255, Imgproc.THRESH_TOZERO);

        List<Point> centers = new ArrayList<>();
        Set<Rect> foundCenterFor = new HashSet<>();

        while (true) {
            Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
            if (minMaxLocResult.maxVal > threshold) {
                Point maxLoc = minMaxLocResult.maxLoc;
                Optional<Rect> containingRect = getPointContainingRect(maxLoc, elementRectangles);

                if (containingRect.isPresent() && !foundCenterFor.contains(containingRect.get())) {
                    centers.add(new Point(maxLoc.x + fullNoteHeadMat.width() / 2, maxLoc.y + fullNoteHeadMat.height() / 2));
                    foundCenterFor.add(containingRect.get());
                }
                Imgproc.floodFill(result, new Mat(), minMaxLocResult.maxLoc, new Scalar(0));
            } else {
                break;
            }
        }
        return centers;
    }

    private Optional<Rect> getPointContainingRect(Point point, List<Rect> elementRectangles) {
        for (Rect rect : elementRectangles) {
            if (StaveImageProcessing.pointInsideRect(rect, point)) {
                return Optional.of(rect);
            }
        }
        return Optional.empty();
    }
}
