import model.Element;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import utils.OutputPaths;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

/**
 * Main class starter.
 * <p>
 * Creator: Ambrozie
 * Info: Main.class
 * Date: 12/06/2017 22:59
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new Exception("No input image path found!");
        }

        System.out.println("OMR PRESENTATION Paval Ambrozie");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        OmrOpenCV omrOpenCV = new OmrOpenCV(args[0]);
//        omrOpenCV.detectMusicElement();
//        omrOpenCV.detectStaveLines();
//        omrOpenCV.detectAllElements();
//        omrOpenCV.saveAllElements();
//        omrOpenCV.detectQuarters();
//        omrOpenCV.recongniseElementWithDatasets();
//        List<Point> allCenterNotePoints = omrOpenCV.findAllCenterNotePoints();
        List<Element> elements = omrOpenCV.recongiseElementsWithCenter();
        elements.sort(getElementComparator());

        PrintWriter resultFile = new PrintWriter(new FileWriter(OutputPaths.SORTED_RECOGNISED_ELEMENTS.getPath()));
        elements.forEach(element -> resultFile.println(elements.indexOf(element) + "-\t" + element.toString()));
        resultFile.close();
    }

    private static Comparator<Element> getElementComparator() {
        return (element1, element2) -> {
            Rect rectFirst = element1.getRectangle();
            Rect rectSecond = element2.getRectangle();

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
        };
    }
}
