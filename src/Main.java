import org.opencv.core.Core;
import org.opencv.core.Point;

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
        List<Point> allCenterNotePoints = omrOpenCV.findAllCenterNotePoints();
        omrOpenCV.recongiseElementsWithCenter();
    }
}
