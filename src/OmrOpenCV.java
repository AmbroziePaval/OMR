import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import model.Element;
import model.ElementType;
import model.NodeDuration;
import model.PlayElement;
import opencv.MatUtils;
import opencv.MatchingTemplate;
import opencv.StaveElementDetection;
import opencv.StaveImageProcessing;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import utils.DatasetPaths;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private MatchingTemplate matchingTemplate;

    private Mat rawInput;
    private Mat verticalObjectsMat;
    private Mat horizontalObjectsMat;
    private Mat refinedVerticalObjectsMat;
    private Mat refinedHorizontalObjectsMat;
    private Scalar colorRed;
    private Scalar colorGreen;
    private Scalar colorBlue;

    public OmrOpenCV(String inputImagePath) {
        staveImageProcessing = new StaveImageProcessing();
        matchingTemplate = new MatchingTemplate();
        processImage(inputImagePath);
        colorRed = new Scalar(0, 0, 255);
        colorGreen = new Scalar(0, 255, 0);
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

    public List<Rect> detectSortedStaveLines() {
        List<Rect> rectangles = StaveElementDetection.getImageElementContourRectangles(refinedHorizontalObjectsMat);
        System.out.println("Stave lines rectangles");
//        rectangles.forEach(rect -> System.out.println("rectangle " + rectangles.indexOf(rect) + " - x:" + rect.x + " y:" + rect.y));
//        List<Rect> sortedRectangles = StaveElementDetection.sortElementsRectangles(rectangles);
        rectangles.sort(Comparator.comparingInt(rect -> rect.y));

        Mat linesWithRectanglesMat = StaveElementDetection.findNotationContours(refinedHorizontalObjectsMat, rectangles, colorRed);
        staveImageProcessing.saveImage(linesWithRectanglesMat, DEFAULT_OUTPUT.getPath());

        return rectangles;
    }

    public void detectAllElements() {
        List<Rect> elementRectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);
        List<Rect> lineRectangles = StaveElementDetection.getImageElementContourRectangles(refinedHorizontalObjectsMat);

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

    public void recongniseElementWithDatasets() {
        List<Rect> rectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);

        Map<Rect, Mat> quartersMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.QUARTERS_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);

        Table<Rect, Mat, Point> centerNoteByRectangeWithImage = HashBasedTable.create();
        quartersMat.forEach((rectangle, noteMat) -> {
            Point centerPoint = matchingTemplate.getAproximateCenterNoteHeadPoint(noteMat);
            if (centerPoint != null) {
                centerNoteByRectangeWithImage.put(rectangle, noteMat, new Point(rectangle.x + centerPoint.x, rectangle.y + centerPoint.y));
            }
        });

        Mat elementsWithQuartersRectangles = StaveElementDetection.findNotationContours(refinedVerticalObjectsMat, new ArrayList<>(quartersMat.keySet()), colorRed);
        centerNoteByRectangeWithImage.values().forEach(
                centerPoint -> Imgproc.circle(elementsWithQuartersRectangles, centerPoint, 1, colorGreen, 1, Imgproc.LINE_8, 0));

        staveImageProcessing.saveImage(elementsWithQuartersRectangles, DEFAULT_OUTPUT.getPath());
    }

    public List<Point> findAllCenterNotePoints() {
        Mat outputMat = refinedVerticalObjectsMat.clone();
        if (outputMat.channels() < 3) {
            Imgproc.cvtColor(outputMat, outputMat, Imgproc.COLOR_GRAY2BGR);
        }

        List<Rect> rectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);

        List<Point> centers = matchingTemplate.findAllNoteHeadCenters(refinedVerticalObjectsMat.clone(), rectangles);
        centers.forEach(centerPoint -> Imgproc.circle(outputMat, centerPoint, 1, colorGreen, 1, Imgproc.LINE_8, 0));
        staveImageProcessing.saveImage(outputMat, DEFAULT_OUTPUT.getPath());

        return centers;
    }


    public List<Element> recongiseElementsWithCenter() {
        List<Rect> rectangles = StaveElementDetection.getImageElementContourRectangles(refinedVerticalObjectsMat);
        List<Point> centers = matchingTemplate.findAllNoteHeadCenters(refinedVerticalObjectsMat, rectangles);
        List<Element> elements = new ArrayList<>();

        // recognise all quarter notes
        Map<Rect, Mat> quartersMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.QUARTERS_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        rectangles.removeAll(quartersMat.keySet());

        quartersMat.forEach((rect, mat) -> {
            Optional<Point> center = getElementCenterPoint(rect, centers);
            center.ifPresent(point -> elements.add(new PlayElement(rect, mat, ElementType.NOTE, NodeDuration.QUARTER, center.get())));
        });

        // recognise all octet notes
        Map<Rect, Mat> eightsMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.OCTETS_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        eightsMat.forEach((rect, mat) -> {
            Optional<Point> center = getElementCenterPoint(rect, centers);
            center.ifPresent(point -> elements.add(new PlayElement(rect, mat, ElementType.NOTE, NodeDuration.EIGHT, center.get())));
        });
        rectangles.removeAll(eightsMat.keySet());


        // recognise all bar line
        Map<Rect, Mat> barsMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.BARS_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        barsMat.forEach(((rect, mat) -> elements.add(new Element(rect, mat, ElementType.BAR))));
        rectangles.removeAll(barsMat.keySet());

        // recognise all dots
        Map<Rect, Mat> dotsMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.DOTS_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        dotsMat.forEach(((rect, mat) -> elements.add(new Element(rect, mat, ElementType.DOT))));
        rectangles.removeAll(dotsMat.keySet());

        // recognise all cleffs
        Map<Rect, Mat> cleffMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.CLEF_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        cleffMat.forEach(((rect, mat) -> elements.add(new Element(rect, mat, ElementType.CLEF))));
        rectangles.removeAll(cleffMat.keySet());

        // recognise all diezes(?)
        Map<Rect, Mat> sharpsMat = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.DIEZ_DATASET.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        sharpsMat.forEach(((rect, mat) -> elements.add(new Element(rect, mat, ElementType.SHARP))));
        rectangles.removeAll(sharpsMat.keySet());

        Map<Rect, Mat> quartersPause = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.QUARTER_PAUSE.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        quartersPause.forEach(((rect, mat) -> elements.add(new PlayElement(rect, mat, ElementType.PAUSE, NodeDuration.QUARTER, null))));
        rectangles.removeAll(quartersPause.keySet());

        Map<Rect, Mat> eightsPause = matchingTemplate.detectAllElementsUsingDataset(
                DatasetPaths.EIGHT_PAUSE.getPath(),
                refinedVerticalObjectsMat,
                rectangles);
        eightsPause.forEach(((rect, mat) -> elements.add(new PlayElement(rect, mat, ElementType.PAUSE, NodeDuration.EIGHT, null))));
        rectangles.removeAll(eightsPause.keySet());

        if (rectangles.size() > 0) {
            System.err.println("There are still elements unrecognised from the input stave!");
        }

        return elements;
    }

    /**
     * Find the note center point
     *
     * @param rectangle input rectangle
     * @param centers   list of note centers
     * @return NULL if not a note
     */
    private Optional<Point> getElementCenterPoint(Rect rectangle, List<Point> centers) {
        for (Point point : centers) {
            if (StaveImageProcessing.pointInsideRect(rectangle, point)) {
                return Optional.of(point);
            }
        }
        return Optional.empty();
    }
}
