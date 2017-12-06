import opencv.StaveElementDetection;
import opencv.StaveImageProcessing;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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

    private Mat refinedVerticalObjectsMat;
    private Mat refinedHorizontalObjectsMat;

    public OmrOpenCV(String inputImagePath) {
        staveImageProcessing = new StaveImageProcessing();
        processImage(inputImagePath);
    }

    /**
     * For presentation purposes!
     *
     * @param imagePath input image full path
     */
    public void processImage(String imagePath) {
        System.out.println("Loading image");
        Mat staveMat = Imgcodecs.imread(imagePath);

        // Change input Mat to GRAY if not already
        if (staveMat.channels() == 3) {
            Imgproc.cvtColor(staveMat, staveMat, Imgproc.COLOR_BGR2GRAY);
        }
        staveImageProcessing.saveImage(staveMat, GRAY_IMAGE_PATH.getPath());

        // BITWISE_NOT of the GRAY input image
        Core.bitwise_not(staveMat, staveMat);
        staveImageProcessing.saveImage(staveMat, BITWISE_NOT_IMAGE_PATH.getPath());

        Mat binaryMat = staveImageProcessing.getBinaryMat(staveMat);
        Mat horizontalObjectsMat = staveImageProcessing.getHorizontalObjectsMat(binaryMat);
        Mat verticalObjectsMat = staveImageProcessing.getVerticalObjectsMat(binaryMat);
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
        sortedRectangles.forEach(rect -> System.out.println("rectangle " + sortedRectangles.indexOf(rect) + " - x:" + rect.x + " y:" + rect.y));

        Mat elementsWithRectanglesMat = StaveElementDetection.findNotationContours(refinedVerticalObjectsMat, rectangles);
        staveImageProcessing.saveImage(elementsWithRectanglesMat, DEFAULT_OUTPUT.getPath());
    }
}
