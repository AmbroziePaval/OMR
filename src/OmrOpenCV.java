import opencv.MatUtils;
import opencv.StaveElementDetection;
import opencv.StaveImageProcessing;
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

import static utils.OutputPaths.*;

/**
 * Class with the soul purpose of the Computer Vision presentation.
 * <p>
 * Creator: Ambrozie
 * Info: OmrOpenCV.class
 * Date: 12/06/2017 01:13
 */
public class OmrOpenCV {
    private StaveImageProcessing staveImageProcessing;

    private Mat rawInput;
    private Mat verticalObjectsMat;
    private Mat horizontalObjectsMat;
    private Mat refinedVerticalObjectsMat;
    private Mat refinedHorizontalObjectsMat;
    private Scalar colorRed;
    private Scalar colorBlue;

    public OmrOpenCV(String inputImagePath) {
        staveImageProcessing = new StaveImageProcessing();
        processImage(inputImagePath);
        colorRed = new Scalar(0, 0, 255);
        colorBlue = new Scalar(255, 0, 0);
    }

    /**
     * For presentation purposes!
     *
     * @param imagePath input image full path
     */
    public void processImage(String imagePath) {
        System.out.println("Loading image");
        rawInput = Imgcodecs.imread(imagePath);
        Mat staveMat = rawInput.clone();

        // Change input Mat to GRAY if not already
        if (staveMat.channels() == 3) {
            Imgproc.cvtColor(staveMat, staveMat, Imgproc.COLOR_BGR2GRAY);
        }
        staveImageProcessing.saveImage(staveMat, GRAY_IMAGE_PATH.getPath());

        // BITWISE_NOT of the GRAY input image
        Core.bitwise_not(staveMat, staveMat);
        staveImageProcessing.saveImage(staveMat, BITWISE_NOT_IMAGE_PATH.getPath());

        Mat binaryMat = staveImageProcessing.getBinaryMat(staveMat);
        horizontalObjectsMat = staveImageProcessing.getHorizontalObjectsMat(binaryMat);
        verticalObjectsMat = staveImageProcessing.getVerticalObjectsMat(binaryMat);
        refinedVerticalObjectsMat = staveImageProcessing.getRefinedObjectsMat(verticalObjectsMat);
        refinedHorizontalObjectsMat = staveImageProcessing.getRefinedObjectsMat(horizontalObjectsMat);

        staveImageProcessing.saveImage(binaryMat, BINARY_IMAGE_PATH.getPath());
        staveImageProcessing.saveImage(horizontalObjectsMat, HORIZONTAL_OBJ_IMAGE_PATH.getPath());
        staveImageProcessing.saveImage(verticalObjectsMat, VERTICAL_OBJ_IMAGE_PATH.getPath());
        staveImageProcessing.saveImage(refinedVerticalObjectsMat, REFINED_VERTICAL_OBJ_IMAGE_PATH.getPath());
        staveImageProcessing.saveImage(refinedHorizontalObjectsMat, REFINED_HORIZONTAL_OBJ_IMAGE_PATH.getPath());
    }

    public void detectMusicElement() {
        List<Rect> rectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);

        List<Rect> sortedRectangles = StaveElementDetection.sortElementsRectangles(rectangles);
        System.out.println("Music elements rectangles");
        sortedRectangles.forEach(rect -> System.out.println("rectangle " + sortedRectangles.indexOf(rect) + " - x:" + rect.x + " y:" + rect.y));

        Mat elementsWithRectanglesMat = StaveElementDetection.findNotationContours(refinedVerticalObjectsMat, rectangles, colorRed);
        staveImageProcessing.saveImage(elementsWithRectanglesMat, DEFAULT_OUTPUT.getPath());
    }

    public void detectStaveLines() {
        List<Rect> rectangles = StaveElementDetection.getImageElementContourRectangles(refinedHorizontalObjectsMat);
        System.out.println("Stave lines rectangles");
        rectangles.forEach(rect -> System.out.println("rectangle " + rectangles.indexOf(rect) + " - x:" + rect.x + " y:" + rect.y));

        Mat linesWithRectanglesMat = StaveElementDetection.findNotationContours(refinedHorizontalObjectsMat, rectangles, colorRed);
        staveImageProcessing.saveImage(linesWithRectanglesMat, DEFAULT_OUTPUT.getPath());
    }

    public void detectAllElements() {
        List<Rect> elementRectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);
        List<Rect> lineRectangles = StaveElementDetection.getImageElementContourRectangles(refinedHorizontalObjectsMat);

//        Mat elementsWithRectanglesMat = StaveElementDetection.findNotationContours(refinedVerticalObjectsMat, elementRectangles, colorRed);
//        Mat linesWithRectanglesMat = StaveElementDetection.findNotationContours(refinedHorizontalObjectsMat, lineRectangles, colorBlue);


        Core.bitwise_not(verticalObjectsMat, verticalObjectsMat);
        Core.bitwise_not(horizontalObjectsMat, horizontalObjectsMat);
        Mat elementsWithRectanglesMat = StaveElementDetection.findNotationContours(verticalObjectsMat, elementRectangles, colorRed);
        Mat linesWithRectanglesMat = StaveElementDetection.findNotationContours(horizontalObjectsMat, lineRectangles, colorBlue);

        Mat allRectanglesMat = new Mat();
//        Core.add(elementsWithRectanglesMat, linesWithRectanglesMat, allRectanglesMat);
        Core.min(elementsWithRectanglesMat, linesWithRectanglesMat, allRectanglesMat);
        staveImageProcessing.saveImage(allRectanglesMat, ALL_ELEMENTS.getPath());
    }

    public void saveAllElements() {
        List<Rect> elementRectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);
        elementRectangles.forEach(rect -> {
            Mat element = MatUtils.cropRectangleFromMat(refinedVerticalObjectsMat, rect);
            staveImageProcessing.saveImage(element, ELEMENTS_OUTPUT_FOLDER_NAME.getPath() + elementRectangles.indexOf(rect) + ".png");
        });
    }

    public void detectQuarters() {
        Mat source = refinedVerticalObjectsMat.clone();
        Mat quartedTemplate = Imgcodecs.imread("C:\\Users\\Ambrozie\\IdeaProjects\\OpenCVStart\\outputs\\elements\\9.png");
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



        Imgproc.cvtColor(source, source, Imgproc.COLOR_GRAY2BGR);
        for (Point point : quarterPoints) {
            Imgproc.rectangle(source,
                    point,
                    new Point(point.x + quartedTemplate.width() - 1, point.y + quartedTemplate.height() - 1),
                    colorRed, 1, Imgproc.LINE_8, 0);
        }
        staveImageProcessing.saveImage(source, DEFAULT_OUTPUT.getPath());

        int a = 2;
    }
}
